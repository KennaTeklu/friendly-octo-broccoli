package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog

/**
 * OneRmLab — BATCH-18.
 *
 * Lab helpers for 1RM estimation and audit:
 *   - weeksSinceTest(testDate): weeks elapsed since the last 1RM test.
 *   - confidenceLabel(sigma2): "Low" / "Medium" / "High" from the ERF variance.
 *   - estimate1RM(weight, reps, formula): one-shot estimation from a weight ×
 *     reps set, using one of four published formulas. Used by the OneRmLabScreen
 *     to let the user compare formulas against their tested 1RM.
 *   - resetCache(ctx): clears every exercise's tested1RM / mu / sigma2 / testDate
 *     for the active profile. History is preserved. Used by OneRMAuditScreen's
 *     "Reset 1RM cache" button.
 *
 * Storage: the lab itself is stateless (formulas are pure). resetCache() writes
 * through ProState.data.exercises (the per-profile Room store).
 */
object OneRmLab {

    enum class Formula(val id: String, val label: String) {
        EPLEY("epley", "Epley"),
        BRZYCKI("brzycki", "Brzycki"),
        LOMBARDI("lombardi", "Lombardi"),
        MAYHEW("mayhew", "Mayhew"),
    }

    /** Weeks elapsed since the last 1RM test. Returns 99 if no test date. */
    fun weeksSinceTest(testDate: String?): Int {
        if (testDate.isNullOrBlank()) return 99
        val ms = ProState.utcDayMillis(testDate).takeIf { it > 0 } ?: return 99
        val days = ((System.currentTimeMillis() - ms) / (24L * 3600 * 1000)).toInt()
        return (days / 7).coerceAtLeast(0)
    }

    /** Confidence label from the ERF variance (σ²). Lower variance = higher confidence. */
    fun confidenceLabel(sigma2: Double?): String = when {
        sigma2 == null -> "Unknown"
        sigma2 < 5.0 -> "High"
        sigma2 < 25.0 -> "Medium"
        else -> "Low"
    }

    /** Estimate a 1RM from a weight × reps set using the named formula.
     *  All formulas assume reps ≥ 1 and weight > 0. */
    fun estimate1RM(weight: Double, reps: Int, formula: Formula): Double {
        if (weight <= 0 || reps < 1) return 0.0
        return when (formula) {
            // 1RM = w * (1 + r/30)
            Formula.EPLEY -> weight * (1 + reps / 30.0)
            // 1RM = w * 36 / (37 - r)
            Formula.BRZYCKI -> weight * 36.0 / (37 - reps)
            // 1RM = w * reps^0.10
            Formula.LOMBARDI -> weight * Math.pow(reps.toDouble(), 0.10)
            // 1RM = w * (100 / (52.2 + 41.9 * e^(-0.055 * r)))
            Formula.MAYHEW -> weight * 100.0 / (52.2 + 41.9 * Math.exp(-0.055 * reps))
        }
    }

    /** Run every formula and return label → estimate. */
    fun allEstimates(weight: Double, reps: Int): Map<String, Double> =
        Formula.values().associate { it.label to estimate1RM(weight, reps, it) }

    /** Reset every exercise's 1RM cache (tested1RM, mu, sigma2, testDate).
     *  History is preserved. */
    fun resetCache(ctx: Context) {
        val updated = ProState.data.exercises.mapValues { (_, rec) ->
            rec.copy(
                tested1RM = null,
                testDate = null,
                testReps = null,
                testWeight = null,
                mu = null,
                sigma2 = null,
                residuals = emptyList(),
                avgMuW = null,
                techniqueFactor = null,
                lastUpdate = null,
                wHistory = emptyList(),
            )
        }
        ProState.data = ProState.data.copy(exercises = updated)
        ProState.saveWorkoutData()
        ProLog.i("ONERMLAB", "1RM cache cleared for ${updated.size} exercises")
    }
}
