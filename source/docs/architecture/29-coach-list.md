# 29 - The full coach list

The names. The families. The scope.

## Core barbell compound coaches

- BenchPressCoach
- BackSquatCoach
- DeadliftCoach
- OverheadPressCoach
- BarbellRowCoach
- FrontSquatCoach
- RomanianDeadliftCoach
- SumoDeadliftCoach
- InclineBenchPressCoach

## Dumbbell and machine compound coaches

- DumbbellBenchPressCoach
- DumbbellRowCoach
- DumbbellShoulderPressCoach
- LegPressCoach
- LatPulldownCoach
- CableRowCoach
- HipThrustCoach

## Bodyweight coaches

- PullUpCoach
- ChinUpCoach
- PushUpCoach
- DipCoach
- PistolSquatCoach
- InvertedRowCoach

## Banded coaches

- BandedLateralRaiseCoach
- BandedFacePullCoach
- BandedPullApartCoach
- BandedSquatCoach
- BandedRowCoach

## Time-based coaches

- PlankCoach
- SidePlankCoach
- DeadHangCoach
- FarmersCarryCoach
- WallSitCoach

## Isolation coaches

- DumbbellCurlCoach
- BarbellCurlCoach
- TricepPushdownCoach
- OverheadTricepCoach
- LegCurlCoach
- LegExtensionCoach
- CalfRaiseCoach
- LateralRaiseCoach
- RearDeltFlyCoach
- ChestFlyCoach
- HipAbductionCoach
- HipAdductionCoach

## Cardio coaches

- RunningCoach
- CyclingCoach
- RowingCoach
- SwimmingCoach
- EllipticalCoach
- StairClimberCoach

## Mobility coaches

- HamstringStretchCoach
- HipFlexorStretchCoach
- ShoulderDislocateCoach
- AnkleMobilityCoach
- ThoracicRotationCoach

## Recovery coaches

- FoamRollCoach
- SaunaCoach
- ColdExposureCoach
- SleepCoach

## Overall and coordination

- OverallCoach
- ElevenComponentCoaches
- ChronicleCoach
- MetaCoach
- TwoBodyCoach

## Generic fallbacks (should be zero in practice)

- GenericBarbellCoach
- GenericDumbbellCoach
- GenericBodyweightCoach
- GenericBandedCoach
- GenericTimeBasedCoach
- GenericIsolationCoach
- GenericCardioCoach
- GenericMobilityCoach

Fallbacks exist as safety. The library coverage test asserts that no exercise is resolved to a fallback unless no specific coach could be written.

## The count

- ~60 named coaches
- 11 component coaches
- 4 meta (Overall, Chronicle, Meta, TwoBody)
- 8 generic fallbacks

Total: ~83 named decision-makers. Plus the field. Plus the state tree. Plus the surfaces.

The system is dense. Every exercise has a coach. Every coach has a name.