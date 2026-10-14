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
 * BATCH-37b — Sources + UI wiring.
 *
 * Part 1: 5 new source files were attempted (low-2016, nsca-2016,
 * schoenfeld-2020, contreras-2013, cressey-2008). All 5 were skipped
 * because the sandbox does not have access to the book content and cannot
 * transcribe verbatim tables. No fabricated sources were added.
 *
 * Part 2: Re-ran the citation pass with the same strict complete-phrase
 * matching rules from v37a-v3. Cited count unchanged: 5 (same sources,
 * same matches).
 *
 * Part 3: Wired Workout.kt card and Library.kt card to read
 * DossierRegistry. Cited exercises show goal chip + landmark. Non-cited
 * show "No published source" / "Awaiting source" chip.
 *
 * Part 4: Prerequisite gate shipped but not exercised (all prereqs empty).
 *
 * Tests:
 *   1. dossiers.json count == exercises.json count
 *   2. every key in dossiers.json is a key in exercises.json
 *   3. non-cited dossiers have goal[0].source == "no-source" + landmark == ""
 *   4. cited dossiers have goal[0].source != "no-source" + landmark != ""
 *   5. no non-empty prerequisites, progressions, stimulus, variations
 *   6. every coachId resolves via CoachRegistry or generics
 *   7. cited dossier landmarks are specific (not section headers)
 *   8. non-cited count is 1408 (was 1408 in v37a-v3, unchanged)
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

    private val citedIds = setOf(
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

    // ---- 2. Keys match ----

    @Test
    fun everyDossierKey_isInExercisesJson() {
        ensureLoaded()
        val exIds = readExerciseIds()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' must be in exercises.json",
                dossier.exerciseId in exIds,
            )
        }
    }

    // ---- 3. Non-cited have provisional goals ----

    @Test
    fun nonCitedHaveProvisionalGoals() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            if (dossier.exerciseId !in citedIds) {
                Assert.assertEquals(1, dossier.goals.size)
                Assert.assertEquals("no-source", dossier.goals[0].source)
                Assert.assertTrue(dossier.goals[0].landmark.isBlank())
            }
        }
    }

    // ---- 4. Cited have non-no-source goals with landmarks ----

    @Test
    fun citedHaveCitedGoalsWithLandmarks() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            if (dossier.exerciseId in citedIds) {
                Assert.assertTrue(
                    "Cited '${dossier.exerciseId}' source != 'no-source'",
                    dossier.goals[0].source != "no-source",
                )
                Assert.assertTrue(
                    "Cited '${dossier.exerciseId}' landmark non-blank",
                    dossier.goals[0].landmark.isNotBlank(),
                )
            }
        }
    }

    // ---- 5. No non-empty fields ----

    @Test
    fun noNonEmptyPrerequisites() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(dossier.prerequisites.isEmpty())
        }
    }

    @Test
    fun noNonEmptyProgression() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(dossier.progressionIn.isEmpty())
            Assert.assertTrue(dossier.progressionOut.isEmpty())
        }
    }

    @Test
    fun noNonEmptyStimulus() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(dossier.stimulusProfile.isEmpty())
            Assert.assertTrue(dossier.componentStimulus.isEmpty())
        }
    }

    @Test
    fun noNonEmptyVariations() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(dossier.variations.isEmpty())
        }
    }

    // ---- 6. CoachId resolves ----

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

    // ---- 7. Landmarks are specific strings (not section headers) ----

    @Test
    fun citedLandmarksAreSpecificStrings() {
        ensureLoaded()
        val pullUp = DossierRegistry.forExercise("pull-up")!!
        Assert.assertTrue(
            "Pull-Up landmark should contain 'Tier 5': ${pullUp.goals[0].landmark}",
            pullUp.goals[0].landmark.contains("Tier 5"),
        )
        val plank = DossierRegistry.forExercise("plank")!!
        Assert.assertTrue(
            "Plank landmark should contain 'Front plank': ${plank.goals[0].landmark}",
            plank.goals[0].landmark.contains("Front plank"),
        )
    }

    // ---- 8. Cited count unchanged ----

    @Test
    fun exactly5ExercisesAreCited() {
        ensureLoaded()
        val cited = DossierRegistry.all().filter { it.goals[0].source != "no-source" }
        Assert.assertEquals(5, cited.size)
    }

    @Test
    fun notCitedCountIs1408() {
        ensureLoaded()
        val notCited = DossierRegistry.all().filter { it.goals[0].source == "no-source" }
        Assert.assertEquals(1408, notCited.size)
    }

    // ---- 9. UI wiring: card renders dossier-derived content ----

    @Test
    fun citedDossier_rendersLandmark() {
        ensureLoaded()
        // The test verifies the DATA shape that the card renders, not the
        // Compose tree itself (Compose UI testing requires a ComposeRule
        // which is heavyweight for a data-shape assertion). The card reads
        // dossier.goals[0].landmark and renders it as "Landmark: <value>".
        // Here we verify the data is correct.
        for (eid in citedIds) {
            val dossier = DossierRegistry.forExercise(eid)
            Assert.assertNotNull("Cited exercise '$eid' should have a dossier", dossier)
            Assert.assertTrue(
                "Cited '$eid' should have non-blank landmark",
                dossier!!.goals[0].landmark.isNotBlank(),
            )
        }
    }

    @Test
    fun nonCitedDossier_rendersNoSourceLine() {
        ensureLoaded()
        // The card renders "No published source names this exercise yet."
        // when the dossier is not cited. Here we verify the data condition
        // that triggers that render path.
        for (dossier in DossierRegistry.all()) {
            if (dossier.exerciseId !in citedIds) {
                Assert.assertEquals(
                    "Non-cited '${dossier.exerciseId}' should have source='no-source'",
                    "no-source", dossier.provenance.source,
                )
            }
        }
    }

    // ---- 10. Library chip: cited → "Coached", non-cited → "Awaiting source" ----

    @Test
    fun citedExercises_showCoachedChip() {
        ensureLoaded()
        // The library card shows "Coached" when isCited is true.
        // isCited = dossier != null && dossier.provenance.source != "no-source"
        for (eid in citedIds) {
            val dossier = DossierRegistry.forExercise(eid)
            Assert.assertNotNull(dossier)
            Assert.assertTrue(
                "Cited '$eid' should have source != 'no-source'",
                dossier!!.provenance.source != "no-source",
            )
        }
    }

    @Test
    fun nonCitedExercises_showAwaitingSourceChip() {
        ensureLoaded()
        // The library card shows "Awaiting source" when isCited is false.
        for (dossier in DossierRegistry.all()) {
            if (dossier.exerciseId !in citedIds) {
                Assert.assertEquals(
                    "Non-cited '${dossier.exerciseId}' should have source='no-source'",
                    "no-source", dossier.provenance.source,
                )
            }
        }
    }
}
