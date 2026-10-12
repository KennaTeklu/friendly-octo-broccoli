package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.GapClosures
import com.peakform.fitness.core.LibraryExercise
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
 * BATCH-15 evidence test — GapClosures helpers.
 *
 * 1. filterByGender drops womens_only for males and vice-versa.
 * 2. fuzzySearch substring match works.
 * 3. rpeColor returns the right band.
 * 4. competitionCountdown returns null for blank date.
 * 5. postpartumMultiplier scales correctly.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch15Test {

    private lateinit var ctx: Context

    private fun mkEx(id: String, components: List<String> = emptyList()): LibraryExercise =
        LibraryExercise(
            name = id, muscles = emptyList(), primaryMuscle = "", equipment = "",
            skillFactor = 0.7, fitnessComponents = components,
        )

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProState.init(ctx)
        ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
    }

    @After
    fun tearDown() {
        ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
    }

    @Test
    fun filterByGender_unspecifiedKeepsAll() {
        val list = listOf(mkEx("a", listOf("mens_only")), mkEx("b", listOf("womens_only")))
        Assert.assertEquals(2, GapClosures.filterByGender(list, "unspecified").size)
    }

    @Test
    fun filterByGender_dropsOpposite() {
        val list = listOf(mkEx("a", listOf("mens_only")), mkEx("b", listOf("womens_only")))
        Assert.assertEquals(1, GapClosures.filterByGender(list, "male").size)
        Assert.assertEquals("b", GapClosures.filterByGender(list, "female").first().id)
    }

    @Test
    fun fuzzySearch_substringMatch() {
        val list = listOf(
            mkEx("bench_press"),
            mkEx("squat"),
            mkEx("deadlift"),
        )
        val r = GapClosures.fuzzySearch(list, "bench")
        Assert.assertEquals(1, r.size)
        Assert.assertEquals("bench_press", r.first().id)
    }

    @Test
    fun rpeColor_returnsBands() {
        Assert.assertEquals("green", GapClosures.rpeColor(5.0))
        Assert.assertEquals("yellow", GapClosures.rpeColor(7.5))
        Assert.assertEquals("orange", GapClosures.rpeColor(9.0))
        Assert.assertEquals("red", GapClosures.rpeColor(10.0))
    }

    @Test
    fun competitionCountdown_nullForBlank() {
        Assert.assertNull(GapClosures.competitionCountdown(null))
        Assert.assertNull(GapClosures.competitionCountdown(""))
        Assert.assertNull(GapClosures.competitionCountdown("not-a-date"))
    }

    @Test
    fun postpartumMultiplier() {
        Assert.assertEquals(1.0, GapClosures.postpartumMultiplier(null), 0.001)
        Assert.assertEquals(0.7, GapClosures.postpartumMultiplier(3), 0.001)
        Assert.assertEquals(0.85, GapClosures.postpartumMultiplier(9), 0.001)
        Assert.assertEquals(1.0, GapClosures.postpartumMultiplier(15), 0.001)
    }
}
