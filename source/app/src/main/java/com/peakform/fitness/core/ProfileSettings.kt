package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

/**
 * ProfileSettings — typed per-profile settings storage on top of ProfileState.
 *
 * BATCH-5A: every per-profile setting that used to live in the global ProPrefs
 * (`p4_ls`) file — theme, accent, Material You flag, gym mode, language,
 * vocab level, power-user settings, screenshot-safe mode, library collapsed
 * groups, notification flags, competition date, studio export, last-backup /
 * streak-recover timestamps, workoutData mirror — is now stored in the active
 * profile's SharedPreferences file (`p4_ls_<profileId>`).
 *
 * Why: a setting picked in Profile A (e.g. dark theme) was bleeding into
 * Profile B on switch, because the keys were global. Routing every read/write
 * through ProfileSettings makes the per-profile scope explicit at the call
 * site, and the underlying ProfileState automatically points at the right
 * file for the active profile.
 *
 * Migration (one-shot v22): on first v22 cold start, the legacy ProPrefs
 * values for these keys are copied into the DEFAULT profile's SharedPreferences
 * so existing users keep their settings. Idempotent and gated on
 * `p4_v22_migrated`.
 */
object ProfileSettings {
    // ---- per-profile setting keys (SharedPreferences `p4_ls_<profileId>`) ----
    const val K_THEME = "p4_theme"
    const val K_DYNAMIC = "p4_dynamic"
    const val K_GYM = "p4_gym"
    const val K_LANG = "p4_lang"
    const val K_VOCAB = "p4_vocab"
    const val K_POWER_SETTINGS = "p4_power_settings"
    const val K_SECURE = "p4_secure"
    const val K_LIB_GROUP = "p4_lib_group"
    const val K_LIB_CHUNK = "p4_lib_chunk"
    const val K_LIB_STREAM = "p4_lib_stream"
    const val K_LIB_EXPAND_ALL = "p4_lib_expand_all"
    const val K_LIBRARY_COLLAPSED = "p4_library_collapsed"
    const val K_NOTIF_ASKED = "p4_notif_asked"
    const val K_NOTIF_SYSTEM = "p4_notif_system"
    const val K_COMPETITION = "p4_competition"
    const val K_STUDIO_EXPORT = "p4_studio_export"
    const val K_LAST_BACKUP = "p4_last_backup"
    const val K_LAST_STREAK_RECOVER = "lastStreakRecoverAt"
    const val K_WORKOUT_DATA = "workoutData"

    // Notification flag prefixes — full key is `${K_NOTIF_PREFIX}${nudgeKey}`.
    const val K_NOTIF_PREFIX = "p4_notif_"
    const val K_NOTIF_SUPPRESSED_PREFIX = "p4_notif_suppressed_"

    /** BATCH-5A v22 migration: keys that previously lived on the global ProPrefs
     *  file and are now per-profile. On first v22 cold start their values are
     *  copied into the DEFAULT profile's SharedPreferences. */
    val MIGRATED_KEYS = listOf(
        K_THEME, K_DYNAMIC, K_GYM, K_LANG, K_VOCAB, K_POWER_SETTINGS, K_SECURE,
        K_LIB_GROUP, K_LIB_CHUNK, K_LIB_STREAM, K_LIB_EXPAND_ALL, K_LIBRARY_COLLAPSED,
        K_NOTIF_ASKED, K_NOTIF_SYSTEM, K_COMPETITION, K_STUDIO_EXPORT, K_LAST_BACKUP,
        K_LAST_STREAK_RECOVER, K_WORKOUT_DATA,
    )

    /** Migration gate (lives on the global ProPrefs file — one-shot per install). */
    private const val K_V22_MIGRATED = "p4_v22_migrated"

    fun get(ctx: Context, key: String, def: String? = null): String? = ProfileState.get(ctx, key, def)
    fun put(ctx: Context, key: String, value: String) = ProfileState.put(ctx, key, value)
    fun putSync(ctx: Context, key: String, value: String) = ProfileState.putSync(ctx, key, value)
    fun remove(ctx: Context, key: String) = ProfileState.remove(ctx, key)
    fun has(ctx: Context, key: String): Boolean = ProfileState.has(ctx, key)

    // ---- notification flag helpers ----

    /** Per-nudge enabled flag (defaults to enabled unless explicitly "false"). */
    fun getNotif(ctx: Context, key: String): Boolean =
        ProfileState.get(ctx, "${K_NOTIF_PREFIX}$key") != "false"

    fun setNotif(ctx: Context, key: String, enabled: Boolean) {
        ProfileState.put(ctx, "${K_NOTIF_PREFIX}$key", if (enabled) "true" else "false")
    }

    /** Per-nudge "don't show today" suppression stamp. */
    fun getNotifSuppressed(ctx: Context, key: String): String? =
        ProfileState.get(ctx, "${K_NOTIF_SUPPRESSED_PREFIX}$key")

    fun setNotifSuppressed(ctx: Context, key: String, day: String) {
        ProfileState.put(ctx, "${K_NOTIF_SUPPRESSED_PREFIX}$key", day)
    }

    /**
     * One-shot v22 migration: copy legacy global p4_theme / p4_dynamic / p4_gym
     * / p4_lang / p4_vocab / p4_power_settings / p4_secure / p4_library_* /
     * p4_notif_* / p4_competition / p4_studio_export / p4_last_backup /
     * lastStreakRecoverAt / workoutData values from ProPrefs into the DEFAULT
     * profile's SharedPreferences (`p4_ls_default`), so existing users keep
     * their settings after the move from global to per-profile scope.
     *
     * Idempotent — gated on the global `p4_v22_migrated` flag. New profiles
     * created after v22 inherit defaults, NOT the legacy values.
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, K_V22_MIGRATED) == "true") return
        val sp = ProfileState.prefsFor(ctx, Profiles.DEFAULT)
        val ed = sp.edit()
        for (key in MIGRATED_KEYS) {
            if (sp.contains(key)) continue  // don't overwrite an existing per-profile value
            val legacy = ProPrefs.get(ctx, key) ?: continue
            ed.putString(key, legacy)
        }
        ed.commit()
        ProPrefs.put(ctx, K_V22_MIGRATED, "true")
        ProLog.i("MIGRATE", "v22 settings migration complete for default profile")
    }
}
