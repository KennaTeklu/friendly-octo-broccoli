package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable

/**
 * SelfAssessment — the post-session RPE / feel / readiness survey.
 *
 * BATCH-14: the Workout screen surfaces a compact self-assessment after each
 * session so the coach engine can adjust the next prescription. Responses are
 * per-profile (ProfileSettings) so the engine sees only the active profile's
 * recent answers.
 *
 * Fields:
 *  - rpe (1–10) — Borg CR-10 perceived exertion
 *  - readiness (1–5) — how recovered the user feels before the next session
 *  - soreness (0–3) — localized muscle soreness scale
 *  - sleepHours (0–12) — last night's sleep
 *  - mood (1–5) — subjective mood entering the session
 *  - notes — free text
 */
@Serializable
data class SelfAssessment(
    val rpe: Int = 0,
    val readiness: Int = 0,
    val soreness: Int = 0,
    val sleepHours: Double = 0.0,
    val mood: Int = 0,
    val notes: String = "",
    val timestamp: String = "",
) {
    companion object {
        const val STORAGE_KEY = "p4_self_assessment_last"

        fun load(ctx: Context): SelfAssessment? = try {
            ProfileSettings.get(ctx, STORAGE_KEY)?.let {
                ProJson.json.decodeFromString(serializer(), it)
            }
        } catch (_: Exception) { null }

        fun save(ctx: Context, a: SelfAssessment) {
            ProfileSettings.put(ctx, STORAGE_KEY, ProJson.json.encodeToString(serializer(), a))
        }

        /** Heuristic: the next session's recommended load multiplier
         *  based on the last self-assessment. 1.0 = same load, 1.05 = +5%, 0.95 = -5%. */
        fun recommendedLoadMultiplier(a: SelfAssessment?): Double {
            if (a == null || a.rpe == 0) return 1.0
            // Average RPE >8 → back off 5%; <6 → push 5%; else hold.
            return when {
                a.rpe >= 9 -> 0.92
                a.rpe >= 8 -> 0.97
                a.rpe <= 5 -> 1.05
                a.rpe <= 6 -> 1.02
                else -> 1.0
            }
        }
    }
}
