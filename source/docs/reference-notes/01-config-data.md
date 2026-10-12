# 01-config-data — Version, data structures, Dexie, Phase 2 CIG

Source: `reference/scripts/app/01-config-data.js` (L1–485 of original `app.js`)

## A. File purpose
This module is the bootstrap for the entire HTML app: it defines the app version, wires a version-mismatch banner to `localStorage`, declares the central `workoutData` object (user profile, workouts, exercises, history, muscle fatigue, momentum bias, demographic `_cache`), and opens a Dexie IndexedDB store. It also documents the exercise rotation system (weighted random by `lastPerformed` × importance) used by `generateNextWorkout`.

## B. Public surface
- `APP_VERSION` (`'2.0.0'`) — current release tag, compared against `localStorage[VERSION_STORAGE_KEY]`.
- `VERSION_STORAGE_KEY` — localStorage key for the last-seen version.
- (IIFE) — runs on load; renders `#versionBanner` with `Refresh Now` / `Later` buttons when versions differ.
- `workoutData` — root state: `user`, `workouts[]`, `exercises{}`, `history[]`, `muscleFatigue{}`, `lastFatigueDecay`, `muscleRecentExercises{}`, `syncedToBackend`, `couplingMatrix{}`.
- `workoutData.user` — `name`, `birthDate`, `gender`, `weight`, `height`, `experience`, `goal`, `created`, `settings{}`, `agingRisks[]`, `menstrual{}`, `momentumBias{}`, `_cache{}` (effectiveBodyWeight, ageFactor, genderFactor, experienceFactor, expDeltaFactor, phaseFactor, priorBaseMultiplier, progBaseMultiplier).
- `db` — `new Dexie('WorkoutApp')` with `version(1).stores({ workouts, exercises, ... })`.

## C. Dependencies
- Globals: `localStorage`, `document`, `location`, `Dexie` (3rd-party), `Swal` (SweetAlert assumed in later modules).
- Cross-module: feeds `workoutData` consumed by 02–18; rotation helpers `updateExerciseLastPerformed`, `isMuscleReady`, `selectExerciseForMuscle`, `backfillLastPerformed` are documented here and defined later.

## D. Native-equivalent mapping
- `workoutData` → `core/Model.kt::WorkoutData` (L62), `UserProfile` (L79), `Settings` (L111), `AgingRisk` (L128), `MenstrualState` (L139), `MomentumBias` (L149), `UserCache` (L157), `Aggregates` (L178), `WeeklyVolume` (L188), `DeloadStatus` (L191).
- Version banner + bootstrap → `MainActivity.kt::BootSplash` (L209) and `core/ProState.kt::init` (L66) / `loadAll` (L116). No native version-mismatch banner — **GAP** (Android uses Play Store updates; could re-add for beta channel).
- Dexie store → `core/ProState.kt` DataStore-backed persistence + `core/ProStore.kt::ProStore` (L166) for SharedPreferences migration.

## E. Patterns worth porting
- **Version mismatch banner** with `Refresh Now`/`Later` is genuinely nice for hot-fix rollout; native app uses Play console, but a "force update" flag from a remote config could mirror this UX.
- The `_cache` block on `user` (age/gender/experience/phase multipliers pre-multiplied) is a clean memoization pattern; `Stats.kt` recomputes these inline each call — porting the cache would cut CPU on hot paths.
- `couplingMatrix` field on `workoutData` (learned per-user muscle coupling) — currently `Fatigue.learnCouplingFromWorkout` lives in `engine/Library.kt` (L515) but isn't persisted to the user record; consider surfacing it.

## F. Feature IDs
`P01-VERSION-BANNER`, `P01-WORKOUT-DATA-MODEL`, `P01-USER-CACHE`, `P01-DEXIE-STORE`, `P01-EXERCISE-ROTATION-SYSTEM`.
