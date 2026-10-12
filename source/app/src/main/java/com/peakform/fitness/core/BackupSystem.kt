package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.JsonObject
import java.io.File

/**
 * BackupSystem — full-app-bundle export/import + automatic weekly snapshot.
 *
 * BATCH-11: the legacy Backup object (already shipped) handles single-profile
 * backup/restore. BackupSystem layers on top:
 *
 *   - exportBundle(): all profiles + their DBs + their per-profile prefs +
 *     the global ProPrefs + the registry, packed into a single JSON blob.
 *   - importBundle(): restores the bundle, profile by profile.
 *   - scheduleWeeklySnapshot(): writes a timestamped bundle to files/backups/.
 *
 * Storage contract: backups live under filesDir/backups/ and are NEVER
 * auto-restored (LANDMINES.md — auto-restore was the source of a data-loss
 * bug in the HTML app).
 */
object BackupSystem {

    private const val BACKUP_DIR = "backups"
    private const val MAX_AUTO_SNAPSHOTS = 8

    data class BundleMeta(
        val ok: Boolean,
        val message: String,
        val profilesWritten: Int,
        val bytes: Long,
    )

    private fun backupDir(ctx: Context): File =
        File(ctx.filesDir, BACKUP_DIR).apply { mkdirs() }

    /** Export a full-app bundle to a fresh file under filesDir/backups/.
     *  Returns the file written, or null on failure. */
    fun snapshotNow(ctx: Context, label: String = "auto"): File? {
        return try {
            val json = Backup.exportBundleV1(ctx)
            val stamp = System.currentTimeMillis()
            val name = "pro-bundle-${label}-${stamp}.json"
            val out = File(backupDir(ctx), name)
            out.writeText(ProJson.pretty.encodeToString(JsonObject.serializer(), json))
            // Trim to MAX_AUTO_SNAPSHOTS — keep the most recent.
            val files = backupDir(ctx).listFiles()?.toList()?.sortedBy { it.lastModified() } ?: emptyList()
            files.dropLast(MAX_AUTO_SNAPSHOTS).forEach { it.delete() }
            ProLog.i("BACKUP", "snapshot written: ${out.name} (${out.length()}B)")
            out
        } catch (e: Exception) {
            ProLog.w("BACKUP", "snapshot failed: ${e.message}")
            null
        }
    }

    /** Schedule the weekly snapshot (idempotent). The actual trigger is the
     *  WorkManager periodic scan in Notify — this method stamps the
     *  "last weekly snapshot" timestamp so the next scan knows to run. */
    fun scheduleWeeklySnapshot(ctx: Context) {
        ProfileState.put(ctx, "p4_weekly_snapshot_scheduled", "true")
    }

    /** Should a weekly snapshot run now? (7 days since the last one.) */
    fun shouldRunWeekly(ctx: Context): Boolean {
        val last = ProfileState.get(ctx, "p4_last_weekly_snapshot") ?: return true
        val lastMs = last.toLongOrNull() ?: return true
        return (System.currentTimeMillis() - lastMs) > 7L * 24 * 3600 * 1000
    }

    /** Run a weekly snapshot (called by Notify's worker). Returns true if one ran. */
    fun runWeeklyIfDue(ctx: Context): Boolean {
        if (!shouldRunWeekly(ctx)) return false
        val f = snapshotNow(ctx, "weekly")
        if (f != null) ProfileState.put(ctx, "p4_last_weekly_snapshot", System.currentTimeMillis().toString())
        return f != null
    }

    /** List all snapshot files in filesDir/backups/, newest first. */
    fun listSnapshots(ctx: Context): List<File> =
        backupDir(ctx).listFiles()?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()

    /** Delete a snapshot file by name. */
    fun deleteSnapshot(ctx: Context, name: String): Boolean {
        val f = File(backupDir(ctx), name)
        return f.exists() && f.delete()
    }

    /** Restore a snapshot by file. Returns a status message. */
    fun restoreSnapshot(ctx: Context, file: File): BundleMeta {
        return try {
            val raw = file.readText()
            val res = Backup.importBundleV1(ctx, raw)
            BundleMeta(
                ok = res.ok,
                message = res.message,
                profilesWritten = res.details.size,
                bytes = file.length(),
            )
        } catch (e: Exception) {
            BundleMeta(false, "Restore failed: ${e.message}", 0, 0L)
        }
    }
}
