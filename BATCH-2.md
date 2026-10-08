# BATCH-2.md — Workout + Progress

Predecessor: Batch 1 (vCode 11) + Hotfix 1.1 (vCode 12) both accepted. Baseline on client phone is vCode 12, versionName 1.4.2.

Ship as versionCode 13, versionName 1.4.3.

---

## Scope

Two screens: Workout and Progress. Both must match the HTML source of truth in layout, behavior, and data. Both must be fast — no ANRs, no dropped frames.

Read index (24).html from https://github.com/KennaTeklu/cautious-enigma in full for these two screens before writing code. Specific line anchors:

Workout anchors:
- Workout activation: line 37186 (case 'workout':) and 37274
- performGenerateWorkout: line 36069
- updateWorkoutHeader: line 38178
- startOrGenerateWorkout: line 46582
- generateWorkoutForExercise: line 45957
- generateWorkoutForMuscles: line 43284
- generateWorkoutShareText: line 46974
- mpRenderWorkoutBanner: line 48902
- mpEnhanceWorkoutCards: line 48977

Progress anchors:
- Chart canvases: dashboardChart (line 6354), mpComponentRadar (6473), frequencyChart (6485), volumeChart (6505), strengthChart (6511)
- Chart height locks: lines 5978-5981

---

## Fix 1 — Workout screen

Must include — port every item from the HTML:

1. Today's workout header — exercise count, estimated time, muscle tags, regenerate button (updateWorkoutHeader, line 38178).
2. Exercise cards — one per prescribed exercise. Each card shows: name, prescription (sets x reps x weight), clickable muscle tags, RPE selector, weight field, reps field, Log set button, 'How to do' button (opens https://www.google.com/search?q=<exercise> how to do), 'Images' button (opens https://www.google.com/search?tbm=isch&q=<exercise>), and a 'Show Instructions' toggle that expands a numbered step list.
3. Rest timer — starts on Log set. MM:SS countdown. Pause / resume / skip. Honors autoRest setting. Persists across tab switches (app-scope state, not screen-local).
4. Superset / drop set / warmup — three toggles per card. Match HTML behavior.
5. Plate math — displayed on each working set (e.g. 45 + 45 + 25 for 205).
6. Resume / replace / skip — three actions. Replace and skip show confirmation dialogs.
7. Regenerate workout — top button (regenerateWorkoutBtn) and bottom button (bottomRegenerateWorkoutBtn). Both call the same flow.
8. Complete workout — button at the bottom. On tap: save session, show momentum modal (BIGGER / STRONGER / STEADY), fire confetti (session cap 3), haptic buzz, return to Dashboard.
9. Share workout — share sheet with QR code, share text in legacy format, clipboard copy.

Performance rules (same as Library fix): LazyColumn with key and contentType on every item. No animated Canvas inside list items. Rest timer updates must not recompose the whole list — isolate timer state to a single composable.

Workout verification: screen opens under 1 second with all exercises visible; log set starts timer; rotate device survives; switching screens keeps timer running; complete shows momentum modal + confetti; no ANR after 60 seconds.

---

## Fix 2 — Progress screen

Must include — port every chart and card from the HTML:

1. Longevity score circle with full breakdown.
2. Strength forecast — projected S/B/D total + Wilks (forecastTotal, forecastWilks, line 6327).
3. Volume bars by muscle (last 7 days).
4. 11-component radar — canvas mpComponentRadar (line 6473).
5. 8-week workout frequency chart — canvas frequencyChart (line 6485).
6. RPE histogram.
7. Volume chart — canvas volumeChart (line 6505).
8. Strength trend chart — canvas strengthChart (line 6511).
9. Records board — cards listing personal records by lift.
10. Goal-cycle status — current mesocycle / block progress.
11. 3-month projection.
12. 5-year forecast (Monte Carlo or equivalent engine call).
13. Per-muscle sparklines — inline trend per muscle (last 4 weeks).

Performance rules: use Compose charts, not WebView, not embedded Chart.js. Each chart is its own composable. Render charts after first frame (LaunchedEffect + withFrameNanos or deferred state). Expensive computations on Dispatchers.Default, observed with collectAsStateWithLifecycle.

Progress verification: screen opens under 1 second; all 13 items render with real data; rotate device survives; scrolling no dropped frames; no ANR after 60 seconds.

---

## Delivery

- versionCode: 13
- versionName: 1.4.3
- Cert: 1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953 (unchanged)
- Package: com.peakform.fitness (unchanged)
- File: pro-batch-2.zip

Contents (same structure as Batch 1):

    pro-batch-2.zip
    ├── pro.apk
    ├── SHA256SUMS.txt
    ├── README.md
    ├── CHANGES.md
    ├── PARITY.md
    ├── docs/
    │   ├── BUILD.md
    │   ├── FONT-FIX.md
    │   └── verify/
    └── source/

Required in delivery: screenshots of Workout screen (exercises loaded), rest timer running, momentum modal, and each Progress chart (13 screenshots minimum for Progress). adb shell dumpsys gfxinfo output showing no dropped frames. Five verification commands raw output in README.md. CHANGES.md as three-column table (Item | Status | Evidence).

## Acceptance

Install with adb install -r. Open Workout — loads fast. Log a set — timer starts. Complete workout — momentum modal appears. Open Progress — all 13 items render. No ANR, no crash.

## Then

After Batch 2 is accepted, proceed to BATCHES.md Batch 3 — Recovery + History.
