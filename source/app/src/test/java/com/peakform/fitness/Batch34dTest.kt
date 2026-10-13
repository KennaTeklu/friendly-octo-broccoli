package com.peakform.fitness

import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch34dTest {

    // ---- Declaration sentence from explain() ----

    @Test
    fun backSquat_explain_returnsDeclarationWithNumbers() {
        val pres = Prescription(sets = 5, reps = "5", pct1RM = 0.80, restSeconds = 240, sourceRowId = "bs_int_stronger_build")
        val text = BackSquatAuthoredCoach.explain(pres, "STRONGER")
        Assert.assertTrue("Should contain 'Back squat'", text.contains("Back squat"))
        Assert.assertTrue("Should contain '5×5'", text.contains("5×5") || text.contains("5x5"))
        Assert.assertTrue("Should contain '80%'", text.contains("80"))
    }

    @Test
    fun pullUp_explain_returnsDeclarationWithTier() {
        val pres = Prescription(sets = 3, reps = "3-8", tier = 5, restSeconds = 120, sourceRowId = "pu_t5_stronger_build")
        val text = PullUpAuthoredCoach.explain(pres, "STRONGER")
        Assert.assertTrue("Should contain 'Tier 5'", text.contains("Tier 5") || text.contains("tier 5"))
        Assert.assertTrue("Should mention leverage", text.contains("leverage"))
    }

    @Test
    fun bandedLateralRaise_explain_returnsDeclarationWithBandColour() {
        val pres = Prescription(sets = 3, reps = "15-20", bandColour = "red", restSeconds = 45, sourceRowId = "blr_red_hypertrophy_build")
        val text = BandedLateralRaiseAuthoredCoach.explain(pres, "BIGGER")
        Assert.assertTrue("Should contain 'red' band", text.contains("red", ignoreCase = true))
        Assert.assertTrue("Should contain '3×15-20'", text.contains("3×15-20") || text.contains("3x15-20") || text.contains("15-20"))
    }

    @Test
    fun plank_explain_returnsDeclarationWithHoldTime() {
        val pres = Prescription(sets = 3, reps = "hold", duration = 120.0, restSeconds = 45, sourceRowId = "pl_int_endure_build_m")
        val text = PlankAuthoredCoach.explain(pres, "ENDURE")
        Assert.assertTrue("Should contain '120s'", text.contains("120"))
        Assert.assertTrue("Should mention 'hold' or 'hips'", text.contains("hold") || text.contains("hips") || text.contains("Hips"))
    }

    @Test
    fun running_explain_returnsDeclarationWithDistance() {
        val pres = Prescription(sets = 1, reps = "continuous", distance = 3.0, pace = "59-74% VDOT", duration = 1800.0, restSeconds = 0, sourceRowId = "run_e_endure_base")
        val text = RunningAuthoredCoach.explain(pres, "ENDURE")
        Assert.assertTrue("Should contain '3' miles", text.contains("3"))
        Assert.assertTrue("Should mention Zone 2", text.contains("Zone 2"))
    }

    @Test
    fun explain_emptyWhenNoSourceRowId() {
        val pres = Prescription(note = "No source row exists.")
        val text = BackSquatAuthoredCoach.explain(pres, "STRONGER")
        // explain returns prescription.note when sourceRowId is null
        Assert.assertEquals("No source row exists.", text)
    }
}
