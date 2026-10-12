# 02 — Theme (shim — engine in 01-core, UI in 15-inline)

> Reference module: `reference/scripts/p4/02-theme.js` (74 lines).
> Source line ranges in p4-core.js: n/a (shim only). Engine lives in L13423-L13539 of P4 CORE IIFE (file 01-core.js). Picker UI lives in L18375+ of P4 BOOT IIFE (file 15-inline.js). Appearance rewrite lives in L26917-L27107 of STAGE-8 (file 15-inline.js).
> Native counterparts: `ui/ProTheme.kt`, `ui/ThemeController.kt`, `ui/ProApp.kt` (theme boot).

## A. File purpose
This is a thin shim that re-exports the theme surface (`P4.theme.current`, `P4.theme.apply`, `P4.theme.accents`, `P4.theme.isDark`, `P4.theme.isGym`) for module discoverability. The actual theme engine (`P4.ACCENTS`, `P4.Theme.get/set/apply/gym/gymOn`) is defined inside the **P4 CORE IIFE** in `01-core.js` and cannot be hoisted out without breaking the closure-scoped `store` reference. The theme picker UI (`P4.themeUI.renderInto`) is defined inside the **P4 BOOT IIFE** in `15-inline.js`.

## B. Public surface
- `P4.theme.current()` → `{ mode, accent }`
- `P4.theme.apply(patch, opts)` → delegates to `P4.Theme.set()`
- `P4.theme.accents()` → 16-entry ACCENTS array
- `P4.theme.isDark()` → `true` if `mode === 'dark'`
- `P4.theme.isGym()` → `true` if gym mode (giant buttons) is on

## C. Dependencies
- `window.P4` (from `01-core.js`)
- `P4.Theme` (defined in `01-core.js`)
- `P4.ACCENTS` (defined in `01-core.js`)
- `P4.toast` (for the boot-ready info toast)

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `P4.theme.current` | `ThemeController.currentTheme()` |
| `P4.theme.apply` | `ThemeController.apply(patch)` |
| `P4.theme.accents` | `ProTheme.ACCENTS` list |
| `P4.theme.isDark` | `ThemeController.isDarkMode()` |
| `P4.theme.isGym` | `Accessibility.isGymMode()` |

## E. Patterns worth porting
- **Lazy delegation**: the shim does not duplicate state — it always reads from the source of truth via `P4.Theme.get()`. No risk of drift.
- **Boot toast**: emits a one-line "Theme shim ready" toast so QA can verify load order via the visible toast stack.

## E2. Deep patterns
- **Cross-reference convention**: when a concern spans two IIFEs (engine in CORE, UI in BOOT), the shim file documents both locations so future readers can find the code without grepping.
- **Idempotent guard**: `if (!window.P4 || !P4.Theme) return;` — no-op if loaded before `01-core.js`.

## E3. Edge cases
- `01-core.js` failed to load → shim returns early, no exception, no broken references.
- `P4.themeUI` not yet defined (load order: `02-theme` before `15-inline`) → shim does NOT call `P4.themeUI.sync()`; the actual `P4.Theme.set()` call inside `01-core.js` guards with `if (P4.themeUI) P4.themeUI.sync();`.

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Shim boot toast duration | 1200 ms | Short — diagnostic only, not user-facing |
| Shim toast kind | `info` | Not a warning, not a success |
| No state mutation in shim | true | Single source of truth is `P4.Theme` |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Theme engine (16 accents, dark/light, gym mode) | FULL (in 01-core) |
| Theme picker UI (mode buttons + swatches + gym toggle) | FULL (in 15-inline) |
| Appearance rewrite (cute swatches, no redundant h3) | FULL (in 15-inline STAGE-8) |
| Theme boot (pre-paint to prevent flash) | PARTIAL (in index.html inline script, native uses splash) |

**Coverage: 95%**

## H. Top 3 gaps to close
1. **Pre-paint theme flash prevention** — index.html has an inline IIFE that reads `p4_theme` from localStorage and sets `data-mode`/`data-accent` before first paint. Native uses a splash screen but doesn't pre-apply the accent color. Add `ProTheme.applyCached()` to `ProApp.onCreate()` before `setContentView`.
2. **Shim → engine dependency** — the shim assumes `P4.Theme` exists. If `01-core.js` fails to load (CDN issue), the shim is a silent no-op. Add a `console.warn` so the failure is visible in dev.
3. **Theme picker pro-gating** — the picker marks 12 of 16 accents as Pro-locked. Native `ProTheme.ACCENTS` does not have a `locked` flag. Add `locked: Boolean` to the accent data class.
