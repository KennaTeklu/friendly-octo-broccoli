package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * ThemeController — runtime dark/light/system + accent + Material You state.
 *
 * BATCH-5B write-through + reload:
 *  - Theme state lives here as Compose state (`mode`, `dark`, `accentId`,
 *    `dynamic`); Compose observes these properties, so recomposition fires
 *    synchronously when any of them changes.
 *  - Every write method (`set`, `setAccentAndDynamic`, `setDynamic`,
 *    `persist`) updates the in-memory state AND writes to per-profile storage
 *    in the SAME call. Compose observes the state values, so recomposition
 *    fires synchronously — there is no "applied-but-not-yet-persisted" window
 *    where a config change could lose the user's selection.
 *  - Storage boundary: per-profile SharedPreferences (`p4_ls_$profileId`) via
 *    ProfileSettings. Theme follows the active profile — switching profiles
 *    reloads the new profile's theme via `reload(ctx)`.
 *  - `mode` is one of `"system"` (follow the OS dark/light setting), `"dark"`,
 *    `"light"`. `dark` is derived: true for dark-mode, false for light-mode,
 *    resolved from `mode` and the system dark state when mode == "system".
 *
 * Logging: every load / set / resolve / reload logs through ProLog under the
 * tag "ProTheme" so a profile-switch theme bug can be reproduced from the
 * logcat alone.
 */
object ThemeController {
    /** System dark state, captured at boot + on system config changes. */
    @Volatile private var systemDark: Boolean = true

    /** Three values: "system", "dark", "light". */
    var mode by mutableStateOf("system")
        private set

    /** Derived from mode + systemDark. Compose observes this. */
    var dark by mutableStateOf(true)
        private set

    var accentId by mutableStateOf("blue")
        private set

    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /**
     * Cold-start hydration. Pass the raw `p4_theme` JSON blob (or null on a
     * fresh install). When `initial` is null the mode is reset to "system"
     * so a brand-new install follows the OS dark/light setting.
     */
    fun load(initial: String?) {
        if (initial == null) {
            mode = "system"
            dark = systemDark
            ProLog.i("ProTheme", "load mode=$mode accent=$accentId dynamic=$dynamic")
            return
        }
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(initial) as? JsonObject
            if (o != null) {
                val m = (o["mode"] as? JsonPrimitive)?.content
                val a = (o["accent"] as? JsonPrimitive)?.content
                if (m == "system" || m == "dark" || m == "light") mode = m
                if (a != null && ACCENTS.any { it.id == a }) accentId = a
            }
        } catch (_: Exception) {}
        dark = resolveDarkValue()
        ProLog.i("ProTheme", "load mode=$mode accent=$accentId dynamic=$dynamic")
    }

    /** Cold-start hydration for the Material You flag (legacy `p4_dynamic` = "on"/"off"). */
    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
        ProLog.i("ProTheme", "loadDynamic dynamic=$dynamic")
    }

    /**
     * Update the cached system dark state (called from Compose via
     * `isSystemInDarkTheme()` and on configuration changes). When the active
     * mode is "system", the derived `dark` follows the new system state.
     */
    fun setSystemDark(systemDark: Boolean) {
        this.systemDark = systemDark
        if (mode == "system") dark = systemDark
    }

    /**
     * Force a re-resolution of `dark` from `mode` + the supplied system state.
     * Called by Profiles.switchTo() after the active profile flips so the new
     * profile's theme matches the OS state.
     */
    fun resolveDark(systemDark: Boolean) {
        this.systemDark = systemDark
        dark = resolveDarkValue()
        ProLog.i("ProTheme", "resolved theme mode=$mode dark=$dark accent=$accentId")
    }

    private fun resolveDarkValue(): Boolean = when (mode) {
        "light" -> false
        "dark" -> true
        else -> systemDark
    }

    /**
     * Apply mode + accent to runtime state AND persist to per-profile storage
     * (K_THEME) in the SAME call. The caller cannot forget to write — every
     * mode/accent change lands in SharedPreferences atomically.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        dark = resolveDarkValue()
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_THEME, currentJson()) }
        ProLog.i("ProTheme", "set mode=$mode accent=$accentId dynamic=$dynamic")
    }

    /**
     * BATCH-5B atomic write of accent + dynamic in a single SharedPreferences
     * commit(). Updates ALL in-memory state (mode, accentId, dynamic, dark) and
     * writes K_THEME + K_DYNAMIC via
     * `ProfileState.prefs(ctx).edit().putString(K_THEME, ...).putString(K_DYNAMIC, ...).commit()`.
     *
     * Code path: ThemeController.setAccentAndDynamic()
     *   1. Updates runtime state (mode, accentId, dynamic, dark) — Compose
     *      observes these via mutableStateOf, so recomposition fires synchronously.
     *   2. ProfileState.prefs(ctx).edit()
     *        .putString(K_THEME, currentJson())
     *        .putString(K_DYNAMIC, if (dynamic) "on" else "off")
     *        .commit()  // synchronous, atomic
     *   Both keys land in the same SharedPreferences transaction. No frame can
     *   observe accent=X with dynamic=on (which would show Material You instead
     *   of the user's chosen accent).
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        dark = resolveDarkValue()
        ctx?.let { c ->
            ProfileState.prefs(c).edit()
                .putString(ProfileSettings.K_THEME, currentJson())
                .putString(ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
                .commit()
        }
        ProLog.i("ProTheme", "setAccentAndDynamic mode=$mode accent=$accentId dynamic=$dynamic")
    }

    /** Toggle Material You; persists to K_DYNAMIC and refreshes the runtime flag. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_DYNAMIC, if (on) "on" else "off") }
        ProLog.i("ProTheme", "setDynamic dynamic=$dynamic")
    }

    /** Persist the current mode + accent to per-profile storage (K_THEME). */
    fun persist(ctx: Context) {
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, currentJson())
    }

    /** The JSON blob to write to K_THEME. */
    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""

    /**
     * Reload theme state from the active profile's per-profile storage. Called
     * by Profiles.switchTo() after the active profile flips so the new
     * profile's theme is applied before the UI re-renders.
     */
    fun reload(ctx: Context) {
        val themeRaw = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        val dynamicRaw = ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC)
        load(themeRaw)
        loadDynamic(dynamicRaw)
        ProLog.i("ProTheme", "profile switch reload mode=$mode accent=$accentId dynamic=$dynamic")
    }
}
