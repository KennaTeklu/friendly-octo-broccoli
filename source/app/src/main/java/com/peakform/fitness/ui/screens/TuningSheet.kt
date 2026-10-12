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
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * BATCH-8: TuningSheet — modal sheet that captures the user's tuning answers
 * (RPE at target, recovery, soreness, target volume) and shows the resulting
 * coach sentence so they understand what changed and why.
 */
@Composable
fun TuningSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    if (!visible) return
    val initial = remember { CoachingEngine.getTuning(ctx) }
    var rpeAtTarget by remember { mutableIntStateOf(initial.rpeAtTarget) }
    var recoveryReady by remember { mutableStateOf(initial.recoveryReady) }
    var lastSoreness by remember { mutableIntStateOf(initial.lastSessionSoreness) }
    var targetVolume by remember { mutableStateOf(initial.targetVolume) }

    val adjustment = remember(rpeAtTarget, recoveryReady, lastSoreness, targetVolume) {
        CoachingEngine.computeAdjustment(
            CoachingEngine.TuningAnswers(rpeAtTarget, recoveryReady, lastSoreness, targetVolume)
        )
    }

    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.glass2).padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Tune today's session", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text("RPE at the target weight: $rpeAtTarget / 10", style = ProType.small, color = c.text3)
        Slider(value = rpeAtTarget.toFloat(), onValueChange = { rpeAtTarget = it.toInt().coerceIn(1, 10) }, valueRange = 1f..10f, steps = 8)
        Spacer(Modifier.height(8.dp))

        Text("Soreness from last session: $lastSoreness / 5", style = ProType.small, color = c.text3)
        Slider(value = lastSoreness.toFloat(), onValueChange = { lastSoreness = it.toInt().coerceIn(0, 5) }, valueRange = 0f..5f, steps = 4)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Recovery ready?", style = ProType.body2, color = c.text, modifier = Modifier.weight(1f))
            androidx.compose.material3.Switch(checked = recoveryReady, onCheckedChange = { recoveryReady = it })
        }
        Spacer(Modifier.height(8.dp))

        Text("Volume target today", style = ProType.small, color = c.text3)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
            listOf("cut" to "Cut", "maintain" to "Maintain", "push" to "Push").forEach { (k, label) ->
                val active = targetVolume == k
                Box(
                    Modifier.clip(RoundedCornerShape(999.dp))
                        .background(if (active) c.accentSoft else c.surface2)
                        .border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(999.dp))
                        .clickable { targetVolume = k }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(label, fontSize = 12.sp, color = if (active) c.accent else c.text2)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // Coach sentence preview.
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(c.tintBg).border(1.dp, c.tintLine, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Text("Coach", style = ProType.small, color = c.text3, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(adjustment.sentence, style = ProType.body2, color = c.text)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Adjusted: sets ${if (adjustment.setsDelta >= 0) "+" else ""}${adjustment.setsDelta} · " +
                        "reps ${if (adjustment.repsDelta >= 0) "+" else ""}${adjustment.repsDelta} · " +
                        "weight ${(adjustment.weightPct * 100 - 100).toInt()}%",
                    style = ProType.small, color = c.text3,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        P4Button("Save tuning", icon = "fa-save", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
            CoachingEngine.saveTuning(ctx, CoachingEngine.TuningAnswers(rpeAtTarget, recoveryReady, lastSoreness, targetVolume))
            onDismiss()
        }
        Spacer(Modifier.height(8.dp))
        P4Button("Cancel", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) { onDismiss() }
    }
}
