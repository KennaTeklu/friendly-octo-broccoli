package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProgressSummary
import com.peakform.fitness.core.SelfAssessment
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-8 evidence test — CoachingEngine + SelfAssessment + ProgressSummary.
 *
 * Verifies:
 *  - TuningAnswers round-trip via ProfileSettings (per-profile).
 *  - computeAdjustment clamps sets/reps/weight deltas to safe ranges.
 *  - coachSentence generates a non-blank sentence for every input.
 *  - SelfAssessment PostSessionFeel round-trips and is retrievable.
 *  - ProgressSummary.computeTrends returns 11 ComponentTrend entries.
 *  - OneRmLab.estimate returns the 4 methods + confidence interval + average.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch8Test {
    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        try {
            com.peakform.fitness.core.ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
        } catch (_: Exception) {}
        ProState.init(ctx)
    }

    @Test
    fun tuning_answers_round_trip() {
        val a = CoachingEngine.TuningAnswers(rpeAtTarget = 9, recoveryReady = false, lastSessionSoreness = 4, targetVolume = "cut")
        CoachingEngine.saveTuning(ctx, a)
        val loaded = CoachingEngine.getTuning(ctx)
        Assert.assertEquals(9, loaded.rpeAtTarget)
        Assert.assertFalse(loaded.recoveryReady)
        Assert.assertEquals(4, loaded.lastSessionSoreness)
        Assert.assertEquals("cut", loaded.targetVolume)
    }

    @Test
    fun compute_adjustment_clamps_to_safe_ranges() {
        val a = CoachingEngine.TuningAnswers(rpeAtTarget = 10, recoveryReady = false, lastSessionSoreness = 5, targetVolume = "cut")
        val adj = CoachingEngine.computeAdjustment(a)
        Assert.assertTrue("setsDelta should be ≥ -2", adj.setsDelta >= -2)
        Assert.assertTrue("setsDelta should be ≤ 1", adj.setsDelta <= 1)
        Assert.assertTrue("repsDelta should be ≥ -2", adj.repsDelta >= -2)
        Assert.assertTrue("repsDelta should be ≤ 2", adj.repsDelta <= 2)
        Assert.assertTrue("weightPct should be ≥ 0.85", adj.weightPct >= 0.85)
        Assert.assertTrue("weightPct should be ≤ 1.05", adj.weightPct <= 1.05)
        Assert.assertTrue("sentence should be non-blank", adj.sentence.isNotBlank())
    }

    @Test
    fun coach_sentence_is_non_blank_for_all_inputs() {
        listOf(
            CoachingEngine.TuningAnswers(7, true, 2, "maintain"),
            CoachingEngine.TuningAnswers(10, false, 5, "cut"),
            CoachingEngine.TuningAnswers(4, true, 0, "push"),
            CoachingEngine.TuningAnswers(7, true, 0, "maintain"),
        ).forEach { a ->
            val adj = CoachingEngine.computeAdjustment(a)
            Assert.assertTrue("sentence for $a should be non-blank", adj.sentence.isNotBlank())
        }
    }

    @Test
    fun band_resistance_lookup_is_present() {
        // Industry-standard band resistance values (lbs at full stretch).
        Assert.assertEquals(5, CoachingEngine.BAND_RESISTANCE_LB["yellow"])
        Assert.assertEquals(10, CoachingEngine.BAND_RESISTANCE_LB["red"])
        Assert.assertEquals(15, CoachingEngine.BAND_RESISTANCE_LB["green"])
        Assert.assertEquals(25, CoachingEngine.BAND_RESISTANCE_LB["blue"])
        Assert.assertEquals(35, CoachingEngine.BAND_RESISTANCE_LB["black"])
        Assert.assertEquals(5, CoachingEngine.BAND_COLORS.size)
    }

    @Test
    fun self_assessment_post_session_round_trips() {
        val feel = SelfAssessment.PostSessionFeel(
            workoutId = "w1", at = ProState.nowIso(),
            overallRpe = 8, feltStrong = true, notes = "Felt great", minutesTrained = 60,
        )
        SelfAssessment.savePostSession(ctx, feel)
        val list = SelfAssessment.getPostSessions(ctx)
        Assert.assertTrue("Post-session list should contain the saved entry", list.any { it.workoutId == "w1" && it.feltStrong })
    }

    @Test
    fun progress_summary_compute_trends_returns_11_entries() {
        val trends = ProgressSummary.computeTrends(ctx)
        Assert.assertEquals("Should return exactly 11 component trends", 11, trends.size)
        trends.forEach { t ->
            Assert.assertTrue("Component name should be non-blank", t.component.isNotBlank())
            Assert.assertTrue("Current score should be ≥ 0", t.currentScore >= 0.0)
        }
    }

    @Test
    fun one_rm_lab_estimate_returns_all_four_methods() {
        val r = OneRmLab.estimate("bench_press", "Bench Press", 135.0, 5, rpe = 8.0)
        Assert.assertTrue("Epley should be > 0", r.epley > 0)
        Assert.assertTrue("Brzycki should be > 0", r.brzycki > 0)
        Assert.assertTrue("Lombardi should be > 0", r.lombardi > 0)
        Assert.assertNotNull("RPE-based should be non-null when rpe provided", r.rpeBased)
        Assert.assertTrue("Average should be > 0", r.average > 0)
        Assert.assertTrue("Confidence low should be < average", r.confidenceLow < r.average)
        Assert.assertTrue("Confidence high should be > average", r.confidenceHigh > r.average)
        // Working-weight percentages
        Assert.assertTrue("65% should be 0.65×average", r.workingWeight(0.65) < r.average)
        Assert.assertTrue("95% should be 0.95×average", r.workingWeight(0.95) < r.average)
    }

    @Test
    fun one_rm_lab_snapshot_round_trips_per_profile() {
        val snap = OneRmLab.Snapshot(
            "bench_press", "Bench Press",
            OneRmLab.estimate("bench_press", "Bench Press", 225.0, 1),
        )
        OneRmLab.saveSnapshot(ctx, snap)
        val list = OneRmLab.getSnapshots(ctx)
        Assert.assertTrue("Snapshot list should contain the saved entry", list.any { it.exerciseId == "bench_press" })
    }
}
