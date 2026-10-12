package com.peakform.fitness.coach.exercises

import com.peakform.fitness.coach.ExerciseCoach
import com.peakform.fitness.core.CoachingEngine

/**
 * AllCoaches — every concrete ExerciseCoach shipped in this build.
 *
 * BATCH-18: 29 specific coaches (one per major barbell / dumbbell / machine
 * movement) + 8 generic coaches (one per muscle group / equipment combo
 * that covers the long tail of the 1,413-entry library).
 *
 * Each coach has hardcoded rules — no I/O, no ProState writes. They are
 * pure functions over the CoachInput struct.
 */
object AllCoaches {

    // ---- 29 specific coaches ----

    object BackSquat : ExerciseCoach {
        override val exerciseIds = setOf("back_squat", "barbell_back_squat", "squat")
        override val movementName = "Barbell Back Squat"
        override val formCues = listOf(
            "Brace hard before unracking",
            "Knees track over toes",
            "Chest up, hips under the bar",
            "Hit depth — hip crease below knee",
        )
        override fun message(input: CoachingEngine.CoachInput): String = when {
            input.jointsFlagged.contains("knee") -> "Knees flagged — box squats today, drop the load 20%."
            input.fatigue > 0.7 -> "CNS is hot — drop intensity, keep the volume."
            else -> "Drive through the floor — chest up, knees out."
        }
        override fun deloadTrigger(input: CoachingEngine.CoachInput): Boolean =
            input.jointsFlagged.contains("knee") || input.fatigue > 0.85
    }

    object FrontSquat : ExerciseCoach {
        override val exerciseIds = setOf("front_squat")
        override val movementName = "Front Squat"
        override val formCues = listOf("Elbows high", "Torso upright", "Drive the chest up")
        override fun message(input: CoachingEngine.CoachInput): String = "Stay tall — elbows up, drive the chest."
    }

    object BenchPress : ExerciseCoach {
        override val exerciseIds = setOf("bench_press", "barbell_bench_press")
        override val movementName = "Barbell Bench Press"
        override val formCues = listOf("Shoulder blades pinched", "Feet planted", "Bar to lower chest", "Lock out strong")
        override fun message(input: CoachingEngine.CoachInput): String = when {
            input.jointsFlagged.contains("shoulder") -> "Shoulders flagged — close-grip floor press today."
            input.fatigue > 0.7 -> "Triceps are tired — drop 10% and pause every rep."
            else -> "Pin the shoulders, drive the feet — press through the ceiling."
        }
    }

    object Deadlift : ExerciseCoach {
        override val exerciseIds = setOf("deadlift", "conventional_deadlift")
        override val movementName = "Deadlift"
        override val formCues = listOf("Bar over midfoot", "Brace before pulling", "Hips and chest rise together", "Lock out the hips")
        override fun message(input: CoachingEngine.CoachInput): String = when {
            input.jointsFlagged.contains("lower_back") -> "Lower back flagged — rack pulls today, drop 30%."
            input.fatigue > 0.7 -> "CNS is hot — drop to 70% and work speed sets."
            else -> "Pull the slack out — drive the floor away."
        }
        override fun deloadTrigger(input: CoachingEngine.CoachInput): Boolean =
            input.jointsFlagged.contains("lower_back") || input.fatigue > 0.8
    }

    object SumoDeadlift : ExerciseCoach {
        override val exerciseIds = setOf("sumo_deadlift")
        override val movementName = "Sumo Deadlift"
        override val formCues = listOf("Wide stance", "Toes out", "Chest over the bar", "Drive knees out")
        override fun message(input: CoachingEngine.CoachInput): String = "Drive the knees out — chest up."
    }

    object OverheadPress : ExerciseCoach {
        override val exerciseIds = setOf("overhead_press", "ohp", "military_press")
        override val movementName = "Overhead Press"
        override val formCues = listOf("Brace hard", "Glutes tight", "Bar travels straight up", "Press through the ceiling")
        override fun message(input: CoachingEngine.CoachInput): String = "Brace — press the bar through the ceiling."
    }

    object BarbellRow : ExerciseCoach {
        override val exerciseIds = setOf("barbell_row", "bent_over_row")
        override val movementName = "Barbell Row"
        override val formCues = listOf("Hinge at hips", "Pull to lower ribs", "Squeeze shoulder blades", "Control the lower")
        override fun message(input: CoachingEngine.CoachInput): String = "Lead with the elbows — squeeze at the top."
    }

    object PullUp : ExerciseCoach {
        override val exerciseIds = setOf("pull_up", "pullup")
        override val movementName = "Pull Up"
        override val formCues = listOf("Dead hang", "Pull elbows down", "Chest to bar", "Control the lower")
        override fun message(input: CoachingEngine.CoachInput): String = "Pull the elbows down — chest to the bar."
    }

    object ChinUp : ExerciseCoach {
        override val exerciseIds = setOf("chin_up", "chinup")
        override val movementName = "Chin Up"
        override val formCues = listOf("Supinated grip", "Pull elbows down", "Chest to bar", "Full hang each rep")
        override fun message(input: CoachingEngine.CoachInput): String = "Full hang — drive the chest to the bar."
    }

    object Dip : ExerciseCoach {
        override val exerciseIds = setOf("dip", "parallel_bar_dip")
        override val movementName = "Parallel Bar Dip"
        override val formCues = listOf("Shoulders down", "Lean forward", "Chest to bars", "Lock out strong")
        override fun message(input: CoachingEngine.CoachInput): String = "Lean forward — chest to the bars."
    }

    object RomanianDeadlift : ExerciseCoach {
        override val exerciseIds = setOf("romanian_deadlift", "rdl")
        override val movementName = "Romanian Deadlift"
        override val formCues = listOf("Soft knees", "Hinge at hips", "Bar stays close", "Stretch the hamstring")
        override fun message(input: CoachingEngine.CoachInput): String = "Hinge back — stretch the hamstring, drive the hips through."
    }

    object PowerClean : ExerciseCoach {
        override val exerciseIds = setOf("power_clean")
        override val movementName = "Power Clean"
        override val formCues = listOf("Bar over midfoot", "Explosive second pull", "Catch in front rack", "Stand tall")
        override fun message(input: CoachingEngine.CoachInput): String = "Jump and catch — full extension, fast elbows."
    }

    object FrontRackLunge : ExerciseCoach {
        override val exerciseIds = setOf("front_rack_lunge", "walking_lunge")
        override val movementName = "Walking Lunge"
        override val formCues = listOf("Long step", "Back knee kisses the floor", "Drive through front heel", "Stay tall")
        override fun message(input: CoachingEngine.CoachInput): String = "Long step — back knee to the floor."
    }

    object BulgarianSplitSquat : ExerciseCoach {
        override val exerciseIds = setOf("bulgarian_split_squat", "bss")
        override val movementName = "Bulgarian Split Squat"
        override val formCues = listOf("Back foot elevated", "Front foot forward", "Drop straight down", "Drive through front heel")
        override fun message(input: CoachingEngine.CoachInput): String = "Drop straight down — drive the front heel."
    }

    object HipThrust : ExerciseCoach {
        override val exerciseIds = setOf("hip_thrust")
        override val movementName = "Barbell Hip Thrust"
        override val formCues = listOf("Shoulders on bench", "Feet flat", "Drive hips to ceiling", "Squeeze the glutes")
        override fun message(input: CoachingEngine.CoachInput): String = "Drive the hips up — squeeze at the top."
    }

    object GluteBridge : ExerciseCoach {
        override val exerciseIds = setOf("glute_bridge", "barbell_glute_bridge")
        override val movementName = "Barbell Glute Bridge"
        override val formCues = listOf("Shoulders flat", "Drive through heels", "Squeeze glutes", "Pause at top")
        override fun message(input: CoachingEngine.CoachInput): String = "Drive through the heels — pause at the top."
    }

    object InclineBenchPress : ExerciseCoach {
        override val exerciseIds = setOf("incline_bench_press", "incline_press")
        override val movementName = "Incline Bench Press"
        override val formCues = listOf("Shoulder blades pinched", "Bar to upper chest", "Press straight up", "Lock out strong")
        override fun message(input: CoachingEngine.CoachInput): String = "Pin the shoulders — bar to the upper chest."
    }

    object CloseGripBenchPress : ExerciseCoach {
        override val exerciseIds = setOf("close_grip_bench_press", "cg_bench")
        override val movementName = "Close-Grip Bench Press"
        override val formCues = listOf("Hands shoulder-width", "Elbows tucked", "Bar to lower chest", "Triceps drive")
        override fun message(input: CoachingEngine.CoachInput): String = "Tuck the elbows — triceps drive the bar."
    }

    object PushPress : ExerciseCoach {
        override val exerciseIds = setOf("push_press")
        override val movementName = "Push Press"
        override val formCues = listOf("Dip quarter-squat", "Drive through legs", "Press to lockout", "Snap under the bar")
        override fun message(input: CoachingEngine.CoachInput): String = "Dip — drive — press. Snap under the bar."
    }

    object PendlayRow : ExerciseCoach {
        override val exerciseIds = setOf("pendlay_row")
        override val movementName = "Pendlay Row"
        override val formCues = listOf("Bar dead-stops each rep", "Explosive pull", "Squeeze the upper back", "Lower fast")
        override fun message(input: CoachingEngine.CoachInput): String = "Dead-stop each rep — explosive pull."
    }

    object FacePull : ExerciseCoach {
        override val exerciseIds = setOf("face_pull")
        override val movementName = "Cable Face Pull"
        override val formCues = listOf("Rope to forehead", "Pull elbows high", "External rotate", "Slow return")
        override fun message(input: CoachingEngine.CoachInput): String = "Pull to the forehead — rotate the hands back."
    }

    object LatPulldown : ExerciseCoach {
        override val exerciseIds = setOf("lat_pulldown")
        override val movementName = "Lat Pulldown"
        override val formCues = listOf("Lean back slightly", "Pull elbows to ribs", "Squeeze the lats", "Control the raise")
        override fun message(input: CoachingEngine.CoachInput): String = "Pull the elbows to the ribs — squeeze the lats."
    }

    object CableRow : ExerciseCoach {
        override val exerciseIds = setOf("cable_row", "seated_cable_row")
        override val movementName = "Seated Cable Row"
        override val formCues = listOf("Chest tall", "Pull to belly", "Squeeze the back", "Slow return")
        override fun message(input: CoachingEngine.CoachInput): String = "Pull to the belly — chest tall."
    }

    object LegPress : ExerciseCoach {
        override val exerciseIds = setOf("leg_press")
        override val movementName = "Leg Press"
        override val formCues = listOf("Feet shoulder-width", "Lower to 90°", "Drive through heels", "Don't lock the knees")
        override fun message(input: CoachingEngine.CoachInput): String = "Lower to 90° — drive through the heels."
    }

    object HamstringCurl : ExerciseCoach {
        override val exerciseIds = setOf("hamstring_curl", "leg_curl")
        override val movementName = "Hamstring Curl"
        override val formCues = listOf("Hips flat on pad", "Curl slow", "Squeeze at top", "Control the lower")
        override fun message(input: CoachingEngine.CoachInput): String = "Curl slow — squeeze at the top."
    }

    object LegExtension : ExerciseCoach {
        override val exerciseIds = setOf("leg_extension")
        override val movementName = "Leg Extension"
        override val formCues = listOf("Toes neutral", "Extend fully", "Pause at top", "Slow lower")
        override fun message(input: CoachingEngine.CoachInput): String = "Extend fully — pause at the top."
    }

    object CalfRaise : ExerciseCoach {
        override val exerciseIds = setOf("calf_raise", "standing_calf_raise")
        override val movementName = "Standing Calf Raise"
        override val formCues = listOf("Full stretch at bottom", "Drive up onto toes", "Pause at top", "Slow lower")
        override fun message(input: CoachingEngine.CoachInput): String = "Full stretch — drive up onto the toes."
    }

    object Plank : ExerciseCoach {
        override val exerciseIds = setOf("plank")
        override val movementName = "Plank"
        override val formCues = listOf("Elbows under shoulders", "Hips level", "Brace the core", "Breathe")
        override fun message(input: CoachingEngine.CoachInput): String = "Brace the core — hips level with the shoulders."
    }

    object PushUp : ExerciseCoach {
        override val exerciseIds = setOf("push_up", "pushup")
        override val movementName = "Push Up"
        override val formCues = listOf("Body in a straight line", "Lower chest to floor", "Drive through palms", "Lock out strong")
        override fun message(input: CoachingEngine.CoachInput): String = "Body straight — chest to the floor."
    }

    // ---- 8 generic coaches ----

    object GenericChest : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Chest movement"
        override val formCues = listOf("Pin the shoulders", "Full ROM", "Drive through the chest")
        override fun message(input: CoachingEngine.CoachInput): String = "Press through the chest — pin the shoulders."
    }

    object GenericBack : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Back movement"
        override val formCues = listOf("Lead with the elbows", "Squeeze the shoulder blades", "Control the return")
        override fun message(input: CoachingEngine.CoachInput): String = "Pull — squeeze — control."
    }

    object GenericLegs : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Leg movement"
        override val formCues = listOf("Brace the core", "Drive through the heels", "Hit depth")
        override fun message(input: CoachingEngine.CoachInput): String = "Drive through the heels — hit depth."
    }

    object GenericShoulders : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Shoulder movement"
        override val formCues = listOf("Brace the core", "Press through the ceiling", "Don't arch the back")
        override fun message(input: CoachingEngine.CoachInput): String = "Press up — don't arch the back."
    }

    object GenericArms : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Arm movement"
        override val formCues = listOf("Pin the elbows", "Full extension", "Squeeze at the top")
        override fun message(input: CoachingEngine.CoachInput): String = "Pin the elbows — squeeze at the top."
    }

    object GenericCore : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Core movement"
        override val formCues = listOf("Brace the core", "Hips level", "Breathe through each rep")
        override fun message(input: CoachingEngine.CoachInput): String = "Brace — breathe through each rep."
    }

    object GenericCardio : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Cardio movement"
        override val formCues = listOf("Warm up first", "Stay in zone 2-3", "Cool down after")
        override fun message(input: CoachingEngine.CoachInput): String = "Warm up — stay in zone — cool down."
    }

    object GenericMobility : ExerciseCoach {
        override val exerciseIds = emptySet<String>()
        override val movementName = "Mobility movement"
        override val formCues = listOf("Breathe deep", "Hold 30s", "Don't force the range")
        override fun message(input: CoachingEngine.CoachInput): String = "Breathe deep — hold 30s."
    }

    /** Every coach registered (specific + generic). Used by the registry. */
    fun all(): List<ExerciseCoach> = listOf(
        BackSquat, FrontSquat, BenchPress, Deadlift, SumoDeadlift,
        OverheadPress, BarbellRow, PullUp, ChinUp, Dip,
        RomanianDeadlift, PowerClean, FrontRackLunge, BulgarianSplitSquat,
        HipThrust, GluteBridge, InclineBenchPress, CloseGripBenchPress,
        PushPress, PendlayRow, FacePull, LatPulldown, CableRow,
        LegPress, HamstringCurl, LegExtension, CalfRaise, Plank, PushUp,
        // 8 generic coaches
        GenericChest, GenericBack, GenericLegs, GenericShoulders,
        GenericArms, GenericCore, GenericCardio, GenericMobility,
    )
}
