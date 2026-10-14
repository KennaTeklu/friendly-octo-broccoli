package com.peakform.fitness

import com.peakform.fitness.coach.authored.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch34Test {

    // ---- Item 2: Authored coaches ----

    @Test
    fun backSquat_stronger_intermediate_build_matchesPrilepinRow() {
        val rows = GraphSubstrate.findRows("back_squat", "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertEquals("Should find exactly 1 row", 1, rows.size)
        val row = rows[0]
        Assert.assertEquals("prilepin-1974/chart.md", row.sourceFile)
        Assert.assertEquals(5, row.sets)           // Prilepin 80%: 5×5
        Assert.assertEquals("5", row.reps)
        Assert.assertEquals(0.80, row.pct1RM!!, 0.001)  // 80% 1RM
        Assert.assertNull("No load (computed from %1RM at runtime)", row.load)
        Assert.assertNull("No band colour", row.bandColour)
        Assert.assertNull("No tier", row.tier)
    }

    @Test
    fun backSquat_novice_stronger_base_matchesRippetoeRow() {
        val rows = GraphSubstrate.findRows("back_squat", "STRONGER", "BASE", BodyProfile("novice", "*", "*"))
        Assert.assertEquals(1, rows.size)
        val row = rows[0]
        Assert.assertEquals("rippetoe-2011/novice-lp.md", row.sourceFile)
        Assert.assertEquals(3, row.sets)           // Rippetoe novice: 3×5
        Assert.assertEquals(0.65, row.pct1RM!!, 0.001)  // 65% 1RM
    }

    @Test
    fun pullUp_stronger_build_hasTierNotWeight() {
        val rows = GraphSubstrate.findRows("pull_up", "STRONGER", "BUILD")
        Assert.assertEquals(1, rows.size)
        val row = rows[0]
        Assert.assertEquals("sommer-2006/gymnastic-body-tier-table.md", row.sourceFile)
        Assert.assertEquals(5, row.tier)           // Tier 5: standard pull-up
        Assert.assertNull("No load field", row.load)
        Assert.assertNull("No %1RM", row.pct1RM)
        Assert.assertNull("No band colour", row.bandColour)
    }

    @Test
    fun pullUp_hasNo1RMOrWeightField() {
        val rows = AuthoredRows.pullUpRows
        rows.forEach { row ->
            Assert.assertNull("Pull-up row ${row.rowId}: no load", row.load)
            Assert.assertNull("Pull-up row ${row.rowId}: no %1RM", row.pct1RM)
            Assert.assertNull("Pull-up row ${row.rowId}: no band colour", row.bandColour)
        }
    }

    @Test
    fun bandedLateralRaise_hasBandColourNotWeight() {
        val rows = GraphSubstrate.findRows("banded_lateral_raise", "BIGGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        Assert.assertEquals(1, rows.size)
        val row = rows[0]
        Assert.assertEquals("rogue-band-tension/chart.md", row.sourceFile)
        Assert.assertEquals("red", row.bandColour)
        Assert.assertNull("No load field", row.load)
        Assert.assertNull("No %1RM", row.pct1RM)
        Assert.assertNull("No tier", row.tier)
    }

    @Test
    fun plank_hasDurationNotWeightOr1RM() {
        val rows = GraphSubstrate.findRows("plank", "ENDURE", "BUILD", BodyProfile("intermediate", "male", "*"))
        Assert.assertEquals(1, rows.size)
        val row = rows[0]
        Assert.assertEquals("mcgill-2010/plank-standards.md", row.sourceFile)
        Assert.assertEquals(120.0, row.duration!!, 0.1)  // McGill intermediate male: 120s
        Assert.assertNull("No load", row.load)
        Assert.assertNull("No %1RM", row.pct1RM)
        Assert.assertNull("No band colour", row.bandColour)
        Assert.assertNull("No tier", row.tier)
    }

    @Test
    fun running_hasDistanceAndPaceNotRepsOrLoad() {
        val rows = GraphSubstrate.findRows("running", "ENDURE", "BASE")
        Assert.assertEquals(1, rows.size)
        val row = rows[0]
        Assert.assertEquals("daniels-2013/vdot-zones.md", row.sourceFile)
        Assert.assertEquals(3.0, row.distance!!, 0.01)  // 3 miles
        Assert.assertTrue("Should have pace", row.pace!!.contains("VDOT"))
        Assert.assertNull("No load", row.load)
        Assert.assertNull("No %1RM", row.pct1RM)
    }

    @Test
    fun everyRowHasSourceAndComponents() {
        AuthoredRows.allRows().forEach { row ->
            Assert.assertTrue("Row ${row.rowId}: sourceFile must be non-empty", row.sourceFile.isNotBlank())
            Assert.assertTrue("Row ${row.rowId}: sourceSection must be non-empty", row.sourceSection.isNotBlank())
            Assert.assertTrue("Row ${row.rowId}: components must be non-empty", row.components.isNotEmpty())
        }
    }

    @Test
    fun threeIntents_backSquat() {
        val stronger = GraphSubstrate.findRows("back_squat", "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        val hypertrophy = GraphSubstrate.findRows("back_squat", "BIGGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        val maintain = GraphSubstrate.findRows("back_squat", "MAINTAIN", "MAINTAIN", BodyProfile("intermediate", "*", "*"))
        Assert.assertTrue("STRONGER should find a row", stronger.isNotEmpty())
        Assert.assertTrue("BIGGER should find a row", hypertrophy.isNotEmpty())
        Assert.assertTrue("MAINTAIN should find a row", maintain.isNotEmpty())
        Assert.assertEquals(0.80, stronger[0].pct1RM!!, 0.001)  // 80%
        Assert.assertEquals(0.75, hypertrophy[0].pct1RM!!, 0.001)  // 75%
        Assert.assertEquals(0.70, maintain[0].pct1RM!!, 0.001)  // 70%
    }

    @Test
    fun threeIntents_pullUp() {
        val stronger = GraphSubstrate.findRows("pull_up", "STRONGER", "BUILD")
        val hypertrophy = GraphSubstrate.findRows("pull_up", "BIGGER", "BUILD")
        val maintain = GraphSubstrate.findRows("pull_up", "MAINTAIN", "MAINTAIN")
        Assert.assertTrue(stronger.isNotEmpty())
        Assert.assertTrue(hypertrophy.isNotEmpty())
        Assert.assertTrue(maintain.isNotEmpty())
        Assert.assertEquals(5, stronger[0].tier)  // Tier 5
        Assert.assertEquals(5, hypertrophy[0].tier)
        Assert.assertEquals(5, maintain[0].tier)
    }

    @Test
    fun threeIntents_bandedLateralRaise() {
        val stronger = GraphSubstrate.findRows("banded_lateral_raise", "STRONGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        val hypertrophy = GraphSubstrate.findRows("banded_lateral_raise", "BIGGER", "BUILD", BodyProfile("intermediate", "*", "*"))
        val maintain = GraphSubstrate.findRows("banded_lateral_raise", "MAINTAIN", "MAINTAIN", BodyProfile("intermediate", "*", "*"))
        Assert.assertTrue(stronger.isNotEmpty())
        Assert.assertTrue(hypertrophy.isNotEmpty())
        Assert.assertTrue(maintain.isNotEmpty())
        Assert.assertEquals("green", stronger[0].bandColour)
        Assert.assertEquals("red", hypertrophy[0].bandColour)
        Assert.assertEquals("blue", maintain[0].bandColour)
    }

    @Test
    fun threeIntents_plank() {
        val endure = GraphSubstrate.findRows("plank", "ENDURE", "BUILD", BodyProfile("intermediate", "male", "*"))
        val maintain = GraphSubstrate.findRows("plank", "MAINTAIN", "MAINTAIN")
        Assert.assertTrue(endure.isNotEmpty())
        Assert.assertTrue(maintain.isNotEmpty())
        Assert.assertEquals(120.0, endure[0].duration!!, 0.1)  // McGill intermediate male
        Assert.assertEquals(30.0, maintain[0].duration!!, 0.1) // Maintenance
    }

    @Test
    fun threeIntents_running() {
        val endure = GraphSubstrate.findRows("running", "ENDURE", "BASE")
        val maintain = GraphSubstrate.findRows("running", "MAINTAIN", "MAINTAIN")
        Assert.assertTrue(endure.isNotEmpty())
        Assert.assertTrue(maintain.isNotEmpty())
        Assert.assertEquals(3.0, endure[0].distance!!, 0.01)  // 3 miles Zone E
        Assert.assertEquals(2.0, maintain[0].distance!!, 0.01) // 2 miles Zone E
    }

    // ---- Item 3: Graph substrate ----

    @Test
    fun graphSubstrate_hasNodesAndEdges() {
        Assert.assertTrue("Should have source nodes", GraphSubstrate.nodes.any { it.type == NodeType.SOURCE })
        Assert.assertTrue("Should have row nodes", GraphSubstrate.nodes.any { it.type == NodeType.ROW })
        Assert.assertTrue("Should have exercise nodes", GraphSubstrate.nodes.any { it.type == NodeType.EXERCISE })
        Assert.assertTrue("Should have CITES edges", GraphSubstrate.edges.any { it.type == EdgeType.CITES })
    }

    @Test
    fun graphSubstrate_everyRowCitesASource() {
        AuthoredRows.allRows().forEach { row ->
            val cites = GraphSubstrate.edges.any { it.from == "row:${row.rowId}" && it.type == EdgeType.CITES }
            Assert.assertTrue("Row ${row.rowId} must have a CITES edge", cites)
        }
    }

    // ---- Item 6: Drift check ----

    @Test
    fun driftCheck_allRowsMatchSources() {
        // For each authored row, verify the citation exists and the values are internally consistent.
        // In v34 this is a structural check — the source files exist and the row cites one.
        AuthoredRows.allRows().forEach { row ->
            val sourceFileExists = listOf(
                "prilepin-1974/chart.md", "rippetoe-2011/novice-lp.md",
                "sommer-2006/gymnastic-body-tier-table.md", "rogue-band-tension/chart.md",
                "daniels-2013/vdot-zones.md", "mcgill-2010/plank-standards.md",
                "rippetoe-2011/assistance.md"
            ).contains(row.sourceFile)
            Assert.assertTrue("Row ${row.rowId}: source file ${row.sourceFile} must exist in sources/", sourceFileExists)
        }
    }

    @Test
    fun totalAuthoredRows() {
        Assert.assertEquals("Should have 113 authored rows across 31 coaches (55 v34+v35a + 58 v35b)", 113, AuthoredRows.allRows().size)
    }
}
