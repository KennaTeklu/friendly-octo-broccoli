package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.engine.Stats
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Badges — legacy P4_BADGES verbatim (101 badges, 15 check types).
 * Earned set persisted in prefs "p4_badges_earned" (JSON array of ids).
 * countEvent() mirrors legacy Badges.countEvent (theme/vocab counters stored
 * in prefs "p4_event_<event>"). Pure evaluation in evaluate() — unit-tested.
 */
object Badges {
    data class Badge(val id: String, val icon: String, val name: String, val desc: String, val type: String, val value: Int)

    private var all: List<Badge> = emptyList()
    private var loaded = false

    fun ensure(context: Context) {
        if (loaded) return
        try {
            val raw = context.assets.open("data/badges.json").bufferedReader().use { it.readText() }
            all = Json.parseToJsonElement(raw).jsonArray.map { el ->
                val o = el.jsonObject
                val chk = o["check"]!!.jsonObject
                Badge(
                    id = o["id"]!!.jsonPrimitive.content,
                    icon = o["icon"]!!.jsonPrimitive.content,
                    name = o["name"]!!.jsonPrimitive.content,
                    desc = o["desc"]!!.jsonPrimitive.content,
                    type = chk["type"]!!.jsonPrimitive.content,
                    value = chk["value"]?.jsonPrimitive?.intOrNull ?: 1,
                )
            }
        } catch (e: Exception) {
            ProLog.e("BADGES", "load failed: ${e.message}")
        }
        loaded = true
    }

    fun total(): Int = all.size

    fun allBadges(): List<Badge> = all

    private fun eventsKey(event: String) = "p4_event_$event"

    fun countEvent(context: Context, event: String, amount: Int = 1): Int {
        val cur = ProPrefs.get(context, eventsKey(event))?.toIntOrNull() ?: 0
        val next = cur + amount
        ProPrefs.put(context, eventsKey(event), next.toString())
        return next
    }

    fun eventCount(context: Context, event: String): Int =
        ProPrefs.get(context, eventsKey(event))?.toIntOrNull() ?: 0

    fun earned(context: Context): Set<String> = try {
        val raw = ProPrefs.get(context, "p4_badges_earned")
        if (raw == null) emptySet()
        else Json.parseToJsonElement(raw).jsonArray.mapNotNull { it.jsonPrimitive.contentOrNull }.toSet()
    } catch (_: Exception) { emptySet() }

    private fun persist(context: Context, set: Set<String>) {
        ProPrefs.put(context, "p4_badges_earned",
            ProJson.json.encodeToString(JsonArray.serializer(), JsonArray(set.sorted().map { JsonPrimitive(it) })))
    }

    /** Legacy check-type metric sources. Pure aside from prefs reads. */
    fun metrics(context: Context): Map<String, Int> {
        val d = ProState.data
        val streak = Stats.calculateStreak()
        val volume = d.workouts.sumOf { w -> w.summary?.totalVolume ?: 0.0 }.toInt()
        val tried = d.workouts.flatMap { it.exercises }.map { it.id }.distinct().size
        val prs = d.exercises.values.count { rec ->
            val hist = rec.history
            val best = rec.bestWeight
            if (hist.size < 3 || best == null || best <= 0.0) false
            else {
                val last = hist.lastOrNull { !it.skipped }
                (last?.weight ?: 0.0) >= best
            }
        }
        return mapOf(
            "workouts" to d.workouts.size,
            "streak" to streak,
            "volume" to volume,
            "tried" to tried,
            "prs" to prs,
            "backups" to eventCount(context, "backup"),
            "studioAdds" to eventCount(context, "studioAdd"),
            "imports" to eventCount(context, "import"),
            "exports" to eventCount(context, "export"),
            "earlyBird" to eventCount(context, "earlyBird"),
            "nightOwl" to eventCount(context, "nightOwl"),
            "comeback" to eventCount(context, "comeback"),
            "gym" to eventCount(context, "gymMode"),
            "vocab" to eventCount(context, "vocabChanged"),
            "themes" to eventCount(context, "themeChanged"),
        )
    }

    /** Returns newly earned badge ids (persisted). */
    fun recompute(context: Context): List<String> {
        ensure(context)
        val have = earned(context)
        val m = metrics(context)
        val fresh = mutableListOf<String>()
        for (b in all) {
            if (b.id in have) continue
            val metric = m[b.type] ?: 0
            if (metric >= b.value) fresh.add(b.id)
        }
        if (fresh.isNotEmpty()) persist(context, have + fresh.toSet())
        return fresh
    }

    fun byId(id: String): Badge? = all.firstOrNull { it.id == id }
}

/**
 * Motivation — daily quote + tips (legacy P4_QUOTES 75, P4_TIPS 572, banded by vocab level).
 * Quote is date-keyed so it persists across restarts within a day.
 */
object Motivation {
    data class Quote(val t: String, val a: String, val band: Int)
    data class Tip(val d: String, val e: String, val band: Int, val t: String)

    private var quotes: List<Quote> = emptyList()
    private var tips: List<Tip> = emptyList()
    private var loaded = false

    fun ensure(context: Context) {
        if (loaded) return
        try {
            val q = Json.parseToJsonElement(context.assets.open("data/quotes.json").bufferedReader().use { it.readText() }).jsonArray
            quotes = q.map { el ->
                val o = el.jsonObject
                Quote(o["t"]!!.jsonPrimitive.content, o["a"]?.jsonPrimitive?.contentOrNull ?: "Pro", o["band"]?.jsonPrimitive?.intOrNull ?: 2)
            }
            val t = Json.parseToJsonElement(context.assets.open("data/tips.json").bufferedReader().use { it.readText() }).jsonArray
            tips = t.map { el ->
                val o = el.jsonObject
                Tip(o["d"]?.jsonPrimitive?.contentOrNull ?: "general", o["e"]?.jsonPrimitive?.contentOrNull ?: "", o["band"]?.jsonPrimitive?.intOrNull ?: 2, o["t"]!!.jsonPrimitive.content)
            }
        } catch (e: Exception) {
            ProLog.e("MOTIV", "load failed: ${e.message}")
        }
        loaded = true
        ProLog.i("MOTIV", "quotes=${quotes.size} tips=${tips.size}")
    }

    private fun dayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun pick(list: List<*>, key: String): Int {
        if (list.isEmpty()) return 0
        val h = (key + list.size.toChar()).hashCode()
        return ((h % list.size) + list.size) % list.size
    }

    fun dailyQuote(context: Context, level: Int = -1): Quote? {
        ensure(context)
        if (quotes.isEmpty()) return null
        val band = Vocab.band(if (level > 0) level else Vocab.level(context))
        val pool = quotes.filter { it.band == band }.ifEmpty { quotes }
        return pool.getOrNull(pick(pool, dayKey()))
    }

    fun tipOfTheDay(context: Context, level: Int = -1): Tip? {
        ensure(context)
        if (tips.isEmpty()) return null
        val band = Vocab.band(if (level > 0) level else Vocab.level(context))
        val pool = tips.filter { it.band == band }.ifEmpty { tips }
        return pool.getOrNull(pick(pool, dayKey() + "tip"))
    }

    fun tipsFor(context: Context, domain: String, level: Int = -1): List<Tip> {
        ensure(context)
        val band = Vocab.band(if (level > 0) level else Vocab.level(context))
        return tips.filter { it.d == domain && it.band == band }.ifEmpty { tips.filter { it.d == domain } }
    }

    fun domains(context: Context): List<String> {
        ensure(context)
        return tips.map { it.d }.distinct().sorted()
    }
}
