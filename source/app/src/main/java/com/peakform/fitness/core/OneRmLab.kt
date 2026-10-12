package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.engine.Stats
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * OneRmLab — BATCH-12 1RM experimentation + audit core.
 *
 * Provides read-only audit views over the per-profile 1RM model (tested vs.
 * est., staleness, residuals spread) plus the batch actions surfaced by the
 * 1RM Lab screen:
 *   - clearStale(ctx, weeks): drop tested1RM records older than N weeks.
 *   - decayAll(ctx, factor): apply a global mu shrink (e.g. for a deload
 *     block where 1RMs are temporarily depressed).
 *   - reEstimateMissing(ctx): seed mu for any exercise record that has a
 *     best-weight history but no mu (legacy gap from the v18 migration).
 *
 * The Lab never mutates the underlying Library (1,413 default exercises) —
 * it only adjusts ExerciseRecord rows on the active profile.
 */
object OneRmLab {

    data class AuditRow(
        val id: String,
        val name: String,
        val tested: Double?,
        val est: Double?,
        val weeksOld: Int,            // -1 = never tested
        val residualSpread: Double,   // 0 = single sample, larger = more variance
    )

    /** Build the audit view (newest-tested first, then never-tested). */
    fun audit(ctx: Context): List<AuditRow> = try {
        ProState.data.exercises.entries.mapNotNull { (id, rec) ->
            val name = Library.getExerciseById(id, includeBodyweight = true)?.name
                ?: id.replace('_', ' ').replaceFirstChar { it.uppercase() }
            val weeks = rec.testDate?.let { d ->
                ((System.currentTimeMillis() - ProState.utcDayMillis(d)) / (7L * 86400000)).toInt()
            } ?: -1
            val spread = rec.residuals.takeIf { it.size >= 2 }?.let { rs ->
                val mean = rs.average()
                sqrt(rs.map { (it - mean) * (it - mean) }.average())
            } ?: 0.0
            AuditRow(id, name, rec.tested1RM, rec.mu, weeks, spread)
        }.sortedWith(compareByDescending<AuditRow> { it.weeksOld }.thenBy { it.name })
    } catch (e: Exception) {
        ProLog.w("ONERMLAB", "audit failed: ${e.message}")
        emptyList()
    }

    /** Drop tested1RM records older than `weeks`. Returns the count cleared. */
    fun clearStale(ctx: Context, weeks: Int): Int {
        val cutoff = System.currentTimeMillis() - weeks.toLong() * 7L * 86400000
        var cleared = 0
        val updated = ProState.data.exercises.mapValues { (id, rec) ->
            val ts = rec.testDate?.let { ProState.utcDayMillis(it) } ?: 0L
            if (ts > 0 && ts < cutoff && rec.tested1RM != null) {
                cleared++
                rec.copy(tested1RM = null, testDate = null)
            } else rec
        }
        if (cleared > 0) {
            ProState.data = ProState.data.copy(exercises = updated)
            ProState.saveWorkoutData()
            ProLog.i("ONERMLAB", "cleared $cleared stale 1RM records")
        }
        return cleared
    }

    /** Apply a uniform mu decay (factor <1 shrinks). Returns the count affected. */
    fun decayAll(ctx: Context, factor: Double): Int {
        if (factor >= 1.0) return 0
        var count = 0
        val updated = ProState.data.exercises.mapValues { (id, rec) ->
            val mu = rec.mu ?: return@mapValues rec
            count++
            rec.copy(mu = mu * factor, sigma2 = (rec.sigma2 ?: (0.2 * mu * factor).pow(2)))
        }
        if (count > 0) {
            ProState.data = ProState.data.copy(exercises = updated)
            ProState.saveWorkoutData()
            ProLog.i("ONERMLAB", "decayed $count 1RM records by ×$factor")
        }
        return count
    }

    /** Seed mu for any record with best-weight history but no mu (legacy gap). */
    fun reEstimateMissing(ctx: Context): Int {
        var count = 0
        val updated = ProState.data.exercises.mapValues { (id, rec) ->
            if (rec.mu != null) return@mapValues rec
            val best = rec.bestWeight ?: rec.history.lastOrNull { !it.skipped }?.weight ?: return@mapValues rec
            val reps = rec.bestReps ?: rec.history.lastOrNull { !it.skipped }?.reps?.maxOrNull() ?: 5.0
            count++
            rec.copy(mu = OneRm.roundToNearest(best * (1 + reps / 30.0)))
        }
        if (count > 0) {
            ProState.data = ProState.data.copy(exercises = updated)
            ProState.saveWorkoutData()
            ProLog.i("ONERMLAB", "seeded $count missing 1RM estimates")
        }
        return count
    }
}
