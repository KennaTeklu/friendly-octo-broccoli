// ============================================================
// reference/scripts/app/07-exercise-generation.js
// Extracted from reference/scripts/app.js
// Original lines: 56738-58215  (1478 lines)
// Section: Exercise generation (rep curves, sets curves, deload)
// ============================================================

    // ==================== NEW EXERCISE GENERATION FUNCTIONS ====================

function generateExerciseFromLibrary(muscleGroup) {

    if (!ultimateExerciseLibrary[muscleGroup]) return null;

    const allExercises = ultimateExerciseLibrary[muscleGroup];

    if (allExercises.length === 0) return null;



    // Get the list of recent exercise IDs for this muscle group

    const recent = workoutData.muscleRecentExercises[muscleGroup] || [];



    // Filter out recent exercises (by ID)

    let candidates = allExercises.filter(ex => {

        const exId = ex.name.toLowerCase().replace(/\s/g, '_');

        return !recent.includes(exId);

    });



    // If all exercises are recent, use the full list (no rotation possible)

    if (candidates.length === 0) {

        candidates = allExercises;

    }



    const base = candidates[Math.floor(Math.random() * candidates.length)];



    // Create a consistent ID from the exercise name

    const id = base.name.toLowerCase().replace(/\s/g, '_');



    // Build the base exercise object

    const exercise = {

        id: id,

        name: base.name,

        muscleGroup: base.muscles,

        prescribed: {

            sets: base.defaultSets,

            reps: base.defaultReps,

            weight: null

        },

        actual: null,

        progressionNotes: base.progression,

        equipment: base.equipment,

        instructions: base.instructions

    };



    // If the user has already set a starting weight for this exercise, use it

    const stored = workoutData.exercises[id];

    if (stored && typeof stored.startingWeight === 'number') {

        exercise.prescribed.weight = stored.startingWeight;

    }



    // Add default load distribution based on muscleGroup (equal distribution)

    if (exercise.muscleGroup && exercise.muscleGroup.length > 0) {

        const factor = 1 / exercise.muscleGroup.length;

        exercise.loadDistribution = {};

        exercise.muscleGroup.forEach(m => exercise.loadDistribution[m] = factor);

    } else {

        exercise.loadDistribution = {};

    }



    return exercise;

}



/**

 * Generate a dynamic prescription for an exercise.

 * @param {Object} exercise - The exercise object (must include fields from library)

 * @param {number} phaseMultiplier - Menstrual cycle phase multiplier (default 1.0)

 * @returns {Object} The exercise with prescribed sets, reps/duration, weight, and notes.

 */

function generateExercisePrescription(exercise, phaseMultiplier = 1.0) {

    const exId = exercise.id;

    const record = getOrCreateExerciseRecord(exId);

    const user = workoutData.user;

    const historyArray = record.history || [];

    const sameExerciseHistory = historyArray.length > 0;



    // ---- 1. Try to get last session for this specific exercise ----

    let lastSession = null;

    if (historyArray.length > 0) {

        lastSession = historyArray[historyArray.length - 1];

    }



    // ---- 2. If no exerciseâ€‘specific history, find the most recent session for any exercise that shares a muscle group ----

    if (!lastSession && exercise.muscleGroup && exercise.muscleGroup.length > 0) {

        const targetMuscles = new Set(exercise.muscleGroup);

        let bestDate = null;

        let bestExId = null;

        let bestHistoryEntry = null;



        for (const [otherExId, exData] of Object.entries(workoutData.exercises)) {

            if (!exData.history || exData.history.length === 0) continue;

            const otherExercise = getExerciseById(otherExId);

            if (!otherExercise || !otherExercise.muscleGroup) continue;

            const hasCommon = otherExercise.muscleGroup.some(m => targetMuscles.has(m));

            if (!hasCommon) continue;

            const recentEntry = [...exData.history].reverse().find(e => !e.skipped);

            if (!recentEntry) continue;

            const entryDate = new Date(recentEntry.date);

            if (!bestDate || entryDate > bestDate) {

                bestDate = entryDate;

                bestExId = otherExId;

                bestHistoryEntry = recentEntry;

            }

        }



        if (bestHistoryEntry) {

            lastSession = {

                weight: bestHistoryEntry.weight,

                reps: (Array.isArray(bestHistoryEntry.reps) ? Math.max(...bestHistoryEntry.reps) : bestHistoryEntry.reps),

                sets: bestHistoryEntry.sets,

                rpe: bestHistoryEntry.rpe,

                date: bestDate.toISOString()

            };

        }

    }



    // ---- 3. If still no history (new muscle group) â†’ start with high reps (17-20) and safe weight ----

    if (!lastSession) {

        // ---- 3a. Athletic drills & stretches: prescribe straight from library

        // defaults. No fiber weight math â€” these are quality/skill/lengthening

        // movements where load is optional and reps or time are the target.

        if (exercise.noFatigue || exercise.prescriptionType === 'time') {

            const baseSets = (exercise.prescribed && exercise.prescribed.sets) || exercise.defaultSets || 3;

            const baseReps = (exercise.prescribed && exercise.prescribed.reps) || exercise.defaultReps || '10';

            exercise.prescribed = { sets: baseSets, reps: baseReps, weight: null };

            if (exercise.prescriptionType === 'time' || exercise.defaultDuration) {

                exercise.prescribed.duration = exercise.defaultDuration || 45;

            }

            exercise.progressionNotes = exercise.progression || 'Quality over quantity â€” crisp reps, steady breathing.';

            return exercise;

        }

        let estimated1RM;

        // First check if we have a tested 1RM from the bracketing test

        const exRecord = workoutData.exercises[exId];

        if (exRecord && exRecord.tested1RM && exRecord.tested1RM > 0) {

            estimated1RM = exRecord.tested1RM;

        } else {

            // Fallback to body weight and muscleâ€‘group averaged strength index

            const userWeight = user.weight || 150;

            

            // Compute average strength index for the exercise's primary muscle groups

            const targetMuscles = exercise.muscleGroup || [];

            let strengthIndex = 0;

            if (targetMuscles.length > 0) {

                let totalSI = 0;

                let count = 0;

                // Iterate over all exercises in the library

                for (const group in ultimateExerciseLibrary) {

                    for (const ex of ultimateExerciseLibrary[group]) {

                        if (ex.noFatigue) continue; // stretches & athletic drills never dilute strength estimates

                        const exMuscles = ex.muscles || [];

                        // Check if this exercise targets any of the target muscles

                        if (exMuscles.some(m => targetMuscles.includes(m))) {

                            totalSI += ex.strengthIndex || 0.7;

                            count++;

                        }

                    }

                }

                strengthIndex = count > 0 ? totalSI / count : 0.7;

            } else {

                strengthIndex = exercise.strengthIndex || 0.7;

            }

            

            if (user.birthDate) {

                const age = calculateAge(user.birthDate);

                if (age > 60) strengthIndex *= 0.7;

                else if (age > 50) strengthIndex *= 0.8;

                else if (age > 40) strengthIndex *= 0.9;

            }

            estimated1RM = userWeight * strengthIndex * phaseMultiplier;

            if (isNaN(estimated1RM) || estimated1RM < 20) estimated1RM = 50;

        }

        

        // ---- NEW: Muscleâ€‘specific maturity factor ----

        const targetMuscles = exercise.muscleGroup || [];

        let muscleWorkouts = 0;

        if (targetMuscles.length > 0) {

            // Count workouts that trained any of these muscles

            for (const w of workoutData.workouts) {

                if (!w.exercises) continue;

                const trained = w.exercises.some(ex => 

                    ex.actual && !ex.skipped && ex.muscleGroup && 

                    ex.muscleGroup.some(m => targetMuscles.includes(m))

                );

                if (trained) muscleWorkouts++;

            }

        }

        const muscleMaturity = 1 + 0.1 * (1 - Math.exp(-muscleWorkouts / 20));

        estimated1RM *= muscleMaturity;

        // --------------------------------------------------

        

        const expMapStart = { beginner: 0.50, intermediate: 0.55, advanced: 0.60 };

        const startingIntensity = expMapStart[user.experience] || 0.55;

        // Fiber-aware first load: reps 17-20 normally imply ~36-40% 1RM, but

        // slow-twitch muscles cruise below their floor â€” clamp UP to the floor;

        // fragile fast-twitch muscles cap the ceiling. First-ever session is

        // additionally capped at 60% of the estimated 1RM for safety.

        const firstFiberRange = computeFiberWeightRange(exercise, estimated1RM, 18);

        let prescribedWeight = firstFiberRange

            ? Math.min(firstFiberRange.min, estimated1RM * 0.60)

            : estimated1RM * startingIntensity;

        prescribedWeight = roundToNearest(prescribedWeight, 2.5);

        if (isNaN(prescribedWeight) || prescribedWeight < 5) prescribedWeight = 5;

        exercise._fiberMax = firstFiberRange ? firstFiberRange.max : null;

        

        // Fixed high rep range (17-20)

        const repLow = 17;

        const repHigh = 20;

        

        // ---- NEW: define intensity for humpSets ----

        const intensity = startingIntensity;

        // --------------------------------------------

        

        const totalWorkouts = workoutData.workouts.length;

        const expressMode = user.settings?.expressMode || false;

        const muscleType = getExerciseMuscleType(exercise);

        let targetSets = humpSets(intensity, muscleType, totalWorkouts, expressMode);

        // Ensure sets are valid

        if (isNaN(targetSets) || targetSets < 2) targetSets = 3;

        targetSets = Math.min(7, Math.max(2, targetSets));

        

        exercise.prescribed = {

            sets: targetSets,

            reps: `${repLow}-${repHigh}`,

            weight: prescribedWeight,

            weightMax: exercise._fiberMax || null

        };

        delete exercise._fiberMax;

        if (exercise.prescriptionType === 'time') {

            exercise.prescribed.duration = exercise.defaultDuration || 60;

        }

        const fiberNoteFirst = firstFiberRange ? ` Fiber window ${firstFiberRange.lowPct}-${firstFiberRange.maxPct}% 1RM.` : '';

        exercise.progressionNotes = `First time for this muscle group! Starting with higher reps (17-20). Aim for ${repHigh} reps before increasing weight.${fiberNoteFirst}`;

        return exercise;

    }



    // ---- 3b. Time-based movements (stretches, holds, timed conditioning) ----

    if (exercise.prescriptionType === 'time') {

        const libSets = (exercise.prescribed && exercise.prescribed.sets) || exercise.defaultSets || 3;

        const duration = exercise.defaultDuration || (exercise.prescribed && exercise.prescribed.duration) || 45;

        exercise.prescribed = {

            sets: libSets,

            reps: 'hold',

            weight: null,

            duration: duration

        };

        exercise.progressionNotes = `Hold for ${duration} seconds per set. Breathe slowly, sink deeper on each exhale, never bounce.`;

        return exercise;

    }



    // ---- 3c. No-fatigue athletic drills (skill/power/agility/balance/coordination) ----

    // Reps and sets still progress, but weight stays optional and unsuggested â€”

    // these movements are trained for quality, speed and control.

    if (exercise.noFatigue) {

        const libSets = (exercise.prescribed && exercise.prescribed.sets) || exercise.defaultSets || 3;

        const libReps = (exercise.prescribed && exercise.prescribed.reps) || exercise.defaultReps || '10';

        let drillSets = libSets;

        if (sameExerciseHistory && lastSession && lastSession.sets) {

            const lastRPEdrill = (Array.isArray(lastSession.rpe) ? lastSession.rpe[0] : lastSession.rpe) || 7;

            drillSets = lastRPEdrill >= 9 ? Math.max(2, lastSession.sets - 1)

                      : lastRPEdrill <= 5 ? Math.min(8, lastSession.sets + 1)

                      : lastSession.sets;

        }

        exercise.prescribed = { sets: drillSets, reps: libReps, weight: null };

        if (exercise.defaultDuration) exercise.prescribed.duration = exercise.defaultDuration;

        exercise.progressionNotes = 'Athletic drill â€” every rep crisp. Only add load when form is perfect on all sets.';

        return exercise;

    }



    // ---- 4. Gradual progression from the last session (exerciseâ€‘specific or muscleâ€‘group) ----

    // a) Determine target reps with caps and steering

    let newReps = (Array.isArray(lastSession.reps) ? Math.max(...lastSession.reps) : lastSession.reps);

    let newSets = lastSession.sets;



    const bias = workoutData.user.momentumBias?.value || 0;

    const lastRPE = (Array.isArray(lastSession.rpe) ? lastSession.rpe[0] : lastSession.rpe) || 7;

    let rpeFactor = 1.0;

    if (lastRPE >= 9) rpeFactor = 0.6;

    else if (lastRPE <= 5) rpeFactor = 1.3;



    // Rep change from bias (capped)

    let repDelta = (-bias) * rpeFactor;

    const maxRepDrop = (user.settings?.expressMode ? 3 : 2);

    const maxRepIncrease = 2;

    let repChange = Math.min(maxRepIncrease, Math.max(-maxRepDrop, repDelta));

    newReps = newReps + repChange;



    // Natural progression if top of rep range hit (only if we have a prescribed range from previous session)

    const lastRepRange = exercise.prescribed?.reps ? exercise.prescribed.reps.split('-') : null;

    if (lastRepRange && lastRepRange.length === 2 && newReps >= parseInt(lastRepRange[1]) && bias === 0) {

        // Encourage progression: reduce reps by an extra 1 (since we already capped, we can't drop more than maxDrop)

        // Instead, we keep reps the same but weight will increase via inverse Epley because the target reps are now lower? Actually if they hit top, they should increase weight, but with inverse Epley, if reps stay same, weight stays same. So we need to force a rep drop? 

        // We'll add a small additional drop: we already have a cap, but if they hit top, we can allow one extra drop beyond the cap? 

        // Better: we can just keep it as is; the natural progression is that they will increase weight because they are capable of more weight, but that's not automatic. 

        // We'll add a slight weight boost by increasing intensity? 

        // Instead, we'll increase weight by 2.5 lbs directly (as a bonus) but still keep reps same. However, that would decouple weight again.

        // Let's go with the original idea: if they hit top, we drop reps by 1 (beyond the cap) but only if they consistently hit top.

        // We'll implement a small extra drop.

        newReps = Math.max(1, newReps - 1);

    }



    // Apply muscleâ€‘type rep floor

    const repFloor = getExerciseRepFloor(exercise);

    newReps = Math.max(repFloor, newReps);

    newReps = Math.min(20, Math.max(1, Math.round(newReps)));



    // ---- 5. Compute weight using Epley-Brzycki progression delta (NEW) ----

    const muscleType = getExerciseMuscleType(exercise);

    const muscleSizeMap = { large: 0.9, medium: 1.0, small: 1.15 };

    const muscleSize = muscleSizeMap[muscleType] || 1.0;

    

    // Determine 1RM (history stores reps as an array â€” normalize first, the

    // raw-array arithmetic here used to silently NaN out to the bodyweight fallback)

    let oneRM = workoutData.exercises[exercise.id]?.tested1RM;

    if (!oneRM || oneRM <= 0) {

        const lastRepsFor1RM = Array.isArray(lastSession.reps) ? Math.max(...lastSession.reps) : lastSession.reps;

        oneRM = lastSession.weight * (1 + lastRepsFor1RM / 30);

    }

    if (!oneRM || oneRM <= 0) {

        // fallback to prior

        oneRM = workoutData.user._cache.effectiveBodyWeight * (exercise.strengthIndex || 0.7);

    }

    

    // ---- 5. Weight DERIVED from the rep/sets prescription (reps drive weight) ----

    // The rep/sets curve decides how many reps you do; Epley converts those

    // reps into the %1RM that allows them; the fiber window then clamps the

    // load between the muscle's floor (slow-twitch stimulus) and ceiling

    // (fast-twitch / structural safety). This is the hypertrophy-first flow:

    // many reps at the floor weight -> as weight climbs, reps descend the

    // curve toward the ceiling weight at low reps (strength).

    const lastReps = Array.isArray(lastSession.reps) ? Math.max(...lastSession.reps) : lastSession.reps;

    const fiberRange = computeFiberWeightRange(exercise, oneRM, newReps);

    const targetIntensity = Math.min(0.95, epleyIntensityForReps(newReps));

    let targetWeight = fiberRange ? fiberRange.min : oneRM * targetIntensity;

    

    // Gradual step: move toward the curve target in small plate increments so

    // the weight always tracks the reps without violent jumps. Last session's

    // RPE steers an extra increment (grinding -> ease off; cruising -> add).

    const lastRPEStep = (Array.isArray(lastSession.rpe) ? lastSession.rpe[0] : lastSession.rpe) || 7;

    let newWeight = lastSession.weight;

    const maxStep = Math.min(7.5, Math.max(2.5, lastSession.weight * 0.10));

    if (targetWeight > newWeight) newWeight = newWeight + Math.min(maxStep, targetWeight - newWeight);

    else if (targetWeight < newWeight - 2.5) newWeight = newWeight - Math.min(maxStep, newWeight - targetWeight);

    newWeight = roundToNearest(newWeight, 2.5);

    // Effort post-adjustment: grinding (RPE 9+) eases one plate off even if the

    // curve target matched; cruising (RPE 5-) adds one plate on.

    newWeight = newWeight + (lastRPEStep >= 9 ? -2.5 : (lastRPEStep <= 5 ? 2.5 : 0));

    newWeight = roundToNearest(newWeight, 2.5);

    // Hard caps: fiber ceiling if known, else classic limits

    if (fiberRange && fiberRange.max) newWeight = Math.min(newWeight, fiberRange.max);

    newWeight = Math.min(oneRM * 0.95, lastSession.weight * 1.10, newWeight);

    if (newWeight < 20) newWeight = 20;



    // ---- 6. Rep range wraps the reps the curve decided (weight followed reps) ----

    const muscleRepFloor = getExerciseRepFloor(exercise);

    let repLow = Math.max(muscleRepFloor, Math.max(1, newReps - 1));

    let repHigh = Math.min(20, Math.max(repLow + 1, newReps + 1));

    if (repLow === repHigh) {

        repLow = Math.max(1, repLow - 1);

        repHigh = Math.min(20, repHigh + 1);

    }

    

    const totalWorkouts = workoutData.workouts.length;

    const expressMode = user.settings?.expressMode || false;

    let targetSets = humpSets(targetIntensity, muscleType, totalWorkouts, expressMode);

    targetSets = Math.min(7, Math.max(2, Math.round(targetSets)));

    

    // Cap set change per session

    let setChange = targetSets - lastSession.sets;

    setChange = Math.min(1, Math.max(-1, setChange));

    newSets = lastSession.sets + setChange;

    newSets = Math.min(7, Math.max(2, Math.round(newSets)));



    // ---- 8. Build prescription ----

    if (exercise.prescriptionType === 'time') {

        let duration = exercise.prescribed?.duration || exercise.defaultDuration || 60;

        exercise.prescribed = {

            sets: newSets,

            reps: `${repLow}-${repHigh}`,

            weight: newWeight,

            weightMax: fiberRange ? fiberRange.max : null,

            duration: duration

        };

    } else {

        let repsString;

        if (exercise.prescriptionType === 'amrap') {

            repsString = `${repLow}-${repHigh} (AMRAP)`;

        } else {

            repsString = `${repLow}-${repHigh}`;

        }

        exercise.prescribed = {

            sets: newSets,

            reps: repsString,

            weight: newWeight,

            weightMax: fiberRange ? fiberRange.max : null

        };

    }



    // ---- 9. Progression notes ----

    let notes = `Gradual step from last session on same muscle group (${lastSession.weight} lbs Ã— ${lastSession.reps} reps). `;

    if (bias > 0.2) notes += `Steering toward STRONGER â†’ ${repChange} reps. `;

    else if (bias < -0.2) notes += `Steering toward BIGGER â†’ ${repChange} reps. `;

    else notes += `Neutral steering. `;

    notes += `Aim for ${repHigh} reps before increasing weight. `;

    if (fiberRange) {

        notes += `Fiber window ${fiberRange.lowPct}-${fiberRange.maxPct}% 1RM â€” today's cap ${fiberRange.max} lbs. `;

        if (fiberRange.note) notes += fiberRange.note;

    }

    exercise.progressionNotes = notes;



    return exercise;

}

/**

 * Get up to 5 most recent exercises from history that share at least one muscle group with the given exercise.

 * Returns an array of objects with details for display.

 */

function getSimilarPastExercises(exercise, maxCount = 5) {

    const targetMuscles = new Set(exercise.muscleGroup || []);

    if (targetMuscles.size === 0) return [];

    

    const similar = [];

    const workouts = [...workoutData.workouts].sort((a, b) => new Date(b.date) - new Date(a.date)); // newest first

    

    for (const workout of workouts) {

        for (const ex of workout.exercises) {

            if (ex.id === exercise.id) continue; // skip the same exercise

            if (!ex.actual || ex.skipped) continue; // only completed exercises

            const sharedMuscles = (ex.muscleGroup || []).filter(m => targetMuscles.has(m));

            if (sharedMuscles.length === 0) continue;

            

            // Get best set from actual performance

            const actual = ex.actual;

            let bestWeight = actual.weight;

            let bestReps = 0;

            if (actual.reps && actual.reps.length > 0) {

                bestReps = Math.max(...actual.reps);

            } else if (actual.durations && actual.durations.length > 0) {

                // For time-based, we treat reps as seconds (not ideal but show duration)

                bestReps = Math.max(...actual.durations);

            }

            let rpe = null;

            if (actual.rpe) {

                rpe = Array.isArray(actual.rpe) ? actual.rpe[0] : actual.rpe;

            }

            let rir = null;

            if (actual.rirs && actual.rirs.length > 0) {

                rir = actual.rirs[0];

            }

            

            // Calculate percentage gain from first time this exercise was performed

            const exRecord = workoutData.exercises[ex.id];

            let gainPercent = null;

            if (exRecord && exRecord.history && exRecord.history.length >= 2) {

                const firstEntry = exRecord.history[0];

                const lastEntry = exRecord.history[exRecord.history.length - 1];

                if (firstEntry.weight && lastEntry.weight && firstEntry.weight > 0) {

                    const first1RM = estimate1RM(firstEntry.weight, Math.max(...(firstEntry.reps || [1])));

                    const last1RM = estimate1RM(lastEntry.weight, Math.max(...(lastEntry.reps || [1])));

                    gainPercent = ((last1RM - first1RM) / first1RM) * 100;

                }

            }

            

            similar.push({

                name: ex.name,

                weight: bestWeight,

                reps: bestReps,

                rpe: rpe,

                rir: rir,

                date: workout.date,

                gainPercent: gainPercent

            });

            

            if (similar.length >= maxCount) break;

        }

        if (similar.length >= maxCount) break;

    }

    return similar;

}

/* P5P14: intensity->target anchors used by recalcExerciseFromWeight.

   These were referenced but never defined (pre-existing crash on weight edit). */

const BEGINNER_REP_POINTS = [

    { intensity: 0.30, reps: 20 },

    { intensity: 0.50, reps: 15 },

    { intensity: 0.65, reps: 12 },

    { intensity: 0.75, reps: 10 },

    { intensity: 0.85, reps: 6 },

    { intensity: 0.95, reps: 3 }

];

const BEGINNER_SET_POINTS = [

    { intensity: 0.30, sets: 2 },

    { intensity: 0.60, sets: 3 },

    { intensity: 0.85, sets: 4 },

    { intensity: 0.95, sets: 4 }

];

const ADVANCED_REP_POINTS = [

    { intensity: 0.30, reps: 18 },

    { intensity: 0.50, reps: 12 },

    { intensity: 0.65, reps: 10 },

    { intensity: 0.75, reps: 8 },

    { intensity: 0.85, reps: 5 },

    { intensity: 0.95, reps: 2 }

];

const ADVANCED_SET_POINTS = [

    { intensity: 0.30, sets: 3 },

    { intensity: 0.60, sets: 4 },

    { intensity: 0.85, sets: 5 },

    { intensity: 0.95, sets: 5 }

];



function recalcExerciseFromWeight(index, newWeight) {

    const exercise = currentWorkout.exercises[index];

    if (!exercise) return;



    const parsed = parseFloat(newWeight);

    if (isNaN(parsed) || parsed < 0) return; // ignore invalid



    // Update the prescribed weight

    exercise.prescribed.weight = parsed;



    // Recalculate using the same logic as generateExercisePrescription, but without progression factors

    // We'll reuse the core intensity logic. For simplicity, copy the relevant parts:



    const exData = workoutData.exercises[exercise.id] || {};

    const historyArray = exData.history || [];

    const lastSession = historyArray[historyArray.length - 1];

    const user = workoutData.user;



    // 1. Estimate 1RM (same as before)

    let estimated1RM = exData.bestWeight ? exData.bestWeight : (lastSession ? lastSession.weight : parsed);

    if (lastSession && lastSession.reps && lastSession.reps.length > 0) {

        const bestReps = Math.max(...lastSession.reps);

        estimated1RM = estimate1RM(lastSession.weight, bestReps);

    } else if (exData.bestWeight && exData.bestReps) {

        estimated1RM = estimate1RM(exData.bestWeight, exData.bestReps);

    }

    estimated1RM = Math.max(estimated1RM, parsed);

    

    // ---- NEW: Limbâ€‘length adjustment for tall users on armâ€‘dominant exercises ----

    const leverageSensitiveExercises = ['bench_press', 'overhead_press', 'bicep_curl', 'tricep_extension', 'dumbbell_press', 'incline_bench', 'skull_crusher'];

    if (leverageSensitiveExercises.includes(exercise.id)) {

        const height = workoutData.user.height || 70;

        const threshold = workoutData.user.gender === 'female' ? 70 : 74; // inches

        if (height > threshold) {

            estimated1RM *= 0.95; // 5% reduction

        }

    }

    // -------------------------------------------------------------------------

    

    let intensity = parsed / estimated1RM;

    intensity = Math.min(intensity, 0.95);



    // 2. Maturity (same as in generateExercisePrescription)

    const totalWorkouts = workoutData.workouts.length;

    const maturity = 1 - Math.exp(-totalWorkouts / 100);



    // 3. Goal factor

    let goalFactor = 1.0;

    if (user.goal === 'strength') goalFactor = 0.9;

    else if (user.goal === 'hypertrophy') goalFactor = 1.1;

    else if (user.goal === 'endurance') goalFactor = 1.2;



    // 4. Anchor curves (using global constants)

    const beginnerRepPoints = BEGINNER_REP_POINTS;

    const beginnerSetPoints = BEGINNER_SET_POINTS;

    const advancedRepPoints = ADVANCED_REP_POINTS;

    const advancedSetPoints = ADVANCED_SET_POINTS;



    function interpolate(points, x) {

        if (x <= points[0].intensity) return points[0].reps ?? points[0].sets;

        if (x >= points[points.length-1].intensity) return points[points.length-1].reps ?? points[points.length-1].sets;

        for (let i = 0; i < points.length - 1; i++) {

            if (x >= points[i].intensity && x <= points[i+1].intensity) {

                const t = (x - points[i].intensity) / (points[i+1].intensity - points[i].intensity);

                const startVal = points[i].reps ?? points[i].sets;

                const endVal = points[i+1].reps ?? points[i+1].sets;

                return startVal + t * (endVal - startVal);

            }

        }

        return points[0].reps ?? points[0].sets;

    }



    const beginnerReps = interpolate(beginnerRepPoints, intensity);

    const beginnerSets = interpolate(beginnerSetPoints, intensity);

    const advancedReps = interpolate(advancedRepPoints, intensity);

    const advancedSets = interpolate(advancedSetPoints, intensity);



    let targetReps = (1 - maturity) * beginnerReps + maturity * advancedReps;

    let targetSets = (1 - maturity) * beginnerSets + maturity * advancedSets;



    targetReps *= goalFactor;



    let sets = Math.max(1, Math.min(10, Math.round(targetSets)));

    let repsTarget = Math.max(1, Math.min(20, Math.round(targetReps)));

    const rangeWidth = maturity < 0.5 ? 2 : 1;

    let repRangeLow = Math.max(1, repsTarget - rangeWidth);

    let repRangeHigh = Math.min(20, repsTarget + rangeWidth);



    // ----- Minimum rep floor for beginners (first 8 workouts) -----

    const totalWorkoutsCompleted = workoutData.workouts.length;

    const isStillBeginner = (user.experience === 'beginner' || user.experience === 'intermediate') && totalWorkoutsCompleted < 8;

    if (isStillBeginner && (user.goal === 'balanced' || user.goal === 'hypertrophy')) {

        // Force reps to never drop below 12

        if (repRangeLow < 12) repRangeLow = 12;

        if (repRangeHigh < 12) repRangeHigh = 12;

    }



    if (exercise.prescriptionType === 'time') {

        // Preserve existing duration if already set; otherwise use default

        if (!exercise.prescribed.duration) {

            exercise.prescribed.duration = exercise.defaultDuration || 60;

        }

        // Optionally, you could scale duration based on weight change here.

        // For now, we keep duration constant.

    } else {

        if (exercise.prescriptionType === 'amrap') {

            exercise.prescribed.reps = `${repRangeLow}-${repRangeHigh} (AMRAP)`;

        } else {

            exercise.prescribed.reps = `${repRangeLow}-${repRangeHigh}`;

        }

    }



    // Optionally update the performance form (if it's open)

    const perfForm = document.getElementById(`performanceForm_${index}`);

    if (perfForm && perfForm.parentElement.classList.contains('active')) {

        // If the logging panel is open, refresh its contents

        createPerformanceForm(index, exercise);

    }



    // Also update the progression notes? Possibly not needed, but could be nice.

    // For simplicity, we skip updating the notes here â€“ they remain from the original generation.

}





// =====================================================================

// 11-COMPONENT ATHLETIC BLOCKS

// Warm-up (dynamic mobility) -> component drill finisher -> flexibility

// cooldown. Every generated workout touches the health & skill components:

// strength + muscular endurance come from the main lifts, one skill

// component is emphasized per rotation day (see workoutProgram.splits),

// and every session ends with stretches for the muscles that were trained.

// =====================================================================

function pickDrillFromGroup(groupKey, excludeIds) {

    const pool = (ultimateExerciseLibrary[groupKey] || []).filter(ex => {

        const id = ex.name.toLowerCase().replace(/\s/g, '_');

        return !excludeIds || !excludeIds.includes(id);

    });

    if (pool.length === 0) return null;

    return pool[Math.floor(Math.random() * pool.length)];

}



function buildDrillExercise(base) {

    const id = base.name.toLowerCase().replace(/\s/g, '_');

    const exercise = {

        id: id,

        name: base.name,

        muscleGroup: base.muscles || [],

        prescribed: {

            sets: base.defaultSets || 3,

            reps: base.defaultReps || '10',

            weight: null

        },

        actual: null,

        progressionNotes: base.progression || '',

        equipment: base.equipment || 'none',

        instructions: base.instructions || [],

        prescriptionType: base.prescriptionType || 'reps',

        noFatigue: true,

        fitnessComponents: base.fitnessComponents || []

    };

    if (base.defaultDuration) {

        exercise.defaultDuration = base.defaultDuration;

        exercise.prescribed.duration = base.defaultDuration;

    }

    if (typeof base.strengthIndex === 'number') exercise.strengthIndex = base.strengthIndex;

    return exercise;

}



function appendAthleticBlocks(workout, isDeload, isTaper) {

    if (!workout || !workout.exercises) return;

    const split = workoutProgram.splits.find(s => s.id === workout.type) || null;

    const usedIds = workout.exercises.map(e => e.id);



    // 1) Dynamic warm-up â€” one mobility drill, 2 sets (skipped on deload days)

    if (!isDeload) {

        const warmBase = pickDrillFromGroup('mobility_dynamic', usedIds);

        if (warmBase) {

            const warm = buildDrillExercise(warmBase);

            warm.prescribed.sets = 2;

            warm.isWarmup = true;

            warm.progressionNotes = 'Dynamic warm-up â€” move smoothly, no straining.';

            workout.exercises.unshift(warm);

        }

    }



    // 2) Component drill finisher(s) â€” the split's skill emphasis for the day

    if (split && split.drills && !isDeload) {

        for (const groupKey of split.drills) {

            const base = pickDrillFromGroup(groupKey, usedIds);

            if (!base) continue;

            const drill = buildDrillExercise(base);

            drill.isComponentDrill = true;

            drill.component = split.component || '';

            if (isTaper && drill.prescribed.sets) {

                drill.prescribed.sets = Math.max(2, Math.round(drill.prescribed.sets * 0.6));

            }

            workout.exercises.push(drill);

        }

    }



    // 3) Flexibility cooldown â€” static stretches for the muscles trained today

    const trainedMuscles = new Set();

    workout.exercises.forEach(e => (e.muscleGroup || []).forEach(m => trainedMuscles.add(m)));

    const stretchCount = isDeload ? 4 : 2;

    const pickedStretchIds = [];

    for (let i = 0; i < stretchCount; i++) {

        const pool = (ultimateExerciseLibrary.stretching_static || []).filter(ex => {

            const id = ex.name.toLowerCase().replace(/\s/g, '_');

            if (pickedStretchIds.includes(id)) return false;

            return (ex.muscles || []).some(m => trainedMuscles.has(m));

        });

        if (pool.length === 0) {

            // Few muscle-specific matches left: top up from any remaining

            // stretch (extra mobility always helps, never hurts a deload)

            const anyPool = (ultimateExerciseLibrary.stretching_static || []).filter(ex =>

                !pickedStretchIds.includes(ex.name.toLowerCase().replace(/\s/g, '_')));

            if (anyPool.length === 0) break;

            const base = anyPool[Math.floor(Math.random() * anyPool.length)];

            pickedStretchIds.push(base.name.toLowerCase().replace(/\s/g, '_'));

            const stretch = buildDrillExercise(base);

            stretch.isCooldown = true;

            stretch.progressionNotes = 'Cooldown stretch â€” breathe slowly, never bounce.';

            workout.exercises.push(stretch);

            continue;

        }

        const base = pool[Math.floor(Math.random() * pool.length)];

        pickedStretchIds.push(base.name.toLowerCase().replace(/\s/g, '_'));

        const stretch = buildDrillExercise(base);

        stretch.isCooldown = true;

        stretch.progressionNotes = 'Cooldown stretch â€” breathe slowly, never bounce.';

        workout.exercises.push(stretch);

    }

}



