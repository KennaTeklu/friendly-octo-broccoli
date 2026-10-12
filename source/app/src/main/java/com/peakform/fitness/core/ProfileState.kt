package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog

/**
 * ProfileState — per-profile SharedPreferences ("p4_ls_<profileId>").
 *
 * BATCH-5A structural change: previously per-profile state lived in the active
 * profile's Room `meta` table. The meta-table approach required a DB open +
 * a synchronized DAO call for every boot-time read, which blocked the main
 * thread on cold start. SharedPreferences gives us O(1) reads with no DB
 * round-trip and is the same persistent KV store pattern used by the legacy
 * HTML app's per-profile localStorage shim.
 *
 * Storage boundary (BATCH-5A):
 *   PER-PROFILE  (this file, "p4_ls_<profileId>" SharedPrefs): onboarding flag,
 *                 onboarding draft, onboarding finished_at, first-launch
 *                 timestamp, health-screen state (screened_at / tier /
 *                 general / followup / joints / unlocked_at / declared),
 *                 health-intro-seen, emergency backup, training preferences,
 *                 notifications, vocabulary level, language, library prefs,
 *                 streak recovery, competition date.
 *   GLOBAL       ("p4_ls" SharedPrefs, ProPrefs): theme mode + accent,
 *                 Material You flag, active profile id, default profile id,
 *                 device id, consent log (legal record), app id.
 *
 * Key naming: all per-profile keys keep the legacy `p4_*` / `workoutEmergencyBackup`
 * / `lastStreakRecoverAt` names so Complete-Backup-v1 bundles round-trip and
 * downgrading does not lose data.
 *
 * New-user detection (BATCH-4B item 6, retained): a profile is "new" only if
 * NONE of: p4_onboarded, p4_health_screened_at, workout_count > 0,
 * exercise_count > 0, p4_first_launch are present.
 *
 * Migration (one-shot, BATCH-5A): on first v22 cold start, every profile in
 * the registry inherits the legacy global p4_onboarded / p4_health_* /
 * workoutEmergencyBackup values from ProPrefs so existing users keep their
 * "already onboarded" state. Idempotent — gated on `p4_v22_migrated` (global).
 */
object ProfileState {
    // ---- per-profile key constants (SharedPrefs "p4_ls_<profileId>") ----
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
    const val K_V22_MIGRATED = "p4_v22_migrated"

    // Legacy v18 migration flag — kept for idempotent re-runs of older code paths.
    const val K_V18_MIGRATED = "p4_v18_migrated"

    /** Per-profile SharedPrefs file name: "p4_ls_<profileId>".
     *  Default profile uses "p4_ls_default" so a fresh install lines up with
     *  the global ProPrefs file "p4_ls" only for the global keys — never mixed. */
    fun prefsName(profileId: String): String = "p4_ls_${profileId.ifBlank { Profiles.DEFAULT }}"

    /** Cached SharedPreferences instances per profile id. */
    private val instances = java.util.Collections.synchronizedMap(mutableMapOf<String, SharedPreferences>())

    /** Return the SharedPreferences for the ACTIVE profile. */
    fun prefsFor(ctx: Context, profileId: String? = null): SharedPreferences {
        val pid = profileId ?: Profiles.activeId(ctx)
        val name = prefsName(pid)
        return instances.getOrPut(name) {
            ctx.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        }
    }

    /** Drop the cached instance for a profile — used on switch / delete. */
    fun invalidate(profileId: String) {
        synchronized(instances) { instances.remove(prefsName(profileId)) }
    }

    /** Read a value from the active profile's SharedPrefs. */
    fun get(ctx: Context, key: String, default: String? = null): String? =
        try { prefsFor(ctx).getString(key, default) } catch (_: Exception) { default }

    /** Write a value to the active profile's SharedPrefs (async apply). */
    fun put(ctx: Context, key: String, value: String) = try {
        prefsFor(ctx).edit().putString(key, value).apply()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "put $key failed: ${e.message}") }

    /** Write a value synchronously (commit) — used by the flush-before-close path. */
    fun putSync(ctx: Context, key: String, value: String) = try {
        prefsFor(ctx).edit().putString(key, value).commit()
    } catch (e: Exception) { ProLog.w("PROFILESTATE", "putSync $key failed: ${e.message}") }

    /** Remove a key from the active profile's SharedPrefs. */
    fun remove(ctx: Context, key: String) = try {
        prefsFor(ctx).edit().remove(key).apply()
    } catch (_: Exception) {}

    /** True if the key exists in the active profile's SharedPrefs. */
    fun has(ctx: Context, key: String): Boolean = prefsFor(ctx).contains(key)

    /** All keys in the active profile's SharedPrefs. */
    fun keys(ctx: Context): List<String> = try {
        prefsFor(ctx).all.keys.toList()
    } catch (_: Exception) { emptyList() }

    /**
     * New-user detection (BATCH-4B item 6, retained).
     *
     * Returns true ONLY when none of the strong signals are present:
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
     * One-shot v22 migration: copy legacy global p4_onboarded / p4_health_* /
     * workoutEmergencyBackup values from the global ProPrefs ("p4_ls") into
     * EVERY existing profile's per-profile SharedPrefs ("p4_ls_<profileId>")
     * so existing users keep their onboarded / screened state and never see
     * the wizard fire again after upgrade.
     *
     * Idempotent — gated on the global `p4_v22_migrated` flag (stored in
     * ProPrefs so it is global). New profiles created after v22 do NOT inherit
     * the legacy flag (they're genuinely new).
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, K_V22_MIGRATED) == "true") return
        val profiles = Profiles.list(ctx)
        val legacyOnboarded = ProPrefs.get(ctx, K_ONBOARDED)
        val legacyObDraft = ProPrefs.get(ctx, K_OB_DRAFT)
        val legacyObFinishedAt = ProPrefs.get(ctx, K_OB_FINISHED_AT)
        val legacyFirstLaunch = ProPrefs.get(ctx, K_FIRST_LAUNCH)
        val legacyHealth = mapOf(
            K_HEALTH_SCREENED to ProPrefs.get(ctx, K_HEALTH_SCREENED),
            K_HEALTH_TIER to ProPrefs.get(ctx, K_HEALTH_TIER),
            K_HEALTH_GENERAL to ProPrefs.get(ctx, K_HEALTH_GENERAL),
            K_HEALTH_FOLLOWUP to ProPrefs.get(ctx, K_HEALTH_FOLLOWUP),
            K_HEALTH_JOINTS to ProPrefs.get(ctx, K_HEALTH_JOINTS),
            K_HEALTH_UNLOCKED to ProPrefs.get(ctx, K_HEALTH_UNLOCKED),
            K_HEALTH_DECLARED to ProPrefs.get(ctx, K_HEALTH_DECLARED),
            K_HEALTH_INTRO_SEEN to ProPrefs.get(ctx, K_HEALTH_INTRO_SEEN),
            K_EMERGENCY_BACKUP to ProPrefs.get(ctx, K_EMERGENCY_BACKUP),
        )
        for (p in profiles) {
            val sp = ctx.applicationContext.getSharedPreferences(prefsName(p.id), Context.MODE_PRIVATE)
            val ed = sp.edit()
            if (legacyOnboarded != null && !sp.contains(K_ONBOARDED)) ed.putString(K_ONBOARDED, legacyOnboarded)
            if (legacyObDraft != null && !sp.contains(K_OB_DRAFT)) ed.putString(K_OB_DRAFT, legacyObDraft)
            if (legacyObFinishedAt != null && !sp.contains(K_OB_FINISHED_AT)) ed.putString(K_OB_FINISHED_AT, legacyObFinishedAt)
            if (legacyFirstLaunch != null && !sp.contains(K_FIRST_LAUNCH)) ed.putString(K_FIRST_LAUNCH, legacyFirstLaunch)
            legacyHealth.forEach { (k, v) ->
                if (v != null && !sp.contains(k)) ed.putString(k, v)
            }
            ed.apply()
            ProLog.i("MIGRATE", "v22 prefs migrated for ${p.id} (${p.name})")
        }
        ProPrefs.put(ctx, K_V22_MIGRATED, "true")
        ProLog.i("MIGRATE", "v22 prefs migration complete for ${profiles.size} profiles")
    }
}
