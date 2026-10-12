package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.engine.OneRm
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.FaIcon
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.GlassCard
import com.peakform.fitness.ui.components.SectionTitle

/**
 * TuningSheet — Batch 8 coaching tuning sheet.
 *
 * Five-question dialog that captures the user's tuning intent and
 * translates it into a CoachingEngine.TuningAdjustment. Save writes
 * the answers to per-profile ProfileSettings so the next workout
 * reads them.
 *
 * Hosts three other Batch 8 composables:
 *   - OneRmContextBlock — shows the exercise's tested 1RM (or "Needs test")
 *   - TargetChipsBlock  — target rep range / intensity / volume chips
 *   - BandedExerciseInput — banded-exercise resistance picker
 */
@Composable
fun TuningSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val initial = remember { CoachingEngine.getTuning(ctx) }
    var goal by remember { mutableStateOf(initial.goal) }
    var priority by remember { mutableStateOf(initial.priority) }
    var restriction by remember { mutableStateOf(initial.restriction) }
    var enough by remember { mutableStateOf(initial.enough) }
    var mindMuscle by remember { mutableStateOf(initial.mindMuscle.toFloat()) }

    val adjustment = remember(goal, priority, restriction, enough, mindMuscle) {
        CoachingEngine.computeAdjustment(
            CoachingEngine.TuningAnswers(
                goal = goal, priority = priority, restriction = restriction,
                enough = enough, mindMuscle = mindMuscle.toDouble(),
            )
        )
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.glass2,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FaIcon("fa-sliders", size = 18.sp, tint = c.accent)
                Spacer(Modifier.width(10.dp))
                Text("Tune today's session", style = ProType.cardTitle, color = c.text)
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()).heightIn(max = 480.dp)) {
                Text("Goal", style = ProType.small, color = c.text3)
                TuningChipRow(listOf("bigger", "stronger", "leaner", "fit"), goal) { goal = it }
                Spacer(Modifier.height(10.dp))
                Text("Priority", style = ProType.small, color = c.text3)
                TuningChipRow(listOf("strength", "muscle", "endurance", "recovery", "mobility"), priority) { priority = it }
                Spacer(Modifier.height(10.dp))
                Text("Restriction", style = ProType.small, color = c.text3)
                TuningChipRow(listOf("none", "joint", "lower_back", "shoulder", "knee"), restriction) { restriction = it }
                Spacer(Modifier.height(10.dp))
                Text("Last session was…", style = ProType.small, color = c.text3)
                TuningChipRow(listOf("too_easy", "just_right", "hard", "brutal"), enough) { enough = it }
                Spacer(Modifier.height(10.dp))
                Text("Mind-muscle focus: ${(mindMuscle * 100).toInt()}%", style = ProType.small, color = c.text3)
                Slider(value = mindMuscle, onValueChange = { mindMuscle = it }, valueRange = 0f..1f, steps = 9)
                Spacer(Modifier.height(12.dp))
                GlassCard(padding = PaddingValues(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FaIcon("fa-wand-magic-sparkles", size = 14.sp, tint = c.accent)
                        Spacer(Modifier.width(8.dp))
                        Text("Adjustment", style = ProType.label, color = c.text)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(adjustment.sentence, style = ProType.body2, color = c.text2)
                    Spacer(Modifier.height(6.dp))
                    Text("Intensity ×${String.format("%.2f", adjustment.intensityMultiplier)} · Reps ${adjustment.repRange.first}-${adjustment.repRange.second} · Volume ×${String.format("%.2f", adjustment.volumeMultiplier)}${if (adjustment.restrictLoad) " · load restricted" else ""}", style = ProType.small, color = c.accent)
                }
            }
        },
        confirmButton = {
            P4Button("Save", onClick = {
                CoachingEngine.saveTuning(ctx, CoachingEngine.TuningAnswers(
                    goal = goal, priority = priority, restriction = restriction,
                    enough = enough, mindMuscle = mindMuscle.toDouble(),
                ))
                onSave()
            }, style = BtnStyle.PRIMARY)
        },
        dismissButton = {
            P4Button("Cancel", onClick = onDismiss, style = BtnStyle.GHOST)
        },
    )
}

@Composable
private fun TuningChipRow(options: List<String>, selected: String, onPick: (String) -> Unit) {
    val c = LocalProColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
        options.forEach { opt ->
            val active = opt == selected
            Box(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) c.accentSoft else c.surface2)
                    .border(1.dp, if (active) c.accentLine else c.hairline2, RoundedCornerShape(999.dp))
                    .clickable { onPick(opt) }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Text(opt.replace('_', ' ').replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = if (active) c.accent else c.text2, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }
        }
    }
}

// =================================================================
// Batch 8 — sub-composables used by the Workout screen
// =================================================================

/**
 * OneRmContextBlock — shows the exercise's tested 1RM (or "Needs test")
 * at the top of the prescription card.
 */
@Composable
fun OneRmContextBlock(exerciseId: String, modifier: Modifier = Modifier) {
    val c = LocalProColors.current
    val rec = ProState.data.exercises[exerciseId]
    val tested = rec?.tested1RM
    val est = rec?.mu
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.tintBg)
            .border(1.dp, c.tintLine, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FaIcon("fa-trophy", size = 14.sp, tint = c.accent)
        Column(Modifier.weight(1f)) {
            Text("1RM context", style = ProType.statLabel, color = c.text3)
            when {
                tested != null && tested > 0 -> {
                    Text("${OneRm.roundToNearest(tested).toInt()} lbs tested", style = ProType.label, color = c.text)
                    if (est != null && est > 0) {
                        Text("Est: ${OneRm.roundToNearest(est).toInt()} lbs", style = ProType.small, color = c.text3)
                    }
                }
                est != null && est > 0 -> {
                    Text("Est: ${OneRm.roundToNearest(est).toInt()} lbs", style = ProType.label, color = c.text)
                    Text("Not yet tested — run a 1RM test for accurate prescriptions", style = ProType.small, color = c.warn)
                }
                else -> {
                    Text("Needs 1RM test", style = ProType.label, color = c.warn)
                    Text("Run a 1RM test to calibrate all future prescriptions", style = ProType.small, color = c.text3)
                }
            }
        }
    }
}

/**
 * TargetChipsBlock — shows the target rep range + intensity + volume chips
 * derived from the CoachingEngine's current tuning.
 */
@Composable
fun TargetChipsBlock(exerciseId: String, modifier: Modifier = Modifier) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val adj = remember(exerciseId) { CoachingEngine.currentAdjustment(ctx) }
    Row(
        modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TargetChip("fa-hashtag", "${adj.repRange.first}-${adj.repRange.second} reps")
        TargetChip("fa-gauge-high", "${(adj.intensityMultiplier * 100).toInt()}% intensity")
        TargetChip("fa-layer-group", "${(adj.volumeMultiplier * 100).toInt()}% volume")
        if (adj.restrictLoad) TargetChip("fa-shield-halved", "Load restricted", tint = c.warn)
    }
}

@Composable
private fun TargetChip(icon: String, label: String, tint: androidx.compose.ui.graphics.Color? = null) {
    val c = LocalProColors.current
    val color = tint ?: c.accent
    Row(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.32f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FaIcon(icon, size = 9.sp, tint = color)
        Spacer(Modifier.width(5.dp))
        Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

/**
 * BandedExerciseInput — banded-exercise resistance picker.
 *
 * Renders the band color chips (yellow → purple) and writes the chosen
 * resistance into the calling logger via onChange(resistanceLbs).
 */
@Composable
fun BandedExerciseInput(
    modifier: Modifier = Modifier,
    initial: Double = 0.0,
    onChange: (Double) -> Unit,
) {
    val c = LocalProColors.current
    var selected by remember { mutableStateOf(initial) }
    Row(
        modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CoachingEngine.BAND_COLORS.forEach { color ->
            val active = selected == CoachingEngine.BAND_RESISTANCE_LB[color] ?: 0.0
            val bandHex = when (color) {
                "yellow" -> androidx.compose.ui.graphics.Color(0xFFFACC15)
                "green" -> androidx.compose.ui.graphics.Color(0xFF34D399)
                "red" -> androidx.compose.ui.graphics.Color(0xFFF87171)
                "black" -> androidx.compose.ui.graphics.Color(0xFF1F2937)
                "purple" -> androidx.compose.ui.graphics.Color(0xFFA78BFA)
                else -> c.surface3
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    .background(if (active) c.tintStrong else c.surface2)
                    .border(1.dp, if (active) c.focusRing else c.hairline2, RoundedCornerShape(10.dp))
                    .clickable {
                        val r = CoachingEngine.BAND_RESISTANCE_LB[color] ?: 0.0
                        selected = r
                        onChange(r)
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Box(Modifier.size(20.dp, 8.dp).clip(RoundedCornerShape(2.dp)).background(bandHex))
                Spacer(Modifier.height(4.dp))
                Text(color.replaceFirstChar { it.uppercase() }, fontSize = 9.sp, color = if (active) c.accent else c.text2)
                Text("${(CoachingEngine.BAND_RESISTANCE_LB[color] ?: 0.0).toInt()} lb", fontSize = 9.sp, color = c.text3)
            }
        }
    }
}
