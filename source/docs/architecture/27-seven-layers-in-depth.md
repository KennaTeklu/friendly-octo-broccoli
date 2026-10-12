# 27 - The seven layers in depth

The seven-layer stack, expanded.

## Guardian

**Reads.** Every request from the surfaces. Every prescription from the coach. Every state write.

**Decides.** Allow / refuse / modify. Refusal is absolute only for hard safety. Everything else is modify-with-reason.

**Variables.** Injury flags. Envelope bounds. Privacy scopes. Consent state. Signature validity.

**Math.** Control Barrier Function. Refusal reason is named. Modify returns the modified object plus the reason.

**Never.** Never explains itself in user-facing copy as "the system." It speaks as the coach: "I cannot prescribe that. Here is what I can do."

## Lifecycle

**Reads.** Dossier, body signals, life events, arc state.

**Decides.** Which arc, which phase, when to transition, when to announce.

**Variables.** Arc state. Week in arc. Phase (Discovery..Acceptance). Comeback state. Season boundary.

**Math.** Hidden Markov Model over the phase states. Transition rules authored by hand. Emission probabilities learned per state.

**Never.** Never surprises. Every transition announced one session before.

## Decision

**Reads.** Arc, dossier, field, tuning, permission.

**Decides.** Which exercise, which prescription, what to say.

**Variables.** All the coach variables from 06-coaches and 07-function-library.

**Math.** The full coachDecision pipeline. Ten steps. Every prescription carries provenance.

**Never.** Never invents math. Never speaks without a reason. Never writes outside the state tree.

## Data

**Reads.** Every write from every layer.

**Decides.** Nothing. It stores and folds.

**Variables.** Every field in the state tree. Every event. Every version pin. Every signature.

**Math.** Event fold. Migration functions. Provenance DAG. Export contract schema.

**Never.** Never loses an event without an explicit forget. Never merges without evidence. Never exports without the user's signature.

## Composition

**Reads.** State projections.

**Decides.** What renders. What is visible.

**Variables.** Surface state (Today, Session, Chronicle, Goals, You). Visibility. Animation. Accessibility scale.

**Math.** None. Rendering. Projections. Granular recomposition.

**Never.** Never writes to state. Never recomposes broadly.

## Interaction

**Reads.** Input events.

**Decides.** Nothing. It records.

**Variables.** Taps. Gestures. Voice input. Session logs. Tuning choices.

**Math.** Input shaping. Debounce. Confirmation. Undo window.

**Never.** Never completes an action without the user knowing what it did.

## The field

**Reads.** Every session. Every response.

**Decides.** Nothing. It is the substrate.

**Variables.** Every (exercise, muscle, intent, time) profile. Every response rate. Every prior.

**Math.** Gaussian Process over the joint space. Posterior updates on each observation.

**Never.** Never hardcoded completely. Always learning. Always returning a distribution, not a value.

## The rule that binds them

Every layer speaks the same language. Where they diverge, jaggedness appears. Where they align, the system approaches the asymptote in a straight, quiet line.