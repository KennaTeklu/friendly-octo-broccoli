package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.peakform.fitness.core.Chronicle
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.state.StateModel
import com.peakform.fitness.ui.*
import com.peakform.fitness.ui.components.*
import java.util.concurrent.TimeUnit

/**
 * BATCH-40 — Chronicle screen.
 *
 * Source: source/docs/system/15-the-chronicle.md (the narrator) and
 * source/docs/architecture/10-chronicle.md.
 *
 * Renders a [Chronicle.ChronicleReport] produced by [Chronicle.narrate]
 * from the active profile's state. Four sections:
 *
 *   - Headline — large, accent color.
 *   - Body — 2-4 sentences naming specifics.
 *   - Highlights — 2-5 short bullets.
 *   - Turning points — 0-3 timeline entries, small text.
 *
 * Footer line: "Generated {relative time}" — minutes / hours / days ago.
 *
 * The chronicle is not a chatbot. It does not converse. It does not pep
 * talk. It does not prescribe. It does not grade. It narrates and heals.
 * That is all. So this screen is read-only.
 */
@Composable
fun ChronicleScreen(onClose: () -> Unit) {
    val c = LocalProColors.current

    // Subscribe to ProState changes so the report rebuilds when the
    // underlying sessions/field/recovery change (e.g. after a fresh
    // appendSession).
    val ver = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val l: () -> Unit = { ver.intValue++ }
        synchronized(ProState.listeners) { ProState.listeners.add(l) }
        onDispose {
            synchronized(ProState.listeners) { ProState.listeners.remove(l) }
        }
    }
    @Suppress("unused") val _verTick = ver.intValue  // touch to subscribe

    // Snapshot the current state + narrate. Memoized on ver so the work
    // only re-runs when ProState.notifyChanged fires.
    val report = remember(ver.intValue) {
        val state = Chronicle.ProStateData(
            userId = ProState.activeProfileId,
            sessions = ProState.sessions,
            field = ProState.field,
            recovery = ProState.recovery,
        )
        Chronicle.narrate(ProState.activeProfileId, state)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon("fa-book-open", size = 17.sp, tint = c.accent)
            Spacer(Modifier.width(10.dp))
            Text("Chronicle", style = ProType.brand, color = c.text, modifier = Modifier.weight(1f))
        }

        // Headline — large, accent color.
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(c.accentSoft)
                .border(1.dp, c.accentLine, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                report.headline,
                style = ProType.sectionTitle,
                color = c.accent,
                fontSize = 20.sp,
            )
        }

        // Body — 2-4 sentences naming specifics.
        if (report.body.isNotBlank()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.surface)
                    .border(1.dp, c.hairline2, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Text(report.body, style = ProType.body, color = c.text)
            }
        }

        // Highlights — 2-5 short bullets.
        if (report.highlights.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Highlights", style = ProType.cardTitle, color = c.text2)
                report.highlights.forEach { line ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(c.surface)
                            .border(1.dp, c.hairline2, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(c.accent),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(line, style = ProType.body2, color = c.text)
                    }
                }
            }
        }

        // Turning points — 0-3 timeline entries, small text.
        if (report.turningPoints.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Turning points", style = ProType.cardTitle, color = c.text2)
                report.turningPoints.forEach { line ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        FaIcon("fa-clock", size = 10.sp, tint = c.text3)
                        Spacer(Modifier.width(8.dp))
                        Text(line, style = ProType.small, color = c.text3)
                    }
                }
            }
        }

        // Footer: "Generated {relative time}"
        Text(
            "Generated ${relativeTime(report.generatedAt)}",
            style = ProType.small,
            color = c.text3,
        )

        P4Button(
            "Close",
            style = BtnStyle.GHOST,
            modifier = Modifier.fillMaxWidth().padding(bottom = 120.dp),
            onClick = onClose,
        )
    }
}

/** Format a relative-time string for "X minutes/hours/days ago". */
private fun relativeTime(generatedAt: Long): String {
    val deltaMs = System.currentTimeMillis() - generatedAt
    if (deltaMs < 60_000L) return "just now"
    val mins = TimeUnit.MILLISECONDS.toMinutes(deltaMs)
    if (mins < 60) return "${mins}m ago"
    val hours = TimeUnit.MILLISECONDS.toHours(deltaMs)
    if (hours < 24) return "${hours}h ago"
    val days = TimeUnit.MILLISECONDS.toDays(deltaMs)
    return "${days}d ago"
}
