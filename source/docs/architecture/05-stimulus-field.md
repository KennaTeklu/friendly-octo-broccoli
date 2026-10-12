# 05 - The stimulus field

The substrate beneath every coach.

## The unit

Not the exercise. The (exercise, muscle, intent, time) quadruple.

BackSquat x GluteMax x STRONGER x Build-week-3 is one query.
BackSquat x Quadriceps x BIGGER x same-week is a different query.

Each has its own stimulus. Its own mathematics. Its own response.

## The matrix

Rows: 1413 exercises. Columns: 49 muscles. Cell (E, M) is a stimulus profile with:

- primary force (PUSH/PULL/STATIC)
- joint action
- length at load
- peak tension point
- contribution weight (0..1)
- best rep range
- best intensity range
- stimulus by rep range
- stimulus by tempo
- response rate (learned)

70,000 cells. About 3,000 non-zero. The rest default.

## The field

Not a matrix. A Gaussian Process over the joint space:

f(x) ~ GP( m(x), k(x, x') )
x = (exercise, muscle, intent, arc, readiness)

m(x) is the authored default profile.
k(x, x') is a kernel (RBF plus categorical for discrete components).
Observed responses update the posterior.

Every query returns a distribution, not a value. The coach picks the action that maximizes expected intent-match minus risk.

## Response-rate learning

Each (exercise, muscle) cell starts with an authored prior. After 20+ weeks of data, the cell has this user's rate:

r_t = alpha * delta_t + (1 - alpha) * r_{t-1}
alpha = 2 / (n_effective + 1)

Bayesian shrinkage to the prior until enough observations accumulate.

## Four axes

Query the field by exercise, by muscle, by intent, by time. The four-axis query is what makes expectation reconcilable.

## Physics view

The same shape a physiotherapist uses. Which muscles, which movements, which loads, which joint angles. Injuries, progress, aging, goals all live in the same map.

## Engine selection

Given (intent, exercise family, muscle type), the right mathematics engine is selected. Twelve engines: StrengthCompound, HypertrophyCompound, HypertrophyIsolation, StrengthBand, HypertrophyBand, EnduranceIsometric, EnduranceDynamic, StrengthBodyweight, Mobility, Power, Maintenance, MinimalDose.

Each produces a PrescriptionFragment. The exercise coach reconciles fragments.

## The rule

Hardcoded rules for the movement. Learned field for this specific user. Never the reverse.