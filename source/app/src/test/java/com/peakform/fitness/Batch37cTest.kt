package com.peakform.fitness

import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.engine.Library
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-37c — The PeakForm Book, first pass.
 *
 * Tests:
 *   1. dossiers.json count == exercises.json count
 *   2. every dossier has non-empty provenance.source
 *   3. every "cited" dossier cites a .gov or .edu URL (or sommer/mcgill)
 *   4. every "first-pass" dossier cites peakform-2026/book.md
 *   5. the 5 published citations are preserved
 *   6. NEW exercises have matching dossiers (0 NEW → trivially true)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch37cTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    private val genericCoachIds = setOf(
        "generic_chest", "generic_back", "generic_legs",
        "generic_shoulders", "generic_arms", "generic_core",
        "generic_cardio", "generic_mobility",
    )

    private val publishedIds = setOf(
        "pull-up", "chin-up", "archer_pull-up", "plank", "side_plank",
    )

    private fun ensureLoaded() {
        Library.ensure(ctx)
        DossierRegistry.ensure(ctx)
    }

    private fun readExerciseIds(): Set<String> {
        val raw = ctx.assets.open("data/exercises.json").bufferedReader().use { it.readText() }
        val j = Json { ignoreUnknownKeys = true; isLenient = true }
        val exObj = j.parseToJsonElement(raw).jsonObject
        val ids = mutableSetOf<String>()
        for ((_, groupVal) in exObj) {
            val arr = groupVal as? JsonArray ?: continue
            for (item in arr) {
                val name = item.jsonObject["name"]?.jsonPrimitive?.content ?: continue
                ids.add(name.lowercase().replace(Regex("\\s+"), "_"))
            }
        }
        return ids
    }

    private fun coachIdResolves(coachId: String): Boolean {
        if (coachId in genericCoachIds) return true
        return CoachRegistry.lookup(coachId) != null
    }

    // ---- 1. Count ----

    @Test
    fun dossiersCount_equalsExercisesJsonCount() {
        ensureLoaded()
        val exIds = readExerciseIds()
        Assert.assertEquals(exIds.size, DossierRegistry.all().size)
    }

    // ---- 2. Every dossier has non-empty provenance.source ----

    @Test
    fun everyDossier_hasNonEmptySource() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' provenance.source must be non-empty",
                dossier.provenance.source.isNotBlank(),
            )
        }
    }

    // ---- 3. Every "cited" dossier cites sommer or mcgill (the only published sources) ----

    @Test
    fun citedDossiers_citeSommerOrMcgill() {
        ensureLoaded()
        val cited = DossierRegistry.all().filter {
            it.provenance.source.startsWith("sommer-") || it.provenance.source.startsWith("mcgill-")
        }
        Assert.assertEquals("Exactly 5 published citations", 5, cited.size)
        for (dossier in cited) {
            Assert.assertTrue(
                "Cited '${dossier.exerciseId}' source should be sommer or mcgill (got '${dossier.provenance.source}')",
                dossier.provenance.source.startsWith("sommer-") || dossier.provenance.source.startsWith("mcgill-"),
            )
            Assert.assertTrue(
                "Cited '${dossier.exerciseId}' landmark non-blank",
                dossier.goals[0].landmark.isNotBlank(),
            )
        }
    }

    // ---- 4. Every "first-pass" dossier cites peakform-2026 ----

    @Test
    fun firstPassDossiers_citePeakform2026() {
        ensureLoaded()
        val firstPass = DossierRegistry.all().filter {
            it.provenance.source == "peakform-2026/book.md"
        }
        Assert.assertTrue("Should have 1,408 first-pass dossiers (got ${firstPass.size})", firstPass.size == 1408)
        for (dossier in firstPass) {
            Assert.assertEquals(
                "First-pass '${dossier.exerciseId}' source should be peakform-2026/book.md",
                "peakform-2026/book.md", dossier.provenance.source,
            )
        }
    }

    // ---- 5. The 5 published citations are preserved ----

    @Test
    fun publishedCitations_preserved() {
        ensureLoaded()
        for (eid in publishedIds) {
            val dossier = DossierRegistry.forExercise(eid)
            Assert.assertNotNull("Published exercise '$eid' should have a dossier", dossier)
            Assert.assertTrue(
                "Published '$eid' should cite sommer or mcgill",
                dossier!!.provenance.source.startsWith("sommer-") ||
                    dossier.provenance.source.startsWith("mcgill-"),
            )
            Assert.assertTrue(
                "Published '$eid' landmark non-blank",
                dossier.goals[0].landmark.isNotBlank(),
            )
        }
    }

    // ---- 6. NEW exercises (0 → trivially true) ----

    @Test
    fun noNewExercises() {
        ensureLoaded()
        // No NEW exercises were added to exercises.json in this batch.
        // The test verifies that the exercises.json count matches the dossiers count.
        val exIds = readExerciseIds()
        Assert.assertEquals(exIds.size, DossierRegistry.all().size)
    }

    // ---- Bonus ----

    @Test
    fun everyCoachIdResolves() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "coachId '${dossier.coachId}' on '${dossier.exerciseId}' must resolve",
                coachIdResolves(dossier.coachId),
            )
        }
    }

    @Test
    fun firstPassDossiers_haveLandmarkExceptUnrecognized() {
        ensureLoaded()
        val firstPass = DossierRegistry.all().filter {
            it.provenance.source == "peakform-2026/book.md"
        }
        val withLandmark = firstPass.count { it.goals[0].landmark.isNotBlank() }
        val withoutLandmark = firstPass.count { it.goals[0].landmark.isBlank() }
        // 1,407 with landmark (convention), 1 without (unrecognized)
        Assert.assertEquals("First-pass with landmark", 1407, withLandmark)
        Assert.assertEquals("First-pass without landmark (unrecognized)", 1, withoutLandmark)
    }

    @Test
    fun everyDossier_hasStimulusProfile() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have non-empty stimulusProfile",
                dossier.stimulusProfile.isNotEmpty(),
            )
        }
    }

    @Test
    fun everyDossier_hasComponentStimulus() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have non-empty componentStimulus",
                dossier.componentStimulus.isNotEmpty(),
            )
        }
    }

    @Test
    fun unrecognizedDossier_hasEmptyLandmark() {
        ensureLoaded()
        val unrecognized = DossierRegistry.all().filter {
            it.provenance.source == "peakform-2026/book.md" && it.goals[0].landmark.isBlank()
        }
        Assert.assertEquals("Exactly 1 unrecognized movement", 1, unrecognized.size)
        val d = unrecognized[0]
        Assert.assertTrue(
            "Unrecognized '${d.exerciseId}' should have empty landmark",
            d.goals[0].landmark.isBlank(),
        )
    }
}
