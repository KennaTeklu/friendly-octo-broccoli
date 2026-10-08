# PARITY.md — native ↔ HTML import/export surface (Batch 2A)

Goal: the native app (this APK) exposes the same import/export surface as the legacy single-file HTML app (`cautious-enigma/index.html`). HTML line anchors from BATCH-2A.md are kept for traceability. Everything below ships in vCode 14.

## Mapping table

| # | Feature | HTML reference | Native implementation | Notes |
|---|---|---|---|---|
| — | P0: accept HTML exports with special floats | HTML writer emits `"averageRPE": "NaN"` | `core/Model.kt` `allowSpecialFloatingPointValues = true` + `core/JsonHeal.kt` `replaceSpecialValues()` + `core/Backup.kt` `sanitize()` on every export | Import tolerant AND export never emits NaN again |
| 1 | CSV export of workout history | `exportHistoryCSV` (L41547) | `core/Exporter.kt` `historyCsvPerSet()` — one row per logged set: date, exercise, set, reps, weight, RPE, note; SAF save + system share | CSV quoting per RFC 4180 (`csvField`) |
| 2 | Custom exercise library export | `exportExerciseData` (L37804), `P4.Backup.exportLibrary` (L55972) | `core/Studio.kt` `exportLibrary(ctx)` — JSON array of user-authored exercises | |
| 3 | Custom exercise library import | `importExerciseData` (L37400), `importExerciseLibrary` (L37798) | `core/Studio.kt` `importLibrary()` — `JsonHeal` first, merge (never replace), reports added / updated / rejected | |
| 4 | Consent log export | `P4.Backup.exportConsent` (L56478) | `core/Consent.kt` `exportLog(ctx)` — JSON of every consent entry | |
| 5 | Feedback export | `P4.Studio.exportFeedback` (L57347) | `ui/screens/FeedbackScreen.kt` (textarea + submit) + `core/Exporter.kt` `addFeedback` / `feedbackJson` | Feedback UI built, not assumed |
| 6 | Phrasebook export + import | `exportPhrasebook` (L57657), `importPhrasebook` (L57668) | `core/Vocab.kt` export + merge-import with added / updated / rejected counts | |
| 7 | Snapshot management screen | `P4.Backup.snapshots` (L56023), `restoreSnapshot` (L56024), `snapshotNow` (L58796) | `ui/screens/SnapshotsScreen.kt` + `core/Snapshots.kt` — list with timestamp + size, tap-to-restore with confirm + pre-restore safety snapshot, delete, manual create | |
| 8 | Pre-import guard dialog | `P4.Studio.guardImport` (L57405) | `ui/Dialogs.kt` `ImportGuardDialog` — confirm before any import, proceeds only on red confirm | |
| 9 | Paste-JSON import screen | `P4.Studio.submitImport` (L57506) | `ui/screens/PasteJsonScreen.kt` — text field → heal → import → merge report | |
| 10 | Full app bundle export + import | `exportBundle` (L68168), `importBundle` (L68172) | `core/Backup.kt` `exportBundleV1` / `importBundleV1` — one file (`PeakForm-App-Bundle-v1`) with workouts, exercises, library, profiles, snapshots, settings, consent; restore after confirm | Round trip verified by test: export → wipe → import → identical workout count |
| 11 | Merge report on every import | (legacy merge summary) | `ui/screens/PasteJsonScreen.kt` `MergeReportDialog` fed by `Backup.ImportResult`: format detected, workouts added, exercises added, items rejected with reasons | Surfaced as a dialog on every import path |
| 12 | Format B detection | `kind: try4ever-fitness-backup` + sibling fields | `core/Backup.kt` `detectFormat()` → `"try4ever-fitness-backup (Format B)"` → direct import path | Unit test covers detect + import |

## UI order (Settings → Data and Backup card)

Exactly as specified in BATCH-2A.md:

1. Export full backup (Complete-v1) — existing
2. Export workout JSON (Format A) — existing
3. Import backup — existing
4. Export workout history as CSV — new
5. Export custom exercises — new
6. Import custom exercises — new
7. Export consent log — new
8. Export feedback — new
9. Export phrasebook — new
10. Import phrasebook — new
11. Snapshots — new, opens screen
12. Paste JSON to import — new
13. Export full app bundle — new
14. Import full app bundle — new

## Healing pipeline (shared by every import path)

1. Smart quotes → ASCII, ellipsis → `...`
2. Markdown code fences stripped
3. Trailing commas removed (string-aware)
4. Special floating-point values (`NaN`, `-NaN`, `Infinity`, `-Infinity`, quoted or bare, in VALUE positions) → `null` — keys and prose untouched
5. Format sniffing: bare array / `{exercises:[...]}` / `{items:[...]}` / Format B kind tag / Format A / Complete-v1 / bundle

Then `ProJson.json` (lenient, coerce, special-float-accepting) decodes. Every export path runs the produced tree through `Backup.sanitize()` so a native export is always spec-clean JSON.
