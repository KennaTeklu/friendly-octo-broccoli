# 04 — Profiles (p4-user-reconcile-js; P4.Profiles in 15-inline)

> Reference module: `reference/scripts/p4/04-profiles.js` (552 lines).
> Source line range in p4-core.js: L31216-31745 (p4-user-reconcile-js IIFE).
> Note: `P4.Profiles` (multi-profile data model), `P4.Picker` (overlay UI), `P4.Emoji` (avatar picker) live in `15-inline.js` (L19931-L20890, "P4 STAGE-2 MODULES").
> Native counterparts: `core/Profiles.kt`, `core/ProfileState.kt`, `core/OnboardDraft.kt`, `ui/screens/ProfilesScreen.kt`.

## A. File purpose
Owns the **p4-user-reconcile-js** IIFE — a defensive fix that reconciles the user's name/birthDate/gender/weight across every storage layer (localStorage `workoutData`, Dexie `user` table, settings UI, navbar, dashboard) whenever any of them is written. Resolves conflicts by timestamp + source priority, then propagates the winner to every store + UI surface.

## B. Public surface
- `_p4_reconcile.readAllSources()` → `{name, birthDate, gender, weight, at}` from each store.
- `_p4_reconcile.merge(sources)` → picks the winner by timestamp + source priority.
- `_p4_reconcile.writeWinner(winner)` → writes back to every store.
- `_p4_reconcile.refreshUI()` → updates navbar, dashboard, settings.
- `_p4_reconcile.run()` → full pipeline.
- Hooks: `loadAllData` wrap, `appReady`, `importEverything` post-hook, save hook, settings-open hook, tab-visibility hook, boot double-run.

## C. Dependencies
- `workoutData` global, Dexie `user` table.
- `localStorage['workoutData']`, `localStorage['p4_profile_*']`.
- DOM: `#userNameDisplay`, `#navUserName`, `#headerNameStreak`.
- `P4.debounce` (from `01-core.js`).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| Multi-source reconciliation | `Profiles.reconcile()` (single Room source — less conflict surface) |
| `loadAllData` hook | `ProState.load()` |
| `importEverything` hook | `BackupSystem.importAll()` post-hook |
| Settings-open reconciliation | `ProfilesScreen.onStart()` |
| Tab-visibility reconcile | `MainActivity.onResume()` |
| Boot double-run | `ProState.init()` + delayed re-check |

## E. Patterns worth porting
- **Multi-source conflict resolution**: when two stores disagree, the winner is determined by `(timestamp, source-priority)` tuple. Native uses Room as the single source of truth but should still expose `Profiles.reconcile()` for the legacy-import path.
- **Boot double-run**: runs reconcile once at 0ms (synchronous) and again at 6s (after `loadAllData` settles). Catches race conditions where Dexie async writes land after the first run.
- **MutationObserver fallback**: watches `#settings-section` for re-renders and reconciles whenever the section becomes active without a `showSection` call.

## E2. Deep patterns
- **Source priority**: `dexie > localStorage > navbar-display`. The Dexie record always wins on a timestamp tie because it is the canonical persisted store.
- **Save stamping**: every save writes `at: Date.now()` into the record so the next reconcile can compare timestamps.
- **Safety net**: if `loadAllData` never fires (e.g. a script error), a 6s `setTimeout` rebroadcasts the reconcile.

## E3. Edge cases
- All sources empty → no winner, no-op.
- Two sources have the same timestamp → source priority breaks the tie.
- Save fails (quota) → reconcile still fires with the in-memory value; UI shows the latest input even if persistence lagged.
- Tab visibility = hidden → skip (avoid wasteful DOM writes).

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Reconcile on tab focus | true | Catches cross-tab edits |
| Reconcile on settings open | true | User expects to see the latest name they typed anywhere |
| Boot double-run delay | 6s | Generous buffer for slow Dexie loads |
| No user-visible "conflict" UI | true | Silent winner — user never sees the disagreement |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Multi-source reconciliation | PARTIAL (single Room source — no conflict) |
| Boot double-run safety net | MISSING (native loads synchronously) |
| Save stamping (at: timestamp) | FULL (Room `updatedAt` column) |
| Tab-visibility reconcile | FULL (`MainActivity.onResume`) |
| Settings-open reconcile | FULL (`ProfilesScreen.onStart`) |
| MutationObserver fallback | MISSING (Compose recomposition is automatic) |

**Coverage: 65%**

## H. Top 3 gaps to close
1. **Cross-device reconciliation** — when a user signs in on a second device, the cloud sync should run the same reconcile logic. Native currently has no cloud sync; add a `Profiles.reconcileWithCloud()` flow tied to Play Games Services or Drive AppData.
2. **Conflict resolution UI** — silent winner is good UX for trivial fields (name), but risky for `weight` (a mismerge could corrupt progress charts). Add an opt-in "review conflicts" modal for weight/birthDate.
3. **MutationObserver equivalent** — Compose recomposition handles this automatically, but legacy XML screens do not. Add a `LiveEvent` observer in `ProfilesScreen` that re-reads on `onResume` AND on `Flow<Profile>` emit.
