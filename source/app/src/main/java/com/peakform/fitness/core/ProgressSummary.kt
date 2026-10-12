package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * ProgressSummary — reorganized progress analytics (BATCH-9).
 *
 * Reorganizes the existing radar / chart code so the radar chart sits at the
 * TOP of the Progress screen with a comparison polygon (current vs. 4 weeks
 * ago), followed by a monthly summary card and trend arrows per fitness
 * component.
 *
 * computeTrends() walks the 11 fitness components and computes a per-component
 * trend arrow (UP / FLAT / DOWN) comparing the last 4 weeks to the prior 4.
 * monthlySummary() returns a per-month volume/load aggregate for charting.
 */
object ProgressSummary {
    /** 11 fitness components tracked for the radar + trends. */
    val COMPONENTS: List<String> = listOf(
        "strength", "power", "hypertrophy", "endurance", "speed",
        "agility", "balance", "coordination", "flexibility", "mobility", "stability",
    )

    enum class Trend { UP, FLAT, DOWN }

    @Serializable
    data class ComponentTrend(
        val component: String,
        val currentScore: Double,
        val fourWeeksAgoScore: Double,
        val trend: Trend,
    )

    @Serializable
    data class MonthlySummary(
        val monthIso: String,             // YYYY-MM
        val workoutCount: Int,
        val totalVolumeLbs: Double,
        val avgRpe: Double,
        val topLiftLbs: Double,
    )

    /** Compute a trend per component from the user's workout history.
     *  Each workout contributes a per-component score derived from the
     *  exercises' fitnessComponents weights × volume. Returns 11 entries. */
    fun computeTrends(ctx: Context): List<ComponentTrend> {
        val now = System.currentTimeMillis()
        val fourWeeksMs = 28L * 24 * 3600 * 1000L
        val recentStart = now - fourWeeksMs
        val priorStart = now - 2 * fourWeeksMs

        val workouts = ProState.data.workouts.filter { it.isCompleted }
        val recent = workouts.filter { ProState.utcDayMillis(it.date) >= recentStart }
        val prior = workouts.filter {
            val t = ProState.utcDayMillis(it.date)
            t in priorStart until recentStart
        }

        return COMPONENTS.map { comp ->
            val curScore = scoreFor(recent, comp)
            val prevScore = scoreFor(prior, comp)
            val trend = when {
                curScore > prevScore * 1.05 -> Trend.UP
                curScore < prevScore * 0.95 -> Trend.DOWN
                else -> Trend.FLAT
            }
            ComponentTrend(comp, curScore, prevScore, trend)
        }
    }

    private fun scoreFor(workouts: List<WorkoutRecord>, component: String): Double {
        var score = 0.0
        for (w in workouts) {
            for (ex in w.exercises) {
                if (ex.actual == null || ex.skipped) continue
                if (ex.fitnessComponents.contains(component)) {
                    val sets = ex.actual.sets
                    val reps = ex.actual.reps.sumOf { it.toInt() }.coerceAtLeast(if (ex.actual.reps.isNotEmpty()) 0 else 0)
                    val volume = (ex.actual.weight) * reps * sets
                    score += volume
                }
            }
        }
        return score
    }

    /** Monthly summary for the last `months` months (default 6). */
    fun monthlySummary(ctx: Context, months: Int = 6): List<MonthlySummary> {
        val workouts = ProState.data.workouts.filter { it.isCompleted }
        val now = java.util.Calendar.getInstance()
        val out = mutableListOf<MonthlySummary>()
        val fmt = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US)
        for (i in 0 until months) {
            val cal = now.clone() as java.util.Calendar
            cal.add(java.util.Calendar.MONTH, -i)
            val monthIso = fmt.format(cal.time)
            val monthStart = cal.timeInMillis
            cal.add(java.util.Calendar.MONTH, 1)
            val monthEnd = cal.timeInMillis
            val inMonth = workouts.filter {
                val t = ProState.utcDayMillis(it.date)
                t in monthStart until monthEnd
            }
            var volume = 0.0
            var rpeSum = 0.0
            var rpeN = 0
            var top = 0.0
            for (w in inMonth) {
                for (ex in w.exercises) {
                    if (ex.actual == null || ex.skipped) continue
                    val w_ = ex.actual.weight
                    val reps = ex.actual.reps.sumOf { it.toInt() }
                    val sets = ex.actual.sets
                    volume += w_ * reps * sets
                    if (w_ > top) top = w_
                    val rpeList = ex.actual.rpeList
                    if (rpeList.isNotEmpty()) { rpeSum += rpeList.average(); rpeN++ }
                }
            }
            out.add(MonthlySummary(monthIso, inMonth.size, volume, if (rpeN > 0) rpeSum / rpeN else 0.0, top))
        }
        return out.reversed()
    }
}
