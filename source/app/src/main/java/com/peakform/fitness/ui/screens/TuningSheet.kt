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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.ToggleRow

/**
 * TuningSheet — BATCH-8 coach-led tuning gear.
 *
 * Surfaces the per-profile dials that drive the CoachingEngine:
 *   - aggression slider (0.6 — 1.4)
 *   - hold-back toggle (force fatigue factor = 0.7)
 *   - tune-target toggle (drop load 10% for flagged joints)
 *   - postpartum months (multiplier)
 *
 * All writes go through ProfileSettings so they persist per-profile.
 */
@Composable
fun TuningSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val user = ProState.data.user
    var aggression by remember { mutableStateOf(user.settings.aggression.toFloat()) }
    var holdBack by remember { mutableStateOf(ProfileSettings.get(ctx, "p4_tune_hold_back") == "true") }
    var tuneTarget by remember { mutableStateOf(ProfileSettings.get(ctx, "p4_tune_target") == "true") }
    var postpartum by remember { mutableStateOf((user.postpartumMonths ?: 0).toString()) }

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
            Text("Postpartum months (0-12 — applies ×0.7 / ×0.85 multiplier)", style = ProType.small, color = c.text3)
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
            P4Button("Save tuning", icon = "fa-save", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) {
                val s = ProState.data.user.settings
                ProState.data = ProState.data.copy(user = ProState.data.user.copy(
                    postpartumMonths = postpartum.toIntOrNull()?.takeIf { it > 0 }?.toDouble(),
                    settings = s.copy(aggression = aggression.toDouble().coerceIn(0.6, 1.4)),
                ))
                ProState.saveWorkoutData()
                onDismiss()
            }
        }
    }
}
