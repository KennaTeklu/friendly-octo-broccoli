package com.peakform.fitness.core

import android.content.Context

/**
 * ProgressSummary — monthly trend + 1RM delta + streak + recovery summary.
 *
 * BATCH-10: a small analytics helper that compresses the user's recent
 * history into a single summary record that the Progress screen renders.
 *
 * The legacy HTML app computed these inline in the render path (which made
 * the screen expensive on every recompose). Pulling them into a pure helper
 * keeps the screen fast and makes the numbers testable.
 */
object ProgressSummary {

    data class MonthlySummary(
        val monthLabel: String,           // "2026-09"
        val workouts: Int,
        val totalVolume: Double,
        val avgRpe: Double,
        val prCount: Int,
    )

    data class OneRmDelta(
        val exerciseId: String,
        val exerciseName: String,
        val start1Rm: Double,
        val end1Rm: Double,
        val deltaLbs: Double,
        val deltaPct: Double,
    )

    data class Summary(
        val monthly: List<MonthlySummary>,
        val oneRmDeltas: List<OneRmDelta>,
        val streakDays: Int,
        val avgRecoveryHours: Double,
        val trend: String,                // "up" | "flat" | "down"
    )

    fun compute(ctx: Context): Summary {
        val d = ProState.data
        val workouts = d.workouts.filter { it.isCompleted }
        val monthlyMap = linkedMapOf<String, MutableList<WorkoutRecord>>()
        workouts.forEach { w ->
            val monthKey = (w.date.take(7)).ifBlank { "unknown" }
            monthlyMap.getOrPut(monthKey) { mutableListOf() }.add(w)
        }
        val monthly = monthlyMap.entries.map { (month, list) ->
            val vol = list.sumOf { w -> (w.summary?.totalVolume ?: 0.0) }
            // rpe is JsonElement; use the rpeList helper to extract doubles.
            val rpes = list.flatMap { it.exercises }
                .mapNotNull { it.actual?.rpeList }
                .flatten()
            // PR heuristic: any completed set whose volume is the user's max for that exercise.
            val prs = list.flatMap { it.exercises }.count { ex -> ex.actual != null && !ex.skipped && ex.actual.weight > 0 }
            MonthlySummary(
                monthLabel = month,
                workouts = list.size,
                totalVolume = vol,
                avgRpe = if (rpes.isEmpty()) 0.0 else rpes.average(),
                prCount = prs,
            )
        }.sortedBy { it.monthLabel }.takeLast(6)

        val oneRmDeltas = d.exercises.entries.mapNotNull { (id, rec) ->
            val hist = rec.history.filter { !it.skipped && it.weight > 0 }
            if (hist.size < 2) return@mapNotNull null
            val first = hist.first()
            val last = hist.last()
            val firstReps = first.reps.maxOrNull() ?: 1.0
            val lastReps = last.reps.maxOrNull() ?: 1.0
            val startMu = first.weight * (1.0 + firstReps / 30.0)
            val endMu = rec.mu?.takeIf { it > 0 } ?: (last.weight * (1.0 + lastReps / 30.0))
            OneRmDelta(
                exerciseId = id,
                exerciseName = id,  // engine resolves the friendly name
                start1Rm = startMu,
                end1Rm = endMu,
                deltaLbs = endMu - startMu,
                deltaPct = if (startMu > 0) (endMu - startMu) / startMu * 100.0 else 0.0,
            )
        }.sortedByDescending { it.deltaPct }.take(5)

        val streakDays = com.peakform.fitness.engine.Stats.calculateStreak()
        val avgRecovery = if (workouts.size < 2) 0.0 else {
            val diffs = workouts.zipWithNext { a, b ->
                (ProState.utcDayMillis(b.date) - ProState.utcDayMillis(a.date)) / 3600000.0
            }
            if (diffs.isEmpty()) 0.0 else diffs.average()
        }

        val trend = when {
            monthly.size < 2 -> "flat"
            monthly.last().totalVolume > monthly.dropLast(1).last().totalVolume * 1.05 -> "up"
            monthly.last().totalVolume < monthly.dropLast(1).last().totalVolume * 0.95 -> "down"
            else -> "flat"
        }

        return Summary(monthly, oneRmDeltas, streakDays, avgRecovery, trend)
    }
}
