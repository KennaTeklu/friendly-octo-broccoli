# 03 - The dossier

## What it is

One dossier per exercise. 1,413 today (in the target
count). Authored, cited, read-only at runtime.

The dossier is the *noun* of the system. The coach is
the verb. Every coach governs a family of exercises.
Every exercise has its own dossier.

## The schema

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

    data class Goal(
        val goalId: String,
        val target: String,
        val label: String,
        val componentWeights: Map<String, Int>,
        val landmark: String,
        val source: String,
    )

    data class Prerequisite(
        val forGoalId: String,
        val exerciseId: String,
        val requirement: String,
        val source: String,
    )

    data class VariationRange(
        val forGoalId: String,
        val min: Double,
        val max: Double,
        val unit: String,
        val note: String,
    )

    data class DossierProvenance(
        val source: String,
        val section: String,
        val authorNote: String,
    )

## Field-by-field

**exerciseId** — the library id. Must match exactly.
No aliases. If the library uses "barbell_back_squat",
the dossier uses "barbell_back_squat".

**coachId** — which coach governs this exercise.
"back_squat" routes to BackSquatAuthoredCoach.
The coach supplies requirements(), envelope, reconcile().
The dossier supplies everything else.

**instructions** — the how-to. Real text. Already in
the library for most exercises. Copy verbatim.

**provenance** — where the numbers come from. Source
file, section, author note.

**components** — the 11-item map (0..5). Authored.
Never computed from muscles. This is what shows on
the card.

**goals** — 1-3 per exercise. Each goal is a path the
user can declare. Each goal has a landmark (real
threshold string) and a source.

**prerequisites** — one per goal. What must be true of
the person. Authored, not derived.

**progressionIn / progressionOut** — exercise ids.
The chain that leads here and the chain this leads to.
Bidirectional.

**stimulusProfile** — muscle id to per-set contribution
weight (0.0..1.0). This is what makes cross-exercise
transfer possible. Real ratios, cited where possible,
marked "coach-authored, provisional" where not.

**componentStimulus** — component name to per-set
contribution weight. Same shape as stimulusProfile,
for the 11 components.

**variations** — per goal, which ranges the coach will
prescribe from. Authored. The user does not edit
these; the coach uses them to compute today's numbers.

**version** — starts at 1. Bumped when the human
reviews and changes any number.

## What a dossier is NOT

- Not computed from taxonomy.
- Not grouped by category.
- Not inferred from name.
- Not derived from muscles alone.
- Not shared across exercises.

## The rule

If a dossier field is not sourced, it does not ship.
If a source does not cover an exercise, the exercise
is marked "no source — not prescribed." It does not
receive a default value.
