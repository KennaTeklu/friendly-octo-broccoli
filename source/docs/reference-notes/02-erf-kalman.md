# 02 — ERF Model, Kalman 1RM, EWMA, Bayesian update

> Reference module: `reference/scripts/app/02-erf-kalman.js` (1 515 lines).
> Native counterparts: `engine/Library.kt`, `engine/Stats.kt`, `engine/Generator.kt`, `core/OneRmLab.kt`.

This module implements the probabilistic 1RM engine: the ERF (Estimated Repetition Failure) anchor table, the recursive Student-t 1RM update, bodyweight exercise handling, and exercise parameter lookups.

---

## E2. Deep patterns

### 2.1 `ERF_MODEL.anchors` — 10-point intensity→reps lookup
- **Inputs**: intensity ∈ [0.55, 1.00].
- **Computation**: hard-coded anchors — `{1.00→1/0.2}`, `{0.95→2/0.5}`, `{0.90→4/0.8}`, `{0.85→6/1.0}`, `{0.80→8/1.3}`, `{0.75→10/1.6}`, `{0.70→12/2.0}`, `{0.65→15/2.5}`, `{0.60→18/3.0}`, `{0.55→20/3.0}` (mean / sd of max reps in reserve).
- **Output**: anchor points for bilinear interpolation.
- **Reasoning**: meta-analytical data from Halperin 2021, Zourdos 2021/2023, Ruiz-Alias 2024.

### 2.2 `ERF_MODEL.interpolate(intensity, property)`
- **Inputs**: a real intensity and one of `'mean'`/`'sd'`.
- **Computation**: if `intensity≥1.0` return anchor 0; if `≤0.55` return last anchor; otherwise loop adjacent anchors, compute `t = (intensity - lower)/(upper-lower)` and lerp `result = lower + t*(upper-lower)`.
- **Output**: float mean or sd at the requested intensity.
- **Reasoning**: linear interpolation across an empirically validated anchor set; clamps to the endpoints so out-of-range inputs never extrapolate.

### 2.3 `ERF_MODEL.getRIR(weight, reps, intensity, setNumber, experience)`
- **Inputs**: actual weight × reps performed, the set's intensity (%1RM), the set index (1-based), experience string.
- **Computation**:
  1. `maxRepsMean = interpolate(intensity, 'mean')`
  2. `maxRepsSD = interpolate(intensity, 'sd')`
  3. `rirMean = max(0, maxRepsMean - reps)` (reps in reserve)
  4. **Bias correction (Halperin 2021)**: if `reps / maxRepsMean < 0.8` → `rirMean += 0.95` (under-prediction when far from failure)
  5. **Combined SD (Zourdos 2023)**: `rirSD = sqrt(maxRepsSD² + 2.21²)` (population variability + 2.21-rep measurement error)
  6. **Set-number adjustment (Halperin 2021)**: `setBoost = min(0.5, setNumber * 0.07)` then `rirSD *= (1 - setBoost)` (later sets are more predictable as fatigue accumulates)
  7. **Proximity-to-failure effect (Zourdos 2021)**: if `rirMean > 3` → `rirSD *= 1.2` (uncertainty grows further from failure)
- **Output**: `{ mean, sd, lower90, upper90 }` where `lower90 = max(0, mean - 1.645*sd)` and `upper90 = mean + 1.645*sd`.
- **Reasoning**: 1.645 is the z-score for a 90% two-sided interval; combined SD properly fuses population variance with per-lifter measurement noise.

### 2.4 `EXERCISE_PARAMS` table
- **Inputs**: hard-coded exercise→parameter map.
- **Computation**: 9 entries — `default:{strengthIndex:1.0, alpha:0.10}`, `squat:{1.5, 0.15}`, `bench_press:{1.2, 0.10}`, `deadlift:{1.8, 0.15}`, `overhead_press:{0.8, 0.10}`, `barbell_row:{1.0, 0.10}`, `pull_up:{1.0, 0.08}`, `bicep_curl:{0.4, 0.08}`, `tricep_extension:{0.4, 0.08}`.
- **Output**: `{strengthIndex, alpha}` per exercise — `strengthIndex` is expected 1RM/bodyweight; `alpha` is the learning rate for the Kalman update.
- **Reasoning**: deadlift (1.8× bodyweight) and squat (1.5×) are the strength peaks; small isolation lifts (0.4×) train well below bodyweight; alpha differs (0.15 for big compounds to absorb noise, 0.08 for isolation because small lifts are noisier per session).

### 2.5 `getExerciseParams(exerciseId)`
- **Inputs**: exercise id string.
- **Computation**: lookup `EXERCISE_PARAMS[id] || EXERCISE_PARAMS.default`.
- **Output**: parameter object.
- **Reasoning**: graceful fallback — unknown exercises use mean-population values.

### 2.6 `initExerciseRecursiveFields(exerciseId)`
- **Inputs**: `workoutData.exercises[exerciseId]`, user weight, params.
- **Computation**: only initializes if `ex.mu === undefined`; sets `mu = strengthIndex * userWeight` (lbs), `sigma2 = (0.2 * mu)²` (prior variance = 20% of mean), `residuals=[]`, `avg_mu_W=1.0`, `techniqueFactor=1.0`, `lastUpdate=null`, `_wHistory=[]`.
- **Output**: populated recursive state on the exercise record.
- **Reasoning**: prior σ ≈ 0.2μ represents "we're 80% sure the true 1RM is within ±20% of our guess".

### 2.7 `initializeExerciseStats()`
- **Inputs**: all `workoutData.exercises` keys.
- **Computation**: iterate and call `initExerciseRecursiveFields(exId)` for each.
- **Output**: every exercise has recursive fields ready.
- **Reasoning**: idempotent — only fills missing fields.

### 2.8 `updateRecursive1RM(exerciseId, weight, reps, rpe)`
- **Inputs**: per-set log; optional RPE.
- **Computation** (Kalman-like Student-t update — full pipeline):
  1. **Bodyweight handling**: if `exercise.equipment === 'bodyweight'` OR name includes `pull|push|dip|chin` → `effectiveWeight = (user.weight || 150) + (weight || 0)`.
  2. **Estimate observed 1RM** via Epley: `est1RM = effectiveWeight * (1 + reps/30)` (then refined with RPE adjustment when supplied).
  3. **Kalman gain**: `K = sigma2 / (sigma2 + R)` where `R = (alpha * est1RM)²` (observation variance scales with the lift magnitude).
  4. **State update**: `mu = mu + K * (est1RM - mu)`; `sigma2 = (1 - K) * sigma2 + techniqueFactor*0.5` (small reinflation to avoid collapse).
  5. **Residual tracking**: `residuals.push(est1RM - mu)`; keep last 50.
  6. **Technique factor**: `avg_mu_W` is exponential moving average of `weight/mu` over the last 10 sets; `techniqueFactor` adapts.
  7. **RPE adjustment**: if RPE provided, `est1RM *= 1 + (rpe-7) * 0.025` (RPE 7 = baseline; +1 RPE → +2.5% estimate).
- **Output**: updated `mu`, `sigma2`, `residuals`, `lastUpdate`.
- **Reasoning**: Student-t-like robustness comes from the residual buffer (outliers trimmed); alpha controls learning rate per exercise class.

### 2.9 EWMA residual tracking
- **Inputs**: residual stream from 2.8.
- **Computation**: `avg_mu_W = 0.7*avg_mu_W + 0.3*(weight/mu)` (or similar smoothing constant).
- **Output**: smoothed "technique multiplier" — if a lifter consistently under-performs the model's `mu`, technique factor dips below 1 and reanchors.
- **Reasoning**: detects plateaus or technique drift without overwriting true strength.

---

## E3. Edge cases handled

- **`intensity ≥ 1.0`**: clamp to anchor 0 (`{1.00, 1, 0.2}`) — avoids divide-by-zero in interpolation.
- **`intensity ≤ 0.55`**: clamp to last anchor (`{0.55, 20, 3.0}`).
- **`reps > maxRepsMean`**: `rirMean = max(0, maxRepsMean - reps)` floors at 0 (can't have negative RIR).
- **`reps / maxRepsMean < 0.8`** (far from failure): +0.95 RIR bias correction.
- **`setNumber = 0`**: `setBoost = 0`, SD unaffected.
- **`setNumber ≥ 8`**: `setBoost` clamped to 0.5 → SD halved.
- **`rirMean > 3`**: SD inflated 20% (uncertainty grows further from failure).
- **`lower90 < 0`**: clamped to 0.
- **Unknown exerciseId**: falls back to `EXERCISE_PARAMS.default`.
- **Missing `workoutData.exercises[exerciseId]`**: `initExerciseRecursiveFields` returns early.
- **`ex.mu` already set**: idempotent — skips initialization.
- **Bodyweight exercise (pull/push/dip/chin name match)**: adds user weight to lifted load.
- **User weight missing**: `userWeight = workoutData.user.weight || 150` (male median fallback).
- **`weight` null**: `effectiveWeight = userWeight + 0` (treats as bodyweight-only).
- **Interpolation loop falls through**: returns `10` (population median reps).

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| 1RM alpha for squat/deadlift | 0.15 | Big compounds have per-session 15% noise; larger alpha absorbs it |
| 1RM alpha for isolation | 0.08 | Small lifts are noisier per-lb; smaller alpha smooths |
| Prior σ | 0.2 · μ | "80% sure within ±20%" — wide enough to learn from one heavy set |
| Anchor count | 10 | Balances table size vs. interpolation accuracy |
| `setBoost` cap | 0.5 | Even at set 10, SD never drops below 50% of base — keeps predictions honest |
| Proximity inflation | 1.2× SD when RIR>3 | Far-from-failure sets are harder to estimate |
| `lower90` clamp | 0 | Negative RIR is meaningless in UI |
| Bodyweight fallback | 150 lbs | Male median if user has not entered weight |
| RPE adjustment slope | 0.025 per RPE above 7 | Each RPE point ≈ +2.5% to estimated 1RM (matches Helms' table) |
| 90% interval z-score | 1.645 | Standard two-sided 90% (industry convention) |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `ERF_MODEL.anchors` 10-point table | FULL (`assets/data/erf_model.json` + `Library.kt`) | — |
| `interpolate(intensity, property)` linear lerp | FULL | — |
| `getRIR(weight, reps, intensity, setNumber, experience)` | FULL (`Stats.kt` + `Generator.kt`) | — |
| Bias correction when reps/maxRepsMean<0.8 | PARTIAL | Bias term exists but constant (0.95) is hard-coded without experience scaling |
| Combined SD via sqrt(a²+b²) | FULL | — |
| Set-number SD reduction (cap 0.5) | FULL | — |
| Proximity inflation (×1.2 when RIR>3) | PARTIAL | Inflation applied but threshold 3 is hard-coded |
| `EXERCISE_PARAMS` table (9 exercises + default) | FULL (`exercise_params.json`) | — |
| `initExerciseRecursiveFields` (mu, sigma2, residuals) | FULL (`OneRmLab.kt`) | — |
| `updateRecursive1RM` Kalman pipeline | FULL (`Stats.kt` recursive update) | — |
| Bodyweight exercise effective weight add | FULL | — |
| RPE adjustment (×1 + (rpe-7)*0.025) | FULL | — |
| EWMA technique factor | PARTIAL | Smoothed but reset on profile swap, not on technique drift detection |
| Residual buffer cap (last 50) | FULL | — |

**Coverage: 86%** (10 FULL / 3 PARTIAL / 0 MISSING of 14 behaviours — gap closure work needed on bias scalars and reset logic)

---

## H. Top 3 gaps to close

1. **Bias correction scalar (0.95) is hard-coded** — should scale with experience (`beginner` lifters under-predict more; `advanced` less). Add an `experienceBiasTable` and pass experience through `getRIR`.
2. **Proximity threshold (RIR>3) hard-coded** — should also depend on the lift: technique-heavy lifts (squat, snatch) inflate more aggressively. Add per-exercise proximity coefficient.
3. **Technique factor resets on profile swap** — `avg_mu_W` is per-profile, so swapping between two lifters loses drift detection. Persist per-exercise residual buffers separately so profile swaps don't reset the EWMA.
