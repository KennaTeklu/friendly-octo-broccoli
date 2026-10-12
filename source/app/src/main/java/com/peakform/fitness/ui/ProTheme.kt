package com.peakform.fitness.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.sp
import com.peakform.fitness.R

/**
 * ProTheme — 1:1 port of the P4 "Kinetic" design system (work/native-docs/design-system.md).
 * Final effective values: dark canvas #07090D, surface #10141C, hairline borders,
 * accent per user selection (16 accents), Inter UI / Space Grotesk display / JetBrains Mono timers.
 */

/** Font families resolve via FontVault (assets/fonts by path — no font resource IDs in the render path). */
val Inter: FontFamily get() = FontVault.body
val SpaceGrotesk: FontFamily get() = FontVault.display
val JetBrainsMono: FontFamily get() = FontVault.mono

@Immutable
data class AccentDef(val id: String, val name: String, val darkHex: Color, val lightHex: Color, val onDark: Color, val onLight: Color)

/** 16 accents from the legacy ACCENTS table (design-system.md §1c). */
val ACCENTS: List<AccentDef> = listOf(
    AccentDef("blue", "Kinetic Blue", Color(0xFF4E9BFF), Color(0xFF2563EB), Color(0xFF071018), Color.White),
    AccentDef("green", "Court Green", Color(0xFF34D399), Color(0xFF059669), Color(0xFF04120C), Color.White),
    AccentDef("purple", "Deep Purple", Color(0xFFA78BFA), Color(0xFF7C3AED), Color(0xFF0E0718), Color.White),
    AccentDef("orange", "Sunset Orange", Color(0xFFFB923C), Color(0xFFEA580C), Color(0xFF180A02), Color.White),
    AccentDef("red", "Crimson Red", Color(0xFFF87171), Color(0xFFDC2626), Color(0xFF180404), Color.White),
    AccentDef("pink", "Rose Pink", Color(0xFFF472B6), Color(0xFFDB2777), Color(0xFF180410), Color.White),
    AccentDef("yellow", "Sunny Yellow", Color(0xFFFACC15), Color(0xFFCA8A04), Color(0xFF161202), Color.White),
    AccentDef("teal", "Lagoon Teal", Color(0xFF2DD4BF), Color(0xFF0D9488), Color(0xFF031412), Color.White),
    AccentDef("cyan", "Ice Cyan", Color(0xFF22D3EE), Color(0xFF0891B2), Color(0xFF031418), Color.White),
    AccentDef("indigo", "Midnight Indigo", Color(0xFF818CF8), Color(0xFF4F46E5), Color(0xFF050616), Color.White),
    AccentDef("lime", "Volt Lime", Color(0xFFA3E635), Color(0xFF65A30D), Color(0xFF101502), Color.White),
    AccentDef("rose", "Coral Rose", Color(0xFFFB7185), Color(0xFFE11D48), Color(0xFF180407), Color.White),
    AccentDef("sky", "Sky Blue", Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF041018), Color.White),
    AccentDef("mint", "Fresh Mint", Color(0xFF6EE7B7), Color(0xFF10B981), Color(0xFF04140C), Color.White),
    AccentDef("violet", "Soft Violet", Color(0xFFC4B5FD), Color(0xFF8B5CF6), Color(0xFF0F0818), Color.White),
    AccentDef("gold", "Champion Gold", Color(0xFFFBBF24), Color(0xFFB45309), Color(0xFF161002), Color.White),
)

@Immutable
data class ProColors(
    val dark: Boolean,
    val bg: Color,
    val bg2: Color,
    val surface: Color,
    val surface2: Color,
    val surface3: Color,
    val hairline: Color,
    val hairline2: Color,
    val text: Color,
    val text2: Color,
    val text3: Color,
    val glass: Color,
    val glass2: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentLine: Color,
    val onAccent: Color,
    val ok: Color,
    val warn: Color,
    val bad: Color,
    val accentId: String,
) {
    val accentGradient: Brush
        get() = Brush.linearGradient(listOf(accent, lighten(accent, 0.28f)))

    val primaryButtonShadow: Brush get() = accentGradient

    // ---- BATCH-5B: M3-aligned semantic tokens (tint family) ----
    // tint / tintStrong / tintBg / tintLine / inkSoft / ring map onto the
    // Material 3 ColorScheme roles so component code that wants a "tint" or
    // "focus ring" doesn't have to invent alpha each call site.

    /** Mid-opacity accent tint (chips, selected text backgrounds). */
    val tint: Color get() = accent.copy(alpha = if (dark) 0.20f else 0.16f)
    /** Strong accent tint (active hover backgrounds, pressed states). */
    val tintStrong: Color get() = accent.copy(alpha = if (dark) 0.34f else 0.26f)
    /** Subtle accent background (selected card fills, nav highlights). */
    val tintBg: Color get() = accent.copy(alpha = if (dark) 0.10f else 0.08f)
    /** Accent border / hairline on tint backgrounds. */
    val tintLine: Color get() = accent.copy(alpha = if (dark) 0.40f else 0.32f)
    /** Softer body-ink — between text2 and text3 (for de-emphasized labels). */
    val inkSoft: Color get() = text2.copy(alpha = 0.78f)
    /** Standard focus-ring color (4-px border for keyboard focus). */
    val ring: Color get() = accent.copy(alpha = 0.65f)

    /** Card gradient — subtle vertical blend surface → surface2 used by
     *  GlassCard and the focus-card variants. Returns a 2-stop Brush. */
    val cardGradient: Brush
        get() = Brush.verticalGradient(listOf(surface, surface2))

    /** Modifier-friendly border color for a 2-px focus ring (BATCH-5B). */
    val focusRing: Color get() = ring

    companion object {
        fun lighten(c: Color, f: Float): Color = Color(
            red = c.red + (1f - c.red) * f,
            green = c.green + (1f - c.green) * f,
            blue = c.blue + (1f - c.blue) * f,
            alpha = c.alpha,
        )

        fun darken(c: Color, f: Float): Color = Color(
            red = c.red * (1f - f), green = c.green * (1f - f), blue = c.blue * (1f - f), alpha = c.alpha,
        )

        /**
         * BATCH-5B mix(): blend two ProColors by a 0..1 factor. 0 returns a,
         * 1 returns b. Used by the 3-mode theme switch (system → dark/light)
         * so the transition between schemes is smooth instead of a hard cut.
         */
        fun mix(a: ProColors, b: ProColors, t: Float): ProColors {
            val f = t.coerceIn(0f, 1f)
            if (f == 0f) return a
            if (f == 1f) return b
            return ProColors(
                dark = if (f < 0.5f) a.dark else b.dark,
                bg = mixColor(a.bg, b.bg, f),
                bg2 = mixColor(a.bg2, b.bg2, f),
                surface = mixColor(a.surface, b.surface, f),
                surface2 = mixColor(a.surface2, b.surface2, f),
                surface3 = mixColor(a.surface3, b.surface3, f),
                hairline = mixColor(a.hairline, b.hairline, f),
                hairline2 = mixColor(a.hairline2, b.hairline2, f),
                text = mixColor(a.text, b.text, f),
                text2 = mixColor(a.text2, b.text2, f),
                text3 = mixColor(a.text3, b.text3, f),
                glass = mixColor(a.glass, b.glass, f),
                glass2 = mixColor(a.glass2, b.glass2, f),
                accent = mixColor(a.accent, b.accent, f),
                accentSoft = mixColor(a.accentSoft, b.accentSoft, f),
                accentLine = mixColor(a.accentLine, b.accentLine, f),
                onAccent = mixColor(a.onAccent, b.onAccent, f),
                ok = mixColor(a.ok, b.ok, f),
                warn = mixColor(a.warn, b.warn, f),
                bad = mixColor(a.bad, b.bad, f),
                accentId = if (f < 0.5f) a.accentId else b.accentId,
            )
        }

        private fun mixColor(a: Color, b: Color, t: Float): Color = Color(
            red = a.red + (b.red - a.red) * t,
            green = a.green + (b.green - a.green) * t,
            blue = a.blue + (b.blue - a.blue) * t,
            alpha = a.alpha + (b.alpha - a.alpha) * t,
        )
    }
}

fun buildProColors(dark: Boolean, accent: AccentDef): ProColors = if (dark) {
    ProColors(
        dark = true,
        bg = Color(0xFF07090D), bg2 = Color(0xFF0B0E14),
        surface = Color(0xFF10141C), surface2 = Color(0xFF151B25), surface3 = Color(0xFF1B2230),
        hairline = Color(0x2494A3B8), hairline2 = Color(0x4294A3B8),
        text = Color(0xFFF2F4F8), text2 = Color(0xFFB7BDC9), text3 = Color(0xFF8A919E),
        glass = Color(0xB810141C), glass2 = Color(0xDB0B0E14),
        accent = accent.darkHex,
        accentSoft = accent.darkHex.copy(alpha = 0.14f),
        accentLine = accent.darkHex.copy(alpha = 0.38f),
        onAccent = accent.onDark,
        ok = Color(0xFF34D399), warn = Color(0xFFFBBF24), bad = Color(0xFFF87171),
        accentId = accent.id,
    )
} else {
    ProColors(
        dark = false,
        bg = Color(0xFFF4F6F9), bg2 = Color.White,
        surface = Color.White, surface2 = Color(0xFFF2F4F8), surface3 = Color(0xFFE8ECF2),
        hairline = Color(0x1A0F172A), hairline2 = Color(0x2E0F172A),
        text = Color(0xFF10141C), text2 = Color(0xFF3D4451), text3 = Color(0xFF6A7280),
        glass = Color(0xCCFFFFFF), glass2 = Color(0xF0FFFFFF),
        accent = accent.lightHex,
        accentSoft = accent.lightHex.copy(alpha = 0.10f),
        accentLine = accent.lightHex.copy(alpha = 0.32f),
        onAccent = accent.onLight,
        ok = Color(0xFF34D399), warn = Color(0xFFFBBF24), bad = Color(0xFFF87171),
        accentId = accent.id,
    )
}

val LocalProColors = staticCompositionLocalOf { buildProColors(true, ACCENTS[0]) }

object ProType {
    val sectionTitle = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = (-0.2).sp)
    val statValue = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Black, fontSize = 29.sp)
    val statLabel = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, letterSpacing = 0.3.sp)
    val cardTitle = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 17.sp, letterSpacing = (-0.1).sp)
    val exerciseTitle = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 19.sp)
    val body = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 25.6.sp)
    val body2 = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp)
    val label = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    val small = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 13.sp)
    val chip = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
    val tab = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp, letterSpacing = 0.2.sp)
    val monoTimer = TextStyle(fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 30.sp)
    val brand = TextStyle(fontFamily = SpaceGrotesk, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, letterSpacing = (-0.2).sp)
}

/** NA4: Material You dynamic color — derive the accent from the system palette (Android 12+). */
@Composable
fun dynamicAccent(dark: Boolean): AccentDef? {
    if (android.os.Build.VERSION.SDK_INT < 31) return null
    val ctx = androidx.compose.ui.platform.LocalContext.current
    return if (dark) AccentDef("dynamic", "Material You", androidx.compose.material3.dynamicDarkColorScheme(ctx).primary, androidx.compose.material3.dynamicDarkColorScheme(ctx).primary, Color(0xFF071018), Color.White)
    else AccentDef("dynamic", "Material You", androidx.compose.material3.dynamicLightColorScheme(ctx).primary, androidx.compose.material3.dynamicLightColorScheme(ctx).primary, Color(0xFF071018), Color.White)
}

@Composable
fun ProTheme(
    dark: Boolean = true,
    accent: AccentDef = ACCENTS[0],
    dynamic: Boolean = false,
    content: @Composable () -> Unit,
) {
    val effectiveAccent = if (dynamic) dynamicAccent(dark) ?: accent else accent
    val pro = buildProColors(dark, effectiveAccent)
    val scheme: ColorScheme = if (dark) darkColorScheme(
        primary = pro.accent, onPrimary = pro.onAccent,
        background = pro.bg, onBackground = pro.text,
        surface = pro.surface, onSurface = pro.text,
        surfaceVariant = pro.surface2, onSurfaceVariant = pro.text2,
        error = pro.bad, secondary = pro.accent, tertiary = pro.ok,
        outline = pro.hairline2,
    ) else lightColorScheme(
        primary = pro.accent, onPrimary = pro.onAccent,
        background = pro.bg, onBackground = pro.text,
        surface = pro.surface, onSurface = pro.text,
        surfaceVariant = pro.surface2, onSurfaceVariant = pro.text2,
        error = pro.bad, secondary = pro.accent, tertiary = pro.ok,
        outline = pro.hairline2,
    )
    CompositionLocalProvider(LocalProColors provides pro) {
        // BATCH-5B: wire the M3 ColorScheme through ProColors so the new tint
        // family (tint / tintStrong / tintBg / tintLine / inkSoft / ring) and
        // the cardGradient / focusRing properties flow into M3 components.
        MaterialTheme(colorScheme = scheme, typography = MaterialTheme.typography.copy(
            bodyLarge = ProType.body,
        ), content = content)
    }
}

/** Utility: hex from legacy CSS (e.g. chart colors) → Color */
fun cssColor(hex: String): Color {
    val h = hex.trim().removePrefix("#")
    return when (h.length) {
        6 -> Color(android.graphics.Color.parseColor("#$h"))
        8 -> Color(android.graphics.Color.parseColor("#$h"))
        3 -> Color(android.graphics.Color.parseColor("#${h[0]}${h[0]}${h[1]}${h[1]}${h[2]}${h[2]}"))
        else -> Color.Gray
    }
}
