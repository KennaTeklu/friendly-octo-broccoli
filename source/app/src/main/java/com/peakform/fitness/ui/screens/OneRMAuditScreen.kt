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
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * OneRMAuditScreen — BATCH-8 audit view for the 1RM calibration state.
 *
 * Lists every exercise record with a tested1RM, est.1RM (mu), and the test
 * age (weeks since testDate). The user can re-test or clear a stale 1RM.
 *
 * Opened from Settings → Library management → 1RM Audit and from the
 * Workout screen's "1RM REQUIRED" hint.
 */
@Composable
fun OneRMAuditScreen(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }
    val _tick = ver.intValue

    val rows = remember(ver.intValue) {
        ProState.data.exercises.entries
            .filter { it.value.tested1RM != null || it.value.mu != null }
            .map { (id, rec) ->
                AuditRow(
                    id = id,
                    name = id.replace('_', ' ').replaceFirstChar { it.uppercase() },
                    tested = rec.tested1RM,
                    est = rec.mu,
                    testDate = rec.testDate,
                    weeksOld = rec.testDate?.let { d ->
                        ((System.currentTimeMillis() - ProState.utcDayMillis(d)) / (7L * 86400000)).toInt()
                    } ?: -1,
                )
            }
            .sortedByDescending { it.weeksOld }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-magnifying-glass-chart", "1RM Audit")
        Text(
            "Every calibrated lift and Est. 1RM on file. Lifts older than 6 weeks are flagged for re-test.",
            style = ProType.small, color = c.text3,
        )
        if (rows.isEmpty()) {
            EmptyState(
                icon = "fa-gauge-high",
                title = "No 1RM records yet",
                message = "Run a 1RM test on a compound lift (Squat, Bench, Deadlift, Press, Row) to populate the audit.",
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
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AuditStat("Tested 1RM", if (row.tested != null) "${row.tested.toInt()} lbs" else "—", c.ok)
                    AuditStat("Est. 1RM", if (row.est != null) "${row.est.toInt()} lbs" else "—", c.accent)
                    AuditStat("Last test", if (row.weeksOld >= 0) "${row.weeksOld}w ago" else "—", c.text3)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    P4Button("Open Lab", icon = "fa-flask", style = BtnStyle.INFO, modifier = Modifier.weight(1f), minHeight = 36) {
                        onOpenSection("onerm_lab")
                    }
                    P4Button("Clear", icon = "fa-eraser", style = BtnStyle.GHOST, modifier = Modifier.weight(1f), minHeight = 36) {
                        val rec = ProState.data.exercises[row.id] ?: return@P4Button
                        val cleared = rec.copy(tested1RM = null, testDate = null)
                        ProState.data = ProState.data.copy(exercises = ProState.data.exercises + (row.id to cleared))
                        ProState.saveWorkoutData()
                        ProState.notifyChanged()
                    }
                }
            }
        }
        Spacer(Modifier.height(120.dp))
    }
}

private data class AuditRow(
    val id: String, val name: String,
    val tested: Double?, val est: Double?,
    val testDate: String?, val weeksOld: Int,
)

@Composable
private fun AuditStat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    val c = LocalProColors.current
    Box(Modifier.clip(RoundedCornerShape(10.dp)).background(c.surface2).padding(horizontal = 10.dp, vertical = 6.dp)) {
        Column {
            Text(label, fontSize = 9.sp, color = c.text3)
            Text(value, style = ProType.label, color = color)
        }
    }
}
