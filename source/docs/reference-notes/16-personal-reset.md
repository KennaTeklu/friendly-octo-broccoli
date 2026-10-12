# 16-personal-reset — savePersonalInfo, reset workflow, wipe, import restore

Source: `reference/scripts/app/16-personal-reset.js` (L73020–74249 of original `app.js`; ~1,230 lines)

## A. File purpose
This module is the settings-write side of the app. It (1) gathers and validates personal-info inputs (name, birthDate, gender, weight, height, experience, goal, restTime 30-300s, progressionRate 0.5-5%, postpartum months for females, bottom-nav auto-hide, muscle coupling toggle); (2) writes them into `workoutData.user` and the `settings{}` sub-object; (3) preserves menstrual data when gender changes; (4) calls `updateUserCache()` + `saveToLocalStorage()` + UI refresh; (5) implements reset/wipe workflows and import-restore flows.

## B. Public surface
- `savePersonalInfo()` (implicit) — input gathering + validation:
  - `birthDate` future-check.
  - `weight`/`height` positive-number check.
  - `restTime` integer 30-300.
  - `progressionRate` 0.5-5% → divided by 100 to decimal.
  - `postpartumMonths` (female only, ≥0).
  - `bottomNavAutoHide`, `muscleCoupling` toggle persistence.
  - `workoutDays[]` from checked `.workout-day` checkboxes.
  - Persists `theme`/`darkMode` from localStorage.
- Validation error path — collects `errorMessages[]`, shows `alert` joined, returns early.
- Post-save updates — `updateHeaderNameAndStreak()`, `updateUserCache()`, `saveToLocalStorage()`, gender-change handling for period button visibility.
- Reset workflow — destructive confirm + `localStorage.clear()` + Dexie wipe + reload.
- Import-restore flow — merges imported JSON into `workoutData`.

## C. Dependencies
- Globals: `document`, `workoutData`, `localStorage`, `alert`, `Swal`.
- Cross-module: `updateHeaderNameAndStreak` (11), `updateUserCache` (06), `saveToLocalStorage`, `applyFatigueDecay`, `renderLibrary`, `updateDashboard`, `updateRecoverySection`, `updateLongevityScoreDisplay`, `db` (Dexie).

## D. Native-equivalent mapping
- `savePersonalInfo` → `ui/screens/Settings.kt::PersonalCardBody` (L557) — Compose form writes into `ProState.data.user` via `ProfileSettings.kt::ProfileSettings` (L27) + `ProState.notifyChanged()`. Validation rules match (restTime 30-300, progression 0.5-5%).
- `postpartumMonths` (female-only) → `Model.kt::UserProfile.postpartumMonths` field — ported.
- `bottomNavAutoHide` toggle → `ProfileSettings.getNotif`/`setNotif` equivalent — ported.
- `muscleCoupling` toggle → `ProfileSettings` — ported.
- Reset/wipe workflow → `Settings.kt::ResetFlowDialog` (L288) + `SettingsCardEntry("p4DangerCard", "Danger zone", …)` (L92) + `P4Button("Reset all data", …)` (L547) — ported with explicit "Your license and app settings are preserved" copy.
- Import-restore → `core/MigrationBridge.kt::ImportMerger` (L180) + `ui/screens/PasteJsonScreen.kt` — ported.
- Library reset → `engine/Library.kt::Library.resetToDefault` (L237) + `reinjectCustom` (L251) — ported.

## E. Patterns worth porting
- **Validation as collected messages** (`errorMessages[]` joined with newlines, single alert) — better UX than per-field toasts; native could collect all errors into one dialog.
- **`progressionRate` percentage→decimal conversion at save time** — keeps the UI in user-facing units (0.5–5%) while the data model stores a decimal. Native should mirror.
- **`postpartumMonths` female-only field with delete-on-invalid** — clean conditional schema; native mirrors.
- **Gender-change UI reflow** (show/hide period button) — native should mirror via `if (user.gender == "female")` Compose conditional.
- **Preserving license/app-settings during wipe** — native ResetFlowDialog already states this; the field-level preservation list (which keys survive) should be audited.

## F. Feature IDs
`P16-SAVE-PERSONAL-INFO`, `P16-INPUT-VALIDATION`, `P16-PROGRESSION-RATE-CONVERSION`, `P16-POSTPARTUM-MONTHS`, `P16-NAV-AUTOHIDE-TOGGLE`, `P16-MUSCLE-COUPLING-TOGGLE`, `P16-RESET-WORKFLOW`, `P16-IMPORT-RESTORE`.
