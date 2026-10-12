package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/**
 * SelfAssessment — Batch 9 self-assessment web.
 *
 * Lightweight per-profile records the user self-reports about how they
 * actually feel — separate from the engine-derived 1RM / volume / fatigue
 * aggregates. Used to:
 *   - tune the next session (PostSessionFeel → CoachingEngine)
 *   - track soreness / recovery between sessions
 *   - capture weekly check-ins (wellness, sleep, stress)
 *   - track body-region ratings (per-region strength / mobility)
 *   - log injuries (active injuries restrict load via CoachingEngine)
 *   - free-text monthly summaries (one paragraph per month)
 *
 * All values stored per-profile via ProfileSettings → ProfileState →
 * per-profile SharedPreferences (`p4_ls_<id>`).
 */
object SelfAssessment {
    private const val K_FEEL = "p4_sa_feel"               // list<PostSessionFeel>
    private const val K_SORENESS = "p4_sa_soreness"       // list<SorenessEntry>
    private const val K_WEEKLY = "p4_sa_weekly"           // list<WeeklyCheckIn>
    private const val K_BODY_RATING = "p4_sa_body_rating" // list<BodyRegionRating>
    private const val K_INJURIES = "p4_sa_injuries"       // list<InjuryLog>
    private const val K_MONTHLY_TEXT = "p4_sa_monthly"   // map<yyyy-MM, text>

    @Serializable
    data class PostSessionFeel(
        val workoutId: String,
        val date: String,
        /** too_easy | just_right | hard | brutal */
        val feel: String,
        val note: String = "",
    )

    @Serializable
    data class SorenessEntry(
        val date: String,
        val region: String,
        /** 0 = none, 1 = mild, 2 = moderate, 3 = severe */
        val level: Int,
    )

    @Serializable
    data class WeeklyCheckIn(
        val weekStart: String,
        val wellness: Int,        // 0..100
        val sleep: Int,           // 0..100
        val stress: Int,          // 0..100
        val note: String = "",
    )

    @Serializable
    data class BodyRegionRating(
        val date: String,
        val region: String,
        /** strength rating 0..10 */
        val strength: Int,
        /** mobility rating 0..10 */
        val mobility: Int,
    )

    @Serializable
    data class InjuryLog(
        val startDate: String,
        val region: String,
        val description: String,
        val resolvedAt: String? = null,
    )

    // ---- PostSessionFeel ----
    private val feelListSerializer = ListSerializer(PostSessionFeel.serializer())

    fun saveFeel(ctx: Context, feel: PostSessionFeel) {
        val list = getFeel(ctx).toMutableList()
        // Replace any prior feel for the same workoutId, else append.
        list.removeAll { it.workoutId == feel.workoutId }
        list.add(feel)
        ProfileSettings.put(ctx, K_FEEL, ProJson.json.encodeToString(feelListSerializer, list.takeLast(200)))
    }

    fun getFeel(ctx: Context): List<PostSessionFeel> = try {
        val raw = ProfileSettings.get(ctx, K_FEEL) ?: return emptyList()
        ProJson.json.decodeFromString(feelListSerializer, raw)
    } catch (_: Exception) { emptyList() }

    fun feelForWorkout(ctx: Context, workoutId: String): PostSessionFeel? =
        getFeel(ctx).firstOrNull { it.workoutId == workoutId }

    // ---- Soreness ----
    private val sorenessListSerializer = ListSerializer(SorenessEntry.serializer())

    fun saveSoreness(ctx: Context, entry: SorenessEntry) {
        val list = getSorenessForDate(ctx, entry.date).toMutableList()
        list.removeAll { it.date == entry.date && it.region == entry.region }
        list.add(entry)
        // Merge with all existing entries
        val all = getAllSoreness(ctx).toMutableList()
        all.removeAll { it.date == entry.date && it.region == entry.region }
        all.add(entry)
        ProfileSettings.put(ctx, K_SORENESS, ProJson.json.encodeToString(sorenessListSerializer, all.takeLast(400)))
    }

    fun getSorenessForDate(ctx: Context, date: String): List<SorenessEntry> =
        getAllSoreness(ctx).filter { it.date == date }

    fun getAllSoreness(ctx: Context): List<SorenessEntry> = try {
        val raw = ProfileSettings.get(ctx, K_SORENESS) ?: return emptyList()
        ProJson.json.decodeFromString(sorenessListSerializer, raw)
    } catch (_: Exception) { emptyList() }

    // ---- Weekly check-ins ----
    private val weeklyListSerializer = ListSerializer(WeeklyCheckIn.serializer())

    fun saveWeeklyCheckIn(ctx: Context, checkIn: WeeklyCheckIn) {
        val list = getWeeklyCheckIns(ctx).toMutableList()
        list.removeAll { it.weekStart == checkIn.weekStart }
        list.add(checkIn)
        ProfileSettings.put(ctx, K_WEEKLY, ProJson.json.encodeToString(weeklyListSerializer, list.takeLast(200)))
    }

    fun getWeeklyCheckIns(ctx: Context): List<WeeklyCheckIn> = try {
        val raw = ProfileSettings.get(ctx, K_WEEKLY) ?: return emptyList()
        ProJson.json.decodeFromString(weeklyListSerializer, raw)
    } catch (_: Exception) { emptyList() }

    // ---- Body region ratings ----
    private val bodyRatingListSerializer = ListSerializer(BodyRegionRating.serializer())

    fun saveBodyRating(ctx: Context, rating: BodyRegionRating) {
        val list = getBodyRatings(ctx).toMutableList()
        list.removeAll { it.date == rating.date && it.region == rating.region }
        list.add(rating)
        ProfileSettings.put(ctx, K_BODY_RATING, ProJson.json.encodeToString(bodyRatingListSerializer, list.takeLast(400)))
    }

    fun getBodyRatings(ctx: Context): List<BodyRegionRating> = try {
        val raw = ProfileSettings.get(ctx, K_BODY_RATING) ?: return emptyList()
        ProJson.json.decodeFromString(bodyRatingListSerializer, raw)
    } catch (_: Exception) { emptyList() }

    // ---- Injuries ----
    private val injuryListSerializer = ListSerializer(InjuryLog.serializer())

    fun saveInjury(ctx: Context, injury: InjuryLog) {
        val list = getInjuries(ctx).toMutableList()
        // Replace any active injury for the same region, else append.
        list.removeAll { it.region == injury.region && it.resolvedAt == null }
        list.add(injury)
        ProfileSettings.put(ctx, K_INJURIES, ProJson.json.encodeToString(injuryListSerializer, list.takeLast(100)))
    }

    fun getInjuries(ctx: Context): List<InjuryLog> = try {
        val raw = ProfileSettings.get(ctx, K_INJURIES) ?: return emptyList()
        ProJson.json.decodeFromString(injuryListSerializer, raw)
    } catch (_: Exception) { emptyList() }

    fun hasActiveInjury(ctx: Context, region: String? = null): Boolean {
        val now = ProState.todayLocal()
        return getInjuries(ctx).any { injury ->
            injury.resolvedAt == null &&
                (region == null || injury.region.equals(region, ignoreCase = true)) &&
            // An injury is "active" for 90 days after its start date, unless explicitly resolved.
            ProState.utcDayMillis(injury.startDate).let { ms ->
                ms > 0 && (System.currentTimeMillis() - ms) <= 90L * 24 * 3600 * 1000
            }
        }
    }

    fun resolveInjury(ctx: Context, region: String) {
        val list = getInjuries(ctx).map {
            if (it.region.equals(region, ignoreCase = true) && it.resolvedAt == null)
                it.copy(resolvedAt = ProState.nowIso())
            else it
        }
        ProfileSettings.put(ctx, K_INJURIES, ProJson.json.encodeToString(injuryListSerializer, list))
    }

    // ---- Monthly free-text summaries ----
    private val strMap = MapSerializer(String.serializer(), String.serializer())

    fun saveMonthlyText(ctx: Context, monthKey: String, text: String) {
        val map = getMonthlyTexts(ctx).toMutableMap()
        map[monthKey] = text
        ProfileSettings.put(ctx, K_MONTHLY_TEXT, ProJson.json.encodeToString(strMap, map))
    }

    fun getMonthlyTexts(ctx: Context): Map<String, String> = try {
        val raw = ProfileSettings.get(ctx, K_MONTHLY_TEXT) ?: return emptyMap()
        ProJson.json.decodeFromString(strMap, raw)
    } catch (_: Exception) { emptyMap() }

    fun getMonthlyText(ctx: Context, monthKey: String): String? = getMonthlyTexts(ctx)[monthKey]
}
