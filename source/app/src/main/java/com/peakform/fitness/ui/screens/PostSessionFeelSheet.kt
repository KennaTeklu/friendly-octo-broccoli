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
 * PostSessionFeelSheet — BATCH-9 post-workout feel capture.
 *
 * After completing a workout, the user is asked to rate how the session went
 * (1–5 stars) and tag how they feel (energy, soreness, motivation, sleep).
 * The capture is persisted via SelfAssessment.record() into per-profile
 * SharedPreferences.
 */
@Composable
fun PostSessionFeelSheet(
    workoutId: String?,
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var rating by remember { mutableIntStateOf(0) }
    var energy by remember { mutableIntStateOf(0) }
    var soreness by remember { mutableIntStateOf(0) }
    var motivation by remember { mutableIntStateOf(0) }
    var sleep by remember { mutableIntStateOf(0) }
    var note by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(c.glass2)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("How did that feel?", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            Text("Close", style = ProType.small, color = c.accent, modifier = Modifier.clickable { onDismiss() })
        }
        StarRow("Overall rating", rating) { rating = it }
        StarRow("Energy", energy) { energy = it }
        StarRow("Soreness (5 = very sore)", soreness) { soreness = it }
        StarRow("Motivation", motivation) { motivation = it }
        StarRow("Sleep last night", sleep) { sleep = it }
        Text("Notes (optional)", style = ProType.small, color = c.text2)
        ProTextField(value = note, onValueChange = { note = it.take(280) }, placeholder = "What worked? What didn't?")
        if (saved) {
            Text("Saved — thank you!", style = ProType.label, color = c.ok)
        }
        P4Button("Save", icon = "fa-save", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth(), minHeight = 42,
            enabled = rating > 0 && energy > 0) {
            SelfAssessment.record(ctx, SelfAssessment.Assessment(
                at = ProState.nowIso(),
                workoutId = workoutId,
                rating = rating,
                energy = energy,
                soreness = soreness,
                motivation = motivation,
                sleep = sleep,
                note = note,
            ))
            saved = true
        }
    }
}

@Composable
private fun StarRow(label: String, value: Int, onPick: (Int) -> Unit) {
    val c = LocalProColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { s ->
                Box(Modifier.size(28.dp).clip(RoundedCornerShape(6.dp))
                    .background(if (s <= value) c.accent else c.surface2)
                    .border(1.dp, if (s <= value) c.accent else c.hairline2, RoundedCornerShape(6.dp))
                    .clickable { onPick(s) },
                    contentAlignment = Alignment.Center) {
                    Text("★", fontSize = 14.sp, color = if (s <= value) c.onAccent else c.text3)
                }
            }
        }
    }
}
