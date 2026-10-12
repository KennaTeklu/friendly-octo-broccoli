# 07 - The function library

The engineered functions that carry Pro from Level 3 to Level 5.

## A. Coupling

A1 jointBeliefUpdate. Fuse user self-report and coach measurement into one posterior. Variables: theta (latent state), w_u (trust on user, 0.3-0.9), w_c (trust on measurement).

A2 couplingCoefficient. Mutual information between user trajectory and coach trajectory. Healthy band: 0.3*H(U) <= I <= 0.7*H(U).

A3 negotiatedSetpoint. Blend user intent and coach proposal. Weighted by confidence times surprise. Clipped to envelope.

A4 relationshipHealth. One scalar. sigmoid of weighted sum of mutual information, adherence, comeback rate, tuning engagement, chronicle reads.

## B. Presence

B1 silenceDecider. Speak iff importance * surprise * (1 - disruptionCost) * recencyDecay > threshold. Default is silent.

B2 presenceIndex. alpha*Readiness + beta*Freshness + gamma*(1 - DisruptionTicks). Target 0.85-0.95.

B3 transitionAnnounce. Announce arc transitions one session before.

## C. Field

C1 fieldQuery. Query the field for (exercise, muscle, intent, time). Gaussian Process with posterior predictive distribution.

C2 responseRateEMA. Learned response rate per (exercise, muscle). r_t = alpha*delta_t + (1-alpha)*r_{t-1}.

C3 provenanceChain. Reconstruct why a prescription happened. DAG of intent, arc, field values, engine, rules, envelopes, author.

## D. Reconciliation

D1 expectationCapture. Record what the user expected before reality.

D2 reconcileExpectation. Minimize T(message) = a*|claim - reality| + b*|claim - expectation| + c*(1 - timeFrame) + d*(1 - respectAutonomy). Truthfulness is a hard constraint.

D3 reconcileStory. C* = argmax [ Coverage + ToneFit - Hallucination - Platitude ].

## E. Substrate

E1 eventFold. Rebuild state from event log.

E2 controlBarrierEnvelope. Formal safety via Control Barrier Function. r_safe = argmin ||r - r_proposed||^2 s.t. h(r, state) >= 0.

E3 phaseClassifier. Hidden Markov Model over frequency, intensity, consistency, injuries, notes, RPE drift. States: Discovery, Establishment, Exploration, Specialization, Maintenance, Restoration, Acceptance.

## F. The meta-function

coachDecision(context, envelope, dossier, field, phase):

1. intent from dossier
2. belief = jointBeliefUpdate
3. proposal = fieldQuery
4. reference = negotiatedSetpoint
5. safe = controlBarrierEnvelope
6. if Refusal, return refusal
7. adjustment = dampedCorrection
8. message = reconcileExpectation
9. if silenceDecider != Speak, suppress
10. return CoachResponse

Every coach is this function. The specialization lives in the data.