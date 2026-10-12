package com.peakform.fitness.core

import android.content.Context

/**
 * ProfileSettings — the per-profile settings facade.
 *
 * BATCH-5B: every per-profile setting now flows through this object so the
 * call sites are explicit about which keys are per-profile vs global. The
 * underlying storage is ProfileState (per-profile SharedPrefs "p4_ls_<pid>").
 *
 * Migration: on first v22 cold start, every per-profile setting is copied
 * out of the legacy global ProPrefs ("p4_ls") into the active profile's
 * per-profile SharedPrefs (see ProfileState.migrateFromPrefs). The
 * MIGRATED_KEYS set documents which legacy global keys crossed over.
 */
object ProfileSettings {

    // ---- per-profile key constants (named) ----
    // Onboarding / health
    const val ONBOARDED = ProfileState.K_ONBOARDED
    const val OB_DRAFT = ProfileState.K_OB_DRAFT
    const val OB_FINISHED_AT = ProfileState.K_OB_FINISHED_AT
    const val FIRST_LAUNCH = ProfileState.K_FIRST_LAUNCH
    const val HEALTH_SCREENED = ProfileState.K_HEALTH_SCREENED
    const val HEALTH_TIER = ProfileState.K_HEALTH_TIER
    const val HEALTH_GENERAL = ProfileState.K_HEALTH_GENERAL
    const val HEALTH_FOLLOWUP = ProfileState.K_HEALTH_FOLLOWUP
    const val HEALTH_JOINTS = ProfileState.K_HEALTH_JOINTS
    const val HEALTH_UNLOCKED = ProfileState.K_HEALTH_UNLOCKED
    const val HEALTH_DECLARED = ProfileState.K_HEALTH_DECLARED
    const val HEALTH_INTRO_SEEN = ProfileState.K_HEALTH_INTRO_SEEN
    const val EMERGENCY_BACKUP = ProfileState.K_EMERGENCY_BACKUP

    // Training prefs / per-profile
    const val TRAINING_MODE = "p4_training_mode"
    const val REST_TIME = "p4_rest_time"
    const val PROGRESSION_RATE = "p4_progression_rate"
    const val WORKOUT_DAYS = "p4_workout_days"
    const val EXPRESS_MODE = "p4_express_mode"
    const val AGGRESSION = "p4_aggression"
    const val COMPETITION_DATE = "p4_competition"
    const val HEIGHT = "p4_height"
    const val EXPERIENCE = "p4_experience"
    const val GOAL = "p4_goal"

    // Library prefs (per-profile — different users have different libraries)
    const val LIB_GROUP = "p4_lib_group"
    const val LIB_CHUNK = "p4_lib_chunk"
    const val LIB_EXPAND_ALL = "p4_lib_expand_all"
    const val LIB_STREAM = "p4_lib_stream"
    const val LIB_COLLAPSED = "p4_library_collapsed"
    const val CUSTOM_EX = "p4_custom_ex"

    // Notifications (per-profile)
    const val NOTIF_RETEST = "p4_notif_retest_reminder"
    const val NOTIF_DELOAD = "p4_notif_deload_notice"
    const val NOTIF_PERIOD = "p4_notif_period_notice"
    const val NOTIF_SYSTEM = "p4_notif_system"
    const val NOTIF_SUPPRESS_PREFIX = "p4_notif_suppressed_"
    const val NOTIF_ASKED = "p4_notif_asked"

    // Vocabulary + language (per-profile)
    const val VOCAB_LEVEL = "p4_vocab"
    const val LANG = "p4_lang"

    // Power-user (per-profile)
    const val POWER_SETTINGS = "p4_power_settings"
    const val GYM_MODE = "p4_gym"
    const val SECURE_MODE = "p4_secure"

    // Streak / competition
    const val STREAK_RECOVER_AT = "lastStreakRecoverAt"

    // Onboarding Ask / Vocab
    const val ONBOARD_ASK_LEVEL = "p4_ob_ask_level"

    /** The set of legacy global keys that crossed over to per-profile in v22. */
    val MIGRATED_KEYS: Set<String> = setOf(
        ONBOARDED, OB_DRAFT, OB_FINISHED_AT, FIRST_LAUNCH,
        HEALTH_SCREENED, HEALTH_TIER, HEALTH_GENERAL, HEALTH_FOLLOWUP,
        HEALTH_JOINTS, HEALTH_UNLOCKED, HEALTH_DECLARED, HEALTH_INTRO_SEEN,
        EMERGENCY_BACKUP,
        TRAINING_MODE, REST_TIME, PROGRESSION_RATE, WORKOUT_DAYS,
        EXPRESS_MODE, AGGRESSION, COMPETITION_DATE,
        LIB_GROUP, LIB_CHUNK, LIB_EXPAND_ALL, LIB_STREAM, LIB_COLLAPSED,
        CUSTOM_EX,
        NOTIF_RETEST, NOTIF_DELOAD, NOTIF_PERIOD, NOTIF_SYSTEM,
        VOCAB_LEVEL, LANG,
        POWER_SETTINGS, GYM_MODE,
        STREAK_RECOVER_AT,
    )

    // ---- pass-through API to ProfileState (active profile) ----
    fun get(ctx: Context, key: String, default: String? = null): String? = ProfileState.get(ctx, key, default)
    fun put(ctx: Context, key: String, value: String) = ProfileState.put(ctx, key, value)
    fun remove(ctx: Context, key: String) = ProfileState.remove(ctx, key)
    fun has(ctx: Context, key: String): Boolean = ProfileState.has(ctx, key)

    // ---- typed helpers ----
    fun getInt(ctx: Context, key: String, default: Int): Int = ProfileState.get(ctx, key)?.toIntOrNull() ?: default
    fun putInt(ctx: Context, key: String, value: Int) = ProfileState.put(ctx, key, value.toString())

    fun getBool(ctx: Context, key: String, default: Boolean): Boolean =
        ProfileState.get(ctx, key)?.let { it == "true" || it == "on" } ?: default
    fun putBool(ctx: Context, key: String, value: Boolean) =
        ProfileState.put(ctx, key, if (value) "true" else "false")

    // ---- notification opts (per-profile) ----
    fun getNotif(ctx: Context, key: String): Boolean = ProfileState.get(ctx, key) != "false"
    fun setNotif(ctx: Context, key: String, on: Boolean) =
        ProfileState.put(ctx, key, if (on) "true" else "false")

    /**
     * One-shot v22 migration: copy every MIGRATED_KEYS entry from the global
     * ProPrefs into the active profile's per-profile SharedPrefs. Idempotent
     * — gated on the global `p4_v22_migrated` flag (set by ProfileState).
     */
    fun migrateFromPrefs(ctx: Context) {
        // ProfileState handles the gate; we just supply the keys to migrate.
        // The actual copy is done per-profile in ProfileState.migrateFromPrefs.
        ProfileState.migrateFromPrefs(ctx)
    }
}
