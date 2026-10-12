# 09-period-dashboard — Period tracking UI, SmartMessageBank, dashboard, exercise search

Source: `reference/scripts/app/09-period-dashboard.js` (L58732–59655 of original `app.js`)

## A. File purpose
This module packs three responsibilities: (1) `SmartMessageBank` — a 150+ message corpus organized by menstrual phase (menstrual/follicular/ovulatory/luteal/any), tone (gentle/witty/motivational/nature/sarcastic/queen/competitive/fun), severity range, sarcasm gating, with used-history tracking in localStorage and tone-rotation anti-repetition; (2) period-tracking UI (`savePeriodData`) writing into `workoutData.user.menstrual`; (3) dashboard and exercise-search glue functions referenced by other modules.

## B. Public surface
- `SmartMessageBank` — singleton with:
  - `messages[]` — array of `{text, phases[], tone, minSeverity, maxSeverity, requiresSarcasm?}`.
  - `usedMessageIndices[]`, `lastTone` — anti-repetition state.
  - `loadHistory()`, `saveHistory()` — localStorage round-trip of used-set.
  - `selectMessage(phase, severity, goal, sarcasmEnabled)` — filters by phase/`any`, severity range, sarcasm gate; prefers unused candidates with tone different from `lastTone`; resets history when pool exhausted; returns text.
- `savePeriodData()` — async; reads `lastPeriodDate`, `cycleLength`, `periodSymptoms` from DOM, validates, writes `workoutData.user.menstrual.{lastPeriodStart, cycleLength, symptoms[]}`, persists, closes modal.
- Dashboard/exercise-search helpers — referenced by `10-init-nav.js::showSection` cases.

## C. Dependencies
- Globals: `localStorage`, `document`, `Swal`, `Math`, `Date`.
- Cross-module: `workoutData.user.menstrual`, `saveToLocalStorage`, `closeModal`.

## D. Native-equivalent mapping
- `SmartMessageBank` → `core/MessageBank.kt::MessageBank` (L9) — ported but **only a subset** ("subset port" per source comment L7). Native `BANK` has ~30 messages, legacy has 150+. Tone rotation logic is implemented (`pick` L30) but sarcasm gating by user preference is **GAP**.
- `SmartMessageBank.loadHistory/saveHistory` → **GAP**. Native `MessageBank.pick` does not persist used-set across launches — messages can repeat within a session; legacy guarantees non-repetition until pool exhausted.
- `savePeriodData` → `ui/screens/MyCycle.kt::MyCycleScreen` (L31) — Compose UI writes into `ProState.data.user.menstrual` via `MenstrualState` (Model.kt L139) and `SymptomEntry` (L146).
- Dashboard glue → `ui/screens/Dashboard.kt::DashboardScreen` (L37) + `Stats.dashboardComposite` (Stats L564) + `Stats.ringsData` (L528).
- Exercise search glue → `ui/screens/Library.kt::LibraryScreen` (fuzzy search via Fuse.js equivalent).

## E. Patterns worth porting
- **150+ message corpus with phase × tone × severity tagging** — native has ~30. The depth matters: cycling through motivational variants reduces banner-blindness.
- **`usedMessageIndices` persistence across launches** — guarantees no message repeats until the eligible pool is exhausted, then resets. High-value port for native (`MessageBank` currently has no memory between sessions).
- **Tone rotation anti-repetition** — even among unused messages, prefer a different tone from the last one shown. Subtle but effective at keeping the daily coach line feeling fresh.
- **Sarcasm gating** — user preference flag (`sarcasmEnabled`) gates sarcastic messages; respectful default. Native has sarcastic messages in `MessageBank.BANK` but no gating.
- **Phase × severity × goal multi-filter** — native `MessageBank.pick` filters by phase only, ignoring severity/goal context. Porting the full filter chain would make the coach feel smarter during high-fatigue / ovulatory windows.

## F. Feature IDs
`P09-SMART-MESSAGE-BANK`, `P09-USED-MESSAGE-PERSISTENCE`, `P09-TONE-ROTATION`, `P09-SARCASM-GATING`, `P09-PERIOD-TRACKING-UI`, `P09-DASHBOARD-GLUE`.
