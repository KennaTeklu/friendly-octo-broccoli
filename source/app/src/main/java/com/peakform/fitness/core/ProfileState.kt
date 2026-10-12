package com.peakform.fitness.core

import android.content.Context
import android.content.SharedPreferences
import com.peakform.fitness.ProLog

/**
 * ProfileState — per-profile state lives in a profile-scoped SharedPreferences file.
 *
 * BATCH-5A: storage boundary moved from the active profile's Room `meta` table
 * to a dedicated SharedPreferences file `p4_ls_<profileId>` per profile.
 *
 * Rationale:
 *  - SharedPreferences reads are O(1) and main-thread safe — the legacy metaSync
 *    queries blocked on the Room executor and were unsafe from the main thread.
 *  - Profile isolation is now structural: each profile's prefs file is fully
 *    independent, no namespace collisions, no migration needed when a profile's
 *    DB is rebuilt.
 *  - Backup/restore becomes trivial: the per-profile file is a stable key/value
 *    blob the bundle export can include verbatim.
 *
 * Storage boundary:
 *   PER-PROFILE (this file, p4_ls_<profileId>): onboarding flag, onboarding draft,
 *     onboarding finished_at, first-launch timestamp, health-screen state, health
 *     intro seen, emergency backup, training preferences, library collapsed state,
 *     streak recover timestamps.
 *   GLOBAL (ProPrefs "p4_ls"): theme mode+accent, Material You flag, gym mode,
 *     language, vocab level, active profile id, default profile id, device id,
 *     consent log.
 *
 * Migration: `migrateFromPrefs(ctx)` is a one-shot v22 migration that copies
 * legacy per-profile keys from the global ProPrefs file into each profile's
 * dedicated p4_ls_<profileId> file. Idempotent — gated on `p4_v22_migrated`.
 */
object ProfileState {
    // ---- per-profile keys (legacy K_* names preserved for bundle compat) ----
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
    const val K_V22_MIGRATED = "p4_v22_migrated"

    /** SharedPreferences file name for a profile. */
    fun prefsName(profileId: String): String = "p4_ls_$profileId"

    /** Returns the per-profile SharedPreferences for the given profile id. */
    fun prefsFor(ctx: Context, profileId: String): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(prefsName(profileId), Context.MODE_PRIVATE)

    /** Returns the SharedPreferences for the active profile. */
    fun prefs(ctx: Context): SharedPreferences = prefsFor(ctx, Profiles.activeId(ctx))

    /** Read a per-profile value (main-thread safe). */
    fun get(ctx: Context, key: String, default: String? = null): String? = try {
        prefs(ctx).getString(key, default)
    } catch (_: Exception) { default }

    /** Async write (legacy contract — apply()). */
    fun put(ctx: Context, key: String, value: String) = try {
        prefs(ctx).edit().putString(key, value).apply()
    } catch (e: Exception) {
        ProLog.w("PROFILESTATE", "put $key failed: ${e.message}")
    }

    /** Synchronous write (commit()). Used where the caller needs the value on
     *  disk before continuing (e.g. before a profile switch). */
    fun putSync(ctx: Context, key: String, value: String): Boolean = try {
        prefs(ctx).edit().putString(key, value).commit()
    } catch (e: Exception) {
        ProLog.w("PROFILESTATE", "putSync $key failed: ${e.message}")
        false
    }

    /** Remove a per-profile key. */
    fun remove(ctx: Context, key: String) = try {
        prefs(ctx).edit().remove(key).apply()
    } catch (_: Exception) {}

    /** True if the key exists for the active profile. */
    fun has(ctx: Context, key: String): Boolean = prefs(ctx).contains(key)

    /** All keys for the active profile. */
    fun keys(ctx: Context): List<String> = prefs(ctx).all.keys.toList()

    /**
     * New-user detection — a profile is "new" only if NONE of the strong signals
     * are present: p4_onboarded, p4_health_screened_at, workout count > 0,
     * exercise record count > 0, p4_first_launch. If ANY signal is set, treat
     * the profile as existing. Never re-onboard someone with workout history.
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
     * One-shot v22 migration: copy legacy per-profile keys from the GLOBAL
     * ProPrefs file (`p4_ls`) into each profile's DEDICATED per-profile file
     * (`p4_ls_<profileId>`). Idempotent — gated on `p4_v22_migrated` in the
     * global file.
     *
     * Why: prior versions stored per-profile keys (onboarding, health, draft,
     * emergency backup) in the global p4_ls file because there was no profile
     * scoping. Profile switching would silently leak state across profiles.
     * The v22 migration distributes each legacy key to its owning profile's
     * dedicated file (the global default profile gets everything, since we
     * can't tell which profile a legacy key belonged to; non-default profiles
     * start fresh on their own files).
     *
     * Also forwards the legacy v18 migration marker — if v18 already ran (i.e.
     * the meta table was populated and the global p4_v18_migrated flag is
     * set), we copy that flag into the per-profile files so any code reading
     * it from there sees the right value.
     */
    fun migrateFromPrefs(ctx: Context) {
        val global = ProPrefs.get(ctx)
        if (global.getString(K_V22_MIGRATED, null) == "true") return

        val profiles = Profiles.list(ctx)
        val legacyKeys = listOf(
            K_ONBOARDED, K_OB_DRAFT, K_OB_FINISHED_AT,
            K_HEALTH_SCREENED, K_HEALTH_TIER, K_HEALTH_GENERAL,
            K_HEALTH_FOLLOWUP, K_HEALTH_JOINTS, K_HEALTH_UNLOCKED,
            K_HEALTH_DECLARED, K_HEALTH_INTRO_SEEN, K_EMERGENCY_BACKUP,
            K_FIRST_LAUNCH, K_V18_MIGRATED,
        )

        // Default profile inherits ALL legacy keys (the global file was the
        // default profile's de-facto namespace before v22). Other profiles
        // start fresh — their dedicated files are empty, and they will be
        // populated naturally as the user interacts with them.
        val defaultId = Profiles.defaultId(ctx)
        val defaultPrefs = prefsFor(ctx, defaultId)
        val defaultEdit = defaultPrefs.edit()
        var defaultTouched = false
        for (k in legacyKeys) {
            val v = global.getString(k, null) ?: continue
            if (!defaultPrefs.contains(k)) {
                defaultEdit.putString(k, v)
                defaultTouched = true
            }
        }
        if (defaultTouched) defaultEdit.commit()

        // Mark every other profile as v22-migrated in their own files so
        // future code reading K_V22_MIGRATED from the per-profile file sees
        // "true" (the migration has run for them).
        for (p in profiles) {
            if (p.id == defaultId) continue
            val pp = prefsFor(ctx, p.id)
            if (!pp.contains(K_V22_MIGRATED)) {
                pp.edit().putString(K_V22_MIGRATED, "true").commit()
            }
        }

        global.edit().putString(K_V22_MIGRATED, "true").commit()
        ProLog.i("MIGRATE", "v22 per-profile prefs migration complete for ${profiles.size} profile(s)")
    }
}
