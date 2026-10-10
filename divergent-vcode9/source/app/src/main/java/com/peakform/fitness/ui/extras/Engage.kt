package com.peakform.fitness.ui.extras

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.peakform.fitness.core.Badges
import com.peakform.fitness.core.ClipboardShare
import com.peakform.fitness.core.Motivation
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.ShareKit
import com.peakform.fitness.core.StreakFire
import com.peakform.fitness.core.Vocab
import com.peakform.fitness.ui.FaIcon
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.SectionTitle
import kotlin.math.cos
import kotlin.math.sin

/** ShareScreen — QR code + witty shareable text + system share sheet (legacy QR modal + generator). */
@Composable
fun ShareScreen(onBack: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var text by remember { mutableStateOf("") }
    var qr by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        val signals = ShareKit.signals(ProState.data, ProState.currentWorkout, System.currentTimeMillis())
        text = ShareKit.buildShareText(ctx, ProState.data, ProState.currentWorkout, signals, System.currentTimeMillis())
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-share-nodes", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Share workout", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        if (ProState.currentWorkout == null) {
            Text("No active workout to share — generate one first.", style = ProType.body2, color = c.text2)
        } else {
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(14.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (qr != null) {
                    Image(bitmap = qr!!.asImageBitmap(), contentDescription = "Workout QR code", modifier = Modifier.size(200.dp))
                } else {
                    Text("Generating QR…", color = Color.Black, style = ProType.small)
                }
            }
            LaunchedEffect(text) {
                if (text.isNotBlank() && qr == null) {
                    qr = com.peakform.fitness.core.QR.bitmap(text.take(600), 400, 0xFF000000.toInt(), 0xFFFFFFFF.toInt())
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Scan to import the workout text on any phone", style = ProType.small, color = c.text3)
            Spacer(Modifier.height(14.dp))
            SectionTitle("fa-quote-left", "What will be copied")
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(c.surface2)
                    .padding(12.dp),
            ) {
                Text(text, style = ProType.small, color = c.text2)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                P4Button("Copy text", onClick = {
                    ClipboardShare.copy(ctx, text, "workout share")
                }, style = BtnStyle.SECONDARY)
                P4Button("Share…", onClick = {
                    ClipboardShare.systemShare(ctx, text)
                }, style = BtnStyle.PRIMARY)
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}

/** MuscleWikiScreen — Wikipedia lookup with fallback search buttons (legacy muscle tags). */
@Composable
fun MuscleWikiScreen(muscle: String?, onBack: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val display = (muscle ?: "").replace('_', ' ')
    var summary by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("Looking up $display on Wikipedia…") }

    LaunchedEffect(muscle) {
        if (muscle.isNullOrBlank()) return@LaunchedEffect
        summary = null
        status = "Looking up $display on Wikipedia…"
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val term = Uri.encode(display)
                val url = "https://en.wikipedia.org/api/rest_v1/page/summary/$term"
                val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                conn.connectTimeout = 6000
                conn.readTimeout = 6000
                conn.setRequestProperty("User-Agent", "Pro/1.3.0 (Android)")
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val obj = com.peakform.fitness.core.ProJson.json
                        .parseToJsonElement(body).jsonObject
                    val extract = obj["extract"] as? kotlinx.serialization.json.JsonPrimitive
                    summary = extract?.contentOrNull
                    status = if (summary.isNullOrBlank()) "No Wikipedia summary for \"$display\"." else "From Wikipedia"
                } else {
                    status = "Wikipedia returned ${conn.responseCode} for \"$display\"."
                }
                conn.disconnect()
            } catch (e: Exception) {
                status = "Lookup failed (${e.message ?: "offline"}). Try a fallback search:"
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-book-atlas", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Muscle Wiki — $display", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Text(status, style = ProType.small, color = c.text3)
        summary?.let {
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.surface2).padding(12.dp)) {
                Text(it, style = ProType.body2, color = c.text2)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Fallback searches", style = ProType.cardTitle, color = c.text)
        Spacer(Modifier.height(8.dp))
        FallbackRow("Wikipedia (browser)", "https://en.wikipedia.org/w/index.php?search=${Uri.encode(display)}+muscle", ctx)
        FallbackRow("How to do (Google)", "https://www.google.com/search?q=${Uri.encode("how to do " + display + " exercise")}", ctx)
        FallbackRow("Video (YouTube)", "https://www.youtube.com/results?search_query=${Uri.encode(display + " exercise form")}", ctx)
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun FallbackRow(label: String, url: String, ctx: android.content.Context) {
    val c = LocalProColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.surface2)
            .clickable {
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FaIcon("fa-arrow-up-right-from-square", size = 12.sp, tint = c.accent)
        Spacer(Modifier.width(8.dp))
        Text(label, style = ProType.label, color = c.text)
    }
    Spacer(Modifier.height(6.dp))
}

/** GlossaryScreen — vocabulary word list at the current reading level (legacy glossary overlay). */
@Composable
fun GlossaryScreen(onBack: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var query by remember { mutableStateOf("") }
    var level by remember { mutableIntStateOf(Vocab.level(ctx)) }
    val all = remember { Vocab.keys() }
    val filtered = remember(query, all) {
        if (query.isBlank()) all.take(120) else all.filter { it.contains(query, ignoreCase = true) }.take(120)
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-spell-check", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Words — how the app talks", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        Text(
            "${Vocab.count()} phrases tuned · Level $level — ${Vocab.LEVELS[level - 1].name}. Showing the first ${filtered.size} keys.",
            style = ProType.small, color = c.text3, modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(1, 5, 10).forEach { l ->
                P4Button("L$l", onClick = { level = l; Vocab.setLevel(ctx, l) }, style = if (level == l) BtnStyle.PRIMARY else BtnStyle.GHOST, minHeight = 34)
            }
        }
        Spacer(Modifier.height(8.dp))
        var q by remember { mutableStateOf(TextFieldValue(query)) }
        ProTextField(q, { q = it; query = it.text }, "Search phrases…", modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filtered.size) { i ->
                val key = filtered[i]
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(c.surface2).padding(10.dp)) {
                    Text(key, style = ProType.small, color = c.text3)
                    Text(Vocab.variant(key, level), style = ProType.label, color = c.text)
                }
            }
        }
    }
}

/** QuickActionsScreen — the mobile command palette (legacy Ctrl+K palette / quick actions modal). */
@Composable
fun QuickActionsScreen(onBack: () -> Unit, onAction: (String) -> Unit) {
    val c = LocalProColors.current
    val actions = listOf(
        "generate" to ("fa-dumbbell" to "Generate today's workout"),
        "resume" to ("fa-play" to "Resume workout"),
        "longevity" to ("fa-user-md" to "Longevity workout"),
        "share" to ("fa-share-nodes" to "Share workout"),
        "snapshot" to ("fa-camera" to "Take a quick snapshot"),
        "export" to ("fa-file-export" to "Export backup"),
        "themes" to ("fa-palette" to "Change theme"),
        "words" to ("fa-spell-check" to "Adjust reading level"),
        "health" to ("fa-heart-pulse" to "Health screen"),
        "legal" to ("fa-scale-balanced" to "Legal Center"),
        "studio" to ("fa-flask-vial" to "Library Studio"),
        "badges" to ("fa-award" to "Badges"),
        "history" to ("fa-clock-rotate-left" to "History"),
        "recovery" to ("fa-heart-pulse" to "Recovery"),
        "settings" to ("fa-gear" to "Settings"),
    )
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-terminal", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Quick actions", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(actions.size) { i ->
                val (id, pair) = actions[i]
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.surface2)
                        .clickable { onAction(id) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FaIcon(pair.first, size = 14.sp, tint = c.accent)
                    Spacer(Modifier.width(10.dp))
                    Text(pair.second, style = ProType.label, color = c.text)
                }
            }
        }
    }
}

/** StreakRecoveryDialog — honor-system recovery when the streak is dead (legacy modal). */
@Composable
fun StreakRecoveryDialog(onDismiss: () -> Unit, onRecovered: (String) -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var honest by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.glass2,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Streak lost — recover it?", style = ProType.cardTitle, color = c.text) },
        text = {
            Column {
                Text(
                    "The fire is out. This is an honor-system recovery: share that you're back at it, tick the honest box, and your streak restarts at day 1 today.",
                    style = ProType.body2, color = c.text2,
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Checkbox(
                        checked = honest,
                        onCheckedChange = { honest = it },
                        colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = c.accent),
                    )
                    Text("I honestly trained today (or will before midnight).", style = ProType.small, color = c.text)
                }
            }
        },
        confirmButton = {
            P4Button("Share & recover", onClick = {
                StreakFire.recover(ctx)
                ClipboardShare.systemShare(ctx, StreakFire.SHARE_LINE)
                onDismiss()
                onRecovered("Streak recovered — day 1. Welcome back.")
            }, style = BtnStyle.PRIMARY, enabled = honest)
        },
        dismissButton = { P4Button("Not today", onClick = onDismiss, style = BtnStyle.GHOST) },
    )
}

/** BadgesScreen — full badge gallery (101 badges verbatim) with earned states. */
@Composable
fun BadgesScreen(onBack: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    var earned by remember { mutableStateOf(Badges.earned(ctx)) }
    LaunchedEffect(Unit) {
        Badges.ensure(ctx)
        val fresh = Badges.recompute(ctx)
        if (fresh.isNotEmpty()) earned = Badges.earned(ctx)
    }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-award", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("Badges — ${earned.size}/${Badges.total()}", style = ProType.cardTitle, color = c.text, modifier = Modifier.weight(1f))
            P4Button("Done", onClick = onBack, style = BtnStyle.GHOST, minHeight = 34)
        }
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Badges.allBadges().size) { i ->
                val b = Badges.allBadges()[i]
                val has = b.id in earned
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (has) c.accentSoft else c.surface2)
                        .border(1.dp, if (has) c.accent.copy(alpha = 0.5f) else c.hairline2, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(if (has) "🏅" else "🔒", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(b.name, style = ProType.label, color = if (has) c.text else c.text3)
                        Text(b.desc, style = ProType.small, color = c.text3)
                    }
                    if (has) FaIcon("fa-check", size = 13.sp, tint = c.ok)
                }
            }
        }
    }
}

/**
 * ConfettiOverlay — canvas confetti burst (legacy confetti on completion + PR).
 * Purely decorative; renders for ~2.4 s then calls onDone.
 */
@Composable
fun ConfettiOverlay(onDone: () -> Unit) {
    val c = LocalProColors.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        progress.animateTo(1f, tween(2200))
        onDone()
    }
    val particles = remember {
        List(80) {
            val angle = kotlin.random.Random.nextDouble(0.0, 2.0 * Math.PI)
            val speed = kotlin.random.Random.nextDouble(0.35, 1.0)
            Triple(angle, speed, kotlin.random.Random.nextInt(0, 4))
        }
    }
    val colors = listOf(c.accent, c.ok, c.warn, c.bad)
    Canvas(Modifier.fillMaxSize()) {
        val p = progress.value
        if (p <= 0f || p >= 1f) return@Canvas
        particles.forEach { (angle, speed, colorIdx) ->
            val dist = p * size.minDimension * speed.toFloat() * 0.9f
            val x = size.width / 2f + (cos(angle).toFloat() * dist)
            val y = size.height / 3f + (sin(angle).toFloat() * dist) + (p * p * size.height * 0.35f)
            val sizePx = 10.dp.toPx() * (1f - p * 0.5f)
            translate(left = x - sizePx / 2f, top = y - sizePx / 2f) {
                drawCircle(color = colors[colorIdx].copy(alpha = 1f - p), radius = sizePx / 2f)
            }
        }
    }
}
