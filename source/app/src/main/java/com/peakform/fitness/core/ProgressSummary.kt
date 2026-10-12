package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.engine.Stats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * ProgressSummary — BATCH-10 monthly summary + trend arrow aggregation.
 *
 * Used by the Progress screen's "Monthly Summary" card and the trend arrows
 * next to each metric. Pulls from the active profile's workout history
 * (ProState.data.workouts) so the summary is correct per-profile.
 *
 * All numbers are pre-formatted for the UI; the UI layer only handles
 * presentation (color, alignment, etc.).
 */
object ProgressSummary {

    data class MonthlyRow(
        val monthLabel: String,        // "Jan 2025"
        val workouts: Int,
        val volume: Double,
        val avgRpe: Double?,
        val trend: String,             // "up" | "down" | "flat"
    )

    data class MetricTrend(
        val label: String,
        val current: Double,
        val previous: Double,
        val arrow: String,            // "↑" "↓" "→"
        val delta: Double,
    )

    private val UTC = TimeZone.getTimeZone("UTC")
    private val monthFmt = SimpleDateFormat("yyyy-MM", Locale.US).apply { timeZone = UTC }
    private val labelFmt = SimpleDateFormat("MMM yyyy", Locale.US).apply { timeZone = UTC }

    /** Build the trailing-6-months summary (newest → oldest). */
    fun monthly(ctx: Context): List<MonthlyRow> {
        try {
            val workouts = ProState.data.workouts.filter { it.isCompleted }
            if (workouts.isEmpty()) return emptyList()
            // bucket by yyyy-MM
            val byMonth = workouts.groupBy { w ->
                val d = try { SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = UTC }.parse(w.date) } catch (_: Exception) { null }
                    ?: Date()
                monthFmt.format(d)
            }
            val now = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val rows = mutableListOf<MonthlyRow>()
            var prevVol = -1.0
            for (i in 5 downTo 0) {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    set(Calendar.YEAR, now.get(Calendar.YEAR))
                    set(Calendar.MONTH, now.get(Calendar.MONTH) - i)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val key = monthFmt.format(cal.time)
                val list = byMonth[key] ?: emptyList()
                val vol = list.sumOf { it.summary?.totalVolume ?: 0.0 }
                val rpe = list.flatMap { it.exercises }.mapNotNull { ex -> ex.actual?.firstRPE }.let { rs ->
                    if (rs.isEmpty()) null else rs.average()
                }
                val trend = when {
                    prevVol < 0 -> "flat"
                    vol > prevVol * 1.05 -> "up"
                    vol < prevVol * 0.95 -> "down"
                    else -> "flat"
                }
                rows += MonthlyRow(
                    monthLabel = labelFmt.format(cal.time),
                    workouts = list.size,
                    volume = vol,
                    avgRpe = rpe,
                    trend = trend,
                )
                prevVol = vol
            }
            return rows.reversed()  // newest first
        } catch (e: Exception) {
            ProLog.w("PROGSUM", "monthly failed: ${e.message}")
            return emptyList()
        }
    }

    /** Per-metric trend arrows comparing the most-recent 4 weeks vs the prior 4. */
    fun metricTrends(): List<MetricTrend> {
        val out = mutableListOf<MetricTrend>()
        try {
            val workouts = ProState.data.workouts.filter { it.isCompleted }.sortedBy { it.date }
            val now = System.currentTimeMillis()
            val recent = workouts.filter { ProState.utcDayMillis(it.date) >= now - 28L * 86400000 }
            val prior = workouts.filter {
                val ms = ProState.utcDayMillis(it.date)
                ms in (now - 56L * 86400000)..(now - 28L * 86400000)
            }
            fun trend(label: String, cur: Double, prev: Double): MetricTrend {
                val delta = if (prev > 0) cur - prev else 0.0
                val arrow = when {
                    prev <= 0 -> "→"
                    delta > prev * 0.05 -> "↑"
                    delta < -prev * 0.05 -> "↓"
                    else -> "→"
                }
                return MetricTrend(label, cur, prev, arrow, delta)
            }
            out += trend("Volume", recent.sumOf { it.summary?.totalVolume ?: 0.0 }, prior.sumOf { it.summary?.totalVolume ?: 0.0 })
            out += trend("Workouts", recent.size.toDouble(), prior.size.toDouble())
            val recentRpe = recent.flatMap { it.exercises }.mapNotNull { it.actual?.firstRPE }
            val priorRpe = prior.flatMap { it.exercises }.mapNotNull { it.actual?.firstRPE }
            out += trend("Avg RPE",
                if (recentRpe.isEmpty()) 0.0 else recentRpe.average(),
                if (priorRpe.isEmpty()) 0.0 else priorRpe.average(),
            )
            // longevity delta
            val longevityNow = Stats.calculateLongevityScore().total.toDouble()
            out += trend("Longevity", longevityNow, longevityNow) // single-shot — no historical snapshot
        } catch (e: Exception) {
            ProLog.w("PROGSUM", "metricTrends failed: ${e.message}")
        }
        return out
    }
}
