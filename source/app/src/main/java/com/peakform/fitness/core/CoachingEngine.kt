package com.peakform.fitness.core

import android.content.Context

/**
 * BATCH-8 Item 1: Coaching engine.
 *
 * Consultation summary (CHANGES.md):
 *  - Strength coach: prescription should show 1RM context FIRST, then task.
 *    Band colour ladder: yellow < red < green < blue < black (ascending resistance).
 *  - Sports scientist: tuning answers should translate to intensity multipliers:
 *    "bigger" → hypertrophy range (8-12), "stronger" → strength range (3-6),
 *    "enough" → reduce volume 30%, "restriction" → reduce load 20% + flag.
 *  - Compose designer: tuning gear is a small gear icon; sheet opens with
 *    multiple-choice questions; predictions shown as suggestions.
 *
 * The tuning answers feed the prescription formula via [TuningAdjustment].
 * Generator.generateExercisePrescription reads this and applies the multipliers.
 */
object CoachingEngine {

    data class TuningAnswers(
        val goal: String = "",        // bigger / stronger / both / neither
        val priority: String = "",    // high / medium / low
        val restriction: String = "",  // tightness / pain / injury / none
        val enough: String = "",       // yes / no
        val mindMuscle: Int = 0,       // 1-5, 0 = no answer
    )

    data class TuningAdjustment(
        val intensityMultiplier: Double = 1.0,
        val repRange: String = "",     // "8-12" / "3-6" / "15-20" / "" = default
        val volumeMultiplier: Double = 1.0,
        val restrictLoad: Boolean = false,
        val restrictNote: String = "",
    )

    /** Standard band colour ladder (ascending resistance). */
    val BAND_COLORS = listOf("yellow", "red", "green", "blue", "black")
    val BAND_RESISTANCE_LB = mapOf(
        "yellow" to 5.0, "red" to 10.0, "green" to 15.0, "blue" to 20.0, "black" to 25.0,
    )

    fun tuningKey(exerciseId: String): String = "p8_tuning_$exerciseId"

    fun getTuning(ctx: Context, exerciseId: String): TuningAnswers {
        val raw = ProfileSettings.get(ctx, tuningKey(exerciseId)) ?: return TuningAnswers()
        return try {
            val o = ProJson.json.parseToJsonElement(raw) as kotlinx.serialization.json.JsonObject
            TuningAnswers(
                goal = (o["goal"] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: "",
                priority = (o["priority"] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: "",
                restriction = (o["restriction"] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: "",
                enough = (o["enough"] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: "",
                mindMuscle = (o["mindMuscle"] as? kotlinx.serialization.json.JsonPrimitive)?.content?.toIntOrNull() ?: 0,
            )
        } catch (_: Exception) { TuningAnswers() }
    }

    fun saveTuning(ctx: Context, exerciseId: String, answers: TuningAnswers) {
        val json = """{"goal":"${answers.goal}","priority":"${answers.priority}","restriction":"${answers.restriction}","enough":"${answers.enough}","mindMuscle":${answers.mindMuscle}}"""
        ProfileSettings.put(ctx, tuningKey(exerciseId), json)
    }

    /** Translate tuning answers into prescription adjustments. */
    fun computeAdjustment(answers: TuningAnswers): TuningAdjustment {
        var intensity = 1.0
        var volume = 1.0
        var repRange = ""
        var restrictLoad = false
        var restrictNote = ""

        // Goal
        when (answers.goal) {
            "bigger" -> { repRange = "8-12"; intensity *= 0.70 }
            "stronger" -> { repRange = "3-6"; intensity *= 0.85 }
            "both" -> { repRange = "6-10"; intensity *= 0.75 }
            "neither" -> { repRange = "15-20"; intensity *= 0.55; volume *= 0.6 }
        }

        // Priority
        when (answers.priority) {
            "high" -> volume *= 1.2
            "medium" -> {}  // default
            "low" -> volume *= 0.7
        }

        // Restriction
        when (answers.restriction) {
            "tightness" -> { restrictLoad = true; intensity *= 0.80; restrictNote = "Tightness flagged — load reduced 20%." }
            "pain" -> { restrictLoad = true; intensity *= 0.60; restrictNote = "Pain flagged — load reduced 40%. Consider skipping." }
            "injury" -> { restrictLoad = true; intensity *= 0.50; volume *= 0.5; restrictNote = "Injury flagged — load and volume halved." }
        }

        // Enough
        if (answers.enough == "yes") {
            volume *= 0.7
            repRange = if (repRange.isBlank()) "12-15" else repRange
        }

        // Mind-muscle connection
        if (answers.mindMuscle > 0) {
            // Higher MMC → can handle slightly more volume at the same load
            volume *= 1.0 + (answers.mindMuscle - 3) * 0.05
        }

        return TuningAdjustment(
            intensityMultiplier = intensity.coerceIn(0.3, 1.2),
            repRange = repRange,
            volumeMultiplier = volume.coerceIn(0.4, 1.5),
            restrictLoad = restrictLoad,
            restrictNote = restrictNote,
        )
    }

    /** Coach sentence — derived from goal, phase, and history. */
    fun coachSentence(exerciseName: String, components: List<String>, muscles: List<String>, answers: TuningAnswers): String {
        val comp = components.firstOrNull() ?: "strength"
        return when {
            answers.restriction == "injury" -> "Injury noted — this is deload territory. Form over load."
            answers.restriction == "pain" -> "Pain flagged — keep it light, stop if it hurts."
            answers.goal == "bigger" -> "Hypertrophy focus: slow eccentrics, feel the stretch, chase the pump."
            answers.goal == "stronger" -> "Strength focus: heavy sets, long rest, full-body tension."
            answers.goal == "neither" -> "Maintenance: keep it moving, don't chase numbers."
            comp == "endurance" -> "Endurance focus: this will not make the muscle bigger, it will make it work longer."
            comp == "power" -> "Power focus: explosive concentric, controlled eccentric."
            comp == "mobility" -> "Mobility focus: control the range, breathe into the stretch."
            else -> "Quality over quantity — crisp reps, steady breathing."
        }
    }
}
