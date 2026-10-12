# 10 — Messages / Motivation (daily quote, win-of-the-day, expert tips)

> Reference module: `reference/scripts/p4/10-messages.js` (624 lines).
> Source line range in p4-core.js: L15131-15738 (P4 MOTIVATION IIFE — daily quote, win-of-the-day, expert tips rotation, badges count event, Motivation.init).
> Native counterparts: `core/Motivation.kt`, `core/MessageBank.kt`, `core/Badges.kt`.

## A. File purpose
Owns the in-app coaching surface: **daily quote** (rotates one of 365 curated quotes per day), **win-of-the-day** callout (highlights one positive trend from the last 7 days), **expert tips rotation** (cycles through domain-specific tips every 30s), and the `P4.Badges.countEvent()` hook used by other modules to record achievement progress.

## B. Public surface
- `P4.Motivation.init()` — picks the day's quote, computes win-of-the-day, starts tip rotation timer.
- `P4.Motivation.renderInto(hostId)` — renders the quote + win + tips block into a host element.
- `P4.Motivation.rotateTip()` — manually advances to the next tip.
- `P4.Badges.countEvent(event, count)` — increments a badge-progress counter.
- `P4.Badges.countEventOnce(event)` — idempotent variant (only counts once per session).

## C. Dependencies
- `window.P4`, `P4.store`, `P4.SEC` (from `01-core.js`).
- `P4.toast`, `P4.buzz`, `P4.pick`, `P4.fmtDate`.
- `P4.keys.quoteDay`, `P4.keys.badges`, `P4.keys.badgesSeen`.
- `workoutData.workouts` (for win-of-the-day computation).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| Daily quote rotation | `Motivation.dailyQuote()` (seeded by day-of-year) |
| Win-of-the-day | `Motivation.winOfDay()` (last-7-day trend analysis) |
| Expert tips rotation | `MessageBank.tips` (rotates on a 30s timer) |
| `P4.Badges.countEvent` | `Badges.countEvent(event)` (Room-backed) |
| `P4.Badges.countEventOnce` | `Badges.countEventOnce(event)` |

## E. Patterns worth porting
- **Day-seeded quote pick**: `Math.floor(Date.now() / 86400000) % quotes.length` ensures every user sees the same quote on the same day — creates a sense of community.
- **Win-of-the-day trend analysis**: scans the last 7 days for any positive trend (volume up, RPE down, new PR, streak milestone) and surfaces the most notable one.
- **Tip rotation timer**: a `setInterval` cycles tips every 30s, pausing on hover/tap. The user can also tap to advance manually.

## E2. Deep patterns
- **Idempotent badge counting**: `countEventOnce` uses a session-scoped `Set` to dedupe. The set is not persisted — once the user closes the tab, the same event can be counted again next session. This is intentional for daily-streak-style events.
- **Quote-of-the-day cache**: the picked quote is cached in `p4_quote_day` localStorage key with the date stamp so re-renders during the day don't re-pick.
- **Tip domain tagging**: each tip is tagged with a domain (`form`, `recovery`, `nutrition`, `mindset`) so the rotation can be domain-weighted (e.g. show more `recovery` tips the day after a heavy workout).

## E3. Edge cases
- Empty workout history → win-of-the-day falls back to "Welcome — log your first workout!"
- All quotes exhausted (rare, 365 quotes) → wraps around.
- User dismisses tip rotation → timer pauses until next user action.
- Badge count exceeds threshold → badge unlocked, `P4.Badges.unlock(badgeId)` fires confetti + toast.

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Quote rotation cadence | daily | Same for all users → community feel |
| Tip rotation cadence | 30s | Long enough to read, short enough to feel alive |
| Win-of-the-day window | last 7 days | Captures weekly trends without over-weighting recent |
| Badge unlock celebration | confetti + toast | Memorable moment |
| Tip pause on hover | true | Don't change while user is reading |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Daily quote rotation (day-seeded) | FULL |
| Win-of-the-day trend analysis | FULL |
| Expert tips rotation (30s) | FULL |
| `P4.Badges.countEvent` | FULL |
| `P4.Badges.countEventOnce` | FULL |
| Confetti on badge unlock | FULL (`ui/Confetti.kt`) |
| Tip domain weighting | MISSING (native rotates randomly) |

**Coverage: 90%**

## H. Top 3 gaps to close
1. **Tip domain weighting** — reference has tags but rotates randomly. Native `MessageBank.tips` should weight by domain based on the user's recent activity (more recovery tips after a heavy session).
2. **Win-of-the-day multilingual** — the win message is hard-coded English. Native `MessageBank` should localize via `Vocab.kt` ladder levels.
3. **Badge unlock share** — when a badge unlocks, the user should be able to share it (image + text). Native has `ShareSheet.kt` but it's not wired to badge unlocks.
