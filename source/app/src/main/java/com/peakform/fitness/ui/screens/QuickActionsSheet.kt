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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle

/**
 * QuickActionsSheet — BATCH-6 quick-actions bottom sheet.
 *
 * Shortcuts the legacy HTML app exposed via a "+" FAB drop-down. Here we
 * surface them as a vertical column of one-tap chips so the user can fire
 * off the most common actions without navigating to the dock.
 */
@Composable
fun QuickActionsSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val actions = listOf(
        "export_data" to ("fa-cloud-arrow-down" to "Export full backup"),
        "export_history_csv" to ("fa-file-csv" to "Export history CSV"),
        "longevity_workout" to ("fa-heart-pulse" to "Longevity workout"),
        "test_1rm" to ("fa-trophy" to "Test a 1RM"),
        "snapshot_now" to ("fa-camera" to "Take a snapshot now"),
        "open_studio" to ("fa-flask-vial" to "Open Library Studio"),
        "open_audit" to ("fa-clipboard-check" to "Open 1RM Audit"),
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(c.bg.copy(alpha = 0.6f))
            .clickable { onDismiss() },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(c.surface)
                .border(1.dp, c.hairline, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Quick Actions", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
                P4Button("Close", style = BtnStyle.GHOST, onClick = onDismiss)
            }
            Spacer(Modifier.height(12.dp))
            actions.forEach { (id, info) ->
                val (icon, label) = info
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .clickable { onAction(id) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(c.accentSoft), contentAlignment = Alignment.Center) {
                        com.peakform.fitness.ui.FaIcon(icon, size = 14.sp, tint = c.accent)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(label, style = ProType.body2, color = c.text, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Tap an action to run it. Long-press the brand in the top bar to open the command palette.",
                style = ProType.small, color = c.text3)
        }
    }
}
