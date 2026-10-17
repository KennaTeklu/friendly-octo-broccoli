package com.peakform.fitness.core.state

import com.peakform.fitness.ProLog
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.core.state.StateModel.ExerciseRecord
import com.peakform.fitness.core.state.StateModel.RecoveryCurve
import com.peakform.fitness.core.state.StateModel.RecoveryState
import com.peakform.fitness.core.state.StateModel.SessionRecord
import com.peakform.fitness.core.state.StateModel.SetRecord
import kotlin.math.max
import kotlin.math.min

/**
 * BATCH-39g — Recovery curve learning.
 *
 * Source: source/docs/system/09-recovery-curves.md § "Learned curves" and
 * BATCH 39g spec.
 *
 * Learns the user's actual per-muscle recovery time from their session
 * history. v39e ships authored defaults (48h compound, 24h accessory, …);
 * this object recomputes the curve per muscle once enough sessions are
 * logged, and writes the learned curve back into [ProState.recovery].
 *
 * The field's decay then uses the learned curve automatically —
 * [com.peakform.fitness.core.ProState.decayField] reads
 * `recovery.curves[muscleId]?.recoveryHours` and falls back to
 * [RecoveryCurves.defaultHours] when no learned curve exists. Once
 * [recomputeAll] writes a learned curve, decay uses it.
 *
 * ## Algorithm (per muscle / per component)
 *
 * For each muscle with at least 3 sessions logged:
 *
 * 1. Read all sessions where that muscle was a primary target.
 *    "Primary target" means: the session has at least one exercise whose
 *    dossier's `stimulusProfile` weight for this muscle is `>= 0.5`.
 *
 * 2. For consecutive pairs (session A, session B) where B is the next
 *    session targeting the same muscle:
 *       gap = (B.timestamp - A.timestamp) / 3_600_000.0  (in hours)
 *
 * 3. Look at B's performance vs A's performance on the same exercise if
 *    the exercises match, or on exercises sharing the muscle if they
 *    differ. Performance = `sum(reps × load)` across sets (skipped and
 *    incomplete sets excluded). For hold-based exercises (durationSeconds
 *    > 0), performance = `sum(durationSeconds / 30.0 × load)` — but the
 *    load is usually 0 for holds, so performance reduces to total
 *    duration / 30.
 *
 * 4. If `B.performance >= A.performance × 0.95`: the gap was sufficient.
 *    Record `gap` as a sample.
 *    If `B.performance < A.performance × 0.95`: the gap was too short.
 *    Record `gap × 1.15` as the required recovery (need more rest).
 *
 * 5. After collecting N samples for the muscle:
 *       learned = median(samples)
 *       Clamp learned to [12h, 96h] for muscles, [8h, 72h] for components.
 *
 * 6. Blend with the default using Bayesian shrinkage:
 *       weight = min(samples.size / 20.0, 1.0)
 *       recoveryHours = weight × learned + (1 - weight) × default
 *       samples = samples.size
 *       source = "learned" when samples >= 3, else "default"
 *
 * 7. Write back to RecoveryState:
 *       curves[muscleId] = RecoveryCurve(muscleId, recoveryHours, source, samples)
 *
 * Components use the same algorithm but the "primary" signal is which
 * components the session's exercises declared (via `componentStimulus`).
 * Minimum 5 samples before a component curve is marked learned (vs 3 for
 * muscles). Components have a wider clamp: [8h, 72h].
 *
 * ## When it runs
 *
 * Called from:
 *   - [com.peakform.fitness.core.ProState.appendSession] — after the
 *     field write, wrapped in Dispatchers.IO.
 *   - [com.peakform.fitness.core.ProState.loadAll] — once at startup,
 *     cheap because it reads from the in-memory sessions list.
 *
 * ## What it does NOT do
 *
 * - No changes to the field write or accumulation (v39b/v39e intact).
 * - No changes to decay (decayField unchanged — it reads the learned
 *   curve automatically).
 * - No changes to default curves (v39e's authored defaults are unchanged
 *   — they remain the fallback and the shrinkage target).
 * - No UI changes — the learned curve is invisible.
 *
 * The object is **pure** — it takes sessions + the dossier registry +
 * the current RecoveryState, and returns a new RecoveryState. It does
 * not touch ProState directly (the caller does the writeback + persist).
 */
object RecoveryLearner {

    private const val TAG = "RECOVERY"

    /** Minimum sample count for a muscle's curve to be marked "learned". */
    const val MUSCLE_LEARN_THRESHOLD = 3

    /** Minimum sample count for a component's curve to be marked "learned". */
    const val COMPONENT_LEARN_THRESHOLD = 5

    /** Sample count at which the shrinkage weight reaches 1.0 (full learned, no default). */
    const val SHRINKAGE_FULL_AT = 20

    /** Stimulus profile weight threshold for "primary target" — muscles with
     *  weight >= this in any of the session's exercises are considered
     *  targeted by that session. */
    const val PRIMARY_WEIGHT_THRESHOLD = 0.5

    /** Performance drop threshold — if B's performance is below A's by more
     *  than this fraction, the gap was too short. */
    const val PERF_HOLD_FRACTION = 0.95

    /** Gap inflation when the gap was too short (B underperformed A). */
    const val GAP_INFLATION = 1.15

    /** Min/max clamp for muscle curves (hours). */
    const val MUSCLE_MIN_H = 12.0
    const val MUSCLE_MAX_H = 96.0

    /** Min/max clamp for component curves (hours). Wider than muscles. */
    const val COMPONENT_MIN_H = 8.0
    const val COMPONENT_MAX_H = 72.0

    /**
     * Recompute every muscle + component curve from [sessions] and merge
     * into [current]. Returns a new [RecoveryState] with learned curves
     * for muscles/components that have enough samples, and unchanged
     * curves for the rest.
     *
     * Pure — does not touch ProState. The caller is responsible for
     * writing the result back to [com.peakform.fitness.core.ProState.recovery]
     * and persisting it via saveRecovery().
     *
     * @param sessions  the active profile's session history (append-only)
     * @param current   the current RecoveryState (defaults are preserved
     *                  for muscles with insufficient samples)
     */
    fun recomputeAll(
        sessions: List<SessionRecord>,
        current: RecoveryState,
    ): RecoveryState {
        val newCurves = current.curves.toMutableMap()

        // ---- Muscles ----
        // Build a map from muscleId → list of (session, performance-per-exercise)
        // where the muscle was a primary target (dossier.stimulusProfile weight >= 0.5).
        val muscleSessions = HashMap<String, MutableList<MuscleSessionSample>>()
        for (session in sessions) {
            // For each exercise in the session, find its dossier and check
            // which muscles it primarily targets.
            for (ex in session.exercises) {
                if (ex.skipped) continue
                val dossier = DossierRegistry.forExercise(ex.exerciseId) ?: continue
                val perf = performanceFor(ex)
                if (perf <= 0.0) continue
                for ((muscleId, weight) in dossier.stimulusProfile) {
                    if (weight < PRIMARY_WEIGHT_THRESHOLD) continue
                    val list = muscleSessions.getOrPut(muscleId) { mutableListOf() }
                    list.add(MuscleSessionSample(session.timestamp, ex.exerciseId, perf))
                }
            }
        }

        // For each muscle with >= 1 sample, compute the learned curve (or
        // leave at default if < MUSCLE_LEARN_THRESHOLD).
        for ((muscleId, samples) in muscleSessions) {
            val defaultH = RecoveryCurves.defaultHours(muscleId)
            val learned = computeLearnedCurve(
                muscleId = muscleId,
                samples = samples,
                defaultH = defaultH,
                minH = MUSCLE_MIN_H,
                maxH = MUSCLE_MAX_H,
                learnThreshold = MUSCLE_LEARN_THRESHOLD,
            )
            newCurves[muscleId] = learned
            ProLog.i(TAG, formatLogLine(muscleId, learned))
        }

        // ---- Components ----
        // Same algorithm but the "primary" signal is componentStimulus weight
        // >= 0.5. The performance signal is the same (reps × load).
        val componentSessions = HashMap<String, MutableList<MuscleSessionSample>>()
        for (session in sessions) {
            for (ex in session.exercises) {
                if (ex.skipped) continue
                val dossier = DossierRegistry.forExercise(ex.exerciseId) ?: continue
                val perf = performanceFor(ex)
                if (perf <= 0.0) continue
                for ((componentName, weight) in dossier.componentStimulus) {
                    if (weight < PRIMARY_WEIGHT_THRESHOLD) continue
                    val list = componentSessions.getOrPut(componentName) { mutableListOf() }
                    list.add(MuscleSessionSample(session.timestamp, ex.exerciseId, perf))
                }
            }
        }
        for ((componentName, samples) in componentSessions) {
            val defaultH = RecoveryCurves.defaultHoursForComponent(componentName)
            val learned = computeLearnedCurve(
                muscleId = componentName,
                samples = samples,
                defaultH = defaultH,
                minH = COMPONENT_MIN_H,
                maxH = COMPONENT_MAX_H,
                learnThreshold = COMPONENT_LEARN_THRESHOLD,
            )
            newCurves[componentName] = learned
            ProLog.i(TAG, formatLogLine(componentName, learned))
        }

        return RecoveryState(curves = newCurves)
    }

    /**
     * Compute the learned curve for one muscle/component from its session
     * samples. Returns a [RecoveryCurve] with:
     *
     *   - source = "learned" when sample count >= [learnThreshold]
     *   - source = "default" otherwise (curve still carries the sample
     *     count for observability)
     *   - recoveryHours = blend of learned median + default per the
     *     Bayesian shrinkage formula
     *
     * The [muscleId] is the muscle or component name this curve is for.
     * The caller writes the returned curve into the map under that key.
     *
     * Pure — no side effects.
     */
    private fun computeLearnedCurve(
        muscleId: String,
        samples: List<MuscleSessionSample>,
        defaultH: Double,
        minH: Double,
        maxH: Double,
        learnThreshold: Int,
    ): RecoveryCurve {
        // Sort samples by timestamp so consecutive pairs are well-defined.
        val sorted = samples.sortedBy { it.timestamp }
        // Step 2-4: for each consecutive pair, compute the gap and decide
        // whether it was sufficient (B held A's performance) or too short
        // (B underperformed A → inflate the gap).
        val gapSamples = ArrayList<Double>(sorted.size - 1)
        for (i in 1 until sorted.size) {
            val a = sorted[i - 1]
            val b = sorted[i]
            val gapHours = (b.timestamp - a.timestamp) / 3_600_000.0
            if (gapHours <= 0.0) continue  // same-timestamp sessions: skip
            // Performance comparison: B vs A. If exercises match, compare
            // directly. If they differ but both target this muscle, compare
            // B's performance to A's performance as a proxy.
            val aPerf = a.performance
            val bPerf = b.performance
            if (aPerf <= 0.0) continue
            val ratio = bPerf / aPerf
            val sample = if (ratio >= PERF_HOLD_FRACTION) {
                // B held A's performance → gap was sufficient.
                gapHours
            } else {
                // B underperformed → gap was too short. Inflate.
                gapHours * GAP_INFLATION
            }
            gapSamples.add(sample)
        }

        // Step 5: median of samples, clamped.
        val sampleCount = gapSamples.size
        val source = if (sampleCount >= learnThreshold) "learned" else "default"

        // When there are zero gap samples (only one session, or all sessions
        // at the same timestamp), the curve stays at default — surface the
        // raw session count for observability.
        if (sampleCount == 0) {
            return RecoveryCurve(
                muscleId = muscleId,
                recoveryHours = defaultH,
                source = "default",
                samples = sorted.size,
            )
        }

        val learned = median(gapSamples).coerceIn(minH, maxH)
        // Step 6: Bayesian shrinkage.
        val weight = min(sampleCount.toDouble() / SHRINKAGE_FULL_AT, 1.0)
        val blended = weight * learned + (1.0 - weight) * defaultH

        return RecoveryCurve(
            muscleId = muscleId,
            recoveryHours = blended,
            source = source,
            samples = sampleCount,
        )
    }

    /**
     * Performance = sum(reps × load) across sets. Skipped and incomplete
     * sets excluded. For hold-based exercises (durationSeconds > 0),
     * performance = sum(durationSeconds / 30.0 × max(load, 1.0)) — the
     * `max(load, 1.0)` keeps bodyweight holds from collapsing to zero
     * performance (load=0 for bodyweight).
     *
     * Returns 0.0 for skipped exercises or exercises with no completed sets.
     */
    private fun performanceFor(ex: ExerciseRecord): Double {
        if (ex.skipped) return 0.0
        var total = 0.0
        for (set in ex.sets) {
            if (!set.completed) continue
            total += setPerformance(set)
        }
        return total
    }

    /** Per-set performance contribution. */
    private fun setPerformance(set: SetRecord): Double {
        return if (set.durationSeconds != null && set.durationSeconds > 0.0) {
            // Hold: duration / 30 × max(load, 1.0). The max() keeps
            // bodyweight holds (load=0) from collapsing to zero — they
            // count as duration / 30 reps-equivalent.
            (set.durationSeconds / 30.0) * max(set.load, 1.0)
        } else {
            // Reps-based: reps × load. Bodyweight reps have load=0 →
            // performance=0, but bodyweight exercises still contribute
            // to the muscle's session count (which is the minimum bar
            // for the curve to flip to "learned"). Their gap samples
            // are skipped because performance=0 fails the aPerf > 0
            // guard in computeLearnedCurve.
            set.reps.toDouble() * set.load
        }
    }

    /** Median of a list of doubles. Sorts the list in-place. */
    private fun median(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        val sorted = values.sorted()
        val n = sorted.size
        return if (n % 2 == 1) {
            sorted[n / 2]
        } else {
            (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        }
    }

    /**
     * Format the debug log line for one muscle/component curve:
     *
     *     "chest: 42.3h (learned, 8 samples)"
     *     "quads: 48.0h (default, 2 samples)"
     *
     * Visible in logcat with tag RECOVERY. Not visible to users.
     */
    private fun formatLogLine(id: String, curve: RecoveryCurve): String {
        val hoursStr = String.format("%.1f", curve.recoveryHours)
        return "$id: ${hoursStr}h (${curve.source}, ${curve.samples} samples)"
    }

    /**
     * One sample for the learning algorithm — a session that targeted a
     * specific muscle/component. The [performance] field is the total
     * `reps × load` (or duration-equivalent) for the exercise(s) in that
     * session that targeted the muscle.
     */
    private data class MuscleSessionSample(
        val timestamp: Long,
        val exerciseId: String,
        val performance: Double,
    )

    /**
     * Migration helper: populate [RecoveryState.curves] with v39e defaults
     * for every muscle in the taxonomy + every component. Used by loadAll
     * when RecoveryState is empty so nothing reads null.
     *
     * Returns a new RecoveryState with default curves for every bucketed
     * muscle + every component. The map keys are muscle ids and component
     * short names; the values are all `source = "default", samples = 0`.
     *
     * This is idempotent — calling it on a state that already has curves
     * preserves the existing learned curves and only fills in missing
     * defaults.
     */
    fun populateDefaults(
        current: RecoveryState,
        muscleIds: List<String>,
        componentNames: List<String>,
    ): RecoveryState {
        val newCurves = current.curves.toMutableMap()
        for (muscleId in muscleIds) {
            if (muscleId !in newCurves) {
                newCurves[muscleId] = RecoveryCurves.defaultCurve(muscleId)
            }
        }
        for (componentName in componentNames) {
            if (componentName !in newCurves) {
                newCurves[componentName] = RecoveryCurves.defaultCurveForComponent(componentName)
            }
        }
        return RecoveryState(curves = newCurves)
    }
}
