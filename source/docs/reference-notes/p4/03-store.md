# 03 — Store (P4.Backup + p4DataSchemaJs; P4.store helpers in 01-core)

> Reference module: `reference/scripts/p4/03-store.js` (490 lines).
> Source line ranges in p4-core.js: L14761-15130 (P4 BACKUP IIFE), L33119-33212 (p4DataSchemaJs + P4Backup module).
> Note: `P4.store` helpers (get/set/del/snapshot) live in `01-core.js` (L13313-13369) — they share closure scope with `SEC` and the `P4` namespace.
> Native counterparts: `core/ProStore.kt`, `core/BackupSystem.kt`, `core/Backup.kt`, `core/Snapshots.kt`.

## A. File purpose
Owns the higher-level persistence modules: **P4 BACKUP IIFE** (one-tap export, end-of-day reminder, after-workout nudge, restore snapshot, snapshot rotation) and **p4DataSchemaJs** (the data schema constants + `P4Backup` module that defines the export JSON envelope and field whitelist).

## B. Public surface
- `P4.Backup.init()` — wires the daily reminder + after-workout auto-snapshot.
- `P4.Backup.exportAll()` / `exportEverything()` — exports the unified state bundle.
- `P4.Backup.importAll()` / `importEverything()` — restores from a bundle.
- `P4.Backup.snapshot()` — captures a rolling localStorage snapshot (last 3).
- `P4.Backup.restoreSnapshot(i)` — restores snapshot `i`.
- `P4Backup.SCHEMA_VERSION`, `P4Backup.STATE_KEYS` — whitelist of state keys.

## C. Dependencies
- `window.P4`, `P4.store`, `P4.SEC` (from `01-core.js`).
- `P4.toast`, `P4.download`, `P4.fmtDate`.
- `workoutData` global, Dexie tables.
- `P4.Paywall.isPro()` (for pro-gated backup features).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `P4.Backup.exportAll` | `BackupSystem.exportAll()` → JSON file |
| `P4.Backup.importAll` | `BackupSystem.importAll(uri)` |
| `P4.Backup.snapshot` (rolling 3) | `Snapshots.rotate()` (Room table) |
| `P4Backup.STATE_KEYS` | `BackupSystem.STATE_KEYS` constant |
| Daily reminder | `Notify.scheduleDailyReminder()` |

## E. Patterns worth porting
- **Unified whitelist**: `STATE_KEYS` is a single source of truth for what gets exported. Adding a new persisted key only requires updating the whitelist.
- **End-of-day reminder**: if no backup has been made in 24h, the next time the app is opened a toast appears.
- **After-workout nudge**: each completed workout triggers a snapshot write — no user action needed.

## E2. Deep patterns
- **Prefix scan**: `P4Backup.STATE_KEYS` includes both literal keys (`p4_theme`) and prefix patterns (`p4_profile_*`). The exporter walks `localStorage` once and matches each key against the whitelist.
- **Healed import**: if the imported JSON has trailing commas or stray markdown fences, `SEC.healJson` (from `01-core.js`) repairs it before parse.
- **Backup timestamp**: every backup writes `p4_last_backup` so the reminder can compute "24h since last backup" without scanning the file system.

## E3. Edge cases
- Import bundle for a different schema version → `P4Backup.SCHEMA_VERSION` mismatch aborts with a toast.
- `localStorage` quota exceeded during snapshot → `store.set` catches and toasts "Storage full — export a backup".
- Importing into a session with active workout → confirms via `P4.SEC.clampStr` + Swal dialog before overwriting.

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Snapshot count | 3 | Enough history, not too much storage |
| Reminder cadence | 24h | Daily is annoying; weekly is too risky |
| After-workout snapshot | automatic | User should not have to think about it |
| Export filename | `peakform-backup-YYYY-MM-DD.json` | Sortable, date-stamped |

## G. Audit score
| Behavior | Coverage |
|---|---|
| One-tap export | FULL |
| One-tap import (with heal) | FULL |
| Rolling 3-snapshot rotation | FULL (Room-backed `Snapshots`) |
| End-of-day reminder | PARTIAL (native uses WorkManager, less precise) |
| After-workout auto-snapshot | FULL |
| Unified STATE_KEYS whitelist | FULL |
| Schema version migration | MISSING (native has no migration on import) |

**Coverage: 85%**

## H. Top 3 gaps to close
1. **Schema version migration on import** — `P4Backup.SCHEMA_VERSION` is checked but never migrated. Native should add a `migrate(oldSchema, newSchema)` step that upgrades old bundles to the current shape.
2. **Encrypted backup option** — the export is plaintext JSON. Native could offer an AES-encrypted variant (password-derived key) for users who sync to cloud storage.
3. **Cross-profile backup merge** — `importEverything` overwrites the active profile only. Add a "merge into profile X" flow so users can import a friend's bundle as a new profile without losing their own.
