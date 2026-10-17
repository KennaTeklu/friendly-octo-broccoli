package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.coach.dossiers.ExerciseDossier
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlin.math.max
import kotlin.math.min

/**
 * BATCH-42 — OneRmResolver.
 *
 * Source: BATCH-42 spec (1RM Resolution Chain).
 *
 * The 1RM test panel appears on every compound barbell exercise. It should
 * only appear when the system has NO way to estimate a 1RM from history or
 * siblings. The resolver walks six sources in order — first hit wins:
 *
 *   1. Direct 1RM         — tested1RM stored on the ExerciseRecord.   conf 1.00
 *   2. Recent history     — 3+ logged sets at 1-5 reps in 30 days.     conf 0.85
 *                            Epley/Brzycki average.
 *   3. Sibling tested 1RM — related exercise (overlap >= 0.6) has       conf 0.70
 *                            tested 1RM. Ratio from strength_ratios.json.
 *   4. Sibling history    — same as #2 on a sibling. Apply ratio.       conf 0.60
 *   5. Muscle history     — primary muscles trained in 14 days.         conf 0.30
 *                            Estimate from recent peak volume.
 *   6. Nothing            — return null. The caller renders the panel.
 *
 * requirements().needs1RM still gates whether the exercise COULD have a
 * 1RM. The resolver gates whether the user must be asked.
 *
 * Sibling discovery (per spec):
 *   1. Read target dossier.
 *   2. For each other exercise with a dossier, compute overlap:
 *        overlap = sum over muscles m of min(target.sp[m], sibling.sp[m])
 *      where sp is the dossier's stimulusProfile (muscle -> weight).
 *   3. Siblings have overlap >= 0.6 AND have a tested 1RM OR recent history.
 *   4. From strength_ratios.json, look up the ratio. If missing, skip.
 *
 * The resolver is read-only. It never writes to ProState. It never throws —
 * every lookup is fail-soft and returns null on failure.
 *
 * Asset loading is gated by [ensure] (mirrors DossierRegistry / Library).
 * Tests use [ensureForTest] with an injected asset loader. UI calls go
 * through [resolve] which silently no-ops when the ratios asset isn't loaded
 * (siblings are then skipped — the resolver falls through to source 5 or 6).
 */
object OneRmResolver {

    /** One resolution result. */
    data class Estimate(
        val value: Double,
        val source: String,
        val confidence: Double,
    )

    // ---- Source confidence constants (spec) ----
    const val CONF_TESTED: Double = 1.0
    const val CONF_RECENT: Double = 0.85
    const val CONF_SIBLING_1RM: Double = 0.70
    const val CONF_SIBLING_HISTORY: Double = 0.60
    const val CONF_MUSCLE: Double = 0.30

    // ---- Source label templates (spec) ----
    const val SRC_TESTED: String = "tested"
    const val SRC_RECENT: String = "estimated from recent"        // "<exercise>"
    const val SRC_SIBLING_1RM: String = "derived from"            // "<sibling> 1RM"
    const val SRC_SIBLING_HISTORY: String = "derived from"         // "<sibling> history"
    const val SRC_MUSCLE: String = "estimated from muscle history"

    // ---- Time windows (spec) ----
    const val RECENT_WINDOW_DAYS: Long = 30L
    const val MUSCLE_WINDOW_DAYS: Long = 14L
    const val MIN_RECENT_SETS: Int = 3

    // ---- Sibling overlap threshold (spec) ----
    const val SIBLING_OVERLAP_THRESHOLD: Double = 0.6

    @Volatile private var loaded: Boolean = false
    @Volatile private var ratios: Map<String, Map<String, Double>> = emptyMap()

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /** Load the strength_ratios.json asset via the app context. Idempotent. */
    fun ensure(ctx: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            loadImpl { name -> ctx.assets.open("data/$name").bufferedReader().use { it.readText() } }
        }
    }

    /** JVM-test entry: loads ratios from an injected source (no Android context). */
    fun ensureForTest(loader: (String) -> String) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            loadImpl { name -> loader(name) }
        }
    }

    /** Test hook: reset to unloaded so a different ratios asset can be loaded. */
    fun resetForTest() {
        synchronized(this) {
            loaded = false
            ratios = emptyMap()
        }
    }

    private fun loadImpl(asset: (String) -> String) {
        try {
            val raw = asset("strength_ratios.json")
            val obj = json.parseToJsonElement(raw).jsonObject
            val parsed = mutableMapOf<String, Map<String, Double>>()
            for ((outerKey, outerVal) in obj) {
                val inner = mutableMapOf<String, Double>()
                val innerObj = outerVal.jsonObject
                for ((k, v) in innerObj) {
                    val d = v.jsonPrimitive.doubleOrNull
                    if (d != null && d > 0.0) inner[k] = d
                }
                if (inner.isNotEmpty()) parsed[outerKey] = inner
            }
            ratios = parsed
        } catch (_: Exception) {
            ratios = emptyMap()
        }
        loaded = true
    }

    /** Whether the asset has been loaded. UI code may use this to no-op. */
    val isReady: Boolean get() = loaded

    // ---- Public API --------------------------------------------------------

    /**
     * Resolve a 1RM estimate for the given exerciseId. Walks the six sources
     * in order; first non-null hit wins.
     *
     * Returns null if no source produces an estimate — the caller should
     * render the OneRmTestPanel.
     */
    fun resolve(exerciseId: String): Estimate? {
        // SOURCE 1 — Direct tested 1RM.
        resolveDirectTested(exerciseId)?.let { return it }

        // SOURCE 2 — Recent direct history (3+ sets at 1-5 reps in 30 days).
        resolveRecentHistory(exerciseId)?.let { return it }

        // Siblings are computed once and shared between sources 3 and 4.
        val targetDossier = DossierRegistry.forExercise(exerciseId)
        if (targetDossier != null) {
            val siblings = discoverSiblings(exerciseId, targetDossier)

            // SOURCE 3 — Sibling tested 1RM.
            for (sib in siblings) {
                resolveSiblingTested(exerciseId, sib)?.let { return it }
            }
            // SOURCE 4 — Sibling history.
            for (sib in siblings) {
                resolveSiblingHistory(exerciseId, sib)?.let { return it }
            }
        }

        // SOURCE 5 — Muscle history.
        resolveMuscleHistory(exerciseId, targetDossier)?.let { return it }

        // SOURCE 6 — Nothing.
        return null
    }

    // ---- Source 1: direct tested 1RM --------------------------------------

    private fun resolveDirectTested(exerciseId: String): Estimate? {
        val rec = ProState.data.exercises[exerciseId] ?: return null
        val tested = rec.tested1RM?.takeIf { it > 0 } ?: return null
        return Estimate(
            value = tested,
            source = SRC_TESTED,
            confidence = CONF_TESTED,
        )
    }

    // ---- Source 2: recent direct history ----------------------------------

    /**
     * 3+ logged sets at 1-5 reps in 30 days. Epley/Brzycki average.
     * Source label: "estimated from recent <exercise>".
     */
    private fun resolveRecentHistory(exerciseId: String): Estimate? {
        val now = System.currentTimeMillis()
        val cutoff = now - RECENT_WINDOW_DAYS * 24L * 3600L * 1000L
        val sessions = ProState.recentSessions(exerciseId, limit = 50)
        val recentSets = mutableListOf<Pair<Double, Int>>()  // (weight, reps)
        for (s in sessions) {
            if (s.timestamp < cutoff) continue
            for (ex in s.exercises) {
                if (ex.exerciseId != exerciseId) continue
                if (ex.skipped) continue
                for (set in ex.sets) {
                    if (!set.completed) continue
                    val reps = set.reps
                    if (reps < 1 || reps > 5) continue
                    val w = set.load
                    if (w <= 0.0) continue
                    recentSets.add(w to reps)
                }
            }
        }
        if (recentSets.size < MIN_RECENT_SETS) return null
        val epleyAvg = recentSets.map { (w, r) -> OneRmLab.estimate(w, r, OneRmLab.F_EPLEY) }
            .filter { it > 0 }
            .averageOrNull() ?: return null
        val brzyckiAvg = recentSets.map { (w, r) -> OneRmLab.estimate(w, r, OneRmLab.F_BRZYCKI) }
            .filter { it > 0 }
            .averageOrNull() ?: return null
        val avg = (epleyAvg + brzyckiAvg) / 2.0
        if (avg <= 0.0) return null
        return Estimate(
            value = avg,
            source = "$SRC_RECENT ${friendlyName(exerciseId)}",
            confidence = CONF_RECENT,
        )
    }

    // ---- Source 3: sibling tested 1RM -------------------------------------

    /**
     * Related exercise (overlap >= 0.6) has a tested 1RM. Read the ratio
     * from strength_ratios.json. Apply ratio to convert sibling's 1RM
     * to target's 1RM: target_est = sibling_1RM * ratio[target][sibling].
     */
    private fun resolveSiblingTested(targetId: String, sibling: Sibling): Estimate? {
        val ratio = lookupRatio(targetId, sibling) ?: return null
        val sibRec = ProState.data.exercises[sibling.exerciseId] ?: return null
        val sibTested = sibRec.tested1RM?.takeIf { it > 0 } ?: return null
        val value = sibTested * ratio
        if (value <= 0.0) return null
        return Estimate(
            value = value,
            source = "$SRC_SIBLING_1RM ${friendlyName(sibling.exerciseId)} 1RM",
            confidence = CONF_SIBLING_1RM,
        )
    }

    // ---- Source 4: sibling history ----------------------------------------

    /**
     * Same as Source 2 on a sibling. Apply ratio to convert.
     */
    private fun resolveSiblingHistory(targetId: String, sibling: Sibling): Estimate? {
        val ratio = lookupRatio(targetId, sibling) ?: return null
        val now = System.currentTimeMillis()
        val cutoff = now - RECENT_WINDOW_DAYS * 24L * 3600L * 1000L
        val sessions = ProState.recentSessions(sibling.exerciseId, limit = 50)
        val recentSets = mutableListOf<Pair<Double, Int>>()
        for (s in sessions) {
            if (s.timestamp < cutoff) continue
            for (ex in s.exercises) {
                if (ex.exerciseId != sibling.exerciseId) continue
                if (ex.skipped) continue
                for (set in ex.sets) {
                    if (!set.completed) continue
                    val reps = set.reps
                    if (reps < 1 || reps > 5) continue
                    val w = set.load
                    if (w <= 0.0) continue
                    recentSets.add(w to reps)
                }
            }
        }
        if (recentSets.size < MIN_RECENT_SETS) return null
        val epleyAvg = recentSets.map { (w, r) -> OneRmLab.estimate(w, r, OneRmLab.F_EPLEY) }
            .filter { it > 0 }.averageOrNull() ?: return null
        val brzyckiAvg = recentSets.map { (w, r) -> OneRmLab.estimate(w, r, OneRmLab.F_BRZYCKI) }
            .filter { it > 0 }.averageOrNull() ?: return null
        val sibEstimate = (epleyAvg + brzyckiAvg) / 2.0
        val value = sibEstimate * ratio
        if (value <= 0.0) return null
        return Estimate(
            value = value,
            source = "$SRC_SIBLING_HISTORY ${friendlyName(sibling.exerciseId)} history",
            confidence = CONF_SIBLING_HISTORY,
        )
    }

    // ---- Source 5: muscle history ----------------------------------------

    /**
     * Primary muscles trained in 14 days, no direct/sibling data.
     * Estimate from recent peak volume.
     *
     * Implementation: walk recent sessions (<=14 days). For each session
     * whose exercise touches a target primary muscle (dossier.stimulusProfile
     * weight >= 0.7), collect the heaviest 1-5 rep set seen. The Epley
     * estimate of that set, scaled by the muscle's relative load
     * (target muscle weight / sibling muscle weight, capped to 1.0),
     * is the estimate.
     *
     * This is intentionally a coarse heuristic — confidence is 0.30.
     */
    private fun resolveMuscleHistory(
        exerciseId: String,
        targetDossier: ExerciseDossier?,
    ): Estimate? {
        if (targetDossier == null) return null
        val primaryMuscles = targetDossier.stimulusProfile
            .filter { it.value >= 0.7 }
            .keys
        if (primaryMuscles.isEmpty()) return null

        val now = System.currentTimeMillis()
        val cutoff = now - MUSCLE_WINDOW_DAYS * 24L * 3600L * 1000L
        val sessions = ProState.sessions
            .filter { it.timestamp >= cutoff }
            .sortedByDescending { it.timestamp }

        // Collect heaviest 1-5 rep set per primary muscle.
        val heaviestPerMuscle = mutableMapOf<String, Pair<Double, Int>>()
        for (s in sessions) {
            for (ex in s.exercises) {
                if (ex.skipped) continue
                val exDossier = DossierRegistry.forExercise(ex.exerciseId) ?: continue
                val exMuscles = exDossier.stimulusProfile
                    .filter { it.value >= 0.7 }
                    .keys
                val shared = exMuscles.intersect(primaryMuscles)
                if (shared.isEmpty()) continue
                for (set in ex.sets) {
                    if (!set.completed) continue
                    val reps = set.reps
                    if (reps < 1 || reps > 5) continue
                    val w = set.load
                    if (w <= 0.0) continue
                    for (m in shared) {
                        val cur = heaviestPerMuscle[m]
                        val newEpley = OneRmLab.estimate(w, reps, OneRmLab.F_EPLEY)
                        if (cur == null || newEpley > OneRmLab.estimate(cur.first, cur.second, OneRmLab.F_EPLEY)) {
                            heaviestPerMuscle[m] = w to reps
                        }
                    }
                }
            }
        }
        if (heaviestPerMuscle.isEmpty()) return null

        // Average across primary muscles — coarse heuristic.
        val estimates = heaviestPerMuscle.map { (_, wr) ->
            OneRmLab.estimate(wr.first, wr.second, OneRmLab.F_EPLEY)
        }
        val avg = estimates.averageOrNull() ?: return null
        if (avg <= 0.0) return null
        return Estimate(
            value = avg,
            source = SRC_MUSCLE,
            confidence = CONF_MUSCLE,
        )
    }

    // ---- Sibling discovery ------------------------------------------------

    /** Internal: a sibling candidate's resolved identity. */
    private data class Sibling(
        val exerciseId: String,
        val coachId: String,
        val overlap: Double,
    )

    /**
     * Discover siblings for the target exercise.
     *
     * Sibling = a different exercise with a dossier whose stimulusProfile
     * overlaps the target's by >= SIBLING_OVERLAP_THRESHOLD (sum of min
     * weights across shared muscles), AND that has a tested 1RM OR recent
     * history. The ratio lookup is deferred to the caller (skip if missing).
     *
     * Returns siblings sorted by descending overlap so the strongest
     * sibling is tried first.
     */
    private fun discoverSiblings(
        targetId: String,
        targetDossier: ExerciseDossier,
    ): List<Sibling> {
        val targetSp = targetDossier.stimulusProfile
        if (targetSp.isEmpty()) return emptyList()

        val out = mutableListOf<Sibling>()
        for (dossier in DossierRegistry.all()) {
            if (dossier.exerciseId == targetId) continue
            // Same coachId → same family, not a sibling (it's the same exercise family).
            // The spec says "sibling", implying different exercise. We still allow
            // different coachIds on the same family — the ratio lookup will gate.
            val sibSp = dossier.stimulusProfile
            if (sibSp.isEmpty()) continue
            val overlap = muscleOverlap(targetSp, sibSp)
            if (overlap < SIBLING_OVERLAP_THRESHOLD) continue
            // Must have tested1RM OR recent history to be a usable sibling.
            val sibRec = ProState.data.exercises[dossier.exerciseId]
            val hasTested = sibRec?.tested1RM?.let { it > 0 } == true
            val hasRecent = hasRecentHistory(dossier.exerciseId)
            if (!hasTested && !hasRecent) continue
            out.add(Sibling(dossier.exerciseId, dossier.coachId, overlap))
        }
        return out.sortedByDescending { it.overlap }
    }

    /** Compute muscle overlap = sum over muscles m of min(a[m], b[m]). */
    fun muscleOverlap(a: Map<String, Double>, b: Map<String, Double>): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        val smaller = if (a.size <= b.size) a else b
        val larger = if (a.size <= b.size) b else a
        var sum = 0.0
        for ((m, w) in smaller) {
            val w2 = larger[m]
            if (w2 != null) sum += min(w, w2)
        }
        return sum
    }

    /** True if the exercise has 3+ sets at 1-5 reps in the recent window. */
    private fun hasRecentHistory(exerciseId: String): Boolean {
        val now = System.currentTimeMillis()
        val cutoff = now - RECENT_WINDOW_DAYS * 24L * 3600L * 1000L
        var count = 0
        for (s in ProState.recentSessions(exerciseId, limit = 50)) {
            if (s.timestamp < cutoff) continue
            for (ex in s.exercises) {
                if (ex.exerciseId != exerciseId) continue
                if (ex.skipped) continue
                for (set in ex.sets) {
                    if (!set.completed) continue
                    if (set.reps in 1..5 && set.load > 0.0) count++
                    if (count >= MIN_RECENT_SETS) return true
                }
            }
        }
        return false
    }

    /**
     * Look up the strength ratio from target -> sibling.
     *
     * strength_ratios.json is keyed by exercise family — the spec says
     * "look up the ratio. If missing, skip sibling." We try multiple keys
     * in order: target's exerciseId, target's coachId, common synonyms.
     * Inside the matched outer entry, we look for the sibling's
     * exerciseId, coachId, or common synonyms.
     *
     * Returns null if no ratio is found (caller skips the sibling).
     */
    private fun lookupRatio(targetId: String, sibling: Sibling): Double? {
        if (!loaded) return null
        val targetKeys = candidateKeys(targetId, targetCoachId(targetId))
        val sibKeys = candidateKeys(sibling.exerciseId, sibling.coachId)
        for (tKey in targetKeys) {
            val inner = ratios[tKey] ?: continue
            for (sKey in sibKeys) {
                val r = inner[sKey]
                if (r != null && r > 0.0) return r
            }
        }
        return null
    }

    /** Resolve the coachId for the target exercise via DossierRegistry. */
    private fun targetCoachId(targetId: String): String? =
        DossierRegistry.forExercise(targetId)?.coachId

    /**
     * Build a list of candidate keys to try when looking up a ratio.
     * Order: exerciseId (as-is), coachId (as-is), exerciseId without
     * trailing suffixes, and a few common synonyms (squat, bench_press,
     * deadlift, barbell_row) that map known multi-id families.
     */
    private fun candidateKeys(exerciseId: String, coachId: String?): List<String> {
        val out = linkedSetOf<String>()
        if (exerciseId.isNotBlank()) out.add(exerciseId)
        if (coachId != null && coachId.isNotBlank()) out.add(coachId)
        // Common synonyms present in strength_ratios.json.
        when (coachId ?: exerciseId) {
            "back_squat", "front_squat", "banded_squat" -> {
                out.add("squat")
                out.add("back_squat")
                out.add("front_squat")
            }
            "bench_press", "incline_bench_press", "close_grip_bench_press" -> {
                out.add("bench_press")
                out.add("incline_bench")
                out.add("dumbbell_bench")
            }
            "deadlift", "sumo_deadlift", "romanian_deadlift" -> {
                out.add("deadlift")
                out.add("rdl")
            }
            "barbell_row", "pendlay_row", "inverted_row", "cable_row",
            "lat_pulldown", "face_pull" -> {
                out.add("barbell_row")
                out.add("cable_row")
                out.add("lat_pulldown")
            }
            "overhead_press", "push_press" -> {
                out.add("shoulder_press")
                out.add("overhead_press")
            }
        }
        return out.toList()
    }

    /** Friendly name for source labels: replace underscores, title-case first word. */
    private fun friendlyName(exerciseId: String): String =
        exerciseId.replace('_', ' ').trim().replaceFirstChar { it.uppercaseChar() }
}

// ---- small extension so we can call .averageOrNull() without crashing on empty lists ----

private fun List<Double>.averageOrNull(): Double? =
    if (isEmpty()) null else this.average()
