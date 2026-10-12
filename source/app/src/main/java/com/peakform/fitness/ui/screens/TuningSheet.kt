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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * TuningSheet — BATCH-9 inline tuning panel for the Workout screen.
 *
 * Surfaced via the "tuning gear" icon on each exercise card, this panel lets
 * the user nudge the prescription (sets / reps / weight / rest) for the
 * current session only. The values are not persisted back to the 1RM model
 * (that path goes through the 1RM Lab) — they only adjust the displayed
 * prescription for this workout.
 *
 * Caller is responsible for the actual mutate of `ProState.currentWorkout`.
 * This composable returns the proposed values via onApply; the caller decides
 * whether to commit them.
 */
@Composable
fun TuningSheet(
    initialSets: Int,
    initialReps: Int,
    initialWeight: Double,
    initialRest: Int,
    onApply: (sets: Int, reps: Int, weight: Double, rest: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    var sets by remember { mutableIntStateOf(initialSets) }
    var reps by remember { mutableIntStateOf(initialReps) }
    var weight by remember { mutableStateOf(initialWeight) }
    var rest by remember { mutableIntStateOf(initialRest) }

    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(c.glass2)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tune prescription", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            Text("Close", style = ProType.small, color = c.accent, modifier = Modifier.clickable { onDismiss() })
        }
        Text("Adjust this session only — the 1RM model is not modified.", style = ProType.small, color = c.text3)

        LabeledStepper("Sets", sets, 1..8) { sets = it }
        LabeledStepper("Reps", reps, 1..20) { reps = it }
        Text("Weight (lbs): ${weight.toInt()}", style = ProType.small, color = c.text2)
        Slider(value = weight.toFloat(), onValueChange = { weight = it.toDouble() }, valueRange = 0f..500f)
        Text("Rest (s): $rest", style = ProType.small, color = c.text2)
        Slider(value = rest.toFloat(), onValueChange = { rest = it.toInt() }, valueRange = 30f..300f, steps = 8)

        P4Button("Apply", icon = "fa-check", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth(), minHeight = 42) {
            onApply(sets, reps, weight, rest)
            onDismiss()
        }
    }
}

@Composable
private fun LabeledStepper(label: String, value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    val c = LocalProColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            P4Button("−", style = BtnStyle.GHOST, minHeight = 32) { if (value > range.first) onValueChange(value - 1) }
            Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(c.surface2), contentAlignment = Alignment.Center) {
                Text("$value", style = ProType.label, color = c.text)
            }
            P4Button("+", style = BtnStyle.GHOST, minHeight = 32) { if (value < range.last) onValueChange(value + 1) }
        }
    }
}
