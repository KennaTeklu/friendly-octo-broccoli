package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog
import kotlinx.coroutines.runBlocking

/**
 * ProfileState — per-profile state lives in SharedPreferences `p4_ls_<profileId>`.
 *
 * BATCH-5A: previously per-profile state was stored in the active profile's Room
 * `meta` table. That worked but the meta table shared the lifecycle of the
 * profile DB itself, was invisible to other observers, and could not be read
 * without going through Room. We now back the same keys with per-profile
 * SharedPreferences files (one file per profile) so:
 *
 *   - the keys are still per-profile-isolated,
 *   - the global ProPrefs (`p4_ls`) is reserved for genuinely global state,
 *   - the legacy v18 migration copy from `p4_ls` → profile file is one-shot and
 *     gated on `p4_v22_migrated` (ProfileSettings.migrateFromPrefs),
 *   - the legacy `workoutEmergencyBackup` continues to be per-profile.
 *
 * Storage boundary (BATCH-5A):
 *   PER-PROFILE  (this file, p4_ls_<profileId>): onboarding flag, onboarding draft,
 *                 onboarding finished_at, first-launch timestamp, health-screen
 *                 state (screened_at / tier / general / followup / joints /
 *                 unlocked_at / declared), health-intro-seen, emergency draft
 *                 backup, training preferences, theme, gym mode, language, vocab,
 *                 notifications, power settings, library prefs.
 *   GLOBAL       (ProPrefs, "p4_ls"):          active profile id, default profile
 *                 id, device id, consent log (legal record), registry mirror.
 *
 * New-user detection (BATCH-4B item 6) — a profile is "new" if NONE of:
 *   p4_onboarded, p4_health_screened_at, workout_count > 0,
 *   exercise_count > 0, p4_first_launch are present.
 */
object ProfileState {
    // Per-profile keys (SharedPreferences p4_ls_<profileId>, active profile namespace)
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
    /** BATCH-5A: per-profile v22 migration flag. */
    const val K_V22_MIGRATED = "p4_v22_migrated"

    /** The SharedPreferences file name for the given profile id. */
    fun prefsName(profileId: String): String = "p4_ls_$profileId"

    /** SharedPreferences for the given profile id (creates the file on first access). */
    fun prefsFor(ctx: Context, profileId: String): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(prefsName(profileId), Context.MODE_PRIVATE)

    /** SharedPreferences for the ACTIVE profile (resolved via Profiles.activeId). */
    fun prefs(ctx: Context): SharedPreferences = prefsFor(ctx, Profiles.activeId(ctx))

    /** Read a per-profile key from the active profile's SharedPreferences. */
    fun get(ctx: Context, key: String, default: String? = null): String? = try {
        prefs(ctx).getString(key, default)
    } catch (_: Exception) { default }

    /** Write a per-profile key to the active profile's SharedPreferences (async apply). */
    fun put(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).apply()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "put $key failed: ${e.message}") }

    /** Write a per-profile key SYNCHRONOUSLY (commit). Use sparingly — only for
     *  boot-time reads where the next line must observe the value. */
    fun putSync(ctx: Context, key: String, value: String): Boolean = try {
        prefs(ctx).edit().putString(key, value).commit()
    } catch (e: Exception) {
        ProLog.w("PROFILESTATE", "putSync $key failed: ${e.message}")
        false
    }

    /** Remove a per-profile key from the active profile's SharedPreferences. */
    fun remove(ctx: Context, key: String) = try {
        prefs(ctx).edit().remove(key).apply()
    } catch (_: Exception) {}

    /** True if the active profile's SharedPreferences has the given key. */
    fun has(ctx: Context, key: String): Boolean = get(ctx, key) != null

    /** All keys in the active profile's SharedPreferences. */
    fun keys(ctx: Context): List<String> = try {
        prefs(ctx).all.keys.toList()
    } catch (_: Exception) { emptyList() }

    /**
     * New-user detection (BATCH-4B item 6) — a profile is "new" only if NONE of
     * the strong signals are present:
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

    /** Mark the first-launch timestamp for the active profile (idempotent). */
    fun stampFirstLaunch(ctx: Context) {
        if (get(ctx, K_FIRST_LAUNCH) == null) {
            put(ctx, K_FIRST_LAUNCH, ProState.nowIso())
        }
    }

    /**
     * One-shot v18 migration: copy legacy global p4_onboarded / p4_health_* /
     * workoutEmergencyBackup values from ProPrefs into EVERY existing profile's
     * SharedPreferences file, so existing users keep their onboarded / screened
     * state and never see the wizard fire again after upgrade.
     *
     * Idempotent — gated on the global `p4_v18_migrated` flag.
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
            try {
                val sp = prefsFor(ctx, p.id)
                val ed = sp.edit()
                if (legacyOnboarded != null && !sp.contains(K_ONBOARDED)) ed.putString(K_ONBOARDED, legacyOnboarded)
                if (legacyObDraft != null && !sp.contains(K_OB_DRAFT)) ed.putString(K_OB_DRAFT, legacyObDraft)
                if (legacyObFinishedAt != null && !sp.contains(K_OB_FINISHED_AT)) ed.putString(K_OB_FINISHED_AT, legacyObFinishedAt)
                legacyHealth.forEach { (k, v) ->
                    if (v != null && !sp.contains(k)) ed.putString(k, v)
                }
                ed.commit()
            } catch (e: Exception) {
                ProLog.w("MIGRATE", "v18 prefs migration failed for ${p.id}: ${e.message}")
            }
        }
        ProPrefs.put(ctx, K_V18_MIGRATED, "true")
        ProLog.i("MIGRATE", "v18 prefs migration complete for ${profiles.size} profiles")
    }
}
