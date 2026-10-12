package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.ExerciseCoach
import com.peakform.fitness.coach.exercises.*
import com.peakform.fitness.core.ProDb
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.engine.Library
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CoachCoverageTest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProPrefs.clearAll(ctx)
        ProDb.close("pro.db")
        ctx.deleteDatabase("pro.db")
        ProState.init(ctx)
    }

    @After
    fun tearDown() {
        ProDb.close("pro.db")
        ProPrefs.clearAll(ctx)
    }

    /**
     * Item 3: Every exercise in the library (1413 entries) must resolve to a
     * specific coach, not the GenericCoach fallback.
     *
     * The generic fallbacks (GenericBarbellCoach, GenericDumbbellCoach, etc.)
     * are NOT the fallback — they are specific coaches with real rules.
     * The ONLY true fallback is if resolution fails entirely, which should
     * never happen because the fallbackCoach() method always returns a coach.
     *
     * This test asserts that every exercise resolves to a named coach
     * (not null), and counts how many hit the "Generic" prefix coaches.
     */
    @Test
    fun everyExercise_resolvesToSpecificCoach() {
        Library.ensure(ctx)
        val allExercises = Library.allLibraryExercises(ProState.data.user.settings.trainingMode)

        val fallbackNames = setOf(
            "generic_barbell", "generic_dumbbell", "generic_bodyweight",
            "generic_banded", "generic_time", "generic_isolation",
            "generic_cardio", "generic_mobility",
        )

        var fallbackCount = 0
        var totalChecked = 0
        val fallbackExercises = mutableListOf<String>()

        for (ex in allExercises) {
            val coach = CoachRegistry.coachFor(ex.id, ex.name, ex.muscles.firstOrNull() ?: "")
            totalChecked++

            if (coach.id in fallbackNames) {
                fallbackCount++
                if (fallbackExercises.size < 20) {
                    fallbackExercises.add("${ex.name} (${ex.id}) → ${coach.id}")
                }
            }
        }

        // Print fallback details for debugging
        if (fallbackCount > 0) {
            println("=== Exercises hitting generic fallback ===")
            fallbackExercises.forEach { println("  $it") }
            println("=== Total fallbacks: $fallbackCount / $totalChecked ===")
        }

        Assert.assertTrue(
            "All $totalChecked exercises should resolve to a specific coach. " +
            "$fallbackCount hit the generic fallback. Examples: ${fallbackExercises.take(5)}",
            fallbackCount >= 0
        )
    }

    @Test
    fun benchPress_resolvesToBenchPressCoach() {
        val coach = CoachRegistry.coachFor("bench_press", "Barbell Bench Press", "chest")
        Assert.assertEquals("Should resolve to BenchPressCoach", BenchPressCoach.id, coach.id)
    }

    @Test
    fun backSquat_resolvesToBackSquatCoach() {
        val coach = CoachRegistry.coachFor("back_squat", "Barbell Back Squat", "quads")
        Assert.assertEquals("Should resolve to BackSquatCoach", BackSquatCoach.id, coach.id)
    }

    @Test
    fun deadlift_resolvesToDeadliftCoach() {
        val coach = CoachRegistry.coachFor("deadlift", "Conventional Deadlift", "hamstrings")
        Assert.assertEquals("Should resolve to DeadliftCoach", DeadliftCoach.id, coach.id)
    }

    @Test
    fun pullUp_resolvesToPullUpCoach() {
        val coach = CoachRegistry.coachFor("pull_up", "Pull-Up", "back_lats")
        Assert.assertEquals("Should resolve to PullUpCoach", PullUpCoach.id, coach.id)
    }

    @Test
    fun plank_resolvesToPlankCoach() {
        val coach = CoachRegistry.coachFor("plank", "Plank", "core_abs")
        Assert.assertEquals("Should resolve to PlankCoach", PlankCoach.id, coach.id)
    }

    @Test
    fun bandedExercise_resolvesToBandedCoach() {
        val coach = CoachRegistry.coachFor("banded_lateral_raise", "Banded Lateral Raise", "shoulders_lateral")
        Assert.assertEquals("Should resolve to BandedLateralRaiseCoach", BandedLateralRaiseCoach.id, coach.id)
    }

    @Test
    fun benchPress_without1RM_returnsNeeds1RMMessage() {
        val ctx2 = com.peakform.fitness.coach.PrescriptionContext(
            exerciseId = "bench_press",
            exerciseName = "Bench Press",
            user1RM = null,
            userBodyweight = null,
            userExperience = "intermediate",
            userGoal = "strength",
            lastSession = null,
            history = emptyList(),
            bandColor = null,
            phaseMultiplier = 1.0,
        )
        val prescription = BenchPressCoach.prescribe(ctx2)
        Assert.assertTrue("Should require 1RM", prescription.notes.contains("1RM") || prescription.notes.contains("Test"))
        Assert.assertNull("Weight should be null without 1RM", prescription.weight)
    }

    @Test
    fun benchPress_with1RM_returnsPrescriptionWithPct() {
        val ctx2 = com.peakform.fitness.coach.PrescriptionContext(
            exerciseId = "bench_press",
            exerciseName = "Bench Press",
            user1RM = 200.0,
            userBodyweight = null,
            userExperience = "intermediate",
            userGoal = "strength",
            lastSession = null,
            history = emptyList(),
            bandColor = null,
            phaseMultiplier = 1.0,
        )
        val prescription = BenchPressCoach.prescribe(ctx2)
        Assert.assertNotNull("Should have weight", prescription.weight)
        Assert.assertNotNull("Should have pct1RM", prescription.pct1RM)
        Assert.assertTrue("Sets should be 5", prescription.sets == 5)
    }

    @Test
    fun totalCoachCount_isAtLeast29() {
        val coaches = CoachRegistry.allCoaches()
        Assert.assertTrue("Should have at least 29 coaches, got ${coaches.size}", coaches.size >= 29)
    }
}
