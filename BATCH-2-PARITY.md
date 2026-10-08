# PARITY.md — gap audit (legacy HTML app → native Pro), vCode 13

This is our own audit, built by scanning the entire legacy single-file app (`cautious-enigma/index (24).html`, 50k+ lines) and the native Kotlin source. Method: BATCH-2.md line anchors were used to locate each feature in the HTML; the corresponding Kotlin composable was then verified to exist, render real data, and pass the screenshot battery. Status legend: **shipped** (in this APK, screenshot or test referenced) · **partial** (engine/data shipped, some UI outstanding) · **not yet** · **not porting** (harmful/dead/WebView-only by agreement).

## v1.4.3 delta — Batch 2 (Workout + Progress) additions

| Feature | Status | Notes |
|---|---|---|
| Workout: "How to do" + "Images" browser buttons on each exercise card | shipped | `Workout.kt` openExerciseBrowser — opens `google.com/search?q=<ex> how to do` and `google.com/search?tbm=isch&q=<ex>` |
| Workout: "Show Instructions" toggle expanding numbered step list | shipped | `Workout.kt` — `exercise.instructions.forEachIndexed` renders numbered steps |
| Workout: Superset / Drop set / Warmup toggle chips per card | shipped | `Workout.kt` PlateToggleChip × 3 (transient state, not persisted — matches HTML) |
| Workout: Plate math (45 + 25 + 10 per side for 205 lbs) | shipped | `Workout.kt` plateMath() — 45-lb bar, denominations 45/35/25/10/5/2 |
| Workout: Resume action (re-opens logger for completed/skipped exercise) | shipped | `Workout.kt` onResume — clears `actual`/`skipped`, re-opens card |
| Workout: Replace action with confirmation dialog | shipped | `Workout.kt` confirmReplace SwalDialog → removeExerciseFromWorkout |
| Workout: Regenerate buttons (top + bottom, same flow, with confirm) | shipped | `Workout.kt` confirmRegenerate → Generator.performGenerateWorkout |
| Progress: forecastTotal (sum of S/B/D est1RM) + forecastWilks (BW-normalized) | shipped | `Progress.kt` StrengthForecastCard — two stat boxes above per-lift rows |
| Progress: Volume by muscle (last 7 days) | shipped | `Progress.kt` VolumeByMuscle7d — top 10 muscles by volume |
| Progress: Volume trend line chart (canvas volumeChart) | shipped | `Progress.kt` VolumeChart — Canvas path + fill, last 20 sessions |
| Progress: 5-year Monte Carlo forecast | shipped | `Progress.kt` FiveYearMonteCarlo — 1000 scenarios × 260 weeks, Box-Muller noise |
| Progress: Per-muscle sparklines (last 4 weeks) | shipped | `Progress.kt` MuscleSparklines — 8 muscles × mini Canvas paths |
| Hotfix 1.1 Fix 1: Library LazyColumn + key + contentType (ANR fix) | shipped | `Library.kt` — converted from Column(verticalScroll)+forEach to LazyColumn |
| Hotfix 1.1 Fix 2: Removed all in-app 3D visuals | shipped | No RigArt.kt in source; no Canvas in Library cards; browser intents only |

## 4. Workout & logging (full audit — carried forward + Batch 2 additions)

| Legacy feature | Status | Notes |
|---|---|---|
| Generation engine (splits, rotation, deload, taper, roulette, athletic blocks) | shipped | vCode 10 verbatim formulas |
| Time wizard (15/25/40/60/90/All + trimmer + Continue/Replace) | shipped | vCode 10 |
| Logger (stepper, per-set reps, RPE chips, RIR, failure, notes) | shipped | vCode 10 LoggerPanel |
| 1RM test panel + failure path + re-test reminder | shipped | vCode 10 OneRmTestPanel |
| Rest timer overlay (+15s, autoRest, haptics, app-scope persistence) | shipped | `RestTimer` object singleton survives tab switches |
| Complete flow (unlogged confirm, summary, BIGGER/STRONGER/STEADY) | shipped | `Sessions.completeWorkout` + `MomentumDialog` |
| Confetti on completion (session cap 3) | shipped | `Confetti` composable |
| Card badges (Needs 1RM/Ready/Started, one-open-at-a-time) | shipped | vCode 10 |
| **"How to do" / "Images" browser buttons per card** | shipped | **Batch 2** — `openExerciseBrowser()` |
| **"Show Instructions" toggle (numbered step list)** | shipped | **Batch 2** — `exercise.instructions.forEachIndexed` |
| **Superset / Drop set / Warmup toggle chips** | shipped | **Batch 2** — `PlateToggleChip` × 3 |
| **Plate math per working set** | shipped | **Batch 2** — `plateMath()` |
| **Resume / Replace / Skip actions** | shipped | **Batch 2** — Resume clears actual; Replace shows confirm; Skip already existed |
| **Regenerate workout (top + bottom buttons)** | shipped | **Batch 2** — `confirmRegenerate` dialog + `Generator.performGenerateWorkout` |
| **Share workout (QR + legacy text + clipboard)** | shipped | `ShareSheet.kt` — ZXing QR + `ShareText.workoutShareText()` + system share + clipboard |

## 6. History & Progress (full audit — carried forward + Batch 2 additions)

| Legacy feature | Status | Notes |
|---|---|---|
| Volume bars / strength trend / forecast / mastery | shipped | vCode 10 |
| Longevity score circle + breakdown | shipped | vCode 10 + Batch 2 `LongevityCircleCard` extracted |
| **Strength forecast with forecastTotal + forecastWilks** | shipped | **Batch 2** — `StrengthForecastCard` |
| 11-component radar (canvas mpComponentRadar) | shipped | vCode 10 `ComponentRadar` |
| 8-week workout frequency chart (canvas frequencyChart) | shipped | vCode 10 `FrequencyStrip` |
| RPE histogram | shipped | vCode 10 `RpeHistogram` |
| **Volume trend line chart (canvas volumeChart)** | shipped | **Batch 2** — `VolumeChart` |
| Strength trend chart (canvas strengthChart) | shipped | vCode 10 `CompositeChart` |
| Records board | shipped | vCode 10 `RecordsBoard` |
| Goal-cycle status | shipped | vCode 10 + Batch 2 `GoalCycleCard` extracted |
| 3-month projection | shipped | vCode 10 `projection3Month` |
| **5-year forecast (Monte Carlo)** | shipped | **Batch 2** — `FiveYearMonteCarlo` (1000 scenarios × 260 weeks) |
| **Per-muscle sparklines (last 4 weeks)** | shipped | **Batch 2** — `MuscleSparklines` |
| **Volume by muscle (last 7 days)** | shipped | **Batch 2** — `VolumeByMuscle7d` |

## 5. Library (post-Hotfix-1.1)

| Legacy feature | Status | Notes |
|---|---|---|
| 1,558 exercises verbatim | shipped | assets/data/exercises.json |
| **Lazy/chunked rendering, collapsed groups (ANR fix)** | shipped | **Hotfix 1.1** — LazyColumn + key + contentType |
| Search + group-by (muscle/equipment/difficulty/performed/category/component) | shipped | vCode 10 — 6 modes |
| Synonym expansion + fuzzy rank | shipped | vCode 10 |
| Clickable muscle tags → Muscle Wiki | shipped | vCode 10 |
| "How to do" / "Images" browser lookups | shipped | vCode 10 `openBrowser()` |
| Instruction detail (cues, mistakes, breathing, tempo) | shipped | `ExerciseDetailDialog` |
| Library Studio (author/delete custom exercises) | shipped | vCode 10 |
| Library export/import w/ merge report | shipped | vCode 10 |
| **No in-app 3D animated figures (removed per Hotfix 1.1)** | shipped | **Hotfix 1.1** — no RigArt.kt, no Canvas in cards |

## Intentionally not ported (unchanged from vCode 12 policy)

Anti-debug guards · vault blind autoRestore · remote CDN boot dependency · GAS backend telemetry · paywall/subscription gates · 404-style paywall takeover · free-license DM application wizard · notification suppression glue · UA fingerprinting · device-id telemetry.
