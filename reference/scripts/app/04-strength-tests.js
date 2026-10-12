// ============================================================
// reference/scripts/app/04-strength-tests.js
// Extracted from reference/scripts/app.js
// Original lines: 3782-4253  (472 lines)
// Section: Strength ratios, test processing, modal, weekly plan
// ============================================================

      // ========== TEST RESULT PROCESSING ==========

async function processTestResult(exerciseId, weight, reps) {

    if (reps < 1 || reps > 15) {

        await Swal.fire({

            title: 'Invalid Reps',

            text: 'Reps must be between 1 and 15 for accurate 1RM estimation.',

            icon: 'error',

            confirmButtonText: 'OK'

        });

        return null;

    }



    // Estimate 1RM using Epley formula

    const epley = weight * (1 + reps / 30);

    const estimated1RM = epley;



    const record = getOrCreateExerciseRecord(exerciseId);

    record.tested1RM = estimated1RM;

    record.testDate = new Date().toISOString();

    record.testReps = reps;

    record.testWeight = weight;



    // Add to history as a test entry

    record.history.push({

        date: record.testDate,

        weight: weight,

        reps: [reps],

        rpe: 7,

        isTest: true,

        estimated1RM: estimated1RM

    });



    saveToLocalStorage();



    // Send test result to backend (fire and forget)

    const testPayload = {

        deviceId: getDeviceId(),

        deviceName: await getDeviceInfo(),

        dataType: 'test',

        timestamp: new Date().toISOString(),

        name: workoutData.user.name || '',

        data: {

            exerciseId: exerciseId,

            weight: weight,

            reps: reps,

            estimated1RM: estimated1RM,

            notes: document.getElementById('testNotes')?.value || ''

        }

    };



    /* GAS removed */ (function(){})( {

        method: 'POST',

        headers: { 'Content-Type': 'application/json' },

        body: JSON.stringify(testPayload)

    }).catch(e => console.warn('Backend sync failed', e));



    return estimated1RM;

}

      

// ========== RE-TEST REMINDER ==========

function checkAndRemindReTest() {

    const sixWeeksAgo = new Date();

    sixWeeksAgo.setDate(sixWeeksAgo.getDate() - 42); // 6 weeks



    for (const exId in workoutData.exercises) {

        const record = workoutData.exercises[exId];

        if (record.tested1RM && record.testDate) {

            const testDate = new Date(record.testDate);

            if (testDate < sixWeeksAgo) {

                // Found an exercise with a test older than 6 weeks

                const exercise = getExerciseById(exId);

                if (exercise) {

                    showNotification(

                        `Your 1RM test for ${exercise.name} is over 6 weeks old. Consider re-testing.`,

                        'info',

                        'retest_reminder',

                        function () {

                            try {

                                if (typeof showSection === 'function') showSection('library');

                                setTimeout(function () {

                                    var search = document.getElementById('librarySearch');

                                    if (search) {

                                        search.value = exercise.name;

                                        if (search.dispatchEvent) search.dispatchEvent(new Event('input'));

                                        if (search.focus) search.focus();

                                    }

                                    if (typeof renderLibrary === 'function') renderLibrary();

                                }, 120);

                            } catch (e) {}

                        }

                    );

                    return; // Show only one reminder per session

                }

            }

        }

    }

}

      

      // ========== EXERCISE LOOKUP ==========

function getExerciseById(exerciseId) {

    for (const group in ultimateExerciseLibrary) {

        const found = ultimateExerciseLibrary[group].find(ex => 

            ex.name.toLowerCase().replace(/\s/g, '_') === exerciseId

        );

        if (found) return found;

    }

    return null;

}



// ========== TEST MODAL UI ==========

let testOnCompleteCallback = null; // stores callback for test queue



async function showTestModal(exerciseId, onComplete) {

    const exercise = getExerciseById(exerciseId);

    if (!exercise) {

        await Swal.fire({

            title: 'Exercise Not Found',

            text: 'The selected exercise could not be found in the library.',

            icon: 'error',

            confirmButtonText: 'OK'

        });

        return;

    }



    // Store callback for when test is submitted

    testOnCompleteCallback = onComplete;



    // Populate modal

    document.getElementById('testExerciseName').textContent = `Test: ${exercise.name}`;

    document.getElementById('testWeight').value = '';

    document.getElementById('testReps').value = '';



    // Set up submit button (remove any previous listeners to avoid duplicates)

    const submitBtn = document.getElementById('submitTestBtn');

    const newSubmitBtn = submitBtn.cloneNode(true);

    submitBtn.parentNode.replaceChild(newSubmitBtn, submitBtn);

    

    newSubmitBtn.addEventListener('click', async function() {

        const weight = parseFloat(document.getElementById('testWeight').value);

        const reps = parseInt(document.getElementById('testReps').value);

        

        if (isNaN(weight) || isNaN(reps)) {

            await Swal.fire({

                title: 'Missing Information',

                text: 'Please enter both weight and reps.',

                icon: 'warning',

                confirmButtonText: 'OK'

            });

            return;

        }

        

        const estimated = await processTestResult(exerciseId, weight, reps);

        if (estimated) {

            closeModal('testExerciseModal');

            if (testOnCompleteCallback) {

                testOnCompleteCallback();

                testOnCompleteCallback = null;

            }

            // Optionally refresh current workout if this exercise is in it

            if (currentWorkout && currentWorkout.exercises.some(ex => ex.id === exerciseId)) {

                if (document.getElementById('workout-section').classList.contains('active')) {

                    updateTodaysWorkout();

                }

            }

        }

    });



    // Show the modal

    showModal('testExerciseModal');

}

      

    // COMPLETE MUSCLE GROUPS DATABASE (40+ groups)

    const muscleDatabase = {

        major: [

            { name: "quads", display: "Quadriceps", restDays: 3, category: "lower" },

            { name: "hamstrings", display: "Hamstrings", restDays: 3, category: "lower" },

            { name: "glutes", display: "Glutes", restDays: 2, category: "lower" },

            { name: "chest", display: "Chest", restDays: 2, category: "upper" },

            { name: "back", display: "Back", restDays: 2, category: "upper" },

            { name: "shoulders", display: "Shoulders", restDays: 2, category: "upper" },

            { name: "biceps", display: "Biceps", restDays: 2, category: "arms" },

            { name: "triceps", display: "Triceps", restDays: 2, category: "arms" },

            { name: "calves", display: "Calves", restDays: 2, category: "lower" },

            { name: "core", display: "Core", restDays: 1, category: "core" },

            { name: "forearms", display: "Forearms", restDays: 2, category: "arms" },

            { name: "traps", display: "Traps", restDays: 2, category: "upper" },

            { name: "lats", display: "Latissimus Dorsi", restDays: 2, category: "upper" },

            { name: "rear_delts", display: "Rear Deltoids", restDays: 2, category: "upper" },

            { name: "obliques", display: "Obliques", restDays: 2, category: "core" },

            { name: "hip_flexors", display: "Hip Flexors", restDays: 2, category: "lower" },

            { name: "adductors", display: "Adductors", restDays: 3, category: "lower" },

            { name: "abductors", display: "Abductors", restDays: 2, category: "lower" },

            { name: "erectors", display: "Erector Spinae", restDays: 3, category: "core" },

            { name: "serratus", display: "Serratus Anterior", restDays: 2, category: "upper" }

        ],

        longevity: [

            { name: "neck", display: "Neck", restDays: 3, category: "longevity", agingRisk: "high" },

            { name: "deep_neck", display: "Deep Neck Flexors", restDays: 2, category: "longevity", agingRisk: "high" },

            { name: "levator_scap", display: "Levator Scapulae", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "rhomboids", display: "Rhomboids", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "teres", display: "Teres Major/Minor", restDays: 3, category: "longevity", agingRisk: "medium" },

            { name: "infraspinatus", display: "Infraspinatus", restDays: 3, category: "longevity", agingRisk: "high" },

            { name: "supraspinatus", display: "Supraspinatus", restDays: 3, category: "longevity", agingRisk: "high" },

            { name: "subscapularis", display: "Subscapularis", restDays: 3, category: "longevity", agingRisk: "high" },

            { name: "brachialis", display: "Brachialis", restDays: 2, category: "longevity", agingRisk: "low" },

            { name: "brachioradialis", display: "Brachioradialis", restDays: 2, category: "longevity", agingRisk: "low" },

            { name: "anconeus", display: "Anconeus", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "supinator", display: "Supinator", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "pronator", display: "Pronators", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "pec_minor", display: "Pectoralis Minor", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "coracobrach", display: "Coracobrachialis", restDays: 2, category: "longevity", agingRisk: "low" },

            { name: "popliteus", display: "Popliteus", restDays: 3, category: "longevity", agingRisk: "high" },

            { name: "tibialis", display: "Tibialis Anterior", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "soleus", display: "Soleus", restDays: 2, category: "longevity", agingRisk: "medium" },

            { name: "peroneus_tertius", display: "Peroneus Tertius", restDays: 2, category: "longevity", agingRisk: "high" },

            { name: "articularis_genus", display: "Articularis Genus", restDays: 2, category: "longevity", agingRisk: "high" },

            { name: "multifidus", display: "Multifidus", restDays: 2, category: "longevity", agingRisk: "high" },

            { name: "transverse", display: "Transverse Abdominis", restDays: 1, category: "longevity", agingRisk: "high" },

            { name: "quadratus_plantae", display: "Quadratus Plantae", restDays: 2, category: "longevity", agingRisk: "medium" }

        ],

        hands: [

            { name: "hand_lumbricals", display: "Hand Lumbricals", restDays: 1, category: "hands", agingRisk: "high" },

            { name: "hand_interossei", display: "Hand Interossei", restDays: 1, category: "hands", agingRisk: "high" },

            { name: "thenar", display: "Thenar/Hypothenar", restDays: 1, category: "hands", agingRisk: "high" }

        ],

        feet: [

            { name: "foot_intrinsics", display: "Foot Intrinsics", restDays: 1, category: "feet", agingRisk: "high" },

            { name: "foot_interossei", display: "Foot Interossei", restDays: 1, category: "feet", agingRisk: "high" },

            { name: "abductor_hallucis", display: "Abductor Hallucis", restDays: 2, category: "feet", agingRisk: "medium" },

            { name: "flexor_brevis", display: "Flexor Digitorum Brevis", restDays: 2, category: "feet", agingRisk: "medium" }

        ]

    };



    // WORKOUT PROGRAM (splits)

    const workoutProgram = {

        splits: [

