package com.peakform.fitness.core

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * OneRmLab — BATCH-18A.
 *
 * Pure-function 1RM estimation laboratory. Wraps the legacy Epley / Brzycki /
 * Lombardi / Mayhew formulas in one place so the OneRmLabScreen and the
 * CoachingEngine can ask "given weight × reps, what's the 1RM?" without
 * pulling in the full Kalman engine.
 *
 * Each formula returns pounds (input unit). The "consensus" output is the
 * median of all four — more robust than picking one.
 *
 * Also exposes the inverse: given a target 1RM, what weight × reps combo
 * trains it for a given RPE / percentage.
 */
object OneRmLab {

    /** Epley: 1RM = w × (1 + reps/30) */
    fun epley(weight: Double, reps: Int): Double = weight * (1 + reps / 30.0)

    /** Brzycki: 1RM = w × 36 / (37 - reps) */
    fun brzycki(weight: Double, reps: Int): Double = weight * 36.0 / (37 - reps).coerceAtLeast(1)

    /** Lombardi: 1RM = w × reps^0.10 */
    fun lombardi(weight: Double, reps: Int): Double = weight * reps.toDouble().pow(0.10)

    /** Mayhew: 1RM = 100 × w / (52.2 + 41.9 × e^(-0.055 × reps)) */
    fun mayhew(weight: Double, reps: Int): Double {
        val denom = 52.2 + 41.9 * kotlin.math.exp(-0.055 * reps)
        return 100.0 * weight / denom
    }

    /** Consensus — median of all four formulas. */
    fun consensus(weight: Double, reps: Int): Double {
        val list = listOf(
            epley(weight, reps),
            brzycki(weight, reps),
            lombardi(weight, reps),
            mayhew(weight, reps),
        ).sorted()
        return list[1] + list[2] / 2.0  // average of middle two = median
    }

    /** RPE-adjusted 1RM — accounts for reps in reserve. */
    fun adjusted(weight: Double, reps: Int, rpe: Int): Double {
        val rir = 10 - rpe.coerceIn(1, 10)
        val totalReps = reps + rir
        return consensus(weight, totalReps)
    }

    /** Percentage table for a given 1RM (50%-100% in 5% steps). */
    fun percentageTable(oneRm: Double): List<Pair<Int, Double>> =
        (50..100 step 5).map { pct -> pct to (oneRm * pct / 100.0).roundToInt().toDouble() }

    /** Inverse — given a 1RM and target %, return the weight × reps prescription. */
    fun prescription(oneRm: Double, pct: Int, repTarget: Int): Pair<Double, Int> {
        val w = (oneRm * pct / 100.0).roundToInt().toDouble()
        return w to repTarget
    }
}
