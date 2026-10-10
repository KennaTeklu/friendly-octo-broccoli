package com.peakform.fitness.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.StreakFire
import com.peakform.fitness.core.WorkoutRecord
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.P4Button

/** Local ZXing QR rendering — see ShareSheet.kt for the canonical definition. */

/**
 * Shared dialogs, implemented as in-composition glass dialogs (same idiom as the
 * MoreSheet) so they inherit the app theme fully. Copy is verbatim legacy.
 */

/** H2/ST3/ST4 — honor-system streak recovery (PF L59587–59663). */
@Composable
fun StreakRecoveryDialog(onDismiss: () -> Unit, onShare: () -> Unit, onRelight: () -> Unit) {
    val c = LocalProColors.current
    GlassDialogHost(onDismiss = onDismiss) {
        Text("❄️ " + StreakFire.RECOVERY_TITLE, style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text(StreakFire.RECOVERY_BODY, style = ProType.body2, color = c.text2)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Share", style = BtnStyle.SECONDARY, onClick = onShare)
            P4Button(StreakFire.RECOVERY_CONFIRM, style = BtnStyle.PRIMARY, onClick = onRelight)
        }
    }
}

/** I1 — pre-import confirmation, verbatim legacy copy (PF processImport L37667–37681). */
@Composable
fun ImportWarningDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val c = LocalProColors.current
    GlassDialogHost(onDismiss = onDismiss) {
        Text("Import Data", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text("This will replace all your current data. Continue?", style = ProType.body2, color = c.text2)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Yes, overwrite", style = BtnStyle.DANGER, onClick = onConfirm)
            P4Button("Cancel", style = BtnStyle.SECONDARY, onClick = onDismiss)
        }
    }
}

/** C3 — momentum steering modal (PF L41130–41173). */
@Composable
fun MomentumDialog(onBigger: () -> Unit, onStronger: () -> Unit, onDismiss: () -> Unit) {
    val c = LocalProColors.current
    GlassDialogHost(onDismiss = onDismiss) {
        Text("🎯 Next time, I want to be…", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            P4Button("💪 BIGGER", style = BtnStyle.PRIMARY, onClick = onBigger)
            Spacer(Modifier.width(10.dp))
            P4Button("🔥 STRONGER", style = BtnStyle.INFO, onClick = onStronger)
        }
        Spacer(Modifier.height(8.dp))
        Text("(This will influence your next workout)", style = ProType.small, color = c.text3)
    }
}

/**
 * BATCH-2A feature 8 — pre-import guard (P4.Studio.guardImport): confirm before
 * ANY import; only the red DANGER button proceeds.
 */
@Composable
fun ImportGuardDialog(detail: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val c = LocalProColors.current
    GlassDialogHost(onDismiss = onDismiss) {
        Text("⚠️ Import data?", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text(detail, style = ProType.body2, color = c.text2)
        Spacer(Modifier.height(8.dp))
        Text("A guarded snapshot of your current data is taken first, so you can always restore.", style = ProType.small, color = c.text3)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Yes, import", style = BtnStyle.DANGER, onClick = onConfirm)
            P4Button("Cancel", style = BtnStyle.SECONDARY, onClick = onDismiss)
        }
    }
}

/** V2 — snapshot restore confirmation. */
@Composable
fun RestoreConfirmDialog(name: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val c = LocalProColors.current
    GlassDialogHost(onDismiss = onDismiss) {
        Text("Restore snapshot?", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text(
            "“$name” will replace your current workouts, exercises and profile stats. Your current state is snapshotted first.",
            style = ProType.body2, color = c.text2,
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Restore", style = BtnStyle.PRIMARY, onClick = onConfirm)
            P4Button("Cancel", style = BtnStyle.SECONDARY, onClick = onDismiss)
        }
    }
}

/** SH1–SH4 — workout share: see ShareSheet.kt for the canonical definition (QR + share text + system share + copy). */

/** LB detail — exercise detail sheet as a glass dialog. */
@Composable
fun ExerciseDetailDialog(
    exercise: com.peakform.fitness.core.LibraryExercise,
    onClose: () -> Unit,
    onAdd: () -> Unit,
) {
    val c = LocalProColors.current
    GlassDialogHost(onDismiss = onClose, scrollable = true) {
        Text(exercise.name, style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text("Muscles: ${exercise.muscles.joinToString(", ") { it.replace('_', ' ') }}", style = ProType.small, color = c.text2)
        Text("Equipment: ${exercise.equipment} · Sets: ${exercise.defaultSets} × ${exercise.defaultReps}", style = ProType.small, color = c.text2)
        if (exercise.progression.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text("Progression: ${exercise.progression}", style = ProType.small, color = c.text3)
        }
        if (exercise.instructions.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text("How to do it", style = ProType.label, color = c.text)
            exercise.instructions.forEachIndexed { i, step ->
                Text("${i + 1}. ${step.replace(Regex("<[^>]*>"), "")}", style = ProType.small, color = c.text2, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Add to Workout", onClick = onAdd, style = BtnStyle.PRIMARY, minHeight = 40)
            P4Button("Close", onClick = onClose, style = BtnStyle.SECONDARY, minHeight = 40)
        }
    }
}

/** In-composition modal host: scrim + centered glass card (MoreSheet idiom). */
@Composable
fun GlassDialogHost(
    onDismiss: () -> Unit,
    scrollable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = LocalProColors.current
    Box(
        Modifier
            .fillMaxSize()
            .background(c.bg.copy(alpha = 0.66f))
            .clickable { onDismiss() }
            .pointerInput(Unit) {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(c.glass2)
                .border(1.dp, c.hairline2, RoundedCornerShape(22.dp))
                .clickable { }
                .pointerInput(Unit) { detectTapGestures { } }
                .padding(20.dp)
                .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier),
            content = content,
        )
    }
}

/**
 * BATCH-4C Gap 2 — Longevity Report modal.
 *
 * Mirrors reference showLongevityReport() (app.js L45036–45169): surfaces the
 * longevity score, sub-score breakdown, aging-risk factors with severity
 * badges, and recommendations. The native app already computes all of this
 * (Stats.calculateLongevityScore, Stats.assessAgingRisks,
 * Stats.generateLongevityRecommendations) but only used it internally to pick
 * longevity-workout exercises — the user never saw the data.
 *
 * onLongevityWorkout: called when the user taps "Longevity Workout" — the
 * caller routes to the existing generate-longevity path.
 */
@Composable
fun LongevityReportDialog(
    onDismiss: () -> Unit,
    onLongevityWorkout: () -> Unit,
) {
    val c = LocalProColors.current
    val longevity = remember { com.peakform.fitness.engine.Stats.calculateLongevityScore() }
    val risks = remember { com.peakform.fitness.engine.Stats.assessAgingRisks() }
    val recommendations = remember { com.peakform.fitness.engine.Stats.generateLongevityRecommendations() }

    GlassDialogHost(onDismiss = onDismiss, scrollable = true) {
        // ---- header: score circle + status ----
        Row(verticalAlignment = Alignment.CenterVertically) {
            val color = when {
                longevity.total >= 80 -> c.ok
                longevity.total >= 60 -> c.warn
                else -> c.bad
            }
            Box(
                Modifier.size(64.dp).clip(RoundedCornerShape(999.dp))
                    .background(c.surface).border(2.dp, color, RoundedCornerShape(999.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${longevity.total}", style = ProType.statValue, color = color, fontSize = 22.sp)
                    Text("/100", style = ProType.small, color = c.text3, fontSize = 9.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Longevity & Aging Risk", style = ProType.cardTitle, color = c.text)
                Text(longevity.status, style = ProType.body2, color = color)
            }
        }

        Spacer(Modifier.height(14.dp))

        // ---- sub-score breakdown ----
        Text("Score Breakdown", style = ProType.label, color = c.text)
        Spacer(Modifier.height(6.dp))
        longevity.sub.forEach { (name, score) ->
            val display = when (name) {
                "gripStrength" -> "Grip Strength"
                "jointMobility" -> "Joint Mobility"
                "muscleBalance" -> "Muscle Balance"
                "consistency" -> "Consistency"
                "trend" -> "Strength Trend"
                "variety" -> "Exercise Variety"
                else -> name.replaceFirstChar { it.uppercase() }
            }
            val pct = score.coerceIn(0.0, 100.0)
            val barColor = when {
                pct >= 70 -> c.ok
                pct >= 40 -> c.warn
                else -> c.bad
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(display, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                Box(Modifier.width(80.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(c.surface2)) {
                    Box(Modifier.fillMaxWidth(pct.toFloat() / 100f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(barColor))
                }
                Spacer(Modifier.width(6.dp))
                Text("${score.toInt()}", style = ProType.small, color = c.text3, modifier = Modifier.width(28.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
        }

        // ---- aging risk factors ----
        if (risks.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Text("Aging Risk Factors", style = ProType.label, color = c.text)
            Spacer(Modifier.height(6.dp))
            risks.forEach { risk ->
                val sevColor = when (risk.severity) {
                    "High" -> c.bad
                    "Medium" -> c.warn
                    else -> c.accent
                }
                val sevBg = when (risk.severity) {
                    "High" -> c.bad.copy(alpha = 0.12f)
                    "Medium" -> c.warn.copy(alpha = 0.12f)
                    else -> c.accentSoft
                }
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(sevBg)
                        .border(1.dp, sevColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.clip(RoundedCornerShape(999.dp))
                                .background(sevColor).padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(risk.severity, style = ProType.small, color = c.onAccent, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(risk.muscle.replace('_', ' ').replaceFirstChar { it.uppercase() }, style = ProType.body2, color = c.text, modifier = Modifier.weight(1f))
                        Text("${risk.score.toInt()}/100", style = ProType.small, color = c.text3)
                    }
                    if (risk.reason.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(risk.reason, style = ProType.small, color = c.text2)
                    }
                    if (risk.ideal.isNotBlank() && risk.current.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text("Target: ${risk.ideal} · Current: ${risk.current}", style = ProType.small, color = c.text3, fontSize = 10.sp)
                    }
                }
            }
        }

        // ---- recommendations ----
        if (recommendations.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Text("Recommendations", style = ProType.label, color = c.text)
            Spacer(Modifier.height(6.dp))
            recommendations.take(8).forEachIndexed { i, rec ->
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    Text("${i + 1}.", style = ProType.small, color = c.accent, modifier = Modifier.width(20.dp))
                    Text(rec, style = ProType.small, color = c.text2)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Longevity Workout", style = BtnStyle.PRIMARY, modifier = Modifier.weight(1f), onClick = {
                onLongevityWorkout()
                onDismiss()
            })
            P4Button("Close", style = BtnStyle.SECONDARY, onClick = onDismiss)
        }
    }
}
