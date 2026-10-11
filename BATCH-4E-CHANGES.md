# BATCH-4E — Appearance redesign

**versionCode:** 21
**versionName:** 1.5.1
**Package:** com.peakform.fitness
**Cert SHA-256:** 1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953
**APK SHA-256:** ca9cb3bd9b305d9d401b2972d6f2ab67a07d69d87d9107f51fbc51549cfb7f3d

Differs from Batch 4D (53F3BE9D…) and Batch 4C (12E862D8…).

---

## ITEM 1 — Appearance redesign

### Context

Batch 4D shipped the Material You override banner and the crash fix. The banner was the wrong shape of fix — it told the user their choice was being ignored instead of applying their choice. This batch replaces the banner with a redesign that collapses two cards into one and makes Material You a palette option, not a mode.

### Per-ID status

| ID | Description | Status | Evidence |
|---|---|---|---|
| P4E-UI-01 | Delete the "Theme picker" card | ✅ shipped | `ThemePickerCardBody` function deleted from `Settings.kt`; registry entry `"p4ThemeCard"` removed from `SETTINGS_CARDS` (line 70); dispatch entry removed (was at line 446). Comment block at `Settings.kt:706-711` documents the deletion. |
| P4E-UI-02 | Rename "Appearance" → "Change theme" | ✅ shipped | `SETTINGS_CARDS` entry changed from `"Appearance"` to `"Change theme"` at `Settings.kt:69`. |
| P4E-UI-03 | Material You default OFF on fresh install | ✅ shipped (verified) | Code path: `MainActivity.kt:87` → `ThemeController.loadDynamic(ProPrefs.get(this, "p4_dynamic"))` → `loadDynamic(null)` → `dynamic = (null == "on")` = `false`. On fresh install, `p4_dynamic` key is absent from prefs, so `loadDynamic(null)` sets `dynamic = false`. No code path seeds `p4_dynamic` to `"on"` on first launch. The 4B fix already established this default; 4E confirms it with a test. |
| P4E-UI-04 | Material You tile added to the colour grid | ✅ shipped | `Settings.kt:663-688` — gradient circle tile rendered after the 16 accent swatches. Only shown on Android 12+ (`SDK_INT >= 31`). Uses `dynamicDarkColorScheme(ctx).primary` / `dynamicLightColorScheme(ctx).primary` for the live wallpaper palette preview when active; a static purple gradient when not. |
| P4E-UI-05 | Tapping a non-Material-You swatch: sets accent and sets p4_dynamic = off in the SAME atomic write | ✅ shipped | Code path: `ThemeController.setAccentAndDynamic()` at `ThemeController.kt:83-93`. Updates runtime state (`dark`, `accentId`, `dynamic`) synchronously via `mutableStateOf`, then writes both keys in a single `ProPrefs.get(ctx).edit().putString("p4_theme", …).putString("p4_dynamic", …).commit()` call. `commit()` is synchronous and atomic — no frame can observe `accent=X` with `dynamic=on`. Called from `Settings.kt:654` (swatch tap) and `Settings.kt:681` (Material You tile tap). |
| P4E-UI-06 | Remove the banner. Replaced by auto-disable | ✅ shipped | The 4B hint text ("Material You is active — turn it off below…") and the palette dimming (`paletteAlpha`) are both removed from `appearanceInner`. The user never sees text telling them their choice is being ignored, because their choice is applied the moment they tap. |
| P4E-UI-07 | Gym Mode and Material You toggle stay | ✅ shipped (design choice) | Both `ToggleRow`s stay. The Material You toggle is an explicit on/off for users who prefer a standard control; the tile is a visual preview + one-tap enable. Both read from the same `ThemeController.dynamic` state, so they are always in sync. Rationale: the toggle provides a familiar control pattern and a description string ("Tint Pro with your wallpaper palette…"); removing it would lose that context. The tile alone is sufficient for users who prefer visual controls. |

### Atomic write — exact code path

**File:** `app/src/main/java/com/peakform/fitness/ui/ThemeController.kt`
**Lines:** 83–93

```kotlin
fun setAccentAndDynamic(mode: String, accent: String, dynamic: Boolean, ctx: Context? = null) {
    dark = mode != "light"
    if (ACCENTS.any { it.id == accent }) accentId = accent
    this.dynamic = dynamic
    ctx?.let { c ->
        ProPrefs.get(c).edit()
            .putString("p4_theme", currentJson())
            .putString("p4_dynamic", if (dynamic) "on" else "off")
            .commit()
    }
}
```

**Why this is atomic:** `SharedPreferences.edit().commit()` writes all pending changes to disk in a single synchronous transaction. Both `p4_theme` (accent + mode) and `p4_dynamic` land in the same transaction. No frame can observe one key written and the other not. The previous code path called `ThemeController.set()` (writes `p4_theme` via `ProPrefs.put()` → `edit().apply()`) and `ThemeController.setDynamic()` (writes `p4_dynamic` via `ProPrefs.put()` → `edit().apply()`) as two separate async writes — Material You could win for one frame because the two writes raced.

### Call sites

- **Swatch tap** (`Settings.kt:654`): `ThemeController.setAccentAndDynamic(mode, a.id, dynamic = false, ctx = ctx)` — writes accent + turns Material You off atomically.
- **Material You tile tap** (`Settings.kt:681`): `ThemeController.setAccentAndDynamic(mode, accentId, dynamic = true, ctx = ctx)` — writes accent (unchanged) + turns Material You on atomically.
- **Dark/light toggle** (`Settings.kt:622`): still uses `ThemeController.set(m, accentId, ctx)` — only writes `p4_theme`, no dynamic change needed.
- **Material You toggle** (`Settings.kt:701`): still uses `ThemeController.setDynamic(on, ctx)` — only writes `p4_dynamic`, no accent change needed.

---

## ITEM 2 onward — per BATCHES.md

BATCHES.md plans through vCode 18 only and does not name any items for vCode 21. The actual batches went 4A/4B/4C/4D (vCodes 17–20) as a parallel track. No vCode 21 scope is specified in BATCHES.md.

The deferred-from-4C candidates (1RM Audit, Quick Actions modal, Reset-to-Default library, Accessibility pass) appear only as general references in BATCHES.md/PRIORITIES.md/SPEC.md — they are not named for vCode 21. Per the batch prompt: "If BATCHES.md does not name these for vCode 21, leave them for the next batch." These items remain deferred.

**No other BATCHES.md items included for vCode 21.**

---

## Non-regressions

- **Theme persists across restart** ✓ — `p4_theme` written via `commit()` in `setAccentAndDynamic`; read via `ThemeController.load()` at `MainActivity.kt:86`.
- **Gym Mode toggle still functions** ✓ — `ToggleRow` at `Settings.kt:692` unchanged; writes `p4_gym` via `ProPrefs.put()`.
- **Every colour currently in the palette is still available** ✓ — all 16 `ACCENTS` entries rendered in the grid at `Settings.kt:641`; no swatch removed.
- **Crash fix from 4D untouched (W1, W2, W3)** ✓ — 4D crash fix re-applied to `ProState.kt`, `ProStore.kt`, `Profiles.kt`. The repo HEAD (8b4f144) did not contain the 4D fix (it was local-only from the previous session), so the fix is re-applied here as a non-regression requirement. `Batch4DConcurrentSaveTest.kt` passes.
- **Profile switching still works** ✓ — `Profiles.switchTo()` calls `ProState.flush()` then `reopenStore()`; the 4D fix ensures flush joins all in-flight saves before close.
- **Onboarding still presents the Material You choice the same way it did in 4B** ✓ — `OnboardDraft.kt` theme step unchanged. Onboarding seeds `p4_dynamic` via `ThemeController.setDynamic()` only when the user explicitly picks Material You; the default is OFF. No change from 4B.

---

## Test evidence

### Batch4EAppearanceTest.kt — 4 tests

1. `freshInstall_materialYouOff_defaultAccentApplied` — simulates fresh install (clears prefs, loads ThemeController with null values). Asserts `p4_dynamic` is absent, `ThemeController.dynamic` is false, default accent is "blue". Addresses P4E-UI-03.
2. `tapSwatch_writesAccentAndDynamicAtomically` — sets Material You ON, then taps a "green" swatch via `setAccentAndDynamic`. Asserts both `p4_theme` and `p4_dynamic` are written atomically (`p4_theme` contains `"accent":"green"`, `p4_dynamic` is `"off"`). Addresses P4E-UI-05.
3. `tapMaterialYouTile_writesDynamicOnAtomically` — taps the Material You tile. Asserts `p4_dynamic` is `"on"` and accent is unchanged. Addresses P4E-UI-04 + P4E-UI-05.
4. `themePickerCardBody_deleted` — compile-time guard verifying `ThemePickerCardBody` is gone. Addresses P4E-UI-01.

### Batch4DConcurrentSaveTest.kt — 4 tests (re-applied from 4D)

All four tests pass, confirming the 4D crash fix is intact.

All eight tests pass under `./gradlew testReleaseUnitTest`.

---

## Guardrails

- Cert: `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953` ✓
- APK hash differs from 53F3BE9D (Batch 4D) and 12E862D8 (Batch 4C) ✓
- versionCode increments by exactly 1 (20 → 21) ✓
- No personal names, no keystores, no local.properties ✓
- `adb install -r` only — no uninstall ✓
