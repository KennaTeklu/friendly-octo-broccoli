package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.SelfAssessment
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * BATCH-8: PostSessionFeelSheet — captures the user's subjective feedback
 * after finishing a workout (overall RPE, felt-strong, minutes trained, notes).
 * Stored per-profile via SelfAssessment.savePostSession and surfaced in the
 * Progress trends.
 */
@Composable
fun PostSessionFeelSheet(
    visible: Boolean,
    workoutId: String?,
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    if (!visible) return
    var rpe by remember { mutableIntStateOf(7) }
    var feltStrong by remember { mutableStateOf(false) }
    var minutes by remember { mutableIntStateOf(45) }
    var notes by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.glass2).padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("How did it feel?", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text("Overall RPE: $rpe / 10", style = ProType.small, color = c.text3)
        Slider(value = rpe.toFloat(), onValueChange = { rpe = it.toInt().coerceIn(1, 10) }, valueRange = 1f..10f, steps = 8)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Felt strong today?", style = ProType.body2, color = c.text, modifier = Modifier.weight(1f))
            androidx.compose.material3.Switch(checked = feltStrong, onCheckedChange = { feltStrong = it })
        }
        Spacer(Modifier.height(8.dp))

        Text("Minutes trained: $minutes", style = ProType.small, color = c.text3)
        Slider(value = minutes.toFloat(), onValueChange = { minutes = it.toInt().coerceIn(5, 240) }, valueRange = 5f..240f, steps = 46)
        Spacer(Modifier.height(8.dp))

        Text("Notes (optional)", style = ProType.small, color = c.text3)
        ProTextField(value = notes, onValueChange = { notes = it }, placeholder = "What worked? What didn't?")
        Spacer(Modifier.height(12.dp))
        P4Button("Save feedback", icon = "fa-save", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
            SelfAssessment.savePostSession(ctx, SelfAssessment.PostSessionFeel(
                workoutId = workoutId ?: "unknown",
                at = ProState.nowIso(),
                overallRpe = rpe,
                feltStrong = feltStrong,
                notes = notes,
                minutesTrained = minutes,
            ))
            onDismiss()
        }
        Spacer(Modifier.height(8.dp))
        P4Button("Skip", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) { onDismiss() }
    }
}
