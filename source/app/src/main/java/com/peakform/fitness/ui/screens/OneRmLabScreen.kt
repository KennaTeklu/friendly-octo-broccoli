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
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * BATCH-12: OneRmLabScreen — interactive 1RM estimator.
 *
 * Inputs: weight lifted, reps performed, optional RPE.
 * Outputs: 4 estimation methods (Epley, Brzycki, Lombardi, RPE-based),
 * average + confidence interval, working weights at 65/75/85/95%.
 *
 * Snapshots persist per-profile via OneRmLab.saveSnapshot; the history list
 * shows prior attempts.
 */
@Composable
fun OneRmLabScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current

    var weight by remember { mutableStateOf("135") }
    var reps by remember { mutableStateOf("5") }
    var rpe by remember { mutableStateOf("") }
    var exerciseId by remember { mutableStateOf("bench_press") }
    var result by remember { mutableStateOf<OneRmLab.EstimateResult?>(null) }

    val history = remember(result) { OneRmLab.getSnapshots(ctx) }

    Column(Modifier.fillMaxSize().background(c.bg)) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FaIcon("fa-flask-vial", size = 18.sp, tint = c.accent)
            Spacer(Modifier.width(10.dp))
            Text("1RM Lab", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Close", style = BtnStyle.GHOST, onClick = onClose)
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
            Text("Weight lifted (lbs)", style = ProType.small, color = c.text3)
            ProTextField(value = weight, onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "135")
            Spacer(Modifier.height(8.dp))

            Text("Reps performed", style = ProType.small, color = c.text3)
            ProTextField(value = reps, onValueChange = { reps = it.filter { ch -> ch.isDigit() } }, placeholder = "5")
            Spacer(Modifier.height(8.dp))

            Text("RPE (1–10, optional)", style = ProType.small, color = c.text3)
            ProTextField(value = rpe, onValueChange = { rpe = it.filter { ch -> ch.isDigit() } }, placeholder = "7")
            Spacer(Modifier.height(12.dp))

            P4Button("Estimate 1RM", icon = "fa-calculator", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
                val w = weight.toDoubleOrNull() ?: return@P4Button
                val r = reps.toIntOrNull() ?: return@P4Button
                if (w <= 0 || r <= 0) return@P4Button
                val rp = rpe.toDoubleOrNull()?.takeIf { it in 1.0..10.0 }
                val name = Library.getExerciseById(exerciseId)?.name ?: exerciseId
                result = OneRmLab.estimate(exerciseId, name, w, r, rp)
            }
            Spacer(Modifier.height(16.dp))

            result?.let { res ->
                Text("Estimate results", style = ProType.label, color = c.text, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                ResultRow("Epley", "${res.epley.toInt()} lbs", c)
                ResultRow("Brzycki", "${res.brzycki.toInt()} lbs", c)
                ResultRow("Lombardi", "${res.lombardi.toInt()} lbs", c)
                res.rpeBased?.let { ResultRow("RPE-based", "${it.toInt()} lbs", c) }
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(c.tintBg).border(1.dp, c.tintLine, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Average 1RM: ${res.average.toInt()} lbs", style = ProType.label, color = c.accent)
                        Text("Confidence interval: ${res.confidenceLow.toInt()} – ${res.confidenceHigh.toInt()} lbs", style = ProType.small, color = c.text3)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Working weights", style = ProType.label, color = c.text, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                ResultRow("65%", "${res.workingWeight(0.65).toInt()} lbs × ${(res.average * 0.65 / res.weightLifted).toInt()} reps", c)
                ResultRow("75%", "${res.workingWeight(0.75).toInt()} lbs × ${(res.average * 0.75 / res.weightLifted).toInt()} reps", c)
                ResultRow("85%", "${res.workingWeight(0.85).toInt()} lbs × ${(res.average * 0.85 / res.weightLifted).toInt()} reps", c)
                ResultRow("95%", "${res.workingWeight(0.95).toInt()} lbs × ${(res.average * 0.95 / res.weightLifted).toInt()} reps", c)
                Spacer(Modifier.height(12.dp))
                P4Button("Save snapshot", icon = "fa-save", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) {
                    OneRmLab.saveSnapshot(ctx, OneRmLab.Snapshot(exerciseId, res.exerciseName, res))
                }
                Spacer(Modifier.height(16.dp))
            }

            if (history.isNotEmpty()) {
                Text("History (${history.size})", style = ProType.label, color = c.text, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                history.takeLast(10).reversed().forEach { snap ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(c.surface2)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(snap.exerciseName, style = ProType.body2, color = c.text, modifier = Modifier.weight(1f))
                        Text("${snap.result.average.toInt()} lbs", style = ProType.small, color = c.accent)
                        Spacer(Modifier.width(8.dp))
                        Text(snap.result.at.take(10), style = ProType.small, color = c.text3)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, c: ProColors) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = ProType.body2, color = c.text2)
        Text(value, style = ProType.body2, color = c.text, fontWeight = FontWeight.SemiBold)
    }
}
