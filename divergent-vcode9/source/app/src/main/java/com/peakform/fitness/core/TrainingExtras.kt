package com.peakform.fitness.core

import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * TrainingExtras — v1.4.0 native enhancements (review board: product expert MUST #1/#4,
 * systems expert REC 4/5). Pure logic, unit-tested in ParityTest.
 *
 * 1. PlateMath — the #1 gym-floor moment the legacy app never had: it computed fiber-window
 *    weight RANGES but never told you what to physically LOAD. Given a target bar weight and
 *    the bar itself, produce the exact per-side plate breakdown, snapping to available plates.
 * 2. Favorites — star any exercise, persisted in the p4_ls prefs mirror under a
 *    namespaced key ("pro_favorites"). Stored as a JSON string set so Complete-Backup-v1
 *    bundles carry it verbatim with zero schema risk.
 * 3. Test-input validation for the 1RM Lab — mirrors the legacy modal rules verbatim
 *    (missing fields / reps 1–15) plus the fat-finger guard (systems expert REC 4).
 */
object PlateMath {

    /** Standard Olympic plate tree, heaviest first (lbs). */
    val STANDARD_PLATES = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5)

    /**
     * Per-side plates needed to reach [target] on a [bar]-pound bar.
     * Returns (perSidePlates heaviest-first, residual weight the plates cannot express).
     * Residual > 0 means the target sits between plate combos — caller shows "≈".
     */
    fun forWeight(target: Double, bar: Double = 45.0, available: List<Double> = STANDARD_PLATES): Pair<List<Double>, Double> {
        var side = (target - bar) / 2.0
        if (side < 0) side = 0.0
        val out = mutableListOf<Double>()
        var residual = side
        for (p in available) {
            while (residual >= p - 0.001) {
                out.add(p)
                residual -= p
            }
        }
        return out to residual
    }

    /** Human string: "45 · 25 · 10 per side" or "empty bar" when nothing fits. */
    fun describe(target: Double, bar: Double = 45.0): String {
        val (plates, residual) = forWeight(target, bar)
        val base = if (plates.isEmpty()) "empty bar"
        else plates.joinToString(" · ") {
            if (it == it.toLong().toDouble()) "${it.toLong()}" else "$it"
        } + " per side"
        return if (residual > 0.01) "$base (+${PlateMath.fmt(residual)})" else base
    }

    fun fmt(v: Double): String = if (v == v.toLong().toDouble()) "${v.toLong()}" else String.format("%.1f", v)
}

object Favorites {
    private const val KEY = "pro_favorites"

    fun load(ctx: android.content.Context): Set<String> =
        ProPrefs.get(ctx, KEY)?.let { raw ->
            runCatching { Json.decodeFromString(SetSerializer(String.serializer()), raw) }.getOrNull()
        } ?: emptySet()

    fun save(ctx: android.content.Context, favs: Set<String>) {
        ProPrefs.put(ctx, KEY, Json.encodeToString(SetSerializer(String.serializer()), favs))
    }

    fun toggle(ctx: android.content.Context, id: String): Set<String> {
        val next = load(ctx).let { if (id in it) it - id else it + id }
        save(ctx, next)
        return next
    }
}

object OneRmInput {
    sealed class Result {
        data class Ok(val weight: Double, val reps: Int) : Result()
        data class Missing(val title: String, val message: String) : Result()
    }

    /**
     * Legacy modal validation, verbatim (legacy 8824-8825, 8963-8964):
     *  - missing either field  -> "Missing Information" / "Please enter both weight and reps."
     *  - reps outside 1..15    -> "Invalid Reps" / "Reps must be between 1 and 15 for accurate 1RM estimation."
     * Weight must parse to > 0.
     */
    fun validate(weightText: String, repsText: String): Result {
        val w = weightText.trim().toDoubleOrNull()
        val r = repsText.trim().toIntOrNull()
        if (w == null || w <= 0 || r == null) {
            return Result.Missing("Missing Information", "Please enter both weight and reps.")
        }
        if (r < 1 || r > 15) {
            return Result.Missing("Invalid Reps", "Reps must be between 1 and 15 for accurate 1RM estimation.")
        }
        return Result.Ok(w, r)
    }

    /** Fat-finger guard (systems REC 4): tested 1RM above 3.5× bodyweight is suspicious. */
    fun implausible(oneRm: Double, bodyweight: Double?): Boolean =
        bodyweight != null && bodyweight > 0 && oneRm > bodyweight * 3.5
}
