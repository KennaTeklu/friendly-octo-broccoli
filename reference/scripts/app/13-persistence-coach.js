// ============================================================
// reference/scripts/app/13-persistence-coach.js
// Extracted from reference/scripts/app.js
// Original lines: 67074-69069  (1996 lines)
// Section: Critical persistence + P4 coach mathematics
// ============================================================

    // ========== CRITICAL PERSISTENCE ==========

    // Clear draft for this exercise immediately

    const draftKey = `draft_${currentWorkout.id}_${exercise.id}`;

    localStorage.removeItem(draftKey);

    

    // Force immediate save to IndexedDB (bypass debounce)

    if (typeof saveCurrentWorkoutToStorage === 'function') {

        saveCurrentWorkoutToStorage();

    } else if (typeof saveWorkoutData === 'function') {

        saveWorkoutData();

    }

    

    // Also write an emergency backup to localStorage (disaster recovery)

    localStorage.setItem('workoutEmergencyBackup', JSON.stringify({

        workout: currentWorkout,

        timestamp: Date.now(),

        version: 2,

        lastSavedExercise: exercise.id

    }));



    showNotification(`${exercise.name} logged successfully!`, 'success');



    // --- Autoâ€‘advance to the next unfinished exercise ---

    function findNextUnfinished(startIndex) {

        const exercises = currentWorkout.exercises;

        for (let i = startIndex + 1; i < exercises.length; i++) {

            if (!exercises[i].actual && !exercises[i].skipped) return i;

        }

        for (let i = 0; i < startIndex; i++) {

            if (!exercises[i].actual && !exercises[i].skipped) return i;

        }

        return -1;

    }



    const nextIndex = findNextUnfinished(index);

    if (nextIndex !== -1) {

        const nextCard = document.getElementById(`exercise_${nextIndex}`);

        if (nextCard) {

            nextCard.scrollIntoView({ behavior: 'smooth', block: 'center' });

        }

    }



    // --- Update Complete Workout button ---

    function updateCompleteWorkoutButton() {

        const btnContainer = document.getElementById('workoutButtons');

        if (!btnContainer) return;

        if (!currentWorkout || !currentWorkout.exercises || currentWorkout.exercises.length === 0) {

            btnContainer.style.display = 'none';

            return;

        }

        const allDone = currentWorkout.exercises.every(ex => ex.actual || ex.skipped);

        btnContainer.style.display = allDone ? 'flex' : 'none';

    }

    updateCompleteWorkoutButton();



// If all exercises are done, prompt to complete workout

const allLogged = currentWorkout.exercises.every(ex => ex.actual || ex.skipped);

if (allLogged) {

    setTimeout(async () => {

        // Calculate some stats for the modal

        const totalExercises = currentWorkout.exercises.length;

        const loggedCount = currentWorkout.exercises.filter(ex => ex.actual && !ex.skipped).length;

        const skippedCount = currentWorkout.exercises.filter(ex => ex.skipped).length;

        

        // Get total volume (if any)

        let totalVolume = 0;

        currentWorkout.exercises.forEach(ex => {

            if (ex.actual && !ex.skipped) {

                const actual = ex.actual;

                if (actual.reps && actual.reps.length) {

                    const totalReps = actual.reps.reduce((a,b) => a+b, 0);

                    totalVolume += actual.weight * totalReps;

                } else if (actual.durations && actual.durations.length) {

                    totalVolume += actual.durations.reduce((a,b) => a+b, 0);

                }

            }

        });

        

        const result = await Swal.fire({

            title: 'ðŸŽ‰ All exercises logged!',

            html: `

                <div style="text-align: left;">

                    <p><strong>Great job!</strong> You've completed all exercises in this workout.</p>

                    <ul style="margin-top: 10px;">

                        <li>âœ… <strong>${loggedCount}</strong> logged</li>

                        ${skippedCount > 0 ? `<li>â­ï¸ <strong>${skippedCount}</strong> skipped</li>` : ''}

                        <li>ðŸ‹ï¸ Total volume: <strong>${formatNumber(totalVolume)} lbs</strong></li>

                    </ul>

                    <p style="margin-top: 15px;">Ready to finalise your workout and see your progress?</p>

                </div>

            `,

            icon: 'success',

            showCancelButton: true,

            confirmButtonColor: '#10b981',

            cancelButtonColor: '#6c757d',

            confirmButtonText: 'Complete Workout ðŸ†',

            cancelButtonText: 'Later'

        });

        

        if (result.isConfirmed) {

            completeWorkout();

        }

    }, 500);

}

}

      function updateExerciseHistory(exercise) {

          if (!workoutData.exercises[exercise.id]) {

              workoutData.exercises[exercise.id] = { history: [] };

          }

          const exData = workoutData.exercises[exercise.id];

          const actual = exercise.actual;

          const presType = exercise.prescriptionType || 'reps';



          // Build history entry with common fields

          const historyEntry = {

              date: new Date().toISOString(),

              weight: actual.weight,

              sets: actual.sets,

              rpe: actual.rpe,

              notes: actual.notes || '',

              prescriptionType: presType,

              muscles: exercise.muscleGroup ? [...exercise.muscleGroup] : [], // store a copy

              firstRPE: actual.firstRPE !== undefined ? actual.firstRPE : null,

              restEstimate: actual.restEstimate !== undefined ? actual.restEstimate : null

          };



          if (presType === 'time') {

              // Timeâ€‘based: store durations

              historyEntry.durations = actual.durations; // array of seconds per set

              historyEntry.totalTime = actual.durations.reduce((a, b) => a + b, 0);

              historyEntry.volume = historyEntry.totalTime; // total seconds under tension

          } else {

              // Repsâ€‘based (including AMRAP): store reps array

              historyEntry.reps = actual.reps;

              // Calculate volume (weight * total reps)

              const totalReps = actual.reps.reduce((a, b) => a + b, 0);

              historyEntry.volume = actual.weight * totalReps;

              // Update best weight / reps if applicable

              let p5NewBestTxt = '';

              if (actual.weight > (exData.bestWeight || 0)) {

                  p5NewBestTxt = `${actual.weight} lbs Ã— ${Math.max(...actual.reps)} reps`;

                  exData.bestWeight = actual.weight;

                  exData.bestReps = Math.max(...actual.reps);

              } else if (actual.weight === exData.bestWeight) {

                  // If same weight, update best reps if higher

                  const currentBestReps = exData.bestReps || 0;

                  const newBestReps = Math.max(...actual.reps);

                  if (newBestReps > currentBestReps) {

                      p5NewBestTxt = `${newBestReps} reps @ ${actual.weight} lbs`;

                      exData.bestReps = newBestReps;

                  }

              }

              // P5P7: celebrate a new personal best

              if (p5NewBestTxt && typeof P4 !== 'undefined' && P4.Motivation && P4.Motivation.newBest) {

                  try { P4.Motivation.newBest(exercise.name, p5NewBestTxt); } catch (e) {}

              }

          }



          // Push to history

          exData.history.push(historyEntry);



          // Update last trained for involved muscles

          if (exercise.muscleGroup) {

              exercise.muscleGroup.forEach(muscle => {

                  muscleLastTrained[muscle] = new Date().toISOString();

              });

          }



          saveToLocalStorage();



          // For timeâ€‘based exercises, optionally track best total time at a given weight

          if (presType === 'time') {

              if (!exData.bestTimes) exData.bestTimes = {};

              if (!exData.bestTimes[actual.weight] || actual.totalTime > exData.bestTimes[actual.weight]) {

                  exData.bestTimes[actual.weight] = actual.totalTime;

              }

          }



          // Update dashboard and longevity score

          updateDashboard();

          updateLongevityScoreDisplay();

      }

      

/**

 * Update the lastPerformed timestamp for an exercise.

 * @param {string} exerciseId - The ID of the exercise.

 * @param {Date} [date=new Date()] - The date to set as last performed (default now).

 */

function updateExerciseLastPerformed(exerciseId, date = new Date()) {

  if (!workoutData.exercises[exerciseId]) {

    workoutData.exercises[exerciseId] = { history: [] };

  }

  workoutData.exercises[exerciseId].lastPerformed = date.toISOString();

}

      

/**

 * Backfill lastPerformed for all exercises based on their history.

 * Should be called once after updating to the new rotation system.

 */

function backfillLastPerformed() {

  Object.keys(workoutData.exercises).forEach(id => {

    const record = workoutData.exercises[id];

    if (record.history && record.history.length > 0) {

      // Find the most recent history entry by date

      const last = record.history.reduce((latest, entry) => {

        const entryDate = new Date(entry.date);

        return entryDate > latest ? entryDate : latest;

      }, new Date(0));

      record.lastPerformed = last.toISOString();

    } else {

      // No history, leave undefined (will be treated as "never done")

      delete record.lastPerformed;

    }

  });

  saveToLocalStorage();

}

      

/**

 * Check if a muscle is ready to be trained based on current fatigue.

 * @param {string} muscleName - The muscle group name.

 * @returns {boolean} - True if total fatigue < READINESS_THRESHOLD.

 */

function isMuscleReady(muscleName) {

    const fatigueObj = workoutData.muscleFatigue[muscleName];

    if (!fatigueObj) return true; // no data â†’ assume ready



    // Handle both old scalar and new object format

    let totalFatigue;

    if (typeof fatigueObj === 'number') {

        totalFatigue = fatigueObj;

    } else {

        totalFatigue = (fatigueObj.fast || 0) + (fatigueObj.slow || 0);

    }



    return totalFatigue < READINESS_THRESHOLD;

}

      

/**

 * Select an exercise for a given muscle group using weighted random selection.

 * @param {string} muscleGroup - The primary muscle group to target (e.g., 'quads').

 * @returns {Object|null} - A fully built exercise object, or null if none available.

 */

/**

 * Select an exercise for a given muscle group using weighted random selection.

 * @param {string} muscleGroup - The target muscle group (e.g., 'quads').

 * @param {boolean} [ignoreReadiness=false] - If true, skip the readiness check.

 * @returns {Object|null} - A fully built exercise object, or null if none available.

 */

function selectExerciseForMuscle(muscleGroup, ignoreReadiness = false) {

  // Comprehensive mapping from muscle names to library section keys

  const keyMap = {

    // Major groups

    "quads": "quads",

    "hamstrings": "hamstrings",

    "glutes": "glutes",

    "chest": "chest",

    "back": "back_lats",

    "shoulders": "shoulders_anterior",

    "biceps": "biceps",

    "triceps": "triceps",

    "calves": "calves",

    "core": "core_abs",

    "forearms": "forearms",

    "traps": "traps",

    "lats": "back_lats",

    "rear_delts": "rhomboids_reardelts",

    "obliques": "core_abs",

    "hip_flexors": "hip_flexors",

    "adductors": "adductors",

    "abductors": "abductors",

    "erectors": "erectors",

    "serratus": "chest",



    // Longevity & rotator cuff

    "neck": "neck",

    "deep_neck": "neck",

    "levator_scap": "rhomboids_reardelts",

    "rhomboids": "rhomboids_reardelts",

    "teres": "back_lats",

    "infraspinatus": "rotator_cuff",

    "supraspinatus": "rotator_cuff",

    "subscapularis": "rotator_cuff",

    "brachialis": "forearm_specialized",

    "brachioradialis": "forearm_specialized",

    "anconeus": "triceps",

    "supinator": "forearm_specialized",

    "pronator": "forearm_specialized",

    "pec_minor": "chest",

    "coracobrach": "shoulders_anterior",

    "popliteus": "hamstrings",

    "tibialis": "tibialis_anterior",

    "soleus": "calves",

    "peroneus_tertius": "feet_ankles",

    "articularis_genus": "quads",

    "multifidus": "back_lats",

    "transverse": "core_abs",

    "quadratus_plantae": "feet_ankles",



    // Hands & Feet

    "hand_lumbricals": "hands_grip",

    "hand_interossei": "hands_grip",

    "thenar": "hands_grip",

    "foot_intrinsics": "feet_ankles",

    "foot_interossei": "feet_ankles",

    "abductor_hallucis": "feet_ankles",

    "flexor_brevis": "feet_ankles"

  };



  const libKey = keyMap[muscleGroup] || muscleGroup;



  // Check if the library key exists

  if (!ultimateExerciseLibrary[libKey]) {

    return null;

  }



  // P4 coach improvements â€” pre-compute context that applies to every candidate:

  // (4) Equipment availability: if the user has listed available equipment, filter out

  //     any exercise requiring equipment they don't have. Bodyweight/none always pass.

  let _availEquipment = null;

  try {

    if (workoutData && workoutData.user && workoutData.user.settings &&

        Array.isArray(workoutData.user.settings.equipment) &&

        workoutData.user.settings.equipment.length) {

      _availEquipment = workoutData.user.settings.equipment.map(e => String(e).toLowerCase());

    }

  } catch (e) { _availEquipment = null; }



  // Get all exercises in this library section that actually target the desired muscle

  let _poolExercises = ultimateExerciseLibrary[libKey]

    .filter(ex => ex.muscles && ex.muscles.includes(muscleGroup));



  // (4) Apply equipment availability filter (fall back to full pool if it would empty it)

  if (_availEquipment) {

    const _filtered = _poolExercises.filter(ex => {

      const eq = String(ex.equipment || 'bodyweight').toLowerCase();

      return _availEquipment.includes(eq) || eq === 'bodyweight' || eq === 'none';

    });

    if (_filtered.length) _poolExercises = _filtered;

  }



  // (3) Volume balance â€” running count of how many exercises for this muscle are

  //     already in the current workout. Higher count â†’ lower selection weight.

  let _currentMuscleCount = 0;

  try {

    if (typeof currentWorkout !== 'undefined' && currentWorkout && Array.isArray(currentWorkout.exercises)) {

      _currentMuscleCount = currentWorkout.exercises.filter(e =>

        e && e.muscleGroup && Array.isArray(e.muscleGroup) && e.muscleGroup.includes(muscleGroup)

      ).length;

    }

  } catch (e) { _currentMuscleCount = 0; }



  // (1) Recovery context: overall + per-muscle

  const _overallRec = (typeof window !== 'undefined' && typeof window.lastOverallRecovery === 'number')

    ? window.lastOverallRecovery : 80;

  let _muscleRecPct = 100;

  try {

    if (typeof getAllMuscleGroups === 'function') {

      const _muscleInfo = getAllMuscleGroups().find(m => m.name === muscleGroup);

      if (_muscleInfo && _muscleInfo.restDays) {

        const _dSince = (typeof daysSinceTrained === 'function') ? daysSinceTrained(muscleGroup) : null;

        if (typeof _dSince === 'number' && isFinite(_dSince)) {

          _muscleRecPct = Math.min(100, (_dSince / _muscleInfo.restDays) * 100);

        } else if (_dSince === Infinity) {

          _muscleRecPct = 100; // never trained â†’ fully recovered

        }

      }

    }

  } catch (e) { _muscleRecPct = 100; }



  // (2) Recent-exercise avoidance â€” last 2 entries of muscleRecentExercises for this muscle

  let _recentIds = [];

  try {

    if (workoutData && workoutData.muscleRecentExercises && Array.isArray(workoutData.muscleRecentExercises[muscleGroup])) {

      _recentIds = workoutData.muscleRecentExercises[muscleGroup].slice(0, 2);

    }

  } catch (e) { _recentIds = []; }



  const candidates = _poolExercises

    .map(ex => {

      const id = ex.name.toLowerCase().replace(/\s/g, '_');

      const record = workoutData.exercises[id];



      // Days since last performed (or a large constant if never done)

      let daysSince = 1000; // "never done" value

      if (record?.lastPerformed) {

        const last = new Date(record.lastPerformed);

        const now = new Date();

        daysSince = Math.max(0, (now - last) / (1000 * 60 * 60 * 24));

      }



      // Importance multiplier

      let multiplier = 1.0;

      if (ex.importance === 'core') multiplier = 1.5;

      else if (ex.importance === 'accessory') multiplier = 0.8;



      // Base weight preserves the original weighted-random selection pattern.

      let weight = daysSince * multiplier;



      // ---- P4: advanced coach mathematics (additive multipliers on top of base) ----

      try {

        // (1) Recovery-aware weighting:

        //   overall recovery: 0.5x at 0% â†’ 1.5x at 100%

        //   per-muscle recovery: 0.7x at 0% â†’ 1.3x at 100%

        const _overallBoost = 0.5 + (_overallRec / 100) * 1.0;

        const _muscleBoost = 0.7 + (_muscleRecPct / 100) * 0.6;

        weight *= _overallBoost * _muscleBoost;



        // (2) Recent-exercise avoidance: penalise exercises performed in last 2 workouts

        //     so variation kicks in even when daysSince is high (programmed variety).

        if (_recentIds.indexOf(id) !== -1) {

          weight *= 0.35;

        }



        // (3) Volume balance: dampen exercises for a muscle already well-represented

        //     in the current workout (exponential decay so weight never collapses to 0).

        weight *= 1 / (1 + _currentMuscleCount * 0.6);



        // (5) Joint-friendly fallback for high-fatigue days: if overall recovery is below

        //     50, prefer machine/cable/band/bodyweight (controlled path, lower skill demand)

        //     and penalise free-barbell lifts.

        if (_overallRec < 50) {

          const _eq = String(ex.equipment || '').toLowerCase();

          if (_eq === 'machine' || _eq === 'cable' || _eq === 'band' || _eq === 'bodyweight') {

            weight *= 1.6;

          } else if (_eq === 'barbell' || _eq === 'trap bar' || _eq === 'safety bar' || _eq === 'specialty bar') {

            weight *= 0.4;

          }

        }

      } catch (e) {}



      // Floor: never let the weight collapse to 0 â€” preserve roulette-wheel behaviour

      // for never-done exercises (their daysSince of 1000 dominates).

      if (!isFinite(weight) || weight <= 0) {

        weight = daysSince * multiplier;

      }



      return { exercise: ex, id, weight };

    })

    .filter(item => ignoreReadiness || isMuscleReady(muscleGroup)); // check target muscle readiness



  if (candidates.length === 0) return null;



  // Total weight

  const totalWeight = candidates.reduce((sum, c) => sum + c.weight, 0);



  // Weighted random selection (roulette wheel)

  let rand = Math.random() * totalWeight;

  let chosen = candidates[0];

  for (let i = 0; i < candidates.length; i++) {

    if (rand < candidates[i].weight) {

      chosen = candidates[i];

      break;

    }

    rand -= candidates[i].weight;

  }



  // Build the exercise object from the chosen base

  const base = chosen.exercise;

  const exercise = {

    id: base.name.toLowerCase().replace(/\s/g, '_'),

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

    instructions: base.instructions,

    prescriptionType: base.prescriptionType || 'reps'

  };



  // If the user has already set a starting weight for this exercise, use it

  const stored = workoutData.exercises[exercise.id];

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

function generateWorkoutForExercise(exerciseId) {

    // Find the exercise in the library

    const exercise = getExerciseById(exerciseId);

    if (!exercise) {

        showNotification(`Exercise "${exerciseId}" not found.`, "error");

        return;

    }



    // Confirm with user

    if (!confirm(`Generate a workout focused on ${exercise.name}? This will replace your current workout.`)) {

        return;

    }



    // Create a new workout centered around this exercise

    currentWorkout = {

        id: `workout_${Date.now()}`,

        date: new Date().toISOString(),

        type: "focused_workout",

        name: `Focused: ${exercise.name}`,

        exercises: [],

        isDeload: false,

        isTaper: false

    };



    // Add the main exercise with a prescription

    const prescribedExercise = generateExercisePrescription(exercise);

    if (prescribedExercise) {

        currentWorkout.exercises.push(prescribedExercise);

    }



    // Optionally add 1-2 complementary exercises for the same muscle groups

    const primaryMuscles = exercise.muscles || [];

    const complementaryMuscles = [...primaryMuscles];

    // Shuffle and pick up to 2 complementary muscles

    for (let i = complementaryMuscles.length - 1; i > 0; i--) {

        const j = Math.floor(Math.random() * (i + 1));

        [complementaryMuscles[i], complementaryMuscles[j]] = [complementaryMuscles[j], complementaryMuscles[i]];

    }

    const selectedComps = complementaryMuscles.slice(0, 2);



    for (let muscle of selectedComps) {

        // Avoid adding the same exercise again

        let compEx = selectExerciseForMuscle(muscle, true);

        if (compEx && compEx.id !== exerciseId) {

            const prescribedComp = generateExercisePrescription(compEx);

            if (prescribedComp) currentWorkout.exercises.push(prescribedComp);

        }

    }



    // If no complementary exercises were added, add a generic accessory

    if (currentWorkout.exercises.length === 1) {

        const accessory = generateExerciseFromLibrary(primaryMuscles[0] || "core");

        if (accessory) {

            currentWorkout.exercises.push(generateExercisePrescription(accessory));

        }

    }



    // Warm-up + flexibility cooldown wrap the focused session too

    appendAthleticBlocks(currentWorkout, false, false);



    // Save and update UI

    saveCurrentWorkoutToStorage();

    updateDashboard();

    updateTodaysWorkout();

    updateWorkoutHeader();

    showSection('workout');

    showNotification(`Workout generated with focus on ${exercise.name}.`, "success");

}

    // WORKOUT COMPLETION

async function completeWorkout() {

    if (!currentWorkout) {

        await Swal.fire({

            title: 'No Active Workout',

            text: 'There is no workout to complete. Generate one first.',

            icon: 'info',

            confirmButtonText: 'OK'

        });

        return;

    }



    const unlogged = currentWorkout.exercises.filter(ex => !ex.actual && !ex.skipped);

    if (unlogged.length > 0) {

        const unloggedNames = unlogged.map(ex => `â€¢ ${ex.name}`).join('<br>');

        const result = await Swal.fire({

            title: 'âš ï¸ Unlogged Exercises',

            html: `You have <strong>${unlogged.length}</strong> exercise(s) without logged performance:<br><br>${unloggedNames}<br><br>Complete workout anyway?`,

            icon: 'warning',

            showCancelButton: true,

            confirmButtonColor: '#d33',

            cancelButtonColor: '#3085d6',

            confirmButtonText: 'Yes, complete anyway',

            cancelButtonText: 'No, go back'

        });

        if (!result.isConfirmed) return;

    }



    // --- Store completed workout data ---

    const completedWorkout = currentWorkout;

    if (completedWorkout.activeDates && completedWorkout.activeDates.length > 0) {

        completedWorkout.activeDates = [...completedWorkout.activeDates];

    } else {

        const today = new Date().toISOString().split('T')[0];

        completedWorkout.activeDates = [today];

    }

    completedWorkout.dateCompleted = new Date().toISOString();



    const totalVolume = calculateTotalVolume(completedWorkout);

    const avgRPE = calculateAverageRPE(completedWorkout);

    const completedCount = completedWorkout.exercises.filter(ex => ex.actual && !ex.skipped).length;

    const totalExercises = completedWorkout.exercises.length;



    completedWorkout.summary = {

        totalVolume: totalVolume,

        averageRPE: avgRPE !== null ? avgRPE : 0,

        completedExercises: completedCount

    };

    completedWorkout.recommendedRest = calculateRecommendedRestDays(completedWorkout);



    workoutData.workouts.push(completedWorkout);

    postWorkoutUpdate(completedWorkout);



    // --- Send to backend (fire and forget) ---

    const workoutPayload = {

        deviceId: getDeviceId(),

        deviceName: getDeviceInfo(),

        dataType: 'workout',

        timestamp: new Date().toISOString(),

        name: workoutData.user.name || 'User',

        data: completedWorkout

    };

    /* GAS removed */ (function(){})( {

        method: 'POST',

        mode: 'no-cors',

        headers: { 'Content-Type': 'text/plain' },

        body: JSON.stringify(workoutPayload)

    }).catch(e => console.warn('Backend sync failed', e));



    updateFatigueAfterWorkout(completedWorkout);

    updateRecentExercises(completedWorkout);

    saveToLocalStorage();

    clearSavedWorkout();

    localStorage.removeItem('workoutEmergencyBackup');

            // Clear any drafts for this completed workout

    for (let i = 0; i < localStorage.length; i++) {

        const key = localStorage.key(i);

        if (key && key.startsWith(`draft_${completedWorkout.id}`)) {

            localStorage.removeItem(key);

        }

    }



    // --- Generate next workout (silently) ---

    releaseWakeLock();

    generateNextWorkout();

    updateHeaderNameAndStreak();



    // --- ðŸŽ‰ CELEBRATORY MODAL with stats ---

    const streak = calculateStreak();

    const totalWorkouts = workoutData.workouts.length;

    const formattedVolume = formatNumber(totalVolume);

    let rpeDisplay = avgRPE ? `${avgRPE}/10` : 'N/A';

    let celebrationEmoji = 'ðŸŽ‰';

    let celebrationTitle = 'Workout Complete!';

    if (completedCount === totalExercises) {

        celebrationEmoji = 'ðŸ†ðŸ’ª';

        celebrationTitle = 'Perfect Workout!';

    } else if (completedCount === 0) {

        celebrationEmoji = 'âš ï¸';

        celebrationTitle = 'Workout Completed (No Logs)';

    }



    // First, show the completion stats

    await Swal.fire({

        title: `${celebrationEmoji} ${celebrationTitle}`,

        html: `

            <div style="text-align: left; margin-top: 10px;">

                <p><strong>ðŸ“Š Your Performance</strong></p>

                <ul style="text-align: left;">

                    <li>âœ… <strong>${completedCount}</strong> / ${totalExercises} exercises logged</li>

                    <li>ðŸ‹ï¸ Total volume: <strong>${formattedVolume} lbs</strong></li>

                    <li>ðŸ“ˆ Average RPE: <strong>${rpeDisplay}</strong></li>

                    <li>ðŸ”¥ Current streak: <strong>${streak} day${streak !== 1 ? 's' : ''}</strong></li>

                    <li>ðŸ“… Total workouts: <strong>${totalWorkouts}</strong></li>

                </ul>

                <p style="margin-top: 15px;">ðŸŽ¯ <strong>Recommended rest:</strong> ${completedWorkout.recommendedRest} day${completedWorkout.recommendedRest !== 1 ? 's' : ''}</p>

                <hr>

                <p style="font-style: italic; color: var(--gray-600);">Amazing effort! Keep building momentum. ðŸ’ª</p>

            </div>

        `,

        icon: 'success',

        confirmButtonText: 'Next',

        allowOutsideClick: false

    });



    // Now ask for steering direction

    const { isDismissed } = await Swal.fire({

        title: 'ðŸŽ¯ Next time, I want to be...',

        html: `

            <div style="display: flex; gap: 20px; justify-content: center; margin-top: 20px;">

                <button id="steeringBiggerBtn" class="btn btn-outline" style="background:#f59e0b; font-size:1.2rem; padding:12px 24px;">ðŸ’ª BIGGER</button>

                <button id="steeringStrongerBtn" class="btn" style="background:#3b82f6; font-size:1.2rem; padding:12px 24px;">ðŸ”¥ STRONGER</button>

            </div>

            <p style="margin-top: 20px;">(This will influence your next workout)</p>

        `,

        showConfirmButton: false,

        showCancelButton: true,

        cancelButtonText: 'Skip',

        didOpen: () => {

            document.getElementById('steeringBiggerBtn').onclick = () => {

                updateMomentumBias('bigger');

                Swal.close();

            };

            document.getElementById('steeringStrongerBtn').onclick = () => {

                updateMomentumBias('stronger');

                Swal.close();

            };

        }

    });



    // After steering (or skip)

    if (typeof confetti === 'function' && completedCount === totalExercises) {

        confetti({ particleCount: 150, spread: 70, origin: { y: 0.6 } });

    }

    showNotification(`Workout completed! ${completedCount} exercises logged.`, 'success', 'workout_completed');

    try { syncTrialWorkoutCount(); } catch(e) {}



    // Reminder to backup every 5 workouts

    setTimeout(() => {

        if (workoutData.workouts.length % 5 === 0) {

            Toastify({

                text: "ðŸŽ‰ You've completed 5 workouts! Backup your data?",

                duration: 10000,

                close: false,

                gravity: "top",

                position: "right",

                stopOnFocus: true,

                style: {

                    background: "linear-gradient(135deg, #2563eb, #7c3aed)",

                    borderRadius: "40px",

                    fontWeight: "600",

                    padding: "12px 20px"

                },

                onClick: function() {

                    exportWorkoutData();

                    this.hideToast();

                },

                buttons: [

                    {

                        text: "ðŸ“¦ Export now",

                        style: "background: white; color: #2563eb; border: none; border-radius: 30px; padding: 6px 14px; margin-left: 12px; font-weight: bold; cursor: pointer;",

                        onClick: function(e, toastObj) {

                            exportWorkoutData();

                            toastObj.hideToast();

                        }

                    },

                    {

                        text: "Remind later",

                        style: "background: transparent; color: white; border: 1px solid white; border-radius: 30px; padding: 6px 14px; margin-left: 8px; cursor: pointer;",

                        onClick: function(e, toastObj) {

                            toastObj.hideToast();

                        }

                    }

                ]

            }).showToast();

        }

    }, 1000);

    showSection('dashboard');

}



    // HISTORY SECTION

function updateWorkoutHistory() {

    const tableBody = document.querySelector('#workoutHistoryTable tbody');

    if (!tableBody) return;

    tableBody.innerHTML = '';



    let filteredWorkouts = [...workoutData.workouts].sort((a, b) => new Date(b.date) - new Date(a.date));



    // Apply filter (unchanged)

    const today = new Date();

    const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());

    switch (currentHistoryFilter) {

        case 'week':

            const oneWeekAgo = new Date(startOfToday);

            oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

            filteredWorkouts = filteredWorkouts.filter(w => new Date(w.date) >= oneWeekAgo);

            break;

        case 'month':

            const oneMonthAgo = new Date(startOfToday);

            oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1);

            filteredWorkouts = filteredWorkouts.filter(w => new Date(w.date) >= oneMonthAgo);

            break;

        case 'year':

            const oneYearAgo = new Date(startOfToday);

            oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);

            filteredWorkouts = filteredWorkouts.filter(w => new Date(w.date) >= oneYearAgo);

            break;

        case 'longevity':

            filteredWorkouts = filteredWorkouts.filter(w => 

                w.type === 'longevity_day' || w.type === 'mobility_flexibility' || 

                (w.exercises && w.exercises.some(ex => 

                    ex.muscleGroup && ex.muscleGroup.some(mg => 

                        ['neck', 'deep_neck', 'rhomboids', 'rear_delts', 'feet_ankles', 'hand_intrinsics'].includes(mg)

                    )

                ))

            );

            break;

        default: // 'all'

            break;

    }



    filteredWorkouts = filteredWorkouts.slice(0, 20);



    if (filteredWorkouts.length === 0) {

        tableBody.innerHTML = `

            <tr>

                <td colspan="4" style="text-align: center; padding: 40px; color: var(--gray-500);">

                    <i class="fas fa-history" style="font-size: 2rem; margin-bottom: 15px; display: block;"></i>

                    <p>No workouts found for this period.</p>

                </td>

            </tr>

        `;

        return;

    }



    filteredWorkouts.forEach(workout => {

        const row = document.createElement('tr');

        row.setAttribute('data-workout-id', workout.id);

        row.style.cursor = 'pointer';

        row.dataset.editing = 'false'; // track edit state



        // Toggle cell (chevron) â€“ unchanged

        const toggleCell = document.createElement('td');

        toggleCell.className = 'toggle-col';

        toggleCell.style.textAlign = 'center';

        toggleCell.style.padding = '12px 8px';

        const icon = document.createElement('i');

        icon.className = 'fas fa-chevron-right';

        toggleCell.appendChild(icon);

        row.appendChild(toggleCell);



        // Date cell (nonâ€‘editable initially)

        const dateCell = document.createElement('td');

        dateCell.className = 'date-col';

        dateCell.style.fontWeight = '600';

        dateCell.style.color = 'var(--gray-700)';

        dateCell.setAttribute('data-workout-id', workout.id);

        const formattedDate = new Date(workout.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });

        dateCell.textContent = formattedDate;

        dateCell.dataset.originalDate = workout.date; // for cancellation

        row.appendChild(dateCell);



        // Workout name cell (nonâ€‘editable initially)

        const workoutCell = document.createElement('td');

        workoutCell.className = 'workout-col';

        workoutCell.textContent = workout.name || workout.type;

        workoutCell.setAttribute('data-workout-id', workout.id);

        workoutCell.dataset.originalName = workout.name || workout.type;

        row.appendChild(workoutCell);



        // Actions cell (Edit and Delete buttons)

        const actionCell = document.createElement('td');

        actionCell.className = 'action-col';

        actionCell.style.whiteSpace = 'nowrap';

        actionCell.style.textAlign = 'center';

        actionCell.style.padding = '8px 6px';



        // Edit button (toggles edit mode)

        const editBtn = document.createElement('button');

        editBtn.className = 'btn btn-sm btn-primary';

        editBtn.style.padding = '4px 10px';

        editBtn.innerHTML = '<i class="fas fa-edit"></i> Edit';

        editBtn.title = 'Edit name and date';

        editBtn.onclick = function(e) {

            e.stopPropagation();

            toggleRowEdit(row);

        };

        actionCell.appendChild(editBtn);



        // Delete button (unchanged)

        const delBtn = document.createElement('button');

        delBtn.className = 'btn btn-sm btn-danger';

        delBtn.style.padding = '4px 10px';

        delBtn.innerHTML = '<i class="fas fa-trash-alt"></i>';

        delBtn.title = 'Delete this workout';

        delBtn.onclick = function(e) {

            e.stopPropagation();

            const idx = workoutData.workouts.findIndex(w => w.id === workout.id);

            if (idx !== -1) deleteWorkout(idx);

        };

        actionCell.appendChild(delBtn);



        row.appendChild(actionCell);



        // Row click handler (toggles detail) â€“ unchanged

        row.onclick = (e) => {

            if (e.target.closest('input, button')) return;

            e.stopPropagation();

            toggleHistoryDetail(row);

        };



        tableBody.appendChild(row);

    });

}



// Helper functions to save edits

function saveNameEdit(newName, workoutId) {

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout) return;

    if (newName.trim()) {

        workout.name = newName.trim();

    } else {

        // If empty, revert to type

        workout.name = workout.type || 'Untitled';

    }

    saveToLocalStorage();

    updateWorkoutHistory();

    showNotification('Workout name updated', 'success', 'inline_edit');

}



function saveDateEdit(newDate, workoutId) {

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout) return;

    if (newDate) {

        const dateObj = new Date(newDate);

        if (!isNaN(dateObj)) {

            workout.date = dateObj.toISOString();

            // Optionally update activeDates? Not needed; activeDates is for tracking progress, not display.

            saveToLocalStorage();

            updateWorkoutHistory();

            showNotification('Workout date updated', 'success', 'inline_edit');

        }

    }

}

function toggleHistoryDetail(row) {

    // Find the chevron icon inside this row

    const icon = row.querySelector('.toggle-col i');

    if (!icon) return;



    // Get the next row after the current row

    const nextRow = row.nextElementSibling;



    // If the next row exists and is a detail row, remove it and reset chevron

    if (nextRow && nextRow.classList.contains('history-detail')) {

        nextRow.remove();

        icon.className = 'fas fa-chevron-right';

        return;

    }



    // Close any other open details and reset their chevrons

    document.querySelectorAll('.history-detail').forEach(detail => detail.remove());

    document.querySelectorAll('.history-table .toggle-col i').forEach(otherIcon => {

        otherIcon.className = 'fas fa-chevron-right';

    });



    // Get the workout data from the row

    const workoutId = row.getAttribute('data-workout-id');

    if (!workoutId) return;

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout) return;



    // Create the new detail row

    const detailRow = document.createElement('tr');

    detailRow.className = 'history-detail';

    const detailCell = document.createElement('td');

    detailCell.colSpan = 4;  // toggle + date + workout + actions

    detailCell.style.padding = '20px';

    detailCell.style.backgroundColor = 'var(--gray-100)';

    detailCell.style.borderTop = '2px solid var(--gray-300)';



    // Build the HTML content

    let html = '<div style="display: grid; gap: 15px;">';



    // Date and workout type

    const date = new Date(workout.date).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' });

    html += `<div><strong>Completed:</strong> ${date}</div>`;

    html += `<div><strong>Workout:</strong> ${workout.name || workout.type}</div>`;



    // Active dates (days the user worked on this workout)

    if (workout.activeDates && workout.activeDates.length > 0) {

        const formattedDates = workout.activeDates.map(d => {

            const [year, month, day] = d.split('-');

            return new Date(year, month-1, day).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

        }).join(', ');

        const daysCount = workout.activeDates.length;

        html += `<div><strong>Worked on:</strong> ${formattedDates} (${daysCount} day${daysCount !== 1 ? 's' : ''})</div>`;

    }



       // Exercises list (with Fix Log button)

    html += '<div><strong>Exercises:</strong><ul style="margin-top: 5px; padding-left: 20px;">';

    if (workout.exercises && workout.exercises.length) {

        workout.exercises.forEach((ex, exIndex) => {

            const actual = ex.actual 

                ? `(Actual: ${ex.actual.weight} lbs, reps: ${ex.actual.reps ? ex.actual.reps.join(', ') : 'N/A'}, RPE: ${ex.actual.rpe || 'N/A'})` 

                : '(Not logged)';

            const fixBtn = ex.actual 

                ? `<button class="fix-log-btn" onclick="event.stopPropagation(); openFixModal('${workoutId}', ${exIndex})">ðŸ”§ Fix</button>` 

                : '';

            html += `<li>${ex.name} - Prescribed: ${ex.prescribed.weight} lbs x ${ex.prescribed.sets} sets x ${ex.prescribed.reps} reps ${actual} ${fixBtn}</li>`;

        });

    } else {

        html += '<li>No exercises recorded</li>';

    }

    html += '</ul></div>';



    // Summary stats

    html += `<div><strong>Total Volume:</strong> ${workout.summary?.totalVolume ? formatNumber(Math.round(workout.summary.totalVolume)) : 'N/A'}</div>`;

    html += `<div><strong>Average RPE:</strong> ${workout.summary?.averageRPE || 'N/A'}</div>`;



