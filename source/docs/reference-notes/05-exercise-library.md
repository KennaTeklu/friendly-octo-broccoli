# 05 — Ultimate Exercise Library (52 K-line data store)

> Reference module: `reference/scripts/app/05-exercise-library.js` (52 067 lines).
> Native counterparts: `engine/Library.kt`, `assets/data/exercises.json`, `assets/data/bodyweight.json`, `assets/data/fiber_profiles.json`.

This module is the **data store** (not algorithmic). It defines `ultimateExerciseLibrary` — a giant object keyed by muscle group, where each entry has 11 fields including `strengthIndex`, `skillFactor`, `genderSuitability`, `prescriptionType`, `fitnessComponents`.

---

## E2. Deep patterns

### 2.1 Library schema (per exercise)
- **Inputs**: hard-coded data.
- **Computation**: each exercise has `{name, muscles[], equipment, defaultSets, defaultReps, progression, instructions[], strengthIndex, skillFactor, genderSuitability, prescriptionType, fitnessComponents[]}`.
- **Output**: 11 fields per exercise, ~600+ exercises total across all muscle groups.
- **Reasoning**: `strengthIndex` (expected 1RM/bodyweight) seeds the Kalman prior in module 02; `skillFactor` (0.5=technical, 1.0=simple) gates beginner inclusion; `prescriptionType` ('reps' | 'time' | 'amrap') routes through different prescription paths in module 07.

### 2.2 `strengthIndex` table
- **Inputs**: per-exercise constant.
- **Computation**: squat=1.5, deadlift=1.8, bench=1.2, OHP=0.8, leg extension=0.8, goblet squat=1.0, bicep curl=0.4, tricep extension=0.4.
- **Output**: multiplier used by `initExerciseRecursiveFields`: `mu = strengthIndex * userWeight`.
- **Reasoning**: encodes the population-average 1RM-to-bodyweight ratio; lets the engine prescribe a sensible starting weight before any history exists.

### 2.3 `skillFactor` table
- **Inputs**: per-exercise constant (0.5 to 1.0).
- **Computation**: barbell back squat=0.6, front squat=0.55, goblet=0.7, smith=0.85, leg extension=0.95.
- **Output**: difficulty rating used by `getEnhancedExerciseList` → `difficulty` field (`hard`/`medium`/`easy`): `<0.5→hard`, `<0.7→medium`, else `easy`.
- **Reasoning**: beginners should not be prescribed snatch (`skillFactor≈0.4`); the gate prevents injury.

### 2.4 `prescriptionType` routing
- **Inputs**: enum `'reps'` | `'time'` | `'amrap'`.
- **Computation**: most exercises = `'reps'`; stretches/mobility = `'time'` with `defaultDuration` (seconds) replacing `defaultReps`; AMRAP = `'amrap'` for finishers.
- **Output**: routing flag consumed by `generateExercisePrescription`.
- **Reasoning**: time-based movements (plank, dead hang) should not be forced into the rep-curve math.

### 2.5 `genderSuitability` filter
- **Inputs**: per-exercise string `'both'` | `'male'` | `'female'`.
- **Computation**: currently informational only — not actively filtered (planned future use).
- **Output**: tag for future filtering.
- **Reasoning**: some pre-natal/postpartum-safe exercises should only surface for female users in luteal/menstrual phases.

### 2.6 `fitnessComponents` tags
- **Inputs**: per-exercise list of strings.
- **Computation**: each exercise tagged with one or more of: `muscular_strength`, `muscular_endurance`, `power`, `balance`, `coordination`, `speed`, `agility`, `reaction_time`, `cardiorespiratory_endurance`, `body_composition`, `flexibility`.
- **Output**: tag list consumed by weekly split logic (module 04 splits ensure every component is covered).
- **Reasoning**: ties the exercise library to the 11-component fitness model.

### 2.7 `noFatigue` flag (athletic drills & stretches)
- **Inputs**: boolean on athletic drills / stretches.
- **Computation**: drills like `athletic_power`, `stretching_static` have `noFatigue=true`.
- **Output**: signals `generateExercisePrescription` to bypass the fiber/weight math (use library defaults only); also excluded from `strengthIndex` averaging in module 07.
- **Reasoning**: stretches and skill drills don't accumulate muscle fatigue the same way; mixing them into strength estimates would dilute the average.

### 2.8 `defaultSets` / `defaultReps` conventions
- **Inputs**: per-exercise constant.
- **Computation**: barbell back squat = 5×5; goblet squat = 3×10-15; leg extension = 3×12-20.
- **Output**: fallback prescription if no history exists.
- **Reasoning**: aligns with proven programme conventions (5×5 for compounds, 3×12 for hypertrophy, 3×15+ for isolation).

### 2.9 `progression` notes
- **Inputs**: per-exercise string.
- **Computation**: e.g. squat = "add 5 lbs when all sets completed", goblet = "heavier dumbbell", leg press = "add 10-20 lbs".
- **Output**: text shown in UI as progression guidance.
- **Reasoning**: small lifts progress in 2.5-5 lb jumps; machine lifts in 10-20 lb jumps (plates/machine stack).

### 2.10 `instructions[]`
- **Inputs**: per-exercise list of strings.
- **Computation**: 3-4 cue strings per exercise (e.g. squat = "Place bar on upper traps", "Squat to parallel", "Drive through heels").
- **Output**: form cues shown in exercise card.
- **Reasoning**: beginners need form cues more than weight prescriptions.

### 2.11 Augmentation pass (`primaryMuscle` auto-fill)
- **Inputs**: each exercise after library load.
- **Computation**: `exercise.primaryMuscle = exercise.muscles[0]` if missing; `importance` derived from muscle group ('core'/'accessory'/'specialised').
- **Output**: every exercise has `primaryMuscle` and `importance` for rotation fairness.
- **Reasoning**: weighted rotation (module 01) needs `importance` to bias core lifts.

---

## E3. Edge cases handled

- **Exercise has no `muscles` array**: defaults to `[]` in `getEnhancedExerciseList`.
- **`primaryMuscle` missing**: falls back to `exercise.muscles[0]` or `muscleGroup` (the key in the library).
- **`skillFactor` missing**: defaults to `'medium'` difficulty.
- **`equipment` missing**: defaults to `'unknown'`.
- **`prescriptionType` missing**: treated as `'reps'`.
- **`defaultReps` is a range string ("5-8")**: stored as string; rep parser splits on `-`.
- **`defaultDuration` missing for time-based**: defaults to 45 seconds.
- **`noFatigue=true`**: excluded from strength-index averaging in module 07.
- **`progression` missing**: replaced with "Quality over quantity — crisp reps, steady breathing." in module 07.
- **Muscle group not in library**: `generateExerciseFromLibrary` returns null; workout gen falls through to default moves.
- **`genderSuitability` doesn't match user**: not actively filtered (planned).

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| `strengthIndex` for deadlift | 1.8 (highest) | Deadlift uses the most muscle mass — population ratio tops out near 2× bodyweight |
| `strengthIndex` for OHP | 0.8 | OHP is mechanically inefficient — even elite lifters rarely exceed bodyweight |
| `strengthIndex` for isolation | 0.4 | Biceps/triceps can move ~40% of bodyweight |
| `skillFactor` for front squat | 0.55 (harder than back squat 0.6) | Front rack demands wrist mobility |
| `skillFactor` for smith squat | 0.85 (easiest) | Guided path removes balance component |
| Default squat prescription | 5×5 | Classic strength rep range |
| Default leg extension | 3×12-20 | Hypertrophy + metabolic stress |
| Default goblet squat | 3×10-15 | Beginner-friendly; load capped by dumbbell availability |
| `progression` jump for compounds | 5 lbs | Smallest plate increment |
| `progression` jump for machines | 10-20 lbs | Stack increments |
| Form cue count | 3 per exercise | Fits on a card without scroll |
| Component coverage per split | ≥1 per session | 7-day rotation covers all 11 components |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| Library schema (11 fields per exercise) | FULL (`assets/data/exercises.json` matches schema) | — |
| `strengthIndex` per exercise | FULL | — |
| `skillFactor` per exercise | FULL | — |
| `prescriptionType` enum (reps/time/amrap) | FULL | — |
| `genderSuitability` filter | MISSING (data present, filter not enforced in UI) | Female-only exercises never surfaced |
| `fitnessComponents` tag list | FULL | — |
| `noFatigue` flag on drills/stretches | FULL | — |
| `defaultSets`/`defaultReps` per exercise | FULL | — |
| `progression` text per exercise | FULL | — |
| `instructions[]` form cues | FULL | — |
| `primaryMuscle` auto-fill augmentation | FULL (`Library.kt` augmentation pass) | — |
| `importance` auto-tag (core/accessory/specialised) | FULL | — |
| Exercise count (~600+) | PARTIAL | Native library is ~40% the size of the reference 52K-line library; many isolation/longevity exercises missing |

**Coverage: 77%** (10 FULL / 2 PARTIAL / 1 MISSING of 13 behaviours)

---

## H. Top 3 gaps to close

1. **`genderSuitability` filter is missing** — the data flag exists in the reference and JSON, but the Kotlin `selectExerciseForMuscle` does not gate on it. Add a filter pass when the user is in luteal/menstrual phase or postpartum months are set.
2. **Exercise library is ~40% the size of the reference** — many longevity muscles (popliteus, articularis_genus, quadratus_plantae) have zero exercises. Audit `assets/data/exercises.json` against the reference and fill the gaps with at least 2 exercises per missing muscle.
3. **No `importance` tier in native UI** — the `core/accessory/specialised` tag is computed but not surfaced. Show it as a colour chip in the library card so users see why a lift is weighted heavier in rotation.
