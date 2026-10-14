# 04 - Stimulus profiles

## What they are

Per exercise, the per-set contribution to each muscle
and each component. Two maps. Both authored.

    stimulusProfile:
      chest:            1.0
      triceps:          0.7
      anterior_deltoid: 0.5
      core:             0.3

    componentStimulus:
      Strength:     0.8
      Endurance:    0.2

The numbers mean: for one working set of this exercise,
deposit this many units of stimulus into that muscle or
component.

## Why they exist

Cross-exercise transfer. Without a stimulus profile,
the field cannot know that push-ups affect bench press.
With it, the field can compute the overlap directly.

Example: 3x10 push-ups.

    chest     += 3 x 10 x 1.0 = 30 units
    triceps   += 3 x 10 x 0.7 = 21 units
    ant_delt  += 3 x 10 x 0.5 = 15 units
    core      += 3 x 10 x 0.3 =  9 units
    Strength  += 3 x 10 x 0.8 = 24 units
    Endurance += 3 x 10 x 0.2 =  6 units

Three days later, Bench Press asks the field:
"how ready is chest?"
"how ready is triceps?"

The field answers. It knows because push-ups declared
these ratios when the session was logged.

## Where the numbers come from

Three tiers, in order of preference:

1. **Cited** — a real source gives per-muscle activation
   ratios for this exercise. Examples: EMG studies
   published in strength science texts. Cite them.

2. **Derived from a cited family** — the exercise's
   family has a cited profile, and this variant's
   profile is authored by comparison. Mark as
   "derived from <source>."

3. **Coach-authored, provisional** — no source exists.
   The developer authors a best guess. Marked
   explicitly so the field treats it as a soft prior
   that can be overwritten by learned data.

Never ship a profile without a provenance tier.

## How the field uses them

1. Read: dossier.stimulusProfile for the exercise
2. Multiply by the session: sets x reps x weight
3. Write: add the product to the user's field
4. Decay: as time passes, the field decrements

The field never re-reads the dossier for old sessions.
Once written, the field owns the number. It decays on
its own.

## Why this is the difference

A stat-based system would say "Push-ups and bench press
both train chest, so they transfer." It would guess
the ratio.

This system reads the authored ratio. 1.0 to chest,
0.7 to triceps. That ratio is the difference between
a guess and a coach.

## The overlap is the intelligence

    Push-Up      -> chest 1.0, triceps 0.7, ant_delt 0.5
    Bench Press  -> chest 1.0, triceps 0.8, ant_delt 0.6
    Overhead Press -> ant_delt 1.0, triceps 0.7, chest 0.3
    Dip          -> chest 0.9, triceps 0.9, ant_delt 0.4

Transfer from A to B = product of their profiles.

    Push-Up -> Bench:
        chest   1.0 x 1.0 = 1.00  HIGH
        triceps 0.7 x 0.8 = 0.56  MEDIUM
        ant_delt 0.5 x 0.6 = 0.30 LOW

    Bench -> Leg Press:
        chest vs quads = 0.0  NONE

The field does not "reason." It multiplies. The
dossiers hold the ratios. The multiplication is the
intelligence.
