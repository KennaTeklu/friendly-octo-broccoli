# 08 — Notifications (P4 Fix #4_5 — suppression + 1RM retest reminder kill)

> Reference module: `reference/scripts/p4/08-notif.js` (137 lines).
> Source line range in p4-core.js: L29400-29513 (P4 Fix #4_5 IIFE — notification suppression + kill 1RM retest reminder).
> Native counterparts: `core/Notify.kt`, `core/Nudges.kt`.

## A. File purpose
Owns the **P4 Fix #4_5** IIFE — a defensive patch that wraps `Notification.permission` and `Notification.requestPermission` to gracefully handle the "default" state (browsers return "default" when the user has not yet decided), and suppresses the automated 1RM retest reminder toast that fires too often.

## B. Public surface
- `_p4_notif_wrap()` — wraps `Notification.requestPermission` to return a Promise that resolves to `"default" | "granted" | "denied"`.
- `_p4_notif_suppress_1rm_retest()` — kills the `setInterval` that fires the "Time to retest your 1RM!" toast.
- `_p4_notif_init()` — runs both patches at boot.

## C. Dependencies
- `window.Notification` (browser API).
- `P4.toast` (from `01-core.js`) — fallback when native notifications are denied.
- `setInterval` registry (to find and kill the 1RM retest timer).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `Notification.requestPermission` | `Notify.requestPermission()` (Android runtime perm) |
| `"default"` permission state | `shouldShowRationale` (Android 3-state) |
| 1RM retest reminder toast | `Nudges.maybeNudge1RmRetest()` (schedules via WorkManager) |
| Suppression of over-frequent reminders | `Nudges.rateLimit(key, maxPerWeek)` |

## E. Patterns worth porting
- **Promise-based permission request**: the wrap converts the callback-style `Notification.requestPermission(cb)` into a Promise, so callers can `await` it.
- **Rate-limit the retest reminder**: instead of unconditionally suppressing, the patch should rate-limit (e.g. max once per 7 days). Native `Nudges.rateLimit` already does this.

## E2. Deep patterns
- **Interval ID capture**: the patch walks the `setInterval` registry (via a wrapped `window.setInterval`) to find the 1RM retest timer ID, then `clearInterval`s it. This is brittle — the original timer is identified by its callback source string, not a stable ID.
- **Permission state caching**: `Notification.permission` is read once at boot and cached; subsequent reads use the cache to avoid the synchronous IPC cost.

## E3. Edge cases
- `Notification` API missing (legacy browser) → wrap is a no-op, fallback toasts are used.
- Permission denied by user → fallback toasts via `P4.toast`.
- 1RM retest timer already fired before the patch loads → no-op, no double-kill.
- `setInterval` registry empty (no timers scheduled) → walk is a no-op.

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Suppression vs rate-limit | suppression (current) | Simpler; user complained about frequency |
| Fallback to in-app toast | true | User still gets the reminder, just less intrusive |
| Permission state cache | true | Avoids IPC cost on every read |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Permission request Promise wrap | n/a (Android uses runtime perm) |
| Permission state caching | FULL (`Notify.cachedPermission`) |
| 1RM retest reminder kill | PARTIAL (native rate-limits instead) |
| Fallback to in-app toast | FULL (`Notify.fallback`) |

**Coverage: 75%**

## H. Top 3 gaps to close
1. **Rate-limit instead of suppress** — the reference kills the 1RM retest reminder entirely. Switch to `Nudges.rateLimit('1rm_retest', 1, 7 days)` so the user still gets reminded weekly.
2. **Per-channel notification controls** — Android supports notification channels. Native should expose per-channel toggles in Settings (workout reminders, daily quote, badge unlock, 1RM retest) instead of a single master switch.
3. **Permission denial recovery** — when the user denies notification permission, the fallback toast appears but no path is offered to re-request. Add a "Open system settings" deep link from the Settings screen.
