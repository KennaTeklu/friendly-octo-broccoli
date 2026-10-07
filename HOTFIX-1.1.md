# HOTFIX-1.1.md — required fixes before Batch 2

Batch 1 shipped (vCode 11, versionName 1.4.1). Two problems were found on the client's device during acceptance testing. Both must be fixed before Batch 2 work begins.

---

## Fix 1 — Library crashes on open

**Symptom:** Client opens Library screen; app crashes immediately.

**Reproduction:** Install vCode 11 (pro (6).zip), launch, tap Library from the bottom dock, app force-closes.

**Required action:**
1. Get the crash trace with `adb logcat -b crash -d | tail -50` after reproducing.
2. Identify the exact exception (Compose recomposition? Animated media rendering? Null pointer in group builder?).
3. Fix the root cause. Do not wrap in try/catch and call it fixed — find the actual bug.
4. Verify: Library opens and stays open for 60 seconds with 1,413 items loaded. No crash.

**Known context:** The vCode 9 divergent branch added animated 3D pictograms to library cards. The vCode 10 branch did not. The merge may have introduced an incompatibility between the animated media renderer and the vCode 10 library layout (streaming groups, 8 group-by modes, filter chips).

---

## Fix 2 — Remove in-app 3D exercise visuals

**Symptom:** Library cards display 3D animated stick-athlete pictograms that are sometimes inaccurate.

**Why remove:** The HTML source of truth does not ship in-app 3D visuals. It provides two buttons per card — "How to do" and "Images" — that open the system browser to Google Search and Google Images. Real human demonstrations (professional trainers, gym channels) show the movement. Accurate demonstration matters; an inaccurate 3D figure creates liability and teaches wrong form.

**Required action:**
1. Delete the 3D animated pictogram renderer (`RigArt.kt` and any related Compose drawing code if the only purpose is in-app animation).
2. Keep card layout otherwise identical: name, muscle tags, difficulty chip, badges, 1RM lines, etc.
3. Ensure the two buttons remain: "How to do" (opens `https://www.google.com/search?q=<exercise name> how to do` in the system browser) and "Images" (opens `https://www.google.com/search?tbm=isch&q=<exercise name>` in the system browser).
4. Ensure muscle tags are clickable → Muscle Wiki lookup.
5. Verify: Library shows cards with the two buttons; tapping them opens the browser to the correct query. No 3D figure anywhere in the Library.

---

## Verification (must include in the next delivery)

- Library opens and stays open (60-second soak test)
- No 3D visuals in Library — screenshot proof
- "How to do" button opens Google Search with the exercise name — screenshot proof
- "Images" button opens Google Images with the exercise name — screenshot proof
- Crash trace from the fix (before/after) pasted into CHANGES.md

---

## Version

This is a fix-up of Batch 1. Ship as **versionCode 12, versionName 1.4.2** — do not reduce below 11. Signing cert unchanged: `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953`.

Deliverable: `pro-hotfix-1.1.zip`, same structure as before.