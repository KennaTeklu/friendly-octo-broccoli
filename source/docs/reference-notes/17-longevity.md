# 17 — Longevity Score Components, Aging Risks

> Reference module: `reference/scripts/app/17-longevity.js` (1 637 lines).
> Native counterparts: `engine/Stats.kt`, `core/ProgressSummary.kt`, `ui/screens/Dashboard.kt`, `ui/Dialogs.kt`.

This module computes the **longevity score** (0-100) from 8 sub-scores with age-dependent weighting, the trend score (3-month 1RM improvement), the variety score (longevity muscles trained in 30 days), and the aging-risk assessment.

---

## E2. Deep patterns

### 2.1 Age calculation
- **Inputs**: `workoutData.user.birthDate`.
- **Computation**: `age = today.year - birth.year`; if `month < 0 || (month === 0 && day < 0)` → `age--` (birthday not yet reached this year).
- **Output**: integer age or null.
- **Reasoning**: standard age calculation with birthday adjustment.

### 2.2 Base scores (6 sub-scores)
- **Inputs**: `calculateGripStrengthScore()`, `calculateBalanceScore()`, `calculateJointMobilityScore()`, `calculatePostureScore()`, `calculateMuscleBalanceScore()`, `calculateConsistencyScore()`.
- **Computation**: each returns 0-100 based on training history, exercise variety, and consistency metrics.
- **Output**: 6 base scores.
- **Reasoning**: 6 dimensions cover the longevity-relevant fitness components.

### 2.3 `calculateTrendScore()` — 3-month strength improvement
- **Inputs**: `workoutData.exercises` for 4 key lifts (squat, deadlift, bench_press, overhead_press).
- **Computation** (7-stage):
  1. `threeMonthsAgo = today - 3 months`.
  2. For each key lift: get history; if `<2 entries` → skip.
  3. Filter to entries ≥3 months ago.
  4. If `<2 recent entries` → skip.
  5. `oldest = recent[0]`; `newest = recent[last]`.
  6. `oldest1RM = estimate1RM(oldest.weight, max(oldest.reps || [1]))`.
  7. `newest1RM = estimate1RM(newest.weight, max(newest.reps || [1]))`.
  8. If `oldest1RM > 0` → `improvement = (newest1RM - oldest1RM) / oldest1RM` (fractional gain).
  9. Accumulate `totalImprovement += improvement`; `count++`.
  10. If `count === 0` → return 50 (neutral).
  11. `avgImprovement = totalImprovement / count`.
  12. `return min(100, max(0, 50 + avgImprovement * 250))`.
- **Output**: 0-100 trend score.
- **Reasoning**: 0% improvement → 50 (neutral); +10% → 75; -10% → 25; `* 250` makes the scale responsive.

### 2.4 `calculateVarietyScore()` — longevity muscle coverage
- **Inputs**: `workoutData.workouts` (last 30 days), `muscleDatabase.longevity` (23 muscles).
- **Computation** (5-stage):
  1. `thirtyDaysAgo = today - 30 days`.
  2. `longevityMuscles = muscleDatabase.longevity.map(m => m.name)`.
  3. Iterate workouts; for each exercise with `actual && !skipped && muscleGroup`:
     - For each muscle in `muscleGroup` → if in `longevityMuscles` → add to `trainedLongevity` Set.
  4. `percentage = (trainedLongevity.size / longevityMuscles.length) * 100`.
  5. `return min(100, round(percentage * 1.5))`.
- **Output**: 0-100 variety score.
- **Reasoning**: training 67% of longevity muscles → 100 (scaled by 1.5×); rewards broad coverage over depth.

### 2.5 Age-dependent weights (3 tiers)
- **Inputs**: `age`.
- **Computation**:
  - **< 30** (youth — strength focus): grip 0.20, balance 0.10, jointMobility 0.15, posture 0.20, muscleBalance 0.25, consistency 0.10, trend 0.10, variety 0.10.
  - **< 50** (middle — joint health): grip 0.15, balance 0.15, jointMobility 0.25, posture 0.15, muscleBalance 0.10, consistency 0.10, trend 0.05, variety 0.05.
  - **≥ 50** (50+ — balance + mobility critical): grip 0.10, balance 0.25, jointMobility 0.30, posture 0.15, muscleBalance 0.05, consistency 0.05, trend 0.05, variety 0.05.
- **Output**: weight object.
- **Reasoning**: youth optimizes for strength/muscle balance; middle age shifts to joint health; 50+ prioritizes balance and mobility (fall prevention).

### 2.6 Weight normalization
- **Inputs**: `weightBase` (raw weights).
- **Computation**: `totalWeight = sum(values)`; `weights[key] = weightBase[key] / totalWeight` for each key.
- **Output**: normalized weights summing to 1.0.
- **Reasoning**: handles floating-point drift; ensures weighted average is mathematically correct.

### 2.7 Combined longevity score
- **Inputs**: 8 sub-scores (6 base + trend + variety), normalized weights.
- **Computation**: `total = sum(scores[key] * weights[key])` for each key.
- **Output**: 0-100 longevity score.
- **Reasoning**: weighted average of 8 dimensions, age-tuned.

### 2.8 Aging risk assessment
- **Inputs**: sub-scores, age, training history.
- **Computation**: for each aging risk (e.g. "Poor grip strength", "Balance deficit"):
  - If relevant sub-score < threshold → mark as risk with severity (High/Medium/Low).
  - Severity scales with how far below threshold.
- **Output**: list of risks with `{area, severity, recommendation, exercises[]}`.
- **Reasoning**: actionable risks (not just scores) drive the recommendations module.

### 2.9 Score → status mapping
- **Inputs**: total score.
- **Computation**:
  - ≥80 → "Excellent Longevity", green.
  - ≥60 → "Good Longevity", yellow.
  - ≥40 → "Needs Improvement", yellow.
  - <40 → "High Risk", red.
- **Output**: status text + color.
- **Reasoning**: 4-tier status with traffic-light colors.

### 2.10 `estimate1RM(weight, reps)` helper
- **Inputs**: weight, reps.
- **Computation**: Epley `weight * (1 + reps/30)`.
- **Output**: estimated 1RM.
- **Reasoning**: same formula as module 04 — consistency.

### 2.11 Longevity breakdown panel
- **Inputs**: sub-scores.
- **Computation**: render chips per sub-score with color (≥70 green, ≥50 yellow, <50 red); show 6 key scores (grip, balance, jointMobility, posture, muscleBalance, consistency).
- **Output**: visual breakdown.
- **Reasoning**: users want to see *which* dimension is dragging the score down.

---

## E3. Edge cases handled

- **`birthDate` missing**: `age = null`; weight tier defaults to middle (the default `weightBase`).
- **Age exactly 30 or 50**: uses the lower tier (< 30 → youth; < 50 → middle).
- **Key lift has `< 2 history entries`**: skipped in trend calc.
- **Key lift has `< 2 entries in last 3 months`**: skipped.
- **`oldest.reps` empty/missing**: defaults to `[1]`; `max([1]) = 1`.
- **`oldest1RM = 0`**: skipped (division by zero protection).
- **All key lifts skipped**: `count = 0` → trend = 50 (neutral).
- **No workouts in last 30 days**: `trainedLongevity` empty → variety = 0.
- **All longevity muscles trained**: `percentage = 100`; `* 1.5 = 150`; clamped to 100.
- **Weight sum = 0** (impossible but defensive): would divide by zero; in practice never happens.
- **Sub-score missing from `breakdown`**: `if (breakdown[key] !== undefined)` guard.
- **`muscleDatabase.longevity` empty**: `longevityMuscles = []`; `percentage = NaN`; would return NaN — but `muscleDatabase` is hard-coded so this never happens.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Age tier 1 | < 30 (youth) | Peak strength years |
| Age tier 2 | 30-49 (middle) | Joint health becomes relevant |
| Age tier 3 | ≥ 50 (50+) | Fall prevention is critical |
| Trend window | 3 months | Meaningful strength adaptation timeframe |
| Trend neutral score | 50 | No data ≠ bad |
| Trend scaling | `* 250` | 10% improvement → 25-point gain |
| Variety window | 30 days | Recent coverage matters |
| Variety scale | `* 1.5` | 67% coverage → 100 (achievable) |
| Longevity muscles count | 23 | Comprehensive but not exhaustive |
| Status tiers | 4 (80/60/40) | Traffic light + "high risk" |
| Breakdown chips | 6 key scores | Scannable without overwhelming |
| Chip color thresholds | 70/50 | Green/yellow/red |
| Key lifts for trend | squat, deadlift, bench, OHP | The 4 powerlifting compounds |
| `estimate1RM` formula | Epley | Consistency with module 04 |
| Age-dependent weight shift | jointMobility 0.15 → 0.30 with age | Reflects sarcopenia/fall risk |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| Age calculation with birthday adjustment | FULL (`Stats.kt`) | — |
| 6 base sub-scores | FULL | — |
| `calculateTrendScore` 3-month improvement | FULL | — |
| `estimate1RM` (Epley) in trend | FULL | — |
| `calculateVarietyScore` 30-day coverage | FULL | — |
| Variety scale `* 1.5` | FULL | — |
| Age-dependent weights (3 tiers) | FULL | — |
| Weight normalization | FULL | — |
| Combined longevity score | FULL | — |
| Aging risk assessment | FULL (`CoachingEngine.kt`) | — |
| Score → status mapping (4-tier) | FULL | — |
| Longevity breakdown panel (6 chips) | FULL (`Dashboard.kt`) | — |
| Key lifts (squat/deadlift/bench/OHP) | FULL | — |
| Trend neutral score (50) | FULL | — |

**Coverage: 100%** (14 FULL / 0 PARTIAL / 0 MISSING of 14 behaviours)

---

## H. Top 3 gaps to close

1. **Trend score doesn't surface *which* lift is improving/declining** — the aggregate is shown but not the per-lift breakdown. Add a per-lift trend chip (squat ↑ 8%, bench ↓ 2%) so users know where to focus.
2. **Variety score doesn't show which longevity muscles are missing** — the percentage is shown but not the list of untrained muscles. Add an expandable "Missing muscles: deep_neck, popliteus, …" panel.
3. **Age-tier thresholds (30/50) are hard-coded** — should be tunable by user (some 50-year-olds are athletic; some 30-year-olds have joint issues). Expose as advanced settings or compute from self-assessment.
