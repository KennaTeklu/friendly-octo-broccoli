package com.peakform.fitness.coach

import com.peakform.fitness.core.CoachingEngine

/**
 * OverallCoach — top-level coach that pulls together the per-exercise
 * coach's verdict + the fatigue / streak / readiness signals into a single
 * "what should today look like?" recommendation.
 *
 * BATCH-18: the legacy HTML app's `coachLedPrescription()` (PF L41200-41280)
 * did this inline; pulling it out lets us test it in isolation.
 */
object OverallCoach {

    data class SessionRecommendation(
        val sessionType: String,         // "push" | "pull" | "legs" | "upper" | "lower" | "full" | "deload" | "test"
        val message: String,             // top-of-screen coach message
        val holdBack: Boolean,
        val focusExercises: List<String>, // exercise ids to prioritize
        val deloadExercises: List<String>,// exercises that need a 10% drop
    )

    /** Run the overall coach across the user's current data. */
    fun recommend(input: CoachingEngine.CoachInput): SessionRecommendation {
        val muscleGroup = guessMuscleGroup(input.exerciseId)
        val fatigue = input.fatigue
        val sessionType = when {
            fatigue > 0.75 -> "deload"
            input.streakDays >= 7 -> "test"
            muscleGroup == "squat" || muscleGroup == "legs" -> "legs"
            muscleGroup == "bench" || muscleGroup == "push" -> "push"
            muscleGroup == "deadlift" || muscleGroup == "pull" -> "pull"
            else -> "full"
        }
        val message = when (sessionType) {
            "deload" -> "Fatigue is high — drop the volume 40% today, come back stronger next session."
            "test" -> "Streak is hot — open the 1RM Lab and test a max if you feel ready."
            "legs" -> "Legs today — brace, drive, control the descent."
            "push" -> "Push day — shoulders down, chest up, full ROM."
            "pull" -> "Pull day — lead with the elbows, squeeze at the top."
            else -> "Full-body work — keep the rest short and the reps clean."
        }
        return SessionRecommendation(
            sessionType = sessionType,
            message = message,
            holdBack = fatigue > 0.65,
            focusExercises = listOf(input.exerciseId),
            deloadExercises = if (fatigue > 0.75) listOf(input.exerciseId) else emptyList(),
        )
    }

    private fun guessMuscleGroup(exerciseId: String): String {
        val id = exerciseId.lowercase()
        return when {
            id.contains("squat") || id.contains("leg") || id.contains("lunge") -> "legs"
            id.contains("bench") || id.contains("press") || id.contains("push") -> "push"
            id.contains("deadlift") || id.contains("row") || id.contains("pull") -> "pull"
            else -> "general"
        }
    }
}
