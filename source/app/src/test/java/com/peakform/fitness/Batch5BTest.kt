package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.Profiles
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
 * BATCH-5B evidence test — theme write-through + reload on profile switch.
 *
 * Verifies:
 *  - ThemeController.set() writes to ProfileSettings in the same call
 *    (per-profile SharedPreferences, not the global ProPrefs file).
 *  - ThemeController.reload() re-reads K_THEME/K_DYNAMIC from ProfileSettings.
 *  - 3-mode model: "system" / "dark" / "light" persists correctly.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch5BTest {
    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        // Clear per-profile + global prefs for a clean slate.
        try {
            com.peakform.fitness.core.ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
        } catch (_: Exception) {}
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
        // Force-load defaults.
        ThemeController.load("""{"mode":"dark","accent":"blue"}""")
        ThemeController.loadDynamic(null)
    }

    @After
    fun tearDown() {
        try {
            com.peakform.fitness.core.ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
        } catch (_: Exception) {}
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
    }

    @Test
    fun set_persists_to_profile_settings() {
        ThemeController.set("light", "red", ctx)
        val stored = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        Assert.assertNotNull("set() must persist K_THEME", stored)
        Assert.assertTrue("K_THEME must contain accent 'red'", stored!!.contains("\"accent\":\"red\""))
        Assert.assertEquals("ThemeController.accentId should be 'red'", "red", ThemeController.accentId)
        Assert.assertEquals("ThemeController.mode should be 'light'", "light", ThemeController.mode)
        Assert.assertFalse("ThemeController.dark should be false for light mode", ThemeController.dark)
    }

    @Test
    fun setAccentAndDynamic_writes_both_keys_atomically() {
        ThemeController.setAccentAndDynamic("dark", "green", dynamic = true, ctx = ctx)
        Assert.assertEquals("p4_dynamic should be 'on'", "on", ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC))
        val theme = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        Assert.assertNotNull("K_THEME should be written", theme)
        Assert.assertTrue("K_THEME should contain accent 'green'", theme!!.contains("\"accent\":\"green\""))
    }

    @Test
    fun reload_reads_from_profile_settings() {
        // Manually write a theme blob to the active profile's prefs.
        ProfileSettings.putSync(ctx, ProfileSettings.K_THEME, """{"mode":"system","accent":"purple"}""")
        ProfileSettings.putSync(ctx, ProfileSettings.K_DYNAMIC, "on")

        ThemeController.reload(ctx)

        Assert.assertEquals("After reload, mode should be 'system'", "system", ThemeController.mode)
        Assert.assertEquals("After reload, accent should be 'purple'", "purple", ThemeController.accentId)
        Assert.assertTrue("After reload, dynamic should be ON", ThemeController.dynamic)
    }

    @Test
    fun resolveDark_system_mode_follows_platform() {
        ThemeController.set("system", "blue", ctx)
        // System dark → ThemeController.dark should resolve true.
        Assert.assertTrue("system mode + systemDark=true → dark=true", ThemeController.resolveDark(true))
        // System light → ThemeController.dark should resolve false.
        Assert.assertFalse("system mode + systemDark=false → dark=false", ThemeController.resolveDark(false))
    }

    @Test
    fun three_mode_persistence_round_trip() {
        ThemeController.set("dark", "blue", ctx)
        Assert.assertEquals("dark", ThemeController.mode)
        Assert.assertTrue(ThemeController.dark)

        ThemeController.set("light", "blue", ctx)
        Assert.assertEquals("light", ThemeController.mode)
        Assert.assertFalse(ThemeController.dark)

        ThemeController.set("system", "blue", ctx)
        Assert.assertEquals("system", ThemeController.mode)

        ThemeController.reload(ctx)
        Assert.assertEquals("system", ThemeController.mode)
    }
}
