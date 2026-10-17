package com.peakform.fitness.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peakform.fitness.coach.AuditFinding
import com.peakform.fitness.coach.MetaCoach
import com.peakform.fitness.coach.MetaCoachReport
import com.peakform.fitness.coach.Severity
import com.peakform.fitness.ui.components.BtnStyle
import com.peakform.fitness.ui.components.P4Button

/**
 * BATCH-41 — MetaCoach audit dialog.
 *
 * Source: source/docs/system/16-the-meta-coach.md and
 * source/docs/architecture/20-the-meta-coach.md.
 *
 * Renders the result of [MetaCoach.audit] in a scrollable dialog. The
 * dialog is reached from the "Audit" tile in [SHEET_CELLS] (visible
 * only when [com.peakform.fitness.BuildConfig.DEBUG] is true).
 *
 * The dialog never auto-corrects anything — it only reports. The
 * "Re-run" button calls [MetaCoach.audit] again; the "Close" button
 * dismisses the dialog and clears the cached findings.
 *
 * Findings are grouped by severity (CRITICAL first, then WARN, then
 * INFO). Within a severity group, they are sorted by category then by
 * coachId for deterministic output.
 */
@Composable
fun MetaCoachAuditDialog(
    onDismiss: () -> Unit,
) {
    val c = LocalProColors.current
    var report by remember { mutableStateOf<MetaCoachReport?>(MetaCoach.findings) }
    var running by remember { mutableStateOf(false) }

    fun runAudit() {
        running = true
        // The audit is fast enough (~50-200ms) to run on the main thread
        // for an on-demand invocation. The boot-time audit runs on a
        // background coroutine via MetaCoach.runAtBoot.
        val r = MetaCoach.audit()
        report = r
        running = false
    }

    // Lazily run the audit once on first composition so the dialog
    // shows something useful even when the user has never opened it
    // before. Subsequent openings reuse the cached report until the
    // user taps "Re-run".
    LaunchedEffect(Unit) {
        if (report == null) runAudit()
    }

    Box(
        Modifier.fillMaxSize().background(c.bg.copy(alpha = 0.6f)).clickable { onDismiss() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(c.glass2)
                .border(1.dp, c.hairline2, RoundedCornerShape(20.dp))
                .clickable(enabled = false) { /* swallow */ }
                .padding(16.dp),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Meta-coach audit",
                    style = ProType.cardTitle,
                    color = c.text,
                    modifier = Modifier.weight(1f),
                )
                if (running) {
                    Text("running…", style = ProType.small, color = c.text3)
                } else {
                    val r = report
                    val statusColor = if (r == null) c.text3 else if (r.healthy) c.ok else c.warn
                    val statusText = if (r == null) "—" else if (r.healthy) "healthy" else "${r.findings.size} finding(s)"
                    Text(statusText, style = ProType.small, color = statusColor)
                }
            }
            Spacer(Modifier.height(8.dp))
            val r = report
            if (r == null) {
                Text("Running audit…", style = ProType.body2, color = c.text3)
            } else if (r.findings.isEmpty()) {
                Text(
                    "No findings. All 31 authored coaches pass envelope, provenance, coverage, contradiction, and dossier checks.",
                    style = ProType.body2, color = c.text2,
                )
            } else {
                val grouped = r.findings
                    .sortedWith(compareBy({ it.severity.ordinal }, { it.category }, { it.coachId ?: "" }))
                    .groupBy { it.severity }
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (sev in listOf(Severity.CRITICAL, Severity.WARN, Severity.INFO)) {
                        val list = grouped[sev] ?: continue
                        Text(
                            "${sev.name} (${list.size})",
                            style = ProType.cardTitle,
                            color = when (sev) {
                                Severity.CRITICAL -> c.warn
                                Severity.WARN -> c.warn
                                Severity.INFO -> c.text3
                            },
                            fontSize = 13.sp,
                        )
                        for (f in list) {
                            FindingRow(f, c)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                P4Button(
                    "Re-run",
                    onClick = { runAudit() },
                    icon = "fa-rotate",
                    style = BtnStyle.SECONDARY,
                    modifier = Modifier.weight(1f),
                )
                P4Button(
                    "Close",
                    onClick = {
                        MetaCoach.clearFindings()
                        onDismiss()
                    },
                    icon = "fa-xmark",
                    style = BtnStyle.PRIMARY,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FindingRow(f: AuditFinding, c: ProColors) {
    val sevColor = when (f.severity) {
        Severity.CRITICAL -> c.warn
        Severity.WARN -> c.warn
        Severity.INFO -> c.text3
    }
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(c.glass)
            .border(1.dp, c.hairline2, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(f.category, style = ProType.small, color = sevColor, fontSize = 10.sp)
            Spacer(Modifier.width(8.dp))
            if (f.coachId != null) {
                Text(f.coachId, style = ProType.small, color = c.text3, fontSize = 10.sp)
                if (f.exerciseId != null) {
                    Text("·", style = ProType.small, color = c.text3, fontSize = 10.sp)
                }
            }
            if (f.exerciseId != null) {
                Text(f.exerciseId, style = ProType.small, color = c.text3, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(f.message, style = ProType.body2, color = c.text, fontSize = 12.sp, textAlign = TextAlign.Start)
    }
}
