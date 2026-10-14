# 01 - The thesis

## What this app is

A coaching system. Not a fitness app.

The difference: a fitness app calculates. A coaching
system reads authored knowledge and cites it.

A fitness app says "for hypertrophy, do 3x8-12 at 70%
1RM." It computed that from a formula. Any user, any
exercise, same answer.

A coaching system says "Barbell Back Squat, intermediate,
Build week 3, goal STRONGER, do 5x5 at 80%. Source:
Prilepin 1974, 80-89% row. You are 2 sessions from the
next landmark." Every number is written down, cited,
and adjusted for this exact person on this exact day.

## The mandate

From source/docs/meta/02-truth-layer.md:

> The hardcoded exercise coaches are the truth. Every
> exercise has an authored coach with numbers from a
> real source. Those numbers do not compute. They are
> written down. The app reads them, cites them, and
> renders them. Nothing is invented.

## What follows from this

Three consequences that rule out most fitness-app design:

1. **No computed prescriptions.** If a number is not in
   an authored row, it does not exist. Empty row ->
   empty prescription + a note. Never a fallback number.

2. **Every threshold cites a source.** Prilepin's chart.
   Rippetoe's novice LP. Sommer's tier table. McGill's
   plank standards. Daniels' VDOT zones. Rogue's band
   chart. Or, when no source exists, "coach-authored,
   reviewed" — explicitly marked as such so it can be
   corrected.

3. **Per exercise, not per category.** A planche push-up
   and a wall push-up are not two entries in a "chest
   bodyweight" bucket. They are different exercises with
   different prerequisites, different landmarks,
   different instructions, and different places in a
   progression chain. Taxonomy can group them for
   search. It cannot decide what to prescribe.

## The long-term shape

The floor is 1,413 authored dossiers and 31 authored
coaches. The ceiling is a system where the field learns
each body's recovery rate, adaptation rate, and
response to each component, and every coach reads the
field to prescribe today.

The system is the field. The dossiers are the map. The
coaches are the will. The user is the goal.
