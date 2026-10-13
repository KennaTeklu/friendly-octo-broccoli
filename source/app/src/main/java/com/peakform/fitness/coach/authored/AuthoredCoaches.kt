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
