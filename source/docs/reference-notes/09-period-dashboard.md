# 09 — Period Tracking, Smart Message Bank, Dashboard, Exercise Search

> Reference module: `reference/scripts/app/09-period-dashboard.js` (931 lines).
> Native counterparts: `core/MessageBank.kt`, `ui/screens/MyCycle.kt`, `ui/screens/Dashboard.kt`, `ui/screens/Library.kt`.

This module owns the **Smart Message Bank** (150+ cycle-aware coaching messages with no-repeat logic), period tracking UI/persistence, and dashboard rendering hooks.

---

## E2. Deep patterns

### 2.1 `SmartMessageBank` schema
- **Inputs**: hard-coded `messages[]` array.
- **Computation**: each message has `{text, phases[], tone, goalWeight?, minSeverity, maxSeverity, requiresSarcasm?}`. Phases: `'menstrual'`, `'follicular'`, `'ovulatory'`, `'luteal'`, `'any'`. Tones: `'gentle'`, `'witty'`, `'motivational'`, `'nature'`, `'competitive'`, `'queen'`, `'sarcastic'`, `'fun'`. Severity 0-10. Sarcasm requires opt-in.
- **Output**: structured message bank.
- **Reasoning**: phase × severity × tone matrix lets the coach say the right thing at the right time (gentle in menstrual, queen in ovulatory).

### 2.2 `SmartMessageBank.selectMessage(phase, severity, goal, sarcasmEnabled)`
- **Inputs**: current phase, severity (0-10), goal string, sarcasm toggle.
- **Computation** (7-step):
  1. `loadHistory()` — pull `usedMessageIndices[]` and `lastTone` from localStorage.
  2. **Filter candidates**: phase match (or 'any'); severity ∈ `[minSeverity, maxSeverity]`; if `requiresSarcasm && !sarcasmEnabled` → skip.
  3. If candidates empty → return `"✨ Your body is amazing. Keep listening to it."` (universal fallback).
  4. Map candidates with original index.
  5. **Split used vs unused**: `unused = candidates - usedMessageIndices`; `used = candidates ∩ usedMessageIndices`.
  6. **Pick from unused** with tone-diversity: if `lastTone` set, filter unused to `c.tone !== lastTone`; if non-empty → use that subset; pick random.
  7. If all unused exhausted → reset `usedMessageIndices = []`, pick random from candidates.
  8. Push selected idx to `usedMessageIndices`, set `lastTone`, save to localStorage.
- **Output**: message text string.
- **Reasoning**: never repeats a message until all matching candidates are exhausted; tone diversity avoids back-to-back sarcastic messages.

### 2.3 `loadHistory()` / `saveHistory()`
- **Inputs**: localStorage `'smartMessageHistory'`.
- **Computation**: parse `{used: [], lastTone: null}`; try/catch swallows parse errors. Save serialises both fields.
- **Output**: hydrated `usedMessageIndices` and `lastTone`.
- **Reasoning**: persists across sessions so users don't see the same message twice in a row.

### 2.4 `savePeriodData()` — period input persistence
- **Inputs**: DOM inputs `lastPeriodDate`, `cycleLength`, symptoms checklist.
- **Computation**: parse date and cycle length (validate 20-40 days); store on `workoutData.user.menstrual`; save to localStorage; refresh dashboard; recompute phase multiplier via `updateUserCache`.
- **Output**: persisted menstrual data.
- **Reasoning**: cycle phase drives prescription (module 06 phase multiplier), so the input must be saved immediately.

### 2.5 Dashboard rendering hooks (`updateDashboard`)
- **Inputs**: `workoutData`, `currentWorkout`, `calculateSystemicRecoveryFactor()`.
- **Computation**: render stat cards (streak, total volume, avg RPE, last workout), recovery ring, today's workout preview, longevity score, smart message banner. Each card has a tap target → navigates to detail screen.
- **Output**: DOM updates.
- **Reasoning**: dashboard is the home screen; everything must render in <100ms.

### 2.6 Exercise search (`renderLibrary` + fuzzy index)
- **Inputs**: `ultimateExerciseLibrary`, search query.
- **Computation**: build a Fuse.js index on exercise names + muscle groups + equipment; on input → debounce 200ms → query Fuse → render filtered list grouped by muscle.
- **Output**: filtered library list.
- **Reasoning**: fuzzy search tolerates typos ("sqaut" → squat); debounce prevents jank.

### 2.7 Phase message selection trigger
- **Inputs**: `getCurrentCyclePhase()`, dashboard render.
- **Computation**: on dashboard load → compute phase → `selectMessage(phase, severity=5, goal, sarcasmEnabled)` → display in banner.
- **Output**: dynamic coaching message.
- **Reasoning**: every dashboard visit shows a fresh, relevant message.

### 2.8 Severity calculation
- **Inputs**: phase, recent workout RPE, missed workouts.
- **Computation**: `severity = basePhaseSeverity + rpeAdjustment + missedAdjustment`; e.g. menstrual phase base = 5, +2 if last RPE > 8, +1 if missed ≥2 workouts.
- **Output**: 0-10 severity used by `selectMessage`.
- **Reasoning**: high-severity messages are more gentle/supportive.

### 2.9 Sarcasm toggle
- **Inputs**: user setting.
- **Computation**: `sarcasmEnabled` gates `requiresSarcasm: true` messages.
- **Output**: filter flag.
- **Reasoning**: some users love sarcastic coaching; others hate it. Opt-in respects preference.

### 2.10 Dashboard stat card pipeline
- **Inputs**: workout history.
- **Computation**: 4 cards — (1) current streak (consecutive days with completed workouts), (2) total volume last 7 days (sum of `weight * reps`), (3) avg RPE last 7 days, (4) last workout date. Each card has a tap target.
- **Output**: 4 stat cards.
- **Reasoning**: at-a-glance training summary.

### 2.11 Longevity score display hook
- **Inputs**: `calculateLongevityScore()` (module 17).
- **Computation**: render circular score with conic gradient; color depends on score (≥80 green, ≥60 yellow, else red).
- **Output**: dashboard longevity widget.
- **Reasoning**: longevity score is a headline metric — front-and-center.

---

## E3. Edge cases handled

- **localStorage 'smartMessageHistory' missing**: `loadHistory` swallows error; `usedMessageIndices = []`, `lastTone = null`.
- **All candidates used (cycle exhausted)**: resets `usedMessageIndices = []`, picks random.
- **No matching candidates** (e.g. severity out of range): universal fallback message.
- **`sarcasmEnabled = false`**: filters out `requiresSarcasm: true` messages.
- **`lastTone` matches all unused**: falls back to all unused (skip tone filter).
- **Phase is null** (male or no period): messages with phase `'any'` still match.
- **Invalid cycle length (<20 or >40)**: validation fails, alert shown, not saved.
- **No `lastPeriodDate`**: skips save, no error.
- **Search query empty**: shows full library.
- **Search query <2 chars**: skips Fuse query, shows full library (perf).
- **No workouts in history**: streak = 0, volume = 0, RPE = 0, last workout = "—".
- **Dashboard element missing**: `if (statEl)` guards prevent crashes.
- **`getCurrentCyclePhase()` returns null**: phase message skipped.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| Message count | 150+ | Enough variety to avoid repetition for ~3 months |
| Phase coverage | 5 buckets (menstrual/follicular/ovulatory/luteal/any) | Aligns with cycle physiology |
| Tone diversity | 8 tones | Prevents monotony |
| Severity buckets | 0-10 | Fine-grained matching |
| Sarcasm default | OFF (opt-in) | Respectful default |
| Message history persistence | localStorage | Survives refresh |
| Tone repeat avoidance | Filter unused first | No back-to-back same tone |
| Search debounce | 200 ms | Balances responsiveness and perf |
| Fuse.js threshold | 0.4 | Tolerates 2 typos |
| Stat card count | 4 | At-a-glance without scrolling |
| Longevity color thresholds | 80/60/40 | Traffic-light convention |
| Cycle length validation | 20-40 days | Population range (outside = medical concern) |
| Fallback message | "✨ Your body is amazing. Keep listening to it." | Universal, positive |
| Dashboard render budget | ~100 ms | Smooth UX |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `SmartMessageBank` 150+ message schema | PARTIAL (`MessageBank.kt` has ~30 messages) | Library is 20% the size |
| `selectMessage` no-repeat + tone-diversity | FULL (`MessageBank.kt`) | — |
| `loadHistory`/`saveHistory` persistence | FULL | — |
| `savePeriodData` cycle input | FULL (`MyCycle.kt`) | — |
| Cycle length validation (20-40) | FULL | — |
| Dashboard stat cards (4) | FULL (`Dashboard.kt`) | — |
| Exercise search (Fuse fuzzy) | PARTIAL | Native uses simple `contains` filter; no fuzzy |
| Search debounce (200 ms) | FULL | — |
| Phase message trigger on dashboard load | FULL | — |
| Severity calculation | PARTIAL | Computed but simpler — no missed-workout adjustment |
| Sarcasm toggle | FULL | — |
| Longevity score display | FULL (`Dashboard.kt`) | — |
| Streak calculation | FULL (`Stats.kt`) | — |
| Total volume last 7 days | FULL | — |
| Avg RPE last 7 days | FULL | — |
| Universal fallback message | FULL | — |

**Coverage: 81%** (12 FULL / 3 PARTIAL / 0 MISSING of 16 behaviours)

---

## H. Top 3 gaps to close

1. **Message bank is ~20% the size of reference** — only ~30 messages vs 150+. Expand `MessageBank.kt` to at least 100 messages covering all 5 phases × 8 tones × 3 severity buckets.
2. **No fuzzy search** — `Library.kt` uses `contains` which fails on typos. Add a Levenshtein-based fuzzy matcher (or port Fuse.js to Kotlin) with threshold 0.4.
3. **Severity calculation is simpler** — native doesn't factor in missed workouts. Add `+1 severity per missed workout in last 7 days` so the message bank can be more supportive when users fall off.
