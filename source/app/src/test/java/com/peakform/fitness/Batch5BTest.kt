package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.ACCENTS
import com.peakform.fitness.ui.ThemeController
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-5B evidence test — per-profile SharedPreferences + 3-mode theme.
 *
 * 1. ProfileState writes land in `p4_ls_<profileId>` (not `p4_ls`).
 * 2. ProfileSettings.get/put round-trip through the active profile's file.
 * 3. ThemeController 3-mode toggle (system / dark / light) persists to
 *    the per-profile prefs atomically.
 * 4. ProfileSettings.migrateFromPrefs is idempotent.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch5BTest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProState.init(ctx)
        ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
        ProfileState.remove(ctx, ProfileState.K_V22_MIGRATED)
        ProfileState.remove(ctx, ProfileState.K_V18_MIGRATED)
        ThemeController.load("""{"mode":"dark","accent":"blue"}""")
        ThemeController.loadDynamic(null)
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
    }

    @Test
    fun profileState_writesLandInPerProfileFile() {
        ProfileState.put(ctx, "test_key", "test_value")
        val perProfile = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        Assert.assertEquals("test_value", perProfile.getString("test_key", null))
        // The global file must NOT contain the key.
        val global = ctx.getSharedPreferences("p4_ls", Context.MODE_PRIVATE)
        Assert.assertNull(global.getString("test_key", null))
    }

    @Test
    fun profileSettings_roundTrips() {
        ProfileSettings.put(ctx, ProfileSettings.K_GYM, "on")
        Assert.assertEquals("on", ProfileSettings.get(ctx, ProfileSettings.K_GYM))
        Assert.assertTrue(ProfileSettings.has(ctx, ProfileSettings.K_GYM))
        ProfileSettings.remove(ctx, ProfileSettings.K_GYM)
        Assert.assertFalse(ProfileSettings.has(ctx, ProfileSettings.K_GYM))
    }

    @Test
    fun themeController_3modeTogglePersistsAtomically() {
        ThemeController.set("system", "red", ctx)
        Assert.assertEquals("system", ThemeController.mode)
        val themeJson = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        Assert.assertNotNull(themeJson)
        Assert.assertTrue(themeJson!!.contains("\"mode\":\"system\""))
        Assert.assertTrue(themeJson.contains("\"accent\":\"red\""))
    }

    @Test
    fun profileSettings_notifToggleRoundTrips() {
        Assert.assertTrue("default on", ProfileSettings.getNotif(ctx, "retest_reminder"))
        ProfileSettings.setNotif(ctx, "retest_reminder", false)
        Assert.assertFalse(ProfileSettings.getNotif(ctx, "retest_reminder"))
        ProfileSettings.setNotif(ctx, "retest_reminder", true)
        Assert.assertTrue(ProfileSettings.getNotif(ctx, "retest_reminder"))
    }

    @Test
    fun migrateFromPrefs_isIdempotent() {
        // Seed legacy global values.
        ProPrefs.put(ctx, ProfileSettings.K_GYM, "on")
        ProPrefs.put(ctx, ProfileSettings.K_VOCAB, "7")
        // First run copies them into the per-profile file.
        ProfileSettings.migrateFromPrefs(ctx)
        Assert.assertEquals("on", ProfileSettings.get(ctx, ProfileSettings.K_GYM))
        Assert.assertEquals("7", ProfileSettings.get(ctx, ProfileSettings.K_VOCAB))
        // Mutate per-profile after migration.
        ProfileSettings.put(ctx, ProfileSettings.K_GYM, "off")
        // Second run must NOT overwrite the per-profile value.
        ProfileSettings.migrateFromPrefs(ctx)
        Assert.assertEquals("off", ProfileSettings.get(ctx, ProfileSettings.K_GYM))
    }

    @Test
    fun themeController_reloadReadsPerProfile() {
        // Write theme to per-profile prefs directly.
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, """{"mode":"light","accent":"green"}""")
        ProfileSettings.put(ctx, ProfileSettings.K_DYNAMIC, "on")
        ThemeController.reload(ctx)
        Assert.assertEquals("light", ThemeController.mode)
        Assert.assertEquals("green", ThemeController.accentId)
        Assert.assertTrue(ThemeController.dynamic)
    }
}
