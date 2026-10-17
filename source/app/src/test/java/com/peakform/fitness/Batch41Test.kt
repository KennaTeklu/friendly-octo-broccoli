package com.peakform.fitness

import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.coach.AuditFinding
import com.peakform.fitness.coach.CoachRegistry
import com.peakform.fitness.coach.ExerciseCoach
import com.peakform.fitness.coach.MetaCoach
import com.peakform.fitness.coach.Severity
import com.peakform.fitness.coach.authored.AuthoredRow
import com.peakform.fitness.coach.authored.AuthoredRows
import com.peakform.fitness.coach.authored.BodyProfile
import com.peakform.fitness.coach.authored.CoachProvenance
import com.peakform.fitness.coach.authored.ExerciseEnvelope
import com.peakform.fitness.coach.authored.Prescription
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.coach.dossiers.DossierProvenance
import com.peakform.fitness.coach.dossiers.ExerciseDossier
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
 * BATCH-41 — The Meta-coach (auditor).
 *
 * Source: source/docs/system/16-the-meta-coach.md and
 * source/docs/architecture/20-the-meta-coach.md.
 *
 * Tests:
 *   1. audit() returns a healthy report for the current 31 coaches
 *   2. an envelope violation is detected when a coach returns too many sets
 *   3. a missing source is detected
 *   4. a duplicate exercise id is detected
 *   5. a dossier-coach mismatch is detected
 *   6. the audit does not write to state
 *   7. the audit runs in under 500ms
 *
 * The meta-coach never auto-corrects — every check is read-only.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch41Test {

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
        ProState.initialized = true
        ProState.data = com.peakform.fitness.core.WorkoutData(
            user = com.peakform.fitness.core.UserProfile(
                name = "Meta-coach Tester",
                birthDate = "1995-01-01",
                gender = "female",
                weight = 65.0,
                height = 170.0,
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
        MetaCoach.clearFindings()
    }

    @After
    fun tearDown() {
        MetaCoach.clearFindings()
        ProState.field = StateModel.FieldState()
        ProState.sessions = emptyList()
        ProState.recovery = StateModel.RecoveryState()
    }

    // 1. audit() returns a healthy report for the current 31 coaches
    //
    // The 31 authored coaches have been carefully authored to:
    //   - return prescriptions within their declared envelopes
    //   - cite source files that exist in sources/
    //   - claim disjoint exerciseIds (v35b explicitly removed collisions)
    //   - sit at version 1 (no authoring batch has bumped versions)
    //   - resolve stably through CoachRegistry
    //   - match the coachId named in each dossier that mentions them
    //
    // The audit must produce a healthy report (no WARN, no CRITICAL).
    @Test
    fun audit_returnsHealthyReportForCurrentCoaches() {
        val report = MetaCoach.audit()
        assertNotNull("audit must produce a report", report)
        assertTrue(
            "audit must be healthy for the 31 authored coaches (got ${report.findings.size} findings: ${report.findings.joinToString { "${it.severity}/${it.category}/${it.message}" }})",
            report.healthy,
        )
        // The 31 coaches must all be checked.
        val expectedCoachCount = 31
        // Each coach contributes at least one envelope sample (10 inputs) +
        // 1 provenance check + 1 coverage check. The audit reports per-finding
        // (not per-coach), so the finding count is not a simple function of the
        // coach count. We assert that the report exists and is healthy, which
        // transitively confirms every coach was checked.
        assertEquals(
            "CoachRegistry must list exactly 31 authored coaches",
            expectedCoachCount,
            CoachRegistry.authored.size,
        )
    }

    // 2. an envelope violation is detected when a coach returns too many sets
    //
    // Build a synthetic coach whose reconcile() returns sets = 99 (way above
    // envelope.maxSets = 5). auditCoach must emit at least one CRITICAL
    // finding with category "envelope" naming that coach.
    @Test
    fun audit_detectsEnvelopeViolationWhenCoachReturnsTooManySets() {
        val bogus = BogusEnvelopeCoach()
        val findings = MetaCoach.auditCoach(bogus)
        val envFindings = findings.filter { it.category == "envelope" && it.severity == Severity.CRITICAL }
        assertTrue(
            "audit must produce a CRITICAL envelope finding for a coach returning too many sets. Got: ${findings.joinToString { "${it.severity}/${it.category}: ${it.message}" }}",
            envFindings.isNotEmpty(),
        )
        // The message must reference the breach (sets=99 > maxSets=5).
        val first = envFindings.first()
        assertTrue(
            "envelope finding must mention the breached ceiling. Got: ${first.message}",
            first.message.contains("sets") && first.message.contains("99"),
        )
    }

    // 3. a missing source is detected
    //
    // A synthetic coach whose provenance.sourceFile points to a file that
    // does not exist in sources/ must produce a WARN finding with category
    // "provenance" and a message naming the missing file.
    @Test
    fun audit_detectsMissingSource() {
        val bogus = BogusProvenanceCoach()
        val findings = MetaCoach.auditCoach(bogus)
        val provFindings = findings.filter { it.category == "provenance" }
        assertTrue(
            "audit must produce a provenance finding for a missing source file. Got: ${findings.joinToString { "${it.severity}/${it.category}: ${it.message}" }}",
            provFindings.isNotEmpty(),
        )
        val first = provFindings.first()
        assertTrue(
            "provenance finding must name the missing file. Got: ${first.message}",
            first.message.contains("definitely_not_a_real_source.md"),
        )
    }

    // 4. a duplicate exercise id is detected
    //
    // Build two synthetic coaches that share the exerciseId "bogus_dup_id".
    // auditDuplicateExerciseIds must emit a CRITICAL finding naming both.
    @Test
    fun audit_detectsDuplicateExerciseId() {
        val a = SyntheticCoachA()
        val b = SyntheticCoachB()
        val findings = MetaCoach.auditDuplicateExerciseIds(listOf(a, b))
        val dupFindings = findings.filter { it.category == "coverage" && it.severity == Severity.CRITICAL }
        assertTrue(
            "audit must produce a CRITICAL coverage finding for duplicate exerciseId. Got: ${findings.joinToString { "${it.severity}/${it.category}: ${it.message}" }}",
            dupFindings.isNotEmpty(),
        )
        val first = dupFindings.first()
        assertTrue(
            "duplicate finding must name the shared id. Got: ${first.message}",
            first.message.contains("bogus_dup_id"),
        )
    }

    // 5. a dossier-coach mismatch is detected
    //
    // A synthetic dossier whose coachId does not match any coach in the
    // registry (e.g. "bogus_coach_id") must emit a WARN finding with
    // category "dossier" naming the dossier and the unresolved coachId.
    @Test
    fun audit_detectsDossierCoachMismatch() {
        val dossier = ExerciseDossier(
            exerciseId = "bogus_exercise_for_mismatch_test",
            name = "Bogus Exercise for Mismatch Test",
            coachId = "definitely_not_a_real_coach_id",
            instructions = emptyList(),
            provenance = DossierProvenance("", "", "synthetic"),
            components = emptyMap(),
            goals = emptyList(),
            prerequisites = emptyList(),
            progressionIn = emptyList(),
            progressionOut = emptyList(),
            stimulusProfile = emptyMap(),
            componentStimulus = emptyMap(),
            variations = emptyMap(),
            version = 1,
        )
        val findings = MetaCoach.auditDossierAlignment(listOf(dossier))
        val misFindings = findings.filter { it.category == "dossier" }
        assertTrue(
            "audit must produce a dossier finding for a mismatch. Got: ${findings.joinToString { "${it.severity}/${it.category}: ${it.message}" }}",
            misFindings.isNotEmpty(),
        )
        val first = misFindings.first()
        assertTrue(
            "dossier finding must name the bogus coachId. Got: ${first.message}",
            first.message.contains("definitely_not_a_real_coach_id"),
        )
    }

    // 6. the audit does not write to state
    //
    // The meta-coach is read-only by spec. Capture the field, recovery,
    // and sessions state before the audit; run the audit; verify the
    // state is byte-for-byte unchanged.
    @Test
    fun audit_doesNotWriteToState() {
        // Seed some state so we can detect a write (vs. "all empty"
        // which trivially looks unchanged after).
        val now = System.currentTimeMillis()
        ProState.field = StateModel.FieldState(
            muscles = mapOf(
                "quads" to StateModel.FieldSlot(current = 5.0, peak = 10.0, lastUpdate = now - 24 * 3_600_000L),
            ),
        )
        ProState.sessions = listOf(
            StateModel.SessionRecord(
                id = "s1",
                userId = "u1",
                timestamp = now - 24 * 3_600_000L,
                exercises = listOf(
                    StateModel.ExerciseRecord(
                        exerciseId = "back_squat",
                        sets = listOf(StateModel.SetRecord(reps = 5, load = 100.0, completed = true)),
                    ),
                ),
            ),
        )
        ProState.recovery = StateModel.RecoveryState()

        val fieldBefore = ProState.field
        val sessionsBefore = ProState.sessions
        val recoveryBefore = ProState.recovery

        val report = MetaCoach.audit()

        // State must be unchanged reference + value.
        assertSame("audit must not replace ProState.field reference", fieldBefore, ProState.field)
        assertSame("audit must not replace ProState.sessions reference", sessionsBefore, ProState.sessions)
        assertSame("audit must not replace ProState.recovery reference", recoveryBefore, ProState.recovery)
        assertEquals("field muscles must be unchanged",
            fieldBefore.muscles, ProState.field.muscles)
        assertEquals("sessions list must be unchanged",
            sessionsBefore, ProState.sessions)
        assertEquals("recovery curves must be unchanged",
            recoveryBefore.curves, ProState.recovery.curves)
        // The report is cached internally; that's a MetaCoach field,
        // NOT a state mutation. Verify the cache is populated (good —
        // the on-demand UI uses it).
        assertNotNull("MetaCoach.findings cache must be populated after audit", MetaCoach.findings)
        assertSame("the cached report must be the one audit returned", report, MetaCoach.findings)
    }

    // 7. the audit runs in under 500ms
    //
    // The spec requires that the audit completes within 500ms on a
    // JVM (Robolectric) test runner. The audit walks 31 coaches ×
    // 10 sample inputs + every dossier (1,413 entries) + every
    // exerciseId (about 70 ids) twice. On a modern CPU this is well
    // under 100ms; the 500ms ceiling gives plenty of headroom.
    @Test
    fun audit_runsInUnder500ms() {
        // Warm-up: the first call also pays for class-loading of
        // AuthoredRows / DossierRegistry data. The spec measures the
        // audit cost, not the JVM cold-start.
        MetaCoach.audit()
        MetaCoach.clearFindings()

        val start = System.currentTimeMillis()
        MetaCoach.audit()
        val took = System.currentTimeMillis() - start
        assertTrue(
            "audit must complete in under 500ms (took ${took}ms)",
            took < 500L,
        )
    }

    // ── Synthetic coaches for the detection tests ────────────────────────────
    //
    // These are NOT registered in CoachRegistry. They exist purely to
    // exercise the per-coach audit helpers with controlled inputs.

    /** A coach whose reconcile() returns sets=99 (envelope maxSets=5). */
    private class BogusEnvelopeCoach : ExerciseCoach {
        override val exerciseIds = setOf("bogus_envelope_test")
        override val movementName = "Bogus Envelope Coach"
        override val formCues = emptyList<String>()
        override val envelope = ExerciseEnvelope(maxSets = 5, minSets = 1, maxLoad = 100.0, maxDuration = 0.0, maxDistance = 0.0, maxPct1RM = 0.5)
        override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "synthetic")
        override val version = 1
        override fun requirements() = com.peakform.fitness.coach.authored.CoachRequirements(needs1RM = true, needsWeight = true)
        override fun message(input: com.peakform.fitness.core.CoachingEngine.CoachInput) = "synthetic"
        override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
            // 99 sets — well above the envelope maxSets of 5.
            return Prescription(sets = 99, reps = "5", pct1RM = 0.5, restSeconds = 60, sourceRowId = "bogus", note = "synthetic")
        }
    }

    /** A coach whose provenance.sourceFile does not exist in sources/. */
    private class BogusProvenanceCoach : ExerciseCoach {
        override val exerciseIds = setOf("bogus_provenance_test")
        override val movementName = "Bogus Provenance Coach"
        override val formCues = emptyList<String>()
        override val envelope = ExerciseEnvelope(maxSets = 5, minSets = 1, maxLoad = 100.0, maxDuration = 0.0, maxDistance = 0.0, maxPct1RM = 0.5)
        override val provenance = CoachProvenance("definitely_not_a_real_source.md", "Bogus section", 1, "synthetic")
        override val version = 1
        override fun requirements() = com.peakform.fitness.coach.authored.CoachRequirements(needs1RM = true, needsWeight = true)
        override fun message(input: com.peakform.fitness.core.CoachingEngine.CoachInput) = "synthetic"
        override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription {
            // Return a non-prescription so the envelope check passes.
            return Prescription(note = "synthetic — no row matched")
        }
    }

    /** A synthetic coach sharing "bogus_dup_id" with SyntheticCoachB. */
    private class SyntheticCoachA : ExerciseCoach {
        override val exerciseIds = setOf("bogus_dup_id", "a_only_id")
        override val movementName = "Synthetic Coach A"
        override val formCues = emptyList<String>()
        override val envelope = ExerciseEnvelope(maxSets = 5, minSets = 1, maxLoad = 100.0, maxDuration = 0.0, maxDistance = 0.0, maxPct1RM = 0.5)
        override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "synthetic")
        override val version = 1
        override fun requirements() = com.peakform.fitness.coach.authored.CoachRequirements()
        override fun message(input: com.peakform.fitness.core.CoachingEngine.CoachInput) = "synthetic"
        override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription =
            Prescription(note = "synthetic")
    }

    /** A second synthetic coach sharing "bogus_dup_id" with SyntheticCoachA. */
    private class SyntheticCoachB : ExerciseCoach {
        override val exerciseIds = setOf("bogus_dup_id", "b_only_id")
        override val movementName = "Synthetic Coach B"
        override val formCues = emptyList<String>()
        override val envelope = ExerciseEnvelope(maxSets = 5, minSets = 1, maxLoad = 100.0, maxDuration = 0.0, maxDistance = 0.0, maxPct1RM = 0.5)
        override val provenance = CoachProvenance("prilepin-1974/chart.md", "80-89% row", 1, "synthetic")
        override val version = 1
        override fun requirements() = com.peakform.fitness.coach.authored.CoachRequirements()
        override fun message(input: com.peakform.fitness.core.CoachingEngine.CoachInput) = "synthetic"
        override fun reconcile(rows: List<AuthoredRow>, intent: String, phase: String, bodyProfile: BodyProfile?): Prescription =
            Prescription(note = "synthetic")
    }
}
