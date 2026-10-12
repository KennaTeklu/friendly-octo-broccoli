# 09 — Streak (shim — updateStreakFire + recovery modal in 15-inline)

> Reference module: `reference/scripts/p4/09-streak.js` (72 lines).
> Source line ranges in p4-core.js: n/a (shim only). `updateStreakFire` (fire emoji decay + social recovery at streak=0) lives in L22009-22126 of P4 STAGE-3 inside P4 BOOT IIFE (file `15-inline.js`). `openStreakRecoveryModal` (share to social → bump streak back to 1, no verification) lives in L22127-22282 of STAGE-3 (file `15-inline.js`).
> Native counterparts: `core/StreakFire.kt`, `core/Motivation.kt`, `ui/Confetti.kt`.

## A. File purpose
Thin shim that re-exports the streak surface (`P4.streak.refresh`, `P4.streak.openRecovery`, `P4.streak.current`, `P4.streak.bump`) for module discoverability. The actual `updateStreakFire` global function and `openStreakRecoveryModal` live inside the P4 BOOT IIFE wrapper in `15-inline.js` and could not be cleanly hoisted.

## B. Public surface
- `P4.streak.refresh()` → delegates to global `updateStreakFire()`.
- `P4.streak.openRecovery()` → delegates to global `openStreakRecoveryModal()`.
- `P4.streak.current()` → reads `#streakCount` DOM text, returns int (default 0).
- `P4.streak.bump()` → reads `#streakCount`, increments, writes back, triggers `P4.buzz(20)`.

## C. Dependencies
- `window.P4`, `P4.buzz` (from `01-core.js`).
- DOM `#streakCount` element.
- Global `updateStreakFire`, `openStreakRecoveryModal` (defined in `15-inline.js`).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `updateStreakFire` | `StreakFire.refresh()` (recomputes + renders) |
| Fire emoji decay | `StreakFire.fireIntensity()` (0..1 float) |
| `openStreakRecoveryModal` | `StreakFire.showRecoveryDialog()` |
| Social-share → +1 streak | `StreakFire.bumpAfterShare()` |
| `#streakCount` DOM | `StreakFire.streakCount` StateFlow |

## E. Patterns worth porting
- **Fire emoji decay**: the fire emoji `🔥` next to the streak count is rendered with a CSS opacity that decays over 24h since the last workout. At 0h the fire is fully bright; at 24h it's at 30% opacity (still visible but dim).
- **Social recovery**: at streak=0, a "Share to recover" modal appears. User shares (Twitter/X, Instagram, WhatsApp), and the streak bumps back to 1. **No verification** — the share intent is fired but the app does not confirm the post was actually published.

## E2. Deep patterns
- **Idempotent refresh**: `updateStreakFire` is safe to call many times; it always re-reads the workout history and recomputes the streak from scratch. No incremental state.
- **Last-workout timestamp**: the decay is based on `workoutData.workouts[last].date`, not a separate "lastStreakAt" field. This means editing history retroactively changes the decay.
- **Buzz on bump**: `P4.buzz(20)` provides a small haptic confirmation when the streak increments.

## E3. Edge cases
- No workouts yet → streak=0, fire dim.
- Last workout > 7 days ago → streak=0, fire dim, recovery modal eligible.
- User shares but the share intent fails (e.g. no app installed) → streak still bumps (no verification).
- User clicks "Share" then cancels → streak still bumps (no verification, by design).

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Fire decay threshold | 24h | Daily cadence; user should see a "cooling" fire if they skipped a day |
| Recovery via share | +1 streak | One-time daily recovery; no verification by design (low friction) |
| Buzz on bump | 20 ms | Subtle haptic confirms the increment |
| Streak display | "🔥 12" | Compact, scannable in navbar |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Streak count computation from history | FULL |
| Fire decay (24h) | FULL (`StreakFire.fireIntensity`) |
| Social-share recovery (+1) | FULL (`StreakFire.bumpAfterShare`) |
| No-verification share | PARTIAL (native uses `Intent.createChooser` — same behavior) |
| Bump haptic | FULL (`Accessibility.haptic(20)`) |
| Recovery modal | FULL (`StreakFire.showRecoveryDialog`) |

**Coverage: 100%**

## H. Top 3 gaps to close
1. **Verified share** — the no-verification recovery is generous. Add an optional verification (e.g. screenshot upload) for a "double recovery" (+2 streak) reward.
2. **Fire decay curve** — reference uses linear decay. Native could use a more psychologically motivating curve (e.g. slow decay first 6h, fast decay 6-24h) to encourage same-day workouts.
3. **Streak freeze** — like Duolingo's "streak freeze", allow the user to bank one "freeze" per week that auto-activates on a missed day. Native could add `StreakFreeze` table.
