# 07-exercise-generation — Per-exercise prescription (rep curves, weight from history, muscle fallback)

Source: `reference/scripts/app/07-exercise-generation.js` (L56738–58215 of original `app.js`)

## A. File purpose
This module turns a library exercise into a fully prescribed workout exercise: sets, reps, weight, and progression notes. It handles three regimes — (a) athletic drills / stretches (no-fatigue / time-based) which skip the load math; (b) exercises with prior history on this exact lift (use last session's weight + progression); (c) exercises with no direct history but muscle-group overlap (borrow weight from a sibling exercise's most recent entry); (d) brand-new muscle groups (use `tested1RM` or estimate from `strengthIndex × bodyWeight`, then derive working weight from fiber profile + intensity target).

## B. Public surface
- `generateExerciseFromLibrary(muscleGroup)` — picks a random non-recent exercise from `ultimateExerciseLibrary[muscleGroup]`, builds `{id, name, muscleGroup, prescribed:{sets,reps,weight}, actual:null, progressionNotes, equipment, instructions, loadDistribution{}}` with equal-weight load distribution.
- `generateExercisePrescription(exercise, phaseMultiplier=1.0)` — main entrypoint. Steps: (1) try last session for THIS exercise; (2) fallback to most-recent sibling exercise sharing a muscle; (3) if none, derive from `tested1RM` or `strengthIndex × bodyWeight`; (4) compute target intensity via fiber profile; (5) compute reps via `lovedReps`, sets via `humpSets`, weight via `roundToNearest`; (6) attach `progressionNotes` and `phaseMultiplier`.
- Internal: `lastSession` lookup, sibling-exercise search, new-muscle-group seed weight, fiber-window derivation.

## C. Dependencies
- Globals: `workoutData`, `ultimateExerciseLibrary`, `Math`.
- Cross-module: `getOrCreateExerciseRecord` (13), `getExerciseById` (04), `lovedReps`, `humpSets`, `getExerciseRepFloor`, `getExerciseMuscleType` (06), `getCurrentCyclePhase`, `getPhaseMultiplier` (06), `EXERCISE_PARAMS` / `initExerciseRecursiveFields` / `updateRecursive1RM` (02), `roundToNearest`, `effectiveWeightFor` (engine/Library.kt OneRm), `getMuscleType`.

## D. Native-equivalent mapping
- `generateExerciseFromLibrary` → `engine/Generator.kt::generateExerciseFromLibrary` (L230) + `buildBaseExercise` (L209) + `selectExerciseForMuscle` (L113). Native version uses weighted random by `lastPerformed × importance` (the rotation system from 01) — a deliberate enhancement.
- `generateExercisePrescription` → `engine/Generator.kt::generateExercisePrescription` (L246) — ported, but native version is **simpler**: it does not implement the sibling-exercise muscle-group fallback (step 2 in legacy). That's a real GAP.
- Fiber-window derivation → `engine/Library.kt::OneRm.fiberWeightRange` (L685) + `Library.fiberWindowFor` (L315) — implemented natively using `fiber_profiles.json`.
- `roundToNearest` / `effectiveWeightFor` → `engine/Library.kt::OneRm.roundToNearest` (L616) / `effectiveWeightFor` (L621) — ported.
- `recalcExerciseFromWeight` (companion, 08) → `Generator.recalcExerciseFromWeight` (L747) — ported.

## E. Patterns worth porting
- **Sibling-exercise fallback.** When you've never squatted but you've leg-pressed, the legacy code borrows the most recent leg-press weight as a seed. Native `generateExercisePrescription` starts from `strengthIndex × bodyWeight` instead, which is far noisier. This is a high-value port.
- **Seed weight preference order:** `tested1RM` → last session on this exercise → most-recent sibling → `strengthIndex × bodyWeight`. Native does this in spirit but the sibling step is missing.
- **`loadDistribution = equal across muscles`** for the exercise — `Library.loadDistributionFor` (L469) does the same but defaults to first-muscle-only when the muscle list is empty; legacy equal-spread is safer.
- **Phase multiplier applied to prescribed weight** (not just volume) — legacy multiplies `weight × phaseMultiplier`; native applies it to volume only. Worth comparing.

## F. Feature IDs
`P07-EXERCISE-PRESCRIPTION`, `P07-SIBLING-MUSCLE-FALLBACK`, `P07-SEED-WEIGHT-LADDER`, `P07-FIBER-WINDOW`, `P07-PHASE-WEIGHT-ADJUSTMENT`, `P07-LOAD-DISTRIBUTION`.
