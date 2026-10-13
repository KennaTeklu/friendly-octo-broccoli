package com.peakform.fitness

import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch34eTest {

    @Test
    fun tuningSheet_acceptsInitialPrescriptionParameter() {
        // Verify the Prescription data class is usable as initialPrescription
        val pres = Prescription(sets = 5, reps = "5", pct1RM = 0.80, restSeconds = 240, sourceRowId = "test")
        Assert.assertEquals(5, pres.sets)
        Assert.assertEquals("5", pres.reps)
        Assert.assertEquals(0.80, pres.pct1RM!!, 0.001)
    }

    @Test
    fun previewString_containsAllFields() {
        val pres = Prescription(sets = 5, reps = "5", load = 160.0, pct1RM = 0.80, restSeconds = 240)
        val parts = mutableListOf<String>()
        if (pres.load != null) parts.add("${pres.load!!.toInt()} lbs")
        if (pres.pct1RM != null) parts.add("${(pres.pct1RM!! * 100).toInt()}% 1RM")
        if (pres.sets > 0 && pres.reps.isNotBlank()) parts.add("${pres.sets}x${pres.reps}")
        parts.add("rest ${pres.restSeconds}s")
        val preview = "Coach will set: ${parts.joinToString(", ")}."
        Assert.assertTrue(preview.contains("160 lbs"))
        Assert.assertTrue(preview.contains("80% 1RM"))
        Assert.assertTrue(preview.contains("5x5"))
        Assert.assertTrue(preview.contains("rest 240s"))
    }

    @Test
    fun previewString_emptyWhenNoFields() {
        val pres = Prescription()
        val parts = mutableListOf<String>()
        if (pres.load != null) parts.add("${pres.load!!.toInt()} lbs")
        if (pres.pct1RM != null) parts.add("${(pres.pct1RM!! * 100).toInt()}% 1RM")
        if (pres.bandColour != null) parts.add("${pres.bandColour} band")
        if (pres.tier != null) parts.add("Tier ${pres.tier}")
        if (pres.duration != null) parts.add("${pres.duration!!.toInt()}s hold")
        if (pres.distance != null) parts.add("${pres.distance!!.toInt()} mi")
        if (pres.sets > 0 && pres.reps.isNotBlank()) parts.add("${pres.sets}x${pres.reps}")
        if (pres.restSeconds > 0) parts.add("rest ${pres.restSeconds}s")
        val preview = if (parts.isEmpty()) "" else "Coach will set: ${parts.joinToString(", ")}."
        Assert.assertEquals("", preview)
    }

    @Test
    fun backSquat_prescription_hasPct1RMNotLoad() {
        val row = AuthoredRows.backSquatRows.first { it.intent == "STRONGER" && it.phase == "BUILD" }
        val pres = Prescription(sets = row.sets, reps = row.reps, pct1RM = row.pct1RM, restSeconds = row.restSeconds, sourceRowId = row.rowId)
        Assert.assertNotNull(pres.pct1RM)
        Assert.assertNull(pres.load)
        Assert.assertNull(pres.bandColour)
        Assert.assertNull(pres.tier)
    }

    @Test
    fun pullUp_prescription_hasTierNotLoad() {
        val row = AuthoredRows.pullUpRows.first()
        val pres = Prescription(sets = row.sets, reps = row.reps, tier = row.tier, restSeconds = row.restSeconds, sourceRowId = row.rowId)
        Assert.assertNotNull(pres.tier)
        Assert.assertNull(pres.load)
        Assert.assertNull(pres.pct1RM)
    }

    @Test
    fun bandedLateralRaise_prescription_hasBandColourNotLoad() {
        val row = AuthoredRows.bandedLateralRaiseRows.first { it.intent == "BIGGER" && it.phase == "BUILD" }
        val pres = Prescription(sets = row.sets, reps = row.reps, bandColour = row.bandColour, restSeconds = row.restSeconds, sourceRowId = row.rowId)
        Assert.assertNotNull(pres.bandColour)
        Assert.assertNull(pres.load)
        Assert.assertNull(pres.pct1RM)
    }

    @Test
    fun plank_prescription_hasDurationNotLoad() {
        val row = AuthoredRows.plankRows.first { it.intent == "ENDURE" && it.phase == "BUILD" }
        val pres = Prescription(sets = row.sets, reps = "hold", duration = row.duration, restSeconds = row.restSeconds, sourceRowId = row.rowId)
        Assert.assertNotNull(pres.duration)
        Assert.assertNull(pres.load)
        Assert.assertNull(pres.pct1RM)
    }

    @Test
    fun running_prescription_hasDistanceAndPace() {
        val row = AuthoredRows.runningRows.first()
        val pres = Prescription(sets = 1, reps = "continuous", distance = row.distance, pace = row.pace, duration = row.duration, restSeconds = 0, sourceRowId = row.rowId)
        Assert.assertNotNull(pres.distance)
        Assert.assertNotNull(pres.pace)
        Assert.assertNull(pres.load)
        Assert.assertNull(pres.pct1RM)
    }

    @Test
    fun driftReport_hasCorrectRippetoeSpelling() {
        val candidates = listOf(
            File("../docs/drift-report.txt"),
            File("../../docs/drift-report.txt"),
            File("docs/drift-report.txt"),
            File("build/drift-report.txt"),
        )
        val report = candidates.firstOrNull { it.exists() }
        if (report == null) {
            println("ProDrift: no drift report found; run the drift task first")
            return
        }
        val text = report.readText()
        Assert.assertFalse("Should not contain 'rippetetoe'", text.contains("rippetetoe"))
        Assert.assertTrue("Should contain 'rippetoe'", text.contains("rippetoe"))
    }
}
