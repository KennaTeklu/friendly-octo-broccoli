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
 * PostSessionFeelSheet — BATCH-15.
 *
 * Two-question post-session sheet:
 *   1. How did that feel? (1..10 emoji slider)
 *   2. Estimated RPE (optional, 1..10)
 *
 * On submit, CoachingEngine.recordFeel() captures the values for the active
 * profile, and the next workout generation will see them via
 * CoachingEngine.noteFor(ctx).
 *
 * The sheet is a Composable; the host decides whether to render it inside a
 * ModalBottomSheet or inline.
 */
@Composable
fun PostSessionFeelSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var feel by remember { mutableIntStateOf(7) }
    var rpe by remember { mutableIntStateOf(7) }
    var saved by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.glass2)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("How did that feel?", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 16.sp, tint = c.text2, modifier = Modifier.clickable { onDismiss() })
        }
        // feel slider 1..10 with emoji
        val emoji = when {
            feel >= 9 -> "🤩"
            feel >= 7 -> "🙂"
            feel >= 5 -> "😐"
            feel >= 3 -> "😣"
            else -> "🥵"
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Feel: $feel / 10", style = ProType.label, color = c.text)
                androidx.compose.material3.Slider(
                    value = feel.toFloat(),
                    onValueChange = { feel = it.toInt().coerceIn(1, 10) },
                    valueRange = 1f..10f,
                    steps = 8,
                )
            }
        }
        // RPE slider 1..10
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("RPE", style = ProType.label, color = c.text, modifier = Modifier.width(40.dp))
            Column(Modifier.weight(1f)) {
                Text("$rpe / 10", style = ProType.small, color = c.text2)
                androidx.compose.material3.Slider(
                    value = rpe.toFloat(),
                    onValueChange = { rpe = it.toInt().coerceIn(1, 10) },
                    valueRange = 1f..10f,
                    steps = 8,
                )
            }
        }
        P4Button(
            if (saved) "Saved ✓" else "Save & adjust next session",
            icon = "fa-save",
            style = BtnStyle.PRIMARY,
            modifier = Modifier.fillMaxWidth(),
        ) {
            CoachingEngine.recordFeel(ctx, feel, rpe)
            saved = true
            onDismiss()
        }
    }
}
