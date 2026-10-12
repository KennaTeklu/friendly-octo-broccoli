package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
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
 * BATCH-5B evidence test — per-profile theme isolation + write-through +
 * reload-on-profile-switch contract.
 *
 * Covers:
 *   1. ThemeController writes update in-memory state AND storage in the same
 *      call (setAccentAndDynamic writes accent + p4_dynamic atomically).
 *   2. Two profiles keep independent themes — switching profile reloads the
 *      theme from the new profile's SharedPreferences.
 *   3. ProfileSettings.migrateFromPrefs copies legacy global `p4_ls` keys
 *      into every profile's `p4_ls_<id>` (one-shot, idempotent).
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
        // Clear every per-profile prefs file we might create.
        ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
        // Reset theme state.
        ThemeController.load("""{"mode":"dark","accent":"blue"}""")
        ThemeController.loadDynamic(null)
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit().clear().commit()
    }

    /**
     * Write-through contract: ThemeController.setAccentAndDynamic updates the
     * in-memory state (dark / accentId / dynamic) AND the per-profile
     * SharedPreferences in the SAME call. Both sides are observable.
     */
    @Test
    fun setAccentAndDynamic_writeThrough_updatesMemoryAndStorage() {
        // Precondition: state is default (blue, dark=false/dynamic=off).
        Assert.assertEquals("blue", ThemeController.accentId)
        Assert.assertFalse(ThemeController.dynamic)

        // Action: set "red" + dynamic=true (write-through).
        ThemeController.setAccentAndDynamic("dark", "red", dynamic = true, ctx = ctx)

        // In-memory updates:
        Assert.assertEquals("red", ThemeController.accentId)
        Assert.assertTrue(ThemeController.dynamic)

        // Storage updates (per-profile):
        val p4Theme = ProfileSettings.get(ctx, ProfileSettings.K_THEME)
        val p4Dynamic = ProfileSettings.get(ctx, ProfileSettings.K_DYNAMIC)
        Assert.assertNotNull("p4_theme must be written", p4Theme)
        Assert.assertTrue("p4_theme must contain accent 'red'", p4Theme!!.contains("\"accent\":\"red\""))
        Assert.assertEquals("on", p4Dynamic)
    }

    /**
     * Per-profile isolation: switching the active profile reloads the theme
     * from the new profile's SharedPreferences, so a theme change in profile A
     * does not bleed into profile B.
     */
    @Test
    fun switchProfile_reloadsThemeFromNewProfilePrefs() {
        // Profile A (default): theme = dark/red.
        ProfileState.prefsFor(ctx, Profiles.DEFAULT).edit()
            .putString(ProfileSettings.K_THEME, """{"mode":"dark","accent":"red"}""")
            .putString(ProfileSettings.K_DYNAMIC, "on")
            .commit()

        // Profile B: create it, set theme to light/blue.
        val b = Profiles.create(ctx, "Sam", "🦊")
        ProfileState.prefsFor(ctx, b.id).edit()
            .putString(ProfileSettings.K_THEME, """{"mode":"light","accent":"blue"}""")
            .putString(ProfileSettings.K_DYNAMIC, "off")
            .commit()

        // Cold-start on the default profile.
        ThemeController.reload(ctx)
        Assert.assertEquals("red", ThemeController.accentId)
        Assert.assertTrue(ThemeController.dynamic)

        // Switch to profile B — reload() should pick up B's theme.
        Profiles.switchTo(ctx, b.id)
        Assert.assertEquals("blue", ThemeController.accentId)
        Assert.assertFalse(ThemeController.dynamic)
    }

    /**
     * v22 migration: copying legacy global `p4_ls` keys into every existing
     * profile's `p4_ls_<id>` (one-shot, idempotent).
     */
    @Test
    fun migrateFromPrefs_copiesGlobalKeysToAllProfiles() {
        // Seed legacy global values.
        ProPrefs.put(ctx, ProfileSettings.K_THEME, """{"mode":"dark","accent":"green"}""")
        ProPrefs.put(ctx, ProfileSettings.K_GYM, "on")
        ProPrefs.put(ctx, ProfileSettings.K_VOCAB, "7")
        // Make sure the v22 marker is unset.
        ProPrefs.remove(ctx, ProfileSettings.K_V22_MIGRATED)

        // The default profile should have no p4_theme yet.
        Assert.assertNull(ProfileState.prefsFor(ctx, Profiles.DEFAULT).getString(ProfileSettings.K_THEME, null))

        // Run migration.
        ProfileSettings.migrateFromPrefs(ctx)

        // Default profile should now have the migrated values.
        Assert.assertEquals("""{"mode":"dark","accent":"green"}""",
            ProfileState.prefsFor(ctx, Profiles.DEFAULT).getString(ProfileSettings.K_THEME, null))
        Assert.assertEquals("on", ProfileState.prefsFor(ctx, Profiles.DEFAULT).getString(ProfileSettings.K_GYM, null))
        Assert.assertEquals("7", ProfileState.prefsFor(ctx, Profiles.DEFAULT).getString(ProfileSettings.K_VOCAB, null))

        // Marker set so re-running migration is a no-op.
        Assert.assertEquals("true", ProPrefs.get(ctx, ProfileSettings.K_V22_MIGRATED))

        // Mutate global — re-running migration must NOT overwrite.
        ProPrefs.put(ctx, ProfileSettings.K_GYM, "off")
        ProfileSettings.migrateFromPrefs(ctx)
        Assert.assertEquals("on", ProfileState.prefsFor(ctx, Profiles.DEFAULT).getString(ProfileSettings.K_GYM, null))
    }

    /**
     * No allowMainThreadQueries() in ProStore — this is a static contract
     * check: the ProStore.kt source file must not reference the method.
     */
    @Test
    fun proStore_doesNotCallAllowMainThreadQueries() {
        // Robolectric sets user.dir to the :app module directory, so resolve
        // the source file relative to that — fall back to walking up to the
        // repo root if needed (e.g. when run from IDE).
        val here = java.io.File(System.getProperty("user.dir"))
        val candidates = listOf(
            java.io.File(here, "src/main/java/com/peakform/fitness/core/ProStore.kt"),
            java.io.File(here, "app/src/main/java/com/peakform/fitness/core/ProStore.kt"),
        )
        val src = candidates.firstOrNull { it.isFile }?.readText()
            ?: error("ProStore.kt not found under ${here.absolutePath} or its app/ subdirectory")
        Assert.assertFalse(
            "ProStore.kt must not call allowMainThreadQueries()",
            src.contains("allowMainThreadQueries"),
        )
    }
}
