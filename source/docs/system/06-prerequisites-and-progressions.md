# 06 - Prerequisites and progressions

## Two different things

**Prerequisites** — what must be true of the person
BEFORE they attempt this exercise. Authored per goal.
A sentence, not a number.

**Progressions** — the exercise that comes before, and
the exercise that comes after. A graph edge. Bidirectional.

## Prerequisites

    data class Prerequisite(
        val forGoalId: String,
        val exerciseId: String,
        val requirement: String,
        val source: String,
    )

Example:

    Prerequisite(
        forGoalId = "skill_planche",
        exerciseId = "pseudo_planche_push_up",
        requirement = "3x8 clean, hollow body, 2s pause bottom",
        source = "sommer-2006/...#tier-9",
    )

The requirement is a **sentence**, not "minSets 3, minReps 8."

Why: two people with the same rep count can have very
different form. A sentence lets the coach render "3x8
clean, hollow body, 2s pause bottom. You have done 2x6
with hips sagging." A number cannot say that.

## Why per goal

Different goals have different prerequisites. Skill path
needs clean reps and hollow body. Strength path might
need a stricter ROM standard or a heavier 1RM. The
prerequisites are attached to the goal, not the
exercise.

## Progressions

    progressionIn: List<String>   // exerciseIds that lead here
    progressionOut: List<String>  // exerciseIds this leads to

Author them so the chain walks in both directions.
A user on `tuck_planche_push_up` can see the whole
chain behind them and ahead.

## The rule

Both fields are authored. Neither is derived from
muscle overlap, category, or name.

If two exercises train the same muscles but have no
authored progression edge between them, they are not
in the same chain. That is correct. Groups like
"chest exercises" are search categories, not
progressions.

## The resolver

    DossierRegistry.progressFrom(exerciseId, goalId)
        returns the next exercise id on this goal's path

    DossierRegistry.prerequisitesFor(exerciseId, goalId)
        returns the list of Prerequisite for this path

    DossierRegistry.chainFor(exerciseId)
        returns the whole chain both directions

If a user has no declared goal, progressFrom returns
null. The card shows "Choose a goal."

## What the UI does

Locked card (prerequisites not met):

    Shows: the exercise name
    Shows: the prerequisite sentence
    Shows: the user's current state against it
    Shows: link to the prerequisite exercise
    Hides: prescription line, buttons

Unlocked card:

    Shows: normal card, plus goal chips at top if the
           exercise has multiple goals
    Shows: progress toward the goal's landmark
    Shows: next milestone sentence

## Why sentences, not numbers

A number gate says "you may pass." A sentence says
"here is where you are, here is what remains." Coaching
is the sentence.
