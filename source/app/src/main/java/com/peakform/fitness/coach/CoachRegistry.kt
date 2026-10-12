package com.peakform.fitness.coach

import com.peakform.fitness.coach.exercises.*

/**
 * CoachRegistry — resolves an exercise to its specific coach.
 *
 * Resolution order:
 *   1. exact exerciseId match in any coach's supportedExerciseIds
 *   2. name pattern match (coach's supportedNamePatterns)
 *   3. muscle group fallback coach
 *   4. GenericCoach (logs a warning; should never hit in prod)
 */
object CoachRegistry {
    private val coaches: List<ExerciseCoach> = listOf(
        // Compound barbell
        BenchPressCoach, BackSquatCoach, DeadliftCoach, OverheadPressCoach,
        BarbellRowCoach, FrontSquatCoach, RomanianDeadliftCoach,
        // Compound dumbbell / machine
        DumbbellBenchPressCoach, DumbbellRowCoach, LegPressCoach, LatPulldownCoach,
        // Bodyweight
        PullUpCoach, PushUpCoach, DipCoach,
        // Banded
        BandedLateralRaiseCoach, BandedFacePullCoach, BandedPullApartCoach,
        // Time-based
        PlankCoach, SidePlankCoach, DeadHangCoach,
        // Isolation
        DumbbellCurlCoach, TricepPushdownCoach, LegCurlCoach, LegExtensionCoach,
        // Cardio
        RunningCoach, CyclingCoach, RowingCoach,
        // Fallback coaches (broad coverage)
        GenericBarbellCoach, GenericDumbbellCoach, GenericBodyweightCoach,
        GenericBandedCoach, GenericTimeBasedCoach, GenericIsolationCoach,
        GenericCardioCoach, GenericMobilityCoach,
    )

    fun coachFor(exerciseId: String, name: String, group: String = ""): ExerciseCoach {
        // 1. exact ID match
        coaches.firstOrNull { exerciseId in it.supportedExerciseIds }?.let { return it }
        // 2. name pattern match
        coaches.firstOrNull { coach ->
            coach.supportedNamePatterns.any { it.containsMatchIn(name) || it.containsMatchIn(name.lowercase()) }
        }?.let { return it }
        // 3. muscle group / equipment fallback
        val fallback = fallbackCoach(name, group)
        return fallback
    }

    private fun fallbackCoach(name: String, group: String): ExerciseCoach {
        val lower = name.lowercase()
        return when {
            lower.contains("plank") || lower.contains("hang") || lower.contains("hold") || lower.contains("bridge") -> GenericTimeBasedCoach
            lower.contains("band") || lower.contains("resistance band") -> GenericBandedCoach
            lower.contains("barbell") || lower.contains("squat") || lower.contains("deadlift") || lower.contains("press") || lower.contains("row") -> GenericBarbellCoach
            lower.contains("dumbbell") || lower.contains("curl") || lower.contains("extension") || lower.contains("raise") -> GenericDumbbellCoach
            lower.contains("pull-up") || lower.contains("push-up") || lower.contains("dip") || lower.contains("chin-up") || lower.contains("bodyweight") -> GenericBodyweightCoach
            lower.contains("run") || lower.contains("jog") || lower.contains("sprint") || lower.contains("bike") || lower.contains("cycl") || lower.contains("row") && lower.contains("machine") -> GenericCardioCoach
            lower.contains("stretch") || lower.contains("mobility") || lower.contains("foam") || lower.contains("roll") -> GenericMobilityCoach
            lower.contains("cable") || lower.contains("machine") || lower.contains("pulldown") || lower.contains("pushdown") -> GenericIsolationCoach
            group.contains("cardio") || group.contains("athletic") -> GenericCardioCoach
            group.contains("stretch") || group.contains("mobility") -> GenericMobilityCoach
            else -> GenericIsolationCoach
        }
    }

    fun allCoaches(): List<ExerciseCoach> = coaches
}
