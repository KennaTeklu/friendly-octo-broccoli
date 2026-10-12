// ============================================================
// reference/scripts/app/11-exercise-card.js
// Extracted from reference/scripts/app.js
// Original lines: 60588-64705  (4118 lines)
// Section: Exercise card HTML build + test panel listeners
// ============================================================

    // ========== INFINITE NAVIGATION STACK (never loses history) ==========

    if (!replaceState) {

        navigationStack.push(sectionId);

        // Cap at 1000 (more than enough â€“ never "crowded")

        if (navigationStack.length > 1000) navigationStack = navigationStack.slice(-1000);

        localStorage.setItem('workout_navigation_stack', JSON.stringify(navigationStack));

        

        const state = { stack: navigationStack, section: sectionId, ts: Date.now() };

        try { window.history.pushState(state, '', `#${sectionId}`); } catch(e) {}

        // Double trap: push an extra guard state immediately

        setTimeout(() => {

            try { window.history.pushState({ ...state, guard: true }, '', `#${sectionId}`); } catch(e) {}

        }, 10);

    } else {

        // replaceState mode (used for internal back navigation)

        if (navigationStack.length === 0) navigationStack.push(sectionId);

        else navigationStack[navigationStack.length - 1] = sectionId;

        localStorage.setItem('workout_navigation_stack', JSON.stringify(navigationStack));

        

        const state = { stack: navigationStack, section: sectionId, ts: Date.now() };

        try { window.history.replaceState(state, '', `#${sectionId}`); } catch(e) {}

        // Also push a guard after replace to maintain trap

        setTimeout(() => {

            window.history.pushState({ ...state, guard: true }, '', `#${sectionId}`);

        }, 10);

    }

}

// If in sandboxed iframe, replace showSection with a version that doesn't use history

if (window.__INSANDBOX) {

    const originalShowSection = showSection;

    window.showSection = function(sectionId, replaceState = false) {

        document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));

        const target = document.getElementById(`${sectionId}-section`);

        if (target) target.classList.add('active');

        document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));

        let navItem = sectionId === 'dashboard' 

            ? document.querySelector('.nav-item[onclick*="dashboard"]')

            : document.querySelector(`.nav-item[onclick*="${sectionId}"]`);

        if (navItem) navItem.classList.add('active');

        document.querySelectorAll('.bottom-nav-item').forEach(item => {

            item.classList.remove('active');

            if (item.getAttribute('onclick')?.includes(sectionId)) item.classList.add('active');

        });

        if (sectionId === 'history') {

            document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));

            const allTab = document.querySelector('.tab[onclick*="all"]');

            if (allTab) allTab.classList.add('active');

        }

        if (window.innerWidth <= 1100 && typeof navMenu !== 'undefined') navMenu.classList.remove('active');

        switch(sectionId) {

            case 'dashboard': if (typeof updateDashboard === 'function') updateDashboard(); if (typeof updateLongevityScoreDisplay === 'function') updateLongevityScoreDisplay(); break;

            case 'workout': if (typeof updateTodaysWorkout === 'function') updateTodaysWorkout(); if (typeof updateWorkoutHeader === 'function') updateWorkoutHeader(); break;

            case 'library': if (typeof renderLibrary === 'function') renderLibrary(); break;

            case 'history': currentHistoryFilter = 'all'; if (typeof updateWorkoutHistory === 'function') updateWorkoutHistory(); break;

            case 'progress': if (typeof updateProgressCharts === 'function') updateProgressCharts(); break;

            case 'recovery': if (typeof updateRecoverySection === 'function') updateRecoverySection(); break;

            case 'settings': if (typeof loadSettingsForm === 'function') loadSettingsForm(); break;

        }

        sessionStorage.setItem('lastSection', sectionId);

        setTimeout(() => {

            if (target) {

                const offset = typeof getTopOffset === 'function' ? getTopOffset() : 80;

                const elementPosition = target.getBoundingClientRect().top + window.pageYOffset;

                window.scrollTo({ top: Math.max(0, elementPosition - offset), behavior: 'smooth' });

            }

        }, 50);

    };

}

    function toggleMobileMenu() {

        navMenu.classList.toggle('active');

    }



function updateNavigation() {

    // Safely get user name

    const userName = (workoutData.user && workoutData.user.name) ? workoutData.user.name : 'User';

    

    // Update nav user name (top right)

    const navUserName = document.getElementById('navUserName');

    if (navUserName) navUserName.textContent = userName;

    

    // Update avatar (first letter)

    const navUserAvatar = document.getElementById('navUserAvatar');

    if (navUserAvatar) navUserAvatar.textContent = userName.charAt(0).toUpperCase();

    

    // Experience level

    const experience = (workoutData.user && workoutData.user.experience) ? workoutData.user.experience : 'beginner';

    const expDisplay = experience.charAt(0).toUpperCase() + experience.slice(1);

    

    // Streak

    let streak = 0;

    try {

        streak = calculateStreak();

    } catch(e) {

        console.warn("calculateStreak failed", e);

    }

    

    // Update nav user stats

    const navUserStats = document.getElementById('navUserStats');

    if (navUserStats) navUserStats.textContent = `${expDisplay} â€¢ ${streak} Day Streak`;

    

    // Update optional welcome elements (if they still exist in DOM)

    const welcomeStreak = document.getElementById('welcomeStreak');

    if (welcomeStreak) welcomeStreak.textContent = `${streak} day${streak === 1 ? '' : 's'}`;

    

    const welcomeLevel = document.getElementById('welcomeLevel');

    if (welcomeLevel) welcomeLevel.textContent = expDisplay;

    

    // IMPORTANT: Update header brand with first name + streak

    updateHeaderNameAndStreak();

}

function updateHeaderNameAndStreak() {



    // --- Helper to safely update DOM element only if value changed ---

    function safeTextUpdate(elementId, newValue) {

        const el = document.getElementById(elementId);

        if (!el) return;

        const current = el.textContent;

        if (current !== String(newValue)) {

            el.textContent = newValue;

        }

    }



    // 1. STREAK: On error, keep existing displayed value

    let streak = null;

    try {

        const calculated = calculateStreak();

        if (typeof calculated === 'number' && !isNaN(calculated)) {

            streak = calculated;

        }

    } catch (e) {

        console.warn("Streak calculation failed:", e);

    }



    if (streak !== null) {

        safeTextUpdate('streakCount', streak);

        if (typeof updateStreakFire === 'function') updateStreakFire(streak);

    } else {

        // If we couldn't compute a new streak, do NOT overwrite with 0.

        // Keep whatever is already displayed (if any).

        console.log("Streak update skipped â€“ using existing UI value.");

    }



    // 2. NAME: Alphanumeric check + emoji / symbol filter

    // P5P12: the app talks to YOU â€” your name everywhere, neutral "Athlete" if unnamed

    let firstName = "Athlete";

    try {

        const userName = workoutData?.user?.name;

        if (typeof userName === 'string' && userName.trim().length > 0) {

            const trimmed = userName.trim();

            // Must contain at least one letter or digit to be considered a "real name"

            if (/[a-zA-Z0-9]/.test(trimmed)) {

                firstName = trimmed.split(' ')[0];

            } else {

                // Name is only emojis / symbols â€“ keep the neutral default

                console.log("Name contains no alphanumeric characters, using default 'Athlete'.");

            }

        }

    } catch (e) {

        console.warn("Failed to extract user name:", e);

    }



    safeTextUpdate('userNameDisplay', firstName);



    // P5P12: personalize the tab title and avatar initial

    try {

        const named = firstName !== 'Athlete';

        document.title = named ? `${firstName}'s Fitness` : 'My Fitness';

        const avatarEl = document.getElementById('navUserAvatar');

        if (avatarEl && named) avatarEl.textContent = firstName.charAt(0).toUpperCase();

    } catch (e) {}

}



    // IMPORT FUNCTIONS

    function importWorkoutData() {

        showModal('importModal');

    }



    function importExerciseData() {

        showModal('importExerciseModal');

    }



async function startNewProgram() {

    const result = await Swal.fire({

        title: 'âš ï¸ Start a Fresh Program?',

        html: 'All current data will be lost. This action cannot be undone.<br><br>Do you want to continue?',

        icon: 'warning',

        showCancelButton: true,

        confirmButtonColor: '#d33',

        cancelButtonColor: '#3085d6',

        confirmButtonText: 'Yes, start fresh',

        cancelButtonText: 'Cancel'

    });

    

    if (!result.isConfirmed) {

        return;

    }

    

    const emptyData = {

        user: {

            name: "",

            birthDate: "",

            gender: "",

            weight: 0,

            height: 0,

            experience: "",

            goal: "balanced",

            created: new Date().toISOString(),

            settings: {

                workoutDays: [],

                restTime: 60,

                progressionRate: 0.02,

                theme: "blue",

                darkMode: false

            },

            agingRisks: []

        },

        workouts: [],

        exercises: {}

    };

    loadData(emptyData);

    clearSavedWorkout();

    showMainApp();

    showSection('settings');

    updateNavigation();

    showNotification("Welcome! Let's set up your profile to get started.");

    showLoading(false);

}



async function loadSampleData() {

    const result = await Swal.fire({

        title: 'âš ï¸ Load Sample Data?',

        html: 'This will replace all your current data. This action cannot be undone.<br><br>Do you want to continue?',

        icon: 'warning',

        showCancelButton: true,

        confirmButtonColor: '#d33',

        cancelButtonColor: '#3085d6',

        confirmButtonText: 'Yes, load sample',

        cancelButtonText: 'Cancel'

    });

    

    if (!result.isConfirmed) {

        return;

    }

    

    showLoading(true);

    const sampleData = {

        user: {

            name: "Ms Berecha",

            birthDate: "1990-05-15",

            gender: "female",

            weight: 185,

            height: 70,

            experience: "intermediate",

            goal: "balanced",

            created: new Date().toISOString(),

            settings: {

                workoutDays: ["monday", "wednesday", "friday", "saturday"],

                restTime: 90,

                progressionRate: 0.02,

                theme: "blue",

                darkMode: false

            },

            agingRisks: [

                {

                    factor: "Neck weakness",

                    severity: "Medium",

                    recommendation: "Add neck strengthening 2x/week",

                    exercises: ["neck_isometric", "chin_tucks"]

                }

            ]

        },

        workouts: [

            {

                id: "workout_1",

                date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),

                type: "full_body_a",

                name: "Full Body A",

                exercises: [

                    { 

                        id: "squat", 

                        name: "Barbell Squat",

                        muscleGroup: ["quads", "glutes"],

                        prescribed: { sets: 3, reps: "8-10", weight: 185 },

                        actual: { sets: 3, reps: [8, 8, 7], weight: 185, rpe: 8 },

                        notes: "Felt strong"

                    },

                    { 

                        id: "bench_press", 

                        name: "Barbell Bench Press",

                        muscleGroup: ["chest", "triceps"],

                        prescribed: { sets: 3, reps: "8-10", weight: 155 },

                        actual: { sets: 3, reps: [9, 8, 8], weight: 155, rpe: 7 }

                    }

                ],

                summary: {

                    totalVolume: 185*23 + 155*25,

                    averageRPE: 7.5,

                    completedExercises: 2

                }

            },

            {

                id: "workout_2",

                date: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),

                type: "full_body_b",

                name: "Full Body B",

                exercises: [

                    { 

                        id: "deadlift", 

                        name: "Deadlift",

                        muscleGroup: ["hamstrings", "back", "glutes"],

                        prescribed: { sets: 3, reps: "6-8", weight: 225 },

                        actual: { sets: 3, reps: [6, 6, 5], weight: 225, rpe: 9 }

                    }

                ],

                summary: {

                    totalVolume: 225*17,

                    averageRPE: 9,

                    completedExercises: 1

                }

            },

            {

                id: "workout_3",

                date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),

                type: "longevity_day",

                name: "Longevity & Joint Health",

                exercises: [

                    { 

                        id: "neck_isometric", 

                        name: "Neck Isometric Holds",

                        muscleGroup: ["neck", "deep_neck"],

                        prescribed: { sets: 3, reps: "10-15", weight: 0 },

                        actual: { sets: 3, reps: [15, 15, 15], weight: 0, rpe: 5 },

                        notes: "Good for posture"

                    },

                    { 

                        id: "short_foot", 

                        name: "Short Foot Exercise",

                        muscleGroup: ["foot_intrinsics"],

                        prescribed: { sets: 3, reps: "15-20", weight: 0 },

                        actual: { sets: 3, reps: [20, 20, 20], weight: 0, rpe: 4 }

                    }

                ],

                summary: {

                    totalVolume: 0,

                    averageRPE: 4.5,

                    completedExercises: 2

                }

            }

        ],

        exercises: {

            "squat": {

                bestWeight: 185,

                bestReps: 8,

                lastTrained: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),

                progression: 0.02,

                history: [

                    {

                        date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),

                        weight: 185,

                        sets: 3,

                        reps: [8, 8, 7],

                        rpe: 8,

                        volume: 185 * 23

                    }

                ]

            },

            "bench_press": {

                bestWeight: 155,

                bestReps: 9,

                lastTrained: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),

                progression: 0.02,

                history: [

                    {

                        date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),

                        weight: 155,

                        sets: 3,

                        reps: [9, 8, 8],

                        rpe: 7,

                        volume: 155 * 25

                    }

                ]

            },

            "deadlift": {

                bestWeight: 225,

                bestReps: 6,

                lastTrained: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),

                progression: 0.02,

                history: [

                    {

                        date: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),

                        weight: 225,

                        sets: 3,

                        reps: [6, 6, 5],

                        rpe: 9,

                        volume: 225 * 17

                    }

                ]

            },

            "neck_isometric": {

                bestWeight: 0,

                bestReps: 15,

                lastTrained: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),

                progression: 0.01,

                history: [

                    {

                        date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),

                        weight: 0,

                        sets: 3,

                        reps: [15, 15, 15],

                        rpe: 5,

                        volume: 0

                    }

                ]

            },

            "short_foot": {

                bestWeight: 0,

                bestReps: 20,

                lastTrained: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),

                progression: 0.01,

                history: [

                    {

                        date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),

                        weight: 0,

                        sets: 3,

                        reps: [20, 20, 20],

                        rpe: 4,

                        volume: 0

                    }

                ]

            }

        }

    };

    setTimeout(() => {

        loadData(sampleData);

        clearSavedWorkout();

        showMainApp();

        showLoading(false);

        showNotification("Sample data loaded successfully!");

        updateNavigation();

        updateLongevityScoreDisplay();

        showSection('workout');

    }, 1000);

}



async function processImport() {

    // 1. Close the import modal first

    closeModal('importModal');



    // 2. Simple confirmation (no extra options)

    const { isConfirmed } = await Swal.fire({

        title: 'Import Data',

        text: 'This will replace all your current data. Continue?',

        icon: 'warning',

        showCancelButton: true,

        confirmButtonColor: '#d33',

        confirmButtonText: 'Yes, overwrite',

        cancelButtonText: 'Cancel'

    });

    if (!isConfirmed) return;



    const fileInput = document.getElementById('workoutFile');

    const jsonInput = document.getElementById('workoutJson').value.trim();

    showLoading(true);



    try {

        let rawData = null;



        // Get data from file or textarea

        if (fileInput.files.length > 0) {

            const file = fileInput.files[0];

            rawData = await file.text();

        } else if (jsonInput) {

            rawData = jsonInput;

        } else {

            throw new Error('No file or JSON data provided.');

        }



        // Parse JSON

        let importData;

        try {

            importData = JSON.parse(rawData);

        } catch (e) {

            throw new Error(`Invalid JSON: ${e.message}`);

        }



        // Basic validation (optional but helpful)

        if (!importData || typeof importData !== 'object') {

            throw new Error('Imported data must be a JSON object.');

        }



        // Use your existing loadData function (clears everything and loads new data)

        loadData(importData);



        // Clear the form fields for next time

        fileInput.value = '';

        document.getElementById('workoutJson').value = '';



        // Refresh UI

        clearSavedWorkout();

        showMainApp();

        showNotification('Data imported successfully!');

        updateNavigation();

        updateDashboard();

        updateLongevityScoreDisplay();



        const activeSection = document.querySelector('.section.active');

        if (activeSection && activeSection.id === 'workout-section' && currentWorkout) {

            updateTodaysWorkout();

        }

    } catch (err) {

        console.error('Import error:', err);

        await Swal.fire({

            title: 'Import Failed',

            text: err.message,

            icon: 'error',

            confirmButtonText: 'OK'

        });

    } finally {

        showLoading(false);

    }

}



async function processExerciseImport() {

    const fileInput = document.getElementById('exerciseFile');

    const jsonInput = document.getElementById('exerciseJson').value;

    showLoading(true);

    

    if (fileInput.files.length > 0) {

        const file = fileInput.files[0];

        const reader = new FileReader();

        reader.onload = async function(e) {

            try {

                const data = JSON.parse(e.target.result);

                importExerciseLibrary(data);

                showLoading(false);

                showNotification("Exercise library imported successfully!");

                closeModal('importExerciseModal');

            } catch (error) {

                showLoading(false);

                await Swal.fire({

                    title: 'Error parsing JSON file',

                    text: error.message,

                    icon: 'error',

                    confirmButtonText: 'OK'

                });

            }

        };

        reader.readAsText(file);

    } else if (jsonInput) {

        try {

            const data = JSON.parse(jsonInput);

            importExerciseLibrary(data);

            showLoading(false);

            showNotification("Exercise library imported successfully!");

            closeModal('importExerciseModal');

        } catch (error) {

            showLoading(false);

            await Swal.fire({

                title: 'Error parsing JSON',

                text: error.message,

                icon: 'error',

                confirmButtonText: 'OK'

            });

        }

    } else {

        showLoading(false);

        await Swal.fire({

            title: 'No data provided',

            text: 'Please upload a file or paste JSON data',

            icon: 'warning',

            confirmButtonText: 'OK'

        });

    }

}



    function importExerciseLibrary(data) {

        Object.assign(workoutProgram.exercises, data);

        showNotification(`Imported ${Object.keys(data).length} exercises`);

        saveToLocalStorage();

    }



    function exportExerciseData() {

        const dataStr = JSON.stringify(workoutProgram.exercises, null, 2);

        const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);

        const exportFileDefaultName = `exercise-library-${new Date().toISOString().split('T')[0]}.json`;

        const linkElement = document.createElement('a');

        linkElement.setAttribute('href', dataUri);

        linkElement.setAttribute('download', exportFileDefaultName);

        linkElement.click();

        showNotification("Exercise library exported successfully!");

    }



async function resetExerciseLibrary() {

    const result = await Swal.fire({

        title: 'Reset Exercise Library?',

        text: 'This will remove any custom exercises you have imported and restore the default library.',

        icon: 'warning',

        showCancelButton: true,

        confirmButtonColor: '#d33',

        cancelButtonColor: '#3085d6',

        confirmButtonText: 'Yes, reset',

        cancelButtonText: 'Cancel'

    });

    

    if (result.isConfirmed) {

        // TODO: Implement actual reset logic (reload defaults)

        showNotification("Exercise reset functionality would reload defaults here", "info");

    }

}



function rebuildFatigueFromHistory() {

    // Reset fatigue and recent exercises

    workoutData.muscleFatigue = {};

    workoutData.muscleRecentExercises = {};



    // Initialize fatigue for all muscles with object (fast/slow)

    const allMuscles = getAllMuscleGroups();

    allMuscles.forEach(muscle => {

        workoutData.muscleFatigue[muscle.name] = { fast: 0, slow: 0, lastUpdate: null };

    });



    // Sort workouts chronologically (oldest first)

    const sortedWorkouts = [...workoutData.workouts].sort((a, b) => new Date(a.date) - new Date(b.date));



    let previousDate = null;



    sortedWorkouts.forEach(workout => {

        const workoutDate = new Date(workout.date);



        // Apply decay since previous workout (if any)

        if (previousDate) {

            const daysDiff = (workoutDate - previousDate) / (1000 * 60 * 60 * 24);

            if (daysDiff > 0) {

                decayFatigue(daysDiff);

            }

        }



        // Process this workout

        updateFatigueAfterWorkout(workout);

        updateRecentExercises(workout);



        previousDate = workoutDate;

    });



    // Apply decay from last workout to now

    if (previousDate) {

        const now = new Date();

        const daysSinceLast = (now - previousDate) / (1000 * 60 * 60 * 24);

        if (daysSinceLast > 0) {

            decayFatigue(daysSinceLast);

        }

    }



    // Update the last decay timestamp

    workoutData.lastFatigueDecay = new Date().toISOString();

}

    function estimate1RM(weight, reps) {

        if (!weight || weight <= 0) return 0; // bodyweight or no data

        // Epley formula: 1RM = weight * (1 + reps/30)

        return weight * (1 + reps / 30);

    }

      

    function calculateAge(birthDateStr) {

        if (!birthDateStr) return null;

        const birthDate = new Date(birthDateStr);

        const today = new Date();

        let age = today.getFullYear() - birthDate.getFullYear();

        const m = today.getMonth() - birthDate.getMonth();

        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) age--;

        return age;

    }

      

function calculateRecommendedRestDays(workout) {

    if (!workout || !workout.summary) return 1; // default 1 day



    const user = workoutData.user;

    const userWeight = user.weight || 150; // lbs

    const age = user.birthDate ? calculateAge(user.birthDate) : 30;

    const gender = user.gender || 'male';



    // Volume is in lbs Ã— total reps (from calculateTotalVolume)

    const volume = workout.summary.totalVolume || 0;



    // Heuristic: every 5000 lbs of volume adds 0.5 days of rest, up to +3 days

    let volumeFactor = (volume / 5000) * 0.5;

    volumeFactor = Math.min(volumeFactor, 3);



    // Age factor: older = more rest

    let ageFactor = 0;

    if (age > 50) ageFactor = 1.5;

    else if (age > 40) ageFactor = 1.0;

    else if (age > 30) ageFactor = 0.5;



    // Gender factor (optional â€“ you can remove if not desired)

    let genderFactor = (gender === 'female') ? 0.3 : 0;



    // Base 1 day + factors

    let restDays = 1 + volumeFactor + ageFactor + genderFactor;

    restDays = Math.max(1, Math.min(restDays, 5)); // clamp between 1 and 5 days



    return Math.round(restDays * 2) / 2; // round to nearest 0.5

}



    // DASHBOARD FUNCTIONS

    function updateDashboard() {

        document.getElementById('statWorkouts').textContent = workoutData.workouts.length;

        const streak = calculateStreak();

        document.getElementById('statStreak').textContent = streak;

        const totalVolume = workoutData.workouts.reduce((total, workout) => {

            return total + (workout.summary?.totalVolume || 0);

        }, 0);

        document.getElementById('statVolume').textContent = formatNumber(totalVolume);



        // New progress calculation based on strength gains

        const progress = calculateOverallStrengthProgress();

        document.getElementById('statProgress').textContent = `${progress}%`;



        updateDashboardWorkoutPreview();

        updateDashboardChart();

        updateNextWorkoutDate();

        updatePeriodButtonVisibility();

        checkAndRemindReTest();

        // Update recovery data for the readiness gauge

        updateRecoverySection();

        updateReadinessGauge();



        // Phase badge for female users

        const phase = getCurrentCyclePhase();

        if (phase && workoutData.user.gender === 'female') {

            const welcomeDiv = document.getElementById('userWelcome');

            if (welcomeDiv && !document.getElementById('phaseBadge')) {

                let phaseText = '';

                let phaseColor = '';

                switch(phase) {

                    case 'menstrual': phaseText = 'Menstrual Phase â€“ Rest & Recover'; phaseColor = 'var(--danger)'; break;

                    case 'follicular': phaseText = 'Follicular Phase â€“ Go Hard!'; phaseColor = 'var(--success)'; break;

                    case 'ovulatory': phaseText = 'Ovulatory Phase â€“ Peak Performance'; phaseColor = 'var(--warning)'; break;

                    case 'luteal': phaseText = 'Luteal Phase â€“ Moderate Intensity'; phaseColor = 'var(--warning)'; break;

                }

                const phaseBadge = document.createElement('div');

                phaseBadge.id = 'phaseBadge';

                phaseBadge.className = 'badge';

                phaseBadge.style.backgroundColor = phaseColor;

                phaseBadge.style.color = 'white';

                phaseBadge.style.marginLeft = '10px';

                phaseBadge.textContent = phaseText;

                welcomeDiv.appendChild(phaseBadge);

            }

        }

      

        // Update recommendation card

        const recText = document.getElementById('dashboardRecommendationText');

        if (recText) {

          recText.textContent = getTopRecommendation();

        }

        // Update elite date projection â€“ with error handling

        try {

            const projection = calculateProjectedEliteDate();

            const eliteDateEl = document.getElementById('eliteDate');

            if (eliteDateEl && projection && projection.medianDate instanceof Date && !isNaN(projection.medianDate)) {

                const formatted = projection.medianDate.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });

                eliteDateEl.textContent = formatted;

const card = document.getElementById('eliteDateCard');

if (card) {

    const total = (typeof projection.total1RM === 'number' && !isNaN(projection.total1RM)) ? Math.round(projection.total1RM) : '?';

    const target = (typeof projection.targetTotal === 'number' && !isNaN(projection.targetTotal)) ? projection.targetTotal : '?';

    const formatted = projection.medianDate instanceof Date && !isNaN(projection.medianDate) 

        ? projection.medianDate.toLocaleDateString() 

        : '?';

    

    // Friendly tooltip text (no jargon)

    const tooltipText = `ðŸŒŸ Based on your progress:\nâ€¢ Your current strength total: about ${total} lbs\nâ€¢ To reach a top strength level, you need about ${target} lbs\nâ€¢ If you stay consistent, this could happen around ${formatted}\nâ€¢ ${projection.message || 'Every workout brings you closer!'}`;

    

    card.setAttribute('title', tooltipText);

    

    if (typeof tippy !== 'undefined') {

        if (card._tippy) card._tippy.destroy();

        tippy(card, { content: tooltipText, allowHTML: true, theme: 'light-border' });

    }

}

            } else {

                // Fallback: show a friendly placeholder

                if (eliteDateEl) eliteDateEl.textContent = 'â€”';

                console.warn('Invalid elite date projection:', projection);

            }

        } catch (err) {

            console.error('Error updating elite date:', err);

            const eliteDateEl = document.getElementById('eliteDate');

            if (eliteDateEl) eliteDateEl.textContent = 'â€”';

        } 

                    updateStrengthForecast();

    }

function updateStrengthForecast() {

    try {

        const projection = calculateProjectedEliteDate();

        if (!projection || typeof projection.targetTotal !== 'number') {

            console.warn("updateStrengthForecast: invalid projection");

            return;

        }

        const targetTotal = projection.targetTotal;

        const bodyWeight = (workoutData.user.bodyWeightHistory && workoutData.user.bodyWeightHistory.length)

            ? workoutData.user.bodyWeightHistory[workoutData.user.bodyWeightHistory.length - 1].weight

            : (workoutData.user.weight || 150);

        const isFemale = (workoutData.user.gender === 'female');



        let squatRatio = 0.35, benchRatio = 0.25, deadliftRatio = 0.40;

        if (isFemale) {

            squatRatio = 0.32; benchRatio = 0.22; deadliftRatio = 0.46;

        } else if (bodyWeight > 198) {

            squatRatio = 0.33; benchRatio = 0.23; deadliftRatio = 0.44;

        }



        const squat = Math.round(targetTotal * squatRatio);

        const bench = Math.round(targetTotal * benchRatio);

        const deadlift = Math.round(targetTotal * deadliftRatio);



        let wilks = 0;

        if (!isFemale) {

            wilks = targetTotal * (500 / (bodyWeight + 150));

        } else {

            wilks = targetTotal * (400 / (bodyWeight + 100));

        }

        wilks = Math.round(wilks);



        document.getElementById('forecastSquat').textContent = squat;

        document.getElementById('forecastBench').textContent = bench;

        document.getElementById('forecastDeadlift').textContent = deadlift;

        document.getElementById('forecastTotal').textContent = targetTotal;

        document.getElementById('forecastWilks').textContent = wilks;

    } catch (e) {

        console.error("updateStrengthForecast error:", e);

    }

}

     

function calculateOverallStrengthProgress() {

    const allMuscles = getAllMuscleGroups();

    if (allMuscles.length === 0) return 0;



    const userWeight = workoutData.user.weight || 150;



    // Helper: estimate 1RM using Brzycki (reps â‰¤10) or Epley (reps >10)

    function estimate1RMAdvanced(weight, reps) {

        if (!weight || weight <= 0) return 0;

        if (reps <= 10) {

            return weight * 36 / (37 - reps);

        } else {

            return weight * (1 + reps / 30);

        }

    }



    // Helper: determine if an exercise is bodyweight based on name or equipment

    function isBodyweightExercise(exerciseId, exerciseName) {

        const exObj = getExerciseById(exerciseId);

        if (exObj && exObj.equipment === 'bodyweight') return true;

        if (exerciseName && /pull|push|dip|chin|plank|bodyweight|push-?up|pull-?up|squat|jump/i.test(exerciseName)) return true;

        return false;

    }



    // Build muscle â†’ list of exercise IDs that target it

    const muscleToExercises = new Map();

    for (const muscle of allMuscles) {

        muscleToExercises.set(muscle.name, []);

    }



    for (const group in ultimateExerciseLibrary) {

        for (const ex of ultimateExerciseLibrary[group]) {

            const exId = ex.name.toLowerCase().replace(/\s/g, '_');

            if (ex.muscles && ex.muscles.length > 0) {

                for (const muscle of ex.muscles) {

                    if (muscleToExercises.has(muscle)) {

                        muscleToExercises.get(muscle).push({ id: exId, name: ex.name });

                    }

                }

            }

        }

    }



    // For each muscle, collect all history entries from its exercises

    const muscleGains = [];

    const muscleSlopes = []; // optional, for future use



    for (const [muscleName, exercisesList] of muscleToExercises.entries()) {

        // Collect all entries (date, estimated1RM) from all exercises targeting this muscle

        const allEntries = [];



        for (const { id: exId, name: exName } of exercisesList) {

            const exRecord = workoutData.exercises[exId];

            if (!exRecord || !exRecord.history) continue;



            const isBodyweight = isBodyweightExercise(exId, exName);



            for (const entry of exRecord.history) {

                if (entry.skipped) continue;

                if (!entry.reps || entry.reps.length === 0) continue;



                let effectiveWeight = entry.weight;

                if (effectiveWeight === 0 && isBodyweight) {

                    effectiveWeight = userWeight;

                }

                if (!effectiveWeight || effectiveWeight <= 0) continue;



                const bestReps = Math.max(...entry.reps);

                if (bestReps <= 0) continue;



                const estimated1RM = estimate1RMAdvanced(effectiveWeight, bestReps);

                allEntries.push({

                    date: new Date(entry.date).getTime(),

                    estimated1RM: estimated1RM

                });

            }

        }



        if (allEntries.length < 2) continue;



        // Sort by date

        allEntries.sort((a, b) => a.date - b.date);

        const first = allEntries[0].estimated1RM;

        const last = allEntries[allEntries.length - 1].estimated1RM;

        if (first <= 0) continue;



        const gain = (last - first) / first;

        muscleGains.push(gain);



        // Compute slope over last 30 days (optional, for future use)

        const now = Date.now();

        const thirtyDaysAgo = now - 30 * 24 * 60 * 60 * 1000;

        const recentEntries = allEntries.filter(e => e.date >= thirtyDaysAgo);

        if (recentEntries.length >= 2) {

            const times = recentEntries.map(e => e.date);

            const values = recentEntries.map(e => e.estimated1RM);

            const n = times.length;

            const sumX = times.reduce((a, b) => a + b, 0);

            const sumY = values.reduce((a, b) => a + b, 0);

            const sumXY = times.reduce((acc, x, i) => acc + x * values[i], 0);

            const sumX2 = times.reduce((acc, x) => acc + x * x, 0);

            const slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);

            muscleSlopes.push(slope);

        }

    }



    if (muscleGains.length === 0) return 0;



    const avgGain = muscleGains.reduce((a, b) => a + b, 0) / muscleGains.length;

    const progressPercent = Math.min(100, Math.max(0, Math.round(avgGain * 100)));



    // Optionally store average slope for future use (e.g., phase shifts)

    if (muscleSlopes.length > 0) {

        const avgSlope = muscleSlopes.reduce((a, b) => a + b, 0) / muscleSlopes.length;

        // Store globally or in workoutData.user if needed

        window.lastStrengthSlope = avgSlope;

    }



    return progressPercent;

}

      

function updateWorkoutHeader() {

    const container = document.getElementById('workoutMetaContainer');

    if (!container) return;



    if (!currentWorkout || !currentWorkout.exercises || currentWorkout.exercises.length === 0) {

        // Hide workout elements, show no-workout message

        document.getElementById('workoutDetails').classList.add('collapsed');

        document.getElementById('noWorkoutMessage').style.display = 'block';

        document.getElementById('workoutButtons').style.display = 'none';

        return;

    }



    // Show workout elements, hide no-workout message

    document.getElementById('noWorkoutMessage').style.display = 'none';

    document.getElementById('workoutButtons').style.display = 'flex';



    // Build the workout meta HTML based on currentWorkout

    const split = workoutProgram.splits.find(s => s.id === currentWorkout.type) || {};

    const focus = split.focus ? split.focus.map(m => getMuscleDisplayName(m)).join(', ') : 'Full Body';

    const exerciseCount = currentWorkout.exercises.length;

    const estTime = exerciseCount * 5 + 10; // rough estimate



    let intensity = 'Moderate';

    if (currentWorkout.readiness) {

        if (currentWorkout.readiness >= 80) intensity = 'High';

        else if (currentWorkout.readiness >= 60) intensity = 'Moderate-High';

        else if (currentWorkout.readiness >= 40) intensity = 'Moderate';

        else intensity = 'Low';

    }



    // P5P5: the same facts as always-visible chips right under the page title

    const chipsRow = document.getElementById('workoutChips');

    if (chipsRow) {

        let compChip = '';

        // P4: chip reflects the actual exercise distribution, not the split plan

        const _cDist = (typeof mpComputeWorkoutComponentDistribution === 'function')

            ? mpComputeWorkoutComponentDistribution(currentWorkout) : [];

        if (_cDist.length) {

            const top = _cDist[0];

            const c = (typeof MP_COMPONENT_MAP !== 'undefined') ? MP_COMPONENT_MAP[top.id] : null;

            if (c) compChip = `<span class="p4-chip comp" style="background:${c.color}1f; color:${c.color}; border-color:${c.color}44;"><i class="fas ${c.icon}"></i> ${c.name} ${top.pct}%</span>`;

        } else if (split.component) {

            const cidMap = { cardio: 'cardiorespiratory_endurance', agility_reaction: 'agility' };

            const comp = (typeof MP_COMPONENTS !== 'undefined')

                ? MP_COMPONENTS.find(c => c.id === (cidMap[split.component] || split.component)) : null;

            if (comp) compChip = `<span class="p4-chip comp" style="background:${comp.color}1f; color:${comp.color}; border-color:${comp.color}44;"><i class="fas ${comp.icon}"></i> ${comp.name}</span>`;

        }

        chipsRow.innerHTML =

            `<span class="p4-chip"><i class="fas fa-dumbbell"></i> ${currentWorkout.name || split.name || 'Custom'}</span>` +

            (compChip || `<span class="p4-chip"><i class="fas fa-crosshairs"></i> ${focus}</span>`) +

            `<span class="p4-chip"><i class="fas fa-clock"></i> ${estTime}-${estTime + 15} min</span>` +

            `<span class="p4-chip"><i class="fas fa-gauge-high"></i> ${intensity}</span>` +

            (currentWorkout.isDeload ? `<span class="p4-chip" style="border-color:var(--p4-warn); color:var(--p4-warn);"><i class="fas fa-leaf"></i> Deload</span>` : '') +

            (currentWorkout.isTaper ? `<span class="p4-chip" style="border-color:var(--p4-ok); color:var(--p4-ok);"><i class="fas fa-feather"></i> Taper</span>` : '');

    }



    container.innerHTML = `

        <div class="workout-meta">

            <div class="meta-item">

                <span class="meta-label">Workout Type:</span>

                <span class="meta-value">${currentWorkout.name || split.name || 'Custom'}</span>

            </div>

            <div class="meta-item">

                <span class="meta-label">Focus:</span>

                <span class="meta-value">${focus}</span>

            </div>

            <div class="meta-item">

                <span class="meta-label">Estimated Time:</span>

                <span class="meta-value">${estTime}-${estTime + 15} minutes</span>

            </div>

            <div class="meta-item">

                <span class="meta-label">Intensity:</span>

                <span class="meta-value">${intensity}</span>

            </div>

        </div>

        <div class="workout-instructions">

            <p><strong>Instructions:</strong> Complete all exercises in order. Rest 60-90 seconds between sets, 2-3 minutes between exercises. Log your actual performance after each exercise.</p>

        </div>

    `;

}



    function updateDashboardWorkoutPreview() {

        const container = document.getElementById('dashboardWorkoutInfo');

        if (!currentWorkout) {

            container.innerHTML = `

                <div style="text-align: center; padding: 20px;">

                    <i class="fas fa-dumbbell" style="font-size: 3rem; color: var(--gray-400); margin-bottom: 15px;"></i>

                    <h3 style="color: var(--gray-600); margin-bottom: 10px;">No Workout Scheduled</h3>

                    <p style="color: var(--gray-500); margin-bottom: 20px;">Generate a workout to get started.</p>

                </div>

            `;

            return;

        }

        const exerciseCount = currentWorkout.exercises.length;

        const muscleGroups = new Set();

        currentWorkout.exercises.forEach(ex => {

            ex.muscleGroup?.forEach(mg => muscleGroups.add(mg));

        });

        const muscleList = Array.from(muscleGroups).slice(0, 3).join(', ');

        const moreCount = muscleGroups.size > 3 ? ` +${muscleGroups.size - 3} more` : '';

        container.innerHTML = `

            <div class="workout-meta">

                <div class="meta-item">

                    <span class="meta-label">Workout:</span>

                    <span class="meta-value">${currentWorkout.name}</span>

                </div>

                <div class="meta-item">

                    <span class="meta-label">Exercises:</span>

                    <span class="meta-value">${exerciseCount}</span>

                </div>

                <div class="meta-item">

                    <span class="meta-label">Muscle Groups:</span>

                    <span class="meta-value">${muscleList}${moreCount}</span>

                </div>

                <div class="meta-item">

                    <span class="meta-label">Status:</span>

                    <span class="meta-value badge badge-success">Ready to Start</span>

                </div>

            </div>

            <div class="workout-instructions">

                <p><strong>Next Steps:</strong> Click "Start Today's Workout" to begin logging your exercises.</p>

            </div>

        `;

    }



function updateDashboardChart(selectedRange = '12weeks') {

    const ctx = document.getElementById('dashboardChart').getContext('2d');

    if (charts.dashboard) charts.dashboard.destroy();



    // 1. Filter workouts by selected range

    const ranges = { '4weeks': 28, '8weeks': 56, '12weeks': 84, 'all': Infinity };

    const cutoffDays = ranges[selectedRange] || 84;

    const now = new Date();

    let workouts = [...workoutData.workouts];

    if (cutoffDays !== Infinity) {

        const cutoff = new Date(now.getTime() - cutoffDays * 86400000);

        workouts = workouts.filter(w => new Date(w.date) >= cutoff);

    }

    workouts.sort((a,b) => new Date(a.date) - new Date(b.date));



    if (workouts.length === 0) {

        charts.dashboard = new Chart(ctx, { type: 'line', data: { labels: [], datasets: [] } });

        return;

    }



    // 2. Helper: estimate 1RM

    const est1RM = (w, r) => (r <= 10) ? w * 36 / (37 - r) : w * (1 + r / 30);



    // 3. Compute composite score per workout

    const labels = [], scores = [];

    let allStrength = [], allVolume = [];



    for (const workout of workouts) {

        const date = new Date(workout.date).toLocaleDateString('en-US', { month:'short', day:'numeric' });

        labels.push(date);



        // Strength: avg of best muscleâ€‘group 1RM

        const muscleBest = new Map();

        for (const ex of (workout.exercises || [])) {

            if (!ex.actual || ex.skipped) continue;

            const a = ex.actual;

            if (!a.weight || a.weight <= 0) continue;

            let reps = 0;

            if (a.reps?.length) reps = Math.max(...a.reps);

            else if (a.durations?.length) reps = Math.max(...a.durations);

            if (!reps) continue;

            const oneRM = est1RM(a.weight, reps);

            (ex.muscleGroup || []).forEach(m => {

                if (!muscleBest.has(m) || oneRM > muscleBest.get(m)) muscleBest.set(m, oneRM);

            });

        }

        let strength = 0;

        if (muscleBest.size) strength = [...muscleBest.values()].reduce((s,v)=>s+v,0) / muscleBest.size;

        allStrength.push(strength);



        // Volume

        let volume = workout.summary?.totalVolume || 0;

        allVolume.push(volume);



        // Consistency (inverse days since last)

        let consistency = 50;

        const idx = workouts.indexOf(workout);

        if (idx > 0) {

            const days = (new Date(workout.date) - new Date(workouts[idx-1].date)) / 86400000;

            consistency = Math.min(100, 100 / Math.max(1, days));

        }



        // RPE (lower better)

        let avgRPE = workout.summary?.averageRPE || 7;

        if (typeof avgRPE === 'string') avgRPE = parseFloat(avgRPE);

        const rpeScore = Math.max(0, 100 - (avgRPE - 1) * 12.5);



        // Store raw for later normalisation

        scores.push({ strength, volume, consistency, rpeScore });

    }



    // 4. Normalise strength and volume across the selected range

    const minStrength = Math.min(...allStrength, 0);

    const maxStrength = Math.max(...allStrength, 1);

    const minVolume = Math.min(...allVolume, 0);

    const maxVolume = Math.max(...allVolume, 1);

    const norm = (val, mn, mx) => (mx === mn) ? 50 : ((val - mn) / (mx - mn)) * 100;



    const composite = scores.map(s => 

        norm(s.strength, minStrength, maxStrength) * 0.4 +

        norm(s.volume, minVolume, maxVolume) * 0.3 +

        s.consistency * 0.2 +

        s.rpeScore * 0.1

    ).map(v => Math.round(v));



    // 5. Yâ€‘axis scaling to emphasise bumps (use 5% padding)

    const yMin = Math.max(0, Math.min(...composite) - 5);

    const yMax = Math.min(100, Math.max(...composite) + 5);



    // 6. Create or update range buttons

    let btnDiv = document.getElementById('chartRangeBtns');

    if (!btnDiv) {

        btnDiv = document.createElement('div');

        btnDiv.id = 'chartRangeBtns';

        btnDiv.style.cssText = 'display:flex; gap:8px; justify-content:center; margin-bottom:12px;';

        document.querySelector('#dashboardChart').parentNode.insertBefore(btnDiv, document.querySelector('#dashboardChart'));

    }

    btnDiv.innerHTML = ['4weeks','8weeks','12weeks','all'].map(r => `

        <button class="btn btn-sm ${selectedRange===r ? 'btn-primary' : 'btn-outline'}" data-range="${r}">

            ${r === 'all' ? 'All' : r.replace('weeks',' wk')}

        </button>

    `).join('');

    btnDiv.querySelectorAll('button').forEach(btn => {

        btn.addEventListener('click', e => updateDashboardChart(btn.dataset.range));

    });



    // 7. Draw chart

    const gradient = ctx.createLinearGradient(0,0,0,300);

    gradient.addColorStop(0, 'rgba(16,185,129,0.6)');

    gradient.addColorStop(1, 'rgba(16,185,129,0.05)');

    charts.dashboard = new Chart(ctx, {

        type: 'line',

        data: {

            labels: labels,

            datasets: [{

                label: 'Activity Progress',

                data: composite,

                borderColor: '#10b981',

                backgroundColor: gradient,

                borderWidth: 3,

                tension: 0.2,

                fill: true,

                pointRadius: 3,

                pointHoverRadius: 5

            }]

        },

        options: {

            responsive: true,

            maintainAspectRatio: false,

            plugins: {

                tooltip: {

                    callbacks: {

                        label: (ctx) => `Progress: ${ctx.raw}%`,

                        afterBody: (items) => {

                            const i = items[0].dataIndex;

                            const s = scores[i];

                            return [

                                `Strength: ${Math.round(s.strength)} lbs`,

                                `Volume: ${Math.round(s.volume).toLocaleString()} lbs`,

                                `Consistency: ${Math.round(s.consistency)}%`,

                                `RPE: ${Math.round(s.rpeScore)}%`

                            ];

                        }

                    }

                },

                legend: { display: false }

            },

            scales: {

                y: { min: yMin, max: yMax, title: { display: true, text: 'Progress (%)' } },

                x: { title: { display: true, text: 'Workout Date' }, ticks: { maxRotation: 45, autoSkip: true } }

            }

        }

    });

}



function updateReadinessGauge() {

    const gauge = document.getElementById('readinessGauge');

    if (!gauge) return;

    const score = window.lastOverallRecovery || 0;

    const percentage = Math.min(100, Math.max(0, score));

    

    // Determine color + state label

    let color, state;

    if (percentage >= 80) { color = '#10b981'; state = 'Ready'; }           // green

    else if (percentage >= 60) { color = '#f59e0b'; state = 'Recovering'; } // amber

    else if (percentage >= 40) { color = '#f97316'; state = 'Recovering'; } // orange

    else { color = '#ef4444'; state = 'Not Ready'; }                        // red

    

    // Set conic gradient

    gauge.style.background = `conic-gradient(${color} 0deg ${percentage * 3.6}deg, #e2e8f0 0deg)`;

    

    // Keep the NUMBER inside the gauge (restored â€” user request)

    const span = gauge.querySelector('span');

    if (span) span.textContent = Math.round(percentage);

    

    // Update the readiness-label TEXT below the gauge to the state phrase

    var label = document.querySelector('.readiness-label');

    if (label) label.textContent = state;

}

 

// Get the top recommendation from the recovery section

function getTopRecommendation() {

    const container = document.getElementById('recoveryRecommendations');

    if (!container) return 'Check your recovery status for personalized advice.';

    

    // Find the first div that contains actual text (skip any empty ones)

    const firstDiv = container.querySelector('div');

    if (firstDiv) {

        // Extract plain text (remove inner HTML)

        const text = firstDiv.innerText.trim();

        if (text) return text;

    }

    return 'No recommendations available.';

}

      

    // WORKOUT GENERATION (already overridden above)



    // UTILITY FUNCTIONS

    function hasRecentLongevityWorkout() {

        const twoWeeksAgo = new Date(Date.now() - 14 * 24 * 60 * 60 * 1000);

        return workoutData.workouts.some(w => 

            (w.type === 'longevity_day' || w.type === 'mobility_flexibility') && new Date(w.date) > twoWeeksAgo

        );

    }



    // WORKOUT DISPLAY (MODIFIED to add search icon)

function updateTodaysWorkout() {

    const container = document.getElementById('exerciseList');

    if (!container) return;

    if (!currentWorkout || !currentWorkout.exercises) {

        container.innerHTML = '';

        document.getElementById('noWorkoutMessage').style.display = 'block';

        document.getElementById('workoutButtons').style.display = 'none';

        return;

    }

    

    const signature = JSON.stringify(currentWorkout.exercises.map(e => e.id));

    if (window.lastWorkoutSignature === signature && container.innerHTML !== '') return;

    window.lastWorkoutSignature = signature;

    

    container.innerHTML = '';

    currentWorkout.exercises.forEach((exercise, index) => {

        // *** CRITICAL: Do NOT regenerate prescription if already logged ***

        if (!exercise.actual && !exercise.skipped && !exercise.prescribed?.weight) {

            // Only generate prescription for exercises that have no actual data, are not skipped, and lack a prescribed weight

            const phase = getCurrentCyclePhase();

            const mult = getPhaseMultiplier(phase);

            const needsDeload = currentWorkout.isDeload || false;

            const isTapering = currentWorkout.isTaper || false;

            let localMultiplier = mult;

            if (needsDeload && !isTapering) localMultiplier *= 0.8;

            // P5P8: gentler loads for cycle phase / postpartum / youth / senior

            try { if (window.P4 && P4.LifeStage) localMultiplier *= P4.LifeStage.intensityMultiplier(); } catch (e) {}

            const updatedExercise = generateExercisePrescription(exercise, localMultiplier);

            // Preserve the id and any existing actual (should be null) but replace the object

            Object.assign(exercise, updatedExercise);

        }

        const exerciseEl = createExerciseElement(exercise, index);

        exerciseEl.setAttribute('data-index', index);

        container.appendChild(exerciseEl);

    });

    

    document.getElementById('noWorkoutMessage').style.display = 'none';

    document.getElementById('workoutButtons').style.display = 'flex';

}



function createExerciseElement(exercise, index) {

    // 1. Check if this exercise already has logged data

    const isLogged = (exercise.actual && !exercise.skipped);

    const exRecord = workoutData.exercises[exercise.id] || {};

    const hasTested1RM = (exRecord.tested1RM && exRecord.tested1RM > 0);



    const div = document.createElement('div');

    div.className = 'exercise-item' + (isLogged ? ' completed' : '');

    div.id = `exercise_${index}`;

    div.dataset.exerciseId = exercise.id;

    

    // Card click toggles logger (only if data exists)

    div.onclick = function(e) {

        if (e.target.closest('button, input, select, textarea, .record-icon, .drawer-close, .close-logger-btn, .exercise-btn, .show-instructions-btn, a')) return;

        if (!isLogged && !hasTested1RM) return;

        const logging = document.getElementById(`performance_${index}`);

        if (!logging || !logging.classList.contains('active')) togglePerformanceLogging(index);

    };



    // ---- Instructions & muscle tags ----

    const exerciseInfo = workoutProgram.exercises[exercise.id] || {};

    const instructions = exerciseInfo.instructions || exercise.instructions || ["No instructions available."];

    const instructionsList = instructions.map(inst => {

        let parsed = inst;

        if (typeof marked !== 'undefined') {

            parsed = marked.parse(inst);

            parsed = parsed.replace(/^<p>/, '').replace(/<\/p>$/, '');

        }

        return `<li>${parsed}</li>`;

    }).join('');

    const muscleTags = exercise.muscleGroup.map(mg =>

        `<span class="muscle-tag" data-muscle="${mg}" onclick="event.stopPropagation(); showMuscleImage('${mg}');">${getMuscleDisplayName(mg)}</span>`

    ).join('');

    const safeName = exercise.name.replace(/'/g, "\\'");



    // ---- Build prescription display (prioritise actual logged data) ----

    let prescriptionDisplay = '';

    let bestInfo = '';

    let lastInfo = '';

    let lastRPEInfo = '';

    let progressionNotesHtml = '';



    if (isLogged) {

        // *** SHOW THE ACTUAL LOGGED DATA ***

        const actual = exercise.actual;

        const weightStr = actual.weight ? `${actual.weight} lbs` : 'â€”';

        let repsOrDuration = 'â€”';

        if (actual.reps && actual.reps.length) {

            repsOrDuration = actual.reps.join(', ') + ' reps';

        } else if (actual.durations && actual.durations.length) {

            repsOrDuration = actual.durations.join(', ') + ' sec';

        }

        const setsStr = actual.sets ? `${actual.sets} sets` : 'â€”';

        let rpeStr = '';

        if (actual.rpe) {

            const rpeVal = Array.isArray(actual.rpe) ? actual.rpe.join('/') : actual.rpe;

            rpeStr = `(RPE ${rpeVal})`;

        }

        prescriptionDisplay = `âœ… Logged: ${weightStr} Ã— ${setsStr} (${repsOrDuration}) ${rpeStr}`;

        

        // Best info from history (if any)

        const bestWeight = exRecord.bestWeight;

        if (bestWeight && bestWeight > 0) {

            let bestStr = `${bestWeight} lbs`;

            if (exRecord.bestReps) bestStr += ` for ${exRecord.bestReps} reps`;

            const oneRM = Math.round(estimate1RM(bestWeight, exRecord.bestReps));

            bestStr += ` (est. 1RM: ${oneRM} lbs)`;

            bestInfo = `<div class="best-info" style="font-size:0.8rem; color:var(--gray-500);">ðŸ† Previous best: ${bestStr}</div>`;

        }

        const lastPerf = getLastPerformanceString(exercise.id);

        if (lastPerf) lastInfo = `<div class="last-info" style="font-size:0.8rem; color:var(--gray-500); margin-top:4px;">ðŸ“‹ ${lastPerf}</div>`;

        if (actual.rpe) {

            let rpe = Array.isArray(actual.rpe) ? actual.rpe[0] : actual.rpe;

            let color = rpe >= 9 ? 'var(--danger)' : (rpe >= 7 ? 'var(--warning)' : 'var(--success)');

            lastRPEInfo = `<span class="last-rpe-badge" style="background:${color}20; color:${color};">RPE ${rpe}</span>`;

        }

        progressionNotesHtml = `<div class="progression-notes" style="font-size: 0.85rem; color: var(--gray-600); max-width: 250px;">${exercise.progressionNotes || ''}</div>`;

    } 

    else if (hasTested1RM) {

        // Show prescription (only if not logged)

        const presType = exercise.prescriptionType || 'reps';

        if (presType === 'time') {

            const duration = exercise.prescribed.duration || 60;

            prescriptionDisplay = `${exercise.prescribed.sets} sets Ã— ${duration} sec holds`;

        } else {

            const repsInfo = typeof exercise.prescribed.reps === 'string'

                ? exercise.prescribed.reps

                : `${exercise.prescribed.reps.min}â€“${exercise.prescribed.reps.max}`;

            prescriptionDisplay = `${exercise.prescribed.sets} sets Ã— ${repsInfo}`;

        }

        // Fiber-aware weight range in the header: the suggested (min-side)

        // weight goes into the logger textbox; the ceiling is printed so the

        // working range stays fully visible at the top of the card.

        const rxWMin = exercise.prescribed ? exercise.prescribed.weight : null;

        const rxWMax = exercise.prescribed ? exercise.prescribed.weightMax : null;

        if (presType !== 'time' && rxWMin && rxWMax) {

            prescriptionDisplay += ` Â· ${rxWMin} lbs <span style="color:var(--primary); font-weight:700; white-space:nowrap;">(max: ${rxWMax} lbs)</span>`;

        }

        const lastSession = exRecord.history ? exRecord.history[exRecord.history.length - 1] : null;

        const bestWeight = exRecord.bestWeight;

        if (bestWeight && bestWeight > 0) {

            let bestStr = `${bestWeight} lbs`;

            if (exRecord.bestReps) {

                bestStr += ` for ${exRecord.bestReps} reps`;

                const oneRM = Math.round(estimate1RM(bestWeight, exRecord.bestReps));

                bestStr += ` (est. 1RM: ${oneRM} lbs)`;

            }

            bestInfo = `<div class="best-info" style="font-size:0.8rem; color:var(--gray-500);">ðŸ† Previous best: ${bestStr}</div>`;

        }

        const lastPerf = getLastPerformanceString(exercise.id);

        if (lastPerf) lastInfo = `<div class="last-info" style="font-size:0.8rem; color:var(--gray-500); margin-top:4px;">ðŸ“‹ ${lastPerf}</div>`;

        if (lastSession && lastSession.rpe) {

            let rpe = lastSession.rpe;

            let color = rpe >= 9 ? 'var(--danger)' : (rpe >= 7 ? 'var(--warning)' : 'var(--success)');

            lastRPEInfo = `<span class="last-rpe-badge" style="background:${color}20; color:${color};">RPE ${rpe}</span>`;

        }

        progressionNotesHtml = `<div class="progression-notes" style="font-size: 0.85rem; color: var(--gray-600); max-width: 250px;">${exercise.progressionNotes || ''}</div>`;

    }



    // ---- P5P5: component + prescription chips (always visible in the header) ----

    let chipsHtml = '';

    try {

        const splitForChips = workoutProgram.splits.find(s => s.id === currentWorkout.type);

        // P4: normalize componentsCovered to canonical order so header & footer agree

        const _rawCovered = (splitForChips && splitForChips.componentsCovered) ? splitForChips.componentsCovered : [];

        const covered = (typeof P4 !== 'undefined' && P4.normalizeComponents ? P4.normalizeComponents(_rawCovered) : _rawCovered).slice(0, 2);

        covered.forEach(cid => {

            const comp = (typeof MP_COMPONENTS !== 'undefined') ? MP_COMPONENTS.find(c => c.id === cid) : null;

            if (comp) {

                chipsHtml += `<span class="p4-chip comp" style="background:${comp.color}1f; color:${comp.color}; border-color:${comp.color}44;" title="${comp.name}"><i class="fas ${comp.icon}"></i>${comp.short}</span>`;

            }

        });

    } catch (e) {}

    if (isLogged) {

        chipsHtml += `<span class="p4-chip rx" title="Logged today"><i class="fas fa-check"></i> Logged</span>`;

    } else if (hasTested1RM && exercise.prescribed) {

        const pt = exercise.prescriptionType || 'reps';

        const rxTxt = pt === 'time'

            ? `${exercise.prescribed.sets} Ã— ${exercise.prescribed.duration || 60}s`

            : `${exercise.prescribed.sets} sets Ã— ${typeof exercise.prescribed.reps === 'string' ? exercise.prescribed.reps : exercise.prescribed.reps.min + 'â€“' + exercise.prescribed.reps.max}`;

        chipsHtml += `<span class="p4-chip rx" title="Today's prescription"><i class="fas fa-bullseye"></i> ${rxTxt}</span>`;

    }



    // ---- Build top HTML (test panel vs prescription) ----

    let topHtml = '';

    const pencilStyle = (isLogged || hasTested1RM) ? '' : 'display: none;';



    if (!isLogged && !hasTested1RM) {

        // Test panel with "could not do any rep" checkbox

        topHtml = `

            <div class="exercise-test-panel" style="background: var(--gray-100); border-radius: 12px; padding: 20px; margin: 15px 0 0 0;">

                <h4 style="margin-bottom: 12px; color: var(--primary);"><i class="fas fa-flask"></i> Find Your 1â€‘Rep Max for ${exercise.name}</h4>

                <p style="font-size: 0.9rem; margin-bottom: 15px;">Enter two numbers to get your personalised workout plan, or indicate if you couldn't do any rep:</p>

                <div class="form-group">

                    <label>ðŸ‹ï¸ For ${exercise.name}, heaviest weight you successfully lifted (lbs):</label>

                    <input type="number" id="test_success_${index}" step="2.5" placeholder="e.g., 185" class="test-input" style="min-height: 44px;">

                </div>

                <div class="form-group">

                    <label>âŒ For ${exercise.name}, weight just above that you could NOT lift (lbs):</label>

                    <input type="number" id="test_fail_${index}" step="2.5" placeholder="e.g., 195" class="test-input" style="min-height: 44px;">

                </div>

                <div class="form-group" style="margin-top: auto;">

                    <label style="display: flex; align-items: center; gap: 10px; cursor: pointer; color: var(--danger); font-weight: 600;">

                        <input type="checkbox" id="test_could_not_do_any_${index}" style="width: 20px; height: 20px; flex: 0 0 auto;">

                        <span><i class="fas fa-exclamation-triangle"></i> I couldn't do any rep, even with the lightest weight</span>

                    </label>

                </div>

                <button id="test_submit_${index}" class="btn btn-primary" style="width: 100%; opacity: 0.6; transition: 0.2s;" disabled>

                    <i class="fas fa-arrow-right"></i> Reveal my workout

                </button>

                <p class="test-note" style="font-size: 0.75rem; color: var(--gray-500); margin-top: 12px;">

                    âš ï¸ Warm up properly. Use a spotter. Stop if form breaks.

                </p>

            </div>

            <div class="exercise-prescription test-mode">

                <div class="prescription-title"><i class="fas fa-flask"></i> Test day â€” 1 set Ã— 1 rep</div>

                <div class="prescription-content" style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:8px;">

                    <div class="prescription-sets">1 set of 1 rep (test or estimate your max weight possible vs failed carefully)</div>

                    <div style="text-align:right;">${chipsHtml}</div>

                </div>

            </div>

        `;

    } else {

        topHtml = `

            <div class="exercise-prescription">

                <div class="prescription-title">

                    <i class="fas fa-bullseye"></i> Today's Prescription

                </div>

                <div class="prescription-content" style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 10px;">

                    <div>

                        <div class="prescription-sets" id="prescriptionDisplay_${index}">${prescriptionDisplay}</div>

                        ${(() => {

                            if (exRecord?.history && !isLogged) {

                                const lastEntry = [...exRecord.history].reverse().find(e => !e.skipped);

                                if (lastEntry && lastEntry.weight && exercise.prescribed && exercise.prescribed.weight) {

                                    const diff = exercise.prescribed.weight - lastEntry.weight;

                                    if (Math.abs(diff) > 0.1) {

                                        const sign = diff > 0 ? 'â†‘' : 'â†“';

                                        const color = diff > 0 ? 'var(--success)' : 'var(--warning)';

                                        return `<div style="font-size:0.8rem; color:${color}; margin-top:4px;">${sign} ${Math.abs(diff).toFixed(1)} lbs from last</div>`;

                                    }

                                }

                            }

                            return '';

                        })()}

                        ${bestInfo}

                        ${lastInfo}

                    </div>

                    <div style="text-align: right;">

                        ${lastRPEInfo}

                        ${progressionNotesHtml}

                    </div>

                </div>

                <div class="p4-card-chips">${chipsHtml}</div>

                <div class="sets-reps-grid" id="setsGrid_${index}"></div>

            </div>

        `;

    }



    // ---- Full HTML structure ----

    div.innerHTML = `

        <div class="exercise-header">

            <div style="flex:1;">

                <div class="exercise-title">

                    <i class="fas fa-dumbbell" style="color: var(--primary);"></i>

                    <span>${exercise.name}</span>

                </div>

                <div class="exercise-muscles">${muscleTags}</div>

                <div class="exercise-controls" style="margin-top: 8px; display: flex; gap: 8px; flex-wrap: wrap;">

                    <button class="exercise-btn show-instructions-btn" onclick="toggleInstructions(event, ${index})">

                        <i class="fas fa-info-circle"></i> Show Instructions

                    </button>

                    <div class="exercise-instructions" id="instructions_${index}">

                        <div class="instructions-title">

                            <i class="fas fa-graduation-cap"></i> How to Perform ${exercise.name}

                        </div>

                        <div class="instructions-content">

                            <ul>${instructionsList}</ul>

                        </div>

                    </div>

                    <button class="exercise-btn" style="background: var(--primary);" onclick="googleSearch('${safeName}', 'web'); event.stopPropagation();">

                        <i class="fas fa-play"></i> How to do

                    </button>

                    <button class="exercise-btn" style="background: var(--gray-600);" onclick="googleSearch('${safeName}', 'images'); event.stopPropagation();">

                        <i class="fas fa-image"></i> Images

                    </button>

                </div>

            </div>

            <div style="display: flex; gap: 8px;">

<div class="record-icon" style="cursor: pointer; background: linear-gradient(135deg, var(--primary), var(--secondary)); border-radius: 40px; width: auto; min-width: 100px; height: 40px; display: inline-flex; align-items: center; justify-content: center; transition: all 0.2s; box-shadow: 0 2px 6px rgba(0,0,0,0.1); ${pencilStyle}" onclick="${(isLogged || hasTested1RM) ? `openLoggerForExercise(${index}); event.stopPropagation();` : ''}" title="Record your sets">

    <span style="font-size: 0.85rem; font-weight: 600; white-space: nowrap; padding: 0 16px; display: flex; align-items: center; gap: 6px;">

        <i class="fas ${(() => {

            let hasDraft = false;

            if (currentWorkout && currentWorkout.id) {

                const draftKey = `draft_${currentWorkout.id}_${exercise.id}`;

                hasDraft = localStorage.getItem(draftKey) !== null;

            }

            return (hasDraft || isLogged) ? 'fa-play-circle' : 'fa-play';

        })()}" style="font-size: 0.9rem;"></i>

        <span>${(() => {

            let hasDraft = false;

            if (currentWorkout && currentWorkout.id) {

                const draftKey = `draft_${currentWorkout.id}_${exercise.id}`;

                hasDraft = localStorage.getItem(draftKey) !== null;

            }

            return (hasDraft || isLogged) ? 'Continue' : 'Begin';

        })()}</span>

    </span>

</div>

                <div class="record-icon" style="cursor: pointer; background: var(--danger); border-radius: 50%; width: 38px; height: 38px; display: flex; align-items: center; justify-content: center; transition: all 0.2s;" onclick="removeExerciseFromWorkout(${index}); event.stopPropagation();" title="Remove exercise from workout">

                    <i class="fas fa-trash-alt" style="color: white; font-size: 1rem;"></i>

                </div>

            </div>

        </div>

        ${topHtml}

        <div class="performance-logging" id="performance_${index}" style="display: none;">

            <div style="display: flex; justify-content: space-between; align-items: center; padding: 0 0 10px 0;">

                <button class="btn btn-outline btn-sm" onclick="closeLoggerAndRestoreCard(${index}); event.stopPropagation();">

                    <i class="fas fa-arrow-left"></i> Back

                </button>

                <span class="close-logger-btn" style="cursor: pointer; font-size: 1.5rem; line-height: 1; padding: 0 8px; color: var(--gray-600);" onclick="closeLoggerAndRestoreCard(${index}); event.stopPropagation();">&times;</span>

            </div>

            <h4 style="margin-bottom: 15px; color: var(--dark);"><i class="fas fa-edit"></i> Log Your Performance</h4>

            <div class="performance-form" id="performanceForm_${index}"></div>

        </div>

    `;



    // ---- Attach test panel listeners (only if no data) ----

if (!isLogged && !hasTested1RM) {

    const successInput = div.querySelector(`#test_success_${index}`);

    const failInput = div.querySelector(`#test_fail_${index}`);

    const couldNotCheckbox = div.querySelector(`#test_could_not_do_any_${index}`);

    const submitBtn = div.querySelector(`#test_submit_${index}`);

    

    // Create a separate Skip button (hidden by default)

    let skipBtn = div.querySelector(`#test_skip_${index}`);

    if (!skipBtn) {

        skipBtn = document.createElement('button');

        skipBtn.id = `test_skip_${index}`;

        skipBtn.className = 'btn btn-warning';

        skipBtn.style.marginTop = '10px';

        skipBtn.style.width = '100%';

        skipBtn.style.display = 'none';

        skipBtn.innerHTML = '<i class="fas fa-forward"></i> Skip this exercise for today';

        // Insert after submitBtn

        submitBtn.parentNode.insertBefore(skipBtn, submitBtn.nextSibling);

    }



    const checkInputs = () => {

        if (couldNotCheckbox.checked) {

            submitBtn.disabled = false;

            submitBtn.style.opacity = '1';

            submitBtn.innerHTML = '<i class="fas fa-dumbbell"></i> Try very light weight';

            skipBtn.style.display = 'block';

            return;

        }

        submitBtn.innerHTML = '<i class="fas fa-arrow-right"></i> Reveal my workout';

        skipBtn.style.display = 'none';

        const successVal = parseFloat(successInput.value);

        const failVal = parseFloat(failInput.value);

        const isValid = !isNaN(successVal) && successVal > 0 && !isNaN(failVal) && failVal > successVal;

        submitBtn.disabled = !isValid;

        submitBtn.style.opacity = isValid ? '1' : '0.6';

    };



    successInput.addEventListener('input', checkInputs);

    failInput.addEventListener('input', checkInputs);

    couldNotCheckbox.addEventListener('change', checkInputs);



    // Original submit (low 1RM) handler

    submitBtn.addEventListener('click', () => {

        if (submitBtn.disabled) return;

        if (couldNotCheckbox.checked) {

            // User cannot do any rep â€“ set a very low estimated 1RM (original behavior)

            const lowEstimate = Math.max(20, (workoutData.user.weight || 150) * 0.15);

            const exRecord = getOrCreateExerciseRecord(exercise.id);

            exRecord.tested1RM = lowEstimate;

            exRecord.testDate = new Date().toISOString();

            exRecord.testWeight = 0;

            exRecord.testReps = 0;

            exRecord.failWeight = 0;

            saveToLocalStorage();

            // Regenerate prescription

            const phase = getCurrentCyclePhase();

            const mult = getPhaseMultiplier(phase);

            const needsDeload = currentWorkout.isDeload || false;

            const isTapering = currentWorkout.isTaper || false;

            let localMultiplier = mult;

            if (needsDeload && !isTapering) localMultiplier *= 0.8;

            // P5P8: gentler loads for cycle phase / postpartum / youth / senior

            try { if (window.P4 && P4.LifeStage) localMultiplier *= P4.LifeStage.intensityMultiplier(); } catch (e) {}

            const updatedExercise = generateExercisePrescription(exercise, localMultiplier);

            currentWorkout.exercises[index] = updatedExercise;

            saveCurrentWorkoutToStorage();

            refreshExerciseCard(index);

            showNotification(`Estimated 1RM set to ${Math.round(lowEstimate)} lbs (very low). Prescription updated.`, "info");

        } else {

            submitStrengthTest(index);

        }

    });



    // Skip button handler

    skipBtn.addEventListener('click', () => {

        skipExercise(index);

    });

}



    // ---- Postâ€‘test initialisation ----

    if (isLogged || hasTested1RM) {

        setTimeout(() => {

            updateSetsGrid(index, exercise);

            createPerformanceForm(index, exercise);

        }, 10);

    }



    return div;

}

      // Estimate 1RM from a successful single rep and a failed single rep weight.

// Uses a simple linear interpolation: 1RM = success + (fail - success) * 0.5

// You can adjust the factor (0.5) later based on expert advice.

function robustEstimate1RM(passWeight, failWeight, exerciseId, workoutCache) {

    const recovery = workoutCache ? workoutCache.recoveryFactor : 1.0;

    const priorBase = workoutCache ? workoutCache.sessionPriorBase : (workoutData.user._cache.effectiveBodyWeight * 0.7);

    

    // Validation gate

    if (passWeight <= 0 || failWeight <= 0) {

        return { value: priorBase * 0.9, confidence: 0.5 };

    }

    const gapRatio = (failWeight - passWeight) / passWeight;

    const maxGap = 0.15 + 0.05 * (1 - recovery);

    if (gapRatio > maxGap) {

        return { value: priorBase * 0.9, confidence: 0.5 };

    }

    

    // Shrinkage estimator

    const lambda = Math.min(0.8, Math.max(0.2, gapRatio / 0.15));

    const midpoint = (passWeight + failWeight) / 2;

    const estimated = lambda * priorBase + (1 - lambda) * midpoint;

    const confidence = 1 - lambda * 0.3;

    

    return { value: estimated, confidence };

}

function submitStrengthTest(index) {

    const exercise = currentWorkout.exercises[index];

    if (!exercise) return;



    const successInput = document.getElementById(`test_success_${index}`);

    const failInput = document.getElementById(`test_fail_${index}`);

    if (!successInput || !failInput) return;



    const successWeight = parseFloat(successInput.value);

    const failWeight = parseFloat(failInput.value);



    if (isNaN(successWeight) || successWeight <= 0 || isNaN(failWeight) || failWeight <= successWeight) {

        showNotification("Please enter valid weights (fail weight must be heavier than successful lift).", "error");

        return;

    }



    // Estimate 1RM using robust bracketing

    const result = robustEstimate1RM(successWeight, failWeight, exercise.id, currentWorkout._cache);

    const estimated1RM = result.value;

    const confidence = result.confidence;

    

    if (!estimated1RM || estimated1RM <= 0) {

        showNotification("Could not estimate 1RM. Please try again.", "error");

        return;

    }



    // Store the tested 1RM and confidence in the exercise record

    const exRecord = getOrCreateExerciseRecord(exercise.id);

    exRecord.tested1RM = estimated1RM;

    exRecord.testConfidence = confidence;

    exRecord.testDate = new Date().toISOString();

    exRecord.testWeight = successWeight;

    exRecord.testReps = 1;

    exRecord.failWeight = failWeight;



    // Save to storage

    saveToLocalStorage();



    // Regenerate prescription based on the new tested 1RM

    const phase = getCurrentCyclePhase();

    const mult = getPhaseMultiplier(phase);

    // Use the current deload/taper flags (if needed)

    const needsDeload = currentWorkout.isDeload || false;

    const isTapering = currentWorkout.isTaper || false;

    let localMultiplier = mult;

    if (needsDeload && !isTapering) localMultiplier *= 0.8;

    // P5P8: gentler loads for cycle phase / postpartum / youth / senior

    try { if (window.P4 && P4.LifeStage) localMultiplier *= P4.LifeStage.intensityMultiplier(); } catch (e) {}



    // Reâ€‘generate the prescription for this exercise

    const updatedExercise = generateExercisePrescription(exercise, localMultiplier);

    // Replace the exercise in the workout array

    currentWorkout.exercises[index] = updatedExercise;



    // Save the updated workout

    saveCurrentWorkoutToStorage();



    // Refresh the exercise card

    refreshExerciseCard(index);



    showNotification(`1RM estimated at ${Math.round(estimated1RM)} lbs! Prescription updated.`, "success");

}



function refreshExerciseCard(index) {

    const exercise = currentWorkout.exercises[index];

    if (!exercise) return;

    const container = document.getElementById(`exercise_${index}`);

    if (!container) return;

    const newCard = createExerciseElement(exercise, index);

    container.parentNode.replaceChild(newCard, container);

}

/**

 * Updates the rolling weekly volume history used for adaptive deload threshold.

 * Call this after each completed workout (inside postWorkoutUpdate).

 * @param {Object} completedWorkout - The workout object that was just finished.

 */

function updateWeeklyVolumeTracking(completedWorkout) {

    // 1. Get the ISO week number of the workout date (Monday as first day of week)

    const workoutDate = new Date(completedWorkout.date);

    const weekKey = getISOWeekKey(workoutDate); // e.g., "2025-W14"



    // 2. Get the total volume of this workout (already computed in summary)

    const workoutVolume = completedWorkout.summary?.totalVolume || 0;



    // 3. Initialize weeklyVolumes array if not exists

    if (!workoutData.user.weeklyVolumes) {

        workoutData.user.weeklyVolumes = [];

    }



    // 4. Find existing entry for this week

    const existingIndex = workoutData.user.weeklyVolumes.findIndex(entry => entry.weekKey === weekKey);



    if (existingIndex !== -1) {

        // Add volume to existing week

        workoutData.user.weeklyVolumes[existingIndex].totalVolume += workoutVolume;

    } else {

        // Create new week entry

        workoutData.user.weeklyVolumes.push({

            weekKey: weekKey,

            totalVolume: workoutVolume

        });

    }



    // 5. Sort by weekKey descending (most recent first)

    workoutData.user.weeklyVolumes.sort((a, b) => b.weekKey.localeCompare(a.weekKey));



    // 6. Keep only the last 4 weeks (or fewer if not enough data)

    if (workoutData.user.weeklyVolumes.length > 4) {

        workoutData.user.weeklyVolumes = workoutData.user.weeklyVolumes.slice(0, 4);

    }



    // 7. Save to storage

    saveToLocalStorage();

}



/**

 * Returns an ISO week key (e.g., "2025-W14") for a given date.

 * Based on ISO 8601: weeks start on Monday, first week of year has Thursday.

 * @param {Date} date

 * @returns {string}

 */

function getISOWeekKey(date) {

    const tempDate = new Date(date);

    // Set to nearest Thursday: current date + 4 - current day number (Mon=1, Sun=7)

    const dayNum = tempDate.getDay() || 7; // Convert Sun (0) to 7

    tempDate.setDate(tempDate.getDate() + 4 - dayNum);

    const year = tempDate.getFullYear();

    const firstThursday = new Date(year, 0, 1);

    const firstThursdayDay = firstThursday.getDay() || 7;

    const weekNumber = Math.ceil(((tempDate - firstThursday) / 86400000 + firstThursdayDay) / 7);

    return `${year}-W${weekNumber.toString().padStart(2, '0')}`;

}

    function getMuscleDisplayName(muscleId) {

        const allMuscles = getAllMuscleGroups();

        const muscle = allMuscles.find(m => m.name === muscleId);

        return muscle ? muscle.display : muscleId;

    }

function getLastPerformanceString(exerciseId) {

    const exData = workoutData.exercises[exerciseId];

    if (!exData || !exData.history) return null;

    // Get most recent nonâ€‘skipped entry (from history array, newest first)

    const lastEntry = [...exData.history].reverse().find(entry => !entry.skipped);

    if (!lastEntry) return null;



    const weight = lastEntry.weight;

    let volumeText = '';

    if (lastEntry.durations) {

        // Timeâ€‘based exercise

        volumeText = lastEntry.durations.join(', ') + ' sec';

    } else if (lastEntry.reps) {

        volumeText = lastEntry.reps.join(', ') + ' reps';

    } else {

        volumeText = '?';

    }



    const rpe = lastEntry.rpe;

    if (rpe !== undefined && rpe !== null) {

        return `Last: ${weight} lbs Ã— ${volumeText} (RPE ${rpe})`;

    } else {

        return `Last: ${weight} lbs Ã— ${volumeText}`;

    }

}

// Open the add exercise modal and populate library

function openAddExerciseModal() {

    const modal = document.getElementById('addExerciseModal');

    if (!modal) return;

    populateAddExerciseList();

    modal.classList.add('active');

}



// Populate the list with all exercises from the library (using getEnhancedExerciseList)

function populateAddExerciseList() {

    const container = document.getElementById('addExerciseList');

    if (!container) return;

    const allExercises = getEnhancedExerciseList(); // already defined in your code

    renderAddExerciseList(allExercises);

}



// Filter the list based on search input

function filterAddExerciseList() {

    const searchTerm = document.getElementById('addExerciseSearch')?.value.toLowerCase() || '';

    const allExercises = getEnhancedExerciseList();

    const filtered = allExercises.filter(ex => 

        ex.name.toLowerCase().includes(searchTerm) || 

        (ex.muscles && ex.muscles.some(m => m.toLowerCase().includes(searchTerm))) ||

        (ex.equipment && ex.equipment.toLowerCase().includes(searchTerm))

    );

    renderAddExerciseList(filtered);

}



// Render exercises in the modal

function renderAddExerciseList(exercises) {

    const container = document.getElementById('addExerciseList');

    if (!container) return;

    if (exercises.length === 0) {

        container.innerHTML = '<div class="empty-state">No exercises found</div>';

        return;

    }

    let html = '';

    exercises.forEach(ex => {

        const muscles = ex.muscles ? ex.muscles.map(m => `<span class="muscle-tag">${getMuscleDisplayName(m)}</span>`).join(' ') : '';

        const equipment = ex.equipment ? `<i class="fas fa-tools"></i> ${ex.equipment}` : '';

        const safeName = ex.name.replace(/'/g, "\\'");

        html += `

            <div class="chart-card" style="padding: 15px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">

                <div style="flex: 2;">

                    <strong>${ex.name}</strong><br>

                    <div style="font-size: 0.8rem; color: var(--gray-600);">${muscles} ${equipment}</div>

                </div>

                <button class="btn btn-sm btn-success" onclick="addExerciseToCurrentWorkout('${ex.id}', '${safeName}')">

                    <i class="fas fa-plus"></i> Add

                </button>

            </div>

        `;

    });

    container.innerHTML = html;

}



// Add the selected exercise to the current workout

function addExerciseToCurrentWorkout(exerciseId, exerciseName) {

    // Find the full exercise object from ultimateExerciseLibrary

    let libraryEx = null;

    for (const group in ultimateExerciseLibrary) {

        const found = ultimateExerciseLibrary[group].find(ex => ex.name.toLowerCase().replace(/\s/g, '_') === exerciseId);

        if (found) {

            libraryEx = found;

            break;

        }

    }

    if (!libraryEx) {

        showNotification('Exercise not found', 'error');

        return;

    }

    

    // Create a new exercise object

    const newExercise = {

        id: exerciseId,

        name: libraryEx.name,

        muscleGroup: libraryEx.muscles || [],

        prescribed: {

            sets: libraryEx.defaultSets,

            reps: libraryEx.defaultReps,

            weight: null

        },

        actual: null,

        skipped: false,

        progressionNotes: libraryEx.progression || '',

        equipment: libraryEx.equipment,

        instructions: libraryEx.instructions,

        prescriptionType: libraryEx.prescriptionType || 'reps'

    };

    

    // Generate prescription based on user data

    const generated = generateExercisePrescription(newExercise);

    Object.assign(newExercise, generated);

    

    if (!currentWorkout) {

        // No active workout â€“ create one first

        generateNextWorkout();

        setTimeout(() => {

            if (currentWorkout) {

                currentWorkout.exercises.push(newExercise);

                saveCurrentWorkoutToStorage();

                if (document.getElementById('workout-section').classList.contains('active')) {

                    updateTodaysWorkout();

                    updateWorkoutHeader();

                }

                closeModal('addExerciseModal');

                showNotification(`Added ${libraryEx.name} to workout`, 'success');

            }

        }, 500);

    } else {

        currentWorkout.exercises.push(newExercise);

        saveCurrentWorkoutToStorage();

        if (document.getElementById('workout-section').classList.contains('active')) {

            updateTodaysWorkout();

            updateWorkoutHeader();

        }

        closeModal('addExerciseModal');

        showNotification(`Added ${libraryEx.name} to workout`, 'success');

    }

}

  function toggleInstructions(event, index) {

      event.stopPropagation();

      const instructions = document.getElementById(`instructions_${index}`);

      instructions.classList.toggle('active');

      const button = event.target.closest('.show-instructions-btn');

      button.innerHTML = instructions.classList.contains('active')

          ? '<i class="fas fa-times"></i> Hide Instructions'

          : '<i class="fas fa-info-circle"></i> Show Instructions';

  }



    function updateSetsGrid(index, exercise) {

    const grid = document.getElementById(`setsGrid_${index}`);

    if (!grid) return;

    grid.innerHTML = '';



    const presType = exercise.prescriptionType || 'reps';

    const targetSets = exercise.prescribed.sets;



    // Create a compact representation

    const container = document.createElement('div');

    container.style.display = 'flex';

    container.style.flexWrap = 'wrap';

    container.style.gap = '8px';

    container.style.marginTop = '10px';



    for (let i = 0; i < targetSets; i++) {

        const setBadge = document.createElement('span');

        setBadge.className = 'set-badge';

        setBadge.style.backgroundColor = 'var(--gray-200)';

        setBadge.style.padding = '4px 12px';

        setBadge.style.borderRadius = '20px';

        setBadge.style.fontSize = '0.9rem';

        setBadge.style.fontWeight = '600';

        setBadge.style.color = 'var(--gray-700)';



        if (presType === 'time') {

            const duration = exercise.prescribed.duration || 60;

            setBadge.textContent = `Set ${i+1}: ${duration}s`;

        } else {

            const repsInfo = typeof exercise.prescribed.reps === 'string'

                ? exercise.prescribed.reps

                : `${exercise.prescribed.reps.min}â€“${exercise.prescribed.reps.max}`;

            setBadge.textContent = `Set ${i+1}: ${repsInfo} reps`;

        }

        container.appendChild(setBadge);

    }

    grid.appendChild(container);

}

      

