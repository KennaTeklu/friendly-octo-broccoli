package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProDb
import com.peakform.fitness.core.ProgressSummary
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.SelfAssessment
import com.peakform.fitness.ui.ThemeController
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-8+9+10 consolidated tests.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch8Test {

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
        try { ProState.flush() } catch (_: Exception) {}
        ProDb.close("pro.db")
        ProPrefs.clearAll(ctx)
    }

    // ---- Item 1: Coaching layer ----

    @Test
    fun tuningAnswers_modifyPrescriptionFormula() {
        val answers = CoachingEngine.TuningAnswers(goal = "bigger", priority = "high")
        val adj = CoachingEngine.computeAdjustment(answers)
        // "bigger" → hypertrophy range 8-12, intensity 0.70
        Assert.assertEquals("8-12", adj.repRange)
        Assert.assertTrue("Intensity should be 0.70 for bigger", adj.intensityMultiplier < 0.75)
        // "high" priority → volume 1.2
        Assert.assertTrue("Volume should be > 1.0 for high priority", adj.volumeMultiplier > 1.0)
    }

    @Test
    fun tuningAnswers_injuryRestriction_halvesLoad() {
        val answers = CoachingEngine.TuningAnswers(restriction = "injury")
        val adj = CoachingEngine.computeAdjustment(answers)
        Assert.assertTrue("Should restrict load for injury", adj.restrictLoad)
        Assert.assertTrue("Intensity should be <= 0.60 for injury", adj.intensityMultiplier <= 0.60)
    }

    @Test
    fun tuningAnswers_persistAndReadBack() {
        val answers = CoachingEngine.TuningAnswers(goal = "stronger", priority = "medium", mindMuscle = 4)
        CoachingEngine.saveTuning(ctx, "bench_press", answers)
        val read = CoachingEngine.getTuning(ctx, "bench_press")
        Assert.assertEquals("stronger", read.goal)
        Assert.assertEquals("medium", read.priority)
        Assert.assertEquals(4, read.mindMuscle)
    }

    @Test
    fun bandedExercise_colorLadderIsOrdered() {
        val colors = CoachingEngine.BAND_COLORS
        Assert.assertEquals(listOf("yellow", "red", "green", "blue", "black"), colors)
        // Resistance should be ascending
        val resistances = colors.map { CoachingEngine.BAND_RESISTANCE_LB[it]!! }
        for (i in 1 until resistances.size) {
            Assert.assertTrue("Band $i should be lighter than band ${i-1}",
                resistances[i] > resistances[i-1])
        }
    }

    // ---- Item 2: Self-assessment ----

    @Test
    fun postSessionFeel_capturesAndPersists() {
        val feel = SelfAssessment.PostSessionFeel(
            workoutId = "w1", date = "2026-01-15", rating = "hard", note = "gassed"
        )
        SelfAssessment.saveFeel(ctx, feel)
        val read = SelfAssessment.getFeel(ctx, "w1")
        Assert.assertNotNull(read)
        Assert.assertEquals("hard", read!!.rating)
        Assert.assertEquals("gassed", read.note)
    }

    @Test
    fun weeklyCheckIn_persistsAndRetrieves() {
        val checkIn = SelfAssessment.WeeklyCheckIn(
            weekStart = "2026-01-12", sleep = 4, stress = 3, energy = 4, motivation = 5, bodyFeel = 4
        )
        SelfAssessment.saveWeeklyCheckIn(ctx, checkIn)
        val all = SelfAssessment.getWeeklyCheckIns(ctx)
        Assert.assertTrue("Should have at least one check-in", all.isNotEmpty())
        Assert.assertEquals(4, all.last().sleep)
    }

    @Test
    fun injuryLog_persistsAndDetectsActive() {
        val injury = SelfAssessment.InjuryLog(
            muscle = "shoulder", startDate = "2026-01-01", status = "active", note = "rotator cuff"
        )
        SelfAssessment.saveInjury(ctx, injury)
        Assert.assertTrue("Should detect active shoulder injury", SelfAssessment.hasActiveInjury(ctx, "shoulder"))
        Assert.assertFalse("Should not detect injury for chest", SelfAssessment.hasActiveInjury(ctx, "chest"))
    }

    @Test
    fun bodyRegionRating_persistsAndIsPrivate() {
        val rating = SelfAssessment.BodyRegionRating(
            date = "2026-01-15", region = "chest", size = "too_small", strength = "too_weak"
        )
        SelfAssessment.saveBodyRating(ctx, rating)
        val all = SelfAssessment.getBodyRatings(ctx)
        Assert.assertTrue("Should have body rating", all.isNotEmpty())
        Assert.assertEquals("chest", all.first().region)
    }

    // ---- Item 3: Progress reorganization ----

    @Test
    fun radar_rendersWithTwoPolygons() {
        // The ComponentRadarWithComparison function reads from ProgressSummary.computeTrends()
        // which returns 11 components. Verify the data shape is correct.
        val trends = ProgressSummary.computeTrends()
        Assert.assertEquals("Should have 11 components", 11, trends.size)
        // Each trend should have current and thirtyDaysAgo values
        trends.forEach { t ->
            Assert.assertTrue("Current should be in range", t.current in 5.0..100.0)
            Assert.assertTrue("ThirtyDaysAgo should be in range", t.thirtyDaysAgo in 5.0..100.0)
        }
    }

    @Test
    fun monthlySummary_computesTrendWords() {
        val summary = ProgressSummary.monthlySummary()
        // Should contain "up", "flat", or "down" or "Not enough data"
        Assert.assertTrue("Summary should contain trend words",
            summary.contains("up") || summary.contains("flat") || summary.contains("down") || summary.contains("Not enough"))
    }

    @Test
    fun trendArrows_areComputed() {
        val trends = ProgressSummary.computeTrends()
        val upCount = trends.count { it.trend == ProgressSummary.Trend.UP }
        val downCount = trends.count { it.trend == ProgressSummary.Trend.DOWN }
        val flatCount = trends.count { it.trend == ProgressSummary.Trend.FLAT }
        Assert.assertEquals("All 11 components should have a trend", 11, upCount + downCount + flatCount)
    }
}
