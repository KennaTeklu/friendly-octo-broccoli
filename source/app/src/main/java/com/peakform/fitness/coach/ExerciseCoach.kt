package com.peakform.fitness.coach

import android.content.Context

/**
 * Per-exercise coach interface.
 *
 * Each exercise gets a specific coach with hardcoded, coach-approved mathematics.
 * The generic engine (CoachingEngine / Generator) is a fallback, not the primary path.
 */
interface ExerciseCoach {
    val id: String
    val displayName: String
    val supportedExerciseIds: Set<String>
    val supportedNamePatterns: List<Regex>

    fun requirements(): CoachRequirements
    fun prescribe(ctx: PrescriptionContext): Prescription
    fun explain(prescription: Prescription): String
    fun progression(history: List<LoggedSet>): ProgressionRule
}

data class CoachRequirements(
    val needs1RM: Boolean = false,
    val needsBand: Boolean = false,
    val needsBodyweight: Boolean = false,
    val needsDuration: Boolean = false,
    val needsDistance: Boolean = false,
    val needsPace: Boolean = false,
    val needsWeight: Boolean = false,
)

data class PrescriptionContext(
    val exerciseId: String,
    val exerciseName: String,
    val user1RM: Double?,
    val userBodyweight: Double?,
    val userExperience: String,
    val userGoal: String,
    val lastSession: LoggedSet?,
    val history: List<LoggedSet>,
    val bandColor: String?,
    val phaseMultiplier: Double,
)

data class Prescription(
    val sets: Int,
    val reps: String,
    val weight: Double?,
    val duration: Double?,
    val distance: Double?,
    val pct1RM: Double?,
    val restSeconds: Int,
    val notes: String,
)

data class LoggedSet(
    val weight: Double,
    val reps: Int,
    val rpe: Double?,
    val date: String,
)

data class ProgressionRule(
    val name: String,
    val rule: String,
    val increment: Double,
    val triggerReps: Int,
)
