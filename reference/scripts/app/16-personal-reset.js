// ============================================================
// reference/scripts/app/16-personal-reset.js
// Extracted from reference/scripts/app.js
// Original lines: 73020-74249  (1230 lines)
// Section: savePersonalInfo, reset workflow, wipe, import restore
// ============================================================

    // ----- 1. Gather and validate inputs -----

    const name = document.getElementById('settingsName').value.trim();

    const birthDate = document.getElementById('settingsBirthDate').value;

    const gender = document.getElementById('settingsGender').value;

    const weightRaw = document.getElementById('settingsWeight').value;

    const heightRaw = document.getElementById('settingsHeight').value;

    const experience = document.getElementById('settingsExperience').value;

    const goal = document.getElementById('settingsGoal').value;

    const restTimeRaw = document.getElementById('settingsRestTime').value;

    const progressionRaw = document.getElementById('settingsProgression').value;



    // Validation flags

    let hasError = false;

    let errorMessages = [];



    // Birth date: cannot be in the future

    if (birthDate) {

        const birth = new Date(birthDate);

        const today = new Date();

        if (birth > today) {

            errorMessages.push('Birth date cannot be in the future.');

            hasError = true;

        }

    }

  

const autoHide = document.getElementById('bottomNavAutoHide').checked;

workoutData.user.settings.bottomNavAutoHide = autoHide;

  

const muscleCoupling = document.getElementById('muscleCouplingToggle').checked;

workoutData.user.settings.muscleCoupling = muscleCoupling;



// P5P8: postpartum months (female only, optional)

{

    const ppEl = document.getElementById('settingsPostpartum');

    const ppVal = ppEl ? parseFloat(ppEl.value) : NaN;

    if (gender === 'female' && !isNaN(ppVal) && ppVal >= 0) {

        workoutData.user.postpartumMonths = ppVal;

    } else {

        delete workoutData.user.postpartumMonths;

    }

}



    // Weight: must be positive number if provided

    let weight = null;

    if (weightRaw) {

        weight = parseFloat(weightRaw);

        if (isNaN(weight) || weight <= 0) {

            errorMessages.push('Weight must be a positive number.');

            hasError = true;

        }

    }



    // Height: must be positive number if provided

    let height = null;

    if (heightRaw) {

        height = parseFloat(heightRaw);

        if (isNaN(height) || height <= 0) {

            errorMessages.push('Height must be a positive number.');

            hasError = true;

        }

    }



    // Rest time: integer between 30 and 300

    let restTime = parseInt(restTimeRaw);

    if (isNaN(restTime) || restTime < 30 || restTime > 300) {

        errorMessages.push('Rest time must be between 30 and 300 seconds.');

        hasError = true;

    }



    // Progression rate: number between 0.5% and 5%

    let progressionRate = parseFloat(progressionRaw);

    if (isNaN(progressionRate) || progressionRate < 0.5 || progressionRate > 5) {

        errorMessages.push('Progression rate must be between 0.5% and 5%.');

        hasError = true;

    } else {

        progressionRate = progressionRate / 100; // convert to decimal

    }



    if (hasError) {

        alert('Please fix the following:\n- ' + errorMessages.join('\n- '));

        return;

    }



    // ----- 2. Update workoutData.user -----

    workoutData.user.name = name || ''; // allow empty

        updateHeaderNameAndStreak();

    workoutData.user.birthDate = birthDate || null;

    workoutData.user.gender = gender;

    workoutData.user.weight = weight;

    workoutData.user.height = height;

    workoutData.user.experience = experience;

    workoutData.user.goal = goal;



    // Settings object

    workoutData.user.settings = {

        workoutDays: Array.from(document.querySelectorAll('.workout-day:checked')).map(cb => cb.value),

        restTime: restTime,

        progressionRate: progressionRate,

        theme: localStorage.getItem('workoutTheme') || 'blue',

        darkMode: localStorage.getItem('workoutDarkMode') === 'true'

    };



    // ----- 3. Save menstrual data if available (from period modal) -----

    const lastPeriodInput = document.getElementById('lastPeriodDate');

    const cycleLengthInput = document.getElementById('cycleLength');

    if (lastPeriodInput && cycleLengthInput) {

        const lastPeriod = lastPeriodInput.value;

        const cycleLength = parseInt(cycleLengthInput.value);

        if (lastPeriod) {

            workoutData.user.menstrual = workoutData.user.menstrual || { symptoms: [] };

            workoutData.user.menstrual.lastPeriodStart = lastPeriod;

            if (!isNaN(cycleLength) && cycleLength >= 20 && cycleLength <= 40) {

                workoutData.user.menstrual.cycleLength = cycleLength;

            }

        }

    }



    // ----- 4. Handle UI updates based on gender change -----

    const oldGender = workoutData.user.gender; // we haven't overwritten yet, but we have new gender

    // Actually we already set gender above, so we compare with previous? We can store previous.

    // We'll track if gender changed to adjust period button.

    const previousGender = workoutData.user.gender; // but we just set it â€“ need to capture before change

    // Let's reorder: capture previous gender before updating.

    // I'll add a line at the top: const prevGender = workoutData.user.gender;

    // For simplicity, I'll just add the period button logic based on new gender.

    // But if gender changed from female to male, we need to remove the button.

    // We can handle that in a moment after saving.



    // ----- 5. Save to localStorage -----

    // Update caches before persisting

    updateUserCache();

    if (currentWorkout) updateWorkoutCache();

    saveToLocalStorage();



    // ----- 6. Update UI elements that depend on settings -----

    updateNavigation();                // updates nav bar with name, streak, etc.

    updateLongevityScoreDisplay();     // longevity score may change with new body weight



        updatePeriodButtonVisibility();



    // If no workouts exist and no current workout, generate one

    if (workoutData.workouts.length === 0 && !currentWorkout) {

        generateNextWorkout();

    } else if (goal !== workoutData.user.goal || experience !== workoutData.user.experience) {

        // If goal or experience changed, offer to regenerate the workout

        if (confirm('Your training goal or experience level has changed. Would you like to generate a new workout based on your updated preferences?')) {

            generateNextWorkout();

        }

    }



    // Show success message

    showNotification('Settings saved successfully!', 'success');



  // ----- Send profile to backend -----

  const payload = {

    deviceId: getDeviceId(),

    dataType: 'profile',

    timestamp: new Date().toISOString(),

    name: workoutData.user.name || '',

    data: {

      name: workoutData.user.name,

      birthDate: workoutData.user.birthDate,

      gender: workoutData.user.gender,

      weight: workoutData.user.weight,

      height: workoutData.user.height,

      experience: workoutData.user.experience,

      goal: workoutData.user.goal,

      workoutDays: workoutData.user.settings.workoutDays,

      restTime: workoutData.user.settings.restTime,

      progressionRate: workoutData.user.settings.progressionRate,

      bottomNavAutoHide: workoutData.user.settings.bottomNavAutoHide,

      lastPeriodStart: workoutData.user.menstrual?.lastPeriodStart,

      cycleLength: workoutData.user.menstrual?.cycleLength

    }

  };



  /* GAS removed */ (function(){})( {

    method: 'POST',

    headers: { 'Content-Type': 'application/json' },

    body: JSON.stringify(payload)

  })

  

    // Optional: if competition date was set (not in current UI), you could handle it here

    // For now, it's omitted.

}



async function resetAllData() {

    // ========== 0. CHECK FOR ACTIVE WORKOUT WITH UNSAVED PROGRESS ==========

    const hasUnsaved = currentWorkout && currentWorkout.exercises && currentWorkout.exercises.some(ex => !ex.actual && !ex.skipped);

    if (hasUnsaved) {

        const unsavedWarning = await Swal.fire({

            title: 'Unsaved Workout Detected',

            text: 'You have unsaved progress in your current workout. Resetting will lose it. Continue?',

            icon: 'warning',

            showCancelButton: true,

            confirmButtonColor: '#d33',

            confirmButtonText: 'Yes, discard and reset',

            cancelButtonText: 'Cancel'

        });

        if (!unsavedWarning.isConfirmed) return;

    }



    // ========== 1. SELECTIVE RESET OPTIONS ==========

    const { value: resetScope } = await Swal.fire({

        title: 'Select Reset Scope',

        text: 'What do you want to reset?',

        icon: 'warning',

        input: 'select',

        inputOptions: {

            'all': 'ðŸ”´ Everything (all data, workouts, settings)',

            'workouts_only': 'ðŸ“‹ Workouts & history only (keep settings)',

            'settings_only': 'âš™ï¸ Settings only (keep workouts)'

        },

        inputPlaceholder: 'Choose an option',

        showCancelButton: true,

        confirmButtonText: 'Next'

    });

    

    if (!resetScope) return;



    // ========== 2. TEXT CONFIRMATION (type "RESET") ==========

    const { value: typedText } = await Swal.fire({

        title: 'âš ï¸ DANGER ZONE',

        html: `To confirm reset, type <strong>RESET</strong> in the box below.<br><br>

               This action cannot be undone.`,

        icon: 'error',

        input: 'text',

        inputPlaceholder: 'Type RESET here',

        showCancelButton: true,

        confirmButtonText: 'Confirm Reset',

        cancelButtonText: 'Cancel',

        preConfirm: (input) => {

            if (input !== 'RESET') {

                Swal.showValidationMessage('You must type "RESET" exactly');

                return false;

            }

            return true;

        }

    });

    

    if (!typedText) return;



    // ========== 3. BACKUP OPTION ==========

    const { value: backupChoice } = await Swal.fire({

        title: 'Create Backup?',

        text: 'Would you like to download a backup before resetting?',

        icon: 'question',

        showDenyButton: true,

        confirmButtonText: 'Yes, backup first',

        denyButtonText: 'No, skip backup',

        cancelButtonText: 'Cancel reset'

    });

    

    if (backupChoice === undefined) return; // cancelled

    

    let backupFileName = null;

    if (backupChoice === true) {

        // Generate backup

        let firstName = (workoutData.user.name || "User").trim().split(' ')[0];

        if (firstName === "") firstName = "User";

        const now = new Date();

        const month = now.toLocaleString('default', { month: 'long' });

        const day = now.getDate();

        const year = now.getFullYear();

        backupFileName = `backup_${firstName}_${month}_${day}_${year}.json`;

        

        const exportData = {

            ...workoutData,

            exportDeviceId: getDeviceId(),

            exportTimestamp: now.toISOString(),

            backupNote: 'Created before reset'

        };

        const dataStr = JSON.stringify(exportData, null, 2);

        const blob = new Blob([dataStr], { type: 'application/json' });

        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');

        a.href = url;

        a.download = backupFileName;

        document.body.appendChild(a);

        a.click();

        document.body.removeChild(a);

        URL.revokeObjectURL(url);

        

        await Swal.fire({

            title: 'Backup Downloaded',

            text: `Backup saved as ${backupFileName}. Reset will now continue.`,

            icon: 'success',

            timer: 1500,

            showConfirmButton: false

        });

    }



    // ========== 4. FINAL WARNING (with undo window) ==========

    const finalWarning = await Swal.fire({

        title: 'âš ï¸ FINAL WARNING',

        html: `You are about to reset <strong>${resetScope === 'all' ? 'ALL DATA' : (resetScope === 'workouts_only' ? 'WORKOUT HISTORY' : 'SETTINGS')}</strong>.<br><br>

               This action cannot be undone. Click "Erase" to proceed.`,

        icon: 'error',

        showCancelButton: true,

        confirmButtonColor: '#d33',

        confirmButtonText: 'Erase',

        cancelButtonText: 'Cancel'

    });

    

    if (!finalWarning.isConfirmed) return;



    // Show loading

    showLoading(true);

    console.log('[Reset] Starting reset with scope:', resetScope);



    // ========== 5. PRESERVE SPECIFIC SETTINGS (if not resetting everything) ==========

    let preservedTheme = null;

    let preservedDeviceId = null;

    let preservedWakeLock = null;

    

    if (resetScope !== 'all') {

        preservedTheme = localStorage.getItem('workoutTheme') || 'blue';

        preservedDeviceId = localStorage.getItem('deviceId');

        preservedWakeLock = document.getElementById('screenWakeLockToggle')?.checked === true;

    }



    // ========== 6. PERFORM RESET BASED ON SCOPE ==========

    try {

        if (resetScope === 'all' || resetScope === 'workouts_only') {

            // Clear workouts and exercises from IndexedDB

            await db.workouts.clear();

            await db.exercises.clear();

            await db.savedWorkout.clear();

            console.log('[Reset] IndexedDB workouts/exercises cleared');

            

            // Verify read-back

            const verifyWorkouts = await db.workouts.count();

            const verifyExercises = await db.exercises.count();

            if (verifyWorkouts !== 0 || verifyExercises !== 0) {

                throw new Error('Verification failed â€“ some data remains');

            }

            console.log('[Reset] Read-back verification passed');

            

            // Reset in-memory workout data

            workoutData.workouts = [];

            workoutData.exercises = {};

            workoutData.muscleFatigue = {};

            workoutData.muscleRecentExercises = {};

            workoutData.couplingMatrix = {};

            currentWorkout = null;

            savedWorkout = null;

        }

        

        if (resetScope === 'all' || resetScope === 'settings_only') {

            // Clear user data from IndexedDB

            await db.user.clear();

            console.log('[Reset] User data cleared');

            

            // Reset in-memory user to defaults

            workoutData.user = {

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

                    theme: preservedTheme || "blue",

                    darkMode: false,

                    muscleCoupling: false,

                    bottomNavAutoHide: false

                },

                agingRisks: [],

                menstrual: { lastPeriodStart: null, cycleLength: 28, symptoms: [] },

                strengthComposite: 0,

                bodyWeightHistory: [],

                geneticPotential: { maxLeanMass: 0, sGenetic: 0 },

                neuralPhaseEnd: null,

                aggregates: { totalVolumeLast7Days: 0, avgRPELast7Days: 0, volumeByMuscleLast60Days: {} }

            };

            

            // Clear localStorage (except preserved items)

            if (resetScope === 'all') {

                localStorage.clear();

            } else {

                // Settings-only: clear only app keys, keep deviceId and theme

                const keysToKeep = ['deviceId', 'workoutTheme', 'workoutDarkMode'];

                for (let i = 0; i < localStorage.length; i++) {

                    const key = localStorage.key(i);

                    if (!keysToKeep.includes(key)) {

                        localStorage.removeItem(key);

                    }

                }

            }

            

            // Restore preserved values

            if (preservedDeviceId && resetScope === 'settings_only') {

                localStorage.setItem('deviceId', preservedDeviceId);

            }

            if (preservedTheme) {

                localStorage.setItem('workoutTheme', preservedTheme);

                setTheme(preservedTheme);

            }

        }

    } catch (err) {

        console.error('[Reset] Error during reset:', err);

        showLoading(false);

        await Swal.fire({

            title: 'Reset Failed',

            text: 'An error occurred during reset. Some data may remain. Please try again.',

            icon: 'error',

            confirmButtonText: 'OK'

        });

        return;

    }



    // ========== 7. REINITIALIZE TRACKING STRUCTURES ==========

    if (typeof initializeMuscleTracking === 'function') initializeMuscleTracking();

    if (typeof initializeMuscleFatigue === 'function') initializeMuscleFatigue();

    

    // ========== 8. UNDO WINDOW (5 seconds to cancel reload) ==========

    if (resetScope !== 'settings_only') {

        const undoButton = await Swal.fire({

            title: 'Reset Complete',

            html: `The reset has been performed.<br><br>

                   <strong>You have 5 seconds to cancel the page reload</strong> (your data is already gone, but you can stay on this page).`,

            icon: 'success',

            timer: 5000,

            timerProgressBar: true,

            showConfirmButton: true,

            confirmButtonText: 'Stay on this page',

            showCancelButton: true,

            cancelButtonText: 'Reload now'

        });

        

        if (undoButton.dismiss === Swal.DismissReason.timer) {

            // User did nothing â€“ reload

            location.reload();

        } else if (undoButton.isConfirmed) {

            // User chose to stay â€“ just update UI

            showLoading(false);

            await Swal.fire({

                title: 'Reset Applied',

                text: 'Data has been reset. You can continue using the app.',

                icon: 'info',

                confirmButtonText: 'OK'

            });

            // Refresh current section

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

        } else {

            // User clicked "Reload now"

            location.reload();

        }

    } else {

        // Settings-only: no reload needed, just update UI

        showLoading(false);

        await Swal.fire({

            title: 'Settings Reset',

            text: 'Your settings have been reset to defaults.',

            icon: 'success',

            confirmButtonText: 'OK'

        });

        if (typeof loadSettingsForm === 'function') loadSettingsForm();

        if (typeof updateNavigation === 'function') updateNavigation();

    }

    

    console.log('[Reset] Reset process finished');

}

async function loadData(data) {

    // ========== 1. WIPE DEXIE (IndexedDB) ==========

    try {

        await db.workouts.clear();

        await db.exercises.clear();

        await db.user.clear();

        await db.savedWorkout.clear();

    } catch (e) {

        console.warn('[loadData] Dexie clear error:', e);

    }



    // ========== 2. WIPE ALL LOCALSTORAGE ==========

    localStorage.clear();  // removes EVERYTHING (deviceId, theme, backups, etc.)



    // ========== 3. RESET GLOBAL IN-MEMORY STATE ==========

    // Fresh default structure (no leftover fields)

    workoutData.user = {

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

            muscleCoupling: false,

            bottomNavAutoHide: false,

            expressMode: false,    // new

            aggression: 1.0        // new (range 0.5â€“2.0)

        },

        agingRisks: [],

        menstrual: { lastPeriodStart: null, cycleLength: 28, symptoms: [] },

        strengthComposite: 0,

        bodyWeightHistory: [],

        geneticPotential: { maxLeanMass: 0, sGenetic: 0 },

        neuralPhaseEnd: null,

        aggregates: { totalVolumeLast7Days: 0, avgRPELast7Days: 0, volumeByMuscleLast60Days: {} }

    };

    workoutData.workouts = [];

    workoutData.exercises = {};

    workoutData.muscleFatigue = {};

    workoutData.muscleRecentExercises = {};

    workoutData.lastFatigueDecay = new Date().toISOString();

    workoutData.syncedToBackend = false;

    workoutData.couplingMatrix = {};



    // ========== 4. OVERWRITE WITH IMPORTED DATA ==========

    if (data.user) {

        // Deep merge to preserve any nested structures

        workoutData.user = { ...workoutData.user, ...data.user };

        if (data.user.settings) {

            workoutData.user.settings = { ...workoutData.user.settings, ...data.user.settings };

        }

      updateHeaderNameAndStreak();

    }

    if (data.workouts) workoutData.workouts = data.workouts;

    if (data.exercises) workoutData.exercises = data.exercises;

    if (data.muscleFatigue) workoutData.muscleFatigue = data.muscleFatigue;

    if (data.muscleRecentExercises) workoutData.muscleRecentExercises = data.muscleRecentExercises;

    if (data.lastFatigueDecay) workoutData.lastFatigueDecay = data.lastFatigueDecay;

    if (data.couplingMatrix) workoutData.couplingMatrix = data.couplingMatrix;

    if (data.syncedToBackend !== undefined) workoutData.syncedToBackend = data.syncedToBackend;



    // ========== 5. REBUILD DERIVED DATA ==========

    rebuildFatigueFromHistory();

    calculateMuscleLastTrained();

    backfillLastPerformed();

    recomputeAggregates();

    updateBodyWeightEstimate();



    // ========== 6. SAVE TO DEXIE ==========

    await saveToLocalStorage();  // this will repopulate Dexie with the new data



    // ========== 7. CLEAR STALE IN-MEMORY WORKOUTS ==========

    currentWorkout = null;

    savedWorkout = null;

    updateResumeButtonVisibility();



    // ========== 8. UPDATE UI ==========

    const welcomeName = document.getElementById('welcomeName');

    if (welcomeName) welcomeName.textContent = workoutData.user.name || 'User';



    const navUserName = document.getElementById('navUserName');

    if (navUserName) navUserName.textContent = workoutData.user.name || 'User';



    updateNavigation();

    updateDashboard();

    updateLongevityScoreDisplay();



    // Force workout section to show empty state

    if (document.getElementById('workout-section').classList.contains('active')) {

        updateWorkoutHeader();

        const container = document.getElementById('exerciseList');

        if (container) container.innerHTML = '';

    }



    // Re-apply theme from imported data (or default to blue)

    const theme = workoutData.user.settings?.theme || 'blue';

    setTheme(theme);

    if (workoutData.user.settings?.darkMode) {

        document.documentElement.setAttribute('data-theme', 'dark');

        const darkToggle = document.getElementById('darkModeToggle');

        if (darkToggle) darkToggle.checked = true;

    }



    showNotification('Data imported successfully!', 'success');

}

async function attemptRecovery(oldDeviceId, newDeviceId, importedData) {

    const payload = {

        dataType: 'recover',

        oldDeviceId: oldDeviceId,

        newDeviceId: newDeviceId,

        timestamp: new Date().toISOString(),

        name: importedData.user?.name || 'User',

        data: { profile: importedData.user ? { name: importedData.user.name, birthDate: importedData.user.birthDate } : null }

    };

    try {

        await /* GAS removed */ (function(){})( {

            method: 'POST',

            mode: 'no-cors',

            headers: { 'Content-Type': 'text/plain' },

            body: JSON.stringify(payload)

        });

        showNotification('Recovery attempted. Reloading...', 'info');

        setTimeout(() => location.reload(), 2000);

    } catch (err) {

        showNotification('Recovery failed. Importing as new user.', 'error');

        loadData(importedData);

    }

}

    // LONGEVITY FUNCTIONS

function calculateLongevityScore() {

