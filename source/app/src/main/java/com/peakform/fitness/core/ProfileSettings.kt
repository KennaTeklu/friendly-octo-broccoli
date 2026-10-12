package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

/**
 * ProfileSettings — named-constant facade over ProfileState for per-profile
 * settings keys (BATCH-5B).
 *
 * Why: ProfileState exposes the raw get/put/remove/has storage primitive;
 * callers previously referenced keys like "p4_lib_group" or "p4_notif_retest"
 * inline. ProfileSettings gathers every per-profile key in one place so
 * renames, audit, and the v22 migration can find them all at once.
 *
 * Storage: ProfileSettings does NOT own storage — it routes every read/write
 * through ProfileState, which itself uses the per-profile SharedPreferences
 * file `p4_ls_<profileId>`. Switching profiles is enough to isolate state;
 * no further work needed here.
 *
 * Notification toggles (getNotif/setNotif) live here because each profile
 * should own its notification preferences (a coach profile and a trainee
 * profile on the same device may want different reminders).
 *
 * Migration: `migrateFromPrefs(ctx, v = 22)` is the forward-compat hook —
 * future versions can pass a higher `v` to extend the migrated-keys list
 * without touching ProfileState.
 */
object ProfileSettings {

    // ---- library / browsing ----
    const val K_LIB_GROUP = "p4_lib_group"
    const val K_LIB_CHUNK = "p4_lib_chunk"
    const val K_LIB_STREAM = "p4_lib_stream"
    const val K_LIB_EXPAND_ALL = "p4_lib_expand_all"
    const val K_LIB_COLLAPSED = "p4_library_collapsed"
    const val K_LIB_RESET_AT = "p4_lib_reset_at"

    // ---- notifications (per-profile, not global) ----
    const val K_NOTIF_RETEST = "p4_notif_retest_reminder"
    const val K_NOTIF_DELOAD = "p4_notif_deload_notice"
    const val K_NOTIF_PERIOD = "p4_notif_period_notice"
    const val K_NOTIF_BACKUP = "p4_notif_backup_nudge"

    // ---- streak ----
    const val K_STREAK_RECOVER_AT = "lastStreakRecoverAt"

    // ---- onboarding wizard scratch (non-draft bits) ----
    const val K_OB_LAST_STEP = "p4_ob_last_step"

    // ---- training-environment prefs (per-profile) ----
    const val K_TRAINING_ENV = "p4_training_env"
    const val K_GOAL = "p4_goal"

    // ---- post-session feel (last entry, for coach tuning) ----
    const val K_LAST_FEEL = "p4_last_feel"
    const val K_LAST_RPE = "p4_last_rpe"

    // ---- tuning gear (per-profile coach knobs) ----
    const val K_TUNING_VOLUME = "p4_tuning_volume"
    const val K_TUNING_INTENSITY = "p4_tuning_intensity"
    const val K_TUNING_FREQUENCY = "p4_tuning_frequency"

    /** Keys migrated from global ProPrefs → per-profile prefs by the v22
     *  migration. MIRRORS ProfileState.migrateFromPrefs; extended here so
     *  future additions don't require editing two files. */
    val MIGRATED_KEYS: List<String> = listOf(
        ProfileState.K_ONBOARDED,
        ProfileState.K_OB_DRAFT,
        ProfileState.K_OB_FINISHED_AT,
        ProfileState.K_HEALTH_SCREENED,
        ProfileState.K_HEALTH_TIER,
        ProfileState.K_HEALTH_GENERAL,
        ProfileState.K_HEALTH_FOLLOWUP,
        ProfileState.K_HEALTH_JOINTS,
        ProfileState.K_HEALTH_UNLOCKED,
        ProfileState.K_HEALTH_DECLARED,
        ProfileState.K_HEALTH_INTRO_SEEN,
        ProfileState.K_EMERGENCY_BACKUP,
        ProfileState.K_FIRST_LAUNCH,
        K_LIB_GROUP,
        K_LIB_CHUNK,
        K_LIB_STREAM,
        K_LIB_EXPAND_ALL,
        K_LIB_COLLAPSED,
        K_STREAK_RECOVER_AT,
    )

    // ---- generic routing ----
    fun get(ctx: Context, key: String, default: String? = null): String? =
        ProfileState.get(ctx, key, default)

    fun put(ctx: Context, key: String, value: String) =
        ProfileState.put(ctx, key, value)

    fun remove(ctx: Context, key: String) =
        ProfileState.remove(ctx, key)

    fun has(ctx: Context, key: String): Boolean =
        ProfileState.has(ctx, key)

    // ---- notifications ----
    /** Per-profile notification toggle. Defaults ON (legacy contract — the
     *  legacy global key was `p4_notif_<key>` with the value "false" meaning
     *  off; absent meant on. */
    fun getNotif(ctx: Context, key: String): Boolean =
        get(ctx, notifKeyFor(key)) != "false"

    fun setNotif(ctx: Context, key: String, on: Boolean) {
        put(ctx, notifKeyFor(key), if (on) "on" else "false")
    }

    /** Map a logical notification id ("retest_reminder", etc.) to the
     *  per-profile prefs key. */
    private fun notifKeyFor(key: String): String = "p4_notif_$key"

    // ---- per-profile helpers ----
    /** Last-streak-recover-at ISO timestamp (honor-system daily guard). */
    fun lastStreakRecoverAt(ctx: Context): String? = get(ctx, K_STREAK_RECOVER_AT)

    fun setStreakRecoverAt(ctx: Context, iso: String) = put(ctx, K_STREAK_RECOVER_AT, iso)

    /** Library collapsed-group set, encoded as a JSON array string. */
    fun libraryCollapsed(ctx: Context): String? = get(ctx, K_LIB_COLLAPSED)
    fun setLibraryCollapsed(ctx: Context, encoded: String) = put(ctx, K_LIB_COLLAPSED, encoded)

    /**
     * Forward-compat migration hook. v22 routes through ProfileState.migrateFromPrefs
     * (which handles the legacy per-profile keys). Higher v values can extend
     * the migrated-keys list here without touching ProfileState.
     *
     * Safe to call multiple times — gated on K_V22_MIGRATED.
     */
    fun migrateFromPrefs(ctx: Context, v: Int = 22) {
        when (v) {
            22 -> ProfileState.migrateFromPrefs(ctx)
            else -> {
                ProfileState.migrateFromPrefs(ctx)
                ProLog.i("PROFILESETTINGS", "migrateFromPrefs v=$v — no extra keys")
            }
        }
    }
}
