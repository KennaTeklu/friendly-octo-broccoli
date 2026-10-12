package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.Vocab
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-6 evidence test — per-profile settings isolation through
 * ProfileSettings (BATCH-5A architecture).
 *
 * Covers the named constants + getters/setters + per-profile isolation.
 * (BATCH-6 is the Settings/UI work that consumes ProfileSettings; this
 * test pins the underlying contract so a refactor can't silently regress
 * per-profile isolation.)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch6Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProState.init(ctx)
        ProPrefs.clearAll(ctx)
        ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
    }

    /** Each named constant routes through ProfileState and lands in
     *  per-profile SharedPreferences, NOT the global ProPrefs file. */
    @Test
    fun namedKeys_routeThroughProfileState_toPerProfilePrefs() {
        // Write through ProfileSettings.
        ProfileSettings.put(ctx, ProfileSettings.K_GYM, "on")
        ProfileSettings.put(ctx, ProfileSettings.K_VOCAB, "8")
        ProfileSettings.put(ctx, ProfileSettings.K_LANG, "fr")

        // Storage should be in the per-profile file, NOT the global one.
        Assert.assertEquals("on", ProfileState.prefs(ctx).getString(ProfileSettings.K_GYM, null))
        Assert.assertEquals("8", ProfileState.prefs(ctx).getString(ProfileSettings.K_VOCAB, null))
        Assert.assertEquals("fr", ProfileState.prefs(ctx).getString(ProfileSettings.K_LANG, null))

        Assert.assertNull("K_GYM must NOT be in global prefs", ProPrefs.get(ctx, ProfileSettings.K_GYM))
        Assert.assertNull("K_VOCAB must NOT be in global prefs", ProPrefs.get(ctx, ProfileSettings.K_VOCAB))
        Assert.assertNull("K_LANG must NOT be in global prefs", ProPrefs.get(ctx, ProfileSettings.K_LANG))
    }

    /** Notification helpers (getNotif / setNotif) write to the per-profile
     *  file under K_NOTIF_PREFIX + key. */
    @Test
    fun notifHelpers_writeToPerProfilePrefsWithPrefix() {
        ProfileSettings.setNotif(ctx, "retest_reminder", false)
        Assert.assertFalse(ProfileSettings.getNotif(ctx, "retest_reminder", defaultOn = true))
        // absent → default-on
        Assert.assertTrue(ProfileSettings.getNotif(ctx, "deload_notice", defaultOn = true))
        // stored under p4_notif_retest_reminder
        Assert.assertEquals("false",
            ProfileState.prefs(ctx).getString(ProfileSettings.K_NOTIF_PREFIX + "retest_reminder", null))
    }

    /** Switching profile switches every ProfileSettings read. */
    @Test
    fun switchProfile_switchesProfileSettingsReads() {
        ProfileSettings.put(ctx, ProfileSettings.K_VOCAB, "5")
        val b = Profiles.create(ctx, "Sam", "🦊")
        // Switch to B, set its vocab to L8.
        Profiles.switchTo(ctx, b.id)
        ProfileSettings.put(ctx, ProfileSettings.K_VOCAB, "8")

        // Switch back to default — Vocab.currentLevel should reflect default's L5.
        Profiles.switchTo(ctx, Profiles.DEFAULT)
        Assert.assertEquals(5, Vocab.currentLevel(ctx))

        // Switch to B — level should be L8.
        Profiles.switchTo(ctx, b.id)
        Assert.assertEquals(8, Vocab.currentLevel(ctx))
    }
}
