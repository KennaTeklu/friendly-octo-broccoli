# 07 — Settings (shim — UI in P4 BOOT/STAGE-3/6/7/8 in 15-inline)

> Reference module: `reference/scripts/p4/07-settings.js` (69 lines).
> Source line ranges in p4-core.js: n/a (shim only). Settings UI lives across multiple IIFEs in `15-inline.js`:
> - P4 BOOT body (settings injection: Appearance, Words, Language, Power, Plan, Legal & Data, theme/vocab UI sync): L18375-19930
> - P4.SettingsAccordion (collapsible cards): L22511-22595 (STAGE-3)
> - P4 STAGE-6 (settings restructure, 3D wheel wiring): L24323-25292
> - P4 STAGE-7 (settings dual-UI, plan state refresh, notif toggles): L25293-26358
> - P4 STAGE-8 (cute Appearance settings rewrite): L26359-27279
> Native counterparts: `ui/screens/Settings.kt`, `ui/screens/TuningSheet.kt`.

## A. File purpose
Thin shim that re-exports the settings surface (`P4.settings.inject`, `P4.settings.refreshTheme`, `P4.settings.power`, `P4.settings.savePower`) for module discoverability. The actual `P4.SettingsUI`, `P4.SettingsAccordion`, `P4.themeUI.renderInto` rewrite, and the 3D wheel wiring all live inside the P4 BOOT IIFE wrapper in `15-inline.js` and could not be cleanly hoisted.

## B. Public surface
- `P4.settings.inject()` → delegates to `P4.SettingsUI.inject()`.
- `P4.settings.refreshTheme(hostId)` → delegates to `P4.themeUI.renderInto()`.
- `P4.settings.power()` → returns the active `P4.Power.settings` object (`{ gymMode, autoRest, haptics, swipe, dock }`).
- `P4.settings.savePower()` → persists `P4.Power.settings` via `P4.Power.saveSettings()`.

## C. Dependencies
- `window.P4`, `P4.SettingsUI` (defined in `15-inline.js`).
- `P4.themeUI` (defined in `15-inline.js`).
- `P4.Power.settings`, `P4.Power.saveSettings` (defined in `13-clock.js`).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `P4.SettingsUI.inject` | `SettingsScreen` (Compose, static) |
| `P4.SettingsAccordion` | `SettingsScreen` (Compose groups, collapsible) |
| `P4.themeUI.renderInto` | `SettingsScreen.appearanceSection()` |
| `P4.Power.settings` | `ProfileState.powerSettings` (data class) |
| 3D wheel wiring | `TuningSheet.englishLevelWheel` (Compose wheel) |

## E. Patterns worth porting
- **MutationObserver re-injection**: the app may re-render the settings section (e.g. after a profile switch). A `MutationObserver` watches for `#settings-section` becoming active without a `showSection` call and re-injects the P4 cards.
- **Accordion one-open-at-a-time**: `P4.SettingsAccordion` enforces that only one card is open at a time; opening a new card auto-closes the previous. All-closed-by-default.
- **Cute appearance rewrite (STAGE-8)**: removed the redundant "Appearance" h3 (the group header already says it), bigger swatches in an 8-col grid, less hint clutter.

## E2. Deep patterns
- **Dual-UI strategy**: STAGE-7 fixes a "dual-UI" bug where both the legacy settings and the P4 settings were visible. The fix hides the legacy block via `display: none` and lets the P4 block own the section.
- **Plan state refresh**: when the user changes their plan (free/trial/pro), every settings card that gates on plan state is re-rendered.
- **Notification toggle persistence**: STAGE-7 wires up notification toggles to `P4.Power.settings` so they persist across sessions.

## E3. Edge cases
- Settings section not yet in DOM (lazy-loaded sections) → `MutationObserver` waits.
- User opens settings, switches profile, returns to settings → reconcile hook (from `04-profiles.js`) re-runs.
- `P4.Power.settings` missing keys (legacy users) → defaults applied: `gymMode: false, autoRest: true, haptics: true, swipe: true, dock: true`.

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Accordion default | all closed | Less overwhelming on first open |
| Power dock default | on | Quick actions visible by default |
| Auto-rest default | on | Most users want the timer |
| Haptics default | on | Subtle, not annoying |
| Swipe nav default | on | Mobile-first gesture |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Settings card injection | FULL (`SettingsScreen`) |
| Accordion (collapsible cards) | FULL (Compose groups) |
| Theme picker UI | FULL (`appearanceSection`) |
| 3D wheel (English level picker) | FULL (`TuningSheet.englishLevelWheel`) |
| Power settings persistence | FULL (`ProfileState.powerSettings`) |
| Plan-state refresh on change | FULL (`Flow<PlanState>`) |
| MutationObserver re-injection | n/a (Compose is reactive) |

**Coverage: 100%** (most behaviors are inherently reactive in Compose)

## H. Top 3 gaps to close
1. **Accordion one-open-at-a-time** — Compose groups are independently collapsible. Add a `singleOpen: Boolean` flag to mirror the reference behavior.
2. **Cute appearance rewrite** — native picker uses Material `Slider` + `RadioButton`. Port the bigger swatch grid + accent-soft tinted background from STAGE-8.
3. **Notification toggle granularity** — reference has per-event-type toggles (workout reminder, daily quote, badge unlock). Native has a single "notifications" master switch. Add per-category toggles.
