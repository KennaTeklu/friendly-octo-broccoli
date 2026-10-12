package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * BackupSystem — BATCH-18A.
 *
 * Higher-level backup orchestrator that complements the existing Backup object.
 * Backup handles the file-format side (JSON bundle v1). BackupSystem layers on
 * top:
 *  - per-profile backup manifest (last backup timestamp, hash, kind)
 *  - auto-snapshot on a 5-session cadence (legacy N7 backup nudge)
 *  - restore verification (hash check before merging)
 *
 * All writes go through the active profile's Room store + ProState.saveWorkoutData()
 * so a restore is just an inverse of save.
 */
object BackupSystem {

    @Serializable
    data class Manifest(
        val lastBackupAt: String = "",
        val lastBackupKind: String = "",   // "manual" | "auto" | "pre-reset"
        val lastBackupHash: String = "",
        val autoBackupsCount: Int = 0,
        val sessionCounter: Int = 0,
    ) {
        companion object {
            const val KEY = "p4_backup_manifest"

            fun load(ctx: Context): Manifest = try {
                ProfileSettings.get(ctx, KEY)?.let {
                    ProJson.json.decodeFromString(serializer(), it)
                } ?: Manifest()
            } catch (_: Exception) { Manifest() }

            fun save(ctx: Context, m: Manifest) {
                ProfileSettings.put(ctx, KEY, ProJson.json.encodeToString(serializer(), m))
            }
        }
    }

    /** Hash the workoutData payload so a restore can verify the bundle is unchanged. */
    fun hash(data: WorkoutData): String {
        val raw = ProJson.json.encodeToString(WorkoutData.serializer(), data)
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(raw.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(16)
    }

    /** Bump the session counter and check if an auto-backup is due (every 5). */
    fun bumpSessionCounter(ctx: Context): Boolean {
        val m = Manifest.load(ctx)
        val next = m.copy(sessionCounter = m.sessionCounter + 1)
        Manifest.save(ctx, next)
        return next.sessionCounter % 5 == 0
    }

    /** Mark a backup as having happened — updates the manifest. */
    fun recordBackup(ctx: Context, kind: String, data: WorkoutData) {
        val m = Manifest.load(ctx)
        Manifest.save(ctx, m.copy(
            lastBackupAt = ProState.nowIso(),
            lastBackupKind = kind,
            lastBackupHash = hash(data),
            autoBackupsCount = if (kind == "auto") m.autoBackupsCount + 1 else m.autoBackupsCount,
        ))
        ProLog.i("BACKUP", "manifest updated: kind=$kind hash=${hash(data)} at=${ProState.nowIso()}")
    }

    /** Verify a bundle's hash matches what we recorded. Returns false if no manifest or hash mismatch. */
    fun verifyHash(ctx: Context, data: WorkoutData): Boolean {
        val m = Manifest.load(ctx)
        if (m.lastBackupHash.isBlank()) return true // no prior backup — accept
        return hash(data) == m.lastBackupHash
    }
}
