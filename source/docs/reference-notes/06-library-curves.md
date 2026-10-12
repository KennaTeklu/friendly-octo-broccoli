# 06 — Library Curves, Cycle Phases, Muscle Types, Rep Floors

> Reference module: `reference/scripts/app/06-library-curves.js` (431 lines).
> Native counterparts: `engine/Library.kt`, `engine/Generator.kt`, `engine/Stats.kt`.

This module implements the demographic cache (`updateUserCache`, `updateWorkoutCache`), muscle-type classification (`getMuscleType`, `getRepFloorForMuscle`), the **loved rep curve** (`lovedReps`), the **Prilepin-style sets curve** (`humpSets`), and the menstrual cycle phase functions.

---

## E2. Deep patterns

### 2.1 `updateUserCache()` — pre-compute demographic multipliers
- **Inputs**: `workoutData.user` (birthDate, gender, weight, experience), `getCurrentCyclePhase()`.
- **Computation** (7 cached factors):
  1. `age = birthDate ? calculateAge(birthDate) : 30`
  2. `effectiveBodyWeight = weight || (gender==='female' ? 130 : 150)`
  3. `ageFactor = age<30 ? 0.9 : age<=50 ? 1.0 : 0.8`
  4. `genderFactor = gender==='female' ? 0.9 : 1.0`
  5. `experienceFactor = expMap[experience]` ({beginner:0.8, intermediate:1.0, advanced:1.2})
  6. `expDeltaFactor = expDeltaMap[experience]` ({beginner:1.3, intermediate:1.0, advanced:0.7})
  7. `phaseFactor = getPhaseMultiplier(phase)` (menstrual=0.6, follicular=1.0, ovulatory=1.05, luteal=0.8, null=1.0)
- **Combined multipliers**:
  - `priorBaseMultiplier = experienceFactor * ageFactor * genderFactor * phaseFactor` (used for absolute prescription baselines)
  - `progBaseMultiplier = expDeltaFactor * ageDelta * genderDelta * phaseFactor` (used for progression deltas)
  - where `ageDelta = age<30 ? 1.0 : age<=50 ? 0.8 : 0.6` and `genderDelta = gender==='female' ? (phase in [luteal,menstrual] ? 0.7 : 1.0) : 1.0`
- **Output**: populated `_cache` object on user.
- **Reasoning**: every prescription call uses these 7 factors; precomputing avoids 7 multiplies per exercise per set.

### 2.2 `updateWorkoutCache()` — session-level cache
- **Inputs**: `currentWorkout`, `workoutData.user._cache`, `calculateSystemicRecoveryFactor()`.
- **Computation**: `recoveryFactor = calculateSystemicRecoveryFactor()`; `sessionPriorBase = user._cache.priorBaseMultiplier * recoveryFactor`; `sessionDeltaBase = user._cache.progBaseMultiplier * recoveryFactor`.
- **Output**: per-workout cache.
- **Reasoning**: scales all exercises in a session by the same recovery factor; avoids re-querying fatigue per exercise.

### 2.3 `getMuscleType(muscleName)` — three-tier classification
- **Inputs**: muscle name string.
- **Computation**:
  - `LARGE = ['quads','glutes','lats','back','chest','hamstrings','erectors']` (7 muscles)
  - `MEDIUM = ['biceps','triceps','shoulders','front_delts','rear_delts','traps','calves','core','obliques','hip_flexors','adductors','abductors']` (12 muscles)
  - `SMALL = ['forearms','neck','deep_neck','tibialis','soleus','peroneus_tertius','foot_intrinsics','hand_intrinsics','thenar','supinator','pronator','brachialis','brachioradialis','anconeus','popliteus','articularis_genus','multifidus','transverse','quadratus_plantae']` (19 muscles)
  - Returns `'large'` / `'medium'` / `'small'`, defaults to `'small'` for safety.
- **Output**: type string used by `getRepFloorForMuscle` and `humpSets`.
- **Reasoning**: small muscles can't safely handle 1-rep maxes; rep floor protects them.

### 2.4 `getRepFloorForMuscle(muscleName)` — rep floor by type
- **Inputs**: muscle type.
- **Computation**: `small→10`, `medium→5`, `large→1`.
- **Output**: minimum reps prescription will ever assign for this muscle.
- **Reasoning**: small muscles (forearms, tibialis) are injury-prone at low reps; medium muscles (biceps) need ≥5 reps for hypertrophy stimulus.

### 2.5 `getExerciseRepFloor(exercise)` — strictest floor
- **Inputs**: exercise with `muscleGroup[]`.
- **Computation**: returns the maximum floor across all muscles in the exercise (`Math.max(floor) for each muscle`).
- **Output**: integer floor 1/5/10.
- **Reasoning**: a squat (large) has floor 1, but a calf-raise (small) has floor 10; a split squat (quads+glutes, both large) is 1.

### 2.6 `lovedReps(intensity, workouts, goal, express, aggression)` — the **rep curve**
- **Inputs**: intensity (0.40-0.95), workouts count (maturity), goal string, express flag, aggression scalar.
- **Computation** (8-step formula):
  1. **Base k** (exponential decay constant): `powerlifting→5.0`, `balanced→3.8`, `hypertrophy→3.2`, `endurance→2.8`; if `express && powerlifting → 6.0`.
  2. **Aggression scaling**: `k *= aggression` (user-tunable 0.8-1.3).
  3. **Shallower below 75%**: `if intensity<0.75 → k *= 0.7` (preserves warm-up reps).
  4. **Raw exponential**: `raw = 20 * exp(-k * (intensity - 0.4))` (20 reps at 40%, decays toward 0).
  5. **Maturity decay**: `decay = 0.94^(workouts/15)` (reps drop as user accumulates workouts — advanced lifters do fewer reps at same intensity); `express+powerlifting → 0.90^(workouts/12)`.
  6. **Reps after decay**: `reps = raw * decay`.
  7. **Goal factor**: `powerlifting→0.9, hypertrophy→1.1, endurance→1.2, else 1.0`; `reps *= factor`.
  8. **Linear floor**: `floor = 18 - (intensity - 0.4) * 30`, clamped to `[1, 18]`; `reps = max(reps, floor)`.
  9. **Beginner protection** (workouts<20): `beginnerFloor = max(6, 18 - (intensity-0.4)*30)`; `reps = max(reps, beginnerFloor)`.
  10. **Final clamp**: `reps = min(20, round(reps*10)/10)`.
- **Output**: float reps in [1, 20].
- **Reasoning**: exponential curve gives high reps at low intensity (warm-up) and drops sharply near max; the linear floor ensures even advanced lifters do ≥3 reps at 90% (form safety); beginner floor protects the first 20 workouts with ≥6 reps minimum.

### 2.7 `humpSets(intensity, muscleType, workouts, express)` — the **sets curve**
- **Inputs**: intensity, muscle type, workouts count, express flag.
- **Computation** (6-step formula):
  1. **Peak sets by type**: `{large:5, medium:4, small:3}[muscleType] || 4`.
  2. **Constants**: `minSets=2`, `peakIntensity=0.75`, `width=0.25`.
  3. **Hump position**: `t = (intensity - 0.75) / 0.25`.
  4. **Curve**: `curve = 1 - t²` (parabolic, peaks at t=0 i.e. intensity=0.75).
  5. **Raw sets**: `raw = minSets + (peak - minSets) * curve`, clamped to `[minSets, peak]`.
  6. **Experience factor**: `expFactor = min(1.2, 0.8 + workouts/150)`; `express+workouts>20 → min(1.3, 0.9 + workouts/100)`.
  7. **Final**: `sets = raw * expFactor`, clamped to `[minSets, peak+1]` and rounded.
- **Output**: integer sets in [2, peak+1].
- **Reasoning**: Prilepin's table is a hump — most sets at 75% intensity, fewer at extremes (warm-up or max). Beginner (low workouts) gets fewer sets; advanced gets more.

### 2.8 `getExerciseMuscleType(exercise)` — strictest muscle type
- **Inputs**: exercise with `muscleGroup[]`.
- **Computation**: if any muscle is `'small'` → return `'small'`; elif any `'medium'` → `'medium'`; else `'large'`.
- **Output**: muscle type for the exercise.
- **Reasoning**: a squat+core combo (large+medium) → medium floor; a calf+forearm combo → small floor (strictest wins).

### 2.9 `getCurrentCyclePhase(date)` — menstrual phase
- **Inputs**: `workoutData.user.gender`, `workoutData.user.menstrual.lastPeriodStart`, `cycleLength`.
- **Computation**: returns null if not female or no `lastPeriodStart`; else `daysSince = floor((today - last) / 86400000)`; `cycleDay = (daysSince % cycleLength) + 1`; cycleDay ≤5 → `'menstrual'`; ≤13 → `'follicular'`; ≤17 → `'ovulatory'`; else `'luteal'`.
- **Output**: phase string or null.
- **Reasoning**: 28-day cycle assumption; phases: 5-day bleed, 8-day follicular (rising estrogen), 4-day ovulatory (peak), 11-day luteal (progesterone).

### 2.10 `getPhaseMultiplier(phase)` — performance multiplier
- **Inputs**: phase string.
- **Computation**: `menstrual→0.6, follicular→1.0, ovulatory→1.05, luteal→0.8, null/unknown→1.0`.
- **Output**: multiplier applied to prescription weights.
- **Reasoning**: ovulatory is +5% (peak strength); menstrual is -40% (energy low); luteal is -20% (progesterone fatigue).

---

## E3. Edge cases handled

- **User weight missing**: `effectiveBodyWeight = weight || (gender==='female' ? 130 : 150)` (gender-specific median).
- **BirthDate missing**: age defaults to 30 (population median).
- **Experience unknown**: `experienceFactor = expMap[experience] || 1.0` (intermediate default).
- **Phase null (male or no period logged)**: `phaseFactor = 1.0` (no penalty).
- **`currentWorkout` null when `updateWorkoutCache` called**: returns early.
- **Muscle not in any size list**: defaults to `'small'` (safest floor).
- **Exercise with empty `muscleGroup`**: `getExerciseRepFloor` returns 1; `getExerciseMuscleType` returns `'medium'`.
- **Intensity > 0.95**: `lovedReps` returns ~1 rep (exponential + floor clamp).
- **Intensity < 0.40**: still computes; `raw` is high (20+); floor caps at 18.
- **`workouts = 0`**: `decay = 0.94^0 = 1.0` (no decay); beginner floor applies.
- **`workouts < 20`**: beginner floor (min 6 reps) overrides.
- **`aggression` undefined**: defaults to 1.0 (multiplication identity).
- **Phase string not in switch**: `getPhaseMultiplier` returns 1.0.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Age < 30 factor | 0.9 (slightly weaker) | Young lifters have less neural efficiency |
| Age 30-50 factor | 1.0 (peak) | Prime strength years |
| Age > 50 factor | 0.8 | Sarcopenia onset |
| Female gender factor | 0.9 | Population-average upper-body strength differential |
| Beginner expFactor | 0.8 | Conservative prescription |
| Advanced expFactor | 1.2 | Tolerates higher load |
| Beginner expDeltaFactor | 1.3 | Faster progression (newbie gains) |
| Advanced expDeltaFactor | 0.7 | Slower progression (near genetic ceiling) |
| Menstrual phase multiplier | 0.6 | 40% intensity reduction during bleed |
| Ovulatory phase multiplier | 1.05 | 5% boost during peak strength window |
| Luteal phase multiplier | 0.8 | 20% reduction (progesterone fatigue) |
| Rep floor for small muscles | 10 reps | Injury prevention |
| Rep floor for medium muscles | 5 reps | Hypertrophy minimum |
| Rep floor for large muscles | 1 rep | Allows heavy singles |
| Beginner floor (workouts<20) | 6 reps min | Form safety during neural adaptation |
| `lovedReps` upper bound | 20 reps | Beyond 20 is cardio, not strength |
| `humpSets` peak for large | 5 sets | Classic strength volume |
| `humpSets` peak for small | 3 sets | Small muscles fatigue quickly |
| Peak intensity (Prilepin) | 0.75 | Maximum sets at 75% — proven hypertrophy sweet spot |
| Maturity decay base | 0.94^(w/15) | Advanced lifters do ~6% fewer reps per 15 workouts |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `updateUserCache()` 7-factor cache | FULL (`ProfileState.kt`) | — |
| `updateWorkoutCache()` session base | FULL (`Generator.kt`) | — |
| `getMuscleType` 3-tier classification | FULL (`Library.kt`) | — |
| `getRepFloorForMuscle` (10/5/1) | FULL | — |
| `getExerciseRepFloor` strictest floor | FULL | — |
| `lovedReps` 10-step formula | FULL (`Generator.kt` `lovedReps` function) | — |
| `humpSets` Prilepin curve | FULL (`Generator.kt` `humpSets` function) | — |
| `getExerciseMuscleType` strictest type | FULL | — |
| `getCurrentCyclePhase` 4-phase | FULL (`Stats.kt`) | — |
| `getPhaseMultiplier` (0.6/1.0/1.05/0.8) | FULL | — |
| Beginner floor (workouts<20) | FULL | — |
| `aggression` parameter exposed | PARTIAL | Computed internally but not user-tunable in UI |

**Coverage: 92%** (11 FULL / 1 PARTIAL / 0 MISSING of 12 behaviours)

---

## H. Top 3 gaps to close

1. **`aggression` parameter not exposed** — the multiplier exists but is hardcoded to 1.0. Expose as a "training aggression" slider (0.8 conservative, 1.0 default, 1.3 aggressive) in Settings → Training Preferences so users can dial intensity up/down.
2. **Phase multiplier applies only to weight, not sets/reps** — the reference scales weight by phase but leaves `lovedReps` and `humpSets` unmodified. In luteal/menstrual phases, sets should also reduce by 1 (volume reduction). Apply the phase multiplier to `peak` in `humpSets`.
3. **Maturity decay is global** — `0.94^(workouts/15)` decays reps regardless of exercise. Newbie gains differ per exercise (deadlift progresses faster than curl). Track per-exercise workout count and decay independently.
