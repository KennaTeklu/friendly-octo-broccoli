# 11 - Cross-exercise transfer

## What it is

The implicit relationship between any two exercises,
based on shared muscles and components.

No pair of exercises knows about the other. The
transfer is computed from overlap. The field is the
mediator.

## The math

For exercises A and B, transfer of muscle m:

    transfer(A, B, m) = A.stimulusProfile[m]
                      x B.stimulusProfile[m]

The product of their profiles.

Example:

    Push-Up:       chest 1.0, triceps 0.7, ant_delt 0.5
    Bench Press:   chest 1.0, triceps 0.8, ant_delt 0.6

    Transfer (push-up -> bench):
        chest    1.0 x 1.0 = 1.00
        triceps  0.7 x 0.8 = 0.56
        ant_delt 0.5 x 0.6 = 0.30

    Total overlap = 1.86 (rough strength score)

This means: 100 units of stimulus deposited by push-ups
shows up as 100 chest, 56 triceps, 30 ant_delt units of
readiness relevant to bench press.

## What it produces

When Bench Press coach asks "how ready is chest?", it
reads the field. The field's chest value includes
everything deposited by push-ups, dips, OHP, previous
bench, and any other exercise that trained chest.

The bench coach sees a single number. Behind that
number, every chest-training exercise contributed.

## The three levels of transfer

1. **Within a progression chain** (push-up -> pseudo
   planche push-up). High transfer. The later exercise
   exists because of the earlier one.

2. **Across the same muscle group** (push-up -> bench).
   Medium transfer. Both train chest, but the loading
   and skill demands are different.

3. **Across muscle overlap** (bench -> OHP via ant_delt).
   Lower transfer. Shared muscles, different primes.

The field handles all three the same way. Only the
overlap weight differs.

## What the coach does with it

When the coach reads readiness, it implicitly reads
the whole history of that muscle. Then:

- If readiness is high from unrelated work, coach can
  prescribe full session.
- If readiness is low because an unrelated exercise
  just trained the same muscle, coach dials back.
- If readiness is high and the current exercise is
  higher in a chain, coach may advance the user faster.

The user never sees the math. The user sees: "chest is
recovered. You're ready to push."

## What this is NOT

- Not a lookup table of pairs. Pairs are implicit.
- Not a statistic. The weights are authored in dossiers.
- Not a suggestion system. It is measurement.

## The scale

1,413 exercises, each with an authored stimulus profile
over ~50 muscles. The field holds [user x 50 muscles].
Adding an exercise adds one row of profile data. It
does not create 1,412 new pairs. The pairs are the
implicit cross product, computed on the fly when
needed.

This is how 1,413 exercises "communicate" without any
of them knowing about the others.
