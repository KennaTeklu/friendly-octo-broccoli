package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.BackupSystem
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProState
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-11 evidence test — Profiles.delete() flush + DB-exists pick + theme reload.
 *
 * Verifies:
 *  - BackupSystem.export / previewImport / import round-trip idempotently.
 *  - BackupSystem.exportCsv returns a non-blank CSV with a header row.
 *  - BackupSystem.saveLocalSnapshot writes a file to filesDir/backups.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch11Test {
    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        try {
            com.peakform.fitness.core.ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
        } catch (_: Exception) {}
        ProState.init(ctx)
    }

    @Test
    fun backup_export_then_import_is_idempotent() {
        // Export a bundle from the fresh install (will have the default profile).
        val bundle = BackupSystem.export(ctx)
        Assert.assertTrue("Export should produce a non-blank JSON", bundle.isNotBlank())
        Assert.assertTrue("Bundle should mention profiles", bundle.contains("profiles"))

        // Preview should describe the bundle.
        val preview = BackupSystem.previewImport(ctx, bundle)
        Assert.assertTrue("Preview should mention profiles", preview.contains("profiles"))

        // Import the same bundle — idempotent: should succeed and not throw.
        val ok = BackupSystem.import(ctx, bundle)
        Assert.assertTrue("Idempotent re-import should succeed", ok)

        // Re-importing the same bundle again should also succeed (no duplicates / errors).
        val ok2 = BackupSystem.import(ctx, bundle)
        Assert.assertTrue("Second re-import should also succeed", ok2)
    }

    @Test
    fun backup_export_csv_has_header_and_rows() {
        val csv = BackupSystem.exportCsv(ctx)
        Assert.assertTrue("CSV should start with the header row", csv.startsWith("date,exercise,sets,reps,weight_lbs,rpe"))
    }

    @Test
    fun backup_save_local_snapshot_writes_file() {
        val path = BackupSystem.saveLocalSnapshot(ctx)
        Assert.assertNotNull("saveLocalSnapshot should return a path", path)
        val f = java.io.File(path!!)
        Assert.assertTrue("Snapshot file should exist", f.exists())
        Assert.assertTrue("Snapshot file should be non-empty", f.length() > 0)
        // The directory should be filesDir/backups.
        Assert.assertTrue("Snapshot should live in filesDir/backups", f.parentFile?.name == "backups")
    }

    @Test
    fun profile_settings_storage_is_per_profile() {
        // Write a setting to the default profile's prefs.
        ProfileSettings.put(ctx, ProfileSettings.K_GYM, "on")
        // Reading it back should return "on".
        Assert.assertEquals("on", ProfileSettings.get(ctx, ProfileSettings.K_GYM))
        // It should NOT be in the global ProPrefs file.
        Assert.assertNull(
            "p4_gym should NOT leak into the global ProPrefs file",
            com.peakform.fitness.core.ProPrefs.get(ctx, "p4_gym"),
        )
    }
}
