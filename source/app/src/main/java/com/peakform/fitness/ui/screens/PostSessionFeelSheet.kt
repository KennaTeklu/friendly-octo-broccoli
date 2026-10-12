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
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle

/**
 * PostSessionFeelSheet — BATCH-9 post-session feel tracker.
 *
 * Three sliders (effort, readiness-next, soreness-expected) plus an optional
 * note. Saves to per-profile SharedPreferences keyed on the workout id.
 * Reachable from the Workout screen's "Log Session" flow.
 */
@Composable
fun PostSessionFeelSheet(
    visible: Boolean,
    sessionId: String,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var effort by remember(sessionId) { mutableIntStateOf(7) }
    var readiness by remember(sessionId) { mutableIntStateOf(7) }
    var soreness by remember(sessionId) { mutableIntStateOf(3) }
    var note by remember(sessionId) { mutableStateOf("") }

    Box(
        Modifier.fillMaxSize().background(c.bg.copy(alpha = 0.6f)).clickable { onDismiss() },
    ) {
        Column(
            Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(c.surface)
                .border(1.dp, c.hairline, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.peakform.fitness.ui.FaIcon("fa-comment-dots", size = 16.sp, tint = c.accent)
                Spacer(Modifier.width(8.dp))
                Text("How did that feel?", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
                P4Button("Close", style = BtnStyle.GHOST, onClick = onDismiss)
            }
            Spacer(Modifier.height(8.dp))
            Text("Rate the session — effort you put in, readiness for next time, soreness you expect.",
                style = ProType.small, color = c.text3)
            Spacer(Modifier.height(12.dp))
            RatingRow("Effort", effort, 1..10, c) { effort = it }
            Spacer(Modifier.height(8.dp))
            RatingRow("Readiness next time", readiness, 1..10, c) { readiness = it }
            Spacer(Modifier.height(8.dp))
            RatingRow("Soreness expected", soreness, 1..5, c) { soreness = it }
            Spacer(Modifier.height(12.dp))
            com.peakform.fitness.ui.screens.ProTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = "Optional note (how did it really go?)",
            )
            Spacer(Modifier.height(12.dp))
            P4Button(
                "Save feel",
                icon = "fa-save",
                style = BtnStyle.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SelfAssessment.save(
                    ctx,
                    SelfAssessment.Feel(
                        sessionId = sessionId,
                        atIso = ProState.nowIso(),
                        effort = effort,
                        readinessNext = readiness,
                        sorenessExpected = soreness,
                        note = note.takeIf { it.isNotBlank() },
                    ),
                )
                onDismiss()
            }
        }
    }
}

@Composable
private fun RatingRow(label: String, value: Int, range: IntRange, c: com.peakform.fitness.ui.ProColors, onSet: (Int) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
            Text("$value", style = ProType.statValue, color = c.accent, fontSize = 18.sp)
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            range.forEach { n ->
                val active = n == value
                Box(
                    Modifier.weight(1f).height(36.dp).clip(RoundedCornerShape(6.dp))
                        .background(if (active) c.accentSoft else c.surface2)
                        .border(1.dp, if (active) c.accent else c.hairline, RoundedCornerShape(6.dp))
                        .clickable { onSet(n) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("$n", fontSize = 11.sp, color = if (active) c.accent else c.text2, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
