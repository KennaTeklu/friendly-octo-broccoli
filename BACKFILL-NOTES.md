# BACKFILL NOTES

## The gap

The repo's history jumped from Batch 4E (versionCode 21) directly
to Batch 31 (versionCode 31). Batches 4D and 5 through 18
(versionCodes 20 and 22-30) shipped to the phone but were never
committed at the time.

## The backfill

Nine commits were appended after Batch 31, one per missing zip,
in versionCode order. Each commit message starts with the word
"Backfill" and names the batch, versionCode, and APK hash.

Because they were appended after v31 rather than inserted in
their correct chronological positions, `git log` shows the
backfill commits above v31. This was a deliberate choice:
appending required no force-push and no history rewrite.

## versionCode 27

No zip exists on disk for versionCode 27. Whether that version
was ever built is unknown. It is left as a gap.

## How to look up a batch

    git log --all --grep "versionCode 30"
    git show <commit>:source/
