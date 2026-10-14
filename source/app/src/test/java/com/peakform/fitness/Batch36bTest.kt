package com.peakform.fitness

import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.HistoryEntry
import com.peakform.fitness.core.WorkoutData
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.screens.loadLoggerHeaderData
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-36b — Logger reads taxonomy.
 *
 * The LoggerPanel shows taxonomy fields per exercise at the top of the
 * set-logging inputs: category, difficulty (with lock state from
 * DifficultyGate), primaryMuscles chips, secondaryMuscles chips, equipment
 * chips, the components map (via the shared ComponentChipsRow from v36a),
 * and the performed indicator (checkmark or "first time" label).
 *
 * The 1RM test panel header line now also shows the exercise's category +
 * difficulty.
 *
 * Tests exercise the pure `loadLoggerHeaderData(exerciseId, state)` helper
 * (in `WorkoutLogger.kt`) without a Compose host:
 *   1. Logger header renders category and difficulty for a known exercise.
 *   2. Missing taxonomy → header data is null (no header rendered, no crash).
 *   3. performed is false on fresh state (no history).
 *   4. performed is true after one logged set (HistoryEntry with sets >= 1,
 *      not skipped).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch36bTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    /** A single completed-set HistoryEntry. */
    private fun session(weight: Double = 100.0, sets: Int = 3, skipped: Boolean = false): HistoryEntry =
        HistoryEntry(
            date = "2026-10-14",
            weight = weight,
            sets = sets,
            reps = listOf(5.0, 5.0, 5.0),
            skipped = skipped,
        )

    /** Build a WorkoutData with the given exercise records (id → history list). */
    private fun stateWith(exercises: Map<String, List<HistoryEntry>>): WorkoutData {
        val exMap = exercises.mapValues { (_, entries) -> ExerciseRecord(history = entries) }
        return WorkoutData(exercises = exMap)
    }

    /** Find a real taxonomy entry id by difficulty. */
    private fun idForDifficulty(difficulty: String): String {
        val entry = Library.taxonomy.entries.firstOrNull { it.value.difficulty == difficulty }
        Assert.assertNotNull("Taxonomy should have an entry with difficulty=$difficulty", entry)
        return entry!!.key
    }

    // ---- 1. Logger header renders category and difficulty ----

    @Test
    fun loggerHeader_rendersCategoryAndDifficulty() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        val data = loadLoggerHeaderData(easyId, WorkoutData())
        Assert.assertNotNull("Header data should be non-null for a known exercise id", data)
        Assert.assertTrue(
            "category must be non-blank: ${data!!.category}",
            data.category.isNotBlank(),
        )
        Assert.assertEquals("difficulty should be 'easy'", "easy", data.difficulty)
        // Verify the category is one of the seven allowed values (sanity).
        val allowedCategories = setOf(
            "compound", "isolation", "cardio", "mobility",
            "recovery", "time_based", "bodyweight",
        )
        Assert.assertTrue(
            "category '${data.category}' must be in the allowed set",
            data.category in allowedCategories,
        )
    }

    @Test
    fun loggerHeader_rendersForEveryDifficultyTier() {
        Library.ensure(ctx)
        // Sanity: every difficulty tier has at least one taxonomy entry, and
        // the header data loads correctly for each.
        for (tier in listOf("easy", "medium", "hard")) {
            val id = idForDifficulty(tier)
            val data = loadLoggerHeaderData(id, WorkoutData())
            Assert.assertNotNull("Header data should be non-null for $tier exercise", data)
            Assert.assertEquals(tier, data!!.difficulty)
        }
    }

    @Test
    fun loggerHeader_rendersAllTaxonomyFields() {
        Library.ensure(ctx)
        // Pick an entry that has all 6 fields populated.
        val entry = Library.taxonomy.entries.firstOrNull {
            it.value.primaryMuscles.isNotEmpty() &&
                it.value.equipment.isNotEmpty() &&
                it.value.components.isNotEmpty()
        }
        Assert.assertNotNull("Taxonomy should have an entry with all fields populated", entry)
        val data = loadLoggerHeaderData(entry!!.key, WorkoutData())
        Assert.assertNotNull(data)
        Assert.assertTrue("primaryMuscles non-empty", data!!.primaryMuscles.isNotEmpty())
        Assert.assertTrue("equipment non-empty", data.equipment.isNotEmpty())
        Assert.assertTrue("components non-empty", data.components.isNotEmpty())
        Assert.assertTrue("category non-blank", data.category.isNotBlank())
        Assert.assertTrue("difficulty non-blank", data.difficulty.isNotBlank())
    }

    // ---- 2. Missing taxonomy → no header, no crash ----

    @Test
    fun missingTaxonomy_returnsNullAndDoesNotCrash() {
        Library.ensure(ctx)
        val state = WorkoutData()
        val data = loadLoggerHeaderData("zzz_not_a_real_exercise_id_xyz_123", state)
        Assert.assertNull("Missing taxonomy → null header data", data)
        // The composable LoggerTaxonomyHeader early-returns when data is null,
        // so no header renders. The helper never throws.
    }

    @Test
    fun missingTaxonomy_doesNotThrowOnWeirdInput() {
        Library.ensure(ctx)
        val state = WorkoutData()
        // Empty string, whitespace, special chars — never throws.
        Assert.assertNull(loadLoggerHeaderData("", state))
        Assert.assertNull(loadLoggerHeaderData("   ", state))
        Assert.assertNull(loadLoggerHeaderData("%%weird%%", state))
    }

    // ---- 3. performed is false on fresh state ----

    @Test
    fun performed_isFalseOnFreshState() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        val fresh = WorkoutData()  // no exercise records
        val data = loadLoggerHeaderData(easyId, fresh)
        Assert.assertNotNull(data)
        Assert.assertFalse(
            "performed should be false on fresh state (no history for this exercise)",
            data!!.performed,
        )
    }

    @Test
    fun performed_isFalseWhenOtherExercisesHaveHistoryButNotThisOne() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        // A different exercise has history, but easyId has none.
        val otherId = Library.taxonomy.entries.firstOrNull {
            it.key != easyId && it.value.difficulty == "easy"
        }?.key
        Assert.assertNotNull(otherId)
        val state = stateWith(mapOf(otherId!! to listOf(session())))
        val data = loadLoggerHeaderData(easyId, state)
        Assert.assertFalse(
            "performed should be false when only a DIFFERENT exercise has history",
            data!!.performed,
        )
    }

    // ---- 4. performed is true after one logged set ----

    @Test
    fun performed_isTrueAfterOneLoggedSet() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        val state = stateWith(mapOf(easyId to listOf(session())))
        val data = loadLoggerHeaderData(easyId, state)
        Assert.assertTrue(
            "performed should be true after one logged set with sets>=1, not skipped",
            data!!.performed,
        )
    }

    @Test
    fun performed_isFalseWhenOnlySkippedEntriesExist() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        val state = stateWith(mapOf(easyId to listOf(session(skipped = true))))
        val data = loadLoggerHeaderData(easyId, state)
        Assert.assertFalse(
            "performed should be false when the only history entry is skipped",
            data!!.performed,
        )
    }

    @Test
    fun performed_isFalseWhenOnlyZeroSetEntriesExist() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        val state = stateWith(mapOf(easyId to listOf(session(sets = 0))))
        val data = loadLoggerHeaderData(easyId, state)
        Assert.assertFalse(
            "performed should be false when the only history entry has sets=0",
            data!!.performed,
        )
    }

    @Test
    fun performed_isTrueWhenAnyEntryHasCompletedSets() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        // Mixed history: one skipped, one zero-set, one completed — performed=true.
        val state = stateWith(mapOf(
            easyId to listOf(
                session(skipped = true),
                session(sets = 0),
                session(sets = 3),
            ),
        ))
        val data = loadLoggerHeaderData(easyId, state)
        Assert.assertTrue(
            "performed should be true when ANY history entry has sets>=1 and !skipped",
            data!!.performed,
        )
    }

    // ---- Bonus: unlock state is exposed ----

    @Test
    fun loggerHeader_exposesUnlockState() {
        Library.ensure(ctx)
        val easyId = idForDifficulty("easy")
        val data = loadLoggerHeaderData(easyId, WorkoutData())
        // Easy exercises are always unlocked per DifficultyGate.
        Assert.assertTrue("easy exercise should be unlocked", data!!.unlocked)
        Assert.assertTrue(
            "unlock reason should mention 'easy' or 'unlocked'",
            data.unlockReason.contains("easy", ignoreCase = true) ||
                data.unlockReason.contains("unlocked", ignoreCase = true),
        )

        // Medium/hard on fresh state should be locked.
        val mediumId = idForDifficulty("medium")
        val mediumData = loadLoggerHeaderData(mediumId, WorkoutData())
        Assert.assertFalse("medium should be locked on fresh state", mediumData!!.unlocked)
        Assert.assertTrue(
            "unlock reason should mention the requirement",
            mediumData.unlockReason.contains("Need", ignoreCase = true),
        )
    }
}
