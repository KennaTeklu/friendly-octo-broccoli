package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import com.peakform.fitness.engine.Stats
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * HealthScreen — legacy HEALTH SCREEN v2 (10-physician panel), verbatim rules.
 * Storage: p4_health_screened_at / p4_health_tier / p4_health_general /
 * p4_health_followup / p4_health_joints / p4_health_unlocked_at / p4_health_declared.
 * Pure decision logic — unit-tested in HealthScreenTest.
 */
object HealthScreen {

    data class Question(val id: String, val text: String, val important: Boolean)

    // Step 1 — General (7 questions, exact legacy copy)
    val GENERAL = listOf(
        Question("g1", "Has a doctor said you have a heart condition or high blood pressure?", true),
        Question("g2", "Do you have chest pain at rest, or during daily activity or exercise?", true),
        Question("g3", "Have you had dizziness or loss of consciousness in the last 12 months?", true),
        Question("g4", "Do you have a bone, joint, or back problem that could get worse with exercise?", false),
        Question("g5", "Are you taking medication for blood pressure, a heart condition, or a blood thinner?", false),
        Question("g6", "Are you pregnant, or have you given birth in the last 6 months?", false),
        Question("g7", "Has any doctor ever told you to avoid or restrict exercise for any other reason?", true),
    )

    // Step 2 — Follow-ups (max one level)
    val FOLLOWUPS = mapOf(
        "fu_heart" to listOf(
            Question("h1", "Has your doctor limited your activity, or said your condition is not well-controlled?", true),
            Question("h2", "Have you had a heart attack, heart failure, or a cardiac procedure in the last 3 months?", true),
        ),
        "fu_meds" to listOf(
            Question("m1", "Have you started or changed this medication in the last month?", true),
        ),
        "fu_preg" to listOf(
            Question("p1", "Has your obstetric provider cleared you for moderate exercise?", true),
        ),
    )

    val JOINT_OPTIONS = listOf("Shoulder", "Elbow", "Wrist", "Lower back", "Hip", "Knee", "Ankle", "Neck")

    data class Verdict(val tier: String, val reasons: List<String>, val followup: String?) {
        val title: String
            get() = when (tier) {
                "A" -> "A: Clear to train"
                "B" -> "B: Clear with modification"
                else -> "C: Sign-off required"
            }
        val description: String
            get() = when (tier) {
                "A" -> "No restrictions found. Full workout plan will be generated."
                else -> reasons.firstOrNull() ?: ""
            }
    }

    const val WAIVER_TEXT =
        "I accept the Liability Waiver & Assumption of Risk, and I understand this screen is not a medical exam or a doctor's clearance."
    const val RECOMMENDATION =
        "One last thing — This screen is not a medical exam and does not diagnose anything. If you feel unwell on the day, " +
        "or if anything here leaves you with doubt, delay the workout and talk to your doctor — that is always a reasonable choice, even when nothing was flagged."
    const val LOCK_BUTTON = "Locked — see note below"
    const val LOCK_NOTE =
        "Workout generation is paused. Your health screen flagged an item that needs a doctor's sign-off. " +
        "You can still browse Library, History, and Settings."
    const val LOCK_TOAST = "Confirm doctor clearance in Settings to unlock."

    /** Verdict computation — legacy computeTier (64708) transcribed exactly. */
    fun computeTier(general: Map<String, Boolean>, followup: Map<String, Boolean>): Verdict {
        val reasons = mutableListOf<String>()
        var tier = "A"
        fun raise(t: String, reason: String) {
            if (t == "C" && tier != "C") { tier = "C"; reasons.add(reason) }
            else if (t == "B" && tier == "A") { tier = "B"; reasons.add(reason) }
            else if (t == "C" && tier == "B") { tier = "C"; reasons.add(0, reason) }
        }
        var pending: String? = null
        if (general["g2"] == true) raise("C", "Chest pain at rest or during activity")
        if (general["g3"] == true) raise("C", "Dizziness or loss of consciousness")
        if (general["g7"] == true) raise("C", "Other physician restriction stated")
        if (general["g1"] == true) {
            if (followup["h1"] == true) raise("C", "Physician has limited your activity")
            else if (followup["h2"] == true) raise("C", "Recent cardiac event or procedure")
            else if (followup["h1"] == false && followup["h2"] == false) raise("B", "Stable cardiac history — intensity ceiling applied")
            else pending = "fu_heart"
        }
        if (general["g4"] == true) raise("B", "Joint modifications apply — movements loading flagged joints are adjusted")
        if (general["g5"] == true) {
            if (followup["m1"] == true) raise("C", "Medication started or changed in the last month")
            else if (followup["m1"] == false) raise("B", "Medication-managed — intensity ceiling applied")
            else if (pending == null) pending = "fu_meds"
        }
        if (general["g6"] == true) {
            if (followup["p1"] == true) raise("B", "Prenatal-safe programming active")
            else if (followup["p1"] == false) raise("C", "Obstetric clearance required before exercise")
            else if (pending == null) pending = "fu_preg"
        }
        return Verdict(tier, reasons, pending)
    }

    fun jointModifications(joints: List<String>): String? =
        if (joints.isEmpty()) null else "Joint modifications: ${joints.joinToString(", ")}"

    // ---- persistence ----
    fun save(context: Context, tier: String, general: Map<String, Boolean>, followup: Map<String, Boolean>, joints: List<String>) {
        fun obj(m: Map<String, Boolean>) = JsonObject(m.mapValues { JsonPrimitive(it.value) })
        ProPrefs.put(context, "p4_health_tier", tier)
        ProPrefs.put(context, "p4_health_general", ProJson.json.encodeToString(JsonObject.serializer(), obj(general)))
        ProPrefs.put(context, "p4_health_followup", ProJson.json.encodeToString(JsonObject.serializer(), obj(followup)))
        ProPrefs.put(context, "p4_health_joints", ProJson.json.encodeToString(
            JsonArray.serializer(), JsonArray(joints.map { JsonPrimitive(it) })))
        ProPrefs.put(context, "p4_health_screened_at", System.currentTimeMillis().toString())
    }

    fun tier(context: Context): String? = ProPrefs.get(context, "p4_health_tier")

    fun screenedAt(context: Context): Long? = ProPrefs.get(context, "p4_health_screened_at")?.toLongOrNull()

    fun isLocked(context: Context): Boolean =
        tier(context) == "C" && ProPrefs.get(context, "p4_health_unlocked_at") == null

    fun recordClearance(context: Context) {
        ProPrefs.put(context, "p4_health_unlocked_at", System.currentTimeMillis().toString())
    }

    fun reScreenNeeded(context: Context): Boolean {
        val at = screenedAt(context) ?: return true
        // re-screen on >90-day layoff
        val days = (System.currentTimeMillis() - at) / (1000.0 * 60 * 60 * 24)
        return days > 90
    }
}

/**
 * StreakFire — legacy updateStreakFire (59528): fire emoji decay + recovery at streak=0.
 * Intensity: full (≤2d) → dim (≤4d) → smoke (≤6d) → dead (streak 0 or >6d).
 */
object StreakFire {
    data class Fire(val intensity: String, val glyph: String, val opacity: Float, val title: String)

    fun of(streak: Int, daysSinceLastWorkout: Int): Fire {
        val intensity = when {
            streak == 0 -> "dead"
            daysSinceLastWorkout <= 2 -> "full"
            daysSinceLastWorkout <= 4 -> "dim"
            daysSinceLastWorkout <= 6 -> "smoke"
            else -> "dead"
        }
        return when (intensity) {
            "full" -> Fire("full", "🔥", 1f, "Tap for dashboard")
            "dim" -> Fire("dim", "🔥", 0.7f, "Last workout ${daysSinceLastWorkout}d ago — fire fading — tap for dashboard")
            "smoke" -> Fire("smoke", "🌫️", 0.5f, "Last workout ${daysSinceLastWorkout}d ago — fire fading — tap for dashboard")
            else -> if (streak == 0) Fire("dead", "❄️", 0.4f, "Streak lost — tap to share + recover")
                    else Fire("dead", "❄️", 0.4f, "Last workout ${daysSinceLastWorkout}d ago — tap for dashboard")
        }
    }

    fun daysSinceLast(data: WorkoutData, todayMillis: Long): Int {
        val last = data.workouts.mapNotNull { w -> if (w.date.isBlank()) null else ProState.utcDayMillis(w.date) }.maxOrNull()
            ?: return 99
        return ((todayMillis - last) / (1000.0 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }

    // ---- honor-system recovery (legacy openStreakRecoveryModal / bumpStreakAfterShare) ----
    const val SHARE_LINE = "Just crushed my workout with Pro 💪 — back at it today."

    fun recover(context: Context) {
        ProPrefs.put(context, "p4_streak_recovered_at", System.currentTimeMillis().toString())
        ProPrefs.put(context, "p4_streak_recover_count", ((ProPrefs.get(context, "p4_streak_recover_count")?.toIntOrNull() ?: 0) + 1).toString())
    }

    fun recoveryCount(context: Context): Int = ProPrefs.get(context, "p4_streak_recover_count")?.toIntOrNull() ?: 0

    /** A recovered streak counts as 1 day (honest: no fake history). */
    fun recoveredStreakBump(context: Context): Int =
        if (ProPrefs.get(context, "p4_streak_recovered_at") != null) 1 else 0
}

/**
 * ShareKit — legacy witty share-text generator (copyWorkoutLink, ~47k) with the
 * extracted verbatim phrase pools (share_pools.json) + QR payload.
 */
object ShareKit {
    private var pools: JsonObject? = null
    private var loaded = false

    fun ensure(context: Context) {
        if (loaded) return
        try {
            pools = Json.parseToJsonElement(context.assets.open("data/share_pools.json").bufferedReader().use { it.readText() }).jsonObject
        } catch (e: Exception) {
            ProLog.e("SHARE", "pools load failed: ${e.message}")
        }
        loaded = true
    }

    private fun pool(name: String): List<String> =
        pools?.get(name)?.jsonArray?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull } ?: emptyList()

    private fun pick(list: List<String>): String =
        if (list.isEmpty()) "" else list[kotlin.random.Random.nextInt(list.size)]

    data class Signals(
        val experience: String,
        val goal: String,
        val ageBand: String?,
        val streak: Int,
        val totalWorkouts: Int,
        val isDeload: Boolean,
        val isTaper: Boolean,
        val exerciseCount: Int,
        val muscleVariety: Int,
        val trend: String,
        val avgRPE: Double,
        val confidence: Int,
        val competitive: Int,
        val style: String,
    )

    fun signals(data: WorkoutData, workout: WorkoutRecord?, todayMillis: Long): Signals {
        val user = data.user
        val exp = user.experience.ifBlank { "intermediate" }
        val goal = user.goal.ifBlank { "balanced" }
        val ageBand = user.birthDate?.let { b ->
            val year = b.toIntOrNull(10)?.takeIf { b.length == 4 }
            val age = if (year != null) 2026 - year else null
            when {
                age == null -> null
                age < 30 -> "young"
                age < 50 -> "mid"
                else -> "senior"
            }
        }
        val streak = Stats.calculateStreak()
        val exerciseCount = workout?.exercises?.size ?: 0
        val muscleVariety = workout?.exercises?.flatMap { it.muscleGroup }?.distinct()?.size ?: 0

        // trend from key lifts (last 3 logged entries, legacy est1RM = w*(1+maxReps/30))
        var trend = "unknown"
        for (lift in listOf("squat", "bench_press", "deadlift", "overhead_press", "barbell_row")) {
            val rec = data.exercises[lift]
            val entries = rec?.history?.filter { !it.skipped && it.weight > 0 && it.reps.isNotEmpty() }?.takeLast(3)
            if (entries != null && entries.size >= 3) {
                val vals = entries.map { e -> e.weight * (1 + (e.reps.maxOrNull() ?: 0.0) / 30.0) }
                val slope = (vals[2] - vals[0]) / vals[0]
                trend = when {
                    slope > 0.03 -> "improving"
                    slope < -0.03 -> "declining"
                    else -> "plateau"
                }
                break
            }
        }

        val recent = data.workouts.takeLast(7)
        var rpeSum = 0.0; var rpeCount = 0
        for (w in recent) for (ex in w.exercises) {
            val r = ex.actual?.rpeList?.firstOrNull()
            if (r != null && r > 0) { rpeSum += r; rpeCount++ }
        }
        val avgRPE = if (rpeCount > 0) rpeSum / rpeCount else 5.0

        var confidence = 50
        when (exp) { "advanced" -> confidence += 20; "beginner" -> confidence -= 10 }
        when (trend) { "improving" -> confidence += 15; "declining" -> confidence -= 15 }
        if (goal == "strength") confidence += 10
        if (streak > 14) confidence += 10
        if (data.workouts.size > 100) confidence += 5
        if (muscleVariety > 3) confidence += 5
        confidence = confidence.coerceIn(0, 100)

        var competitive = 50
        if (exp == "advanced") competitive += 20
        if (goal == "strength") competitive += 20
        if (trend == "improving") competitive += 10
        if (exerciseCount > 5) competitive += 5
        competitive = competitive.coerceIn(0, 100)

        val style = when {
            confidence > 70 && competitive > 70 -> "competitive"
            confidence < 40 -> "humble"
            exp == "advanced" && goal == "hypertrophy" -> "analytical"
            avgRPE > 8 && workout?.isDeload != true -> "sarcastic"
            else -> "encouraging"
        }
        return Signals(exp, goal, ageBand, streak, data.workouts.size,
            workout?.isDeload ?: false, workout?.isTaper ?: false,
            exerciseCount, muscleVariety, trend, avgRPE, confidence, competitive, style)
    }

    /** Build the shareable text (legacy assemble step, verbatim pools). */
    fun buildShareText(context: Context, data: WorkoutData, workout: WorkoutRecord?, signals: Signals, todayMillis: Long): String {
        ensure(context)
        if (workout == null || workout.exercises.isEmpty()) return "No active workout."
        val date = java.text.SimpleDateFormat("EEEE, MMMM d, yyyy", java.util.Locale.US).format(java.util.Date(todayMillis))
        var text = "🏋️ WORKOUT: ${workout.name.ifBlank { workout.type.ifBlank { "Today's Workout" } }}\n"
        text += "📅 Date: $date\n\n"
        text += "📋 EXERCISES:\n"
        workout.exercises.forEachIndexed { idx, ex ->
            val muscles = if (ex.muscleGroup.isNotEmpty()) ex.muscleGroup.joinToString(", ") else "Various"
            text += "${idx + 1}. ${ex.name} – $muscles\n"
        }
        text += "\n"

        // history message (last vs today for key exercise)
        var historyMsg = ""
        val keyEx = workout.exercises.firstOrNull { ex ->
            val rec = data.exercises[ex.id]
            rec != null && rec.history.any { !it.skipped && it.weight > 0 && it.reps.isNotEmpty() }
        } ?: workout.exercises.firstOrNull()
        if (keyEx != null) {
            val rec = data.exercises[keyEx.id]
            val lastEntry = rec?.history?.lastOrNull { !it.skipped && it.weight > 0 && it.reps.isNotEmpty() }
            val todayWeight = keyEx.prescribed.weight
            if (lastEntry != null && todayWeight != null && todayWeight > 0) {
                val diff = todayWeight - lastEntry.weight
                val oldReps = lastEntry.reps.maxOrNull() ?: 0.0
                if (abs(diff) > 0.1) {
                    if (diff > 0) historyMsg += "Last time I did ${keyEx.name}, I used ${ProJson.formatNum(lastEntry.weight)} lbs for ${ProJson.formatNum(oldReps)} reps. Today I'm aiming for ${ProJson.formatNum(todayWeight)} lbs – that's ${ProJson.formatNum(diff)} lbs heavier. "
                    else historyMsg += "Last time I did ${keyEx.name}, I used ${ProJson.formatNum(lastEntry.weight)} lbs for ${ProJson.formatNum(oldReps)} reps. Today I'm using ${ProJson.formatNum(todayWeight)} lbs (${ProJson.formatNum(abs(diff))} lbs lighter). "
                } else {
                    historyMsg += "I've done ${keyEx.name} before (${ProJson.formatNum(lastEntry.weight)} lbs for ${ProJson.formatNum(oldReps)} reps). Same weight today – consistency builds strength. "
                }
            } else if (rec == null || rec.history.none { !it.skipped }) {
                historyMsg += "First time logging ${keyEx.name}. "
            }
        }

        val opener = pick(stylePools("openers", signals.style))
        val perfFollow = if (historyMsg.isNotBlank()) pick(pool("perfFollowNeu")) else ""
        val muscleNames = workout.exercises.flatMap { it.muscleGroup }.distinct()
            .joinToString(", ") { it.replace('_', ' ') }
        val firstMuscle = workout.exercises.flatMap { it.muscleGroup }.distinct()
            .map { it.replace('_', ' ') }
            .joinToString(", ").split(",").firstOrNull()?.trim() ?: "muscles"
        val firstTwoMuscles = workout.exercises.flatMap { it.muscleGroup }.distinct()
            .take(2).joinToString(", ") { it.replace('_', ' ') }
        val muscleSentence = if (muscleNames.isNotBlank())
            pick(pool("muscleSentences"))
                .replace("\${muscleGroupNames.split(',')[0]}", firstMuscle)
                .replace("\${muscleGroupNames.split(',').slice(0,2).join(',')}", firstTwoMuscles)
        else ""
        val estimatedTime = workout.exercises.size * 5 + 10
        val difficulty = when {
            workout.isDeload -> "Light / Deload"
            workout.isTaper -> "Taper / Peaking"
            else -> "Moderate-High"
        }
        val timeSentence = pick(pool("timeSentences"))
            .replace("{time}", estimatedTime.toString())
            .replace("{diff}", difficulty)
        val closer = pick(stylePools("closers", signals.style))

        var streakMsg = ""
        when {
            signals.streak >= 100 -> streakMsg = "💯 ${signals.streak} days in a row! That's legendary. "
            signals.streak >= 30 -> streakMsg = "${signals.streak} days in a row – that's dedication. "
            signals.streak >= 14 -> streakMsg = "A ${signals.streak}‑day streak! Consistency is paying off. "
            signals.streak >= 7 -> streakMsg = "One full week of training. I'm building a habit. "
            signals.streak >= 3 -> streakMsg = "${signals.streak} days straight – I'm on a roll. "
            signals.streak == 1 && signals.totalWorkouts == 1 -> streakMsg = "My very first workout logged. Let's make it a good one. "
        }

        val lotteryMsg = if (kotlin.random.Random.nextDouble() < 0.05) pick(pool("lotteryMessages")) + " " else ""
        val offTopicMsg = if (kotlin.random.Random.nextDouble() < 0.01) pick(pool("offTopicMessages")) + " " else ""

        text += "$historyMsg$opener $perfFollow\n\n$muscleSentence $timeSentence\n\n$streakMsg$lotteryMsg$offTopicMsg$closer\n\n💪 Generated by Pro Fitness App"
        return text
    }

    /** openers/closers are objects keyed by style — resolve with graceful fallback. */
    private fun stylePools(name: String, style: String): List<String> {
        val obj = pools?.get(name)?.jsonObject
        val arr = obj?.get(style)?.jsonArray
        if (arr != null) {
            val list = arr.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
            if (list.isNotEmpty()) return list
        }
        // fallback: flat arrays perfFollowPos/Neg/Neu shape → use openers-level default
        return pool(name).ifEmpty { pool("perfFollowNeu") }
    }

    fun plainWorkoutText(workout: WorkoutRecord): String {
        if (workout.exercises.isEmpty()) return "No active workout."
        var text = "🏋️ WORKOUT: ${workout.name.ifBlank { workout.type.ifBlank { "Today's Workout" } }}\n\n📋 EXERCISES:\n"
        workout.exercises.forEachIndexed { idx, ex ->
            val muscles = if (ex.muscleGroup.isNotEmpty()) ex.muscleGroup.joinToString(", ") else "Various"
            val w = ex.prescribed.weight?.let { " @ ${ProJson.formatNum(it)} lbs" } ?: ""
            text += "${idx + 1}. ${ex.name} – $muscles$w\n"
        }
        text += "\n💪 Generated by Pro Fitness App"
        return text
    }
}

/**
 * Period — My Cycle (legacy period tracking): phase computation, engine multipliers
 * (menstrual 0.6 / follicular 1.0 / ovulatory 1.05 / luteal 0.8), SmartMessageBank.
 */
object Period {
    const val MULTIPLIER_MENSTRUAL = 0.6
    const val MULTIPLIER_FOLLICULAR = 1.0
    const val MULTIPLIER_OVULATORY = 1.05
    const val MULTIPLIER_LUTEAL = 0.8

    fun phase(data: WorkoutData, todayMillis: Long): String? {
        val start = data.user.menstrual.lastPeriodStart ?: return null
        if (start.isBlank()) return null
        val startDay = ProState.utcDayMillis(start)
        if (startDay <= 0) return null
        val cycleLen = data.user.menstrual.cycleLength.coerceIn(20, 45)
        val dayInCycle = (((todayMillis - startDay) / (1000.0 * 60 * 60 * 24)).toInt() % cycleLen) + 1
        return when {
            dayInCycle <= 5 -> "menstrual"
            dayInCycle <= 13 -> "follicular"
            dayInCycle <= 16 -> "ovulatory"
            else -> "luteal"
        }
    }

    fun multiplierFor(phase: String?): Double = when (phase) {
        "menstrual" -> MULTIPLIER_MENSTRUAL
        "follicular" -> MULTIPLIER_FOLLICULAR
        "ovulatory" -> MULTIPLIER_OVULATORY
        "luteal" -> MULTIPLIER_LUTEAL
        else -> 1.0
    }

    fun savePeriodStart(context: Context, data: WorkoutData, isoDate: String) {
        ProState.data = data.copy(user = data.user.copy(
            menstrual = data.user.menstrual.copy(lastPeriodStart = isoDate)))
        ProState.saveWorkoutData()
    }

    // ---- SmartMessageBank ----
    private var bank: Map<String, List<Msg>> = emptyMap()
    private var loaded = false

    data class Msg(val text: String, val tone: String, val minSeverity: Int, val maxSeverity: Int)

    fun ensure(context: Context) {
        if (loaded) return
        try {
            val obj = Json.parseToJsonElement(context.assets.open("data/period_messages.json").bufferedReader().use { it.readText() }).jsonObject
            bank = obj.mapValues { (_, arr) ->
                arr.jsonArray.map { el ->
                    val o = el.jsonObject
                    Msg(
                        o["text"]?.jsonPrimitive?.contentOrNull ?: "",
                        o["tone"]?.jsonPrimitive?.contentOrNull ?: "gentle",
                        o["minSeverity"]?.jsonPrimitive?.intOrNull ?: 0,
                        o["maxSeverity"]?.jsonPrimitive?.intOrNull ?: 10,
                    )
                }
            }
        } catch (e: Exception) {
            ProLog.e("PERIOD", "bank load failed: ${e.message}")
        }
        loaded = true
    }

    /** Message for phase + severity(0-10) with no-repeat rotation (smartMessageHistory). */
    fun message(context: Context, phase: String?, severity: Int): Msg? {
        ensure(context)
        val list = bank[phase] ?: bank["any"] ?: return null
        val candidates = list.filter { severity in it.minSeverity..it.maxSeverity }.ifEmpty { list }
        if (candidates.isEmpty()) return null
        val historyRaw = ProPrefs.get(context, "smartMessageHistory")
        val used = historyRaw?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        val fresh = candidates.filter { it.text !in used }
        val msg = (fresh.ifEmpty { candidates }).let { it[kotlin.random.Random.nextInt(it.size)] }
        ProPrefs.put(context, "smartMessageHistory", (used + msg.text).toList().takeLast(15).joinToString(","))
        return msg
    }
}
