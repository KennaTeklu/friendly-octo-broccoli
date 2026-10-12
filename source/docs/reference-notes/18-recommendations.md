# 18 — Recommendations, Breakdown Panel, Unsaved Workout Checks

> Reference module: `reference/scripts/app/18-recommendations.js` (5 241 lines).
> Native counterparts: `core/CoachingEngine.kt`, `core/ProgressSummary.kt`, `ui/screens/Dashboard.kt`, `ui/Dialogs.kt`.

This module generates prioritised recommendations from aging risks + sub-scores + goal alignment, renders the breakdown panel, and handles unsaved-workout guard checks before navigation.

---

## E2. Deep patterns

### 2.1 Risk-based recommendations (prioritised)
- **Inputs**: `risks[]` (from module 17), `severityOrder = {High:3, Medium:2, Low:1}`.
- **Computation** (5-stage):
  1. `sortedRisks = [...risks].sort((a,b) => severityOrder[b.severity] - severityOrder[a.severity])` — High first.
  2. For each risk: build recommendation text from `risk.recommendation`.
  3. If `risk.exercises.length > 0` → append "Try: ${exercise names joined}".
  4. If `risk.severity === 'High'` → prefix "⚠️ URGENT: ".
  5. Push to `recommendations[]`.
- **Output**: prioritised recommendation strings.
- **Reasoning**: high-severity risks surface first; urgent marker draws attention; exercise suggestions are actionable.

### 2.2 Exercise name resolution for recommendations
- **Inputs**: `risk.exercises[]` (ids).
- **Computation**: for each id → `ex = ultimateExerciseLibrary[id] || workoutProgram.exercises[id]`; if found → `ex.name`; else `id.replace(/_/g, ' ')` (humanized id).
- **Output**: human-readable exercise names.
- **Reasoning**: falls back to humanized id when exercise not in library (e.g. custom exercises).

### 2.3 Score-based general recommendations
- **Inputs**: `scores` object (posture, balance, jointMobility, trend, variety).
- **Computation** (5-tier per score):
  - **Posture**: `≥70 && < 85` → "good, maintain with regular upper back work"; `≥85` → "excellent, keep up".
  - **Balance**: `≥60 && < 80` → "decent, aim for 2-3 balance sessions/week"; `≥80` → "great, continue".
  - **Joint mobility**: `≥50 && < 75` → "acceptable, add one mobility session/week"; `≥75` → "excellent".
  - **Trend**: `< 40` → "declining, consider structured progressive overload"; `> 75` → "strong progress, keep challenging".
  - **Variety**: `< 50` → "missing many longevity muscles, include wider variety"; `≥ 80` → "excellent variety".
- **Output**: recommendation strings.
- **Reasoning**: tiered advice — don't nag users who are already doing well.

### 2.4 Goal-based advice
- **Inputs**: `workoutData.user.goal`.
- **Computation**: if `goal === 'longevity'` → "Your goal is longevity. Keep focusing on joint health, balance, and consistency."
- **Output**: goal-aligned recommendation.
- **Reasoning**: reinforces the user's stated objective.

### 2.5 Reassessment reminder
- **Inputs**: `recommendations.length`.
- **Computation**: if `recommendations.length > 0` → push "Reassess your longevity score in 4-6 weeks to track progress."
- **Output**: reminder string.
- **Reasoning**: longevity score changes slowly; 4-6 weeks is the meaningful reassessment window.

### 2.6 Deduplication
- **Inputs**: `recommendations[]`.
- **Computation**: `return [...new Set(recommendations)]`.
- **Output**: deduplicated list.
- **Reasoning**: prevents duplicate strings from risk + score pathways.

### 2.7 `updateLongevityScoreDisplay()` — UI render
- **Inputs**: `calculateLongevityScore()` result.
- **Computation**:
  1. Update `#longevityScore` text.
  2. Update `#longevityCircleScore` text.
  3. Update `.score-circle` conic gradient: `${color} 0% ${score}%, var(--gray-300) ${score}% 100%` where color = green ≥80, yellow ≥60, red <60.
  4. Update `#longevityStatus` text + color (4-tier).
  5. Build breakdown panel: for each key score (grip, balance, jointMobility, posture, muscleBalance, consistency) → render chip with color and label.
- **Output**: full longevity UI.
- **Reasoning**: circular progress is the headline; chips show breakdown.

### 2.8 Color thresholds (UI)
- **Inputs**: score value.
- **Computation**: `≥70 → green; ≥50 → yellow; <50 → red` (for chips); `≥80 → green; ≥60 → yellow; <60 → red` (for circle/status).
- **Output**: CSS color var.
- **Reasoning**: chips use 70/50 (stricter — sub-scores need higher bar); circle uses 80/60 (overall score).

### 2.9 Unsaved-workout guard (before navigation)
- **Inputs**: `currentWorkout.exercises` with `actual` flags.
- **Computation**: before allowing navigation away from workout section:
  1. Check if any exercise has `actual` logged but workout not completed.
  2. If yes → SweetAlert confirm "You have unsaved progress. Leave anyway?".
  3. On confirm → navigate; on cancel → stay.
- **Output**: navigation gated or allowed.
- **Reasoning**: prevents accidental loss of in-progress logging.

### 2.10 Recommendation source priority
- **Inputs**: risk recommendations, score recommendations, goal recommendation, reassessment.
- **Computation**: order: (1) urgent risks (High severity), (2) non-urgent risks, (3) score-based, (4) goal-based, (5) reassessment reminder.
- **Output**: ordered recommendation list.
- **Reasoning**: urgency first, then maintenance, then reminder.

### 2.11 Conic gradient computation
- **Inputs**: score (0-100), color string.
- **Computation**: `circle.style.background = conic-gradient(${color} 0% ${score}%, var(--gray-300) ${score}% 100%)`.
- **Output**: CSS background string.
- **Reasoning**: conic gradient draws a pie-chart-like progress ring.

---

## E3. Edge cases handled

- **No risks**: `sortedRisks = []`; loop doesn't execute; recommendations start from score-based.
- **Risk with no `exercises` array**: skips the "Try:" append.
- **Risk severity not in `severityOrder`**: `severityOrder[b.severity] || 0` → sorts last.
- **Exercise id not in library or workoutProgram**: humanized id used.
- **`scores.posture` undefined**: `if (scores.posture >= 70)` is false; skip.
- **`scores.trend` undefined**: `if (scores.trend !== undefined)` guards.
- **`scores.variety` undefined**: same guard.
- **`workoutData.user.goal` not 'longevity'**: skips goal advice.
- **No recommendations generated**: reassessment reminder skipped (`length > 0` guard).
- **Duplicate recommendations**: `Set` deduplication.
- **`#longevityScore` element missing**: `if (statEl)` guard.
- **`#longevityCircleScore` missing**: guard.
- **`.score-circle` element missing**: guard.
- **`#longevityStatus` missing**: guard.
- **`.longevity-score-card` missing**: breakdown panel not appended.
- **`breakdown[key]` undefined**: `if (breakdown[key] !== undefined)` guard.
- **Score = 0**: conic gradient shows 0% color, 100% gray — valid.
- **Score = 100**: conic gradient shows 100% color, 0% gray — valid.
- **Unsaved-workout guard with no `currentWorkout`**: skip guard.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Severity order | High → Medium → Low | Urgency first |
| Urgent prefix | "⚠️ URGENT: " | Visual attention |
| Exercise suggestion format | "Try: ${names}." | Actionable |
| Posture "good" tier | 70-84 | Below 70 = needs work |
| Posture "excellent" tier | ≥85 | Above 84 = exemplary |
| Balance "decent" tier | 60-79 | Below 60 = deficit |
| Joint mobility "acceptable" tier | 50-74 | Below 50 = concern |
| Trend "declining" threshold | < 40 | Below 40 = regression |
| Trend "strong" threshold | > 75 | Above 75 = notable progress |
| Variety "missing" threshold | < 50 | Below 50 = limited coverage |
| Variety "excellent" threshold | ≥ 80 | Above 80 = broad |
| Reassessment window | 4-6 weeks | Meaningful change timeframe |
| Circle color tiers | 80/60 | Higher bar for overall |
| Chip color tiers | 70/50 | Stricter for sub-scores |
| Status tiers | 4 (80/60/40) | Granular |
| Unsaved-workout confirm | SweetAlert warning | Prevents accidental loss |
| Breakdown chip count | 6 | Scannable |
| Chip label format | humanized camelCase | Readable |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| Risk-based recommendations (prioritised) | FULL (`CoachingEngine.kt`) | — |
| Severity sort (High → Low) | FULL | — |
| "⚠️ URGENT: " prefix for High | FULL | — |
| Exercise name resolution + humanized fallback | FULL | — |
| Score-based recommendations (5-tier) | FULL | — |
| Goal-based advice | FULL | — |
| Reassessment reminder (4-6 weeks) | FULL | — |
| Deduplication via Set | FULL | — |
| `updateLongevityScoreDisplay` UI render | FULL (`Dashboard.kt`) | — |
| Conic gradient computation | FULL | — |
| Color thresholds (circle 80/60, chips 70/50) | FULL | — |
| Status 4-tier mapping | FULL | — |
| Breakdown panel (6 chips) | FULL | — |
| Unsaved-workout guard before navigation | FULL (`Workout.kt` back-handler) | — |
| Recommendation source priority (urgent → risk → score → goal → reminder) | FULL | — |

**Coverage: 100%** (15 FULL / 0 PARTIAL / 0 MISSING of 15 behaviours)

---

## H. Top 3 gaps to close

1. **Recommendations don't link to actionable screens** — text says "Try: face pulls" but tapping doesn't navigate to library search for that exercise. Add tap-to-search on recommendation chips.
2. **Reassessment reminder is static text** — no calendar integration or reminder notification. Schedule a `WorkManager` notification 4 weeks after the last longevity assessment.
3. **Score thresholds (70/50 for chips) not user-tunable** — a powerlifter may care less about balance; a senior may prioritize it more. Allow per-user threshold overrides in advanced settings.
