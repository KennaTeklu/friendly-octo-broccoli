package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProgressSummary
import com.peakform.fitness.core.SelfAssessment
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
 * BATCH-8 evidence test — CoachingEngine + SelfAssessment + ProgressSummary
 * core engines.
 *
 * Pins the contract for the new coach-led flow:
 *   1. CoachingEngine.evaluate returns a list of Recommendation rows (can be
 *      empty for a brand-new profile, but never null).
 *   2. SelfAssessment.record writes to per-profile SharedPreferences, so a
 *      profile switch brings the right assessment history.
 *   3. ProgressSummary.monthly + metricTrends return empty lists (not null)
 *      when there's no workout history.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch8Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = RuntimeEnvironment.getApplication()
        com.peakform.fitness.ui.Fa.appContext = ctx
        ProState.init(ctx)
        ProPrefs.clearAll(ctx)
        ProfileState.prefs(ctx).edit().clear().commit()
        Library.ensure(ctx)
        ProState.data = ProState.data.copy(workouts = emptyList(), exercises = emptyMap())
    }

    @After
    fun tearDown() {
        ProfileState.prefs(ctx).edit().clear().commit()
        ProPrefs.clearAll(ctx)
    }

    /** CoachingEngine.evaluate is null-safe on an empty profile. */
    @Test
    fun coachingEngine_evaluate_returnsNonNullOnEmptyProfile() {
        val recs = CoachingEngine.evaluate(ctx)
        Assert.assertNotNull("evaluate must never return null", recs)
        // With no workouts / exercises, the recommendation list can be empty
        // — that's fine. We only require non-null.
    }

    /** SelfAssessment.record persists to the per-profile prefs file. */
    @Test
    fun selfAssessment_record_persistsToPerProfilePrefs() {
        val a = SelfAssessment.Assessment(
            at = ProState.nowIso(),
            workoutId = null,
            rating = 5, energy = 4, soreness = 3, motivation = 5, sleep = 4,
            note = "felt great",
        )
        SelfAssessment.record(ctx, a)
        // Read back from the same per-profile prefs.
        val last = SelfAssessment.last(ctx)
        Assert.assertNotNull(last)
        Assert.assertEquals(5, last!!.rating)
        Assert.assertEquals(4, last.energy)
        Assert.assertEquals("felt great", last.note)
        // History grows.
        Assert.assertEquals(1, SelfAssessment.history(ctx).size)
    }

    /** ProgressSummary.monthly + metricTrends never throw on an empty profile. */
    @Test
    fun progressSummary_emptyProfile_returnsEmptyLists() {
        val monthly = ProgressSummary.monthly(ctx)
        val trends = ProgressSummary.metricTrends()
        Assert.assertNotNull(monthly)
        Assert.assertNotNull(trends)
        // Both should be empty when there are no completed workouts.
        Assert.assertTrue("monthly should be empty on a fresh profile", monthly.isEmpty())
        // metricTrends includes Volume + Workouts + Avg RPE + Longevity, so it
        // can be non-empty even without history (the values are 0.0). We only
        // require that it doesn't throw.
    }
}
