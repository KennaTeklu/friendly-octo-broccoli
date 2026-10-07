# BATCHES.md — 8 sequential builds, one zip per batch

Each batch is a complete, runnable APK. The agent works on one batch at a time. Every batch ships as its own zip. Between batches the client reviews the delivery, the repo is updated, and the next batch prompt is issued.

versionCode increments by 1 every batch. First batch = 11. Last batch = 18. `adb install -r` works continuously. No data loss between batches.

---

## Batch 1 — Merge baseline

**Scope:** Merge `source/` and `divergent-vcode9/source/` into one tree. No new features. This batch only proves the merge works.

**Must ship:**
- One source tree at `source/` that compiles with `./gradlew clean testReleaseUnitTest assembleRelease`
- Every feature from vCode 10 (onboarding, health, legal, glossary, snapshots, profiles, command palette, import warning) present
- Every feature from vCode 9 (animated media, 1RM Lab, gold header, plate math, favorites) present
- versionCode 11, versionName 1.4.1
- Cert matches 1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953
- APK installs with `adb install -r` over the existing vCode 10 app without data loss
- App launches, all screens reachable

**Out of scope:** Any feature not in vCode 10 or vCode 9. Do not add anything new this batch.

**Deliverable:** `pro-batch-1.zip` with pro.apk (vCode 11), SHA256SUMS.txt, README.md (5 verification outputs), CHANGES.md (list of merged features with ✅/⛔), source/, docs/verify/ (screenshots for at least: dashboard, workout, library, progress, recovery, history, settings, onboarding).

**Acceptance:** App installs on client's phone. Every vCode 10 and vCode 9 feature is visible. No crashes in 5 minutes of use.

---

## Batch 2 — Library

**Scope:** Full library parity with the HTML app.

**Must ship:** 8 group-by modes, streaming (load 20 at a time), filter chips, clickable muscle tags, "How to do" and "Images" buttons, tested 1RM / Est. 1RM / Last / Next lines, per-card badges, animated exercise pictograms, custom exercise indicator, "Add to Workout" button, exercise detail sheet with instructions, search, Expand All / Collapse All.

**Reference:** `SPEC.md` section A item 3, `spec/A-functions.md`, `spec/D-ids.md`, `spec/E-css.md`.

**Deliverable:** `pro-batch-2.zip`, versionCode 12.

**Acceptance:** Library screen matches the HTML app side-by-side. Screenshots for each feature.

---

## Batch 3 — Workout + Progress

**Scope:** Full workout logger and full progress analytics.

**Must ship:**
- Workout: exercise cards, sets/reps/weight loggers, rest timer, superset / drop set / warmup, RPE selector, plate math, resume / replace / skip, momentum modal, confetti on completion
- Progress: longevity circle, strength forecast, volume bars, 11-component radar, 8-week frequency chart, RPE histogram, records board, goal-cycle status, 3-month projection, 5-year forecast, per-muscle sparklines

**Reference:** `SPEC.md` section A items 2 and 4, `spec/A-functions.md`, `spec/D-ids.md`.

**Deliverable:** `pro-batch-3.zip`, versionCode 13.

**Acceptance:** Both screens match the HTML side-by-side. Every chart renders real data.

---

## Batch 4 — Recovery + History

**Scope:** Full recovery screen and full history screen.

**Must ship:**
- Recovery: 49-muscle readiness list, coupling matrix visualization, decay model UI, recovery bands, per-muscle fatigue bars
- History: chronological sessions, full-text search, per-session notes, PR markers, edit / fix past logs, exercise history detail

**Reference:** `SPEC.md` section A items 5 and 6, `spec/A-functions.md`.

**Deliverable:** `pro-batch-4.zip`, versionCode 14.

**Acceptance:** Both screens match the HTML. Search returns correct results.

---

## Batch 5 — Settings

**Scope:** Every settings card, every toggle, every control.

**Must ship:** p4PersonalCard, p4AppearanceCard, p4LanguageCard, p4LookLangCard, p4DataCard, p4TrainingEnvCard, p4LifeCard, p4PowerCard, p4ReadingCard, p4StudioCard, p4PickerCard, p4LibMgmtCard, p4NotifCard, p4VocabCard, p4ThemeCard, p4StreamGroupCard, p4CpCard, p4LegalCard. All controls functional.

**Reference:** `SPEC.md` section D, `spec/D-ids.md`.

**Deliverable:** `pro-batch-5.zip`, versionCode 15.

**Acceptance:** Every card present, every toggle works, changes persist across app restart.

---

## Batch 6 — Sheets and modals

**Scope:** All non-monetization modals, sheets, and overlays.

**Must ship:** addExerciseModal, exerciseSearchModal, fixLogModal, importExerciseModal, importModal (with pre-import warning + 3 format support), longevityModal, periodModal, qrModal, quickActionsModal, testExerciseModal (1RM test), command palette, snapshots sheet, profiles sheet, glossary, My Cycle, muscle wiki.

**Reference:** `SPEC.md` section B, `spec/A-functions.md`, `spec/D-ids.md`.

**Deliverable:** `pro-batch-6.zip`, versionCode 16.

**Acceptance:** Every modal reachable from its trigger, every modal functional.

---

## Batch 7 — Onboarding + Health + Legal

**Scope:** First-run wizard, health screening, legal center.

**Must ship:**
- Onboarding: 5-step wizard (welcome/name → birthdate+gender → reading level → theme → consent) with resumable draft
- Health: PAR-Q 7 questions, follow-up questions, joint picker (8 joints), verdict tiers A/B/C, tier-C soft lock, dashboard banner when locked
- Legal: 13 documents verbatim, tab strip, consent history panel, export consent log

**Reference:** `SPEC.md` sections A and B, `spec/A-functions.md`.

**Deliverable:** `pro-batch-7.zip`, versionCode 17.

**Acceptance:** Wizard appears on fresh install. Health screening gates generation when tier C. Legal documents render.

---

## Batch 8 — Polish + above-and-beyond

**Scope:** Everything remaining from SPEC.md plus the P2 items.

**Must ship:**
- All remaining SPEC.md items not shipped in batches 1–7
- Real system notifications (1RM retest, deload, period expected)
- Home-screen widget
- Material You dynamic color
- FLAG_SECURE privacy mode
- Accessibility pass (TalkBack labels, 48dp tap targets)
- Voice input logging
- Share sheet for workout export
- Launcher shortcuts

**Reference:** `SPEC.md`, `PRIORITIES.md` P2, `spec/A-functions.md`.

**Deliverable:** `pro-batch-8.zip`, versionCode 18.

**Acceptance:** SPEC.md items all classified as ✅ or ⛔. App feature-complete against the HTML.

---

## Rules for every batch

- Ship one zip. Not multiple. Not partial.
- versionCode increments by exactly 1.
- Every ✅ item has a screenshot in `docs/verify/`.
- Every ❌ or deferred item is marked with a reason.
- Five verification commands in README.md, raw output, no summary.
- No `adb uninstall` anywhere. `adb install -r` only.
- If a batch cannot be completed, ship what is done and mark the rest ❌ with a reason. Do not skip a batch.

---

## Current batch

**Batch 1 — Merge baseline.** Awaiting delivery.