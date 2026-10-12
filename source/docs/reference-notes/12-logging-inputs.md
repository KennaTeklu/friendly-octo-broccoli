# 12-logging-inputs — RPE slider, performance logging toggle, set inputs, modes, notes

Source: `reference/scripts/app/12-logging-inputs.js` (L64706–67073 of original `app.js`; 2,368 lines)

## A. File purpose
This module builds the in-session logging UI: a 1-10 RPE checkbox-slider with color interpolation (light-green → yellow → orange → dark-red), an "all-up-to-N" selection model, responsive layout (min 44px tap targets shrinking to 38px under 500px), the performance-logging toggle with viewport-aware auto-scroll after the 0.4s CSS transition, and the inputs/modals/notes glue used by exercise cards.

## B. Public surface
- `createRPESlider(index, setIndex, initialValue=null)` — injects CSS (`.rpe-slider-container`, `.rpe-checkbox` with `!important` overrides) once; returns a DOM container with 10 checkboxes + labels + hidden input. Each label's `backgroundColor` is computed via `getRpeColor(rpe)`.
- `getRpeColor(rpe)` — interpolates across 4 stops (`1: lightgreen 144/238/144`, `4: yellow 255/255/102`, `7: orange 255/140/0`, `10: darkred 139/0/0`) → `rgb(r,g,b)`.
- Slider select-all-up-to-N logic — clicking value N checks boxes 1..N and writes hidden input.
- `togglePerformanceLogging(index)` — toggles `#performance_${index}.active`, swaps chevron, then 400ms later auto-scrolls to bring logger into view (handles taller-than-viewport case).
- Responsive CSS — `@media (max-width:500px)` shrinks min-width from 44 to 38px and padding 10→6px, font 0.9→0.8rem.

## C. Dependencies
- Globals: `document`, `window`, `getTopOffset` (10).
- Cross-module: consumed by `11-exercise-card.js` card builder; feeds `actual.rpe[]` array persisted by `13-persistence-coach.js`.

## D. Native-equivalent mapping
- `createRPESlider` → `ui/screens/WorkoutLogger.kt::ProTextField` (L368) + custom RPE selector in `LoggerPanel` (L41). Native uses a segmented `Slider`/`SegmentedButton` row rather than checkboxes.
- `getRpeColor` color interpolation → **GAP**. Native `WorkoutLogger.kt` uses fixed color buckets; the smooth 4-stop interpolation is missing.
- Slider select-all-up-to-N model → **GAP**. Native shows a single RPE number; the "fill from 1 to N" visual is a nicer UX cue.
- `togglePerformanceLogging` viewport-aware auto-scroll → `WorkoutLogger.kt` Compose `LazyColumn` auto-scroll via `rememberLazyListState` — partially ported; the "if taller than viewport, show top; else align bottom" logic isn't applied.
- Responsive tap-target sizing (44px → 38px) → `ui/Accessibility.kt` (probably) — should verify min-touch-target compliance.
- `actual.rpe[]` storage → `Model.kt::ActualPerformance` (L266) `rpe: List<Double>?` — ported.

## E. Patterns worth porting
- **RPE color interpolation across 4 stops** (`lightgreen → yellow → orange → darkred`) — visually communicates effort at a glance. Native uses fixed buckets; the gradient is more intuitive.
- **Select-all-up-to-N checkbox pattern** — clicking RPE 7 visually fills 1-7, making the "I went to failure-ish" intent obvious. Cheap UX win.
- **`!important` CSS overrides injected once** — defensive against theme color leaks; the equivalent on Compose is `Modifier.semantics` and `LocalContentColor` overrides.
- **Auto-scroll after CSS transition completes** — Compose `LaunchedEffect` can replicate by waiting `animationSpec` duration before `animateScrollToItem`.
- **44px min tap target (38px on small screens)** — Material Design baseline; native should enforce via `Modifier.minimumInteractiveComponentSize()` or `Modifier.size(44.dp)`.

## F. Feature IDs
`P12-RPE-CHECKBOX-SLIDER`, `P12-RPE-COLOR-INTERPOLATION`, `P12-SELECT-ALL-UP-TO-N`, `P12-PERFORMANCE-LOGGING-TOGGLE`, `P12-VIEWPORT-AWARE-SCROLL`, `P12-RESPONSIVE-TAP-TARGETS`.
