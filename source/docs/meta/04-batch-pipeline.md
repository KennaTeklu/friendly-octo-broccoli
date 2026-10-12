# The batch pipeline

## Shape

Symptom -> batch file -> prompt -> developer -> zip -> verify -> install -> test -> accept -> push.

One direction. Same shape every batch. No batch enters a step out of order.

## Roles

- Middle layer: writes batch files, prompts, audits. Reads zips. Never pushes, never installs, never touches the phone.
- Desk: runs commands, installs, tests on the phone, pushes to GitHub.
- Developer: builds from the prompt, returns the zip.

## Delivery contract

Every zip contains exactly six entries:

- app.apk (named app.apk, not pro.apk)
- source/
- CHANGES.md
- README.md
- SHA256SUMS.txt
- docs/BUILD.md

Nothing else. No docs/verify/ stub. No empty directories.

Missing any of the six: the delivery is refused.

## Verification

Five commands, output pasted raw into README.md:

- ./gradlew clean testReleaseUnitTest assembleRelease
- aapt2 dump badging <apk> | head -1
- apksigner verify --print-certs <apk>
- sha256sum <apk>
- python3 verify_apk.py <apk>

Cert 1e08a903...7b5953. APK hash differs from prior. versionCode increments by exactly 1.

## GitHub is mandatory

The repo is the source of truth. It is not a backup.

Every batch:
- Work in a git checkout that can push.
- Commit as you go. One commit per logical change.
- Push to origin/main before returning the zip.
- The repo must build from a fresh clone.
- If you cannot push, say so and stop.

## Install

adb install -r only. Never uninstall.

## Post-install

- Confirm versionCode/versionName match the batch.
- Launch, check for FATAL EXCEPTION.
- Fuzz with monkey when the batch changes UI.
- Manual walkthrough for behavioral changes.

## Acceptance

A batch is accepted when:
- The zip contains the six entries.
- All five verification commands pass.
- The version and cert match.
- The install succeeds.
- The targeted behavior on the phone matches the prompt.
- The commit is pushed and a fresh clone builds.

## What never happens

- No batch skips a step.
- No prompt is sent before the prior batch is accepted.
- No acceptance without a push.
- No autoupdate of the truth layer without human review.
