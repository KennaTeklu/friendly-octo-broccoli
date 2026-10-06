package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Synonyms — legacy synonym expansion (VS Code style, 65061): typing "squat"
 * also matches "front squat", "bulgarian split squat"… Key = what the user types,
 * value = array of extra terms that should match.
 */
object Synonyms {
    private var map: Map<String, List<String>> = emptyMap()
    private var loaded = false

    fun ensure(context: Context) {
        if (loaded) return
        try {
            val obj = Json.parseToJsonElement(context.assets.open("data/synonyms.json").bufferedReader().use { it.readText() }).jsonObject
            map = obj.mapValues { (_, v) ->
                v.toString().let { Json.parseToJsonElement(it) }.let { el ->
                    (el as? kotlinx.serialization.json.JsonArray)?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull } ?: emptyList()
                }
            }
        } catch (e: Exception) {
            ProLog.e("SYN", "load failed: ${e.message}")
        }
        loaded = true
        ProLog.i("SYN", "synonym groups: ${map.size}")
    }

    /** Expand a query term into all synonym terms (legacy expandTerm). */
    fun expand(term: String): List<String> {
        val t = term.trim().lowercase()
        if (t.isEmpty()) return listOf(t)
        val out = linkedSetOf(t)
        map[t]?.let { out.addAll(it) }
        // reverse: term appears inside a synonym group's values
        for ((k, vals) in map) {
            if (vals.any { it.lowercase() == t }) { out.add(k); out.addAll(vals) }
        }
        return out.toList()
    }

    /** Score an exercise name against expanded terms (legacy fuzzy rank). */
    fun score(name: String, terms: List<String>): Int {
        val n = name.lowercase()
        var best = 0
        for (t in terms) {
            when {
                n == t -> best = maxOf(best, 100)
                n.startsWith(t) -> best = maxOf(best, 80)
                n.contains(t) -> best = maxOf(best, 60)
                else -> {
                    // subsequence fuzzy (legacy Fuse-like tolerance)
                    var ti = 0
                    for (c in n) if (ti < t.length && c == t[ti]) ti++
                    if (ti == t.length) best = maxOf(best, 30)
                }
            }
        }
        return best
    }
}
