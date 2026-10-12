package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.ProLog
import com.peakform.fitness.core.ProJson
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.Profiles
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * ThemeController — 3-mode (system / dark / light) write-through theme state.
 *
 * BATCH-6: previously a 2-mode (dark / light) controller with the mode
 * boolean stored in `p4_theme` JSON. This rewrite adds a third mode,
 * "system", which follows the OS dark-mode setting at resolve time.
 *
 * Storage layout (`p4_theme` JSON in the global ProPrefs file):
 *   {"mode": "system|dark|light", "accent": "<accentId>"}
 * `p4_dynamic` ("on"|"off") stays in the global file (Material You is a
 * device-wide setting, not per-profile).
 *
 * Write-through: every mutation (set / setAccentAndDynamic / setDynamic)
 * updates BOTH runtime state (mutableStateOf, observed by Compose) AND
 * persistent storage (ProPrefs commit) in the SAME call. No caller can
 * forget to persist — and no frame can observe state and storage out of
 * sync.
 *
 * reload(ctx): re-reads `p4_theme` + `p4_dynamic` from prefs and refreshes
 * runtime state. Used by Profiles.switchTo() so a freshly-switched profile
 * picks up its own theme (profiles share theme via the global file today;
 * the per-profile story is reserved for a future batch).
 *
 * resolveDark(ctx): returns the effective dark boolean for the current mode.
 * "system" → follows Android's night-mode UI mode. "dark" → true. "light" → false.
 *
 * Logging: every state mutation is logged under the "ProTheme" tag.
 */
object ThemeController {
    const val MODE_SYSTEM = "system"
    const val MODE_DARK = "dark"
    const val MODE_LIGHT = "light"

    /** Current mode — observed by Compose. */
    var mode by mutableStateOf(MODE_SYSTEM)
        private set

    /** Effective dark flag for the current mode — observed by Compose. */
    var dark by mutableStateOf(true)
        private set

    var accentId by mutableStateOf("blue")
        private set

    /** Material You flag — observed; defaults OFF so the user's chosen accent wins. */
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    /** Cold-start hydration: read `p4_theme` JSON. */
    fun load(initial: String?) {
        val raw = initial ?: return
        try {
            val o = ProJson.json.parseToJsonElement(raw) as? JsonObject ?: return
            val m = (o["mode"] as? JsonPrimitive)?.content
            val a = (o["accent"] as? JsonPrimitive)?.content
            if (m != null && m in setOf(MODE_SYSTEM, MODE_DARK, MODE_LIGHT)) mode = m
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
            ProLog.i("ProTheme", "load mode=$mode accent=$accentId")
        } catch (e: Exception) {
            ProLog.w("ProTheme", "load failed: ${e.message}")
        }
    }

    /** Cold-start hydration for the Material You flag. */
    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
        ProLog.i("ProTheme", "loadDynamic dynamic=$dynamic")
    }

    /**
     * Re-read theme state from persistent storage. Called by Profiles.switchTo()
     * and any other code that needs to refresh theme after a profile switch or
     * external change.
     */
    fun reload(ctx: Context) {
        val raw = ProPrefs.get(ctx, "p4_theme")
        load(raw)
        loadDynamic(ProPrefs.get(ctx, "p4_dynamic"))
        // Resolve the effective dark flag from the (possibly new) mode.
        resolveDark(ctx)
        ProLog.i("ProTheme", "reload complete — mode=$mode dark=$dark accent=$accentId dynamic=$dynamic")
    }

    /**
     * Resolve the effective dark flag for the current mode + device state.
     * "system" → follows Android's night-mode UI mode (Configuration.UI_MODE_NIGHT_YES).
     * "dark" → true. "light" → false.
     *
     * Updates the observable `dark` property.
     */
    fun resolveDark(ctx: Context): Boolean {
        val effective = when (mode) {
            MODE_DARK -> true
            MODE_LIGHT -> false
            else -> {
                val nightMode = ctx.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
        if (dark != effective) {
            dark = effective
            ProLog.i("ProTheme", "resolveDark mode=$mode → dark=$dark")
        }
        return effective
    }

    /**
     * Apply mode + accent to runtime state AND persist to `p4_theme`.
     * Write-through: state and storage update in the same call.
     */
    fun set(mode: String, accent: String, ctx: Context? = null) {
        if (mode in setOf(MODE_SYSTEM, MODE_DARK, MODE_LIGHT)) this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        ctx?.let { c ->
            ProPrefs.get(c).edit().putString("p4_theme", currentJson()).commit()
            resolveDark(c)
        }
        ProLog.i("ProTheme", "set mode=${this.mode} accent=$accentId")
    }

    /**
     * Atomic write of mode + accent + p4_dynamic in a single SharedPreferences
     * commit(). The fix for the race where tapping a swatch wrote accent and
     * read p4_dynamic separately.
     */
    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        if (mode in setOf(MODE_SYSTEM, MODE_DARK, MODE_LIGHT)) this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        ctx?.let { c ->
            ProPrefs.get(c).edit()
                .putString("p4_theme", currentJson())
                .putString("p4_dynamic", if (dynamic) "on" else "off")
                .commit()
            resolveDark(c)
        }
        ProLog.i("ProTheme", "setAccentAndDynamic mode=${this.mode} accent=$accentId dynamic=$dynamic")
    }

    /** Toggle Material You; persists to `p4_dynamic` and refreshes the runtime flag. */
    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { c ->
            ProPrefs.get(c).edit().putString("p4_dynamic", if (on) "on" else "off").commit()
        }
        ProLog.i("ProTheme", "setDynamic dynamic=$on")
    }

    /** Cycle through the 3 modes — used by the More sheet quick chip. */
    fun cycleMode(ctx: Context? = null): String {
        val next = when (mode) {
            MODE_SYSTEM -> MODE_DARK
            MODE_DARK -> MODE_LIGHT
            else -> MODE_SYSTEM
        }
        set(next, accentId, ctx)
        return next
    }

    /** Persist the current mode + accent to `p4_theme` via ProPrefs. */
    fun persist(ctx: Context) {
        ProPrefs.get(ctx).edit().putString("p4_theme", currentJson()).commit()
    }

    /** The JSON blob to write to `p4_theme`. */
    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""
}
