package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlin.math.roundToInt

/**
 * OneRmLab — calibrated 1RM estimation + retest scheduling.
 *
 * BATCH-12: the legacy HTML app had a "1RM Lab" overlay that let the user
 *   - compute a fresh 1RM from a recent heavy set (Epley / Brzycki / Lombardi)
 *   - see when the next retest is due (6-week window from the last test)
 *   - audit every "tested 1RM" entry and remove outliers
 *
 * The native app stores tested 1RMs per-exercise in ExerciseRecord.tested1RM.
 * The Lab pulls those + the last 6 weeks of heavy sets and surfaces a list of
 * "retest recommended" exercises, plus an audit report for the user to
 * delete entries that look wrong.
 */
object OneRmLab {

    /** Formula identifiers. */
    const val F_EPLEY = "epley"
    const val F_BRZYCKI = "brzycki"
    const val F_LOMBARDI = "lombardi"

    data class Estimate(
        val exerciseId: String,
        val exerciseName: String,
        val weight: Double,
        val reps: Int,
        val formula: String,
        val estimated1Rm: Double,
    )

    data class AuditRow(
        val exerciseId: String,
        val exerciseName: String,
        val tested1Rm: Double,
        val estimated1Rm: Double,
        val deltaLbs: Double,
        val lastTestedAt: String?,
        val retestDue: Boolean,
        val suspicious: Boolean,
    )

    data class LabReport(
        val rows: List<AuditRow>,
        val retestDueCount: Int,
        val suspiciousCount: Int,
        val avgDeltaPct: Double,
    )

    /** Estimate a 1RM from a single heavy set. Returns 0.0 if inputs are bad. */
    fun estimate(weight: Double, reps: Int, formula: String = F_EPLEY): Double {
        if (weight <= 0 || reps < 1 || reps > 15) return 0.0
        return when (formula) {
            F_BRZYCKI -> weight * (36.0 / (37.0 - reps))
            F_LOMBARDI -> weight * Math.pow(reps.toDouble(), 0.10)
            else -> weight * (1.0 + reps / 30.0)  // Epley
        }
    }

    /** Per-exercise audit row from the user's record + recent heavy sets. */
    fun auditRow(exerciseId: String, record: ExerciseRecord): AuditRow? {
        val tested = record.tested1RM?.takeIf { it > 0 } ?: return null
        val mu = record.mu?.takeIf { it > 0 } ?: run {
            val heaviest = record.history
                .filter { !it.skipped && it.weight > 0 && it.reps.isNotEmpty() }
                .maxByOrNull { it.weight * (1.0 + (it.reps.maxOrNull() ?: 1.0) / 30.0) }
            heaviest?.let { h -> estimate(h.weight, (h.reps.maxOrNull() ?: 1.0).toInt()) } ?: tested
        }
        val delta = tested - mu
        val lastTestedAt = record.testDate
        val retestDue = lastTestedAt == null ||
            (System.currentTimeMillis() - ProState.utcDayMillis(lastTestedAt) > 6L * 7 * 24 * 3600 * 1000)
        val suspicious = kotlin.math.abs(delta) > tested * 0.20  // >20% off the estimate
        return AuditRow(
            exerciseId = exerciseId,
            exerciseName = exerciseId,  // engine resolves the friendly name
            tested1Rm = tested,
            estimated1Rm = mu,
            deltaLbs = delta,
            lastTestedAt = lastTestedAt,
            retestDue = retestDue,
            suspicious = suspicious,
        )
    }

    /** Build a full audit report across every exercise with a tested 1RM. */
    fun auditAll(ctx: Context): LabReport {
        val rows = ProState.data.exercises.entries.mapNotNull { (id, rec) -> auditRow(id, rec) }
        val retest = rows.count { it.retestDue }
        val susp = rows.count { it.suspicious }
        val avg = if (rows.isEmpty()) 0.0 else rows.map { kotlin.math.abs(it.deltaLbs) / it.tested1Rm * 100.0 }.average()
        ProLog.i("1RMLAB", "audit: ${rows.size} tested, $retest retest, $susp suspicious, avg Δ=${"%.1f".format(avg)}%")
        return LabReport(rows, retest, susp, avg)
    }

    /** Reset a single exercise's tested 1RM to null (audit fix-up). */
    fun resetOne(ctx: Context, exerciseId: String) {
        val d = ProState.data
        val rec = d.exercises[exerciseId] ?: return
        val updated = rec.copy(tested1RM = null, testDate = null)
        ProState.data = d.copy(exercises = d.exercises + (exerciseId to updated))
        ProState.saveWorkoutData()
        ProState.notifyChanged()
    }

    /** Reset every suspicious 1RM entry (audit cleanup). */
    fun resetSuspicious(ctx: Context): Int {
        val report = auditAll(ctx)
        report.rows.filter { it.suspicious }.forEach { resetOne(ctx, it.exerciseId) }
        return report.suspiciousCount
    }
}
