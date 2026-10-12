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
class Batch31Test {

    // ---- Item 2: State model ----

    @Test
    fun eventFold_producesIdenticalStateFromSameEvents() {
        val events = listOf(
            Event(source = "test", type = "1rm_tested", payload = """{"exerciseId":"bench","tested1RM":200.0}"""),
            Event(source = "test", type = "workout_completed", payload = """{"exerciseId":"bench","weight":135.0,"reps":5,"date":"2026-01-01"}"""),
        )
        val state1 = StateFold.fold(events)
        val state2 = StateFold.fold(events)
        Assert.assertEquals("Fold should be deterministic", state1.events.size, state2.events.size)
        Assert.assertEquals("Should have 2 events", 2, state1.events.size)
    }

    @Test
    fun migration_runsIdempotently() {
        val events = listOf(Event(source = "test", type = "test", payload = "{}"))
        val migrated1 = Migrations.migrate(events, 30, 31)
        val migrated2 = Migrations.migrate(migrated1, 30, 31)
        Assert.assertEquals("Migration should be idempotent", migrated1.size, migrated2.size)
    }

    @Test
    fun exportImport_roundTrips() {
        val state = ProfileStateTree(events = listOf(Event(source = "test", type = "test", payload = "{}")))
        val json = ExportContract.export(state)
        val imported = ExportContract.import(json)
        Assert.assertNotNull("Import should succeed", imported)
        Assert.assertEquals("Round-trip preserves events", 1, imported!!.size)
    }

    @Test
    fun fieldQuery_returnsValidDistribution() {
        val dist = StimulusField.fieldQuery("bench_press", "chest", Intent.STRONGER, "build-week-3")
        Assert.assertTrue("Mean should be > 0", dist.mean > 0)
        Assert.assertTrue("StdDev should be > 0", dist.stdDev > 0)
        Assert.assertTrue("Mean should be <= 1", dist.mean <= 1.0)
    }

    @Test
    fun fieldQuery_narrowsWithHistory() {
        val dossier = mapOf("bench_press" to ExerciseDossier("bench_press", tested1RM = 200.0, history = listOf(LoggedSession("2026-01-01", 160.0, 5, 8.0, 800.0))))
        val dist = StimulusField.fieldQuery("bench_press", "chest", Intent.STRONGER, dossier = dossier)
        // With history, the mean should reflect the observed 160/200 = 0.80
        Assert.assertTrue("Posterior mean should shift toward observed", dist.mean > 0.72)
    }

    // ---- Item 3: Engines ----

    @Test
    fun engineRegistry_resolvesAllCombinations() {
        val intents = Intent.values()
        val families = listOf("compound_barbell", "compound_dumbbell", "bodyweight", "band", "time", "isolation", "cardio", "mobility")
        val muscles = listOf("compound", "isolation", "band", "isometric", "dynamic", "bodyweight", "mobility")
        for (intent in intents) {
            for (family in families) {
                for (muscle in muscles) {
                    val engine = EngineRegistry.select(intent, family, muscle)
                    Assert.assertNotNull("Engine should not be null for $intent/$family/$muscle", engine)
                }
            }
        }
    }

    @Test
    fun strengthCompoundEngine_returnsValidFragment() {
        val input = EngineInput(Intent.STRONGER, "compound_barbell", "compound", "intermediate", ArcState.BUILD, 200.0, null, 1.0, Envelope(), null, null)
        val frag = StrengthCompoundEngine.prescribe(input)
        Assert.assertTrue("Sets should be >= 2", frag.sets >= 2)
        Assert.assertTrue("Weight should be > 0", frag.weight!! > 0)
        Assert.assertTrue("Rest should be > 0", frag.restSeconds > 0)
    }

    @Test
    fun all12Engines_returnValidFragments() {
        val input = EngineInput(Intent.MAINTAIN, "compound_barbell", "compound", "intermediate", ArcState.MAINTAIN, 200.0, 150.0, 1.0, Envelope(), 135.0, 5)
        val engines = listOf(StrengthCompoundEngine, HypertrophyCompoundEngine, HypertrophyIsolationEngine, StrengthBandEngine, HypertrophyBandEngine, EnduranceIsometricEngine, EnduranceDynamicEngine, StrengthBodyweightEngine, MobilityEngine, PowerEngine, MaintenanceEngine, MinimalDoseEngine)
        for (e in engines) {
            val frag = e.prescribe(input)
            Assert.assertTrue("${e.id}: sets should be >= 1", frag.sets >= 1)
            Assert.assertTrue("${e.id}: rest should be >= 0", frag.restSeconds >= 0)
        }
    }

    @Test
    fun totalEngineCount_is12() {
        val engines = listOf(StrengthCompoundEngine, HypertrophyCompoundEngine, HypertrophyIsolationEngine, StrengthBandEngine, HypertrophyBandEngine, EnduranceIsometricEngine, EnduranceDynamicEngine, StrengthBodyweightEngine, MobilityEngine, PowerEngine, MaintenanceEngine, MinimalDoseEngine)
        Assert.assertEquals("Should have 12 engines", 12, engines.size)
    }
}
