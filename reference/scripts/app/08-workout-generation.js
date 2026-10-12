// ============================================================
// reference/scripts/app/08-workout-generation.js
// Extracted from reference/scripts/app.js
// Original lines: 58216-58731  (516 lines)
// Section: Workout generation + deload logic
// ============================================================

// ==================== ENHANCED WORKOUT GENERATION with Deload Logic ====================

window.generateNextWorkout = function() {

    // ðŸ›¡ï¸ If there is already a current workout with exercises, ask before replacing

    if (currentWorkout && currentWorkout.exercises && currentWorkout.exercises.length > 0) {

        Swal.fire({

            title: 'Generate a new workout?',

            text: 'This will replace your current unsaved workout. Any unsaved progress will be lost.',

            icon: 'warning',

            showCancelButton: true,

            confirmButtonColor: '#d33',

            cancelButtonColor: '#3085d6',

            confirmButtonText: 'Yes, generate new',

            cancelButtonText: 'Cancel'

        }).then((result) => {

            if (result.isConfirmed) {

                performGenerateWorkout();

            } else {

                // Ensure the existing workout is still shown

                if (document.getElementById('workout-section').classList.contains('active')) {

                    updateTodaysWorkout();

                    updateWorkoutHeader();

                }

                updateResumeButtonVisibility();

            }

        });

        return;

    }

    // No current workout or it's empty â€“ generate directly

    performGenerateWorkout();

};



// Move the original generation logic into a helper function

function performGenerateWorkout() {

    applyFatigueDecay();

        decayMomentumBias();



    const now = new Date();

    const systemicFactor = calculateSystemicRecoveryFactor();



    // ----- 1. Adaptive volumeâ€‘based deload (Raj + Kaelen) -----

    // Compute average weekly volume over last 4 weeks (stored in workoutData.user.weeklyVolumes)

    let avgWeeklyVolume = 0;

    if (workoutData.user.weeklyVolumes && workoutData.user.weeklyVolumes.length >= 4) {

        const last4 = workoutData.user.weeklyVolumes.slice(-4);

        avgWeeklyVolume = last4.reduce((a,b) => a + b, 0) / 4;

    } else {

        // Fallback: use total volume last 7 days as baseline

        avgWeeklyVolume = workoutData.user.aggregates?.totalVolumeLast7Days || 0;

    }

    const volumeDeloadThreshold = avgWeeklyVolume * 1.5; // 150% of baseline

    const currentWeekVolume = workoutData.user.aggregates?.totalVolumeLast7Days || 0;

    const volumeDeload = currentWeekVolume > volumeDeloadThreshold;



    // Original systemic deload

    const systemicDeload = systemicFactor < 0.6;

    const needsDeload = systemicDeload || volumeDeload;



    // Taper logic (unchanged)

    const compDate = workoutData.user.competition?.date;

    let isTapering = false;

    if (compDate) {

        const meet = new Date(compDate);

        const daysToMeet = Math.ceil((meet - now) / (1000 * 60 * 60 * 24));

        if (daysToMeet <= 7 && daysToMeet >= 0) isTapering = true;

    }



    // ----- Split selection (unchanged) -----

    const splits = workoutProgram.splits;

    const lastWorkout = workoutData.workouts[workoutData.workouts.length - 1];

    let split;

    if (lastWorkout) {

        const lastIndex = splits.findIndex(s => s.id === lastWorkout.type);

        if (lastIndex !== -1) {

            const nextIndex = (lastIndex + 1) % splits.length;

            split = splits[nextIndex];

        } else split = splits[0];

    } else split = splits[0];



    if (needsDeload && !isTapering) {

        const recoverySplit = splits.find(s => s.id === 'mobility_flexibility') || split;

        split = recoverySplit;

    }



    const phase = getCurrentCyclePhase();

    const mult = getPhaseMultiplier(phase);



    // ----- Build workout object -----

    currentWorkout = {

        id: `workout_${Date.now()}`,

        date: now.toISOString(),

        type: split.id,

        name: split.name + (needsDeload && !isTapering ? ' (Deload)' : isTapering ? ' (Taper)' : ''),

        exercises: [],

        isDeload: needsDeload && !isTapering,

        isTaper: isTapering

    };



    // ---- NEW: Update workout cache with recovery factor and session bases ----

    updateWorkoutCache();

    // ------------------------------------------------------------------------



    // ----- Muscle selection (unchanged) -----

    const allMusclesList = getAllMuscleGroups().map(m => m.name);

    const muscleWorkCount = {};

    allMusclesList.forEach(m => muscleWorkCount[m] = 0);

    workoutData.workouts.forEach(workout => {

        workout.exercises?.forEach(ex => {

            if (ex.actual && !ex.skipped && ex.muscleGroup) {

                ex.muscleGroup.forEach(m => {

                    if (muscleWorkCount.hasOwnProperty(m)) muscleWorkCount[m]++;

                });

            }

        });

    });



    let targetMuscles = new Set(split.focus);

    const overdueMuscles = getOverdueMuscles(1.5, 2);

    overdueMuscles.forEach(m => targetMuscles.add(m));



    const readyMuscles = getReadyMuscles(Array.from(targetMuscles)).map(m => m.name);

    const focusCount = split.focus.length;

    let accessoryTarget = Math.floor(Math.random() * 3) + 2;

    accessoryTarget = Math.min(accessoryTarget, Math.max(2, 8 - focusCount));

    accessoryTarget = Math.max(0, accessoryTarget);



    if (readyMuscles.length > 0 && accessoryTarget > 0) {

        const numToPick = Math.min(accessoryTarget, readyMuscles.length);

        const shuffled = [...readyMuscles];

        for (let i = shuffled.length - 1; i > 0; i--) {

            const j = Math.floor(Math.random() * (i + 1));

            [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];

        }

        shuffled.slice(0, numToPick).forEach(m => targetMuscles.add(m));

    } else {

        const fallback = allMusclesList.filter(m => !targetMuscles.has(m));

        const numToPick = Math.min(accessoryTarget, fallback.length);

        for (let i = 0; i < numToPick; i++) {

            targetMuscles.add(fallback[i]);

        }

    }



    if (!targetMuscles.has('core')) {

        const coreFatigue = workoutData.muscleFatigue['core'] || 0;

        if (coreFatigue < READINESS_THRESHOLD) {

            targetMuscles.add('core');

        }

    }



    // ----- Generate exercises with deload & fatigueâ€‘based muscle deload -----

    const selectedExerciseIds = new Set();



    targetMuscles.forEach(mg => {

        const ignoreReadiness = overdueMuscles.includes(mg);

        let ex = selectExerciseForMuscle(mg, ignoreReadiness);

        if (!ex) ex = generateExerciseFromLibrary(mg);

        if (!ex) return;



        if (selectedExerciseIds.has(ex.id)) return;

        selectedExerciseIds.add(ex.id);



        let localMultiplier = mult;

        if (needsDeload && !isTapering) {

            localMultiplier *= 0.8;

        } else if (isTapering) {

            // taper â€“ keep intensity high

        }



        // ----- Fatigueâ€‘based muscleâ€‘specific deload (Elena + Kaelen) -----

        // Check if this muscle group requires a deload

        const muscleDeloadMultiplier = getMuscleDeloadMultiplier(mg);

        if (muscleDeloadMultiplier < 1.0) {

            localMultiplier *= muscleDeloadMultiplier;

        }



        let prescribed = generateExercisePrescription(ex, localMultiplier);

        if (prescribed) {

            // Apply steering bias to weight, reps, sets

            const bias = workoutData.user.momentumBias?.value || 0;

            currentWorkout.exercises.push(prescribed);

        }

    });



    // Fallback default exercises (unchanged)

    if (currentWorkout.exercises.length === 0) {

        const defaultMoves = [

            { id: "squat", mg: "quads" },

            { id: "bench_press", mg: "chest" },

            { id: "deadlift", mg: "hamstrings" }

        ];

        defaultMoves.forEach(def => {

            let ex = generateExerciseFromLibrary(def.mg);

            if (ex) currentWorkout.exercises.push(generateExercisePrescription(ex, mult));

        });

    }



    // Main lifts stay shuffled; athletic blocks then wrap them in fixed order:

    // [warm-up] -> [main lifts] -> [component drill finisher] -> [stretch cooldown]

    currentWorkout.exercises = currentWorkout.exercises.sort(() => Math.random() - 0.5);

    appendAthleticBlocks(currentWorkout, needsDeload && !isTapering, isTapering);

    saveCurrentWorkoutToStorage();



    // ----- UI updates (unchanged) -----

    updateDashboard();

    if (document.getElementById('workout-section').classList.contains('active')) {

        updateTodaysWorkout();

        updateWorkoutHeader();

    }



    if (needsDeload && !isTapering) {

        if (volumeDeload) showNotification("Deload triggered by high weekly volume. Reduce intensity.", "info");

        else showNotification("Deload week activated â€“ focus on form and recovery.", "info");

    } else if (isTapering) {

        showNotification("Taper week â€“ reduce volume, keep intensity for peak performance.", "info");

    } else {

        showNotification(`Workout generated: ${currentWorkout.name}`, 'success', 'workout_generated');

    }



    showSection('workout');

}

      

// Helper to get muscleâ€‘specific deload multiplier 

function getMuscleDeloadMultiplier(muscleName) {

    if (!workoutData.user.muscleDeloadStatus) return 1.0;

    const status = workoutData.user.muscleDeloadStatus[muscleName];

    if (status && status.active) {

        // Deload active â€“ reduce weight by 30%

        return 0.7;

    }

    return 1.0;

}



// Helper to update muscle deload status after a workout (call in postWorkoutUpdate)

function updateMuscleDeloadStatus(workout) {

    if (!workoutData.user.muscleDeloadStatus) workoutData.user.muscleDeloadStatus = {};

    const musclesTrained = new Set();

    workout.exercises.forEach(ex => {

        if (ex.actual && !ex.skipped && ex.muscleGroup) {

            ex.muscleGroup.forEach(m => musclesTrained.add(m));

        }

    });

    for (let muscle of musclesTrained) {

        const fatigue = getCoupledFatigue(muscle);

        const status = workoutData.user.muscleDeloadStatus[muscle] || { consecutiveHighFatigue: 0, active: false };

        if (fatigue >= 0.6) {

            status.consecutiveHighFatigue++;

            if (status.consecutiveHighFatigue >= 3) {

                status.active = true;

            }

        } else {

            status.consecutiveHighFatigue = 0;

            // Deactivate deload after one lowâ€‘fatigue session

            status.active = false;

        }

        workoutData.user.muscleDeloadStatus[muscle] = status;

    }



    saveToLocalStorage();

}

