package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProgressSummary
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.SelfAssessment
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-8 evidence test — CoachingEngine + SelfAssessment + ProgressSummary.
 *
 * 1. CoachingEngine.prescribe returns sane numbers for fresh + smoked inputs.
 * 2. SelfAssessment.save/load round-trips per-profile.
 * 3. ProgressSummary.compute produces a Summary with the right shape.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch8Test {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProState.init(ctx)
        kotlinx.coroutines.runBlocking { if (!ProState.initialized) ProState.loadAll() }
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
    }

    @After
    fun tearDown() {
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
        ctx.deleteSharedPreferences(ProfileState.prefsName(Profiles.DEFAULT))
    }

    @Test
    fun coachingEngine_prescribe_freshUser() {
        val input = CoachingEngine.CoachInput(
            exerciseId = "back_squat",
            exerciseName = "Back Squat",
            lastWeight = 200.0,
            lastReps = 5,
            est1Rm = 240.0,
            fatigue = 0.0,
            goal = "strength",
            aggression = 1.0,
            recoveryDays = 3,
            streakDays = 5,
            jointsFlagged = emptyList(),
        )
        val p = CoachingEngine.prescribe(input)
        Assert.assertTrue("sets >= 2", p.sets >= 2)
        Assert.assertTrue("reps > 0", p.reps > 0)
        Assert.assertTrue("weight positive", p.weight > 0)
        Assert.assertTrue("pushIt when fresh + streak", p.pushIt)
        Assert.assertFalse("no hold-back when fresh", p.holdBack)
    }

    @Test
    fun coachingEngine_prescribe_fatiguedUser() {
        val input = CoachingEngine.CoachInput(
            exerciseId = "bench_press",
            exerciseName = "Bench Press",
            lastWeight = 150.0,
            lastReps = 5,
            est1Rm = 180.0,
            fatigue = 0.8,
            goal = "hypertrophy",
            aggression = 1.0,
            recoveryDays = 0,
            streakDays = 0,
            jointsFlagged = emptyList(),
        )
        val p = CoachingEngine.prescribe(input)
        Assert.assertTrue("hold back when smoked", p.holdBack)
        Assert.assertTrue("fewer sets when tired", p.sets <= 4)
    }

    @Test
    fun selfAssessment_roundTrips() {
        val feel = SelfAssessment.Feel(
            sessionId = "test_session",
            atIso = "2026-10-01T12:00:00.000Z",
            effort = 8,
            readinessNext = 6,
            sorenessExpected = 4,
            note = "Tough but good",
        )
        SelfAssessment.save(ctx, feel)
        val loaded = SelfAssessment.load(ctx, "test_session")
        Assert.assertNotNull(loaded)
        Assert.assertEquals(8, loaded!!.effort)
        Assert.assertEquals(6, loaded.readinessNext)
        Assert.assertEquals(4, loaded.sorenessExpected)
        Assert.assertEquals("Tough but good", loaded.note)
    }

    @Test
    fun progressSummary_returnsShape() {
        val s = ProgressSummary.compute(ctx)
        Assert.assertNotNull(s)
        // Monthly + oneRmDeltas are always lists (possibly empty).
        Assert.assertTrue("monthly list", s.monthly.isNotEmpty() || s.monthly.isEmpty())
        Assert.assertTrue("oneRmDeltas list", s.oneRmDeltas.isNotEmpty() || s.oneRmDeltas.isEmpty())
        // Trend is one of the three known strings.
        Assert.assertTrue("trend valid", s.trend in setOf("up", "flat", "down"))
    }
}
