# reference/ — modularized HTML spec

The legacy HTML app lives at https://github.com/example-user/cautious-enigma
(file: `index (24).html`, 68,208 lines, single file). Every future Pro batch
starts by grepping that file, which is slow and error-prone.

This folder is the **modularized reference**: the same file split into a
folder tree so each screen, style, and script is independently inspectable.

## How to open it

The reference is a static site. From this `reference/` folder, run:

    python3 -m http.server 8080

Then open http://localhost:8080/ in a browser.

Why a static server? `index.html` uses `fetch()` to load the per-screen HTML
fragments from `sections/`. Chrome blocks `fetch()` on `file://` URLs for
security; Firefox allows it. A static server works in every browser.

To suppress the anti-debug guard (which can blank the page if devtools is
open), append `?p4_nodebug=1` to the URL, or set
`localStorage.setItem('p4_nodebug', '1')` in the console once.

## Structure

    reference/
      index.html              # single entry — imports the rest
      sections/
        dashboard.html        # L6270-6365   — main home screen
        workout.html          # L6368-6421   — training session
        history.html          # L6424-6459   — workout history
        progress.html         # L6462-6546   — analytics
        recovery.html         # L6549-6586   — muscle recovery
        settings.html         # L6589-6688   — settings hub
        library.html          # L6692-6719   — exercise database
        modals.html           # L6722-6932 + L49070-49101 + L62186-63450
                              # all modals, loading, bottom nav, P4 overlays, paywall
      styles/
        base.css              # L30-3572, L3645-5501, L5984-6002
                              # reset, root vars, foundational styles, browser fix, reduce-motion
        components.css        # L5801-5834, L63509-63529, L63767-63773, L64388-64404
                              # card state, time wizard, workout card wrapper, fix15 glow
        sections.css          # L5666-5983, L62188-62275, L64573-64636
                              # per-screen styles (settings search, tabbar, health, navbar, paywall, health v2)
        themes.css            # L5502-5665
                              # dark mode fixes
      scripts/
        p4-core.js            # L3644, L6002-6130, L47489-47919, L49103-62165, L62166-63508,
                              # L62413-63450, L63451-63508, L63745-63766, L64260-64387,
                              # L64637-65057, L65416-66282, L67592-67761, L67947-68206
                              # P4 namespace + shared helpers (toast/debounce/download/store) +
                              # P4 layer (all subsystems) + cross-cutting P4 fixes + floating pill +
                              # anti-debug + P4_EXERCISE_COMPONENTS registry
        p4-store.js           # PLACEHOLDER — P4.store + P4.keys live inside p4-core.js IIFE (L55058-55211)
        p4-profiles.js        # PLACEHOLDER — P4.Profiles lives inside p4-core.js IIFE (L58572-58728)
        p4-vault.js           # PLACEHOLDER — P4.Vault lives inside p4-core.js IIFE (L58731-58818)
        app.js                # L6933-47485 — main 40,553-line app script (flat globals, all features intermingled)
                              # Added beyond the required 11-file structure because pure extraction
                              # prohibits surgical feature-splitting of this monolith.
        dashboard.js          # PLACEHOLDER — dashboard functions are in app.js + progress.js
        workout.js            # L63530-63744, L63774-63904, L64405-64569, L67762-67867, L67868-67946
                              # gym-time wizard, workout card collapse, time wizard glow,
                              # 1RM auto-open, continue badge
        library.js            # L66283-67129, L67130-67336
                              # bodyweight exercise library + logging infra
        progress.js           # L47920-49046
                              # masterpiece engine (11-component radar, goal cycles, 5-year forecast)
        recovery.js           # PLACEHOLDER — recovery functions are in app.js (10 disjoint ranges)
        history.js            # PLACEHOLDER — history functions are in app.js (L41226-41567, L46646-46895)
        settings.js           # L63905-64011, L64012-64062, L64063-64259, L65061-65415,
                              # L67337-67437, L67439-67484, L67485-67591
                              # sheet cleanup, dock import, avatar picker, settings search, training env
      README.md               # this file

## Why some `scripts/*.js` files are placeholders

The task structure requires 11 script files (`p4-core`, `p4-store`,
`p4-profiles`, `p4-vault`, `dashboard`, `workout`, `library`, `progress`,
`recovery`, `history`, `settings`). The original HTML's architecture doesn't
neatly map onto this structure for three reasons:

1. **P4.store, P4.Profiles, P4.Vault are defined INSIDE one big IIFE** in the
   P4 layer script (L49103-62165). Extracting them to separate files would
   require breaking the IIFE — a rewrite, violating the "pure extraction"
   rule. They all live in `p4-core.js`; the placeholder files document where
   to find them in the monolith.

2. **The main 40,553-line script (L6933-47485) is a flat global namespace**
   with no internal module boundaries. Dashboard, workout, recovery, history,
   and settings functions are intermingled with shared globals and helper
   functions. Surgical feature-splitting would require rewriting (violating
   "pure extraction") and risks breaking execution-order dependencies
   (IIFEs, `addEventListener` calls, hoisted function declarations). The
   main script lives in `app.js`; the per-feature placeholder files
   (`dashboard.js`, `recovery.js`, `history.js`) document where each
   feature's functions live in the monolith.

3. **`workout.js`, `library.js`, `progress.js`, `settings.js`** contain the
   LATER, clearly-separable `<script>` blocks that were added to the file
   after the main script (P4 fix scripts, masterpiece engine, bodyweight
   library, settings-env scripts). These ARE cleanly extractable because
   each is a self-contained IIFE that hooks into existing globals.

## Why `app.js` is loaded before `p4-core.js`

The task says "p4-core.js is loaded first" — interpreted here as "first
among the P4 subsystem files". The main app script (`app.js`) is a separate
concern: it defines the flat globals (`workoutData`, `currentWorkout`,
`ultimateExerciseLibrary`, etc.) and functions (`exportWorkoutData`,
`performGenerateWorkout`, `renderLibrary`, etc.) that the P4 fix scripts in
`p4-core.js` patch at parse time.

If `p4-core.js` loaded before `app.js`, the P4 fix IIFEs would bail (their
guards check `typeof window.exportWorkoutData !== 'function'` and return
early), and the patches would never apply — breaking behavior.

Actual load order in `index.html`:

    1. app.js              (main script — defines all globals + functions)
    2. p4-core.js          (P4 namespace + shared helpers + P4 layer + cross-cutting fixes)
    3. p4-store.js         (placeholder)
    4. p4-profiles.js      (placeholder)
    5. p4-vault.js         (placeholder)
    6. library.js          (bodyweight library + infra)
    7. workout.js          (card collapse, time wizard, 1RM auto-open, continue badge)
    8. progress.js         (masterpiece engine)
    9. recovery.js         (placeholder)
    10. history.js         (placeholder)
    11. dashboard.js       (placeholder)
    12. settings.js        (settings fixes)

## Line-number comments

Every extracted file has a header comment block noting the exact source line
range(s) in `index (24).html`. Each contiguous block is also wrapped with
`BEGIN extracted from ... L<n>-<m>` / `END extracted from ...` markers. This
lets you jump back to the monolith when you need surrounding context.

## What lives in each P4 subsystem (for grep navigation)

| Subsystem | Monolith line range | What it does |
|---|---|---|
| `P4` (root) | L55058-55072 | namespace, MAGIC canary, version, `keys` map |
| `P4.SEC` | L55075-55180 | HTML/text escaping, sanitization |
| `P4.store` | L55181-55211 | localStorage wrapper (`get`/`set`/`del`/`snapshot`) |
| `P4.toast` | L55212-55254 | toast notification host |
| `P4.ACCENTS` | L55254-55256 | accent color palette |
| `P4.Theme` | L55256-55307 | theme mode + accent + gym flag |
| `P4.debounce` | L55307-55316 | throttle helper |
| `P4.download` | L55316-55370 | file download helper |
| `P4.Vocab` | L55370-55565 | reading-level phrase ladders |
| `P4.Nav` | L55565-55757 | navigation + sheet routing |
| `P4.Power` | L55757-55913 | power user settings + toggles |
| `P4.Backup` | L55913-56097 | export/import + snapshot management |
| `P4.Motivation` | L56097-56401 | motivational message banks |
| `P4.Legal` | L56401-56508 | legal center (13 docs + consent log) — SKIP in native per LANDMINES |
| `P4.Onboard` | L56508-57163 | 5-step onboarding wizard |
| `P4.Studio` | L57163-57895 | Library Studio (add/export/import/AI/feedback) |
| `P4.SettingsUI` | L57895-58162 | inject settings cards dynamically |
| `P4.Rings` | L58162-58233 | concentric progress rings |
| `P4.LifeStage` | L58233-58495 | life-stage guidance (age-banded advice) |
| `P4.Emoji` | L58495-58572 | emoji avatar system (64 animals) |
| `P4.Profiles` | L58572-58728 | multi-profile system with per-profile isolation |
| `P4.Vault` | L58731-58818 | per-profile vault snapshots (IndexedDB) — autoRestore DISABLED in native per LANDMINES |
| `P4.WorkoutCards` | L59847-? | workout card renderer (collapsed-by-default wrapper) |
| `P4.Health` | L64830-? | PAR-Q health screen + joint picker + verdict tiers |
| `P4.Bodyweight` | L67158-? | bodyweight log + trend |

## Cross-reference to SPEC.md

Each item in `friendly-octo-broccoli/SPEC.md` (sections A-F) maps to a line
range in this reference. Use the table above to jump from SPEC.md's feature
description to the monolith source.

| SPEC.md item | Reference location |
|---|---|
| Section A.1 dashboard | `sections/dashboard.html` + `scripts/app.js` (L6933-47485, scattered) + `scripts/progress.js` (masterpiece engine) |
| Section A.2 workout | `sections/workout.html` + `scripts/app.js` (scattered) + `scripts/workout.js` |
| Section A.3 library | `sections/library.html` + `scripts/app.js` (L9080-35057 ultimateExerciseLibrary) + `scripts/library.js` |
| Section A.4 progress | `sections/progress.html` + `scripts/app.js` (scattered) + `scripts/progress.js` |
| Section A.5 recovery | `sections/recovery.html` + `scripts/app.js` (10 disjoint ranges — see `scripts/recovery.js` placeholder) |
| Section A.6 history | `sections/history.html` + `scripts/app.js` (L41226-41567, L46646-46895) |
| Section A.7 settings | `sections/settings.html` + `scripts/app.js` (scattered) + `scripts/settings.js` |
| Section B modals | `sections/modals.html` |
| Section C P4 subsystems | `scripts/p4-core.js` (P4 layer L49103-62165) |
| Section D settings cards | `sections/settings.html` + `scripts/settings.js` + `scripts/p4-core.js` (P4.SettingsUI) |
