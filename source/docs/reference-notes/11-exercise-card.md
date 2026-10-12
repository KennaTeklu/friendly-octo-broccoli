# 11-exercise-card — Exercise card HTML build, test panel listeners, navigation, header updates

Source: `reference/scripts/app/11-exercise-card.js` (L60588–64705 of original `app.js`; 4,118 lines)

## A. File purpose
This module is the workout-session presentation layer. It (1) finalizes the sandbox-aware `showSection` override (iframe-safe nav with double-trap pushState); (2) builds the per-exercise card HTML (prescribed sets/reps/weight, inputs, RPE picker, skip/remove buttons); (3) wires test-panel listeners and rest-timer hooks; (4) updates the navigation header (`updateNavigation`, `updateHeaderNameAndStreak`) with first-name + streak + avatar initial; (5) handles import modals (`importWorkoutData`, `importExerciseData`) and the "start fresh program" destructive confirm.

## B. Public surface
- `showSection` override (sandbox-aware) — DOM-only nav with smooth-scroll, updates nav-item active state, fires per-section updaters (`updateDashboard`, `updateTodaysWorkout`, `renderLibrary`, `updateWorkoutHistory`, `updateProgressCharts`, `updateRecoverySection`, `loadSettingsForm`).
- `toggleMobileMenu()` — toggles `navMenu.active`.
- `updateNavigation()` — reads `workoutData.user.name` + `experience`, computes streak via `calculateStreak`, updates nav user name/avatar/stats.
- `updateHeaderNameAndStreak()` — `safeTextUpdate` helper (no-op if unchanged), updates `streakCount`/`userNameDisplay`/`document.title`/avatar initial. Default "Athlete" if name is empty or non-alphanumeric. Calls `updateStreakFire`.
- `importWorkoutData()` / `importExerciseData()` — open import modals.
- `startNewProgram()` — async SweetAlert confirm "All current data will be lost".
- Exercise-card HTML builders and test-panel listener attachers (used by `12-logging-inputs.js`).

## C. Dependencies
- Globals: `document`, `window`, `Swal`, `localStorage`, `workoutData`, `navigationStack`, `navMenu`, `sessionStorage`.
- Cross-module: `calculateStreak` (Stats), `updateStreakFire` (StreakFire), `updateDashboard`, `updateTodaysWorkout`, `renderLibrary`, `updateWorkoutHistory`, `updateProgressCharts`, `updateRecoverySection`, `loadSettingsForm`, `updateLongevityScoreDisplay`, `updateWorkoutHeader`, `updateResumeButtonVisibility`, `showModal`, `closeModal`.

## D. Native-equivalent mapping
- `showSection` → `MainActivity.kt::AppShell` (L226) + Compose `NavHost.navigate(route)` — Compose handles section switching natively; no `pushState` double-trap needed.
- `toggleMobileMenu` → `MainActivity.kt::MoreSheet` / `GlassDock` (Nav.kt L67) — ported as a bottom sheet.
- `updateNavigation` / `updateHeaderNameAndStreak` → `MainActivity.kt::TopBar` (L618) — name/streak/avatar live in the Compose top bar; reads from `ProState.data.user.name` and `Stats.calculateStreak`.
- "Athlete" fallback for empty / non-alphanumeric name → **GAP**. Native uses raw name with empty fallback; should port the alphanumeric check + neutral default.
- `importWorkoutData` / `importExerciseData` → `ui/screens/PasteJsonScreen.kt` (PasteJson) + `core/MigrationBridge.kt::ImportMerger` (L180) — ported.
- `startNewProgram` destructive confirm → `ui/Dialogs.kt` / `WorkoutLogger.kt::SwalDialog` (L399) — ported.

## E. Patterns worth porting
- **`safeTextUpdate` (only write if changed)** — micro-optimization to avoid redundant DOM writes; irrelevant in Compose (diffing handles this), but the philosophy maps to "only emit StateFlow updates when value changes".
- **Default "Athlete" when name is empty OR non-alphanumeric (emoji-only names)** — nice edge-case handling; native crashes/renders empty if name is `""`. Worth a one-line port.
- **Per-section updaters fired after navigation** (`updateDashboard`, `updateTodaysWorkout`, …) — Compose does this implicitly via `LaunchedEffect(route)`, but the explicit list is a good reminder of what needs recompute-on-show.
- **Streak failure → keep existing displayed value** rather than overwrite with 0 — defensive; matches `StreakFire.kt` philosophy.

## F. Feature IDs
`P11-SECTION-NAV-SANDBOX`, `P11-EXERCISE-CARD-BUILD`, `P11-TEST-PANEL-LISTENERS`, `P11-HEADER-NAME-STREAK`, `P11-ATHLETE-NAME-FALLBACK`, `P11-IMPORT-MODALS`, `P11-START-FRESH-CONFIRM`.
