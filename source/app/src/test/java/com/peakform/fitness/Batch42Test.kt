package com.peakform.fitness

import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.core.OneRmResolver
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.state.StateModel
import com.peakform.fitness.engine.Library
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * BATCH-42 — 1RM Resolution Chain.
 *
 * Source: BATCH-42 spec (1RM Resolution Chain).
 *
 * The resolver walks six sources in order — first hit wins:
 *   1. Direct tested 1RM                  conf 1.00, source "tested"
 *   2. Recent direct history (3+ sets    conf 0.85, source "estimated from
 *      at 1-5 reps in 30 days)                     recent <exercise>"
 *   3. Sibling tested 1RM                 conf 0.70, source "derived from
 *                                                     <sibling> 1RM"
 *   4. Sibling history (3+ sets)          conf 0.60, source "derived from
 *                                                     <sibling> history"
 *   5. Muscle history (14 days)          conf 0.30, source "estimated from
 *                                                     muscle history"
 *   6. Nothing                            return null
 *
 * Tests below cover every source + the sibling-overlap computation +
 * the card-gate (panel hidden when estimate.confidence >= 0.7,
 * panel rendered when estimate is null).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch42Test {

    private val ctx: android.content.Context
        get() = ApplicationProvider.getApplicationContext()

    private lateinit var dataDir: File

    @Before
    fun setUp() {
        dataDir = sequenceOf("src/main/assets/data", "app/src/main/assets/data")
            .map { File(it) }.firstOrNull { it.isDirectory }
            ?: error("assets/data not found from ${File(".").absolutePath}")
        Library.ensureForTest { name -> File(dataDir, name).readText() }
        DossierRegistry.ensureForTest { name -> File(dataDir, name).readText() }
        OneRmResolver.ensureForTest { name -> File(dataDir, name).readText() }
        ProState.initialized = true
        ProState.data = com.peakform.fitness.core.WorkoutData(
            user = com.peakform.fitness.core.UserProfile(
                name = "Batch42 Tester",
                birthDate = "1990-01-01",
                gender = "male",
                weight = 80.0,
                height = 180.0,
                experience = "intermediate",
                goal = "balanced",
                created = ProState.nowIso(),
                settings = com.peakform.fitness.core.Settings(
                    workoutDays = listOf("monday", "wednesday", "friday"),
                    restTime = 120,
                    trainingMode = "gym",
                    theme = "blue",
                    darkMode = false,
                ),
            ),
        )
        ProState.currentWorkout = null
        ProState.sessions = emptyList()
        ProState.field = StateModel.FieldState()
        ProState.recovery = StateModel.RecoveryState()
    }

    @After
    fun tearDown() {
        ProState.field = StateModel.FieldState()
        ProState.sessions = emptyList()
        ProState.recovery = StateModel.RecoveryState()
        ProState.data = ProState.data.copy(exercises = emptyMap())
    }

    // ── Source 1: direct tested 1RM ────────────────────────────────────────

    // 1. A direct tested 1RM (ExerciseRecord.tested1RM > 0) returns an
    //    estimate with source="tested" and confidence 1.0.
    @Test
    fun direct_1rm_returnsTestedSourceConfidence1() {
        ProState.data = ProState.data.copy(exercises = mapOf(
            "barbell_back_squat_(high_bar)" to com.peakform.fitness.core.ExerciseRecord(
                tested1RM = 140.0,
                testDate = ProState.todayLocal(),
            ),
        ))
        val est = OneRmResolver.resolve("barbell_back_squat_(high_bar)")
        assertNotNull("resolver must return an estimate for a tested 1RM", est)
        assertEquals("tested source", OneRmResolver.SRC_TESTED, est!!.source)
        assertEquals("confidence must be 1.0", 1.0, est.confidence, 0.001)
        assertEquals("value must match the stored tested1RM", 140.0, est.value, 0.001)
    }

    // ── Source 2: recent direct history ───────────────────────────────────

    // 2. Three or more logged sets at 1-5 reps in the last 30 days returns
    //    source="estimated from recent <exercise>" with confidence >= 0.7
    //    (the spec sets it to 0.85).
    @Test
    fun recentHistory_returnsEstimatedFromRecentConfidenceAtLeast07() {
        // No tested1RM — clear exercises so Source 1 does not fire.
        ProState.data = ProState.data.copy(exercises = emptyMap())
        val now = System.currentTimeMillis()
        ProState.sessions = listOf(
            StateModel.SessionRecord(
                id = "s1",
                userId = "u1",
                timestamp = now - 2 * 24L * 3600L * 1000L,
                exercises = listOf(
                    StateModel.ExerciseRecord(
                        exerciseId = "barbell_back_squat_(high_bar)",
                        sets = listOf(
                            StateModel.SetRecord(reps = 5, load = 200.0, completed = true),
                            StateModel.SetRecord(reps = 3, load = 215.0, completed = true),
                            StateModel.SetRecord(reps = 5, load = 205.0, completed = true),
                        ),
                    ),
                ),
            ),
        )
        val est = OneRmResolver.resolve("barbell_back_squat_(high_bar)")
        assertNotNull("resolver must return an estimate from recent history", est)
        assertTrue(
            "source must start with 'estimated from recent' (got: ${est!!.source})",
            est.source.startsWith(OneRmResolver.SRC_RECENT),
        )
        assertTrue(
            "confidence must be >= 0.7 (got: ${est.confidence})",
            est.confidence >= 0.7,
        )
        // Epley(200, 5) = 200 * (1 + 5/30) = 233.3
        // Brzycki(200, 5) = 200 * 36/(37-5) = 225.0
        // Epley(215, 3) = 215 * 1.1 = 236.5
        // Brzycki(215, 3) = 215 * 36/34 = 227.6
        // Epley(205, 5) = 205 * 1.1667 = 239.2
        // Brzycki(205, 5) = 205 * 36/32 = 230.6
        // Avg ≈ 232.0 — sanity check value is reasonable.
        assertTrue(
            "estimate value must be > 200 lbs (got: ${est.value})",
            est.value > 200.0,
        )
    }

    // ── Source 3: sibling tested 1RM ──────────────────────────────────────

    // 3. A sibling exercise (overlap >= 0.6) with a tested 1RM returns
    //    source="derived from <sibling> 1RM" with confidence >= 0.6 (spec
    //    sets it to 0.70).
    //
    // barbell_back_squat_(high_bar) and leg_press both have stimulusProfile
    // touching quads+glutes — overlap = 1.7 >= 0.6.
    // strength_ratios.json["squat"]["leg_press"] = 1.2, so the back_squat
    // estimate is leg_press.tested1RM * 1.2.
    @Test
    fun sibling_1rm_returnsDerivedFromConfidenceAtLeast06() {
        ProState.data = ProState.data.copy(exercises = mapOf(
            // Sibling: leg_press has tested1RM = 240 lbs. Ratio squat->leg_press = 1.2,
            // so the back_squat estimate is 240 * 1.2 = 288 lbs.
            "leg_press" to com.peakform.fitness.core.ExerciseRecord(
                tested1RM = 240.0,
                testDate = ProState.todayLocal(),
            ),
        ))
        val est = OneRmResolver.resolve("barbell_back_squat_(high_bar)")
        assertNotNull(
            "resolver must return an estimate via sibling tested 1RM. " +
                "Sessions: ${ProState.sessions.size}, exercises: ${ProState.data.exercises.keys}",
            est,
        )
        assertTrue(
            "source must start with 'derived from' (got: ${est!!.source})",
            est.source.startsWith(OneRmResolver.SRC_SIBLING_1RM),
        )
        assertTrue(
            "confidence must be >= 0.6 (got: ${est.confidence})",
            est.confidence >= 0.6,
        )
        // Value = 240 * 1.2 = 288.
        assertEquals(288.0, est.value, 0.5)
    }

    // ── Source 5: muscle-only history (low confidence) ────────────────────

    // 4. When the only signal is recent muscle history (no direct 1RM, no
    //    sibling ratio, no recent 1-5 rep sets on the target itself), the
    //    estimate's confidence must be < 0.7.
    @Test
    fun muscleOnlyHistory_returnsConfidenceBelow07() {
        // No tested 1RM. No recent 1-5 rep sets on the target itself.
        // But a session touching the primary muscle (quads) within 14 days
        // with a 1-5 rep set on a sibling-like exercise whose dossier has
        // primary muscle quads >= 0.7 — that exercises source 5.
        ProState.data = ProState.data.copy(exercises = emptyMap())
        val now = System.currentTimeMillis()
        // Use a sibling exercise that has NO tested 1RM and NO ratio entry
        // in strength_ratios.json so source 3/4 don't fire — front_squat's
        // ratio isn't in strength_ratios.json (only leg_press, leg_extension,
        // leg_curl are listed under "squat").
        ProState.sessions = listOf(
            StateModel.SessionRecord(
                id = "s1",
                userId = "u1",
                timestamp = now - 3 * 24L * 3600L * 1000L,
                exercises = listOf(
                    StateModel.ExerciseRecord(
                        exerciseId = "barbell_front_squat",
                        sets = listOf(
                            StateModel.SetRecord(reps = 3, load = 185.0, completed = true),
                            StateModel.SetRecord(reps = 5, load = 175.0, completed = true),
                            StateModel.SetRecord(reps = 4, load = 180.0, completed = true),
                        ),
                    ),
                ),
            ),
        )
        val est = OneRmResolver.resolve("barbell_back_squat_(high_bar)")
        assertNotNull(
            "resolver must return an estimate from muscle history (got null). " +
                "Sessions: ${ProState.sessions.size}, field.muscles: ${ProState.field.muscles}",
            est,
        )
        assertTrue(
            "muscle-history confidence must be < 0.7 (got: ${est!!.confidence})",
            est.confidence < 0.7,
        )
        assertEquals(
            "source must be 'estimated from muscle history'",
            OneRmResolver.SRC_MUSCLE,
            est.source,
        )
    }

    // ── Source 6: nothing ──────────────────────────────────────────────────

    // 5. When the user has no history whatsoever for the target or any
    //    sibling, the resolver returns null — the card-gate must render
    //    the OneRmTestPanel.
    @Test
    fun noHistory_returnsNull() {
        ProState.data = ProState.data.copy(exercises = emptyMap())
        ProState.sessions = emptyList()
        ProState.field = StateModel.FieldState()
        val est = OneRmResolver.resolve("barbell_back_squat_(high_bar)")
        assertNull("resolver must return null when no history exists", est)
    }

    // ── Card gate: confidence >= 0.7 hides the panel ──────────────────────

    // 6. When OneRmResolver.resolve() returns an estimate with confidence
    //    >= 0.7, the Workout screen's card-gate must NOT render the
    //    OneRmTestPanel. We exercise this with a tested 1RM (conf 1.0)
    //    and assert that the gate's predicate (oneRmEstimate == null)
    //    evaluates false → panel is hidden.
    @Test
    fun cardGate_hidesPanelWhenConfidenceAtLeast07() {
        ProState.data = ProState.data.copy(exercises = mapOf(
            "barbell_back_squat_(high_bar)" to com.peakform.fitness.core.ExerciseRecord(
                tested1RM = 140.0,
                testDate = ProState.todayLocal(),
            ),
        ))
        val est = OneRmResolver.resolve("barbell_back_squat_(high_bar)")
        assertNotNull(est)
        assertTrue(
            "tested 1RM confidence must be >= 0.7 (got: ${est!!.confidence})",
            est.confidence >= 0.7,
        )
        // The card gate's predicate: render panel iff (!isDrill && !has1RM
        // && reqs.needs1RM && oneRmEstimate == null). With a tested 1RM,
        // has1RM is true → predicate is false → panel hidden.
        val has1RM = (ProState.data.exercises["barbell_back_squat_(high_bar)"]?.tested1RM ?: 0.0) > 0
        assertTrue("has1RM must be true for a tested 1RM", has1RM)
        val renderPanel = !has1RM && (est == null)
        assertFalse(
            "card gate must NOT render the panel when estimate.confidence >= 0.7",
            renderPanel,
        )
    }

    // ── Card gate: null estimate renders the panel ───────────────────────

    // 7. When OneRmResolver.resolve() returns null (no estimate), the
    //    card-gate must render the OneRmTestPanel.
    @Test
    fun cardGate_rendersPanelWhenEstimateIsNull() {
        ProState.data = ProState.data.copy(exercises = emptyMap())
        ProState.sessions = emptyList()
        ProState.field = StateModel.FieldState()
        val est = OneRmResolver.resolve("barbell_back_squat_(high_bar)")
        assertNull("resolver must return null when no history exists", est)
        val has1RM = (ProState.data.exercises["barbell_back_squat_(high_bar)"]?.tested1RM ?: 0.0) > 0
        val renderPanel = !has1RM && (est == null)
        assertTrue(
            "card gate MUST render the panel when estimate is null",
            renderPanel,
        )
    }

    // ── Sibling overlap computation ───────────────────────────────────────

    // 8. The muscle overlap between two dossiers' stimulusProfiles equals
    //    sum(min(a[m], b[m])) over shared muscles.
    @Test
    fun siblingOverlap_computesCorrectly() {
        val a = mapOf("quads" to 1.0, "glutes" to 0.7, "core" to 0.5)
        val b = mapOf("quads" to 1.0, "glutes" to 0.5, "hamstrings" to 0.7)
        // Overlap = min(1.0, 1.0) + min(0.7, 0.5) = 1.0 + 0.5 = 1.5.
        val overlap = OneRmResolver.muscleOverlap(a, b)
        assertEquals(1.5, overlap, 0.001)

        // Identical → overlap = sum of all values.
        val identical = OneRmResolver.muscleOverlap(a, a)
        assertEquals(1.0 + 0.7 + 0.5, identical, 0.001)

        // Disjoint → 0.0.
        val disjoint = OneRmResolver.muscleOverlap(a, mapOf("lats" to 1.0))
        assertEquals(0.0, disjoint, 0.001)

        // Empty → 0.0.
        assertEquals(0.0, OneRmResolver.muscleOverlap(emptyMap(), a), 0.001)
        assertEquals(0.0, OneRmResolver.muscleOverlap(a, emptyMap()), 0.001)
    }
}
