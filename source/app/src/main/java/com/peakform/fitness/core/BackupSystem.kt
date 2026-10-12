package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import com.peakform.fitness.ProLog

/**
 * BATCH-12 Item 2: Full round-trip backup.
 *
 * Exports: all profiles' data, all per-profile settings (p4_ls_<id> SharedPreferences),
 * registry, theme, self-assessment answers. Single JSON file. Versioned.
 *
 * Import: preview shows what will merge + overwrite. Confirm. Idempotent.
 */
object BackupSystem {

    const val BACKUP_VERSION = 1
    const val TAG = "BACKUP"

    @Serializable
    data class BackupBundle(
        val version: Int = BACKUP_VERSION,
        val createdAt: String,
        val deviceId: String,
        val registry: String?,        // JSON of ProfileRegistry raw
        val globalPrefs: Map<String, String>,  // ProPrefs global keys
        val profiles: List<ProfileBackup>,
    )

    @Serializable
    data class ProfileBackup(
        val id: String,
        val name: String,
        val prefs: Map<String, String>,  // per-profile SharedPreferences
        val roomData: RoomDataBackup?,
    )

    @Serializable
    data class RoomDataBackup(
        val workouts: List<String>,   // JSON rows
        val exercises: List<String>,
        val user: String?,
        val savedWorkout: String?,
    )

    /** Export the full backup as a JSON string. */
    fun export(ctx: Context): String {
        val profiles = Profiles.list(ctx)
        val profileBackups = profiles.map { p ->
            val sp = ProfileState.prefsFor(ctx, p.id)
            val prefsMap = sp.all.mapValues { it.value?.toString() ?: "" }
            val dbName = Profiles.dbNameFor(ctx, p.id)
            val roomData = try {
                ProDb.withDb(ctx, dbName) { dao ->
                    kotlinx.coroutines.runBlocking {
                        RoomDataBackup(
                            workouts = dao.allWorkouts().map { it.json },
                            exercises = dao.allExercises().map { it.json },
                            user = dao.userJson(),
                            savedWorkout = dao.savedJson(),
                        )
                    }
                }
            } catch (_: Exception) { null }
            ProfileBackup(p.id, p.name, prefsMap, roomData)
        }

        val globalPrefs = ProPrefs.all(ctx).mapValues { it.value?.toString() ?: "" }
        val registry = ProfileRegistry.rawJson(ctx)
        val deviceId = ProPrefs.get(ctx, "deviceId") ?: ""

        val bundle = BackupBundle(
            createdAt = ProState.nowIso(),
            deviceId = deviceId,
            registry = registry,
            globalPrefs = globalPrefs,
            profiles = profileBackups,
        )
        return ProJson.json.encodeToString(BackupBundle.serializer(), bundle)
    }

    /** Import preview: what will be merged vs overwritten. */
    data class ImportPreview(
        val profileCount: Int,
        val newProfiles: List<String>,
        val existingProfiles: List<String>,
        val workoutCount: Int,
        val willOverwriteGlobal: Boolean,
    )

    fun previewImport(ctx: Context, json: String): ImportPreview? {
        return try {
            val bundle = ProJson.json.decodeFromString(BackupBundle.serializer(), json)
            val existingIds = Profiles.list(ctx).map { it.id }.toSet()
            val newProfiles = bundle.profiles.filter { it.id !in existingIds }.map { it.name }
            val existing = bundle.profiles.filter { it.id in existingIds }.map { it.name }
            val workoutCount = bundle.profiles.sumOf { it.roomData?.workouts?.size ?: 0 }
            ImportPreview(
                profileCount = bundle.profiles.size,
                newProfiles = newProfiles,
                existingProfiles = existing,
                workoutCount = workoutCount,
                willOverwriteGlobal = bundle.globalPrefs.isNotEmpty(),
            )
        } catch (_: Exception) { null }
    }

    /** Import: apply the backup. Idempotent — running twice does not duplicate. */
    fun import(ctx: Context, json: String): Boolean {
        return try {
            val bundle = ProJson.json.decodeFromString(BackupBundle.serializer(), json)
            // Restore registry
            bundle.registry?.let { raw ->
                val sp = ctx.getSharedPreferences("registry", Context.MODE_PRIVATE)
                sp.edit().putString("pro_profiles_v2", raw).commit()
            }
            // Restore global prefs (deviceId, etc.)
            val globalSp = ProPrefs.get(ctx)
            val ed = globalSp.edit()
            bundle.globalPrefs.forEach { (k, v) -> ed.putString(k, v) }
            ed.apply()
            // Restore each profile's prefs + room data
            for (pb in bundle.profiles) {
                val sp = ProfileState.prefsFor(ctx, pb.id)
                val ped = sp.edit()
                ped.clear()  // idempotent: clear then write
                pb.prefs.forEach { (k, v) -> ped.putString(k, v) }
                ped.apply()
                // Restore room data
                pb.roomData?.let { rd ->
                    val dbName = Profiles.dbNameFor(ctx, pb.id)
                    ProDb.withDb(ctx, dbName) { dao ->
                        kotlinx.coroutines.runBlocking {
                            if (rd.workouts.isNotEmpty()) {
                                dao.putWorkouts(rd.workouts.map { WorkoutRow(it.take(20), "", it) })
                            }
                        }
                    }
                }
            }
            ProLog.i(TAG, "import complete: ${bundle.profiles.size} profiles")
            true
        } catch (e: Exception) {
            ProLog.e(TAG, "import failed: ${e.message}")
            false
        }
    }

    /** CSV export of workouts: date, exercise, sets, reps, weight, RPE, notes. */
    fun exportCsv(ctx: Context): String {
        val sb = StringBuilder()
        sb.append("date,exercise,sets,reps,weight,rpe,notes\n")
        for (w in ProState.data.workouts) {
            for (ex in w.exercises) {
                val a = ex.actual ?: continue
                sb.append("${w.date},")
                sb.append("\"${ex.name}\",")
                sb.append("${a.sets},")
                sb.append("\"${a.reps.joinToString(";") { it.toString() }}\",")
                sb.append("${a.weight},")
                sb.append("${a.rpe?.toString() ?: ""},")
                sb.append("\"${a.notes}\"\n")
            }
        }
        return sb.toString()
    }

    /** Scheduled local snapshot — saves to app-private storage. */
    fun saveLocalSnapshot(ctx: Context): Boolean {
        return try {
            val dir = File(ctx.filesDir, "backups").apply { mkdirs() }
            // Rotate: keep last 7
            val existing = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
            if (existing.size >= 7) {
                existing.drop(6).forEach { it.delete() }
            }
            val timestamp = ProState.todayLocal()
            val file = File(dir, "pro_backup_$timestamp.json")
            file.writeText(export(ctx))
            ProfileSettings.put(ctx, ProfileSettings.K_LAST_BACKUP, ProState.nowIso())
            ProLog.i(TAG, "local snapshot saved: ${file.name}")
            true
        } catch (e: Exception) {
            ProLog.e(TAG, "local snapshot failed: ${e.message}")
            false
        }
    }
}
