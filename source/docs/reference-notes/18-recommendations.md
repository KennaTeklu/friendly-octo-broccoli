# 18-recommendations — Recommendations, breakdown panel, unsaved-workout checks, display

Source: `reference/scripts/app/18-recommendations.js` (L75880–81113 of original `app.js`; ~5,234 lines)

## A. File purpose
This module is the longevity dashboard presentation layer and the largest by line count. It (1) sorts risks by severity (`High:3, Medium:2, Low:1`) and emits risk-based recommendations with `URGENT:` prefix for High; (2) emits score-based general recommendations for `posture`, `balance`, `jointMobility`, `trend`, `variety` based on threshold bands; (3) emits goal-based advice (e.g. `goal==='longevity'`); (4) appends a 4–6 week reassessment reminder; (5) `updateLongevityScoreDisplay()` renders the score, conic-gradient ring color (80 success / 60 warning / else danger), status text (`Excellent Longevity`/`Good Longevity`/`Needs Improvement`/`High Risk`), and a breakdown panel; (6) handles unsaved-workout checks before navigation.

## B. Public surface
- Recommendations builder (lines 8–158) — `severityOrder = {High:3, Medium:2, Low:1}`; sorts `risks` descending; appends `Try: ${exerciseNames}` for each risk with exercises; prepends `⚠️ URGENT:` for High severity; score-based thresholds (posture 70/85, balance 60/80, jointMobility 50/75, trend 40/75, variety 50/80); goal-based advice; reassessment reminder; dedup via `Set`.
- `updateLongevityScoreDisplay()` — reads `calculateLongevityScore()`; updates `#longevityScore`, `#longevityCircleScore`; sets conic-gradient `conic-gradient(${color} 0% ${score}%, var(--gray-300) ${score}% 100%)`; sets status text/color (80/60/40 thresholds); injects `#longevityBreakdown` panel if missing.
- Risk rendering — appends risk rows with severity badges and `recommendation` text.
- Unsaved-workout guards — checks `currentWorkout` and prompts before navigation away.

## C. Dependencies
- Globals: `document`, `workoutData`, `ultimateExerciseLibrary`, `workoutProgram`.
- Cross-module: `calculateLongevityScore` (17), `assessAgingRisks`, `currentWorkout`, `Swal`.

## D. Native-equivalent mapping
- Recommendations builder → `engine/Stats.kt::longevityBaseRecommendations` (L1497, private) — ported with identical severity-ordering (`High:3, Medium:2, Low:1`), posture/balance/jointMobility threshold bands, trend/variety thresholds, goal-based advice, reassessment reminder.
- Risk-based `URGENT:` prefix → `Stats.assessAgingRisks` + `RiskDef` table (L1432) — ported.
- `updateLongevityScoreDisplay` → `ui/screens/Dashboard.kt::DashboardScreen` (L37) + `Stats.ringsData` (L528) which exposes `RingData("lon", "Longevity", lon.toDouble(), 100.0, "%", ringColor(…), "progress")`. Native uses Compose `Canvas`-based rings instead of CSS conic-gradient.
- Status text bands (80 Excellent / 60 Good / 40 Needs Improvement / else High Risk) → `Stats.LongevityScore.status` field + `Stats.ringColor(pct)` (L513) — ported.
- Breakdown panel → `Dashboard.kt` Compose card showing per-subscore bars — ported.
- Unsaved-workout guards → `MainActivity.kt::AppShell` back-handler + `ProState.hasDraft` — partially ported (Compose `BackHandler`).

## E. Patterns worth porting
- **Severity-ordered risk recommendations with `URGENT:` prefix for High** — small UX cue that draws attention without overwhelming.
- **Score-based recommendation bands** (posture 70/85, balance 60/80, jointMobility 50/75, trend 40/75, variety 50/80) — these thresholds are calibration choices worth preserving explicitly; native mirrors them.
- **4–6 week reassessment reminder** appended to all recommendation lists — nice meta-coaching cue.
- **`Set`-based dedup** (`return [...new Set(recommendations)]`) — cheap guard against duplicate text strings when multiple risks share the same `baseRecommendation`.
- **Conic-gradient ring with 80/60/40 color thresholds** — Compose can replicate with `SweepGradient`; the threshold table should be centralized.
- **Unsaved-workout guards before navigation** — `BackHandler` should call a SweetAlert-equivalent confirm if `currentWorkout.exercises.any { it.actual == null && !it.skipped }`.

## F. Feature IDs
`P18-RISK-RECOMMENDATIONS`, `P18-SCORE-BASED-RECOMMENDATIONS`, `P18-GOAL-BASED-ADVICE`, `P18-REASSESSMENT-REMINDER`, `P18-LONGEVITY-SCORE-DISPLAY`, `P18-CONIC-GRADIENT-RING`, `P18-STATUS-BANDS`, `P18-UNSAVED-WORKOUT-GUARD`.
