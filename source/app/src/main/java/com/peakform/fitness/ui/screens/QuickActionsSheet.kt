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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType

/**
 * QuickActionsSheet — 5A+5B+6+7: a composable listing the app's quick actions
 * (Start workout, Library, History, Progress, 1RM Audit, Settings). Each row is
 * an icon + label, fully clickable, with a contentDescription for accessibility.
 *
 * Intended to be embedded inside a ModalBottomSheet or AlertDialog — the host
 * manages visibility and the action callback is propagated to the parent shell.
 */
@Composable
fun QuickActionsSheet(onAction: (String) -> Unit) {
    val c = LocalProColors.current
    val items = listOf(
        QuickAction("workout", "Start workout", "fa-play"),
        QuickAction("library", "Library", "fa-book-open"),
        QuickAction("history", "History", "fa-clock-rotate-left"),
        QuickAction("progress", "Progress", "fa-chart-line"),
        QuickAction("1rm_audit", "1RM Audit", "fa-trophy"),
        QuickAction("settings", "Settings", "fa-cog"),
    )
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        items.forEach { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface)
                    .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
                    .clickable { onAction(item.route) }
                    .semantics { contentDescription = item.label }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                com.peakform.fitness.ui.FaIcon(item.icon, size = 16.sp, tint = c.accent)
                Spacer(Modifier.width(12.dp))
                Text(item.label, style = ProType.body2, color = c.text)
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

private data class QuickAction(val route: String, val label: String, val icon: String)
