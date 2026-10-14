# 10 - Component response

## What it is

How fast this user adapts to a given training
component. Per component, per muscle.

Different users adapt differently. Some build strength
fast. Some build endurance fast. Some respond to power
training. Some do not.

## The 11 components

Strength, Hypertrophy, Endurance, Power, Flexibility,
Skill, Balance, Agility, Speed, BodyComposition,
Longevity.

Each user has a response rate per component, per
muscle.

## How it is measured

1. Look at sessions where component C was trained for
   muscle m.
2. Look at the training stimulus applied
   (sets x reps x intensity x componentStimulus).
3. Look at the change in performance or measurement
   for that component/muscle.
4. Rate = delta_measurement / delta_stimulus.
5. Average across sessions.

Example:

    User A trains chest with Strength focus.
    Week 1: 3x5 at 100kg
    Week 4: 3x5 at 110kg
    Delta = 10kg over 3 weeks
    Strength response rate (A, chest) = high

    User B trains chest with Strength focus.
    Week 1: 3x5 at 100kg
    Week 4: 3x5 at 102kg
    Delta = 2kg over 3 weeks
    Strength response rate (B, chest) = low

## What the response rate does

The coach uses it to set today's prescription:

    High response rate:
        smaller stimulus produces the same adaptation
        coach can dial back volume
        reduced overtraining risk

    Low response rate:
        larger stimulus needed for the same adaptation
        coach ramps volume carefully

Over months, this individualizes the training
precisely. No formula. Just this body's actual rate.

## Where the initial prior comes from

Before any history, use the dossier's componentStimulus
and assume a neutral response rate (1.0). Mark
confidence 0.

After 3 sessions: blend toward measured rate.
After 20 sessions: use the measured rate.

## How the coach reads it

    responseRate(user, chest, Strength) = 1.35

That number says: this user gains strength in the
chest 35% faster than the prior assumed. The coach
biases the prescription toward less volume, more
intensity, longer rest.

## Components work independently

A user can be high-strength-response and
low-endurance-response. Chest can respond fast to
strength and slow to hypertrophy. Muscles and
components cross.

The field tracks both dimensions independently. The
coach reads both.

## Why this is important

Without component response, the coach treats every
user the same. With it, every prescription is
individualized to this body's actual adaptation rate.

Same exercise. Same goal. Same week. Different user.
Different numbers. Not because the app guessed.
Because the field measured.
