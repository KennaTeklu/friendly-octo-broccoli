// ============================================================
// EXTRACTED FROM: index (24).html (cautious-enigma repo)
// workout.js — workout-specific late-bound scripts (card collapse, time wizard, 1RM auto-open, continue badge)
// Source line ranges (1-indexed, inclusive):
//   L63530-63744  (P4 Fix #6 — gym-time wizard + coach/physician-approved trimmer (patches performGenerateWorkout))
//   L63774-63904  (P4 Fix #7 — workout page cards: collapsed by default, one open at a time)
//   L64405-64569  (P4 Fix #15 — always-ask time wizard + Continue/Generate confirmation glow)
//   L67762-67867  (p4-autoopen-after-1rm-js (auto-open logger after 1RM test))
//   L67868-67946  (p4-continue-badge-js (continue workout badge))
// Total source lines: 696
// ============================================================

// ---- BEGIN extracted from index (24).html L63530-63744 (P4 Fix #6 — gym-time wizard + coach/physician-approved trimmer (patches performGenerateWorkout)) ----
/* P4 FIX #6 — Gym-time wizard + coach/physician-approved trimmer */

(function () {

  'use strict';

  if (window.__p4Fix6) return;

  window.__p4Fix6 = true;



  var STORE_KEY = 'p4_time_budget';

  var _p4TimeBudget = null;

  try {

    var s = localStorage.getItem(STORE_KEY);

    if (s !== null) { var n = parseInt(s, 10); _p4TimeBudget = (n > 0) ? n : null; }

  } catch (e) {}



  /* ---------- Time estimator (per exercise, minutes) ---------- */

  function estimateMin (ex, restSec) {

    if (!ex) return 3;

    if (ex.isWarmup) return 2;                                  // mobility block

    if (ex.isCooldown) return 2;                                 // one stretch

    if (ex.isComponentDrill || ex.noFatigue) {                  // athletic drill

      var ds = (ex.prescribed && ex.prescribed.sets) || 3;

      return ds * 0.75;

    }

    var sets = (ex.prescribed && ex.prescribed.sets) || 3;

    return (sets * (50 + restSec) + 60) / 60;

  }

  function estimateWorkoutMin (workout, restSec) {

    if (!workout || !workout.exercises) return 0;

    return workout.exercises.reduce(function (s, e) { return s + estimateMin(e, restSec); }, 0);

  }



  /* ---------- Trimmer — coach + physician approved ----------

     Non-negotiables:

       • warm-up always kept (injury prevention)

       • at least 2 main lifts kept (adaptation stimulus)

       • at least 1 cooldown stretch kept (recovery)

     Drop order: drills → accessory isolation → regular mains → extra cooldowns.

     Sets floor: 2. Warm-up untouched. */

  function trimWorkoutToBudget (workout, maxMinutes) {

    if (!workout || !Array.isArray(workout.exercises) || !workout.exercises.length) return 0;

    var restSec = (workoutData && workoutData.user && workoutData.user.settings && workoutData.user.settings.restTime) || 90;



    // Classify with priority flags

    workout.exercises.forEach(function (ex) {

      if (ex.isWarmup) ex._prio = 100;                          // never drop

      else if (ex.isCooldown) ex._prio = 70;                    // drop last, keep >=1

      else if (ex.isComponentDrill) ex._prio = 20;              // drop first

      else if (ex.noFatigue) ex._prio = 15;                     // other athletic

      else if (ex.importance === 'core') ex._prio = 90;         // never drop (compound)

      else if (ex.importance === 'accessory') ex._prio = 30;    // drop early

      else ex._prio = 60;                                       // regular main

    });



    var totalMin = function () { return estimateWorkoutMin(workout, restSec); };

    var mainCount = function () {

      return workout.exercises.filter(function (e) { return !e.isWarmup && !e.isCooldown; }).length;

    };

    var dropLowest = function (maxPrio) {

      var dropIdx = -1, dropPrio = Infinity;

      workout.exercises.forEach(function (ex, i) {

        if (ex._prio <= maxPrio && ex._prio < dropPrio) { dropPrio = ex._prio; dropIdx = i; }

      });

      if (dropIdx === -1) return false;

      workout.exercises.splice(dropIdx, 1);

      return true;

    };



    // Stage 1 — drop athletic drills (never touches warm-up/mains/cooldowns)

    while (totalMin() > maxMinutes && dropLowest(25)) { /* loop */ }



    // Stage 2 — reduce sets (floor 2), largest first, skip warm-up

    while (totalMin() > maxMinutes) {

      var target = null, targetSets = 2;

      workout.exercises.forEach(function (ex) {

        if (ex.isWarmup || ex.isCooldown) return;

        var cnt = (ex.prescribed && ex.prescribed.sets) || 3;

        if (cnt > targetSets) { targetSets = cnt; target = ex; }

      });

      if (!target) break;

      target.prescribed.sets = targetSets - 1;

    }



    // Stage 3 — drop accessories, floor 2 mains

    while (totalMin() > maxMinutes && mainCount() > 2 && dropLowest(40)) { /* loop */ }



    // Stage 4 — drop regular main lifts, floor 2 mains

    while (totalMin() > maxMinutes && mainCount() > 2 && dropLowest(60)) { /* loop */ }



    // Stage 5 — last resort: drop extra cooldowns, floor 1 cooldown

    while (totalMin() > maxMinutes) {

      var coolCount = workout.exercises.filter(function (e) { return e.isCooldown; }).length;

      if (coolCount <= 1) break;

      var idx = -1;

      workout.exercises.forEach(function (ex, i) { if (ex.isCooldown && idx === -1) idx = i; });

      if (idx === -1) break;

      workout.exercises.splice(idx, 1);

    }



    // Cleanup temp markers

    workout.exercises.forEach(function (ex) { delete ex._prio; });

    return workout.exercises.length;

  }



  /* ---------- Wizard UI ---------- */

  function openTimeWizard () {

    return new Promise(function (resolve) {

      if (document.getElementById('p4TimeWizard')) {

        document.getElementById('p4TimeWizard').remove();

      }

      var modal = document.createElement('div');

      modal.className = 'p4-time-wiz-backdrop';

      modal.id = 'p4TimeWizard';

      var preselect = _p4TimeBudget;

      var opts = [

        { v: 15, sub: 'Quick session' },

        { v: 25, sub: 'Short & focused' },

        { v: 40, sub: 'Standard' },

        { v: 60, sub: 'Full session' },

        { v: 90, sub: 'Extended' },

        { v: 0,  sub: 'Full coach plan', unit: 'no limit', num: '\u221E' }

      ];

      var gridHtml = opts.map(function (o) {

        var isActive = (preselect === o.v) || (preselect === null && o.v === 0);

        return '<button data-min="' + o.v + '"' + (isActive ? ' style="border-color:var(--p4-accent,#2563eb);background:var(--p4-accent-soft,#eef);"' : '') + '>' +

          '<span class="p4-twm-num">' + (o.num || o.v) + '</span>' +

          '<span class="p4-twm-unit">' + (o.unit || 'min') + '</span>' +

          '<span class="p4-twm-sub">' + o.sub + '</span>' +

        '</button>';

      }).join('');

      modal.innerHTML =

        '<div class="p4-time-wiz">' +

          '<h3>How long do you have?</h3>' +

          '<p>We\'ll build your plan to fit. Warm-up, main lifts and cooldown are always kept.</p>' +

          '<div class="p4-time-wiz-grid">' + gridHtml + '</div>' +

          '' +

          '<button class="p4-time-wiz-cancel">Cancel</button>' +

        '</div>';

      document.body.appendChild(modal);



      modal.querySelectorAll('.p4-time-wiz-grid button').forEach(function (btn) {

        btn.addEventListener('click', function () {

          var min = parseInt(btn.getAttribute('data-min'), 10);

          try { localStorage.setItem(STORE_KEY, String(min)); } catch (e) {}

          modal.remove();

          resolve({ minutes: min > 0 ? min : null });

        });

      });

      modal.querySelector('.p4-time-wiz-cancel').addEventListener('click', function () {

        modal.remove();

        resolve({ cancel: true });

      });

      modal.addEventListener('click', function (e) {

        if (e.target === modal) { modal.remove(); resolve({ cancel: true }); }

      });

    });

  }



  /* ---------- Hook: startOrGenerateWorkout (fresh generation only) ---------- */

  if (typeof window.startOrGenerateWorkout === 'function' && !window.startOrGenerateWorkout.__p4tw) {

    var origStart = window.startOrGenerateWorkout;

    window.startOrGenerateWorkout = async function () {

      var hasActive = (typeof currentWorkout !== 'undefined') && currentWorkout

        && Array.isArray(currentWorkout.exercises) && currentWorkout.exercises.length > 0;

      if (hasActive) return origStart.apply(this, arguments);   // resuming — no wizard

      var result = await openTimeWizard();

      if (result.cancel) return;

      _p4TimeBudget = result.minutes;

      return origStart.apply(this, arguments);

    };

    window.startOrGenerateWorkout.__p4tw = true;

  }



  /* ---------- Hook: performGenerateWorkout (apply trim after gen) ---------- */

  if (typeof window.performGenerateWorkout === 'function' && !window.performGenerateWorkout.__p4tw) {

    var origPerform = window.performGenerateWorkout;

    window.performGenerateWorkout = function () {

      var r = origPerform.apply(this, arguments);

      if (_p4TimeBudget && currentWorkout && Array.isArray(currentWorkout.exercises)) {

        var beforeCount = currentWorkout.exercises.length;

        var beforeMin = Math.round(estimateWorkoutMin(currentWorkout,

          (workoutData.user.settings && workoutData.user.settings.restTime) || 90));

        var afterCount = trimWorkoutToBudget(currentWorkout, _p4TimeBudget);

        var afterMin = Math.round(estimateWorkoutMin(currentWorkout,

          (workoutData.user.settings && workoutData.user.settings.restTime) || 90));

        try { saveCurrentWorkoutToStorage(); } catch (e) {}

        try { if (typeof updateTodaysWorkout === 'function') updateTodaysWorkout(); } catch (e) {}

        try { if (typeof updateWorkoutHeader === 'function') updateWorkoutHeader(); } catch (e) {}

        if (typeof showNotification === 'function') {

          var msg = afterCount < beforeCount

            ? 'Trimmed to fit ' + _p4TimeBudget + ' min (' + beforeCount + '→' + afterCount + ' exercises, ~' + afterMin + ' min). Warm-up, main lifts, and cooldown kept.'

            : 'Fits your ' + _p4TimeBudget + ' min budget (~' + afterMin + ' min).';

          showNotification(msg, 'info', 'time_trim_' + Date.now());

        }

        console.log('[P4 Fix #6] trim:', beforeCount, '→', afterCount, '| est', beforeMin, '→', afterMin, 'min');

      }

      return r;

    };

    window.performGenerateWorkout.__p4tw = true;

  }



  /* Public API for testing / future UI */

  window.__p4TimeBudget = {

    get: function () { return _p4TimeBudget; },

    set: function (m) {

      _p4TimeBudget = (m && m > 0) ? m : null;

      try { localStorage.setItem(STORE_KEY, String(_p4TimeBudget || 0)); } catch (e) {}

    },

    openWizard: openTimeWizard,

    estimate: estimateWorkoutMin,

    trim: trimWorkoutToBudget

  };



  console.log('[P4 Fix #6] Time wizard armed. Budget:', _p4TimeBudget || 'unlimited');

})();

// ---- END extracted from index (24).html L63530-63744 (P4 Fix #6 — gym-time wizard + coach/physician-approved trimmer (patches performGenerateWorkout)) ----

// ---- BEGIN extracted from index (24).html L63774-63904 (P4 Fix #7 — workout page cards: collapsed by default, one open at a time) ----
/* P4 FIX #7 — workout page cards: collapsed by default, one open at a time */

(function () {

  'use strict';

  if (window.__p4Fix7) return;

  window.__p4Fix7 = true;



  if (!window.P4) window.P4 = {};

  if (!P4.WorkoutCards) P4.WorkoutCards = {};



  /* Final override of wrapCards — always collapses ALL siblings when one opens */

  P4.WorkoutCards.wrapCards = function () {

    var list = document.getElementById('exerciseList');

    if (!list) return;

    var cards = list.querySelectorAll(

      '.exercise-item:not(.p4-wc-wrapped),' +

      '.exercise-card:not(.p4-wc-wrapped),' +

      '.chart-card:not(.p4-wc-wrapped)'

    );

    if (!cards.length) return;



    cards.forEach(function (card) {

      card.classList.add('p4-wc-wrapped');

      card.classList.add('p4-wc-collapsed');   // collapsed by default



      /* Extract index from "exercise_N" or data-index */

      var exIdx = parseInt(card.getAttribute('data-index') || (card.id ? card.id.replace('exercise_', '') : '0'), 10);

      if (isNaN(exIdx)) exIdx = 0;



      /* Name + difficulty */

      var nameEl = card.querySelector('.exercise-title, h3, h4, .exercise-name, [class*="title"], strong, b');

      var name = nameEl ? (nameEl.textContent || '').trim() : 'Exercise';

      if (name.length > 60) name = name.slice(0, 60) + '…';

      var diff = card.querySelector('.badge, .difficulty, [class*="badge"]');

      var diffHTML = diff ? diff.outerHTML : '';



      /* Does this exercise still need a 1RM test? */

      var needs1RM = false;

      try {

        if (typeof currentWorkout !== 'undefined' && currentWorkout

            && Array.isArray(currentWorkout.exercises) && currentWorkout.exercises[exIdx]) {

          var ex = currentWorkout.exercises[exIdx];

          if (ex && ex.id) {

            var rec = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises[ex.id] : null;

            needs1RM = !rec || !rec.tested1RM || rec.tested1RM <= 0;

          }

        }

      } catch (e) {}



      /* Build the always-visible header bar */

      var header = document.createElement('div');

      header.className = 'p4-wc-header';

      header.style.cssText =

        'display:flex;justify-content:space-between;align-items:center;gap:10px;' +

        'padding:10px 12px;cursor:pointer;border-radius:8px;background:var(--gray-100, #f1f5f9);margin-bottom:8px';

      header.innerHTML =

        '<div style="display:flex;align-items:center;gap:8px;flex:1;min-width:0">' +

          '<i class="fas fa-chevron-down p4-wc-chevron" style="font-size:11px;color:var(--p4-text-3,#94a3b8);transform:rotate(-90deg);"></i>' +

          '<span style="font-weight:700;color:var(--primary,#2563eb);font-size:1rem;' +

            'white-space:nowrap;overflow:hidden;text-overflow:ellipsis">' + (name || 'Exercise') + '</span>' +

          (needs1RM ? '<span class="p4-wc-1rm-badge" style="font-size:10px;font-weight:700;color:#f59e0b;' +

            'background:rgba(245,158,11,0.15);padding:2px 6px;border-radius:6px">Needs 1RM</span>' : '') +

        '</div>' +

        '<div style="display:flex;gap:6px;align-items:center">' + diffHTML + '</div>';



      if (card.firstChild) card.insertBefore(header, card.firstChild);

      else card.appendChild(header);



      /* Move all following siblings into a body wrapper */

      var body = document.createElement('div');

      body.className = 'p4-wc-body';

      while (header.nextSibling) body.appendChild(header.nextSibling);

      card.appendChild(body);



      /* Click toggles — collapses ALL siblings, not just 1RM-needing ones */

      header.addEventListener('click', function (e) {

        if (e.target.closest('button, input, select, label, a, textarea')) return;

        var wasCollapsed = card.classList.contains('p4-wc-collapsed');



        list.querySelectorAll('.p4-wc-wrapped').forEach(function (sib) {

          if (sib === card) return;

          sib.classList.add('p4-wc-collapsed');

          var chev = sib.querySelector('.p4-wc-chevron');

          if (chev) chev.style.transform = 'rotate(-90deg)';

        });



        if (wasCollapsed) {

          card.classList.remove('p4-wc-collapsed');

          var chev2 = card.querySelector('.p4-wc-chevron');

          if (chev2) chev2.style.transform = 'rotate(0deg)';

          if (needs1RM) {

            setTimeout(function () {

              var btn = card.querySelector('[onclick*="test"], [onclick*="1RM"], button');

              if (btn) btn.scrollIntoView({ behavior: 'smooth', block: 'center' });

            }, 120);

          }

        } else {

          card.classList.add('p4-wc-collapsed');

          var chev3 = card.querySelector('.p4-wc-chevron');

          if (chev3) chev3.style.transform = 'rotate(-90deg)';

        }

      });

    });

  };



  /* MutationObserver — re-wrap every time the exercise list re-renders */

  function bind () {

    var list = document.getElementById('exerciseList');

    if (!list) { setTimeout(bind, 800); return; }

    if (list._p4wcObs) { try { list._p4wcObs.disconnect(); } catch (e) {} }

    var mo = new MutationObserver(function () {

      if (window.__p4wcTimer) clearTimeout(window.__p4wcTimer);

      window.__p4wcTimer = setTimeout(function () {

        try { P4.WorkoutCards.wrapCards(); } catch (e) {}

      }, 100);

    });

    try { mo.observe(list, { childList: true }); } catch (e) {}

    list._p4wcObs = mo;

    try { P4.WorkoutCards.wrapCards(); } catch (e) {}

  }



  if (document.readyState === 'loading') {

    document.addEventListener('DOMContentLoaded', function () { setTimeout(bind, 800); });

  } else {

    setTimeout(bind, 800);

  }

  setTimeout(bind, 2500);



  console.log('[P4 Fix #7] Workout cards: collapsed by default, one open at a time.');

})();

// ---- END extracted from index (24).html L63774-63904 (P4 Fix #7 — workout page cards: collapsed by default, one open at a time) ----

// ---- BEGIN extracted from index (24).html L64405-64569 (P4 Fix #15 — always-ask time wizard + Continue/Generate confirmation glow) ----
/* P4 FIX #15 — Always-ask time wizard + Continue/Generate confirmation (glow) */

(function () {

  'use strict';

  if (window.__p4Fix15) return;

  window.__p4Fix15 = true;



  var STORE_KEY = 'p4_time_budget';

  var _p4BootDone = false;

  setTimeout(function () { _p4BootDone = true; }, 2500);



  function getBudget () {

    try { var v = localStorage.getItem(STORE_KEY); return v ? parseInt(v, 10) : 0; } catch (e) { return 0; }

  }

  function setBudget (m) {

    try { localStorage.setItem(STORE_KEY, String(m || 0)); } catch (e) {}

    try { if (window.__p4TimeBudget && window.__p4TimeBudget.set) window.__p4TimeBudget.set(m); } catch (e) {}

  }

  function shouldSkipWizard () {

    if (window.__p4SkipWizard) return true;

    if (!_p4BootDone) return true;

    return false;

  }



  function openReplaceConfirm (hasUnsaved) {

    return new Promise(function (resolve) {

      var ex = document.getElementById('p4ReplaceConfirm');

      if (ex) ex.remove();

      var modal = document.createElement('div');

      modal.className = 'p4-replace-backdrop';

      modal.id = 'p4ReplaceConfirm';

      modal.innerHTML =

        '<div class="p4-replace-card">' +

          '<h3>You already have a workout</h3>' +

          '<p>' + (hasUnsaved

            ? 'You have unsaved progress. Continue it to keep your sets, or generate a fresh plan for today.'

            : 'Your logged sets are already saved. Continue this workout, or generate a fresh plan for today.') +

          '</p>' +

          '<div class="p4-replace-actions">' +

            '<button type="button" class="p4-replace-continue" data-choice="continue">' +

              '<i class="fas fa-play"></i><span>Continue Workout</span>' +

            '</button>' +

            '<button type="button" class="p4-replace-generate" data-choice="generate">' +

              '<i class="fas fa-rotate"></i><span>Generate New</span>' +

            '</button>' +

          '</div>' +

        '</div>';

      document.body.appendChild(modal);

      function cleanup (choice) { modal.remove(); resolve(choice); }

      modal.querySelectorAll('[data-choice]').forEach(function (btn) {

        btn.addEventListener('click', function () { cleanup(btn.getAttribute('data-choice')); });

      });

      modal.addEventListener('click', function (e) { if (e.target === modal) cleanup('continue'); });

      document.addEventListener('keydown', function esc (e) {

        if (e.key === 'Escape') { document.removeEventListener('keydown', esc); cleanup('continue'); }

      });

    });

  }



  function openTimeWizard () {

    return new Promise(function (resolve) {

      var ex = document.getElementById('p4TimeWizard');

      if (ex) ex.remove();

      var modal = document.createElement('div');

      modal.className = 'p4-time-wiz-backdrop';

      modal.id = 'p4TimeWizard';

      var preselect = getBudget();

      var opts = [

        { v: 15, sub: 'Quick session' },

        { v: 25, sub: 'Short & focused' },

        { v: 40, sub: 'Standard' },

        { v: 60, sub: 'Full session' },

        { v: 90, sub: 'Extended' },

        { v: 0,  sub: 'Full coach plan', unit: 'no limit', num: '\u221E' }

      ];

      var gridHtml = opts.map(function (o) {

        var isActive = (preselect === o.v);

        return '<button data-min="' + o.v + '"' + (isActive ? ' style="border-color:var(--p4-accent,#2563eb);background:var(--p4-accent-soft,#eef);"' : '') + '>' +

          '<span class="p4-twm-num">' + (o.num || o.v) + '</span>' +

          '<span class="p4-twm-unit">' + (o.unit || 'min') + '</span>' +

          '<span class="p4-twm-sub">' + o.sub + '</span>' +

        '</button>';

      }).join('');

      modal.innerHTML =

        '<div class="p4-time-wiz">' +

          '<h3>How long do you have?</h3>' +

          '<p>We\'ll build your plan to fit. Warm-up, main lifts and cooldown are always kept.</p>' +

          '<div class="p4-time-wiz-grid">' + gridHtml + '</div>' +

          '<button type="button" class="p4-time-wiz-cancel">Cancel</button>' +

        '</div>';

      document.body.appendChild(modal);

      function cleanup (result) { modal.remove(); resolve(result); }

      modal.querySelectorAll('.p4-time-wiz-grid button').forEach(function (btn) {

        btn.addEventListener('click', function () {

          var min = parseInt(btn.getAttribute('data-min'), 10) || 0;

          setBudget(min);

          cleanup({ minutes: min || null });

        });

      });

      modal.querySelector('.p4-time-wiz-cancel').addEventListener('click', function () { cleanup({ cancel: true }); });

      modal.addEventListener('click', function (e) { if (e.target === modal) cleanup({ cancel: true }); });

      document.addEventListener('keydown', function esc (e) {

        if (e.key === 'Escape') { document.removeEventListener('keydown', esc); cleanup({ cancel: true }); }

      });

    });

  }



  async function generateWithWizard () {

    var hasActive = (typeof currentWorkout !== 'undefined') && currentWorkout

      && Array.isArray(currentWorkout.exercises) && currentWorkout.exercises.length > 0;

    if (hasActive) {

      var hasUnsaved = currentWorkout.exercises.some(function (ex) { return !ex.actual && !ex.skipped; });

      var choice = await openReplaceConfirm(hasUnsaved);

      if (choice === 'continue') {

        try { if (typeof showSection === 'function') showSection('workout'); } catch (e) {}

        try { if (typeof requestWakeLock === 'function') requestWakeLock(); } catch (e) {}

        return;

      }

      try { window.currentWorkout = null; } catch (e) {}

      try { if (typeof clearSavedWorkout === 'function') clearSavedWorkout(); } catch (e) {}

    }

    var t = await openTimeWizard();

    if (t.cancel) return;

    try { if (typeof showLoading === 'function') showLoading(true); } catch (e) {}

    try {

      if (typeof window.performGenerateWorkout === 'function') window.performGenerateWorkout();

      if (currentWorkout) {

        try { if (typeof requestWakeLock === 'function') requestWakeLock(); } catch (e) {}

        try { if (typeof showSection === 'function') showSection('workout'); } catch (e) {}

      } else {

        try { if (typeof showNotification === 'function') showNotification('Could not generate workout. Please try again.', 'error'); } catch (e) {}

      }

    } catch (e) {

      console.error('[P4 Fix #15] Generation error:', e);

      try { if (typeof showNotification === 'function') showNotification('Workout generation failed.', 'error'); } catch (e2) {}

    } finally {

      try { if (typeof showLoading === 'function') showLoading(false); } catch (e) {}

    }

  }



  if (typeof window.startOrGenerateWorkout === 'function' && !window.startOrGenerateWorkout.__p4fix15) {

    window.startOrGenerateWorkout = async function () { return generateWithWizard(); };

    window.startOrGenerateWorkout.__p4fix15 = true;

  }

  if (typeof window.generateNextWorkout === 'function' && !window.generateNextWorkout.__p4fix15) {

    var _prevGen = window.generateNextWorkout;

    window.generateNextWorkout = async function () {

      if (shouldSkipWizard()) { return _prevGen.apply(this, arguments); }

      return generateWithWizard();

    };

    window.generateNextWorkout.__p4fix15 = true;

  }

  if (typeof window.completeWorkout === 'function' && !window.completeWorkout.__p4fix15) {

    var _prevComplete = window.completeWorkout;

    window.completeWorkout = async function () {

      window.__p4SkipWizard = true;

      try { return await _prevComplete.apply(this, arguments); }

      finally { window.__p4SkipWizard = false; }

    };

    window.completeWorkout.__p4fix15 = true;

  }



  console.log('[P4 Fix #15] Always-ask wizard + Continue/Generate confirmation armed.');

})();

// ---- END extracted from index (24).html L64405-64569 (P4 Fix #15 — always-ask time wizard + Continue/Generate confirmation glow) ----

// ---- BEGIN extracted from index (24).html L67762-67867 (p4-autoopen-after-1rm-js (auto-open logger after 1RM test)) ----
(function(){

  'use strict';

  if (window.__p4AutoOpenAfter1RM) return;

  window.__p4AutoOpenAfter1RM = true;



  function log(m){ try { console.log('[P4 AutoOpen]', m); } catch(e){} }



  function getTested1RM(exId){

    try {

      var rec = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises[exId] : null;

      return rec && rec.tested1RM && rec.tested1RM > 0 ? rec.tested1RM : null;

    } catch(e){ return null; }

  }



  function findCardIndexByExId(exId){

    try {

      if (typeof currentWorkout === 'undefined' || !currentWorkout || !currentWorkout.exercises) return -1;

      for (var i = 0; i < currentWorkout.exercises.length; i++) {

        if (currentWorkout.exercises[i] && currentWorkout.exercises[i].id === exId) return i;

      }

    } catch(e){}

    return -1;

  }



  function expandAndOpenLogger(index){

    var card = document.getElementById('exercise_' + index);

    if (!card) return;

    card.classList.remove('p4-wc-collapsed');

    var chev = card.querySelector('.p4-wc-chevron');

    if (chev) chev.style.transform = 'rotate(0deg)';

    // Hide other expanded cards

    var list = document.getElementById('exerciseList');

    if (list) {

      list.querySelectorAll('.p4-wc-wrapped').forEach(function(sib){

        if (sib === card) return;

        sib.classList.add('p4-wc-collapsed');

        var c2 = sib.querySelector('.p4-wc-chevron');

        if (c2) c2.style.transform = 'rotate(-90deg)';

      });

    }

    setTimeout(function(){

      if (typeof openLoggerForExercise === 'function') {

        try { openLoggerForExercise(index); } catch(e){ log('openLogger failed: ' + e.message); }

      }

      card.scrollIntoView({ behavior: 'smooth', block: 'center' });

    }, 120);

  }



  function watchFor1RMEntry(){

    try {

      if (typeof workoutData === 'undefined' || !workoutData.exercises) return;

      // Find any exercise in the current workout that now has a tested1RM

      // but whose card is still showing the test panel (not the prescription)

      if (typeof currentWorkout === 'undefined' || !currentWorkout || !currentWorkout.exercises) return;

      for (var i = 0; i < currentWorkout.exercises.length; i++) {

        var ex = currentWorkout.exercises[i];

        if (!ex || !ex.id) continue;

        if (ex.actual || ex.skipped) continue;

        var tested = getTested1RM(ex.id);

        if (!tested) continue;

        var card = document.getElementById('exercise_' + i);

        if (!card) continue;

        // Card still showing test panel? That means we just got a 1RM.

        var testPanel = card.querySelector('.exercise-test-panel');

        if (testPanel) {

          log('1RM detected for ' + ex.id + ' — refreshing card + expanding');

          if (typeof refreshExerciseCard === 'function') {

            try { refreshExerciseCard(i); } catch(e){ log('refresh failed: ' + e.message); }

          }

          setTimeout(function(){ expandAndOpenLogger(i); }, 260);

          return;

        }

      }

    } catch(e){ log('watch error: ' + e.message); }

  }



  // Poll after any change event in the workout section

  function hookChangeEvents(){

    document.addEventListener('change', function(e){

      var t = e.target;

      if (!t) return;

      if (t.id && t.id.indexOf('test_') === 0) {

        setTimeout(watchFor1RMEntry, 400);

      }

    }, true);



    document.addEventListener('click', function(e){

      var t = e.target;

      if (!t || !t.closest) return;

      var btn = t.closest('#test_submit_, [id^="test_submit_"]');

      if (btn) setTimeout(watchFor1RMEntry, 500);

    }, true);

  }



  // Also re-check whenever a card re-renders

  var mo = new MutationObserver(function(){

    if (window.__p4AO1Timer) clearTimeout(window.__p4AO1Timer);

    window.__p4AO1Timer = setTimeout(watchFor1RMEntry, 250);

  });

  try { mo.observe(document.body, { childList: true, subtree: true }); } catch(e){}



  hookChangeEvents();

  log('armed');

})();

// ---- END extracted from index (24).html L67762-67867 (p4-autoopen-after-1rm-js (auto-open logger after 1RM test)) ----

// ---- BEGIN extracted from index (24).html L67868-67946 (p4-continue-badge-js (continue workout badge)) ----
(function(){

  'use strict';

  if (window.__p4ContinueBadge) return;

  window.__p4ContinueBadge = true;



  function log(m){ try { console.log('[P4 ContinueBadge]', m); } catch(e){} }



  function getExerciseStatus(ex){

    if (!ex || !ex.id) return 'pending';

    if (ex.actual && !ex.skipped) return 'done';

    if (ex.skipped) return 'skipped';

    // 1RM test completed?

    var rec = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises[ex.id] : null;

    if (rec && rec.tested1RM && rec.tested1RM > 0) return 'tested';

    // Draft in progress?

    try {

      if (typeof currentWorkout !== 'undefined' && currentWorkout && currentWorkout.id) {

        if (localStorage.getItem('draft_' + currentWorkout.id + '_' + ex.id)) return 'draft';

      }

    } catch(e){}

    return 'pending';

  }



  function annotateCards(){

    var list = document.getElementById('exerciseList');

    if (!list) return;

    if (typeof currentWorkout === 'undefined' || !currentWorkout || !currentWorkout.exercises) return;



    list.querySelectorAll('.p4-wc-wrapped').forEach(function(card, idx){

      var exIdx = parseInt(card.getAttribute('data-index'), 10);

      if (isNaN(exIdx)) exIdx = parseInt((card.id || '').replace('exercise_', ''), 10);

      if (isNaN(exIdx)) exIdx = idx;

      var ex = currentWorkout.exercises[exIdx];

      if (!ex) return;

      var status = getExerciseStatus(ex);

      if (status !== 'tested' && status !== 'draft') return;



      var header = card.querySelector('.p4-wc-header');

      if (!header) return;

      if (header.querySelector('.p4-continue-badge')) return;



      var badge = document.createElement('span');

      badge.className = 'p4-continue-badge';

      badge.style.cssText = 'display:inline-flex;align-items:center;gap:5px;padding:4px 10px;border-radius:20px;font-size:10.5px;font-weight:800;letter-spacing:.02em;background:#fef3c7;color:#92400e;border:1px solid #fbbf24;margin-left:8px;white-space:nowrap;';

      badge.innerHTML = '<i class="fas fa-play-circle"></i>' + (status === 'draft' ? 'Started' : 'Ready');

      // Insert after the exercise title span

      var titleSpan = header.querySelector('span[style*="font-weight"]') || header.querySelector('span');

      if (titleSpan && titleSpan.parentNode) {

        titleSpan.parentNode.insertBefore(badge, titleSpan.nextSibling);

      } else {

        header.appendChild(badge);

      }

    });

  }



  var mo = new MutationObserver(function(){

    if (window.__p4CBTimer) clearTimeout(window.__p4CBTimer);

    window.__p4CBTimer = setTimeout(annotateCards, 100);

  });

  try { mo.observe(document.body, { childList: true, subtree: true }); } catch(e){}



  setTimeout(annotateCards, 800);

  setTimeout(annotateCards, 2000);

  setTimeout(annotateCards, 4000);



  if (typeof window.showSection === 'function' && !window.showSection._p4CB) {

    window.showSection._p4CB = true;

    var orig = window.showSection;

    window.showSection = function(id){

      var r = orig.apply(this, arguments);

      if (id === 'workout') setTimeout(annotateCards, 200);

      return r;

    };

  }



  log('armed');

})();

// ---- END extracted from index (24).html L67868-67946 (p4-continue-badge-js (continue workout badge)) ----

