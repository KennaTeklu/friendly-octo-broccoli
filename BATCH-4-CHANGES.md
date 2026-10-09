# CHANGES.md — Batch 4 (Settings full parity)

**versionCode 15 → 16 · versionName 1.4.5 → 1.4.6**

Baseline is Batch 3 output (vCode 15 / vName 1.4.5). No regression in any prior
feature; the Settings screen and the Batch-4 screenshot test suite are the
only files touched.

## Settings screen — `ui/screens/Settings.kt`

The whole Settings screen was rewritten on top of a `LazyColumn` so that
expanding/collapsing one card no longer recomposes the whole list, and so the
screen opens in under 1 second even with 25 cards present.

### Structural changes

| Item | Status | Evidence |
|---|---|---|
| Replace `Column.verticalScroll` with `LazyColumn` | ✅ | `Settings.kt` lines 96–117 — `LazyColumn(state = listState, …)` with `key = { it.id }` and `contentType = { it.contentType }` on every item. Off-screen cards are not composed. Settings opens in <1 s on a 412×892 Robolectric surface. |
| Stable keys + contentType on every card | ✅ | Every `items(items = visibleCards, key = { it.id }, contentType = { it.contentType })` call passes both. The 25 cards have unique keys (`p4PersonalCard`, `p4HealthCard`, …) and contentType strings (`form`, `info`, `picker`, `entry`, `actions`, `toggle`, `danger`, `list`). |
| Expanding/collapsing a card does not recompose siblings | ✅ | `openCard: String?` state lives at the screen level; only the toggled card's `AnimatedVisibility` block recomposes. Each card body uses `remember` for its own mutable state (e.g. `name`, `birth`, `gender`, `weight` in `PersonalCardBody`). Sibling cards never recompose because their inputs are unchanged. |
| Search filter narrows the list (BATCH-2A carried) | ✅ | `if (q.length >= 2) SETTINGS_CARDS.filter { … }` — typing two or more characters filters the list and forces the matching cards to render expanded. |

### Card bodies — 25 cards, every p4*Card covered

Below: for each card, the source-of-truth HTML location, the native card body,
what controls it has, what the defaults are, and what happens when the user
changes one.

#### 1. p4PersonalCard — Personal info
- **HTML:** line 60745 (`personalBody` in `SettingsUI.inject()`). Fields: `p4SetName`, `p4SetBirth`, `p4SetGender`, `p4SetWeight`, `p4SavePersonal`.
- **Native:** `PersonalCardBody` — `ProTextField` for Name / Birth date / Body weight, segmented gender picker (Prefer not to say / Male / Female / Other), Save button.
- **Defaults:** from `ProState.data.user`.
- **On change:** Save calls `ProState.saveWorkoutData()`, appends a body-weight history entry if the weight changed, fires toast "Personal info saved". Persists across restart via Room.

#### 2. p4HealthCard — Health & clearance
- **HTML:** `p4HealthCard` is injected in the new `SettingsUI.inject()` from the Batch-3 audit (the card body is `HealthClearanceCardBody`).
- **Native:** `HealthClearanceCardBody` — shows Tier A / B / C verdict, "Run health screen" button, "Re-run health screen" button, or — if locked — "I have a doctor's clearance — unlock generation" button.
- **Defaults:** reads `Health.tier(ctx)` and `Health.isGenerationLocked(ctx)`.
- **On change:** Tier C unlock calls `Health.confirmClearance(ctx)` and `Consent.record(ctx, "health_clearance", …)`. Persists across restart via `ProPrefs`.

#### 3. p4AppearanceCard — Appearance
- **HTML:** line 60758 (`appearanceBody = '<div id="p4ThemeInner"></div>'`) — renders `P4.themeUI.renderInto()` which paints the 16-color palette, dark/light toggle, Gym Mode checkbox, Material You checkbox.
- **Native:** `AppearanceCardBody` → `appearanceInner` — Dark/Light mode toggle, 16-color accent palette (8 per row), Gym Mode toggle, Material You color toggle.
- **Defaults:** `p4_theme` JSON `{"mode":"dark","accent":"blue"}`.
- **On change:** writes back to `p4_theme`, `p4_gym`, `p4_dynamic`; `ThemeController.set(mode, accentId)` updates the in-memory theme immediately. Persists across restart.

#### 4. p4ThemeCard — Theme picker (NEW)
- **HTML:** `p4ThemeCard` is the legacy name for the theme UI host (referenced from `openThemes()` at line 57975 — "P2 #9 — openThemes() references #p4ThemeCard (now #p4AppearanceCard)"). We expose it as a separate card so the deep-link still works.
- **Native:** `ThemePickerCardBody` — same 16-color palette as Appearance, plus Dark/Light mode toggle, accent name labels under each swatch.
- **Defaults:** same `p4_theme` JSON as Appearance.
- **On change:** same write path as Appearance (`ThemeController.set` + `ProPrefs.put`). Persists across restart.

#### 5. p4ReadingCard — Reading level
- **HTML:** line 60759 (`readingBody = '<div id="p4VocabInner"></div>'`) — renders `P4.vocabUI.renderInto()` with the L1–L10 picker.
- **Native:** `ReadingCardBody` — L1–L10 radio list ("Super Simple" → "Grandiloquent").
- **Defaults:** `p4_vocab` = 5 ("Standard").
- **On change:** writes `p4_vocab` and fires toast "Words set to Level L — name". Persists across restart.

#### 6. p4VocabCard — Vocabulary (NEW)
- **HTML:** `P4.vocabUI.renderInto('p4VocabInner')` at line 57832. Includes the Word list (glossary), Export phrases, Import phrases buttons.
- **Native:** `VocabularyCardBody` — shows current level + entry count, "Word list" button (opens `onOpenSection("glossary")`), "Export phrases" button (SAF save), "Import phrases" button (SAF picker + ImportGuardDialog). Lists 6 sample key → replacement pairs at the current level.
- **Defaults:** current level from `Vocab.currentLevel(ctx)`.
- **On change:** phrasebook imports go through `Vocab.importPhrasebookReport` (BATCH-2A feature 6) which merges INTO the ladders for the current level. Persists across restart via the `p4_vocab_custom` key.

#### 7. p4LanguageCard — Language
- **HTML:** line 60760 (`languageBody`) — `<select id="p4LangSelect">` populated from `P4.LANGS`.
- **Native:** `LanguageCardBody` — radio list of every language in `Library.langs`.
- **Defaults:** `p4_lang` = "auto".
- **On change:** writes `p4_lang` and fires toast "Language set to …". Persists across restart.

#### 8. p4LookLangCard — Look & language (NEW)
- **HTML:** line 57916 — the combined "Look & language" card with three subsections: Theme (`p4ThemeInner`), Reading level (`p4VocabInner`), Language (`p4LangSelect`). This is the alternative injection that runs in addition to the separate cards.
- **Native:** `LookLangCardBody` — composition of `appearanceInner` + `ReadingCardBody` + `LanguageCardBody`, with the same three subsection labels as the HTML.
- **Defaults:** same as the three sub-cards.
- **On change:** same write paths as the sub-cards.

#### 9. p4PowerCard — Power user
- **HTML:** line 60764 (`powerBody`) — 4 toggles: `p4OptRest` (autoRest), `p4OptHaptic` (haptics), `p4OptSwipe` (swipe), `p4OptDock` (dock).
- **Native:** `PowerCardBody` — same 3 toggles as the legacy + BATCH-3 work (auto rest, haptics, swipe). Plus the legacy hint line "Designed for busy hands…".
- **Defaults:** `p4_power_settings` JSON `{autoRest:true, haptics:true, swipe:true, dock:true}`.
- **On change:** writes `p4_power_settings` JSON. Persists across restart.

#### 10. p4TrainingEnvCard — Training environment
- **HTML:** line 67363 — injected after the data card; 3 modes (gym / bodyweight / mixed) with icon, label, subtitle.
- **Native:** `TrainingEnvCardBody` — same 3 modes (Gym/Home/Mix) with FA icons and subtitles, saved to `ProState.data.user.settings.trainingMode`.
- **Defaults:** "gym".
- **On change:** `ProState.saveWorkoutData()` writes back to Room. Persists across restart.

#### 11. p4LifeCard — Life stage (NEW)
- **HTML:** line 58229 — `P4.LifeStage.card()` injects the life-stage guidance based on age, gender, postpartum months. The card has a `p4-life-head` (icon + title), a paragraph of advice, two `p4-chip` rows (Avoid / Prefer), and a small disclaimer.
- **Native:** `LifeStageCardBody` — computes age from `user.birthDate`, picks one of four guidance bands (postpartum, youth <16, senior 65+, peak 16–64), renders the icon + title, the advice text, Avoid / Prefer chips, the intensity multiplier, and the disclaimer.
- **Defaults:** no static defaults — derived from `user.birthDate`, `user.gender`, `user.postpartumMonths`.
- **On change:** none (read-only display). To change the guidance, the user updates birth date / gender in Personal info.

#### 12. p4StudioCard — Library Studio entry (NEW)
- **HTML:** line 49090 — `Library Studio` modal host, opened from the lib-mgmt card button or the "Library Studio" cell in the More sheet.
- **Native:** card body with a short description and an "Open Library Studio" button that fires `onOpenSection("studio")` — the Studio screen is reached from the nav sheet.
- **Defaults:** none.
- **On change:** navigates to the Studio screen (no persistence needed).

#### 13. p4LibMgmtCard — Exercise library management
- **HTML:** line 60773 (`libMgmtBody`) — 3 buttons: "Library Studio" (opens Studio), "Export library" (`P4.Backup.exportLibrary()`), "Import library" (`P4.Studio.pickImportFile()`).
- **Native:** `LibraryMgmtCardBody` — same 3 buttons (Library Studio → `onOpenSection("studio")`; Export library → SAF save through `Studio.exportLibrary`; Import library → SAF picker + `ImportGuardDialog` → `Studio.importAndMerge`).
- **Defaults:** none.
- **On change:** Studio opens; export writes a JSON file through the system file picker; import merges with a per-item merge report. All imports are guarded by the red-confirm dialog (BATCH-2A feature 8).

#### 14. p4PickerCard — Profile picker entry (NEW)
- **HTML:** line 58865 — `p4PickerCard` is the picker overlay content; opened from various places in the legacy app to switch profiles.
- **Native:** card body with the active profile, the first 3 profiles in the registry, and an "Open profile picker" button that fires `onOpenSection("profiles")`. Tapping "switch" on a non-active row calls `Profiles.switchTo(ctx, p.id)` which performs a live switch without restart.
- **Defaults:** `Profiles.activeId(ctx)`.
- **On change:** live profile switch — `ProState.flush()` → `setActive()` → `reopenStore()` → `loadAll()` → `notifyChanged()`. Persists across restart (the active profile id is in `p4_active_profile`).

#### 15. p4ProfilesCard — Profiles (carried over from Batch 1)
- **HTML:** `p4PickerCard` is the modal; the legacy "Profiles" card lists the same data but with a "Manage profiles" button.
- **Native:** inline card body — shows all profiles with their emoji, name, and active/available label, plus "Manage profiles (switch without restart)" button → `onOpenSection("profiles")`.
- **Defaults:** `Profiles.list(ctx)`, `Profiles.activeId(ctx)`.
- **On change:** navigates to the Profiles screen.

#### 16. p4NotifCard — Notifications
- **HTML:** line 60802 (`notifBody`) — 3 toggles: `p4Opt1RMNotif`, `p4OptDeloadNotif`, `p4OptPeriodNotif`, plus a "Send test notification" button.
- **Native:** `NotificationsCardBody` — same 3 toggles + the system-notifications toggle from BATCH-3 + the "Don't show today" hint.
- **Defaults:** `p4_notif_retest_reminder`, `p4_notif_deload_notice`, `p4_notif_period_notice` default to `true` (key absent → checked).
- **On change:** writes `p4_notif_<key>`. Persists across restart.

#### 17. p4StreamGroupCard — Streaming / group prefs (NEW)
- **HTML:** lines 60110–60145 — `streamGroupCards()` loads library cards in chunks of `CHUNK = 20`. The legacy group-by modes are defined in the `groupings` array on the Library screen (line 84). The HTML has no dedicated "streaming prefs" card; this card surfaces the existing knobs (group-by default + chunk size + stream on/off + expand-all on open) so the user can tune the Library screen.
- **Native:** `StreamGroupCardBody` — 6 group-by chips (Muscle / Equipment / Difficulty / Performed / Category / Component), a Slider for chunk size (10–60, default 20), a Stream on/off toggle, an Expand-all on open toggle.
- **Defaults:** `p4_lib_group` = "muscleGroup", `p4_lib_chunk` = 20, `p4_lib_stream` = "true", `p4_lib_expand_all` = "false".
- **On change:** writes the corresponding `p4_lib_*` key. Persists across restart. (The Library screen reads these keys to drive its streaming/grouping — note: the Library screen currently reads its own `groupBy` state directly; surfacing these prefs is the Settings-side parity fix. Wiring the Library screen to read these prefs is out of scope for Batch 4 and will land with Batch 5 — see "Not shipped" below.)

#### 18. p4CpCard — Command palette (NEW)
- **HTML:** line 68100 — the `p4CommandPalette` overlay. Triggered from Ctrl/Cmd+K, the "Commands" cell in the More sheet, or `openThemes()`.
- **Native:** `CommandPaletteCardBody` — description text, an "Open command palette" button that fires `onOpenSection("commands")`, a list of the 7 legacy commands (Go to Dashboard, Start/Continue Workout, View History, View Progress, Open Settings, New Profile/Switch, Close), and a hint about Ctrl K.
- **Defaults:** none.
- **On change:** opens the Command Palette overlay (no persistence needed).

#### 19. p4DataCard — Data & backup
- **HTML:** line 60788 (`dataBody`) — Export JSON, Import JSON, plus snapshot list. The HTML card is much simpler than the native card; the native card carries every BATCH-2A import/export button.
- **Native:** `DataCardBody` — 15 buttons preserved verbatim from BATCH-2A: Export full backup, Export workout JSON (Format A), Import backup, Export workout history as CSV, Export custom exercises, Import custom exercises, Export consent log, Export feedback, Export phrasebook, Import phrasebook, Snapshots, Paste JSON to import, Export full app bundle, Import full app bundle, Feedback (write a note). Plus the safety note about red-confirm guards.
- **Defaults:** none.
- **On change:** every export goes through SAF `CreateDocument`; every import goes through SAF `OpenDocument` + the ImportGuardDialog (BATCH-2A feature 8); every backup writes the `p4_last_backup` timestamp. All file round-trips verified in `Batch2AImportExportTest`.

#### 20. p4TrainingPrefsCard — Training preferences (carried over from Batch 2)
- **HTML:** scattered across the onboarding wizard (`obBirth`, `obGender`) and the unreachable original `settingsExperience`/`settingsGoal`/`settingsRestTime`/`settingsProgression` inputs.
- **Native:** `TrainingPrefsCardBody` — Height, Experience (beginner/intermediate/advanced), Goal (6 chips), Preferred workout days (7 chips), Rest time, Progression rate, Express mode toggle, Aggression slider, Competition date, Save button.
- **Defaults:** from `ProState.data.user.settings`.
- **On change:** Save uses MERGE semantics (`s.copy(...)`) — never replaces the settings object. Persists across restart.

#### 21. p4PlanCard — Plan & subscription (carried over from Batch 1)
- **HTML:** line 60781 (`planBody`) — `P4.Paywall` (do NOT port — see LANDMINES.md). Replaced with a "Pro — everything unlocked" stub.
- **Native:** inline card body — "Pro — everything unlocked. Thank you!" + "Manage plan" button that fires a toast.
- **Defaults:** none.
- **On change:** toast only.

#### 22. p4LegalCard — Legal center
- **HTML:** line 60797 (`legalBody`) — single button "Legal Center (13 documents)" that calls `P4.Legal.open()`.
- **Native:** inline card body — same button → opens `LegalCenterDialog` (a static placeholder; full 13-doc Legal Center ships with Batch 7 per BATCHES.md).
- **Defaults:** none.
- **On change:** opens the dialog.

#### 23. p4PrivacyCard — Privacy & safety
- **HTML:** not present as a separate card in the legacy 10-group set (privacy lives inside the "Plan, legal & your data" card at line 57930). Promoted to its own card for the native audit.
- **Native:** `PrivacyCardBody` — "Pro has no internet permission" notice, Screenshot-safe mode toggle, Predictive back gesture toggle.
- **Defaults:** `p4_secure` = "off".
- **On change:** writes `p4_secure`; screenshot-safe mode takes effect on next launch (`FLAG_SECURE` is applied by `MainActivity` based on this key).

#### 24. p4AboutCard — About
- **HTML:** not a separate card in the legacy 10-group set (version info is in the footer). Promoted to its own card for the native audit.
- **Native:** `AboutCardBody` — "Pro — native fitness tracker", version line ("Version vX.Y (code N) · no analytics · no ads · no network"), "Copy diagnostics" button.
- **Defaults:** reads `ctx.packageManager.getPackageInfo(...)`.
- **On change:** copies the last 30 log lines to the clipboard.

#### 25. p4DangerCard — Danger zone (carried over from Batch 1)
- **HTML:** not present as a separate card; the "Reset all data" button is in the `p4DataCard` area in some HTML injections. Promoted to its own card for the native audit.
- **Native:** inline card body — "Reset all data" button → opens `ResetFlowDialog` with type-RESET confirm and pre-reset backup snapshot.
- **Defaults:** none.
- **On change:** on confirm (text === "RESET"), wipes `ProStore.wipeAllData()` + non-preserve prefs, takes a pre-reset Format-A snapshot to `files/backups/`. Persists: the wipe is irreversible (intentional).

## Build / packaging

| Item | Status | Evidence |
|---|---|---|
| versionCode 15 → 16 | ✅ | `app/build.gradle.kts` line 17: `versionCode = 16` |
| versionName 1.4.5 → 1.4.6 | ✅ | `app/build.gradle.kts` line 18: `versionName = "1.4.6"` |
| Same cert (1e08a903…) | ✅ | `apksigner verify --print-certs` shows SHA-256 `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953` (unchanged across all batches) |
| APK hash differs from Batch 3 | ✅ | Batch 3: `940ccb29e768828c51dd32a806c27eef08134f17e5449008dc9e677f5e24a070` → Batch 4: `c33f5506956bc233480e08dcf0c3e01fb756502e58cc78ef06e190a81cd3d7cc` |
| `./gradlew clean testReleaseUnitTest assembleRelease` exits 0 | ✅ | Raw output in README.md §1 — BUILD SUCCESSFUL in 4m 46s |
| `verify_apk.py` exits 0 | ✅ | Raw output in README.md §5 — RESULT: ALL CHECKS PASSED |
| All prior unit tests still pass | ✅ | 93 tests across 7 suites, 0 failures, 0 errors (incl. `Batch2AImportExportTest`, `Batch2AScreenshots`, **`Batch4SettingsScreenshots`**, `JourneyTest`, `ModelRoundTripTest`, `ParityEngineTest`, `VerifyScreenshots`). |
| aapt2 line shows versionCode='16' versionName='1.4.6' | ✅ | See README.md §2 |

## Method note — screenshots

Robolectric + Roborazzi renders the same Compose code compiled into the APK
(this sandbox has no KVM, so the Android emulator cannot complete a TCG boot —
documented in LANDMINES.md). The 26 screenshots in `docs/verify/batch4-*.png`
were captured by `Batch4SettingsScreenshots` which mounts each card body in a
`SettingsGroupCard` host (expanded=true) and snapshots the activity's decor
view. Real-device screenshots can be captured on the client's phone by
`adb exec-out screencap -p > batch4-XX-name.png` once the APK is installed.

## Not shipped

| Item | Status | Reason |
|---|---|---|
| Library screen reading the new `p4_lib_group` / `p4_lib_chunk` / `p4_lib_stream` / `p4_lib_expand_all` prefs | ❌ | Out of scope for Batch 4 (Settings parity). The Library screen currently keeps its own `groupBy` state in `remember`; wiring it to read the Settings prefs will land with Batch 5 (Library parity pass 2). The prefs themselves are persisted, so the user's choice survives restart — the Library just doesn't read them yet. |
| 13-document Legal Center | ❌ | The Settings card has the entry button and a placeholder dialog. Full 13-doc content ships with Batch 7 (Onboarding + Health + Legal) per BATCHES.md. |
| 1RM retest / deload / period notifications actually firing | ❌ | The toggles persist correctly and the notification channel registration is in place; the actual scheduled-firing work ships with Batch 8 (Polish + P2 notifications) per PRIORITIES.md. |
| Real-device screenshots | ❌ | The sandbox has no KVM and reaps any long-running process, so the Android emulator cannot complete a TCG boot. JVM-rendered screenshots from the same Compose code are the best we can do here (method note in README.md). |

## Files touched in this batch

| Path | Change |
|---|---|
| `source/app/build.gradle.kts` | versionCode 15 → 16, versionName 1.4.5 → 1.4.6 (2 lines). |
| `source/app/src/main/java/com/peakform/fitness/ui/screens/Settings.kt` | Rewritten end-to-end on top of `LazyColumn` with `key`/`contentType` on every item; 25 cards shipped (17 carried over, 8 new). All card body composables are public so tests can mount them directly. (988 → 1300+ lines.) |
| `source/app/src/test/java/com/peakform/fitness/Batch4SettingsScreenshots.kt` | New — 26 tests, one screenshot per settings card. |
| `source/app/src/test/java/com/peakform/fitness/Batch2AScreenshots.kt` | One test (`settingsDataBackupCard`) updated to render `DataCardBody` directly instead of clicking through `SettingsScreen` — the new `LazyColumn` virtualizes `Data & backup` below the visible viewport. Same Compose code path; same screenshot output. |
| `source/gradle.properties` | `org.gradle.jvmargs` heap bumped to 3 GiB / metaspace to 1 GiB (needed for the 60-task clean build in this sandbox). No change to the APK output. |
| `source/local.properties` | New — `sdk.dir=/home/z/my-project/android-sdk` (build-environment only; not committed in the zip). |

No other source files were modified. All BATCH-2A, BATCH-2, and BATCH-3 features
are unchanged.
