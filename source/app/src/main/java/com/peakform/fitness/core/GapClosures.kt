package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * GapClosures — BATCH-21.
 *
 * A grab-bag of small "gap-closure" helpers identified in the reference-app
 * audit (BATCH-19). Each is independent and self-contained; they live together
 * so the audit can find them under one file.
 *
 *   - GenderFilter: filter the exercise library by gender suitability.
 *   - FuzzySearch: lightweight Levenshtein-distance search for the library.
 *   - RpeColor: tint for an RPE value (1..10).
 *   - CompetitionDate: countdown + phase label for a competition date.
 *   - ExpandedMessageBank: more variety in coach messages (per mood).
 *   - ExerciseAudit: per-exercise gap analysis for the audit screen.
 *   - EwmaPerExercise: exponentially-weighted moving average of volume per
 *     exercise, for trend detection.
 *   - CouplingMatrixWriteback: persist a user-edited coupling matrix override.
 *   - TestResultSync: mirror a 1RM test result into the exercise record.
 *   - VersionCheck: compare two versionName strings (1.5.1 < 1.9.1 etc.).
 *
 * Storage: read/write through ProfileSettings where per-profile state is
 * needed (CouplingMatrixWriteback). Everything else is stateless.
 */
object GapClosures {

    // ---- GenderFilter ----
    fun genderFilter(gender: String, suitability: String): Boolean = when (gender.lowercase()) {
        "male" -> suitability == "both" || suitability == "male"
        "female" -> suitability == "both" || suitability == "female"
        else -> true
    }

    // ---- FuzzySearch (Levenshtein ≤ 2 OR contains) ----
    fun fuzzyMatch(query: String, candidate: String): Boolean {
        if (query.isBlank()) return true
        val q = query.lowercase().trim()
        val c = candidate.lowercase()
        if (c.contains(q)) return true
        return levenshtein(q, c) <= 2
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
        }
        return dp[a.length][b.length]
    }

    // ---- RpeColor (returns a 0..360 hue for HSL conversion) ----
    fun rpeColor(rpe: Int): String = when {
        rpe >= 9 -> "red"
        rpe >= 7 -> "orange"
        rpe >= 5 -> "yellow"
        rpe >= 3 -> "green"
        else -> "blue"
    }

    // ---- CompetitionDate ----
    data class CompPhase(val daysOut: Int, val phase: String)

    fun competitionDate(iso: String): CompPhase? {
        val ms = ProState.utcDayMillis(iso).takeIf { it > 0 } ?: return null
        val days = ((ms - System.currentTimeMillis()) / (24L * 3600 * 1000)).toInt()
        val phase = when {
            days < 0 -> "Past"
            days <= 7 -> "Peaking"
            days <= 28 -> "Building"
            days <= 84 -> "Base"
            else -> "Off-season"
        }
        return CompPhase(days, phase)
    }

    // ---- ExpandedMessageBank ----
    object ExpandedMessageBank {
        fun forTone(tone: String): List<String> = when (tone) {
            "ease" -> listOf(
                "Pull back today — you've been pushing hard.",
                "Recovery is part of the work. Ease off 10%.",
                "Listen to your body: a deload week is fine.",
            )
            "push" -> listOf(
                "You've got fuel in the tank — push it today.",
                "Fresh and ready? Add a set or two.",
                "Today's the day to chase a PR.",
            )
            "hold" -> listOf(
                "Steady as she goes — the plan is working.",
                "Trust the program. Same as last week.",
                "Consistency beats intensity today.",
            )
            else -> listOf(
                "Show up. That's the win.",
                "One rep at a time.",
                "Form first, weight second.",
            )
        }
    }

    // ---- ExerciseAudit ----
    data class ExerciseAuditRow(
        val id: String,
        val name: String,
        val tested1RM: Double?,
        val est1RM: Double?,
        val lastPerformed: String?,
        val weeksSinceTest: Int,
        val confidence: String,
    )

    fun exerciseAudit(): List<ExerciseAuditRow> {
        return ProState.data.exercises.entries.map { (id, rec) ->
            ExerciseAuditRow(
                id = id,
                name = com.peakform.fitness.engine.Library.getExerciseById(id)?.name ?: id,
                tested1RM = rec.tested1RM,
                est1RM = rec.mu,
                lastPerformed = rec.lastPerformed,
                weeksSinceTest = OneRmLab.weeksSinceTest(rec.testDate),
                confidence = OneRmLab.confidenceLabel(rec.sigma2),
            )
        }.sortedByDescending { it.tested1RM ?: it.est1RM ?: 0.0 }
    }

    // ---- EwmaPerExercise ----
    /** EWMA of weekly volume per exercise. alpha = 0.4 (recent weeks weigh more). */
    fun ewmaPerExercise(alpha: Double = 0.4): Map<String, Double> {
        val byExe = mutableMapOf<String, MutableList<Pair<Long, Double>>>()
        ProState.data.workouts.filter { it.isCompleted }.forEach { w ->
            val dayMs = ProState.utcDayMillis(w.date)
            w.exercises.forEach { ex ->
                val a = ex.actual
                val vol: Double = when {
                    a == null -> 0.0
                    a.volume != null -> a.volume
                    else -> a.weight * a.sets
                }
                if (vol > 0) byExe.getOrPut(ex.id) { mutableListOf() }.add(dayMs to vol)
            }
        }
        return byExe.mapValues { (_, entries) ->
            entries.sortedBy { it.first }.map { it.second }.fold(0.0) { acc, v -> alpha * v + (1 - alpha) * acc }
        }
    }

    // ---- CouplingMatrixWriteback ----
    /** Persist a user-edited coupling-matrix override per-profile. */
    fun saveCouplingOverride(ctx: Context, json: String) {
        ProfileSettings.put(ctx, "p4_coupling_override", json)
        ProLog.i("GAPCLOSURES", "coupling matrix override saved (${json.length} bytes)")
    }

    fun loadCouplingOverride(ctx: Context): String? =
        ProfileSettings.get(ctx, "p4_coupling_override")

    // ---- TestResultSync ----
    /** Mirror a 1RM test result into the exercise record so every chart sees it. */
    fun syncTestResult(exerciseId: String, weight: Double, reps: Int, date: String) {
        val exercises = ProState.data.exercises.toMutableMap()
        val rec = exercises[exerciseId] ?: ExerciseRecord()
        exercises[exerciseId] = rec.copy(
            tested1RM = weight,
            testDate = date,
            testReps = reps,
            testWeight = weight,
            lastUpdate = ProState.nowIso(),
        )
        ProState.data = ProState.data.copy(exercises = exercises)
        ProState.saveWorkoutData()
        ProLog.i("GAPCLOSURES", "synced test result for $exerciseId: $weight × $reps")
    }

    // ---- VersionCheck ----
    /** Returns -1 if a < b, 0 if equal, 1 if a > b. Both must be like "1.5.1". */
    fun versionCompare(a: String, b: String): Int {
        val pa = a.split(".").map { it.toIntOrNull() ?: 0 }
        val pb = b.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(pa.size, pb.size)) {
            val av = pa.getOrElse(i) { 0 }
            val bv = pb.getOrElse(i) { 0 }
            if (av != bv) return av.compareTo(bv)
        }
        return 0
    }
}
