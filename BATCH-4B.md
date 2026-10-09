# BATCH-4B.md — Profiles, Theme, Onboarding

Predecessor: Batch 4A (vCode 17, versionName 1.4.7). Ship as versionCode 18, versionName 1.4.8.

The client tested Batch 4A on their phone. This batch addresses what they found, plus two related areas we want to open up at the same time.

Before writing any code, read README.md, SPEC.md, LANDMINES.md, WORKING-STYLE.md, ACCEPTANCE.md.

---

## 1. Theme color bug

What the client sees: dark/light mode toggle works. Accent color selection does not — tapping a swatch highlights it, but nothing in the app changes color.

What we found: reading ui/ThemeController.kt and ui/screens/Settings.kt, the state flow looks correct on paper, but there are two structural suspects:

- ProTheme(dynamic = ...) accepts a Material You flag from p4_dynamic. If that flag is on (possibly by default on Android 12+), the dynamic palette wins and the user's selected accent is never used. Dark/light would still respond because it is a separate parameter.
- ThemeController.accent() is a function returning a value. Compose observes state reads inside composition. A function that returns the state's value may still track it, but the pattern is fragile. Where a card keeps its own local remember copy of accentId, the card's own UI can lag behind the source of truth.

Consult relevant experts before deciding the fix. This is a design-system question as much as a state-management one — the answer should feel right to a user who expects tapping a color to recolor the app instantly.

Fix direction we would suggest: first, check the on-device value of p4_dynamic — if it is on, the app is doing what it was told, just not what the user expected. Decide whether Material You should default off, or whether the app should show Material You is active — turn off to use a custom accent. Second, decide whether ThemeController.set() should persist itself, so no caller can forget to write p4_theme. Third, make each picker read accent state from a single source. If you see a better path, propose it.

Verify: pick an accent — the whole app recolors immediately, without a restart. Open the other picker card — same accent highlighted. Kill and reopen — persists.

---

## 2. Profile data isolation extended

What the client sees: workout history is already separate per profile. But some things still feel shared — the current workout draft, muscle-fatigue state, some settings.

What we found: Profiles.switchTo() reopens the Room database, but a few pieces of state live outside the per-profile namespace. The current workout draft, the vault snapshots (partially), and some preferences may still be global.

Consult relevant experts before deciding the model. The question is not which fields but what is the boundary — what belongs to the user (profile) and what belongs to the app (device). Once the boundary is clear, the code follows.

Fix direction we would suggest: enumerate everything the app stores. For each item, decide per-profile or global. Per-profile: workout records, current workout draft, muscle fatigue, exercise records, vault snapshots, personal settings (name, birthdate, goals, workout days). Global: theme mode, accent, language, onboarding-complete flag, device ID.

Verify: create two profiles. In profile A, generate a workout — leave it as a draft. Switch to profile B. Profile B must see no draft. Switch back to A. A's draft is exactly where it was left. Same for fatigue, snapshots, settings.

---

## 3. Profile picker: live swap without restart

What the client sees: selecting a different profile requires restarting the app to see the change.

What we found: Profiles.switchTo() flushes state, reopens the store, reloads data, and notifies. It should not need a restart. Something downstream is not observing the change — likely the composition is holding onto state that was loaded at boot.

Consult relevant experts before deciding the state flow. This is a flow-design question as much as a data question. What does the user expect to see the instant they pick a profile? Where should they land?

Fix direction we would suggest: after switchTo(), the app should navigate to that profile's Dashboard. Everything the Dashboard renders must observe the same state source that switchTo() writes to, so a single notifyChanged() re-renders the whole screen.

Verify: from the Profile Picker, tap a different profile. The current screen transitions to the new profile's Dashboard without a restart. All numbers, name, streak, workouts reflect the new profile.

---

## 4. Default profile — rename and change-default

What the client sees: the first profile is called just Default. They want it to read Default (<user name>) — the user's real name in parentheses. They also want to be able to change which profile is treated as the default.

What we found: Profiles.DEFAULT = "default" is a fixed ID. The display name is hardcoded as "Default". Nothing today lets the user reassign default.

Consult relevant experts before deciding. This touches naming conventions, the mental model of default, and how a user scans a list.

Fix direction we would suggest: label format is `Name (Default) (Currently selected)` — the profile's own name first, `(Default)` in parentheses only on the default profile, `(Currently selected)` appended on the active profile. Examples: an active default profile named "Alex" reads `Alex (Default) (Currently selected)`. A non-active non-default profile named "Sam" reads just `Sam`. A default profile with no name reads `Default (Currently selected)` if it is active, `Default` otherwise. All parts are pulled live from the profile meta and the user name — nothing is hardcoded. Add a `Set as default` action on any profile in the picker. Decide what "default" means — the profile the app opens to on cold start, or a profile with special delete-protection, or both. State the choice in the delivery.

Verify: Default shows the label Default (Name) where Name is pulled live from the profile's Personal Info — never hardcoded. Setting another profile as default makes the app open to it on next cold start. The original default profile's data is untouched.

---

## 5. New profile name flows into Personal Info

What the client sees: create a profile named Alex — the profile is created, but the Personal Info name field inside it is empty.

What we found: Profiles.create() writes to the profile registry, but the new profile's Room database starts empty (name blank).

Consult relevant experts before deciding. Small fix, but worth a moment on how a user perceives the flow — profile identity vs. personal identity.

Fix direction we would suggest: when creating a profile, seed the new Room database with user.name = the profile's name. Other fields stay at their defaults.

Verify: create profile Alex. Open Personal Info in that profile. Name field reads Alex. Delete Alex — no residue elsewhere.

---

## 6. New-user detection

What the client wants: reliable discrimination between this is a fresh install and this user has been here before. Not a single signal — several, combined.

Consult relevant experts before deciding the signals. This is a data-integrity question. Getting it wrong is either intrusive (asking onboarding of existing users) or neglectful (skipping it for new users who need it).

Signals we would suggest, in decreasing strength:

- p4_onboarded flag — set when the onboarding wizard completes
- p4_consent_log — has any consent been recorded
- p4_health_screened_at — has the health screen ever been completed
- Workout count > 0
- Exercise record count > 0
- First-launch timestamp (p4_first_launch) vs. now

Combination rule: a profile is new only if none of the strong signals are present. If any one of them is set, treat the profile as existing. Design the rule so a user who wiped their workouts but kept their onboarding flag is still existing.

Verify: fresh install — wizard fires. Existing user with only onboarding flag set — wizard skipped. Existing user with only workouts but no flag (rare migration case) — decide and document what happens; probably run onboarding once.

---

## 7. Onboarding intro for new users

What the client sees: they want a first-run wizard for new users — both the default profile and any newly created profile that has no data.

Consult relevant experts before deciding the sequence. This is the single most-seen flow in the app. Visual hierarchy, pacing, and copy all matter here.

Steps we would suggest (matching the HTML source of truth):

1. Welcome + name
2. Birth date + gender
3. Reading level
4. Theme
5. Consent (terms, privacy, waiver)

Each step keeps its own draft so the wizard is resumable. Finishing writes p4_onboarded and records consents.

Verify: new install — wizard runs once. Complete it — not shown again. Create a new profile — wizard runs for that profile only.

---

## 8. Health screen first-use intro

What the client wants: a brief intro before the health screen questions appear the first time, so the user understands what they are about to fill in.

Consult relevant experts before deciding the presentation.

Fix direction we would suggest: before the questions, one screen that says what this is and why. Then the questions. After completion, p4_health_screened_at is set and the intro never shows again for that profile.

Verify: new profile — intro shows. Complete the screen — next time, the questions open directly.

---

## Ship as

- versionCode: 18
- versionName: 1.4.8
- Cert: 1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953
- Package: com.peakform.fitness

Deliverable: pro-batch-4b.zip, same structure as pro-batch-4a.zip.

Run these five and paste raw output in README.md:

    ./gradlew clean testReleaseUnitTest assembleRelease
    aapt2 dump badging app/build/outputs/apk/release/app-release.apk | head -1
    apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
    sha256sum app/build/outputs/apk/release/app-release.apk
    python3 verify_apk.py app/build/outputs/apk/release/app-release.apk

Target values: versionCode='18', versionName='1.4.8', hash differs from 9D249BDFEFA7272A220161A20937A53AA27514592C6C3FF7C9133AA71F5D8C1E.

## Screenshots required

- Theme picker: accent tapped, whole app recolored
- Same after restart
- Profile picker: default entry showing its personal-info name in parentheses alongside another profile
- Profile A draft workout, then Profile B (no draft), then back to A (draft present)
- Profile swap: before and after (no restart)
- New profile name appearing in Personal Info
- Onboarding wizard first step, for a fresh install
- Health screen intro, for a new profile

## Reply with

pro-batch-4b.zip attached or linked, plus the five verification outputs pasted in your reply.

If any item is unclear, or if you see a better path than we suggested, say so before starting. One message now saves a rebuild later.
