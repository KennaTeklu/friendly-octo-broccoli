package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog

/**
 * ProfileState — per-profile state lives in per-profile SharedPreferences
 * `p4_ls_<profileId>` (Batch 5A rewrite).
 *
 * Previously (Batch 4B–4E), per-profile state was stored in the active profile's
 * Room `meta` table. That worked but blocked boot reads on the DAO and required
 * the room handle to be open at switch time. Batch 5A moves per-profile state
 * to a dedicated SharedPreferences file per profile — `p4_ls_<profileId>` —
 * which is read/write-safe off the main thread without holding the DB handle.
 *
 * Storage boundary (Batch 5A):
 *   PER-PROFILE  (this file, `p4_ls_<id>`): onboarding flag, onboarding draft,
 *                 onboarding finished_at, first-launch timestamp, health-screen
 *                 state (screened_at / tier / general / followup / joints /
 *                 unlocked_at / declared), health-intro-seen, emergency draft
 *                 backup, theme mode+accent, Material You flag, gym mode, language,
 *                 vocab level, power settings, secure mode, library prefs,
 *                 notification prefs, competition, studio export, last backup,
 *                 streak recover, workoutData mirror, coaching tuning,
 *                 self-assessment records.
 *   GLOBAL       (ProPrefs `p4_ls`, not here):   active profile id, default
 *                 profile id, device id, consent log, v18_migrated / v22_migrated
 *                 one-shot flags.
 *
 * New-user detection (Batch 4B item 6) — a profile is "new" if NONE of:
 *   p4_onboarded, p4_health_screened_at, workout_count > 0,
 *   exercise_count > 0, p4_first_launch are present.
 *
 * Migration (one-shot, idempotent): on first v18 cold start, every profile in
 * the registry inherits the legacy global p4_onboarded / p4_health_* values
 * from ProPrefs so existing users keep their "already onboarded" state and
 * never see the wizard fire again. The migration is gated on
 * `p4_v18_migrated` (global ProPrefs).
 */
object ProfileState {
    // Per-profile keys (SharedPreferences `p4_ls_<id>`, active profile namespace)
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

    /** Filename prefix — `p4_ls_<profileId>` (e.g. `p4_ls_default`, `p4_ls_p_abc123`). */
    const val PREF_PREFIX = "p4_ls_"

    /** SharedPreferences for a specific profile (by id). Always returns a real handle. */
    fun prefsFor(ctx: Context, profileId: String): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(PREF_PREFIX + profileId, Context.MODE_PRIVATE)

    /** SharedPreferences for the ACTIVE profile. Looks up the active id every call
     *  so it always reflects the latest switch (no stale cache in our code). */
    fun prefs(ctx: Context): SharedPreferences = prefsFor(ctx, Profiles.activeId(ctx))

    fun get(ctx: Context, key: String, default: String? = null): String? =
        try { prefs(ctx).getString(key, default) } catch (_: Exception) { default }

    fun put(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).apply()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "put $key failed: ${e.message}") }

    /** Synchronous put — used where the caller needs the write visible before returning
     *  (e.g. migration, restart-sensitive flags). */
    fun putSync(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).commit()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "putSync $key failed: ${e.message}") }

    fun remove(ctx: Context, key: String) = try {
        prefs(ctx).edit().remove(key).apply()
    } catch (_: Exception) {}

    fun has(ctx: Context, key: String): Boolean = try { prefs(ctx).contains(key) } catch (_: Exception) { false }

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
     *
     * Combination rule: if ANY signal is set, treat the profile as existing.
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
     * per-profile SharedPreferences (`p4_ls_<id>`), so existing users keep
     * their onboarded / screened state and never see the wizard fire again
     * after upgrade.
     *
     * Idempotent — gated on the global `p4_v18_migrated` flag. New profiles
     * created after v18 do NOT inherit the legacy flag (they're genuinely new),
     * because the migration only touches profiles that exist in the registry
     * at the moment it runs.
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
