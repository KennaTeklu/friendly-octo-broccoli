package com.peakform.fitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.peakform.fitness.core.OneRmLab
import com.peakform.fitness.core.ProState
import com.peakform.fitness.ui.LocalProColors
import com.peakform.fitness.ui.ProType
import com.peakform.fitness.ui.components.P4Button
import com.peakform.fitness.ui.components.BtnStyle

/**
 * OneRMAuditScreen — BATCH-8 1RM audit console.
 *
 * Lists every exercise that has a tested 1RM, with the estimated 1RM, delta,
 * last-tested date, and a "retest due" flag. The user can:
 *   - reset a single exercise's tested 1RM (audit fix-up),
 *   - reset every suspicious entry (>20% off the estimate).
 *
 * Reachable from Settings → "1RM Audit" button.
 */
@Composable
fun OneRMAuditScreen(onClose: () -> Unit) {
    val c = LocalProColors.current
    val ctx = LocalContext.current
    val report = remember { OneRmLab.auditAll(ctx) }
    var resetCount by remember { mutableIntStateOf(0) }
    val ver = remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            com.peakform.fitness.ui.FaIcon("fa-flask-vial", size = 18.sp, tint = c.accent)
            Spacer(Modifier.width(8.dp))
            Text("1RM Audit", style = ProType.cardTitle, color = c.text)
            Spacer(Modifier.weight(1f))
            P4Button("Close", style = BtnStyle.GHOST, onClick = onClose)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "${report.rows.size} exercises have a tested 1RM. ${report.retestDueCount} retest due, ${report.suspiciousCount} look suspicious (off the estimate by >20%).",
            style = ProType.body2, color = c.text2,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            P4Button(
                "Reset suspicious entries (${report.suspiciousCount})",
                icon = "fa-triangle-exclamation",
                style = BtnStyle.DANGER,
                modifier = Modifier.weight(1f),
                enabled = report.suspiciousCount > 0,
            ) {
                val n = OneRmLab.resetSuspicious(ctx)
                resetCount += n
                ver.intValue++
            }
            P4Button("Done", icon = "fa-check", style = BtnStyle.PRIMARY, modifier = Modifier.weight(1f), onClick = onClose)
        }
        if (resetCount > 0) {
            Spacer(Modifier.height(8.dp))
            Text("Reset $resetCount suspicious entries.", style = ProType.small, color = c.ok)
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(report.rows, key = { it.exerciseId }) { row ->
                val _tick = ver.intValue
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(c.surface2).border(1.dp, c.hairline, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(row.exerciseName, style = ProType.label, color = c.text, modifier = Modifier.weight(1f))
                        if (row.retestDue) {
                            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.warn.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text("retest due", fontSize = 10.sp, color = c.warn, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(6.dp))
                        }
                        if (row.suspicious) {
                            Box(Modifier.clip(RoundedCornerShape(999.dp)).background(c.bad.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text("suspicious", fontSize = 10.sp, color = c.bad, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Tested: ${row.tested1Rm.toInt()} lbs · Est: ${row.estimated1Rm.toInt()} lbs · Δ ${"%+.0f".format(row.deltaLbs)} lbs",
                        style = ProType.small, color = c.text3)
                    if (row.lastTestedAt != null) {
                        Text("Last tested: ${row.lastTestedAt.take(10)}", style = ProType.small, color = c.text3)
                    }
                    Spacer(Modifier.height(8.dp))
                    P4Button("Reset this 1RM", icon = "fa-rotate-left", style = BtnStyle.GHOST, minHeight = 32) {
                        OneRmLab.resetOne(ctx, row.exerciseId)
                        ver.intValue++
                    }
                }
            }
        }
    }
}
