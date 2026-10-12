package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle

/**
 * OneRmLabScreen — BATCH-12 1RM Lab calculator + audit console.
 *
 * Reachable from Settings → "1RM Lab" button.
 */
@Composable
fun OneRmLabScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("5") }
    var formula by remember { mutableStateOf(OneRmLab.F_EPLEY) }
    val estimate = remember(weight, reps, formula) {
        val w = weight.toDoubleOrNull() ?: 0.0
        val r = reps.toIntOrNull() ?: 0
        OneRmLab.estimate(w, r, formula)
    }
    val audit = remember { OneRmLab.auditAll(ctx) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            com.peakform.fitness.ui.FaIcon("fa-flask-vial", size = 18.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("1RM Lab", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Close", style = BtnStyle.GHOST, onClick = onClose)
        }
        Spacer(Modifier.height(12.dp))
        // Calculator
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(c.surface2).border(1.dp, c.hairline, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Text("Calculator", style = ProType.label, color = c.text)
            Spacer(Modifier.height(8.dp))
            com.peakform.fitness.ui.screens.ProTextField(
                value = weight,
                onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } },
                placeholder = "Weight lifted (lbs)",
            )
            Spacer(Modifier.height(8.dp))
            com.peakform.fitness.ui.screens.ProTextField(
                value = reps,
                onValueChange = { reps = it.filter { ch -> ch.isDigit() } },
                placeholder = "Reps (1-15)",
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(OneRmLab.F_EPLEY to "Epley", OneRmLab.F_BRZYCKI to "Brzycki", OneRmLab.F_LOMBARDI to "Lombardi").forEach { (f, label) ->
                    val active = formula == f
                    Box(
                        Modifier.clip(RoundedCornerShape(999.dp))
                            .background(if (active) c.accentSoft else c.surface3)
                            .border(1.dp, if (active) c.accent else c.hairline2, RoundedCornerShape(999.dp))
                            .clickable { formula = f }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) { Text(label, fontSize = 11.sp, color = if (active) c.accent else c.text2) }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("Estimated 1RM: ${estimate.toInt()} lbs", style = ProType.statValue, color = c.accent)
        }
        Spacer(Modifier.height(12.dp))
        Text("Audit — every tested 1RM vs estimate", style = ProType.label, color = c.text)
        Spacer(Modifier.height(6.dp))
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(audit.rows, key = { it.exerciseId }) { row ->
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(c.surface2).border(1.dp, c.hairline, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(row.exerciseName, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
                        if (row.retestDue) Text("retest", fontSize = 10.sp, color = c.warn, fontWeight = FontWeight.Bold)
                    }
                    Text("Tested ${row.tested1Rm.toInt()} · Est ${row.estimated1Rm.toInt()} · Δ ${"%+.0f".format(row.deltaLbs)} lbs",
                        style = ProType.small, color = c.text3)
                    Spacer(Modifier.height(6.dp))
                    P4Button("Reset", icon = "fa-rotate-left", style = BtnStyle.GHOST, minHeight = 30) {
                        OneRmLab.resetOne(ctx, row.exerciseId)
                    }
                }
            }
        }
    }
}
