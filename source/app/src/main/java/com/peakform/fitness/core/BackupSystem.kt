package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * BackupSystem — BATCH-12 scheduled backup + safety snapshot facility.
 *
 * The legacy app exported backups only on explicit user action; BATCH-12
 * adds a periodic safety snapshot to local files (filesDir/backups/) so a
 * crashed or accidentally reset device keeps the last 5 sessions recoverable.
 *
 * Snapshots are written as Format-A JSON to filesDir/backups/safety-N.json
 * (rolling N=1..5). The user can restore from Settings → Data & backup →
 * Snapshots (existing SnapshotsScreen already handles UI).
 *
 * This object owns the file-roll logic; the actual export shape comes from
 * Backup.exportFormatA() so the bundle is byte-compatible with the manual
 * export path.
 */
object BackupSystem {
    private const val MAX_SAFETY = 5
    private const val DIR_NAME = "backups"

    private fun dir(ctx: Context): File = File(ctx.filesDir, DIR_NAME).apply { mkdirs() }

    /** Take a safety snapshot to the rolling file set. Returns the file path or null. */
    fun takeSafetySnapshot(ctx: Context): String? = try {
        val payload = Backup.exportFormatA(ctx, "safety snapshot")
        val stamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val file = File(dir(ctx), "safety-$stamp.json")
        file.writeText(ProJson.pretty.encodeToString(JsonObject.serializer(), payload))
        // roll: keep only the newest MAX_SAFETY
        dir(ctx).listFiles()?.toList()?.sortedByDescending { it.lastModified() }?.drop(MAX_SAFETY)?.forEach { it.delete() }
        ProLog.i("BACKUP", "safety snapshot written: ${file.name}")
        file.absolutePath
    } catch (e: Exception) {
        ProLog.w("BACKUP", "safety snapshot failed: ${e.message}")
        null
    }

    /** List the safety snapshots available for restore (newest first). */
    fun safetySnapshots(ctx: Context): List<File> = try {
        dir(ctx).listFiles()?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()
    } catch (_: Exception) { emptyList() }

    /** Restore a safety snapshot file back into ProState via Backup.import(). */
    suspend fun restore(ctx: Context, file: File): Boolean = try {
        val raw = file.readText()
        Backup.import(ctx, raw)
        true
    } catch (e: Exception) {
        ProLog.w("BACKUP", "restore failed: ${e.message}")
        false
    }

    /** Periodic trigger: take a snapshot every N completed workouts (BATCH-12). */
    fun maybeAutoSnapshot(ctx: Context) {
        val completed = ProState.data.workouts.count { it.isCompleted }
        if (completed in 1..9999 && completed % 5 == 0) {
            takeSafetySnapshot(ctx)
        }
    }
}
