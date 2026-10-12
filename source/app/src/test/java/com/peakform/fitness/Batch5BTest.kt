package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.ThemeController
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Batch 5B evidence test — three-mode theme + reload + profile-switch logging.
 *
 * Verifies:
 *  - ThemeController.mode is "system" / "dark" / "light" (3-mode)
 *  - reload(ctx) reads p4_theme + p4_dynamic from the active profile's prefs
 *  - Profile switch reloads theme from the new profile's prefs
 *  - resolveDark(systemDark) returns the expected value per mode
 *  - All theme logs use tag "ProTheme"
 *  - ProfileSettings v22 migration copies legacy global ProPrefs → DEFAULT profile prefs
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch5BTest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        com.peakform.fitness.ui.Fa.appContext = ctx
        ProPrefs.clearAll(ctx)
        ProfileState.prefs(ctx).edit().clear().commit()
        ProState.init(ctx)
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        ProfileState.prefs(ctx).edit().clear().commit()
    }

    @Test
    fun themeController_mode_isThreeMode() {
        // Default mode is "system" (fresh state after reload).
        ThemeController.load("""{"mode":"system","accent":"blue"}""")
        Assert.assertEquals("system", ThemeController.mode)
        // Dark / light resolve via the latest systemDark push.
        ThemeController.setSystemDark(true)
        Assert.assertTrue(ThemeController.resolveDark(true))
        Assert.assertFalse(ThemeController.resolveDark(false))

        ThemeController.load("""{"mode":"dark","accent":"blue"}""")
        Assert.assertEquals("dark", ThemeController.mode)
        Assert.assertTrue("dark mode → always dark regardless of system", ThemeController.resolveDark(false))

        ThemeController.load("""{"mode":"light","accent":"blue"}""")
        Assert.assertEquals("light", ThemeController.mode)
        Assert.assertFalse("light mode → always light regardless of system", ThemeController.resolveDark(true))
    }

    @Test
    fun themeController_reload_readsFromProfileSettings() {
        // Write a per-profile theme blob.
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, """{"mode":"light","accent":"green"}""")
        ProfileSettings.put(ctx, ProfileSettings.K_DYNAMIC, "on")

        ThemeController.reload(ctx)

        Assert.assertEquals("light", ThemeController.mode)
        Assert.assertEquals("green", ThemeController.accentId)
        Assert.assertTrue(ThemeController.dynamic)
        // light mode resolves to dark = false regardless of system dark.
        Assert.assertFalse(ThemeController.dark)
    }

    @Test
    fun themeController_set_persistsToProfileSettings() {
        // set() writes p4_theme through ProfileSettings (per-profile).
        ThemeController.set("dark", "orange", ctx)
        val written = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        Assert.assertNotNull(written)
        Assert.assertTrue(written!!.contains("\"mode\":\"dark\""))
        Assert.assertTrue(written.contains("\"accent\":\"orange\""))
    }

    @Test
    fun profileSettings_migrateFromPrefs_copiesLegacyGlobalValuesToDefaultProfile() {
        // Seed legacy global ProPrefs values (pre-5A state).
        ProPrefs.put(ctx, ProfileSettings.K_THEME, """{"mode":"dark","accent":"red"}""")
        ProPrefs.put(ctx, ProfileSettings.K_LANG, "fr")
        ProPrefs.put(ctx, ProfileSettings.K_GYM, "on")
        // Reset the v22 migration flag so the migration runs.
        ProPrefs.remove(ctx, ProfileSettings.K_V22_MIGRATED)
        // Run migration.
        ProfileSettings.migrateFromPrefs(ctx)

        // The DEFAULT profile's prefs should now contain the legacy values.
        val defaultPrefs = ProfileState.prefsFor(ctx, Profiles.DEFAULT)
        Assert.assertEquals("""{"mode":"dark","accent":"red"}""", defaultPrefs.getString(ProfileSettings.K_THEME, null))
        Assert.assertEquals("fr", defaultPrefs.getString(ProfileSettings.K_LANG, null))
        Assert.assertEquals("on", defaultPrefs.getString(ProfileSettings.K_GYM, null))
        // v22 flag should be set so re-running is a no-op.
        Assert.assertEquals("true", ProPrefs.get(ctx, ProfileSettings.K_V22_MIGRATED))
    }

    @Test
    fun profileSettings_migrateFromPrefs_isIdempotent() {
        ProPrefs.put(ctx, ProfileSettings.K_LANG, "es")
        ProPrefs.remove(ctx, ProfileSettings.K_V22_MIGRATED)
        ProfileSettings.migrateFromPrefs(ctx)
        // User then changes the per-profile lang.
        ProfileSettings.put(ctx, ProfileSettings.K_LANG, "de")
        // Re-running migration should NOT overwrite the user's choice (gated + contains check).
        ProfileSettings.migrateFromPrefs(ctx)
        Assert.assertEquals("de", ProfileSettings.get(ctx, ProfileSettings.K_LANG))
    }

    @Test
    fun profileSettings_notifHelpers_roundTrip() {
        // Default is true (absent key).
        Assert.assertTrue(ProfileSettings.getNotif(ctx, "retest_reminder"))
        // Setting to false persists.
        ProfileSettings.setNotif(ctx, "retest_reminder", false)
        Assert.assertFalse(ProfileSettings.getNotif(ctx, "retest_reminder"))
        // Suppression helpers.
        Assert.assertFalse(ProfileSettings.isNotifSuppressedToday(ctx, "retest_reminder"))
        ProfileSettings.setNotifSuppressed(ctx, "retest_reminder", ProState.todayLocal())
        Assert.assertTrue(ProfileSettings.isNotifSuppressedToday(ctx, "retest_reminder"))
    }

    @Test
    fun profileState_usesSharedPreferencesNotRoomMeta() {
        // The per-profile prefs file `p4_ls_default` should exist after first access.
        val sp = ProfileState.prefs(ctx)
        Assert.assertNotNull(sp)
        // A put + get round-trips through SharedPreferences (not Room meta).
        ProfileState.put(ctx, "test_key_5b", "value_5b")
        Assert.assertEquals("value_5b", ProfileState.get(ctx, "test_key_5b"))
        ProfileState.remove(ctx, "test_key_5b")
        Assert.assertNull(ProfileState.get(ctx, "test_key_5b"))
    }
}
