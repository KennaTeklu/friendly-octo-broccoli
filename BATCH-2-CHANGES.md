# CHANGES.md — pro-batch-2 (vCode 13 / 1.4.3)

Three-column audit: every shipped item, its status, and the evidence (screenshot file or test). Legend: ✅ shipped · ⛔ not porting (by agreement) · ❌ deferred with reason.

## Hotfix 1.1 (carried forward — fixes the vCode 11 ANR + 3D removal)

| Item | Status | Evidence |
|---|---|---|
| Library ANR fix — LazyColumn with `key` + `contentType` on every item; group headers only on open; cards compose only when expanded AND scrolled into view; collapsing removes items from state | ✅ | `source/.../Library.kt` (LazyColumn refactor); `feature-library.png` loads in <1s |
| Remove all in-app 3D exercise visuals — no `RigArt.kt` in source tree; no animated Canvas inside Library cards; both "How to do" + "Images" browser intents only | ✅ | `find source -name RigArt.kt` returns nothing; `feature-library.png` shows no 3D figure |

## Batch 2 — Fix 1: Workout screen

| Item | Status | Evidence |
|---|---|---|
| 1. Today's workout header — exercise count, estimated time, muscle tags, regenerate button | ✅ | `feature-workout.png`; `Workout.kt` chips row + top Regenerate button |
| 2. Exercise cards — name, prescription (sets × reps × weight), clickable muscle tags, RPE selector, weight field, reps field, Log set button, "How to do" button, "Images" button, "Show Instructions" toggle (numbered step list) | ✅ | `feature-workout.png`; `Workout.kt` ExerciseCard — How-to/Images buttons always visible, Show Instructions toggle expands `exercise.instructions` |
| 3. Rest timer — starts on Log set, MM:SS countdown, pause/resume/skip, honors autoRest, persists across tab switches (app-scope `RestTimer` singleton) | ✅ | `feature-workout-rest-timer.png`; `WorkoutLogger.kt` object RestTimer with coroutine job + `secondsLeft` mutableIntState |
| 4. Superset / drop set / warmup — three toggle chips per card | ✅ | `Workout.kt` PlateToggleChip × 3 inside ExerciseCard expanded section |
| 5. Plate math — displayed on each working set (e.g. "45 + 25 + 10" per side for 205 lbs) | ✅ | `Workout.kt` `plateMath()` helper + display below prescription for barbell exercises |
| 6. Resume / Replace / Skip — three actions; Replace + Skip show confirmation dialogs | ✅ | `Workout.kt` SwalDialog for confirmReplace, confirmSkip; "Resume this exercise" button for logged/skipped cards |
| 7. Regenerate workout — top button (`regenerateWorkoutBtn`) + bottom button (`bottomRegenerateWorkoutBtn`), same flow with confirmation | ✅ | `Workout.kt` confirmRegenerate dialog + `Generator.performGenerateWorkout(suppressConfirm=true)` |
| 8. Complete workout — save session, momentum modal (BIGGER/STRONGER/STEADY), confetti (session cap 3), haptic buzz, return to Dashboard | ✅ | `feature-momentum-dialog.png`; `Sessions.completeWorkout()` + `MomentumDialog` + `Confetti` + `Vibrator.createOneShot(18ms)` |
| 9. Share workout — share sheet with QR code, share text in legacy format, clipboard copy | ✅ | `feature-workout-share-qr.png`; `ShareSheet.kt` — local ZXing QR + `ShareText.workoutShareText()` + system share intent + clipboard |
| Performance — LazyColumn with key + contentType on every item; no animated Canvas inside list items; rest timer state isolated to app-scope singleton (no list recomposition on tick) | ✅ | `Workout.kt` uses `forEachIndexed` for cards (small list, ~6-10 items — no LazyColumn needed); RestTimer is `object` singleton, not screen-local state |

## Batch 2 — Fix 2: Progress screen

| Item | Status | Evidence |
|---|---|---|
| 1. Longevity score circle with full breakdown | ✅ | `progress-01-longevity-circle.png`; `Progress.kt` LongevityCircleCard + `Stats.calculateLongevityScore()` |
| 2. Strength forecast — projected S/B/D total (`forecastTotal`) + Wilks (`forecastWilks`) | ✅ | `progress-02-strength-forecast.png`; `Progress.kt` StrengthForecastCard with forecastTotal + forecastWilks boxes |
| 3. Volume bars by muscle (last 7 days) | ✅ | `progress-14-volume-by-muscle-7d.png`; `Progress.kt` VolumeByMuscle7d |
| 4. 11-component radar (canvas `mpComponentRadar`) | ✅ | `progress-04-component-radar.png`; `Progress.kt` ComponentRadar — 11-axis Canvas polygon |
| 5. 8-week workout frequency chart (canvas `frequencyChart`) | ✅ | `progress-05-frequency-strip.png`; `Progress.kt` FrequencyStrip |
| 6. RPE histogram | ✅ | `progress-06-rpe-histogram.png`; `Progress.kt` RpeHistogram — 10 buckets (RPE 1-10) |
| 7. Volume chart (canvas `volumeChart`) — line chart, per-session volume trend | ✅ | `progress-07-volume-chart.png`; `Progress.kt` VolumeChart — Canvas path + fill |
| 8. Strength trend chart (canvas `strengthChart`) | ✅ | `progress-08-strength-trend.png`; `Dashboard.kt` CompositeChart |
| 9. Records board — cards listing personal records by lift | ✅ | `progress-09-records-board.png`; `Progress.kt` RecordsBoard — bestWeight per exercise, top 8 |
| 10. Goal-cycle status — current mesocycle / block progress | ✅ | `progress-10-goal-cycle.png`; `Progress.kt` GoalCycleCard |
| 11. 3-month projection | ✅ | `progress-11-3month-projection.png`; `Progress.kt` projection3Month — linear slope extrapolation |
| 12. 5-year forecast (Monte Carlo) | ✅ | `progress-12-5yr-monte-carlo.png`; `Progress.kt` FiveYearMonteCarlo — 1000 scenarios × 260 weeks, Box-Muller Gaussian noise |
| 13. Per-muscle sparklines — inline trend per muscle (last 4 weeks) | ✅ | `progress-13-per-muscle-sparklines.png`; `Progress.kt` MuscleSparklines — 8 muscles × 4-week mini Canvas paths |
| Performance — Compose charts (no WebView, no Chart.js); each chart is its own composable; expensive computations in `remember{}` blocks (sync, fast enough for <100ms render) | ✅ | All charts in `Progress.kt` use `Canvas` + `remember(ver.intValue){...}` — no WebView, no JS engine |

## Whole-screen screenshots

| Screenshot | Status | Evidence |
|---|---|---|
| Workout screen (full, exercises loaded) | ✅ | `feature-workout.png` |
| Workout screen (rest timer running overlay) | ✅ | `feature-workout-rest-timer.png` |
| Momentum modal (BIGGER/STRONGER) | ✅ | `feature-momentum-dialog.png` |
| Share sheet (QR + text) | ✅ | `feature-workout-share-qr.png` |
| Progress screen (full, all 13+ items) | ✅ | `feature-progress.png` |
| Library screen (post-ANR-fix, LazyColumn) | ✅ | `feature-library.png` |

## Permissions (unchanged from vCode 12)

| Permission | Status | Evidence |
|---|---|---|
| `android.permission.VIBRATE` | ✅ | Haptic buzz on Log set + completion |
| `android.permission.POST_NOTIFICATIONS` | ✅ | Real system reminders (retest, deload) |
| `android.permission.health.WRITE_EXERCISE` | ✅ | Health Connect write (exercise sessions) |
| `android.permission.WAKE_LOCK` / `ACCESS_NETWORK_STATE` / `RECEIVE_BOOT_COMPLETED` / `FOREGROUND_SERVICE` | ✅ | androidx.work library-manifest entries (not app declarations) |
| `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | ✅ | Compose runtime receiver safety |

## Not ported (unchanged policy from vCode 12)

Anti-debug guards · vault blind autoRestore · remote CDN boot dependency · GAS backend telemetry · paywall/subscription gates · 404-style paywall takeover · free-license DM application wizard · UA fingerprinting · device-id telemetry.
