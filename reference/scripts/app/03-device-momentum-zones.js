// ============================================================
// reference/scripts/app/03-device-momentum-zones.js
// Extracted from reference/scripts/app.js
// Original lines: 1994-3781  (1788 lines)
// Section: Device ID, sync, testing helpers, momentum, zones
// ============================================================

// ==================== DEVICE ID (add after workoutData) ====================

function getDeviceId() {

  let id = localStorage.getItem('deviceId');

  if (id) return id;



  // Generate a new UUID v4

  id = generateUUID();

  localStorage.setItem('deviceId', id);

  return id;

}

      

// Ultimate device detection â€“ 2025 Edition with full GPU & iPad version fix

async function getDeviceInfo() {

  const info = {

    browser: { name: '', version: '' },

    os: { name: '', version: '' },

    device: { model: '', vendor: '', type: '' },

    hardware: {

      cores: navigator.hardwareConcurrency || null,

      ram: navigator.deviceMemory || null,

      gpu: 'Software',

      screen: `${screen.width}x${screen.height} (${window.devicePixelRatio}x)`,

      touchPoints: navigator.maxTouchPoints || 0

    },

    context: {

      net: '',

      battery: '',

      tz: Intl.DateTimeFormat().resolvedOptions().timeZone.split('/').pop(),

      lang: navigator.language

    }

  };



  // --- 1. CLIENT HINTS (Chromium) ---

  if (navigator.userAgentData) {

    try {

      const hints = await navigator.userAgentData.getHighEntropyValues([

        'platform', 'platformVersion', 'model', 'fullVersionList'

      ]);



      // OS

      if (hints.platform === 'Windows') {

        const major = parseInt(hints.platformVersion?.split('.')[0] || '0');

        info.os.name = 'Windows';

        info.os.version = major >= 13 ? '11' : '10';

      } else {

        info.os.name = hints.platform || '';

        info.os.version = hints.platformVersion || '';

      }



      info.device.model = hints.model || '';

      if (hints.fullVersionList?.length > 0) {

        const preferred = hints.fullVersionList.find(b => !b.brand.includes('Not')) || hints.fullVersionList[0];

        info.browser.name = preferred.brand;

        info.browser.version = preferred.version;

      }

      info.device.type = navigator.userAgentData.mobile ? 'mobile' : 'desktop';

    } catch (e) {

      // fall through

    }

  }



  // --- 2. UAPARSER FALLBACK (Safari/Firefox) ---

  if (!info.os.name && typeof UAParser !== 'undefined') {

    const uaRes = new UAParser().getResult();

    info.os.name = uaRes.os.name;

    info.os.version = uaRes.os.version;

    info.browser.name = uaRes.browser.name;

    info.browser.version = uaRes.browser.major;

    info.device.model = uaRes.device.model;

    info.device.vendor = uaRes.device.vendor;

    info.device.type = uaRes.device.type;

  }



  // --- 3. IPAD CORRECTION (critical) ---

  const isIpad = (/Macintosh/.test(navigator.userAgent) || navigator.platform === 'MacIntel') 

                 && navigator.maxTouchPoints > 1;

  if (isIpad) {

    info.os.name = 'iPadOS';

    info.device.model = 'iPad';

    info.device.type = 'tablet';

    // Attempt to extract iOS version (may fail in desktop mode, that's okay)

    const iosMatch = navigator.userAgent.match(/OS (\d+)_(\d+)/) || 

                     navigator.userAgent.match(/iPhone OS (\d+)_(\d+)/);

    if (iosMatch) {

      info.os.version = `${iosMatch[1]}.${iosMatch[2]}`;

    } else {

      // Version hidden â€“ leave empty, will show as "iPadOS"

    }

  }



  // --- 4. DEVICE TYPE REFINEMENT ---

  if (!info.device.type) {

    if (/Mobi|Android|iPhone/i.test(navigator.userAgent)) {

      info.device.type = 'mobile';

    } else if (/Tablet|iPad|PlayBook/i.test(navigator.userAgent) || 

               (info.hardware.touchPoints > 1 && screen.width > 700)) {

      info.device.type = 'tablet';

    } else {

      info.device.type = 'desktop';

    }

  }



  // --- 5. GPU DETECTION WITH SMART CLEANUP (preserve model numbers) ---

  try {

    const canvas = document.createElement('canvas');

    const gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');

    if (gl) {

      const debugInfo = gl.getExtension('WEBGL_debug_renderer_info');

      if (debugInfo) {

        let raw = gl.getParameter(debugInfo.UNMASKED_RENDERER_WEBGL);

        // Clean ANGLE wrapper and driver noise, but keep the full model name

        info.hardware.gpu = raw

          .replace(/ANGLE \((.*)\)/, '$1')               // Extract from ANGLE(...)

          .replace(/Direct3D.*/, '')                     // Remove Direct3D version

          .replace(/vs_\d+_\d+|ps_\d+_\d+/, '')          // Remove shader models

          .replace(/\s+/g, ' ')                           // Normalize spaces

          .trim();

        // If after cleaning we have a very long string, optionally limit length

        if (info.hardware.gpu.length > 60) {

          info.hardware.gpu = info.hardware.gpu.substring(0, 60) + 'â€¦';

        }

      }

    }

  } catch (e) {

    // GPU detection failed

  }



  // --- 6. NETWORK INFO ---

  if (navigator.connection) {

    info.context.net = navigator.connection.effectiveType || '';

    if (navigator.connection.saveData) {

      info.context.net += ' (Eco)';

    }

  }



  // --- 7. BATTERY INFO ---

  if (navigator.getBattery) {

    try {

      const battery = await navigator.getBattery();

      info.context.battery = `${Math.round(battery.level * 100)}%${battery.charging ? 'âš¡' : ''}`;

    } catch (e) {

      info.context.battery = '';

    }

  }



  // --- 8. BUILD THE FINAL STRING ---

  const parts = [];



  // Device name: vendor + model, or "PC" for desktops

  let deviceName = info.device.model || (info.device.type === 'desktop' ? 'PC' : 'Unknown');

  if (info.device.vendor && !deviceName.includes(info.device.vendor)) {

    deviceName = `${info.device.vendor} ${deviceName}`;

  }

  parts.push(deviceName);



  // OS (version may be empty for iPadOS in desktop mode)

  let osStr = info.os.name;

  if (info.os.version) osStr += ` ${info.os.version}`;

  parts.push(`(${osStr})`);



  // Browser

  parts.push(`[${info.browser.name} ${info.browser.version}]`);



  // Device type & screen

  parts.push(info.device.type);

  parts.push(info.hardware.screen);



  // Hardware metadata (full GPU, cores, RAM)

  const hw = [];

  if (info.hardware.cores) hw.push(`${info.hardware.cores}c`);

  if (info.hardware.ram) hw.push(`${info.hardware.ram}GB`);

  if (info.hardware.gpu && info.hardware.gpu !== 'Software') {

    hw.push(info.hardware.gpu);

  }

  if (hw.length > 0) parts.push(`{${hw.join('|')}}`);



  // Context (network, battery, timezone)

  const ctx = [];

  if (info.context.net) ctx.push(info.context.net);

  if (info.context.battery) ctx.push(info.context.battery);

  ctx.push(info.context.tz);

  parts.push(`@ ${ctx.join('; ')}`);



  return parts.join(' ').replace(/\s+/g, ' ');

}



function generateUUID() {

  // Use crypto.randomUUID() if available (modern browsers)

  if (typeof crypto !== 'undefined' && crypto.randomUUID) {

    return crypto.randomUUID();

  }



  // Fallback: manual UUID v4 generation using crypto.getRandomValues()

  // Format: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx

  const uuidTemplate = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx';

  const bytes = new Uint8Array(16);

  crypto.getRandomValues(bytes);



  // Set version (4) and variant (10xx) bits

  bytes[6] = (bytes[6] & 0x0f) | 0x40; // version 4

  bytes[8] = (bytes[8] & 0x3f) | 0x80; // variant 1 (10xx)



  const hex = Array.from(bytes).map(b => b.toString(16).padStart(2, '0')).join('');

  return uuidTemplate.replace(/[xy]/g, (c, i) => {

    const pos = i - (i > 8 ? 1 : 0) - (i > 13 ? 1 : 0) - (i > 18 ? 1 : 0) - (i > 23 ? 1 : 0);

    return hex[pos];

  });

}





      

// ==================== SYNC EXISTING USER DATA ====================

async function syncExistingUserData() {

  const SCRIPT_URL = ''; /* GAS removed â€” data stays on device */



  if (workoutData.syncedToBackend) {

    return;

  }

  const deviceId = getDeviceId();

  const basePayload = {

    deviceId: deviceId,

    deviceName: getDeviceInfo(),

    timestamp: new Date().toISOString(),

    name: workoutData.user?.name || 'User'

  };



  let syncSuccess = true;



  // 1. Sync Profile

  if (workoutData.user && Object.keys(workoutData.user).length > 0) {

    const profilePayload = {

      ...basePayload,

      dataType: 'profile',

      data: workoutData.user

    };

    try {

      const response = await /* GAS removed */ (function(){})( {

        method: 'POST',

        mode: 'no-cors', // temporarily keep as no-cors; we'll see later

        headers: { 'Content-Type': 'text/plain' },

        body: JSON.stringify(profilePayload)

      });

    } catch (err) {

      syncSuccess = false;

    }

  } else {

  }



  // 2. Sync Workouts (with 1-second throttle)

  if (workoutData.workouts && workoutData.workouts.length) {

    for (const workout of workoutData.workouts) {

      await new Promise(resolve => setTimeout(resolve, 1000));

      const workoutPayload = {

        ...basePayload,

        dataType: 'workout',

        data: workout

      };

      try {

        await /* GAS removed */ (function(){})( {

          method: 'POST',

          mode: 'no-cors',

          headers: { 'Content-Type': 'text/plain' },

          body: JSON.stringify(workoutPayload)

        });

      } catch (err) {

        syncSuccess = false;

      }

    }

  } else {

  }



  // 3. Sync Test Results

  if (workoutData.exercises) {

    const testResults = [];

    Object.entries(workoutData.exercises).forEach(([exId, exData]) => {

      if (exData.history) {

        exData.history.forEach(entry => {

          if (entry.isTest) {

            testResults.push({

              exerciseId: exId,

              weight: entry.weight,

              reps: entry.reps[0],

              estimated1RM: entry.estimated1RM,

              date: entry.date,

              notes: entry.notes || ''

            });

          }

        });

      }

    });



    for (const test of testResults) {

      await new Promise(resolve => setTimeout(resolve, 1000));

      const testPayload = {

        ...basePayload,

        dataType: 'test',

        data: test

      };

      try {

        await /* GAS removed */ (function(){})( {

          method: 'POST',

          mode: 'no-cors',

          headers: { 'Content-Type': 'text/plain' },

          body: JSON.stringify(testPayload)

        });

      } catch (err) {

        syncSuccess = false;

      }

    }

  } else {

  }



  // 4. Sync Menstrual Data

  if (workoutData.user?.menstrual?.lastPeriodStart) {

    const menstrualPayload = {

      ...basePayload,

      dataType: 'menstrual',

      data: {

        phase: getCurrentCyclePhase(),

        symptoms: "Historical Data Sync",

        notes: ''

      }

    };

    try {

      await /* GAS removed */ (function(){})( {

        method: 'POST',

        mode: 'no-cors',

        headers: { 'Content-Type': 'text/plain' },

        body: JSON.stringify(menstrualPayload)

      });

    } catch (err) {

      syncSuccess = false;

    }

  } else {

  }



  if (syncSuccess) {

    workoutData.syncedToBackend = true;

    saveToLocalStorage();

  } else {

  }

}

      

      

// Fatigue decay rate per day (halfâ€‘life â‰ˆ 2 days)

const FATIGUE_DECAY_RATE = 0.35;

      

// Fatigue threshold: a muscle is considered ready if its fatigue < READINESS_THRESHOLD

const READINESS_THRESHOLD = 0.4;   // fast+slow are fractions 0-1, combined max = 2



/**

 * Apply exponential decay to fatigue for all muscles based on days passed.

 * Uses muscleâ€‘specific fast/slow decay rates.

 * @param {number} days - Number of days elapsed since last decay.

 */

function decayFatigue(days) {

    if (days <= 0) return;

    const hours = days * 24;  // convert to hours for consistency with updateMuscleFatigue



    for (let muscle in workoutData.muscleFatigue) {

        if (!workoutData.muscleFatigue.hasOwnProperty(muscle)) continue;

        const f = workoutData.muscleFatigue[muscle];

        if (!f || (f.fast === 0 && f.slow === 0)) continue;



        const decay = MUSCLE_DECAY[muscle] || DEFAULT_DECAY;

        const Î»_fast = decay.fast;

        const Î»_slow = decay.slow;



        const df = Math.exp(-Î»_fast * hours);

        const ds = Math.exp(-Î»_slow * hours);



        f.fast *= df;

        f.slow *= ds;



        // Clamp very small values to zero

        if (f.fast < 0.01) f.fast = 0;

        if (f.slow < 0.01) f.slow = 0;

    }

}



/**

 * Ensure every muscle has an entry in muscleFatigue (initialised to 0).

 * Call this after loading data and whenever a new muscle is added.

 */

function initializeMuscleFatigue() {

    if (!workoutData.muscleFatigue) workoutData.muscleFatigue = {};

    getAllMuscleGroups().forEach(muscle => {

        const name = muscle.name;

        if (!workoutData.muscleFatigue[name]) {

            workoutData.muscleFatigue[name] = { fast: 0, slow: 0, lastUpdate: null };

        } else if (typeof workoutData.muscleFatigue[name] === 'number') {

            // Convert old scalar to object

            const oldValue = workoutData.muscleFatigue[name];

            workoutData.muscleFatigue[name] = { fast: oldValue, slow: oldValue, lastUpdate: null };

        }

    });

}

      

/**

 * Check the last decay timestamp and apply decay for the elapsed days.

 * Call this before any operation that relies on current fatigue values.

 */

function applyFatigueDecay() {

    const now = new Date();

    if (!workoutData.lastFatigueDecay) {

        // First time â€“ set it to now and do nothing (no decay needed)

        workoutData.lastFatigueDecay = now.toISOString();

        saveToLocalStorage();

        return;

    }



    const last = new Date(workoutData.lastFatigueDecay);

    const diffMs = now - last;

    const diffDays = diffMs / (1000 * 60 * 60 * 24);



    if (diffDays >= 0.1) {   // at least 0.1 day (~2.4 hours) to avoid tiny decays

        decayFatigue(diffDays);

        workoutData.lastFatigueDecay = now.toISOString();

        saveToLocalStorage();

    }

}

      

/**

 * Calculate fatigue contribution from a completed workout and add to muscleFatigue.

 * @param {Object} workout - The completed workout object.

 */

function updateFatigueAfterWorkout(workout) {

    if (!workout || !workout.exercises) return;



    workout.exercises.forEach(exercise => {

        // Skip if exercise was skipped or no actual performance logged

        if (!exercise.actual || exercise.skipped) return;

        const actual = exercise.actual;

        if (exercise.noFatigue) return; // mobility/athletic work: no muscle fatigue accrual

        if (!actual.sets || actual.sets === 0) return;



        // Determine the muscles involved

        const muscles = exercise.muscleGroup || [];

        if (muscles.length === 0) return;



        // Calculate stress per set

        let stressPerSet = 0;

        if (exercise.prescriptionType === 'time') {

            if (actual.durations && actual.durations.length > 0) {

                const totalDuration = actual.durations.reduce((a, b) => a + b, 0);

                const avgDuration = totalDuration / actual.durations.length;

                // RPE scaling: if actual.rpe is an array, take average

                let rpe = actual.rpe;

                if (Array.isArray(rpe)) rpe = rpe.reduce((a,b)=>a+b,0) / rpe.length;

                const rpeFactor = (rpe || 7) / 10;

                stressPerSet = avgDuration * rpeFactor;

            }

        } else {

            if (actual.reps && actual.reps.length > 0) {

                const avgReps = actual.reps.reduce((a, b) => a + b, 0) / actual.reps.length;

                let rpe = actual.rpe;

                if (Array.isArray(rpe)) rpe = rpe.reduce((a,b)=>a+b,0) / rpe.length;

                const rpeFactor = (rpe || 7) / 10;

                stressPerSet = actual.weight * avgReps * rpeFactor;

            }

        }



        // Total stress = stress per set Ã— number of sets performed

        const totalStress = stressPerSet * actual.sets;

        const clampedS = Math.min(1, Math.max(0, totalStress));



        // Determine fast/slow fractions based on RPE (higher RPE = more fast fatigue)

        let rpe = actual.rpe;

        if (Array.isArray(rpe)) rpe = rpe.reduce((a,b)=>a+b,0) / rpe.length;

        const fastFrac = (rpe || 7) > 7 ? 0.7 : 0.3;

        const slowFrac = 1 - fastFrac;



        const loadDist = getLoadDistribution(exercise);



        // Helper to add stress to a muscle's fast/slow

        const addStressToMuscle = (muscle, stressFraction) => {

            if (!workoutData.muscleFatigue[muscle]) {

                workoutData.muscleFatigue[muscle] = { fast: 0, slow: 0, lastUpdate: null };

            }

            const muscleFat = workoutData.muscleFatigue[muscle];

            muscleFat.fast += clampedS * fastFrac * stressFraction;

            muscleFat.slow += clampedS * slowFrac * stressFraction;

        };



        if (Object.keys(loadDist).length === 0) {

            // Equal distribution

            const factor = 1 / muscles.length;

            muscles.forEach(muscle => addStressToMuscle(muscle, factor));

        } else {

            // Use specified distribution

            for (let muscle in loadDist) {

                if (loadDist.hasOwnProperty(muscle)) {

                    addStressToMuscle(muscle, loadDist[muscle]);

                }

            }

        }

    });



    // Save the updated state (using Dexie via saveToLocalStorage)

    saveToLocalStorage();

}

      

/**

 * Update the recent exercises list for each muscle after a workout.

 * @param {Object} workout - The completed workout object.

 * @param {number} [maxPerMuscle=3] - Maximum number of recent exercises to keep per muscle.

 */

function updateRecentExercises(workout, maxPerMuscle = 3) {

    if (!workout || !workout.exercises) return;



    workout.exercises.forEach(exercise => {

        if (!exercise.actual || exercise.skipped) return;

        const muscles = exercise.muscleGroup || [];

        if (muscles.length === 0) return;

        if (exercise.noFatigue) return; // drills never occupy strength rotation slots



        const exerciseId = exercise.id;



        muscles.forEach(muscle => {

            if (!workoutData.muscleRecentExercises[muscle]) {

                workoutData.muscleRecentExercises[muscle] = [];

            }

            const list = workoutData.muscleRecentExercises[muscle];

            // Add the exercise ID to the front (most recent)

            list.unshift(exerciseId);

            // Remove duplicates

            const unique = [];

            list.forEach(id => {

                if (!unique.includes(id)) unique.push(id);

            });

            // Keep only the last maxPerMuscle

            workoutData.muscleRecentExercises[muscle] = unique.slice(0, maxPerMuscle);

        });

    });



    saveToLocalStorage();

}

      

/**

 * Returns an array of muscle objects that are currently ready (total fatigue < threshold),

 * sorted by last worked (oldest first) to prioritise neglect.

 * @param {Array<string>} [ignoreMuscles] - Optional list of muscle names to exclude.

 * @returns {Array<Object>} Array of muscle objects (from getAllMuscleGroups) that are ready.

 */

function getReadyMuscles(ignoreMuscles = []) {

    applyFatigueDecay(); // Ensure fatigue is up to date



    const allMuscles = getAllMuscleGroups();

    const readyMuscles = [];



    allMuscles.forEach(muscle => {

        const name = muscle.name;

        if (ignoreMuscles.includes(name)) return;



        const fatigueObj = workoutData.muscleFatigue[name];

        let totalFatigue = 0;

        if (fatigueObj) {

            if (typeof fatigueObj === 'number') {

                totalFatigue = fatigueObj;

            } else {

                totalFatigue = (fatigueObj.fast || 0) + (fatigueObj.slow || 0);

            }

        }

        if (totalFatigue < READINESS_THRESHOLD) {

            readyMuscles.push(muscle);

        }

    });



    // Sort by last trained (oldest first) to prioritize most neglected

    readyMuscles.sort((a, b) => {

        const lastA = muscleLastTrained[a.name];

        const lastB = muscleLastTrained[b.name];

        if (!lastA && !lastB) return a.display.localeCompare(b.display);

        if (!lastA) return -1; // never trained comes first

        if (!lastB) return 1;

        return new Date(lastA) - new Date(lastB);

    });



    return readyMuscles;

}

      

      // ========== NEW HELPERS FOR TESTING & PROGRESSION ==========

const REP_TARGET = 18;                // target reps for hypertrophy

const INTENSITY_PERCENT = 0.6;        // 60% of 1RM for starting weight

const ROUNDING_INCREMENT = 2.5;        // round weights to nearest 2.5 lbs



function roundToNearest(value, increment) {

    return Math.round(value / increment) * increment;

}

      

// ==================== MOMENTUM STEERING ====================

function updateMomentumBias(tapType) {

    // tapType: 'bigger' or 'stronger'

    const bias = workoutData.user.momentumBias;

    if (!bias) return;

    

    // Decay first (apply decay to existing value)

    let decayed = bias.value * (1 - bias.decayRate);

    

    // Apply nudge

    let nudge = (tapType === 'stronger') ? 0.35 : -0.35;

    let newValue = decayed + nudge;

    

    // Clamp to -1..1

    newValue = Math.max(-1.0, Math.min(1.0, newValue));

    

    bias.value = newValue;

    bias.lastUpdated = new Date().toISOString();

    bias.lastTap = tapType;

    

    saveToLocalStorage();

    console.log(`Momentum bias updated: ${newValue} (${tapType})`);

}



function decayMomentumBias() {

    const bias = workoutData.user.momentumBias;

    if (!bias || !bias.lastUpdated) return;

    const now = new Date();

    const last = new Date(bias.lastUpdated);

    const daysSince = (now - last) / (1000*60*60*24);

    if (daysSince >= 1) {

        // Decay once per day (simple)

        bias.value = bias.value * (1 - bias.decayRate);

        bias.lastUpdated = now.toISOString();

        saveToLocalStorage();

    }

}



function applySteeringToPrescription(exercise, baseWeight, baseReps, baseSets) {

    const bias = workoutData.user.momentumBias?.value || 0;

    // Weight: Â±2.5 lbs per 0.35 bias (max Â±7.5 lbs)

    let weightDelta = bias * 7.5;

    let newWeight = baseWeight + weightDelta;

    newWeight = Math.max(20, Math.round(newWeight / 2.5) * 2.5);

    

    // Reps: bias positive (stronger) reduces reps by up to 2, negative increases by up to 2

    let repDelta = -bias * 2;

    let newReps = baseReps + repDelta;

    newReps = Math.min(20, Math.max(1, Math.round(newReps)));

    

    // Sets: bias positive increases sets by up to 1, negative decreases by up to 1

    let setDelta = bias * 1;

    let newSets = baseSets + setDelta;

    newSets = Math.min(7, Math.max(2, Math.round(newSets)));

    

    return { weight: newWeight, reps: newReps, sets: newSets };

}



function getOrCreateExerciseRecord(exerciseId) {

    if (!workoutData.exercises[exerciseId]) {

        workoutData.exercises[exerciseId] = {

            history: [],

            tested1RM: null,

            testDate: null,

            testReps: null,

            testWeight: null,

            derivedFrom: null,

            strengthRatio: null

        };

    }

    // Ensure recursive fields are present

    initExerciseRecursiveFields(exerciseId);

    return workoutData.exercises[exerciseId];

}

      

/**

 * Returns the load distribution for an exercise (muscle â†’ load factor).

 * If the exercise has a loadDistribution property, uses it.

 * Otherwise, computes an equal distribution from its muscleGroup.

 * @param {Object} exercise - The exercise object.

 * @returns {Object} Map of muscle name to load factor (sum = 1).

 */

function getLoadDistribution(exercise) {

    // If already present and valid (sum close to 1), use it

    if (exercise.loadDistribution && typeof exercise.loadDistribution === 'object') {

        const values = Object.values(exercise.loadDistribution);

        const sum = values.reduce((a, b) => a + b, 0);

        if (Math.abs(sum - 1) < 0.01) {

            return exercise.loadDistribution;

        }

    }



    // Otherwise compute equal distribution from muscleGroup

    const muscles = exercise.muscleGroup || [];

    if (muscles.length === 0) return {};



    const factor = 1 / muscles.length;

    const dist = {};

    muscles.forEach(m => dist[m] = factor);

    return dist;

}

      

// ========== ZONE DEFINITIONS ==========

const ZONES = [

    { name: 'Zone 5', repRange: [15, 20], intensity: [0.60, 0.65], primary: 'Hypertrophy' },

    { name: 'Zone 4', repRange: [10, 15], intensity: [0.65, 0.70], primary: 'Hypertrophy' },

    { name: 'Zone 3', repRange: [8, 12],  intensity: [0.70, 0.75], primary: 'Strength/Hypertrophy' },

    { name: 'Zone 2', repRange: [5, 8],   intensity: [0.75, 0.80], primary: 'Strength' },

    { name: 'Zone 1', repRange: [3, 5],   intensity: [0.80, 0.85], primary: 'Strength/Peaking' },

    { name: 'Zone 0', repRange: [1, 3],   intensity: [0.85, 1.00], primary: 'Max Strength' }

];



function getZoneFromIntensity(intensity) {

    for (let i = 0; i < ZONES.length; i++) {

        if (intensity >= ZONES[i].intensity[0] && intensity <= ZONES[i].intensity[1]) {

            return ZONES[i];

        }

    }

    // If intensity > 1.0, return Zone 0; if < 0.6, return Zone 5 (should not happen)

    return intensity > 1.0 ? ZONES[ZONES.length-1] : ZONES[0];

}



function getMidIntensityForZone(zoneIndex) {

    // zoneIndex: 0-based index of ZONES array

    const zone = ZONES[zoneIndex];

    return (zone.intensity[0] + zone.intensity[1]) / 2;

}



      

// =====================================================================

// MUSCLE FIBER PROFILES â€” fiber-aware load windows for every muscle

// =====================================================================

// The reps/sets engine decides HOW MANY reps and sets you do. The muscle's

// fiber profile then decides WHAT WEIGHT those reps must be performed with:

//   min  = lowest %1RM that still forces adaptation (below it, slow-twitch

//          fibers simply "cruise" through the movement without growing)

//   max  = highest %1RM the muscle should ever be loaded to (above it,

//          fast-twitch form shatters or fragile structures take damage)

// Fast-twitch dominant muscles have a WIDE window (light high-rep work,

// heavy low-rep work) and collapse hard between sets. Slow-twitch dominant

// muscles have a NARROW, HIGH window (they need constant tension near the

// floor, and heavy low-rep work is wasted on them).

//

// The 11 Foundational Physical Fitness Components coverage map:

//   1. Cardiorespiratory Endurance -> cardio_condition group + Cardio Endurance day

//   2. Muscular Strength           -> Zone 0-2 prescriptions (this engine)

//   3. Muscular Endurance          -> Zone 4-5 prescriptions + Athletic Day + Conditioning

//   4. Flexibility                 -> stretching_static cooldown every session + Mobility day

//   5. Body Composition            -> combined resistance + conditioning programming

//   6. Power                       -> athletic_power drills + Full Body Strength+Power day

//   7. Agility                     -> athletic_agility drills + Athletic Day

//   8. Balance                     -> athletic_balance drills + Push day finisher

//   9. Coordination                -> athletic_coordination drills + Pull day finisher

//  10. Speed                       -> athletic_speed drills + Legs & Speed day

//  11. Reaction Time               -> athletic_reaction drills + Athletic Day

const MUSCLE_FIBER_PROFILES = {

    // ---------- Major muscle groups ----------

    quads:            { fiber: 'mixed', min: 0.52, max: 0.82, note: 'Mixed 50/50 split. High reps introduce massive lung and systemic fatigue.' },

    hamstrings:       { fiber: 'fast',  min: 0.45, max: 0.85, note: 'Fast-twitch. Highly explosive layout; high reps must stay light.' },

    glutes:           { fiber: 'mixed', min: 0.55, max: 0.85, note: 'Strongest muscle group. High reps create massive cardiovascular demand.' },

    adductors:        { fiber: 'slow',  min: 0.55, max: 0.75, note: 'Require smooth transitions; avoid jerky movements (groin risk).' },

    abductors:        { fiber: 'slow',  min: 0.50, max: 0.70, note: 'Outer hip stabilizers. Best with high-rep lateral cable or band work.' },

    calves:           { fiber: 'fast',  min: 0.50, max: 0.80, note: 'Gastrocnemius (diamond upper calf). Best with straight-leg raises.' },

    soleus:           { fiber: 'slow',  min: 0.65, max: 0.75, note: 'Flat/deep lower calf. Incredibly high endurance; bent-knee work above 65%.' },

    tibialis:         { fiber: 'slow',  min: 0.60, max: 0.75, note: 'Shin muscle. Hard to injure; keep constant tension for a high burn.' },

    chest:            { fiber: 'mixed', min: 0.50, max: 0.84, note: 'Mixed-fiber pectoral; productive across the whole rep spectrum.' },

    lats:             { fiber: 'mixed', min: 0.55, max: 0.85, note: 'Wide window; heavy pulls and high-rep pulldowns both work.' },

    back:             { fiber: 'mixed', min: 0.55, max: 0.85, note: 'General mid-back window; rows cover it well.' },

    traps:            { fiber: 'mixed', min: 0.55, max: 0.80, note: 'Responds to heavy shrugs plus long-duration holds.' },

    shoulders:        { fiber: 'mixed', min: 0.50, max: 0.80, note: 'Anterior delt window (overhead pressing).' },

    front_delts:      { fiber: 'mixed', min: 0.50, max: 0.80, note: 'Anterior delt window (overhead pressing).' },

    shoulders_lateral:{ fiber: 'slow',  min: 0.50, max: 0.65, note: 'Side delts are slow-twitch; a too-light weight simply cruises.' },

    rear_delts:       { fiber: 'fast',  min: 0.45, max: 0.75, note: 'Fast-twitch. Power collapses from set 1 to set 2; keep later sets honest.' },

    biceps:           { fiber: 'mixed', min: 0.50, max: 0.80, note: 'Mixed fibers; strict curls across the window.' },

    triceps:          { fiber: 'fast',  min: 0.45, max: 0.85, note: 'Fast-twitch. Drops drastically from set 1 to set 2 â€” respect the floor.' },

    forearms:         { fiber: 'slow',  min: 0.60, max: 0.75, note: 'Endurance-rich; constant tension and high reps work best.' },

    core:             { fiber: 'slow',  min: 0.60, max: 0.75, note: 'Abs are slow-twitch; too-light weight cruises without growth.' },

    obliques:         { fiber: 'mixed', min: 0.55, max: 0.75, note: 'Avoid heavy low-reps to prevent widening the waistline.' },

    erectors:         { fiber: 'slow',  min: 0.55, max: 0.85, note: 'Lumbar disc risk. Never train high-reps to absolute structural failure.' },

    hip_flexors:      { fiber: 'slow',  min: 0.55, max: 0.70, note: 'High-rep controlled knee drives; low injury tolerance.' },

    serratus:         { fiber: 'mixed', min: 0.55, max: 0.75, note: 'Scapular stabilizer; protraction with full reach.' },

    // ---------- Longevity & deep stabilizers (safety-first: narrow windows) ----------

    neck:             { fiber: 'slow', min: 0.60, max: 0.70, note: 'Spine isolation tax â€” prioritize impeccable static posture over load.' },

    deep_neck:        { fiber: 'slow', min: 0.55, max: 0.65, note: 'Chin-tuck quality over load.' },

    levator_scap:     { fiber: 'slow', min: 0.55, max: 0.70, note: 'Gentle loads; stretch-biased work.' },

    rhomboids:        { fiber: 'slow', min: 0.55, max: 0.75, note: 'Endurance postural muscle; squeeze and hold.' },

    teres:            { fiber: 'mixed', min: 0.55, max: 0.80, note: 'Lat assistant; moderate window.' },

    infraspinatus:    { fiber: 'slow', min: 0.55, max: 0.70, note: 'Rotator cuff â€” strict form with light bands or cables.' },

    supraspinatus:    { fiber: 'slow', min: 0.50, max: 0.65, note: 'Cuff â€” thumb-up raises only, never heavy.' },

    subscapularis:    { fiber: 'slow', min: 0.55, max: 0.70, note: 'Cuff â€” controlled internal rotation work.' },

    brachialis:       { fiber: 'mixed', min: 0.50, max: 0.80, note: 'Elbow flexor; hammer and reverse curls.' },

    brachioradialis:  { fiber: 'mixed', min: 0.50, max: 0.75, note: 'Forearm flexor; reverse curls and neutral grips.' },

    anconeus:         { fiber: 'slow', min: 0.55, max: 0.70, note: 'Tiny elbow stabilizer; light high-rep work.' },

    supinator:        { fiber: 'slow', min: 0.55, max: 0.70, note: 'Rotation work, light and strict.' },

    pronator:         { fiber: 'slow', min: 0.55, max: 0.70, note: 'Rotation work, light and strict.' },

    pec_minor:        { fiber: 'mixed', min: 0.50, max: 0.75, note: 'Deep pec layer; protraction and depression.' },

    coracobrach:      { fiber: 'mixed', min: 0.50, max: 0.75, note: 'Small shoulder flexor; moderate reps.' },

    popliteus:        { fiber: 'slow', min: 0.50, max: 0.65, note: 'Knee unlocker â€” tiny controlled loads only.' },

    peroneus_tertius: { fiber: 'slow', min: 0.55, max: 0.70, note: 'Ankle eversion; band work with slow tempo.' },

    articularis_genus:{ fiber: 'slow', min: 0.50, max: 0.65, note: 'Knee-capsule protector; ultra-light extensions.' },

    multifidus:       { fiber: 'slow', min: 0.55, max: 0.80, note: 'Spinal stabilizer â€” never to structural failure.' },

    transverse:       { fiber: 'slow', min: 0.55, max: 0.70, note: 'Deep core; bracing quality over load.' },

    quadratus_plantae:{ fiber: 'slow', min: 0.50, max: 0.70, note: 'Foot intrinsic; short-foot drills.' },

    // ---------- Hands & feet ----------

    hand_lumbricals:  { fiber: 'slow', min: 0.50, max: 0.70, note: 'High-rep finger flexion/extension.' },

    hand_interossei:  { fiber: 'slow', min: 0.50, max: 0.70, note: 'Spreading and crushing band work.' },

    thenar:           { fiber: 'slow', min: 0.50, max: 0.70, note: 'Thumb work; pinch drills.' },

    foot_intrinsics:  { fiber: 'slow', min: 0.50, max: 0.70, note: 'Short-foot drills, high reps.' },

    foot_interossei:  { fiber: 'slow', min: 0.50, max: 0.70, note: 'Toe splay work.' },

    abductor_hallucis:{ fiber: 'slow', min: 0.50, max: 0.70, note: 'Arch support; big-toe abduction.' },

    flexor_brevis:    { fiber: 'slow', min: 0.50, max: 0.70, note: 'Towel curls and marble pickups.' }

};



function getMuscleFiberProfile(muscleName) {

    if (!muscleName) return null;

    return MUSCLE_FIBER_PROFILES[muscleName] || null;

}



// Working window (%1RM) for an exercise = INTERSECTION of its muscles' windows.

//   low  = strictest (highest) floor â€” below it some slow-twitch muscle just cruises

//   high = strictest (lowest) ceiling â€” above it a fragile muscle exceeds safe load

function getExerciseFiberWindow(exercise) {

    const muscles = exercise.muscleGroup || exercise.muscles || [];

    let low = 0, high = 1;

    const notes = [];

    for (const m of muscles) {

        const p = getMuscleFiberProfile(m);

        if (!p) continue;

        low = Math.max(low, p.min);

        high = Math.min(high, p.max);

        notes.push(p.note);

    }

    if (!(high > 0) || high > 1) high = 0.85;

    if (!(low > 0)) low = 0.45;

    if (low >= high) {

        // Conflicting windows: settle on a narrow safe band around the midpoint

        const mid = (low + high) / 2;

        low = Math.max(0.45, mid - 0.05);

        high = Math.min(0.95, mid + 0.05);

    }

    return { low, high, note: notes[0] || '' };

}



// Epley: the %1RM that allows a given number of reps

function epleyIntensityForReps(reps) {

    return 1 / (1 + Math.max(1, reps) / 30);

}



// Convert a rep prescription into the fiber-aware weight range (lbs).

// The rep/sets curve decides the target; the fiber window clamps it so the

// suggested weight (min side) and the ceiling (max side) both stay honest.

function computeFiberWeightRange(exercise, oneRM, targetReps) {

    if (!oneRM || oneRM <= 0 || !targetReps || targetReps <= 0) return null;

    const win = getExerciseFiberWindow(exercise);

    const atReps = oneRM * epleyIntensityForReps(targetReps);   // weight that allows targetReps

    const lowW  = oneRM * win.low;                              // minimum stimulus-effective load

    const highW = oneRM * win.high;                             // fiber ceiling load

    let suggested = Math.max(atReps, lowW);                     // never below the floor

    suggested = Math.min(suggested, highW, oneRM * 0.95);       // never above the ceiling

    return {

        min: roundToNearest(suggested, 2.5),

        max: roundToNearest(highW, 2.5),

        lowPct: Math.round(win.low * 100),

        maxPct: Math.round(win.high * 100),

        note: win.note

    };

}



// =========== // ========== STRENGTH RATIOS FOR DERIVED EXERCISES ==========

const STRENGTH_RATIOS = {

    squat: {

        leg_press: 1.2,

        leg_extension: 0.4,

        leg_curl: 0.3

    },

    bench_press: {

        incline_bench: 0.85,

        dumbbell_bench: 0.9,

        shoulder_press: 0.6,

        lateral_raise: 0.2,

        triceps_pushdown: 0.3

    },

    barbell_row: {

        cable_row: 0.9,

        lat_pulldown: 1.0,

        face_pull: 0.3

    },

    deadlift: {

        rdl: 0.7,

        good_morning: 0.5

    }

};



function enhanceExerciseWithDerivation(exercise) {

    for (const [source, targets] of Object.entries(STRENGTH_RATIOS)) {

        if (targets[exercise.id] !== undefined) {

            exercise.derivedFrom = source;

            exercise.strengthRatio = targets[exercise.id];

            break;

        }

    }

    return exercise;

}

      

