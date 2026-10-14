# 08 - Session state

## What a session is

One logged unit of work. A set of exercises performed
by one user at one time. Stored as a structured record.

## The schema

    data class SessionRecord(
        val id: String,
        val userId: String,
        val timestamp: Long,
        val exercises: List<ExerciseRecord>,
        val overallFelt: String?,      // "easy", "solid", "tough"
        val notes: String?,
    )

    data class ExerciseRecord(
        val exerciseId: String,
        val sets: List<SetRecord>,
        val skipped: Boolean,
        val goalId: String?,           // which goal was active
        val felt: String?,
    )

    data class SetRecord(
        val reps: Int,
        val load: Double,              // 0 for bodyweight
        val tempo: String?,            // "3-1-2", "explosive", etc.
        val rpe: Double?,              // 1-10
        val durationSeconds: Double?,  // for holds
        val completed: Boolean,
    )

## What writes

Every completed set writes one SetRecord. Every
exercise with at least one completed set writes one
ExerciseRecord. Every session with at least one
exercise writes one SessionRecord.

The field then reads the session and updates:

    for each set in each exercise:
        for each (muscle, weight) in dossier.stimulusProfile:
            field[user][muscle] += reps x weight
        for each (component, weight) in dossier.componentStimulus:
            field[user][component] += reps x weight

The multiplication is simple. The intelligence is in
the dossier's authored ratios.

## What a session is NOT

- Not a plan. Plans are the coach's output. Sessions
  are what actually happened.
- Not a score. No grading. Just record.
- Not required to match the prescription. If the coach
  said 5x5 and the user did 4x4, the session records
  4x4. The field sees the truth, not the plan.

## The timestamp is the ground truth

Session order is real. Session timestamps drive decay.
Session history drives learning.

No layers above may reorder sessions. Every layer
reads them in the order they were logged.

## What session history enables

    09-recovery-curves  — measures how fast this user
                          bounces back per muscle
    10-component-response — measures how fast this user
                            adapts per component
    11-cross-exercise-transfer — measures actual overlap
                                  between exercise pairs
                                  for this user

Session history is the only input to learning. Nothing
else. The field decays between sessions. The learning
algorithms read the sessions themselves.

## Retention

Sessions are never deleted. They are the user's history.
A user with 5 years of sessions has 5 years of learning
data. That is the point.

Sessions can be exported. Sessions can be imported.
They are the user's. If the user deletes their account,
they go with it.
