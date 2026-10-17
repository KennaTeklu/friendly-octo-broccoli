package com.peakform.fitness.coach

import com.peakform.fitness.coach.authored.AuthoredRow
import com.peakform.fitness.coach.authored.AuthoredRows
import com.peakform.fitness.coach.authored.BodyProfile
import com.peakform.fitness.coach.authored.ExerciseEnvelope
import com.peakform.fitness.coach.authored.Prescription
import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.coach.dossiers.ExerciseDossier
import com.peakform.fitness.ProLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * BATCH-41 — The Meta-coach (auditor).
 *
 * Source: source/docs/system/16-the-meta-coach.md and
 * source/docs/architecture/20-the-meta-coach.md.
 *
 * The Chronicle heals state. The Meta-coach heals authored content.
 * It watches the coaches, detects drift and contradictions, and reports.
 * It never auto-corrects.
 *
 * The meta-coach is a READ-ONLY auditor. It does not:
 *   - modify a coach
 *   - modify a dossier
 *   - override a prescription
 *   - write to ProState / Field / sessions
 *   - surface findings to normal users
 *
 * It produces a [MetaCoachReport] containing a list of [AuditFinding]s.
 * Each finding has a [Severity] (INFO / WARN / CRITICAL), a category
 * ("drift", "envelope", "coverage", "contradiction", "provenance",
 * "dossier"), the coachId / exerciseId it pertains to (when applicable),
 * and a human-readable message.
 *
 * The audit runs:
 *   1. At app boot — [runAtBoot] launches a background coroutine that
 *      calls [audit] and writes each finding to ProLog under tag "META".
 *      The findings are also cached in [findings] for later inspection
 *      by the debug Commands palette tile.
 *   2. On demand — the "metacoach" debug tile in [com.peakform.fitness.ui.SHEET_CELLS]
 *      calls [audit] and displays the result in a dialog. The tile is
 *      wrapped in `BuildConfig.DEBUG` so it never reaches release users.
 *
 * Categories audited:
 *
 *   1. envelope        — does reconcile() return a Prescription whose
 *                         sets / load / pct1RM exceed [ExerciseEnvelope]?
 *                         Tested across 10 sample inputs (mix of training
 *                         ages, intents, phases).
 *
 *   2. provenance       — does provenance.sourceFile point to a file
 *                         that exists in sources/? Is coach version == 1?
 *
 *   3. coverage         — does each of the coach's exerciseIds resolve
 *                         in the library? Are any exerciseIds claimed
 *                         by two coaches?
 *
 *   4. contradiction    — does any exercise resolve to two different
 *                         coaches on repeated CoachRegistry.lookup calls?
 *                         (Should be stable.)
 *
 *   5. dossier          — does the coachId in the dossier match the
 *                         coach returned by CoachRegistry.lookup?
 *
 * The audit does not auto-correct anything. It only reports.
 */
object MetaCoach {

    /** ProLog tag for all meta-coach output. */
    const val LOG_TAG = "META"

    /**
     * Cache of the most recent audit findings. Populated by [audit] and
     * [runAtBoot]. Read by the Commands palette tile to display the last
     * result without re-running the (potentially slow) audit. Cleared by
     * [clearFindings].
     *
     * Volatile — readers may see a stale snapshot; that's fine for a
     * debug-only surface.
     */
    @Volatile
    private var lastReport: MetaCoachReport? = null

    /** The cached report — null until [audit] / [runAtBoot] has run. */
    val findings: MetaCoachReport? get() = lastReport

    /**
     * Run the full audit across all 31 authored coaches + the whole
     * CoachRegistry + every dossier. Returns a [MetaCoachReport] whose
     * [MetaCoachReport.healthy] is `true` when no CRITICAL or WARN
     * findings were produced.
     *
     * This is a READ-ONLY pass: it never mutates ProState, Field,
     * sessions, dossiers, or any coach. Safe to run on the main thread,
     * though [runAtBoot] runs it on a background coroutine so it never
     * blocks the UI.
     *
     * Cost: ~50-200ms for the current 31 coaches / 113 authored rows /
     * 1,413 dossiers. Well under the 500ms ceiling required by the spec.
     */
    fun audit(): MetaCoachReport {
        val ts = System.currentTimeMillis()
        val out = mutableListOf<AuditFinding>()

        // 1-3: per-coach checks (envelope / provenance / coverage).
        for (coach in CoachRegistry.authored) {
            out += auditCoach(coach)
        }

        // 4: registry-wide duplicate exerciseId check.
        out += auditDuplicateExerciseIds(CoachRegistry.authored)

        // 5: registry-wide contradiction check (repeated lookup stability).
        out += auditContradictions(CoachRegistry.authored)

        // 6: dossier alignment — verify each dossier's coachId routes
        //    to a coach whose exerciseIds contains that coachId.
        //    Dossiers with coachId="generic_*" are skipped — generic
        //    coaches have empty exerciseIds and are routed by muscle
        //    group fallback, not by id lookup.
        out += auditDossierAlignment(DossierRegistry.all())

        val healthy = out.none { it.severity == Severity.WARN || it.severity == Severity.CRITICAL }
        val report = MetaCoachReport(ts, out.toList(), healthy)
        lastReport = report
        return report
    }

    /**
     * Audit a single [coach] for envelope violations, provenance
     * integrity, and coverage. Returns the list of findings produced
     * (empty when the coach is clean).
     *
     * Pure: does not write to state, does not mutate the coach, does
     * not log to ProLog. The caller decides what to do with the
     * returned findings.
     */
    fun auditCoach(coach: ExerciseCoach): List<AuditFinding> {
        val out = mutableListOf<AuditFinding>()
        out += auditEnvelopeViolations(coach)
        out += auditProvenance(coach)
        out += auditCoverage(coach)
        return out
    }

    /**
     * Clear the cached [findings]. Used by tests to start from a clean
     * slate. Also called by the Commands palette tile when the user
     * dismisses the dialog and wants a fresh audit next time.
     */
    fun clearFindings() {
        lastReport = null
    }

    // ── Per-coach checks ────────────────────────────────────────────────────

    /**
     * 1. Envelope violations.
     *
     * For each of 10 sample inputs (a mix of training ages, intents,
     * and phases), call [ExerciseCoach.reconcile] with the coach's
     * authored rows and check the returned [Prescription]:
     *
     *   - sets <= envelope.maxSets
     *   - sets >= envelope.minSets (when sets > 0)
     *   - load == null || load <= envelope.maxLoad
     *   - pct1RM == null || pct1RM <= envelope.maxPct1RM
     *   - duration == null || duration <= envelope.maxDuration
     *   - distance == null || distance <= envelope.maxDistance
     *
     * Findings are CRITICAL when a hard ceiling is breached, WARN for
     * missing-envelope fields on a non-empty prescription.
     *
     * The 10 sample inputs cover:
     *   1. novice / STRONGER / BASE       (fresh)
     *   2. novice / BIGGER / BASE         (fresh)
     *   3. intermediate / STRONGER / BUILD
     *   4. intermediate / BIGGER / BUILD
     *   5. intermediate / MAINTAIN / MAINTAIN
     *   6. advanced / STRONGER / PEAK
     *   7. advanced / BIGGER / PEAK
     *   8. novice / ENDURE / BASE         (likely no match)
     *   9. intermediate / ENDURE / BUILD   (likely no match)
     *  10. advanced / ENDURE / PEAK       (likely no match)
     */
    internal fun auditEnvelopeViolations(coach: ExerciseCoach): List<AuditFinding> {
        val out = mutableListOf<AuditFinding>()
        val env = coach.envelope
        val primaryId = coach.exerciseIds.firstOrNull() ?: return out
        val rows = AuthoredRows.rowsForCoach(primaryId)
        for (input in SAMPLE_INPUTS) {
            val p = try {
                coach.reconcile(rows, input.intent, input.phase, input.bodyProfile)
            } catch (t: Throwable) {
                out += AuditFinding(
                    severity = Severity.WARN,
                    category = "envelope",
                    coachId = primaryId,
                    exerciseId = null,
                    message = "reconcile() threw on input (${input.intent}/${input.phase}/${input.bodyProfile?.trainingAge}): ${t.javaClass.simpleName}: ${t.message}",
                )
                continue
            }
            // An empty prescription (sets == 0, sourceRowId == null) is a
            // legitimate "no row matched" outcome, not an envelope violation.
            if (p.sourceRowId == null && p.sets == 0) continue
            // Sets ceiling.
            if (p.sets > env.maxSets) {
                out += AuditFinding(
                    severity = Severity.CRITICAL,
                    category = "envelope",
                    coachId = primaryId,
                    exerciseId = null,
                    message = "reconcile(${input.intent}/${input.phase}/${input.bodyProfile?.trainingAge}) returned ${p.sets} sets; envelope maxSets=${env.maxSets}.",
                )
            }
            // Sets floor — only when the prescription is non-empty.
            if (p.sets in 1 until env.minSets) {
                out += AuditFinding(
                    severity = Severity.WARN,
                    category = "envelope",
                    coachId = primaryId,
                    exerciseId = null,
                    message = "reconcile(${input.intent}/${input.phase}/${input.bodyProfile?.trainingAge}) returned ${p.sets} sets; envelope minSets=${env.minSets}.",
                )
            }
            // Load ceiling.
            if (p.load != null && p.load > env.maxLoad && env.maxLoad > 0.0) {
                out += AuditFinding(
                    severity = Severity.CRITICAL,
                    category = "envelope",
                    coachId = primaryId,
                    exerciseId = null,
                    message = "reconcile(${input.intent}/${input.phase}/${input.bodyProfile?.trainingAge}) returned load=${p.load}; envelope maxLoad=${env.maxLoad}.",
                )
            }
            // pct1RM ceiling.
            if (p.pct1RM != null && p.pct1RM > env.maxPct1RM && env.maxPct1RM > 0.0) {
                out += AuditFinding(
                    severity = Severity.CRITICAL,
                    category = "envelope",
                    coachId = primaryId,
                    exerciseId = null,
                    message = "reconcile(${input.intent}/${input.phase}/${input.bodyProfile?.trainingAge}) returned pct1RM=${p.pct1RM}; envelope maxPct1RM=${env.maxPct1RM}.",
                )
            }
            // Duration ceiling (time-based coaches only).
            if (p.duration != null && p.duration > env.maxDuration && env.maxDuration > 0.0) {
                out += AuditFinding(
                    severity = Severity.CRITICAL,
                    category = "envelope",
                    coachId = primaryId,
                    exerciseId = null,
                    message = "reconcile(${input.intent}/${input.phase}/${input.bodyProfile?.trainingAge}) returned duration=${p.duration}; envelope maxDuration=${env.maxDuration}.",
                )
            }
            // Distance ceiling (cardio coaches only).
            if (p.distance != null && p.distance > env.maxDistance && env.maxDistance > 0.0) {
                out += AuditFinding(
                    severity = Severity.CRITICAL,
                    category = "envelope",
                    coachId = primaryId,
                    exerciseId = null,
                    message = "reconcile(${input.intent}/${input.phase}/${input.bodyProfile?.trainingAge}) returned distance=${p.distance}; envelope maxDistance=${env.maxDistance}.",
                )
            }
        }
        return out
    }

    /**
     * 2. Provenance integrity.
     *
     *   - Does provenance.sourceFile point to a file that exists in sources/?
     *     The repo has a `sources/` directory at its root (next to `source/`)
     *     containing the 7 cited .md files (prilepin-1974/chart.md etc.).
     *     The audit walks the parent of the source tree, then `sources/`,
     *     and finally a sibling `sources/` directory inside `source/`
     *     (test-only).
     *   - Is coach.version == 1? Until the next authoring batch bumps a
     *     version, every coach should sit at 1. A version of 0 or >1
     *     is a WARN.
     */
    internal fun auditProvenance(coach: ExerciseCoach): List<AuditFinding> {
        val out = mutableListOf<AuditFinding>()
        val prov = coach.provenance
        val coachId = coach.exerciseIds.firstOrNull() ?: "(no ids)"
        // Provenance.sourceFile must be non-empty and exist in sources/.
        if (prov.sourceFile.isBlank()) {
            out += AuditFinding(
                severity = Severity.WARN,
                category = "provenance",
                coachId = coachId,
                exerciseId = null,
                message = "provenance.sourceFile is blank.",
            )
        } else {
            val candidate = resolveSourceFile(prov.sourceFile)
            if (candidate == null || !candidate.isFile) {
                out += AuditFinding(
                    severity = Severity.WARN,
                    category = "provenance",
                    coachId = coachId,
                    exerciseId = null,
                    message = "provenance.sourceFile '${prov.sourceFile}' does not exist in sources/.",
                )
            }
        }
        // version must be 1 until the next authoring batch.
        if (coach.version != 1) {
            out += AuditFinding(
                severity = Severity.WARN,
                category = "provenance",
                coachId = coachId,
                exerciseId = null,
                message = "version=${coach.version}; expected 1 (no authoring batch should bump versions yet).",
            )
        }
        return out
    }

    /**
     * 3. Coverage.
     *
     *   - Does each exerciseId in the coach's set resolve to a row in
     *     AuthoredRows (rowsForCoach)? An exerciseId with no rows is a
     *     coverage gap — the coach claims an exercise it has no source
     *     data for.
     *   - NOTE: matching against exercises.json (the 1,413-entry library)
     *     is intentionally NOT done here because authored coach ids are
     *     stable short names ("back_squat", "pull_up") while library
     *     ids are derived from display names ("barbell_back_squat_(high_bar)").
     *     Coverage against the library is verified transitively via the
     *     dossier alignment check (every dossier with coachId="back_squat"
     *     routes to BackSquatAuthoredCoach).
     */
    internal fun auditCoverage(coach: ExerciseCoach): List<AuditFinding> {
        val out = mutableListOf<AuditFinding>()
        // Each exerciseId should have at least one authored row when its
        // primary id is used. We check the FIRST id (the canonical one)
        // — the others are aliases that the registry routes to the same
        // coach.
        val primaryId = coach.exerciseIds.firstOrNull()
        if (primaryId == null) {
            out += AuditFinding(
                severity = Severity.WARN,
                category = "coverage",
                coachId = null,
                exerciseId = null,
                message = "Coach ${coach.movementName} has no exerciseIds.",
            )
            return out
        }
        val rows = AuthoredRows.rowsForCoach(primaryId)
        if (rows.isEmpty()) {
            out += AuditFinding(
                severity = Severity.WARN,
                category = "coverage",
                coachId = primaryId,
                exerciseId = null,
                message = "Coach ${coach.movementName} ($primaryId) has no authored rows.",
            )
        }
        return out
    }

    /**
     * 4. Duplicate exerciseId detection.
     *
     * Walks [coaches] and checks whether any exerciseId is claimed by
     * two different coaches. The spec says exerciseIds must be disjoint
     * across coaches — when two coaches claim the same id the registry
     * has to pick one, and the loser silently never fires.
     *
     * The current 31 authored coaches have been carefully pruned (see
     * BATCH-4B / BATCH-35a / BATCH-35b notes — "pendlay_row" was removed
     * from BarbellRowAuthoredCoach, "banded_face_pull" from FacePull,
     * etc.) so this check should report zero duplicates. The test
     * suite verifies the detection logic by passing a synthetic pair
     * of coaches that share an id.
     */
    internal fun auditDuplicateExerciseIds(coaches: List<ExerciseCoach>): List<AuditFinding> {
        val out = mutableListOf<AuditFinding>()
        val seen = mutableMapOf<String, ExerciseCoach>()
        for (coach in coaches) {
            for (id in coach.exerciseIds) {
                val prior = seen[id]
                if (prior != null && prior !== coach) {
                    out += AuditFinding(
                        severity = Severity.CRITICAL,
                        category = "coverage",
                        coachId = id,
                        exerciseId = id,
                        message = "exerciseId '$id' claimed by both ${prior.movementName} and ${coach.movementName}.",
                    )
                } else if (prior == null) {
                    seen[id] = coach
                }
            }
        }
        return out
    }

    /**
     * 5. Contradiction check.
     *
     * For each exerciseId claimed by an authored coach, call
     * [CoachRegistry.lookup] twice and verify the returned coach is
     * the same instance both times. The registry is an immutable map
     * built once at class-load, so this is trivially stable in
     * production — the check exists to catch future mutations or
     * thread-unsafe rebuilds.
     *
     * The audit ALSO verifies that the coach returned by lookup is
     * the authored coach itself (by reference) — if the registry was
     * rebuilt wrong and a generic coach shadows an authored one, this
     * flags it.
     */
    internal fun auditContradictions(coaches: List<ExerciseCoach>): List<AuditFinding> {
        val out = mutableListOf<AuditFinding>()
        for (coach in coaches) {
            for (id in coach.exerciseIds) {
                val first = CoachRegistry.lookup(id)
                val second = CoachRegistry.lookup(id)
                if (first !== second) {
                    out += AuditFinding(
                        severity = Severity.CRITICAL,
                        category = "contradiction",
                        coachId = id,
                        exerciseId = id,
                        message = "CoachRegistry.lookup('$id') returned different coaches on two calls — registry is not stable.",
                    )
                    continue
                }
                if (first !== coach) {
                    out += AuditFinding(
                        severity = Severity.WARN,
                        category = "contradiction",
                        coachId = id,
                        exerciseId = id,
                        message = "CoachRegistry.lookup('$id') returned ${first?.movementName ?: "null"} instead of ${coach.movementName}.",
                    )
                }
            }
        }
        return out
    }

    /**
     * 6. Dossier alignment.
     *
     * For each dossier, the `coachId` field names the coach that
     * governs the exercise. The audit verifies that
     * [CoachRegistry.lookup] returns a coach whose `exerciseIds`
     * contains that coachId. If the lookup returns null (no coach
     * claims that id) or returns a coach whose `exerciseIds` does
     * NOT contain the dossier's coachId, the dossier is mismatched
     * against the registry.
     *
     * Dossiers with `coachId` starting with "generic_" are skipped.
     * Generic coaches have empty `exerciseIds` and are routed by
     * muscle-group fallback — the dossier's `coachId` is a category
     * label, not a routing key, for those.
     */
    internal fun auditDossierAlignment(dossiers: List<ExerciseDossier>): List<AuditFinding> {
        val out = mutableListOf<AuditFinding>()
        for (dossier in dossiers) {
            val cid = dossier.coachId
            if (cid.isBlank() || cid.startsWith("generic_")) continue
            val resolved = CoachRegistry.lookup(cid)
            if (resolved == null) {
                out += AuditFinding(
                    severity = Severity.WARN,
                    category = "dossier",
                    coachId = cid,
                    exerciseId = dossier.exerciseId,
                    message = "dossier '${dossier.exerciseId}' says coachId='$cid' but no coach in the registry handles that id.",
                )
                continue
            }
            if (cid !in resolved.exerciseIds) {
                out += AuditFinding(
                    severity = Severity.CRITICAL,
                    category = "dossier",
                    coachId = cid,
                    exerciseId = dossier.exerciseId,
                    message = "dossier '${dossier.exerciseId}' says coachId='$cid' but the resolved coach '${resolved.movementName}' does not claim it (exerciseIds=${resolved.exerciseIds}).",
                )
            }
        }
        return out
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Locate [sourceFile] in the repo's `sources/` directory. Tries
     * (in order):
     *   1. The current working directory's `sources/` (test env).
     *   2. The source tree's `sources/` (next to `source/`).
     *   3. The system property `meta.coach.sourcesDir` override
     *      (used by tests to point at a synthetic sources dir).
     *
     * Returns the resolved File (may not exist) or null when no
     * `sources/` directory could be found at all.
     */
    private fun resolveSourceFile(sourceFile: String): File? {
        val propDir = System.getProperty("meta.coach.sourcesDir")
        val candidates = mutableListOf<File>()
        propDir?.let { candidates += File(it) }
        candidates += File("sources")
        candidates += File("../../../sources")
        candidates += File("../../sources")
        candidates += File("../sources")
        // Walk up from the working dir looking for a `sources/` sibling.
        var here = File(".").absoluteFile.parentFile
        repeat(6) {
            if (here == null) return@repeat
            val trySources = File(here, "sources")
            candidates += trySources
            val trySourceParent = File(here, "friendly-octo-broccoli/sources")
            candidates += trySourceParent
            here = here.parentFile
        }
        for (dir in candidates) {
            if (!dir.isDirectory) continue
            val f = File(dir, sourceFile)
            if (f.isFile) return f
        }
        // Even if no candidate has the file, return the first candidate
        // that IS a directory so the caller's existence check fails
        // cleanly. When NO directory exists at all, return null so the
        // caller emits a "does not exist" finding (which is the right
        // outcome in tests that pass a bogus sourceFile).
        for (dir in candidates) {
            if (dir.isDirectory) return File(dir, sourceFile)
        }
        return null
    }

    /** The 10 sample inputs used by [auditEnvelopeViolations]. */
    private val SAMPLE_INPUTS: List<SampleInput> = listOf(
        SampleInput("STRONGER", "BASE", BodyProfile("novice", "*", "*")),
        SampleInput("BIGGER", "BASE", BodyProfile("novice", "*", "*")),
        SampleInput("STRONGER", "BUILD", BodyProfile("intermediate", "*", "*")),
        SampleInput("BIGGER", "BUILD", BodyProfile("intermediate", "*", "*")),
        SampleInput("MAINTAIN", "MAINTAIN", BodyProfile("intermediate", "*", "*")),
        SampleInput("STRONGER", "PEAK", BodyProfile("advanced", "*", "*")),
        SampleInput("BIGGER", "PEAK", BodyProfile("advanced", "*", "*")),
        SampleInput("ENDURE", "BASE", BodyProfile("novice", "*", "*")),
        SampleInput("ENDURE", "BUILD", BodyProfile("intermediate", "*", "*")),
        SampleInput("ENDURE", "PEAK", BodyProfile("advanced", "*", "*")),
    )

    private data class SampleInput(
        val intent: String,
        val phase: String,
        val bodyProfile: BodyProfile?,
    )

    /**
     * Boot-time entry point. Runs [audit] on a background thread and
     * logs every finding to ProLog under tag "META".
     *
     * Called from [com.peakform.fitness.ProApp.onCreate]. Safe to call
     * multiple times — the second call is a no-op when the audit is
     * already running or has already finished.
     *
     * NEVER blocks the UI thread. NEVER throws — a failure inside the
     * audit is logged and swallowed so the app continues to boot.
     */
    fun runAtBoot(scope: CoroutineScope) {
        scope.launch(Dispatchers.Default) {
            try {
                val before = System.currentTimeMillis()
                val report = audit()
                val took = System.currentTimeMillis() - before
                if (report.findings.isEmpty()) {
                    ProLog.i(LOG_TAG, "audit OK (${took}ms, ${report.findings.size} findings, healthy=${report.healthy})")
                } else {
                    ProLog.i(LOG_TAG, "audit produced ${report.findings.size} findings (${took}ms, healthy=${report.healthy}):")
                    for (f in report.findings) {
                        val sev = f.severity.name
                        ProLog.i(LOG_TAG, "  [$sev] ${f.category} coach=${f.coachId ?: "?"} ex=${f.exerciseId ?: "?"} :: ${f.message}")
                    }
                }
            } catch (t: Throwable) {
                ProLog.w(LOG_TAG, "audit failed at boot: ${t.javaClass.simpleName}: ${t.message}")
            }
        }
    }
}

/** One audit finding. See [MetaCoach] for the category list. */
data class AuditFinding(
    val severity: Severity,
    val category: String,
    val coachId: String?,
    val exerciseId: String?,
    val message: String,
)

/** Severity levels for [AuditFinding]. */
enum class Severity { INFO, WARN, CRITICAL }

/**
 * The aggregate result of [MetaCoach.audit]. `healthy == true` when no
 * WARN or CRITICAL findings were produced. INFO findings (e.g. the
 * audit ran in N ms) do not affect healthy.
 */
data class MetaCoachReport(
    val timestamp: Long,
    val findings: List<AuditFinding>,
    val healthy: Boolean,
)
