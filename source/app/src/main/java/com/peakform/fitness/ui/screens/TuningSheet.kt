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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ProfileSettings
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * TuningSheet — BATCH-15.
 *
 * Per-profile tuning dials surfaced from the Workout screen:
 *  - Aggression (0.6 – 1.4) — how hard the prescription engine pushes
 *  - Rest time (30 – 300 s) — between-set rest the timer uses
 *  - Progression rate (0.5% – 5% / week)
 *
 * Each dial persists on change via ProfileSettings + ProState.saveWorkoutData
 * so the next workout picks up the new values immediately.
 *
 * Also exposes a "Tune for today" button that opens the post-session feel sheet.
 */
@Composable
fun TuningSheet(onDismiss: () -> Unit, onPostSessionFeel: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val user = ProState.data.user
    var aggression by remember { mutableStateOf(user.settings.aggression) }
    var restTime by remember { mutableStateOf(user.settings.restTime.toFloat()) }
    var progression by remember { mutableStateOf((user.settings.progressionRate * 100).toFloat()) }

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-sliders", size = 16.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Tuning", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            FaIcon("fa-xmark", size = 14.sp, tint = c.text3, modifier = Modifier.clickable { onDismiss() })
        }
        Spacer(Modifier.height(8.dp))
        Text("Aggression — how hard the engine pushes prescriptions: ${(aggression * 100).toInt()}%", style = ProType.small, color = c.text3)
        Slider(value = aggression.toFloat(), onValueChange = {
            aggression = it.toDouble()
        }, valueRange = 0.6f..1.4f, steps = 7)
        Text("Rest time between sets: ${restTime.toInt()}s", style = ProType.small, color = c.text3)
        Slider(value = restTime, onValueChange = { restTime = it }, valueRange = 30f..300f, steps = 26)
        Text("Progression rate: ${"%.2f".format(progression / 100)} per week", style = ProType.small, color = c.text3)
        Slider(value = progression, onValueChange = { progression = it }, valueRange = 0.5f..5f, steps = 8)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button("Save", icon = "fa-save", style = BtnStyle.PRIMARY, modifier = Modifier.weight(1f)) {
                val s = ProState.data.user.settings
                ProState.data = ProState.data.copy(user = ProState.data.user.copy(
                    settings = s.copy(
                        aggression = aggression.coerceIn(0.6, 1.4),
                        restTime = restTime.toInt().coerceIn(30, 300),
                        progressionRate = (progression / 100.0).coerceIn(0.005, 0.05),
                    ),
                ))
                ProfileSettings.put(ctx, ProfileSettings.AGGRESSION, aggression.toString())
                ProfileSettings.putInt(ctx, ProfileSettings.REST_TIME, restTime.toInt())
                ProfileSettings.put(ctx, ProfileSettings.PROGRESSION_RATE, progression.toString())
                ProState.saveWorkoutData()
                ProState.notifyChanged()
                onDismiss()
            }
            P4Button("Post-session feel", icon = "fa-face-smile", style = BtnStyle.SECONDARY, modifier = Modifier.weight(1f)) {
                onPostSessionFeel()
            }
        }
    }
}
