package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * OneRmLab — 1-rep-max estimation lab (BATCH-12).
 *
 * Implements the four standard 1RM estimation formulas, an RPE-based
 * adjustment, a confidence interval, working-weight percentages for
 * programming (65/75/85/95%), and a per-exercise snapshot history.
 *
 * All snapshot data is per-profile, stored via ProfileSettings (SharedPreferences).
 */
object OneRmLab {
    private const val K_SNAPSHOTS = "p4_1rm_snapshots"

    @Serializable
    data class EstimateResult(
        val weightLifted: Double,
        val reps: Int,
        val rpe: Double?,
        val epley: Double,
        val brzycki: Double,
        val lombardi: Double,
        val rpeBased: Double?,
        val average: Double,
        val confidenceLow: Double,
        val confidenceHigh: Double,
        val at: String,
        val exerciseId: String,
        val exerciseName: String,
    ) {
        /** Working-weight percentages for programming. */
        fun workingWeight(pct: Double): Double = average * pct
    }

    @Serializable
    data class Snapshot(
        val exerciseId: String,
        val exerciseName: String,
        val result: EstimateResult,
    )

    /**
     * Epley: 1RM = w × (1 + reps/30)
     * Best for reps ≤ 10. Confidence drops sharply above 10 reps.
     */
    fun epley(weight: Double, reps: Int): Double = weight * (1 + reps / 30.0)

    /**
     * Brzycki: 1RM = w × 36 / (37 - reps)
     * Best for reps ≤ 12. Most accurate in the 1–10 rep range.
     */
    fun brzycki(weight: Double, reps: Int): Double {
        if (reps >= 37) return weight * 36.0 / 1.0  // saturate
        return weight * 36.0 / (37.0 - reps)
    }

    /**
     * Lombardi: 1RM = w × reps^0.10
     * Tends to overestimate at high reps — used as an upper-bound cross-check.
     */
    fun lombardi(weight: Double, reps: Int): Double = weight * Math.pow(reps.toDouble(), 0.10)

    /**
     * RPE-based estimation (Helms / RPE chart): if the user reports an RPE,
     * estimate how many MORE reps they could have done and adjust.
     *
     * RPE 10 = 0 reps in reserve (RIR), 9 = 1, 8 = 2, ... so we can compute
     * an "all-out" 1RM using the same formulas with reps + RIR.
     */
    fun rpeBased(weight: Double, reps: Int, rpe: Double): Double? {
        if (rpe <= 0 || rpe > 10) return null
        val rir = (10 - rpe).toInt().coerceAtLeast(0)
        val effectiveReps = reps + rir
        if (effectiveReps <= 0) return weight
        return brzycki(weight, effectiveReps)
    }

    /**
     * Compute a full EstimateResult for a single set.
     * Reps should be ≤ 12 for the most reliable estimates.
     */
    fun estimate(
        exerciseId: String,
        exerciseName: String,
        weight: Double,
        reps: Int,
        rpe: Double? = null,
        at: String = ProState.nowIso(),
    ): EstimateResult {
        val epl = if (reps <= 10) epley(weight, reps) else epley(weight, 10)
        val brz = if (reps <= 12) brzycki(weight, reps) else brzycki(weight, 12)
        val lom = lombardi(weight, reps)
        val rpeEst = rpe?.let { rpeBased(weight, reps, it) }
        val methods = mutableListOf(epl, brz, lom)
        if (rpeEst != null) methods.add(rpeEst)
        val average = methods.average()
        // Confidence interval: ±8% (rough — empirical across the four methods).
        val confidenceLow = average * 0.92
        val confidenceHigh = average * 1.08
        return EstimateResult(
            weightLifted = weight,
            reps = reps,
            rpe = rpe,
            epley = epl,
            brzycki = brz,
            lombardi = lom,
            rpeBased = rpeEst,
            average = average,
            confidenceLow = confidenceLow,
            confidenceHigh = confidenceHigh,
            at = at,
            exerciseId = exerciseId,
            exerciseName = exerciseName,
        )
    }

    /** Save a snapshot to per-profile storage (history for the lab screen). */
    fun saveSnapshot(ctx: Context, snap: Snapshot) {
        val list = getSnapshots(ctx).toMutableList()
        list.add(snap)
        val trimmed = list.takeLast(100)
        ProfileSettings.put(ctx, K_SNAPSHOTS,
            Json.encodeToString(ListSerializer(Snapshot.serializer()), trimmed))
    }

    fun getSnapshots(ctx: Context): List<Snapshot> = try {
        val raw = ProfileSettings.get(ctx, K_SNAPSHOTS) ?: return emptyList()
        Json.decodeFromString(ListSerializer(Snapshot.serializer()), raw)
    } catch (_: Exception) { emptyList() }

    /** Snapshots for one exercise only, newest last. */
    fun snapshotsFor(ctx: Context, exerciseId: String): List<Snapshot> =
        getSnapshots(ctx).filter { it.exerciseId == exerciseId }
}
