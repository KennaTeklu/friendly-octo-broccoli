# 16 - The meta-coach

## What it is

The coach that watches the coaches. Detects drift.
Detects contradiction. Detects envelope violations.
Reports to the human.

Deferred from v34, still planned.

## What it watches

Every coach, every session, every read:

1. **Drift** — a coach's output moves away from its
   cited source over time.
2. **Contradiction** — two coaches give conflicting
   prescriptions for overlapping muscles on the same
   day.
3. **Envelope violations** — a coach's output exceeds
   its declared envelope.
4. **Coverage gaps** — an exercise resolves to no
   coach, or a coach resolves to no row.
5. **Silent failure** — a card renders but nothing
   happens when the user interacts.

## What it does

For each detection:

- Logs to `build/meta-coach-report.txt`
- Categorizes: drift, contradiction, envelope,
  coverage, failure
- Suggests a fix but does not apply it
- Escalates to the human reviewer

## What it does NOT do

- Modify a coach. Only the human modifies coaches.
- Modify a dossier. Same.
- Override a prescription. The coach's output stands.
- Auto-correct drift. Report only.

## Why it exists

Over months, dozens of batches, hundreds of authored
rows, drift is inevitable. A source gets updated. A
coach is edited. A dossier is revised. The system
needs a watchdog that says: "these numbers no longer
match their sources."

The meta-coach is that watchdog. It is not a
decision-maker. It is an auditor.

## The rule

The meta-coach never prescribes. Never writes to the
field. Never edits a dossier. It reads and reports.
The human decides what to do with the report.

## What "healing" means here

Not automatic repair. The chronicle heals state.
The meta-coach heals authored content. Two different
layers.

Chronicle: session data is inconsistent -> rebuild
from sessions.

Meta-coach: authored data no longer matches its source
-> flag for human review.

Different mechanisms, different scopes.
