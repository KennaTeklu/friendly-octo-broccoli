package com.peakform.fitness.coach

import android.content.Context

/**
 * OverallCoach — selects which coach handles each exercise.
 *
 * NEVER invents training math. It:
 *   - selects which coach handles each exercise
 *   - enforces session-level rules: total volume target, component balance, warmup/cooldown, rest timing
 *   - never produces a prescription itself
 */
class OverallCoach(private val registry: CoachRegistry = CoachRegistry) {

    data class SessionDay(
        val exerciseIds: List<String>,
        val exerciseNames: List<String>,
        val exerciseGroups: List<String>,
    )

    data class SessionPlan(
        val exercises: List<PlannedExercise>,
    )

    data class PlannedExercise(
        val exerciseId: String,
        val exerciseName: String,
        val coach: ExerciseCoach,
        val prescription: Prescription,
    )

    data class UserContext(
        val user1RM: Map<String, Double>,
        val userBodyweight: Double?,
        val userExperience: String,
        val userGoal: String,
        val history: Map<String, List<LoggedSet>>,
        val bandColors: Map<String, String>,
        val phaseMultiplier: Double,
    )

    fun planSession(day: SessionDay, user: UserContext): SessionPlan {
        val planned = day.exerciseIds.indices.map { i ->
            val exId = day.exerciseIds[i]
            val exName = day.exerciseNames.getOrNull(i) ?: exId
            val exGroup = day.exerciseGroups.getOrNull(i) ?: ""
            prescribeExercise(exId, exName, exGroup, user)
        }
        return SessionPlan(planned)
    }

    fun prescribeExercise(exerciseId: String, name: String, group: String, user: UserContext): PlannedExercise {
        val coach = registry.coachFor(exerciseId, name, group)
        val lastSession = user.history[exerciseId]?.lastOrNull()
        val ctx = PrescriptionContext(
            exerciseId = exerciseId,
            exerciseName = name,
            user1RM = user.user1RM[exerciseId],
            userBodyweight = user.userBodyweight,
            userExperience = user.userExperience,
            userGoal = user.userGoal,
            lastSession = lastSession,
            history = user.history[exerciseId] ?: emptyList(),
            bandColor = user.bandColors[exerciseId],
            phaseMultiplier = user.phaseMultiplier,
        )
        val prescription = coach.prescribe(ctx)
        return PlannedExercise(exerciseId, name, coach, prescription)
    }
}
