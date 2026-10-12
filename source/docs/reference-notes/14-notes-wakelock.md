# 14-notes-wakelock — Notes inline editor, history filtering, charts, screen wake lock

Source: `reference/scripts/app/14-notes-wakelock.js` (L69070–70557 of original `app.js`; ~1,488 lines)

## A. File purpose
This module bundles four concerns: (1) the inline notes textarea with debounced (300ms) auto-save, blur-save, and Enter-to-commit (Shift+Enter preserves newlines); (2) history filter tabs (`all`/`week`/`month`/`year`/`longevity`) and CSV export; (3) progress charts (volume/strength/frequency/RPE) via Chart.js; (4) **screen wake lock** using the native Wake Lock API with an iOS video-fallback that primes a silent looping webm on first user click.

## B. Public surface
- `saveNotesInline(workoutId, notes)` — finds `workout` by id, sets `workout.summary.notes = trimmed`, saves.
- Notes textarea wiring — `input` listener (300ms debounce) + `blur` listener + `keydown` Enter (no shift) commits.
- `filterHistory(filter, event)` — toggles active tab, sets `currentHistoryFilter`, refreshes table.
- `exportHistoryCSV()` — writes `Date, Workout Type, Exercises Completed, Total Volume, Average RPE` rows, Blob+anchor download.
- `updateProgressCharts()` → `updateVolumeChart()`, `updateStrengthChart()`, `updateFrequencyChart()`, `updateRPEChart()` — Chart.js with linear gradient fills, `responsive:true, maintainAspectRatio:false`, last-8-workouts window.
- `currentWakeLock`, `wakeLockVideo`, `setupVideoFallback()`, `primeVideo` (one-shot document click handler), `enableVideoFallbackWakeLock()`, `disableVideoFallbackWakeLock()`, `shouldBeAwake()` — wake lock machinery.
- Wake lock toggle (`#screenWakeLockToggle`).

## C. Dependencies
- Globals: `document`, `window`, `workoutData`, `Chart`, `currentWorkout`, `setTimeout`, `clearTimeout`, `Blob`, `URL`.
- Cross-module: `saveToLocalStorage`, `updateWorkoutHistory`, `showNotification`, `calculateAge` (for charts).

## D. Native-equivalent mapping
- Notes textarea + debounced auto-save → `ui/screens/History.kt::NoteEditDialog` (L634) — ported. Native uses a Compose dialog with explicit save button rather than debounce; the `HistoryRow` (L458) shows the saved note inline.
- `filterHistory` tabs → `History.kt` filter chips (`all`/`week`/`month`/`year`/`longevity`) — ported.
- `exportHistoryCSV` → `History.kt` CSV exporter (L398 `StringBuilder("date,name,type,exercise,weight,sets,reps,rpe,volume,skipped,notes")`) — ported with richer per-exercise rows.
- Progress charts → `ui/screens/Progress.kt` Compose charts + `engine/Stats.kt::dashboardComposite` — ported. Native uses Compose Canvas instead of Chart.js.
- Screen wake lock (`navigator.wakeLock.request('screen')` + iOS video fallback) → **GAP**. Native Android has `PowerManager.WakeLock` (`PARTIAL_WAKE_LOCK`) and `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` — neither is used. Native could trivially add `window.addFlags(FLAG_KEEP_SCREEN_ON)` when `shouldBeAwake()` is true.
- Wake-lock toggle UI (`#screenWakeLockToggle`) → **GAP**. No equivalent in `Settings.kt`.

## E. Patterns worth porting
- **Auto wake-lock when on the Workout screen with an active workout + user toggle** — `shouldBeAwake()` returns `toggleEnabled && hasWorkout && isOnWorkoutPage`. Native could mirror this in `MainActivity` by adding `FLAG_KEEP_SCREEN_ON` to the window while `currentWorkout.exercises.isNotEmpty() && currentRoute == "workout"`. High value, trivial to port.
- **iOS video fallback** (silent looping webm primed on first click) is a Safari workaround; Android doesn't need this, but the lesson "use platform wake lock if available, degrade gracefully" applies.
- **Notes debounced 300ms auto-save + blur + Enter-to-commit** — Compose dialog should mirror (currently explicit save only); users lose notes if they tap away.
- **CSV export with `workout.summary.averageRPE`** — native CSV already richer; legacy is simpler but matches user expectation.

## F. Feature IDs
`P14-NOTES-INLINE-AUTOSAVE`, `P14-HISTORY-FILTER-TABS`, `P14-CSV-EXPORT`, `P14-PROGRESS-CHARTS`, `P14-SCREEN-WAKE-LOCK`, `P14-WAKE-LOCK-TOGGLE`, `P14-SHOULD-BE-AWAKE`.
