# Design Contract — PeakForm v2.0.0

**Version:** 2.0.0 (versionCode 30)
**Date:** 2026-10
**Audience:** contributors, reviewers, downstream ports.

This document is the binding design contract for the v2.0.0 release. Code
paths that disagree with this contract are bugs; tests that disagree with
this contract are also bugs. The contract wins.

## 1. Storage boundary

### 1.1 Global state (ProPrefs — "p4_ls")

Global state lives in `ProPrefs` (SharedPreferences "p4_ls") and is shared
across every profile on the device:

- Theme mode token (`system` / `dark` / `light`) + accent id (`p4_theme`)
- Material You flag (`p4_dynamic`)
- Active profile id (`p4_active_profile`)
- Default profile id (`p4_default_profile`)
- Device UUID (`deviceId`)
- One-shot migration flags (`p4_v18_migrated`, `p4_v22_migrated`)
- App license (`p4_license`)
- Consent log (legal record — per-app, not per-profile)

### 1.2 Per-profile state (ProfileState — "p4_ls_<profileId>")

Per-profile state lives in `ProfileState` (SharedPreferences
"p4_ls_<profileId>") and is isolated per profile:

- Onboarding (`p4_onboarded`, `p4_ob_draft`, `p4_ob_finished_at`, `p4_first_launch`)
- Health screen (`p4_health_screened_at`, `p4_health_tier`, `p4_health_general`,
  `p4_health_followup`, `p4_health_joints`, `p4_health_unlocked_at`,
  `p4_health_declared`, `p4_health_intro_seen`)
- Emergency backup (`workoutEmergencyBackup`)
- Training prefs (`p4_training_mode`, `p4_rest_time`, `p4_progression_rate`,
  `p4_workout_days`, `p4_express_mode`, `p4_aggression`, `p4_competition`)
- Library prefs (`p4_lib_group`, `p4_lib_chunk`, `p4_lib_expand_all`,
  `p4_lib_stream`, `p4_library_collapsed`, `p4_custom_ex`)
- Notifications (`p4_notif_*`, `p4_notif_suppressed_*`)
- Vocab / language (`p4_vocab`, `p4_lang`)
- Power user (`p4_power_settings`, `p4_gym`, `p4_secure`)
- Streak (`lastStreakRecoverAt`)
- Self-assessment (`p4_self_assessment_last`)
- Backup manifest (`p4_backup_manifest`)

### 1.3 ProfileSettings facade

All per-profile reads/writes go through `ProfileSettings` — never reach into
`ProfileState` directly from UI code. The facade exposes named constants
(e.g. `ProfileSettings.LIB_GROUP`) so a typo at a call site is a compile
error, not a silent data-corruption bug.

## 2. Theme controller

### 2.1 Three modes

`ThemeController.modeToken` is `"system"` | `"dark"` | `"light"`.

- `system` follows the device dark-theme setting; `dark` is resolved at
  boot and on every `reload(ctx)` (which is called after every profile
  switch).
- `dark` and `light` are explicit overrides — they ignore the system
  setting.

### 2.2 Write-through

Every setter (`set`, `setAccentAndDynamic`, `setDynamic`, `persist`)
mutates the runtime state AND writes to ProPrefs in the SAME call. No
caller can forget to persist — the call site is one line.

### 2.3 Atomic accent + dynamic write

`setAccentAndDynamic` writes `p4_theme` AND `p4_dynamic` in a single
`SharedPreferences.commit()` so no frame can observe `accent=X` with
`dynamic=on` (which would show Material You instead of the chosen
accent).

## 3. Room store

### 3.1 No main-thread queries

`ProDb.get(...)` MUST NOT be called with `.allowMainThreadQueries()`. The
boot path uses `ProState.init` → `ProState.loadAll()` on `Dispatchers.IO`.

### 3.2 withDb lock

`ProDb.withDb(ctx, dbName, block)` runs `block` against the DAO while
holding the instances lock, so `close()` cannot race an in-flight DAO
call.

### 3.3 wipeAll @Transaction

`ProDao.wipeAll()` is annotated `@androidx.room.Transaction` so a crash
between `clearUser()` and `clearSaved()` cannot leave a half-wiped DB.

## 4. Profiles flow

### 4.1 switchTo

```
flush → setActive → invalidate cached prefs → reopen → loadAll →
  ThemeController.reload → resolveDark → stampFirstLaunch → notifyChanged
```

All on `Dispatchers.IO` via `runBlocking` so the switch is atomic from
the caller's POV.

### 4.2 delete

```
flush → close DB handle → delete db file + wal + shm →
  invalidate cached prefs → reopen + loadAll + theme for new active
```

If the deleted profile was the active one, the new active is picked by
"first remaining whose DB file exists" (not just first in the registry —
handles the case where a profile was created but never opened).

### 4.3 4D crash fix retained

The `@Transaction` wipeAll, the `withDb` lock, and the in-flight save
join in `flush()` are all retained from Batch 4D.

## 5. ProTheme tint tokens

`ProColors` exposes:

- `tint` — subtle accent wash (alpha 0.16 dark / 0.12 light)
- `tintStrong` — stronger wash (0.28 / 0.22)
- `tintBg` — accent-tinted surface via `mix(surface, accent, t)`
- `tintLine` — accent hairline (0.45 / 0.40)
- `inkSoft` — text on tinted surfaces (`mix(text, accent, 0.10)` at 0.85 alpha)
- `ring` — accent focus ring (0.55 / 0.50)
- `cardGradient` — linear gradient surface → surface2
- `focusRing` — radial halo

`ProColors.mix(a, b, t)` is a linear interpolation between two colors,
used so the tint matches the user's accent without a hard alpha overlay.

The M3 `ColorScheme` is wired so `primaryContainer`, `secondaryContainer`,
`tertiaryContainer`, `surfaceTint`, `outlineVariant`, and `inverseSurface`
all read from these tokens — any caller using `MaterialTheme.colorScheme.*`
automatically gets the user's chosen accent.

## 6. Settings routing

Per-profile settings read/write through `ProfileSettings`. Global settings
read/write through `ProPrefs`. The Settings screen's `StreamGroupCardBody`
and `Library` prefs are the canonical example — see
`Settings.kt` lines 1200-1240.

The Danger Zone card now houses:
- Reset all data (existing)
- Run 1RM Audit (new — opens `1rm_audit` section)
- Reset 1RM estimates (new — zeros `mu` + `sigma2` on every exercise record)

## 7. Library routing

Library prefs are per-profile. The default state is **all-collapsed** unless
the user has opted into "Expand all groups on open" via Settings.

`Library.resetToDefault(ctx)` clears every per-profile library pref AND
reloads the bundled JSON library, dropping any in-memory custom exercises.

The Library screen exposes a smart "Expand remaining groups" button that
only appears when the state is mixed (some collapsed, some not).

## 8. Icons accessibility

`FaIcon(contentDescription = ...)` wires the description to
`Modifier.semantics { contentDescription = ...; role = Image }`. When
`contentDescription` is null (the default), the icon is marked
`invisibleToUser()` so screen readers skip it.

## 9. Coaching engine

```
CoachRegistry
  ├── 29 specific coaches (one per exercise id)
  └── 8 generic coaches (keyed by tag: barbell, dumbbell, machine, cable,
                          bodyweight, kettlebell, band, compound)

CoachingEngine.evaluate(ctx, focusExerciseId?) → CoachingPlan
  ├── per-exercise tips from specific + generic coaches
  ├── overall tips from OverallCoach
  └── sorted by priority (1 = high), focus-tips floated to top
```

Coaches are pure functions — no side effects, no I/O. The engine handles
persistence + logging.

## 10. Screens

### 10.1 OneRMAuditScreen

Lists every exercise with a tested / estimated 1RM, marks stale estimates
(>6 weeks), and offers mark-tested + reset per row.

### 10.2 OneRmLabScreen

Interactive 1RM calculator (Epley / Brzycki / Lombardi / Mayhew /
Consensus), RPE-adjusted, with a percentage table for programming.

### 10.3 QuickActionsSheet

4-action horizontal pill row (Resume / Theme / Export / 1RM Audit) for
one-tap access from the dashboard.

### 10.4 TuningSheet

Per-profile tuning dials: aggression, rest time, progression rate. Saves
to both `ProState.data.user.settings` AND `ProfileSettings` so the next
session picks up the new values immediately.

### 10.5 PostSessionFeelSheet

RPE / readiness / mood / soreness / sleep survey after a workout. Saves
to `ProfileSettings.p4_self_assessment_last`. The next session's
prescribed load is adjusted via
`SelfAssessment.recommendedLoadMultiplier`.

## 11. Build

```
versionCode = 30
versionName = "2.0.0"
signingConfig = release (debug.keystore, cert 1e08a903…)
```

`./gradlew clean testReleaseUnitTest assembleRelease` MUST pass.

## 12. Test battery

- `Batch5BTest` — ProfileSettings + migrateFromPrefs
- `Batch6Test` — ThemeController 3-mode + reload + resolveDark
- `Batch8Test` — ProStore.withDb + wipeAll @Transaction + no main-thread queries
- `Batch11Test` — Library ProfileSettings routing + resetToDefault + changeGroupBy
- `Batch14Test` — CoachingEngine + SelfAssessment + ProgressSummary
- `Batch15Test` — OneRMAuditScreen + QuickActionsSheet + TuningSheet + PostSessionFeelSheet
- `CoachCoverageTest` — 29 specific + 8 generic coaches registered
- Updated: `Batch4BScreenshots`, `Batch4SettingsScreenshots`, `Batch4EAppearanceTest`

## 13. Forward-compatibility

- Storage formats are versioned (`p4_v22_migrated` for the prefs migration,
  `p4_backup_manifest` for backups).
- Future upgrades read the version tag and apply the appropriate migration
  in `ProfileState.migrateFromPrefs(ctx)`.
- Downgrades preserve data because the per-profile SharedPrefs file
  (`p4_ls_<profileId>`) is independent of the global file (`p4_ls`).
