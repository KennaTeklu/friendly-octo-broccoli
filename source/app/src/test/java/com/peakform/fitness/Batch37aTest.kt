package com.peakform.fitness

import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.coach.dossiers.ExerciseDossier
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-37a — Dossier schema + pilot family (gymnastic push chain).
 *
 * Tests the schema, the registry API, and the 15 push-chain dossiers
 * authored from sommer-2006/gymnastic-body-tier-table.md.
 *
 * Spec cases:
 *   1. Every exercise in the chain has a dossier.
 *   2. Every dossier cites sommer-2006.
 *   3. Every goal has a landmark and a source.
 *   4. Every prerequisite references a real dossier by id.
 *   5. Every progressionIn points to a real dossier.
 *   6. Every progressionOut points to a real dossier.
 *   7. The chain is walkable start to end.
 *   8. No dossier invents a goal without a source.
 *   9. The full planche push-up dossier exists and its prerequisite chain
 *      resolves to the wall push-up.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch37aTest {

    /** The 15 gymnastic push-chain exercise ids (canonical ids, per CHANGES.md). */
    private val chainIds: List<String> = listOf(
        "wall_push-up",
        "incline_push_up",
        "knee_push-up",
        "push-up",
        "diamond_push-up",
        "wide_push-up",
        "decline_push_up",
        "pseudo_planche_push-up",
        "tuck_planche_hold",
        "advanced_tuck_planche_hold",
        "straddle_planche_hold",
        "planche_lean",
        "tuck_planche_push-up",
        "straddle_planche_push-up",
        "full_planche_push-up",
    )

    // ---- 1. Every exercise in the chain has a dossier ----

    @Test
    fun everyChainExercise_hasDossier() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)
            Assert.assertNotNull(
                "Chain exercise '$id' should have a dossier",
                dossier,
            )
        }
    }

    // ---- 2. Every dossier cites sommer-2006 ----

    @Test
    fun everyDossier_citesSommer2006() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)
            Assert.assertNotNull("Chain exercise '$id' should have a dossier", dossier)
            val source = dossier!!.provenance.source
            Assert.assertTrue(
                "Dossier for '$id' should cite sommer-2006 (got '$source')",
                source.startsWith("sommer-2006/"),
            )
        }
    }

    // ---- 3. Every goal has a landmark and a source ----

    @Test
    fun everyGoal_hasLandmarkAndSource() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            Assert.assertTrue(
                "Dossier for '$id' should have at least one goal",
                dossier.goals.isNotEmpty(),
            )
            for (goal in dossier.goals) {
                Assert.assertTrue(
                    "Goal '${goal.goalId}' on '$id' should have a non-blank landmark",
                    goal.landmark.isNotBlank(),
                )
                Assert.assertTrue(
                    "Goal '${goal.goalId}' on '$id' should have a non-blank source",
                    goal.source.isNotBlank(),
                )
                Assert.assertTrue(
                    "Goal '${goal.goalId}' on '$id' source should cite sommer-2006 (got '${goal.source}')",
                    goal.source.startsWith("sommer-2006/"),
                )
            }
        }
    }

    // ---- 4. Every prerequisite references a real dossier by id ----

    @Test
    fun everyPrerequisite_referencesRealDossier() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            for (prereq in dossier.prerequisites) {
                // The Prerequisite's exerciseId is THIS exercise's id (per the
                // schema — the prerequisite describes what must be true of the
                // person BEFORE they attempt this exercise).
                Assert.assertEquals(
                    "Prerequisite on '$id' should have exerciseId == '$id' (got '${prereq.exerciseId}')",
                    id, prereq.exerciseId,
                )
                // The prerequisite's requirement must be a sentence, not a number.
                Assert.assertTrue(
                    "Prerequisite on '$id' (for goal ${prereq.forGoalId}) should be a non-blank sentence",
                    prereq.requirement.isNotBlank(),
                )
                Assert.assertTrue(
                    "Prerequisite on '$id' should cite a source",
                    prereq.source.isNotBlank(),
                )
            }
        }
    }

    // ---- 5. Every progressionIn points to a real dossier ----

    @Test
    fun everyProgressionIn_pointsToRealDossier() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            for (prevId in dossier.progressionIn) {
                Assert.assertNotNull(
                    "progressionIn '$prevId' on '$id' should resolve to a dossier",
                    DossierRegistry.forExercise(prevId),
                )
            }
        }
    }

    // ---- 6. Every progressionOut points to a real dossier ----

    @Test
    fun everyProgressionOut_pointsToRealDossier() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            for (nextId in dossier.progressionOut) {
                Assert.assertNotNull(
                    "progressionOut '$nextId' on '$id' should resolve to a dossier",
                    DossierRegistry.forExercise(nextId),
                )
            }
        }
    }

    // ---- 7. The chain is walkable start to end ----

    @Test
    fun chainIsWalkable_startToEnd() {
        // chainFor(wall_push-up) should return all 15 ids in chain order.
        val chain = DossierRegistry.chainFor("wall_push-up")
        Assert.assertEquals(
            "Chain from wall_push-up should have ${chainIds.size} entries",
            chainIds.size, chain.size,
        )
        Assert.assertEquals(chainIds, chain)

        // chainFor(full_planche_push-up) should also return all 15 ids in
        // the same order (full planche is the terminus).
        val chainFromEnd = DossierRegistry.chainFor("full_planche_push-up")
        Assert.assertEquals(chainIds, chainFromEnd)
    }

    @Test
    fun chainFromMiddle_walksBothDirections() {
        // From push-up (index 3), the chain should include all 15 ids.
        val chain = DossierRegistry.chainFor("push-up")
        Assert.assertEquals(chainIds.size, chain.size)
        // push-up should be in the middle of the chain.
        val pushUpIndex = chain.indexOf("push-up")
        Assert.assertTrue(
            "push-up should be in the middle of the chain (index > 0 and < size-1)",
            pushUpIndex > 0 && pushUpIndex < chain.size - 1,
        )
        // Ids before push-up should be the earlier chain ids.
        Assert.assertEquals(chainIds.subList(0, 3), chain.subList(0, 3))
        // Ids after push-up should be the later chain ids.
        Assert.assertEquals(chainIds.subList(4, chainIds.size), chain.subList(pushUpIndex + 1, chain.size))
    }

    // ---- 8. No dossier invents a goal without a source ----

    @Test
    fun noDossier_inventsGoalWithoutSource() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            for (goal in dossier.goals) {
                Assert.assertTrue(
                    "Goal '${goal.goalId}' on '$id' must have a source (cannot be invented)",
                    goal.source.isNotBlank(),
                )
                Assert.assertTrue(
                    "Goal '${goal.goalId}' on '$id' source must cite sommer-2006 (cannot invent from another source)",
                    goal.source.startsWith("sommer-2006/"),
                )
            }
        }
    }

    // ---- 9. Full planche push-up dossier exists; chain resolves to wall push-up ----

    @Test
    fun fullPlancheDossier_existsAndChainResolvesToWallPushUp() {
        val fullPlanche = DossierRegistry.forExercise("full_planche_push-up")
        Assert.assertNotNull(
            "Full planche push-up dossier should exist",
            fullPlanche,
        )
        // The full planche is the chain terminus — progressionOut is empty
        // (or null target on the goal).
        Assert.assertTrue(
            "Full planche should have no progressionOut (chain terminus)",
            fullPlanche!!.progressionOut.isEmpty(),
        )
        // chainFor(full_planche_push-up) should resolve back to wall_push-up
        // as the chain root.
        val chain = DossierRegistry.chainFor("full_planche_push-up")
        Assert.assertEquals(
            "Chain root should be wall_push-up",
            "wall_push-up", chain.first(),
        )
        Assert.assertEquals(
            "Chain terminus should be full_planche_push-up",
            "full_planche_push-up", chain.last(),
        )

        // Walk the chain via prerequisites — full planche's prerequisite
        // references straddle_planche_push-up, which references
        // tuck_planche_push-up, etc. — the chain should walk back to
        // wall_push-up, whose prerequisite is "No prerequisite — entry point".
        val walkBack = mutableListOf<String>()
        var current = "full_planche_push-up"
        val visited = mutableSetOf<String>()
        while (current !in visited) {
            visited.add(current)
            walkBack.add(current)
            val dossier = DossierRegistry.forExercise(current) ?: break
            val prevId = dossier.progressionIn.firstOrNull() ?: break
            current = prevId
        }
        Assert.assertEquals(
            "Walking back from full_planche should reach wall_push-up",
            "wall_push-up", walkBack.last(),
        )
        // Wall push-up's prerequisite should say "No prerequisite — entry point".
        val wallPrereq = DossierRegistry.prerequisitesFor("wall_push-up", "advance_chain")
        Assert.assertTrue(
            "Wall push-up should have a prerequisite",
            wallPrereq.isNotEmpty(),
        )
        Assert.assertTrue(
            "Wall push-up's prerequisite should mention 'No prerequisite' (entry point): ${wallPrereq[0].requirement}",
            wallPrereq[0].requirement.contains("No prerequisite", ignoreCase = true),
        )
    }

    // ---- Bonus: registry API sanity ----

    @Test
    fun registry_all_returnsAtLeast15Dossiers() {
        val all = DossierRegistry.all()
        Assert.assertTrue(
            "Registry should have at least 15 dossiers (the push chain)",
            all.size >= 15,
        )
    }

    @Test
    fun registry_forExercise_returnsNullForUnknownId() {
        Assert.assertNull(
            "Unknown exercise id should return null",
            DossierRegistry.forExercise("zzz_not_a_real_exercise_xyz"),
        )
    }

    @Test
    fun registry_progressFrom_returnsNextOnPath() {
        // push-up → diamond_push-up on the "advance_chain" goal.
        val next = DossierRegistry.progressFrom("push-up", "advance_chain")
        Assert.assertEquals("diamond_push-up", next)
    }

    @Test
    fun registry_progressFrom_returnsNullForChainTerminus() {
        val next = DossierRegistry.progressFrom("full_planche_push-up", "advance_chain")
        // Full planche is the chain terminus — target is "chain_terminus"
        // and progressFrom returns null.
        Assert.assertNull(next)
    }

    @Test
    fun registry_progressFrom_returnsNullForUnknownGoal() {
        val next = DossierRegistry.progressFrom("push-up", "nonexistent_goal")
        Assert.assertNull(next)
    }

    @Test
    fun registry_prerequisitesFor_returnsListForKnownGoal() {
        val prereqs = DossierRegistry.prerequisitesFor("push-up", "advance_chain")
        Assert.assertTrue(
            "push-up should have at least one prerequisite on the advance_chain goal",
            prereqs.isNotEmpty(),
        )
        Assert.assertEquals(
            "advance_chain", prereqs[0].forGoalId,
        )
    }

    @Test
    fun registry_prerequisitesFor_returnsEmptyForUnknownGoal() {
        val prereqs = DossierRegistry.prerequisitesFor("push-up", "nonexistent_goal")
        Assert.assertTrue(prereqs.isEmpty())
    }

    @Test
    fun registry_chainFor_returnsJustIdForUnknownExercise() {
        val chain = DossierRegistry.chainFor("zzz_unknown_exercise_xyz")
        Assert.assertEquals(listOf("zzz_unknown_exercise_xyz"), chain)
    }

    @Test
    fun everyDossier_hasRequiredFieldsPopulated() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            // Required string fields
            Assert.assertTrue("'$id' name", dossier.name.isNotBlank())
            Assert.assertTrue("'$id' coachId", dossier.coachId.isNotBlank())
            // components map should be non-empty (authored)
            Assert.assertTrue("'$id' components should be non-empty", dossier.components.isNotEmpty())
            // stimulusProfile + componentStimulus (authored — may be provisional)
            Assert.assertTrue("'$id' stimulusProfile", dossier.stimulusProfile.isNotEmpty())
            Assert.assertTrue("'$id' componentStimulus", dossier.componentStimulus.isNotEmpty())
            // version starts at 1
            Assert.assertTrue("'$id' version should be >= 1", dossier.version >= 1)
            // goals + prerequisites
            Assert.assertTrue("'$id' goals", dossier.goals.isNotEmpty())
            Assert.assertTrue("'$id' prerequisites", dossier.prerequisites.isNotEmpty())
        }
    }

    @Test
    fun everyDossier_coachIdIsPushUp() {
        // All 15 chain exercises route to PushUpAuthoredCoach.
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            Assert.assertEquals(
                "Dossier for '$id' should route to push_up coach",
                "push_up", dossier.coachId,
            )
        }
    }

    @Test
    fun chainIsLinear_noCycles() {
        // Walk forward from wall_push-up — every id should be unique.
        val forward = mutableListOf<String>()
        var current = "wall_push-up"
        val visited = mutableSetOf<String>()
        while (current !in visited) {
            visited.add(current)
            forward.add(current)
            val d = DossierRegistry.forExercise(current) ?: break
            current = d.progressionOut.firstOrNull() ?: break
        }
        Assert.assertEquals(
            "Forward chain should have ${chainIds.size} unique ids",
            chainIds.size, forward.size,
        )
        Assert.assertEquals(chainIds, forward)
    }

    @Test
    fun everyProgressionEdgeIsBidirectional() {
        // For every progressionIn edge A → B (B's progressionIn contains A),
        // A's progressionOut should contain B.
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            for (prevId in dossier.progressionIn) {
                val prevDossier = DossierRegistry.forExercise(prevId)
                Assert.assertNotNull(
                    "progressionIn target '$prevId' on '$id' should resolve to a dossier",
                    prevDossier,
                )
                Assert.assertTrue(
                    "If '$id' lists '$prevId' in progressionIn, then '$prevId' should list '$id' in progressionOut (bidirectional)",
                    prevDossier!!.progressionOut.contains(id),
                )
            }
            for (nextId in dossier.progressionOut) {
                val nextDossier = DossierRegistry.forExercise(nextId)
                Assert.assertNotNull(
                    "progressionOut target '$nextId' on '$id' should resolve to a dossier",
                    nextDossier,
                )
                Assert.assertTrue(
                    "If '$id' lists '$nextId' in progressionOut, then '$nextId' should list '$id' in progressionIn (bidirectional)",
                    nextDossier!!.progressionIn.contains(id),
                )
            }
        }
    }

    @Test
    fun everyStimulusProfile_valueInRange() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            for ((_, weight) in dossier.stimulusProfile) {
                Assert.assertTrue(
                    "Stimulus weight $weight on '$id' should be in 0.0..1.0",
                    weight in 0.0..1.0,
                )
            }
            for ((_, weight) in dossier.componentStimulus) {
                Assert.assertTrue(
                    "Component stimulus weight $weight on '$id' should be in 0.0..1.0",
                    weight in 0.0..1.0,
                )
            }
        }
    }

    @Test
    fun everyComponent_weightInRange() {
        for (id in chainIds) {
            val dossier = DossierRegistry.forExercise(id)!!
            for ((_, weight) in dossier.components) {
                Assert.assertTrue(
                    "Component weight $weight on '$id' should be in 0..5",
                    weight in 0..5,
                )
            }
        }
    }
}
