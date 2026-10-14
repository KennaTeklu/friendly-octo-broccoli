# 12 - The coach's read loop

## The loop

One per exercise. Runs every session.

    1. User opens exercise card
    2. Coach reads user's declared goal for this exercise
    3. Coach reads dossier for this exercise
    4. Coach reads field readiness for the muscles and
       components this exercise trains
    5. Coach reads recent session history for this
       exercise
    6. Coach computes today's prescription
    7. User logs the session (or skips)
    8. Session writes to the field
    9. Field decays over time
    10. Next session, the loop runs again

## What the coach decides

Given all the input, the coach outputs:

    Prescription {
        sets: Int,
        reps: Int or range,
        load: Double,               // 0 for bodyweight
        rest: Int,                  // seconds
        tempo: String?,             // optional
        duration: Double?,          // for holds
        note: String,               // the declaration sentence
        progress: Pair<Int, Int>,   // toward landmark
        next_milestone: String,     // short sentence
    }

Every field is authored. Nothing is computed from
formulas.

## What the coach does NOT decide

- The goal. The user does.
- The prerequisites. The dossier does.
- The progression chain. The dossier does.
- The recovery rate. The field does.
- The response rate. The field does.

The coach assembles. It does not invent.

## The read order

Always the same. Always in this order:

    1. requirements()    — what inputs are needed
    2. envelope          — bounds the coach may output
    3. provenance        — which source row this cites
    4. version           — which version of the coach
    5. reconcile()       — selects a row from the authored set
    6. explain()         — the declaration sentence

The card reads exactly these. Nothing else.

## Readiness gating

Before reconcile returns a row, the coach checks:

    for muscle in exercise.primaryMuscles:
        if readiness(user, muscle) < 0.4:
            return Prescription(note = "Recovering — $muscle not ready")
            with reduced volume or rest recommendation

This is the cross-exercise awareness showing up in the
loop.

## Response-rate adjustment

After selecting a row, the coach adjusts for the
user's component response:

    if responseRate(user, muscle, component) > 1.2:
        reduce volume slightly  (this body adapts fast)
    if responseRate(user, muscle, component) < 0.8:
        increase volume carefully  (this body needs more)

The adjustment is bounded by the coach's envelope.

## Goal-aware biasing

The row that reconcile returns is the base. The goal
adjusts:

    goal "skill_planche"     -> bias toward reps, tempo,
                                sub-maximal work
    goal "strength_weighted" -> bias toward weight,
                                lower reps, longer rest

The bias is a selection among the authored rows, not
a computation. If the goal says "skill", reconcile
picks the skill-appropriate row. If the goal says
"strength", it picks the strength-appropriate row.

## The output is a sentence

The card never shows the math. It shows:

    "Back squat today: 5x5 at 80%. Over the next
     6 weeks of Build, this makes your glutes and
     quads stronger. Your last 3 RPE were 8.5.
     We push today."

One sentence. Coach voice. Cites the source internally.
Adjusted for this body, this day, this goal. The
sentence is the loop's output.
