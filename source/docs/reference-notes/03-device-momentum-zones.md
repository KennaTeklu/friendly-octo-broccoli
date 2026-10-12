# 03 — Device ID, Device Info, Momentum, Zones

> Reference module: `reference/scripts/app/03-device-momentum-zones.js` (1 795 lines).
> Native counterparts: `core/Model.kt`, `core/ProState.kt`, `core/Backup.kt`, `engine/Generator.kt`, `engine/Library.kt`.

This module owns the UUID generation, the 8-step device fingerprint pipeline, the momentum bias decay, and the zone/threshold tables used by readiness checks.

---

## E2. Deep patterns

### 2.1 `getDeviceId()` — UUID v4 cached in localStorage
- **Inputs**: `localStorage['deviceId']`.
- **Computation**: if id present → return it; else `generateUUID()` (RFC 4122 v4 — `crypto.getRandomValues` for the 6 variant bits); store and return.
- **Output**: 36-char UUID string.
- **Reasoning**: persists across sessions; generated lazily so first-time users don't pay the cost until backend sync.

### 2.2 `getDeviceInfo()` — 8-step fingerprint pipeline
1. **Client Hints API** (Chromium only): `navigator.userAgentData.getHighEntropyValues(['platform','platformVersion','model','fullVersionList'])`. Windows version major≥13 → Windows 11, else Windows 10.
2. **UA-parser fallback** (Safari/Firefox): `new UAParser().getResult()` populates the same fields.
3. **iPad correction**: `/Macintosh/.test(ua) || platform==='MacIntel'` AND `maxTouchPoints>1` → mark as `iPadOS / iPad / tablet`. Then attempt iOS version regex `OS (\d+)_(\d+)`.
4. **Device-type refinement**: regex `/Mobi|Android|iPhone/` → mobile; `/Tablet|iPad|PlayBook/` OR (`touchPoints>1 && width>700`) → tablet; else desktop.
5. **GPU detection via WebGL**: create canvas, `getContext('webgl')`, pull `WEBGL_debug_renderer_info`, then clean the raw string — strip `ANGLE(...)`, `Direct3D.*`, `vs_\d+_\d+`, `ps_\d+_\d+`, collapse spaces; truncate at 60 chars.
6. **Network info** (`navigator.connection`): `effectiveType` + ` (Eco)` if `saveData`.
7. **Battery info** (`navigator.getBattery`): `${level*100}%` plus `⚡` if charging.
8. **Build final string**: concat OS, browser, model, hardware (cores/ram/gpu/screen/touchPoints), context (net/battery/tz/lang).
- **Output**: human-readable fingerprint used by the backend for anonymous analytics.
- **Reasoning**: 8-step cascade covers every major browser family; each step is wrapped in try/catch.

### 2.3 `generateUUID()` (RFC 4122 v4)
- **Inputs**: `crypto.getRandomValues` on a 16-byte Uint8Array.
- **Computation**: zero bits 6 and 7 of byte 6 to `0b01000000`; zero bits 8 and 9 of byte 8 to `0b10000000`; format as `xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx`.
- **Output**: UUID v4 string.
- **Reasoning**: standard variant + version bits so server-side validation passes.

### 2.4 Momentum bias decay (`decayMomentumBias`)
- **Inputs**: `workoutData.user.momentumBias` (`value`, `decayRate`, `lastUpdated`, `lastTap`).
- **Computation**: `daysSince = (now - lastUpdated) / 86400000`; `value *= pow(1 - decayRate, daysSince)`; clamp to `[-1, 1]`; update `lastUpdated = now`.
- **Output**: decays the steering bias toward 0 over time.
- **Reasoning**: a "bigger" tap from 3 weeks ago should not still steer today's prescription; exponential decay with `decayRate=0.25` halves the bias every ~2.5 sessions.

### 2.5 Zone / threshold tables
- **Inputs**: hard-coded zones file.
- **Computation**: defines `READINESS_THRESHOLD`, `VOLUME_DELOAD_THRESHOLD = 1.5`, `SYSTEMIC_DELOAD_THRESHOLD = 0.6`, etc.
- **Output**: constants consumed by `getReadyMuscles`, `getOverdueMuscles`, `performGenerateWorkout`.
- **Reasoning**: single source of truth — every module reads the same thresholds so behaviour is consistent.

### 2.6 `getReadyMuscles(candidates)` filter
- **Inputs**: array of muscle names, `muscleFatigue`, `READINESS_THRESHOLD`.
- **Computation**: `candidates.filter(m => getCoupledFatigue(m) < READINESS_THRESHOLD)`.
- **Output**: ready subset.
- **Reasoning**: enforces coupling-aware fatigue gating before prescription.

### 2.7 `getOverdueMuscles(thresholdDays, minCount)`
- **Inputs**: `muscleLastTrained` map, threshold (e.g. 1.5× restDays), minCount (e.g. 2).
- **Computation**: iterate muscles; if `daysSince > thresholdDays * restDays` AND muscle hasn't been trained ≥minCount times this week → mark overdue.
- **Output**: list of muscles that must be prioritised even if not "ready".
- **Reasoning**: catches "I haven't trained neck in 30 days" — would be filtered out by readiness but should be force-included.

### 2.8 `applyFatigueDecay()` (boot + workout gen)
- **Inputs**: `muscleFatigue`, MUSCLE_DECAY, `lastFatigueDecay` timestamp.
- **Computation**: `daysSince = (now - lastFatigueDecay) / 86400000`; for each muscle, `fast *= (1 - decay.fast)^daysSince`, `slow *= (1 - decay.slow)^daysSince`; update `lastFatigueDecay = now`.
- **Output**: time-aware decayed fatigue per muscle.
- **Reasoning**: bi-exponential — `fast` (neuromuscular) recovers in hours/days; `slow` (muscle damage) recovers over weeks.

---

## E3. Edge cases handled

- **`localStorage['deviceId']` present**: returns immediately, no UUID generation.
- **Client Hints API not available** (Safari): falls through to UA-parser.
- **UA-parser not loaded**: leaves OS/browser empty; pipeline continues.
- **iPad in desktop mode**: detects via Macintosh UA + touchPoints; sets type to `tablet` even though UA says desktop.
- **iOS version hidden**: regex returns null → leaves `os.version` empty; UI shows just "iPadOS".
- **WebGL context unavailable**: try/catch swallows; `gpu='Software'` default.
- **GPU string longer than 60 chars**: truncated with `…` ellipsis.
- **`navigator.connection` missing**: `context.net` stays empty string.
- **`navigator.getBattery` missing**: `context.battery` stays empty string.
- **`navigator.hardwareConcurrency` missing**: `cores = null`.
- **`navigator.deviceMemory` missing**: `ram = null`.
- **Momentum bias `lastUpdated = null`**: skip decay (first time).
- **Momentum bias `value` already 0**: decay is no-op.
- **Muscle not in MUSCLE_DECAY**: uses `DEFAULT_DECAY`.
- **`lastFatigueDecay = null`** (first boot): sets to `now`, no decay applied.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Device ID storage | localStorage | Persists across sessions; not tied to install |
| iPad correction regex | `Macintosh` + `maxTouchPoints>1` | Apple reports iPadOS 13+ as MacIntel in desktop mode |
| GPU string truncate | 60 chars | Keeps backend column tidy; "…" signals truncation |
| Momentum decay rate | 0.25 per day | Halves bias every ~2.5 days — short enough to be responsive, long enough to survive a missed session |
| Bias clamp | [-1, 1] | Prevents runaway after many "bigger" taps |
| `READINESS_THRESHOLD` | 0.3 | Below 30% coupled fatigue → ready (matches ~70% recovery) |
| `VOLUME_DELOAD_THRESHOLD` | 1.5× baseline | Triggers deload at 150% of 4-week avg volume |
| `SYSTEMIC_DELOAD_THRESHOLD` | 0.6 | Recovery <60% → deload |
| Overdue threshold | 1.5× restDays | If you should have trained 2 days ago, force-include |
| Overdue minCount | 2 | Don't force-include if muscle was trained twice this week |
| Timezone display | `Intl.DateTimeFormat().resolvedOptions().timeZone.split('/').pop()` | "New_York" instead of "America/New_York" — cleaner UI |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `getDeviceId()` UUID v4 + localStorage cache | FULL (`Profiles.kt` + `ProStore.kt`) | — |
| `generateUUID()` RFC 4122 | FULL (`UUID.randomUUID()`) | — |
| Client Hints / UA-parser / iPad / GPU / net / battery pipeline | PARTIAL | Android-only — no iPad/GPU detection needed, but `Build.MODEL`, `Build.MANUFACTURER`, `Build.VERSION.RELEASE` are surfaced; battery/network only via `BatteryManager`/`ConnectivityManager` opt-in |
| `decayMomentumBias` exponential decay | FULL (`Generator.kt` + `Model.kt`) | — |
| `applyFatigueDecay` bi-exponential | FULL (`Stats.kt`) | — |
| Zone / threshold tables | FULL (`Library.kt` constants + `assets/data/zones.json`) | — |
| `getReadyMuscles` filter | FULL (`Generator.kt`) | — |
| `getOverdueMuscles(threshold, minCount)` | FULL | — |
| Battery info display | PARTIAL | Acquired only when permission granted; not shown in profile UI |
| Network info display | PARTIAL | `ConnectivityManager` hooked but effectiveType not surfaced in profile |

**Coverage: 70%** (7 FULL / 3 PARTIAL / 0 MISSING of 10 behaviours)

---

## H. Top 3 gaps to close

1. **Device fingerprint not surfaced in profile UI** — Android has the info (`Build.MODEL`, screen density, battery, network) but the profile screen shows none of it. Surface at least model + Android version + screen size in Settings → About.
2. **Battery/network info requires explicit permission flow** — wire `BatteryManager.isCharging` and `ConnectivityManager.networkCapabilities` into the profile panel with a one-tap permission dialog.
3. **Momentum bias decay rate is hard-coded at 0.25** — should adapt to user's training frequency (3×/week users tolerate faster decay than 1×/week users). Expose `decayRate` in settings or compute from `workoutDays.length`.
