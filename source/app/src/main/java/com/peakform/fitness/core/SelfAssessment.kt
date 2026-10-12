package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * BATCH-9 Item 2: Self-assessment web.
 *
 * Consultation summary:
 *  - Feedback loop expert: post-session feel should be one tap, 4 buttons.
 *    Feed the next session's RPE baseline.
 *  - Sports scientist: soreness at ~20h post-session is the strongest
 *    signal of recovery need. 5-point per-muscle slider.
 *  - Muscle physiologist: body-region self-rating (too small / just right /
 *    too big) is a powerful motivator but must be private — never exported.
 *  - Local-first data modeler: all self-assessment data is per-profile,
 *    stored in SharedPreferences. Never leaves the device.
 */
object SelfAssessment {

    @Serializable
    data class PostSessionFeel(
        val workoutId: String,
        val date: String,
        val rating: String,  // too_easy / just_right / hard / brutal
        val note: String = "",
    )

    @Serializable
    data class SorenessEntry(
        val date: String,
        val muscle: String,
        val level: Int,  // 0-5, 0 = no soreness
    )

    @Serializable
    data class WeeklyCheckIn(
        val weekStart: String,
        val sleep: Int,     // 1-5
        val stress: Int,    // 1-5
        val energy: Int,    // 1-5
        val motivation: Int,// 1-5
        val bodyFeel: Int,  // 1-5
    )

    @Serializable
    data class BodyRegionRating(
        val date: String,
        val region: String,  // chest / back / shoulders / arms / glutes / legs / core
        val size: String,    // too_small / just_right / too_big
        val strength: String,// too_weak / just_right / too_strong
    )

    @Serializable
    data class InjuryLog(
        val muscle: String,
        val startDate: String,
        val status: String, // active / recovering / cleared
        val note: String = "",
    )

    // ---- Post-session feel ----
    fun saveFeel(ctx: Context, feel: PostSessionFeel) {
        val json = ProJson.json.encodeToString(PostSessionFeel.serializer(), feel)
        ProfileSettings.put(ctx, "p9_feel_${feel.workoutId}", json)
    }

    fun getFeel(ctx: Context, workoutId: String): PostSessionFeel? {
        val raw = ProfileSettings.get(ctx, "p9_feel_$workoutId") ?: return null
        return try { ProJson.json.decodeFromString(PostSessionFeel.serializer(), raw) } catch (_: Exception) { null }
    }

    fun lastFeelRating(ctx: Context): String? {
        // Returns the most recent feel rating across all workouts
        val keys = ProfileState.prefs(ctx).all.keys.filter { it.startsWith("p9_feel_") }
        if (keys.isEmpty()) return null
        return keys.mapNotNull { k ->
            getFeel(ctx, k.removePrefix("p9_feel_"))
        }.maxByOrNull { it.date }?.rating
    }

    // ---- Soreness ----
    fun saveSoreness(ctx: Context, entries: List<SorenessEntry>) {
        val json = ProJson.json.encodeToString(kotlinx.serialization.builtins.ListSerializer(SorenessEntry.serializer()), entries)
        ProfileSettings.put(ctx, "p9_soreness_${entries.firstOrNull()?.date ?: ProState.todayLocal()}", json)
    }

    fun getSorenessForDate(ctx: Context, date: String): List<SorenessEntry> {
        val raw = ProfileSettings.get(ctx, "p9_soreness_$date") ?: return emptyList()
        return try { ProJson.json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(SorenessEntry.serializer()), raw) } catch (_: Exception) { emptyList() }
    }

    // ---- Weekly check-in ----
    fun saveWeeklyCheckIn(ctx: Context, checkIn: WeeklyCheckIn) {
        val json = ProJson.json.encodeToString(WeeklyCheckIn.serializer(), checkIn)
        ProfileSettings.put(ctx, "p9_weekly_${checkIn.weekStart}", json)
    }

    fun getWeeklyCheckIns(ctx: Context): List<WeeklyCheckIn> {
        val keys = ProfileState.prefs(ctx).all.keys.filter { it.startsWith("p9_weekly_") }
        return keys.mapNotNull { k ->
            val raw = ProfileState.prefs(ctx).getString(k, null) ?: return@mapNotNull null
            try { ProJson.json.decodeFromString(WeeklyCheckIn.serializer(), raw) } catch (_: Exception) { null }
        }.sortedBy { it.weekStart }
    }

    // ---- Body region rating (private) ----
    fun saveBodyRating(ctx: Context, rating: BodyRegionRating) {
        val json = ProJson.json.encodeToString(BodyRegionRating.serializer(), rating)
        ProfileSettings.put(ctx, "p9_body_${rating.date}_${rating.region}", json)
    }

    fun getBodyRatings(ctx: Context): List<BodyRegionRating> {
        val keys = ProfileState.prefs(ctx).all.keys.filter { it.startsWith("p9_body_") }
        return keys.mapNotNull { k ->
            val raw = ProfileState.prefs(ctx).getString(k, null) ?: return@mapNotNull null
            try { ProJson.json.decodeFromString(BodyRegionRating.serializer(), raw) } catch (_: Exception) { null }
        }.sortedByDescending { it.date }
    }

    // ---- Injury log ----
    fun saveInjury(ctx: Context, injury: InjuryLog) {
        val json = ProJson.json.encodeToString(InjuryLog.serializer(), injury)
        ProfileSettings.put(ctx, "p9_injury_${injury.muscle}", json)
    }

    fun getInjuries(ctx: Context): List<InjuryLog> {
        val keys = ProfileState.prefs(ctx).all.keys.filter { it.startsWith("p9_injury_") }
        return keys.mapNotNull { k ->
            val raw = ProfileState.prefs(ctx).getString(k, null) ?: return@mapNotNull null
            try { ProJson.json.decodeFromString(InjuryLog.serializer(), raw) } catch (_: Exception) { null }
        }
    }

    fun hasActiveInjury(ctx: Context, muscle: String): Boolean {
        val injuries = getInjuries(ctx)
        return injuries.any { it.muscle == muscle && it.status == "active" }
    }

    // ---- Free text monthly ----
    fun saveMonthlyText(ctx: Context, month: String, text: String) {
        ProfileSettings.put(ctx, "p9_monthly_$month", text)
    }

    fun getMonthlyText(ctx: Context, month: String): String? {
        return ProfileSettings.get(ctx, "p9_monthly_$month")
    }
}
