package com.peakform.fitness.core

import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.core.state.StateModel
import com.peakform.fitness.engine.Library

/**
 * BATCH-39f — The field's read interface.
 *
 * Source: source/docs/system/07-the-field.md § "Read interface" and
 * source/docs/system/12-the-coachs-read-loop.md § "Readiness gating".
 *
 * Pure functions over [ProState.field]. No state, no side effects, no writes.
 * Every coach reads the field through this object — never through ProState
 * directly — so the read side stays auditable and the write side (v39b's
 * accumulateField + v39e's decayField) stays isolated.
 *
 * The field is the shared substrate where every session writes stimulus and
 * every coach reads readiness. This object is the second half of that
 * contract — the read half. v39b shipped the write half, v39e shipped the
 * decay, this batch ships the read.
 *
 * All values are 0.0..1.0 except [recentStimulus] which is in arbitrary
 * stimulus units (the same units [ProState.accumulateField] deposits).
 *
 * The active profile's field is read from [ProState.field]. Callers are
 * responsible for ensuring decay has run (via [ProState.decayField]) before
 * reading if they want a fresh value — the generator does this at the top
 * of `performGenerateWorkout`, and the card reads through this object
 * without re-decaying (the generator already did it).
 *
 * Read functions:
 *   - [readiness] (muscle)        → 1 - (current / peak), or 1.0 when peak == 0
 *   - [readinessForComponent]     → same, for components
 *   - [slotFor]                   → raw FieldSlot for a muscle, or null
 *   - [componentSlot]             → raw FieldSlot for a component, or null
 *   - [recentStimulus]            → sum of session-deposited stimulus in last N hours
 *   - [isRecovered]               → readiness(muscle) >= 0.85
 *   - [muscleSummary]             → readiness per muscle id in the taxonomy
 *   - [componentSummary]          → readiness per component short name
 *
 * Notes on overloads:
 *   - [readiness] takes a muscle id String (e.g. "quads", "rear_delts").
 *   - [readinessForComponent] takes a component short name (e.g. "Strength").
 *   These were originally specced as two `readiness(String)` overloads, but
 *   Kotlin forbids same-signature overloads even with `@JvmName`. Splitting
 *   the names keeps the call sites explicit and unambiguous.
 */
object Field {

    /** Recovery threshold — muscles at or above this are considered recovered. */
    const val RECOVERED_THRESHOLD = 0.85

    /** Readiness bucket thresholds (spec PART 2). */
    const val TIER_FULL = 0.85
    const val TIER_MODERATE_HIGH = 0.60
    const val TIER_MODERATE_LOW = 0.40

    /**
     * Readiness for a muscle id from the field.
     *
     *     readiness = 1.0 - (current / peak)   when peak > 0
     *     readiness = 1.0                      when peak == 0 (never trained → fully ready)
     *
     * Result is clamped to [0.0, 1.0]. A muscle that was just trained has
     * current == peak → readiness == 0.0. As the field decays (v39e) and
     * current drops toward 0, readiness climbs toward 1.0.
     *
     * @param muscleId one of the muscle ids from `muscles.json` (e.g. "quads", "rear_delts")
     */
    fun readiness(muscleId: String): Double {
        val slot = ProState.field.muscles[muscleId] ?: return 1.0
        return slotReadiness(slot)
    }

    /**
     * Readiness for a component short name (e.g. "Strength", "Endurance").
     *
     * Same formula as [readiness] but reads from [ProState.field.components]
     * instead of `.muscles`. The component key is the `short` field from
     * `mp_components.json` — e.g. "Strength", "Endurance", "Cardio".
     *
     * Components use a fixed 24h decay (no per-component curve), so the
     * recovery timeline is the same across the 11 components — only the
     * `peak`/`current` ratio varies by how much stimulus was deposited.
     */
    fun readinessForComponent(componentName: String): Double {
        val slot = ProState.field.components[componentName] ?: return 1.0
        return slotReadiness(slot)
    }

    /** Common slot → readiness formula. Returns 1.0 when peak == 0. */
    private fun slotReadiness(slot: StateModel.FieldSlot): Double {
        if (slot.peak <= 0.0) return 1.0
        val ratio = (slot.current / slot.peak).coerceIn(0.0, 1.0)
        return (1.0 - ratio).coerceIn(0.0, 1.0)
    }

    /**
     * Raw slot for a muscle id, or null if the muscle was never written.
     *
     * Returns the [StateModel.FieldSlot] (current, peak, lastUpdate) directly.
     * Coaches that need the raw numbers (e.g. for stimulus-bookkeeping) read
     * through this. Coaches that just need a 0..1 readiness use [readiness].
     */
    fun slotFor(muscleId: String): StateModel.FieldSlot? = ProState.field.muscles[muscleId]

    /** Raw slot for a component short name, or null if never written. */
    fun componentSlot(componentName: String): StateModel.FieldSlot? = ProState.field.components[componentName]

    /**
     * Whether a muscle is recovered enough to train hard.
     *
     * Returns true when [readiness] >= [RECOVERED_THRESHOLD] (0.85). The
     * generator's per-muscle loop checks this indirectly through
     * `Fatigue.getReadyMuscles`; this function exposes the same check to
     * the coach layer for the per-exercise readiness gating (spec PART 2).
     */
    fun isRecovered(muscleId: String): Boolean = readiness(muscleId) >= RECOVERED_THRESHOLD

    /**
     * Sum of the stimulus deposited to a muscle in the last [hours] hours.
     *
     * Reads from [ProState.sessions] — the ground-truth append-only log —
     * not from [ProState.field.current] (which has already decayed). For
     * each session in the window, for each exercise in that session whose
     * dossier's stimulusProfile mentions [muscleId], multiply the set's
     * volume by the dossier's weight for that muscle, and sum.
     *
     * Volume per set matches the [ProState.accumulateField] convention:
     *   - if `durationSeconds > 0`: `durationSeconds / 30.0`
     *   - else: `reps.toDouble()`
     * Skipped and incomplete sets are excluded.
     *
     * Returns 0.0 when there are no sessions in the window OR no dossier
     * stimulus profile maps to this muscle. The unit is arbitrary — same
     * as [ProState.accumulateField]'s deposit unit.
     *
     * @param muscleId the muscle to sum stimulus for
     * @param hours    the lookback window in hours (e.g. 24, 48, 72)
     */
    fun recentStimulus(muscleId: String, hours: Int): Double {
        if (hours <= 0) return 0.0
        val now = System.currentTimeMillis()
        val cutoff = now - hours * 3_600_000L
        var total = 0.0
        for (session in ProState.sessions) {
            if (session.timestamp < cutoff) continue
            for (ex in session.exercises) {
                if (ex.skipped) continue
                val dossier = DossierRegistry.forExercise(ex.exerciseId) ?: continue
                val weight = dossier.stimulusProfile[muscleId] ?: 0.0
                if (weight == 0.0) continue
                for (set in ex.sets) {
                    if (!set.completed) continue
                    val volume = if (set.durationSeconds != null && set.durationSeconds > 0.0) {
                        set.durationSeconds / 30.0
                    } else {
                        set.reps.toDouble()
                    }
                    if (volume <= 0.0) continue
                    total += volume * weight
                }
            }
        }
        return total
    }

    /**
     * Readiness value for every muscle id in the taxonomy.
     *
     * Returns a map from muscle id (e.g. "quads", "chest") to its current
     * readiness value. Muscles not in the field default to 1.0 (never
     * trained → fully ready). The key set matches [Library.allMuscleGroups]
     * — the same set the generator iterates when building targetMuscles.
     *
     * Useful for the dashboard / radar view (a future batch) and for tests
     * that need to assert the field's state across the whole taxonomy.
     */
    fun muscleSummary(): Map<String, Double> {
        val out = HashMap<String, Double>()
        for (def in Library.allMuscleGroups()) {
            out[def.name] = readiness(def.name)
        }
        return out
    }

    /**
     * Readiness value for every component short name.
     *
     * Returns a map from component `short` name (e.g. "Strength", "Endurance")
     * to its current readiness. The key set matches [Library.mpComponents] —
     * the 11 fitness components from `mp_components.json`.
     */
    fun componentSummary(): Map<String, Double> {
        val out = HashMap<String, Double>()
        for (comp in Library.mpComponents) {
            out[comp.short] = readinessForComponent(comp.short)
        }
        return out
    }
}
