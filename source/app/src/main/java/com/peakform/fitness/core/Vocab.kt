package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/**
 * Vocab — reading-level engine (PF L55343–55535). Levels L1–L10 with the legacy names;
 * t() resolves an exact registered phrase at the current level (L5 = authored default).
 * Native scope: exact-phrase lookup + label substitution for core UI strings — the DOM
 * tree-walk of the web app has no equivalent (and no need) in Compose.
 */
object Vocab {
    data class Level(val id: Int, val name: String)

    val LEVELS = listOf(
        Level(1, "Super Simple"), Level(2, "Easy"), Level(3, "Everyday"), Level(4, "Clear"),
        Level(5, "Standard"), Level(6, "Sharp"), Level(7, "Advanced"), Level(8, "Technical"),
        Level(9, "Pro Coaching"), Level(10, "Grandiloquent"),
    )

    const val PREF_KEY = "p4_vocab"
    private var ladders: Map<String, List<String>> = emptyMap()

    fun init(ctx: Context) {
        if (ladders.isNotEmpty()) return
        ladders = try {
            val raw = ctx.assets.open("data/vocab_ladders.json").bufferedReader().use { it.readText() }
            val obj = ProJson.json.parseToJsonElement(raw).jsonObject
            obj.mapValues { (_, v) ->
                v.jsonArray.map { (it as JsonPrimitive).content }
            }
        } catch (e: Exception) {
            ProLog.e("VOCAB", "ladder load failed: ${e.message}")
            emptyMap()
        }
        ProLog.i("VOCAB", "ladders loaded: ${ladders.size} keys")
    }

    fun currentLevel(ctx: Context): Int = ProPrefs.get(ctx, PREF_KEY)?.toIntOrNull()?.coerceIn(1, 10) ?: 5

    fun setLevel(ctx: Context, level: Int) {
        ProPrefs.put(ctx, PREF_KEY, level.coerceIn(1, 10).toString())
    }

    /** Legacy invariant: L5 always equals the authored default (no-op level). */
    fun t(ctx: Context, key: String): String {
        val level = currentLevel(ctx)
        val ladder = ladders[key] ?: return key
        return ladder.getOrNull(level - 1) ?: key
    }

    /** Word list entry for the glossary overlay: word → replacement at the current level. */
    fun glossaryEntries(ctx: Context): List<Pair<String, String>> {
        val level = currentLevel(ctx)
        return ladders.mapNotNull { (key, variants) ->
            val rep = variants.getOrNull(level - 1) ?: return@mapNotNull null
            if (rep != key) key to rep else null
        }.sortedBy { it.first }
    }

    fun exportPhrasebook(ctx: Context): String {
        val level = currentLevel(ctx)
        val obj = JsonObject(ladders.mapValues { (_, v) -> JsonPrimitive(v.getOrNull(level - 1) ?: "") })
        return ProJson.pretty.encodeToString(JsonObject.serializer(), obj)
    }

    /** Phrasebook import merges community translations INTO the ladders for the current level. */
    fun importPhrasebook(ctx: Context, raw: String): Int {
        val level = currentLevel(ctx)
        return try {
            val obj = ProJson.json.parseToJsonElement(JsonHeal.heal(raw)).jsonObject
            var applied = 0
            val updated = ladders.toMutableMap()
            obj.forEach { (key, value) ->
                val text = (value as? JsonPrimitive)?.contentOrNull ?: return@forEach
                val ladder = updated[key]?.toMutableList() ?: MutableList(10) { idx -> if (idx == 4) key else "" }
                if (level - 1 in ladder.indices) {
                    ladder[level - 1] = text
                    updated[key] = ladder
                    applied++
                }
            }
            ladders = updated
            ProLog.i("VOCAB", "phrasebook applied $applied phrases at L$level")
            applied
        } catch (e: Exception) {
            ProLog.e("VOCAB", "phrasebook import failed: ${e.message}")
            0
        }
    }

    /** Tip band by level: band 1 = L1–3, band 2 = L4–6, band 3 = L7–10 (L51551). */
    fun band(level: Int): Int = when (level) {
        in 1..3 -> 1
        in 4..6 -> 2
        else -> 3
    }
}
