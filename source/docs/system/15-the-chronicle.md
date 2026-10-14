# 15 - The chronicle

## What it is

The narrator and healer. Watches the field. Tells the
story of this body over time. Detects drift. Corrects
without inventing.

Deferred from v34, still planned.

## The two jobs

**Narrator** — reads the field and the sessions. Produces
a running account of what is happening. "Chest strength
up 6% over 4 weeks. Endurance flat. Recovery slowed by
12% since the cut started."

**Healer** — reads the state tree. Detects inconsistent
or corrupted state. Repairs it from session history
without changing what happened.

## What the narrator reads

- Field values over time
- Session history per muscle, per component
- Response rates
- Recovery curves
- Goal changes and their effect

## What the narrator produces

- Weekly summary: what improved, what stalled
- Monthly summary: trajectory, plateau warnings
- Turning points: when a rate changed, and why
- Narrative continuity: "you started cutting in week 5"

Every claim cites the underlying data. No invented
insight. No motivational filler.

## What the healer does

State can drift when:

- A migration goes wrong
- A profile switch leaves stale goals
- A session writes but the field does not update
- A restoration from backup disagrees with field state

The healer:

1. Reads the whole state tree.
2. Compares session history to field state.
3. Recomputes the field from sessions if needed.
4. Never deletes sessions.
5. Never invents a session that did not happen.
6. Reports what it did.

## What the chronicle is NOT

- Not a chatbot. It does not converse.
- Not a motivator. It does not pep talk.
- Not a planner. It does not prescribe.
- Not a scorer. It does not grade.

It narrates and heals. That is all.

## Why it matters

Months of training produce noise. Sessions add up.
Recovery changes. Goals shift. Without a narrator,
the user sees a spreadsheet. With one, they see a
story — grounded in real numbers, cited, no
embellishment.

## The rule

The chronicle may only report what is in the state.
If a fact is not stored, it does not appear. No
inference. No projection. No sentiment.
