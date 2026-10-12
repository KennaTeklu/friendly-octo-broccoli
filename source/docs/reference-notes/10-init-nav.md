# 10-init-nav — Init, restore, history trap, infinite navigation, muscle tracking

Source: `reference/scripts/app/10-init-nav.js` (L59656–60588 of original `app.js`)

## A. File purpose
This module is the bootstrapper for the live app: it caches the muscle group list, initializes muscle-tracking state, declares global singletons (`currentWorkout`, `muscleLastTrained`, `charts`, `fuseIndex`, `collapsedGroups`), implements the **persistent navigation stack** (localStorage-backed, capped at 1000, restored on `DOMContentLoaded`, with a "double trap" `pushState` + guard state to prevent back-button escapes), and exposes DOM-element references used by every other module.

## B. Public surface
- `muscleGroupsCache`, `getAllMuscleGroups()` — concatenation of `muscleDatabase.{major, longevity, hands, feet}`.
- `initializeMuscleTracking()` — seeds `muscleLastTrained[name] = null` for every muscle.
- Globals: `currentWorkout`, `lastWorkoutSignature`, `fuseIndex`, `savedWorkout`, `STORAGE_KEY_WORKOUT`, `muscleLastTrained`, `charts`, `lastRenderedWorkoutId`, `currentHistoryFilter`, `currentGrouping`, `collapsedGroups`, `librarySearchTimeout`.
- `navigationStack` — persistent array (localStorage `workout_navigation_stack`, capped at 1000).
- `saveLibraryCollapse()` — persists `collapsedGroups` to localStorage.
- `getTopOffset()` — returns `84` (navbar height + breathing room) for scroll offset.
- `DOMContentLoaded` handler — restores nav stack, re-arms history trap, hides import screen, opens Dexie, migrates from localStorage if needed.
- `pagehide` listener — saves nav stack on tab close.
- DOM refs: `mainApp`, `navMenu`.

## C. Dependencies
- Globals: `localStorage`, `document`, `window`, `Dexie`, `muscleDatabase` (defined in 05/06 area).
- Cross-module: none directly; downstream modules consume the globals declared here.

## D. Native-equivalent mapping
- Bootstrapping → `MainActivity.kt::MainActivity` (L74) + `core/ProState.kt::init` (L66) + `loadAll` (L116) — ported (Android `ComponentActivity.onCreate`).
- `navigationStack` + history trap → `ui/Nav.kt::NavHost` / `MainActivity.kt::AppShell` (L226) — native uses Compose `NavHost` back-stack instead. The "double trap" pattern (push guard state after each `pushState`) is **GAP** — Android handles back via `OnBackPressedDispatcher`, but the deep-link restoration of last section is mirrored via `ProfileState.get(ctx, K_LAST_SECTION)`.
- `muscleGroupsCache` / `initializeMuscleTracking` → `engine/Library.kt::Library.allMuscleGroups` (L290) + `Fatigue.initializeMuscleFatigue` (L376) — ported.
- `currentWorkout` / `muscleLastTrained` globals → `ProState.data` (single source of truth) + `Fatigue.calculateMuscleLastTrained` (L576) — ported, no globals.
- `collapsedGroups` persistence → `ProfileState` (per-profile prefs) — ported, more robust.
- Dexie IndexedDB → `ProStore` / `DataStore<WorkoutData>` — ported.

## E. Patterns worth porting
- **Navigation stack capped at 1000 entries with localStorage persistence** is overkill on Android (Compose back-stack handles this), but the underlying idea — "user should never lose their place after a force-close" — is worth mirroring: persist `lastSection` to `ProfileState` (native already does this) but also a small back-stack of the last 5-10 visited screens for a "jump back" affordance.
- **`pagehide` save handler** — Android has `onStop` / `onSaveInstanceState`; ensure the equivalent "save draft on background" runs synchronously.
- **`getTopOffset()` constant** — a single source of truth for "navbar height + breathing room" used by every scroll-into-view call. Native Compose has `Scaffold` padding parameters; equivalent is to centralize one constant.

## F. Feature IDs
`P10-MUSCLE-GROUPS-CACHE`, `P10-MUSCLE-TRACKING-INIT`, `P10-NAVIGATION-STACK`, `P10-HISTORY-TRAP`, `P10-BOOTSTRAP-RESTORE`, `P10-COLLAPSED-GROUPS-PERSIST`.
