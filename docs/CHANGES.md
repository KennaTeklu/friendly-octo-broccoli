# CHANGES.md — 1.1 → 1.2 (Athlete-Audit Release) + 1.2.1 (Re-Audit)

---

## 1.2.1 — the re-audit: every claimed fix verified, the last gaps closed

Method: the v1.2 audit had surfaced 67 findings and claimed them all fixed.
This pass re-verified **each one against the source tree** (P0/P1 line-by-line,
rebuild from clean source), re-ran the 16-check APK battery on a fresh build,
and hunted for regressions the fixes themselves could have introduced.
Result: the P0/P1 fixes all held, but the re-audit caught 6 residual issues:

* **Source/apk drift (the big one):** the shipped v1.2 APK had the 3 XML
  font-family descriptors deleted from `res/font/` — but that deletion never
  made it back into the source package, so a clean rebuild of the shipped
  source FAILED its own font-table audit (arsc mapped 16 font resources, not
  the 13 real TTFs). The XMLs are now gone from the source; `native-source/`
  reproduces the shipped artifact bit-for-bit at the audit level and the
  battery passes 16/16 **from source**.
* **More-sheet "Longevity" was a 5th blank destination:** `go("long")` had no
  mapping and no screen branch — the Crossfade rendered an empty screen. (The
  original audit listed themes/glossary/studio/period but missed "long".)
  Now routes to Recovery, where the longevity generator lives.
* **Audit #35 (backup nudge) was claimed but absent:** the dashboard never
  read `p4_last_backup`. Restored as pure, unit-tested logic (`Nudges`):
  nudge when ≥3 workouts and no backup (or backup older than 14 days), tap
  opens Settings → Data & backup.
* **Audit #38 (history notes) was claimed but absent:** `onNoteSaved` was a
  dead parameter with no notes UI. History detail now shows each exercise's
  notes and offers Add/Edit with a save that round-trips to the store and the
  CSV export.
* **The three notification toggles still had no readers** (audit #57 was
  only half-honest: swipe got a "coming soon" suffix, the others didn't).
  Real wiring: "1RM retest reminders" now drives a dashboard banner when a
  calibrated lift's test/last-update is older than 8 weeks (via `Nudges`,
  unit-tested); "deload notices" now drive the workout screen's Deload chip;
  "period notices" is honestly labeled "My Cycle — coming soon".
* **Backup metadata said 1.1:** `appVersion`/`userAgent` in every Complete-v1
  export claimed `4.0.0-native-1.1` / `Pro/1.1`. Now `1.2.1`.

Also fixed in passing: the packaging script grabbed the **unsigned** APK;
it now packages the signed, zipaligned one. SIGNING.md's claim that a
`peakform.jks` ships with the source was false — the doc now explains the
deterministic uber-apk-signer embedded keystore instead.

Verified: **8/8** JVM tests (the 7-test suite + a new nudge-threshold suite),
16/16 APK battery on the fresh v1.2.1 build, same phone cert (`1e08a903…`),
`adb install -r` upgrade path intact.

---

## 1.2 — the athlete audit (67 findings, fixed)

Method: a simulated multi-week athlete journey drove the real engine on the JVM
(12 sessions generated, logged with per-set reps/RPE/failures, completed, plus
backdated 4-week history, backup→wipe→restore, hostile imports, unicode/NaN
edges) while every screen was audited against the legacy design specs. 60+
findings; every one fixed below.

## P0 — crash & data-integrity
* **Launch crash** `Resources$NotFoundException: Font resource ID #0x7f05000f` — TWO root causes fixed:
  - fonts rendered by resource ID (`ResourceFont(resId, Blocking)`) against an
    arsc rewritten by the aapt2 "optimize" pass → font pipeline now loads
    `assets/fonts` **by path** (`FontVault`), zero `R.font` references, XML
    family descriptors deleted, resource optimizations/shrinking disabled,
    TTFs stored uncompressed; launch-time `FontAudit` log with per-font fallback.
  - Inter/JetBrains Mono/Space Grotesk shipped as **WOFF2 renamed .ttf**
    (Android cannot parse WOFF2) → converted to real TTFs (magic-byte verified).
* **1RM progression engine was dead**: `OneRm.updateRecursive` silently no-oped
  on an exercise's first-ever log — and the roulette picker avoids repeating
  exercises, so `mu` could stay null forever. Now seeds the record instead.
* **Empty reps poisoned 1RM**: logging with an empty reps field saved
  sets × 0.0 reps. Save is now blocked with a clear inline error (and the
  failure path requires the attempted weight).
* **Import could blank your profile**: backup import wholesale-replaced the
  user object. Now merged field-wise (blank incoming fields never wipe local
  data); merge is a pure, unit-tested function.

## P1 — wrong behavior a real user hits
* More-sheet **Themes / Words / Studio / My Cycle** rendered blank screens → land on Settings.
* **Longevity Workout** was a dead end (flag written, never read) — now generates and navigates.
* Library **"Add to Workout" assigned seconds as lbs** (plank @ 480 lbs) → uses the engine's next recommended weight; duration stays duration.
* Tapping anywhere on an exercise card (labels, padding) collapsed the open logger and **destroyed all typed input** → only the header/prescription toggle now.
* 1RM panel **"Skip this exercise"** did nothing (panel looped forever) → actually skips with confirm.
* **Rest timer died on tab switch** (screen-local state) → app-scope singleton, survives navigation.
* **"Export CSV" was fake** (toast only) → real CSV built from all workouts + system share sheet.
* **Every recovery/mastery bar rendered 100% full** (0–100 fed into a 0–1 fraction) → ProgressBar rescaled.
* No **window-insets handling**: top bar under the status clock, dock under gesture nav → status/navigation bar padding added.
* **Stale UI**: top-bar streak/name frozen forever; shell not observing data changes → ProState-driven recomposition.

## P2 — correctness & ANR
* Remove/Skip exercise now confirm (engine's verbatim strings) — no more accidental deletes.
* Rest timer honors the **autoRest** power setting; **haptics** now actually buzz on log.
* Reset-all-data ran `runBlocking` on the main thread → coroutine + **pre-reset safety snapshot** written to `files/backups/` + vault snapshot.
* Export's vault read ran `runBlocking` on main → moved to IO; `p4_last_backup` stamped only on success.
* `EngineHooks.rebuildDerived` was never assigned — derived data now rebuilds after import.
* Dashboard readiness bands aligned with spec (≥80 fully recovered / ≥60 recovering / ≥40 fatigued).
* Library: groups collapsed by default (1,413 exercises no longer jank first paint), search-no-match empty state, raised cap for filtered results.
* Composite chart empty state; volume bars scale from 0; malformed dates no longer render "Jan 1, 1970"; name ellipsis; 40dp delete target with contentDescription.
* Rest-timer "+15" respects run; RIR text un-inverted ("2–3" at RPE 7).
* Time-wizard: always shown first (Continue/Replace step only with a draft), "Continue" navigates, generate lands on the workout screen, "All day" no longer reads "All day min".
* Settings: **Preferred Workout Days** picker (weekly goal), gender "Other", accent swatches follow live dark/light, dead toggles honestly marked, failed-export no longer counts as a backup.

## P3 — polish
* Chip estimated time now uses the engine estimator (was count×5+10 — "75 min" chip on a 45-min session).
* Theme scrim + streak fire use theme tokens; accent display names match legacy copy; muscle tags scroll; draft badge dot restored on the Train FAB; dock-clearance spacers right-sized; single recovery bar per muscle; off-by-one "resting in Xd".

## Verified (this build)
* Unit tests **7/7** (journey suite: 4-week simulation, backup round-trip + hostile import, backdated stats/fatigue, fresh-install & unicode/NaN edges; model round-trips).
* `verify_apk.py` **16/16 PASS** on the shipped APK: 13/13 font resources mapped, all TTFs real + uncompressed, zero R.font refs, permissions, v2+v3 signature (cert `1e08a903…7b5953` — your phone's cert), zipalign.
* `./gradlew clean testReleaseUnitTest assembleRelease` — BUILD SUCCESSFUL.

## Known gaps (honest, scoped for 1.3)
Themes/Studio/Legal-Center/onboarding screens are routed to Settings rather than blank; per-profile Room namespaces, system notifications, radar/RPE charts remain backlog. The legacy library (1,558 exercises) was re-extracted schema-faithfully; verbatim library data ships in `assets/data/exercises.json`.
