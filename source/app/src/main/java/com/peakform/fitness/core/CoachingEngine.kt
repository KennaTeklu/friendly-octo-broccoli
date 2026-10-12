package com.peakform.fitness.core

import android.content.Context

/**
 * CoachingEngine — BATCH-14: a thin, profile-scoped engine that turns the
 * user's last session feel + 1RM cache into a next-session prescription hint.
 *
 * Scope: this engine does NOT replace Generator. It runs alongside it and
 * surfaces a "coach's note" the workout screen can show above the first
 * exercise — e.g. "Last session felt hard (RPE 9). Pull back ~10% volume."
 *
 * Storage: every read/write routes through ProfileSettings (per-profile
 * SharedPreferences). Switching profiles gives the engine a fresh slate —
 * no cross-profile leakage.
 *
 * Tuning knobs: K_TUNING_VOLUME / K_TUNING_INTENSITY / K_TUNING_FREQUENCY
 * (ProfileSettings) — the user can manually nudge the coach ±20% via the
 * TuningSheet (BATCH-15).
 */
object CoachingEngine {

    data class CoachNote(
        val headline: String,
        val body: String,
        val tone: String, // "ease" | "push" | "hold" | "neutral"
    )

    /** Compose a note for the active profile based on last feel + tuning. */
    fun noteFor(ctx: Context): CoachNote {
        val lastFeel = ProfileSettings.get(ctx, ProfileSettings.K_LAST_FEEL)?.toIntOrNull()
        val lastRpe = ProfileSettings.get(ctx, ProfileSettings.K_LAST_RPE)?.toIntOrNull()
        val volTune = tuningVolume(ctx)
        val intTune = tuningIntensity(ctx)

        // If no recent feel data, return a neutral nudge.
        if (lastFeel == null && lastRpe == null) {
            return CoachNote(
                headline = "Coach's note",
                body = "Log a session and rate your feel — future sessions will tune to your fatigue.",
                tone = "neutral",
            )
        }

        // Combine RPE and feel on a 0..10 scale.
        val load = listOfNotNull(lastRpe, lastFeel).average()
        val tone = when {
            load >= 9.0 -> "ease"
            load >= 7.5 -> "hold"
            load <= 5.0 -> "push"
            else -> "neutral"
        }
        val volDelta = when (tone) {
            "ease" -> -10 * (volTune.coerceIn(0.8, 1.2))
            "push" -> +5 * (volTune.coerceIn(0.8, 1.2))
            else -> 0.0
        }
        val intDelta = when (tone) {
            "ease" -> -5 * (intTune.coerceIn(0.8, 1.2))
            "push" -> +2.5 * (intTune.coerceIn(0.8, 1.2))
            else -> 0.0
        }
        val body = when (tone) {
            "ease" -> "Last session felt hard (load ${"%.1f".format(load)}). Pull back volume ${"%.0f".format(volDelta)}% and intensity ${"%.0f".format(intDelta)}%."
            "push" -> "Last session felt easy (load ${"%.1f".format(load)}). Nudge volume +${"%.0f".format(volDelta)}% if you're fresh."
            "hold" -> "Steady — last session load ${"%.1f".format(load)} was right in the sweet spot. Hold the plan."
            else -> "Load ${"%.1f".format(load)} — neutral signal. Trust the program."
        }
        return CoachNote(
            headline = "Coach's note — $tone",
            body = body,
            tone = tone,
        )
    }

    /** Tuning factor (1.0 = neutral). Range 0.8..1.2 (±20%). */
    fun tuningVolume(ctx: Context): Double =
        ProfileSettings.get(ctx, ProfileSettings.K_TUNING_VOLUME)?.toDoubleOrNull()?.coerceIn(0.8, 1.2) ?: 1.0

    fun tuningIntensity(ctx: Context): Double =
        ProfileSettings.get(ctx, ProfileSettings.K_TUNING_INTENSITY)?.toDoubleOrNull()?.coerceIn(0.8, 1.2) ?: 1.0

    fun tuningFrequency(ctx: Context): Double =
        ProfileSettings.get(ctx, ProfileSettings.K_TUNING_FREQUENCY)?.toDoubleOrNull()?.coerceIn(0.8, 1.2) ?: 1.0

    fun setTuningVolume(ctx: Context, v: Double) =
        ProfileSettings.put(ctx, ProfileSettings.K_TUNING_VOLUME, v.toString())

    fun setTuningIntensity(ctx: Context, v: Double) =
        ProfileSettings.put(ctx, ProfileSettings.K_TUNING_INTENSITY, v.toString())

    fun setTuningFrequency(ctx: Context, v: Double) =
        ProfileSettings.put(ctx, ProfileSettings.K_TUNING_FREQUENCY, v.toString())

    /** Capture the post-session feel (0..10). Called by PostSessionFeelSheet. */
    fun recordFeel(ctx: Context, feel: Int, rpe: Int?) {
        ProfileSettings.put(ctx, ProfileSettings.K_LAST_FEEL, feel.coerceIn(0, 10).toString())
        if (rpe != null) ProfileSettings.put(ctx, ProfileSettings.K_LAST_RPE, rpe.coerceIn(0, 10).toString())
    }
}
