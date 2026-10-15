package com.peakform.fitness

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.peakform.fitness.ui.ACCENTS
import com.peakform.fitness.ui.ProColors
import com.peakform.fitness.ui.buildProColors
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-38b — Tint system port.
 *
 * Tests the four CSS-derived tint tokens in ProColors:
 *   tint       = lerp(white_or_surface, accent, 0.09f)  — app.html L3753
 *   tintStrong = lerp(white_or_surface, accent, 0.17f)  — app.html L3754
 *   tintBg     = lerp(bg, accent, 0.06f)                — app.html L3755
 *   tintLine   = lerp(white_or_surface, accent, 0.24f)  — app.html L3756
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch38bTest {

    @Test
    fun proColors_hasFourTintTokens() {
        val c = buildProColors(true, ACCENTS[0])
        // Verify all four tint fields exist and are non-null (Color is non-nullable)
        Assert.assertTrue("tint should be a valid color", c.tint.red >= 0f)
        Assert.assertTrue("tintStrong should be a valid color", c.tintStrong.red >= 0f)
        Assert.assertTrue("tintBg should be a valid color", c.tintBg.red >= 0f)
        Assert.assertTrue("tintLine should be a valid color", c.tintLine.red >= 0f)
    }

    @Test
    fun tints_deriveFromAccent_dark() {
        val accent = ACCENTS[0]  // Kinetic Blue
        val c = buildProColors(true, accent)
        val surface = Color(0xFF10141C)
        val bg = Color(0xFF07090D)
        val a = accent.darkHex

        Assert.assertEquals(lerp(surface, a, 0.09f), c.tint)
        Assert.assertEquals(lerp(surface, a, 0.17f), c.tintStrong)
        Assert.assertEquals(lerp(bg, a, 0.06f), c.tintBg)
        Assert.assertEquals(lerp(surface, a, 0.24f), c.tintLine)
    }

    @Test
    fun tints_deriveFromAccent_light() {
        val accent = ACCENTS[0]  // Kinetic Blue
        val c = buildProColors(false, accent)
        val white = Color.White
        val bg = Color(0xFFF4F6F9)
        val a = accent.lightHex

        Assert.assertEquals(lerp(white, a, 0.09f), c.tint)
        Assert.assertEquals(lerp(white, a, 0.17f), c.tintStrong)
        Assert.assertEquals(lerp(bg, a, 0.06f), c.tintBg)
        Assert.assertEquals(lerp(white, a, 0.24f), c.tintLine)
    }

    @Test
    fun tints_computeCorrectlyForBlueAccent_dark() {
        val c = buildProColors(true, ACCENTS[0])  // Kinetic Blue (#4E9BFF dark)
        // tint = lerp(#10141C, #4E9BFF, 0.09) — should be a dark color with slight blue
        Assert.assertTrue("tint should be darker than accent", c.tint.red < c.accent.red)
        Assert.assertTrue("tint should be lighter than surface in blue channel",
            c.tint.blue > Color(0xFF10141C).blue)
        // tintStrong should be more tinted than tint
        Assert.assertTrue("tintStrong blue > tint blue", c.tintStrong.blue >= c.tint.blue)
        // tintLine should be more tinted than tintStrong
        Assert.assertTrue("tintLine blue > tintStrong blue", c.tintLine.blue >= c.tintStrong.blue)
    }

    @Test
    fun tints_differentForDifferentAccents() {
        val blue = buildProColors(true, ACCENTS[0])   // Kinetic Blue
        val green = buildProColors(true, ACCENTS[1])  // Court Green
        // Blue and green accents should produce different tints
        Assert.assertNotEquals("tint should differ for blue vs green", blue.tint, green.tint)
        Assert.assertNotEquals("tintStrong should differ", blue.tintStrong, green.tintStrong)
        Assert.assertNotEquals("tintBg should differ", blue.tintBg, green.tintBg)
        Assert.assertNotEquals("tintLine should differ", blue.tintLine, green.tintLine)
    }

    @Test
    fun tintBg_isBetweenBgAndAccent() {
        val c = buildProColors(true, ACCENTS[0])
        // tintBg = lerp(bg, accent, 0.06) — should be very close to bg
        val bg = Color(0xFF07090D)
        // The distance from bg to tintBg should be 6% of the distance from bg to accent
        val distBgToTint = colorDistance(bg, c.tintBg)
        val distBgToAccent = colorDistance(bg, c.accent)
        Assert.assertTrue(
            "tintBg should be between bg and accent (dist $distBgToTint should be < dist $distBgToAccent)",
            distBgToTint < distBgToAccent,
        )
    }

    @Test
    fun tintLine_isMoreTintedThanTint() {
        val c = buildProColors(true, ACCENTS[0])
        val surface = Color(0xFF10141C)
        // tintLine = lerp(surface, accent, 0.24) — more accent than tint = lerp(surface, accent, 0.09)
        val distTintToSurface = colorDistance(c.tint, surface)
        val distTintLineToSurface = colorDistance(c.tintLine, surface)
        Assert.assertTrue(
            "tintLine should be further from surface than tint",
            distTintLineToSurface > distTintToSurface,
        )
    }

    private fun colorDistance(a: Color, b: Color): Float {
        val dr = a.red - b.red
        val dg = a.green - b.green
        val db = a.blue - b.blue
        return kotlin.math.sqrt(dr * dr + dg * dg + db * db)
    }
}
