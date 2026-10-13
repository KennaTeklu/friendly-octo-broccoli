package com.peakform.fitness

import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch34bTest {

    // ---- Item 2: Authored coaches ----

    @Test
    fun backSquat_reconcile_selectsCorrectRow() {
        val rows = AuthoredRows.backSquatRows
        val p = BackSquatAuthoredCoach.reconcile(rows, "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertNotNull("Should have sourceRowId", p.sourceRowId)
        Assert.assertEquals("bs_int_stronger_build", p.sourceRowId)
        Assert.assertEquals(5, p.sets)
        Assert.assertEquals("5", p.reps)
        Assert.assertEquals(0.80, p.pct1RM!!, 0.001)
    }

    @Test
    fun backSquat_reconcile_noMatch_returnsEmptyWithNote() {
        val rows = AuthoredRows.backSquatRows
        val p = BackSquatAuthoredCoach.reconcile(rows, "SKILL", "PEAK", null)
        Assert.assertNull("No sourceRowId", p.sourceRowId)
        Assert.assertTrue("Note should explain no row", p.note.contains("No source row"))
        Assert.assertEquals(0, p.sets)
    }

    @Test
    fun pullUp_reconcile_hasTierNotWeight() {
        val p = PullUpAuthoredCoach.reconcile(AuthoredRows.pullUpRows, "STRONGER", "BUILD", null)
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(5, p.tier)
        Assert.assertNull("No load", p.load)
        Assert.assertNull("No pct1RM", p.pct1RM)
    }

    @Test
    fun bandedLateralRaise_reconcile_hasBandColourNotWeight() {
        val p = BandedLateralRaiseAuthoredCoach.reconcile(AuthoredRows.bandedLateralRaiseRows, "BIGGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals("red", p.bandColour)
        Assert.assertNull("No load", p.load)
        Assert.assertNull("No pct1RM", p.pct1RM)
    }

    @Test
    fun plank_reconcile_hasDurationNotWeight() {
        val p = PlankAuthoredCoach.reconcile(AuthoredRows.plankRows, "ENDURE", "BUILD", BodyProfile("intermediate", "male", "*"))
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(120.0, p.duration!!, 0.1)
        Assert.assertNull("No load", p.load)
        Assert.assertNull("No pct1RM", p.pct1RM)
    }

    @Test
    fun running_reconcile_hasDistanceAndPace() {
        val p = RunningAuthoredCoach.reconcile(AuthoredRows.runningRows, "ENDURE", "BASE", null)
        Assert.assertNotNull(p.sourceRowId)
        Assert.assertEquals(3.0, p.distance!!, 0.01)
        Assert.assertTrue(p.pace!!.contains("VDOT"))
        Assert.assertNull("No load", p.load)
        Assert.assertNull("No reps", p.reps.takeIf { it.isNotBlank() && it != "continuous" })
    }

    @Test
    fun everyCoach_hasRequirementsEnvelopeProvenanceVersion() {
        val coaches = listOf(BackSquatAuthoredCoach, PullUpAuthoredCoach, BandedLateralRaiseAuthoredCoach, PlankAuthoredCoach, RunningAuthoredCoach)
        for (c in coaches) {
            Assert.assertNotNull("${c.movementName}: requirements()", c.requirements())
            Assert.assertNotNull("${c.movementName}: envelope", c.envelope)
            Assert.assertNotNull("${c.movementName}: provenance", c.provenance)
            Assert.assertTrue("${c.movementName}: version > 0", c.version > 0)
        }
    }

    @Test
    fun backSquat_requirements_has1RMAndWeight() {
        val reqs = BackSquatAuthoredCoach.requirements()
        Assert.assertTrue("needs1RM", reqs.needs1RM)
        Assert.assertTrue("needsWeight", reqs.needsWeight)
        Assert.assertFalse("not needsBand", reqs.needsBand)
    }

    @Test
    fun pullUp_requirements_hasBodyweightOnly() {
        val reqs = PullUpAuthoredCoach.requirements()
        Assert.assertTrue("needsBodyweight", reqs.needsBodyweight)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
    }

    @Test
    fun banded_requirements_hasBandOnly() {
        val reqs = BandedLateralRaiseAuthoredCoach.requirements()
        Assert.assertTrue("needsBand", reqs.needsBand)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
    }

    @Test
    fun plank_requirements_hasDurationOnly() {
        val reqs = PlankAuthoredCoach.requirements()
        Assert.assertTrue("needsDuration", reqs.needsDuration)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
    }

    @Test
    fun running_requirements_hasDistanceAndPace() {
        val reqs = RunningAuthoredCoach.requirements()
        Assert.assertTrue("needsDistance", reqs.needsDistance)
        Assert.assertTrue("needsPace", reqs.needsPace)
        Assert.assertFalse("not needs1RM", reqs.needs1RM)
        Assert.assertFalse("not needsWeight", reqs.needsWeight)
    }

    // ---- Item 3: Graph substrate ----

    @Test
    fun graph_hasRequirementNodes() {
        Assert.assertTrue("Should have Requirement nodes", GraphSubstrate.nodes.any { it.type == NodeType.REQUIREMENT })
    }

    @Test
    fun graph_hasEnvelopeBoundNodes() {
        Assert.assertTrue("Should have EnvelopeBound nodes", GraphSubstrate.nodes.any { it.type == NodeType.ENVELOPE_BOUND })
    }

    @Test
    fun graph_hasDeclaresEdges() {
        Assert.assertTrue("Should have DECLARES edges", GraphSubstrate.edges.any { it.type == EdgeType.DECLARES })
    }

    @Test
    fun graph_hasBoundsEdges() {
        Assert.assertTrue("Should have BOUNDS edges", GraphSubstrate.edges.any { it.type == EdgeType.BOUNDS })
    }

    @Test
    fun graph_hasBodyProfileNodes() {
        val bpNodes = GraphSubstrate.nodes.filter { it.type == NodeType.BODY_PROFILE }
        Assert.assertEquals("Should have 36 body profile nodes", 36, bpNodes.size)
    }

    // ---- Item 6: Real drift check ----

    @Test
    fun driftCheck_readsSourceFilesAndComparesValues() {
        val drifts = mutableListOf<String>()
        for (row in AuthoredRows.allRows()) {
            // Read the source file
            val userDir = System.getProperty("user.dir"); var dir = java.io.File(userDir); var repoRoot = dir; while (repoRoot != null && !java.io.File(repoRoot, "sources").exists() && repoRoot.parentFile != null) { repoRoot = repoRoot.parentFile }
            val sourcePath = java.io.File(repoRoot, "sources/${row.sourceFile}").absolutePath
            val sourceFile = java.io.File(sourcePath)
            if (!sourceFile.exists()) {
                drifts.add("ProDrift: Row ${row.rowId} cites ${row.sourceFile} but file not found at $sourcePath")
                continue
            }
            val sourceText = sourceFile.readText()
            // Verify the source section exists
            val sectionKey = row.sourceSection.split(" ")[0].split("%")[0]
            if (!sourceText.contains(row.sourceSection, ignoreCase = true) && !sourceText.contains(sectionKey, ignoreCase = true)) {
                drifts.add("ProDrift: Row ${row.rowId} cites section '${row.sourceSection}' not found in ${row.sourceFile}")
                continue
            }
            // For numeric fields, verify the value appears in the source file
            if (row.pct1RM != null) {
                val pct = (row.pct1RM!! * 100).toInt()
                val pctStr = pct.toString()
                // Source files use ranges like "70-79" or "80-89" — check if the value falls within any range in the text
                val ranges = Regex("(\\d+)-(\\d+)").findAll(sourceText).map { it.groupValues[1].toInt() to it.groupValues[2].toInt() }.toList()
                val inRange = sourceText.contains(pctStr) || ranges.any { (lo, hi) -> pct in lo..hi }
                if (!inRange) {
                    drifts.add("ProDrift: Row ${row.rowId} says pct1RM=${row.pct1RM} (${pctStr}%). Source ${row.sourceFile}#${row.sourceSection} does not contain '$pctStr' or a range containing it.")
                }
            }
            if (row.duration != null && row.exerciseId != "running") {
                val durStr = row.duration!!.toInt().toString()
                if (!sourceText.contains(durStr)) {
                    drifts.add("ProDrift: Row ${row.rowId} says duration=${row.duration}. Source ${row.sourceFile}#${row.sourceSection} does not contain '$durStr'.")
                }
            }
            if (row.bandColour != null) {
                if (!sourceText.contains(row.bandColour, ignoreCase = true)) {
                    drifts.add("ProDrift: Row ${row.rowId} says bandColour=${row.bandColour}. Source ${row.sourceFile}#${row.sourceSection} does not contain '${row.bandColour}'.")
                }
            }
            if (row.tier != null) {
                if (!sourceText.contains("| ${row.tier}    ") && !sourceText.contains("| ${row.tier} |")) {
                    drifts.add("ProDrift: Row ${row.rowId} says tier=${row.tier}. Source ${row.sourceFile}#${row.sourceSection} does not contain 'Tier ${row.tier}'.")
                }
            }
            if (row.distance != null) {
                val distStr = row.distance!!.toInt().toString()
                if (!sourceText.contains(distStr)) {
                    drifts.add("ProDrift: Row ${row.rowId} says distance=${row.distance}. Source ${row.sourceFile}#${row.sourceSection} does not contain '$distStr'.")
                }
            }
        }
        // Write drift report
        val reportFile = java.io.File("build/drift-report.txt")
        reportFile.parentFile?.mkdirs()
        val report = buildString {
            appendLine("=== DRIFT REPORT — v34b ===")
            appendLine("Generated: ${java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())}")
            appendLine("Rows checked: ${AuthoredRows.allRows().size}")
            appendLine("Drifts found: ${drifts.size}")
            appendLine()
            if (drifts.isEmpty()) {
                appendLine("All rows match their cited sources.")
            } else {
                drifts.forEach { appendLine(it) }
            }
        }
        reportFile.writeText(report)

        Assert.assertTrue("Drift check found ${drifts.size} drift(s): ${drifts.joinToString("; ")}", drifts.isEmpty())
    }

    @Test
    fun everyRowHasComponentsMap() {
        AuthoredRows.allRows().forEach { row ->
            Assert.assertTrue("Row ${row.rowId}: components must be non-empty", row.components.isNotEmpty())
        }
    }

    @Test
    fun totalAuthoredRows() {
        Assert.assertEquals(55, AuthoredRows.allRows().size)
    }
}
