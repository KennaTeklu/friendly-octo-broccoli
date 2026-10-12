# 08-workout-generation — Next-workout generation + deload logic + taper

Source: `reference/scripts/app/08-workout-generation.js` (L58216–58731 of original `app.js`)

## A. File purpose
This module orchestrates a full workout generation pass. It (1) prompts the user with SweetAlert if a current workout has unsaved exercises; (2) computes adaptive volume-based deload (last 4 weeks avg × 1.5 threshold) + systemic deload (recovery < 0.6); (3) detects taper windows (competition within 7 days); (4) selects the next split by rotating from the last workout's type; (5) merges `split.focus` + overdue muscles (`getOverdueMuscles(1.5, 2)`) + ready accessory muscles + auto-core; (6) calls `selectExerciseForMuscle` / `generateExerciseFromLibrary` per target muscle, deduplicates, applies per-muscle deload multiplier, generates prescriptions, and pushes fallback squat/bench/deadlift if the selection comes up empty.

## B. Public surface
- `window.generateNextWorkout()` — entrypoint. If `currentWorkout.exercises.length > 0`, SweetAlert confirm; else direct call to `performGenerateWorkout`.
- `performGenerateWorkout()` — main pipeline: decay fatigue, decay momentum bias, compute systemic factor, volume-based deload (avg of last 4 weeklyVolumes × 1.5), taper detection, split rotation, muscle selection (focus ∪ overdue ∪ ready accessory ∪ optional core), per-muscle deload multiplier, prescription with `localMultiplier` (phase × deload × muscle-deload), fallback to squat/bench/deadlift if empty.

## C. Dependencies
- Globals: `currentWorkout`, `workoutData`, `Swal`, `document`, `Math`, `Date`, `READINESS_THRESHOLD`.
- Cross-module: `applyFatigueDecay`, `decayMomentumBias` (Fatigue), `calculateSystemicRecoveryFactor` (15), `getCurrentCyclePhase`, `getPhaseMultiplier` (06), `getAllMuscleGroups`, `getOverdueMuscles`, `getReadyMuscles` (Library/Fatigue), `selectExerciseForMuscle`, `generateExerciseFromLibrary`, `generateExercisePrescription` (07), `getMuscleDeloadMultiplier` (Fatigue), `updateWorkoutCache` (06), `updateTodaysWorkout`, `updateWorkoutHeader`, `updateResumeButtonVisibility`.

## D. Native-equivalent mapping
- `generateNextWorkout` confirm dialog → `MainActivity.kt::TimeWizardDialog` (L706) — native uses a wizard-style dialog when a draft exists; `ProState.hasDraft` gates generation.
- `performGenerateWorkout` → `engine/Generator.kt::performGenerateWorkout` (L475) — ported with `timeBudgetMin` parameter and `suppressConfirm` flag (native enhancement). Native also has `generateLongevityWorkout` (L704), `appendAthleticBlocks` (L647), `trimWorkoutToBudget` (L800).
- Volume-based deload (`avg weekly volume × 1.5`) → `engine/Sessions.kt::updateMuscleDeloadStatus` (L620) + `updateWeeklyVolumeTracking` (L648) — ported, uses consecutive-sessions rule instead (3 high-fatigue sessions → deload).
- Taper detection (`compDate within 7 days`) → `Model.kt::CompetitionInfo` (L194) + check inside `performGenerateWorkout` — ported.
- Per-muscle deload multiplier → `engine/Library.kt::Fatigue` (L341) `getCoupledFatigue` + `Sessions.updateMuscleDeloadStatus` — implemented.
- `decayMomentumBias` → `engine/Generator.kt::decayMomentumBias` (L613) — ported.

## E. Patterns worth porting
- **Volume-based deload (150% of 4-week average)** as an OR condition with systemic recovery (< 0.6). Native uses consecutive-sessions rule only; adding the volume-spike check would catch overreach earlier.
- **Auto-include `core` when fresh** — a small but genuinely nice touch: every generated workout quietly gets a core exercise if the muscle isn't fatigued.
- **Fallback squat/bench/deadlift if selection is empty** — guarantees the user never sees "no workout generated today". Native should mirror this defensive behavior.
- **Per-muscle deload multiplier composed with phase multiplier** (`localMultiplier = phase × deload × muscleDeload`) — native composes phase × systemic but doesn't multiply in per-muscle deload at prescription time; it's only checked at muscle-readiness stage.
- **Taper preserves intensity** (no deload multiplier applied) — deliberate; native should make this explicit rather than implicit.

## F. Feature IDs
`P08-GENERATE-NEXT-WORKOUT`, `P08-VOLUME-DELoad`, `P08-TAPER-WINDOW`, `P08-SPLIT-ROTATION`, `P08-AUTO-CORE`, `P08-FALLBACK-COMPOUNDS`, `P08-PER-MUSCLE-DELOAD`.
