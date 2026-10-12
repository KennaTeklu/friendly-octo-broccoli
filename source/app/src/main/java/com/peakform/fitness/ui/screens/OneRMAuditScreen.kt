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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * OneRMAuditScreen — BATCH-15.
 *
 * Surfaces a per-exercise audit of every 1RM the active profile has on file:
 *   - Tested 1RM (from a real 1RM test session)
 *   - Estimated 1RM (μ from the ERF model)
 *   - Last performed weight × reps
 *   - Staleness: weeks since the last test (re-test nudge ≥ 6 weeks)
 *   - Confidence (σ² → low / medium / high)
 *
 * Plus two action buttons:
 *   - "Reset 1RM cache" — clears every exercise's tested1RM / mu / sigma2 so
 *     the engine starts fresh. History is preserved.
 *   - "Open 1RM Lab" — deep-link to the OneRmLabScreen for formula experiments.
 */
@Composable
fun OneRMAuditScreen(
    onClose: () -> Unit,
    onOpenSection: (String) -> Unit,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }
    var toast by remember { mutableStateOf<String?>(null) }
    var resetConfirm by remember { mutableStateOf(false) }

    val exercises = remember(ver.intValue) {
        ProState.data.exercises.entries
            .filter { it.value.tested1RM != null || it.value.mu != null || it.value.history.isNotEmpty() }
            .sortedByDescending { it.value.tested1RM ?: it.value.mu ?: 0.0 }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-magnifying-glass-chart", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            SectionTitle("fa-magnifying-glass-chart", "1RM Audit")
        }
        Text(
            "Every 1RM on file for this profile — tested, estimated, last performed, and how stale each one is.",
            style = ProType.small, color = c.text3,
        )

        // summary tile
        GlassCard(padding = PaddingValues(14.dp)) {
            val testedCount = exercises.count { it.value.tested1RM != null }
            val estCount = exercises.count { it.value.mu != null && it.value.tested1RM == null }
            val staleCount = exercises.count { OneRmLab.weeksSinceTest(it.value.testDate) >= 6 }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("$testedCount", "Tested", modifier = Modifier.weight(1f))
                StatCard("$estCount", "Estimated", modifier = Modifier.weight(1f))
                StatCard("$staleCount", "Stale ≥6w", modifier = Modifier.weight(1f))
            }
        }

        // exercise audit rows
        if (exercises.isEmpty()) {
            EmptyState("fa-dumbbell", "No 1RM data yet", "Generate a workout and log a session — your 1RMs will appear here.")
        } else {
            exercises.forEach { (id, rec) ->
                val name = Library.getExerciseById(id)?.name ?: id
                val tested = rec.tested1RM
                val est = rec.mu
                val lastEntry = rec.history.lastOrNull { !it.skipped }
                val weeks = OneRmLab.weeksSinceTest(rec.testDate)
                val confidence = OneRmLab.confidenceLabel(rec.sigma2)
                val stale = weeks >= 6
                GlassCard(padding = PaddingValues(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(name, style = ProType.label, color = c.text)
                            if (tested != null) {
                                Text("🏆 Tested: ${tested.toInt()} lbs", style = ProType.small, color = c.ok)
                            }
                            if (est != null && tested == null) {
                                Text("Est. 1RM: ${est.toInt()} lbs", style = ProType.small, color = c.accent)
                            }
                            if (lastEntry != null) {
                                Text("Last: ${lastEntry.weight.toInt()} × ${lastEntry.sets}", style = ProType.small, color = c.text3)
                            }
                            Text("Confidence: $confidence", style = ProType.small, color = c.text3)
                        }
                        if (stale) {
                            Box(
                                Modifier.clip(RoundedCornerShape(999.dp))
                                    .background(c.warn.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text("Re-test ($weeks w)", fontSize = 10.sp, color = c.warn, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // action buttons
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button("Open 1RM Lab", icon = "fa-flask", style = BtnStyle.SECONDARY, modifier = Modifier.weight(1f)) {
                onOpenSection("1rm_lab")
            }
            P4Button(
                if (resetConfirm) "Confirm reset?" else "Reset 1RM cache",
                icon = "fa-rotate-left",
                style = BtnStyle.DANGER,
                modifier = Modifier.weight(1f),
            ) {
                if (!resetConfirm) {
                    resetConfirm = true
                } else {
                    OneRmLab.resetCache(ctx)
                    toast = "1RM cache cleared — history preserved"
                    resetConfirm = false
                    ProState.notifyChanged()
                }
            }
        }
        P4Button("Back", icon = "fa-arrow-left", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) {
            onClose()
        }

        toast?.let { t ->
            LaunchedEffect(t) { kotlinx.coroutines.delay(2400); toast = null }
            ToastBanner(t)
        }
        Spacer(Modifier.height(120.dp))
    }
}
