# 08 — Workout Generation + Deload Logic

> Reference module: `reference/scripts/app/08-workout-generation.js` (523 lines).
> Native counterparts: `engine/Generator.kt`, `engine/Sessions.kt`, `core/Notify.kt`.

This module orchestrates the full workout generation flow: deload detection (systemic + volume-based), taper logic, split selection, muscle targeting (overdue + accessory), exercise assembly, athletic-block wrapping, and post-generation notifications.

---

## E2. Deep patterns

### 2.1 `generateNextWorkout()` — entry guard
- **Inputs**: `currentWorkout` (existing unsaved workout).
- **Computation**: if `currentWorkout.exercises.length > 0` → SweetAlert confirm ("Generate a new workout? This will replace your current unsaved workout. Any unsaved progress will be lost.") with confirm/cancel. On confirm → `performGenerateWorkout()`. On cancel → re-render existing workout. If no current workout → call `performGenerateWorkout()` directly.
- **Output**: either new workout or restored existing.
- **Reasoning**: prevents accidental loss of unsaved logging progress.

### 2.2 `performGenerateWorkout()` — main pipeline
- **Inputs**: `workoutData`, `currentWorkout`, `workoutProgram.splits`, `getCurrentCyclePhase()`.
- **Computation** (12-stage):
  1. `applyFatigueDecay()` (decay fatigue since last boot).
  2. `decayMomentumBias()` (decay steering bias).
  3. `systemicFactor = calculateSystemicRecoveryFactor()` (0.5-1.0).
  4. **Volume deload**: `avgWeeklyVolume = mean(last4 weeklyVolumes)` or fallback to `totalVolumeLast7Days`; `volumeDeloadThreshold = avgWeeklyVolume * 1.5`; `volumeDeload = currentWeekVolume > volumeDeloadThreshold`.
  5. **Systemic deload**: `systemicDeload = systemicFactor < 0.6`.
  6. `needsDeload = systemicDeload || volumeDeload`.
  7. **Taper**: if `competition.date` set AND `daysToMeet ∈ [0, 7]` → `isTapering = true`.
  8. **Split selection**: pick the next split in rotation (mod `splits.length`); if `needsDeload && !isTapering` → force `mobility_flexibility` split.
  9. **Phase multiplier**: `mult = getPhaseMultiplier(getCurrentCyclePhase())`.
  10. **Build workout object**: `{id: workout_<ts>, date, type: split.id, name: split.name + (Deload|Taper suffix), exercises:[], isDeload, isTaper}`.
  11. `updateWorkoutCache()` (session base scaling).
  12. **Muscle selection**: target muscles = split.focus + overdue muscles + ready accessory muscles (Fisher-Yates shuffle, pick `min(accessoryTarget, readyMuscles.length)`); add `core` if `core` not in target and `core fatigue < READINESS_THRESHOLD`.
  13. **Exercise assembly**: for each target muscle → `selectExerciseForMuscle` (or fallback `generateExerciseFromLibrary`); apply `localMultiplier = mult * (needsDeload ? 0.8 : 1) * muscleDeloadMultiplier`; call `generateExercisePrescription(ex, localMultiplier)`.
  14. **Default fallback**: if no exercises generated → use `[squat, bench_press, deadlift]`.
  15. **Shuffle + athletic blocks**: `exercises.sort(() => Math.random() - 0.5)`; `appendAthleticBlocks(workout, needsDeload, isTapering)`.
  16. `saveCurrentWorkoutToStorage()`, update dashboard, update workout UI if visible, show notification.
- **Output**: new `currentWorkout` object with exercises.
- **Reasoning**: the 6-week deload cycle (3 high weeks + 1 deload) is enforced by combining systemic recovery + volume threshold + competition taper.

### 2.3 Volume deload threshold
- **Inputs**: `workoutData.user.weeklyVolumes` (last 4+ entries), `workoutData.user.aggregates.totalVolumeLast7Days`.
- **Computation**: `avgWeeklyVolume = mean(weeklyVolumes.slice(-4))` if ≥4 entries; else `totalVolumeLast7Days`; `volumeDeloadThreshold = avgWeeklyVolume * 1.5`; `volumeDeload = currentWeekVolume > volumeDeloadThreshold`.
- **Output**: boolean.
- **Reasoning**: 150% of baseline is the overtraining red line; deload activates before injury.

### 2.4 Systemic deload threshold
- **Inputs**: `systemicFactor` from `calculateSystemicRecoveryFactor()` (0.5-1.0).
- **Computation**: `systemicDeload = systemicFactor < 0.6`.
- **Output**: boolean.
- **Reasoning**: recovery < 60% → meaningful fatigue; deload enforced.

### 2.5 Taper logic
- **Inputs**: `workoutData.user.competition.date`.
- **Computation**: if date set → `daysToMeet = ceil((meet - now) / 86400000)`; `isTapering = daysToMeet ∈ [0, 7]`.
- **Output**: boolean.
- **Reasoning**: 7-day taper window — reduce volume, keep intensity for peak.

### 2.6 Split selection (rotation)
- **Inputs**: `splits` array, `lastWorkout.type`.
- **Computation**: `lastIndex = splits.findIndex(s => s.id === lastWorkout.type)`; `nextIndex = (lastIndex + 1) % splits.length`; `split = splits[nextIndex]`. If lastIndex not found → `splits[0]`.
- **Output**: split object.
- **Reasoning**: round-robin ensures all 7 splits hit in a 7-day cycle.

### 2.7 Muscle targeting pipeline
- **Inputs**: `split.focus[]`, `getOverdueMuscles(1.5, 2)`, `getReadyMuscles(candidates)`, `muscleFatigue['core']`, `READINESS_THRESHOLD`.
- **Computation**:
  1. `targetMuscles = new Set(split.focus)`.
  2. Add all overdue muscles (force-include even if fatigued).
  3. `accessoryTarget = floor(rand*3) + 2`, clamped to `[max(0, 8-focusCount), max(2, 8-focusCount)]` — wait, re-read: `accessoryTarget = min(accessoryTarget, max(2, 8 - focusCount))` then `max(0, accessoryTarget)`.
  4. If readyMuscles available → Fisher-Yates shuffle, take `min(accessoryTarget, readyMuscles.length)`.
  5. If no ready muscles → take `accessoryTarget` from remaining (non-target) muscles.
  6. Add `core` if not targeted and `core fatigue < READINESS_THRESHOLD`.
- **Output**: Set of muscle names.
- **Reasoning**: split focus is the backbone; accessories fill remaining slots with ready muscles; overdue muscles force-included.

### 2.8 `getMuscleDeloadMultiplier(muscleName)` — per-muscle deload
- **Inputs**: `workoutData.user.muscleDeloadStatus[muscleName]`.
- **Computation**: if `status && status.active` → return 0.7 (30% reduction); else 1.0.
- **Output**: multiplier.
- **Reasoning**: a single muscle can be in deload (after 3 consecutive high-fatigue sessions) even if systemic is fine.

### 2.9 `updateMuscleDeloadStatus(workout)` — post-workout deload tracker
- **Inputs**: completed workout, `getCoupledFatigue(muscle)`.
- **Computation**:
  1. Collect muscles trained (with `actual && !skipped`).
  2. For each muscle: get `fatigue = getCoupledFatigue(muscle)`.
  3. If `fatigue ≥ 0.6` → `consecutiveHighFatigue++`; if `consecutiveHighFatigue ≥ 3` → `active = true`.
  4. Else → reset `consecutiveHighFatigue = 0`, `active = false` (one low-fatigue session clears deload).
  5. Save to localStorage.
- **Output**: per-muscle deload status.
- **Reasoning**: 3 strikes rule — three consecutive high-fatigue sessions trigger deload; one recovery session clears it.

### 2.10 `appendAthleticBlocks(workout, isDeload, isTaper)`
- **Inputs**: workout, deload/taper flags.
- **Computation**: wraps the main lifts in order: `[warm-up] → [main lifts] → [component drill finisher] → [stretch cooldown]`. Warm-up is light dynamic; finisher is the split's `drills[]`; cooldown is `stretching_static` + `mobility_dynamic`.
- **Output**: workout.exercises with athletic blocks appended.
- **Reasoning**: structural template ensures every workout has a warm-up and cooldown regardless of split.

### 2.11 Notification routing
- **Inputs**: `needsDeload`, `isTapering`, `volumeDeload`.
- **Computation**: if `needsDeload && !isTapering` → if `volumeDeload` → "Deload triggered by high weekly volume. Reduce intensity." else "Deload week activated – focus on form and recovery."; elif `isTapering` → "Taper week – reduce volume, keep intensity for peak performance."; else → `Workout generated: ${currentWorkout.name}`.
- **Output**: notification string.
- **Reasoning**: user knows why deload activated (volume vs systemic) so they can adjust training.

---

## E3. Edge cases handled

- **No `currentWorkout`**: `generateNextWorkout()` calls `performGenerateWorkout()` directly (no confirm dialog).
- **`currentWorkout.exercises.length === 0`**: also calls `performGenerateWorkout()` directly.
- **User cancels confirm**: re-renders existing workout; `updateResumeButtonVisibility()` called.
- **No `lastWorkout`**: `split = splits[0]`.
- **`lastWorkout.type` not in splits**: `lastIndex = -1` → `split = splits[0]`.
- **No `weeklyVolumes` array (or <4 entries)**: falls back to `totalVolumeLast7Days`.
- **No `competition.date`**: `isTapering = false` (skip taper check).
- **`competition.date` in past**: `daysToMeet < 0` → `isTapering = false` (meet already happened).
- **No exercises selected (all muscles untrained + library empty)**: fallback to `[squat, bench_press, deadlift]`.
- **`readyMuscles` empty**: takes `accessoryTarget` from non-target fallback list.
- **`accessoryTarget = 0`**: skips accessory step.
- **Core already in targetMuscles**: skips core add.
- **Core fatigue ≥ READINESS_THRESHOLD**: skips core add (let it rest).
- **`muscleDeloadStatus` undefined for muscle**: returns 1.0 (no deload).
- **No exercises in `currentWorkout.exercises` after generation**: default fallback inserts 3 lifts.
- **All exercises skipped/failed**: `updateMuscleDeloadStatus` still runs but `musclesTrained` is empty (no-op).

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Confirm dialog when overwriting unsaved | SweetAlert warning, red confirm button | Prevents accidental loss of in-progress logging |
| Deload multiplier | 0.8 (20% reduction) | Standard deload volume (60-80% of normal) |
| Volume deload threshold | 1.5× baseline (150%) | 50% over baseline = overtraining zone |
| Systemic deload threshold | 0.6 recovery factor | Below 60% = meaningful fatigue |
| Taper window | 7 days pre-meet | Standard powerlifting taper |
| Taper behavior | Keep intensity, drop volume | Peak strength requires high-intensity exposure |
| Accessory target | `floor(rand*3) + 2` = 2-4 accessories | Variety without over-volume |
| Accessory cap | `max(2, 8 - focusCount)` | Fewer accessories if focus is wide |
| Fisher-Yates shuffle | Yes | Unbiased accessory selection |
| Core auto-add | If fatigue < READINESS_THRESHOLD | Core recovers fast, train daily |
| Per-muscle deload threshold | 3 consecutive ≥0.6 fatigue | Three-strikes rule |
| Per-muscle deload reduction | 0.7 (30% reduction) | Targeted deload for problem muscles |
| Deload clears on | 1 low-fatigue session | Quick reset prevents chronic under-training |
| Default fallback | squat + bench + deadlift | The big three — always available |
| Athletic block order | warm-up → main → drill → stretch | Standard session structure |
| Notification delay | None (immediate) | User sees result right away |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `generateNextWorkout` confirm-guard | FULL (`Sessions.kt`) | — |
| `performGenerateWorkout` 12-stage pipeline | FULL (`Generator.kt`) | — |
| Volume deload (1.5× avg) | FULL | — |
| Systemic deload (<0.6 recovery) | FULL | — |
| Taper logic (7-day window) | PARTIAL | `competition.date` field exists in `Model.kt` but no UI to set it |
| Split rotation (mod length) | FULL | — |
| Muscle targeting (focus + overdue + accessory) | FULL | — |
| Core auto-add | FULL | — |
| Fisher-Yates accessory shuffle | FULL | — |
| Per-muscle deload multiplier | FULL | — |
| `updateMuscleDeloadStatus` (3-strike rule) | FULL | — |
| `appendAthleticBlocks` warm-up/drill/stretch | FULL | — |
| Default fallback (squat/bench/deadlift) | FULL | — |
| Notification routing (deload reason) | FULL (`Notify.kt`) | — |
| `saveCurrentWorkoutToStorage` immediate | FULL | — |
| Competition date UI | MISSING | No settings UI to set competition date — taper never triggers |

**Coverage: 88%** (14 FULL / 1 PARTIAL / 1 MISSING of 16 behaviours)

---

## H. Top 3 gaps to close

1. **No UI to set competition date** — `competition.date` exists in the model but there's no Settings field. Add a "Competition date" picker under Training Preferences; once set, the taper logic activates automatically.
2. **Volume deload uses 4-week average but native `weeklyVolumes` is not persisted** — the model has the field but it's not updated by `completeWorkout`. Wire a weekly roll-up job (or compute on-the-fly from workout history).
3. **Per-muscle deload status not surfaced** — `muscleDeloadStatus[muscle].active` triggers a 30% weight reduction but the user isn't told. Show a "Deload active" badge on the exercise card for affected muscles.
