package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
 * PostSessionFeelSheet — BATCH-15.
 *
 * Surfaces the SelfAssessment survey after a workout is completed. The
 * captured RPE / readiness / mood / sleep feeds back into the next session's
 * prescribed load (via SelfAssessment.recommendedLoadMultiplier).
 *
 * Renders inside any AlertDialog text slot.
 */
@Composable
fun PostSessionFeelSheet(onDismiss: () -> Unit, onSave: (SelfAssessment) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var rpe by remember { mutableIntStateOf(7) }
    var readiness by remember { mutableIntStateOf(3) }
    var soreness by remember { mutableIntStateOf(1) }
    var sleep by remember { mutableStateOf(7f) }
    var mood by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-face-smile", size = 16.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("How did that feel?", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 14.sp, tint = c.text3, modifier = Modifier.clickable { onDismiss() })
        }
        Spacer(Modifier.height(10.dp))
        FeelRow("RPE (1-10)", "$rpe", rpe, 1..10) { rpe = it }
        Spacer(Modifier.height(8.dp))
        FeelRow("Readiness (1-5)", "$readiness", readiness, 1..5) { readiness = it }
        Spacer(Modifier.height(8.dp))
        FeelRow("Mood (1-5)", "$mood", mood, 1..5) { mood = it }
        Spacer(Modifier.height(8.dp))
        FeelRow("Soreness (0-3)", "$soreness", soreness, 0..3) { soreness = it }
        Spacer(Modifier.height(8.dp))
        Text("Sleep last night: ${sleep.toInt()}h", style = ProType.small, color = c.text3)
        SliderRow(value = sleep, range = 0f..12f, onValueChange = { sleep = it })
        Spacer(Modifier.height(8.dp))
        Text("Notes (optional)", style = ProType.small, color = c.text3)
        ProTextField(value = notes, onValueChange = { notes = it }, placeholder = "Anything to remember for next time?")
        Spacer(Modifier.height(12.dp))
        val multiplier = SelfAssessment.recommendedLoadMultiplier(
            SelfAssessment(rpe = rpe, readiness = readiness, mood = mood, soreness = soreness, sleepHours = sleep.toDouble())
        )
        Text(
            "Next session recommendation: " +
                if (multiplier > 1.0) "push +${((multiplier - 1) * 100).toInt()}%"
                else if (multiplier < 1.0) "back off -${((1 - multiplier) * 100).toInt()}%"
                else "hold the same load",
            style = ProType.small, color = c.accent,
        )
        Spacer(Modifier.height(10.dp))
        P4Button("Save and apply to next session", icon = "fa-save", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
            val a = SelfAssessment(
                rpe = rpe, readiness = readiness, soreness = soreness,
                sleepHours = sleep.toDouble(), mood = mood, notes = notes,
                timestamp = ProState.nowIso(),
            )
            SelfAssessment.save(ctx, a)
            onSave(a)
            onDismiss()
        }
    }
}

@Composable
private fun FeelRow(label: String, value: String, current: Int, range: IntRange, onPick: (Int) -> Unit) {
    val c = LocalProColors.current
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = ProType.small, color = c.text3, modifier = Modifier.weight(1f))
            Text(value, style = ProType.label, color = c.accent)
        }
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            range.forEach { v ->
                val active = v == current
                Box(
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (active) c.accentSoft else c.surface2)
                        .border(1.dp, if (active) c.accent else c.hairline2, RoundedCornerShape(6.dp))
                        .clickable { onPick(v) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$v", fontSize = 11.sp, color = if (active) c.accent else c.text2, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SliderRow(value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    androidx.compose.material3.Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = range,
    )
}
