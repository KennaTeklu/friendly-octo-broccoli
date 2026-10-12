# 04-strength-tests — Test result processing, re-test reminders, test modal UI

Source: `reference/scripts/app/04-strength-tests.js` (L3782–4253 of original `app.js`)

## A. File purpose
This module handles 1RM strength testing end-to-end: it validates the rep range (1–15), computes an Epley-based estimated 1RM, persists the result on the exercise record (`tested1RM`, `testDate`, `testReps`, `testWeight`), pushes a `history` entry flagged `isTest`, fires-and-forgets a backend sync payload, and surfaces a 6-week re-test reminder. It also exposes `showTestModal` for the queue-based test UI.

## B. Public surface
- `processTestResult(exerciseId, weight, reps)` — validates 1–15 reps; computes `epley = weight × (1 + reps/30)`; updates `record.tested1RM/testDate/testReps/testWeight`; pushes `record.history` entry `{date, weight, reps:[reps], rpe:7, isTest:true, estimated1RM}`; saves; fire-and-forgets `testPayload` (`dataType:'test'`) to backend; returns `estimated1RM`.
- `checkAndRemindReTest()` — walks `workoutData.exercises`, finds tests older than 42 days, calls `showNotification` with a callback that opens `library` and pre-fills search with the exercise name. Returns after first reminder per session.
- `getExerciseById(exerciseId)` — scans `ultimateExerciseLibrary` for `name.toLowerCase().replace(/\s/g,'_') === exerciseId`.
- `testOnCompleteCallback` — module-level callback for the test queue.
- `showTestModal(exerciseId, onComplete)` — SweetAlert-driven test modal with weight/reps/notes inputs; calls `processTestResult` and `onComplete`.

## C. Dependencies
- Globals: `Swal` (SweetAlert), `document`, `localStorage`.
- Cross-module: `getOrCreateExerciseRecord` (defined in 02-erf-kalman / persistence), `getDeviceId`, `getDeviceInfo` (from 03), `workoutData.user.name`, `saveToLocalStorage`, `showNotification` (from later modules), `ultimateExerciseLibrary` (05), `renderLibrary`, `showSection`.

## D. Native-equivalent mapping
- `processTestResult` → `engine/Library.kt::OneRm.processTestResult` (L705) — implemented natively with the same Epley formula. Native also exposes `OneRm.robustEstimate` (L732) which legacy lacks.
- `checkAndRemindReTest` (6-week reminder) → **GAP**. Native `core/Nudges.kt::Nudges` exists for general nudges but no specific retest-after-42-days reminder is wired. `OneRmLabScreen.kt` could surface this.
- `getExerciseById` → `engine/Library.kt::Library.getExerciseById` (L197) — implemented (with `includeBodyweight` flag, an enhancement).
- `showTestModal` UI → `ui/screens/WorkoutLogger.kt::OneRmTestPanel` (L256) — Compose replacement for the SweetAlert modal.
- Backend `testPayload` sync → **GAP** (no backend, see 03).

## E. Patterns worth porting
- **6-week re-test reminder with deep-link to library search.** Native currently shows no such nudge. Pattern: schedule via `Nudges` after `testDate + 42d`, tap-action opens Library with `search.value = exercise.name`. Cheap and high-value UX.
- **`isTest:true` flag on history entries** distinguishes max-effort reps from regular working sets — `Model.kt::HistoryEntry` (L311) has the field but `Stats.kt` doesn't filter on it; using it to exclude test sets from fatigue calculations would be more accurate.
- **RPE hard-coded to 7 for tests** — a deliberate "assumed moderate effort" default; native could ask the user (test sets are typically RPE 9–10).
- **`testOnCompleteCallback` queue** — pattern worth replicating when multiple tests are queued (post-onboarding assessment); `Onboarding.kt` could chain these.

## F. Feature IDs
`P04-1RM-TEST-PROCESSING`, `P04-RETEST-REMINDER`, `P04-TEST-MODAL`, `P04-BACKEND-TEST-SYNC`, `P04-IS-TEST-FLAG`.
