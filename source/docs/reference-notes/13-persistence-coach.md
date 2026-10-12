# 13 — Critical Persistence + P4 Coach Mathematics

> Reference module: `reference/scripts/app/13-persistence-coach.js` (2 003 lines).
> Native counterparts: `engine/Sessions.kt`, `core/ProState.kt`, `core/ProfileState.kt`, `core/MigrationBridge.kt`.

This module owns the **critical persistence layer**: draft clearing, emergency localStorage backup, IndexedDB immediate-save (bypass debounce), auto-advance to next unfinished exercise, all-logged detection, complete-workout prompt, and exercise history record update (with best-weight tracking).

---

## E2. Deep patterns

### 2.1 Draft clearing + immediate save
- **Inputs**: `currentWorkout.id`, `exercise.id`.
- **Computation**:
  1. Build draft key `draft_${currentWorkout.id}_${exercise.id}`.
  2. `localStorage.removeItem(draftKey)` — clears per-exercise draft.
  3. Call `saveCurrentWorkoutToStorage()` if defined (bypasses debounce); else `saveWorkoutData()`.
  4. **Emergency backup**: write `localStorage['workoutEmergencyBackup'] = JSON.stringify({workout, timestamp, version:2, lastSavedExercise: exercise.id})`.
- **Output**: persisted workout + emergency backup.
- **Reasoning**: log-a-set is the most critical write — never lose it to a crash; emergency backup is disaster recovery.

### 2.2 `findNextUnfinished(startIndex)` — auto-advance
- **Inputs**: `currentWorkout.exercises`, start index.
- **Computation**: scan forward from `startIndex+1` to end; if no unfinished found, wrap from 0 to `startIndex-1`; return first index where `!actual && !skipped`; return -1 if all done.
- **Output**: next index or -1.
- **Reasoning**: forward-then-wrap ensures sequential flow but recovers from skipped exercises.

### 2.3 Auto-advance scroll-into-view
- **Inputs**: `nextIndex`, exercise card element.
- **Computation**: `nextCard.scrollIntoView({behavior:'smooth', block:'center'})`.
- **Output**: smooth scroll to next exercise.
- **Reasoning**: keeps the user in flow — no manual scrolling between sets.

### 2.4 `updateCompleteWorkoutButton()` — visibility gate
- **Inputs**: `currentWorkout.exercises`, all exercises' `actual`/`skipped` flags.
- **Computation**: if `currentWorkout` missing or empty → hide button; else `allDone = every(ex => ex.actual || ex.skipped)`; show button if `allDone`, hide otherwise.
- **Output**: button visibility.
- **Reasoning**: complete-workout button only appears when there's nothing left to log.

### 2.5 All-logged prompt flow
- **Inputs**: `currentWorkout.exercises`, 500ms delay.
- **Computation**:
  1. Check `allLogged = every(ex => ex.actual || ex.skipped)`.
  2. If true → `setTimeout(500ms)` → show SweetAlert:
     - Title: "🎉 All exercises logged!"
     - HTML: stats list — logged count, skipped count (if >0), total volume (formatted with `formatNumber`).
     - Confirm: "Complete Workout 🏆" (green #10b981); Cancel: "Later" (gray #6c757d).
  3. On confirm → `completeWorkout()`.
- **Output**: completion prompt.
- **Reasoning**: 500ms delay lets the last log animation finish before prompt; green confirm reinforces positive completion.

### 2.6 Volume calculation for prompt
- **Inputs**: `currentWorkout.exercises`.
- **Computation**: for each exercise with `actual && !skipped`:
  - If `actual.reps && actual.reps.length` → `totalVolume += actual.weight * actual.reps.reduce((a,b)=>a+b, 0)`.
  - Else if `actual.durations && actual.durations.length` → `totalVolume += actual.durations.reduce((a,b)=>a+b, 0)` (time-based volume = total seconds).
- **Output**: total volume number.
- **Reasoning**: time-based exercises (plank) contribute seconds, not weight×reps.

### 2.7 `updateExerciseHistory(exercise)` — history record
- **Inputs**: exercise with `actual`, `prescriptionType`.
- **Computation**:
  1. Ensure `workoutData.exercises[exercise.id]` exists with `{history:[]}`.
  2. Build `historyEntry = {date: ISO now, weight, sets, rpe, notes, prescriptionType, muscles:[...muscleGroup], firstRPE, restEstimate}`.
  3. If `prescriptionType === 'time'`:
     - `historyEntry.durations = actual.durations` (array of seconds).
     - `historyEntry.totalTime = durations.reduce((a,b)=>a+b, 0)`.
     - `historyEntry.volume = totalTime` (seconds under tension).
  4. Else (reps/amrap):
     - `historyEntry.reps = actual.reps`.
     - `totalReps = actual.reps.reduce((a,b)=>a+b, 0)`.
     - `historyEntry.volume = actual.weight * totalReps`.
     - **Best-weight tracking**: if `actual.weight > exData.bestWeight` → update `bestWeight`, `bestReps = max(reps)`; elif `actual.weight === bestWeight` → update `bestReps` if higher.
  5. Push `historyEntry` to `exData.history`.
- **Output**: updated exercise history record.
- **Reasoning**: best-weight/best-reps tracking lets users see PRs without parsing history.

### 2.8 Best-weight comparison logic
- **Inputs**: `actual.weight`, `exData.bestWeight` (default 0).
- **Computation**:
  - If `actual.weight > bestWeight || bestWeight === undefined` → new PR; update both `bestWeight` and `bestReps`.
  - Elif `actual.weight === bestWeight` → check reps; if `max(reps) > bestReps` → update `bestReps`.
- **Output**: best fields updated.
- **Reasoning**: same-weight-more-reps is also a PR (rep PR); only weight PR is strictly higher.

### 2.9 Time-based volume tracking
- **Inputs**: `actual.durations` (array of seconds).
- **Computation**: `totalTime = durations.reduce((a,b)=>a+b, 0)`; `volume = totalTime`.
- **Output**: time volume.
- **Reasoning**: time under tension is the load metric for isometric/time-based work.

### 2.10 `restEstimate` field
- **Inputs**: optional per-set rest time.
- **Computation**: stored if present (computed elsewhere); null otherwise.
- **Output**: rest estimate on history entry.
- **Reasoning**: tracks actual rest vs prescribed rest for future prescription tuning.

---

## E3. Edge cases handled

- **`currentWorkout` null**: `updateCompleteWorkoutButton` returns early.
- **`currentWorkout.exercises` empty**: button hidden.
- **Exercise with no `actual` and not `skipped`**: not "done"; `allLogged` is false.
- **All exercises skipped**: `allLogged` is true (skipped counts as "handled"); prompt still shows.
- **`actual.reps` empty array**: `totalReps = 0`; volume = 0.
- **`actual.durations` empty array**: `totalTime = 0`; volume = 0.
- **`actual.weight = 0`** (bodyweight only): volume = 0; best-weight check `0 > 0` is false, no PR.
- **`exData.bestWeight` undefined**: treated as 0; first log sets it.
- **`exercise.id` not in `workoutData.exercises`**: creates `{history:[]}`.
- **`exercise.muscleGroup` empty/missing**: `muscles: []` on history entry.
- **`actual.firstRPE` undefined**: stored as null.
- **`actual.restEstimate` undefined**: stored as null.
- **SweetAlert `result.isConfirmed` false**: prompt dismissed; workout not completed.
- **`saveCurrentWorkoutToStorage` not defined**: falls back to `saveWorkoutData`.
- **`workoutEmergencyBackup` localStorage quota exceeded**: try/catch in `saveCurrentWorkoutToStorage` swallows.
- **`nextIndex === -1`** (all done): no scroll; complete button visible.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Draft clear timing | immediate on log | No stale drafts after successful log |
| IndexedDB save mode | bypass debounce | Critical write — must hit disk now |
| Emergency backup | localStorage (separate from Dexie) | Double-redundancy in case IndexedDB corrupts |
| Emergency backup version | 2 | Schema migration marker |
| Auto-advance scan | forward then wrap | Sequential flow + skip recovery |
| Scroll behavior | smooth, center | User keeps context |
| Complete button visibility | only when allDone | Prevents accidental partial completion |
| All-logged prompt delay | 500 ms | Lets last log animation settle |
| Prompt title | "🎉 All exercises logged!" | Celebratory |
| Confirm button color | #10b981 (green) | Positive reinforcement |
| Confirm text | "Complete Workout 🏆" | Trophy emoji = achievement |
| Cancel text | "Later" | Non-judgmental |
| Volume display | `formatNumber(totalVolume) lbs` | Comma-separated thousands |
| Skipped count shown | only if > 0 | Don't shame zero-skip workouts |
| Best-weight update | strictly > | Same weight isn't a weight PR |
| Best-reps at same weight | > | Rep PR is valid |
| Time volume unit | seconds | Simple, unambiguous |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| Draft clearing on log | FULL (`Sessions.kt`) | — |
| Immediate IndexedDB save (bypass debounce) | FULL | — |
| Emergency localStorage backup | PARTIAL | `Backup.kt` writes periodic snapshots, not per-log emergency backup |
| Auto-advance to next unfinished | FULL | — |
| Forward-then-wrap scan order | FULL | — |
| Scroll-into-view smooth center | FULL (Compose scroll) | — |
| `updateCompleteWorkoutButton` visibility gate | FULL | — |
| All-logged prompt (500ms delay, stats) | FULL (`Sessions.kt` ALL_LOGGED_*) | — |
| Volume calculation (reps + time) | FULL | — |
| `updateExerciseHistory` full record | FULL | — |
| Best-weight/best-reps tracking | FULL | — |
| Same-weight rep PR | FULL | — |
| Time-based volume = seconds | FULL | — |
| `firstRPE` capture on history | FULL | — |
| `restEstimate` capture | PARTIAL | Field exists but rest timer not wired in UI |
| `muscles` copy on history entry | FULL | — |
| `prescriptionType` stored on history | FULL | — |

**Coverage: 88%** (15 FULL / 2 PARTIAL / 0 MISSING of 17 behaviours)

---

## H. Top 3 gaps to close

1. **No per-log emergency backup** — native `Backup.kt` writes periodic snapshots (every 5 workouts) but not a per-log emergency backup. Add a per-log write to a separate `emergency_workout.json` in app-private storage so a crash mid-session never loses the last set.
2. **Rest estimate not captured** — the `restEstimate` field exists on history but the rest timer UI isn't wired to populate it. Add a rest timer that auto-starts after each set log and writes the elapsed time to `actual.restEstimate`.
3. **Best-weight PR not surfaced** — the `bestWeight`/`bestReps` fields are updated but no notification fires. Show a "🏆 New PR!" toast when a weight or rep PR is hit.
