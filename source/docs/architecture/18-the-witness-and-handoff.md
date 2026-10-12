# 18 - The witness and the handoff

Training is not always private. Sometimes a partner, a physio, a doctor, or a coach needs to see part of the record. The system must have a clear model for who sees what.

## The witness

A witness is someone the user has chosen to share something with.

Types:

- Partner or spouse
- Training partner
- Physiotherapist
- Doctor
- Personal trainer (human)
- Sport coach
- Parent, sibling, or friend

What a witness can see:

- The chronicle (whole or excerpts)
- Session history
- Specific PRs
- Progress charts
- Nothing. Some witnesses see presence only. Some see more.

What a witness can never see:

- Body signals
- Injury logs
- Private notes
- Tuning intents marked private
- Self-assessment answers

## The scope

Every witness share is scoped:

- Time-bounded (this week, this phase, this season)
- Content-bounded (this subset of the record)
- Purpose-bounded (physio needs injury context; partner needs the chronicle)

The user can revoke at any time. Revocation is immediate and visible to the witness.

## The handoff

A handoff is a specific witness relationship. Temporary. Purposeful. Structured.

When the user sees a physiotherapist for a shoulder issue:

- The coach exports a handoff package: shoulder history, movement patterns, current restrictions, 8-week timeline
- The package is signed by the user's identity key
- The physio returns notes in the same format
- The coach reads the return notes and adjusts the plan

The handoff format is documented and stable. Every handoff is auditable.

## What the coach never does

- Never shares without explicit scope
- Never broadcasts changes
- Never treats a witness as a co-owner
- Never assumes a handoff is permanent