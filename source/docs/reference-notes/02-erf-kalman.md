# 02-erf-kalman — ERF model, Kalman 1RM, EWMA, Bayesian estimation

Source: `reference/scripts/app/02-erf-kalman.js` (L486–1993 of original `app.js`)

## A. File purpose
This module is the statistical core of strength estimation. It implements (a) an Enhanced Repetition-in-Reserve (ERF) model anchored at 10 intensity points with bilinear interpolation and confidence intervals derived from Halperin 2021, Zourdos 2021/2023, Ruiz-Alias 2024; (b) a recursive Student-t Kalman filter for 1RM tracking with adaptive noise; (c) exercise-parameter table (`strengthIndex`, `alpha`); (d) recursive field initialization for each exercise record.

## B. Public surface
- `ERF_MODEL` — object with `anchors[]` (intensity 1.00→0.55, mean + sd max reps), `interpolate(intensity, property)`, `getRIR(weight, reps, intensity, setNumber, experience)` → `{mean, sd, lower90, upper90}`. Applies bias correction (`+0.95` if `reps/maxReps < 0.8`), combines population variability with measurement error (`sqrt(sd² + 2.21²)`), set-number boost, proximity-to-failure widening.
- `EXERCISE_PARAMS` — `{default, squat, bench_press, deadlift, overhead_press, barbell_row, pull_up, bicep_curl, tricep_extension}` → `{strengthIndex, alpha}`.
- `getExerciseParams(exerciseId)` — fallback to `default`.
- `initExerciseRecursiveFields(exerciseId)` — seeds `mu = strengthIndex × userWeight`, `sigma2 = (0.2·mu)²`, `residuals[]`, `avg_mu_W = 1.0`, `techniqueFactor = 1.0`, `lastUpdate = null`, `_wHistory[]`.
- `initializeExerciseStats()` — walks all `workoutData.exercises` keys.
- `updateRecursive1RM(exerciseId, weight, reps, rpe)` — Student-t recursive update (commented Eq1).

## C. Dependencies
- Globals: `workoutData.user.weight`, `Math`.
- Downstream: called from `04-strength-tests.js::processTestResult`, `12-logging-inputs.js` after each set.

## D. Native-equivalent mapping
- `core/OneRmLab.kt::OneRmLab` (L17) — `epley` (L51), `brzycki` (L57), `lombardi` (L66), `rpeBased` (L75), `estimate` (L87) with `FormulaProbabilities{epley, brzycki, lombardi}` (Model.kt L185). The native side uses a 3-formula weighted blend rather than ERF anchors.
- Kalman recursion → `engine/Library.kt::OneRm.updateRecursive` (L628) — present, runs per-set Bayesian update with `effectiveWeightFor` (L621).
- **GAP**: ERF anchor table, bilinear interpolation, `getRIR` confidence intervals (lower90/upper90), set-number boost, proximity-to-failure widening, Halperin bias correction. Native has no RIR-distribution model — only point estimates.

## E. Patterns worth porting
- **RIR as a distribution, not a point.** Returning `{mean, sd, lower90, upper90}` lets the UI show "RIR 2 (90% CI 0–4)" instead of "RIR 2". Native `OneRmLab.estimate` returns a single double — porting the SD + CI fields would meaningfully improve the OneRmLabScreen.
- **Bias correction for sub-maximal reps** (Halperin 2021) — `+0.95 RIR` when `reps/maxReps < 0.8`. Cheap to port; current native estimate is unbiased.
- **Set-number boost** on SD (`min(0.5, setNumber·0.07)`) — uncertainty shrinks across sets as you reveal more reps; clever, missing in native.
- **Anchored interpolation** vs formula blending — anchors are easier to A/B test than formula weights.

## F. Feature IDs
`P02-ERF-MODEL`, `P02-KALMAN-1RM`, `P02-RIR-CONFIDENCE-INTERVAL`, `P02-EXERCISE-PARAMS`, `P02-BIAS-CORRECTION`.
