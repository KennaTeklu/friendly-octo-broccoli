# DESIGN-CONTRACT.md

The full design contract for the native app at vCode 31 / versionName 2.1.0.

## 1. Storage boundary

| Layer | Where | What |
|-------|------|------|
| Per-profile Room DB | `pro_<id>.db` | workouts, exercises, user, savedWorkout, vault, meta |
| Per-profile SharedPreferences | `p4_ls_<profileId>` | onboarding flag, draft, health screen, theme, vocab, gym mode, library prefs, notification toggles, power settings |
| Global SharedPreferences | `p4_ls` | active profile id, default profile id, device id, consent log, registry mirror |
| Profile registry | `registry` | the JSON blob enumerating every profile |

## 2. Theme contract

- 3 modes: `system` | `dark` | `light`.
- Persisted as `{"mode": "...", "accent": "..."}` JSON in `p4_ls_<id>` under key `p4_theme`.
- Material You flag in `p4_ls_<id>` under key `p4_dynamic` (`"on"` / `"off"` / null).
- ThemeController.set() is write-through: state + storage in the same call.
- After profile switch: `ThemeController.reload(ctx)` re-reads the new profile's prefs.

## 3. Coach contract

- 29 specific coaches + 8 generic coaches register in `CoachRegistry`.
- Each coach is a pure function over `CoachInput` (no ProState writes).
- 12 engines fan out the same `EngineInput`, fragments assemble into one prescription.

## 4. State model

- Events → `StateFold.reduce(tree, event) → tree`.
- Side-effect free reducer. Engine writes result via ProState.
- Export contract: every backup file is `PeakForm-Complete-Backup-v1` with schema version 31.

## 5. Migration contract

- One-shot, idempotent, gated on `p4_v18_migrated` (meta → meta) and `p4_v22_migrated` (global → per-profile).
- Never overwrites per-profile state a user already wrote.
- Migration runs on cold start before any screen renders.

## 6. Build contract

- `./gradlew clean testReleaseUnitTest assembleRelease` must pass.
- Cert SHA-256: `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953`.
- No `allowMainThreadQueries()` anywhere.
- `ProDb.withDb()` for every Room access that needs to survive concurrent close().

## 7. Accessibility contract

- All icons accept `contentDescription` (passes through to `Modifier.semantics`).
- Min 48dp touch targets (`Accessibility.MinTouchTarget`).
- We do NOT use `invisibleToUser()`, `heading()`, or `traversalIndex()` — experimental APIs.
- Plain `contentDescription` is the stable, supported semantic.

## 8. Versioning

- versionCode increments by 1 per batch.
- versionName follows semantic versioning (major.minor.patch).
- vCode 21 → 31 ships BATCH-5A through BATCH-31 in this build.
