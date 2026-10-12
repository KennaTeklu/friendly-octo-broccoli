package com.peakform.fitness.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProJson

/**
 * ThemeController — runtime dark/light/system + accent + Material You state
 * (BATCH-5B rewrite).
 *
 * 3-mode theme (system / dark / light): the user can pick "follow system",
 * in which case the runtime `dark` flag is resolved from the current Android
 * night-mode configuration via resolveDark(ctx). The mode itself is persisted
 * as a JSON blob `p4_theme` in the active profile's SharedPreferences
 * (`p4_ls_<profileId>` via ProfileSettings.K_THEME), so theme switches with
 * the profile.
 *
 * Write-through contract (BATCH-5B root fix): every setter updates the
 * in-memory Compose state (dark / accentId / dynamic / mode) AND writes the
 * backing SharedPreferences in the SAME call. There is no separate persist()
 * step a caller can forget. This is the structural fix for the BATCH-4B race
 * where a swatch tap wrote accent but lagged p4_dynamic by one frame, so
 * Material You would win for one frame.
 *
 * reload(ctx): re-reads `p4_theme` + `p4_dynamic` from the active profile's
 * prefs. Called by Profiles.switchTo() so a profile switch is immediately
 * visible without a restart.
 *
 * resolveDark(ctx): returns the effective dark flag for "system" mode based
 * on the current Configuration. Used by the host ProTheme so system-mode
 * users see the right scheme on the very first frame.
 *
 * All log lines use the tag "ProTheme" so they group with the legacy theme
 * diagnostics.
 */
object ThemeController {
    private const val TAG = "ProTheme"

    /** Theme modes the user can pick. "system" follows the Android UI mode. */
    const val MODE_SYSTEM = "system"
    const val MODE_DARK = "dark"
    const val MODE_LIGHT = "light"

    var mode by mutableStateOf(MODE_SYSTEM)
        private set
    /** Effective dark flag (resolved from mode + system for "system"). */
    var dark by mutableStateOf(true)
        private set
    var accentId by mutableStateOf("blue")
        private set
    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /** Resolve the effective dark flag from the Android Configuration for
     *  "system" mode. Always returns true for "dark", false for "light". */
    fun resolveDark(ctx: Context): Boolean {
        val m = mode
        if (m == MODE_DARK) return true
        if (m == MODE_LIGHT) return false
        // system — read the current Configuration
        val night = ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return night == Configuration.UI_MODE_NIGHT_YES
    }

    /** Cold-start hydration: read `p4_theme` JSON. Called once at MainActivity
     *  onCreate; subsequent switches go through reload(). */
    fun load(initial: String?) {
        val raw = initial ?: return
        applyThemeJson(raw)
    }

    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
    }

    /** Re-read theme + dynamic from the active profile's prefs. Used by
     *  Profiles.switchTo() and Profiles.delete() so a profile switch is
     *  visible immediately without a restart. */
    fun reload(ctx: Context) {
        val themeRaw = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        val dynRaw = ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC)
        ProLog.i(TAG, "reload: theme=${themeRaw?.take(40) ?: "null"} dynamic=$dynRaw")
        applyThemeJson(themeRaw)
        dynamic = dynRaw == "on"
        dark = resolveDark(ctx)
        ProLog.i(TAG, "reload: mode=$mode dark=$dark accent=$accentId dynamic=$dynamic")
    }

    private fun applyThemeJson(raw: String?) {
        if (raw.isNullOrBlank()) {
            // Defaults: system mode, blue accent
            mode = MODE_SYSTEM
            accentId = "blue"
            return
        }
        try {
            val o = ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            when (m) {
                MODE_LIGHT, MODE_DARK, MODE_SYSTEM -> mode = m!!
            }
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
        } catch (e: Exception) {
            ProLog.w(TAG, "applyThemeJson failed: ${e.message}")
        }
    }

    /**
     * Apply mode + accent to runtime state AND persist to `p4_theme` in the
     * active profile's SharedPreferences (BATCH-5B write-through). The caller
     * no longer needs to write `p4_theme` themselves.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        if (mode == MODE_LIGHT || mode == MODE_DARK || mode == MODE_SYSTEM) this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        ctx?.let { c ->
            dark = resolveDark(c)
            writeThrough(c)
        }
    }

    /** P4E-UI-05: atomic write of accent + p4_dynamic in a single
     *  SharedPreferences commit() (BATCH-5B write-through). */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        if (mode == MODE_LIGHT || mode == MODE_DARK || mode == MODE_SYSTEM) this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        ctx?.let { c ->
            dark = resolveDark(c)
            writeThrough(c)
        }
    }

    /** Toggle Material You; persists `p4_dynamic` (write-through). */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { c -> ProfileSettings.put(c, ProfileSettings.K_DYNAMIC, if (on) "on" else "off") }
    }

    /** Cycle the system/dark/light mode (write-through). Returns the new mode. */
    fun cycleMode(ctx: Context? = null): String {
        val next = when (mode) {
            MODE_SYSTEM -> MODE_DARK
            MODE_DARK -> MODE_LIGHT
            else -> MODE_SYSTEM
        }
        mode = next
        ctx?.let { c ->
            dark = resolveDark(c)
            writeThrough(c)
        }
        return next
    }

    /** Persist the current mode + accent + dynamic to the active profile's
     *  SharedPreferences in a single atomic commit. */
    fun persist(ctx: Context) = writeThrough(ctx)

    private fun writeThrough(ctx: Context) {
        val sp = ProfileState.prefs(ctx)
        sp.edit()
            .putString(ProfileSettings.K_THEME, currentJson())
            .putString(ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
            .commit()
        ProLog.i(TAG, "writeThrough: $mode dark=$dark accent=$accentId dynamic=$dynamic")
    }

    /** The JSON blob to write to `p4_theme`. */
    fun currentJson(): String =
        """{"mode":"$mode","accent":"$accentId"}"""
}
