# 04 — Strength Tests, Ratios, Modal, Weekly Plan

> Reference module: `reference/scripts/app/04-strength-tests.js` (479 lines).
> Native counterparts: `core/OneRmLab.kt`, `engine/Generator.kt`, `engine/Sessions.kt`, `ui/screens/OneRmLabScreen.kt`, `ui/screens/OneRMAuditScreen.kt`.

This module handles 1RM test capture, the Epley formula, the 6-week re-test reminder, the muscle database, and the 7-day split programme.

---

## E2. Deep patterns

### 2.1 `processTestResult(exerciseId, weight, reps)`
- **Inputs**: exerciseId, weight (lbs), reps (1-15).
- **Computation**:
  1. **Validate reps** — if `reps < 1 || reps > 15` → SweetAlert "Invalid Reps", return null.
  2. **Epley formula** — `epley = weight * (1 + reps/30)` → `estimated1RM`.
  3. **Persist on record** — `record.tested1RM = estimated1RM`, `record.testDate = ISO now`, `record.testReps = reps`, `record.testWeight = weight`.
  4. **Append to history** — `record.history.push({date, weight, reps:[reps], rpe:7, isTest:true, estimated1RM})`.
  5. **Save** — `saveToLocalStorage()`.
  6. **Backend sync** (fire-and-forget) — POST `{deviceId, deviceName, dataType:'test', timestamp, name, data:{exerciseId, weight, reps, estimated1RM, notes}}`.
- **Output**: estimated 1RM (number) or null on validation failure.
- **Reasoning**: Epley is the de-facto 1RM formula at 1-15 reps; capped at 15 because Epley loses accuracy beyond that.

### 2.2 Epley formula
- **Inputs**: weight, reps.
- **Computation**: `1RM = weight * (1 + reps/30)`.
- **Output**: estimated 1RM.
- **Reasoning**: 1 rep → 1.033×weight (small overhead); 10 reps → 1.333×weight; 30 reps → 2.0×weight. Linear in reps; matches empirical data 1-10 reps, acceptable 10-15.

### 2.3 `checkAndRemindReTest()` (6-week reminder)
- **Inputs**: `workoutData.exercises[*].testDate`.
- **Computation**: compute `sixWeeksAgo = today - 42 days`; iterate exercises; if `tested1RM` exists AND `testDate < sixWeeksAgo` → show notification "1RM test for X is over 6 weeks old. Consider re-testing." with a callback that navigates to library, sets the search input to the exercise name, dispatches an `input` event, and focuses after 120 ms.
- **Output**: one notification per session (returns after first match).
- **Reasoning**: 1RM values drift over 6 weeks due to neural adaptation; reminding keeps the prescription engine accurate.

### 2.4 Re-test callback chain
- **Inputs**: matched exercise record.
- **Computation**: `showSection('library')` → `setTimeout(120ms)` → set `librarySearch.value = exercise.name` → `search.dispatchEvent(new Event('input'))` → `search.focus()` → `renderLibrary()`.
- **Output**: user lands on library with the exercise already searched.
- **Reasoning**: 120 ms lets `showSection` finish DOM swap before focus.

### 2.5 `getExerciseById(exerciseId)`
- **Inputs**: exercise id string.
- **Computation**: iterate `ultimateExerciseLibrary` groups; for each, `find(ex => ex.name.toLowerCase().replace(/\s/g, '_') === exerciseId)`; return first match or null.
- **Output**: exercise object or null.
- **Reasoning**: the library is grouped by muscle; id is derived from name by space→underscore.

### 2.6 `showTestModal(exerciseId, onComplete)` UI flow
- **Inputs**: exerciseId, optional completion callback.
- **Computation**:
  1. Resolve exercise; if not found → SweetAlert "Exercise Not Found", return.
  2. Store `testOnCompleteCallback = onComplete`.
  3. Populate modal: `testExerciseName.textContent = "Test: ${exercise.name}"`; clear `testWeight` and `testReps` inputs.
  4. Clone the submit button to remove all prior listeners (avoids duplicate-fire): `submitBtn.cloneNode(true)`, replace in DOM.
  5. Attach click listener: parse `weight = parseFloat(testWeight.value)`, `reps = parseInt(testReps.value)`; if either is NaN → SweetAlert "Missing Information"; else call `processTestResult(exerciseId, weight, reps)`; on success close modal, fire callback, optionally refresh workout if exercise is in `currentWorkout` and workout section is active.
  6. Show modal.
- **Output**: visible modal with two inputs and submit button.
- **Reasoning**: cloning the button is a defensive pattern — duplicate listeners were a recurring bug in flat-namespace JS.

### 2.7 `muscleDatabase` (40+ muscles across 4 categories)
- **Inputs**: hard-coded data.
- **Computation**: `major` (20 muscles), `longevity` (23 muscles), `hands` (3 muscles), `feet` (4 muscles). Each entry: `{name, display, restDays, category, [agingRisk]}`.
- **Output**: muscle metadata consumed by `getAllMuscleGroups`, recovery calc, longevity score.
- **Reasoning**: rest days are tuned per muscle — calves/core/hands/feet recover in 1-2 days; large muscles in 3 days; longevity muscles in 2-3 days.

### 2.8 `workoutProgram.splits` (7-day component rotation)
- **Inputs**: hard-coded split definitions.
- **Computation**: 7 splits — `full_body_power` (quads/chest/back + power drill), `push_hypertrophy_balance` (chest/shoulders/triceps + balance), `pull_hypertrophy_coordination` (back/biceps/rear_delts + coordination), `legs_speed` (quads/hamstrings/glutes/calves + speed), `athletic_agility_reaction` (core/obliques/hip_flexors + agility + reaction), `cardio_endurance` (core/calves/glutes + cardio), `mobility_flexibility` (neck/forearms/hip_flexors/tibialis + stretching + mobility). Each entry: `{id, name, focus[], restAfter, drills[], component, componentsCovered[]}`.
- **Output**: weekly rotation that covers all 6 fitness components (muscular_strength, power, balance, coordination, speed, agility, reaction_time, muscular_endurance, cardiorespiratory_endurance, body_composition, flexibility).
- **Reasoning**: 7-day cycle ensures every component gets ≥1 session; flexibility wraps every workout.

---

## E3. Edge cases handled

- **`reps < 1` or `> 15`**: SweetAlert "Invalid Reps", returns null.
- **Exercise not found by `getExerciseById`**: returns null; modal shows "Exercise Not Found".
- **`weight` or `reps` blank / NaN**: SweetAlert "Missing Information", blocks submission.
- **`processTestResult` returns null**: modal stays open; no callback fired.
- **No exercises with old tests**: `checkAndRemindReTest` returns silently after iterating all.
- **Multiple old tests**: only the first triggers a notification (`return` after first match).
- **`showSection` not defined**: try/catch swallows.
- **`librarySearch` element missing**: callback guards with `if (search)`.
- **`renderLibrary` not defined**: callback guards with `typeof` check.
- **`currentWorkout` has the exercise but workout section inactive**: skips refresh (only refreshes if visible).
- **Submit button previously had listeners**: clone+replace pattern strips them.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Reps validation range | 1-15 | Epley is accurate to ±10% up to 10 reps, ±15% to 15; beyond that the curve flattens unrealistically |
| Epley formula | `weight × (1 + reps/30)` | Standard; widely studied |
| Re-test reminder threshold | 6 weeks (42 days) | Strength adapts measurably over 6 weeks |
| Reminder callback delay | 120 ms | Lets DOM swap settle before focus |
| Test history entry RPE | 7 | Tests are by definition RPE 10 (max), but stored as 7 to avoid skewing avg-RPE charts (would inflate) |
| Test modal cancel | "OK" button only on error | Forces acknowledgement |
| Test modal submit colors | SweetAlert defaults (blue confirm) | Not destructive |
| Muscle rest-day defaults | quads/hamstrings=3, glutes/chest/back/shoulders/biceps=2, core=1, neck/deep_neck=2 | Tuned to muscle damage recovery curves |
| Longevity muscles rest days | mostly 2-3 | Smaller deep muscles recover faster than they appear |
| Notification limit per session | 1 | Don't spam — even if 5 exercises have stale tests |
| Default splits count | 7 | One per day of week; covers all 6 fitness components |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `processTestResult(exerciseId, weight, reps)` validation + Epley | FULL (`OneRmLab.kt` + `OneRmLabScreen.kt`) | — |
| Epley formula `weight*(1+reps/30)` | FULL | — |
| 6-week re-test reminder | FULL (`Nudges.kt`) | — |
| Reminder callback: navigate, search, focus | FULL (`Nudges.kt` + `Nav.kt`) | — |
| `getExerciseById` library lookup | FULL (`engine/Library.kt`) | — |
| `showTestModal` UI with submit listener cloning | FULL (Compose dialog) | — |
| `muscleDatabase` 40+ muscles (major/longevity/hands/feet) | FULL (`assets/data/muscles.json` + `Library.kt`) | — |
| `workoutProgram.splits` 7-day component rotation | FULL (`Generator.kt` SPLITS) | — |
| Backend sync (POST test result) | MISSING | Native has no backend sync; everything is local-only |
| Test history RPE=7 to avoid chart skew | PARTIAL | RPE recorded correctly, but no audit check that warns when many test entries inflate averages |
| Test type tag (`isTest: true` on history) | FULL | — |

**Coverage: 73%** (8 FULL / 2 PARTIAL / 1 MISSING of 11 behaviours)

---

## H. Top 3 gaps to close

1. **Backend sync for test results is missing** — the reference POSTs each test result to a GAS endpoint for anonymous cross-user analytics. Native should at minimum export test results as part of the optional feedback bundle so power users can contribute data.
2. **No audit warning for test-inflated RPE averages** — the reference sets test entries to RPE 7, but neither reference nor native flags when ≥30% of recent entries are tests. Add a warning banner in `OneRMAuditScreen.kt`.
3. **6-week reminder fires only once per session** — fine for desktop but on mobile users may not re-open for weeks. Schedule a `WorkManager` periodic check (daily) that emits a notification when a test goes stale.
