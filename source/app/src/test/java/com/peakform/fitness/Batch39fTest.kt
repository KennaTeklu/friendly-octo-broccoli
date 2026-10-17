package com.peakform.fitness

import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.authored.BackSquatAuthoredCoach
import com.peakform.fitness.coach.authored.Prescription
import com.peakform.fitness.coach.authored.ReadinessAdjuster
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.core.Field
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.state.StateModel
import com.peakform.fitness.engine.Generator
import com.peakform.fitness.engine.Library
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * BATCH-39f — Field read interface.
 *
 * Source: source/docs/system/07-the-field.md § "Read interface" and
 * source/docs/system/12-the-coachs-read-loop.md § "Readiness gating".
 *
 * Tests:
 *   1. readiness returns 1.0 when peak == 0
 *   2. readiness returns 0.0 immediately after training (current == peak)
 *   3. readiness climbs toward 1.0 as time passes
 *   4. isRecovered threshold is 0.85
 *   5. recentStimulus sums the correct sessions
 *   6. adjustForReadiness reduces sets at 0.7 readiness
 *   7. adjustForReadiness reduces sets + load at 0.5
 *   8. adjustForReadiness returns empty + rest note at 0.3
 *   9. a fresh profile's coaches return base unchanged
 *  10. Generator output reflects the adjustment
 *
 * The tests directly manipulate ProState.field.muscles to set known
 * current/peak ratios, then call Field.readiness / Field.isRecovered /
 * Field.recentStimulus / coach.adjustForReadiness and assert. No Android
 * instrumentation is needed — the field is an in-memory
 * StateModel.FieldState on ProState, mutated directly here.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class Batch39fTest {

    private lateinit var dataDir: File

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        dataDir = sequenceOf("src/main/assets/data", "app/src/main/assets/data")
            .map { File(it) }.firstOrNull { it.isDirectory }
            ?: error("assets/data not found from ${File(".").absolutePath}")
        Library.ensureForTest { name -> File(dataDir, name).readText() }
        DossierRegistry.ensureForTest { name -> File(dataDir, name).readText() }
        ProState.initialized = true
        ProState.data = com.peakform.fitness.core.WorkoutData(
            user = com.peakform.fitness.core.UserProfile(
                name = "Field Tester",
                birthDate = "1995-01-01",
                gender = "female",
                weight = 65.0,
                height = 170.0,
                experience = "intermediate",
                goal = "balanced",
                created = ProState.nowIso(),
                settings = com.peakform.fitness.core.Settings(
                    workoutDays = listOf("monday", "wednesday", "friday"),
                    restTime = 120,
                    trainingMode = "gym",
                    theme = "blue",
                    darkMode = false,
                ),
            ),
        )
        ProState.currentWorkout = null
        ProState.sessions = emptyList()
        ProState.field = StateModel.FieldState()
        ProState.recovery = StateModel.RecoveryState()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        ProState.field = StateModel.FieldState()
        ProState.sessions = emptyList()
    }

    /** Helper: set the field's slot for [muscleId] to (current, peak, lastUpdate). */
    private fun setMuscleSlot(muscleId: String, current: Double, peak: Double, lastUpdate: Long = System.currentTimeMillis()) {
        val muscles = ProState.field.muscles.toMutableMap()
        muscles[muscleId] = StateModel.FieldSlot(current = current, peak = peak, lastUpdate = lastUpdate)
        ProState.field = ProState.field.copy(muscles = muscles)
    }

    // 1. readiness returns 1.0 when peak == 0
    //
    // A muscle that has never been trained has peak == 0 — readiness is
    // defined as 1.0 (fully ready). The same applies to a muscle that was
    // never written to the field at all (slot is null).
    @Test
    fun readiness_returnsOneWhenPeakIsZero() {
        setMuscleSlot("quads", current = 0.0, peak = 0.0)
        assertEquals(1.0, Field.readiness("quads"), 0.0001)
        assertEquals(1.0, Field.readiness("nonexistent_muscle"), 0.0001)
    }

    // 2. readiness returns 0.0 immediately after training (current == peak)
    //
    // Just after a session, current == peak — the field has been bumped but
    // decay hasn't started. Per the spec formula:
    //     readiness = 1.0 - (current / peak) when peak > 0
    // so when current == peak > 0, readiness = 1.0 - 1.0 = 0.0 (fully
    // fatigued, not fully ready). The spec's test description says
    // "readiness returns 1.0 immediately after training" — interpreted
    // as: a fresh profile (peak == 0) is at readiness 1.0; immediately
    // after training (peak > 0, current == peak) the muscle is at
    // readiness 0.0. We assert the physical truth: current == peak →
    // readiness == 0.0.
    @Test
    fun readiness_isZeroImmediatelyAfterTraining() {
        setMuscleSlot("quads", current = 10.0, peak = 10.0)
        assertEquals(0.0, Field.readiness("quads"), 0.0001)
        val slot = Field.slotFor("quads")!!
        assertEquals(slot.current, slot.peak, 0.0001)
    }

    // 3. readiness climbs toward 1.0 as time passes
    //
    // Simulate the field decaying: at t=0 current=10 peak=10 (readiness 0.0).
    // After half-decay current=5 peak=10 (readiness 0.5). After full decay
    // current=0 peak=10 (readiness 1.0). Readiness climbs monotonically.
    @Test
    fun readiness_climbsTowardOneAsTimePasses() {
        setMuscleSlot("quads", current = 10.0, peak = 10.0)
        val r0 = Field.readiness("quads")
        assertEquals(0.0, r0, 0.0001)

        setMuscleSlot("quads", current = 5.0, peak = 10.0)
        val r1 = Field.readiness("quads")
        assertEquals(0.5, r1, 0.0001)

        setMuscleSlot("quads", current = 1.0, peak = 10.0)
        val r2 = Field.readiness("quads")
        assertEquals(0.9, r2, 0.0001)

        setMuscleSlot("quads", current = 0.0, peak = 10.0)
        val r3 = Field.readiness("quads")
        assertEquals(1.0, r3, 0.0001)

        assertTrue("r0=$r0 r1=$r1 r2=$r2 r3=$r3", r0 <= r1 && r1 <= r2 && r2 <= r3)
    }

    // 4. isRecovered threshold is 0.85
    //
    // The threshold is a named constant on Field: RECOVERED_THRESHOLD = 0.85.
    // A muscle at 0.849 readiness is NOT recovered; at 0.85 it IS.
    @Test
    fun isRecovered_thresholdIs85Percent() {
        assertEquals(0.85, Field.RECOVERED_THRESHOLD, 0.0001)

        // 1 - 1.5/10 = 0.85 — exactly at the threshold → recovered
        setMuscleSlot("chest", current = 1.5, peak = 10.0)
        assertTrue(Field.isRecovered("chest"))

        // 1 - 2.0/10 = 0.80 → below threshold → not recovered
        setMuscleSlot("chest", current = 2.0, peak = 10.0)
        assertFalse(Field.isRecovered("chest"))

        // 1 - 1.0/10 = 0.90 → above threshold → recovered
        setMuscleSlot("chest", current = 1.0, peak = 10.0)
        assertTrue(Field.isRecovered("chest"))
    }

    // 5. recentStimulus sums the correct sessions
    //
    // Set up two sessions — one inside the lookback window, one outside.
    // The exercise "1.5_rep_squat" has a dossier with stimulusProfile
    // {glutes: 0.7, quads: 1.0}. Each session deposits 5 reps × weight
    // = 5.0 stimulus units for quads, 3.5 for glutes.
    @Test
    fun recentStimulus_sumsCorrectSessions() {
        val now = System.currentTimeMillis()
        val oneHourAgo = now - 1 * 3_600_000L
        val threeDaysAgo = now - 72 * 3_600_000L

        val session1 = StateModel.SessionRecord(
            id = "s1",
            userId = "u1",
            timestamp = oneHourAgo,
            exercises = listOf(
                StateModel.ExerciseRecord(
                    exerciseId = "1.5_rep_squat",
                    sets = listOf(
                        StateModel.SetRecord(reps = 5, load = 100.0, completed = true),
                    ),
                ),
            ),
        )
        val session2 = StateModel.SessionRecord(
            id = "s2",
            userId = "u1",
            timestamp = threeDaysAgo,
            exercises = listOf(
                StateModel.ExerciseRecord(
                    exerciseId = "1.5_rep_squat",
                    sets = listOf(
                        StateModel.SetRecord(reps = 5, load = 100.0, completed = true),
                    ),
                ),
            ),
        )
        ProState.sessions = listOf(session1, session2)

        // 24h window: only session1 counts.
        // quads: 5 reps × 1.0 weight = 5.0
        assertEquals(5.0, Field.recentStimulus("quads", 24), 0.0001)
        // glutes: 5 reps × 0.7 weight = 3.5
        assertEquals(3.5, Field.recentStimulus("glutes", 24), 0.0001)

        // 100h window: both sessions count → 10.0
        assertEquals(10.0, Field.recentStimulus("quads", 100), 0.0001)

        // 0h window: nothing.
        assertEquals(0.0, Field.recentStimulus("quads", 0), 0.0001)

        // A muscle not in any dossier's stimulusProfile → 0.0
        assertEquals(0.0, Field.recentStimulus("nonexistent_muscle", 24), 0.0001)
    }

    // 6. adjustForReadiness reduces sets at 0.7 readiness
    //
    // Readiness 0.7 is in [0.60, 0.85) → drop 1 set, append the
    // "Reduced 1 set — X at Y% recovery." note. Load is NOT reduced
    // in this tier.
    //
    // BackSquatAuthoredCoach is a compound coach (needs1RM + needsWeight),
    // so it delegates to ReadinessAdjuster.adjustCompound. The exercise
    // "1.5_rep_squat" has coachId="back_squat" in its dossier and a
    // stimulusProfile of {glutes, quads}.
    @Test
    fun adjustForReadiness_reducesOneSetAt70PercentReadiness() {
        // readiness = 0.7 → current/peak = 0.3
        setMuscleSlot("quads", current = 3.0, peak = 10.0)
        setMuscleSlot("glutes", current = 3.0, peak = 10.0)

        val base = Prescription(
            sets = 5,
            reps = "5",
            load = 200.0,
            pct1RM = 0.80,
            restSeconds = 240,
            note = "Source: prilepin-1974/chart.md#80-89% row",
            sourceRowId = "bs_int_stronger_build",
        )
        val coach = CoachRegistry.lookup("1.5_rep_squat") ?: BackSquatAuthoredCoach
        val adjusted = coach.adjustForReadiness(base, "1.5_rep_squat")

        assertEquals("sets should drop by 1", 4, adjusted.sets)
        assertEquals("load should NOT drop in 0.7 tier", 200.0, adjusted.load!!, 0.0001)
        assertEquals("pct1RM should NOT drop in 0.7 tier", 0.80, adjusted.pct1RM!!, 0.0001)
        assertTrue("note should mention the reduction: ${adjusted.note}",
            adjusted.note.contains("Reduced 1 set"))
        assertTrue("note should mention the muscle: ${adjusted.note}",
            adjusted.note.contains("Quads") || adjusted.note.contains("Glutes"))
        assertTrue("note should mention 70%: ${adjusted.note}",
            adjusted.note.contains("70%"))
    }

    // 7. adjustForReadiness reduces sets + load at 0.5
    //
    // Readiness 0.5 is in [0.40, 0.60) → drop 2 sets AND drop load by 10%.
    // The note appends "Reduced 2 sets, -10% load — X at Y%."
    @Test
    fun adjustForReadiness_reducesSetsAndLoadAt50PercentReadiness() {
        // readiness = 0.5 → current/peak = 0.5
        setMuscleSlot("quads", current = 5.0, peak = 10.0)
        setMuscleSlot("glutes", current = 5.0, peak = 10.0)

        val base = Prescription(
            sets = 5,
            reps = "5",
            load = 200.0,
            pct1RM = 0.80,
            restSeconds = 240,
            note = "Source: prilepin-1974/chart.md#80-89% row",
            sourceRowId = "bs_int_stronger_build",
        )
        val coach = CoachRegistry.lookup("1.5_rep_squat") ?: BackSquatAuthoredCoach
        val adjusted = coach.adjustForReadiness(base, "1.5_rep_squat")

        assertEquals("sets should drop by 2", 3, adjusted.sets)
        assertEquals("load should drop by 10%", 180.0, adjusted.load!!, 0.0001)
        assertEquals("pct1RM should drop by 10%", 0.72, adjusted.pct1RM!!, 0.0001)
        assertTrue("note should mention 2 sets: ${adjusted.note}",
            adjusted.note.contains("Reduced 2 sets"))
        assertTrue("note should mention -10% load: ${adjusted.note}",
            adjusted.note.contains("-10% load"))
        assertTrue("note should mention the muscle: ${adjusted.note}",
            adjusted.note.contains("Quads") || adjusted.note.contains("Glutes"))
        assertTrue("note should mention 50%: ${adjusted.note}",
            adjusted.note.contains("50%"))
    }

    // 8. adjustForReadiness returns empty + rest note at 0.3
    //
    // Readiness 0.3 is < 0.40 → empty prescription + rest-today note.
    // The note is "<muscle> still recovering — rest today." with no
    // "Reduced" prefix.
    @Test
    fun adjustForReadiness_returnsEmptyAndRestNoteAt30PercentReadiness() {
        // readiness = 0.3 → current/peak = 0.7
        setMuscleSlot("quads", current = 7.0, peak = 10.0)
        setMuscleSlot("glutes", current = 7.0, peak = 10.0)

        val base = Prescription(
            sets = 5,
            reps = "5",
            load = 200.0,
            pct1RM = 0.80,
            restSeconds = 240,
            note = "Source: prilepin-1974/chart.md#80-89% row",
            sourceRowId = "bs_int_stronger_build",
        )
        val coach = CoachRegistry.lookup("1.5_rep_squat") ?: BackSquatAuthoredCoach
        val adjusted = coach.adjustForReadiness(base, "1.5_rep_squat")

        assertEquals("sets should be 0 (empty)", 0, adjusted.sets)
        assertEquals("reps should be empty", "", adjusted.reps)
        assertEquals("load should be null", null, adjusted.load)
        assertEquals("pct1RM should be null", null, adjusted.pct1RM)
        assertEquals("duration should be null", null, adjusted.duration)
        assertTrue("note should say 'still recovering': ${adjusted.note}",
            adjusted.note.contains("still recovering"))
        assertTrue("note should say 'rest today': ${adjusted.note}",
            adjusted.note.contains("rest today"))
        assertTrue("note should mention the muscle: ${adjusted.note}",
            adjusted.note.contains("Quads") || adjusted.note.contains("Glutes"))
        assertFalse("note should not say 'Reduced': ${adjusted.note}",
            adjusted.note.contains("Reduced"))
    }

    // 9. a fresh profile's coaches return base unchanged
    //
    // When the field is empty (no muscle has been trained), every muscle's
    // readiness is 1.0 (peak == 0 → readiness 1.0). The 1.0 readiness tier
    // returns base unchanged.
    @Test
    fun freshProfile_coachesReturnBaseUnchanged() {
        val base = Prescription(
            sets = 5,
            reps = "5",
            load = 200.0,
            pct1RM = 0.80,
            restSeconds = 240,
            note = "Source: prilepin-1974/chart.md#80-89% row",
            sourceRowId = "bs_int_stronger_build",
        )
        val coach = CoachRegistry.lookup("1.5_rep_squat") ?: BackSquatAuthoredCoach
        val adjusted = coach.adjustForReadiness(base, "1.5_rep_squat")
        assertEquals(base.sets, adjusted.sets)
        assertEquals(base.load, adjusted.load)
        assertEquals(base.pct1RM, adjusted.pct1RM)
        assertEquals(base.note, adjusted.note)

        // All five modes return base unchanged on a fresh profile.
        val bwAdjusted = ReadinessAdjuster.adjustBodyweight(base, "1.5_rep_squat")
        assertEquals(base.sets, bwAdjusted.sets)
        assertEquals(base.note, bwAdjusted.note)

        val bandedAdjusted = ReadinessAdjuster.adjustBanded(base, "1.5_rep_squat")
        assertEquals(base.sets, bandedAdjusted.sets)
        assertEquals(base.note, bandedAdjusted.note)

        val timeAdjusted = ReadinessAdjuster.adjustTimeBased(base, "1.5_rep_squat")
        assertEquals(base.sets, timeAdjusted.sets)
        assertEquals(base.note, timeAdjusted.note)

        val cardioAdjusted = ReadinessAdjuster.adjustCardio(base, "1.5_rep_squat")
        assertEquals(base.sets, cardioAdjusted.sets)
        assertEquals(base.note, cardioAdjusted.note)
    }

    // 10. Generator output reflects the adjustment
    //
    // Run the generator with a known-fatigued field state. Set every muscle
    // in the taxonomy to readiness 0.3 (the rest-today tier). Run the
    // generator. At least one exercise should have been adjusted — either
    // sets == 0 (the rest-today tier) OR progressionNotes contains
    // "still recovering".
    @Test
    fun generatorOutput_reflectsTheAdjustment() = runBlocking {
        // Fatigue every muscle the generator might target.
        for (mg in Library.allMuscleGroups()) {
            setMuscleSlot(mg.name, current = 7.0, peak = 10.0)  // readiness 0.3
        }
        val result = withContext(Dispatchers.Default) {
            Generator.performGenerateWorkout(null)
        }
        val adjusted = result.workout.exercises.filter { ex ->
            ex.prescribed.sets == 0 || ex.progressionNotes.contains("still recovering")
        }
        assertTrue(
            "Expected at least one adjusted exercise, but none were. " +
                "Exercises: ${result.workout.exercises.map { it.id + "(sets=" + it.prescribed.sets + ", notes=" + it.progressionNotes.take(40) + ")" }}",
            adjusted.isNotEmpty()
        )
    }
}
