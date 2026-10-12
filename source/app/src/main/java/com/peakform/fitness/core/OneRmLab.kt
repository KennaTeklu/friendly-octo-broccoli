package com.peakform.fitness.core

import android.content.Context

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * BATCH-13 Item 4A: 1RM Lab.
 *
 * Estimation methods: Epley, Brzycki, Lombardi, RPE-based.
 * Confidence interval based on reps.
 * Working weight recommendations at 65/75/85/95% 1RM.
 */
object OneRmLab {

    @kotlinx.serialization.Serializable
    data class EstimateResult(
        val epley: Double,
        val brzycki: Double,
        val lombardi: Double,
        val rpeBased: Double,
        val average: Double,
        val confidenceLow: Double,
        val confidenceHigh: Double,
    )

    @kotlinx.serialization.Serializable
    data class WorkingWeight(
        val pct: Int,
        val weight: Double,
    )

    @kotlinx.serialization.Serializable
    data class Snapshot(
        val exerciseId: String,
        val date: String,
        val weight: Double,
        val reps: Int,
        val rpe: Double?,
        val estimate: EstimateResult,
    )

    private const val SNAPSHOT_KEY = "p13_1rm_snapshots"

    /** Epley: 1RM = w * (1 + r/30) */
    fun epley(weight: Double, reps: Int): Double = weight * (1 + reps.toDouble() / 30)

    /** Brzycki: 1RM = w * 36 / (37 - r) */
    fun brzycki(weight: Double, reps: Int): Double = weight * 36.0 / (37 - reps)

    /** Lombardi: 1RM = w * r^0.10 */
    fun lombardi(weight: Double, reps: Int): Double = weight * reps.toDouble().pow(0.10)

    /** RPE-based: if RPE < 10, estimate what 10 RPE would be (1RM).
     *  RPE 10 = max effort. Each RPE below 10 = ~3% less than 1RM per rep. */
    fun rpeBased(weight: Double, reps: Int, rpe: Double?): Double {
        if (rpe == null) return epley(weight, reps)
        val rpeDeficit = (10.0 - rpe) * 0.03 * reps
        return weight / (1 - rpeDeficit)
    }

    /** Full estimate with confidence interval. */
    fun estimate(weight: Double, reps: Int, rpe: Double? = null): EstimateResult {
        val e = epley(weight, reps)
        val b = brzycki(weight, reps)
        val l = lombardi(weight, reps)
        val r = rpeBased(weight, reps, rpe)
        val avg = listOf(e, b, l, r).average()
        // Confidence: lower reps = higher confidence. CI widens with reps.
        val spread = when {
            reps <= 3 -> 0.02
            reps <= 5 -> 0.04
            reps <= 8 -> 0.06
            reps <= 12 -> 0.10
            else -> 0.15
        }
        return EstimateResult(
            epley = e.roundTo(1),
            brzycki = b.roundTo(1),
            lombardi = l.roundTo(1),
            rpeBased = r.roundTo(1),
            average = avg.roundTo(1),
            confidenceLow = (avg * (1 - spread)).roundTo(1),
            confidenceHigh = (avg * (1 + spread)).roundTo(1),
        )
    }

    /** Working weights at 65/75/85/95% of estimated 1RM. */
    fun workingWeights(estimate: Double): List<WorkingWeight> {
        return listOf(65, 75, 85, 95).map { pct ->
            WorkingWeight(pct, (estimate * pct / 100.0).roundToNearest())
        }
    }

    /** Save a snapshot for the exercise history. */
    fun saveSnapshot(ctx: Context, snapshot: Snapshot) {
        val key = SNAPSHOT_KEY + "_" + snapshot.exerciseId
        val existing = ProfileSettings.get(ctx, key) ?: "[]"
        val list = try {
            ProJson.json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(Snapshot.serializer()), existing)
        } catch (_: Exception) { emptyList() }
        val updated = (list + snapshot).takeLast(20)  // keep last 20
        ProfileSettings.put(ctx, key, ProJson.json.encodeToString(kotlinx.serialization.builtins.ListSerializer(Snapshot.serializer()), updated))
    }

    fun getSnapshots(ctx: Context, exerciseId: String): List<Snapshot> {
        val key = SNAPSHOT_KEY + "_" + exerciseId
        val raw = ProfileSettings.get(ctx, key) ?: return emptyList()
        return try { ProJson.json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(Snapshot.serializer()), raw) } catch (_: Exception) { emptyList() }
    }

    private fun Double.roundTo(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToInt() / factor
    }

    private fun Double.roundToNearest(): Double {
        // Round to nearest 5 lbs
        return (this / 5).roundToInt() * 5.0
    }
}
