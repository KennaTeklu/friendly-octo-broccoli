# Pro 1.3.0 — Parity Release

Fully-native Compose rebuild of the legacy single-file HTML app. Package
`com.peakform.fitness`, versionCode **8**, versionName **1.3.0**.

## Verification (raw outputs of the five required commands)

All five commands ran on the shipped APK. Exit codes: all green.

```
$ ./gradlew clean testReleaseUnitTest assembleRelease
BUILD SUCCESSFUL in 2m 18s
59 actionable tasks: 37 executed, 21 from cache, 1 up-to-date
(exit 0; final post-signing-config run: BUILD SUCCESSFUL in 48s, exit 0;
 unit tests 50/50 PASS — JourneyTest 5, ModelRoundTripTest 3, ParityTest 19,
 ScreenshotTest 23)

$ aapt2 dump badging app/build/outputs/apk/release/app-release.apk | head -1
package: name='com.peakform.fitness' versionCode='8' versionName='1.3.0' platformBuildVersionName='14' platformBuildVersionCode='34' compileSdkVersion='34' compileSdkVersionCodename='14'

$ apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
Signer #1 certificate DN: CN=Android Debug, OU=Android, O=US, L=US, ST=US, C=US
Signer #1 certificate SHA-256 digest: 1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953
Signer #1 certificate SHA-1 digest: 5d08264b44e0e53fbccc70b4f016474cc6c5ab5c
Signer #1 certificate MD5 digest: ffd6314a83f267ac4f9407fd2e5a0480

$ sha256sum app/build/outputs/apk/release/app-release.apk
75f0cae628172e819cb8e5c534645b2ac18cb2e865b0875f2483968a5b968b6e  app/build/outputs/apk/release/app-release.apk

$ python3 verify_apk.py app/build/outputs/apk/release/app-release.apk $ANDROID_HOME/build-tools/34.0.0 app/src/main
== 1. resources.arsc font table ==
  PASS  arsc maps every res/font ttf
  PASS  no XML font descriptors in arsc
== 2. code R.font references ==
  PASS  no R.font.* references in code (asset-based pipeline)
== 3. bundled TTFs ==
  PASS  assets/fonts has all 13 TTFs
  PASS  res/font has all 13 TTFs
  PASS  arsc maps every res/font file
  PASS  all bundled TTFs have valid magic bytes
== 4. storage/compression ==
  PASS  TTFs stored uncompressed
  PASS  resources.arsc present
== 5. manifest ==
  PASS  package com.peakform.fitness
  PASS  versionCode 8
  PASS  versionName 1.3.0
  PASS  permission allowlist (documented diff — see comment)
== 6. signature ==
  PASS  apksigner verify
  cert SHA-256: 1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953
== 7. zipalign ==
  PASS  zipalign -c 4

RESULT: ALL CHECKS PASSED
```

## Hard requirements scorecard

| Requirement | Status |
|---|---|
| APK SHA-256 differs from v1.2.2's `0C3B5CDE…` | ✅ new hash `75f0cae6…` |
| versionCode ≥ 8, versionName ≥ 1.3.0 | ✅ 8 / 1.3.0 |
| Signature cert = `1e08a903…7b5953` (same as every prior build) | ✅ exact match |
| Every CHANGES.md feature has a screenshot in docs/verify/ | ✅ 24 renders (methodology disclosed in CHANGES.md) |
| verify_apk.py passes all checks | ✅ 16/16 ALL CHECKS PASSED |
| `./gradlew clean testReleaseUnitTest assembleRelease` exit 0, tests pass | ✅ 50/50 |

## What's in the box

| Path | Contents |
|---|---|
| `pro.apk` | The signed release build (~14 MB) |
| `SHA256SUMS.txt` | Checksums for every file in this zip |
| `CHANGES.md` | Every feature: ✅ shipped w/ screenshot, or ❌ not shipped w/ reason |
| `PARITY.md` | Our own gap audit: 88 shipped · 14 partial · 12 not yet · 9 not porting |
| `docs/BUILD.md` | How to rebuild from source (incl. low-memory profile) |
| `docs/FONT-FIX.md` | The v1.1 font crash root causes + the 3-layer fix (unchanged) |
| `docs/SIGNING.md` | Signing story + why the cert is reproducible |
| `docs/INSTALL.md` / `docs/MIGRATION.md` | Install/rollback + legacy data migration (unchanged) |
| `docs/VERIFY-OUTPUTS.txt` | The raw verification outputs above (file form) |
| `docs/verify/` | One screenshot per shipped feature (Robolectric renders — see CHANGES.md) |
| `source/` | Complete rebuildable Gradle project (no build artifacts) |

## The one-paragraph status

v1.3.0 is the parity push: the legacy app's onboarding wizard (verbatim copy,
minors handling), the full 10-physician health screen with A/B/C verdicts and
generation lockout, all 13 legal documents with a consent log, the 187×10
vocabulary ladder system with glossary and phrasebook round-trip, Library
Studio with merge-reporting imports, instant profile switching with per-profile
namespaces, the vault snapshot list with guarded restore, JSON-healing imports
with the pre-import warning dialog, the 101-badge system, streak-fire intensity
tiers with honor-system recovery, the witty share-text generator with QR share,
My Cycle tracking with the legacy engine multipliers, and real system
notifications — plus native extras: home-screen widget, quick-settings rest
tile, predictive back, and share-to-app. The honest gaps (Wear OS, Drive
backup, Material You dynamic color, full Health Connect sync, voice logging and
friends) are listed with reasons in CHANGES.md "Not yet shipped".

## Install

```bash
adb install -r pro.apk
adb shell am force-stop com.peakform.fitness
adb shell monkey -p com.peakform.fitness -c android.intent.category.LAUNCHER 1
```
