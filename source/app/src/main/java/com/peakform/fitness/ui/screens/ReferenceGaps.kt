package com.peakform.fitness.ui.screens

/**
 * ReferenceGaps — BATCH-18B.
 *
 * Lists the gaps between the native port and the legacy HTML reference app
 * (docs/reference/) that have been intentionally deferred or are out of scope
 * for v2.0.0. Surfaced as a UI screen so QA can verify the gaps are
 * deliberate, not lost work.
 *
 * Each gap is tagged: deferred (will land later), out-of-scope (no plan), or
 * resolved (was a gap, now shipped).
 *
 * Source of truth: docs/reference-notes/15-reference-gaps.md.
 */
object ReferenceGaps {
    data class Gap(
        val id: String,
        val title: String,
        val category: String,    // UI / Engine / Data / Settings
        val status: Status,
        val note: String,
    )
    enum class Status { DEFERRED, OUT_OF_SCOPE, RESOLVED }

    val gaps: List<Gap> = listOf(
        Gap("rg_01", "Real-time collaborative editing (two profiles editing the same workout)", "Engine", Status.OUT_OF_SCOPE, "Out of scope — multi-user collab needs a server."),
        Gap("rg_02", "Apple Health import (iOS)", "Data", Status.OUT_OF_SCOPE, "Out of scope — Android-only app."),
        Gap("rg_03", "Voice-activated set logging (\"log 135 for 8\")", "Engine", Status.DEFERRED, "Deferred — voice pipelines need on-device ASR; will land in v2.1."),
        Gap("rg_04", "Animated exercise demo GIFs in detail sheet", "UI", Status.DEFERRED, "Deferred — licensing stock GIFs is heavy; bundle is 12MB without them."),
        Gap("rg_05", "Cloud sync via Google Drive folder pick", "Data", Status.DEFERRED, "Deferred — needs GMS sign-in. User can still manually export to Drive."),
        Gap("rg_06", "Live workout share-link with overlay replay", "Engine", Status.OUT_OF_SCOPE, "Out of scope — privacy first; no network egress."),
        Gap("rg_07", "On-device pose estimation for form feedback", "Engine", Status.DEFERRED, "Deferred — MLKit pose detection is 30MB+; pilot in v2.2."),
        Gap("rg_08", "Wearable (WearOS) companion app", "UI", Status.DEFERRED, "Deferred — separate APK target."),
        Gap("rg_09", "Rest timer with smart-watch handoff", "Engine", Status.DEFERRED, "Deferred — needs WearOS bridge."),
        Gap("rg_10", "Per-muscle training load chart (last 12 weeks)", "UI", Status.RESOLVED, "Resolved in v2.0 — Progress screen ComponentRadar."),
        Gap("rg_11", "Competition day countdown widget", "UI", Status.RESOLVED, "Resolved in v2.0 — CompetitionDate in GapClosures."),
        Gap("rg_12", "Health Connect integration (weight, steps)", "Data", Status.RESOLVED, "Resolved — HealthConnect client in core/."),
    )

    fun pending(): List<Gap> = gaps.filter { it.status != Status.RESOLVED }
    fun resolved(): List<Gap> = gaps.filter { it.status == Status.RESOLVED }
    fun countByStatus(): Map<Status, Int> = gaps.groupingBy { it.status }.eachCount()
}

/** Simple compositor that prints the gap table for QA / screenshots. */
fun ReferenceGaps.markdown(): String {
    val sb = StringBuilder()
    sb.appendLine("# Reference Gaps (v2.0.0)")
    sb.appendLine()
    sb.appendLine("| ID | Title | Category | Status | Note |")
    sb.appendLine("|----|-------|----------|--------|------|")
    gaps.forEach { g ->
        sb.appendLine("| ${g.id} | ${g.title} | ${g.category} | ${g.status} | ${g.note} |")
    }
    sb.appendLine()
    sb.appendLine("Total: ${gaps.size} — ${countByStatus()[ReferenceGaps.Status.RESOLVED] ?: 0} resolved, ${countByStatus()[ReferenceGaps.Status.DEFERRED] ?: 0} deferred, ${countByStatus()[ReferenceGaps.Status.OUT_OF_SCOPE] ?: 0} out-of-scope.")
    return sb.toString()
}
