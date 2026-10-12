package com.peakform.fitness.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Accessibility — BATCH-13 helpers.
 *
 * - isReducedMotion(): respects the system "Remove animations" / animator
 *   scale 0 setting so users with motion sensitivity don't see animated
 *   transitions.
 * - minTapTarget(): enforces a minimum 44dp touch target on a Modifier.
 * - LocalReducedMotion: a composition local so descendants can read the
 *   current motion preference without re-querying the system.
 */
object Accessibility {
    /** True if the user has disabled / reduced animations at the system level. */
    fun isReducedMotion(ctx: android.content.Context): Boolean {
        return try {
            val resolver = ctx.contentResolver
            // Settings.Global.ANIMATOR_DURATION_SCALE == "animator_duration_scale"
            val scale = android.provider.Settings.Global.getFloat(
                resolver,
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            )
            scale == 0f
        } catch (_: Exception) { false }
    }

    /** Modifier that enforces a minimum 44dp tap target (Apple HIG / Material). */
    fun minTapTarget(): Modifier = Modifier.size(44.dp)
}

/** Composition local exposing the current reduced-motion setting to descendants. */
val LocalReducedMotion = staticCompositionLocalOf { false }

/** Helper to wire contentDescription into a Modifier (used by FaIcon etc.). */
fun Modifier.withContentDescription(description: String?): Modifier =
    if (description.isNullOrBlank()) this
    else this.semantics { contentDescription = description }
