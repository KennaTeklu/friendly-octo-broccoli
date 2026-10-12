# 17-longevity — Longevity score components, aging risks, trend & variety

Source: `reference/scripts/app/17-longevity.js` (L74250–75879 of original `app.js`; ~1,630 lines)

## A. File purpose
This module computes the holistic Longevity Score (0–100). It aggregates six base sub-scores (`gripStrength`, `balance`, `jointMobility`, `posture`, `muscleBalance`, `consistency`) plus two dynamic scores (`trend` — 3-month compound-lift improvement; `variety` — 30-day longevity-muscle coverage). Weights are age-dependent (youth emphasizes muscleBalance; 50+ emphasizes balance + jointMobility) and normalized to sum 1.0. The module also runs `assessAgingRisks(baseScores)` and emits trend/variety recommendations.

## B. Public surface
- `calculateTrendScore()` — for `keyExercises = ['squat','deadlift','bench_press','overhead_press']`, filters history to last 3 months, computes `(newest1RM − oldest1RM) / oldest1RM`, averages across exercises, converts to 0–100 scale (`50 + avgImprovement × 250`), clamped 0–100. Neutral 50 if no data.
- `calculateVarietyScore()` — over the last 30 days, collects distinct longevity muscles trained; returns `min(100, round((trainedCount / longevityMuscleCount × 100) × 1.5))` (67% trained → 100).
- `weightBase{}` — age-bracketed weights for each sub-score (`<30`, `<50`, `50+`).
- Final score assembly — normalizes weights to sum 1.0, computes weighted sum across `allScores = {...baseScores, trend, variety}`, rounds.
- `assessAgingRisks(baseScores)` — produces risk list per aging factor.
- Recommendations — trend/variety threshold strings (`<40` declining, `>70` progressing; `<50` neglecting, `>=80` excellent variety).

## C. Dependencies
- Globals: `workoutData`, `muscleDatabase`, `Math`, `Date`.
- Cross-module: `calculateGripStrengthScore`, `calculateBalanceScore`, `calculateJointMobilityScore`, `calculatePostureScore`, `calculateMuscleBalanceScore`, `calculateConsistencyScore`, `estimate1RM` (Stats/OneRmLab), `assessAgingRisks`.

## D. Native-equivalent mapping
- All sub-scorers → `engine/Stats.kt` private functions: `calculateGripStrengthScore` (L972), `calculateBalanceScore` (L1040), `calculateJointMobilityScore` (L1144), `calculatePostureScore` (L1186), `calculateMuscleBalanceScore` (L1222), `calculateConsistencyScore` (L1249), `calculateTrendScore` (L1338), `calculateVarietyScore` (L1377). All ported with the same formulas (`50 + improvement × 250`, `min(100, pct × 1.5)`).
- `assessAgingRisks` → `engine/Stats.kt::assessAgingRisks` (L1482) returning `List<AgingRiskRow>`, with `assessAgingRisksInternal` (L1451) — ported; native uses `RiskDef` table (L1432) for cleaner config-driven risk definitions.
- Final weighted score → `engine/Stats.kt::calculateLongevityScore` (referenced at L525 via `Stats.LongevityScore` and `ringsData` L528–550). Native also exposes the score via `RecoveryScreen`/`Dashboard.kt` ring component.
- `weightBase{}` age-bracketed weights → ported inline in `Stats.calculateLongevityScore` (verified via `BATCH-3-PARITY` migration notes).
- `Model.kt::AgingRisk` (L128) — ported data class for risk rows.

## E. Patterns worth porting
- **Age-bracketed weighting** (`<30` youth, `<50` middle, `50+` senior) with explicit rebalancing (youth → muscleBalance 0.25, senior → jointMobility 0.30 + balance 0.25) is a genuinely thoughtful design — a 25-year-old's "longevity score" shouldn't be dominated by balance. Native mirrors this but the design rationale is worth preserving in code comments.
- **Normalize weights to sum 1.0** before combining — defensive against typos in the weight table.
- **Trend score 0% improvement → 50 (neutral), +10% → 75, −10% → 25** — symmetric, intuitive mapping.
- **Variety score scale factor 1.5** — training 67% of longevity muscles yields 100 (not 100%); this is a calibration choice worth keeping documented.
- **Risk definitions as data** (`RiskDef` table) — native already does this; legacy hardcodes. Native pattern is better.

## F. Feature IDs
`P17-LONGEVITY-SCORE`, `P17-TREND-SCORE`, `P17-VARIETY-SCORE`, `P17-AGE-BRACKET-WEIGHTS`, `P17-WEIGHT-NORMALIZATION`, `P17-AGING-RISKS`, `P17-LONGEVITY-RECOMMENDATIONS`.
