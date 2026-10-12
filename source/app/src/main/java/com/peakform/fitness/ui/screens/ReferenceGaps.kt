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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.GapClosures
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle

/**
 * ReferenceGaps — BATCH-15 reference-gap closures screen.
 *
 * A small catalog screen that surfaces the closures the user can act on:
 *   - gender-filtered library (the legacy "men's only / women's only" tags)
 *   - fuzzy library search
 *   - RPE color band legend
 *   - competition-date countdown
 *   - postpartum multiplier explainer
 *   - 1RM retest recommendations
 *
 * Reachable from the Library screen's overflow menu.
 */
@Composable
fun ReferenceGapsScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val user = ProState.data.user
    val compDays = GapClosures.competitionCountdown(user.competition?.date)
    val audit = remember { OneRmLab.auditAll(com.peakform.fitness.ui.Fa.appContext!!) }
    val pp = GapClosures.postpartumMultiplier(user.postpartumMonths?.toInt())

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            com.peakform.fitness.ui.FaIcon("fa-clipboard-list", size = 18.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Reference gaps closed", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Close", style = BtnStyle.GHOST, onClick = onClose)
        }
        Spacer(Modifier.height(12.dp))
        // Gender filter
        GapCard("Gender-filtered library", "Movements tagged men-only or women-only are now hidden based on your gender (${user.gender}).", "fa-venus-mars", c)
        // Fuzzy search
        GapCard("Fuzzy library search", "Short queries with no direct hit fall back to a Levenshtein edit distance ≤ 1.", "fa-magnifying-glass", c)
        // RPE band
        GapCard("RPE color band", "RPE ≤ 6 green · 7-8 yellow · 9 orange · 10 red. Surfaces on the Workout card next to every set.", "fa-palette", c)
        // Competition countdown
        if (compDays != null) {
            GapCard(
                "Competition countdown",
                if (compDays >= 0) "${compDays} days until your competition (${user.competition?.date})."
                else "Competition was ${-compDays} days ago — set a new date in Training preferences.",
                "fa-trophy",
                c,
            )
        }
        // Postpartum multiplier
        if (user.postpartumMonths != null && user.postpartumMonths > 0) {
            GapCard("Postpartum multiplier", "×${pp} applied to all prescriptions — pelvic floor first, max lifts off the menu.", "fa-baby", c)
        }
        // 1RM retest
        if (audit.retestDueCount > 0) {
            GapCard("1RM retest due", "${audit.retestDueCount} lifts are stale (>6 weeks since last test). Open the 1RM Lab to re-test.", "fa-trophy", c)
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun GapCard(title: String, body: String, icon: String, c: com.peakform.fitness.ui.ProColors) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2).border(1.dp, c.hairline, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            com.peakform.fitness.ui.FaIcon(icon, size = 14.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text(title, style = ProType.label, color = c.text)
        }
        Spacer(Modifier.height(6.dp))
        Text(body, style = ProType.body2, color = c.text2)
    }
}
