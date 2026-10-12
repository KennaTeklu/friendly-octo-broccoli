// ============================================================
// reference/scripts/app/18-recommendations.js
// Extracted from reference/scripts/app.js
// Original lines: 75880-81113  (5234 lines)
// Section: Recommendations, breakdown panel, unsaved workout checks
// ============================================================

    // ----- 1. Riskâ€‘based recommendations (prioritised by severity) -----

    const severityOrder = { High: 3, Medium: 2, Low: 1 };

    const sortedRisks = [...risks].sort((a, b) => 

        (severityOrder[b.severity] || 0) - (severityOrder[a.severity] || 0)

    );



    sortedRisks.forEach(risk => {

        let rec = risk.recommendation;



        // Append specific exercises if available

        if (risk.exercises && risk.exercises.length > 0) {

            const exerciseNames = risk.exercises.map(exId => {

                // Try to get a friendly name from the exercise library

                const ex = ultimateExerciseLibrary[exId] || workoutProgram.exercises[exId];

                return ex ? ex.name : exId.replace(/_/g, ' ');

            }).join(', ');

            rec += ` Try: ${exerciseNames}.`;

        }



        // Add urgency marker for high severity

        if (risk.severity === 'High') {

            rec = 'âš ï¸ URGENT: ' + rec;

        }



        recommendations.push(rec);

    });



    // ----- 2. Scoreâ€‘based general recommendations (if no risk for that area) -----

    if (scores.posture >= 70 && scores.posture < 85) {

        recommendations.push("Your posture is good. Maintain with regular upper back work.");

    } else if (scores.posture >= 85) {

        recommendations.push("Excellent posture! Keep up the good work.");

    }



    if (scores.balance >= 60 && scores.balance < 80) {

        recommendations.push("Balance is decent. Aim for 2â€‘3 balance sessions per week to maintain.");

    } else if (scores.balance >= 80) {

        recommendations.push("Great balance! Continue your routine.");

    }



    if (scores.jointMobility >= 50 && scores.jointMobility < 75) {

        recommendations.push("Joint mobility is acceptable. Add one mobility session per week.");

    } else if (scores.jointMobility >= 75) {

        recommendations.push("Excellent joint mobility! Keep moving.");

    }



    // New scores from the enhanced longevity calculator

    if (scores.trend !== undefined) {

        if (scores.trend < 40) {

            recommendations.push("Your strength is declining. Consider a structured progressive overload program.");

        } else if (scores.trend > 75) {

            recommendations.push("Strong progress! Keep challenging yourself.");

        }

    }



    if (scores.variety !== undefined) {

        if (scores.variety < 50) {

            recommendations.push("You're missing many longevity muscles. Try to include a wider variety of jointâ€‘health exercises.");

        } else if (scores.variety >= 80) {

            recommendations.push("Excellent variety in your training! This is great for longâ€‘term health.");

        }

    }



    // ----- 3. Goalâ€‘based advice -----

    if (workoutData.user.goal === 'longevity') {

        recommendations.push("Your goal is longevity. Keep focusing on joint health, balance, and consistency.");

    }



    // ----- 4. Reassessment reminder -----

    if (recommendations.length > 0) {

        recommendations.push("Reassess your longevity score in 4â€‘6 weeks to track progress.");

    }



    // Remove any duplicates (rare, but safe)

    return [...new Set(recommendations)];

}



function updateLongevityScoreDisplay() {

    const scoreData = calculateLongevityScore();

    const score = scoreData.score;

    const breakdown = scoreData.breakdown;

    const risks = scoreData.risks || [];



    // Update main stat card (on dashboard)

    const statEl = document.getElementById('longevityScore');

    if (statEl) statEl.textContent = score;



    // Update the circular score inside the card

    const circleScoreEl = document.getElementById('longevityCircleScore');

    if (circleScoreEl) circleScoreEl.textContent = score;



    // Update the circle's conic gradient

    const circle = document.querySelector('.score-circle');

    if (circle) {

        let color;

        if (score >= 80) color = 'var(--success)';

        else if (score >= 60) color = 'var(--warning)';

        else color = 'var(--danger)';

        circle.style.background = `conic-gradient(${color} 0% ${score}%, var(--gray-300) ${score}% 100%)`;

    }



    // Update status text and color

    const statusEl = document.getElementById('longevityStatus');

    if (statusEl) {

        let statusText, statusColor;

        if (score >= 80) {

            statusText = "Excellent Longevity";

            statusColor = "var(--success)";

        } else if (score >= 60) {

            statusText = "Good Longevity";

            statusColor = "var(--warning)";

        } else if (score >= 40) {

            statusText = "Needs Improvement";

            statusColor = "var(--warning)";

        } else {

            statusText = "High Risk";

            statusColor = "var(--danger)";

        }

        statusEl.textContent = statusText;

        statusEl.style.color = statusColor;

    }



    // ----- Add a small breakdown panel inside the longevity card -----

    const card = document.querySelector('.longevity-score-card');

    if (card) {

        let breakdownDiv = document.getElementById('longevityBreakdown');

        if (!breakdownDiv) {

            breakdownDiv = document.createElement('div');

            breakdownDiv.id = 'longevityBreakdown';

            breakdownDiv.style.marginTop = '15px';

            breakdownDiv.style.padding = '10px';

            breakdownDiv.style.backgroundColor = 'rgba(0,0,0,0.02)';

            breakdownDiv.style.borderRadius = '8px';

            breakdownDiv.style.fontSize = '0.85rem';

            card.appendChild(breakdownDiv);

        }



        // Build subâ€‘score summary (show only key ones)

        const keyScores = ['gripStrength', 'balance', 'jointMobility', 'posture', 'muscleBalance', 'consistency'];

        let html = '<div style="display: flex; flex-wrap: wrap; gap: 8px; justify-content: center;">';

        keyScores.forEach(key => {

            if (breakdown[key] !== undefined) {

                let color = 'var(--gray-600)';

                if (breakdown[key] >= 70) color = 'var(--success)';

                else if (breakdown[key] >= 50) color = 'var(--warning)';

                else color = 'var(--danger)';

                html += `<span style="background: ${color}20; padding: 4px 8px; border-radius: 20px; color: ${color}; font-weight: 600;">${key.replace(/([A-Z])/g, ' $1')}: ${breakdown[key]}</span>`;

            }

        });

        html += '</div>';



        // Add risk count if any

        if (risks.length > 0) {

            const highRisks = risks.filter(r => r.severity === 'High').length;

            html += `<div style="margin-top: 8px; text-align: center; color: var(--gray-600);">

                <i class="fas fa-exclamation-triangle" style="color: var(--warning);"></i> ${risks.length} risk factor${risks.length !== 1 ? 's' : ''} (${highRisks} high)

            </div>`;

        }



        breakdownDiv.innerHTML = html;

    }



    // Store risks in user data for later use (e.g., in modal)

    workoutData.user.agingRisks = risks;

    saveToLocalStorage();

}



function showLongevityReport() {

    const scoreData = calculateLongevityScore();

    const container = document.getElementById('longevityReport');

    const age = (() => {

        if (!workoutData.user.birthDate) return null;

        const birth = new Date(workoutData.user.birthDate);

        const today = new Date();

        let age = today.getFullYear() - birth.getFullYear();

        const m = today.getMonth() - birth.getMonth();

        if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;

        return age;

    })();



    // Helper to get color based on score

    function getScoreColor(score) {

        if (score >= 80) return 'var(--success)';

        if (score >= 60) return 'var(--warning)';

        if (score >= 40) return 'var(--danger)';

        return 'var(--danger)';

    }



    // Build breakdown grid with color coding

    let breakdownHtml = '';

    const displayNames = {

        gripStrength: 'Grip',

        balance: 'Balance',

        jointMobility: 'Joints',

        posture: 'Posture',

        muscleBalance: 'Push/Pull',

        consistency: 'Consistency',

        trend: 'Trend',

        variety: 'Variety'

    };

    Object.entries(scoreData.breakdown).forEach(([key, value]) => {

        const displayName = displayNames[key] || key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());

        const color = getScoreColor(value);

        breakdownHtml += `

            <div style="background: ${color}10; padding: 15px; border-radius: 10px; border-left: 4px solid ${color};">

                <div style="font-weight: 600; color: var(--gray-700); margin-bottom: 5px;">${displayName}</div>

                <div style="font-size: 1.5rem; font-weight: 800; color: ${color};">${Math.round(value)}</div>

            </div>

        `;

    });



    // Build risks section with severity badges and exercise buttons

    let risksHtml = '';

    if (scoreData.risks.length > 0) {

        risksHtml = '<div style="margin-bottom: 25px;"><h4 style="color: var(--dark); margin-bottom: 15px;"><i class="fas fa-exclamation-triangle"></i> Aging Risk Factors</h4>';

        // Sort risks by severity (High â†’ Medium â†’ Low)

        const severityOrder = { High: 0, Medium: 1, Low: 2 };

        const sortedRisks = [...scoreData.risks].sort((a, b) => severityOrder[a.severity] - severityOrder[b.severity]);



        sortedRisks.forEach(risk => {

            const severityColor = risk.severity === 'High' ? 'var(--danger)' : 

                                 risk.severity === 'Medium' ? 'var(--warning)' : 'var(--info)';

            // Build exercise buttons if exercises are provided

            let exerciseButtons = '';

            if (risk.exercises && risk.exercises.length > 0) {

                exerciseButtons = `

                    <div style="display: flex; gap: 8px; flex-wrap: wrap; margin-top: 10px;">

                        ${risk.exercises.map(exId => {

                            const exName = (workoutProgram.exercises[exId]?.name || exId.replace(/_/g, ' ')).replace(/^\w/, c => c.toUpperCase());

                            return `<button class="btn btn-sm" style="padding: 4px 12px; font-size: 0.8rem;" onclick="generateWorkoutForExercise('${exId}')">âž• ${exName}</button>`;

                        }).join('')}

                    </div>

                `;

            }

            risksHtml += `

                <div style="background: ${severityColor}10; border-left: 4px solid ${severityColor}; padding: 15px; margin-bottom: 10px; border-radius: 0 8px 8px 0;">

                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">

                        <strong style="color: var(--dark);">${risk.factor}</strong>

                        <span class="badge" style="background: ${severityColor}30; color: ${severityColor};">${risk.severity}</span>

                    </div>

                    <p style="color: var(--gray-600); font-size: 0.9rem; margin-bottom: 8px;">${risk.impact}</p>

                    <div style="color: var(--gray-700); font-size: 0.9rem;">

                        <strong>Recommendation:</strong> ${risk.recommendation}

                    </div>

                    ${exerciseButtons}

                </div>

            `;

        });

        risksHtml += `</div>`;

    }



    // Build recommendations list

    let recsHtml = '';

    if (scoreData.recommendations.length > 0) {

        recsHtml = `

            <div style="margin-bottom: 25px;">

                <h4 style="color: var(--dark); margin-bottom: 15px;"><i class="fas fa-lightbulb"></i> Recommendations</h4>

                <ul style="color: var(--gray-700); padding-left: 20px;">

                    ${scoreData.recommendations.map(rec => `<li style="margin-bottom: 8px;">${rec}</li>`).join('')}

                </ul>

            </div>

        `;

    }



    // Build age note

    let ageNote = '';

    if (age !== null) {

        ageNote = `<p style="text-align: center; color: var(--gray-500); font-size: 0.9rem; margin-top: 5px;">Based on your age (${age} years), certain factors are weighted differently.</p>`;

    }



    // Final HTML

    const html = `

        <div style="margin-bottom: 25px;">

            <div class="score-circle" style="margin: 0 auto 20px; width: 120px; height: 120px;">

                <div class="score-circle-inner" style="width: 90px; height: 90px; font-size: 2rem;">${scoreData.score}</div>

            </div>

            <h3 style="text-align: center; color: var(--dark); margin-bottom: 5px;">Longevity Score: ${scoreData.score}/100</h3>

            <p style="text-align: center; color: var(--gray-600);">Higher scores indicate better joint health and reduced aging risks</p>

            ${ageNote}

        </div>

        <div style="margin-bottom: 25px;">

            <h4 style="color: var(--dark); margin-bottom: 15px;">Score Breakdown</h4>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 15px;">

                ${breakdownHtml}

            </div>

        </div>

        ${risksHtml}

        ${recsHtml}

        <div style="display: flex; gap: 10px; justify-content: center; margin-top: 20px;">

            <button class="btn btn-success" onclick="generateLongevityWorkout()">

                <i class="fas fa-user-md"></i> Longevity Workout

            </button>

            <button class="btn" onclick="closeModal('longevityModal')">Close</button>

        </div>

    `;



    container.innerHTML = html;

    showModal('longevityModal');

}



// Optional helper function to generate a workout for a specific exercise

function generateWorkoutForExercise(exerciseId) {

    // This function could create a miniâ€‘workout focused on that exercise

    // For now, we'll just show a notification and fallback to a normal generation

    showNotification(`Workout for ${exerciseId} coming soon!`, 'info');

    generateNextWorkout();

}



async function generateLongevityWorkout() {

    // ========== CHECK FOR UNSAVED WORKOUT DATA ==========

    const hasUnsaved = currentWorkout && currentWorkout.exercises && currentWorkout.exercises.some(ex => !ex.actual && !ex.skipped);

    

    let confirmMessage = 'Generate a new longevity workout? This will replace your current workout.';

    if (hasUnsaved) {

        confirmMessage = 'âš ï¸ You have unsaved progress in your current workout.\n\nGenerating a new longevity workout will replace it and you will lose that progress.\n\nContinue?';

    }

    

    const result = await Swal.fire({

        title: 'ðŸƒ Longevity Workout',

        text: confirmMessage,

        icon: hasUnsaved ? 'warning' : 'question',

        showCancelButton: true,

        confirmButtonColor: '#10b981',

        cancelButtonColor: '#6c757d',

        confirmButtonText: 'Generate & Replace',

        cancelButtonText: 'Cancel'

    });

    

    if (!result.isConfirmed) return;

    

    // Show loading indicator

    showLoading(true);

    

    try {

        // ----- 1. Get current risks and neglected muscles -----

        const scoreData = calculateLongevityScore();

        const risks = scoreData.risks || workoutData.user.agingRisks || [];

        

        // Collect target muscles from risks and from neverâ€‘trained longevity muscles

        const targetMuscles = new Set();

        

        // Add muscles from risk exercises

        risks.forEach(risk => {

            if (risk.exercises) {

                risk.exercises.forEach(exId => {

                    const ex = ultimateExerciseLibrary[exId] || workoutProgram.exercises[exId];

                    if (ex && ex.muscleGroup) {

                        ex.muscleGroup.forEach(mg => targetMuscles.add(mg));

                    }

                });

            }

        });

        

        // Add neverâ€‘trained longevity muscles (those with no lastTrained)

        const allLongevityMuscles = [

            ...muscleDatabase.longevity,

            ...muscleDatabase.hands,

            ...muscleDatabase.feet

        ];

        allLongevityMuscles.forEach(muscle => {

            if (!muscleLastTrained[muscle.name]) {

                targetMuscles.add(muscle.name);

            }

        });

        

        // If no target muscles, fallback to a balanced set of key longevity muscles

        if (targetMuscles.size === 0) {

            ['neck', 'rhomboids', 'rear_delts', 'forearms', 'foot_intrinsics'].forEach(m => targetMuscles.add(m));

        }

        

        // ----- 2. Build workout -----

        currentWorkout = {

            id: `workout_${Date.now()}`,

            date: new Date().toISOString(),

            type: "longevity_day",

            name: "Personalised Longevity & Joint Health",

            exercises: [],

            isDeload: false,

            isTaper: false

        };

        

        // For each target muscle, select an appropriate exercise

        targetMuscles.forEach(mg => {

            const ex = selectExerciseForMuscle(mg);

            if (!ex) return;

            currentWorkout.exercises.push(generateExercisePrescription(ex));

        });

        

        // If we ended up with no exercises, add a default set

        if (currentWorkout.exercises.length === 0) {

            const defaultMobility = [

                { id: "neck_isometric", name: "Neck Isometric Holds", muscleGroup: ["neck"] },

                { id: "face_pull", name: "Face Pulls", muscleGroup: ["rear_delts", "rhomboids"] },

                { id: "short_foot", name: "Short Foot Exercise", muscleGroup: ["foot_intrinsics"] }

            ];

            defaultMobility.forEach(def => {

                const ex = generateExerciseFromLibrary(def.id);

                if (ex) currentWorkout.exercises.push(generateExercisePrescription(ex));

            });

        }

        

        saveCurrentWorkoutToStorage();

        

        // ----- 3. Update UI -----

        updateDashboard();

        updateTodaysWorkout();

        showSection('workout');

        closeModal('longevityModal');

        

        // Count addressed risk factors

        const addressedRisks = risks.filter(risk => 

            risk.exercises && risk.exercises.some(exId => 

                currentWorkout.exercises.some(ex => ex.id === exId)

            )

        ).length;

        

        // ----- 4. Success notification with details -----

        showLoading(false);

        await Swal.fire({

            title: 'âœ¨ Longevity Workout Generated',

            html: `

                <div style="text-align: left;">

                    <p><strong>Workout:</strong> Personalised Longevity & Joint Health</p>

                    <p><strong>Exercises:</strong> ${currentWorkout.exercises.length}</p>

                    <p><strong>Target muscles:</strong> ${Array.from(targetMuscles).slice(0, 5).map(m => getMuscleDisplayName(m)).join(', ')}${targetMuscles.size > 5 ? '...' : ''}</p>

                    <p><strong>Addresses ${addressedRisks} of your ${risks.length} risk factors.</strong></p>

                </div>

                <hr>

                <p>Your workout is ready. Focus on form and controlled movements.</p>

            `,

            icon: 'success',

            confirmButtonText: 'Start Workout',

            confirmButtonColor: '#10b981'

        }).then(() => {

            // Optional: scroll to workout section

            document.getElementById('workout-section').scrollIntoView({ behavior: 'smooth' });

        });

        

        showNotification(`Personalised longevity workout generated! Addresses ${addressedRisks} of your risk factors.`, 'success');

        

    } catch (error) {

        console.error('[generateLongevityWorkout] Error:', error);

        showLoading(false);

        await Swal.fire({

            title: 'Generation Failed',

            text: 'Could not generate longevity workout. Please try again.',

            icon: 'error',

            confirmButtonText: 'OK'

        });

    }

}



// Add this global variable near the top with other globals (e.g., after muscleLastTrained)

let muscleSessionCount = {}; // will hold count of sessions per muscle in last 60 days



function calculateMuscleLastTrained() {

    const LOOKBACK_DAYS = 60;

    const cutoffDate = new Date();

    cutoffDate.setDate(cutoffDate.getDate() - LOOKBACK_DAYS);



    // Reset session counts

    muscleSessionCount = {};

    getAllMuscleGroups().forEach(m => muscleSessionCount[m.name] = 0);



    // Reset last trained dates (optional: we could keep existing and update only if newer)

    // But we'll recompute from scratch to ensure consistency

    initializeMuscleTracking(); // sets muscleLastTrained to null for all muscles



    workoutData.workouts.forEach(workout => {

        const workoutDate = new Date(workout.date);

        const isRecent = workoutDate >= cutoffDate;



        workout.exercises.forEach(exercise => {

            if (exercise.actual && !exercise.skipped) {

                const muscles = Array.isArray(exercise.muscleGroup) ? exercise.muscleGroup : [exercise.muscleGroup];

                muscles.forEach(muscle => {

                    // Update last trained (most recent overall)

                    if (!muscleLastTrained[muscle] || workoutDate > new Date(muscleLastTrained[muscle])) {

                        muscleLastTrained[muscle] = workout.date;

                    }

                    // Update session count for recent workouts

                    if (isRecent) {

                        muscleSessionCount[muscle] = (muscleSessionCount[muscle] || 0) + 1;

                    }

                });

            }

        });

    });

}

      

/**

 * Identify muscles that have been neglected beyond a threshold.

 * @param {number} thresholdMultiplier - Factor times restDays to consider overdue (default 1.5).

 * @param {number} maxCount - Maximum number of overdue muscles to return (to avoid overcrowding).

 * @returns {Array<string>} - Array of muscle names that are overdue.

 */

function getOverdueMuscles(thresholdMultiplier = 1.5, maxCount = 3) {

  const allMuscles = getAllMuscleGroups();

  const now = new Date();

  const overdue = [];



  allMuscles.forEach(muscle => {

    const last = muscleLastTrained[muscle.name];

    if (!last) {

      // Never trained â€“ definitely overdue

      overdue.push(muscle.name);

      return;

    }

    const lastDate = new Date(last);

    const daysSince = Math.max(0, (now - lastDate) / (1000 * 60 * 60 * 24));

    if (daysSince >= muscle.restDays * thresholdMultiplier) {

      overdue.push(muscle.name);

    }

  });



  // Sort by most overdue (largest daysSince / restDays) and limit

  overdue.sort((a, b) => {

    const aMuscle = allMuscles.find(m => m.name === a);

    const bMuscle = allMuscles.find(m => m.name === b);

    const aDays = daysSinceTrained(a);

    const bDays = daysSinceTrained(b);

    const aRatio = aDays / aMuscle.restDays;

    const bRatio = bDays / bMuscle.restDays;

    return bRatio - aRatio;

  });



  return overdue.slice(0, maxCount);

}



/**

 * Calculate the number of full days since a muscle was last trained.

 * Uses UTC dates to avoid timezone inconsistencies.

 *

 * @param {string} muscle - The muscle group name (e.g., 'quads').

 * @param {Date|string} [asOfDate] - Optional reference date (defaults to now).

 * @returns {number} - Days since last training (Infinity if never trained).

 */

function daysSinceTrained(muscle, asOfDate = new Date()) {

    const lastTrained = muscleLastTrained[muscle];

    if (!lastTrained) return Infinity;



    // Convert to Date objects, ensure UTC comparison

    const lastDate = new Date(lastTrained);

    const refDate = new Date(asOfDate);



    // Validate dates

    if (isNaN(lastDate.getTime()) || isNaN(refDate.getTime())) {

        return Infinity;

    }



    // Set both dates to UTC midnight to count full calendar days

    const lastUTC = Date.UTC(lastDate.getUTCFullYear(), lastDate.getUTCMonth(), lastDate.getUTCDate());

    const refUTC = Date.UTC(refDate.getUTCFullYear(), refDate.getUTCMonth(), refDate.getUTCDate());



    const diffMs = refUTC - lastUTC;

    const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));



    // If diffMs is negative (future date), treat as 0 days? Usually shouldn't happen.

    return Math.max(0, days);

}



function calculateStreak() {

    if (workoutData.workouts.length === 0) return 0;



    // Sort workouts by date (oldest first for easier processing)

    const sorted = [...workoutData.workouts].sort((a, b) => new Date(a.date) - new Date(b.date));



    let streak = 1; // start with the first workout

    let lastWorkout = sorted[0];

    let lastDate = new Date(lastWorkout.date);



    for (let i = 1; i < sorted.length; i++) {

        const current = sorted[i];

        const currentDate = new Date(current.date);



        // Calculate days between workouts

        const diffDays = Math.floor((currentDate - lastDate) / (1000 * 60 * 60 * 24));



        // Get recommended rest from last workout (default 2 if not set)

        const requiredRest = lastWorkout.recommendedRest || 2;



        // If we worked out within required rest days (or same day), streak continues

        if (diffDays <= requiredRest) {

            streak++;

        } else {

            // Streak breaks; start new streak from this workout

            streak = 1;

        }



        lastWorkout = current;

        lastDate = currentDate;

    }



    // Now check if today is within the rest window of the last workout

    const last = sorted[sorted.length - 1];

    const lastDateObj = new Date(last.date);

    const today = new Date();

    const daysSinceLast = Math.floor((today - lastDateObj) / (1000 * 60 * 60 * 24));

    const lastRest = last.recommendedRest || 2;



    // If today is within the recommended rest window, include today in streak

    if (daysSinceLast <= lastRest) {

        // But if today already has a workout, it's already counted

        const hasWorkoutToday = sorted.some(w => {

            const wDate = new Date(w.date);

            return wDate.toDateString() === today.toDateString();

        });

        if (!hasWorkoutToday) {

            streak++;

        }

    }



    return streak;

}



    function calculateTotalVolume(workout) {

        return workout.exercises.reduce((total, ex) => {

            if (ex.actual && !ex.skipped) {

                return total + calculateVolume(ex.actual);

            }

            return total;

        }, 0);

    }



    function calculateVolume(performance) {

        if (!performance.reps || performance.reps.length === 0) return 0;

        const avgReps = performance.reps.reduce((a, b) => a + b, 0) / performance.reps.length;

        return performance.weight * performance.sets * avgReps;

    }



function calculateAverageRPE(workout) {

    let totalRPE = 0;

    let count = 0;



    workout.exercises.forEach(ex => {

        if (!ex.actual || ex.skipped) return;

        let rpe = ex.actual.rpe;

        if (rpe === undefined || rpe === null) return;



        // If rpe is an array (e.g., [7,7,7]), sum its elements

        if (Array.isArray(rpe)) {

            totalRPE += rpe.reduce((sum, val) => sum + val, 0);

            count += rpe.length;

        } 

        // If it's a single number

        else if (typeof rpe === 'number' && !isNaN(rpe)) {

            totalRPE += rpe;

            count++;

        }

        // If it's a string that can be parsed

        else if (typeof rpe === 'string') {

            const parsed = parseFloat(rpe);

            if (!isNaN(parsed)) {

                totalRPE += parsed;

                count++;

            }

        }

    });



    if (count === 0) return null;          // no valid RPE data

    const avg = totalRPE / count;

    return Math.round(avg * 10) / 10;      // one decimal place, e.g., 7.3

}



    function updateNextWorkoutDate() {

        const container = document.getElementById('dashboardNextWorkout');

        if (!container) return;

        if (!currentWorkout) {

            container.textContent = "No workout scheduled";

            return;

        }

        const currentSplit = workoutProgram.splits.find(s => s.id === currentWorkout.type);

        const nextDate = new Date();

        nextDate.setDate(nextDate.getDate() + (currentSplit?.restAfter || 2));

        container.textContent = nextDate.toLocaleDateString('en-US', { 

            weekday: 'long', 

            month: 'short', 

            day: 'numeric' 

        });

    }



    function getRpeDescription(rpe) {

        const descriptions = {

            10: "Maximal effort", 9: "Very very hard", 8: "Very hard",

            7: "Hard", 6: "Moderately hard", 5: "Moderate",

            4: "Somewhat easy", 3: "Easy", 2: "Very easy", 1: "Extremely easy"

        };

        return descriptions[rpe] || "";

    }



/**

 * Returns a color for a given RPE using HSL gradient.

 * Green (120Â°) â†’ Red (0Â°) with 45% lightness for whiteâ€‘text contrast.

 * @param {number} rpe - 1 to 10

 * @returns {string} CSS hsl() color string

 */

function getRpeColor(rpe) {

    const hue = Math.max(0, 120 - (rpe - 1) * (120 / 9));

    return `hsl(${hue}, 100%, 45%)`;

}



/**

 * Creates a custom RPE dropdown element.

 * @param {number} index - The exercise index (for id generation)

 * @returns {HTMLElement} The dropdown container

 */

      

    function getWorkoutFocus(type) {

        const focusMap = {

            'full_body_a': 'Full Body - Strength Focus',

            'full_body_b': 'Full Body - Hypertrophy Focus',

            'push_day': 'Push - Chest, Shoulders, Triceps',

            'pull_day': 'Pull - Back, Biceps',

            'legs_day': 'Legs - Quads, Hamstrings, Glutes',

            'longevity_day': 'Joint Health & Mobility',

            'full_body_power': 'Strength + Power (11-component rotation)',

            'push_hypertrophy_balance': 'Hypertrophy + Balance (11-component rotation)',

            'pull_hypertrophy_coordination': 'Hypertrophy + Coordination (11-component rotation)',

            'legs_speed': 'Legs + Speed (11-component rotation)',

            'athletic_agility_reaction': 'Agility + Reaction (11-component rotation)',

            'cardio_endurance': 'Cardio Endurance (11-component rotation)',

            'mobility_flexibility': 'Mobility & Flexibility (11-component rotation)'

        };

        return focusMap[type] || 'Strength Training';

    }



    function formatNumber(num) {

        if (num >= 1000000) {

            return (num / 1000000).toFixed(1) + 'M';

        } else if (num >= 1000) {

            return (num / 1000).toFixed(1) + 'K';

        }

        return num.toLocaleString();

    }



    // DATA MANAGEMENT

function exportWorkoutData() {

    // Get user's first name (trim and capitalize)

    let firstName = (workoutData.user.name || "User").trim().split(' ')[0];

    if (firstName === "") firstName = "User";

    

    const now = new Date();

    const monthNames = ["January", "February", "March", "April", "May", "June",

                        "July", "August", "September", "October", "November", "December"];

    const month = monthNames[now.getMonth()];

    const day = now.getDate();

    const year = now.getFullYear();

    const fileName = `export_${firstName}_${month}_${day}_${year}.json`;



    const exportData = {

        ...workoutData,

        exportDeviceId: getDeviceId(),

        exportTimestamp: now.toISOString()

    };

    const dataStr = JSON.stringify(exportData, null, 2);

    const dataUri = 'data:application/json;charset=utf-8,' + encodeURIComponent(dataStr);

    const linkElement = document.createElement('a');

    linkElement.setAttribute('href', dataUri);

    linkElement.setAttribute('download', fileName);

    linkElement.click();

    showNotification("Data exported successfully!");

}



    function printHistory() {

        window.print();

    }



    // UI FUNCTIONS

    function showMainApp() {

        mainApp.style.display = 'block';

        updateDashboard();

        updateLongevityScoreDisplay();

    }



    function showModal(modalId) {

        document.getElementById(modalId).classList.add('active');

    }

      

    function openQuickActionsModal() {

        showModal('quickActionsModal');

    }



    function closeModal(modalId) {

        document.getElementById(modalId).classList.remove('active');

    }

// Store suppressed notification IDs for the current day

let suppressedNotifications = new Set();



// Load suppressed notifications from localStorage

function loadSuppressedNotifications() {

    const today = new Date().toLocaleDateString();

    const key = `suppressedNotifications_${today}`;

    const saved = localStorage.getItem(key);

    if (saved) {

        try {

            suppressedNotifications = new Set(JSON.parse(saved));

        } catch(e) {

            suppressedNotifications = new Set();

        }

    } else {

        suppressedNotifications.clear();

    }

}



// Save suppressed notifications to localStorage

function saveSuppressedNotifications() {

    const today = new Date().toLocaleDateString();

    const key = `suppressedNotifications_${today}`;

    localStorage.setItem(key, JSON.stringify([...suppressedNotifications]));

}

function addExerciseToWorkoutFromLibrary(exerciseId, exerciseName) {

    // Find the exercise in the ultimateExerciseLibrary

    let libraryExercise = null;

    for (const group in ultimateExerciseLibrary) {

        const found = ultimateExerciseLibrary[group].find(ex => ex.name.toLowerCase().replace(/\s/g, '_') === exerciseId);

        if (found) {

            libraryExercise = found;

            break;

        }

    }

    if (!libraryExercise) {

        showNotification('Exercise not found in library', 'error');

        return;

    }

    

    // Confirm with user

    Swal.fire({

        title: 'Add exercise?',

        text: `Add "${libraryExercise.name}" to your current workout?`,

        icon: 'question',

        showCancelButton: true,

        confirmButtonText: 'Yes, add',

        cancelButtonText: 'Cancel'

    }).then((result) => {

        if (result.isConfirmed) {

            // Create a new exercise object (similar to how generateExerciseFromLibrary does)

            const newExercise = {

                id: exerciseId,

                name: libraryExercise.name,

                muscleGroup: libraryExercise.muscles || [],

                prescribed: {

                    sets: libraryExercise.defaultSets,

                    reps: libraryExercise.defaultReps,

                    weight: null

                },

                actual: null,

                skipped: false,

                progressionNotes: libraryExercise.progression || '',

                equipment: libraryExercise.equipment,

                instructions: libraryExercise.instructions,

                prescriptionType: libraryExercise.prescriptionType || 'reps'

            };

            

            // Generate a proper prescription based on user's 1RM data (if any)

            const generated = generateExercisePrescription(newExercise);

            // Merge generated fields

            Object.assign(newExercise, generated);

            

            if (!currentWorkout) {

                // No active workout â€“ create one first

                generateNextWorkout();

                // After generation, add this exercise (but generateNextWorkout is async)

                // For simplicity, we'll just generate a new workout then add, but better to handle via callback

                // We'll do: after generation, push and re-render

                setTimeout(() => {

                    if (currentWorkout) {

                        currentWorkout.exercises.push(newExercise);

                        saveCurrentWorkoutToStorage();

                        if (document.getElementById('workout-section').classList.contains('active')) {

                            updateTodaysWorkout();

                            updateWorkoutHeader();

                        }

                        showNotification(`Added ${libraryExercise.name} to workout`, 'success');

                    }

                }, 500);

            } else {

                currentWorkout.exercises.push(newExercise);

                saveCurrentWorkoutToStorage();

                if (document.getElementById('workout-section').classList.contains('active')) {

                    updateTodaysWorkout();

                    updateWorkoutHeader();

                }

                showNotification(`Added ${libraryExercise.name} to workout`, 'success');

            }

        }

    });

}

// Clean up old suppression entries (keep only today's)

function clearOldSuppressionEntries() {

    const today = new Date().toLocaleDateString();

    for (let i = 0; i < localStorage.length; i++) {

        const key = localStorage.key(i);

        if (key && key.startsWith('suppressedNotifications_') && !key.includes(today)) {

            localStorage.removeItem(key);

        }

    }

}

let notificationTimeout = null;



function showNotification(message, type = "success", notificationId = null, onClickAction = null) {

    const id = notificationId || message.replace(/\s/g, '_').substring(0, 80);

    if (suppressedNotifications.has(id)) return;

    

    // Map type to Toastify style

    let backgroundColor;

    let icon;

    switch(type) {

        case 'success': backgroundColor = 'linear-gradient(135deg, #10b981, #059669)'; icon = 'âœ“'; break;

        case 'warning': backgroundColor = 'linear-gradient(135deg, #f59e0b, #d97706)'; icon = 'âš '; break;

        case 'error':   backgroundColor = 'linear-gradient(135deg, #ef4444, #dc2626)'; icon = 'âœ—'; break;

        default:        backgroundColor = 'linear-gradient(135deg, #06b6d4, #0891b2)'; icon = 'â„¹'; break;

    }

    

    // Build enriched HTML with a real "Don't show today" checkbox inside the toast

    const chkId = 'nsChk_' + Math.random().toString(36).slice(2, 9);

    const safeMsg = String(message).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');

    const enrichedHTML = '<span style="display:block">' + icon + '&nbsp;&nbsp;' + safeMsg + '</span>' +

        '<label style="margin-top:8px;font-size:11px;opacity:0.85;display:flex;align-items:center;gap:5px;cursor:pointer" onclick="event.stopPropagation()">' +

        '<input type="checkbox" id="' + chkId + '" style="margin:0">' +

        'Don\'t show this today</label>';

    

    const handleTap = function(e) {

        // Ignore taps on the checkbox / label so toggling it doesn\'t dismiss the toast or route

        if (e && e.target && (e.target.id === chkId || e.target.tagName === 'LABEL')) return;

        // Respect "Don\'t show today" checkbox

        try {

            var chk = document.getElementById(chkId);

            if (chk && chk.checked) {

                suppressedNotifications.add(id);

                saveSuppressedNotifications();

            }

        } catch (err) {}

        // Route by notificationId or custom callback

        if (typeof onClickAction === 'function') {

            try { onClickAction(e); } catch (err) {}

            return;

        }

        if (notificationId === 'retest_reminder') {

            try {

                if (typeof showSection === 'function') showSection('library');

            } catch (err) {}

        }

    };

    

    Toastify({

        text: enrichedHTML,

        escapeMarkup: false,

        duration: 6000,

        close: true,

        gravity: "top",

        position: "right",

        stopOnFocus: true,

        style: { background: backgroundColor, borderRadius: "12px", fontWeight: "600", padding: "10px 14px" },

        onClick: handleTap

    }).showToast();

}



    function showLoading(show) {

        document.getElementById('loading').classList.toggle('active', show);

    }



    // Initialize if no data exists

    if (!localStorage.getItem('workoutContinuityData')) {

    }

function renderLibrary() {

    const container = document.getElementById('libraryGrid');

    if (!container) return;



    const searchTerm = document.getElementById('librarySearch')?.value.toLowerCase() || '';

    const allExercises = getEnhancedExerciseList();



    // Build or update Fuse index

    if (!fuseIndex) {

        fuseIndex = new Fuse(allExercises, {

            keys: ['name', 'muscles', 'equipment', 'primaryMuscle'],

            threshold: 0.4,      // fuzzy matching sensitivity

            distance: 100,

            includeScore: true

        });

    }

    

    let filtered;

    if (searchTerm.length > 1) {

        const results = fuseIndex.search(searchTerm);

        filtered = results.map(r => r.item);

    } else {

        filtered = allExercises;

    }

    if (typeof mpLibraryFilteredList === 'function') filtered = mpLibraryFilteredList(filtered);



    // Group exercises

    const groups = {};

    filtered.forEach(ex => {

        let key;

        switch (currentGrouping) {

            case 'equipment': key = ex.equipment || 'Other'; break;

            case 'difficulty': key = ex.difficulty || 'medium'; break;

            case 'performed': key = ex.performed ? 'Done' : 'Not Done'; break;

            case 'muscleCategory':

                const muscleInfo = getAllMuscleGroups().find(m => m.name === ex.primaryMuscle);

                key = muscleInfo ? muscleInfo.category : 'other';

                break;

            case 'component': { const mpInfo = (typeof mpGetLookup === 'function' ? mpGetLookup()[ex.id] : null); const mpComps = mpInfo ? mpInfo.components : (ex.fitnessComponents || []); const mpMeta = (typeof MP_COMPONENT_MAP !== 'undefined') ? MP_COMPONENT_MAP[mpComps[0]] : null; key = mpMeta ? mpMeta.short : 'Other'; } break;

            default: key = ex.originalMuscleGroup; break;

        }

        if (!groups[key]) groups[key] = [];

        groups[key].push(ex);

    });



    const sortedKeys = Object.keys(groups).sort((a, b) => {

        if (currentGrouping === 'performed') return (a === 'Not Done' ? -1 : 1);

        return a.localeCompare(b);

    });



    document.getElementById('library-count').textContent = filtered.length;



    let html = '';

    sortedKeys.forEach(groupKey => {

        const exercisesInGroup = groups[groupKey];

        const groupId = `group-${groupKey.replace(/\s+/g, '_')}`;

        const isCollapsed = collapsedGroups[groupKey] || false;

        html += `

            <div class="library-group-header ${isCollapsed ? 'collapsed' : ''}" onclick="toggleGroup('${groupKey.replace(/'/g, "\\'")}')">

                <i class="fas fa-chevron-down group-toggle"></i>

                <h3>${groupKey} (${exercisesInGroup.length})</h3>

            </div>

            <div class="library-group-content" id="${groupId}">

        `;

        exercisesInGroup.forEach(ex => {

            const musclesList = ex.muscles ? ex.muscles.map(m => 

                `<span class="muscle-tag clickable" data-muscle="${m}" onclick="showMuscleImage(this.dataset.muscle)">${getMuscleDisplayName(m)}</span>`

            ).join('') : '';

            const equipment = ex.equipment ? `<span style="font-size:0.8rem; color:var(--gray-500);"><i class="fas fa-tools"></i> ${ex.equipment}</span>` : '';

            let prescription = `${ex.defaultSets} sets Ã— ${ex.defaultReps}`;

            if (ex.prescriptionType === 'time') prescription = `${ex.defaultSets} sets Ã— ${ex.defaultDuration || 60} sec`;

            else if (ex.prescriptionType === 'amrap') prescription += ' (AMRAP)';

            const record = workoutData.exercises[ex.id] || {};

            let testInfo = '';

            if (record.tested1RM) testInfo = `<div style="font-size:0.8rem; color:var(--success); margin-top:5px;">ðŸ† Tested 1RM: ${Math.round(record.tested1RM)} lbs</div>`;

            const performedBadge = ex.performed ? `<span class="badge badge-success">âœ” Done ${ex.performedCount}x</span>` : `<span class="badge badge-info">New</span>`;

            let difficultyBadge = '';

            if (ex.difficulty === 'easy') difficultyBadge = `<span class="badge badge-success">Easy</span>`;

            else if (ex.difficulty === 'medium') difficultyBadge = `<span class="badge badge-warning">Medium</span>`;

            else if (ex.difficulty === 'hard') difficultyBadge = `<span class="badge badge-danger">Hard</span>`;

            const safeName = ex.name.replace(/'/g, "\\'");

            html += `

                <div class="chart-card hover-lift" style="padding: 20px; display: flex; flex-direction: column; justify-content: space-between;">

                    <div>

                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">

                            <h3 style="color: var(--primary); font-size:1.2rem; margin:0;">${ex.name}</h3>

                            ${difficultyBadge}

                        </div>

                        <div style="margin-bottom: 10px;">${musclesList}</div>

                        <div style="margin-bottom: 8px;">${equipment}</div>

                        ${testInfo}

                        <div style="font-weight:600; color:var(--dark);">ðŸ“‹ ${prescription}</div>

                        <div style="margin-top:10px; font-size:0.85rem; color:var(--gray-600);">

                            <i class="fas fa-lightbulb"></i> ${ex.progression || 'Progressive overload recommended'}

                        </div>

                    </div>

                    <div style="margin-top: 15px; display: flex; flex-wrap: wrap; gap: 8px; align-items: center;">

                        <button class="btn btn-sm" style="flex:1;" onclick="showExerciseSearch('${safeName}')">

                            <i class="fas fa-search"></i> How to do

                        </button>

                        <div class="btn btn-sm btn-outline" style="flex:1; text-align:center; pointer-events:none;">

                            <i class="fas fa-chart-line"></i> Est. 1RM: ${(() => {

                                const r = workoutData.exercises[ex.id];

                                return (r && r.mu && r.mu > 0) ? Math.round(r.mu) + ' lbs' : 'â€”';

                            })()}

                        </div>

                      <button class="btn btn-sm btn-success" style="flex:1;" onclick="addExerciseToWorkoutFromLibrary('${ex.id}', '${ex.name.replace(/'/g, "\\'")}')">

                          <i class="fas fa-plus"></i> Add to Workout

                      </button>

                    </div>

                    <div style="margin-top: 8px; text-align: right;">

                        ${performedBadge}

                        ${(() => {

                            const exRec = workoutData.exercises[ex.id];

                            let lastHtml = '';

                            if (exRec && Array.isArray(exRec.history) && exRec.history.length > 0) {

                                const lastEntry = [...exRec.history].reverse().find(e => !e.skipped);

                                if (lastEntry && typeof lastEntry.weight === 'number') {

                                    const w = lastEntry.weight === 0 ? 'bodyweight' : lastEntry.weight.toFixed(1) + ' lbs';

                                    lastHtml = `<div style="font-size:0.9rem; color:var(--gray-600);">Last: ${w}</div>`;

                                } else { lastHtml = `<div style="font-size:0.9rem; color:var(--gray-600);">Last: â€”</div>`; }

                            } else { lastHtml = `<div style="font-size:0.9rem; color:var(--gray-600);">Last: â€”</div>`; }

                            let nextHtml = '';

                            if (exRec && exRec.nextWeight && typeof exRec.nextWeight === 'number') {

                                nextHtml = `<div style="margin-top:4px; font-size:0.9rem; color:var(--success);">â†‘ Next: ${exRec.nextWeight.toFixed(1)} lbs</div>`;

                            }

                            return lastHtml + nextHtml;

                        })()}

                    </div>

                </div>

            `;

        });

        html += '</div>';

    });

    if (html === '') html = '<div class="empty-state" style="grid-column:1/-1;"><i class="fas fa-dumbbell"></i><h3>No exercises match your search</h3></div>';

    container.innerHTML = html;

    

    // Re-initialize tooltips on new muscle tags

    setTimeout(() => { if (typeof initTooltips === 'function') initTooltips(); }, 50);

}

      

// Group toggle functions

function toggleGroup(groupKey) {

    collapsedGroups[groupKey] = !collapsedGroups[groupKey];

    saveLibraryCollapse();

    renderLibrary(); // re-render to reflect collapse state

}



function expandAllGroups() {

    collapsedGroups = {};

    saveLibraryCollapse();

    refreshLibraryExpandCollapseButtons();

    renderLibrary();

}



function collapseAllGroups() {

    const groups = document.querySelectorAll('.library-group-header h3');

    groups.forEach(header => {

        const key = header.textContent.split(' (')[0]; // extract group name

        collapsedGroups[key] = true;

    });

    saveLibraryCollapse();

    refreshLibraryExpandCollapseButtons();

    renderLibrary();

}



/* Show only the relevant button: Expand when any collapsed, Collapse when any expanded */

function refreshLibraryExpandCollapseButtons() {

    var keys = Object.keys(collapsedGroups);

    var anyCollapsed = keys.some(function (k) { return collapsedGroups[k]; });

    var anyExpanded = keys.length === 0 || keys.some(function (k) { return !collapsedGroups[k]; });

    var exp = document.getElementById('expandAllBtn');

    var col = document.getElementById('collapseAllBtn');

    if (exp) exp.style.display = anyCollapsed ? '' : 'none';

    if (col) col.style.display = anyExpanded ? '' : 'none';

}



function changeGrouping() {

    currentGrouping = document.getElementById('groupBySelect').value;

    collapsedGroups = {}; // reset collapsed state

    saveLibraryCollapse();

    renderLibrary();

}



// Debounced search

function debouncedLibrarySearch() {

    clearTimeout(librarySearchTimeout);

    librarySearchTimeout = setTimeout(() => {

        // Rebuild Fuse index when exercise library changes (e.g., after import)

        fuseIndex = null;

        renderLibrary();

    }, 300);

}

      

async function saveWorkoutData() {

    try {

        // 1. Save user

        await db.user.put({ key: 'main', value: workoutData.user });



        // 2. Save workouts â€“ ensure each has an id

        const validWorkouts = workoutData.workouts.filter(w => w && w.id);

        if (validWorkouts.length !== workoutData.workouts.length) {

            console.warn('Skipped some workouts without id:', workoutData.workouts.filter(w => !w || !w.id));

        }

        await db.workouts.bulkPut(validWorkouts);



        // 3. Save exercises â€“ id is taken from object key

        const exerciseRecords = Object.entries(workoutData.exercises)

            .map(([id, data]) => ({ id, ...data }));

        await db.exercises.bulkPut(exerciseRecords);

    } catch (err) {

        console.error('Dexie save error:', err);

        // Optionally fallback to localStorage?

    }

}



async function saveToLocalStorage() {

    await saveWorkoutData();

}



async function migrateFromLocalStorage() {

    const count = await db.workouts.count();

    if (count > 0) return; // already migrated

    const oldData = localStorage.getItem('workoutContinuityData');

    if (!oldData) return;

    let parsed;

    try {

        parsed = JSON.parse(oldData);

    } catch (e) {

        return;

    }

    if (parsed.workouts && parsed.workouts.length) {

        await db.workouts.bulkPut(parsed.workouts);

    }

    if (parsed.exercises) {

        const exerciseRecords = Object.entries(parsed.exercises).map(([id, data]) => ({ id, ...data }));

        await db.exercises.bulkPut(exerciseRecords);

    }

    if (parsed.user) {

        await db.user.put({ key: 'main', value: parsed.user });

    }

    if (parsed.savedWorkout) {

        await db.savedWorkout.put({ id: 'current', value: parsed.savedWorkout });

    }

}

async function saveCurrentWorkoutToStorage() {

    if (!currentWorkout) return;

    try {

        await db.savedWorkout.put({ id: 'current', value: currentWorkout });

        savedWorkout = currentWorkout;

    } catch (e) {

        console.error("Critical save failure:", e);

        // Fallback to localStorage

        localStorage.setItem('workoutEmergencyBackup', JSON.stringify({

            workout: currentWorkout,

            timestamp: Date.now(),

            version: 2

        }));

    }

    updateResumeButtonVisibility();

}

// ==== DEEP ROBUST LOAD & WRITE-BACK SYSTEM ====

let saveWorkoutTimeout = null;



function saveWorkoutDraftImmediately() {

    if (!currentWorkout) return;

    if (saveWorkoutTimeout) clearTimeout(saveWorkoutTimeout);

    

    // Update metadata

    if (!currentWorkout.draftStartedAt) {

        currentWorkout.draftStartedAt = new Date().toISOString();

        currentWorkout.activeDates = [];

    }

    currentWorkout.lastModifiedAt = new Date().toISOString();

    const today = new Date().toISOString().split('T')[0];

    if (!currentWorkout.activeDates) currentWorkout.activeDates = [];

    if (!currentWorkout.activeDates.includes(today)) {

        currentWorkout.activeDates.push(today);

    }

    

    // Debounce the actual save (200ms)

    saveWorkoutTimeout = setTimeout(() => {

        performSave();

        saveWorkoutTimeout = null;

    }, 200);

}



// Separate function that does the actual save and emergency backup

function performSave() {

    if (!currentWorkout) return;

    // Call the existing Dexie save (which also backs up to localStorage)

    saveCurrentWorkoutToStorage();

    // Additionally, store a timestamped emergency backup in localStorage

    // This survives even if Dexie gets corrupted

    localStorage.setItem('workoutEmergencyBackup', JSON.stringify({

        workout: currentWorkout,

        timestamp: Date.now(),

        version: 2

    }));

}



// Force an immediate save on page visibility change (tab switch, minimize, etc.)

function handleVisibilityChange() {

    if (document.visibilityState === 'hidden') {

        // Cancel any pending debounce and save immediately

        if (saveWorkoutTimeout) {

            clearTimeout(saveWorkoutTimeout);

            saveWorkoutTimeout = null;

        }

        performSave();

    }

}



// Force save on page unload (closing tab, refreshing)

function handlePageHide() {

    if (saveWorkoutTimeout) {

        clearTimeout(saveWorkoutTimeout);

        saveWorkoutTimeout = null;

    }

    performSave();

}



// Register the event listeners (call this once during app initialization)

function registerAutoSaveListeners() {

    document.addEventListener('visibilitychange', handleVisibilityChange);

    window.addEventListener('pagehide', handlePageHide);

    // Also use beforeunload for older browsers

    window.addEventListener('beforeunload', handlePageHide);

}



// If the app hasn't already registered these listeners, call this function.

// You can call it inside your DOMContentLoaded or init function.

if (typeof window.autoSaveListenersRegistered === 'undefined') {

    registerAutoSaveListeners();

    window.autoSaveListenersRegistered = true;

}

// Calculate severity score (0.2 to 2.0) for a zeroâ€‘rep failure

function calculateFailureSeverity(exercise, user, attemptedWeight) {

    let severity = 1.0;

    

    if (user.experience === 'advanced') severity *= 1.5;

    else if (user.experience === 'beginner') severity *= 0.7;

    

    const phase = getCurrentCyclePhase();

    if (user.gender === 'male') severity *= 1.2;

    else if (user.gender === 'female' && (phase === 'luteal' || phase === 'menstrual')) severity *= 0.8;

    

    const systemicFatigue = 1 - calculateSystemicRecoveryFactor();

    severity *= (1 + systemicFatigue);

    

    const age = calculateAge(user.birthDate) || 30;

    if (age < 30) severity *= 1.2;

    else if (age > 60) severity *= 0.6;

    

    const userWeight = user.weight || 150;

    const weightFactor = 150 / userWeight;

    severity *= Math.min(1.5, Math.max(0.5, weightFactor));

    

    const primaryMuscle = exercise.muscleGroup?.[0];

    if (primaryMuscle && workoutData.user.muscleEWMA && workoutData.user.muscleEWMA[primaryMuscle] > 15000) {

        severity *= 0.8;

    }

    

    return Math.min(2.0, Math.max(0.2, severity));

}



// Apply penalty for zeroâ€‘rep failure

function applyFailurePenalty(exerciseId, attemptedWeight, severity) {

    const exRecord = getOrCreateExerciseRecord(exerciseId);

    

    if (!exRecord.originalMu) {

        exRecord.originalMu = exRecord.mu || 0;

    }

    const muDropPercent = Math.min(0.3, 0.1 * severity);

    if (exRecord.mu) {

        exRecord.mu = exRecord.mu * (1 - muDropPercent);

    }

    

    exRecord.failedAtWeight = attemptedWeight;

    exRecord.failedSeverity = severity;

    exRecord.failedDate = new Date().toISOString();

    

    if (!workoutData.user.muscleGroupStats) workoutData.user.muscleGroupStats = {};

    const muscles = getExerciseById(exerciseId)?.muscles || [];

    for (const muscle of muscles) {

        const currentStat = workoutData.user.muscleGroupStats[muscle] || 100;

        const statDrop = Math.min(0.15, 0.05 * severity);

        workoutData.user.muscleGroupStats[muscle] = Math.max(0, currentStat * (1 - statDrop));

    }

    

    saveToLocalStorage();

}



// Recovery when the user later completes at least one rep

function recoverFromFailure(exerciseId) {

    const exRecord = getOrCreateExerciseRecord(exerciseId);

    if (!exRecord.originalMu && !exRecord.failedAtWeight) return false;

    

    if (exRecord.originalMu) {

        exRecord.mu = exRecord.originalMu;

        delete exRecord.originalMu;

    }

    

    const severity = exRecord.failedSeverity || 1.0;

    const bonus = 0.03 * (1 - severity);

    const muscles = getExerciseById(exerciseId)?.muscles || [];

    for (const muscle of muscles) {

        if (workoutData.user.muscleGroupStats && workoutData.user.muscleGroupStats[muscle]) {

            workoutData.user.muscleGroupStats[muscle] = Math.min(100, workoutData.user.muscleGroupStats[muscle] * (1 + bonus));

        }

    }

    

    delete exRecord.failedAtWeight;

    delete exRecord.failedSeverity;

    delete exRecord.failedDate;

    

    saveToLocalStorage();

    

    showCelebration(`ðŸŽ‰ You conquered ${getExerciseById(exerciseId)?.name}! Your strength is back +${Math.round(bonus*100)}% bonus!`, 'success');

    return true;

}



// Simple celebration (confetti optional)

function showCelebration(message, type) {

    showNotification(message, type, 'failure_recovery');

    if (typeof confetti === 'function') confetti({ particleCount: 100, spread: 70, origin: { y: 0.6 } });

}

// Helper: robust save with verification

async function robustSaveCurrentWorkout(workout, retries = 3) {

    if (!workout) return false;

    for (let attempt = 1; attempt <= retries; attempt++) {

        try {

            // 1. Save to IndexedDB

            await db.savedWorkout.put({ id: 'current', value: workout });

            // 2. Verify by reading back

            const verify = await db.savedWorkout.get('current');

            if (verify && verify.value && verify.value.id === workout.id) {

                // 3. Update inâ€‘memory reference

                savedWorkout = workout;

                // 4. Also store a timestamped backup in localStorage (synchronous fallback)

                localStorage.setItem('workoutEmergencyBackup', JSON.stringify({

                    workout: workout,

                    timestamp: Date.now(),

                    version: 2

                }));

                console.log(`[RobustSave] Success (attempt ${attempt})`);

                return true;

            }

        } catch (err) {

            console.warn(`[RobustSave] Attempt ${attempt} failed:`, err);

            if (attempt === retries) {

                // Last resort: only localStorage backup

                localStorage.setItem('workoutEmergencyBackup', JSON.stringify({

                    workout: workout,

                    timestamp: Date.now(),

                    version: 2,

                    degraded: true

                }));

                return false;

            }

            await new Promise(r => setTimeout(r, 100 * attempt)); // backoff

        }

    }

    return false;

}



// Recovery from emergency backup

async function recoverFromEmergencyBackup() {

    const emergencyRaw = localStorage.getItem('workoutEmergencyBackup');

    if (!emergencyRaw) return null;

    try {

        const emergency = JSON.parse(emergencyRaw);

        // Only use if younger than 10 seconds (recent crash)

        if (Date.now() - emergency.timestamp < 10000 && emergency.workout) {

            localStorage.removeItem('workoutEmergencyBackup');

            return emergency.workout;

        } else {

            // Too old â€“ discard

            localStorage.removeItem('workoutEmergencyBackup');

        }

    } catch (e) {}

    return null;

}



// Main load function

async function loadAllData() {



    // ========== 1. LOAD FROM INDEXEDDB ==========

    const [workouts, exerciseRecords, userRecord, savedRecord] = await Promise.all([

        db.workouts.toArray(),

        db.exercises.toArray(),

        db.user.get('main'),

        db.savedWorkout.get('current')

    ]);



    // Rebuild exercises object

    const exercises = {};

    exerciseRecords.forEach(rec => {

        const { id, ...rest } = rec;

        exercises[id] = rest;

    });



    // Load user data (with defaults)

    const defaultUser = {

        name: "", birthDate: null, gender: "male", weight: null, height: null,

        experience: "intermediate", goal: "balanced", created: new Date().toISOString(),

        settings: { workoutDays: ["monday", "wednesday", "friday"], restTime: 90, progressionRate: 0.02, theme: "blue", darkMode: false, muscleCoupling: false, bottomNavAutoHide: false },

        agingRisks: [], menstrual: { lastPeriodStart: null, cycleLength: 28, symptoms: [] },

        strengthComposite: 0, bodyWeightHistory: [], geneticPotential: { maxLeanMass: 0, sGenetic: 0 },

        neuralPhaseEnd: null, aggregates: { totalVolumeLast7Days: 0, avgRPELast7Days: 0, volumeByMuscleLast60Days: {} }

    };

    const user = userRecord ? userRecord.value : defaultUser;

    

    // Merge any missing user fields, but preserve _cache if it exists in user

    workoutData.user = { ...defaultUser, ...user };

    // Ensure cache object exists after merge (in case user had no _cache)

    if (!workoutData.user._cache) workoutData.user._cache = {};

    workoutData.workouts = workouts || [];

    workoutData.exercises = exercises;

    sanitizeExerciseMu();



    // ========== 2. REBUILD DERIVED DATA ==========

    if (typeof rebuildFatigueFromHistory === 'function') rebuildFatigueFromHistory();

    if (typeof calculateMuscleLastTrained === 'function') calculateMuscleLastTrained();

    if (typeof backfillLastPerformed === 'function') backfillLastPerformed();

    if (typeof recomputeAggregates === 'function') recomputeAggregates();

    if (typeof updateBodyWeightEstimate === 'function') updateBodyWeightEstimate();

        updateHeaderNameAndStreak(); 



    // ========== 3. EMERGENCY BACKUP RECOVERY (with version and timestamp) ==========

    let emergencyWorkout = null;

    let emergencyTimestamp = 0;

    try {

        const emergencyRaw = localStorage.getItem('workoutEmergencyBackup');

        if (emergencyRaw) {

            const emergency = JSON.parse(emergencyRaw);

            if (emergency.workout && emergency.version === 2 && emergency.timestamp) {

                emergencyTimestamp = emergency.timestamp;

                emergencyWorkout = emergency.workout;

                console.log(`[LoadAll] Emergency backup found from ${new Date(emergencyTimestamp).toISOString()}`);

            } else if (emergency.workout && emergency.version === 1) {

                // Migrate old backup to version 2

                emergencyWorkout = emergency.workout;

                emergencyTimestamp = emergency.timestamp || 0;

            }

        }

    } catch(e) { console.warn('[LoadAll] Emergency backup read failed', e); }



    // ========== 4. DETERMINE THE BEST WORKOUT TO RESTORE ==========

    let loadedSavedWorkout = savedRecord ? savedRecord.value : null;

    let bestWorkout = null;

    let bestScore = -1;



    // Score function: higher = better (more recent, more actual data)

    function scoreWorkout(workout) {

        if (!workout || !workout.exercises) return -1;

        let score = 0;

        // Timestamp (lastModifiedAt or date)

        const ts = workout.lastModifiedAt ? new Date(workout.lastModifiedAt).getTime() : (workout.date ? new Date(workout.date).getTime() : 0);

        score += ts / 1000; // weight by time

        // Bonus for each exercise with actual data (logged)

        const loggedCount = workout.exercises.filter(ex => ex.actual && !ex.skipped).length;

        score += loggedCount * 10000; // strong preference for workouts with logged data

        // Penalty: completed workouts should never be restored

        if (workout.dateCompleted) score = -1e9;

        return score;

    }



    if (loadedSavedWorkout && loadedSavedWorkout.exercises && loadedSavedWorkout.exercises.length > 0) {

        bestWorkout = loadedSavedWorkout;

        bestScore = scoreWorkout(loadedSavedWorkout);

    }

    if (emergencyWorkout && emergencyWorkout.exercises && emergencyWorkout.exercises.length > 0) {

        const emergencyScore = scoreWorkout(emergencyWorkout);

        if (emergencyScore > bestScore) {

            bestWorkout = emergencyWorkout;

            bestScore = emergencyScore;

        }

    }



    // ========== 5. RESTORE CURRENT WORKOUT ==========

    if (bestWorkout) {

        // Deep clone to avoid reference issues

        let candidateWorkout = JSON.parse(JSON.stringify(bestWorkout));

        

        // Ensure required fields exist

        if (!candidateWorkout.activeDates) candidateWorkout.activeDates = [];

        if (!candidateWorkout.lastModifiedAt) candidateWorkout.lastModifiedAt = candidateWorkout.date || new Date().toISOString();

        if (!candidateWorkout.id) candidateWorkout.id = `workout_${Date.now()}`;

        

        // Sanitize each exercise

        candidateWorkout.exercises.forEach((ex, idx) => {

            if (ex.actual) {

                if (!ex.actual.notes) ex.actual.notes = '';

            } else if (ex.skipped) {

                // keep skipped flag

            } else {

                if (!ex.prescribed) {

                    ex.prescribed = { sets: 3, reps: '8-12', weight: 0 };

                }

            }

            if (!ex.id) ex.id = `temp_${idx}`;

        });

        

        // Check if the workout is COMPLETED (all exercises have either actual or skipped)

        const isCompleted = candidateWorkout.exercises.length > 0 && 

                            candidateWorkout.exercises.every(ex => ex.actual || ex.skipped);

        

        if (isCompleted) {

            console.log(`[LoadAll] Workout ${candidateWorkout.id} is already completed. Clearing draft.`);

            currentWorkout = null;

            savedWorkout = null;

            await db.savedWorkout.delete('current');

            localStorage.removeItem('workoutEmergencyBackup');

            // Generate a fresh workout for today

            generateNextWorkout();

        } else {

            // Incomplete workout â€“ restore it as a draft

            currentWorkout = candidateWorkout;

            // Do NOT merge old actual logs from global workouts array

            await db.savedWorkout.put({ id: 'current', value: currentWorkout });

            savedWorkout = currentWorkout;

            console.log(`[LoadAll] Incomplete workout restored with ${currentWorkout.exercises.filter(e => e.actual).length} logged exercises`);

        }

    } else {

        currentWorkout = null;

        savedWorkout = null;

        await db.savedWorkout.delete('current');

        localStorage.removeItem('workoutEmergencyBackup');

    }



    // ========== 6. UPDATE UI AND CLEANUP ==========

    // Update global variables

    window.currentWorkout = currentWorkout;

    window.savedWorkout = savedWorkout;

    

    // ---- NEW: Populate user cache and workout cache ----

    updateUserCache();

    if (currentWorkout) updateWorkoutCache();

    // ----------------------------------------------------



    // Remove loading class after a short delay

    document.body.classList.add('loading');

    setTimeout(() => {

        document.body.classList.remove('loading');

    }, 300);

    

    // Update resume button visibility

    if (typeof updateResumeButtonVisibility === 'function') updateResumeButtonVisibility();

    

    // Force refresh of current section if it's workout or dashboard

    const activeSection = document.querySelector('.section.active');

    if (activeSection) {

        const sectionId = activeSection.id.replace('-section', '');

        if (sectionId === 'workout' && typeof updateTodaysWorkout === 'function') {

            updateTodaysWorkout();

            updateWorkoutHeader();

        } else if (sectionId === 'dashboard' && typeof updateDashboard === 'function') {

            updateDashboard();

        }

    }

}



async function loadSavedWorkoutFromStorage() {

    const record = await db.savedWorkout.get('current');

    savedWorkout = record ? record.value : null;

    updateResumeButtonVisibility();

}



async function clearSavedWorkout() {

    await db.savedWorkout.delete('current');

    savedWorkout = null;

    updateResumeButtonVisibility();

}

      

function updateResumeButtonVisibility() {

    const topResume = document.getElementById('resumeWorkoutBtn');

    const topRegen = document.getElementById('regenerateWorkoutBtn');

    const bottomResume = document.getElementById('bottomResumeWorkoutBtn');

    const bottomRegen = document.getElementById('bottomRegenerateWorkoutBtn');

    

    const shouldShowResume = savedWorkout && (!currentWorkout || currentWorkout.id !== savedWorkout.id);

    

    // Top navbar buttons

    if (topResume) topResume.style.display = shouldShowResume ? 'inline-flex' : 'none';

    

    // Bottom nav buttons

    if (bottomResume) bottomResume.style.display = shouldShowResume ? 'flex' : 'none';

}

      

function resumeSavedWorkout() {

    if (!savedWorkout) return;

    if (!savedWorkout.exercises || savedWorkout.exercises.length === 0) {

        clearSavedWorkout();

        generateNextWorkout();

        return;

    }

    currentWorkout = savedWorkout;

    // NEW: Update workout cache with recovery factor and session bases

    updateWorkoutCache();

    // ----------------------------------------------------

    requestWakeLock(); 

    updateDashboard();

    updateTodaysWorkout();

    updateWorkoutHeader();

    updateResumeButtonVisibility();   // hide the button now that workout is current

    showNotification('Workout restored!', 'success');

    showSection('workout');

}

    

// Theme functions

function setTheme(themeName) {

    document.documentElement.setAttribute('data-theme', themeName);

    localStorage.setItem('workoutTheme', themeName);

    // Update active class on theme buttons

    document.querySelectorAll('.theme-btn').forEach(btn => {

        btn.classList.remove('active');

        if (btn.classList.contains(`theme-${themeName}`)) {

            btn.classList.add('active');

        }

    });

    // If there's a dashboard chart, update it (optional)

    if (charts.dashboard) {

        updateDashboardChart(); // This will redraw the chart with the new theme colours

    }

}



function toggleDarkMode() {

    const isDarkMode = document.getElementById('darkModeToggle').checked;

    if (isDarkMode) {

        document.documentElement.setAttribute('data-theme', 'dark');

    } else {

        // Revert to the saved theme (or default 'blue')

        const savedTheme = localStorage.getItem('workoutTheme') || 'blue';

        document.documentElement.setAttribute('data-theme', savedTheme);

    }

    localStorage.setItem('workoutDarkMode', isDarkMode);

}



function loadThemeSettings() {

    const savedTheme = localStorage.getItem('workoutTheme') || 'blue';

    const savedDarkMode = localStorage.getItem('workoutDarkMode') === 'true';

    setTheme(savedTheme);

    if (savedDarkMode) {

        document.documentElement.setAttribute('data-theme', 'dark');

        document.getElementById('darkModeToggle').checked = true;

    }

}

      

async function startOrGenerateWorkout() {

    // If a workout already exists, ask user before replacing

    if (currentWorkout) {

        // Check if there is any unsaved progress (exercises without actual data and not skipped)

        const hasUnsaved = currentWorkout.exercises && currentWorkout.exercises.some(ex => !ex.actual && !ex.skipped);

        

        let warningMessage = 'You already have a workout. Generating a new one will replace it.';

        if (hasUnsaved) {

            warningMessage = 'You have unsaved progress in your current workout. Generating a new one will replace it and any unsaved data will be lost.';

        } else {

            warningMessage = 'You already have a workout. Generating a new one will replace it. (Your logged exercises are already saved.)';

        }

        

        const result = await Swal.fire({

            title: 'Generate New Workout?',

            text: warningMessage,

            icon: hasUnsaved ? 'warning' : 'question',

            showCancelButton: true,

            confirmButtonColor: '#d33',

            cancelButtonColor: '#3085d6',

            confirmButtonText: 'Yes, generate new',

            cancelButtonText: 'Cancel'

        });

        

        if (!result.isConfirmed) {

            requestWakeLock();

            showSection('workout');

            return;

        }

        

        // User confirmed â€“ clear current workout and generate new one

        currentWorkout = null;

        clearSavedWorkout();

    }



    // Generate a new workout

    showLoading(true);

    try {

        await generateNextWorkout();

        if (currentWorkout) {

            requestWakeLock();

            showSection('workout');

        } else {

            showNotification("Could not generate workout. Please try again.", "error");

        }

    } catch (error) {

        console.error("Workout generation error:", error);

        showNotification("Workout generation failed.", "error");

    } finally {

        showLoading(false);

    }

}

      

function toggleWorkoutDetails() {

    const details = document.getElementById('workoutDetails');

    const btn = document.getElementById('toggleDetailsBtn');

    details.classList.toggle('collapsed');

    btn.textContent = details.classList.contains('collapsed') ? 'View details' : 'Hide details';

}

      

clearOldSuppressionEntries();

loadSuppressedNotifications();



// ==================== FIX LOG (Tier 3) ====================

function openFixModal(workoutId, exerciseIndex) {

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout) return;

    const exercise = workout.exercises[exerciseIndex];

    if (!exercise || !exercise.actual) {

        showNotification('No logged data to fix.', 'warning');

        return;

    }



    document.getElementById('fixWorkoutId').value = workoutId;

    document.getElementById('fixExerciseIndex').value = exerciseIndex;



    const actual = exercise.actual;

    const weight = actual.weight || 0;

    const reps = actual.reps ? actual.reps.join(', ') : '';

    const rpe = actual.rpe || 0;

    const notes = actual.notes || '';



    const content = document.getElementById('fixLogContent');

    content.innerHTML = `

        <div style="margin-bottom: 15px;">

            <strong>${exercise.name}</strong>

        </div>

        <div class="form-group">

            <label>Weight (lbs):</label>

            <input type="number" id="fixWeight" value="${weight}" step="2.5" min="0" style="min-height: 44px;">

        </div>

        <div class="form-group">

            <label>Reps (comma separated, e.g., 8, 8, 7, 6):</label>

            <input type="text" id="fixReps" value="${reps}" placeholder="8, 8, 7, 6" style="min-height: 44px;">

        </div>

        <div class="form-group">

            <label>RPE (1-10):</label>

            <input type="number" id="fixRpe" value="${rpe}" min="1" max="10" step="1" style="min-height: 44px;">

        </div>

        <div class="form-group">

            <label>Notes:</label>

            <textarea id="fixNotes" rows="2" style="min-height: 44px;">${notes}</textarea>

        </div>

    `;



    showModal('fixLogModal');

}



function saveFixLog() {

    const workoutId = document.getElementById('fixWorkoutId').value;

    const exerciseIndex = parseInt(document.getElementById('fixExerciseIndex').value);

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout || !workout.exercises[exerciseIndex]) {

        showNotification('Workout or exercise not found.', 'error');

        return;

    }



    const exercise = workout.exercises[exerciseIndex];

    const weight = parseFloat(document.getElementById('fixWeight').value);

    const repsRaw = document.getElementById('fixReps').value.trim();

    const rpe = parseInt(document.getElementById('fixRpe').value);

    const notes = document.getElementById('fixNotes').value.trim();



    // Validation

    if (isNaN(weight) || weight <= 0) {

        showNotification('Please enter a valid weight.', 'warning');

        return;

    }

    const reps = repsRaw.split(',').map(r => parseInt(r.trim())).filter(r => !isNaN(r) && r > 0);

    if (reps.length === 0) {

        showNotification('Please enter at least one valid rep number.', 'warning');

        return;

    }

    if (isNaN(rpe) || rpe < 1 || rpe > 10) {

        showNotification('RPE must be between 1 and 10.', 'warning');

        return;

    }



    // Update the actual object

    exercise.actual.weight = weight;

    exercise.actual.reps = reps;

    exercise.actual.rpe = rpe;

    exercise.actual.notes = notes;



    // Recalc 1RM for this exercise

    const bestReps = Math.max(...reps);

    updateRecursive1RM(exercise.id, weight, bestReps, rpe);



    // Recalc fatigue (using total volume)

    const totalReps = reps.reduce((a, b) => a + b, 0);

    updateMuscleFatigue(exercise.id, weight, totalReps, rpe, workout.date);



    // Recalc workout summary (volume, RPE)

    recalcWorkoutSummary(workout);



    // Save everything

    saveToLocalStorage();



    // Close modal and refresh history

    closeModal('fixLogModal');

    updateWorkoutHistory();

    // If the detail row is open, refresh it (optional â€“ the user can reâ€‘expand)

    showNotification('Exercise log updated and recalculation done.', 'success');

}



function recalcWorkoutSummary(workout) {

    let totalVolume = 0;

    let totalRPE = 0;

    let rpeCount = 0;

    let completedCount = 0;



    workout.exercises.forEach(ex => {

        if (ex.actual && !ex.skipped) {

            const actual = ex.actual;

            if (actual.reps && actual.reps.length > 0) {

                const repsTotal = actual.reps.reduce((a, b) => a + b, 0);

                totalVolume += actual.weight * repsTotal;

                if (actual.rpe) {

                    totalRPE += actual.rpe;

                    rpeCount++;

                }

                completedCount++;

            }

        }

    });



    workout.summary = {

        totalVolume: totalVolume,

        averageRPE: rpeCount > 0 ? (totalRPE / rpeCount) : 0,

        completedExercises: completedCount,

        notes: workout.summary?.notes || ''

    };

}

      

    function deleteWorkout(index) {

        const workout = workoutData.workouts[index];

        if (!workout) return;

        Swal.fire({

            title: `Delete "${workout.name || 'Untitled'}"?`,

            text: 'This action cannot be undone.',

            icon: 'warning',

            showCancelButton: true,

            confirmButtonColor: '#d33',

            confirmButtonText: 'Yes, delete',

            cancelButtonText: 'Cancel'

        }).then((result) => {

            if (result.isConfirmed) {

                workoutData.workouts.splice(index, 1);

                saveToLocalStorage();

                updateWorkoutHistory();

                // Also update dashboard if current section

                if (document.getElementById('dashboard-section').classList.contains('active')) {

                    updateDashboard();

                }

                showNotification('Workout deleted.', 'success');

            }

        });

    }

function toggleRowEdit(row) {

    const isEditing = row.dataset.editing === 'true';

    const workoutId = row.dataset.workoutId;

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout) return;



    const dateCell = row.querySelector('.date-col');

    const workoutCell = row.querySelector('.workout-col');

    const editBtn = row.querySelector('.btn-primary');



    if (isEditing) {

        // If already editing, do nothing (or you could treat as save)

        // But we use separate Save and Cancel buttons, so this won't be called.

        return;

    }



    // Enter edit mode

    row.dataset.editing = 'true';



    // Replace date cell with input

    const currentDate = new Date(workout.date);

    const dateStr = currentDate.toISOString().split('T')[0];

    const dateInput = document.createElement('input');

    dateInput.type = 'date';

    dateInput.className = 'inline-edit-date';

    dateInput.value = dateStr;

    dateInput.style.width = 'auto';

    dateCell.innerHTML = '';

    dateCell.appendChild(dateInput);



    // Replace workout name cell with input

    const currentName = workout.name || workout.type;

    const nameInput = document.createElement('input');

    nameInput.type = 'text';

    nameInput.className = 'inline-edit-input';

    nameInput.value = currentName;

    nameInput.style.width = 'auto';

    nameInput.style.minWidth = '120px';

    workoutCell.innerHTML = '';

    workoutCell.appendChild(nameInput);



    // Change Edit button to Save

    editBtn.innerHTML = '<i class="fas fa-save"></i> Save';

    editBtn.className = 'btn btn-sm btn-success';

    editBtn.onclick = function(e) {

        e.stopPropagation();

        saveRowEdit(row);

    };



    // Add Cancel button

    const cancelBtn = document.createElement('button');

    cancelBtn.className = 'btn btn-sm btn-secondary';

    cancelBtn.style.padding = '4px 10px';

    cancelBtn.innerHTML = '<i class="fas fa-times"></i> Cancel';

    cancelBtn.onclick = function(e) {

        e.stopPropagation();

        cancelRowEdit(row);

    };

    // Insert Cancel button next to Save

    editBtn.parentNode.insertBefore(cancelBtn, editBtn.nextSibling);

}



function saveRowEdit(row) {

    const workoutId = row.dataset.workoutId;

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout) return;



    const dateInput = row.querySelector('.date-col input');

    const nameInput = row.querySelector('.workout-col input');

    if (!dateInput || !nameInput) return;



    const newDate = dateInput.value;

    const newName = nameInput.value.trim();



    if (newDate) {

        const dateObj = new Date(newDate);

        if (!isNaN(dateObj)) {

            workout.date = dateObj.toISOString();

        }

    }

    if (newName) {

        workout.name = newName;

    } else {

        workout.name = workout.type || 'Untitled';

    }



    saveToLocalStorage();

    updateWorkoutHistory(); // reâ€‘render to reset the row

    showNotification('Workout updated', 'success', 'inline_edit');

}



function cancelRowEdit(row) {

    // Simply reâ€‘render the history to discard changes

    updateWorkoutHistory();

}

    // Initialize Tippy.js tooltips

    function initTooltips() {

        tippy('.muscle-tag', {

            content: (ref) => {

                const muscleName = ref.getAttribute('data-muscle') || ref.textContent.trim();

                const muscle = getAllMuscleGroups().find(m => m.name === muscleName || m.display === muscleName);

                if (muscle) {

                    return `${muscle.display}<br>â±ï¸ Rest: ${muscle.restDays} days${muscle.agingRisk ? `<br>âš ï¸ Risk: ${muscle.agingRisk}` : ''}`;

                }

                return `Muscle: ${muscleName}`;

            },

            allowHTML: true,

            theme: 'light-border',

            placement: 'top'

        });

        

        tippy('.rpe-slider-container .rpe-checkbox label', {

            content: (ref) => {

                const rpe = ref.textContent;

                const desc = {10:"No reps left",9:"1 rep left",8:"2 reps left",7:"3 reps left",6:"4 reps left",5:"Moderate",4:"Easy",3:"Very easy",2:"Very easy",1:"Extremely easy"};

                return `RPE ${rpe} â€“ ${desc[rpe] || ''}`;

            },

            placement: 'top',

            theme: 'light'

        });

        

        tippy('.info-tooltip', {

            content: (ref) => ref.querySelector('.tooltip-text')?.textContent || 'Info',

            allowHTML: true,

            placement: 'right'

        });

    }

    

    // Run after DOM updates

    const origRender = renderLibrary;

    renderLibrary = function() { origRender(); setTimeout(initTooltips, 50); };

    const origUpdate = updateTodaysWorkout;

    updateTodaysWorkout = function() { origUpdate(); setTimeout(initTooltips, 50); };

    

    initTooltips();

    // QR Code sharing functions

    let currentQR = null;

    

    function showWorkoutQR() {

        if (!currentWorkout || !currentWorkout.exercises || currentWorkout.exercises.length === 0) {

            showNotification('No workout to share', 'warning');

            return;

        }

        

        // Get current page URL (without any hash or params)

        const currentUrl = window.location.href.split('#')[0].split('?')[0];

        

        const container = document.getElementById('qrCodeContainer');

        container.innerHTML = '';

        

        // Generate QR code with the URL only

        currentQR = new QRCode(container, {

            text: currentUrl,

            width: 200,

            height: 200,

            colorDark: "#2563eb",

            colorLight: "#ffffff",

            correctLevel: QRCode.CorrectLevel.H

        });

        

        // Also generate a readable workout summary text for copying

        const workoutText = generateWorkoutShareText();

        const previewContainer = document.getElementById('shareTextPreview');

        if (previewContainer) {

            previewContainer.innerHTML = `

                <div style="margin-top: 15px; padding: 12px; background: var(--gray-100); border-radius: 12px; text-align: left; font-size: 0.85rem; color: var(--gray-700); max-height: 150px; overflow-y: auto;">

                    <strong>ðŸ“‹ What will be copied:</strong><br>

                    ${workoutText.replace(/\n/g, '<br>')}

                </div>

            `;

        }

        

        showModal('qrModal');

    }

    function generateWorkoutShareText() {

        if (!currentWorkout || !currentWorkout.exercises) return 'No active workout.';

        

        const date = new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

        let text = `ðŸ‹ï¸ WORKOUT: ${currentWorkout.name || 'Today\'s Workout'}\n`;

        text += `ðŸ“… Date: ${date}\n`;

        text += `ðŸ”— Link: ${window.location.href.split('#')[0].split('?')[0]}\n\n`;

        text += `ðŸ“‹ EXERCISES:\n`;

        

        currentWorkout.exercises.forEach((ex, idx) => {

            const muscleGroups = ex.muscleGroup ? ex.muscleGroup.map(m => getMuscleDisplayName(m)).join(', ') : 'Various';

            text += `${idx+1}. ${ex.name} â€“ ${muscleGroups}\n`;

        });

        

        text += `\nðŸ’ª Generated by Pro Fitness App`;

        return text;

    }    

function copyWorkoutLink() {

    if (!currentWorkout || !currentWorkout.exercises || currentWorkout.exercises.length === 0) {

        showNotification('No workout to share', 'warning');

        return;

    }



    // ---------- 1. COLLECT ALL USER SIGNALS (expanded) ----------

    const user = workoutData.user;

    const exp = user.experience || 'intermediate';

    const goal = user.goal || 'balanced';

    const age = user.birthDate ? (() => {

        const a = new Date().getFullYear() - new Date(user.birthDate).getFullYear();

        return a < 30 ? 'young' : (a < 50 ? 'mid' : 'senior');

    })() : null;

    const gender = user.gender && user.gender !== 'other' ? user.gender : null;

    const streak = calculateStreak();

    const totalWorkouts = workoutData.workouts.length;

    const isDeload = !!currentWorkout.isDeload;

    const isTaper = !!currentWorkout.isTaper;

    const compDate = user.competition?.date ? new Date(user.competition.date) : null;

    const daysToComp = compDate ? Math.ceil((compDate - new Date()) / (1000 * 60 * 60 * 24)) : null;



    // Exercise count and variety

    const exerciseCount = currentWorkout.exercises.length;

    const uniqueMuscles = new Set();

    currentWorkout.exercises.forEach(ex => (ex.muscleGroup || []).forEach(m => uniqueMuscles.add(m)));

    const muscleVariety = uniqueMuscles.size;



    // Recent trend (improving/plateau/declining) â€“ more accurate

    let trend = 'unknown';

    let trendStrength = 0;

    const keyLifts = ['squat', 'bench_press', 'deadlift', 'overhead_press', 'barbell_row'];

    for (const lift of keyLifts) {

        const rec = workoutData.exercises[lift];

        if (rec && rec.history && rec.history.length >= 3) {

            const lastThree = rec.history.slice(-3).filter(e => !e.skipped && e.weight && e.reps);

            if (lastThree.length >= 3) {

                const est1RM = (w, reps) => w * (1 + Math.max(...reps) / 30);

                const vals = lastThree.map(e => est1RM(e.weight, e.reps));

                const slope = (vals[2] - vals[0]) / vals[0];

                trendStrength = slope;

                if (slope > 0.03) trend = 'improving';

                else if (slope < -0.03) trend = 'declining';

                else trend = 'plateau';

                break;

            }

        }

    }



    // Average RPE last 7 days

    let avgRPE = 5;

    const recentWorkouts = workoutData.workouts.slice(-7);

    let rpeSum = 0, rpeCount = 0;

    for (const w of recentWorkouts) {

        for (const ex of w.exercises || []) {

            if (ex.actual && ex.actual.rpe) {

                rpeSum += ex.actual.rpe;

                rpeCount++;

            }

        }

    }

    if (rpeCount) avgRPE = rpeSum / rpeCount;

    const rpeLevel = avgRPE < 6 ? 'low' : (avgRPE > 8 ? 'high' : 'medium');



    // Confidence score (0-100) â€“ more factors

    let confidence = 50;

    if (exp === 'advanced') confidence += 20;

    else if (exp === 'beginner') confidence -= 10;

    if (trend === 'improving') confidence += 15;

    else if (trend === 'declining') confidence -= 15;

    if (goal === 'strength') confidence += 10;

    if (streak > 14) confidence += 10;

    if (totalWorkouts > 100) confidence += 5;

    if (muscleVariety > 3) confidence += 5;

    confidence = Math.min(100, Math.max(0, confidence));



    // Competitiveness (0-100)

    let competitive = 50;

    if (exp === 'advanced') competitive += 20;

    if (goal === 'strength') competitive += 20;

    if (trend === 'improving') competitive += 10;

    if (daysToComp && daysToComp <= 14) competitive += 20;

    if (exerciseCount > 5) competitive += 5;

    competitive = Math.min(100, Math.max(0, competitive));



    // Self-talk style

    let style = 'encouraging';

    if (confidence > 70 && competitive > 70) style = 'competitive';

    else if (confidence < 40) style = 'humble';

    else if (exp === 'advanced' && goal === 'hypertrophy') style = 'analytical';

    else if (avgRPE > 8 && !isDeload) style = 'sarcastic';

    else style = 'encouraging';



    // ---------- 2. PAST PERFORMANCE (KEY EXERCISE) â€“ enhanced ----------

    let keyEx = null, lastEntry = null, todayWeight = null;

    for (const ex of currentWorkout.exercises) {

        const rec = workoutData.exercises[ex.id];

        if (rec && rec.history && rec.history.length) {

            const entry = [...rec.history].reverse().find(h => !h.skipped && h.weight && h.reps && h.reps.length);

            if (entry) {

                keyEx = ex;

                lastEntry = entry;

                todayWeight = ex.prescribed?.weight;

                break;

            }

        }

    }

    if (!keyEx && currentWorkout.exercises.length) {

        keyEx = currentWorkout.exercises[0];

        const rec = workoutData.exercises[keyEx.id];

        if (rec && rec.history && rec.history.length) {

            const entry = [...rec.history].reverse().find(h => !h.skipped && h.weight && h.reps && h.reps.length);

            if (entry) { lastEntry = entry; todayWeight = keyEx.prescribed?.weight; }

        }

    }



    let historyMsg = '';

    let diff = 0, diffPercent = 0;

    let oldWeight = 0, oldReps = 0;

    if (lastEntry && todayWeight && todayWeight > 0) {

        oldWeight = lastEntry.weight;

        oldReps = Math.max(...lastEntry.reps);

        diff = todayWeight - oldWeight;

        diffPercent = (diff / oldWeight) * 100;

        if (Math.abs(diff) > 0.1) {

            if (diff > 0) {

                historyMsg = `Last time I did ${keyEx.name}, I used ${oldWeight} lbs for ${oldReps} reps. Today I'm aiming for ${todayWeight} lbs â€“ that's ${diff.toFixed(1)} lbs heavier. `;

            } else {

                historyMsg = `Last time I did ${keyEx.name}, I used ${oldWeight} lbs for ${oldReps} reps. Today I'm using ${todayWeight} lbs (${Math.abs(diff).toFixed(1)} lbs lighter). `;

                if (isDeload) historyMsg += `This is a planned lighter day â€“ perfect for recovery. `;

                else historyMsg += `Adjusting weight is smart. I'll focus on perfect form. `;

            }

        } else {

            historyMsg = `I've done ${keyEx.name} before (${oldWeight} lbs for ${oldReps} reps). Same weight today â€“ consistency builds strength. `;

        }

    } else if (keyEx) {

        historyMsg = `First time logging ${keyEx.name}. `;

    }



    // ---------- 3. MASSIVE PHRASE POOLS (hundreds of options) ----------

    const openers = {

        encouraging: [

            `I'm ready to give my best today.`, `Every workout is a step forward.`, `I'll show up and do the work.`,

            `Progress, not perfection.`, `I trust the process.`, `One rep at a time, I'm building something great.`,

            `Today I choose growth over comfort.`, `I'm grateful for the ability to move my body.`,

            `No matter what, I'll finish this workout.`, `I've got nothing to prove, but everything to gain.`,

            `Let's make today count.`, `I'm here to improve, not to impress.`, `Small consistent efforts lead to big results.`,

            `I'm proud of myself for showing up.`, `This is my time.`, `I'll give it my honest effort.`

        ],

        competitive: [

            `Time to prove something to myself.`, `I'm chasing a PR today. No excuses.`, `Let's see what I'm made of.`,

            `The weight doesn't lift itself.`, `I've trained for this moment.`, `PR or bust. Let's go.`,

            `I'm coming for that next level.`, `Heavy weights, happy life.`, `Let's dominate this session.`,

            `I will not be outworked today.`, `I'm here to conquer.`, `Let's raise the bar.`,

            `No one else will do this for me.`, `I'm built for this.`, `Let's get uncomfortable.`

        ],

        humble: [

            `Just grateful to be moving.`, `Another day to get a little better.`, `I'll do what I can today.`,

            `Small steps, big journey.`, `Thankful for my health.`, `I'm not the strongest, but I'm consistent.`,

            `Every rep is a victory.`, `I'll leave my ego at the door.`, `Just happy to be here.`,

            `I'm doing this for future me.`, `I'm not competing with anyone but myself.`, `This is my therapy.`,

            `I'll take it slow and steady.`, `I'm just glad to move.`, `One day at a time.`

        ],

        analytical: [

            `I'll focus on mindâ€‘muscle connection today.`, `Form first, weight second.`, `Let's see how my body responds.`,

            `Controlled reps, full range of motion.`, `Quality over quantity.`, `Time to analyse my technique.`,

            `I'll pay attention to every microâ€‘adjustment.`, `Let's break down each movement.`, `Precision over power today.`,

            `I'll log every detail for later review.`, `I'll track my bar speed.`, `Let's see where I can improve.`,

            `I'm treating this as a data collection session.`, `Form breakdown starts here.`, `I'll film my last set.`

        ],

        sarcastic: [

            `Oh good, another chance to suffer.`, `My body is ready... I think.`, `Let's get this over with.`,

            `Future me will hate present me.`, `I voluntarily chose to do this.`, `Here we go again.`,

            `My muscles are already complaining.`, `I could be sleeping, but no.`, `Let the pain begin.`,

            `I'll regret this tomorrow, but today we lift.`, `This is my idea of fun, apparently.`, `My legs will file a protest later.`,

            `I'm only doing this for the endorphins.`, `Someone remind me why I like this.`, `Let's see how much I can hurt myself.`

        ]

    };



    const perfFollowPos = [

        `That's a solid jump. I've earned this.`, `I'm getting stronger â€“ love to see it.`,

        `Let's see if I can handle the increase.`, `No fear. I've done the prep work.`, `Time to push past my limits.`,

        `This is what I've been working towards.`, `Bring on the challenge.`, `I've been waiting for this weight.`,

        `Let's see if my strength has truly improved.`, `I'm excited to test myself.`, `I've been dreaming of this weight.`,

        `The hard work is paying off.`, `I'm ready for the next chapter.`, `Let's see if I can surprise myself.`,

        `I've earned the right to attempt this.`

    ];

    const perfFollowNeg = [

        `Lighter weight means I can focus on form.`, `Nothing wrong with backing off. Smart training.`,

        `I'll own every rep with perfect technique.`, `Deload or not, I'm still showing up.`, `Recovery is part of the process.`,

        `I'll use this as a technique day.`, `Every rep will be crisp and controlled.`, `I'm prioritising longevity over ego.`,

        `This is still progress.`, `I'll come back stronger next week.`, `Today is about quality, not quantity.`,

        `I'll work on my weak points.`, `This is strategic, not weak.`, `I'm building a foundation.`,

        `I'll nail every rep with precision.`

    ];

    const perfFollowNeu = [

        `Consistency over intensity today.`, `Same weight, better form.`, `I'll match last time and then some.`,

        `Steady progress wins the race.`, `No change in weight, but I'll add a rep.`, `I'll aim for cleaner reps.`,

        `Let's see if I can beat last time's RPE.`, `Every session is a chance to refine.`, `I'll focus on squeezing the muscle.`,

        `Same load, new focus.`, `I'll increase time under tension.`, `I'll pause at the bottom.`,

        `I'll control the negative more strictly.`, `Let's see if I can get more volume.`, `I'll add a drop set on the last exercise.`

    ];



    // Muscle group names and sentences

    const muscleGroupNames = [...new Set(currentWorkout.exercises.flatMap(ex => ex.muscleGroup || []))]

        .sort().map(m => { const mu = getAllMuscleGroups().find(mg => mg.name === m); return mu ? mu.display : m; }).join(', ');

    const muscleSentences = [

        `My ${muscleGroupNames.split(',').slice(0,2).join(',')} will be feeling this tomorrow.`,

        `I'll really focus on squeezing the ${muscleGroupNames.split(',')[0]} during each rep.`,

        `The main targets today: ${muscleGroupNames}.`,

        `I can already feel the burn in my ${muscleGroupNames.split(',')[0]}.`,

        `These moves are perfect for building ${muscleGroupNames}.`,

        `I'm going to exhaust my ${muscleGroupNames.split(',')[0]} first, then move on.`,

        `Controlled negatives will really hit the ${muscleGroupNames.split(',')[0]}.`,

        `I'll pause at the stretch for ${muscleGroupNames.split(',')[0]}.`,

        `My ${muscleGroupNames} will be sore, but it's a good sore.`,

        `I'm focusing on the mindâ€‘muscle connection with ${muscleGroupNames.split(',')[0]}.`,

        `I'll try to feel every fiber of my ${muscleGroupNames.split(',')[0]}.`,

        `This workout is a love letter to my ${muscleGroupNames}.`,

        `I'm going to make my ${muscleGroupNames.split(',')[0]} scream.`,

        `I'll visualise my ${muscleGroupNames.split(',')[0]} growing.`,

        `I'll use lighter weight to really feel my ${muscleGroupNames.split(',')[0]}.`

    ];



    const estimatedTime = currentWorkout.exercises.length * 5 + 10;

    let difficulty = 'Moderate-High';

    if (isDeload) difficulty = 'Light / Deload';

    else if (isTaper) difficulty = 'Taper / Peaking';



    const timeSentences = [

        `This should take about ${estimatedTime} minutes at ${difficulty} intensity.`,

        `I've set aside ${estimatedTime} min for this ${difficulty} session.`,

        `${difficulty} effort for ${estimatedTime} minutes â€“ let's go.`,

        `I'll pace myself through ${estimatedTime} minutes of ${difficulty} work.`,

        `Roughly ${estimatedTime} min of ${difficulty}. I can do this.`,

        `I'll keep rest periods tight, aiming for ${estimatedTime} minutes total.`,

        `${difficulty} intensity means I'll be breathing hard.`,

        `This ${difficulty} session will take about ${estimatedTime} minutes.`,

        `I'll push hard but steady for ${estimatedTime} minutes.`,

        `Time to get after it â€“ ${estimatedTime} minutes of ${difficulty} work.`,

        `I'll try to finish under ${estimatedTime} minutes.`,

        `${estimatedTime} minutes of focused work. Let's do it.`,

        `I've cleared my schedule for the next ${estimatedTime} minutes.`,

        `Let's see if I can beat my previous time for this workout.`,

        `I'll use a stopwatch to keep myself honest.`

    ];



    const closers = {

        encouraging: [

            `I've got this. One rep at a time.`, `Let's do this. No regrets.`, `I'll be proud when I'm done.`,

            `Every rep counts.`, `Let's earn that postâ€‘workout meal.`, `I'm building a better me.`,

            `I'll finish strong.`, `One step closer to my goal.`, `I'm capable of more than I think.`,

            `Let's make today count.`, `I'll be one workout stronger.`, `I'm excited for the results.`,

            `This is how champions are made.`, `I'll never regret a workout.`, `Let's go get it.`

        ],

        competitive: [

            `PR or bust. Let's go.`, `The iron never lies. Time to work.`, `Let's see what I can do.`,

            `I'm coming for that next level.`, `Heavy weights, happy life.`, `I will not be outworked.`,

            `Let's crush this.`, `No mercy, just reps.`, `I'm here to dominate.`, `Let's leave it all in the gym.`,

            `I'll earn every pound.`, `I'm chasing greatness.`, `Let's see who's stronger than yesterday.`,

            `I'll make this session my bitch.`, `Pain is temporary, PRs are forever.`

        ],

        humble: [

            `Just happy to be moving.`, `I'll do my best and that's enough.`, `Another day stronger.`,

            `Thankful for my health.`, `Small wins add up.`, `I'm grateful for this body.`, `Every rep is a gift.`,

            `I'll take it one set at a time.`, `I'm proud of myself for showing up.`, `Let's be kind to ourselves.`,

            `I'm doing this for my future self.`, `I'll celebrate finishing.`, `I'm enough as I am.`,

            `This is selfâ€‘care.`, `I'm building a sustainable habit.`

        ],

        analytical: [

            `Mindâ€‘muscle connection activated.`, `Form check on every rep.`, `Let's make each set better than the last.`,

            `Control the negative.`, `Quality over quantity.`, `I'll log my RPE after every set.`,

            `I'm focusing on the details today.`, `Precision will pay off.`, `I'll analyse my bar path.`,

            `Every rep is a data point.`, `I'll video my top set.`, `Let's see where I can improve.`,

            `I'll track my fatigue levels.`, `I'm treating this as an experiment.`, `Let's optimise my technique.`

        ],

        sarcastic: [

            `My future self will thank me (maybe).`, `RIP my muscles.`, `I'll be walking funny tomorrow.`,

            `Wish me luck. I'll need it.`, `Let the pain begin.`, `I'll regret this, but it'll be worth it.`,

            `My body will file a complaint later.`, `Here's to soreness.`, `I'm already looking forward to the couch.`,

            `Let's get this suffering over with.`, `I'll earn my rest day.`, `Pain is just weakness leaving the body... or so they say.`,

            `I'll be cursing my past self tomorrow.`, `Let's see if I can still walk after this.`, `I'm doing this for the endorphins.`

        ]

    };



    const lotteryMessages = [

        `May I prosper in all areas of life, not just the gym.`,

        `The universe has my back today. Let's see what happens.`,

        `I'm training for the zombie apocalypse. You never know.`,

        `This workout is dedicated to my future self.`,

        `I hope the gains fairy visits me tonight.`,

        `Random thought: what if I could fly? Anyway, back to lifting.`,

        `I'm one workout closer to looking like my avatar.`,

        `May my fridge be full of protein and my will be strong.`,

        `I'm not just building muscle; I'm building character.`,

        `Let's see if I can surprise myself.`,

        `I wonder if my dog is proud of me. Probably.`,

        `If I lift heavy enough, maybe gravity will stop working.`,

        `Today I'll pretend I'm in a movie montage.`,

        `I'm training to be the hero of my own story.`,

        `May the force be with me.`,

        `I'm doing this so I can eat an extra slice of pizza.`,

        `Let's manifest some gains.`,

        `I'm going to channel my inner superhero.`,

        `Maybe I'll finally be able to open that stubborn jar.`,

        `I'm doing this for my future grandchildren's arm wrestling matches.`

    ];



    // Rare offâ€‘topic messages (even rarer than lottery, ~1%)

    const offTopicMessages = [

        `I also need to remember to buy milk.`,

        `Did I turn off the oven? Probably.`,

        `I wonder what my cat is doing right now.`,

        `The meaning of life might be hidden in the last rep.`,

        `I should probably call my mom later.`,

        `Is it weird that I'm thinking about tacos?`,

        `I hope there's a good podcast waiting for me after this.`,

        `Maybe I'll learn to play the guitar next.`,

        `Life is short. Lift heavy. Eat pizza.`,

        `I'm doing this so I can open all the pickle jars.`

    ];



    // ---------- 4. ASSEMBLE THE WITTY MESSAGE ----------

    const getRandom = (arr) => arr[Math.floor(Math.random() * arr.length)];



    let opener = getRandom(openers[style] || openers.encouraging);

    let perfFollow = '';

    if (historyMsg) {

        if (diff > 0) perfFollow = getRandom(perfFollowPos);

        else if (diff < 0) perfFollow = getRandom(perfFollowNeg);

        else perfFollow = getRandom(perfFollowNeu);

    }

    let muscleSentence = getRandom(muscleSentences);

    let timeSentence = getRandom(timeSentences);

    let closer = getRandom(closers[style] || closers.encouraging);



    const isLottery = Math.random() < 0.05;

    let lotteryMsg = isLottery ? getRandom(lotteryMessages) + ' ' : '';



    const isOffTopic = Math.random() < 0.01;

    let offTopicMsg = isOffTopic ? getRandom(offTopicMessages) + ' ' : '';



    let streakMsg = '';

    if (streak >= 100) streakMsg = `ðŸ’¯ ${streak} days in a row! That's legendary. `;

    else if (streak >= 30) streakMsg = `${streak} days in a row â€“ that's dedication. `;

    else if (streak >= 14) streakMsg = `A ${streak}â€‘day streak! Consistency is paying off. `;

    else if (streak >= 7) streakMsg = `One full week of training. I'm building a habit. `;

    else if (streak >= 3) streakMsg = `${streak} days straight â€“ I'm on a roll. `;

    else if (streak === 1 && totalWorkouts === 1) streakMsg = `My very first workout logged. Let's make it a good one. `;



    let trendMsg = '';

    if (trend === 'improving') trendMsg = `I've been getting stronger lately. Let's keep that momentum. `;

    else if (trend === 'declining') trendMsg = `Progress isn't linear. I'll focus on form and come back stronger next week. `;

    else if (trend === 'plateau') trendMsg = `I've hit a plateau. Time to switch something up â€“ or just push through. `;



    let deloadMsg = '';

    if (isDeload) deloadMsg = `This is a lighter week â€“ I'll focus on form and recovery. I'll come back stronger. `;

    else if (isTaper) {

        deloadMsg = `Sharpening the blade. Keeping intensity high, volume low. I'm peaking at the right time. `;

        if (daysToComp && daysToComp <= 14) deloadMsg += `Only ${daysToComp} days until my meet. I trust my training. `;

    }



    let efficacyMsg = '';

    if (diffPercent > 5 && (exp === 'beginner' || exp === 'intermediate')) efficacyMsg = `Do I think I can do it? I think I can. `;

    else if (diffPercent > 10 && exp === 'advanced') efficacyMsg = `That's a serious jump. I've earned the right to attempt this. `;



    let confidenceMsg = '';

    if (confidence > 80 && competitive > 70 && !isDeload) confidenceMsg = `I feel unstoppable today. `;

    else if (confidence < 30) confidenceMsg = `I'm a bit tired, but I'll still show up. `;



    let ageMsg = '';

    if (age === 'senior' && goal === 'longevity') ageMsg = `Staying active keeps me young at heart. `;

    else if (age === 'young' && goal === 'strength') ageMsg = `I'm building a foundation that will last decades. `;



    let genderMsg = '';

    if (gender === 'female' && goal === 'strength') genderMsg = `Strong is beautiful. `;

    else if (gender === 'male' && goal === 'hypertrophy') genderMsg = `Let's build something impressive. `;



    let fullWitty = '';

    fullWitty += lotteryMsg + offTopicMsg + streakMsg + trendMsg + deloadMsg + confidenceMsg + ageMsg + genderMsg;

    fullWitty += opener + ' ';

    if (historyMsg) fullWitty += historyMsg + perfFollow + ' ';

    fullWitty += muscleSentence + ' ';

    fullWitty += timeSentence + ' ';

    fullWitty += efficacyMsg + closer;



    // Avoid repetition (store last 15 messages)

    const storageKey = 'lastShareMessages';

    let lastMessages = [];

    try {

        const stored = localStorage.getItem(storageKey);

        if (stored) lastMessages = JSON.parse(stored);

        if (lastMessages.includes(fullWitty)) {

            // Reroll closer and opener

            closer = getRandom(closers[style] || closers.encouraging);

            opener = getRandom(openers[style] || openers.encouraging);

            fullWitty = fullWitty.replace(/[^.!?]*$/, closer);

            fullWitty = fullWitty.replace(/^[^.!?]*\. /, opener + ' ');

        }

        lastMessages.push(fullWitty);

        if (lastMessages.length > 15) lastMessages.shift();

        localStorage.setItem(storageKey, JSON.stringify(lastMessages));

    } catch(e) {}



    // ---------- 5. FINAL OUTPUT (SAME LAYOUT) ----------

    const pageUrl = window.location.href.split('#')[0].split('?')[0];

    const dateStr = new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

    const workoutName = currentWorkout.name || 'Today\'s Workout';

    const userName = workoutData.user.name || 'Anonymous lifter';

    const muscleSet = new Set();

    currentWorkout.exercises.forEach(ex => (ex.muscleGroup || []).forEach(m => muscleSet.add(m)));

    const muscleGroupsList = [...muscleSet].sort().map(m => {

        const muscle = getAllMuscleGroups().find(mg => mg.name === m);

        return muscle ? muscle.display : m;

    }).join(', ');

    const exercisesList = currentWorkout.exercises.map((ex, idx) => `   ${idx + 1}. ${ex.name}`).join('\n');



    const fullMessage = `${pageUrl}\n\nâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€\nWORKOUT: ${workoutName}\nDATE: ${dateStr}\n\nEXERCISES:\n${exercisesList}\n\nMUSCLES: ${muscleGroupsList}\nTIME: ${estimatedTime} min Â· DIFFICULTY: ${difficulty}\n\nâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€\nâ€œ${fullWitty}â€\nâ€” ${userName}`;



    navigator.clipboard.writeText(fullMessage).then(() => {

        showNotification('âœ… Workout shared! Deeply personalized message copied.', 'success');

    }).catch(() => {

        const textarea = document.createElement('textarea');

        textarea.value = fullMessage;

        textarea.style.position = 'fixed';

        textarea.style.opacity = '0';

        document.body.appendChild(textarea);

        textarea.select();

        document.execCommand('copy');

        document.body.removeChild(textarea);

        showNotification('âœ… Workout shared (fallback).', 'success');

    });

}

    // Initialize smooth scroll with offset for fixed navbar

    if (typeof SmoothScroll !== 'undefined') {

        new SmoothScroll('a[href*="#"]', {

            speed: 800,

            speedAsDuration: true,

            offset: function(anchor, toggle) {

                // Return offset based on device type

                return document.body.classList.contains('device-computer') ? 20 : 80;

            },

            easing: 'easeInOutCubic',

            updateURL: false,

            popstate: false

        });

    }

    // Manual override if loader never disappears

    function forceShowDashboard() {

        const loader = document.getElementById('loading');

        if (loader) {

            loader.classList.remove('active');

            loader.style.opacity = '';

        }

        document.body.classList.remove('loading');

        showSection('dashboard');

        showNotification("Manual override â€“ dashboard loaded", "info");

    }

    

    // Show manual override link after 5 seconds

    setTimeout(() => {

        const link = document.getElementById('manualOverrideLink');

        const loader = document.getElementById('loading');

        if (link && loader && loader.classList.contains('active')) {

            link.style.display = 'block';

        }

    }, 5000);

    // ========== ULTIMATE BACK BUTTON HANDLER ==========

    window.addEventListener('popstate', function(event) {

        const state = event.state;

        let currentStack = navigationStack;

        

        // Use stack from event state if available (more reliable)

        if (state && state.stack && Array.isArray(state.stack)) {

            currentStack = state.stack;

        }

        

        // If there is a previous section (stack length > 1)

        if (currentStack.length > 1) {

            const newStack = [...currentStack];

            newStack.pop(); // remove current section

            const previousSection = newStack[newStack.length - 1];

            if (previousSection && document.getElementById(`${previousSection}-section`)) {

                navigationStack = newStack;

                localStorage.setItem('workout_navigation_stack', JSON.stringify(navigationStack));

                showSection(previousSection, true);

                // Reâ€‘arm the guard after navigation

                setTimeout(() => {

                    try { window.history.pushState({ stack: navigationStack, guard: true }, '', `#${previousSection}`); } catch(e) {}

                }, 10);

                return;

            }

        }

    });

// ---- END extracted from index (24).html L6933-47485 (main app script (40,553 lines â€” version check, data structures, all feature functions: dashboard, workout, library, progress, recovery, history, settings; flat global namespace, no internal module boundaries)) ----

