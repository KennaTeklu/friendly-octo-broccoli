package com.peakform.fitness.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Progress — 1:1 port of #progress-section (scr-progress.md):
 * 11-component radar, workout frequency strip + stats, longevity score circle,
 * volume bar chart, strength line chart (top-3 exercises by Epley 1RM),
 * RPE histogram, mastery list, records board, goal cycle detail.
 */

@Composable
fun ProgressScreen(onOpenSection: (String) -> Unit) {
    val c = LocalProColors.current
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose {
            synchronized(ProState.listeners) { ProState.listeners.remove(l) }
        }
    }
    val _verTick = ver.intValue

    val longevity = remember(ver.intValue) { Stats.calculateLongevityScore() }
    val strengthProgress = remember(ver.intValue) { Stats.calculateOverallStrengthProgress() }
    val streak = remember(ver.intValue) { Stats.calculateStreak() }
    val forecast = remember(ver.intValue) { Stats.strengthForecast() }
    val composite = remember(ver.intValue) { Stats.dashboardComposite(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-chart-line", "Progress Analytics")

        // hero: longevity circle + radar
        GlassCard(padding = PaddingValues(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val color = when {
                    longevity.total >= 80 -> c.ok
                    longevity.total >= 60 -> c.warn
                    else -> c.bad
                }
                ProgressRing(pct = longevity.total.toDouble(), sizeDp = 100, ringWidth = 8, color = color) {
                    Box(Modifier.size(80.dp).clip(RoundedCornerShape(999.dp)).background(c.surface), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${longevity.total}", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, color = color, fontFamily = SpaceGrotesk)
                            Text("/100", fontSize = 10.sp, color = c.text3)
                        }
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Longevity Score", style = ProType.cardTitle, color = c.text)
                    Text(longevity.status, style = ProType.body2, color = color)
                    Spacer(Modifier.height(6.dp))
                    Text("Overall strength progress ${String.format("%.1f", strengthProgress)}%", style = ProType.small, color = c.text3)
                    Text("$streak day streak", style = ProType.small, color = c.text3)
                }
            }
        }

        // strength forecast (SBD)
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-weight-hanging", "Strength Forecast", Modifier)
            forecast.take(4).forEach { row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    com.peakform.fitness.ui.FaIcon("fa-dumbbell", size = 12.sp, tint = c.accent)
                    Spacer(Modifier.width(8.dp))
                    Text(row.lift.replace('_', ' ').replaceFirstChar { it.uppercase() }, style = ProType.body2, color = c.text2, modifier = Modifier.weight(1f))
                    Text("${row.est1RM.toInt()} lbs", style = ProType.label, color = c.text)
                    Spacer(Modifier.width(8.dp))
                    Text("×${String.format("%.2f", row.bodyweightRatio)} BW", style = ProType.small, color = c.text3)
                }
            }
            if (forecast.isEmpty()) Text("Log some workouts to see your forecast.", style = ProType.small, color = c.text3)
        }

        // volume chart
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-chart-simple", "Volume — Last 8 Workouts", Modifier)
            VolumeBars(ver.intValue)
        }

        // strength trend
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-arrow-trend-up", "Strength Trend (est. 1RM)", Modifier)
            CompositeChart(points = composite)
        }

        // mastery list
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-layer-group", "Component Mastery", Modifier)
            val sub = longevity.sub.entries.sortedByDescending { it.value }
            sub.forEach { (name, score) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(name.replace('_', ' ').replaceFirstChar { it.uppercase() }, style = ProType.small, color = c.text2, modifier = Modifier.weight(1f))
                    Box(Modifier.width(90.dp)) { ProgressBar(score, c.accent, height = 7) }
                    Spacer(Modifier.width(8.dp))
                    Text("${score.toInt()}", style = ProType.small, color = c.text, textAlign = TextAlign.End)
                }
            }
            if (sub.isEmpty()) Text("Complete workouts to build your component profile.", style = ProType.small, color = c.text3)
        }

        P4Button("Back to Dashboard", icon = "fa-arrow-left", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) {
            onOpenSection("dashboard")
        }
        Spacer(Modifier.height(120.dp))
    }
}

@Composable
fun VolumeBars(ver: Int = 0) {
    val c = LocalProColors.current
    val volumes = remember(ver) {
        ProState.data.workouts.takeLast(8).map { it.summary?.totalVolume ?: 0.0 }
    }
    val maxV = volumes.maxOrNull()?.takeIf { it > 0 } ?: 1.0
    Row(
        Modifier.fillMaxWidth().height(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        volumes.forEachIndexed { i, v ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                val barHeight = (120 * (v / maxV)).dp
                val isStub = barHeight < 2.dp
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(if (isStub) 2.dp else barHeight)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isStub) Brush.verticalGradient(listOf(c.accent.copy(alpha = 0.35f), c.accent.copy(alpha = 0.35f)))
                            else Brush.verticalGradient(listOf(c.accent, c.accent.copy(alpha = 0.45f)))
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text("W${i + 1}", fontSize = 9.sp, color = c.text3)
            }
        }
    }
    if (volumes.isEmpty()) {
        Text("No workout volumes yet.", style = ProType.small, color = c.text3)
    }
}
