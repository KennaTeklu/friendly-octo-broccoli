package com.peakform.fitness

import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch35Test {

    @Test
    fun totalAuthoredRows_is48() {
        // 20 (v34) + 35 (v35: 5+5+4+3+3+3+3+3+3+3) = 55
        Assert.assertEquals(55, AuthoredRows.allRows().size)
    }

    @Test
    fun deadlift_hasRows() {
        Assert.assertTrue(AuthoredRows.deadliftRows.isNotEmpty())
        Assert.assertEquals(5, AuthoredRows.deadliftRows.size)
    }

    @Test
    fun benchPress_hasRows() {
        Assert.assertTrue(AuthoredRows.benchPressRows.isNotEmpty())
        Assert.assertEquals(5, AuthoredRows.benchPressRows.size)
    }

    @Test
    fun overheadPress_hasRows() {
        Assert.assertTrue(AuthoredRows.overheadPressRows.isNotEmpty())
        Assert.assertEquals(4, AuthoredRows.overheadPressRows.size)
    }

    @Test
    fun barbellRow_hasRows() {
        Assert.assertTrue(AuthoredRows.barbellRowRows.isNotEmpty())
        Assert.assertEquals(3, AuthoredRows.barbellRowRows.size)
    }

    @Test
    fun frontSquat_hasRows() {
        Assert.assertTrue(AuthoredRows.frontSquatRows.isNotEmpty())
        Assert.assertEquals(3, AuthoredRows.frontSquatRows.size)
    }

    @Test
    fun romanianDeadlift_hasRows() {
        Assert.assertTrue(AuthoredRows.romanianDeadliftRows.isNotEmpty())
        Assert.assertEquals(3, AuthoredRows.romanianDeadliftRows.size)
    }

    @Test
    fun chinUp_hasRows() {
        Assert.assertTrue(AuthoredRows.chinUpRows.isNotEmpty())
        Assert.assertEquals(3, AuthoredRows.chinUpRows.size)
    }

    @Test
    fun dip_hasRows() {
        Assert.assertTrue(AuthoredRows.dipRows.isNotEmpty())
        Assert.assertEquals(3, AuthoredRows.dipRows.size)
    }

    @Test
    fun pushUp_hasRows() {
        Assert.assertTrue(AuthoredRows.pushUpRows.isNotEmpty())
        Assert.assertEquals(3, AuthoredRows.pushUpRows.size)
    }

    @Test
    fun facePull_hasRows() {
        Assert.assertTrue(AuthoredRows.facePullRows.isNotEmpty())
        Assert.assertEquals(3, AuthoredRows.facePullRows.size)
    }

    // ---- Requirements per family ----

    @Test
    fun deadlift_requirements_compoundBarbell() {
        val r = DeadliftAuthoredCoach.requirements()
        Assert.assertTrue(r.needs1RM); Assert.assertTrue(r.needsWeight)
        Assert.assertFalse(r.needsBand); Assert.assertFalse(r.needsBodyweight)
    }

    @Test
    fun chinUp_requirements_bodyweight() {
        val r = ChinUpAuthoredCoach.requirements()
        Assert.assertTrue(r.needsBodyweight)
        Assert.assertFalse(r.needs1RM); Assert.assertFalse(r.needsWeight)
    }

    @Test
    fun facePull_requirements_banded() {
        val r = FacePullAuthoredCoach.requirements()
        Assert.assertTrue(r.needsBand)
        Assert.assertFalse(r.needs1RM); Assert.assertFalse(r.needsWeight)
    }

    // ---- Reconcile selects correct row ----

    @Test
    fun deadlift_reconcile_stronger_build() {
        val p = DeadliftAuthoredCoach.reconcile(AuthoredRows.deadliftRows, "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(3, p.sets)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    @Test
    fun benchPress_reconcile_stronger_build() {
        val p = BenchPressAuthoredCoach.reconcile(AuthoredRows.benchPressRows, "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertEquals(5, p.sets)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    @Test
    fun chinUp_reconcile_hasTierNotLoad() {
        val p = ChinUpAuthoredCoach.reconcile(AuthoredRows.chinUpRows, "STRONGER", "BUILD", null)
        Assert.assertEquals(4, p.tier)
        Assert.assertNull(p.load); Assert.assertNull(p.pct1RM)
    }

    @Test
    fun facePull_reconcile_hasBandColourNotLoad() {
        val p = FacePullAuthoredCoach.reconcile(AuthoredRows.facePullRows, "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertEquals("green", p.bandColour)
        Assert.assertNull(p.load); Assert.assertNull(p.pct1RM)
    }

    @Test
    fun romanianDeadlift_reconcile_citesAssistanceSource() {
        val p = RomanianDeadliftAuthoredCoach.reconcile(AuthoredRows.romanianDeadliftRows, "BIGGER", "BUILD", null)
        Assert.assertTrue(p.note.contains("assistance.md"))
    }

    @Test
    fun everyNewCoach_hasRequirementsEnvelopeProvenanceVersion() {
        val coaches = listOf(DeadliftAuthoredCoach, BenchPressAuthoredCoach, OverheadPressAuthoredCoach,
            BarbellRowAuthoredCoach, FrontSquatAuthoredCoach, RomanianDeadliftAuthoredCoach,
            ChinUpAuthoredCoach, DipAuthoredCoach, PushUpAuthoredCoach, FacePullAuthoredCoach)
        for (c in coaches) {
            Assert.assertNotNull("${c.movementName}: requirements()", c.requirements())
            Assert.assertNotNull("${c.movementName}: envelope", c.envelope)
            Assert.assertNotNull("${c.movementName}: provenance", c.provenance)
            Assert.assertTrue("${c.movementName}: version > 0", c.version > 0)
        }
    }

    @Test
    fun everyNewRowHasComponentsMap() {
        val newRows = AuthoredRows.deadliftRows + AuthoredRows.benchPressRows + AuthoredRows.overheadPressRows +
            AuthoredRows.barbellRowRows + AuthoredRows.frontSquatRows + AuthoredRows.romanianDeadliftRows +
            AuthoredRows.chinUpRows + AuthoredRows.dipRows + AuthoredRows.pushUpRows + AuthoredRows.facePullRows
        newRows.forEach { row -> Assert.assertTrue("Row ${row.rowId}: components non-empty", row.components.isNotEmpty()) }
    }
}
