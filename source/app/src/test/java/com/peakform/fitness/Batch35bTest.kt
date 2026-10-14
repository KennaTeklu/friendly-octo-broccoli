package com.peakform.fitness

import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH 35b — 16 new authored coaches.
 *
 * Pattern follows BackSquatAuthoredCoach / DeadliftAuthoredCoach exactly.
 * Six Prilepin barbell, six Rogue banded, three Sommer bodyweight, one McGill side plank.
 *
 * Drift fixes:
 *   - br_int_stronger_build: pct1RM 0.75 → 0.80, reps "6-8" → "4" (in 80-89% zone bound)
 *   - fs_int_stronger_build: pct1RM 0.75 → 0.80, reps "5" → "4" (in 80-89% zone bound)
 *
 * ID collisions resolved (35a IDs moved to 35b):
 *   - pendlay_row removed from BarbellRowAuthoredCoach → PendlayRowAuthoredCoach
 *   - banded_face_pull removed from FacePullAuthoredCoach → BandedFacePullAuthoredCoach
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch35bTest {

    // ---- Total rows ----

    @Test
    fun totalAuthoredRows_is113() {
        // 55 (v34+v35a) + 58 (v35b) = 113
        Assert.assertEquals(113, AuthoredRows.allRows().size)
    }

    // ---- Part 1: 6 Prilepin compound barbell coaches ----

    @Test
    fun sumoDeadlift_hasRows() {
        Assert.assertEquals(5, AuthoredRows.sumoDeadliftRows.size)
    }

    @Test
    fun inclineBenchPress_hasRows() {
        Assert.assertEquals(5, AuthoredRows.inclineBenchPressRows.size)
    }

    @Test
    fun closeGripBenchPress_hasRows() {
        Assert.assertEquals(4, AuthoredRows.closeGripBenchPressRows.size)
    }

    @Test
    fun pendlayRow_hasRows() {
        Assert.assertEquals(3, AuthoredRows.pendlayRowRows.size)
    }

    @Test
    fun pushPress_hasRows() {
        Assert.assertEquals(4, AuthoredRows.pushPressRows.size)
    }

    @Test
    fun powerClean_hasRows() {
        Assert.assertEquals(3, AuthoredRows.powerCleanRows.size)
    }

    @Test
    fun prilepinCoaches_declareNeeds1RMAndWeight() {
        val coaches = listOf(
            SumoDeadliftAuthoredCoach, InclineBenchPressAuthoredCoach, CloseGripBenchPressAuthoredCoach,
            PendlayRowAuthoredCoach, PushPressAuthoredCoach, PowerCleanAuthoredCoach
        )
        for (c in coaches) {
            val r = c.requirements()
            Assert.assertTrue("${c.movementName}: needs1RM", r.needs1RM)
            Assert.assertTrue("${c.movementName}: needsWeight", r.needsWeight)
            Assert.assertFalse("${c.movementName}: not needsBand", r.needsBand)
            Assert.assertFalse("${c.movementName}: not needsBodyweight", r.needsBodyweight)
        }
    }

    @Test
    fun prilepinCoaches_everyPct1RMFallsInsideZoneNamedInSection() {
        val rows = AuthoredRows.sumoDeadliftRows + AuthoredRows.inclineBenchPressRows +
            AuthoredRows.closeGripBenchPressRows + AuthoredRows.pendlayRowRows +
            AuthoredRows.pushPressRows + AuthoredRows.powerCleanRows
        rows.forEach { row ->
            val pct = row.pct1RM!!
            val inZone = when (row.sourceSection) {
                "70-79% row" -> pct in 0.70..0.79
                "80-89% row" -> pct in 0.80..0.89
                "90+ row" -> pct >= 0.90
                "Novice table" -> pct in 0.65..0.75  // Rippetoe novice starts at 65%, reaches 75 by week 4
                else -> false
            }
            Assert.assertTrue(
                "Row ${row.rowId}: pct1RM=$pct not in zone '${row.sourceSection}'",
                inZone
            )
        }
    }

    @Test
    fun powerClean_provenanceNotesRest180s() {
        // Prilepin chart does not specify rest for power_clean; 180s documented in provenance.
        Assert.assertTrue(
            "PowerClean provenance must note rest=180s",
            PowerCleanAuthoredCoach.provenance.authorNote.contains("180s")
        )
        Assert.assertEquals(180, AuthoredRows.powerCleanRows.first().restSeconds)
    }

    @Test
    fun sumoDeadlift_reconcile_stronger_build() {
        val p = SumoDeadliftAuthoredCoach.reconcile(AuthoredRows.sumoDeadliftRows, "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(3, p.sets)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    @Test
    fun inclineBenchPress_reconcile_stronger_peak() {
        val p = InclineBenchPressAuthoredCoach.reconcile(AuthoredRows.inclineBenchPressRows, "STRONGER", "PEAK", BodyProfile("advanced", "*", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(0.90, p.pct1RM!!, 0.001)
    }

    @Test
    fun closeGripBenchPress_reconcile_stronger_build() {
        val p = CloseGripBenchPressAuthoredCoach.reconcile(AuthoredRows.closeGripBenchPressRows, "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    @Test
    fun pendlayRow_reconcile_stronger_build() {
        val p = PendlayRowAuthoredCoach.reconcile(AuthoredRows.pendlayRowRows, "STRONGER", "BUILD", null)
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    @Test
    fun pushPress_reconcile_stronger_build() {
        val p = PushPressAuthoredCoach.reconcile(AuthoredRows.pushPressRows, "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    @Test
    fun powerClean_reconcile_stronger_build() {
        val p = PowerCleanAuthoredCoach.reconcile(AuthoredRows.powerCleanRows, "STRONGER", "BUILD", null)
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    // ---- Part 2: 6 Rogue banded coaches ----

    @Test
    fun bandedFacePull_hasRows() {
        Assert.assertEquals(3, AuthoredRows.bandedFacePullRows.size)
    }

    @Test
    fun bandedPullApart_hasRows() {
        Assert.assertEquals(4, AuthoredRows.bandedPullApartRows.size)
    }

    @Test
    fun bandedSquat_hasRows() {
        Assert.assertEquals(4, AuthoredRows.bandedSquatRows.size)
    }

    @Test
    fun bandedRow_hasRows() {
        Assert.assertEquals(4, AuthoredRows.bandedRowRows.size)
    }

    @Test
    fun bandedGluteBridge_hasRows() {
        Assert.assertEquals(4, AuthoredRows.bandedGluteBridgeRows.size)
    }

    @Test
    fun bandedChestPress_hasRows() {
        Assert.assertEquals(4, AuthoredRows.bandedChestPressRows.size)
    }

    @Test
    fun bandedCoaches_declareNeedsBandOnly() {
        val coaches = listOf(
            BandedFacePullAuthoredCoach, BandedPullApartAuthoredCoach, BandedSquatAuthoredCoach,
            BandedRowAuthoredCoach, BandedGluteBridgeAuthoredCoach, BandedChestPressAuthoredCoach
        )
        for (c in coaches) {
            val r = c.requirements()
            Assert.assertTrue("${c.movementName}: needsBand", r.needsBand)
            Assert.assertFalse("${c.movementName}: not needs1RM", r.needs1RM)
            Assert.assertFalse("${c.movementName}: not needsWeight", r.needsWeight)
            Assert.assertFalse("${c.movementName}: not needsBodyweight", r.needsBodyweight)
        }
    }

    @Test
    fun bandedRows_noWeightNoPct1RMEver() {
        val rows = AuthoredRows.bandedFacePullRows + AuthoredRows.bandedPullApartRows +
            AuthoredRows.bandedSquatRows + AuthoredRows.bandedRowRows +
            AuthoredRows.bandedGluteBridgeRows + AuthoredRows.bandedChestPressRows
        rows.forEach { row ->
            Assert.assertNull("Row ${row.rowId}: no load field ever", row.load)
            Assert.assertNull("Row ${row.rowId}: no %1RM ever", row.pct1RM)
            Assert.assertNotNull("Row ${row.rowId}: band colour required", row.bandColour)
        }
    }

    @Test
    fun bandedPullApart_includesYellowNoviceRow() {
        Assert.assertTrue(
            "BandedPullApart must include a yellow novice row",
            AuthoredRows.bandedPullApartRows.any { it.bandColour == "yellow" && it.trainingAge == "novice" }
        )
    }

    @Test
    fun bandedLadder_redGreenBlue_allCovered() {
        // Every banded coach (except BandedFacePull which uses the FacePull cable
        // ladder for novice) has at least red, green, blue rows.
        val ladders = listOf(
            AuthoredRows.bandedPullApartRows, AuthoredRows.bandedSquatRows,
            AuthoredRows.bandedRowRows, AuthoredRows.bandedGluteBridgeRows,
            AuthoredRows.bandedChestPressRows
        )
        ladders.forEachIndexed { idx, rows ->
            val colours = rows.map { it.bandColour }.toSet()
            Assert.assertTrue("Banded ladder $idx must include red",
                colours.contains("red"))
            Assert.assertTrue("Banded ladder $idx must include green",
                colours.contains("green"))
            Assert.assertTrue("Banded ladder $idx must include blue",
                colours.contains("blue"))
        }
    }

    // ---- Part 3: 3 Sommer bodyweight coaches ----

    @Test
    fun invertedRow_hasRows_tiers1to3() {
        Assert.assertEquals(3, AuthoredRows.invertedRowRows.size)
        val tiers = AuthoredRows.invertedRowRows.map { it.tier }.toSet()
        Assert.assertTrue("InvertedRow must use tiers 1-3", tiers == setOf(1, 2, 3))
    }

    @Test
    fun archerPullUp_hasRows_tier8() {
        Assert.assertEquals(2, AuthoredRows.archerPullUpRows.size)
        AuthoredRows.archerPullUpRows.forEach {
            Assert.assertEquals("ArcherPullUp must use tier 8", 8, it.tier)
        }
    }

    @Test
    fun lSitPullUp_hasRows_tier7() {
        Assert.assertEquals(2, AuthoredRows.lSitPullUpRows.size)
        AuthoredRows.lSitPullUpRows.forEach {
            Assert.assertEquals("LSitPullUp must use tier 7", 7, it.tier)
        }
    }

    @Test
    fun sommerCoaches_declareNeedsBodyweightOnly() {
        val coaches = listOf(InvertedRowAuthoredCoach, ArcherPullUpAuthoredCoach, LSitPullUpAuthoredCoach)
        for (c in coaches) {
            val r = c.requirements()
            Assert.assertTrue("${c.movementName}: needsBodyweight", r.needsBodyweight)
            Assert.assertFalse("${c.movementName}: not needs1RM", r.needs1RM)
            Assert.assertFalse("${c.movementName}: not needsWeight", r.needsWeight)
            Assert.assertFalse("${c.movementName}: not needsBand", r.needsBand)
        }
    }

    @Test
    fun sommerRows_noWeightNo1RMEver() {
        val rows = AuthoredRows.invertedRowRows + AuthoredRows.archerPullUpRows + AuthoredRows.lSitPullUpRows
        rows.forEach { row ->
            Assert.assertNull("Row ${row.rowId}: no load", row.load)
            Assert.assertNull("Row ${row.rowId}: no %1RM", row.pct1RM)
            Assert.assertNotNull("Row ${row.rowId}: tier required", row.tier)
        }
    }

    @Test
    fun invertedRow_reconcile_stronger_base() {
        val p = InvertedRowAuthoredCoach.reconcile(AuthoredRows.invertedRowRows, "STRONGER", "BASE", BodyProfile("novice", "*", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(1, p.tier)
    }

    @Test
    fun archerPullUp_reconcile_stronger_build() {
        val p = ArcherPullUpAuthoredCoach.reconcile(AuthoredRows.archerPullUpRows, "STRONGER", "BUILD", null)
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(8, p.tier)
    }

    @Test
    fun lSitPullUp_reconcile_stronger_build() {
        val p = LSitPullUpAuthoredCoach.reconcile(AuthoredRows.lSitPullUpRows, "STRONGER", "BUILD", null)
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(7, p.tier)
    }

    // ---- Part 4: 1 McGill side plank coach ----

    @Test
    fun sidePlank_hasRows() {
        Assert.assertEquals(4, AuthoredRows.sidePlankRows.size)
    }

    @Test
    fun sidePlank_citesSidePlankTable_notFrontPlank() {
        // Side plank rows must NOT cite a "Front plank" section.
        // (The "Maintenance row" section name is shared between front and side plank
        // because the maintenance prescription is identical in both McGill tables —
        // it is not a drift because the side-plank maintenance duration is shorter.)
        AuthoredRows.sidePlankRows.forEach { row ->
            Assert.assertFalse(
                "Row ${row.rowId}: sourceSection must NOT reference 'Front plank' (cites side plank table)",
                row.sourceSection.contains("Front plank")
            )
        }
    }

    @Test
    fun sidePlank_intermediateMaleIs90s_not120s() {
        // McGill side plank: intermediate male = 90s. Front plank intermediate male = 120s.
        // The drift fix: ensure side_plank doesn't accidentally use the front plank value.
        val row = AuthoredRows.sidePlankRows.first { it.sourceSection.contains("intermediate") && it.sex == "male" }
        Assert.assertEquals(90.0, row.duration!!, 0.1)
        Assert.assertNotEquals("Side plank intermediate male must NOT be 120s (front plank value)", 120.0, row.duration!!)
    }

    @Test
    fun sidePlank_declaresNeedsDurationOnly() {
        val r = SidePlankAuthoredCoach.requirements()
        Assert.assertTrue("needsDuration", r.needsDuration)
        Assert.assertFalse("not needs1RM", r.needs1RM)
        Assert.assertFalse("not needsWeight", r.needsWeight)
        Assert.assertFalse("not needsBand", r.needsBand)
        Assert.assertFalse("not needsBodyweight", r.needsBodyweight)
    }

    @Test
    fun sidePlank_rowsNoWeightNo1RM() {
        AuthoredRows.sidePlankRows.forEach { row ->
            Assert.assertNull("Row ${row.rowId}: no load", row.load)
            Assert.assertNull("Row ${row.rowId}: no %1RM", row.pct1RM)
            Assert.assertNotNull("Row ${row.rowId}: duration required", row.duration)
        }
    }

    @Test
    fun sidePlank_reconcile_intermediateMale() {
        val p = SidePlankAuthoredCoach.reconcile(AuthoredRows.sidePlankRows, "ENDURE", "BUILD", BodyProfile("intermediate", "male", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(90.0, p.duration!!, 0.1)
    }

    // ---- Drift fixes (v35a rows corrected) ----

    @Test
    fun driftFix_barbellRow_stronger_build_pct1RM_is0_80() {
        val row = AuthoredRows.barbellRowRows.first { it.rowId == "br_int_stronger_build" }
        Assert.assertEquals("br_int_stronger_build pct1RM must be 0.80 (in 80-89% zone)", 0.80, row.pct1RM!!, 0.001)
    }

    @Test
    fun driftFix_barbellRow_stronger_build_reps_inZoneBound() {
        // 80-89% zone bound: 2-4 reps/set
        val row = AuthoredRows.barbellRowRows.first { it.rowId == "br_int_stronger_build" }
        Assert.assertEquals("4", row.reps)
    }

    @Test
    fun driftFix_frontSquat_stronger_build_pct1RM_is0_80() {
        val row = AuthoredRows.frontSquatRows.first { it.rowId == "fs_int_stronger_build" }
        Assert.assertEquals("fs_int_stronger_build pct1RM must be 0.80 (in 80-89% zone)", 0.80, row.pct1RM!!, 0.001)
    }

    @Test
    fun driftFix_frontSquat_stronger_build_reps_inZoneBound() {
        val row = AuthoredRows.frontSquatRows.first { it.rowId == "fs_int_stronger_build" }
        Assert.assertEquals("4", row.reps)
    }

    // ---- ID collision resolution ----

    @Test
    fun idCollision_pendlayRow_assigned_toPendlayRowCoach_only() {
        // pendlay_row removed from BarbellRowAuthoredCoach; claimed by PendlayRowAuthoredCoach.
        Assert.assertFalse(
            "BarbellRowAuthoredCoach must NOT claim pendlay_row (moved to PendlayRowAuthoredCoach)",
            BarbellRowAuthoredCoach.exerciseIds.contains("pendlay_row")
        )
        Assert.assertTrue(
            "PendlayRowAuthoredCoach must claim pendlay_row",
            PendlayRowAuthoredCoach.exerciseIds.contains("pendlay_row")
        )
    }

    @Test
    fun idCollision_bandedFacePull_assigned_toBandedFacePullCoach_only() {
        Assert.assertFalse(
            "FacePullAuthoredCoach must NOT claim banded_face_pull (moved to BandedFacePullAuthoredCoach)",
            FacePullAuthoredCoach.exerciseIds.contains("banded_face_pull")
        )
        Assert.assertTrue(
            "BandedFacePullAuthoredCoach must claim banded_face_pull",
            BandedFacePullAuthoredCoach.exerciseIds.contains("banded_face_pull")
        )
    }

    // ---- No new source files (7 in sources/ cover all 16) ----

    @Test
    fun allNewRows_citeOnlyThe7ExistingSourceFiles() {
        val allowed = setOf(
            "prilepin-1974/chart.md",
            "rogue-band-tension/chart.md",
            "sommer-2006/gymnastic-body-tier-table.md",
            "mcgill-2010/plank-standards.md",
            "daniels-2013/vdot-zones.md",
            "rippetoe-2011/novice-lp.md",
            "rippetoe-2011/assistance.md"
        )
        val newRows = AuthoredRows.sumoDeadliftRows + AuthoredRows.inclineBenchPressRows +
            AuthoredRows.closeGripBenchPressRows + AuthoredRows.pendlayRowRows +
            AuthoredRows.pushPressRows + AuthoredRows.powerCleanRows +
            AuthoredRows.bandedFacePullRows + AuthoredRows.bandedPullApartRows +
            AuthoredRows.bandedSquatRows + AuthoredRows.bandedRowRows +
            AuthoredRows.bandedGluteBridgeRows + AuthoredRows.bandedChestPressRows +
            AuthoredRows.invertedRowRows + AuthoredRows.archerPullUpRows +
            AuthoredRows.lSitPullUpRows + AuthoredRows.sidePlankRows
        newRows.forEach { row ->
            Assert.assertTrue(
                "Row ${row.rowId}: sourceFile ${row.sourceFile} must be one of the 7 existing source files",
                allowed.contains(row.sourceFile)
            )
        }
    }

    @Test
    fun everyNewCoach_hasRequirementsEnvelopeProvenanceVersion() {
        val coaches = listOf(
            SumoDeadliftAuthoredCoach, InclineBenchPressAuthoredCoach, CloseGripBenchPressAuthoredCoach,
            PendlayRowAuthoredCoach, PushPressAuthoredCoach, PowerCleanAuthoredCoach,
            BandedFacePullAuthoredCoach, BandedPullApartAuthoredCoach, BandedSquatAuthoredCoach,
            BandedRowAuthoredCoach, BandedGluteBridgeAuthoredCoach, BandedChestPressAuthoredCoach,
            InvertedRowAuthoredCoach, ArcherPullUpAuthoredCoach, LSitPullUpAuthoredCoach,
            SidePlankAuthoredCoach
        )
        for (c in coaches) {
            Assert.assertNotNull("${c.movementName}: requirements()", c.requirements())
            Assert.assertNotNull("${c.movementName}: envelope", c.envelope)
            Assert.assertNotNull("${c.movementName}: provenance", c.provenance)
            Assert.assertTrue("${c.movementName}: version > 0", c.version > 0)
        }
    }

    @Test
    fun everyNewRowHasComponentsMap() {
        val newRows = AuthoredRows.sumoDeadliftRows + AuthoredRows.inclineBenchPressRows +
            AuthoredRows.closeGripBenchPressRows + AuthoredRows.pendlayRowRows +
            AuthoredRows.pushPressRows + AuthoredRows.powerCleanRows +
            AuthoredRows.bandedFacePullRows + AuthoredRows.bandedPullApartRows +
            AuthoredRows.bandedSquatRows + AuthoredRows.bandedRowRows +
            AuthoredRows.bandedGluteBridgeRows + AuthoredRows.bandedChestPressRows +
            AuthoredRows.invertedRowRows + AuthoredRows.archerPullUpRows +
            AuthoredRows.lSitPullUpRows + AuthoredRows.sidePlankRows
        newRows.forEach { row ->
            Assert.assertTrue("Row ${row.rowId}: components non-empty", row.components.isNotEmpty())
        }
    }
}
