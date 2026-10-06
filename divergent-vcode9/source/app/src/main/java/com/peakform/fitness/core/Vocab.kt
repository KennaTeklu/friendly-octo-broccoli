package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Vocab — reading-level system (legacy P4.Vocab + P4_VOCAB_LADDERS).
 * 187 ladder keys × exactly 10 level variants (L5 = authored default).
 * Level stored in prefs "p4_vocab" (1..10, default 5). Backup key: vocabLevel.
 * Bands for quotes/tips: 1 = L1–3, 2 = L4–6, 3 = L7–10.
 */
object Vocab {
    data class Level(val id: Int, val name: String, val desc: String)

    val LEVELS = listOf(
        Level(1, "Super Simple", "Very short words. Nothing fancy."),
        Level(2, "Easy Going", "Simple words, friendly sentences."),
        Level(3, "Clear", "Plain English with a bit more detail."),
        Level(4, "Standard", "The way fitness apps usually talk."),
        Level(5, "Balanced", "Everyday coaching language."),
        Level(6, "Polished", "Smoother phrasing, richer words."),
        Level(7, "Articulate", "Expressive coaching with nuance."),
        Level(8, "Elevated", "Refined vocabulary, formal touches."),
        Level(9, "Erudite", "Learned, precise, a little grand."),
        Level(10, "Grandiloquent", "Maximum pomp. Maximum circumstance."),
    )

    private var ladders: Map<String, List<String>> = emptyMap()
    private var loaded = false
    private var ctxRef: Context? = null

    fun ensure(context: Context) {
        if (loaded && ctxRef != null) return
        ctxRef = context.applicationContext
        try {
            val raw = context.assets.open("data/vocab_ladders.json").bufferedReader().use { it.readText() }
            val obj = Json.parseToJsonElement(raw).jsonObject
            ladders = obj.mapValues { (_, v) -> v.jsonArray.map { it.jsonPrimitive.content } }
        } catch (e: Exception) {
            ProLog.e("VOCAB", "ladder load failed: ${e.message}")
            ladders = emptyMap()
        }
        loaded = true
        ProLog.i("VOCAB", "ladders loaded: ${ladders.size} keys")
    }

    fun level(context: Context): Int {
        val v = ProPrefs.get(context, "p4_vocab")?.toIntOrNull() ?: 5
        return v.coerceIn(1, 10)
    }

    fun setLevel(context: Context, level: Int) {
        ProPrefs.put(context, "p4_vocab", level.coerceIn(1, 10).toString())
    }

    fun band(level: Int = -1): Int {
        val l = if (level > 0) level else 5
        return when {
            l <= 3 -> 1
            l <= 6 -> 2
            else -> 3
        }
    }

    /** Legacy P4.Vocab.t(key): variant at the current level (index 4 = L5 default). */
    fun t(context: Context?, key: String): String {
        val variants = ladders[key] ?: return key
        val lvl = if (context != null) level(context) else 5
        return variants.getOrNull(lvl - 1) ?: variants[4]
    }

    fun variant(key: String, level: Int): String {
        val variants = ladders[key] ?: return key
        return variants.getOrNull((level - 1).coerceIn(0, 9)) ?: variants[4]
    }

    fun keys(): List<String> = ladders.keys.sorted()

    fun count(): Int = ladders.size

    /** Legacy phrasebook JSON: {kind:'try4ever-fitness-phrasebook', levels{1..10}, entries:[{key, phrase}]} */
    fun exportPhrasebook(context: Context): String {
        val level = level(context)
        val entries = ladders.keys.sorted().map { k ->
            JsonObject(mapOf(
                "key" to JsonPrimitive(k),
                "phrase" to JsonPrimitive(variant(k, level)),
            ))
        }
        val payload = JsonObject(mapOf(
            "kind" to JsonPrimitive("try4ever-fitness-phrasebook"),
            "levels" to JsonObject((1..10).associate { it.toString() to JsonPrimitive(it == level) }),
            "entries" to JsonArray(entries),
            "_exportedAt" to JsonPrimitive(ProState.nowIso()),
        ))
        return ProJson.json.encodeToString(JsonObject.serializer(), payload)
    }

    /** Import: accepts length-10 arrays per entry (legacy rule). Adds custom overrides only. */
    fun importPhrasebook(context: Context, raw: String): Pair<Int, Int> {
        var accepted = 0
        var rejected = 0
        try {
            val obj = Json.parseToJsonElement(Heal.heal(raw)).jsonObject
            val kind = obj["kind"]?.jsonPrimitive?.contentOrNull
            if (kind != "try4ever-fitness-phrasebook") return 0 to 1
            val entries = obj["entries"]?.jsonArray ?: return 0 to 1
            val customs = mutableMapOf<String, String>()
            for (e in entries) {
                val o = e.jsonObject
                val key = o["key"]?.jsonPrimitive?.contentOrNull ?: continue
                val phrase = o["phrase"]?.jsonPrimitive?.contentOrNull ?: continue
                val lvl = level(context)
                val valid = ladders[key]?.size == 10
                if (valid && phrase.isNotBlank()) {
                    customs[key] = phrase; accepted++
                } else rejected++
            }
            if (customs.isNotEmpty()) {
                ProPrefs.put(context, "p4_phrases_custom", ProJson.json.encodeToString(
                    JsonObject.serializer(), JsonObject(customs.mapValues { JsonPrimitive(it.value) })))
            }
        } catch (e: Exception) {
            return 0 to 1
        }
        return accepted to rejected
    }
}

/**
 * Consent — legal consent log (legacy P4.Legal.record).
 * Array newest-first, capped at 200. Record: {at, kind, detail, version}.
 */
object Consent {
    const val DOC_VERSION = "2026.10-native"

    fun record(context: Context, kind: String, detail: String) {
        val log = read(context).toMutableList()
        log.add(0, JsonObject(mapOf(
            "at" to JsonPrimitive(System.currentTimeMillis()),
            "kind" to JsonPrimitive(kind),
            "detail" to JsonPrimitive(detail),
            "version" to JsonPrimitive(DOC_VERSION),
        )))
        val capped = log.take(200)
        val arr = JsonArray(capped)
        ProPrefs.put(context, "p4_consent_log", ProJson.json.encodeToString(JsonArray.serializer(), arr))
    }

    fun read(context: Context): List<JsonObject> {
        return try {
            val raw = ProPrefs.get(context, "p4_consent_log") ?: return emptyList()
            Json.parseToJsonElement(raw).jsonArray.mapNotNull { it as? JsonObject }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun exportJson(context: Context): String {
        val payload = JsonObject(mapOf(
            "kind" to JsonPrimitive("ConsentLog"),
            "records" to JsonArray(read(context)),
            "_exportedAt" to JsonPrimitive(ProState.nowIso()),
        ))
        return ProJson.json.encodeToString(JsonObject.serializer(), payload)
    }

    fun hasAccepted(context: Context, kind: String): Boolean =
        read(context).any { it["kind"]?.jsonPrimitive?.contentOrNull == kind }
}
