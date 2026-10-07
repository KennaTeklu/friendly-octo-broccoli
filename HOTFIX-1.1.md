# HOTFIX-1.1.md — required before Batch 2

Batch 1 (vCode 11, versionName 1.4.1, SHA-256 `88625962fffada93a7dee3ebdd01a0bca3baaaa01308e2f647dc043e4f44f81e`) is installed on the client's phone. Two problems found during acceptance testing. Both must be fixed and re-verified before Batch 2 begins.

## Fix 1 — Library causes Application Not Responding (ANR)

**Symptom:** Client opens Library. After ~5 seconds the OS shows "Application Not Responding" and force-closes the app.

**Not a crash — an ANR.** The main thread is blocked. Confirmed from `adb logcat`:
- `Surface(name=370b97 Application Not Responding: com.peakform.fitness)`
- `ActivityRecord{f6b5a2e u0 com.peakform.fitness/.MainActivity t73 isExiting}`
- `Activity top resumed state loss timeout`
- `android.os.DeadObjectException`

**Root cause (diagnosis):** 1,413 library cards are being laid out with animated canvases and heavy recomposition on the main thread. The Library screen also uses streaming groups and 8 group-by modes. The combination of animated 3D pictograms plus large list layout is blocking recomposition long enough to trigger an ANR.

**Required fix:**
1. Remove the 3D animated pictogram rendering from library cards.
2. Ensure the Library opens with 1,413 items loaded and stays open for 60 seconds without ANR.
3. If the ANR persists after removing the animation, profile the main thread with Android Studio Profiler (or `adb shell dumpsys gfxinfo com.peakform.fitness`) and identify the blocking call. Fix the underlying cause — do not wrap in try/catch.

## Fix 2 — Remove all in-app 3D exercise visuals

**Reason:** The HTML source of truth does not ship in-app 3D visuals. It provides two buttons per card that open the system browser:
- **"How to do"** → `https://www.google.com/search?q=<exercise name> how to do`
- **"Images"** → `https://www.google.com/search?tbm=isch&q=<exercise name>`

Real human demonstration video and photos from professional trainers is the standard the client wants. Inaccurate 3D renderings create liability and can teach wrong form.

**Required changes:**
1. Delete the 3D animation renderer (`RigArt.kt` and any dependent Compose drawing code if it exists only for in-app library animation).
2. Keep card layout otherwise unchanged: name, muscle tags, difficulty chip, badges, 1RM lines, Last / Next progression lines, favorite star, "Add to Workout" button.
3. Keep both buttons ("How to do", "Images") on every card.
4. Muscle tags stay clickable → Muscle Wiki lookup.
5. Remove animated media from the exercise detail sheet as well. Detail sheet should show: name, muscles, equipment, difficulty, instructions (text), and the same two buttons.

**Verify:** No 3D animated figure appears anywhere in the Library or exercise detail. Screenshot proof required.

## Version and packaging

- versionCode: **12**
- versionName: **1.4.2**
- Signing cert: `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953` (unchanged)
- Package: `com.peakform.fitness` (unchanged)
- Deliverable: `pro-hotfix-1.1.zip` with the same structure as before

## Verification (must be in the delivery)

- Library opens and stays open for 60 seconds with all 1,413 items — screen recording or timestamped screenshots
- No 3D figure anywhere — screenshot of a library card
- "How to do" button opens browser to Google Search — screenshot
- "Images" button opens browser to Google Images — screenshot
- Main thread profile before/after the fix (gfxinfo or Android Studio Profiler) pasted into CHANGES.md
- Five verification commands from ACCEPTANCE.md, raw output in README.md
- CHANGES.md as three-column table (Item | Status | Evidence)

## Acceptance

Install `pro-hotfix-1.1.zip` with `adb install -r`. Open Library. Tap every group-by mode. Tap into 5 different exercises. Nothing ANRs. No 3D visuals anywhere.

## Then

After HOTFIX-1.1 is accepted, proceed to BATCHES.md Batch 2 — Library.