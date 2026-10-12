package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * SelfAssessment — per-session subjective feedback (BATCH-8).
 *
 * Captures the user's perceived effort, soreness by body region, and weekly
 * check-in answers. Used by CoachingEngine.computeAdjustment to nudge the
 * next session's volume/intensity based on how the user is recovering.
 *
 * Storage: per-profile, via ProfileSettings (SharedPreferences `p4_ls_<id>`).
 */
object SelfAssessment {
    private const val K_POST_SESSION = "p4_post_session"
    private const val K_SORENESS = "p4_soreness"
    private const val K_WEEKLY = "p4_weekly_checkin"
    private const val K_INJURIES = "p4_injury_log"

    @Serializable
    data class PostSessionFeel(
        val workoutId: String,
        val at: String,
        val overallRpe: Int = 6,           // 1–10
        val feltStrong: Boolean = false,
        val notes: String = "",
        val minutesTrained: Int = 0,
    )

    @Serializable
    data class SorenessEntry(
        val at: String,
        val region: String,               // e.g. "chest", "legs", "back"
        val severity: Int,                // 0–5
        val durationHours: Int = 24,
    )

    @Serializable
    data class WeeklyCheckIn(
        val at: String,
        val energyLevel: Int = 3,          // 1–5
        val motivationLevel: Int = 3,      // 1–5
        val sleepHours: Int = 7,
        val stressLevel: Int = 3,          // 1–5
        val weightLbs: Double? = null,
        val notes: String = "",
    )

    @Serializable
    data class BodyRegionRating(
        val region: String,
        val rating: Int,                  // 1–10 how the region feels
        val notes: String = "",
    )

    @Serializable
    data class InjuryLog(
        val at: String,
        val region: String,
        val description: String,
        val resolved: Boolean = false,
        val resolvedAt: String? = null,
    )

    fun savePostSession(ctx: Context, feel: PostSessionFeel) {
        val list = getPostSessions(ctx).toMutableList()
        list.add(feel)
        // Keep the last 100 entries to avoid unbounded growth.
        val trimmed = list.takeLast(100)
        ProfileSettings.put(ctx, K_POST_SESSION,
            Json.encodeToString(ListSerializer(PostSessionFeel.serializer()), trimmed))
    }

    fun getPostSessions(ctx: Context): List<PostSessionFeel> = try {
        val raw = ProfileSettings.get(ctx, K_POST_SESSION) ?: return emptyList()
        Json.decodeFromString(ListSerializer(PostSessionFeel.serializer()), raw)
    } catch (_: Exception) { emptyList() }

    fun saveSoreness(ctx: Context, entry: SorenessEntry) {
        val list = getSorenessLog(ctx).toMutableList()
        list.add(entry)
        val trimmed = list.takeLast(200)
        ProfileSettings.put(ctx, K_SORENESS,
            Json.encodeToString(ListSerializer(SorenessEntry.serializer()), trimmed))
    }

    fun getSorenessLog(ctx: Context): List<SorenessEntry> = try {
        val raw = ProfileSettings.get(ctx, K_SORENESS) ?: return emptyList()
        Json.decodeFromString(ListSerializer(SorenessEntry.serializer()), raw)
    } catch (_: Exception) { emptyList() }

    fun saveWeekly(ctx: Context, checkIn: WeeklyCheckIn) {
        val list = getWeeklyCheckIns(ctx).toMutableList()
        list.add(checkIn)
        val trimmed = list.takeLast(60)
        ProfileSettings.put(ctx, K_WEEKLY,
            Json.encodeToString(ListSerializer(WeeklyCheckIn.serializer()), trimmed))
    }

    fun getWeeklyCheckIns(ctx: Context): List<WeeklyCheckIn> = try {
        val raw = ProfileSettings.get(ctx, K_WEEKLY) ?: return emptyList()
        Json.decodeFromString(ListSerializer(WeeklyCheckIn.serializer()), raw)
    } catch (_: Exception) { emptyList() }

    fun saveInjury(ctx: Context, injury: InjuryLog) {
        val list = getInjuries(ctx).toMutableList()
        list.add(injury)
        ProfileSettings.put(ctx, K_INJURIES,
            Json.encodeToString(ListSerializer(InjuryLog.serializer()), list))
    }

    fun getInjuries(ctx: Context): List<InjuryLog> = try {
        val raw = ProfileSettings.get(ctx, K_INJURIES) ?: return emptyList()
        Json.decodeFromString(ListSerializer(InjuryLog.serializer()), raw)
    } catch (_: Exception) { emptyList() }

    /** Most-recent weekly check-in, or null if never. */
    fun latestWeekly(ctx: Context): WeeklyCheckIn? = getWeeklyCheckIns(ctx).lastOrNull()

    /** Most-recent soreness for a region, or null. */
    fun latestSorenessFor(ctx: Context, region: String): SorenessEntry? =
        getSorenessLog(ctx).lastOrNull { it.region == region }
}
