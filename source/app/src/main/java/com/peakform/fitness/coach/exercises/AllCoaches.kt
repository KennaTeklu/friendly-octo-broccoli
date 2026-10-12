package com.peakform.fitness.coach.exercises

import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.ExerciseCoach
import com.peakform.fitness.coach.RawTip
import com.peakform.fitness.coach.TipCategory
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.UserProfile

/**
 * AllCoaches — the 29 specific + 8 generic coaches, each with hardcoded rules.
 *
 * BATCH-18: every coach is a small object with 1-3 rules. The rules are
 * intentionally hardcoded — the goal is reliable, opinionated coaching advice
 * for the 29 most-trained lifts plus 8 generic tags that span the rest of
 * the library.
 *
 * Specific coaches key on the exercise id (e.g. "back_squat"); the registry
 * looks up the coach for an exercise record by its id. Generic coaches key
 * on a tag set (e.g. setOf("barbell")) and apply to every exercise whose
 * LibraryExercise.equipment matches.
 *
 * Register all coaches at app start with `AllCoaches.registerAll()`.
 */
object AllCoaches {

    fun registerAll() {
        // 29 specific coaches
        listOf(
            SquatCoach, BenchPressCoach, DeadliftCoach, OverheadPressCoach,
            BarbellRowCoach, PullUpCoach, ChinUpCoach, DipCoach,
            FrontSquatCoach, RomanianDeadliftCoach, SumoDeadliftCoach,
            InclineBenchPressCoach, LegPressCoach, LungeCoach,
            BicepCurlCoach, TricepExtensionCoach, LateralRaiseCoach,
            FacePullCoach, PlankCoach, HangingLegRaiseCoach,
            BarbellShrugCoach, CalfRaiseCoach, HipThrustCoach,
            KettlebellSwingCoach, PushUpCoach, DumbbellRowCoach,
            CableFlyCoach, LatPulldownCoach, PreacherCurlCoach,
        ).forEach(CoachRegistry::register)
        // 8 generic coaches
        listOf(
            BarbellCoach, DumbbellCoach, MachineCoach, CableCoach,
            BodyweightCoach, KettlebellCoach, BandCoach, CompoundCoach,
        ).forEach(CoachRegistry::register)
    }

    // ------------------------- helpers -------------------------

    /** Returns true if the record has a recent history entry with the given weight. */
    private fun lastEntry(rec: ExerciseRecord): com.peakform.fitness.core.HistoryEntry? =
        rec.history.lastOrNull { !it.skipped }

    private fun avgRpe(rec: ExerciseRecord): Double {
        val rpes = rec.history.mapNotNull { it.firstRPE }.filter { it > 0 }
        return if (rpes.isNotEmpty()) rpes.average() else 0.0
    }

    // ------------------------- 29 specific coaches -------------------------

    // 1. Squat — depth cue + knee tracking + bracing.
    object SquatCoach : ExerciseCoach {
        override val id = "back_squat"
        override val name = "Squat Coach"
        override val tags = setOf("barbell", "compound", "lower-body")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> {
            val out = mutableListOf<RawTip>()
            out.add(RawTip("depth", TipCategory.FORM, 2, "Hit depth", "Crease of hip below the top of the knee — every rep. Shallow reps build ego, not strength."))
            val last = lastEntry(record)
            if (last != null && last.weight > 0 && last.sets >= 5) {
                out.add(RawTip("volume", TipCategory.LOAD, 4, "5+ work sets logged", "Drop to 3 work sets next session — high-set squatting adds fatigue fast."))
            }
            if (avgRpe(record) >= 9) out.add(RawTip("rpe", TipCategory.RECOVERY, 3, "RPE climbing on squats", "Drop intensity 10% next session. Squats punish cumulative fatigue."))
            if (user.experience == "beginner") out.add(RawTip("brace", TipCategory.FORM, 1, "Brace before you descend", "Big breath into the belly, squeeze the core, then break at the hips and knees together."))
            return out
        }
    }

    // 2. Bench Press — bar path + shoulder blades + foot drive.
    object BenchPressCoach : ExerciseCoach {
        override val id = "bench_press"
        override val name = "Bench Press Coach"
        override val tags = setOf("barbell", "compound", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> {
            val out = mutableListOf<RawTip>()
            out.add(RawTip("scapula", TipCategory.FORM, 1, "Pin the shoulder blades", "Retract + depress — keep them pinned for every rep. A loose back is a weak bench."))
            out.add(RawTip("barpath", TipCategory.FORM, 2, "Bar over the lower chest", "Touch the bar to the sternum, press back up over the shoulders. Diagonal bar path, not vertical."))
            out.add(RawTip("legs", TipCategory.FORM, 3, "Drive through the floor", "Feet planted, heels down. Leg drive transfers force to the bar — don't leave it on the table."))
            val last = lastEntry(record)
            if (last != null && last.weight > 0 && last.reps.firstOrNull() == 1.0) {
                out.add(RawTip("singles", TipCategory.LOAD, 3, "Singles logged", "Mix in 3-rep sets too — singles alone don't build muscle, they just test strength."))
            }
            return out
        }
    }

    // 3. Deadlift — setup + back angle + lockout.
    object DeadliftCoach : ExerciseCoach {
        override val id = "deadlift"
        override val name = "Deadlift Coach"
        override val tags = setOf("barbell", "compound", "lower-body", "posterior-chain")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> {
            val out = mutableListOf<RawTip>()
            out.add(RawTip("setup", TipCategory.FORM, 1, "Pull the slack out", "Set your brace, then pull the bar tight against the plates before breaking it off the floor."))
            out.add(RawTip("back", TipCategory.SAFETY, 1, "Neutral spine — never round", "Chest tall, hips above knees. A rounded lower back under load is the #1 deadlift injury."))
            out.add(RawTip("lockout", TipCategory.FORM, 2, "Lock out with hips + glutes", "Don't hyperextend. Squeeze the glutes, stand tall — that's the rep."))
            val last = lastEntry(record)
            if (last != null && last.weight > 0 && last.sets >= 5) {
                out.add(RawTip("volume", TipCategory.RECOVERY, 2, "5+ deadlift work sets", "Deadlifts tax the CNS more than any other lift — drop to 1-2 heavy work sets next time."))
            }
            return out
        }
    }

    // 4. Overhead Press — hip drive + bar path + breathing.
    object OverheadPressCoach : ExerciseCoach {
        override val id = "overhead_press"
        override val name = "Overhead Press Coach"
        override val tags = setOf("barbell", "compound", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> {
            val out = mutableListOf<RawTip>()
            out.add(RawTip("brace", TipCategory.FORM, 1, "Brace hard before each rep", "Glutes squeezed, ribs down. A loose core leaks force — the press starts from the ground."))
            out.add(RawTip("barpath", TipCategory.FORM, 2, "Bar straight up — face back", "Move the head out of the way, press the bar straight up, then bring the head back through."))
            out.add(RawTip("breath", TipCategory.FORM, 3, "One breath per rep", "Re-brace at the bottom. Holding one breath across 5 reps weakens the last 2."))
            return out
        }
    }

    // 5. Barbell Row — back angle + elbow path.
    object BarbellRowCoach : ExerciseCoach {
        override val id = "barbell_row"
        override val name = "Barbell Row Coach"
        override val tags = setOf("barbell", "compound", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> {
            val out = mutableListOf<RawTip>()
            out.add(RawTip("hip", TipCategory.FORM, 1, "Hinge to ~45°", "Back flat, bar hanging just below the knees. Too upright = shrug; too flat = deadlift."))
            out.add(RawTip("elbow", TipCategory.FORM, 2, "Elbows tucked — brush the ribs", "Pull to the lower ribs, not the chest. Flared elbows turn the row into a rear-delt fly."))
            return out
        }
    }

    // 6. Pull-Up — full hang + scapula + tempo.
    object PullUpCoach : ExerciseCoach {
        override val id = "pull_up"
        override val name = "Pull-Up Coach"
        override val tags = setOf("bodyweight", "compound", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> {
            val out = mutableListOf<RawTip>()
            out.add(RawTip("scapula", TipCategory.FORM, 1, "Engage the scapula first", "From a dead hang, pull the shoulder blades down + back before bending the elbows."))
            out.add(RawTip("range", TipCategory.FORM, 2, "Chin over, full hang below", "Half-rep pull-ups build half-strength. Hit the top, hit the bottom."))
            out.add(RawTip("tempo", TipCategory.LOAD, 3, "3-second negative", "Lower under control — eccentric strength is where the muscle grows."))
            return out
        }
    }

    // 7. Chin-Up — grip + scapula.
    object ChinUpCoach : ExerciseCoach {
        override val id = "chin_up"
        override val name = "Chin-Up Coach"
        override val tags = setOf("bodyweight", "compound", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("grip", TipCategory.FORM, 2, "Shoulder-width supinated grip", "Too narrow overloads the biceps; too wide shifts the work away from the lats."),
            RawTip("scapula", TipCategory.FORM, 1, "Pull from the lats, not the arms", "Lead with the elbows — biceps assist; the back should do the heavy lifting."),
        )
    }

    // 8. Dip — depth + scapula.
    object DipCoach : ExerciseCoach {
        override val id = "dip"
        override val name = "Dip Coach"
        override val tags = setOf("bodyweight", "compound", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("depth", TipCategory.FORM, 1, "90° at the shoulder", "Don't go so deep the chest folds; don't shortcut to half-reps. Shoulder crease at 90° is the sweet spot."),
            RawTip("scapula", TipCategory.FORM, 2, "Don't sink into the shoulders", "Keep the shoulder blades set — sinking lets the load drift onto the anterior capsule."),
            RawTip("lean", TipCategory.FORM, 3, "Slight forward lean = chest", "Stay upright for triceps; lean 15° forward for chest emphasis."),
        )
    }

    // 9. Front Squat — elbow position + depth.
    object FrontSquatCoach : ExerciseCoach {
        override val id = "front_squat"
        override val name = "Front Squat Coach"
        override val tags = setOf("barbell", "compound", "lower-body")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("elbows", TipCategory.FORM, 1, "Elbows high — rack the bar", "Elbows up the whole time. If they drop, the bar rolls forward and the back rounds."),
            RawTip("upright", TipCategory.FORM, 2, "Stay upright", "More upright torso than back squat — front squat rewards a vertical chest."),
            RawTip("depth", TipCategory.FORM, 3, "Below parallel", "Elbows-to-knees depth. Shallow front squats build shallow quad strength."),
        )
    }

    // 10. Romanian Deadlift — hip hinge + tempo.
    object RomanianDeadliftCoach : ExerciseCoach {
        override val id = "romanian_deadlift"
        override val name = "Romanian Deadlift Coach"
        override val tags = setOf("barbell", "compound", "posterior-chain")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("hinge", TipCategory.FORM, 1, "Push hips back — not down", "RDL is a hip hinge, not a squat. Bar stays close to the thighs the whole way."),
            RawTip("stretch", TipCategory.FORM, 2, "Feel the hamstring stretch", "Stop just before the back rounds — that's where the muscle is loaded."),
            RawTip("tempo", TipCategory.LOAD, 3, "3-sec descent", "Slow eccentrics = more hamstring growth. Don't bounce the bottom."),
        )
    }

    // 11. Sumo Deadlift — stance + knee tracking.
    object SumoDeadliftCoach : ExerciseCoach {
        override val id = "sumo_deadlift"
        override val name = "Sumo Deadlift Coach"
        override val tags = setOf("barbell", "compound", "lower-body")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("stance", TipCategory.FORM, 1, "Toes out, knees track over them", "Wide stance, toes pointed out ~45°. Knees must follow the toes — don't let them cave."),
            RawTip("hip", TipCategory.FORM, 2, "Hips lower than conventional", "Sumo starts with the hips closer to the bar — vertical torso is the goal."),
        )
    }

    // 12. Incline Bench Press — bar path + shoulder blades.
    object InclineBenchPressCoach : ExerciseCoach {
        override val id = "incline_bench_press"
        override val name = "Incline Bench Press Coach"
        override val tags = setOf("barbell", "compound", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("angle", TipCategory.FORM, 1, "30° incline, not 45°", "Lower angle targets the upper chest without shifting load to the shoulders."),
            RawTip("scapula", TipCategory.FORM, 2, "Pin the shoulder blades", "Same as flat bench — retract + depress. A loose back kills the chest emphasis."),
        )
    }

    // 13. Leg Press — foot placement + depth.
    object LegPressCoach : ExerciseCoach {
        override val id = "leg_press"
        override val name = "Leg Press Coach"
        override val tags = setOf("machine", "compound", "lower-body")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("feet", TipCategory.FORM, 1, "Feet placement shifts emphasis", "High + wide = glutes/hamstrings; low + narrow = quads. Pick the one that matches the goal."),
            RawTip("depth", TipCategory.FORM, 2, "90° at the knee", "Stop short of bottoming out — protect the lower back from curling off the pad."),
            RawTip("lockout", TipCategory.FORM, 3, "Don't lock the knees", "Stop just short of full extension — keeps the load on the quads, not the joint."),
        )
    }

    // 14. Lunge — step length + knee tracking.
    object LungeCoach : ExerciseCoach {
        override val id = "lunge"
        override val name = "Lunge Coach"
        override val tags = setOf("bodyweight", "compound", "lower-body")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("step", TipCategory.FORM, 1, "Long step — glutes; short step — quads", "Step length changes emphasis. Pick the one that matches the goal."),
            RawTip("knee", TipCategory.FORM, 2, "Front knee tracks over the toe", "Don't let the knee cave in — push the knee out over the middle toe."),
        )
    }

    // 15. Bicep Curl — full extension + no swing.
    object BicepCurlCoach : ExerciseCoach {
        override val id = "bicep_curl"
        override val name = "Bicep Curl Coach"
        override val tags = setOf("dumbbell", "isolation", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("full", TipCategory.FORM, 1, "Full extension at the bottom", "Lock the elbow at the bottom — half-rep curls build half-biceps."),
            RawTip("swing", TipCategory.FORM, 2, "No hip swing", "Stand against a wall if you have to. The biceps move the weight, not the hips."),
            RawTip("supinate", TipCategory.FORM, 3, "Supinate at the top", "Twist the pinky outward at the top — peak contraction."),
        )
    }

    // 16. Tricep Extension — elbow position + tempo.
    object TricepExtensionCoach : ExerciseCoach {
        override val id = "tricep_extension"
        override val name = "Tricep Extension Coach"
        override val tags = setOf("cable", "isolation", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("elbow", TipCategory.FORM, 1, "Pin the elbows", "Elbows stay pinned to the sides — moving the elbows turns the extension into a press."),
            RawTip("squeeze", TipCategory.FORM, 2, "Squeeze at the bottom", "Full extension + 1-second hold — the long head only fires at full lockout."),
        )
    }

    // 17. Lateral Raise — lean + tempo.
    object LateralRaiseCoach : ExerciseCoach {
        override val id = "lateral_raise"
        override val name = "Lateral Raise Coach"
        override val tags = setOf("dumbbell", "isolation", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("lean", TipCategory.FORM, 1, "Slight forward lean", "5° forward — clears the front delt and isolates the medial head."),
            RawTip("tempo", TipCategory.LOAD, 2, "Lead with the elbows", "Elbows drive the movement, not the hands. Pinky slightly higher at the top."),
            RawTip("weight", TipCategory.LOAD, 3, "Lighter is better", "Lateral raises punish ego — drop the weight 5 lbs and the form fixes itself."),
        )
    }

    // 18. Face Pull — rope + tempo.
    object FacePullCoach : ExerciseCoach {
        override val id = "face_pull"
        override val name = "Face Pull Coach"
        override val tags = setOf("cable", "isolation", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("rope", TipCategory.FORM, 1, "Pull to the forehead", "Pull the rope toward the forehead, not the chest — external rotation at the top."),
            RawTip("tempo", TipCategory.LOAD, 2, "Slow + controlled", "Face pulls are posture work, not max-effort. 2-sec pull, 2-sec hold, 2-sec release."),
        )
    }

    // 19. Plank — neutral spine + breath.
    object PlankCoach : ExerciseCoach {
        override val id = "plank"
        override val name = "Plank Coach"
        override val tags = setOf("bodyweight", "isolation", "core")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("spine", TipCategory.FORM, 1, "Neutral spine — don't sag", "Hips level with the shoulders. Sagging = lumbar load; piking = avoidance."),
            RawTip("breath", TipCategory.FORM, 2, "Breathe through the brace", "Don't hold the breath — small sips of air maintain the brace."),
        )
    }

    // 20. Hanging Leg Raise — no swing + lower abs.
    object HangingLegRaiseCoach : ExerciseCoach {
        override val id = "hanging_leg_raise"
        override val name = "Hanging Leg Raise Coach"
        override val tags = setOf("bodyweight", "isolation", "core")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("swing", TipCategory.FORM, 1, "No swing — dead stop at the bottom", "Hanging leg raises with swing = momentum raises. Pause at the bottom."),
            RawTip("legs", TipCategory.FORM, 2, "Toes to the bar", "Full range — toes-to-bar activates the whole abdominal wall. Knee raises only hit the lower abs."),
        )
    }

    // 21. Barbell Shrug — full range + no roll.
    object BarbellShrugCoach : ExerciseCoach {
        override val id = "barbell_shrug"
        override val name = "Barbell Shrug Coach"
        override val tags = setOf("barbell", "isolation", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("range", TipCategory.FORM, 1, "Full range — up + down", "Don't roll the shoulders. Up = squeeze; down = full stretch. The roll is a shoulder-impingement machine."),
            RawTip("hold", TipCategory.LOAD, 2, "1-sec hold at the top", "Squeeze at the top — the trap only fires at peak contraction."),
        )
    }

    // 22. Calf Raise — full stretch + slow.
    object CalfRaiseCoach : ExerciseCoach {
        override val id = "calf_raise"
        override val name = "Calf Raise Coach"
        override val tags = setOf("machine", "isolation", "lower-body")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("stretch", TipCategory.FORM, 1, "Full stretch at the bottom", "Drop the heel below the step — the calf's stretch-shortening cycle is the whole point."),
            RawTip("hold", TipCategory.LOAD, 2, "1-sec hold at the top", "Squeeze at the top — otherwise you're just bouncing on the Achilles."),
        )
    }

    // 23. Hip Thrust — full lockout + chin tuck.
    object HipThrustCoach : ExerciseCoach {
        override val id = "hip_thrust"
        override val name = "Hip Thrust Coach"
        override val tags = setOf("barbell", "compound", "lower-body", "posterior-chain")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("lockout", TipCategory.FORM, 1, "Full lockout — squeeze the glutes", "Lock the hips out fully — the glute only fires at peak extension. Don't hyperextend the lower back."),
            RawTip("chin", TipCategory.FORM, 2, "Tuck the chin", "Look at the ceiling — neutral cervical spine. Looking forward strains the neck under load."),
        )
    }

    // 24. Kettlebell Swing — hip hinge + no squat.
    object KettlebellSwingCoach : ExerciseCoach {
        override val id = "kettlebell_swing"
        override val name = "Kettlebell Swing Coach"
        override val tags = setOf("kettlebell", "compound", "posterior-chain")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("hinge", TipCategory.FORM, 1, "Hip hinge — not a squat", "The bell swings back between the legs, then forward with hip snap. Don't squat the bell up."),
            RawTip("snap", TipCategory.FORM, 2, "Sharp hip snap", "Squeeze the glutes hard at the top — the bell floats. Arm is just a rope."),
        )
    }

    // 25. Push-Up — body line + elbow angle.
    object PushUpCoach : ExerciseCoach {
        override val id = "push_up"
        override val name = "Push-Up Coach"
        override val tags = setOf("bodyweight", "compound", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("line", TipCategory.FORM, 1, "Body line — head to heels straight", "Squeeze the glutes, brace the core. A sagging push-up is a back injury waiting to happen."),
            RawTip("elbow", TipCategory.FORM, 2, "45° elbow angle", "Don't flare to 90° — drops load onto the shoulder capsule. 45° protects the joint."),
            RawTip("range", TipCategory.FORM, 3, "Chest to the floor", "Full range — half-push-ups don't count. Hands on a bench if you need to."),
        )
    }

    // 26. Dumbbell Row — chest support + elbow path.
    object DumbbellRowCoach : ExerciseCoach {
        override val id = "dumbbell_row"
        override val name = "Dumbbell Row Coach"
        override val tags = setOf("dumbbell", "compound", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("support", TipCategory.FORM, 1, "Support the chest", "Hand + knee on a bench — frees the working side to pull without rotation."),
            RawTip("elbow", TipCategory.FORM, 2, "Pull to the hip", "Dumbbell to the hip, not the chest. Lat fibers run diagonally — match the line."),
        )
    }

    // 27. Cable Fly — slight elbow bend + squeeze.
    object CableFlyCoach : ExerciseCoach {
        override val id = "cable_fly"
        override val name = "Cable Fly Coach"
        override val tags = setOf("cable", "isolation", "upper-body-push")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("elbow", TipCategory.FORM, 1, "Slight elbow bend — fixed", "Locked elbow angle. Bending the elbows during the fly turns it into a press."),
            RawTip("squeeze", TipCategory.FORM, 2, "Squeeze at the front", "Hands meet in front of the chest — 1-sec hold, peak contraction."),
        )
    }

    // 28. Lat Pulldown — scapula + grip.
    object LatPulldownCoach : ExerciseCoach {
        override val id = "lat_pulldown"
        override val name = "Lat Pulldown Coach"
        override val tags = setOf("cable", "compound", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("scapula", TipCategory.FORM, 1, "Pull from the scapula first", "Depress the shoulder blades before bending the elbows — lat fires, not the arms."),
            RawTip("grip", TipCategory.FORM, 2, "Wide grip = upper lats; narrow = lower", "Pick the grip that matches the weak point — both have a place in the program."),
        )
    }

    // 29. Preacher Curl — full stretch + no swing.
    object PreacherCurlCoach : ExerciseCoach {
        override val id = "preacher_curl"
        override val name = "Preacher Curl Coach"
        override val tags = setOf("machine", "isolation", "upper-body-pull")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("stretch", TipCategory.FORM, 1, "Full extension at the bottom", "Let the arm fully extend over the pad — the long head fires hardest at full stretch."),
            RawTip("pad", TipCategory.FORM, 2, "Armpit on the pad", "Pad under the armpit, not the chest. Locks the shoulder so the bicep does all the work."),
        )
    }

    // ------------------------- 8 generic coaches -------------------------

    // G1. Barbell — applies to any exercise tagged "barbell".
    object BarbellCoach : ExerciseCoach {
        override val id = "generic_barbell"
        override val name = "Barbell Coach"
        override val tags = setOf("barbell")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("warmup", TipCategory.SAFETY, 2, "Warm up the barbell", "Two empty-bar sets before the working sets — prime the motor pattern + grease the groove."),
            RawTip("collars", TipCategory.SAFETY, 3, "Collars on, always", "Even on light sets — a shifting plate is a missed rep at best, an injury at worst."),
        )
    }

    // G2. Dumbbell — applies to any exercise tagged "dumbbell".
    object DumbbellCoach : ExerciseCoach {
        override val id = "generic_dumbbell"
        override val name = "Dumbbell Coach"
        override val tags = setOf("dumbbell")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("balance", TipCategory.FORM, 2, "Start with the weak side", "Lead with the non-dominant arm — it gets the freshest sets and catches up faster."),
            RawTip("grip", TipCategory.FORM, 3, "Neutral grip on the heavy sets", "Heavy dumbbell work with neutral grip spares the shoulder."),
        )
    }

    // G3. Machine — applies to any exercise tagged "machine".
    object MachineCoach : ExerciseCoach {
        override val id = "generic_machine"
        override val name = "Machine Coach"
        override val tags = setOf("machine")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("setup", TipCategory.FORM, 2, "Adjust the seat to your body", "One pin off = wrong joint axis. Take 10 seconds to set the seat — protects the joint."),
            RawTip("control", TipCategory.LOAD, 3, "Slow on the negative", "Machines let you cheat with momentum — slow the eccentric, more muscle."),
        )
    }

    // G4. Cable — applies to any exercise tagged "cable".
    object CableCoach : ExerciseCoach {
        override val id = "generic_cable"
        override val name = "Cable Coach"
        override val tags = setOf("cable")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("stack", TipCategory.FORM, 3, "Walk the stack back", "Step back from the stack — keeps tension on the muscle at the start. No slack, no rest."),
            RawTip("pin", TipCategory.SAFETY, 3, "Pin fully inserted", "Half-in pins strip the holes. Always push until the clip clicks."),
        )
    }

    // G5. Bodyweight — applies to any exercise tagged "bodyweight".
    object BodyweightCoach : ExerciseCoach {
        override val id = "generic_bodyweight"
        override val name = "Bodyweight Coach"
        override val tags = setOf("bodyweight")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("tempo", TipCategory.LOAD, 2, "Slow eccentric = harder", "Bodyweight too easy? Slow the negative — 4-sec descent is +30% muscle tension."),
            RawTip("leverage", TipCategory.LOAD, 3, "Change leverage, not reps", "Stuck at 20 reps? Change the leverage (decline / archer / 1-arm), not the rep count."),
        )
    }

    // G6. Kettlebell — applies to any exercise tagged "kettlebell".
    object KettlebellCoach : ExerciseCoach {
        override val id = "generic_kettlebell"
        override val name = "Kettlebell Coach"
        override val tags = setOf("kettlebell")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("grip", TipCategory.FORM, 2, "Hook grip — not death grip", "Bell rests in the hook of the fingers, not a squeezed fist — saves the forearm."),
            RawTip("hips", TipCategory.FORM, 3, "Hips do the work", "Every KB move is a hip hinge — arms are the rope, hips are the engine."),
        )
    }

    // G7. Band — applies to any exercise tagged "band".
    object BandCoach : ExerciseCoach {
        override val id = "generic_band"
        override val name = "Band Coach"
        override val tags = setOf("band")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("tension", TipCategory.LOAD, 2, "Anchor distance sets the tension", "Step back to add tension, forward to remove it — bands have no fixed weight."),
            RawTip("slow", TipCategory.LOAD, 3, "Slow the eccentric", "Band tension drops at the bottom — slow the negative to keep the load on the muscle."),
        )
    }

    // G8. Compound — applies to any exercise tagged "compound".
    object CompoundCoach : ExerciseCoach {
        override val id = "generic_compound"
        override val name = "Compound Coach"
        override val tags = setOf("compound")
        override fun evaluate(record: ExerciseRecord, user: UserProfile): List<RawTip> = listOf(
            RawTip("warmup", TipCategory.SAFETY, 1, "Warm up like you mean it", "Compound lifts punish cold joints — 5 min cardio + 2 ramp-up sets minimum."),
            RawTip("technique", TipCategory.FORM, 2, "Technique over weight", "Add weight only when every rep looks the same. Sloppy reps = injuries + ego."),
            RawTip("rest", TipCategory.RECOVERY, 3, "3-min rest between heavy sets", "ATP-PC system needs 3 minutes to refill. Shorter rest = lower quality, not more gains."),
        )
    }
}
