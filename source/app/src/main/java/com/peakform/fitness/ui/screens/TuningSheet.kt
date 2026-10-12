package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.SelfAssessment
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle
/**
 * BATCH-8 Item 1D: Per-exercise tuning sheet.
 *
 * Opens when the user taps the gear icon on an exercise card.
 * 3-5 multiple-choice questions that steer future prescriptions.
 * Each question has a default of "No answer" and a Pro prediction shown as a suggestion.
 */
@Composable
fun TuningSheet(
    exerciseId: String,
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var answers by remember(exerciseId) {
        mutableStateOf<CoachingEngine.TuningAnswers>(CoachingEngine.getTuning(ctx, exerciseId))
    }

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(c.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tune ${exerciseId.replace('_', ' ').replaceFirstChar { it.uppercase() }}",
                style = ProType.sectionTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 20.sp, tint = c.text2, contentDescription = "Close",
                modifier = Modifier.clickable(onClick = onDismiss).padding(4.dp))
        }
        Text("Your answers steer future prescriptions. Leave blank for Pro's default.",
            style = ProType.small, color = c.text3)

        TuningQuestion(
            label = "What do you want for this muscle?",
            options = listOf("bigger", "stronger", "both", "neither"),
            selected = answers.goal,
            suggestion = "both",
        ) { answers = answers.copy(goal = it) }

        TuningQuestion(
            label = "Priority for this muscle?",
            options = listOf("high", "medium", "low"),
            selected = answers.priority,
            suggestion = "medium",
        ) { answers = answers.copy(priority = it) }

        TuningQuestion(
            label = "Any restriction here?",
            options = listOf("tightness", "pain", "injury", "none"),
            selected = answers.restriction,
            suggestion = "none",
        ) { answers = answers.copy(restriction = it) }

        TuningQuestion(
            label = "Do you already have enough of this muscle?",
            options = listOf("yes", "no"),
            selected = answers.enough,
            suggestion = "no",
        ) { answers = answers.copy(enough = it) }

        // Mind-muscle connection 1-5 slider
        Text("Mind-muscle connection: ${if (answers.mindMuscle > 0) answers.mindMuscle else "—"}", style = ProType.body2, color = c.text2)
        Slider(
            value = answers.mindMuscle.toFloat(),
            onValueChange = { answers = answers.copy(mindMuscle = it.toInt()) },
            valueRange = 0f..5f,
            steps = 4,
        )

        // Show what the adjustment will be
        val adj = CoachingEngine.computeAdjustment(answers)
        if (adj.restrictNote.isNotBlank()) {
            Text("⚠ ${adj.restrictNote}", style = ProType.small, color = c.warn)
        }
        Text("Result: intensity ×${String.format("%.2f", adj.intensityMultiplier)}, volume ×${String.format("%.2f", adj.volumeMultiplier)}${if (adj.repRange.isNotBlank()) ", reps $adj.repRange" else ""}",
            style = ProType.small, color = c.accent)

        P4Button("Save tuning", icon = "fa-check", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
            CoachingEngine.saveTuning(ctx, exerciseId, answers)
            ProState.notifyChanged()
            onDismiss()
        }
    }
}

@Composable
private fun TuningQuestion(
    label: String,
    options: List<String>,
    selected: String,
    suggestion: String,
    onSelect: (String) -> Unit,
) {
    val c = LocalProColors.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = ProType.body2, color = c.text)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { opt ->
                val active = selected == opt
                val isSuggestion = opt == suggestion && selected.isBlank()
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) c.accentSoft else if (isSuggestion) c.tint else c.surface2)
                        .border(1.dp, if (active) c.accent else if (isSuggestion) c.accentLine else c.hairline2, RoundedCornerShape(999.dp))
                        .clickable { onSelect(if (selected == opt) "" else opt) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(opt.replaceFirstChar { it.uppercase() }, fontSize = 12.sp,
                        color = if (active) c.accent else c.text2)
                }
            }
        }
    }
}

/** BATCH-8 Item 1A: 1RM context block — shown before prescription. */
@Composable
fun OneRmContextBlock(
    has1RM: Boolean,
    tested1RM: Double?,
    testDate: String?,
    lastWeight: Double,
    lastReps: Double,
) {
    val c = LocalProColors.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("YOUR CAPACITY", style = ProType.statLabel, color = c.text3)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (has1RM && tested1RM != null) {
                Column {
                    Text("${tested1RM.toInt()} lbs", style = ProType.label, color = c.accent, fontSize = 18.sp)
                    Text("tested 1RM", style = ProType.statLabel, color = c.text3)
                }
            } else {
                Column {
                    Text("—", style = ProType.label, color = c.warn, fontSize = 18.sp)
                    Text("needs 1RM test", style = ProType.statLabel, color = c.warn)
                }
            }
            if (lastWeight > 0) {
                Column {
                    Text("${lastWeight.toInt()} × ${lastReps.toInt()}", style = ProType.label, color = c.text2, fontSize = 18.sp)
                    Text("last session", style = ProType.statLabel, color = c.text3)
                }
            }
        }
    }
}

/** BATCH-8 Item 1C: Target chips + coach sentence. */
@Composable
fun TargetChipsBlock(
    components: List<String>,
    muscles: List<String>,
    coachSentence: String,
) {
    val c = LocalProColors.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            components.take(3).forEach { comp ->
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(c.tint)
                        .border(1.dp, c.accentLine, RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(comp.replace('_', ' '), fontSize = 10.sp, color = c.inkSoft)
                }
            }
            muscles.take(2).forEach { m ->
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(c.surface2)
                        .border(1.dp, c.hairline2, RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(m.replace('_', ' '), fontSize = 10.sp, color = c.text2)
                }
            }
        }
        Text(coachSentence, style = ProType.small, color = c.text3, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    }
}

/** BATCH-8 Item 1B: Banded exercise input. */
@Composable
fun BandedExerciseInput(
    selectedColor: String?,
    onColorSelect: (String) -> Unit,
) {
    val c = LocalProColors.current
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("BAND RESISTANCE", style = ProType.statLabel, color = c.text3)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoachingEngine.BAND_COLORS.forEach { color ->
                val active = selectedColor == color
                val bandColor = when (color) {
                    "yellow" -> androidx.compose.ui.graphics.Color(0xFFFACC15)
                    "red" -> androidx.compose.ui.graphics.Color(0xFFEF4444)
                    "green" -> androidx.compose.ui.graphics.Color(0xFF10B981)
                    "blue" -> androidx.compose.ui.graphics.Color(0xFF3B82F6)
                    "black" -> androidx.compose.ui.graphics.Color(0xFF1F2937)
                    else -> c.surface3
                }
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(bandColor)
                        .border(if (active) 3.dp else 1.dp, if (active) c.text else c.hairline2, androidx.compose.foundation.shape.CircleShape)
                        .clickable { onColorSelect(if (active) "" else color) }
                )
            }
        }
        selectedColor?.let {
            Text("~${CoachingEngine.BAND_RESISTANCE_LB[it]} lbs resistance", style = ProType.small, color = c.accent)
        }
    }
}
