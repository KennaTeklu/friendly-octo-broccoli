// ============================================================
// reference/scripts/app/01-config-data.js
// Extracted from reference/scripts/app.js
// Original lines: 1-485  (485 lines)
// Section: Version, data structures, Dexie, Phase 2, CIG
// ============================================================

// ============================================================
// EXTRACTED FROM: index (24).html (cautious-enigma repo)
// app.js â€” main app script (flat global namespace; contains ALL feature logic intermingled). Added beyond the required 11-file structure because pure extraction prohibits surgical feature-splitting of this 40K-line monolith.
// Source line ranges (1-indexed, inclusive):
//   L6933-47485  (main app script (40,553 lines â€” version check, data structures, all feature functions: dashboard, workout, library, progress, recovery, history, settings; flat global namespace, no internal module boundaries))
// Total source lines: 40553
// ============================================================

// ---- BEGIN extracted from index (24).html L6933-47485 (main app script (40,553 lines â€” version check, data structures, all feature functions: dashboard, workout, library, progress, recovery, history, settings; flat global namespace, no internal module boundaries)) ----
      

// ==================== VERSION CONTROL ====================

const APP_VERSION = '2.0.0'; // Increment with each release

const VERSION_STORAGE_KEY = 'workout_app_version';



// On page load, check version and prompt if outdated

(function() {

    try {

        const storedVersion = localStorage.getItem(VERSION_STORAGE_KEY);

        if (storedVersion && storedVersion !== APP_VERSION) {

            // Version mismatch â€“ show banner

            const banner = document.createElement('div');

            banner.id = 'versionBanner';

            banner.style.cssText = 'position:fixed; top:0; left:0; right:0; background:#ff4444; color:white; text-align:center; padding:12px; z-index:10000; font-family:Inter, sans-serif; display:flex; align-items:center; justify-content:center; gap:15px;';

            banner.innerHTML = `

                <span>ðŸš€ New version available (${APP_VERSION}). Please refresh to get the latest features.</span>

                <button id="refreshNowBtn" style="background:white; color:#ff4444; border:none; padding:8px 20px; border-radius:5px; cursor:pointer; font-weight:bold;">Refresh Now</button>

                <button id="closeBannerBtn" style="background:transparent; color:white; border:1px solid white; padding:8px 12px; border-radius:5px; cursor:pointer;">Later</button>

            `;

            document.body.prepend(banner);

            

            document.getElementById('refreshNowBtn').addEventListener('click', () => {

                localStorage.setItem(VERSION_STORAGE_KEY, APP_VERSION);

                location.reload();

            });

            document.getElementById('closeBannerBtn').addEventListener('click', () => {

                banner.remove();

            });

        } else if (!storedVersion) {

            // First visit â€“ set version

            localStorage.setItem(VERSION_STORAGE_KEY, APP_VERSION);

        }

    } catch (e) {

    }

})();

      

      /**

 * === EXERCISE ROTATION SYSTEM (WEIGHTED RANDOM) ===

 * 

 * Data fields added to workoutData.exercises[exerciseId]:

 *   - lastPerformed: ISO timestamp of the most recent completion.

 * 

 * Data fields added to ultimateExerciseLibrary (augmented automatically):

 *   - primaryMuscle: The main muscle group targeted (used for rotation fairness).

 *   - importance: 'core', 'accessory', or 'specialised' â€“ influences selection weight.

 * 

 * Helper functions:

 *   - updateExerciseLastPerformed(exerciseId) â€“ Updates lastPerformed after logging.

 *   - isMuscleReady(muscleName) â€“ Checks if fatigue < threshold.

 *   - selectExerciseForMuscle(muscleGroup) â€“ Returns an exercise using weighted random.

 *   - backfillLastPerformed() â€“ Sets lastPerformed from existing history (called on load).

 * 

 * Selection weight = daysSinceLast * importanceMultiplier

 *   - daysSinceLast = 1000 for neverâ€‘performed exercises.

 *   - importanceMultiplier: core = 1.5, accessory = 0.8, specialised = 1.0.

 * 

 * How it's used:

 *   - In generateNextWorkout and generateLongevityWorkout, exercises are chosen via

 *     selectExerciseForMuscle instead of the old random function.

 *   - Exercises are filtered by primary muscle readiness before selection.

 */

 

    // ==================== ORIGINAL DATA STRUCTURES (enhanced) ====================

  const workoutData = {

    user: {

        name: "",

        birthDate: null,

        gender: "male",

        weight: null,

        height: null,

        experience: "intermediate",

        goal: "balanced",

        created: new Date().toISOString(),

        settings: {

            workoutDays: ["monday", "wednesday", "friday"],

            restTime: 90,

            progressionRate: 0.02,

            theme: "blue",

            darkMode: false,

            muscleCoupling: false  

        },

        agingRisks: [],

        menstrual: { lastPeriodStart: null, cycleLength: 28, symptoms: [] },

        momentumBias: {

            value: 0.0,

            decayRate: 0.25,

            lastUpdated: null,

            lastTap: null   // stores 'bigger' or 'stronger' for last session

        },

        // NEW: caching object for preâ€‘computed demographic multipliers

        _cache: {

            effectiveBodyWeight: null,   // user weight or fallback

            ageFactor: null,             // 0.9 / 1.0 / 0.8

            genderFactor: null,          // 0.9 / 1.0

            experienceFactor: null,      // 0.8 / 1.0 / 1.2

            expDeltaFactor: null,        // 1.3 / 1.0 / 0.7

            phaseFactor: null,           // 0.8 / 1.0 / 1.05

            priorBaseMultiplier: null,   // product of all prior factors

            progBaseMultiplier: null     // product of all progression factors

        }

    },

    workouts: [],

    exercises: {},

    history: [],

    muscleFatigue: {},

    lastFatigueDecay: null, 

    muscleRecentExercises: {},

    syncedToBackend: false,

    // Learned coupling matrix (will be updated over time)

    couplingMatrix: {} // will be populated dynamically

};



// ==================== DEXIE DATABASE SETUP ====================

const db = new Dexie('WorkoutApp');

db.version(1).stores({

    workouts: 'id, date',

    exercises: 'id',

    user: 'key',

    savedWorkout: 'id'

});

     

// ==================== PHASE 2 DATA MODEL EXTENSIONS ====================

// Extend user object

workoutData.user.strengthComposite = 0;

workoutData.user.bodyWeightHistory = []; // array of { date, weight, source }

workoutData.user.geneticPotential = { maxLeanMass: 0, sGenetic: 0 };

workoutData.user.neuralPhaseEnd = null;

workoutData.user.aggregates = {

    totalVolumeLast7Days: 0,

    avgRPELast7Days: 0,

    volumeByMuscleLast60Days: {}

};



// Replace muscleFatigue with a biâ€‘exponential structure (we'll reinitialize later)

workoutData.muscleFatigue = {};



// Add muscle decay constants (after workoutData)

const MUSCLE_DECAY = {

    quads:         { fast: 0.10, slow: 0.012 },

    hamstrings:    { fast: 0.09, slow: 0.010 },

    glutes:        { fast: 0.08, slow: 0.009 },

    chest:         { fast: 0.12, slow: 0.008 },

    back:          { fast: 0.10, slow: 0.011 },

    shoulders:     { fast: 0.11, slow: 0.010 },

    biceps:        { fast: 0.13, slow: 0.007 },

    triceps:       { fast: 0.13, slow: 0.007 },

    calves:        { fast: 0.06, slow: 0.015 },

    core:          { fast: 0.09, slow: 0.010 },

    forearms:      { fast: 0.12, slow: 0.008 },

    traps:         { fast: 0.10, slow: 0.010 },

    neck:          { fast: 0.08, slow: 0.012 },

    // Add any other muscles from your database as needed

};

      

// Muscle coupling matrix (sparse, values represent fraction of fatigue transferred)

const COUPLING_MATRIX = {

    quads: { glutes: 0.05, hamstrings: 0.02 },

    glutes: { quads: 0.05, hamstrings: 0.03 },

    hamstrings: { glutes: 0.03, quads: 0.02 },

    chest: { shoulders: 0.04, triceps: 0.02 },

    shoulders: { chest: 0.04, triceps: 0.03 },

    back: { lats: 0.05, rear_delts: 0.02 },

    lats: { back: 0.05, biceps: 0.02 },

    // add other pairs as needed â€“ you can expand later

};

async function loadAllData() {

    // Load workouts

    const workouts = await db.workouts.toArray();

    // Load exercises (convert array back to object keyed by id)

    const exerciseRecords = await db.exercises.toArray();

    const exercises = {};

    exerciseRecords.forEach(rec => {

        const { id, ...rest } = rec;

        exercises[id] = rest;

    });

    // Load user profile

    const userRecord = await db.user.get('main');

    const user = userRecord ? userRecord.value : { ...workoutData.user };

    // Load saved workout

    const savedRecord = await db.savedWorkout.get('current');

    savedWorkout = savedRecord ? savedRecord.value : null;



    // Populate global workoutData

    workoutData.workouts = workouts;

    workoutData.exercises = exercises;

    workoutData.user = user;



    // Rebuild derived data

    rebuildFatigueFromHistory();

    calculateMuscleLastTrained();

    backfillLastPerformed();

    recomputeAggregates();

    updateBodyWeightEstimate();



    // Set current workout (if savedWorkout exists and matches format)

    if (savedWorkout && savedWorkout.exercises && savedWorkout.exercises.length > 0) {

        currentWorkout = savedWorkout;

        requestWakeLock(); 

    } else if (!currentWorkout) {

        generateNextWorkout();

    }

}    

// Add this near the bottom of your script

function checkVersionAndWarn() {

    const storedVersion = localStorage.getItem(VERSION_STORAGE_KEY);

    if (storedVersion && storedVersion !== APP_VERSION) {

        const warningDiv = document.createElement('div');

        warningDiv.style.cssText = 'position:fixed; top:0; left:0; right:0; background:#ff4444; color:white; text-align:center; padding:10px; z-index:9999;';

        warningDiv.innerHTML = 'âš ï¸ New version available. <button onclick="location.reload(true)" style="background:white; color:#ff4444; border:none; padding:5px 15px; border-radius:5px; cursor:pointer;">Refresh Now</button>';

        document.body.prepend(warningDiv);

    }

}

setTimeout(checkVersionAndWarn, 1000);

      

// Get coupled fatigue for a muscle (respects user setting and learned matrix)

function getCoupledFatigue(muscle) {

    const base = workoutData.muscleFatigue[muscle];

    if (!base) return 0;

    let total = base.fast + base.slow;

    

    // If coupling is disabled, return raw fatigue

    if (!workoutData.user.settings.muscleCoupling) return total;

    

    // Use learned matrix if available, otherwise static matrix

    const couplings = workoutData.couplingMatrix?.[muscle] || COUPLING_MATRIX[muscle];

    if (couplings) {

        for (let [src, coeff] of Object.entries(couplings)) {

            const srcFatigue = workoutData.muscleFatigue[src];

            if (srcFatigue) {

                total += coeff * (srcFatigue.fast + srcFatigue.slow);

            }

        }

    }

    return Math.min(1, total); // clamp to [0,1]

}

      

const DEFAULT_DECAY = { fast: 0.10, slow: 0.010 };

      

