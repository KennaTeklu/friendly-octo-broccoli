package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog

/**
 * ProfileState — per-profile state lives in the active profile's SharedPreferences
 * file `p4_ls_<profileId>` (the default profile uses `p4_ls_default`).
 *
 * BATCH-5A: previously, per-profile state was stored in the Room `meta` table of
 * the active profile. That required a DB hit on every read and a writable
 * DB handle on every write — slow, and a footgun when a profile switch closed
 * the DB mid-write. SharedPreferences are lighter, never block on the DB lock,
 * and give us atomic multi-key writes via a single edit().commit().
 *
 * Storage boundary:
 *   PER-PROFILE (this file, SharedPreferences `p4_ls_<profileId>`): onboarding
 *                 flag, onboarding draft, onboarding finished_at, first-launch
 *                 timestamp, health-screen state, emergency draft backup,
 *                 training preferences, theme mode+accent, Material You flag,
 *                 gym mode, language, vocab level, library collapsed groups,
 *                 notification flags, last-backup timestamp, last-streak-recover,
 *                 competition date, studio export, workout data mirror.
 *   GLOBAL    (ProPrefs, file `p4_ls`): active profile id, default profile id,
 *                 device id, consent log (legal record), p4_v18_migrated flag,
 *                 p4_v22_migrated flag.
 *
 * New-user detection — a profile is "new" if NONE of:
 *   p4_onboarded, p4_health_screened_at, workout_count > 0,
 *   exercise_count > 0, p4_first_launch are present.
 *
 * Migration (one-shot v18): on first v18 cold start, every profile in the
 * registry inherits the legacy global p4_onboarded / p4_health_* values from
 * ProPrefs so existing users keep their "already onboarded" state and never
 * see the wizard fire again. Idempotent and gated on `p4_v18_migrated`.
 */
object ProfileState {
    // Per-profile keys (SharedPreferences `p4_ls_<profileId>` namespace)
    const val K_ONBOARDED = "p4_onboarded"
    const val K_OB_DRAFT = "p4_ob_draft"
    const val K_OB_FINISHED_AT = "p4_ob_finished_at"
    const val K_FIRST_LAUNCH = "p4_first_launch"
    const val K_HEALTH_SCREENED = "p4_health_screened_at"
    const val K_HEALTH_TIER = "p4_health_tier"
    const val K_HEALTH_GENERAL = "p4_health_general"
    const val K_HEALTH_FOLLOWUP = "p4_health_followup"
    const val K_HEALTH_JOINTS = "p4_health_joints"
    const val K_HEALTH_UNLOCKED = "p4_health_unlocked_at"
    const val K_HEALTH_DECLARED = "p4_health_declared"
    const val K_HEALTH_INTRO_SEEN = "p4_health_intro_seen"
    const val K_EMERGENCY_BACKUP = "workoutEmergencyBackup"
    const val K_V18_MIGRATED = "p4_v18_migrated"
    // BATCH-5A: legacy meta keys kept on the global ProPrefs file
    const val K_WORKOUT_DATA = "workoutData"

    /** SharedPreferences file name for a profile id. The default profile uses
     *  `p4_ls_default` so it never collides with the global `p4_ls` file
     *  (which holds profile-registry, device-id and consent records). */
    fun prefsNameFor(profileId: String): String = "p4_ls_${profileId}"

    /** SharedPreferences for a specific profile id. */
    fun prefsFor(ctx: Context, profileId: String): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(prefsNameFor(profileId), Context.MODE_PRIVATE)

    /** SharedPreferences for the active profile. */
    fun prefs(ctx: Context): SharedPreferences = prefsFor(ctx, Profiles.activeId(ctx))

    fun get(ctx: Context, key: String, default: String? = null): String? = try {
        prefs(ctx).getString(key, default)
    } catch (_: Exception) { default }

    fun put(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).apply()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "put $key failed: ${e.message}") }

    /** Synchronous write — used by performSave() / emergency backup so the
     *  value lands on disk before the next read. */
    fun putSync(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).commit()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "putSync $key failed: ${e.message}") }

    fun remove(ctx: Context, key: String) = try {
        prefs(ctx).edit().remove(key).apply()
    } catch (_: Exception) {}

    fun has(ctx: Context, key: String): Boolean = try {
        prefs(ctx).contains(key)
    } catch (_: Exception) { false }

    fun keys(ctx: Context): List<String> = try {
        prefs(ctx).all.keys.toList()
    } catch (_: Exception) { emptyList() }

    /**
     * New-user detection — a profile is "new" only if NONE of the strong
     * signals are present:
     *   - p4_onboarded (per-profile)
     *   - p4_health_screened_at (per-profile)
     *   - workout count > 0 (active profile Room workouts table)
     *   - exercise record count > 0 (active profile Room exercises table)
     *   - p4_first_launch (per-profile, set on first cold-start of this profile)
     */
    fun isNewUser(ctx: Context): Boolean {
        if (get(ctx, K_ONBOARDED) == "true") return false
        if (get(ctx, K_HEALTH_SCREENED) != null) return false
        if (ProState.data.workouts.isNotEmpty()) return false
        if (ProState.data.exercises.isNotEmpty()) return false
        if (get(ctx, K_FIRST_LAUNCH) != null) return false
        return true
    }

    /** Mark the first-launch timestamp for the active profile (idempotent —
     *  only writes if the key is missing). */
    fun stampFirstLaunch(ctx: Context) {
        if (get(ctx, K_FIRST_LAUNCH) == null) {
            put(ctx, K_FIRST_LAUNCH, ProState.nowIso())
        }
    }

    /**
     * One-shot v18 migration: copy legacy global p4_onboarded / p4_health_* /
     * workoutEmergencyBackup values from ProPrefs into EVERY existing profile's
     * SharedPreferences file (`p4_ls_<id>`), so existing users keep their
     * onboarded / screened state and never see the wizard fire again after
     * upgrade.
     *
     * Idempotent — gated on the global `p4_v18_migrated` flag. New profiles
     * created after v18 do NOT inherit the legacy flag (they're genuinely new).
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, K_V18_MIGRATED) == "true") return
        val profiles = Profiles.list(ctx)
        val legacyOnboarded = ProPrefs.get(ctx, K_ONBOARDED)
        val legacyObDraft = ProPrefs.get(ctx, K_OB_DRAFT)
        val legacyObFinishedAt = ProPrefs.get(ctx, K_OB_FINISHED_AT)
        val legacyHealth = mapOf(
            K_HEALTH_SCREENED to ProPrefs.get(ctx, K_HEALTH_SCREENED),
            K_HEALTH_TIER to ProPrefs.get(ctx, K_HEALTH_TIER),
            K_HEALTH_GENERAL to ProPrefs.get(ctx, K_HEALTH_GENERAL),
            K_HEALTH_FOLLOWUP to ProPrefs.get(ctx, K_HEALTH_FOLLOWUP),
            K_HEALTH_JOINTS to ProPrefs.get(ctx, K_HEALTH_JOINTS),
            K_HEALTH_UNLOCKED to ProPrefs.get(ctx, K_HEALTH_UNLOCKED),
            K_HEALTH_DECLARED to ProPrefs.get(ctx, K_HEALTH_DECLARED),
            K_EMERGENCY_BACKUP to ProPrefs.get(ctx, K_EMERGENCY_BACKUP),
        )
        for (p in profiles) {
            val sp = prefsFor(ctx, p.id)
            val ed = sp.edit()
            if (legacyOnboarded != null && !sp.contains(K_ONBOARDED)) ed.putString(K_ONBOARDED, legacyOnboarded)
            if (legacyObDraft != null && !sp.contains(K_OB_DRAFT)) ed.putString(K_OB_DRAFT, legacyObDraft)
            if (legacyObFinishedAt != null && !sp.contains(K_OB_FINISHED_AT)) ed.putString(K_OB_FINISHED_AT, legacyObFinishedAt)
            legacyHealth.forEach { (k, v) ->
                if (v != null && !sp.contains(k)) ed.putString(k, v)
            }
            ed.commit()
        }
        ProPrefs.put(ctx, K_V18_MIGRATED, "true")
        ProLog.i("MIGRATE", "v18 prefs migration complete for ${profiles.size} profiles")
    }
}
