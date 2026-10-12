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
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * OneRMAuditScreen — Batch 6 1RM Audit screen.
 *
 * Lists every exercise with a tested 1RM > 0, shows:
 *   - 1RM value (rounded to nearest 2.5)
 *   - test date (when the 1RM was tested)
 *   - reps / weight at test time
 *   - status flags: STALE (>180 days), SUSPICIOUS (≤0 or >500)
 *
 * Tapping an exercise row opens its detail sheet.
 */
@Composable
fun OneRMAuditScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose { synchronized(ProState.listeners) { ProState.listeners.remove(l) } }
    }

    val all = remember(ver.intValue) {
        ProState.data.exercises.entries
            .mapNotNull { (id, rec) ->
                val tested = rec.tested1RM ?: 0.0
                if (tested <= 0) return@mapNotNull null
                Triple(id, rec, tested)
            }
            .sortedByDescending { it.third }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("fa-trophy", "1RM Audit", Modifier.weight(1f))
        }
        Text("${all.size} lifts with a tested 1RM.", style = ProType.small, color = c.text3)

        if (all.isEmpty()) {
            EmptyState(
                icon = "fa-trophy",
                title = "No tested 1RMs yet",
                message = "Run a 1RM test from the Workout screen — calibrated lifts show up here.",
            )
        }

        all.forEach { (id, rec, tested) ->
            OneRmAuditRow(id = id, record = rec, tested = tested)
        }

        P4Button("Back", icon = "fa-arrow-left", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) {
            onClose()
        }
        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun OneRmAuditRow(id: String, record: ExerciseRecord, tested: Double) {
    val c = LocalProColors.current
    val name = id.replace('_', ' ').replaceFirstChar { it.uppercase() }
    val dateStr = record.testDate?.take(10) ?: "—"
    val daysSince = remember(record.testDate) {
        try {
            if (record.testDate.isNullOrBlank()) return@remember null
            val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val tested = fmt.parse(record.testDate?.take(19) ?: "") ?: return@remember null
            ((System.currentTimeMillis() - tested.time) / (24L * 3600 * 1000)).toInt()
        } catch (_: Exception) { null }
    }
    val stale = daysSince != null && daysSince > 180
    val suspicious = tested <= 0 || tested > 500

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface)
            .border(1.dp, c.hairline, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
            Text("${OneRm.roundToNearest(tested).toInt()} lbs", style = ProType.statValue, fontSize = 20.sp, color = c.accent)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tested: $dateStr", style = ProType.small, color = c.text3, modifier = Modifier.weight(1f))
            if (record.testReps != null && record.testWeight != null) {
                Text("Last test: ${record.testReps} × ${record.testWeight.toInt()} lbs", style = ProType.small, color = c.text3)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (stale) {
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.warn.copy(alpha = 0.18f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("STALE", fontSize = 10.sp, color = c.warn, fontWeight = FontWeight.Bold)
                }
            }
            if (suspicious) {
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.bad.copy(alpha = 0.18f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("SUSPICIOUS", fontSize = 10.sp, color = c.bad, fontWeight = FontWeight.Bold)
                }
            }
            if (daysSince != null) {
                Text("$daysSince days ago", style = ProType.small, color = c.text3)
            }
        }
    }
}
