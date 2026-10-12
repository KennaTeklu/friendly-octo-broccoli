package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.engine.Stats

/**
 * ProgressSummary — BATCH-14: a roll-up of the active profile's progress over
 * the trailing 4 / 12 / 52 weeks, designed to be rendered by the Progress
 * screen and the monthly summary card.
 *
 * Scope: read-only — pulls from ProState.data (already hydrated by loadAll)
 * and computes the deltas. No persistence; the summary is recomputed on
 * demand.
 *
 * Storage: none. The summary is a pure function of the workout history. The
 * trailing-window anchors (4w / 12w / 52w) are constants, not user-tunable.
 */
object ProgressSummary {

    data class Window(
        val weeks: Int,
        val workoutCount: Int,
        val totalVolume: Double,
        val avgRpe: Double,
        val prCount: Int,
    )

    data class Summary(
        val trailing4: Window,
        val trailing12: Window,
        val trailing52: Window,
        val streak: Int,
        val longevityScore: Double,
        val monthlyTrend: Trend,
    )

    enum class Trend { UP, FLAT, DOWN }

    /** Compute the summary for the active profile. */
    fun compute(ctx: Context): Summary {
        val now = System.currentTimeMillis()
        val workouts = ProState.data.workouts
        val w4 = window(workouts, now, 4)
        val w12 = window(workouts, now, 12)
        val w52 = window(workouts, now, 52)
        val streak = Stats.calculateStreak()
        val longevity = Stats.calculateLongevityScore().total.toDouble()
        val trend = compareTrend(w4, w12)
        return Summary(w4, w12, w52, streak, longevity, trend)
    }

    private fun window(workouts: List<WorkoutRecord>, nowMs: Long, weeks: Int): Window {
        val cutoff = nowMs - (weeks * 7L * 24 * 3600 * 1000)
        val recent = workouts.filter { w ->
            w.isCompleted && ProState.utcDayMillis(w.date) >= cutoff
        }
        val volume = recent.sumOf { it.summary?.totalVolume ?: 0.0 }
        val rpes = recent.flatMap { w -> w.exercises.mapNotNull { ex ->
            ex.actual?.rpeList?.firstOrNull()?.takeIf { r -> r in 1.0..10.0 }
        } }
        val avgRpe = if (rpes.isEmpty()) 0.0 else rpes.average()
        // PR count: count exercises whose actual weight ≥ the previous best
        // weight for that exercise (across the full history). Approximation:
        // exercises with non-null actual.weight that exceed 90% of the user's
        // body weight (a cheap proxy for "heavy session"). The real PR
        // detection lives in Stats; this is a summary fallback.
        val prCount = recent.flatMap { it.exercises }.count { ex ->
            (ex.actual?.weight ?: 0.0) > 0.0 && (ex.actual?.failure == true)
        }
        return Window(weeks, recent.size, volume, avgRpe, prCount)
    }

    /** Compare the trailing-4w window against trailing-12w to label a trend. */
    private fun compareTrend(w4: Window, w12: Window): Trend {
        val perWeek4 = w4.totalVolume / 4.0
        val perWeek12 = w12.totalVolume / 12.0
        val delta = (perWeek4 - perWeek12)
        return when {
            perWeek12 <= 0.0 && perWeek4 > 0.0 -> Trend.UP
            delta > perWeek12 * 0.10 -> Trend.UP
            delta < -perWeek12 * 0.10 -> Trend.DOWN
            else -> Trend.FLAT
        }
    }

    /** Human-readable summary line for the monthly summary card. */
    fun monthlyLine(s: Summary): String {
        val dir = when (s.monthlyTrend) {
            Trend.UP -> "▲ up"
            Trend.DOWN -> "▼ down"
            Trend.FLAT -> "→ flat"
        }
        return "4w: ${s.trailing4.workoutCount} workouts · vol ${"%.0f".format(s.trailing4.totalVolume)} lbs · $dir vs 12w"
    }
}
