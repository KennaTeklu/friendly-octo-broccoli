package com.peakform.fitness.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.*

/**
 * BATCH-14: Closes 5 reference gaps.
 *
 * Gap 1: Longevity breakdown panel inside the longevity card
 *        (reference/app/17-longevity.js lines 252-276)
 * Gap 2: Recommendation cards on dashboard (reference/app/18-recommendations.js)
 * Gap 3: Cycle-phase messaging (reference/app/09-period-dashboard.js)
 * Gap 4: Snapshot vault restore workflow (reference/app/13-persistence-coach.js)
 * Gap 5: Command palette integration on all screens (reference/app/10-init-nav.js)
 */

/** Gap 1: Longevity breakdown panel — shows sub-scores inside the longevity card. */
@Composable
fun LongevityBreakdownPanel(longevity: Stats.LongevityScore) {
    val c = LocalProColors.current
    val sub = longevity.sub
    val items = listOf(
        "Strength" to sub["strength"],
        "Mobility" to sub["mobility"],
        "Balance" to sub["balance"],
        "Posture" to sub["posture"],
        "Consistency" to sub["consistency"],
    )
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("Breakdown", style = ProType.label, color = c.text2)
        items.forEach { (label, score) ->
            if (score != null) {
                val scoreColor = when {
                    score >= 80 -> c.ok
                    score >= 60 -> c.warn
                    else -> c.bad
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(label, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                    Text("${score.toInt()}", style = ProType.label, color = scoreColor)
                }
                LinearProgressIndicator(
                    progress = { (score / 100f).toFloat() },
                    color = scoreColor,
                    trackColor = c.surface3,
                    modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                )
            }
        }
    }
}

/** Gap 2: Recommendation cards on dashboard. */
@Composable
fun RecommendationCards() {
    val c = LocalProColors.current
    val recommendations = remember { Stats.generateLongevityRecommendations() }
    if (recommendations.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        recommendations.take(3).forEach { rec ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.tint)
                    .border(1.dp, c.accentLine, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FaIcon("fa-lightbulb", size = 14.sp, tint = c.accent, contentDescription = "Recommendation")
                Text(rec, style = ProType.body2, color = c.inkSoft)
            }
        }
    }
}

/** Gap 3: Cycle-phase messaging — shows a coach message based on the goal cycle phase. */
@Composable
fun CyclePhaseMessage() {
    val c = LocalProColors.current
    val gc = ProState.data.user.goalCycle ?: return
    val phaseMessage = when {
        gc.cycleNumber <= 1 -> "Base building phase — focus on form and consistency."
        gc.cycleNumber <= 3 -> "Progressive phase — add load gradually."
        gc.cycleNumber <= 5 -> "Peak phase — push your limits safely."
        else -> "Maintenance phase — keep what you've built."
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.accentSoft)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FaIcon("fa-flag-checkered", size = 14.sp, tint = c.accent, contentDescription = "Cycle phase")
        Text(phaseMessage, style = ProType.body2, color = c.accent)
    }
}

/** Gap 4: Snapshot vault restore — shows available snapshots and restores on tap. */
@Composable
fun SnapshotVaultRestore(
    snapshots: List<String>,
    onRestore: (String) -> Unit,
) {
    val c = LocalProColors.current
    if (snapshots.isEmpty()) {
        Text("No snapshots saved. Snapshots are taken automatically before imports and resets.",
            style = ProType.small, color = c.text3)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Available snapshots", style = ProType.label, color = c.text2)
        snapshots.forEach { date ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(c.surface2)
                    .clickable { onRestore(date) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FaIcon("fa-clock-rotate-left", size = 14.sp, tint = c.accent, contentDescription = "Restore snapshot")
                Text(date, style = ProType.body2, color = c.text, modifier = Modifier.weight(1f))
                Text("Restore", style = ProType.label, color = c.accent)
            }
        }
    }
}

/** Gap 5: Command palette quick-access — a small bar showing the Ctrl+K hint on every screen. */
@Composable
fun CommandPaletteHintBar(onOpen: () -> Unit) {
    val c = LocalProColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(c.surface2)
            .clickable { onOpen() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FaIcon("fa-terminal", size = 12.sp, tint = c.text3, contentDescription = "Command palette")
        Text("Quick actions", style = ProType.small, color = c.text3, modifier = Modifier.weight(1f))
        Text("⌘K", style = ProType.small, color = c.text3)
    }
}
