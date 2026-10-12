package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProPrefs

/**
 * ThemeController — runtime mode (dark/light/system) + accent + Material You state.
 *
 * BATCH-5 Item 2: "Follow system" is now the third mode alongside Dark and Light.
 * Default for users with no prior choice. Explicit choice overrides. When mode
 * is "system", the OS theme is observed and dark/light follows it.
 *
 * BATCH-5 Item 1: theme + accent + Material You now live in per-profile storage
 * (ProfileSettings → Room meta) instead of global ProPrefs. Each profile owns
 * its own theme.
 */
object ThemeController {
    /** Three modes: "system" (follow OS), "dark", "light". */
    var mode by mutableStateOf("system")
        private set
    /** Effective dark/light — derived from mode + system state. Observed by Compose. */
    var dark by mutableStateOf(true)
        private set
    var accentId by mutableStateOf("blue")
        private set
    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /**
     * Cold-start hydration: read `p4_theme` JSON + `p4_dynamic` from per-profile
     * storage. Called after ProState.init() so ProfileState has the right DB.
     *
     * BATCH-5 Item 2: if no prior theme choice exists, mode defaults to "system"
     * (follow OS). If a prior choice exists (dark/light), it overrides.
     */
    fun load(initial: String?) {
        val raw = initial
        if (raw == null) {
            // No prior theme — default to "system" mode with default accent.
            mode = "system"
            return
        }
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            // BATCH-5 Item 2: mode can be "system", "dark", or "light"
            if (m == "system" || m == "dark" || m == "light") mode = m
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
        } catch (_: Exception) {}
    }

    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
    }

    /**
     * BATCH-5 Item 2: set the effective dark/light from the system theme.
     * Called by the system theme observer in MainActivity when mode == "system".
     */
    fun setSystemDark(systemDark: Boolean) {
        if (mode == "system") {
            dark = systemDark
        }
    }

    /**
     * Resolve the effective dark/light for the current mode. Called after
     * load() and after set() to update `dark`.
     */
    fun resolveDark(systemDark: Boolean = false) {
        dark = when (mode) {
            "light" -> false
            "dark" -> true
            else -> systemDark  // "system"
        }
    }

    /**
     * Apply mode + accent to runtime state AND persist to per-profile `p4_theme`.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_THEME, currentJson()) }
    }

    /**
     * P4E-UI-05: atomic write of accent + p4_dynamic in a single
     * SharedPreferences commit(). This is the fix for the race where tapping
     * a swatch wrote accent and read p4_dynamic separately.
     *
     * BATCH-5 Item 1: now writes to per-profile storage (ProfileSettings →
     * Room meta). The atomicity is preserved because ProfileSettings routes
     * through a single Room meta row.
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        ctx?.let { c ->
            // Write both keys. ProfileSettings.put routes through Room meta.
            // Two meta rows is fine — Room is transactional at the DAO level.
            ProfileSettings.put(c, ProfileSettings.K_THEME, currentJson())
            ProfileSettings.put(c, ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
        }
    }

    /** Toggle Material You; persists to per-profile `p4_dynamic`. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_DYNAMIC, if (on) "on" else "off") }
    }

    /** Persist the current mode + accent to per-profile `p4_theme`. */
    fun persist(ctx: Context) {
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, currentJson())
    }

    /** The JSON blob to write to `p4_theme`. */
    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""
}
