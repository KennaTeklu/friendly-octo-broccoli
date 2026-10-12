package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * CoachingEngine — adaptive per-exercise coaching layer (BATCH-8).
 *
 * Reads the user's tuning answers (RPE at target weight, recovery readiness)
 * and computes a per-exercise prescription adjustment (sets/reps/weight delta)
 * plus a one-sentence coach explanation that surfaces in the workout card.
 *
 * Storage: per-profile, via ProfileSettings (SharedPreferences `p4_ls_<id>`).
 */
object CoachingEngine {
    private const val K_TUNING = "p4_tuning"

    /** Resistance-band color → estimated resistance in lbs at full stretch.
     *  Industry-standard values, applicable for band-resisted accessory work. */
    val BAND_COLORS: List<String> = listOf("yellow", "red", "green", "blue", "black")
    val BAND_RESISTANCE_LB: Map<String, Int> = mapOf(
        "yellow" to 5, "red" to 10, "green" to 15, "blue" to 25, "black" to 35,
    )

    @Serializable
    data class TuningAnswers(
        val rpeAtTarget: Int = 6,         // 1–10 perceived effort at prescribed weight
        val recoveryReady: Boolean = true, // false → "tired today"
        val lastSessionSoreness: Int = 2,  // 0–5 soreness from previous session
        val targetVolume: String = "maintain", // "cut" | "maintain" | "push"
    )

    @Serializable
    data class TuningAdjustment(
        val setsDelta: Int,           // -2..+1
        val repsDelta: Int,           // -2..+2
        val weightPct: Double,        // multiplier, e.g. 0.95 = -5%
        val sentence: String,         // coach sentence for the workout card
        val rpeTarget: Int,           // adjusted RPE target for this session
    )

    fun getTuning(ctx: Context): TuningAnswers = try {
        val raw = ProfileSettings.get(ctx, K_TUNING) ?: return TuningAnswers()
        Json.decodeFromString(TuningAnswers.serializer(), raw)
    } catch (_: Exception) { TuningAnswers() }

    fun saveTuning(ctx: Context, answers: TuningAnswers) {
        ProfileSettings.put(ctx, K_TUNING, Json.encodeToString(TuningAnswers.serializer(), answers))
    }

    /**
     * Compute a per-exercise adjustment from the user's current tuning answers.
     *
     * Rules:
     *   - If recoveryReady=false OR soreness ≥ 4: cut volume (-1 set, -5% weight)
     *   - If rpeAtTarget ≥ 9 (grinder): drop weight 5% to restore bar speed
     *   - If rpeAtTarget ≤ 5 (way too easy) AND targetVolume="push": +1 set
     *   - targetVolume="cut": -1 set, -2 reps
     *   - targetVolume="push": +1 set, +1 rep (capped)
     */
    fun computeAdjustment(answers: TuningAnswers): TuningAdjustment {
        var setsDelta = 0
        var repsDelta = 0
        var weightPct = 1.0
        var rpeTarget = 7

        if (!answers.recoveryReady || answers.lastSessionSoreness >= 4) {
            setsDelta -= 1
            weightPct -= 0.05
            rpeTarget = 6
        }
        when {
            answers.rpeAtTarget >= 9 -> { weightPct -= 0.05; rpeTarget = 7 }
            answers.rpeAtTarget <= 5 && answers.targetVolume == "push" -> { setsDelta += 1; rpeTarget = 8 }
        }
        when (answers.targetVolume) {
            "cut" -> { setsDelta -= 1; repsDelta -= 2; rpeTarget = 6 }
            "push" -> { setsDelta = setsDelta.coerceAtLeast(1); repsDelta = (repsDelta + 1).coerceAtMost(2); rpeTarget = 8 }
        }
        // Sanity clamp.
        setsDelta = setsDelta.coerceIn(-2, 1)
        repsDelta = repsDelta.coerceIn(-2, 2)
        weightPct = weightPct.coerceIn(0.85, 1.05)

        return TuningAdjustment(
            setsDelta = setsDelta,
            repsDelta = repsDelta,
            weightPct = weightPct,
            sentence = coachSentence(answers, setsDelta, repsDelta, weightPct),
            rpeTarget = rpeTarget,
        )
    }

    /** Generate a one-sentence coach explanation for the workout card. */
    fun coachSentence(answers: TuningAnswers, setsDelta: Int, repsDelta: Int, weightPct: Double): String {
        val parts = mutableListOf<String>()
        if (!answers.recoveryReady) parts += "Pulling back today since you flagged recovery as low"
        if (answers.lastSessionSoreness >= 4) parts += "dropping volume to respect soreness"
        if (answers.rpeAtTarget >= 9) parts += "easing the load 5% to restore bar speed"
        if (answers.rpeAtTarget <= 5 && answers.targetVolume == "push") parts += "adding a set — you had more in the tank"
        when (answers.targetVolume) {
            "cut" -> parts += "deload-style reduction this week"
            "push" -> parts += "push volume today — make it count"
            else -> {}
        }
        if (parts.isEmpty()) return "Holding prescription — you're dialed in."
        val first = parts.removeAt(0).replaceFirstChar { it.uppercase() }
        val tail = if (parts.isEmpty()) "" else parts.joinToString(prefix = "; ", postfix = ".") { it }
        return "$first$tail"
    }

    /** Per-exercise storage key (so multiple exercises can carry their own tuning
     *  on top of the global answers). */
    private fun tuningKeyFor(exerciseId: String) = "${K_TUNING}_$exerciseId"

    fun getExerciseTuning(ctx: Context, exerciseId: String): TuningAnswers? = try {
        val raw = ProfileSettings.get(ctx, tuningKeyFor(exerciseId)) ?: return null
        Json.decodeFromString(TuningAnswers.serializer(), raw)
    } catch (_: Exception) { null }

    fun saveExerciseTuning(ctx: Context, exerciseId: String, answers: TuningAnswers) {
        ProfileSettings.put(ctx, tuningKeyFor(exerciseId), Json.encodeToString(TuningAnswers.serializer(), answers))
    }
}
