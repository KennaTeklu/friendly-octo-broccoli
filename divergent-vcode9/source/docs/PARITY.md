# PARITY.md — gap audit (legacy HTML app → native Pro)

This is our own audit, built by scanning the entire legacy single-file
app (68,208 lines, 48 script blocks, 809 top-level functions, 155 DOM ids) and
every native Kotlin file — re-swept again for v1.4.0 by the HTML-spec expert.
Method and full extraction notes live in the build machine's `audit/` corpus;
this file is the condensed matrix the client asked for. Status legend:
**shipped** (in this APK, screenshot or test referenced) · **partial**
(engine/data shipped, some UI outstanding) · **not yet** · **not porting**
(harmful/dead/WebView-only by agreement).

Counts: **100 shipped · 14 partial · 12 not yet · 9 not porting** across 11 areas
(88 shipped in v1.3.0; v1.4.0 adds 12 — see the delta section below).

## v1.4.0 delta — found by the round-2 HTML sweep + review board

| Feature | Status | Notes |
|---|---|---|
| Group-by Performed status ('Done'/'Not Done', Not Done first — 60049) | shipped | was missing; all 6 legacy modes now live |
| Group-by Muscle Category Major/Longevity/Hands/Feet (60051) | shipped | via `Library.muscleDef().category` |
| Group-by Fitness Component (60054) + component chips w/ counts (48456) | shipped | colored chips, toggles filter |
| Expand All / Collapse All mutually exclusive pair (6707) | shipped | + per-group "Show more" (native) |
| `p4_library_collapsed` persistence, undefined ⇒ collapsed (60084) | shipped | same localStorage key, JSON map |
| Library "Test Exercise" modal — 4 verbatim steps, weight step 2.5, reps 1–15 + exact validation strings (6805/8821) | shipped | 1RM Lab → Estimate tab |
| Workout-card bracketing panel: success/fail weights, zero-rep floor, λ-blend robustEstimate1RM, `failWeight`/`testConfidence` writes (38696–38913) | shipped | 1RM Lab → Test day tab; JourneyTest covers all commit paths |
| Luxury gold header name (.user-name 2891/2909–2928) | shipped | serif + muted-gold gradient + native shimmer — `27-gold-identity-header.png` |
| Muscle Fiber Profile bars in detail modal (48526+) | shipped | first render of the extracted data |
| Exercise card media (animated pictograms) | shipped | native enhancement — legacy was text-only (sweep confirmed) |
| 1RM working-% table + plate math + test history + working-weight note | shipped | native extras, additive schema |
| Starred favorites + Pattern grouping | shipped | native extras ("organized by many") |

## 1. App shell & navigation

| Legacy feature | Status | Notes |
|---|---|---|
| 7-item top nav + retired bottom nav (P4 dock) | shipped | glass dock + More sheet (v1.2.1), dock can't disappear |
| Streak fire w/ intensity tiers (full/dim/smoke/dead) | shipped | `updateStreakFire` rules transcribed; header; tests |
| Animal-emoji avatar (64) | shipped | header + profiles picker |
| Profile name + experience + streak label | shipped | header |
| Resume Workout pill | shipped | header, draft-aware |
| More-sheet cells: themes/words/studio/my cycle → real screens | shipped | were Settings fallbacks in v1.2.1 |
| Command palette (Ctrl+K) | shipped | mobile Quick Actions screen with 15 actions |
| Theme boot (no wrong-theme flash) | shipped | ThemeController pre-set (v1.2.1) |
| Anti-debug (P4.Guard, p4AntiDebugV2), DOM freeze, ID-protect | not porting | by agreement |
| Swipe navigation between tabs | not yet | v1.2.1 "coming soon" carried |

## 2. Onboarding & consent

| Legacy feature | Status | Notes |
|---|---|---|
| 5-step wizard (name→birthdate/gender→reading level→theme→consents) | shipped | verbatim copy; demo sentence; 16 swatches |
| Minor handling (parental box, extra consent) | shipped | |
| Suppression when any user data exists | shipped | `hasAnyUserData` semantics |
| Health screen v2 (7 general + joints + 3 follow-up chains) | shipped | verbatim questions |
| Verdict tiers A/B/C (computeTier rules) | shipped | unit-tested |
| Tier-C lockout + clearance confirm | shipped | |
| Mandatory waiver checkbox + consent record | shipped | |
| 90-day layoff re-screen | shipped | `reScreenNeeded` |
| Legal Center: 13 documents verbatim | shipped | consent per doc, log capped 200 |
| Consent log export (ConsentLog_<date>.json) | partial | export API exists; Settings button pending |

## 3. Dashboard

| Legacy feature | Status | Notes |
|---|---|---|
| Readiness gauge + bands (≥80/≥60/≥40) | shipped | v1.2.1 |
| Stat cards incl. elite-date forecast + SBD/Wilks | shipped | v1.2.1 |
| Composite activity chart (0.4/0.3/0.2/0.1) | shipped | v1.2.1 |
| Backup nudge (≥3 workouts, 14d) + retest nudge (8wk) | shipped | v1.2.1 + Notify channel |
| Today's recommendation / next workout / resume | shipped | v1.2.1 |
| Daily quote card (75 quotes, banding) | partial | engine shipped w/ data verbatim; card deferred |
| LifeStage guidance card | not yet | data rules identified in audit chunk E |
| Rings row (recovery/fire/week/longevity) | shipped | v1.2.1 |

## 4. Workout & logging

| Legacy feature | Status | Notes |
|---|---|---|
| Generation engine (splits, rotation, deload, taper, roulette, athletic blocks) | shipped | v1.2.1 verbatim formulas |
| Time wizard (15/25/40/60/90/All + trimmer + Continue/Replace) | shipped | v1.2.1 |
| Logger (stepper, per-set reps, RPE chips, RIR, failure, notes) | shipped | v1.2.1 |
| 1RM test panel + failure path + re-test reminder | shipped | v1.2.1 |
| Rest timer overlay (+15s, autoRest, haptics) | shipped | v1.2.1 |
| Complete flow (unlogged confirm, summary, BIGGER/STRONGER) | shipped | v1.2.1 |
| Confetti on completion | shipped | completion-count trigger |
| Every-5-workouts backup nudge | shipped | v1.2.1 |
| Deload chip driven by notification pref | shipped | v1.2.1 wiring |
| Card badges (Needs 1RM/Ready/Started, one-open-at-a-time) | shipped | v1.2.1 |
| Longevity workout generator | shipped | v1.2.1 |

## 5. Library

| Legacy feature | Status | Notes |
|---|---|---|
| 1,558 exercises verbatim (587+569+256+146 groups) | shipped | assets/data/exercises.json |
| Lazy/chunked rendering, collapsed groups | shipped | v1.2.1 |
| Search + group-by (muscle/equipment/difficulty) | shipped | v1.2.1 |
| Synonym expansion (VS Code style) + fuzzy rank | shipped | asset + engine, this build |
| Clickable muscle tags → Muscle Wiki | shipped | this build |
| "How to do" / Wikipedia / video lookups | shipped | Muscle Wiki screen + fallbacks |
| Instruction detail (cues, mistakes, breathing, tempo) | partial | base instructions ship; extended coaching block subset |
| Library Studio (author/delete custom exercises) | shipped | this build |
| Library export `exercise-library-YYYY-MM-DD.json` | shipped | legacy shape |
| Library import w/ merge report | shipped | add/skip report |
| "AI helper" | partial | rule-based suggestions, honestly labeled |

## 6. History & Progress

| Legacy feature | Status | Notes |
|---|---|---|
| 5 filter tabs, expandable rows, delete confirm | shipped | v1.2.1 |
| Notes view/edit + CSV export | shipped | v1.2.1 |
| Fix Log (1RM/fatigue recalc on edit) | partial | inline edits ship; modal re-calc subset |
| Volume bars / strength trend / forecast / mastery | shipped | v1.2.1 |
| Radar chart (components) | not yet | audit doc'd; data model present |
| RPE histogram + zone stats | not yet | |
| Records board | not yet | |
| Goal-cycle card + 5-year vision ladder | partial | GoalCycle model ships; UI deferred |
| Frequency dot-strip (P6E) | not yet | |
| Floating exercise pill rail | not yet | |

## 7. Recovery & Cycle

| Legacy feature | Status | Notes |
|---|---|---|
| 49-muscle readiness, 4 categories, recommendations | shipped | v1.2.1 |
| Systemic recovery factor + deload detection | shipped | v1.2.1 |
| My Cycle: period entry, phase compute, engine multipliers | shipped | 0.6/1.0/1.05/0.8 unit-tested |
| SmartMessageBank (~50 msgs, no-repeat rotation) | shipped | asset + rotation |
| Period "Queen ✨" celebration | not yet | copy captured in audit |
| Severity scoring (savePeriodData) | partial | phase + message ship; severity slider deferred |

## 8. Data safety

| Legacy feature | Status | Notes |
|---|---|---|
| Format A export/import | shipped | v1.2.1, byte-compat |
| PeakForm-Complete-Backup-v1 | shipped | byte-compat |
| Schema-2 bundle (`{_schema:2,_app:'Pro'…}`) | partial | reads accepted; writer stays v1 for compat |
| Import warning dialog (legacy copy) | shipped | this build |
| JSON healing (fences/smart quotes/ellipsis/dashes/commas/NaN) | shipped | 7 unit tests |
| Guarded snapshots (never auto-restore) | shipped | v1.2.1 + vault list |
| Vault snapshot list + tap-to-restore | shipped | this build |
| Profile namespaces (p4_profile_data_) | shipped | files-based namespaces, instant switch |
| Emergency backup (draft, v2) | shipped | v1.2.1 |
| Type-RESET danger zone + pre-reset snapshot | shipped | v1.2.1 |

## 9. Motivation

| Legacy feature | Status | Notes |
|---|---|---|
| 101 badges (15 check types) | shipped | verbatim + gallery |
| 75 quotes banded | shipped | engine |
| 572 tips, 26 domains, banded | shipped | engine |
| Streak recovery (honor system) | shipped | |
| Share text generator (5 styles, lottery/off-topic rates) | shipped | |
| QR code share | shipped | zxing, shares workout text (legacy QR'd the URL; text QR is strictly more useful — documented deviation) |
| System share sheet | shipped | |
| Win/moment celebrations beyond completion confetti | not yet | |

## 10. Vocabulary / reading levels

| Legacy feature | Status | Notes |
|---|---|---|
| P4_VOCAB_LADDERS (187 keys × 10) | shipped | verbatim asset |
| Reading level L1–10 selection + persistence | shipped | onboarding + Settings |
| Glossary overlay (search, key→variant) | shipped | first 120 rows + search |
| Phrasebook export (`try4ever-fitness-phrasebook`) | shipped | legacy JSON shape |
| Phrasebook import (length-10 validation) | shipped | custom overrides stored |
| DOM-wide text substitution engine | not porting | WebView-only mechanism; native uses keys directly |
| 8-language phrase starters (P4_PHRASE_STARTERS) | not porting | unwired sample data in legacy |

## 11. Native-only improvements (this build)

| Feature | Status |
|---|---|
| System notifications (retest/deload, channels, toggles, boot reschedule) | shipped |
| Home-screen widget (streak/readiness/next action) | shipped |
| Quick-settings tile (rest timer) | shipped |
| Predictive back gesture | shipped |
| Share-to-app (receive) | shipped |
| TalkBack groundwork on new surfaces | partial |
| Material You dynamic color | not yet |
| FLAG_SECURE screenshot-safe mode | not yet |
| Health Connect full sync | not yet (permissions declared) |
| Voice logging | not yet |
| Wear OS tiles | not yet (separate APK) |
| Google Drive backup | not yet (needs OAuth client from client) |
| Android Auto PiP | not yet |
| Offline AI model | not porting (rule-based helper ships instead) |

## Intentionally not ported (unchanged from v1.2.1 policy)

Anti-debug guards · vault blind autoRestore · remote CDN boot dependency ·
GAS backend telemetry · paywall/subscription gates (legacy shipped state is
"everything unlocked") · 404-style paywall takeover · free-license DM
application wizard (16-platform social flow) · notification suppression glue ·
UA fingerprinting · device-id telemetry.
