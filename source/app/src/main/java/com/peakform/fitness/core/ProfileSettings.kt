package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

/**
 * ProfileSettings — facade over ProfileState for named per-profile settings keys
 * (BATCH-5A).
 *
 * Every key that previously lived in the global ProPrefs ("p4_ls" SharedPreferences
 * file) and was tied to the active profile now lives in the per-profile
 * SharedPreferences file (`p4_ls_$profileId`, see ProfileState). ProfileSettings
 * centralises:
 *   - the key constants so callers stop hard-coding "p4_*" strings,
 *   - get/put/remove/has routing through ProfileState (active profile namespace),
 *   - notification-key helpers (p4_notif_<key> / p4_notif_suppressed_<key>),
 *   - the one-shot v22 migration from legacy global ProPrefs into the DEFAULT
 *     profile's per-profile store.
 *
 * GLOBAL keys (NOT migrated here, kept in ProPrefs): p4_pending_section,
 * p4_share_text (transient), deviceId, p4_v18_migrated / p4_v22_migrated
 * (migration gates), profile-registry pointers.
 */
object ProfileSettings {

    // ---- theme + Material You ----
    const val K_THEME = "p4_theme"
    const val K_DYNAMIC = "p4_dynamic"

    // ---- UI prefs ----
    const val K_GYM = "p4_gym"
    const val K_LANG = "p4_lang"
    const val K_VOCAB = "p4_vocab"
    const val K_POWER_SETTINGS = "p4_power_settings"
    const val K_SECURE = "p4_secure"

    // ---- library prefs ----
    const val K_LIB_GROUP = "p4_lib_group"
    const val K_LIB_CHUNK = "p4_lib_chunk"
    const val K_LIB_STREAM = "p4_lib_stream"
    const val K_LIB_EXPAND_ALL = "p4_lib_expand_all"
    const val K_LIBRARY_COLLAPSED = "p4_library_collapsed"

    // ---- notifications ----
    const val K_NOTIF_ASKED = "p4_notif_asked"
    const val K_NOTIF_SYSTEM = "p4_notif_system"
    const val K_NOTIF_PREFIX = "p4_notif_"
    const val K_NOTIF_SUPPRESSED_PREFIX = "p4_notif_suppressed_"

    // ---- other per-profile state ----
    const val K_COMPETITION = "p4_competition"
    const val K_STUDIO_EXPORT = "p4_studio_export"
    const val K_LAST_BACKUP = "p4_last_backup"
    const val K_LAST_STREAK_RECOVER = "lastStreakRecoverAt"
    const val K_WORKOUT_DATA = "workoutData"

    /**
     * Keys that the v22 migration copies from legacy global ProPrefs into the
     * DEFAULT profile's per-profile store. Add new per-profile keys here as the
     * app grows — the migration is idempotent so adding a key after a prior
     * migration already ran will still copy it on the next cold start.
     */
    val MIGRATED_KEYS = listOf(
        K_THEME, K_DYNAMIC, K_GYM, K_LANG, K_VOCAB, K_POWER_SETTINGS, K_SECURE,
        K_LIB_GROUP, K_LIB_CHUNK, K_LIB_STREAM, K_LIB_EXPAND_ALL, K_LIBRARY_COLLAPSED,
        K_NOTIF_ASKED, K_NOTIF_SYSTEM, K_COMPETITION, K_STUDIO_EXPORT,
        K_LAST_BACKUP, K_LAST_STREAK_RECOVER, K_WORKOUT_DATA,
    )

    // ---- routing through ProfileState (active profile namespace) ----

    fun get(ctx: Context, key: String, default: String? = null): String? =
        ProfileState.get(ctx, key, default)

    fun put(ctx: Context, key: String, value: String) =
        ProfileState.put(ctx, key, value)

    fun putSync(ctx: Context, key: String, value: String) =
        ProfileState.putSync(ctx, key, value)

    fun remove(ctx: Context, key: String) =
        ProfileState.remove(ctx, key)

    fun has(ctx: Context, key: String): Boolean =
        ProfileState.has(ctx, key)

    // ---- notification helpers ----

    /** p4_notif_<key> — absent = on (legacy contract). */
    fun getNotif(ctx: Context, key: String, default: Boolean = true): Boolean {
        val v = get(ctx, K_NOTIF_PREFIX + key)
        return if (v == null) default else v != "false"
    }

    fun setNotif(ctx: Context, key: String, on: Boolean) {
        put(ctx, K_NOTIF_PREFIX + key, if (on) "true" else "false")
    }

    /** Per-toast "Don't show today" suppression (legacy p4_notif_suppressed_<key>). */
    fun getNotifSuppressed(ctx: Context, key: String): String? =
        get(ctx, K_NOTIF_SUPPRESSED_PREFIX + key)

    fun setNotifSuppressed(ctx: Context, key: String, stamp: String) {
        put(ctx, K_NOTIF_SUPPRESSED_PREFIX + key, stamp)
    }

    /**
     * One-shot v22 migration: copy legacy global ProPrefs values into the DEFAULT
     * profile's per-profile SharedPreferences (`p4_ls_default`). Existing users
     * keep their theme, gym mode, language, etc. on the default profile after
     * the storage boundary moves from global → per-profile.
     *
     * Idempotent — gated on the global `p4_v22_migrated` flag (kept in ProPrefs
     * because the gate itself must be visible across all profiles). Only the
     * DEFAULT profile is touched — non-default profiles start with no values
     * and inherit the user's choices per-profile from then on.
     *
     * Safe to call on every cold start; the gate makes it a no-op after the
     * first run.
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, ProfileState.K_V22_MIGRATED) == "true") return
        val sp = ProfileState.prefsFor(ctx, Profiles.DEFAULT)
        val ed = sp.edit()
        var copied = 0
        for (k in MIGRATED_KEYS) {
            val v = ProPrefs.get(ctx, k) ?: continue
            if (sp.contains(k)) continue  // do not overwrite per-profile values
            ed.putString(k, v)
            copied++
        }
        ed.commit()
        ProPrefs.put(ctx, ProfileState.K_V22_MIGRATED, "true")
        ProLog.i("MIGRATE", "v22 settings migration complete — $copied key(s) copied into p4_ls_default")
    }
}
