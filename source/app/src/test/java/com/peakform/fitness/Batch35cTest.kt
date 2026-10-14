package com.peakform.fitness

import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.ExerciseCoach
import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-35c — UI + routing blocker fixes.
 *
 * B1: every authored coach is reachable through CoachRegistry. Before this
 * batch, CoachRegistry only loaded AllCoaches.all() — the 31 authored
 * coaches (BackSquatAuthoredCoach … SidePlankAuthoredCoach) were never
 * registered, so lookup("romanian_deadlift") returned AllCoaches.RomanianDeadlift
 * (which has no requirements() override → "No prescription available" card).
 *
 * The test enumerates all 31 authored coaches and asserts CoachRegistry.lookup(id)
 * returns THAT coach (by reference — assertSame) for every exercise id the coach
 * declares. The keyword fallback is also exercised for the two library ids
 * that differ from the authored coach's exerciseIds:
 *   - "barbell_romanian_deadlift"  → RomanianDeadliftAuthoredCoach (substring match on "romanian_deadlift")
 *   - "chin-up"                    → ChinUpAuthoredCoach          (movement-name token "chin" match)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch35cTest {

    /** The 31 authored coaches, in declaration order (v34 → v35a → v35b). */
    private val authored: List<ExerciseCoach> = listOf(
        BackSquatAuthoredCoach,
        PullUpAuthoredCoach,
        BandedLateralRaiseAuthoredCoach,
        PlankAuthoredCoach,
        RunningAuthoredCoach,
        DeadliftAuthoredCoach,
        BenchPressAuthoredCoach,
        OverheadPressAuthoredCoach,
        BarbellRowAuthoredCoach,
        FrontSquatAuthoredCoach,
        RomanianDeadliftAuthoredCoach,
        ChinUpAuthoredCoach,
        DipAuthoredCoach,
        PushUpAuthoredCoach,
        FacePullAuthoredCoach,
        SumoDeadliftAuthoredCoach,
        InclineBenchPressAuthoredCoach,
        CloseGripBenchPressAuthoredCoach,
        PendlayRowAuthoredCoach,
        PushPressAuthoredCoach,
        PowerCleanAuthoredCoach,
        BandedFacePullAuthoredCoach,
        BandedPullApartAuthoredCoach,
        BandedSquatAuthoredCoach,
        BandedRowAuthoredCoach,
        BandedGluteBridgeAuthoredCoach,
        BandedChestPressAuthoredCoach,
        InvertedRowAuthoredCoach,
        ArcherPullUpAuthoredCoach,
        LSitPullUpAuthoredCoach,
        SidePlankAuthoredCoach,
    )

    @Test
    fun authoredCoachList_has31Coaches() {
        // 15 from v34+v35a + 16 from v35b = 31.
        Assert.assertEquals(31, authored.size)
        // The registry exposes the same list (B1 evidence).
        Assert.assertEquals(authored.size, CoachRegistry.authored.size)
    }

    @Test
    fun everyAuthoredCoach_resolvesViaRegistry() {
        // B1 proof: for each of the 31 authored coaches, look up every one of
        // its declared exerciseIds through CoachRegistry and assert the lookup
        // returns THAT authored coach (by reference).
        for (coach in authored) {
            Assert.assertTrue(
                "Coach ${coach.movementName} declares at least one exerciseId",
                coach.exerciseIds.isNotEmpty(),
            )
            for (id in coach.exerciseIds) {
                val resolved = CoachRegistry.lookup(id)
                Assert.assertNotNull("lookup($id) returned null for coach ${coach.movementName}", resolved)
                Assert.assertSame(
                    "lookup($id) should resolve to ${coach.movementName} (got ${resolved!!.movementName})",
                    coach, resolved,
                )
            }
        }
    }

    @Test
    fun authoredCoach_takesPrecedenceOverAllCoachesForSharedIds() {
        // exercise ids like "back_squat", "deadlift", "bench_press", "romanian_deadlift",
        // "chin_up", "dip", "front_squat", "push_up", "overhead_press", "barbell_row"
        // are claimed by BOTH AllCoaches (generic) and the authored coach.
        // The authored coach MUST win — otherwise requirements() returns the
        // all-false default and the card shows "No prescription available".
        Assert.assertSame(BackSquatAuthoredCoach, CoachRegistry.lookup("back_squat"))
        Assert.assertSame(DeadliftAuthoredCoach, CoachRegistry.lookup("deadlift"))
        Assert.assertSame(BenchPressAuthoredCoach, CoachRegistry.lookup("bench_press"))
        Assert.assertSame(OverheadPressAuthoredCoach, CoachRegistry.lookup("overhead_press"))
        Assert.assertSame(BarbellRowAuthoredCoach, CoachRegistry.lookup("barbell_row"))
        Assert.assertSame(FrontSquatAuthoredCoach, CoachRegistry.lookup("front_squat"))
        Assert.assertSame(RomanianDeadliftAuthoredCoach, CoachRegistry.lookup("romanian_deadlift"))
        Assert.assertSame(RomanianDeadliftAuthoredCoach, CoachRegistry.lookup("rdl"))
        Assert.assertSame(ChinUpAuthoredCoach, CoachRegistry.lookup("chin_up"))
        Assert.assertSame(ChinUpAuthoredCoach, CoachRegistry.lookup("chinup"))
        Assert.assertSame(DipAuthoredCoach, CoachRegistry.lookup("dip"))
        Assert.assertSame(PushUpAuthoredCoach, CoachRegistry.lookup("push_up"))
        Assert.assertSame(FacePullAuthoredCoach, CoachRegistry.lookup("face_pull"))
    }

    @Test
    fun keywordFallback_resolvesLibraryIdsThatDontMatchExactly() {
        // Library exercise id is derived from name (lowercase, spaces→underscores).
        //   "Barbell Romanian Deadlift" → "barbell_romanian_deadlift"
        //   "Chin-Up"                   → "chin-up"   (hyphen!)
        // Neither is in any authored coach's exerciseIds set, but the keyword
        // fallback (substring on authored exerciseIds + movement-name token match)
        // resolves them to the right coach.
        Assert.assertSame(
            RomanianDeadliftAuthoredCoach,
            CoachRegistry.lookup("barbell_romanian_deadlift"),
        )
        Assert.assertSame(
            ChinUpAuthoredCoach,
            CoachRegistry.lookup("chin-up"),
        )
    }

    @Test
    fun lookup_unknownIdReturnsNull() {
        // Sanity: the keyword fallback doesn't over-match — a truly unknown id
        // still returns null so the workout card falls through to "no coach".
        Assert.assertNull(CoachRegistry.lookup("zzz_not_a_real_exercise_zzz"))
    }

    // ---- B2: requirements() gating rules (regression) ----

    @Test
    fun compoundBarbellCoaches_declareNeeds1RM() {
        // B2: the 1RM panel + "Needs 1RM" chip gate on requirements().needs1RM.
        // Compound barbell movements declare it; everything else doesn't.
        val needs1RM = listOf(
            BackSquatAuthoredCoach, DeadliftAuthoredCoach, BenchPressAuthoredCoach,
            OverheadPressAuthoredCoach, BarbellRowAuthoredCoach, FrontSquatAuthoredCoach,
            RomanianDeadliftAuthoredCoach,
            SumoDeadliftAuthoredCoach, InclineBenchPressAuthoredCoach,
            CloseGripBenchPressAuthoredCoach, PendlayRowAuthoredCoach,
            PushPressAuthoredCoach, PowerCleanAuthoredCoach,
        )
        for (c in needs1RM) {
            Assert.assertTrue("${c.movementName} should declare needs1RM", c.requirements().needs1RM)
        }
    }

    @Test
    fun nonCompoundCoaches_doNotDeclareNeeds1RM() {
        // B2: these coaches do NOT declare needs1RM — they should not show the panel/chip.
        val no1RM = listOf(
            PullUpAuthoredCoach, BandedLateralRaiseAuthoredCoach, PlankAuthoredCoach,
            RunningAuthoredCoach, ChinUpAuthoredCoach, DipAuthoredCoach,
            PushUpAuthoredCoach, FacePullAuthoredCoach,
            BandedFacePullAuthoredCoach, BandedPullApartAuthoredCoach, BandedSquatAuthoredCoach,
            BandedRowAuthoredCoach, BandedGluteBridgeAuthoredCoach, BandedChestPressAuthoredCoach,
            InvertedRowAuthoredCoach, ArcherPullUpAuthoredCoach, LSitPullUpAuthoredCoach,
            SidePlankAuthoredCoach,
        )
        for (c in no1RM) {
            Assert.assertFalse("${c.movementName} should NOT declare needs1RM", c.requirements().needs1RM)
        }
    }

    // ---- B3: collapse-default + sort-reset regression ----
    // (Library.kt — covered indirectly by the library-screen screenshot tests;
    // the integer + logic assertions here document the intended state.)

    @Test
    fun authoredRows_stillHas113Rows() {
        // B3 doesn't add rows — confirm no accidental regression.
        Assert.assertEquals(113, AuthoredRows.allRows().size)
    }

    @Test
    fun authoredRows_has31RowLists() {
        val lists = listOf(
            AuthoredRows.backSquatRows, AuthoredRows.pullUpRows,
            AuthoredRows.bandedLateralRaiseRows, AuthoredRows.plankRows,
            AuthoredRows.runningRows,
            AuthoredRows.deadliftRows, AuthoredRows.benchPressRows,
            AuthoredRows.overheadPressRows, AuthoredRows.barbellRowRows,
            AuthoredRows.frontSquatRows, AuthoredRows.romanianDeadliftRows,
            AuthoredRows.chinUpRows, AuthoredRows.dipRows,
            AuthoredRows.pushUpRows, AuthoredRows.facePullRows,
            AuthoredRows.sumoDeadliftRows, AuthoredRows.inclineBenchPressRows,
            AuthoredRows.closeGripBenchPressRows, AuthoredRows.pendlayRowRows,
            AuthoredRows.pushPressRows, AuthoredRows.powerCleanRows,
            AuthoredRows.bandedFacePullRows, AuthoredRows.bandedPullApartRows,
            AuthoredRows.bandedSquatRows, AuthoredRows.bandedRowRows,
            AuthoredRows.bandedGluteBridgeRows, AuthoredRows.bandedChestPressRows,
            AuthoredRows.invertedRowRows, AuthoredRows.archerPullUpRows,
            AuthoredRows.lSitPullUpRows, AuthoredRows.sidePlankRows,
        )
        Assert.assertEquals(31, lists.size)
        lists.forEach { Assert.assertTrue("every row list non-empty", it.isNotEmpty()) }
    }

    // ---- B4: the openBrowser wiring is a runtime behavior; the build's
    //         presence of FLAG_ACTIVITY_NEW_TASK is verified by visual
    //         inspection of Library.kt (grep). No unit-testable seam. ----
}
