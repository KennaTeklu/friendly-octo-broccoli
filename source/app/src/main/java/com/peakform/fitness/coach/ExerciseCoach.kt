package com.peakform.fitness.coach

import com.peakform.fitness.core.CoachingEngine

/**
 * ExerciseCoach — per-exercise rules engine interface.
 *
 * BATCH-18: every exercise has a coach that knows the movement's specific
 * progression curve, deload triggers, common form mistakes, and the
 * "coach voice" for that movement.
 *
 * Concrete coaches live in `coach/exercises/AllCoaches.kt` — 29 specific
 * coaches (one per major movement) plus 8 generic coaches that handle the
 * long tail (by muscle group / equipment).
 */
interface ExerciseCoach {
    /** The exercise id(s) this coach handles. */
    val exerciseIds: Set<String>

    /** Friendly movement name (e.g. "Barbell Back Squat"). */
    val movementName: String

    /** Short coach-voice one-liner (rendered above the prescription). */
    fun message(input: CoachingEngine.CoachInput): String

    /** Per-exercise progression factor (default 1.0 — bump sets/reps only). */
    fun progressionFactor(input: CoachingEngine.CoachInput): Double = 1.0

    /** Per-exercise deload trigger — return true to force a 10% load drop. */
    fun deloadTrigger(input: CoachingEngine.CoachInput): Boolean = false

    /** Common form cues (rendered in the workout card on long-press). */
    val formCues: List<String>
}
