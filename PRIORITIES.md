# PRIORITIES.md — what to build, in what order

The client wants a full clone of the legacy HTML app (cautious-enigma repo). Do the following, in order. Do not skip ahead.

## P0 — must ship this build

1. Merge source/ and divergent-vcode9/source/ into one feature-complete tree. The vCode 10 build and the vCode 9 build each have features the other lacks. Both must be present in the shipped APK.
2. versionCode at least 11, versionName at least 1.4.1. Same cert, same signing process as prior builds.
3. Every screen listed in CHANGES.md must have a real device/emulator screenshot in docs/verify/.
4. All P0 items from the legacy HTML audit — onboarding, health, legal, glossary, snapshots, profiles, command palette, library group-by modes, exercise card actions (How-to/Images/muscle tags), 1RM lab, animated media, import warning dialog. These already exist in one of the two branches — merge them, do not rewrite.

## P1 — must ship, but after P0

5. Full library parity — every group-by, filter, search mode, badge, detail row present in the HTML.
6. Progress screen parity — every chart, stat, projection, record, radar, histogram in the HTML.
7. Recovery screen parity — 49-muscle readiness, coupling matrix, decay model UI.
8. History screen parity — full-text search, per-session notes, PR markers.
9. Settings parity — every toggle, every card, every control in the HTML.
10. Workout flow parity — resume, replace, skip, rest timer, superset, drop set, warmup sets, plate math.

## P2 — should ship, backlog

11. Notifications (scheduled, PR, period, deload).
12. Widgets, launcher shortcuts.
13. Accessibility pass (TalkBack labels, 48dp targets).
14. Material You dynamic color.
15. FLAG_SECURE privacy mode.
16. Health Connect two-way sync.
17. Voice input, Android Auto, Wear OS.

## P3 — nice-to-have

18. Cloud TTS (needs INTERNET permission — ask client first).
19. Google Drive backup (needs OAuth — ask client first).
20. Multi-language UI.

## Rules

- Do not add features that are not in the legacy HTML and not in this list.
- Do not remove features that are already shipped in vCode 10 or vCode 9.
- Do not skip P0 to work on P2. Ship P0 fully before moving on.
- If something is impossible on Android, note it in CHANGES.md under Not shipped — desktop-only and move on.
