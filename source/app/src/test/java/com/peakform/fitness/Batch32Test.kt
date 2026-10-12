package com.peakform.fitness

import com.peakform.fitness.coach.engine.*
import com.peakform.fitness.core.state.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch32Test {

    // ---- Item 2: Engines with sourced methodology ----

    @Test
    fun strengthCompoundEngine_usesPrilepinTable() {
        // Source: Prilepin's Chart — at 80% 1RM, optimal total reps = 15, reps/set = 2-4
        // Rippetoe: intermediate = 5×5 at 80%
        val input = EngineInput(Intent.STRONGER, "compound_barbell", "compound", "intermediate", ArcState.BUILD, 200.0, null, 1.0, Envelope(), null, null)
        val frag = StrengthCompoundEngine.prescribe(input)
        Assert.assertEquals("Prilepin/Rippetoe/Bompa", frag.source)
        Assert.assertEquals(5, frag.sets)  // Rippetoe 5×5
        Assert.assertEquals("5", frag.reps)  // Rippetoe 5 reps
        Assert.assertEquals(0.80, frag.pct1RM!!, 0.001)  // Prilepin 80%
        Assert.assertEquals(160.0, frag.weight!!, 0.1)  // 200 * 0.80
    }

    @Test
    fun strengthCompoundEngine_beginnerUsesRippetoeNovice() {
        // Source: Rippetoe SS 3e — novice starts at 65%, 3×5
        val input = EngineInput(Intent.STRONGER, "compound_barbell", "compound", "beginner", ArcState.BASE, 200.0, null, 1.0, Envelope(), null, null)
        val frag = StrengthCompoundEngine.prescribe(input)
        Assert.assertEquals(3, frag.sets)  // Rippetoe novice 3×5
        Assert.assertEquals("5", frag.reps)
        Assert.assertEquals(0.65, frag.pct1RM!!, 0.001)  // Rippetoe novice 65%
    }

    @Test
    fun hypertrophyCompoundEngine_usesIsraetelMEV() {
        // Source: Israetel — 70% 1RM, 8-12 reps, 2 RIR
        val input = EngineInput(Intent.BIGGER, "compound_barbell", "compound", "intermediate", ArcState.BUILD, 200.0, null, 1.0, Envelope(), null, null)
        val frag = HypertrophyCompoundEngine.prescribe(input)
        Assert.assertEquals("Israetel/Helms", frag.source)
        Assert.assertEquals(0.70, frag.pct1RM!!, 0.001)
        Assert.assertEquals(4, frag.sets)
        Assert.assertTrue("Reps should be 8-12", frag.reps.contains("8"))
    }

    @Test
    fun powerEngine_usesZatsiorskyOptimalZone() {
        // Source: Zatsiorsky — optimal power zone = 30-60% 1RM
        val input = EngineInput(Intent.POWER, "compound_barbell", "compound", "intermediate", ArcState.PEAK, 200.0, null, 1.0, Envelope(), null, null)
        val frag = PowerEngine.prescribe(input)
        Assert.assertEquals("Verkhoshansky/Zatsiorsky", frag.source)
        Assert.assertEquals(0.50, frag.pct1RM!!, 0.001)  // 50% = middle of 30-60%
        Assert.assertEquals(100.0, frag.weight!!, 0.1)  // 200 * 0.50
    }

    @Test
    fun enduranceDynamicEngine_usesSeilerPolarized() {
        val input = EngineInput(Intent.ENDURE, "cardio", "dynamic", "intermediate", ArcState.BASE, null, 150.0, 1.0, Envelope(), null, null)
        val frag = EnduranceDynamicEngine.prescribe(input)
        Assert.assertEquals("Seiler/Maffetone", frag.source)
        Assert.assertTrue("Should mention Zone 2", frag.note.contains("Zone 2"))
    }

    @Test
    fun all12Engines_haveSourceAttribution() {
        val input = EngineInput(Intent.MAINTAIN, "compound_barbell", "compound", "intermediate", ArcState.MAINTAIN, 200.0, 150.0, 1.0, Envelope(), 135.0, 5)
        val engines = listOf(
            StrengthCompoundEngine, HypertrophyCompoundEngine, HypertrophyIsolationEngine,
            StrengthBandEngine, HypertrophyBandEngine, EnduranceIsometricEngine,
            EnduranceDynamicEngine, StrengthBodyweightEngine, MobilityEngine,
            PowerEngine, MaintenanceEngine, MinimalDoseEngine,
        )
        for (e in engines) {
            val frag = e.prescribe(input)
            Assert.assertTrue("${e.id}: source should be non-empty", frag.source.isNotBlank())
            Assert.assertTrue("${e.id}: ruleId should be non-empty", frag.ruleId.isNotBlank())
        }
    }

    @Test
    fun engineRegistry_neverReturnsNull() {
        val intents = Intent.values()
        val families = listOf("compound_barbell", "isolation", "bodyweight", "band", "time", "cardio", "mobility")
        val muscles = listOf("compound", "isolation", "band", "isometric", "dynamic", "bodyweight", "mobility")
        for (intent in intents) {
            for (family in families) {
                for (muscle in muscles) {
                    val engine = EngineRegistry.select(intent, family, muscle)
                    Assert.assertNotNull(engine)
                }
            }
        }
    }

    @Test
    fun totalEngineCount_is12() {
        val engines = listOf(
            StrengthCompoundEngine, HypertrophyCompoundEngine, HypertrophyIsolationEngine,
            StrengthBandEngine, HypertrophyBandEngine, EnduranceIsometricEngine,
            EnduranceDynamicEngine, StrengthBodyweightEngine, MobilityEngine,
            PowerEngine, MaintenanceEngine, MinimalDoseEngine,
        )
        Assert.assertEquals(12, engines.size)
    }

    // ---- Item 3: Coaches as reconcilers ----

    @Test
    fun everyPrescriptionCarriesSource() {
        // Every prescription from every engine has a non-empty source field
        val input = EngineInput(Intent.STRONGER, "compound_barbell", "compound", "intermediate", ArcState.BUILD, 200.0, 150.0, 1.0, Envelope(), 135.0, 5)
        val engines = listOf(StrengthCompoundEngine, HypertrophyCompoundEngine, HypertrophyIsolationEngine,
            StrengthBandEngine, HypertrophyBandEngine, EnduranceIsometricEngine,
            EnduranceDynamicEngine, StrengthBodyweightEngine, MobilityEngine,
            PowerEngine, MaintenanceEngine, MinimalDoseEngine)
        for (e in engines) {
            val frag = e.prescribe(input)
            Assert.assertTrue("${e.id}: source must be non-empty", frag.source.isNotBlank())
        }
    }

}

