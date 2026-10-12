# Reference Note: 02 Theme Write Through

## Summary
This note documents the design contract for the feature shipped in BATCH-5A/5B and later batches.

## Storage contract
- Per-profile state lives in `p4_ls_<profileId>` SharedPreferences.
- Global state (active profile id, default profile id, device id, consent log) stays in `p4_ls`.

## Invariants
- All writes are write-through: the runtime state and the persisted state change in the same call.
- All reads resolve through the active profile's prefs file.
- Migration is one-shot and idempotent (gated on `p4_v22_migrated`).

## References
- BATCHES.md (root)
- DESIGN-CONTRACT.md
