# 07 - The field

## What it is

The shared substrate. Where every session writes. Where
every coach reads.

    field[user][muscle]    -> stimulus units, decaying
    field[user][component] -> stimulus units, decaying

The field has no edges. No structure. Just values that
change with time.

## Why it exists

1,413 exercises cannot each know 1,412 others. That is
2 million pairs. Unbuildable, unmaintainable, and wrong
— most pairs have no real relationship.

Instead: each exercise writes to the shared field.
Each exercise reads from the shared field. The field
is the mediator. Cross-exercise transfer falls out of
shared muscle and component overlap, without any pair
of exercises knowing about each other.

## The state size

Per user, per day:

    ~6 muscles updated per session
    ~5 components updated per session
    ~10 numbers written

State is bounded by [user x 50 muscles x 11 components],
not by exercise pairs. Adding exercise #1,414 changes
nothing structurally.

## Decay

Stimulus decays over time according to the user's
recovery curve for that muscle.

    readiness(user, muscle) = 1 - (current / peak)

At t=0 after a session: readiness ~0.
At t=recovery: readiness ~1.

The recovery curve is per user, per muscle, learned
from history. Default is authored per exercise family.
See 09-recovery-curves.md.

## What the field stores

For each (user, muscle) and (user, component):

    current      units
    peak         units (the highest recent value)
    lastUpdate   timestamp
    curveId      which recovery curve is applied

That is the entire state. Four fields per slot.

## What the field does NOT store

- Prescriptions. Coaches produce those.
- Dossier data. Dossiers are static files.
- Goals. Stored on the user's profile per exercise.
- Exercise pair relationships. Those fall out of
  shared muscles.

## Read interface

    readinessFor(userId, muscleId) -> Double
    readinessFor(userId, componentId) -> Double
    stimulusFor(userId, muscleId) -> Stimulus
    recentSessions(userId, exerciseId, limit) -> List

Pure functions. Read-only. No side effects.

## Write interface

    logSession(userId, exerciseId, sets, reps, load,
               tempo, rpe, felt) -> Unit

Reads the dossier's stimulusProfile, multiplies by the
session volume, adds to the field. Updates peak.
Stamps lastUpdate.

## The rule

The field never decides. It stores numbers and decays
them. Coaches decide. The field is memory, not will.

## Why this is the whole design

Everything else is a layer over the field:

    dossiers    hold the priors that seed the field
    sessions    write into the field
    coaches     read from the field
    goals       modulate the read
    users       update the field every session

Remove any layer and the field still works. The field
is the load-bearing wall.
