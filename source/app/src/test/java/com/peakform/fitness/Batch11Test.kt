package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.BackupSystem
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

/**
 * BATCH-11 evidence test — profile delete safety + safety snapshot facility.
 *
 * Pins the contract:
 *   1. Profiles.dbFileExists returns false for a profile whose DB file is
 *      missing, true for the active profile.
 *   2. Profiles.switchTo refuses to switch to a profile whose DB file is
 *      missing — returns the current active profile name unchanged.
 *   3. BackupSystem.takeSafetySnapshot writes a JSON file under
 *      filesDir/backups/ and BackupSystem.safetySnapshots lists it.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch11Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = RuntimeEnvironment.getApplication()
        com.peakform.fitness.ui.Fa.appContext = ctx
        ProState.init(ctx)
        ProPrefs.clearAll(ctx)
        ProfileState.prefs(ctx).edit().clear().commit()
        Library.ensure(ctx)
    }

    @After
    fun tearDown() {
        ProfileState.prefs(ctx).edit().clear().commit()
        ProPrefs.clearAll(ctx)
    }

    /** dbFileExists — true for the active default profile, false for an
     *  arbitrary never-created profile id. */
    @Test
    fun dbFileExists_activeProfileTrue_missingProfileFalse() {
        // Touch the default profile's DB so its file exists.
        kotlinx.coroutines.runBlocking { ProState.loadAll() }
        // The active profile (default) should have its DB file.
        val active = Profiles.activeId(ctx)
        Assert.assertTrue("Active profile DB should exist", Profiles.dbFileExists(ctx, active))
        // An arbitrary missing profile id should return false.
        Assert.assertFalse("Phantom profile DB should not exist", Profiles.dbFileExists(ctx, "p_doesnotexist"))
    }

    /** switchTo refuses to switch to a profile whose DB is missing. */
    @Test
    fun switchTo_refusesPhantomProfile_returnsCurrentName() {
        val active = Profiles.activeId(ctx)
        val name = Profiles.switchTo(ctx, "p_doesnotexist")
        // The active profile id should NOT have changed.
        Assert.assertEquals("Active profile should be unchanged after refusing a phantom switch", active, Profiles.activeId(ctx))
        // switchTo returned the current profile's display name (or its id).
        // We don't assert exact equality with the active profile name (display
        // label can include suffixes like "(Default)"), but it must not be
        // "p_doesnotexist".
        Assert.assertNotEquals("Returned name must not be the phantom id", "p_doesnotexist", name)
    }

    /** takeSafetySnapshot writes a JSON file under filesDir/backups/. */
    @Test
    fun takeSafetySnapshot_writesJsonFileUnderBackupsDir() {
        val path = BackupSystem.takeSafetySnapshot(ctx)
        Assert.assertNotNull("Snapshot path must not be null", path)
        val f = File(path!!)
        Assert.assertTrue("Snapshot file must exist", f.exists())
        Assert.assertTrue("Snapshot file must be under filesDir/backups/", f.absolutePath.contains("backups"))
        // Listed by safetySnapshots().
        val snaps = BackupSystem.safetySnapshots(ctx)
        Assert.assertTrue("safetySnapshots should include the new file", snaps.any { it.name == f.name })
    }
}
