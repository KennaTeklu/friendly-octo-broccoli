package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProState
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

/**
 * ThemeController — runtime dark/light + accent + Material You state.
 *
 * Batch 5B rewrite — three-mode (system / dark / light), write-through,
 * reloadable. Every `set` / `setDynamic` updates in-memory Compose state AND
 * writes the per-profile storage in the same call, so no caller can forget
 * to persist.
 *
 * Modes:
 *   "system" → dark = resolveDark(systemDark)
 *   "dark"   → dark = true
 *   "light"  → dark = false
 *
 * `reload(ctx)` re-reads `p4_theme` + `p4_dynamic` from the active profile's
 * per-profile SharedPreferences (ProfileSettings). It is called after every
 * profile switch so a switch from profile A (dark) to profile B (light)
 * re-applies B's theme atomically before the next frame.
 *
 * `setSystemDark(systemDark)` is called from the Compose tree
 * (via `isSystemInDarkTheme()`) so the controller knows the latest system
 * theme state. When mode == "system", dark recomputes against this value.
 *
 * All theme logging uses tag "ProTheme".
 */
object ThemeController {
    /** Three-mode state: "system" | "dark" | "light". */
    var mode by mutableStateOf("system")
        private set

    /** Resolved dark flag — observed by Compose via mutableStateOf. When mode
     *  is "system", this is `resolveDark(systemDark)`. */
    var dark by mutableStateOf(true)
        private set

    var accentId by mutableStateOf("blue")
        private set

    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    /** Latest known system dark state, supplied by the Compose tree. */
    @Volatile private var systemDark: Boolean = true

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /** Resolve dark from the active mode + the latest system dark value. */
    fun resolveDark(systemDark: Boolean): Boolean = when (mode) {
        "light" -> false
        "dark" -> true
        else -> systemDark
    }

    /** Called from the Compose tree (via isSystemInDarkTheme()) to push the
     *  latest system dark state. When mode == "system", the controller recomputes
     *  `dark` and logs the change at INFO level. */
    fun setSystemDark(systemDark: Boolean) {
        val prev = this.systemDark
        this.systemDark = systemDark
        if (mode == "system" && prev != systemDark) {
            dark = systemDark
            ProLog.i("ProTheme", "system dark changed → dark=$dark")
        }
    }

    /** Cold-start hydration: read `p4_theme` JSON + `p4_dynamic` once.
     *  Kept for backward compatibility — callers should prefer reload(ctx). */
    fun load(initial: String?) {
        val raw = initial ?: return
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as? JsonObject ?: return
            val m = (o["mode"] as? JsonPrimitive)?.content
            val a = (o["accent"] as? JsonPrimitive)?.content
            if (m == "light" || m == "dark" || m == "system") mode = m
            if (m == "light") dark = false
            else if (m == "dark") dark = true
            else dark = systemDark
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
        } catch (e: Exception) {
            ProLog.w("ProTheme", "load failed: ${e.message}")
        }
    }

    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
    }

    /**
     * Batch 5B reload: re-read p4_theme + p4_dynamic from the active profile's
     * per-profile SharedPreferences (ProfileSettings), then resolve dark
     * against the current system dark value.
     *
     * Called from MainActivity.onCreate (after migrateFromPrefs) and from
     * Profiles.switchTo (after loadAll, before notifyChanged).
     */
    fun reload(ctx: Context) {
        val themeRaw = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        val dynRaw = ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC)
        load(themeRaw)
        loadDynamic(dynRaw)
        dark = resolveDark(systemDark)
        ProLog.i("ProTheme", "reload: mode=$mode accent=$accentId dynamic=$dynamic dark=$dark")
    }

    /**
     * Apply mode + accent to runtime state AND persist to `p4_theme` via
     * ProfileSettings. Caller does not need to write `p4_theme` themselves.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        val newMode = if (mode == "light" || mode == "dark" || mode == "system") mode else "system"
        this.mode = newMode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        dark = resolveDark(systemDark)
        ctx?.let {
            ProfileSettings.put(it, ProfileSettings.K_THEME, currentJson())
        }
        ProLog.i("ProTheme", "set: mode=$newMode accent=$accent dark=$dark")
    }

    /**
     * Batch 4E / 5B: atomic write of accent + p4_dynamic in a single
     * SharedPreferences commit(). The fix for the race where tapping
     * a swatch wrote accent and read p4_dynamic separately — Material You
     * would win for one frame because the two writes were not atomic.
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        val newMode = if (mode == "light" || mode == "dark" || mode == "system") mode else "system"
        this.mode = newMode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        dark = resolveDark(systemDark)
        ctx?.let { c ->
            // Atomic single commit() so no frame can observe accent=X with dynamic=on
            // (which would show Material You instead of the user's chosen accent).
            ProfileState.prefs(c).edit()
                .putString(ProfileSettings.K_THEME, currentJson())
                .putString(ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
                .commit()
        }
        ProLog.i("ProTheme", "setAccentAndDynamic: mode=$newMode accent=$accent dynamic=$dynamic")
    }

    /** Toggle Material You; persists to `p4_dynamic` and refreshes the runtime flag. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_DYNAMIC, if (on) "on" else "off") }
        ProLog.i("ProTheme", "setDynamic: $on")
    }

    /** Persist the current mode + accent to `p4_theme` via ProfileSettings. */
    fun persist(ctx: Context) {
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, currentJson())
    }

    /** The JSON blob to write to `p4_theme`. */
    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""
}
