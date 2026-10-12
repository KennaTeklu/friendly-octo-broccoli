package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.BackupSystem
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-11 evidence test — BackupSystem snapshots.
 *
 * snapshotNow writes a fresh JSON file under filesDir/backups/. The trim
 * to MAX_AUTO_SNAPSHOTS (8) keeps the directory small. listSnapshots
 * returns newest-first.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch11Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProState.init(ctx)
        ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
    }

    @Test
    fun snapshotNow_writesFile() {
        val f = BackupSystem.snapshotNow(ctx, "test")
        Assert.assertNotNull("snapshot written", f)
        Assert.assertTrue("file exists", f!!.exists())
        Assert.assertTrue("file non-empty", f.length() > 0)
    }

    @Test
    fun listSnapshots_returnsNewestFirst() {
        BackupSystem.snapshotNow(ctx, "first")
        Thread.sleep(20)
        BackupSystem.snapshotNow(ctx, "second")
        val list = BackupSystem.listSnapshots(ctx)
        Assert.assertTrue("at least 2 snapshots", list.size >= 2)
        // newest first
        Assert.assertTrue("first < second", list[0].lastModified() >= list[1].lastModified())
    }

    @Test
    fun deleteSnapshot_removesFile() {
        val f = BackupSystem.snapshotNow(ctx, "deleteme")!!
        val name = f.name
        Assert.assertTrue(BackupSystem.deleteSnapshot(ctx, name))
        Assert.assertFalse(f.exists())
    }
}
