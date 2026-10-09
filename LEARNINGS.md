# LEARNINGS.md — process improvements from each batch

## Working style with the agent

The agent is capable and has full context of the codebase. Frame requests as **guidance with reasoning**, not imperative instructions:

- State the symptom the client observed
- Share what we found reading the code
- Suggest a fix direction
- Invite the agent to confirm, improve, or challenge

This produces better results than "add function X, remove line Y" because:
- The agent may know a reason for the current shape we don't
- The agent may see a better fix
- Catching a misunderstanding before the build saves a full batch cycle

## Per-batch learnings

### Batch 1 — Merge baseline
- **What worked:** clear deliverable structure, exact hash target, versionCode rules
- **What failed:** the agent shipped a workspace archive instead of a batch zip. Fixed by stating the zip structure explicitly.

### Hotfix 1.1 — Library ANR
- **What worked:** referencing the HTML source of truth by line number. The agent read the exact `streamGroupCards` implementation and reproduced the technique.
- **Lesson:** when the client has a working reference, point at the specific line.

### Batch 2 — Workout + Progress
- **What worked:** performance rules stated up front (LazyColumn key + contentType, no Canvas in list items)
- **What failed:** initial delivery forgot the rebuild — APK hash matched Batch 1.

### Batch 2A — Import/Export + NaN fix
- **What worked:** the client reproduced the error on-device and captured the exact log line. The agent could see the string `"averageRPE":"NaN"` and target the fix precisely.
- **Lesson:** on-device reproduction + exact log > any description of the bug.

### Batch 3 — Recovery + History
- **What worked:** reference to HTML anchors (line numbers) for both screens
- **What failed:** personal data leaked into the repo twice (client file in `source/docs/verify/client-file/`)
- **Rule added:** never commit real client data. Synthetic fixtures only.

### Batch 4 — Settings
- **What worked:** LazyColumn + contentType performance requirement carried forward from prior batches
- **What failed:** profiles default missing; theme colors didn't apply
- **Lesson:** the client tests specific behaviors. Ask them to test the *interaction* (create profile → is Default still there?) not just the *screen* (does Settings open?).

## Rules that persist

1. `adb install -r` only, never `adb uninstall`
2. versionCode must monotonically increase
3. Cert SHA-256 must match: `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953`
4. Every ✅ needs a screenshot
5. Never commit client data
6. Guide the agent, don't command it
7. Reference the HTML source of truth by line number
8. Reproduce bugs on-device, capture the exact log, then specify the fix

## Verification workflow

After every batch delivery:
1. Extract the zip
2. Confirm APK hash differs from previous
3. Install with `-r`
4. Confirm versionCode / versionName
5. Test the specific feature(s) the batch touched
6. Report: works / partial / broken, with specifics