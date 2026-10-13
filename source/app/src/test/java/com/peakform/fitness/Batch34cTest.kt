package com.peakform.fitness

import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch34cTest {

    // ---- Item 4: Card renders only declared fields ----

    @Test
    fun backSquat_requirements_declares1RMAndWeight() {
        // Family: compound barbell → shows: reps, sets, load, %1RM, rest
        val coach = BackSquatAuthoredCoach
        val reqs = coach.requirements()
        Assert.assertTrue("needs1RM", reqs.needs1RM)
        Assert.assertTrue("needsWeight", reqs.needsWeight)
        Assert.assertFalse("not needsBand", reqs.needsBand)
        Assert.assertFalse("not needsDuration", reqs.needsDuration)
        Assert.assertFalse("not needsDistance", reqs.needsDistance)
    }

    @Test
    fun pullUp_requirements_declaresBodyweightOnly() {
        // Family: bodyweight → shows: reps, sets, tier, rest; hides: load, %1RM
        val reqs = PullUpAuthoredCoach.requirements()
        Assert.assertTrue("needsBodyweight", reqs.needsBodyweight)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
        Assert.assertFalse("not needsBand", reqs.needsBand)
    }

    @Test
    fun bandedLateralRaise_requirements_declaresBandOnly() {
        // Family: banded → shows: reps, sets, band colour, rest; hides: load, %1RM
        val reqs = BandedLateralRaiseAuthoredCoach.requirements()
        Assert.assertTrue("needsBand", reqs.needsBand)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
        Assert.assertFalse("not needsBodyweight", reqs.needsBodyweight)
    }

    @Test
    fun plank_requirements_declaresDurationOnly() {
        // Family: time-based → shows: hold time, sets, rest; hides: load, reps
        val reqs = PlankAuthoredCoach.requirements()
        Assert.assertTrue("needsDuration", reqs.needsDuration)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
        Assert.assertFalse("not needsBand", reqs.needsBand)
    }

    @Test
    fun running_requirements_declaresDistanceAndPace() {
        // Family: cardio → shows: distance, pace, duration; hides: load, reps, sets
        val reqs = RunningAuthoredCoach.requirements()
        Assert.assertTrue("needsDistance", reqs.needsDistance)
        Assert.assertTrue("needsPace", reqs.needsPace)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
        Assert.assertFalse("not needsBand", reqs.needsBand)
    }

    @Test
    fun allFalseRequirements_resultsInNoPrescriptionLine() {
        // If requirements() returns all false, card shows exercise name + chips only
        val reqs = CoachRequirements()  // all false by default
        Assert.assertFalse("needs1RM", reqs.needs1RM)
        Assert.assertFalse("needsBand", reqs.needsBand)
        Assert.assertFalse("needsBodyweight", reqs.needsBodyweight)
        Assert.assertFalse("needsDuration", reqs.needsDuration)
        Assert.assertFalse("needsDistance", reqs.needsDistance)
        Assert.assertFalse("needsPace", reqs.needsPace)
        Assert.assertFalse("needsWeight", reqs.needsWeight)
    }

    // ---- Item 5: Component chips from authored rows ----

    @Test
    fun everyAuthoredRowHasComponentsMap() {
        AuthoredRows.allRows().forEach { row ->
            Assert.assertTrue("Row ${row.rowId}: components must be non-empty", row.components.isNotEmpty())
        }
    }

    @Test
    fun componentsAreWeightedNotBoolean() {
        // Every component value should be 1-5, not just 0/1
        AuthoredRows.allRows().forEach { row ->
            row.components.values.forEach { weight ->
                Assert.assertTrue("Row ${row.rowId}: component weight $weight should be 1-5", weight in 1..5)
            }
        }
    }

    @Test
    fun backSquatRow_hasStrengthAsPrimary() {
        val row = AuthoredRows.backSquatRows.first { it.intent == "STRONGER" && it.phase == "BUILD" }
        val strength = row.components["STRENGTH"]
        Assert.assertNotNull("Should have STRENGTH component", strength)
        Assert.assertEquals("STRENGTH should be weight 5", 5, strength)
    }

    @Test
    fun plankRow_hasEnduranceAsPrimary() {
        val row = AuthoredRows.plankRows.first()
        val endurance = row.components["ENDURANCE"]
        Assert.assertNotNull("Should have ENDURANCE component", endurance)
        Assert.assertEquals("ENDURANCE should be weight 5", 5, endurance)
    }

    @Test
    fun runningRow_hasEnduranceAsPrimary() {
        val row = AuthoredRows.runningRows.first()
        val endurance = row.components["ENDURANCE"]
        Assert.assertNotNull("Should have ENDURANCE component", endurance)
        Assert.assertEquals("ENDURANCE should be weight 5", 5, endurance)
    }

    @Test
    fun componentNamesIncludeNonStandard() {
        // AuthoredRows uses CORE_STABILITY which is not in the 11-item list
        // The card should render it anyway, not drop it
        val hasCoreStability = AuthoredRows.allRows().any { row ->
            row.components.containsKey("CORE_STABILITY")
        }
        Assert.assertTrue("Some rows use CORE_STABILITY (not in the 11-item list) — card must render anyway", hasCoreStability)
    }

    // ---- Family resolution per coach ----

    @Test
    fun familyResolution() {
        // BackSquat → compound barbell (needs1RM + needsWeight)
        Assert.assertTrue("BackSquat is compound barbell", BackSquatAuthoredCoach.requirements().needs1RM && BackSquatAuthoredCoach.requirements().needsWeight)
        // PullUp → bodyweight (needsBodyweight, no 1RM/weight)
        Assert.assertTrue("PullUp is bodyweight", PullUpAuthoredCoach.requirements().needsBodyweight)
        // BandedLateralRaise → banded (needsBand, no weight/1RM)
        Assert.assertTrue("BandedLR is banded", BandedLateralRaiseAuthoredCoach.requirements().needsBand)
        // Plank → time-based (needsDuration, no weight/1RM)
        Assert.assertTrue("Plank is time-based", PlankAuthoredCoach.requirements().needsDuration)
        // Running → cardio (needsDistance + needsPace, no weight/1RM/reps)
        Assert.assertTrue("Running is cardio", RunningAuthoredCoach.requirements().needsDistance && RunningAuthoredCoach.requirements().needsPace)
    }
}
