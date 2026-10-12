package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.core.ProfileSettings

/**
 * ThemeController — runtime mode (system/dark/light) + dark flag + accent + Material You state.
 *
 * BATCH-5A FIX: per-profile theme state now lives in per-profile SharedPreferences
 * ("p4_ls_<profileId>") via ProfileSettings, NOT in the global ProPrefs file. This
 * was the silent-drop bug —Room blocks synchronous main-thread queries in production
 * (even with `allowMainThreadQueries` only allowed in tests), so every theme write
 * from a UI callback was swallowed by the try/catch in ProfileState.put(). Now
 * SharedPreferences handles the write synchronously and safely on the main thread.
 *
 * Mode (BATCH-5A new): three values instead of two:
 *   - "system" — follows the system dark/light setting (default).
 *   - "dark"   — always dark.
 *   - "light"  — always light.
 * `dark` is derived from `mode` + the system dark state, so a single source of
 * truth drives both the persisted JSON and the live Compose recomposition.
 */
object ThemeController {
    /** Display mode: "system" | "dark" | "light". */
    var mode by mutableStateOf("system")
        private set
    /** Derived dark flag — true when mode resolves to dark (mode == "dark", or mode == "system" && system is dark). */
    var dark by mutableStateOf(true)
        private set
    var accentId by mutableStateOf("blue")
        private set
    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /**
     * Cold-start hydration: read the per-profile theme JSON. If `initial` is null
     * (fresh install or first time on this profile), default mode to "system".
     * The JSON shape is `{"mode":"system|dark|light","accent":"<id>"}`.
     */
    fun load(initial: String?) {
        if (initial == null) {
            // Fresh install: default to "system" — the user gets the OS's choice.
            mode = "system"
            return
        }
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(initial) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            if (m == "system" || m == "dark" || m == "light") mode = m
            // `dark` is not set here — it is resolved later by setSystemDark() / resolveDark()
            // once the Activity knows the actual system uiMode.
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
        } catch (_: Exception) {}
    }

    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
    }

    /**
     * Live system-dark update — call from the Compose tree whenever the system
     * uiMode changes. Only flips `dark` when mode == "system"; explicit dark/light
     * modes override the system value.
     */
    fun setSystemDark(systemDark: Boolean) {
        if (mode == "system") dark = systemDark
    }

    /**
     * Resolve `dark` from `mode` + the current system-dark state. Called at cold
     * start once the Activity's uiMode is known, so the first frame is correct.
     */
    fun resolveDark(systemDark: Boolean) {
        dark = when (mode) {
            "light" -> false
            "dark" -> true
            else -> systemDark  // "system"
        }
    }

    /**
     * Apply mode + accent to runtime state AND persist to the per-profile K_THEME
     * SharedPreferences key. Caller no longer needs to write K_THEME themselves —
     * they cannot forget.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        // Recompute `dark` from the new mode. For "system" we keep the current `dark`
        // (which the Activity keeps in sync via setSystemDark); for dark/light we
        // pin it explicitly.
        when (this.mode) {
            "light" -> dark = false
            "dark" -> dark = true
            // "system" — leave `dark` as-is; setSystemDark keeps it in sync.
        }
        if (ACCENTS.any { it.id == accent }) accentId = accent
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_THEME, currentJson()) }
    }

    /**
     * P4E-UI-05 / BATCH-5A: atomic write of accent + p4_dynamic in a single
     * SharedPreferences commit() against the active profile's prefs file. This
     * is the fix for the race where tapping a swatch wrote accent and read
     * p4_dynamic separately — Material You would win for one frame because the
     * two writes were not atomic. Now both keys land in the same per-profile
     * SharedPreferences transaction via ProfileState.prefs(ctx).edit().commit().
     *
     * Code path: ThemeController.setAccentAndDynamic() at ThemeController.kt
     *   1. Updates runtime state (mode, dark, accentId, dynamic) — Compose
     *      observes these via mutableStateOf, so recomposition fires synchronously.
     *   2. ProfileState.prefs(ctx).edit()
     *        .putString(K_THEME, currentJson())
     *        .putString(K_DYNAMIC, if (dynamic) "on" else "off")
     *        .commit()  // synchronous, atomic, per-profile
     *   Both keys land in the same SharedPreferences transaction. No frame can
     *   observe accent=X with dynamic=on (which would show Material You instead
     *   of the user's chosen accent).
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        when (this.mode) {
            "light" -> dark = false
            "dark" -> dark = true
            // "system" — leave `dark` as-is; setSystemDark keeps it in sync.
        }
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        ctx?.let { c ->
            // BATCH-5A: route through ProfileState.prefs(c) so we get the per-profile
            // SharedPreferences file ("p4_ls_<profileId>"), and commit() both keys in
            // one atomic transaction. ProfileSettings exposes get/put but not a
            // two-key atomic write, so we go straight to the underlying SharedPreferences.
            com.peakform.fitness.core.ProfileState.prefs(c).edit()
                .putString(ProfileSettings.K_THEME, currentJson())
                .putString(ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
                .commit()
        }
    }

    /** Toggle Material You; persists to the per-profile K_DYNAMIC key. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_DYNAMIC, if (on) "on" else "off") }
    }

    /** Persist the current mode + accent to the per-profile K_THEME key. */
    fun persist(ctx: Context) {
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, currentJson())
    }

    /** The JSON blob written to K_THEME. Mode is one of "system" | "dark" | "light". */
    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""
}
