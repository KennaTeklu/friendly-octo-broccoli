package com.peakform.fitness.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.FaIcon

/**
 * QuickActionsSheet — Batch 6 quick actions sheet.
 *
 * Slides up from the bottom dock when the user taps the "more" tab in the
 * glass dock OR long-presses the brand. Six quick action rows:
 *   Start workout · Library · History · Progress · 1RM Audit · Settings
 */
@Composable
fun QuickActionsSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val c = LocalProColors.current
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(c.bg.copy(alpha = 0.55f))
                .clickable(onClick = onDismiss)
        )
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(26.dp, 26.dp, 0.dp, 0.dp))
                    .background(c.glass2)
                    .border(1.dp, c.hairline2, RoundedCornerShape(26.dp, 26.dp, 0.dp, 0.dp))
                    .pointerInput(Unit) { detectTapGestures { } }
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 26.dp),
            ) {
                Box(
                    Modifier.width(44.dp).height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(c.hairline2)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))
                Text("Quick actions", style = ProType.cardTitle, color = c.text)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickActionRow("fa-bolt", "Start workout") { onSelect("workout") }
                    QuickActionRow("fa-book-open", "Library") { onSelect("library") }
                    QuickActionRow("fa-clock-rotate-left", "History") { onSelect("history") }
                    QuickActionRow("fa-chart-line", "Progress") { onSelect("progress") }
                    QuickActionRow("fa-trophy", "1RM Audit") { onSelect("1rm_audit") }
                    QuickActionRow("fa-gear", "Settings") { onSelect("settings") }
                }
            }
        }
    }
}

@Composable
private fun QuickActionRow(icon: String, label: String, onClick: () -> Unit) {
    val c = LocalProColors.current
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface)
            .border(1.dp, c.hairline, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FaIcon(icon, size = 16.sp, tint = c.accent)
        Spacer(Modifier.width(12.dp))
        Text(label, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
        FaIcon("fa-chevron-right", size = 11.sp, tint = c.text3)
    }
}
