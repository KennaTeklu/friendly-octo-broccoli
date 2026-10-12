# 03-device-momentum-zones — Device ID, sync, device fingerprinting, momentum bias, training zones

Source: `reference/scripts/app/03-device-momentum-zones.js` (L1994–3781 of original `app.js`)

## A. File purpose
This module handles three loosely related concerns: (1) device identification + rich device fingerprinting for backend sync; (2) backend sync pipeline (profile/workouts push with throttling); (3) momentum-bias helpers and the per-muscle fatigue/readiness zone system used by workout generation. It defines `getDeviceId`, a deep `getDeviceInfo` (User-Agent Client Hints → UAParser → iPad correction → WebGL GPU cleanup), `syncToBackend`, momentum-bias decay, and zone-based readiness queries.

## B. Public surface
- `getDeviceId()` — returns/creates UUID v4 in `localStorage['deviceId']`.
- `getDeviceInfo()` (async) — returns `{browser, os, device, hardware{cores, ram, gpu, screen, touchPoints}, context{net, battery, tz, lang}}`. Uses `navigator.userAgentData.getHighEntropyValues` first, UAParser fallback, iPad OS detection, WebGL `WEBGL_debug_renderer_info` cleanup.
- `syncToBackend()` — POSTs `{deviceId, deviceName, timestamp, name, dataType, data}` payloads (`profile`, `workout`, `exercise`, etc.) with 1-second throttle between workout uploads.
- `generateUUID()` — UUID v4 helper.
- Momentum helpers feeding `workoutData.user.momentumBias` (value, decayRate 0.25, lastUpdated, lastTap).
- Zone/region helpers: per-muscle readiness thresholds, decay rates — feeds `muscleFatigue` and `couplingMatrix` updates.

## C. Dependencies
- Globals: `localStorage`, `navigator`, `screen`, `Intl`, `document`, `window`, `UAParser` (3rd-party), WebGL.
- Cross-module: `workoutData`, `saveToLocalStorage`.

## D. Native-equivalent mapping
- `getDeviceId` → `core/ProState.kt::deviceId()` (L308) — backed by `ProPrefs` SharedPreferences; simpler UUID.
- `getDeviceInfo` rich fingerprinting → **GAP**. Native collects nothing about Android model/GPU; Android has `Build.MODEL`, `Build.MANUFACTURER`, `Build.VERSION.RELEASE`, `ActivityManager.MemoryInfo`, `GLES20.glGetString` — all unused today.
- `syncToBackend` (GAS backend) → **GAP**. Native has no remote sync; only local DataStore + optional JSON export via `core/Exporter.kt`. The `BackupSystem.kt` (L32) writes to local file, not a server.
- Momentum bias decay → `engine/Sessions.kt::applyMomentumTap` (L505) — implemented; `Generator.decayMomentumBias` (L613) also exists.
- Zone fatigue model → `engine/Library.kt::Fatigue` (L341) — `applyFatigueDecay`, `decayFatigue`, `isMuscleReady`, `getReadyMuscles`, `getCoupledFatigue` all ported.

## E. Patterns worth porting
- **Rich device fingerprint** (GPU model, RAM, CPU cores, network type, battery, timezone, language) collected once per sync. Native Android could trivially surface this in a diagnostics screen, useful for crash triage. Consider an opt-in "anonymous telemetry" payload.
- **1-second throttle between workout uploads** to avoid hammering the server — clean pattern; relevant if a sync endpoint is added.
- **iPad correction** (`/Macintosh/.test(ua) && maxTouchPoints > 1`) — clever edge case the native app doesn't face, but the WebGL GPU cleanup regex is portable to `Renderer` strings on Android.
- **Momentum bias as a tunable decay (`0.25/day`)** with `lastTap` remembered — preserves "I felt stronger today" steering; native has this but could expose it as a UI affordance.

## F. Feature IDs
`P03-DEVICE-ID`, `P03-DEVICE-FINGERPRINT`, `P03-BACKEND-SYNC`, `P03-MOMENTUM-BIAS`, `P03-ZONE-READINESS`.
