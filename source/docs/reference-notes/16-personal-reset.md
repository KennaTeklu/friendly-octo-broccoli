# 16 — Personal Info Save, Reset Workflow, Wipe, Import Restore

> Reference module: `reference/scripts/app/16-personal-reset.js` (1 237 lines).
> Native counterparts: `ui/screens/Settings.kt`, `core/Profiles.kt`, `core/ProfileState.kt`, `core/MigrationBridge.kt`.

This module owns: `savePersonalInfo` (validate + persist user profile), settings form parsing, menstrual/postpartum data handling, reset/wipe workflow, and import-restore from JSON.

---

## E2. Deep patterns

### 2.1 `savePersonalInfo()` — validation pipeline
- **Inputs**: 10 DOM inputs (name, birthDate, gender, weight, height, experience, goal, restTime, progression, workoutDays).
- **Computation** (6-stage validation):
  1. **Birth date**: if provided and `birth > today` → error "Birth date cannot be in the future."
  2. **Weight**: if provided, parse float; if `isNaN || ≤ 0` → error "Weight must be a positive number."
  3. **Height**: same as weight.
  4. **Rest time**: `parseInt`; if `isNaN || < 30 || > 300` → error "Rest time must be between 30 and 300 seconds."
  5. **Progression rate**: `parseFloat`; if `isNaN || < 0.5 || > 5` → error "Progression rate must be between 0.5% and 5%." Else divide by 100 (decimal).
  6. If any errors → `alert('Please fix the following:\n- ' + errorMessages.join('\n- '))` and return.
- **Output**: validated values or alert.
- **Reasoning**: range validation prevents nonsensical inputs (0-second rest, 50% progression rate).

### 2.2 Settings object assembly
- **Inputs**: validated values, DOM checkboxes.
- **Computation**: `workoutData.user.settings = {workoutDays: Array.from(querySelectorAll('.workout-day:checked')).map(cb => cb.value), restTime, progressionRate, theme: localStorage.getItem('workoutTheme') || 'blue', darkMode: localStorage.getItem('workoutDarkMode') === 'true'}`.
- **Output**: settings object.
- **Reasoning**: theme/darkMode read from localStorage (separate from profile) so theme persists across profile swaps.

### 2.3 Menstrual data handling
- **Inputs**: `lastPeriodDate` input, `cycleLength` input.
- **Computation**: if both inputs exist AND `lastPeriod` is set:
  - Ensure `workoutData.user.menstrual` exists with `{symptoms:[]}`.
  - Set `lastPeriodStart = lastPeriod`.
  - If `cycleLength` is valid (20-40) → store.
- **Output**: menstrual data persisted.
- **Reasoning**: cycle length outside 20-40 is medically unusual; silently ignored (no error) to avoid alarming users.

### 2.4 Postpartum months handling
- **Inputs**: `settingsPostpartum` input, gender.
- **Computation**: if `gender === 'female' && !isNaN(ppVal) && ppVal ≥ 0` → `workoutData.user.postpartumMonths = ppVal`; else `delete workoutData.user.postpartumMonths`.
- **Output**: postpartum field set or removed.
- **Reasoning**: postpartum only applies to females; removing the field for males/invalid keeps the schema clean.

### 2.5 `muscleCoupling` toggle
- **Inputs**: `muscleCouplingToggle` checkbox.
- **Computation**: `workoutData.user.settings.muscleCoupling = checkbox.checked`.
- **Output**: coupling flag persisted.
- **Reasoning**: opt-in feature — users can disable if they train antagonist splits.

### 2.6 `bottomNavAutoHide` toggle
- **Inputs**: checkbox.
- **Computation**: `workoutData.user.settings.bottomNavAutoHide = checkbox.checked`.
- **Output**: nav behavior flag.
- **Reasoning**: power users want max screen real estate; auto-hide on scroll.

### 2.7 Goal/experience change → regenerate prompt
- **Inputs**: old goal, old experience, new values.
- **Computation**: if `goal !== workoutData.user.goal || experience !== workoutData.user.experience` → `confirm('Your training goal or experience level has changed. Would you like to generate a new workout based on your updated preferences?')` → on true → `generateNextWorkout()`.
- **Output**: optional workout regen.
- **Reasoning**: goal change invalidates current prescription; offer fresh start.

### 2.8 First-time workout generation
- **Inputs**: `workoutData.workouts.length`, `currentWorkout`.
- **Computation**: if `workouts.length === 0 && !currentWorkout` → `generateNextWorkout()`.
- **Output**: first workout generated.
- **Reasoning**: brand-new users shouldn't see an empty workout screen.

### 2.9 Cache + UI update post-save
- **Inputs**: saved user.
- **Computation**: `updateUserCache()`; if `currentWorkout` → `updateWorkoutCache()`; `saveToLocalStorage()`; `updateNavigation()`; `updateLongevityScoreDisplay()`; `updatePeriodButtonVisibility()`.
- **Output**: caches rebuilt, UI refreshed.
- **Reasoning**: demographic changes invalidate caches; longevity score depends on body weight.

### 2.10 Reset/wipe workflow
- **Inputs**: confirm dialogs.
- **Computation**:
  1. Confirm "This will delete all workouts, exercises, and settings. Cannot be undone."
  2. If confirmed → clear `localStorage` keys (`workout_app_version`, `deviceId`, `smartMessageHistory`, `p4_library_collapsed`, `workout_navigation_stack`, `workoutContinuityData`, `workoutEmergencyBackup`).
  3. Clear Dexie tables (`workouts`, `exercises`, `user`, `savedWorkout`).
  4. Reset `workoutData` to defaults.
  5. Reload page.
- **Output**: factory-reset state.
- **Reasoning**: destructive operation requires explicit confirm; clears all localStorage + IndexedDB.

### 2.11 Import-restore from JSON
- **Inputs**: JSON file (uploaded or pasted).
- **Computation**: parse JSON; validate schema (must have `workouts[]`, `exercises{}`, `user{}`); bulkPut into Dexie; reload `loadAllData()`.
- **Output**: imported state.
- **Reasoning**: allows cross-device migration and backup restore.

---

## E3. Edge cases handled

- **Birth date in future**: validation error; save blocked.
- **Weight = 0 or negative**: validation error.
- **Height = 0 or negative**: validation error.
- **Rest time < 30 or > 300**: validation error.
- **Progression rate < 0.5 or > 5**: validation error.
- **Name empty**: allowed (`workoutData.user.name = name || ''`).
- **No workout days checked**: `workoutDays = []` (allows — user can train ad-hoc).
- **`lastPeriodDate` empty**: menstrual data not updated.
- **`cycleLength` outside 20-40**: silently ignored (no error).
- **`settingsPostpartum` empty**: `ppVal = NaN`; condition `!isNaN(ppVal)` fails; field deleted.
- **Gender male with postpartum set**: field deleted.
- **`muscleCouplingToggle` missing**: no error (graceful).
- **`bottomNavAutoHide` missing**: no error.
- **`currentWorkout` null when `updateWorkoutCache` called**: skipped (called inside `if`).
- **Goal/experience unchanged**: no regen prompt.
- **No workouts + currentWorkout exists**: no first-time gen (current workout kept).
- **Reset confirm cancelled**: no action.
- **Import JSON invalid**: try/catch swallows; alert "Invalid backup file".
- **Import JSON missing required keys**: validation fails; alert.
- **`updateLongevityScoreDisplay` not defined**: `typeof === 'function'` guard.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Rest time range | 30-300 seconds | 30s minimum (hypertrophy), 300s max (powerlifting) |
| Progression rate range | 0.5%-5% | Below 0.5% is imperceptible; above 5% is dangerous |
| Birth date future | blocked | Nonsensical |
| Weight/height ≤ 0 | blocked | Nonsensical |
| Name empty | allowed | Privacy — user may not want to provide name |
| Cycle length range | 20-40 days | Population range; outside = medical concern |
| Postpartum only for females | Yes | Biological relevance |
| Postpartum negative | blocked | Nonsensical |
| Reset confirm | double-confirm (alert + window.confirm) | Destructive operation |
| Reset clears ALL localStorage keys | Yes | Including deviceId, smartMessageHistory, navigation stack |
| Reset reloads page | Yes | Ensures clean state |
| Import validates schema | Yes | Prevents corrupt imports |
| Goal/experience change → regen prompt | confirm | Fresh prescription on goal change |
| First-time auto-gen | Yes | No empty workout screen for new users |
| Validation error format | `alert` with bullet list | Scannable |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `savePersonalInfo` 6-stage validation | FULL (`Settings.kt`) | — |
| Birth date future check | FULL | — |
| Weight/height positive check | FULL | — |
| Rest time range (30-300) | FULL | — |
| Progression rate range (0.5-5%) | FULL | — |
| Settings object assembly | FULL | — |
| Menstrual data handling (cycleLength 20-40) | FULL (`MyCycle.kt`) | — |
| Postpartum months (female only) | FULL | — |
| `muscleCoupling` toggle | FULL | — |
| `bottomNavAutoHide` toggle | FULL | — |
| Goal/experience change → regen prompt | FULL | — |
| First-time workout generation | FULL (`Onboarding.kt`) | — |
| Cache + UI update post-save | FULL | — |
| Reset/wipe workflow (clear all) | FULL (`Settings.kt` danger zone) | — |
| Reset double-confirm | FULL | — |
| Import-restore from JSON | FULL (`MigrationBridge.kt` + `PasteJsonScreen.kt`) | — |
| Import schema validation | FULL | — |
| Validation error alert format | PARTIAL | Native uses inline errors per field, not a single alert |

**Coverage: 94%** (17 FULL / 1 PARTIAL / 0 MISSING of 18 behaviours)

---

## H. Top 3 gaps to close

1. **Validation errors are inline per-field, not a single alert** — native shows errors next to each field (better UX) but doesn't aggregate them on save. Keep inline errors but add a summary toast on save attempt: "3 fields need attention".
2. **Reset doesn't clear `deviceId`** — native reset preserves the device ID for analytics continuity. The reference clears it. Decide: if analytics continuity matters, keep deviceId; if true factory reset is the goal, clear it.
3. **Import doesn't validate workout date ranges** — a malicious/corrupt import with future-dated workouts would skew recovery calculations. Add a date-range validator (reject entries > 1 year in future).
