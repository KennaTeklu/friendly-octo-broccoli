package com.peakform.fitness.core

import kotlin.math.abs
import kotlin.math.min

/**
 * GapClosures — BATCH-18C.
 *
 * A collection of small, focused helpers that close specific UX / data gaps
 * surfaced by the v2.0 architecture audit. Each closure is its own class so
 * the test battery can target it individually.
 *
 *  - GenderFilter     — filter the exercise library by gender suitability
 *  - FuzzySearch      — Levenshtein-distance-based exercise name search
 *  - RpeColor         — RPE 1-10 → Tailwind color token
 *  - CompetitionDate  — countdown days + taper-week flag
 *  - SkillFactorBucket — categorize an exercise as Easy/Medium/Hard
 *  - AgeBucket        — under-16 / 16-64 / 65+ bucket from birth date
 *  - PostpartumFlag   — flag a profile as postpartum (0-6 months)
 *  - VolumeBucket     — light/moderate/heavy from total tonnage
 */
object GapClosures {

    // ---------------- GenderFilter ----------------

    object GenderFilter {
        /** Returns true if the exercise is suitable for the given gender. */
        fun accepts(ex: LibraryExercise, gender: String): Boolean {
            // genderSuitability ∈ {both, male, female, unspecified}
            val suit = ex.genderSuitability.ifBlank { "both" }
            return suit == "both" || suit == gender
        }

        /** Apply the filter to a list — keeps only the exercises suitable for gender. */
        fun apply(list: List<LibraryExercise>, gender: String): List<LibraryExercise> =
            list.filter { accepts(it, gender) }
    }

    // ---------------- FuzzySearch ----------------

    object FuzzySearch {
        /** Levenshtein distance — classic DP, O(n*m). */
        fun distance(a: String, b: String): Int {
            val la = a.lowercase()
            val lb = b.lowercase()
            val n = la.length; val m = lb.length
            if (n == 0) return m; if (m == 0) return n
            val prev = IntArray(m + 1) { it }
            val curr = IntArray(m + 1)
            for (i in 1..n) {
                curr[0] = i
                for (j in 1..m) {
                    val cost = if (la[i - 1] == lb[j - 1]) 0 else 1
                    curr[j] = minOf(curr[j - 1] + 1, prev[j] + 1, prev[j - 1] + cost)
                }
                prev.indices.forEach { prev[it] = curr[it] }
            }
            return prev[m]
        }

        /** Score 0..1 — 1 = perfect match, 0 = no similarity. */
        fun score(query: String, target: String): Double {
            if (query.isBlank() && target.isBlank()) return 1.0
            val maxLen = maxOf(query.length, target.length)
            if (maxLen == 0) return 1.0
            return 1.0 - distance(query, target).toDouble() / maxLen
        }

        /** Search a list — returns items sorted by score (best first). */
        fun search(query: String, list: List<LibraryExercise>, threshold: Double = 0.3): List<LibraryExercise> =
            list.map { it to score(query, it.name) }
                .filter { it.second >= threshold }
                .sortedByDescending { it.second }
                .map { it.first }

        /** Convenience — also matches muscle tags. */
        fun searchWithMuscles(query: String, list: List<LibraryExercise>, threshold: Double = 0.3): List<LibraryExercise> =
            list.map { ex ->
                val nameScore = score(query, ex.name)
                val muscleScore = ex.muscles.maxOfOrNull { score(query, it) } ?: 0.0
                ex to maxOf(nameScore, muscleScore)
            }
                .filter { it.second >= threshold }
                .sortedByDescending { it.second }
                .map { it.first }
    }

    // ---------------- RpeColor ----------------

    object RpeColor {
        /** Returns a hex color string for the given RPE (1-10). */
        fun hex(rpe: Int): String = when (rpe.coerceIn(1, 10)) {
            in 1..4 -> "#34D399"  // green — easy
            in 5..6 -> "#A3E635"  // lime — moderate
            7 -> "#FACC15"        // yellow — challenging
            8 -> "#FB923C"        // orange — hard
            9 -> "#F87171"        // red — very hard
            10 -> "#DC2626"       // dark red — max
            else -> "#8A919E"
        }

        /** Returns the legacy token name (matches the HTML app's color tokens). */
        fun token(rpe: Int): String = when (rpe.coerceIn(1, 10)) {
            in 1..4 -> "ok"
            in 5..6 -> "lime"
            7 -> "warn"
            else -> "bad"
        }
    }

    // ---------------- CompetitionDate ----------------

    object CompetitionDate {
        data class Plan(
            val daysOut: Int,
            val isInTaperWindow: Boolean,  // last 7 days
            val isInPeakBlock: Boolean,    // last 4 weeks
            val suggestedTaperPct: Double, // volume % to drop in taper
        )

        /** Compute the taper plan from a competition date string (yyyy-MM-dd). */
        fun plan(compDate: String): Plan {
            val targetMs = try {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    .parse(compDate)?.time ?: return Plan(-1, false, false, 0.0)
            } catch (_: Exception) { return Plan(-1, false, false, 0.0) }
            val nowMs = System.currentTimeMillis()
            val daysOut = ((targetMs - nowMs) / (24L * 3600 * 1000)).toInt()
            val taper = daysOut in 0..7
            val peak = daysOut in 0..28
            val pct = when {
                daysOut < 0 -> 0.0
                daysOut <= 3 -> 0.5   // half volume in the last 3 days
                daysOut <= 7 -> 0.7   // 70% in the last week
                else -> 1.0
            }
            return Plan(daysOut, taper, peak, pct)
        }
    }

    // ---------------- SkillFactorBucket ----------------

    object SkillFactorBucket {
        fun label(sf: Double): String = when {
            sf < 0.5 -> "Hard"
            sf < 0.7 -> "Medium"
            else -> "Easy"
        }
    }

    // ---------------- AgeBucket ----------------

    object AgeBucket {
        fun fromBirthDate(birth: String?): String? {
            if (birth.isNullOrBlank()) return null
            val d = try {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(birth)
            } catch (_: Exception) { return null } ?: return null
            val age = ((System.currentTimeMillis() - d.time) / 31557600000.0).toInt()
            return when {
                age < 16 -> "youth"
                age >= 65 -> "senior"
                else -> "adult"
            }
        }
    }

    // ---------------- PostpartumFlag ----------------

    object PostpartumFlag {
        fun isPostpartum(monthsPostpartum: Int?): Boolean =
            monthsPostpartum != null && monthsPostpartum in 0..6
    }

    // ---------------- VolumeBucket ----------------

    object VolumeBucket {
        fun label(totalVolume: Double): String = when {
            totalVolume < 5000 -> "light"
            totalVolume < 20000 -> "moderate"
            else -> "heavy"
        }
    }
}
