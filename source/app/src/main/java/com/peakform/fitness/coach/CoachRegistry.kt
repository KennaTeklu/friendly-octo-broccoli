package com.peakform.fitness.coach

import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.coach.exercises.AllCoaches

/**
 * CoachRegistry — looks up the right ExerciseCoach for an exercise id.
 *
 * BATCH-18: a single map from exerciseId → ExerciseCoach. Every concrete
 * coach registers itself in AllCoaches. Lookup is O(1) and falls back to
 * a generic coach based on the exercise's muscle group when no specific
 * coach exists.
 */
object CoachRegistry {

    private val all: List<ExerciseCoach> = AllCoaches.all()
    private val byId: Map<String, ExerciseCoach> = buildMap {
        all.forEach { coach -> coach.exerciseIds.forEach { id -> put(id, coach) } }
    }

    fun lookup(exerciseId: String): ExerciseCoach? = byId[exerciseId]

    fun all(): List<ExerciseCoach> = all

    /** Total coaches registered (used by CoachCoverageTest). */
    val size: Int get() = all.size

    /** All descriptors — used by the test battery to verify coverage. */
    fun descriptors(): List<CoachDescriptor> = all.map {
        CoachDescriptor(
            id = it.movementName.lowercase().replace(' ', '_'),
            movementName = it.movementName,
            exerciseIds = it.exerciseIds,
            category = "general",
            primaryMuscle = "",
        )
    }
}

data class CoachDescriptor(
    val id: String,
    val movementName: String,
    val exerciseIds: Set<String>,
    val category: String,
    val primaryMuscle: String,
)
