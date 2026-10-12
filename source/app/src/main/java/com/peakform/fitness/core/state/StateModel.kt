package com.peakform.fitness.core.state

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.util.UUID

const val SCHEMA_VERSION = 31

@Serializable
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val ts: Long = System.currentTimeMillis(),
    val source: String,
    val type: String,
    val payload: String,
    val signature: String = "",
    val schemaVersion: Int = SCHEMA_VERSION,
)

enum class ArcState { BASE, BUILD, PEAK, DELOAD, MAINTAIN, COMEBACK, INTERRUPTED }
enum class Intent { STRONGER, BIGGER, ENDURE, POWER, MOBILE, SKILL, BALANCE, AGILE, FAST, COMP, LONGEVITY, MAINTAIN }

@Serializable
data class PrescriptionWithProvenance(
    val sets: Int, val reps: String, val weight: Double?, val duration: Double?,
    val pct1RM: Double?, val restSeconds: Int, val note: String,
    val intent: String, val arc: String, val engineIds: List<String>,
    val ruleIds: List<String>, val envelopeChecks: List<String>,
    val authorCoachId: String, val authorCoachVersion: Int, val schemaVersion: Int = SCHEMA_VERSION,
)

@Serializable
data class StimulusProfile(
    val exerciseId: String, val muscle: String, val primaryForce: String,
    val jointAction: String, val contributionWeight: Double,
    val bestRepRange: String, val bestIntensityPct: Double,
)

data class Distribution(val mean: Double, val stdDev: Double)

data class ProfileStateTree(
    val schemaVersion: Int = SCHEMA_VERSION,
    val arc: ArcState = ArcState.BASE,
    val events: List<Event> = emptyList(),
    val dossier: Map<String, ExerciseDossier> = emptyMap(),
    val chronicle: List<ChronicleEntry> = emptyList(),
    val tuningIntents: Map<String, String> = emptyMap(),
    val bodySignals: List<BodySignal> = emptyList(),
    val lifeEvents: List<LifeEvent> = emptyList(),
    val coachVersionPins: Map<String, Int> = emptyMap(),
    val exportContractVersion: Int = 1,
)

data class ExerciseDossier(val exerciseId: String, val tested1RM: Double? = null, val history: List<LoggedSession> = emptyList(), val tuningGoal: String = "", val responseRate: Double = 0.0)
data class LoggedSession(val date: String, val weight: Double, val reps: Int, val rpe: Double?, val volume: Double)
data class ChronicleEntry(val ts: Long, val text: String, val arc: ArcState)
data class BodySignal(val ts: Long, val type: String, val value: Double, val muscle: String? = null)
data class LifeEvent(val ts: Long, val type: String, val note: String)

object StateFold {
    fun apply(state: ProfileStateTree, event: Event): ProfileStateTree {
        return when (event.type) {
            "workout_completed" -> applyWorkout(state, event)
            "1rm_tested" -> apply1RM(state, event)
            "tuning_set" -> applyTuning(state, event)
            "chronicle_entry" -> applyChronicle(state, event)
            "body_signal" -> applyBodySignal(state, event)
            "life_event" -> applyLifeEvent(state, event)
            "arc_change" -> applyArcChange(state, event)
            else -> state.copy(events = state.events + event)
        }
    }
    fun fold(events: List<Event>, initial: ProfileStateTree = ProfileStateTree()): ProfileStateTree = events.fold(initial) { s, e -> apply(s, e) }
    private fun applyWorkout(s: ProfileStateTree, e: Event): ProfileStateTree { return s.copy(events = s.events + e) }
    private fun apply1RM(s: ProfileStateTree, e: Event): ProfileStateTree { return s.copy(events = s.events + e) }
    private fun applyTuning(s: ProfileStateTree, e: Event): ProfileStateTree { return s.copy(events = s.events + e) }
    private fun applyChronicle(s: ProfileStateTree, e: Event): ProfileStateTree { return s.copy(events = s.events + e) }
    private fun applyBodySignal(s: ProfileStateTree, e: Event): ProfileStateTree { return s.copy(events = s.events + e) }
    private fun applyLifeEvent(s: ProfileStateTree, e: Event): ProfileStateTree { return s.copy(events = s.events + e) }
    private fun applyArcChange(s: ProfileStateTree, e: Event): ProfileStateTree { return s.copy(events = s.events + e) }
}

object Migrations {
    fun migrate(events: List<Event>, from: Int, to: Int): List<Event> {
        if (from >= to) return events
        return events.map { it.copy(schemaVersion = to) }
    }
}

object ExportContract {
    const val VERSION = 1
    fun export(state: ProfileStateTree): String = com.peakform.fitness.core.ProJson.json.encodeToString(kotlinx.serialization.builtins.ListSerializer(Event.serializer()), state.events)
    fun import(json: String): List<Event>? = try { com.peakform.fitness.core.ProJson.json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(Event.serializer()), json) } catch (_: Exception) { null }
}

object StimulusField {
    fun fieldQuery(exerciseId: String, muscle: String, intent: Intent, timeContext: String = "build-week-3", dossier: Map<String, ExerciseDossier> = emptyMap()): Distribution {
        val priorMean = 0.70; val priorStd = 0.10
        val d = dossier[exerciseId]
        if (d != null && d.history.isNotEmpty()) {
            val lastW = d.history.lastOrNull()?.weight ?: 0.0
            val orm = d.tested1RM ?: (lastW / 0.70)
            if (orm > 0 && lastW > 0) { val obs = lastW / orm; val a = 0.3; return Distribution(priorMean*(1-a)+obs*a, priorStd*0.7) }
        }
        return Distribution(priorMean, priorStd)
    }
}
