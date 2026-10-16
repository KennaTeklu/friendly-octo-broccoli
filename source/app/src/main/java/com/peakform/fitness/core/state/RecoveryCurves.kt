package com.peakform.fitness.core.state

import com.peakform.fitness.ProLog

/**
 * BATCH-39e — Authored default recovery times per muscle bucket.
 *
 * Source: source/docs/system/09-recovery-curves.md.
 *
 *   compound       48h   (squat, deadlift, bench, OHP)
 *   accessory      24h   (curls, calf raises, etc.)
 *   isometric      24h   (planks, holds)
 *   cardio         12h   (running, cycling)
 *   mobility        6h   (stretches)
 *
 * The spec lists 6 concrete buckets of muscle ids. The 5 categories above
 * collapse to these 6 buckets:
 *
 *   compound:     quads, hamstrings, glutes, chest, back_lats,
 *                 rhomboids_reardelts, traps, shoulders_anterior,
 *                 shoulders_lateral                  → 48h
 *   accessory:    biceps, triceps, forearms, calves, adductors,
 *                 abductors, neck                     → 24h
 *   core:         core_abs                             → 24h  (isometric)
 *   mobility:    stretching_static, mobility_dynamic  →  6h
 *   extremities: feet_ankles, hands_grip               → 24h  (small muscles,
 *                                                                accessory-like)
 *   athletic:    athletic_power, athletic_speed,
 *                athletic_balance, athletic_coordination,
 *                cardio_condition                      → 12h  (cardio-like)
 *
 * For muscle ids not in any bucket (e.g. the long-tail dossier muscle ids
 * like `infraspinatus`, `tibialis`, `serratus`), default to 48h and log a
 * warning so the missing bucket can be authored later. The 48h default is
 * conservative — it errs on the side of slower recovery, so the field
 * decays more slowly and the muscle looks less ready than it might be.
 *
 * Components get a fixed 24h curve (no authored distinction per the spec).
 */
object RecoveryCurves {

    private const val TAG = "RECOVERY"

    /** Time constant for compound-family muscles (squat, deadlift, bench, OHP). */
    const val COMPOUND_H = 48.0

    /** Time constant for accessory / isolation movements (curls, calf raises). */
    const val ACCESSORY_H = 24.0

    /** Time constant for isometric holds (planks). */
    const val ISOMETRIC_H = 24.0

    /** Time constant for cardio movements (running, cycling). */
    const val CARDIO_H = 12.0

    /** Time constant for mobility / stretching. */
    const val MOBILITY_H = 6.0

    /** Time constant for extremities (feet, hands) — accessory-like. */
    const val EXTREMITIES_H = 24.0

    /** Time constant for athletic / cardio-conditioning movements. */
    const val ATHLETIC_H = 12.0

    /** Fallback time constant for muscle ids not in any bucket. */
    const val DEFAULT_H = 48.0

    /** Fixed time constant for the 11 components (Strength, Endurance, …).
     *  No authored distinction per the spec. */
    const val COMPONENT_H = 24.0

    /** Compound-family muscle ids (48h). Spec list. */
    val COMPOUND_MUSCLES: Set<String> = setOf(
        "quads", "hamstrings", "glutes", "chest",
        "back_lats", "rhomboids_reardelts", "traps",
        "shoulders_anterior", "shoulders_lateral",
    )

    /** Accessory / isolation muscle ids (24h). Spec list. */
    val ACCESSORY_MUSCLES: Set<String> = setOf(
        "biceps", "triceps", "forearms", "calves",
        "adductors", "abductors", "neck",
    )

    /** Core muscle id (24h, isometric). Spec list. */
    val CORE_MUSCLES: Set<String> = setOf(
        "core_abs",
    )

    /** Mobility muscle ids (6h). Spec list. */
    val MOBILITY_MUSCLES: Set<String> = setOf(
        "stretching_static", "mobility_dynamic",
    )

    /** Extremities muscle ids (24h, accessory-like). Spec list. */
    val EXTREMITIES_MUSCLES: Set<String> = setOf(
        "feet_ankles", "hands_grip",
    )

    /** Athletic muscle ids (12h, cardio-like). Spec list. */
    val ATHLETIC_MUSCLES: Set<String> = setOf(
        "athletic_power", "athletic_speed",
        "athletic_balance", "athletic_coordination",
        "cardio_condition",
    )

    /** All muscle ids covered by an authored bucket. */
    val ALL_BUCKETED: Set<String> = (
        COMPOUND_MUSCLES + ACCESSORY_MUSCLES + CORE_MUSCLES +
            MOBILITY_MUSCLES + EXTREMITIES_MUSCLES + ATHLETIC_MUSCLES
    )

    /**
     * Authored default recovery time for a muscle id. Returns 48h with a
     * warning for ids not in any bucket — the warning surfaces missing
     * buckets so they can be authored later. Idempotent — repeats the
     * same warning each call, so callers in hot paths should cache.
     */
    fun defaultHours(muscleId: String): Double = when (muscleId) {
        in COMPOUND_MUSCLES -> COMPOUND_H
        in ACCESSORY_MUSCLES -> ACCESSORY_H
        in CORE_MUSCLES -> ISOMETRIC_H
        in MOBILITY_MUSCLES -> MOBILITY_H
        in EXTREMITIES_MUSCLES -> EXTREMITIES_H
        in ATHLETIC_MUSCLES -> ATHLETIC_H
        else -> {
            if (muscleId.isNotBlank()) {
                ProLog.w(TAG, "no authored recovery bucket for muscleId=$muscleId — using default $DEFAULT_H h")
            }
            DEFAULT_H
        }
    }

    /** Authored default recovery time for a component name (always 24h). */
    fun defaultHoursForComponent(componentName: String): Double = COMPONENT_H

    /**
     * Build a default RecoveryCurve for a muscle id. The curve is marked
     * `source = "default"` with `samples = 0` so v39e-2's learning pass
     * can override it once it has 3+ sessions on that muscle.
     */
    fun defaultCurve(muscleId: String): StateModel.RecoveryCurve =
        StateModel.RecoveryCurve(
            muscleId = muscleId,
            recoveryHours = defaultHours(muscleId),
            source = "default",
            samples = 0,
        )

    /** Build a default RecoveryCurve for a component name (24h). */
    fun defaultCurveForComponent(componentName: String): StateModel.RecoveryCurve =
        StateModel.RecoveryCurve(
            muscleId = componentName,
            recoveryHours = COMPONENT_H,
            source = "default",
            samples = 0,
        )
}
