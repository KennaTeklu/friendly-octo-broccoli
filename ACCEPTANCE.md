# ACCEPTANCE.md — the exact test the client runs on your delivery

You will be approved or rejected based on this list. Build to pass it.

## Before you ship

Run all five commands. If any is not green, do not ship.

    ./gradlew clean testReleaseUnitTest assembleRelease
    aapt2 dump badging app/build/outputs/apk/release/app-release.apk | head -1
    apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
    sha256sum app/build/outputs/apk/release/app-release.apk
    python3 verify_apk.py app/build/outputs/apk/release/app-release.apk

Copy the raw output of all five into README.md inside the zip. Not a summary — the raw text.

## What the client runs

    adb install -r pro.apk
    adb shell am force-stop com.peakform.fitness
    adb shell monkey -p com.peakform.fitness -c android.intent.category.LAUNCHER 1
    adb shell dumpsys package com.peakform.fitness | Select-String "versionCode|versionName"

Install must print Success. Version must be at least 11 / 1.4.1. App must launch.

## What the client checks in the app

1. Every screen listed in CHANGES.md with a green check is present in the app.
2. Every green check has a corresponding screenshot in docs/verify/.
3. No crashes during a 5-minute session of normal use.
4. Existing data survived the upgrade (workouts, profile, streaks).
5. Airplane mode: core features still work (Library, Workout, Progress, Settings).

## Red flags that will get the delivery rejected

- INSTALL_FAILED_VERSION_DOWNGRADE — versionCode too low
- INSTALL_FAILED_UPDATE_INCOMPATIBLE — wrong signing cert (data will be lost)
- App crashes on launch — build is broken
- Missing screenshots for claimed features
- APK hash matches a prior delivery
- CHANGES.md claims features not in the APK
- gradlew assembleRelease does not exit 0

## Two-branch merge requirement

The client's phone currently has the vCode 10 build installed (see baseline/pro.apk).
A separate vCode 9 build exists (divergent-vcode9/pro.apk plus its source) with features
the vCode 10 build does not have: animated exercise media, 1RM Lab, gold header, plate math,
favorites, haptics.

The vCode 10 build has features the vCode 9 build does not: onboarding wizard, health screen,
legal center, glossary, snapshots, profiles, command palette, import warning.

Merge both feature sets into one source tree. Do not ship one or the other — ship both, unified.
