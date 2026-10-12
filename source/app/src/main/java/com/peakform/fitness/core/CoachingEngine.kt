package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.OverallCoach

/**
 * CoachingEngine — orchestrates per-exercise coach rules + the overall coach.
 *
 * BATCH-14: this is the entry point for any "what should I do next?" question.
 * The engine loads the active profile's training data, asks each registered
 * exercise-specific coach for a recommendation, then asks the OverallCoach to
 * reconcile / prioritize / dedupe them into a single coherent prescription.
 *
 * The output is a CoachingPlan — a list of CoachingTip objects with priority,
 * category (form / load / recovery / motivation) and a target exercise id.
 */
object CoachingEngine {

    enum class TipCategory { FORM, LOAD, RECOVERY, MOTIVATION, SAFETY }

    data class CoachingTip(
        val id: String,
        val exerciseId: String?,
        val category: TipCategory,
        val priority: Int, // 1=high, 5=low
        val title: String,
        val body: String,
    )

    data class CoachingPlan(
        val tips: List<CoachingTip>,
        val summary: String,
        val overallPriority: String,
    )

    /**
     * Evaluate the active profile and produce a coaching plan.
     *
     * @param ctx for profile reads
     * @param focusExerciseId optional — when set, the plan prioritizes tips for
     *  that exercise (used by the Workout screen's coach-led prescription block).
     */
    fun evaluate(ctx: Context, focusExerciseId: String? = null): CoachingPlan {
        val data = ProState.data
        val tips = mutableListOf<CoachingTip>()
        // 1. Ask each exercise coach for tips on its exercise.
        for ((exId, rec) in data.exercises) {
            val coach = CoachRegistry.forExercise(exId)
            if (coach != null) {
                val exTips = coach.evaluate(rec, data.user)
                exTips.forEach { t ->
                    tips.add(CoachingTip(
                        id = "${coach.id}_${t.id}",
                        exerciseId = exId,
                        category = TipCategory.valueOf(t.category.name),
                        priority = t.priority,
                        title = t.title,
                        body = t.body,
                    ))
                }
            }
        }
        // 2. Ask the overall coach for cross-exercise / plan-level tips.
        val overallTips = OverallCoach.evaluate(data, tips)
        overallTips.forEach { t ->
            tips.add(CoachingTip(
                id = "overall_${t.id}",
                exerciseId = null,
                category = TipCategory.valueOf(t.category.name),
                priority = t.priority,
                title = t.title,
                body = t.body,
            ))
        }
        // 3. Sort by priority (1=high first).
        val sorted = tips.sortedBy { it.priority }
        // 4. If a focus exercise was provided, float its tips to the top.
        val focused = if (focusExerciseId != null) {
            val (focus, rest) = sorted.partition { it.exerciseId == focusExerciseId }
            focus + rest
        } else sorted
        // 5. Compose a one-line summary.
        val summary = when {
            focused.isEmpty() -> "No coaching tips right now — log a few sets and check back."
            focused.size == 1 -> "1 tip: ${focused.first().title}"
            else -> "${focused.size} tips — top: ${focused.first().title}"
        }
        ProLog.i("COACH", "evaluate: ${focused.size} tips (focus=$focusExerciseId)")
        return CoachingPlan(
            tips = focused,
            summary = summary,
            overallPriority = focused.firstOrNull()?.priority?.toString() ?: "—",
        )
    }
}
