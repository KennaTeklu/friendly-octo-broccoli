// ============================================================
// reference/scripts/app/17-longevity.js
// Extracted from reference/scripts/app.js
// Original lines: 74250-75879  (1630 lines)
// Section: Longevity score components, aging risks
// ============================================================

    // ----- 1. Basic user info -----

    const birthDate = workoutData.user.birthDate;

    let age = null;

    if (birthDate) {

        const birth = new Date(birthDate);

        const today = new Date();

        age = today.getFullYear() - birth.getFullYear();

        const m = today.getMonth() - birth.getMonth();

        if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;

    }



    // ----- 2. Core scores (unchanged from original helpers) -----

    const baseScores = {

        gripStrength: calculateGripStrengthScore(),

        balance: calculateBalanceScore(),

        jointMobility: calculateJointMobilityScore(),

        posture: calculatePostureScore(),

        muscleBalance: calculateMuscleBalanceScore(),

        consistency: calculateConsistencyScore()

    };



    // ----- 3. Trend score (improvement over last 3 months) -----

    function calculateTrendScore() {

        const threeMonthsAgo = new Date();

        threeMonthsAgo.setMonth(threeMonthsAgo.getMonth() - 3);



        // Track improvement in key compound lifts (by exercise ID)

        const keyExercises = ['squat', 'deadlift', 'bench_press', 'overhead_press'];

        let totalImprovement = 0;

        let count = 0;



        keyExercises.forEach(exId => {

            const exData = workoutData.exercises[exId];

            if (!exData || !exData.history || exData.history.length < 2) return;



            // Filter history to last 3 months

            const recent = exData.history.filter(entry => new Date(entry.date) >= threeMonthsAgo);

            if (recent.length < 2) return;



            // Estimate 1RM for oldest and newest

            const oldest = recent[0];

            const newest = recent[recent.length - 1];

            const oldest1RM = estimate1RM(oldest.weight, Math.max(...(oldest.reps || [1])));

            const newest1RM = estimate1RM(newest.weight, Math.max(...(newest.reps || [1])));



            if (oldest1RM > 0) {

                const improvement = (newest1RM - oldest1RM) / oldest1RM; // fractional gain

                totalImprovement += improvement;

                count++;

            }

        });



        if (count === 0) return 50; // neutral score if no data

        const avgImprovement = totalImprovement / count;

        // Convert to 0â€‘100 scale: 0% improvement â†’ 50, +10% improvement â†’ 75, -10% â†’ 25

        return Math.min(100, Math.max(0, 50 + avgImprovement * 250));

    }



    // ----- 4. Variety score (how many different longevity muscles trained recently) -----

    function calculateVarietyScore() {

        const thirtyDaysAgo = new Date();

        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);



        const longevityMuscles = muscleDatabase.longevity.map(m => m.name);

        const trainedLongevity = new Set();



        workoutData.workouts.forEach(workout => {

            const workoutDate = new Date(workout.date);

            if (workoutDate < thirtyDaysAgo) return;

            workout.exercises?.forEach(ex => {

                if (ex.actual && !ex.skipped && ex.muscleGroup) {

                    ex.muscleGroup.forEach(mg => {

                        if (longevityMuscles.includes(mg)) {

                            trainedLongevity.add(mg);

                        }

                    });

                }

            });

        });



        const percentage = (trainedLongevity.size / longevityMuscles.length) * 100;

        return Math.min(100, Math.round(percentage * 1.5)); // scale so training 67% gives 100

    }



    const trendScore = calculateTrendScore();

    const varietyScore = calculateVarietyScore();



    // ----- 5. Dynamic weights based on age -----

    let weightBase = {

        gripStrength: 0.15,

        balance: 0.15,

        jointMobility: 0.25,

        posture: 0.20,

        muscleBalance: 0.15,

        consistency: 0.10,

        trend: 0.0,

        variety: 0.0

    };



    if (age !== null) {

        if (age < 30) {

            // Youth: focus on strength and muscle balance

            weightBase.gripStrength = 0.20;

            weightBase.balance = 0.10;

            weightBase.jointMobility = 0.15;

            weightBase.muscleBalance = 0.25;

            weightBase.consistency = 0.10;

            weightBase.trend = 0.10;

            weightBase.variety = 0.10;

        } else if (age < 50) {

            // Middle age: joint health becomes important

            weightBase.gripStrength = 0.15;

            weightBase.balance = 0.15;

            weightBase.jointMobility = 0.25;

            weightBase.posture = 0.15;

            weightBase.muscleBalance = 0.10;

            weightBase.consistency = 0.10;

            weightBase.trend = 0.05;

            weightBase.variety = 0.05;

        } else {

            // 50+: balance and joint mobility are critical

            weightBase.gripStrength = 0.10;

            weightBase.balance = 0.25;

            weightBase.jointMobility = 0.30;

            weightBase.posture = 0.15;

            weightBase.muscleBalance = 0.05;

            weightBase.consistency = 0.05;

            weightBase.trend = 0.05;

            weightBase.variety = 0.05;

        }

    }



    // Normalize weights to sum to 1.0

    const totalWeight = Object.values(weightBase).reduce((a, b) => a + b, 0);

    const weights = {};

    Object.keys(weightBase).forEach(key => {

        weights[key] = weightBase[key] / totalWeight;

    });



    // ----- 6. Combine all scores -----

    const allScores = {

        ...baseScores,

        trend: trendScore,

        variety: varietyScore

    };



    let total = 0;

    Object.keys(weights).forEach(key => {

        if (allScores[key] !== undefined) {

            total += allScores[key] * weights[key];

        }

    });

    const finalScore = Math.round(total);



    // ----- 7. Assess aging risks (using base scores only, as before) -----

    const agingRisks = assessAgingRisks(baseScores);



    // ----- 8. Generate enhanced recommendations -----

    const recommendations = [];



    // Add trendâ€‘based recommendation

    if (trendScore < 40) {

        recommendations.push("Your strength is declining. Consider a more structured progressive overload program.");

    } else if (trendScore > 70) {

        recommendations.push("Great strength gains! Keep progressing.");

    }



    // Add varietyâ€‘based recommendation

    if (varietyScore < 50) {

        recommendations.push("You're neglecting many longevity muscles. Add more jointâ€‘health exercises.");

    }



    // Add existing recommendations from helper

    const baseRecs = generateLongevityRecommendations(baseScores, agingRisks);

    recommendations.push(...baseRecs);



    // Remove duplicates (simple)

    const uniqueRecs = [...new Set(recommendations)];



    return {

        score: finalScore,

        breakdown: {

            ...baseScores,

            trend: Math.round(trendScore),

            variety: Math.round(varietyScore)

        },

        risks: agingRisks,

        recommendations: uniqueRecs

    };

}



function calculateGripStrengthScore() {

    // List of exercise IDs that heavily involve grip strength

    const gripExerciseIds = [

        "deadlift",           // conventional deadlift

        "pull_up",            // pull-up (bodyweight + added weight)

        "farmer_walk",        // farmer's walk (weight per hand)

        "dead_hang",          // dead hang (duration in seconds)

        "wrist_curl",         // wrist curl

        "plate_pinch",        // plate pinch

        "grippers",           // grippers (closing force, e.g., Captains of Crush rating)

        "barbell_shrug",      // heavy shrugs also test grip

        "dumbbell_shrug",

        "rack_pull"           // rack pull (similar to deadlift)

    ];



    const userWeight = workoutData.user.weight || 150;

    let bestScore = 0;



    gripExerciseIds.forEach(exId => {

        const exData = workoutData.exercises[exId];

        if (!exData) return;



        // Use best 1RM if available, otherwise fallback to bestWeight

        let gripLoad = 0;

        let ratio = 0;



        // For dead hang, interpret bestWeight as seconds (or we could use a separate field)

        if (exId === "dead_hang") {

            const duration = exData.bestWeight || exData.bestDuration || 0;

            if (duration > 0) {

                // Score based on duration: 60s = 50, 120s = 100

                const durationScore = Math.min(100, (duration / 120) * 100);

                if (durationScore > bestScore) bestScore = durationScore;

            }

            return; // skip ratio calculation for duration

        }



        // For weighted exercises, compute best estimated 1RM

        if (exData.bestWeight) {

            // If we have a history, use the best 1RM from any entry

            if (exData.history && exData.history.length > 0) {

                let best1RM = 0;

                exData.history.forEach(entry => {

                    if (entry.weight && entry.reps && entry.reps.length > 0) {

                        const maxReps = Math.max(...entry.reps);

                        const estimated = estimate1RM(entry.weight, maxReps);

                        if (estimated > best1RM) best1RM = estimated;

                    }

                });

                if (best1RM > 0) gripLoad = best1RM;

                else gripLoad = exData.bestWeight; // fallback

            } else {

                gripLoad = exData.bestWeight;

            }



            // Adjust for farmer's walk (weight per hand â†’ total grip load is 2x)

            if (exId === "farmer_walk") {

                gripLoad *= 2;

            }



            // Compute ratio relative to bodyweight

            ratio = gripLoad / userWeight;

        } 

        // For pull-ups, bestWeight is added weight, so total = userWeight + addedWeight

        else if (exId === "pull_up" && exData.bestWeight) {

            const totalWeight = userWeight + exData.bestWeight;

            ratio = totalWeight / userWeight;

        }



        // Score based on ratio

        if (ratio > 0) {

            let score = 0;

            if (ratio >= 2.0) score = 100;

            else if (ratio >= 1.5) score = 90;

            else if (ratio >= 1.2) score = 75;

            else if (ratio >= 1.0) score = 60;

            else if (ratio >= 0.8) score = 40;

            else if (ratio >= 0.5) score = 20;

            else score = 0;

            if (score > bestScore) bestScore = score;

        }

    });



    // If no gripâ€‘specific data, fallback to compound lifts that involve grip (already included)

    // Return neutral 50 if still nothing

    return bestScore > 0 ? bestScore : 50;

}



function calculateBalanceScore() {

    // ----- 1. Define exercise categories -----

    const balanceExerciseIds = new Set([

        "single_leg_stand",

        "balance_board",

        "yoga_tree",

        "single_leg_rdl",

        "single_leg_squat",

        "ankle_stability",

        "pistol_squat",

        "staggered_stance_deadlift"

    ]);



    const unilateralExerciseIds = new Set([

        "bulgarian_split_squat",

        "single_leg_press",

        "lunge",

        "step_up",

        "single_leg_rdl",

        "pistol_squat",

        "single_leg_squat"

    ]);



    const ankleMuscles = ["peroneus_tertius", "tibialis", "foot_intrinsics"];



    // ----- 2. Lookback period (days) -----

    const lookbackDays = 60;

    const cutoffDate = new Date();

    cutoffDate.setDate(cutoffDate.getDate() - lookbackDays);



    // ----- 3. Scan workout history -----

    let balanceSessions = 0;

    let lastBalanceDate = null;

    let unilateralSessions = 0;



    workoutData.workouts.forEach(workout => {

        const workoutDate = new Date(workout.date);

        if (workoutDate < cutoffDate) return; // ignore older workouts



        let hasBalanceExercise = false;

        let hasUnilateralExercise = false;



        workout.exercises?.forEach(ex => {

            if (!ex.actual || ex.skipped) return; // only completed exercises count



            const exId = ex.id;

            if (balanceExerciseIds.has(exId)) {

                hasBalanceExercise = true;

            }

            if (unilateralExerciseIds.has(exId)) {

                hasUnilateralExercise = true;

            }

        });



        if (hasBalanceExercise) {

            balanceSessions++;

            if (!lastBalanceDate || workoutDate > lastBalanceDate) {

                lastBalanceDate = workoutDate;

            }

        }

        if (hasUnilateralExercise) {

            unilateralSessions++;

        }

    });



    // ----- 4. Ankle muscle recency (from muscleLastTrained) -----

    let mostRecentAnkleDate = null;

    ankleMuscles.forEach(muscle => {

        const last = muscleLastTrained[muscle];

        if (last) {

            const lastDate = new Date(last);

            if (!mostRecentAnkleDate || lastDate > mostRecentAnkleDate) {

                mostRecentAnkleDate = lastDate;

            }

        }

    });



    // ----- 5. Compute component scores (each 0â€‘100, then weighted) -----

    const weights = {

        frequency: 0.4,

        recency: 0.2,

        ankle: 0.2,

        unilateral: 0.2

    };



    // Frequency score: aim for at least 8 balance sessions in 60 days

    const targetSessions = 8;

    const freqScore = Math.min(100, (balanceSessions / targetSessions) * 100);



    // Recency score

    let recencyScore = 0;

    if (lastBalanceDate) {

        const daysSince = Math.floor((new Date() - lastBalanceDate) / (1000 * 60 * 60 * 24));

        if (daysSince <= 7) recencyScore = 100;

        else if (daysSince <= 14) recencyScore = 80;

        else if (daysSince <= 30) recencyScore = 60;

        else if (daysSince <= 60) recencyScore = 40;

        else recencyScore = 20;

    } else {

        recencyScore = 0;

    }



    // Ankle muscle score (based on most recent training)

    let ankleScore = 0;

    if (mostRecentAnkleDate) {

        const daysSince = Math.floor((new Date() - mostRecentAnkleDate) / (1000 * 60 * 60 * 24));

        if (daysSince <= 14) ankleScore = 100;

        else if (daysSince <= 30) ankleScore = 80;

        else if (daysSince <= 60) ankleScore = 60;

        else ankleScore = 40;

    } else {

        ankleScore = 20; // never trained â€“ low score

    }



    // Unilateral score: aim for at least 4 unilateral sessions in 60 days

    const targetUnilateral = 4;

    const unilateralScore = Math.min(100, (unilateralSessions / targetUnilateral) * 100);



    // ----- 6. Combine scores -----

    const totalScore = Math.round(

        freqScore * weights.frequency +

        recencyScore * weights.recency +

        ankleScore * weights.ankle +

        unilateralScore * weights.unilateral

    );



    return Math.min(100, Math.max(0, totalScore));

}



function calculateJointMobilityScore() {

    const longevityMuscles = muscleDatabase.longevity;

    const now = new Date();

    const sixtyDaysAgo = new Date(now);

    sixtyDaysAgo.setDate(now.getDate() - 60);



    // Count how many times each longevity muscle has been worked in the last 60 days

    const muscleSessionCount = {};

    longevityMuscles.forEach(m => muscleSessionCount[m.name] = 0);



    workoutData.workouts.forEach(workout => {

        const workoutDate = new Date(workout.date);

        if (workoutDate < sixtyDaysAgo) return; // ignore older workouts

        workout.exercises?.forEach(ex => {

            if (ex.actual && !ex.skipped && ex.muscleGroup) {

                ex.muscleGroup.forEach(mg => {

                    if (muscleSessionCount.hasOwnProperty(mg)) {

                        muscleSessionCount[mg]++;

                    }

                });

            }

        });

    });



    let totalWeightedScore = 0;

    let totalWeight = 0;



    longevityMuscles.forEach(muscle => {

        const name = muscle.name;

        const lastTrained = muscleLastTrained[name];

        const daysSince = lastTrained ? daysSinceTrained(name) : Infinity;



        // ----- Recency component (0â€“1) -----

        let recencyScore;

        if (daysSince >= 60) {

            recencyScore = 0;                     // completely stale

        } else if (daysSince <= 7) {

            recencyScore = 1.0;                    // perfectly fresh

        } else {

            // linear decay from 1 at 7 days to 0 at 60 days

            recencyScore = 1 - (daysSince - 7) / 53;

        }



        // ----- Frequency bonus (0â€“0.2) -----

        const sessions = muscleSessionCount[name] || 0;

        const freqBonus = Math.min(sessions, 4) * 0.05; // up to +0.2 for 4+ sessions



        // Combine, cap at 1.0

        let muscleScore = Math.min(recencyScore + freqBonus, 1.0);



        // ----- Agingâ€‘risk weight -----

        let weight = 1.0;

        if (muscle.agingRisk === 'high') weight = 2.0;

        else if (muscle.agingRisk === 'medium') weight = 1.5;

        else if (muscle.agingRisk === 'low') weight = 1.0;



        totalWeightedScore += muscleScore * weight;

        totalWeight += weight;

    });



    if (totalWeight === 0) return 50; // fallback (should not happen)



    const rawScore = (totalWeightedScore / totalWeight) * 100;

    return Math.min(100, Math.round(rawScore));

}



function calculatePostureScore() {

    // ----- 1. Define posture muscles and their importance weights -----

    const postureMuscles = [

        { name: "neck", weight: 1.5 },

        { name: "deep_neck", weight: 1.5 },

        { name: "rhomboids", weight: 2.0 },

        { name: "rear_delts", weight: 1.2 },

        { name: "traps", weight: 1.0 }

    ];



    const now = new Date();

    const sixtyDaysAgo = new Date(now);

    sixtyDaysAgo.setDate(now.getDate() - 60);



    // ----- 2. Count how many times each posture muscle has been worked in the last 60 days -----

    const muscleSessionCount = {};

    postureMuscles.forEach(m => muscleSessionCount[m.name] = 0);



    workoutData.workouts.forEach(workout => {

        const workoutDate = new Date(workout.date);

        if (workoutDate < sixtyDaysAgo) return; // ignore older workouts

        workout.exercises?.forEach(ex => {

            if (ex.actual && !ex.skipped && ex.muscleGroup) {

                ex.muscleGroup.forEach(mg => {

                    if (muscleSessionCount.hasOwnProperty(mg)) {

                        muscleSessionCount[mg]++;

                    }

                });

            }

        });

    });



    // ----- 3. Compute score for each muscle -----

    let totalWeightedScore = 0;

    let totalWeight = 0;



    postureMuscles.forEach(muscle => {

        const name = muscle.name;

        const lastTrained = muscleLastTrained[name];

        const daysSince = lastTrained ? daysSinceTrained(name) : Infinity;



        // Recency component: linear decay from 1 (if â‰¤14 days) to 0 (if â‰¥60 days)

        let recencyScore;

        if (daysSince <= 14) {

            recencyScore = 1.0;

        } else if (daysSince >= 60) {

            recencyScore = 0;

        } else {

            recencyScore = 1 - (daysSince - 14) / 46; // 46 days from 15 to 60

        }



        // Frequency bonus: up to +0.2 for 4+ sessions

        const sessions = muscleSessionCount[name] || 0;

        const freqBonus = Math.min(sessions, 4) * 0.05; // 0, 0.05, 0.1, 0.15, 0.2



        // Combine, cap at 1.0

        const muscleScore = Math.min(recencyScore + freqBonus, 1.0);



        totalWeightedScore += muscleScore * muscle.weight;

        totalWeight += muscle.weight;

    });



    if (totalWeight === 0) return 50; // fallback (should not happen)



    const rawScore = (totalWeightedScore / totalWeight) * 100;

    return Math.min(100, Math.round(rawScore));

}



function calculateMuscleBalanceScore() {

    // ----- 1. Define muscle groups -----

    const pushMuscles = ["chest", "triceps", "front_delts"];

    const pullMuscles = ["back", "biceps", "rear_delts"];



    // ----- 2. Helper: recency score (1 if â‰¤14 days, 0 if â‰¥60 days, linear decay) -----

    function recencyScore(muscleName) {

        const last = muscleLastTrained[muscleName];

        if (!last) return 0;

        const daysSince = daysSinceTrained(muscleName);

        if (daysSince <= 14) return 1;

        if (daysSince >= 60) return 0;

        // linear decay from 1 at 14 days to 0 at 60 days

        return 1 - (daysSince - 14) / 46;

    }



    // ----- 3. Compute average recency for push and pull -----

    let pushTotal = 0;

    pushMuscles.forEach(m => pushTotal += recencyScore(m));

    const pushAvg = pushTotal / pushMuscles.length;



    let pullTotal = 0;

    pullMuscles.forEach(m => pullTotal += recencyScore(m));

    const pullAvg = pullTotal / pullMuscles.length;



    // ----- 4. Balance ratio (smaller / larger) -----

    if (pushAvg === 0 && pullAvg === 0) return 0;

    const maxAvg = Math.max(pushAvg, pullAvg);

    const minAvg = Math.min(pushAvg, pullAvg);

    const balanceRatio = minAvg / maxAvg; // always between 0 and 1



    // ----- 5. Convert to 0â€‘100 score -----

    const score = Math.round(balanceRatio * 100);

    return score;

}



function calculateConsistencyScore() {

    const now = new Date();

    const eightWeeksAgo = new Date(now);

    eightWeeksAgo.setDate(now.getDate() - 56); // 8 weeks



    // ----- 1. Weekly workout counts (last 8 weeks) -----

    const weeks = {};

    for (let i = 0; i < 8; i++) {

        const weekStart = new Date(eightWeeksAgo);

        weekStart.setDate(eightWeeksAgo.getDate() + i * 7);

        const weekEnd = new Date(weekStart);

        weekEnd.setDate(weekStart.getDate() + 6);

        weeks[i] = { start: weekStart, end: weekEnd, count: 0 };

    }



    workoutData.workouts.forEach(workout => {

        const wDate = new Date(workout.date);

        if (wDate < eightWeeksAgo) return;

        for (let i = 0; i < 8; i++) {

            if (wDate >= weeks[i].start && wDate <= weeks[i].end) {

                weeks[i].count++;

                break;

            }

        }

    });



    const weeklyCounts = Object.values(weeks).map(w => w.count);



    // ----- 2. Longâ€‘term frequency (average workouts/week) -----

    const avgWeekly = weeklyCounts.reduce((a, b) => a + b, 0) / weeklyCounts.length;

    let freqScore = 0;

    if (avgWeekly >= 3.5) freqScore = 40;

    else if (avgWeekly >= 2.5) freqScore = 30;

    else if (avgWeekly >= 1.5) freqScore = 20;

    else if (avgWeekly >= 0.5) freqScore = 10;

    else freqScore = 0;



    // ----- 3. Streak bonus -----

    const streak = calculateStreak();

    let streakScore = 0;

    if (streak >= 30) streakScore = 30;

    else if (streak >= 14) streakScore = 25;

    else if (streak >= 7) streakScore = 20;

    else if (streak >= 3) streakScore = 15;

    else if (streak >= 1) streakScore = 10;

    else streakScore = 0;



    // ----- 4. Adherence to planned days (if set) -----

    const plannedDays = workoutData.user.settings?.workoutDays || [];

    let adherenceScore = 0;

    if (plannedDays.length > 0) {

        const fourWeeksAgo = new Date(now);

        fourWeeksAgo.setDate(now.getDate() - 28);

        let plannedCount = 0;

        let actualCount = 0;



        // Count planned days in last 4 weeks

        for (let d = new Date(fourWeeksAgo); d <= now; d.setDate(d.getDate() + 1)) {

            const dayName = d.toLocaleDateString('en-US', { weekday: 'long' }).toLowerCase();

            if (plannedDays.includes(dayName)) {

                plannedCount++;

                // Check if there was a workout on this day

                const hasWorkout = workoutData.workouts.some(w => {

                    const wDate = new Date(w.date);

                    return wDate.toDateString() === d.toDateString();

                });

                if (hasWorkout) actualCount++;

            }

        }



        if (plannedCount > 0) {

            adherenceScore = Math.round((actualCount / plannedCount) * 20); // max 20

        }

    }



    // ----- 5. Regularity penalty (coefficient of variation) -----

    const mean = avgWeekly || 0.01;

    const variance = weeklyCounts.reduce((acc, val) => acc + Math.pow(val - mean, 2), 0) / weeklyCounts.length;

    const stdDev = Math.sqrt(variance);

    const cv = mean > 0 ? stdDev / mean : 0; // coefficient of variation

    let regularityScore = 0;

    if (cv <= 0.3) regularityScore = 10;

    else if (cv <= 0.5) regularityScore = 7;

    else if (cv <= 0.7) regularityScore = 4;

    else if (cv <= 1.0) regularityScore = 2;

    else regularityScore = 0;



    // ----- 6. Combine (max 40 + 30 + 20 + 10 = 100) -----

    const total = freqScore + streakScore + adherenceScore + regularityScore;

    return Math.min(100, Math.max(0, total));

}



function assessAgingRisks(scores) {

    // ----- 1. Get user info for age adjustments -----

    const birthDate = workoutData.user.birthDate;

    let age = null;

    if (birthDate) {

        const birth = new Date(birthDate);

        const today = new Date();

        age = today.getFullYear() - birth.getFullYear();

        const m = today.getMonth() - birth.getMonth();

        if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;

    }



    // ----- 2. Define risk factors with thresholds and weights -----

    const riskDefinitions = [

        {

            factor: "Neck/Posture Weakness",

            scoreKey: "posture",

            idealMin: 70,

            severeThreshold: 50,

            severity: "High",

            impact: "Forward head posture, cervical degeneration, headaches",

            baseRecommendation: "Add neck strengthening 2x/week",

            exercises: ["neck_isometric", "chin_tucks", "face_pull"],

            ageAdjust: (age) => age > 50 ? 5 : 0 // older adults need higher threshold

        },

        {

            factor: "Low Grip Strength",

            scoreKey: "gripStrength",

            idealMin: 60,

            severeThreshold: 40,

            severity: "Medium",

            impact: "Reduced independence, difficulty with daily tasks",

            baseRecommendation: "Add grip training 2x/week",

            exercises: ["wrist_curl", "hand_therapy", "farmer_walks"],

            ageAdjust: (age) => age > 60 ? 10 : 0

        },

        {

            factor: "Poor Balance",

            scoreKey: "balance",

            idealMin: 70,

            severeThreshold: 40,

            severity: "High",

            impact: "Increased fall risk, fractures, fear of falling",

            baseRecommendation: "Add balance exercises daily",

            exercises: ["ankle_stability", "single_leg_stands", "balance_board"],

            ageAdjust: (age) => age > 50 ? 10 : 0

        },

        {

            factor: "Joint Stiffness",

            scoreKey: "jointMobility",

            idealMin: 60,

            severeThreshold: 40,

            severity: "Medium",

            impact: "Reduced range of motion, arthritis progression",

            baseRecommendation: "Add joint mobility work 3x/week",

            exercises: ["terminal_extension", "ankle_stability", "shoulder_circles"],

            ageAdjust: (age) => age > 50 ? 5 : 0

        },

        {

            factor: "Muscle Imbalance (Push/Pull)",

            scoreKey: "muscleBalance",

            idealMin: 80,

            severeThreshold: 60,

            severity: "Medium",

            impact: "Postural issues, increased injury risk",

            baseRecommendation: "Incorporate more pulling exercises to balance pushing",

            exercises: ["face_pull", "barbell_row", "pull_up"],

            ageAdjust: (age) => 0 // no age adjustment

        },

        {

            factor: "Low Training Consistency",

            scoreKey: "consistency",

            idealMin: 70,

            severeThreshold: 40,

            severity: "Low",

            impact: "Missed training opportunities, reduced longâ€‘term gains",

            baseRecommendation: "Aim for at least 3 workouts per week",

            exercises: [], // no specific exercises

            ageAdjust: (age) => 0

        },

        {

            factor: "Neglected Longevity Muscles",

            scoreKey: "variety", // from enhanced calculateLongevityScore

            idealMin: 70,

            severeThreshold: 50,

            severity: "Medium",

            impact: "Unaddressed joint health muscles increase future injury risk",

            baseRecommendation: "Add more variety to target all joint health areas",

            exercises: [], // too broad for specific exercises

            ageAdjust: (age) => 0

        },

        {

            factor: "Strength Decline Trend",

            scoreKey: "trend", // from enhanced calculateLongevityScore

            idealMin: 50,

            severeThreshold: 30,

            severity: "Medium",

            impact: "Possible loss of muscle mass or strength",

            baseRecommendation: "Consider a structured progressive overload program",

            exercises: [], // generic

            ageAdjust: (age) => age > 50 ? 5 : 0

        }

    ];



    const risks = [];



    riskDefinitions.forEach(def => {

        const scoreValue = scores[def.scoreKey];

        if (scoreValue === undefined) return; // skip if score not available



        // Adjust idealMin based on age

        let adjustedIdeal = def.idealMin;

        if (age && def.ageAdjust) {

            adjustedIdeal += def.ageAdjust(age);

        }



        // If score is below severeThreshold, it's a definite risk with high severity

        if (scoreValue < def.severeThreshold) {

            risks.push({

                factor: def.factor,

                severity: def.severity, // could also increase severity if very low

                impact: def.impact,

                recommendation: def.baseRecommendation + ` (current score ${scoreValue}/100)`,

                exercises: def.exercises

            });

        }

        // If between severeThreshold and idealMin, it's a moderate risk

        else if (scoreValue < adjustedIdeal) {

            risks.push({

                factor: def.factor,

                severity: "Low", // not yet severe but needs attention

                impact: def.impact,

                recommendation: `Your ${def.factor.toLowerCase()} score is ${scoreValue}/100. ` + def.baseRecommendation,

                exercises: def.exercises

            });

        }

        // else score >= adjustedIdeal: no risk

    });



    return risks;

}



function generateLongevityRecommendations(scores, risks) {

    const recommendations = [];



