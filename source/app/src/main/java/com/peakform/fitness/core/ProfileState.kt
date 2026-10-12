package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog

/**
 * ProfileState — per-profile state lives in a per-profile SharedPreferences file
 * `p4_ls_$profileId` (BATCH-5A).
 *
 * Previously (BATCH-4B), per-profile state was stored in the active profile's Room
 * `meta` table. BATCH-5A moves the storage layer to SharedPreferences so reads
 * never block on the Room DAO — main-thread access is still discouraged but the
 * catastrophic "DB not open" window is gone because SharedPreferences handles
 * concurrent reads/writes natively.
 *
 * Storage boundary (BATCH-5A):
 *   PER-PROFILE  (this file, `p4_ls_$profileId` SharedPreferences): onboarding flag,
 *                 onboarding draft, onboarding finished_at, first-launch timestamp,
 *                 health-screen state, health-intro-seen, emergency draft backup,
 *                 training preferences, theme + Material You flag, gym mode, language,
 *                 vocab level, library prefs, notification prefs, etc. (See
 *                 ProfileSettings for the full key list.)
 *   GLOBAL       (ProPrefs, "p4_ls" SharedPreferences): device id, consent log
 *                 (legal record), pending section, share-text, v18/v22 migration
 *                 gates, profile registry pointers.
 *
 * New-user detection (carried over from BATCH-4B item 6) — a profile is "new" if
 * NONE of: p4_onboarded, p4_health_screened_at, workout_count > 0,
 * exercise_count > 0, p4_first_launch are present.
 *
 * Migration (one-shot v18): on first v18 cold start, every profile in the registry
 * inherits the legacy global p4_onboarded / p4_health_* values from ProPrefs so
 * existing users keep their "already onboarded" state and never see the wizard
 * fire again. The migration writes to per-profile SharedPreferences. Idempotent,
 * gated on `p4_v18_migrated`.
 */
object ProfileState {
    // Per-profile keys (stored in `p4_ls_$profileId` SharedPreferences)
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
    /** v22 migration gate — see ProfileSettings.migrateFromPrefs(). Global (ProPrefs). */
    const val K_V22_MIGRATED = "p4_v22_migrated"

    /**
     * The per-profile SharedPreferences file. Naming convention `p4_ls_$profileId`
     * matches the legacy "p4_ls" global store — the default profile uses
     * `p4_ls_default`, second profile `p4_ls_p_abc123`, etc.
     */
    fun prefsFor(ctx: Context, profileId: String): SharedPreferences =
        ctx.applicationContext.getSharedPreferences("p4_ls_$profileId", Context.MODE_PRIVATE)

    /** Active profile's SharedPreferences (resolves via Profiles.activeId). */
    fun prefs(ctx: Context): SharedPreferences = prefsFor(ctx, Profiles.activeId(ctx))

    fun get(ctx: Context, key: String, default: String? = null): String? =
        try { prefs(ctx).getString(key, default) } catch (_: Exception) { default }

    fun put(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).apply()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "put $key failed: ${e.message}") }

    /** Atomic commit — used by hot paths where the caller cannot tolerate an
     *  apply() window (e.g. theme writes that must be visible to the next read
     *  on the same thread). */
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
     * New-user detection (BATCH-4B item 6) — a profile is "new" only if NONE of
     * the strong signals are present:
     *   - p4_onboarded (per-profile)
     *   - p4_health_screened_at (per-profile)
     *   - workout count > 0 (active profile Room workouts table)
     *   - exercise record count > 0 (active profile Room exercises table)
     *   - p4_first_launch (per-profile, set on first cold-start of this profile)
     *
     * Combination rule: if ANY signal is set, treat the profile as existing.
     * A user who wiped their workouts but kept their onboarding flag is still
     * existing. A user with only workouts but no flag (rare migration case) is
     * also existing — never re-onboard someone with workout history.
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
     * per-profile SharedPreferences (`p4_ls_$profileId`), so existing users keep
     * their onboarded / screened state and never see the wizard fire again after
     * upgrade.
     *
     * Idempotent — gated on the global `p4_v18_migrated` flag. New profiles
     * created after v18 do NOT inherit the legacy flag (they're genuinely new),
     * because the migration only touches profiles that exist in the registry
     * at the moment it runs.
     *
     * BATCH-5A: storage moved from Room `meta` table to SharedPreferences. The
     * migration logic is unchanged — only the sink changed.
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
