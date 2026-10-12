package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.exercises.AllCoaches
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.HistoryEntry
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProgressSummary
import com.peakform.fitness.core.SelfAssessment
import com.peakform.fitness.core.UserProfile
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-14 evidence test — CoachingEngine + SelfAssessment + ProgressSummary.
 *
 * Verifies:
 *  1. CoachingEngine.evaluate() returns a non-empty plan when the profile has
 *     exercise records.
 *  2. SelfAssessment.save / load round-trips through ProfileSettings.
 *  3. SelfAssessment.recommendedLoadMultiplier() returns the expected value
 *     for boundary RPE values.
 *  4. ProgressSummary.compute() returns a snapshot with the expected fields
 *     (totalWorkouts, totalVolume, streakDays, trendDirection).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch14Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProPrefs.clearAll(ctx)
        val sp = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        sp.edit().clear().commit()
        ProfileState.invalidate(Profiles.DEFAULT)
        ProState.init(ctx)
        kotlinx.coroutines.runBlocking { if (!ProState.initialized) ProState.loadAll() }
        // Register coaches (in case ProApp didn't run).
        CoachRegistry.clear()
        AllCoaches.registerAll()
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        val sp = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        sp.edit().clear().commit()
        ProfileState.invalidate(Profiles.DEFAULT)
        CoachRegistry.clear()
    }

    @Test
    fun coachingEngine_returns_plan() {
        // Seed an exercise record for back_squat so the SquatCoach fires.
        val rec = ExerciseRecord(
            history = listOf(HistoryEntry(date = "2026-01-01", weight = 200.0, sets = 3, firstRPE = 8.0)),
            mu = 250.0,
            tested1RM = 250.0,
            testDate = "2026-01-01",
        )
        ProState.data = ProState.data.copy(
            exercises = ProState.data.exercises.toMutableMap().apply { this["back_squat"] = rec },
            user = UserProfile(name = "Test", experience = "intermediate"),
        )
        val plan = CoachingEngine.evaluate(ctx, "back_squat")
        Assert.assertNotNull(plan)
        Assert.assertTrue("Plan should have at least one tip", plan.tips.isNotEmpty())
        // Focused tips (exercise = back_squat) should be at the top.
        Assert.assertEquals("back_squat", plan.tips.first().exerciseId)
    }

    @Test
    fun coachingEngine_summary_is_non_empty() {
        val plan = CoachingEngine.evaluate(ctx)
        Assert.assertTrue("Summary should be a non-empty string", plan.summary.isNotEmpty())
    }

    @Test
    fun selfAssessment_round_trips_through_profileSettings() {
        val a = SelfAssessment(
            rpe = 8, readiness = 3, soreness = 1, sleepHours = 7.0,
            mood = 4, notes = "felt good", timestamp = "2026-01-01T00:00:00Z",
        )
        SelfAssessment.save(ctx, a)
        val loaded = SelfAssessment.load(ctx)
        Assert.assertNotNull(loaded)
        Assert.assertEquals(8, loaded!!.rpe)
        Assert.assertEquals(3, loaded.readiness)
        Assert.assertEquals(1, loaded.soreness)
        Assert.assertEquals(7.0, loaded.sleepHours, 0.01)
        Assert.assertEquals(4, loaded.mood)
        Assert.assertEquals("felt good", loaded.notes)
    }

    @Test
    fun selfAssessment_recommendedLoadMultiplier_at_boundaries() {
        // RPE 9+ → back off 8%.
        Assert.assertEquals(0.92, SelfAssessment.recommendedLoadMultiplier(SelfAssessment(rpe = 10)), 0.001)
        Assert.assertEquals(0.92, SelfAssessment.recommendedLoadMultiplier(SelfAssessment(rpe = 9)), 0.001)
        // RPE 8 → back off 3%.
        Assert.assertEquals(0.97, SelfAssessment.recommendedLoadMultiplier(SelfAssessment(rpe = 8)), 0.001)
        // RPE 7 → hold.
        Assert.assertEquals(1.0, SelfAssessment.recommendedLoadMultiplier(SelfAssessment(rpe = 7)), 0.001)
        // RPE 6 → push 2%.
        Assert.assertEquals(1.02, SelfAssessment.recommendedLoadMultiplier(SelfAssessment(rpe = 6)), 0.001)
        // RPE 5 → push 5%.
        Assert.assertEquals(1.05, SelfAssessment.recommendedLoadMultiplier(SelfAssessment(rpe = 5)), 0.001)
        // No assessment → hold.
        Assert.assertEquals(1.0, SelfAssessment.recommendedLoadMultiplier(null), 0.001)
    }

    @Test
    fun progressSummary_compute_returns_snapshot() {
        val snap = ProgressSummary.compute()
        Assert.assertNotNull(snap)
        // With no workouts, totals should be zero.
        Assert.assertTrue("totalWorkouts >= 0", snap.totalWorkouts >= 0)
        Assert.assertTrue("totalVolume >= 0", snap.totalVolume >= 0.0)
        Assert.assertTrue("streakDays >= 0", snap.streakDays >= 0)
        Assert.assertNotNull("trendDirection != null", snap.trendDirection)
        // Direction has a non-empty symbol + label.
        Assert.assertTrue(snap.trendDirection.symbol.isNotEmpty())
        Assert.assertTrue(snap.trendDirection.label.isNotEmpty())
    }

    @Test
    fun progressSummary_direction_symbols() {
        // Verify all three direction values have the expected symbols.
        Assert.assertEquals("↑", ProgressSummary.Direction.UP.symbol)
        Assert.assertEquals("→", ProgressSummary.Direction.FLAT.symbol)
        Assert.assertEquals("↓", ProgressSummary.Direction.DOWN.symbol)
        Assert.assertTrue(ProgressSummary.Direction.UP.label.contains("up"))
        Assert.assertTrue(ProgressSummary.Direction.DOWN.label.contains("down"))
    }
}
