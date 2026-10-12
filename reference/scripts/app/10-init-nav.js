// ============================================================
// reference/scripts/app/10-init-nav.js
// Extracted from reference/scripts/app.js
// Original lines: 59656-60588  (933 lines)
// Section: Init, restore, history trap, device detection, infinite nav
// ============================================================

    // ==================== ALL ORIGINAL FUNCTIONS ====================

    // GET ALL MUSCLE GROUPS

// Cache for muscle groups

let muscleGroupsCache = null;



function getAllMuscleGroups() {

    if (!muscleGroupsCache) {

        muscleGroupsCache = [

            ...muscleDatabase.major,

            ...muscleDatabase.longevity,

            ...muscleDatabase.hands,

            ...muscleDatabase.feet

        ];

    }

    return muscleGroupsCache;

}



    // Initialize muscle tracking

    function initializeMuscleTracking() {

        const allMuscles = getAllMuscleGroups();

        allMuscles.forEach(muscle => {

            if (!muscleLastTrained[muscle.name]) {

                muscleLastTrained[muscle.name] = null;

            }

        });

    }







    // GLOBAL VARIABLES

    let currentWorkout = null;

 // let sortableInstance = null;   // drag & drop removed

    let lastWorkoutSignature = null;

    let fuseIndex = null;        // for fuzzy search

    let savedWorkout = null;               // last generated workout saved in localStorage

    const STORAGE_KEY_WORKOUT = 'savedWorkout';   // localStorage key

    let muscleLastTrained = {};

    let charts = {};

    let lastRenderedWorkoutId = null;

    let currentHistoryFilter = 'all';   // 'all', 'week', 'month', 'year', 'longevity'

    let currentGrouping = 'muscleGroup';        // default grouping

    let collapsedGroups = (function () {                       // persisted per-tab via localStorage

        try {

            var raw = localStorage.getItem('p4_library_collapsed');

            if (raw) return JSON.parse(raw) || {};

        } catch (e) {}

        return {};

    })();

    function saveLibraryCollapse() {

        try { localStorage.setItem('p4_library_collapsed', JSON.stringify(collapsedGroups)); } catch (e) {}

    }

    let librarySearchTimeout;                    // for debounce

    // Persistent navigation stack â€“ never loses history

    let navigationStack = (function() {

        try {

            const saved = localStorage.getItem('workout_navigation_stack');

            if (saved) {

                const parsed = JSON.parse(saved);

                if (Array.isArray(parsed) && parsed.length > 0) return parsed;

            }

        } catch(e) {}

        return ['dashboard'];

    })();

    // Immediately save back to localStorage (ensures consistency)

    localStorage.setItem('workout_navigation_stack', JSON.stringify(navigationStack));  



    function getTopOffset() {

        // P5P2 restored the fixed navbar on desktop, so both paths offset it

        return 84; // navbar height (70px) + breathing room

    }

      

    // DOM ELEMENTS

    const mainApp = document.getElementById('mainApp');

    const navMenu = document.getElementById('navMenu');

      

    // Save navigation stack when the page is hidden (covers closing tab, navigating away)

    window.addEventListener('pagehide', function() {

        localStorage.setItem('workout_navigation_stack', JSON.stringify(navigationStack));

    });



document.addEventListener('DOMContentLoaded', async function() {

    // ========== RESTORE AND REâ€‘ARM THE HISTORY TRAP ==========

    (function restoreAndTrap() {

        try {

            const saved = localStorage.getItem('workout_navigation_stack');

            if (saved) {

                const parsed = JSON.parse(saved);

                if (Array.isArray(parsed) && parsed.length > 0) navigationStack = parsed;

            }

        } catch(e) {}

        

        let currentSection = window.location.hash.slice(1) || navigationStack[navigationStack.length - 1] || 'dashboard';

        if (!document.getElementById(`${currentSection}-section`)) currentSection = 'dashboard';

        

        if (navigationStack[navigationStack.length - 1] !== currentSection) {

            navigationStack.push(currentSection);

            localStorage.setItem('workout_navigation_stack', JSON.stringify(navigationStack));

        }

        

        const trapState = { stack: navigationStack, trap: true, ts: Date.now() };

        try { window.history.replaceState(trapState, '', `#${currentSection}`); } catch(e) {}

        try { window.history.pushState({ ...trapState, guard: true }, '', `#${currentSection}`); } catch(e) {}

    })();



    // ========== 1. IMMEDIATELY HIDE IMPORT SCREEN ==========

    const mainApp = document.getElementById('mainApp');

    if (mainApp) mainApp.style.display = 'block';



    // ========== 2. ENSURE DEXIE IS READY ==========

    if (typeof db === 'undefined') {

        window.db = new Dexie('WorkoutApp');

        db.version(1).stores({

            workouts: 'id, date',

            exercises: 'id',

            user: 'key',

            savedWorkout: 'id'

        });

    }



    // ========== 3. MIGRATE FROM LOCALSTORAGE (if needed) ==========

    const workoutCount = await db.workouts.count();

    if (workoutCount === 0) {

        const oldData = localStorage.getItem('workoutContinuityData');

        if (oldData) {

            try {

                const parsed = JSON.parse(oldData);

                if (parsed.workouts && parsed.workouts.length) await db.workouts.bulkPut(parsed.workouts);

                if (parsed.exercises) {

                    const exerciseRecords = Object.entries(parsed.exercises).map(([id, data]) => ({ id, ...data }));

                    await db.exercises.bulkPut(exerciseRecords);

                }

                if (parsed.user) await db.user.put({ key: 'main', value: parsed.user });

                if (parsed.savedWorkout) await db.savedWorkout.put({ id: 'current', value: parsed.savedWorkout });

            } catch (e) { }

        }

    }



    // ========== 4. LOAD ALL DATA USING ROBUST FUNCTION (fixes refresh data loss) ==========

    await loadAllData();   // This replaces the old manual Dexie loading



console.log("loadAllData completed. Current state:", { currentWorkout, savedWorkout });

console.log("Loader element classes:", document.getElementById('loading')?.className);

console.log("Body classes:", document.body.className);

  

    // ========== 5. RESTORE LAST VISITED SECTION ==========

    const isFreshStart = (workoutData.workouts.length === 0 && Object.keys(workoutData.exercises).length === 0);

    const lastSection = sessionStorage.getItem('lastSection');

    let sectionToShow = 'dashboard';

    navigationStack = [sectionToShow];

    if (isFreshStart) {

        sectionToShow = 'workout';

    } else if (lastSection && document.getElementById(`${lastSection}-section`)) {

        sectionToShow = lastSection;

    }



    // Hide the loader and show the section

    const loaderDiv = document.getElementById('loading');

    if (loaderDiv && loaderDiv.classList.contains('loading')) {

        loaderDiv.style.transition = 'opacity 0.3s';

        loaderDiv.style.opacity = '0';

        setTimeout(() => {

            loaderDiv.classList.remove('active');

            loaderDiv.style.opacity = '';

            void document.body.offsetHeight;

            document.body.classList.remove('loading');

            setTimeout(() => {

                showSection(sectionToShow, true);

                if (typeof window.appReady === 'function') window.appReady();

                window.__appInitialized = true;

            }, 50);

        }, 300);

    } else {

        document.body.classList.remove('loading');

        setTimeout(() => {

            showSection(sectionToShow, true);

            if (typeof window.appReady === 'function') window.appReady();

                window.__appInitialized = true;

        }, 50);

    }



    // ========== 6. INITIALIZE REMAINING UI (preserves all features) ==========

    const today = new Date();

    const eighteenYearsAgo = new Date(today.getFullYear() - 25, today.getMonth(), today.getDate());

    document.getElementById('settingsBirthDate').value = eighteenYearsAgo.toISOString().split('T')[0];

    loadThemeSettings();

    if (!document.querySelector('#inter-font')) {

        const link = document.createElement('link');

        link.id = 'inter-font';

        link.rel = 'stylesheet';

        link.href = 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap';

        document.head.appendChild(link);

    }

    initializeMuscleTracking();

    initializeMuscleFatigue();

    applyFatigueDecay();



    // Close modal when clicking outside content

    document.querySelectorAll('.modal').forEach(modal => {

        modal.addEventListener('click', function(e) {

            if (e.target === this) this.classList.remove('active');

        });

    });



    // --- Populate quick actions modal with static buttons ---

    const quickButtonsContainer = document.getElementById('quickActionsButtons');

    if (quickButtonsContainer) {

        if (!quickButtonsContainer.querySelector('.btn[onclick*="exportWorkoutData"]')) {

            const exportBtn = document.createElement('button');

            exportBtn.className = 'btn';

            exportBtn.innerHTML = '<i class="fas fa-file-export"></i> Export Data';

            exportBtn.onclick = function() { exportWorkoutData(); closeModal('quickActionsModal'); };

            quickButtonsContainer.appendChild(exportBtn);

        }

        if (!quickButtonsContainer.querySelector('.btn[onclick*="generateLongevityWorkout"]')) {

            const longevityBtn = document.createElement('button');

            longevityBtn.className = 'btn btn-info';

            longevityBtn.innerHTML = '<i class="fas fa-user-md"></i> Longevity Workout';

            longevityBtn.onclick = function() { generateLongevityWorkout(); closeModal('quickActionsModal'); };

            quickButtonsContainer.appendChild(longevityBtn);

        }

    }



    setTimeout(() => {

        updatePeriodButtonVisibility();

        if (workoutData.user.menstrual.lastPeriodStart) {

            const nextPeriod = new Date(workoutData.user.menstrual.lastPeriodStart);

            nextPeriod.setDate(nextPeriod.getDate() + workoutData.user.menstrual.cycleLength);

            const daysUntil = Math.ceil((nextPeriod - new Date()) / (1000*60*60*24));

            if (daysUntil <= 5 && daysUntil >= 0) {

                showNotification(`Your period is expected in ${daysUntil} days. Prepare accordingly.`, 'info', 'period_reminder');

            }

        }

    }, 500);



    window.applyBottomNavBehavior = function() {};

    window.addEventListener('beforeunload', () => {

        if (currentWorkout) {

            localStorage.setItem('workoutEmergencyBackup', JSON.stringify({

                workout: currentWorkout,

                timestamp: Date.now(),

                version: 2

            }));

        }

    });



    // ------------------ DEVICE DETECTION AND BOTTOM NAV (COMPUTERS ONLY) ------------------

    const isComputer = (function() {

        const ua = navigator.userAgent.toLowerCase();

        const isWindows = ua.includes('windows');

        const isMac = ua.includes('macintosh') || ua.includes('mac os');

        const isLinux = ua.includes('linux') && !ua.includes('android');

        const isMobile = /mobile|tablet|ipad|iphone|android|blackberry|windows phone|iemobile/i.test(ua);

        return (isWindows || isMac || isLinux) && !isMobile;

    })();



    if (isComputer) {

        document.body.classList.add('device-computer');

        if (!workoutData.user.settings) workoutData.user.settings = {};

        const bottomNav = document.getElementById('bottomNav');

        let hideTimer = null;

        let lastMouseY = window.innerHeight;

        function showBottomNav() { if (hideTimer) clearTimeout(hideTimer); bottomNav.classList.add('visible'); }

        function scheduleHideBottomNav() { if (hideTimer) clearTimeout(hideTimer); hideTimer = setTimeout(() => bottomNav.classList.remove('visible'), 1000); }

        window.showBottomNav = showBottomNav;

        window.scheduleHideBottomNav = scheduleHideBottomNav;

        window.addEventListener('mousemove', (e) => { lastMouseY = e.clientY; });

        let listenersAttached = false;

        const mouseMoveHandler = (e) => {

            const threshold = 50;

            if (e.clientY >= window.innerHeight - threshold) showBottomNav();

            else if (bottomNav.classList.contains('visible')) scheduleHideBottomNav();

        };

        const mouseEnterHandler = showBottomNav;

        const mouseLeaveHandler = scheduleHideBottomNav;

        function attachListeners() { if (listenersAttached) return; window.addEventListener('mousemove', mouseMoveHandler); bottomNav.addEventListener('mouseenter', mouseEnterHandler); bottomNav.addEventListener('mouseleave', mouseLeaveHandler); listenersAttached = true; }

        function detachListeners() { if (!listenersAttached) return; window.removeEventListener('mousemove', mouseMoveHandler); bottomNav.removeEventListener('mouseenter', mouseEnterHandler); bottomNav.removeEventListener('mouseleave', mouseLeaveHandler); listenersAttached = false; }

        function applyBottomNavBehavior() {

            const autoHide = workoutData.user.settings.bottomNavAutoHide || false;

            if (autoHide) { showBottomNav(); setTimeout(() => { if (lastMouseY < window.innerHeight - 50) scheduleHideBottomNav(); }, 100); attachListeners(); }

            else { showBottomNav(); detachListeners(); }

        }

        window.applyBottomNavBehavior = applyBottomNavBehavior;

        applyBottomNavBehavior();

    }



    initializeExerciseStats();

    migrateToPhase2();

    initializeMuscleFatigue();

    recomputeAggregates();

    updateBodyWeightEstimate();



});

    // Replace workout trigger logic â€“ DISABLED to prevent accidental replacement

document.addEventListener('DOMContentLoaded', function() {

    // After loading user from Dexie or fresh default

    if (!workoutData.user.aggregates) {

        workoutData.user.aggregates = {

            totalVolumeLast7Days: 0,

            avgRPELast7Days: 0,

            volumeByMuscleLast60Days: {}

        };

    }

    // REPLACE TRIGGER DISABLED â€“ commenting out to prevent accidental workout loss

    /*

    const trigger = document.getElementById('replaceTrigger');

    const replaceBtn = document.getElementById('replaceButton');

    const container = document.getElementById('replaceWorkoutContainer');



    if (!trigger || !replaceBtn || !container) return;



    function resetToTrigger() {

        replaceBtn.style.display = 'none';

        trigger.style.display = 'inline-flex';

    }



    function showReplaceButton() {

        trigger.style.display = 'none';

        replaceBtn.style.display = 'inline-flex';

    }



    trigger.addEventListener('click', function(e) {

        e.stopPropagation();

        showReplaceButton();

    });



    replaceBtn.addEventListener('click', function(e) {

        e.stopPropagation();

        Swal.fire({

            title: 'Replace this workout?',

            text: "You will lose all unsaved progress for this session.",

            icon: 'warning',

            showCancelButton: true,

            confirmButtonColor: '#d33',

            confirmButtonText: 'Yes, replace it',

            cancelButtonText: 'Cancel'

        }).then((result) => {

            if (result.isConfirmed) {

                if (typeof generateNextWorkout === 'function') {

                    generateNextWorkout();

                } else {

                    console.warn("generateNextWorkout not available");

                    showNotification("Could not replace workout", "error");

                }

                resetToTrigger();

                setTimeout(() => {

                    if (document.getElementById('workout-section').classList.contains('active')) {

                        if (typeof updateTodaysWorkout === 'function') updateTodaysWorkout();

                        if (typeof updateWorkoutHeader === 'function') updateWorkoutHeader();

                    }

                }, 150);

            }

        });

    });



    document.addEventListener('click', function(e) {

        if (!container.contains(e.target)) {

            resetToTrigger();

        }

    });

    */

});

      

// Debounce helper â€“ delays execution until after a pause

function debounce(func, wait) {

    let timeout;

    return function executedFunction(...args) {

        const later = () => {

            clearTimeout(timeout);

            func(...args);

        };

        clearTimeout(timeout);

        timeout = setTimeout(later, wait);

    };

}



    // Perâ€‘exercise debouncers for weight input

    const weightDebouncers = {};



    function debouncedWeightChange(index, value) {

        if (weightDebouncers[index]) {

            clearTimeout(weightDebouncers[index]);

        }

        weightDebouncers[index] = setTimeout(() => {

            recalcExerciseFromWeight(index, value);

        }, 400); // 400ms delay after last keystroke

    }



    // NAVIGATION FUNCTIONS

function showSection(sectionId, replaceState = false) {

    // Hide all sections

    document.querySelectorAll('.section').forEach(section => {

        section.classList.remove('active');

    });

    

    // Show target section

    const targetSection = document.getElementById(`${sectionId}-section`);

    if (targetSection) {

        targetSection.classList.add('active');

    }

  

    if (sectionId !== 'workout') {

        releaseWakeLock();

    } else if (currentWorkout && currentWorkout.exercises && currentWorkout.exercises.length > 0) {

        requestWakeLock();

    }

    

    // Update navigation active states

    document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));

    let navItem;

    if (sectionId === 'dashboard') navItem = document.querySelector('.nav-item[onclick*="dashboard"]');

    else navItem = document.querySelector(`.nav-item[onclick*="${sectionId}"]`);

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

    

    if (window.innerWidth <= 1100) navMenu.classList.remove('active');

    

    // Refresh section data

    switch(sectionId) {

        case 'dashboard': updateDashboard(); updateLongevityScoreDisplay(); break;

        case 'workout': 

            // Ensure currentWorkout is set from saved if missing

            if (!currentWorkout && savedWorkout) {

                currentWorkout = savedWorkout;

                requestWakeLock(); 

            }

            // Only re-render if the container is empty or signature changed (updateTodaysWorkout already checks signature)

            const container = document.getElementById('exerciseList');

            if (!container || container.innerHTML.trim() === '') {

                updateTodaysWorkout();

            } else {

                // Still update header even if list stays

                updateWorkoutHeader();

                // But also force a signature sync if needed

                const newSignature = currentWorkout ? JSON.stringify(currentWorkout.exercises.map(e => e.id)) : null;

                if (window.lastWorkoutSignature !== newSignature) {

                    updateTodaysWorkout();

                } else {

                    updateWorkoutHeader();

                }

            }

            break;

        case 'library': renderLibrary(); break;

        case 'history': currentHistoryFilter = 'all'; updateWorkoutHistory(); break;

        case 'progress': updateProgressCharts(); break;

        case 'recovery': updateRecoverySection(); break;

        case 'settings': loadSettingsForm(); break;

    }

    

    sessionStorage.setItem('lastSection', sectionId);

    

    // P5P1: opening a page always starts at the very top of that page.

    // Instant (no smooth) so the new page reads as a clean native transition.

    window.scrollTo(0, 0);

    document.documentElement.scrollTop = 0;

    document.body.scrollTop = 0;

    

    // ========== INFINITE NAVIGATION STACK (never loses history) ==========
