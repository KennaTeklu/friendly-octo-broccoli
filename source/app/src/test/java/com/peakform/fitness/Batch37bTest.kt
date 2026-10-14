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
 * BATCH-37b (updated for v37c dossiers).
 *
 * v37c changed the dossiers.json shape: all 1,408 "no-source" entries now
 * cite peakform-2026/book.md with convention landmarks + stimulus profiles.
 * The 5 published citations (sommer/mcgill) are preserved.
 *
 * Updated assertions to match the new shape.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch37bTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    private val genericCoachIds = setOf(
        "generic_chest", "generic_back", "generic_legs",
        "generic_shoulders", "generic_arms", "generic_core",
        "generic_cardio", "generic_mobility",
    )

    private val citedIds = setOf("pull-up", "chin-up", "archer_pull-up", "plank", "side_plank")

    private fun ensureLoaded() {
        Library.ensure(ctx)
        DossierRegistry.ensure(ctx)
    }

    private fun coachIdResolves(coachId: String): Boolean {
        if (coachId in genericCoachIds) return true
        return CoachRegistry.lookup(coachId) != null
    }

    @Test
    fun dossiersCount_equalsExercisesJsonCount() {
        ensureLoaded()
        val raw = ctx.assets.open("data/exercises.json").bufferedReader().use { it.readText() }
        val j = Json { ignoreUnknownKeys = true; isLenient = true }
        val exObj = j.parseToJsonElement(raw).jsonObject
        var count = 0
        for ((_, groupVal) in exObj) {
            val arr = groupVal as? JsonArray ?: continue
            count += arr.size
        }
        Assert.assertEquals(count, DossierRegistry.all().size)
    }

    @Test
    fun everyCoachIdResolves() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(coachIdResolves(dossier.coachId))
        }
    }

    @Test
    fun publishedCitations_preserved() {
        ensureLoaded()
        for (eid in citedIds) {
            val dossier = DossierRegistry.forExercise(eid)!!
            Assert.assertTrue(dossier.provenance.source.startsWith("sommer-") || dossier.provenance.source.startsWith("mcgill-"))
            Assert.assertTrue(dossier.goals[0].landmark.isNotBlank())
        }
    }

    @Test
    fun peakformDossiers_haveSource() {
        ensureLoaded()
        val peakform = DossierRegistry.all().filter { it.provenance.source == "peakform-2026/book.md" }
        Assert.assertEquals(1408, peakform.size)
    }

    @Test
    fun noDossierHasEmptyProvenanceSource() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(dossier.provenance.source.isNotBlank())
        }
    }
}
