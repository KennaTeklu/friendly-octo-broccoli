# 13-persistence-coach — Critical persistence, exercise history, auto-advance, completion modal

Source: `reference/scripts/app/13-persistence-coach.js` (L67074–69069 of original `app.js`; ~2,000 lines)

## A. File purpose
This module guarantees a logged set survives a crash: it clears the per-exercise draft from localStorage, force-saves the workout to IndexedDB (`saveCurrentWorkoutToStorage`), writes an emergency backup (`workoutEmergencyBackup` in localStorage), updates exercise history (`updateExerciseHistory` handling reps/time prescriptions, RPE arrays, notes, firstRPE, restEstimate, muscle list), auto-advances to the next unfinished exercise, toggles the "Complete Workout" button, and shows the SweetAlert completion modal summarizing logged/skipped counts + total volume + finalizing prompt.

## B. Public surface
- Persistence flow (top of file, IIFE-adjacent):
  - Clears `draft_${currentWorkout.id}_${exercise.id}` from localStorage.
  - Calls `saveCurrentWorkoutToStorage()` (bypasses debounce) — IndexedDB write.
  - Writes `workoutEmergencyBackup` = `{workout, timestamp, version:2, lastSavedExercise}`.
  - Calls `showNotification('… logged successfully!', 'success')`.
- `findNextUnfinished(startIndex)` — scans forward then wraps to find the next non-skipped, non-logged exercise; returns -1 if all done. Scrolls card into view.
- `updateCompleteWorkoutButton()` — shows `#workoutButtons` only when every exercise is `actual || skipped`.
- Completion modal (delayed 500ms) — SweetAlert with logged/skipped counts, total volume (handles both reps×weight and time-based durations), "Complete Workout" CTA calling `completeWorkout()`.
- `updateExerciseHistory(exercise)` — pushes `{date, weight, sets, rpe, notes, prescriptionType, muscles, firstRPE, restEstimate, durations?, totalTime?, volume?}` onto `workoutData.exercises[exercise.id].history[]`. For `prescriptionType: 'time'`, `volume = total seconds under tension`.

## C. Dependencies
- Globals: `localStorage`, `currentWorkout`, `workoutData`, `document`, `Swal`, `setTimeout`, `formatNumber`.
- Cross-module: `saveCurrentWorkoutToStorage`, `saveWorkoutData`, `showNotification`, `completeWorkout`.

## D. Native-equivalent mapping
- Critical persistence + emergency backup → `core/ProState.kt::saveWorkoutDraftImmediately` (L237) + `performSave` (L256) + `readEmergencyBackup` (L292) + `clearEmergencyBackup` (L302) — ported. Native uses DataStore + `workoutEmergencyBackup` SharedPreferences.
- Draft key `draft_${workoutId}_${exerciseId}` → `ProfileState` per-exercise prefs keys (similar pattern).
- `findNextUnfinished` → `engine/Sessions.kt::findNextUnfinished` (L1191) — ported, used by auto-advance.
- `updateCompleteWorkoutButton` → `WorkoutLogger.kt` button-state Compose — ported via `unloggedExercises` (Sessions.kt L377) and `allLoggedLines` (L481).
- Completion modal → `WorkoutLogger.kt::CelebrationDialog` (L424) + `Confetti.kt` — ported with richer celebration. Native `Sessions.completeWorkout` (L397) returns `CompletionResult` consumed by the dialog.
- `updateExerciseHistory` → `engine/Sessions.kt::updateExerciseHistory` (L234) — ported; native also tracks `prescriptionType` branch and stores `durations` for time-based exercises.

## E. Patterns worth porting
- **Triple-write redundancy**: localStorage draft cleared, IndexedDB force-save, localStorage emergency backup. Native relies on DataStore atomic writes — generally safe, but the emergency-backup JSON in SharedPreferences is a genuinely belt-and-suspenders disaster-recovery pattern worth keeping (one Android equivalent is a separate "crash recovery" prefs file).
- **Auto-advance to next unfinished exercise** (forward scan, then wrap) — smooth UX; verify `WorkoutLogger.kt` auto-scrolls on log.
- **`firstRPE` and `restEstimate` stored per history entry** — these enable RPE-trend analysis and rest-time analytics over time. Native `HistoryEntry` (Model.kt L311) doesn't carry `firstRPE`/`restEstimate`; porting them unlocks richer recovery analytics.
- **`prescriptionType: 'time'` volume = total seconds under tension** — clean way to unify time-based and rep-based volume. Native `calculateVolume` (Sessions.kt L697) branches similarly but doesn't unify into a single `volume` field.

## F. Feature IDs
`P13-FORCE-SAVE`, `P13-EMERGENCY-BACKUP`, `P13-AUTO-ADVANCE`, `P13-COMPLETE-WORKOUT-MODAL`, `P13-EXERCISE-HISTORY`, `P13-TIME-BASED-VOLUME`, `P13-FIRST-RPE-REST-ESTIMATE`.
