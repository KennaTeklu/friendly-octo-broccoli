package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

/**
 * ProfileSettings — per-profile SETTINGS facade (Batch 5A).
 *
 * Wraps ProfileState so callers use named constants instead of bare string keys.
 * Every get/put/remove/has call routes through ProfileState → active profile's
 * SharedPreferences file (`p4_ls_<activeId>`).
 *
 * Boundary:
 *   - Anything that should change with the active profile (theme, language,
 *     gym mode, vocab level, power settings, library prefs, notification prefs,
 *     competition date, secure mode, studio export, last backup, last streak
 *     recover, workoutData mirror, etc.) lives here.
 *   - Truly global flags (active profile id, default profile id, device id,
 *     consent log, v18_migrated / v22_migrated one-shot flags) stay in
 *     ProPrefs (`p4_ls` global file).
 *
 * v22 migration: on first v22 cold start, copy the legacy global ProPrefs values
 * for every MIGRATED_KEY into the DEFAULT profile's `p4_ls_default` file. That
 * way an upgrading user keeps their pre-5A theme / language / library prefs
 * on the default profile. Other profiles start fresh (they had no per-profile
 * versions of these settings pre-5A anyway).
 */
object ProfileSettings {
    // ---- theme + look ----
    const val K_THEME = "p4_theme"
    const val K_DYNAMIC = "p4_dynamic"
    const val K_GYM = "p4_gym"
    const val K_SECURE = "p4_secure"

    // ---- language / vocab ----
    const val K_LANG = "p4_lang"
    const val K_VOCAB = "p4_vocab"

    // ---- power user ----
    const val K_POWER_SETTINGS = "p4_power_settings"

    // ---- library ----
    const val K_LIB_GROUP = "p4_lib_group"
    const val K_LIB_CHUNK = "p4_lib_chunk"
    const val K_LIB_EXPAND_ALL = "p4_lib_expand_all"
    const val K_LIB_STREAM = "p4_lib_stream"
    const val K_LIBRARY_COLLAPSED = "p4_library_collapsed"

    // ---- notifications ----
    const val K_NOTIF_SYSTEM = "p4_notif_system"
    const val K_NOTIF_ASKED = "p4_notif_asked"
    const val K_NOTIF_PREFIX = "p4_notif_"
    const val K_NOTIF_SUPPRESSED_PREFIX = "p4_notif_suppressed_"

    // ---- training + studio ----
    const val K_COMPETITION = "p4_competition"
    const val K_STUDIO_EXPORT = "p4_studio_export"

    // ---- timestamps / mirrors ----
    const val K_LAST_BACKUP = "p4_last_backup"
    const val K_LAST_STREAK_RECOVER = "lastStreakRecoverAt"
    const val K_WORKOUT_DATA = "workoutData"

    /**
     * Keys to copy from the legacy global ProPrefs into the DEFAULT profile's
     * `p4_ls_default` SharedPreferences on first v22 cold start. Idempotent —
     * only writes when the destination does not already contain the key.
     */
    val MIGRATED_KEYS: List<String> = listOf(
        K_THEME, K_DYNAMIC, K_GYM, K_LANG, K_VOCAB,
        K_POWER_SETTINGS, K_SECURE,
        K_LIB_GROUP, K_LIB_CHUNK, K_LIB_EXPAND_ALL, K_LIB_STREAM, K_LIBRARY_COLLAPSED,
        K_NOTIF_SYSTEM, K_NOTIF_ASKED,
        K_COMPETITION, K_STUDIO_EXPORT,
        K_LAST_BACKUP, K_LAST_STREAK_RECOVER, K_WORKOUT_DATA,
    )

    /** Migration one-shot flag — stored in the GLOBAL ProPrefs file (not per-profile). */
    const val K_V22_MIGRATED = "p4_v22_migrated"

    // ---- basic KV ops (route through ProfileState) ----
    fun get(ctx: Context, key: String, default: String? = null): String? =
        ProfileState.get(ctx, key, default)

    fun put(ctx: Context, key: String, value: String) = ProfileState.put(ctx, key, value)
    fun remove(ctx: Context, key: String) = ProfileState.remove(ctx, key)
    fun has(ctx: Context, key: String): Boolean = ProfileState.has(ctx, key)

    // ---- notification helpers ----
    /** Read p4_notif_<key>. Default true when missing (legacy invariant). */
    fun getNotif(ctx: Context, key: String, default: Boolean = true): Boolean {
        val raw = get(ctx, K_NOTIF_PREFIX + key) ?: return default
        return raw != "false"
    }

    fun setNotif(ctx: Context, key: String, on: Boolean) {
        put(ctx, K_NOTIF_PREFIX + key, if (on) "true" else "false")
    }

    /** Read p4_notif_suppressed_<key> — returns today's date string when suppressed, else null. */
    fun getNotifSuppressed(ctx: Context, key: String): String? =
        get(ctx, K_NOTIF_SUPPRESSED_PREFIX + key)

    fun setNotifSuppressed(ctx: Context, key: String, todayIso: String) {
        put(ctx, K_NOTIF_SUPPRESSED_PREFIX + key, todayIso)
    }

    fun isNotifSuppressedToday(ctx: Context, key: String): Boolean {
        val stamp = getNotifSuppressed(ctx, key) ?: return false
        return stamp == ProState.todayLocal()
    }

    /**
     * v22 one-shot migration: copy legacy global ProPrefs values for every
     * MIGRATED_KEY into the DEFAULT profile's `p4_ls_default` SharedPreferences.
     *
     * Idempotent — gated on the global `p4_v22_migrated` flag. Only writes
     * when the destination key is missing (so any user who has already
     * chosen a per-profile value after switch keeps their value).
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, K_V22_MIGRATED) == "true") return
        val sp = ProfileState.prefsFor(ctx, Profiles.DEFAULT)
        val ed = sp.edit()
        var copied = 0
        for (key in MIGRATED_KEYS) {
            val legacy = ProPrefs.get(ctx, key) ?: continue
            if (!sp.contains(key)) {
                ed.putString(key, legacy)
                copied++
            }
        }
        ed.commit()
        ProPrefs.put(ctx, K_V22_MIGRATED, "true")
        ProLog.i("MIGRATE", "v22 settings migration complete — $copied keys copied to p4_ls_default")
    }
}
