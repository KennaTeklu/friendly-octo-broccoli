# 13 - Goals as modulator

## What a modulator is

Not a layer. A bias. Applied to a read, not a system
of its own.

The field does not change when the goal changes.
The dossier does not change. The session history does
not change. Only the read changes.

## Why a modulator, not a subsystem

If the goal were a subsystem, it would have its own
state, its own storage, its own UI. Every goal would
be a parallel stack.

Instead, the goal is a single string per (user,
exercise). Stored in the user's state. Read by the
coach when it composes today's prescription.

Two users with the same history, same dossier, same
field readiness, and different goals get different
prescriptions. Same everything, different output. The
goal is the only difference.

## What the goal changes

Three things. All bounded by the coach's envelope.

1. **Row selection.** The coach's authored rows are
   tagged with intent. Goal maps to intent. If the goal
   is skill, pick the skill-appropriate row. If
   strength, pick the strength row.

2. **Volume bias.** Skill goals bias toward more reps
   at sub-maximal load. Strength goals bias toward
   lower reps at higher load. Hypertrophy goals bias
   toward moderate reps and moderate load.

3. **Progress landmark.** The goal's landmark is the
   target the progress metric tracks against. Skill
   path and strength path have different landmarks. The
   user sees progress toward their own landmark, not
   some universal one.

## What the goal does NOT change

- The field. The field holds values. Goals do not
  affect decay or accumulation.
- The recovery curve. Recovery is physiological. Goals
  do not speed or slow it.
- The component response rate. Adaptation speed is
  physiological. Goals do not change it.
- The prerequisites. Prerequisites are authored per
  goal. Once the user picks a goal, its prerequisites
  apply. Different goals have different prerequisites,
  but the goal does not modify them.

## Switching goals mid-way

Allowed. The user can change the goal at any time.

What happens:

1. The old goal's progress is archived (visible in
   history, not active).
2. The new goal's prerequisites are re-checked against
   session history.
3. If the new goal's prerequisites are already met,
   the user continues from current session state.
4. If not, the user is gated at the new goal's
   prerequisites, with a link to the exercise they need.

The field does not reset. Stimulus accumulated under
the old goal still contributes. The body does not know
what goal was declared.

## Where the goal lives

Stored on the user's profile:

    user.goals[exerciseId] = "skill_planche"

One string per exercise. That is the entire state. If
the user never declares a goal, the field defaults to
the exercise's first authored goal.

## Why this is right

The user's job is choosing a destination. The system's
job is the map. The coach's job is the route.

Modulating the read is the cleanest way to keep those
three jobs separate. No subsystem creep. No parallel
state. One string per exercise, read by the coach,
producing a different path.

## The relationship to declare + tune

This is the v34d declaration pattern extended. The card
declares the goal. The user reads it. The user consents
or switches. The coach then prescribes.

The declaration says what the coach will do. The goal
says where it is taking the user. Two different
sentences on the card. Both authored, both cited.
