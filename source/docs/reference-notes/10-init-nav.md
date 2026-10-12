# 10 — Init, Restore, History Trap, Device Detection, Infinite Nav

> Reference module: `reference/scripts/app/10-init-nav.js` (940 lines).
> Native counterparts: `MainActivity.kt`, `core/ProState.kt`, `ui/Nav.kt`, `core/MigrationBridge.kt`.

This module owns the boot sequence: `getAllMuscleGroups` cache, `initializeMuscleTracking`, the persistent `navigationStack` (1000-deep with double-trap history), `DOMContentLoaded` boot, Dexie re-init, localStorage→Dexie migration, and last-section restore.

---

## E2. Deep patterns

### 2.1 `getAllMuscleGroups()` — cached muscle list
- **Inputs**: `muscleDatabase` (module 04).
- **Computation**: if `muscleGroupsCache` null → `muscleGroupsCache = [...major, ...longevity, ...hands, ...feet]`; return cache.
- **Output**: 50 muscle definitions.
- **Reasoning**: avoids re-concatenating on every call.

### 2.2 `initializeMuscleTracking()`
- **Inputs**: all muscle names.
- **Computation**: for each muscle, if `!muscleLastTrained[muscle.name]` → set to `null`.
- **Output**: `muscleLastTrained` map with explicit null entries.
- **Reasoning**: explicit null distinguishes "never trained" from "not in map" (missing key).

### 2.3 `navigationStack` — persistent nav history
- **Inputs**: localStorage `'workout_navigation_stack'`.
- **Computation**: on script load → parse stored array; if valid non-empty array → use; else default `['dashboard']`. Immediately write back to localStorage.
- **Output**: navigation stack array.
- **Reasoning**: survives page refresh — never loses history.

### 2.4 `getTopOffset()` — fixed navbar offset
- **Inputs**: none.
- **Computation**: returns `84` (navbar 70px + 14px breathing room).
- **Output**: scroll offset integer.
- **Reasoning**: anchor scrolling needs to account for fixed navbar.

### 2.5 `pagehide` event listener
- **Inputs**: navigationStack.
- **Computation**: on `pagehide` → `localStorage.setItem('workout_navigation_stack', JSON.stringify(navigationStack))`.
- **Output**: persisted stack on tab close/navigate away.
- **Reasoning**: belt-and-suspenders with the per-push save; covers tab close.

### 2.6 `DOMContentLoaded` boot sequence (10-stage)
1. **Restore and re-arm the history trap**: parse localStorage stack; if `lastSection !== top of stack` → push; write `replaceState` + `pushState` (double-trap with guard state).
2. **Immediately hide import screen**: `mainApp.style.display = 'block'`.
3. **Ensure Dexie ready**: if `db` undefined → create new Dexie('WorkoutApp') with 4-store schema.
4. **Migrate from localStorage**: if `db.workouts.count() === 0` AND `localStorage['workoutContinuityData']` exists → parse, bulkPut workouts/exercises/user/savedWorkout.
5. **Load all data**: `await loadAllData()` (module 01).
6. **Restore last visited section**: `sessionStorage.getItem('lastSection')` or default `dashboard` for fresh installs.
7. **Update navigation UI**: render nav bar with user name, streak, level.
8. **Initialize fuzzy search index**: build Fuse on library.
9. **Apply fatigue decay**: `applyFatigueDecay()`.
10. **Initial dashboard render**.
- **Output**: fully booted app.
- **Reasoning**: every step is defensive — try/catch swallows errors so boot never hangs.

### 2.7 History trap (double `pushState`)
- **Inputs**: current section id.
- **Computation**: `replaceState({stack, section, ts})` then `setTimeout(10ms)` → `pushState({...state, guard:true})`. The guard state catches back-button presses; on `popstate` if state has `guard:true` → re-push the trap.
- **Output**: back-button never leaves the app.
- **Reasoning**: web apps lose state on back-button; the trap ensures any back press stays in-app.

### 2.8 Section restoration logic
- **Inputs**: `workoutData.workouts.length`, `Object.keys(workoutData.exercises).length`, `sessionStorage.getItem('lastSection')`.
- **Computation**: `isFreshStart = workouts.length === 0 && exercises.length === 0`; if fresh → `lastSection = 'onboarding'` (or `'dashboard'` if onboarding done); else `lastSection = sessionStorage value || 'dashboard'`.
- **Output**: section to show on boot.
- **Reasoning**: brand-new users go to onboarding; returning users go to last section.

### 2.9 localStorage → Dexie migration
- **Inputs**: `localStorage['workoutContinuityData']` (legacy key).
- **Computation**: parse `{workouts, exercises, user, savedWorkout}`; bulkPut each into Dexie stores.
- **Output**: migrated data.
- **Reasoning**: one-time migration from old localStorage-only versions.

### 2.10 `initializeMuscleTracking` + cache rebuild
- **Inputs**: all muscle groups, `muscleLastTrained` map.
- **Computation**: ensure every muscle has an entry (null if never trained).
- **Output**: complete `muscleLastTrained` map.
- **Reasoning**: missing keys would crash recovery calc.

---

## E3. Edge cases handled

- **`muscleGroupsCache` null**: builds cache on first call.
- **`muscleLastTrained` missing a muscle**: `initializeMuscleTracking` adds it.
- **localStorage `'workout_navigation_stack'` missing or unparseable**: defaults to `['dashboard']`.
- **Stored stack empty array**: defaults to `['dashboard']`.
- **`window.location.hash` empty**: uses top of stack or `'dashboard'`.
- **Hash section doesn't exist in DOM**: falls back to `'dashboard'`.
- **Dexie already defined**: skips re-init.
- **`db.workouts.count() > 0`**: skips migration.
- **`localStorage['workoutContinuityData']` missing**: skips migration.
- **Legacy data unparseable**: try/catch swallows.
- **`loadAllData` fails**: would throw; outer try/catch in `DOMContentLoaded` swallows.
- **`sessionStorage.getItem('lastSection')` null**: defaults based on `isFreshStart`.
- **Fresh install (no workouts/exercises)**: routes to onboarding.
- **Fuse index build fails**: catches, search returns full library.
- **`pagehide` fires before stack saved**: belt-and-suspenders save catches it.
- **`history.replaceState` throws (sandboxed iframe)**: try/catch swallows.
- **`setTimeout` for guard state fails**: try/catch swallows.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Navigation stack cap | 1000 entries | Never "crowded" but practically unlimited |
| Top offset | 84 px | Navbar 70 + 14 breathing |
| Guard state delay | 10 ms | Lets replaceState settle before push |
| Boot hide import screen | immediate | Perceived performance |
| Dexie schema version | 1 | First release |
| Migration trigger | `workouts.count() === 0` | Only migrate on empty DB (avoid duplicates) |
| Fresh start route | onboarding | First-time UX |
| Returning user route | lastSection | Resume where left off |
| Fuzzy search threshold | 0.4 | Tolerates 2 typos |
| Search debounce | 200 ms | Performance vs responsiveness |
| History trap | double pushState | Catches back-button reliably |
| Boot error handling | try/catch each step | Never hang on boot |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `getAllMuscleGroups` cache | FULL (`Library.kt`) | — |
| `initializeMuscleTracking` | FULL (`ProState.kt`) | — |
| Persistent `navigationStack` (localStorage) | FULL (`Nav.kt` + `ProState.kt`) | — |
| Navigation stack cap 1000 | PARTIAL | Native uses Compose back-stack, unbounded but practically capped |
| `getTopOffset` (84px) | N/A (Compose, no fixed navbar) | — |
| `pagehide` save | N/A (Android lifecycle) | — |
| `DOMContentLoaded` 10-stage boot | FULL (`MainActivity.kt` onCreate) | — |
| Dexie schema (4 stores) | FULL (Room entities) | — |
| localStorage → Dexie migration | FULL (`MigrationBridge.kt`) | — |
| `loadAllData` boot sequence | FULL (`ProState.kt` hydrate) | — |
| Section restoration | FULL (`ProState.kt` lastRoute) | — |
| Fresh-start routing to onboarding | FULL (`Onboarding.kt`) | — |
| History trap (double pushState) | N/A (Android back handled by system) | — |
| Fuse fuzzy search | MISSING | Native uses `contains` filter; no fuzzy match |
| Search debounce | FULL | — |
| Boot error tolerance (per-step try/catch) | PARTIAL | Some steps wrapped, others not |

**Coverage: 80%** (11 FULL / 2 PARTIAL / 1 MISSING of 14 applicable behaviours; 2 N/A excluded)

---

## H. Top 3 gaps to close

1. **No fuzzy search** — `Library.kt` uses simple `contains` filter. Add a Levenshtein-based matcher or port Fuse.js so "sqaut" finds "squat".
2. **No cap on Compose back-stack** — practically fine but theoretically unbounded. Cap at 1000 entries to match reference behaviour; prevents memory growth on long sessions.
3. **Boot error tolerance is inconsistent** — some `MainActivity.onCreate` steps are not wrapped in try/catch. Wrap each step so a single failure (e.g. asset load error) doesn't crash boot.
