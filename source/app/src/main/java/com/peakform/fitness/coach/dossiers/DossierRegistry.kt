package com.peakform.fitness.coach.dossiers

/**
 * BATCH-37a — DossierRegistry.
 *
 * Reads the authored [ALL_DOSSIERS] source file and exposes the API
 * defined in source/docs/system/06-prerequisites-and-progressions.md:
 *
 *   DossierRegistry.forExercise(exerciseId) → ExerciseDossier?
 *   DossierRegistry.all() → List<ExerciseDossier>
 *   DossierRegistry.progressFrom(exerciseId, goalId) → String?   // next on path
 *   DossierRegistry.prerequisitesFor(exerciseId, goalId) → List<Prerequisite>
 *   DossierRegistry.chainFor(exerciseId) → List<String>           // both directions
 *
 * The registry is read-only at runtime. Authored content lives in
 * [Dossiers.kt] — this file just exposes pure query functions over it.
 *
 * If a user has no declared goal, progressFrom returns null. The card
 * shows "Choose a goal." (per spec doc 06 § "The resolver").
 */
object DossierRegistry {

    /** Index: exerciseId → ExerciseDossier. Built once at class init. */
    private val byId: Map<String, ExerciseDossier> = ALL_DOSSIERS.associateBy { it.exerciseId }

    /** All authored dossiers (read-only view). */
    fun all(): List<ExerciseDossier> = ALL_DOSSIERS

    /**
     * Lookup a dossier by its library exercise id. Returns null for unknown
     * ids — never throws.
     */
    fun forExercise(exerciseId: String): ExerciseDossier? = byId[exerciseId]

    /**
     * Returns the next exercise id on this goal's path. Looks up the goal
     * by [goalId] in the dossier's goals list, then resolves goal.target.
     *
     * If the exercise has no dossier, returns null.
     * If the goal is not declared on this dossier, returns null.
     * If goal.target == "chain_terminus", returns null (end of the chain).
     */
    fun progressFrom(exerciseId: String, goalId: String): String? {
        val dossier = byId[exerciseId] ?: return null
        val goal = dossier.goals.firstOrNull { it.goalId == goalId } ?: return null
        val target = goal.target
        if (target == "chain_terminus") return null
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
        val dossier = byId[exerciseId] ?: return emptyList()
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
     * Cycles are guarded against — each id appears at most once.
     */
    fun chainFor(exerciseId: String): List<String> {
        if (byId[exerciseId] == null) return listOf(exerciseId)
        val backward = mutableListOf<String>()
        val forward = mutableListOf<String>()
        val visited = mutableSetOf(exerciseId)
        // Walk backward via progressionIn (take the first parent — chains
        // are linear; the API doesn't support multi-parent DAGs).
        var current = exerciseId
        while (true) {
            val d = byId[current] ?: break
            val parent = d.progressionIn.firstOrNull() ?: break
            if (parent in visited) break
            visited.add(parent)
            backward.add(0, parent)
            current = parent
        }
        // Walk forward via progressionOut (take the first child).
        current = exerciseId
        while (true) {
            val d = byId[current] ?: break
            val child = d.progressionOut.firstOrNull() ?: break
            if (child in visited) break
            visited.add(child)
            forward.add(child)
            current = child
        }
        return backward + listOf(exerciseId) + forward
    }
}
