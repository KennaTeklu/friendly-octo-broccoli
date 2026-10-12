package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

/**
 * ProfileSettings — per-profile named-constants + accessors for the legacy
 * P4 settings keys (theme, gym mode, language, vocab level, power settings,
 * notifications, library prefs, training environment, etc).
 *
 * BATCH-5A: every key below used to live in the global ProPrefs (`p4_ls`).
 * Routing the accessors through ProfileState means a freshly-switched profile
 * opens with its OWN gym mode / language / accent / etc, not the previous
 * profile's. The legacy v22 migration is one-shot, idempotent, and gated on
 * `p4_v22_migrated`.
 *
 * Storage contract (per-profile file `p4_ls_<profileId>`):
 *   K_THEME                → JSON {mode, accent} blob (legacy p4_theme)
 *   K_DYNAMIC              → "on" / "off" / null (Material You)
 *   K_GYM                  → "on" / "off"
 *   K_LANG                 → "auto" / language code
 *   K_VOCAB                → integer-as-string reading level 1..10
 *   K_POWER_SETTINGS       → JSON {autoRest, haptics, swipe, dock}
 *   K_SECURE               → "on" / "off"  (screenshot-safe mode)
 *   K_LIB_GROUP            → "muscleGroup" / "equipment" / etc.
 *   K_LIB_CHUNK            → integer-as-string (10..60)
 *   K_LIB_EXPAND_ALL       → "true" / "false"
 *   K_LIB_STREAM           → "true" / "false"
 *   K_LIBRARY_COLLAPSED    → JSON list of collapsed group keys
 *   K_COMPETITION          → "YYYY-MM-DD"
 *   K_NOTIF_<key>          → "true" / "false" (notification toggles)
 */
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
    const val K_LIB_EXPAND_ALL = "p4_lib_expand_all"
    const val K_LIB_STREAM = "p4_lib_stream"
    const val K_LIBRARY_COLLAPSED = "p4_library_collapsed"
    const val K_COMPETITION = "p4_competition"
    const val K_LAST_BACKUP = "p4_last_backup"
    const val K_NOTIF_SYSTEM = "p4_notif_system"

    /** Keys that were lifted from global ProPrefs to per-profile in v22.
     *  The v22 migration copies each of these from `p4_ls` (global) into the
     *  per-profile file ONLY when the target is absent — never overwrites
     *  per-profile state a user already wrote. */
    val MIGRATED_KEYS: List<String> = listOf(
        K_THEME, K_DYNAMIC, K_GYM, K_LANG, K_VOCAB, K_POWER_SETTINGS,
        K_SECURE, K_LIB_GROUP, K_LIB_CHUNK, K_LIB_EXPAND_ALL, K_LIB_STREAM,
        K_LIBRARY_COLLAPSED, K_COMPETITION, K_LAST_BACKUP, K_NOTIF_SYSTEM,
    )

    fun get(ctx: Context, key: String, default: String? = null): String? = ProfileState.get(ctx, key, default)
    fun put(ctx: Context, key: String, value: String) = ProfileState.put(ctx, key, value)
    fun remove(ctx: Context, key: String) = ProfileState.remove(ctx, key)
    fun has(ctx: Context, key: String): Boolean = ProfileState.has(ctx, key)

    /** Notification toggle read. Defaults to ON (legacy behavior). */
    fun getNotif(ctx: Context, key: String): Boolean = ProfileState.get(ctx, "p4_notif_$key") != "false"
    /** Notification toggle write. */
    fun setNotif(ctx: Context, key: String, on: Boolean) {
        ProfileState.put(ctx, "p4_notif_$key", if (on) "true" else "false")
    }

    /**
     * BATCH-5A: v22 migration. Copy every MIGRATED_KEYS entry from the legacy
     * global ProPrefs (`p4_ls`) into the active profile's per-profile prefs
     * file (`p4_ls_<profileId>`) — idempotent, gated on `p4_v22_migrated`.
     *
     * Runs once per profile on cold start (gated per-profile). The default
     * profile inherits the legacy global values; other profiles start with
     * the same defaults when first switched into (only the values that
     * exist in the legacy global file are copied).
     */
    fun migrateFromPrefs(ctx: Context) {
        if (ProfileState.get(ctx, ProfileState.K_V22_MIGRATED) == "true") return
        try {
            val sp = ProfileState.prefs(ctx)
            val ed = sp.edit()
            MIGRATED_KEYS.forEach { key ->
                if (!sp.contains(key)) {
                    val legacy = ProPrefs.get(ctx, key)
                    if (legacy != null) ed.putString(key, legacy)
                }
            }
            // Notif toggles — also per-profile now.
            listOf("retest_reminder", "deload_notice", "period_notice", "test_notif", "backup_nudge").forEach { nkey ->
                val fullKey = "p4_notif_$nkey"
                if (!sp.contains(fullKey)) {
                    val legacy = ProPrefs.get(ctx, fullKey)
                    if (legacy != null) ed.putString(fullKey, legacy)
                }
            }
            ed.commit()
            ProfileState.put(ctx, ProfileState.K_V22_MIGRATED, "true")
            ProLog.i("MIGRATE", "v22 profile-settings migration complete for active profile")
        } catch (e: Exception) {
            ProLog.w("MIGRATE", "v22 profile-settings migration failed: ${e.message}")
        }
    }
}
