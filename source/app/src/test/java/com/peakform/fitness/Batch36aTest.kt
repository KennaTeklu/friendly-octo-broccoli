package com.peakform.fitness

import com.peakform.fitness.engine.Library
import com.peakform.fitness.ui.components.selectComponentChips
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-36a — Component chips on cards.
 *
 * The Workout card and the Library card render the components map from
 * exercise_taxonomy.json as a row of small chips with a 5-segment weight
 * bar each. The shared composable is `ComponentChipsRow` in
 * `com.peakform.fitness.ui.components.Kit`. The chip-selection logic is
 * extracted into the pure `selectComponentChips` helper so it can be unit
 * tested without a Compose host.
 *
 * Tests:
 *   1. A taxonomy entry with 3 components renders 3 chips.
 *   2. A taxonomy entry with 6 components renders 4 chips + "and 2 more".
 *   3. Weight-0 components are hidden.
 *   4. The components map order does not matter (weight sort wins).
 *   5. Missing taxonomy renders no chips (empty selection).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch36aTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    // ---- 1. Three components → 3 chips ----

    @Test
    fun taxonomyWithThreeComponents_rendersThreeChips() {
        Library.ensure(ctx)
        // Pick a real taxonomy entry with exactly 3 components.
        val entry = Library.taxonomy.entries.firstOrNull { it.value.components.size == 3 }
        Assert.assertNotNull(
            "Taxonomy should have at least one entry with exactly 3 components",
            entry,
        )
        val selection = selectComponentChips(entry!!.value.components, maxShown = 4)
        Assert.assertEquals("3 chips rendered", 3, selection.chips.size)
        Assert.assertEquals("no 'and N more' suffix", 0, selection.remaining)
    }

    @Test
    fun syntheticThreeComponentMap_rendersThreeChips() {
        val map = mapOf("Strength" to 3, "Endurance" to 1, "Power" to 1)
        val selection = selectComponentChips(map, maxShown = 4)
        Assert.assertEquals(3, selection.chips.size)
        Assert.assertEquals(0, selection.remaining)
        // Verify sort: Strength (3) first, then Endurance (1) + Power (1) —
        // tie broken alphabetically (Endurance before Power).
        Assert.assertEquals("Strength", selection.chips[0].name)
        Assert.assertEquals(3, selection.chips[0].weight)
        Assert.assertEquals("Endurance", selection.chips[1].name)
        Assert.assertEquals("Power", selection.chips[2].name)
    }

    // ---- 2. Six components → 4 chips + "and 2 more" ----

    @Test
    fun taxonomyWithSixComponents_rendersFourChips_andTwoMore() {
        // The real taxonomy caps at 4 components per exercise (no entry has 6).
        // Construct a synthetic 6-component map.
        val map = mapOf(
            "Strength" to 3,
            "Endurance" to 2,
            "Power" to 2,
            "Flexibility" to 1,
            "Skill" to 1,
            "Balance" to 1,
        )
        val selection = selectComponentChips(map, maxShown = 4)
        Assert.assertEquals("4 chips rendered (maxShown cap)", 4, selection.chips.size)
        Assert.assertEquals("2 remaining → 'and 2 more' suffix", 2, selection.remaining)
        // Top 4 by weight: Strength(3), Endurance(2), Power(2), then alphabetically Flex(1)
        Assert.assertEquals("Strength", selection.chips[0].name)
        Assert.assertEquals(3, selection.chips[0].weight)
        Assert.assertEquals("Endurance", selection.chips[1].name)
        Assert.assertEquals(2, selection.chips[1].weight)
        Assert.assertEquals("Power", selection.chips[2].name)
        Assert.assertEquals(2, selection.chips[2].weight)
        // 4th chip: Flexibility, Skill, Balance all have weight 1 — alphabetical
        // tiebreaker puts Balance first. But the test for which one specifically
        // is selected doesn't matter for this assertion — the chip count does.
        Assert.assertEquals(1, selection.chips[3].weight)
    }

    // ---- 3. Weight-0 components are hidden ----

    @Test
    fun weightZeroComponents_areHidden() {
        val map = mapOf(
            "Strength" to 3,
            "Power" to 0,           // weight 0 → hidden
            "Endurance" to 1,
            "Flexibility" to 0,     // weight 0 → hidden
        )
        val selection = selectComponentChips(map, maxShown = 4)
        Assert.assertEquals("only non-zero components rendered", 2, selection.chips.size)
        // Verify the weight-0 names are NOT in the selection.
        val names = selection.chips.map { it.name }.toSet()
        Assert.assertFalse("Power (weight 0) must not appear", "Power" in names)
        Assert.assertFalse("Flexibility (weight 0) must not appear", "Flexibility" in names)
        Assert.assertTrue("Strength must appear", "Strength" in names)
        Assert.assertTrue("Endurance must appear", "Endurance" in names)
    }

    @Test
    fun allZeroWeights_rendersNoChips() {
        val map = mapOf("Strength" to 0, "Power" to 0, "Endurance" to 0)
        val selection = selectComponentChips(map, maxShown = 4)
        Assert.assertTrue("all-weight-0 map → empty selection", selection.chips.isEmpty())
        Assert.assertEquals(0, selection.remaining)
    }

    // ---- 4. Order independence (weight sort wins) ----

    @Test
    fun componentMapOrder_doesNotMatter() {
        // Same components, different insertion orders.
        val mapA = linkedMapOf(
            "Strength" to 3,
            "Endurance" to 1,
            "Power" to 2,
        )
        val mapB = linkedMapOf(
            "Power" to 2,
            "Strength" to 3,
            "Endurance" to 1,
        )
        val mapC = linkedMapOf(
            "Endurance" to 1,
            "Power" to 2,
            "Strength" to 3,
        )
        val selA = selectComponentChips(mapA, maxShown = 4)
        val selB = selectComponentChips(mapB, maxShown = 4)
        val selC = selectComponentChips(mapC, maxShown = 4)
        // All three selections must have the same chip names in the same order
        // (sorted by weight desc, then name asc).
        Assert.assertEquals(
            "selection chip names should be identical regardless of map insertion order",
            selA.chips.map { it.name },
            selB.chips.map { it.name },
        )
        Assert.assertEquals(
            "selection chip names should be identical regardless of map insertion order",
            selA.chips.map { it.name },
            selC.chips.map { it.name },
        )
        // Verify the actual sort order: Strength(3), Power(2), Endurance(1)
        Assert.assertEquals(
            listOf("Strength", "Power", "Endurance"),
            selA.chips.map { it.name },
        )
    }

    // ---- 5. Missing taxonomy → no chips ----

    @Test
    fun missingTaxonomy_rendersNoChips() {
        Library.ensure(ctx)
        // An unknown exercise id has no taxonomy → Library.taxonomyFor returns null.
        val taxonomy = Library.taxonomyFor("zzz_not_a_real_exercise_id_xyz_123")
        Assert.assertNull("unknown exercise id → null taxonomy", taxonomy)
        val components = taxonomy?.components ?: emptyMap()
        val selection = selectComponentChips(components, maxShown = 4)
        Assert.assertTrue("empty components map → empty selection", selection.chips.isEmpty())
        Assert.assertEquals(0, selection.remaining)
    }

    @Test
    fun emptyComponentsMap_rendersNoChips() {
        val selection = selectComponentChips(emptyMap(), maxShown = 4)
        Assert.assertTrue(selection.chips.isEmpty())
        Assert.assertEquals(0, selection.remaining)
    }

    // ---- Bonus: maxShown parameter is honored ----

    @Test
    fun maxShownParameter_isHonored() {
        val map = mapOf(
            "Strength" to 3, "Endurance" to 2, "Power" to 2,
            "Flexibility" to 1, "Skill" to 1, "Balance" to 1,
        )
        // maxShown = 2 → 2 chips + 4 remaining
        val sel2 = selectComponentChips(map, maxShown = 2)
        Assert.assertEquals(2, sel2.chips.size)
        Assert.assertEquals(4, sel2.remaining)

        // maxShown = 10 → all 6 chips, 0 remaining
        val sel10 = selectComponentChips(map, maxShown = 10)
        Assert.assertEquals(6, sel10.chips.size)
        Assert.assertEquals(0, sel10.remaining)

        // maxShown = 0 → 0 chips + 6 remaining (degenerate but well-defined)
        val sel0 = selectComponentChips(map, maxShown = 0)
        Assert.assertEquals(0, sel0.chips.size)
        Assert.assertEquals(6, sel0.remaining)
    }

    // ---- Bonus: real taxonomy sanity ----

    @Test
    fun everyTaxonomyComponentHasNonNegativeWeight() {
        Library.ensure(ctx)
        for ((id, t) in Library.taxonomy) {
            for ((name, weight) in t.components) {
                Assert.assertTrue(
                    "exercise $id component $name has weight $weight (must be >= 0)",
                    weight >= 0,
                )
            }
        }
    }
}
