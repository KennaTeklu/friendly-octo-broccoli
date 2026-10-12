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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.CoachingEngine
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * BATCH-8: QuickActionsSheet — bottom sheet with quick actions for the
 * active workout (log last-set RPE, mark all done, skip rest, tune).
 *
 * This is a lightweight composable — it shows the user's current tuning
 * answers and lets them jump to the tuning sheet or post-session feel sheet.
 */
@Composable
fun QuickActionsSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onOpenTuning: () -> Unit,
    onOpenPostSession: () -> Unit,
    onLogAll: () -> Unit,
    onSkipRest: () -> Unit,
) {
    val c = LocalProColors.current
    if (!visible) return
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(c.glass2).padding(16.dp)
    ) {
        Text("Quick actions", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(12.dp))
        P4Button("Log all sets as RPE 7", icon = "fa-bolt", style = BtnStyle.PRIMARY, modifier = Modifier.fillMaxWidth()) { onLogAll() }
        Spacer(Modifier.height(8.dp))
        P4Button("Skip rest timer", icon = "fa-forward", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onSkipRest() }
        Spacer(Modifier.height(8.dp))
        P4Button("Tune today's session", icon = "fa-sliders", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onOpenTuning() }
        Spacer(Modifier.height(8.dp))
        P4Button("Post-session feel", icon = "fa-face-smile", style = BtnStyle.SECONDARY, modifier = Modifier.fillMaxWidth()) { onOpenPostSession() }
        Spacer(Modifier.height(12.dp))
        P4Button("Close", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) { onDismiss() }
    }
}
