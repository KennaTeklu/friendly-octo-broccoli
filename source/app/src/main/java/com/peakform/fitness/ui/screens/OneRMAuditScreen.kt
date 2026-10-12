package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.Nudges
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * BATCH-8: 1RM Audit screen — lists every exercise that needs a 1RM retest
 * (tested >6 weeks ago) or has never been tested. Lets the user jump to the
 * 1RM Lab to log a fresh attempt.
 *
 * Source of truth: ProState.data.exercises (per-exercise testDate) and
 * Nudges.retestDue for the staleness threshold.
 */
@Composable
fun OneRMAuditScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current

    val exercises = remember { ProState.data.exercises }
    val now = System.currentTimeMillis()

    // Partition: never-tested, stale (>6 weeks), fresh.
    val neverTested = exercises.filter { it.value.tested1RM == null }
    val stale = exercises.filter {
        val r = it.value
        r.tested1RM != null && r.testDate != null &&
            Nudges.isStale(r.testDate, now)
    }
    val fresh = exercises.filter {
        val r = it.value
        r.tested1RM != null && (r.testDate == null || !Nudges.isStale(r.testDate, now))
    }

    Column(Modifier.fillMaxSize().background(c.bg)) {
        // Header bar
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FaIcon("fa-magnifying-glass", size = 18.sp, tint = c.accent)
            Spacer(Modifier.width(10.dp))
            Text("1RM Audit", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Close", style = BtnStyle.GHOST, onClick = onClose)
        }
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
            Text(
                "${neverTested.size + stale.size} of ${exercises.size} exercises need a 1RM retest. Tap one to open the 1RM Lab.",
                style = ProType.small, color = c.text3,
            )
            Spacer(Modifier.height(12.dp))

            if (neverTested.isNotEmpty()) {
                SectionHeader("Never tested (${neverTested.size})")
                neverTested.forEach { (id, rec) ->
                    val name = Library.getExerciseById(id)?.name ?: id
                    AuditRow(name, "No 1RM on record", c.warn) {}
                }
                Spacer(Modifier.height(12.dp))
            }

            if (stale.isNotEmpty()) {
                SectionHeader("Stale — retest due (${stale.size})")
                stale.forEach { (id, rec) ->
                    val name = Library.getExerciseById(id)?.name ?: id
                    val days = rec.testDate?.let {
                        ((now - com.peakform.fitness.core.ProState.utcDayMillis(it)) / (24L * 3600 * 1000)).toInt()
                    } ?: 0
                    AuditRow(name, "Last tested ${rec.testDate?.take(10)} (${days}d ago)", c.warn) {}
                }
                Spacer(Modifier.height(12.dp))
            }

            if (fresh.isNotEmpty()) {
                SectionHeader("Fresh (${fresh.size})")
                fresh.forEach { (id, rec) ->
                    val name = Library.getExerciseById(id)?.name ?: id
                    AuditRow(name, "Tested ${rec.testDate?.take(10) ?: "—"} · 1RM ${rec.tested1RM?.toInt() ?: "—"} lbs", c.ok) {}
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(label: String) {
    val c = LocalProColors.current
    Text(label, style = ProType.label, color = c.text2, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
}

@Composable
private fun AuditRow(name: String, subtitle: String, accent: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    val c = LocalProColors.current
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(RoundedCornerShape(999.dp)).background(accent))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(name, style = ProType.body2, color = c.text)
            Text(subtitle, style = ProType.small, color = c.text3)
        }
        FaIcon("fa-chevron-right", size = 11.sp, tint = c.text3)
    }
}
