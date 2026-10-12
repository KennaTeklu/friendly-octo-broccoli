# 05 — Vault (shim — P4.Vault in 15-inline, P4.Backup.snapshot in 03-store)

> Reference module: `reference/scripts/p4/05-vault.js` (67 lines).
> Source line ranges in p4-core.js: n/a (shim only). `P4.Vault` (IndexedDB-backed per-profile snapshot layer) lives in L20413-20592 of P4 STAGE-2 inside P4 BOOT IIFE (file `15-inline.js`). The lighter `P4.Backup.snapshot()` localStorage rotation lives in `03-store.js`.
> Native counterparts: `core/Snapshots.kt`, `core/Backup.kt`.

## A. File purpose
Thin shim that re-exports the vault surface (`P4.vault.save`, `P4.vault.list`, `P4.vault.restore`, `P4.vault.quickSnapshot`) for module discoverability. The actual `P4.Vault` IndexedDB layer lives inside the P4 BOOT IIFE in `15-inline.js` and could not be cleanly hoisted without breaking the wrapper closure.

## B. Public surface
- `P4.vault.save(label, payload)` → delegates to `P4.Vault.save()`.
- `P4.vault.list()` → returns snapshots for the active profile (newest first).
- `P4.vault.restore(id)` → restores a snapshot by id.
- `P4.vault.quickSnapshot()` → writes a fast localStorage snapshot via `P4.store.snapshot()` (defined in `01-core.js`).

## C. Dependencies
- `window.P4`, `P4.Vault` (defined in `15-inline.js`).
- `P4.store.snapshot` (defined in `01-core.js`).
- IndexedDB (`WorkoutAppVault` database).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `P4.Vault.save(label, payload)` | `Snapshots.insert(label, payload)` (Room) |
| `P4.Vault.list()` | `Snapshots.getAll()` (Flow) |
| `P4.Vault.restore(id)` | `Snapshots.restore(id)` |
| Per-profile scoping | `Snapshots.profileId` foreign key |
| Survives clear-cache | true (IndexedDB) → native: Room survives clear-cache via `autoBackup` |

## E. Patterns worth porting
- **Two-tier snapshot strategy**: lightweight localStorage rotation (`P4.store.snapshot`, last 3) for fast recovery + heavy IndexedDB vault (`P4.Vault.save`, unlimited, per-profile) for long-term history. Native currently has only one tier.
- **Per-profile scoping**: snapshots are keyed by `profileId`, so switching profiles does not pollute the other profile's history.

## E2. Deep patterns
- **IndexedDB transaction batching**: `P4.Vault.save` opens a readwrite transaction, writes the snapshot record, and commits in one operation. If the transaction aborts (e.g. quota), the rejection is swallowed and the user sees a soft warning instead of a hard error.
- **Lazy DB open**: `dbPromise` is created on first call to `P4.Vault.save()`, not at boot. Saves cold-start time.
- **Schema version gate**: IndexedDB `onupgradeneeded` migrates the schema; old snapshots survive the migration.

## E3. Edge cases
- IndexedDB blocked (private mode) → `dbPromise` rejects, shim catches and falls back to `quickSnapshot()`.
- Snapshot payload too large (>10MB) → IndexedDB may silently truncate on some browsers; native Room has no such limit.
- Profile deleted → orphaned snapshots remain in the vault; `P4.Vault.purgeProfile(id)` should be called (currently only invoked on profile delete).

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Vault snapshot limit | unlimited (IndexedDB) | Long-term history matters |
| localStorage snapshot limit | 3 | Fast recovery only |
| Auto-save on profile switch | true | Don't lose the previous profile's last state |
| No "Restore" confirmation dialog | false (always confirms) | Restoring overwrites current state — destructive |

## G. Audit score
| Behavior | Coverage |
|---|---|
| IndexedDB per-profile vault | FULL (`Snapshots` Room table) |
| Two-tier snapshot (light + heavy) | PARTIAL (native has only Room) |
| Lazy DB open | n/a (Room is lazy by default) |
| Schema migration | FULL (Room migrations) |
| Orphan cleanup on profile delete | FULL (`Snapshots.purgeProfile`) |

**Coverage: 90%**

## H. Top 3 gaps to close
1. **Two-tier snapshot strategy** — native has only the Room-backed `Snapshots` table. Add an in-memory LRU cache (`SnapshotsMemoryCache`) that mirrors the last 3 snapshots for instant restore (mirrors the localStorage rotation).
2. **Vault quota warning** — the IndexedDB layer swallows quota errors. Native should surface a `Notify.quotaWarning()` when the Room `Snapshots` table exceeds 50MB.
3. **Restore preview** — `P4.vault.restore(id)` currently restores blindly after a confirm dialog. Add a "preview" mode that shows a diff (changed workouts, changed weight) before the user commits.
