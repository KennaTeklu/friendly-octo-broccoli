# PARITY.md — Batch 3 (Recovery + History)

Side-by-side parity check between the legacy HTML app
(`cautious-enigma/index (24).html`) and the native Compose app shipped in
this zip. Only the two screens in scope for Batch 3 are compared here;
parity for the other screens is unchanged from Batch 2A.

## recovery-section — HTML L6549–L6586 vs native `RecoveryScreen`

| HTML element | HTML line | Native equivalent | Status |
|---|---|---|---|
| `#recovery-section` container | L6549 | `RecoveryScreen` LazyColumn | ✅ |
| `<h2 class="section-title"><i class="fas fa-heartbeat"></i> Muscle Recovery Status</h2>` | L6551 | `SectionTitle("fa-heartbeat", "Muscle Recovery Status")` | ✅ |
| `<div class="muscle-category"><h4>Major Muscle Groups</h4>` + 3 more category blocks | L6552–L6567 | Per-category `item(key="rec_cat_$cat")` headers, then `items(...)` of muscle rows; same 4 categories (Major / Longevity / Grip / Foot) | ✅ |
| Per-muscle row: name + last-trained + status (legacy `updateMuscleRecoveryDisplay` L42863) | L42863 | `RecoveryMuscleRow` — name + last-trained timestamp + recovery band chip + numeric % + readiness bar + fast/slow fatigue bars | ✅ (extended) |
| `<div id="recoveryRecommendations">` recommendations block | L6570 | Glass card with `Stats.generateLongevityRecommendations().take(6)` | ✅ |
| `<button onclick="generateNextWorkout()">Generate Next Workout</button>` | L6578 | `P4Button("Generate Next Workout", icon="fa-bolt")` → `onRequireGenerate("normal")` | ✅ |
| `<button onclick="generateLongevityWorkout()">Longevity Workout</button>` | L6581 | `P4Button("Longevity Workout", icon="fa-user-md", style=SUCCESS)` → `onRequireGenerate("longevity")` | ✅ |
| `<button onclick="showSection('dashboard')">Back to Dashboard</button>` | L6575 | `P4Button("Back to Dashboard", icon="fa-arrow-left", style=GHOST)` → `onOpenSection("dashboard")` | ✅ |

### Batch-3 spec extensions (HTML has no equivalent — added natively)

| Spec requirement | Native implementation | Status |
|---|---|---|
| Coupling matrix visualization from `couplingMatrix` in WorkoutData | `CouplingMatrixCard` reads `ProState.data.couplingMatrix`, renders one chip+bar per edge | ✅ |
| Decay model UI — fast + slow decay per muscle, with last-trained timestamp | `DecayModelCard` reads `Library.decayFor(muscle)` for each muscle, shows k_fast / k_slow / last-trained date | ✅ |
| Recovery bands — color-code each muscle by band | `bandFor(status)` → `colorForBand()` resolves to `c.ok`/`c.warn`/`c.bad`/`c.text3` | ✅ |
| Per-muscle fatigue bars | `RecoveryMuscleRow` renders fast + slow `ProgressBar` per muscle | ✅ |

## history-section — HTML L6424–L6459 vs native `HistoryScreen`

| HTML element | HTML line | Native equivalent | Status |
|---|---|---|---|
| `#history-section` container | L6424 | `HistoryScreen` LazyColumn | ✅ |
| `<h2 class="section-title"><i class="fas fa-history"></i> Workout History</h2>` | L6426 | `SectionTitle("fa-history", "Workout History")` | ✅ |
| `<button class="tab" onclick="filterHistory('all')">All</button>` + 4 more filter tabs | L6428–L6432 | `HistFilter` enum (ALL / WEEK / MONTH / YEAR / LONGEVITY) rendered as horizontal-scroll `TabPills` | ✅ |
| `<table id="workoutHistoryTable">` rows: Date / Workout / Actions | L6435–L6445 | `HistoryRow` GlassCard: date label, name (with PR badge if applicable), trash action | ✅ |
| Per-row expand → exercise list (legacy `renderHistoryRow`) | — | `AnimatedVisibility(expanded)` shows summary + per-exercise prescribed/actual + notes + Fix-log link | ✅ |
| `<div class="export-btn" onclick="exportHistoryCSV()">Export as CSV</div>` | L6448 | `P4Button("Export CSV", icon="fa-file-csv")` builds CSV via `buildHistoryCsv()` and fires `ACTION_SEND` | ✅ |
| `<div class="export-btn" onclick="printHistory()">Print History</div>` | L6451 | ⛔ Desktop-only — print is a browser API. CSV export covers the same use case. | ⛔ desktop-only |
| `<div class="export-btn" onclick="showSection('dashboard')">Back to Dashboard</div>` | L6454 | `P4Button("Back to Dashboard", icon="fa-arrow-left", style=GHOST)` → `onOpenSection("dashboard")` | ✅ |

### Batch-3 spec extensions (HTML has no equivalent — added natively)

| Spec requirement | Native implementation | Status |
|---|---|---|
| Chronological session list — newest first, grouped by month | `buildGroupedSessions()` sorts by `utcDayMillis` desc, groups by `yyyy-MM`, `MonthHeader` renders "October 2026" + count | ✅ |
| Full-text search across session notes and exercise names | `ProTextField` + `LaunchedEffect` debounce + `buildGroupedSessions(filter, searchDebounced)` filter across notes/exercise-name/workout-name | ✅ |
| Per-session notes — inline display + edit (also Batch-2A audit #38) | `NoteEditDialog` opens from "Add notes"/"Edit notes" button | ✅ |
| PR markers — badge on sessions where a personal record was set | `computePrSessions()` walks oldest→newest tracking running Epley-style effective weight per exercise; gold "PR" badge on session rows | ✅ |
| Fix/edit past log entries — tap a past session, edit sets/reps/weight, save | `FixLogDialog` with editable weight/reps/sets/RPE/notes/failure; verbatim warning preserved | ✅ |
| Exercise history detail — tap an exercise in a past session, see its full history over time | Exercise name is now tappable; `ExerciseHistoryDialog` shows the `ExerciseRecord.history` list (date/weight/reps/RPE/est1RM) + all-time-best banner | ✅ |

## Performance parity — HTML vs native

| Concern | HTML approach | Native approach | Status |
|---|---|---|---|
| Render 49 muscle rows | DOM append, all rows always in document | `LazyColumn` virtualizes — only visible rows compose; `key=` and `contentType=` on every item | ✅ (better than HTML) |
| Render hundreds of history rows | DOM append, `.take(20)` cap (HTML had no virtualization) | `LazyColumn` virtualizes; no `.take(20)` cap; grouping/filter/search memoized on `(ver, filter, searchDebounced)` only | ✅ (better than HTML) |
| Avoid recomputation on scroll | n/a (HTML doesn't recompose) | Heavy state (`Stats.recoveryReport()`, `computePrSessions()`, `buildGroupedSessions()`, etc.) is `remember(ver.intValue, …)`, never recomputed during scroll | ✅ |
| Sub-1-second open for Recovery | n/a | 49 LazyColumn items + ~10 fixed items = ~60 items; cold-open time dominated by `Stats.recoveryReport()` which is one pass over `Library.allMuscleGroups()` (~1ms on the test JVM) | ✅ |

## Items NOT shipped (and why)

| Item | Reason |
|---|---|
| `printHistory()` button | Desktop-only — uses `window.print()`. The CSV export covers the same use case (and the user can share the CSV to any printer app on Android). Documented in CHANGES.md as ⛔ desktop-only per SPEC.md §F. |
| Real-device screenshots | This sandbox has no KVM, so an Android emulator cannot complete a TCG boot (LANDMINES.md notes this constraint). Robolectric-rendered screenshots from the JVM exercise the same Compose code; the client can capture real-device screenshots via `adb exec-out screencap` once the APK is installed. |
