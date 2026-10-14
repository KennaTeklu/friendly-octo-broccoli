# 02 - The four layers

## The stack

  5. COACHES             31 today, ~60 target
        reads field, dossier, goal, history
        writes today's prescription
        │
  4. READ INTERFACE      readinessFor(exerciseId, userId)
        │
  3. THE FIELD           shared substrate of the body
        stores muscle + component stimulus per user
        decays with time
        │
  2. SESSION STATE       one record per logged session
        │
  1. DOSSIERS            1,413 authored, cited
        muscles, components, goals, prerequisites,
        progressions, stimulus profiles

## What each layer does

### Dossiers (authored, static)
Per-exercise knowledge. Never computed. Read-only.
Cite a source. Changed only by human review.

### Session state (user, append-only)
Every logged session writes a structured record.
One record per exercise per session. Contains sets,
reps, load, tempo, RPE, timestamp, felt.

### The field (user, decaying)
Accumulates stimulus per muscle and per component.
Decays over time based on the user's recovery curve.
Never contains a prescription. Contains only state.

### Read interface (pure functions)
Queries the field. Returns readiness values.
Does not write. Does not decide. Does not prescribe.

### Coaches (per-exercise, active)
Read field + dossier + goal + session history.
Write today's prescription. Cite sources.
Never invent.

## The four shapes

The layers are not all the same shape.

  Sessions:      linear   one at a time, in order
  Dossiers:      web      cross-referenced, a graph
  Field:         field    values across dimensions, no edges
  Coaches:       loop     read, prescribe, user logs, repeat

They compose because they share one memory: the field.

## Why the shapes matter

A linear system can only move forward. A web can only
be traversed. A field can only be queried. A loop can
only cycle.

Together: the linear timeline writes into the field,
the field is queried by the loops, the loops reference
the web, and the web tells each loop where the user
has been and where they can go.
