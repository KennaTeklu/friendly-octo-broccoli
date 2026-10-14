package com.peakform.fitness.coach

import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.coach.exercises.AllCoaches
import com.peakform.fitness.coach.authored.BackSquatAuthoredCoach
import com.peakform.fitness.coach.authored.PullUpAuthoredCoach
import com.peakform.fitness.coach.authored.BandedLateralRaiseAuthoredCoach
import com.peakform.fitness.coach.authored.PlankAuthoredCoach
import com.peakform.fitness.coach.authored.RunningAuthoredCoach
import com.peakform.fitness.coach.authored.DeadliftAuthoredCoach
import com.peakform.fitness.coach.authored.BenchPressAuthoredCoach
import com.peakform.fitness.coach.authored.OverheadPressAuthoredCoach
import com.peakform.fitness.coach.authored.BarbellRowAuthoredCoach
import com.peakform.fitness.coach.authored.FrontSquatAuthoredCoach
import com.peakform.fitness.coach.authored.RomanianDeadliftAuthoredCoach
import com.peakform.fitness.coach.authored.ChinUpAuthoredCoach
import com.peakform.fitness.coach.authored.DipAuthoredCoach
import com.peakform.fitness.coach.authored.PushUpAuthoredCoach
import com.peakform.fitness.coach.authored.FacePullAuthoredCoach
import com.peakform.fitness.coach.authored.SumoDeadliftAuthoredCoach
import com.peakform.fitness.coach.authored.InclineBenchPressAuthoredCoach
import com.peakform.fitness.coach.authored.CloseGripBenchPressAuthoredCoach
import com.peakform.fitness.coach.authored.PendlayRowAuthoredCoach
import com.peakform.fitness.coach.authored.PushPressAuthoredCoach
import com.peakform.fitness.coach.authored.PowerCleanAuthoredCoach
import com.peakform.fitness.coach.authored.BandedFacePullAuthoredCoach
import com.peakform.fitness.coach.authored.BandedPullApartAuthoredCoach
import com.peakform.fitness.coach.authored.BandedSquatAuthoredCoach
import com.peakform.fitness.coach.authored.BandedRowAuthoredCoach
import com.peakform.fitness.coach.authored.BandedGluteBridgeAuthoredCoach
import com.peakform.fitness.coach.authored.BandedChestPressAuthoredCoach
import com.peakform.fitness.coach.authored.InvertedRowAuthoredCoach
import com.peakform.fitness.coach.authored.ArcherPullUpAuthoredCoach
import com.peakform.fitness.coach.authored.LSitPullUpAuthoredCoach
import com.peakform.fitness.coach.authored.SidePlankAuthoredCoach

/**
 * CoachRegistry — looks up the right ExerciseCoach for an exercise id.
 *
 * BATCH-18: a single map from exerciseId → ExerciseCoach. Every concrete
 * coach registers itself in AllCoaches. Lookup is O(1) and falls back to
 * a generic coach based on the exercise's muscle group when no specific
 * coach exists.
 *
 * BATCH-35c (B1): the 31 authored coaches (BackSquatAuthoredCoach,
 * DeadliftAuthoredCoach, … SidePlankAuthoredCoach) are now registered
 * alongside the AllCoaches entries. Authored coaches take PRECEDENCE —
 * when an exerciseId is claimed by both (e.g. "back_squat",
 * "romanian_deadlift", "chin_up"), the authored coach wins because it
 * overrides requirements(), envelope, provenance, reconcile() and
 * explain() per spec. Without this routing, Romanian Deadlift and
 * Chin-Up cards rendered "No prescription available — no coach
 * requirements declared" even though authored rows exist for them.
 *
 * A keyword fallback (lookupByKeyword) catches library exercises whose
 * generated id does not exactly match any authored coach's exerciseIds
 * (e.g. "barbell_romanian_deadlift" → RomanianDeadliftAuthoredCoach via
 * the "romanian_deadlift" substring; "chin-up" → ChinUpAuthoredCoach
 * via the "chin" token). The fallback is conservative — it never
 * matches a 2-char id like "rdl" against a multi-word library name.
 */
object CoachRegistry {

    /** The 31 authored coaches — listed explicitly so the test can read them. */
    val authored: List<ExerciseCoach> = listOf(
        BackSquatAuthoredCoach,
        PullUpAuthoredCoach,
        BandedLateralRaiseAuthoredCoach,
        PlankAuthoredCoach,
        RunningAuthoredCoach,
        DeadliftAuthoredCoach,
        BenchPressAuthoredCoach,
        OverheadPressAuthoredCoach,
        BarbellRowAuthoredCoach,
        FrontSquatAuthoredCoach,
        RomanianDeadliftAuthoredCoach,
        ChinUpAuthoredCoach,
        DipAuthoredCoach,
        PushUpAuthoredCoach,
        FacePullAuthoredCoach,
        SumoDeadliftAuthoredCoach,
        InclineBenchPressAuthoredCoach,
        CloseGripBenchPressAuthoredCoach,
        PendlayRowAuthoredCoach,
        PushPressAuthoredCoach,
        PowerCleanAuthoredCoach,
        BandedFacePullAuthoredCoach,
        BandedPullApartAuthoredCoach,
        BandedSquatAuthoredCoach,
        BandedRowAuthoredCoach,
        BandedGluteBridgeAuthoredCoach,
        BandedChestPressAuthoredCoach,
        InvertedRowAuthoredCoach,
        ArcherPullUpAuthoredCoach,
        LSitPullUpAuthoredCoach,
        SidePlankAuthoredCoach,
    )

    private val all: List<ExerciseCoach> = AllCoaches.all()

    /** byId is built AllCoaches-first, authored-second so authored OVERWRITES
     *  AllCoaches entries when exercise IDs collide. */
    private val byId: Map<String, ExerciseCoach> = buildMap {
        all.forEach { coach -> coach.exerciseIds.forEach { id -> put(id, coach) } }
        authored.forEach { coach -> coach.exerciseIds.forEach { id -> put(id, coach) } }
    }

    /**
     * O(1) lookup by exact exercise id. Falls back to keyword matching
     * (substring + movement-name-token) when the id is not in [byId] —
     * covers library exercises whose id is derived from a long display
     * name (e.g. "barbell_romanian_deadlift" instead of "romanian_deadlift").
     */
    fun lookup(exerciseId: String): ExerciseCoach? {
        byId[exerciseId]?.let { return it }
        // Keyword fallback — only against authored coaches (generic coaches
        // have no authored rows, so a fuzzy match against them is useless).
        val id = exerciseId.lowercase()
        // 1. Substring match on authored exerciseIds — pick the coach whose
        //    MATCHING exerciseId is longest, NOT the coach with the longest
        //    exerciseId overall. This disambiguates "barbell_romanian_deadlift"
        //    between RomanianDeadliftAuthoredCoach (matches "romanian_deadlift",
        //    17 chars) and DeadliftAuthoredCoach (matches "deadlift", 8 chars)
        //    in favour of the more-specific Romanian match.
        val bestSubstring = authored
            .flatMap { coach -> coach.exerciseIds.filter { it.length >= 4 && (id.contains(it) || it.contains(id)) }
                .map { matchedId -> matchedId to coach } }
            .maxByOrNull { it.first.length }
        if (bestSubstring != null) return bestSubstring.second
        // 2. Movement-name token match — every non-trivial token (>=4 chars)
        //    of the coach's movementName must appear in the id. Conservative:
        //    prevents "Pull Up" matching "Chin-Up" (only "up" would match).
        authored.forEach { coach ->
            val tokens = coach.movementName.lowercase()
                .split(' ', '-', '_')
                .filter { it.length >= 4 }
            if (tokens.isNotEmpty() && tokens.all { token -> id.contains(token) }) return coach
        }
        return null
    }

    fun all(): List<ExerciseCoach> = all

    /** Total coaches registered (used by CoachCoverageTest). Counts the
     *  union of AllCoaches + authored coaches (authored coaches that share
     *  exercise ids with AllCoaches are deduplicated by reference). */
    val size: Int get() = (all + authored).distinct().size

    /** All descriptors — used by the test battery to verify coverage. */
    fun descriptors(): List<CoachDescriptor> = (all + authored).distinct().map {
        CoachDescriptor(
            id = it.movementName.lowercase().replace(' ', '_'),
            movementName = it.movementName,
            exerciseIds = it.exerciseIds,
            category = "general",
            primaryMuscle = "",
        )
    }
}

data class CoachDescriptor(
    val id: String,
    val movementName: String,
    val exerciseIds: Set<String>,
    val category: String,
    val primaryMuscle: String,
)
