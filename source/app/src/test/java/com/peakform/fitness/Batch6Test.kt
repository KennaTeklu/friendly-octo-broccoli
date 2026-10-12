package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.ui.ACCENTS
import com.peakform.fitness.ui.ProColors
import com.peakform.fitness.ui.buildProColors
import com.peakform.fitness.ui.ACCENTS
import androidx.compose.ui.graphics.Color
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-6 evidence test — ProColors extended with tint / tintStrong / tintBg /
 * tintLine / inkSoft / ring / cardGradient / focusRing / mix().
 *
 * Verifies the new derived properties exist, are observable, and mix() lands
 * the accent on the right base (dark surface #10141C, light white).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch6Test {
    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun proColors_has_new_tint_fields() {
        val pro = buildProColors(true, ACCENTS[0])  // dark, blue
        // tint — accent at 12% over surface (must NOT equal accent or surface)
        Assert.assertNotEquals("tint should differ from accent", pro.accent, pro.tint)
        Assert.assertNotEquals("tint should differ from surface", pro.surface, pro.tint)
        Assert.assertTrue("tint should be non-transparent", pro.tint.alpha > 0f)

        // tintStrong — accent at 22% over surface (should be more saturated than tint)
        Assert.assertNotEquals("tintStrong should differ from tint", pro.tint, pro.tintStrong)

        // tintBg — accent at 6% over canvas (lighter than tint)
        Assert.assertNotEquals("tintBg should differ from tint", pro.tint, pro.tintBg)

        // tintLine — accent at 32% alpha (just accent.copy(alpha=0.32))
        Assert.assertEquals(0.32f, pro.tintLine.alpha, 0.01f)

        // inkSoft — text at 60% alpha
        Assert.assertEquals(0.60f, pro.inkSoft.alpha, 0.01f)

        // ring — accent at 48% alpha
        Assert.assertEquals(0.48f, pro.ring.alpha, 0.01f)

        // focusRing aliases ring
        Assert.assertEquals(pro.ring, pro.focusRing)

        // cardGradient — vertical accent→surface wash
        Assert.assertNotNull("cardGradient should not be null", pro.cardGradient)
    }

    @Test
    fun mix_blends_accent_and_base_at_pct() {
        val accent = Color(0xFF4E9BFF)  // Kinetic Blue
        val base = Color(0xFF10141C)   // dark surface
        // pct=0 → accent
        val pureAccent = ProColors.mix(accent, base, 0.0f)
        Assert.assertEquals(accent.red, pureAccent.red, 0.001f)
        Assert.assertEquals(accent.green, pureAccent.green, 0.001f)
        Assert.assertEquals(accent.blue, pureAccent.blue, 0.001f)
        // pct=1 → base
        val pureBase = ProColors.mix(accent, base, 1.0f)
        Assert.assertEquals(base.red, pureBase.red, 0.001f)
        Assert.assertEquals(base.green, pureBase.green, 0.001f)
        Assert.assertEquals(base.blue, pureBase.blue, 0.001f)
        // pct=0.5 → halfway
        val half = ProColors.mix(accent, base, 0.5f)
        Assert.assertEquals((accent.red + base.red) / 2f, half.red, 0.01f)
    }

    @Test
    fun dark_mix_uses_surface_base() {
        val pro = buildProColors(true, ACCENTS[0])
        // In dark mode, tint should be a mix of accent and surface (#10141C),
        // not a mix with white.
        val expectedBase = Color(0xFF10141C)
        val manualMix = ProColors.mix(pro.accent, expectedBase, 0.12f)
        Assert.assertEquals(manualMix.red, pro.tint.red, 0.01f)
        Assert.assertEquals(manualMix.green, pro.tint.green, 0.01f)
        Assert.assertEquals(manualMix.blue, pro.tint.blue, 0.01f)
    }

    @Test
    fun light_mix_uses_white_base() {
        val pro = buildProColors(false, ACCENTS[0])
        // In light mode, tint should be a mix of accent and white.
        val expectedBase = Color.White
        val manualMix = ProColors.mix(pro.accent, expectedBase, 0.12f)
        Assert.assertEquals(manualMix.red, pro.tint.red, 0.01f)
        Assert.assertEquals(manualMix.green, pro.tint.green, 0.01f)
        Assert.assertEquals(manualMix.blue, pro.tint.blue, 0.01f)
    }
}
