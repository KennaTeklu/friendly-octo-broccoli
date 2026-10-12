# 06 — Library (P4 Library Studio + streaming renderer in 15-inline)

> Reference module: `reference/scripts/p4/06-library.js` (914 lines).
> Source line range in p4-core.js: L17259-18148 (P4 LIBRARY STUDIO IIFE).
> Note: the library streaming renderer (`renderLibraryStreaming`, replaces all-at-once `renderLibrary`) and `P4.WorkoutCards` (collapsible exercise cards) live in `15-inline.js` (L22647-L23346, P4 STAGE-4 MODULES).
> Native counterparts: `engine/Library.kt`, `core/Studio.kt`, `ui/screens/Library.kt`, `ui/screens/Studio.kt`.

## A. File purpose
Owns the **P4 LIBRARY STUDIO IIFE** — the user-facing CRUD UI for managing custom exercises: add manually, export, import+merge, AI-prompt wizard, exercise validation, muscle/group normalization, custom-exercise persistence.

## B. Public surface
- `P4.Studio.init()` — wires the Studio open button + modal lifecycle.
- `P4.Studio.open()` — opens the studio modal.
- `P4.Studio.addManual(exercise)` — validates + saves a manually-entered exercise.
- `P4.Studio.exportLibrary()` — exports the custom library to JSON.
- `P4.Studio.importLibrary(json, { merge })` — imports + optionally merges.
- `P4.Studio.aiWizard(prompt)` — generates an exercise from an AI prompt.
- `P4.Studio.validateExercise(ex, registry)` — delegates to `P4.SEC.validateExercisePayload`.

## C. Dependencies
- `window.P4`, `P4.SEC` (from `01-core.js`).
- `P4.toast`, `P4.download`, `P4.uid`.
- `workoutData.exercises` (custom exercises registry).
- `P4_EXERCISE_COMPONENTS` (component whitelist, from `01-core.js`).
- Swal2 (modal host), Fuse.js (fuzzy search).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `P4.Studio.open` | `Studio.open()` (Compose dialog) |
| `P4.Studio.addManual` | `Studio.addCustomExercise(ex)` |
| `P4.Studio.exportLibrary` | `Exporter.exportCustomLibrary()` |
| `P4.Studio.importLibrary` | `Exporter.importCustomLibrary(uri, merge)` |
| `P4.Studio.aiWizard` | `Studio.aiWizard(prompt)` (Gemini API) |
| `P4.SEC.validateExercisePayload` | `Studio.validateExercise()` |

## E. Patterns worth porting
- **Import + merge mode**: when `merge: true`, the importer appends new exercises and updates existing ones by `id`, rather than overwriting the whole library. User keeps their edits.
- **AI-prompt wizard**: free-text prompt → structured exercise object. Validates the AI output against `P4_EXERCISE_COMPONENTS` before saving.
- **Healed JSON import**: `P4.SEC.healJson` repairs trailing commas, stray markdown fences, smart-quotes before parsing.

## E2. Deep patterns
- **Component whitelist**: `P4_EXERCISE_COMPONENTS` (defined in `01-core.js`) is the single source of truth for which muscles/groups/equipments are valid. The Studio UI dropdowns are populated from this registry — adding a new muscle is a one-line change.
- **Source stamping**: every custom exercise gets `source: 'studio'` so the library UI can show a "custom" badge and the importer can skip them on merge.
- **Recent-prompts memory**: `p4_studio_recent` localStorage key remembers the last 5 AI prompts so the user can re-run them quickly.

## E3. Edge cases
- AI returns malformed JSON → `healJson` repairs; if still invalid, the user sees the raw text in an editable textarea.
- Imported exercise has unknown group → marked invalid, user must pick a valid group before saving.
- Imported exercise has unknown muscle → muscle is dropped, exercise is saved with the valid muscles only.
- Image URL is not HTTPS → URL is dropped (no mixed-content warning).

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Merge default | true | Users typically want to add, not replace |
| AI prompt history | last 5 | Enough to recall, not so many it's clutter |
| Custom badge | "Studio" tag | Distinguishes from built-in library |
| Image URL HTTPS-only | true | No mixed-content warnings |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Manual add (with validation) | FULL |
| Export library to JSON | FULL |
| Import + merge | FULL |
| AI-prompt wizard | PARTIAL (native uses Gemini, ref uses unspecified) |
| Healed JSON import | FULL (`JsonHeal`) |
| Source stamping (`source: 'studio'`) | FULL |
| Streaming renderer (no all-at-once render) | MISSING (native uses LazyColumn — equivalent) |

**Coverage: 90%**

## H. Top 3 gaps to close
1. **AI prompt streaming** — the reference waits for the full AI response before rendering. Native should stream tokens so the user sees progress on long prompts.
2. **Diff view on import+merge** — currently the merge happens silently. Add a diff modal that shows "5 new, 3 updated, 2 unchanged" before the user commits.
3. **Custom exercise sharing** — no way to share a custom exercise with another user except via export+import. Add a deep-link "Share exercise" intent that encodes the exercise as a URL fragment.
