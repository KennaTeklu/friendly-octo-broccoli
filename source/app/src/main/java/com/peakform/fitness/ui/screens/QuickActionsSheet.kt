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
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * QuickActionsSheet — BATCH-8 quick-jump panel surfaced from the dashboard.
 *
 * A bottom-sheet-style scrollable panel with the most-used one-tap actions:
 * start workout, run a 1RM test, open 1RM Lab, run health screen, take a
 * snapshot, open Recovery. Each action routes via onOpenSection so the shell
 * handles navigation.
 *
 * The sheet itself is presented as a Column so callers (MainActivity, the
 * "more" sheet) can host it inline or wrap it in a Dialog. The action list is
 * static — there is no per-action business logic here.
 */
@Composable
fun QuickActionsSheet(
    onOpenSection: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(c.glass2)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Quick actions", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            Text("Close", style = ProType.small, color = c.accent, modifier = Modifier.clickable { onDismiss() })
        }
        val actions = listOf(
            Action("Start workout", "fa-bolt", "workout", BtnStyle.PRIMARY),
            Action("Run 1RM test", "fa-gauge-high", "workout", BtnStyle.SECONDARY),
            Action("1RM Audit", "fa-magnifying-glass-chart", "onerm_audit", BtnStyle.INFO),
            Action("1RM Lab", "fa-flask", "onerm_lab", BtnStyle.INFO),
            Action("Recovery", "fa-bed", "recovery", BtnStyle.SECONDARY),
            Action("Health screen", "fa-notes-medical", "health", BtnStyle.SECONDARY),
            Action("Take snapshot", "fa-camera", "snapshots", BtnStyle.GHOST),
        )
        actions.forEach { a ->
            P4Button(a.label, icon = a.icon, style = a.style, modifier = Modifier.fillMaxWidth(), minHeight = 42) {
                onDismiss()
                onOpenSection(a.route)
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

private data class Action(val label: String, val icon: String, val route: String, val style: BtnStyle)
