# Pro 1.1 — Fully Native — Install & Verify

## What this is

`pro.apk` is a **100% native Android app** (Kotlin + Jetpack Compose + Room). There is **no
WebView in the UI anywhere**. The old 3.6 MB single-file HTML app has been translated
code-to-code into native Kotlin: the workout generation engine, the Kalman 1RM estimator,
the muscle-fatigue model, the longevity scoring, the settings, the profiles, the import/export —
all of it, formula for formula, string for string.

- Package: `com.peakform.fitness` (the **same identity as your current app**)
- Version: `1.1` (versionCode 3 — your current install is versionCode 2)
- Signing cert: **SHA-256 `1e08a903…7b5953` — verified identical to the build on your phone**
- Permissions: **VIBRATE only. No INTERNET.** The app cannot phone home, and it never needs to.

## Install (no data loss)

```bash
adb install -r pro.apk
```

Because the package name and the signing certificate both match your current install,
`adb install -r` upgrades **in place**. Your app data directory is preserved, which is exactly
what makes the automatic migration possible.

> If adb says "INSTALL_FAILED_VERSION_DOWNGRADE" you are somehow installing over a newer
> build — that should not happen (3 > 2). If it says signature mismatch, you have a different
> build on the phone than the one this was cut from — stop and read MIGRATION.md (manual path).

## First launch — what you will see

1. **"Loading your training data…"** — Room hydrates.
2. **"Checking for data from the old app…"** — the one-shot migration shuttle runs (see
   MIGRATION.md). It reads the old app's Dexie IndexedDB + localStorage, merges everything
   into the native database, and then never touches a WebView again.
3. **"Legacy data imported ✓ — N workouts · M exercise records"** (or "No legacy data found"
   on a fresh setup).
4. You land on the Dashboard: readiness gauge, weekly rings, stat cards, your workout.

If anything looks wrong at step 3, **do not panic — nothing has been deleted.** The old data
directory is still intact; the manual fallback in MIGRATION.md can re-import at any time,
and `legacy-tools/pro-legacy-fixed.apk` reinstalls the old app (install -r) if you want to
export a JSON backup the long way.

## Verify the basics

| Check | How |
|---|---|
| Console logging (adb-only by design — there is **no debug UI**) | `adb logcat -s ProConsole:* ProNative:*` |
| Crash ring on device | `adb shell run-as com.peakform.fitness cat files/logs/pro.log` |
| Package/version | `adb shell dumpsys package com.peakform.fitness \| grep version` |
| Cert | `apksigner verify --print-certs pro.apk` |

## Rollback (if you ever want the old app back)

```bash
adb install -r legacy-tools/pro-legacy-fixed.apk
```

Same cert, lower versionCode (2 < 3) — if adb refuses the downgrade add `-d`.
Your native data and the legacy WebView data live in separate subsystems inside the same
private directory, so both directions of upgrade/downgrade keep what belongs to them.
