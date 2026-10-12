# The truth layer

## What it is

The hardcoded exercise coaches are the truth of this system. Every exercise has an authored coach with real numbers from a real source. Those numbers do not compute. They are written down.

The graph substrate is the learner. It holds every source, every row, every citation in one structure. It can compare. It can propose. It cannot prescribe.

## The two layers

Hardcoded coaches:
- One per exercise, or per exercise variant.
- Authored numbers, cited to a published source.
- requirements(), envelope, provenance, version.
- reconcile() selects rows, never blends.
- explain() returns the sentence.
- The card reads from the coach.

Graph substrate:
- One node per source section, row, exercise, body profile, requirement, envelope bound.
- One edge per citation, declaration, bound.
- Read-only in any given batch.
- Provides walker.findRows(...) for tests and drift checks.

## The priority rule

Exact exercise id beats family. Hardcoded beats walked. Always.

1. Exact exerciseId in a coach's supportedExerciseIds.
2. Exact exerciseId in the walker's supported families.
3. Name pattern.
4. Muscle group fallback.
5. GenericCoach. Logs a warning. The audit treats it as a defect.

## What hardcoded means

Hardcoded does not mean unmovable. It means authored and reviewed.

Hardcode if:
- The exercise is a machine, a variant, or a modality whose numbers do not generalize.
- A named program prescribes it (Wendler 5/3/1, Smolov, McGill Big 3).
- Bodyweight tiers are the progression (pistol, one-arm push-up, front lever).
- It is a mobility drill with a joint-specific target.
- It is a recovery modality.

Walk if:
- The exercise is a family member whose prescription derives from published tables.
- Intent and phase produce one clear row from a shared source.
- No program override exists.

## The rule that keeps it credible

External sources never update the hardcoded layer automatically. Ever.

The path is:
1. Source arrives.
2. Graph node created.
3. Walker compares to hardcoded rows.
4. Proposal issued.
5. Human reviews.
6. Hardcoded coach updated.
7. Version bumped.
8. Push.

A human is in the loop for every change to the truth layer. An AI cannot decide a chart has changed. A human reads the chart and says so.

## What every coach carries

- Id, displayName, supportedExerciseIds, supportedNamePatterns.
- Envelope (bounds on what it may output).
- Provenance (the source citations).
- Version (bumped when the numbers change).
- requirements() — declares what the user must provide.
- reconcile(fragments) — selects a row, does not blend.
- explain(prescription, intent) — one sentence, coach voice.

## Reconcile, exactly

1. Gather authored rows for this exercise.
2. Filter by intent, phase, body_profile.
3. One match: return it.
4. Multiple matches: dominant intent picks. Others recorded as deferrals.
5. No matches: return empty prescription with a note. Never invent.

No blending. No interpolation. No compromise math. A prescription that is not a row is a bug.
