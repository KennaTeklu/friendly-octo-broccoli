# 14 - The body as signal channel

The body is not only a response to training. It is an ongoing broadcast. The coach must be tuned to the channel, not only polling for outcomes.

## The channel

The body speaks every hour. Most apps only listen when it is convenient.

Continuous signals:

- Sleep duration and quality (from wearables, from self-report, from sleep timer)
- HRV (if available)
- Resting heart rate drift over weeks
- Soreness on waking (self-report, one tap)
- Mood within the first hour (self-report, one tap)
- Menstrual phase and symptoms (for users who opt in)
- Appetite and digestion (self-report, occasional)
- Energy at three points: morning, mid-day, evening

Event signals:

- Sharp pain (from any screen, always one tap away)
- Dull ache that persists past 48 hours
- Dizziness, nausea, or anything else that stops a set
- Unusual shortness of breath
- Night sweats, fever, or signs of systemic illness

## How the coach reads it

The signals feed four things:

1. Session readiness (should today be pushed, held, or reduced)
2. Arc modulation (should the current arc extend, compress, or pause)
3. Comeback detection (is the user entering a soft interruption before it is a hard one)
4. Health watch (are any signals converging in a way a doctor should see)

The body broadcasts. The coach reads the channel. The user never has to explain.

## What the coach never does

- Never diagnoses. Concern is not a diagnosis. The coach recommends a doctor.
- Never invents meaning from one signal. Patterns come from multiple signals over time.
- Never shares the channel with anyone without consent. The body channel is private.
- Never uses the channel to shame. Rest is not weakness. Pain is not failure.

## Where the channel lives

Per profile. Stored in the state tree under BodySignal events. Read by the Overall Coach, by the arc machine, and by the phase classifier. Never exported without the user's explicit key.