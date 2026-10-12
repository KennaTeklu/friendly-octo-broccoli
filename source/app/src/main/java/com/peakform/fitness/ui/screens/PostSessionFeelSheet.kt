package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
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
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.SelfAssessment
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle
/**
 * BATCH-9 Item 2A: Post-session one-tap feel.
 *
 * Shown after a completed workout. One question: "How was that?"
 * 4 buttons: Too easy / Just right / Hard / Brutal.
 * Optional 30-char note. One tap completes it.
 */
@Composable
fun PostSessionFeelSheet(
    workoutId: String,
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var note by remember { mutableStateOf("") }
    val feelings = listOf(
        "too_easy" to "Too easy" to "fa-face-smile",
        "just_right" to "Just right" to "fa-face-meh",
        "hard" to "Hard" to "fa-face-tired",
        "brutal" to "Brutal" to "fa-face-dizzy",
    )

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(c.surface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("How was that?", style = ProType.sectionTitle, color = c.text)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            feelings.forEach { (pair, icon) ->
                val (rating, label) = pair
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.surface2)
                        .clickable {
                            SelfAssessment.saveFeel(ctx, SelfAssessment.PostSessionFeel(
                                workoutId = workoutId,
                                date = ProState.todayLocal(),
                                rating = rating,
                                note = note,
                            ))
                            onDismiss()
                        }
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    FaIcon(icon, size = 24.sp, tint = c.accent)
                    Spacer(Modifier.height(4.dp))
                    Text(label, fontSize = 11.sp, color = c.text2)
                }
            }
        }
        OutlinedTextField(
            value = note,
            onValueChange = { if (it.length <= 30) note = it },
            label = { Text("What felt off? (optional)") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
