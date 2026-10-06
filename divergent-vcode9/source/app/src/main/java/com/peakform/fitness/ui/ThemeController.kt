package com.peakform.fitness.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * ThemeController — runtime dark/light + accent state, persisted in the legacy
 * localStorage-mirror key "p4_theme" {"mode","accent"} (default dark/blue).
 */
object ThemeController {
    var dark by mutableStateOf(true)
        private set
    var accentId by mutableStateOf("blue")
        private set

    fun accent(): AccentDef = ACCENTS.firstOrNull { it.id == accentId } ?: ACCENTS[0]

    fun load(initial: String?) {
        val raw = initial ?: return
        try {
            val o = com.peakform.fitness.core.ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return
            val m = (o["mode"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            val a = (o["accent"] as? kotlinx.serialization.json.JsonPrimitive)?.content
            if (m == "light") dark = false
            if (m == "dark") dark = true
            if (a != null && ACCENTS.any { it.id == a }) accentId = a
        } catch (_: Exception) {}
    }

    fun set(mode: String, accent: String) {
        dark = mode != "light"
        if (ACCENTS.any { it.id == accent }) accentId = accent
    }
}
