package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.OneRmLab
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
 * BATCH-14 evidence test — 1RM Lab estimate formulas + audit report shape.
 *
 * 1. estimate() with each formula returns a value ≥ the input weight.
 * 2. auditAll() returns a LabReport with the right shape (rows may be empty
 *    on a fresh test profile, that's fine).
 * 3. estimate() with bad inputs returns 0.0.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch14Test {

    private lateinit var ctx: Context

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
    fun estimate_epley() {
        val e = OneRmLab.estimate(200.0, 5, OneRmLab.F_EPLEY)
        Assert.assertTrue("Epley 200x5 > 200", e > 200.0)
    }

    @Test
    fun estimate_brzycki() {
        val e = OneRmLab.estimate(200.0, 5, OneRmLab.F_BRZYCKI)
        Assert.assertTrue("Brzycki 200x5 > 200", e > 200.0)
    }

    @Test
    fun estimate_lombardi() {
        val e = OneRmLab.estimate(200.0, 5, OneRmLab.F_LOMBARDI)
        Assert.assertTrue("Lombardi 200x5 ≥ 200", e >= 200.0)
    }

    @Test
    fun estimate_badInputsReturnZero() {
        Assert.assertEquals(0.0, OneRmLab.estimate(0.0, 5, OneRmLab.F_EPLEY), 0.001)
        Assert.assertEquals(0.0, OneRmLab.estimate(200.0, 0, OneRmLab.F_EPLEY), 0.001)
        Assert.assertEquals(0.0, OneRmLab.estimate(200.0, 20, OneRmLab.F_EPLEY), 0.001)
    }

    @Test
    fun auditAll_returnsShape() {
        val r = OneRmLab.auditAll(ctx)
        Assert.assertNotNull(r)
        Assert.assertTrue("retestDueCount ≥ 0", r.retestDueCount >= 0)
        Assert.assertTrue("suspiciousCount ≥ 0", r.suspiciousCount >= 0)
    }
}
