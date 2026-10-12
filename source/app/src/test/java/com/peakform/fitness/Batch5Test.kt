package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProDb
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
 * BATCH-5 evidence tests.
 *
 * Item 1: Per-profile ownership — theme + dynamic now live in per-profile
 *         Room meta (ProfileSettings), not global ProPrefs.
 * Item 2: Follow system — "system" is the default mode for fresh install.
 * Item 4: Library opens all-collapsed every time.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch5Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProPrefs.clearAll(ctx)
        // Close any cached DB handles and delete the DB file so Room
        // re-creates the meta table fresh for this test.
        ProDb.close("pro.db")
        ctx.deleteDatabase("pro.db")
        ctx.deleteDatabase("pro.db-wal")
        ctx.deleteDatabase("pro.db-shm")
        // ProState.init is a no-op if store is already set (singleton), so
        // call reopenStore to force a fresh store + DB handle.
        try {
            ProState.reopenStore(ctx)
        } catch (_: Exception) {
            ProState.init(ctx)
        }
        // Force a write to ensure the DB + meta table are created
        ProfileSettings.put(ctx, "init_probe", "1")
    }

    @After
    fun tearDown() {
        try { ProState.flush() } catch (_: Exception) {}
        ProDb.close("pro.db")
        ProPrefs.clearAll(ctx)
    }

    /**
     * BATCH-5 Item 1: theme + dynamic are stored in per-profile Room meta,
     * NOT in global ProPrefs. After writing through ThemeController, the
     * values should be absent from ProPrefs and present in ProfileSettings.
     */
    @Test
    fun themeStoredInProfileSettings_notInProPrefs() {
        // Write a theme through ThemeController (the public API)
        ThemeController.set("dark", "green", ctx)

        // Should be absent from global ProPrefs
        Assert.assertNull("p4_theme should NOT be in global ProPrefs",
            ProPrefs.get(ctx, "p4_theme"))

        // Should be present in per-profile ProfileSettings
        val fromProfile = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        Assert.assertNotNull("p4_theme should be in per-profile ProfileSettings", fromProfile)
        Assert.assertTrue("p4_theme should contain accent 'green'",
            fromProfile!!.contains("\"accent\":\"green\""))
    }

    /**
     * BATCH-5 Item 1: Material You flag is also per-profile.
     */
    @Test
    fun dynamicStoredInProfileSettings_notInProPrefs() {
        ThemeController.setDynamic(true, ctx)

        Assert.assertNull("p4_dynamic should NOT be in global ProPrefs",
            ProPrefs.get(ctx, "p4_dynamic"))
        Assert.assertEquals("p4_dynamic should be 'on' in ProfileSettings",
            "on", ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC))
    }

    /**
     * BATCH-5 Item 2: fresh install defaults to "system" mode.
     */
    @Test
    fun freshInstall_defaultsToSystemMode() {
        // Simulate fresh install: no p4_theme key anywhere
        ThemeController.load(null)  // null = no prior choice

        Assert.assertEquals("Default mode should be 'system'", "system", ThemeController.mode)
    }

    /**
     * BATCH-5 Item 2: explicit dark/light choice overrides system.
     */
    @Test
    fun explicitChoiceOverridesSystem() {
        ThemeController.load("""{"mode":"dark","accent":"blue"}""")
        Assert.assertEquals("Mode should be 'dark' after explicit choice", "dark", ThemeController.mode)

        ThemeController.load("""{"mode":"light","accent":"red"}""")
        Assert.assertEquals("Mode should be 'light' after explicit choice", "light", ThemeController.mode)
    }

    /**
     * BATCH-5 Item 2: "system" mode resolves dark/light from OS.
     */
    @Test
    fun systemMode_resolvesFromOsTheme() {
        ThemeController.load(null)  // defaults to "system"
        ThemeController.resolveDark(systemDark = true)
        Assert.assertTrue("When OS is dark, system mode → dark=true", ThemeController.dark)

        ThemeController.resolveDark(systemDark = false)
        Assert.assertFalse("When OS is light, system mode → dark=false", ThemeController.dark)
    }

    /**
     * BATCH-5 Item 3: derived tint tokens are present in ProColors.
     * Verifies the color-mix equivalent is computed.
     */
    @Test
    fun derivedTintTokens_presentAndDistinct() {
        val pro = com.peakform.fitness.ui.buildProColors(dark = true, accent = ACCENTS[0])
        // tint, tintStrong, tintBg, tintLine, inkSoft, ring should all be non-null
        Assert.assertNotNull(pro.tint)
        Assert.assertNotNull(pro.tintStrong)
        Assert.assertNotNull(pro.tintBg)
        Assert.assertNotNull(pro.tintLine)
        Assert.assertNotNull(pro.inkSoft)
        Assert.assertNotNull(pro.ring)
        // tintStrong should be more saturated than tint (higher accent ratio)
        Assert.assertTrue("tintStrong should differ from tint", pro.tintStrong != pro.tint)
        // ring should be the accent with alpha
        Assert.assertTrue("ring alpha should be < 1", pro.ring.alpha < 1f)
    }

    /**
     * BATCH-5 Item 1: v22 migration copies legacy ProPrefs keys into
     * per-profile Room meta. Idempotent.
     */
    @Test
    fun migration_copiesLegacyPrefsToProfileSettings() {
        // Seed a legacy value in ProPrefs
        ProPrefs.put(ctx, "p4_gym", "on")
        ProPrefs.put(ctx, "p4_vocab", "7")
        // Clear the migration flag so it runs
        ProPrefs.remove(ctx, "p4_v22_migrated")

        // Run migration
        ProfileSettings.migrateFromPrefs(ctx)

        // Values should now be in per-profile ProfileSettings
        Assert.assertEquals("p4_gym should have migrated", "on",
            ProfileSettings.get(ctx, ProfileSettings.K_GYM))
        Assert.assertEquals("p4_vocab should have migrated", "7",
            ProfileSettings.get(ctx, ProfileSettings.K_VOCAB))

        // Migration flag should be set
        Assert.assertEquals("p4_v22_migrated should be set", "true",
            ProPrefs.get(ctx, "p4_v22_migrated"))

        // Running again should be idempotent — values unchanged
        ProfileSettings.migrateFromPrefs(ctx)
        Assert.assertEquals("p4_gym still 'on' after re-run", "on",
            ProfileSettings.get(ctx, ProfileSettings.K_GYM))
    }
}
