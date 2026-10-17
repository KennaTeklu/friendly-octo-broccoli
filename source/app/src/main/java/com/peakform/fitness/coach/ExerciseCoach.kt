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

    // ---- v34b additions: authored coach metadata ----

    /** What inputs this coach needs (1RM, band, bodyweight, etc.). */
    fun requirements(): com.peakform.fitness.coach.authored.CoachRequirements = com.peakform.fitness.coach.authored.CoachRequirements()

    /** Envelope bounds for this coach's prescriptions. */
    val envelope: com.peakform.fitness.coach.authored.ExerciseEnvelope
        get() = com.peakform.fitness.coach.authored.ExerciseEnvelope(7, 2, 1000.0, 300.0, 50.0, 0.95)

    /** Provenance: source file, section, coach version, author note. */
    val provenance: com.peakform.fitness.coach.authored.CoachProvenance
        get() = com.peakform.fitness.coach.authored.CoachProvenance("", "", 1, "")

    /** Coach version (starts at 1). */
    val version: Int
        get() = 0

    /** Reconcile authored rows into a prescription. Selects, never blends. */
    fun reconcile(rows: List<com.peakform.fitness.coach.authored.AuthoredRow>, intent: String, phase: String, bodyProfile: com.peakform.fitness.coach.authored.BodyProfile?): com.peakform.fitness.coach.authored.Prescription = com.peakform.fitness.coach.authored.Prescription(note = "Not authored — no rows available.")

    /** One-sentence explanation of the prescription in coach voice. */
    fun explain(prescription: com.peakform.fitness.coach.authored.Prescription, intent: String): String = prescription.note

    // ---- v39f additions: field-read adjustment pass ----

    /**
     * BATCH-39f — Field-read adjustment pass.
     *
     * Source: source/docs/system/12-the-coachs-read-loop.md § "Readiness gating"
     * and § "Response-rate adjustment".
     *
     * Called AFTER [reconcile] returns the base prescription. This is an
     * adjustment pass on top of the selected row — it does NOT change which
     * row was picked, only the numbers on it. The coach reads the field
     * via [com.peakform.fitness.core.Field] and decides whether to:
     *
     *   - leave the prescription unchanged (readiness >= 0.85)
     *   - drop a set (readiness in [0.60, 0.85))
     *   - drop two sets + 10% load (readiness in [0.40, 0.60))
     *   - return an empty prescription + rest note (readiness < 0.40)
     *
     * Default implementation: return [base] unchanged. Coaches that do not
     * override keep the pre-v39f behavior. Authored coaches that have a
     * working set already override this with a delegation to one of the
     * [com.peakform.fitness.coach.authored.ReadinessAdjuster] helpers
     * (compound, bodyweight, banded, time-based, cardio) based on the
     * coach's requirements (needs1RM+needsWeight → compound; needsBodyweight
     * → bodyweight; needsBand → banded; needsDuration → time-based;
     * needsDistance → cardio).
     *
     * The [exerciseId] parameter is the exercise the prescription applies
     * to. The coach's [exerciseIds] set may contain multiple exercises
     * (e.g. a generic coach), so the caller passes the specific id. The
     * coach reads the dossier for that id to find its primary muscles,
     * then reads [com.peakform.fitness.core.Field.readiness] for each.
     *
     * This method is pure — no side effects, no writes to ProState. The
     * field's state is read but never mutated.
     *
     * @param base       the prescription returned by [reconcile]
     * @param exerciseId the exercise this prescription is for
     * @return either [base] unchanged, or an adjusted prescription
     */
    fun adjustForReadiness(
        base: com.peakform.fitness.coach.authored.Prescription,
        exerciseId: String,
    ): com.peakform.fitness.coach.authored.Prescription = base
}
