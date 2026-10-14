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
 * BATCH-37a (v3) — Dossiers for ALL 1,413 exercises in exercises.json.
 *
 * Previous attempts:
 *   v1: authored 15 dossiers for a "push-up chain" — 14 of 15 ids did not
 *       exist in exercises.json. Reverted.
 *   v2: authored 1,413 dossiers but fabricated goals — every dossier got a
 *       "cited" goal with landmark = "Notes" (a section header, not a
 *       landmark). 176 "cited" exercises were actually keyword-matched
 *       (e.g. "1.5 Rep Squat" was marked "cited by prilepin-1974" because
 *       Prilepin names "squat" as a family). Rejected.
 *
 * v3: strict complete-phrase citation matching. A source CITES an exercise
 *     ONLY IF the exercise name appears as a COMPLETE PHRASE in the source
 *     AND the source names a specific landmark for that exercise. 5
 *     exercises are cited (3 from Sommer, 2 from McGill). 1,408 are
 *     structural-only (no-source, provisional goal).
 *
 * Tests:
 *   1. dossiers.json has exactly exercises.json count of entries
 *   2. every key in dossiers.json is a key in exercises.json
 *   3. every exercise not in the cited set has goals.size == 1 AND
 *      goals[0].source == "no-source" AND goals[0].landmark == ""
 *   4. every cited exercise has goals[0].source != "no-source" AND
 *      goals[0].landmark != ""
 *   5. no dossier has non-empty prerequisites
 *   6. no dossier has non-empty progressionIn or progressionOut
 *   7. no dossier has non-empty stimulusProfile or componentStimulus
 *   8. every coachId resolves via CoachRegistry
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch37aTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    private val genericCoachIds = setOf(
        "generic_chest", "generic_back", "generic_legs",
        "generic_shoulders", "generic_arms", "generic_core",
        "generic_cardio", "generic_mobility",
    )

    /** The 5 cited exercise ids (strict complete-phrase + landmark). */
    private val citedIds = setOf(
        "pull-up",       // Sommer tier 5: 3x3-8, 120s rest
        "chin-up",       // Sommer tier 4: 3x3-8, 120s rest
        "archer_pull-up", // Sommer tier 8: 3x2-4, 150s rest
        "plank",         // McGill front plank: 60s/120s/180+ (male)
        "side_plank",    // McGill side plank: 45s/90s/120+ (male)
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

    // ---- 1. Dossiers count == exercises.json count ----

    @Test
    fun dossiersCount_equalsExercisesJsonCount() {
        ensureLoaded()
        val exIds = readExerciseIds()
        val dossiers = DossierRegistry.all()
        Assert.assertEquals(
            "DossierRegistry.all().size should equal exercises.json exercise count",
            exIds.size, dossiers.size,
        )
    }

    // ---- 2. Every key in dossiers.json is a key in exercises.json ----

    @Test
    fun everyDossierKey_isInExercisesJson() {
        ensureLoaded()
        val exIds = readExerciseIds()
        val dossiers = DossierRegistry.all()
        for (dossier in dossiers) {
            Assert.assertTrue(
                "Dossier exerciseId '${dossier.exerciseId}' must be a real key in exercises.json",
                dossier.exerciseId in exIds,
            )
        }
    }

    @Test
    fun everyExerciseId_hasDossier() {
        ensureLoaded()
        val exIds = readExerciseIds()
        for (id in exIds) {
            Assert.assertNotNull(
                "Exercise id '$id' should have a dossier",
                DossierRegistry.forExercise(id),
            )
        }
    }

    // ---- 3. Non-cited exercises have provisional goals ----

    @Test
    fun nonCitedExercises_haveProvisionalGoals() {
        ensureLoaded()
        val dossiers = DossierRegistry.all()
        for (dossier in dossiers) {
            if (dossier.exerciseId !in citedIds) {
                Assert.assertEquals(
                    "Non-cited '${dossier.exerciseId}' should have exactly 1 goal",
                    1, dossier.goals.size,
                )
                Assert.assertEquals(
                    "Non-cited '${dossier.exerciseId}' goal source should be 'no-source'",
                    "no-source", dossier.goals[0].source,
                )
                Assert.assertTrue(
                    "Non-cited '${dossier.exerciseId}' goal landmark should be empty",
                    dossier.goals[0].landmark.isBlank(),
                )
            }
        }
    }

    // ---- 4. Cited exercises have non-no-source goals with non-empty landmarks ----

    @Test
    fun citedExercises_haveCitedGoalsWithLandmarks() {
        ensureLoaded()
        val dossiers = DossierRegistry.all()
        for (dossier in dossiers) {
            if (dossier.exerciseId in citedIds) {
                Assert.assertTrue(
                    "Cited '${dossier.exerciseId}' should have goals[0].source != 'no-source' (got '${dossier.goals[0].source}')",
                    dossier.goals[0].source != "no-source",
                )
                Assert.assertTrue(
                    "Cited '${dossier.exerciseId}' should have goals[0].landmark non-blank (got '${dossier.goals[0].landmark}')",
                    dossier.goals[0].landmark.isNotBlank(),
                )
            }
        }
    }

    @Test
    fun exactly5ExercisesAreCited() {
        ensureLoaded()
        val dossiers = DossierRegistry.all()
        val cited = dossiers.filter { it.goals[0].source != "no-source" }
        Assert.assertEquals(
            "Exactly 5 exercises should be cited (got ${cited.size})",
            5, cited.size,
        )
        val citedIdsActual = cited.map { it.exerciseId }.toSet()
        Assert.assertEquals(citedIds, citedIdsActual)
    }

    // ---- 5. No non-empty prerequisites ----

    @Test
    fun noDossierHasNonEmptyPrerequisites() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have empty prerequisites",
                dossier.prerequisites.isEmpty(),
            )
        }
    }

    // ---- 6. No non-empty progressionIn/Out ----

    @Test
    fun noDossierHasNonEmptyProgression() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have empty progressionIn",
                dossier.progressionIn.isEmpty(),
            )
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have empty progressionOut",
                dossier.progressionOut.isEmpty(),
            )
        }
    }

    // ---- 7. No non-empty stimulusProfile or componentStimulus ----

    @Test
    fun noDossierHasNonEmptyStimulus() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have empty stimulusProfile",
                dossier.stimulusProfile.isEmpty(),
            )
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have empty componentStimulus",
                dossier.componentStimulus.isEmpty(),
            )
        }
    }

    // ---- 8. Every coachId resolves ----

    @Test
    fun everyCoachIdResolves() {
        ensureLoaded()
        val dossiers = DossierRegistry.all()
        val unresolved = mutableListOf<String>()
        for (dossier in dossiers) {
            if (!coachIdResolves(dossier.coachId)) {
                unresolved.add("${dossier.exerciseId} → coachId='${dossier.coachId}'")
            }
        }
        Assert.assertTrue(
            "Unresolved coachIds (first 10): ${unresolved.take(10)}",
            unresolved.isEmpty(),
        )
    }

    // ---- Bonus: structural integrity ----

    @Test
    fun everyDossierHasVersionOne() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertEquals(
                "Dossier '${dossier.exerciseId}' version should be 1",
                1, dossier.version,
            )
        }
    }

    @Test
    fun everyDossierHasEmptyVariations() {
        ensureLoaded()
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(
                "Dossier '${dossier.exerciseId}' should have empty variations",
                dossier.variations.isEmpty(),
            )
        }
    }

    @Test
    fun registryForExercise_returnsNullForUnknownId() {
        ensureLoaded()
        Assert.assertNull(
            DossierRegistry.forExercise("zzz_not_a_real_exercise_xyz_123"),
        )
    }

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
}
