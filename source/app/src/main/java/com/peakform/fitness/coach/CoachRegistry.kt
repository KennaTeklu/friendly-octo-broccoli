package com.peakform.fitness.coach

import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.UserProfile

/**
 * CoachRegistry — the lookup table from exercise id → ExerciseCoach.
 *
 * BATCH-18: the CoachingEngine uses this to find a coach for a given exercise.
 * The registry is populated at app start by AllCoaches.registerAll().
 *
 * Specific coaches are keyed by their exercise id (e.g. "back_squat" → SquatCoach).
 * Generic coaches are tagged ("barbell" → BarbellCoach) and apply to any exercise
 * whose tags intersect the coach's tags.
 */
object CoachRegistry {
    private val specific = mutableMapOf<String, ExerciseCoach>()
    private val generic = mutableListOf<Pair<Set<String>, ExerciseCoach>>()

    fun register(coach: ExerciseCoach) {
        if (coach.id.startsWith("generic_")) {
            generic.add(coach.tags to coach)
        } else {
            specific[coach.id] = coach
        }
    }

    fun forExercise(exerciseId: String): ExerciseCoach? = specific[exerciseId]

    /** All coaches that match the given tags (used for generic coaches). */
    fun forTags(tags: Set<String>): List<ExerciseCoach> = generic
        .filter { (coachTags, _) -> coachTags.intersect(tags).isNotEmpty() }
        .map { it.second }

    /** All registered coaches — used by CoachCoverageTest to assert 29+8 = 37. */
    fun all(): List<ExerciseCoach> = specific.values.toList() + generic.map { it.second }

    /** Specific coach count (target: 29). */
    fun specificCount(): Int = specific.size

    /** Generic coach count (target: 8). */
    fun genericCount(): Int = generic.size

    /** Clear the registry — used by tests. */
    fun clear() {
        specific.clear()
        generic.clear()
    }

    /**
     * Convenience: evaluate a record through BOTH the specific coach (if any)
     * AND any matching generic coaches. Used by the CoachingEngine.
     */
    fun evaluateAll(
        exerciseId: String,
        tags: Set<String>,
        record: ExerciseRecord,
        user: UserProfile,
    ): List<RawTip> {
        val out = mutableListOf<RawTip>()
        specific[exerciseId]?.let { out.addAll(it.evaluate(record, user)) }
        forTags(tags).forEach { out.addAll(it.evaluate(record, user)) }
        return out
    }
}
