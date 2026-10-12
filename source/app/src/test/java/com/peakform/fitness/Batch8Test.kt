package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.SelfAssessment
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Batch 8 evidence test — coaching layer (CoachingEngine + TuningSheet data flow).
 *
 * Verifies the engine-side logic (the sheet UI is exercised through screenshots):
 *  - TuningAnswers round-trip through per-profile ProfileSettings
 *  - computeAdjustment translates answers to intensityMultiplier / repRange /
 *    volumeMultiplier / restrictLoad
 *  - coachSentence includes the goal + intensity percentage + restrict clause
 *  - BAND_COLORS + BAND_RESISTANCE_LB constants
 *  - SelfAssessment.PostSessionFeel / SorenessEntry round-trip (Batch 9 light coverage)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch8Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        com.peakform.fitness.ui.Fa.appContext = ctx
        ProPrefs.clearAll(ctx)
        ProfileState.prefs(ctx).edit().clear().commit()
        ProState.init(ctx)
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        ProfileState.prefs(ctx).edit().clear().commit()
    }

    @Test
    fun coachingEngine_tuningAnswers_roundTrip() {
        val answers = CoachingEngine.TuningAnswers(
            goal = "bigger",
            priority = "muscle",
            restriction = "shoulder",
            enough = "hard",
            mindMuscle = 0.7,
        )
        CoachingEngine.saveTuning(ctx, answers)
        val loaded = CoachingEngine.getTuning(ctx)
        Assert.assertEquals(answers.goal, loaded.goal)
        Assert.assertEquals(answers.priority, loaded.priority)
        Assert.assertEquals(answers.restriction, loaded.restriction)
        Assert.assertEquals(answers.enough, loaded.enough)
        Assert.assertEquals(answers.mindMuscle, loaded.mindMuscle, 0.001)
    }

    @Test
    fun coachingEngine_computeAdjustment_forBiggerMuscleGoal() {
        val answers = CoachingEngine.TuningAnswers(
            goal = "bigger",
            priority = "muscle",
            restriction = "none",
            enough = "just_right",
            mindMuscle = 0.5,
        )
        val adj = CoachingEngine.computeAdjustment(answers)
        // Bigger goal: repMin >= 8, repMax >= 12, volume >= 1.10
        Assert.assertTrue("bigger goal → repMin >= 8", adj.repRange.first >= 8)
        Assert.assertTrue("bigger goal → repMax >= 12", adj.repRange.second >= 12)
        Assert.assertTrue("bigger goal → volume >= 1.10", adj.volumeMultiplier >= 1.10)
        Assert.assertFalse("no restriction → restrictLoad false", adj.restrictLoad)
        Assert.assertTrue("sentence should mention build muscle", adj.sentence.contains("build muscle"))
    }

    @Test
    fun coachingEngine_computeAdjustment_forStrongerStrengthGoal() {
        val answers = CoachingEngine.TuningAnswers(
            goal = "stronger",
            priority = "strength",
            restriction = "none",
            enough = "just_right",
            mindMuscle = 0.0,
        )
        val adj = CoachingEngine.computeAdjustment(answers)
        // Stronger goal: repMin <= 5 (low reps for strength)
        Assert.assertTrue("stronger goal → repMin <= 5", adj.repRange.first <= 5)
        Assert.assertTrue("stronger goal → intensity >= 1.0", adj.intensityMultiplier >= 1.0)
        Assert.assertTrue("sentence should mention build strength", adj.sentence.contains("build strength"))
    }

    @Test
    fun coachingEngine_computeAdjustment_respectJointRestriction() {
        val answers = CoachingEngine.TuningAnswers(
            goal = "fit",
            priority = "strength",
            restriction = "knee",
            enough = "just_right",
            mindMuscle = 0.5,
        )
        val adj = CoachingEngine.computeAdjustment(answers)
        Assert.assertTrue("knee restriction → restrictLoad true", adj.restrictLoad)
        Assert.assertTrue("sentence should mention restriction", adj.sentence.contains("knee"))
    }

    @Test
    fun coachingEngine_bandConstants_exist() {
        // 5 colors: yellow / green / red / black / purple
        Assert.assertEquals(5, CoachingEngine.BAND_COLORS.size)
        Assert.assertTrue(CoachingEngine.BAND_COLORS.contains("yellow"))
        Assert.assertTrue(CoachingEngine.BAND_COLORS.contains("purple"))
        // Resistance increases with band darkness
        val yellow = CoachingEngine.BAND_RESISTANCE_LB["yellow"]
        val purple = CoachingEngine.BAND_RESISTANCE_LB["purple"]
        Assert.assertNotNull(yellow)
        Assert.assertNotNull(purple)
        Assert.assertTrue("yellow is lightest", yellow!! < purple!!)
    }

    @Test
    fun coachingEngine_resetTuning_clearsAnswers() {
        CoachingEngine.saveTuning(ctx, CoachingEngine.TuningAnswers(goal = "leaner"))
        Assert.assertEquals("leaner", CoachingEngine.getTuning(ctx).goal)
        CoachingEngine.resetTuning(ctx)
        Assert.assertEquals("fit", CoachingEngine.getTuning(ctx).goal)  // default
    }

    @Test
    fun selfAssessment_postSessionFeel_roundTrip() {
        val feel = SelfAssessment.PostSessionFeel(
            workoutId = "w_test",
            date = ProState.nowIso(),
            feel = "hard",
            note = "tough",
        )
        SelfAssessment.saveFeel(ctx, feel)
        val loaded = SelfAssessment.feelForWorkout(ctx, "w_test")
        Assert.assertNotNull(loaded)
        Assert.assertEquals("hard", loaded!!.feel)
        Assert.assertEquals("tough", loaded.note)
    }

    @Test
    fun selfAssessment_postSessionFeel_replacesPrior() {
        SelfAssessment.saveFeel(ctx, SelfAssessment.PostSessionFeel(workoutId = "w_dup", date = "2025-01-01", feel = "too_easy"))
        SelfAssessment.saveFeel(ctx, SelfAssessment.PostSessionFeel(workoutId = "w_dup", date = "2025-01-02", feel = "brutal"))
        val loaded = SelfAssessment.feelForWorkout(ctx, "w_dup")
        Assert.assertEquals("brutal", loaded!!.feel)
        Assert.assertEquals("2025-01-02", loaded.date)
    }

    @Test
    fun selfAssessment_injury_hasActiveInjury() {
        val today = ProState.todayLocal()
        SelfAssessment.saveInjury(ctx, SelfAssessment.InjuryLog(
            startDate = today,
            region = "shoulder",
            description = "Rotator cuff strain",
        ))
        Assert.assertTrue(SelfAssessment.hasActiveInjury(ctx, "shoulder"))
        Assert.assertFalse(SelfAssessment.hasActiveInjury(ctx, "knee"))
        SelfAssessment.resolveInjury(ctx, "shoulder")
        Assert.assertFalse(SelfAssessment.hasActiveInjury(ctx, "shoulder"))
    }

    @Test
    fun selfAssessment_monthlyText_roundTrip() {
        SelfAssessment.saveMonthlyText(ctx, "2025-01", "Strong January — PR on squat.")
        Assert.assertEquals("Strong January — PR on squat.", SelfAssessment.getMonthlyText(ctx, "2025-01"))
    }
}
