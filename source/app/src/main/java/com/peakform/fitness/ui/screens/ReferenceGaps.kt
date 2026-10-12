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
import com.peakform.fitness.core.BackupSystem
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProgressSummary
import com.peakform.fitness.engine.Stats
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * ReferenceGaps — BATCH-14 coach-led reference panel.
 *
 * A single scrollable column collecting the panels surfaced by the coach-led
 * flow:
 *   - LongevityBreakdownPanel — 11-sub-score breakdown with bar fills.
 *   - RecommendationCards — coach recommendations (CoachingEngine output).
 *   - CyclePhaseMessage — menstrual cycle phase message (if applicable).
 *   - SnapshotVaultRestore — safety snapshot file list + one-tap restore.
 *   - CommandPaletteHintBar — the Ctrl/Cmd+K hint at the bottom.
 *
 * Each panel is its own @Composable so callers (Dashboard, Recovery) can host
 * a subset without taking the whole column.
 */
@Composable
fun ReferenceGapsScreen(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-compass", "Coach's Reference")
        LongevityBreakdownPanel()
        RecommendationCards(onOpenSection = onOpenSection)
        CyclePhaseMessage()
        SnapshotVaultRestore()
        CommandPaletteHintBar(onOpenSection = onOpenSection)
        Spacer(Modifier.height(120.dp))
    }
}

/** BATCH-14 — 11-sub-score breakdown. */
@Composable
fun LongevityBreakdownPanel() {
    val c = LocalProColors.current
    val longevity = remember { Stats.calculateLongevityScore() }
    val sub = longevity.sub.entries.sortedBy { it.key }
    GlassCard(padding = PaddingValues(16.dp)) {
        SectionTitle("fa-heart-pulse", "Longevity Breakdown", Modifier)
        Spacer(Modifier.height(8.dp))
        sub.forEach { (name, score) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(name.replace('_', ' ').replaceFirstChar { it.uppercase() }, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                Box(Modifier.width(80.dp)) { ProgressBar(score, c.accent, height = 6) }
                Spacer(Modifier.width(8.dp))
                Text("${score.toInt()}", style = ProType.small, color = c.text)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Total: ${longevity.total}/100 — ${longevity.status}", style = ProType.label, color = c.text)
    }
}

/** BATCH-14 — coach recommendations. */
@Composable
fun RecommendationCards(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val recs = remember { CoachingEngine.evaluate(ctx) }
    GlassCard(padding = PaddingValues(16.dp)) {
        SectionTitle("fa-user-md", "Today's Recommendations", Modifier)
        Spacer(Modifier.height(8.dp))
        if (recs.isEmpty()) {
            Text("No urgent actions — your training is on track.", style = ProType.small, color = c.text3)
        }
        recs.forEach { r ->
            val tint = when (r.urgency) { "bad" -> c.bad; "warn" -> c.warn; else -> c.accent }
            Row(
                Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.10f))
                    .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .clickable { onOpenSection(r.action) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                com.peakform.fitness.ui.FaIcon(r.icon, size = 14.sp, tint = tint)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(r.title, style = ProType.label, color = c.text)
                    Text(r.detail, style = ProType.small, color = c.text2)
                }
                com.peakform.fitness.ui.FaIcon("fa-chevron-right", size = 11.sp, tint = tint)
            }
        }
    }
}

/** BATCH-14 — cycle phase message (only renders for users with a logged cycle). */
@Composable
fun CyclePhaseMessage() {
    val c = LocalProColors.current
    val m = ProState.data.user.menstrual
    if (m.lastPeriodStart == null) return
    val startMs = ProState.utcDayMillis(m.lastPeriodStart!!)
    val dayInCycle = ((System.currentTimeMillis() - startMs) / (24 * 3600 * 1000L)).toInt() % m.cycleLength
    val phase = when (dayInCycle) {
        in 0..4 -> "Menstrual" to "Lower intensity is fine — your body is rebuilding."
        in 5..13 -> "Follicular" to "Great window to push PRs and heavy compounds."
        in 14..16 -> "Ovulation" to "Peak strength window — go for it."
        else -> "Luteal" to "Steady-state and technique work shine here."
    }
    GlassCard(padding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            com.peakform.fitness.ui.FaIcon("fa-clock-rotate-left", size = 14.sp, tint = c.accent)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("${phase.first} — day $dayInCycle", style = ProType.label, color = c.text)
                Text(phase.second, style = ProType.small, color = c.text2)
            }
        }
    }
}

/** BATCH-14 — safety snapshot vault list + one-tap restore. */
@Composable
fun SnapshotVaultRestore() {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val snaps = remember { BackupSystem.safetySnapshots(ctx) }
    var restoring by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }
    GlassCard(padding = PaddingValues(14.dp)) {
        SectionTitle("fa-vault", "Safety Snapshots", Modifier)
        Spacer(Modifier.height(6.dp))
        if (snaps.isEmpty()) {
            Text("No safety snapshots yet — they auto-write every 5 completed workouts.", style = ProType.small, color = c.text3)
        } else {
            snaps.take(5).forEach { f ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    com.peakform.fitness.ui.FaIcon("fa-file-medical", size = 12.sp, tint = c.accent)
                    Spacer(Modifier.width(8.dp))
                    Text(f.name, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                    Text("${f.length() / 1024} KB", style = ProType.small, color = c.text3)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (restoring) "…" else "Restore",
                        style = ProType.small, color = c.accent,
                        modifier = Modifier.clickable {
                            if (restoring) return@clickable
                            restoring = true
                            scope.launch {
                                val ok = withContext(Dispatchers.IO) { BackupSystem.restore(ctx, f) }
                                restoring = false
                                toast = if (ok) "Snapshot restored" else "Restore failed"
                            }
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        P4Button("Take snapshot now", icon = "fa-camera", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth(), minHeight = 38) {
            val path = BackupSystem.takeSafetySnapshot(ctx)
            toast = if (path != null) "Snapshot written" else "Snapshot failed"
        }
    }
    toast?.let { msg ->
        LaunchedEffect(msg) { kotlinx.coroutines.delay(2400); toast = null }
        ToastBanner(msg)
    }
}

/** BATCH-14 — command palette hint bar at the bottom of the column. */
@Composable
fun CommandPaletteHintBar(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .border(1.dp, c.hairline2, RoundedCornerShape(12.dp))
            .clickable { onOpenSection("commands") }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        com.peakform.fitness.ui.FaIcon("fa-keyboard", size = 14.sp, tint = c.accent)
        Spacer(Modifier.width(10.dp))
        Text("Press Ctrl/Cmd+K anywhere to open the command palette", style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
        com.peakform.fitness.ui.FaIcon("fa-angle-right", size = 12.sp, tint = c.text3)
    }
}
