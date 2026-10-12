package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * SelfAssessment — BATCH-9 post-session feel capture.
 *
 * After completing a workout, the user is asked to rate how the session went
 * (1–5 stars) and to tag how they feel across four dimensions (energy,
 * soreness, motivation, sleep). The capture is stored in the active profile's
 * SharedPreferences (ProfileState) under K_LAST_ASSESSMENT so a profile switch
 * brings its own assessment history.
 *
 * The captured object is JSON so it can be appended to a per-profile history
 * array (K_ASSESSMENT_HISTORY) and surfaced in the Progress / ReferenceGaps
 * panels.
 */
object SelfAssessment {
    const val K_LAST_ASSESSMENT = "p4_self_assessment_last"
    const val K_ASSESSMENT_HISTORY = "p4_self_assessment_history"

    @kotlinx.serialization.Serializable
    data class Assessment(
        val at: String,             // ISO timestamp
        val workoutId: String?,     // null when capture was free-form
        val rating: Int,            // 1..5 stars
        val energy: Int,            // 1..5
        val soreness: Int,           // 1..5 (5 = very sore)
        val motivation: Int,        // 1..5
        val sleep: Int,              // 1..5
        val note: String = "",
    )

    /** Persist the latest assessment + append to history. */
    fun record(ctx: Context, a: Assessment) {
        try {
            val json = ProJson.json.encodeToString(Assessment.serializer(), a)
            ProfileState.put(ctx, K_LAST_ASSESSMENT, json)
            // append to history (cap at 100 entries)
            val history = mutableListOf<Assessment>()
            history += history(ctx)
            history += a
            if (history.size > 100) history.subList(0, history.size - 100).clear()
            val histJson = ProJson.json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(Assessment.serializer()),
                history,
            )
            ProfileState.put(ctx, K_ASSESSMENT_HISTORY, histJson)
            ProLog.i("SELF", "assessment recorded: rating=${a.rating} energy=${a.energy}")
        } catch (e: Exception) {
            ProLog.w("SELF", "record failed: ${e.message}")
        }
    }

    fun last(ctx: Context): Assessment? = try {
        ProfileState.get(ctx, K_LAST_ASSESSMENT)?.let {
            ProJson.json.decodeFromString(Assessment.serializer(), it)
        }
    } catch (_: Exception) { null }

    fun history(ctx: Context): List<Assessment> = try {
        ProfileState.get(ctx, K_ASSESSMENT_HISTORY)?.let {
            ProJson.json.decodeFromString(
                kotlinx.serialization.builtins.ListSerializer(Assessment.serializer()),
                it,
            )
        } ?: emptyList()
    } catch (_: Exception) { emptyList() }

    /** Trailing average rating (last N assessments), or null when no history. */
    fun averageRating(ctx: Context, n: Int = 5): Double? {
        val h = history(ctx).takeLast(n)
        if (h.isEmpty()) return null
        return h.map { it.rating.toDouble() }.average()
    }
}
