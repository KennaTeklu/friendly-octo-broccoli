package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * OneRMAuditScreen — BATCH-14/15.
 *
 * Lists every exercise the active profile has logged at least one set of,
 * with the tested / estimated 1RM, when it was last calibrated, and a
 * "stale" flag if the estimate is older than 6 weeks.
 *
 * Each row offers:
 *  - Mark as 1RM-tested (sets tested1RM = mu, testDate = today)
 *  - Reset the estimate (sets mu = 0)
 *
 * Top-of-screen tiles summarize the count of stale estimates + total volume
 * calibrated across the library.
 *
 * The audit takes no destructive action without user confirmation — every
 * write goes through ProState.saveWorkoutData() so the change lands on the
 * active profile's Room namespace.
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
    val _tick = ver.intValue

    val rows = remember(_tick) {
        ProState.data.exercises.entries
            .mapNotNull { (id, rec) ->
                val lib = Library.augmented(Library.findExercise(id) ?: return@mapNotNull null)
                AuditRow(
                    id = id,
                    name = lib.name,
                    tested1RM = rec.tested1RM ?: 0.0,
                    estimated1RM = rec.mu ?: 0.0,
                    lastCalibrated = rec.lastUpdate ?: rec.testDate,
                    testDate = rec.testDate,
                    isStale = run {
                        val ts = rec.lastUpdate ?: rec.testDate
                        if (ts == null) false
                        else (System.currentTimeMillis() - ProState.utcDayMillis(ts)) > 6L * 7 * 24 * 3600 * 1000
                    },
                )
            }
            .sortedByDescending { it.estimated1RM + it.tested1RM }
    }
    val staleCount = rows.count { it.isStale }
    val testedCount = rows.count { it.tested1RM > 0 }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(key = "hdr", contentType = "header") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaIcon("fa-magnifying-glass-chart", size = 18.sp, tint = c.accent)
                    Spacer(Modifier.width(10.dp))
                    Text("1RM Audit", style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
                    FaIcon("fa-xmark", size = 16.sp, tint = c.text3, modifier = Modifier.clickable { onClose() })
                }
            }
            item(key = "summary", contentType = "summary") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) { AuditTile("Tracked lifts", "${rows.size}", c.accent) }
                    Box(Modifier.weight(1f)) { AuditTile("1RM-tested", "$testedCount", c.ok) }
                    Box(Modifier.weight(1f)) { AuditTile("Stale (>6w)", "$staleCount", if (staleCount > 0) c.warn else c.text3) }
                }
            }
            item(key = "intro", contentType = "intro") {
                Text(
                    "Each row shows your calibrated (tested) and estimated (modeled) 1-rep max. Tap a row to mark it as tested today, or reset the estimate.",
                    style = ProType.small, color = c.text3,
                )
            }
            items(items = rows, key = { it.id }, contentType = { "row" }) { row ->
                AuditCard(row, onMarkTested = {
                    val cur = ProState.data.exercises[row.id] ?: return@AuditCard
                    val testedVal = if (row.estimated1RM > 0) row.estimated1RM else row.tested1RM
                    if (testedVal > 0) {
                        ProState.data = ProState.data.copy(
                            exercises = ProState.data.exercises.toMutableMap().apply {
                                this[row.id] = cur.copy(tested1RM = testedVal, testDate = ProState.todayLocal())
                            }
                        )
                        ProState.saveWorkoutData()
                        ProState.notifyChanged()
                    }
                }, onReset = {
                    val cur = ProState.data.exercises[row.id] ?: return@AuditCard
                    ProState.data = ProState.data.copy(
                        exercises = ProState.data.exercises.toMutableMap().apply {
                            this[row.id] = cur.copy(mu = 0.0, sigma2 = 1.0, lastUpdate = null, tested1RM = null, testDate = null)
                        }
                    )
                    ProState.saveWorkoutData()
                    ProState.notifyChanged()
                })
            }
            if (rows.isEmpty()) {
                item(key = "empty", contentType = "empty") {
                    EmptyState("fa-dumbbell", "No tracked lifts yet", "Log a few sets in a workout, then come back to audit your 1RM estimates.")
                }
            }
            item(key = "spacer", contentType = "spacer") { Spacer(Modifier.height(120.dp)) }
        }
    }
}

private data class AuditRow(
    val id: String,
    val name: String,
    val tested1RM: Double,
    val estimated1RM: Double,
    val lastCalibrated: String?,
    val testDate: String?,
    val isStale: Boolean,
)

@Composable
private fun BoxScope.AuditTile(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    val c = LocalProColors.current
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface)
            .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(value, style = ProType.statValue, color = color, fontSize = 22.sp)
        Text(label, style = ProType.small, color = c.text3)
    }
}

@Composable
private fun AuditCard(row: AuditRow, onMarkTested: () -> Unit, onReset: () -> Unit) {
    val c = LocalProColors.current
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.surface)
            .border(1.dp, if (row.isStale) c.warn.copy(alpha = 0.4f) else c.hairline, RoundedCornerShape(14.dp))
            .padding(13.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(row.name, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
            if (row.isStale) {
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.warn.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("stale", fontSize = 10.sp, color = c.warn, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Tested 1RM", style = ProType.small, color = c.text3)
                Text(if (row.tested1RM > 0) "${row.tested1RM.toInt()} lbs" else "—", style = ProType.body2, color = c.ok)
            }
            Column(Modifier.weight(1f)) {
                Text("Est. 1RM", style = ProType.small, color = c.text3)
                Text(if (row.estimated1RM > 0) "${row.estimated1RM.toInt()} lbs" else "—", style = ProType.body2, color = c.accent)
            }
            Column(Modifier.weight(1f)) {
                Text("Last calibrated", style = ProType.small, color = c.text3)
                Text(row.lastCalibrated?.take(10) ?: "—", style = ProType.small, color = c.text2)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button("Mark tested today", icon = "fa-check", style = BtnStyle.SUCCESS, minHeight = 32, modifier = Modifier.weight(1f)) { onMarkTested() }
            P4Button("Reset estimate", icon = "fa-arrow-rotate-left", style = BtnStyle.SECONDARY, minHeight = 32, modifier = Modifier.weight(1f)) { onReset() }
        }
    }
}
