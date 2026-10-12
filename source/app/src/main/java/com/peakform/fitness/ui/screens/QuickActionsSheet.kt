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
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * QuickActionsSheet — BATCH-15.
 *
 * A bottom-sheet-style surface surfacing the four most common in-workout
 * actions: log a set, switch exercise, open the timer, finish workout.
 * The sheet itself is a Composable; the host (WorkoutScreen) decides
 * whether to render it inside a ModalBottomSheet or inline.
 *
 * Reads CoachingEngine.noteFor(ctx) so the user sees the coach's current
 * prescription hint at the top of the sheet.
 */
@Composable
fun QuickActionsSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onLogSet: () -> Unit,
    onSwitchExercise: () -> Unit,
    onOpenTimer: () -> Unit,
    onFinishWorkout: () -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val note = remember { CoachingEngine.noteFor(ctx) }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.glass2)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp, 4.dp).clip(RoundedCornerShape(2.dp)).background(c.hairline2))
            Spacer(Modifier.weight(1f))
            FaIcon("fa-xmark", size = 16.sp, tint = c.text2, modifier = Modifier.clickable { onDismiss() })
        }
        Text("Quick actions", style = ProType.cardTitle, color = c.text)
        // coach's note
        GlassCard(padding = PaddingValues(10.dp)) {
            Text(note.headline, style = ProType.label, color = c.accent)
            Text(note.body, style = ProType.small, color = c.text2)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickActionTile("fa-pen-to-square", "Log set", Modifier.weight(1f)) { onLogSet() }
            QuickActionTile("fa-arrow-right-arrow-left", "Switch", Modifier.weight(1f)) { onSwitchExercise() }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickActionTile("fa-stopwatch", "Timer", Modifier.weight(1f)) { onOpenTimer() }
            QuickActionTile("fa-flag-checkered", "Finish", Modifier.weight(1f)) { onFinishWorkout() }
        }
    }
}

@Composable
private fun QuickActionTile(icon: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = LocalProColors.current
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface2)
            .border(1.dp, c.hairline, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FaIcon(icon, size = 18.sp, tint = c.accent)
        Spacer(Modifier.height(6.dp))
        Text(label, style = ProType.small, color = c.text2, fontWeight = FontWeight.SemiBold)
    }
}
