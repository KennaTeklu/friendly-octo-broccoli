// ============================================================
// EXTRACTED FROM: index (24).html (cautious-enigma repo)
// settings.js — settings-specific late-bound scripts (sheet cleanup, dock, avatar picker, settings search, training env)
// Source line ranges (1-indexed, inclusive):
//   L63905-64011  (P4 Fix #8 — sheet cleanup (one Backup, one Import, no duplicate exports))
//   L64012-64062  (P4 Fix #9 — dock: insert Import button between Backup and Gym Mode)
//   L64063-64259  (P4 Fix #10 — animal-emoji avatar picker (selection only, no typing))
//   L65061-65415  (p4-settings-search-js (settings card search/filter))
//   L67337-67437  (p4-settings-env-js (training environment settings))
//   L67439-67484  (p4-settings-env-persist-js (persist training env settings))
//   L67485-67591  (p4-settings-env-v2-js (training env v2 — gym/home/equipment))
// Total source lines: 964
// ============================================================

// ---- BEGIN extracted from index (24).html L63905-64011 (P4 Fix #8 — sheet cleanup (one Backup, one Import, no duplicate exports)) ----
/* P4 FIX #8 — sheet cleanup: one Backup, one Import. No duplicate exports. */

(function () {

  'use strict';

  if (window.__p4Fix8) return;

  window.__p4Fix8 = true;

  if (!window.P4 || !P4.Nav) { console.warn('[P4 Fix #8] P4.Nav missing'); return; }



  /* ---------- 1. Sheet grid: drop duplicate Export, rename Backup, insert Import ---------- */

  function cleanGrid () {

    var sheet = document.getElementById('p4Sheet');

    if (!sheet) return false;

    if (sheet._p4gCleaned) return true;

    var grid = sheet.querySelector('.p4-sheet-grid');

    if (!grid) return false;

    sheet._p4gCleaned = true;



    // Locate cells by label text

    var cells = Array.prototype.slice.call(grid.querySelectorAll('.p4-sheet-cell'));

    var exportCell = null, backupCell = null;

    cells.forEach(function (cell) {

      var span = cell.querySelector('span');

      if (!span) return;

      var t = (span.textContent || '').trim();

      if (t === 'Export')  exportCell = cell;

      if (t === 'Backup')  backupCell = cell;

    });



    // Remove the duplicate "Export" cell entirely

    if (exportCell && exportCell.parentNode) exportCell.parentNode.removeChild(exportCell);



    // Rename Backup → "Backup JSON" and point at the unified exporter

    if (backupCell) {

      var bspan = backupCell.querySelector('span');

      if (bspan) bspan.textContent = 'Backup JSON';

      backupCell.setAttribute('onclick', 'P4.Backup.exportEverything(); P4.Nav.closeSheet(); return false;');

    }



    // Insert "Import JSON" right after Backup (skip if already there)

    if (backupCell && !grid.querySelector('[data-p4-import]')) {

      var imp = document.createElement('a');

      imp.href = '#';

      imp.className = 'p4-sheet-cell';

      imp.setAttribute('data-p4-import', '1');

      imp.setAttribute('onclick', 'importWorkoutData(); P4.Nav.closeSheet(); return false;');

      imp.innerHTML = '<i class="fas fa-cloud-arrow-up"></i><span>Import JSON</span>';

      backupCell.insertAdjacentElement('afterend', imp);

    }

    console.log('[P4 Fix #8] sheet grid cleaned.');

    return true;

  }



  // Try now, and once more after the DOM settles / P4.Nav.build has run

  setTimeout(cleanGrid, 800);

  setTimeout(cleanGrid, 2000);

  setTimeout(cleanGrid, 4000);



  /* ---------- 2. Quickchip row: rename Export → Backup, append Import ---------- */

  if (P4.Nav.syncQuick && !P4.Nav.syncQuick.__p4fix8) {

    var origSync = P4.Nav.syncQuick;

    P4.Nav.syncQuick = function () {

      var r = origSync.apply(this, arguments);

      try {

        var quick = document.getElementById('p4SheetQuick');

        if (!quick) return r;



        // Rename the "Export JSON" chip → "Backup"; wire to unified exporter

        quick.querySelectorAll('.p4-quickchip').forEach(function (chip) {

          var html = chip.innerHTML || '';

          if (html.indexOf('Export JSON') !== -1) {

            chip.innerHTML = html.replace('Export JSON', 'Backup');

            chip.setAttribute('onclick', 'P4.Backup.exportEverything(); P4.Nav.syncQuick();');

          }

        });



        // Append Import chip once

        if (!quick.querySelector('[data-p4-import-chip]')) {

          var imp = document.createElement('span');

          imp.className = 'p4-quickchip';

          imp.setAttribute('data-p4-import-chip', '1');

          imp.setAttribute('onclick', 'importWorkoutData();');

          imp.innerHTML = '<i class="fas fa-cloud-arrow-up"></i>Import';

          quick.appendChild(imp);

        }

      } catch (e) {}

      return r;

    };

    P4.Nav.syncQuick.__p4fix8 = true;

  }



  /* ---------- 3. Bind a MutationObserver in case the sheet is rebuilt ---------- */

  function bindObserver () {

    var sheet = document.getElementById('p4Sheet');

    if (!sheet) { setTimeout(bindObserver, 800); return; }

    if (sheet._p4gObs) { try { sheet._p4gObs.disconnect(); } catch (e) {} }

    var mo = new MutationObserver(function () {

      if (window.__p4g8Timer) clearTimeout(window.__p4g8Timer);

      window.__p4g8Timer = setTimeout(cleanGrid, 120);

    });

    try { mo.observe(sheet, { childList: true, subtree: true }); } catch (e) {}

    sheet._p4gObs = mo;

  }

  bindObserver();



  console.log('[P4 Fix #8] sheet: one Backup, one Import. Duplicates removed.');

})();

// ---- END extracted from index (24).html L63905-64011 (P4 Fix #8 — sheet cleanup (one Backup, one Import, no duplicate exports)) ----

// ---- BEGIN extracted from index (24).html L64012-64062 (P4 Fix #9 — dock: insert Import button between Backup and Gym Mode) ----
/* P4 FIX #9 — Dock: insert Import button between Backup and Gym Mode */

(function () {

  'use strict';

  if (window.__p4Fix9) return;

  window.__p4Fix9 = true;



  function rebuild () {

    var dock = document.getElementById('p4Dock');

    if (!dock) { setTimeout(rebuild, 800); return; }

    if (dock._p4dRebuilt) return;

    dock._p4dRebuilt = true;



    // Replace entire dock innerHTML — deterministic order, no reliance on

    // parsing existing buttons or their positions.

    dock.innerHTML =

      '<button class="p4-dock-btn" title="Rest timer" aria-label="Rest timer" onclick="P4.Power.restStart()">' +

        '<i class="fas fa-stopwatch"></i>' +

      '</button>' +

      '<button class="p4-dock-btn" title="Backup JSON" aria-label="Backup now" onclick="P4.Backup.exportEverything()">' +

        '<i class="fas fa-cloud-arrow-down"></i>' +

      '</button>' +

      '<button class="p4-dock-btn" title="Import JSON" aria-label="Import backup" onclick="importWorkoutData()">' +

        '<i class="fas fa-cloud-arrow-up"></i>' +

      '</button>' +

      '<button class="p4-dock-btn" title="Gym Mode: giant buttons" aria-label="Gym mode" onclick="P4.Power.toggleGym()">' +

        '<i class="fas fa-hand-fist"></i>' +

      '</button>' +

      '<button class="p4-dock-btn" title="Top of page" aria-label="Scroll to top" onclick="P4.Power.toTop()">' +

        '<i class="fas fa-arrow-up"></i>' +

      '</button>';



    console.log('[P4 Fix #9] Dock rebuilt — Backup + Import + Gym Mode + Top.');

  }



  setTimeout(rebuild, 800);

  setTimeout(rebuild, 2500);



  // Re-run if anything wipes the dock

  var obs = new MutationObserver(function () {

    var dock = document.getElementById('p4Dock');

    if (dock && !dock._p4dRebuilt) {

      if (window.__p4d9Timer) clearTimeout(window.__p4d9Timer);

      window.__p4d9Timer = setTimeout(rebuild, 100);

    }

  });

  try { obs.observe(document.body, { childList: true, subtree: true }); } catch (e) {}



  console.log('[P4 Fix #9] Dock manager armed.');

})();

// ---- END extracted from index (24).html L64012-64062 (P4 Fix #9 — dock: insert Import button between Backup and Gym Mode) ----

// ---- BEGIN extracted from index (24).html L64063-64259 (P4 Fix #10 — animal-emoji avatar picker (selection only, no typing)) ----
/* P4 FIX #10 — animal-emoji avatar picker (selection only, no typing) */

(function () {

  'use strict';

  if (window.__p4Fix10) return;

  window.__p4Fix10 = true;

  if (!window.P4) window.P4 = {};



  var ANIMALS = [

    '\uD83D\uDC36','\uD83D\uDC31','\uD83D\uDC2D','\uD83D\uDC39',

    '\uD83D\uDC30','\uD83E\uDD8A','\uD83D\uDC3B','\uD83D\uDC3C',

    '\uD83D\uDC28','\uD83D\uDC2F','\uD83E\uDD81','\uD83D\uDC2E',

    '\uD83D\uDC37','\uD83D\uDC38','\uD83D\uDC35','\uD83D\uDC14',

    '\uD83D\uDC27','\uD83D\uDC26','\uD83D\uDC24','\uD83E\uDD86',

    '\uD83E\uDD85','\uD83E\uDD89','\uD83E\uDD87','\uD83D\uDC3A',

    '\uD83D\uDC17','\uD83D\uDC34','\uD83E\uDD84','\uD83D\uDC1D',

    '\uD83D\uDC1B','\uD83E\uDD8B','\uD83D\uDC0C','\uD83D\uDC1E',

    '\uD83D\uDC1C','\uD83E\uDD97','\uD83D\uDD77','\uD83E\uDD82',

    '\uD83D\uDC22','\uD83D\uDC0D','\uD83E\uDD8E','\uD83D\uDC19',

    '\uD83E\uDD91','\uD83E\uDD90','\uD83E\uDD80','\uD83D\uDC21',

    '\uD83D\uDC20','\uD83D\uDC1F','\uD83D\uDC2C','\uD83D\uDC33',

    '\uD83D\uDC0B','\uD83E\uDD88','\uD83D\uDC0A','\uD83D\uDC06',

    '\uD83E\uDD93','\uD83E\uDD8D','\uD83D\uDC18','\uD83E\uDD8F',

    '\uD83E\uDD9B','\uD83D\uDC2A','\uD83E\uDD92','\uD83D\uDC03',

    '\uD83D\uDC02','\uD83D\uDC04','\uD83D\uDC0E','\uD83D\uDC10'

  ];



  /* Replace the (broken) emoji generator with a real curated list */

  P4.Emoji = {

    ANIMALS: ANIMALS,

    pick: function () { return ANIMALS[Math.floor(Math.random() * ANIMALS.length)]; }

  };



  /* ---------- helpers ---------- */

  function currentProfile () {

    try { return (P4.Profiles && P4.Profiles.current) ? P4.Profiles.current() : null; } catch (e) { return null; }

  }

  function isRealEmoji (s) {

    if (!s || typeof s !== 'string') return false;

    var cp = s.codePointAt(0);

    return cp && cp >= 0x2600;

  }

  function ensureEmoji () {

    var p = currentProfile();

    if (!p) return;

    if (!isRealEmoji(p.emoji)) {

      var fresh = ANIMALS[Math.floor(Math.random() * ANIMALS.length)];

      try { P4.Profiles.update(p.id, { emoji: fresh }); } catch (e) {}

    }

  }

  function syncAvatarEmoji () {

    try {

      var p = currentProfile();

      var avatar = document.getElementById('navUserAvatar');

      if (!avatar) return;

      var emoji = (p && isRealEmoji(p.emoji)) ? p.emoji : ANIMALS[0];

      avatar.textContent = emoji;

      avatar.style.fontFamily = '"Apple Color Emoji","Segoe UI Emoji","Noto Color Emoji","EmojiOne Color",sans-serif';

      avatar.style.fontSize = '1.35rem';

      avatar.style.lineHeight = '1';

      avatar.style.display = 'flex';

      avatar.style.alignItems = 'center';

      avatar.style.justifyContent = 'center';

    } catch (e) {}

  }



  /* ---------- picker UI ---------- */

  P4.EmojiPicker = {

    open: function () {

      var existing = document.getElementById('p4EmojiBackdrop');

      if (existing) existing.parentNode.removeChild(existing);



      var host = document.createElement('div');

      host.id = 'p4EmojiBackdrop';

      host.style.cssText = 'position:fixed;inset:0;z-index:100004;background:rgba(0,0,0,.55);display:flex;align-items:center;justify-content:center;padding:20px;-webkit-backdrop-filter:blur(4px);backdrop-filter:blur(4px);';

      host.addEventListener('click', function (e) { if (e.target === host) host.parentNode.removeChild(host); });



      var p = currentProfile();

      var cur = (p && isRealEmoji(p.emoji)) ? p.emoji : null;



      var gridHtml = ANIMALS.map(function (a) {

        var isCur = (a === cur);

        return '<button type="button" data-emoji="' + a + '" aria-label="Pick animal" style="' +

          'aspect-ratio:1;display:flex;align-items:center;justify-content:center;' +

          'font-size:22px;line-height:1;padding:0;cursor:pointer;' +

          'border-radius:12px;border:2px solid ' + (isCur ? 'var(--p4-accent,#2563eb)' : 'var(--gray-200,#e2e8f0)') + ';' +

          'background:' + (isCur ? 'var(--p4-accent-soft,#eef)' : 'var(--light,#fff)') + ';' +

          'transition:transform .12s,border-color .12s,background .12s;' +

          'font-family:\'Apple Color Emoji\',\'Segoe UI Emoji\',\'Noto Color Emoji\',sans-serif;">' + a + '</button>';

      }).join('');



      host.innerHTML =

        '<div style="background:var(--light,#fff);border-radius:20px;padding:22px;max-width:520px;width:100%;max-height:88vh;overflow-y:auto;position:relative;box-shadow:0 24px 72px rgba(0,0,0,.4);font-family:inherit;box-sizing:border-box;">' +

          '<button type="button" id="p4EmojiClose" aria-label="Close" style="position:absolute;top:10px;right:12px;background:transparent;border:none;font-size:20px;cursor:pointer;color:#94a3b8;line-height:1;padding:6px;">\u2715</button>' +

          '<h3 style="margin:0 0 4px;font-size:18px;font-weight:800;color:var(--p4-text,#1e293b);letter-spacing:-.2px;">Pick your animal</h3>' +

          '<p style="margin:0 0 16px;font-size:12.5px;color:var(--p4-text-2,#475569);line-height:1.5;">Your avatar. Tap any animal \u2014 only the grid, no typing.</p>' +

          '<div style="display:grid;grid-template-columns:repeat(8,1fr);gap:6px;">' + gridHtml + '</div>' +

        '</div>';



      document.body.appendChild(host);



      host.querySelector('#p4EmojiClose').addEventListener('click', function () { host.parentNode.removeChild(host); });



      host.querySelectorAll('[data-emoji]').forEach(function (btn) {

        btn.addEventListener('mouseenter', function () { btn.style.transform = 'scale(1.12)'; });

        btn.addEventListener('mouseleave', function () { btn.style.transform = ''; });

        btn.addEventListener('click', function () {

          var e = btn.getAttribute('data-emoji');

          P4.EmojiPicker.commit(e);

          host.parentNode.removeChild(host);

        });

      });



      document.addEventListener('keydown', function esc(e) {

        if (e.key === 'Escape') { host.parentNode && host.parentNode.removeChild(host); document.removeEventListener('keydown', esc); }

      });

    },

    commit: function (emoji) {

      try {

        var p = currentProfile();

        if (p) {

          P4.Profiles.update(p.id, { emoji: emoji });

        } else {

          // No profile yet — try creating one silently

          if (P4.Profiles && P4.Profiles.create) {

            var fresh = P4.Profiles.create('You');

            P4.Profiles.update(fresh.id, { emoji: emoji });

          }

        }

        syncAvatarEmoji();

        // Also refresh the name display so its inline emoji matches

        try { if (P4.Picker && P4.Picker.refreshNavbar) P4.Picker.refreshNavbar(); } catch (e) {}

        if (typeof P4.toast === 'function') P4.toast('Avatar set to ' + emoji, 'ok', 1600);

      } catch (err) { console.warn('[P4 Fix #10]', err); }

    }

  };



  /* ---------- wire avatar click → open picker ---------- */

  function wireAvatar () {

    var avatar = document.getElementById('navUserAvatar');

    if (!avatar) { setTimeout(wireAvatar, 800); return; }

    if (avatar._p4EmojiWired) return;

    avatar._p4EmojiWired = true;

    avatar.style.cursor = 'pointer';

    avatar.setAttribute('title', 'Change avatar');

    /* ============ P4-APK-LATER:BEGIN ==============================

       Avatar tap -> Emoji Picker is DISABLED on purpose.

       Reason: avatar tap should fall through to inline HTML

       onclick=showSection('settings') so it opens Settings.

       To re-enable for a future APK build:

         remove the line containing only   slash-star

         remove the line containing only   star-slash

       ============================================================== */

    /*

    // Replace existing inline onclick (showSection('settings'))

    avatar.onclick = function (ev) {

      if (ev) { ev.preventDefault(); ev.stopPropagation(); }

      P4.EmojiPicker.open();

      return false;

    };

    */

    /* ============ P4-APK-LATER:END ================================ */

  }



  /* ---------- keep the emoji in sync whenever the name refreshes ---------- */

  var origUpdate = window.updateHeaderNameAndStreak;

  if (typeof origUpdate === 'function' && !origUpdate._p4EmojiHooked) {

    var hooked = function () {

      var r = origUpdate.apply(this, arguments);

      try { setTimeout(syncAvatarEmoji, 0); } catch (e) {}

      return r;

    };

    hooked._p4EmojiHooked = true;

    window.updateHeaderNameAndStreak = hooked;

  }



  /* ---------- boot ---------- */

  function boot () {

    ensureEmoji();

    wireAvatar();

    syncAvatarEmoji();

  }

  if (document.readyState === 'loading') {

    document.addEventListener('DOMContentLoaded', function () {

      setTimeout(boot, 700);

      setTimeout(syncAvatarEmoji, 2000);

      setTimeout(syncAvatarEmoji, 4000);

    });

  } else {

    setTimeout(boot, 700);

    setTimeout(syncAvatarEmoji, 2000);

    setTimeout(syncAvatarEmoji, 4000);

  }



  console.log('[P4 Fix #10] Emoji avatar picker armed \u2014 ' + ANIMALS.length + ' animals available.');

})();

// ---- END extracted from index (24).html L64063-64259 (P4 Fix #10 — animal-emoji avatar picker (selection only, no typing)) ----

// ---- BEGIN extracted from index (24).html L65061-65415 (p4-settings-search-js (settings card search/filter)) ----
(function(){

  'use strict';

  if (window.__p4SettingsSearch) return;

  window.__p4SettingsSearch = true;



  // Synonym expansion — VS Code style. Key = what the user types,

  // array = phrases that should also match. Add freely, no code changes.

  var SYNONYMS = {

    'theme':        ['appearance', 'color', 'dark', 'light', 'mode', 'accent'],

    'color':        ['appearance', 'theme', 'accent', 'swatch'],

    'dark':         ['appearance', 'theme', 'mode', 'light'],

    'light':        ['appearance', 'theme', 'mode', 'dark'],

    'notification': ['notification', 'reminder', 'alert', 'notify'],

    'reminder':     ['notification', 'retest', 'deload'],

    'backup':       ['data', 'export', 'json', 'snapshot', 'vault'],

    'export':       ['data', 'backup', 'json', 'download'],

    'import':       ['data', 'restore', 'json', 'upload'],

    'reset':        ['data', 'delete', 'erase', 'wipe'],

    'delete':       ['data', 'reset', 'erase', 'wipe'],

    'reading':      ['vocab', 'words', 'level', 'language'],

    'language':     ['lang', 'reading', 'vocab'],

    'profile':      ['personal', 'name', 'birth', 'gender', 'weight'],

    'personal':     ['profile', 'name', 'birth', 'gender', 'weight'],

    'haptic':       ['power', 'vibration', 'buzz'],

    'power':        ['power user', 'haptic', 'rest timer', 'dock', 'swipe'],

    'gym':          ['power', 'gym mode', 'giant'],

    'library':      ['exercise', 'studio', 'movement'],

    'studio':       ['library', 'exercise', 'custom'],

    'plan':         ['subscription', 'pro', 'upgrade', 'paywall'],

    'subscription': ['plan', 'pro', 'billing', 'cancel'],

    'legal':        ['terms', 'privacy', 'waiver', 'cookies'],

    'terms':        ['legal', 'privacy', 'waiver'],

    'privacy':      ['legal', 'terms', 'data']

  };



  // Hidden search terms per group — the VS Code "searchTerms" idea.

  // Key = group title (lowercase). Indexed but never shown in the UI.

  var GROUP_EXTRA = {

    'personal info':               'profile name birthday gender weight body age edit account',

    'appearance':                  'theme color mode dark light accent gym hand fist giant',

    'reading level':               'words vocab vocabulary level simple fancy shakespeare language',

    'language':                    'lang translate locale spanish french german portuguese',

    'power user':                  'haptic buzz vibrate rest timer swipe dock power expert advanced',

    'exercise library management': 'library studio custom add import export exercise movement',

    'plan & subscription':         'pro upgrade paywall billing cancel subscribe trial free',

    'data & backup':               'export import json snapshot vault backup restore reset delete erase',

    'legal':                       'terms privacy waiver cookies accessibility minors dmca security',

    'notifications':               'reminder alert retest deload period notify'

  };



  function esc(s){ return String(s == null ? '' : s).replace(/[&<>"']/g, function(c){

    return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];

  }); }

  function low(s){ return String(s || '').toLowerCase().trim(); }



  function buildIndex(){

    var host = document.getElementById('p4SettingsExtra');

    if (!host) return null;

    var groups = host.querySelectorAll('.p4-settings-group');

    var entries = [];



    groups.forEach(function(group){

      var headerEl = group.querySelector('.p4-settings-group-header');

      var bodyEl   = group.querySelector('.p4-settings-group-body');

      var groupTitle = headerEl ? (headerEl.querySelector('span') || headerEl).textContent.trim() : '';

      var gExtra = GROUP_EXTRA[low(groupTitle)] || '';



      entries.push({

        kind: 'group',

        groupId: group.id,

        groupTitle: groupTitle,

        label: groupTitle,

        context: '',

        extra: gExtra

      });



      if (!bodyEl) return;

      bodyEl.querySelectorAll('label, button, select, input[type="checkbox"], input[type="radio"]').forEach(function(ctrl){

        var top = ctrl;

        while (top && top.parentElement !== bodyEl) top = top.parentElement;

        if (!top) return;



        var txt = '';

        if (ctrl.tagName === 'LABEL') {

          txt = ctrl.textContent.replace(/\s+/g,' ').trim();

        } else if (ctrl.tagName === 'BUTTON') {

          txt = ctrl.textContent.replace(/\s+/g,' ').trim();

        } else if (ctrl.tagName === 'SELECT') {

          var lbl = ctrl.parentElement && ctrl.parentElement.querySelector('label');

          txt = lbl ? lbl.textContent.replace(/\s+/g,' ').trim() : '';

        } else if (ctrl.type === 'checkbox' || ctrl.type === 'radio') {

          var w = ctrl.closest('label');

          txt = w ? w.textContent.replace(/\s+/g,' ').trim() : '';

        }

        if (!txt || txt.length < 2) return;

        // strip the control's own value display (checkbox label repeats itself)

        txt = txt.replace(/\s+/g,' ').trim();

        if (txt.length > 100) txt = txt.slice(0, 100) + '…';



        entries.push({

          kind: 'control',

          groupId: group.id,

          groupTitle: groupTitle,

          label: txt,

          context: groupTitle,

          extra: '',

          el: top

        });

      });

    });



    return entries;

  }



  function makeFuse(entries){

    if (typeof Fuse === 'undefined') return null;

    return new Fuse(entries, {

      keys: [

        { name: 'label',   weight: 0.65 },

        { name: 'context', weight: 0.20 },

        { name: 'extra',   weight: 0.15 }

      ],

      threshold: 0.4,

      ignoreLocation: true,

      minMatchCharLength: 2,

      includeScore: true

    });

  }



  function highlight(label, q){

    var safe = esc(label);

    if (!q || q.length < 2) return safe;

    var re = new RegExp('(' + q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'ig');

    return safe.replace(re, '<mark>$1</mark>');

  }



  var state = { fuse: null, entries: [], query: '' };



  function mount(){

    var host = document.getElementById('p4SettingsExtra');

    if (!host) return false;

    if (host.querySelector('.p4-ss-wrap')) return true;



    var wrap = document.createElement('div');

    wrap.className = 'p4-ss-wrap';

    wrap.innerHTML =

      '<i class="fas fa-magnifying-glass p4-ss-icon"></i>' +

      '<input type="search" class="p4-ss-input" id="p4SsInput" placeholder="Search settings…" autocomplete="off" translate="no">' +

      '<kbd class="p4-ss-hint">/</kbd>' +

      '<button type="button" class="p4-ss-clear" id="p4SsClear" aria-label="Clear"><i class="fas fa-xmark"></i></button>';



    var results = document.createElement('div');

    results.className = 'p4-ss-results';

    results.id = 'p4SsResults';



    var empty = document.createElement('div');

    empty.className = 'p4-ss-empty';

    empty.id = 'p4SsEmpty';

    empty.innerHTML = '<i class="fas fa-magnifying-glass"></i>No settings match "<b></b>"';



    host.insertBefore(wrap, host.firstChild);

    host.insertBefore(results, wrap.nextSibling);

    host.insertBefore(empty, results.nextSibling);



    var input = document.getElementById('p4SsInput');

    var clearBtn = document.getElementById('p4SsClear');

    var resultsEl = document.getElementById('p4SsResults');

    var emptyEl = document.getElementById('p4SsEmpty');



    state.entries = buildIndex() || [];

    state.fuse = makeFuse(state.entries);



    function reset(){

      host.classList.remove('p4-ss-active');

      resultsEl.classList.remove('on');

      resultsEl.innerHTML = '';

      emptyEl.classList.remove('on');

      host.querySelectorAll('.p4-settings-group').forEach(function(g){

        g.classList.remove('p4-ss-hit');

        g.querySelectorAll('.p4-ss-ctrl-hit').forEach(function(c){ c.classList.remove('p4-ss-ctrl-hit'); });

      });

    }



    function applyFilter(q){

      state.query = q.trim();

      wrap.classList.toggle('has-value', state.query.length > 0);



      if (state.query.length < 2) { reset(); return; }



      var hits;

      if (state.fuse) {

        hits = state.fuse.search(state.query).slice(0, 12);

      } else {

        var ql = low(state.query);

        hits = state.entries

          .filter(function(e){ return (e.label + ' ' + e.context + ' ' + (e.extra||'')).toLowerCase().indexOf(ql) >= 0; })

          .slice(0, 12)

          .map(function(e){ return { item: e, score: 0 }; });

      }



      // Synonym expansion

      var ql = low(state.query);

      var expanded = (SYNONYMS[ql] || []).map(low);

      if (expanded.length) {

        state.entries.forEach(function(e){

          var hay = (e.label + ' ' + e.context + ' ' + (e.extra||'')).toLowerCase();

          var ok = expanded.some(function(t){ return hay.indexOf(t) !== -1; });

          if (!ok) return;

          if (hits.some(function(h){ return h.item === e; })) return;

          hits.push({ item: e, score: 0.5 });

        });

      }



      // Dedupe

      var seen = new Set();

      var clean = [];

      hits.forEach(function(h){

        var k = h.item.kind + '|' + h.item.groupId + '|' + h.item.label;

        if (seen.has(k)) return;

        seen.add(k);

        clean.push(h);

      });



      var hitGroups = new Set();

      clean.forEach(function(h){ if (h.item.groupId) hitGroups.add(h.item.groupId); });

      host.classList.add('p4-ss-active');

      host.querySelectorAll('.p4-settings-group').forEach(function(g){

        g.classList.toggle('p4-ss-hit', hitGroups.has(g.id));

      });



      if (clean.length === 0) {

        resultsEl.classList.remove('on');

        resultsEl.innerHTML = '';

        emptyEl.classList.add('on');

        var b = emptyEl.querySelector('b'); if (b) b.textContent = state.query;

        return;

      }

      emptyEl.classList.remove('on');

      resultsEl.classList.add('on');

      resultsEl.innerHTML = clean.map(function(h, i){

        var e = h.item;

        var icon = e.kind === 'group' ? 'fa-layer-group' : 'fa-sliders';

        return '<button type="button" class="p4-ss-res" data-idx="' + i + '">' +

          '<i class="fas ' + icon + ' p4-ss-res-icon"></i>' +

          '<div class="p4-ss-res-body">' +

            '<div class="p4-ss-res-group">' + esc(e.groupTitle || 'Settings') + '</div>' +

            '<div class="p4-ss-res-label">' + highlight(e.label, state.query) + '</div>' +

          '</div>' +

        '</button>';

      }).join('');



      resultsEl.querySelectorAll('.p4-ss-res').forEach(function(btn){

        btn.addEventListener('click', function(){

          var idx = parseInt(btn.getAttribute('data-idx'), 10);

          var hit = clean[idx];

          if (hit) jumpTo(hit.item);

        });

      });

    }



    function jumpTo(entry){

      var group = document.getElementById(entry.groupId);

      if (!group) return;

      var body = group.querySelector('.p4-settings-group-body');

      var header = group.querySelector('.p4-settings-group-header');

      if (body && body.style.display === 'none' && header) {

        header.click();

      }

      input.value = '';

      applyFilter('');

      setTimeout(function(){

        var target = entry.el || group;

        if (target && target.scrollIntoView) {

          target.scrollIntoView({ behavior: 'smooth', block: 'center' });

        }

        setTimeout(function(){

          if (target && target.classList) {

            target.classList.add('p4-ss-flash');

            setTimeout(function(){ target.classList.remove('p4-ss-flash'); }, 1200);

          }

        }, 350);

      }, 60);

    }



    input.addEventListener('input', function(){ applyFilter(input.value); });

    input.addEventListener('keydown', function(e){

      if (e.key === 'Escape') { input.value = ''; applyFilter(''); input.blur(); return; }

      if (e.key === 'Enter') {

        var first = resultsEl.querySelector('.p4-ss-res');

        if (first) first.click();

      }

    });

    clearBtn.addEventListener('click', function(){

      input.value = ''; applyFilter(''); input.focus();

    });



    if (!document._p4SsSlash) {

      document._p4SsSlash = true;

      document.addEventListener('keydown', function(e){

        if (e.key !== '/' || e.ctrlKey || e.metaKey || e.altKey) return;

        var tag = (e.target && e.target.tagName) || '';

        if (tag === 'INPUT' || tag === 'TEXTAREA' || (e.target && e.target.isContentEditable)) return;

        var sec = document.getElementById('settings-section');

        if (!sec || !sec.classList.contains('active')) return;

        e.preventDefault();

        var inp = document.getElementById('p4SsInput');

        if (inp) inp.focus();

      });

    }



    return true;

  }



  function installHook(){

    if (!window.P4 || !P4.SettingsUI) { setTimeout(installHook, 200); return; }

    if (P4.SettingsUI._ssHooked) return;

    P4.SettingsUI._ssHooked = true;

    var orig = P4.SettingsUI.inject;

    if (typeof orig !== 'function') return;

    P4.SettingsUI.inject = function(){

      var r = orig.apply(this, arguments);

      setTimeout(mount, 60);

      setTimeout(mount, 400);

      return r;

    };

  }



  function boot(){

    installHook();

    if (typeof window.showSection === 'function' && !window.showSection._ssHooked) {

      window.showSection._ssHooked = true;

      var origShow = window.showSection;

      window.showSection = function(id){

        var r = origShow.apply(this, arguments);

        if (id === 'settings') {

          setTimeout(mount, 120);

          setTimeout(mount, 500);

        }

        return r;

      };

    }

    setTimeout(mount, 1000);

    setTimeout(mount, 2500);

  }



  if (document.readyState === 'loading') {

    document.addEventListener('DOMContentLoaded', function(){ setTimeout(boot, 500); });

  } else {

    setTimeout(boot, 500);

  }



  console.log('[P4 Settings Search] mounted — Fuse fuzzy + synonym expansion + jump-to-control');

})();

// ---- END extracted from index (24).html L65061-65415 (p4-settings-search-js (settings card search/filter)) ----

// ---- BEGIN extracted from index (24).html L67337-67437 (p4-settings-env-js (training environment settings)) ----
(function(){

  'use strict';

  if (window.__p4SettingsEnv) return;

  window.__p4SettingsEnv = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 Settings Env]', m); } catch(e){} }



  function inject(){

    var dataCard = document.getElementById('p4DataCard');

    if (!dataCard) return false;

    if (document.getElementById('p4TrainingEnvCard')) return true;



    var cur = (P4.Bodyweight && P4.Bodyweight.getMode) ? P4.Bodyweight.getMode() : 'gym';



    function opt(m, icon, title, sub){

      var on = cur === m ? ' style="border-color:var(--p4-accent,#2563eb);background:var(--p4-accent-soft,#eef);"' : '';

      return '<button type="button" class="p4-env-opt" data-mode="' + m + '"' + on + ' style="flex:1;min-width:110px;padding:14px 12px;border-radius:12px;border:2px solid var(--gray-200,#e2e8f0);background:var(--light,#fff);cursor:pointer;text-align:center;font-family:inherit;transition:.15s;">' +

        '<div style="font-size:22px;margin-bottom:4px;"><i class="fas ' + icon + '" style="color:var(--p4-accent,#2563eb)"></i></div>' +

        '<div style="font-weight:700;font-size:13px;color:var(--p4-text,#1e293b);">' + title + '</div>' +

        '<div style="font-size:11px;color:var(--p4-text-3,#94a3b8);margin-top:2px;">' + sub + '</div>' +

      '</button>';

    }



    var card = document.createElement('div');

    card.id = 'p4TrainingEnvCard';

    card.className = 'card p4-settings-group';

    card.style.cssText = 'margin-top:14px;padding:0;overflow:hidden;border:1px solid var(--gray-200,#e2e8f0);border-radius:14px';

    card.innerHTML =

      '<div class="p4-settings-group-header" style="display:flex;align-items:center;gap:10px;padding:14px 18px;cursor:pointer;user-select:none;background:var(--gray-100,#f1f5f9);">' +

        '<i class="fas fa-house-chimney" style="color:var(--p4-accent,#2563eb);width:18px;text-align:center"></i>' +

        '<span style="flex:1;font-weight:600;font-size:15px;color:var(--p4-text,#1e293b);">Training environment</span>' +

        '<i class="fas fa-chevron-down p4-env-chevron" style="font-size:11px;color:var(--p4-text-3,#94a3b8);transition:transform .2s;transform:rotate(-90deg);"></i>' +

      '</div>' +

      '<div class="p4-settings-group-body" style="padding:16px 18px;display:none">' +

        '<div style="display:flex;gap:8px;flex-wrap:wrap;">' +

          opt('gym',        'fa-dumbbell',       'Gym',  'Full equipment') +

          opt('bodyweight', 'fa-house-chimney',  'Home', 'No equipment')   +

          opt('mixed',      'fa-shuffle',        'Mix',  'Both libraries') +

        '</div>' +

        '<div style="font-size:11.5px;color:var(--p4-text-3,#94a3b8);margin-top:10px;">Controls which exercise library the generator draws from. Switch any time.</div>' +

      '</div>';



    dataCard.parentNode.insertBefore(card, dataCard.nextSibling);



    var header = card.querySelector('.p4-settings-group-header');

    var body   = card.querySelector('.p4-settings-group-body');

    var chev   = card.querySelector('.p4-env-chevron');



    header.addEventListener('click', function(e){

      if (e.target.closest('button')) return;

      var isOpen = body.style.display !== 'none';

      body.style.display = isOpen ? 'none' : 'block';

      chev.style.transform = isOpen ? 'rotate(-90deg)' : 'rotate(0deg)';

    });



    card.querySelectorAll('.p4-env-opt').forEach(function(b){

      b.addEventListener('click', function(){

        var m = b.getAttribute('data-mode');

        if (P4.Bodyweight && P4.Bodyweight.setMode) P4.Bodyweight.setMode(m);

        card.querySelectorAll('.p4-env-opt').forEach(function(x){

          x.style.borderColor = 'var(--gray-200,#e2e8f0)';

          x.style.background = 'var(--light,#fff)';

        });

        b.style.borderColor = 'var(--p4-accent,#2563eb)';

        b.style.background = 'var(--p4-accent-soft,#eef)';

        try { if (typeof showNotification === 'function') showNotification('Training environment: ' + m, 'info', 'p4_env_' + Date.now()); } catch(e){}

      });

    });



    log('training-environment card injected');

    return true;

  }



  function boot(){

    inject();

    setTimeout(inject, 800);

    setTimeout(inject, 2000);

  }



  if (document.readyState === 'loading') {

    document.addEventListener('DOMContentLoaded', function(){ setTimeout(boot, 900); });

  }

  if (document.readyState !== 'loading') {

    setTimeout(boot, 900);

  }



  if (typeof window.showSection === 'function' && !window.showSection._p4SE) {

    window.showSection._p4SE = true;

    var orig = window.showSection;

    window.showSection = function(id){

      var r = orig.apply(this, arguments);

      if (id === 'settings') setTimeout(inject, 200);

      return r;

    };

  }



  log('armed');

})();

// ---- END extracted from index (24).html L67337-67437 (p4-settings-env-js (training environment settings)) ----

// ---- BEGIN extracted from index (24).html L67439-67484 (p4-settings-env-persist-js (persist training env settings)) ----
(function(){

  'use strict';

  if (window.__p4SettingsEnvPersist) return;

  window.__p4SettingsEnvPersist = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 Settings Env Persist]', m); } catch(e){} }



  function ensureCard(){

    var dataCard = document.getElementById('p4DataCard');

    if (!dataCard) return;

    if (document.getElementById('p4TrainingEnvCard')) return;

    if (window.P4 && P4.SettingsEnv && typeof P4.SettingsEnv.inject === 'function') {

      try { P4.SettingsEnv.inject(); } catch(e){ log('inject failed: ' + e.message); }

    }

  }



  function hookInject(){

    if (!window.P4 || !P4.SettingsUI) { setTimeout(hookInject, 300); return; }

    if (P4.SettingsUI._envHooked) return;

    P4.SettingsUI._envHooked = true;

    var orig = P4.SettingsUI.inject;

    if (typeof orig !== 'function') return;

    P4.SettingsUI.inject = function(){

      var r = orig.apply(this, arguments);

      setTimeout(ensureCard, 40);

      setTimeout(ensureCard, 300);

      return r;

    };

    log('SettingsUI.inject hooked');

  }

  hookInject();



  var mo = new MutationObserver(function(){

    if (window.__p4EnvTimer) clearTimeout(window.__p4EnvTimer);

    window.__p4EnvTimer = setTimeout(ensureCard, 60);

  });

  try { mo.observe(document.body, { childList: true, subtree: true }); } catch(e){}



  setTimeout(ensureCard, 1500);

  setTimeout(ensureCard, 3500);



  log('armed');

})();

// ---- END extracted from index (24).html L67439-67484 (p4-settings-env-persist-js (persist training env settings)) ----

// ---- BEGIN extracted from index (24).html L67485-67591 (p4-settings-env-v2-js (training env v2 — gym/home/equipment)) ----
(function(){

  'use strict';

  if (window.__p4SettingsEnvV2) return;

  window.__p4SettingsEnvV2 = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 Env v2]', m); } catch(e){} }



  function inject(){

    var dataCard = document.getElementById('p4DataCard');

    if (!dataCard) return false;

    if (document.getElementById('p4TrainingEnvCard')) return true;



    var cur = 'gym';

    try { if (P4.Bodyweight && P4.Bodyweight.getMode) cur = P4.Bodyweight.getMode(); } catch(e){}



    function opt(m, icon, title, sub){

      var on = cur === m ? ' style="border-color:var(--p4-accent,#2563eb);background:var(--p4-accent-soft,#eef);"' : '';

      return '<button type="button" class="p4-env-opt" data-mode="' + m + '"' + on + ' style="flex:1;min-width:110px;padding:14px 12px;border-radius:12px;border:2px solid var(--gray-200,#e2e8f0);background:var(--light,#fff);cursor:pointer;text-align:center;font-family:inherit;transition:.15s;">' +

        '<div style="font-size:22px;margin-bottom:4px;"><i class="fas ' + icon + '" style="color:var(--p4-accent,#2563eb)"></i></div>' +

        '<div style="font-weight:700;font-size:13px;color:var(--p4-text,#1e293b);">' + title + '</div>' +

        '<div style="font-size:11px;color:var(--p4-text-3,#94a3b8);margin-top:2px;">' + sub + '</div>' +

      '</button>';

    }



    var card = document.createElement('div');

    card.id = 'p4TrainingEnvCard';

    card.className = 'card p4-settings-group';

    card.style.cssText = 'margin-top:14px;padding:0;overflow:hidden;border:1px solid var(--gray-200,#e2e8f0);border-radius:14px';

    card.innerHTML =

      '<div class="p4-settings-group-header" style="display:flex;align-items:center;gap:10px;padding:14px 18px;cursor:pointer;user-select:none;background:var(--gray-100,#f1f5f9);">' +

        '<i class="fas fa-house-chimney" style="color:var(--p4-accent,#2563eb);width:18px;text-align:center"></i>' +

        '<span style="flex:1;font-weight:600;font-size:15px;color:var(--p4-text,#1e293b);">Training environment</span>' +

        '<i class="fas fa-chevron-down p4-env-chevron" style="font-size:11px;color:var(--p4-text-3,#94a3b8);transition:transform .2s;transform:rotate(-90deg);"></i>' +

      '</div>' +

      '<div class="p4-settings-group-body" style="padding:16px 18px;display:none">' +

        '<div style="display:flex;gap:8px;flex-wrap:wrap;">' +

          opt('gym',        'fa-dumbbell',       'Gym',  'Full equipment') +

          opt('bodyweight', 'fa-house-chimney',  'Home', 'No equipment')   +

          opt('mixed',      'fa-shuffle',        'Mix',  'Both libraries') +

        '</div>' +

        '<div style="font-size:11.5px;color:var(--p4-text-3,#94a3b8);margin-top:10px;">Controls which exercise library the generator draws from. Switch any time.</div>' +

      '</div>';



    dataCard.parentNode.insertBefore(card, dataCard.nextSibling);



    var header = card.querySelector('.p4-settings-group-header');

    var body   = card.querySelector('.p4-settings-group-body');

    var chev   = card.querySelector('.p4-env-chevron');



    header.addEventListener('click', function(e){

      if (e.target.closest('button')) return;

      var isOpen = body.style.display !== 'none';

      body.style.display = isOpen ? 'none' : 'block';

      chev.style.transform = isOpen ? 'rotate(-90deg)' : 'rotate(0deg)';

    });



    card.querySelectorAll('.p4-env-opt').forEach(function(b){

      b.addEventListener('click', function(){

        var m = b.getAttribute('data-mode');

        if (P4.Bodyweight && P4.Bodyweight.setMode) P4.Bodyweight.setMode(m);

        card.querySelectorAll('.p4-env-opt').forEach(function(x){

          x.style.borderColor = 'var(--gray-200,#e2e8f0)';

          x.style.background = 'var(--light,#fff)';

        });

        b.style.borderColor = 'var(--p4-accent,#2563eb)';

        b.style.background = 'var(--p4-accent-soft,#eef)';

        try { if (typeof showNotification === 'function') showNotification('Training environment: ' + m, 'info', 'p4_env_' + Date.now()); } catch(e){}

      });

    });



    log('card injected');

    return true;

  }



  P4.SettingsEnv = { inject: inject };



  function hookInject(){

    if (!window.P4 || !P4.SettingsUI) { setTimeout(hookInject, 300); return; }

    if (P4.SettingsUI._envV2Hooked) return;

    P4.SettingsUI._envV2Hooked = true;

    var orig = P4.SettingsUI.inject;

    if (typeof orig !== 'function') return;

    P4.SettingsUI.inject = function(){

      var r = orig.apply(this, arguments);

      setTimeout(inject, 40);

      setTimeout(inject, 300);

      setTimeout(inject, 900);

      return r;

    };

    log('SettingsUI.inject hooked');

  }

  hookInject();



  var mo = new MutationObserver(function(){

    if (window.__p4EnvV2Timer) clearTimeout(window.__p4EnvV2Timer);

    window.__p4EnvV2Timer = setTimeout(inject, 60);

  });

  try { mo.observe(document.body, { childList: true, subtree: true }); } catch(e){}



  setTimeout(inject, 1500);

  setTimeout(inject, 3500);



  log('armed');

})();

// ---- END extracted from index (24).html L67485-67591 (p4-settings-env-v2-js (training env v2 — gym/home/equipment)) ----

