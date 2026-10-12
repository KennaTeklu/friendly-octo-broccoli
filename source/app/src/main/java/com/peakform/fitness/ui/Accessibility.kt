package com.peakform.fitness.ui

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.provider.Settings

object Accessibility {
    const val MIN_TAP_TARGET = 48

    @Composable
    fun isReducedMotion(): Boolean {
        val ctx = LocalContext.current
        return try {
            val scale = Settings.Global.getFloat(ctx.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
            scale == 0f
        } catch (_: Exception) { false }
    }

    fun Modifier.minTapTarget(): Modifier = this.then(sizeIn(minWidth = 48.dp, minHeight = 48.dp))
}

val LocalReducedMotion = staticCompositionLocalOf { false }
