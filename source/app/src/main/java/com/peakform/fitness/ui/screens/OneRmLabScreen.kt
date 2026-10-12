package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * OneRmLabScreen — BATCH-18A.
 *
 * Interactive 1RM calculator: enter weight × reps, pick a formula
 * (Epley / Brzycki / Lombardi / Mayhew / Consensus), see the 1RM + a
 * percentage table (50%-100% in 5% steps) for programming the next
 * session.
 *
 * Also lists every exercise the profile has logged, with the current
 * consensus 1RM pulled from the last logged set. Tapping a row pre-fills
 * the calculator with that exercise's last weight × reps.
 */
@Composable
fun OneRmLabScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    var weight by remember { mutableStateOf("135") }
    var reps by remember { mutableStateOf("5") }
    var rpe by remember { mutableStateOf("8") }
    var formula by remember { mutableStateOf("Consensus") }
    val w = weight.toDoubleOrNull() ?: 0.0
    val r = reps.toIntOrNull() ?: 0
    val rp = rpe.toIntOrNull() ?: 0
    val oneRm = when (formula) {
        "Epley" -> OneRmLab.epley(w, r)
        "Brzycki" -> OneRmLab.brzycki(w, r)
        "Lombardi" -> OneRmLab.lombardi(w, r)
        "Mayhew" -> OneRmLab.mayhew(w, r)
        else -> OneRmLab.consensus(w, r)
    }
    val oneRmAdj = if (rp > 0) OneRmLab.adjusted(w, r, rp) else oneRm
    val table = OneRmLab.percentageTable(oneRmAdj)

    val history = remember {
        ProState.data.exercises.entries
            .mapNotNull { (id, rec) ->
                val last = rec.history.lastOrNull { !it.skipped && it.weight > 0 } ?: return@mapNotNull null
                val lib = Library.findExercise(id) ?: return@mapNotNull null
                Triple(lib.name, last.weight, last.sets)
            }
            .sortedByDescending { it.second }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "hdr", contentType = "header") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaIcon("fa-flask", size = 18.sp, tint = c.accent)
                    Spacer(Modifier.width(10.dp))
                    Text("1RM Lab", style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
                    FaIcon("fa-xmark", size = 16.sp, tint = c.text3, modifier = Modifier.clickable { onClose() })
                }
            }
            item(key = "inputs", contentType = "inputs") {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = weight, onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Weight (lbs)") }, modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                        OutlinedTextField(value = reps, onValueChange = { reps = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Reps") }, modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                        OutlinedTextField(value = rpe, onValueChange = { rpe = it.filter { ch -> ch.isDigit() } },
                            label = { Text("RPE") }, modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Consensus", "Epley", "Brzycki", "Lombardi", "Mayhew").forEach { f ->
                            val active = formula == f
                            Box(Modifier.clip(RoundedCornerShape(999.dp))
                                .background(if (active) c.accentSoft else c.surface2)
                                .border(1.dp, if (active) c.accent else c.hairline2, RoundedCornerShape(999.dp))
                                .clickable { formula = f }
                                .padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(f, fontSize = 11.sp, color = if (active) c.accent else c.text2, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
            item(key = "result", contentType = "result") {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(c.tintBg).border(1.dp, c.tintLine, RoundedCornerShape(14.dp))
                        .padding(16.dp),
                ) {
                    Text("Estimated 1RM", style = ProType.small, color = c.text3)
                    Text("${oneRmAdj.toInt()} lbs", style = ProType.statValue, color = c.accent, fontSize = 38.sp)
                    if (rp > 0 && oneRm != oneRmAdj) {
                        Text("RPE-adjusted from $rp (was ${oneRm.toInt()} lbs unadjusted)", style = ProType.small, color = c.text3)
                    }
                }
            }
            item(key = "table", contentType = "table") {
                Column {
                    Text("Percentage table", style = ProType.label, color = c.text)
                    Spacer(Modifier.height(6.dp))
                    table.forEach { (pct, w) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("$pct%", style = ProType.small, color = c.text3, modifier = Modifier.width(56.dp))
                            Text("${w.toInt()} lbs", style = ProType.body2, color = c.text)
                            Spacer(Modifier.weight(1f))
                            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.surface2).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text("~${(100 / pct.toDouble()).toInt()} reps at 1RM×${pct}%", fontSize = 10.sp, color = c.text3)
                            }
                        }
                    }
                }
            }
            item(key = "history", contentType = "history_h") {
                Text("Recent lifts (tap to load)", style = ProType.label, color = c.text)
            }
            items(items = history, key = { it.first }, contentType = { "history_row" }) { (name, w, sets) ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(c.surface).border(1.dp, c.hairline, RoundedCornerShape(10.dp))
                        .clickable { weight = w.toString(); reps = sets.toString() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(name, style = ProType.body2, color = c.text, modifier = Modifier.weight(1f))
                    Text("${w.toInt()} × $sets", style = ProType.small, color = c.accent)
                    Spacer(Modifier.width(8.dp))
                    Text("${OneRmLab.consensus(w, sets).toInt()} lbs", style = ProType.label, color = c.ok)
                }
            }
            item(key = "spacer", contentType = "spacer") { Spacer(Modifier.height(120.dp)) }
        }
    }
}
