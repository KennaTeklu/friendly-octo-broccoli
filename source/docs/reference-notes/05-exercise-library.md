# 05-exercise-library — `ultimateExerciseLibrary` data + weekly plan + augmentation

Source: `reference/scripts/app/05-exercise-library.js` (L4254–56313 of original `app.js`; 52K lines)

## A. File purpose
This is the data backbone of the app — a giant inlined `ultimateExerciseLibrary` object keyed by muscle group (quads, chest, back, …), each exercise carrying `name`, `muscles[]`, `equipment`, `defaultSets`, `defaultReps`, `progression`, `instructions[]`, `strengthIndex` (expected 1RM/bodyweight), `skillFactor` (0.5 technical → 1.0 simple), `genderSuitability`, `prescriptionType` (`reps`/`time`/`amrap`), `defaultDuration`, `fitnessComponents[]`, and `primaryMuscle`. It also embeds `workoutProgram.splits[]` (7-day component rotation: full-body power, push hypertrophy, pull hypertrophy, legs/speed, athletic agility, cardio endurance, mobility/flexibility) covering the 11 fitness components.

## B. Public surface
- `ultimateExerciseLibrary` — `{quads:[…], chest:[…], back:[…], …}`. Per-exercise fields documented above.
- `workoutProgram.splits[]` — 7 entries with `{id, name, focus[], restAfter, drills[], component, componentsCovered[]}`.
- `workoutProgram.exercises{}` — empty map populated by `Generator` at runtime.
- Augment hook — sets `primaryMuscle` (first muscle) and `importance` (`core`/`accessory`/`specialised`) on each exercise, used by the rotation weighting in 01-config-data.

## C. Dependencies
- Globals: none at load (pure data).
- Downstream: consumed by `06-library-curves.js::getEnhancedExerciseList`, `07-exercise-generation.js::generateExerciseFromLibrary`, `04-strength-tests.js::getExerciseById`, `18-recommendations.js` for friendly names.

## D. Native-equivalent mapping
- `ultimateExerciseLibrary` → `assets/data/exercises.json` + `engine/Library.kt::LibraryExercise` (Model.kt L392). Loaded by `Library.ensure(ctx)` (L98) / `loadImpl` (L115).
- `workoutProgram.splits[]` → `engine/Generator.kt::SPLITS` constant + `splitById` (L43).
- `primaryMuscle`/`importance` augment → `engine/Library.kt::Library.augmented` (L279) — implemented natively.
- Bodyweight exercises (in 05-extension) → `Model.kt::BodyweightExercise` (L416) + `Library.bwToLibrary` (L210).
- Per-exercise `fitnessComponents[]` mapping to the 11-component system → `Library.componentFor(exerciseId)` (L333) — implemented but reads from `mp_components.json` rather than the exercise record.

## E. Patterns worth porting
- **`prescriptionType: 'reps' | 'time' | 'amrap'`** on each exercise — native `WorkoutExercise`/`Prescription` (Model.kt L257) carries `prescriptionType` but most code paths assume `reps`. Time-based (planks, hangs) and AMRAP (kettlebell swings) flows are weaker natively.
- **7-day split rotation mapping each day to a single skill emphasis** (power / balance / coordination / speed / agility+reaction / cardio / flexibility) is a clean pedagogical structure that the native `Generator.performGenerateWorkout` doesn't follow — it picks splits by simple rotation.
- **`strengthIndex` field** (expected 1RM as fraction of bodyweight) — already present in `assets/exercise_params.json`, but the native `LibraryExercise` doesn't surface it as a top-level field; it would help first-time weight estimation.
- **`skillFactor` for difficulty classification** (`<0.5` hard, `<0.7` medium, else easy) — `Library.augmented` doesn't currently compute a difficulty badge; useful for filtering in `MuscleWiki.kt`.

## F. Feature IDs
`P05-ULTIMATE-LIBRARY`, `P05-SPLITS-7DAY-ROTATION`, `P05-PRESCRIPTION-TYPES`, `P05-STRENGTH-INDEX`, `P05-SKILL-FACTOR`, `P05-COMPONENTS-COVERED`.
