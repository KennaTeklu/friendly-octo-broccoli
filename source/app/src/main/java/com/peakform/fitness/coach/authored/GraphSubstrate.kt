package com.peakform.fitness.coach.authored

/**
 * v34 Item 3: Graph substrate (read-only).
 * Holds every node and edge the five coaches cite.
 * Loaded at build time from AuthoredRows. Not queried at runtime by the card in v34.
 *
 * Reference: source/docs/meta/02-truth-layer.md
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

    private fun buildNodes(): List<GraphNode> {
        val n = mutableListOf<GraphNode>()
        // Source nodes — one per source file
        val sourceFiles = AuthoredRows.allRows().map { it.sourceFile }.distinct()
        sourceFiles.forEach { sf ->
            n.add(GraphNode("src:$sf", NodeType.SOURCE, sf, "Source file"))
        }
        // Row nodes — one per authored row
        AuthoredRows.allRows().forEach { row ->
            n.add(GraphNode("row:${row.rowId}", NodeType.ROW, row.rowId, "${row.exerciseId}|${row.intent}|${row.phase}|${row.trainingAge}|${row.sex}"))
        }
        // Exercise nodes
        val exercises = AuthoredRows.allRows().map { it.exerciseId }.distinct()
        exercises.forEach { ex ->
            n.add(GraphNode("ex:$ex", NodeType.EXERCISE, ex, "Exercise"))
        }
        // Body profile nodes
        listOf("novice", "intermediate", "advanced").forEach { ta ->
            listOf("male", "female", "intersex").forEach { s ->
                listOf("cut", "recomp", "bulk", "maintain").forEach { bg ->
                    n.add(GraphNode("bp:$ta:$s:$bg", NodeType.BODY_PROFILE, "$ta/$s/$bg", "Body profile"))
                }
            }
        }
        return n
    }

    private fun buildEdges(): List<GraphEdge> {
        val e = mutableListOf<GraphEdge>()
        // Cites edges: Row → Source
        AuthoredRows.allRows().forEach { row ->
            e.add(GraphEdge("row:${row.rowId}", "src:${row.sourceFile}", EdgeType.CITES))
        }
        return e
    }

    /**
     * walker.findRows(exerciseId, intent, phase, bodyProfile) → List<AuthoredRow>
     * Used by tests only in v34.
     */
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
