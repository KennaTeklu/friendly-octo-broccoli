# 09 - Recovery curves

## What it is

The time it takes one muscle to recover from one
session, for one user. Learned, not assumed.

## The default

Before any history, each muscle has an authored default
recovery time:

    compound       48h   (squat, deadlift, bench, OHP)
    accessory      24h   (curls, calf raises, etc.)
    isometric      24h   (planks, holds)
    cardio         12h   (running, cycling)
    mobility        6h   (stretches)

These defaults are authored in the dossier family.
Not computed.

## The learned curve

After 3+ sessions on the same muscle, the system can
measure the user's actual recovery.

Method:

1. Look at sessions where the user trained muscle m.
2. Find the next session where they trained m again.
3. Note the time gap.
4. Note the performance at the second session
   (reps x load, compared to the first).
5. If performance matched or exceeded, the gap is
   within their recovery. If it dropped, the gap was
   too short.
6. Average across sessions.

Over time, the curve narrows:

    recoveryTime(user, chest) = 42h   (this user)
    default                   = 48h   (authored)

## How the field uses it

Decay shape per muscle per user:

    stimulus(t) = peak x exp(-t / recoveryTime)
    readiness(t) = 1 - (stimulus(t) / peak)
                 = 1 - exp(-t / recoveryTime)

At t=0: readiness 0.
At t=recoveryTime: readiness ~0.63.
At t=2x recoveryTime: readiness ~0.86.
At t=3x recoveryTime: readiness ~0.95.

The coach reads readiness before prescribing. High
readiness -> full intensity. Low readiness -> reduced.

## What the curve does NOT assume

- It does not assume recovery is linear. It is
  exponential decay.
- It does not assume one recovery time per user. It is
  per muscle.
- It does not assume the default is right. It overrides
  the default once it has data.

## Confidence

Before 3 sessions: use the default, mark confidence 0.
After 3-20 sessions: blend default and learned,
    weighted by n / 20.
After 20+ sessions: use the learned curve.

Bayesian shrinkage. Same method as the response-rate
learning in 05-stimulus-field.md.

## Per user, per muscle, per component

Both muscles and components have curves.

A user with strong recovery from strength work but slow
from endurance work will show different curves for the
two. The field tracks both separately.

## What the coach sees

The coach never sees the curve. It sees readiness.

    readinessFor(user, chest) = 0.85

That number encodes the curve, the peak, and the time
since the last session. The coach uses the number. It
never reasons about the curve.

## The rule

Curves are learned. Defaults are authored. Confidence
grows with data. The user never sees either — only
readiness.
