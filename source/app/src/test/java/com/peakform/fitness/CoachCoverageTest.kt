package com.peakform.fitness

import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.ExerciseCoach
import com.peakform.fitness.coach.exercises.AllCoaches
import com.peakform.fitness.core.CoachingEngine
import org.junit.Assert
import org.junit.Test

/**
 * BATCH-18 evidence test — coach coverage.
 *
 * 1. Registry exposes at least 29 specific + 8 generic coaches = 37.
 * 2. Every coach returns a non-blank message for a baseline input.
 * 3. CoachRegistry.lookup returns the right coach for known exercise ids.
 */
class CoachCoverageTest {

    private val baselineInput = CoachingEngine.CoachInput(
        exerciseId = "back_squat",
        exerciseName = "Back Squat",
        lastWeight = 200.0,
        lastReps = 5,
        est1Rm = 240.0,
        fatigue = 0.3,
        goal = "strength",
        aggression = 1.0,
        recoveryDays = 3,
        streakDays = 5,
        jointsFlagged = emptyList(),
    )

    @Test
    fun registry_hasAtLeast37Coaches() {
        Assert.assertTrue("registry size ${CoachRegistry.size} ≥ 37", CoachRegistry.size >= 37)
    }

    @Test
    fun allCoaches_returnNonBlankMessage() {
        AllCoaches.all().forEach { coach: ExerciseCoach ->
            val msg = coach.message(baselineInput)
            Assert.assertTrue("coach ${coach.movementName} returns a message", msg.isNotBlank())
        }
    }

    @Test
    fun lookup_backSquat() {
        val c = CoachRegistry.lookup("back_squat")
        Assert.assertNotNull("BackSquat coach registered", c)
        Assert.assertEquals("Barbell Back Squat", c!!.movementName)
    }

    @Test
    fun lookup_benchPress() {
        // BATCH-35c B1: the authored BenchPressAuthoredCoach now wins precedence
        // over AllCoaches.BenchPress (its requirements() / reconcile() / explain()
        // overrides are why the registry was extended). The authored coach's
        // movementName is "Bench Press" (the AllCoaches entry was "Barbell Bench
        // Press"). The shorter name is the intended post-35c contract.
        val c = CoachRegistry.lookup("bench_press")
        Assert.assertNotNull("BenchPress coach registered", c)
        Assert.assertEquals("Bench Press", c!!.movementName)
    }

    @Test
    fun lookup_deadlift() {
        val c = CoachRegistry.lookup("deadlift")
        Assert.assertNotNull("Deadlift coach registered", c)
        Assert.assertEquals("Deadlift", c!!.movementName)
    }

    @Test
    fun lookup_unknownExerciseReturnsNull() {
        Assert.assertNull(CoachRegistry.lookup("not_a_real_exercise_id_xyz_123"))
    }

    @Test
    fun formCues_nonEmptyForSpecificCoaches() {
        AllCoaches.all().forEach { coach ->
            if (coach.exerciseIds.isNotEmpty()) {
                Assert.assertTrue("form cues for ${coach.movementName}", coach.formCues.isNotEmpty())
            }
        }
    }
}
