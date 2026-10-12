// ============================================================
// reference/scripts/app/12-logging-inputs.js
// Extracted from reference/scripts/app.js
// Original lines: 64706-67073  (2368 lines)
// Section: RPE slider, logging, inputs, modes, notes, sticky buttons
// ============================================================

// ==================== RPE CHECKBOX SLIDER (1-10) ====================

function createRPESlider(index, setIndex, initialValue = null) {

    // Inject required CSS rules (only once) â€“ without !important on background-color

    if (!document.getElementById('rpe-slider-styles')) {

        const style = document.createElement('style');

        style.id = 'rpe-slider-styles';

        style.textContent = `

            .rpe-slider-container {

                display: flex !important;

                flex-direction: row !important;

                flex-wrap: wrap !important;

                gap: 6px !important;

                margin: 8px 0 !important;

                width: 100% !important;

                max-width: 100% !important;

                box-sizing: border-box !important;

            }

            .rpe-slider-container .rpe-checkbox {

                flex: 0 0 auto !important;

                min-width: 44px !important;

                max-width: 60px !important;

                text-align: center !important;

                position: relative !important;

                box-sizing: border-box !important;

            }

            .rpe-slider-container .rpe-checkbox input {

                position: absolute !important;

                opacity: 0 !important;

                width: 0 !important;

                height: 0 !important;

            }

            .rpe-slider-container .rpe-checkbox label {

                display: block !important;

                padding: 10px 0 !important;

                border-radius: 12px !important;

                font-weight: bold !important;

                font-size: 0.9rem !important;

                text-align: center !important;

                cursor: pointer !important;

                transition: all 0.1s ease !important;

                color: white !important;

                text-shadow: 0 0 2px rgba(0,0,0,0.5) !important;

                width: 100% !important;

                box-sizing: border-box !important;

            }

            .rpe-slider-container .rpe-checkbox input:checked + label {

                transform: scale(1.02) !important;

                box-shadow: 0 0 0 2px white, 0 0 0 4px var(--primary, #2563eb) !important;

            }

            @media (max-width: 500px) {

                .rpe-slider-container .rpe-checkbox {

                    min-width: 38px !important;

                }

                .rpe-slider-container .rpe-checkbox label {

                    padding: 6px 0 !important;

                    font-size: 0.8rem !important;

                }

            }

        `;

        document.head.appendChild(style);

    }



    const container = document.createElement('div');

    container.className = 'rpe-slider-container';



    const hiddenInput = document.createElement('input');

    hiddenInput.type = 'hidden';

    hiddenInput.id = `rpe_${index}_${setIndex}`;

    // No default value â€“ start empty

    hiddenInput.value = '';



    // Advanced colour interpolation (light green â†’ yellow â†’ orange â†’ dark red)

    function getRpeColor(rpe) {

        const stops = [

            { rpe: 1, r: 144, g: 238, b: 144 },

            { rpe: 4, r: 255, g: 255, b: 102 },

            { rpe: 7, r: 255, g: 140, b: 0 },

            { rpe: 10, r: 139, g: 0, b: 0 }

        ];

        let lower = stops[0], upper = stops[stops.length - 1];

        for (let i = 0; i < stops.length - 1; i++) {

            if (rpe >= stops[i].rpe && rpe <= stops[i + 1].rpe) {

                lower = stops[i];

                upper = stops[i + 1];

                break;

            }

        }

        const t = (rpe - lower.rpe) / (upper.rpe - lower.rpe);

        const r = Math.round(lower.r + (upper.r - lower.r) * t);

        const g = Math.round(lower.g + (upper.g - lower.g) * t);

        const b = Math.round(lower.b + (upper.b - lower.b) * t);

        return `rgb(${r}, ${g}, ${b})`;

    }



    for (let rpe = 1; rpe <= 10; rpe++) {

        const box = document.createElement('div');

        box.className = 'rpe-checkbox';



        const cb = document.createElement('input');

        cb.type = 'checkbox';

        cb.id = `rpe_cb_${index}_${setIndex}_${rpe}`;

        cb.value = rpe;

        // No checkbox is checked by default

        cb.checked = false;



        const label = document.createElement('label');

        label.htmlFor = cb.id;

        label.textContent = rpe;

        label.style.backgroundColor = getRpeColor(rpe);



        // Slider logic: select all up to clicked value, and update hidden input

        cb.addEventListener('change', (e) => {

            const selected = parseInt(e.target.value);

            for (let i = 1; i <= 10; i++) {

                const other = container.querySelector(`#rpe_cb_${index}_${setIndex}_${i}`);

                if (other) other.checked = (i <= selected);

            }

            hiddenInput.value = selected;

            hiddenInput.dispatchEvent(new Event('change', { bubbles: true }));

        });



        box.appendChild(cb);

        box.appendChild(label);

        container.appendChild(box);

    }



    container.appendChild(hiddenInput);

    return container;

}



// ==================== TOGGLE PERFORMANCE LOGGING (unchanged) ====================

function togglePerformanceLogging(index) {

    const logging = document.getElementById(`performance_${index}`);

    if (!logging) return;

    logging.classList.toggle('active');



    const header = logging.previousElementSibling;

    if (!header) return;



    const icon = header.querySelector('.fa-chevron-down, .fa-chevron-up');

    if (!icon) return;



    if (logging.classList.contains('active')) {

        icon.className = 'fas fa-chevron-up';



        // Wait for the CSS transition to finish (0.4s)

        setTimeout(() => {

            const rect = logging.getBoundingClientRect();

            const offset = getTopOffset(); // fixed navbar height

            const viewportHeight = window.innerHeight;

            const loggerTop = rect.top + window.scrollY;

            const loggerBottom = loggerTop + rect.height;



            // Ideal: align logger top with offset

            let targetTop = loggerTop - offset;



            // If logger is taller than viewport, we can only show its top

            // But if logger fits, we want its bottom also visible

            if (rect.height <= viewportHeight - offset) {

                // Check if bottom is below viewport

                const currentBottom = loggerTop - offset + rect.height;

                if (currentBottom > viewportHeight) {

                    // Adjust so bottom is at bottom of viewport

                    targetTop = loggerBottom - viewportHeight + offset;

                }

            }



            // Snap to exact pixel (no smooth for snappiness)

            window.scrollTo({

                top: Math.max(0, targetTop),

                behavior: 'auto'  // instant, not smooth

            });

        }, 450);

    } else {

        icon.className = 'fas fa-chevron-down';

    }

}

// Opens logger, hides prescription and header

function openLoggerForExercise(index) {

    const exerciseDiv = document.getElementById(`exercise_${index}`);

    if (!exerciseDiv) return;



    // Force rebuild the performance form using the current exercise data

    const exercise = currentWorkout.exercises[index];

    if (exercise) {

        createPerformanceForm(index, exercise);

    }



    const logger = document.getElementById(`performance_${index}`);

    // P5P5: the prescription header STAYS visible (sticky card header) while logging

    if (logger) logger.style.display = 'block';

}



// Closes logger, restores prescription and header

function closeLoggerAndRestoreCard(index) {

    const exerciseDiv = document.getElementById(`exercise_${index}`);

    if (!exerciseDiv) return;



    const prescription = exerciseDiv.querySelector('.exercise-prescription');

    const logger = document.getElementById(`performance_${index}`);



    // Restore prescription visibility

    if (prescription) prescription.style.display = '';



    // Hide logger completely and remove active class

    if (logger) {

        logger.style.display = 'none';

        logger.classList.remove('active');

    }



    // Also ensure the chevron icon (if any) is reset to down state

    const chevron = exerciseDiv.querySelector('.fa-chevron-up, .fa-chevron-down');

    if (chevron) {

        chevron.className = 'fas fa-chevron-down';

    }



    // Small delay to allow any pending DOM updates

    setTimeout(() => {

        // Optionally re-trigger any resize or scroll adjustments

        if (window.dispatchEvent) {

            window.dispatchEvent(new Event('resize'));

        }

    }, 50);

}

/* P5P6: hold-to-repeat stepper for logger number inputs */

function attachLoggerStepper(input, step, opts) {

    opts = opts || {};

    if (!input || input._p5step) return;

    input._p5step = true;

    const min = (opts.min !== undefined) ? opts.min : 0;

    const max = (opts.max !== undefined) ? opts.max : Infinity;

    const row = document.createElement('div');

    row.className = 'p4-step-row';

    const parent = input.parentNode;

    parent.insertBefore(row, input);

    row.appendChild(input);

    const mk = (dir) => {

        const b = document.createElement('button');

        b.type = 'button';

        b.className = 'p4-step-btn';

        b.innerHTML = dir < 0 ? '<i class="fas fa-minus"></i>' : '<i class="fas fa-plus"></i>';

        b.setAttribute('aria-label', (dir < 0 ? 'Decrease by ' : 'Increase by ') + step);

        const bump = () => {

            const cur = parseFloat(input.value);

            let v = (isNaN(cur) ? (opts.start || 0) : cur) + dir * step;

            v = Math.max(min, Math.min(max, Math.round(v * 100) / 100));

            input.value = v;

            input.dispatchEvent(new Event('input', { bubbles: true }));

            input.dispatchEvent(new Event('change', { bubbles: true }));

            b.classList.add('flash');

            setTimeout(() => b.classList.remove('flash'), 140);

        };

        let t1 = null, t2 = null, lastBump = 0;

        b.addEventListener('pointerdown', (e) => {

            e.preventDefault();

            lastBump = Date.now();

            bump();

            t1 = setTimeout(() => { t2 = setInterval(bump, 110); }, 420);

        });

        // P5P13: keyboard / assistive-tech fallback (real taps don't double-fire:

        // preventDefault on pointerdown cancels the synthesized click)

        b.addEventListener('click', (e) => {

            if (Date.now() - lastBump > 350) bump();

        });

        ['pointerup', 'pointerleave', 'pointercancel'].forEach(ev => b.addEventListener(ev, () => { clearTimeout(t1); clearInterval(t2); }));

        return b;

    };

    row.insertBefore(mk(-1), input);

    row.appendChild(mk(1));

}



function createPerformanceForm(index, exercise) {

    const form = document.getElementById(`performanceForm_${index}`);

    if (!form) return;

    form.innerHTML = '';



    // ========== DRAFT HELPERS (only used if no actual data) ==========

    function getDraftKey() {

        if (!currentWorkout || !currentWorkout.id) return null;

        return `draft_${currentWorkout.id}_${exercise.id}`;

    }

    function saveDraft(data) {

        const key = getDraftKey();

        if (!key) return;

        localStorage.setItem(key, JSON.stringify({ ...data, timestamp: Date.now() }));

    }

    function loadDraft() {

        const key = getDraftKey();

        if (!key) return null;

        const raw = localStorage.getItem(key);

        if (!raw) return null;

        try { return JSON.parse(raw); } catch(e) { return null; }

    }

    function clearDraft() {

        const key = getDraftKey();

        if (key) localStorage.removeItem(key);

    }



    // *** PRIORITY: Use actual saved data if it exists ***

    const hasActual = (exercise.actual && !exercise.actual.skipped);

    let draft = null;

    if (!hasActual) {

        draft = loadDraft();

    }



    // Helper to collect current UI state (only for drafting, not for actual)

    function collectAndSaveDraft() {

        if (hasActual) return; // never overwrite actual with draft

        const weightInput = document.getElementById(`prescribedWeight_${index}`);

        if (!weightInput) return;

        const weight = parseFloat(weightInput.value) || 0;

        const setsSelect = document.getElementById(`sets_${index}`);

        const setsCompleted = setsSelect ? parseInt(setsSelect.value) || 0 : 0;

        const presType = exercise.prescriptionType || 'reps';

        const reps = [];

        const rpes = [];

        const durations = [];

        if (presType === 'time') {

            for (let i = 0; i < setsCompleted; i++) {

                const durInput = document.getElementById(`duration_${index}_${i}`);

                const rpeHidden = document.getElementById(`rpe_${index}_${i}`);

                durations.push(durInput ? parseInt(durInput.value) || 0 : 0);

                rpes.push(rpeHidden ? parseInt(rpeHidden.value) || 0 : 0);

            }

        } else {

            const prescribedSets = exercise.prescribed.sets;

            const prescribedReps = exercise.prescribed.reps;

            let targetRepsMin = 1, targetRepsMax = 1;

            if (typeof prescribedReps === 'string') {

                const cleanReps = prescribedReps.replace(/\s*\(AMRAP\)\s*/, '');

                const parts = cleanReps.split('-').map(Number);

                targetRepsMin = parts[0] || 1;

                targetRepsMax = parts[1] || targetRepsMin;

            } else {

                targetRepsMin = targetRepsMax = Number(prescribedReps) || 1;

            }

            const isClusterMode = prescribedSets >= 8 && targetRepsMax <= 3;

            if (isClusterMode) {

                for (let i = 0; i < prescribedSets; i++) {

                    const setDiv = document.querySelector(`#performance_${index} .cluster-set:nth-child(${i+1})`);

                    if (setDiv) {

                        const selectedBtn = setDiv.querySelector('.rep-selector[style*="color: white"]');

                        reps.push(selectedBtn ? parseInt(selectedBtn.dataset.rep) : 0);

                        const rpeHidden = document.getElementById(`rpe_${index}_${i}`);

                        rpes.push(rpeHidden ? parseInt(rpeHidden.value) || 0 : 0);

                    } else {

                        reps.push(0);

                        rpes.push(0);

                    }

                }

            } else {

                for (let i = 0; i < setsCompleted; i++) {

                    const repInput = document.getElementById(`reps_${index}_${i}`);

                    const rpeHidden = document.getElementById(`rpe_${index}_${i}`);

                    reps.push(repInput ? parseInt(repInput.value) || 0 : 0);

                    rpes.push(rpeHidden ? parseInt(rpeHidden.value) || 0 : 0);

                }

            }

        }

        const notes = document.getElementById(`notes_${index}`)?.value || '';

        const draftData = { weight, setsCompleted, reps, rpes, durations, notes, timestamp: Date.now() };

        saveDraft(draftData);

        draft = draftData;

    }



    // ==================== WEIGHT INPUT ====================

    // Determine default weight: actual > draft > prescribed

    let defaultWeight = '';

    if (hasActual && exercise.actual.weight) defaultWeight = exercise.actual.weight;

    else if (draft && draft.weight !== undefined) defaultWeight = draft.weight;

    else defaultWeight = exercise.prescribed.weight || '';



    // Fiber-aware weight hint: the textbox holds the min-side suggestion and

    // the label shows "max: X lbs". Typing above the ceiling flips the hint to

    // a soft red warning (advisory only â€” nothing is blocked).

    const fiberMax = (exercise.prescribed && typeof exercise.prescribed.weightMax === 'number')

        ? exercise.prescribed.weightMax : null;

    const weightGroup = document.createElement('div');

    weightGroup.className = 'form-group';

    weightGroup.innerHTML = `

        <label>Weight (lbs):${fiberMax !== null ? ` <span id="fiberMaxHint_${index}" style="font-weight:600; color:var(--gray-500);">max: ${fiberMax} lbs</span>` : ''}</label>

        <input type="number" id="prescribedWeight_${index}" class="weight-input"

               value="${defaultWeight}" step="2.5" min="0"

               style="width: 100%; min-height: 48px; font-size: 16px;"

               oninput="debouncedWeightChange(${index}, this.value)">

    `;

    form.appendChild(weightGroup);

    const weightInput = weightGroup.querySelector('input');

    attachLoggerStepper(weightInput, 2.5, { min: 0 }); // P5P6: Â±2.5 lbs, hold to repeat

    weightInput.addEventListener('input', () => {

        const hint = document.getElementById(`fiberMaxHint_${index}`);

        if (hint && fiberMax !== null) {

            const typed = parseFloat(weightInput.value);

            if (!isNaN(typed) && typed > fiberMax) {

                hint.textContent = `âš  above fiber max (${fiberMax} lbs)`;

                hint.style.color = 'var(--danger)';

            } else {

                hint.textContent = `max: ${fiberMax} lbs`;

                hint.style.color = 'var(--gray-500)';

            }

        }

        saveWorkoutDraftImmediately();

        if (!hasActual) collectAndSaveDraft();

    });



    const presType = exercise.prescriptionType || 'reps';

    const prescribedSets = exercise.prescribed.sets;



    // ==================== TIMEâ€‘BASED EXERCISES ====================

    if (presType === 'time') {

        const duration = exercise.prescribed.duration || 60;

        const setsGroup = document.createElement('div');

        setsGroup.className = 'form-group';

        let defaultSetsCompleted = hasActual ? exercise.actual.sets : (draft?.setsCompleted !== undefined ? draft.setsCompleted : prescribedSets);

        setsGroup.innerHTML = `

            <label>Sets Completed:</label>

            <select id="sets_${index}" class="sets-completed">

                ${Array.from({ length: prescribedSets + 1 }, (_, i) =>

                    `<option value="${i}" ${defaultSetsCompleted === i ? 'selected' : ''}>${i}</option>`

                ).join('')}

            </select>

        `;

        form.appendChild(setsGroup);



        const setsContainer = document.createElement('div');

        setsContainer.id = `setsContainer_${index}`;

        form.appendChild(setsContainer);



        function updateSetInputs() {

            const setsCompleted = parseInt(document.getElementById(`sets_${index}`).value) || 0;

            setsContainer.innerHTML = '';

            for (let i = 0; i < setsCompleted; i++) {

                const row = document.createElement('div');

                row.style.display = 'flex';

                row.style.alignItems = 'center';

                row.style.gap = '10px';

                row.style.marginBottom = '10px';



                let defaultDur = duration;

                let defaultRpe = 7;

                if (hasActual && exercise.actual.durations && exercise.actual.durations[i]) defaultDur = exercise.actual.durations[i];

                else if (draft && draft.durations && draft.durations[i]) defaultDur = draft.durations[i];

                if (hasActual && exercise.actual.rpe) {

                    const rpeVal = Array.isArray(exercise.actual.rpe) ? exercise.actual.rpe[i] : exercise.actual.rpe;

                    if (rpeVal) defaultRpe = rpeVal;

                } else if (draft && draft.rpes && draft.rpes[i]) defaultRpe = draft.rpes[i];



                const durGroup = document.createElement('div');

                durGroup.style.flex = '2';

                durGroup.innerHTML = `

                    <label>Set ${i + 1} Duration (sec):</label>

                    <input type="number" id="duration_${index}_${i}" value="${defaultDur}" min="0" step="1">

                `;



                const rpeGroup = document.createElement('div');

                rpeGroup.style.flex = '1';

                rpeGroup.innerHTML = '<label>RPE (click slider):</label>';

                const rpeSlider = createRPESlider(index, i, defaultRpe);

                rpeGroup.appendChild(rpeSlider);



                row.appendChild(durGroup);

                row.appendChild(rpeGroup);

                setsContainer.appendChild(row);



                const durInput = durGroup.querySelector('input');

                attachLoggerStepper(durInput, 5, { min: 0 }); // P5P6: Â±5 sec

                durInput.addEventListener('input', () => {

                    saveWorkoutDraftImmediately();

                    if (!hasActual) collectAndSaveDraft();

                });

                const hiddenRpe = rpeSlider.querySelector('input[type="hidden"]');

                if (hiddenRpe) {

                    hiddenRpe.addEventListener('change', () => {

                        saveWorkoutDraftImmediately();

                        if (!hasActual) collectAndSaveDraft();

                    });

                }

            }

            if (!hasActual) collectAndSaveDraft();

        }

        updateSetInputs();

        const setsSelect = document.getElementById(`sets_${index}`);

        setsSelect.addEventListener('change', () => {

            if (!hasActual) collectAndSaveDraft();

            updateSetInputs();

            saveWorkoutDraftImmediately();

        });



            /* P5P6: Â±1 set stepper around the select */

            (function wrapSetsSelect() {

                const row = document.createElement('div');

                row.className = 'p4-step-row';

                setsSelect.parentNode.insertBefore(row, setsSelect);

                row.appendChild(setsSelect);

                const mk = (dir) => {

                    const b = document.createElement('button');

                    b.type = 'button';

                    b.className = 'p4-step-btn';

                    b.innerHTML = dir < 0 ? '<i class="fas fa-minus"></i>' : '<i class="fas fa-plus"></i>';

                    b.setAttribute('aria-label', dir < 0 ? 'One set fewer' : 'One set more');

                    b.addEventListener('click', (e) => {

                        e.preventDefault();

                        const max = setsSelect.options.length - 1;

                        let v = (parseInt(setsSelect.value) || 0) + dir;

                        v = Math.max(0, Math.min(max, v));

                        if (String(v) !== String(setsSelect.value)) {

                            setsSelect.value = v;

                            setsSelect.dispatchEvent(new Event('change', { bubbles: true }));

                        }

                        try { P4.buzz(6); } catch (err) {}

                    });

                    return b;

                };

                row.insertBefore(mk(-1), setsSelect);

                row.appendChild(mk(1));

            })();



    }

    // ==================== REPSâ€‘BASED EXERCISES ====================

    else {

        const prescribedReps = exercise.prescribed.reps;

        let targetRepsMin = 1, targetRepsMax = 1;

        if (typeof prescribedReps === 'string') {

            const cleanReps = prescribedReps.replace(/\s*\(AMRAP\)\s*/, '');

            const parts = cleanReps.split('-').map(Number);

            targetRepsMin = parts[0] || 1;

            targetRepsMax = parts[1] || targetRepsMin;

        } else {

            targetRepsMin = targetRepsMax = Number(prescribedReps) || 1;

        }



        const isClusterMode = prescribedSets >= 8 && targetRepsMax <= 3;



        if (isClusterMode) {

            // Cluster mode (rep buttons)

            const clusterContainer = document.createElement('div');

            clusterContainer.className = 'cluster-container';

            clusterContainer.style.marginBottom = '15px';



            function getRepColor(rep, min, max) {

                if (min === max) return '#34d399';

                const t = (rep - min) / (max - min);

                const hue = 120 - t * 120;

                return `hsl(${hue}, 80%, 45%)`;

            }



            const setRefs = [];



            for (let i = 0; i < prescribedSets; i++) {

                const setDiv = document.createElement('div');

                setDiv.className = 'cluster-set';

                setDiv.style.display = 'flex';

                setDiv.style.alignItems = 'center';

                setDiv.style.marginBottom = '8px';

                setDiv.style.gap = '8px';



                const label = document.createElement('span');

                label.textContent = `Set ${i + 1}:`;

                label.style.minWidth = '50px';

                label.style.fontWeight = '500';



                const btnGroup = document.createElement('div');

                btnGroup.style.display = 'flex';

                btnGroup.style.gap = '4px';



                let defaultRep = null;

                if (hasActual && exercise.actual.reps && exercise.actual.reps[i]) defaultRep = exercise.actual.reps[i];

                else if (draft && draft.reps && draft.reps[i]) defaultRep = draft.reps[i];



                for (let r = targetRepsMin; r <= targetRepsMax; r++) {

                    const btn = document.createElement('button');

                    btn.type = 'button';

                    btn.className = 'rep-selector';

                    btn.dataset.set = i;

                    btn.dataset.rep = r;

                    btn.textContent = r;

                    btn.style.padding = '8px 12px';

                    btn.style.border = '1px solid var(--gray-300)';

                    btn.style.borderRadius = '6px';

                    btn.style.background = 'white';

                    btn.style.cursor = 'pointer';

                    btn.style.minWidth = '50px';

                    btn.style.fontWeight = '600';

                    const repColor = getRepColor(r, targetRepsMin, targetRepsMax);

                    btn.style.backgroundColor = repColor + '20';

                    btn.style.color = 'var(--gray-700)';

                    btn.style.borderColor = 'var(--gray-300)';

                    if (defaultRep === r) {

                        btn.style.backgroundColor = repColor;

                        btn.style.color = 'white';

                        btn.style.borderColor = repColor;

                    }

                    btn.onclick = (e) => {

                        const siblings = btnGroup.querySelectorAll('.rep-selector');

                        siblings.forEach(b => {

                            const bc = getRepColor(parseInt(b.dataset.rep), targetRepsMin, targetRepsMax);

                            b.style.backgroundColor = bc + '20';

                            b.style.color = 'var(--gray-700)';

                            b.style.borderColor = 'var(--gray-300)';

                        });

                        btn.style.backgroundColor = repColor;

                        btn.style.color = 'white';

                        btn.style.borderColor = repColor;

                        updateRIRForSet(i);

                        saveWorkoutDraftImmediately();

                        if (!hasActual) collectAndSaveDraft();

                    };

                    btnGroup.appendChild(btn);

                }



                let defaultRpe = 7;

                if (hasActual && exercise.actual.rpe) {

                    const rpeVal = Array.isArray(exercise.actual.rpe) ? exercise.actual.rpe[i] : exercise.actual.rpe;

                    if (rpeVal) defaultRpe = rpeVal;

                } else if (draft && draft.rpes && draft.rpes[i]) defaultRpe = draft.rpes[i];



                const rpeSlider = createRPESlider(index, i, defaultRpe);

                rpeSlider.style.marginLeft = '8px';

                const rirSpan = document.createElement('span');

                rirSpan.id = `rir_${index}_${i}`;

                rirSpan.style.marginLeft = '8px';

                rirSpan.style.fontWeight = 'bold';

                rirSpan.style.minWidth = '70px';

                rirSpan.textContent = 'RIR: --';



                setDiv.appendChild(label);

                setDiv.appendChild(btnGroup);

                setDiv.appendChild(rpeSlider);

                setDiv.appendChild(rirSpan);

                clusterContainer.appendChild(setDiv);



                const hiddenRpe = rpeSlider.querySelector('input[type="hidden"]');

                setRefs.push({ hiddenRpe, rirSpan, btnGroup, rpeSlider });

                if (hiddenRpe) {

                    hiddenRpe.addEventListener('change', () => {

                        updateRIRForSet(i);

                        saveWorkoutDraftImmediately();

                        if (!hasActual) collectAndSaveDraft();

                    });

                }

            }



            const updateRIRForSet = (setIndex) => {

                const weight = parseFloat(document.getElementById(`prescribedWeight_${index}`).value);

                if (!weight || weight <= 0) return;

                const setDiv = clusterContainer.children[setIndex];

                const selectedBtn = setDiv.querySelector('.rep-selector[style*="color: white"]');

                const reps = selectedBtn ? parseInt(selectedBtn.dataset.rep) : 0;

                const hiddenRpe = setRefs[setIndex].hiddenRpe;

                const rpe = hiddenRpe ? parseInt(hiddenRpe.value) : 0;

                if (reps > 0 && rpe > 0) {

                    const rir = computeRIR(exercise.id, weight, reps, rpe, setIndex + 1);

                    if (rir !== null) {

                        const exRecord = workoutData.exercises[exercise.id];

                        const sd = exRecord?.lastRIR_uncertainty || 0;

                        const lower = Math.max(0, Math.min(10, Math.round(rir - sd)));

                        const upper = Math.max(0, Math.min(10, Math.round(rir + sd)));

                        setRefs[setIndex].rirSpan.textContent = `RIR: ${lower}â€“${upper}`;

                        setRefs[setIndex].rirSpan.title = `68% CI (mean ${rir.toFixed(1)} Â± ${sd.toFixed(1)})`;

                    } else setRefs[setIndex].rirSpan.textContent = 'RIR: --';

                } else setRefs[setIndex].rirSpan.textContent = 'RIR: --';

            };



            const weightInputElem = document.getElementById(`prescribedWeight_${index}`);

            const updateAllRIR = () => {

                for (let i = 0; i < prescribedSets; i++) updateRIRForSet(i);

                saveWorkoutDraftImmediately();

                if (!hasActual) collectAndSaveDraft();

            };

            weightInputElem.addEventListener('input', updateAllRIR);

            form.appendChild(clusterContainer);



        }

        // ---------- NORMAL MODE (with sets dropdown) ----------

        else {

            const setsGroup = document.createElement('div');

            setsGroup.className = 'form-group';

            let defaultSetsCompleted = hasActual ? exercise.actual.sets : (draft?.setsCompleted !== undefined ? draft.setsCompleted : prescribedSets);

            setsGroup.innerHTML = `

                <label>Sets Completed:</label>

                <select id="sets_${index}" class="sets-completed">

                    ${Array.from({ length: prescribedSets + 1 }, (_, i) =>

                        `<option value="${i}" ${defaultSetsCompleted === i ? 'selected' : ''}>${i}</option>`

                    ).join('')}

                </select>

            `;

            form.appendChild(setsGroup);



            const setsContainer = document.createElement('div');

            setsContainer.id = `setsContainer_${index}`;

            form.appendChild(setsContainer);



            function updateSetInputs() {

                const setsCompleted = parseInt(document.getElementById(`sets_${index}`).value) || 0;

                setsContainer.innerHTML = '';

                for (let i = 0; i < setsCompleted; i++) {

                    const row = document.createElement('div');

                    row.style.display = 'flex';

                    row.style.alignItems = 'center';

                    row.style.gap = '10px';

                    row.style.marginBottom = '10px';



                    let defaultRep = '';

                    let defaultRpe = 7;

                    if (hasActual && exercise.actual.reps && exercise.actual.reps[i]) defaultRep = exercise.actual.reps[i];

                    else if (draft && draft.reps && draft.reps[i]) defaultRep = draft.reps[i];

                    if (hasActual && exercise.actual.rpe) {

                        const rpeVal = Array.isArray(exercise.actual.rpe) ? exercise.actual.rpe[i] : exercise.actual.rpe;

                        if (rpeVal) defaultRpe = rpeVal;

                    } else if (draft && draft.rpes && draft.rpes[i]) defaultRpe = draft.rpes[i];



                    const repsGroup = document.createElement('div');

                    repsGroup.style.flex = '2';

                    repsGroup.innerHTML = `

                        <label>Set ${i + 1} Reps:</label>

                        <input type="number" id="reps_${index}_${i}" placeholder="Actual reps" min="0" step="1" value="${defaultRep}">

                    `;



                    const rpeGroup = document.createElement('div');

                    rpeGroup.style.flex = '1';

                    rpeGroup.innerHTML = '<label>RPE (click slider):</label>';

                    const rpeSlider = createRPESlider(index, i, defaultRpe);

                    rpeGroup.appendChild(rpeSlider);



                    const rirSpan = document.createElement('span');

                    rirSpan.id = `rir_${index}_${i}`;

                    rirSpan.style.flex = '1';

                    rirSpan.style.fontWeight = 'bold';

                    rirSpan.textContent = 'RIR: --';



                    row.appendChild(repsGroup);

                    row.appendChild(rpeGroup);

                    row.appendChild(rirSpan);

                    setsContainer.appendChild(row);



                    const repsInput = document.getElementById(`reps_${index}_${i}`);

                    const weightInputElem = document.getElementById(`prescribedWeight_${index}`);

                    const hiddenRpe = rpeSlider.querySelector('input[type="hidden"]');



                    const updateRIR = () => {

                        const weight = parseFloat(weightInputElem.value);

                        const reps = parseInt(repsInput.value);

                        const rpe = hiddenRpe ? parseInt(hiddenRpe.value) : 0;

                        if (weight > 0 && reps > 0 && rpe) {

                            const rir = computeRIR(exercise.id, weight, reps, rpe, i + 1);

                            if (rir !== null) {

                                const exRecord = workoutData.exercises[exercise.id];

                                const sd = exRecord?.lastRIR_uncertainty || 0;

                                const lower = Math.max(0, Math.min(10, Math.round(rir - sd)));

                                const upper = Math.max(0, Math.min(10, Math.round(rir + sd)));

                                rirSpan.textContent = `RIR: ${lower}â€“${upper}`;

                                rirSpan.title = `68% CI (mean ${rir.toFixed(1)} Â± ${sd.toFixed(1)})`;

                            } else rirSpan.textContent = 'RIR: --';

                        } else rirSpan.textContent = 'RIR: --';

                        saveWorkoutDraftImmediately();

                        if (!hasActual) collectAndSaveDraft();

                    };



                    attachLoggerStepper(repsInput, 1, { min: 0 }); // P5P6: Â±1 rep

                    repsInput.addEventListener('input', updateRIR);

                    if (hiddenRpe) hiddenRpe.addEventListener('change', updateRIR);

                    weightInputElem.addEventListener('input', updateRIR);

                }

                if (!hasActual) collectAndSaveDraft();

            }

            updateSetInputs();

            const setsSelect = document.getElementById(`sets_${index}`);

            setsSelect.addEventListener('change', () => {

                if (!hasActual) collectAndSaveDraft();

                updateSetInputs();

                saveWorkoutDraftImmediately();

            });



            /* P5P6: Â±1 set stepper around the select */

            (function wrapSetsSelect() {

                const row = document.createElement('div');

                row.className = 'p4-step-row';

                setsSelect.parentNode.insertBefore(row, setsSelect);

                row.appendChild(setsSelect);

                const mk = (dir) => {

                    const b = document.createElement('button');

                    b.type = 'button';

                    b.className = 'p4-step-btn';

                    b.innerHTML = dir < 0 ? '<i class="fas fa-minus"></i>' : '<i class="fas fa-plus"></i>';

                    b.setAttribute('aria-label', dir < 0 ? 'One set fewer' : 'One set more');

                    b.addEventListener('click', (e) => {

                        e.preventDefault();

                        const max = setsSelect.options.length - 1;

                        let v = (parseInt(setsSelect.value) || 0) + dir;

                        v = Math.max(0, Math.min(max, v));

                        if (String(v) !== String(setsSelect.value)) {

                            setsSelect.value = v;

                            setsSelect.dispatchEvent(new Event('change', { bubbles: true }));

                        }

                        try { P4.buzz(6); } catch (err) {}

                    });

                    return b;

                };

                row.insertBefore(mk(-1), setsSelect);

                row.appendChild(mk(1));

            })();



        }

    }

// ==================== ZEROâ€‘REP FAILURE HANDLING ====================

const failureGroup = document.createElement('div');

failureGroup.className = 'form-group';

failureGroup.style.marginBottom = '15px';

failureGroup.style.padding = '10px';

failureGroup.style.backgroundColor = 'rgba(239, 68, 68, 0.1)';

failureGroup.style.borderRadius = '8px';

failureGroup.style.borderLeft = '4px solid var(--danger)';

failureGroup.innerHTML = `

    <label style="display: flex; align-items: center; gap: 8px; cursor: pointer;">

        <input type="checkbox" id="failure_checkbox_${index}" style="width: 18px; height: 18px;">

        <span><i class="fas fa-exclamation-triangle"></i> Could not do any rep</span>

    </label>

    <div id="failure_weight_container_${index}" style="display: none; margin-top: 10px;">

        <label>Attempted weight (lbs): <span style="color: var(--danger);">*required</span></label>

        <input type="number" id="failure_weight_${index}" step="2.5" min="0" placeholder="e.g., 135" style="min-height: 44px;">

    </div>

`;

form.appendChild(failureGroup);



// Add event listeners for the checkbox

const failureCheckbox = document.getElementById(`failure_checkbox_${index}`);

const failureWeightContainer = document.getElementById(`failure_weight_container_${index}`);

const failureWeightInput = document.getElementById(`failure_weight_${index}`);



if (failureCheckbox) {

    failureCheckbox.addEventListener('change', function() {

        if (this.checked) {

            failureWeightContainer.style.display = 'block';

            if (failureWeightInput) failureWeightInput.setAttribute('required', 'required');

        } else {

            failureWeightContainer.style.display = 'none';

            if (failureWeightInput) {

                failureWeightInput.removeAttribute('required');

                failureWeightInput.value = '';

            }

        }

        if (!hasActual) collectAndSaveDraft();

    });

}

if (failureWeightInput) {

    failureWeightInput.addEventListener('input', () => {

        if (!hasActual) collectAndSaveDraft();

    });

}

    // ==================== NOTES ====================

    const notesGroup = document.createElement('div');

    notesGroup.className = 'form-group';

    let defaultNotes = '';

    if (hasActual && exercise.actual.notes) defaultNotes = exercise.actual.notes;

    else if (draft && draft.notes) defaultNotes = draft.notes;

    notesGroup.innerHTML = `

        <label>Notes:</label>

        <textarea id="notes_${index}" placeholder="How did it feel? Any issues?">${defaultNotes}</textarea>

    `;

    form.appendChild(notesGroup);

    const notesTextarea = notesGroup.querySelector('textarea');

    notesTextarea.addEventListener('input', () => {

        saveWorkoutDraftImmediately();

        if (!hasActual) collectAndSaveDraft();

    });



    // ==================== STICKY BUTTONS ====================

    const stickyContainer = document.createElement('div');

    stickyContainer.style.position = 'sticky';

    stickyContainer.style.bottom = '0';

    stickyContainer.style.backgroundColor = 'white';

    stickyContainer.style.padding = '10px 0';

    stickyContainer.style.borderTop = '2px solid var(--gray-200)';

    stickyContainer.style.marginTop = '10px';

    stickyContainer.style.zIndex = '10';



    const btnGroup = document.createElement('div');

    btnGroup.className = 'btn-group';

    btnGroup.style.display = 'flex';

    btnGroup.style.gap = '10px';

    btnGroup.innerHTML = `

        <button class="btn btn-success" style="flex:2; min-height:56px; font-size:18px;" onclick="saveExercisePerformance(${index})">

            <i class="fas fa-save"></i> Save

        </button>

        <button class="btn" style="flex:1; min-height:56px;" onclick="skipExercise(${index})">

            <i class="fas fa-forward"></i> Skip

        </button>

    `;

    stickyContainer.appendChild(btnGroup);

    form.appendChild(stickyContainer);



    const saveButton = btnGroup.querySelector('.btn-success');

    if (saveButton) {

        saveButton.addEventListener('click', () => {

            if (!hasActual) clearDraft();

        });

    }

}

function removeExerciseFromWorkout(index) {

    if (!currentWorkout || !currentWorkout.exercises) return;

    

    Swal.fire({

        title: 'Remove exercise?',

        text: `Are you sure you want to remove "${currentWorkout.exercises[index].name}" from this workout?`,

        icon: 'warning',

        showCancelButton: true,

        confirmButtonColor: '#d33',

        confirmButtonText: 'Yes, remove',

        cancelButtonText: 'Cancel'

    }).then((result) => {

        if (result.isConfirmed) {

            // Remove the exercise at index

            currentWorkout.exercises.splice(index, 1);

            

            // Re-index any remaining exercises (their indices will be fixed on re-render)

            // Save to storage

            saveCurrentWorkoutToStorage();

            

            // Refresh the workout display

            if (document.getElementById('workout-section').classList.contains('active')) {

                updateTodaysWorkout();

                updateWorkoutHeader();

            }

            

            // Update dashboard preview

            updateDashboard();

            

            showNotification('Exercise removed from workout', 'info');

        }

    });

}

function skipExercise(index) {

    const exercise = currentWorkout.exercises[index];

    if (!exercise) return;



    // Show confirmation dialog using SweetAlert2

    Swal.fire({

        title: 'Skip Exercise?',

        text: `Are you sure you want to skip "${exercise.name}"? Any unsaved data will be lost.`,

        icon: 'warning',

        showCancelButton: true,

        confirmButtonColor: '#d33',

        cancelButtonColor: '#3085d6',

        confirmButtonText: 'Yes, skip it',

        cancelButtonText: 'Cancel'

    }).then((result) => {

        if (result.isConfirmed) {

            // User confirmed â€“ proceed with skip

            performSkipExercise(index);

        }

    });

}



// Actual skip logic moved to a separate function

function performSkipExercise(index) {

    const exercise = currentWorkout.exercises[index];

    if (!exercise) return;



    // Mark as skipped

    exercise.skipped = true;

    exercise.actual = {

        skipped: true,

        notes: "Skipped - " + (document.getElementById(`notes_${index}`)?.value || "No reason given")

    };



    // Clear any draft for this exercise from localStorage

    const draftKey = getDraftKeyForExercise(index);

    if (draftKey) localStorage.removeItem(draftKey);



    // Close logger if open and restore card appearance

    const loggingDiv = document.getElementById(`performance_${index}`);

    if (loggingDiv && loggingDiv.classList.contains('active')) {

        closeLoggerAndRestoreCard(index);

    }



    // Add visual skipped class to the exercise card

    const exerciseCard = document.getElementById(`exercise_${index}`);

    if (exerciseCard) {

        exerciseCard.classList.add('skipped');

        exerciseCard.style.opacity = '0.7';

    }



    // Update the "Complete Workout" button visibility

    updateCompleteWorkoutButtonVisibility();



    // Save the updated workout to storage

    saveCurrentWorkoutToStorage();



    // Show notification

    showNotification(`${exercise.name} skipped`, 'info', 'exercise_skipped');



    // Auto-advance to the next unfinished exercise

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

}



// Helper function to get draft key for an exercise

function getDraftKeyForExercise(index) {

    if (!currentWorkout || !currentWorkout.id) return null;

    const exercise = currentWorkout.exercises[index];

    if (!exercise) return null;

    return `draft_${currentWorkout.id}_${exercise.id}`;

}



// Helper to update the Complete Workout button

function updateCompleteWorkoutButtonVisibility() {

    const btnContainer = document.getElementById('workoutButtons');

    if (!btnContainer) return;

    if (!currentWorkout || !currentWorkout.exercises || currentWorkout.exercises.length === 0) {

        btnContainer.style.display = 'none';

        return;

    }

    const allDone = currentWorkout.exercises.every(ex => ex.actual || ex.skipped);

    btnContainer.style.display = allDone ? 'flex' : 'none';



    // Optional celebration when button appears

    if (allDone && !btnContainer.hasAttribute('data-celebrated')) {

        btnContainer.setAttribute('data-celebrated', 'true');

        const completeBtn = btnContainer.querySelector('.btn-success');

        if (completeBtn) {

            completeBtn.classList.add('animate__animated', 'animate__pulse');

            setTimeout(() => completeBtn.classList.remove('animate__animated', 'animate__pulse'), 1000);

        }

    }

}

function saveExercisePerformance(index) {

    const exercise = currentWorkout.exercises[index];

    if (!exercise) return;



    const weightInput = document.getElementById(`prescribedWeight_${index}`);

    const weight = weightInput ? parseFloat(weightInput.value) : exercise.prescribed.weight;



    if (isNaN(weight) || weight <= 0) {

        showNotification('Please enter a valid weight', 'warning');

        return;

    }



    const presType = exercise.prescriptionType || 'reps';

    let setsCompleted = 0;

    let repsArray = [];

    let durationsArray = [];

    let rpeArray = [];

    let notes = '';



    if (presType === 'time') {

        const setsSelect = document.getElementById(`sets_${index}`);

        setsCompleted = setsSelect ? parseInt(setsSelect.value) : 0;

        if (setsCompleted === 0) {

            showNotification('Please select at least one completed set', 'warning');

            return;

        }

        for (let i = 0; i < setsCompleted; i++) {

            const durationInput = document.getElementById(`duration_${index}_${i}`);

            const duration = durationInput ? parseInt(durationInput.value) : 0;

            if (isNaN(duration) || duration <= 0) {

                showNotification(`Please enter a valid duration for set ${i+1}`, 'warning');

                return;

            }

            durationsArray.push(duration);



            const hiddenRpe = document.getElementById(`rpe_${index}_${i}`);

            if (hiddenRpe && hiddenRpe.value) {

                rpeArray.push(parseInt(hiddenRpe.value));

            } else {

                rpeArray.push(7);

            }

        }

    } else {

        const setsSelect = document.getElementById(`sets_${index}`);

        setsCompleted = setsSelect ? parseInt(setsSelect.value) : 0;

        if (setsCompleted === 0) {

            showNotification('Please select at least one completed set', 'warning');

            return;

        }



        const usesClusterMode = !!document.querySelector(`#performanceForm_${index} .rep-selector`);

        if (usesClusterMode) {

            for (let i = 0; i < setsCompleted; i++) {

                const setDiv = document.querySelector(`#performanceForm_${index} .cluster-set:nth-child(${i+1})`);

                const selectedBtn = setDiv ? setDiv.querySelector('.rep-selector[style*="color: white"]') : null;

                if (selectedBtn && selectedBtn.dataset.rep) {

                    repsArray.push(parseInt(selectedBtn.dataset.rep));

                } else {

                    showNotification(`Please select a rep value for set ${i+1}`, 'warning');

                    return;

                }

                const hiddenRpe = document.getElementById(`rpe_${index}_${i}`);

                if (hiddenRpe && hiddenRpe.value) {

                    rpeArray.push(parseInt(hiddenRpe.value));

                } else {

                    rpeArray.push(7);

                }

            }

        } else {

            for (let i = 0; i < setsCompleted; i++) {

                const repsInput = document.getElementById(`reps_${index}_${i}`);

                const reps = repsInput ? parseInt(repsInput.value) : 0;

                if (isNaN(reps) || reps <= 0) {

                    showNotification(`Please enter valid reps for set ${i+1}`, 'warning');

                    return;

                }

                repsArray.push(reps);

                const hiddenRpe = document.getElementById(`rpe_${index}_${i}`);

                if (hiddenRpe && hiddenRpe.value) {

                    rpeArray.push(parseInt(hiddenRpe.value));

                } else {

                    rpeArray.push(7);

                }

            }

        }

    }



    const notesInput = document.getElementById(`notes_${index}`);

    notes = notesInput ? notesInput.value : '';



    const failureCheckbox = document.getElementById(`failure_checkbox_${index}`);

    const isFailure = failureCheckbox ? failureCheckbox.checked : false;

    let attemptedWeight = null;

    if (isFailure) {

        const failureWeightInput = document.getElementById(`failure_weight_${index}`);

        attemptedWeight = failureWeightInput ? parseFloat(failureWeightInput.value) : weight;

        if (isNaN(attemptedWeight) || attemptedWeight <= 0) {

            showNotification('Please enter the attempted weight for the failed set', 'warning');

            return;

        }

    }



    const actual = {

        weight: weight,

        sets: setsCompleted,

        notes: notes,

        rpe: presType === 'time' ? (rpeArray.length ? rpeArray[0] : null) : rpeArray,

        firstRPE: rpeArray.length ? rpeArray[0] : null,

        restEstimate: null

    };



    if (presType === 'time') {

        actual.durations = durationsArray;

        actual.totalTime = durationsArray.reduce((a,b) => a+b, 0);

    } else {

        actual.reps = repsArray;

        actual.rpe = rpeArray;

        actual.volume = weight * repsArray.reduce((a,b) => a+b, 0);

    }



    if (isFailure) {

        actual.failure = true;

        actual.attemptedWeight = attemptedWeight;

        const severity = calculateFailureSeverity(exercise, workoutData.user, attemptedWeight);

        applyFailurePenalty(exercise.id, attemptedWeight, severity);

        showNotification(`Logged failure for ${exercise.name}. Strength adjusted.`, 'info');

    } else {

        if (repsArray.length > 0 && weight > 0) {

            const bestReps = Math.max(...repsArray);

            updateRecursive1RM(exercise.id, weight, bestReps, rpeArray[0]);

        }

    }



    exercise.actual = actual;

    exercise.skipped = false;

        updateExerciseHistory(exercise);

    updateExerciseLastPerformed(exercise.id);



    // Close the logger and mark card as completed

    closeLoggerAndRestoreCard(index);

    const exerciseDiv = document.getElementById(`exercise_${index}`);

    if (exerciseDiv) exerciseDiv.classList.add('completed');



