package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.core.ProState
import com.peakform.fitness.engine.Fatigue
import com.peakform.fitness.engine.Generator
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*

/**
 * Recovery — 1:1 port of #recovery-section (scr-recovery.md):
 * overall readiness banner, per-category muscle rows (name, last trained, shimmer bar,
 * status Ready/Soon/Resting), recommendations block, Generate Next Workout + Longevity Workout.
 */

@Composable
fun RecoveryScreen(onOpenSection: (String) -> Unit, onRequireGenerate: (String) -> Unit) {
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
    val report = remember(ver.intValue) { Stats.recoveryReport() }
    val recommendations = remember(ver.intValue) { Stats.generateLongevityRecommendations() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        SectionTitle("fa-heartbeat", "Muscle Recovery Status")

        // overall readiness
        val overallColor = when {
            report.overall >= 80 -> c.ok
            report.overall >= 50 -> c.warn
            report.overall > 0 -> c.bad
            else -> c.surface3
        }
        val overallLabel = when {
            report.overall >= 85 -> "Excellent"
            report.overall >= 70 -> "Good"
            report.overall >= 50 -> "Moderate"
            report.overall >= 30 -> "Low"
            else -> "Very Low"
        }
        GlassCard(padding = PaddingValues(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(pct = report.overall.toDouble(), sizeDp = 64, ringWidth = 6, color = overallColor) {
                    Box(Modifier.size(50.dp).clip(RoundedCornerShape(999.dp)).background(c.surface), contentAlignment = Alignment.Center) {
                        Text("${report.overall}", style = ProType.cardTitle, fontSize = 17.sp, color = c.text)
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Overall Readiness", style = ProType.label, color = c.text)
                    Text(overallLabel, style = ProType.cardTitle, fontSize = 18.sp, color = overallColor)
                    Text(
                        when {
                            report.overall < 50 -> "Consider a deload or light workout today."
                            report.overall > 90 -> "Great time to push hard."
                            else -> "Systemic recovery ${report.systemicPct.toInt()}% — steady session recommended."
                        },
                        style = ProType.small, color = c.text3,
                    )
                }
            }
        }

        // category sections
        report.categorySummary.forEach { (category, muscles) ->
            val readyCount = muscles.count { it.status == "ready" }
            val soonCount = muscles.count { it.status == "soon" }
            val restingCount = muscles.count { it.status == "resting" }
            val neverCount = muscles.count { it.status == "never" }
            val catTitle = when (category) {
                "major" -> "Major Muscle Groups"
                "longevity" -> "Longevity & Joint Health"
                "grip" -> "Grip & Hand"
                else -> "Foot & Ankle"
            }
            GlassCard(padding = PaddingValues(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(catTitle, style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallPill("$readyCount ready", c.ok)
                    SmallPill("$soonCount soon", c.warn)
                    SmallPill("$restingCount resting", c.text3)
                    if (neverCount > 0) SmallPill("$neverCount never", c.bad)
                }
                Spacer(Modifier.height(10.dp))
                muscles.forEach { m ->
                    RecoveryMuscleRow(m)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // recommendations
        GlassCard(padding = PaddingValues(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                com.peakform.fitness.ui.FaIcon("fa-lightbulb", size = 15.sp, tint = c.warn)
                Spacer(Modifier.width(8.dp))
                Text("Recommendations", style = ProType.label, color = c.text)
            }
            Spacer(Modifier.height(8.dp))
            recommendations.take(5).forEach { r ->
                Text("• $r", style = ProType.small, color = c.text2, modifier = Modifier.padding(vertical = 3.dp))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            P4Button("Generate Next Workout", icon = "fa-bolt", style = BtnStyle.PRIMARY, modifier = Modifier.weight(1f)) {
                onRequireGenerate("normal")
            }
            P4Button("Longevity Workout", icon = "fa-user-md", style = BtnStyle.SUCCESS, modifier = Modifier.weight(1f)) {
                onRequireGenerate("longevity")
            }
        }
        P4Button("Back to Dashboard", icon = "fa-arrow-left", style = BtnStyle.GHOST, modifier = Modifier.fillMaxWidth()) {
            onOpenSection("dashboard")
        }
        Spacer(Modifier.height(120.dp))
    }
}

@Composable
fun SmallPill(text: String, tint: androidx.compose.ui.graphics.Color) {
    val c = LocalProColors.current
    Box(Modifier.clip(RoundedCornerShape(999.dp)).background(tint.copy(alpha = 0.14f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(text, fontSize = 10.5.sp, color = tint, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
    }
}

@Composable
fun RecoveryMuscleRow(m: Stats.MuscleRecovery) {
    val c = LocalProColors.current
    val barColor = when (m.status) {
        "ready" -> c.ok
        "soon" -> c.warn
        else -> c.bad
    }
    val statusText = when (m.status) {
        "ready" -> "✓ Ready"
        "soon" -> "⏳ Soon (in ${Math.ceil(m.daysLeft).toInt()}d)"
        "resting" -> "💤 Resting (in ${Math.ceil(m.daysLeft).toInt()}d)"
        else -> "Never trained"
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(m.display.replaceFirstChar { it.uppercase() }, style = ProType.small, color = c.text, modifier = Modifier.weight(1f))
            Text(
                if (m.lastTrainedDays == null) "—" else "${m.lastTrainedDays!!.toInt()}d ago",
                style = ProType.small, color = c.text3,
            )
            Spacer(Modifier.width(8.dp))
            Box(Modifier.width(70.dp)) {
                ProgressBar(m.recoveryPct, barColor, height = 8)
            }
            Spacer(Modifier.width(10.dp))
            Text(statusText, style = ProType.small, fontSize = 10.sp, color = barColor, textAlign = TextAlign.End)
        }
    }
}
