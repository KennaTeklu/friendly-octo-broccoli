# 12 — RPE Slider, Logging, Inputs, Modes, Notes, Sticky Buttons

> Reference module: `reference/scripts/app/12-logging-inputs.js` (2 375 lines).
> Native counterparts: `ui/screens/WorkoutLogger.kt`, `ui/screens/Workout.kt`, `core/Exporter.kt`.

This module implements the **RPE checkbox-slider** (1-10 with color interpolation), the per-set logging inputs, performance-logging toggle, sticky action buttons, and notes textarea with debounce-save.

---

## E2. Deep patterns

### 2.1 `createRPESlider(index, setIndex, initialValue)` — checkbox-style RPE
- **Inputs**: exercise index, set index, optional initial value.
- **Computation**:
  1. **CSS injection** (once): inject `#rpe-slider-styles` `<style>` block with flex layout, 44-60px checkbox width, 12px border-radius labels, color transition 0.1s, scale 1.02 on checked, white box-shadow ring on checked.
  2. **Container**: `<div class="rpe-slider-container">`.
  3. **Hidden input**: `<input type="hidden" id="rpe_${index}_${setIndex}">` with empty initial value.
  4. **Color function** `getRpeColor(rpe)` — 4-stop linear interpolation:
     - RPE 1: `rgb(144, 238, 144)` (light green)
     - RPE 4: `rgb(255, 255, 102)` (yellow)
     - RPE 7: `rgb(255, 140, 0)` (orange)
     - RPE 10: `rgb(139, 0, 0)` (dark red)
     - Compute `t = (rpe - lower.rpe) / (upper.rpe - lower.rpe)`; lerp each RGB channel; round.
  5. **10 checkboxes**: for rpe 1-10, create `<input type="checkbox">` + `<label>` with `backgroundColor = getRpeColor(rpe)`.
  6. **Slider logic**: on change → `selected = parseInt(value)`; for i=1..10 → `other.checked = (i ≤ selected)`; update hidden input; dispatch change event.
- **Output**: DOM container with 10 colored checkboxes + hidden input.
- **Reasoning**: visual slider (select all up to clicked value) + color gradient (green→red) gives instant RPE feedback; checkboxes are accessible (keyboard-navigable).

### 2.2 `getRpeColor(rpe)` — 4-stop color interpolation
- **Inputs**: integer 1-10.
- **Computation**: 4 anchor stops at RPE 1/4/7/10; find bracketing stops; `t = (rpe - lower) / (upper - lower)`; `r = round(lower.r + (upper.r - lower.r) * t)`; same for g, b; return `rgb(r, g, b)`.
- **Output**: CSS color string.
- **Reasoning**: green at low RPE (easy), yellow at moderate, orange at hard, dark red at max — universal traffic-light convention.

### 2.3 `togglePerformanceLogging(index)` — collapsible panel
- **Inputs**: exercise index.
- **Computation**:
  1. Toggle `.active` class on `#performance_${index}` element.
  2. Find header chevron icon.
  3. If active → chevron `fa-chevron-up`; wait 400ms (CSS transition) → scroll into view.
  4. Else → chevron `fa-chevron-down`.
- **Output**: expanded/collapsed panel.
- **Reasoning**: keeps the card compact by default; power users expand for detailed logging.

### 2.4 Per-set logging inputs
- **Inputs**: prescribed sets, weight stepper, reps inputs.
- **Computation**: for each set 1..N → render weight stepper (±2.5 lbs), reps input (number), RPE slider, "Log Set" button. Sticky behavior: once a set is logged, the next set's inputs auto-focus.
- **Output**: per-set input row.
- **Reasoning**: steppers prevent typos; auto-focus speeds up sequential logging.

### 2.5 Weight stepper
- **Inputs**: current weight, increment (2.5 lbs default).
- **Computation**: `+` button → `weight += 2.5`; `-` button → `weight = max(0, weight - 2.5)`; clamp to bar minimum.
- **Output**: updated weight.
- **Reasoning**: 2.5 lb microplates are the smallest practical increment.

### 2.6 Notes textarea with debounce-save
- **Inputs**: textarea content.
- **Computation**: on `input` → clear previous timeout, set new 300ms timeout → `saveNotesInline(workoutId, value)`. On `blur` → clear timeout, save immediately. On `Enter` (without Shift) → prevent default, blur (triggers save).
- **Output**: saved notes.
- **Reasoning**: 300ms debounce prevents save spam; blur + Enter cover edge cases.

### 2.7 Sticky action buttons
- **Inputs**: scroll position, action button container.
- **Computation**: when exercise card scrolls past viewport top → button container becomes `position: sticky; top: 84px`; remains visible while card in view.
- **Output**: always-accessible action buttons.
- **Reasoning**: long workouts shouldn't require scrolling to log each set.

### 2.8 Set completion markers
- **Inputs**: per-set actual data.
- **Computation**: if `actual.reps[i]` exists → marker `✓`; else `⏳`.
- **Output**: visual progress.
- **Reasoning**: at-a-glance set status.

### 2.9 AMRAP set handling
- **Inputs**: `prescriptionType === 'amrap'`.
- **Computation**: reps input becomes free-text number (no prescribed target); RPE slider still applies.
- **Output**: AMRAP-friendly input.
- **Reasoning**: AMRAP sets have no rep target — user goes to failure.

### 2.10 Mark-failed set flow
- **Inputs**: failed set flag.
- **Computation**: on "Mark Failed" → set `actual.failure = true`, `actual.attemptedWeight = weight`; reps recorded as 0; RPE = 10 (max).
- **Output**: failed set logged.
- **Reasoning**: failed sets inform the Kalman update (1RM estimate adjusts down).

### 2.11 First-RPE capture
- **Inputs**: first set's RPE.
- **Computation**: `actual.firstRPE = first set's RPE` (separate from per-set RPE array).
- **Output**: firstRPE field on actual.
- **Reasoning**: first-set RPE is the most predictive of true 1RM (fatigue hasn't accumulated); used by `Stats.kt` for trend analysis.

---

## E3. Edge cases handled

- **CSS already injected**: `if (!document.getElementById('rpe-slider-styles'))` guard.
- **RPE 0 or undefined**: hidden input value stays empty; no checkbox checked.
- **RPE > 10**: not possible (only 10 checkboxes).
- **RPE < 1**: not possible.
- **`getRpeColor` with rpe outside [1,10]**: clamped to first/last stop.
- **`getRpeColor` with `upper.rpe === lower.rpe`**: divide by zero → `t = NaN` → would produce NaN color; in practice never happens since stops are distinct.
- **Weight stepper below bar minimum**: clamped to 45 (barbell) / 5 (dumbbell).
- **Reps input empty**: blocks "Log Set" with validation error.
- **Reps input non-numeric**: blocks with validation error.
- **Notes textarea empty after trim**: saves empty string.
- **`saveNotesInline` called with missing workout**: returns early.
- **Performance panel header missing**: `if (!header) return`.
- **Chevron icon missing**: `if (!icon) return`.
- **AMRAP set with no reps entered**: blocks (must enter something).
- **Failed set without attempted weight**: blocks (must enter attempted weight).
- **CSS transition not finished when scrolling**: 400ms timeout ensures transition completes first.

---

## E4. UX decisions baked in

| Decision | Value | Reason |
|---|---|---|
| RPE range | 1-10 | Standard RPE scale |
| RPE color stops | 1/4/7/10 | Traffic-light gradient |
| RPE color at 1 | Light green (144,238,144) | Visually easy |
| RPE color at 10 | Dark red (139,0,0) | Visually alarming |
| Checkbox min width | 44 px | Touch target (Apple HIG) |
| Checkbox max width | 60 px | Prevents overflow on mobile |
| Label border-radius | 12 px | Modern, soft |
| Label font-size | 0.9 rem (0.8 on mobile) | Readable |
| Color transition | 0.1s ease | Snappy |
| Checked scale | 1.02 | Subtle confirmation |
| Checked ring | 2px white + 4px primary | High-contrast selection |
| Weight stepper increment | 2.5 lbs | Smallest practical microplate |
| Notes debounce | 300 ms | Typical typing pause |
| Notes save on Enter (no Shift) | Yes | Quick save without mouse |
| Notes save on blur | Yes | Catches missed debounces |
| Performance panel transition | 0.4s CSS | Smooth expand |
| Scroll-to-view delay | 400 ms | Matches transition |
| Sticky button top | 84 px | Below navbar |
| Set markers | ✓ / ⏳ | Universal icons |
| First-RPE capture | Separate field | Most predictive of true 1RM |

---

## G. Audit score

| Behavior in reference | Native coverage | Gap |
|---|---|---|
| `createRPESlider` checkbox-slider | FULL (`WorkoutLogger.kt` RPE chips 1-10) | — |
| `getRpeColor` 4-stop interpolation | PARTIAL | Native uses static color tiers (green/y/orange/red) not interpolated |
| `togglePerformanceLogging` collapsible | FULL (Compose expandable card) | — |
| Per-set logging inputs | FULL | — |
| Weight stepper (±2.5) | FULL | — |
| Notes textarea debounce-save (300ms) | FULL | — |
| Notes save on Enter/blur | FULL | — |
| Sticky action buttons | PARTIAL | Compose uses LazyColumn; sticky header behavior differs |
| Set completion markers (✓/⏳) | FULL | — |
| AMRAP set handling | FULL | — |
| Mark-failed set flow | FULL (`WorkoutLogger.kt` failure flag) | — |
| First-RPE capture | FULL | — |
| RPE color interpolation (smooth gradient) | MISSING | Native has 4 discrete tiers, not interpolated |
| Mobile breakpoint (1100px) | N/A (Compose adaptive) | — |

**Coverage: 77%** (10 FULL / 2 PARTIAL / 1 MISSING of 13 applicable behaviours)

---

## H. Top 3 gaps to close

1. **RPE color is not interpolated** — native uses 4 discrete color tiers (green for 1-3, yellow for 4-6, orange for 7-9, red for 10). Implement smooth RGB interpolation across the 1-10 range so the gradient feels continuous.
2. **Sticky buttons behave differently** — Compose's `LazyColumn` sticky header doesn't pin action buttons the same way the reference does. Add a `BottomBar` slot to the workout screen that pins "Log Set" / "Skip" / "Complete" buttons above the keyboard.
3. **Performance panel not collapsible by default** — native shows all per-set inputs expanded. Default to collapsed with a "Show details" toggle to reduce visual noise for casual users.
