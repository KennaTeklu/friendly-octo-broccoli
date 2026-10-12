// ============================================================
// reference/scripts/app/02-erf-kalman.js
// Extracted from reference/scripts/app.js
// Original lines: 486-1993  (1508 lines)
// Section: ERF model, Kalman 1RM, EWMA, Bayesian
// ============================================================

// ==================== CIG ADDITIONS ====================

// ERF Lookup Table (100Ã—100) â€“ bilinear interpolation will be used

// For brevity, we'll include a small sample; the full table should be loaded from JSON.

// In practice, you'd embed the full table as a constant. We'll define a placeholder.



// ==================== ENHANCED ERF MODEL (Expert Consensus 2026) ====================

// This model provides both mean RIR estimates and confidence intervals based on

// metaâ€‘analytical data (Halperin 2021, Zourdos 2021, Zourdos 2023, Ruizâ€‘Alias 2024).



const ERF_MODEL = {

    // Anchor points: intensity -> { mean, sd } of max reps

    anchors: [

        { intensity: 1.00, mean: 1, sd: 0.2 },

        { intensity: 0.95, mean: 2, sd: 0.5 },

        { intensity: 0.90, mean: 4, sd: 0.8 },

        { intensity: 0.85, mean: 6, sd: 1.0 },

        { intensity: 0.80, mean: 8, sd: 1.3 },

        { intensity: 0.75, mean: 10, sd: 1.6 },

        { intensity: 0.70, mean: 12, sd: 2.0 },

        { intensity: 0.65, mean: 15, sd: 2.5 },

        { intensity: 0.60, mean: 18, sd: 3.0 },

        { intensity: 0.55, mean: 20, sd: 3.0 }

    ],



    // Linear interpolation between anchor points

    interpolate(intensity, property) {

        if (intensity >= 1.0) return this.anchors[0][property];

        if (intensity <= 0.55) return this.anchors[this.anchors.length-1][property];

        

        for (let i = 0; i < this.anchors.length - 1; i++) {

            if (intensity >= this.anchors[i+1].intensity && 

                intensity <= this.anchors[i].intensity) {

                const t = (intensity - this.anchors[i+1].intensity) / 

                         (this.anchors[i].intensity - this.anchors[i+1].intensity);

                return this.anchors[i+1][property] + 

                       t * (this.anchors[i][property] - this.anchors[i+1][property]);

            }

        }

        return 10; // fallback

    },



    // Main function to get RIR distribution for a set

    getRIR(weight, reps, intensity, setNumber, experience = 'experienced') {

        const maxRepsMean = this.interpolate(intensity, 'mean');

        const maxRepsSD = this.interpolate(intensity, 'sd');

        

        // Base RIR mean

        let rirMean = Math.max(0, maxRepsMean - reps);

        

        // Bias correction (Halperin 2021)

        if (reps / maxRepsMean < 0.8) {

            rirMean += 0.95; // underâ€‘prediction bias

        }

        

        // Combine population variability with measurement error (Zourdos 2023)

        let rirSD = Math.sqrt(Math.pow(maxRepsSD, 2) + Math.pow(2.21, 2));

        

        // Set number adjustment (Halperin 2021)

        const setBoost = Math.min(0.5, setNumber * 0.07);

        rirSD *= (1 - setBoost);

        

        // Proximityâ€‘toâ€‘failure effect (Zourdos 2021)

        if (rirMean > 3) {

            rirSD *= 1.2; // increased uncertainty further from failure

        }

        

        return {

            mean: rirMean,

            sd: rirSD,

            lower90: Math.max(0, rirMean - 1.645 * rirSD),

            upper90: rirMean + 1.645 * rirSD

        };

    }

};

      

// Exercise parameters (strengthIndex, alpha)

const EXERCISE_PARAMS = {

    default: { strengthIndex: 1.0, alpha: 0.10 },

    squat: { strengthIndex: 1.5, alpha: 0.15 },

    bench_press: { strengthIndex: 1.2, alpha: 0.10 },

    deadlift: { strengthIndex: 1.8, alpha: 0.15 },

    overhead_press: { strengthIndex: 0.8, alpha: 0.10 },

    barbell_row: { strengthIndex: 1.0, alpha: 0.10 },

    pull_up: { strengthIndex: 1.0, alpha: 0.08 },

    bicep_curl: { strengthIndex: 0.4, alpha: 0.08 },

    tricep_extension: { strengthIndex: 0.4, alpha: 0.08 },

    // Add all exercises from your library with appropriate values

};



function getExerciseParams(exerciseId) {

    return EXERCISE_PARAMS[exerciseId] || EXERCISE_PARAMS.default;

}

      

function initExerciseRecursiveFields(exerciseId) {

    const ex = workoutData.exercises[exerciseId];

    if (!ex) return;

    

    // Only initialize if fields are missing

    if (ex.mu === undefined) {

        const params = getExerciseParams(exerciseId);

        const userWeight = workoutData.user.weight || 150;

        ex.mu = params.strengthIndex * userWeight;

        ex.sigma2 = Math.pow(0.2 * ex.mu, 2);

        ex.residuals = [];

        ex.avg_mu_W = 1.0;

        ex.techniqueFactor = 1.0;

        ex.lastUpdate = null;

        ex._wHistory = [];

    }

}





// Helper to get exercise parameters

function getExerciseParams(exerciseId) {

  return EXERCISE_PARAMS[exerciseId] || EXERCISE_PARAMS.default;

}



function initializeExerciseStats() {

    Object.keys(workoutData.exercises).forEach(exId => {

        initExerciseRecursiveFields(exId);

    });

}

      

/**

 * Eq1: Recursive 1RM update (Studentâ€‘t with adaptive noise)

 * @param {string} exerciseId - ID of the exercise

 * @param {number} weight - Weight used (lbs)

 * @param {number} reps - Reps performed (for the set)

 * @param {number} [rpe] - RPE of the set (1â€‘10); optional, but improves estimation

 */

function updateRecursive1RM(exerciseId, weight, reps, rpe = null) {

    const ex = workoutData.exercises[exerciseId];

    if (!ex) return;



    // ----- 1. Handle bodyweight exercises -----

    let effectiveWeight = weight;

    const exercise = getExerciseById(exerciseId);

    if (exercise && (exercise.equipment === 'bodyweight' ||

        exercise.name.toLowerCase().includes('pull') ||

        exercise.name.toLowerCase().includes('push') ||

        exercise.name.toLowerCase().includes('dip') ||

        exercise.name.toLowerCase().includes('chin'))) {

        // Use user bodyweight + added weight

        const userWeight = workoutData.user.weight || 150;

        effectiveWeight = userWeight + (weight || 0);

    }



    if (effectiveWeight <= 0) return;



    // ----- 2. Compute the observed 1RM (using Epley) -----

    const observed = effectiveWeight * (1 + reps / 30);

    const e = observed - ex.mu;   // innovation



    // ----- 3. Estimate the measurement noise variance (nu) -----

    let nu;

    if (ex.residuals.length < 20) {

        // Initial guess: 20% of mu

        nu = Math.pow(0.2 * ex.mu, 2);

    } else {

        // Use median absolute deviation of recent residuals

        const residuals = ex.residuals;

        const sorted = [...residuals].sort((a, b) => a - b);

        const median = sorted[Math.floor(sorted.length / 2)];

        const absoluteDeviations = residuals.map(r => Math.abs(r - median));

        const madSorted = [...absoluteDeviations].sort((a, b) => a - b);

        const mad = madSorted[Math.floor(madSorted.length / 2)];

        // MAD -> standard deviation assuming Gaussian (1.4826 factor)

        nu = Math.pow(1.4826 * mad, 2);

    }



    // ----- 4. Adjust noise based on RPE (lower noise for highâ€‘effort sets) -----

    let rpeFactor = 1.0;

    if (rpe !== null && rpe >= 1 && rpe <= 10) {

        if (rpe >= 9) rpeFactor = 0.3;      // very high confidence

        else if (rpe >= 7) rpeFactor = 0.6; // moderate confidence

        else rpeFactor = 1.2;               // lower confidence for easy sets

    }

    nu = nu * rpeFactor;



    // ----- 5. Kalman gain (K) and update mu & sigmaÂ² -----

    const K = ex.sigma2 / (ex.sigma2 + nu);

    ex.mu += K * e;

    ex.sigma2 = (1 - K) * ex.sigma2 + K * e * e;



    // ----- 6. Clamp to reasonable bounds -----

    if (ex.mu < 50) ex.mu = 50;

    // Prevent sigmaÂ² from blowing up

    ex.sigma2 = Math.min(ex.sigma2, Math.pow(0.3 * ex.mu, 2));



    // ----- 7. Store residual and maintain history -----

    ex.residuals.push(e);

    if (ex.residuals.length > 20) ex.residuals.shift();

    ex.lastUpdate = new Date().toISOString();



    // ----- 8. Also update bestWeight/bestReps for convenience (optional) -----

    if (!ex.bestWeight || effectiveWeight > ex.bestWeight) {

        ex.bestWeight = effectiveWeight;

        ex.bestReps = reps;

    } else if (Math.abs(effectiveWeight - ex.bestWeight) < 0.1 && reps > (ex.bestReps || 0)) {

        ex.bestReps = reps;

    }

    // Quick cap to prevent immediate corruption

    const maxPlausible = (workoutData.user.weight || 150) * (workoutData.user.experience === 'advanced' ? 5 : 3.5);

    if (ex.mu > maxPlausible) {

        ex.mu = maxPlausible;

        ex.sigma2 = Math.pow(0.2 * ex.mu, 2);

    }

}

      

// Eq7: Update muscle fatigue after a set (or after a workout)

// Note: `reps` should be the total volume (total reps for repsâ€‘based, total seconds for timeâ€‘based)

function updateMuscleFatigue(exerciseId, weight, reps, rpe, timestamp, phaseMultiplier = 1.0) {

    const exercise = getExerciseById(exerciseId);

    if (!exercise) return;

    if (exercise.noFatigue) return; // stretches & athletic drills aid recovery, never tax it

    const muscles = exercise.muscles;

    if (!muscles || muscles.length === 0) return;



    // Calculate stress: (weight * volume) / 10000 * (rpe/10)

    // Volume = total reps or total seconds. For bodyweight exercises, weight = 0 â†’ stress ~0.

    const userWeight = workoutData.user.weight || 150;

    const effectiveWeight = (weight === 0) ? userWeight * 0.7 : weight;

    const S_total = (effectiveWeight * reps) / 10000 * (rpe / 10) * phaseMultiplier;

    const clampedS = Math.min(1, Math.max(0, S_total));

    const fastFrac = rpe > 7 ? 0.7 : 0.3;

    const slowFrac = 1 - fastFrac;



    const now = timestamp ? new Date(timestamp) : new Date();



    muscles.forEach(muscle => {

        let f = workoutData.muscleFatigue[muscle];

        if (!f) {

            f = { fast: 0, slow: 0, lastUpdate: null };

            workoutData.muscleFatigue[muscle] = f;

        }



        const decay = MUSCLE_DECAY[muscle] || DEFAULT_DECAY;

        const Î»_fast = decay.fast;

        const Î»_slow = decay.slow;



        // If this is the first time the muscle is trained, simply set fatigue

        if (f.lastUpdate === null) {

            f.fast = clampedS * fastFrac;

            f.slow = clampedS * slowFrac;

        } else {

            const last = new Date(f.lastUpdate);

            let dt = (now - last) / (1000 * 60 * 60); // hours

            if (dt > 168) dt = 168; // cap at one week



            const df = Math.exp(-Î»_fast * dt);

            const ds = Math.exp(-Î»_slow * dt);



            f.fast = f.fast * df + (clampedS * fastFrac / Î»_fast) * (1 - df);

            f.slow = f.slow * ds + (clampedS * slowFrac / Î»_slow) * (1 - ds);

        }



        f.lastUpdate = now.toISOString();



        f.fast = Math.min(1, Math.max(0, f.fast));

        f.slow = Math.min(1, Math.max(0, f.slow));

    });

}



// Recompute aggregates from workout history (call after each workout)

function recomputeAggregates() {

    // Ensure aggregates object exists

    if (!workoutData.user.aggregates) {

        workoutData.user.aggregates = {

            totalVolumeLast7Days: 0,

            avgRPELast7Days: 0,

            volumeByMuscleLast60Days: {}

        };

    }

    const now = new Date();

    const sevenDaysAgo = new Date(now); sevenDaysAgo.setDate(now.getDate() - 7);

    const sixtyDaysAgo = new Date(now); sixtyDaysAgo.setDate(now.getDate() - 60);



    let totalVolume7 = 0;

    let totalRPE7 = 0;

    let setCount7 = 0;

    const volumeByMuscle60 = {};



    workoutData.workouts.forEach(workout => {

        const wDate = new Date(workout.date);

        const isRecent7 = wDate >= sevenDaysAgo;

        const isRecent60 = wDate >= sixtyDaysAgo;



        workout.exercises.forEach(ex => {

            if (!ex.actual || ex.skipped) return;

            if (ex.noFatigue) return; // conditioning/stretch work stays out of volume & RPE aggregates

            const actual = ex.actual;

            const repsTotal = actual.reps ? actual.reps.reduce((a,b)=>a+b,0) : 0;

            const volume = actual.weight * repsTotal;

            if (isRecent7) {

                totalVolume7 += volume;

                if (actual.rpe) {

                    if (Array.isArray(actual.rpe)) {

                        actual.rpe.forEach(r => { totalRPE7 += r; setCount7++; });

                    } else {

                        totalRPE7 += actual.rpe * actual.sets;

                        setCount7 += actual.sets;

                    }

                }

            }

            if (isRecent60) {

                const muscles = ex.muscleGroup || [];

                muscles.forEach(muscle => {

                    if (!volumeByMuscle60[muscle]) volumeByMuscle60[muscle] = 0;

                    volumeByMuscle60[muscle] += volume;

                });

            }

        });

    });



    workoutData.user.aggregates.totalVolumeLast7Days = totalVolume7;

    workoutData.user.aggregates.avgRPELast7Days = setCount7 > 0 ? totalRPE7 / setCount7 : 5;

    workoutData.user.aggregates.volumeByMuscleLast60Days = volumeByMuscle60;

}



// Helper to determine if exercise is compound (used in rest inference)

function isCompound(exerciseId) {

    const compoundList = ['squat', 'deadlift', 'bench', 'press', 'row'];

    return compoundList.some(key => exerciseId.includes(key));

}



// Eq5: Estimate rest time from performance decay in a single exercise

function inferRestTime(exercise) {

    if (!exercise.actual || !exercise.actual.reps || exercise.actual.reps.length < 2) return null;

    const reps = exercise.actual.reps;

    const weight = exercise.actual.weight;

    const r1 = reps[0];

    const rN = reps[reps.length - 1];

    if (r1 <= 0) return null;

    const Î”r = Math.max(1, r1 - rN);

    const avgRPE = Array.isArray(exercise.actual.rpe) 

        ? exercise.actual.rpe.reduce((a,b)=>a+b,0) / exercise.actual.rpe.length

        : exercise.actual.rpe;

    const Ï„0 = isCompound(exercise.id) ? 120 : 90;

    const restEst = Ï„0 * Math.pow(1 + 0.3 * Î”r / r1, -1) * Math.exp(0.1 * (avgRPE - 7));

    return Math.min(300, Math.max(30, restEst));

}

      

// Eq6: Update body weight estimate based on strength gains

function updateBodyWeightEstimate() {

    const user = workoutData.user;

    if (!user.height) return;

    const H = user.height;

    const squat = workoutData.exercises['squat']?.mu || 0;

    const bench = workoutData.exercises['bench_press']?.mu || 0;

    const deadlift = workoutData.exercises['deadlift']?.mu || 0;

    let S = 0, count = 0;

    if (squat > 0) { S += squat; count++; }

    if (bench > 0) { S += bench; count++; }

    if (deadlift > 0) { S += deadlift; count++; }

    if (count === 0) return;

    S /= count;

    user.strengthComposite = S;



    const wrist = 7.0, ankle = 9.0; // median inches (you can expand later)

    const maxLeanMass = (H * H * (wrist / Math.PI) * (ankle / Math.PI) * 0.5) / 100;

    const sGenetic = 3.0 * maxLeanMass;

    user.geneticPotential = { maxLeanMass, sGenetic };



    const firstWorkout = workoutData.workouts[0]?.date;

    if (!firstWorkout) return;

    const daysSinceStart = Math.floor((new Date() - new Date(firstWorkout)) / (1000*60*60*24));



    let neuralScale = 1;

    if (user.neuralPhaseEnd) {

        const neuralEnd = new Date(user.neuralPhaseEnd);

        if (new Date() < neuralEnd) {

            neuralScale = daysSinceStart / 56;

        }

    } else {

        user.neuralPhaseEnd = new Date(new Date(firstWorkout).getTime() + 56*24*60*60*1000).toISOString();

    }



    const BW_genetic = maxLeanMass * Math.tanh(S / sGenetic) * neuralScale;

    const BW_initial = user.bodyWeightHistory.find(e => e.source === 'entry')?.weight || user.weight || 150;

    const BW_est = BW_genetic + BW_initial * Math.exp(-0.01 * daysSinceStart);



    const lastEst = user.bodyWeightHistory.slice().reverse().find(e => e.source === 'estimate');

    let BW_final = BW_est;

    if (lastEst) {

        BW_final = 0.9 * lastEst.weight + 0.1 * BW_est;

    }

    user.bodyWeightHistory.push({ date: new Date().toISOString(), weight: BW_final, source: 'estimate' });

    if (user.bodyWeightHistory.length > 100) user.bodyWeightHistory.shift();

}

      

// Eq9: Compute progression potential for a given exercise

function computeProgressionPotential(exerciseId, lastSet) {

    const ex = workoutData.exercises[exerciseId];

    if (!ex) return 0;

    const exercise = getExerciseById(exerciseId);

    if (!exercise) return 0;



    const W = lastSet.weight;

    const mu = ex.mu;

    const rpe = lastSet.rpe;

    const age = calculateAge(workoutData.user.birthDate) || 30;

    const gender = workoutData.user.gender;



    const primaryMuscles = exercise.muscleGroup || [];

    let totalVol = 0;

    primaryMuscles.forEach(m => {

        totalVol += workoutData.user.aggregates.volumeByMuscleLast60Days[m] || 0;

    });

    const CSA = (totalVol / 60) * (1 - W/mu);

    const CSA_clamped = Math.min(1, Math.max(0, CSA));



    const P_TK = 1 / (1 + Math.exp(-3 * (W/mu - 0.6)));

    const A_myo = Math.tanh(4 * W/mu);

    const NE = (W/mu) / (rpe/10);

    const NE_clamped = Math.min(1, Math.max(0, NE));



    let CF = 0;

    if (lastSet.restEstimate > 0 && lastSet.firstRPE) {

        CF = (lastSet.rpe - lastSet.firstRPE) / lastSet.restEstimate * (1 / (1 + Math.exp(-0.5*(age-50))));

        CF = Math.min(1, Math.max(0, CF));

    }



    let Î²_as = 1 + 0.01 * (25 - age);

    if (age >= 60) Î²_as = 1 + 0.01*(25-60) - 0.02*(age-60);

    Î²_as = Math.min(1.2, Math.max(0.8, Î²_as));

    if (gender === 'female') Î²_as -= 0.05;



    let fatigueSum = 0;

    primaryMuscles.forEach(m => {

        fatigueSum += getCoupledFatigue(m);

    });

    const F_total = primaryMuscles.length ? fatigueSum / primaryMuscles.length : 0;



    const H = (1 - Math.exp(-0.01 * CSA_clamped)) / (1 + Math.exp(-0.5 * P_TK));

    const M = A_myo / (1 + 0.2 * F_total);

    const N = NE_clamped / (1 + 0.3 * CF);

    const I = 0.05 * H * M;



    let Î¦ = H * M * N * Î²_as + I;

    const phase = getCurrentCyclePhase(); // today's phase

    const phaseMult = getPhaseMultiplier(phase) || 1.0;

    Î¦ *= phaseMult;

    return Math.min(1, Math.max(0, Î¦));

}

      

function getTargetReps(goal) {

    const map = {

        hypertrophy: 10,

        strength: 5,

        endurance: 17,

        longevity: 12,

        balanced: 8

    };

    return map[goal] || 8;

}



// Eq10: Compute next recommended weight for an exercise

function computeNextWeight(exerciseId) {

    const ex = workoutData.exercises[exerciseId];

    if (!ex) return null;

    const exercise = getExerciseById(exerciseId);

    if (!exercise) return null;



    const last = ex.history?.slice().reverse().find(h => !h.skipped);

    if (!last) return null;

    const W = last.weight;

    const mu = ex.mu;

    const r = last.reps ? Math.max(...last.reps) : 0;

    const r_target = getTargetReps(workoutData.user.goal);



    const totalVol7 = workoutData.user.aggregates.totalVolumeLast7Days || 0;

    const avgRPE7 = workoutData.user.aggregates.avgRPELast7Days || 5;

    const FD = (1 - Math.exp(-0.01 * totalVol7)) * (avgRPE7 / 10);



    // For Î¦ we need a lastSet object with restEstimate and firstRPE â€“ use defaults for now

    const lastSet = {

        weight: W,

        rpe: last.rpe,

        restEstimate: last.restEstimate || 0,

        firstRPE: last.firstRPE || last.rpe

    };

    const Î¦ = computeProgressionPotential(exerciseId, lastSet);



    let Î”W = 5 * Î¦ * (1 - W/mu) * Math.tanh((r - r_target) / r_target) * Math.exp(-0.5 * FD);

    Î”W = Math.min(Î”W, 0.05 * W, 10);

    if (Î”W < 0) Î”W = 0;

    const W_next = W + Î”W;

    return { weight: W_next, delta: Î”W, confidence: 2 * Math.sqrt(ex.sigma2) };

}

      

function postWorkoutUpdate(workout) {

    const phase = getCurrentCyclePhase(workout.date);

    const phaseMult = getPhaseMultiplier(phase) || 1.0;

    workout.exercises.forEach(ex => {

        if (!ex.actual || ex.skipped) return;

        const actual = ex.actual;

        if (actual.reps && actual.reps.length > 0) {

            const avgReps = actual.reps.reduce((a,b)=>a+b,0) / actual.reps.length;

            updateMuscleFatigue(ex.id, actual.weight, avgReps, actual.rpe, workout.date, phaseMult);

        }

    });



    recomputeAggregates();



    workout.exercises.forEach(ex => {

        if (!ex.actual || ex.skipped) return;

        const restEst = inferRestTime(ex);

        if (restEst) {

            if (!workoutData.exercises[ex.id].restEstimates) {

                workoutData.exercises[ex.id].restEstimates = [];

            }

            workoutData.exercises[ex.id].restEstimates.push(restEst);

            if (workoutData.exercises[ex.id].restEstimates.length > 5) {

                workoutData.exercises[ex.id].restEstimates.shift();

            }

        }

    });



    learnCouplingFromWorkout(workout);

    updateBodyWeightEstimate();



    Object.keys(workoutData.exercises).forEach(exId => {

        const next = computeNextWeight(exId);

        if (next) {

            workoutData.exercises[exId].nextWeight = next.weight;

            workoutData.exercises[exId].nextWeightConfidence = next.confidence;

        }

    });



    updateWeeklyVolumeTracking(workout);



    // ----- Muscleâ€‘group EWMA volume tracking (Carla + Kaelen) -----

    if (!workoutData.user.muscleEWMA) {

        workoutData.user.muscleEWMA = {};

    }

    const alpha = 0.3; // smoothing factor (30% weight to current workout)

    const muscleEWMA = workoutData.user.muscleEWMA;



    // Decay all existing EWMAs based on days since last update

    const now = new Date();

    const lastUpdate = workoutData.user.lastEWMAUpdate ? new Date(workoutData.user.lastEWMAUpdate) : now;

    const daysSinceLast = (now - lastUpdate) / (1000 * 60 * 60 * 24);

    if (daysSinceLast > 0) {

        const decayFactor = Math.pow(1 - alpha, daysSinceLast);

        for (let muscle in muscleEWMA) {

            muscleEWMA[muscle] *= decayFactor;

        }

    }



    // Calculate volume per muscle for this workout (distribute exercise volume equally among its target muscles)

    const volumeThisWorkout = {};

    workout.exercises.forEach(ex => {

        if (!ex.actual || ex.skipped) return;

        const actual = ex.actual;

        const repsTotal = actual.reps ? actual.reps.reduce((a,b)=>a+b,0) : 0;

        const volume = actual.weight * repsTotal;

        const muscles = ex.muscleGroup || [];

        if (muscles.length === 0) return;

        const perMuscleVolume = volume / muscles.length;

        muscles.forEach(muscle => {

            volumeThisWorkout[muscle] = (volumeThisWorkout[muscle] || 0) + perMuscleVolume;

        });

    });



    // Update EWMA for each muscle

    for (let muscle in volumeThisWorkout) {

        const oldVal = muscleEWMA[muscle] || 0;

        muscleEWMA[muscle] = oldVal * (1 - alpha) + volumeThisWorkout[muscle] * alpha;

    }



    // Store last update timestamp

    workoutData.user.lastEWMAUpdate = now.toISOString();

        

        updateMuscleDeloadStatus(workout);

        // ----- Update Bayesian formula probabilities

    updateFormulaProbabilitiesFromWorkout(workout);

        saveToLocalStorage();

}

/**

 * Updates Bayesian probabilities for 1RM formulas based on logged sets.

 * Call this inside postWorkoutUpdate after each workout.

 */

function updateFormulaProbabilitiesFromWorkout(workout) {

    if (!workoutData.user.formulaProbabilities) {

        workoutData.user.formulaProbabilities = { epley: 1/3, brzycki: 1/3, lombardi: 1/3 };

    }

    let totalSets = 0;

    let errors = { epley: 0, brzycki: 0, lombardi: 0 };

    

    workout.exercises.forEach(ex => {

        if (!ex.actual || ex.skipped) return;

        const actual = ex.actual;

        if (actual.weight && actual.reps && actual.reps.length > 0) {

            const weight = actual.weight;

            const reps = Math.max(...actual.reps);

            // Use the stored tested 1RM as ground truth if available, otherwise estimate from the set itself using a robust method (geometric mean of formulas)

            const record = workoutData.exercises[ex.id] || {};

            let true1RM = record.tested1RM;

            if (!true1RM && record.successWeight && record.failWeight) {

                true1RM = Math.sqrt(record.successWeight * record.failWeight);

            }

            if (!true1RM && weight && reps) {

                // Fallback: use Epley as approximate truth

                true1RM = weight * (1 + reps / 30);

            }

            if (true1RM) {

                const epleyEst = weight * (1 + reps / 30);

                const brzyckiEst = (reps <= 10) ? weight * 36 / (37 - reps) : epleyEst;

                const lombardiEst = weight * Math.pow(reps, 0.1);

                errors.epley += Math.pow((epleyEst - true1RM) / true1RM, 2);

                errors.brzycki += Math.pow((brzyckiEst - true1RM) / true1RM, 2);

                errors.lombardi += Math.pow((lombardiEst - true1RM) / true1RM, 2);

                totalSets++;

            }

        }

    });

    

    if (totalSets > 0) {

        // Convert errors to likelihoods (smaller error = higher likelihood)

        const likelihood = {

            epley: Math.exp(-errors.epley / totalSets),

            brzycki: Math.exp(-errors.brzycki / totalSets),

            lombardi: Math.exp(-errors.lombardi / totalSets)

        };

        const sumLikelihood = likelihood.epley + likelihood.brzycki + likelihood.lombardi;

        workoutData.user.formulaProbabilities.epley = likelihood.epley / sumLikelihood;

        workoutData.user.formulaProbabilities.brzycki = likelihood.brzycki / sumLikelihood;

        workoutData.user.formulaProbabilities.lombardi = likelihood.lombardi / sumLikelihood;

        

        saveToLocalStorage();

    }

}

      

// Hebbian learning for muscle coupling

function learnCouplingFromWorkout(workout) {

    if (!workoutData.user.settings.muscleCoupling) return; // only learn if feature is enabled



    const LEARNING_RATE = 0.001;      // small increment per coâ€‘activation

    const MAX_COUPLING = 0.2;         // maximum allowed coupling between any two muscles

    const DECAY = 0.999;              // gradual decay per workout to prevent explosion



    // Ensure couplingMatrix exists

    if (!workoutData.couplingMatrix) workoutData.couplingMatrix = {};



    // Collect all muscles that were trained in this workout

    const musclesTrained = new Set();

    workout.exercises.forEach(ex => {

        if (!ex.actual || ex.skipped) return;

        (ex.muscleGroup || []).forEach(m => musclesTrained.add(m));

    });



    // Convert to array for pairwise combinations

    const muscleArray = Array.from(musclesTrained);

    if (muscleArray.length < 2) return; // nothing to learn



    // Increment coupling for each pair

    for (let i = 0; i < muscleArray.length; i++) {

        for (let j = i + 1; j < muscleArray.length; j++) {

            const m1 = muscleArray[i];

            const m2 = muscleArray[j];



            // Initialize objects if needed

            if (!workoutData.couplingMatrix[m1]) workoutData.couplingMatrix[m1] = {};

            if (!workoutData.couplingMatrix[m2]) workoutData.couplingMatrix[m2] = {};



            // Increment both directions (symmetric)

            let val1 = (workoutData.couplingMatrix[m1][m2] || 0) + LEARNING_RATE;

            let val2 = (workoutData.couplingMatrix[m2][m1] || 0) + LEARNING_RATE;



            // Apply decay to all existing couplings (optional, can be done once per workout)

            // We'll do a global decay before increments to keep values bounded.

            // But to keep this simple, we'll cap after increment.



            // Cap

            workoutData.couplingMatrix[m1][m2] = Math.min(val1, MAX_COUPLING);

            workoutData.couplingMatrix[m2][m1] = Math.min(val2, MAX_COUPLING);

        }

    }



    // Optional: apply a small decay to all entries to prevent indefinite growth

    // This is a bit heavy â€“ we can skip for now and rely on capping.

}

      

// Migration function for Phase 2 data structures

function migrateToPhase2() {

    Object.keys(workoutData.exercises).forEach(exId => {

        const ex = workoutData.exercises[exId];

        if (ex.restEstimates === undefined) ex.restEstimates = [];

        if (ex.nextWeight === undefined) ex.nextWeight = null;

    });

    if (!workoutData.user.aggregates) {

        workoutData.user.aggregates = { totalVolumeLast7Days:0, avgRPELast7Days:0, volumeByMuscleLast60Days:{} };

    }

}



function computeRIR(exerciseId, weight, reps, rpe, setNumber) {

    const ex = workoutData.exercises[exerciseId];

    if (!ex) return null;

    const params = getExerciseParams(exerciseId);

    const mu = ex.mu;

    const avg_mu_W = ex.avg_mu_W;

    const techFactor = ex.techniqueFactor;

    let I = weight / mu;

    if (I < 0.4) I = 0.4;

    if (I > 1.0) I = 1.0;



    // Get RIR distribution from the enhanced model

    const rirDist = ERF_MODEL.getRIR(weight, reps, I, setNumber);

    let rir = rirDist.mean; // base RIR before adjustments



    // Apply tempo and RPE calibration factors (same as before)

    // Note: The model already includes set number effects, so we may want to remove the old decay factor.

    // But to be safe, we keep the tempo factor (which is separate from set number).

    const expectedRPE = 10 - rir; // approximate expected RPE based on mean RIR

    const rpeDiff = rpe - expectedRPE;

    const totalWorkouts = workoutData.workouts.length;

    const beta = totalWorkouts > 20 ? 0.2 : 0.1;

    const rpeFactor = 1 + beta * rpeDiff / 10;

    const v_norm = (mu / weight) / avg_mu_W;

    const v_clamped = Math.max(0.5, Math.min(2.0, v_norm));

    const gamma = 0.3;

    const tempoFactor = 1 + gamma * (v_clamped - 1);



    // Final RIR (apply tempo and RPE calibration)

    let finalRIR = rir * rpeFactor * tempoFactor * techFactor;

    if (finalRIR < 0) finalRIR = 0;

    if (finalRIR > 10) finalRIR = 10;



    // Update moving averages (unchanged)

    ex.avg_mu_W = 0.9 * ex.avg_mu_W + 0.1 * (mu / weight);

    if (!ex._wHistory) ex._wHistory = [];

    ex._wHistory.push(mu / weight);

    if (ex._wHistory.length > 10) ex._wHistory.shift();

    if (ex._wHistory.length >= 3) {

        const mean = ex._wHistory.reduce((a,b)=>a+b,0) / ex._wHistory.length;

        const variance = ex._wHistory.reduce((a,b)=>a + (b-mean)**2, 0) / (ex._wHistory.length-1);

        const cv = Math.sqrt(variance) / mean;

        ex.techniqueFactor = 1 / (1 + cv);

        ex.techniqueFactor = Math.max(0.8, Math.min(1.0, ex.techniqueFactor));

    }



    // Store uncertainty for potential UI use

    ex.lastRIR_uncertainty = rirDist.sd;



    return finalRIR;

}



// Eq4: Failure status

function isFailure(rir, setNumber, totalSets, rpe) {

    if (rir <= 0.5) return true;

    if (setNumber === totalSets && rir <= 1.5 && rpe >= 9) return true;

    return false;

}

      

