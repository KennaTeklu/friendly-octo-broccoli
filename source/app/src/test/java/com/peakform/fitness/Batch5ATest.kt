package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProDb
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProState
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-5A evidence tests — the production bug fix that moved per-profile
 * settings from Room `meta` to per-profile SharedPreferences.
 *
 * Background: in production (NOT in unit tests with `allowMainThreadQueries`),
 * Room blocks synchronous main-thread queries by throwing
 * `IllegalStateException: Cannot access database on the main thread`. The
 * legacy `ProfileState.put` had a try/catch around `dao.putMetaSync(...)` that
 * swallowed the exception, so every per-profile write from a UI callback
 * silently dropped. The user could change their accent, see the UI update,
 * and then on next launch find their old accent restored.
 *
 * BATCH-5A fix: ProfileState now uses per-profile SharedPreferences
 * ("p4_ls_<profileId>") instead of Room meta. SharedPreferences is safe on the
 * main thread, so per-profile writes from UI callbacks persist correctly.
 *
 * These tests prove the fix works end-to-end:
 *  1. A write from the main thread is immediately readable from the main thread
 *     — no `allowMainThreadQueries()` needed because we never touch Room.
 *  2. Per-profile SharedPreferences files are truly isolated: writing to
 *     profile A's prefs does not leak into profile B's prefs.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch5ATest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        // Start from a clean slate — both the global ProPrefs file and every
        // per-profile file. ProDb is also closed so a prior test's handle does
        // not interfere.
        ProDb.close("pro.db")
        ctx.deleteDatabase("pro.db")
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
        ProState.init(ctx)
        // Clear the default profile's per-profile SharedPreferences so any value
        // left over from a prior test does not leak in.
        ProfileState.prefs(ctx).edit().clear().commit()
    }

    @After
    fun tearDown() {
        ProState.flush()
        ProDb.close("pro.db")
    }

    /**
     * BATCH-5A regression: writes a theme key via ProfileSettings from the test
     * (main) thread, then reads it back from the same thread and asserts the
     * value matches. No `allowMainThreadQueries()` is enabled in ProStore —
     * the test passes because ProfileSettings routes through ProfileState, which
     * uses SharedPreferences (not Room) for both reads and writes.
     *
     * Before the fix: ProfileState.put used `dao.putMetaSync(...)`, which
     * threw `IllegalStateException` on the main thread (the try/catch swallowed
     * it). The value was never stored, so the read-back returned null.
     */
    @Test
    fun writeThemeFromMainThread_readsBackCorrectly() {
        // The exact JSON shape written by ThemeController.currentJson().
        val themeJson = """{"mode":"dark","accent":"green"}"""

        // Write from the test (main) thread — Room would throw here.
        ProfileSettings.put(ctx, ProfileSettings.K_THEME, themeJson)

        // Read back from the same thread — should return the exact value.
        val readBack = ProfileSettings.get(ctx, ProfileSettings.K_THEME)

        Assert.assertNotNull(
            "Theme key should be readable from the main thread after a write — " +
                "if this is null, the per-profile write silently dropped (the Room " +
                "main-thread block was being swallowed by a try/catch).",
            readBack,
        )
        Assert.assertEquals(
            "Theme JSON should round-trip exactly through ProfileSettings",
            themeJson,
            readBack,
        )
    }

    /**
     * BATCH-5A per-profile isolation: writing accent "green" to profile A's
     * per-profile SharedPreferences does not leak into profile B's. Each
     * profile's file ("p4_ls_<profileId>") is independent, so the active
     * profile's reads see only its own values.
     *
     * We use `ProfileState.prefsFor(ctx, profileId)` to write directly into
     * each profile's SharedPreferences file (bypassing the active-profile
     * resolution in ProfileSettings.put). This is the same call ProfileSettings
     * would make if the user switched to that profile and then wrote the key.
     */
    @Test
    fun twoProfiles_differentAccents() {
        // Create two distinct profiles. Profiles.create seeds the new
        // profile's Room `user` table — that uses suspend DAO methods, which
        // dispatch off the main thread via Room's executor, so it does not
        // trigger the main-thread block.
        val profileA = Profiles.create(ctx, "ProfileA")
        val profileB = Profiles.create(ctx, "ProfileB")

        try {
            val themeA = """{"mode":"dark","accent":"green"}"""
            val themeB = """{"mode":"dark","accent":"red"}"""

            // Write accent "green" to profile A's per-profile SharedPreferences.
            // ProfileState.prefsFor(ctx, profileId) returns the SharedPreferences
            // file "p4_ls_<profileId>" — independent per profile.
            ProfileState.prefsFor(ctx, profileA.id).edit()
                .putString(ProfileSettings.K_THEME, themeA)
                .commit()

            // Write accent "red" to profile B's per-profile SharedPreferences.
            ProfileState.prefsFor(ctx, profileB.id).edit()
                .putString(ProfileSettings.K_THEME, themeB)
                .commit()

            // Read each profile's theme back from its OWN SharedPreferences file.
            val readA = ProfileState.prefsFor(ctx, profileA.id)
                .getString(ProfileSettings.K_THEME, null)
            val readB = ProfileState.prefsFor(ctx, profileB.id)
                .getString(ProfileSettings.K_THEME, null)

            Assert.assertNotNull("Profile A should have a theme key", readA)
            Assert.assertNotNull("Profile B should have a theme key", readB)
            Assert.assertNotEquals(
                "Profile A and Profile B should have different accents — " +
                    "if these are equal, the per-profile SharedPreferences files " +
                    "are leaking into each other (wrong filename / wrong active id).",
                readA,
                readB,
            )
            Assert.assertTrue(
                "Profile A should hold the green accent",
                readA!!.contains("\"accent\":\"green\""),
            )
            Assert.assertTrue(
                "Profile B should hold the red accent",
                readB!!.contains("\"accent\":\"red\""),
            )
        } finally {
            // Cleanup: delete the two test profiles (also unlinks their DB files).
            Profiles.delete(ctx, profileA.id)
            Profiles.delete(ctx, profileB.id)
        }
    }
}
