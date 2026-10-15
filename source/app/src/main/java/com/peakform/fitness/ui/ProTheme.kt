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
import androidx.compose.ui.graphics.lerp
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
    // BATCH-38b: CSS tint system port from app.html L3753-3756.
    // Light: color-mix(in srgb, accent N%, #FFFFFF / #F7F9FC)
    // Dark:  lerp toward dark surface (#10141C) / bg (#07090D)
    val tint: Color,
    val tintStrong: Color,
    val tintBg: Color,
    val tintLine: Color,
) {
    val accentGradient: Brush
        get() = Brush.linearGradient(listOf(accent, lighten(accent, 0.28f)))

    val primaryButtonShadow: Brush get() = accentGradient

    /** BATCH-5B: muted ink (body2 captions) — text2. */
    val inkSoft: Color get() = text2
    /** BATCH-5B: focus-ring color for inputs. */
    val ring: Color get() = accent.copy(alpha = 0.55f)
    /** BATCH-5B: card gradient (subtle vertical accent → surface) — used on hero cards. */
    val cardGradient: Brush
        get() = Brush.verticalGradient(listOf(surface, surface2))
    /** BATCH-5B: focus-ring brush for outlined inputs. */
    val focusRing: Brush
        get() = Brush.linearGradient(listOf(accent.copy(alpha = 0.55f), accent.copy(alpha = 0.25f)))

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

        /** BATCH-5B: blend two colors at a fractional position (0..1). */
        fun mix(a: Color, b: Color, t: Float): Color = Color(
            red = a.red * (1f - t) + b.red * t,
            green = a.green * (1f - t) + b.green * t,
            blue = a.blue * (1f - t) + b.blue * t,
            alpha = a.alpha * (1f - t) + b.alpha * t,
        )
    }
}

fun buildProColors(dark: Boolean, accent: AccentDef): ProColors = if (dark) {
    // BATCH-38b: dark mode tint system — lerp toward dark surface (#10141C) / bg (#07090D)
    // The HTML dark mode doesn't define explicit tint vars; these are the dark
    // equivalents of the light-mode color-mix values from app.html L3753-3756.
    val s = Color(0xFF10141C)  // surface
    val b = Color(0xFF07090D)  // bg
    val a = accent.darkHex
    ProColors(
        dark = true,
        bg = b, bg2 = Color(0xFF0B0E14),
        surface = s, surface2 = Color(0xFF151B25), surface3 = Color(0xFF1B2230),
        hairline = Color(0x2494A3B8), hairline2 = Color(0x4294A3B8),
        text = Color(0xFFF2F4F8), text2 = Color(0xFFB7BDC9), text3 = Color(0xFF8A919E),
        glass = Color(0xB810141C), glass2 = Color(0xDB0B0E14),
        accent = a,
        accentSoft = a.copy(alpha = 0.14f),
        accentLine = a.copy(alpha = 0.38f),
        onAccent = accent.onDark,
        ok = Color(0xFF34D399), warn = Color(0xFFFBBF24), bad = Color(0xFFF87171),
        accentId = accent.id,
        // Dark tints: lerp(surface, accent, fraction) = surface * (1-f) + accent * f
        tint = lerp(s, a, 0.09f),        // 9% accent toward dark surface
        tintStrong = lerp(s, a, 0.17f),  // 17% accent toward dark surface
        tintBg = lerp(b, a, 0.06f),      // 6% accent toward dark bg
        tintLine = lerp(s, a, 0.24f),    // 24% accent toward dark surface
    )
} else {
    // BATCH-38b: light mode tint system — from app.html L3753-3756
    // color-mix(in srgb, accent N%, #FFFFFF / #F7F9FC)
    val w = Color.White
    val b = Color(0xFFF4F6F9)  // page bg
    val a = accent.lightHex
    ProColors(
        dark = false,
        bg = b, bg2 = Color.White,
        surface = w, surface2 = Color(0xFFF2F4F8), surface3 = Color(0xFFE8ECF2),
        hairline = Color(0x1A0F172A), hairline2 = Color(0x2E0F172A),
        text = Color(0xFF10141C), text2 = Color(0xFF3D4451), text3 = Color(0xFF6A7280),
        glass = Color(0xCCFFFFFF), glass2 = Color(0xF0FFFFFF),
        accent = a,
        accentSoft = a.copy(alpha = 0.10f),
        accentLine = a.copy(alpha = 0.32f),
        onAccent = accent.onLight,
        ok = Color(0xFF34D399), warn = Color(0xFFFBBF24), bad = Color(0xFFF87171),
        accentId = accent.id,
        // Light tints: lerp(white, accent, fraction) = white * (1-f) + accent * f
        tint = lerp(w, a, 0.09f),         // 9% accent + 91% white (app.html L3753)
        tintStrong = lerp(w, a, 0.17f),  // 17% accent + 83% white (L3754)
        tintBg = lerp(b, a, 0.06f),       // 6% accent + 94% page bg (L3755)
        tintLine = lerp(w, a, 0.24f),     // 24% accent + 76% white (L3756)
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
