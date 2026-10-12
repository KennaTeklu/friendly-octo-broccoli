# 01 — Core (P4 namespace, SEC, store, theme, toasts, misc utils)

> Reference module: `reference/scripts/p4/01-core.js` (1,737 lines).
> Source line ranges in p4-core.js: L32-34, L36-295, L297-1156, L13051-13638.
> Native counterparts: `core/ProStore.kt`, `core/ProState.kt`, `core/JsonHeal.kt`, `ui/ProTheme.kt`, `ui/Accessibility.kt`.

## A. File purpose
Boots the P4 layer: defines `window.P4` (idempotent), the `P4_EXERCISE_COMPONENTS` registry, the `p4AntiDebugV2` devtools-detector IIFE, the floating pill UI overlay, and the **P4 CORE** IIFE that owns the namespace, the `SEC` security kit, `P4.store` helpers, `P4.toast`, `P4.buzz`, `P4.ACCENTS`, `P4.Theme`, and the misc utils (`uid`, `todayKey`, `fmtDate`, `debounce`, `pick`, `download`, `copyText`).

## B. Public surface
- `window.P4` — root namespace (MAGIC, version, keys).
- `P4.SEC` — `escapeHtml`, `stripHtml`, `clampStr`, `healJson`, `validateExercisePayload`, `rateLimit`, `integrityOk`, `domainAllowed`.
- `P4.store` — `get(key, fallback)`, `set(key, val)`, `del(key)`, `snapshot()`.
- `P4.toast(msg, kind, ms)`, `P4.buzz(pattern)`.
- `P4.ACCENTS` — 16-entry identity color table.
- `P4.Theme` — `get/set/apply/accentName/accentHex/accents/gym/gymOn`.
- Utils: `P4.uid`, `P4.todayKey`, `P4.fmtDate`, `P4.debounce`, `P4.pick`, `P4.download`, `P4.copyText`, `P4._copyFallback`.

## C. Dependencies
- Dexie (loaded via CDN before this script).
- `workoutData` global (from `scripts/app/01-config-data.js`).
- `localStorage` (gracefully degrades on private mode).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `P4.SEC.healJson` | `core/JsonHeal.kt` |
| `P4.store` | `core/ProStore.kt` (Room-backed) |
| `P4.Theme` | `ui/ProTheme.kt` + `ThemeController.kt` |
| `P4.toast` | `ui/Dialogs.kt` (snackbar host) |
| `P4.buzz` | `ui/Accessibility.kt` (haptic controller) |
| `p4AntiDebugV2` | n/a (web-only concern) |

## E. Patterns worth porting
- **Idempotent namespace guard**: `if (window.P4) return;` prevents double-boot.
- **Rolling snapshots**: `store.snapshot()` keeps last 3 self-recovery points.
- **Token-bucket rate limiter**: `SEC.rateLimit(key, maxHits, windowMs)` for license/import floods.

## E2. Deep patterns
- **Integrity canary**: `P4_MAGIC = 0xDEADBEEF ^ 0x55AA55AA` is verified at boot by `SEC.integrityOk()`. If a bundler mangles the constant, boot aborts silently.
- **Domain gate**: `SEC.domainAllowed()` whitelists `try4ever.com`, `localhost`, `127.0.0.1`, `0.0.0.0`, `*.local`. Other hosts see a 404 page.

## E3. Edge cases
- `localStorage` throws in private mode → `store.set` catches and shows a "Storage full" toast.
- `JSON.parse` fails on tampered data → `store.get` returns `fallback`.
- `navigator.clipboard` unavailable → falls back to `document.execCommand('copy')` via hidden `<textarea>`.

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Toast duration | 3400 ms | Long enough to read, short enough to not annoy |
| Haptic default | 12 ms | Subtle tap, not a buzz |
| Toast fade-out | 340 ms | Matches iOS sheet dismiss |
| Store failure toast kind | `bad` | Red icon — user must act |

## G. Audit score
| Behavior | Coverage |
|---|---|
| P4 namespace + idempotent guard | FULL |
| SEC kit (escape/heal/validate/rateLimit) | FULL |
| Store helpers (get/set/del/snapshot) | FULL |
| Theme engine (16 accents + dark/light + gym) | FULL |
| Anti-debug v2 | MISSING (web-only) |
| Floating pill UI overlay | MISSING (Android uses nav rail) |

**Coverage: 80%** (5 FULL / 1 MISSING of 6)

## H. Top 3 gaps to close
1. **Floating pill UI overlay** — the floating status pill (`#p4FloatingPill`) has no native counterpart. Add a Jetpack Compose floating chip that mirrors the same data (streak + next workout).
2. **Anti-debug v2** — the devtools detector (timer-based stack-trace heuristic) has no Android equivalent. Skip or implement via `BuildConfig.DEBUG` gating.
3. **Integrity canary** — `P4_MAGIC` is checked but never logged. Surface integrity failures to a remote crash reporter so we can detect repackaged builds.
