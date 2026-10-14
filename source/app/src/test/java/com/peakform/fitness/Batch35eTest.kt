package com.peakform.fitness

import com.peakform.fitness.core.DifficultyGate
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.HistoryEntry
import com.peakform.fitness.core.WorkoutData
import com.peakform.fitness.engine.Library
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-35e — Difficulty gating (prerequisites).
 *
 * School-grade unlock per muscle group. The user does not get medium
 * exercises for a muscle group until they have passed N=3 easy sessions
 * for that muscle. Hard unlocks after N=3 medium sessions.
 *
 * The rule is PER-MUSCLE: for each m in exercise.primaryMuscles, the
 * threshold must be satisfied. So a medium exercise with primaryMuscles
 * [quads, glutes] requires 3 easy sessions counted toward quads AND 3
 * easy sessions counted toward glutes (the sessions can come from any
 * easy exercise whose primaryMuscles contains the relevant muscle).
 *
 * Tests:
 *   1. Fresh state unlocks all easy exercises.
 *   2. Fresh state blocks medium and hard.
 *   3. After 3 easy sessions logged for every primaryMuscle, the medium
 *      exercise unlocks.
 *   4. After 3 medium sessions logged for every primaryMuscle, the hard
      exercise unlocks.
 *   5. An exercise with no taxonomy is unlocked.
 *   6. Progress counts are correct (current, required).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch35eTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    /** Build a single HistoryEntry that counts as one session toward a
     *  muscle's tier count (sets >= 1, not skipped). */
    private fun session(weight: Double = 100.0, sets: Int = 3, skipped: Boolean = false): HistoryEntry =
        HistoryEntry(
            date = "2026-10-14",
            weight = weight,
            sets = sets,
            reps = listOf(5.0, 5.0, 5.0),
            skipped = skipped,
        )

    /** Build a WorkoutData with the given exercise records (id → list of history entries). */
    private fun stateWith(exercises: Map<String, List<HistoryEntry>>): WorkoutData {
        val exMap = exercises.mapValues { (_, entries) ->
            ExerciseRecord(history = entries)
        }
        return WorkoutData(exercises = exMap)
    }

    /**
     * Pick a (easyId, mediumId, hardId) triple where:
     *   - the medium exercise has exactly ONE primaryMuscle (simplifies the gate)
     *   - the hard exercise also has exactly ONE primaryMuscle, the SAME muscle
     *   - the easy exercise's primaryMuscles contains that muscle
     *
     * All three share the same single primaryMuscle m. The medium's
     * primaryMuscles == [m]; the hard's primaryMuscles == [m]; the easy's
     * primaryMuscles contains m (may have others, but m is present).
     */
    private fun findTriplet(): Triple<String, String, String> {
        val candidates = mutableListOf<Triple<String, String, String>>()
        for ((mId, mTax) in Library.taxonomy) {
            if (mTax.difficulty != "medium") continue
            if (mTax.primaryMuscles.size != 1) continue
            val m = mTax.primaryMuscles.first()
            // Find an easy exercise that has m in its primaryMuscles.
            val easyId = Library.taxonomy.entries.firstOrNull { (eid, et) ->
                eid != mId && et.difficulty == "easy" && m in et.primaryMuscles
            }?.key ?: continue
            // Find a hard exercise with exactly ONE primaryMuscle == m.
            val hardId = Library.taxonomy.entries.firstOrNull { (hid, ht) ->
                hid != mId && hid != easyId
                    && ht.difficulty == "hard"
                    && ht.primaryMuscles.size == 1
                    && ht.primaryMuscles.first() == m
            }?.key ?: continue
            candidates.add(Triple(easyId, mId, hardId))
        }
        Assert.assertTrue(
            "Could not find a (easy, medium, hard) triplet sharing a single primaryMuscle " +
                "(medium and hard must each have exactly one primaryMuscle == m; easy must include m)",
            candidates.isNotEmpty(),
        )
        return candidates.first()
    }

    @Test
    fun freshState_unlocksAllEasyExercises() {
        Library.ensure(ctx)
        val (easyId, _, _) = findTriplet()
        val state = WorkoutData()  // fresh, no exercise records
        val r = DifficultyGate.isUnlocked(easyId, state)
        Assert.assertTrue("easy exercise should be unlocked on fresh state: $r", r.unlocked)
        Assert.assertTrue("reason should mention Easy: ${r.reason}", r.reason.contains("Easy", ignoreCase = true))
        Assert.assertNull("no blocking muscle on easy", r.blockingMuscle)
    }

    @Test
    fun freshState_blocksMediumAndHard() {
        Library.ensure(ctx)
        val (_, mediumId, hardId) = findTriplet()
        val state = WorkoutData()
        val rMed = DifficultyGate.isUnlocked(mediumId, state)
        Assert.assertFalse("medium exercise should be blocked on fresh state: $rMed", rMed.unlocked)
        Assert.assertNotNull("medium should have a blocking muscle", rMed.blockingMuscle)
        Assert.assertEquals(
            "progress should be (0, ${DifficultyGate.UNLOCK_THRESHOLD}) on fresh state",
            0 to DifficultyGate.UNLOCK_THRESHOLD, rMed.progress,
        )

        val rHard = DifficultyGate.isUnlocked(hardId, state)
        Assert.assertFalse("hard exercise should be blocked on fresh state: $rHard", rHard.unlocked)
        Assert.assertNotNull("hard should have a blocking muscle", rHard.blockingMuscle)
        Assert.assertEquals(
            "progress should be (0, ${DifficultyGate.UNLOCK_THRESHOLD}) on fresh state",
            0 to DifficultyGate.UNLOCK_THRESHOLD, rHard.progress,
        )
    }

    @Test
    fun afterThreeEasySessions_mediumUnlocks() {
        Library.ensure(ctx)
        val (easyId, mediumId, _) = findTriplet()
        // The medium exercise has exactly one primaryMuscle (enforced by findTriplet).
        // The easy exercise shares that muscle. Log 3 easy sessions.
        val state = stateWith(mapOf(
            easyId to listOf(session(), session(), session()),
        ))
        val r = DifficultyGate.isUnlocked(mediumId, state)
        Assert.assertTrue(
            "medium should unlock after 3 easy sessions: $r",
            r.unlocked,
        )
        Assert.assertNull("no blocking muscle after unlock", r.blockingMuscle)
    }

    @Test
    fun afterThreeMediumSessions_hardUnlocks() {
        Library.ensure(ctx)
        val (_, mediumId, hardId) = findTriplet()
        // The hard exercise has exactly one primaryMuscle (enforced by findTriplet).
        // The medium exercise shares that muscle. Log 3 medium sessions.
        val state = stateWith(mapOf(
            mediumId to listOf(session(weight = 200.0), session(weight = 200.0), session(weight = 200.0)),
        ))
        val r = DifficultyGate.isUnlocked(hardId, state)
        Assert.assertTrue(
            "hard should unlock after 3 medium sessions: $r",
            r.unlocked,
        )
        Assert.assertNull("no blocking muscle after unlock", r.blockingMuscle)
    }

    @Test
    fun exerciseWithNoTaxonomy_isUnlocked() {
        Library.ensure(ctx)
        val state = WorkoutData()
        val r = DifficultyGate.isUnlocked("zzz_not_a_real_exercise_id_xyz_123", state)
        Assert.assertTrue("no-taxonomy exercise should be unlocked by default: $r", r.unlocked)
        Assert.assertTrue(
            "reason should mention 'no taxonomy': ${r.reason}",
            r.reason.contains("no taxonomy", ignoreCase = true),
        )
        Assert.assertNull("no blocking muscle on missing taxonomy", r.blockingMuscle)
        Assert.assertEquals("progress (0, 0)", 0 to 0, r.progress)
    }

    @Test
    fun progressCountsAreCorrect() {
        Library.ensure(ctx)
        val (easyId, mediumId, _) = findTriplet()
        // Fresh state → (0, 3)
        val fresh = WorkoutData()
        val r0 = DifficultyGate.isUnlocked(mediumId, fresh)
        Assert.assertEquals("fresh progress", 0 to DifficultyGate.UNLOCK_THRESHOLD, r0.progress)

        // 1 session logged → (1, 3)
        val state1 = stateWith(mapOf(easyId to listOf(session())))
        val r1 = DifficultyGate.isUnlocked(mediumId, state1)
        Assert.assertEquals(
            "after 1 easy session, progress should be (1, ${DifficultyGate.UNLOCK_THRESHOLD}): $r1",
            1 to DifficultyGate.UNLOCK_THRESHOLD, r1.progress,
        )
        Assert.assertFalse("still blocked after 1 session", r1.unlocked)

        // 2 sessions → (2, 3)
        val state2 = stateWith(mapOf(easyId to listOf(session(), session())))
        val r2 = DifficultyGate.isUnlocked(mediumId, state2)
        Assert.assertEquals(
            "after 2 easy sessions, progress should be (2, ${DifficultyGate.UNLOCK_THRESHOLD}): $r2",
            2 to DifficultyGate.UNLOCK_THRESHOLD, r2.progress,
        )
        Assert.assertFalse("still blocked after 2 sessions", r2.unlocked)
    }

    @Test
    fun unlockThresholdConstantIsThree() {
        // The spec said N=3. Codify it so future tuning goes through this constant.
        Assert.assertEquals(3, DifficultyGate.UNLOCK_THRESHOLD)
    }

    @Test
    fun skippedSessionsDoNotCount() {
        Library.ensure(ctx)
        val (easyId, mediumId, _) = findTriplet()
        // 3 sessions, but all skipped → should NOT unlock.
        val state = stateWith(mapOf(
            easyId to listOf(session(skipped = true), session(skipped = true), session(skipped = true)),
        ))
        val r = DifficultyGate.isUnlocked(mediumId, state)
        Assert.assertFalse(
            "3 skipped sessions should not count toward the threshold: $r",
            r.unlocked,
        )
        Assert.assertEquals(
            "progress should be (0, ${DifficultyGate.UNLOCK_THRESHOLD}) when all sessions are skipped",
            0 to DifficultyGate.UNLOCK_THRESHOLD, r.progress,
        )
    }

    @Test
    fun sessionsWithZeroSetsDoNotCount() {
        Library.ensure(ctx)
        val (easyId, mediumId, _) = findTriplet()
        // 3 sessions, but sets=0 → should NOT count (spec: "at least one completed set").
        val state = stateWith(mapOf(
            easyId to listOf(session(sets = 0), session(sets = 0), session(sets = 0)),
        ))
        val r = DifficultyGate.isUnlocked(mediumId, state)
        Assert.assertFalse(
            "3 sessions with sets=0 should not count: $r",
            r.unlocked,
        )
    }
}
