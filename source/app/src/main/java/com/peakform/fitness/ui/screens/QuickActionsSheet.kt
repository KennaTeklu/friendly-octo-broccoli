package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * QuickActionsSheet — BATCH-15.
 *
 * The bottom-dock "more" sheet already exists (MoreSheet in MainActivity),
 * but the new QuickActionsSheet is a focused subset: the four most-used
 * actions (resume / theme toggle / export / 1RM audit) surfaced as a
 * horizontal pill row so the user can one-tap them from the dashboard.
 *
 * Renders inside any AlertDialog / ModalBottomSheet content slot.
 */
@Composable
fun QuickActionsSheet(
    onDismiss: () -> Unit,
    onResume: () -> Unit,
    onToggleTheme: () -> Unit,
    onExport: () -> Unit,
    onOneRmAudit: () -> Unit,
) {
    val c = LocalProColors.current
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-bolt", size = 16.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Quick actions", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 14.sp, tint = c.text3, modifier = Modifier.clickable { onDismiss() })
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            QuickAction("fa-play", "Resume", c.accent, Modifier.weight(1f)) { onResume() }
            QuickAction("fa-circle-half-stroke", "Theme", c.accent, Modifier.weight(1f)) { onToggleTheme() }
            QuickAction("fa-file-export", "Export", c.accent, Modifier.weight(1f)) { onExport() }
            QuickAction("fa-magnifying-glass-chart", "1RM Audit", c.accent, Modifier.weight(1f)) { onOneRmAudit() }
        }
        Spacer(Modifier.height(16.dp))
        Text("Swipe down to dismiss", style = ProType.small, color = c.text3)
    }
}

@Composable
private fun QuickAction(icon: String, label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val c = LocalProColors.current
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface)
            .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FaIcon(icon, size = 18.sp, tint = color)
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = c.text2, fontWeight = FontWeight.SemiBold)
    }
}
