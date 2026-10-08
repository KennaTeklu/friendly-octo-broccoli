# CHANGES.md — pro-batch-2a (vCode 14 / 1.4.4)

Three-column audit: every shipped item, its status, and the evidence (screenshot file, test, or command output). Legend: ✅ shipped · 📦 evidence artifact (not bundled: client personal data excluded per delivery instruction).

## P0 — HTML export cannot be imported (NaN)

| Item | Status | Evidence |
|---|---|---|
| Part 1 — `ProJson.json` accepts special floating-point values: `allowSpecialFloatingPointValues = true` added to the Json block in `core/Model.kt`; every pre-existing line kept | ✅ | `source/app/src/main/java/com/peakform/fitness/core/Model.kt` L36; tests `part1 - raw client file parses WITHOUT healing`, `part1 - quoted NaN becomes a real NaN double that survives decode` |
| Part 2 — every export path runs through `sanitize()`: walks the JsonElement tree and replaces `NaN` / `-NaN` / `Infinity` / `-Infinity` primitives with `JsonNull`; called in `exportFormatA`, `exportCompleteV1`, `exportBundleV1` | ✅ | `core/Backup.kt` L37–54, L94, L326; test `part2 - exportFormatA contains zero NaN or Infinity after sanitize`; native export of the client's imported data greps `0` for `NaN` (client file itself greps `12`) |
| Part 3 — `JsonHeal.heal()` replaces string-form special values in value positions (quoted `"NaN"` from the HTML writer AND bare `NaN`), key positions and prose untouched | ✅ | `core/JsonHeal.kt` `replaceSpecialValues()`; tests `part3 - heal replaces quoted and bare special values in value positions`, `part3 - quoted NaN used as a KEY is left alone`, `part3 - client file heals to zero value-position special floats` |
| **The exact client file `export_Kenna_September_28_2026.json` imports successfully** | ✅ 📦 | Test `client file imports through the Backup import decode path` (30 workouts, user Kenna); `p0-client-import-success.png` (toast), `p0-merge-report.png` |
| Imported data visible in Dashboard / History / Library | ✅ | `p0-dashboard-after-import.png`, `p0-history-after-import.png`, `p0-library-after-import.png` |
| Native export of imported data contains zero `NaN` and re-imports cleanly | ✅ 📦 | Test `round trip - native export of imported client data re-imports cleanly`; `grep -c NaN` on `export_format_A.json` → `0` (raw file excluded from zip — client data) |
| Paste a JSON containing the string `NaN` as averageRPE succeeds | ✅ | `feature-paste-json.png`; `PasteJsonScreen.kt` heals then imports |

## Parity — 12 import/export features

| Item | Status | Evidence |
|---|---|---|
| 1. CSV export of workout history — one row per logged set (date, exercise, set, reps, weight, RPE, note); SAF save + system share | ✅ 📦 | `core/Exporter.kt` `historyCsvPerSet()`; test battery asserts header + >50 rows; `feature-csv-editor.png` (the real generated CSV in a text-editor view); 302-row CSV generated (raw file excluded — client data) |
| 2. Custom exercise library export — JSON array of user-authored exercises | ✅ 📦 | `core/Studio.kt` `exportLibrary(ctx)`; artifact `docs/verify/artifacts/custom_exercises.json` (neutral: `[]` — test env has no user-authored exercises at export time); button visible in `feature-settings-data-backup.png` |
| 3. Custom exercise library import — heal first, merge not replace, added/updated/rejected counts | ✅ | `Studio.importLibrary()` with heal via `JsonHeal`; counts surfaced through the merge report (`p0-merge-report.png`); covered by unit battery |
| 4. Consent log export — JSON of every consent entry | ✅ 📦 | `core/Consent.kt` `exportLog(ctx)`; artifact `docs/verify/artifacts/consent_log.json` |
| 5. Feedback export — minimal feedback screen (textarea + submit) then export | ✅ 📦 | `ui/screens/FeedbackScreen.kt` + `Exporter.feedbackJson()`; `feature-feedback.png`; artifact `docs/verify/artifacts/feedback.json` |
| 6. Phrasebook export + import — merge on import with added/updated/rejected counts | ✅ 📦 | `core/Vocab.kt` `exportPhrasebook()` / import with merge counts; artifact `docs/verify/artifacts/phrasebook_L5.json`; buttons in `feature-settings-data-backup.png` |
| 7. Snapshot management screen — list with timestamp + size, tap to restore with confirm + pre-restore safety snapshot, delete, manual create | ✅ | `ui/screens/SnapshotsScreen.kt` + `core/Snapshots.kt`; `feature-snapshots-batch2a.png` shows populated list; pre-restore snapshot exercised in `bundleRoundTrip` test |
| 8. Pre-import guard dialog — confirm before ANY import, red confirm only | ✅ | `ui/Dialogs.kt` `ImportGuardDialog` (L85); `feature-import-guard.png` |
| 9. Paste-JSON import screen — text field, heal, import, merge report | ✅ | `ui/screens/PasteJsonScreen.kt`; `feature-paste-json.png`; merge report `p0-merge-report.png` |
| 10. Full app bundle export + import — one file with workouts, exercises, library, profiles, snapshots, settings, consent; restore after confirm | ✅ | `core/Backup.kt` `exportBundleV1` / `importBundleV1` (`PeakForm-App-Bundle-v1`); round trip test `bundleRoundTrip` (export → wipe → restore → 30 workouts back); `feature-bundle-before.png` / `feature-bundle-after.png` |
| 11. Merge report on every import — format detected, workouts added, exercises added, items rejected with reasons | ✅ | `ui/screens/PasteJsonScreen.kt` `MergeReportDialog` (L112) fed by `Backup.ImportResult`; `p0-merge-report.png`; test `feature11 - merge report counts added vs duplicates with reasons` |
| 12. Format B detection — `kind: try4ever-fitness-backup` with sibling fields accepted | ✅ | `core/Backup.kt` `detectFormat()` → `"try4ever-fitness-backup (Format B)"`; test `feature12 - Format B kind try4ever-fitness-backup is detected and imported` |

## UI placement (Settings → Data and Backup card, required order)

| Item | Status | Evidence |
|---|---|---|
| Buttons in spec order: Export full backup / Export workout JSON / Import backup / Export CSV / Export custom exercises / Import custom exercises / Export consent / Export feedback / Export phrasebook / Import phrasebook / Snapshots / Paste JSON / Export bundle / Import bundle | ✅ | `ui/screens/Settings.kt` DataBackupCard (L648–668) matches spec order exactly; `feature-settings-data-backup.png` |

## Repo hygiene

| Item | Status | Evidence |
|---|---|---|
| `source/keystore/debug.keystore` removed from the source tree; release signing resolves outside the tree (`-Ppro.keystore` → `$PRO_KEYSTORE` → repo-root `keystore/` → `~/.keys/`) | ✅ | `find source -name "*.keystore"` → empty; `app/build.gradle.kts` signingConfigs resolution order |
| `.gitignore` covers `.kotlin/` and `*.log`; stale `source/.kotlin/errors/errors-1791422469518.log` deleted | ✅ | `source/.gitignore`; `find source -name "*.log"` → empty |

## Build identity

| Item | Status | Evidence |
|---|---|---|
| versionCode 14, versionName 1.4.4, package com.peakform.fitness | ✅ | `aapt2 dump badging` in README §2 |
| Same signing cert as vCode 11–13 deliveries (upgrade-safe) | ✅ | `apksigner verify --print-certs` in README §3 — `1e08a903…` |
| APK hash differs from prior delivery `1AB357D2…` | ✅ | `sha256sum` in README §4 — `5e827c59…` |
