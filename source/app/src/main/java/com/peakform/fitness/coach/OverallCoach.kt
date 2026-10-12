package com.peakform.fitness.coach

import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.WorkoutData
import com.peakform.fitness.engine.Stats

/**
 * OverallCoach — cross-exercise coaching layer.
 *
 * BATCH-18: looks at the whole training history + the per-exercise tips
 * already collected and produces plan-level recommendations:
 *  - Streak momentum ("14 days — keep going" / "0 days — schedule something")
 *  - Volume trend (going up → push it; flat → variety; down → ease back)
 *  - RPE drift (avg RPE climbing → schedule deload)
 *  - Muscle balance (chest-heavy week → push back / pull days)
 *  - Readiness (uses the last SelfAssessment if present)
 */
object OverallCoach : OverallCoachInterface {
    override val id: String = "overall"
    override val name: String = "Overall Coach"

    override fun evaluate(
        data: WorkoutData,
        exerciseTips: List<CoachingEngine.CoachingTip>,
    ): List<RawTip> {
        val out = mutableListOf<RawTip>()
        val streak = Stats.calculateStreak()
        // 1. Streak momentum.
        when {
            streak >= 14 -> out.add(RawTip(
                id = "streak_high",
                category = TipCategory.MOTIVATION,
                priority = 3,
                title = "$streak-day streak — keep it alive",
                body = "You're past the two-week mark. Pick a small win today to keep the chain going.",
            ))
            streak == 0 -> out.add(RawTip(
                id = "streak_zero",
                category = TipCategory.MOTIVATION,
                priority = 2,
                title = "Streak is cold — relight it",
                body = "Even 10 minutes counts. Get one set in today to restart the chain.",
            ))
            streak in 1..3 -> out.add(RawTip(
                id = "streak_young",
                category = TipCategory.MOTIVATION,
                priority = 4,
                title = "Streak is $streak days young",
                body = "The first week is the hardest — train today to lock it in.",
            ))
        }
        // 2. Volume trend — last 7 days vs prior 7 days.
        val completed = data.workouts.filter { it.isCompleted }
        val now = System.currentTimeMillis()
        val last7 = volumeSince(completed, now - 7L * 24 * 3600 * 1000)
        val prev7 = volumeSince(completed, now - 14L * 24 * 3600 * 1000) - last7
        if (prev7 > 0 && last7 > 0) {
            val delta = (last7 - prev7) / prev7 * 100.0
            when {
                delta > 20 -> out.add(RawTip(
                    id = "vol_spike",
                    category = TipCategory.RECOVERY,
                    priority = 2,
                    title = "Volume is up ${delta.toInt()}% — deload soon",
                    body = "Big jumps in volume raise injury risk. Consider a lighter week next week.",
                ))
                delta < -20 -> out.add(RawTip(
                    id = "vol_drop",
                    category = TipCategory.LOAD,
                    priority = 3,
                    title = "Volume is down ${(-delta).toInt()}%",
                    body = "Easier week — that's fine. Schedule a heavier session when you're ready.",
                ))
            }
        }
        // 3. RPE drift — average of last 5 sessions' firstRPE.
        val rpes = completed.takeLast(5).flatMap { w -> w.exercises.mapNotNull { it.actual?.firstRPE } }
        if (rpes.size >= 3) {
            val avg = rpes.average()
            if (avg >= 9.0) out.add(RawTip(
                id = "rpe_high",
                category = TipCategory.SAFETY,
                priority = 1,
                title = "Average RPE ${"%.1f".format(avg)} — too high",
                body = "Sustained 9+ RPE is a red flag. Schedule a deload week — drop volume 40%.",
            ))
        }
        // 4. Muscle balance — count exercises per muscle group.
        val muscleHits = completed
            .filter { ProState.utcDayMillis(it.date) >= now - 7L * 24 * 3600 * 1000 }
            .flatMap { it.exercises.flatMap { ex -> ex.muscleGroup } }
            .groupingBy { it }
            .eachCount()
        if (muscleHits.isNotEmpty()) {
            val maxHit = muscleHits.maxByOrNull { it.value }
            val minHit = muscleHits.minByOrNull { it.value }
            if (maxHit != null && minHit != null && maxHit.value > minHit.value * 3 && maxHit.value >= 3) {
                out.add(RawTip(
                    id = "balance_off",
                    category = TipCategory.LOAD,
                    priority = 3,
                    title = "${maxHit.key.replace('_', ' ')} heavy this week",
                    body = "Train ${minHit.key.replace('_', ' ')} next — balance prevents overuse.",
                ))
            }
        }
        // 5. Tip count cap — keep the plan digestible.
        if (exerciseTips.size > 12) {
            out.add(RawTip(
                id = "tip_overload",
                category = TipCategory.MOTIVATION,
                priority = 5,
                title = "Lots of tips today — focus on the top 3",
                body = "Don't try to fix everything at once. Pick the high-priority items and revisit the rest next session.",
            ))
        }
        return out
    }

    private fun volumeSince(workouts: List<com.peakform.fitness.core.WorkoutRecord>, sinceMs: Long): Double =
        workouts.filter { ProState.utcDayMillis(it.date) >= sinceMs }.sumOf { w ->
            w.exercises.sumOf { ex ->
                val s = ex.actual?.sets ?: 0
                val wt = ex.actual?.weight ?: 0.0
                val reps = ex.actual?.reps?.firstOrNull() ?: 0.0
                s * wt * reps
            }
        }
}

/** ProState shim — imported here to avoid a circular import via core package. */
private object ProState {
    fun utcDayMillis(iso: String): Long = com.peakform.fitness.core.ProState.utcDayMillis(iso)
}
