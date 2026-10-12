package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog

/**
 * ProfileState — per-profile state lives in per-profile SharedPreferences
 * `p4_ls_<profileId>` (BATCH-5A).
 *
 * Storage boundary:
 *   PER-PROFILE (this file, `p4_ls_<profileId>`): onboarding flag, onboarding
 *                 draft, onboarding finished_at, first-launch timestamp,
 *                 health-screen state, health-intro-seen, emergency backup,
 *                 training preferences, theme, dynamic flag, gym mode, language,
 *                 vocab level, library prefs, notification toggles, etc.
 *   GLOBAL      (ProPrefs, `p4_ls`):              active profile id, default
 *                 profile id, device id, consent log, license.
 *
 * The previous BATCH-4B implementation wrote these keys into Room `meta` table;
 * BATCH-5A moves them to SharedPreferences for synchronous, main-thread-safe
 * access (no DAO, no IO dispatcher hop) and correct per-profile isolation.
 *
 * Migration:
 *   - v18: copy legacy global `p4_ls` onboarded / health / draft values into
 *     every existing profile's `p4_ls_<id>` (one-shot, gated on `p4_v18_migrated`).
 *   - v22: see ProfileSettings.migrateFromPrefs — copies the per-profile-
 *     eligible keys (theme, gym, lang, vocab, …) from global `p4_ls` into every
 *     profile's `p4_ls_<id>`.
 *
 * New-user detection (BATCH-4B item 6) — a profile is "new" if NONE of:
 *   p4_onboarded, p4_health_screened_at, workout_count > 0, exercise_count > 0,
 *   p4_first_launch are present.
 */
object ProfileState {
    // Per-profile keys (`p4_ls_<profileId>` SharedPreferences file)
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

    /** File name format for per-profile SharedPreferences. */
    fun prefsName(profileId: String): String = "p4_ls_$profileId"

    /** Returns the SharedPreferences for the given profile (`p4_ls_<profileId>`). */
    fun prefsFor(ctx: Context, profileId: String): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(prefsName(profileId), Context.MODE_PRIVATE)

    /** Returns the SharedPreferences for the active profile. */
    fun prefs(ctx: Context): SharedPreferences = prefsFor(ctx, Profiles.activeId(ctx))

    fun get(ctx: Context, key: String, default: String? = null): String? = try {
        prefs(ctx).getString(key, default)
    } catch (_: Exception) { default }

    fun put(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).apply()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "put $key failed: ${e.message}") }

    /** Synchronous write — uses commit() so the value is on disk before the
     *  call returns. Use for emergency backup writes and migration steps. */
    fun putSync(ctx: Context, key: String, value: String): Boolean = try {
        prefs(ctx).edit().putString(key, value).commit()
    } catch (e: Exception) {
        ProLog.w("PROFILESTATE", "putSync $key failed: ${e.message}")
        false
    }

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

    /** Mark the first-launch timestamp for the active profile (idempotent —
     *  only writes if the key is missing). */
    fun stampFirstLaunch(ctx: Context) {
        if (get(ctx, K_FIRST_LAUNCH) == null) {
            put(ctx, K_FIRST_LAUNCH, ProState.nowIso())
        }
    }

    /**
     * One-shot v18 migration: copy legacy global p4_onboarded / p4_health_* /
     * workoutEmergencyBackup values from the global ProPrefs file into EVERY
     * existing profile's `p4_ls_<id>` SharedPreferences, so existing users
     * keep their onboarded / screened state and never see the wizard fire
     * again after upgrade.
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
        ProLog.i("MIGRATE", "v18 meta migration complete for ${profiles.size} profiles")
    }
}
