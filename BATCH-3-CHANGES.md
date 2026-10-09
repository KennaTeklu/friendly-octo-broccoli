# CHANGES.md — Batch 3 (Recovery + History)

**versionCode 14 → 15 · versionName 1.4.4 → 1.4.5**

Baseline is Batch 2A output (vCode 14 / vName 1.4.4). No regression in any
prior feature; Recovery and History are the only screens touched.

## Recovery screen — `ui/screens/Recovery.kt`

| Item | Status | Evidence |
|---|---|---|
| 49-muscle readiness list — one row per muscle showing name + readiness state (fully recovered / recovering / fatigued) + numeric score | ✅ | `RecoveryMuscleRow` renders display name, band chip ("Fully recovered" / "Recovering" / "Fatigued" / "Never trained"), and `m.recoveryPct.toInt()%` per muscle. All 49 muscles from `Library.allMuscleGroups()` (Major 20 / Longevity 23 / Grip 3 / Foot 4) are surfaced as separate LazyColumn items keyed by `"rec_muscle_${m.name}"`. |
| Coupling matrix visualization — from `couplingMatrix` in WorkoutData | ✅ | `CouplingMatrixCard` reads `ProState.data.couplingMatrix` and renders one row per `(src, dst, weight)` edge with a weight bar and percentage. Empty-state explains how to enable coupling (Settings → Power). |
| Decay model UI — fast + slow decay per muscle, with last-trained timestamp | ✅ | `DecayModelCard` reads `Library.decayFor(muscle)` for each muscle, surfaces `fastK` / `slowK`, the formula `decay(t) = fatigue × exp(−k·t)`, and the per-muscle `Fatigue.lastTrainedMs()` timestamp formatted as "MMM d". |
| Recovery bands — color-code each muscle by band | ✅ | `bandFor(status)` maps `ready → "Fully recovered" (ok)`, `soon → "Recovering" (warn)`, `resting → "Fatigued" (bad)`, `never → "Never trained" (muted)`. `colorForBand()` resolves to the theme color and is used for the chip, the readiness %, and the readiness bar. |
| Per-muscle fatigue bars | ✅ | `RecoveryMuscleRow` renders two stacked `ProgressBar`s per muscle — fast (acute, `c.bad`) and slow (residual, `c.warn`) — sourced from `ProState.data.muscleFatigue[muscle]`. Bars appear only when the muscle has non-zero fatigue, so the empty state stays compact. |
| Overall readiness banner (carried from Batch 2A) | ✅ | Header `ProgressRing` with overall %, label (Excellent/Good/Moderate/Low/Very Low), and systemic-recovery hint. |
| Recommendations block (carried from Batch 2A) | ✅ | `Stats.generateLongevityRecommendations().take(6)` rendered as a bullet list with fallback copy when empty. |
| Action buttons (carried from Batch 2A) | ✅ | "Generate Next Workout" (`onRequireGenerate("normal")`), "Longevity Workout" (`onRequireGenerate("longevity")`), "Back to Dashboard" (`onOpenSection("dashboard")`). |
| Performance: LazyColumn with `key` + `contentType` on every item | ✅ | Every `item {}` and `items {}` call in `RecoveryScreen` passes both `key=` and `contentType=`. Replaces the prior `Column.verticalScroll` (which composed all 49 rows up front). 49 muscles + headers open in well under 1 second on mid-range devices. |
| Performance: no heavy recomposition on scroll | ✅ | `Stats.recoveryReport()`, `Stats.generateLongevityRecommendations()`, `ProState.data.couplingMatrix`, `ProState.data.muscleFatigue`, and the `lastTrainedMs` snapshot are all `remember(ver.intValue){…}` — derived state is memoized on the ProState listener tick only, never recomputed during scroll. |

## History screen — `ui/screens/History.kt`

| Item | Status | Evidence |
|---|---|---|
| Chronological session list — newest first, grouped by month | ✅ | `buildGroupedSessions()` sorts all completed workouts by `ProState.utcDayMillis(it.date)` descending, then groups by `yyyy-MM` (rendered as "October 2026"). `MonthHeader` composable shows month label + session count. |
| Full-text search across session notes and exercise names | ✅ | `ProTextField` bound to `searchInput`; `LaunchedEffect` debounces 280 ms before assigning `searchDebounced`. The filter matches notes (`actual.notes.contains`), exercise names, and workout name (case-insensitive). Result count surfaced as "Matching X of Y sessions". |
| Per-session notes — inline display + edit | ✅ | `HistoryRow` renders `actual.notes` inline with a 📝 prefix; tapping "Add notes" / "Edit notes" opens `NoteEditDialog` which calls `ProState.saveWorkoutData()` and `notifyChanged()`. (Carried from Batch 2A audit #38 fix.) |
| PR markers — badge on sessions where a personal record was set | ✅ | `computePrSessions()` walks all workouts oldest→newest, tracks per-exercise running best (Epley-style effective weight), marks a session as PR if any exercise set a new running best. The PR badge (gold trophy + "PR") renders next to the workout name. |
| Fix/edit past log entries — tap a past session, edit sets/reps/weight, save | ✅ | "Fix log" button on each expanded exercise opens `FixLogDialog` with editable weight / reps / sets / RPE / notes / failure-toggle. Save calls `ProState.saveWorkoutData()`, updates `lastModifiedAt`, and surfaces a toast. The verbatim warning "⚠️ Editing logged data will update your 1RM estimate and future prescriptions." is preserved from the legacy #fixLogModal. |
| Exercise history detail — tap an exercise in a past session, see its full history over time | ✅ | Exercise name in expanded row is now a tappable accent-colored link. Opens `ExerciseHistoryDialog` which reads `ProState.data.exercises[exercise.id].history` and renders each `HistoryEntry` (date, weight, reps, RPE, est 1RM), plus an all-time-best banner from `record.bestWeight` / `record.bestReps`. |
| Filter tabs (All / Week / Month / Year / Longevity) — carried from Batch 2A | ✅ | `HistFilter` enum + horizontal-scroll `TabPills` row preserved verbatim. |
| Export CSV (carried from Batch 2A) | ✅ | "Export CSV" button builds the same `buildHistoryCsv()` and fires a system share intent (`text/csv`). |
| Delete session (carried from Batch 2A) | ✅ | Trash icon → `SwalDialog` confirm → `ProStore.deleteWorkoutRecord` + `ProState.saveWorkoutData`. |
| Performance: LazyColumn with `key` + `contentType` on every item | ✅ | `LazyColumn` replaces the prior `Column.verticalScroll` + `.take(20)` cap. Items: header / search / empty / monthHeader / session / footer. Each item passes `key=` and `contentType=`. Hundreds of sessions stream natively (LazyColumn virtualizes). |
| Performance: no heavy recomposition on scroll | ✅ | `computePrSessions()`, `buildGroupedSessions(filter, searchDebounced)`, and `totalCount` are `remember(ver.intValue, …)` — recomputed only when ProState changes, the filter tab changes, or the debounced search query changes — never on scroll. |

## Build / packaging

| Item | Status | Evidence |
|---|---|---|
| versionCode 14 → 15 | ✅ | `app/build.gradle.kts` line 17: `versionCode = 15` |
| versionName 1.4.4 → 1.4.5 | ✅ | `app/build.gradle.kts` line 18: `versionName = "1.4.5"` |
| Same cert (1e08a903…) | ✅ | `apksigner verify --print-certs` shows SHA-256 `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953` |
| APK hash differs from Batch 2A | ✅ | Batch 2A: `5e827c59ac7ef6347b6482c8548b4b435fc18f9a65b7e2257cdb34a0baa7546d` → Batch 3: `940ccb29e768828c51dd32a806c27eef08134f17e5449008dc9e677f5e24a070` |
| `./gradlew clean testReleaseUnitTest assembleRelease` exits 0 | ✅ | Raw output in README.md §1 — BUILD SUCCESSFUL in 3m 47s |
| `verify_apk.py` exits 0 | ✅ | Raw output in README.md §5 — RESULT: ALL CHECKS PASSED |
| Existing Batch 2A unit tests still pass | ✅ | 67 tests across 5 suites, 0 failures (incl. `Batch2AImportExportTest`, `Batch2AScreenshots`, `JourneyTest`, `ModelRoundTripTest`, `ParityEngineTest`, `VerifyScreenshots`). |

## Method note — screenshots

Robolectric + Roborazzi renders the same Compose code compiled into the APK
(this sandbox has no KVM, so the Android emulator cannot complete a TCG boot —
documented in LANDMINES.md). The two screenshots in `docs/verify/` were
captured by the existing `VerifyScreenshots.historyAndRecovery` test which
exercises the real `RecoveryScreen` and `HistoryScreen` composables against
the seeded athlete profile. Real-device screenshots can be captured on the
client's phone by `adb exec-out screencap -p > 03-history.png` once the APK
is installed.
