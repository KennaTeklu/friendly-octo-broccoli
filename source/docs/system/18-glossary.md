# 18 - Glossary

## A

**Authored row** — a prescription written down in a
source-cited table. Never computed. Stored in
AuthoredRows.kt.

**Authored coach** — a coach class (e.g.
BackSquatAuthoredCoach) with requirements(), envelope,
provenance, version, reconcile(), explain(). Never
computes. Selects from authored rows.

## B

**Batch** — one delivery. One prompt. One zip. One
commit. Named with a letter (35a) or a version (v34).

**Blocking muscle** — the primary muscle of an exercise
that has the lowest readiness. Determines whether the
exercise is locked.

## C

**Coach** — the decision-maker. Reads field, dossier,
goal, history. Produces prescription. Never invents.

**Component** — one of the 11 training components:
Strength, Hypertrophy, Endurance, Power, Flexibility,
Skill, Balance, Agility, Speed, BodyComposition,
Longevity.

**ComponentStimulus** — per exercise, per-component
per-set contribution weight. Authored in the dossier.

**Chronicle** — the narrator and healer. Reads field,
tells the story, repairs inconsistent state. Never
prescribes.

## D

**Declared goal** — the user's choice of path for a
specific exercise. One string per (user, exercise).

**Dossier** — one per exercise. Authored knowledge:
goals, prerequisites, progressions, stimulus profile,
instructions, provenance. Read-only at runtime.

**Drift** — when a row's value no longer matches its
source. Detected by the drift check. Reported, never
auto-corrected.

## E

**Envelope** — the bounds a coach's output must obey.
Max sets, min sets, max load, max %1RM, max duration,
max distance.

## F

**Field** — the shared substrate. Values across [user
x muscle x component]. Decays with time. Written by
sessions, read by coaches.

## G

**Goal** — a path an exercise can serve. Authored per
exercise. Landmark, component weights, source.

## H

**Highway** — the coach layer. 31 today, ~60 target.
Each governs a family of exercises.

## L

**Landmark** — the real threshold a goal tracks toward.
A sentence, not a number. Cited.

**Long tail** — the dossier layer. 1,413 exercises,
each with its own authored knowledge.

## M

**Meta-coach** — the auditor. Watches coaches for
drift, contradiction, envelope violations. Reports.
Never modifies.

## P

**Prerequisite** — what must be true of the person
before they attempt an exercise under a specific
goal. A sentence, not a number. Authored, cited.

**Progression** — the exercise before or after this
one in a chain. Bidirectional edge. Authored.

**Provenance** — the source citation for a number.
File, section, author note, version.

## R

**Readiness** — the field's answer to "how recovered
is muscle m for user u?" A value 0..1. Derived from
current stimulus, peak stimulus, and the recovery
curve.

**Recovery curve** — per user, per muscle. Exponential
decay time constant. Learned from session history.
Default authored per family.

**Response rate** — per user, per component, per
muscle. The speed at which this body adapts to this
component. Learned. Prior is neutral (1.0).

## S

**Session** — one logged unit of work. One user, one
time, multiple exercises. Contains sets, reps, load,
tempo, RPE, felt.

**Stimulus profile** — per exercise, per-muscle per-set
contribution weight. Authored. Feeds cross-exercise
transfer.

## T

**Taxonomy** — the per-exercise record of muscles,
equipment, category, components, difficulty. Used for
search, chips, and gating. Not used for prescription.

**Transfer** — the implicit relationship between two
exercises, computed from the product of their
stimulus profiles. No pair is stored. Computed on
demand.

## V

**Variation** — a range the coach may prescribe from
for a specific goal. Not a user-editable field.
Authored.

**versionCode** — the APK's integer version. Increments
by 1 per batch.

**versionName** — the APK's display version. Bumped per
letter series (e.g. 3.3.0 -> 3.3.1).

## W

**Walker** — the graph traversal interface over the
dossier web. `walker.findRows(...)`, `walker.chainFor(...)`.
Used by tests and the drift check.
