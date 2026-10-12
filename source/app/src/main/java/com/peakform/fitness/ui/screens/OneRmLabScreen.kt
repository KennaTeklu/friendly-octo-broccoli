package com.peakform.fitness.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.GlassCard
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.*

/**
 * BATCH-13 Item 4A: 1RM Lab screen.
 *
 * Enter a recent set (weight × reps × RPE) and the app computes:
 * - Estimated 1RM (Epley + Brzycki + Lombardi + RPE-based)
 * - Confidence interval based on reps
 * - Recommended working weights at 65/75/85/95% 1RM
 * Save as a snapshot.
 */
@Composable
fun OneRmLabScreen(
    exerciseId: String,
    onClose: () -> Unit,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var rpe by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<OneRmLab.EstimateResult?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("1RM Lab", style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 20.sp, tint = c.text2, contentDescription = "Close",
                modifier = Modifier.clickable(onClick = onClose).padding(4.dp))
        }
        Text(exerciseId.replace('_', ' ').replaceFirstChar { it.uppercase() },
            style = ProType.body2, color = c.text3)

        // Input fields
        OutlinedTextField(
            value = weight, onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Weight (lbs)") }, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = reps, onValueChange = { reps = it.filter { c -> c.isDigit() } },
            label = { Text("Reps") }, modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = rpe, onValueChange = { rpe = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("RPE (optional, 1-10)") }, modifier = Modifier.fillMaxWidth(),
        )

        P4Button("Estimate", icon = "fa-calculator", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
            val w = weight.toDoubleOrNull() ?: return@P4Button
            val r = reps.toIntOrNull() ?: return@P4Button
            if (r !in 1..30) return@P4Button
            val rpeVal = rpe.toDoubleOrNull()?.takeIf { it in 1.0..10.0 }
            result = OneRmLab.estimate(w, r, rpeVal)
        }

        // Results
        result?.let { res ->
            GlassCard(padding = PaddingValues(16.dp)) @Composable {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estimated 1RM", style = ProType.cardTitle, color = c.text)
                    Text("${res.average.toInt()} lbs", fontSize = 32.sp, fontWeight = FontWeight.Black, color = c.accent)
                    Text("Confidence: ${res.confidenceLow.toInt()} – ${res.confidenceHigh.toInt()} lbs", style = ProType.small, color = c.text3)

                    Spacer(Modifier.height(8.dp))
                    Text("Methods", style = ProType.label, color = c.text2)
                    EstimateRow("Epley", res.epley)
                    EstimateRow("Brzycki", res.brzycki)
                    EstimateRow("Lombardi", res.lombardi)
                    EstimateRow("RPE-based", res.rpeBased)

                    Spacer(Modifier.height(8.dp))
                    Text("Working Weights", style = ProType.label, color = c.text2)
                    OneRmLab.workingWeights(res.average).forEach { ww ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(c.surface2).padding(8.dp)) {
                                Text("${ww.pct}% 1RM", fontSize = 11.sp, color = c.text3)
                            }
                            Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(c.accentSoft).padding(8.dp)) {
                                Text("${ww.weight.toInt()} lbs", fontSize = 14.sp, color = c.accent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    P4Button("Save Snapshot", icon = "fa-camera", style = BtnStyle.SUCCESS, modifier = Modifier.fillMaxWidth()) {
                        OneRmLab.saveSnapshot(ctx, OneRmLab.Snapshot(
                            exerciseId = exerciseId,
                            date = ProState.todayLocal(),
                            weight = weight.toDoubleOrNull() ?: 0.0,
                            reps = reps.toIntOrNull() ?: 0,
                            rpe = rpe.toDoubleOrNull(),
                            estimate = res,
                        ))
                        onClose()
                    }
                }
            }
        }

        // History
        val snapshots = OneRmLab.getSnapshots(ctx, exerciseId)
        if (snapshots.isNotEmpty()) {
            Text("History", style = ProType.cardTitle, color = c.text)
            snapshots.takeLast(5).reversed().forEach { snap ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(snap.date, style = ProType.small, color = c.text3, modifier = Modifier.weight(1f))
                    Text("${snap.weight.toInt()} × ${snap.reps}", style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                    Text("${snap.estimate.average.toInt()} lbs", style = ProType.label, color = c.accent, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EstimateRow(label: String, value: Double) {
    val c = LocalProColors.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = ProType.small, color = c.text2)
        Text("${value.toInt()} lbs", style = ProType.body2, color = c.text)
    }
}
