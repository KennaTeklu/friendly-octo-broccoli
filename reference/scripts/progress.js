// ============================================================
// EXTRACTED FROM: index (24).html (cautious-enigma repo)
// progress.js — masterpiece analytics engine (11-component radar, goal cycles, projections)
// Source line ranges (1-indexed, inclusive):
//   L47920-49046  (masterpiece engine (11 fitness components, scoring, goal cycles, dashboard block builder, library chips, progress analytics, workout banner, 5-year forecast))
// Total source lines: 1127
// ============================================================

// ---- BEGIN extracted from index (24).html L47920-49046 (masterpiece engine (11 fitness components, scoring, goal cycles, dashboard block builder, library chips, progress analytics, workout banner, 5-year forecast)) ----
// ============================================================

// MASTERPIECE ENGINE — Part 1: Components, Scoring, Goal Cycles

// The 11 fitness components become first-class citizens.

// ============================================================

'use strict';



const MP_COMPONENTS = [

  { id: 'muscular_strength', name: 'Muscle Strength', short: 'Strength', icon: 'fa-dumbbell', color: '#e74c3c',

    tagline: 'Maximal force production',

    desc: 'The ability of a muscle or muscle group to exert maximal force against resistance. Built through heavy, low-rep, high-quality sets on compound movements.',

    train: ['Train 2-4 sets of 3-6 reps at 80-90% of your 1RM on big lifts', 'Rest 2-4 minutes between heavy sets', 'Progress by adding 2.5-5 lbs whenever all sets hit target reps', 'Anchor each session with one heavy compound before accessory work'],

    assess: 'Estimated 1RM on squat, bench, deadlift and press — your app tracks these automatically from every logged set.' },

  { id: 'muscular_endurance', name: 'Muscle Endurance', short: 'Endurance', icon: 'fa-redo', color: '#3498db',

    tagline: 'Repeat effort capacity',

    desc: 'The ability of muscles to perform repeated contractions over time without fatigue. Built with moderate loads, high reps, short rests and time-under-tension work.',

    train: ['Work 2-4 sets of 12-20+ reps with strict form', 'Keep rests short: 30-60 seconds', 'Use tempo work (3-second lowerings) to extend time under tension', 'Finish sessions with a burnout set on one isolation move'],

    assess: 'Rep maxes at moderate loads — how many clean push-ups, goblet squats or rows you can chain together.' },

  { id: 'cardiorespiratory_endurance', name: 'Cardiorespiratory Endurance', short: 'Cardio', icon: 'fa-heartbeat', color: '#e67e22',

    tagline: 'Heart and lung engine',

    desc: 'The capacity of the heart, lungs and blood vessels to deliver oxygen during sustained whole-body activity. The strongest single predictor of long-term health.',

    train: ['Build a base: 2-3 zone-2 sessions of 20-40 minutes weekly', 'Add 1 interval session (e.g., 6 × 2 min hard / 2 min easy)', 'Keep the easy days truly easy — nasal breathing pace', 'Progress total weekly minutes by about 10%'],

    assess: 'Sustained pace over distance or time — a 20-30 minute steady effort you can hold while speaking short sentences.' },

  { id: 'flexibility', name: 'Flexibility', short: 'Flexibility', icon: 'fa-spa', color: '#9b59b6',

    tagline: 'Range of motion',

    desc: 'The ability of joints to move through their full, intended range. Static stretching after training and dedicated mobility sessions keep tissue long and joints healthy.',

    train: ['Hold static stretches 30-60 seconds at mild tension, never pain', 'Breathe slow: 4-second inhale, 6-8 second exhale', 'Stretch the muscles you trained the same day', 'Train range daily — consistency beats intensity'],

    assess: 'Joint-by-joint range checks: can you touch your toes, squat deep, reach overhead and rotate fully?' },

  { id: 'body_composition', name: 'Body Composition', short: 'Body Comp', icon: 'fa-balance-scale', color: '#1abc9c',

    tagline: 'Muscle to mass ratio',

    desc: 'The ratio of lean mass to fat mass. Built by combining resistance training, sufficient protein, sensible cardio and consistent sleep.',

    train: ['Hit a protein target of roughly 0.7-1 g per lb of bodyweight', 'Combine lifting 3-4x weekly with 2-3 cardio sessions', 'Weigh in consistently (same time, same conditions) and track the weekly trend', 'Aim for slow changes: 0.5-1% of bodyweight per week'],

    assess: 'Bodyweight trend plus waist measurement plus how your clothes fit — trends over weeks matter more than any single day.' },

  { id: 'power', name: 'Power', short: 'Power', icon: 'fa-bolt', color: '#f39c12',

    tagline: 'Force × speed',

    desc: 'The ability to express strength explosively. Jumps, throws and speed work convert raw strength into athletic output — and it fades fastest with age, so train it year-round.',

    train: ['Keep sets of 3-6 explosive reps with full recovery (60-90s)', 'Move the load or your body with maximum intent every rep', 'Jumps, med-ball throws and speed lifts before heavy strength work', 'Stop the set when explosiveness drops — quality only'],

    assess: 'Vertical or broad jump height/distance and bar speed on moderate loads.' },

  { id: 'speed', name: 'Speed', short: 'Speed', icon: 'fa-tachometer-alt', color: '#e91e63',

    tagline: 'Top velocity',

    desc: 'The ability to move the body rapidly. Sprint mechanics, fast footwork and full-recovery sprint reps develop genuine top-end speed.',

    train: ['Sprint at 95-100% effort with FULL recovery between reps (2-4 min)', 'Keep acceleration reps short: 10-20 yards', 'Drill mechanics: A-skips, high knees, fast-leg runs', 'Never sprint with tight hamstrings — warm up thoroughly'],

    assess: 'Timed short sprints (10-40 yards) — the app logs your drill sessions and you can track split times.' },

  { id: 'agility', name: 'Agility', short: 'Agility', icon: 'fa-running', color: '#2ecc71',

    tagline: 'Change of direction',

    desc: 'The ability to decelerate, re-accelerate and change direction quickly while staying in control. Cone drills, ladder work and reactive games build it.',

    train: ['Drop the hips before every cut — low and wide base', 'Decelerate first, then explode out', 'Keep eyes up, not on your feet', 'Short bouts: 5-15 seconds of work, full recovery'],

    assess: 'Timed shuttles like the 5-10-5 pro agility and T-drill — repeatable, comparable, honest.' },

  { id: 'balance', name: 'Balance', short: 'Balance', icon: 'fa-street-view', color: '#16a085',

    tagline: 'Stability under you',

    desc: 'The ability to hold your center of gravity over your base of support — static and dynamic. Falls risk, joint health and athletic control all trace back to it.',

    train: ['Single-leg stands: progress eyes open → eyes closed → unstable surface', 'Add slow limb reaches while balancing', 'Train barefoot when possible to wake the feet up', '2-3 minutes of dedicated balance work beats an hour of occasionally'],

    assess: 'Single-leg stance time with eyes closed — under 10s needs work, 30s+ is strong for any age.' },

  { id: 'coordination', name: 'Coordination', short: 'Coordination', icon: 'fa-project-diagram', color: '#2980b9',

    tagline: 'Movement intelligence',

    desc: 'The ability to combine movements smoothly and efficiently — the brain-body connection. Skipping, crawling patterns, ladder drills and sport play build it.',

    train: ['Learn patterns slowly, then speed up only when clean', 'Alternate sides evenly — both hemispheres, both limbs', 'Reset after every mistake; never rehearse sloppiness', 'Try new movements often — novelty is the point'],

    assess: 'Quality of new movement acquisition: how fast a new pattern becomes smooth and repeatable.' },

  { id: 'reaction_time', name: 'Reaction Time', short: 'Reaction', icon: 'fa-stopwatch', color: '#c0392b',

    tagline: 'Respond to the world',

    desc: 'How fast you perceive and respond to a stimulus. Ball drops, random cues and partner games sharpen the perception-action loop.',

    train: ['React to unpredictable cues — randomness is essential', 'Stay in a ready athletic stance, weight on the balls of the feet', 'Full quality reps with complete recovery', 'Partner games and reaction balls beat solo predictable drills'],

    assess: 'Reaction ball return counts and partner-cue response drills — track catches per round.' }

];



const MP_COMPONENT_MAP = {};

MP_COMPONENTS.forEach(c => MP_COMPONENT_MAP[c.id] = c);



// Group-key → component fallback (for entries without explicit fitnessComponents)

const MP_GROUP_COMPONENT_FALLBACK = {

  stretching_static: ['flexibility'],

  mobility_dynamic: ['flexibility', 'coordination'],

  athletic_power: ['power'],

  athletic_speed: ['speed'],

  athletic_agility: ['agility'],

  athletic_balance: ['balance'],

  athletic_coordination: ['coordination'],

  athletic_reaction: ['reaction_time'],

  cardio_condition: ['cardiorespiratory_endurance', 'body_composition']

};



function mpExerciseComponentsForId(exerciseId) {

  // Returns the resolved fitnessComponents for an exercise id.

  try {

    var info = (typeof mpGetLookup === 'function') ? mpGetLookup()[exerciseId] : null;

    if (info && info.ex) return mpExerciseComponents(info.ex, info.group);

    if (typeof getExerciseById === 'function') {

      var ex = getExerciseById(exerciseId);

      if (ex) return mpExerciseComponents(ex, null);

    }

  } catch(e){}

  return [];

}

function mpExerciseComponents(ex, groupKey) {

  if (!ex) return [];

  // 1. Registry first (canonical, extracted from source)

  if (ex.name && window.P4_EXERCISE_COMPONENTS) {

    var rid = ex.name.toLowerCase().replace(/[^a-z0-9]+/g, '_').replace(/^_|_$/g, '');

    var reg = window.P4_EXERCISE_COMPONENTS[rid];

    if (reg && Array.isArray(reg.components) && reg.components.length) return reg.components.slice();

  }

  // 2. Exercise's own field

  if (Array.isArray(ex.fitnessComponents) && ex.fitnessComponents.length) return ex.fitnessComponents;

  // 3. Group fallback

  const fb = MP_GROUP_COMPONENT_FALLBACK[groupKey];

  if (fb) return fb;

  // 4. Last-resort heuristic

  const equip = (ex.equipment || '').toLowerCase();

  const reps = String(ex.defaultReps || '');

  const heavy = (ex.strengthIndex || 0) >= 1.2 || /^\d(-\d)?$/.test(reps);

  return heavy ? ['muscular_strength', 'muscular_endurance'] : ['muscular_endurance', 'muscular_strength'];

}



// Build a lookup: exerciseId -> {ex, group}

let MP_EX_LOOKUP = null;

function mpBuildExerciseLookup() {

  const map = {};

  for (const group in ultimateExerciseLibrary) {

    ultimateExerciseLibrary[group].forEach(ex => {

      const id = ex.name.toLowerCase().replace(/\s+/g, '_');

      map[id] = { ex: ex, group: group, id: id, components: mpExerciseComponents(ex, group) };

    });

  }

  MP_EX_LOOKUP = map;

  return map;

}

function mpGetLookup() {

  if (!MP_EX_LOOKUP) mpBuildExerciseLookup();

  return MP_EX_LOOKUP;

}



// P4: actual component distribution across a workout's exercises.

// Each exercise contributes 1 unit, split equally among its own components.

// Returns array sorted by weight desc: [{id, weight, pct}]

function mpComputeWorkoutComponentDistribution(workout) {

  if (!workout || !Array.isArray(workout.exercises) || !workout.exercises.length) return [];

  const lookup = mpGetLookup();

  const tally = {};

  let total = 0;

  workout.exercises.forEach(function (ex) {

    if (!ex) return;

    const info = ex.id ? lookup[ex.id] : null;

    const comps = (info && info.components && info.components.length)

      ? info.components

      : (Array.isArray(ex.fitnessComponents) ? ex.fitnessComponents : []);

    if (!comps.length) return;

    const each = 1 / comps.length;

    comps.forEach(function (c) { tally[c] = (tally[c] || 0) + each; });

    total += 1;

  });

  if (!total) return [];

  return Object.keys(tally)

    .map(function (cid) { return { id: cid, weight: tally[cid], pct: Math.round(tally[cid] / total * 100) }; })

    .sort(function (a, b) { return b.weight - a.weight; });

}



// ============================================================

// COMPONENT SCORING — 0-100 per component from real history

// ============================================================

const MP_SCORE_TARGETS = { // sessions in last 28 days that score "full frequency"

  muscular_strength: 10, muscular_endurance: 10, cardiorespiratory_endurance: 8, flexibility: 12,

  body_composition: 8, power: 4, speed: 4, agility: 4, balance: 6, coordination: 4, reaction_time: 4

};



function mpSessionComponents(workout) {

  const lookup = mpGetLookup();

  const comps = new Set();

  const src = (workout.exercises && Array.isArray(workout.exercises)) ? workout.exercises : [];

  src.forEach(item => {

    const id = item.id || (item.name ? String(item.name).toLowerCase().replace(/\s+/g, '_') : null);

    const info = id ? lookup[id] : null;

    if (!info) return;

    const done = item.skipped ? false : true;

    if (!done) return;

    info.components.forEach(c => comps.add(c));

  });

  return comps;

}



function mpComponentScores() {

  const scores = {};

  MP_COMPONENTS.forEach(c => scores[c.id] = { sessions28: 0, lastDone: null, score: 0, recency: 0 });

  const now = Date.now();

  (workoutData.workouts || []).forEach(w => {

    const dstr = w.dateCompleted || w.date || (w.activeDates && w.activeDates[0]);

    if (!dstr) return;

    const t = new Date(dstr).getTime();

    if (isNaN(t)) return;

    const days = (now - t) / 86400000;

    if (days > 90) return;

    const comps = mpSessionComponents(w);

    comps.forEach(cid => {

      if (!scores[cid]) return;

      if (days <= 28) scores[cid].sessions28++;

      if (!scores[cid].lastDone || days < scores[cid].lastDone) scores[cid].lastDone = days;

    });

  });

  MP_COMPONENTS.forEach(c => {

    const s = scores[c.id];

    const target = MP_SCORE_TARGETS[c.id] || 6;

    const freq = Math.min(1, s.sessions28 / target);

    const rec = s.lastDone === null ? 0 : Math.max(0, 1 - s.lastDone / 21);

    // score: frequency dominates, recency keeps it honest

    let score = Math.round(freq * 78 + rec * 22);

    if (s.sessions28 === 0 && s.lastDone === null) score = 0;

    s.recency = rec;

    s.score = Math.max(0, Math.min(100, score));

  });

  return scores;

}



function mpScoreLevel(score) {

  if (score >= 93) return 'Elite';

  if (score >= 80) return 'Advanced';

  if (score >= 65) return 'Proficient';

  if (score >= 45) return 'Developing';

  if (score >= 25) return 'Beginner';

  return 'Untrained';

}



// ============================================================

// GOAL CYCLE ENGINE — 3-month rotating goals + 5-year vision

// ============================================================

const MP_CYCLE_DAYS = 91; // 13 weeks



function mpCycleId() { return 'cycle_' + Date.now().toString(36); }



function mpExerciseEst1RM(exId) {

  const rec = workoutData.exercises[exId];

  if (rec && typeof rec.mu === 'number' && rec.mu > 0) return Math.round(rec.mu);

  return null;

}



function mpTopLifts() {

  // top 4 lifts with most history, having est 1RM

  const rows = [];

  for (const [id, rec] of Object.entries(workoutData.exercises || {})) {

    const h = rec.history || [];

    if (!h.length) continue;

    const mu = rec.mu || 0;

    rows.push({ id: id, sessions: h.length, mu: mu, name: id.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase()) });

  }

  rows.sort((a, b) => b.sessions - a.sessions);

  return rows.slice(0, 4);

}



function mpWeeklyAvg(workouts, valueFn) {

  if (!workouts.length) return 0;

  const span = 28; // last 28 days

  const cutoff = Date.now() - span * 86400000;

  let total = 0, count = 0;

  workouts.forEach(w => {

    const dstr = w.dateCompleted || w.date || (w.activeDates && w.activeDates[0]);

    if (!dstr) return;

    if (new Date(dstr).getTime() >= cutoff) { total += valueFn(w); count++; }

  });

  if (!count) return 0;

  return (total / span) * 7; // weekly average

}



function mpGoalCandidates(difficultyFactor) {

  const df = difficultyFactor || 1;

  const cands = [];

  const user = workoutData.user || {};

  const workouts = workoutData.workouts || [];



  // 1. Strength gains on top lifts

  mpTopLifts().slice(0, 2).forEach(lift => {

    const base = lift.mu;

    if (!base || base < 20) return;

    const gain = Math.max(5, Math.round(base * 0.055 * df));

    cands.push({

      key: 'est1rm:' + lift.id,

      component: 'muscular_strength',

      title: 'Add ' + gain + ' lbs to your ' + lift.name,

      metric: 'est1rm', metricArg: lift.id, unit: 'lbs',

      baseline: base, target: base + gain,

      hint: 'Drive this with 3-6 rep strength sets on the main lift day. Your estimate updates after every logged set.'

    });

  });



  // 2. Consistency: workouts this cycle

  const weekly = mpWeeklyAvg(workouts, w => 1) || (user.settings && user.settings.workoutDays ? user.settings.workoutDays.length : 3);

  const wTarget = Math.max(8, Math.round(weekly * 13 * 0.92 * df));

  cands.push({

    key: 'workouts_count', component: 'body_composition',

    title: 'Complete ' + wTarget + ' workouts this cycle',

    metric: 'workouts_count', unit: 'sessions',

    baseline: 0, target: wTarget,

    hint: 'Consistency is the multiplier on every other goal. Streaks count double psychologically.'

  });



  // 3. Total volume

  const volWeekly = mpWeeklyAvg(workouts, w => (w.summary && w.summary.totalVolume) || 0);

  if (volWeekly > 0) {

    const vTarget = Math.round(volWeekly * 13 * 0.95 * df);

    cands.push({

      key: 'volume_total', component: 'muscular_endurance',

      title: 'Accumulate ' + Math.round(vTarget).toLocaleString() + ' lbs of training volume',

      metric: 'volume_total', unit: 'lbs',

      baseline: 0, target: vTarget,

      hint: 'Volume is the quiet driver of muscle. Every logged set moves this needle.'

    });

  }



  // 4. Weakest 2 non-strength components: sessions goal

  const scores = mpComponentScores();

  const weak = MP_COMPONENTS

    .filter(c => !['muscular_strength', 'body_composition'].includes(c.id))

    .sort((a, b) => scores[a.id].score - scores[b.id].score)

    .slice(0, 2);

  weak.forEach(c => {

    const t = Math.max(6, Math.round((MP_SCORE_TARGETS[c.id] || 6) * 0.9));

    cands.push({

      key: 'sessions:' + c.id, component: c.id,

      title: 'Train ' + c.name + ' in ' + t + ' sessions',

      metric: 'sessions', metricArg: c.id, unit: 'sessions',

      baseline: 0, target: t,

      hint: c.train[0]

    });

  });



  // 5. All-11 sweep

  cands.push({

    key: 'distinct_components', component: 'coordination',

    title: 'Cover all 11 fitness components at least once',

    metric: 'distinct_components', unit: 'of 11',

    baseline: 0, target: 11,

    hint: 'The weekly rotation covers a component a day — follow the plan and this completes itself.'

  });



  return cands;

}



function mpExtractMetric(metric, arg) {

  const cycle = workoutData.user.goalCycle;

  const startT = cycle ? new Date(cycle.startDate).getTime() : 0;

  const workouts = (workoutData.workouts || []).filter(w => {

    const dstr = w.dateCompleted || w.date || (w.activeDates && w.activeDates[0]);

    return dstr && new Date(dstr).getTime() >= startT;

  });

  switch (metric) {

    case 'est1rm': return mpExerciseEst1RM(arg) || 0;

    case 'workouts_count': return workouts.length;

    case 'volume_total': return workouts.reduce((t, w) => t + ((w.summary && w.summary.totalVolume) || 0), 0);

    case 'sessions': {

      let n = 0;

      workouts.forEach(w => { if (mpSessionComponents(w).has(arg)) n++; });

      return n;

    }

    case 'distinct_components': {

      const set = new Set();

      workouts.forEach(w => mpSessionComponents(w).forEach(c => set.add(c)));

      return set.size;

    }

  }

  return 0;

}



function mpLinearProjection(history, baseline, target) {

  // history: [{date, value}] sorted asc; fit slope over the recent window

  const pts = history.slice(-14);

  if (pts.length < 2) return { rate: 0, projected: null };

  const t0 = new Date(pts[0].date).getTime();

  const xs = pts.map(p => (new Date(p.date).getTime() - t0) / 86400000);

  const ys = pts.map(p => p.value);

  const n = pts.length;

  const sumX = xs.reduce((a, b) => a + b, 0), sumY = ys.reduce((a, b) => a + b, 0);

  const sumXY = xs.reduce((a, x, i) => a + x * ys[i], 0);

  const sumX2 = xs.reduce((a, x) => a + x * x, 0);

  const denom = n * sumX2 - sumX * sumX;

  if (denom === 0) return { rate: 0, projected: null };

  const slope = (n * sumXY - sumX * sumY) / denom; // units per day

  const last = pts[pts.length - 1];

  const lastT = new Date(last.date).getTime();

  const gap = target - last.value;

  let projected = null;

  if (slope > 1e-9 && gap > 0) {

    projected = new Date(lastT + (gap / slope) * 86400000).toISOString();

  }

  return { rate: slope, projected: projected };

}



function mpUpdateGoalCycle() {

  const user = workoutData.user;

  if (!user) return null;

  let cycle = user.goalCycle;

  const now = new Date();

  if (!cycle || new Date(cycle.endDate) <= now) {

    cycle = mpCreateGoalCycle(cycle);

  }

  // update each goal

  cycle.goals.forEach(g => {

    const raw = mpExtractMetric(g.metric, g.metricArg);

    g.current = raw;

    // history bookkeeping (one point per day)

    const today = now.toISOString().split('T')[0];

    if (!Array.isArray(g.history)) g.history = [];

    const lastPt = g.history[g.history.length - 1];

    if (!lastPt || lastPt.date !== today) {

      g.history.push({ date: today, value: raw });

    } else {

      lastPt.value = raw;

    }

    if (g.history.length > 60) g.history = g.history.slice(-60);

    // projection + confidence + status

    const proj = mpLinearProjection(g.history, g.baseline, g.target);

    g.rate = proj.rate;

    g.projectedDate = g.current >= g.target ? now.toISOString() : (proj.projected || null);

    const totalMs = new Date(cycle.endDate) - new Date(cycle.startDate);

    const leftMs = new Date(cycle.endDate) - now;

    const needMs = g.current >= g.target ? 0 : (g.target - g.current) / (proj.rate || 1e-9) * 86400000;

    if (g.current >= g.target) { g.confidence = 100; g.status = 'achieved'; }

    else if (!proj.rate || proj.rate <= 0) {

      // no progress slope yet — base on elapsed pace vs required pace

      const elapsed = now - new Date(cycle.startDate);

      const expected = (g.target - g.baseline) * (elapsed / totalMs);

      const onPace = g.current - g.baseline >= expected;

      g.confidence = Math.round(Math.max(8, Math.min(80, (g.current - g.baseline) / Math.max(1, g.target - g.baseline) * 100)));

      g.status = onPace ? 'on_track' : 'behind';

    } else {

      g.confidence = Math.min(97, Math.max(5, Math.round(100 * Math.min(1, leftMs / needMs))));

      g.status = g.projectedDate && new Date(g.projectedDate) <= new Date(cycle.endDate) ? 'on_track'

        : (leftMs > needMs * 0.75 ? 'at_risk' : 'behind');

    }

  });

  cycle.lastUpdated = now.toISOString();

  cycle.achievementRate = cycle.goals.filter(g => g.status === 'achieved').length / cycle.goals.length;

  user.goalCycle = cycle;

  return cycle;

}



function mpCreateGoalCycle(prevCycle) {

  const now = new Date();

  const start = new Date(now);

  const end = new Date(now.getTime() + MP_CYCLE_DAYS * 86400000);

  // adaptive difficulty from previous cycle achievement

  let df = 1;

  if (prevCycle) {

    const achieved = (prevCycle.goals || []).filter(g => (g.current || 0) >= g.target).length;

    const rate = prevCycle.goals && prevCycle.goals.length ? achieved / prevCycle.goals.length : 0.5;

    df = Math.max(0.8, Math.min(1.35, 0.82 + rate * 0.42));

  }

  const cands = mpGoalCandidates(df);

  // pick: 1 strength (if exists) + consistency + volume + weakest + all-11, max 5, min 3

  const picked = [];

  const take = pred => { const i = cands.findIndex(pred); if (i >= 0) picked.push(cands.splice(i, 1)[0]); };

  take(c => c.metric === 'est1rm');

  take(c => c.key === 'workouts_count');

  take(c => c.key === 'volume_total');

  take(c => c.key === 'sessions');

  take(c => c.key === 'distinct_components');

  const goals = (picked.length ? picked : cands.slice(0, 4)).map((c, i) => ({

    id: 'g' + i + '_' + Date.now().toString(36),

    key: c.key, title: c.title, component: c.component,

    metric: c.metric, metricArg: c.metricArg, unit: c.unit,

    baseline: c.baseline, target: c.target, current: 0,

    hint: c.hint, history: [], status: 'on_track', confidence: 50

  }));

  return {

    id: mpCycleId(), startDate: start.toISOString(), endDate: end.toISOString(),

    difficultyFactor: df, goals: goals,

    cycleNumber: ((prevCycle && prevCycle.cycleNumber) || 0) + 1,

    previousCycleSummary: prevCycle ? {

      cycleNumber: prevCycle.cycleNumber,

      goals: (prevCycle.goals || []).map(g => ({ title: g.title, current: g.current, target: g.target, status: g.status }))

    } : null,

    createdAt: now.toISOString(), lastUpdated: now.toISOString()

  };

}



function mpRegenerateGoalCycle() {

  const user = workoutData.user;

  const fresh = mpCreateGoalCycle(user.goalCycle || null);

  user.goalCycle = fresh;

  mpUpdateGoalCycle();

  if (typeof saveWorkoutData === 'function') saveWorkoutData();

  return user.goalCycle;

}



// ============================================================

// 5-YEAR VISION LADDER

// ============================================================

function mpFiveYearLadder() {

  const lifts = mpTopLifts();

  const total = lifts.reduce((t, l) => t + (l.mu || 0), 0);

  const rows = [];

  // conservative compounding: 8% yr1 decaying to 3% yr5

  const rates = [0.09, 0.075, 0.06, 0.05, 0.04];

  let cur = total || 0;

  const nowY = new Date().getFullYear();

  for (let i = 0; i < 5; i++) {

    cur = cur * (1 + rates[i]);

    rows.push({ year: nowY + i + 1, total: Math.round(cur) });

  }

  return { hasData: total > 0, currentTotal: Math.round(total), rows: rows, lifts: lifts };

}



// ============================================================

// MASTERPIECE ENGINE — Part 2: UI Renderers + Hooks + CSS

// ============================================================

'use strict';



// ---------- shared UI helpers ----------

function mpEsc(str) {

  return String(str == null ? '' : str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');

}

function mpCompBadge(cid, small) {

  const c = MP_COMPONENT_MAP[cid];

  if (!c) return '';

  return '<span class="mp-comp-badge' + (small ? ' mp-sm' : '') + '" style="background:' + c.color + '1a; color:' + c.color + '; border:1px solid ' + c.color + '44;">' +

    '<i class="fas ' + c.icon + '"></i> ' + mpEsc(c.short) + '</span>';

}

function mpCompBadges(comps, small) {

  return (comps || []).map(c => mpCompBadge(c, small)).join(' ');

}

function mpStatusChip(status) {

  const map = {

    achieved: ['#1abc9c', 'Achieved', 'fa-trophy'],

    on_track: ['#27ae60', 'On Track', 'fa-check-circle'],

    at_risk: ['#f39c12', 'At Risk', 'fa-exclamation-triangle'],

    behind: ['#e74c3c', 'Behind', 'fa-arrow-down'],

    on_pace: ['#27ae60', 'On Pace', 'fa-check-circle']

  };

  const m = map[status] || map.on_track;

  return '<span class="mp-status" style="background:' + m[0] + '1a; color:' + m[0] + ';"><i class="fas ' + m[2] + '"></i> ' + m[1] + '</span>';

}

function mpProgressPct(goal) {

  const span = Math.max(1, goal.target - (goal.baseline || 0));

  const done = Math.max(0, goal.current - (goal.baseline || 0));

  return Math.max(0, Math.min(100, Math.round(done / span * 100)));

}



// ---------- LIBRARY: component chips + filter ----------

let mpLibraryComponentFilter = null;



function mpRenderLibraryChips() {

  const holder = document.getElementById('mpComponentChips');

  if (!holder) return;

  const lookup = mpGetLookup();

  const counts = {};

  for (const id in lookup) {

    lookup[id].components.forEach(c => { counts[c] = (counts[c] || 0) + 1; });

  }

  const total = Object.keys(lookup).length;

  let html = '<button class="mp-chip' + (!mpLibraryComponentFilter ? ' active' : '') + '" data-comp="">All <b>' + total + '</b></button>';

  MP_COMPONENTS.forEach(c => {

    html += '<button class="mp-chip' + (mpLibraryComponentFilter === c.id ? ' active' : '') + '" data-comp="' + c.id + '" style="--chipc:' + c.color + '">' +

      '<i class="fas ' + c.icon + '"></i> ' + mpEsc(c.short) + ' <b>' + (counts[c.id] || 0) + '</b></button>';

  });

  holder.innerHTML = html;

  holder.querySelectorAll('.mp-chip').forEach(btn => {

    btn.addEventListener('click', () => {

      const v = btn.getAttribute('data-comp') || null;

      mpLibraryComponentFilter = (mpLibraryComponentFilter === v) ? null : v;

      if (typeof fuseIndex !== 'undefined') fuseIndex = null;

      if (typeof renderLibrary === 'function') renderLibrary();

    });

  });

}



function mpLibraryFilteredList(list) {

  if (!mpLibraryComponentFilter) return list;

  const lookup = mpGetLookup();

  return list.filter(ex => {

    const info = lookup[ex.id];

    const comps = info ? info.components : (ex.fitnessComponents || []);

    return comps.includes(mpLibraryComponentFilter);

  });

}



// Enhance rendered library cards with component badges + Details button

function mpEnhanceLibraryCards() {

  const grid = document.getElementById('libraryGrid');

  if (!grid) return;

  const lookup = mpGetLookup();

  grid.querySelectorAll('.chart-card').forEach(card => {

    if (card.dataset.mpDone) return;

    card.dataset.mpDone = '1';

    const addBtn = card.querySelector('button[onclick*="addExerciseToWorkoutFromLibrary"]');

    if (!addBtn) return;

    const m = addBtn.getAttribute('onclick').match(/addExerciseToWorkoutFromLibrary\('([^']+)'/);

    if (!m) return;

    const id = m[1];

    const info = lookup[id];

    if (!info) return;

    const titleEl = card.querySelector('h3');

    if (titleEl && !card.querySelector('.mp-comp-badge')) {

      const div = document.createElement('div');

      div.className = 'mp-card-badges';

      // P4: normalize library card badges to canonical component order

      const libComps = (typeof P4 !== 'undefined' && P4.normalizeComponents) ? P4.normalizeComponents(info.components) : info.components;

      div.innerHTML = mpCompBadges(libComps, true);

      titleEl.parentNode.insertBefore(div, titleEl.nextSibling);

    }

    // Details button before Add-to-Workout

    const details = document.createElement('button');

    details.className = 'btn btn-sm btn-info';

    details.style.flex = '1';

    details.innerHTML = '<i class="fas fa-book-open"></i> Details';

    details.addEventListener('click', () => mpOpenExerciseDetail(id));

    addBtn.parentNode.insertBefore(details, addBtn);

  });

}



// ---------- EXERCISE DETAIL MODAL ----------

function mpOpenExerciseDetail(exId) {

  const lookup = mpGetLookup();

  const info = lookup[exId];

  if (!info) return;

  const ex = info.ex;

  // P4: normalize the exercise's components to canonical order for consistent display

  const comps = (typeof P4 !== 'undefined' && P4.normalizeComponents) ? P4.normalizeComponents(info.components) : info.components;

  const compMain = MP_COMPONENT_MAP[comps[0]];

  const record = (typeof workoutData !== 'undefined' && workoutData.exercises[exId]) || {};

  const muscleTags = (ex.muscles || []).map(m =>

    '<span class="muscle-tag clickable" data-muscle="' + m + '" onclick="showMuscleImage(this.dataset.muscle)">' +

    (typeof getMuscleDisplayName === 'function' ? getMuscleDisplayName(m) : m) + '</span>').join('');

  // fiber profile of primary muscle

  let fiberHtml = '';

  if (typeof MUSCLE_FIBER_PROFILES !== 'undefined') {

    const primary = (ex.muscles && ex.muscles[0]) || null;

    const prof = primary ? MUSCLE_FIBER_PROFILES[primary] : null;

    if (prof) {

      const fast = prof.fastTwitch !== undefined ? prof.fastTwitch : prof.fast;

      const slow = prof.slowTwitch !== undefined ? prof.slowTwitch : prof.slow;

      fiberHtml = '<div class="mp-detail-section"><h4><i class="fas fa-dna"></i> Muscle Fiber Profile — ' +

        (typeof getMuscleDisplayName === 'function' ? getMuscleDisplayName(primary) : primary) + '</h4>' +

        '<div class="mp-fiber-bars">' +

        '<div class="mp-fiber-row"><span>Fast-twitch</span><div class="mp-fiber-bar"><i style="width:' + fast + '%; background:#e74c3c;"></i></div><b>' + fast + '%</b></div>' +

        '<div class="mp-fiber-row"><span>Slow-twitch</span><div class="mp-fiber-bar"><i style="width:' + slow + '%; background:#3498db;"></i></div><b>' + slow + '%</b></div>' +

        '</div><p class="mp-hint">Higher fast-twitch → heavier loads, lower reps, longer rests. Higher slow-twitch → higher reps, shorter rests.</p></div>';

    }

  }

  const html = `

    <div class="mp-modal-head" style="border-left:6px solid ${compMain ? compMain.color : 'var(--primary)'};">

      <div>

        <h2>${mpEsc(ex.name)}</h2>

        <div class="mp-card-badges" style="margin-top:6px;">${mpCompBadges(comps)}</div>

      </div>

      <button class="modal-close" onclick="mpCloseExerciseDetail()">&times;</button>

    </div>

    <div class="mp-modal-body">

      <div class="mp-detail-section">

        <h4><i class="fas fa-info-circle"></i> Overview</h4>

        <div class="mp-meta-row">

          ${muscleTags}

          <span class="mp-meta"><i class="fas fa-tools"></i> ${mpEsc(ex.equipment || '—')}</span>

          <span class="mp-meta"><i class="fas fa-list-ol"></i> ${ex.defaultSets || '—'} sets × ${mpEsc(ex.defaultReps || '—')}</span>

          <span class="mp-meta"><i class="fas fa-signal"></i> Difficulty: ${(ex.skillFactor < 0.5) ? 'Hard' : (ex.skillFactor < 0.7 ? 'Medium' : 'Easy')}</span>

        </div>

      </div>

      <div class="mp-detail-section">

        <h4><i class="fas fa-clipboard-list"></i> How to Perform</h4>

        <ol class="mp-steps">${(ex.instructions || []).map(s => '<li>' + mpEsc(s) + '</li>').join('')}</ol>

      </div>

      ${(ex.cues && ex.cues.length) ? `<div class="mp-detail-section"><h4><i class="fas fa-bullseye"></i> Coaching Cues</h4><ul class="mp-cues">${ex.cues.map(c => '<li>' + mpEsc(c) + '</li>').join('')}</ul></div>` : ''}

      ${(ex.commonMistakes && ex.commonMistakes.length) ? `<div class="mp-detail-section"><h4><i class="fas fa-exclamation-circle"></i> Common Mistakes</h4><ul class="mp-mistakes">${ex.commonMistakes.map(c => '<li>' + mpEsc(c) + '</li>').join('')}</ul></div>` : ''}

      ${fiberHtml}

      <div class="mp-detail-grid">

        <div class="mp-detail-section"><h4><i class="fas fa-wind"></i> Breathing</h4><p>${mpEsc(ex.breathing || 'Exhale on effort, inhale on the return.')}</p></div>

        <div class="mp-detail-section"><h4><i class="fas fa-clock"></i> Tempo</h4><p>${mpEsc(ex.tempo || 'Controlled')}</p></div>

        <div class="mp-detail-section"><h4><i class="fas fa-chart-line"></i> Progression</h4><p>${mpEsc(ex.progression || 'Progressive overload')}</p></div>

        ${record.mu ? `<div class="mp-detail-section"><h4><i class="fas fa-medal"></i> Your Est. 1RM</h4><p><b>${Math.round(record.mu)} lbs</b></p></div>` : ''}

      </div>

      ${compMain ? `<div class="mp-detail-section mp-comp-panel" style="border-left:5px solid ${compMain.color};">

        <h4 style="color:${compMain.color};"><i class="fas ${compMain.icon}"></i> ${mpEsc(compMain.name)} — ${mpEsc(compMain.tagline)}</h4>

        <p>${mpEsc(compMain.desc)}</p>

        <ul class="mp-cues">${compMain.train.slice(0, 3).map(t => '<li>' + mpEsc(t) + '</li>').join('')}</ul>

      </div>` : ''}

    </div>

    <div class="mp-modal-foot">

      <button class="btn btn-success" onclick="mpCloseExerciseDetail(); addExerciseToWorkoutFromLibrary('${exId}', '${mpEsc(ex.name).replace(/'/g, "\\'")}');"><i class="fas fa-plus"></i> Add to Workout</button>

      <button class="btn btn-outline" onclick="mpCloseExerciseDetail(); showExerciseSearch('${mpEsc(ex.name).replace(/'/g, "\\'")}')"><i class="fas fa-search"></i> Web How-To</button>

      <button class="btn" onclick="mpCloseExerciseDetail()"><i class="fas fa-times"></i> Close</button>

    </div>`;

  let holder = document.getElementById('mpDetailModal');

  if (!holder) {

    holder = document.createElement('div');

    holder.id = 'mpDetailModal';

    holder.className = 'modal';

    document.body.appendChild(holder);

  }

  holder.innerHTML = '<div class="modal-content mp-detail-modal">' + html + '</div>';

  holder.style.display = 'block';

}

function mpCloseExerciseDetail() {

  const holder = document.getElementById('mpDetailModal');

  if (holder) holder.style.display = 'none';

}



// ---------- DASHBOARD: component hero, week strip, goal cycle, 5-year ----------

function mpCurrentSplitDay() {

  try {

    const program = (typeof workoutProgram !== 'undefined') ? workoutProgram : null;

    const cw = (typeof currentWorkout !== 'undefined') ? currentWorkout : null;

    if (!program || !program.splits || !cw) return null;

    // the app tags the current workout with the split day id (currentWorkout.type)

    if (cw.type) {

      const split = program.splits.find(s => s.id === cw.type);

      if (split) return { split: split, index: program.splits.indexOf(split) };

    }

    const idx = typeof currentWorkoutSplitIndex === 'number' ? currentWorkoutSplitIndex : null;

    if (idx !== null && program.splits[idx]) return { split: program.splits[idx], index: idx };

    return null;

  } catch (e) { return null; }

}



function mpRenderComponentHero() {

  const el = document.getElementById('mpComponentHero');

  if (!el) return;

  const cur = mpCurrentSplitDay();

  const scores = mpComponentScores();

  // component of the day: from split if available, else rotate by day-of-year

  let comp = null, source = '';

  if (cur && cur.split.component && MP_COMPONENT_MAP[cur.split.component]) {

    comp = MP_COMPONENT_MAP[cur.split.component];

    source = (cur.split.name || 'Today\u2019s Session');

  }

  if (!comp) {

    const doy = Math.floor(Date.now() / 86400000);

    comp = MP_COMPONENTS[doy % MP_COMPONENTS.length];

    source = 'Today\u2019s Focus';

  }

  const weakest = MP_COMPONENTS.slice().sort((a, b) => scores[a.id].score - scores[b.id].score)[0];

  el.innerHTML = `

    <div class="mp-hero" style="background:linear-gradient(135deg, ${comp.color}14, ${comp.color}05); border-left:6px solid ${comp.color};">

      <div class="mp-hero-main">

        <div class="mp-hero-label"><i class="fas ${comp.icon}"></i> ${mpEsc(source)} · COMPONENT OF THE DAY</div>

        <h2 style="color:${comp.color}; margin:4px 0;">${mpEsc(comp.name)} <span class="mp-tagline">— ${mpEsc(comp.tagline)}</span></h2>

        <p class="mp-hero-desc">${mpEsc(comp.desc)}</p>

        <div class="mp-hero-tip"><i class="fas fa-lightbulb"></i> ${mpEsc(comp.train[0])}</div>

      </div>

      <div class="mp-hero-side">

        <div class="mp-hero-score">

          <div class="mp-score-num" style="color:${comp.color};">${scores[comp.id].score}</div>

          <div class="mp-score-lvl">${mpEsc(mpScoreLevel(scores[comp.id].score))}</div>

          <div class="mp-score-sub">${scores[comp.id].sessions28} sessions / 28d</div>

        </div>

        <div class="mp-hero-weak">

          <div class="mp-weak-label">Development priority</div>

          <div>${mpCompBadge(weakest.id)} <b style="color:${weakest.color};">${scores[weakest.id].score}/100</b></div>

          <button class="btn btn-sm btn-outline" style="margin-top:6px;" onclick="mpJumpToComponent('${weakest.id}')">Train it</button>

        </div>

      </div>

    </div>`;

}



function mpJumpToComponent(cid) {

  mpLibraryComponentFilter = cid;

  showSection('library');

  mpRenderLibraryChips();

  if (typeof renderLibrary === 'function') renderLibrary();

}



function mpRenderWeekStrip() {

  const el = document.getElementById('mpWeekStrip');

  if (!el) return;

  const program = (typeof workoutProgram !== 'undefined') ? workoutProgram : null;

  const todayDoy = Math.floor(Date.now() / 86400000);

  let html = '<div class="mp-strip-title"><i class="fas fa-calendar-week"></i> The 11-Component Week</div>';

  html += '<div class="mp-week-strip-track">';

  for (let i = 0; i < 7; i++) {

    const doy = todayDoy - todayDoy % 7 + i;

    const comp = MP_COMPONENTS[doy % MP_COMPONENTS.length];

    const isToday = doy === todayDoy;

    html += '<div class="mp-strip-day' + (isToday ? ' today' : '') + '" style="--chipc:' + comp.color + ';">' +

      '<div class="mp-strip-dow">' + ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][new Date(doy * 86400000).getDay()] + '</div>' +

      '<i class="fas ' + comp.icon + '"></i><div class="mp-strip-name">' + mpEsc(comp.short) + '</div></div>';

  }

  html += '</div><div class="mp-strip-note">Every one of the 11 components gets dedicated training time each week — 7 themed days + warm-ups, cooldowns and endurance woven into every session.</div>';

  el.innerHTML = html;

}



function mpRenderGoalCycleCard() {

  const el = document.getElementById('mpGoalCycleCard');

  if (!el) return;

  const cycle = mpUpdateGoalCycle();

  if (!cycle) { el.innerHTML = ''; return; }

  const now = new Date();

  const daysLeft = Math.max(0, Math.ceil((new Date(cycle.endDate) - now) / 86400000));

  const pct = Math.round((1 - daysLeft / MP_CYCLE_DAYS) * 100);

  let goalsHtml = '';

  cycle.goals.forEach(g => {

    const p = mpProgressPct(g);

    const c = MP_COMPONENT_MAP[g.component];

    const proj = g.projectedDate && g.status !== 'achieved'

      ? '· projects ' + new Date(g.projectedDate).toLocaleDateString() : '';

    goalsHtml += `

      <div class="mp-goal-row" style="--chipc:${c ? c.color : 'var(--primary)'};">

        <div class="mp-goal-title">${mpCompBadge(g.component, true)} ${mpEsc(g.title)} ${mpStatusChip(g.status)}</div>

        <div class="mp-goal-bar"><i style="width:${p}%;"></i></div>

        <div class="mp-goal-meta">

          <span><b>${(g.unit === 'lbs' && g.target > 9999) ? Math.round(g.current).toLocaleString() : g.current}</b> / ${(g.unit === 'lbs' && g.target > 9999) ? Math.round(g.target).toLocaleString() : g.target} ${mpEsc(g.unit)}</span>

          <span>${p}% · ${g.confidence}% confidence ${proj}</span>

        </div>

      </div>`;

  });

  el.innerHTML = `

    <div class="mp-cycle-head">

      <h3><i class="fas fa-bullseye" style="color:var(--primary);"></i> 3-Month Goal Cycle #${cycle.cycleNumber}</h3>

      <div class="mp-cycle-dates">${new Date(cycle.startDate).toLocaleDateString()} → ${new Date(cycle.endDate).toLocaleDateString()} · <b>${daysLeft} days left</b></div>

    </div>

    <div class="mp-cycle-progress"><i style="width:${pct}%;"></i></div>

    ${goalsHtml}

    <div class="mp-cycle-foot">

      <span class="mp-hint"><i class="fas fa-sync"></i> Rotates automatically every 3 months — targets adapt to your achievement rate (current difficulty ×${(cycle.difficultyFactor || 1).toFixed(2)}).</span>

      <button class="btn btn-sm btn-outline" onclick="mpRegenerateGoalCycleUI()"><i class="fas fa-dice"></i> Reroll</button>

    </div>`;

  if (typeof saveWorkoutData === 'function') saveWorkoutData();

}



function mpRegenerateGoalCycleUI() {

  mpRegenerateGoalCycle();

  mpRenderGoalCycleCard();

  if (typeof Swal !== 'undefined') Swal.fire('New Cycle', 'A fresh 3-month goal cycle has been generated from your current numbers.', 'success');

}



function mpRenderFiveYearCard() {

  const el = document.getElementById('mpFiveYearCard');

  if (!el) return;

  const v = mpFiveYearLadder();

  if (!v.hasData) {

    el.innerHTML = '<h3><i class="fas fa-binoculars"></i> 5-Year Vision</h3><p class="mp-hint">Log a few strength sessions and your 5-year strength ladder will project itself here — year by year.</p>';

    return;

  }

  const rows = v.rows.map(r => '<div class="mp-vision-row"><span class="mp-vision-year"><i class="far fa-calendar"></i> ' + r.year + '</span>' +

    '<div class="mp-vision-bar"><i style="width:' + Math.round(r.total / v.rows[4].total * 100) + '%;"></i></div>' +

    '<b>' + r.total.toLocaleString() + ' lbs</b></div>').join('');

  el.innerHTML = `

    <h3><i class="fas fa-binoculars"></i> 5-Year Vision <span class="mp-tagline">— total est. strength ${v.currentTotal.toLocaleString()} → ${v.rows[4].total.toLocaleString()} lbs</span></h3>

    ${rows}

    <div class="mp-hint" style="margin-top:8px;">Conservative compounding (9% → 4% yearly). Paired with your 3-month cycles: five-year vision, quarter-by-quarter execution.</div>`;

}



// ---------- P6E: theme-aware chart helpers + frequency strip ----------

// Canvas cannot resolve CSS var() colors — the old 'var(--gray-*)' options

// silently fell back to dim canvas defaults. These resolve real values.

function p6CssVar(name, fallback) {

  try {

    const v = getComputedStyle(document.documentElement).getPropertyValue(name).trim();

    return v || fallback;

  } catch (e) { return fallback; }

}

function p6HexA(hex, a) {

  try {

    let h = String(hex).replace('#', '');

    if (h.length === 3) h = h.split('').map(c => c + c).join('');

    const n = parseInt(h, 16);

    if (isNaN(n)) return 'rgba(78,155,255,' + a + ')';

    return 'rgba(' + ((n >> 16) & 255) + ',' + ((n >> 8) & 255) + ',' + (n & 255) + ',' + a + ')';

  } catch (e) { return 'rgba(78,155,255,' + a + ')'; }

}

/*P6:E*//* P6E: hero strip for the Workout Frequency card — this week at a glance */

function p6RenderFreqStrip(goal, counts) {

  const holder = document.getElementById('p6FreqStrip');

  if (!holder) return;

  const workouts = (typeof workoutData !== 'undefined' && workoutData && workoutData.workouts) ? workoutData.workouts : [];

  const today = new Date();

  const weekStart = new Date(today);

  weekStart.setDate(today.getDate() - today.getDay()); // Sunday-based week (matches the chart)

  weekStart.setHours(0, 0, 0, 0);

  const days = [];

  for (let d = 0; d < 7; d++) {

    const day = new Date(weekStart);

    day.setDate(weekStart.getDate() + d);

    const next = new Date(day); next.setDate(day.getDate() + 1);

    let hit = false;

    for (let w = 0; w < workouts.length; w++) {

      const t = new Date(workouts[w].date);

      if (t >= day && t < next) { hit = true; break; }

    }

    days.push({ day, hit, isToday: day.toDateString() === today.toDateString() });

  }

  /*P6:E2*//* "This week" counts the in-progress Sunday-start week — same window as the dots */

  let wk = 0;

  const weekEnd = new Date(weekStart); weekEnd.setDate(weekStart.getDate() + 7);

  for (let w2 = 0; w2 < workouts.length; w2++) {

    const t2 = new Date(workouts[w2].date);

    if (t2 >= weekStart && t2 < weekEnd) wk++;

  }

  const last4 = (counts || []).slice(-4);

  const avg4 = last4.length ? Math.round(last4.reduce((s, c) => s + c, 0) / last4.length * 10) / 10 : 0;

  let streak = 0;

  try { streak = calculateStreak(); } catch (e) { streak = 0; }

  const names = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  holder.innerHTML =

    '<div class="p6-freq-stats">' +

      '<div class="p6-fstat p6-hot"><b>' + wk + '<em> / ' + goal + ' goal</em></b><span>This week</span></div>' +

      '<div class="p6-fstat"><b>' + avg4 + '</b><span>4-week avg</span></div>' +

      '<div class="p6-fstat"><b>' + streak + '<em> ' + (streak === 1 ? 'day' : 'days') + '</em></b><span>Current streak</span></div>' +

    '</div>' +

    '<div class="p6-week">' +

      days.map(function (x) {

        return '<div class="p6-day' + (x.hit ? ' hit' : '') + (x.isToday ? ' today' : '') + '">' +

          '<i>' + (x.hit ? '<span class="fas fa-check"></span>' : x.day.getDate()) + '</i>' +

          '<span>' + names[x.day.getDay()] + '</span>' +

        '</div>';

      }).join('') +

    '</div>';

}

// ---------- PROGRESS PAGE: radar, mastery, records ----------

let mpRadarChart = null;

function mpRenderProgressAnalytics() {

  const scores = mpComponentScores();

  /*P6:E*//* one-glance takeaway under the radar title */

  const p6sub = document.getElementById('p6RadarSub');

  if (p6sub && typeof MP_COMPONENTS !== 'undefined' && MP_COMPONENTS.length) {

    const p6sorted = MP_COMPONENTS.slice().sort((a, b) => scores[b.id].score - scores[a.id].score);

    p6sub.textContent = 'Strongest: ' + p6sorted[0].name + ' · Focus next: ' + p6sorted[p6sorted.length - 1].name;

  }

  // radar

  const canvas = document.getElementById('mpComponentRadar');

  if (canvas && typeof Chart !== 'undefined') {

    const data = MP_COMPONENTS.map(c => scores[c.id].score);

    if (mpRadarChart) { mpRadarChart.destroy(); mpRadarChart = null; }

    /*P6:E*//* theme-aware radar — readable in every mode and accent */

    const p6acc = p6CssVar('--p4-accent', '#4E9BFF');

    const p6grid = 'rgba(148,163,184,.22)';

    mpRadarChart = new Chart(canvas.getContext('2d'), {

      type: 'radar',

      data: {

        labels: MP_COMPONENTS.map(c => c.short),

        datasets: [{

          label: 'Your score (0-100)', data: data,

          backgroundColor: p6HexA(p6acc, 0.18), borderColor: p6acc, borderWidth: 2,

          pointBackgroundColor: MP_COMPONENTS.map(c => c.color),

          pointBorderColor: p6CssVar('--p4-surface', '#10141C'), pointBorderWidth: 1.5,

          pointRadius: 4, pointHoverRadius: 6

        }]

      },

      options: {

        responsive: true, maintainAspectRatio: false,

        scales: {

          r: {

            min: 0, max: 100,

            ticks: { stepSize: 20, color: p6CssVar('--p4-text-3', '#94a3b8'), backdropColor: 'transparent', font: { size: 10 } },

            pointLabels: { font: { size: 11, weight: '600' }, color: p6CssVar('--p4-text-2', '#cbd5e1') },

            grid: { color: p6grid }, angleLines: { color: p6grid }

          }

        },

        plugins: { legend: { display: false } }

      }

    });

  }

  // mastery list

  const list = document.getElementById('mpMasteryList');

  if (list) {

    const sorted = MP_COMPONENTS.slice().sort((a, b) => scores[b.id].score - scores[a.id].score);

    list.innerHTML = sorted.map(c => {

      const s = scores[c.id];

      return `<div class="mp-mastery-row" style="--chipc:${c.color};">

        <div class="mp-mastery-head"><i class="fas ${c.icon}"></i> <span>${mpEsc(c.name)}</span>

        <b style="color:${c.color};">${s.score}</b></div>

        <div class="mp-goal-bar"><i style="width:${s.score}%; background:${c.color};"></i></div>

        <div class="mp-mastery-meta"><span>${mpEsc(mpScoreLevel(s.score))}</span><span>${s.sessions28} sessions / 28d</span></div>

      </div>`;

    }).join('');

  }

  // records board

  const board = document.getElementById('mpRecordsBoard');

  if (board) {

    const lifts = mpTopLifts();

    if (!lifts.length) {

      board.innerHTML = '<p class="mp-hint">Complete workouts to build your records board — every logged set updates your estimated 1RMs.</p>';

    } else {

      board.innerHTML = '<div class="mp-records-grid">' + lifts.map(l =>

        '<div class="mp-record-card"><div class="mp-record-name">' + mpEsc(l.name) + '</div>' +

        '<div class="mp-record-value">' + Math.round(l.mu).toLocaleString() + ' <small>lbs est.</small></div>' +

        '<div class="mp-record-sub">' + l.sessions + ' sessions logged</div></div>').join('') + '</div>';

    }

  }

  // goal cycle detail mirror

  const det = document.getElementById('mpGoalCycleDetail');

  if (det) {

    const cycle = workoutData.user && workoutData.user.goalCycle;

    det.innerHTML = cycle ? `<div class="mp-hint">Cycle #${cycle.cycleNumber} · ${new Date(cycle.startDate).toLocaleDateString()} → ${new Date(cycle.endDate).toLocaleDateString()} · difficulty ×${(cycle.difficultyFactor || 1).toFixed(2)} · ${(cycle.achievementRate * 100).toFixed(0)}% of goals achieved so far.</div>`

      : '<div class="mp-hint">Visit the dashboard to generate your first 3-month goal cycle.</div>';

  }

}



// ---------- WORKOUT PAGE: banner + card badges ----------

function mpRenderWorkoutBanner() {

  const sec = document.getElementById('workout-section');

  if (!sec) return;

  let holder = document.getElementById('mpWorkoutBanner');

  if (!holder) {

    holder = document.createElement('div');

    holder.id = 'mpWorkoutBanner';

    const listEl = document.getElementById('exerciseList');

    if (listEl && listEl.parentNode) listEl.parentNode.insertBefore(holder, listEl);

    else sec.insertBefore(holder, sec.firstChild);

  }

  // P4: distribution comes from the exercises themselves, never the split.

  const dist = mpComputeWorkoutComponentDistribution(currentWorkout);

  let comps = [];

  if (dist.length) {

    comps = dist.map(function (d) { return d.id; });

  } else {

    const cur = mpCurrentSplitDay();

    const raw = (cur && cur.split && cur.split.componentsCovered) ? cur.split.componentsCovered : [];

    comps = (typeof P4 !== 'undefined' && P4.normalizeComponents ? P4.normalizeComponents(raw) : raw);

  }

  if (!comps.length) { holder.innerHTML = ''; return; }

  const badges = comps.map(function (cid) {

    const c = MP_COMPONENT_MAP[cid];

    if (!c) return '';

    const hit = dist.find(function (d) { return d.id === cid; });

    const pct = hit ? ' <b style="font-weight:800; opacity:.8;">' + hit.pct + '%</b>' : '';

    return '<span class="mp-comp-badge" style="background:' + c.color + '1a; color:' + c.color + '; border:1px solid ' + c.color + '44;">' +

      '<i class="fas ' + c.icon + '"></i> ' + mpEsc(c.short) + pct + '</span>';

  }).join(' ');

  holder.innerHTML = '<div class="mp-workout-banner">' +

    '<div class="mp-banner-label"><i class="fas fa-layer-group"></i> Components trained in this session</div>' +

    '<div class="mp-banner-badges">' + badges + '</div></div>';

}



function mpEnhanceWorkoutCards() {

  const lookup = mpGetLookup();

  const listEl = document.getElementById('exerciseList');

  if (!listEl) return;

  // P4: footer pulls from the SAME workout componentsCovered as the header

  // (normalized + sliced to 2) so each card's header and footer agree.

  const _curSplit = (typeof mpCurrentSplitDay === 'function') ? mpCurrentSplitDay() : null;

  const _rawWorkoutComps = (_curSplit && _curSplit.split && _curSplit.split.componentsCovered) ? _curSplit.split.componentsCovered : [];

  const _workoutComps = (typeof P4 !== 'undefined' && P4.normalizeComponents ? P4.normalizeComponents(_rawWorkoutComps) : _rawWorkoutComps).slice(0, 2);

  listEl.querySelectorAll('.exercise-item').forEach(card => {

    if (card.dataset.mpDone) return;

    const id = card.dataset.exerciseId;

    const info = id ? lookup[id] : null;

    if (!info) return;

    const titleEl = card.querySelector('.exercise-title') || card.querySelector('h3, h4');

    if (!titleEl || card.querySelector('.mp-comp-badge')) return;

    const div = document.createElement('div');

    div.className = 'mp-card-badges';

    // P4: prefer the workout's componentsCovered (matches the header chips);

    // fall back to the exercise's own components only if the workout has none.

    const footerComps = (typeof P4 !== 'undefined' && P4.normalizeComponents ? P4.normalizeComponents(info.components) : info.components);

    div.innerHTML = mpCompBadges(footerComps, true);

    titleEl.parentNode.insertBefore(div, titleEl.nextSibling);

    card.dataset.mpDone = '1';

  });

}



// ---------- HOOKS ----------

const _mp_origShowSection = showSection;

showSection = function (sectionId, replaceState) {

  const res = _mp_origShowSection(sectionId, replaceState);

  setTimeout(() => {

    try {

      if (sectionId === 'dashboard') {

        mpBuildDashboardBlock();

        mpRenderComponentHero(); mpRenderWeekStrip(); mpRenderGoalCycleCard(); mpRenderFiveYearCard();

      } else if (sectionId === 'library') {

        mpRenderLibraryChips(); mpEnhanceLibraryCards();

      } else if (sectionId === 'progress') {

        mpRenderProgressAnalytics();

      } else if (sectionId === 'workout') {

        mpRenderWorkoutBanner(); mpEnhanceWorkoutCards();

      }

    } catch (e) { console.warn('MP hook', e); }

  }, 60);

  return res;

};



const _mp_origRenderLibrary = renderLibrary;

renderLibrary = function () {

  _mp_origRenderLibrary();

  try { mpEnhanceLibraryCards(); } catch (e) {}

};



if (typeof updateProgressCharts === 'function') {

  const _mp_origUpdProgress = updateProgressCharts;

  updateProgressCharts = function () {

    _mp_origUpdProgress();

    try { mpRenderProgressAnalytics(); } catch (e) {}

  };

}



if (typeof updateTodaysWorkout === 'function') {

  const _mp_origUTW = updateTodaysWorkout;

  updateTodaysWorkout = function () {

    _mp_origUTW();

    try { mpRenderWorkoutBanner(); mpEnhanceWorkoutCards(); } catch (e) {}

  };

}



if (typeof completeWorkout === 'function') {

  const _mp_origComplete = completeWorkout;

  completeWorkout = async function () {

    const res = await _mp_origComplete();

    try { mpUpdateGoalCycle(); if (typeof saveWorkoutData === 'function') saveWorkoutData(); } catch (e) { console.warn(e); }

    return res;

  };

}



function mpBuildDashboardBlock() {

  if (document.getElementById('mpComponentHero')) return;

  const sidebar = document.querySelector('#dashboard-section .sidebar');

  const parent = sidebar ? sidebar.parentNode : null;

  if (!parent) return;

  const block = document.createElement('div');

  block.innerHTML = `

    <div id="mpComponentHero"></div>

    <div class="chart-card hover-lift" id="mpWeekStrip" style="margin-top:15px;"></div>

    <div class="chart-card hover-lift" id="mpGoalCycleCard" style="margin-top:15px;"></div>

    <div class="chart-card hover-lift" id="mpFiveYearCard" style="margin-top:15px;"></div>`;

  parent.insertBefore(block, sidebar);

}



// boot: build lookup after library exists, render the initially-active section

if (typeof ultimateExerciseLibrary !== 'undefined') mpBuildExerciseLookup();

setTimeout(() => {

  try {

    const active = document.querySelector('.section.active');

    const id = active ? active.id.replace('-section', '') : 'dashboard';

    if (id === 'dashboard') {

      mpBuildDashboardBlock();

      mpRenderComponentHero(); mpRenderWeekStrip(); mpRenderGoalCycleCard(); mpRenderFiveYearCard();

    } else if (id === 'library') { mpRenderLibraryChips(); mpEnhanceLibraryCards(); }

    else if (id === 'progress') { mpRenderProgressAnalytics(); }

    else if (id === 'workout') { mpRenderWorkoutBanner(); mpEnhanceWorkoutCards(); }

  } catch (e) { console.warn('[MP] boot render', e); }

}, 250);

console.log('[MP] Masterpiece engine loaded — 11 components active, goal cycles armed, library lookup ready');



// ---- END extracted from index (24).html L47920-49046 (masterpiece engine (11 fitness components, scoring, goal cycles, dashboard block builder, library chips, progress analytics, workout banner, 5-year forecast)) ----

