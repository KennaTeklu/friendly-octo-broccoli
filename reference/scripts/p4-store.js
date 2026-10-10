// ============================================================
// p4-store.js — PLACEHOLDER
// ============================================================
// P4.store and P4.keys are defined INSIDE the P4 core IIFE in
// p4-core.js (extracted from index (24).html L55058-55072 for P4.keys,
// and L55181-55211 for P4.store).
//
// They cannot be extracted to this separate file without breaking
// the IIFE — that would be a rewrite, violating the "pure extraction"
// rule for this batch. Future batches may refactor the P4 layer into
// true modules (ES modules or namespaced imports); for now, all P4
// subsystems live in p4-core.js.
//
// P4.keys is at index (24).html L55061-55069:
//   keys: {
//     theme: 'p4_theme', gym: 'p4_gym', vocab: 'p4_vocab', ...
//   }
//
// P4.store is at index (24).html L55181-55211:
//   var store = P4.store = {};
//   store.get = function (key, fallback) { ... };
//   store.set = function (key, val) { ... };
//   store.del = function (key) { ... };
//   store.snapshot = function () { ... };
// ============================================================
