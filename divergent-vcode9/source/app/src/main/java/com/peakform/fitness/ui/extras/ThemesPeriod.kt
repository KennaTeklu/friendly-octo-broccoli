package com.peakform.fitness.ui.extras

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.Period
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.Vocab
import com.peakform.fitness.ui.FaIcon
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.ProColors
import com.peakform.fitness.ui.ThemeController
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.SectionTitle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** ThemePickerScreen — dark/light + 16 accents (legacy "cute appearance" settings rewrite). */
@Composable
fun ThemePickerScreen(onBack: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var dark by remember { mutableStateOf(ThemeController.dark) }
    var accentId by remember { mutableStateOf(ThemeController.accentId) }

    fun apply() {
        ProPrefs.put(ctx, "p4_theme", """{"mode":"${if (dark) "dark" else "light"}","accent":"$accentId"}""")
        ThemeController.set(if (dark) "dark" else "light", accentId)
        com.peakform.fitness.core.Badges.countEvent(ctx, "themeChanged")
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-palette", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Themes", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(false to "Light", true to "Dark").forEach { (d, label) ->
                val sel = dark == d
                Box(
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (sel) c.accentSoft else c.surface2)
                        .border(1.dp, if (sel) c.accent else c.hairline2, RoundedCornerShape(10.dp))
                        .clickable { dark = d; apply() }
                        .padding(horizontal = 22.dp, vertical = 10.dp),
                ) { Text(label, style = ProType.label, color = if (sel) c.accent else c.text2) }
            }
        }
        Spacer(Modifier.height(14.dp))
        val accents = com.peakform.fitness.ui.ACCENTS
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            accents.chunked(4).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { a ->
                        val hex = if (dark) a.darkHex else a.lightHex
                        val sel = accentId == a.id
                        Box(
                            Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(hex)
                                .border(if (sel) 3.dp else 1.dp, if (sel) c.text else c.hairline2, RoundedCornerShape(10.dp))
                                .clickable { accentId = a.id; apply() },
                            contentAlignment = Alignment.Center,
                        ) { if (sel) FaIcon("fa-check", size = 14.sp, tint = Color.White) }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("Accent: ${accents.firstOrNull { it.id == accentId }?.name ?: accentId}", style = ProType.small, color = c.text3)
        Spacer(Modifier.height(30.dp))
    }
}

/** CycleScreen — My Cycle tracking (legacy period tracking + SmartMessageBank). */
@Composable
fun CycleScreen(onBack: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var lastStart by remember { mutableStateOf(ProState.data.user.menstrual.lastPeriodStart ?: "") }
    var length by remember { mutableStateOf(ProState.data.user.menstrual.cycleLength.toString()) }
    var saved by remember { mutableStateOf(false) }
    val phase = remember(lastStart, length, ProState.data.user.menstrual.cycleLength) {
        Period.phase(ProState.data, System.currentTimeMillis())
    }
    val msg = remember(phase) { Period.message(ctx, phase, severity = 3) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-calendar-days", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("My Cycle", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Text(
            "Cycle-aware programming: menstrual ×0.6 · follicular ×1.0 · ovulatory ×1.05 · luteal ×0.8 on training stress. Data stays on this device.",
            style = ProType.body2, color = c.text2,
        )
        Spacer(Modifier.height(14.dp))
        ProTextField(
            TextFieldValue(lastStart),
            onValueChange = { lastStart = it.text; saved = false },
            hint = "Last period start (YYYY-MM-DD)",
        )
        Spacer(Modifier.height(8.dp))
        ProTextField(
            TextFieldValue(length),
            onValueChange = { length = it.text; saved = false },
            hint = "Cycle length in days (default 28)",
        )
        Spacer(Modifier.height(10.dp))
        P4Button("Save cycle data", onClick = {
            val u = ProState.data.user
            ProState.data = ProState.data.copy(user = u.copy(
                menstrual = u.menstrual.copy(
                    lastPeriodStart = lastStart.trim().takeIf { it.isNotBlank() },
                    cycleLength = length.trim().toIntOrNull() ?: 28,
                )))
            ProState.saveWorkoutData()
            saved = true
        }, style = BtnStyle.PRIMARY)
        if (saved) {
            Spacer(Modifier.height(6.dp))
            Text("Saved ✓", style = ProType.small, color = c.ok)
        }
        Spacer(Modifier.height(16.dp))
        if (phase != null) {
            SectionTitle("fa-wand-magic-sparkles", "Coach message")
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(c.accentSoft)
                    .padding(12.dp),
            ) {
                Column {
                    Text("Phase: ${phase?.replaceFirstChar { it.uppercase() }}", style = ProType.label, color = c.accent)
                    Spacer(Modifier.height(4.dp))
                    Text(msg?.text ?: "", style = ProType.body2, color = c.text2)
                }
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}
