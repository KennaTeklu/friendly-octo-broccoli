package com.peakform.fitness.core

import android.content.Context

/**
 * SelfAssessment — BATCH-14: lightweight daily readiness self-check.
 *
 * Scope: a 3-question self-assessment (sleep, soreness, motivation) the user
 * can fill in from the dashboard or the post-session sheet. Produces a single
 * 0..100 readiness score that supplements the engine-computed recovery.
 *
 * Storage: per-profile via ProfileSettings (K_LAST_FEEL is reused; this adds
 * K_LAST_RPE + 3 daily-assessment keys under the same namespace).
 */
object SelfAssessment {

    data class Assessment(
        val sleep: Int,        // 0..10 (10 = best)
        val soreness: Int,     // 0..10 (10 = no soreness)
        val motivation: Int,    // 0..10 (10 = pumped)
        val takenAt: String,
    )

    fun read(ctx: Context): Assessment? {
        val sleep = ProfileSettings.get(ctx, "p4_sa_sleep")?.toIntOrNull() ?: return null
        val soreness = ProfileSettings.get(ctx, "p4_sa_soreness")?.toIntOrNull() ?: return null
        val motivation = ProfileSettings.get(ctx, "p4_sa_motivation")?.toIntOrNull() ?: return null
        val takenAt = ProfileSettings.get(ctx, "p4_sa_taken_at") ?: ProState.nowIso()
        return Assessment(sleep, soreness, motivation, takenAt)
    }

    fun save(ctx: Context, a: Assessment) {
        ProfileSettings.put(ctx, "p4_sa_sleep", a.sleep.coerceIn(0, 10).toString())
        ProfileSettings.put(ctx, "p4_sa_soreness", a.soreness.coerceIn(0, 10).toString())
        ProfileSettings.put(ctx, "p4_sa_motivation", a.motivation.coerceIn(0, 10).toString())
        ProfileSettings.put(ctx, "p4_sa_taken_at", ProState.nowIso())
    }

    /** Composite 0..100 readiness score from the last assessment. */
    fun readiness(ctx: Context): Int {
        val a = read(ctx) ?: return 50
        val raw = (a.sleep + a.soreness + a.motivation) / 3.0
        return (raw * 10).toInt().coerceIn(0, 100)
    }
}
