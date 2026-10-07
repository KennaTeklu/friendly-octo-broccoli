# HOTFIX-1.1.md — required before Batch 2

Batch 1 (vCode 11, versionName 1.4.1) is installed on the client's phone. Two problems found during acceptance testing. Both must be fixed and shipped as versionCode 12 / versionName 1.4.2 before Batch 2 begins.

---

## Fix 1 — Library causes Application Not Responding (ANR)

**Symptom:** Client opens Library. After ~5 seconds the OS shows "Application Not Responding" and force-closes the app.

**Not a crash — an ANR.** Main thread is blocked. Confirmed from `adb logcat`:
- `Surface(name=370b97 Application Not Responding: com.peakform.fitness)`
- `ActivityRecord{f6b5a2e u0 com.peakform.fitness/.MainActivity t73 isExiting}`
- `Activity top resumed state loss timeout`

**Root cause:** 1,413 library cards are laid out with animated canvases and heavy recomposition on the main thread. Compose cannot recompose fast enough and the main thread blocks past the 5-second ANR threshold.

**The HTML source of truth solves this exact problem already.** Read `index (24).html` line 60111-60171 — the `streamGroupCards` function. That is the reference implementation. The technique is:

1. **Groups collapsed by default.** Cards are not rendered on Library open. Only group headers render. On Library open, you see ~6-10 group headers, no cards yet.
2. **Only the expanded group renders its cards.** Tapping a group header streams that group's cards.
3. **Stream in chunks of 20 per frame.** The HTML uses `requestAnimationFrame(nextChunk)` — 20 cards per frame, yielding between chunks. This keeps the main thread responsive.
4. **Collapsing a group clears its cards.** `contentEl.innerHTML = ''`. Memory is freed immediately.

**Required fix — the native equivalent for Compose:**

1. **Use `LazyColumn` with `key` and `contentType` on every item.** Compose recycles items by `key`. `contentType` groups items with the same layout for reuse. Without both, Compose throws away and rebuilds the whole list.
2. **Render only group headers when Library opens.** Cards not rendered until a group is expanded. This alone drops initial work from 1,413 items to ~10.
3. **When a group is expanded, render its cards lazily.** Do not precompute the entire group's rendered content. Let `LazyColumn` lay out only visible items.
4. **When a group is collapsed, remove its cards from state.** Free the memory.
5. **Remove all `Canvas`-based animated rendering from library cards** (see Fix 2 below). Animations inside `LazyColumn` items force recomposition on every frame and cause the same ANR even with recycling.
6. **If the ANR persists after the above**, add explicit pagination: after the group expands, render 20 cards at a time with a "Load 20 more" button at the bottom. This matches the HTML's chunk size exactly.

**Verification:** Open Library. It loads in under 1 second showing only group headers. Expand a group — cards appear. Expand 3 different groups — each loads in under 1 second. Collapse and re-expand — still fast. Leave the Library open for 60 seconds with a group expanded — no ANR.

---

## Fix 2 — Remove all in-app 3D exercise visuals

**Reason:** The HTML source of truth does not ship in-app 3D visuals. It provides two buttons per card that open the system browser:
- **"How to do"** → `https://www.google.com/search?q=<exercise name> how to do`
- **"Images"** → `https://www.google.com/search?tbm=isch&q=<exercise name>`

Real human demonstration video and photos from professional trainers is the standard the client wants. Inaccurate 3D renderings create liability and can teach wrong form. Additionally, animated canvases inside `LazyColumn` items are a primary contributor to the ANR.

**Required changes:**
1. Delete `RigArt.kt` and any dependent Compose drawing code whose only purpose is in-app library animation.
2. Keep card layout otherwise unchanged: name, muscle tags, difficulty chip, badges, 1RM lines, Last / Next progression lines, favorite star, "Add to Workout" button.
3. Keep both buttons ("How to do", "Images") on every card.
4. Muscle tags stay clickable → Muscle Wiki lookup.
5. Remove animated media from the exercise detail sheet as well. Detail sheet: name, muscles, equipment, difficulty, instructions (text), the same two buttons.
6. Remove any animated demo banner from the collapsed-card morph.

**Verification:** No 3D animated figure appears anywhere in Library or exercise detail. Screenshot proof required.

---

## Version and packaging

- versionCode: **12**
- versionName: **1.4.2**
- Signing cert: `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953` (unchanged)
- Package: `com.peakform.fitness` (unchanged)
- Deliverable: `pro-hotfix-1.1.zip`, same structure as Batch 1

## Verification required in delivery

- Timestamped screenshots or screen recording showing Library open → under 1 second → group headers only
- Screenshot showing group expanded with cards
- Screenshot showing two buttons work (browser opens with correct query)
- No 3D figure anywhere — screenshot
- `adb shell dumpsys gfxinfo com.peakform.fitness` output before/after the fix, pasted into CHANGES.md
- Five verification commands from ACCEPTANCE.md, raw output in README.md
- CHANGES.md as three-column table (Item | Status | Evidence)

## Acceptance

Install with `adb install -r`. Open Library. Confirm group headers only, no cards. Expand 3 groups. Tap 5 exercises. No ANR, no crash, no 3D figure.

## Then

After HOTFIX-1.1 is accepted, proceed to BATCHES.md Batch 2 — Library.