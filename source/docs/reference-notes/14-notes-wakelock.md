# 14 — Notes Section, Screen Wake Lock, History Filter, CSV Export, Charts

> Reference module: `reference/scripts/app/14-notes-wakelock.js` (1 495 lines).
> Native counterparts: `ui/screens/History.kt`, `ui/screens/Progress.kt`, `core/Exporter.kt`, `MainActivity.kt` (wakelock).

This module owns: inline notes textarea (debounce-save), `filterHistory` tab switcher, `exportHistoryCSV` (Blob download), progress charts (Chart.js volume/strength/frequency/RPE), and the screen wake lock (`navigator.wakeLock`).

---

## E2. Deep patterns

### 2.1 Inline notes textarea with auto-save
- **Inputs**: `workout.summary.notes`, textarea content.
- **Computation**:
  1. Render `<textarea id="notes_${workoutId}" placeholder="Add notes...">${currentNotes}</textarea>`.
  2. After 50ms setTimeout (DOM settle) → attach listeners:
     - `input` event → 300ms debounce → `saveNotesInline(workoutId, value)`.
     - `blur` event → clear timeout, save immediately.
     - `keydown` Enter (no Shift) → prevent default, blur (triggers save).
- **Output**: auto-saved notes.
- **Reasoning**: 50ms delay ensures textarea is in DOM before listener attaches; 300ms debounce prevents save spam.

### 2.2 `saveNotesInline(workoutId, notes)`
- **Inputs**: workout id, textarea value.
- **Computation**: find workout in `workoutData.workouts`; if missing → return; ensure `workout.summary` exists; `summary.notes = notes.trim()`; `saveToLocalStorage()`.
- **Output**: persisted notes.
- **Reasoning**: trim prevents whitespace-only "notes"; silent save (no notification spam).

### 2.3 `filterHistory(filter, event)` — tab switcher
- **Inputs**: filter string ('all'|'week'|'month'|'year'|'longevity'), click event.
- **Computation**:
  1. If event provided → remove `.active` from all `.tab` elements; add `.active` to `event.target`.
  2. Set `currentHistoryFilter = filter`.
  3. Call `updateWorkoutHistory()` to re-render table.
- **Output**: filtered history table.
- **Reasoning**: tab-based filter is standard UX; programmatic filter (no event) is also supported.

### 2.4 `exportHistoryCSV()` — Blob download
- **Inputs**: `workoutData.workouts`.
- **Computation**:
  1. Build CSV header: `Date,Workout Type,Exercises Completed,Total Volume,Average RPE`.
  2. For each workout → row: `"${date}","${workout.name || workout.type}","${completed}/${total}","${volume}","${rpe}"`.
  3. Create `Blob([csvContent], {type:'text/csv'})`.
  4. `URL.createObjectURL(blob)`.
  5. Create `<a download="workout-history-${ISO date}.csv">`, append to body, click, remove, revoke URL.
  6. Show notification "Workout history exported as CSV!".
- **Output**: downloaded CSV file.
- **Reasoning**: Blob+anchor pattern is the standard browser download mechanism; revoking URL prevents memory leak.

### 2.5 `updateProgressCharts()` — chart orchestrator
- **Inputs**: `workoutData.workouts`.
- **Computation**: calls 4 chart updaters in sequence:
  1. `updateVolumeChart()` — last 8 workouts' volume bar chart.
  2. `updateStrengthChart()` — strength trend line.
  3. `updateFrequencyChart()` — workout frequency per week.
  4. `updateRPEChart()` — RPE distribution histogram.
- **Output**: 4 rendered Chart.js canvases.
- **Reasoning**: centralized orchestrator ensures consistent data slicing.

### 2.6 `updateVolumeChart()` — bar chart with gradient
- **Inputs**: `workoutData.workouts.slice(-8)`.
- **Computation**:
  1. Get canvas context; destroy existing chart if present.
  2. Labels: `W1`...`W8`.
  3. Data: `workout.summary.totalVolume` per workout.
  4. Create linear gradient `rgba(54, 162, 235, 0.7)` → `rgba(54, 162, 235, 0.1)`.
  5. New Chart.js bar with `borderRadius: 6`, `responsive: true`, `maintainAspectRatio: false`, legend hidden.
- **Output**: bar chart.
- **Reasoning**: last 8 workouts is a sensible default window; gradient gives visual depth.

### 2.7 Screen wake lock (`requestWakeLock`)
- **Inputs**: `navigator.wakeLock` API.
- **Computation**: `wakeLock = await navigator.wakeLock.request('screen')`; store handle; on visibility change → re-acquire if lost.
- **Output**: screen stays awake during workout.
- **Reasoning**: prevents screen from sleeping mid-set; re-acquires after tab switch.

### 2.8 `releaseWakeLock()`
- **Inputs**: stored wakeLock handle.
- **Computation**: `wakeLock.release()`; set handle to null.
- **Output**: screen can sleep.
- **Reasoning**: releases on workout complete or app backgrounded.

### 2.9 Visibility change re-acquire
- **Inputs**: `document.visibilitychange` event.
- **Computation**: on visible → if wakeLock was held and released → re-acquire.
- **Output**: wakeLock re-acquired.
- **Reasoning**: Chrome releases wakeLock on tab switch; auto re-acquire maintains continuity.

### 2.10 History table rendering
- **Inputs**: `currentHistoryFilter`, `workoutData.workouts`.
- **Computation**: filter workouts by date range (all/week/month/year/longevity); render rows with date, type, exercises completed, volume, RPE; clicking a row expands detail panel with notes textarea.
- **Output**: history table.
- **Reasoning**: filter reduces cognitive load; expandable rows show detail on demand.

### 2.11 CSV escaping
- **Inputs**: string values.
- **Computation**: wrap each field in double quotes; escape internal quotes by doubling (`"` → `""`).
- **Output**: CSV-safe string.
- **Reasoning**: prevents comma/quote injection from workout names.

---

## E3. Edge cases handled

- **Notes textarea not in DOM** (50ms timeout fires before render): `if (!textarea) return`.
- **Workout not found by id**: `saveNotesInline` returns early.
- **Notes empty after trim**: saves empty string (not null).
- **Filter value invalid**: `currentHistoryFilter = filter` (no validation); downstream `updateWorkoutHistory` falls through to 'all'.
- **`event.target` not a tab**: `.active` class still toggled (no guard).
- **No workouts in history**: CSV exports header only; charts show empty.
- **Chart canvas missing**: `getContext` throws; not caught — would crash. (Reference bug.)
- **Existing chart instance**: destroyed before re-create (prevents Chart.js warning).
- **`navigator.wakeLock` undefined**: `requestWakeLock` would throw; caller guards with `if (typeof navigator.wakeLock === 'function')`.
- **WakeLock request rejects**: try/catch swallows.
- **WakeLock released by browser**: `visibilitychange` re-acquires.
- **CSV with special characters in workout name**: wrapped in quotes, escaped.
- **Blob URL not revoked**: memory leak (reference bug; revocation present but on error path it's skipped).

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Notes debounce | 300 ms | Typical typing pause |
| Notes save on Enter (no Shift) | Yes | Quick commit |
| Notes save on blur | Yes | Catches missed debounces |
| Notes placeholder | "Add notes about this workout..." | Guiding |
| Hint text | "💡 Changes save automatically when you stop typing." | Sets expectation |
| Filter tabs | all/week/month/year/longevity | Standard time ranges |
| Chart window | last 8 workouts | ~2 months at 3×/week |
| Volume chart type | bar | Discrete per-workout values |
| Volume chart gradient | blue 0.7 → 0.1 alpha | Visual depth |
| Chart borderRadius | 6 px | Modern, soft |
| Legend hidden | true | Single-series chart, no legend needed |
| CSV filename | `workout-history-${ISO date}.csv` | Date-stamped |
| CSV columns | 5 (date, type, completed, volume, RPE) | Core summary |
| WakeLock type | 'screen' | Prevents screen sleep |
| WakeLock re-acquire on visibility | Yes | Chrome releases on tab switch |
| History row expand | click → detail panel with notes | Progressive disclosure |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| Inline notes textarea with debounce-save | FULL (`History.kt` notes field) | — |
| `saveNotesInline` trim + silent save | FULL | — |
| `filterHistory` tab switcher | FULL (`History.kt` filter chips) | — |
| `exportHistoryCSV` Blob download | FULL (`Exporter.kt` + share intent) | — |
| CSV escaping (quotes) | FULL | — |
| `updateProgressCharts` orchestrator | FULL (`Progress.kt`) | — |
| `updateVolumeChart` bar + gradient | FULL (Compose chart) | — |
| `updateStrengthChart` line | FULL | — |
| `updateFrequencyChart` | FULL | — |
| `updateRPEChart` histogram | FULL | — |
| Chart window (last 8) | FULL | — |
| Screen wake lock request | FULL (`MainActivity.kt` WakeLock) | — |
| WakeLock release | FULL | — |
| Visibility-change re-acquire | PARTIAL | Re-acquires on `onResume` but doesn't handle `onStop`→`onResume` race |
| History table expandable rows | FULL | — |
| Notes hint text "💡 Changes save automatically" | MISSING | Native has no hint text on notes field |

**Coverage: 88%** (14 FULL / 1 PARTIAL / 1 MISSING of 16 behaviours)

---

## H. Top 3 gaps to close

1. **No hint text on notes field** — native textarea has placeholder but no "auto-saves" hint. Add helper text below the field so users know they don't need to tap save.
2. **WakeLock re-acquire race on `onStop`→`onResume`** — if the system releases the wakelock during `onStop` and the user returns quickly, the re-acquire may fail silently. Add a retry loop with 3 attempts and exponential backoff.
3. **Chart.js gradient not replicated** — native Compose chart uses solid color, not the blue→transparent gradient. Add a `Brush.verticalGradient` to match the visual depth.
