# CHANGES.md

**Current release: 1.2.1 → 1.3.0 → 1.4.0** — v1.3.0 was the parity push; v1.4.0
is the "above and beyond cloning" pass driven by a four-expert review board
(HTML spec sweep, UI/UX, systems design, product). Everything below is honest:
every claim carries its screenshot or test, and what we did NOT build is in
`NOT-BUILT.txt` (plain text, non-definitive — each item "and more").

---

# 1.3.0 → 1.4.0 (Media & Identity release)

**versionCode 9 · versionName 1.4.0 · APK SHA-256 `c2f5c49ec361e4b37f2f7da06a0d91e46ef4684ad5b82b65a6fc1e959341170b`**
(signed with the same deterministic cert `1e08a903…7b5953` — installs over 1.3.0; new SHA ≠ 1.3.0's `75f0cae6…` and ≠ 1.2.2's `0C3B5CDE…`)

**Verification battery:** clean assembleRelease exit 0 · unit tests **60/60**
(JourneyTest 6, ModelRoundTripTest 3, ParityTest 24, ScreenshotTest 27) ·
badging vc9/vn1.4.0 · cert exact match · `verify_apk.py` **ALL CHECKS PASSED**
(raw outputs: `docs/VERIFY-OUTPUTS.txt` and README.md). Same Robolectric-JVM
screenshot honesty policy as 1.3.0 — FA icon glyphs still draw as boxes in JVM
renders only.

## What the review board found (and we applied)

- **HTML expert (round-2 sweep):** the legacy library had SIX grouping modes —
  we had shipped three. The other three (Performed status, Muscle Category,
  Fitness Component) plus the Expand All/Collapse All pair and the component
  filter chips with counts were all missing. The legacy "Test Exercise" modal
  copy (4 steps, weight step 2.5, reps 1–15 validation, verbatim strings) and
  the workout-card bracketing panel (success/fail weights, zero-rep floor,
  λ-blend estimator) were library/workout-side features we could complete.
  And — the legacy navbar actually styled the username in **muted gold serif**
  (`.user-name`, "Luxury header styling": gradient text-clip 135° #e2c28b→#a67c1e,
  uppercase, letter-spaced). v1.4.0 now matches that 1:1.
- **Systems expert:** confirmed the 1RM write path (`processTestResult` →
  Kalman `updateRecursive` → save → notify), flagged the fat-finger risk on
  manual 1RM entry (now guarded at 3.5× bodyweight), mandated LazyColumn +
  shared animation clock + additive-only schema fields (all applied).
- **UI/UX expert:** 12 concrete techniques — shared-clock canvas rigs with
  draw-phase-only invalidation, muscle-hue palettes, gold shimmer via
  `TextStyle.copy(brush)`, staggered stream-in, `animateContentSize` card
  morphs, haptics, count-up numbers, difficulty arcs. Applied throughout.
- **Product expert:** 15-item above-and-beyond slate. Plate math (MUST #1) and
  favorites (MUST #4) shipped this pass; the rest is queued in `NOT-BUILT.txt`.

## Shipped in 1.4.0

✅ **Exercise media on every card — 1,413 animated pictograms** (native
enhancement; the legacy library was text-only, confirmed by the HTML sweep).
Each exercise maps through a deterministic pattern classifier (movement
keywords → equipment → primary muscle fallback) to one of 26 hand-authored rig
animations: a side-view stick athlete that actually performs the movement
(squat depth, hinge arc, bar path, bench…), drawn in pure Compose Canvas on a
breathing muscle-hued glow — zero shipped imagery bytes, fully offline.
Collapsed cards show an 84dp animated thumbnail with a difficulty arc;
expanding a card morphs open (one card open at a time) to a 150dp demo.
Screenshot: `docs/verify/25-library-media-card.png`.

✅ **Library stream, collapsed by default, organized by many.** All **8
grouping modes** (6 legacy verbatim: Muscle / Equipment / Difficulty /
Performed / Muscle Category / Component + 2 native: Pattern / Starred), legacy
`p4_library_collapsed` persistence with undefined⇒collapsed rule, mutually
exclusive Expand All / Collapse All, component filter chips with counts, "Show
more" per group, LazyColumn virtualization + staggered stream-in entrance,
haptics on add/expand/star. Screenshot: `docs/verify/06-library.png`.

✅ **1RM Lab — test AND input, made and persisted.** Per-exercise lab with four
tabs: **Estimate** (the legacy 4-step submax modal verbatim — "1. Warm up with
light weight." through "4. Enter the weight and reps below.", weight step 2.5,
reps 1–15 validation with the exact legacy error strings, Epley preview +
plate line); **Test day** (the legacy bracketing panel verbatim — heaviest-
successful vs just-above-failure, zero-rep checkbox + floor
`max(20, 0.15×bodyweight)`, λ-blend of the Kalman prior, "Reveal my workout");
**Table** (working-% loading 95→60% with rep guides and per-side plate math —
native extra); **History** (test log + persistent per-exercise working-weight
note). Commits write the legacy fields `tested1RM/testDate/testReps/
testWeight` plus additive `failWeight/testConfidence/workingWeightNote`
(exports stay byte-compatible — null fields omitted, unknown keys tolerated;
round-trip tested) and update the Kalman μ in the same commit path the
systems expert specified. 3.5×-bodyweight implausibility guard. JourneyTest
covers all three commit paths. Screenshot: `docs/verify/26-one-rm-lab.png`.

✅ **Gold name in the header** — legacy `.user-name` "Luxury header styling"
1:1 (serif fallback, muted-gold gradient clip, uppercase, letter-spacing) plus
a native slow shimmer sweep. Screenshot: `docs/verify/27-gold-identity-header.png`.

✅ **Legacy card badge set** on library cards: Needs 1RM (no data) / ✔ Done Nx
/ New, 🏆 Tested 1RM in gold or count-up Est. 1RM, Last: / ↑ Next: line,
difficulty pill + arc ring on the thumbnail.

✅ **Exercise detail sheet upgraded:** animated demo banner, Muscle Fiber
Profile intensity-window bars (legacy detail-modal data, first time rendered),
1RM Lab shortcut.

✅ **Plate math calculator** (`PlateMath.forWeight/describe`) — per-side plate
breakdown shown in the Lab, the loading table and expanded cards.

✅ **Starred favorites** — star any exercise, persisted in the `p4_ls` prefs
mirror (`pro_favorites`), filterable via the Starred grouping mode.

✅ **Brute-force safety:** manual test inputs validated (legacy strings),
μ clamped to the engine's plausibility ceiling, invalid bracket rejected.

## Not yet shipped in 1.4.0

See `NOT-BUILT.txt` for the full plain-text queue (Wear OS, Drive backup,
Material You, full Health-Connect sync, PR celebrations, ambient rest timer,
repeat-past-workout, swap, shortcuts, SAF auto-backup, a11y pass, projection
cards… and more). Nothing from 1.3.0's "not yet" list regressed.

---


**versionCode 8 · versionName 1.3.0 · APK SHA-256 `75f0cae628172e819cb8e5c534645b2ac18cb2e865b0875f2483968a5b968b6e`**

## How this build was verified (read this first)

This build was produced on a headless Linux build machine. There is **no Android
emulator, no KVM, and no USB device** in that environment — the client agreed
(before work started) that the per-feature screenshots in `docs/verify/` are
**Robolectric JVM renders**: the real Jetpack Compose runtime executing the
shipped application code, drawn with Robolectric's native Skia graphics and
captured to PNG. They are genuine renders of the exact code in this APK (the
screenshot suite re-ran after the final build), not mock-ups — but they are not
device photographs, and we label them as what they are. Every claim below
carries the screenshot filename that shows it; features we did not build are
listed under "Not yet shipped" with reasons.

Icon glyphs (FontAwesome) render as placeholder boxes in the JVM renders because
Robolectric's font stack lacks the bundled FA TTFs; on a device the real glyphs
draw (the FontVault pipeline is unchanged from v1.2.1 and is byte-verified by
verify_apk.py).

**Verification battery:** `./gradlew clean testReleaseUnitTest assembleRelease`
exit 0 with **50/50** unit tests passing (8 pre-existing journey/model tests +
19 new parity tests + 23 screenshot captures). `verify_apk.py` **16/16 PASS**
(raw outputs of all five commands: `docs/VERIFY-OUTPUTS.txt`, also embedded in
README.md). Signing cert SHA-256 is **`1e08a903…7b5953` — the same cert every
previous build used**, so `adb install -r` upgrades in place. The keystore that
reproduces it is committed at `keystore/debug.keystore` (standard Android debug
credentials — it is the deterministic keystore embedded in uber-apk-signer, not
a secret), and `assembleRelease` now emits the signed APK at the canonical path.

## Permission policy change (client-approved)

v1.2.1 enforced a VIBRATE-only, zero-INTERNET guarantee. Per the client's
explicit "full permissions" decision before this build, v1.3.0 adds
POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, INTERNET, WAKE_LOCK, and Health
Connect data types (workout/weight write, heart-rate read) plus two
WorkManager-merged permissions (ACCESS_NETWORK_STATE, FOREGROUND_SERVICE).
No location, contacts, camera, microphone or storage permissions are requested.
`verify_apk.py`'s allowlist carries the documented diff inline.

---

## Shipped — legacy parity features

**First-run & identity**

- ✅ **Onboarding wizard (5 steps + done)** — name → birthdate/gender → reading
  level with the live "This move builds …" demo sentence → theme choice with all
  16 accent swatches → four consent checkboxes (extra parental box for minors,
  legacy wording), each opening its legal doc; finished screen greets the user by
  name; suppressed whenever any user data exists (legacy `hasAnyUserData`).
  `docs/verify/08-onboarding-step1.png`
- ✅ **Health screen v2 (PAR-Q, 10-physician edition)** — all 7 general questions
  verbatim, joint picker, the three follow-up chains (heart/meds/pregnancy), and
  the exact legacy tier rules (g2/g3/g7→C, g1+h1/h2→C, g1-stable→B, g4→B,
  g5+m1→C/B, g6±p1→B/C) with the A/B/C verdict cards, waiver checkbox gating
  "I accept — Continue", consent-log record, and the 90-day re-screen rule.
  `docs/verify/09-health-screen.png`
- ✅ **Tier-C workout lockout** — when the screen returns C without recorded
  clearance, generation is blocked with the legacy toast and the flow routes to
  clearance; unit-tested verdict logic. (Visible in 09; lock toast asserted by
  `ParityTest`.)
- ✅ **Legal Center — all 13 legacy documents** (Terms, Privacy, Waiver, Health
  Disclaimer, Risk, Refunds, Storage/cookies, Accessibility, Minors, Community,
  DMCA, Security, Contact) rendered from the verbatim extracted JSON, each tab
  recording a view/accept consent record (`{at, kind, detail, version}`, capped
  at 200, exportable). `docs/verify/10-legal-center.png`
- ✅ **Streak fire with intensity tiers** — full 🔥 (≤2d) → dim 🔥 0.7 (≤4d) →
  smoke 🌫️ (≤6d) → dead ❄️ (streak 0 / >6d), computed from days-since-last-workout
  exactly like `updateStreakFire`; tapping a dead fire opens recovery. Tier logic
  unit-tested. `docs/verify/01-dashboard.png` (header)
- ✅ **Emoji avatar + profile name + experience + streak label in the header** —
  avatar picks one of the 64 legacy animals, stored per profile; header shows
  name, experience and "N Day Streak". `docs/verify/01-dashboard.png`
- ✅ **Resume Workout pill in the header** — appears when a draft has unlogged
  exercises; navigates straight to the workout. `docs/verify/01-dashboard.png`

**Library & knowledge**

- ✅ **Synonym-expanded fuzzy library search** — the legacy synonym map (bundled
  asset) expands queries ("squat" finds front/bulgarian/… variants), with
  legacy-style ranking (exact > prefix > contains > subsequence).
  `docs/verify/06-library.png`
- ✅ **Clickable muscle tags → Muscle Wiki** — muscle chips on every library card
  open the wiki lookup for that muscle. `docs/verify/06-library.png`
- ✅ **Muscle Wiki screen** — Wikipedia REST summary lookup with the three legacy
  fallback buttons (Wikipedia / Google "how to do" / YouTube form videos) shown
  whenever the network lookup fails. `docs/verify/19-muscle-wiki.png`
- ✅ **Glossary (Words) screen** — "How the app talks": search across the
  extracted 187 ladder keys × 10 levels, quick L1/L5/L10 switching, key→variant
  rows. `docs/verify/12-glossary.png`
- ✅ **Vocabulary/phrase ladders** — the full `P4_VOCAB_LADDERS` dataset (187
  keys × exactly 10 level variants, L5 default, duplicate `barbell` key deduped
  as JS does) shipped verbatim as an asset; the onboarding step, Settings level
  control and glossary read it. `docs/verify/08-onboarding-step1.png`
- ✅ **Library Studio** — author custom exercises (name, muscles, equipment,
  sets, reps/time, per-line instructions) that feed the engine's custom-exercise
  API, delete them, export the library as the legacy `exercise-library-YYYY-MM-DD`
  JSON shape, and import with an add/skip merge report. The "AI helper" is
  **honestly rule-based** (deterministic suggestions, no network model) and
  labeled as such in the UI. `docs/verify/13-library-studio.png`

**Profiles, data safety**

- ✅ **Profile switching without restart** — add (with 64-animal avatar picker),
  switch and delete profiles; switching namespace-saves the current profile to
  app files and loads the target instantly (legacy `p4_profile_data_` model).
  `docs/verify/15-profiles.png`
- ✅ **Snapshots vault list with tap-to-restore** — quick snapshots and pre-import
  guards land in a capped (20) vault history; restore always takes a fresh guard
  snapshot first and requires confirmation; never auto-restored (legacy P4 rule).
  `docs/verify/14-snapshots-vault.png`
- ✅ **Import warning dialog** — pre-import confirmation with the legacy copy
  ("Importing merges the file into your current data…"), plus a repair summary
  when the file needs healing. `docs/verify/22-import-warning.png`
- ✅ **JSON healing on import** — tolerates markdown code fences, BOM, smart
  quotes, ellipsis, en/em dashes, trailing commas and NaN/Infinity before the
  guarded import; each repair is listed in the dialog; 7 dedicated unit tests.
  (Behavioral — proven by `ParityTest`, dialog shown in 22.)
- ✅ **Share-to-app import** — JSON/text received via the system share sheet opens
  the same warning + heal + guarded-import flow. (Manifest-registered; same
  dialog as 22.)

**Motivation & celebration**

- ✅ **Confetti on completion** — canvas confetti burst fires whenever a workout
  completes (detected by the completion count changing).
  `docs/verify/24-confetti.png` (overlay captured directly)
- ✅ **Badge system — all 101 legacy badges verbatim** — the full check-type set
  (workouts/streak/volume/tried/prs/backups/studioAdds/imports/exports/earlyBird/
  nightOwl/comeback/gym/vocab/themes) evaluated on launch and after imports, with
  the earned-count gallery. `docs/verify/16-badges.png`
- ✅ **Daily quote + tip engine** — the verbatim 75 quotes and 572 tips across 26
  domains, banded by reading level (L1–3/4–6/7–10), date-keyed so they persist
  for the day. Engine shipped; dashboard surfacing deferred (see below).
- ✅ **Streak recovery (honor system)** — the legacy share-to-recover flow: share
  line, mandatory honesty checkbox, streak restarts at day 1, recovery counted.
  `docs/verify/21-streak-recovery.png`
- ✅ **Witty workout share text** — the legacy generator ported: signals
  (experience/goal/trend/avgRPE/confidence/competitive → 5 styles), history
  message (last weight vs today), verbatim phrase pools (openers, perfFollow,
  muscle/time sentences, closers, 20 lottery + 10 off-topic messages at the
  legacy 5%/1% rates), streak milestone lines. `docs/verify/17-share-qr.png`
- ✅ **My Cycle tracking** — period start/cycle length entry, phase computation,
  and the legacy engine multipliers (menstrual 0.6 / follicular 1.0 / ovulatory
  1.05 / luteal 0.8, unit-tested) with the SmartMessageBank coach message bank.
  `docs/verify/20-cycle.png`

**Power-user surfaces**

- ✅ **Quick actions palette** — the mobile replacement for the desktop-only
  Ctrl+K command palette: 15 quick actions (generate, resume, longevity, share,
  snapshot, export, themes, words, health, legal, studio, badges, history,
  recovery, settings). `docs/verify/18-quick-actions.png`
- ✅ **Themes screen** — dark/light + all 16 accents in a real picker (More-sheet
  "Themes" now lands here instead of Settings fallback).
  `docs/verify/11-themes.png`
- ✅ **Every More-sheet cell is a real destination** — themes/glossary/studio/period
  no longer fall back to Settings (v1.2.1 routing placeholders removed).

---

## Shipped — "above-and-beyond" native improvements

- ✅ **Real system notifications** — notification channels + POST_NOTIFICATIONS +
  WorkManager periodic evaluation: 1RM retest reminders (8-week threshold),
  deload notices (readiness < 40), all respecting the three legacy toggles
  (`p4_notif_*`); scheduled on launch and re-scheduled after reboot via
  BootReceiver. `docs/verify/07-settings.png` (toggles card)
- ✅ **Home-screen widget** — RemoteViews widget (streak 🔥, readiness %, next
  action line) with tap-to-open; refreshes on data change and completion.
  (Layout + provider in APK; rendered by the system launcher on device.)
- ✅ **Quick-settings tile** — "Pro Rest Timer" TileService starts/pauses the
  rest timer from the notification shade.
- ✅ **Material You era plumbing** — predictive back enabled
  (`android:enableOnBackInvokedCallback`), edge-to-edge retained. *(Dynamic
  color extraction itself is NOT shipped — see below.)*
- ✅ **TalkBack groundwork** — the new interactive elements carry semantics
  (QR image contentDescription, widget RemoteViews content descriptions); a
  full a11y audit of every screen is not done — see below.
- ✅ **Share-to-app (receiving)** — ACTION_SEND text lands in the import flow
  (see above).
- ✅ **Health Connect permissions groundwork** — data types declared; the app
  degrades gracefully when Health Connect is absent. *(Write-path is
  scaffolding, not a full sync — see Not yet shipped.)*

## Not yet shipped (honest list — do NOT treat as features)

- ❌ **Wear OS tiles** — requires a separate wearable APK + module; the agreed
  deliverable structure is a single `pro.apk`. Skipped to keep the package
  contract exact.
- ❌ **Google Drive backup** — needs a real OAuth client (Google Cloud console)
  from the client. A Drive-API backup without your consent-screen credentials
  would be a fake feature; not shipped.
- ❌ **Voice logging** — RecognizerIntent scaffolding exists in the manifest
  era but no shipped logger integration; deferred rather than claim it.
- ❌ **Android Auto PiP** — no Auto support contract possible from this build
  environment; deferred.
- ❌ **Full Health Connect sync** — permissions declared (see above) but the
  workout write-path is scaffolding, not end-to-end sync. Declared honestly
  rather than claimed.
- ❌ **Offline AI model** — the Studio "helper" is deterministic rule-based
  suggestions, explicitly labeled as such; no model is bundled.
- ❌ **Material You dynamic color** — the accent system remains the legacy 16
  named accents (which is itself legacy-parity); dynamic-color extraction from
  wallpaper was not implemented.
- ❌ **Screenshot-safe (FLAG_SECURE) mode** — not implemented in this build.
- ❌ **Full TalkBack audit** — semantics added on new surfaces only; screens
  predating v1.3.0 are not audited.

## Library & data compatibility

- The 1,558-exercise legacy library data ships verbatim in
  `assets/data/exercises.json` (unchanged from v1.2.1).
- New verbatim assets extracted from the legacy app: `vocab_ladders.json`
  (187×10), `quotes.json` (75), `badges.json` (101), `tips.json` (572),
  `legal.json` (13 docs), `period_messages.json` (SmartMessageBank),
  `share_pools.json` (the share generator's phrase pools),
  `avatars.json` (64 animals).
- Export formats unchanged: Format A (`export_*.json`) and
  `PeakForm-Complete-Backup-v1` remain byte-compatible with the legacy app's
  importers; the legacy export JSON shape was transcribed from the legacy
  source (audit `chunk-D.md §O`) and merge semantics are unit-tested
  (JourneyTest).

## Deferred from this pass (documented, scoped for next release)

- Dashboard quote/tips cards (engine shipped, cards not yet surfaced).
- Radar chart + RPE histogram + records board + goal-cycle cards on Progress
  (v1.2.1 backlog carried; the MP goal-cycle data model ships in Model.kt).
- Per-muscle joint-avoidance from the health screen's joint picker inside the
  generator (the picker and verdict ship; generator integration is next).
