# source/docs/system — the implementation book

A complete explanation of how PeakForm works, in the
order it is built.

## Reading paths

**New to the project.** Start at 01-the-thesis.md.
Then 02-the-four-layers.md. That is the shape.

**Writing a dossier.** Read 03-the-dossier.md,
04-stimulus-profiles.md, 05-goals-and-paths.md,
06-prerequisites-and-progressions.md.

**Understanding the field.** Read 07-the-field.md,
08-session-state.md, 09-recovery-curves.md,
10-component-response.md, 11-cross-exercise-transfer.md.

**Understanding the coach.** Read 12-the-coachs-read-loop.md,
13-goals-as-modulator.md, 14-the-highway-and-the-long-tail.md.

**Meta layers.** Read 15-the-chronicle.md,
16-the-meta-coach.md.

**Shipping.** Read 17-batch-pipeline.md.

**Terms.** Read 18-glossary.md.

## The one-sentence summary

Hardcoded, cited, per-exercise knowledge flows into a
shared field that learns each body, and the coaches
read that field to prescribe the next session.

## Relationship to other docs

- source/docs/architecture/ — the vision. What the
  system should be.
- source/docs/meta/ — the process. How batches ship.
- source/docs/system/ — this folder. How it works.
- reference/ — the HTML prototype. The visual target.

## The four layers

    5. Coaches        will
    4. Read interface pure functions
    3. Field          memory
    2. Sessions       ground truth
    1. Dossiers       authored knowledge

Each layer is useful alone. Each depends on the one
below. The field is the load-bearing wall.
