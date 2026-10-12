package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * TuningSheet — BATCH-15.
 *
 * Three sliders (Volume, Intensity, Frequency) that nudge the active profile's
 * coach ±20%. Persists via ProfileSettings (per-profile). The next workout
 * generation will multiply the program's targets by these factors.
 *
 * The sheet is a Composable; the host decides whether to render it inside a
 * ModalBottomSheet or inline.
 */
@Composable
fun TuningSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var vol by remember { mutableFloatStateOf(CoachingEngine.tuningVolume(ctx).toFloat()) }
    var intensity by remember { mutableFloatStateOf(CoachingEngine.tuningIntensity(ctx).toFloat()) }
    var freq by remember { mutableFloatStateOf(CoachingEngine.tuningFrequency(ctx).toFloat()) }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.glass2)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Coach tuning", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 16.sp, tint = c.text2, modifier = Modifier.clickable { onDismiss() })
        }
        Text("Nudge the coach ±20% on volume, intensity, and frequency. Per-profile — switching profiles resets to defaults.", style = ProType.small, color = c.text3)
        TuningSlider("Volume", vol) {
            vol = it
            CoachingEngine.setTuningVolume(ctx, it.toDouble())
        }
        TuningSlider("Intensity", intensity) {
            intensity = it
            CoachingEngine.setTuningIntensity(ctx, it.toDouble())
        }
        TuningSlider("Frequency", freq) {
            freq = it
            CoachingEngine.setTuningFrequency(ctx, it.toDouble())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button("Reset", icon = "fa-rotate-left", style = BtnStyle.GHOST, modifier = Modifier.weight(1f)) {
                vol = 1.0f; intensity = 1.0f; freq = 1.0f
                CoachingEngine.setTuningVolume(ctx, 1.0)
                CoachingEngine.setTuningIntensity(ctx, 1.0)
                CoachingEngine.setTuningFrequency(ctx, 1.0)
            }
            P4Button("Done", style = BtnStyle.PRIMARY, modifier = Modifier.weight(1f)) { onDismiss() }
        }
    }
}

@Composable
private fun TuningSlider(label: String, value: Float, onChange: (Float) -> Unit) {
    val c = LocalProColors.current
    val pct = ((value - 1.0f) * 100f).toInt()
    val pctLabel = if (pct >= 0) "+$pct%" else "$pct%"
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
            Text(pctLabel, style = ProType.label, color = if (pct >= 0) c.ok else c.warn, fontWeight = FontWeight.Bold)
        }
        androidx.compose.material3.Slider(
            value = value,
            onValueChange = { onChange(it) },
            valueRange = 0.8f..1.2f,
            steps = 7, // 0.8, 0.85, 0.9, 0.95, 1.0, 1.05, 1.1, 1.15, 1.2
        )
    }
}
