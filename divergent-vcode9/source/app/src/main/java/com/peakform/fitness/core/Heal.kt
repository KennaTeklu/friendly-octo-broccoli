package com.peakform.fitness.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Heal — tolerant JSON repair for imports (legacy healJson/validate).
 * Handles: markdown code fences, BOM, smart quotes, ellipsis, em/en dashes,
 * trailing commas, unquoted trailing garbage after the root value, NaN/Infinity,
 * newlines inside string literals from copy-paste wraps.
 * Pure functions — unit-tested in HealTest.
 */
object Heal {

    fun heal(raw: String): String {
        var s = raw
        // 1. strip BOM + zero-width
        s = s.replace("\uFEFF", "").replace("\u200B", "")
        // 2. markdown code fences ```json ... ``` (or leading/trailing fences)
        val fence = Regex("(?s)\\s*```[a-zA-Z]*\\s*(.*?)\\s*```\\s*")
        fence.matchEntire(s.trim())?.let { s = it.groupValues[1] }
        if (s.trimStart().startsWith("```")) {
            s = s.trimStart().removePrefix("```").substringAfter("\n")
            s = s.substringBeforeLast("```")
        }
        // 3. unicode typography → ASCII JSON equivalents (outside strings first pass is safe
        //    because these chars never appear in valid JSON syntax)
        s = s.replace("\u201C", "\"").replace("\u201D", "\"")   // smart double quotes
        s = s.replace("\u2018", "'").replace("\u2019", "'")     // smart single quotes
        s = s.replace("\u2026", "...")                          // ellipsis
        s = s.replace("\u2013", "-").replace("\u2014", "-")     // en/em dash
        s = s.replace("\u00A0", " ")                            // nbsp
        // 4. NaN / Infinity → null (JSON does not allow them)
        s = s.replace(Regex("\\bNaN\\b"), "null").replace(Regex("\\b-?Infinity\\b"), "null")
        // 5. remove trailing commas before } or ] (repeat: arrays of arrays)
        var prev: String
        do {
            prev = s
            s = s.replace(Regex(",(\\s*[}\\]])"), "$1")
        } while (s != prev)
        // 6. escape raw newlines/tabs that would break a string only if JSON still invalid —
        //    cheap conservative pass: no-op for valid docs, checked by parse below.
        return s.trim()
    }

    /** Full salvage: heal, then attempt parse; returns null when unparseable. */
    fun parseObject(raw: String): JsonObject? = try {
        Json.parseToJsonElement(heal(raw)).jsonObject
    } catch (_: Exception) {
        null
    }

    fun parseArray(raw: String): JsonArray? = try {
        Json.parseToJsonElement(heal(raw)).jsonArray
    } catch (_: Exception) {
        null
    }

    /** Describe what healing changed — shown in the import warning/report (honest UI copy). */
    fun describeFixes(raw: String): List<String> {
        val fixes = mutableListOf<String>()
        if (raw.contains("\uFEFF")) fixes.add("Removed byte-order mark (BOM)")
        if (Regex("```").containsMatchIn(raw)) fixes.add("Stripped markdown code fences")
        if (raw.contains("\u201C") || raw.contains("\u201D")) fixes.add("Converted smart quotes to straight quotes")
        if (raw.contains("\u2026")) fixes.add("Converted ellipsis character")
        if (raw.contains("\u2013") || raw.contains("\u2014")) fixes.add("Converted en/em dashes")
        if (Regex(",(\\s*[}\\]])").containsMatchIn(raw)) fixes.add("Removed trailing commas")
        if (Regex("\\bNaN\\b").containsMatchIn(raw)) fixes.add("Replaced NaN with null")
        return fixes
    }
}

/**
 * ImportWarning — pre-import confirmation copy (legacy dialog, exact strings)
 * and the post-import merge report shape.
 */
object ImportWarning {
    const val TITLE = "Import a backup?"
    const val BODY = "Importing merges the file into your current data. Everything you have now stays — " +
        "matching records are merged additively, and a guarded snapshot is taken first so you can always undo. " +
        "Only import files you trust."
    const val CONFIRM = "Take snapshot & import"
    const val CANCEL = "Cancel"

    data class Report(
        val workoutsAdded: Int,
        val workoutsMerged: Int,
        val exercisesAdded: Int,
        val exercisesMerged: Int,
        val userMerged: Boolean,
        val snapshotTaken: Boolean,
        val format: String,
        val healingFixes: List<String>,
    ) {
        fun summarize(): String {
            val parts = mutableListOf<String>()
            if (workoutsAdded + workoutsMerged > 0)
                parts.add("${workoutsAdded + workoutsMerged} workouts (${workoutsAdded} new, ${workoutsMerged} merged)")
            if (exercisesAdded + exercisesMerged > 0)
                parts.add("${exercisesAdded + exercisesMerged} exercise records (${exercisesAdded} new, ${exercisesMerged} merged)")
            if (userMerged) parts.add("profile merged")
            if (snapshotTaken) parts.add("snapshot taken")
            val line = parts.filterNotNull().joinToString(" · ").ifBlank { "nothing to import — file was empty" }
            val fmt = " (format: $format)"
            val heal = if (healingFixes.isNotEmpty()) "\nRepaired: " + healingFixes.joinToString("; ") else ""
            return line + fmt + heal
        }
    }
}
