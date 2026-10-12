package com.peakform.fitness.coach

import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.UserProfile
import com.peakform.fitness.core.WorkoutData

/**
 * ExerciseCoach — a per-exercise rule engine.
 *
 * BATCH-18: every coach (specific or generic) implements this interface. The
 * CoachingEngine asks each registered coach "given this exercise record + user,
 * what tips do you have?" and the coach returns a list of CoachingTip objects
 * with priority (1=high, 5=low), category, and a short title + body.
 *
 * Specific coaches live in `coach/exercises/AllCoaches.kt` and hardcode the
 * rules for one exercise (e.g. SquatCoach only fires on the squat). Generic
 * coaches apply to any exercise that matches a tag (e.g. BarbellCoach applies
 * to all barbell lifts).
 */
interface ExerciseCoach {
    /** Unique id (matches the exercise id when this is a specific coach). */
    val id: String
    /** Display name. */
    val name: String
    /** Tags this coach matches (e.g. "barbell", "lower-body", "compound"). */
    val tags: Set<String>

    /**
     * Evaluate the exercise record + user profile and return tips.
     *
     * Implementations should be pure functions — no side effects, no I/O.
     * The CoachingEngine handles persistence + logging.
     */
    fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip>
}

/**
 * Raw coaching tip — the raw form before the CoachingEngine wraps it with
 * the exercise id + a stable id. Coaches return these; the engine converts.
 */
data class RawTip(
    val id: String,
    val category: TipCategory,
    val priority: Int,
    val title: String,
    val body: String,
)

enum class TipCategory { FORM, LOAD, RECOVERY, MOTIVATION, SAFETY }

/**
 * OverallCoachInterface — cross-exercise tips that don't belong to any single coach.
 *
 * Examples: "you've trained chest three days in a row — back day tomorrow",
 * "your average RPE is 9.5 — schedule a deload", "streak is at 14 days — keep
 * it going", "you missed your last two scheduled days — pick a small win".
 */
interface OverallCoachInterface {
    val id: String
    val name: String
    fun evaluate(data: WorkoutData, exerciseTips: List<com.peakform.fitness.core.CoachingEngine.CoachingTip>): List<RawTip>
}
