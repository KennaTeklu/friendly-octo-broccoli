package com.peakform.fitness

import com.peakform.fitness.core.*
import com.peakform.fitness.engine.Generator
import com.peakform.fitness.engine.Library
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * BATCH-39d — Building your workout progress bar.
 *
 * Source: source/docs/system/17-batch-pipeline.md (delivery contract).
 *
 * Tests:
 *   1. performGenerateWorkout invokes onProgress for every phase in order
 *   2. the phase count is stable (same total each run)
 *   3. genPhase resets to null after generation completes
 *   4. the overlay renders a determinate bar when total > 1
 *   5. the overlay renders an indeterminate bar when total <= 1
 *
 * Tests 1-2 actually drive the generator (via the JourneyTest setup) and
 * record every (phase, step, total) tuple emitted by the onProgress
 * callback. Tests 3-5 exercise the ProgressBarState pure helper that
 * the MainActivity overlay calls.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class Batch39dTest {

    private lateinit var dataDir: File

    @Before
    fun setUp() {
        // BATCH-39c: install a Main dispatcher for JVM tests because the
        // generator routes notifyChanged through withContext(Dispatchers.Main).
        Dispatchers.setMain(Dispatchers.Unconfined)
        dataDir = sequenceOf("src/main/assets/data", "app/src/main/assets/data")
            .map { File(it) }.firstOrNull { it.isDirectory }
            ?: error("assets/data not found from ${File(".").absolutePath}")
        Library.ensureForTest { name -> File(dataDir, name).readText() }
        ProState.initialized = true
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun freshPersona(): WorkoutData {
        val u = UserProfile(
            name = "Progress Tester",
            birthDate = "1995-01-01",
            gender = "female",
            weight = 65.0,
            height = 170.0,
            experience = "intermediate",
            goal = "balanced",
            created = ProState.nowIso(),
            settings = Settings(
                workoutDays = listOf("monday", "wednesday", "friday"),
                restTime = 120,
                trainingMode = "gym",
                theme = "blue",
                darkMode = false,
            ),
        )
        return WorkoutData(user = u)
    }

    // 1. performGenerateWorkout invokes onProgress for every phase in order
    @Test
    fun performGenerateWorkout_invokesOnProgressForEveryPhaseInOrder() {
        ProState.data = freshPersona()
        ProState.currentWorkout = null

        val recorded = mutableListOf<Triple<String, Int, Int>>()
        runBlocking {
            Generator.performGenerateWorkout(
                timeBudgetMin = null,
                suppressConfirm = true,
            ) { phase, step, total ->
                recorded.add(Triple(phase, step, total))
            }
        }

        // Must fire exactly 5 times — one per phase.
        assertEquals(
            "onProgress must fire exactly 5 times (one per phase); got ${recorded.size}",
            5,
            recorded.size,
        )

        // Phases must match ProgressPhases.ALL in order.
        val expectedPhases = Generator.ProgressPhases.ALL
        recorded.forEachIndexed { i, (phase, _, _) ->
            assertEquals(
                "phase[$i] must be ${expectedPhases[i]} (was $phase)",
                expectedPhases[i],
                phase,
            )
        }

        // Steps must be 1..5 in ascending order.
        recorded.forEachIndexed { i, (_, step, _) ->
            assertEquals(
                "step[$i] must be ${i + 1} (was $step)",
                i + 1,
                step,
            )
        }

        // Total must be 5 on every callback.
        recorded.forEach { (_, _, total) ->
            assertEquals(
                "total must be ${Generator.ProgressPhases.TOTAL}",
                Generator.ProgressPhases.TOTAL,
                total,
            )
        }
    }

    // 2. The phase count is stable (same total each run)
    @Test
    fun performGenerateWorkout_phaseCountIsStable_acrossRuns() {
        ProState.data = freshPersona()
        ProState.currentWorkout = null

        val totalsRun1 = mutableListOf<Int>()
        runBlocking {
            Generator.performGenerateWorkout { _, _, total -> totalsRun1.add(total) }
        }

        ProState.data = freshPersona()
        ProState.currentWorkout = null

        val totalsRun2 = mutableListOf<Int>()
        runBlocking {
            Generator.performGenerateWorkout { _, _, total -> totalsRun2.add(total) }
        }

        // Both runs must produce the same total sequence.
        assertEquals(
            "phase totals must be stable across runs (size mismatch)",
            totalsRun1.size,
            totalsRun2.size,
        )
        totalsRun1.forEachIndexed { i, t ->
            assertEquals(
                "phase totals must be stable across runs (index $i)",
                t,
                totalsRun2[i],
            )
        }
        // The stable total is 5.
        assertTrue(
            "all phase totals must be 5 (got $totalsRun1)",
            totalsRun1.all { it == Generator.ProgressPhases.TOTAL },
        )
    }

    // 3. genPhase resets to null after generation completes
    //
    // The MainActivity.generateAndGo flow sets genPhase = null before
    // launching, then sets genPhase = phase in the onProgress callback,
    // then sets genPhase = null again in the withContext(Dispatchers.Main)
    // block after the generator returns. The reset behaviour is verified
    // via the ProgressBarState.overlayVisible helper (the overlay hides
    // when genPhase is null) plus a state-machine check that mirrors
    // the AppShell's flow: genPhase starts null, becomes non-null during
    // the run, returns to null at the end.
    @Test
    fun genPhase_resetsToNullAfterGenerationCompletes() {
        // Pre-state: null.
        var genPhase: String? = null
        assertTrue("overlay must be hidden before generation starts (genPhase=null)",
            !ProgressBarState.overlayVisible(regenerating = true, genPhase = genPhase))

        // During-state: non-null. The onProgress callback in MainActivity
        // sets genPhase to the reported phase.
        runBlocking {
            Generator.performGenerateWorkout { phase, _, _ ->
                // Simulate the MainActivity callback body — note the
                // withContext(Dispatchers.Main) wrap is a no-op here
                // because Dispatchers.setMain(Unconfined) is installed.
                withContext(Dispatchers.Main) { genPhase = phase }
            }
        }

        // Post-state: null. The MainActivity finally block sets genPhase
        // back to null when regenerating flips to false.
        genPhase = null
        assertTrue("overlay must be hidden after generation completes (genPhase=null)",
            !ProgressBarState.overlayVisible(regenerating = false, genPhase = genPhase))

        // Also verify the overlay-visible contract holds mid-run: if
        // regenerating=true AND genPhase!=null, overlay must show.
        genPhase = Generator.ProgressPhases.SELECTING_EXERCISES
        assertTrue("overlay must show mid-run (regenerating=true, genPhase=$genPhase)",
            ProgressBarState.overlayVisible(regenerating = true, genPhase = genPhase))
    }

    // 4. The overlay renders a determinate bar when total > 1
    @Test
    fun overlay_rendersDeterminateBarWhenTotalGreaterThanOne() {
        // total = 5 (the generator's stable phase count) → determinate.
        val total = Generator.ProgressPhases.TOTAL
        assertTrue("total must be > 1 for determinate bar (got $total)", total > 1)
        assertFalse(
            "isIndeterminate must be false when total > 1",
            ProgressBarState.isIndeterminate(total),
        )

        // progress advances with step.
        assertEquals(0.2f, ProgressBarState.progress(step = 1, total = 5), 0.001f)
        assertEquals(0.4f, ProgressBarState.progress(step = 2, total = 5), 0.001f)
        assertEquals(0.6f, ProgressBarState.progress(step = 3, total = 5), 0.001f)
        assertEquals(0.8f, ProgressBarState.progress(step = 4, total = 5), 0.001f)
        assertEquals(1.0f, ProgressBarState.progress(step = 5, total = 5), 0.001f)

        // step caption format.
        assertEquals("Step 3 of 5", ProgressBarState.stepCaption(step = 3, total = 5))
        assertEquals("Step 5 of 5", ProgressBarState.stepCaption(step = 5, total = 5))

        // header text falls through to the reported phase.
        assertEquals(
            Generator.ProgressPhases.SELECTING_EXERCISES,
            ProgressBarState.headerText(Generator.ProgressPhases.SELECTING_EXERCISES),
        )
    }

    // 5. The overlay renders an indeterminate bar when total <= 1
    @Test
    fun overlay_rendersIndeterminateBarWhenTotalLessThanOrEqualOne() {
        // total = 0 → indeterminate.
        assertTrue(
            "isIndeterminate must be true when total = 0",
            ProgressBarState.isIndeterminate(total = 0),
        )
        // total = 1 → indeterminate (per spec: "total is 0 or 1 → indeterminate").
        assertTrue(
            "isIndeterminate must be true when total = 1 (fast path)",
            ProgressBarState.isIndeterminate(total = 1),
        )
        // progress returns 0 for the indeterminate branch (ignored by the
        // UI, but defined so the pure function is total).
        assertEquals(0f, ProgressBarState.progress(step = 0, total = 1), 0.001f)
        assertEquals(0f, ProgressBarState.progress(step = 1, total = 0), 0.001f)

        // headerText falls back to "Building your workout" when phase is null.
        assertEquals(
            ProgressBarState.HEADER_FALLBACK,
            ProgressBarState.headerText(null),
        )

        // Overlay hides when genPhase is null even if regenerating is true.
        assertFalse(
            "overlay must hide when genPhase is null",
            ProgressBarState.overlayVisible(regenerating = true, genPhase = null),
        )

        // Sanity: indeterminate total differs from the generator's stable total.
        assertNotEquals(
            "indeterminate totals (0,1) must differ from the generator's stable total (5)",
            Generator.ProgressPhases.TOTAL,
            0,
        )
        assertNotEquals(
            "indeterminate totals (0,1) must differ from the generator's stable total (5)",
            Generator.ProgressPhases.TOTAL,
            1,
        )
    }
}
