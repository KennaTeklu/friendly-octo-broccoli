package com.peakform.fitness.core.state

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** Training intent — what the user wants from this exercise/session. */
enum class Intent {
    STRONGER, BIGGER, ENDURE, POWER, MOBILE, SKILL, BALANCE, AGILE, FAST, COMP, LONGEVITY, MAINTAIN
}

/** Arc state — where in the training cycle the user is. */
enum class ArcState {
    BASE, BUILD, PEAK, DELOAD, MAINTAIN, COMEBACK, INTERRUPTED
}

/**
 * StateModel — the formal state model for the engine.
 *
 * BATCH-31: every input the engine consumes and every output it produces
 * is modelled here so the prescription path is testable end-to-end without
 * touching Room or Compose.
 *
 * Hierarchy:
 *   Event           — user-initiated stimulus (logged a set, switched profile, etc.)
 *   ProfileStateTree — the per-profile state tree (workouts + exercises + user + draft + prefs)
 *   StateFold       — pure reducer: (tree, event) → tree
 *   Migrations      — version-tagged state migrations (one-shot, idempotent)
 *   ExportContract  — the round-trip contract every backup file must satisfy
 *   StimulusField   — typed wrapper for the inputs to the engines (Batch 31)
 */
object StateModel {

    /** User-initiated events that drive state changes. */
    @Serializable
    sealed class Event {
        @Serializable data class LoggedSet(val exerciseId: String, val weight: Double, val reps: Int, val rpe: Double?) : Event()
        @Serializable data class SkippedSet(val exerciseId: String) : Event()
        @Serializable data class SwitchedProfile(val profileId: String) : Event()
        @Serializable data class StartedWorkout(val sessionId: String, val date: String) : Event()
        @Serializable data class CompletedWorkout(val sessionId: String, val atIso: String) : Event()
        @Serializable data class UpdatedUser(val name: String? = null, val weight: Double? = null, val goal: String? = null) : Event()
        @Serializable data class ResetData(val reason: String) : Event()
        @Serializable data class ImportedBundle(val bundle: JsonObject) : Event()
    }

    /** Per-profile state tree — pure data, no I/O. */
    @Serializable
    data class ProfileStateTree(
        val profileId: String,
        val workouts: List<String> = emptyList(),       // workout ids
        val exercises: Map<String, Double> = emptyMap(), // exerciseId → mu (estimated 1RM)
        val draftId: String? = null,
        val prefsVersion: Int = 31,
    )

    /** Pure reducer — applies an event to a tree, returns the new tree.
     *  Side-effect free. The engine writes the result via ProState. */
    object StateFold {
        fun reduce(tree: ProfileStateTree, event: Event): ProfileStateTree = when (event) {
            is Event.LoggedSet -> {
                val newMu = tree.exercises[event.exerciseId]?.let {
                    if (it > 0) it * 1.005 else event.weight * (1.0 + event.reps / 30.0)
                } ?: event.weight * (1.0 + event.reps / 30.0)
                tree.copy(exercises = tree.exercises + (event.exerciseId to newMu))
            }
            is Event.SkippedSet -> tree
            is Event.SwitchedProfile -> tree.copy(profileId = event.profileId, workouts = emptyList(), exercises = emptyMap(), draftId = null)
            is Event.StartedWorkout -> tree.copy(draftId = event.sessionId)
            is Event.CompletedWorkout -> tree.copy(draftId = null, workouts = tree.workouts + event.sessionId)
            is Event.UpdatedUser -> tree
            is Event.ResetData -> ProfileStateTree(profileId = tree.profileId)
            is Event.ImportedBundle -> tree  // bundle import handled at the store layer
        }
    }

    /** Version-tagged state migrations (one-shot, idempotent). */
    @Serializable
    data class Migration(
        val fromVersion: Int,
        val toVersion: Int,
        val description: String,
    )

    object Migrations {
        val ALL: List<Migration> = listOf(
            Migration(18, 22, "per-profile meta → per-profile SharedPreferences"),
            Migration(22, 26, "global prefs → per-profile prefs (ProfileSettings)"),
            Migration(26, 31, "coach-led prescription overlay"),
        )

        /** Returns the migration path from `from` to `to`. Empty if none. */
        fun path(from: Int, to: Int): List<Migration> =
            ALL.filter { it.fromVersion >= from && it.toVersion <= to }
    }

    /** Export contract — every backup file must round-trip through this. */
    @Serializable
    data class ExportContract(
        val format: String,         // "PeakForm-Complete-Backup-v1"
        val createdAt: String,
        val schemaVersion: Int,
        val requiredKeys: List<String>,
    )

    /** Stimulus field — typed wrapper for engine inputs. */
    @Serializable
    data class StimulusField(
        val id: String,
        val label: String,
        val type: String,           // "number" | "boolean" | "string" | "enum"
        val default: String,
        val range: Pair<String, String>? = null,
        val options: List<String>? = null,
    )

    /** Every stimulus field the engines read. */
    val STIMULUS_FIELDS: List<StimulusField> = listOf(
        StimulusField("aggression", "Aggression dial", "number", "1.0", "0.6" to "1.4"),
        StimulusField("fatigue", "Current fatigue", "number", "0.0", "0.0" to "1.0"),
        StimulusField("goal", "Training goal", "enum", "balanced", null, listOf("balanced", "hypertrophy", "strength", "endurance", "powerlifting")),
        StimulusField("streakDays", "Current streak", "number", "0", "0" to "365"),
        StimulusField("recoveryDays", "Days since muscle trained", "number", "2", "0" to "30"),
        StimulusField("holdBack", "Hold-back flag", "boolean", "false"),
        StimulusField("tuneTarget", "Tune target flag", "boolean", "false"),
        StimulusField("periodPhase", "Period phase", "enum", "none", null, listOf("none", "follicular", "ovulation", "luteal", "menstrual")),
    )

    // ---- BATCH-39a: Session state schema (source/docs/system/08-session-state.md) ----

    /** One logged unit of work. A set of exercises performed by one user at one time. */
    @Serializable
    data class SessionRecord(
        val id: String,
        val userId: String,
        val timestamp: Long,
        val exercises: List<ExerciseRecord>,
        val overallFelt: String? = null,      // "easy", "solid", "tough"
        val notes: String? = null,
    )

    /** Per-exercise record within a session. */
    @Serializable
    data class ExerciseRecord(
        val exerciseId: String,
        val sets: List<SetRecord>,
        val skipped: Boolean = false,
        val goalId: String? = null,           // which goal was active
        val felt: String? = null,
    )

    /** One completed set within an exercise record. */
    @Serializable
    data class SetRecord(
        val reps: Int,
        val load: Double = 0.0,               // 0 for bodyweight
        val tempo: String? = null,            // "3-1-2", "explosive", etc.
        val rpe: Double? = null,              // 1-10
        val durationSeconds: Double? = null,  // for holds
        val completed: Boolean = true,
    )

    // ---- BATCH-39b: The field (source/docs/system/07-the-field.md) ----
    //
    // The shared substrate. Where every session writes. Where every coach
    // will read (v39c+). One FieldState per profile, stored under
    // ProStore key `p9_field_<profileId>` (ProPrefs-backed, same pattern
    // as v39a sessions).
    //
    // Muscle keys are the ids from exercise_taxonomy.json (chest, quads,
    // lats, triceps, etc.). Component keys are the 11 short names from
    // mp_components.json (Strength, Endurance, Cardio, Flexibility,
    // Body Comp, Power, Speed, Agility, Balance, Coordination, Reaction).
    //
    // This batch is write-only and monotonic. Decay arrives in v39c.
    // Read interface (readinessFor, stimulusFor) arrives in v39d.

    /** One slot in the field — per (user, muscle) or (user, component). */
    @Serializable
    data class FieldSlot(
        val current: Double = 0.0,
        val peak: Double = 0.0,
        val lastUpdate: Long = 0L,
    )

    /** The field for one profile. Two maps: muscles and components. */
    @Serializable
    data class FieldState(
        val muscles: Map<String, FieldSlot> = emptyMap(),
        val components: Map<String, FieldSlot> = emptyMap(),
    )

    // ---- BATCH-39e: Recovery curves (source/docs/system/09-recovery-curves.md) ----
    //
    // Per-user, per-muscle time constant for the exponential decay that
    // shrinks field values back toward zero after a session. Defaults are
    // authored per muscle bucket (compound=48h, accessory=24h, …). The
    // schema has `samples` and `source` fields ready for v39e-2 to write
    // learned curves into — this batch ships defaults + decay only.
    //
    // Components get a fixed 24h curve (no authored distinction per the
    // spec). Stored alongside the field under ProPrefs key
    // `p9_recovery_<profileId>`.

    /** One recovery curve — per (user, muscle) or (user, component). */
    @Serializable
    data class RecoveryCurve(
        val muscleId: String,
        val recoveryHours: Double,   // time constant T in exp(-t/T)
        val source: String,          // "default" or "learned"
        val samples: Int = 0,        // count of sessions used to learn (0 for default)
    )

    /** Recovery state for one profile — one curve per muscle that has either
     *  a default or learned time constant. */
    @Serializable
    data class RecoveryState(
        val curves: Map<String, RecoveryCurve> = emptyMap(),
    )
}
