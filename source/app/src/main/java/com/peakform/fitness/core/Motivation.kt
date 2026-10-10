package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.engine.Sessions
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * BATCH-4C Gap 3 — Motivation: daily quote, win-of-the-day, coach tips.
 *
 * Mirrors reference P4.Motivation (p4-core.js L56097–56288):
 *  - dailyQuote: pick once per day from P4_QUOTES (band-filtered), persisted per-day
 *  - winOfTheDay: personalized message based on workout count + didToday + streak
 *  - tips: pick n distinct tips from P4_TIPS (band-filtered)
 *
 * Data assets:
 *  - assets/data/quotes.json (75 entries, extracted from app.js L51368–51444)
 *  - assets/data/tips.json (572 entries, extracted from app.js L51554+)
 *  - assets/data/tip_domains.json (26 domains, extracted from app.js L51553)
 */
object Motivation {

    @Serializable
    data class Quote(val t: String, val a: String = "", val band: Int = 0)

    @Serializable
    data class Tip(val d: String, val e: String = "", val band: Int = 0, val t: String)

    @Serializable
    data class TipDomain(val id: String, val name: String, val icon: String)

    data class Win(val icon: String, val big: String, val small: String)

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Volatile private var quotesCache: List<Quote>? = null
    @Volatile private var tipsCache: List<Tip>? = null
    @Volatile private var domainsCache: List<TipDomain>? = null

    private fun loadQuotes(ctx: Context): List<Quote> {
        quotesCache?.let { return it }
        return try {
            val raw = ctx.assets.open("data/quotes.json").bufferedReader().use { it.readText() }
            val parsed = json.decodeFromString(ListSerializer(Quote.serializer()), raw)
            quotesCache = parsed
            parsed
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun loadTips(ctx: Context): List<Tip> {
        tipsCache?.let { return it }
        return try {
            val raw = ctx.assets.open("data/tips.json").bufferedReader().use { it.readText() }
            val parsed = json.decodeFromString(ListSerializer(Tip.serializer()), raw)
            tipsCache = parsed
            parsed
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun loadDomains(ctx: Context): List<TipDomain> {
        domainsCache?.let { return it }
        return try {
            val raw = ctx.assets.open("data/tip_domains.json").bufferedReader().use { it.readText() }
            val parsed = json.decodeFromString(ListSerializer(TipDomain.serializer()), raw)
            domainsCache = parsed
            parsed
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Band by vocab level: 1 = L1–3, 2 = L4–6, 3 = L7–10 (mirrors P4.Motivation.band). */
    private fun band(ctx: Context): Int {
        val level = Vocab.currentLevel(ctx)
        return Vocab.band(level)
    }

    /**
     * Daily quote — picked once per day, persisted in p4_daily_quote_<dayKey>.
     * Mirrors P4.Motivation.dailyQuote (p4-core.js L56106–56121).
     */
    fun dailyQuote(ctx: Context): Quote? {
        val dayKey = ProState.todayLocal()
        val prefKey = "p4_daily_quote_$dayKey"
        ProPrefs.get(ctx, prefKey)?.let { saved ->
            return try {
                json.decodeFromString(Quote.serializer(), saved)
            } catch (_: Exception) { null }
        }
        val all = loadQuotes(ctx)
        if (all.isEmpty()) return null
        val b = band(ctx)
        val pool = all.filter { it.band == 0 || it.band == b }.ifEmpty { all }
        val pick = pool.random()
        try {
            ProPrefs.put(ctx, prefKey, json.encodeToString(Quote.serializer(), pick))
        } catch (_: Exception) {}
        return pick
    }

    /**
     * Win of the day — personalized message based on workout history.
     * Mirrors P4.Motivation.winOfTheDay (p4-core.js L56147–56168).
     */
    fun winOfTheDay(): Win {
        val workouts = ProState.data.workouts
        val total = workouts.size
        if (total == 0) {
            return Win(
                icon = "fa-seedling",
                big = "Your first win is waiting",
                small = "Generate a workout — it takes two taps.",
            )
        }
        val last = workouts.last()
        val lastDate = last.dateCompleted ?: last.date
        val today = ProState.todayLocal()
        val didToday = lastDate != null && lastDate.startsWith(today)
        if (didToday) {
            val vol = last.summary?.totalVolume ?: 0.0
            val streak = Stats.calculateStreak()
            return Win(
                icon = "fa-fire",
                big = "You trained today — that is the whole game.",
                small = if (vol > 0) {
                    "Volume moved: ${Math.round(vol)} lbs. Streak: $streak day${if (streak == 1) "" else "s"}."
                } else {
                    "Streak: $streak. See you tomorrow."
                },
            )
        }
        val streak = Stats.calculateStreak()
        if (streak >= 2) {
            return Win(
                icon = "fa-fire",
                big = "Streak: $streak days strong",
                small = "One session keeps it alive. Short counts.",
            )
        }
        if (total >= 3) {
            return Win(
                icon = "fa-bolt",
                big = "Welcome back",
                small = "You have $total sessions banked. Add one more today.",
            )
        }
        return Win(
            icon = "fa-star",
            big = "Every rep you do today pays you back",
            small = "Start small. Two taps and you are moving.",
        )
    }

    /**
     * Pick n distinct tips (band-filtered). Mirrors P4.Motivation.tips (p4-core.js L56171–56185).
     */
    fun tips(ctx: Context, n: Int = 2): List<Tip> {
        val all = loadTips(ctx)
        if (all.isEmpty()) return emptyList()
        val b = band(ctx)
        val pool = all.filter { it.band == 0 || it.band == b }.ifEmpty { all }
        if (pool.size <= n) return pool
        val out = mutableListOf<Tip>()
        val used = mutableSetOf<String>()
        var attempts = 0
        while (out.size < n && attempts < n * 10) {
            val t = pool.random()
            val key = t.d + "|" + t.t.take(24)
            if (used.add(key)) out.add(t)
            attempts++
        }
        return out
    }

    /** Lookup domain metadata by id (returns a default if not found). */
    fun domainOf(ctx: Context, id: String): TipDomain {
        val d = loadDomains(ctx).firstOrNull { it.id == id }
        return d ?: TipDomain(id = id, name = "Coach", icon = "fa-circle-info")
    }
}
