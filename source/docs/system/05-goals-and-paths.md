# 05 - Goals and paths

## The two roles

**User decides:** the goal.

"I want the planche push-up."
"I want a heavier handstand."
"I want to emphasize strength over skill."

**Coach decides:** the path, the metrics, the
variations, the threshold.

The user does not pick variations freely. The coach
offers the variations that serve the chosen goal.

## Why not automatic

The user's goal cannot be inferred. Two people at the
same exercise have different destinations:

- Person A wants the planche push-up. Their next step
  is the planche.
- Person B wants a heavier handstand press. Their next
  step is a weighted variant.

Same start. Different paths. Same body. Different
destinations. Only the user knows which.

## Where goals live

Per exercise, in the dossier:

    goals [
      {
        goalId: "skill_planche"
        target: "planche_push_up"
        label: "Skill path to planche"
        componentWeights: { Skill: 5, Strength: 3 }
        landmark: "3x8 clean pseudo planche push-ups, hollow body, 2s pause"
        source: "sommer-2006/...#tier-9"
      },
      {
        goalId: "strength_weighted"
        target: "weighted_pseudo_planche_push_up"
        label: "Strength path to heavier press"
        componentWeights: { Strength: 5, Hypertrophy: 2 }
        landmark: "3x5 at +10kg, full ROM"
        source: "coach-authored, reviewed"
      }
    ]

Each goal cites a source. If a source names only one
path, only one goal is authored. Do not invent a
second path from thin air.

## How the user picks

First time the user opens an exercise with multiple
goals, the card shows a goal selector. Two or three
chips with labels. User taps one.

That choice persists. It is stored as part of the
user's state for that exercise. Changing goals is
allowed — the coach re-prescribes.

## What the goal changes

The goal is a modulator, not a new system.

Same field. Same dossier. Same coach. The goal
re-tunes the read.

With goal "skill_planche":
    componentWeights dominant = Skill
    -> coach biases reps, tempo, sub-maximal work
    -> landmark tracked = the skill landmark
    -> progress metric = skill progress

With goal "strength_weighted":
    componentWeights dominant = Strength
    -> coach biases weight, lower reps, longer rest
    -> landmark tracked = the strength landmark
    -> progress metric = strength progress

## What the coach computes from it

For today's session, the coach reads:

1. Field readiness for the muscles this exercise trains
2. Dossier stimulus profile
3. Session history on this exact exercise
4. Goal componentWeights

The coach produces:

    Prescription {
      sets, reps, load, rest, tempo,
      note: "metric sentence",
      progress: (current, target) toward landmark,
      next_milestone: short sentence
    }

## What the goal is NOT

- Not a variation. The user does not choose tempo or
  load. The coach does.
- Not a difficulty. Difficulty is separate.
- Not a category. Category is taxonomy.
- Not permanent. The user can switch.

## The rule

The user declares. The coach prescribes. The field
learns. Never let the user pick numbers directly.
Never let the coach pick the goal.
