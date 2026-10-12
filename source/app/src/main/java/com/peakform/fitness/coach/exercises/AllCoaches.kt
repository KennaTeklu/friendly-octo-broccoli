package com.peakform.fitness.coach.exercises
import com.peakform.fitness.coach.*
import kotlin.math.max
import kotlin.math.min
// ═══════════════════════════════════════════════════════════════
// COMPOUND BARBELL COACHES (needs1RM = true)
// ═══════════════════════════════════════════════════════════════
/**
 * Bench Press Coach — "The king of upper-body pressing."
 * Philosophy: 5/3/1-style progression. 65-85% 1RM working sets.
 * Progression: add 5 lbs when all reps at prescribed weight are completed.
 */
object BenchPressCoach : ExerciseCoach {
    override val id = "bench_press"
    override val displayName = "Bench Press Coach"
    override val supportedExerciseIds = setOf("bench_press", "barbell_bench_press")
    override val supportedNamePatterns = listOf(Regex("bench.?press", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "5-8", null, null, null, null, 180, "Test your 1RM first.")
        val pct = if (ctx.lastSession != null) 0.80 else 0.65
        val w = max(45.0, orm * pct * ctx.phaseMultiplier)
        return Prescription(5, "5-8", w, null, null, pct, 180, "Control the descent. Drive through the heels.")
    }
    override fun explain(p: Prescription) = "Bench: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs (${(p.pct1RM!!*100).toInt()}% 1RM). Drive through heels."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete at target reps", 5.0, 8)
}
object BackSquatCoach : ExerciseCoach {
    override val id = "back_squat"
    override val displayName = "Back Squat Coach"
    override val supportedExerciseIds = setOf("back_squat", "barbell_back_squat", "squat")
    override val supportedNamePatterns = listOf(Regex("back.?squat", RegexOption.IGNORE_CASE), Regex("\\bsquat\\b", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "5-8", null, null, null, null, 240, "Test your 1RM first.")
        val pct = if (ctx.lastSession != null) 0.80 else 0.65
        val w = max(45.0, orm * pct * ctx.phaseMultiplier)
        return Prescription(5, "5-8", w, null, null, pct, 240, "Brace. Sit between your heels. Knees track toes.")
    }
    override fun explain(p: Prescription) = "Squat: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs (${(p.pct1RM!!*100).toInt()}% 1RM). Brace hard."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete", 5.0, 8)
}
object DeadliftCoach : ExerciseCoach {
    override val id = "deadlift"
    override val displayName = "Deadlift Coach"
    override val supportedExerciseIds = setOf("deadlift", "conventional_deadlift")
    override val supportedNamePatterns = listOf(Regex("\\bdeadlift\\b", RegexOption.IGNORE_CASE), Regex("conventional.?deadlift", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "3-5", null, null, null, null, 300, "Test your 1RM first.")
        val pct = if (ctx.lastSession != null) 0.75 else 0.60
        val w = max(135.0, orm * pct * ctx.phaseMultiplier)
        return Prescription(3, "3-5", w, null, null, pct, 300, "Flat back. Push the floor away. Bar stays close.")
    }
    override fun explain(p: Prescription) = "Deadlift: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs (${(p.pct1RM!!*100).toInt()}% 1RM). Push the floor."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete", 5.0, 5)
}
object OverheadPressCoach : ExerciseCoach {
    override val id = "overhead_press"
    override val displayName = "Overhead Press Coach"
    override val supportedExerciseIds = setOf("overhead_press", "ohp", "military_press")
    override val supportedNamePatterns = listOf(Regex("overhead.?press", RegexOption.IGNORE_CASE), Regex("military.?press", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "5-8", null, null, null, null, 180, "Test your 1RM first.")
        val pct = if (ctx.lastSession != null) 0.75 else 0.60
        val w = max(45.0, orm * pct * ctx.phaseMultiplier)
        return Prescription(4, "5-8", w, null, null, pct, 180, "Brace. Glutes tight. Press straight up.")
    }
    override fun explain(p: Prescription) = "OHP: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs (${(p.pct1RM!!*100).toInt()}% 1RM). Glutes tight."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 2.5 lbs when all sets complete", 2.5, 8)
}
object BarbellRowCoach : ExerciseCoach {
    override val id = "barbell_row"
    override val displayName = "Barbell Row Coach"
    override val supportedExerciseIds = setOf("barbell_row", "pendlay_row", "t_bar_row")
    override val supportedNamePatterns = listOf(Regex("barbell.?row", RegexOption.IGNORE_CASE), Regex("pendlay", RegexOption.IGNORE_CASE), Regex("t.?bar.?row", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(4, "6-10", null, null, null, null, 120, "Test your 1RM first.")
        val w = max(45.0, orm * 0.70 * ctx.phaseMultiplier)
        return Prescription(4, "6-10", w, null, null, 0.70, 120, "Flat back. Pull to the sternum. Squeeze the shoulder blades.")
    }
    override fun explain(p: Prescription) = "Row: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Pull to sternum."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete", 5.0, 10)
}
object FrontSquatCoach : ExerciseCoach {
    override val id = "front_squat"
    override val displayName = "Front Squat Coach"
    override val supportedExerciseIds = setOf("front_squat", "barbell_front_squat")
    override val supportedNamePatterns = listOf(Regex("front.?squat", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "5-8", null, null, null, null, 240, "Test your 1RM first.")
        val w = max(45.0, orm * 0.70 * ctx.phaseMultiplier)
        return Prescription(4, "5-8", w, null, null, 0.70, 240, "Elbows high. Front rack. Stay upright.")
    }
    override fun explain(p: Prescription) = "Front Squat: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Elbows high."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete", 5.0, 8)
}
object RomanianDeadliftCoach : ExerciseCoach {
    override val id = "romanian_deadlift"
    override val displayName = "Romanian Deadlift Coach"
    override val supportedExerciseIds = setOf("romanian_deadlift", "rdl")
    override val supportedNamePatterns = listOf(Regex("romanian.?deadlift", RegexOption.IGNORE_CASE), Regex("\\brdl\\b", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "8-12", null, null, null, null, 120, "Test your 1RM first.")
        val w = max(45.0, orm * 0.60 * ctx.phaseMultiplier)
        return Prescription(3, "8-12", w, null, null, 0.60, 120, "Hinge at the hips. Stretch the hamstrings. Flat back.")
    }
    override fun explain(p: Prescription) = "RDL: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Hinge, stretch, flat back."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete", 5.0, 12)
}
// ═══════════════════════════════════════════════════════════════
// COMPOUND DUMBBELL / MACHINE (needs1RM = true)
// ═══════════════════════════════════════════════════════════════
object DumbbellBenchPressCoach : ExerciseCoach {
    override val id = "dumbbell_bench_press"
    override val displayName = "Dumbbell Bench Press Coach"
    override val supportedExerciseIds = setOf("dumbbell_bench_press")
    override val supportedNamePatterns = listOf(Regex("dumbbell.?bench", RegexOption.IGNORE_CASE), Regex("db.?bench", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "8-12", null, null, null, null, 120, "Test your 1RM first.")
        val w = max(15.0, orm * 0.70 * ctx.phaseMultiplier / 2) // per hand
        return Prescription(3, "8-12", w, null, null, 0.70, 120, "Stabilize. Full range. Don't flare the elbows.")
    }
    override fun explain(p: Prescription) = "DB Bench: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs/hand. Stabilize."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs/hand when all sets complete", 5.0, 12)
}
object DumbbellRowCoach : ExerciseCoach {
    override val id = "dumbbell_row"
    override val displayName = "Dumbbell Row Coach"
    override val supportedExerciseIds = setOf("dumbbell_row", "db_row")
    override val supportedNamePatterns = listOf(Regex("dumbbell.?row", RegexOption.IGNORE_CASE), Regex("db.?row", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "8-12", null, null, null, null, 90, "Test your 1RM first.")
        val w = max(10.0, orm * 0.65 * ctx.phaseMultiplier / 2)
        return Prescription(3, "8-12", w, null, null, 0.65, 90, "Hinge. Pull elbow past the torso. Squeeze.")
    }
    override fun explain(p: Prescription) = "DB Row: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs/hand. Pull past torso."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs/hand when all sets complete", 5.0, 12)
}
object LegPressCoach : ExerciseCoach {
    override val id = "leg_press"
    override val displayName = "Leg Press Coach"
    override val supportedExerciseIds = setOf("leg_press")
    override val supportedNamePatterns = listOf(Regex("leg.?press", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "10-15", null, null, null, null, 120, "Test your 1RM first.")
        val w = max(90.0, orm * 0.75 * ctx.phaseMultiplier)
        return Prescription(3, "10-15", w, null, null, 0.75, 120, "Full depth. Don't lock out. Control the descent.")
    }
    override fun explain(p: Prescription) = "Leg Press: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Full depth."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 10 lbs when all sets complete", 10.0, 15)
}
object LatPulldownCoach : ExerciseCoach {
    override val id = "lat_pulldown"
    override val displayName = "Lat Pulldown Coach"
    override val supportedExerciseIds = setOf("lat_pulldown", "cable_pulldown")
    override val supportedNamePatterns = listOf(Regex("lat.?pulldown", RegexOption.IGNORE_CASE), Regex("cable.?pulldown", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "8-12", null, null, null, null, 90, "Test your 1RM first.")
        val w = max(30.0, orm * 0.65 * ctx.phaseMultiplier)
        return Prescription(3, "8-12", w, null, null, 0.65, 90, "Pull to the upper chest. Lead with the elbows.")
    }
    override fun explain(p: Prescription) = "Pulldown: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Lead with elbows."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete", 5.0, 12)
}
// ═══════════════════════════════════════════════════════════════
// BODYWEIGHT (needsBodyweight = true, needs1RM = false)
// ═══════════════════════════════════════════════════════════════
object PullUpCoach : ExerciseCoach {
    override val id = "pull_up"
    override val displayName = "Pull-Up Coach"
    override val supportedExerciseIds = setOf("pull_up", "pullup")
    override val supportedNamePatterns = listOf(Regex("pull.?up", RegexOption.IGNORE_CASE), Regex("chin.?up", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val bw = ctx.userBodyweight ?: 150.0
        val sets = 3
        val reps = if (ctx.lastSession != null) "${max(3, ctx.lastSession.reps)}-${ctx.lastSession.reps + 2}" else "3-8"
        return Prescription(sets, reps, bw, null, null, null, 120, "Dead hang start. Pull chin over. Control the descent.")
    }
    override fun explain(p: Prescription) = "Pull-Ups: ${p.sets}×${p.reps}. Dead hang. Chin over bar."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Rep", "Add 1 rep when all sets complete at top of range", 1.0, 8)
}
object PushUpCoach : ExerciseCoach {
    override val id = "push_up"
    override val displayName = "Push-Up Coach"
    override val supportedExerciseIds = setOf("push_up", "pushup")
    override val supportedNamePatterns = listOf(Regex("push.?up", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val bw = ctx.userBodyweight ?: 150.0
        return Prescription(3, "10-20", bw * 0.64, null, null, null, 60, "Body straight. Chest to floor. Full lockout.")
    }
    override fun explain(p: Prescription) = "Push-Ups: ${p.sets}×${p.reps}. Body straight. Full range."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Rep", "Add 2 reps when all sets complete at top", 2.0, 20)
}
object DipCoach : ExerciseCoach {
    override val id = "dip"
    override val displayName = "Dip Coach"
    override val supportedExerciseIds = setOf("dip", "dips")
    override val supportedNamePatterns = listOf(Regex("\\bdip", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val bw = ctx.userBodyweight ?: 150.0
        return Prescription(3, "6-12", bw, null, null, null, 90, "Lean forward for chest, upright for triceps. Shoulder to elbow.")
    }
    override fun explain(p: Prescription) = "Dips: ${p.sets}×${p.reps}. Shoulder to elbow depth."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Rep", "Add 1 rep when all sets complete", 1.0, 12)
}
// ═══════════════════════════════════════════════════════════════
// BANDED (needsBand = true, needsWeight = false)
// ═══════════════════════════════════════════════════════════════
object BandedLateralRaiseCoach : ExerciseCoach {
    override val id = "banded_lateral_raise"
    override val displayName = "Banded Lateral Raise Coach"
    override val supportedExerciseIds = setOf("banded_lateral_raise")
    override val supportedNamePatterns = listOf(Regex("banded.?lateral", RegexOption.IGNORE_CASE), Regex("band.?raise", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(3, "15-20", null, null, null, null, 45, "Stand on the band. Raise to shoulder height. Control the descent.")
    }
    override fun explain(p: Prescription) = "Banded Lateral Raise: ${'$'}{p.sets}×${'$'}{p.reps}. Raise to shoulder height."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Band", "Upgrade band color when 20 reps completed", 0.0, 20)
}
object BandedFacePullCoach : ExerciseCoach {
    override val id = "banded_face_pull"
    override val displayName = "Banded Face Pull Coach"
    override val supportedExerciseIds = setOf("banded_face_pull")
    override val supportedNamePatterns = listOf(Regex("banded.?face.?pull", RegexOption.IGNORE_CASE), Regex("face.?pull", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(3, "15-25", null, null, null, null, 45, "Pull to the face. External rotate. Squeeze the rear delts.")
    }
    override fun explain(p: Prescription) = "Banded Face Pull: ${p.sets}×${p.reps}. Pull to face, external rotate."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Rep", "Add 5 reps when all sets complete", 5.0, 25)
}
object BandedPullApartCoach : ExerciseCoach {
    override val id = "banded_pull_apart"
    override val displayName = "Banded Pull-Apart Coach"
    override val supportedExerciseIds = setOf("banded_pull_apart", "band_pull_apart")
    override val supportedNamePatterns = listOf(Regex("pull.?apart", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(3, "20-30", null, null, null, null, 30, "Arms straight. Pull apart. Squeeze the upper back.")
    }
    override fun explain(p: Prescription) = "Banded Pull-Aparts: ${p.sets}×${p.reps}. Arms straight. Squeeze."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Rep", "Add 5 reps when all sets complete", 5.0, 30)
}
// ═══════════════════════════════════════════════════════════════
// TIME-BASED (needsDuration = true, needsWeight = false)
// ═══════════════════════════════════════════════════════════════
object PlankCoach : ExerciseCoach {
    override val id = "plank"
    override val displayName = "Plank Coach"
    override val supportedExerciseIds = setOf("plank")
    override val supportedNamePatterns = listOf(Regex("\\bplank\\b", RegexOption.IGNORE_CASE), Regex("weighted.?plank", RegexOption.IGNORE_CASE), Regex("long.?lever", RegexOption.IGNORE_CASE), Regex("fingertip.?plank", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val dur = if (ctx.lastSession != null) min(90.0, ctx.lastSession.weight + 10) else 30.0
        return Prescription(3, "hold", null, dur, null, null, 45, "Brace. Hips level. Breathe. Don't sag.")
    }
    override fun explain(p: Prescription) = "Plank: ${p.sets} × ${p.duration?.toInt()}s hold. Hips level."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Time", "Add 10 seconds when all sets complete", 10.0, 90)
}
object SidePlankCoach : ExerciseCoach {
    override val id = "side_plank"
    override val displayName = "Side Plank Coach"
    override val supportedExerciseIds = setOf("side_plank")
    override val supportedNamePatterns = listOf(Regex("side.?plank", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val dur = if (ctx.lastSession != null) min(60.0, ctx.lastSession.weight + 5) else 20.0
        return Prescription(3, "hold", null, dur, null, null, 45, "Stack the hips. Don't let the bottom hip drop.")
    }
    override fun explain(p: Prescription) = "Side Plank: ${p.sets} × ${p.duration?.toInt()}s/side. Stack hips."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Time", "Add 5 seconds per side", 5.0, 60)
}
object DeadHangCoach : ExerciseCoach {
    override val id = "dead_hang"
    override val displayName = "Dead Hang Coach"
    override val supportedExerciseIds = setOf("dead_hang")
    override val supportedNamePatterns = listOf(Regex("dead.?hang", RegexOption.IGNORE_CASE), Regex("hang", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val dur = if (ctx.lastSession != null) min(120.0, ctx.lastSession.weight + 10) else 30.0
        return Prescription(3, "hold", null, dur, null, null, 60, "Relax the shoulders. Let the spine decompress. Breathe.")
    }
    override fun explain(p: Prescription) = "Dead Hang: ${p.sets} × ${p.duration?.toInt()}s. Decompress."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Time", "Add 10 seconds", 10.0, 120)
}
// ═══════════════════════════════════════════════════════════════
// ISOLATION (needsWeight = true, needs1RM = false at first)
// ═══════════════════════════════════════════════════════════════
object DumbbellCurlCoach : ExerciseCoach {
    override val id = "dumbbell_curl"
    override val displayName = "Dumbbell Curl Coach"
    override val supportedExerciseIds = setOf("dumbbell_curl", "db_curl")
    override val supportedNamePatterns = listOf(Regex("dumbbell.?curl", RegexOption.IGNORE_CASE), Regex("db.?curl", RegexOption.IGNORE_CASE), Regex("\\bcurl\\b", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val w = if (ctx.lastSession != null) ctx.lastSession.weight else 15.0
        return Prescription(3, "10-15", w, null, null, null, 60, "Supinate. Don't swing. Squeeze at the top.")
    }
    override fun explain(p: Prescription) = "DB Curls: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs/hand. Supinate. No swing."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 2.5 lbs/hand when 15 reps achieved", 2.5, 15)
}
object TricepPushdownCoach : ExerciseCoach {
    override val id = "tricep_pushdown"
    override val displayName = "Tricep Pushdown Coach"
    override val supportedExerciseIds = setOf("tricep_pushdown", "cable_pushdown")
    override val supportedNamePatterns = listOf(Regex("pushdown", RegexOption.IGNORE_CASE), Regex("tricep.?extension", RegexOption.IGNORE_CASE), Regex("skull.?crusher", RegexOption.IGNORE_CASE), Regex("overhead.?extension", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val w = if (ctx.lastSession != null) ctx.lastSession.weight else 25.0
        return Prescription(3, "10-15", w, null, null, null, 60, "Elbows pinned. Full extension. Don't lean forward.")
    }
    override fun explain(p: Prescription) = "Pushdowns: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Elbows pinned."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when 15 reps achieved", 5.0, 15)
}
object LegCurlCoach : ExerciseCoach {
    override val id = "leg_curl"
    override val displayName = "Leg Curl Coach"
    override val supportedExerciseIds = setOf("leg_curl", "hamstring_curl")
    override val supportedNamePatterns = listOf(Regex("leg.?curl", RegexOption.IGNORE_CASE), Regex("hamstring.?curl", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val w = if (ctx.lastSession != null) ctx.lastSession.weight else 30.0
        return Prescription(3, "10-15", w, null, null, null, 60, "Control the negative. Squeeze the hamstring at flexion.")
    }
    override fun explain(p: Prescription) = "Leg Curls: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Control the negative."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when 15 reps achieved", 5.0, 15)
}
object LegExtensionCoach : ExerciseCoach {
    override val id = "leg_extension"
    override val displayName = "Leg Extension Coach"
    override val supportedExerciseIds = setOf("leg_extension", "quad_extension")
    override val supportedNamePatterns = listOf(Regex("leg.?extension", RegexOption.IGNORE_CASE), Regex("quad.?extension", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val w = if (ctx.lastSession != null) ctx.lastSession.weight else 40.0
        return Prescription(3, "12-15", w, null, null, null, 45, "Full extension. Squeeze the quad. Control the descent.")
    }
    override fun explain(p: Prescription) = "Leg Extensions: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Squeeze at top."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when 15 reps achieved", 5.0, 15)
}
// ═══════════════════════════════════════════════════════════════
// CARDIO (no 1RM, no weight)
// ═══════════════════════════════════════════════════════════════
object RunningCoach : ExerciseCoach {
    override val id = "running"
    override val displayName = "Running Coach"
    override val supportedExerciseIds = setOf("running", "run")
    override val supportedNamePatterns = listOf(Regex("\\brun", RegexOption.IGNORE_CASE), Regex("\\bjog", RegexOption.IGNORE_CASE), Regex("\\bsprint", RegexOption.IGNORE_CASE), Regex("interval", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDistance = true, needsPace = true, needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(1, "continuous", null, 1800.0, 3.0, null, 0, "Zone 2. Nasal breathing. You should be able to hold a conversation.")
    }
    override fun explain(p: Prescription) = "Run: ${p.duration?.toInt()?.div(60)} min @ Zone 2. Nasal breathing."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Distance", "Add 0.5 miles when session completed at Zone 2", 0.5, 1)
}
object CyclingCoach : ExerciseCoach {
    override val id = "cycling"
    override val displayName = "Cycling Coach"
    override val supportedExerciseIds = setOf("cycling", "bike")
    override val supportedNamePatterns = listOf(Regex("cycl", RegexOption.IGNORE_CASE), Regex("bike", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDistance = true, needsPace = true, needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(1, "continuous", null, 2400.0, 8.0, null, 0, "Steady cadence 80-90 rpm. Zone 2 heart rate.")
    }
    override fun explain(p: Prescription) = "Cycle: ${p.duration?.toInt()?.div(60)} min @ 80-90 rpm. Steady cadence."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Time", "Add 10 minutes when session completed", 10.0, 1)
}
object RowingCoach : ExerciseCoach {
    override val id = "rowing"
    override val displayName = "Rowing Coach"
    override val supportedExerciseIds = setOf("rowing", "erg")
    override val supportedNamePatterns = listOf(Regex("\\brow", RegexOption.IGNORE_CASE), Regex("\\berg\\b", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDistance = true, needsPace = true, needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(1, "continuous", null, 1200.0, 2.0, null, 0, "Legs drive first. Back follows. Arms last. Reverse the order on recovery.")
    }
    override fun explain(p: Prescription) = "Row: ${p.duration?.toInt()?.div(60)} min. Legs, back, arms."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Distance", "Add 500m when session completed", 0.5, 1)
}
// ═══════════════════════════════════════════════════════════════
// GENERIC FALLBACK COACHES (should rarely/never hit)
// ═══════════════════════════════════════════════════════════════
object GenericBarbellCoach : ExerciseCoach {
    override val id = "generic_barbell"
    override val displayName = "Generic Barbell Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("barbell", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needs1RM = true, needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val orm = ctx.user1RM ?: return Prescription(3, "8-12", null, null, null, null, 120, "Test your 1RM for a specific prescription.")
        val w = max(45.0, orm * 0.70 * ctx.phaseMultiplier)
        return Prescription(3, "8-12", w, null, null, 0.70, 120, "Barbell movement. Control the tempo.")
    }
    override fun explain(p: Prescription) = "Barbell: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Control tempo."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when all sets complete", 5.0, 12)
}
object GenericDumbbellCoach : ExerciseCoach {
    override val id = "generic_dumbbell"
    override val displayName = "Generic Dumbbell Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("dumbbell", RegexOption.IGNORE_CASE), Regex("\\bdb\\b", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val w = if (ctx.lastSession != null) ctx.lastSession.weight else 15.0
        return Prescription(3, "10-15", w, null, null, null, 60, "Dumbbell movement. Stabilize and control.")
    }
    override fun explain(p: Prescription) = "Dumbbell: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Stabilize."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when 15 reps achieved", 5.0, 15)
}
object GenericBodyweightCoach : ExerciseCoach {
    override val id = "generic_bodyweight"
    override val displayName = "Generic Bodyweight Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("bodyweight", RegexOption.IGNORE_CASE), Regex("body.?weight", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBodyweight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(3, "10-15", null, null, null, null, 60, "Bodyweight movement. Quality over quantity.")
    }
    override fun explain(p: Prescription) = "Bodyweight: ${p.sets}×${p.reps}. Quality reps."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Rep", "Add 2 reps when all sets complete", 2.0, 15)
}
object GenericBandedCoach : ExerciseCoach {
    override val id = "generic_banded"
    override val displayName = "Generic Banded Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("band", RegexOption.IGNORE_CASE), Regex("resistance", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsBand = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(3, "15-20", null, null, null, null, 45, "Banded movement. Control the tension.")
    }
    override fun explain(p: Prescription) = "Banded: ${p.sets}×${p.reps}. Control the tension."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Rep", "Add 5 reps when 20 reps achieved", 5.0, 20)
}
object GenericTimeBasedCoach : ExerciseCoach {
    override val id = "generic_time"
    override val displayName = "Generic Time-Based Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("hold", RegexOption.IGNORE_CASE), Regex("bridge", RegexOption.IGNORE_CASE), Regex("iso", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(3, "hold", null, 30.0, null, null, 45, "Hold. Breathe. Maintain position.")
    }
    override fun explain(p: Prescription) = "Hold: ${p.sets} × ${p.duration?.toInt()}s. Maintain."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Time", "Add 5 seconds when all sets complete", 5.0, 60)
}
object GenericIsolationCoach : ExerciseCoach {
    override val id = "generic_isolation"
    override val displayName = "Generic Isolation Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("cable", RegexOption.IGNORE_CASE), Regex("machine", RegexOption.IGNORE_CASE), Regex("raise", RegexOption.IGNORE_CASE), Regex("fly", RegexOption.IGNORE_CASE), Regex("crunch", RegexOption.IGNORE_CASE), Regex("extension", RegexOption.IGNORE_CASE), Regex("curl", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsWeight = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        val w = if (ctx.lastSession != null) ctx.lastSession.weight else 20.0
        return Prescription(3, "12-15", w, null, null, null, 45, "Isolation. Feel the target muscle. Squeeze.")
    }
    override fun explain(p: Prescription) = "Isolation: ${p.sets}×${p.reps} @ ${p.weight?.toInt()} lbs. Feel the target."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Linear", "Add 5 lbs when 15 reps achieved", 5.0, 15)
}
object GenericCardioCoach : ExerciseCoach {
    override val id = "generic_cardio"
    override val displayName = "Generic Cardio Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("cardio", RegexOption.IGNORE_CASE), Regex("condition", RegexOption.IGNORE_CASE), Regex("athletic", RegexOption.IGNORE_CASE), Regex("jump", RegexOption.IGNORE_CASE), Regex("burpee", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(1, "continuous", null, 600.0, null, null, 0, "Move. Stay in Zone 2-3.")
    }
    override fun explain(p: Prescription) = "Cardio: ${p.duration?.toInt()?.div(60)} min. Zone 2-3."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Time", "Add 5 minutes", 5.0, 1)
}
object GenericMobilityCoach : ExerciseCoach {
    override val id = "generic_mobility"
    override val displayName = "Generic Mobility Coach"
    override val supportedExerciseIds = emptySet<String>()
    override val supportedNamePatterns = listOf(Regex("stretch", RegexOption.IGNORE_CASE), Regex("mobility", RegexOption.IGNORE_CASE), Regex("foam", RegexOption.IGNORE_CASE), Regex("roll", RegexOption.IGNORE_CASE), Regex("floss", RegexOption.IGNORE_CASE), Regex("slide", RegexOption.IGNORE_CASE), Regex("wall", RegexOption.IGNORE_CASE), Regex("cat.?cow", RegexOption.IGNORE_CASE), Regex("world.?greatest", RegexOption.IGNORE_CASE))
    override fun requirements() = CoachRequirements(needsDuration = true)
    override fun prescribe(ctx: PrescriptionContext): Prescription {
        return Prescription(2, "hold", null, 30.0, null, null, 15, "Breathe into the stretch. Don't force the range.")
    }
    override fun explain(p: Prescription) = "Mobility: ${p.sets} × ${p.duration?.toInt()}s/side. Breathe."
    override fun progression(h: List<LoggedSet>) = ProgressionRule("Time", "Add 10 seconds when comfortable", 10.0, 60)
}
