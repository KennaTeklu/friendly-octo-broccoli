package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.coach.authored.Prescription
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.ToggleRow

/**
 * TuningSheet — coach-led tuning gear.
 *
 * v34e: Accepts initialPrescription and shows a preview line.
 * Pre-populates with what the coach already decided.
 * The user tunes from knowledge, not from a blank form.
 */
@Composable
fun TuningSheet(
    visible: Boolean,
    initialPrescription: Prescription? = null,
    onDismiss: () -> Unit,
    onSave: (Prescription) -> Unit = {},
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val user = ProState.data.user
    var aggression by remember { mutableStateOf(user.settings.aggression.toFloat()) }
    var holdBack by remember { mutableStateOf(ProfileSettings.get(ctx, "p4_tune_hold_back") == "true") }
    var tuneTarget by remember { mutableStateOf(ProfileSettings.get(ctx, "p4_tune_target") == "true") }
    var postpartum by remember { mutableStateOf((user.postpartumMonths ?: 0).toString()) }

    // v34e: Pending prescription fields — start from initialPrescription
    var pendingSets by remember { mutableStateOf(initialPrescription?.sets ?: 0) }
    var pendingReps by remember { mutableStateOf(initialPrescription?.reps ?: "") }
    var pendingLoad by remember { mutableStateOf(initialPrescription?.load?.toInt()?.toString() ?: "") }
    var pendingPct1RM by remember { mutableStateOf(initialPrescription?.pct1RM?.let { "${(it * 100).toInt()}" } ?: "") }
    var pendingBandColour by remember { mutableStateOf(initialPrescription?.bandColour ?: "") }
    var pendingTier by remember { mutableStateOf(initialPrescription?.tier?.toString() ?: "") }
    var pendingDuration by remember { mutableStateOf(initialPrescription?.duration?.toInt()?.toString() ?: "") }
    var pendingDistance by remember { mutableStateOf(initialPrescription?.distance?.toInt()?.toString() ?: "") }
    var pendingRest by remember { mutableStateOf(initialPrescription?.restSeconds?.toString() ?: "") }

    // Preview string generator
    fun buildPreview(): String {
        val parts = mutableListOf<String>()
        if (pendingLoad.isNotBlank()) parts.add("${pendingLoad} lbs")
        if (pendingPct1RM.isNotBlank()) parts.add("${pendingPct1RM}% 1RM")
        if (pendingBandColour.isNotBlank()) parts.add("${pendingBandColour} band")
        if (pendingTier.isNotBlank()) parts.add("Tier $pendingTier")
        if (pendingDuration.isNotBlank()) parts.add("${pendingDuration}s hold")
        if (pendingDistance.isNotBlank()) parts.add("${pendingDistance} mi")
        if (pendingSets > 0 && pendingReps.isNotBlank()) parts.add("${pendingSets}x$pendingReps")
        if (pendingRest.isNotBlank()) parts.add("rest ${pendingRest}s")
        return if (parts.isEmpty()) "" else "Coach will set: ${parts.joinToString(", ")}."
    }

    Box(
        Modifier.fillMaxSize().background(c.bg.copy(alpha = 0.6f)).clickable { onDismiss() },
    ) {
        Column(
            Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(c.surface)
                .border(1.dp, c.hairline, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.peakform.fitness.ui.FaIcon("fa-sliders", size = 16.sp, tint = c.accent)
                Spacer(Modifier.width(8.dp))
                Text("Tuning", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
                P4Button("Close", style = BtnStyle.GHOST, onClick = onDismiss)
            }
            Spacer(Modifier.height(8.dp))

            // v34e: Pre-populated prescription display
            if (initialPrescription != null) {
                Text("Current prescription (from coach):", style = ProType.label, color = c.text2)
                Spacer(Modifier.height(6.dp))
                if (pendingLoad.isNotBlank()) Text("Load: $pendingLoad lbs", style = ProType.small, color = c.text3)
                if (pendingPct1RM.isNotBlank()) Text("% 1RM: $pendingPct1RM%", style = ProType.small, color = c.text3)
                if (pendingBandColour.isNotBlank()) Text("Band: $pendingBandColour", style = ProType.small, color = c.text3)
                if (pendingTier.isNotBlank()) Text("Tier: $pendingTier", style = ProType.small, color = c.text3)
                if (pendingDuration.isNotBlank()) Text("Hold: ${pendingDuration}s", style = ProType.small, color = c.text3)
                if (pendingDistance.isNotBlank()) Text("Distance: $pendingDistance mi", style = ProType.small, color = c.text3)
                if (pendingSets > 0) Text("Sets: $pendingSets", style = ProType.small, color = c.text3)
                if (pendingReps.isNotBlank()) Text("Reps: $pendingReps", style = ProType.small, color = c.text3)
                if (pendingRest.isNotBlank()) Text("Rest: ${pendingRest}s", style = ProType.small, color = c.text3)
                Spacer(Modifier.height(8.dp))

                // Editable adjustment buttons
                Text("Adjust:", style = ProType.label, color = c.accent)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (initialPrescription.load != null || initialPrescription.pct1RM != null) {
                        P4Button("Load: $pendingLoad", style = BtnStyle.GHOST, modifier = Modifier.weight(1f)) {
                            pendingLoad = ((pendingLoad.toIntOrNull() ?: 0) + 5).toString()
                        }
                    }
                    if (pendingSets > 0) {
                        P4Button("Sets: $pendingSets", style = BtnStyle.GHOST, modifier = Modifier.weight(1f)) {
                            pendingSets = (pendingSets + 1).coerceAtMost(7)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // v34e: Preview line — updates as user tunes
                val preview = buildPreview()
                if (preview.isNotBlank()) {
                    Text("Preview:", style = ProType.label, color = c.accent)
                    Spacer(Modifier.height(4.dp))
                    Text(preview, style = ProType.body2.copy(fontStyle = FontStyle.Italic), color = c.text2)
                    Spacer(Modifier.height(12.dp))
                }
            }

            // Existing coach dials
            Text("Coach dials — how hard the prescriptions push. Per-profile.",
                style = ProType.small, color = c.text3)
            Spacer(Modifier.height(12.dp))
            Text("Aggression dial: ${(aggression * 100).toInt()}%", style = ProType.label, color = c.text)
            Slider(
                value = aggression,
                onValueChange = { aggression = it },
                valueRange = 0.6f..1.4f,
                steps = 7,
            )
            Spacer(Modifier.height(8.dp))
            ToggleRow(
                "Hold back (force fatigue factor = 0.7)",
                "Forced easy day — overrides the auto fatigue reading.",
                checked = holdBack,
            ) { on ->
                holdBack = on
                ProfileSettings.put(ctx, "p4_tune_hold_back", if (on) "true" else "false")
            }
            Spacer(Modifier.height(6.dp))
            ToggleRow(
                "Tune target (drop load 10% for flagged joints)",
                "Form-over-weight when a joint is flagged.",
                checked = tuneTarget,
            ) { on ->
                tuneTarget = on
                ProfileSettings.put(ctx, "p4_tune_target", if (on) "true" else "false")
            }
            Spacer(Modifier.height(12.dp))
            Text("Postpartum months (0-12 — applies x0.7 / x0.85 multiplier)", style = ProType.small, color = c.text3)
            P4Button(
                if (postpartum == "0") "Not postpartum" else "Postpartum: $postpartum months",
                icon = "fa-baby",
                style = BtnStyle.SECONDARY,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val next = ((postpartum.toIntOrNull() ?: 0) + 1).coerceAtMost(12)
                    postpartum = next.toString()
                },
            )
            Spacer(Modifier.height(12.dp))

            // v34e: Save calls onSave with pending prescription, then dismisses
            P4Button("Save tuning", icon = "fa-save", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
                val result = Prescription(
                    sets = pendingSets,
                    reps = pendingReps,
                    load = pendingLoad.toDoubleOrNull(),
                    pct1RM = pendingPct1RM.toDoubleOrNull()?.let { it / 100.0 },
                    bandColour = pendingBandColour.ifBlank { null },
                    tier = pendingTier.toIntOrNull(),
                    duration = pendingDuration.toDoubleOrNull(),
                    distance = pendingDistance.toDoubleOrNull(),
                    restSeconds = pendingRest.toIntOrNull() ?: 0,
                    components = initialPrescription?.components ?: emptyMap(),
                    sourceRowId = initialPrescription?.sourceRowId,
                )
                android.util.Log.d("ProTune", "Tune saved: $result")
                onSave(result)
                onDismiss()
            }
        }
    }
}
