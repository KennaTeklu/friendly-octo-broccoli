# BATCH-2A.md — Import/Export fixes + full parity

Predecessor: Batch 1 (vCode 11), Hotfix 1.1 (vCode 12), Batch 2 (vCode 13). Ship as versionCode 14, versionName 1.4.4.

---

## P0 — CRITICAL FIX: HTML export cannot be imported

### Symptom (confirmed by client log on Samsung SM-A145M, Android 14)

Client imports an HTML-app-exported JSON. Import fails with:

    [BACKUP] parse of workoutData failed: Unexpected JSON token at offset 25180:
    Unexpected special floating-point value NaN. By default, non-finite
    floating point values are prohibited because they do not conform JSON
    specification at path: $.workouts[8].summary.averageRPE

### Root cause

The HTML export writes the string "NaN" as the value of averageRPE. ProJson in the native app has isLenient = true and coerceInputValues = true, which together make kotlinx.serialization parse the string "NaN" as a special floating-point value. It then rejects it because allowSpecialFloatingPointValues defaults to false.

To confirm on the client file, run:

    grep -c "averageRPE\": \"NaN\"" export_Alex_September_28_2026.json

It appears many times in the real client export.

### Fix — three parts, all required

**Part 1 — accept special floats on import.** Edit app/src/main/java/com/peakform/fitness/core/Model.kt, in object ProJson, in the val json: Json = Json { ... } block. Add exactly one line: allowSpecialFloatingPointValues = true. Keep every existing line. The full block becomes:

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
        explicitNulls = false
        prettyPrint = false
        allowSpecialFloatingPointValues = true
    }

**Part 2 — normalize before export.** Every export path (Backup.exportFormatA, Backup.exportCompleteV1, and any new exporter added in this batch) must run the resulting JsonObject through a sanitizer that walks the tree and replaces any JsonPrimitive whose string form is NaN, -NaN, Infinity, or -Infinity with JsonNull. Add this function to Backup.kt:

    private fun sanitize(el: JsonElement): JsonElement = when (el) {
        is JsonObject -> JsonObject(el.mapValues { sanitize(it.value) })
        is JsonArray -> JsonArray(el.map { sanitize(it) })
        is JsonPrimitive -> {
            val s = el.contentOrNull
            if (s == "NaN" || s == "-NaN" || s == "Infinity" || s == "-Infinity") JsonNull else el
        }
        else -> el
    }

Call it in exportFormatA and exportCompleteV1 before returning. Result: the native export never writes NaN as a value again, and round-trips cleanly into both the HTML app and the native app.

**Part 3 — heal on paste.** Extend core/JsonHeal.kt heal() to replace string-form special values before parsing. Add regex replacements for both quoted and unquoted forms: replace the value NaN, -NaN, Infinity, and -Infinity with null. This makes any pasted or file-imported JSON tolerant of the HTML writer's bug, regardless of whether Part 1 is applied.

### Verification (must be in the delivery)

1. Import export_Alex_September_28_2026.json (the exact client file) — succeeds. Screenshot of the success toast.
2. The imported data appears in Dashboard, History, Library. Screenshot of each.
3. Export from the native app — grep the output for NaN — zero matches.
4. Re-import that native export — succeeds.
5. Paste a JSON containing the string NaN as averageRPE into the Paste-JSON screen — succeeds.

---

## Then — 12 import/export parity features

After Parts 1 to 3 are working, add the following so the native app has the same import/export surface as the HTML app. HTML line anchors are for reference only. Read the code, do not copy it.

1. CSV export of workout history. HTML reference: exportHistoryCSV, line 41547. One row per logged set. Columns: date, exercise, sets, reps, weight, RPE, note. SAF save plus system share.
2. Custom exercise library export. HTML: exportExerciseData (line 37804), P4.Backup.exportLibrary (55972). JSON array of user-authored exercises.
3. Custom exercise library import. HTML: importExerciseData (37400), importExerciseLibrary (37798). Heal JSON first. Merge, do not replace. Show added, updated, and rejected counts.
4. Consent log export. HTML: P4.Backup.exportConsent (56478). JSON of every consent entry.
5. Feedback export. HTML: P4.Studio.exportFeedback (57347). If no feedback UI exists, build a minimal one with a textarea and submit button, then export.
6. Phrasebook export and import. HTML: exportPhrasebook (57657), importPhrasebook (57668). Merge on import, show added, updated, and rejected counts.
7. Snapshot management screen. HTML: P4.Backup.snapshots (56023), restoreSnapshot (56024), snapshotNow (58796). List with timestamp and size. Tap to restore with confirm plus pre-restore safety snapshot. Delete. Manual create.
8. Pre-import guard dialog. HTML: P4.Studio.guardImport (57405). Confirm before any import. Only proceed on red confirm.
9. Paste-JSON import screen. HTML: P4.Studio.submitImport (57506). Text field, heal, import, show merge report.
10. Full app bundle export and import. HTML: exportBundle (68168), importBundle (68172). One file with workouts, exercises, library, profiles, snapshots, settings, and consent. Import restores all after confirm.
11. Merge report on every import. Confirm ImportResult is surfaced in the UI as a dialog. Fields: format detected, workouts added, exercises added, items rejected with reasons.
12. Format B detection. Verify the importer accepts kind: try4ever-fitness-backup with sibling fields. If missing, add it.

---

## UI

All new buttons go in Settings, Data and Backup card. Order:

    Export full backup (Complete-v1)      existing
    Export workout JSON (Format A)        existing
    Import backup                         existing
    Export workout history as CSV         new
    Export custom exercises               new
    Import custom exercises               new
    Export consent log                    new
    Export feedback                       new
    Export phrasebook                     new
    Import phrasebook                     new
    Snapshots                             new, opens screen
    Paste JSON to import                  new
    Export full app bundle                new
    Import full app bundle                new

---

## Delivery

- versionCode: 14
- versionName: 1.4.4
- Cert: 1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953
- Package: com.peakform.fitness
- File: pro-batch-2a.zip
- Structure: same as pro-hotfix-1.1.zip

## Verification required

- Client's exact file export_Alex_September_28_2026.json imports successfully. Screenshot.
- Native export contains zero occurrences of NaN. Grep output in CHANGES.md.
- Each new export produces a valid file. Screenshot of file picker and saved file.
- CSV opens correctly in a text editor or spreadsheet. Screenshot.
- Snapshots screen with two or more snapshots. Screenshot.
- Pre-import guard dialog. Screenshot.
- Merge report after test import. Screenshot.
- Paste-JSON screen with valid JSON. Screenshot.
- Round-trip: export full bundle, clear app data, import. Before and after screenshots.
- Five verification commands from ACCEPTANCE.md, raw output in README.md.
- CHANGES.md as three-column table: Item, Status, Evidence.

## Acceptance

Install with adb install -r. Import the client's HTML export successfully. Export and re-import native. Every new button works. No crash.

## Then

Resume batch sequence: Batch 3 (Recovery plus History), Batch 4 (Settings), Batch 5 (Sheets), Batch 6 (Onboarding plus Health plus Legal), Batch 7 (Polish).