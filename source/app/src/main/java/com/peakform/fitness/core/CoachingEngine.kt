package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.engine.Stats

/**
 * CoachingEngine — BATCH-8 coach-led recommendation layer.
 *
 * Pulls from Stats (longevity score, recovery report, strength forecast) and
 * the user's history to produce a short list of coach-style recommendations
 * the UI surfaces in the new coach-led flow (Dashboard cards, Workout
 * context block, ReferenceGaps panel). All logic is offline — no network,
 * no analytics.
 *
 * The engine does not mutate state — it only reads ProState.data + Stats
 * and returns immutable Recommendation rows. The UI decides what to do with
 * them (route to 1RM Lab, suggest a deload, etc.).
 */
object CoachingEngine {

    data class Recommendation(
        val id: String,
        val title: String,
        val detail: String,
        val icon: String,
        val action: String,        // section route key (e.g. "onerm_audit")
        val urgency: String,        // "info" | "warn" | "bad"
    )

    /** Compute the current coach recommendation stack (BATCH-8). */
    fun evaluate(ctx: Context): List<Recommendation> {
        val out = mutableListOf<Recommendation>()
        try {
            val longevity = Stats.calculateLongevityScore()
            val recovery = Stats.recoveryReport()
            val forecast = Stats.strengthForecast()
            val exercises = ProState.data.exercises

            // 1) 1RM audit: any calibrated lift stale >6 weeks?
            if (Nudges.retestDue(exercises, System.currentTimeMillis())) {
                out += Recommendation(
                    id = "retest_due",
                    title = "Re-test a 1RM",
                    detail = "One or more calibrated lifts are 6+ weeks old. A fresh test keeps the prescription engine honest.",
                    icon = "fa-gauge-high",
                    action = "onerm_audit",
                    urgency = "warn",
                )
            }

            // 2) Recovery: very low → deload; very high → push.
            when {
                recovery.overall < 50 -> out += Recommendation(
                    id = "deload",
                    title = "Plan a deload week",
                    detail = "Systemic readiness is ${recovery.overall}%. A deload this week protects progress.",
                    icon = "fa-leaf",
                    action = "recovery",
                    urgency = "bad",
                )
                recovery.overall >= 85 -> out += Recommendation(
                    id = "push",
                    title = "Push a heavy session",
                    detail = "Readiness ${recovery.overall}% — a great window to test a PR or heavy double.",
                    icon = "fa-bolt",
                    action = "workout",
                    urgency = "info",
                )
            }

            // 3) Longevity gaps: pull the lowest sub-scores.
            longevity.sub.entries.sortedBy { it.value }.take(2).forEach { (k, v) ->
                if (v < 50.0) {
                    out += Recommendation(
                        id = "longevity_gap_$k",
                        title = "Train $k",
                        detail = "Your ${k.replace('_', ' ')} score is ${v.toInt()}/100. A focused session this week lifts the longevity total.",
                        icon = "fa-heart-pulse",
                        action = "recovery",
                        urgency = "info",
                    )
                }
            }

            // 4) Forecast: top-3 lifts — surface the weakest of S/B/D if it lags.
            val sbd = forecast.associate { it.lift to it.est1RM }
            val squat = sbd["squat"]; val bench = sbd["bench_press"]; val deadlift = sbd["deadlift"]
            if (squat != null && bench != null && deadlift != null) {
                val min = minOf(squat, bench, deadlift)
                val max = maxOf(squat, bench, deadlift)
                if (max > 0 && (max - min) / max > 0.20) {
                    val weakest = when (min) { squat -> "squat"; bench -> "bench_press"; else -> "deadlift" }
                    out += Recommendation(
                        id = "weak_lift_$weakest",
                        title = "Bring up $weakest",
                        detail = "Your $weakest est. 1RM (${min.toInt()} lbs) is >20% behind your strongest lift. Cycle in a focused block.",
                        icon = "fa-dumbbell",
                        action = "workout",
                        urgency = "info",
                    )
                }
            }
        } catch (e: Exception) {
            ProLog.w("COACH", "evaluate failed: ${e.message}")
        }
        return out
    }
}
