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
    // BATCH-5 Item 3: derived tint tokens — the color-mix() equivalent.
    // These mirror the CSS --p4-tint* variables in base.css:7306-7400.
    // Every element the HTML recolours (cards, charts, rings, badges, radar
    // polygons, progress bars, focus rings) reads from these so the whole
    // app glows with the chosen accent, not just buttons.
    val tint: Color,          // accent 9% on white (light) / accent 14% on surface (dark)
    val tintStrong: Color,    // accent 17% on white / accent 20% on surface
    val tintBg: Color,         // accent 6% on bg
    val tintLine: Color,       // accent 24% on white / accent 30% on surface
    val inkSoft: Color,        // accent 62% on ink (light mode button text)
    val ring: Color,           // accent 45% alpha (focus ring)
) {
    val accentGradient: Brush
        get() = Brush.linearGradient(listOf(accent, lighten(accent, 0.28f)))

    val primaryButtonShadow: Brush get() = accentGradient

    /** BATCH-5 Item 3: card background gradient — accent-washed top melting
     *  into crisp surface. Mirrors the light-mode .card gradient in base.css. */
    val cardGradient: Brush
        get() = Brush.linearGradient(listOf(tintStrong, tint, surface))

    /** BATCH-5 Item 3: focus ring color for outline/border. */
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

        /** BATCH-5 Item 3: color-mix(in srgb, accent P%, base) equivalent.
         *  Linear interpolation in sRGB space — same as CSS color-mix(). */
        fun mix(accent: Color, base: Color, pct: Float): Color = Color(
            red = base.red + (accent.red - base.red) * pct,
            green = base.green + (accent.green - base.green) * pct,
            blue = base.blue + (accent.blue - base.blue) * pct,
            alpha = base.alpha,
        )
    }
}

fun buildProColors(dark: Boolean, accent: AccentDef): ProColors {
    val accentColor = if (dark) accent.darkHex else accent.lightHex
    val onAccent = if (dark) accent.onDark else accent.onLight
    val white = Color.White
    val ink = if (dark) Color(0xFF0F172A) else Color(0xFF0F172A)
    return if (dark) {
        // Dark mode: tints mixed onto the dark surface, not white.
        // Mirrors the dark-mode accent usage in base.css (accent-soft / accent-line).
        val surface = Color(0xFF10141C)
        ProColors(
            dark = true,
            bg = Color(0xFF07090D), bg2 = Color(0xFF0B0E14),
            surface = surface, surface2 = Color(0xFF151B25), surface3 = Color(0xFF1B2230),
            hairline = Color(0x2494A3B8), hairline2 = Color(0x4294A3B8),
            text = Color(0xFFF2F4F8), text2 = Color(0xFFB7BDC9), text3 = Color(0xFF8A919E),
            glass = Color(0xB810141C), glass2 = Color(0xDB0B0E14),
            accent = accentColor,
            accentSoft = accentColor.copy(alpha = 0.14f),
            accentLine = accentColor.copy(alpha = 0.38f),
            onAccent = onAccent,
            ok = Color(0xFF34D399), warn = Color(0xFFFBBF24), bad = Color(0xFFF87171),
            accentId = accent.id,
            // BATCH-5 Item 3: derived tints (dark mode — mix onto surface)
            tint = ProColors.mix(accentColor, surface, 0.14f),
            tintStrong = ProColors.mix(accentColor, surface, 0.20f),
            tintBg = ProColors.mix(accentColor, Color(0xFF07090D), 0.06f),
            tintLine = ProColors.mix(accentColor, surface, 0.30f),
            inkSoft = ProColors.mix(accentColor, ink, 0.62f),
            ring = accentColor.copy(alpha = 0.45f),
        )
    } else {
        // Light mode: tints mixed onto white (matches base.css:7306-7400).
        val bg = Color(0xFFF4F6F9)
        ProColors(
            dark = false,
            bg = bg, bg2 = Color.White,
            surface = Color.White, surface2 = Color(0xFFF2F4F8), surface3 = Color(0xFFE8ECF2),
            hairline = Color(0x1A0F172A), hairline2 = Color(0x2E0F172A),
            text = Color(0xFF10141C), text2 = Color(0xFF3D4451), text3 = Color(0xFF6A7280),
            glass = Color(0xCCFFFFFF), glass2 = Color(0xF0FFFFFF),
            accent = accentColor,
            accentSoft = accentColor.copy(alpha = 0.10f),
            accentLine = accentColor.copy(alpha = 0.32f),
            onAccent = onAccent,
            ok = Color(0xFF34D399), warn = Color(0xFFFBBF24), bad = Color(0xFFF87171),
            accentId = accent.id,
            // BATCH-5 Item 3: derived tints (light mode — color-mix onto white)
            // --p4-tint:        color-mix(in srgb, var(--p4-accent) 9%,  #FFFFFF)
            // --p4-tint-strong: color-mix(in srgb, var(--p4-accent) 17%, #FFFFFF)
            // --p4-tint-bg:     color-mix(in srgb, var(--p4-accent) 6%,  #F7F9FC)
            // --p4-tint-line:   color-mix(in srgb, var(--p4-accent) 24%, #FFFFFF)
            // --p4-ink-soft:    color-mix(in srgb, var(--p4-accent) 62%, #0F172A)
            tint = ProColors.mix(accentColor, white, 0.09f),
            tintStrong = ProColors.mix(accentColor, white, 0.17f),
            tintBg = ProColors.mix(accentColor, Color(0xFFF7F9FC), 0.06f),
            tintLine = ProColors.mix(accentColor, white, 0.24f),
            inkSoft = ProColors.mix(accentColor, Color(0xFF0F172A), 0.62f),
            ring = accentColor.copy(alpha = 0.40f),
        )
    }
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
        // BATCH-5 Item 3: Material 3 scheme now includes the derived tints so
        // every M3 component (cards, chips, sliders, progress bars) follows
        // the accent without each caller reading LocalProColors manually.
        surfaceTint = pro.tint,
        primaryContainer = pro.tintStrong,
        onPrimaryContainer = pro.inkSoft,
        secondaryContainer = pro.tint,
        tertiaryContainer = pro.tint,
    ) else lightColorScheme(
        primary = pro.accent, onPrimary = pro.onAccent,
        background = pro.bg, onBackground = pro.text,
        surface = pro.surface, onSurface = pro.text,
        surfaceVariant = pro.surface2, onSurfaceVariant = pro.text2,
        error = pro.bad, secondary = pro.accent, tertiary = pro.ok,
        outline = pro.hairline2,
        surfaceTint = pro.tint,
        primaryContainer = pro.tintStrong,
        onPrimaryContainer = pro.inkSoft,
        secondaryContainer = pro.tint,
        tertiaryContainer = pro.tint,
    )
    CompositionLocalProvider(LocalProColors provides pro) {
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
