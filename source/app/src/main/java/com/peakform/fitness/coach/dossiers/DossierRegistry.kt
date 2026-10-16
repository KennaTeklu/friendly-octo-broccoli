package com.peakform.fitness.coach.dossiers

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * BATCH-37a — DossierRegistry.
 *
 * Reads the authored `dossiers.json` asset (1,413 entries — one per exercise
 * in exercises.json) at init time and exposes the API defined in
 * source/docs/system/06-prerequisites-and-progressions.md:
 *
 *   DossierRegistry.forExercise(exerciseId) → ExerciseDossier?
 *   DossierRegistry.all() → List<ExerciseDossier>
 *   DossierRegistry.progressFrom(exerciseId, goalId) → String?
 *   DossierRegistry.prerequisitesFor(exerciseId, goalId) → List<Prerequisite>
 *   DossierRegistry.chainFor(exerciseId) → List<String>
 *
 * The registry is read-only at runtime. Authored content lives in
 * `assets/data/dossiers.json` — generated via
 * /home/z/my-project/scripts/gen_dossiers.py.
 *
 * Loading is gated by [ensure] — call [ensure] with the app context (the
 * same way `Library.ensure` is called from MainActivity.onCreate) before
 * querying. Tests call [ensureForTest] with an asset loader lambda.
 *
 * If a user has no declared goal, progressFrom returns null. The card
 * shows "Choose a goal." (per spec doc 06 § "The resolver").
 *
 * For exercises whose dossier has no progressionIn/Out (most of them —
 * the sources do not name progression edges for the long tail), chainFor
 * returns just [exerciseId].
 *
 * Loading is fail-soft: if the asset is missing or malformed, the
 * dossiers map stays empty, lookups return null, and the app continues
 * without the dossier layer. UI consumers should treat null as
 * "no dossier available, fall back to legacy fields".
 */
object DossierRegistry {

    @Volatile private var loaded = false

    /**
     * BATCH-39c: readiness flag for the UI. Use this to gate UI calls that
     * depend on the dossier layer without blocking on `ensure`. While
     * `isReady == false`, `forExercise` returns null and the card must
     * show the fallback (awaiting dossier). The card must NOT block
     * waiting for the load.
     */
    val isReady: Boolean get() = loaded

    /** Test hook (JVM): optional first-chance asset source. */
    @Volatile var testAssetLoader: ((String) -> String?)? = null

    private var dossiers: Map<String, ExerciseDossier> = emptyMap()

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /** Load the dossiers.json asset via the app context. Idempotent. */
    fun ensure(ctx: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            loadImpl { name -> testAssetLoader?.invoke(name) ?: ctx.assets.open("data/$name").bufferedReader().use { it.readText() } }
        }
    }

    /** JVM-test entry: loads the dossiers from an injected source (no Android context needed). */
    fun ensureForTest(loader: (String) -> String) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            loadImpl { name -> testAssetLoader?.invoke(name) ?: loader(name) }
        }
    }

    private fun loadImpl(asset: (String) -> String) {
        try {
            val raw = asset("dossiers.json")
            val obj = json.parseToJsonElement(raw).jsonObject
            dossiers = obj.mapValues { (_, v) ->
                json.decodeFromString(ExerciseDossier.serializer(), v.toString())
            }
        } catch (_: Exception) {
            // Never throw on load failure — dossiers stay empty, lookups return null.
            // UI consumers should treat null as "no dossier available, fall back".
            dossiers = emptyMap()
        }
        loaded = true
    }

    /** All authored dossiers (read-only view). Sorted by exerciseId for determinism. */
    fun all(): List<ExerciseDossier> = dossiers.values.toList().sortedBy { it.exerciseId }

    /**
     * Lookup a dossier by its library exercise id. Returns null for unknown
     * ids — never throws.
     */
    fun forExercise(exerciseId: String): ExerciseDossier? = dossiers[exerciseId]

    /**
     * Returns the next exercise id on this goal's path. Looks up the goal
     * by [goalId] in the dossier's goals list, then resolves goal.target.
     *
     * If the exercise has no dossier, returns null.
     * If the goal is not declared on this dossier, returns null.
     * If goal.target is empty (no next exercise known), returns null.
     */
    fun progressFrom(exerciseId: String, goalId: String): String? {
        val dossier = dossiers[exerciseId] ?: return null
        val goal = dossier.goals.firstOrNull { it.goalId == goalId } ?: return null
        val target = goal.target
        if (target.isBlank()) return null
        return target
    }

    /**
     * Returns the prerequisites for this goal on this exercise.
     *
     * If the exercise has no dossier, returns empty.
     * If the goal is not declared on this dossier, returns empty.
     * Otherwise returns the authored Prerequisite entries for that goal.
     */
    fun prerequisitesFor(exerciseId: String, goalId: String): List<Prerequisite> {
        val dossier = dossiers[exerciseId] ?: return emptyList()
        return dossier.prerequisites.filter { it.forGoalId == goalId }
    }

    /**
     * Walks the progression chain in both directions from this exercise.
     *
     * Returns a list ordered: [most distant progressionIn ... immediate
     * progressionIn] ++ [this exercise id] ++ [immediate progressionOut ...
     * most distant progressionOut].
     *
     * If the exercise has no dossier, returns just [exerciseId].
     * If the exercise has no progressionIn/Out edges (most of the long tail),
     * returns just [exerciseId].
     * Cycles are guarded against — each id appears at most once.
     */
    fun chainFor(exerciseId: String): List<String> {
        if (dossiers[exerciseId] == null) return listOf(exerciseId)
        val backward = mutableListOf<String>()
        val forward = mutableListOf<String>()
        val visited = mutableSetOf(exerciseId)
        var current = exerciseId
        while (true) {
            val d = dossiers[current] ?: break
            val parent = d.progressionIn.firstOrNull() ?: break
            if (parent in visited) break
            visited.add(parent)
            backward.add(0, parent)
            current = parent
        }
        current = exerciseId
        while (true) {
            val d = dossiers[current] ?: break
            val child = d.progressionOut.firstOrNull() ?: break
            if (child in visited) break
            visited.add(child)
            forward.add(child)
            current = child
        }
        return backward + listOf(exerciseId) + forward
    }
}
