# 15 - The environment

Training does not happen in a perfect gym. It happens in the environment the user is in. The coach must know which one.

## Environmental variables

- Location type: gym, home, hotel, outdoors, park, traveling
- Available equipment: barbell, rack, dumbbells, bands, cables, machines, kettlebells, benches, mats, none
- Space constraints: ceiling height for overhead press, floor space for lunges
- Temperature: hot, temperate, cold
- Altitude: sea level, moderate, high
- Floor: wood, rubber, tile, carpet, concrete, grass
- Bar weight variance: standard 20 kg, women's 15 kg, specialty 25 kg, unknown

## How the coach reads it

Every prescription passes through an environment filter before it is issued. The filter can:

- Substitute exercises for available equipment
- Adjust rest if the environment is loud or hot
- Reduce load if altitude or temperature warrants
- Select unilateral variants if space is tight
- Prefer carries or loaded walks outdoors
- Prefer bands in a hotel room

The user never has to tell the coach the environment twice. The coach remembers per location.

## Environmental events

When the user marks "training somewhere new," the coach runs a two-question environment capture:

1. What equipment do you have access to?
2. Any space or temperature constraints?

Stored as a Location event in the state tree. Available for every future session in that location.

## What the coach never does

- Never assumes a perfect gym.
- Never silently fails to substitute. If an exercise cannot be done, the coach says so and offers the alternative.
- Never overrides equipment choices the user made. If the user says "barbell only," the coach does not add dumbbells.