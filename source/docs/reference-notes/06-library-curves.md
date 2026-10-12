# 06-library-curves — Cache helpers, muscle types, rep/set curves, cycle phases

Source: `reference/scripts/app/06-library-curves.js` (L56314–56737 of original `app.js`)

## A. File purpose
This module is the calibration layer between the library and the generator. It (1) memoizes demographic multipliers into `workoutData.user._cache` (age, gender, experience, phase, progression factors); (2) builds the enriched exercise list (`getEnhancedExerciseList`) with derived difficulty/performed flags; (3) classifies muscles by size (large/medium/small) for rep floors; (4) implements the **loved reps curve** (exponential decay + linear floor + beginner protection + express/aggression modifiers) and the **hump-shaped sets curve** (Prilepin-style peak at 75%); (5) computes menstrual cycle phase from `lastPeriodStart`.

## B. Public surface
- `updateUserCache()` — populates `user._cache` with `effectiveBodyWeight`, `ageFactor` (<30:0.9, ≤50:1.0, else 0.8), `genderFactor` (female:0.9), `experienceFactor` (beg 0.8/int 1.0/adv 1.2), `expDeltaFactor` (1.3/1.0/0.7), `phaseFactor`, `priorBaseMultiplier`, `progBaseMultiplier`.
- `updateWorkoutCache()` — seeds `currentWorkout._cache.recoveryFactor`, `sessionPriorBase`, `sessionDeltaBase`.
- `getEnhancedExerciseList()` — flattens `ultimateExerciseLibrary` into a list with `id`, `primaryMuscle`, `difficulty` (skillFactor-based), `performed`, `performedCount`, `equipment`, `muscles[]`; applies `enhanceExerciseWithDerivation`.
- `getMuscleType(muscleName)` — large/medium/small classifier.
- `getRepFloorForMuscle(muscleName)` — small→10, medium→5, large→1.
- `getExerciseRepFloor(exercise)` — strictest floor among its muscles.
- `lovedReps(intensity, workouts, goal, express, aggression)` — exponential rep curve `20·exp(-k·(intensity-0.4))·decay`, goal-dependent `k` (powerlifting 5.0, balanced 3.8, hypertrophy 3.2, else 2.8), 75%-below shallow modifier, linear floor `18-(intensity-0.4)·30`, beginner protection (workouts<20 → floor≥6), capped at 20 reps.
- `humpSets(intensity, muscleType, workouts, express)` — Prilepin-style parabola peaking at 75% intensity with `{large:5, medium:4, small:3}` peaks, experience factor up to 1.2×, clamped to `[2, peak+1]`.
- `getExerciseMuscleType(exercise)` — strictest type among muscles.
- `getCurrentCyclePhase(date)` — `menstrual` (1–5), `follicular` (6–13), `ovulatory` (14–17), `luteal` (18+).
- `getPhaseMultiplier(phase)` — menstrual 0.6, follicular 1.0, ovulatory 1.05, luteal 0.8.

## C. Dependencies
- Globals: `workoutData`, `currentWorkout`, `ultimateExerciseLibrary`.
- Cross-module: `calculateAge`, `calculateSystemicRecoveryFactor` (15), `enhanceExerciseWithDerivation` (defined later), `muscleDatabase` (10).

## D. Native-equivalent mapping
- `updateUserCache` / `updateWorkoutCache` → **GAP** (no memoization; `Stats.kt` recomputes inline). Highest-value port.
- `getEnhancedExerciseList` → `engine/Library.kt::Library.allLibraryExercises` (L219) + `augmented` (L279).
- `getMuscleType` / `getRepFloorForMuscle` / `getExerciseRepFloor` / `getExerciseMuscleType` → `engine/Generator.kt` L50–65 — fully ported.
- `lovedReps` → `engine/Generator.kt` does NOT have a direct equivalent; native `Generator.generateExercisePrescription` (L246) uses fixed rep ranges from `defaultReps` field — **GAP**.
- `humpSets` → `engine/Generator.kt::humpSets` (L76) — implemented, signature `(intensity, muscleType, workouts, express)` matches.
- `getCurrentCyclePhase` → `engine/Sessions.kt::getCurrentCyclePhase` (L809) — ported, same day-window logic.
- `getPhaseMultiplier` → `engine/Sessions.kt::getPhaseMultiplier` (L826) — ported, same constants (0.6/1.0/1.05/0.8).

## E. Patterns worth porting
- **Loved reps curve** is mathematically elegant: blends exponential decay, goal-specific tuning (`k`), express/aggression modifiers, 75%-below shallower slope (preserve warmup), linear floor, beginner floor (≥6 reps), 20-rep cap. Native uses static rep strings — porting this would make every session adapt to the user's intensity/load choice dynamically.
- **`updateUserCache` memoization** — converting age/gender/experience/phase multipliers once per session vs per-call is a free CPU win; `Stats.kt` calls these in `recoveryReport`, `longevityBaseRecommendations`, etc.
- **Beginner floor protection** (`workouts<20 → reps≥6`) — native has nothing equivalent; beginners get prescribed 3-rep powerlifting sets, which is risky.

## F. Feature IDs
`P06-USER-CACHE-MEMO`, `P06-MUSCLE-TYPE-CLASSIFIER`, `P06-LOVED-REPS-CURVE`, `P06-HUMP-SETS-CURVE`, `P06-CYCLE-PHASE-MULTIPLIER`, `P06-BEGINNER-FLOOR-PROTECTION`.
