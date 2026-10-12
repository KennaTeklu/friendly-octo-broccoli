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
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * OneRmLabScreen — BATCH-12 1RM experimentation view.
 *
 * Lists every record with a tested1RM or est.1RM (mu), shows the residual
 * spread as a confidence indicator, and exposes the batch actions
 * (clear stale, decay, re-estimate missing) so the user can prune the
 * calibration model without a manual export + edit + import cycle.
 *
 * Read-only by default — every batch action opens a confirmation dialog
 * before mutating ProState.data.exercises.
 */
@Composable
fun OneRmLabScreen(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }
    val _tick = ver.intValue

    val rows = remember(ver.intValue) { OneRmLab.audit(ctx) }
    var confirmClearStale by remember { mutableStateOf(false) }
    var confirmDecay by remember { mutableStateOf(false) }
    var confirmReEstimate by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-flask", "1RM Lab")
        Text(
            "Experiment with the 1RM model — clear stale tests, apply a global decay, or seed missing estimates. " +
                "Changes affect the active profile only.",
            style = ProType.small, color = c.text3,
        )

        // ---- batch actions ----
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button("Clear stale (>6w)", icon = "fa-eraser", style = BtnStyle.DANGER, modifier = Modifier.weight(1f), minHeight = 40) {
                confirmClearStale = true
            }
            P4Button("Decay ×0.95", icon = "fa-chart-line", style = BtnStyle.SECONDARY, modifier = Modifier.weight(1f), minHeight = 40) {
                confirmDecay = true
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button("Re-estimate missing", icon = "fa-wand-magic-sparkles", style = BtnStyle.INFO, modifier = Modifier.weight(1f), minHeight = 40) {
                confirmReEstimate = true
            }
            P4Button("Open Audit", icon = "fa-magnifying-glass-chart", style = BtnStyle.GHOST, modifier = Modifier.weight(1f), minHeight = 40) {
                onOpenSection("onerm_audit")
            }
        }

        if (rows.isEmpty()) {
            EmptyState(
                icon = "fa-flask",
                title = "No 1RM records yet",
                message = "Log a few sessions — the Lab populates automatically as the engine builds a 1RM model.",
            )
        }

        rows.forEach { row ->
            val stale = row.weeksOld >= 6
            val borderColor = when {
                stale -> c.warn
                row.tested != null -> c.ok
                else -> c.accent
            }
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.surface)
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp)).padding(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(row.name, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
                    if (stale) {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.warn.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("STALE ${row.weeksOld}w", color = c.warn, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (row.tested != null) {
                        Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.ok.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("TESTED", color = c.ok, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LabStat("Tested", if (row.tested != null) "${row.tested.toInt()} lbs" else "—", c.ok)
                    LabStat("Est.", if (row.est != null) "${row.est.toInt()} lbs" else "—", c.accent)
                    LabStat("Spread", String.format("%.0f", row.residualSpread), c.text3)
                }
            }
        }
        Spacer(Modifier.height(120.dp))
    }

    // ---- confirmations ----
    if (confirmClearStale) {
        SwalDialog(
            title = "Clear stale 1RM records?",
            text = "This drops every tested1RM older than 6 weeks. Est. 1RM (mu) values stay intact. Continue?",
            confirmText = "Clear stale", cancelText = "Cancel",
            onConfirm = {
                confirmClearStale = false
                val n = OneRmLab.clearStale(ctx, 6)
                toast = if (n > 0) "Cleared $n stale records" else "No stale records found"
                ver.intValue++
            },
            onDismiss = { confirmClearStale = false },
        )
    }
    if (confirmDecay) {
        SwalDialog(
            title = "Apply global decay ×0.95?",
            text = "Multiplies every Est. 1RM (mu) by 0.95. Use after a deload block where 1RMs are temporarily depressed.",
            confirmText = "Apply decay", cancelText = "Cancel",
            onConfirm = {
                confirmDecay = false
                val n = OneRmLab.decayAll(ctx, 0.95)
                toast = if (n > 0) "Decayed $n records ×0.95" else "No est. 1RM to decay"
                ver.intValue++
            },
            onDismiss = { confirmDecay = false },
        )
    }
    if (confirmReEstimate) {
        SwalDialog(
            title = "Re-estimate missing?",
            text = "Seeds an Est. 1RM for every exercise with a logged best-weight but no mu value (legacy gap).",
            confirmText = "Re-estimate", cancelText = "Cancel",
            onConfirm = {
                confirmReEstimate = false
                val n = OneRmLab.reEstimateMissing(ctx)
                toast = if (n > 0) "Seeded $n missing estimates" else "No missing estimates"
                ver.intValue++
            },
            onDismiss = { confirmReEstimate = false },
        )
    }
    toast?.let { msg ->
        LaunchedEffect(msg) { kotlinx.coroutines.delay(2400); toast = null }
        ToastBanner(msg)
    }
}

@Composable
private fun LabStat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    val c = LocalProColors.current
    Box(Modifier.clip(RoundedCornerShape(10.dp)).background(c.surface2).padding(horizontal = 10.dp, vertical = 6.dp)) {
        Column {
            Text(label, fontSize = 9.sp, color = c.text3)
            Text(value, style = ProType.label, color = color)
        }
    }
}
