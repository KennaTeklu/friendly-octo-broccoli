# 01 — Config & Data (workoutData, Dexie, MUSCLE_DECAY, COUPLING_MATRIX)

> Reference module: `reference/scripts/app/01-config-data.js` (492 lines).
> Native counterparts: `core/Model.kt`, `engine/Library.kt`, `engine/Stats.kt`, `engine/Generator.kt`.

This module owns the global `workoutData` object, the Dexie IndexedDB schema,
the static MUSCLE_DECAY table, the static COUPLING_MATRIX, the version banner,
and the `loadAllData` boot sequence.

---

## E2. Deep patterns

### 2.1 Version banner — IIFE on script load
- **Inputs**: `localStorage['workout_app_version']`, `APP_VERSION = '2.0.0'`.
- **Computation**: `if storedVersion && storedVersion !== APP_VERSION` → inject a fixed red `<div>` at `position:fixed; top:0` with two buttons ("Refresh Now" / "Later"). If `storedVersion` is null → write `APP_VERSION` silently.
- **Output**: DOM banner or silent version write. `try/catch` swallows all errors so boot never fails on localStorage being unavailable.
- **Reasoning**: stale clients must not run stale data migrations silently.

### 2.2 `workoutData.user` default schema
- **Inputs**: none (constant default object).
- **Computation**: assigns `name=""`, `birthDate=null`, `gender="male"`, `weight=null`, `height=null`, `experience="intermediate"`, `goal="balanced"`, `created=new Date().toISOString()`, `settings={workoutDays:["monday","wednesday","friday"], restTime:90, progressionRate:0.02, theme:"blue", darkMode:false, muscleCoupling:false}`, `agingRisks=[]`, `menstrual={lastPeriodStart:null, cycleLength:28, symptoms:[]}`, `momentumBias={value:0.0, decayRate:0.25, lastUpdated:null, lastTap:null}`, `_cache={…}` with seven pre-computed multiplier slots.
- **Output**: in-memory `user` shape — every downstream module assumes these keys exist.
- **Reasoning**: keeps the cache explicit so demographic math can be memoised per session.

### 2.3 Dexie schema (`db.version(1).stores`)
- **Inputs**: `new Dexie('WorkoutApp')`.
- **Computation**: declares four object stores: `workouts:'id, date'`, `exercises:'id'`, `user:'key'`, `savedWorkout:'id'`.
- **Output**: indexed tables — `workouts` indexed by date for chronological queries, `user` keyed by `key` so multiple profiles could later coexist.
- **Reasoning**: minimal indexed set; everything else is derived in JS.

### 2.4 `workoutData.user._cache` schema
- **Inputs**: per-session demographic factors.
- **Computation**: pre-allocates 7 slots — `effectiveBodyWeight`, `ageFactor (0.9|1.0|0.8)`, `genderFactor (0.9|1.0)`, `experienceFactor (0.8|1.0|1.2)`, `expDeltaFactor (1.3|1.0|0.7)`, `phaseFactor`, `priorBaseMultiplier`, `progBaseMultiplier`.
- **Output**: cache object filled later by `updateUserCache()` (module 06).
- **Reasoning**: the demographic multipliers are used by every prescription call; caching avoids recomputing age/gender/experience per set.

### 2.5 MUSCLE_DECAY table
- **Inputs**: hard-coded per-muscle bi-exponential constants.
- **Computation**: 13 entries — e.g. `quads:{fast:0.10, slow:0.012}`, `chest:{fast:0.12, slow:0.008}`, `biceps:{fast:0.13, slow:0.007}`, `calves:{fast:0.06, slow:0.015}`.
- **Output**: lookup table consumed by `applyFatigueDecay` (module 15) and `getCoupledFatigue` here.
- **Reasoning**: large muscles (quads, glutes) recover slowly (`slow≈0.011`); small muscles (biceps, forearms) recover quickly (`slow≈0.007`); calves have the slowest fast decay (`0.06`) reflecting their unique oxidative profile.
- **Default fallback**: `DEFAULT_DECAY = { fast:0.10, slow:0.010 }` for any muscle not in the table.

### 2.6 COUPLING_MATRIX (sparse, fatigue-transfer coefficients)
- **Inputs**: hard-coded adjacent-muscle coefficients.
- **Computation**: 7 source muscles with sparse neighbour maps — `quads→glutes:0.05, hamstrings:0.02`; `glutes→quads:0.05, hamstrings:0.03`; `hamstrings→glutes:0.03, quads:0.02`; `chest→shoulders:0.04, triceps:0.02`; `shoulders→chest:0.04, triceps:0.03`; `back→lats:0.05, rear_delts:0.02`; `lats→back:0.05, biceps:0.02`.
- **Output**: sparse graph used by `getCoupledFatigue(muscle)`.
- **Reasoning**: compound lifts leak fatigue into synergists; 2-5% transfer per edge models the systemic bleed without double-counting.

### 2.7 `getCoupledFatigue(muscle)`
- **Inputs**: `workoutData.muscleFatigue[muscle]`, optional learned `workoutData.couplingMatrix[muscle]`, fallback `COUPLING_MATRIX[muscle]`, flag `workoutData.user.settings.muscleCoupling`.
- **Computation**: `total = base.fast + base.slow`; if coupling disabled → return `total`; else iterate edges and add `coeff * (src.fast + src.slow)`; clamp `Math.min(1, total)`.
- **Output**: float in `[0, 1]` representing effective fatigue.
- **Reasoning**: lets users opt out of coupling if they train antagonist splits.

### 2.8 `loadAllData()` boot sequence
- **Inputs**: Dexie tables.
- **Computation**: (a) load workouts array; (b) load exercises, convert array → object keyed by `id`; (c) load user record (`main`) or fall back to `workoutData.user` defaults; (d) load `savedWorkout` (current); (e) populate global `workoutData`; (f) rebuild derived state: `rebuildFatigueFromHistory()`, `calculateMuscleLastTrained()`, `backfillLastPerformed()`, `recomputeAggregates()`, `updateBodyWeightEstimate()`; (g) if saved workout has exercises → `requestWakeLock()`; else `generateNextWorkout()`.
- **Output**: hydrated in-memory state; either resumes a workout or generates a fresh one.
- **Reasoning**: every derived index is recomputed on load — never trusted from storage.

### 2.9 `checkVersionAndWarn()` (1 s delay)
- **Inputs**: localStorage version key.
- **Computation**: `setTimeout(checkVersionAndWarn, 1000)` then re-checks and prepends a banner if mismatch.
- **Output**: banner appears after the first second so it does not race with first paint.
- **Reasoning**: defensive duplicate of the IIFE — covers cases where the IIFE was bypassed by a sandbox.

---

## E3. Edge cases handled

- **First visit / empty localStorage** (`storedVersion` falsy): silently write `APP_VERSION`; no banner.
- **localStorage throws** (private mode, quota): outer `try/catch` swallows, app continues.
- **Dexie user record missing**: `user = userRecord ? userRecord.value : { ...workoutData.user }` — fresh defaults.
- **savedWorkout missing**: `savedWorkout = savedRecord ? savedRecord.value : null` — leads to `generateNextWorkout()`.
- **Saved workout present but empty (`exercises.length === 0`)**: does not resume; falls through to `generateNextWorkout()`.
- **Muscle not in MUSCLE_DECAY**: `DEFAULT_DECAY = {fast:0.10, slow:0.010}` is used implicitly by `applyFatigueDecay`.
- **Muscle not in COUPLING_MATRIX**: `couplings` is undefined → coupling branch skipped → returns raw `total`.
- **Coupling disabled** (`muscleCoupling:false`): short-circuits, returns `total` early.
- **Coupled fatigue exceeds 1.0**: `Math.min(1, total)` clamp.
- **`muscleFatigue[muscle]` missing**: returns `0` immediately.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Banner background | `#ff4444` (red) | Eye-catching for a "must refresh" prompt |
| Banner z-index | `10000` | Above any modal (Swal default ~9999) |
| Banner buttons | "Refresh Now" (white on red) + "Later" (transparent w/ white border) | Clear primary action; secondary does not look default |
| Default workout days | Mon/Wed/Fri | Universal PPL-friendly starter cadence |
| Default rest time | 90 s | Moderate between powerlifting (3-5 min) and hypertrophy (60 s) |
| Default progression rate | 0.02 (2%) | Conservative linear progression — 1 lb per 50 lbs lifted |
| Default theme | `blue` | Brand-neutral, accessibility-tested |
| Default dark mode | `false` | System preference is read elsewhere; explicit false avoids surprise |
| Default `muscleCoupling` | `false` | Off until user opts in — simpler mental model |
| Default cycle length | 28 days | Population median |
| Default `momentumBias.value` | `0.0` | Neutral; user must steer explicitly |
| Default `momentumBias.decayRate` | `0.25` | Bias halves every ~2.5 generations |
| `checkVersionAndWarn` delay | `1000 ms` | Lets first paint settle before banner |
| Banner padding | `12 px` | Touch-tappable |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `workoutData.user` default schema (16 fields + settings + cache) | FULL | — |
| Dexie schema (4 stores, indexed by date) | FULL (Room/ProStore equivalents) | — |
| MUSCLE_DECAY table (13 muscles, fast+slow) | FULL (`assets/data/muscle_decay.json` + `engine/Library.kt`) | — |
| COUPLING_MATRIX sparse graph | PARTIAL (`muscle_decay.json` + `coupling_matrix.json` loaded; `getCoupledFatigue` present in `Generator.kt`/`Stats.kt`) | Learned `couplingMatrix` runtime update path not surfaced in Kotlin |
| `getCoupledFatigue` with clamp + coupling toggle | FULL | — |
| `loadAllData` boot sequence (rebuild derived state) | FULL (`ProState.kt` + `Sessions.kt`) | — |
| `requestWakeLock()` on resume | PARTIAL | Android `WakeLock` acquired via `MainActivity.kt` but not gated on savedWorkout resumption specifically |
| Version banner (IIFE + 1 s `setTimeout`) | MISSING | Native uses in-app update flow instead |
| `_cache` pre-allocation (7 multiplier slots) | FULL (`ProfileState.kt` exposes cached factors) | — |
| `DEFAULT_DECAY` fallback constant | FULL | — |

**Coverage: 70%** (7 FULL / 2 PARTIAL / 1 MISSING of 10 behaviours)

---

## H. Top 3 gaps to close

1. **Version banner UX** — Native relies on Play Store updates; no in-app "new version, refresh" banner. Either add a remote-flags check or accept Play-Store-only updates as the canonical path.
2. **Learned coupling matrix runtime updates** — `workoutData.couplingMatrix` is meant to be learned over time from co-fatigue patterns. Native loads the static JSON but never writes back. Implement a learning pass that updates coefficients when two muscles repeatedly spike together.
3. **WakeLock trigger on savedWorkout resume** — Native acquires wakelock on app foreground, not specifically on workout resume. Tie wakelock acquisition to "active workout has unsaved progress" so backgrounded sessions don't burn battery when nothing is in flight.
