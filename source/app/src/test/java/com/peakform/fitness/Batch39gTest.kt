package com.peakform.fitness

import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.state.RecoveryCurves
import com.peakform.fitness.core.state.RecoveryLearner
import com.peakform.fitness.core.state.StateModel
import com.peakform.fitness.engine.Library
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * BATCH-39g — Recovery curve learning.
 *
 * Source: source/docs/system/09-recovery-curves.md § "Learned curves" and
 * BATCH 39g spec.
 *
 * Tests:
 *   1. with 0 samples, curve stays at default
 *   2. with 2 samples, curve stays at default with samples=2
 *   3. with 3+ samples, source flips to "learned"
 *   4. learned value is the median of the samples
 *   5. performance drop triggers gap * 1.15
 *   6. performance hold counts the gap as-is
 *   7. clamp to [12h, 96h] for muscles
 *   8. clamp to [8h, 72h] for components
 *   9. decayField uses the learned value when present
 *  10. migration populates defaults when empty
 *
 * The tests build [StateModel.SessionRecord] lists with known timestamps
 * and performance values, call [RecoveryLearner.recomputeAll], and assert
 * on the returned [StateModel.RecoveryState]. The "1.5_rep_squat" exercise
 * has a dossier with stimulusProfile {glutes: 0.7, quads: 1.0} — both
 * above the 0.5 primary threshold — so it targets both quads and glutes
 * for the learning algorithm.
 *
 * Each session deposits N reps × load performance per the algorithm:
 *   - performance = sum(reps × load) for reps-based sets
 *   - performance = sum(durationSeconds / 30 × max(load, 1.0)) for holds
 *
 * Tests 5 and 6 use the same exercise id across both sessions so the
 * performance comparison is direct (B vs A on the same exercise).
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class Batch39gTest {

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
                name = "Recovery Learner",
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
        ProState.recovery = StateModel.RecoveryState()
    }

    /** Build a session record at [timestampMs] with one exercise "1.5_rep_squat"
     *  doing [reps] reps at [load] lbs. The exercise has stimulusProfile
     *  {glutes: 0.7, quads: 1.0} so it targets both muscles.
     *
     *  Performance = reps × load (e.g. 5 × 100 = 500). */
    private fun sessionAt(timestampMs: Long, reps: Int, load: Double, sessionId: String = "s$timestampMs"): StateModel.SessionRecord {
        return StateModel.SessionRecord(
            id = sessionId,
            userId = "u1",
            timestamp = timestampMs,
            exercises = listOf(
                StateModel.ExerciseRecord(
                    exerciseId = "1.5_rep_squat",
                    sets = listOf(
                        StateModel.SetRecord(reps = reps, load = load, completed = true),
                    ),
                ),
            ),
        )
    }

    /** Build a session record for a hold-based exercise. */
    private fun holdSessionAt(timestampMs: Long, durationSeconds: Double, sessionId: String = "h$timestampMs"): StateModel.SessionRecord {
        return StateModel.SessionRecord(
            id = sessionId,
            userId = "u1",
            timestamp = timestampMs,
            exercises = listOf(
                StateModel.ExerciseRecord(
                    exerciseId = "1.5_rep_squat",
                    sets = listOf(
                        StateModel.SetRecord(reps = 0, load = 0.0, durationSeconds = durationSeconds, completed = true),
                    ),
                ),
            ),
        )
    }

    /** The default recovery hours for quads (compound family → 48h). */
    private val quadsDefaultH: Double = RecoveryCurves.COMPOUND_H  // 48.0

    /** The default recovery hours for the "Strength" component (24h). */
    private val strengthDefaultH: Double = RecoveryCurves.COMPONENT_H  // 24.0

    // 1. with 0 samples, curve stays at default
    //
    // No sessions → no muscle has any samples → no curve is written. The
    // returned RecoveryState is the same as the input (empty curves map).
    @Test
    fun withZeroSamples_curveStaysAtDefault() {
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = emptyList(), current = current)
        // No sessions → no curves written. The result equals the input.
        assertEquals(0, result.curves.size)
    }

    // 2. with 2 samples, curve stays at default with samples=2
    //
    // Two sessions on the same muscle produce 1 gap sample (between the two
    // sessions). 1 < MUSCLE_LEARN_THRESHOLD (3), so source stays "default"
    // — but the samples field on the curve records the gap count for
    // observability.
    //
    // Wait — the spec says "with 2 samples, curve stays at default with
    // samples=2". The "samples" in the spec text refers to the count of
    // SESSIONS, not gap-samples. Two sessions = two session-samples, one
    // gap. We test the session-count interpretation here: with 2 sessions
    // on the same muscle, the curve stays at default but the samples
    // field reflects the session count when no gap was learned.
    //
    // Re-reading the spec: the algorithm in PART 5 says "After collecting
    // N samples for the muscle: learned = median(samples)." The samples
    // here are GAP samples (one per consecutive pair), not sessions.
    // 2 sessions → 1 gap. 1 < 3 → default. But the curve's `samples`
    // field stores the gap count for observability.
    //
    // The spec's test description "samples=2" most naturally reads as
    // "the curve records 2 samples" — which we interpret as the gap
    // count. With 3 sessions we get 2 gaps. So this test uses 3 sessions
    // (2 gaps) and asserts samples=2 + source="default".
    @Test
    fun withTwoGapSamples_curveStaysAtDefaultWithSamplesTwo() {
        // Three sessions: 2 gaps. Still < 3 threshold (the 3rd gap would
        // come from a 4th session). 2 < 3 → source stays "default".
        // The curve's samples field = 2 (the gap count).
        val t0 = 1_000_000L
        val t1 = t0 + 48 * 3_600_000L  // 48h gap
        val t2 = t1 + 48 * 3_600_000L  // 48h gap
        val sessions = listOf(
            sessionAt(t0, reps = 5, load = 100.0),
            sessionAt(t1, reps = 5, load = 100.0),
            sessionAt(t2, reps = 5, load = 100.0),
        )
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = sessions, current = current)

        val quadsCurve = result.curves["quads"]
        assertNotNull("quads curve should be written", quadsCurve)
        // 2 gap samples < MUSCLE_LEARN_THRESHOLD (3) → default
        assertEquals("default", quadsCurve!!.source)
        assertEquals(2, quadsCurve.samples)
        // recoveryHours stays at default (no blending because source is default)
        assertEquals(quadsDefaultH, quadsCurve.recoveryHours, 0.001)
    }

    // 3. with 3+ samples, source flips to "learned"
    //
    // Four sessions on the same muscle with 30h gaps → 3 gap samples.
    // 3 >= 3 → source = "learned". Use 30h (not 48h) so the learned
    // median differs from the default — the recoveryHours should shift.
    @Test
    fun withThreeGapSamples_sourceFlipsToLearned() {
        val t0 = 1_000_000L
        val gap = 30L  // hours — distinct from the 48h default
        val sessions = (0..3).map { i ->
            sessionAt(t0 + i * gap * 3_600_000L, reps = 5, load = 100.0)
        }
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = sessions, current = current)

        val quadsCurve = result.curves["quads"]
        assertNotNull(quadsCurve)
        assertEquals("learned", quadsCurve!!.source)
        assertEquals(3, quadsCurve.samples)
        // The recoveryHours should NOT be the default 48h anymore — it should
        // be the blended value (median 30 × weight 0.15 + default 48 × 0.85
        // = 4.5 + 40.8 = 45.3).
        assertNotEquals("recoveryHours should differ from default: ${quadsCurve.recoveryHours}",
            quadsDefaultH, quadsCurve.recoveryHours, 0.001)
    }

    // 4. learned value is the median of the samples
    //
    // Build 5 sessions with gaps of 24h, 36h, 48h, 60h (4 gaps). The median
    // of [24, 36, 48, 60] is (36 + 48) / 2 = 42. After clamping to [12, 96]
    // (no-op) and Bayesian shrinkage:
    //   weight = min(4 / 20, 1.0) = 0.2
    //   learned = 0.2 × 42 + 0.8 × 48 = 8.4 + 38.4 = 46.8
    //
    // All sessions have the same performance (5×100=500), so every gap is
    // a "hold" → gap is recorded as-is (no inflation).
    @Test
    fun learnedValue_isMedianOfSamples() {
        val t0 = 1_000_000L
        // Gaps: 24h, 36h, 48h, 60h → 4 gaps
        val gaps = listOf(24L, 36L, 48L, 60L)
        val timestamps = ArrayList<Long>()
        var t = t0
        timestamps.add(t)
        for (gap in gaps) {
            t += gap * 3_600_000L
            timestamps.add(t)
        }
        val sessions = timestamps.mapIndexed { i, ts ->
            sessionAt(ts, reps = 5, load = 100.0, sessionId = "s$i")
        }
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = sessions, current = current)

        val quadsCurve = result.curves["quads"]!!
        // 4 gaps >= 3 → learned
        assertEquals("learned", quadsCurve.source)
        // Median of [24, 36, 48, 60] = (36 + 48) / 2 = 42
        val expectedMedian = 42.0
        val weight = minOf(4.0 / RecoveryLearner.SHRINKAGE_FULL_AT, 1.0)  // 0.2
        val expectedBlended = weight * expectedMedian + (1.0 - weight) * quadsDefaultH
        assertEquals(expectedBlended, quadsCurve.recoveryHours, 0.01)
    }

    // 5. performance drop triggers gap * 1.15
    //
    // Two sessions where B underperforms A by > 5% (B < A × 0.95). The gap
    // is recorded as `gap × 1.15` (need more rest).
    //
    // A: 5 reps × 100 lbs = 500 performance
    // B: 5 reps × 80 lbs = 400 performance (400 / 500 = 0.8 < 0.95 → drop)
    // Gap = 24h → recorded as 24 × 1.15 = 27.6h
    //
    // We need 4 sessions for the curve to flip to "learned" (3 gaps), so
    // we use 4 sessions with the same drop pattern. Each gap = 24h × 1.15.
    @Test
    fun performanceDrop_triggersGapInflation() {
        val t0 = 1_000_000L
        val gap = 24L  // hours
        // 4 sessions, alternating 500/400/500/400 performance. Each gap is
        // a drop (B < A × 0.95 when B=400, A=500; B < A × 0.95 when B=500,
        // A=400 → 500/400=1.25, NOT a drop. Hmm — we need every gap to be
        // a drop). Use 500/400/400/400: gap1=400/500=0.8 (drop), gap2=
        // 400/400=1.0 (hold), gap3=400/400=1.0 (hold). Median of [27.6,
        // 24, 24] = 24.
        //
        // Simpler: 4 sessions all with the same drop pattern. Each session
        // decreases performance. A=500, B=400, C=300, D=200. Each gap is
        // a drop. Gaps recorded as [24×1.15, 24×1.15, 24×1.15] = [27.6,
        // 27.6, 27.6]. Median = 27.6.
        val sessions = listOf(
            sessionAt(t0, reps = 5, load = 100.0),  // 500 perf
            sessionAt(t0 + gap * 3_600_000L, reps = 4, load = 100.0),  // 400 perf, drop
            sessionAt(t0 + 2 * gap * 3_600_000L, reps = 3, load = 100.0),  // 300 perf, drop
            sessionAt(t0 + 3 * gap * 3_600_000L, reps = 2, load = 100.0),  // 200 perf, drop
        )
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = sessions, current = current)

        val quadsCurve = result.curves["quads"]!!
        assertEquals("learned", quadsCurve.source)
        // Each gap was 24h × 1.15 = 27.6h. Median of [27.6, 27.6, 27.6] = 27.6.
        val expectedMedian = 24.0 * RecoveryLearner.GAP_INFLATION  // 27.6
        val weight = minOf(3.0 / RecoveryLearner.SHRINKAGE_FULL_AT, 1.0)  // 0.15
        val expectedBlended = weight * expectedMedian + (1.0 - weight) * quadsDefaultH
        assertEquals(expectedBlended, quadsCurve.recoveryHours, 0.01)
    }

    // 6. performance hold counts the gap as-is
    //
    // Sessions where B holds A's performance (B >= A × 0.95). The gap is
    // recorded as-is, no inflation.
    //
    // All sessions same performance → every gap is a hold.
    @Test
    fun performanceHold_countsGapAsIs() {
        val t0 = 1_000_000L
        val gap = 30L  // hours — distinct from the 48h default
        // 4 sessions, all same performance → every gap is a hold. Gap = 30h.
        val sessions = (0..3).map { i ->
            sessionAt(t0 + i * gap * 3_600_000L, reps = 5, load = 100.0)
        }
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = sessions, current = current)

        val quadsCurve = result.curves["quads"]!!
        assertEquals("learned", quadsCurve.source)
        // Every gap = 30h (hold, no inflation). Median of [30, 30, 30] = 30.
        val expectedMedian = 30.0
        val weight = minOf(3.0 / RecoveryLearner.SHRINKAGE_FULL_AT, 1.0)  // 0.15
        val expectedBlended = weight * expectedMedian + (1.0 - weight) * quadsDefaultH
        assertEquals(expectedBlended, quadsCurve.recoveryHours, 0.01)
    }

    // 7. clamp to [12h, 96h] for muscles
    //
    // Build sessions with gaps so short that the median is below 12h. The
    // learned value should be clamped to 12h. (Hard to construct a > 96h
    // case naturally — but we can construct a < 12h case: 4 sessions with
    // 4h gaps, all holds. Median = 4 → clamped to 12.)
    @Test
    fun clamp_toRangeForMuscles() {
        val t0 = 1_000_000L
        val gap = 4L  // hours — well below MUSCLE_MIN_H (12h)
        val sessions = (0..3).map { i ->
            sessionAt(t0 + i * gap * 3_600_000L, reps = 5, load = 100.0)
        }
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = sessions, current = current)

        val quadsCurve = result.curves["quads"]!!
        assertEquals("learned", quadsCurve.source)
        // The learned median (4h) is below MUSCLE_MIN_H (12h). After clamp,
        // learned = 12. After shrinkage with default 48:
        //   weight = 0.15, learned = 12, blended = 0.15 × 12 + 0.85 × 48
        //                                               = 1.8 + 40.8 = 42.6
        // But wait — the clamp happens BEFORE the shrinkage (per the spec:
        // "Clamp learned to [12h, 96h]" then "Blend with the default").
        // So learned = 12, blended = 0.15 × 12 + 0.85 × 48 = 42.6.
        val expectedLearned = RecoveryLearner.MUSCLE_MIN_H  // 12.0
        val weight = minOf(3.0 / RecoveryLearner.SHRINKAGE_FULL_AT, 1.0)
        val expectedBlended = weight * expectedLearned + (1.0 - weight) * quadsDefaultH
        assertEquals(expectedBlended, quadsCurve.recoveryHours, 0.01)
        // The clamp lower bound is enforced on the learned value before blending.
        assertTrue("recoveryHours should reflect clamped learned: ${quadsCurve.recoveryHours}",
            quadsCurve.recoveryHours >= 0.0)
    }

    // 8. clamp to [8h, 72h] for components
    //
    // Same idea, but for a component. Components have a wider clamp:
    // [8h, 72h] and a higher learn threshold (5 samples vs 3).
    //
    // The "1.5_rep_squat" exercise has componentStimulus {Strength: 0.6,
    // Endurance: 0.2} — only Strength is >= 0.5. So this tests the
    // Strength component.
    //
    // We need 6 sessions for 5 gaps (component threshold is 5).
    @Test
    fun clamp_toRangeForComponents() {
        val t0 = 1_000_000L
        val gap = 4L  // hours — below COMPONENT_MIN_H (8h)
        // 6 sessions for 5 gaps (component threshold)
        val sessions = (0..5).map { i ->
            sessionAt(t0 + i * gap * 3_600_000L, reps = 5, load = 100.0)
        }
        val current = StateModel.RecoveryState()
        val result = RecoveryLearner.recomputeAll(sessions = sessions, current = current)

        // The Strength component should be in the result.
        val strengthCurve = result.curves["Strength"]
        assertNotNull("Strength curve should be written", strengthCurve)
        // 5 gaps >= COMPONENT_LEARN_THRESHOLD (5) → learned
        assertEquals("learned", strengthCurve!!.source)
        // The learned median (4h) is below COMPONENT_MIN_H (8h). After clamp,
        // learned = 8. After shrinkage with default 24:
        //   weight = min(5/20, 1.0) = 0.25
        //   blended = 0.25 × 8 + 0.75 × 24 = 2 + 18 = 20
        val expectedLearned = RecoveryLearner.COMPONENT_MIN_H  // 8.0
        val weight = minOf(5.0 / RecoveryLearner.SHRINKAGE_FULL_AT, 1.0)
        val expectedBlended = weight * expectedLearned + (1.0 - weight) * strengthDefaultH
        assertEquals(expectedBlended, strengthCurve.recoveryHours, 0.01)
    }

    // 9. decayField uses the learned value when present
    //
    // Set up a field slot with current=10, peak=10, lastUpdate=now-24h.
    // Set up a learned RecoveryState where quads has recoveryHours=12 (much
    // shorter than the 48h default). Call ProState.decayField(now).
    // Verify the decayed current reflects the 12h curve, not the 48h one.
    @Test
    fun decayField_usesLearnedValueWhenPresent() {
        val now = System.currentTimeMillis()
        val twentyFourHoursAgo = now - 24 * 3_600_000L
        // Set the field slot for quads: current=10, peak=10, last=24h ago.
        val muscles = mapOf(
            "quads" to StateModel.FieldSlot(current = 10.0, peak = 10.0, lastUpdate = twentyFourHoursAgo),
        )
        ProState.field = StateModel.FieldState(muscles = muscles)
        // Set up a LEARNED curve for quads with recoveryHours=12 (vs the
        // 48h default). The 24h elapsed time at T=12 gives factor =
        // exp(-24/12) = exp(-2) ≈ 0.1353. At T=48, factor = exp(-24/48)
        // = exp(-0.5) ≈ 0.6065. The decayed current is 10 × factor.
        val learnedCurve = StateModel.RecoveryCurve(
            muscleId = "quads", recoveryHours = 12.0, source = "learned", samples = 5,
        )
        ProState.recovery = StateModel.RecoveryState(curves = mapOf("quads" to learnedCurve))
        // Run decayField. The new current should be 10 × exp(-2) ≈ 1.353.
        ProState.decayField(now)
        val slot = ProState.field.muscles["quads"]!!
        val expectedFactor = kotlin.math.exp(-24.0 / 12.0)
        val expectedCurrent = 10.0 * expectedFactor
        assertEquals(expectedCurrent, slot.current, 0.01)
        // Sanity: the learned-curve value (1.353) is much smaller than the
        // default-curve value (6.065). Confirm we used the learned curve.
        val defaultFactor = kotlin.math.exp(-24.0 / 48.0)
        val defaultCurrent = 10.0 * defaultFactor
        assertTrue(
            "learned-curve current (${slot.current}) should be < default-curve current ($defaultCurrent)",
            slot.current < defaultCurrent,
        )
    }

    // 10. migration populates defaults when empty
    //
    // populateDefaults on an empty RecoveryState should write a default
    // curve for every muscle id in the taxonomy + every component short
    // name. All curves have source="default", samples=0, recoveryHours
    // matching the v39e bucket defaults.
    @Test
    fun migration_populatesDefaultsWhenEmpty() {
        val muscleIds = Library.allMuscleGroups().map { it.name }
        val componentNames = Library.mpComponents.map { it.short }
        val populated = RecoveryLearner.populateDefaults(
            StateModel.RecoveryState(),
            muscleIds,
            componentNames,
        )
        // Every muscle + every component should have a curve.
        for (muscleId in muscleIds) {
            val curve = populated.curves[muscleId]
            assertNotNull("muscle $muscleId should have a default curve", curve)
            assertEquals("default", curve!!.source)
            assertEquals(0, curve.samples)
            assertEquals(RecoveryCurves.defaultHours(muscleId), curve.recoveryHours, 0.001)
        }
        for (componentName in componentNames) {
            val curve = populated.curves[componentName]
            assertNotNull("component $componentName should have a default curve", curve)
            assertEquals("default", curve!!.source)
            assertEquals(0, curve.samples)
            assertEquals(RecoveryCurves.COMPONENT_H, curve.recoveryHours, 0.001)
        }
        // Idempotent: calling populateDefaults on an already-populated state
        // should preserve existing curves (e.g. a learned curve stays).
        val learned = StateModel.RecoveryCurve(
            muscleId = "quads", recoveryHours = 30.0, source = "learned", samples = 5,
        )
        val withLearned = populated.copy(curves = populated.curves + ("quads" to learned))
        val rePopulated = RecoveryLearner.populateDefaults(withLearned, muscleIds, componentNames)
        // quads should still be the learned curve, not overwritten with default
        assertEquals(learned, rePopulated.curves["quads"])
    }
}
