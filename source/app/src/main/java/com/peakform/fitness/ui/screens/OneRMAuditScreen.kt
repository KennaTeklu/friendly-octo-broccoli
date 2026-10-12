package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button

/**
 * OneRMAuditScreen — 5A+5B+6+7: lists every exercise whose tested1RM is non-null
 * and > 0, showing the tested 1RM, test date, reps and weight. Stale (>180 days)
 * and suspicious (<=0 or >500 lbs) tests are flagged so the user can re-test or
 * correct bad data without hunting through the Library.
 *
 * Routes from Settings → Library management → 1RM Audit.
 */
@Composable
fun OneRMAuditScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose {
            synchronized(ProState.listeners) { ProState.listeners.remove(l) }
        }
    }
    val _tick = ver.intValue

    // Collect every exercise record with a non-null, >0 tested1RM.
    val rows = remember(_tick) {
        ProState.data.exercises.entries.mapNotNull { (id, rec) ->
            val tested = rec.tested1RM ?: return@mapNotNull null
            if (tested <= 0) return@mapNotNull null
            val libEx = Library.getExerciseById(id, includeBodyweight = true)
            AuditRow(
                id = id,
                name = libEx?.name ?: id.replace('_', ' '),
                tested1RM = tested,
                testDate = rec.testDate,
                testReps = rec.testReps,
                testWeight = rec.testWeight,
            )
        }.sortedByDescending { it.tested1RM }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("1RM Audit", style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Close", icon = "fa-xmark", style = com.peakform.fitness.ui.components.BtnStyle.GHOST, minHeight = 36, onClick = onClose)
        }
        Text("${rows.size} tested 1RM records", style = ProType.small, color = c.text3)

        if (rows.isEmpty()) {
            Text(
                "No tested 1RM records yet — open an exercise in the Library and tap “Test 1RM” to record one.",
                style = ProType.body2, color = c.text2,
            )
        }

        rows.forEach { row ->
            val ageDays = row.testDate?.let { dateStr ->
                try {
                    val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    val parsed = fmt.parse(dateStr.take(10))
                    if (parsed != null) ((System.currentTimeMillis() - parsed.time) / (24L * 3600 * 1000)).toInt() else null
                } catch (_: Exception) { null }
            }
            val stale = ageDays != null && ageDays > 180
            val suspicious = row.tested1RM <= 0 || row.tested1RM > 500
            val flagTint = when {
                suspicious -> c.bad
                stale -> c.warn
                else -> c.ok
            }
            val flagText = when {
                suspicious -> "Suspicious"
                stale -> "Stale"
                else -> "Current"
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(c.surface2)
                    .border(1.dp, c.hairline, RoundedCornerShape(14.dp))
                    .padding(13.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(row.name, style = ProType.label, color = c.text, modifier = Modifier.weight(1f), fontSize = 14.sp)
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(flagTint.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(flagText, fontSize = 10.sp, color = flagTint, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text("🏆 Tested 1RM: ${row.tested1RM.toInt()} lbs", style = ProType.body2, color = c.text)
                if (row.testDate != null) {
                    Text("Tested: ${row.testDate.take(10)}${ageDays?.let { " ($it days ago)" } ?: ""}", style = ProType.small, color = c.text3)
                }
                if (row.testReps != null || row.testWeight != null) {
                    Text(
                        "Reps: ${row.testReps ?: "—"} · Weight: ${row.testWeight?.toInt() ?: "—"} lbs",
                        style = ProType.small, color = c.text3,
                    )
                }
                if (suspicious) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Suspicious value — re-test or clear it from the Library → exercise detail.",
                        style = ProType.small, color = c.bad,
                    )
                } else if (stale) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Stale — consider a re-test so the engine keeps prescribing accurately.",
                        style = ProType.small, color = c.warn,
                    )
                }
            }
        }
        Spacer(Modifier.height(120.dp))
    }
}

private data class AuditRow(
    val id: String,
    val name: String,
    val tested1RM: Double,
    val testDate: String?,
    val testReps: Int?,
    val testWeight: Double?,
)
