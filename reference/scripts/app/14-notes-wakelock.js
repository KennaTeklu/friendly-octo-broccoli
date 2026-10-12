// ============================================================
// reference/scripts/app/14-notes-wakelock.js
// Extracted from reference/scripts/app.js
// Original lines: 69070-70557  (1488 lines)
// Section: Notes section, screen wake lock
// ============================================================

    // ---- Notes section (editable inline, autoâ€‘save) ----

    const currentNotes = workout.summary?.notes || '';

    html += `

        <div style="margin-top: 10px; border-top: 1px solid var(--gray-200); padding-top: 15px;">

            <label style="font-weight: 600; color: var(--gray-700); display: block; margin-bottom: 6px;">

                <i class="fas fa-pen"></i> Notes

            </label>

            <textarea id="notes_${workoutId}" class="notes-textarea" placeholder="Add notes about this workout...">${currentNotes}</textarea>

            <div style="font-size: 0.75rem; color: var(--gray-500); margin-top: 4px;">

                ðŸ’¡ Changes save automatically when you stop typing.

            </div>

        </div>

    `;



    html += '</div>';

    detailCell.innerHTML = html;



    // ---- Attach autoâ€‘save to the textarea ----

    // We need to wait for the DOM to update before we can attach the listener.

    setTimeout(() => {

        const textarea = document.getElementById(`notes_${workoutId}`);

        if (!textarea) return;



        // Debounced save on input (300ms after typing stops)

        let saveTimeout = null;

        textarea.addEventListener('input', function() {

            if (saveTimeout) clearTimeout(saveTimeout);

            saveTimeout = setTimeout(() => {

                saveNotesInline(workoutId, this.value);

                saveTimeout = null;

            }, 300);

        });



        // Also save on blur (clicking outside)

        textarea.addEventListener('blur', function() {

            if (saveTimeout) {

                clearTimeout(saveTimeout);

                saveTimeout = null;

            }

            saveNotesInline(workoutId, this.value);

        });



        // Save on Enter (but allow Shift+Enter for new lines)

        textarea.addEventListener('keydown', function(e) {

            if (e.key === 'Enter' && !e.shiftKey) {

                e.preventDefault();

                this.blur();

            }

        });

    }, 50);



    detailRow.appendChild(detailCell);

    row.insertAdjacentElement('afterend', detailRow);

    icon.className = 'fas fa-chevron-down';

}



// Helper function to save notes inline

function saveNotesInline(workoutId, notes) {

    const workout = workoutData.workouts.find(w => w.id === workoutId);

    if (!workout) return;

    if (!workout.summary) workout.summary = {};

    workout.summary.notes = notes.trim();

    saveToLocalStorage();

    // Optionally show a subtle notification (but avoid spamming)

    // showNotification('Notes saved', 'success', 'notes_saved');

}

      

        function filterHistory(filter, event) {

        // If event is provided (from onclick), update active tab

        if (event && event.target) {

          document.querySelectorAll('.tab').forEach(tab => {

            tab.classList.remove('active');

          });

          event.target.classList.add('active');

        }



        // Set current filter

        currentHistoryFilter = filter;



        // Refresh the history table with the new filter

        updateWorkoutHistory();

      }



    function exportHistoryCSV() {

        let csvContent = "Date,Workout Type,Exercises Completed,Total Volume,Average RPE\n";

        workoutData.workouts.forEach(workout => {

            const date = new Date(workout.date).toLocaleDateString();

            const completed = workout.exercises?.filter(ex => ex.actual && !ex.skipped).length || 0;

            const total = workout.exercises?.length || 0;

            const volume = workout.summary?.totalVolume || 0;

            const rpe = workout.summary?.averageRPE || '';

            csvContent += `"${date}","${workout.name || workout.type}","${completed}/${total}","${volume}","${rpe}"\n`;

        });

        const blob = new Blob([csvContent], { type: 'text/csv' });

        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');

        a.href = url;

        a.download = `workout-history-${new Date().toISOString().split('T')[0]}.csv`;

        document.body.appendChild(a);

        a.click();

        document.body.removeChild(a);

        window.URL.revokeObjectURL(url);

        showNotification("Workout history exported as CSV!");

    }



    // PROGRESS SECTION

    function updateProgressCharts() {

        updateVolumeChart();

        updateStrengthChart();

        updateFrequencyChart();

        updateRPEChart();

    }



    function updateVolumeChart() {

        const ctx = document.getElementById('volumeChart').getContext('2d');

        if (charts.volume) {

            charts.volume.destroy();

        }

        const lastWorkouts = workoutData.workouts.slice(-8);

        const labels = lastWorkouts.map((w, i) => `W${i + 1}`);

        const volumes = lastWorkouts.map(w => w.summary?.totalVolume || 0);

        const gradient = ctx.createLinearGradient(0, 0, 0, 300);

        gradient.addColorStop(0, 'rgba(54, 162, 235, 0.7)');

        gradient.addColorStop(1, 'rgba(54, 162, 235, 0.1)');

        charts.volume = new Chart(ctx, {

            type: 'bar',

            data: {

                labels: labels,

                datasets: [{

                    label: 'Volume',

                    data: volumes,

                    backgroundColor: gradient,

                    borderColor: 'rgb(54, 162, 235)',

                    borderWidth: 1,

                    borderRadius: 6

                }]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                plugins: { legend: { display: false } },

                scales: {

                    y: {

                        beginAtZero: true,

                        title: { display: true, text: 'Volume', color: 'var(--gray-600)' },

                        grid: { color: 'rgba(0, 0, 0, 0.05)' },

                        ticks: { color: 'var(--gray-500)' }

                    },

                    x: {

                        grid: { display: false },

                        ticks: { color: 'var(--gray-500)' }

                    }

                }

            }

        });

    }

// ========== SCREEN WAKE LOCK ==========

let currentWakeLock = null;

let wakeLockVideo = null;



// 1. Setup the iOS Video Fallback and "Prime" it on first tap

function setupVideoFallback() {

    if (!wakeLockVideo) {

        wakeLockVideo = document.createElement('video');

        wakeLockVideo.setAttribute('playsinline', '');

        wakeLockVideo.setAttribute('muted', '');

        wakeLockVideo.setAttribute('loop', '');

        wakeLockVideo.src = 'data:video/webm;base64,GkXfowEAAAAAAAAfQoaBAUL3gQFC8oEEQvOBCEKChHdlYm1ChgIQvUKEAUH0gQFaQQAwM1+OAUH9gQFaQQAwM1+OAUH8gQFaQQAwM1+OAUH5gQFaQQAwM1+OAUH6gQFaQQAwM1+OATiCEB0BAAAAAAAAEB2IBAAAAAAAABAwgQEDvQEDvwEDxIEB3gED4QEDAIEBEQEDHIEBIgEDKAEBE4EBEIEBFIEBE4EBEIEBFIEBEIEBFIEBEYEBEIEBFIEBEYEBEIEBEI';

        document.body.appendChild(wakeLockVideo);

    }

}



document.addEventListener('click', function primeVideo() {

    setupVideoFallback();

    wakeLockVideo.play().then(() => wakeLockVideo.pause()).catch(() => {});

    document.removeEventListener('click', primeVideo);

}, { once: true });



function enableVideoFallbackWakeLock() {

    setupVideoFallback();

    wakeLockVideo.play().catch(e => console.log('Video fallback blocked (waiting for interaction)'));

}



function disableVideoFallbackWakeLock() {

    if (wakeLockVideo) wakeLockVideo.pause();

}



function shouldBeAwake() {

    const toggleEnabled = document.getElementById('screenWakeLockToggle')?.checked === true;

    const hasWorkout = currentWorkout && currentWorkout.exercises && currentWorkout.exercises.length > 0;

    const isOnWorkoutPage = document.getElementById('workout-section')?.classList.contains('active');

    return toggleEnabled && hasWorkout && isOnWorkoutPage;

}



async function requestWakeLock() {

    if (!shouldBeAwake()) return; 

    

    if ('wakeLock' in navigator) {

        try {

            if (currentWakeLock) currentWakeLock.onrelease = null; 

            currentWakeLock = await navigator.wakeLock.request('screen');

            

            currentWakeLock.onrelease = () => {

                if (document.visibilityState === 'visible' && shouldBeAwake()) {

                    setTimeout(requestWakeLock, 300); 

                }

            };

        } catch (err) {

            enableVideoFallbackWakeLock(); 

        }

    } else {

        enableVideoFallbackWakeLock();

    }

}



async function releaseWakeLock() {

    if (currentWakeLock) {

        try {

            currentWakeLock.onrelease = null; 

            await currentWakeLock.release();

            currentWakeLock = null;

        } catch (err) {}

    }

    disableVideoFallbackWakeLock();

}



function handleWakeLockVisibilityChange() {

    if (document.visibilityState === 'visible') {

        if (shouldBeAwake()) requestWakeLock();

    } else {

        disableVideoFallbackWakeLock();

    }

}

document.addEventListener('visibilitychange', handleWakeLockVisibilityChange);



function toggleScreenWakeLockSetting() {

    if (shouldBeAwake()) requestWakeLock();

    else releaseWakeLock();

    saveToLocalStorage(); 

}

    function updateStrengthChart() {

        const ctx = document.getElementById('strengthChart').getContext('2d');

        if (charts.strength) {

            charts.strength.destroy();

        }



        // 1. Collect exercises that have at least 2 history entries (to show progress)

        const exercisesWithHistory = Object.entries(workoutData.exercises)

            .map(([id, data]) => ({ id, ...data }))

            .filter(ex => ex.history && ex.history.length >= 2)

            .sort((a, b) => b.history.length - a.history.length)

            .slice(0, 3);   // top 3 mostâ€‘performed exercises



        if (exercisesWithHistory.length === 0) {

            // No data â€“ display a placeholder message on the canvas

            ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

            ctx.font = '14px Inter, sans-serif';

            ctx.fillStyle = 'var(--gray-500)';

            ctx.textAlign = 'center';

            ctx.fillText('No enough history to show strength progress', ctx.canvas.width/2, ctx.canvas.height/2);

            return;

        }



        // 2. Prepare datasets: each exercise gets its own line

        const datasets = [];

        const colors = ['rgb(255,99,132)', 'rgb(54,162,235)', 'rgb(255,205,86)']; // pink, blue, yellow



        exercisesWithHistory.forEach((ex, idx) => {

            // Sort history by date (oldest first for consistent line drawing)

            const sortedHistory = ex.history

                .map(entry => ({

                    ...entry,

                    date: new Date(entry.date).getTime()   // convert to timestamp (ms)

                }))

                .filter(entry => entry.weight > 0 && entry.reps && entry.reps.length > 0) // skip bodyweight / incomplete

                .sort((a, b) => a.date - b.date);



            if (sortedHistory.length < 2) return; // not enough points for a line



            // Compute estimated 1RM for each session using the best set (max reps)

            const dataPoints = sortedHistory.map(entry => {

                const bestReps = Math.max(...entry.reps);

                const oneRM = estimate1RM(entry.weight, bestReps); // uses existing Epley formula

                return {

                    x: entry.date,

                    y: Math.round(oneRM)

                };

            });



            // Exercise name for label (clean up)

            const exerciseName = workoutProgram.exercises[ex.id]?.name || ex.id;

            const shortName = exerciseName.length > 20 ? exerciseName.substring(0,18)+'â€¦' : exerciseName;



            datasets.push({

                label: shortName,

                data: dataPoints,

                borderColor: colors[idx % colors.length],

                backgroundColor: 'transparent',

                borderWidth: 3,

                tension: 0.2,                // slight curve

                pointRadius: 4,

                pointHoverRadius: 6,

                pointBackgroundColor: colors[idx % colors.length],

                pointBorderColor: 'white',

                pointBorderWidth: 2,

                fill: false,

                spanGaps: true                // connect points even if some dates missing

            });

        });



        if (datasets.length === 0) {

            ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

            ctx.font = '14px Inter, sans-serif';

            ctx.fillStyle = 'var(--gray-500)';

            ctx.textAlign = 'center';

            ctx.fillText('Not enough weighted exercises with history', ctx.canvas.width/2, ctx.canvas.height/2);

            return;

        }



        // 3. Create chart with timeâ€‘based xâ€‘axis

        charts.strength = new Chart(ctx, {

            type: 'line',

            data: { datasets: datasets },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                plugins: {

                    legend: {

                        display: true,

                        position: 'top',

                        labels: { color: 'var(--gray-700)' }

                    },

                    tooltip: {

                        backgroundColor: 'rgba(15, 23, 42, 0.9)',

                        titleColor: 'white',

                        bodyColor: 'white',

                        borderColor: 'rgba(255,99,132,0.5)',

                        borderWidth: 1,

                        callbacks: {

                            title: (items) => {

                                const date = new Date(items[0].parsed.x);

                                return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });

                            },

                            label: (context) => {

                                const datasetLabel = context.dataset.label || '';

                                const oneRM = context.parsed.y;

                                return `${datasetLabel}: ${oneRM} lbs (estimated 1RM)`;

                            }

                        }

                    }

                },

                scales: {

                    x: {

                        type: 'linear',

                        position: 'bottom',

                        title: {

                            display: true,

                            text: 'Date',

                            color: 'var(--gray-600)'

                        },

                        ticks: {

                            color: 'var(--gray-500)',

                            maxTicksLimit: 8,

                            callback: function(value) {

                                const date = new Date(value);

                                return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

                            }

                        },

                        grid: { color: 'rgba(0,0,0,0.05)' }

                    },

                    y: {

                        beginAtZero: true,

                        title: {

                            display: true,

                            text: 'Estimated 1â€‘Rep Max (lbs)',

                            color: 'var(--gray-600)'

                        },

                        ticks: {

                            color: 'var(--gray-500)',

                            callback: value => value + ' lbs'

                        },

                        grid: { color: 'rgba(0,0,0,0.05)' }

                    }

                }

            }

        });

    }



    function updateFrequencyChart() {

        const ctx = document.getElementById('frequencyChart').getContext('2d');

        if (charts.frequency) {

            charts.frequency.destroy();

        }



        // 1. Configuration

        const weeksToShow = 8;                     // number of weeks displayed

        const targetFrequency = workoutData.user.settings?.workoutDays?.length || 3; // goal from settings (default 3)



        // 2. Build the last 'weeksToShow' complete weeks (ending on most recent Sunday)

        const periods = [];

        const labels = [];

        const workoutCounts = [];



        const today = new Date();

        // Find the most recent Sunday (end of week)

        const endOfWeek = new Date(today);

        endOfWeek.setDate(today.getDate() - today.getDay()); // Sunday of current week



        for (let i = 0; i < weeksToShow; i++) {

            const weekEnd = new Date(endOfWeek);

            weekEnd.setDate(endOfWeek.getDate() - (i * 7));

            const weekStart = new Date(weekEnd);

            weekStart.setDate(weekEnd.getDate() - 6); // 7â€‘day period



            periods.push({ start: weekStart, end: weekEnd });



            // Label: "Week of Mar 3" (start date)

            labels.push(weekStart.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));



            // Count workouts in this period

            const count = workoutData.workouts.filter(w => {

                const wDate = new Date(w.date);

                return wDate >= weekStart && wDate <= weekEnd;

            }).length;

            workoutCounts.push(count);

        }



        // Reverse so that most recent week is on the right (optional, but typical in charts)

        labels.reverse();

        workoutCounts.reverse();



        // 3. Calculate 4â€‘week rolling average (for trend line)

        const rollingAvg = [];

        for (let i = 0; i < workoutCounts.length; i++) {

            if (i < 3) {

                rollingAvg.push(null); // not enough points

            } else {

                const avg = (workoutCounts[i] + workoutCounts[i-1] + workoutCounts[i-2] + workoutCounts[i-3]) / 4;

                rollingAvg.push(Math.round(avg * 10) / 10); // one decimal

            }

        }



        // 4. Create datasets â€” P6E: accent-driven, theme-aware, high visibility

        try { p6RenderFreqStrip(targetFrequency, workoutCounts); } catch (e) {}

        const p6acc2 = p6CssVar('--p4-accent', '#4E9BFF');

        const freqGradient = ctx.createLinearGradient(0, 0, 0, 300);

        freqGradient.addColorStop(0, p6HexA(p6acc2, 0.95));

        freqGradient.addColorStop(1, p6HexA(p6acc2, 0.25));

        const barDataset = {

            type: 'bar',

            label: 'Workouts',

            data: workoutCounts,

            backgroundColor: freqGradient,

            borderColor: p6acc2,

            borderWidth: 0,

            borderRadius: 8,

            maxBarThickness: 30,

            order: 2  // bars behind line

        };



        const avgDataset = {

            type: 'line',

            label: '4â€‘week avg',

            data: rollingAvg,

            borderColor: 'rgb(255, 159, 64)',

            backgroundColor: 'transparent',

            borderWidth: 3,

            tension: 0.3,

            pointRadius: 4,

            pointHoverRadius: 6,

            pointBackgroundColor: 'rgb(255, 159, 64)',

            pointBorderColor: 'white',

            pointBorderWidth: 2,

            fill: false,

            order: 1  // line on top

        };



        // 5. Add a goal line annotation (horizontal line at target frequency)

        // Chart.js doesn't have builtâ€‘in annotation, but we can add a line dataset.

        const goalDataset = {

            type: 'line',

            label: 'Goal',

            data: Array(weeksToShow).fill(targetFrequency),

            borderColor: 'rgba(244, 63, 94, 0.85)',

            borderWidth: 2,

            borderDash: [5, 5],

            pointRadius: 0,

            fill: false,

            tension: 0,

            order: 0  // behind bars and avg

        };



        charts.frequency = new Chart(ctx, {

            type: 'bar',  // default type, but we'll override per dataset

            data: {

                labels: labels,

                datasets: [barDataset, avgDataset, goalDataset]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                plugins: {

                    legend: {

                        display: true,

                        position: 'top',

                        labels: { color: p6CssVar('--p4-text-2', '#cbd5e1'), usePointStyle: true, boxWidth: 8 }

                    },

                    tooltip: {

                        backgroundColor: 'rgba(15, 23, 42, 0.9)',

                        titleColor: 'white',

                        bodyColor: 'white',

                        borderColor: 'rgba(75, 192, 192, 0.5)',

                        borderWidth: 1,

                        callbacks: {

                            title: (items) => {

                                const index = items[0].dataIndex;

                                const period = periods[periods.length - 1 - index]; // reverse index because we reversed labels

                                const start = period.start.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

                                const end = period.end.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });

                                return `${start} â€“ ${end}`;

                            },

                            label: (context) => {

                                const datasetLabel = context.dataset.label || '';

                                const value = context.raw;

                                if (context.dataset.label === 'Goal') {

                                    return `Goal: ${value} workouts/week`;

                                }

                                if (context.dataset.label === '4â€‘week avg') {

                                    return value ? `Avg: ${value}` : null;

                                }

                                return `${datasetLabel}: ${value}`;

                            }

                        }

                    }

                },

                scales: {

                    y: {

                        beginAtZero: true,

                        max: Math.max(...workoutCounts, targetFrequency) + 1,

                        title: {

                            display: true,

                            text: 'Workouts per Week',

                            color: p6CssVar('--p4-text-3', '#94a3b8')

                        },

                        ticks: {

                            stepSize: 1,

                            color: p6CssVar('--p4-text-3', '#94a3b8'),

                            callback: value => value + (value === 1 ? ' workout' : ' workouts')

                        },

                        grid: { color: 'rgba(148, 163, 184, 0.16)' }

                    },

                    x: {

                        title: {

                            display: true,

                            text: 'Week Starting',

                            color: p6CssVar('--p4-text-3', '#94a3b8')

                        },

                        grid: { display: false },

                        ticks: { color: p6CssVar('--p4-text-3', '#94a3b8') }

                    }

                }

            }

        });

    }



function updateRPEChart() {

    const ctx = document.getElementById('rpeChart').getContext('2d');

    if (charts.rpe) {

        charts.rpe.destroy();

    }



    // 1. Determine date range based on current history filter

    const today = new Date();

    const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());

    let startDate = null;



    switch (currentHistoryFilter) {

        case 'week':

            startDate = new Date(startOfToday);

            startDate.setDate(startDate.getDate() - 7);

            break;

        case 'month':

            startDate = new Date(startOfToday);

            startDate.setMonth(startDate.getMonth() - 1);

            break;

        case 'year':

            startDate = new Date(startOfToday);

            startDate.setFullYear(startDate.getFullYear() - 1);

            break;

        case 'longevity':

            // For longevity, we still need a date filter â€“ use last 90 days or all?

            // Let's use last 90 days to keep it relevant

            startDate = new Date(startOfToday);

            startDate.setDate(startDate.getDate() - 90);

            break;

        default: // 'all'

            startDate = null; // no filter

    }



    // 2. Collect RPE values from workouts within the date range

    const rpeValues = [];

    workoutData.workouts.forEach(workout => {

        const workoutDate = new Date(workout.date);

        if (startDate && workoutDate < startDate) return;



        workout.exercises?.forEach(exercise => {

            if (exercise.actual?.rpe) {

                rpeValues.push(exercise.actual.rpe);

            }

        });

    });



    // 3. Handle empty data

    if (rpeValues.length === 0) {

        ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

        ctx.font = '14px Inter, sans-serif';

        ctx.fillStyle = 'var(--gray-500)';

        ctx.textAlign = 'center';

        ctx.fillText('No RPE data in selected period', ctx.canvas.width/2, ctx.canvas.height/2);

        return;

    }



    // 4. Count frequencies for RPE 1â€“10

    const counts = Array(10).fill(0);

    rpeValues.forEach(rpe => {

        if (rpe >= 1 && rpe <= 10) counts[rpe - 1]++;

    });



    // 5. Define intensity zones and colors

    const zoneColors = [

        '#10b981', '#10b981', '#10b981', '#10b981', // RPE 1-4 : easy (green)

        '#f59e0b', '#f59e0b',                       // RPE 5-6 : moderate (orange)

        '#ef4444', '#ef4444',                       // RPE 7-8 : hard (red)

        '#8b5cf6', '#8b5cf6'                        // RPE 9-10: max effort (purple)

    ];



    // 6. Calculate statistics

    const total = rpeValues.length;

    const average = (rpeValues.reduce((a, b) => a + b, 0) / total).toFixed(1);

    const median = (() => {

        const sorted = [...rpeValues].sort((a, b) => a - b);

        const mid = Math.floor(sorted.length / 2);

        return sorted.length % 2 !== 0 ? sorted[mid] : ((sorted[mid - 1] + sorted[mid]) / 2).toFixed(1);

    })();



    // 7. Create chart

    charts.rpe = new Chart(ctx, {

        type: 'bar',

        data: {

            labels: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],

            datasets: [{

                label: 'Number of Sets',

                data: counts,

                backgroundColor: zoneColors,

                borderColor: 'white',

                borderWidth: 1,

                borderRadius: 6

            }]

        },

        options: {

            responsive: true,

            maintainAspectRatio: false,

            plugins: {

                legend: { display: false },

                tooltip: {

                    backgroundColor: 'rgba(15, 23, 42, 0.9)',

                    titleColor: 'white',

                    bodyColor: 'white',

                    borderColor: 'rgba(75, 192, 192, 0.5)',

                    borderWidth: 1,

                    callbacks: {

                        label: (context) => {

                            const value = context.raw;

                            const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;

                            return `${value} sets (${percentage}%)`;

                        }

                    }

                },

                // Display stats as a subtitle using the chart's subtitle plugin? Not built-in.

                // We'll add a custom HTML element below the canvas instead.

            },

            scales: {

                y: {

                    beginAtZero: true,

                    title: {

                        display: true,

                        text: 'Number of Sets',

                        color: 'var(--gray-600)'

                    },

                    ticks: {

                        stepSize: 1,

                        color: 'var(--gray-500)',

                        precision: 0

                    },

                    grid: { color: 'rgba(0, 0, 0, 0.05)' }

                },

                x: {

                    title: {

                        display: true,

                        text: 'RPE (Rate of Perceived Exertion)',

                        color: 'var(--gray-600)'

                    },

                    grid: { display: false },

                    ticks: { color: 'var(--gray-500)' }

                }

            }

        }

    });



    // 8. Add statistics below the canvas

    // We'll find or create a div for stats

    const chartContainer = document.querySelector('#rpeChart').parentNode;

    let statsDiv = document.getElementById('rpeChartStats');

    if (!statsDiv) {

        statsDiv = document.createElement('div');

        statsDiv.id = 'rpeChartStats';

        statsDiv.style.marginTop = '10px';

        statsDiv.style.fontSize = '0.9rem';

        statsDiv.style.color = 'var(--gray-600)';

        statsDiv.style.textAlign = 'center';

        chartContainer.appendChild(statsDiv);

    }



    // Determine zone distribution

    const easy = counts.slice(0,4).reduce((a,b) => a+b, 0);

    const moderate = counts.slice(4,6).reduce((a,b) => a+b, 0);

    const hard = counts.slice(6,8).reduce((a,b) => a+b, 0);

    const max = counts.slice(8,10).reduce((a,b) => a+b, 0);



    statsDiv.innerHTML = `

        <div style="display: flex; justify-content: space-around; flex-wrap: wrap; gap: 10px;">

            <span><strong>Average RPE:</strong> ${average}</span>

            <span><strong>Median RPE:</strong> ${median}</span>

            <span><strong>Total sets:</strong> ${total}</span>

        </div>

        <div style="display: flex; justify-content: space-around; flex-wrap: wrap; margin-top: 5px;">

            <span style="color: #10b981;">â— Easy (1-4): ${easy}</span>

            <span style="color: #f59e0b;">â— Moderate (5-6): ${moderate}</span>

            <span style="color: #ef4444;">â— Hard (7-8): ${hard}</span>

            <span style="color: #8b5cf6;">â— Max (9-10): ${max}</span>

        </div>

    `;

}



