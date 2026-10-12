package com.peakform.fitness.core

import com.peakform.fitness.engine.Stats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ProgressSummary — composes the headline progress numbers shown on the
 * Progress screen and the Dashboard "since last week" tile.
 *
 * BATCH-14: extracted from the inline calculations the Progress screen used
 * to do, so the engine can render the same numbers without pulling the whole
 * Progress composable tree.
 */
object ProgressSummary {

    data class Snapshot(
        val totalWorkouts: Int,
        val totalVolume: Double,           // sum of weight × reps across all sets
        val avgRpe: Double,
        val streakDays: Int,
        val prsThisMonth: Int,
        val topLift: Pair<String, Double>?, // exercise name → 1RM (estimated or tested)
        val trendDirection: Direction,      // up / flat / down vs last month
        val monthlyDeltaPct: Double,        // volume change % vs last month
    )

    enum class Direction(val symbol: String, val label: String) {
        UP("↑", "trending up"),
        FLAT("→", "holding steady"),
        DOWN("↓", "trending down"),
    }

    fun compute(): Snapshot {
        val d = ProState.data
        val completed = d.workouts.filter { it.isCompleted }
        val totalVolume = completed.sumOf { w ->
            w.exercises.sumOf { ex ->
                val sets = ex.actual?.sets ?: 0
                val w = ex.actual?.weight ?: 0.0
                val reps = ex.actual?.reps?.firstOrNull() ?: 0.0
                sets * w * reps
            }
        }
        val rpes = completed.flatMap { w -> w.exercises.mapNotNull { it.actual?.firstRPE } }
        val avgRpe = if (rpes.isNotEmpty()) rpes.average() else 0.0
        val streak = Stats.calculateStreak()

        // PRs this month: count exercise records whose bestWeight went up in the
        // last 30 days. Approximation — uses bestWeight as the "current PR".
        val now = System.currentTimeMillis()
        val monthAgo = now - 30L * 24 * 3600 * 1000
        val prsThisMonth = d.exercises.values.count { rec ->
            rec.bestWeight != null && rec.bestWeight > 0 && (rec.lastUpdate?.let { ProState.utcDayMillis(it) } ?: 0L) >= monthAgo
        }

        val topLift = d.exercises.entries
            .mapNotNull { (id, rec) -> rec.tested1RM?.takeIf { it > 0 }?.let { id to it } }
            .maxByOrNull { it.second }

        // Trend: compare last-month volume to the prior month.
        val monthVol = monthVolume(completed, 0)
        val prevMonthVol = monthVolume(completed, 1)
        val deltaPct = if (prevMonthVol > 0) ((monthVol - prevMonthVol) / prevMonthVol) * 100.0 else 0.0
        val trend = when {
            prevMonthVol <= 0 -> Direction.FLAT
            deltaPct > 5.0 -> Direction.UP
            deltaPct < -5.0 -> Direction.DOWN
            else -> Direction.FLAT
        }

        return Snapshot(
            totalWorkouts = completed.size,
            totalVolume = totalVolume,
            avgRpe = avgRpe,
            streakDays = streak,
            prsThisMonth = prsThisMonth,
            topLift = topLift,
            trendDirection = trend,
            monthlyDeltaPct = deltaPct,
        )
    }

    /** Sum volume of workouts completed in month offset (0 = current month). */
    private fun monthVolume(workouts: List<WorkoutRecord>, monthOffset: Int): Double {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.MONTH, -monthOffset)
        val year = cal.get(java.util.Calendar.YEAR)
        val month = cal.get(java.util.Calendar.MONTH)
        return workouts.filter { w ->
            try {
                val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(w.date) ?: return@filter false
                val c = java.util.Calendar.getInstance().apply { time = d }
                c.get(java.util.Calendar.YEAR) == year && c.get(java.util.Calendar.MONTH) == month
            } catch (_: Exception) { false }
        }.sumOf { w ->
            w.exercises.sumOf { ex ->
                val sets = ex.actual?.sets ?: 0
                val wt = ex.actual?.weight ?: 0.0
                val reps = ex.actual?.reps?.firstOrNull() ?: 0.0
                sets * wt * reps
            }
        }
    }
}
