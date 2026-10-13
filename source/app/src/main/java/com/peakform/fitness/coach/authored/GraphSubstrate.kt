package com.peakform.fitness.coach.authored

/**
 * v34b Item 3: Graph substrate (read-only) — extended.
 * Holds every node and edge the five coaches cite.
 *
 * Node types: Source, Row, Exercise, BodyProfile, Requirement, EnvelopeBound
 * Edge types: Cites (Row→Source), Declares (Coach→Requirement), Bounds (Coach→EnvelopeBound)
 */

data class GraphNode(
    val id: String,
    val type: NodeType,
    val label: String,
    val payload: String,
)

enum class NodeType { SOURCE, ROW, EXERCISE, BODY_PROFILE, REQUIREMENT, ENVELOPE_BOUND }

data class GraphEdge(
    val from: String,
    val to: String,
    val type: EdgeType,
)

enum class EdgeType { CITES, DECLARES, BOUNDS }

object GraphSubstrate {
    val nodes: List<GraphNode> by lazy { buildNodes() }
    val edges: List<GraphEdge> by lazy { buildEdges() }

    private val coachedExercises = mapOf(
        "back_squat" to BackSquatAuthoredCoach,
        "pull_up" to PullUpAuthoredCoach,
        "banded_lateral_raise" to BandedLateralRaiseAuthoredCoach,
        "plank" to PlankAuthoredCoach,
        "running" to RunningAuthoredCoach,
    )

    private fun buildNodes(): List<GraphNode> {
        val n = mutableListOf<GraphNode>()
        // Source nodes
        AuthoredRows.allRows().map { it.sourceFile }.distinct().forEach { sf ->
            n.add(GraphNode("src:$sf", NodeType.SOURCE, sf, "Source file"))
        }
        // Row nodes
        AuthoredRows.allRows().forEach { row ->
            n.add(GraphNode("row:${row.rowId}", NodeType.ROW, row.rowId, "${row.exerciseId}|${row.intent}|${row.phase}|${row.trainingAge}|${row.sex}"))
        }
        // Exercise nodes
        AuthoredRows.allRows().map { it.exerciseId }.distinct().forEach { ex ->
            n.add(GraphNode("ex:$ex", NodeType.EXERCISE, ex, "Exercise"))
        }
        // BodyProfile nodes (3×3×4 = 36)
        listOf("novice", "intermediate", "advanced").forEach { ta ->
            listOf("male", "female", "intersex").forEach { s ->
                listOf("cut", "recomp", "bulk", "maintain").forEach { bg ->
                    n.add(GraphNode("bp:$ta:$s:$bg", NodeType.BODY_PROFILE, "$ta/$s/$bg", "Body profile"))
                }
            }
        }
        // Requirement nodes — one per CoachRequirements field
        listOf("needs1RM", "needsBand", "needsBodyweight", "needsDuration", "needsDistance", "needsPace", "needsWeight").forEach { req ->
            n.add(GraphNode("req:$req", NodeType.REQUIREMENT, req, "Requirement"))
        }
        // EnvelopeBound nodes — one per ExerciseEnvelope field
        listOf("maxSets", "minSets", "maxLoad", "maxDuration", "maxDistance", "maxPct1RM").forEach { bound ->
            n.add(GraphNode("eb:$bound", NodeType.ENVELOPE_BOUND, bound, "Envelope bound"))
        }
        return n
    }

    private fun buildEdges(): List<GraphEdge> {
        val e = mutableListOf<GraphEdge>()
        // CITES edges: Row → Source
        AuthoredRows.allRows().forEach { row ->
            e.add(GraphEdge("row:${row.rowId}", "src:${row.sourceFile}", EdgeType.CITES))
        }
        // DECLARES edges: Coach → Requirement
        coachedExercises.forEach { (exId, coach) ->
            val reqs = coach.requirements()
            if (reqs.needs1RM) e.add(GraphEdge("coach:$exId", "req:needs1RM", EdgeType.DECLARES))
            if (reqs.needsBand) e.add(GraphEdge("coach:$exId", "req:needsBand", EdgeType.DECLARES))
            if (reqs.needsBodyweight) e.add(GraphEdge("coach:$exId", "req:needsBodyweight", EdgeType.DECLARES))
            if (reqs.needsDuration) e.add(GraphEdge("coach:$exId", "req:needsDuration", EdgeType.DECLARES))
            if (reqs.needsDistance) e.add(GraphEdge("coach:$exId", "req:needsDistance", EdgeType.DECLARES))
            if (reqs.needsPace) e.add(GraphEdge("coach:$exId", "req:needsPace", EdgeType.DECLARES))
            if (reqs.needsWeight) e.add(GraphEdge("coach:$exId", "req:needsWeight", EdgeType.DECLARES))
        }
        // BOUNDS edges: Coach → EnvelopeBound
        coachedExercises.forEach { (exId, coach) ->
            e.add(GraphEdge("coach:$exId", "eb:maxSets", EdgeType.BOUNDS))
            e.add(GraphEdge("coach:$exId", "eb:minSets", EdgeType.BOUNDS))
            e.add(GraphEdge("coach:$exId", "eb:maxLoad", EdgeType.BOUNDS))
            e.add(GraphEdge("coach:$exId", "eb:maxDuration", EdgeType.BOUNDS))
            e.add(GraphEdge("coach:$exId", "eb:maxDistance", EdgeType.BOUNDS))
            e.add(GraphEdge("coach:$exId", "eb:maxPct1RM", EdgeType.BOUNDS))
        }
        return e
    }

    fun findRows(exerciseId: String, intent: String, phase: String, bodyProfile: BodyProfile? = null): List<AuthoredRow> {
        return AuthoredRows.rowsForCoach(exerciseId).filter { row ->
            row.exerciseId == exerciseId &&
            (row.intent == intent || row.intent == "*") &&
            (row.phase == phase || row.phase == "*") &&
            (bodyProfile == null || row.trainingAge == "*" || row.trainingAge == bodyProfile.trainingAge) &&
            (bodyProfile == null || row.sex == "*" || row.sex == bodyProfile.sex) &&
            (bodyProfile == null || row.bodyGoal == "*" || row.bodyGoal == bodyProfile.bodyGoal)
        }
    }
}
