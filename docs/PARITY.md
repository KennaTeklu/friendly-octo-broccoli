# Parity matrix — legacy HTML app vs Pro 1.1 native

Legend: ✅ translated natively · 🔶 placeholder/simplified (documented in CHANGES.md) · ⛔ intentionally not ported (harmful/dead)

| Area | Status | Notes |
|---|---|---|
| Dashboard: readiness gauge, stat cards, rings, recommendation, composite chart, next-workout, resume | ✅ | formulas verbatim |
| Dashboard elite-date + SBD forecast + 3-month projection card | ✅ | projection card merged into forecast card |
| Workout: chips, details, P4 collapsible cards, state badges | ✅ | one-open-at-a-time preserved |
| 1RM test panel + failure path + auto-open logger | ✅ | |
| Logger: weight ±2.5 stepper, sets, per-set reps, RPE 1–10 chips, RIR, failure, notes | ✅ | cluster/duration special-cases simplified to per-set reps/seconds |
| Rest timer overlay (+15 s / Done) + auto-start after effort | ✅ | |
| Complete flow: unlogged confirm, summary, celebration, BIGGER/STRONGER, backup nudge | ✅ | |
| Generation: splits, rotation, deload, taper, roulette, prescription branches, athletic blocks | ✅ | |
| Time wizard (Continue/Generate + 15/25/40/60/90/All + trimmer) | ✅ | |
| Longevity workout builder | ✅ | |
| History: 5 tabs, expandable rows, delete confirm, notes, fix-log | ✅ | fix-log edits weight/reps/rpe inline via detail row; CSV export via share/settings |
| Progress: longevity circle, forecast, volume bars, strength trend, mastery, records | ✅ | radar + RPE histogram + goal-cycle detail in 1.2 |
| Recovery: 49 muscles, 4 categories, readiness, recommendations, generators | ✅ | |
| Library: 1413+145 exercises, search, group-by, cards, detail, add-to-workout | ✅ | streaming renderer replaced by chunked groups (60/page) |
| Muscle tags → Wikipedia / "How to do" web search | ⛔ | no INTERNET permission in native build (privacy upgrade) |
| Settings: 10 P4 groups + env card | ✅ | |
| Settings: Height/Experience/Goal/WorkoutDays/Rest/Progression (orphaned in legacy) | ✅ | resurfaced in "Training preferences" |
| Danger zone: type-RESET, optional backup, preserve license/registry | ✅ | |
| Profiles: registry read, picker, switch signal | 🔶 | full per-profile namespaces in 1.2 |
| Import: Format A + Complete-Backup-v1 + try4ever tolerance | ✅ | snapshot-first, additive merge |
| Export: Format A + Complete-Backup-v1 | ✅ | SAF file save |
| Themes: dark/light + 16 accents + gym mode | ✅ | |
| Reading levels L1–L10 selection + persistence | ✅ | glossary overlay + phrase import/export in 1.2 |
| Notifications prefs (3 toggles) | ✅ | in-app toasts respect them; system notifications in 1.2 |
| Paywall / trial / license gates | ✅ | legacy p4-unlock-all policy: everything unlocked, "Pro — everything unlocked" |
| Onboarding wizard + health screening | 🔶 | fresh-install flow gets a light first-run; full 5-step wizard + health tier in 1.2 |
| Library Studio (custom exercise authoring) | 🔶 | imported custom exercises train; authoring UI in 1.2 |
| Legal Center (13 docs) | 🔶 | privacy statement embedded; full docs in 1.2 |
| Command palette (Ctrl+K) | ⛔ | desktop-only in legacy |
| QR share / social share sheet | 🔶 | share via system share sheet arrives with 1.2 |
| Anti-debug (P4.Guard, p4AntiDebugV2), DOM freeze, lockouts | ⛔ | never ported |
| Vault blind autoRestore | ⛔ | replaced by guarded snapshots, restore-on-demand only |
| Remote CDN boot dependency (16 libs incl. Dexie) | ⛔ | everything bundled; app has NO internet permission |
| GAS backend telemetry POSTs | ⛔ | dead code in legacy too |
