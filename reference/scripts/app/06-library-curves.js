// ============================================================
// reference/scripts/app/06-library-curves.js
// Extracted from reference/scripts/app.js
// Original lines: 56314-56737  (424 lines)
// Section: Library helpers, muscle types, rep curves, cycle phases
// ============================================================

// ----------------------------------------------------------------------------

// Note: In generateExercisePrescription, the target reps and sets are computed

// as a weighted average between the beginner and advanced curves, where the

// weight is the maturity factor (based on total workouts).

// The interpolation function is linear between the given intensity points.

// ----------------------------------------------------------------------------

      

// ==================== LIBRARY HELPER FUNCTIONS ====================



// ============================================================

// NEW: Cache Update Functions (Phase 1.2)

// ============================================================

function updateUserCache() {

    const u = workoutData.user;

    // Ensure cache object exists

    if (!u._cache) u._cache = {};

    

    const age = u.birthDate ? calculateAge(u.birthDate) : 30;

    

    u._cache.effectiveBodyWeight = u.weight || (u.gender === 'female' ? 130 : 150);

    

    u._cache.ageFactor = age < 30 ? 0.9 : (age <= 50 ? 1.0 : 0.8);

    u._cache.genderFactor = u.gender === 'female' ? 0.9 : 1.0;

    

    const expMap = { beginner: 0.8, intermediate: 1.0, advanced: 1.2 };

    u._cache.experienceFactor = expMap[u.experience] || 1.0;

    

    const expDeltaMap = { beginner: 1.3, intermediate: 1.0, advanced: 0.7 };

    u._cache.expDeltaFactor = expDeltaMap[u.experience] || 1.0;

    

    const phase = getCurrentCyclePhase();

    u._cache.phaseFactor = getPhaseMultiplier(phase) || 1.0;

    

    u._cache.priorBaseMultiplier = u._cache.experienceFactor * u._cache.ageFactor * u._cache.genderFactor * u._cache.phaseFactor;

    

    const ageDelta = age < 30 ? 1.0 : (age <= 50 ? 0.8 : 0.6);

    const genderDelta = u.gender === 'female' ? (phase === 'luteal' || phase === 'menstrual' ? 0.7 : 1.0) : 1.0;

    u._cache.progBaseMultiplier = u._cache.expDeltaFactor * ageDelta * genderDelta * u._cache.phaseFactor;

}



function updateWorkoutCache() {

    if (!currentWorkout) return;

    if (!currentWorkout._cache) currentWorkout._cache = {};

    const recovery = calculateSystemicRecoveryFactor();

    currentWorkout._cache.recoveryFactor = recovery;

    currentWorkout._cache.sessionPriorBase = workoutData.user._cache.priorBaseMultiplier * recovery;

    currentWorkout._cache.sessionDeltaBase = workoutData.user._cache.progBaseMultiplier * recovery;

}

// ============================================================



function getEnhancedExerciseList() {

    const enhanced = [];



    // Iterate over each muscle group category (e.g., 'quads', 'chest')

    for (const [muscleGroup, exercises] of Object.entries(ultimateExerciseLibrary)) {

        // Loop through each exercise in that category

        exercises.forEach(ex => {

            // --- Generate a consistent ID from the exercise name ---

            const id = ex.name.toLowerCase().replace(/\s+/g, '_');



            // --- Determine performed status from user history ---

            const record = workoutData.exercises[id];

            const performed = !!(record && record.history && record.history.length > 0);

            const performedCount = performed ? record.history.length : 0;



            // --- Difficulty classification (with fallback) ---

            let difficulty = 'medium';

            if (ex.skillFactor !== undefined) {

                if (ex.skillFactor < 0.5) difficulty = 'hard';

                else if (ex.skillFactor < 0.7) difficulty = 'medium';

                else difficulty = 'easy';

            }



            // --- Build the enhanced exercise object, providing fallbacks for missing fields ---

            const enhancedExercise = {

                ...ex,

                id: id,

                originalMuscleGroup: muscleGroup,

                primaryMuscle: ex.primaryMuscle || (ex.muscles?.[0]) || muscleGroup,

                difficulty: difficulty,

                performed: performed,

                performedCount: performedCount,

                equipment: ex.equipment || 'unknown',

                // Ensure muscles array exists (default to empty array)

                muscles: ex.muscles || []

            };



            // --- Apply derivation (adds strengthRatio, derivedFrom for specific exercises) ---

            enhanced.push(enhanceExerciseWithDerivation(enhancedExercise));

        });

    }



    return enhanced;

}

      

// ==================== MUSCLE TYPE & REP FLOORS ====================

// Map muscle names to type: 'large', 'medium', 'small'

function getMuscleType(muscleName) {

    const largeMuscles = ['quads', 'glutes', 'lats', 'back', 'chest', 'hamstrings', 'erectors'];

    const mediumMuscles = ['biceps', 'triceps', 'shoulders', 'front_delts', 'rear_delts', 'traps', 'calves', 'core', 'obliques', 'hip_flexors', 'adductors', 'abductors'];

    const smallMuscles = ['forearms', 'neck', 'deep_neck', 'tibialis', 'soleus', 'peroneus_tertius', 'foot_intrinsics', 'hand_intrinsics', 'thenar', 'supinator', 'pronator', 'brachialis', 'brachioradialis', 'anconeus', 'popliteus', 'articularis_genus', 'multifidus', 'transverse', 'quadratus_plantae'];



    if (largeMuscles.includes(muscleName)) return 'large';

    if (mediumMuscles.includes(muscleName)) return 'medium';

    return 'small';   // default small for safety

}



function getRepFloorForMuscle(muscleName) {

    const type = getMuscleType(muscleName);

    if (type === 'small') return 10;

    if (type === 'medium') return 5;

    return 1; // large has no floor

}



// For an exercise, get the strictest rep floor among its muscle groups

function getExerciseRepFloor(exercise) {

    if (!exercise.muscleGroup || exercise.muscleGroup.length === 0) return 1;

    let floor = 1;

    for (const muscle of exercise.muscleGroup) {

        const f = getRepFloorForMuscle(muscle);

        if (f > floor) floor = f;

    }

    return floor;

}



// ==================== LOVED REP CURVE (EXPONENTIAL + LINEAR FLOOR) ====================

function lovedReps(intensity, workouts, goal, express, aggression) {

    // Exponential base (starts at 20 reps at 40% intensity)

    let k = 4.5;

    if (goal === 'powerlifting') k = 5.0;

    else if (goal === 'balanced') k = 3.8;

    else if (goal === 'hypertrophy') k = 3.2;

    else k = 2.8;

    if (express && goal === 'powerlifting') k = 6.0;

    k *= aggression;

    // Shallower below 75% to preserve warmâ€‘up

    if (intensity < 0.75) k = k * 0.7;

    let raw = 20 * Math.exp(-k * (intensity - 0.4));

    let decay = Math.pow(0.94, workouts / 15);

    if (express && goal === 'powerlifting') decay = Math.pow(0.90, workouts / 12);

    let reps = raw * decay;

    let factor = 1.0;

    if (goal === 'powerlifting') factor = 0.9;

    else if (goal === 'hypertrophy') factor = 1.1;

    else if (goal === 'endurance') factor = 1.2;

    reps *= factor;

    // Linear floor: 18 reps at 40%, 3 reps at 90%, 1 at 95%

    let floor = 18 - (intensity - 0.4) * 30;

    floor = Math.min(18, Math.max(1, floor));

    reps = Math.max(reps, floor);

    // Beginner protection for low N

    if (workouts < 20) {

        let beginnerFloor = 18 - (intensity - 0.4) * 30;

        beginnerFloor = Math.max(6, beginnerFloor);

        reps = Math.max(reps, beginnerFloor);

    }

    reps = Math.min(20, Math.round(reps * 10) / 10);

    return reps;

}



// ==================== HUMPâ€‘SHAPED SETS CURVE (PRILEPIN) ====================

function humpSets(intensity, muscleType, workouts, express) {

    let peak = { large:5, medium:4, small:3 }[muscleType] || 4;

    const minSets = 2;

    const peakIntensity = 0.75;

    const width = 0.25;

    let t = (intensity - peakIntensity) / width;

    let curve = 1 - t * t;

    let raw = minSets + (peak - minSets) * curve;

    raw = Math.min(peak, Math.max(minSets, raw));

    // Experience factor (more workouts â†’ slightly more sets)

    let expFactor = Math.min(1.2, 0.8 + workouts / 150);

    if (express && workouts > 20) expFactor = Math.min(1.3, 0.9 + workouts / 100);

    let sets = raw * expFactor;

    // Clamp

    sets = Math.min(peak + 1, Math.max(minSets, Math.round(sets)));

    return sets;

}



// Helper to get the primary muscle type for an exercise (strictest among its muscles)

function getExerciseMuscleType(exercise) {

    if (!exercise.muscleGroup || exercise.muscleGroup.length === 0) return 'medium';

    let types = exercise.muscleGroup.map(m => getMuscleType(m));

    if (types.includes('small')) return 'small';

    if (types.includes('medium')) return 'medium';

    return 'large';

}



    // ==================== CYCLE PHASE FUNCTIONS ====================

function getCurrentCyclePhase(date = new Date()) {

    if (workoutData.user.gender !== "female" || !workoutData.user.menstrual.lastPeriodStart) return null;

    const last = new Date(workoutData.user.menstrual.lastPeriodStart);

    const today = new Date(date);

    const daysSince = Math.floor((today - last) / (1000*60*60*24));

    const cycleDay = (daysSince % workoutData.user.menstrual.cycleLength) + 1;

    if (cycleDay <= 5) return "menstrual";

    if (cycleDay <= 13) return "follicular";

    if (cycleDay <= 17) return "ovulatory";

    return "luteal";

}



    function getPhaseMultiplier(phase) {

        if (!phase) return 1.0;

        switch(phase) {

            case "menstrual": return 0.6;

            case "follicular": return 1.0;

            case "ovulatory": return 1.05;

            case "luteal": return 0.8;

            default: return 1.0;

        }

    }



