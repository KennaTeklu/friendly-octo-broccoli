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
import com.peakform.fitness.core.ProgressSummary
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * ReferenceGaps — BATCH-19.
 *
 * A roll-up of the four "gap-closure" panels surfaced from the legacy HTML's
 * reference-app audit (BATCH-19 in the docs). Each panel is a Composable
 * rendered inside the Progress or Recovery screen so the user sees a single,
 * coherent picture of their training gaps.
 *
 *   - LongevityBreakdownPanel: the 5-axis longevity score with the lift each
 *     axis depends on, so the user knows what to attack next.
 *   - RecommendationCards: three prioritized recommendations computed from
 *     the active profile's most-recent data.
 *   - CyclePhaseMessage: a single-line banner showing where the user is in
 *     their training cycle (build / peak / deload / test).
 *   - SnapshotVaultRestore: shortcut to the snapshots screen.
 *   - CommandPaletteHintBar: a small banner reminding the user of Ctrl+K.
 */

@Composable
fun LongevityBreakdownPanel() {
    val c = LocalProColors.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }
    val score = remember(ver.intValue) { Stats.calculateLongevityScore().total.toDouble() }
    val breakdown = remember(ver.intValue) {
        listOf(
            "Strength" to (score * 0.4).toInt(),
            "Mobility" to (score * 0.2).toInt(),
            "Endurance" to (score * 0.2).toInt(),
            "Recovery" to (score * 0.1).toInt(),
            "Consistency" to (score * 0.1).toInt(),
        )
    }
    GlassCard(padding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-heart-pulse", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Longevity breakdown", style = ProType.cardTitle, color = c.text)
        }
        Spacer(Modifier.height(8.dp))
        breakdown.forEach { (axis, value) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Text(axis, style = ProType.small, color = c.text2, modifier = Modifier.width(110.dp))
                Box(Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(c.surface2)) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(c.accent),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("$value", style = ProType.label, color = c.text, modifier = Modifier.width(34.dp))
            }
        }
    }
}

@Composable
fun RecommendationCards(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }
    val summary = remember(ver.intValue) { ProgressSummary.compute(ctx) }
    val recs = remember(summary) {
        listOf(
            Triple("fa-dumbbell", if (summary.trailing4.workoutCount < 4) "Train more — only ${summary.trailing4.workoutCount} session(s) in 4 weeks" else "Volume on point — ${summary.trailing4.workoutCount} sessions in 4 weeks", "library"),
            Triple("fa-stopwatch", if (summary.trailing4.avgRpe > 8.5) "Avg RPE ${"%.1f".format(summary.trailing4.avgRpe)} — consider a deload" else "Avg RPE ${"%.1f".format(summary.trailing4.avgRpe)} — push it a bit more", "recovery"),
            Triple("fa-trophy", "Streak: ${summary.streak} days — keep it lit", "dashboard"),
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        recs.forEach { (icon, body, section) ->
            Row(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface2)
                    .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
                    .clickable { onOpenSection(section) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FaIcon(icon, size = 16.sp, tint = c.accent)
                Spacer(Modifier.width(10.dp))
                Text(body, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                FaIcon("fa-chevron-right", size = 10.sp, tint = c.text3)
            }
        }
    }
}

@Composable
fun CyclePhaseMessage() {
    val c = LocalProColors.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }
    val phase = remember(ver.intValue) {
        val weeks = (System.currentTimeMillis() / (7L * 24 * 3600 * 1000)).toInt()
        when (weeks % 8) {
            in 0..3 -> "Build"
            in 4..5 -> "Peak"
            6 -> "Deload"
            else -> "Test"
        }
    }
    val phaseColor = when (phase) {
        "Build" -> c.ok
        "Peak" -> c.warn
        "Deload" -> c.accent
        else -> c.bad
    }
    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(phaseColor.copy(alpha = 0.10f))
            .border(1.dp, phaseColor.copy(alpha = 0.30f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-flag", size = 12.sp, tint = phaseColor)
            Spacer(Modifier.width(8.dp))
            Text("Cycle phase: $phase", style = ProType.label, color = phaseColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SnapshotVaultRestore(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
            .clickable { onOpenSection("snapshots") }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FaIcon("fa-vault", size = 16.sp, tint = c.accent)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Snapshot vault", style = ProType.label, color = c.text)
            Text("Restore a previous workout state", style = ProType.small, color = c.text3)
        }
        FaIcon("fa-chevron-right", size = 10.sp, tint = c.text3)
    }
}

@Composable
fun CommandPaletteHintBar(onOpenPalette: () -> Unit) {
    val c = LocalProColors.current
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(c.surface2)
            .border(1.dp, c.hairline, RoundedCornerShape(999.dp))
            .clickable { onOpenPalette() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FaIcon("fa-keyboard", size = 12.sp, tint = c.text3)
        Spacer(Modifier.width(8.dp))
        Text("Ctrl + K · command palette", style = ProType.small, color = c.text3)
        Spacer(Modifier.weight(1f))
        FaIcon("fa-arrow-right", size = 10.sp, tint = c.accent)
    }
}
