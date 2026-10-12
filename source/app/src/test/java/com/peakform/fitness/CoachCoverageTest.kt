package com.peakform.fitness

import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.exercises.AllCoaches
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-18D CoachCoverageTest — verifies all 29 specific + 8 generic coaches
 * are registered.
 *
 * Pure-registry test — no Compose, no Context. Fast.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CoachCoverageTest {

    @Before
    fun setUp() {
        CoachRegistry.clear()
        AllCoaches.registerAll()
    }

    @After
    fun tearDown() {
        CoachRegistry.clear()
    }

    @Test
    fun registry_has_29_specific_coaches() {
        Assert.assertEquals(29, CoachRegistry.specificCount())
    }

    @Test
    fun registry_has_8_generic_coaches() {
        Assert.assertEquals(8, CoachRegistry.genericCount())
    }

    @Test
    fun registry_has_37_total_coaches() {
        Assert.assertEquals(37, CoachRegistry.all().size)
    }

    @Test
    fun specific_coaches_match_expected_exercise_ids() {
        val expectedIds = setOf(
            "back_squat", "bench_press", "deadlift", "overhead_press",
            "barbell_row", "pull_up", "chin_up", "dip",
            "front_squat", "romanian_deadlift", "sumo_deadlift",
            "incline_bench_press", "leg_press", "lunge",
            "bicep_curl", "tricep_extension", "lateral_raise",
            "face_pull", "plank", "hanging_leg_raise",
            "barbell_shrug", "calf_raise", "hip_thrust",
            "kettlebell_swing", "push_up", "dumbbell_row",
            "cable_fly", "lat_pulldown", "preacher_curl",
        )
        for (id in expectedIds) {
            Assert.assertNotNull("Coach for $id should be registered", CoachRegistry.forExercise(id))
        }
    }

    @Test
    fun generic_coaches_have_generic_prefix() {
        val generic = CoachRegistry.all().filter { it.id.startsWith("generic_") }
        Assert.assertEquals(8, generic.size)
        for (coach in generic) {
            Assert.assertTrue("Generic coach id should start with generic_: ${coach.id}",
                coach.id.startsWith("generic_"))
        }
    }

    @Test
    fun every_coach_has_non_empty_tags() {
        for (coach in CoachRegistry.all()) {
            Assert.assertTrue("Coach ${coach.id} should have non-empty tags",
                coach.tags.isNotEmpty())
        }
    }

    @Test
    fun every_coach_returns_tips_on_evaluate() {
        // Every coach should return a list (possibly empty) without crashing.
        val rec = com.peakform.fitness.core.ExerciseRecord(
            history = listOf(com.peakform.fitness.core.HistoryEntry(
                date = "2026-01-01", weight = 100.0, sets = 3, firstRPE = 7.0,
            )),
            mu = 150.0,
        )
        val user = com.peakform.fitness.core.UserProfile(
            name = "Test", experience = "intermediate",
        )
        for (coach in CoachRegistry.all()) {
            val tips = coach.evaluate(rec, user)
            // We don't assert non-empty — some coaches may legitimately return
            // empty tips for this record. We just assert no crash.
            Assert.assertTrue("Coach ${coach.id} should return a list", tips is List<*>)
        }
    }

    @Test
    fun forTags_returns_matching_generic_coaches() {
        val barbellCoaches = CoachRegistry.forTags(setOf("barbell"))
        Assert.assertTrue("At least one coach should match 'barbell'", barbellCoaches.isNotEmpty())
        // The BarbellCoach + every specific coach tagged with "barbell" should match.
        val compoundCoaches = CoachRegistry.forTags(setOf("compound"))
        Assert.assertTrue("At least one coach should match 'compound'", compoundCoaches.isNotEmpty())
    }

    @Test
    fun evaluateAll_combines_specific_and_generic() {
        val tips = CoachRegistry.evaluateAll(
            exerciseId = "back_squat",
            tags = setOf("barbell", "compound", "lower-body"),
            record = com.peakform.fitness.core.ExerciseRecord(
                history = listOf(com.peakform.fitness.core.HistoryEntry(
                    date = "2026-01-01", weight = 200.0, sets = 5, firstRPE = 9.0,
                )),
                mu = 250.0,
            ),
            user = com.peakform.fitness.core.UserProfile(name = "Test", experience = "beginner"),
        )
        // SquatCoach + BarbellCoach + CompoundCoach should all contribute.
        Assert.assertTrue("evaluateAll should return tips for back_squat", tips.isNotEmpty())
        // The SquatCoach contributes at least 1 tip (depth cue).
        Assert.assertTrue("Should have at least 3 tips (squat + barbell + compound)", tips.size >= 3)
    }
}
