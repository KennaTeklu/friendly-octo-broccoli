# 14 - The highway and the long tail

## The two parts of the system

**The highway** — the coaches. 31 today, ~60 target.
Each governs a family of exercises. Each supplies
requirements(), envelope, provenance, reconcile(),
explain(). The highway is what makes every exercise
prescribable.

**The long tail** — the dossiers. 1,413 exercises. Each
has its own knowledge: goals, prerequisites,
progressions, stimulus profile. The long tail is what
makes each exercise unique.

## Why both

A coach alone cannot prescribe 1,413 different
exercises. BackSquatAuthoredCoach knows how to
prescribe back squats. It does not know what a
front squat is, what a zercher squat is, what a
hack squat is. It cannot tell the user which one comes
first.

A dossier alone cannot prescribe anything. It has
prerequisites and a landmark, but no numbers. It
cannot say "do 5x5 at 80%."

Together: the coach supplies the numbers. The dossier
supplies the uniqueness. The card shows both.

## The routing

User opens an exercise:

    1. Library lookup -> exerciseId
    2. Dossier lookup -> this dossier
    3. dossier.coachId -> the coach
    4. coach.reconcile(exerciseId, goal, phase, profile)
    5. coach returns prescription built from
       dossier's goals + coach's authored rows

The dossier is the on-ramp. The coach is the highway.

## Why this scales

Adding exercise #1,414:

    1. New dossier for the exercise
    2. Route it to an existing coach (family match)
    3. Coach already knows how to prescribe

No new coach needed unless the exercise belongs to a
family that has no coach. The highway grows slowly
(~60 total). The dossiers grow to 1,413+.

## What the highway knows

Each coach:

- Requirements: 1RM, band, bodyweight tier, duration,
  distance, pace, weight
- Envelope: sets range, load range, %1RM range,
  duration range
- Provenance: source file, section, coach version
- Rows: one per (intent, phase, training_age, sex,
  body_goal)
- Reconcile: selects one row, never blends
- Explain: one declaration sentence

Everything authored, cited, reviewed.

## What the long tail knows

Each dossier:

- The exercise's unique instructions
- Its muscles, components (authored, not computed)
- Its goal paths
- Its prerequisites
- Its stimulus profile (the transfer ratios)
- Its place in a progression chain

## The rule

Route by family. Prescribe by coach. Uniqueness by
dossier. Cite by source.

Never let a dossier hold prescription numbers. Never
let a coach hold exercise-specific prerequisites. The
split is clean.
