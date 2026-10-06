package com.peakform.fitness.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProJson
import com.peakform.fitness.core.WorkoutRecord
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * History — 1:1 port of #history-section (scr-history.md):
 * filter tabs (All/This Week/Month/Year/Longevity), card rows with date + name +
 * Edit/Delete, expandable detail (worked-on, per-exercise prescribed vs actual,
 * volume, RPE, auto-saving notes), CSV export + print via system share.
 */

enum class HistFilter(val label: String) { ALL("All"), WEEK("This Week"), MONTH("This Month"), YEAR("This Year"), LONGEVITY("Longevity") }

@Composable
fun HistoryScreen(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose {
            synchronized(ProState.listeners) { ProState.listeners.remove(l) }
        }
    }
    val _verTick = ver.intValue
    var filter by remember { mutableStateOf(HistFilter.ALL) }
    var expandedId by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<WorkoutRecord?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }
    // FIX (audit #38): notes were shown in CSV only — the detail view had no way to
    // read or edit the session notes the logger saves. Editable in place now.
    var noteTarget by remember { mutableStateOf<Pair<WorkoutRecord, Int>?>(null) }

    val workouts = remember(ver.intValue, filter) {
        val all = ProState.data.workouts.filter { it.dateCompleted != null }.sortedByDescending { ProState.utcDayMillis(it.date) }
        val now = System.currentTimeMillis()
        when (filter) {
            HistFilter.ALL -> all
            HistFilter.WEEK -> all.filter { now - ProState.utcDayMillis(it.date) <= 7L * 86400000 }
            HistFilter.MONTH -> all.filter { now - ProState.utcDayMillis(it.date) <= 31L * 86400000 }
            HistFilter.YEAR -> all.filter { now - ProState.utcDayMillis(it.date) <= 365L * 86400000 }
            HistFilter.LONGEVITY -> all.filter { w ->
                w.type == "longevity_day" || w.type == "mobility_flexibility" ||
                    w.exercises.any { ex -> ex.muscleGroup.any { m -> m in LONGEVITY_MUSCLES } }
            }
        }.take(20)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-history", "Workout History")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HistFilter.entries.forEach { f ->
                TabPills(listOf(f.label), if (filter == f) 0 else -1, onSelect = { filter = f }, modifier = Modifier.width(IntrinsicSize.Min))
            }
        }
        if (workouts.isEmpty()) {
            EmptyState("fa-calendar", "No workouts yet", "Completed workouts appear here.")
        }
        workouts.forEach { w ->
            HistoryRow(
                workout = w,
                expanded = expandedId == w.id,
                onToggle = { expandedId = if (expandedId == w.id) null else w.id },
                onDelete = { deleteTarget = w },
                onNoteSaved = { ver.intValue++ },
                onEditNotes = { idx -> noteTarget = w to idx },
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Export CSV", icon = "fa-file-csv", style = BtnStyle.SECONDARY, modifier = Modifier.weight(1f)) {
                val (csv, rows) = buildHistoryCsv()
                try {
                    val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(android.content.Intent.EXTRA_TEXT, csv)
                    }
                    ctx.startActivity(android.content.Intent.createChooser(send, "Export workout history CSV"))
                } catch (_: Exception) {
                }
                toast = "CSV exported — $rows rows"
            }
            P4Button("Back to Dashboard", icon = "fa-arrow-left", style = BtnStyle.GHOST, modifier = Modifier.weight(1f)) {
                onOpenSection("dashboard")
            }
        }
        Spacer(Modifier.height(120.dp))
    }

    deleteTarget?.let { target ->
        SwalDialog(
            title = "Delete \"${target.name}\"?",
            text = "This action cannot be undone.",
            confirmText = "Yes, delete",
            cancelText = "Cancel",
            onConfirm = {
                ProState.data = ProState.data.copy(workouts = ProState.data.workouts.filterNot { it.id == target.id })
                ProState.scope.launch {
                    com.peakform.fitness.core.ProStore(ctx).deleteWorkoutRecord(target.id)
                }
                ProState.saveWorkoutData()
                ProState.notifyChanged()
                deleteTarget = null
                ver.intValue++
            },
            onDismiss = { deleteTarget = null },
        )
    }

    // ---- notes editor (FIX (audit) #38) ----
    noteTarget?.let { (w, idx) ->
        val ex = w.exercises.getOrNull(idx)
        if (ex?.actual == null) {
            noteTarget = null
        } else {
            NoteEditDialog(
                exerciseName = ex.name,
                initial = ex.actual?.notes ?: "",
                onSave = { note ->
                    val updated = w.copy(exercises = w.exercises.mapIndexed { i, e ->
                        if (i == idx) e.copy(actual = e.actual?.copy(notes = note)) else e
                    })
                    ProState.data = ProState.data.copy(
                        workouts = ProState.data.workouts.map { if (it.id == w.id) updated else it },
                    )
                    ProState.saveWorkoutData()
                    ProState.notifyChanged()
                    noteTarget = null
                    ver.intValue++
                    toast = "Notes saved"
                },
                onDismiss = { noteTarget = null },
            )
        }
    }

    toast?.let { t ->
        LaunchedEffect(t) {
            kotlinx.coroutines.delay(2600)
            toast = null
        }
        ToastBanner(t)
    }
}

val LONGEVITY_MUSCLES = setOf("neck", "deep_neck", "rhomboids", "rear_delts", "feet_ankles", "hand_intrinsics", "foot_intrinsics", "forearms")

private val df = SimpleDateFormat("MMM d, yyyy", Locale.US)

/** Wrap a CSV field in quotes, doubling any inner quotes (commas/newlines stay safe inside quotes). */
private fun csvField(raw: String): String = "\"" + raw.replace("\"", "\"\"") + "\""

/** Build the history CSV — header + one row per exercise per workout. Returns csv text + row count. */
private fun buildHistoryCsv(): Pair<String, Int> {
    val sb = StringBuilder("date,name,type,exercise,weight,sets,reps,rpe,volume,skipped,notes")
    var rows = 0
    ProState.data.workouts.forEach { w ->
        w.exercises.forEach { ex ->
            val a = ex.actual
            val volume = a?.volume ?: (a?.weight ?: 0.0) * (a?.reps?.sum() ?: 0.0)
            val fields = listOf(
                w.date,
                w.name,
                w.type,
                ex.name,
                a?.let { ProJson.formatNum(it.weight) } ?: "",
                a?.sets?.toString() ?: "",
                a?.reps?.joinToString(";") { ProJson.formatNum(it) } ?: "",
                a?.rpeList?.joinToString(";") { ProJson.formatNum(it) } ?: "",
                ProJson.formatNum(volume),
                ex.skipped.toString(),
                a?.notes ?: "",
            )
            sb.append('\n').append(fields.joinToString(",") { csvField(it) })
            rows++
        }
    }
    return sb.toString() to rows
}

@Composable
fun HistoryRow(
    workout: WorkoutRecord,
    expanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onNoteSaved: () -> Unit = {},
    onEditNotes: (Int) -> Unit = {},
) {
    val c = LocalProColors.current
    val dayMillis = ProState.utcDayMillis(workout.date)
    val dateLabel = if (dayMillis == 0L) workout.date.take(10)
        else try { df.format(Date(dayMillis)) } catch (_: Exception) { workout.date.take(10) }
    GlassCard(padding = PaddingValues(0.dp)) {
        Row(
            Modifier.fillMaxWidth().clickable { onToggle() }.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            com.peakform.fitness.ui.FaIcon(if (expanded) "fa-chevron-up" else "fa-chevron-down", size = 12.sp, tint = c.text3)
            Spacer(Modifier.width(12.dp))
            Text(dateLabel, style = ProType.label, color = c.text)
            Spacer(Modifier.width(12.dp))
            Text(workout.name, style = ProType.body2, color = c.text2, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Box(
                Modifier
                    .size(40.dp)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                com.peakform.fitness.ui.FaIcon("fa-trash", size = 13.sp, tint = c.bad, contentDescription = "Delete workout")
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                HorizontalHairline()
                Spacer(Modifier.height(10.dp))
                val summary = workout.summary
                if (summary != null) {
                    KeyValueRow("Total Volume", Stats.formatNumber(summary.totalVolume) + " lbs")
                    KeyValueRow("Average RPE", ProJson.formatNum(summary.averageRPE))
                    KeyValueRow("Completed", "${summary.completedExercises} exercises")
                }
                Spacer(Modifier.height(8.dp))
                workout.exercises.forEachIndexed { exIdx, ex ->
                    Column(Modifier.padding(vertical = 4.dp)) {
                        Text(ex.name, style = ProType.label, color = c.text, fontSize = 14.sp)
                        val rx = ex.prescribed
                        val actual = ex.actual
                        val actualStr = when {
                            ex.skipped -> "Skipped"
                            actual == null -> "—"
                            actual.durations.isNotEmpty() -> "${actual.durations.joinToString(", ") { ProJson.formatNum(it) }}s"
                            actual.failure -> "Failed @ ${actual.attemptedWeight?.toInt() ?: actual.weight.toInt()} lbs"
                            else -> buildString {
                                append("${ProJson.formatNum(actual.weight)} lbs, ${actual.reps.joinToString(", ") { ProJson.formatNum(it) }} reps")
                                val rpes = actual.rpeList
                                if (rpes.isNotEmpty()) append(", RPE ${ProJson.formatNum(rpes.average())}")
                            }
                        }
                        Text("Prescribed ${rx.sets} × ${rx.reps}${if ((rx.weight ?: 0.0) > 0) " @ ${rx.weight?.toInt()} lbs" else ""} — Actual: $actualStr", style = ProType.small, color = c.text3)
                        if (actual != null) {
                            val note = actual.notes
                            if (!note.isNullOrBlank()) {
                                Text("\uD83D\uDCDD $note", style = ProType.small, color = c.text2, modifier = Modifier.padding(top = 2.dp))
                            }
                            Box(
                                Modifier
                                    .heightIn(min = 32.dp)
                                    .clickable { onEditNotes(exIdx) },
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    com.peakform.fitness.ui.FaIcon("fa-pen", size = 11.sp, tint = c.accent, contentDescription = "Edit notes for ${ex.name}")
                                    Spacer(Modifier.width(5.dp))
                                    Text(if (note.isNullOrBlank()) "Add notes" else "Edit notes", fontSize = 11.sp, color = c.accent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteEditDialog(exerciseName: String, initial: String, onSave: (String) -> Unit, onDismiss: () -> Unit) {
    val c = LocalProColors.current
    var text by remember { mutableStateOf(initial) }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.glass2,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Notes — $exerciseName", style = ProType.cardTitle, color = c.text) },
        text = {
            Column {
                Text("How did this exercise feel? Notes are saved with the workout and included in the CSV export.", style = ProType.small, color = c.text3)
                Spacer(Modifier.height(8.dp))
                ProTextField(value = text, onValueChange = { text = it }, placeholder = "e.g. grip gave out on the last set", singleLine = false)
            }
        },
        confirmButton = { P4Button("Save notes", onClick = { onSave(text.trim()) }, style = BtnStyle.PRIMARY, minHeight = 40) },
        dismissButton = { P4Button("Cancel", onClick = onDismiss, style = BtnStyle.SECONDARY, minHeight = 40) },
    )
}

@Composable
fun ToastBanner(text: String) {
    val c = LocalProColors.current
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
        Row(
            Modifier.clip(RoundedCornerShape(14.dp)).background(c.glass2).border(1.dp, c.hairline2, RoundedCornerShape(14.dp)).padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            com.peakform.fitness.ui.FaIcon("fa-circle-info", size = 14.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text(text, style = ProType.body2, color = c.text)
        }
    }
}
