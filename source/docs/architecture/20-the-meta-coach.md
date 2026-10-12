# 20 - The meta-coach

The coach watches the user. Who watches the coach?

## The problem

Coaches drift. Over years, a coach can:

- Over-prescribe volume as the user ages
- Miss signs of an injury developing
- Stay rigid in a phase that no longer serves
- Become too flexible, losing the shape of the plan
- Accumulate small biases that compound
- Fail to notice when its own rules are wrong

Without supervision, the coach has no correction. Drift compounds.

## The meta-coach

A separate decision layer that reads the coach's history and produces audits.

What it monitors:

- **Envelope violations.** Has the coach issued any prescription outside its envelope?
- **Volume drift.** Is total volume rising without corresponding recovery?
- **Intensity drift.** Is intensity rising without arc justification?
- **Response lag.** Is the coach adjusting fast enough to real changes?
- **Refusal rate.** Is the coach refusing too often, or never?
- **Consistency.** Are similar inputs producing similar outputs?
- **Innovation.** Is the coach doing anything new, or has it ossified?
- **User signal alignment.** Are the coach's decisions matching the user's stated intents?

## When the meta-coach speaks

Not to the user. To the coach, and to the release team.

- Weekly audit of the coach's own decisions
- Monthly envelope review per user
- Quarterly rule review (are any rules failing?)
- Yearly coach version recommendation (should this coach be updated?)

## What the meta-coach never does

- Never prescribes. It audits.
- Never overrides the coach silently. It flags and logs.
- Never reaches the user without the coach's knowledge.

## The output

A weekly audit entry. A monthly envelope note. A yearly coach-version recommendation.

The output lives in a separate log. Not shown to the user. Read by the release team. Available for the user if they ask.