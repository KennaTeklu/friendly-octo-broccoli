# 17 - Batch pipeline

## How work ships

Symptom -> batch file -> prompt -> developer -> zip
-> verify -> install -> test -> accept -> push.

One direction. Same shape every batch. No batch
enters a step out of order.

## Roles

**Middle layer** — writes batch files, prompts,
audits. Reads zips. Never pushes, never installs,
never touches the phone.

**Desk** — runs commands, installs, tests on the
phone, pushes to GitHub.

**Developer** — builds from the prompt, returns the
zip.

**Sandbox** — z.ai or equivalent. No GitHub
credentials. Reads the repo. Writes locally. Returns
a zip. The desk pushes.

## The delivery contract

Every zip contains exactly seven entries:

    app.apk
    source/
    CHANGES.md
    README.md
    SHA256SUMS.txt
    docs/BUILD.md
    docs/drift-report.txt

The last two are the docs/ entry. Nothing else. No
docs/verify/ stub inside source/.

## Verification

Five commands, output pasted raw into README.md:

    ./gradlew clean testReleaseUnitTest assembleRelease
    aapt2 dump badging <apk> | head -1
    apksigner verify --print-certs <apk>
    sha256sum <apk>
    python3 verify_apk.py <apk>

Cert 1e08a903...7b5953. APK hash differs from prior.
versionCode increments by exactly 1.

## GitHub is mandatory

The repo is the source of truth. Not a backup.

Every batch:

- Work in a git checkout that can push
- Commit as you go. One commit per logical change
- Push to origin/main before returning the zip
- The repo must build from a fresh clone
- If you cannot push, say so and stop

The desk pushes. The sandbox does not.

## Install

adb install -r only. Never uninstall.

## Post-install

- Confirm versionCode/versionName match the batch
- Launch, check for FATAL EXCEPTION
- Fuzz with monkey when the batch changes UI
- Manual walkthrough for behavioral changes

## Acceptance

A batch is accepted when:

- The zip contains the seven entries
- All five verification commands pass
- The version and cert match
- The install succeeds
- The targeted behavior on the phone matches the prompt
- The commit is pushed and a fresh clone builds

## What never happens

- No batch skips a step
- No prompt is sent before the prior batch is accepted
- No acceptance without a push
- No autoupdate of the truth layer without human review

## The letters model

Deep letters, then consolidation.

    35a — coach breadth
    35b — more coaches
    35c — blocker fixes
    35d — taxonomy
    35e — difficulty gating
    ── consolidate → v35 ──

Five letters. Each one deep. Then one merge. Then the
next version starts.

No single-letter versions. No half-batches. The
alphabet is a symptom, not the plan.

## The prompt rule

Prompts are lean and imperative. Point at the repo.
Name the files. List the work. Do not explain context.
Do not orient the reader. Verbose prompts trigger
conversational replies from the model instead of
action.

Successful prompt shape:

    Repo: <url>
    Commit: <hash>
    Package: <package>

    Read the repo at that commit.
    Read <specific files>.

    BATCH NN — <title>

    <imperative list of work>

    MAY NOT
    <forbidden actions>

    DELIVERY
    <zip shape, verification, version>

    REPLY
    Return the zip. No prose.

Nothing more.
