# GIT HISTORY GAP — vCode 22 through 30

## What is in this repo

- `b543d6f` — Batch 4E, versionCode 21 (last code commit before gap)
- `056d2c5` — architecture documentation wave 2
- `6f9a0dd` — architecture documentation wave 1
- `b5da324` — Batch 31, versionCode 31 (pushed 2026-10-12)

## What is not

- versionCode 22 through 30 have no commits in this repo.
- Those batches shipped to the phone. `pro-batch-31.zip`
  depends on them. But no zip survives on disk for them,
  so no source history was recoverable at the time
  `b5da324` was committed.

## What this means for future audits

- An audit that reads git history cannot verify v22–v30.
- An audit that reads zips cannot verify v22–v30 either —
  the zips are gone.
- Only the dev's build sandbox or the phone can confirm
  what shipped between v21 and v31.

## Recovery

If any of `pro-batch-22.zip` through `pro-batch-30.zip`
surface later, insert them as separate commits between
`b543d6f` and `b5da324`, or commit them after `b5da324`
with a note in the message.
