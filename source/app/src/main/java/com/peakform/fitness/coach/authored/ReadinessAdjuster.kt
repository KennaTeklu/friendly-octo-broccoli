package com.peakform.fitness.coach.authored

import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.core.Field

/**
 * BATCH-39f — Field-read adjustment helpers for authored coaches.
 *
 * Source: BATCH 39f spec PART 2 — "The coach reads it."
 *
 * Each authored coach's [com.peakform.fitness.coach.ExerciseCoach.adjustForReadiness]
 * override delegates to one of these helpers based on the coach's
 * [CoachRequirements]. The helpers are pure — they read [Field] and
 * return an adjusted [Prescription], no side effects.
 *
 * The shared logic:
 *
 *   1. Look up the exercise's dossier via [DossierRegistry.forExercise].
 *   2. For each muscle in `dossier.stimulusProfile`, read [Field.readiness].
 *   3. Take the minimum readiness across those muscles — call it `readiness`.
 *   4. Apply the threshold table (spec PART 2 § 4-7):
 *
 *      | readiness | effect                                              |
 *      |-----------|-----------------------------------------------------|
 *      | >= 0.85   | base unchanged                                      |
 *      | [0.60, 0.85) | -1 set, note: "Reduced 1 set — X at Y% recovery." |
 *      | [0.40, 0.60) | -2 sets, -10% load (or -10% duration), note appended |
 *      | <  0.40   | empty prescription, note: "X still recovering — rest today." |
 *
 *   The least-ready muscle's display name (from the dossier's
 *   `stimulusProfile` keys, fall back to the raw id) and its percentage
 *   are interpolated into the note.
 *
 * Per-mode helpers:
 *
 *   - [adjustCompound]     — needs1RM + needsWeight → reduce sets AND load
 *   - [adjustBodyweight]   — needsBodyweight → reduce sets only (no load to reduce)
 *   - [adjustBanded]       — needsBand → reduce sets only (band colour is the load, not a number)
 *   - [adjustTimeBased]    — needsDuration (no needsDistance) → reduce sets + duration
 *   - [adjustCardio]       — needsDistance + needsDuration → reduce duration only
 *
 * Per the spec: "Bodyweight and banded coaches skip load reduction — they
 * only reduce sets." and "Time-based and cardio coaches reduce duration by
 * the same percentages." So:
 *
 *   - bodyweight / banded: never touch `load` or `pct1RM`
 *   - compound:            drop `load` by 10% in the [0.40, 0.60) tier
 *   - time-based:          drop `duration` by 10% in the [0.40, 0.60) tier
 *   - cardio:              drop `duration` by 10% in the [0.40, 0.60) tier (no sets to drop)
 *
 * When the readiness is < 0.40, every mode returns an empty Prescription
 * (sets=0, reps="", load=null, duration=null) with the rest-today note.
 */
object ReadinessAdjuster {

    /** Threshold for "fully recovered — leave unchanged". */
    private const val TIER_FULL = 0.85

    /** Threshold for "moderate recovery — drop one set". */
    private const val TIER_MODERATE_HIGH = 0.60

    /** Threshold for "low recovery — drop two sets + 10% load/duration". */
    private const val TIER_MODERATE_LOW = 0.40

    /**
     * Find the least-ready muscle for [exerciseId] and return the minimum
     * readiness value plus the muscle's display name.
     *
     * Returns `Triple(1.0, null, null)` when:
     *   - the dossier is missing
     *   - the stimulusProfile is empty
     *   - no muscle in the profile has a non-null readiness
     *
     * In those cases the adjustment is a no-op (readiness 1.0 → base unchanged).
     *
     * The display name is the muscle id with `_` replaced by space and the
     * first letter capitalized (e.g. "rear_delts" → "Rear delts"). The
     * dossier's `stimulusProfile` keys are muscle ids, not display names —
     * the conversion is local to this helper.
     */
    private fun leastReadyMuscle(exerciseId: String): Triple<Double, String?, Int?> {
        val dossier = DossierRegistry.forExercise(exerciseId) ?: return Triple(1.0, null, null)
        val sp = dossier.stimulusProfile
        if (sp.isEmpty()) return Triple(1.0, null, null)
        var minReadiness = 1.0
        var minMuscle: String? = null
        for (muscleId in sp.keys) {
            val r = Field.readiness(muscleId)
            if (r < minReadiness) {
                minReadiness = r
                minMuscle = muscleId
            }
        }
        if (minMuscle == null) return Triple(1.0, null, null)
        val display = minMuscle.replace('_', ' ').replaceFirstChar { it.uppercase() }
        val pct = (minReadiness * 100).toInt()
        return Triple(minReadiness, display, pct)
    }

    /** Format the readiness percentage for notes. */
    private fun pctStr(pct: Int?): String = "${pct ?: 0}%"

    /**
     * Compound adjustment — reduce sets AND load.
     *
     * Used by coaches with `needs1RM = true` AND `needsWeight = true`
     * (BackSquat, Deadlift, BenchPress, OverheadPress, BarbellRow,
     * FrontSquat, RomanianDeadlift, SumoDeadlift, InclineBenchPress,
     * CloseGripBenchPress, PendlayRow, PushPress, PowerClean).
     *
     * In the [0.40, 0.60) tier, drops `load` (absolute weight) by 10%
     * AND `pct1RM` (percentage of 1RM) by 10% — both are forms of
     * "load" per the spec. A prescription may have either, both, or
     * neither field set; only the non-null fields are touched.
     */
    fun adjustCompound(base: Prescription, exerciseId: String): Prescription {
        val (readiness, muscle, pct) = leastReadyMuscle(exerciseId)
        if (muscle == null) return base
        return when {
            readiness >= TIER_FULL -> base
            readiness >= TIER_MODERATE_HIGH -> base.copy(
                sets = (base.sets - 1).coerceAtLeast(1),
                note = appendNote(base.note, "Reduced 1 set — $muscle at ${pctStr(pct)} recovery."),
            )
            readiness >= TIER_MODERATE_LOW -> {
                // Reduce both `load` (absolute weight) and `pct1RM`
                // (percentage of 1RM) by 10% — both are forms of "load"
                // per the spec. A prescription may have either, both,
                // or neither; only the non-null fields are touched.
                val newLoad = base.load?.times(0.9)
                val newPct1RM = base.pct1RM?.times(0.9)
                base.copy(
                    sets = (base.sets - 2).coerceAtLeast(1),
                    load = newLoad,
                    pct1RM = newPct1RM,
                    note = appendNote(base.note, "Reduced 2 sets, -10% load — $muscle at ${pctStr(pct)}."),
                )
            }
            else -> Prescription(
                sets = 0,
                reps = "",
                load = null,
                pct1RM = null,
                duration = null,
                note = "$muscle still recovering — rest today.",
            )
        }
    }

    /**
     * Bodyweight adjustment — reduce sets only.
     *
     * Used by coaches with `needsBodyweight = true` (PullUp, ChinUp, Dip,
     * PushUp, InvertedRow, ArcherPullUp, LSitPullUp).
     *
     * The "load" is the user's bodyweight — we don't reduce it. Only the
     * set count is adjusted.
     */
    fun adjustBodyweight(base: Prescription, exerciseId: String): Prescription {
        val (readiness, muscle, pct) = leastReadyMuscle(exerciseId)
        if (muscle == null) return base
        return when {
            readiness >= TIER_FULL -> base
            readiness >= TIER_MODERATE_HIGH -> base.copy(
                sets = (base.sets - 1).coerceAtLeast(1),
                note = appendNote(base.note, "Reduced 1 set — $muscle at ${pctStr(pct)} recovery."),
            )
            readiness >= TIER_MODERATE_LOW -> base.copy(
                sets = (base.sets - 2).coerceAtLeast(1),
                note = appendNote(base.note, "Reduced 2 sets — $muscle at ${pctStr(pct)}."),
            )
            else -> Prescription(
                sets = 0,
                reps = "",
                load = null,
                pct1RM = null,
                duration = null,
                note = "$muscle still recovering — rest today.",
            )
        }
    }

    /**
     * Banded adjustment — reduce sets only.
     *
     * Used by coaches with `needsBand = true` (BandedLateralRaise, FacePull,
     * BandedFacePull, BandedPullApart, BandedSquat, BandedRow,
     * BandedGluteBridge, BandedChestPress).
     *
     * The "load" is the band colour — a categorical, not a number. We
     * don't reduce it. Only the set count is adjusted.
     */
    fun adjustBanded(base: Prescription, exerciseId: String): Prescription {
        val (readiness, muscle, pct) = leastReadyMuscle(exerciseId)
        if (muscle == null) return base
        return when {
            readiness >= TIER_FULL -> base
            readiness >= TIER_MODERATE_HIGH -> base.copy(
                sets = (base.sets - 1).coerceAtLeast(1),
                note = appendNote(base.note, "Reduced 1 set — $muscle at ${pctStr(pct)} recovery."),
            )
            readiness >= TIER_MODERATE_LOW -> base.copy(
                sets = (base.sets - 2).coerceAtLeast(1),
                note = appendNote(base.note, "Reduced 2 sets — $muscle at ${pctStr(pct)}."),
            )
            else -> Prescription(
                sets = 0,
                reps = "",
                load = null,
                pct1RM = null,
                duration = null,
                note = "$muscle still recovering — rest today.",
            )
        }
    }

    /**
     * Time-based adjustment — reduce sets + duration.
     *
     * Used by coaches with `needsDuration = true` AND `needsDistance = false`
     * (Plank, SidePlank).
     *
     * The duration is the hold time in seconds. In the [0.40, 0.60) tier,
     * drop it by 10%. Sets are also reduced (a 3-set hold becomes 1 set
     * in the lowest tier before the empty-prescription tier).
     */
    fun adjustTimeBased(base: Prescription, exerciseId: String): Prescription {
        val (readiness, muscle, pct) = leastReadyMuscle(exerciseId)
        if (muscle == null) return base
        return when {
            readiness >= TIER_FULL -> base
            readiness >= TIER_MODERATE_HIGH -> base.copy(
                sets = (base.sets - 1).coerceAtLeast(1),
                note = appendNote(base.note, "Reduced 1 set — $muscle at ${pctStr(pct)} recovery."),
            )
            readiness >= TIER_MODERATE_LOW -> base.copy(
                sets = (base.sets - 2).coerceAtLeast(1),
                duration = base.duration?.times(0.9),
                note = appendNote(base.note, "Reduced 2 sets, -10% duration — $muscle at ${pctStr(pct)}."),
            )
            else -> Prescription(
                sets = 0,
                reps = "",
                load = null,
                pct1RM = null,
                duration = null,
                note = "$muscle still recovering — rest today.",
            )
        }
    }

    /**
     * Cardio adjustment — reduce duration only.
     *
     * Used by coaches with `needsDistance = true` (Running). Cardio
     * sessions don't have sets in the same sense — the prescription is
     * one continuous effort. The duration is reduced by 10% in the
     * [0.40, 0.60) tier, and the prescription becomes a rest note in
     * the lowest tier.
     */
    fun adjustCardio(base: Prescription, exerciseId: String): Prescription {
        val (readiness, muscle, pct) = leastReadyMuscle(exerciseId)
        if (muscle == null) return base
        return when {
            readiness >= TIER_FULL -> base
            readiness >= TIER_MODERATE_HIGH -> base.copy(
                duration = base.duration?.times(0.9),
                note = appendNote(base.note, "Reduced duration — $muscle at ${pctStr(pct)} recovery."),
            )
            readiness >= TIER_MODERATE_LOW -> base.copy(
                duration = base.duration?.times(0.8),
                distance = base.distance?.times(0.9),
                note = appendNote(base.note, "Reduced 20% duration — $muscle at ${pctStr(pct)}."),
            )
            else -> Prescription(
                sets = 0,
                reps = "",
                load = null,
                pct1RM = null,
                duration = null,
                distance = null,
                note = "$muscle still recovering — rest today.",
            )
        }
    }

    /**
     * Append [addition] to [existing], joining with a space when both
     * parts are non-blank. Returns [addition] alone when [existing] is
     * blank, [existing] alone when [addition] is blank.
     */
    private fun appendNote(existing: String, addition: String): String {
        if (addition.isBlank()) return existing
        if (existing.isBlank()) return addition
        return "$existing $addition"
    }
}
