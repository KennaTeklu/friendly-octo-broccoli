package com.peakform.fitness.core

import android.content.Context
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.JsonObject

/**
 * SelfAssessment — quick post-session feel tracker.
 *
 * BATCH-9: the user rates the session on three axes (effort / readiness next
 * time / soreness expectation) plus an optional free-text note. Stored
 * per-profile in SharedPreferences (key p4_self_assess_<sessionId>) so the
 * Progress screen can chart "perceived effort vs actual volume" over time.
 *
 * The legacy HTML app had a `postSessionFeelModal` that wrote to localStorage
 * under the same key shape. We mirror that for round-trip compatibility.
 */
object SelfAssessment {

    data class Feel(
        val sessionId: String,
        val atIso: String,
        val effort: Int,           // 1..10 (1 = walk in the park, 10 = PR-level grind)
        val readinessNext: Int,    // 1..10 (1 = wrecked tomorrow, 10 = ready again)
        val sorenessExpected: Int,// 1..5 (1 = nothing, 5 = DOMS bomb)
        val note: String?,
    )

    private fun key(sessionId: String) = "p4_self_assess_$sessionId"

    fun save(ctx: Context, feel: Feel) {
        val json = """{"sessionId":${escape(feel.sessionId)},"at":${escape(feel.atIso)},"effort":${feel.effort},"readinessNext":${feel.readinessNext},"soreness":${feel.sorenessExpected},"note":${if (feel.note.isNullOrBlank()) "null" else escape(feel.note)}}"""
        ProfileState.put(ctx, key(feel.sessionId), json)
    }

    fun load(ctx: Context, sessionId: String): Feel? {
        val raw = ProfileState.get(ctx, key(sessionId)) ?: return null
        return try {
            val o = ProJson.json.parseToJsonElement(raw) as? kotlinx.serialization.json.JsonObject ?: return null
            val sId = (o["sessionId"] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: sessionId
            val at = (o["at"] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: ""
            val effort = (o["effort"] as? kotlinx.serialization.json.JsonPrimitive)?.content?.toIntOrNull() ?: 5
            val readiness = (o["readinessNext"] as? kotlinx.serialization.json.JsonPrimitive)?.content?.toIntOrNull() ?: 5
            val soreness = (o["soreness"] as? kotlinx.serialization.json.JsonPrimitive)?.content?.toIntOrNull() ?: 3
            val note = (o["note"] as? kotlinx.serialization.json.JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }
            Feel(sId, at, effort, readiness, soreness, note)
        } catch (_: Exception) { null }
    }

    fun all(ctx: Context): List<Feel> = try {
        ProfileState.keys(ctx).filter { it.startsWith("p4_self_assess_") }.mapNotNull { k ->
            val sid = k.removePrefix("p4_self_assess_")
            load(ctx, sid)
        }
    } catch (_: Exception) { emptyList() }

    private fun escape(s: String): String = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
