package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProPrefs

/**
 * ThemeController — runtime 3-mode (system / dark / light) + accent + Material You state.
 *
 * BATCH-6: previously the controller supported only dark/light. The new
 * `mode` enum exposes a third option, "system", which follows the device's
 * dark-theme setting at composition time. The controller resolves the system
 * value once at boot and on every reload(ctx) — the runtime `dark` boolean
 * is updated synchronously so composables that read `dark` keep working.
 *
 * Write-through: every setter persists to ProPrefs (global "p4_ls" file)
 * in the SAME call that mutates runtime state. No caller can forget to
 * persist `p4_theme` / `p4_dynamic` — they go in the same SharedPreferences
 * commit as the runtime mutation.
 *
 * Logging: every state change is logged under the scope "ProTheme" so adb
 * can trace the cold-start hydration + every user-driven toggle.
 */
object ThemeController {

    enum class Mode { SYSTEM, DARK, LIGHT }

    private val MODE_TOKENS = mapOf(
        Mode.SYSTEM to "system",
        Mode.DARK to "dark",
        Mode.LIGHT to "light",
    )
    private val TOKEN_TO_MODE = MODE_TOKENS.entries.associate { (k, v) -> v to k }

    /** The persisted mode token ("system" / "dark" / "light"). */
    var modeToken by mutableStateOf("dark")
        private set

    /** Resolved dark boolean — TRUE when mode is DARK, or when mode is SYSTEM
     *  and the device is currently in dark theme. */
    var dark by mutableStateOf(true)
        private set

    var accentId by mutableStateOf("blue")
        private set
    /** Material You flag — observable; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /** Resolve the system dark-theme setting. Called once at boot and again
     *  on every reload(ctx) (e.g. profile switch — different profile may have
     *  a different effective mode). */
    fun resolveDark(ctx: Context): Boolean {
        val sysDark = when (ctx.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> false
            else -> true
        }
        val effectiveDark = when (mode()) {
            Mode.SYSTEM -> sysDark
            Mode.DARK -> true
            Mode.LIGHT -> false
        }
        if (dark != effectiveDark) {
            dark = effectiveDark
            ProLog.i("ProTheme", "resolveDark: mode=${modeToken} sysDark=$sysDark effectiveDark=$effectiveDark")
        }
        return effectiveDark
    }

    /** Set the system dark flag (host-side override hook for tests). */
    fun setSystemDark(isDark: Boolean) {
        if (mode() == Mode.SYSTEM) {
            dark = isDark
            ProLog.i("ProTheme", "setSystemDark: dark=$isDark (system mode)")
        }
    }

    fun mode(): Mode = TOKEN_TO_MODE[modeToken] ?: Mode.DARK

    /** Cold-start hydration: read `p4_theme` JSON + `p4_dynamic` once. */
    fun load(initial: String?) {
        val raw = initial ?: return
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            if (m != null && TOKEN_TO_MODE.containsKey(m)) modeToken = m
            // Backward-compat: legacy "dark"/"light" tokens map to the explicit mode.
            if (m == "dark") modeToken = "dark"
            if (m == "light") modeToken = "light"
            if (m == "system") modeToken = "system"
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
            ProLog.i("ProTheme", "load: mode=$modeToken accent=$accentId")
        } catch (e: Exception) {
            ProLog.w("ProTheme", "load failed: ${e.message}")
        }
    }

    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
        ProLog.i("ProTheme", "loadDynamic: dynamic=$dynamic")
    }

    /**
     * Reload from ProPrefs + re-resolve the system dark flag.
     * Called after a profile switch (mode + accent are global, but the system
     * dark flag may have changed since the last resolve, and re-resolving
     * guarantees the host activity is using the right scheme before the first
     * composition of the new profile's data).
     */
    fun reload(ctx: Context) {
        load(ProPrefs.get(ctx, "p4_theme"))
        loadDynamic(ProPrefs.get(ctx, "p4_dynamic"))
        resolveDark(ctx)
        ProLog.i("ProTheme", "reload: mode=$modeToken dark=$dark accent=$accentId dynamic=$dynamic")
    }

    /**
     * Apply mode + accent to runtime state AND persist to `p4_theme`.
     * The caller no longer needs to write `p4_theme` themselves — they cannot forget.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        modeToken = if (TOKEN_TO_MODE.containsKey(mode)) mode else "dark"
        if (ACCENTS.any { it.id == accent }) accentId = accent
        if (ctx != null) {
            dark = when (mode()) {
                Mode.SYSTEM -> resolveDark(ctx)
                Mode.DARK -> true
                Mode.LIGHT -> false
            }
            persist(ctx)
        } else {
            // No context — best-effort: keep existing dark if mode is dark/light,
            // leave dark untouched for system mode (caller should call resolveDark).
            dark = when (mode()) { Mode.DARK -> true; Mode.LIGHT -> false; Mode.SYSTEM -> dark }
        }
        ProLog.i("ProTheme", "set: mode=$modeToken dark=$dark accent=$accentId")
    }

    /**
     * P4E-UI-05: atomic write of accent + p4_dynamic in a single
     * SharedPreferences commit(). Writes the new accent, mode token, and
     * p4_dynamic in the SAME commit so no frame can observe accent=X with
     * dynamic=on (which would show Material You instead of the chosen accent).
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        modeToken = if (TOKEN_TO_MODE.containsKey(mode)) mode else "dark"
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        if (ctx != null) {
            dark = when (mode()) {
                Mode.SYSTEM -> resolveDark(ctx)
                Mode.DARK -> true
                Mode.LIGHT -> false
            }
            ProPrefs.get(ctx).edit()
                .putString("p4_theme", currentJson())
                .putString("p4_dynamic", if (dynamic) "on" else "off")
                .commit()
        } else {
            dark = when (mode()) { Mode.DARK -> true; Mode.LIGHT -> false; Mode.SYSTEM -> dark }
        }
        ProLog.i("ProTheme", "setAccentAndDynamic: mode=$modeToken accent=$accentId dynamic=$dynamic dark=$dark")
    }

    /** Toggle Material You; persists to `p4_dynamic` and refreshes the runtime flag. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { ProPrefs.put(it, "p4_dynamic", if (on) "on" else "off") }
        ProLog.i("ProTheme", "setDynamic: dynamic=$on")
    }

    /** Persist the current mode + accent to `p4_theme` via ProPrefs. */
    fun persist(ctx: Context) {
        ProPrefs.put(ctx, "p4_theme", currentJson())
    }

    /** The JSON blob to write to `p4_theme`. */
    fun currentJson(): String = """{"mode":"$modeToken","accent":"$accentId"}"""
}
