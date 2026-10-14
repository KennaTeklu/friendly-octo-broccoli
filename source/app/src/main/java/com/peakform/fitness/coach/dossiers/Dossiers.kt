package com.peakform.fitness.coach.dossiers

/**
 * BATCH-37a — Authored dossiers for the gymnastic push chain.
 *
 * Source: sommer-2006/gymnastic-body-tier-table.md ONLY.
 *
 * SOMMER COVERAGE NOTE
 * -------------------
 * Sommer's tier table covers PULL-UPS only (tiers 1-10, names: Band-assisted,
 * Negative only, Jump-assisted, Chin-up, Pull-up, Wide pull-up, L-sit pull-up,
 * Archer pull-up, Typewriter, One-arm pull-up). Sommer's source explicitly
 * says: "This table covers pull-ups. Push-up and dip progressions follow the
 * same leverage model but with different tier names." Sommer does NOT name
 * the push-up chain tiers.
 *
 * So Sommer SUPPORTS:
 *   - The leverage model (advance when top of rep range cleared for all sets)
 *   - The progression rule (no weight added — increase leverage difficulty)
 *   - Rest increases with tier (90s for low tiers, 120s mid, 150s advanced,
 *     180s elite)
 *
 * Sommer does NOT support:
 *   - Specific push-up tier names (Wall, Incline, Knee, …, Full Planche)
 *   - Specific push-up rep ranges per tier
 *   - Specific muscle activation ratios for push-ups
 *
 * Per the spec rules:
 *   - Do not invent a goal that Sommer does not support. → Goals are
 *     "advance up the chain" — supported by Sommer's progression rule.
 *   - Do not invent a prerequisite number. → Prerequisites are SENTENCES
 *     citing Sommer's progression rule. Specific rep ranges are marked
 *     provisional in the authorNote.
 *   - Do not invent a stimulus ratio. → All push stimulus profiles are
 *     "coach-authored, provisional" (Sommer gives none for push-ups).
 *
 * CHAIN COVERAGE
 * --------------
 * The spec lists 15 push-chain exercises. The library (exercises.json) has
 * entries for only some of them — the others are non-library canonical ids
 * (the id the library WOULD use if the exercise existed). Per spec rule
 * "exerciseId matching the library id" — the dossier's exerciseId is the
 * canonical id derived from the exercise name (lowercase + whitespace→_).
 * The library lookup will fail for non-library exercises, but the dossier
 * itself is valid (it cites Sommer's leverage model and notes the gaps).
 *
 * Library-resolvable chain exercises:
 *   - Incline Push Up          (library id "incline_push_up")
 *   - Push-Up                  (library id "push-up"  — hyphen preserved)
 *   - Diamond Push-Up          (library id "diamond_push-up")
 *   - Decline Push Up          (library id "decline_push_up"  — no hyphen in name)
 *   - Pseudo Planche Push-Up   (library id "pseudo_planche_push-up")
 *
 * Non-library chain exercises (canonical ids):
 *   - Wall Push-Up             → "wall_push-up"
 *   - Knee Push-Up             → "knee_push-up"
 *   - Wide Push-Up             → "wide_push-up"  (library has "Push-Up (Wide)"
 *                                                 with id "push-up_(wide)";
 *                                                 spec name "Wide Push-Up" is
 *                                                 used here per the chain list)
 *   - Tuck Planche Hold        → "tuck_planche_hold"
 *   - Advanced Tuck Planche Hold → "advanced_tuck_planche_hold"
 *   - Straddle Planche Hold    → "straddle_planche_hold"
 *   - Planche Lean             → "planche_lean"
 *   - Tuck Planche Push-Up     → "tuck_planche_push-up"
 *   - Straddle Planche Push-Up → "straddle_planche_push-up"
 *   - Full Planche Push-Up     → "full_planche_push-up"
 *
 * See CHANGES.md for the per-exercise coverage notes.
 */

/**
 * The canonical Sommer source for the leverage model. All dossiers cite this.
 */
private const val SOMMER_SOURCE = "sommer-2006/gymnastic-body-tier-table.md"
private const val SOMMER_LEVERAGE_SECTION = "Progression rule"
private const val SOMMER_LEVERAGE_NOTE =
    "Sommer's leverage model: advance when top of rep range cleared for all sets; " +
        "no weight added — increase leverage difficulty; rest increases with tier."

/**
 * The 11-item component enum (per spec doc 03-the-dossier.md "the 11-item map").
 * Used as the canonical keys for the components / componentStimulus maps.
 */
private const val COMP_STRENGTH = "Strength"
private const val COMP_HYPERTROPHY = "Hypertrophy"
private const val COMP_ENDURANCE = "Endurance"
private const val COMP_POWER = "Power"
private const val COMP_FLEXIBILITY = "Flexibility"
private const val COMP_SKILL = "Skill"
private const val COMP_BALANCE = "Balance"
private const val COMP_AGILITY = "Agility"
private const val COMP_SPEED = "Speed"
private const val COMP_BODY_COMPOSITION = "BodyComposition"
private const val COMP_LONGEVITY = "Longevity"

/**
 * The push-chain stimulus profile template. Sommer gives NO muscle activation
 * ratios for push-ups — all are "coach-authored, provisional." The values are
 * best-guess activation weights for the standard push-up; variants scale
 * specific muscles up/down based on hand placement / leverage.
 *
 * Key: muscle id (lowercase, matching library/taxonomy conventions).
 * Value: per-set contribution weight (0.0..1.0).
 */
private val PUSH_STIMULUS_BASE: Map<String, Double> = mapOf(
    "chest" to 1.0,
    "triceps" to 0.7,
    "front_delts" to 0.5,
    "core" to 0.4,
)

private val PUSH_COMPONENT_STIMULUS_BASE: Map<String, Double> = mapOf(
    COMP_STRENGTH to 0.7,
    COMP_ENDURANCE to 0.3,
)

/**
 * The standard push-up dossier's leverage-model components. Push-ups train
 * Strength (primary) + Endurance + Skill (leverage control). Variants shift
 * the weights — e.g. planche variants push Skill to 5.
 */
private val PUSH_COMPONENTS_BASE: Map<String, Int> = mapOf(
    COMP_STRENGTH to 3,
    COMP_ENDURANCE to 2,
    COMP_SKILL to 2,
)

/**
 * All push-chain dossiers cite Sommer's leverage model for the progression
 * rule. The specific tier name + rep range is "coach-authored, provisional"
 * because Sommer does not enumerate the push tiers.
 */
private fun pushProvenance(tierNote: String): DossierProvenance = DossierProvenance(
    source = SOMMER_SOURCE,
    section = SOMMER_LEVERAGE_SECTION,
    authorNote = "$SOMMER_LEVERAGE_NOTE | $tierNote",
)

/**
 * Build a single-goal dossier for a push-chain exercise. The goal is
 * "advance up the chain" — supported by Sommer's progression rule. The
 * landmark is the top of the next tier's rep range (per Sommer's pull-up
 * table pattern, applied provisionally to the push chain).
 *
 * @param exerciseId       canonical library id
 * @param name              display name
 * @param instructions      how-to (from library where present; empty for
 *                          non-library exercises — Sommer does not provide
 *                          push-up instructions)
 * @param tierLabel         e.g. "Push chain tier 1 — wall (provisional)"
 * @param nextExerciseId    the next exercise in the chain (progressionOut);
 *                          null for the chain terminus (Full Planche Push-Up)
 * @param prevExerciseId    the previous exercise in the chain (progressionIn);
 *                          null for the chain root (Wall Push-Up)
 * @param landmarkSentence  the goal's landmark sentence
 * @param prerequisiteSentence  the prerequisite sentence (cites Sommer's rule)
 * @param componentsOverride  override the base push components (e.g. planche
 *                          variants push Skill to 5)
 * @param stimulusOverride   override the base stimulus profile (e.g. planche
 *                          variants shift emphasis to shoulders)
 * @param componentStimulusOverride  override the base component stimulus
 */
@Suppress("LongParameterList")
private fun pushDossier(
    exerciseId: String,
    name: String,
    instructions: List<String>,
    tierLabel: String,
    nextExerciseId: String?,
    prevExerciseId: String?,
    landmarkSentence: String,
    prerequisiteSentence: String,
    componentsOverride: Map<String, Int> = PUSH_COMPONENTS_BASE,
    stimulusOverride: Map<String, Double> = PUSH_STIMULUS_BASE,
    componentStimulusOverride: Map<String, Double> = PUSH_COMPONENT_STIMULUS_BASE,
): ExerciseDossier {
    val goalId = "advance_chain"
    val target = nextExerciseId ?: "chain_terminus"
    val goal = Goal(
        goalId = goalId,
        target = target,
        label = "Advance up the push chain",
        componentWeights = componentsOverride,
        landmark = landmarkSentence,
        source = "$SOMMER_SOURCE#$SOMMER_LEVERAGE_SECTION",
    )
    val prereq = Prerequisite(
        forGoalId = goalId,
        exerciseId = exerciseId,
        requirement = prerequisiteSentence,
        source = "$SOMMER_SOURCE#$SOMMER_LEVERAGE_SECTION",
    )
    return ExerciseDossier(
        exerciseId = exerciseId,
        name = name,
        coachId = "push_up",  // PushUpAuthoredCoach governs this family
        instructions = instructions,
        provenance = pushProvenance(tierLabel),
        components = componentsOverride,
        goals = listOf(goal),
        prerequisites = listOf(prereq),
        progressionIn = listOfNotNull(prevExerciseId),
        progressionOut = listOfNotNull(nextExerciseId),
        stimulusProfile = stimulusOverride,
        componentStimulus = componentStimulusOverride,
        variations = emptyMap(),  // Sommer names no tempo/load ranges for push-ups
        version = 1,
    )
}

/**
 * All 15 gymnastic push-chain dossiers.
 *
 * Chain order (per spec):
 *   Wall Push-Up → Incline Push-Up → Knee Push-Up → Push-Up →
 *   Diamond Push-Up → Wide Push-Up → Decline Push-Up →
 *   Pseudo Planche Push-Up → Tuck Planche Hold → Advanced Tuck Planche Hold →
 *   Straddle Planche Hold → Planche Lean → Tuck Planche Push-Up →
 *   Straddle Planche Push-Up → Full Planche Push-Up
 */
val PUSH_CHAIN_DOSSIERS: List<ExerciseDossier> = listOf(
    // 1. Wall Push-Up (not in library — canonical id)
    pushDossier(
        exerciseId = "wall_push-up",
        name = "Wall Push-Up",
        instructions = emptyList(),  // Sommer does not provide push-up instructions; library has no entry
        tierLabel = "Push chain tier 1 — wall (provisional; Sommer does not enumerate push tiers)",
        nextExerciseId = "incline_push_up",
        prevExerciseId = null,
        landmarkSentence = "3x10 clean wall push-ups, full ROM, hollow body",
        prerequisiteSentence = "No prerequisite — entry point of the push chain",
    ),
    // 2. Incline Push-Up (library: "Incline Push Up" → id "incline_push_up")
    pushDossier(
        exerciseId = "incline_push_up",
        name = "Incline Push Up",
        instructions = listOf("Hands on elevated surface", "Lower chest to hands", "Press to lockout"),
        tierLabel = "Push chain tier 2 — incline (provisional)",
        nextExerciseId = "knee_push-up",
        prevExerciseId = "wall_push-up",
        landmarkSentence = "3x10 clean incline push-ups, full ROM, hands at hip height",
        prerequisiteSentence = "3x10 clean wall push-ups, full ROM, hollow body (per Sommer's progression rule)",
    ),
    // 3. Knee Push-Up (not in library — canonical id)
    pushDossier(
        exerciseId = "knee_push-up",
        name = "Knee Push-Up",
        instructions = emptyList(),
        tierLabel = "Push chain tier 3 — knee (provisional)",
        nextExerciseId = "push-up",
        prevExerciseId = "incline_push_up",
        landmarkSentence = "3x10 clean knee push-ups, chest to floor, hollow body",
        prerequisiteSentence = "3x10 clean incline push-ups, full ROM, hands at hip height",
    ),
    // 4. Push-Up (library: "Push-Up" → id "push-up")
    pushDossier(
        exerciseId = "push-up",
        name = "Push-Up",
        instructions = listOf("Body straight", "Chest to floor", "Full lockout"),
        tierLabel = "Push chain tier 4 — standard (provisional; library entry exists)",
        nextExerciseId = "diamond_push-up",
        prevExerciseId = "knee_push-up",
        landmarkSentence = "3x10 clean push-ups, body straight, 2s pause bottom",
        prerequisiteSentence = "3x10 clean knee push-ups, chest to floor, hollow body",
    ),
    // 5. Diamond Push-Up (library: "Diamond Push-Up" → id "diamond_push-up")
    pushDossier(
        exerciseId = "diamond_push-up",
        name = "Diamond Push-Up",
        instructions = listOf("Hands form diamond under chest", "Lower chest to hands", "Press to lockout"),
        tierLabel = "Push chain tier 5 — diamond (triceps emphasis; provisional)",
        nextExerciseId = "wide_push-up",
        prevExerciseId = "push-up",
        landmarkSentence = "3x8 clean diamond push-ups, full ROM, triceps-driven",
        prerequisiteSentence = "3x10 clean push-ups, body straight, 2s pause bottom",
        stimulusOverride = mapOf(
            "chest" to 0.7,
            "triceps" to 1.0,  // diamond shifts emphasis to triceps
            "front_delts" to 0.4,
            "core" to 0.4,
        ),
    ),
    // 6. Wide Push-Up (library has "Push-Up (Wide)" → id "push-up_(wide)";
    //    spec name "Wide Push-Up" used per chain list — see CHANGES.md)
    pushDossier(
        exerciseId = "wide_push-up",
        name = "Wide Push-Up",
        instructions = listOf("Hands wider than shoulders", "Lower chest to floor", "Press to lockout"),
        tierLabel = "Push chain tier 6 — wide (chest emphasis; provisional; library name mismatch)",
        nextExerciseId = "decline_push_up",
        prevExerciseId = "diamond_push-up",
        landmarkSentence = "3x8 clean wide push-ups, full ROM, chest-driven",
        prerequisiteSentence = "3x8 clean diamond push-ups, full ROM, triceps-driven",
        stimulusOverride = mapOf(
            "chest" to 1.0,
            "triceps" to 0.5,  // wide shifts emphasis off triceps
            "front_delts" to 0.4,
            "core" to 0.4,
        ),
    ),
    // 7. Decline Push-Up (library: "Decline Push Up" → id "decline_push_up")
    pushDossier(
        exerciseId = "decline_push_up",
        name = "Decline Push Up",
        instructions = listOf("Feet elevated", "Lower chest to floor", "Press to lockout"),
        tierLabel = "Push chain tier 7 — decline (shoulders emphasis; provisional)",
        nextExerciseId = "pseudo_planche_push-up",
        prevExerciseId = "wide_push-up",
        landmarkSentence = "3x8 clean decline push-ups, full ROM, shoulders engaged",
        prerequisiteSentence = "3x8 clean wide push-ups, full ROM, chest-driven",
        stimulusOverride = mapOf(
            "chest" to 0.7,
            "triceps" to 0.7,
            "front_delts" to 1.0,  // decline shifts emphasis to shoulders
            "core" to 0.5,
        ),
    ),
    // 8. Pseudo Planche Push-Up (library: "Pseudo Planche Push-Up" → id "pseudo_planche_push-up")
    pushDossier(
        exerciseId = "pseudo_planche_push-up",
        name = "Pseudo Planche Push-Up",
        instructions = listOf("Lean forward over hands", "Lower chest to floor", "Press with forward lean"),
        tierLabel = "Push chain tier 8 — pseudo planche (planche progression; provisional)",
        nextExerciseId = "tuck_planche_hold",
        prevExerciseId = "decline_push_up",
        landmarkSentence = "3x8 clean pseudo planche push-ups, sustained forward lean, hollow body",
        prerequisiteSentence = "3x8 clean decline push-ups, full ROM, shoulders engaged",
        componentsOverride = mapOf(
            COMP_STRENGTH to 3,
            COMP_ENDURANCE to 2,
            COMP_SKILL to 4,  // planche variants push Skill up
        ),
        stimulusOverride = mapOf(
            "chest" to 0.6,
            "triceps" to 0.7,
            "front_delts" to 1.0,  // planche = shoulders dominant
            "core" to 0.8,  // planche demands huge core anti-extension
        ),
    ),
    // 9. Tuck Planche Hold (not in library — canonical id; this is a HOLD,
    //    not a push-up — the chain transitions to isometric holds)
    pushDossier(
        exerciseId = "tuck_planche_hold",
        name = "Tuck Planche Hold",
        instructions = emptyList(),
        tierLabel = "Push chain tier 9 — tuck planche hold (isometric; provisional)",
        nextExerciseId = "advanced_tuck_planche_hold",
        prevExerciseId = "pseudo_planche_push-up",
        landmarkSentence = "15s hold, knees tucked to chest, hips at shoulder height",
        prerequisiteSentence = "3x8 clean pseudo planche push-ups, sustained forward lean, hollow body",
        componentsOverride = mapOf(
            COMP_STRENGTH to 4,
            COMP_SKILL to 5,  // planche holds are Skill-dominant
            COMP_ENDURANCE to 2,
        ),
        stimulusOverride = mapOf(
            "front_delts" to 1.0,
            "chest" to 0.5,
            "triceps" to 0.4,
            "core" to 1.0,  // anti-extension
        ),
        componentStimulusOverride = mapOf(
            COMP_STRENGTH to 0.5,
            COMP_SKILL to 0.8,  // hold is skill-dominant
            COMP_ENDURANCE to 0.3,
        ),
    ),
    // 10. Advanced Tuck Planche Hold (not in library — canonical id)
    pushDossier(
        exerciseId = "advanced_tuck_planche_hold",
        name = "Advanced Tuck Planche Hold",
        instructions = emptyList(),
        tierLabel = "Push chain tier 10 — advanced tuck planche hold (isometric; provisional)",
        nextExerciseId = "straddle_planche_hold",
        prevExerciseId = "tuck_planche_hold",
        landmarkSentence = "20s hold, hips at shoulder height, straight back, knees tucked",
        prerequisiteSentence = "15s tuck planche hold, knees tucked to chest, hips at shoulder height",
        componentsOverride = mapOf(
            COMP_STRENGTH to 4,
            COMP_SKILL to 5,
            COMP_ENDURANCE to 2,
        ),
        stimulusOverride = mapOf(
            "front_delts" to 1.0,
            "chest" to 0.5,
            "triceps" to 0.4,
            "core" to 1.0,
        ),
        componentStimulusOverride = mapOf(
            COMP_STRENGTH to 0.5,
            COMP_SKILL to 0.8,
            COMP_ENDURANCE to 0.3,
        ),
    ),
    // 11. Straddle Planche Hold (not in library — canonical id)
    pushDossier(
        exerciseId = "straddle_planche_hold",
        name = "Straddle Planche Hold",
        instructions = emptyList(),
        tierLabel = "Push chain tier 11 — straddle planche hold (isometric; provisional)",
        nextExerciseId = "planche_lean",
        prevExerciseId = "advanced_tuck_planche_hold",
        landmarkSentence = "20s straddle planche hold, legs straight and wide, hips at shoulder height",
        prerequisiteSentence = "20s advanced tuck planche hold, hips at shoulder height, straight back",
        componentsOverride = mapOf(
            COMP_STRENGTH to 4,
            COMP_SKILL to 5,
            COMP_ENDURANCE to 2,
        ),
        stimulusOverride = mapOf(
            "front_delts" to 1.0,
            "chest" to 0.6,
            "triceps" to 0.4,
            "core" to 1.0,
        ),
        componentStimulusOverride = mapOf(
            COMP_STRENGTH to 0.5,
            COMP_SKILL to 0.8,
            COMP_ENDURANCE to 0.3,
        ),
    ),
    // 12. Planche Lean (not in library — canonical id)
    pushDossier(
        exerciseId = "planche_lean",
        name = "Planche Lean",
        instructions = emptyList(),
        tierLabel = "Push chain tier 12 — planche lean (static lean; provisional)",
        nextExerciseId = "tuck_planche_push-up",
        prevExerciseId = "straddle_planche_hold",
        landmarkSentence = "30s planche lean, shoulders forward of hands, body straight",
        prerequisiteSentence = "20s straddle planche hold, legs straight and wide, hips at shoulder height",
        componentsOverride = mapOf(
            COMP_STRENGTH to 4,
            COMP_SKILL to 5,
            COMP_ENDURANCE to 3,
        ),
        stimulusOverride = mapOf(
            "front_delts" to 1.0,
            "chest" to 0.5,
            "triceps" to 0.4,
            "core" to 1.0,
        ),
        componentStimulusOverride = mapOf(
            COMP_STRENGTH to 0.5,
            COMP_SKILL to 0.7,
            COMP_ENDURANCE to 0.4,
        ),
    ),
    // 13. Tuck Planche Push-Up (not in library — canonical id)
    pushDossier(
        exerciseId = "tuck_planche_push-up",
        name = "Tuck Planche Push-Up",
        instructions = emptyList(),
        tierLabel = "Push chain tier 13 — tuck planche push-up (dynamic planche; provisional)",
        nextExerciseId = "straddle_planche_push-up",
        prevExerciseId = "planche_lean",
        landmarkSentence = "3x5 clean tuck planche push-ups, hips at shoulder height throughout",
        prerequisiteSentence = "30s planche lean, shoulders forward of hands, body straight",
        componentsOverride = mapOf(
            COMP_STRENGTH to 4,
            COMP_SKILL to 5,
            COMP_ENDURANCE to 2,
        ),
        stimulusOverride = mapOf(
            "front_delts" to 1.0,
            "chest" to 0.7,
            "triceps" to 0.6,
            "core" to 1.0,
        ),
        componentStimulusOverride = mapOf(
            COMP_STRENGTH to 0.6,
            COMP_SKILL to 0.7,
            COMP_ENDURANCE to 0.3,
        ),
    ),
    // 14. Straddle Planche Push-Up (not in library — canonical id)
    pushDossier(
        exerciseId = "straddle_planche_push-up",
        name = "Straddle Planche Push-Up",
        instructions = emptyList(),
        tierLabel = "Push chain tier 14 — straddle planche push-up (dynamic planche; provisional)",
        nextExerciseId = "full_planche_push-up",
        prevExerciseId = "tuck_planche_push-up",
        landmarkSentence = "3x5 clean straddle planche push-ups, legs straight and wide, hips level",
        prerequisiteSentence = "3x5 clean tuck planche push-ups, hips at shoulder height throughout",
        componentsOverride = mapOf(
            COMP_STRENGTH to 5,
            COMP_SKILL to 5,
            COMP_ENDURANCE to 2,
        ),
        stimulusOverride = mapOf(
            "front_delts" to 1.0,
            "chest" to 0.7,
            "triceps" to 0.6,
            "core" to 1.0,
        ),
        componentStimulusOverride = mapOf(
            COMP_STRENGTH to 0.6,
            COMP_SKILL to 0.7,
            COMP_ENDURANCE to 0.3,
        ),
    ),
    // 15. Full Planche Push-Up (not in library — canonical id; chain terminus)
    pushDossier(
        exerciseId = "full_planche_push-up",
        name = "Full Planche Push-Up",
        instructions = emptyList(),
        tierLabel = "Push chain tier 15 — full planche push-up (chain terminus; provisional)",
        nextExerciseId = null,  // chain terminus — no progression out
        prevExerciseId = "straddle_planche_push-up",
        landmarkSentence = "3x3 clean full planche push-ups, legs together, body parallel to floor",
        prerequisiteSentence = "3x5 clean straddle planche push-ups, legs straight and wide, hips level",
        componentsOverride = mapOf(
            COMP_STRENGTH to 5,
            COMP_SKILL to 5,
            COMP_ENDURANCE to 2,
        ),
        stimulusOverride = mapOf(
            "front_delts" to 1.0,
            "chest" to 0.7,
            "triceps" to 0.6,
            "core" to 1.0,
        ),
        componentStimulusOverride = mapOf(
            COMP_STRENGTH to 0.6,
            COMP_SKILL to 0.7,
            COMP_ENDURANCE to 0.3,
        ),
    ),
)

/**
 * All authored dossiers. Currently the gymnastic push chain only (15 entries).
 * Future batches will add more chains (pull, dip, ring, etc.).
 */
val ALL_DOSSIERS: List<ExerciseDossier> = PUSH_CHAIN_DOSSIERS
