package com.peakform.fitness.coach.authored

import com.peakform.fitness.coach.ExerciseCoach
import com.peakform.fitness.core.CoachingEngine

/**
 * v34b: Five authored coaches.
 * Each reads its rows from AuthoredRows, implements reconcile() per 02-truth-layer.md.
 * reconcile() selects rows, never blends. No invented numbers.
 */

// ── BackSquatCoach ──
// Sources: Prilepin chart (prilepin-1974/chart.md), Rippetoe novice LP (rippetoe-2011/novice-lp.md)
object BackSquatAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("back_squat", "barbell_back_squat", "squat")
    override val movementName = "Barbell Back Squat"
    override val formCues = listOf("Brace hard", "Sit between heels", "Knees track toes")
    override val envelope = ExerciseEnvelope(maxSets = 7, minSets = 2, maxLoad = 1000.0, maxDuration = 0.0, maxDistance = 0.0, maxPct1RM = 0.95)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1

    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)

    // BATCH-39f: read the field's readiness for this exercise's primary muscles
    // and adjust the prescription (drop sets + load when the muscle is recovering).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)

    override fun message(input: CoachingEngine.CoachInput) = "Brace. Drive through heels."
    override fun progressionFactor(input: CoachingEngine.CoachInput) = 1.0
    override fun deloadTrigger(input: CoachingEngine.CoachInput) = false

    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { row ->
            (row.intent == intent || row.intent == "*") &&
            (row.phase == phase || row.phase == "*") &&
            (bodyProfile == null || row.trainingAge == "*" || row.trainingAge == bodyProfile.trainingAge) &&
            (bodyProfile == null || row.sex == "*" || row.sex == bodyProfile.sex)
        }
        return when {
            matched.isEmpty() -> Prescription(note = "No source row exists for back_squat at intent=$intent phase=$phase. No prescription issued.")
            matched.size == 1 -> rowToPrescription(matched[0])
            else -> {
                val dominant = matched.first { it.intent == intent } ?: matched.first()
                val deferred = matched.filter { it.rowId != dominant.rowId }.map { it.rowId }
                rowToPrescription(dominant).copy(deferrals = deferred, note = "Multiple rows matched. Dominant intent=$intent selected. Deferred: $deferred")
            }
        }
    }

    override fun explain(prescription: Prescription, intent: String) =
        if (prescription.sourceRowId != null) "Back squat: ${prescription.sets}×${prescription.reps} at ${(prescription.pct1RM!! * 100).toInt()}% 1RM. Brace hard."
        else prescription.note

    private fun rowToPrescription(row: AuthoredRow) = Prescription(
        sets = row.sets, reps = row.reps, pct1RM = row.pct1RM, restSeconds = row.restSeconds,
        components = row.components, sourceRowId = row.rowId, note = "Source: ${row.sourceFile}#${row.sourceSection}"
    )
}

// ── PullUpCoach ──
// Source: Sommer gymnastic body tier table (sommer-2006/gymnastic-body-tier-table.md)
object PullUpAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("pull_up", "pullup")
    override val movementName = "Pull-Up"
    override val formCues = listOf("Dead hang start", "Chin over bar", "Control the descent")
    override val envelope = ExerciseEnvelope(maxSets = 5, minSets = 2, maxLoad = 0.0, maxDuration = 0.0, maxDistance = 0.0, maxPct1RM = 0.0)
    override val provenance = CoachProvenance("sommer-2006/gymnastic-body-tier-table.md", "Tier 5: Pull-up", 1, "Sommer leverage progression")
    override val version = 1

    override fun requirements() = CoachRequirements(needsBodyweight = true)

    // BATCH-39f: bodyweight — reduce sets only (no load to reduce).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBodyweight(base, exerciseId)

    override fun message(input: CoachingEngine.CoachInput) = "Dead hang. Pull chin over."
    override fun progressionFactor(input: CoachingEngine.CoachInput) = 1.0
    override fun deloadTrigger(input: CoachingEngine.CoachInput) = false

    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { row ->
            (row.intent == intent || row.intent == "*") &&
            (row.phase == phase || row.phase == "*") &&
            (bodyProfile == null || row.trainingAge == "*" || row.trainingAge == bodyProfile.trainingAge)
        }
        return when {
            matched.isEmpty() -> Prescription(note = "No source row exists for pull_up at intent=$intent phase=$phase.")
            else -> rowToPrescription(matched.first())
        }
    }

    override fun explain(prescription: Prescription, intent: String) =
        if (prescription.sourceRowId != null) "Pull-ups: Tier ${prescription.tier}, ${prescription.sets}×${prescription.reps}. Progress leverage, not load."
        else prescription.note

    private fun rowToPrescription(row: AuthoredRow) = Prescription(
        sets = row.sets, reps = row.reps, tier = row.tier, restSeconds = row.restSeconds,
        components = row.components, sourceRowId = row.rowId, note = "Source: ${row.sourceFile}#${row.sourceSection}"
    )
}

// ── BandedLateralRaiseCoach ──
// Source: Rogue band tension chart (rogue-band-tension/chart.md)
object BandedLateralRaiseAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("banded_lateral_raise")
    override val movementName = "Banded Lateral Raise"
    override val formCues = listOf("Stand on band", "Raise to shoulder height", "Control the descent")
    override val envelope = ExerciseEnvelope(maxSets = 5, minSets = 2, maxLoad = 0.0, maxDuration = 0.0, maxDistance = 0.0, maxPct1RM = 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Red row", 1, "Rogue band tension chart")
    override val version = 1

    override fun requirements() = CoachRequirements(needsBand = true)

    // BATCH-39f: banded — reduce sets only (band colour is the load, not a number).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)

    override fun message(input: CoachingEngine.CoachInput) = "Band colour is the load."
    override fun progressionFactor(input: CoachingEngine.CoachInput) = 1.0
    override fun deloadTrigger(input: CoachingEngine.CoachInput) = false

    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { row ->
            (row.intent == intent || row.intent == "*") &&
            (row.phase == phase || row.phase == "*") &&
            (bodyProfile == null || row.trainingAge == "*" || row.trainingAge == bodyProfile.trainingAge)
        }
        return when {
            matched.isEmpty() -> Prescription(note = "No source row exists for banded_lateral_raise at intent=$intent phase=$phase.")
            else -> rowToPrescription(matched.first())
        }
    }

    override fun explain(prescription: Prescription, intent: String) =
        if (prescription.sourceRowId != null) "Banded lateral raise: ${prescription.sets}×${prescription.reps} with ${prescription.bandColour} band."
        else prescription.note

    private fun rowToPrescription(row: AuthoredRow) = Prescription(
        sets = row.sets, reps = row.reps, bandColour = row.bandColour, restSeconds = row.restSeconds,
        components = row.components, sourceRowId = row.rowId, note = "Source: ${row.sourceFile}#${row.sourceSection}"
    )
}

// ── PlankCoach ──
// Source: McGill plank standards (mcgill-2010/plank-standards.md)
object PlankAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("plank")
    override val movementName = "Plank"
    override val formCues = listOf("Brace", "Hips level", "Breathe")
    override val envelope = ExerciseEnvelope(maxSets = 5, minSets = 2, maxLoad = 0.0, maxDuration = 300.0, maxDistance = 0.0, maxPct1RM = 0.0)
    override val provenance = CoachProvenance("mcgill-2010/plank-standards.md", "Front plank standards", 1, "McGill isometric endurance norms")
    override val version = 1

    override fun requirements() = CoachRequirements(needsDuration = true)

    // BATCH-39f: time-based — reduce sets + duration.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustTimeBased(base, exerciseId)

    override fun message(input: CoachingEngine.CoachInput) = "Hold. Breathe. Don't sag."
    override fun progressionFactor(input: CoachingEngine.CoachInput) = 1.0
    override fun deloadTrigger(input: CoachingEngine.CoachInput) = false

    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { row ->
            (row.intent == intent || row.intent == "*") &&
            (row.phase == phase || row.phase == "*") &&
            (bodyProfile == null || row.trainingAge == "*" || row.trainingAge == bodyProfile.trainingAge) &&
            (bodyProfile == null || row.sex == "*" || row.sex == bodyProfile.sex)
        }
        return when {
            matched.isEmpty() -> Prescription(note = "No source row exists for plank at intent=$intent phase=$phase.")
            else -> rowToPrescription(matched.first())
        }
    }

    override fun explain(prescription: Prescription, intent: String) =
        if (prescription.sourceRowId != null) "Plank: ${prescription.sets}×${prescription.duration?.toInt()}s hold. Hips level."
        else prescription.note

    private fun rowToPrescription(row: AuthoredRow) = Prescription(
        sets = row.sets, reps = "hold", duration = row.duration, restSeconds = row.restSeconds,
        components = row.components, sourceRowId = row.rowId, note = "Source: ${row.sourceFile}#${row.sourceSection}"
    )
}

// ── RunningCoach ──
// Source: Daniels VDOT zones (daniels-2013/vdot-zones.md)
object RunningAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("running", "run")
    override val movementName = "Running"
    override val formCues = listOf("Nasal breathing", "Relaxed shoulders", "Midfoot strike")
    override val envelope = ExerciseEnvelope(maxSets = 1, minSets = 1, maxLoad = 0.0, maxDuration = 7200.0, maxDistance = 50.0, maxPct1RM = 0.0)
    override val provenance = CoachProvenance("daniels-2013/vdot-zones.md", "Zone E", 1, "Daniels VDOT training zones")
    override val version = 1

    override fun requirements() = CoachRequirements(needsDistance = true, needsPace = true, needsDuration = true)

    // BATCH-39f: cardio — reduce duration only (no sets in the same sense).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCardio(base, exerciseId)

    override fun message(input: CoachingEngine.CoachInput) = "Zone 2. Nasal breathing."
    override fun progressionFactor(input: CoachingEngine.CoachInput) = 1.0
    override fun deloadTrigger(input: CoachingEngine.CoachInput) = false

    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { row ->
            (row.intent == intent || row.intent == "*") &&
            (row.phase == phase || row.phase == "*")
        }
        return when {
            matched.isEmpty() -> Prescription(note = "No source row exists for running at intent=$intent phase=$phase.")
            else -> rowToPrescription(matched.first())
        }
    }

    override fun explain(prescription: Prescription, intent: String) =
        if (prescription.sourceRowId != null) "Run: ${prescription.distance?.toInt()} miles at ${prescription.pace}. Zone 2."
        else prescription.note

    private fun rowToPrescription(row: AuthoredRow) = Prescription(
        sets = 1, reps = "continuous", distance = row.distance, pace = row.pace, duration = row.duration,
        restSeconds = 0, components = row.components, sourceRowId = row.rowId,
        note = "Source: ${row.sourceFile}#${row.sourceSection}"
    )
}

// ── v35: 10 new authored coaches ──

// ── DeadliftCoach ──
object DeadliftAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("deadlift", "conventional_deadlift")
    override val movementName = "Deadlift"
    override val formCues = listOf("Flat back", "Push the floor", "Bar close")
    override val envelope = ExerciseEnvelope(7, 1, 1000.0, 0.0, 0.0, 0.95)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Flat back. Push the floor."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for deadlift at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Deadlift: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Push the floor." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── BenchPressCoach ──
object BenchPressAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("bench_press", "barbell_bench_press")
    override val movementName = "Bench Press"
    override val formCues = listOf("Control descent", "Drive heels", "Lock elbows")
    override val envelope = ExerciseEnvelope(7, 2, 1000.0, 0.0, 0.0, 0.95)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Control. Drive through heels."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for bench_press at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Bench press: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Drive through heels." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── OverheadPressCoach ──
object OverheadPressAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("overhead_press", "ohp", "military_press")
    override val movementName = "Overhead Press"
    override val formCues = listOf("Brace", "Glutes tight", "Press straight up")
    override val envelope = ExerciseEnvelope(7, 2, 500.0, 0.0, 0.0, 0.90)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Brace. Glutes tight."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for overhead_press at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "OHP: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Glutes tight." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── BarbellRowCoach ──
object BarbellRowAuthoredCoach : ExerciseCoach {
    // v35b: "pendlay_row" removed — claimed by PendlayRowAuthoredCoach (35b, Part 1).
    override val exerciseIds = setOf("barbell_row", "t_bar_row")
    override val movementName = "Barbell Row"
    override val formCues = listOf("Flat back", "Pull to sternum", "Squeeze blades")
    override val envelope = ExerciseEnvelope(7, 2, 500.0, 0.0, 0.0, 0.85)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Flat back. Pull to sternum."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for barbell_row at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Row: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Pull to sternum." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── FrontSquatCoach ──
object FrontSquatAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("front_squat", "barbell_front_squat")
    override val movementName = "Front Squat"
    override val formCues = listOf("Elbows high", "Front rack", "Stay upright")
    override val envelope = ExerciseEnvelope(7, 2, 800.0, 0.0, 0.0, 0.90)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Elbows high. Stay upright."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for front_squat at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Front squat: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Elbows high." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── RomanianDeadliftCoach ──
object RomanianDeadliftAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("romanian_deadlift", "rdl")
    override val movementName = "Romanian Deadlift"
    override val formCues = listOf("Hinge at hips", "Stretch hamstrings", "Flat back")
    override val envelope = ExerciseEnvelope(5, 2, 600.0, 0.0, 0.0, 0.75)
    override val provenance = CoachProvenance("rippetoe-2011/assistance.md", "RDL row", 1, "Rippetoe assistance")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Hinge. Stretch. Flat back."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for romanian_deadlift at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "RDL: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Hinge, stretch, flat back." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── ChinUpCoach ──
object ChinUpAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("chin_up", "chinup")
    override val movementName = "Chin-Up"
    override val formCues = listOf("Supinate", "Chin over bar", "Control descent")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("sommer-2006/gymnastic-body-tier-table.md", "Tier 4: Chin-up", 1, "Sommer leverage progression")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Supinate. Chin over bar."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for chin_up at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, tier = matched.first().tier, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Chin-ups: Tier ${p.tier}, ${p.sets}x${p.reps}. Progress leverage." else p.note

    // BATCH-39f: bodyweight — reduce sets only (no load to reduce).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBodyweight(base, exerciseId)
}

// ── DipCoach ──
object DipAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("dip", "dips")
    override val movementName = "Dip"
    override val formCues = listOf("Shoulder to elbow", "Lean for chest", "Lock out")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("sommer-2006/gymnastic-body-tier-table.md", "Tier 5 (adapted)", 1, "Sommer leverage model adapted")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Shoulder to elbow. Lock out."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for dip at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, tier = matched.first().tier, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Dips: Tier ${p.tier}, ${p.sets}x${p.reps}. Shoulder to elbow." else p.note

    // BATCH-39f: bodyweight — reduce sets only (no load to reduce).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBodyweight(base, exerciseId)
}

// ── PushUpCoach ──
object PushUpAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("push_up", "pushup")
    override val movementName = "Push-Up"
    override val formCues = listOf("Body straight", "Chest to floor", "Full lockout")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("sommer-2006/gymnastic-body-tier-table.md", "Tier 2/5 (adapted)", 1, "Sommer leverage model adapted")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Body straight. Full range."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for push_up at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, tier = matched.first().tier, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Push-ups: Tier ${p.tier}, ${p.sets}x${p.reps}. Body straight." else p.note

    // BATCH-39f: bodyweight — reduce sets only (no load to reduce).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBodyweight(base, exerciseId)
}

// ── FacePullCoach ──
object FacePullAuthoredCoach : ExerciseCoach {
    // v35b: "banded_face_pull" removed — claimed by BandedFacePullAuthoredCoach (35b, Part 2).
    override val exerciseIds = setOf("face_pull")
    override val movementName = "Face Pull"
    override val formCues = listOf("Pull to face", "External rotate", "Squeeze rear delts")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Red row", 1, "Rogue band tension chart")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun message(input: CoachingEngine.CoachInput) = "Pull to face. External rotate."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for face_pull at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, bandColour = matched.first().bandColour, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Face pulls: ${p.sets}x${p.reps} with ${p.bandColour} band. Pull to face." else p.note

    // BATCH-39f: banded — reduce sets only (band colour is the load).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)
}

// ══════════════════════════════════════════════════════════════════════════════
// ── v35b: 16 new authored coaches ──
// Pattern follows BackSquatAuthoredCoach / DeadliftAuthoredCoach exactly:
//   - same ExerciseCoach interface
//   - same AuthoredRow shape
//   - reconcile() selects (never blends, never interpolates, never computes)
// 7 source files cover all 16 coaches (no new source files added).
// ══════════════════════════════════════════════════════════════════════════════

// ── PART 1 — Compound barbell from Prilepin (6 coaches) ──
// Prilepin zones: 70-79% → 3-6 reps/set, 80-89% → 2-4 reps/set, 90+ → 1-2 reps/set.
// Every row's pct1RM falls inside the zone named in its sourceSection.

// ── SumoDeadliftCoach ──
object SumoDeadliftAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("sumo_deadlift")
    override val movementName = "Sumo Deadlift"
    override val formCues = listOf("Wide stance", "Toes out", "Chest over the bar", "Drive knees out")
    override val envelope = ExerciseEnvelope(7, 1, 1000.0, 0.0, 0.0, 0.95)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Drive the knees out — chest up."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for sumo_deadlift at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Sumo deadlift: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Drive the knees out." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── InclineBenchPressCoach ──
object InclineBenchPressAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("incline_bench_press", "incline_press")
    override val movementName = "Incline Bench Press"
    override val formCues = listOf("Pin the shoulders", "Bar to upper chest", "Press straight up", "Lock out strong")
    override val envelope = ExerciseEnvelope(7, 2, 800.0, 0.0, 0.0, 0.95)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Pin the shoulders — bar to the upper chest."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for incline_bench_press at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Incline bench: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Bar to upper chest." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── CloseGripBenchPressCoach ──
object CloseGripBenchPressAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("close_grip_bench_press", "cg_bench")
    override val movementName = "Close-Grip Bench Press"
    override val formCues = listOf("Hands shoulder-width", "Elbows tucked", "Bar to lower chest", "Triceps drive")
    override val envelope = ExerciseEnvelope(7, 2, 600.0, 0.0, 0.0, 0.90)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Tuck the elbows — triceps drive the bar."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for close_grip_bench_press at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Close-grip bench: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Triceps drive." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── PendlayRowCoach ──
// Claims "pendlay_row" — removed from BarbellRowAuthoredCoach (35a) to avoid collision.
object PendlayRowAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("pendlay_row")
    override val movementName = "Pendlay Row"
    override val formCues = listOf("Bar dead-stops each rep", "Explosive pull", "Squeeze the upper back", "Lower fast")
    override val envelope = ExerciseEnvelope(7, 2, 500.0, 0.0, 0.0, 0.85)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Dead-stop each rep — explosive pull."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for pendlay_row at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Pendlay row: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Dead-stop each rep." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── PushPressCoach ──
object PushPressAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("push_press")
    override val movementName = "Push Press"
    override val formCues = listOf("Dip quarter-squat", "Drive through legs", "Press to lockout", "Snap under the bar")
    override val envelope = ExerciseEnvelope(7, 2, 500.0, 0.0, 0.0, 0.90)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Dip — drive — press. Snap under the bar."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for push_press at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Push press: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Dip — drive — press." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── PowerCleanCoach ──
// Prilepin chart does not specify rest for the power clean; 180s used here
// (documented in provenance.authorNote). All other Prilepin fields (reps, %1RM
// zone, total volume) apply unchanged.
object PowerCleanAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("power_clean")
    override val movementName = "Power Clean"
    override val formCues = listOf("Bar over midfoot", "Explosive second pull", "Catch in front rack", "Stand tall")
    override val envelope = ExerciseEnvelope(7, 2, 500.0, 0.0, 0.0, 0.90)
    override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "Prilepin + Rippetoe; rest=180s (Prilepin does not specify rest for the power clean)")
    override val version = 1
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Jump and catch — full extension, fast elbows."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for power_clean at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, pct1RM = matched.first().pct1RM, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Power clean: ${p.sets}x${p.reps} at ${(p.pct1RM!! * 100).toInt()}% 1RM. Jump and catch." else p.note

    // BATCH-39f: compound — reduce sets + load.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustCompound(base, exerciseId)
}

// ── PART 2 — Banded from Rogue (6 coaches) ──
// requirements(): needsBand=true. No load. No %1RM.
// Ladder mirror of BandedLateralRaiseAuthoredCoach:
//   yellow novice hypertrophy → red hypertrophy → green stronger → blue maintain
// (BandedFacePullAuthoredCoach drops yellow because the cable face_pull already
// covers novice. BandedPullApartAuthoredCoach includes the yellow novice row.)

// ── BandedFacePullCoach ──
// Claims "banded_face_pull" — removed from FacePullAuthoredCoach (35a) to avoid collision.
object BandedFacePullAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("banded_face_pull")
    override val movementName = "Banded Face Pull"
    override val formCues = listOf("Pull to face", "External rotate", "Squeeze rear delts", "Control the return")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Red row", 1, "Rogue band tension chart")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun message(input: CoachingEngine.CoachInput) = "Pull to face. External rotate."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for banded_face_pull at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, bandColour = matched.first().bandColour, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Banded face pulls: ${p.sets}x${p.reps} with ${p.bandColour} band. Pull to face." else p.note

    // BATCH-39f: banded — reduce sets only (band colour is the load).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)
}

// ── BandedPullApartCoach ──
object BandedPullApartAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("banded_pull_apart")
    override val movementName = "Banded Pull-Apart"
    override val formCues = listOf("Arms straight", "Pull band apart", "Squeeze rear delts", "Slow return")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Yellow row", 1, "Rogue band tension chart; yellow novice row included")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun message(input: CoachingEngine.CoachInput) = "Arms straight. Pull apart."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for banded_pull_apart at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, bandColour = matched.first().bandColour, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Banded pull-aparts: ${p.sets}x${p.reps} with ${p.bandColour} band. Arms straight." else p.note

    // BATCH-39f: banded — reduce sets only (band colour is the load).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)
}

// ── BandedSquatCoach ──
object BandedSquatAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("banded_squat")
    override val movementName = "Banded Squat"
    override val formCues = listOf("Band around knees", "Sit between heels", "Push knees out", "Hit depth")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Yellow row", 1, "Rogue band tension chart")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun message(input: CoachingEngine.CoachInput) = "Push the knees out — sit between heels."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for banded_squat at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, bandColour = matched.first().bandColour, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Banded squats: ${p.sets}x${p.reps} with ${p.bandColour} band. Push knees out." else p.note

    // BATCH-39f: banded — reduce sets only (band colour is the load).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)
}

// ── BandedRowCoach ──
object BandedRowAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("banded_row")
    override val movementName = "Banded Row"
    override val formCues = listOf("Anchor the band", "Pull to sternum", "Squeeze blades", "Control the return")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Yellow row", 1, "Rogue band tension chart")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun message(input: CoachingEngine.CoachInput) = "Anchor the band. Pull to sternum."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for banded_row at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, bandColour = matched.first().bandColour, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Banded rows: ${p.sets}x${p.reps} with ${p.bandColour} band. Pull to sternum." else p.note

    // BATCH-39f: banded — reduce sets only (band colour is the load).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)
}

// ── BandedGluteBridgeCoach ──
object BandedGluteBridgeAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("banded_glute_bridge")
    override val movementName = "Banded Glute Bridge"
    override val formCues = listOf("Band around knees", "Drive through heels", "Squeeze glutes", "Pause at top")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Yellow row", 1, "Rogue band tension chart")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun message(input: CoachingEngine.CoachInput) = "Drive through the heels — pause at the top."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for banded_glute_bridge at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, bandColour = matched.first().bandColour, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Banded glute bridges: ${p.sets}x${p.reps} with ${p.bandColour} band. Squeeze the glutes." else p.note

    // BATCH-39f: banded — reduce sets only (band colour is the load).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)
}

// ── BandedChestPressCoach ──
object BandedChestPressAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("banded_chest_press")
    override val movementName = "Banded Chest Press"
    override val formCues = listOf("Anchor behind back", "Press straight out", "Pin shoulders", "Control return")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("rogue-band-tension/chart.md", "Yellow row", 1, "Rogue band tension chart")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun message(input: CoachingEngine.CoachInput) = "Anchor behind the back. Press straight out."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for banded_chest_press at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, bandColour = matched.first().bandColour, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Banded chest press: ${p.sets}x${p.reps} with ${p.bandColour} band. Press straight out." else p.note

    // BATCH-39f: banded — reduce sets only (band colour is the load).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBanded(base, exerciseId)
}

// ── PART 3 — Bodyweight from Sommer (3 coaches) ──
// requirements(): needsBodyweight=true. No 1RM. No load.

// ── InvertedRowCoach (Sommer tiers 1-3) ──
object InvertedRowAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("inverted_row")
    override val movementName = "Inverted Row"
    override val formCues = listOf("Heels on floor", "Pull chest to bar", "Squeeze blades", "Control the descent")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("sommer-2006/gymnastic-body-tier-table.md", "Tier 1: Band-assisted", 1, "Sommer leverage progression, tiers 1-3 (inverted row is easier than vertical pull-up)")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Pull chest to the bar. Control the descent."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") && (bodyProfile == null || it.trainingAge == "*" || it.trainingAge == bodyProfile.trainingAge) }
        return if (matched.isEmpty()) Prescription(note = "No source row for inverted_row at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, tier = matched.first().tier, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Inverted rows: Tier ${p.tier}, ${p.sets}x${p.reps}. Progress leverage." else p.note

    // BATCH-39f: bodyweight — reduce sets only (no load to reduce).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBodyweight(base, exerciseId)
}

// ── ArcherPullUpCoach (Sommer tier 8) ──
object ArcherPullUpAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("archer_pull_up")
    override val movementName = "Archer Pull-Up"
    override val formCues = listOf("Shift to one side", "Keep one arm straight", "Pull with the loaded arm", "Control the lower")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("sommer-2006/gymnastic-body-tier-table.md", "Tier 8: Archer pull-up", 1, "Sommer leverage progression, tier 8")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Shift to one side. Pull with the loaded arm."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for archer_pull_up at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, tier = matched.first().tier, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Archer pull-ups: Tier ${p.tier}, ${p.sets}x${p.reps}. Shift to one side." else p.note

    // BATCH-39f: bodyweight — reduce sets only (no load to reduce).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBodyweight(base, exerciseId)
}

// ── LSitPullUpCoach (Sommer tier 7) ──
object LSitPullUpAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("l_sit_pull_up")
    override val movementName = "L-Sit Pull-Up"
    override val formCues = listOf("Legs straight out", "Pull chin over bar", "Hold the L", "Control the lower")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 0.0, 0.0, 0.0)
    override val provenance = CoachProvenance("sommer-2006/gymnastic-body-tier-table.md", "Tier 7: L-sit pull-up", 1, "Sommer leverage progression, tier 7")
    override val version = 1
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun message(input: CoachingEngine.CoachInput) = "Legs straight out. Pull chin over the bar."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { (it.intent == intent || it.intent == "*") && (it.phase == phase || it.phase == "*") }
        return if (matched.isEmpty()) Prescription(note = "No source row for l_sit_pull_up at $intent/$phase.") else Prescription(matched.first().sets, matched.first().reps, tier = matched.first().tier, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "L-sit pull-ups: Tier ${p.tier}, ${p.sets}x${p.reps}. Legs straight out." else p.note

    // BATCH-39f: bodyweight — reduce sets only (no load to reduce).
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustBodyweight(base, exerciseId)
}

// ── PART 4 — Time-based from McGill SIDE PLANK table (1 coach) ──
// Cites the SIDE PLANK table (per-side hold times), not the front plank table.
// Side plank values differ from front plank: intermediate male 90s, not 120s.
// requirements(): needsDuration=true. Hold time is the single variable.

// ── SidePlankCoach ──
object SidePlankAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("side_plank")
    override val movementName = "Side Plank"
    override val formCues = listOf("Elbow under shoulder", "Hips stacked", "Brace the core", "Don't sag")
    override val envelope = ExerciseEnvelope(5, 2, 0.0, 300.0, 0.0, 0.0)
    override val provenance = CoachProvenance("mcgill-2010/plank-standards.md", "Side plank standards", 1, "McGill side plank isometric endurance norms (per-side hold time)")
    override val version = 1
    override fun requirements() = CoachRequirements(needsDuration = true)
    override fun message(input: CoachingEngine.CoachInput) = "Hold. Brace. Don't sag."
    override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
        val matched = rows.filter { row ->
            (row.intent == intent || row.intent == "*") &&
            (row.phase == phase || row.phase == "*") &&
            (bodyProfile == null || row.trainingAge == "*" || row.trainingAge == bodyProfile.trainingAge) &&
            (bodyProfile == null || row.sex == "*" || row.sex == bodyProfile.sex)
        }
        return if (matched.isEmpty()) Prescription(note = "No source row for side_plank at $intent/$phase.") else Prescription(matched.first().sets, reps = "hold", duration = matched.first().duration, restSeconds = matched.first().restSeconds, components = matched.first().components, sourceRowId = matched.first().rowId, note = "Source: ${matched.first().sourceFile}#${matched.first().sourceSection}")
    }
    override fun explain(p: Prescription, intent: String) = if (p.sourceRowId != null) "Side plank: ${p.sets}x${p.duration?.toInt()}s hold per side. Hips stacked." else p.note

    // BATCH-39f: time-based — reduce sets + duration.
    override fun adjustForReadiness(base: Prescription, exerciseId: String): Prescription =
        ReadinessAdjuster.adjustTimeBased(base, exerciseId)
}
