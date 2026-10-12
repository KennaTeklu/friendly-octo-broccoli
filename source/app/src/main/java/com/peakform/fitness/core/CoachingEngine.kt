package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

/**
 * CoachingEngine — Batch 8 coaching layer.
 *
 * Tuning sheet answers map to a coaching adjustment (intensity multiplier,
 * rep range, volume multiplier, load restriction). All values are stored
 * per-profile via ProfileSettings so each profile keeps its own tuning.
 *
 * Storage:
 *   - TuningAnswers JSON blob under p4_coaching_tuning
 *   - TuningAdjustment is computed (not stored) — recomputed on every read
 *
 * Banded exercise resistance is also handled here (BandedExerciseInput
 * uses these constants): the band color → resistance in lbs mapping is the
 * legacy fitness convention (red = heaviest, yellow = lightest).
 */
object CoachingEngine {
    private const val K_TUNING = "p4_coaching_tuning"
    private const val K_TUNING_AT = "p4_coaching_tuning_at"

    /** Band color → resistance in lbs (legacy fitness convention). */
    val BAND_COLORS: List<String> = listOf("yellow", "green", "red", "black", "purple")

    val BAND_RESISTANCE_LB: Map<String, Double> = mapOf(
        "yellow" to 5.0,
        "green" to 15.0,
        "red" to 25.0,
        "black" to 35.0,
        "purple" to 50.0,
    )

    @Serializable
    data class TuningAnswers(
        /** goal: "bigger" (hypertrophy) | "stronger" (strength) | "leaner" (cut) | "fit" (general). */
        val goal: String = "fit",
        /** priority: "strength" | "muscle" | "endurance" | "recovery" | "mobility". */
        val priority: String = "strength",
        /** restriction: "none" | "joint" | "lower_back" | "shoulder" | "knee". */
        val restriction: String = "none",
        /** enough: was the last session enough? "too_easy" | "just_right" | "hard" | "brutal". */
        val enough: String = "just_right",
        /** mindMuscle: 0..1 — focus on mind-muscle connection (1) vs raw load (0). */
        val mindMuscle: Double = 0.5,
    )

    @Serializable
    data class TuningAdjustment(
        val intensityMultiplier: Double,
        val repRange: Pair<Int, Int>,
        val volumeMultiplier: Double,
        val restrictLoad: Boolean,
        val sentence: String,
    )

    fun getTuning(ctx: Context): TuningAnswers {
        val raw = ProfileSettings.get(ctx, K_TUNING) ?: return TuningAnswers()
        return try {
            ProJson.json.decodeFromString(TuningAnswers.serializer(), raw)
        } catch (_: Exception) { TuningAnswers() }
    }

    fun saveTuning(ctx: Context, answers: TuningAnswers) {
        ProfileSettings.put(ctx, K_TUNING, ProJson.json.encodeToString(TuningAnswers.serializer(), answers))
        ProfileSettings.put(ctx, K_TUNING_AT, ProState.nowIso())
    }

    fun tuningAt(ctx: Context): String? = ProfileSettings.get(ctx, K_TUNING_AT)

    /**
     * Translate TuningAnswers into a TuningAdjustment the workout generator
     * can consume. The mapping is intentionally simple — these are nudges,
     * not a substitute for the underlying 1RM model.
     */
    fun computeAdjustment(answers: TuningAnswers): TuningAdjustment {
        // intensity multiplier
        var intensity = 1.0
        var repMin = 5
        var repMax = 8
        var volume = 1.0
        var restrictLoad = false

        when (answers.goal) {
            "bigger" -> { repMin = 8; repMax = 12; volume = 1.10 }
            "stronger" -> { repMin = 3; repMax = 6; volume = 0.90; intensity = 1.05 }
            "leaner" -> { repMin = 12; repMax = 15; volume = 1.05; intensity = 0.90 }
            "fit" -> { repMin = 8; repMax = 12 }
        }

        when (answers.priority) {
            "strength" -> { repMin = minOf(repMin, 5); repMax = minOf(repMax, 6) }
            "muscle" -> { repMin = maxOf(repMin, 8); repMax = maxOf(repMax, 10) }
            "endurance" -> { repMin = 12; repMax = 20; intensity *= 0.85 }
            "recovery" -> { intensity *= 0.75; volume *= 0.80 }
            "mobility" -> { intensity *= 0.65; repMin = 10; repMax = 15 }
        }

        when (answers.restriction) {
            "joint" -> { restrictLoad = true; intensity *= 0.85 }
            "lower_back" -> { restrictLoad = true; intensity *= 0.80 }
            "shoulder" -> { restrictLoad = true; intensity *= 0.85 }
            "knee" -> { restrictLoad = true; intensity *= 0.80 }
        }

        when (answers.enough) {
            "too_easy" -> { intensity *= 1.05; volume *= 1.05 }
            "hard" -> { intensity *= 0.95 }
            "brutal" -> { intensity *= 0.85; volume *= 0.85 }
            "just_right" -> { /* no change */ }
        }

        // mind-muscle slider: 0 = pure load (lower reps, higher intensity),
        // 1 = mind-muscle (higher reps, lower intensity).
        val mm = answers.mindMuscle.coerceIn(0.0, 1.0)
        intensity *= (1.0 - 0.15 * mm)
        repMin += (mm * 2).toInt()
        repMax += (mm * 4).toInt()

        // clamp
        intensity = intensity.coerceIn(0.5, 1.20)
        volume = volume.coerceIn(0.6, 1.30)
        repMin = repMin.coerceIn(1, 25)
        repMax = repMax.coerceIn(repMin, 30)

        return TuningAdjustment(
            intensityMultiplier = intensity,
            repRange = repMin to repMax,
            volumeMultiplier = volume,
            restrictLoad = restrictLoad,
            sentence = coachSentence(answers, intensity, repMin to repMax, volume, restrictLoad),
        )
    }

    /** One-sentence coach summary shown under the target chips on the Workout screen. */
    private fun coachSentence(
        answers: TuningAnswers,
        intensity: Double,
        repRange: Pair<Int, Int>,
        volume: Double,
        restrictLoad: Boolean,
    ): String {
        val goal = when (answers.goal) {
            "bigger" -> "build muscle"
            "stronger" -> "build strength"
            "leaner" -> "lean out"
            "fit" -> "stay fit"
            else -> "train"
        }
        val restrictClause = if (restrictLoad) ", respecting your ${answers.restriction.replace('_', ' ')} restriction" else ""
        val intensityPct = (intensity * 100).toInt()
        val volPct = (volume * 100).toInt()
        return "Today's plan: $goal at ${intensityPct}% intensity, ${repRange.first}-${repRange.second} reps, ${volPct}% volume$restrictClause."
    }

    /** Convenience: read + compute in one call. */
    fun currentAdjustment(ctx: Context): TuningAdjustment = computeAdjustment(getTuning(ctx))

    /** Reset tuning to defaults (used by "Reset to defaults" buttons). */
    fun resetTuning(ctx: Context) {
        ProfileSettings.remove(ctx, K_TUNING)
        ProfileSettings.remove(ctx, K_TUNING_AT)
    }
}
