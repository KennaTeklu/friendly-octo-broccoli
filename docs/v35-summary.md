# v35 — Consolidation

Five letters. One delivery each. All landed.

## 35a — Coach breadth
10 new authored coaches: Deadlift, BenchPress,
OverheadPress, BarbellRow, FrontSquat,
RomanianDeadlift, ChinUp, Dip, PushUp, FacePull.
Rows 20 → 55.

## 35b — Compound + banded + bodyweight + time
16 new authored coaches: SumoDeadlift,
InclineBenchPress, CloseGripBenchPress, PendlayRow,
PushPress, PowerClean, BandedFacePull,
BandedPullApart, BandedSquat, BandedRow,
BandedGluteBridge, BandedChestPress, InvertedRow,
ArcherPullUp, LSitPullUp, SidePlank.
Rows 55 → 113.

## 35c — Blocker fixes
B1 CoachRegistry routed to authored coaches (was
   only AllCoaches). Authored wins on shared ids.
   Keyword fallback for id mismatches.
B2 1RM panel gated on requirements().needs1RM.
B3 Library collapse: all groups start collapsed.
   Collapse/Expand buttons only when mixed.
B4 Library How-to and Images buttons wired.

## 35d — Taxonomy
exercise_taxonomy.json, 1413 entries. Six fields
per exercise: primaryMuscles, secondaryMuscles,
equipment, category, components, difficulty.
performed read from state tree.

## 35e — Difficulty gating
DifficultyGate.kt. Per-muscle prerequisites.
UNLOCK_THRESHOLD = 3. Medium unlocks after 3 easy
sessions for that muscle. Hard after 3 medium.
Locked cards render lock icon + reason, not hidden.

## Numbers
Coaches: 5 → 31
Authored rows: 20 → 113
Sources: 6 → 7 (added rippetoe assistance.md)
Exercises with taxonomy: 1413 / 1413
Tests added: ~80 across five letters

## Known issues (open)
- Visual parity with reference/html/app.html not
  yet attempted. Radar, workout page styling,
  floating rail, more tiles all in v37 scope.
- Logger not yet reading taxonomy.
- Overall section on Workout page not yet built.
- v33-v34e tests still reference hardcoded counts
  updated by each letter. If rows grow again,
  those assertions need another bump.
