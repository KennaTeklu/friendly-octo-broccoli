# 15-fatigue-recovery — Systemic fatigue, recovery section, training readiness, muscle recovery

Source: `reference/scripts/app/15-fatigue-recovery.js` (L70558–73019 of original `app.js`; ~2,470 lines)

## A. File purpose
This module is the recovery brain of the app. It computes a systemic recovery factor (0–1) from the last 7 days of training volume/sets/RPE, blends it with a muscle-weighted recovery percent (70% muscle, 30% systemic) into an overall readiness score, maps the score to a colored status (`Excellent`/`Good`/`Moderate`/`Low`/`Very Low`) with a textual recommendation, and renders the full recovery section (overall readiness ring + per-muscle recovery bars + major/longevity/hand/foot muscle breakdowns).

## B. Public surface
- `calculateSystemicRecoveryFactor()` — recent-7-days aggregation: `fatigueFromFreq = min(50, workouts × 10)`, `fatigueFromVolume = min(30, relativeVolume / 500)`, `fatigueFromRPE = min(20, (avgRPE − 5) × 4)`, total clamped 0–100 → recovery factor `1.0 − fatigue/200`, clamped `[0.5, 1.0]`, NaN-safe default `0.8`.
- `updateRecoverySection()` — applies `applyFatigueDecay`, computes `calculateMuscleLastTrained`, builds a `muscleWeights{}` map (quads 2.0 → hand_lumbricals 0.1), weighted average recovery percent, blends `0.7 × muscleAvg + 0.3 × systemicPercent` into `overallRecovery`, picks `readinessColor`/`readinessText`/`recommendation` via thresholds (85/70/50/30), renders `#globalRecovery` card with score circle + progress bar.
- `muscleWeights{}` — per-muscle weighting (large muscles count more towards overall recovery).
- Readiness thresholds — 85 Excellent, 70 Good, 50 Moderate, 30 Low, <30 Very Low, each with a recommendation string.

## C. Dependencies
- Globals: `workoutData`, `document`, `Math`, `Date`.
- Cross-module: `applyFatigueDecay`, `calculateMuscleLastTrained`, `getAllMuscleGroups`, `daysSinceTrained`, `muscleLastTrained`, `muscleDatabase`.

## D. Native-equivalent mapping
- `calculateSystemicRecoveryFactor` → `engine/Sessions.kt::calculateSystemicRecoveryFactor` (private, L1082) and exposed via `engine/Stats.kt::systemicRecoveryFactor` (L355) — ported with identical formulas (`min(50, freq×10)`, `min(30, relVol/500)`, `min(20, (avgRPE−5)×4)`, clamp `[0.5,1.0]`).
- `updateRecoverySection` → `engine/Stats.kt::recoveryReport` (L424) + `overallRecoveryPct` (L507) + `ringsData` (L528) + `ui/screens/Recovery.kt::RecoveryScreen` (L47) — ported. Native also exposes `CouplingMatrixCard` (Recovery.kt L417) and `DecayModelCard` (L524) — enhancements legacy lacks.
- `muscleWeights{}` map → `assets/data/muscle_weights.json` — ported, consumed by `Stats.recoveryReport`.
- Readiness thresholds (85/70/50/30) + recommendation strings → `Stats.recoveryReport` + `Recovery.kt` Compose card — ported.
- `applyFatigueDecay` → `engine/Library.kt::Fatigue.applyFatigueDecay` (L344) — ported.
- `calculateMuscleLastTrained` → `Fatigue.calculateMuscleLastTrained` (L576) + `daysSinceTrained` (L592) — ported.

## E. Patterns worth porting
- **70% muscle + 30% systemic blend** is a deliberate design choice; native `Stats.overallRecoveryPct` calls `recoveryReport().overall` which may use different weights — verify the blend ratio matches.
- **Per-muscle weighting table** (quads 2.0 down to hand_lumbricals 0.1) — gives a meaningful "overall recovery" without small-muscle noise dominating. Already ported via `muscle_weights.json`; worth keeping in sync when adding new muscles.
- **NaN-safe defaults** (`isNaN(factor) ? 0.8`) — defensive; native Kotlin's `Double.NaN` checks should mirror.
- **Threshold-based recommendation strings** — clear UX, easy to A/B test thresholds.
- **`window.lastOverallRecovery` global** for cross-section access — Compose equivalent is a top-level StateFlow or `Stats.overallRecoveryPct()` cached in a `remember`.

## F. Feature IDs
`P15-SYSTEMIC-RECOVERY-FACTOR`, `P15-MUSCLE-WEIGHTED-RECOVERY`, `P15-OVERALL-READINESS-BLEND`, `P15-READINESS-THRESHOLDS`, `P15-RECOVERY-SECTION-UI`, `P15-MUSCLE-WEIGHTS-TABLE`.
