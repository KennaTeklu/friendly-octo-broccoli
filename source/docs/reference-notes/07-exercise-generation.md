# 07 — Exercise Generation (Prescription + Rotation)

> Reference module: `reference/scripts/app/07-exercise-generation.js` (1 485 lines).
> Native counterparts: `engine/Generator.kt`, `engine/Library.kt`.

This module builds an exercise prescription from a library entry: rotation by `lastPerformed`, weighted selection by `importance`, last-session lookup (with shared-muscle fallback), fiber-type weight estimation, and intensity/reps/sets computation.

---

## E2. Deep patterns

### 2.1 `generateExerciseFromLibrary(muscleGroup)` — rotation
- **Inputs**: muscle group string, `ultimateExerciseLibrary[muscleGroup]`, `workoutData.muscleRecentExercises[muscleGroup]`.
- **Computation**:
  1. If group missing or empty → return null.
  2. Read recent list (default `[]`).
  3. Filter out recent exercise ids → `candidates`.
  4. If candidates empty (all were recent) → use full list (rotation exhausted).
  5. `base = candidates[Math.floor(Math.random() * candidates.length)]`.
  6. Build exercise object: `{id, name, muscleGroup, prescribed:{sets:defaultSets, reps:defaultReps, weight:null}, actual:null, progressionNotes, equipment, instructions}`.
  7. If user has `startingWeight` for this exercise → set `prescribed.weight`.
  8. Build `loadDistribution`: each muscle gets `1/muscleGroup.length` (equal split).
- **Output**: exercise object ready for prescription.
- **Reasoning**: round-robin avoids repeating the same exercise in consecutive sessions for the same muscle.

### 2.2 `generateExercisePrescription(exercise, phaseMultiplier)` — full pipeline
- **Inputs**: exercise object, phase multiplier (default 1.0).
- **Computation** (7-stage pipeline):
  1. **History lookup**: `record.history` for this exercise.
  2. **Last-session fallback**: if no history for this exercise, scan `workoutData.exercises` for any exercise sharing a muscle group, find the most recent non-skipped entry.
  3. **No-history branch**: if still no `lastSession`:
     - Athletic drills & stretches (`noFatigue` or `prescriptionType==='time'`): use library defaults (`sets`, `reps`, optional `duration`).
     - Else: estimate 1RM via `tested1RM` (if exists) OR bodyweight × muscle-group-averaged `strengthIndex`.
  4. **Average strength index for muscle group**: iterate `ultimateExerciseLibrary`, skip `noFatigue`, average `strengthIndex` across all exercises whose `muscles[]` overlap with `targetMuscles`.
  5. **Initial weight**: `weight = 0.6 * estimated1RM * phaseMultiplier * (1 - decay)` where `decay = 0.02^(workouts/50)` (maturity decay toward 0.6×1RM as user advances).
  6. **Reps**: `lovedReps(intensity, workouts, goal, express, aggression)`.
  7. **Sets**: `humpSets(intensity, muscleType, workouts, express)`.
  8. **Steering**: apply `momentumBias.value` to weight (bigger → +5%, stronger → -5%) and to reps (bigger → -1, stronger → +1).
  9. **Clamps**: weight ≥ barbell minimum (45 lbs for barbells, 5 lbs for dumbbells); reps ≥ `getExerciseRepFloor`; sets ∈ [2, peak+1].
- **Output**: exercise with `prescribed.weight`, `prescribed.reps`, `prescribed.sets`, `progressionNotes`.
- **Reasoning**: every prescription is built bottom-up from 1RM (or its estimate), then decayed by maturity and biased by momentum.

### 2.3 Shared-muscle fallback (last session lookup)
- **Inputs**: `exercise.muscleGroup`, all of `workoutData.exercises`.
- **Computation**: build `targetMuscles = new Set(exercise.muscleGroup)`; iterate other exercises; for each with `history`:
  - if `otherExercise.muscleGroup.some(m => targetMuscles.has(m))` → consider it.
  - find most recent non-skipped entry; track best (latest) date.
- **Output**: synthetic `lastSession` with weight, reps (max), sets, rpe, date.
- **Reasoning**: a new bicep exercise should still use the last curl's load as a starting point — the muscles overlap.

### 2.4 Strength-index averaging for new muscle groups
- **Inputs**: `targetMuscles`, `ultimateExerciseLibrary`.
- **Computation**: iterate ALL exercises in the library; skip `noFatigue`; for each whose `muscles[]` overlap with `targetMuscles` → add `strengthIndex` to sum, increment count; `avg = sum/count`.
- **Output**: average `strengthIndex` for the muscle group.
- **Reasoning**: a brand-new muscle group (user has never trained it) uses the population average rather than zero.

### 2.5 Bodyweight + load computation
- **Inputs**: exercise `prescriptionType`, `equipment`, name.
- **Computation**: if `equipment==='bodyweight'` OR name includes `pull|push|dip|chin` → `effectiveWeight = userWeight + prescribedWeight`; else `effectiveWeight = prescribedWeight`.
- **Output**: effective weight for 1RM estimation.
- **Reasoning**: pull-ups at bodyweight+45 lb plate need to count the bodyweight in the load math.

### 2.6 Intensity calculation
- **Inputs**: prescribed weight, estimated 1RM.
- **Computation**: `intensity = prescribedWeight / estimated1RM`, clamped to `[0.4, 1.0]`.
- **Output**: intensity fraction.
- **Reasoning**: feeds `lovedReps` and `humpSets` — both curves are functions of intensity.

### 2.7 Momentum bias application
- **Inputs**: `workoutData.user.momentumBias.value`, exercise.
- **Computation**: `bias = momentumBias.value`; if `bias > 0` (bigger) → `weight *= 1 + 0.05*bias`, `reps -= 1*bias`; if `bias < 0` (stronger) → `weight *= 1 + 0.05*bias` (also increased! — but `reps += 1*|bias|`).
- **Output**: steered prescription.
- **Reasoning**: "bigger" pushes heavier weights for fewer reps (hypertrophy bias); "stronger" pushes heavier weights for more reps (strength-endurance bias).

### 2.8 Clamping pipeline
- **Inputs**: computed reps, sets, weight.
- **Computation**:
  - `reps = Math.max(reps, getExerciseRepFloor(exercise))`
  - `reps = Math.min(20, reps)`
  - `sets = Math.max(2, sets)`
  - `sets = Math.min(peak+1, sets)`
  - `weight = Math.max(barbellMin, weight)` (45 for barbell, 5 for dumbbell, 0 for machine)
- **Output**: safe prescription.
- **Reasoning**: never prescribe 0 reps, 0 sets, or below-minimum weight.

---

## E3. Edge cases handled

- **Muscle group not in library**: returns null; workout gen falls through to default moves.
- **Empty exercise list for muscle group**: returns null.
- **All exercises recently used**: candidates reset to full list (rotation exhausted).
- **No history for exercise AND no shared-muscle history**: falls back to bodyweight × strengthIndex average.
- **No `tested1RM` for new exercise**: uses bodyweight × avg strengthIndex.
- **Athletic drill or time-based exercise**: bypasses fiber/weight math — uses library defaults.
- **Exercise has no `muscleGroup`**: `getExerciseMuscleType` returns `'medium'`; `getExerciseRepFloor` returns 1.
- **User weight missing**: `userWeight = user.weight || 150`.
- **`momentumBias.value` is 0**: no steering applied.
- **`momentumBias.value` exceeds ±1**: clamp in module 03.
- **Computed weight below bar minimum**: clamped to 45 (barbell) / 5 (dumbbell) / 0 (machine).
- **Computed reps > 20**: clamped to 20.
- **Computed reps < rep floor**: clamped to floor (1/5/10).
- **`phaseMultiplier` undefined**: defaults to 1.0.
- **`prescriptionType === 'amrap'`**: reps become "AMRAP" string in UI; math still uses computed reps for volume tracking.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Starting intensity for new users | 60% × 1RM | Beginner-safe load |
| Maturity decay toward 60% | `0.02^(workouts/50)` | Advanced lifters train at 60% on average (true 1RM is higher) |
| Steering weight step | ±5% per bias unit | Noticeable but not drastic |
| Steering reps step | ±1 per bias unit | One rep per tap — fine-grained |
| Barbell minimum | 45 lbs | Olympic bar weight |
| Dumbbell minimum | 5 lbs | Smallest dumbbell |
| Machine minimum | 0 lbs | Stack starts at 0 |
| Rep upper bound | 20 | Cardio threshold |
| Set floor | 2 | Anything below is warm-up |
| Set ceiling | peak+1 | Allow one above Prilepin peak for advanced |
| Shared-muscle fallback window | All history | Conservative — even a 6-month-old curl can seed a new curl |
| Recent-rotation length | Last 2 exercises | Module 03 `take(2)` |
| `momentumBias.lastTap` carries into next gen | Yes | Bias persists across sessions until decay |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `generateExerciseFromLibrary` rotation | FULL (`Library.kt` + `Generator.kt`) | — |
| `generateExercisePrescription` 7-stage pipeline | FULL (`Generator.kt`) | — |
| Last-session fallback (shared muscle) | FULL | — |
| Strength-index averaging for new groups | FULL | — |
| Bodyweight + load computation | FULL | — |
| Intensity calc + clamp [0.4, 1.0] | FULL | — |
| Momentum bias application (weight + reps) | FULL | — |
| Clamping pipeline (reps/sets/weight) | FULL | — |
| Athletic drill bypass (noFatigue) | FULL | — |
| `prescriptionType='time'` routing | FULL | — |
| `prescriptionType='amrap'` UI tag | PARTIAL | AMRAP reps tracked but UI doesn't show "AMRAP" tag clearly |
| `loadDistribution` equal-split per muscle | PARTIAL | Computed but not surfaced in UI as a pie/bar |
| `progressionNotes` per exercise | FULL | — |

**Coverage: 85%** (10 FULL / 2 PARTIAL / 0 MISSING of 13 behaviours)

---

## H. Top 3 gaps to close

1. **`loadDistribution` not surfaced in UI** — the per-muscle load split is computed (e.g. squat = 60% quads / 30% glutes / 10% core) but not shown on the exercise card. Add a small bar chart under the prescribed weight so users see which muscles get the most stimulus.
2. **AMRAP tag unclear** — `prescriptionType='amrap'` exercises still show a number. Change the UI to display "AMRAP" as the rep target so users know to go to failure.
3. **Shared-muscle fallback window is unbounded** — a 6-month-old deadlift seeding a new Romanian deadlift is fine, but a 2-year-old one is not. Cap the fallback to 90 days.
