package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

/**
 * ProfileSettings — per-profile facade for every user-visible setting.
 *
 * BATCH-5 Item 1: previously, user-visible settings (theme, accent, Material You,
 * Gym Mode, language, vocab level, library prefs, notification prefs, training
 * prefs, etc.) lived in the global `ProPrefs` SharedPreferences ("p4_ls" file).
 * This meant every profile shared the same theme, the same Gym Mode, the same
 * library group-by — switching profiles did not switch settings.
 *
 * This facade routes those keys through `ProfileState` (Room `meta` table in the
 * active profile's namespace). The API mirrors ProPrefs (get/put/remove/keys/all)
 * so callers swap `ProPrefs` → `ProfileSettings` with no other change.
 *
 * Storage boundary:
 *   PER-PROFILE (this file → Room meta): theme, accent, Material You, Gym Mode,
 *     language, vocab level, power settings, secure mode, library group/stream/
 *     chunk/expand-all/collapsed, notification prefs, competition, studio export,
 *     last backup, streak recover, workoutData mirror, onboarding, health.
 *   GLOBAL (ProPrefs, not this file): profile registry, active/default pointer,
 *     deviceId, consent log, migration flags, transient pending_section/share_text.
 *
 * Migration: `migrateFromPrefs()` runs once on v22 cold start. It copies every
 * per-profile key from the global ProPrefs into the DEFAULT profile's meta table
 * (and into every existing profile's meta, since the legacy global value was
 * shared across all profiles — each profile inherits the same starting value).
 * Idempotent, gated on `p4_v22_migrated`. No data loss.
 */
object ProfileSettings {

    // ---- Per-profile setting keys (moved from ProPrefs → Room meta) ----
    // Theme + appearance
    const val K_THEME = "p4_theme"
    const val K_DYNAMIC = "p4_dynamic"
    const val K_GYM = "p4_gym"
    // Language + reading
    const val K_LANG = "p4_lang"
    const val K_VOCAB = "p4_vocab"
    // Power + privacy
    const val K_POWER_SETTINGS = "p4_power_settings"
    const val K_SECURE = "p4_secure"
    // Library prefs
    const val K_LIB_GROUP = "p4_lib_group"
    const val K_LIB_CHUNK = "p4_lib_chunk"
    const val K_LIB_STREAM = "p4_lib_stream"
    const val K_LIB_EXPAND_ALL = "p4_lib_expand_all"
    const val K_LIBRARY_COLLAPSED = "p4_library_collapsed"
    // Notifications + misc
    const val K_NOTIF_ASKED = "p4_notif_asked"
    const val K_NOTIF_SYSTEM = "p4_notif_system"
    const val K_COMPETITION = "p4_competition"
    const val K_STUDIO_EXPORT = "p4_studio_export"
    const val K_LAST_BACKUP = "p4_last_backup"
    const val K_LAST_STREAK_RECOVER = "lastStreakRecoverAt"
    const val K_WORKOUT_DATA = "workoutData"

    // Notification per-key prefix (p4_notif_<key> + p4_notif_suppressed_<key>)
    const val K_NOTIF_PREFIX = "p4_notif_"
    const val K_NOTIF_SUPPRESSED_PREFIX = "p4_notif_suppressed_"

    /** The set of keys that migrated from ProPrefs to per-profile meta in v22. */
    val MIGRATED_KEYS = listOf(
        K_THEME, K_DYNAMIC, K_GYM, K_LANG, K_VOCAB,
        K_POWER_SETTINGS, K_SECURE,
        K_LIB_GROUP, K_LIB_CHUNK, K_LIB_STREAM, K_LIB_EXPAND_ALL, K_LIBRARY_COLLAPSED,
        K_NOTIF_ASKED, K_NOTIF_SYSTEM, K_COMPETITION, K_STUDIO_EXPORT,
        K_LAST_BACKUP, K_LAST_STREAK_RECOVER, K_WORKOUT_DATA,
    )

    // ---- ProPrefs-compatible API (routes through ProfileState → Room meta) ----

    fun get(ctx: Context, key: String, def: String? = null): String? =
        ProfileState.get(ctx, key, def)

    fun put(ctx: Context, key: String, value: String) =
        ProfileState.put(ctx, key, value)

    fun remove(ctx: Context, key: String) =
        ProfileState.remove(ctx, key)

    fun has(ctx: Context, key: String): Boolean =
        ProfileState.has(ctx, key)

    /** Notification pref helper — per-profile notification toggles. */
    fun getNotif(ctx: Context, key: String, def: String? = null): String? =
        ProfileState.get(ctx, K_NOTIF_PREFIX + key, def)

    fun setNotif(ctx: Context, key: String, value: String) =
        ProfileState.put(ctx, K_NOTIF_PREFIX + key, value)

    /** Suppression stamp — stores the date string so suppressedToday() can
     *  compare against today's date. Returns null if not suppressed. */
    fun getNotifSuppressed(ctx: Context, key: String): String? =
        ProfileState.get(ctx, K_NOTIF_SUPPRESSED_PREFIX + key)

    fun setNotifSuppressed(ctx: Context, key: String, date: String) =
        ProfileState.put(ctx, K_NOTIF_SUPPRESSED_PREFIX + key, date)

    /**
     * One-shot v22 migration: copy every per-profile key from the global
     * ProPrefs into EVERY existing profile's Room `meta` table.
     *
     * Idempotent — gated on the global `p4_v22_migrated` flag. Each key is
     * only written if the profile's meta table does not already contain it
     * (so a re-run after a partial failure does not overwrite user changes).
     *
     * The DEFAULT profile (pro.db) is the primary recipient — it inherits
     * the legacy global value verbatim. Other profiles inherit the same
     * value because the legacy global was shared across all profiles; each
     * profile can then diverge from the shared starting point.
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProPrefs.get(ctx, "p4_v22_migrated") == "true") return
        val profiles = Profiles.list(ctx)
        ProLog.i("MIGRATE", "v22 per-profile settings migration: ${profiles.size} profiles")

        for (p in profiles) {
            val dbName = Profiles.dbNameFor(ctx, p.id)
            try {
                ProDb.withDb(ctx, dbName) { dao ->
                    for (key in MIGRATED_KEYS) {
                        val legacy = ProPrefs.get(ctx, key) ?: continue
                        // Only write if the profile doesn't already have this key
                        // (idempotent — don't overwrite user changes after partial migration)
                        if (dao.metaSync(key) == null) {
                            dao.putMetaSync(MetaRow(key, legacy))
                        }
                    }
                    // Migrate notification prefs (p4_notif_* keys, excluding the prefixes
                    // that are handled above as constants)
                    val allPrefs = ProPrefs.all(ctx)
                    allPrefs.forEach { (k, v) ->
                        if (v != null && k.startsWith(K_NOTIF_PREFIX) && k != K_NOTIF_ASKED && k != K_NOTIF_SYSTEM) {
                            if (dao.metaSync(k) == null) {
                                dao.putMetaSync(MetaRow(k, v))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                ProLog.w("MIGRATE", "v22 settings migration failed for ${p.id}: ${e.message}")
            }
        }

        ProPrefs.put(ctx, "p4_v22_migrated", "true")
        ProLog.i("MIGRATE", "v22 per-profile settings migration complete")
    }
}
