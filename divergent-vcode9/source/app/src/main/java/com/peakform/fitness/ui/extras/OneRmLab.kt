package com.peakform.fitness.ui.extras

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.LibraryExercise
import com.peakform.fitness.core.OneRmInput
import com.peakform.fitness.core.PlateMath
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.screens.ProTextField as ProField
import com.peakform.fitness.ui.components.TabPills
import com.peakform.fitness.ui.components.ToggleRow
import com.peakform.fitness.ui.FaIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 1RM Lab — v1.4.0. The library's per-exercise 1RM test bench.
 *
 * Two flows, both 1:1 legacy:
 *  • Estimate (legacy library modal 6805-6829 + processTestResult 8821-8850): the 4-step
 *    submax test — verbatim step copy, weight step 2.5, reps 1-15 validation verbatim.
 *  • Test day (legacy workout-card bracketing panel 38696-38913): "Find Your 1‑Rep Max" —
 *    heaviest-successful / just-above-failure bracket, zero-rep floor, robustEstimate1RM
 *    blend (λ from gap ratio), verbatim labels + toasts.
 * Plus native extras (review board): live formula comparison, working-% loading table with
 * plate math, test history, per-exercise working-weight note. Everything persists into the
 * legacy ExerciseRecord fields (tested1RM/testDate/testReps/testWeight/failWeight/
 * testConfidence + isTest history entries) so exports stay byte-compatible.
 */

private fun numFilter(s: String): String = s.filter { it.isDigit() || it == '.' }.take(7)

private fun fmtDate(iso: String?): String = runCatching {
    val parsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(iso!!.take(19))
    SimpleDateFormat("MMM d, yyyy", Locale.US).format(parsed!!)
}.getOrDefault(iso?.take(10) ?: "—")

@Composable
fun OneRmLabSheet(exercise: LibraryExercise, onDismiss: () -> Unit, onToast: (String) -> Unit = {}) {
    val c = LocalProColors.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.glass2,
        shape = RoundedCornerShape(22.dp),
        title = { Text("1RM Lab — ${exercise.name}", style = ProType.cardTitle, color = c.text) },
        text = { OneRmLabPanel(exercise, onToast) },
        confirmButton = { P4Button("Done", onClick = onDismiss, style = com.peakform.fitness.ui.components.BtnStyle.PRIMARY, minHeight = 40) },
    )
}

/** Lab body — also embedded directly (no dialog) by the library's expanded flow and screenshots. */
@Composable
fun OneRmLabPanel(exercise: LibraryExercise, onToast: (String) -> Unit = {}) {
    val c = LocalProColors.current
    var tab by remember { mutableIntStateOf(0) }
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        ProState.listeners.add(l)
        onDispose { ProState.listeners.remove(l) }
    }
    val rec = ProState.data.exercises[exercise.id]

    Column(Modifier.verticalScroll(rememberScrollState())) {
        // current status strip
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LabStat("Tested", rec?.tested1RM?.let { "${PlateMath.fmt(it)} lbs" } ?: "—", Modifier.weight(1f))
            LabStat("Engine μ", rec?.mu?.let { "${PlateMath.fmt(it)} lbs" } ?: "—", Modifier.weight(1f))
            LabStat("Best set", rec?.bestWeight?.takeIf { it > 0 }?.let { "${PlateMath.fmt(it)} lbs" } ?: "—", Modifier.weight(1f))
        }
        rec?.testDate?.let {
            Text("Tested ${fmtDate(it)} · ${rec.testReps ?: 1} reps @ ${PlateMath.fmt(rec.testWeight ?: 0.0)} lbs", style = ProType.small, color = c.text3)
        }
        Spacer(Modifier.height(10.dp))
        TabPills(listOf("Estimate", "Test day", "Table", "History"), tab, onSelect = { tab = it })
        Spacer(Modifier.height(12.dp))
        when (tab) {
            0 -> EstimateTab(exercise, ver.intValue, onToast)
            1 -> BracketingTab(exercise, ver.intValue, onToast)
            2 -> TableTab(exercise, ver.intValue)
            else -> HistoryTab(exercise, ver.intValue, onToast)
        }
    }
}

@Composable
private fun LabStat(label: String, value: String, modifier: Modifier = Modifier) {
    val c = LocalProColors.current
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(label, fontSize = 9.sp, color = c.text3, letterSpacing = 1.sp)
        Text(value, style = ProType.label, color = c.text, fontSize = 13.sp)
    }
}

/** Tab 1 — legacy submax modal (6805-6829), verbatim steps + validation. */
@Composable
private fun EstimateTab(exercise: LibraryExercise, ver: Int, onToast: (String) -> Unit) {
    val c = LocalProColors.current
    var weight by remember(exercise.id) { mutableStateOf("") }
    var reps by remember(exercise.id) { mutableStateOf("") }
    var confirmOdd by remember { mutableStateOf(false) }

    val steps = listOf(
        "1. Warm up with light weight.",
        "2. Choose a weight you can lift for 10-15 reps with good form.",
        "3. Perform as many reps as possible (but stop at 15) with perfect technique.",
        "4. Enter the weight and reps below.",
    )
    steps.forEach { Text(it, style = ProType.body2, color = c.text2, modifier = Modifier.padding(vertical = 2.dp)) }
    Spacer(Modifier.height(10.dp))
    ProField(value = weight, onValueChange = { weight = numFilter(it) }, placeholder = "Weight (lbs):")
    Spacer(Modifier.height(8.dp))
    ProField(value = reps, onValueChange = { reps = numFilter(it) }, placeholder = "Reps Achieved (1-15):")

    val w = weight.toDoubleOrNull()
    val r = reps.toIntOrNull()
    if (w != null && w > 0 && r != null && r in 1..15) {
        Spacer(Modifier.height(8.dp))
        Text(
            "≈ ${PlateMath.fmt(w * (1 + r / 30.0))} lbs (Epley) · load ${PlateMath.describe(w)}",
            style = ProType.small, color = c.accent,
        )
    }

    val bw = ProState.data.user.weight
    val previewOneRm = w?.times(1 + (r ?: 1) / 30.0)
    if (confirmOdd) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.warn.copy(alpha = 0.12f)).padding(10.dp),
        ) {
            Text("That estimate is above 3.5× your body weight — double-check the numbers.", style = ProType.small, color = c.warn)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                P4Button("It's correct — save", style = com.peakform.fitness.ui.components.BtnStyle.SUCCESS, minHeight = 36, onClick = {
                    commitEstimate(exercise, w!!, r!!, onToast)
                    confirmOdd = false
                })
                P4Button("Go back", style = com.peakform.fitness.ui.components.BtnStyle.SECONDARY, minHeight = 36, onClick = { confirmOdd = false })
            }
        }
    } else {
        Spacer(Modifier.height(12.dp))
        P4Button("Submit Test", icon = "fa-dumbbell", style = com.peakform.fitness.ui.components.BtnStyle.PRIMARY, minHeight = 44, modifier = Modifier.fillMaxWidth()) {
            when (val v = OneRmInput.validate(weight, reps)) {
                is OneRmInput.Result.Missing -> onToast("${v.title} — ${v.message}")
                is OneRmInput.Result.Ok ->
                    if (OneRmInput.implausible(previewOneRm ?: 0.0, bw)) confirmOdd = true
                    else commitEstimate(exercise, v.weight, v.reps, onToast)
            }
        }
        Text("Reps must be between 1 and 15 for accurate 1RM estimation.", style = ProType.small, color = c.text3, modifier = Modifier.padding(top = 6.dp))
    }
}

/** Commit path (systems expert REC 1-3): processTestResult → Kalman μ → save → notify. */
private fun commitEstimate(exercise: LibraryExercise, weight: Double, reps: Int, onToast: (String) -> Unit) {
    val est = OneRm.processTestResult(exercise.id, weight, reps)
    if (est != null) {
        OneRm.updateRecursive(exercise.id, weight, reps.toDouble(), rpe = 7.0)
        ProState.saveWorkoutData()
        ProState.notifyChanged()
        onToast("1RM estimated at ${PlateMath.fmt(est)} lbs! Prescription updated.")
    }
}

/** Tab 2 — legacy bracketing panel (38696-38913), verbatim copy. */
@Composable
private fun BracketingTab(exercise: LibraryExercise, ver: Int, onToast: (String) -> Unit) {
    val c = LocalProColors.current
    var pass by remember(exercise.id) { mutableStateOf("") }
    var fail by remember(exercise.id) { mutableStateOf("") }
    var zeroRep by remember { mutableStateOf(false) }

    Text("Find Your 1‑Rep Max for ${exercise.name}", style = ProType.label, color = c.text)
    Text("Enter two numbers to get your personalised workout plan, or indicate if you couldn't do any rep:", style = ProType.body2, color = c.text2)
    Spacer(Modifier.height(10.dp))
    Text("🏋️ For ${exercise.name}, heaviest weight you successfully lifted (lbs):", style = ProType.small, color = c.text2)
    ProField(value = pass, onValueChange = { pass = numFilter(it) }, placeholder = "e.g., 185")
    Spacer(Modifier.height(8.dp))
    Text("❌ For ${exercise.name}, weight just above that you could NOT lift (lbs):", style = ProType.small, color = c.text2)
    ProField(value = fail, onValueChange = { fail = numFilter(it) }, placeholder = "e.g., 195")
    Spacer(Modifier.height(6.dp))
    ToggleRow(
        label = "I couldn't do any rep, even with the lightest weight",
        sub = null,
        checked = zeroRep,
        onToggle = { zeroRep = !zeroRep },
    )
    Text("⚠️ Warm up properly. Use a spotter. Stop if form breaks.", style = ProType.small, color = c.warn, modifier = Modifier.padding(vertical = 6.dp))
    P4Button("Reveal my workout", style = com.peakform.fitness.ui.components.BtnStyle.PRIMARY, minHeight = 44, modifier = Modifier.fillMaxWidth()) {
        val p = pass.toDoubleOrNull() ?: 0.0
        val f = fail.toDoubleOrNull() ?: 0.0
        if (zeroRep) {
            val est = OneRm.applyZeroRepFloor(exercise.id)
            ProState.notifyChanged()
            onToast("Estimated 1RM set to ${PlateMath.fmt(est)} lbs (very low). Prescription updated.")
        } else if (p <= 0 || f <= p) {
            onToast("Please enter valid weights (fail weight must be heavier than successful lift).")
        } else {
            val est = OneRm.applyBracketing(exercise.id, p, f)
            ProState.notifyChanged()
            if (est != null) onToast("1RM estimated at ${PlateMath.fmt(est)} lbs! Prescription updated.")
        }
    }
}

/** Tab 3 — native extra: working-% loading table with plate math. */
@Composable
private fun TableTab(exercise: LibraryExercise, ver: Int) {
    val c = LocalProColors.current
    val rec = ProState.data.exercises[exercise.id]
    val oneRm = rec?.tested1RM ?: rec?.mu ?: OneRm.robustEstimate(exercise.id)
    if (oneRm == null || oneRm <= 0) {
        Text("No 1RM on record yet — run an Estimate or a Test day first.", style = ProType.body2, color = c.text3)
        return
    }
    Text("Loading table — based on ${PlateMath.fmt(oneRm)} lbs", style = ProType.label, color = c.text)
    Text("Percentages of your 1RM with classic rep prescriptions; plates are per side on a 45 lb bar.", style = ProType.small, color = c.text3)
    Spacer(Modifier.height(8.dp))
    val rows = listOf(
        95 to "2", 90 to "4", 85 to "6", 80 to "8", 75 to "10", 70 to "12", 65 to "15", 60 to "18",
    )
    rows.forEach { (pct, reps) ->
        val w = OneRm.roundToNearest(oneRm * pct / 100.0)
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(c.surface2).padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("$pct%", style = ProType.label, color = c.accent, modifier = Modifier.width(46.dp))
            Text("${PlateMath.fmt(w)} lbs", style = ProType.small, color = c.text, modifier = Modifier.width(92.dp))
            Text("× $reps reps", style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(4.dp))
        Text("  plates: ${PlateMath.describe(w)}", style = ProType.small, fontSize = 10.sp, color = c.text3)
        Spacer(Modifier.height(4.dp))
    }
}

/** Tab 4 — test history + persistent working-weight note. */
@Composable
private fun HistoryTab(exercise: LibraryExercise, ver: Int, onToast: (String) -> Unit) {
    val c = LocalProColors.current
    val rec = ProState.data.exercises[exercise.id]
    val tests = remember(ver) { rec?.history?.filter { it.isTest }?.sortedByDescending { it.date } ?: emptyList() }
    Text("Test history", style = ProType.label, color = c.text)
    if (tests.isEmpty()) {
        Text("No tests yet — every Submit Test and Test day result lands here.", style = ProType.small, color = c.text3)
    } else {
        Spacer(Modifier.height(6.dp))
        tests.take(8).forEach { e ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(c.surface2).padding(horizontal = 10.dp, vertical = 7.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(fmtDate(e.date), style = ProType.small, color = c.text)
                    Text(
                        "${PlateMath.fmt(e.weight)} lbs × ${e.reps.firstOrNull()?.toInt() ?: 1} reps → est. ${PlateMath.fmt(e.estimated1RM ?: 0.0)} lbs",
                        style = ProType.small, fontSize = 10.sp, color = c.text3,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
    Spacer(Modifier.height(12.dp))
    Text("Working weight note", style = ProType.label, color = c.text)
    Text("Your standing setup for this lift (bar height, pin setting, seat position…).", style = ProType.small, color = c.text3)
    var note by remember(exercise.id) { mutableStateOf(rec?.workingWeightNote ?: "") }
    ProField(value = note, onValueChange = { note = it.take(120) }, placeholder = "e.g., lane 2, seat one notch up")
    Spacer(Modifier.height(8.dp))
    P4Button("Save note", style = com.peakform.fitness.ui.components.BtnStyle.SECONDARY, minHeight = 38, modifier = Modifier.fillMaxWidth()) {
        val cur = ProState.data.exercises[exercise.id] ?: com.peakform.fitness.core.ExerciseRecord(lastUpdate = ProState.nowIso())
        ProState.data = ProState.data.copy(
            exercises = ProState.data.exercises + (exercise.id to cur.copy(workingWeightNote = note.ifBlank { null })),
        )
        ProState.saveWorkoutData()
        ProState.notifyChanged()
        onToast("Working weight note saved.")
    }
}
