package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

object ProfileSettings {
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
    const val K_NOTIF_PREFIX = "p4_notif_"
    const val K_NOTIF_SUPPRESSED_PREFIX = "p4_notif_suppressed_"

    val MIGRATED_KEYS = listOf(K_THEME, K_DYNAMIC, K_GYM, K_LANG, K_VOCAB, K_POWER_SETTINGS, K_SECURE, K_LIB_GROUP, K_LIB_CHUNK, K_LIB_STREAM, K_LIB_EXPAND_ALL, K_LIBRARY_COLLAPSED, K_NOTIF_ASKED, K_NOTIF_SYSTEM, K_COMPETITION, K_STUDIO_EXPORT, K_LAST_BACKUP, K_LAST_STREAK_RECOVER, K_WORKOUT_DATA)

    fun get(ctx: Context, key: String, def: String? = null): String? = ProfileState.get(ctx, key, def)
    fun put(ctx: Context, key: String, value: String) = ProfileState.put(ctx, key, value)
    fun remove(ctx: Context, key: String) = ProfileState.remove(ctx, key)
    fun has(ctx: Context, key: String): Boolean = ProfileState.has(ctx, key)
    fun getNotif(ctx: Context, key: String, def: String? = null): String? = ProfileState.get(ctx, K_NOTIF_PREFIX + key, def)
    fun setNotif(ctx: Context, key: String, value: String) = ProfileState.put(ctx, K_NOTIF_PREFIX + key, value)
    fun getNotifSuppressed(ctx: Context, key: String): String? = ProfileState.get(ctx, K_NOTIF_SUPPRESSED_PREFIX + key)
    fun setNotifSuppressed(ctx: Context, key: String, date: String) = ProfileState.put(ctx, K_NOTIF_SUPPRESSED_PREFIX + key, date)

    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, "p4_v22_migrated") == "true") return
        ProLog.i("MIGRATE", "v22 per-profile settings migration (Shape A)")
        val sp = ProfileState.prefsFor(ctx, Profiles.DEFAULT)
        val ed = sp.edit()
        var wroteAny = false
        for (key in MIGRATED_KEYS) {
            val legacy = ProPrefs.get(ctx, key) ?: continue
            if (!sp.contains(key)) { ed.putString(key, legacy); wroteAny = true }
        }
        ProPrefs.all(ctx).forEach { (k, v) ->
            if (v != null && k.startsWith(K_NOTIF_PREFIX) && k != K_NOTIF_ASKED && k != K_NOTIF_SYSTEM) {
                if (!sp.contains(k)) { ed.putString(k, v); wroteAny = true }
            }
        }
        if (wroteAny) ed.apply()
        ProPrefs.put(ctx, "p4_v22_migrated", "true")
        ProLog.i("MIGRATE", "v22 per-profile settings migration complete")
    }
}
