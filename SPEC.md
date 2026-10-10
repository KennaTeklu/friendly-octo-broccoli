# SPEC.md — what 'clone the HTML app' actually means

The legacy HTML app is at https://github.com/example-user/cautious-enigma (file: index (24).html, 68,208 lines).

Every item below exists in that HTML file. Every one of them must exist in the native Kotlin/Compose app with the same look, same behavior, same data. This is the feature list. If an item is missing from the native app, it is a gap. Port it.

Where a feature is impossible on Android (desktop-only nav, browser-only quirks), say so in CHANGES.md and skip it — do not silently drop it.

## A. Top-level pages (7)

The HTML app has 7 full-page sections. Each one must exist as a Compose screen in the native app, matching the content and layout of the HTML version.

1. **dashboard-section** — main home. Streak fire, today's workout card with Generate/Resume buttons, stats grid (volume/streak/PRs), longevity score card, readiness bars, life-stage guidance card, badges preview, backup nudge banner.
2. **workout-section** — the training session. Exercise cards with sets/reps/weight loggers, rest timer, superset / drop set / warmup support, RPE selector, plate math, 'How to do' and 'Images' buttons per exercise, muscle tags, resume/replace/skip controls, momentum modal on completion, confetti animation.
3. **library-section** — the exercise database. 1,413 movements with 8 group-by modes (Muscle / Equipment / Difficulty / Performed / Category / Component / etc.), Expand All / Collapse All, fitness-component filter chips, search, per-card badges ('Done Nx', 'New', difficulty chip), tested 1RM / Est. 1RM / Last / Next progression lines, clickable muscle tags, animated exercise pictograms, custom exercise indicator, 'Add to Workout' button.
4. **progress-section** — the analytics page. Longevity circle, strength forecast, volume bars by muscle, 11-component radar, 8-week frequency chart, RPE histogram, records board, goal-cycle status, 3-month projection, 5-year Monte Carlo forecast, per-muscle sparklines.
5. **recovery-section** — fatigue and readiness. 49-muscle readiness list, coupling matrix visualization, decay model UI, recovery bands (fully recovered / recovering / fatigued), per-muscle fatigue bars.
6. **history-section** — the log of past sessions. Chronological session list, full-text search across notes and exercise names, per-session notes, PR markers, edit/fix past log entries, exercise history detail.
7. **settings-section** — the settings hub. All cards listed in section D below.

## B. Modals (13 total — 10 to port, 3 to skip)

Each modal is a dialog that appears over the current page. Match the HTML's trigger, content, and behavior.

1. **addExerciseModal** — form to add a custom exercise. Fields: name (=90 chars), steps (=8 × 220 chars), cues (=6 × 160 chars), https-only image URL, muscle group, equipment, difficulty.
2. **exerciseSearchModal** — search dialog that filters the library by name, muscle, equipment, or component.
3. **fixLogModal** — edit a past workout log entry. Triggered from History.
4. **importExerciseModal** — import custom exercise definitions from JSON. Tolerates code fences, trailing commas, smart quotes.
5. **importModal** — import full workout data JSON. Pre-import warning dialog ('This will replace all your current data. Continue?'). Restores Format A (bare workoutData + exportDeviceId + exportTimestamp), Format B (kind: try4ever-fitness-backup with sibling fields), Format C (__format: PeakForm-Complete-Backup-v1).
6. **longevityModal** — Longevity Workout generator. Age-adjusted training suggestions from the longevity engine.
7. **periodModal** — My Cycle tracker. Last period start, cycle length, phase display (follicular / ovulation / luteal / menstrual) with legacy load multipliers (0.6 / 1.0 / 1.05 / 0.8), phase-aware message bank.
8. **qrModal** — QR code + share text for the current workout. Rendered locally (ZXing or equivalent), system share sheet.
9. **quickActionsModal** — Quick Actions menu. Buttons: Export Data, Longevity Workout, plus any others in the HTML. Currently buttons are injected dynamically by JS.
10. **testExerciseModal** — 1RM Test flow. 4-step verbatim modal: bracket entry, warmup set, working set, result confirm. Persists to legacy record fields.

**Skip these (monetization only):**
- billing-manager-modal, freeWizardModal, paidModal, paywall-overlay — subscription/checkout UI. Not wanted.

## C. P4 subsystems (47 modules)

Each P4 module is a self-contained behavior in the HTML app. Port the ones that make sense on Android; skip the ones flagged.

- **ACCENTS** — accent color palette + swatches
- **Auditor** — self-diagnostic / info report (optional)
- **Backup** — export/import + snapshot management
- **Badges** — 101 badge definitions + evaluation engine (workouts, streaks, volume, PRs, variety, backups, studio, imports, exports, early-bird, night-owl, comeback, gym, vocab, themes)
- **Bodyweight** — bodyweight log + trend
- **buzz** — haptic feedback helper ? native Vibrator
- **copyText** — clipboard copy ? native ClipboardManager
- **debounce** — throttle helper (JS-only, skip)
- **download** — file download helper ? SAF / share sheet
- **Dropdown** — dropdown UI component
- **Emoji** — emoji avatar system (64 animals)
- **EmojiPicker** — avatar picker sheet
- **fmtDate** — date formatter ? native DateTimeFormatter
- **Guard** — anti-tamper guard (do NOT port — see LANDMINES.md)
- **Health** — PAR-Q health screen + joint picker + verdict tiers
- **InApp** — in-app messages (optional)
- **LANGS** — language list + locale handling
- **Legal** — legal center (13 documents + consent log)
- **LifeStage** — life-stage guidance (age-banded training advice)
- **Motivation** — motivational message banks
- **Nav** — navigation + sheet routing
- **NavPadding** — content padding for the fixed nav bar
- **normalizeComponents** — normalize fitness component data
- **notifAllowed** — notification permission check
- **Onboard** — 5-step onboarding wizard (welcome/name ? birthdate+gender ? reading level ? theme ? consent) with resumable draft
- **Paywall** — subscription (do NOT port)
- **pick** — object pick helper (JS-only, skip)
- **Picker** — profile picker overlay
- **Power** — power user settings + toggles
- **Profiles** — multi-profile system with per-profile data isolation
- **Projections** — 3-month + 5-year forecasting
- **replaceWorkoutData** — replace workout state on import/restore
- **Rings** — concentric progress rings (dashboard)
- **SEC** — security/escape helpers (HTML sanitization — verify natively)
- **SettingsAccordion** — expandable settings card component
- **SettingsEnv** — training environment settings
- **SettingsUI** — inject settings cards dynamically
- **store** — key-value store (localStorage in HTML ? DataStore natively)
- **Studio** — Library Studio: add / export / import & merge / AI helper / feedback
- **Theme** — light / dark / system + accent
- **themeUI** — theme picker UI
- **toast** — toast notifications ? native Snackbar
- **todayKey** — YYYY-MM-DD today helper
- **uid** — unique ID generator
- **User** — user profile object
- **Vault** — snapshot store (auto-restore disabled — see LANDMINES.md)
- **Vocab** — vocabulary phrase ladders (35 starter × 10 levels)
- **VocabLevels** — reading level L1–L10 system
- **vocabUI** — vocab UI
- **Wheel** — wheel/scroll picker component
- **WorkoutCards** — workout card renderer

## D. Settings cards (20+)

Every settings card must exist. Card IDs detected in the HTML:

- p4PersonalCard — personal info (name, birthdate, gender, weight, height, experience, goal)
- p4AppearanceCard — theme, accent, font size
- p4LanguageCard / p4LookLangCard — language + reading level
- p4DataCard — Data & backup (export, import, snapshots, reset)
- p4TrainingEnvCard — training environment (gym/home, equipment)
- p4LifeCard — life stage (age-based guidance)
- p4PowerCard — power user toggles (muscle coupling, aggression slider, express mode)
- p4ReadingCard — reading level + phrase ladders
- p4StudioCard — Library Studio entry
- p4PickerCard — profile picker entry
- p4LibMgmtCard — library management
- p4PlanCard / p4PlanLegalCard — plan/subscription (SKIP — monetization)
- p4NotifCard — notifications
- p4VocabCard — vocabulary
- p4ThemeCard — theme picker
- p4StreamGroupCard — streaming/group-by prefs
- p4CpCard — command palette / control panel
- p4LegalCard — legal center entry

## E. How to verify you shipped it

For each of the sections, modals, P4 subsystems, and settings cards above:

1. Open the HTML app in a browser at https://github.com/example-user/cautious-enigma (raw or via GitHub Pages).
2. Open the native app side by side.
3. Tap into the same feature. If the native version is missing, incomplete, or visually different, it is a gap.
4. Fix the gap. Take a screenshot. Add it to docs/verify/.
5. List it in CHANGES.md as ? with the screenshot filename.

Missing screenshots = unverified features = do not claim them.

## F. Rules for this build

- Do not skip a feature because it is hard. If it is impossible on Android, say so explicitly and mark it desktop-only.
- Do not port the anti-debug guard, vault auto-restore, paywall, or legal center flows — see LANDMINES.md.
- Do not invent features the HTML does not have.
- Do not drop features the HTML does have.
- Do not claim a feature without a screenshot.

---

## Appendix — extracted feature data

The full feature inventory extracted from the HTML is in the `spec/` folder. Read these alongside SPEC.md:

- `spec/A-functions.md` — 748 JavaScript function names from the HTML app
- `spec/B-modules.md` — 35 P4 modules with their methods
- `spec/C-storage.md` — 33 localStorage/sessionStorage keys
- `spec/D-ids.md` — 324 UI element IDs
- `spec/E-css.md` — 569 CSS classes defining the visual identity
- `spec/G-datakeys.md` — 193 data model keys (import/export round-trip compatibility)

Any function, module, method, key, ID, or class in these appendices that does not have a native equivalent is a gap. Every gap must be either implemented or explicitly marked desktop-only in CHANGES.md.
