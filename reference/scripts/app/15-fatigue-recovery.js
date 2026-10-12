// ============================================================
// reference/scripts/app/15-fatigue-recovery.js
// Extracted from reference/scripts/app.js
// Original lines: 70558-73019  (2462 lines)
// Section: Systemic fatigue, recovery section, training readiness
// ============================================================

// ==================== SYSTEMIC FATIGUE (NEW) ====================

/**

 * Calculate systemic recovery factor (0.0â€“1.0) based on last 7 days of training.

 * 1.0 = fully recovered, 0.5 = very fatigued.

 */

function calculateSystemicRecoveryFactor() {

    try {

        const now = new Date();

        const sevenDaysAgo = new Date(now);

        sevenDaysAgo.setDate(now.getDate() - 7);



        // Collect workouts in last 7 days

        const recentWorkouts = workoutData.workouts.filter(w => new Date(w.date) >= sevenDaysAgo);



        if (recentWorkouts.length === 0) return 1.0; // no fatigue



        let totalVolume = 0;

        let totalSets = 0;

        let rpeSum = 0;

        let rpeCount = 0;

        const userWeight = workoutData.user.weight || 150;



        recentWorkouts.forEach(w => {

            w.exercises?.forEach(ex => {

                if (ex.actual && !ex.skipped) {

                    const actual = ex.actual;

                    if (ex.prescriptionType === 'time') {

                        if (actual.durations) {

                            totalVolume += actual.durations.reduce((a, b) => a + b, 0);

                        }

                    } else {

                        if (actual.weight && actual.reps) {

                            const totalReps = actual.reps.reduce((a, b) => a + b, 0);

                            totalVolume += actual.weight * totalReps;

                        }

                    }

                    totalSets += actual.sets || 0;

                    if (actual.rpe) {

                        // Handle array or single number

                        if (Array.isArray(actual.rpe)) {

                            rpeSum += actual.rpe.reduce((a, b) => a + b, 0);

                            rpeCount += actual.rpe.length;

                        } else {

                            rpeSum += actual.rpe;

                            rpeCount++;

                        }

                    }

                }

            });

        });



        const relativeVolume = totalVolume / userWeight;

        const avgRPE = rpeCount ? rpeSum / rpeCount : 5;



        // Fatigue index (heuristic, tunable)

        const fatigueFromFreq = Math.min(50, recentWorkouts.length * 10);

        const fatigueFromVolume = Math.min(30, relativeVolume / 500);

        const fatigueFromRPE = Math.min(20, (avgRPE - 5) * 4);

        let totalFatigue = fatigueFromFreq + fatigueFromVolume + fatigueFromRPE;



        // Clamp and convert to recovery factor (0.5 to 1.0)

        totalFatigue = Math.min(100, Math.max(0, totalFatigue));

        let factor = 1.0 - (totalFatigue / 200);

        factor = Math.min(1.0, Math.max(0.5, factor));

        

        // Final sanity â€“ ensure it's a number

        return isNaN(factor) ? 0.8 : factor;

    } catch (e) {

        console.warn("calculateSystemicRecoveryFactor error:", e);

        return 0.8; // safe default

    }

}



// ==================== UPDATED RECOVERY SECTION ====================

function updateRecoverySection() {

        applyFatigueDecay();

    // 1. Update lastâ€‘trained timestamps

    calculateMuscleLastTrained();



    // 2. Get systemic recovery factor (0â€“100)

    const systemicFactor = calculateSystemicRecoveryFactor();

    const systemicPercent = Math.round(systemicFactor * 100);



    // 3. Calculate weighted muscle recovery (old logic, kept)

    const allMuscles = getAllMuscleGroups();

    let totalWeight = 0;

    let weightedRecovery = 0;

    const muscleWeights = {

        quads: 2.0, hamstrings: 1.8, glutes: 1.8, chest: 1.5, back: 1.8,

        shoulders: 1.2, biceps: 0.8, triceps: 0.8, calves: 0.6, core: 1.0,

        forearms: 0.5, traps: 0.7, lats: 1.5, rear_delts: 0.6, obliques: 0.5,

        hip_flexors: 0.5, adductors: 0.6, abductors: 0.6, erectors: 1.2,

        serratus: 0.4, neck: 0.4, deep_neck: 0.3, levator_scap: 0.3,

        rhomboids: 0.5, teres: 0.4, infraspinatus: 0.4, supraspinatus: 0.4,

        subscapularis: 0.4, brachialis: 0.3, brachioradialis: 0.3,

        anconeus: 0.2, supinator: 0.2, pronator: 0.2, pec_minor: 0.3,

        coracobrach: 0.2, popliteus: 0.3, tibialis: 0.3, soleus: 0.5,

        peroneus_tertius: 0.2, articularis_genus: 0.2, multifidus: 0.5,

        transverse: 0.5, quadratus_plantae: 0.2, hand_lumbricals: 0.1,

        hand_interossei: 0.1, thenar: 0.1, foot_intrinsics: 0.1,

        foot_interossei: 0.1, abductor_hallucis: 0.1, flexor_brevis: 0.1

    };



    allMuscles.forEach(muscle => {

        const lastTrained = muscleLastTrained[muscle.name];

        if (!lastTrained) {

            weightedRecovery += (muscleWeights[muscle.name] || 1.0) * 100;

            totalWeight += (muscleWeights[muscle.name] || 1.0);

            return;

        }

        const daysSince = daysSinceTrained(muscle.name);

        const recoveryPercent = Math.min(100, (daysSince / muscle.restDays) * 100);

        const weight = muscleWeights[muscle.name] || 1.0;

        weightedRecovery += weight * recoveryPercent;

        totalWeight += weight;

    });



    const muscleAvg = totalWeight > 0 ? weightedRecovery / totalWeight : 100;



    // 4. Combine muscle and systemic recovery (70% muscle, 30% systemic)

    const overallRecovery = Math.round(0.7 * muscleAvg + 0.3 * systemicPercent);

  

        window.lastOverallRecovery = overallRecovery;



    // 5. Determine readiness status and message

    let readinessColor, readinessText, recommendation;

    if (overallRecovery >= 85) {

        readinessColor = 'var(--success)';

        readinessText = 'Excellent';

        recommendation = 'You are fully recovered. Great time for a heavy workout or PR attempt!';

    } else if (overallRecovery >= 70) {

        readinessColor = 'var(--warning)';

        readinessText = 'Good';

        recommendation = 'Recovery is good. You can train hard, but listen to your body.';

    } else if (overallRecovery >= 50) {

        readinessColor = 'var(--warning)';

        readinessText = 'Moderate';

        recommendation = 'Some fatigue remains. Consider a lighter workout or focus on skill work.';

    } else if (overallRecovery >= 30) {

        readinessColor = 'var(--danger)';

        readinessText = 'Low';

        recommendation = 'Significant fatigue. A deload workout, mobility, or rest day is advised.';

    } else {

        readinessColor = 'var(--danger)';

        readinessText = 'Very Low';

        recommendation = 'You are heavily fatigued. Take a rest day or do light recovery work.';

    }



    // 6. Update or create the global recovery display

    const recoverySection = document.querySelector('#recovery-section .todays-workout');

    if (!recoverySection) return;



    let globalDiv = document.getElementById('globalRecovery');

    if (!globalDiv) {

        globalDiv = document.createElement('div');

        globalDiv.id = 'globalRecovery';

        globalDiv.className = 'recovery-status';

        recoverySection.insertBefore(globalDiv, recoverySection.firstChild);

    }



    globalDiv.innerHTML = `

        <h3 style="margin-bottom: 15px; color: var(--dark);">

            <i class="fas fa-chart-pie"></i> Overall Readiness

        </h3>

        <div style="display: flex; align-items: center; gap: 20px; flex-wrap: wrap;">

            <div style="flex: 1; min-width: 150px;">

                <div class="score-circle" style="width: 80px; height: 80px; margin: 0;">

                    <div class="score-circle-inner" style="width: 60px; height: 60px; font-size: 1.5rem;">

                        ${overallRecovery}

                    </div>

                </div>

            </div>

            <div style="flex: 3;">

                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">

                    <span style="font-weight: 600; color: var(--gray-700);">Recovery</span>

                    <span style="font-weight: 700; color: ${readinessColor};">${readinessText}</span>

                </div>

                <div class="progress-bar" style="margin: 10px 0;">

                    <div class="progress-fill" style="width: ${overallRecovery}%; background: ${readinessColor};"></div>

                </div>

                <p style="color: var(--gray-600); margin-top: 10px;">

                    <i class="fas fa-lightbulb" style="color: ${readinessColor};"></i>

                    ${recommendation}

                </p>

            </div>

        </div>

    `;



    // Update circle gradient

    const circle = globalDiv.querySelector('.score-circle');

    if (circle) {

        let color;

        if (overallRecovery >= 80) color = 'var(--success)';

        else if (overallRecovery >= 50) color = 'var(--warning)';

        else color = 'var(--danger)';

        circle.style.background = `conic-gradient(${color} 0% ${overallRecovery}%, var(--gray-300) ${overallRecovery}% 100%)`;

    }



    // 7. Call muscle breakdown and recommendations (they may also use systemic factor)

    updateMuscleRecoveryDisplay();

    updateRecoveryRecommendations(); // this function can now optionally use systemicFactor

}

function sanitizeExerciseMu() {

    const user = workoutData.user;

    const userWeight = (user.bodyWeightHistory && user.bodyWeightHistory.length)

        ? user.bodyWeightHistory[user.bodyWeightHistory.length - 1].weight

        : (user.weight || 150);

    let maxMultiplier = (user.experience === 'advanced') ? 5.0 : 3.5;

    

    Object.entries(workoutData.exercises).forEach(([exId, ex]) => {

        if (!ex.mu || ex.mu <= 0) return;

        

        // If there is a tested 1RM, trust it (but don't corrupt it)

        if (ex.tested1RM && ex.tested1RM > 0 && ex.tested1RM < userWeight * maxMultiplier) {

            if (Math.abs(ex.mu - ex.tested1RM) > ex.tested1RM * 0.5) {

                console.log(`Sanitizing ${exId}: mu ${ex.mu} â†’ ${ex.tested1RM}`);

                ex.mu = ex.tested1RM;

                ex.sigma2 = Math.pow(0.2 * ex.mu, 2);

                ex.residuals = [];

            }

            return;

        }

        

        // Never set mu below 50 lbs (safety floor)

        const minPlausible = 50;

        const maxPlausible = userWeight * maxMultiplier;

        

        if (ex.mu > maxPlausible) {

            let newMu = null;

            if (ex.history && ex.history.length > 0) {

                let best1RM = 0;

                ex.history.forEach(entry => {

                    if (entry.weight && entry.reps && entry.reps.length) {

                        const bestReps = Math.max(...entry.reps);

                        const est = entry.weight * (1 + bestReps / 30);

                        if (est > best1RM) best1RM = est;

                    }

                });

                if (best1RM > 0 && best1RM <= maxPlausible) newMu = best1RM;

            }

            if (!newMu) {

                const libEx = getExerciseById(exId);

                let strengthIndex = libEx?.strengthIndex || 0.7;

                newMu = userWeight * strengthIndex;

                if (newMu > maxPlausible) newMu = maxPlausible;

            }

            if (newMu && newMu >= minPlausible) {

                console.log(`Sanitizing ${exId}: mu ${ex.mu} â†’ ${Math.round(newMu)}`);

                ex.mu = newMu;

                ex.sigma2 = Math.pow(0.2 * ex.mu, 2);

                ex.residuals = [];

            }

        }

        

        // Final absolute floor (never below 50)

        if (ex.mu < minPlausible) {

            console.log(`Sanitizing ${exId}: mu ${ex.mu} â†’ ${minPlausible} (low floor)`);

            ex.mu = minPlausible;

            ex.sigma2 = Math.pow(0.2 * ex.mu, 2);

            ex.residuals = [];

        }

    });

}

/**

 * Calculates a motivational date when the user might reach elite strength.

 * Uses: current strength composite, consistency, progression potential,

 * genetic potential, fatigue, and recent RPE/RIR trends.

 * Returns an object: { medianDate, confidenceLow, confidenceHigh, message, total1RM, targetTotal }

 */

function calculateProjectedEliteDate() {

    const user = workoutData.user;

    const workouts = workoutData.workouts;

    const exercises = workoutData.exercises;



    // If no workouts exist, show placeholder

    if (!workouts || workouts.length === 0) {

        console.warn("No workout history â€“ returning placeholder date");

        const placeholder = new Date();

        placeholder.setFullYear(placeholder.getFullYear() + 5);

        return {

            medianDate: placeholder,

            confidenceLow: new Date(placeholder),

            confidenceHigh: new Date(placeholder),

            message: "Complete your first workout to see your projection! ðŸ’ª",

            total1RM: 0,

            targetTotal: 900

        };

    }



    console.log("=== ELITE DATE DEBUG ===");

    console.log("Workouts count:", workouts.length);

    console.log("Exercises keys:", Object.keys(exercises));



    // ----- 1. Current estimated total (SBD) â€“ only count lifts with â‰¥3 history entries -----

    const keyLifts = ['squat', 'bench_press', 'deadlift'];

    let total1RM = 0;

    let count = 0;

    const userWeight = (user.bodyWeightHistory && user.bodyWeightHistory.length)

        ? user.bodyWeightHistory[user.bodyWeightHistory.length - 1].weight

        : (user.weight || 150);



    let suspicious = false;

    keyLifts.forEach(lift => {

        const ex = exercises[lift];

        if (ex && ex.mu && ex.mu > 0) {

            // Sanity: if mu > 3x bodyweight for a beginner/intermediate, it's likely corrupted

            if (ex.mu > userWeight * 3 && (user.experience === 'beginner' || user.experience === 'intermediate')) {

                suspicious = true;

            }

            // Only count if it has at least 3 history entries

            if (ex.history && ex.history.length >= 3) {

                total1RM += ex.mu;

                count++;

            }

        }

    });



    if (suspicious) {

        total1RM = (user.strengthComposite && user.strengthComposite < 800) ? user.strengthComposite : 0;

        count = 0;

    } else if (count === 0 && user.strengthComposite) {

        total1RM = user.strengthComposite;

    } else if (count > 0) {

        total1RM = total1RM / count * 3;

    }



    // Cap unrealistic total

    if (total1RM > userWeight * 9) total1RM = 0;



    // If not enough data (count === 0 after all checks), show placeholder

    if (count === 0 || total1RM === 0) {

        const placeholder = new Date();

        placeholder.setFullYear(placeholder.getFullYear() + 5);

        return {

            medianDate: placeholder,

            confidenceLow: new Date(placeholder),

            confidenceHigh: new Date(placeholder),

            message: "Log at least 3 sessions of squat, bench, and deadlift to see your projection.",

            total1RM: 0,

            targetTotal: 900

        };

    }



    // ----- 2. Target elite total (genderâ€‘specific) -----

    const bodyWeight = userWeight; // already have it

    const isFemale = (user.gender === 'female');

    let targetTotal;

    if (bodyWeight < 132) {

        targetTotal = isFemale ? 400 : 850;

    } else if (bodyWeight < 165) {

        targetTotal = isFemale ? 500 : 1000;

    } else {

        targetTotal = isFemale ? 600 : 1100;

    }

    console.log("Body weight:", bodyWeight, "Target total:", targetTotal);



    // ----- 3. Consistency -----

    const now = new Date();

    const eightWeeksAgo = new Date(now);

    eightWeeksAgo.setDate(now.getDate() - 56);

    const workoutsLast8Weeks = workouts.filter(w => new Date(w.date) >= eightWeeksAgo).length;

    const consistency = Math.min(1.0, workoutsLast8Weeks / 24);

    console.log("Workouts last 8 weeks:", workoutsLast8Weeks, "Consistency:", consistency);



    // ----- 4. Progression potential (Î¦) -----

    let phiSum = 0, phiCount = 0;

    for (let i = workouts.length - 1; i >= 0 && phiCount < 10; i--) {

        const workout = workouts[i];

        if (!workout.exercises) continue;

        for (const ex of workout.exercises) {

            if (ex.actual && !ex.skipped && ex.actual.reps && ex.actual.reps.length) {

                const lastSet = {

                    weight: ex.actual.weight,

                    rpe: Array.isArray(ex.actual.rpe) ? ex.actual.rpe[ex.actual.rpe.length-1] : ex.actual.rpe,

                    restEstimate: ex.actual.restEstimate || 60,

                    firstRPE: ex.actual.firstRPE || ex.actual.rpe

                };

                const phi = computeProgressionPotential(ex.id, lastSet);

                console.log(`Exercise ${ex.id}: phi = ${phi}`);

                if (!isNaN(phi) && isFinite(phi)) {

                    phiSum += phi;

                    phiCount++;

                }

            }

        }

    }

    const avgPhi = phiCount ? phiSum / phiCount : 0.2;

    console.log("Avg Phi:", avgPhi, "from", phiCount, "sets");



    // ----- 5. Systemic fatigue -----

    let systemic = 0.8;

    if (typeof calculateSystemicRecoveryFactor === 'function') {

        systemic = calculateSystemicRecoveryFactor();

    } else {

        console.warn("calculateSystemicRecoveryFactor not defined");

    }

    console.log("Systemic recovery factor:", systemic);

    const fatiguePenalty = 1 - (1 - systemic) * 0.5;

    console.log("Fatigue penalty:", fatiguePenalty);



    // ----- 6. Weekly gain -----

    const progressionRate = user.settings?.progressionRate || 0.02;

    let weeklyGainPercent = progressionRate * avgPhi * consistency * fatiguePenalty;

    weeklyGainPercent = Math.min(0.03, Math.max(0.002, weeklyGainPercent));

    console.log("Weekly gain %:", weeklyGainPercent);



    // ----- 7. Weeks needed -----

    const remaining = targetTotal - total1RM;

    console.log("Remaining to target:", remaining);

    if (remaining <= 0) {

        if (total1RM > 0 && total1RM < userWeight * 9 && total1RM >= targetTotal * 0.9) {

            return {

                medianDate: new Date(),

                confidenceLow: new Date(),

                confidenceHigh: new Date(),

                message: "You're already elite! ðŸ† Keep inspiring others.",

                total1RM, targetTotal

            };

        } else {

            const placeholderDate = new Date();

            placeholderDate.setFullYear(placeholderDate.getFullYear() + 5);

            return {

                medianDate: placeholderDate,

                confidenceLow: new Date(placeholderDate),

                confidenceHigh: new Date(placeholderDate),

                message: "Not enough reliable data. Keep training â€“ estimate will appear soon!",

                total1RM: 0,

                targetTotal: targetTotal

            };

        }

    }

    const carrying = targetTotal * 1.2;

    const weeks = Math.log(1 + remaining / (carrying - total1RM)) / Math.log(1 + weeklyGainPercent);

    const weeksMedian = Math.max(4, weeks);

    console.log("Weeks needed (median):", weeksMedian);



    // ----- 8. Confidence intervals (capped) -----

    const weeklyCounts = [];

    for (let i = 0; i < 8; i++) {

        const weekStart = new Date(eightWeeksAgo);

        weekStart.setDate(eightWeeksAgo.getDate() + i * 7);

        const weekEnd = new Date(weekStart);

        weekEnd.setDate(weekStart.getDate() + 6);

        const cnt = workouts.filter(w => {

            const d = new Date(w.date);

            return d >= weekStart && d <= weekEnd;

        }).length;

        weeklyCounts.push(cnt);

    }

    const avgW = weeklyCounts.reduce((a,b)=>a+b,0)/8;

    const variance = weeklyCounts.reduce((a,b)=>a + Math.pow(b-avgW,2),0)/8;

    const uncertainty = Math.min(1.3, 1 + Math.min(0.3, variance / 10));

    let weeksLow = weeksMedian * 0.6 * uncertainty;

    let weeksHigh = weeksMedian * 1.8 * uncertainty;

    weeksHigh = Math.min(weeksHigh, weeksMedian + 104);

    weeksLow = Math.max(weeksLow, 4);

    console.log("Uncertainty factor:", uncertainty, "Weeks low/high:", weeksLow, weeksHigh);



    // ----- 9. Dates -----

    const today = new Date();

    const medianDate = new Date(today);

    medianDate.setDate(today.getDate() + Math.round(weeksMedian * 7));

    const lowDate = new Date(today);

    lowDate.setDate(today.getDate() + Math.round(weeksLow * 7));

    const highDate = new Date(today);

    highDate.setDate(today.getDate() + Math.round(weeksHigh * 7));

    console.log("Median date:", medianDate);



    // ----- 10. Motivational message -----

    let message = "";

    if (consistency < 0.3) message = "Consistency is your superpower. Keep showing up!";

    else if (avgPhi < 0.15) message = "Push a little harder on your last reps â€“ you'll grow faster.";

    else if (weeklyGainPercent > 0.02) message = "You're on fire! This date could come sooner. ðŸ”¥";

    else message = "Every workout brings you closer. You've got this! ðŸ’ª";



    // Safety

    let safeMedian = medianDate instanceof Date && !isNaN(medianDate) ? medianDate : new Date();

    let safeLow = lowDate instanceof Date && !isNaN(lowDate) ? lowDate : new Date();

    let safeHigh = highDate instanceof Date && !isNaN(highDate) ? highDate : new Date();

    const safeTotal = (typeof total1RM === 'number' && !isNaN(total1RM)) ? total1RM : 0;

    const safeTarget = (typeof targetTotal === 'number' && !isNaN(targetTotal)) ? targetTotal : 900;



    return {

        medianDate: safeMedian,

        confidenceLow: safeLow,

        confidenceHigh: safeHigh,

        message: message,

        total1RM: safeTotal,

        targetTotal: safeTarget

    };

}

// ==================== UPDATED RECOMMENDATIONS (optional enhancement) ====================

function updateRecoveryRecommendations() {

    const container = document.getElementById('recoveryRecommendations');

    if (!container) return;



    // ----- Systemic fatigue message (new) -----

    const systemicFactor = calculateSystemicRecoveryFactor();

    const systemicPercent = Math.round(systemicFactor * 100);

    let systemicMessage = '';

    if (systemicFactor < 0.6) {

        systemicMessage = `<div style="margin-bottom: 15px; padding: 10px; background: rgba(245,158,11,0.1); border-left: 4px solid var(--warning);">

            <i class="fas fa-exclamation-triangle"></i> Your systemic recovery is low (${systemicPercent}%). Consider a deload or light workout.

        </div>`;

    } else if (systemicFactor > 0.9) {

        systemicMessage = `<div style="margin-bottom: 15px; padding: 10px; background: rgba(16,185,129,0.1); border-left: 4px solid var(--success);">

            <i class="fas fa-check-circle"></i> You're fully recovered! Great time to push hard.

        </div>`;

    }



    // ----- Original muscleâ€‘based recommendations -----

    const readyMuscles = [];

    const almostReadyMuscles = [];      // within 1 day of being ready

    const restingMuscles = [];

    const neverTrainedMuscles = [];

    const priorityReady = [];            // ready muscles with aging risk or large muscle groups



    const allMuscles = getAllMuscleGroups();



    allMuscles.forEach(muscle => {

        const lastTrained = muscleLastTrained[muscle.name];

        if (!lastTrained) {

            neverTrainedMuscles.push(muscle);

            return;

        }

        const daysSince = daysSinceTrained(muscle.name);

        if (daysSince >= muscle.restDays) {

            readyMuscles.push(muscle);

            // Mark as priority if it has aging risk or is a major muscle group

            if (muscle.agingRisk === 'high' || 

                muscle.category === 'major' || 

                muscle.name === 'core' || 

                muscle.name === 'glutes') {

                priorityReady.push(muscle);

            }

        } else if (daysSince >= muscle.restDays - 1) {

            almostReadyMuscles.push(muscle);

        } else {

            restingMuscles.push({ muscle, daysLeft: muscle.restDays - daysSince });

        }

    });



    // Sort priorityReady by importance (agingRisk high first, then by name)

    priorityReady.sort((a, b) => {

        if (a.agingRisk === 'high' && b.agingRisk !== 'high') return -1;

        if (a.agingRisk !== 'high' && b.agingRisk === 'high') return 1;

        return a.display.localeCompare(b.display);

    });



    // Build HTML

    let html = '';



    // Overall summary

    const totalReady = readyMuscles.length;

    const totalAlmost = almostReadyMuscles.length;

    const totalResting = restingMuscles.length;

    const totalNever = neverTrainedMuscles.length;



    html += `

        <div style="display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 20px;">

            <span class="badge badge-success">âœ“ Ready: ${totalReady}</span>

            <span class="badge badge-warning">â³ Almost: ${totalAlmost}</span>

            <span class="badge badge-info">ðŸ’¤ Resting: ${totalResting}</span>

            ${totalNever > 0 ? `<span class="badge badge-danger">âŒ Never trained: ${totalNever}</span>` : ''}

        </div>

    `;



    // Priority ready muscles

    if (priorityReady.length > 0) {

        html += `

            <div style="margin-bottom: 15px;">

                <h4 style="color: var(--dark); margin-bottom: 8px; font-size: 1.1rem;">

                    <i class="fas fa-star" style="color: var(--warning);"></i> Priority muscles to train today

                </h4>

                <p style="color: var(--gray-700); margin-bottom: 10px;">

                    ${priorityReady.slice(0, 5).map(m => m.display).join(', ')}

                    ${priorityReady.length > 5 ? ` and ${priorityReady.length - 5} more` : ''}

                </p>

                <button class="btn btn-success btn-sm" onclick="generateWorkoutForMuscles('${priorityReady.map(m => m.name).join(',')}')" style="padding: 8px 16px; font-size: 0.9rem;">

                    <i class="fas fa-bolt"></i> Generate workout for these

                </button>

            </div>

        `;

    }



    // Never trained muscles (especially longevity)

    const neverLongevity = neverTrainedMuscles.filter(m => 

        m.category === 'longevity' || m.category === 'hands' || m.category === 'feet'

    );

    if (neverLongevity.length > 0) {

        html += `

            <div style="margin-bottom: 15px; padding: 12px; background: rgba(245, 158, 11, 0.1); border-radius: 8px; border-left: 4px solid var(--warning);">

                <i class="fas fa-exclamation-triangle" style="color: var(--warning); margin-right: 8px;"></i>

                <strong>Longevity Alert:</strong> You've never trained ${neverLongevity.length} joint health muscles 

                (${neverLongevity.slice(0, 3).map(m => m.display).join(', ')}${neverLongevity.length > 3 ? '...' : ''}).

                <div style="margin-top: 10px;">

                    <button class="btn btn-warning btn-sm" onclick="generateLongevityWorkout()">

                        <i class="fas fa-user-md"></i> Generate Longevity Workout

                    </button>

                </div>

            </div>

        `;

    }



    // Almost ready muscles

    if (almostReadyMuscles.length > 0) {

        html += `

            <div style="margin-bottom: 15px;">

                <h4 style="color: var(--dark); margin-bottom: 5px; font-size: 1rem;">

                    <i class="fas fa-hourglass-half" style="color: var(--warning);"></i> Almost ready (train tomorrow)

                </h4>

                <p style="color: var(--gray-600); font-size: 0.9rem;">

                    ${almostReadyMuscles.map(m => m.display).join(', ')}

                </p>

            </div>

        `;

    }



    // Resting muscles summary

    if (restingMuscles.length > 0) {

        const sortedRest = restingMuscles.sort((a, b) => a.daysLeft - b.daysLeft);

        html += `

            <div style="margin-bottom: 15px;">

                <h4 style="color: var(--dark); margin-bottom: 5px; font-size: 1rem;">

                    <i class="fas fa-bed" style="color: var(--info);"></i> Still resting

                </h4>

                <p style="color: var(--gray-600); font-size: 0.9rem;">

                    ${sortedRest.slice(0, 5).map(r => `${r.muscle.display} (${r.daysLeft}d left)`).join(', ')}

                    ${sortedRest.length > 5 ? ` and ${sortedRest.length - 5} more` : ''}

                </p>

            </div>

        `;

    }



    // If everything is recovered

    if (readyMuscles.length === 0 && almostReadyMuscles.length === 0 && restingMuscles.length === 0 && neverTrainedMuscles.length === 0) {

        html = `<p style="color: var(--gray-600);">All muscle groups are properly recovered. Great job!</p>`;

    }



    // Add a quick action to generate a balanced workout

    html += `

        <div style="margin-top: 20px; display: flex; gap: 10px; flex-wrap: wrap;">

            <button class="btn" onclick="generateNextWorkout()">

                <i class="fas fa-dumbbell"></i> Generate any workout

            </button>

            <button class="btn btn-info" onclick="showSection('workout')">

                <i class="fas fa-eye"></i> View today's workout

            </button>

        </div>

    `;



    // Combine systemic message with muscle recommendations

    container.innerHTML = systemicMessage + html;

}



    function updateMuscleRecoveryDisplay() {

        const isMobile = window.innerWidth <= 768;



    // Helper to compute category stats

    function computeCategoryStats(muscles) {

        let totalRecovery = 0;

        let readyCount = 0;

        let closeCount = 0;

        let restingCount = 0;

        let neverTrainedCount = 0;



        muscles.forEach(muscle => {

            const lastTrained = muscleLastTrained[muscle.name];

            if (!lastTrained) {

                neverTrainedCount++;

                totalRecovery += 100; // assume ready

                readyCount++;

                return;

            }

            const daysSince = Math.floor((new Date() - new Date(lastTrained)) / (1000 * 60 * 60 * 24));

            const recoveryPercent = Math.min(100, (daysSince / muscle.restDays) * 100);

            totalRecovery += recoveryPercent;



            if (daysSince >= muscle.restDays) readyCount++;

            else if (daysSince >= muscle.restDays - 1) closeCount++;

            else restingCount++;

        });



        const avgRecovery = muscles.length ? Math.round(totalRecovery / muscles.length) : 0;

        return { avgRecovery, readyCount, closeCount, restingCount, neverTrainedCount, total: muscles.length };

    }



    // Helper to create a category header with stats

    function createCategoryHeader(categoryName, iconClass, stats, categoryId) {

        const headerDiv = document.createElement('div');

        headerDiv.className = 'category-header';

        headerDiv.style.marginBottom = '15px';

        headerDiv.style.display = 'flex';

        headerDiv.style.flexWrap = 'wrap';

        headerDiv.style.alignItems = 'center';

        headerDiv.style.justifyContent = 'space-between';



        const titleSpan = document.createElement('span');

        titleSpan.innerHTML = `<i class="${iconClass}"></i> ${categoryName}`;

        titleSpan.style.fontWeight = '600';

        titleSpan.style.color = 'var(--primary)';

        titleSpan.style.fontSize = '1.1rem';



        const statsSpan = document.createElement('span');

        statsSpan.style.display = 'flex';

        statsSpan.style.gap = '10px';

        statsSpan.style.flexWrap = 'wrap';

        statsSpan.style.alignItems = 'center';



        // Ready badge

        const readyBadge = document.createElement('span');

        readyBadge.className = 'badge badge-success';

        readyBadge.textContent = `${stats.readyCount} ready`;



        // Close badge

        const closeBadge = document.createElement('span');

        closeBadge.className = 'badge badge-warning';

        closeBadge.textContent = `${stats.closeCount} soon`;



        // Resting badge

        const restingBadge = document.createElement('span');

        restingBadge.className = 'badge badge-info';

        restingBadge.textContent = `${stats.restingCount} resting`;



        // Never trained badge (if any)

        if (stats.neverTrainedCount > 0) {

            const neverBadge = document.createElement('span');

            neverBadge.className = 'badge badge-danger';

            neverBadge.textContent = `${stats.neverTrainedCount} never trained`;

            statsSpan.appendChild(neverBadge);

        }



        statsSpan.appendChild(readyBadge);

        statsSpan.appendChild(closeBadge);

        statsSpan.appendChild(restingBadge);



        // Add small progress bar for average recovery

        const progressContainer = document.createElement('div');

        progressContainer.style.flex = '1 1 100%';

        progressContainer.style.marginTop = '8px';

        progressContainer.innerHTML = `

            <div style="display: flex; justify-content: space-between; font-size: 0.8rem; color: var(--gray-600);">

                <span>Category recovery</span>

                <span>${stats.avgRecovery}%</span>

            </div>

            <div class="progress-bar" style="height: 6px;">

                <div class="progress-fill" style="width: ${stats.avgRecovery}%; background: var(--primary);"></div>

            </div>

        `;



        headerDiv.appendChild(titleSpan);

        headerDiv.appendChild(statsSpan);

        headerDiv.appendChild(progressContainer);



        return headerDiv;

    }



    // Helper to sort muscles within category

    function sortMuscles(muscles) {

        return muscles.sort((a, b) => {

            const aLast = muscleLastTrained[a.name];

            const bLast = muscleLastTrained[b.name];

            const aDays = aLast ? daysSinceTrained(a.name) : Infinity;

            const bDays = bLast ? daysSinceTrained(b.name) : Infinity;



            // Never trained first (so they get attention)

            if (!aLast && bLast) return -1;

            if (aLast && !bLast) return 1;



            // Then sort by readiness: ready first (days >= restDays)

            const aReady = aLast ? aDays >= a.restDays : true;

            const bReady = bLast ? bDays >= b.restDays : true;

            if (aReady && !bReady) return -1;

            if (!aReady && bReady) return 1;



            // Then by days left (closest to ready first)

            const aLeft = aLast ? a.restDays - aDays : 0;

            const bLeft = bLast ? b.restDays - bDays : 0;

            return aLeft - bLeft;

        });

    }



    // Define categories with their container IDs, names, icons, and muscle arrays

    const categories = [

        { id: 'majorMuscleRecovery', name: 'Major Muscle Groups', icon: 'fas fa-dumbbell', muscles: muscleDatabase.major },

        { id: 'longevityMuscleRecovery', name: 'Longevity & Joint Health', icon: 'fas fa-user-md', muscles: muscleDatabase.longevity },

        { id: 'handMuscleRecovery', name: 'Grip & Hand Muscles', icon: 'fas fa-hands', muscles: muscleDatabase.hands },

        { id: 'footMuscleRecovery', name: 'Foot & Ankle Muscles', icon: 'fas fa-shoe-prints', muscles: muscleDatabase.feet }

    ];



    categories.forEach(cat => {

        const container = document.getElementById(cat.id);

        if (!container) return;



        // Clear container (but we'll keep the parent category div structure)

        container.innerHTML = '';



        // Compute stats for this category

        const stats = computeCategoryStats(cat.muscles);



        // Create and insert header above the muscle list

        const header = createCategoryHeader(cat.name, cat.icon, stats, cat.id);

        container.appendChild(header);



        // Create muscle list container

        const listDiv = document.createElement('div');

        listDiv.className = 'muscle-recovery-list';

        listDiv.style.marginTop = '10px';



        // Sort muscles

        const sortedMuscles = sortMuscles([...cat.muscles]);



        // Add each muscle

        sortedMuscles.forEach(muscle => {

            const muscleEl = isMobile ? createMobileRecoveryElement(muscle) : createRecoveryElement(muscle);

            listDiv.appendChild(muscleEl);

        });



        container.appendChild(listDiv);

    });

}



    function updateCategoryRecovery(containerId, muscles) {

        const container = document.getElementById(containerId);

        if (!container) return;



        // Clear container (including any previous summary)

        container.innerHTML = '';



        // 1. Compute category statistics

        let totalRecovery = 0;

        let readyCount = 0;

        let closeCount = 0;

        let restingCount = 0;

        let neverTrainedCount = 0;



        muscles.forEach(muscle => {

            const lastTrained = muscleLastTrained[muscle.name];

            if (!lastTrained) {

                neverTrainedCount++;

                totalRecovery += 100; // assume ready

                readyCount++;

                return;

            }

            const daysSince = Math.floor((new Date() - new Date(lastTrained)) / (1000 * 60 * 60 * 24));

            const recoveryPercent = Math.min(100, (daysSince / muscle.restDays) * 100);

            totalRecovery += recoveryPercent;



            if (daysSince >= muscle.restDays) readyCount++;

            else if (daysSince >= muscle.restDays - 1) closeCount++;

            else restingCount++;

        });



        const avgRecovery = muscles.length ? Math.round(totalRecovery / muscles.length) : 0;



        // 2. Add a summary bar at the top of the container

        const summaryDiv = document.createElement('div');

        summaryDiv.className = 'category-summary';

        summaryDiv.style.marginBottom = '15px';

        summaryDiv.style.padding = '10px';

        summaryDiv.style.background = 'rgba(0,0,0,0.02)';

        summaryDiv.style.borderRadius = '8px';



        // Determine color for the average

        let avgColor;

        if (avgRecovery >= 80) avgColor = 'var(--success)';

        else if (avgRecovery >= 50) avgColor = 'var(--warning)';

        else avgColor = 'var(--danger)';



        summaryDiv.innerHTML = `

            <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.9rem; color: var(--gray-600);">

                <span><i class="fas fa-chart-line"></i> Category recovery</span>

                <span><strong style="color: ${avgColor};">${avgRecovery}%</strong></span>

            </div>

            <div class="progress-bar" style="height: 6px; margin-top: 5px;">

                <div class="progress-fill" style="width: ${avgRecovery}%; background: ${avgColor};"></div>

            </div>

            <div style="display: flex; gap: 15px; margin-top: 8px; font-size: 0.8rem;">

                <span style="color: var(--success);">âœ”ï¸ ${readyCount} ready</span>

                <span style="color: var(--warning);">â³ ${closeCount} soon</span>

                <span style="color: var(--info);">ðŸ’¤ ${restingCount} resting</span>

                ${neverTrainedCount > 0 ? `<span style="color: var(--danger);">âŒ ${neverTrainedCount} never</span>` : ''}

            </div>

        `;

        container.appendChild(summaryDiv);



        // 3. Sort muscles by importance

        const sortedMuscles = [...muscles].sort((a, b) => {

            const aLast = muscleLastTrained[a.name];

            const bLast = muscleLastTrained[b.name];

            const aDays = aLast ? daysSinceTrained(a.name) : Infinity;

            const bDays = bLast ? daysSinceTrained(b.name) : Infinity;



            // Never trained first

            if (!aLast && bLast) return -1;

            if (aLast && !bLast) return 1;



            // Then ready (days >= restDays) before resting

            const aReady = aLast ? aDays >= a.restDays : true;

            const bReady = bLast ? bDays >= b.restDays : true;

            if (aReady && !bReady) return -1;

            if (!aReady && bReady) return 1;



            // Then by days left (smallest left first)

            const aLeft = aLast ? a.restDays - aDays : 0;

            const bLeft = bLast ? b.restDays - bDays : 0;

            return aLeft - bLeft;

        });



        // 4. Determine mobile/desktop layout

        const isMobile = window.innerWidth <= 768;



        // 5. Append each muscle row

        sortedMuscles.forEach(muscle => {

            const recoveryEl = isMobile ? createMobileRecoveryElement(muscle) : createRecoveryElement(muscle);

            container.appendChild(recoveryEl);

        });

    }



    function createRecoveryElement(muscle) {

        const lastTrained = muscleLastTrained[muscle.name];

        const recoveryEl = document.createElement('div');

        recoveryEl.className = 'recovery-item recovery-item-desktop';



        let recoveryPercent = 100;

        let statusClass = 'recovery-ready';

        let statusText = 'Ready';

        let daysText = 'Not trained yet';

        let daysUntilReady = 0;

        let tooltipText = '';



        if (lastTrained) {

            const lastDate = new Date(lastTrained);

            const today = new Date();

            const daysSince = Math.floor((today - lastDate) / (1000 * 60 * 60 * 24));

            recoveryPercent = Math.min(100, (daysSince / muscle.restDays) * 100);

            daysText = `${daysSince}d ago`;



            if (daysSince >= muscle.restDays) {

                statusClass = 'recovery-ready';

                statusText = 'âœ“ Ready';

                daysUntilReady = 0;

                tooltipText = `Last trained ${daysSince} days ago â€“ fully recovered.`;

            } else if (daysSince >= muscle.restDays - 1) {

                statusClass = 'recovery-close';

                statusText = 'â³ Soon';

                daysUntilReady = muscle.restDays - daysSince;

                tooltipText = `Last trained ${daysSince} days ago â€“ ready in ${daysUntilReady} day${daysUntilReady !== 1 ? 's' : ''}.`;

            } else {

                statusClass = 'recovery-not-ready';

                statusText = 'ðŸ’¤ Resting';

                daysUntilReady = muscle.restDays - daysSince;

                tooltipText = `Last trained ${daysSince} days ago â€“ needs ${daysUntilReady} more day${daysUntilReady !== 1 ? 's' : ''} of rest.`;

            }

        } else {

            tooltipText = 'Never trained â€“ consider adding this muscle to your workout.';

        }



        // Risk indicator badge

        const riskIndicator = muscle.agingRisk ? 

            `<span class="badge ${getRiskBadgeClass(muscle.agingRisk)}" style="margin-left: 5px; font-size: 0.7rem;">

                ${muscle.agingRisk.toUpperCase()}

            </span>` : '';



        // Build the row

        recoveryEl.innerHTML = `

            <span style="font-weight: 600; color: var(--gray-700); min-width: 150px;" title="${muscle.display} requires ${muscle.restDays} days of rest">

                ${muscle.display}${riskIndicator}

            </span>

            <span style="font-size: 0.9rem; color: var(--gray-500); min-width: 100px;" title="Last trained">${daysText}</span>

            <div class="recovery-bar" style="position: relative; min-width: 120px;" title="${tooltipText}">

                <div class="recovery-fill ${statusClass}" style="width: ${recoveryPercent}%;"></div>

                <span style="position: absolute; right: 5px; top: 50%; transform: translateY(-50%); font-size: 0.7rem; color: var(--gray-700); background: rgba(255,255,255,0.7); padding: 0 4px; border-radius: 4px;">

                    ${Math.round(recoveryPercent)}%

                </span>

            </div>

            <span style="font-weight: 700; min-width: 90px; text-align: right; color: var(--${statusClass.split('-')[1] || 'success'});" title="${tooltipText}">

                ${statusText} ${daysUntilReady > 0 && statusClass !== 'recovery-ready' ? `(in ${daysUntilReady}d)` : ''}

            </span>

        `;



        return recoveryEl;

    }



    function createMobileRecoveryElement(muscle) {

        const lastTrained = muscleLastTrained[muscle.name];

        const recoveryEl = document.createElement('div');

        recoveryEl.className = 'recovery-item-mobile';



        let recoveryPercent = 100;

        let statusClass = 'status-ready';

        let statusText = 'Ready';

        let statusIcon = 'âœ“';

        let daysText = 'Not trained yet';

        let daysUntilReady = 0;

        let tooltipText = '';



        if (lastTrained) {

            const lastDate = new Date(lastTrained);

            const today = new Date();

            const daysSince = Math.floor((today - lastDate) / (1000 * 60 * 60 * 24));

            recoveryPercent = Math.min(100, (daysSince / muscle.restDays) * 100);

            daysText = `${daysSince}d ago`;



            if (daysSince >= muscle.restDays) {

                statusClass = 'status-ready';

                statusText = 'Ready';

                statusIcon = 'âœ“';

                daysUntilReady = 0;

                tooltipText = `Last trained ${daysSince} days ago â€“ fully recovered.`;

            } else if (daysSince >= muscle.restDays - 1) {

                statusClass = 'status-close';

                statusText = 'Soon';

                statusIcon = 'â³';

                daysUntilReady = muscle.restDays - daysSince;

                tooltipText = `Last trained ${daysSince} days ago â€“ ready in ${daysUntilReady} day${daysUntilReady !== 1 ? 's' : ''}.`;

            } else {

                statusClass = 'status-not-ready';

                statusText = 'Resting';

                statusIcon = 'ðŸ’¤';

                daysUntilReady = muscle.restDays - daysSince;

                tooltipText = `Last trained ${daysSince} days ago â€“ needs ${daysUntilReady} more day${daysUntilReady !== 1 ? 's' : ''} of rest.`;

            }

        } else {

            tooltipText = 'Never trained â€“ consider adding this muscle to your workout.';

        }



        const riskIndicator = muscle.agingRisk ? 

            `<span class="badge ${getRiskBadgeClass(muscle.agingRisk)}" style="margin-left: 5px; font-size: 0.7rem;">

                ${muscle.agingRisk.toUpperCase()}

            </span>` : '';



        // Build status text with icon and days until ready (if applicable)

        const displayStatus = `${statusIcon} ${statusText}${daysUntilReady > 0 && statusClass !== 'status-ready' ? ` (in ${daysUntilReady}d)` : ''}`;



        recoveryEl.innerHTML = `

            <div class="recovery-item-mobile-content">

                <div class="recovery-item-header">

                    <span class="recovery-item-muscle" title="${muscle.display} requires ${muscle.restDays} days of rest">${muscle.display}${riskIndicator}</span>

                    <span class="recovery-item-status ${statusClass}" title="${tooltipText}">${displayStatus}</span>

                </div>

                <div class="recovery-item-details">

                    <span title="Last trained">${daysText}</span>

                    <span title="Recommended rest period">${muscle.restDays} day rest</span>

                </div>

                <div class="recovery-item-bar-container" title="${tooltipText}">

                    <div class="recovery-item-bar-label">

                        <span>Recovery</span>

                        <span>${Math.round(recoveryPercent)}%</span>

                    </div>

                    <div class="recovery-bar">

                        <div class="recovery-fill ${statusClass.replace('status-', 'recovery-')}" style="width: ${recoveryPercent}%"></div>

                    </div>

                </div>

            </div>

        `;



        return recoveryEl;

    }



    function getRiskBadgeClass(risk) {

        const riskMap = {

            'high': 'badge-danger',

            'medium': 'badge-warning',

            'low': 'badge-success'

        };



        // Normalize risk to lowercase and trim, in case it's passed with spaces or capitals

        const normalizedRisk = risk ? risk.toString().toLowerCase().trim() : '';



        // Return the mapped class, or default to badge-info

        return riskMap[normalizedRisk] || 'badge-info';

    }



    // Optional helper function to generate a workout targeting specific muscles

    // You'll need to implement this if you want the "Generate workout for these" button to work.

    function generateWorkoutForMuscles(muscleNamesCsv) {

        const muscleArray = muscleNamesCsv.split(',').map(m => m.trim());

        // This function should create a workout that includes exercises for these muscles.

        // For now, we'll just show a notification and fallback to normal generation.

        showNotification(`Workout generation for specific muscles coming soon! Using normal workout.`);

        generateNextWorkout();

    }



    // SETTINGS SECTION

function loadSettingsForm() {



    // Helper: calculate BMI (lbs/inches)

    function calculateBMI(weightLbs, heightInches) {

        if (!weightLbs || !heightInches) return null;

        return ((weightLbs / (heightInches * heightInches)) * 703).toFixed(1);

    }



    // --- Load basic fields ---

    const nameInput = document.getElementById('settingsName');

    const birthInput = document.getElementById('settingsBirthDate');

    const genderSelect = document.getElementById('settingsGender');

    const weightInput = document.getElementById('settingsWeight');

    const heightInput = document.getElementById('settingsHeight');

    const expSelect = document.getElementById('settingsExperience');

    const goalSelect = document.getElementById('settingsGoal');

    const restInput = document.getElementById('settingsRestTime');

    const progInput = document.getElementById('settingsProgression');

        const couplingToggle = document.getElementById('muscleCouplingToggle');



    if (nameInput) nameInput.value = workoutData.user.name || '';

    if (birthInput) birthInput.value = workoutData.user.birthDate || '';

    if (genderSelect) genderSelect.value = workoutData.user.gender || 'male';

    if (weightInput) weightInput.value = workoutData.user.weight || '';

    if (heightInput) heightInput.value = workoutData.user.height || '';

    // P5P8: postpartum field (female only)

    const ppInput = document.getElementById('settingsPostpartum');

    const ppGroup = document.getElementById('postpartumGroup');

    if (ppGroup) ppGroup.style.display = (workoutData.user.gender === 'female') ? 'block' : 'none';

    if (ppInput) {

        const ppv = workoutData.user.postpartumMonths;

        ppInput.value = (ppv === undefined || ppv === null || ppv === '') ? '' : ppv;

    }

    if (expSelect) expSelect.value = workoutData.user.experience || 'intermediate';

    if (goalSelect) goalSelect.value = workoutData.user.goal || 'balanced';

    if (restInput) restInput.value = workoutData.user.settings?.restTime || 90;

    if (progInput) progInput.value = (workoutData.user.settings?.progressionRate || 0.02) * 100;

        if (couplingToggle) {

    couplingToggle.checked = workoutData.user.settings?.muscleCoupling || false;

}



    // Workout days checkboxes

    document.querySelectorAll('.workout-day').forEach(checkbox => {

        checkbox.checked = workoutData.user.settings?.workoutDays?.includes(checkbox.value) || false;

    });

  

    const autoHideCheck = document.getElementById('bottomNavAutoHide');

    if (autoHideCheck) {

        autoHideCheck.checked = workoutData.user.settings?.bottomNavAutoHide || false;

    }

  

    const expressToggle = document.getElementById('expressModeToggle');

    if (expressToggle) {

        expressToggle.checked = workoutData.user.settings?.expressMode || false;

    }

    const aggressionSlider = document.getElementById('aggressionSlider');

    if (aggressionSlider) {

        const val = workoutData.user.settings?.aggression || 1.0;

        aggressionSlider.value = val;

        document.getElementById('aggressionValue').innerText = val;

        aggressionSlider.oninput = updateAggression;

    }



    // --- Enhanced displays (created dynamically if not present) ---



    // Age display

    if (birthInput) {

        let ageDisplay = document.getElementById('ageDisplay');

        if (!ageDisplay) {

            ageDisplay = document.createElement('div');

            ageDisplay.id = 'ageDisplay';

            ageDisplay.style.marginTop = '5px';

            ageDisplay.style.fontSize = '0.9rem';

            ageDisplay.style.color = 'var(--gray-600)';

            birthInput.parentNode.appendChild(ageDisplay);

        }

        const age = calculateAge(birthInput.value);

        ageDisplay.textContent = age ? `Age: ${age} years` : '';

    }



    // BMI display with category

    if (weightInput && heightInput) {

        const updateBMI = () => {

            let bmiDisplay = document.getElementById('bmiDisplay');

            if (!bmiDisplay) {

                bmiDisplay = document.createElement('div');

                bmiDisplay.id = 'bmiDisplay';

                bmiDisplay.style.marginTop = '5px';

                bmiDisplay.style.fontSize = '0.9rem';

                bmiDisplay.style.color = 'var(--gray-600)';

                heightInput.parentNode.appendChild(bmiDisplay);

            }

            const weight = parseFloat(weightInput.value);

            const height = parseFloat(heightInput.value);

            const bmi = calculateBMI(weight, height);

            if (bmi) {

                let category = '';

                if (bmi < 18.5) category = 'Underweight';

                else if (bmi < 25) category = 'Normal weight';

                else if (bmi < 30) category = 'Overweight';

                else category = 'Obese';

                bmiDisplay.innerHTML = `BMI: ${bmi} (${category})`;

            } else {

                bmiDisplay.innerHTML = '';

            }

        };

        updateBMI();

    }



    // Preâ€‘fill menstrual data if gender is female

    if (genderSelect.value === 'female') {

        const lastPeriodInput = document.getElementById('lastPeriodDate');

        const cycleLengthInput = document.getElementById('cycleLength');

        if (lastPeriodInput && workoutData.user.menstrual?.lastPeriodStart) {

            lastPeriodInput.value = workoutData.user.menstrual.lastPeriodStart;

        }

        if (cycleLengthInput && workoutData.user.menstrual?.cycleLength) {

            cycleLengthInput.value = workoutData.user.menstrual.cycleLength;

        }

    }



    // Show a note when goal is "longevity"

    if (goalSelect.value === 'longevity') {

        let note = document.getElementById('longevityNote');

        if (!note) {

            note = document.createElement('div');

            note.id = 'longevityNote';

            note.style.marginTop = '10px';

            note.style.padding = '8px';

            note.style.background = 'rgba(16,185,129,0.1)';

            note.style.borderRadius = '8px';

            note.style.color = 'var(--success)';

            note.innerHTML = '<i class="fas fa-leaf"></i> Longevity focus: we will emphasise joint health and mobility.';

            goalSelect.parentNode.appendChild(note);

        }

    } else {

        const note = document.getElementById('longevityNote');

        if (note) note.remove();

    }



    // Optional: if competition data exists (for future use)

    if (workoutData.user.competition?.date) {

    }

}



function saveSettings() {

