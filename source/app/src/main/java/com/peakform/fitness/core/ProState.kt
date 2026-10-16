package com.peakform.fitness.core

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.contentOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * ProState — single source of truth (structural fix for the legacy 3-store race).
 * Mirrors the legacy semantics of loadAllData()/saveWorkoutData()/performSave():
 *  - aggregate workoutData in memory
 *  - currentWorkout draft kept separately (savedWorkout row) + emergency backup pref
 *  - event-driven saves; draft saves debounced 200ms; visibilitychange → immediate flush
 * The vault NEVER auto-restores over live data (P4 fix): snapshots are taken on a
 * guarded schedule and restored only via explicit user action.
 */
object ProState {
    @Volatile var data: WorkoutData = WorkoutData()
    @Volatile var currentWorkout: WorkoutRecord? = null
    @Volatile var initialized: Boolean = false
    @Volatile var activeProfileId: String = Profiles.DEFAULT

    // BATCH-39a: session records (append-only). Stored per profile via ProPrefs.
    @Volatile var sessions: List<com.peakform.fitness.core.state.StateModel.SessionRecord> = emptyList()

    // BATCH-39b: the field (current/peak/lastUpdate per muscle and component).
    // One FieldState per profile, stored under key `p9_field_<profileId>` (ProPrefs).
    // Write-only and monotonic in this batch. Decay arrives in v39c.
    @Volatile var field: com.peakform.fitness.core.state.StateModel.FieldState =
        com.peakform.fitness.core.state.StateModel.FieldState()

    private var store: ProStore? = null
    private var ctx: Context? = null
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val saveMutex = Mutex()
    private var draftDebounceJob: kotlinx.coroutines.Job? = null
    /** BATCH-4B item 2 race fix (original): track the saveWorkoutData coroutine so flush()
     *  can join it before reopenStore() closes the old store's DB.
     *  P4D-CRASH-01/03 (W1+WW2): a single `saveJob` was insufficient —
     *  saveCurrentWorkoutToStorage() launched on `scope` without assigning to
     *  saveJob (W1), and saveWorkoutData() reassigned saveJob on every call,
     *  orphaning the earlier job (W2). The fix is to track ALL in-flight save
     *  coroutines in a synchronized list; flush() joins every one. */
    @Volatile private var saveJob: kotlinx.coroutines.Job? = null
    private val saveJobs = java.util.Collections.synchronizedList(mutableListOf<kotlinx.coroutines.Job>())

    /** P4D-CRASH-01/03: register a save coroutine so flush() can join it.
     *  Used by both saveWorkoutData() and saveCurrentWorkoutToStorage(). */
    private fun trackSave(job: kotlinx.coroutines.Job): kotlinx.coroutines.Job {
        saveJobs.add(job)
        job.invokeOnCompletion { saveJobs.remove(job) }
        return job
    }

    val listeners = mutableListOf<() -> Unit>()

    fun init(context: Context) {
        if (store != null) return
        ctx = context.applicationContext
        activeProfileId = Profiles.activeId(context)
        store = ProStore(context)
    }

    /** Profile switch: drop the old store handle so the next access opens the new namespace.
     *  P4D-CRASH-03: enforce that flush() has completed before closing the old
     *  store's DB. If there are still in-flight save coroutines, flush now —
     *  do NOT silently close a handle a save is still writing to.
     *  The caller (Profiles.switchTo) already calls flush() before this, but
     *  other callers might not — this is the safety net. */
    fun reopenStore(context: Context) {
        val pending = saveJobs.toList()
        if (pending.isNotEmpty()) {
            ProLog.w("STORE", "reopenStore() called with ${pending.size} in-flight save(s) — flushing now")
            flush()
        }
        val old = store
        ctx = context.applicationContext
        activeProfileId = Profiles.activeId(context)
        old?.let { ProDb.close(it.dbName) }
        store = null
        data = WorkoutData()
        currentWorkout = null
        // BATCH-39a/39b: clear derived per-profile caches — they reload from
        // ProPrefs via loadAll() against the new activeProfileId.
        sessions = emptyList()
        field = com.peakform.fitness.core.state.StateModel.FieldState()
        initialized = false
        store = ProStore(context)
    }

    fun notifyChanged() {
        val ls = synchronized(listeners) { listeners.toList() }
        ls.forEach { try { it() } catch (_: Exception) {} }
    }

    // ---- BATCH-39a: session state (append-only) ----
    // Sessions are stored per profile via ProPrefs, keyed "p8_sessions_<profileId>".
    // Loaded in loadAll(), saved on append via saveSessions().

    private val SESSIONS_KEY_PREFIX = "p8_sessions_"
    private val sessionsMutex = kotlinx.coroutines.sync.Mutex()

    /** Load sessions for the active profile from ProPrefs. Called in loadAll(). */
    private fun loadSessions() {
        val c = ctx ?: return
        val key = SESSIONS_KEY_PREFIX + activeProfileId
        val raw = ProPrefs.get(c, key) ?: run {
            sessions = emptyList()
            return
        }
        try {
            sessions = ProJson.json.decodeFromString(
                kotlinx.serialization.builtins.ListSerializer(
                    com.peakform.fitness.core.state.StateModel.SessionRecord.serializer()
                ), raw
            )
        } catch (_: Exception) {
            sessions = emptyList()
        }
    }

    /** Save sessions for the active profile to ProPrefs. Called after append. */
    private fun saveSessions() {
        val c = ctx ?: return
        val key = SESSIONS_KEY_PREFIX + activeProfileId
        val raw = ProJson.json.encodeToString(
            kotlinx.serialization.builtins.ListSerializer(
                com.peakform.fitness.core.state.StateModel.SessionRecord.serializer()
            ), sessions
        )
        ProPrefs.put(c, key, raw)
    }

    /** Append a session record. Append-only — existing records are never mutated. */
    fun appendSession(record: com.peakform.fitness.core.state.StateModel.SessionRecord) {
        synchronized(this) {
            sessions = sessions + record
            saveSessions()
            // BATCH-39b: multiply the session by each exercise's dossier stimulus
            // profile and add the result to the field. Sessions are the ground
            // truth — if the field write fails, the session write above still
            // stands. The field is derived.
            try {
                accumulateField(record)
                saveField()
            } catch (e: Exception) {
                ProLog.w("STORE", "field accumulation failed for session ${record.id}: ${e.message}")
            }
        }
        notifyChanged()
    }

    // ---- BATCH-39b: field accumulation (source/docs/system/07-the-field.md,
    //      04-stimulus-profiles.md, 08-session-state.md § "The field then reads
    //      the session and updates") ----

    /**
     * Multiply each ExerciseRecord in the session by its dossier's stimulus
     * profile and add the result to the field. Monotonic — only adds, never
     * decays. Updates peak for each touched slot. Stamps lastUpdate with the
     * session timestamp.
     *
     * Volume per set:
     *   - For holds (durationSeconds != null && > 0): volume = durationSeconds / 30.0
     *     (30 s treated as one rep-equivalent, per the spec)
     *   - Otherwise: volume = reps.toDouble()
     *
     * Missing dossier → log warning, skip that exercise. Do not throw.
     * Empty stimulusProfile AND empty componentStimulus → skip.
     */
    private fun accumulateField(record: com.peakform.fitness.core.state.StateModel.SessionRecord) {
        val newMuscles = field.muscles.toMutableMap()
        val newComponents = field.components.toMutableMap()
        val touchedMuscles = mutableSetOf<String>()
        val touchedComponents = mutableSetOf<String>()

        for (ex in record.exercises) {
            // Skipped exercises have no completed sets — no contribution.
            if (ex.skipped) continue
            // Pull the dossier for this exercise. Missing dossier → warn + skip.
            val dossier = com.peakform.fitness.coach.dossiers.DossierRegistry.forExercise(ex.exerciseId)
            if (dossier == null) {
                ProLog.w("STORE", "no dossier for exerciseId=${ex.exerciseId} — skipping field contribution")
                continue
            }
            // Both maps empty → skip (per spec).
            val sp = dossier.stimulusProfile
            val cs = dossier.componentStimulus
            if (sp.isEmpty() && cs.isEmpty()) continue

            for (setRec in ex.sets) {
                // Only completed sets contribute.
                if (!setRec.completed) continue
                val volume = if (setRec.durationSeconds != null && setRec.durationSeconds > 0.0) {
                    setRec.durationSeconds / 30.0
                } else {
                    setRec.reps.toDouble()
                }
                if (volume <= 0.0) continue

                // Distribute volume across muscles.
                for ((muscleId, weight) in sp) {
                    if (weight == 0.0) continue
                    val slot = newMuscles[muscleId]
                        ?: com.peakform.fitness.core.state.StateModel.FieldSlot()
                    val newCurrent = slot.current + volume * weight
                    newMuscles[muscleId] = slot.copy(
                        current = newCurrent,
                        peak = maxOf(slot.peak, newCurrent),
                        lastUpdate = record.timestamp,
                    )
                    touchedMuscles += muscleId
                }
                // Distribute volume across components.
                for ((componentName, weight) in cs) {
                    if (weight == 0.0) continue
                    val slot = newComponents[componentName]
                        ?: com.peakform.fitness.core.state.StateModel.FieldSlot()
                    val newCurrent = slot.current + volume * weight
                    newComponents[componentName] = slot.copy(
                        current = newCurrent,
                        peak = maxOf(slot.peak, newCurrent),
                        lastUpdate = record.timestamp,
                    )
                    touchedComponents += componentName
                }
            }
        }

        if (touchedMuscles.isNotEmpty() || touchedComponents.isNotEmpty()) {
            field = com.peakform.fitness.core.state.StateModel.FieldState(
                muscles = newMuscles,
                components = newComponents,
            )
        }
    }

    // ---- BATCH-39b: field persistence (per-profile ProPrefs key p9_field_<profileId>) ----

    private val FIELD_KEY_PREFIX = "p9_field_"

    /** Load the field for the active profile from ProPrefs. Called in loadAll(). */
    private fun loadField() {
        val c = ctx ?: return
        val key = FIELD_KEY_PREFIX + activeProfileId
        val raw = ProPrefs.get(c, key) ?: run {
            field = com.peakform.fitness.core.state.StateModel.FieldState()
            return
        }
        try {
            field = ProJson.json.decodeFromString(
                com.peakform.fitness.core.state.StateModel.FieldState.serializer(), raw
            )
        } catch (_: Exception) {
            field = com.peakform.fitness.core.state.StateModel.FieldState()
        }
    }

    /** Save the field for the active profile to ProPrefs. Called after accumulation. */
    private fun saveField() {
        val c = ctx ?: return
        val key = FIELD_KEY_PREFIX + activeProfileId
        val raw = ProJson.json.encodeToString(
            com.peakform.fitness.core.state.StateModel.FieldState.serializer(), field
        )
        ProPrefs.put(c, key, raw)
    }

    /** Look up a muscle slot in the active profile's field. Null if the muscle was never written. */
    fun fieldSlot(muscleId: String): com.peakform.fitness.core.state.StateModel.FieldSlot? =
        field.muscles[muscleId]

    /** Look up a component slot in the active profile's field. Null if the component was never written. */
    fun componentSlot(componentName: String): com.peakform.fitness.core.state.StateModel.FieldSlot? =
        field.components[componentName]

    /** Return recent sessions that include the given exerciseId, most recent first. */
    fun recentSessions(exerciseId: String, limit: Int = 10): List<com.peakform.fitness.core.state.StateModel.SessionRecord> {
        return sessions
            .filter { session -> session.exercises.any { it.exerciseId == exerciseId } }
            .sortedByDescending { it.timestamp }
            .take(limit)
    }

    // ---------- time helpers (legacy uses local ISO strings; day-diffs UTC-midnight) ----------
    fun nowIso(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())

    fun todayLocal(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun utcDayMillis(iso: String): Long = try {
        val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        val p = if (iso.length >= 19) iso.substring(0, 19) else iso.substring(0, kotlin.math.min(10, iso.length))
        val dayOnly = if (iso.length >= 10 && !iso.contains('T')) iso.substring(0, 10) else null
        (if (dayOnly != null) SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.parse(dayOnly) else d.parse(p))?.time ?: 0L
    } catch (_: Exception) { 0L }

    // ---------- hydration (translation of loadAllData) ----------
    suspend fun loadAll() {
        val s = store ?: return
        val workouts = mutableListOf<WorkoutRecord>()
        val exercises = mutableMapOf<String, ExerciseRecord>()
        var user: UserProfile? = null
        var saved: WorkoutRecord? = null
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            s.db.dao().allWorkouts().forEach { w ->
                try { workouts.add(ProJson.decode(WorkoutRecord.serializer(), w.json)) } catch (e: Exception) { ProLog.e("STORE", "bad workout row ${w.id}: ${e.message}") }
            }
            s.db.dao().allExercises().forEach { e ->
                try { exercises[e.id] = ProJson.decode(ExerciseRecord.serializer(), e.json) } catch (_: Exception) {}
            }
            s.db.dao().userJson()?.let { uj ->
                try { user = ProJson.decode(UserProfile.serializer(), uj) } catch (e: Exception) { ProLog.e("STORE", "bad user row: ${e.message}") }
            }
            s.db.dao().savedJson()?.let { sj ->
                try { saved = ProJson.decode(WorkoutRecord.serializer(), sj) } catch (_: Exception) {}
            }
        }

        val defaultUser = UserProfile(
            created = nowIso(),
            experience = "intermediate",
            settings = Settings(darkMode = false),
        )
        val mergedUser = if (user != null) mergeUser(defaultUser, user!!) else defaultUser

        val d = WorkoutData(
            user = mergedUser,
            workouts = workouts.sortedBy { it.date },
            exercises = exercises,
        )
        data = d

        // Emergency backup arbitration (legacy scoreWorkout)
        val emergency = readEmergencyBackup()
        var best: WorkoutRecord? = null
        var bestScore = -1.0
        if (saved != null && saved!!.exercises.isNotEmpty()) {
            best = saved; bestScore = scoreWorkout(saved!!)
        }
        if (emergency != null && emergency.exercises.isNotEmpty()) {
            val es = scoreWorkout(emergency)
            if (es > bestScore) { best = emergency; bestScore = es }
        }
        best = best?.let { sanitizeDraft(it) }
        if (best != null && best.exercises.isNotEmpty() && best.exercises.all { it.isLogged }) {
            // completed drafts are never restored
            best = null
        }
        currentWorkout = best
        initialized = true
        // BATCH-39a: load session records per profile
        loadSessions()
        // BATCH-39b: load the field (muscle + component slots) per profile
        loadField()
        ProLog.i("STORE", "loaded: workouts=${d.workouts.size} exercises=${d.exercises.size} saved=${saved != null} emergency=${emergency != null} draft=${best != null} sessions=${sessions.size} field.muscles=${field.muscles.size} field.components=${field.components.size}")
        notifyChanged()
    }

    private fun mergeUser(defaults: UserProfile, loaded: UserProfile): UserProfile {
        // Keep loaded values; fill any missing (null/blank) scalars from defaults.
        return loaded.copy(
            created = loaded.created.ifBlank { defaults.created },
            experience = loaded.experience.ifBlank { defaults.experience },
            goal = loaded.goal.ifBlank { defaults.goal },
            gender = loaded.gender.ifBlank { defaults.gender },
            settings = mergeSettings(defaults.settings, loaded.settings),
        )
    }

    /** Settings merge — structural fix for the legacy saveSettings() wipe bug. */
    fun mergeSettings(base: Settings, loaded: Settings): Settings = loaded

    private fun scoreWorkout(w: WorkoutRecord): Double {
        var score = 0.0
        val ts = w.lastModifiedAt?.let { try { utcDayMillis(it) } catch (_: Exception) { 0L } } ?: 0L
        score += ts / 1000.0
        score += w.exercises.count { it.actual != null && !it.skipped } * 10000.0
        if (w.dateCompleted != null) score = -1e9
        return score
    }

    private fun sanitizeDraft(w: WorkoutRecord): WorkoutRecord {
        val exs = w.exercises.mapIndexed { idx, ex ->
            when {
                ex.actual != null -> ex
                else -> ex
            }
        }
        return w.copy(
            activeDates = w.activeDates,
            lastModifiedAt = w.lastModifiedAt ?: w.date.ifBlank { nowIso() },
            id = w.id.ifBlank { "workout_${System.currentTimeMillis()}" },
            exercises = exs,
        )
    }

    // ---------- saves (translation of saveWorkoutData / performSave) ----------
    // P4D-CRASH-01 (W2): do NOT reassign saveJob on every call — that orphans
    // the earlier job from flush()'s join. Track every save in saveJobs.
    fun saveWorkoutData() {
        val job = scope.launch {
            val s = store ?: return@launch
            val d = data
            s.saveAggregate(d, null)
            ProPrefs.put(ctx!!, "workoutData", ProJson.encode(WorkoutData.serializer(), d)) // legacy mirror key
        }
        saveJob = trackSave(job)
    }

    // P4D-CRASH-01 (W1): assign to a tracked job so flush() can join it.
    // Previously this launched on `scope` without any tracking — the save was
    // invisible to flush(), and reopenStore() would close the DB mid-write.
    fun saveCurrentWorkoutToStorage() {
        val cw = currentWorkout ?: return
        val job = scope.launch {
            store?.saveCurrentWorkout(cw)
        }
        trackSave(job)
    }

    fun saveWorkoutDraftImmediately() {
        val cw = currentWorkout ?: return
        var w = cw
        if (w.draftStartedAt == null) {
            w = w.copy(draftStartedAt = nowIso(), activeDates = w.activeDates)
        }
        w = w.copy(lastModifiedAt = nowIso())
        val today = todayLocal()
        if (!w.activeDates.contains(today)) {
            w = w.copy(activeDates = w.activeDates + today)
        }
        currentWorkout = w
        draftDebounceJob?.cancel()
        draftDebounceJob = scope.launch {
            delay(200)
            performSave()
        }
    }

    fun performSave() {
        val cw = currentWorkout ?: return
        saveCurrentWorkoutToStorage()
        // timestamped emergency backup (legacy workoutEmergencyBackup v2)
        // BATCH-4B item 2: moved to per-profile Room meta so a draft in profile A
        // never crosses over into profile B on switch.
        val c = ctx ?: return  // JVM tests / pre-init: in-memory only, never crash
        ProfileState.put(c, ProfileState.K_EMERGENCY_BACKUP, ProJson.json.encodeToString(
            JsonObject.serializer(),
            JsonObject(mutableMapOf(
                "workout" to ProJson.encodeElement(WorkoutRecord.serializer(), cw),
                "timestamp" to JsonPrimitive(System.currentTimeMillis()),
                "version" to JsonPrimitive(2),
            ))
        ))
    }

    /** Flush pending saves (called on onStop / before profile switch).
     *  BATCH-4B item 2 race fix (original): blocks until the in-flight
     *  saveWorkoutData coroutine finishes, so the next reopenStore() can
     *  safely close the old store's DB.
     *  P4D-CRASH-03 (W1+WW2): now joins ALL tracked save coroutines — both
     *  saveWorkoutData() and saveCurrentWorkoutToStorage() — not just the
     *  single `saveJob`. This closes both race windows. */
    fun flush() {
        draftDebounceJob?.cancel()
        draftDebounceJob = null
        if (currentWorkout != null) performSave()
        saveWorkoutData()
        kotlinx.coroutines.runBlocking {
            // P4D-CRASH-03: join every tracked save, not just saveJob.
            val snapshot = saveJobs.toList()
            snapshot.forEach { it.join() }
        }
    }

    private fun readEmergencyBackup(): WorkoutRecord? {
        val c = ctx ?: return null
        val raw = ProfileState.get(c, ProfileState.K_EMERGENCY_BACKUP) ?: return null
        return try {
            val obj = ProJson.json.decodeFromString(JsonObject.serializer(), raw)
            val wEl = obj["workout"] ?: return null
            ProJson.decodeElement(WorkoutRecord.serializer(), wEl)
        } catch (_: Exception) { null }
    }

    fun clearEmergencyBackup() {
        ctx?.let { ProfileState.remove(it, ProfileState.K_EMERGENCY_BACKUP) }
    }

    fun newWorkoutId(): String = "workout_${System.currentTimeMillis()}"

    fun deviceId(): String {
        val c = ctx ?: return "unknown"
        var id = ProPrefs.get(c, "deviceId")
        if (id == null) {
            id = UUID.randomUUID().toString()
            ProPrefs.put(c, "deviceId", id)
        }
        return id
    }

    // ---------- export file naming (legacy format) ----------
    private val monthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    fun exportFileName(prefix: String, note: String? = null): String {
        val first = data.user.name.trim().ifBlank { "User" }.split(Regex("\\s+"))[0]
        val cal = java.util.Calendar.getInstance()
        val mon = monthNames[cal.get(java.util.Calendar.MONTH)]
        val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val yr = cal.get(java.util.Calendar.YEAR)
        return "${prefix}_${first}_${mon}_${day}_${yr}.json"
    }
}

/**
 * ProfileRegistry — reads/writes the SAME SharedPreferences file ("registry") the original
 * native shell used, so profiles created in the old app are enumerated for migration and
 * remain meaningful after upgrade.
 * JSON shape (pro_profiles_v2): {profiles:[{id,name,createdAt,slot,port?}], active, askOnBoot, seq}
 */
object ProfileRegistry {
    data class Profile(val id: String, val name: String, val emoji: String?, val createdAt: Long, val slot: Int)

    /** Raw registry JSON for the full-app-bundle export (feature 10). */
    fun rawJson(ctx: Context): String? {
        val sp = ctx.getSharedPreferences("registry", Context.MODE_PRIVATE)
        return sp.getString("pro_profiles_v2", null) ?: sp.getString("profiles", null)
    }

    fun read(ctx: Context): Triple<List<Profile>, String?, Boolean> {
        val sp = ctx.getSharedPreferences("registry", Context.MODE_PRIVATE)
        val raw = sp.getString("pro_profiles_v2", null) ?: sp.getString("profiles", null)
        var profiles = listOf<Profile>()
        var active: String? = sp.getString("active", null)
        var ask = sp.getBoolean("askOnBoot", false)
        if (raw != null) {
            try {
                val obj = ProJson.json.decodeFromString(JsonObject.serializer(), raw)
                val arr = obj["profiles"] as? JsonArray
                if (arr != null) {
                    profiles = arr.mapNotNull { el ->
                        val o = el as? JsonObject ?: return@mapNotNull null
                        Profile(
                            id = (o["id"] as? JsonPrimitive)?.content ?: return@mapNotNull null,
                            name = (o["name"] as? JsonPrimitive)?.content ?: "Profile",
                            emoji = (o["emoji"] as? JsonPrimitive)?.content,
                            createdAt = (o["createdAt"] as? JsonPrimitive)?.doubleOrNull?.toLong() ?: 0L,
                            slot = (o["slot"] as? JsonPrimitive)?.intOrNull ?: 0,
                        )
                    }
                }
                (obj["active"] as? JsonPrimitive)?.content?.let { if (active == null) active = it }
                (obj["askOnBoot"] as? JsonPrimitive)?.booleanOrNull?.let { ask = it }
            } catch (e: Exception) {
                ProLog.e("REGISTRY", "failed to parse pro_profiles_v2: ${e.message}")
            }
        }
        return Triple(profiles, active, ask)
    }

    fun write(ctx: Context, profiles: List<Profile>, active: String?, askOnBoot: Boolean) {
        val sp = ctx.getSharedPreferences("registry", Context.MODE_PRIVATE)
        val arr = JsonArray(profiles.map { p ->
            JsonObject(mutableMapOf(
                "id" to JsonPrimitive(p.id),
                "name" to JsonPrimitive(p.name),
                "emoji" to (p.emoji?.let { JsonPrimitive(it) } ?: JsonNull),
                "createdAt" to JsonPrimitive(p.createdAt),
                "slot" to JsonPrimitive(p.slot),
            ))
        })
        val obj = JsonObject(mutableMapOf(
            "profiles" to arr,
            "active" to (active?.let { JsonPrimitive(it) } ?: JsonNull),
            "askOnBoot" to JsonPrimitive(askOnBoot),
            "seq" to JsonPrimitive(profiles.size),
        ))
        sp.edit().putString("pro_profiles_v2", ProJson.json.encodeToString(JsonObject.serializer(), obj))
            .putString("active", active ?: "")
            .putBoolean("askOnBoot", askOnBoot).commit()
    }
}
