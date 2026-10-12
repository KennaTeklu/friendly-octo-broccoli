package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProDb
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
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
 * BATCH-6 evidence test — 5A+5B+6+7 foundation.
 *
 * Verifies the four invariants the new theming/profile architecture guarantees:
 *   1. setAccent updates the in-memory Compose state immediately (no deferred refresh)
 *   2. Profile switch reloads the active profile's theme from per-profile prefs
 *   3. write + refresh happen in the SAME call (atomic)
 *   4. Tapping a swatch changes the visible app color synchronously
 *   5. Switching A → B changes the theme, and switching B → A restores it
 *   6. resetToDefault() clears custom exercises from the library + prefs
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch6Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProDb.close("pro.db")
        ctx.deleteDatabase("pro.db")
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
        // Per-profile prefs (p4_ls_<profileId>) live in a separate SharedPreferences file
        // from the global p4_ls file — clear them so each test starts fresh.
        ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
        ProState.init(ctx)
        kotlinx.coroutines.runBlocking { if (!ProState.initialized) ProState.loadAll() }
        // Cold-start load path (mirrors MainActivity.kt)
        ThemeController.load(ProfileSettings.get(ctx, ProfileSettings.K_THEME))
        ThemeController.loadDynamic(ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC))
        ThemeController.resolveDark(false)
    }

    @After
    fun tearDown() {
        ProState.flush()
        ProDb.close("pro.db")
        // Clear the per-profile prefs so tests don't leak state.
        ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
    }

    /** 1. setAccent updates the in-memory Compose state IMMEDIATELY (no deferred refresh). */
    @Test
    fun setAccent_updatesInMemoryStateImmediately() {
        ThemeController.set("dark", "blue", ctx)
        Assert.assertEquals("Precondition: accent should start as 'blue'", "blue", ThemeController.accentId)

        ThemeController.setAccentAndDynamic("dark", "red", dynamic = false, ctx = ctx)

        // In-memory state updated synchronously — no need to call load() or notify.
        Assert.assertEquals("Accent should be 'red' immediately after setAccent", "red", ThemeController.accentId)
        Assert.assertEquals("accent() should return the new accent def", "red", ThemeController.accent().id)
        Assert.assertFalse("Dynamic should be false", ThemeController.dynamic)
    }

    /** 2. Profile switch reloads the active profile's theme from per-profile prefs. */
    @Test
    fun profileSwitch_reloadsTheme() {
        // Profile A (default) — set accent to green
        ThemeController.setAccentAndDynamic("dark", "green", dynamic = false, ctx = ctx)
        Assert.assertEquals("Precondition: A accent is green", "green",
            ProfileSettings.get(ctx, ProfileSettings.K_THEME)?.substringAfter("\"accent\":\"")?.substringBefore("\""))

        // Create profile B and switch to it
        val b = Profiles.create(ctx, "ProfileB")
        try {
            Profiles.switchTo(ctx, b.id)
            // Profile B should have its own theme (default after creation — none yet set)
            val bTheme = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
            // The reload() call should NOT crash on a null theme — and ThemeController
            // should hold its default state ("system"/"blue") for profile B.
            Assert.assertTrue("Profile B theme should be null OR a system/dark mode JSON",
                bTheme == null || bTheme.contains("\"mode\":\"system\"") || bTheme.contains("\"mode\":\"dark\"") || bTheme.contains("\"mode\":\"light\""))

            // Switch back to A
            Profiles.switchTo(ctx, Profiles.DEFAULT)
            // Theme should be reloaded from A's prefs — accent = green
            val aTheme = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
            Assert.assertNotNull("Profile A theme should be persisted", aTheme)
            Assert.assertTrue("Profile A theme should contain green accent", aTheme!!.contains("\"accent\":\"green\""))
            Assert.assertEquals("ThemeController should reflect A's accent after switch back", "green", ThemeController.accentId)
        } finally {
            // Cleanup
            if (Profiles.activeId(ctx) != Profiles.DEFAULT) Profiles.switchTo(ctx, Profiles.DEFAULT)
            Profiles.delete(ctx, b.id)
        }
    }

    /** 3. Write + refresh happen in the SAME call (atomic write-through). */
    @Test
    fun writeAndRefresh_happenInSameCall() {
        // Before: default accent
        ThemeController.setAccentAndDynamic("dark", "blue", dynamic = false, ctx = ctx)

        // Single call: should both update in-memory state AND persist to storage
        ThemeController.setAccentAndDynamic("dark", "orange", dynamic = true, ctx = ctx)

        // In-memory updated
        Assert.assertEquals("In-memory accentId should be orange", "orange", ThemeController.accentId)
        Assert.assertTrue("In-memory dynamic should be true", ThemeController.dynamic)

        // Storage updated atomically (both keys visible immediately after the call returns)
        val storedTheme = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        val storedDynamic = ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC)
        Assert.assertNotNull("p4_theme written", storedTheme)
        Assert.assertTrue("p4_theme contains orange accent", storedTheme!!.contains("\"accent\":\"orange\""))
        Assert.assertEquals("p4_dynamic is 'on'", "on", storedDynamic)
    }

    /** 4. Tapping "light" changes the visible app color (dark flag flips) immediately. */
    @Test
    fun tapLight_changesAppImmediately() {
        ThemeController.setAccentAndDynamic("dark", "blue", dynamic = false, ctx = ctx)
        Assert.assertTrue("Precondition: dark=true", ThemeController.dark)

        // Tap "light"
        ThemeController.set("light", "blue", ctx)
        ThemeController.resolveDark(false)

        Assert.assertEquals("mode should be light", "light", ThemeController.mode)
        Assert.assertFalse("dark should be false (visible app color = light)", ThemeController.dark)
    }

    /** 5. Switching profile A → B changes the theme; B has its own theme state. */
    @Test
    fun switchAtoB_changesTheme() {
        // Profile A: dark mode + red accent
        ThemeController.setAccentAndDynamic("dark", "red", dynamic = false, ctx = ctx)
        Assert.assertTrue("Precondition: A is dark", ThemeController.dark)
        Assert.assertEquals("Precondition: A accent is red", "red", ThemeController.accentId)

        val b = Profiles.create(ctx, "ProfileB2")
        try {
            // Profile B has no theme prefs yet → reload should reset to defaults
            Profiles.switchTo(ctx, b.id)
            // After reload: accentId should reset to "blue" (the default), mode to "system"
            Assert.assertEquals("B accent should be default (blue)", "blue", ThemeController.accentId)
            Assert.assertEquals("B mode should be default (system)", "system", ThemeController.mode)
        } finally {
            if (Profiles.activeId(ctx) != Profiles.DEFAULT) Profiles.switchTo(ctx, Profiles.DEFAULT)
            Profiles.delete(ctx, b.id)
        }
    }

    /** 6. Switching back from B → A restores A's theme from per-profile prefs. */
    @Test
    fun switchBtoA_restoresATheme() {
        // Profile A: dark mode + red accent (persisted)
        ThemeController.setAccentAndDynamic("dark", "red", dynamic = false, ctx = ctx)
        val aThemeBeforeSwitch = ProfileSettings.get(ctx, ProfileSettings.K_THEME)

        val b = Profiles.create(ctx, "ProfileB3")
        try {
            Profiles.switchTo(ctx, b.id)
            // B has no theme → defaults
            Assert.assertEquals("B should be default blue", "blue", ThemeController.accentId)

            // Switch back to A
            Profiles.switchTo(ctx, Profiles.DEFAULT)
            // Theme should be restored from A's prefs
            val aThemeAfterSwitch = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
            Assert.assertEquals("A's stored theme should be unchanged", aThemeBeforeSwitch, aThemeAfterSwitch)
            Assert.assertEquals("ThemeController should restore A's accent", "red", ThemeController.accentId)
            Assert.assertEquals("ThemeController should restore A's mode", "dark", ThemeController.mode)
        } finally {
            if (Profiles.activeId(ctx) != Profiles.DEFAULT) Profiles.switchTo(ctx, Profiles.DEFAULT)
            Profiles.delete(ctx, b.id)
        }
    }

    /** 7. resetToDefault clears all custom exercises from the library + prefs. */
    @Test
    fun resetToDefault_clearsCustomExercises() {
        // Seed a custom exercise
        val draft = com.peakform.fitness.core.StudioDraft(
            name = "TestCustom",
            group = "chest",
            muscles = listOf("chest"),
            equipment = "barbell",
            level = "intermediate",
            countedBy = "reps",
            steps = "step 1\nstep 2",
            cues = "",
            imageUrl = "",
        )
        com.peakform.fitness.core.Studio.addCustom(ctx, draft)
        Assert.assertTrue("Precondition: custom exercise exists",
            Library.customExercisesSnapshot().any { it.name == "TestCustom" })

        // Reset
        Library.resetToDefault(ctx)

        // In-memory custom list is empty
        Assert.assertTrue("Custom exercises should be cleared from in-memory list",
            Library.customExercisesSnapshot().isEmpty())

        // Persisted prefs also removed
        val stored = com.peakform.fitness.core.ProPrefs.get(ctx, com.peakform.fitness.core.Studio.CUST_KEY)
        Assert.assertNull("Studio.CUST_KEY should be removed from prefs", stored)

        // After re-inject (which would happen on next boot), still empty
        Library.reinjectCustom(ctx)
        Assert.assertTrue("After reinject, custom exercises should still be empty",
            Library.customExercisesSnapshot().isEmpty())
    }
}
