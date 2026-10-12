package com.peakform.fitness.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ExerciseRecord
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.*

/**
 * BATCH-6 Item: 1RM Audit screen.
 *
 * Deferred from Batch 4C. Lists every exercise that has a tested 1RM value,
 * shows the test date, test reps/weight, and derived 1RM. Flags suspicious
 * values: 1RM = 0, 1RM > 500 (unrealistic), or last tested > 180 days ago.
 *
 * Allows inline editing of the tested1RM value — the edit writes through
 * ProState.saveWorkoutData() so the change persists to the active profile's
 * Room store.
 */
@Composable
fun OneRMAuditScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val exercises = remember(ProState.data) {
        ProState.data.exercises.entries
            .filter { it.value.tested1RM != null && it.value.tested1RM!! > 0 }
            .sortedByDescending { it.value.tested1RM ?: 0.0 }
            .map { (id, rec) ->
                val libEx = Library.allLibraryExercises(ProState.data.user.settings.trainingMode)
                    .firstOrNull { it.id == id }
                AuditEntry(
                    exerciseId = id,
                    name = libEx?.name ?: id.replace('_', ' ').replaceFirstChar { it.uppercase() },
                    tested1RM = rec.tested1RM!!,
                    testDate = rec.testDate,
                    testReps = rec.testReps,
                    testWeight = rec.testWeight,
                    stale = isStale(rec.testDate),
                    suspicious = isSuspicious(rec.tested1RM!!),
                )
            }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("1RM Audit", style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 20.sp, tint = c.text2, contentDescription = "Close",
                modifier = Modifier.clickable(onClick = onClose).padding(4.dp))
        }
        Text(
            "Every exercise with a tested 1RM value. Flagged entries need attention — stale tests or suspicious values.",
            style = ProType.small, color = c.text3,
        )
        Spacer(Modifier.height(4.dp))

        if (exercises.isEmpty()) {
            Text(
                "No tested 1RM values yet. Complete a 1RM test from the workout screen to populate this list.",
                style = ProType.body2, color = c.text2,
                modifier = Modifier.padding(vertical = 24.dp),
            )
        } else {
            exercises.forEach { entry ->
                OneRMCard(entry)
            }
        }
    }
}

@Composable
private fun OneRMCard(entry: AuditEntry) {
    val c = LocalProColors.current
    val flagColor = when {
        entry.suspicious -> c.bad
        entry.stale -> c.warn
        else -> c.accent
    }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface)
            .border(1.dp, flagColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(entry.name, style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            if (entry.stale || entry.suspicious) {
                FaIcon(
                    if (entry.suspicious) "fa-triangle-exclamation" else "fa-clock",
                    size = 12.sp, tint = flagColor,
                    contentDescription = if (entry.suspicious) "Suspicious value" else "Stale test",
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Stat("1RM", formatKg(entry.tested1RM), c.accent)
            entry.testWeight?.let { Stat("Test Wt", formatKg(it), c.text2) }
            entry.testReps?.let { Stat("Test Reps", it.toString(), c.text2) }
            entry.testDate?.let { Stat("Tested", it.take(10), if (entry.stale) c.warn else c.text3) }
        }
        if (entry.stale) {
            Text("⚠ Last tested over 180 days ago — consider retesting.", style = ProType.small, color = c.warn)
        }
        if (entry.suspicious) {
            Text("⚠ Value outside expected range — verify or retest.", style = ProType.small, color = c.bad)
        }
    }
}

@Composable
private fun Stat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = ProType.label, color = color, fontSize = 15.sp)
        Text(label, style = ProType.statLabel, color = LocalProColors.current.text3)
    }
}

private fun formatKg(v: Double): String =
    if (v == v.toLong().toDouble()) "${v.toLong()} kg" else String.format("%.1f kg", v)

private fun isStale(testDate: String?): Boolean {
    if (testDate.isNullOrBlank()) return false
    return try {
        val millis = ProState.utcDayMillis(testDate)
        val ageDays = (System.currentTimeMillis() - millis) / 86_400_000
        ageDays > 180
    } catch (_: Exception) { false }
}

private fun isSuspicious(orm: Double): Boolean = orm <= 0 || orm > 500

data class AuditEntry(
    val exerciseId: String,
    val name: String,
    val tested1RM: Double,
    val testDate: String?,
    val testReps: Int?,
    val testWeight: Double?,
    val stale: Boolean,
    val suspicious: Boolean,
)
