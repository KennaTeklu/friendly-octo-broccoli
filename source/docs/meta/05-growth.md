# How the design grows

## The spiral

The design is not finished and does not converge to a final state. It accumulates.

Every batch adds:
- Rows to the catalogs.
- Sources to the sources directory.
- Coaches to the registry.
- Coverage of body profile dimensions.
- Confidence in the field per user.

Old arcs keep their coach versions. New arcs run on new versions. The system is a stack of spirals, one per arc.

## Where new sources come from

Initial: hand-transcribed tables from published works.

Later, three channels:

1. Updates to sources already cited. A new edition of Rippetoe. A revised ACSM position stand. A reissued Daniels table.
2. New sources in domains already covered. Another band maker's tension chart. Another plank standard.
3. New domains. Sleep. Cycle. Nutrition. Recovery modalities. Each domain brings new sources and new row shapes.

## External feeds

Eventually, sources connect to external credible data:
- ACSM updates.
- PubMed for the studies behind claims.
- Published chart pages.
- Health Connect for consented user data (sleep, HRV, cycle, activity).

Each external feed produces graph nodes. It does not produce prescriptions.

## The rule for change

External sources never update the hardcoded layer automatically.

Path:
1. Source arrives.
2. Graph node created.
3. Walker compares to hardcoded rows.
4. Proposal issued.
5. Human reviews.
6. Hardcoded coach updated.
7. Version bumped.
8. Push.

A human is in the loop for every change to a prescription.

## The walker's job

The walker:
- Reads the graph.
- Finds rows that agree.
- Flags rows that disagree.
- Flags rows whose source has changed.
- Proposes alignment.

The walker does not:
- Prescribe.
- Blend.
- Update the hardcoded layer.
- Override a coach.

## Growth of the body profile

v34: training_age, sex, body_goal.
Later: cycle_phase, recovery_state, restriction, age_band.

Each dimension is a slice of the catalog. Each slice grows independently. A dimension only ships when sources cover it.

## Growth of the sources directory

Starts with six files for five exercises.
Grows to cover every exercise family.
Grows to cover every body profile dimension.
Grows to cover every domain the app touches.

The directory is not a dump. Every file is real, cited, and used by at least one row.

## What stays stable

- The forward pass. Linear. One direction. Auditable.
- The delivery contract. Six entries. Five commands.
- The push rule. Every batch.
- The human in the loop. Every truth-layer change.

Everything else grows.
