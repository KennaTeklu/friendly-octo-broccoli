package com.peakform.fitness.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.ui.*

/**
 * BATCH-6 Item: Quick Actions modal.
 *
 * Deferred from Batch 4C. A bottom sheet with quick shortcuts to the most
 * common destinations: Start Workout, Open Library, View History, View
 * Progress, Open Settings. Triggered from a floating button on the Dashboard.
 */
@Composable
fun QuickActionsSheet(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    val c = LocalProColors.current
    val actions = listOf(
        QuickAction("Start workout", "fa-play", "dashboard") { onNavigate("dashboard") },
        QuickAction("Library", "fa-dumbbell", "library") { onNavigate("library") },
        QuickAction("History", "fa-clock-rotate-left", "history") { onNavigate("history") },
        QuickAction("Progress", "fa-chart-line", "progress") { onNavigate("progress") },
        QuickAction("1RM Audit", "fa-weight-hanging", "1rm_audit") { onNavigate("1rm_audit") },
        QuickAction("Settings", "fa-gear", "settings") { onNavigate("settings") },
    )

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(c.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Quick Actions", style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 20.sp, tint = c.text2, contentDescription = "Close",
                modifier = Modifier.clickable(onClick = onDismiss).padding(4.dp).semantics { this.contentDescription = "Close quick actions" })
        }
        actions.forEach { action ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface2)
                    .clickable { action.onClick(); onDismiss() }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .semantics { this.contentDescription = action.label },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FaIcon(action.icon, size = 18.sp, tint = c.accent)
                Text(action.label, style = ProType.body, color = c.text)
            }
        }
    }
}

data class QuickAction(val label: String, val icon: String, val section: String, val onClick: () -> Unit)
