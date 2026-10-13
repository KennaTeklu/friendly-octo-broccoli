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
}

// ── BarbellRowCoach ──
object BarbellRowAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("barbell_row", "pendlay_row", "t_bar_row")
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
}

// ── FacePullCoach ──
object FacePullAuthoredCoach : ExerciseCoach {
    override val exerciseIds = setOf("face_pull", "banded_face_pull")
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
}
