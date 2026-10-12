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
import com.peakform.fitness.ui.FaIcon
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle

/**
 * PostSessionFeelSheet — Batch 9 self-assessment.
 *
 * Four-button RPE-style sheet shown after a workout completes:
 *   too_easy / just_right / hard / brutal + optional note.
 *
 * Saves a SelfAssessment.PostSessionFeel record keyed by workoutId
 * (so re-saving the same workout replaces the prior feel).
 */
@Composable
fun PostSessionFeelSheet(
    visible: Boolean,
    workoutId: String,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var feel by remember { mutableStateOf("just_right") }
    var note by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.glass2,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FaIcon("fa-face-smile", size = 18.sp, tint = c.accent)
                Spacer(Modifier.width(10.dp))
                Text("How did that feel?", style = ProType.cardTitle, color = c.text)
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Your answer tunes the next session — be honest, the engine learns from it.", style = ProType.small, color = c.text3)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FeelOption("too_easy", "Too easy", "fa-face-grin", c.ok) { feel = "too_easy" }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    FeelOption("just_right", "Just right", "fa-face-smile", c.accent) { feel = "just_right" }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    FeelOption("hard", "Hard", "fa-face-meh", c.warn) { feel = "hard" }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    FeelOption("brutal", "Brutal", "fa-face-tired", c.bad) { feel = "brutal" }
                }
                Spacer(Modifier.height(12.dp))
                Text("Notes (optional)", style = ProType.small, color = c.text3)
                Spacer(Modifier.height(6.dp))
                com.peakform.fitness.ui.screens.ProTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "Anything specific? (soreness, sleep, energy)",
                    singleLine = false,
                )
            }
        },
        confirmButton = {
            P4Button("Save", onClick = {
                SelfAssessment.saveFeel(ctx, SelfAssessment.PostSessionFeel(
                    workoutId = workoutId,
                    date = ProState.nowIso(),
                    feel = feel,
                    note = note,
                ))
                onDismiss()
            }, style = BtnStyle.PRIMARY)
        },
        dismissButton = {
            P4Button("Skip", onClick = onDismiss, style = BtnStyle.GHOST)
        },
    )
}

@Composable
private fun FeelOption(value: String, label: String, icon: String, tint: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    val c = LocalProColors.current
    val active = false // Visual selection state handled by the row container
    Box(
        Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tint.copy(alpha = 0.14f))
            .border(1.dp, tint.copy(alpha = 0.32f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon(icon, size = 16.sp, tint = tint)
            Spacer(Modifier.width(8.dp))
            Text(label, style = ProType.label, color = tint, fontWeight = FontWeight.SemiBold)
        }
    }
}
