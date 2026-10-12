package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * BackupSystem — BATCH-18.
 *
 * Profile-scoped backup: exports the active profile's prefs file
 * (p4_ls_<profileId>) AS-IS into a JSON bundle the user can save anywhere
 * via the system file picker, and restores it back into the active profile's
 * prefs file. The legacy Backup.kt already handles workouts/exercises/user
 * bundles; this file adds the per-profile prefs file as a separate export
 * so the user can move their preferences between devices without touching
 * the workout data.
 *
 * Storage: reads/writes through ProfileState.prefs(ctx) (the active profile's
 * SharedPreferences file). No global prefs are touched.
 */
object BackupSystem {

    data class PrefsBundle(
        val profileId: String,
        val exportedAt: String,
        val keys: Map<String, String>,
        val version: Int = 1,
    )

    /** Export the active profile's prefs file as a JSON bundle string. */
    fun export(ctx: Context): String {
        val profileId = Profiles.activeId(ctx)
        val sp = ProfileState.prefs(ctx)
        val keys = sp.all.mapValues { it.value?.toString() ?: "" }
        val bundle = PrefsBundle(profileId, ProState.nowIso(), keys)
        val obj = buildJsonObject {
            put("profileId", bundle.profileId)
            put("exportedAt", bundle.exportedAt)
            put("version", bundle.version)
            put("keys", buildJsonObject {
                keys.forEach { (k, v) -> put(k, v) }
            })
        }
        return ProJson.json.encodeToString(JsonObject.serializer(), obj)
    }

    /** Restore a JSON bundle string into the active profile's prefs file.
     *  Returns true on success. */
    fun restore(ctx: Context, raw: String): Boolean {
        return try {
            val obj = ProJson.json.decodeFromString(JsonObject.serializer(), raw)
            val keysObj = obj["keys"] as? JsonObject ?: return false
            val sp = ProfileState.prefs(ctx)
            val edit = sp.edit()
            keysObj.forEach { (k, v) ->
                val s = (v as? JsonPrimitive)?.content ?: v.toString()
                edit.putString(k, s)
            }
            edit.commit()
            ProLog.i("BACKUP", "restored ${keysObj.size} keys into ${ProfileState.prefsName(Profiles.activeId(ctx))}")
            true
        } catch (e: Exception) {
            ProLog.e("BACKUP", "restore failed: ${e.message}")
            false
        }
    }

    /** ISO timestamp helper for the bundle. */
    fun nowIso(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
}
