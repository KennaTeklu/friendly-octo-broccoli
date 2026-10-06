package com.peakform.fitness.core

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * JsonHeal — native port of the legacy SEC.healJson (PF/index.html L55091–55150).
 * Tolerates exactly the malformations the legacy app tolerated, no more, no less:
 *   1. markdown code fences (```json … ```) stripped
 *   2. trailing commas before ] or } removed
 *   3. curly/smart double quotes U+201C/U+201D → " and smart singles U+2018/U+2019 → '
 *   4. ellipsis character … → ...
 *   5. {exercises:[...]} wrapper accepted as the array itself
 *   6. exercise field aliases: Name/title → name, category → group, primaryMuscles → muscles
 * NOT tolerated (matching legacy): BOM, NaN, em-dash keys, ASCII single-quoted keys are left
 * to the strict parser (isLenient already handles unquoted keys in kotlinx-serialization).
 */
object JsonHeal {

    /** Heal an arbitrary pasted JSON string; returns the healed text. */
    fun heal(raw: String): String {
        var s = raw
        // 3. smart quotes → ASCII (before fence stripping; a fence may carry them)
        s = s.replace('\u201C', '"').replace('\u201D', '"')
        s = s.replace('\u2018', '\'').replace('\u2019', '\'')
        // 4. ellipsis
        s = s.replace("\u2026", "...")
        // 1. markdown code fences
        s = stripFences(s)
        // 2. trailing commas
        s = stripTrailingCommas(s)
        return s.trim()
    }

    fun stripFences(s: String): String {
        val t = s.trim()
        // ```json\n...\n``` or ```\n...\n``` (3+ backticks tolerated on the fence lines)
        val fenced = Regex("^`{3,}[a-zA-Z]*\\s*([\\s\\S]*?)\\s*`{3,}$").find(t)
        if (fenced != null) return fenced.groupValues[1].trim()
        // stray leading fence line without a closing fence (truncated paste)
        if (t.startsWith("```")) {
            val firstBreak = t.indexOf('\n')
            if (firstBreak > 0) return t.substring(firstBreak + 1).trim()
        }
        return t
    }

    fun stripTrailingCommas(s: String): String {
        val sb = StringBuilder(s.length)
        var inStr = false
        var quote = ' '
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (inStr) {
                sb.append(c)
                if (c == '\\' && i + 1 < s.length) {
                    sb.append(s[i + 1]); i += 2; continue
                }
                if (c == quote) inStr = false
                i++; continue
            }
            when (c) {
                '"', '\'' -> { inStr = true; quote = c; sb.append(c) }
                ',' -> {
                    // remove when followed only by whitespace and a closing bracket (string-aware)
                    var j = i + 1
                    while (j < s.length && s[j].isWhitespace()) j++
                    if (j < s.length && (s[j] == '}' || s[j] == ']')) {
                        // skip the comma entirely
                    } else {
                        sb.append(c)
                    }
                }
                else -> sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    /** Parse healed JSON into a JsonElement, applying the legacy wrapper sniffing. */
    fun parseHealed(raw: String): JsonElement? {
        val healed = heal(raw)
        return try {
            ProJson.json.parseToJsonElement(healed)
        } catch (_: Exception) {
            null
        }
    }

    /** Legacy exercise payload aliases (SEC.validateExercisePayload normalization). */
    fun normalizeExerciseFields(el: JsonElement): JsonObject {
        if (el !is JsonObject) return el as? JsonObject ?: JsonObject(emptyMap())
        val map = el.toMutableMap()
        fun alias(target: String, vararg sources: String) {
            if (map.containsKey(target)) return
            for (srcKey in sources) {
                val v = map[srcKey]
                if (v is JsonPrimitive && v.content.isNotBlank()) {
                    map[target] = JsonPrimitive(v.content)
                    break
                } else if (v != null && v !is JsonNull) {
                    map[target] = v
                    break
                }
            }
        }
        alias("name", "Name", "title")
        alias("group", "category")
        alias("muscles", "primaryMuscles")
        return JsonObject(map)
    }

    /**
     * Accept an exercise payload that is either a bare array, an {exercises:[...]} wrapper,
     * or an {items:[...]} wrapper — legacy Studio import sniffing (L55111–55113).
     */
    fun unwrapExerciseArray(el: JsonElement): JsonArray? = when {
        el is JsonArray -> el
        el is JsonObject && el["exercises"] is JsonArray -> el["exercises"] as JsonArray
        el is JsonObject && el["items"] is JsonArray -> el["items"] as JsonArray
        else -> null
    }
}
