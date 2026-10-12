package com.peakform.fitness.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.ProLog

object ThemeController {
    var mode by mutableStateOf("system")
        private set
    var dark by mutableStateOf(true)
        private set
    var accentId by mutableStateOf("blue")
        private set
    var dynamic by mutableStateOf(false)
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    fun load(initial: String?) {
        val raw = initial
        if (raw == null) {
            // 5A+5B+6+7: profile switch — when the active profile has no saved
            // theme yet, reset to defaults so a theme set on profile A doesn't
            // leak across into a fresh profile B.
            mode = "system"
            accentId = "blue"
            ProLog.i("ProTheme", "load mode=$mode accent=$accentId dynamic=$dynamic (null initial — reset to defaults)")
            return
        }
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            if (m == "system" || m == "dark" || m == "light") mode = m
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
        } catch (_: Exception) {}
        ProLog.i("ProTheme", "load mode=$mode accent=$accentId dynamic=$dynamic")
    }

    fun loadDynamic(initial: String?) {
        dynamic = initial == "on"
        ProLog.i("ProTheme", "loadDynamic dynamic=$dynamic")
    }

    fun setSystemDark(systemDark: Boolean) {
        if (mode == "system") dark = systemDark
    }

    fun resolveDark(systemDark: Boolean) {
        dark = when (mode) {
            "light" -> false
            "dark" -> true
            else -> systemDark
        }
        ProLog.i("ProTheme", "resolved theme mode=$mode dark=$dark accent=$accentId")
    }

    fun set(mode: String, accent: String, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        // Resolve dark immediately so a tap re-colors the app in the same frame.
        // For system mode we cannot evaluate systemDark here without a Context —
        // the caller can call resolveDark(systemDark) if needed (MainActivity does).
        when (this.mode) {
            "light" -> dark = false
            "dark" -> dark = true
            // "system" — caller invokes setSystemDark/resolveDark via MainActivity.
        }
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_THEME, currentJson()) }
        ProLog.i("ProTheme", "set mode=$mode accent=$accentId dynamic=$dynamic")
    }

    fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
        if (mode == "system" || mode == "dark" || mode == "light") this.mode = mode
        if (ACCENTS.any { it.id == accent }) accentId = accent
        this.dynamic = dynamic
        when (this.mode) {
            "light" -> dark = false
            "dark" -> dark = true
            // "system" — caller invokes setSystemDark/resolveDark via MainActivity.
        }
        ctx?.let { c ->
            ProfileState.prefs(c).edit()
                .putString(ProfileSettings.K_THEME, currentJson())
                .putString(ProfileSettings.K_DYNAMIC, if (dynamic) "on" else "off")
                .commit()
        }
        ProLog.i("ProTheme", "setAccentAndDynamic mode=$mode accent=$accentId dynamic=$dynamic")
    }

    fun setDynamic(on: Boolean, ctx: Context? = null) {
        dynamic = on
        ctx?.let { ProfileSettings.put(it, ProfileSettings.K_DYNAMIC, if (on) "on" else "off") }
        ProLog.i("ProTheme", "setDynamic dynamic=$dynamic")
    }

    fun persist(ctx: Context) {
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, currentJson())
    }

    fun currentJson(): String = """{"mode":"$mode","accent":"$accentId"}"""

    fun reload(ctx: Context) {
        val theme = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        val dyn = ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC)
        load(theme)
        loadDynamic(dyn)
        ProLog.i("ProTheme", "profile switch reload mode=$mode accent=$accentId dynamic=$dynamic")
    }
}
