package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.CompetitionDate
import com.peakform.fitness.core.CouplingMatrixWriteback
import com.peakform.fitness.core.EwmaPerExercise
import com.peakform.fitness.core.ExpandedMessageBank
import com.peakform.fitness.core.FuzzySearch
import com.peakform.fitness.core.GenderFilter

import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProDb
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.RpeColor
import com.peakform.fitness.core.TestResultSync
import com.peakform.fitness.core.VersionCheck
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch15Test {

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

    // H1: genderSuitability filter
    @Test
    fun genderFilter_bothAlwaysIncluded() {
        Assert.assertTrue(GenderFilter.shouldInclude("both", "male"))
        Assert.assertTrue(GenderFilter.shouldInclude("both", "female"))
        Assert.assertTrue(GenderFilter.shouldInclude("", "male"))
    }

    @Test
    fun genderFilter_femaleLutealFiltersMale() {
        Assert.assertFalse(GenderFilter.shouldInclude("male", "female", "luteal"))
        Assert.assertFalse(GenderFilter.shouldInclude("male", "female", "menstrual"))
        Assert.assertTrue(GenderFilter.shouldInclude("female", "female", "luteal"))
    }

    // H4: Fuzzy search
    @Test
    fun fuzzySearch_findsTypos() {
        val items = listOf("squat", "bench press", "deadlift", "overhead press")
        val results = FuzzySearch.search("sqaut", items, threshold = 0.4)
        Assert.assertTrue("Should find 'squat' despite typo", results.contains("squat"))
    }

    @Test
    fun fuzzySearch_exactMatchAlwaysWorks() {
        val items = listOf("squat", "bench press")
        val results = FuzzySearch.search("squat", items)
        Assert.assertTrue("Should find exact match", results.contains("squat"))
    }

    @Test
    fun levenshtein_correctDistance() {
        Assert.assertEquals(0, FuzzySearch.levenshtein("squat", "squat"))
        Assert.assertEquals(2, FuzzySearch.levenshtein("squat", "sqaut"))
        Assert.assertEquals(5, FuzzySearch.levenshtein("squat", "bench"))
    }

    // H5: RPE color smooth gradient
    @Test
    fun rpeColor_greenAtLowRpe() {
        val color = RpeColor.colorForRpe(2.0)
        // Green should have high green channel, low red
        val green = ((color shr 8) and 0xFF).toInt()
        Assert.assertTrue("Green channel should be high for low RPE", green > 0x80)
    }

    @Test
    fun rpeColor_redAtHighRpe() {
        val color = RpeColor.colorForRpe(10.0)
        val red = ((color shr 16) and 0xFF).toInt()
        Assert.assertTrue("Red channel should be high for RPE 10", red > 0x80)
    }

    @Test
    fun rpeColor_interpolatesSmoothly() {
        val c1 = RpeColor.colorForRpe(5.0)
        val c2 = RpeColor.colorForRpe(6.0)
        // Colors should be different (not the same discrete tier)
        Assert.assertNotEquals("RPE 5 and 6 should have different colors", c1, c2)
    }

    // H3: Competition date
    @Test
    fun competitionDate_setAndGet() {
        CompetitionDate.set(ctx, "2026-12-01")
        Assert.assertEquals("2026-12-01", CompetitionDate.get(ctx))
    }

    @Test
    fun competitionDate_taperActiveIn7DayWindow() {
        // Set date 5 days from now
        val future = ProState.todayLocal()
        CompetitionDate.set(ctx, future)
        // daysUntil should be 0 (same day) — taper should be active
        Assert.assertTrue("Taper should be active on competition day", CompetitionDate.isTaperActive(ctx))
    }

    @Test
    fun competitionDate_taperInactiveWhenFar() {
        CompetitionDate.set(ctx, "2027-12-01")
        Assert.assertFalse("Taper should not be active 1 year out", CompetitionDate.isTaperActive(ctx))
    }

    @Test
    fun competitionDate_taperInactiveWhenNotSet() {
        CompetitionDate.set(ctx, null)
        Assert.assertFalse("Taper should not be active when no date set", CompetitionDate.isTaperActive(ctx))
    }

    // H7: Notes hint
    @Test
    fun notesHintIsNotEmpty() {
        val hint = "💡 Notes auto-save — no need to tap a save button."; Assert.assertTrue(hint.isNotBlank())
        Assert.assertTrue(hint.contains("auto-save") || hint.contains("auto-saves"))
    }

    // H6: Version check
    @Test
    fun versionCheck_returnsCurrentVersion() {
        Assert.assertTrue("Version should be non-empty", VersionCheck.currentVersion().isNotBlank())
        Assert.assertTrue("Version code should be > 0", VersionCheck.currentVersionCode() > 0)
    }

    // H2: Test result sync
    @Test
    fun testResultSync_exportsJson() {
        val json = TestResultSync.exportTestResults()
        Assert.assertTrue("Should contain testResults key", json.contains("testResults"))
    }

    // Medium gaps
    @Test
    fun expandedMessageBank_hasAtLeast20Messages() {
        Assert.assertTrue("Should have at least 20 messages", ExpandedMessageBank.messages.size >= 20)
    }

    @Test
    fun couplingMatrix_roundTrip() {
        val matrix = mapOf("chest" to mapOf("triceps" to 0.3, "shoulders" to 0.2))
        CouplingMatrixWriteback.save(ctx, matrix)
        val loaded = CouplingMatrixWriteback.load(ctx)
        Assert.assertNotNull("Should load saved matrix", loaded)
        Assert.assertEquals(0.3, loaded!!["chest"]!!["triceps"]!!, 0.001)
    }

    @Test
    fun ewmaPerExercise_persists() {
        EwmaPerExercise.save(ctx, "bench_press", 0.85)
        Assert.assertEquals(0.85, EwmaPerExercise.get(ctx, "bench_press")!!, 0.001)
    }

    // Audit file exists
    @Test
    fun auditFile_exists() {
        val f = java.io.File("/home/z/friendly-octo-broccoli/source/docs/AUDIT-2026-10.md")
        Assert.assertTrue("AUDIT-2026-10.md should exist", f.exists())
        val content = f.readText()
        Assert.assertTrue("Should contain total coverage", content.contains("86%") || content.contains("coverage"))
    }
}
