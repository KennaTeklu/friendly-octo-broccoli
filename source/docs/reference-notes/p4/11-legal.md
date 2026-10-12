# 11 — Legal (Legal Center UI + consent recording + paywall legal id rename)

> Reference module: `reference/scripts/p4/11-legal.js` (483 lines).
> Source line ranges in p4-core.js: L15739-15950 (P4 LEGAL IIFE), L29558-29811 (P4 Fix #11 — rename paywall template legal ids).
> Native counterparts: `core/Consent.kt`, `ui/screens/LegalCenter.kt`, `assets/data/legal_docs.json`.

## A. File purpose
Owns the **Legal Center UI** (`P4.Legal.center()` modal), consent recording (`P4.Legal.recordConsent(action, payload)` writes to `p4_consent_log`), data-legal delegation (links to export/delete-data flows), and **P4 Fix #11** — a defensive patch that finds paywall template elements with legacy legal-id naming (`legal-terms`, `legal-privacy`) and renames them to the canonical ids (`p4-legal-terms`, `p4-legal-privacy`) expected by `P4.Legal.center()`.

## B. Public surface
- `P4.Legal.init()` — wires Legal Center open buttons.
- `P4.Legal.center(doc?)` — opens the Legal Center modal; optionally opens a specific doc.
- `P4.Legal.open(doc)` — opens a specific legal doc (terms / privacy / disclaimer).
- `P4.Legal.recordConsent(action, payload)` — appends a consent record to the log.
- `P4.Legal.exportConsentLog()` — exports the consent log as JSON.
- `P4.Legal.dataLegal()` — opens the data-legal delegation flow (export / delete).
- Fix #11: `_p4_legal_rename_legacy_ids()` — runs at boot.

## C. Dependencies
- `window.P4`, `P4.store`, `P4.SEC` (from `01-core.js`).
- `P4.toast`, `P4.download`, `P4.fmtDate`.
- `P4.keys.consent` (`p4_consent_log`).
- `assets/data/legal_docs.json` (loaded via Dexie or fetch).
- Swal2 (modal host), `marked.js` (renders Markdown legal docs).

## D. Native-equivalent mapping
| Reference | Native |
|---|---|
| `P4.Legal.center` | `LegalCenter.open(doc?)` (Compose dialog) |
| `P4.Legal.recordConsent` | `Consent.record(action, payload)` (Room) |
| `P4.Legal.exportConsentLog` | `Consent.exportLog()` (JSON file) |
| `P4.Legal.dataLegal` | `LegalCenter.dataLegal()` (export / delete intent) |
| Legacy id rename (Fix #11) | n/a (native uses string resources, no id collision) |

## E. Patterns worth porting
- **Consent log append-only**: every consent action (accept terms, accept privacy, grant data-export permission) is appended with `{ at, action, payload, appVersion }`. The log is immutable — users can export it for GDPR/CCPA requests.
- **Doc versioning**: each legal doc has a `version` field. When the version changes, the user must re-consent on next open.
- **Markdown rendering**: legal docs are stored as Markdown and rendered with `marked.js`. Native should mirror with `Markwon` library.

## E2. Deep patterns
- **Legacy id rename (Fix #11)**: the paywall template was authored with `id="legal-terms"` but `P4.Legal.center()` looks for `id="p4-legal-terms"`. The fix runs at boot, finds any element with the legacy id, and renames it. This is brittle — a better fix would be to update the template, but the template lives in the index.html which the P4 layer cannot easily patch.
- **Consent log entry shape**: `{ at: ISO8601, action: string, payload: object, appVersion: string, profileId: string }`. The `profileId` field ensures multi-profile devices have per-profile consent.
- **Data-legal delegation**: the "delete my data" flow first exports a backup (so the user can recover), then clears `localStorage` + Dexie. Two-step destructive flow.

## E3. Edge cases
- Legal doc missing from CDN → falls back to a hardcoded short version bundled in the JS.
- Consent log empty → user has never consented; first launch should force consent before continuing.
- `marked.js` unavailable → renders the raw Markdown in a `<pre>` (ugly but functional).
- User on a profile without consent → blocks paywall access until consented.

## E4. UX decisions
| Decision | Value | Reason |
|---|---|---|
| Consent log immutability | append-only | Legal defensibility |
| Doc re-consent on version change | required | Compliance with ToS update flow |
| Two-step data delete | export first, then delete | User can recover if they misclick |
| Modal vs full screen | modal | Keeps context visible |

## G. Audit score
| Behavior | Coverage |
|---|---|
| Legal Center modal | FULL (`LegalCenter`) |
| Consent recording (append-only) | FULL (`Consent.record`) |
| Consent log export | FULL (`Consent.exportLog`) |
| Doc versioning + re-consent | FULL (`Consent.requiresReconsent`) |
| Data-legal delegation (export/delete) | FULL (`LegalCenter.dataLegal`) |
| Legacy id rename (Fix #11) | n/a (no native equivalent needed) |
| Markdown rendering | PARTIAL (native uses Markwon, ref uses marked.js) |

**Coverage: 95%**

## H. Top 3 gaps to close
1. **Consent log remote sync** — the consent log lives only on-device. For enterprise compliance, sync to a backend (anonymized). Native should add a `Consent.sync()` WorkManager job.
2. **Per-jurisdiction doc selection** — legal docs are global. Native should select docs based on `Locale` (EU users see GDPR-specific clauses, California users see CCPA).
3. **Pre-consent block** — reference forces consent on first launch, but only via a Swal dialog. Native should block the entire app (opaque overlay) until consent is recorded.
