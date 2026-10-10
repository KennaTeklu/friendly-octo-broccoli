# Pro — native Android fitness tracker

Native Kotlin / Jetpack Compose rebuild of the legacy single-file HTML app.

## What is in this repo

| Path | What it is |
|---|---|
| source/ | Current working source tree — the vCode 10 baseline (v1.4.0). Start here. |
| baseline/pro.apk | The APK installed on the client's phone right now. versionCode 10. |
| divergent-vcode9/ | A separate branch with features the vCode 10 build lacks. Its source must be merged into source/ — do not ship one or the other. See PRIORITIES.md. |
| LANDMINES.md | Traps that have burned previous builds. Read before touching anything. |
| ACCEPTANCE.md | The exact test the client runs on your delivery. Build to pass it. |
| PRIORITIES.md | What to build, in what order. |

## The other repo — the spec

The legacy HTML app lives at https://github.com/example-user/cautious-enigma. This is the spec. Every screen, modal, button, animation, and piece of JS logic in that file must eventually exist in this native app. Clone its behavior and its look — do not clone its WebView, its anti-debug code, or its monetization (see LANDMINES.md).

## Read order

1. LANDMINES.md — so you do not repeat past failures
2. PRIORITIES.md — so you build the right things in the right order
3. ACCEPTANCE.md — so you know exactly how your delivery will be judged
4. source/ — the current code
5. SPEC.md — the feature-by-feature clone list (what 'clone the HTML' actually means)
6. divergent-vcode9/source/ — the features that need merging in
7. cautious-enigma/index.html — the source of truth behind SPEC.md

## The task in one sentence

Merge the two branches, clone the HTML app's full feature set into the native build, ship one APK with versionCode at least 11 and versionName at least 1.4.1, signed with the same cert as every prior build, verified with the five commands in ACCEPTANCE.md, and packaged as pro.zip with a screenshot per shipped feature.

## If you are unsure

Ask before you start. A clear question up front saves a rebuild. Do not guess about scope, priority, the signing cert, or whether a feature belongs.

---

## License

Proprietary. Personal, non-commercial use only. See [`LICENSE`](./LICENSE)
for the full terms. Commercial use is prohibited without prior written
permission.