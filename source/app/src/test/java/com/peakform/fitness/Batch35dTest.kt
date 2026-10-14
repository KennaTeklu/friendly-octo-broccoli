package com.peakform.fitness

import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.Library.ExerciseTaxonomy
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
 * BATCH-35d — Exercise taxonomy.
 *
 * Every exercise in exercises.json gains six taxonomy fields backed by
 * data/exercise_taxonomy.json + a Library.taxonomyFor(id) loader. This test
 * asserts:
 *   - exercises.json size == exercise_taxonomy.json size (full coverage)
 *   - every exercise id in exercises.json has an entry in exercise_taxonomy.json
 *   - every category is one of the seven allowed values
 *   - every difficulty is one of the three allowed values
 *   - every equipment value is one of the eight allowed
 *   - every component weight is in 0..5 and the component name is in the 11-name enum
 *   - primaryMuscles size 0..3, secondaryMuscles size 0..4 (spec bounds)
 *   - Library.taxonomyFor(id) returns non-null for every known id; null for unknown
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch35dTest {

    private val allowedCategories = setOf(
        "compound", "isolation", "cardio", "mobility", "recovery",
        "time_based", "bodyweight",
    )
    private val allowedDifficulties = setOf("easy", "medium", "hard")
    private val allowedEquipment = setOf(
        "barbell", "dumbbell", "machine", "cable", "band",
        "bodyweight", "kettlebell", "other",
    )
    private val allowedComponents = setOf(
        "Strength", "Hypertrophy", "Endurance", "Power", "Flexibility",
        "Skill", "Balance", "Agility", "Speed", "BodyComposition", "Longevity",
    )

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    /** Read every exercise id (name → lowercase + whitespace→_) from exercises.json. */
    private fun readExerciseIds(): Set<String> {
        val raw = ctx.assets.open("data/exercises.json").bufferedReader().use { it.readText() }
        val j = Json { ignoreUnknownKeys = true; isLenient = true }
        val exObj = j.parseToJsonElement(raw).jsonObject
        val ids = mutableSetOf<String>()
        for ((_, groupVal) in exObj) {
            val arr = groupVal as? JsonArray ?: continue
            for (item in arr) {
                val name = item.jsonObject["name"]?.jsonPrimitive?.content ?: continue
                // Kotlin LibraryExercise.id: name.lowercase().replace(Regex("\\s+"), "_")
                val id = name.lowercase().replace(Regex("\\s+"), "_")
                ids.add(id)
            }
        }
        return ids
    }

    /** Read every key from exercise_taxonomy.json. */
    private fun readTaxonomyIds(): Set<String> {
        val raw = ctx.assets.open("data/exercise_taxonomy.json").bufferedReader().use { it.readText() }
        val j = Json { ignoreUnknownKeys = true; isLenient = true }
        return j.parseToJsonElement(raw).jsonObject.keys
    }

    @Test
    fun taxonomy_hasFullCoverage() {
        Library.ensure(ctx)
        val exIds = readExerciseIds()
        val taxIds = readTaxonomyIds()

        Assert.assertEquals(
            "exercises.json exercise count must equal exercise_taxonomy.json entry count",
            exIds.size, taxIds.size,
        )
        val missing = exIds - taxIds
        Assert.assertTrue(
            "Every exercise id must have a taxonomy entry. Missing (first 10): ${missing.take(10)}",
            missing.isEmpty(),
        )
        val orphans = taxIds - exIds
        Assert.assertTrue(
            "No orphan taxonomy entries (in taxonomy but not in exercises.json). Orphans (first 10): ${orphans.take(10)}",
            orphans.isEmpty(),
        )
    }

    @Test
    fun everyCategoryIsValid() {
        Library.ensure(ctx)
        for ((id, t) in Library.taxonomy) {
            Assert.assertTrue(
                "exercise $id has category='${t.category}' (must be one of $allowedCategories)",
                t.category in allowedCategories,
            )
        }
    }

    @Test
    fun everyDifficultyIsValid() {
        Library.ensure(ctx)
        for ((id, t) in Library.taxonomy) {
            Assert.assertTrue(
                "exercise $id has difficulty='${t.difficulty}' (must be one of $allowedDifficulties)",
                t.difficulty in allowedDifficulties,
            )
        }
    }

    @Test
    fun everyEquipmentIsValid() {
        Library.ensure(ctx)
        for ((id, t) in Library.taxonomy) {
            Assert.assertTrue(
                "exercise $id equipment list must be non-empty",
                t.equipment.isNotEmpty(),
            )
            for (e in t.equipment) {
                Assert.assertTrue(
                    "exercise $id has equipment='$e' (must be one of $allowedEquipment)",
                    e in allowedEquipment,
                )
            }
        }
    }

    @Test
    fun everyComponentIsValidAndWeighted() {
        Library.ensure(ctx)
        for ((id, t) in Library.taxonomy) {
            Assert.assertTrue(
                "exercise $id components must be non-empty (primary at weight 3)",
                t.components.isNotEmpty(),
            )
            for ((name, weight) in t.components) {
                Assert.assertTrue(
                    "exercise $id component '$name' (must be in $allowedComponents)",
                    name in allowedComponents,
                )
                Assert.assertTrue(
                    "exercise $id component '$name' weight=$weight must be in 0..5",
                    weight in 0..5,
                )
            }
        }
    }

    @Test
    fun primaryAndSecondaryMusclesBoundsRespected() {
        Library.ensure(ctx)
        for ((id, t) in Library.taxonomy) {
            Assert.assertTrue(
                "exercise $id primaryMuscles size=${t.primaryMuscles.size} must be 0..3",
                t.primaryMuscles.size in 0..3,
            )
            Assert.assertTrue(
                "exercise $id secondaryMuscles size=${t.secondaryMuscles.size} must be 0..4",
                t.secondaryMuscles.size in 0..4,
            )
            // No overlap between primary and secondary
            val primary = t.primaryMuscles.toSet()
            val secondary = t.secondaryMuscles.toSet()
            val overlap = primary intersect secondary
            Assert.assertTrue(
                "exercise $id primary/secondary muscles overlap: $overlap",
                overlap.isEmpty(),
            )
        }
    }

    @Test
    fun taxonomyFor_returnsEntryForKnownId() {
        Library.ensure(ctx)
        // "barbell_back_squat_(high_bar)" is the first exercise in exercises.json
        val id = "barbell_back_squat_(high_bar)"
        val t: ExerciseTaxonomy? = Library.taxonomyFor(id)
        Assert.assertNotNull("taxonomyFor($id) returned null", t)
        Assert.assertTrue("primaryMuscles non-empty", t!!.primaryMuscles.isNotEmpty())
        Assert.assertEquals("compound", t.category)
        Assert.assertTrue("equipment includes barbell", "barbell" in t.equipment)
    }

    @Test
    fun taxonomyFor_returnsNullForUnknownId() {
        Library.ensure(ctx)
        Assert.assertNull(
            Library.taxonomyFor("zzz_not_a_real_exercise_id_xyz_123"),
        )
    }

    @Test
    fun taxonomyFor_doesNotThrowOnWeirdInput() {
        Library.ensure(ctx)
        // Empty string, whitespace, special chars — never throws.
        Assert.assertNull(Library.taxonomyFor(""))
        Assert.assertNull(Library.taxonomyFor("   "))
        Assert.assertNull(Library.taxonomyFor("%%weird%%"))
    }
}
