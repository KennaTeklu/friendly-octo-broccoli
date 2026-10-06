# Pro 1.1 — Data Migration

## Why migration works automatically this time

The v1.0 experiment used a different package (`com.peakform.fitness2`), and Android sandboxes
per-package data — so v1.0 could never read your real data without manual exports. v1.1 uses
your app's **original package and signing cert**, so when `adb install -r` upgrades in place,
the private data directory survives — including the old WebView's IndexedDB (the Dexie
`WorkoutApp` database), its `localStorage` (60+ keys: profiles, settings, vault snapshots,
power preferences…), and the per-profile origins.

## The automatic shuttle (default path)

On first launch after the upgrade, the app runs a **one-shot, invisible data shuttle**:

1. It reads the native profile registry the original shell kept (`registry` SharedPreferences,
   key `pro_profiles_v2`) to find every legacy profile id, and additionally scans the
   WebView storage directory for any `https://<id>.appassets.local` origins.
2. For each origin it creates a hidden WebView pointed at the same host — same host ⇒ same
   IndexedDB/localStorage origin ⇒ full read access to your old databases.
3. A tiny bundled page (`migrate.html`, raw IndexedDB — no libraries) dumps:
   - Dexie `WorkoutApp`: `workouts`, `exercises`, `user`, `savedWorkout` (your current draft)
   - IndexedDB `p4_vault`: every legacy snapshot
   - `localStorage`: every key (profiles registry, p4_theme, power settings, notifications,
     custom exercises, backup stamps…)
4. The native side picks the richest `workoutData` across profiles (most completed workouts
   wins), merges it additively, restores the preference keys, takes a guarded snapshot first,
   and writes everything into Room.
5. The WebView is destroyed. It is never used again for anything. **The app you use every day
   has zero WebViews.**

Manual trigger/re-run: clear the migration flag and relaunch —

```bash
adb shell run-as com.peakform.fitness sh -c 'echo -n > /dev/null'
# simplest honest re-run: reinstall pro.apk with install -r (flag persists; use the manual path below instead)
```

(There is deliberately no in-app button for this — the shuttle runs once, silently.)

## The manual path (belt & suspenders)

If the automatic import reports nothing (e.g. the old data directory was wiped by an
unrelated uninstall), use the legacy app as donor:

1. `adb install -r legacy-tools/pro-legacy-fixed.apk` → old app with working console and
   a real **Export** that produces a `PeakForm-Complete-Backup` or Format A JSON.
2. Transfer the JSON to the phone (or use any file manager).
3. In **Pro 1.1 → Settings → Data & backup → Import backup**, pick the file.
4. The importer accepts **all legacy formats**: `PeakForm-Complete-Backup-v1`,
   Format A (`export_*.json`), and older P4 bundles. Import always takes a guarded
   snapshot first, then merges additively (per-record `_p4SavedAt`/history reconciliation) —
   it can never silently destroy your current data.

Export paths in v1.1 (all byte-compatible with the old app's importers):
- **Export full backup** → `PeakForm-Complete-Backup-v1` (localStorage bundle + vault + live data)
- **Export workout JSON** → legacy Format A
- **Quick snapshot** → guarded vault snapshot (restore-able, never auto-restored)

## What is preserved vs. fixed

| Legacy data | v1.1 behavior |
|---|---|
| Completed workouts (Dexie `workouts`) | Imported verbatim (every field, byte-compat shape) |
| Per-exercise records (history, mu/sigma², tested 1RM, nextWeight) | Imported verbatim |
| Current draft workout (`savedWorkout` + emergency backup) | Restored if incomplete, arbitrated by the legacy scoring rule |
| Profiles (`p4_profiles`, per-profile data keys) | Imported; picker visible in Settings → Profiles (full multi-profile namespaces land in 1.2) |
| Theme/accent (`p4_theme`), Gym Mode, power settings, notification toggles, reading level, language | Restored and live |
| Vault snapshots (IndexedDB `p4_vault`) | Imported as guarded snapshots — **never auto-restored over live data** (the P4 Vault autoRestore data-loss bug is dead by construction) |
| `p4_dev_lock`, anti-debug remnants | Ignored forever |

## The P1–P4 structural fixes, revisited natively

- **P1 (silent console)** — logging is native from day one: `adb logcat -s ProConsole:* ProNative:*`
  plus `files/logs/pro.log`. No debug UI exists; nothing can mute it.
- **P2 (two stacked settings screens)** — one native Settings screen: the 10 legacy P4 groups,
  the Training-environment card, **plus a Training-preferences card** resurfacing the controls
  the legacy accordion orphaned (Height, Experience, Goal, Workout days, Rest, Progression).
  Saving **merges** settings fields — the legacy `saveSettings()` wipe bug cannot exist here.
- **P3 (nav vanishing)** — the glass dock is a structural part of the shell, outside the
  scrollable content. It cannot be hidden by any state.
- **P4 (data loss)** — one store (Room), event-driven saves + debounced drafts (200 ms) +
  `onStop` flush + emergency backup, guarded snapshots, merge-only imports, type-RESET danger
  zone with optional pre-reset backup.
