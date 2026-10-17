package com.peakform.fitness.core

import com.peakform.fitness.ProLog
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.core.state.RecoveryCurves
import com.peakform.fitness.core.state.StateModel
import com.peakform.fitness.engine.Library
import kotlinx.serialization.Serializable
import kotlin.math.exp
import kotlin.math.max

/**
 * BATCH-40 — The Chronicle: narrator + healer.
 *
 * Source: source/docs/system/15-the-chronicle.md and
 * source/docs/architecture/10-chronicle.md.
 *
 * The substrate is built: sessions go in, stimulus accumulates, decays,
 * coaches read readiness, curves learn. The Chronicle watches it all.
 *
 * Two jobs:
 *
 *   1. **heal()** — detect inconsistencies between sessions, field, and
 *      recovery state. Repair them from session history without changing
 *      what happened. Never invents a session. Never deletes a session.
 *
 *   2. **narrate()** — produce a ChronicleReport from real state data.
 *      Four sections: headline, body, highlights, turning points. No
 *      motivational filler. No invented data. If the state is thin, the
 *      narration is thin.
 *
 * Pure: both functions take a [ProStateData] snapshot and return a value.
 * heal() returns a healed [ProStateData]; narrate() returns a
 * [ChronicleReport]. The caller (ProState.loadAll for heal, the
 * ChronicleScreen for narrate) is responsible for writing the result back.
 *
 * Heal rules (source/docs/system/15-the-chronicle.md § "What the healer does"):
 *
 *   1. Field empty (no muscles, no components) + sessions exist →
 *      rebuild the field by replaying every session through the v39b
 *      accumulation logic, then run decayField. Log:
 *      "Healed: rebuilt field from N sessions."
 *
 *   2. Sessions empty + field has values → zero the field. Sessions
 *      are ground truth. Log: "Healed: cleared field, no session data."
 *
 *   3. A field slot's `current` > `peak` → cap current at peak. Log:
 *      "Healed: capped current at peak for {id}."
 *
 *   4. A field slot's `lastUpdate` is in the future → reset to now.
 *      Log: "Healed: reset future timestamp for {id}."
 *
 *   5. RecoveryState missing a curve for a muscle present in the field
 *      → populate with v39e's default. Log: "Healed: added default
 *      curve for {id}."
 *
 *   6. A SessionRecord has an ExerciseRecord referencing an exercise id
 *      not in the library → keep the record, do not delete. Log:
 *      "Warn: session references unknown exercise {id}."
 *
 * Heal never invents a session. Never deletes a session. Only fixes
 * inconsistencies between field and sessions.
 *
 * Narrate rules (source/docs/system/15-the-chronicle.md § "What the
 * narrator produces"):
 *
 *   - HEADLINE: one sentence summarizing the week.
 *   - BODY: 2-4 sentences naming specifics.
 *   - HIGHLIGHTS: 2-5 short bullets.
 *   - TURNING POINTS: 0-3 moments when a rate changed.
 *
 *   - No motivational filler. No "great job!"
 *   - No invented data. Only what's in the state.
 *   - If a week had no sessions, say so plainly:
 *     "No sessions logged. Rest is training too."
 *   - Numbers are exact. "12% more" not "a lot more."
 */
object Chronicle {

    private const val TAG = "CHRONICLE"

    /** One week in milliseconds. */
    private const val WEEK_MS = 7L * 24 * 3_600_000L

    /** One day in milliseconds. */
    private const val DAY_MS = 24L * 3_600_000L

    /**
     * Snapshot of the per-profile state the chronicle reads / heals.
     *
     * Holds the three derived per-profile pieces (sessions, field,
     * recovery). Not the legacy WorkoutData tree — the chronicle only
     * cares about session-derived state.
     */
    @Serializable
    data class ProStateData(
        val userId: String,
        val sessions: List<StateModel.SessionRecord> = emptyList(),
        val field: StateModel.FieldState = StateModel.FieldState(),
        val recovery: StateModel.RecoveryState = StateModel.RecoveryState(),
    )

    /**
     * The chronicle's report — a 4-section narrative of the week.
     *
     *   - [headline]: one sentence summarizing the week.
     *   - [body]: 2-4 sentences naming specifics.
     *   - [highlights]: 2-5 short bullets.
     *   - [turningPoints]: 0-3 moments when a rate changed.
     *   - [generatedAt]: wall clock at the time of generation, for the
     *     "Generated {relative time}" line on the screen.
     */
    @Serializable
    data class ChronicleReport(
        val headline: String,
        val body: String,
        val highlights: List<String>,
        val turningPoints: List<String>,
        val generatedAt: Long,
    )

    /**
     * Heal log — lines from the most recent heal() pass. Useful for tests
     * and for the UI to surface what the healer did. Cleared at the top
     * of each heal() call.
     */
    val healLog: MutableList<String> = mutableListOf()

    /** Reset the heal log. Call before heal() in tests. */
    fun clearHealLog() {
        synchronized(healLog) { healLog.clear() }
    }

    private fun log(line: String) {
        synchronized(healLog) { healLog.add(line) }
        ProLog.i(TAG, line)
    }

    /**
     * Heal the state per the 6 rules.
     *
     * Pure: takes a [ProStateData], returns a healed [ProStateData]. Logs
     * every fix to [healLog] and to ProLog. Never invents a session. Never
     * deletes a session. Only fixes inconsistencies between field and
     * sessions (and recovery curves).
     *
     * Heal rule 1 (rebuild field) needs the dossier registry; heal rule 6
     * (warn unknown exercise) needs the library. Both gracefully degrade
     * when the registries are not loaded yet (e.g. on the very first
     * loadAll at app boot, before Library.ensure/DossierRegistry.ensure
     * complete). On subsequent boots / profile switches the registries
     * are already loaded, so all rules fire.
     */
    fun heal(state: ProStateData): ProStateData {
        clearHealLog()
        val sessions = state.sessions
        var field = state.field
        var recovery = state.recovery
        val now = System.currentTimeMillis()

        // Rule 6 (warn unknown exercise) — runs on every session, never
        // deletes. Skipped silently when the library isn't loaded yet.
        if (Library.isReady) {
            for (session in sessions) {
                for (ex in session.exercises) {
                    if (!isKnownExercise(ex.exerciseId)) {
                        log("Warn: session references unknown exercise ${ex.exerciseId}.")
                    }
                }
            }
        }

        // Rule 1: field empty + sessions exist → rebuild the field by
        // replaying every session through the accumulation logic (v39b),
        // then run decayField.
        val fieldHasValues = field.muscles.isNotEmpty() || field.components.isNotEmpty()
        if (!fieldHasValues && sessions.isNotEmpty()) {
            val rebuilt = rebuildFieldFromSessions(sessions, now, recovery)
            if (rebuilt.muscles.isNotEmpty() || rebuilt.components.isNotEmpty()) {
                field = rebuilt
                log("Healed: rebuilt field from ${sessions.size} sessions.")
            }
        }

        // Rule 2: sessions empty + field has values → zero the field.
        if (sessions.isEmpty() && fieldHasValues) {
            field = StateModel.FieldState()
            log("Healed: cleared field, no session data.")
        }

        // Rule 3: cap current at peak for every slot.
        val cappedMuscles = HashMap<String, StateModel.FieldSlot>(field.muscles.size)
        for ((id, slot) in field.muscles) {
            if (slot.current > slot.peak) {
                log("Healed: capped current at peak for $id.")
                cappedMuscles[id] = slot.copy(current = slot.peak)
            } else {
                cappedMuscles[id] = slot
            }
        }
        val cappedComponents = HashMap<String, StateModel.FieldSlot>(field.components.size)
        for ((id, slot) in field.components) {
            if (slot.current > slot.peak) {
                log("Healed: capped current at peak for $id.")
                cappedComponents[id] = slot.copy(current = slot.peak)
            } else {
                cappedComponents[id] = slot
            }
        }
        field = StateModel.FieldState(muscles = cappedMuscles, components = cappedComponents)

        // Rule 4: reset lastUpdate if it's in the future.
        val timeFixedMuscles = HashMap<String, StateModel.FieldSlot>(field.muscles.size)
        for ((id, slot) in field.muscles) {
            if (slot.lastUpdate > now) {
                log("Healed: reset future timestamp for $id.")
                timeFixedMuscles[id] = slot.copy(lastUpdate = now)
            } else {
                timeFixedMuscles[id] = slot
            }
        }
        val timeFixedComponents = HashMap<String, StateModel.FieldSlot>(field.components.size)
        for ((id, slot) in field.components) {
            if (slot.lastUpdate > now) {
                log("Healed: reset future timestamp for $id.")
                timeFixedComponents[id] = slot.copy(lastUpdate = now)
            } else {
                timeFixedComponents[id] = slot
            }
        }
        field = StateModel.FieldState(muscles = timeFixedMuscles, components = timeFixedComponents)

        // Rule 5: populate default curve for muscles / components in the
        // field that are missing from recovery.
        val newCurves = recovery.curves.toMutableMap()
        for (muscleId in field.muscles.keys) {
            if (!newCurves.containsKey(muscleId)) {
                newCurves[muscleId] = RecoveryCurves.defaultCurve(muscleId)
                log("Healed: added default curve for $muscleId.")
            }
        }
        for (componentName in field.components.keys) {
            if (!newCurves.containsKey(componentName)) {
                newCurves[componentName] = RecoveryCurves.defaultCurveForComponent(componentName)
                log("Healed: added default curve for $componentName.")
            }
        }
        recovery = StateModel.RecoveryState(curves = newCurves)

        return state.copy(
            sessions = sessions,
            field = field,
            recovery = recovery,
        )
    }

    /**
     * Rebuild a fresh FieldState by replaying every session through the
     * v39b accumulation math, then decaying to [now]. Pure — does not
     * touch ProState. Mirrors [ProState.accumulateField] +
     * [ProState.decayField] exactly so the rebuilt field matches what
     * the live write path would have produced.
     *
     * Returns an empty field if the dossier registry is not loaded (the
     * rebuild can't compute stimulus contributions without dossiers).
     */
    private fun rebuildFieldFromSessions(
        sessions: List<StateModel.SessionRecord>,
        now: Long,
        recovery: StateModel.RecoveryState,
    ): StateModel.FieldState {
        if (!DossierRegistry.isReady) return StateModel.FieldState()
        // Sort sessions by timestamp ascending so accumulation happens in
        // the same order the live writes would have happened.
        val sorted = sessions.sortedBy { it.timestamp }
        var field = StateModel.FieldState()
        for (session in sorted) {
            // The live path is decay-then-accumulate: decay the field per
            // its curves to the session's timestamp, then accumulate the
            // session's volume contributions on top.
            field = decayPure(field, session.timestamp, recovery)
            field = accumulatePure(field, session)
        }
        // Final decay to `now` so the rebuilt field reflects stimulus
        // decayed up to right now (matches what the live path produces
        // on loadAll → decayField()).
        return decayPure(field, now, recovery)
    }

    /**
     * Accumulate one session's contributions into the field. Mirrors
     * [ProState.accumulateField]'s math exactly: skipped exercises skip,
     * missing dossier → skip, volume per set = reps or durationSeconds/30,
     * new current = old current + volume × weight, peak = max(old peak,
     * new current), lastUpdate = session timestamp.
     */
    private fun accumulatePure(
        field: StateModel.FieldState,
        record: StateModel.SessionRecord,
    ): StateModel.FieldState {
        val newMuscles = field.muscles.toMutableMap()
        val newComponents = field.components.toMutableMap()

        for (ex in record.exercises) {
            if (ex.skipped) continue
            val dossier = DossierRegistry.forExercise(ex.exerciseId) ?: continue
            val sp = dossier.stimulusProfile
            val cs = dossier.componentStimulus
            if (sp.isEmpty() && cs.isEmpty()) continue

            for (setRec in ex.sets) {
                if (!setRec.completed) continue
                val volume = if (setRec.durationSeconds != null && setRec.durationSeconds > 0.0) {
                    setRec.durationSeconds / 30.0
                } else {
                    setRec.reps.toDouble()
                }
                if (volume <= 0.0) continue

                for ((muscleId, weight) in sp) {
                    if (weight == 0.0) continue
                    val slot = newMuscles[muscleId] ?: StateModel.FieldSlot()
                    val newCurrent = slot.current + volume * weight
                    newMuscles[muscleId] = slot.copy(
                        current = newCurrent,
                        peak = max(slot.peak, newCurrent),
                        lastUpdate = record.timestamp,
                    )
                }
                for ((componentName, weight) in cs) {
                    if (weight == 0.0) continue
                    val slot = newComponents[componentName] ?: StateModel.FieldSlot()
                    val newCurrent = slot.current + volume * weight
                    newComponents[componentName] = slot.copy(
                        current = newCurrent,
                        peak = max(slot.peak, newCurrent),
                        lastUpdate = record.timestamp,
                    )
                }
            }
        }

        return StateModel.FieldState(muscles = newMuscles, components = newComponents)
    }

    /**
     * Decay every slot per its recovery curve to [now]. Mirrors
     * [ProState.decayField]'s math without touching ProState. Peak is
     * NEVER decayed. Slot dust (< 0.01) is zeroed.
     */
    private fun decayPure(
        field: StateModel.FieldState,
        now: Long,
        recovery: StateModel.RecoveryState,
    ): StateModel.FieldState {
        val newMuscles = HashMap<String, StateModel.FieldSlot>(field.muscles.size)
        for ((muscleId, slot) in field.muscles) {
            if (slot.current == 0.0 && slot.peak == 0.0) {
                newMuscles[muscleId] = slot
                continue
            }
            val T = recovery.curves[muscleId]?.recoveryHours
                ?: RecoveryCurves.defaultHours(muscleId)
            val factor = decayFactor(slot.lastUpdate, now, T)
            val newCurrent = if (slot.current * factor < 0.01) 0.0 else slot.current * factor
            newMuscles[muscleId] = slot.copy(
                current = newCurrent,
                peak = slot.peak,
                lastUpdate = now,
            )
        }
        val newComponents = HashMap<String, StateModel.FieldSlot>(field.components.size)
        for ((componentName, slot) in field.components) {
            if (slot.current == 0.0 && slot.peak == 0.0) {
                newComponents[componentName] = slot
                continue
            }
            val T = recovery.curves[componentName]?.recoveryHours
                ?: RecoveryCurves.defaultHoursForComponent(componentName)
            val factor = decayFactor(slot.lastUpdate, now, T)
            val newCurrent = if (slot.current * factor < 0.01) 0.0 else slot.current * factor
            newComponents[componentName] = slot.copy(
                current = newCurrent,
                peak = slot.peak,
                lastUpdate = now,
            )
        }
        return StateModel.FieldState(muscles = newMuscles, components = newComponents)
    }

    /** exp(-elapsedHours / T) — pure helper for decayPure. */
    private fun decayFactor(lastUpdate: Long, now: Long, T: Double): Double {
        if (T <= 0.0) return 1.0
        val elapsedMs = (now - lastUpdate).coerceAtLeast(0L)
        val elapsedHours = elapsedMs / 3_600_000.0
        if (elapsedHours == 0.0) return 1.0
        return exp(-elapsedHours / T)
    }

    /**
     * Whether an exercise id is known to the library. Returns true (assume
     * known) when the library isn't loaded — rule 6 only fires once the
     * library is ready.
     */
    private fun isKnownExercise(exerciseId: String): Boolean {
        if (!Library.isReady) return true
        return Library.getExerciseById(exerciseId, includeBodyweight = true) != null
    }

    // ============================================================
    // PART 2 — THE NARRATOR
    // ============================================================

    /**
     * Narrate the week per the spec. Pure — reads only the state passed
     * in. Produces a [ChronicleReport] with all four sections.
     *
     * Reads:
     *   - Sessions in the last 7 days, 30 days
     *   - Field readiness at now
     *   - RecoveryState curves
     *   - Component summaries
     *   - Weight/volume trends per muscle
     *
     * If the state is thin (no sessions, no field), the narration is
     * thin. No invented data.
     */
    fun narrate(userId: String, state: ProStateData): ChronicleReport {
        val now = System.currentTimeMillis()
        val weekAgo = now - WEEK_MS
        val twoWeeksAgo = now - 2 * WEEK_MS
        val thirtyDaysAgo = now - 30 * DAY_MS

        val weekSessions = state.sessions.filter { it.timestamp >= weekAgo }
        val prevWeekSessions = state.sessions.filter { it.timestamp in twoWeeksAgo until weekAgo }
        val thirtyDaySessions = state.sessions.filter { it.timestamp >= thirtyDaysAgo }

        // Volume per muscle: this week vs last week.
        val muscleVolume = muscleVolume(state.sessions, weekAgo, now)
        val prevMuscleVolume = muscleVolume(state.sessions, twoWeeksAgo, weekAgo)

        // 30-day volume per muscle for the "X% over 4 weeks" highlights.
        val fourWeeksAgo = now - 4 * WEEK_MS
        val muscleVolume30d = muscleVolume(state.sessions, fourWeeksAgo, now)
        val muscleVolumePrev30d = muscleVolume(state.sessions, now - 8 * WEEK_MS, fourWeeksAgo)

        val headline = buildHeadline(weekSessions, muscleVolume, prevMuscleVolume)
        val body = buildBody(weekSessions, prevWeekSessions, muscleVolume, prevMuscleVolume, state)
        val highlights = buildHighlights(state, weekSessions, thirtyDaySessions, muscleVolume30d, muscleVolumePrev30d)
        val turningPoints = buildTurningPoints(state)

        return ChronicleReport(
            headline = headline,
            body = body,
            highlights = highlights,
            turningPoints = turningPoints,
            generatedAt = now,
        )
    }

    /**
     * Volume per muscle for sessions in [fromMs, toMs). Same volume
     * formula as [ProState.accumulateField] + [Field.recentStimulus]:
     * volume = reps or durationSeconds/30, multiplied by the dossier's
     * stimulusProfile weight for the muscle. Skipped and incomplete sets
     * are excluded.
     *
     * Returns an empty map when the dossier registry isn't loaded.
     */
    private fun muscleVolume(
        sessions: List<StateModel.SessionRecord>,
        fromMs: Long,
        toMs: Long,
    ): Map<String, Double> {
        val out = HashMap<String, Double>()
        if (!DossierRegistry.isReady) return out
        for (s in sessions) {
            if (s.timestamp < fromMs || s.timestamp >= toMs) continue
            for (ex in s.exercises) {
                if (ex.skipped) continue
                val dossier = DossierRegistry.forExercise(ex.exerciseId) ?: continue
                for (set in ex.sets) {
                    if (!set.completed) continue
                    val vol = if (set.durationSeconds != null && set.durationSeconds > 0.0) {
                        set.durationSeconds / 30.0
                    } else {
                        set.reps.toDouble()
                    }
                    if (vol <= 0.0) continue
                    for ((m, w) in dossier.stimulusProfile) {
                        if (w == 0.0) continue
                        out[m] = (out[m] ?: 0.0) + vol * w
                    }
                }
            }
        }
        return out
    }

    /**
     * HEADLINE — one sentence summarizing the week.
     *
     *   - If the week had no sessions: "No sessions logged this week.
     *     Rest is training too."
     *   - Else if a muscle grew vs last week: "{Muscle} volume up {N}%
     *     this week."
     *   - Else: "{N} sessions logged. Recovery on pace."
     */
    private fun buildHeadline(
        weekSessions: List<StateModel.SessionRecord>,
        volume: Map<String, Double>,
        prevVolume: Map<String, Double>,
    ): String {
        if (weekSessions.isEmpty()) {
            return "No sessions logged this week. Rest is training too."
        }
        val grown = volume.entries.mapNotNull { (m, cur) ->
            val prev = prevVolume[m] ?: 0.0
            if (prev <= 0.0 && cur > 0.0) {
                return@mapNotNull m to 1.0
            }
            if (prev <= 0.0) return@mapNotNull null
            m to ((cur - prev) / prev)
        }.sortedByDescending { it.second }
        val top = grown.firstOrNull()
        if (top != null && top.second > 0.0) {
            val pct = (top.second * 100).toInt()
            return "${displayName(top.first)} volume up ${pct}% this week."
        }
        return "${weekSessions.size} session${if (weekSessions.size > 1) "s" else ""} logged. Recovery on pace."
    }

    /**
     * BODY — 2-4 sentences naming specifics.
     *
     * Names: session count, week-over-week volume delta, top recovered
     * muscle, least recovered muscle.
     */
    private fun buildBody(
        weekSessions: List<StateModel.SessionRecord>,
        prevWeekSessions: List<StateModel.SessionRecord>,
        volume: Map<String, Double>,
        prevVolume: Map<String, Double>,
        state: ProStateData,
    ): String {
        if (weekSessions.isEmpty()) {
            return "No sessions logged in the last 7 days. The field is empty for this window."
        }
        val sb = StringBuilder()
        sb.append("You trained ${weekSessions.size} time${if (weekSessions.size > 1) "s" else ""} this week")
        val totalCur = volume.values.sum()
        val totalPrev = prevVolume.values.sum()
        if (totalPrev > 0.0) {
            val deltaPct = ((totalCur - totalPrev) / totalPrev * 100).toInt()
            val sign = if (deltaPct >= 0) "+" else ""
            sb.append(", $sign$deltaPct% volume vs last week")
        }
        sb.append(". ")

        // Top recovered + least recovered muscle (only those with peak > 0).
        val withPeak = state.field.muscles.entries.filter { it.value.peak > 0.0 }
        if (withPeak.isNotEmpty()) {
            val mostReady = withPeak.maxByOrNull { 1.0 - (it.value.current / it.value.peak) }
            val leastReady = withPeak.minByOrNull { 1.0 - (it.value.current / it.value.peak) }
            if (mostReady != null) {
                val rd = (((1.0 - mostReady.value.current / mostReady.value.peak) * 100)).toInt().coerceIn(0, 100)
                sb.append("${displayName(mostReady.key)} is ${rd}% recovered. ")
            }
            if (leastReady != null && leastReady.key != mostReady?.key) {
                val rd = (((1.0 - leastReady.value.current / leastReady.value.peak) * 100)).toInt().coerceIn(0, 100)
                sb.append("${displayName(leastReady.key)} is at $rd%. ")
            }
        }
        return sb.toString().trim()
    }

    /**
     * HIGHLIGHTS — 2-5 short bullets.
     *
     *   - Total session count
     *   - Top-3 muscle volume deltas week-over-week
     *   - Number of muscles tracked in the field
     *   - Number of learned recovery curves
     */
    private fun buildHighlights(
        state: ProStateData,
        weekSessions: List<StateModel.SessionRecord>,
        thirtyDaySessions: List<StateModel.SessionRecord>,
        volume30d: Map<String, Double>,
        volumePrev30d: Map<String, Double>,
    ): List<String> {
        if (state.sessions.isEmpty() && state.field.muscles.isEmpty()) {
            return listOf("No training data yet.")
        }
        val out = ArrayList<String>()

        // Total sessions logged.
        out.add("${state.sessions.size} sessions logged total")

        // 4-week volume delta per muscle (top 3 by absolute pct).
        val deltas = volume30d.entries.mapNotNull { (m, cur) ->
            val prev = volumePrev30d[m] ?: 0.0
            if (prev <= 0.0 && cur > 0.0) {
                return@mapNotNull m to "new"
            }
            if (prev <= 0.0) return@mapNotNull null
            val pct = ((cur - prev) / prev * 100).toInt()
            m to pct.toString()
        }.sortedByDescending { (m, d) ->
            if (d == "new") Int.MAX_VALUE else d.toInt()
        }
        for ((m, d) in deltas.take(3)) {
            if (d == "new") {
                out.add("${displayName(m)}: new stimulus in the last 4 weeks")
            } else {
                val sign = if (d.toInt() >= 0) "+" else ""
                out.add("${displayName(m)} volume $sign$d% over 4 weeks")
            }
        }

        // Field size.
        if (state.field.muscles.isNotEmpty()) {
            out.add("${state.field.muscles.size} muscles tracked in the field")
        }
        // Recovery curves learned.
        val learned = state.recovery.curves.values.count { it.source == "learned" }
        if (learned > 0) {
            out.add("$learned recovery curve${if (learned > 1) "s" else ""} learned from data")
        }
        if (weekSessions.isEmpty() && thirtyDaySessions.isNotEmpty()) {
            out.add("No sessions this week, ${thirtyDaySessions.size} in the last 30 days")
        }
        return out.take(5)
    }

    /**
     * TURNING POINTS — 0-3 moments when a rate changed.
     *
     * Heuristic: walk the per-muscle weekly volume series and emit the
     * first week where the muscle exceeded 20 units of stimulus volume
     * (a soft threshold — easy to compute, easy to interpret).
     */
    private fun buildTurningPoints(state: ProStateData): List<String> {
        if (state.sessions.isEmpty()) return emptyList()
        val byMuscle = HashMap<String, MutableList<Pair<Long, Double>>>() // weekStart → volume
        val sessions = state.sessions.sortedBy { it.timestamp }
        val first = sessions.first().timestamp
        for (s in sessions) {
            val wkIdx = ((s.timestamp - first) / WEEK_MS).toInt()
            val wkStart = first + wkIdx * WEEK_MS
            for (ex in s.exercises) {
                if (ex.skipped) continue
                val dossier = DossierRegistry.forExercise(ex.exerciseId) ?: continue
                for (set in ex.sets) {
                    if (!set.completed) continue
                    val vol = if (set.durationSeconds != null && set.durationSeconds > 0.0) {
                        set.durationSeconds / 30.0
                    } else set.reps.toDouble()
                    if (vol <= 0.0) continue
                    for ((m, w) in dossier.stimulusProfile) {
                        if (w == 0.0) continue
                        val list = byMuscle.getOrPut(m) { mutableListOf() }
                        val existing = list.firstOrNull { it.first == wkStart }
                        if (existing == null) {
                            list.add(wkStart to vol * w)
                        } else {
                            val idx = list.indexOf(existing)
                            list[idx] = wkStart to (existing.second + vol * w)
                        }
                    }
                }
            }
        }
        val out = ArrayList<String>()
        for ((m, weeks) in byMuscle) {
            weeks.sortBy { it.first }
            for ((wk, vol) in weeks) {
                if (vol > 20.0) {
                    val wkIdx = ((wk - first) / WEEK_MS).toInt() + 1
                    out.add("Week $wkIdx: first week above 20 units for ${displayName(m)}")
                    break
                }
            }
            if (out.size >= 3) break
        }
        return out.take(3)
    }

    /** Friendly display name for a muscle id. Capitalizes first letter,
     *  replaces underscores with spaces. */
    private fun displayName(muscleId: String): String {
        if (muscleId.isEmpty()) return "—"
        return muscleId.substring(0, 1).uppercase() + muscleId.substring(1).replace("_", " ")
    }
}
