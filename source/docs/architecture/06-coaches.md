# 06 - The coaches

Forty-two decision-makers. One hierarchy.

## The shape

1 Overall Coach. Assembly only. Never invents training math.

11 Component Coaches. One per training component: Strength, Hypertrophy, Endurance, Power, Flexibility/Mobility, Skill/Coordination, Balance, Agility, Speed, Body Composition, Longevity/Recovery.

30 Exercise Coaches. One per movement family. Reconcilers over PrescriptionFragments.

1 Chronicle. Narrator and healer.

1 Meta-Coach. Watches the coach. Drift detection, correction.

1 Two-Body Coach. When a training partner exists.

## How they cooperate

OverallCoach.planSession(day, user):

1. Component coaches assess and return targets.
2. Library selects exercises for the targets.
3. Each exercise's coach collects fragments from the field and dossier.
4. The coach reconciles fragments into a prescription.
5. A session plan is returned.

## Coach resolution order

1. Exact exerciseId in a coach's supportedExerciseIds.
2. Name pattern match.
3. Muscle group fallback.
4. GenericCoach (hardcoded rules, logs a warning).

Every exercise must resolve. A coverage test asserts zero default-only assignments unless a specific coach could not be written.

## What every coach declares

needs1RM, needsBand, needsBodyweight, needsDuration, needsDistance, needsPace, needsWeight.

The logging UI asks only for what the coach declares. Plank never asks for 1RM.

## What every coach must have

- One-paragraph philosophy comment.
- Hardcoded prescriptions. Real numbers. No shared formulas.
- Provenance header: coach expert, research, experience.
- Version number.
- Its own explain() text in coach voice.

## Response shape

prescription, note, modification (ACCEPTED/MODIFIED/REDIRECTED/REFUSED), warnings, questions, refusal.

The coach is not an obedient puppet. Real coaches analyze, warn, negotiate, sometimes refuse.

## What coaches never do

- Never invent math.
- Never bypass the envelope.
- Never speak without provenance.
- Never write outside the state tree.
- Never show UI directly.