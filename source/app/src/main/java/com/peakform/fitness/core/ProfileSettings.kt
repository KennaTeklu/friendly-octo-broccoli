package com.peakform.fitness.core

import android.content.Context

/**
 * ProfileSettings — typed, named accessors for the per-profile keys that
 * previously lived in the global ProPrefs file (`p4_ls`) (BATCH-5A).
 *
 * Each constant here is a key whose value is logically tied to a profile
 * (theme, gym mode, language, vocab level, library prefs, notification
 * toggles, …). Routing them through ProfileState guarantees per-profile
 * isolation: switching profiles switches the values.
 *
 * Migration (v22, one-shot): the v18 ProfileState migration already copied
 * onboarding / health keys into every profile's `p4_ls_<id>`; this
 * migrateFromPrefs() copies the BATCH-5A keys (theme, dynamic, gym, lang,
 * vocab, …) from global `p4_ls` into every existing profile. Idempotent —
 * gated on `p4_v22_migrated` (stored in the global prefs so it runs once
 * for the device, not once per profile).
 *
 * BATCH-5B ThemeController: theme (mode + accent) and the Material You flag
 * now live per-profile, so switching profiles switches the theme. The
 * ThemeController.reload(ctx) call in Profiles.switchTo() picks up the new
 * values from the freshly-active profile's SharedPreferences.
 */
object ProfileSettings {
    // Theme + display
    const val K_THEME = "p4_theme"                 // JSON {mode, accent}
    const val K_DYNAMIC = "p4_dynamic"             // "on" | "off"
    const val K_GYM = "p4_gym"                     // gym mode (giant buttons)
    const val K_LANG = "p4_lang"                   // UI language code
    const val K_VOCAB = "p4_vocab"                // reading level 1..10
    const val K_POWER_SETTINGS = "p4_power_settings"  // JSON {autoRest, haptics, …}

    // Privacy
    const val K_SECURE = "p4_secure"               // screenshot-safe mode

    // Library prefs
    const val K_LIB_GROUP = "p4_lib_group"         // default group-by mode
    const val K_LIB_CHUNK = "p4_lib_chunk"         // streaming chunk size
    const val K_LIB_STREAM = "p4_lib_stream"       // streaming on/off
    const val K_LIB_EXPAND_ALL = "p4_lib_expand_all"  // expand all groups on open
    const val K_LIBRARY_COLLAPSED = "p4_library_collapsed"  // JSON list of collapsed group names

    // Notifications
    const val K_NOTIF_ASKED = "p4_notif_asked"     // POST_NOTIFICATIONS asked once
    const val K_NOTIF_SYSTEM = "p4_notif_system"   // system notifications on/off
    const val K_NOTIF_PREFIX = "p4_notif_"
    const val K_NOTIF_SUPPRESSED_PREFIX = "p4_notif_suppressed_"

    // Misc per-profile state
    const val K_COMPETITION = "p4_competition"           // competition date
    const val K_STUDIO_EXPORT = "p4_studio_export"       // last exported library JSON
    const val K_LAST_BACKUP = "p4_last_backup"           // last backup ISO timestamp
    const val K_LAST_STREAK_RECOVER = "lastStreakRecoverAt"  // streak-recover honor timestamp
    const val K_WORKOUT_DATA = "workoutData"             // legacy mirror key

    /** Migration marker for v22 (BATCH-5A) — stored in global prefs so it
     *  fires exactly once for the device. */
    const val K_V22_MIGRATED = "p4_v22_migrated"

    /** Keys that migrate from global `p4_ls` → per-profile `p4_ls_<id>` in v22. */
    val MIGRATED_KEYS: List<String> = listOf(
        K_THEME, K_DYNAMIC, K_GYM, K_LANG, K_VOCAB, K_POWER_SETTINGS,
        K_SECURE, K_LIB_GROUP, K_LIB_CHUNK, K_LIB_STREAM, K_LIB_EXPAND_ALL,
        K_LIBRARY_COLLAPSED, K_NOTIF_ASKED, K_NOTIF_SYSTEM, K_COMPETITION,
        K_STUDIO_EXPORT, K_LAST_BACKUP, K_LAST_STREAK_RECOVER, K_WORKOUT_DATA,
    )

    // ---- generic accessors through ProfileState ----

    fun get(ctx: Context, key: String, default: String? = null): String? =
        ProfileState.get(ctx, key, default)

    fun put(ctx: Context, key: String, value: String) =
        ProfileState.put(ctx, key, value)

    fun putSync(ctx: Context, key: String, value: String): Boolean =
        ProfileState.putSync(ctx, key, value)

    fun remove(ctx: Context, key: String) =
        ProfileState.remove(ctx, key)

    fun has(ctx: Context, key: String): Boolean =
        ProfileState.has(ctx, key)

    // ---- notification helpers ----

    /** Notification opt (absent = on, like the legacy default). */
    fun getNotif(ctx: Context, key: String, defaultOn: Boolean = true): Boolean {
        val v = ProfileState.get(ctx, K_NOTIF_PREFIX + key) ?: return defaultOn
        return v != "false"
    }

    fun setNotif(ctx: Context, key: String, on: Boolean) {
        ProfileState.put(ctx, K_NOTIF_PREFIX + key, if (on) "true" else "false")
    }

    fun notifSuppressedToday(ctx: Context, key: String): Boolean {
        val stamp = ProfileState.get(ctx, K_NOTIF_SUPPRESSED_PREFIX + key) ?: return false
        return stamp == ProState.todayLocal()
    }

    fun suppressNotifToday(ctx: Context, key: String) {
        ProfileState.put(ctx, K_NOTIF_SUPPRESSED_PREFIX + key, ProState.todayLocal())
    }

    // ---- v22 migration (one-shot, device-wide) ----

    /**
     * Copy every key in MIGRATED_KEYS from global `p4_ls` into EVERY existing
     * profile's `p4_ls_<id>` SharedPreferences, but only when that key is
     * missing from the target profile (so a profile the user already customized
     * keeps its own values). Idempotent — gated on `p4_v22_migrated`.
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, K_V22_MIGRATED) == "true") return
        val profiles = Profiles.list(ctx)
        for (p in profiles) {
            val sp = ProfileState.prefsFor(ctx, p.id)
            val ed = sp.edit()
            for (k in MIGRATED_KEYS) {
                val legacy = ProPrefs.get(ctx, k) ?: continue
                if (!sp.contains(k)) ed.putString(k, legacy)
            }
            ed.commit()
        }
        ProPrefs.put(ctx, K_V22_MIGRATED, "true")
        com.peakform.fitness.ProLog.i("MIGRATE", "v22 settings migration complete for ${profiles.size} profiles")
    }
}
