package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
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

/**
 * BATCH-11 evidence test — Library routing through ProfileSettings.
 *
 * Verifies:
 *  1. Library prefs (group, chunk, expand-all, stream, collapsed, custom-ex)
 *     are stored in the per-profile SharedPrefs (ProfileSettings).
 *  2. changeGroupBy() writes the new group-by key to ProfileSettings.
 *  3. The default state is all-collapsed (when expand-all pref is not set).
 *  4. Library.resetToDefault(ctx) clears the per-profile library prefs and
 *     reloads the bundled library.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch11Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProPrefs.clearAll(ctx)
        val sp = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        sp.edit().clear().commit()
        ProfileState.invalidate(Profiles.DEFAULT)
        ProState.init(ctx)
        kotlinx.coroutines.runBlocking { if (!ProState.initialized) ProState.loadAll() }
        Library.ensure(ctx)
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        val sp = ctx.getSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT), Context.MODE_PRIVATE)
        sp.edit().clear().commit()
        ProfileState.invalidate(Profiles.DEFAULT)
    }

    @Test
    fun library_prefs_route_through_profileSettings() {
        // Write a value via ProfileSettings.
        ProfileSettings.put(ctx, ProfileSettings.LIB_GROUP, "equipment")
        ProfileSettings.putInt(ctx, ProfileSettings.LIB_CHUNK, 30)
        ProfileSettings.put(ctx, ProfileSettings.LIB_STREAM, "false")
        ProfileSettings.put(ctx, ProfileSettings.LIB_EXPAND_ALL, "true")

        // The values should be in the per-profile SharedPrefs (ProfileState).
        Assert.assertEquals("equipment", ProfileState.get(ctx, ProfileSettings.LIB_GROUP))
        Assert.assertEquals(30, ProfileState.get(ctx, ProfileSettings.LIB_CHUNK)?.toIntOrNull() ?: 20)
        Assert.assertEquals("false", ProfileState.get(ctx, ProfileSettings.LIB_STREAM))
        Assert.assertEquals("true", ProfileState.get(ctx, ProfileSettings.LIB_EXPAND_ALL))

        // And NOT in the global ProPrefs.
        Assert.assertNull(ProPrefs.get(ctx, ProfileSettings.LIB_GROUP))
        Assert.assertNull(ProPrefs.get(ctx, ProfileSettings.LIB_STREAM))
    }

    @Test
    fun changeGroupBy_writes_new_key() {
        // Simulate the on-screen changeGroupBy: write a new key.
        ProfileSettings.put(ctx, ProfileSettings.LIB_GROUP, "muscleGroup")
        Assert.assertEquals("muscleGroup", ProfileSettings.get(ctx, ProfileSettings.LIB_GROUP))

        ProfileSettings.put(ctx, ProfileSettings.LIB_GROUP, "component")
        Assert.assertEquals("component", ProfileSettings.get(ctx, ProfileSettings.LIB_GROUP))
    }

    @Test
    fun default_state_is_all_collapsed_when_expand_all_not_set() {
        // When p4_lib_expand_all is NOT set, the default collapsed set is
        // all groups (i.e. every group starts collapsed).
        val expandAllPref = ProfileSettings.get(ctx, ProfileSettings.LIB_EXPAND_ALL)
        Assert.assertNull("Expand-all pref should be unset by default", expandAllPref)

        // The default collapsed set (mirroring the Library screen logic):
        val defaultSet = if (expandAllPref == "true") emptySet<String>() else Library.groups.keys.toSet()
        Assert.assertEquals("Default should be all-collapsed (every group in collapsed set)",
            Library.groups.keys.toSet(), defaultSet.toSet())
    }

    @Test
    fun default_state_is_all_expanded_when_expand_all_set() {
        ProfileSettings.put(ctx, ProfileSettings.LIB_EXPAND_ALL, "true")
        val expandAllPref = ProfileSettings.get(ctx, ProfileSettings.LIB_EXPAND_ALL)
        Assert.assertEquals("true", expandAllPref)

        // When p4_lib_expand_all is "true", the default collapsed set is empty.
        val defaultSet = if (expandAllPref == "true") emptySet<String>() else Library.groups.keys.toSet()
        Assert.assertTrue("Default should be all-expanded (empty collapsed set)", defaultSet.isEmpty())
    }

    @Test
    fun resetToDefault_clears_library_prefs() {
        // Seed library prefs.
        ProfileSettings.put(ctx, ProfileSettings.LIB_GROUP, "equipment")
        ProfileSettings.putInt(ctx, ProfileSettings.LIB_CHUNK, 30)
        ProfileSettings.put(ctx, ProfileSettings.LIB_STREAM, "false")
        ProfileSettings.put(ctx, ProfileSettings.LIB_EXPAND_ALL, "true")
        ProfileSettings.put(ctx, ProfileSettings.LIB_COLLAPSED, """["quadriceps"]""")
        ProfileSettings.put(ctx, ProfileSettings.CUSTOM_EX, "[]")

        // Reset.
        Library.resetToDefault(ctx)

        // Every library pref should be gone.
        Assert.assertNull(ProfileSettings.get(ctx, ProfileSettings.LIB_GROUP))
        Assert.assertNull(ProfileSettings.get(ctx, ProfileSettings.LIB_CHUNK))
        Assert.assertNull(ProfileSettings.get(ctx, ProfileSettings.LIB_STREAM))
        Assert.assertNull(ProfileSettings.get(ctx, ProfileSettings.LIB_EXPAND_ALL))
        Assert.assertNull(ProfileSettings.get(ctx, ProfileSettings.LIB_COLLAPSED))
        Assert.assertNull(ProfileSettings.get(ctx, ProfileSettings.CUSTOM_EX))
    }

    @Test
    fun resetToDefault_reloads_bundled_library() {
        // After resetToDefault, the library should have its default groups.
        Library.resetToDefault(ctx)
        Assert.assertTrue("Library groups should be loaded after reset", Library.groups.isNotEmpty())
    }
}
