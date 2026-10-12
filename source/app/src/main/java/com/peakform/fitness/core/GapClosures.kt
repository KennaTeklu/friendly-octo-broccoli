package com.peakform.fitness.core

/**
 * GapClosures — small stateless helpers that close the last batch of gaps
 * the reference HTML app had but the native port didn't.
 *
 * BATCH-13: gender-filtered library view, fuzzy exercise search, RPE color
 * band, competition-date countdown, postpartum intensity multiplier, and
 * the "next progression suggestion" formatter.
 *
 * Pure functions only — no ProState writes, no side effects. The UI calls
 * these from any thread.
 */
object GapClosures {

    /** Filter the library to movements appropriate for the user's gender
     *  (legacy "genderFilter" — drops movements tagged for the other gender). */
    fun filterByGender(exercises: List<LibraryExercise>, gender: String?): List<LibraryExercise> {
        if (gender == null || gender == "unspecified") return exercises
        return exercises.filter { ex ->
            val tags = ex.fitnessComponents + ex.muscles
            // Legacy tag system: "mens_only" / "womens_only" / "pelvic_safe"
            val mensOnly = tags.any { it.equals("mens_only", ignoreCase = true) }
            val womensOnly = tags.any { it.equals("womens_only", ignoreCase = true) }
            when (gender) {
                "male" -> !womensOnly
                "female" -> !mensOnly
                else -> true
            }
        }
    }

    /** Fuzzy search: substring match on name + muscles + equipment, with a
     *  Levenshtein fallback for short queries (< 4 chars). */
    fun fuzzySearch(exercises: List<LibraryExercise>, query: String): List<LibraryExercise> {
        val q = query.trim().lowercase()
        if (q.length < 2) return exercises
        val direct = exercises.filter {
            it.name.lowercase().contains(q) ||
                it.muscles.any { m -> m.lowercase().contains(q) } ||
                it.equipment.lowercase().contains(q) ||
                it.primaryMuscle.lowercase().contains(q)
        }
        if (direct.isNotEmpty() || q.length >= 4) return direct
        // Short query with no direct hit — fuzzy edit distance ≤ 1.
        return exercises.filter { levenshtein(it.name.lowercase(), q) <= 1 }
    }

    /** RPE color band: green (≤6), yellow (7-8), orange (9), red (10). */
    fun rpeColor(rpe: Double): String = when {
        rpe <= 6.0 -> "green"
        rpe <= 8.0 -> "yellow"
        rpe <= 9.0 -> "orange"
        else -> "red"
    }

    /** Days until the user's competition date (negative if past). */
    fun competitionCountdown(competitionDate: String?): Int? {
        if (competitionDate.isNullOrBlank()) return null
        return try {
            val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val target = fmt.parse(competitionDate) ?: return null
            val days = ((target.time - System.currentTimeMillis()) / (24L * 3600 * 1000)).toInt()
            days
        } catch (_: Exception) { null }
    }

    /** Postpartum intensity multiplier (0-6 months → 0.7, 6-12 → 0.85, else 1.0). */
    fun postpartumMultiplier(monthsPostpartum: Int?): Double = when {
        monthsPostpartum == null -> 1.0
        monthsPostpartum <= 6 -> 0.7
        monthsPostpartum <= 12 -> 0.85
        else -> 1.0
    }

    /** Format the "next progression" suggestion for a single exercise. */
    fun nextProgressionLabel(rec: ExerciseRecord?): String {
        if (rec == null) return "Start light — 2 sets × 10 reps at 50% 1RM"
        val next = rec.nextWeight?.takeIf { it > 0 } ?: return "Hold the load — log another session"
        val lastReps = rec.history.lastOrNull { !it.skipped }?.reps?.maxOrNull()?.toInt() ?: 8
        return "Next: ${next.toInt()} lbs × $lastReps reps"
    }

    // ---- Levenshtein (small, inlined) ----
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
}
