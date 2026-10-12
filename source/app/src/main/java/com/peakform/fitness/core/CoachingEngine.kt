package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlin.math.roundToInt

/**
 * CoachingEngine — produces a coach-led prescription for a single exercise
 * given the user's training history, current fatigue, and stated goal.
 *
 * BATCH-8: a thin rules engine that overlays the existing prescription with
 * coach-style guidance: bump sets/reps if the user is fresh, hold steady if
 * the user is tired, drop volume if joints are flagged, and emit a short
 * coach message ("hold back", "push it", "form first") that the workout card
 * renders above the prescription.
 *
 * Rules are deterministic and side-effect-free — the engine NEVER writes to
 * ProState. The WorkoutScreen decides whether to apply the recommendation.
 */
object CoachingEngine {

    data class CoachInput(
        val exerciseId: String,
        val exerciseName: String,
        val lastWeight: Double?,          // lbs, most recent logged set
        val lastReps: Int?,               // reps of the most recent logged set
        val est1Rm: Double,               // estimated 1RM (lbs)
        val fatigue: Double,              // 0.0 (fresh) — 1.0 (smoked)
        val goal: String,                 // balanced | hypertrophy | strength | endurance | powerlifting
        val aggression: Double,           // 0.6 — 1.4, profile's aggression dial
        val recoveryDays: Int,             // days since this muscle was last trained
        val streakDays: Int,              // current streak
        val jointsFlagged: List<String>,  // joint ids the health screen flagged
        val periodPhase: String? = null,  // menstrual phase (luteal/follicular/etc.)
    )

    data class CoachPrescription(
        val sets: Int,
        val reps: Int,
        val weight: Double,
        val rpeTarget: Double,
        val message: String,              // coach-style one-liner
        val holdBack: Boolean,            // fatigue/joints say ease up
        val pushIt: Boolean,              // user is fresh + streak hot
        val tuneTarget: Boolean,          // technique work over weight
    )

    /** Run the rules. Pure function — no side effects, safe to call from any thread. */
    fun prescribe(input: CoachInput): CoachPrescription {
        val fatigue = input.fatigue.coerceIn(0.0, 1.0)
        val aggression = input.aggression.coerceIn(0.6, 1.4)

        // Base prescription from the legacy progression curve.
        val baseWeight = (input.lastWeight ?: (input.est1Rm * 0.7)).let { w ->
            if (w <= 0) input.est1Rm * 0.6 else w
        }
        val baseReps = input.lastReps ?: 8
        val baseSets = when (input.goal) {
            "strength", "powerlifting" -> 5
            "endurance" -> 3
            else -> 4
        }
        val baseRpe = if (input.goal == "strength") 8.5 else 7.5

        // Joint flags = drop load 10% and pause progression.
        val jointFactor = if (input.jointsFlagged.isNotEmpty()) 0.9 else 1.0
        // Period phase multiplier (luteal → 1.0, follicular → 1.05, menstrual → 0.8, ovulation → 1.0).
        val phaseFactor = when (input.periodPhase) {
            "follicular" -> 1.05
            "menstrual" -> 0.8
            else -> 1.0
        }
        // Fatigue scaling: 0% → 1.0, 50% → 0.9, 100% → 0.7.
        val fatigueFactor = (1.0 - 0.3 * fatigue).coerceIn(0.7, 1.0)
        // Recovery: 0 days = still recovering (0.9), 2+ days = full strength (1.0).
        val recoveryFactor = when {
            input.recoveryDays < 1 -> 0.85
            input.recoveryDays < 2 -> 0.95
            else -> 1.0
        }

        val rawFactor = jointFactor * phaseFactor * fatigueFactor * recoveryFactor * aggression
        val targetWeight = (baseWeight * rawFactor).roundToInt().coerceAtLeast(5).toDouble()
        val rpeTarget = (baseRpe + (aggression - 1.0) * 1.5).coerceIn(5.0, 9.5)

        // Volume: bump sets by 1 when fresh + recovering, drop by 1 when smoked.
        val setAdjust = when {
            fatigue < 0.25 && input.recoveryDays >= 2 -> +1
            fatigue > 0.65 || input.jointsFlagged.isNotEmpty() -> -1
            else -> 0
        }
        val sets = (baseSets + setAdjust).coerceIn(2, 6)

        // Message bank — short, coach-style.
        val pushIt = fatigue < 0.25 && input.recoveryDays >= 2 && input.streakDays >= 3
        val holdBack = fatigue > 0.65 || input.jointsFlagged.isNotEmpty() ||
            input.periodPhase == "menstrual"
        val tuneTarget = input.jointsFlagged.isNotEmpty() || input.recoveryDays < 1
        val message = when {
            tuneTarget && input.jointsFlagged.isNotEmpty() -> "Form first — your ${input.jointsFlagged.first()} flagged. Drop the load."
            holdBack -> "Ease back today — fatigue is high."
            pushIt -> "Push it — you're fresh and your streak is hot."
            input.recoveryDays < 1 -> "Same muscle group yesterday — go lighter, hold the reps."
            input.streakDays in 1..2 -> "Build momentum — clean reps, no PRs today."
            else -> "Steady — hit the prescription."
        }

        ProLog.i("COACH", "prescribe(${input.exerciseName}): w=$targetWeight r=$baseReps s=$sets rpe=$rpeTarget msg=$message")
        return CoachPrescription(
            sets = sets,
            reps = baseReps,
            weight = targetWeight.toDouble(),
            rpeTarget = rpeTarget,
            message = message,
            holdBack = holdBack,
            pushIt = pushIt,
            tuneTarget = tuneTarget,
        )
    }
}
