package com.peakform.fitness.core

import com.peakform.fitness.engine.Library

/**
 * BATCH-35e — Difficulty gating (prerequisites).
 *
 * School-grade unlock. The user does not get medium exercises for a muscle
 * group until they have passed the easy ones for that muscle group. Hard
 * unlocks after medium.
 *
 * Per muscle group. Not global.
 *
 * THE RULE
 * --------
 * For each muscle m in {primaryMuscles} of an exercise e:
 *   difficulty(e) = easy   → always unlocked
 *   difficulty(e) = medium → requires N easy sessions logged for m
 *   difficulty(e) = hard   → requires N medium sessions logged for m
 *
 * N = UNLOCK_THRESHOLD (default 3).
 *
 * WHAT COUNTS AS A SESSION
 * ------------------------
 * A "session for muscle m" is a workout log entry (`HistoryEntry`) where:
 *   - the entry belongs to an exercise whose taxonomy.primaryMuscles contains m
 *   - the entry has at least one completed set logged (entry.sets >= 1)
 *   - the entry is not skipped (entry.skipped == false)
 *
 * The exercise's taxonomy.difficulty determines which tier the session counts
 * toward (easy / medium / hard). An exercise without a taxonomy entry does
 * NOT contribute sessions (we can't know its difficulty) — but a target
 * exercise without a taxonomy is always unlocked (per spec).
 *
 * STATE TYPE NOTE
 * ---------------
 * The spec signature was `isUnlocked(exerciseId: String, state: StateModel)`.
 * `StateModel` (in core/state/StateModel.kt) is an `object` singleton that
 * describes the formal state model architecture — it's not a data class
 * and it doesn't carry the workout log shape. The actual workout log
 * (ExerciseRecord.history → List<HistoryEntry>) lives in `WorkoutData`
 * (in core/Model.kt). Per spec ("Do not invent a new state type"), we
 * use `WorkoutData` directly — that's the existing state class that has
 * the workout log shape. See CHANGES.md for the rationale.
 */
object DifficultyGate {

    /** Number of sessions required at the lower tier before the next tier unlocks. */
    const val UNLOCK_THRESHOLD = 3

    /**
     * Result of an unlock check.
     *
     * @property unlocked       true iff the exercise may be prescribed / shown.
     * @property reason         human-readable explanation (renders on the card).
     * @property blockingMuscle the primaryMuscle that failed the threshold first
     *                          (null when unlocked or no taxonomy).
     * @property progress       (current, required) session count for the blocking
     *                          muscle. (0, 0) when unlocked or no taxonomy.
     */
    data class UnlockResult(
        val unlocked: Boolean,
        val reason: String,
        val blockingMuscle: String?,
        val progress: Pair<Int, Int>,
    )

    /**
     * Check whether the given exercise is unlocked for the given user state.
     *
     * Per-muscle (primaryMuscles), per-difficulty gate. Never throws. If the
     * exercise has no taxonomy entry, returns unlocked=true with the reason
     * "No taxonomy; unlocked by default."
     */
    fun isUnlocked(
        exerciseId: String,
        state: WorkoutData,
    ): UnlockResult {
        val taxonomy = Library.taxonomyFor(exerciseId)
            ?: return UnlockResult(
                unlocked = true,
                reason = "No taxonomy; unlocked by default.",
                blockingMuscle = null,
                progress = 0 to 0,
            )

        val difficulty = taxonomy.difficulty
        // Easy exercises are always unlocked.
        if (difficulty == "easy") {
            return UnlockResult(
                unlocked = true,
                reason = "Easy — always unlocked.",
                blockingMuscle = null,
                progress = 0 to 0,
            )
        }

        // Medium requires N easy sessions for each primaryMuscle.
        // Hard   requires N medium sessions for each primaryMuscle.
        val requiredDifficulty = when (difficulty) {
            "medium" -> "easy"
            "hard"   -> "medium"
            else     -> return UnlockResult(
                unlocked = true,
                reason = "Unknown difficulty '$difficulty'; unlocked by default.",
                blockingMuscle = null,
                progress = 0 to 0,
            )
        }
        val required = UNLOCK_THRESHOLD

        // If the exercise has no primaryMuscles, there's nothing to gate on.
        // Unlock by default.
        if (taxonomy.primaryMuscles.isEmpty()) {
            return UnlockResult(
                unlocked = true,
                reason = "No primary muscles in taxonomy; unlocked by default.",
                blockingMuscle = null,
                progress = 0 to 0,
            )
        }

        var blockingMuscle: String? = null
        var minCurrent = required  // start high; we want the worst case
        for (m in taxonomy.primaryMuscles) {
            val current = countSessionsForMuscle(m, requiredDifficulty, state)
            if (current < required) {
                if (blockingMuscle == null) blockingMuscle = m
                if (current < minCurrent) minCurrent = current
            }
        }

        val unlocked = blockingMuscle == null
        val reason = if (unlocked) {
            "Unlocked — $requiredDifficulty sessions satisfied for all primary muscles."
        } else {
            "Need $required $requiredDifficulty sessions for $blockingMuscle " +
                "($minCurrent/$required)."
        }
        return UnlockResult(
            unlocked = unlocked,
            reason = reason,
            blockingMuscle = blockingMuscle,
            progress = minCurrent to required,
        )
    }

    /**
     * Count "sessions for muscle m" in the user's state.
     *
     * A session is a `HistoryEntry` belonging to an exercise whose
     * taxonomy.primaryMuscles contains m, with at least one completed set
     * (entry.sets >= 1) and not skipped (entry.skipped == false).
     *
     * Only exercises whose taxonomy.difficulty == [requiredDifficulty]
     * contribute — those are the sessions that count toward the next
     * tier's unlock.
     */
    private fun countSessionsForMuscle(
        muscle: String,
        requiredDifficulty: String,
        state: WorkoutData,
    ): Int {
        var count = 0
        state.exercises.forEach { (exId, record) ->
            val tax = Library.taxonomyFor(exId) ?: return@forEach
            if (tax.difficulty != requiredDifficulty) return@forEach
            if (muscle !in tax.primaryMuscles) return@forEach
            count += record.history.count { entry ->
                !entry.skipped && entry.sets >= 1
            }
        }
        return count
    }
}
