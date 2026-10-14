package com.peakform.fitness.coach.dossiers

/**
 * BATCH-37a — The dossier schema.
 *
 * Source: source/docs/system/03-the-dossier.md (the design contract).
 *
 * One dossier per exercise. Authored, cited, read-only at runtime.
 * The dossier is the *noun* of the system. The coach is the verb.
 * Every coach governs a family of exercises. Every exercise has its own
 * dossier.
 *
 * The dossier sits ABOVE the coach layer and adds what taxonomy cannot:
 * goals, prerequisites, progression chains, stimulus profiles. It is
 * independent of the taxonomy — never computed from it.
 *
 * If a dossier field is not sourced, it does not ship. If a source does
 * not cover an exercise, the exercise is marked "no source — not
 * prescribed." It does not receive a default value.
 *
 * Instances are loaded from `assets/data/dossiers.json` at runtime by
 * [DossierRegistry.ensure] — there is no hand-authored Kotlin dossier list
 * (a single Kotlin file with 1,413 inline object literals would exceed
 * the JVM 64KB `<clinit>` method size limit; the JSON asset is the
 * correct substrate for this volume of authored content).
 */

/**
 * One dossier per exercise. Authored, cited, read-only at runtime.
 *
 * @property exerciseId        the library id. Must match exactly. No aliases.
 * @property name              display name (matches the library name verbatim).
 * @property coachId           which coach governs this exercise (e.g. "back_squat"
 *                             routes to BackSquatAuthoredCoach). The coach supplies
 *                             requirements(), envelope, reconcile(). The dossier
 *                             supplies everything else.
 * @property instructions       the how-to. Real text. Copied verbatim from the
 *                             library where the library has it.
 * @property provenance         where the numbers come from. Source file, section,
 *                             author note.
 * @property components         the 11-item map (0..5). Authored. Never computed
 *                             from muscles. This is what shows on the card.
 * @property goals              1-3 per exercise. Each goal is a path the user can
 *                             declare. Each goal has a landmark (real threshold
 *                             string) and a source.
 * @property prerequisites      one per goal. What must be true of the person.
 *                             Authored, not derived.
 * @property progressionIn      exercise ids that lead here.
 * @property progressionOut     exercise ids this leads to.
 * @property stimulusProfile    muscle id to per-set contribution weight (0.0..1.0).
 *                             Real ratios, cited where possible, marked
 *                             "coach-authored, provisional" where not.
 * @property componentStimulus  component name to per-set contribution weight.
 *                             Same shape as stimulusProfile, for the 11 components.
 * @property variations         per goal, which ranges the coach will prescribe
 *                             from. Authored. The user does not edit these; the
 *                             coach uses them to compute today's numbers.
 * @property version             starts at 1. Bumped when the human reviews and
 *                             changes any number.
 */
@kotlinx.serialization.Serializable
data class ExerciseDossier(
    val exerciseId: String,
    val name: String,
    val coachId: String,
    val instructions: List<String>,
    val provenance: DossierProvenance,
    val components: Map<String, Int>,
    val goals: List<Goal>,
    val prerequisites: List<Prerequisite>,
    val progressionIn: List<String>,
    val progressionOut: List<String>,
    val stimulusProfile: Map<String, Double>,
    val componentStimulus: Map<String, Double>,
    val variations: Map<String, VariationRange>,
    val version: Int,
)

/**
 * A goal path the user can declare for an exercise.
 *
 * @property goalId              stable id (e.g. "advance_cited" or "advance_provisional").
 * @property target              the exercise id this goal advances toward
 *                               (the next exercise in the chain on this path).
 *                               Empty when no next exercise is known.
 * @property label                short user-facing label.
 * @property componentWeights     which components this goal emphasizes (0..5 map).
 * @property landmark            a real threshold string — what "done" looks like.
 *                               Authored sentence, not a number. Empty when
 *                               the source does not name a landmark (provisional
 *                               goal — see Batch37a CHANGES.md).
 * @property source               the source citation (e.g.
 *                               "sommer-2006/gymnastic-body-tier-table.md").
 *                               "no-source" for provisional goals.
 */
@kotlinx.serialization.Serializable
data class Goal(
    val goalId: String,
    val target: String,
    val label: String,
    val componentWeights: Map<String, Int>,
    val landmark: String,
    val source: String,
)

/**
 * What must be true of the person BEFORE they attempt this exercise.
 *
 * The requirement is a SENTENCE, not a number. Two people with the same rep
 * count can have very different form. A sentence lets the coach render
 * "3x8 clean, hollow body, 2s pause bottom. You have done 2x6 with hips
 * sagging." A number cannot say that.
 *
 * @property forGoalId   which goal this prerequisite gates.
 * @property exerciseId  the exercise id whose requirement this describes
 *                       (typically the current exercise — the prerequisite
 *                       says "before you do THIS exercise, you must...").
 * @property requirement  authored sentence, cited.
 * @property source       the source citation.
 */
@kotlinx.serialization.Serializable
data class Prerequisite(
    val forGoalId: String,
    val exerciseId: String,
    val requirement: String,
    val source: String,
)

/**
 * Per-goal variation range. The coach prescribes from these ranges — the
 * user does not edit them. Authored.
 *
 * @property forGoalId  which goal this variation serves.
 * @property min         range minimum (e.g. min reps, min tempo).
 * @property max         range maximum.
 * @property unit        "reps", "seconds", "tempo_bpm", etc.
 * @property note        authored note (e.g. "advance when top of range cleared").
 */
@kotlinx.serialization.Serializable
data class VariationRange(
    val forGoalId: String,
    val min: Double,
    val max: Double,
    val unit: String,
    val note: String,
)

/**
 * Where the dossier's numbers come from. Source file, section, author note.
 *
 * @property source      the source file path (e.g. "sommer-2006/gymnastic-body-tier-table.md").
 *                       "no-source" when no published source covers this exercise.
 * @property section      the section within the source (empty for "no-source").
 * @property authorNote   free-text note — used to mark "No published source
 *                        names this exercise. Dossier is structural only…"
 *                        when the source field is "no-source".
 */
@kotlinx.serialization.Serializable
data class DossierProvenance(
    val source: String,
    val section: String,
    val authorNote: String,
)
