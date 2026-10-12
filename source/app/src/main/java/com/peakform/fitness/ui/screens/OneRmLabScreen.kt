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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * OneRmLabScreen — BATCH-15.
 *
 * Lets the user enter a weight × reps set and see what each of the four 1RM
 * estimation formulas would predict. Useful for picking a formula that
 * matches their tested 1RM, and for sanity-checking estimates that look off.
 *
 * The lab is read-only against the user's exercise history — it doesn't write
 * anything back. To update an exercise's 1RM, the user runs a 1RM test
 * (Workout → exercise card → test button) or uses the audit screen.
 */
@Composable
fun OneRmLabScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var weight by remember { mutableStateOf("135") }
    var reps by remember { mutableStateOf("5") }

    val w = weight.toDoubleOrNull() ?: 0.0
    val r = reps.toIntOrNull() ?: 0
    val estimates = remember(weight, reps) { OneRmLab.allEstimates(w, r) }
    val max = estimates.values.maxOrNull() ?: 0.0
    val min = estimates.values.minOrNull() ?: 0.0
    val spread = max - min

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-flask", "1RM Lab")
        Text("Enter a weight × reps set you just hit. The lab estimates your 1RM four ways so you can compare.", style = ProType.small, color = c.text3)

        ProTextField(value = weight, onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "Weight (lbs)")
        ProTextField(value = reps, onValueChange = { reps = it.filter { ch -> ch.isDigit() } }, placeholder = "Reps")

        // formula bar chart
        if (w > 0 && r > 0) {
            GlassCard(padding = PaddingValues(14.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estimates", style = ProType.label, color = c.text)
                    estimates.forEach { (label, est) ->
                        FormulaBar(label, est, max)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Spread: ${"%.1f".format(spread)} lbs (max − min across formulas)", style = ProType.small, color = c.text3)
                }
            }
        }

        P4Button("Back", icon = "fa-arrow-left", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) {
            onClose()
        }
        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun FormulaBar(label: String, value: Double, max: Double) {
    val c = LocalProColors.current
    val pct = if (max > 0) (value / max).coerceIn(0.0, 1.0) else 0.0
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = ProType.small, color = c.text2, modifier = Modifier.width(80.dp))
            Box(Modifier.weight(1f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(c.surface2)) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(pct.toFloat())
                        .clip(RoundedCornerShape(4.dp))
                        .background(c.accent),
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("${value.toInt()} lbs", style = ProType.label, color = c.text, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
        }
    }
}
