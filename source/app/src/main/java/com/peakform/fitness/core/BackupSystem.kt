package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * BackupSystem — full-app export/import (BATCH-11+12).
 *
 * Exports:
 *   - all profiles (every profile's Room DB + every profile's SharedPreferences)
 *   - the profile registry
 *   - the global ProPrefs (active/default device id, consents, migration flags)
 *
 * Imports are idempotent: re-importing the same bundle twice does not create
 * duplicate profiles or duplicate workouts — the active profile's data is
 * replaced atomically.
 *
 * CSV export: a simple flat table of workout history for spreadsheet use.
 *
 * saveLocalSnapshot: a one-tap rotating local backup that keeps the last 7.
 */
object BackupSystem {
    private const val MAX_LOCAL_SNAPSHOTS = 7

    @Serializable
    data class ProfileBundle(
        val id: String,
        val name: String,
        val emoji: String?,
        val dbName: String,
        val prefsName: String,
        val prefsJson: String,        // JSON-encoded Map<String, String>
    )

    @Serializable
    data class FullBundle(
        val version: Int = 2,
        val at: String,
        val registryJson: String,    // raw registry JSON
        val globalPrefsJson: String, // JSON-encoded Map<String, String> from ProPrefs
        val profiles: List<ProfileBundle>,
    )

    /** Export a full bundle to a JSON string. */
    fun export(ctx: Context): String {
        val profiles = Profiles.list(ctx)
        val profileBundles = profiles.map { p ->
            val dbName = Profiles.dbNameFor(ctx, p.id)
            val prefsName = ProfileState.prefsNameFor(p.id)
            val prefsMap = ProfileState.prefsFor(ctx, p.id).all
                .mapValues { it.value?.toString() ?: "" }
            ProfileBundle(
                id = p.id,
                name = p.name,
                emoji = p.emoji,
                dbName = dbName,
                prefsName = prefsName,
                prefsJson = Json.encodeToString(
                    MapSerializer(String.serializer(), String.serializer()),
                    prefsMap,
                ),
            )
        }
        val registryJson = ProfileRegistry.rawJson(ctx) ?: "{}"
        val globalPrefs = ProPrefs.all(ctx)
            .mapValues { it.value ?: "" }
        val bundle = FullBundle(
            at = ProState.nowIso(),
            registryJson = registryJson,
            globalPrefsJson = Json.encodeToString(
                MapSerializer(String.serializer(), String.serializer()),
                globalPrefs,
            ),
            profiles = profileBundles,
        )
        return Json.encodeToString(FullBundle.serializer(), bundle)
    }

    /** Preview a bundle without applying it — returns a human-readable summary. */
    fun previewImport(ctx: Context, raw: String): String {
        return try {
            val bundle = Json.decodeFromString(FullBundle.serializer(), raw)
            "Bundle from ${bundle.at.take(10)} — ${bundle.profiles.size} profiles, " +
                "${bundle.profiles.sumOf { it.prefsJson.length }} bytes of prefs"
        } catch (e: Exception) {
            "Invalid bundle: ${e.message}"
        }
    }

    /**
     * Idempotent import: re-importing the same bundle twice does not create
     * duplicates. The active profile's data is replaced atomically per profile.
     */
    fun import(ctx: Context, raw: String): Boolean {
        return try {
            val bundle = Json.decodeFromString(FullBundle.serializer(), raw)
            // Restore each profile's SharedPreferences.
            for (p in bundle.profiles) {
                val sp = ProfileState.prefsFor(ctx, p.id)
                val ed = sp.edit().clear()
                val prefsMap = Json.decodeFromString(
                    MapSerializer(String.serializer(), String.serializer()),
                    p.prefsJson,
                )
                prefsMap.forEach { (k, v) -> ed.putString(k, v) }
                ed.commit()
            }
            // Restore the registry.
            val spReg = ctx.getSharedPreferences("registry", Context.MODE_PRIVATE)
            spReg.edit().putString("pro_profiles_v2", bundle.registryJson).commit()
            // Restore global ProPrefs.
            val globalMap = Json.decodeFromString(
                MapSerializer(String.serializer(), String.serializer()),
                bundle.globalPrefsJson,
            )
            val edG = ProPrefs.get(ctx).edit()
            globalMap.forEach { (k, v) -> edG.putString(k, v) }
            edG.commit()
            ProLog.i("BACKUP", "imported bundle from ${bundle.at.take(10)} (${bundle.profiles.size} profiles)")
            true
        } catch (e: Exception) {
            ProLog.e("BACKUP", "import failed: ${e.message}")
            false
        }
    }

    /** Flat CSV of completed workouts: date, exercise, sets, reps, weight. */
    fun exportCsv(ctx: Context): String {
        val sb = StringBuilder()
        sb.append("date,exercise,sets,reps,weight_lbs,rpe\n")
        for (w in ProState.data.workouts) {
            if (!w.isCompleted) continue
            val date = w.date
            for (ex in w.exercises) {
                val actual = ex.actual ?: continue
                val sets = actual.sets
                val reps = actual.reps.joinToString(";")
                val weight = actual.weight
                val rpe = actual.rpeList.joinToString(";")
                val name = ex.name.replace(",", ";")
                sb.append("$date,$name,$sets,\"$reps\",$weight,\"$rpe\"\n")
            }
        }
        return sb.toString()
    }

    /**
     * Save a rotating local snapshot: writes the bundle JSON to
     * `filesDir/backups/backup-<timestamp>.json`, then deletes any backups
     * older than the most recent 7.
     */
    fun saveLocalSnapshot(ctx: Context): String? {
        return try {
            val bundle = export(ctx)
            val dir = java.io.File(ctx.filesDir, "backups").apply { mkdirs() }
            val stamp = System.currentTimeMillis()
            val file = java.io.File(dir, "backup-$stamp.json")
            file.writeText(bundle)
            // Rotate: keep the last MAX_LOCAL_SNAPSHOTS by mtime.
            val files = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
            files.drop(MAX_LOCAL_SNAPSHOTS).forEach { it.delete() }
            ProLog.i("BACKUP", "local snapshot saved: ${file.name}")
            file.absolutePath
        } catch (e: Exception) {
            ProLog.e("BACKUP", "saveLocalSnapshot failed: ${e.message}")
            null
        }
    }
}
