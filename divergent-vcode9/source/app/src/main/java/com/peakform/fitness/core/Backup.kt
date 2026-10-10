package com.peakform.fitness.core

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import java.io.File

/**
 * Backup — full import/export compatibility with the legacy formats:
 *  - Format A      : {...workoutData, exportDeviceId, exportTimestamp}
 *  - Complete-v1   : PeakForm-Complete-Backup-v1 {__meta, localStorage, indexedDB.p4_vault, liveWorkoutData}
 *  - try4ever      : older P4 bundle (tolerated on import)
 * Import is additive-safe: a guarded snapshot is taken first (P4 fix for the destructive
 * loadData() clear), then data is merged by the documented priority order.
 */
object Backup {
    const val FORMAT_V1 = "PeakForm-Complete-Backup-v1"

    fun exportFormatA(ctx: Context?, note: String? = null): JsonObject {
        val d = ProState.data
        val obj = d.toJsonElement().jsonObject.toMutableMap()
        obj["exportDeviceId"] = JsonPrimitive(ProState.deviceId())
        obj["exportTimestamp"] = JsonPrimitive(ProState.nowIso())
        if (note != null) obj["backupNote"] = JsonPrimitive(note)
        obj["_p4SavedAt"] = JsonPrimitive(System.currentTimeMillis())
        return JsonObject(obj)
    }

    fun exportCompleteV1(ctx: Context): JsonObject {
        val d = ProState.data
        val lsKeys = mutableMapOf<String, JsonElement>()
        for ((k, v) in ProPrefs.all(ctx)) lsKeys[k] = JsonPrimitive(v ?: "")
        val vaultRows = mutableListOf<JsonElement>()
        kotlinx.coroutines.runBlocking {
            val v = ProStore(ctx).db.dao().allVault()
            v.forEach { row ->
                try {
                    val inner = ProJson.json.decodeFromString(JsonObject.serializer(), row.json)
                    vaultRows.add(JsonObject(mutableMapOf("at" to JsonPrimitive(row.at), "data" to (inner["workoutData"] ?: JsonNull))))
                } catch (_: Exception) {}
            }
        }
        val totalVolume = d.workouts.sumOf { it.summary?.totalVolume ?: 0.0 }
        val meta = buildJsonObject {
            put("appVersion", "4.0.0-native-1.2.1")
            put("exportDate", ProState.nowIso())
            put("exportTimestamp", System.currentTimeMillis())
            put("profileCount", ProfileRegistry.read(ctx).first.size)
            put("workoutCount", d.workouts.size)
            put("totalVolume", totalVolume)
            put("deviceInfo", buildJsonObject {
                put("userAgent", "Pro/1.2.1 Android native")
                put("platform", "android")
                put("language", java.util.Locale.getDefault().toLanguageTag())
            })
        }
        return buildJsonObject {
            put("__format", FORMAT_V1)
            put("__meta", meta)
            put("localStorage", JsonObject(lsKeys))
            put("indexedDB", buildJsonObject {
                put("p4_vault", JsonObject(vaultRows.mapIndexed { i, el -> "snap_$i" to el }.toMap()))
            })
            put("liveWorkoutData", d.toJsonElement())
        }
    }

    /** Result of an import. */
    data class ImportResult(val ok: Boolean, val format: String, val workouts: Int, val exercises: Int, val message: String)

    /**
     * Import from arbitrary JSON. Detects the format. Snapshot first, merge second.
     * Priority order (per worklog spec): liveWorkoutData/inline workoutData > workouts+exercises.
     */
    suspend fun import(ctx: Context, raw: String): ImportResult {
        val root = try { ProJson.json.parseToJsonElement(raw).jsonObject } catch (e: Exception) {
            return ImportResult(false, "?", 0, 0, "Not a valid JSON backup file")
        }
        // 1. guarded snapshot of current state before anything is touched
        ProStore(ctx).snapshotVault("pre-import", ProState.data, ProState.currentWorkout)
        ProLog.i("BACKUP", "pre-import snapshot taken")

        val format = (root["__format"] as? JsonPrimitive)?.contentOrNull
        var source: JsonObject? = null
        var lsBundle: JsonObject? = null
        when (format) {
            FORMAT_V1 -> {
                source = (root["liveWorkoutData"] as? JsonObject)
                    ?: (root["localStorage"]?.let { ls -> (ls as? JsonObject)?.get("p4_profile_data_active") as? JsonObject })
                lsBundle = root["localStorage"] as? JsonObject
            }
            else -> {
                // Format A or bare workoutData
                source = if (root["workouts"] != null || root["exercises"] != null || root["user"] != null) root
                else root["workoutData"] as? JsonObject
                lsBundle = root["localStorage"] as? JsonObject
            }
        }
        if (source == null) {
            // Maybe a pure localStorage-only bundle (profile data key)
            source = root["workoutData"] as? JsonObject
        }
        if (source == null) return ImportResult(false, format ?: "?", 0, 0, "No workout data found in bundle")

        val incoming = try {
            ProJson.decode(WorkoutData.serializer(), source.toString())
        } catch (e: Exception) {
            ProLog.e("BACKUP", "parse of workoutData failed: ${e.message}")
            return ImportResult(false, format ?: "Format A", 0, 0, "Backup payload unreadable: ${e.message?.take(120)}")
        }

        // 2. merge: additive for workouts/exercises; user merged field-wise (blank incoming
        //    fields never blank out local data — P4 loadData() destructive-import fix)
        val cur = ProState.data
        val merged = computeMerge(cur, incoming)
        ProState.data = merged

        // 3. restore localStorage bundle keys (p4_* settings, profiles, etc.)
        if (lsBundle != null) {
            for ((k, v) in lsBundle) {
                if (k == "workoutEmergencyBackup") continue
                val s = (v as? JsonPrimitive)?.contentOrNull ?: continue
                ProPrefs.put(ctx, k, s)
            }
        }

        // 4. persist everything
        ProState.saveWorkoutData()
        EngineHooks.rebuildDerived()
        ProState.notifyChanged()
        ProLog.i("BACKUP", "imported format=$format workouts=${incoming.workouts.size} exercises=${incoming.exercises.size}")
        return ImportResult(true, format ?: "Format A", incoming.workouts.size, incoming.exercises.size,
            "Imported ${incoming.workouts.size} workouts and ${incoming.exercises.size} exercise records")
    }

    /**
     * Pure, testable merge of an incoming backup payload into the current state.
     * - workouts/exercises: additive, dedup by id (or date+name), newest-first order preserved
     * - user: incoming wins, but blank incoming scalars keep local values; settings merged field-wise
     */
    fun computeMerge(cur: WorkoutData, incoming: WorkoutData): WorkoutData {
        val mergedWorkouts = (incoming.workouts + cur.workouts)
            .distinctBy { it.id.ifBlank { it.date + it.name } }
            .sortedBy { it.date }
        val mergedExercises = mutableMapOf<String, ExerciseRecord>()
        cur.exercises.forEach { (k, v) -> mergedExercises[k] = v }
        incoming.exercises.forEach { (k, v) ->
            val old = mergedExercises[k]
            mergedExercises[k] = if (old == null) v else mergeExerciseRecord(old, v)
        }
        val u = incoming.user; val c = cur.user
        val mergedUser = u.copy(
            name = u.name.ifBlank { c.name },
            gender = u.gender.ifBlank { c.gender },
            experience = u.experience.ifBlank { c.experience },
            goal = u.goal.ifBlank { c.goal },
            created = u.created.ifBlank { c.created },
            weight = u.weight ?: c.weight,
            height = u.height ?: c.height,
            birthDate = u.birthDate ?: c.birthDate,
            bodyWeightHistory = if (u.bodyWeightHistory.size >= c.bodyWeightHistory.size) u.bodyWeightHistory else c.bodyWeightHistory,
            settings = mergeSettingsFieldwise(c.settings, u.settings),
        )
        return cur.copy(
            user = mergedUser,
            workouts = mergedWorkouts,
            exercises = mergedExercises,
        )
    }

    /** Field-wise settings merge: strings/collections merge (blanks never wipe); booleans take incoming (legacy decode semantics). */
    fun mergeSettingsFieldwise(cur: Settings, inc: Settings): Settings = Settings(
        workoutDays = inc.workoutDays.ifEmpty { cur.workoutDays },
        restTime = if (inc.restTime > 0) inc.restTime else cur.restTime,
        progressionRate = if (inc.progressionRate != 0.02) inc.progressionRate else cur.progressionRate,
        theme = inc.theme.ifBlank { cur.theme },
        darkMode = inc.darkMode,
        muscleCoupling = inc.muscleCoupling,
        bottomNavAutoHide = inc.bottomNavAutoHide,
        expressMode = inc.expressMode,
        aggression = if (inc.aggression != 1.0) inc.aggression else cur.aggression,
        equipment = inc.equipment.ifEmpty { cur.equipment },
        trainingMode = inc.trainingMode.ifBlank { cur.trainingMode },
        sarcasmMode = inc.sarcasmMode,
        postpartumMode = inc.postpartumMode,
    )

    private fun mergeExerciseRecord(old: ExerciseRecord, inc: ExerciseRecord): ExerciseRecord {
        val hist = (old.history + inc.history).distinctBy { it.date + "|" + it.weight + "|" + it.reps.size }
        return if (old.lastUpdate != null && inc.lastUpdate != null && old.lastUpdate >= inc.lastUpdate) old.copy(history = hist)
        else inc.copy(history = hist, startingWeight = inc.startingWeight ?: old.startingWeight)
    }

    // ---------- SAF helpers ----------
    fun writeJsonToUri(ctx: Context, uri: Uri, json: JsonObject): Boolean = try {
        ctx.contentResolver.openOutputStream(uri, "wt")?.use { os ->
            os.write(ProJson.pretty.encodeToString(JsonObject.serializer(), json).toByteArray())
        }
        true
    } catch (e: Exception) {
        ProLog.e("BACKUP", "write failed: ${e.message}")
        false
    }

    fun readJsonFromUri(ctx: Context, uri: Uri): String? = try {
        ctx.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
    } catch (e: Exception) {
        ProLog.e("BACKUP", "read failed: ${e.message}")
        null
    }
}

/** Hooks the engine exposes to the store layer (set at boot by EngineInitializer). */
object EngineHooks {
    var rebuildDerived: suspend () -> Unit = {}
}
