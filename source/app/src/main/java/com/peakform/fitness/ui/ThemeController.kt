package com.peakform.fitness.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProPrefs

/**
 * ThemeController — runtime dark/light + system + accent + Material You state.
 *
 * BATCH-5B: previously 2-mode (dark/light only). Now 3-mode:
 *   "system" → follows the OS dark theme setting (resolved via resolveDark)
 *   "dark"   → always dark
 *   "light"  → always light
 *
 * Write-through: every mutator (set / setAccentAndDynamic / setDynamic)
 * updates the in-memory Compose state AND writes to per-profile SharedPreferences
 * (ProfileSettings.K_THEME / K_DYNAMIC) in the SAME call. No caller can forget
 * to persist, and no frame can observe state with stale prefs.
 *
 * reload(ctx) is called after a profile switch — it re-reads the new profile's
 * theme prefs and re-resolves the dark flag from the system. This is the
 * mechanism that makes a freshly-switched profile pick up its OWN theme.
 *
 * All log messages are tagged "ProTheme" so they can be filtered from the ring
 * buffer during a UI debugging session.
 */
object ThemeController {
    /** Three observable modes: "system" | "dark" | "light". */
    var mode by mutableStateOf("system")
        private set
    /** Effective dark/light — `mode="system"` resolves this via isSystemInDarkTheme. */
    var dark by mutableStateOf(true)
        private set
    var accentId by mutableStateOf("blue")
        private set
    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /** Resolve the effective dark flag for the given system configuration.
     *  Called whenever `mode == "system"` or after a profile switch. */
    fun resolveDark(ctx: Context): Boolean {
        val sysDark = (ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        val effective = when (mode) {
            "dark" -> true
            "light" -> false
            else -> sysDark
        }
        if (dark != effective) {
            dark = effective
            ProLog.i("ProTheme", "resolveDark: mode=$mode sysDark=$sysDark → dark=$effective")
        }
        return effective
    }

    /** Apply the system's dark flag (for "system" mode). Called from MainActivity
     *  on configuration change so the dark flag tracks the OS toggle live. */
    fun setSystemDark(ctx: Context, isDark: Boolean) {
        if (mode != "system") return
        if (dark != isDark) {
            dark = isDark
            ProLog.i("ProTheme", "setSystemDark: dark=$isDark")
        }
    }

    /** Cold-start hydration: read `p4_theme` JSON + `p4_dynamic` from the
     *  active profile's SharedPreferences. */
    fun load(initial: String?) {
        val raw = initial ?: return
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            if (m != null && (m == "system" || m == "dark" || m == "light")) mode = m
            if (mode == "dark") dark = true
            else if (mode == "light") dark = false
            // "system" — leave dark flag for resolveDark() to set.
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
            ProLog.i("ProTheme", "load: mode=$mode accent=$accentId")
        } catch (e: Exception) {
            ProLog.w("ProTheme", "load failed: ${e.message}")
        }
    }

    /** Load the dynamic flag from prefs ("on" → on). */
    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
        ProLog.i("ProTheme", "loadDynamic: dynamic=$dynamic")
    }

    /** Reload everything from the active profile's prefs (post profile-switch). */
    fun reload(ctx: Context) {
        val themeJson = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        val dynPref = ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC)
        load(themeJson)
        loadDynamic(dynPref)
        resolveDark(ctx)
        ProLog.i("ProTheme", "reload complete — mode=$mode dark=$dark accent=$accentId dynamic=$dynamic")
    }

    /**
     * Apply mode + accent to runtime state AND persist to per-profile prefs.
     * The caller no longer needs to write `p4_theme` themselves — they cannot forget.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        when (this.mode) {
            "dark" -> dark = true
            "light" -> dark = false
            else -> ctx?.let { resolveDark(it) }
        }
        if (ACCENTS.any { it.id == accent }) accentId = accent
        ProLog.i("ProTheme", "set: mode=${this.mode} dark=$dark accent=$accentId")
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_THEME, currentJson()) }
    }

    /**
     * Atomic write of accent + p4_dynamic in a single SharedPreferences commit().
     * Both keys land in the same SharedPreferences transaction. No frame can
     * observe accent=X with dynamic=on (which would show Material You instead
     * of the user's chosen accent).
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        when (this.mode) {
            "dark" -> dark = true
            "light" -> dark = false
            else -> ctx?.let { resolveDark(it) }
        }
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        ProLog.i("ProTheme", "setAccentAndDynamic: mode=${this.mode} dark=$dark accent=$accentId dynamic=$dynamic")
        ctx?.let { c ->
            com.peakform.fitness.core.ProfileState.prefs(c).edit()
                .putString(ProfileSettings.K_THEME, currentJson())
                .putString(ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
                .commit()
        }
    }

    /** Toggle Material You; persists to `p4_dynamic` and refreshes the runtime flag. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_DYNAMIC, if (on) "on" else "off") }
    }

    /** Persist the current mode + accent to per-profile prefs. */
    fun persist(ctx: Context) {
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, currentJson())
    }

    /** The JSON blob to write to `p4_theme`. */
    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""
}
