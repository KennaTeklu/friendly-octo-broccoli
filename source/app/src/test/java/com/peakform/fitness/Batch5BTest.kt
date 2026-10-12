package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
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
 * BATCH-5B evidence test.
 *
 * Verifies:
 *  1. ProfileSettings routes through ProfileState (the per-profile SharedPrefs).
 *  2. ProfileState uses the "p4_ls_<profileId>" SharedPrefs file (not the global "p4_ls").
 *  3. migrateFromPrefs() is idempotent (gated on p4_v22_migrated).
 *  4. The MIGRATED_KEYS list includes the expected per-profile keys.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch5BTest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProPrefs.clearAll(ctx)
        // Clear the per-profile SharedPrefs for the active profile.
        ProfileState.invalidate(Profiles.DEFAULT)
        val sp = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        sp.edit().clear().commit()
        ProState.init(ctx)
        kotlinx.coroutines.runBlocking { if (!ProState.initialized) ProState.loadAll() }
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        val sp = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        sp.edit().clear().commit()
        ProfileState.invalidate(Profiles.DEFAULT)
    }

    @Test
    fun profileSettings_routes_through_per_profile_prefs() {
        // ProfileSettings.put should write to "p4_ls_default" (the active profile).
        ProfileSettings.put(ctx, ProfileSettings.LIB_GROUP, "equipment")

        // The value should be readable via ProfileState (same backing store).
        Assert.assertEquals("equipment", ProfileState.get(ctx, ProfileSettings.LIB_GROUP))

        // And readable via ProfileSettings.
        Assert.assertEquals("equipment", ProfileSettings.get(ctx, ProfileSettings.LIB_GROUP))

        // The value should NOT be in the global ProPrefs ("p4_ls").
        Assert.assertNull("Per-profile value should not leak to global ProPrefs",
            ProPrefs.get(ctx, ProfileSettings.LIB_GROUP))

        // The per-profile SharedPrefs file should exist + contain the value.
        val sp = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        Assert.assertEquals("equipment", sp.getString(ProfileSettings.LIB_GROUP, null))
    }

    @Test
    fun profileSettings_typed_helpers_work() {
        ProfileSettings.putInt(ctx, ProfileSettings.LIB_CHUNK, 30)
        Assert.assertEquals(30, ProfileSettings.getInt(ctx, ProfileSettings.LIB_CHUNK, 20))

        ProfileSettings.putBool(ctx, ProfileSettings.GYM_MODE, true)
        Assert.assertTrue(ProfileSettings.getBool(ctx, ProfileSettings.GYM_MODE, false))

        ProfileSettings.putBool(ctx, ProfileSettings.GYM_MODE, false)
        Assert.assertFalse(ProfileSettings.getBool(ctx, ProfileSettings.GYM_MODE, true))
    }

    @Test
    fun profileSettings_has_remove_and_has() {
        ProfileSettings.put(ctx, ProfileSettings.VOCAB_LEVEL, "5")
        Assert.assertTrue(ProfileSettings.has(ctx, ProfileSettings.VOCAB_LEVEL))
        ProfileSettings.remove(ctx, ProfileSettings.VOCAB_LEVEL)
        Assert.assertFalse(ProfileSettings.has(ctx, ProfileSettings.VOCAB_LEVEL))
    }

    @Test
    fun migrated_keys_includes_expected_entries() {
        // The set documents which legacy global keys crossed over to per-profile in v22.
        Assert.assertTrue("MIGRATED_KEYS should include p4_onboarded",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.ONBOARDED))
        Assert.assertTrue("MIGRATED_KEYS should include p4_health_screened_at",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.HEALTH_SCREENED))
        Assert.assertTrue("MIGRATED_KEYS should include workoutEmergencyBackup",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.EMERGENCY_BACKUP))
        Assert.assertTrue("MIGRATED_KEYS should include p4_lib_group",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.LIB_GROUP))
        Assert.assertTrue("MIGRATED_KEYS should include p4_notif_retest_reminder",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.NOTIF_RETEST))
        Assert.assertTrue("MIGRATED_KEYS should include p4_power_settings",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.POWER_SETTINGS))
        Assert.assertTrue("MIGRATED_KEYS should include p4_vocab",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.VOCAB_LEVEL))
        Assert.assertTrue("MIGRATED_KEYS should include lastStreakRecoverAt",
            ProfileSettings.MIGRATED_KEYS.contains(ProfileSettings.STREAK_RECOVER_AT))
    }

    @Test
    fun migrateFromPrefs_is_idempotent() {
        // Seed the global ProPrefs with legacy onboarding state.
        ProPrefs.put(ctx, ProfileState.K_ONBOARDED, "true")
        ProPrefs.put(ctx, ProfileState.K_V22_MIGRATED, null?.toString() ?: "false")
        ProPrefs.remove(ctx, ProfileState.K_V22_MIGRATED)

        // First run — migrates the value into the per-profile SharedPrefs.
        ProfileState.migrateFromPrefs(ctx)
        Assert.assertEquals("true", ProfileState.get(ctx, ProfileState.K_ONBOARDED))
        Assert.assertEquals("true", ProPrefs.get(ctx, ProfileState.K_V22_MIGRATED))

        // Wipe the per-profile value (simulate it being deleted somehow),
        // then run migrateFromPrefs again — it should NOT re-copy because the
        // global p4_v22_migrated flag is set.
        ProfileState.remove(ctx, ProfileState.K_ONBOARDED)
        ProfileState.migrateFromPrefs(ctx)
        Assert.assertNull("Migration should not re-copy after the gate is set",
            ProfileState.get(ctx, ProfileState.K_ONBOARDED))
    }

    @Test
    fun profileState_uses_per_profile_prefs_name() {
        // Default profile uses "p4_ls_default".
        Assert.assertEquals("p4_ls_default", ProfileState.prefsName(Profiles.DEFAULT))
        // A custom profile uses "p4_ls_p_xxxxxxxx".
        Assert.assertEquals("p4_ls_p_abc12345", ProfileState.prefsName("p_abc12345"))
    }

    @Test
    fun profileState_keys_lists_all_keys() {
        ProfileState.put(ctx, "test_key_a", "1")
        ProfileState.put(ctx, "test_key_b", "2")
        val keys = ProfileState.keys(ctx)
        Assert.assertTrue("keys() should list test_key_a", keys.contains("test_key_a"))
        Assert.assertTrue("keys() should list test_key_b", keys.contains("test_key_b"))
    }
}
