# 15 — Systemic Fatigue, Recovery Section, Training Readiness

> Reference module: `reference/scripts/app/15-fatigue-recovery.js` (2 469 lines).
> Native counterparts: `engine/Stats.kt`, `ui/screens/Recovery.kt`, `core/ProState.kt`.

This module implements the **systemic recovery factor** (0.5-1.0), the weighted muscle recovery aggregate, the readiness status thresholds, the muscle weights table, and the bi-exponential fatigue decay applied on each render.

---

## E2. Deep patterns

### 2.1 `calculateSystemicRecoveryFactor()` — systemic fatigue index
- **Inputs**: `workoutData.workouts` (last 7 days), `workoutData.user.weight`.
- **Computation** (8-stage):
  1. Filter workouts to last 7 days.
  2. If no recent workouts → return 1.0 (fully rested).
  3. Accumulate per-exercise:
     - Time-based: `totalVolume += durations.reduce(sum)`.
     - Reps-based: `totalVolume += weight * reps.reduce(sum)`.
  4. `totalSets += actual.sets`.
  5. RPE handling: if `actual.rpe` is array → sum all + count; if number → sum + count 1.
  6. `relativeVolume = totalVolume / userWeight`.
  7. `avgRPE = rpeCount ? rpeSum / rpeCount : 5` (default 5 if no RPE).
  8. **Fatigue index** (3 components):
     - `fatigueFromFreq = min(50, recentWorkouts.length * 10)` — cap 50.
     - `fatigueFromVolume = min(30, relativeVolume / 500)` — cap 30.
     - `fatigueFromRPE = min(20, (avgRPE - 5) * 4)` — cap 20.
  9. `totalFatigue = fatigueFromFreq + fatigueFromVolume + fatigueFromRPE` (max 100).
  10. Clamp `[0, 100]`.
  11. `factor = 1.0 - (totalFatigue / 200)` — 100 fatigue → 0.5; 0 fatigue → 1.0.
  12. Clamp `[0.5, 1.0]`.
  13. If `isNaN(factor)` → 0.8 (safe default).
- **Output**: recovery factor in [0.5, 1.0].
- **Reasoning**: 3-component fatigue model — frequency (max 50%), relative volume (max 30%), RPE intensity (max 20%); `/200` divisor gives 0.5 floor even at max fatigue.

### 2.2 `updateRecoverySection()` — full recovery render
- **Inputs**: `muscleLastTrained`, `muscleFatigue`, `calculateSystemicRecoveryFactor()`, `muscleWeights` table.
- **Computation** (8-stage):
  1. `applyFatigueDecay()`.
  2. `calculateMuscleLastTrained()`.
  3. `systemicFactor = calculateSystemicRecoveryFactor()`; `systemicPercent = round(factor * 100)`.
  4. **Weighted muscle recovery**: iterate all 50 muscles; for each:
     - If never trained → `weightedRecovery += weight * 100`; `totalWeight += weight`.
     - Else `recoveryPercent = min(100, (daysSince / restDays) * 100)`; `weightedRecovery += weight * recoveryPercent`; `totalWeight += weight`.
  5. `muscleAvg = totalWeight > 0 ? weightedRecovery / totalWeight : 100`.
  6. **Combine**: `overallRecovery = round(0.7 * muscleAvg + 0.3 * systemicPercent)`.
  7. Store `window.lastOverallRecovery`.
  8. Determine readiness status (5 tiers).
- **Output**: recovery percentage + status.
- **Reasoning**: 70/30 weighting — muscle recovery dominates (it's local), systemic adds global context.

### 2.3 `muscleWeights` table — 50 muscles
- **Inputs**: hard-coded weight per muscle.
- **Computation**: large muscles weighted 1.5-2.0 (quads 2.0, hamstrings 1.8, glutes 1.8, back 1.8, chest 1.5, lats 1.5); medium 0.6-1.2 (shoulders 1.2, core 1.0, calves 0.6); small 0.1-0.5 (forearms 0.5, neck 0.4, deep_neck 0.3, hand_lumbricals 0.1).
- **Output**: weight map for weighted average.
- **Reasoning**: large muscles dominate systemic fatigue; small muscles (lumbricals) barely move the needle even when fatigued.

### 2.4 Recovery percentage per muscle
- **Inputs**: `daysSince`, `muscle.restDays`.
- **Computation**: `recoveryPercent = min(100, (daysSince / restDays) * 100)`.
- **Output**: 0-100.
- **Reasoning**: at restDays elapsed → 100% recovered; capped at 100 (can't be >100% recovered).

### 2.5 Readiness status tiers (5-level)
- **Inputs**: `overallRecovery` (0-100).
- **Computation**:
  - ≥85 → "Excellent", green, "fully recovered, great time for heavy workout or PR attempt".
  - ≥70 → "Good", warning, "train hard, listen to body".
  - ≥50 → "Moderate", warning, "lighter workout or skill work".
  - ≥30 → "Low", danger, "deload, mobility, or rest day".
  - <30 → "Very Low", danger, "rest day or light recovery".
- **Output**: status text + color + recommendation.
- **Reasoning**: 5-tier gives meaningful granularity; thresholds tuned to physiological recovery curves.

### 2.6 `applyFatigueDecay()` — bi-exponential decay
- **Inputs**: `muscleFatigue`, MUSCLE_DECAY table, `lastFatigueDecay` timestamp.
- **Computation**: `daysSince = (now - lastFatigueDecay) / 86400000`; for each muscle: `fast *= (1 - decay.fast)^daysSince`; `slow *= (1 - decay.slow)^daysSince`; update `lastFatigueDecay = now`.
- **Output**: decayed fatigue per muscle.
- **Reasoning**: bi-exponential — `fast` recovers in hours (neuromuscular); `slow` recovers in days/weeks (muscle damage).

### 2.7 `calculateMuscleLastTrained()` — timestamp map
- **Inputs**: `workoutData.workouts`.
- **Computation**: iterate all workouts, all exercises (with `actual && !skipped`), all muscles in `muscleGroup`; track latest date per muscle; store in `muscleLastTrained[muscle]`.
- **Output**: map of muscle → ISO date.
- **Reasoning**: drives the "last trained X days ago" display and recovery percentage.

### 2.8 `daysSinceTrained(muscle)` helper
- **Inputs**: `muscleLastTrained[muscle]`.
- **Computation**: `daysSince = floor((now - last) / 86400000)`; if never trained → null.
- **Output**: integer days or null.
- **Reasoning**: feeds recovery percentage calculation.

### 2.9 Recovery section UI rendering
- **Inputs**: overall recovery, muscle recovery list, readiness status.
- **Computation**: render circular progress for overall; list muscles sorted by recovery (ascending = most fatigued first); color-code each row (green ≥85, yellow ≥50, red <50); show recommendation banner.
- **Output**: full recovery section.
- **Reasoning**: most-fatigued-first surfaces problem muscles; color coding is scannable.

### 2.10 `window.lastOverallRecovery` global
- **Inputs**: computed overall.
- **Computation**: assigned to `window.lastOverallRecovery` for cross-module access (module 11 `selectExerciseForMuscle` reads it).
- **Output**: global variable.
- **Reasoning**: avoids recomputing recovery in `selectExerciseForMuscle`.

---

## E3. Edge cases handled

- **No workouts in last 7 days**: `calculateSystemicRecoveryFactor` returns 1.0 early.
- **`workoutData.user.weight` missing**: defaults to 150.
- **`actual.rpe` is array vs number**: handled in accumulation.
- **`actual.rpe` missing**: skipped (rpeCount stays 0).
- **`actual.sets` missing**: `totalSets += 0`.
- **`actual.reps` empty array**: `totalReps = 0`; volume += 0.
- **`actual.durations` empty array**: `totalTime = 0`; volume += 0.
- **`recentWorkouts.length = 0`**: returns 1.0.
- **All workouts skipped**: `totalVolume = 0`; relativeVolume = 0; fatigueFromVolume = 0; avgRPE = 5; fatigueFromRPE = 0; totalFatigue = 0; factor = 1.0.
- **`isNaN(factor)`**: returns 0.8.
- **Calculate function throws**: outer try/catch returns 0.8.
- **Muscle never trained**: contributes `weight * 100` to numerator (fully recovered).
- **`muscleLastTrained[muscle]` null**: treated as never trained.
- **`daysSince / restDays` > 1**: clamped to 100% via `min(100, ...)`.
- **`totalWeight = 0`** (no muscles?): `muscleAvg = 100`.
- **`lastFatigueDecay` null** (first boot): `daysSince = 0`; no decay applied.
- **Muscle not in MUSCLE_DECAY**: uses `DEFAULT_DECAY = {fast:0.10, slow:0.010}`.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Systemic factor floor | 0.5 | Even max-fatigue users have 50% recovery (never 0) |
| Systemic factor ceiling | 1.0 | Can't be >100% recovered |
| Fatigue from frequency cap | 50 (5 workouts) | Diminishing returns beyond 5×/week |
| Fatigue from volume cap | 30 (15,000 relative volume) | High volume is the second biggest contributor |
| Fatigue from RPE cap | 20 (avg RPE 10) | Intensity is real but capped — 80% comes from volume + frequency |
| `/200` divisor | gives 0.5 floor at 100 fatigue | Math ensures floor |
| NaN fallback | 0.8 | Conservative default |
| Recovery weighting | 70% muscle + 30% systemic | Local fatigue dominates |
| Readiness tiers | 5 (85/70/50/30) | Granular without overwhelming |
| Recommendation text | per-tier | Actionable, not just a label |
| Muscle sort order | ascending recovery (most fatigued first) | Surfaces problem muscles |
| Color thresholds | 85/50 | Traffic light: green/yellow/red |
| Quads weight | 2.0 (highest) | Largest muscle, biggest fatigue contributor |
| Hand lumbricals weight | 0.1 (lowest) | Tiny muscle, negligible systemic impact |
| `lastOverallRecovery` global | Yes | Avoids recompute |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `calculateSystemicRecoveryFactor` 8-stage | FULL (`Stats.kt`) | — |
| Fatigue index (freq/volume/RPE) | FULL | — |
| 3-component caps (50/30/20) | FULL | — |
| `/200` divisor + clamp [0.5, 1.0] | FULL | — |
| NaN fallback (0.8) | FULL | — |
| `updateRecoverySection` weighted aggregate | FULL | — |
| `muscleWeights` 50-muscle table | FULL (`assets/data/muscle_weights.json`) | — |
| 70/30 muscle/systemic weighting | FULL | — |
| Readiness 5-tier status | FULL (`Recovery.kt`) | — |
| `applyFatigueDecay` bi-exponential | FULL | — |
| `calculateMuscleLastTrained` | FULL | — |
| `daysSinceTrained` helper | FULL | — |
| Recovery UI (most-fatigued-first sort) | FULL | — |
| `lastOverallRecovery` global | FULL (`ProState.kt` field) | — |
| Per-muscle recovery % clamp | FULL | — |
| Muscle never trained → 100% | FULL | — |

**Coverage: 100%** (16 FULL / 0 PARTIAL / 0 MISSING of 16 behaviours)

---

## H. Top 3 gaps to close

1. **Recovery UI doesn't show fatigue breakdown** — the 3-component fatigue (freq/volume/RPE) is computed but not surfaced. Add a small breakdown panel showing which component dominates so users know *why* they're fatigued.
2. **Readiness recommendation is generic** — "train hard, listen to body" is vague. Personalize based on the dominant fatigue component (e.g. "Frequency fatigue high — add a rest day" vs "Volume fatigue high — drop 1 set per exercise").
3. **Muscle sort doesn't separate "never trained" from "trained yesterday"** — both show 100% recovery, but the former is a gap. Add a separate "Undertrained" section listing muscles never trained or last trained >2× restDays ago.
