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

        // SC1: 11-component radar
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-bullseye", "11-Component Radar", Modifier)
            ComponentRadar(ver.intValue)
        }

        // SC2: workout frequency strip
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-calendar-week", "Workout Frequency — 8 Weeks", Modifier)
            FrequencyStrip(ver.intValue)
        }

        // SC3: RPE trends histogram
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-gauge", "RPE Trends", Modifier)
            RpeHistogram(ver.intValue)
        }

        // SC4: records board
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-trophy", "Records Board", Modifier)
            RecordsBoard()
        }

        // SC5: goal cycle status
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-flag-checkered", "Goal Cycle", Modifier)
            val gc = ProState.data.user.goalCycle
            if (gc == null || gc.goals.isEmpty()) {
                Text("No goal cycle active. Goals arrive with your next cycle.", style = ProType.small, color = c.text3)
            } else {
                KeyValueRow("Cycle", "#${'$'}{gc.cycleNumber}")
                KeyValueRow("Started", gc.startDate.ifBlank { "—"})
                KeyValueRow("Achievement", "${'$'}{gc.achievementRate.toInt()}%")
                gc.goals.take(4).forEach { g ->
                    KeyValueRow(g.title.ifBlank { g.key.replace('_', ' ') }, "${'$'}{g.current.toInt()} / ${'$'}{g.target.toInt()} ${'$'}{g.unit}")
                }
            }
        }

        // SC7: 3-month projection
        GlassCard(padding = PaddingValues(16.dp)) {
            SectionTitle("fa-arrow-trend-up", "3-Month Projection", Modifier)
            val projected = remember(ver.intValue) { projection3Month(composite) }
            if (projected == null) {
                Text("Log a few workouts to see your projection.", style = ProType.small, color = c.text3)
            } else {
                Text(projected, style = ProType.body2, color = c.text2)
            }
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


// ---------------- 1.4 progress additions ----------------

/** SC1: 11-component radar (mpComponentRadar port). */
@Composable
fun ComponentRadar(ver: Int = 0) {
    val c = LocalProColors.current
    val values = remember(ver) {
        val sub = Stats.calculateLongevityScore().sub
        val keys = listOf(
            "cardio_endurance", "strength", "mobility", "balance", "core_stability",
            "power", "agility", "coordination", "flexibility", "muscular_endurance", "reaction_time",
        )
        keys.map { k -> (sub[k] ?: sub.entries.map { it.value }.average().takeIf { !it.isNaN() }?.toInt() ?: 40).toDouble().coerceIn(5.0, 100.0) }
    }
    Canvas(Modifier.fillMaxWidth().height(240.dp).clip(RoundedCornerShape(12.dp)).background(c.bg2)) {
        val n = values.size
        if (n < 3) return@Canvas
        val cx = size.width / 2
        val cy = size.height / 2
        val r = min(size.width, size.height) / 2 * 0.78f
        // grid rings
        for (ring in 1..4) {
            val rr = r * ring / 4
            val path = Path()
            for (i in 0..n) {
                val ang = (2 * Math.PI * (i % n) / n) - Math.PI / 2
                val x = cx + cos(ang).toFloat() * rr
                val y = cy + sin(ang).toFloat() * rr
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, c.hairline2, style = Stroke(width = 2f))
        }
        // data polygon
        val dataPath = Path()
        for (i in 0..n) {
            val idx = i % n
            val ang = (2 * Math.PI * idx / n) - Math.PI / 2
            val v = (values[idx].toFloat() / 100f).coerceIn(0.05f, 1f)
            val x = cx + cos(ang).toFloat() * r * v
            val y = cy + sin(ang).toFloat() * r * v
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        drawPath(dataPath, c.accent.copy(alpha = 0.22f))
        drawPath(dataPath, c.accent, style = Stroke(width = 4f))
    }
    val labels = listOf("Cardio", "Strength", "Mobility", "Balance", "Core", "Power", "Agility", "Coordination", "Flexibility", "Endurance", "Reaction")
    Text(labels.joinToString(" · "), style = ProType.small, color = c.text3, maxLines = 2)
}

/** SC2: workouts per week over 8 weeks. */
@Composable
fun FrequencyStrip(ver: Int = 0) {
    val c = LocalProColors.current
    val weekly = remember(ver) {
        val counts = IntArray(8)
        val now = System.currentTimeMillis()
        ProState.data.workouts.filter { it.isCompleted }.forEach { w ->
            val ms = ProState.utcDayMillis(w.date)
            if (ms > 0) {
                val weeksAgo = ((now - ms) / (7L * 24 * 3600 * 1000)).toInt()
                if (weeksAgo in 0..7) counts[7 - weeksAgo]++
            }
        }
        counts
    }
    val maxV = maxOf(1, weekly.maxOrNull() ?: 1)
    Row(Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
        weekly.forEachIndexed { i, n ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(Modifier.fillMaxWidth().height((100 * (n / maxV.toDouble())).dp.coerceAtLeast(2.dp)).clip(RoundedCornerShape(6.dp)).background(if (n > 0) c.accent else c.surface3))
                Spacer(Modifier.height(4.dp))
                Text("W${'$'}{i + 1}", fontSize = 9.sp, color = c.text3)
                Text("${'$'}n", fontSize = 10.sp, color = c.text2)
            }
        }
    }
}

/** SC3: RPE histogram over the last 20 logged sets (1–10). */
@Composable
fun RpeHistogram(ver: Int = 0) {
    val c = LocalProColors.current
    val buckets = remember(ver) {
        val b = IntArray(10)
        ProState.data.workouts.filter { it.isCompleted }.takeLast(20).forEach { w ->
            w.exercises.forEach { ex ->
                ex.actual?.rpeList?.forEach { r ->
                    val idx = (r.toInt()).coerceIn(1, 10) - 1
                    b[idx]++
                }
                ex.actual?.firstRPE?.let { r ->
                    val idx = (r.toInt()).coerceIn(1, 10) - 1
                    b[idx]++
                }
            }
        }
        b
    }
    val maxV = maxOf(1, buckets.maxOrNull() ?: 1)
    Row(Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
        buckets.forEachIndexed { i, n ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(Modifier.fillMaxWidth().height((100 * (n / maxV.toDouble())).dp.coerceAtLeast(2.dp)).clip(RoundedCornerShape(4.dp)).background(if (i >= 8) c.bad.copy(alpha = 0.75f) else if (i >= 6) c.warn.copy(alpha = 0.75f) else c.accent))
                Spacer(Modifier.height(4.dp))
                Text("${'$'}{i + 1}", fontSize = 9.sp, color = c.text3)
            }
        }
    }
    Text("Logged sets by RPE (last 20 sessions)", style = ProType.small, color = c.text3)
}

/** SC4: records board — best sets across exercises. */
@Composable
fun RecordsBoard() {
    val c = LocalProColors.current
    val records = remember {
        ProState.data.exercises.entries.mapNotNull { (id, rec) ->
            val best = rec.bestWeight ?: return@mapNotNull null
            if (best <= 0) return@mapNotNull null
            Triple(id, best, rec.bestTimes.size)
        }.sortedByDescending { it.second }.take(8)
    }
    if (records.isEmpty()) {
        Text("No records yet — log some heavy sets.", style = ProType.small, color = c.text3)
        return
    }
    records.forEachIndexed { i, (id, best, _) ->
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${'$'}{i + 1}.", style = ProType.small, color = c.text3, modifier = Modifier.width(26.dp))
            Text(id.replace('_', ' ').replaceFirstChar { it.uppercase() }, style = ProType.body2, color = c.text, modifier = Modifier.weight(1f), maxLines = 1)
            Text("${'$'}{best.toInt()} lbs", style = ProType.label, color = c.accent)
        }
    }
}

/** SC7: simple linear projection of the composite trend, 3 months out. */
private fun projection3Month(points: List<Stats.CompositePoint>): String? {
    if (points.size < 6) return null
    val n = points.size
    val lastVal = points.last().value
    val prevVal = points[(n * 3) / 4].value
    val slope = (lastVal - prevVal) / kotlin.math.max(1, n - (n * 3) / 4)
    val projected = (lastVal + slope * 12).coerceIn(0.0, 100.0)
    val direction = when {
        projected > lastVal + 2 -> "trending up"
        projected < lastVal - 2 -> "trending down"
        else -> "holding steady"
    }
    return "Composite score is $direction — projected ${'$'}{projected.toInt()}/100 in 3 months (now ${'$'}{lastVal.toInt()})."
}
