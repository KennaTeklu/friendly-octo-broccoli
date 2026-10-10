// ============================================================
// EXTRACTED FROM: index (24).html (cautious-enigma repo)
// p4-core.js — P4 namespace, shared helpers (toast/debounce/download/store), P4 layer, cross-cutting P4 fixes
// Source line ranges (1-indexed, inclusive):
//   L3644-3644  (P4_EXERCISE_COMPONENTS registry (single-line data))
//   L6002-6130  (p4AntiDebugV2 (anti-debug guard — see LANDMINES.md, kept for spec fidelity))
//   L47489-47919  (floating pill UI overlay)
//   L49103-62165  (P4 layer (P4 namespace, store, keys, SEC, toast, ACCENTS, Theme, debounce, download, Vocab, Nav, Power, Backup, Motivation, Legal, Onboard, Studio, SettingsUI, Rings, LifeStage, Emoji, Profiles, Vault, WorkoutCards))
//   L62166-62185  (P4 Fix #1 — export hook (hides banner + stamps timestamp))
//   L62413-63450  (P4 Paywall v3 (404-style takeover, 21-workout trial))
//   L63451-63508  (P4 Fix #4_5 — notification suppression + kill 1RM retest reminder)
//   L63745-63766  (P4 legacy paywall guard)
//   L64260-64387  (P4 Fix #11 — rename paywall template legal ids)
//   L64637-65057  (P4 Health Screen v2 (10-physician panel))
//   L65416-65536  (p4-boot-sync-js)
//   L65537-65697  (p4-race-fix-js)
//   L65698-65963  (p4-user-reconcile-js)
//   L65964-65983  (p4-unlock-all-js)
//   L65984-66121  (p4-wizard-guard-js)
//   L66122-66282  (p4-id-protect-js)
//   L67592-67614  (p4-workoutdata-replace-js)
//   L67615-67696  (p4-nav-final-js)
//   L67697-67732  (p4-health-fit-js)
//   L67733-67761  (p4-final-cleanup-js)
//   L67947-68040  (p4-speedup-js)
//   L68041-68099  (p4-card-state-js)
//   L68110-68158  (anonymous boot script)
//   L68159-68206  (p4DataSchemaJs (data schema + P4Backup module))
// Total source lines: 16598
// ============================================================

// ---- BEGIN extracted from index (24).html L3644-3644 (P4_EXERCISE_COMPONENTS registry (single-line data)) ----
window.P4_EXERCISE_COMPONENTS={"barbell_back_squat_high_bar":{"name":"Barbell Back Squat (high bar)","components":["muscular_strength","muscular_endurance"]},"barbell_front_squat":{"name":"Barbell Front Squat","components":["muscular_strength","muscular_endurance","coordination"]},"goblet_squat":{"name":"Goblet Squat","components":["muscular_endurance","muscular_strength"]},"dumbbell_bulgarian_split_squat":{"name":"Dumbbell Bulgarian Split Squat","components":["muscular_strength","muscular_endurance","balance"]},"leg_press":{"name":"Leg Press","components":["muscular_strength","muscular_endurance"]},"leg_extension":{"name":"Leg Extension","components":["muscular_endurance","muscular_strength"]},"smith_machine_squat":{"name":"Smith Machine Squat","components":["muscular_strength","muscular_endurance"]},"kettlebell_goblet_squat":{"name":"Kettlebell Goblet Squat","components":["muscular_endurance","muscular_strength"]},"cable_squat":{"name":"Cable Squat","components":["muscular_endurance","muscular_strength"]},"air_squat":{"name":"Air Squat","components":["muscular_endurance"]},"jump_squat":{"name":"Jump Squat","components":["power","muscular_endurance"]},"pistol_squat":{"name":"Pistol Squat","components":["muscular_strength","balance","muscular_endurance"]},"sissy_squat":{"name":"Sissy Squat","components":["muscular_endurance","muscular_strength"]},"hack_squat_machine":{"name":"Hack Squat Machine","components":["muscular_strength","muscular_endurance"]},"overhead_squat":{"name":"Overhead Squat","components":["muscular_strength","coordination","balance","muscular_endurance"]},"zercher_squat":{"name":"Zercher Squat","components":["muscular_strength","muscular_endurance","coordination"]},"paused_squat":{"name":"Paused Squat","components":["muscular_strength","muscular_endurance"]},"pin_squat":{"name":"Pin Squat","components":["muscular_strength","muscular_endurance"]},"box_squat":{"name":"Box Squat","components":["muscular_strength","muscular_endurance"]},"safety_bar_squat":{"name":"Safety Bar Squat","components":["muscular_strength","muscular_endurance"]},"sled_leg_press":{"name":"Sled Leg Press","components":["muscular_strength","muscular_endurance"]},"horizontal_leg_press":{"name":"Horizontal Leg Press","components":["muscular_strength","muscular_endurance"]},"vertical_leg_press":{"name":"Vertical Leg Press","components":["muscular_strength","muscular_endurance"]},"single_leg_press":{"name":"Single Leg Press","components":["muscular_strength","muscular_endurance","balance"]},"reverse_lunge":{"name":"Reverse Lunge","components":["muscular_strength","muscular_endurance","balance"]},"walking_lunge":{"name":"Walking Lunge","components":["muscular_endurance","muscular_strength","balance","coordination"]},"dumbbell_lunge":{"name":"Dumbbell Lunge","components":["muscular_strength","muscular_endurance","balance"]},"barbell_lunge":{"name":"Barbell Lunge","components":["muscular_strength","muscular_endurance","balance"]},"dumbbell_split_squat":{"name":"Dumbbell Split Squat","components":["muscular_strength","muscular_endurance","balance"]},"single_leg_squat":{"name":"Single Leg Squat","components":["muscular_strength","balance","muscular_endurance"]},"thruster":{"name":"Thruster","components":["power","muscular_endurance","cardiorespiratory_endurance","muscular_strength"]},"dumbbell_thruster":{"name":"Dumbbell Thruster","components":["power","muscular_endurance","cardiorespiratory_endurance"]},"wall_ball":{"name":"Wall Ball","components":["power","cardiorespiratory_endurance","muscular_endurance"]},"squat_jump":{"name":"Squat Jump","components":["power","muscular_endurance"]},"belt_squat":{"name":"Belt Squat","components":["muscular_strength","muscular_endurance"]},"landmine_squat":{"name":"Landmine Squat","components":["muscular_strength","muscular_endurance"]},"landmine_front_squat":{"name":"Landmine Front Squat","components":["muscular_strength","muscular_endurance"]},"anderson_squat":{"name":"Anderson Squat","components":["muscular_strength"]},"cambered_bar_squat":{"name":"Cambered Bar Squat","components":["muscular_strength"]},"front_box_squat":{"name":"Front Box Squat","components":["muscular_strength"]},"tempo_squat_4_1_1":{"name":"Tempo Squat 4-1-1","components":["muscular_strength","muscular_endurance"]},"1_5_rep_squat":{"name":"1.5 Rep Squat","components":["muscular_strength","muscular_endurance"]},"cyclist_squat":{"name":"Cyclist Squat","components":["muscular_strength","muscular_endurance"]},"duck_stance_squat":{"name":"Duck Stance Squat","components":["muscular_strength","muscular_endurance"]},"barbell_split_squat":{"name":"Barbell Split Squat","components":["muscular_strength","muscular_endurance"]},"front_rack_reverse_lunge":{"name":"Front-Rack Reverse Lunge","components":["muscular_strength","muscular_endurance"]},"front_rack_walking_lunge":{"name":"Front-Rack Walking Lunge","components":["muscular_strength","muscular_endurance"]},"deficit_reverse_lunge":{"name":"Deficit Reverse Lunge","components":["muscular_strength","muscular_endurance"]},"curtsy_lunge":{"name":"Curtsy Lunge","components":["muscular_strength","muscular_endurance"]},"dumbbell_deficit_curtsy_lunge":{"name":"Dumbbell Deficit Curtsy Lunge","components":["muscular_strength","muscular_endurance"]},"jumping_lunge":{"name":"Jumping Lunge","components":["power","speed"]},"static_lunge_hold":{"name":"Static Lunge Hold","components":["muscular_endurance","balance"]},"bulgarian_split_squat_jumps":{"name":"Bulgarian Split Squat Jumps","components":["power","speed"]},"step_through_lunge_complex":{"name":"Step-Through Lunge Complex","components":["muscular_strength","coordination"]},"lateral_lunge":{"name":"Lateral Lunge","components":["muscular_strength","flexibility"]},"goblet_lateral_lunge":{"name":"Goblet Lateral Lunge","components":["muscular_strength","flexibility"]},"cossack_squat":{"name":"Cossack Squat","components":["muscular_strength","flexibility","coordination"]},"kettlebell_cossack_squat":{"name":"Kettlebell Cossack Squat","components":["muscular_strength","flexibility"]},"skater_squat":{"name":"Skater Squat","components":["muscular_strength","balance"]},"assisted_pistol_squat":{"name":"Assisted Pistol Squat","components":["muscular_strength","balance"]},"pistol_squat_to_box":{"name":"Pistol Squat to Box","components":["muscular_strength","balance"]},"weighted_pistol_squat":{"name":"Weighted Pistol Squat","components":["muscular_strength","balance"]},"shrimp_squat":{"name":"Shrimp Squat","components":["muscular_strength","balance"]},"dragon_pistol_progression":{"name":"Dragon Pistol Progression","components":["muscular_strength","coordination"]},"spanish_squat":{"name":"Spanish Squat","components":["muscular_endurance"]},"wall_sit":{"name":"Wall Sit","components":["muscular_endurance"]},"weighted_wall_sit":{"name":"Weighted Wall Sit","components":["muscular_endurance"]},"single_leg_wall_sit":{"name":"Single-Leg Wall Sit","components":["muscular_endurance","balance"]},"frog_squat_pump":{"name":"Frog Squat Pump","components":["muscular_endurance","flexibility"]},"sumo_squat_pulse":{"name":"Sumo Squat Pulse","components":["muscular_endurance"]},"goblet_sumo_squat":{"name":"Goblet Sumo Squat","components":["muscular_strength","muscular_endurance"]},"vmo_push_sissy_squat":{"name":"VMO Push Sissy Squat","components":["muscular_endurance"]},"cable_sissy_squat":{"name":"Cable Sissy Squat","components":["muscular_endurance"]},"nordic_quad_curl_eccentric":{"name":"Nordic Quad Curl (Eccentric)","components":["muscular_endurance","muscular_strength"]},"quad_dip_on_bench":{"name":"Quad Dip on Bench","components":["muscular_endurance"]},"leg_extension_1_5_reps":{"name":"Leg Extension 1.5 Reps","components":["muscular_endurance"]},"leg_extension_pause_reps":{"name":"Leg Extension Pause Reps","components":["muscular_endurance"]},"leg_extension_21s":{"name":"Leg Extension 21s","components":["muscular_endurance"]},"leg_press_feet_high":{"name":"Leg Press Feet High","components":["muscular_strength","muscular_endurance"]},"leg_press_feet_low":{"name":"Leg Press Feet Low","components":["muscular_strength","muscular_endurance"]},"leg_press_single_leg_1_5_reps":{"name":"Leg Press Single-Leg 1.5 Reps","components":["muscular_endurance"]},"hack_squat_pause_reps":{"name":"Hack Squat Pause Reps","components":["muscular_strength","muscular_endurance"]},"hack_squat_1_5_reps":{"name":"Hack Squat 1.5 Reps","components":["muscular_endurance"]},"belt_squat_march":{"name":"Belt Squat March","components":["muscular_endurance","balance"]},"belt_squat_box_step_up":{"name":"Belt Squat Box Step-Up","components":["muscular_strength","muscular_endurance"]},"landmine_hack_squat":{"name":"Landmine Hack Squat","components":["muscular_strength","muscular_endurance"]},"sled_push_squat_stance":{"name":"Sled Push Squat Stance","components":["muscular_strength","cardiorespiratory_endurance","power"]},"rear_foot_elevated_goblet_squat":{"name":"Rear-Foot-Elevated Goblet Squat","components":["muscular_strength","muscular_endurance"]},"sissy_squat_to_bench":{"name":"Sissy Squat to Bench","components":["muscular_endurance"]},"knee_band_terminal_extension":{"name":"Knee-Band Terminal Extension","components":["muscular_endurance"]},"wall_ball_thruster_ladder":{"name":"Wall-Ball Thruster Ladder","components":["muscular_endurance","cardiorespiratory_endurance","power"]},"barbell_romanian_deadlift":{"name":"Barbell Romanian Deadlift","components":["muscular_strength","muscular_endurance"]},"conventional_deadlift":{"name":"Conventional Deadlift","components":["muscular_strength","power","muscular_endurance"]},"sumo_deadlift":{"name":"Sumo Deadlift","components":["muscular_strength","power"]},"dumbbell_rdl":{"name":"Dumbbell RDL","components":["muscular_strength","muscular_endurance"]},"single_leg_dumbbell_rdl":{"name":"Single-Leg Dumbbell RDL","components":["muscular_strength","balance","muscular_endurance"]},"lying_leg_curl":{"name":"Lying Leg Curl","components":["muscular_endurance","muscular_strength"]},"seated_leg_curl":{"name":"Seated Leg Curl","components":["muscular_endurance","muscular_strength"]},"glute_ham_raise":{"name":"Glute-Ham Raise","components":["muscular_strength","muscular_endurance"]},"kettlebell_swing":{"name":"Kettlebell Swing","components":["power","muscular_endurance","cardiorespiratory_endurance"]},"cable_pull_through":{"name":"Cable Pull-Through","components":["muscular_endurance","muscular_strength"]},"nordic_curl_eccentric":{"name":"Nordic Curl (eccentric)","components":["muscular_strength","muscular_endurance"]},"good_morning":{"name":"Good Morning","components":["muscular_strength","muscular_endurance"]},"hex_bar_deadlift":{"name":"Hex Bar Deadlift","components":["muscular_strength","power","muscular_endurance"]},"power_clean":{"name":"Power Clean","components":["power","muscular_strength","coordination"]},"clean_and_jerk":{"name":"Clean and Jerk","components":["power","muscular_strength","coordination"]},"snatch":{"name":"Snatch","components":["power","muscular_strength","coordination","balance"]},"clean":{"name":"Clean","components":["power","muscular_strength","coordination"]},"hang_clean":{"name":"Hang Clean","components":["power","muscular_strength","coordination"]},"power_snatch":{"name":"Power Snatch","components":["power","muscular_strength","coordination"]},"hang_power_clean":{"name":"Hang Power Clean","components":["power","muscular_strength","coordination"]},"clean_and_press":{"name":"Clean and Press","components":["power","muscular_strength","coordination","muscular_endurance"]},"muscle_snatch":{"name":"Muscle Snatch","components":["power","muscular_strength","coordination"]},"split_jerk":{"name":"Split Jerk","components":["power","muscular_strength","coordination","balance"]},"push_jerk":{"name":"Push Jerk","components":["power","muscular_strength","coordination"]},"jefferson_squat":{"name":"Jefferson Squat","components":["muscular_strength","muscular_endurance","coordination"]},"jefferson_deadlift":{"name":"Jefferson Deadlift","components":["muscular_strength","coordination"]},"zercher_deadlift":{"name":"Zercher Deadlift","components":["muscular_strength","muscular_endurance"]},"rack_pull":{"name":"Rack Pull","components":["muscular_strength"]},"deficit_deadlift":{"name":"Deficit Deadlift","components":["muscular_strength","muscular_endurance"]},"paused_deadlift":{"name":"Paused Deadlift","components":["muscular_strength","muscular_endurance"]},"standing_leg_curl":{"name":"Standing Leg Curl","components":["muscular_endurance","balance"]},"snatch_pull":{"name":"Snatch Pull","components":["power","muscular_strength"]},"clean_pull":{"name":"Clean Pull","components":["power","muscular_strength"]},"hang_snatch":{"name":"Hang Snatch","components":["power","muscular_strength","coordination"]},"reverse_hyperextension":{"name":"Reverse Hyperextension","components":["muscular_endurance","muscular_strength"]},"stiff_leg_deadlift":{"name":"Stiff-Leg Deadlift","components":["muscular_strength","muscular_endurance"]},"barbell_single_leg_rdl":{"name":"Barbell Single-Leg RDL","components":["muscular_strength","balance"]},"dumbbell_stiff_leg_deadlift":{"name":"Dumbbell Stiff-Leg Deadlift","components":["muscular_strength","muscular_endurance"]},"b_stance_dumbbell_rdl":{"name":"B-Stance Dumbbell RDL","components":["muscular_strength","balance"]},"single_leg_kettlebell_rdl":{"name":"Single-Leg Kettlebell RDL","components":["muscular_strength","balance"]},"kettlebell_single_leg_deadlift":{"name":"Kettlebell Single-Leg Deadlift","components":["muscular_strength","balance"]},"band_good_morning":{"name":"Band Good Morning","components":["muscular_endurance"]},"landmine_rdl":{"name":"Landmine RDL","components":["muscular_strength","muscular_endurance"]},"glute_ham_raise_full":{"name":"Glute-Ham Raise (Full)","components":["muscular_strength","muscular_endurance"]},"glute_ham_raise_half_range":{"name":"Glute-Ham Raise (Half Range)","components":["muscular_strength","muscular_endurance"]},"nordic_hamstring_curl":{"name":"Nordic Hamstring Curl","components":["muscular_endurance","muscular_strength"]},"band_assisted_nordic_curl":{"name":"Band-Assisted Nordic Curl","components":["muscular_endurance","muscular_strength"]},"slider_leg_curl":{"name":"Slider Leg Curl","components":["muscular_endurance"]},"towel_leg_curl":{"name":"Towel Leg Curl","components":["muscular_endurance"]},"stability_ball_leg_curl":{"name":"Stability Ball Leg Curl","components":["muscular_endurance","coordination"]},"stability_ball_single_leg_curl":{"name":"Stability Ball Single-Leg Curl","components":["muscular_endurance","balance"]},"trx_leg_curl":{"name":"TRX Leg Curl","components":["muscular_endurance","coordination"]},"seated_leg_curl_1_5_reps":{"name":"Seated Leg Curl 1.5 Reps","components":["muscular_endurance"]},"seated_leg_curl_pause":{"name":"Seated Leg Curl Pause","components":["muscular_endurance"]},"lying_leg_curl_toes_in":{"name":"Lying Leg Curl Toes-In","components":["muscular_endurance"]},"lying_leg_curl_toes_out":{"name":"Lying Leg Curl Toes-Out","components":["muscular_endurance"]},"standing_cable_leg_curl":{"name":"Standing Cable Leg Curl","components":["muscular_endurance"]},"dumbbell_hamstring_curl_on_bench":{"name":"Dumbbell Hamstring Curl on Bench","components":["muscular_endurance"]},"rdl_to_clean_grip_shrug_combo":{"name":"RDL to Clean-Grip Shrug Combo","components":["muscular_strength","power"]},"barbell_hip_thrust_to_hamstring_curl_hybrid":{"name":"Barbell Hip Thrust to Hamstring Curl Hybrid","components":["muscular_endurance"]},"romanian_deadlift_to_row":{"name":"Romanian Deadlift to Row","components":["muscular_strength","muscular_endurance"]},"dead_stop_rdl_from_deficit":{"name":"Dead Stop RDL from Deficit","components":["muscular_strength"]},"snatch_grip_rdl":{"name":"Snatch-Grip RDL","components":["muscular_strength","flexibility"]},"deficit_snatch_grip_rdl":{"name":"Deficit Snatch-Grip RDL","components":["muscular_strength","flexibility"]},"single_leg_rack_pull":{"name":"Single-Leg Rack Pull","components":["muscular_strength","balance"]},"reverse_lunge_to_rdl_combo":{"name":"Reverse Lunge to RDL Combo","components":["muscular_strength","coordination"]},"good_morning_off_pins":{"name":"Good Morning off Pins","components":["muscular_strength"]},"seated_good_morning":{"name":"Seated Good Morning","components":["muscular_strength"]},"kettlebell_swing_heavy_russian":{"name":"Kettlebell Swing (Heavy, Russian)","components":["power","cardiorespiratory_endurance"]},"kettlebell_swing_american":{"name":"Kettlebell Swing (American)","components":["power","cardiorespiratory_endurance"]},"single_arm_kettlebell_swing":{"name":"Single-Arm Kettlebell Swing","components":["power","coordination","muscular_endurance"]},"alternating_kettlebell_swing":{"name":"Alternating Kettlebell Swing","components":["power","coordination","muscular_endurance"]},"broad_jump_for_distance":{"name":"Broad Jump for Distance","components":["power","speed"]},"single_leg_broad_jump":{"name":"Single-Leg Broad Jump","components":["power","balance","speed"]},"bridge_walkouts":{"name":"Bridge Walkouts","components":["muscular_endurance"]},"hamstring_bridge_pulses":{"name":"Hamstring Bridge Pulses","components":["muscular_endurance"]},"single_leg_glute_bridge":{"name":"Single-Leg Glute Bridge","components":["muscular_endurance","balance"]},"elevated_single_leg_bridge":{"name":"Elevated Single-Leg Bridge","components":["muscular_endurance","balance"]},"hamstring_floss_band_stretch_load":{"name":"Hamstring Floss Band Stretch Load","components":["flexibility","muscular_endurance"]},"rdl_iso_hold_at_bottom":{"name":"RDL Iso Hold at Bottom","components":["muscular_endurance","muscular_strength"]},"back_extension_45_degree":{"name":"Back Extension (45 Degree)","components":["muscular_endurance"]},"back_extension_with_round_back_bias":{"name":"Back Extension with Round-Back Bias","components":["muscular_endurance"]},"single_leg_back_extension":{"name":"Single-Leg Back Extension","components":["muscular_endurance","balance"]},"hinge_hip_airplane":{"name":"Hinge Hip Airplane","components":["balance","coordination","muscular_endurance"]},"prone_hamstring_curl_iso":{"name":"Prone Hamstring Curl Iso","components":["muscular_endurance"]},"cable_pull_through_single_leg":{"name":"Cable Pull-Through Single-Leg","components":["muscular_endurance","balance"]},"trap_bar_rdl":{"name":"Trap Bar RDL","components":["muscular_strength","muscular_endurance"]},"sandbag_bear_hinge":{"name":"Sandbag Bear Hinge","components":["muscular_strength","muscular_endurance"]},"hamstring_iso_squeeze_lying":{"name":"Hamstring ISO Squeeze Lying","components":["muscular_endurance"]},"glute_bridge_march":{"name":"Glute Bridge March","components":["muscular_endurance","coordination"]},"box_pistol_bottom_up":{"name":"Box Pistol Bottom-Up","components":["muscular_strength","balance"]},"barbell_hip_thrust":{"name":"Barbell Hip Thrust","components":["muscular_strength","muscular_endurance"]},"dumbbell_glute_bridge":{"name":"Dumbbell Glute Bridge","components":["muscular_endurance","muscular_strength"]},"clamshell":{"name":"Clamshell","components":["muscular_endurance"]},"cable_glute_kickback":{"name":"Cable Glute Kickback","components":["muscular_endurance"]},"step_ups":{"name":"Step-Ups","components":["muscular_endurance","balance","muscular_strength"]},"45_hip_extension":{"name":"45Â° Hip Extension","components":["muscular_endurance","muscular_strength"]},"hip_extension":{"name":"Hip Extension","components":["muscular_endurance"]},"floor_hip_extension":{"name":"Floor Hip Extension","components":["muscular_endurance"]},"frog_pumps":{"name":"Frog Pumps","components":["muscular_endurance"]},"barbell_hip_thrust_pause_reps":{"name":"Barbell Hip Thrust (Pause Reps)","components":["muscular_strength","muscular_endurance"]},"barbell_hip_thrust_single_leg":{"name":"Barbell Hip Thrust (Single-Leg)","components":["muscular_strength","muscular_endurance","balance"]},"dumbbell_hip_thrust":{"name":"Dumbbell Hip Thrust","components":["muscular_strength","muscular_endurance"]},"banded_hip_thrust":{"name":"Banded Hip Thrust","components":["muscular_endurance"]},"hip_thrust_drop_set":{"name":"Hip Thrust Drop Set","components":["muscular_endurance"]},"elevated_glute_bridge_shoulders":{"name":"Elevated Glute Bridge (Shoulders)","components":["muscular_endurance"]},"barbell_glute_bridge_floor":{"name":"Barbell Glute Bridge (Floor)","components":["muscular_strength","muscular_endurance"]},"b_stance_hip_thrust":{"name":"B-Stance Hip Thrust","components":["muscular_strength","muscular_endurance","balance"]},"cable_hip_thrust":{"name":"Cable Hip Thrust","components":["muscular_strength","muscular_endurance"]},"machine_hip_thrust":{"name":"Machine Hip Thrust","components":["muscular_strength","muscular_endurance"]},"smith_machine_hip_thrust":{"name":"Smith Machine Hip Thrust","components":["muscular_strength","muscular_endurance"]},"bulgarian_hip_thrust":{"name":"Bulgarian Hip Thrust","components":["muscular_endurance","balance"]},"cable_glute_kickback_standing":{"name":"Cable Glute Kickback (Standing)","components":["muscular_endurance"]},"cable_glute_kickback_quadruped":{"name":"Cable Glute Kickback (Quadruped)","components":["muscular_endurance"]},"donkey_kick_with_ankle_weights":{"name":"Donkey Kick with Ankle Weights","components":["muscular_endurance","balance"]},"rainbow_kickback":{"name":"Rainbow Kickback","components":["muscular_endurance","coordination"]},"fire_hydrant":{"name":"Fire Hydrant","components":["muscular_endurance","balance"]},"banded_fire_hydrant_pulse":{"name":"Banded Fire Hydrant Pulse","components":["muscular_endurance"]},"clamshell_with_band":{"name":"Clamshell with Band","components":["muscular_endurance"]},"side_lying_hip_abduction":{"name":"Side-Lying Hip Abduction","components":["muscular_endurance"]},"side_lying_hip_abduction_band":{"name":"Side-Lying Hip Abduction (Band)","components":["muscular_endurance"]},"standing_cable_hip_abduction":{"name":"Standing Cable Hip Abduction","components":["muscular_endurance","balance"]},"monster_walk_forward_and_back":{"name":"Monster Walk Forward and Back","components":["muscular_endurance","balance"]},"lateral_mini_band_walk":{"name":"Lateral Mini-Band Walk","components":["muscular_endurance"]},"banded_squat_walk_outs":{"name":"Banded Squat Walk-Outs","components":["muscular_endurance","balance"]},"seated_hip_abduction_machine":{"name":"Seated Hip Abduction Machine","components":["muscular_endurance"]},"seated_hip_abduction_lean_back":{"name":"Seated Hip Abduction Lean-Back","components":["muscular_endurance"]},"standing_hip_abduction_machine":{"name":"Standing Hip Abduction Machine","components":["muscular_endurance","balance"]},"frog_pump_weighted":{"name":"Frog Pump (Weighted)","components":["muscular_endurance"]},"single_leg_frog_pump":{"name":"Single-Leg Frog Pump","components":["muscular_endurance","balance"]},"curtsey_lunge_to_kickback":{"name":"Curtsey Lunge to Kickback","components":["muscular_strength","muscular_endurance","coordination"]},"step_up_to_reverse_lunge":{"name":"Step-Up to Reverse Lunge","components":["muscular_strength","muscular_endurance","coordination"]},"deficit_reverse_lunge_glute_bias":{"name":"Deficit Reverse Lunge (Glute Bias)","components":["muscular_strength","muscular_endurance"]},"bulgarian_split_squat_glute_bias":{"name":"Bulgarian Split Squat (Glute Bias)","components":["muscular_strength","muscular_endurance"]},"sumo_deadlift_dumbbell":{"name":"Sumo Deadlift (Dumbbell)","components":["muscular_strength","muscular_endurance"]},"kettlebell_sumo_deadlift":{"name":"Kettlebell Sumo Deadlift","components":["muscular_strength","muscular_endurance"]},"barbell_sumo_deadlift_high_pull":{"name":"Barbell Sumo Deadlift High Pull","components":["power","muscular_strength"]},"hip_thrust_march_bench":{"name":"Hip Thrust March (Bench)","components":["muscular_endurance","coordination"]},"glute_bridge_with_plate_squeeze":{"name":"Glute Bridge with Plate Squeeze","components":["muscular_endurance"]},"wall_hip_thrust_iso":{"name":"Wall Hip Thrust Iso","components":["muscular_endurance"]},"standing_glute_squeeze_iso":{"name":"Standing Glute Squeeze Iso","components":["muscular_endurance"]},"reverse_hyperextension_bench":{"name":"Reverse Hyperextension (Bench)","components":["muscular_endurance"]},"reverse_hyperextension_machine":{"name":"Reverse Hyperextension (Machine)","components":["muscular_strength","muscular_endurance"]},"quadruped_hip_extension_ankle_weights":{"name":"Quadruped Hip Extension (Ankle Weights)","components":["muscular_endurance"]},"bird_dog_with_heel_drive":{"name":"Bird Dog with Heel Drive","components":["muscular_endurance","coordination","balance"]},"single_leg_hip_thrust_on_floor":{"name":"Single-Leg Hip Thrust on Floor","components":["muscular_endurance","balance"]},"landmine_hip_thrust":{"name":"Landmine Hip Thrust","components":["muscular_strength","muscular_endurance"]},"sled_push_glute_bias":{"name":"Sled Push (Glute Bias)","components":["muscular_strength","power","cardiorespiratory_endurance"]},"glute_kickback_machine":{"name":"Glute Kickback Machine","components":["muscular_endurance"]},"hip_band_abduction_pulse_bridge":{"name":"Hip Band Abduction Pulse (Bridge)","components":["muscular_endurance"]},"deep_goblet_squat_hold":{"name":"Deep Goblet Squat Hold","components":["flexibility","muscular_endurance"]},"glute_kickback_with_banded_finish":{"name":"Glute Kickback with Banded Finish","components":["muscular_endurance"]},"squat_to_stand_adductor_opener":{"name":"Squat to Stand (Adductor Opener)","components":["flexibility","muscular_endurance"]},"hip_thrust_with_feet_on_box":{"name":"Hip Thrust with Feet on Box","components":["muscular_endurance"]},"serratus_wall_slide":{"name":"Serratus Wall Slide","components":["muscular_endurance"]},"scapular_push_up_hold":{"name":"Scapular Push-Up Hold","components":["muscular_endurance"]},"barbell_bench_press":{"name":"Barbell Bench Press","components":["muscular_strength","muscular_endurance"]},"incline_barbell_bench_press":{"name":"Incline Barbell Bench Press","components":["muscular_strength","muscular_endurance"]},"decline_barbell_bench_press":{"name":"Decline Barbell Bench Press","components":["muscular_strength","muscular_endurance"]},"dumbbell_bench_press":{"name":"Dumbbell Bench Press","components":["muscular_strength","muscular_endurance"]},"incline_dumbbell_press":{"name":"Incline Dumbbell Press","components":["muscular_strength","muscular_endurance"]},"dumbbell_fly":{"name":"Dumbbell Fly","components":["muscular_endurance"]},"chest_press_machine":{"name":"Chest Press Machine","components":["muscular_strength","muscular_endurance"]},"pec_deck_fly":{"name":"Pec Deck Fly","components":["muscular_endurance"]},"cable_crossover":{"name":"Cable Crossover","components":["muscular_endurance"]},"push_up":{"name":"Push-Up","components":["muscular_strength","muscular_endurance"]},"dips_chest_focus":{"name":"Dips (chest focus)","components":["muscular_strength","muscular_endurance"]},"svend_press":{"name":"Svend Press","components":["muscular_endurance"]},"smith_machine_bench_press":{"name":"Smith Machine Bench Press","components":["muscular_strength","muscular_endurance"]},"decline_dumbbell_bench_press":{"name":"Decline Dumbbell Bench Press","components":["muscular_strength","muscular_endurance"]},"spoto_press":{"name":"Spoto Press","components":["muscular_strength","muscular_endurance"]},"floor_press":{"name":"Floor Press","components":["muscular_strength","muscular_endurance"]},"pin_press":{"name":"Pin Press","components":["muscular_strength"]},"wide_grip_bench_press":{"name":"Wide Grip Bench Press","components":["muscular_strength","muscular_endurance"]},"reverse_grip_bench_press":{"name":"Reverse-Grip Bench Press","components":["muscular_strength","muscular_endurance"]},"close_grip_incline_bench_press":{"name":"Close Grip Incline Bench Press","components":["muscular_strength","muscular_endurance"]},"dumbbell_pullover":{"name":"Dumbbell Pullover","components":["muscular_endurance","flexibility"]},"barbell_pullover":{"name":"Barbell Pullover","components":["muscular_endurance","flexibility"]},"bent_arm_barbell_pullover":{"name":"Bent Arm Barbell Pullover","components":["muscular_endurance","flexibility"]},"archer_push_ups":{"name":"Archer Push Ups","components":["muscular_strength","muscular_endurance","balance"]},"one_arm_push_ups":{"name":"One Arm Push Ups","components":["muscular_strength","balance","coordination"]},"decline_push_up":{"name":"Decline Push Up","components":["muscular_strength","muscular_endurance"]},"incline_push_up":{"name":"Incline Push Up","components":["muscular_endurance"]},"landmine_press":{"name":"Landmine Press","components":["muscular_strength","muscular_endurance","coordination"]},"close_grip_barbell_bench_press":{"name":"Close-Grip Barbell Bench Press","components":["muscular_strength","muscular_endurance"]},"wide_grip_barbell_bench_press":{"name":"Wide-Grip Barbell Bench Press","components":["muscular_strength","muscular_endurance"]},"larsen_press":{"name":"Larsen Press","components":["muscular_strength","muscular_endurance"]},"feet_up_bench_press":{"name":"Feet-Up Bench Press","components":["muscular_strength","muscular_endurance","balance"]},"dumbbell_floor_press":{"name":"Dumbbell Floor Press","components":["muscular_strength","muscular_endurance"]},"single_arm_dumbbell_bench_press":{"name":"Single-Arm Dumbbell Bench Press","components":["muscular_strength","muscular_endurance","balance"]},"neutral_grip_dumbbell_press":{"name":"Neutral-Grip Dumbbell Press","components":["muscular_strength","muscular_endurance"]},"45_degree_incline_dumbbell_press":{"name":"45-Degree Incline Dumbbell Press","components":["muscular_strength","muscular_endurance"]},"30_degree_incline_dumbbell_press":{"name":"30-Degree Incline Dumbbell Press","components":["muscular_strength","muscular_endurance"]},"incline_dumbbell_press_neutral_grip":{"name":"Incline Dumbbell Press (Neutral Grip)","components":["muscular_strength","muscular_endurance"]},"single_arm_incline_dumbbell_press":{"name":"Single-Arm Incline Dumbbell Press","components":["muscular_strength","muscular_endurance","balance"]},"decline_dumbbell_press":{"name":"Decline Dumbbell Press","components":["muscular_strength","muscular_endurance"]},"incline_barbell_floor_to_ceiling_press":{"name":"Incline Barbell Floor to Ceiling Press","components":["muscular_strength","power"]},"incline_smith_machine_press":{"name":"Incline Smith Machine Press","components":["muscular_strength","muscular_endurance"]},"smith_machine_guillotine_press":{"name":"Smith Machine Guillotine Press","components":["muscular_endurance"]},"machine_chest_press_seated":{"name":"Machine Chest Press (Seated)","components":["muscular_strength","muscular_endurance"]},"machine_chest_press_incline":{"name":"Machine Chest Press (Incline)","components":["muscular_strength","muscular_endurance"]},"machine_fly_pec_deck":{"name":"Machine Fly (Pec Deck)","components":["muscular_endurance"]},"cable_crossover_high_to_low":{"name":"Cable Crossover (High to Low)","components":["muscular_endurance"]},"cable_crossover_low_to_high":{"name":"Cable Crossover (Low to High)","components":["muscular_endurance"]},"cable_crossover_mid_line":{"name":"Cable Crossover (Mid-Line)","components":["muscular_endurance"]},"single_arm_cable_fly":{"name":"Single-Arm Cable Fly","components":["muscular_endurance","balance"]},"cable_fly_lying":{"name":"Cable Fly (Lying)","components":["muscular_endurance"]},"incline_cable_fly":{"name":"Incline Cable Fly","components":["muscular_endurance"]},"decline_cable_fly":{"name":"Decline Cable Fly","components":["muscular_endurance"]},"dumbbell_fly_floor":{"name":"Dumbbell Fly (Floor)","components":["muscular_endurance"]},"incline_dumbbell_fly":{"name":"Incline Dumbbell Fly","components":["muscular_endurance"]},"decline_dumbbell_fly":{"name":"Decline Dumbbell Fly","components":["muscular_endurance"]},"twisting_dumbbell_fly":{"name":"Twisting Dumbbell Fly","components":["muscular_endurance"]},"plate_pinch_press":{"name":"Plate Pinch Press","components":["muscular_endurance"]},"push_up_standard":{"name":"Push-Up (Standard)","components":["muscular_strength","muscular_endurance"]},"push_up_wide":{"name":"Push-Up (Wide)","components":["muscular_strength","muscular_endurance"]},"push_up_close_diamond":{"name":"Push-Up (Close / Diamond)","components":["muscular_strength","muscular_endurance"]},"push_up_feet_elevated":{"name":"Push-Up (Feet Elevated)","components":["muscular_strength","muscular_endurance"]},"push_up_hands_elevated":{"name":"Push-Up (Hands Elevated)","components":["muscular_endurance"]},"deficit_push_up":{"name":"Deficit Push-Up","components":["muscular_strength","muscular_endurance"]},"archer_push_up":{"name":"Archer Push-Up","components":["muscular_strength","coordination","balance"]},"pseudo_planche_push_up":{"name":"Pseudo Planche Push-Up","components":["muscular_strength","muscular_endurance","coordination"]},"explosive_push_up":{"name":"Explosive Push-Up","components":["power","muscular_strength"]},"clap_push_up":{"name":"Clap Push-Up","components":["power","muscular_strength"]},"plyo_push_up_box_drop":{"name":"Plyo Push-Up (Box Drop)","components":["power","muscular_strength"]},"ring_push_up":{"name":"Ring Push-Up","components":["muscular_strength","coordination","balance"]},"trx_push_up":{"name":"TRX Push-Up","components":["muscular_strength","coordination","balance"]},"suspension_chest_fly":{"name":"Suspension Chest Fly","components":["muscular_endurance","coordination","balance"]},"ring_fly":{"name":"Ring Fly","components":["muscular_endurance","coordination"]},"dumbbell_squeeze_press":{"name":"Dumbbell Squeeze Press","components":["muscular_endurance"]},"guillotine_dumbbell_press":{"name":"Guillotine Dumbbell Press","components":["muscular_endurance"]},"chest_dip_weighted":{"name":"Chest Dip (Weighted)","components":["muscular_strength","muscular_endurance"]},"chest_dip_assisted":{"name":"Chest Dip (Assisted)","components":["muscular_strength","muscular_endurance"]},"band_chest_press":{"name":"Band Chest Press","components":["muscular_endurance"]},"band_fly":{"name":"Band Fly","components":["muscular_endurance"]},"landmine_press_half_kneeling":{"name":"Landmine Press (Half-Kneeling)","components":["muscular_strength","muscular_endurance","balance"]},"landmine_press_standing":{"name":"Landmine Press (Standing)","components":["muscular_strength","muscular_endurance","coordination"]},"iso_push_up_hold_bottom":{"name":"Iso Push-Up Hold (Bottom)","components":["muscular_endurance","muscular_strength"]},"serratus_push_up_plus":{"name":"Serratus Push-Up (Plus)","components":["muscular_endurance","coordination"]},"pull_up":{"name":"Pull-Up","components":["muscular_strength","muscular_endurance"]},"chin_up":{"name":"Chin-Up","components":["muscular_strength","muscular_endurance"]},"lat_pulldown":{"name":"Lat Pulldown","components":["muscular_strength","muscular_endurance"]},"barbell_bent_over_row":{"name":"Barbell Bent-Over Row","components":["muscular_strength","muscular_endurance"]},"pendlay_row":{"name":"Pendlay Row","components":["muscular_strength","power","muscular_endurance"]},"t_bar_row":{"name":"T-Bar Row","components":["muscular_strength","muscular_endurance"]},"single_arm_dumbbell_row":{"name":"Single-Arm Dumbbell Row","components":["muscular_strength","muscular_endurance","balance"]},"seated_cable_row":{"name":"Seated Cable Row","components":["muscular_strength","muscular_endurance"]},"straight_arm_pulldown":{"name":"Straight-Arm Pulldown","components":["muscular_endurance"]},"inverted_row":{"name":"Inverted Row","components":["muscular_strength","muscular_endurance"]},"kettlebell_row":{"name":"Kettlebell Row","components":["muscular_strength","muscular_endurance"]},"muscle_ups":{"name":"Muscle Ups","components":["muscular_strength","power","coordination"]},"ring_muscle_ups":{"name":"Ring Muscle Ups","components":["muscular_strength","power","coordination","balance"]},"one_arm_pull_ups":{"name":"One Arm Pull Ups","components":["muscular_strength","coordination","balance"]},"clap_pull_up":{"name":"Clap Pull Up","components":["power","muscular_strength"]},"chest_supported_dumbbell_row":{"name":"Chest Supported Dumbbell Row","components":["muscular_strength","muscular_endurance"]},"renegade_row":{"name":"Renegade Row","components":["muscular_strength","muscular_endurance","coordination","balance"]},"yates_row":{"name":"Yates Row","components":["muscular_strength","muscular_endurance"]},"meadows_row":{"name":"Meadows Row","components":["muscular_strength","muscular_endurance"]},"wide_grip_pull_up":{"name":"Wide-Grip Pull-Up","components":["muscular_strength","muscular_endurance"]},"close_grip_pull_up":{"name":"Close-Grip Pull-Up","components":["muscular_strength","muscular_endurance"]},"neutral_grip_pull_up":{"name":"Neutral-Grip Pull-Up","components":["muscular_strength","muscular_endurance"]},"commando_pull_up":{"name":"Commando Pull-Up","components":["muscular_strength","coordination"]},"archer_pull_up":{"name":"Archer Pull-Up","components":["muscular_strength","coordination","balance"]},"explosive_pull_up":{"name":"Explosive Pull-Up","components":["power","muscular_strength"]},"chest_to_bar_pull_up":{"name":"Chest-to-Bar Pull-Up","components":["muscular_strength","muscular_endurance"]},"typewriter_pull_up":{"name":"Typewriter Pull-Up","components":["muscular_strength","coordination","balance"]},"band_assisted_pull_up":{"name":"Band-Assisted Pull-Up","components":["muscular_strength","muscular_endurance"]},"jumping_pull_up_eccentric_focus":{"name":"Jumping Pull-Up (Eccentric Focus)","components":["muscular_endurance","muscular_strength"]},"australian_pull_up_low_bar_row":{"name":"Australian Pull-Up (Low Bar Row)","components":["muscular_strength","muscular_endurance"]},"feet_elevated_australian_pull_up":{"name":"Feet-Elevated Australian Pull-Up","components":["muscular_strength","muscular_endurance"]},"single_arm_australian_pull_up":{"name":"Single-Arm Australian Pull-Up","components":["muscular_strength","balance","muscular_endurance"]},"ring_row":{"name":"Ring Row","components":["muscular_strength","coordination","balance"]},"trx_single_arm_row":{"name":"TRX Single-Arm Row","components":["muscular_strength","balance","muscular_endurance"]},"barbell_bent_over_row_overhand":{"name":"Barbell Bent-Over Row (Overhand)","components":["muscular_strength","muscular_endurance"]},"barbell_bent_over_row_underhand":{"name":"Barbell Bent-Over Row (Underhand)","components":["muscular_strength","muscular_endurance"]},"pendlay_row_floor_start":{"name":"Pendlay Row (Floor Start)","components":["muscular_strength","power"]},"yates_row_30_degree_torso":{"name":"Yates Row (30-Degree Torso)","components":["muscular_strength","muscular_endurance"]},"meadows_row_landmine_staggered":{"name":"Meadows Row (Landmine, Staggered)","components":["muscular_strength","muscular_endurance"]},"landmine_row_straddle":{"name":"Landmine Row (Straddle)","components":["muscular_strength","muscular_endurance"]},"t_bar_row_chest_supported":{"name":"T-Bar Row (Chest-Supported)","components":["muscular_strength","muscular_endurance"]},"t_bar_row_free":{"name":"T-Bar Row (Free)","components":["muscular_strength","muscular_endurance"]},"incline_bench_dumbbell_row":{"name":"Incline Bench Dumbbell Row","components":["muscular_strength","muscular_endurance"]},"single_arm_dumbbell_row_bench_support":{"name":"Single-Arm Dumbbell Row (Bench Support)","components":["muscular_strength","muscular_endurance"]},"single_arm_dumbbell_row_pause":{"name":"Single-Arm Dumbbell Row (Pause)","components":["muscular_strength","muscular_endurance"]},"dead_stop_single_arm_row":{"name":"Dead-Stop Single-Arm Row","components":["muscular_strength","power"]},"kroc_row":{"name":"Kroc Row","components":["muscular_strength","muscular_endurance"]},"seal_row":{"name":"Seal Row","components":["muscular_strength","muscular_endurance"]},"inverted_row_underhand":{"name":"Inverted Row (Underhand)","components":["muscular_strength","muscular_endurance"]},"lat_pulldown_wide":{"name":"Lat Pulldown (Wide)","components":["muscular_strength","muscular_endurance"]},"lat_pulldown_close_neutral":{"name":"Lat Pulldown (Close Neutral)","components":["muscular_strength","muscular_endurance"]},"lat_pulldown_behind_neck":{"name":"Lat Pulldown (Behind Neck)","components":["muscular_strength","muscular_endurance"]},"single_arm_lat_pulldown":{"name":"Single-Arm Lat Pulldown","components":["muscular_strength","muscular_endurance"]},"cable_pullover_rope":{"name":"Cable Pullover (Rope)","components":["muscular_endurance"]},"dumbbell_pullover_cross_bench":{"name":"Dumbbell Pullover (Cross-Bench)","components":["muscular_endurance","flexibility"]},"standing_band_lat_pulldown":{"name":"Standing Band Lat Pulldown","components":["muscular_endurance"]},"band_straight_arm_pulldown":{"name":"Band Straight-Arm Pulldown","components":["muscular_endurance"]},"resistance_band_pull_apart_overhead":{"name":"Resistance Band Pull-Apart (Overhead)","components":["muscular_endurance"]},"seated_cable_row_wide":{"name":"Seated Cable Row (Wide)","components":["muscular_strength","muscular_endurance"]},"seated_cable_row_close_neutral":{"name":"Seated Cable Row (Close Neutral)","components":["muscular_strength","muscular_endurance"]},"seated_cable_row_single_arm":{"name":"Seated Cable Row (Single-Arm)","components":["muscular_strength","muscular_endurance"]},"half_kneeling_single_arm_cable_row":{"name":"Half-Kneeling Single-Arm Cable Row","components":["muscular_strength","balance","muscular_endurance"]},"standing_cable_row":{"name":"Standing Cable Row","components":["muscular_strength","muscular_endurance"]},"machine_row_seated":{"name":"Machine Row (Seated)","components":["muscular_strength","muscular_endurance"]},"machine_row_single_arm":{"name":"Machine Row (Single-Arm)","components":["muscular_strength","muscular_endurance"]},"renegade_row_double_dumbbell":{"name":"Renegade Row (Double Dumbbell)","components":["muscular_strength","coordination","balance"]},"bat_wing_row":{"name":"Bat Wing Row","components":["muscular_strength","muscular_endurance"]},"incline_dumbbell_y_raise_row_hybrid":{"name":"Incline Dumbbell Y-Raise Row Hybrid","components":["muscular_endurance","coordination"]},"superman_pull_prone":{"name":"Superman Pull (Prone)","components":["muscular_endurance"]},"swimmer_pull_throughs":{"name":"Swimmer Pull-Throughs","components":["muscular_endurance","coordination"]},"plate_switch_row_renegade_grip":{"name":"Plate Switch Row (Renegade Grip)","components":["muscular_strength","coordination"]},"towel_row_door_anchor":{"name":"Towel Row (Door Anchor)","components":["muscular_strength","muscular_endurance"]},"face_pull":{"name":"Face Pull","components":["muscular_endurance","coordination"]},"bent_over_reverse_fly":{"name":"Bent-Over Reverse Fly","components":["muscular_endurance"]},"reverse_pec_deck":{"name":"Reverse Pec Deck","components":["muscular_endurance"]},"band_pull_apart":{"name":"Band Pull-Apart","components":["muscular_endurance"]},"cable_reverse_fly":{"name":"Cable Reverse Fly","components":["muscular_endurance"]},"dumbbell_face_pull":{"name":"Dumbbell Face Pull","components":["muscular_endurance","coordination"]},"ytwl":{"name":"YTWL","components":["muscular_endurance","coordination"]},"face_pull_rope":{"name":"Face Pull (Rope)","components":["muscular_endurance","coordination"]},"face_pull_kneeling":{"name":"Face Pull (Kneeling)","components":["muscular_endurance","balance"]},"face_pull_with_external_rotation":{"name":"Face Pull with External Rotation","components":["muscular_endurance","coordination"]},"band_face_pull":{"name":"Band Face Pull","components":["muscular_endurance"]},"bent_over_dumbbell_reverse_fly":{"name":"Bent-Over Dumbbell Reverse Fly","components":["muscular_endurance"]},"chest_supported_reverse_fly":{"name":"Chest-Supported Reverse Fly","components":["muscular_endurance"]},"incline_dumbbell_y_raise":{"name":"Incline Dumbbell Y-Raise","components":["muscular_endurance"]},"incline_dumbbell_t_raise":{"name":"Incline Dumbbell T-Raise","components":["muscular_endurance"]},"incline_dumbbell_w_raise":{"name":"Incline Dumbbell W-Raise","components":["muscular_endurance","coordination"]},"seated_rear_delt_machine_fly":{"name":"Seated Rear Delt Machine Fly","components":["muscular_endurance"]},"standing_cable_reverse_fly":{"name":"Standing Cable Reverse Fly","components":["muscular_endurance","balance"]},"cable_rear_delt_row_rope":{"name":"Cable Rear Delt Row (Rope)","components":["muscular_endurance"]},"prone_y_t_w_raise_complex":{"name":"Prone Y-T-W Raise Complex","components":["muscular_endurance","coordination"]},"prone_dumbbell_reverse_fly":{"name":"Prone Dumbbell Reverse Fly","components":["muscular_endurance"]},"prone_dumbbell_shrug_row":{"name":"Prone Dumbbell Shrug Row","components":["muscular_endurance"]},"wall_slides_with_lift_off":{"name":"Wall Slides with Lift-Off","components":["muscular_endurance","flexibility"]},"wall_angel_hold":{"name":"Wall Angel Hold","components":["muscular_endurance","flexibility"]},"floor_angel":{"name":"Floor Angel","components":["muscular_endurance","flexibility"]},"band_pull_apart_chest_height":{"name":"Band Pull-Apart (Chest Height)","components":["muscular_endurance"]},"band_pull_apart_overhand_underhand":{"name":"Band Pull-Apart (Overhand + Underhand)","components":["muscular_endurance"]},"bent_over_barbell_rear_delt_row":{"name":"Bent-Over Barbell Rear Delt Row","components":["muscular_endurance","muscular_strength"]},"bent_over_wide_grip_row_pause":{"name":"Bent-Over Wide-Grip Row (Pause)","components":["muscular_endurance","muscular_strength"]},"reclined_ring_row_elbows_wide":{"name":"Reclined Ring Row (Elbows Wide)","components":["muscular_endurance","coordination"]},"scapular_retraction_hold_rings":{"name":"Scapular Retraction Hold (Rings)","components":["muscular_endurance","coordination","balance"]},"scap_pull_up_elevated_feet":{"name":"Scap Pull-Up (Elevated Feet)","components":["muscular_endurance","coordination"]},"single_arm_cable_reverse_fly":{"name":"Single-Arm Cable Reverse Fly","components":["muscular_endurance","balance"]},"rear_delt_row_landmine_single_arm":{"name":"Rear Delt Row (Landmine, Single-Arm)","components":["muscular_endurance","coordination"]},"bent_over_cable_y_raise":{"name":"Bent-Over Cable Y-Raise","components":["muscular_endurance","coordination"]},"reverse_plank_shoulder_squeeze":{"name":"Reverse Plank Shoulder Squeeze","components":["muscular_endurance","balance"]},"prone_swimmer_floor":{"name":"Prone Swimmer (Floor)","components":["muscular_endurance","coordination"]},"thera_band_external_rotation_arm_down":{"name":"Thera-Band External Rotation (Arm Down)","components":["muscular_endurance","coordination"]},"thera_band_external_rotation_90_90":{"name":"Thera-Band External Rotation (90/90)","components":["muscular_endurance","coordination"]},"thera_band_internal_rotation":{"name":"Thera-Band Internal Rotation","components":["muscular_endurance","coordination"]},"dumbbell_cuban_press":{"name":"Dumbbell Cuban Press","components":["muscular_endurance","coordination"]},"prone_cobra_hold":{"name":"Prone Cobra Hold","components":["muscular_endurance","balance"]},"barbell_shrug":{"name":"Barbell Shrug","components":["muscular_strength","muscular_endurance"]},"dumbbell_shrug":{"name":"Dumbbell Shrug","components":["muscular_strength","muscular_endurance"]},"smith_machine_shrug":{"name":"Smith Machine Shrug","components":["muscular_strength","muscular_endurance"]},"upright_row":{"name":"Upright Row","components":["muscular_strength","muscular_endurance"]},"dumbbell_upright_row":{"name":"Dumbbell Upright Row","components":["muscular_strength","muscular_endurance"]},"cable_upright_row":{"name":"Cable Upright Row","components":["muscular_endurance","muscular_strength"]},"hex_bar_shrug":{"name":"Hex Bar Shrug","components":["muscular_strength","muscular_endurance"]},"behind_the_back_barbell_shrug":{"name":"Behind-the-Back Barbell Shrug","components":["muscular_strength","muscular_endurance"]},"clean_high_pull":{"name":"Clean High Pull","components":["power","muscular_strength","coordination"]},"power_shrug_from_hang":{"name":"Power Shrug (from hang)","components":["power","muscular_strength"]},"trap_bar_shrug":{"name":"Trap Bar Shrug","components":["muscular_strength","muscular_endurance"]},"cable_shrug":{"name":"Cable Shrug","components":["muscular_endurance"]},"band_shrug":{"name":"Band Shrug","components":["muscular_endurance"]},"dumbbell_shrugs_pause_1_5_reps":{"name":"Dumbbell Shrugs (Pause + 1.5 Reps)","components":["muscular_endurance"]},"incline_dumbbell_shrug_chest_supported":{"name":"Incline Dumbbell Shrug (Chest-Supported)","components":["muscular_endurance"]},"leaning_single_arm_cable_shrug":{"name":"Leaning Single-Arm Cable Shrug","components":["muscular_endurance","balance"]},"dumbbell_shrug_to_row_combo":{"name":"Dumbbell Shrug to Row Combo","components":["muscular_strength","muscular_endurance"]},"upright_row_barbell_close_grip":{"name":"Upright Row (Barbell, Close Grip)","components":["muscular_strength","muscular_endurance"]},"upright_row_wide_grip":{"name":"Upright Row (Wide Grip)","components":["muscular_strength","muscular_endurance"]},"cable_upright_row_rope":{"name":"Cable Upright Row (Rope)","components":["muscular_endurance"]},"band_upright_row":{"name":"Band Upright Row","components":["muscular_endurance"]},"kettlebell_upright_row_double":{"name":"Kettlebell Upright Row (Double)","components":["muscular_strength","muscular_endurance"]},"plate_front_raise_to_shrug":{"name":"Plate Front Raise to Shrug","components":["muscular_endurance"]},"scaption_raise_thumbs_up":{"name":"Scaption Raise (Thumbs Up)","components":["muscular_endurance"]},"prone_trap_3_raise":{"name":"Prone Trap-3 Raise","components":["muscular_endurance","coordination"]},"wall_ball_overhead_carry_hold":{"name":"Wall-Ball Overhead Carry Hold","components":["muscular_endurance","muscular_strength"]},"farmer_carry_heavy":{"name":"Farmer Carry (Heavy)","components":["muscular_strength","muscular_endurance","coordination"]},"suitcase_carry":{"name":"Suitcase Carry","components":["muscular_strength","muscular_endurance","balance"]},"trap_bar_carry":{"name":"Trap Bar Carry","components":["muscular_strength","muscular_endurance"]},"overhead_dumbbell_carry":{"name":"Overhead Dumbbell Carry","components":["muscular_strength","muscular_endurance","balance"]},"waiter_carry_kettlebell":{"name":"Waiter Carry (Kettlebell)","components":["muscular_strength","muscular_endurance","balance"]},"rack_carry_double_kettlebell":{"name":"Rack Carry (Double Kettlebell)","components":["muscular_strength","muscular_endurance"]},"rack_hold_double_kettlebell":{"name":"Rack Hold (Double Kettlebell)","components":["muscular_endurance","muscular_strength"]},"sandbag_bear_hug_carry":{"name":"Sandbag Bear-Hug Carry","components":["muscular_strength","muscular_endurance","cardiorespiratory_endurance"]},"zercher_carry":{"name":"Zercher Carry","components":["muscular_strength","muscular_endurance"]},"yoke_carry_simulated":{"name":"Yoke Carry (Simulated)","components":["muscular_strength","muscular_endurance"]},"overhead_press_strict":{"name":"Overhead Press (strict)","components":["muscular_strength","muscular_endurance"]},"seated_dumbbell_press":{"name":"Seated Dumbbell Press","components":["muscular_strength","muscular_endurance"]},"arnold_press":{"name":"Arnold Press","components":["muscular_strength","muscular_endurance"]},"push_press":{"name":"Push Press","components":["muscular_strength","power"]},"log_press":{"name":"Log Press","components":["power","muscular_strength","coordination"]},"viking_press":{"name":"Viking Press","components":["muscular_strength","muscular_endurance"]},"z_press":{"name":"Z-Press","components":["muscular_strength","muscular_endurance"]},"behind_the_neck_press":{"name":"Behind The Neck Press","components":["muscular_strength","muscular_endurance","flexibility"]},"handstand_push_ups":{"name":"Handstand Push Ups","components":["muscular_strength","balance","coordination"]},"pike_push_up":{"name":"Pike Push-Up","components":["muscular_strength","muscular_endurance"]},"barbell_front_raise":{"name":"Barbell Front Raise","components":["muscular_endurance"]},"dumbbell_front_raise":{"name":"Dumbbell Front Raise","components":["muscular_endurance"]},"standing_barbell_overhead_press":{"name":"Standing Barbell Overhead Press","components":["muscular_strength","muscular_endurance"]},"seated_barbell_overhead_press":{"name":"Seated Barbell Overhead Press","components":["muscular_strength","muscular_endurance"]},"seated_dumbbell_overhead_press":{"name":"Seated Dumbbell Overhead Press","components":["muscular_strength","muscular_endurance"]},"standing_dumbbell_overhead_press":{"name":"Standing Dumbbell Overhead Press","components":["muscular_strength","muscular_endurance"]},"single_arm_dumbbell_overhead_press":{"name":"Single-Arm Dumbbell Overhead Press","components":["muscular_strength","balance","muscular_endurance"]},"alternating_dumbbell_overhead_press":{"name":"Alternating Dumbbell Overhead Press","components":["muscular_strength","coordination","muscular_endurance"]},"dumbbell_z_press":{"name":"Dumbbell Z-Press","components":["muscular_strength","muscular_endurance"]},"push_press_dumbbell":{"name":"Push Press (Dumbbell)","components":["muscular_strength","power"]},"jerk_behind_the_neck":{"name":"Jerk (Behind the Neck)","components":["power","muscular_strength"]},"landmine_press_tall_kneeling":{"name":"Landmine Press (Tall-Kneeling)","components":["muscular_strength","muscular_endurance","balance"]},"half_kneeling_single_arm_landmine_press":{"name":"Half-Kneeling Single-Arm Landmine Press","components":["muscular_strength","balance","muscular_endurance"]},"front_plate_raise":{"name":"Front Plate Raise","components":["muscular_endurance"]},"alternating_dumbbell_front_raise":{"name":"Alternating Dumbbell Front Raise","components":["muscular_endurance","coordination"]},"cable_front_raise":{"name":"Cable Front Raise","components":["muscular_endurance"]},"band_front_raise":{"name":"Band Front Raise","components":["muscular_endurance"]},"cable_y_raise":{"name":"Cable Y-Raise","components":["muscular_endurance","coordination"]},"barbell_front_raise_strict":{"name":"Barbell Front Raise (Strict)","components":["muscular_endurance"]},"plate_halo":{"name":"Plate Halo","components":["muscular_endurance","coordination"]},"kettlebell_half_kneeling_press":{"name":"Kettlebell Half-Kneeling Press","components":["muscular_strength","balance","muscular_endurance"]},"kettlebell_seesaw_press":{"name":"Kettlebell Seesaw Press","components":["muscular_strength","coordination","muscular_endurance"]},"bottoms_up_kettlebell_press":{"name":"Bottoms-Up Kettlebell Press","components":["muscular_strength","balance","muscular_endurance"]},"sots_press":{"name":"Sots Press","components":["muscular_strength","coordination"]},"barbell_thruster_heavy":{"name":"Barbell Thruster (Heavy)","components":["muscular_strength","power"]},"handstand_hold_wall":{"name":"Handstand Hold (Wall)","components":["muscular_strength","balance","coordination"]},"wall_walks":{"name":"Wall Walks","components":["muscular_strength","coordination","balance"]},"elevated_pike_push_up":{"name":"Elevated Pike Push-Up","components":["muscular_strength","muscular_endurance"]},"freestanding_handstand_hold":{"name":"Freestanding Handstand Hold","components":["muscular_strength","balance","coordination"]},"cable_press_out_overhead_iso":{"name":"Cable Press-Out (Overhead Iso)","components":["muscular_endurance","muscular_strength"]},"incline_push_up_to_shoulder_tap":{"name":"Incline Push-Up to Shoulder Tap","components":["muscular_endurance","coordination","balance"]},"dumbbell_press_bottoms_up_both":{"name":"Dumbbell Press (Bottoms-Up Both)","components":["muscular_strength","balance","muscular_endurance"]},"dumbbell_lateral_raise":{"name":"Dumbbell Lateral Raise","components":["muscular_endurance"]},"cable_lateral_raise":{"name":"Cable Lateral Raise","components":["muscular_endurance"]},"machine_lateral_raise":{"name":"Machine Lateral Raise","components":["muscular_endurance"]},"dumbbell_external_rotation":{"name":"Dumbbell External Rotation","components":["muscular_endurance","coordination"]},"cable_external_rotation":{"name":"Cable External Rotation","components":["muscular_endurance","coordination"]},"banded_lateral_walk":{"name":"Banded Lateral Walk","components":["muscular_endurance","balance"]},"dumbbell_lateral_raise_elbow_lead":{"name":"Dumbbell Lateral Raise (Elbow Lead)","components":["muscular_endurance"]},"dumbbell_lateral_raise_seated":{"name":"Dumbbell Lateral Raise (Seated)","components":["muscular_endurance"]},"single_arm_dumbbell_lateral_raise":{"name":"Single-Arm Dumbbell Lateral Raise","components":["muscular_endurance","balance"]},"cable_lateral_raise_single_arm":{"name":"Cable Lateral Raise (Single-Arm)","components":["muscular_endurance"]},"cable_lateral_raise_leaning":{"name":"Cable Lateral Raise (Leaning)","components":["muscular_endurance"]},"cable_lateral_raise_double":{"name":"Cable Lateral Raise (Double)","components":["muscular_endurance","balance"]},"band_lateral_raise":{"name":"Band Lateral Raise","components":["muscular_endurance"]},"lateral_raise_1_5_reps":{"name":"Lateral Raise 1.5 Reps","components":["muscular_endurance"]},"lateral_raise_behind_the_back_start":{"name":"Lateral Raise (Behind-the-Back Start)","components":["muscular_endurance"]},"leaning_away_cable_lateral":{"name":"Leaning Away Cable Lateral","components":["muscular_endurance","balance"]},"plate_lateral_raise":{"name":"Plate Lateral Raise","components":["muscular_endurance"]},"kettlebell_lateral_raise":{"name":"Kettlebell Lateral Raise","components":["muscular_endurance"]},"lateral_raise_iso_hold_at_top":{"name":"Lateral Raise Iso Hold at Top","components":["muscular_endurance"]},"lateral_raise_drop_set":{"name":"Lateral Raise Drop Set","components":["muscular_endurance"]},"snatch_grip_high_pull":{"name":"Snatch-Grip High Pull","components":["power","muscular_strength"]},"dumbbell_power_snatch":{"name":"Dumbbell Power Snatch","components":["power","muscular_strength","coordination"]},"kettlebell_clean_and_press":{"name":"Kettlebell Clean and Press","components":["power","muscular_strength","coordination"]},"kettlebell_snatch":{"name":"Kettlebell Snatch","components":["power","cardiorespiratory_endurance","coordination"]},"dual_kettlebell_clean":{"name":"Dual Kettlebell Clean","components":["power","muscular_strength"]},"dual_kettlebell_push_press":{"name":"Dual Kettlebell Push Press","components":["muscular_strength","power"]},"leaning_single_arm_lateral_against_wall":{"name":"Leaning Single-Arm Lateral (Against Wall)","components":["muscular_endurance","balance"]},"dumbbell_lateral_raise_to_front_raise_combo":{"name":"Dumbbell Lateral Raise to Front Raise Combo","components":["muscular_endurance"]},"cuban_rotation_cable":{"name":"Cuban Rotation (Cable)","components":["muscular_endurance","coordination"]},"wide_grip_overhead_press_behind_neck":{"name":"Wide-Grip Overhead Press (Behind Neck)","components":["muscular_strength","muscular_endurance"]},"lateral_raise_to_overhead_press":{"name":"Lateral Raise to Overhead Press","components":["muscular_endurance","muscular_strength"]},"smith_machine_overhead_press_seated":{"name":"Smith Machine Overhead Press (Seated)","components":["muscular_strength","muscular_endurance"]},"military_press_strict_paused":{"name":"Military Press (Strict, Paused)","components":["muscular_strength","muscular_endurance"]},"dumbbell_arnold_press_seated":{"name":"Dumbbell Arnold Press (Seated)","components":["muscular_strength","muscular_endurance"]},"neutral_grip_overhead_press_swiss_bar":{"name":"Neutral-Grip Overhead Press (Swiss Bar)","components":["muscular_strength","muscular_endurance"]},"cable_overhead_press_single_arm":{"name":"Cable Overhead Press (Single-Arm)","components":["muscular_strength","balance","muscular_endurance"]},"push_up_plus_scap_protraction_finish":{"name":"Push-Up Plus (Scap Protraction Finish)","components":["muscular_endurance","coordination"]},"handstand_push_up_wall":{"name":"Handstand Push-Up (Wall)","components":["muscular_strength","muscular_endurance","balance"]},"barbell_curl":{"name":"Barbell Curl","components":["muscular_strength","muscular_endurance"]},"ez_bar_curl":{"name":"EZ-Bar Curl","components":["muscular_strength","muscular_endurance"]},"dumbbell_curl":{"name":"Dumbbell Curl","components":["muscular_endurance","muscular_strength"]},"hammer_curl":{"name":"Hammer Curl","components":["muscular_endurance"]},"incline_dumbbell_curl":{"name":"Incline Dumbbell Curl","components":["muscular_endurance"]},"concentration_curl":{"name":"Concentration Curl","components":["muscular_endurance"]},"preacher_curl":{"name":"Preacher Curl","components":["muscular_endurance","muscular_strength"]},"cable_curl":{"name":"Cable Curl","components":["muscular_endurance"]},"barbell_curl_strict_back_to_wall":{"name":"Barbell Curl (Strict, Back to Wall)","components":["muscular_endurance"]},"barbell_cheat_curl_controlled":{"name":"Barbell Cheat Curl (Controlled)","components":["muscular_strength","muscular_endurance"]},"wide_grip_barbell_curl":{"name":"Wide-Grip Barbell Curl","components":["muscular_endurance"]},"close_grip_barbell_curl":{"name":"Close-Grip Barbell Curl","components":["muscular_endurance"]},"ez_bar_wide_curl":{"name":"EZ-Bar Wide Curl","components":["muscular_endurance"]},"ez_bar_close_curl":{"name":"EZ-Bar Close Curl","components":["muscular_endurance"]},"dumbbell_curl_alternating":{"name":"Dumbbell Curl (Alternating)","components":["muscular_endurance"]},"dumbbell_curl_both_arms":{"name":"Dumbbell Curl (Both Arms)","components":["muscular_endurance"]},"prone_incline_dumbbell_curl":{"name":"Prone Incline Dumbbell Curl","components":["muscular_endurance"]},"seated_dumbbell_curl":{"name":"Seated Dumbbell Curl","components":["muscular_endurance"]},"spider_curl":{"name":"Spider Curl","components":["muscular_endurance"]},"zottman_curl":{"name":"Zottman Curl","components":["muscular_endurance"]},"cross_body_hammer_curl":{"name":"Cross-Body Hammer Curl","components":["muscular_endurance"]},"preacher_curl_ez_bar":{"name":"Preacher Curl (EZ Bar)","components":["muscular_endurance"]},"preacher_curl_dumbbell_single_arm":{"name":"Preacher Curl (Dumbbell, Single-Arm)","components":["muscular_endurance"]},"preacher_curl_machine":{"name":"Preacher Curl (Machine)","components":["muscular_endurance"]},"drag_curl":{"name":"Drag Curl","components":["muscular_endurance"]},"bayesian_cable_curl":{"name":"Bayesian Cable Curl","components":["muscular_endurance"]},"cable_curl_straight_bar":{"name":"Cable Curl (Straight Bar)","components":["muscular_endurance"]},"cable_curl_ez_attachment":{"name":"Cable Curl (EZ Attachment)","components":["muscular_endurance"]},"cable_rope_hammer_curl":{"name":"Cable Rope Hammer Curl","components":["muscular_endurance"]},"single_arm_cable_curl":{"name":"Single-Arm Cable Curl","components":["muscular_endurance","balance"]},"cable_curl_behind_the_back":{"name":"Cable Curl (Behind the Back)","components":["muscular_endurance"]},"reverse_curl_barbell":{"name":"Reverse Curl (Barbell)","components":["muscular_endurance"]},"reverse_curl_ez_bar":{"name":"Reverse Curl (EZ Bar)","components":["muscular_endurance"]},"reverse_cable_curl":{"name":"Reverse Cable Curl","components":["muscular_endurance"]},"band_curl":{"name":"Band Curl","components":["muscular_endurance"]},"band_hammer_curl":{"name":"Band Hammer Curl","components":["muscular_endurance"]},"band_concentration_curl":{"name":"Band Concentration Curl","components":["muscular_endurance"]},"kettlebell_curl":{"name":"Kettlebell Curl","components":["muscular_endurance"]},"double_kettlebell_curl":{"name":"Double Kettlebell Curl","components":["muscular_strength","muscular_endurance"]},"kettlebell_bottoms_up_curl":{"name":"Kettlebell Bottoms-Up Curl","components":["muscular_endurance","balance"]},"chin_up_supinated":{"name":"Chin-Up (Supinated)","components":["muscular_strength","muscular_endurance"]},"band_assisted_chin_up":{"name":"Band-Assisted Chin-Up","components":["muscular_strength","muscular_endurance"]},"slow_eccentric_chin_up":{"name":"Slow Eccentric Chin-Up","components":["muscular_endurance","muscular_strength"]},"towel_curl_cable_or_bar":{"name":"Towel Curl (Cable or Bar)","components":["muscular_endurance"]},"suspension_strap_curl":{"name":"Suspension Strap Curl","components":["muscular_endurance","coordination","balance"]},"ring_curl":{"name":"Ring Curl","components":["muscular_endurance","coordination","balance"]},"isometric_biceps_hold_90_degrees":{"name":"Isometric Biceps Hold (90 Degrees)","components":["muscular_endurance"]},"cable_curl_21s":{"name":"Cable Curl 21s","components":["muscular_endurance"]},"close_grip_bench_press":{"name":"Close-Grip Bench Press","components":["muscular_strength","muscular_endurance"]},"skull_crusher":{"name":"Skull Crusher","components":["muscular_endurance","muscular_strength"]},"overhead_dumbbell_extension":{"name":"Overhead Dumbbell Extension","components":["muscular_endurance"]},"triceps_pushdown":{"name":"Triceps Pushdown","components":["muscular_endurance"]},"dips_triceps_focus":{"name":"Dips (triceps focus)","components":["muscular_strength","muscular_endurance"]},"diamond_push_up":{"name":"Diamond Push-Up","components":["muscular_strength","muscular_endurance"]},"close_grip_push_up":{"name":"Close Grip Push Up","components":["muscular_strength","muscular_endurance"]},"bench_dips":{"name":"Bench Dips","components":["muscular_endurance"]},"ring_dips":{"name":"Ring Dips","components":["muscular_strength","coordination","balance"]},"jm_press":{"name":"JM Press","components":["muscular_strength","muscular_endurance"]},"tate_press":{"name":"Tate Press","components":["muscular_endurance"]},"lying_dumbbell_tricep_extension":{"name":"Lying Dumbbell Tricep Extension","components":["muscular_endurance"]},"cable_overhead_tricep_extension":{"name":"Cable Overhead Tricep Extension","components":["muscular_endurance"]},"seated_dumbbell_tricep_extension":{"name":"Seated Dumbbell Tricep Extension","components":["muscular_endurance"]},"machine_tricep_extension":{"name":"Machine Tricep Extension","components":["muscular_endurance"]},"reverse_grip_tricep_pushdown":{"name":"Reverse Grip Tricep Pushdown","components":["muscular_endurance"]},"cable_kickback":{"name":"Cable Kickback","components":["muscular_endurance"]},"kickback":{"name":"Kickback","components":["muscular_endurance"]},"close_grip_floor_press":{"name":"Close-Grip Floor Press","components":["muscular_strength","muscular_endurance"]},"board_press":{"name":"Board Press","components":["muscular_strength","muscular_endurance"]},"skull_crusher_barbell":{"name":"Skull Crusher (Barbell)","components":["muscular_endurance"]},"skull_crusher_ez_bar":{"name":"Skull Crusher (EZ Bar)","components":["muscular_endurance"]},"skull_crusher_to_close_grip_press_combo":{"name":"Skull Crusher to Close-Grip Press Combo","components":["muscular_strength","muscular_endurance"]},"dumbbell_skull_crusher":{"name":"Dumbbell Skull Crusher","components":["muscular_endurance"]},"incline_skull_crusher":{"name":"Incline Skull Crusher","components":["muscular_endurance"]},"decline_skull_crusher":{"name":"Decline Skull Crusher","components":["muscular_endurance"]},"overhead_triceps_extension_dumbbell":{"name":"Overhead Triceps Extension (Dumbbell)","components":["muscular_endurance"]},"overhead_triceps_extension_ez_bar":{"name":"Overhead Triceps Extension (EZ Bar)","components":["muscular_endurance"]},"seated_overhead_dumbbell_extension":{"name":"Seated Overhead Dumbbell Extension","components":["muscular_endurance"]},"single_arm_overhead_dumbbell_extension":{"name":"Single-Arm Overhead Dumbbell Extension","components":["muscular_endurance","balance"]},"kettlebell_overhead_extension":{"name":"Kettlebell Overhead Extension","components":["muscular_endurance","balance"]},"cable_pushdown_straight_bar":{"name":"Cable Pushdown (Straight Bar)","components":["muscular_endurance"]},"cable_pushdown_v_bar":{"name":"Cable Pushdown (V-Bar)","components":["muscular_endurance"]},"cable_rope_pushdown":{"name":"Cable Rope Pushdown","components":["muscular_endurance"]},"single_arm_cable_pushdown":{"name":"Single-Arm Cable Pushdown","components":["muscular_endurance","balance"]},"cable_overhead_extension_rope":{"name":"Cable Overhead Extension (Rope)","components":["muscular_endurance"]},"cable_overhead_extension_bar":{"name":"Cable Overhead Extension (Bar)","components":["muscular_endurance"]},"reverse_grip_cable_pushdown":{"name":"Reverse-Grip Cable Pushdown","components":["muscular_endurance"]},"band_pushdown":{"name":"Band Pushdown","components":["muscular_endurance"]},"band_overhead_extension":{"name":"Band Overhead Extension","components":["muscular_endurance"]},"triceps_kickback_dumbbell":{"name":"Triceps Kickback (Dumbbell)","components":["muscular_endurance"]},"triceps_kickback_cable":{"name":"Triceps Kickback (Cable)","components":["muscular_endurance"]},"kickback_with_external_rotation_finish":{"name":"Kickback with External Rotation Finish","components":["muscular_endurance","coordination"]},"bench_dip":{"name":"Bench Dip","components":["muscular_strength","muscular_endurance"]},"bench_dip_feet_elevated":{"name":"Bench Dip (Feet Elevated)","components":["muscular_strength","muscular_endurance"]},"ring_dip":{"name":"Ring Dip","components":["muscular_strength","coordination","balance"]},"bar_dip_triceps_bias":{"name":"Bar Dip (Triceps Bias)","components":["muscular_strength","muscular_endurance"]},"band_assisted_dip":{"name":"Band-Assisted Dip","components":["muscular_strength","muscular_endurance"]},"machine_dip":{"name":"Machine Dip","components":["muscular_strength","muscular_endurance"]},"suspension_strap_dip":{"name":"Suspension Strap Dip","components":["muscular_strength","coordination","balance"]},"isometric_dip_hold_top":{"name":"Isometric Dip Hold (Top)","components":["muscular_endurance","muscular_strength"]},"isometric_dip_hold_90_degrees":{"name":"Isometric Dip Hold (90 Degrees)","components":["muscular_endurance","muscular_strength"]},"triceps_extension_machine":{"name":"Triceps Extension Machine","components":["muscular_endurance"]},"plate_pinch_overhead_extension":{"name":"Plate Pinch Overhead Extension","components":["muscular_endurance"]},"sandbag_overhead_extension":{"name":"Sandbag Overhead Extension","components":["muscular_strength","muscular_endurance"]},"skull_crusher_21s":{"name":"Skull Crusher 21s","components":["muscular_endurance"]},"cable_pushdown_drop_set":{"name":"Cable Pushdown Drop Set","components":["muscular_endurance"]},"floor_dumbbell_extension":{"name":"Floor Dumbbell Extension","components":["muscular_endurance"]},"banded_skull_crusher":{"name":"Banded Skull Crusher","components":["muscular_endurance"]},"wrist_curl_palms_up":{"name":"Wrist Curl (palms up)","components":["muscular_endurance"]},"reverse_wrist_curl_palms_down":{"name":"Reverse Wrist Curl (palms down)","components":["muscular_endurance"]},"farmer_s_walk":{"name":"Farmer\u0027s Walk","components":["muscular_strength","muscular_endurance","body_composition","coordination"]},"plate_pinch":{"name":"Plate Pinch","components":["muscular_endurance"]},"dumbbell_wrist_curl":{"name":"Dumbbell Wrist Curl","components":["muscular_endurance"]},"dumbbell_reverse_wrist_curl":{"name":"Dumbbell Reverse Wrist Curl","components":["muscular_endurance"]},"dead_hang":{"name":"Dead Hang","components":["muscular_endurance","flexibility"]},"wrist_curl_barbell":{"name":"Wrist Curl (Barbell)","components":["muscular_endurance"]},"reverse_wrist_curl_barbell":{"name":"Reverse Wrist Curl (Barbell)","components":["muscular_endurance"]},"wrist_curl_dumbbell_single_arm":{"name":"Wrist Curl (Dumbbell, Single-Arm)","components":["muscular_endurance"]},"wrist_roller":{"name":"Wrist Roller","components":["muscular_strength","muscular_endurance"]},"plate_pinch_hold":{"name":"Plate Pinch Hold","components":["muscular_endurance"]},"plate_pinch_carry":{"name":"Plate Pinch Carry","components":["muscular_endurance","coordination"]},"dead_hang_single_arm":{"name":"Dead Hang (Single-Arm)","components":["muscular_endurance","balance"]},"weighted_dead_hang":{"name":"Weighted Dead Hang","components":["muscular_endurance","muscular_strength"]},"towel_dead_hang":{"name":"Towel Dead Hang","components":["muscular_endurance"]},"fat_gripz_dumbbell_hold":{"name":"Fat Gripz Dumbbell Hold","components":["muscular_endurance"]},"farmer_hold_static":{"name":"Farmer Hold (Static)","components":["muscular_endurance","muscular_strength"]},"rope_climb_assisted":{"name":"Rope Climb (Assisted)","components":["muscular_strength","coordination","power"]},"rope_climb_legless":{"name":"Rope Climb (Legless)","components":["muscular_strength","coordination"]},"rope_pull_sled":{"name":"Rope Pull (Sled)","components":["muscular_strength","muscular_endurance"]},"barbell_hold_racked":{"name":"Barbell Hold (Racked)","components":["muscular_endurance","muscular_strength"]},"thick_bar_deadlift_hold":{"name":"Thick Bar Deadlift Hold","components":["muscular_strength","muscular_endurance"]},"cable_wrist_curl":{"name":"Cable Wrist Curl","components":["muscular_endurance"]},"cable_reverse_wrist_curl":{"name":"Cable Reverse Wrist Curl","components":["muscular_endurance"]},"dumbbell_radial_deviation":{"name":"Dumbbell Radial Deviation","components":["muscular_endurance"]},"dumbbell_ulnar_deviation":{"name":"Dumbbell Ulnar Deviation","components":["muscular_endurance"]},"pronation_isometric_partner_or_rig":{"name":"Pronation Isometric (Partner or Rig)","components":["muscular_endurance"]},"supination_isometric_partner_or_rig":{"name":"Supination Isometric (Partner or Rig)","components":["muscular_endurance"]},"fingertip_push_up_hold":{"name":"Fingertip Push-Up Hold","components":["muscular_endurance","muscular_strength"]},"fingertip_plank":{"name":"Fingertip Plank","components":["muscular_endurance","muscular_strength"]},"grip_squeezer_spring_or_grip_tool":{"name":"Grip Squeezer (Spring or Grip Tool)","components":["muscular_endurance"]},"rubber_band_finger_extensions":{"name":"Rubber Band Finger Extensions","components":["muscular_endurance"]},"rice_bucket_dig":{"name":"Rice Bucket Dig","components":["muscular_endurance","cardiorespiratory_endurance"]},"sand_grab_and_twist":{"name":"Sand Grab and Twist","components":["muscular_endurance"]},"barbell_finger_curl":{"name":"Barbell Finger Curl","components":["muscular_endurance"]},"barbell_rollout":{"name":"Barbell Rollout","components":["muscular_endurance","muscular_strength"]},"weighted_sit_up":{"name":"Weighted Sit-Up","components":["muscular_endurance","muscular_strength"]},"dumbbell_side_bend":{"name":"Dumbbell Side Bend","components":["muscular_endurance"]},"russian_twist":{"name":"Russian Twist","components":["muscular_endurance"]},"cable_crunch":{"name":"Cable Crunch","components":["muscular_endurance"]},"cable_woodchopper":{"name":"Cable Woodchopper","components":["muscular_endurance","coordination","power"]},"pallof_press":{"name":"Pallof Press","components":["muscular_endurance","coordination"]},"plank":{"name":"Plank","components":["muscular_endurance"]},"hanging_leg_raise":{"name":"Hanging Leg Raise","components":["muscular_endurance","muscular_strength"]},"bicycle_crunch":{"name":"Bicycle Crunch","components":["muscular_endurance","coordination"]},"l_sit":{"name":"L-Sit","components":["muscular_strength","muscular_endurance","balance","coordination"]},"ab_wheel":{"name":"Ab Wheel","components":["muscular_endurance","muscular_strength"]},"ghd_sit_up":{"name":"GHD Sit-Up","components":["muscular_endurance","muscular_strength"]},"crunches":{"name":"Crunches","components":["muscular_endurance"]},"sit_ups":{"name":"Sit Ups","components":["muscular_endurance"]},"decline_sit_up":{"name":"Decline Sit Up","components":["muscular_endurance","muscular_strength"]},"side_crunch":{"name":"Side Crunch","components":["muscular_endurance"]},"reverse_crunches":{"name":"Reverse Crunches","components":["muscular_endurance"]},"roman_chair_side_bend":{"name":"Roman Chair Side Bend","components":["muscular_endurance"]},"flutter_kicks":{"name":"Flutter Kicks","components":["muscular_endurance","coordination"]},"mountain_climbers":{"name":"Mountain Climbers","components":["muscular_endurance","cardiorespiratory_endurance","coordination"]},"scissor_kicks":{"name":"Scissor Kicks","components":["muscular_endurance","coordination"]},"superman":{"name":"Superman","components":["muscular_endurance"]},"hanging_knee_raise":{"name":"Hanging Knee Raise","components":["muscular_endurance"]},"toes_to_bar":{"name":"Toes-to-Bar","components":["muscular_endurance","muscular_strength"]},"lying_leg_raise":{"name":"Lying Leg Raise","components":["muscular_endurance"]},"burpees":{"name":"Burpees","components":["cardiorespiratory_endurance","muscular_endurance","power"]},"dragon_flag":{"name":"Dragon Flag","components":["muscular_strength","muscular_endurance","balance","coordination"]},"side_plank":{"name":"Side Plank","components":["muscular_endurance","balance"]},"dead_bug":{"name":"Dead Bug","components":["muscular_endurance","coordination"]},"hollow_body_hold":{"name":"Hollow Body Hold","components":["muscular_endurance"]},"v_up":{"name":"V-Up","components":["muscular_endurance"]},"ab_rollout_kneeling":{"name":"Ab Rollout (kneeling)","components":["muscular_endurance","muscular_strength"]},"ab_rollout_standing":{"name":"Ab Rollout (standing)","components":["muscular_strength","muscular_endurance","coordination"]},"standing_cable_crunch":{"name":"Standing Cable Crunch","components":["muscular_endurance"]},"kneeling_cable_crunch":{"name":"Kneeling Cable Crunch","components":["muscular_endurance"]},"rope_crunch":{"name":"Rope Crunch","components":["muscular_endurance"]},"weighted_decline_sit_up":{"name":"Weighted Decline Sitâ€‘Up","components":["muscular_endurance","muscular_strength"]},"hanging_windshield_wiper":{"name":"Hanging Windshield Wiper","components":["muscular_endurance","coordination"]},"landmine_rotation":{"name":"Landmine Rotation","components":["muscular_endurance","coordination","power"]},"medicine_ball_slam":{"name":"Medicine Ball Slam","components":["power","cardiorespiratory_endurance"]},"stability_ball_pike":{"name":"Stability Ball Pike","components":["muscular_endurance","balance","coordination"]},"stability_ball_pass":{"name":"Stability Ball Pass","components":["muscular_endurance","coordination"]},"leg_lowers":{"name":"Leg Lowers","components":["muscular_endurance"]},"pallof_press_hold":{"name":"Pallof Press (hold)","components":["muscular_endurance","coordination"]},"pallof_press_with_rotation":{"name":"Pallof Press with Rotation","components":["muscular_endurance","coordination","power"]},"stir_the_pot":{"name":"Stir the Pot","components":["muscular_endurance","coordination","balance"]},"bird_dog":{"name":"Bird Dog","components":["muscular_endurance","coordination","balance"]},"mountain_climbers_slow":{"name":"Mountain Climbers (slow)","components":["muscular_endurance","coordination"]},"dumbbell_side_bend_overhead":{"name":"Dumbbell Side Bend (overhead)","components":["muscular_endurance"]},"seated_russian_twist_weighted":{"name":"Seated Russian Twist (weighted)","components":["muscular_endurance","coordination"]},"roman_chair_leg_raise":{"name":"Roman Chair Leg Raise","components":["muscular_endurance","muscular_strength"]},"captain_s_chair_leg_raise":{"name":"Captain\u0027s Chair Leg Raise","components":["muscular_endurance","muscular_strength"]},"weighted_plank":{"name":"Weighted Plank","components":["muscular_endurance","muscular_strength"]},"plank_shoulder_tap":{"name":"Plank Shoulder Tap","components":["muscular_endurance","coordination","balance"]},"long_lever_plank":{"name":"Long-Lever Plank","components":["muscular_endurance","muscular_strength"]},"plank_up_downs":{"name":"Plank Up-Downs","components":["muscular_endurance","coordination"]},"side_plank_with_hip_dips":{"name":"Side Plank with Hip Dips","components":["muscular_endurance"]},"side_plank_with_reach_through":{"name":"Side Plank with Reach-Through","components":["muscular_endurance","coordination"]},"star_side_plank":{"name":"Star Side Plank","components":["muscular_endurance","balance"]},"copenhagen_plank_short_lever":{"name":"Copenhagen Plank (Short Lever)","components":["muscular_endurance","muscular_strength"]},"copenhagen_plank_long_lever":{"name":"Copenhagen Plank (Long Lever)","components":["muscular_endurance","muscular_strength"]},"rkc_plank":{"name":"RKC Plank","components":["muscular_endurance","muscular_strength"]},"dead_bug_band_resistance":{"name":"Dead Bug (Band Resistance)","components":["muscular_endurance","coordination"]},"bird_dog_weighted":{"name":"Bird Dog (Weighted)","components":["muscular_endurance","balance"]},"hollow_body_hold_long_lever":{"name":"Hollow Body Hold (Long Lever)","components":["muscular_endurance","muscular_strength"]},"hollow_rock":{"name":"Hollow Rock","components":["muscular_endurance","coordination"]},"hollow_hold_with_band_pull_apart":{"name":"Hollow Hold with Band Pull-Apart","components":["muscular_endurance","coordination"]},"tuck_up":{"name":"Tuck-Up","components":["muscular_endurance"]},"knee_raise_hanging":{"name":"Knee Raise (Hanging)","components":["muscular_endurance"]},"hanging_leg_raise_straight":{"name":"Hanging Leg Raise (Straight)","components":["muscular_endurance","muscular_strength"]},"l_sit_tucked":{"name":"L-Sit (Tucked)","components":["muscular_endurance","balance"]},"l_sit_full":{"name":"L-Sit (Full)","components":["muscular_endurance","balance"]},"single_leg_l_sit":{"name":"Single-Leg L-Sit","components":["muscular_endurance","balance"]},"kneeling_cable_crunch_twist":{"name":"Kneeling Cable Crunch (Twist)","components":["muscular_endurance","coordination"]},"standing_cable_woodchop_high_to_low":{"name":"Standing Cable Woodchop (High to Low)","components":["muscular_strength","muscular_endurance","power"]},"standing_cable_woodchop_low_to_high":{"name":"Standing Cable Woodchop (Low to High)","components":["muscular_strength","muscular_endurance","power"]},"half_kneeling_cable_lift":{"name":"Half-Kneeling Cable Lift","components":["muscular_strength","muscular_endurance","balance"]},"half_kneeling_cable_chop":{"name":"Half-Kneeling Cable Chop","components":["muscular_strength","muscular_endurance","balance"]},"landmine_rainbow":{"name":"Landmine Rainbow","components":["muscular_endurance","coordination","power"]},"landmine_twist":{"name":"Landmine Twist","components":["muscular_endurance","coordination"]},"russian_twist_feet_elevated":{"name":"Russian Twist (Feet Elevated)","components":["muscular_endurance","balance"]},"medicine_ball_rotational_throw":{"name":"Medicine Ball Rotational Throw","components":["power","muscular_endurance"]},"medicine_ball_overhead_slam":{"name":"Medicine Ball Overhead Slam","components":["power","muscular_endurance"]},"medicine_ball_chest_pass_wall":{"name":"Medicine Ball Chest Pass (Wall)","components":["power","muscular_endurance"]},"ab_wheel_rollout_kneeling_ribs_down":{"name":"Ab Wheel Rollout (Kneeling, Ribs Down)","components":["muscular_endurance","muscular_strength"]},"ab_wheel_rollout_standing_full_line":{"name":"Ab Wheel Rollout (Standing, Full Line)","components":["muscular_endurance","muscular_strength"]},"stability_ball_rollout":{"name":"Stability Ball Rollout","components":["muscular_endurance","coordination"]},"suspension_strap_fallout":{"name":"Suspension Strap Fallout","components":["muscular_endurance","coordination"]},"pallof_press_half_kneeling":{"name":"Pallof Press (Half-Kneeling)","components":["muscular_endurance","balance"]},"pallof_press_standing":{"name":"Pallof Press (Standing)","components":["muscular_endurance","balance"]},"pallof_iso_hold":{"name":"Pallof Iso Hold","components":["muscular_endurance","balance"]},"sit_up_arms_crossed":{"name":"Sit-Up (Arms Crossed)","components":["muscular_endurance"]},"crunch":{"name":"Crunch","components":["muscular_endurance"]},"crunch_weighted_plate":{"name":"Crunch (Weighted Plate)","components":["muscular_endurance"]},"reverse_crunch":{"name":"Reverse Crunch","components":["muscular_endurance"]},"leg_raise_floor":{"name":"Leg Raise (Floor)","components":["muscular_endurance"]},"leg_raise_bench":{"name":"Leg Raise (Bench)","components":["muscular_endurance"]},"seated_leg_tuck":{"name":"Seated Leg Tuck","components":["muscular_endurance"]},"standing_oblique_crunch_dumbbell":{"name":"Standing Oblique Crunch (Dumbbell)","components":["muscular_endurance"]},"side_bend_barbell":{"name":"Side Bend (Barbell)","components":["muscular_endurance"]},"standing_calf_raise":{"name":"Standing Calf Raise","components":["muscular_strength","muscular_endurance"]},"seated_calf_raise":{"name":"Seated Calf Raise","components":["muscular_endurance","muscular_strength"]},"donkey_calf_raise":{"name":"Donkey Calf Raise","components":["muscular_strength","muscular_endurance"]},"single_leg_calf_raise":{"name":"Single-Leg Calf Raise","components":["muscular_endurance","balance"]},"bodyweight_calf_raise":{"name":"Bodyweight Calf Raise","components":["muscular_endurance"]},"jump_rope":{"name":"Jump Rope","components":["cardiorespiratory_endurance","muscular_endurance","coordination"]},"single_leg_seated_calf_raise":{"name":"Single Leg Seated Calf Raise","components":["muscular_endurance"]},"dumbbell_calf_raise":{"name":"Dumbbell Calf Raise","components":["muscular_strength","muscular_endurance"]},"barbell_calf_raise":{"name":"Barbell Calf Raise","components":["muscular_strength","muscular_endurance"]},"jumping_jack":{"name":"Jumping Jack","components":["cardiorespiratory_endurance","coordination"]},"leg_press_calf_raise":{"name":"Leg Press Calf Raise","components":["muscular_strength","muscular_endurance"]},"standing_barbell_calf_raise":{"name":"Standing Barbell Calf Raise","components":["muscular_strength","muscular_endurance"]},"standing_dumbbell_calf_raise":{"name":"Standing Dumbbell Calf Raise","components":["muscular_strength","muscular_endurance"]},"standing_single_leg_calf_raise":{"name":"Standing Single-Leg Calf Raise","components":["muscular_strength","muscular_endurance","balance"]},"standing_calf_raise_machine":{"name":"Standing Calf Raise (Machine)","components":["muscular_strength","muscular_endurance"]},"standing_calf_raise_smith_machine":{"name":"Standing Calf Raise (Smith Machine)","components":["muscular_strength","muscular_endurance"]},"seated_calf_raise_machine":{"name":"Seated Calf Raise (Machine)","components":["muscular_endurance"]},"seated_calf_raise_barbell_on_knees":{"name":"Seated Calf Raise (Barbell on Knees)","components":["muscular_endurance"]},"seated_dumbbell_calf_raise":{"name":"Seated Dumbbell Calf Raise","components":["muscular_endurance"]},"donkey_calf_raise_machine_hinged":{"name":"Donkey Calf Raise (Machine, Hinged)","components":["muscular_strength","muscular_endurance"]},"donkey_calf_raise_partner":{"name":"Donkey Calf Raise (Partner)","components":["muscular_strength","muscular_endurance"]},"leg_press_calf_raise_toes_on_edge":{"name":"Leg Press Calf Raise (Toes on Edge)","components":["muscular_strength","muscular_endurance"]},"hack_squat_calf_raise":{"name":"Hack Squat Calf Raise","components":["muscular_strength","muscular_endurance"]},"smith_machine_donkey_raise":{"name":"Smith Machine Donkey Raise","components":["muscular_strength","muscular_endurance"]},"cable_calf_raise":{"name":"Cable Calf Raise","components":["muscular_endurance"]},"band_calf_raise":{"name":"Band Calf Raise","components":["muscular_endurance"]},"calf_raise_step_edge_pause_reps":{"name":"Calf Raise (Step Edge, Pause Reps)","components":["muscular_endurance"]},"calf_raise_1_5_reps":{"name":"Calf Raise 1.5 Reps","components":["muscular_endurance"]},"calf_raise_iso_hold_peak":{"name":"Calf Raise Iso Hold (Peak)","components":["muscular_endurance"]},"calf_raise_iso_hold_stretch":{"name":"Calf Raise Iso Hold (Stretch)","components":["muscular_endurance","flexibility"]},"jump_rope_basic_bounce":{"name":"Jump Rope (Basic Bounce)","components":["cardiorespiratory_endurance","muscular_endurance","coordination"]},"jump_rope_double_unders":{"name":"Jump Rope (Double-Unders)","components":["cardiorespiratory_endurance","coordination","power"]},"pogo_hops":{"name":"Pogo Hops","components":["power","speed","muscular_endurance"]},"single_leg_pogo_hops":{"name":"Single-Leg Pogo Hops","components":["power","balance","speed"]},"ankle_hop_four_quadrants":{"name":"Ankle Hop (Four Quadrants)","components":["power","agility","coordination"]},"calf_raise_on_leg_press_single_leg":{"name":"Calf Raise on Leg Press (Single-Leg)","components":["muscular_strength","muscular_endurance"]},"tibialis_raise_against_wall":{"name":"Tibialis Raise Against Wall","components":["muscular_endurance"]},"tibialis_raise_loaded":{"name":"Tibialis Raise (Loaded)","components":["muscular_endurance"]},"kettlebell_calf_raise_bottoms_up_bias":{"name":"Kettlebell Calf Raise (Bottoms-Up Bias)","components":["muscular_strength","muscular_endurance"]},"sled_push_calf_emphasis":{"name":"Sled Push (Calf Emphasis)","components":["muscular_strength","power","cardiorespiratory_endurance"]},"stair_calf_raise_two_leg":{"name":"Stair Calf Raise (Two-Leg)","components":["muscular_endurance"]},"adduction_machine":{"name":"Adduction Machine","components":["muscular_endurance"]},"cable_hip_adduction":{"name":"Cable Hip Adduction","components":["muscular_endurance","balance"]},"side_lying_leg_raise_lower_leg":{"name":"Side-Lying Leg Raise (lower leg)","components":["muscular_endurance"]},"side_lunge":{"name":"Side Lunge","components":["muscular_strength","muscular_endurance","flexibility"]},"sumo_squat":{"name":"Sumo Squat","components":["muscular_strength","muscular_endurance"]},"sumo_squat_barbell":{"name":"Sumo Squat (Barbell)","components":["muscular_strength","muscular_endurance"]},"wide_stance_leg_press":{"name":"Wide-Stance Leg Press","components":["muscular_strength","muscular_endurance"]},"adductor_machine":{"name":"Adductor Machine","components":["muscular_endurance"]},"adductor_machine_single_leg":{"name":"Adductor Machine (Single-Leg)","components":["muscular_endurance"]},"standing_cable_adduction":{"name":"Standing Cable Adduction","components":["muscular_endurance","balance"]},"standing_band_adduction":{"name":"Standing Band Adduction","components":["muscular_endurance","balance"]},"side_lying_adduction_bottom_leg":{"name":"Side-Lying Adduction (Bottom Leg)","components":["muscular_endurance"]},"copenhagen_adduction_knee_support":{"name":"Copenhagen Adduction (Knee Support)","components":["muscular_endurance","muscular_strength"]},"copenhagen_adduction_foot_support":{"name":"Copenhagen Adduction (Foot Support)","components":["muscular_endurance","muscular_strength"]},"sumo_deadlift_kettlebell_heavy":{"name":"Sumo Deadlift (Kettlebell, Heavy)","components":["muscular_strength","muscular_endurance"]},"sumo_box_squat":{"name":"Sumo Box Squat","components":["muscular_strength"]},"butterfly_stretch_pulse_weighted":{"name":"Butterfly Stretch Pulse (Weighted)","components":["flexibility","muscular_endurance"]},"frog_rock":{"name":"Frog Rock","components":["flexibility","muscular_endurance"]},"frog_pump_hold":{"name":"Frog Pump Hold","components":["muscular_endurance"]},"cossack_squat_loaded":{"name":"Cossack Squat (Loaded)","components":["muscular_strength","flexibility","balance"]},"lateral_lunge_goblet_deep":{"name":"Lateral Lunge (Goblet, Deep)","components":["muscular_strength","flexibility"]},"side_lying_copenhagen_raise":{"name":"Side-Lying Copenhagen Raise","components":["muscular_endurance","balance"]},"sliding_side_lunge":{"name":"Sliding Side Lunge","components":["muscular_strength","flexibility","muscular_endurance"]},"kneeling_adductor_slide_out":{"name":"Kneeling Adductor Slide-Out","components":["flexibility","muscular_endurance"]},"wall_straddle_hold":{"name":"Wall Straddle Hold","components":["flexibility","muscular_endurance"]},"sissy_squat_wide_stance":{"name":"Sissy Squat Wide Stance","components":["muscular_endurance"]},"band_sumo_walk_wide_steps":{"name":"Band Sumo Walk (Wide Steps)","components":["muscular_endurance","balance"]},"prone_frog_pulse":{"name":"Prone Frog Pulse","components":["muscular_endurance"]},"standing_groin_iso_squeeze":{"name":"Standing Groin Iso Squeeze","components":["muscular_endurance"]},"seated_groin_iso_squeeze":{"name":"Seated Groin Iso Squeeze","components":["muscular_endurance"]},"deep_sumo_hold":{"name":"Deep Sumo Hold","components":["flexibility","muscular_endurance"]},"adductor_rock_back_quadruped":{"name":"Adductor Rock-Back (Quadruped)","components":["flexibility","muscular_endurance"]},"standing_band_hip_abduction":{"name":"Standing Band Hip Abduction","components":["muscular_endurance"]},"abduction_machine":{"name":"Abduction Machine","components":["muscular_endurance"]},"cable_hip_abduction":{"name":"Cable Hip Abduction","components":["muscular_endurance","balance"]},"side_lying_leg_raise_top_leg":{"name":"Side-Lying Leg Raise (top leg)","components":["muscular_endurance"]},"abductor_machine":{"name":"Abductor Machine","components":["muscular_endurance"]},"abductor_machine_lean_forward":{"name":"Abductor Machine (Lean-Forward)","components":["muscular_endurance"]},"abductor_machine_single_leg":{"name":"Abductor Machine (Single-Leg)","components":["muscular_endurance","balance"]},"standing_cable_abduction":{"name":"Standing Cable Abduction","components":["muscular_endurance","balance"]},"cable_abduction_45_degree_lean":{"name":"Cable Abduction (45-Degree Lean)","components":["muscular_endurance","balance"]},"side_lying_hip_abduction_straight_leg":{"name":"Side-Lying Hip Abduction (Straight Leg)","components":["muscular_endurance"]},"side_lying_hip_abduction_clam_combo":{"name":"Side-Lying Hip Abduction (Clam Combo)","components":["muscular_endurance"]},"banded_lateral_walk_heavy":{"name":"Banded Lateral Walk (Heavy)","components":["muscular_endurance"]},"monster_walk_circle":{"name":"Monster Walk (Circle)","components":["muscular_endurance","coordination","balance"]},"standing_band_abduction_anchor_post":{"name":"Standing Band Abduction (Anchor Post)","components":["muscular_endurance","balance"]},"quadruped_hip_abduction_banded":{"name":"Quadruped Hip Abduction (Banded)","components":["muscular_endurance","coordination"]},"fire_hydrant_circles":{"name":"Fire Hydrant Circles","components":["muscular_endurance","coordination"]},"standing_fire_hydrant":{"name":"Standing Fire Hydrant","components":["muscular_endurance","balance"]},"single_leg_glute_bridge_abduction_finish":{"name":"Single-Leg Glute Bridge (Abduction Finish)","components":["muscular_endurance","balance"]},"bulgarian_lateral_lunge":{"name":"Bulgarian Lateral Lunge","components":["muscular_strength","flexibility"]},"skater_step_through":{"name":"Skater Step-Through","components":["muscular_strength","coordination","power"]},"curtsy_lunge_banded":{"name":"Curtsy Lunge (Banded)","components":["muscular_strength","muscular_endurance"]},"side_plank_with_abduction":{"name":"Side Plank with Abduction","components":["muscular_endurance","balance"]},"single_leg_balance_reach_lateral":{"name":"Single-Leg Balance Reach (Lateral)","components":["balance","muscular_endurance","coordination"]},"wall_single_leg_glute_med_iso":{"name":"Wall Single-Leg Glute Med Iso","components":["muscular_endurance","balance"]},"step_down_lateral":{"name":"Step-Down (Lateral)","components":["muscular_strength","muscular_endurance","balance"]},"step_down_frontal_loaded":{"name":"Step-Down (Frontal, Loaded)","components":["muscular_strength","muscular_endurance","balance"]},"prone_hip_abduction":{"name":"Prone Hip Abduction","components":["muscular_endurance"]},"band_seated_abduction":{"name":"Band Seated Abduction","components":["muscular_endurance"]},"seated_banded_abduction_iso_hold":{"name":"Seated Banded Abduction Iso Hold","components":["muscular_endurance"]},"speed_skater_hold":{"name":"Speed Skater Hold","components":["balance","muscular_endurance"]},"lateral_box_shuffle":{"name":"Lateral Box Shuffle","components":["agility","speed","muscular_endurance"]},"standing_hip_airplane":{"name":"Standing Hip Airplane","components":["balance","coordination","muscular_endurance"]},"neck_isometric_manual":{"name":"Neck Isometric (manual)","components":["muscular_endurance"]},"neck_harness_flexion":{"name":"Neck Harness Flexion","components":["muscular_endurance"]},"neck_curl":{"name":"Neck Curl","components":["muscular_endurance"]},"neck_extension":{"name":"Neck Extension","components":["muscular_endurance"]},"band_neck_flexion":{"name":"Band Neck Flexion","components":["muscular_endurance"]},"neck_flexion_plate_on_forehead":{"name":"Neck Flexion (Plate on Forehead)","components":["muscular_endurance"]},"neck_extension_plate_on_head":{"name":"Neck Extension (Plate on Head)","components":["muscular_endurance"]},"neck_lateral_flexion_side_lying":{"name":"Neck Lateral Flexion (Side-Lying)","components":["muscular_endurance"]},"neck_rotation_side_lying":{"name":"Neck Rotation (Side-Lying)","components":["muscular_endurance"]},"isometric_neck_flexion_hand_resist":{"name":"Isometric Neck Flexion (Hand Resist)","components":["muscular_endurance"]},"isometric_neck_extension_hand_resist":{"name":"Isometric Neck Extension (Hand Resist)","components":["muscular_endurance"]},"isometric_neck_side_resist":{"name":"Isometric Neck Side Resist","components":["muscular_endurance"]},"harness_neck_extension_weighted":{"name":"Harness Neck Extension (Weighted)","components":["muscular_endurance"]},"harness_neck_flexion_weighted":{"name":"Harness Neck Flexion (Weighted)","components":["muscular_endurance"]},"band_neck_flexion_forehead_anchor":{"name":"Band Neck Flexion (Forehead Anchor)","components":["muscular_endurance"]},"band_neck_extension":{"name":"Band Neck Extension","components":["muscular_endurance"]},"band_neck_lateral_raise":{"name":"Band Neck Lateral Raise","components":["muscular_endurance"]},"chin_tuck_deep_neck_flexor_activation":{"name":"Chin Tuck (Deep Neck Flexor Activation)","components":["muscular_endurance"]},"chin_tuck_hold":{"name":"Chin Tuck Hold","components":["muscular_endurance"]},"chin_tuck_against_wall":{"name":"Chin Tuck Against Wall","components":["muscular_endurance"]},"prone_chin_retraction_with_lift":{"name":"Prone Chin Retraction with Lift","components":["muscular_endurance"]},"shrug_with_neck_retraction":{"name":"Shrug with Neck Retraction","components":["muscular_endurance"]},"neck_bridge_front_partial":{"name":"Neck Bridge (Front, Partial)","components":["muscular_strength","muscular_endurance"]},"short_foot":{"name":"Short Foot","components":["muscular_endurance"]},"toe_yoga":{"name":"Toe Yoga","components":["muscular_endurance","coordination"]},"towel_curls":{"name":"Towel Curls","components":["muscular_endurance"]},"marble_pickup":{"name":"Marble Pickup","components":["muscular_endurance","coordination"]},"banded_ankle_eversion":{"name":"Banded Ankle Eversion","components":["muscular_endurance"]},"banded_ankle_inversion":{"name":"Banded Ankle Inversion","components":["muscular_endurance"]},"toe_spread_and_splay":{"name":"Toe Spread and Splay","components":["muscular_endurance","coordination"]},"short_foot_exercise":{"name":"Short-Foot Exercise","components":["muscular_endurance"]},"short_foot_single_leg_balance":{"name":"Short-Foot Single-Leg Balance","components":["muscular_endurance","balance"]},"towel_scrunch_seated":{"name":"Towel Scrunch (Seated)","components":["muscular_endurance"]},"towel_scrunch_standing":{"name":"Towel Scrunch (Standing)","components":["muscular_endurance"]},"marble_pickups":{"name":"Marble Pickups","components":["muscular_endurance","coordination"]},"toe_yoga_big_toe_isolation":{"name":"Toe Yoga (Big Toe Isolation)","components":["muscular_endurance","coordination"]},"toe_yoga_lesser_toes":{"name":"Toe Yoga (Lesser Toes)","components":["muscular_endurance","coordination"]},"heel_walk":{"name":"Heel Walk","components":["muscular_endurance","balance"]},"toe_walk":{"name":"Toe Walk","components":["muscular_endurance","balance"]},"outside_edge_walk":{"name":"Outside-Edge Walk","components":["muscular_endurance","balance"]},"inside_edge_walk":{"name":"Inside-Edge Walk","components":["muscular_endurance","balance"]},"single_leg_balance_barefoot":{"name":"Single-Leg Balance (Barefoot)","components":["balance","muscular_endurance"]},"single_leg_balance_foam_pad":{"name":"Single-Leg Balance (Foam Pad)","components":["balance","muscular_endurance"]},"single_leg_balance_eyes_closed":{"name":"Single-Leg Balance (Eyes Closed)","components":["balance","muscular_endurance"]},"ankle_circles_controlled":{"name":"Ankle Circles (Controlled)","components":["flexibility","muscular_endurance"]},"ankle_alphabet":{"name":"Ankle Alphabet","components":["muscular_endurance","coordination"]},"banded_ankle_dorsiflexion":{"name":"Banded Ankle Dorsiflexion","components":["muscular_endurance"]},"knee_to_wall_ankle_rock":{"name":"Knee-to-Wall Ankle Rock","components":["flexibility","muscular_endurance"]},"calf_stretch_wall_lean_loaded":{"name":"Calf Stretch (Wall Lean, Loaded)","components":["flexibility","muscular_endurance"]},"soleus_wall_sit_stretch":{"name":"Soleus Wall Sit Stretch","components":["flexibility","muscular_endurance"]},"toe_raise_walk_dorsiflexion_march":{"name":"Toe Raise Walk (Dorsiflexion March)","components":["muscular_endurance","balance"]},"hop_matrix_ankle_stiffness":{"name":"Hop Matrix (Ankle Stiffness)","components":["power","agility","muscular_endurance"]},"barefoot_squat_hold":{"name":"Barefoot Squat Hold","components":["flexibility","muscular_endurance","balance"]},"domed_arch_calf_raise":{"name":"Domed-Arch Calf Raise","components":["muscular_endurance","balance"]},"bosu_or_balance_pad_squat_hold":{"name":"Bosu or Balance Pad Squat Hold","components":["balance","muscular_endurance"]},"grippers_captains_of_crush":{"name":"Grippers (Captains of Crush)","components":["muscular_endurance"]},"finger_extension_with_band":{"name":"Finger Extension with Band","components":["muscular_endurance"]},"finger_adduction_with_band":{"name":"Finger Adduction with Band","components":["muscular_endurance"]},"bar_hang_full_grip":{"name":"Bar Hang (Full Grip)","components":["muscular_endurance","flexibility"]},"thick_bar_hang":{"name":"Thick-Bar Hang","components":["muscular_endurance"]},"towel_hang_single_arm":{"name":"Towel Hang (Single Arm)","components":["muscular_endurance","balance"]},"pinch_block_hold":{"name":"Pinch Block Hold","components":["muscular_endurance"]},"plate_pinch_two_plates":{"name":"Plate Pinch (Two Plates)","components":["muscular_endurance"]},"hub_lift_plate_hub":{"name":"Hub Lift (Plate Hub)","components":["muscular_endurance"]},"dead_hang_to_active_scap_pull":{"name":"Dead Hang to Active Scap Pull","components":["muscular_endurance","coordination"]},"gripper_close_heavy":{"name":"Gripper Close (Heavy)","components":["muscular_endurance"]},"gripper_hold_closed":{"name":"Gripper Hold (Closed)","components":["muscular_endurance"]},"gripper_negative_opens":{"name":"Gripper Negative Opens","components":["muscular_endurance"]},"rice_bucket_squeeze_and_dig":{"name":"Rice Bucket Squeeze and Dig","components":["muscular_endurance","cardiorespiratory_endurance"]},"sand_bucket_crush":{"name":"Sand Bucket Crush","components":["muscular_endurance"]},"wrist_roller_extension_roll":{"name":"Wrist Roller Extension Roll","components":["muscular_strength","muscular_endurance"]},"fingertip_dead_hang":{"name":"Fingertip Dead Hang","components":["muscular_endurance"]},"fingertip_hang_two_hands_open_palm":{"name":"Fingertip Hang (Two Hands, Open Palm)","components":["muscular_endurance"]},"single_finger_assisted_hang_advanced":{"name":"Single-Finger Assisted Hang (Advanced)","components":["muscular_endurance"]},"finger_extension_rubber_band":{"name":"Finger Extension (Rubber Band)","components":["muscular_endurance"]},"finger_extension_band_single_finger_bias":{"name":"Finger Extension (Band, Single Finger Bias)","components":["muscular_endurance"]},"book_pinch_walk":{"name":"Book Pinch Walk","components":["muscular_endurance","coordination"]},"hub_style_beanbag_transfer":{"name":"Hub-style Beanbag Transfer","components":["muscular_endurance","coordination"]},"deadlift_hold_fat_bar":{"name":"Deadlift Hold (Fat Bar)","components":["muscular_strength","muscular_endurance"]},"barbell_lever_leverage_wrist_curl":{"name":"Barbell Lever (Leverage Wrist Curl)","components":["muscular_endurance"]},"barbell_lever_reverse":{"name":"Barbell Lever (Reverse)","components":["muscular_endurance"]},"hand_open_close_pump_fast":{"name":"Hand Open-Close Pump (Fast)","components":["muscular_endurance","cardiorespiratory_endurance"]},"finger_walk_wall_or_table":{"name":"Finger Walk (Wall or Table)","components":["muscular_endurance","coordination"]},"sledgehammer_levering_front_raise":{"name":"Sledgehammer Levering (Front Raise)","components":["muscular_strength","muscular_endurance"]},"sledgehammer_levering_rotations":{"name":"Sledgehammer Levering (Rotations)","components":["muscular_endurance","coordination"]},"external_rotation_cable":{"name":"External Rotation (cable)","components":["muscular_endurance","coordination"]},"external_rotation_dumbbell":{"name":"External Rotation (dumbbell)","components":["muscular_endurance","coordination"]},"internal_rotation_cable":{"name":"Internal Rotation (cable)","components":["muscular_endurance","coordination"]},"dumbbell_supination":{"name":"Dumbbell Supination","components":["muscular_endurance","coordination"]},"dumbbell_pronation":{"name":"Dumbbell Pronation","components":["muscular_endurance","coordination"]},"reverse_curl":{"name":"Reverse Curl","components":["muscular_endurance"]},"tibialis_raise_band":{"name":"Tibialis Raise (band)","components":["muscular_endurance"]},"tibialis_raise_dumbbell":{"name":"Tibialis Raise (dumbbell)","components":["muscular_endurance"]},"banded_psoas_march":{"name":"Banded Psoas March","components":["muscular_endurance"]},"back_extension":{"name":"Back Extension","components":["muscular_endurance","muscular_strength"]},"doorway_chest_stretch":{"name":"Doorway Chest Stretch","components":["flexibility"]},"lying_hamstring_stretch_strap":{"name":"Lying Hamstring Stretch (strap)","components":["flexibility"]},"figure_four_piriformis_stretch":{"name":"Figure-Four Piriformis Stretch","components":["flexibility"]},"couch_stretch_quad_and_hip_flexor":{"name":"Couch Stretch (Quad and Hip Flexor)","components":["flexibility"]},"soleus_wall_stretch_bent_knee":{"name":"Soleus Wall Stretch (bent knee)","components":["flexibility"]},"gastrocnemius_wall_stretch_straight_leg":{"name":"Gastrocnemius Wall Stretch (straight leg)","components":["flexibility"]},"childs_pose_lats_and_back":{"name":"Childs Pose (Lats and Back)","components":["flexibility"]},"supine_twist":{"name":"Supine Twist","components":["flexibility"]},"doorway_lat_stretch":{"name":"Doorway Lat Stretch","components":["flexibility"]},"cross_body_shoulder_stretch":{"name":"Cross-Body Shoulder Stretch","components":["flexibility"]},"overhead_triceps_stretch":{"name":"Overhead Triceps Stretch","components":["flexibility"]},"neck_lateral_flexion_stretch":{"name":"Neck Lateral Flexion Stretch","components":["flexibility"]},"neck_extensor_stretch_chin_tuck":{"name":"Neck Extensor Stretch (chin tuck)","components":["flexibility"]},"butterfly_groin_stretch":{"name":"Butterfly Groin Stretch","components":["flexibility"]},"seated_forward_fold":{"name":"Seated Forward Fold","components":["flexibility"]},"wrist_flexor_and_extensor_stretch":{"name":"Wrist Flexor and Extensor Stretch","components":["flexibility"]},"deep_squat_hold":{"name":"Deep Squat Hold","components":["flexibility"]},"thoracic_open_book_rotation":{"name":"Thoracic Open Book Rotation","components":["flexibility"]},"hip_90_90_stretch":{"name":"Hip 90/90 Stretch","components":["flexibility"]},"kneeling_tibialis_stretch":{"name":"Kneeling Tibialis Stretch","components":["flexibility"]},"standing_hamstring_chair_stretch":{"name":"Standing Hamstring Chair Stretch","components":["flexibility"]},"standing_forward_fold":{"name":"Standing Forward Fold","components":["flexibility"]},"half_split_stretch":{"name":"Half Split Stretch","components":["flexibility"]},"wide_leg_forward_fold":{"name":"Wide-Leg Forward Fold","components":["flexibility"]},"standing_quad_stretch_wall_assist":{"name":"Standing Quad Stretch (Wall Assist)","components":["flexibility"]},"kneeling_quad_stretch_upright":{"name":"Kneeling Quad Stretch (Upright)","components":["flexibility"]},"lunge_hip_flexor_stretch":{"name":"Lunge Hip Flexor Stretch","components":["flexibility"]},"lunge_hip_flexor_with_reach":{"name":"Lunge Hip Flexor with Reach","components":["flexibility"]},"standing_psoas_march_stretch":{"name":"Standing Psoas March Stretch","components":["flexibility"]},"lizard_stretch_low":{"name":"Lizard Stretch (Low)","components":["flexibility"]},"lizard_stretch_forearms_down":{"name":"Lizard Stretch (Forearms Down)","components":["flexibility"]},"runner_s_lunge_opener":{"name":"Runner\u0027s Lunge Opener","components":["flexibility"]},"frog_stretch_prone":{"name":"Frog Stretch (Prone)","components":["flexibility"]},"pigeon_stretch_sleeping":{"name":"Pigeon Stretch (Sleeping)","components":["flexibility"]},"reclined_pigeon_figure_4_supine":{"name":"Reclined Pigeon (Figure-4 Supine)","components":["flexibility"]},"seated_glute_stretch_cross_leg":{"name":"Seated Glute Stretch (Cross-Leg)","components":["flexibility"]},"seated_it_band_twist":{"name":"Seated IT Band Twist","components":["flexibility"]},"supine_it_band_cross":{"name":"Supine IT Band Cross","components":["flexibility"]},"standing_tfl_stretch":{"name":"Standing TFL Stretch","components":["flexibility"]},"side_lying_quad_stretch":{"name":"Side-Lying Quad Stretch","components":["flexibility"]},"assisted_quad_stretch_side_lying_band":{"name":"Assisted Quad Stretch (Side-Lying, Band)","components":["flexibility"]},"bulgarian_split_stretch":{"name":"Bulgarian Split Stretch","components":["flexibility"]},"standing_calf_stretch_at_wall":{"name":"Standing Calf Stretch at Wall","components":["flexibility"]},"standing_soleus_stretch_at_wall":{"name":"Standing Soleus Stretch at Wall","components":["flexibility"]},"downward_dog_to_calf_stretch":{"name":"Downward Dog to Calf Stretch","components":["flexibility"]},"standing_adductor_wall_stretch":{"name":"Standing Adductor Wall Stretch","components":["flexibility"]},"side_lunge_adductor_hold":{"name":"Side Lunge Adductor Hold","components":["flexibility"]},"kneeling_adductor_stretch_prayer":{"name":"Kneeling Adductor Stretch (Prayer)","components":["flexibility"]},"supine_hamstring_strap_stretch":{"name":"Supine Hamstring Strap Stretch","components":["flexibility"]},"supine_hamstring_strap_rotated_leg":{"name":"Supine Hamstring Strap (Rotated Leg)","components":["flexibility"]},"supine_glute_rotator_stretch_90_90":{"name":"Supine Glute-Rotator Stretch (90/90)","components":["flexibility"]},"standing_chest_doorway_stretch_low":{"name":"Standing Chest Doorway Stretch (Low)","components":["flexibility"]},"standing_chest_doorway_stretch_high":{"name":"Standing Chest Doorway Stretch (High)","components":["flexibility"]},"floor_angel_chest_opener":{"name":"Floor Angel Chest Opener","components":["flexibility"]},"foam_roller_thoracic_extension":{"name":"Foam Roller Thoracic Extension","components":["flexibility"]},"supported_fish_pose":{"name":"Supported Fish Pose","components":["flexibility"]},"eagle_arms_shoulder_stretch":{"name":"Eagle Arms Shoulder Stretch","components":["flexibility"]},"crossed_arm_lat_stretch":{"name":"Crossed-Arm Lat Stretch","components":["flexibility"]},"kneeling_lat_reach_stretch":{"name":"Kneeling Lat Reach Stretch","components":["flexibility"]},"side_lying_lat_windmill":{"name":"Side-Lying Lat Windmill","components":["flexibility"]},"child_s_pose_with_side_reach":{"name":"Child\u0027s Pose with Side Reach","components":["flexibility"]},"puppy_pose_anahatasana":{"name":"Puppy Pose (Anahatasana)","components":["flexibility"]},"prone_press_up_cobra_extension":{"name":"Prone Press-Up (Cobra Extension)","components":["flexibility"]},"sphinx_pose_hold":{"name":"Sphinx Pose Hold","components":["flexibility"]},"supine_spinal_twist_both_knees":{"name":"Supine Spinal Twist (Both Knees)","components":["flexibility"]},"seated_spinal_twist_long_sit":{"name":"Seated Spinal Twist (Long Sit)","components":["flexibility"]},"thoracic_extension_over_chair":{"name":"Thoracic Extension Over Chair","components":["flexibility"]},"standing_side_bend_reach":{"name":"Standing Side-Bend Reach","components":["flexibility"]},"triceps_wall_stretch_high_anchor":{"name":"Triceps Wall Stretch (High Anchor)","components":["flexibility"]},"forearm_prayers_stretch":{"name":"Forearm Prayers Stretch","components":["flexibility"]},"reverse_forearm_prayers":{"name":"Reverse Forearm Prayers","components":["flexibility"]},"wrist_flexor_stretch_arm_long":{"name":"Wrist Flexor Stretch (Arm Long)","components":["flexibility"]},"wrist_extensor_stretch_arm_long":{"name":"Wrist Extensor Stretch (Arm Long)","components":["flexibility"]},"neck_upper_trap_stretch_seated":{"name":"Neck Upper Trap Stretch (Seated)","components":["flexibility"]},"levator_scap_stretch_chin_down":{"name":"Levator Scap Stretch (Chin Down)","components":["flexibility"]},"scalene_stretch_chin_level":{"name":"Scalene Stretch (Chin Level)","components":["flexibility"]},"suboccipital_release_nod":{"name":"Suboccipital Release Nod","components":["flexibility"]},"ankle_dorsiflexion_wall_stretch":{"name":"Ankle Dorsiflexion Wall Stretch","components":["flexibility"]},"toe_extension_stretch_plantar":{"name":"Toe Extension Stretch (Plantar)","components":["flexibility"]},"plantar_fascia_ball_roll_out":{"name":"Plantar Fascia Ball Roll-Out","components":["flexibility"]},"seated_butterfly_hold_long":{"name":"Seated Butterfly Hold (Long)","components":["flexibility"]},"reclined_butterfly":{"name":"Reclined Butterfly","components":["flexibility"]},"happy_baby_pose":{"name":"Happy Baby Pose","components":["flexibility"]},"standing_figure_four_hold":{"name":"Standing Figure-Four Hold","components":["flexibility"]},"half_kneeling_psoas_reach_back":{"name":"Half-Kneeling Psoas Reach-Back","components":["flexibility"]},"wall_pigeon_standing":{"name":"Wall Pigeon (Standing)","components":["flexibility"]},"deep_lunge_elbow_to_foot":{"name":"Deep Lunge Elbow-to-Foot","components":["flexibility"]},"seated_angel_fold_hamstring_back":{"name":"Seated Angel Fold (Hamstring + Back)","components":["flexibility"]},"reverse_tabletop_hip_opener":{"name":"Reverse Tabletop Hip Opener","components":["flexibility"]},"leg_swings_front_to_back":{"name":"Leg Swings (Front to Back)","components":["flexibility","coordination"]},"leg_swings_lateral":{"name":"Leg Swings (Lateral)","components":["flexibility","coordination"]},"arm_circles":{"name":"Arm Circles","components":["flexibility","coordination"]},"hip_circles":{"name":"Hip Circles","components":["flexibility","coordination"]},"worlds_greatest_stretch":{"name":"Worlds Greatest Stretch","components":["flexibility","coordination"]},"walking_knee_hug":{"name":"Walking Knee Hug","components":["flexibility","coordination"]},"ankle_rocks":{"name":"Ankle Rocks","components":["flexibility","coordination"]},"torso_twists":{"name":"Torso Twists","components":["flexibility","coordination"]},"hip_90_90_switch":{"name":"Hip 90/90 Switch","components":["flexibility","coordination"]},"inchworm_walkout":{"name":"Inchworm Walkout","components":["flexibility","coordination"]},"scapular_push_up":{"name":"Scapular Push-Up","components":["flexibility","coordination"]},"shoulder_band_dislocates":{"name":"Shoulder Band Dislocates","components":["flexibility","coordination"]},"world_s_greatest_stretch_with_reach":{"name":"World\u0027s Greatest Stretch with Reach","components":["flexibility","coordination"]},"inchworm_to_push_up_walkout":{"name":"Inchworm to Push-Up Walkout","components":["flexibility","muscular_endurance"]},"lunge_with_rotation_reach":{"name":"Lunge with Rotation Reach","components":["flexibility","coordination"]},"reverse_lunge_with_overhead_reach":{"name":"Reverse Lunge with Overhead Reach","components":["flexibility","coordination"]},"lateral_lunge_with_shin_touch":{"name":"Lateral Lunge with Shin Touch","components":["flexibility","coordination"]},"cossack_shift_flow":{"name":"Cossack Shift (Flow)","components":["flexibility","coordination"]},"90_90_hip_flow":{"name":"90/90 Hip Flow","components":["flexibility","coordination"]},"hip_airplane_slow":{"name":"Hip Airplane (Slow)","components":["balance","flexibility","muscular_endurance"]},"quadruped_hip_circles":{"name":"Quadruped Hip Circles","components":["flexibility","coordination"]},"quadruped_thoracic_rotations":{"name":"Quadruped Thoracic Rotations","components":["flexibility","coordination"]},"thread_the_needle_flow":{"name":"Thread the Needle Flow","components":["flexibility","coordination"]},"cat_cow_flow":{"name":"Cat-Cow Flow","components":["flexibility"]},"bird_dog_flow_dynamic":{"name":"Bird Dog Flow (Dynamic)","components":["muscular_endurance","coordination"]},"down_dog_to_cobra_flow":{"name":"Down Dog to Cobra Flow","components":["flexibility"]},"down_dog_shoulder_taps":{"name":"Down Dog Shoulder Taps","components":["muscular_endurance","coordination"]},"scapular_pull_up_flow_hang":{"name":"Scapular Pull-Up Flow (Hang)","components":["muscular_endurance","coordination"]},"wall_slide_to_y_raise":{"name":"Wall Slide to Y-Raise","components":["muscular_endurance","coordination"]},"floor_slides_supine":{"name":"Floor Slides (Supine)","components":["muscular_endurance","coordination"]},"standing_shoulder_rolls_full":{"name":"Standing Shoulder Rolls (Full)","components":["flexibility"]},"band_pull_apart_flow_overhead_arc":{"name":"Band Pull-Apart Flow (Overhead Arc)","components":["muscular_endurance","coordination"]},"wall_angels_dynamic":{"name":"Wall Angels (Dynamic)","components":["flexibility","muscular_endurance"]},"standing_torso_rotations_arms_swing":{"name":"Standing Torso Rotations (Arms Swing)","components":["flexibility"]},"standing_hip_cars":{"name":"Standing Hip CARs","components":["flexibility","coordination"]},"standing_shoulder_cars":{"name":"Standing Shoulder CARs","components":["flexibility","coordination"]},"ankle_cars":{"name":"Ankle CARs","components":["flexibility","coordination"]},"wrist_cars":{"name":"Wrist CARs","components":["flexibility","coordination"]},"neck_half_circles_chin_tuck_flow":{"name":"Neck Half-Circles (Chin Tuck Flow)","components":["flexibility"]},"leg_swings_with_hold_return":{"name":"Leg Swings with Hold-Return","components":["flexibility","muscular_endurance"]},"frankenstein_walk":{"name":"Frankenstein Walk","components":["flexibility","coordination"]},"walking_spiderman_with_hip_lift":{"name":"Walking Spiderman with Hip Lift","components":["flexibility","coordination"]},"bear_crawl_shoulder_taps":{"name":"Bear Crawl Shoulder Taps","components":["muscular_endurance","coordination","balance"]},"bear_crawl_forward_and_back":{"name":"Bear Crawl (Forward and Back)","components":["muscular_endurance","coordination"]},"crab_reach":{"name":"Crab Reach","components":["flexibility","coordination"]},"crab_walk_forward":{"name":"Crab Walk (Forward)","components":["muscular_endurance","coordination"]},"ape_reach_to_step":{"name":"Ape Reach-to-Step","components":["flexibility","coordination","muscular_endurance"]},"deep_squat_rocks":{"name":"Deep Squat Rocks","components":["flexibility"]},"deep_squat_shifts":{"name":"Deep Squat Shifts","components":["flexibility","coordination"]},"hip_flexor_floss_lunge_rock":{"name":"Hip Flexor Floss (Lunge Rock)","components":["flexibility"]},"pigeon_rocks_dynamic":{"name":"Pigeon Rocks (Dynamic)","components":["flexibility"]},"standing_windmill_toe_touch":{"name":"Standing Windmill Toe Touch","components":["flexibility","coordination"]},"dynamic_prayer_stretch_to_reach":{"name":"Dynamic Prayer Stretch to Reach","components":["flexibility"]},"standing_pelvic_tilts":{"name":"Standing Pelvic Tilts","components":["flexibility"]},"heel_to_toe_rock_ankle_flow":{"name":"Heel-to-Toe Rock (Ankle Flow)","components":["flexibility","muscular_endurance"]},"knee_circles_standing":{"name":"Knee Circles (Standing)","components":["flexibility"]},"arm_bar_roll_light_db":{"name":"Arm Bar Roll (Light DB)","components":["flexibility","coordination"]},"box_jump":{"name":"Box Jump","components":["power","speed"]},"standing_broad_jump":{"name":"Standing Broad Jump","components":["power","speed"]},"medicine_ball_chest_pass":{"name":"Medicine Ball Chest Pass","components":["power","speed"]},"rotational_med_ball_throw":{"name":"Rotational Med Ball Throw","components":["power","speed"]},"power_skip_for_height":{"name":"Power Skip for Height","components":["power","speed"]},"depth_jump_to_stick":{"name":"Depth Jump to Stick","components":["power","speed"]},"box_jump_max_height":{"name":"Box Jump (Max Height)","components":["power","speed"]},"box_jump_depth_to_box":{"name":"Box Jump (Depth to Box)","components":["power","speed"]},"box_jump_single_leg_landing":{"name":"Box Jump (Single-Leg Landing)","components":["power","balance"]},"seated_box_jump":{"name":"Seated Box Jump","components":["power"]},"standing_vertical_jump_max":{"name":"Standing Vertical Jump (Max)","components":["power","speed"]},"depth_jump":{"name":"Depth Jump","components":["power","speed"]},"depth_jump_to_broad_jump":{"name":"Depth Jump to Broad Jump","components":["power","speed"]},"concentric_only_squat_jump_pin":{"name":"Concentric-Only Squat Jump (Pin)","components":["power","muscular_strength"]},"trap_bar_jump_squat":{"name":"Trap Bar Jump Squat","components":["power","muscular_strength"]},"dumbbell_jump_squat":{"name":"Dumbbell Jump Squat","components":["power"]},"kettlebell_jump_swing":{"name":"Kettlebell Jump Swing","components":["power"]},"continuous_broad_jumps":{"name":"Continuous Broad Jumps","components":["power","speed"]},"bounding_for_distance":{"name":"Bounding for Distance","components":["power","speed"]},"single_leg_hops_for_distance":{"name":"Single-Leg Hops for Distance","components":["power","balance","speed"]},"alternate_leg_bounding":{"name":"Alternate-Leg Bounding","components":["power","speed"]},"hurdle_hops_continuous":{"name":"Hurdle Hops (Continuous)","components":["power","agility"]},"lateral_hurdle_hops":{"name":"Lateral Hurdle Hops","components":["power","agility"]},"single_leg_lateral_hops":{"name":"Single-Leg Lateral Hops","components":["power","balance","agility"]},"medicine_ball_rotational_throw_max":{"name":"Medicine Ball Rotational Throw (Max)","components":["power","muscular_endurance"]},"medicine_ball_overhead_throw_soccer":{"name":"Medicine Ball Overhead Throw (Soccer)","components":["power","muscular_endurance"]},"medicine_ball_slam_max":{"name":"Medicine Ball Slam (Max)","components":["power","muscular_endurance"]},"medicine_ball_push_pass_chest":{"name":"Medicine Ball Push Pass (Chest)","components":["power","muscular_endurance"]},"medicine_ball_punch_throw_split_stance":{"name":"Medicine Ball Punch Throw (Split Stance)","components":["power","coordination"]},"medicine_ball_overhead_rear_toss":{"name":"Medicine Ball Overhead Rear Toss","components":["power","muscular_endurance"]},"medicine_ball_granny_toss":{"name":"Medicine Ball Granny Toss","components":["power","muscular_endurance"]},"plyo_push_up_max_height":{"name":"Plyo Push-Up (Max Height)","components":["power","muscular_strength"]},"explosive_chin_up_chest_to_bar_fast":{"name":"Explosive Chin-Up (Chest to Bar Fast)","components":["power","muscular_strength"]},"push_press_heavy_max_speed":{"name":"Push Press (Heavy, Max Speed)","components":["power","muscular_strength"]},"trap_bar_speed_deadlift":{"name":"Trap Bar Speed Deadlift","components":["power","muscular_strength"]},"split_jerk_practice":{"name":"Split Jerk (Practice)","components":["power","coordination","muscular_strength"]},"sprint_intervals_40_m":{"name":"Sprint Intervals (40 m)","components":["speed","cardiorespiratory_endurance"]},"hill_sprints":{"name":"Hill Sprints","components":["speed","cardiorespiratory_endurance"]},"treadmill_sprint_intervals":{"name":"Treadmill Sprint Intervals","components":["speed","cardiorespiratory_endurance"]},"a_skip_drill":{"name":"A-Skip Drill","components":["speed","cardiorespiratory_endurance"]},"high_knees_drill":{"name":"High Knees Drill","components":["speed","cardiorespiratory_endurance"]},"butt_kicks_drill":{"name":"Butt Kicks Drill","components":["speed","cardiorespiratory_endurance"]},"10_yard_acceleration_starts":{"name":"10-Yard Acceleration Starts","components":["speed","power"]},"20_yard_fly_sprint":{"name":"20-Yard Fly Sprint","components":["speed"]},"40_yard_sprint_timed":{"name":"40-Yard Sprint (Timed)","components":["speed","cardiorespiratory_endurance"]},"flying_20s":{"name":"Flying 20s","components":["speed"]},"sprint_from_prone_start":{"name":"Sprint from Prone Start","components":["speed","power"]},"sprint_from_kneeling_start":{"name":"Sprint from Kneeling Start","components":["speed","power"]},"falling_acceleration_starts":{"name":"Falling Acceleration Starts","components":["speed","power"]},"resisted_sled_sprint_light":{"name":"Resisted Sled Sprint (Light)","components":["speed","power"]},"resisted_band_sprint":{"name":"Resisted Band Sprint","components":["speed","power"]},"assisted_sprint_overspeed_band":{"name":"Assisted Sprint (Overspeed Band)","components":["speed"]},"downhill_sprint_2_3_percent":{"name":"Downhill Sprint (2-3 Percent)","components":["speed"]},"sprint_float_sprint":{"name":"Sprint-Float-Sprint","components":["speed"]},"a_skip_fast_cadence":{"name":"A-Skip (Fast Cadence)","components":["speed","coordination"]},"b_skip_drill":{"name":"B-Skip Drill","components":["speed","coordination"]},"high_knees_speed_cadence":{"name":"High Knees (Speed Cadence)","components":["speed","cardiorespiratory_endurance"]},"butt_kicks_fast_cadence":{"name":"Butt Kicks (Fast Cadence)","components":["speed","cardiorespiratory_endurance"]},"straight_leg_bound_scissor":{"name":"Straight-Leg Bound Scissor","components":["speed","coordination"]},"ankle_dribbles_fast_feet":{"name":"Ankle Dribbles (Fast Feet)","components":["speed","coordination"]},"wall_drill_acceleration_2_count":{"name":"Wall Drill Acceleration (2-Count)","components":["speed","power"]},"wall_drill_acceleration_3_count":{"name":"Wall Drill Acceleration (3-Count)","components":["speed","power"]},"push_up_position_starts":{"name":"Push-Up Position Starts","components":["speed","power"]},"band_resisted_lateral_run":{"name":"Band-Resisted Lateral Run","components":["speed","agility"]},"sprint_with_deceleration_to_5_yard_backpedal":{"name":"Sprint with Deceleration to 5-Yard Backpedal","components":["speed","agility"]},"tempo_runs_70_percent":{"name":"Tempo Runs (70 Percent)","components":["speed","cardiorespiratory_endurance"]},"relay_style_turnaround_sprints":{"name":"Relay-Style Turnaround Sprints","components":["speed","agility"]},"shuttle_build_up_10_20_10":{"name":"Shuttle Build-Up (10-20-10)","components":["speed","agility"]},"sled_march_heavy_drive":{"name":"Sled March (Heavy Drive)","components":["muscular_strength","power"]},"stair_sprints":{"name":"Stair Sprints","components":["speed","cardiorespiratory_endurance","power"]},"track_curve_sprint":{"name":"Track Curve Sprint","components":["speed","coordination"]},"reaction_start_sprint_random_go":{"name":"Reaction Start Sprint (Random Go)","components":["speed","reaction_time"]},"agility_ladder_drill":{"name":"Agility Ladder Drill","components":["agility","speed"]},"5_10_5_pro_agility_shuttle":{"name":"5-10-5 Pro Agility Shuttle","components":["agility","speed"]},"lateral_shuffle_drill":{"name":"Lateral Shuffle Drill","components":["agility","speed"]},"cone_zig_zag_drill":{"name":"Cone Zig-Zag Drill","components":["agility","speed"]},"skater_hops":{"name":"Skater Hops","components":["agility","speed"]},"t_drill":{"name":"T-Drill","components":["agility","speed"]},"agility_ladder_two_foot_in_in":{"name":"Agility Ladder (Two-Foot In-In)","components":["agility","coordination","speed"]},"agility_ladder_lateral_in_in":{"name":"Agility Ladder (Lateral In-In)","components":["agility","coordination"]},"agility_ladder_icky_shuffle":{"name":"Agility Ladder (Icky Shuffle)","components":["agility","coordination"]},"agility_ladder_single_leg_hops":{"name":"Agility Ladder (Single-Leg Hops)","components":["agility","balance","power"]},"agility_ladder_high_knee_run_through":{"name":"Agility Ladder (High-Knee Run-Through)","components":["agility","speed"]},"5_10_5_shuttle_timed":{"name":"5-10-5 Shuttle (Timed)","components":["agility","speed","power"]},"3_cone_drill_l_drill":{"name":"3-Cone Drill (L-Drill)","components":["agility","speed"]},"t_drill_timed":{"name":"T-Drill (Timed)","components":["agility","speed"]},"box_drill_zig_zag_cuts":{"name":"Box-Drill Zig-Zag Cuts","components":["agility","coordination"]},"pro_agility_mirror_drill":{"name":"Pro-Agility Mirror Drill","components":["agility","reaction_time"]},"reactive_cone_chase":{"name":"Reactive Cone Chase","components":["agility","reaction_time"]},"lateral_shuffle_touches":{"name":"Lateral Shuffle Touches","components":["agility","muscular_endurance"]},"forward_backpedal_line_drill":{"name":"Forward-Backpedal Line Drill","components":["agility","coordination"]},"diagonal_backpedal_breaks":{"name":"Diagonal Backpedal Breaks","components":["agility","coordination"]},"carioca_fast_feet":{"name":"Carioca (Fast Feet)","components":["agility","coordination","speed"]},"skipping_carioca":{"name":"Skipping Carioca","components":["agility","coordination","power"]},"w_drill_cone_weave":{"name":"W-Drill (Cone Weave)","components":["agility","coordination"]},"m_drill_four_cone_sprint_pattern":{"name":"M-Drill (Four-Cone Sprint Pattern)","components":["agility","speed"]},"turn_and_sprint_cues_random":{"name":"Turn-and-Sprint Cues (Random)","components":["agility","reaction_time"]},"lateral_bound_and_stick_cone_to_cone":{"name":"Lateral Bound and Stick (Cone to Cone)","components":["agility","power","balance"]},"shuffle_sprint_backpedal_combo":{"name":"Shuffle-Sprint-Backpedal Combo","components":["agility","speed"]},"cone_touch_reaction_partner_call":{"name":"Cone Touch Reaction (Partner Call)","components":["agility","reaction_time"]},"jump_and_cut_45_degrees":{"name":"Jump-and-Cut 45 Degrees","components":["agility","power"]},"jump_and_cut_90_degrees":{"name":"Jump-and-Cut 90 Degrees","components":["agility","power"]},"jump_and_cut_180_degrees":{"name":"Jump-and-Cut 180 Degrees","components":["agility","power","coordination"]},"suicide_line_sprints_short":{"name":"Suicide Line Sprints (Short)","components":["agility","cardiorespiratory_endurance","speed"]},"figure_eight_cone_weave":{"name":"Figure-Eight Cone Weave","components":["agility","coordination"]},"chase_the_tail_partner_drill":{"name":"Chase-the-Tail Partner Drill","components":["agility","speed","reaction_time"]},"zig_zag_bound_cuts":{"name":"Zig-Zag Bound-Cuts","components":["agility","power"]},"four_corner_touch_sprint":{"name":"Four-Corner Touch Sprint","components":["agility","speed"]},"single_leg_balance_hold":{"name":"Single-Leg Balance Hold","components":["balance"]},"single_leg_stand_eyes_closed":{"name":"Single-Leg Stand Eyes Closed","components":["balance"]},"tandem_walk_heel_to_toe":{"name":"Tandem Walk (Heel to Toe)","components":["balance"]},"star_excursion_reach":{"name":"Star Excursion Reach","components":["balance"]},"single_leg_rdl_hold":{"name":"Single-Leg RDL Hold","components":["balance"]},"stability_pad_single_leg_stand":{"name":"Stability Pad Single-Leg Stand","components":["balance"]},"single_leg_balance_arms_crossed":{"name":"Single-Leg Balance (Arms Crossed)","components":["balance"]},"single_leg_balance_on_foam_pad":{"name":"Single-Leg Balance on Foam Pad","components":["balance"]},"single_leg_balance_with_head_turns":{"name":"Single-Leg Balance with Head Turns","components":["balance","coordination"]},"single_leg_balance_with_arm_reach_matrix":{"name":"Single-Leg Balance with Arm Reach Matrix","components":["balance","coordination"]},"single_leg_balance_with_leg_reach_clock":{"name":"Single-Leg Balance with Leg Reach (Clock)","components":["balance","coordination","muscular_endurance"]},"tandem_stance_hold_eyes_closed":{"name":"Tandem Stance Hold (Eyes Closed)","components":["balance"]},"tandem_walk_on_line_eyes_open":{"name":"Tandem Walk on Line (Eyes Open)","components":["balance","coordination"]},"tandem_walk_on_line_eyes_closed":{"name":"Tandem Walk on Line (Eyes Closed)","components":["balance"]},"single_leg_deadlift_unweighted_slow":{"name":"Single-Leg Deadlift (Unweighted, Slow)","components":["balance","muscular_endurance","coordination"]},"single_leg_rdl_with_reach_loaded_light":{"name":"Single-Leg RDL with Reach (Loaded Light)","components":["balance","muscular_endurance"]},"star_excursion_anterior_focus":{"name":"Star Excursion (Anterior Focus)","components":["balance","flexibility"]},"star_excursion_posterolateral":{"name":"Star Excursion (Posterolateral)","components":["balance","flexibility"]},"star_excursion_full_matrix":{"name":"Star Excursion (Full Matrix)","components":["balance","coordination"]},"single_leg_squat_to_box_balance_focus":{"name":"Single-Leg Squat to Box (Balance Focus)","components":["balance","muscular_strength","muscular_endurance"]},"skater_hold_single_leg_deep":{"name":"Skater Hold (Single-Leg, Deep)","components":["balance","muscular_endurance"]},"bosu_dome_squats_flat_side_up":{"name":"Bosu Dome Squats (Flat Side Up)","components":["balance","muscular_strength"]},"bosu_dome_single_leg_stand":{"name":"Bosu Dome Single-Leg Stand","components":["balance"]},"balance_pad_lunge":{"name":"Balance Pad Lunge","components":["balance","muscular_strength","muscular_endurance"]},"half_kneeling_bottoms_up_kettlebell_hold":{"name":"Half-Kneeling Bottoms-Up Kettlebell Hold","components":["balance","muscular_endurance"]},"standing_bottoms_up_kettlebell_hold":{"name":"Standing Bottoms-Up Kettlebell Hold","components":["balance","muscular_endurance"]},"heel_raise_balance_single_leg_eyes_closed":{"name":"Heel Raise Balance (Single-Leg, Eyes Closed)","components":["balance","muscular_endurance"]},"toe_walk_balance_beam_line":{"name":"Toe Walk Balance (Beam Line)","components":["balance","muscular_endurance"]},"lateral_line_hops_stick_landing":{"name":"Lateral Line Hops (Stick Landing)","components":["balance","power"]},"forward_line_hops_stick_landing":{"name":"Forward Line Hops (Stick Landing)","components":["balance","power"]},"single_leg_throw_and_catch_wall":{"name":"Single-Leg Throw-and-Catch (Wall)","components":["balance","coordination","reaction_time"]},"partner_nudge_balance_ankle_tags":{"name":"Partner Nudge Balance (Ankle Tags)","components":["balance","reaction_time"]},"single_leg_bridge_hold_balance_glute":{"name":"Single-Leg Bridge Hold (Balance + Glute)","components":["balance","muscular_endurance"]},"standing_band_chop_on_single_leg":{"name":"Standing Band Chop on Single Leg","components":["balance","coordination","muscular_endurance"]},"windmill_toe_touch_on_single_leg":{"name":"Windmill Toe Touch on Single Leg","components":["balance","flexibility","muscular_endurance"]},"wall_ball_toss_and_catch":{"name":"Wall Ball Toss and Catch","components":["coordination"]},"carioca_drill":{"name":"Carioca Drill","components":["coordination"]},"jump_rope_crossovers":{"name":"Jump Rope Crossovers","components":["coordination"]},"cross_crawl_march":{"name":"Cross-Crawl March","components":["coordination"]},"shadow_boxing_combos":{"name":"Shadow Boxing Combos","components":["coordination"]},"cross_crawl_supine_presses":{"name":"Cross-Crawl Supine Presses","components":["coordination","muscular_endurance"]},"cross_crawl_march_standing":{"name":"Cross-Crawl March (Standing)","components":["coordination","muscular_endurance"]},"contralateral_bird_dog_reach_and_hold":{"name":"Contralateral Bird Dog Reach-and-Hold","components":["coordination","muscular_endurance","balance"]},"jumping_jacks_crisp_tempo":{"name":"Jumping Jacks (Crisp Tempo)","components":["cardiorespiratory_endurance","coordination"]},"seal_jacks":{"name":"Seal Jacks","components":["cardiorespiratory_endurance","coordination"]},"plank_jacks":{"name":"Plank Jacks","components":["muscular_endurance","cardiorespiratory_endurance","coordination"]},"skater_bound_pattern_rhythmic":{"name":"Skater Bound Pattern (Rhythmic)","components":["coordination","power","agility"]},"alternating_lunge_pulse_combo":{"name":"Alternating Lunge Pulse Combo","components":["coordination","muscular_endurance"]},"squat_to_stand_to_reach_complex":{"name":"Squat-to-Stand-to-Reach Complex","components":["coordination","flexibility"]},"ladder_in_in_out_out_pattern":{"name":"Ladder In-In-Out-Out Pattern","components":["coordination","agility"]},"ladder_lateral_crossover_step":{"name":"Ladder Lateral Crossover Step","components":["coordination","agility"]},"ladder_two_in_two_out_forward_back":{"name":"Ladder Two-In-Two-Out Forward-Back","components":["coordination","agility"]},"ball_dribbling_alternating_hands":{"name":"Ball Dribbling (Alternating Hands)","components":["coordination","muscular_endurance"]},"ball_figure_eight_dribble":{"name":"Ball Figure-Eight Dribble","components":["coordination","muscular_endurance"]},"wall_ball_pass_behind_the_back":{"name":"Wall Ball Pass Behind the Back","components":["coordination","muscular_endurance"]},"wall_ball_figure_eight_pass":{"name":"Wall Ball Figure-Eight Pass","components":["coordination","muscular_endurance"]},"cupstack_speed_transfer":{"name":"Cupstack Speed Transfer","components":["coordination","muscular_endurance"]},"jump_rope_boxer_step":{"name":"Jump Rope (Boxer Step)","components":["cardiorespiratory_endurance","coordination"]},"jump_rope_high_knees":{"name":"Jump Rope (High Knees)","components":["cardiorespiratory_endurance","coordination"]},"jump_rope_single_leg_alternating":{"name":"Jump Rope (Single-Leg Alternating)","components":["cardiorespiratory_endurance","coordination","balance"]},"pogo_matrix_plus_and_x":{"name":"Pogo Matrix (Plus and X)","components":["coordination","power"]},"band_opposite_arm_leg_pull_apart":{"name":"Band Opposite Arm-Leg Pull-Apart","components":["coordination","muscular_endurance"]},"rhythmic_turkish_get_up_light":{"name":"Rhythmic Turkish Get-Up (Light)","components":["coordination","muscular_endurance","muscular_strength"]},"kettlebell_figure_eight_carry":{"name":"Kettlebell Figure-Eight Carry","components":["coordination","muscular_endurance"]},"crawl_pattern_switches_bear_crab":{"name":"Crawl Pattern Switches (Bear-Crab)","components":["coordination","muscular_endurance"]},"skipping_with_arm_circles":{"name":"Skipping with Arm Circles","components":["coordination","cardiorespiratory_endurance"]},"skipping_with_crossover_arms":{"name":"Skipping with Crossover Arms","components":["coordination","cardiorespiratory_endurance"]},"three_touch_cone_tap_sprint":{"name":"Three-Touch Cone Tap Sprint","components":["coordination","agility","speed"]},"reaction_ball_drop":{"name":"Reaction Ball Drop","components":["reaction_time","speed"]},"wall_toss_reaction_catch":{"name":"Wall Toss Reaction Catch","components":["reaction_time","speed"]},"falling_start_sprints":{"name":"Falling Start Sprints","components":["reaction_time","speed"]},"random_cue_shuttle_sprint":{"name":"Random-Cue Shuttle Sprint","components":["reaction_time","speed"]},"reaction_ball_drop_solo_wall":{"name":"Reaction Ball Drop (Solo Wall)","components":["reaction_time","agility"]},"reaction_ball_drop_partner":{"name":"Reaction Ball Drop (Partner)","components":["reaction_time","agility"]},"partner_ball_drop_catch_standing":{"name":"Partner Ball Drop Catch (Standing)","components":["reaction_time","coordination"]},"partner_ball_drop_sprinter_stance":{"name":"Partner Ball Drop (Sprinter Stance)","components":["reaction_time","speed"]},"wall_toss_reaction_single_hand":{"name":"Wall Toss Reaction (Single Hand)","components":["reaction_time","coordination"]},"wall_toss_reaction_alternating_hands":{"name":"Wall Toss Reaction (Alternating Hands)","components":["reaction_time","coordination"]},"wall_toss_with_squat_catch":{"name":"Wall Toss with Squat Catch","components":["reaction_time","coordination"]},"partner_mirror_footwork_random":{"name":"Partner Mirror Footwork (Random)","components":["reaction_time","agility","speed"]},"random_cue_directional_sprint":{"name":"Random-Cue Directional Sprint","components":["reaction_time","speed"]},"random_cue_shuttle_partner_call":{"name":"Random-Cue Shuttle (Partner Call)","components":["reaction_time","agility"]},"light_board_or_app_reaction_taps":{"name":"Light-Board or App Reaction Taps","components":["reaction_time","coordination"]},"color_call_catch_multi_ball":{"name":"Color-Call Catch (Multi-Ball)","components":["reaction_time","coordination"]},"partner_hand_slap_reaction_defense_game":{"name":"Partner Hand-Slap Reaction (Defense Game)","components":["reaction_time","coordination"]},"reaction_burpee_on_clap":{"name":"Reaction Burpee on Clap","components":["reaction_time","cardiorespiratory_endurance"]},"go_stop_sprint_control":{"name":"Go-Stop Sprint Control","components":["reaction_time","speed"]},"two_ball_alternating_wall_toss":{"name":"Two-Ball Alternating Wall Toss","components":["reaction_time","coordination"]},"reaction_ladder_partner_calls_pattern":{"name":"Reaction Ladder (Partner Calls Pattern)","components":["reaction_time","agility","coordination"]},"drop_step_reaction_cue_left_or_right":{"name":"Drop-Step Reaction (Cue Left or Right)","components":["reaction_time","agility"]},"reaction_plank_shoulder_tap_on_cue":{"name":"Reaction Plank Shoulder Tap on Cue","components":["reaction_time","muscular_endurance"]},"catching_from_seated_twist_toss":{"name":"Catching from Seated Twist Toss","components":["reaction_time","coordination"]},"reaction_broad_jump_on_cue":{"name":"Reaction Broad Jump on Cue","components":["reaction_time","power"]},"partner_shadow_catch_up_sprint":{"name":"Partner Shadow Catch-Up Sprint","components":["reaction_time","speed","agility"]},"ball_drop_from_behind_turn_and_catch":{"name":"Ball Drop from Behind (Turn and Catch)","components":["reaction_time","coordination"]},"reaction_star_drill_random_points":{"name":"Reaction Star Drill (Random Points)","components":["reaction_time","agility","balance"]},"stationary_bike_intervals":{"name":"Stationary Bike Intervals","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"rowing_machine_intervals":{"name":"Rowing Machine Intervals","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"incline_treadmill_walk":{"name":"Incline Treadmill Walk","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"stair_climber":{"name":"Stair Climber","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"shadow_boxing_rounds":{"name":"Shadow Boxing Rounds","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"high_knees_intervals":{"name":"High Knees Intervals","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"brisk_outdoor_walk_or_jog":{"name":"Brisk Outdoor Walk or Jog","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"treadmill_zone_2_steady_run":{"name":"Treadmill Zone-2 Steady Run","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"outdoor_zone_2_run":{"name":"Outdoor Zone-2 Run","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"treadmill_incline_walk_zone_2":{"name":"Treadmill Incline Walk (Zone 2)","components":["cardiorespiratory_endurance","body_composition"]},"elliptical_steady_state":{"name":"Elliptical Steady State","components":["cardiorespiratory_endurance","body_composition"]},"stationary_bike_zone_2_ride":{"name":"Stationary Bike Zone-2 Ride","components":["cardiorespiratory_endurance","body_composition"]},"rowing_machine_steady_state":{"name":"Rowing Machine Steady State","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"air_bike_steady_state":{"name":"Air Bike Steady State","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"swim_laps_freestyle_steady":{"name":"Swim Laps (Freestyle, Steady)","components":["cardiorespiratory_endurance","body_composition","muscular_endurance"]},"jump_rope_intervals_30_30":{"name":"Jump Rope Intervals (30/30)","components":["cardiorespiratory_endurance","muscular_endurance"]},"rowing_machine_intervals_500_m":{"name":"Rowing Machine Intervals (500 m)","components":["cardiorespiratory_endurance","muscular_endurance"]},"bike_intervals_2_min_hard":{"name":"Bike Intervals (2 min Hard)","components":["cardiorespiratory_endurance"]},"air_bike_sprints_20_40":{"name":"Air Bike Sprints (20/40)","components":["cardiorespiratory_endurance","power"]},"treadmill_hill_intervals":{"name":"Treadmill Hill Intervals","components":["cardiorespiratory_endurance","muscular_endurance"]},"track_400_m_repeats":{"name":"Track 400 m Repeats","components":["cardiorespiratory_endurance","speed"]},"track_200_m_repeats":{"name":"Track 200 m Repeats","components":["cardiorespiratory_endurance","speed"]},"fartlek_run_random_surges":{"name":"Fartlek Run (Random Surges)","components":["cardiorespiratory_endurance","muscular_endurance"]},"tempo_run_comfortably_hard":{"name":"Tempo Run (Comfortably Hard)","components":["cardiorespiratory_endurance","muscular_endurance"]},"stair_climber_intervals":{"name":"Stair Climber Intervals","components":["cardiorespiratory_endurance","muscular_endurance"]},"stadium_stairs_repeats":{"name":"Stadium Stairs Repeats","components":["cardiorespiratory_endurance","power","muscular_endurance"]},"hill_repeat_sprints_moderate_hill":{"name":"Hill Repeat Sprints (Moderate Hill)","components":["cardiorespiratory_endurance","speed","power"]},"battle_rope_intervals_30_30":{"name":"Battle Rope Intervals (30/30)","components":["cardiorespiratory_endurance","muscular_endurance","power"]},"kettlebell_snatch_intervals":{"name":"Kettlebell Snatch Intervals","components":["cardiorespiratory_endurance","power","muscular_endurance"]},"med_ball_circuit_continuous":{"name":"Med Ball Circuit (Continuous)","components":["cardiorespiratory_endurance","muscular_endurance","power"]},"shadow_boxing_intervals":{"name":"Shadow Boxing Intervals","components":["cardiorespiratory_endurance","muscular_endurance"]},"heavy_bag_rounds":{"name":"Heavy Bag Rounds","components":["cardiorespiratory_endurance","muscular_endurance","power"]},"burpee_intervals":{"name":"Burpee Intervals","components":["cardiorespiratory_endurance","muscular_endurance","power"]},"mountain_climber_intervals":{"name":"Mountain Climber Intervals","components":["cardiorespiratory_endurance","muscular_endurance"]},"sled_push_pull_conditioning_circuit":{"name":"Sled Push-Pull Conditioning Circuit","components":["cardiorespiratory_endurance","muscular_strength","power"]},"farmer_carry_conditioning_laps":{"name":"Farmer Carry Conditioning Laps","components":["cardiorespiratory_endurance","muscular_strength","muscular_endurance"]},"sled_drag_backward":{"name":"Sled Drag (Backward)","components":["cardiorespiratory_endurance","muscular_strength","muscular_endurance"]},"ruck_walk_weighted_vest":{"name":"Ruck Walk (Weighted Vest)","components":["cardiorespiratory_endurance","body_composition","muscular_strength"]},"sandbag_carry_circuit":{"name":"Sandbag Carry Circuit","components":["cardiorespiratory_endurance","muscular_strength","muscular_endurance"]},"swim_intervals_50_m_repeats":{"name":"Swim Intervals (50 m Repeats)","components":["cardiorespiratory_endurance","muscular_endurance"]},"aqua_jogging_deep_water":{"name":"Aqua Jogging (Deep Water)","components":["cardiorespiratory_endurance","body_composition"]},"cycling_commute_pace_ride":{"name":"Cycling Commute Pace Ride","components":["cardiorespiratory_endurance","body_composition"]},"outdoor_bike_hill_repeats":{"name":"Outdoor Bike Hill Repeats","components":["cardiorespiratory_endurance","power"]},"rowing_pyramid_500_750_1000_750_500":{"name":"Rowing Pyramid (500-750-1000-750-500)","components":["cardiorespiratory_endurance","muscular_endurance"]},"assault_bike_calorie_ladder_10_8_6_4_2":{"name":"Assault Bike Calorie Ladder (10-8-6-4-2)","components":["cardiorespiratory_endurance","power"]},"skipping_ladder_50_40_30_20_10":{"name":"Skipping Ladder (50-40-30-20-10)","components":["cardiorespiratory_endurance","coordination"]},"sprint_sled_finisher_trio":{"name":"Sprint-Sled-Finisher Trio","components":["cardiorespiratory_endurance","speed","power"]},"long_slow_distance_walk_recovery":{"name":"Long Slow Distance Walk (Recovery)","components":["cardiorespiratory_endurance","body_composition","flexibility"]},"bodyweight_circuit_amrap_8_min":{"name":"Bodyweight Circuit AMRAP (8 min)","components":["cardiorespiratory_endurance","muscular_endurance"]},"kettlebell_clean_and_press_ladder":{"name":"Kettlebell Clean-and-Press Ladder","components":["cardiorespiratory_endurance","power","muscular_strength"]},"sandbag_shouldering_intervals":{"name":"Sandbag Shouldering Intervals","components":["cardiorespiratory_endurance","power","muscular_strength"]},"cardio_dance_flow_continuous":{"name":"Cardio Dance Flow (Continuous)","components":["cardiorespiratory_endurance","coordination","body_composition"]},"incline_treadmill_power_hike":{"name":"Incline Treadmill Power Hike","components":["cardiorespiratory_endurance","body_composition"]}};
// ---- END extracted from index (24).html L3644-3644 (P4_EXERCISE_COMPONENTS registry (single-line data)) ----

// ---- BEGIN extracted from index (24).html L6002-6130 (p4AntiDebugV2 (anti-debug guard — see LANDMINES.md, kept for spec fidelity)) ----
</style><script id="p4AntiDebugV2">

(function(){

  'use strict';

  if (window.__p4AntiDebug) return;

  window.__p4AntiDebug = true;



  /* --- developer bypass (append ?p4_nodebug=1 or set localStorage) --- */

  try {

    if (localStorage.getItem('p4_nodebug') === '1') return;

    if (/[?&]p4_nodebug=1/.test(location.search)) return;

  } catch(e){}



  /* --- 1. device + screen + viewport detection --- */

  var ua = navigator.userAgent || '';

  var isElectron = !!window.__proDesktop || /Electron/i.test(ua);

  var isAndroid  = /Android/i.test(ua);

  var isIOS      = /iPhone|iPad|iPod/i.test(ua);

  var isWebView  = /; wv)/i.test(ua) || (isAndroid && /Version/[d.]+/i.test(ua));

  var isDesktopBrowser = !isElectron && !isAndroid && !isIOS && !isWebView;



  window.__p4ScreenInfo = {

    ua: ua,

    dpr: window.devicePixelRatio || 1,

    screenW: screen.width, screenH: screen.height,

    innerW: window.innerWidth, innerH: window.innerHeight,

    outerW: window.outerWidth || 0, outerH: window.outerHeight || 0,

    platform: navigator.platform || '',

    isElectron: isElectron, isAndroid: isAndroid, isIOS: isIOS,

    isWebView: isWebView, isDesktopBrowser: isDesktopBrowser

  };



  /* skip Electron — native guard already covers it */

  if (isElectron) return;



  /* --- 2. basic key + contextmenu block --- */

  document.addEventListener('keydown', function(e){

    var k = (e.key || '').toLowerCase();

    var ctrl = e.ctrlKey || e.metaKey;

    var shift = e.shiftKey;

    if (k === 'f12' ||

        (ctrl && shift && (k === 'i' || k === 'j' || k === 'c')) ||

        (ctrl && k === 'u')) {

      e.preventDefault(); e.stopPropagation();

      return false;

    }

  }, true);



  document.addEventListener('contextmenu', function(e){

    var t = e.target;

    if (t && (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA' || t.isContentEditable)) return;

    e.preventDefault();

  }, true);



  /* --- 3. detection + freeze --- */

  var detected = false;

  function freeze(){

    if (detected) return;

    detected = true;

    /* kill timers */

    try {

      var hi = setTimeout(function(){}, 0);

      for (var i = 0; i <= hi; i++) clearTimeout(i);

      var hj = setInterval(function(){}, 9999);

      for (var j = 0; j <= hj; j++) clearInterval(j);

    } catch(e){}

    /* blank the page */

    try {

      document.documentElement.innerHTML = '<html><head><title>Pro</title></head><body style="background:#0b0e14;color:#cbd5e1;font-family:system-ui;display:flex;align-items:center;justify-content:center;height:100vh;margin:0"><div style="text-align:center;opacity:0.75"><div style="font-size:32px;font-weight:700;letter-spacing:-0.5px">Pro</div><div style="font-size:13px;margin-top:8px">Session ended. Reload to continue.</div></div></body></html>';

    } catch(e){}

    /* try to close (works only if tab was opened by script) */

    try { window.close(); } catch(e){}

    /* keep hammering it */

    try {

      setInterval(function(){

        try {

          if (document.body && document.body.innerHTML.indexOf('Session ended') === -1) {

            document.documentElement.innerHTML = '<html><body style="background:#0b0e14"></body></html>';

          }

        } catch(e){}

      }, 400);

    } catch(e){}

  }



  /* --- 3a. toString/getter trap (desktop only) --- */

  if (isDesktopBrowser) {

    try {

      var probe = document.createElement('div');

      var fired = false;

      Object.defineProperty(probe, 'id', {

        get: function(){

          if (fired) return 'p4';

          fired = true;

          setTimeout(function(){

            /* verify with timing before nuking */

            var t0 = performance.now();

            try { (function(){ debugger; })(); } catch(e){}

            if (performance.now() - t0 > 60) freeze();

          }, 80);

          return 'p4';

        }

      });

      for (var n = 0; n < 3; n++) try { console.log(probe); } catch(e){}

      try { console.warn(probe); } catch(e){}

    } catch(e){}

  }



  /* --- 3b. debugger timing loop (works everywhere, aggressiveness scales) --- */

  var threshold = isDesktopBrowser ? 90 : 220;

  setInterval(function(){

    if (detected) return;

    var t0 = performance.now();

    try { (function(){ debugger; })(); } catch(e){}

    if (performance.now() - t0 > threshold) freeze();

  }, isDesktopBrowser ? 1200 : 2400);



  /* --- 3c. docked-devTools size delta (desktop only) --- */

  if (isDesktopBrowser) {

    setInterval(function(){

      if (detected) return;

      var dw = Math.abs((window.outerWidth  || 0) - (window.innerWidth  || 0));

      var dh = Math.abs((window.outerHeight || 0) - (window.innerHeight || 0));

      if (dw > 240 || dh > 240) freeze();

    }, 1400);

  }



  /* --- 4. silence verbose logs (after probes have run) --- */

  try { var noop = function(){}; console.log = console.info = console.debug = noop; } catch(e){}

})();

</script></head>

// ---- END extracted from index (24).html L6002-6130 (p4AntiDebugV2 (anti-debug guard — see LANDMINES.md, kept for spec fidelity)) ----

// ---- BEGIN extracted from index (24).html L47489-47919 (floating pill UI overlay) ----
(function() {

    'use strict';



    // ====================================================================

    //  CONFIGURATION – every tunable parameter

    // ====================================================================

    const CONFIG = {

        // Positioning

        leftOffset: 24,

        zIndex: 1001,



        // Sizes (base and active)

        baseSize: {

            sm: { h: 14, w: 32 },

            md: { h: 18, w: 44 },

            lg: { h: 22, w: 56 }

        },

        activeSize: {

            sm: { h: 34, w: 50 },

            md: { h: 42, w: 62 },

            lg: { h: 50, w: 74 }

        },



        // Responsive scaling

        mobileScale: 0.85,   // ≤700px

        smallScale: 0.70,    // ≤500px



        // Scroll & behaviour

        hysteresisThreshold: 50,

        scrollOffset: 80,

        segmentGap: 6,

        maxHeight: '80vh',



        // Tooltip

        tooltipMaxWidth: 200,

        tooltipDelay: 300,   // ms before showing



        // Observer thresholds

        intersectionThresholds: [0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0],



        // Fallback

        fallbackToScroll: true,



        // Debug

        debug: false,



        // Status colors (override if desired)

        colors: {

            done: 'var(--success, #10b981)',

            'in-progress': 'var(--warning, #f59e0b)',

            skipped: 'var(--danger, #ef4444)',

            test: 'var(--primary, #2563eb)',

            pending: 'var(--gray-300, #cbd5e1)'

        }

    };



    // ====================================================================

    //  INTERNAL STATE

    // ====================================================================

    let activeIndex = -1;

    let observer = null;

    let updatePending = false;

    let scrollRaf = null;

    let resizeRaf = null;

    let isInitialized = false;

    let pillFloat, segmentsContainer, activeIndexEl, totalCountEl;



    // ====================================================================

    //  UTILITIES

    // ====================================================================

    function log(...args) { if (CONFIG.debug) console.log('[Pill]', ...args); }

    function warn(...args) { if (CONFIG.debug) console.warn('[Pill]', ...args); }



    function getExerciseStatus(ex) {

        if (ex.actual && !ex.skipped) return 'done';

        if (ex.skipped) return 'skipped';

        const draftKey = currentWorkout?.id ? `draft_${currentWorkout.id}_${ex.id}` : null;

        if (draftKey && localStorage.getItem(draftKey)) return 'in-progress';

        if (workoutData.exercises[ex.id]?.tested1RM) return 'test';

        return 'pending';

    }



    function getExerciseSize(ex) {

        const sets = ex.prescribed?.sets || 3;

        if (sets >= 5) return 'lg';

        if (sets >= 3) return 'md';

        return 'sm';

    }



    function getResponsiveSize(base, active, scale) {

        if (scale === 1) return { base, active };

        return {

            base: { h: Math.round(base.h * scale), w: Math.round(base.w * scale) },

            active: { h: Math.round(active.h * scale), w: Math.round(active.w * scale) }

        };

    }



    function getSizeForScreen() {

        const w = window.innerWidth;

        let scale = 1;

        if (w <= 500) scale = CONFIG.smallScale;

        else if (w <= 700) scale = CONFIG.mobileScale;

        return {

            sm: getResponsiveSize(CONFIG.baseSize.sm, CONFIG.activeSize.sm, scale),

            md: getResponsiveSize(CONFIG.baseSize.md, CONFIG.activeSize.md, scale),

            lg: getResponsiveSize(CONFIG.baseSize.lg, CONFIG.activeSize.lg, scale)

        };

    }



    // ====================================================================

    //  CSS INJECTION (dynamic sizes, theme‑aware, all styles)

    // ====================================================================

    function injectCSS() {

        const s = getSizeForScreen();

        const css = `

            .pill-float{position:fixed;left:${CONFIG.leftOffset}px;top:50%;transform:translateY(-50%);z-index:${CONFIG.zIndex};display:none;flex-direction:column;align-items:center;gap:${CONFIG.segmentGap}px;padding:0;max-height:${CONFIG.maxHeight};pointer-events:none;filter:drop-shadow(0 8px 24px rgba(0,0,0,0.10));transition:opacity 0.2s}

            .pill-float.visible{display:flex;opacity:1}

            .pill-float>*{pointer-events:auto}

            .pill-header{background:rgba(255,255,255,0.6);backdrop-filter:blur(8px);-webkit-backdrop-filter:blur(8px);padding:6px 14px 8px 14px;border-radius:40px;box-shadow:0 4px 12px rgba(0,0,0,0.04);text-align:center;border:1px solid rgba(255,255,255,0.3);flex-shrink:0;cursor:pointer}

            .pill-count{font-weight:800;font-size:1.1rem;color:var(--dark,#0f172a);line-height:1.2}

            .pill-count .active-index{color:var(--primary,#2563eb)}

            .pill-count .total-num{color:var(--gray-400,#94a3b8);font-weight:400}

            .pill-label{font-size:0.4rem;font-weight:700;text-transform:uppercase;letter-spacing:1.2px;color:var(--gray-400,#94a3b8);margin-top:-2px}

            .pill-segments{display:flex;flex-direction:column;gap:${CONFIG.segmentGap}px;padding:0 4px 0 0;flex:1;overflow-y:auto;overscroll-behavior:contain;scroll-behavior:smooth;min-height:50px;justify-content:flex-start;max-height:calc(${CONFIG.maxHeight} - 80px);width:100%;pointer-events:auto}

            .pill-segments::-webkit-scrollbar{width:4px}

            .pill-segments::-webkit-scrollbar-track{background:var(--gray-200,#e2e8f0);border-radius:4px}

            .pill-segments::-webkit-scrollbar-thumb{background:var(--gray-400,#94a3b8);border-radius:4px}

            .segment{border-radius:20px;transition:all 0.4s cubic-bezier(0.34,1.56,0.64,1);position:relative;cursor:pointer;flex-shrink:0;box-shadow:0 2px 6px rgba(0,0,0,0.06);border:2px solid rgba(255,255,255,0.6);box-sizing:border-box;touch-action:manipulation;min-width:32px}

            .segment.size-sm{height:${s.sm.base.h}px;width:${s.sm.base.w}px}

            .segment.size-md{height:${s.md.base.h}px;width:${s.md.base.w}px}

            .segment.size-lg{height:${s.lg.base.h}px;width:${s.lg.base.w}px}

            .segment.active.size-sm{height:${s.sm.active.h}px;width:${s.sm.active.w}px}

            .segment.active.size-md{height:${s.md.active.h}px;width:${s.md.active.w}px}

            .segment.active.size-lg{height:${s.lg.active.h}px;width:${s.lg.active.w}px}

            .segment.done{background:${CONFIG.colors.done}}

            .segment.in-progress{background:${CONFIG.colors['in-progress']};animation:pulse-amber 1.8s ease-in-out infinite}

            .segment.skipped{background:${CONFIG.colors.skipped}}

            .segment.test{background:${CONFIG.colors.test}}

            .segment.pending{background:${CONFIG.colors.pending}}

            .segment.clicked{animation:clickPop 0.3s ease}

            @keyframes clickPop{0%{transform:scale(1)}50%{transform:scale(1.15)}100%{transform:scale(1)}}

            @keyframes pulse-amber{0%,100%{opacity:1;transform:scaleX(1)}50%{opacity:0.5;transform:scaleX(0.95)}}

            .segment:hover::after{content:attr(data-tooltip);position:absolute;left:calc(100% + 14px);top:50%;transform:translateY(-50%);background:var(--dark,#0f172a);color:var(--light,#f8fafc);padding:5px 12px;border-radius:8px;font-size:0.65rem;font-weight:600;white-space:nowrap;z-index:20;box-shadow:0 4px 16px rgba(0,0,0,0.2);pointer-events:none;max-width:${CONFIG.tooltipMaxWidth}px;overflow:hidden;text-overflow:ellipsis;transition:opacity ${CONFIG.tooltipDelay}ms}

            .segment:hover::before{content:'';position:absolute;left:calc(100% + 6px);top:50%;transform:translateY(-50%);border:5px solid transparent;border-right-color:var(--dark,#0f172a);z-index:20;pointer-events:none}

            @media(max-width:700px){.pill-float{left:12px}.segment.size-sm{height:${s.sm.base.h}px;width:${s.sm.base.w}px}.segment.size-md{height:${s.md.base.h}px;width:${s.md.base.w}px}.segment.size-lg{height:${s.lg.base.h}px;width:${s.lg.base.w}px}.segment.active.size-sm{height:${s.sm.active.h}px;width:${s.sm.active.w}px}.segment.active.size-md{height:${s.md.active.h}px;width:${s.md.active.w}px}.segment.active.size-lg{height:${s.lg.active.h}px;width:${s.lg.active.w}px}.pill-count{font-size:0.95rem}.pill-header{padding:4px 12px 6px 12px}.pill-segments{max-height:calc(${CONFIG.maxHeight} - 70px)}.segment:hover::after{left:auto;right:calc(100% + 14px)}.segment:hover::before{left:auto;right:calc(100% + 6px);border-right-color:transparent;border-left-color:var(--dark,#0f172a)}}

            @media(max-width:500px){.pill-float{left:8px}.segment.size-sm{height:${s.sm.base.h}px;width:${s.sm.base.w}px}.segment.size-md{height:${s.md.base.h}px;width:${s.md.base.w}px}.segment.size-lg{height:${s.lg.base.h}px;width:${s.lg.base.w}px}.segment.active.size-sm{height:${s.sm.active.h}px;width:${s.sm.active.w}px}.segment.active.size-md{height:${s.md.active.h}px;width:${s.md.active.w}px}.segment.active.size-lg{height:${s.lg.active.h}px;width:${s.lg.active.w}px}.pill-count{font-size:0.8rem}.pill-header{padding:3px 10px 5px 10px}.pill-segments{max-height:calc(${CONFIG.maxHeight} - 60px)}}

            @media(prefers-reduced-motion:reduce){*{animation-duration:0.01ms!important;transition-duration:0.01ms!important}.segment.in-progress{animation:none}}

        `;

        const existing = document.getElementById('pill-style');

        if (existing) existing.remove();

        const style = document.createElement('style');

        style.id = 'pill-style';

        style.textContent = css;

        document.head.appendChild(style);

    }



    // ====================================================================

    //  DOM INITIALIZATION

    // ====================================================================

    function initDOM() {

        if (document.getElementById('pillFloat')) return;

        const html = `

            <div class="pill-float" id="pillFloat">

                <div class="pill-header" onclick="if(typeof showSection==='function') showSection('workout')">

                    <div class="pill-count">

                        <span class="active-index" id="pillActiveIndex">0</span>

                        <span class="total-num">/<span id="pillTotal">0</span></span>

                    </div>

                    <div class="pill-label"></div>

                </div>

                <div class="pill-segments" id="pillSegments"></div>

            </div>

        `;

        document.body.insertAdjacentHTML('beforeend', html);

        pillFloat = document.getElementById('pillFloat');

        segmentsContainer = document.getElementById('pillSegments');

        activeIndexEl = document.getElementById('pillActiveIndex');

        totalCountEl = document.getElementById('pillTotal');

    }



    // ====================================================================

    //  RENDER PILL

    // ====================================================================

    function renderPill() {

        const workoutSection = document.getElementById('workout-section');

        const isWorkoutPage = workoutSection && workoutSection.classList.contains('active');

        if (!isWorkoutPage || !currentWorkout || !currentWorkout.exercises || !currentWorkout.exercises.length) {

            if (pillFloat) pillFloat.classList.remove('visible');

            return;

        }

        if (!pillFloat) initDOM();

        pillFloat.classList.add('visible');

        const exercises = currentWorkout.exercises;

        totalCountEl.textContent = exercises.length;

        let scrollTop = segmentsContainer.scrollTop;

        segmentsContainer.innerHTML = '';

        exercises.forEach((ex, index) => {

            const status = getExerciseStatus(ex);

            const size = getExerciseSize(ex);

            const seg = document.createElement('div');

            seg.className = `segment ${status} size-${size}`;

            seg.dataset.index = index;

            const statusLabel = status.charAt(0).toUpperCase() + status.slice(1).replace('-', ' ');

            seg.dataset.tooltip = `${ex.name}: ${statusLabel}`;

            seg.setAttribute('role', 'button');

            seg.setAttribute('tabindex', '0');

            seg.setAttribute('aria-label', `Navigate to ${ex.name}`);

            seg.addEventListener('click', function(e) {

                e.stopPropagation();

                const idx = parseInt(this.dataset.index);

                const target = document.getElementById(`exercise_${idx}`);

                if (target) {

                    const topOffset = CONFIG.scrollOffset;

                    const elementPosition = target.getBoundingClientRect().top + window.pageYOffset;

                    window.scrollTo({ top: elementPosition - topOffset, behavior: 'smooth' });

                    // Open logger if available

                    if (typeof openLoggerForExercise === 'function') {

                        setTimeout(() => openLoggerForExercise(idx), 100);

                    }

                    this.classList.add('clicked');

                    setTimeout(() => this.classList.remove('clicked'), 300);

                }

            });

            seg.addEventListener('keydown', function(e) {

                if (e.key === 'Enter' || e.key === ' ') {

                    e.preventDefault();

                    this.click();

                }

                if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {

                    e.preventDefault();

                    const segs = segmentsContainer.querySelectorAll('.segment');

                    const currentIdx = Array.from(segs).indexOf(this);

                    let newIdx = currentIdx + (e.key === 'ArrowDown' ? 1 : -1);

                    if (newIdx < 0) newIdx = segs.length - 1;

                    if (newIdx >= segs.length) newIdx = 0;

                    const targetSeg = segs[newIdx];

                    if (targetSeg) targetSeg.click();

                }

            });

            segmentsContainer.appendChild(seg);

        });

        if (scrollTop > 0) segmentsContainer.scrollTop = scrollTop;

        updateActiveSegment();

    }



    // ====================================================================

    //  ACTIVE SEGMENT DETECTION (hysteresis + throttle)

    // ====================================================================

    function updateActiveSegment() {

        if (updatePending) return;

        updatePending = true;

        requestAnimationFrame(() => {

            updatePending = false;

            const segs = segmentsContainer.querySelectorAll('.segment');

            if (!segs.length) return;

            const viewportCenter = window.innerHeight / 2;

            let bestIndex = -1, bestDistance = Infinity;

            const cards = document.querySelectorAll('.exercise-item');

            cards.forEach(card => {

                const rect = card.getBoundingClientRect();

                if (rect.bottom < 0 || rect.top > window.innerHeight) return;

                const cardCenter = rect.top + rect.height / 2;

                const distance = Math.abs(cardCenter - viewportCenter);

                if (distance < bestDistance) {

                    bestDistance = distance;

                    bestIndex = parseInt(card.dataset.index);

                }

            });

            if (bestIndex === -1) return;

            if (bestIndex !== activeIndex) {

                const currentDist = activeIndex !== -1 ? (() => {

                    const card = document.querySelector(`.exercise-item[data-index="${activeIndex}"]`);

                    if (card) {

                        const rect = card.getBoundingClientRect();

                        if (rect.bottom >= 0 && rect.top <= window.innerHeight) {

                            return Math.abs((rect.top + rect.height/2) - viewportCenter);

                        }

                    }

                    return Infinity;

                })() : Infinity;

                if (bestDistance < currentDist - CONFIG.hysteresisThreshold || activeIndex === -1) {

                    activeIndex = bestIndex;

                    segs.forEach((seg, idx) => seg.classList.toggle('active', idx === activeIndex));

                    activeIndexEl.textContent = activeIndex + 1;

                    log('Active index changed to', activeIndex);

                }

            }

        });

    }



    // ====================================================================

    //  OBSERVER SETUP (IntersectionObserver + fallback)

    // ====================================================================

    function setupObserver() {

        if (observer) observer.disconnect();

        const cards = document.querySelectorAll('.exercise-item');

        if (!cards.length) return;

        if ('IntersectionObserver' in window) {

            observer = new IntersectionObserver(() => {

                updateActiveSegment();

            }, { threshold: CONFIG.intersectionThresholds });

            cards.forEach(card => observer.observe(card));

            log('IntersectionObserver active');

        } else if (CONFIG.fallbackToScroll) {

            const onScroll = () => {

                if (scrollRaf) cancelAnimationFrame(scrollRaf);

                scrollRaf = requestAnimationFrame(() => {

                    updateActiveSegment();

                    scrollRaf = null;

                });

            };

            window.addEventListener('scroll', onScroll, { passive: true });

            log('Fallback scroll listener active');

        }

    }



    function reobserve() {

        setTimeout(() => { setupObserver(); }, 100);

    }



    // ====================================================================

    //  RESIZE HANDLER (dynamic CSS re‑injection)

    // ====================================================================

    function onResize() {

        if (resizeRaf) cancelAnimationFrame(resizeRaf);

        resizeRaf = requestAnimationFrame(() => {

            injectCSS();

            resizeRaf = null;

        });

    }



    // ====================================================================

    //  HOOKS INTO ALL APP FUNCTIONS

    // ====================================================================

    const functionNames = [

        'showSection',

        'updateTodaysWorkout',

        'saveExercisePerformance',

        'skipExercise',

        'generateNextWorkout',

        'completeWorkout',

        'removeExerciseFromWorkout',

        'addExerciseToCurrentWorkout'

    ];

    functionNames.forEach(name => {

        const orig = window[name];

        if (typeof orig === 'function') {

            window[name] = function(...args) {

                const result = orig.apply(this, args);

                setTimeout(() => {

                    renderPill();

                    reobserve();

                }, 100);

                return result;

            };

        }

    });



    // ====================================================================

    //  MUTATION OBSERVER (fallback for DOM changes)

    // ====================================================================

    const workoutContainer = document.getElementById('exerciseList');

    if (workoutContainer) {

        const mutObserver = new MutationObserver(() => {

            if (window._pillMutTimer) clearTimeout(window._pillMutTimer);

            window._pillMutTimer = setTimeout(() => {

                renderPill();

                reobserve();

                injectCSS();

            }, 200);

        });

        mutObserver.observe(workoutContainer, { childList: true, subtree: true, attributes: true });

    }



    // ====================================================================

    //  WINDOW EVENTS

    // ====================================================================

    window.addEventListener('resize', onResize, { passive: true });



    // ====================================================================

    //  INITIALIZATION

    // ====================================================================

    function init() {

        if (isInitialized) return;

        isInitialized = true;

        injectCSS();

        initDOM();

        setTimeout(() => {

            renderPill();

            reobserve();

        }, 300);

        log('Pill initialized');

    }



    if (document.readyState === 'loading') {

        document.addEventListener('DOMContentLoaded', init);

    } else {

        init();

    }



    // ====================================================================

    //  PUBLIC API

    // ====================================================================

    window.refreshPill = function() {

        renderPill();

        reobserve();

        injectCSS();

    };



    window.setActiveSegment = function(index) {

        if (index < 0 || index >= segmentsContainer.querySelectorAll('.segment').length) return;

        activeIndex = index;

        const segs = segmentsContainer.querySelectorAll('.segment');

        segs.forEach((seg, idx) => seg.classList.toggle('active', idx === activeIndex));

        activeIndexEl.textContent = activeIndex + 1;

    };



    window.getPillConfig = function() { return CONFIG; };



    // ====================================================================

    //  CLEANUP ON PAGE UNLOAD

    // ====================================================================

    window.addEventListener('beforeunload', function() {

        if (observer) observer.disconnect();

        if (scrollRaf) cancelAnimationFrame(scrollRaf);

        if (resizeRaf) cancelAnimationFrame(resizeRaf);

    });



})();

// ---- END extracted from index (24).html L47489-47919 (floating pill UI overlay) ----

// ---- BEGIN extracted from index (24).html L49103-62165 (P4 layer (P4 namespace, store, keys, SEC, toast, ACCENTS, Theme, debounce, download, Vocab, Nav, Power, Backup, Motivation, Legal, Onboard, Studio, SettingsUI, Rings, LifeStage, Emoji, Profiles, Vault, WorkoutCards)) ----
/*P4:JS:BEG*/

/* P4 VOCAB LADDERS — every key maps to 10 level variants (L1..L10).

 * L5 (index 4) always equals the authored default string (no-op level).

 * Part A: exact UI phrases. Part B: jargon single-word ladders. */

window.P4_VOCAB_LADDERS = {

  "Home": [

    "Home",  /* L1 */

    "Home",  /* L2 */

    "Home",  /* L3 */

    "Home",  /* L4 */

    "Home",  /* L5 */

    "Home",  /* L6 */

    "Home",  /* L7 */

    "Home",  /* L8 */

    "Home",  /* L9 */

    "Home"  /* L10 */

  ],

  "Dashboard": [

    "My Page",  /* L1 */

    "Home Page",  /* L2 */

    "Dashboard",  /* L3 */

    "Overview",  /* L4 */

    "Dashboard",  /* L5 */

    "Command Center",  /* L6 */

    "Performance Dashboard",  /* L7 */

    "Analytics Dashboard",  /* L8 */

    "Comprehensive Metrics Dashboard",  /* L9 */

    "Panoramic Training Observatory"  /* L10 */

  ],

  "Workout": [

    "Workout",  /* L1 */

    "Workout",  /* L2 */

    "Workout",  /* L3 */

    "Session",  /* L4 */

    "Workout",  /* L5 */

    "Training",  /* L6 */

    "Training Session",  /* L7 */

    "Resistance Training Session",  /* L8 */

    "Structured Training Session",  /* L9 */

    "Constitutional Exercise Regimen"  /* L10 */

  ],

  "Workouts": [

    "Workouts",  /* L1 */

    "Workouts",  /* L2 */

    "Workouts",  /* L3 */

    "Sessions Done",  /* L4 */

    "Workouts",  /* L5 */

    "Sessions Logged",  /* L6 */

    "Completed Workouts",  /* L7 */

    "Completed Training Sessions",  /* L8 */

    "Documented Exercise Bouts Completed",  /* L9 */

    "Culminated Constitutional Exercise Bouts"  /* L10 */

  ],

  "History": [

    "My Past",  /* L1 */

    "History",  /* L2 */

    "History",  /* L3 */

    "Log Book",  /* L4 */

    "History",  /* L5 */

    "Training Log",  /* L6 */

    "Workout History",  /* L7 */

    "Chronological Training Record",  /* L8 */

    "Longitudinal Training Archive",  /* L9 */

    "Diachronic Exercise Ledger"  /* L10 */

  ],

  "Library": [

    "Moves",  /* L1 */

    "Exercises",  /* L2 */

    "Library",  /* L3 */

    "Exercise List",  /* L4 */

    "Library",  /* L5 */

    "Exercise Library",  /* L6 */

    "Movement Library",  /* L7 */

    "Exercise Repository",  /* L8 */

    "Movement Compendium",  /* L9 */

    "Encyclopedic Exercise Repository"  /* L10 */

  ],

  "Progress": [

    "How I'm Doing",  /* L1 */

    "My Progress",  /* L2 */

    "Progress",  /* L3 */

    "Progress",  /* L4 */

    "Progress",  /* L5 */

    "Progress Analytics",  /* L6 */

    "Performance Analytics",  /* L7 */

    "Longitudinal Performance Metrics",  /* L8 */

    "Quantified Progression Analytics",  /* L9 */

    "Longitudinal Kinesiological Analytics"  /* L10 */

  ],

  "Recovery": [

    "Rest Check",  /* L1 */

    "Recovery",  /* L2 */

    "Recovery",  /* L3 */

    "Recovery",  /* L4 */

    "Recovery",  /* L5 */

    "Recovery Status",  /* L6 */

    "Recovery Analytics",  /* L7 */

    "Recuperative Status Assessment",  /* L8 */

    "Convalescence & Homeostatic Monitoring",  /* L9 */

    "Recuperative Homeostatic Surveillance"  /* L10 */

  ],

  "Settings": [

    "Set Up",  /* L1 */

    "Settings",  /* L2 */

    "Settings",  /* L3 */

    "Options",  /* L4 */

    "Settings",  /* L5 */

    "Preferences",  /* L6 */

    "Configuration",  /* L7 */

    "Application Configuration",  /* L8 */

    "Systematized Preferences Architecture",  /* L9 */

    "Granular Configuration Panoply"  /* L10 */

  ],

  "Start Workout": [

    "Let's Go!",  /* L1 */

    "Start Workout",  /* L2 */

    "Start Workout",  /* L3 */

    "Begin Session",  /* L4 */

    "Start Workout",  /* L5 */

    "Initiate Training",  /* L6 */

    "Commence Training Session",  /* L7 */

    "Initiate Resistance Session",  /* L8 */

    "Commence Structured Exercise Bout",  /* L9 */

    "Embark Upon Today's Kinetic Endeavor"  /* L10 */

  ],

  "Resume": [

    "Keep Going",  /* L1 */

    "Resume",  /* L2 */

    "Resume",  /* L3 */

    "Continue",  /* L4 */

    "Resume",  /* L5 */

    "Re-engage Session",  /* L6 */

    "Resume Training Session",  /* L7 */

    "Reinstate Session Continuity",  /* L8 */

    "Resume Ongoing Exercise Bout",  /* L9 */

    "Recommence Thitherto Unfinished Endeavor"  /* L10 */

  ],

  "Save": [

    "Save",  /* L1 */

    "Save",  /* L2 */

    "Save",  /* L3 */

    "Save",  /* L4 */

    "Save",  /* L5 */

    "Persist",  /* L6 */

    "Save Changes",  /* L7 */

    "Commit Modifications",  /* L8 */

    "Perpetuate Adjustments",  /* L9 */

    "Render Permanent These Adjustments"  /* L10 */

  ],

  "Cancel": [

    "Stop",  /* L1 */

    "Cancel",  /* L2 */

    "Cancel",  /* L3 */

    "Cancel",  /* L4 */

    "Cancel",  /* L5 */

    "Abort",  /* L6 */

    "Discard Changes",  /* L7 */

    "Abrogate Operation",  /* L8 */

    "Rescind Pending Modifications",  /* L9 */

    "Annul Thine Pending Alterations"  /* L10 */

  ],

  "Close": [

    "Close",  /* L1 */

    "Close",  /* L2 */

    "Close",  /* L3 */

    "Close",  /* L4 */

    "Close",  /* L5 */

    "Dismiss",  /* L6 */

    "Close Window",  /* L7 */

    "Terminate Display",  /* L8 */

    "Conclude This Panel",  /* L9 */

    "Withdraw This Provisional View"  /* L10 */

  ],

  "RESET": [

    "START OVER",  /* L1 */

    "RESET",  /* L2 */

    "RESET",  /* L3 */

    "RESET",  /* L4 */

    "RESET",  /* L5 */

    "PURGE",  /* L6 */

    "FULL RESET",  /* L7 */

    "EXPUNGE ALL DATA",  /* L8 */

    "IRREVOCABLE DATA EXPUNGING",  /* L9 */

    "UTTER ANNIHILATION OF ACCUMULATED DATA"  /* L10 */

  ],

  "Later": [

    "Later",  /* L1 */

    "Later",  /* L2 */

    "Maybe Later",  /* L3 */

    "Not Now",  /* L4 */

    "Later",  /* L5 */

    "Defer",  /* L6 */

    "Postpone",  /* L7 */

    "Defer To Later",  /* L8 */

    "Defer Consideration To A Later Juncture",  /* L9 */

    "Defer Such Contemplation To A More Propitious Hour"  /* L10 */

  ],

  "User": [

    "User",  /* L1 */

    "User",  /* L2 */

    "User",  /* L3 */

    "Member",  /* L4 */

    "User",  /* L5 */

    "Athlete",  /* L6 */

    "Trainee",  /* L7 */

    "Registered Athlete",  /* L8 */

    "Enrolled Training Practitioner",  /* L9 */

    "Dedicated Adherent Of Physical Culture"  /* L10 */

  ],

  "Readiness": [

    "Ready?",  /* L1 */

    "Ready Check",  /* L2 */

    "Readiness",  /* L3 */

    "Readiness",  /* L4 */

    "Readiness",  /* L5 */

    "Readiness Index",  /* L6 */

    "Readiness Assessment",  /* L7 */

    "Physiological Readiness Estimate",  /* L8 */

    "Multifactorial Readiness Quantification",  /* L9 */

    "Holistic Somatic Readiness Appraisal"  /* L10 */

  ],

  "Streak": [

    "Streak",  /* L1 */

    "Streak",  /* L2 */

    "Streak",  /* L3 */

    "Streak",  /* L4 */

    "Streak",  /* L5 */

    "Consistency Run",  /* L6 */

    "Consecutive-Day Streak",  /* L7 */

    "Unbroken Adherence Sequence",  /* L8 */

    "Sustained Consecutive Adherence Metric",  /* L9 */

    "Uninterrupted Diurnal Adherence Continuum"  /* L10 */

  ],

  "Total Volume:": [

    "Weight Moved:",  /* L1 */

    "Total Weight:",  /* L2 */

    "Total Volume:",  /* L3 */

    "Total Volume:",  /* L4 */

    "Total Volume:",  /* L5 */

    "Aggregate Volume:",  /* L6 */

    "Cumulative Training Volume:",  /* L7 */

    "Gross Tonnage Accumulated:",  /* L8 */

    "Cumulative Load-Displacement Product:",  /* L9 */

    "Aggregate Tonnage-Displacement Quotient:"  /* L10 */

  ],

  "Sets Completed:": [

    "Sets Done:",  /* L1 */

    "Sets Done:",  /* L2 */

    "Sets Completed:",  /* L3 */

    "Sets Completed:",  /* L4 */

    "Sets Completed:",  /* L5 */

    "Completed Sets:",  /* L6 */

    "Sets Brought To Completion:",  /* L7 */

    "Sets Rendered To Completion:",  /* L8 */

    "Work Sets Brought To Termination:",  /* L9 */

    "Work Sets Consummated Unto Fulfillment:"  /* L10 */

  ],

  "Total sets:": [

    "Total sets:",  /* L1 */

    "All sets:",  /* L2 */

    "Total sets:",  /* L3 */

    "Total sets:",  /* L4 */

    "Total sets:",  /* L5 */

    "Aggregate set count:",  /* L6 */

    "Cumulative set volume:",  /* L7 */

    "Gross quantity of sets:",  /* L8 */

    "Cumulative enumeration of work sets:",  /* L9 */

    "Aggregate numerical sum of work sets:"  /* L10 */

  ],

  "Estimated Time:": [

    "Time Needed:",  /* L1 */

    "Estimated Time:",  /* L2 */

    "Estimated Time:",  /* L3 */

    "Estimated Time:",  /* L4 */

    "Estimated Time:",  /* L5 */

    "Projected Duration:",  /* L6 */

    "Estimated Time Requirement:",  /* L7 */

    "Anticipated Temporal Investment:",  /* L8 */

    "Projected Temporal Expenditure:",  /* L9 */

    "Prognosticated Temporal Outlay:"  /* L10 */

  ],

  "Personal Information": [

    "About Me",  /* L1 */

    "My Info",  /* L2 */

    "Personal Information",  /* L3 */

    "Personal Information",  /* L4 */

    "Personal Information",  /* L5 */

    "Personal Particulars",  /* L6 */

    "Personal Information Profile",  /* L7 */

    "Biographical & Anthropometric Data",  /* L8 */

    "Individual Biometric & Demographic Corpus",  /* L9 */

    "Comprehensive Individual Anthropometric Dossier"  /* L10 */

  ],

  "Training Preferences": [

    "How I Train",  /* L1 */

    "Training Preferences",  /* L2 */

    "Training Preferences",  /* L3 */

    "Training Preferences",  /* L4 */

    "Training Preferences",  /* L5 */

    "Training Parameters",  /* L6 */

    "Training Preference Matrix",  /* L7 */

    "Training Modality Configurations",  /* L8 */

    "Exercise Modality & Preference Architecture",  /* L9 */

    "Granular Exercise Preference Calibration Array"  /* L10 */

  ],

  "Muscle Building": [

    "Build Muscle",  /* L1 */

    "Muscle Building",  /* L2 */

    "Muscle Building",  /* L3 */

    "Muscle Building",  /* L4 */

    "Muscle Building",  /* L5 */

    "Hypertrophy Focus",  /* L6 */

    "Muscle Mass Development",  /* L7 */

    "Myofibrillar Growth Emphasis",  /* L8 */

    "Sarcoplasmic & Myofibrillar Hypertrophy",  /* L9 */

    "Augmentation Of Lean Muscle Cross-Sectional Area"  /* L10 */

  ],

  "Strength": [

    "Get Stronger",  /* L1 */

    "Strength",  /* L2 */

    "Strength",  /* L3 */

    "Strength",  /* L4 */

    "Strength",  /* L5 */

    "Maximal Strength",  /* L6 */

    "Strength Development",  /* L7 */

    "Maximal Force Production",  /* L8 */

    "Neural Strength Acquisition Focus",  /* L9 */

    "Maximal Volitional Force Generation Capacity"  /* L10 */

  ],

  "Endurance": [

    "Keep Going",  /* L1 */

    "Endurance",  /* L2 */

    "Endurance",  /* L3 */

    "Endurance",  /* L4 */

    "Endurance",  /* L5 */

    "Muscular Endurance",  /* L6 */

    "Endurance Capacity",  /* L7 */

    "Sustained Output Capacity",  /* L8 */

    "Repeated-Effort Endurance Qualities",  /* L9 */

    "Prolonged Submaximal Output Sustainability"  /* L10 */

  ],

  "Longevity": [

    "Stay Healthy",  /* L1 */

    "Longevity",  /* L2 */

    "Longevity",  /* L3 */

    "Longevity",  /* L4 */

    "Longevity",  /* L5 */

    "Healthspan Focus",  /* L6 */

    "Longevity & Durability",  /* L7 */

    "Lifespan Physical Resilience",  /* L8 */

    "Long-Term Musculoskeletal Durability",  /* L9 */

    "Perpetuation Of Functional Healthspan"  /* L10 */

  ],

  "Longevity & Joint Health": [

    "Stay Healthy",  /* L1 */

    "Healthy Joints",  /* L2 */

    "Joint Health",  /* L3 */

    "Longevity & Joint Health",  /* L4 */

    "Longevity & Joint Health",  /* L5 */

    "Longevity & Articular Health",  /* L6 */

    "Longevity & Joint Integrity",  /* L7 */

    "Lifespan Joint Resilience Focus",  /* L8 */

    "Prolonged Articular Homeostasis Emphasis",  /* L9 */

    "Sustained Diarthrodial Integrity Across Lifespan"  /* L10 */

  ],

  "Balanced": [

    "Balanced",  /* L1 */

    "Balanced",  /* L2 */

    "Balanced",  /* L3 */

    "Balanced",  /* L4 */

    "Balanced",  /* L5 */

    "Well-Rounded",  /* L6 */

    "Balanced Development",  /* L7 */

    "Equilibrated Adaptation",  /* L8 */

    "Harmonized Multi-Qualities Development",  /* L9 */

    "Equilibrated Multi-Faceted Adaptation Matrix"  /* L10 */

  ],

  "Beginner": [

    "Beginner",  /* L1 */

    "Beginner",  /* L2 */

    "Beginner",  /* L3 */

    "Beginner",  /* L4 */

    "Beginner",  /* L5 */

    "Novice",  /* L6 */

    "Beginner Tier",  /* L7 */

    "Entry-Level Trainee",  /* L8 */

    "Initial-Phase Practitioner",  /* L9 */

    "Neophyte Of Physical Culture"  /* L10 */

  ],

  "Easy": [

    "Easy",  /* L1 */

    "Easy",  /* L2 */

    "Easy",  /* L3 */

    "Easy",  /* L4 */

    "Easy",  /* L5 */

    "Light",  /* L6 */

    "Easy Effort",  /* L7 */

    "Low Exertion Level",  /* L8 */

    "Minimal Perceived Exertion Band",  /* L9 */

    "Negligible Sensed Exertion Calibration"  /* L10 */

  ],

  "Medium": [

    "Medium",  /* L1 */

    "Medium",  /* L2 */

    "Medium",  /* L3 */

    "Medium",  /* L4 */

    "Medium",  /* L5 */

    "Moderate",  /* L6 */

    "Medium Effort",  /* L7 */

    "Moderate Exertion",  /* L8 */

    "Intermediate Exertion Band",  /* L9 */

    "Medial Sensed Exertion Calibration"  /* L10 */

  ],

  "Hard": [

    "Hard",  /* L1 */

    "Hard",  /* L2 */

    "Hard",  /* L3 */

    "Hard",  /* L4 */

    "Hard",  /* L5 */

    "Challenging",  /* L6 */

    "Hard Effort",  /* L7 */

    "High Exertion Level",  /* L8 */

    "Maximal Sustainable Exertion Band",  /* L9 */

    "Prodigious Sensed Exertion Calibration"  /* L10 */

  ],

  "No Workout Scheduled": [

    "Nothing Planned Yet",  /* L1 */

    "No Workout Scheduled",  /* L2 */

    "No Workout Scheduled",  /* L3 */

    "No Workout Scheduled",  /* L4 */

    "No Workout Scheduled",  /* L5 */

    "Rest Day — No Session",  /* L6 */

    "No Session Currently Scheduled",  /* L7 */

    "No Training Bout Prescribed Today",  /* L8 */

    "Nil Structured Exercise Bout Scheduled",  /* L9 */

    "Vacuous Exercise Prescription For This Diurnal Cycle"  /* L10 */

  ],

  "Generate a workout to get started.": [

    "Tap below to make your workout!",  /* L1 */

    "Generate a workout to get started.",  /* L2 */

    "Generate a workout to get started.",  /* L3 */

    "Generate a workout to get started.",  /* L4 */

    "Generate a workout to get started.",  /* L5 */

    "Produce a session to initiate training.",  /* L6 */

    "Render a training plan to commence.",  /* L7 */

    "Formulate a training bout to inaugurate practice.",  /* L8 */

    "Synthesize a structured regimen to inaugurate training.",  /* L9 */

    "Fabricate A Bespoke Exercise Regimen To Commence Thine Physical Journey."  /* L10 */

  ],

  "Great job!": [

    "You did it!",  /* L1 */

    "Great job!",  /* L2 */

    "Great job!",  /* L3 */

    "Great job!",  /* L4 */

    "Great job!",  /* L5 */

    "Excellent work!",  /* L6 */

    "Outstanding execution!",  /* L7 */

    "Commendable performance!",  /* L8 */

    "Exemplary athletic execution!",  /* L9 */

    "Most superlative exertion, verily!"  /* L10 */

  ],

  "Ready to Start": [

    "Ready to Start",  /* L1 */

    "Ready to Start",  /* L2 */

    "Ready to Start",  /* L3 */

    "Ready to Start",  /* L4 */

    "Ready to Start",  /* L5 */

    "Prepared To Begin",  /* L6 */

    "Prepared For Initiation",  /* L7 */

    "Poised For Session Initiation",  /* L8 */

    "Primed For Commencement Of Exertion",  /* L9 */

    "Arrayed For The Commencement Of Thine Endeavor"  /* L10 */

  ],

  "Completed:": [

    "Done:",  /* L1 */

    "Completed:",  /* L2 */

    "Completed:",  /* L3 */

    "Completed:",  /* L4 */

    "Completed:",  /* L5 */

    "Finished:",  /* L6 */

    "Brought To Completion:",  /* L7 */

    "Marked As Completed:",  /* L8 */

    "Rendered Unto Completion:",  /* L9 */

    "Consummated Unto Full Completion:"  /* L10 */

  ],

  "Current phase:": [

    "Where you are:",  /* L1 */

    "Current phase:",  /* L2 */

    "Current phase:",  /* L3 */

    "Current phase:",  /* L4 */

    "Current phase:",  /* L5 */

    "Present block:",  /* L6 */

    "Present mesocycle phase:",  /* L7 */

    "Current periodization phase:",  /* L8 */

    "Extant periodization phase designation:",  /* L9 */

    "Presently Operative Periodization Phase:"  /* L10 */

  ],

  "Focus:": [

    "Focus:",  /* L1 */

    "Focus:",  /* L2 */

    "Focus:",  /* L3 */

    "Focus:",  /* L4 */

    "Focus:",  /* L5 */

    "Emphasis:",  /* L6 */

    "Primary emphasis:",  /* L7 */

    "Principal training emphasis:",  /* L8 */

    "Cardinal adaptive emphasis herein:",  /* L9 */

    "Preponderant Emphasis Of This Bout:"  /* L10 */

  ],

  "Intensity:": [

    "How hard:",  /* L1 */

    "Intensity:",  /* L2 */

    "Intensity:",  /* L3 */

    "Intensity:",  /* L4 */

    "Intensity:",  /* L5 */

    "Effort level:",  /* L6 */

    "Relative intensity:",  /* L7 */

    "Training intensity quotient:",  /* L8 */

    "Relative load intensity parameter:",  /* L9 */

    "Relative Magnitude Of Exertion:"  /* L10 */

  ],

  "Recommended rest:": [

    "Rest time:",  /* L1 */

    "Recommended rest:",  /* L2 */

    "Recommended rest:",  /* L3 */

    "Recommended rest:",  /* L4 */

    "Recommended rest:",  /* L5 */

    "Suggested recovery window:",  /* L6 */

    "Recommended inter-set recovery:",  /* L7 */

    "Advised inter-effort recovery interval:",  /* L8 */

    "Prescribed Inter-Set Recuperation Interval:",  /* L9 */

    "Proscribed Inter-Effort Repose Interval:"  /* L10 */

  ],

  "Train it": [

    "Train it",  /* L1 */

    "Train It",  /* L2 */

    "Train It",  /* L3 */

    "Train It",  /* L4 */

    "Train it",  /* L5 */

    "Target This",  /* L6 */

    "Address This Weakness",  /* L7 */

    "Remediate This Deficiency",  /* L8 */

    "Remediate This Deficit Systematically",  /* L9 */

    "Effect Remediation Of This Deficiency"  /* L10 */

  ],

  "No exercises found": [

    "Nothing here",  /* L1 */

    "No exercises found",  /* L2 */

    "No exercises found",  /* L3 */

    "No exercises found",  /* L4 */

    "No exercises found",  /* L5 */

    "Nil results returned",  /* L6 */

    "No matching exercises found",  /* L7 */

    "Zero exercises satisfied the query",  /* L8 */

    "No movements matched your parameters",  /* L9 */

    "Nil Movements Satisfied Thy Search Parameters"  /* L10 */

  ],

  "No exercises match your search": [

    "Can't find it",  /* L1 */

    "No exercises match your search",  /* L2 */

    "No exercises match your search",  /* L3 */

    "No exercises match your search",  /* L4 */

    "No exercises match your search",  /* L5 */

    "Nothing matches that filter",  /* L6 */

    "No movements match your criteria",  /* L7 */

    "Zero movements satisfied your parameters",  /* L8 */

    "Nil movements corresponded to your filters",  /* L9 */

    "No Movements Answered To Thy Inquiry"  /* L10 */

  ],

  "Symptoms today:": [

    "How you feel:",  /* L1 */

    "Symptoms today:",  /* L2 */

    "Symptoms today:",  /* L3 */

    "Symptoms today:",  /* L4 */

    "Symptoms today:",  /* L5 */

    "Present complaints:",  /* L6 */

    "Symptoms reported today:",  /* L7 */

    "Subjective symptoms today:",  /* L8 */

    "Current symptomatic self-report:",  /* L9 */

    "Presentment Of Subjective Symptomatology:"  /* L10 */

  ],

  "Category recovery": [

    "Category recovery",  /* L1 */

    "Muscle recovery",  /* L2 */

    "Category recovery",  /* L3 */

    "Category recovery",  /* L4 */

    "Category recovery",  /* L5 */

    "Group recovery",  /* L6 */

    "Category recovery status",  /* L7 */

    "Muscle category recuperation",  /* L8 */

    "Recovery status by muscle category",  /* L9 */

    "Recuperative Disposition By Muscular Category"  /* L10 */

  ],

  "Muscle Groups:": [

    "Muscles:",  /* L1 */

    "Muscle Groups:",  /* L2 */

    "Muscle Groups:",  /* L3 */

    "Muscle Groups:",  /* L4 */

    "Muscle Groups:",  /* L5 */

    "Musculature:",  /* L6 */

    "Muscle group involvement:",  /* L7 */

    "Muscular groups engaged:",  /* L8 */

    "Musculature implicated herein:",  /* L9 */

    "Muscular Groupings Implicated:"  /* L10 */

  ],

  "Target muscles:": [

    "Works:",  /* L1 */

    "Target muscles:",  /* L2 */

    "Target muscles:",  /* L3 */

    "Target muscles:",  /* L4 */

    "Target muscles:",  /* L5 */

    "Primary movers:",  /* L6 */

    "Target musculature:",  /* L7 */

    "Primary target musculature:",  /* L8 */

    "Principal motor musculature engaged:",  /* L9 */

    "Preponderant Motor Musculature Engaged:"  /* L10 */

  ],

  "Exercise Library Management": [

    "My Exercises",  /* L1 */

    "Manage Exercises",  /* L2 */

    "Exercise Library Management",  /* L3 */

    "Exercise Library Management",  /* L4 */

    "Exercise Library Management",  /* L5 */

    "Exercise Data Controls",  /* L6 */

    "Exercise Library Administration",  /* L7 */

    "Exercise Repository Administration",  /* L8 */

    "Movement Repository Administration Panel",  /* L9 */

    "Administration Of Thine Exercise Repository"  /* L10 */

  ],

  "Keep screen awake during workout": [

    "Keep screen on",  /* L1 */

    "Keep screen awake during workout",  /* L2 */

    "Keep screen awake during workout",  /* L3 */

    "Keep screen awake during workout",  /* L4 */

    "Keep screen awake during workout",  /* L5 */

    "Prevent display sleep while training",  /* L6 */

    "Keep display awake during sessions",  /* L7 */

    "Inhibit display sleep during exercise",  /* L8 */

    "Prevent Display Somnolence During Exertion",  /* L9 */

    "Forestall Display Somnolence During Thine Endeavor"  /* L10 */

  ],

  "Preferred Workout Days:": [

    "Training days:",  /* L1 */

    "Preferred Workout Days:",  /* L2 */

    "Preferred Workout Days:",  /* L3 */

    "Preferred Workout Days:",  /* L4 */

    "Preferred Workout Days:",  /* L5 */

    "Scheduled training days:",  /* L6 */

    "Preferred training weekdays:",  /* L7 */

    "Designated training weekdays:",  /* L8 */

    "Designated Diurnal Training Cadence:",  /* L9 */

    "Designated Diurnal Cadence For Thine Training:"  /* L10 */

  ],

  "Birth Date": [

    "Birthday",  /* L1 */

    "Birth Date",  /* L2 */

    "Birth Date",  /* L3 */

    "Birth Date",  /* L4 */

    "Birth Date",  /* L5 */

    "Date of birth",  /* L6 */

    "Date of birth record",  /* L7 */

    "Chronological birth date",  /* L8 */

    "Recorded date of parturition",  /* L9 */

    "Date Of Thine Advent Into This World"  /* L10 */

  ],

  "Gender:": [

    "Gender:",  /* L1 */

    "Gender:",  /* L2 */

    "Gender:",  /* L3 */

    "Gender:",  /* L4 */

    "Gender:",  /* L5 */

    "Gender identity:",  /* L6 */

    "Gender identity field:",  /* L7 */

    "Self-described gender:",  /* L8 */

    "Declarative gender identification:",  /* L9 */

    "One's Self-Declared Gender Identification:"  /* L10 */

  ],

  "Male": [

    "Male",  /* L1 */

    "Male",  /* L2 */

    "Male",  /* L3 */

    "Male",  /* L4 */

    "Male",  /* L5 */

    "Male",  /* L6 */

    "Male",  /* L7 */

    "Male",  /* L8 */

    "Male",  /* L9 */

    "Male"  /* L10 */

  ],

  "Female": [

    "Female",  /* L1 */

    "Female",  /* L2 */

    "Female",  /* L3 */

    "Female",  /* L4 */

    "Female",  /* L5 */

    "Female",  /* L6 */

    "Female",  /* L7 */

    "Female",  /* L8 */

    "Female",  /* L9 */

    "Female"  /* L10 */

  ],

  "Upgrade to Pro": [

    "Get Everything",  /* L1 */

    "Upgrade to Pro",  /* L2 */

    "Upgrade to Pro",  /* L3 */

    "Upgrade to Pro",  /* L4 */

    "Upgrade to Pro",  /* L5 */

    "Unlock Full Power",  /* L6 */

    "Unlock Professional Tier",  /* L7 */

    "Ascend To Professional Tier",  /* L8 */

    "Attain The Professional Entitlement",  /* L9 */

    "Attain Thine Full Professional Entitlement"  /* L10 */

  ],

  "Free": [

    "Free",  /* L1 */

    "Free",  /* L2 */

    "Free",  /* L3 */

    "Free",  /* L4 */

    "Free",  /* L5 */

    "Free",  /* L6 */

    "Free",  /* L7 */

    "Complimentary",  /* L8 */

    "Complimentary Tier",  /* L9 */

    "Complimentary Access Tier"  /* L10 */

  ],

  "Pro": [

    "Pro",  /* L1 */

    "Pro",  /* L2 */

    "Pro",  /* L3 */

    "Pro",  /* L4 */

    "Pro",  /* L5 */

    "Pro",  /* L6 */

    "Professional",  /* L7 */

    "Professional Tier",  /* L8 */

    "Professional Entitlement",  /* L9 */

    "The Professional Entitlement"  /* L10 */

  ],

  "per month": [

    "a month",  /* L1 */

    "per month",  /* L2 */

    "per month",  /* L3 */

    "per month",  /* L4 */

    "per month",  /* L5 */

    "monthly",  /* L6 */

    "per billing month",  /* L7 */

    "per calendar month",  /* L8 */

    "per remuneration cycle month",  /* L9 */

    "per calendar month of enrollment"  /* L10 */

  ],

  "Activate": [

    "Turn On",  /* L1 */

    "Activate",  /* L2 */

    "Activate",  /* L3 */

    "Activate",  /* L4 */

    "Activate",  /* L5 */

    "Enable",  /* L6 */

    "Activate License",  /* L7 */

    "Render License Active",  /* L8 */

    "Activate Thine Entitlement",  /* L9 */

    "Render Operative Thine Entitlement"  /* L10 */

  ],

  "Subscribe": [

    "Join",  /* L1 */

    "Subscribe",  /* L2 */

    "Subscribe",  /* L3 */

    "Subscribe",  /* L4 */

    "Subscribe",  /* L5 */

    "Enroll",  /* L6 */

    "Take Subscription",  /* L7 */

    "Enter Subscription Agreement",  /* L8 */

    "Enter Thine Subscription Agreement",  /* L9 */

    "Enroll Thyself In Ongoing Subscription"  /* L10 */

  ],

  "Get Started": [

    "Let's Begin",  /* L1 */

    "Get Started",  /* L2 */

    "Get Started",  /* L3 */

    "Get Started",  /* L4 */

    "Get Started",  /* L5 */

    "Begin Setup",  /* L6 */

    "Initiate Onboarding",  /* L7 */

    "Commence Initial Configuration",  /* L8 */

    "Commence Thine Preliminary Configuration",  /* L9 */

    "Embark Upon Preliminary Configuration"  /* L10 */

  ],

  "Continue": [

    "Keep Going",  /* L1 */

    "Continue",  /* L2 */

    "Continue",  /* L3 */

    "Continue",  /* L4 */

    "Continue",  /* L5 */

    "Proceed",  /* L6 */

    "Proceed Forward",  /* L7 */

    "Advance To Next Step",  /* L8 */

    "Advance Unto The Succeeding Stage",  /* L9 */

    "Proceed Unto The Succeeding Juncture"  /* L10 */

  ],

  "Back": [

    "Back",  /* L1 */

    "Back",  /* L2 */

    "Back",  /* L3 */

    "Back",  /* L4 */

    "Back",  /* L5 */

    "Return",  /* L6 */

    "Go Back",  /* L7 */

    "Revert To Prior Step",  /* L8 */

    "Revert To The Antecedent Stage",  /* L9 */

    "Return To The Antecedent Juncture"  /* L10 */

  ],

  "Finish": [

    "Done!",  /* L1 */

    "Finish",  /* L2 */

    "Finish",  /* L3 */

    "Finish",  /* L4 */

    "Finish",  /* L5 */

    "Complete Setup",  /* L6 */

    "Finalize Configuration",  /* L7 */

    "Conclude Initial Configuration",  /* L8 */

    "Consummate The Configuration Process",  /* L9 */

    "Bring Configuration Unto Consummation"  /* L10 */

  ],

  "I agree": [

    "I agree",  /* L1 */

    "I agree",  /* L2 */

    "I agree",  /* L3 */

    "I agree",  /* L4 */

    "I agree",  /* L5 */

    "I consent",  /* L6 */

    "I hereby agree",  /* L7 */

    "I do hereby consent",  /* L8 */

    "I herein express my consent",  /* L9 */

    "I Hereby Express Mine Assent"  /* L10 */

  ],

  "Next": [

    "Next",  /* L1 */

    "Next",  /* L2 */

    "Next",  /* L3 */

    "Next",  /* L4 */

    "Next",  /* L5 */

    "Onward",  /* L6 */

    "Next Step",  /* L7 */

    "Proceed To Next Stage",  /* L8 */

    "Advance Unto Subsequent Stage",  /* L9 */

    "Advance Unto The Subsequent Juncture"  /* L10 */

  ],

  "Skip": [

    "Skip",  /* L1 */

    "Skip",  /* L2 */

    "Skip",  /* L3 */

    "Skip",  /* L4 */

    "Skip",  /* L5 */

    "Omit",  /* L6 */

    "Skip For Now",  /* L7 */

    "Defer This Step",  /* L8 */

    "Defer This Provisional Step",  /* L9 */

    "Defer This Unto A Later Juncture"  /* L10 */

  ],

  "Win of the day": [

    "Your win today",  /* L1 */

    "Win of the day",  /* L2 */

    "Win of the day",  /* L3 */

    "Win of the day",  /* L4 */

    "Win of the day",  /* L5 */

    "Today's victory",  /* L6 */

    "Today's Achievement",  /* L7 */

    "Today's Merited Victory",  /* L8 */

    "Today's Deserved Victory",  /* L9 */

    "This Day's Deserved Triumph"  /* L10 */

  ],

  "Pro Tips": [

    "Good Ideas",  /* L1 */

    "Pro Tips",  /* L2 */

    "Pro Tips",  /* L3 */

    "Pro Tips",  /* L4 */

    "Pro Tips",  /* L5 */

    "Coach's Notes",  /* L6 */

    "Professional Insights",  /* L7 */

    "Specialist Field Notes",  /* L8 */

    "Expert Practitioner Field Notes",  /* L9 */

    "Precepts From Master Practitioners"  /* L10 */

  ],

  "Badges": [

    "Badges",  /* L1 */

    "Badges",  /* L2 */

    "Badges",  /* L3 */

    "Badges",  /* L4 */

    "Badges",  /* L5 */

    "Achievements",  /* L6 */

    "Achievement Gallery",  /* L7 */

    "Accomplishment Collection",  /* L8 */

    "Attained Accomplishments Gallery",  /* L9 */

    "Galleria Of Attained Distinctions"  /* L10 */

  ],

  "Backup": [

    "Backup",  /* L1 */

    "Backup",  /* L2 */

    "Backup",  /* L3 */

    "Backup",  /* L4 */

    "Backup",  /* L5 */

    "Data Backup",  /* L6 */

    "Backup & Restore",  /* L7 */

    "Data Preservation Copy",  /* L8 */

    "Redundant Data Preservation Copy",  /* L9 */

    "Redundant Preservation Of Thine Records"  /* L10 */

  ],

  "hypertrophy": [

    "muscle growth",  /* L1 */

    "muscle growth",  /* L2 */

    "muscle growth",  /* L3 */

    "muscle growth",  /* L4 */

    "hypertrophy",  /* L5 */

    "hypertrophy",  /* L6 */

    "muscle size gains",  /* L7 */

    "myofibrillar hypertrophy",  /* L8 */

    "sarcoplasmic and myofibrillar hypertrophy",  /* L9 */

    "augmentation of muscular cross-sectional area"  /* L10 */

  ],

  "deload": [

    "easy week",  /* L1 */

    "easy week",  /* L2 */

    "lighter week",  /* L3 */

    "deload week",  /* L4 */

    "deload",  /* L5 */

    "deload",  /* L6 */

    "planned recovery week",  /* L7 */

    "recovery-focused mesocycle",  /* L8 */

    "intentional detraining microcycle",  /* L9 */

    "recuperative attenuation of training load"  /* L10 */

  ],

  "taper": [

    "wind down",  /* L1 */

    "wind down",  /* L2 */

    "ease off",  /* L3 */

    "taper period",  /* L4 */

    "taper",  /* L5 */

    "taper",  /* L6 */

    "pre-event reduction",  /* L7 */

    "progressive volume reduction",  /* L8 */

    "pre-competition volume attenuation",  /* L9 */

    "progressive attenuation of prescriptive load"  /* L10 */

  ],

  "1RM": [

    "best lift",  /* L1 */

    "best lift",  /* L2 */

    "one-rep max",  /* L3 */

    "one-rep max",  /* L4 */

    "1RM",  /* L5 */

    "1RM",  /* L6 */

    "one-repetition maximum",  /* L7 */

    "one-repetition maximum estimate",  /* L8 */

    "estimated maximal single-repetition capacity",  /* L9 */

    "estimation of maximal single-repetition strength"  /* L10 */

  ],

  "1RMs": [

    "best lifts",  /* L1 */

    "best lifts",  /* L2 */

    "one-rep maxes",  /* L3 */

    "one-rep maxes",  /* L4 */

    "1RMs",  /* L5 */

    "1RMs",  /* L6 */

    "one-repetition maximums",  /* L7 */

    "one-repetition maximum estimates",  /* L8 */

    "estimated maximal single-repetition capacities",  /* L9 */

    "estimations of maximal single-repetition strength"  /* L10 */

  ],

  "RPE": [

    "effort",  /* L1 */

    "effort score",  /* L2 */

    "effort score",  /* L3 */

    "RPE",  /* L4 */

    "RPE",  /* L5 */

    "RPE",  /* L6 */

    "rating of perceived exertion",  /* L7 */

    "ratings of perceived exertion",  /* L8 */

    "quantified perceptual exertion ratings",  /* L9 */

    "omnibus ratings of perceived exertion"  /* L10 */

  ],

  "volume": [

    "amount",  /* L1 */

    "amount",  /* L2 */

    "amount",  /* L3 */

    "volume",  /* L4 */

    "volume",  /* L5 */

    "volume",  /* L6 */

    "training volume",  /* L7 */

    "cumulative training volume",  /* L8 */

    "aggregate work-volume accumulation",  /* L9 */

    "aggregate tonnage-volume accumulation"  /* L10 */

  ],

  "tonnage": [

    "weight moved",  /* L1 */

    "weight moved",  /* L2 */

    "total weight",  /* L3 */

    "tonnage",  /* L4 */

    "tonnage",  /* L5 */

    "tonnage",  /* L6 */

    "total tonnage lifted",  /* L7 */

    "cumulative load tonnage",  /* L8 */

    "aggregate load-displacement product",  /* L9 */

    "cumulative load-displacement quotient"  /* L10 */

  ],

  "reps": [

    "times",  /* L1 */

    "reps",  /* L2 */

    "reps",  /* L3 */

    "reps",  /* L4 */

    "reps",  /* L5 */

    "reps",  /* L6 */

    "repetitions",  /* L7 */

    "repetition allotment",  /* L8 */

    "prescribed repetition continuum",  /* L9 */

    "enumerated repetition prescriptions"  /* L10 */

  ],

  "rep": [

    "time",  /* L1 */

    "rep",  /* L2 */

    "rep",  /* L3 */

    "rep",  /* L4 */

    "rep",  /* L5 */

    "rep",  /* L6 */

    "repetition",  /* L7 */

    "single repetition",  /* L8 */

    "individual repetition cycle",  /* L9 */

    "singular repetition event"  /* L10 */

  ],

  "set": [

    "round",  /* L1 */

    "round",  /* L2 */

    "set",  /* L3 */

    "set",  /* L4 */

    "set",  /* L5 */

    "set",  /* L6 */

    "work set",  /* L7 */

    "prescription set",  /* L8 */

    "prescribed work set",  /* L9 */

    "enumerated work set"  /* L10 */

  ],

  "sets": [

    "rounds",  /* L1 */

    "rounds",  /* L2 */

    "sets",  /* L3 */

    "sets",  /* L4 */

    "sets",  /* L5 */

    "sets",  /* L6 */

    "work sets",  /* L7 */

    "prescription sets",  /* L8 */

    "prescribed work sets",  /* L9 */

    "enumerated work sets"  /* L10 */

  ],

  "fatigue": [

    "tiredness",  /* L1 */

    "tiredness",  /* L2 */

    "fatigue",  /* L3 */

    "fatigue",  /* L4 */

    "fatigue",  /* L5 */

    "fatigue",  /* L6 */

    "neuromuscular fatigue",  /* L7 */

    "accumulated fatigue load",  /* L8 */

    "multisystem fatigue accumulation",  /* L9 */

    "cumulative neuromuscular fatigue burden"  /* L10 */

  ],

  "recovery": [

    "getting better",  /* L1 */

    "recovery",  /* L2 */

    "recovery",  /* L3 */

    "recovery",  /* L4 */

    "recovery",  /* L5 */

    "recovery",  /* L6 */

    "recuperative capacity",  /* L7 */

    "recuperative processes",  /* L8 */

    "restorative physiological processes",  /* L9 */

    "recuperative homeostatic restoration"  /* L10 */

  ],

  "warm-up": [

    "get loose",  /* L1 */

    "get loose",  /* L2 */

    "warm-up",  /* L3 */

    "warm-up",  /* L4 */

    "warm-up",  /* L5 */

    "warm-up",  /* L6 */

    "preparatory warm-up",  /* L7 */

    "progressive warm-up protocol",  /* L8 */

    "ramped preparatory protocol",  /* L9 */

    "graduated preparatory mobilization protocol"  /* L10 */

  ],

  "cooldown": [

    "cool down",  /* L1 */

    "cool down",  /* L2 */

    "cooldown",  /* L3 */

    "cooldown",  /* L4 */

    "cooldown",  /* L5 */

    "cooldown",  /* L6 */

    "cool-down period",  /* L7 */

    "structured cool-down",  /* L8 */

    "parasympathetic downshift protocol",  /* L9 */

    "facilitated parasympathetic restitution period"  /* L10 */

  ],

  "mobility": [

    "moving freely",  /* L1 */

    "moving freely",  /* L2 */

    "mobility",  /* L3 */

    "mobility",  /* L4 */

    "mobility",  /* L5 */

    "mobility",  /* L6 */

    "functional mobility",  /* L7 */

    "articular mobility work",  /* L8 */

    "active range-of-motion development",  /* L9 */

    "articular range-of-motion amelioration"  /* L10 */

  ],

  "flexibility": [

    "being bendy",  /* L1 */

    "flexibility",  /* L2 */

    "flexibility",  /* L3 */

    "flexibility",  /* L4 */

    "flexibility",  /* L5 */

    "flexibility",  /* L6 */

    "range-of-flexibility",  /* L7 */

    "passive extensibility",  /* L8 */

    "myofascial extensibility qualities",  /* L9 */

    "passive myofascial extensibility capacity"  /* L10 */

  ],

  "tempo": [

    "speed pattern",  /* L1 */

    "speed pattern",  /* L2 */

    "tempo",  /* L3 */

    "tempo",  /* L4 */

    "tempo",  /* L5 */

    "tempo",  /* L6 */

    "lifting tempo",  /* L7 */

    "prescribed movement tempo",  /* L8 */

    "segmented temporal execution scheme",  /* L9 */

    "enumerated temporal execution prescription"  /* L10 */

  ],

  "intensity": [

    "effort",  /* L1 */

    "effort",  /* L2 */

    "intensity",  /* L3 */

    "intensity",  /* L4 */

    "intensity",  /* L5 */

    "intensity",  /* L6 */

    "relative intensity",  /* L7 */

    "training intensity quotient",  /* L8 */

    "relative load intensity parameter",  /* L9 */

    "relative magnitude of exertion"  /* L10 */

  ],

  "load": [

    "weight",  /* L1 */

    "weight",  /* L2 */

    "load",  /* L3 */

    "load",  /* L4 */

    "load",  /* L5 */

    "load",  /* L6 */

    "external load",  /* L7 */

    "prescribed external load",  /* L8 */

    "magnitude of external resistance",  /* L9 */

    "quantified external resistance magnitude"  /* L10 */

  ],

  "progression": [

    "getting better",  /* L1 */

    "progression",  /* L2 */

    "progression",  /* L3 */

    "progression",  /* L4 */

    "progression",  /* L5 */

    "progression",  /* L6 */

    "progressive overload",  /* L7 */

    "systematic overload progression",  /* L8 */

    "longitudinal overload progression",  /* L9 */

    "systematized progressive overload paradigm"  /* L10 */

  ],

  "regression": [

    "easier version",  /* L1 */

    "easier version",  /* L2 */

    "regression",  /* L3 */

    "regression",  /* L4 */

    "regression",  /* L5 */

    "regression",  /* L6 */

    "exercise regression",  /* L7 */

    "scaled-down variant",  /* L8 */

    "regressed movement variant",  /* L9 */

    "attenuated kinetic variant"  /* L10 */

  ],

  "eccentric": [

    "lowering part",  /* L1 */

    "lowering part",  /* L2 */

    "lowering phase",  /* L3 */

    "eccentric",  /* L4 */

    "eccentric",  /* L5 */

    "eccentric",  /* L6 */

    "eccentric phase",  /* L7 */

    "eccentric muscle action",  /* L8 */

    "lengthening muscular action",  /* L9 */

    "eccentric lengthening musculature action"  /* L10 */

  ],

  "concentric": [

    "lifting part",  /* L1 */

    "lifting part",  /* L2 */

    "lifting phase",  /* L3 */

    "concentric",  /* L4 */

    "concentric",  /* L5 */

    "concentric",  /* L6 */

    "concentric phase",  /* L7 */

    "concentric muscle action",  /* L8 */

    "shortening muscular action",  /* L9 */

    "concentric shortening musculature action"  /* L10 */

  ],

  "isometric": [

    "holding still",  /* L1 */

    "holding still",  /* L2 */

    "hold",  /* L3 */

    "isometric",  /* L4 */

    "isometric",  /* L5 */

    "isometric",  /* L6 */

    "isometric contraction",  /* L7 */

    "static muscle action",  /* L8 */

    "isometric muscular contraction",  /* L9 */

    "quasi-isometric muscular contraction"  /* L10 */

  ],

  "plyometric": [

    "jump training",  /* L1 */

    "jump training",  /* L2 */

    "plyometric",  /* L3 */

    "plyometric",  /* L4 */

    "plyometric",  /* L5 */

    "plyometric",  /* L6 */

    "plyometric drill",  /* L7 */

    "reactive jump training",  /* L8 */

    "stretch-shortening cycle drill",  /* L9 */

    "reactive stretch-shortening exercise"  /* L10 */

  ],

  "power": [

    "explosiveness",  /* L1 */

    "explosiveness",  /* L2 */

    "power",  /* L3 */

    "power",  /* L4 */

    "power",  /* L5 */

    "power",  /* L6 */

    "muscular power",  /* L7 */

    "rate of force development",  /* L8 */

    "rapid force-production capacity",  /* L9 */

    "rate of force development capacity"  /* L10 */

  ],

  "agility": [

    "quick feet",  /* L1 */

    "quick feet",  /* L2 */

    "agility",  /* L3 */

    "agility",  /* L4 */

    "agility",  /* L5 */

    "agility",  /* L6 */

    "agility skills",  /* L7 */

    "change-of-direction ability",  /* L8 */

    "deceleration and reacceleration skill",  /* L9 */

    "multidirectional change-of-direction proficiency"  /* L10 */

  ],

  "balance": [

    "staying steady",  /* L1 */

    "staying steady",  /* L2 */

    "balance",  /* L3 */

    "balance",  /* L4 */

    "balance",  /* L5 */

    "balance",  /* L6 */

    "postural balance",  /* L7 */

    "equilibrial control",  /* L8 */

    "static and dynamic equilibrium",  /* L9 */

    "proprioceptive equilibrium maintenance"  /* L10 */

  ],

  "coordination": [

    "body control",  /* L1 */

    "body control",  /* L2 */

    "coordination",  /* L3 */

    "coordination",  /* L4 */

    "coordination",  /* L5 */

    "coordination",  /* L6 */

    "motor coordination",  /* L7 */

    "inter-limb coordination",  /* L8 */

    "intramuscular and intermuscular timing",  /* L9 */

    "intersegmental motor synchronization"  /* L10 */

  ],

  "reaction": [

    "quick response",  /* L1 */

    "quick response",  /* L2 */

    "reaction",  /* L3 */

    "reaction",  /* L4 */

    "reaction",  /* L5 */

    "reaction",  /* L6 */

    "reaction time",  /* L7 */

    "reactive response speed",  /* L8 */

    "stimulus-response latency",  /* L9 */

    "sensorimotor response latency"  /* L10 */

  ],

  "aerobic": [

    "breathing easy",  /* L1 */

    "breathing easy",  /* L2 */

    "aerobic",  /* L3 */

    "aerobic",  /* L4 */

    "aerobic",  /* L5 */

    "aerobic",  /* L6 */

    "aerobic system",  /* L7 */

    "oxidative energy system",  /* L8 */

    "aerobic metabolic pathway",  /* L9 */

    "oxidative phosphorylation pathway"  /* L10 */

  ],

  "anaerobic": [

    "sprint effort",  /* L1 */

    "sprint effort",  /* L2 */

    "anaerobic",  /* L3 */

    "anaerobic",  /* L4 */

    "anaerobic",  /* L5 */

    "anaerobic",  /* L6 */

    "anaerobic system",  /* L7 */

    "glycolytic energy system",  /* L8 */

    "anaerobic glycolytic pathway",  /* L9 */

    "substrate-level phosphorylation pathway"  /* L10 */

  ],

  "cardio": [

    "heart exercise",  /* L1 */

    "cardio",  /* L2 */

    "cardio",  /* L3 */

    "cardio",  /* L4 */

    "cardio",  /* L5 */

    "cardio",  /* L6 */

    "cardiovascular exercise",  /* L7 */

    "cardiorespiratory conditioning",  /* L8 */

    "aerobic conditioning modalities",  /* L9 */

    "cardiorespiratory conditioning modalities"  /* L10 */

  ],

  "fast-twitch": [

    "quick muscle",  /* L1 */

    "quick muscle",  /* L2 */

    "fast-twitch",  /* L3 */

    "fast-twitch",  /* L4 */

    "fast-twitch",  /* L5 */

    "fast-twitch",  /* L6 */

    "fast-twitch fibers",  /* L7 */

    "type II muscle fibers",  /* L8 */

    "high-threshold motor units",  /* L9 */

    "high-threshold type II motor units"  /* L10 */

  ],

  "slow-twitch": [

    "steady muscle",  /* L1 */

    "steady muscle",  /* L2 */

    "slow-twitch",  /* L3 */

    "slow-twitch",  /* L4 */

    "slow-twitch",  /* L5 */

    "slow-twitch",  /* L6 */

    "slow-twitch fibers",  /* L7 */

    "type I muscle fibers",  /* L8 */

    "low-threshold motor units",  /* L9 */

    "low-threshold type I motor units"  /* L10 */

  ],

  "soreness": [

    "achy muscles",  /* L1 */

    "achy muscles",  /* L2 */

    "soreness",  /* L3 */

    "soreness",  /* L4 */

    "soreness",  /* L5 */

    "soreness",  /* L6 */

    "delayed onset soreness",  /* L7 */

    "delayed-onset muscle soreness",  /* L8 */

    "delayed-onset muscular soreness",  /* L9 */

    "delayed-onset myalgic symptomatology"  /* L10 */

  ],

  "barbell": [

    "long bar",  /* L1 */

    "long bar",  /* L2 */

    "barbell",  /* L3 */

    "barbell",  /* L4 */

    "barbell",  /* L5 */

    "barbell",  /* L6 */

    "olympic barbell",  /* L7 */

    "loaded barbell implement",  /* L8 */

    "collapsible loaded barbell implement",  /* L9 */

    "standardized long-bar loading implement"  /* L10 */

  ],

  "dumbbell": [

    "hand weight",  /* L1 */

    "hand weight",  /* L2 */

    "dumbbell",  /* L3 */

    "dumbbell",  /* L4 */

    "dumbbell",  /* L5 */

    "dumbbell",  /* L6 */

    "free-weight dumbbell",  /* L7 */

    "unilateral loading implement",  /* L8 */

    "handheld bilateral loading implements",  /* L9 */

    "dis unilateral handheld loading implements"  /* L10 */

  ],

  "kettlebell": [

    "ball weight",  /* L1 */

    "ball weight",  /* L2 */

    "kettlebell",  /* L3 */

    "kettlebell",  /* L4 */

    "kettlebell",  /* L5 */

    "kettlebell",  /* L6 */

    "kettlebell implement",  /* L7 */

    "offset-load implement",  /* L8 */

    "displaced-center-of-mass implement",  /* L9 */

    "cantilevered center-of-mass implement"  /* L10 */

  ],

  "weight": [

    "weight",  /* L1 */

    "weight",  /* L2 */

    "weight",  /* L3 */

    "weight",  /* L4 */

    "weight",  /* L5 */

    "weight",  /* L6 */

    "resistance",  /* L7 */

    "external resistance",  /* L8 */

    "applied external resistance",  /* L9 */

    "quantified external resistance"  /* L10 */

  ],

  "weights": [

    "weights",  /* L1 */

    "weights",  /* L2 */

    "weights",  /* L3 */

    "weights",  /* L4 */

    "weights",  /* L5 */

    "weights",  /* L6 */

    "resistances",  /* L7 */

    "external resistances",  /* L8 */

    "applied external resistances",  /* L9 */

    "quantified external resistances"  /* L10 */

  ],

  "muscle": [

    "muscle",  /* L1 */

    "muscle",  /* L2 */

    "muscle",  /* L3 */

    "muscle",  /* L4 */

    "muscle",  /* L5 */

    "muscle",  /* L6 */

    "musculature",  /* L7 */

    "skeletal muscle",  /* L8 */

    "skeletal musculature",  /* L9 */

    "skeletal muscular tissue"  /* L10 */

  ],

  "muscles": [

    "muscles",  /* L1 */

    "muscles",  /* L2 */

    "muscles",  /* L3 */

    "muscles",  /* L4 */

    "muscles",  /* L5 */

    "muscles",  /* L6 */

    "musculature",  /* L7 */

    "skeletal muscles",  /* L8 */

    "skeletal musculature",  /* L9 */

    "skeletal muscular tissues"  /* L10 */

  ],

  "exercise": [

    "move",  /* L1 */

    "move",  /* L2 */

    "exercise",  /* L3 */

    "exercise",  /* L4 */

    "exercise",  /* L5 */

    "exercise",  /* L6 */

    "training exercise",  /* L7 */

    "resistance exercise",  /* L8 */

    "structured resistance exercise",  /* L9 */

    "prescribed resistance exercise bout"  /* L10 */

  ],

  "exercises": [

    "moves",  /* L1 */

    "moves",  /* L2 */

    "exercises",  /* L3 */

    "exercises",  /* L4 */

    "exercises",  /* L5 */

    "exercises",  /* L6 */

    "training exercises",  /* L7 */

    "resistance exercises",  /* L8 */

    "structured resistance exercises",  /* L9 */

    "prescribed resistance exercise bouts"  /* L10 */

  ],

  "workout": [

    "workout",  /* L1 */

    "workout",  /* L2 */

    "workout",  /* L3 */

    "workout",  /* L4 */

    "workout",  /* L5 */

    "workout",  /* L6 */

    "training session",  /* L7 */

    "structured training session",  /* L8 */

    "systematized exercise bout",  /* L9 */

    "systematized constitutional exercise bout"  /* L10 */

  ],

  "training": [

    "practice",  /* L1 */

    "practice",  /* L2 */

    "training",  /* L3 */

    "training",  /* L4 */

    "training",  /* L5 */

    "training",  /* L6 */

    "training program",  /* L7 */

    "systematic training",  /* L8 */

    "periodized training intervention",  /* L9 */

    "periodized physical preparation"  /* L10 */

  ],

  "strength": [

    "strength",  /* L1 */

    "strength",  /* L2 */

    "strength",  /* L3 */

    "strength",  /* L4 */

    "strength",  /* L5 */

    "strength",  /* L6 */

    "maximal strength",  /* L7 */

    "neuromuscular strength",  /* L8 */

    "maximal voluntary force production",  /* L9 */

    "maximal volitional force-generating capacity"  /* L10 */

  ],

  "rest": [

    "rest",  /* L1 */

    "rest",  /* L2 */

    "rest",  /* L3 */

    "rest",  /* L4 */

    "rest",  /* L5 */

    "rest",  /* L6 */

    "recovery interval",  /* L7 */

    "inter-set recovery",  /* L8 */

    "inter-effort recovery interval",  /* L9 */

    "inter-effort recuperation interval"  /* L10 */

  ],

  "streak": [

    "streak",  /* L1 */

    "streak",  /* L2 */

    "streak",  /* L3 */

    "streak",  /* L4 */

    "streak",  /* L5 */

    "streak",  /* L6 */

    "consistency run",  /* L7 */

    "consecutive-day adherence run",  /* L8 */

    "unbroken adherence sequence",  /* L9 */

    "uninterrupted diurnal adherence continuum"  /* L10 */

  ],

  "readiness": [

    "readiness",  /* L1 */

    "readiness",  /* L2 */

    "readiness",  /* L3 */

    "readiness",  /* L4 */

    "readiness",  /* L5 */

    "readiness",  /* L6 */

    "readiness status",  /* L7 */

    "physiological readiness",  /* L8 */

    "multifactorial readiness estimate",  /* L9 */

    "holistic somatic readiness appraisal"  /* L10 */

  ],

  "fiber": [

    "muscle thread",  /* L1 */

    "fiber",  /* L2 */

    "fiber",  /* L3 */

    "fiber",  /* L4 */

    "fiber",  /* L5 */

    "fiber",  /* L6 */

    "muscle fiber",  /* L7 */

    "muscle fiber type",  /* L8 */

    "skeletal muscle fiber profile",  /* L9 */

    "myofibrillar fiber-type profile"  /* L10 */

  ],

  "fibers": [

    "muscle threads",  /* L1 */

    "fibers",  /* L2 */

    "fibers",  /* L3 */

    "fibers",  /* L4 */

    "fibers",  /* L5 */

    "fibers",  /* L6 */

    "muscle fibers",  /* L7 */

    "muscle fiber types",  /* L8 */

    "skeletal muscle fiber profiles",  /* L9 */

    "myofibrillar fiber-type profiles"  /* L10 */

  ],

  "activation": [

    "waking up",  /* L1 */

    "waking up",  /* L2 */

    "activation",  /* L3 */

    "activation",  /* L4 */

    "activation",  /* L5 */

    "activation",  /* L6 */

    "muscle activation",  /* L7 */

    "motor unit recruitment",  /* L8 */

    "motor-unit recruitment patterns",  /* L9 */

    "motor-unit recruitment sequencing"  /* L10 */

  ],

  "overtraining": [

    "overdoing it",  /* L1 */

    "overdoing it",  /* L2 */

    "overtraining",  /* L3 */

    "overtraining",  /* L4 */

    "overtraining",  /* L5 */

    "overtraining",  /* L6 */

    "overtraining risk",  /* L7 */

    "overreaching syndrome",  /* L8 */

    "maladaptive overreaching state",  /* L9 */

    "maladaptive supercompensation failure state"  /* L10 */

  ],

  "plateau": [

    "stuck spot",  /* L1 */

    "stuck spot",  /* L2 */

    "plateau",  /* L3 */

    "plateau",  /* L4 */

    "plateau",  /* L5 */

    "plateau",  /* L6 */

    "progress plateau",  /* L7 */

    "adaptive stagnation phase",  /* L8 */

    "allostatic adaptation plateau",  /* L9 */

    "attenuated adaptive responsiveness phase"  /* L10 */

  ],

  "form": [

    "form",  /* L1 */

    "form",  /* L2 */

    "form",  /* L3 */

    "form",  /* L4 */

    "form",  /* L5 */

    "form",  /* L6 */

    "technique",  /* L7 */

    "movement technique",  /* L8 */

    "executive movement technique",  /* L9 */

    "kinetic execution technique"  /* L10 */

  ],

  "technique": [

    "technique",  /* L1 */

    "technique",  /* L2 */

    "technique",  /* L3 */

    "technique",  /* L4 */

    "technique",  /* L5 */

    "technique",  /* L6 */

    "movement skill",  /* L7 */

    "executive technique",  /* L8 */

    "refined movement execution",  /* L9 */

    "motor execution proficiency"  /* L10 */

  ],

  "bracing": [

    "stiffening up",  /* L1 */

    "stiffening up",  /* L2 */

    "bracing",  /* L3 */

    "bracing",  /* L4 */

    "bracing",  /* L5 */

    "bracing",  /* L6 */

    "core bracing",  /* L7 */

    "intra-abdominal bracing",  /* L8 */

    "trunk pressurization strategy",  /* L9 */

    "intra-abdominal pressurization strategy"  /* L10 */

  ],

  "grip": [

    "hold",  /* L1 */

    "hold",  /* L2 */

    "grip",  /* L3 */

    "grip",  /* L4 */

    "grip",  /* L5 */

    "grip",  /* L6 */

    "grip strength",  /* L7 */

    "grip mechanics",  /* L8 */

    "distal segment stabilization",  /* L9 */

    "distal prehensile stabilization"  /* L10 */

  ],

  "posture": [

    "posture",  /* L1 */

    "posture",  /* L2 */

    "posture",  /* L3 */

    "posture",  /* L4 */

    "posture",  /* L5 */

    "posture",  /* L6 */

    "body position",  /* L7 */

    "postural alignment",  /* L8 */

    "sagittal postural alignment",  /* L9 */

    "segmental postural orientation"  /* L10 */

  ],

  "core": [

    "middle body",  /* L1 */

    "core",  /* L2 */

    "core",  /* L3 */

    "core",  /* L4 */

    "core",  /* L5 */

    "core",  /* L6 */

    "core musculature",  /* L7 */

    "truncal stabilizers",  /* L8 */

    "lumbopelvic stabilizing complex",  /* L9 */

    "lumbopelvic stabilizing musculature"  /* L10 */

  ],

  "chest": [

    "chest",  /* L1 */

    "chest",  /* L2 */

    "chest",  /* L3 */

    "chest",  /* L4 */

    "chest",  /* L5 */

    "chest",  /* L6 */

    "pectorals",  /* L7 */

    "pectoral group",  /* L8 */

    "pectoral musculature",  /* L9 */

    "anterior thoracic musculature"  /* L10 */

  ],

  "back_lats": [

    "back",  /* L1 */

    "back",  /* L2 */

    "back",  /* L3 */

    "back",  /* L4 */

    "back_lats",  /* L5 */

    "back",  /* L6 */

    "latissimus",  /* L7 */

    "latissimus dorsi",  /* L8 */

    "latissimus dorsi complex",  /* L9 */

    "posterior thoracolumbar musculature"  /* L10 */

  ],

  "rhomboids_reardelts": [

    "upper back",  /* L1 */

    "upper back",  /* L2 */

    "upper back",  /* L3 */

    "upper back",  /* L4 */

    "rhomboids_reardelts",  /* L5 */

    "upper back",  /* L6 */

    "rhomboids & rear delts",  /* L7 */

    "rhomboid and posterior deltoid group",  /* L8 */

    "rhomboid and posterior deltoid complex",  /* L9 */

    "scapular retractor musculature"  /* L10 */

  ],

  "traps": [

    "traps",  /* L1 */

    "traps",  /* L2 */

    "traps",  /* L3 */

    "traps",  /* L4 */

    "traps",  /* L5 */

    "traps",  /* L6 */

    "trapezius",  /* L7 */

    "trapezius muscle",  /* L8 */

    "trapezius musculature",  /* L9 */

    "superior posterior cervical musculature"  /* L10 */

  ],

  "shoulders_anterior": [

    "front shoulders",  /* L1 */

    "front shoulders",  /* L2 */

    "front delts",  /* L3 */

    "front delts",  /* L4 */

    "shoulders_anterior",  /* L5 */

    "front delts",  /* L6 */

    "anterior deltoid",  /* L7 */

    "anterior deltoid group",  /* L8 */

    "anterior deltoid musculature",  /* L9 */

    "anterior glenohumeral abductors"  /* L10 */

  ],

  "shoulders_lateral": [

    "side shoulders",  /* L1 */

    "side shoulders",  /* L2 */

    "side delts",  /* L3 */

    "side delts",  /* L4 */

    "shoulders_lateral",  /* L5 */

    "side delts",  /* L6 */

    "lateral deltoid",  /* L7 */

    "lateral deltoid group",  /* L8 */

    "lateral deltoid musculature",  /* L9 */

    "lateral glenohumeral abductors"  /* L10 */

  ],

  "biceps": [

    "biceps",  /* L1 */

    "biceps",  /* L2 */

    "biceps",  /* L3 */

    "biceps",  /* L4 */

    "biceps",  /* L5 */

    "biceps",  /* L6 */

    "biceps brachii",  /* L7 */

    "biceps brachii group",  /* L8 */

    "elbow flexor musculature",  /* L9 */

    "anterior brachial flexor complex"  /* L10 */

  ],

  "triceps": [

    "triceps",  /* L1 */

    "triceps",  /* L2 */

    "triceps",  /* L3 */

    "triceps",  /* L4 */

    "triceps",  /* L5 */

    "triceps",  /* L6 */

    "triceps brachii",  /* L7 */

    "triceps brachii group",  /* L8 */

    "elbow extensor musculature",  /* L9 */

    "posterior brachial extensor complex"  /* L10 */

  ],

  "forearms": [

    "forearms",  /* L1 */

    "forearms",  /* L2 */

    "forearms",  /* L3 */

    "forearms",  /* L4 */

    "forearms",  /* L5 */

    "forearms",  /* L6 */

    "forearm muscles",  /* L7 */

    "forearm flexors and extensors",  /* L8 */

    "antebrachial musculature",  /* L9 */

    "distal antebrachial musculature"  /* L10 */

  ],

  "core_abs": [

    "abs",  /* L1 */

    "abs",  /* L2 */

    "core",  /* L3 */

    "core",  /* L4 */

    "core_abs",  /* L5 */

    "core",  /* L6 */

    "abdominal complex",  /* L7 */

    "anterior core musculature",  /* L8 */

    "lumbopelvic stabilizing musculature",  /* L9 */

    "truncal stabilizing cylinder"  /* L10 */

  ],

  "quads": [

    "quads",  /* L1 */

    "quads",  /* L2 */

    "quads",  /* L3 */

    "quads",  /* L4 */

    "quads",  /* L5 */

    "quads",  /* L6 */

    "quadriceps",  /* L7 */

    "quadriceps femoris",  /* L8 */

    "knee extensor musculature",  /* L9 */

    "anterior femoral extensor complex"  /* L10 */

  ],

  "hamstrings": [

    "hamstrings",  /* L1 */

    "hamstrings",  /* L2 */

    "hamstrings",  /* L3 */

    "hamstrings",  /* L4 */

    "hamstrings",  /* L5 */

    "hamstrings",  /* L6 */

    "hamstring group",  /* L7 */

    "biceps femoris complex",  /* L8 */

    "knee flexor musculature",  /* L9 */

    "posterior femoral flexor complex"  /* L10 */

  ],

  "glutes": [

    "glutes",  /* L1 */

    "glutes",  /* L2 */

    "glutes",  /* L3 */

    "glutes",  /* L4 */

    "glutes",  /* L5 */

    "glutes",  /* L6 */

    "gluteal group",  /* L7 */

    "gluteus maximus complex",  /* L8 */

    "hip extensor musculature",  /* L9 */

    "posterior pelvic extensor complex"  /* L10 */

  ],

  "calves": [

    "calves",  /* L1 */

    "calves",  /* L2 */

    "calves",  /* L3 */

    "calves",  /* L4 */

    "calves",  /* L5 */

    "calves",  /* L6 */

    "calf group",  /* L7 */

    "gastrocnemius and soleus",  /* L8 */

    "triceps surae complex",  /* L9 */

    "posterior crural musculature"  /* L10 */

  ],

  "adductors": [

    "inner thighs",  /* L1 */

    "inner thighs",  /* L2 */

    "adductors",  /* L3 */

    "adductors",  /* L4 */

    "adductors",  /* L5 */

    "adductors",  /* L6 */

    "adductor group",  /* L7 */

    "medial thigh muscles",  /* L8 */

    "hip adductor musculature",  /* L9 */

    "medial femoral adductor complex"  /* L10 */

  ],

  "abductors": [

    "outer hips",  /* L1 */

    "outer hips",  /* L2 */

    "abductors",  /* L3 */

    "abductors",  /* L4 */

    "abductors",  /* L5 */

    "abductors",  /* L6 */

    "abductor group",  /* L7 */

    "lateral hip muscles",  /* L8 */

    "hip abductor musculature",  /* L9 */

    "lateral pelvic abductor complex"  /* L10 */

  ],

  "neck": [

    "neck",  /* L1 */

    "neck",  /* L2 */

    "neck",  /* L3 */

    "neck",  /* L4 */

    "neck",  /* L5 */

    "neck",  /* L6 */

    "cervical muscles",  /* L7 */

    "cervical musculature",  /* L8 */

    "cervical stabilizer complex",  /* L9 */

    "deep cervical flexor-extensor cylinder"  /* L10 */

  ],

  "hip_flexors": [

    "hip flexors",  /* L1 */

    "hip flexors",  /* L2 */

    "hip flexors",  /* L3 */

    "hip flexors",  /* L4 */

    "hip_flexors",  /* L5 */

    "hip flexors",  /* L6 */

    "hip flexor group",  /* L7 */

    "iliopsoas complex",  /* L8 */

    "hip flexor musculature",  /* L9 */

    "anterior pelvic flexor cylinder"  /* L10 */

  ],

  "erectors": [

    "low back",  /* L1 */

    "low back",  /* L2 */

    "low back",  /* L3 */

    "low back",  /* L4 */

    "erectors",  /* L5 */

    "low back",  /* L6 */

    "spinal erectors",  /* L7 */

    "erector spinae group",  /* L8 */

    "paraspinal musculature",  /* L9 */

    "thoracolumbar extensor cylinder"  /* L10 */

  ],

  "tibialis_anterior": [

    "shins",  /* L1 */

    "shins",  /* L2 */

    "shins",  /* L3 */

    "shins",  /* L4 */

    "tibialis_anterior",  /* L5 */

    "shins",  /* L6 */

    "tibialis anterior",  /* L7 */

    "anterior tibialis",  /* L8 */

    "dorsiflexor musculature",  /* L9 */

    "anterior crural dorsiflexor complex"  /* L10 */

  ],

  "rotator_cuff": [

    "rotator cuff",  /* L1 */

    "rotator cuff",  /* L2 */

    "rotator cuff",  /* L3 */

    "rotator cuff",  /* L4 */

    "rotator_cuff",  /* L5 */

    "rotator cuff",  /* L6 */

    "rotator cuff group",  /* L7 */

    "rotator cuff complex",  /* L8 */

    "glenohumeral stabilizers",  /* L9 */

    "deep glenohumeral stabilizing cuff"  /* L10 */

  ],

  "hands_grip": [

    "grip",  /* L1 */

    "grip",  /* L2 */

    "grip",  /* L3 */

    "grip",  /* L4 */

    "hands_grip",  /* L5 */

    "grip",  /* L6 */

    "grip muscles",  /* L7 */

    "grip and hand muscles",  /* L8 */

    "intrinsic and extrinsic hand musculature",  /* L9 */

    "distal prehensile musculature"  /* L10 */

  ],

  "feet_ankles": [

    "feet",  /* L1 */

    "feet",  /* L2 */

    "feet",  /* L3 */

    "feet",  /* L4 */

    "feet_ankles",  /* L5 */

    "feet",  /* L6 */

    "foot and ankle muscles",  /* L7 */

    "intrinsic foot musculature",  /* L8 */

    "plantar intrinsic complex",  /* L9 */

    "distal pedal stabilizing cylinder"  /* L10 */

  ],

  "bodyweight": [

    "no equipment",  /* L1 */

    "no equipment",  /* L2 */

    "bodyweight",  /* L3 */

    "bodyweight",  /* L4 */

    "bodyweight",  /* L5 */

    "bodyweight",  /* L6 */

    "bodyweight only",  /* L7 */

    "bodyweight resistance",  /* L8 */

    "bodyweight-as-load modality",  /* L9 */

    "somatically resisted modality"  /* L10 */

  ],

  "dumbbells": [

    "dumbbells",  /* L1 */

    "dumbbells",  /* L2 */

    "dumbbells",  /* L3 */

    "dumbbells",  /* L4 */

    "dumbbells",  /* L5 */

    "dumbbells",  /* L6 */

    "dumbbell pair",  /* L7 */

    "bilateral dumbbell implements",  /* L8 */

    "unilateral loading implements",  /* L9 */

    "dis handheld loading implements"  /* L10 */

  ],

  "barbell": [

    "barbell",  /* L1 */

    "barbell",  /* L2 */

    "barbell",  /* L3 */

    "barbell",  /* L4 */

    "barbell",  /* L5 */

    "barbell",  /* L6 */

    "olympic barbell",  /* L7 */

    "loaded barbell implement",  /* L8 */

    "collapsible barbell implement",  /* L9 */

    "standardized long-bar implement"  /* L10 */

  ],

  "kettlebells": [

    "kettlebells",  /* L1 */

    "kettlebells",  /* L2 */

    "kettlebells",  /* L3 */

    "kettlebells",  /* L4 */

    "kettlebells",  /* L5 */

    "kettlebells",  /* L6 */

    "kettlebell set",  /* L7 */

    "offset-load implements",  /* L8 */

    "displaced-center-of-mass implements",  /* L9 */

    "cantilevered loading implements"  /* L10 */

  ],

  "resistance band": [

    "stretch band",  /* L1 */

    "stretch band",  /* L2 */

    "resistance band",  /* L3 */

    "resistance band",  /* L4 */

    "resistance band",  /* L5 */

    "resistance band",  /* L6 */

    "elastic band",  /* L7 */

    "elastic resistance implement",  /* L8 */

    "variable elastic tension implement",  /* L9 */

    "progressive elastomeric tension implement"  /* L10 */

  ],

  "pull-up bar": [

    "pull-up bar",  /* L1 */

    "pull-up bar",  /* L2 */

    "pull-up bar",  /* L3 */

    "pull-up bar",  /* L4 */

    "pull-up bar",  /* L5 */

    "pull-up bar",  /* L6 */

    "overhead bar",  /* L7 */

    "fixed overhead bar",  /* L8 */

    "suspension anchor point",  /* L9 */

    "elevated fixed suspension anchor"  /* L10 */

  ],

  "Monday": [

    "Monday",  /* L1 */

    "Monday",  /* L2 */

    "Monday",  /* L3 */

    "Monday",  /* L4 */

    "Monday",  /* L5 */

    "Monday",  /* L6 */

    "Monday",  /* L7 */

    "Monday",  /* L8 */

    "Monday",  /* L9 */

    "Monday"  /* L10 */

  ],

  "Tuesday": [

    "Tuesday",  /* L1 */

    "Tuesday",  /* L2 */

    "Tuesday",  /* L3 */

    "Tuesday",  /* L4 */

    "Tuesday",  /* L5 */

    "Tuesday",  /* L6 */

    "Tuesday",  /* L7 */

    "Tuesday",  /* L8 */

    "Tuesday",  /* L9 */

    "Tuesday"  /* L10 */

  ],

  "Wednesday": [

    "Wednesday",  /* L1 */

    "Wednesday",  /* L2 */

    "Wednesday",  /* L3 */

    "Wednesday",  /* L4 */

    "Wednesday",  /* L5 */

    "Wednesday",  /* L6 */

    "Wednesday",  /* L7 */

    "Wednesday",  /* L8 */

    "Wednesday",  /* L9 */

    "Wednesday"  /* L10 */

  ],

  "Thursday": [

    "Thursday",  /* L1 */

    "Thursday",  /* L2 */

    "Thursday",  /* L3 */

    "Thursday",  /* L4 */

    "Thursday",  /* L5 */

    "Thursday",  /* L6 */

    "Thursday",  /* L7 */

    "Thursday",  /* L8 */

    "Thursday",  /* L9 */

    "Thursday"  /* L10 */

  ],

  "Friday": [

    "Friday",  /* L1 */

    "Friday",  /* L2 */

    "Friday",  /* L3 */

    "Friday",  /* L4 */

    "Friday",  /* L5 */

    "Friday",  /* L6 */

    "Friday",  /* L7 */

    "Friday",  /* L8 */

    "Friday",  /* L9 */

    "Friday"  /* L10 */

  ],

  "Saturday": [

    "Saturday",  /* L1 */

    "Saturday",  /* L2 */

    "Saturday",  /* L3 */

    "Saturday",  /* L4 */

    "Saturday",  /* L5 */

    "Saturday",  /* L6 */

    "Saturday",  /* L7 */

    "Saturday",  /* L8 */

    "Saturday",  /* L9 */

    "Saturday"  /* L10 */

  ],

  "Sunday": [

    "Sunday",  /* L1 */

    "Sunday",  /* L2 */

    "Sunday",  /* L3 */

    "Sunday",  /* L4 */

    "Sunday",  /* L5 */

    "Sunday",  /* L6 */

    "Sunday",  /* L7 */

    "Sunday",  /* L8 */

    "Sunday",  /* L9 */

    "Sunday"  /* L10 */

  ],

  "Add new from library": [

    "Add from library",  /* L1 */

    "Add new from library",  /* L2 */

    "Add new from library",  /* L3 */

    "Add new from library",  /* L4 */

    "Add new from library",  /* L5 */

    "Add exercise from library",  /* L6 */

    "Append exercise from library",  /* L7 */

    "Add movement from repository",  /* L8 */

    "Append movement from repository",  /* L9 */

    "Adduce movement from repository"  /* L10 */

  ],

  "View details": [

    "See more",  /* L1 */

    "View details",  /* L2 */

    "View details",  /* L3 */

    "View details",  /* L4 */

    "View details",  /* L5 */

    "Open full view",  /* L6 */

    "View complete details",  /* L7 */

    "Examine full particulars",  /* L8 */

    "Examine the complete particulars",  /* L9 */

    "Peruse the complete particulars"  /* L10 */

  ],

  "Show Instructions": [

    "Show steps",  /* L1 */

    "Show Instructions",  /* L2 */

    "Show Instructions",  /* L3 */

    "Show Instructions",  /* L4 */

    "Show Instructions",  /* L5 */

    "Display instructions",  /* L6 */

    "Display full instructions",  /* L7 */

    "Present execution guidance",  /* L8 */

    "Present the execution guidance",  /* L9 */

    "Present the executive directives"  /* L10 */

  ],

  "How to do": [

    "How to do",  /* L1 */

    "How to",  /* L2 */

    "How to do",  /* L3 */

    "How to do",  /* L4 */

    "How to do",  /* L5 */

    "Technique guide",  /* L6 */

    "Movement technique guide",  /* L7 */

    "Execution methodology",  /* L8 */

    "Executive methodology",  /* L9 */

    "Methodology of execution"  /* L10 */

  ],

  "Generate Workout": [

    "Make my workout",  /* L1 */

    "Generate Workout",  /* L2 */

    "Generate Workout",  /* L3 */

    "Generate Workout",  /* L4 */

    "Generate Workout",  /* L5 */

    "Create session",  /* L6 */

    "Render training session",  /* L7 */

    "Formulate session prescription",  /* L8 */

    "Synthesize session prescription",  /* L9 */

    "Fabricate the session prescription"  /* L10 */

  ],

  "Components trained in this session": [

    "Trained today",  /* L1 */

    "Trained in this session",  /* L2 */

    "Components trained in this session",  /* L3 */

    "Components trained in this session",  /* L4 */

    "Components trained in this session",  /* L5 */

    "Fitness components addressed",  /* L6 */

    "Components addressed this session",  /* L7 */

    "Fitness components engaged herein",  /* L8 */

    "Components engaged within this bout",  /* L9 */

    "Components engaged within this bout's design"  /* L10 */

  ],

  "Muscle Recovery Status": [

    "Muscle recovery",  /* L1 */

    "Muscle Recovery Status",  /* L2 */

    "Muscle Recovery Status",  /* L3 */

    "Muscle Recovery Status",  /* L4 */

    "Muscle Recovery Status",  /* L5 */

    "Recovery by muscle",  /* L6 */

    "Muscle recovery analytics",  /* L7 */

    "Muscular recuperation status",  /* L8 */

    "Muscular recuperative status",  /* L9 */

    "Muscular recuperative disposition"  /* L10 */

  ],

  "Workout History": [

    "My workouts",  /* L1 */

    "Workout History",  /* L2 */

    "Workout History",  /* L3 */

    "Workout History",  /* L4 */

    "Workout History",  /* L5 */

    "Training history",  /* L6 */

    "Workout history log",  /* L7 */

    "Chronological workout record",  /* L8 */

    "Chronological training record",  /* L9 */

    "Diachronic training record"  /* L10 */

  ],

  "Progress Analytics": [

    "My stats",  /* L1 */

    "Progress Analytics",  /* L2 */

    "Progress Analytics",  /* L3 */

    "Progress Analytics",  /* L4 */

    "Progress Analytics",  /* L5 */

    "Performance analytics",  /* L6 */

    "Performance analytics suite",  /* L7 */

    "Longitudinal performance metrics",  /* L8 */

    "Longitudinal kinesiological metrics",  /* L9 */

    "Panoramic kinesiological analytics"  /* L10 */

  ],

  "Export All Data": [

    "Save everything",  /* L1 */

    "Export All Data",  /* L2 */

    "Export All Data",  /* L3 */

    "Export All Data",  /* L4 */

    "Export All Data",  /* L5 */

    "Export all records",  /* L6 */

    "Export complete dataset",  /* L7 */

    "Export the complete dataset",  /* L8 */

    "Effect complete data exportation",  /* L9 */

    "Effect the complete exportation of records"  /* L10 */

  ],

  "Reset All Data": [

    "Erase everything",  /* L1 */

    "Reset All Data",  /* L2 */

    "Reset All Data",  /* L3 */

    "Reset All Data",  /* L4 */

    "Reset All Data",  /* L5 */

    "Erase all records",  /* L6 */

    "Permanently erase all data",  /* L7 */

    "Expunge the complete dataset",  /* L8 */

    "Expunge all accumulated records",  /* L9 */

    "Annul the entirety of accumulated records"  /* L10 */

  ],

  "Import Exercises": [

    "Bring in exercises",  /* L1 */

    "Import Exercises",  /* L2 */

    "Import Exercises",  /* L3 */

    "Import Exercises",  /* L4 */

    "Import Exercises",  /* L5 */

    "Import movements",  /* L6 */

    "Import exercise data",  /* L7 */

    "Incorporate exercise data",  /* L8 */

    "Incorporate external exercise data",  /* L9 */

    "Incorporate exogenous movement data"  /* L10 */

  ],

  "Export Exercises": [

    "Save my exercises",  /* L1 */

    "Export Exercises",  /* L2 */

    "Export Exercises",  /* L3 */

    "Export Exercises",  /* L4 */

    "Export Exercises",  /* L5 */

    "Export movements",  /* L6 */

    "Export exercise data",  /* L7 */

    "Export the exercise dataset",  /* L8 */

    "Effect exercise data exportation",  /* L9 */

    "Effect the exportation of movement data"  /* L10 */

  ],

};



/* P4 QUOTES — original coaching lines, banded by vocabulary level.

 * band 1 = levels 1-3, band 2 = levels 4-6, band 3 = levels 7-10. */

window.P4_QUOTES = [

  {"t": "Move a little today. Your body remembers.", "a": "Coach's promise", "band": 1},

  {"t": "Big muscles grow when small weights show up every day.", "a": "Coach's promise", "band": 1},

  {"t": "You do not need a perfect day. You need ten minutes.", "a": "Coach's promise", "band": 1},

  {"t": "Strong is fun. Strong is helpful. Start where you are.", "a": "Coach's promise", "band": 1},

  {"t": "Water, sleep, play, repeat.", "a": "The simple four", "band": 1},

  {"t": "Every rep is a high-five to your future self.", "a": "Coach's promise", "band": 1},

  {"t": "Wobbly today means steady tomorrow.", "a": "Coach's promise", "band": 1},

  {"t": "Lift, laugh, rest, repeat.", "a": "Gym wisdom", "band": 1},

  {"t": "Your legs carry you everywhere. Say thanks with a squat.", "a": "Coach's promise", "band": 1},

  {"t": "Slow and steady still counts as moving.", "a": "Coach's promise", "band": 1},

  {"t": "A short workout beats a skipped workout. Every time.", "a": "Coach's promise", "band": 1},

  {"t": "Muscles grow while you sleep. Sleep like it matters.", "a": "Coach's promise", "band": 1},

  {"t": "You are stronger than last month. Check the chart.", "a": "Coach's promise", "band": 1},

  {"t": "Push when it is light. Rest when it is heavy. That is the trick.", "a": "Coach's promise", "band": 1},

  {"t": "Sweat now, smile later.", "a": "Gym wisdom", "band": 1},

  {"t": "Nobody regrets the workout they finished.", "a": "Gym wisdom", "band": 1},

  {"t": "Start small. Start today. Start again tomorrow.", "a": "Coach's promise", "band": 1},

  {"t": "Your body loves you. Move it like you mean it.", "a": "Coach's promise", "band": 1},

  {"t": "Ten pushups a day beat zero pushups perfectly planned.", "a": "Coach's promise", "band": 1},

  {"t": "Breathe out when you push. That is the secret most people forget.", "a": "Coach's promise", "band": 1},

  {"t": "It is not about being the best. It is about being better than yesterday.", "a": "Gym wisdom", "band": 1},

  {"t": "Rest days are training days for your muscles.", "a": "Coach's promise", "band": 1},

  {"t": "Carry the groceries like a champion. That is farmer's strength.", "a": "Coach's promise", "band": 1},

  {"t": "Every expert was once a beginner who kept going.", "a": "Gym wisdom", "band": 1},

  {"t": "Drink water. Your muscles are mostly thirsty sponges.", "a": "Coach's promise", "band": 1},

  {"t": "Discipline is choosing what you want most over what you want now.", "a": "Old coaching truth", "band": 2},

  {"t": "You will never always be strong. But you can always start again.", "a": "Coach's promise", "band": 2},

  {"t": "The bar tests your body. Showing up tests your character.", "a": "Gym wisdom", "band": 2},

  {"t": "Train the boring basics until they are impressive.", "a": "Coach's promise", "band": 2},

  {"t": "Progress is quiet. It shows up in numbers before it shows up in mirrors.", "a": "Coach's promise", "band": 2},

  {"t": "Motivation gets you started. Habits carry you.", "a": "Behavior science", "band": 2},

  {"t": "Add a little weight or a little rep. Small hinges swing big doors.", "a": "Coach's promise", "band": 2},

  {"t": "Soreness fades. Skill compounds.", "a": "Coach's promise", "band": 2},

  {"t": "The best program is the one you will actually repeat next week.", "a": "Coach's promise", "band": 2},

  {"t": "Respect the warm-up. It is not lost time; it is insurance.", "a": "Coach's promise", "band": 2},

  {"t": "You cannot out-train a bad recovery. Sleep is part of the program.", "a": "Recovery science", "band": 2},

  {"t": "Consistency beats intensity when intensity has no consistency.", "a": "Coach's promise", "band": 2},

  {"t": "Track it. What gets measured gets managed.", "a": "Old coaching truth", "band": 2},

  {"t": "Every session is a vote for the person you are becoming.", "a": "Habit science", "band": 2},

  {"t": "The weight room forgives everyone who returns.", "a": "Gym wisdom", "band": 2},

  {"t": "Strength is a skill. Practice it like one.", "a": "Coach's promise", "band": 2},

  {"t": "Leave one or two reps in the tank. Progress lives there.", "a": "Coach's promise", "band": 2},

  {"t": "Nobody is watching as much as you think. Lift anyway.", "a": "Coach's promise", "band": 2},

  {"t": "Your weakest lift is your fastest improvement. Own it.", "a": "Coach's promise", "band": 2},

  {"t": "Deload weeks are not quitting. They are planting.", "a": "Coach's promise", "band": 2},

  {"t": "Perfect form at light weight builds the future heavy set.", "a": "Coach's promise", "band": 2},

  {"t": "If it matters, measure it. If it hurts, fix it. If it works, repeat it.", "a": "Coach's promise", "band": 2},

  {"t": "Two good weeks beat one perfect week.", "a": "Coach's promise", "band": 2},

  {"t": "The plan bends. The habit does not.", "a": "Coach's promise", "band": 2},

  {"t": "Eat like someone who has to move well tomorrow.", "a": "Sports nutrition", "band": 2},

  {"t": "Hydration is the cheapest performance upgrade on earth.", "a": "Recovery science", "band": 2},

  {"t": "Warm muscles are kind muscles.", "a": "Gym wisdom", "band": 2},

  {"t": "Heavy is relative. Honest is universal.", "a": "Coach's promise", "band": 2},

  {"t": "Boredom is just progress without novelty. Keep going.", "a": "Coach's promise", "band": 2},

  {"t": "The streak is not magic. It is proof.", "a": "Coach's promise", "band": 2},

  {"t": "Adaptation is the organism's answer to a well-posed question of stress and rest.", "a": "Training theory", "band": 3},

  {"t": "Mechanical tension is the primary driver of hypertrophy; everything else is orchestration.", "a": "Exercise science", "band": 3},

  {"t": "Load is a variable. Effort is the constant.", "a": "Coach's promise", "band": 3},

  {"t": "The nervous system learns the lift before the muscle builds it. Be patient with the first weeks.", "a": "Exercise science", "band": 3},

  {"t": "Progressive overload without progressive recovery is just progressive injury.", "a": "Coach's promise", "band": 3},

  {"t": "Fatigue masks fitness. Schedule its departure.", "a": "Peaking wisdom", "band": 3},

  {"t": "Volume is a dose. Intensity is a direction. Frequency is a teacher.", "a": "Training theory", "band": 3},

  {"t": "Technique under fatigue is the truest portrait of your movement skill.", "a": "Coach's promise", "band": 3},

  {"t": "Specificity rewards exactly what you practiced — no more, no less.", "a": "Training theory", "band": 3},

  {"t": "The eccentric builds; the concentric proves. Train both with intent.", "a": "Exercise science", "band": 3},

  {"t": "Autoregulation is not softness. It is arithmetic with today's numbers.", "a": "Coach's promise", "band": 3},

  {"t": "A plateau is data: either the stimulus stopped changing or the recovery did.", "a": "Coach's promise", "band": 3},

  {"t": "RPE is a conversation with tomorrow's set.", "a": "Coach's promise", "band": 3},

  {"t": "Speed of intent matters even when the bar moves slowly.", "a": "Coach's promise", "band": 3},

  {"t": "Strength endurance is the foundation beneath the skyscraper of peak force.", "a": "Training theory", "band": 3},

  {"t": "Balance is not stillness. It is a thousand quiet corrections per minute.", "a": "Motor learning", "band": 3},

  {"t": "The best warm-up is the shortest one that removes the first-set penalty.", "a": "Coach's promise", "band": 3},

  {"t": "Deload before the body files the complaint for you.", "a": "Coach's promise", "band": 3},

  {"t": "Train movements, not vanity; the mirror will file its report anyway.", "a": "Coach's promise", "band": 3},

  {"t": "Overload judiciously: the body strengthens what it can survive, and survives what you planned.", "a": "Coach's promise", "band": 3},

];



/* P4 BADGES — machine-checkable achievements (see Motivation.check). */

window.P4_BADGES = [

  {"id": "first_workout", "icon": "fa-seedling", "name": "First Rep", "desc": "Complete your first workout.", "check": {"type": "workouts", "value": 1}},

  {"id": "w3", "icon": "fa-dumbbell", "name": "Getting Moving", "desc": "3 workouts done.", "check": {"type": "workouts", "value": 3}},

  {"id": "w5", "icon": "fa-dumbbell", "name": "Rhythm Found", "desc": "5 workouts done.", "check": {"type": "workouts", "value": 5}},

  {"id": "w10", "icon": "fa-dumbbell", "name": "Double Digits", "desc": "10 workouts done.", "check": {"type": "workouts", "value": 10}},

  {"id": "w25", "icon": "fa-dumbbell", "name": "Regular", "desc": "25 workouts done.", "check": {"type": "workouts", "value": 25}},

  {"id": "w50", "icon": "fa-dumbbell", "name": "Fixture", "desc": "50 workouts done.", "check": {"type": "workouts", "value": 50}},

  {"id": "w75", "icon": "fa-dumbbell", "name": "Iron Regular", "desc": "75 workouts done.", "check": {"type": "workouts", "value": 75}},

  {"id": "w100", "icon": "fa-hundred-points", "name": "Century Club", "desc": "100 workouts done.", "check": {"type": "workouts", "value": 100}},

  {"id": "w150", "icon": "fa-medal", "name": "Veteran", "desc": "150 workouts done.", "check": {"type": "workouts", "value": 150}},

  {"id": "w200", "icon": "fa-medal", "name": "Iron Veteran", "desc": "200 workouts done.", "check": {"type": "workouts", "value": 200}},

  {"id": "w250", "icon": "fa-trophy", "name": "Quarter Thousand", "desc": "250 workouts done.", "check": {"type": "workouts", "value": 250}},

  {"id": "w365", "icon": "fa-crown", "name": "Full Year Strong", "desc": "365 workouts done.", "check": {"type": "workouts", "value": 365}},

  {"id": "w500", "icon": "fa-trophy", "name": "Half Thousand", "desc": "500 workouts done.", "check": {"type": "workouts", "value": 500}},

  {"id": "w1000", "icon": "fa-gem", "name": "The Long Game", "desc": "1000 workouts done.", "check": {"type": "workouts", "value": 1000}},

  {"id": "s2", "icon": "fa-fire", "name": "Warming Up", "desc": "2-day streak.", "check": {"type": "streak", "value": 2}},

  {"id": "s3", "icon": "fa-fire", "name": "Three in a Row", "desc": "3-day streak.", "check": {"type": "streak", "value": 3}},

  {"id": "s7", "icon": "fa-fire", "name": "Week of Fire", "desc": "7-day streak.", "check": {"type": "streak", "value": 7}},

  {"id": "s14", "icon": "fa-fire", "name": "Fortnight Forge", "desc": "14-day streak.", "check": {"type": "streak", "value": 14}},

  {"id": "s21", "icon": "fa-fire", "name": "Habit Locked", "desc": "21-day streak.", "check": {"type": "streak", "value": 21}},

  {"id": "s30", "icon": "fa-fire", "name": "Month of Motion", "desc": "30-day streak.", "check": {"type": "streak", "value": 30}},

  {"id": "s50", "icon": "fa-fire", "name": "Fifty Flame", "desc": "50-day streak.", "check": {"type": "streak", "value": 50}},

  {"id": "s75", "icon": "fa-fire", "name": "Blaze Keeper", "desc": "75-day streak.", "check": {"type": "streak", "value": 75}},

  {"id": "s100", "icon": "fa-fire", "name": "Hundred Heat", "desc": "100-day streak.", "check": {"type": "streak", "value": 100}},

  {"id": "s150", "icon": "fa-fire", "name": "Eternal Ember", "desc": "150-day streak.", "check": {"type": "streak", "value": 150}},

  {"id": "s200", "icon": "fa-fire", "name": "Two Hundred Torch", "desc": "200-day streak.", "check": {"type": "streak", "value": 200}},

  {"id": "s365", "icon": "fa-fire", "name": "Year-Round Flame", "desc": "365-day streak.", "check": {"type": "streak", "value": 365}},

  {"id": "vol1000", "icon": "fa-weight-hanging", "name": "First Ton", "desc": "1,000 lbs moved in total.", "check": {"type": "volume", "value": 1000}},

  {"id": "vol5000", "icon": "fa-weight-hanging", "name": "Five Ton Forge", "desc": "5,000 lbs moved in total.", "check": {"type": "volume", "value": 5000}},

  {"id": "vol10000", "icon": "fa-weight-hanging", "name": "Ten Tonnage", "desc": "10,000 lbs moved in total.", "check": {"type": "volume", "value": 10000}},

  {"id": "vol25000", "icon": "fa-weight-hanging", "name": "Mover", "desc": "25,000 lbs moved in total.", "check": {"type": "volume", "value": 25000}},

  {"id": "vol50000", "icon": "fa-weight-hanging", "name": "Shifter", "desc": "50,000 lbs moved in total.", "check": {"type": "volume", "value": 50000}},

  {"id": "vol100000", "icon": "fa-weight-hanging", "name": "Six Figures of Iron", "desc": "100,000 lbs moved in total.", "check": {"type": "volume", "value": 100000}},

  {"id": "vol250000", "icon": "fa-weight-hanging", "name": "Quarter Million", "desc": "250,000 lbs moved in total.", "check": {"type": "volume", "value": 250000}},

  {"id": "vol500000", "icon": "fa-weight-hanging", "name": "Half Million", "desc": "500,000 lbs moved in total.", "check": {"type": "volume", "value": 500000}},

  {"id": "vol1000000", "icon": "fa-weight-hanging", "name": "Million Pound Club", "desc": "1,000,000 lbs moved in total.", "check": {"type": "volume", "value": 1000000}},

  {"id": "vol2000000", "icon": "fa-weight-hanging", "name": "Beyond Millions", "desc": "2,000,000 lbs moved in total.", "check": {"type": "volume", "value": 2000000}},

  {"id": "tried5", "icon": "fa-compass", "name": "Sampler", "desc": "Try 5 different exercises.", "check": {"type": "tried", "value": 5}},

  {"id": "tried15", "icon": "fa-compass", "name": "Explorer", "desc": "Try 15 different exercises.", "check": {"type": "tried", "value": 15}},

  {"id": "tried30", "icon": "fa-compass", "name": "Pathfinder", "desc": "Try 30 different exercises.", "check": {"type": "tried", "value": 30}},

  {"id": "tried60", "icon": "fa-compass", "name": "Cartographer", "desc": "Try 60 different exercises.", "check": {"type": "tried", "value": 60}},

  {"id": "tried100", "icon": "fa-compass", "name": "Movement Collector", "desc": "Try 100 different exercises.", "check": {"type": "tried", "value": 100}},

  {"id": "tried200", "icon": "fa-compass", "name": "Library Legend", "desc": "Try 200 different exercises.", "check": {"type": "tried", "value": 200}},

  {"id": "pr1", "icon": "fa-arrow-trend-up", "name": "First PR", "desc": "Set your first personal record.", "check": {"type": "prs", "value": 1}},

  {"id": "pr5", "icon": "fa-arrow-trend-up", "name": "PR Collector", "desc": "5 personal records set.", "check": {"type": "prs", "value": 5}},

  {"id": "pr10", "icon": "fa-arrow-trend-up", "name": "Record Hunter", "desc": "10 personal records set.", "check": {"type": "prs", "value": 10}},

  {"id": "pr25", "icon": "fa-arrow-trend-up", "name": "Record Breaker", "desc": "25 personal records set.", "check": {"type": "prs", "value": 25}},

  {"id": "pr50", "icon": "fa-arrow-trend-up", "name": "Record Shatterer", "desc": "50 personal records set.", "check": {"type": "prs", "value": 50}},

  {"id": "pr100", "icon": "fa-arrow-trend-up", "name": "Living Record Book", "desc": "100 personal records set.", "check": {"type": "prs", "value": 100}},

  {"id": "bk1", "icon": "fa-shield-halved", "name": "Safety Net", "desc": "Save your first backup.", "check": {"type": "backups", "value": 1}},

  {"id": "bk5", "icon": "fa-shield-halved", "name": "Backup Habit", "desc": "5 backups saved.", "check": {"type": "backups", "value": 5}},

  {"id": "bk20", "icon": "fa-shield-halved", "name": "Data Guardian", "desc": "20 backups saved.", "check": {"type": "backups", "value": 20}},

  {"id": "add1", "icon": "fa-plus", "name": "Librarian", "desc": "Add your first custom exercise.", "check": {"type": "studioAdds", "value": 1}},

  {"id": "add5", "icon": "fa-plus", "name": "Curator", "desc": "Add 5 custom exercises.", "check": {"type": "studioAdds", "value": 5}},

  {"id": "add20", "icon": "fa-plus", "name": "Architect of Moves", "desc": "Add 20 custom exercises.", "check": {"type": "studioAdds", "value": 20}},

  {"id": "imp1", "icon": "fa-file-import", "name": "Bridge Builder", "desc": "Import an exercise library for the first time.", "check": {"type": "imports", "value": 1}},

  {"id": "imp5", "icon": "fa-file-import", "name": "Data Wrangler", "desc": "Import 5 times.", "check": {"type": "imports", "value": 5}},

  {"id": "exp1", "icon": "fa-file-export", "name": "Sharer", "desc": "Export your library or data for the first time.", "check": {"type": "exports", "value": 1}},

  {"id": "exp5", "icon": "fa-file-export", "name": "Open Book", "desc": "Export 5 times.", "check": {"type": "exports", "value": 5}},

  {"id": "early", "icon": "fa-sun", "name": "Early Bird", "desc": "Train before 8 AM.", "check": {"type": "earlyBird", "value": 0}},

  {"id": "owl", "icon": "fa-moon", "name": "Night Owl", "desc": "Train after 9 PM.", "check": {"type": "nightOwl", "value": 0}},

  {"id": "comeback", "icon": "fa-phoenix", "name": "The Comeback", "desc": "Return to training after 3+ weeks away.", "check": {"type": "comeback", "value": 0}},

  {"id": "gym1", "icon": "fa-hand-fist", "name": "Glove Mode", "desc": "Try Gym Mode once.", "check": {"type": "gym", "value": 1}},

  {"id": "gym10", "icon": "fa-hand-fist", "name": "Factory Hands", "desc": "Use Gym Mode 10 sessions.", "check": {"type": "gym", "value": 10}},

  {"id": "vocab1", "icon": "fa-language", "name": "Word Curious", "desc": "Change your reading level once.", "check": {"type": "vocab", "value": 1}},

  {"id": "vocab5", "icon": "fa-language", "name": "Word Collector", "desc": "Try 5 different reading levels.", "check": {"type": "vocab", "value": 5}},

  {"id": "theme1", "icon": "fa-palette", "name": "Paint the Gym", "desc": "Change your accent color once.", "check": {"type": "themes", "value": 1}},

  {"id": "theme5", "icon": "fa-palette", "name": "Chromatophore", "desc": "Try 5 accent colors.", "check": {"type": "themes", "value": 5}},

  {"id": "theme10", "icon": "fa-palette", "name": "Prism", "desc": "Try 10 accent colors.", "check": {"type": "themes", "value": 10}},

  {"id": "comp_cardio_endurance_10", "icon": "fa-heart-pulse", "name": "Cardio Endurance Apprentice", "desc": "Train while Cardio Endurance is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_cardio_endurance_30", "icon": "fa-heart-pulse", "name": "Cardio Endurance Adept", "desc": "Train while Cardio Endurance is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_cardio_endurance_75", "icon": "fa-heart-pulse", "name": "Cardio Endurance Master", "desc": "Train while Cardio Endurance is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_muscular_strength_10", "icon": "fa-dumbbell", "name": "Strength Apprentice", "desc": "Train while Strength is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_muscular_strength_30", "icon": "fa-dumbbell", "name": "Strength Adept", "desc": "Train while Strength is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_muscular_strength_75", "icon": "fa-dumbbell", "name": "Strength Master", "desc": "Train while Strength is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_muscular_endurance_10", "icon": "fa-battery-full", "name": "Muscle Endurance Apprentice", "desc": "Train while Muscle Endurance is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_muscular_endurance_30", "icon": "fa-battery-full", "name": "Muscle Endurance Adept", "desc": "Train while Muscle Endurance is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_muscular_endurance_75", "icon": "fa-battery-full", "name": "Muscle Endurance Master", "desc": "Train while Muscle Endurance is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_flexibility_10", "icon": "fa-gymnast", "name": "Flexibility Apprentice", "desc": "Train while Flexibility is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_flexibility_30", "icon": "fa-gymnast", "name": "Flexibility Adept", "desc": "Train while Flexibility is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_flexibility_75", "icon": "fa-gymnast", "name": "Flexibility Master", "desc": "Train while Flexibility is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_body_composition_10", "icon": "fa-scale-balanced", "name": "Body Composition Apprentice", "desc": "Train while Body Composition is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_body_composition_30", "icon": "fa-scale-balanced", "name": "Body Composition Adept", "desc": "Train while Body Composition is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_body_composition_75", "icon": "fa-scale-balanced", "name": "Body Composition Master", "desc": "Train while Body Composition is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_power_10", "icon": "fa-bolt", "name": "Power Apprentice", "desc": "Train while Power is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_power_30", "icon": "fa-bolt", "name": "Power Adept", "desc": "Train while Power is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_power_75", "icon": "fa-bolt", "name": "Power Master", "desc": "Train while Power is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_speed_10", "icon": "fa-wind", "name": "Speed Apprentice", "desc": "Train while Speed is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_speed_30", "icon": "fa-wind", "name": "Speed Adept", "desc": "Train while Speed is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_speed_75", "icon": "fa-wind", "name": "Speed Master", "desc": "Train while Speed is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_agility_10", "icon": "fa-running", "name": "Agility Apprentice", "desc": "Train while Agility is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_agility_30", "icon": "fa-running", "name": "Agility Adept", "desc": "Train while Agility is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_agility_75", "icon": "fa-running", "name": "Agility Master", "desc": "Train while Agility is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_balance_10", "icon": "fa-person-swimming", "name": "Balance Apprentice", "desc": "Train while Balance is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_balance_30", "icon": "fa-person-swimming", "name": "Balance Adept", "desc": "Train while Balance is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_balance_75", "icon": "fa-person-swimming", "name": "Balance Master", "desc": "Train while Balance is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_coordination_10", "icon": "fa-hands", "name": "Coordination Apprentice", "desc": "Train while Coordination is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_coordination_30", "icon": "fa-hands", "name": "Coordination Adept", "desc": "Train while Coordination is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_coordination_75", "icon": "fa-hands", "name": "Coordination Master", "desc": "Train while Coordination is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

  {"id": "comp_reaction_time_10", "icon": "fa-stopwatch", "name": "Reaction Apprentice", "desc": "Train while Reaction is in focus 10 times.", "check": {"type": "workouts", "value": 10}},

  {"id": "comp_reaction_time_30", "icon": "fa-stopwatch", "name": "Reaction Adept", "desc": "Train while Reaction is in focus 30 times.", "check": {"type": "workouts", "value": 30}},

  {"id": "comp_reaction_time_75", "icon": "fa-stopwatch", "name": "Reaction Master", "desc": "Train while Reaction is in focus 75 times.", "check": {"type": "workouts", "value": 75}},

];



/* P4 EXPERT TIPS — 26 specialist domains of field notes.

 * band 1 = simple (levels 1-3), 2 = standard (4-6), 3 = advanced (7-10). */

window.P4_TIP_DOMAINS = [{"id": "strength", "name": "Strength Coaching", "icon": "fa-dumbbell"}, {"id": "physio", "name": "Physical Therapy", "icon": "fa-user-doctor"}, {"id": "sports_sci", "name": "Exercise Science", "icon": "fa-flask"}, {"id": "nutrition", "name": "Sports Nutrition", "icon": "fa-apple-whole"}, {"id": "sleep", "name": "Sleep Coaching", "icon": "fa-bed"}, {"id": "psychology", "name": "Sport Psychology", "icon": "fa-brain"}, {"id": "habits", "name": "Habit Design", "icon": "fa-calendar-check"}, {"id": "cardio", "name": "Cardio Coaching", "icon": "fa-heart-pulse"}, {"id": "endurance", "name": "Endurance Coaching", "icon": "fa-person-running"}, {"id": "mobility", "name": "Mobility Work", "icon": "fa-gymnast"}, {"id": "injury", "name": "Injury Prevention", "icon": "fa-bandage"}, {"id": "womens", "name": "Women's Health", "icon": "fa-venus"}, {"id": "older", "name": "Older Adults", "icon": "fa-person-cane"}, {"id": "teen", "name": "Youth Fitness", "icon": "fa-child-reaching"}, {"id": "adaptive", "name": "Adaptive Training", "icon": "fa-wheelchair"}, {"id": "pregnancy", "name": "Prenatal Fitness", "icon": "fa-baby-carriage"}, {"id": "hydration", "name": "Hydration Science", "icon": "fa-droplet"}, {"id": "recovery", "name": "Recovery Methods", "icon": "fa-battery-charging"}, {"id": "safety", "name": "Gym Safety", "icon": "fa-helmet-safety"}, {"id": "equipment", "name": "Equipment Know-How", "icon": "fa-screwdriver-wrench"}, {"id": "hygiene", "name": "Gym Hygiene", "icon": "fa-soap"}, {"id": "powerlifting", "name": "Powerlifting", "icon": "fa-weight-hanging"}, {"id": "bodybuilding", "name": "Bodybuilding", "icon": "fa-child-fighting"}, {"id": "calisthenics", "name": "Calisthenics", "icon": "fa-person-walking"}, {"id": "shiftwork", "name": "Shift-Worker Fitness", "icon": "fa-industry"}, {"id": "mindfulness", "name": "Mind-Body Coaching", "icon": "fa-spa"}];

window.P4_TIPS = [

  {"d": "strength", "e": "", "band": 1, "t": "Squeeze the muscle you are training. If you cannot feel it, make the weight lighter."},

  {"d": "strength", "e": "", "band": 1, "t": "Feet flat, hands steady, eyes forward. Set your base before every lift."},

  {"d": "strength", "e": "", "band": 1, "t": "Lower the weight slowly. The going-down part builds strength too."},

  {"d": "strength", "e": "", "band": 1, "t": "Two exercises done well beat six done fast."},

  {"d": "strength", "e": "", "band": 2, "t": "Progress one variable at a time: weight, reps, or sets — never all three in one jump."},

  {"d": "strength", "e": "", "band": 2, "t": "Rest 2-3 minutes on heavy compound lifts; short rests trade quality for the clock."},

  {"d": "strength", "e": "", "band": 2, "t": "Train close to failure, not at failure: 1-2 reps left in the tank grows strength and protects joints."},

  {"d": "strength", "e": "", "band": 2, "t": "Log every set. Numbers remove the guesswork from next week."},

  {"d": "strength", "e": "", "band": 2, "t": "Push, pull, squat, hinge, carry. If your week hits all five, you are covered."},

  {"d": "strength", "e": "", "band": 2, "t": "Unilateral work (one side at a time) exposes and fixes side-to-side gaps."},

  {"d": "strength", "e": "", "band": 3, "t": "Inter-set dead hangs and easy mobility between heavy sets maintain range without stealing recovery."},

  {"d": "strength", "e": "", "band": 3, "t": "If bar speed halves, the set is done — grinding reps teach grinding, not strength."},

  {"d": "strength", "e": "", "band": 2, "t": "Warm-up sets are rehearsals: same stance, same grip, rising weight."},

  {"d": "strength", "e": "", "band": 1, "t": "Ask for a spot on any barbell press over your head or chest."},

  {"d": "strength", "e": "", "band": 3, "t": "Alternate strength blocks (3-6 reps) and size blocks (8-15 reps) across months for the fastest combined gains."},

  {"d": "strength", "e": "", "band": 2, "t": "Add load only when every prescribed rep felt crisp at your usual tempo."},

  {"d": "strength", "e": "", "band": 2, "t": "Grip often fails first — train hangs and holds to keep the chain strong."},

  {"d": "strength", "e": "", "band": 3, "t": "Full range of motion at moderate loads generally beats partials for joint health and strength transfer."},

  {"d": "strength", "e": "", "band": 1, "t": "Keep your back straight and lift with your legs, in the gym and at work."},

  {"d": "strength", "e": "", "band": 2, "t": "Neck relaxed, jaw loose. Tension you do not need is weight you did not ask for."},

  {"d": "strength", "e": "", "band": 3, "t": "Rotate accessories every 6-8 weeks to refresh stimulus, keep the main lifts constant."},

  {"d": "strength", "e": "", "band": 2, "t": "Heavy day, light day, medium day: wave the load instead of maxing everything forever."},

  {"d": "physio", "e": "", "band": 1, "t": "Pain that is sharp, stabbing, or electric means stop. Muscle burn is fine; joint pain is not."},

  {"d": "physio", "e": "", "band": 1, "t": "Breathe out during the hardest part of the move. Your spine thanks you."},

  {"d": "physio", "e": "", "band": 2, "t": "Let soreness guide volume, not stop you: gentle movement usually helps sore muscles heal."},

  {"d": "physio", "e": "", "band": 2, "t": "If a joint swells or locks, get it assessed — those are red flags, not soreness."},

  {"d": "physio", "e": "", "band": 2, "t": "Neck, low back, knees, shoulders: the four places ego costs the most. Load them respectfully."},

  {"d": "physio", "e": "", "band": 1, "t": "Sit less during the day if you can. Movement snacks count."},

  {"d": "physio", "e": "", "band": 2, "t": "Control the last inch of every rep — the endpoints are where strains begin."},

  {"d": "physio", "e": "", "band": 3, "t": "Tendons need heavy-but-slow progressions (isometrics, then slow eccentrics) more than stretching."},

  {"d": "physio", "e": "", "band": 2, "t": "Warm up joints before stretching them cold; save long static stretches for after training."},

  {"d": "physio", "e": "", "band": 1, "t": "If an exercise hurts in a bad way today, pick a version that does not. Pain-free is the rule."},

  {"d": "physio", "e": "", "band": 3, "t": "Capacity beats caution: gradually strengthening a movement is better than avoiding it forever."},

  {"d": "physio", "e": "", "band": 2, "t": "Desk posture rarely 'needs fixing' but tissues love variety — alternate sitting, standing, walking."},

  {"d": "physio", "e": "", "band": 3, "t": "Breathe 360: ribs expanding on inhale, gentle brace on exhale, is the base of heavy lifting."},

  {"d": "physio", "e": "", "band": 2, "t": "Feet are foundations: strong arches and big toes carry knees and hips."},

  {"d": "physio", "e": "", "band": 1, "t": "Cramps usually mean tired muscles or low fluid — rest, stretch gently, drink."},

  {"d": "physio", "e": "", "band": 3, "t": "Return from a layoff at half your old weights for a week. Tendons re-adapt slower than muscles."},

  {"d": "physio", "e": "", "band": 2, "t": "Numbness or tingling that travels down a limb deserves a professional visit, not more reps."},

  {"d": "physio", "e": "", "band": 2, "t": "Rotate grip widths and stances to spread stress across tissues."},

  {"d": "physio", "e": "", "band": 1, "t": "Walking is therapy for almost everything."},

  {"d": "physio", "e": "", "band": 3, "t": "Contralateral loading (carry on one side, feel it on the whole chain) builds trunk resilience."},

  {"d": "physio", "e": "", "band": 2, "t": "Sleep position matters for shoulders: avoid sleeping directly on a cranky shoulder."},

  {"d": "physio", "e": "", "band": 2, "t": "Slow, boring rehab beats fast, painful rehab — adherence is the medicine."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Muscle grows from tension, effort, and enough protein — order matters less than presence."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Mechanical tension across a full range of motion is the primary hypertrophy driver."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "2-3 hard sets per exercise can capture most of the growth; more volume helps until it mostly adds fatigue."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Weekly sets of 10-20 per muscle group is a productive range for most trainees."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Strength is neural before it is muscular: early gains are your brain learning the movement."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Progressive overload works only when recovery keeps pace — sleep and food are half the stimulus."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Rep ranges 5-30 can build muscle when sets are taken close to failure; pick ranges you can control."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Frequency of 2+ sessions per muscle per week generally beats single-session equivalents at equal volume."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Eccentric (lowering) phases tolerate more load and create more soreness — control them."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Long rests (2-3 min) on compounds preserve volume-load and hypertrophy versus very short rests."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Time under tension matters less than proximity to failure with good technique."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Fiber types vary: high-twitch muscles (triceps, hamstrings) respond to heavier loads, low-twitch (soleus, core) to endurance work — your fiber windows encode this."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Cardio does not kill gains when programmed apart from leg work and eating enough."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Velocity loss within a set is a practical fatigue gauge: stop around 20-40% speed loss for hypertrophy."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Bodyweight changes need weeks, not days; daily weigh-ins are weather, weekly averages are climate."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Estimates like 1RM formulas are population math — recalibrate with real performance."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Warm-ups raise muscle temperature and prime the nervous system: ramp sets, not static stretching first."},

  {"d": "sports_sci", "e": "", "band": 1, "t": "More is not always better. Better is better."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Sessions >1 hour are fine; marathon sessions mostly add junk fatigue — split them if you can."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Skill moves (Olympic lifts, jumps) go early in a session while you are fresh."},

  {"d": "sports_sci", "e": "", "band": 3, "t": "Periodization is calendarized common sense: hard and easy alternate on purpose."},

  {"d": "sports_sci", "e": "", "band": 2, "t": "Deload every 4-8 weeks: same movements, half the volume, keep the habit alive."},

  {"d": "nutrition", "e": "", "band": 1, "t": "Protein at every meal helps muscles rebuild: eggs, beans, meat, fish, dairy, tofu all count."},

  {"d": "nutrition", "e": "", "band": 1, "t": "You do not need fancy powders. Food first, extras second."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Aim for roughly 1.6-2.2 g of protein per kg of body weight per day when training hard."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Around the workout, total daily intake matters more than perfect timing."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Carbs are training fuel: low carb and heavy legs make heavy legs heavier and sad."},

  {"d": "nutrition", "e": "", "band": 3, "t": "Creatine monohydrate (3-5 g/day) is the most studied, cheapest effective supplement for strength and power."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Caffeine helps performance but cut it 6-8 hours before sleep."},

  {"d": "nutrition", "e": "", "band": 1, "t": "Eat vegetables at least once a day. Your gut and recovery improve quietly."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Do not start a drastic diet during a hard training block — change one at a time."},

  {"d": "nutrition", "e": "", "band": 3, "t": "Energy availability (eating enough for the work you do) underpins hormones, bones, and mood."},

  {"d": "nutrition", "e": "", "band": 1, "t": "Drink water through the day; your pee should be pale, not neon."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Fiber keeps you full and healthy, but giant fiber right before training can bounce — time it."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Alcohol slows recovery and sleep quality; keep it away from hard training days."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Pre-workout meals: familiar, modest, some carbs and protein. Race day is not the day to experiment."},

  {"d": "nutrition", "e": "", "band": 3, "t": "Protein distribution across 3-4 meals of 0.3-0.5 g/kg supports muscle protein synthesis."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Whole foods first: shakes fill gaps, they are not the plan."},

  {"d": "nutrition", "e": "", "band": 1, "t": "If a food plan sounds like a punishment, it will not last. Build one you can live in."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Muscle building needs a small calorie surplus; fat loss needs a small deficit — not both at once."},

  {"d": "nutrition", "e": "", "band": 3, "t": "Refeeds and diet breaks protect training quality during long fat-loss phases."},

  {"d": "nutrition", "e": "", "band": 1, "t": "Grocery list = training plan. Stock the house with allies."},

  {"d": "nutrition", "e": "", "band": 2, "t": "Salt matters for heavy sweaters: replace sodium on long sweaty sessions, not just water."},

  {"d": "nutrition", "e": "", "band": 3, "t": "Hydration plus carbs improves repeated-sprint work; plain water is fine for short sessions."},

  {"d": "sleep", "e": "", "band": 1, "t": "Muscles are built at night. Sleep is training too."},

  {"d": "sleep", "e": "", "band": 1, "t": "Same bedtime every night makes everything grow better."},

  {"d": "sleep", "e": "", "band": 2, "t": "Aim for 7-9 hours; under 6 and strength, mood, and appetite control all sag."},

  {"d": "sleep", "e": "", "band": 2, "t": "Dark, cool, quiet: the three cheap upgrades every bedroom can rent for free."},

  {"d": "sleep", "e": "", "band": 2, "t": "Caffeine has a half-life of ~5-6 hours — afternoon coffee is still working at bedtime."},

  {"d": "sleep", "e": "", "band": 3, "t": "Sleep restriction of 2 hours measurably cuts max strength, sprint speed, and pain tolerance."},

  {"d": "sleep", "e": "", "band": 2, "t": "Screens: the light matters less than the scroll. Put the phone across the room."},

  {"d": "sleep", "e": "", "band": 2, "t": "Heavy training days: extra sleep is performance fuel, not laziness."},

  {"d": "sleep", "e": "", "band": 3, "t": "Naps of 20-30 minutes restore alertness without sleep inertia; 90 minutes covers a full cycle."},

  {"d": "sleep", "e": "", "band": 1, "t": "A regular wind-down — shower, stretch, book — tells your body the day is done."},

  {"d": "sleep", "e": "", "band": 2, "t": "Alcohol knocks you out but fragments the second half of the night."},

  {"d": "sleep", "e": "", "band": 3, "t": "Consistency (same sleep and wake times, ±30 min) rivals duration in metabolic studies."},

  {"d": "sleep", "e": "", "band": 2, "t": "Legs up the wall for 5 minutes before bed eases heavy-day legs."},

  {"d": "sleep", "e": "", "band": 1, "t": "If you cannot sleep after 20 minutes, get up, read something boring, try again."},

  {"d": "sleep", "e": "", "band": 3, "t": "Shift work? Anchor sleep to your longest rest window and guard it like a training session."},

  {"d": "sleep", "e": "", "band": 2, "t": "Night shift and heavy lifts: schedule your hardest session in your biological 'afternoon'."},

  {"d": "sleep", "e": "", "band": 2, "t": "Room too warm equals worse deep sleep; 17-19°C works for most people."},

  {"d": "sleep", "e": "", "band": 2, "t": "Eating a big meal right before bed can fragment sleep; finish eating ~2 hours before."},

  {"d": "sleep", "e": "", "band": 3, "t": "Tracking sleep is useful for trends, not scores — do not chase perfect numbers into insomnia."},

  {"d": "sleep", "e": "", "band": 1, "t": "Tired? Do the warm-up. If you still feel flat, take the extra rest guilt-free."},

  {"d": "sleep", "e": "", "band": 2, "t": "Snoring with daytime sleepiness deserves a professional check — apnea ruins recovery silently."},

  {"d": "sleep", "e": "", "band": 2, "t": "Morning light within an hour of waking sets tonight's sleep clock."},

  {"d": "psychology", "e": "", "band": 1, "t": "Talk to yourself like you would talk to a friend. Encouragement lifts more than insults."},

  {"d": "psychology", "e": "", "band": 1, "t": "Nervous before the gym is normal. Go anyway; nerves leave after the first set."},

  {"d": "psychology", "e": "", "band": 2, "t": "Process goals (train 3x this week) beat outcome goals (look different by summer) for mood and adherence."},

  {"d": "psychology", "e": "", "band": 2, "t": "Compare yourself to your last entry, not to anyone else's anything."},

  {"d": "psychology", "e": "", "band": 2, "t": "Two-minute rule: on bad days, just do the first two minutes. Momentum usually joins."},

  {"d": "psychology", "e": "", "band": 3, "t": "Self-compassion after a missed week predicts faster return than self-criticism — the research is clear."},

  {"d": "psychology", "e": "", "band": 2, "t": "Visualize the set before the set: feel the reps, then do them."},

  {"d": "psychology", "e": "", "band": 1, "t": "Finish every workout with one thing you did well. Say it to yourself."},

  {"d": "psychology", "e": "", "band": 3, "t": "Anxiety before heavy lifts narrows attention — long exhales (4 in, 6 out) restore control."},

  {"d": "psychology", "e": "", "band": 2, "t": "Progress is invisible in a day and undeniable in a photo a quarter apart. Take the photo."},

  {"d": "psychology", "e": "", "band": 2, "t": "Identity beats willpower: 'I am someone who trains' survives days that plans do not."},

  {"d": "psychology", "e": "", "band": 2, "t": "Music helps output; silence helps technique. Choose per session."},

  {"d": "psychology", "e": "", "band": 3, "t": "If the gym becomes a punishment room, the habit dies. Keep one workout a week pure fun."},

  {"d": "psychology", "e": "", "band": 1, "t": "Bad workout? You still showed up. That was the hard part."},

  {"d": "psychology", "e": "", "band": 3, "t": "Implementation intentions — 'after I close my laptop, I put on gym shoes' — double follow-through."},

  {"d": "psychology", "e": "", "band": 2, "t": "Fear of the big lift shrinks it: exposure in small increments, celebrated out loud."},

  {"d": "psychology", "e": "", "band": 2, "t": "Write tomorrow's first exercise tonight; a decided morning is a trained morning."},

  {"d": "psychology", "e": "", "band": 1, "t": "You are allowed to train for joy. Strong and happy is the actual point."},

  {"d": "psychology", "e": "", "band": 3, "t": "Burnout whispers before it shouts: dreading warm-ups for two weeks is the whisper. Deload."},

  {"d": "psychology", "e": "", "band": 2, "t": "A workout partner converts intention into appointment — the cheapest coach there is."},

  {"d": "psychology", "e": "", "band": 2, "t": "Track streaks but forgive one miss a month; the rule is 'never miss twice'."},

  {"d": "psychology", "e": "", "band": 3, "t": "Perfectionism is procrastination in gym clothes. Ship the workout."},

  {"d": "habits", "e": "", "band": 1, "t": "Put your gym clothes where you will trip over them."},

  {"d": "habits", "e": "", "band": 1, "t": "Same time, same days. The calendar is the coach."},

  {"d": "habits", "e": "", "band": 2, "t": "Stack the habit: after X (coffee, clock-out, kid drop-off), I train."},

  {"d": "habits", "e": "", "band": 2, "t": "Prepare the night before: clothes out, bag packed, first exercise chosen."},

  {"d": "habits", "e": "", "band": 3, "t": "Habit strength = frequency × reward. Make sessions short enough to repeat tomorrow."},

  {"d": "habits", "e": "", "band": 2, "t": "The 10-minute promise: start with ten; leave after if you truly want to (you rarely will)."},

  {"d": "habits", "e": "", "band": 2, "t": "Never miss twice. One miss is life; two is a new habit."},

  {"d": "habits", "e": "", "band": 1, "t": "Check the streak every day. Small circles want filling."},

  {"d": "habits", "e": "", "band": 2, "t": "Track workouts where you already look daily — the app home screen is a shrine, not a filing cabinet."},

  {"d": "habits", "e": "", "band": 3, "t": "Environment design beats discipline: fewer steps between you and movement wins by default."},

  {"d": "habits", "e": "", "band": 2, "t": "Tie training to identity moments: new job, new season, new Monday."},

  {"d": "habits", "e": "", "band": 1, "t": "Tell one person your plan. Friendly witnesses work."},

  {"d": "habits", "e": "", "band": 2, "t": "Weekly review, 5 minutes: what worked, what did not, what changes once."},

  {"d": "habits", "e": "", "band": 3, "t": "Flexible routines (any of 3 slots counts) survive chaotic weeks better than rigid ones."},

  {"d": "habits", "e": "", "band": 2, "t": "Make the easy path: keep a go-to 20-minute session for terrible days."},

  {"d": "habits", "e": "", "band": 2, "t": "Reward the reps: small ritual after training (nice shower, favorite show) seals the loop."},

  {"d": "habits", "e": "", "band": 1, "t": "Rest days are on the plan too. Rest them like a workout."},

  {"d": "habits", "e": "", "band": 3, "t": "Quarterly goals, weekly plans, daily reps: the nesting doll of progress."},

  {"d": "habits", "e": "", "band": 2, "t": "If a habit needs heroic motivation every time, redesign it smaller."},

  {"d": "habits", "e": "", "band": 2, "t": "Batch decisions: prepare two gym outfits on Sunday; decide nothing on Tuesday."},

  {"d": "habits", "e": "", "band": 2, "t": "Missed a week? Restart at 70% loads. Ego restarts at 100% and quits again."},

  {"d": "habits", "e": "", "band": 3, "t": "Milestones deserve celebrations: schedule them, or the brain stops believing."},

  {"d": "cardio", "e": "", "band": 1, "t": "You should be able to talk in short sentences during steady cardio. If you cannot, slow down."},

  {"d": "cardio", "e": "", "band": 1, "t": "Walk first. Brisk walks are cardio."},

  {"d": "cardio", "e": "", "band": 2, "t": "Zone 2 (easy, conversational) for most minutes, hard intervals sprinkled in — the 80/20 feel."},

  {"d": "cardio", "e": "", "band": 2, "t": "Two cardio days a week measurably helps heart health and recovery between heavy sets."},

  {"d": "cardio", "e": "", "band": 2, "t": "Stairs are a free interval machine."},

  {"d": "cardio", "e": "", "band": 3, "t": "Intervals: 30s hard / 90s easy × 6-10 builds VO2max efficiently once your base is set."},

  {"d": "cardio", "e": "", "band": 2, "t": "Bikes and rowers are knee-friendly ways to build an engine."},

  {"d": "cardio", "e": "", "band": 1, "t": "Count minutes per week, not calories: 150+ easy minutes is the public-health target."},

  {"d": "cardio", "e": "", "band": 3, "t": "Interference effect is real but small: separate cardio and legs by 6+ hours or alternate days."},

  {"d": "cardio", "e": "", "band": 2, "t": "Nasal breathing at easy pace is a built-in intensity governor."},

  {"d": "cardio", "e": "", "band": 2, "t": "Track resting heart rate monthly — a slow drift down means the engine is growing."},

  {"d": "cardio", "e": "", "band": 1, "t": "Dance, swim, hike, cycle: cardio you enjoy is the only cardio that lasts."},

  {"d": "cardio", "e": "", "band": 2, "t": "Chest strap or optical HR, both fine; just use the same one for trends."},

  {"d": "cardio", "e": "", "band": 3, "t": "Heart rate drift on long steady days signals dehydration or heat more than fitness loss."},

  {"d": "cardio", "e": "", "band": 2, "t": "Warm up 5 minutes before intervals like you would before heavy squats."},

  {"d": "cardio", "e": "", "band": 2, "t": "Cool down 3-5 minutes easy; the parking lot is not a cool-down."},

  {"d": "cardio", "e": "", "band": 3, "t": "Hill walks: cardio and strength overlapping, joints-friendly, ego-free."},

  {"d": "cardio", "e": "", "band": 1, "t": "Out of breath? That is the feeling of getting better. Pace yourself."},

  {"d": "cardio", "e": "", "band": 2, "t": "If sleep or resting HR is off two days running, swap intervals for an easy walk."},

  {"d": "cardio", "e": "", "band": 3, "t": "Cadence work (steps or pedal RPM) improves economy without adding strain."},

  {"d": "cardio", "e": "", "band": 2, "t": "Cardio after weights, not before, when strength is the priority."},

  {"d": "cardio", "e": "", "band": 2, "t": "Progress duration 10% per week at most — joints file complaints late but loudly."},

  {"d": "endurance", "e": "", "band": 2, "t": "Build the base before the speed: weeks of easy miles make hard work possible."},

  {"d": "endurance", "e": "", "band": 3, "t": "Polarized training (~80% easy, 20% hard) outperforms constant medium effort in most studies."},

  {"d": "endurance", "e": "", "band": 2, "t": "Fuel long sessions over 75-90 minutes with carbs (30-60 g/hour)."},

  {"d": "endurance", "e": "", "band": 3, "t": "Long slow distance teaches fat metabolism and tendon resilience — do not rush it into tempo."},

  {"d": "endurance", "e": "", "band": 2, "t": "One hard session per week is enough stimulus until recovery supports two."},

  {"d": "endurance", "e": "", "band": 1, "t": "Slow down. Everyone starts too fast. Everyone."},

  {"d": "endurance", "e": "", "band": 3, "t": "Cadence and foot strike debates matter less than consistent, gradual mileage."},

  {"d": "endurance", "e": "", "band": 2, "t": "Rotating shoes across two pairs may reduce repetitive stress; rotate surfaces too."},

  {"d": "endurance", "e": "", "band": 3, "t": "Tapering for an event: cut volume, keep intensity, arrive sharp not flat."},

  {"d": "endurance", "e": "", "band": 2, "t": "Practice race-day breakfast before race day, not on it."},

  {"d": "endurance", "e": "", "band": 3, "t": "Hydration plans belong to the athlete and the weather, not to the internet."},

  {"d": "endurance", "e": "", "band": 2, "t": "Muscular endurance comes from volume; strength comes from the weight room — blend both."},

  {"d": "endurance", "e": "", "band": 1, "t": "Walk breaks in long runs are strategy, not failure."},

  {"d": "endurance", "e": "", "band": 3, "t": "Heart-rate drift at constant pace = aerobic decoupling; when it grows, the aerobic base needs work."},

  {"d": "endurance", "e": "", "band": 2, "t": "Recovery weeks every 3-4 weeks: same routine, 30-50% less volume."},

  {"d": "endurance", "e": "", "band": 2, "t": "Cross-training (swim, bike) keeps the engine alive while joints rest."},

  {"d": "endurance", "e": "", "band": 3, "t": "Anaerobic capacity work (40s hard / 2-3min easy) is potent and expensive — use sparingly."},

  {"d": "endurance", "e": "", "band": 1, "t": "Sore shins? Shorten stride, soften surface, check shoes, rest."},

  {"d": "endurance", "e": "", "band": 2, "t": "Endurance appetite is real: long training days need planned meals, not apologies."},

  {"d": "endurance", "e": "", "band": 3, "t": "Consistency across months beats heroic single weeks — the aerobic system votes monthly."},

  {"d": "endurance", "e": "", "band": 2, "t": "Heat training: slow pace, more fluid, shade breaks; fitness shows up later anyway."},

  {"d": "endurance", "e": "", "band": 2, "t": "Breathe rhythmically (3 steps in, 2 out) to smooth effort at moderate paces."},

  {"d": "mobility", "e": "", "band": 1, "t": "Tight hips? Sit less and move more; stretching is a helper, not the hero."},

  {"d": "mobility", "e": "", "band": 2, "t": "Warm tissues stretch better: save the long holds for after training or a warm shower."},

  {"d": "mobility", "e": "", "band": 2, "t": "Active mobility (controlled end-range holds) transfers to lifting better than passive hanging out."},

  {"d": "mobility", "e": "", "band": 2, "t": "Ten focused minutes daily beats an hour once a week."},

  {"d": "mobility", "e": "", "band": 3, "t": "End-range control: lift, lower, and hold at your limit — that is where usable range is built."},

  {"d": "mobility", "e": "", "band": 1, "t": "Shoulders circle, hips circle, ankles circle. Five minutes, whole body happier."},

  {"d": "mobility", "e": "", "band": 2, "t": "Ankle dorsiflexion governs squat depth — train knees-over-toes gradually."},

  {"d": "mobility", "e": "", "band": 3, "t": "PNF (contract-relax) stretching reliably gains range when done consistently, not aggressively."},

  {"d": "mobility", "e": "", "band": 2, "t": "Thoracic rotations free up overhead pressing and desk-stiff afternoons."},

  {"d": "mobility", "e": "", "band": 1, "t": "Never bounce into sharp pain. Ease to strong tension and breathe."},

  {"d": "mobility", "e": "", "band": 2, "t": "Pair every stretch with a strengthener: flexible and strong beats bendy and fragile."},

  {"d": "mobility", "e": "", "band": 3, "t": "Carry positions (suitcase, front rack, overhead) are loaded mobility drills."},

  {"d": "mobility", "e": "", "band": 2, "t": "Hip flexors of sitters love couch stretch + glute bridges — the daily pair."},

  {"d": "mobility", "e": "", "band": 1, "t": "Neck rolls: slow and small. The neck is not a wheel."},

  {"d": "mobility", "e": "", "band": 3, "t": "Loaded progressive stretching (heavier holds over weeks) builds both range and tolerance."},

  {"d": "mobility", "e": "", "band": 2, "t": "Hamstrings respond to hinge patterns (RDLs) as much as to stretching."},

  {"d": "mobility", "e": "", "band": 2, "t": "Morning stiffness that fades in 10 minutes is normal; all-day stiffness deserves attention."},

  {"d": "mobility", "e": "", "band": 1, "t": "Stretch what is short, strengthen what is sleepy: most bodies are the same recipe."},

  {"d": "mobility", "e": "", "band": 3, "t": "Breath drives range: exhale into the last 10% of a stretch, never hold."},

  {"d": "mobility", "e": "", "band": 2, "t": "Foam rolling feels good and warms tissue; it does not 'release' anything permanently."},

  {"d": "mobility", "e": "", "band": 2, "t": "Desk resets every hour: stand, reach, rotate — 30 seconds."},

  {"d": "mobility", "e": "", "band": 3, "t": "Range you cannot control is range you cannot trust: own the whole arc."},

  {"d": "injury", "e": "", "band": 1, "t": "Warm up for five minutes. Cold muscles pull; warm muscles work."},

  {"d": "injury", "e": "", "band": 1, "t": "New shoes, new program, new PR week: pick one new thing at a time."},

  {"d": "injury", "e": "", "band": 2, "t": "Add load or volume about 10% a week — spikes are where strains are born."},

  {"d": "injury", "e": "", "band": 2, "t": "Pain that changes your form is pain you should listen to today, not next month."},

  {"d": "injury", "e": "", "band": 3, "t": "Tendons complain 24 hours late: yesterday's hero set is today's ache. Keep a training log and read it."},

  {"d": "injury", "e": "", "band": 2, "t": "Use safety pins/arms in racks; set them before you need them."},

  {"d": "injury", "e": "", "band": 1, "t": "Collars on barbells. Always collars."},

  {"d": "injury", "e": "", "band": 2, "t": "RPE 8 keeps technique trustworthy; RPE 11 invents new injuries."},

  {"d": "injury", "e": "", "band": 3, "t": "Isometrics (wall sits, Spanish squats) are pain-modulating and strength-building for cranky joints."},

  {"d": "injury", "e": "", "band": 2, "t": "Deload before you need one: scheduled every 4-8 weeks."},

  {"d": "injury", "e": "", "band": 1, "t": "Wipe the floor puddle or report it. Slip injuries end seasons."},

  {"d": "injury", "e": "", "band": 2, "t": "Return-to-lift ladder: half weight, full control, full weight over 2-3 weeks."},

  {"d": "injury", "e": "", "band": 2, "t": "Strengthen the unglamorous chain: wrists, neck, feet, grip."},

  {"d": "injury", "e": "", "band": 3, "t": "Eccentric-first progressions (slow lowering) harden hamstrings and Achilles against the classic tears."},

  {"d": "injury", "e": "", "band": 1, "t": "Stretching before heavy lifting does not prevent injury; warming up does. Stretch after."},

  {"d": "injury", "e": "", "band": 2, "t": "Anterior knee pain? Check volume of squats/stairs, add quad + hip strength, ease the range briefly."},

  {"d": "injury", "e": "", "band": 3, "t": "Low backs prefer strong trunks and calm minds: bracing, hinging, and sleep outperform belts alone."},

  {"d": "injury", "e": "", "band": 2, "t": "Belt up for maximal sets, not for warm-ups — let the trunk learn its job."},

  {"d": "injury", "e": "", "band": 2, "t": "Two days of worsening pain = professional assessment time."},

  {"d": "injury", "e": "", "band": 1, "t": "Lift with a buddy for max attempts. Safety and swagger."},

  {"d": "injury", "e": "", "band": 3, "t": "Watch load asymmetry: favoring one side after 'nothing happened' is a story your body is telling."},

  {"d": "injury", "e": "", "band": 2, "t": "Cracked plates, wobbly benches, frayed cables: report gear before it reports you."},

  {"d": "womens", "e": "", "band": 2, "t": "Strength training does not make you bulky overnight; it makes you strong, capable, and metabolically hungry in a good way."},

  {"d": "womens", "e": "", "band": 2, "t": "Cycle-aware training: many lifters feel strongest in the weeks after a period starts; adjust, do not assume."},

  {"d": "womens", "e": "", "band": 2, "t": "Iron status matters for female athletes — fatigue and breathlessness deserve a blood panel, not more cardio."},

  {"d": "womens", "e": "", "band": 2, "t": "Pelvic floor: heaviness or leaking during lifts is common and treatable — see a pelvic health physio, do not just quit lifting."},

  {"d": "womens", "e": "", "band": 3, "t": "Female athlete triad / RED-S (low energy, missing periods, bone risk) is a medical priority, not a discipline badge."},

  {"d": "womens", "e": "", "band": 1, "t": "Lift heavy within your ability. Heavy is relative and yours to define."},

  {"d": "womens", "e": "", "band": 2, "t": "Bone density is built young and maintained always — resistance work is the architect."},

  {"d": "womens", "e": "", "band": 2, "t": "Breast support matters: a good sports bra is performance equipment."},

  {"d": "womens", "e": "", "band": 2, "t": "Perimenopause is a strength opportunity, not a decline notice — protein and lifting matter more, not less."},

  {"d": "womens", "e": "", "band": 3, "t": "Hormone fluctuations affect perceived effort more than capacity most days; autoregulate with RPE."},

  {"d": "womens", "e": "", "band": 1, "t": "Period week feel flat? Keep moving, keep light, come back strong next week."},

  {"d": "womens", "e": "", "band": 2, "t": "Track performance, not just body weight — cycles move water weight daily."},

  {"d": "womens", "e": "", "band": 3, "t": "Creatine and adequate protein benefit women equally; dosing does not discriminate."},

  {"d": "womens", "e": "", "band": 2, "t": "Hip Q-angle and landing mechanics: add lateral and single-leg work for knee resilience."},

  {"d": "womens", "e": "", "band": 1, "t": "You belong on the squat rack. Full stop."},

  {"d": "womens", "e": "", "band": 3, "t": "Menopause HRT and training are not enemies — coordinate with your clinician."},

  {"d": "womens", "e": "", "band": 2, "t": "Glute and posterior chain emphasis pairs well with female biomechanics and goals alike."},

  {"d": "womens", "e": "", "band": 2, "t": "Iron, vitamin D, calcium: the quiet trio for hard-training women."},

  {"d": "womens", "e": "", "band": 1, "t": "Cycle tracking apps: useful, optional, and never a substitute for how you actually feel."},

  {"d": "womens", "e": "", "band": 2, "t": "Training while trying to conceive: moderate intensity is fine; avoid red-lining and overheating."},

  {"d": "womens", "e": "", "band": 3, "t": "ACL risk reduction lands on landing mechanics and hamstring strength — train both."},

  {"d": "womens", "e": "", "band": 2, "t": "Compare lifts to your own log. The barbell has no gender and no patience for comparisons."},

  {"d": "older", "e": "", "band": 1, "t": "After 50, strength is independence. Train it like rent."},

  {"d": "older", "e": "", "band": 2, "t": "Protein needs go UP with age (aim toward 1.6-2.0 g/kg), not down."},

  {"d": "older", "e": "", "band": 2, "t": "Balance work (single-leg stands, heel-to-toe walks) is fall insurance with dividends."},

  {"d": "older", "e": "", "band": 2, "t": "Power (lifting with intent) matters more than max strength for catching yourself mid-trip."},

  {"d": "older", "e": "", "band": 3, "t": "Sarcopenia responds at any age: 80-year-olds gain strength in trials — the window never closes."},

  {"d": "older", "e": "", "band": 1, "t": "Start with bodyweight and bands; add iron when movements feel easy."},

  {"d": "older", "e": "", "band": 2, "t": "Joint-friendly does not mean load-free: cartilage loves sensible, progressive loading."},

  {"d": "older", "e": "", "band": 2, "t": "Recovery takes a bit longer: 48-72h between hard sessions for the same muscles."},

  {"d": "older", "e": "", "band": 3, "t": "Grip strength predicts more than grip strength — train hangs, carries, and farmer walks."},

  {"d": "older", "e": "", "band": 2, "t": "Bone-building needs impact and load: brisk walking, hops (if joints allow), and resistance work."},

  {"d": "older", "e": "", "band": 1, "t": "Get up from the floor once a day on purpose. That is a real exercise."},

  {"d": "older", "e": "", "band": 2, "t": "Medications affect heart rate and hydration — review your program with your pharmacist."},

  {"d": "older", "e": "", "band": 2, "t": "Ankle strength and range guard against trips: calf raises are a superfood."},

  {"d": "older", "e": "", "band": 3, "t": "Combine balance + strength + walking in one session; adherence loves simple."},

  {"d": "older", "e": "", "band": 1, "t": "Breathe steadily during strength work; do not hold your breath through sets."},

  {"d": "older", "e": "", "band": 2, "t": "Vision and vestibular changes alter balance: train near support first, freedom later."},

  {"d": "older", "e": "", "band": 2, "t": "Soreness is fine; sharp pain is not — same rules at 70 as at 20, different speeds."},

  {"d": "older", "e": "", "band": 3, "t": "Quarterly strength check-ins (chair stand test, grip) turn 'feeling fine' into evidence."},

  {"d": "older", "e": "", "band": 2, "t": "Social training doubles adherence: classes, walking groups, gym buddies."},

  {"d": "older", "e": "", "band": 1, "t": "The best time to start was yesterday. The second best is today."},

  {"d": "older", "e": "", "band": 2, "t": "Hydration sense dulls with age: drink on schedule, not on thirst."},

  {"d": "older", "e": "", "band": 3, "t": "Resistance training plus adequate protein remains the only proven counter to muscle loss."},

  {"d": "teen", "e": "", "band": 1, "t": "Learn the movements first: bodyweight squats, push-ups, hinges. Speed comes later."},

  {"d": "teen", "e": "", "band": 2, "t": "Strength training does not stunt growth — that myth refuses to die, and the research refuses to support it."},

  {"d": "teen", "e": "", "band": 2, "t": "Supervised programs with good technique are the safest sports many teens can do."},

  {"d": "teen", "e": "", "band": 2, "t": "Bones finish building in your early 20s: loading now is a lifelong deposit."},

  {"d": "teen", "e": "", "band": 1, "t": "Play multiple sports and movements; variety builds athletes, early specialization builds injuries."},

  {"d": "teen", "e": "", "band": 2, "t": "Maximal 1RM testing is unnecessary for teens — progress by reps and technique instead."},

  {"d": "teen", "e": "", "band": 2, "t": "Growth spurts make old coordination new: be patient when your body changes size overnight."},

  {"d": "teen", "e": "", "band": 3, "t": "Resistance training adherence and enjoyment at 12-15 predicts adult activity — keep it fun or lose it."},

  {"d": "teen", "e": "", "band": 1, "t": "Sleep is your anabolic superpower: 8-10 hours is the teen target."},

  {"d": "teen", "e": "", "band": 2, "t": "School + sport + gym: watch the total stress, not just gym stress."},

  {"d": "teen", "e": "", "band": 2, "t": "Supplements: food, sleep, and coaching first. Creatine is reasonable later, not at 12."},

  {"d": "teen", "e": "", "band": 1, "t": "Compare yourself to you. Someone else's puberty is not your timeline."},

  {"d": "teen", "e": "", "band": 3, "t": "Motor learning windows: skills learned young stick — learn olympic lifts with a dowel early."},

  {"d": "teen", "e": "", "band": 2, "t": "Body image and social media: curate who you follow; lifting is for feeling capable, not for filters."},

  {"d": "teen", "e": "", "band": 2, "t": "Egos and plates: heavy singles can wait; technique trophies cannot."},

  {"d": "teen", "e": "", "band": 1, "t": "Bring a friend. Training with a buddy beats training alone at every age."},

  {"d": "teen", "e": "", "band": 3, "t": "Eccentric control (slow lowering) reduces teen sports injuries measurably."},

  {"d": "teen", "e": "", "band": 2, "t": "Hydration and lunch matter for afternoon sessions; school-day fueling is part of the plan."},

  {"d": "teen", "e": "", "band": 2, "t": "Coaches and parents: praise effort and technique, not outcomes — it builds resilient athletes."},

  {"d": "teen", "e": "", "band": 1, "t": "Soreness after new exercises is normal; sharp pain is not. Same rule for everyone."},

  {"d": "teen", "e": "", "band": 2, "t": "Screen time and posture: your neck votes too — strengthen it, not just your chest."},

  {"d": "teen", "e": "", "band": 3, "t": "Periodized school-year plans (off-season base, in-season maintenance) beat year-round maxing."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Train the ability you have today; the equipment is negotiable, the effort is not."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Seated training is full training: presses, rows, bands, and hell-yes energy."},

  {"d": "adaptive", "e": "", "band": 3, "t": "Spasticity and tone: slow tempo, full-range work and breathing help; sudden ballistic moves do not."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Wheelchair athletes: shoulder health programming is non-negotiable — balance push with pull, train scapular retractors."},

  {"d": "adaptive", "e": "", "band": 1, "t": "Ask before helping, always. Nothing about us without us."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Autonomy first: the athlete directs goals; the coach adapts the path."},

  {"d": "adaptive", "e": "", "band": 3, "t": "Autonomic dysreflexia (for spinal cord injuries above T6) is an emergency — know the signs before heavy sessions."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Amputee lifters: residual-limb skin care and socket fit schedule around training like any recovery variable."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Visual impairment: consistent gym layout and a sighted guide for new equipment."},

  {"d": "adaptive", "e": "", "band": 1, "t": "Adaptive does not mean easier. It means designed."},

  {"d": "adaptive", "e": "", "band": 3, "t": "Neuro conditions (MS, Parkinson's): exercise is medicine — schedule around fatigue windows and meds."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Chronic pain conditions: pacing beats pushing; graded exposure to movement retrusts the body."},

  {"d": "adaptive", "e": "", "band": 2, "t": " prosthetics and lifting: check skin after sessions the way runners check feet."},

  {"d": "adaptive", "e": "", "band": 3, "t": "FES (functional electrical stimulation) can add muscle stimulus when nerves need a translator."},

  {"d": "adaptive", "e": "", "band": 1, "t": "Accessible gyms are better gyms for everyone. Wider lanes, lower racks, kinder people."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Transfer skills (chair to bench) are technique too — practice them fresh, not exhausted."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Fatigue management for MS: morning sessions, cool rooms, short blocks."},

  {"d": "adaptive", "e": "", "band": 3, "t": "Contractures respond to gentle, frequent, sustained positioning — not heroic one-off stretching."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Communication is equipment: agree on cues (hand signals, lights) before intensity rises."},

  {"d": "adaptive", "e": "", "band": 1, "t": "Every gym is a disability gym the day its members decide it is."},

  {"d": "adaptive", "e": "", "band": 2, "t": "Track victories the world does not count: first independent transfer, first pain-free rep. They count here."},

  {"d": "adaptive", "e": "", "band": 3, "t": "Contraindication lists are starting points for clinical conversation, not permanent walls."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Movement in pregnancy is recommended for most people — confirm your specific plan with your clinician first."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "First-trimester fatigue is real: maintain, do not gain. There is a difference."},

  {"d": "pregnancy", "e": "", "band": 3, "t": "After the first trimester, avoid prolonged supine work (plenty of seated/incline alternatives exist) and watch for coning during core work."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Relaxin changes joint laxity: widen stances slightly, reduce impact, keep loads conversational."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Pelvic floor prep and strength beat panic Kegels — a pelvic health physio is worth the visit."},

  {"d": "pregnancy", "e": "", "band": 1, "t": "Walking, swimming, and light strength are the pregnant-athlete classics for good reason."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Hydration and cooling matter more now: skip hot rooms."},

  {"d": "pregnancy", "e": "", "band": 3, "t": "Alert signs stop training instantly: bleeding, fluid loss, dizziness, chest pain, contractions — call your provider."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Fuel includes the workout: pregnancy is not the season for deficits."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Strength maintenance supports labor, posture, and baby-carrying ergonomics later."},

  {"d": "pregnancy", "e": "", "band": 1, "t": "Nausea weeks: sessions can be 10 minutes. Ten honest minutes count."},

  {"d": "pregnancy", "e": "", "band": 3, "t": "Diastasis recti is common and manageable; pressure management (exhale on effort) is the skill."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Heart rate caps are outdated for most; the talk test and perceived effort are better guides."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Late pregnancy: step-ups, band rows, and supported squats keep the pattern bank full."},

  {"d": "pregnancy", "e": "", "band": 1, "t": "Trust the day you are having. Great days move more; rough days stretch and rest."},

  {"d": "pregnancy", "e": "", "band": 3, "t": "Postpartum return is a staircase: breath and walking, then strength, then impact — roughly weeks, not days, at each step."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "C-section recovery still needs core rebuilding — gently, progressively, ideally with a physio."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Baby-wearing farmer carries: the most honest progressive overload in fitness."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Leaking during lifts postpartum is a signal to see a pelvic physio, not a reason to quit lifting."},

  {"d": "pregnancy", "e": "", "band": 3, "t": "Return-to-impact checklist (jogging, jumping) comes after hop tests without symptoms — weeks 12+ typically."},

  {"d": "pregnancy", "e": "", "band": 1, "t": "Your body is doing the hardest program there is. Be a kind coach to it."},

  {"d": "pregnancy", "e": "", "band": 2, "t": "Lifting after baby: start with the baby. They gain weight weekly — built-in progression."},

  {"d": "hydration", "e": "", "band": 1, "t": "Water first. Most people are slightly dry most of the day."},

  {"d": "hydration", "e": "", "band": 2, "t": "Pale-yellow urine is the cheapest hydration gauge on earth."},

  {"d": "hydration", "e": "", "band": 2, "t": "For sessions under an hour, water covers it; sports drinks are food, not magic."},

  {"d": "hydration", "e": "", "band": 3, "t": "Sweat rates of 1-2 L/hour are common in heat; replace ~150% of losses over the following hours."},

  {"d": "hydration", "e": "", "band": 2, "t": "Sodium matters for heavy sweaters and salty sweat: cramps and sloshy stomachs often trace to it."},

  {"d": "hydration", "e": "", "band": 2, "t": "Thirst lags behind need in heat and age — drink on a schedule for long sessions."},

  {"d": "hydration", "e": "", "band": 1, "t": "Headache after training? Water first, then reassess."},

  {"d": "hydration", "e": "", "band": 3, "t": "Pre-cooling (cool fluids, shade) measurably improves hot-condition performance."},

  {"d": "hydration", "e": "", "band": 2, "t": "Caffeine is mildly diuretic but the fluid in coffee still counts for daily totals."},

  {"d": "hydration", "e": "", "band": 2, "t": "Over-hydration is real: drinking past thirst for hours can drop sodium dangerously (hyponatremia)."},

  {"d": "hydration", "e": "", "band": 2, "t": "Altitude, flights, and saunas all quietly dehydrate — plan extra fluid."},

  {"d": "hydration", "e": "", "band": 1, "t": "Carry a bottle. Bottles at hand get drunk; bottles elsewhere get wished for."},

  {"d": "hydration", "e": "", "band": 3, "t": "Glycogen binds water: low-carb days drop scale weight fast, mostly as water, not fat."},

  {"d": "hydration", "e": "", "band": 2, "t": "Electrolytes earn their price in heat, long sessions, and illness — otherwise food covers them."},

  {"d": "hydration", "e": "", "band": 2, "t": "Kids and teens heat up faster than adults: scheduled drink breaks in summer sport."},

  {"d": "hydration", "e": "", "band": 3, "t": "Body-mass checks before/after hard sessions teach you your own sweat rate within two weeks."},

  {"d": "hydration", "e": "", "band": 1, "t": "Dry lips, dry mouth, flat energy: the trio that means drink now."},

  {"d": "hydration", "e": "", "band": 2, "t": "Cold weather dehydrates too — dry air and layered sweat still leave."},

  {"d": "hydration", "e": "", "band": 2, "t": "Alcohol the night before steals tomorrow's session twice: sleep quality and fluid."},

  {"d": "hydration", "e": "", "band": 3, "t": "Fluid plus sodium plus carbs (2-3% solution) absorbs fastest during long endurance work."},

  {"d": "hydration", "e": "", "band": 1, "t": "Start sessions hydrated; catching up mid-workout is a losing race."},

  {"d": "hydration", "e": "", "band": 2, "t": "Urine vitamins make it neon: judge color on days without supplements."},

  {"d": "recovery", "e": "", "band": 1, "t": "Rest days are when the benefits get installed."},

  {"d": "recovery", "e": "", "band": 2, "t": "Sleep, food, and easy movement are the recovery big three; gadgets are garnish."},

  {"d": "recovery", "e": "", "band": 2, "t": "Light activity (walk, easy bike) clears soreness faster than the couch."},

  {"d": "recovery", "e": "", "band": 3, "t": "DOMS peaks 24-72h after novel work; it is not a scoreboard, just an invoice for novelty."},

  {"d": "recovery", "e": "", "band": 2, "t": "Contrast showers feel great and modestly help stiffness — nice to have, not need to have."},

  {"d": "recovery", "e": "", "band": 2, "t": "Massage and foam rolling reduce perceived soreness; use them because they help you feel good."},

  {"d": "recovery", "e": "", "band": 3, "t": "Cold plunges right after strength training may blunt hypertrophy signaling — time them away from lifting days if muscle is the goal."},

  {"d": "recovery", "e": "", "band": 2, "t": "Sauna use supports cardiovascular markers and feels like a medal — heat is a tool, not a test."},

  {"d": "recovery", "e": "", "band": 3, "t": "HRV trends are useful for spotting sustained stress, useless for daily verdicts."},

  {"d": "recovery", "e": "", "band": 2, "t": "Deload weeks: keep movements, cut sets in half, keep life happy."},

  {"d": "recovery", "e": "", "band": 1, "t": "Stretching before bed doubles as recovery and a wind-down."},

  {"d": "recovery", "e": "", "band": 2, "t": "Protein before sleep (casein or dairy) supports overnight repair — a small, easy win."},

  {"d": "recovery", "e": "", "band": 3, "t": "Active recovery sessions should feel suspiciously easy. If they have a scoreboard, they are training."},

  {"d": "recovery", "e": "", "band": 2, "t": "Stress is stress: arguments and deadlines tax the same recovery account as squats."},

  {"d": "recovery", "e": "", "band": 1, "t": "One full rest day a week minimum. The plan says so, and the plan is wise."},

  {"d": "recovery", "e": "", "band": 2, "t": "Feet up the wall for 5 minutes after long standing days — cheap, calm, effective."},

  {"d": "recovery", "e": "", "band": 3, "t": "Recovery capacity is trainable: repeated exposure to manageable stress + real rest expands both."},

  {"d": "recovery", "e": "", "band": 2, "t": "Naps under 30 minutes restore without grogginess; time them before mid-afternoon."},

  {"d": "recovery", "e": "", "band": 2, "t": "Illness above the neck (sniffles) tolerates light movement; below the neck (chest, fever) means rest."},

  {"d": "recovery", "e": "", "band": 1, "t": "Progressive overload includes progressive resting. They are one rule, not two."},

  {"d": "recovery", "e": "", "band": 3, "t": "Monitor mood + motivation + performance together: two of three sliding for two weeks = back off."},

  {"d": "recovery", "e": "", "band": 2, "t": "Recovery from sitting is not stretching — it is walking."},

  {"d": "safety", "e": "", "band": 1, "t": "Check the bench, check the pins, check the path. Ten seconds, every time."},

  {"d": "safety", "e": "", "band": 1, "t": "Collars on the bar. Every bar. Every time."},

  {"d": "safety", "e": "", "band": 2, "t": "Know where the emergency stop is on every machine you touch."},

  {"d": "safety", "e": "", "band": 2, "t": "Fail safe: set safeties at stick-point height before heavy bench or squat work."},

  {"d": "safety", "e": "", "band": 1, "t": "Spotters for max attempts — hands ready, eyes on the bar, phone away."},

  {"d": "safety", "e": "", "band": 2, "t": "Load plates gently; dropping steel from height cracks plates and ankles."},

  {"d": "safety", "e": "", "band": 2, "t": "Clip your laces, tie your shoes, and never stand immediately behind a rower's handle path."},

  {"d": "safety", "e": "", "band": 1, "t": "Weights belong on racks, not floors, not walkways."},

  {"d": "safety", "e": "", "band": 2, "t": "Wipe down and re-rack. Safety includes other people's ankles."},

  {"d": "safety", "e": "", "band": 2, "t": "Bar path awareness: no one walks behind a lifter mid-set. Ever."},

  {"d": "safety", "e": "", "band": 3, "t": "Home gym: flooring rated for dropped weight, and a plan for training alone (check-ins, camera)."},

  {"d": "safety", "e": "", "band": 2, "t": "Chalk is grip aid, not snow globe — keep clouds away from eyes and walkways."},

  {"d": "safety", "e": "", "band": 1, "t": "If you cannot lower it under control, it is too heavy today."},

  {"d": "safety", "e": "", "band": 2, "t": "Machines: adjust the seat first, load second; the wrong seat turns any lift into a crank."},

  {"d": "safety", "e": "", "band": 2, "t": "Keep jewelry and zippers away from knurling and cables."},

  {"d": "safety", "e": "", "band": 3, "t": "Learn the safety pin heights for your squat and bench and write them down — consistency is protection."},

  {"d": "safety", "e": "", "band": 2, "t": "Fatigue makes gear careless: at the end of sessions, extra-check your re-racking."},

  {"d": "safety", "e": "", "band": 1, "t": "Clear the drop zone before deadlifts — shins and phones will thank you."},

  {"d": "safety", "e": "", "band": 2, "t": "Kettlebell swings need a corridor: nobody passes through your arc."},

  {"d": "safety", "e": "", "band": 2, "t": "Report wobbly equipment immediately; a wobbly bench is a future ER story."},

  {"d": "safety", "e": "", "band": 3, "t": "Cardiac events in gyms are rare and survivable when someone knows CPR and where the AED lives — ask your gym."},

  {"d": "safety", "e": "", "band": 2, "t": "Headphones on, senses partly off: keep volume where you can hear 'incoming'."},

  {"d": "equipment", "e": "", "band": 1, "t": "Learn the pin and seat adjustments before adding weight — comfort is setup, not luck."},

  {"d": "equipment", "e": "", "band": 2, "t": "Barbells: 20kg men's bar, 15kg women's — check before loading your math."},

  {"d": "equipment", "e": "", "band": 2, "t": "Plates vary by system: kg vs lbs conversions have humbled many egos. Check the mold."},

  {"d": "equipment", "e": "", "band": 2, "t": "Dumbbells progress in jumps; use magnets/wrappers or slower tempo when the next jump is too big."},

  {"d": "equipment", "e": "", "band": 3, "t": "Machine cam profiles alter resistance curves — the 'easy' machine may just match your leverage better."},

  {"d": "equipment", "e": "", "band": 2, "t": "Cable stacks: check the pin is fully seated; half-seated pins slip under load."},

  {"d": "equipment", "e": "", "band": 2, "t": "Kettlebell handles: smooth finish, flat base; wobbly bells swing crooked."},

  {"d": "equipment", "e": "", "band": 2, "t": "Resistance bands are color-coded differently by every brand — label your set."},

  {"d": "equipment", "e": "", "band": 1, "t": "Worn-out shoes are quietly responsible for many 'mystery' knee and hip aches."},

  {"d": "equipment", "e": "", "band": 2, "t": "Hex/trap bars are friendlier to backs for beginners than straight bars — use them proudly."},

  {"d": "equipment", "e": "", "band": 3, "t": "Bar knurling and shaft diameter change grip demands; thick bars cut your max noticeably."},

  {"d": "equipment", "e": "", "band": 2, "t": "Smith machines remove stabilization demands — useful, different, not inferior; just count it honestly."},

  {"d": "equipment", "e": "", "band": 1, "t": "Home basics: a rack, an adjustable bench, some dumbbells or bands cover 90% of goals."},

  {"d": "equipment", "e": "", "band": 2, "t": "Adjustable benches: check the ladder locks before lying back."},

  {"d": "equipment", "e": "", "band": 2, "t": "Exercise mats: thin for stability work, thick for floor comfort, not for standing lifts."},

  {"d": "equipment", "e": "", "band": 3, "t": "Belt choice is comfort and task: 10mm powerlifting belts for max work, softer for general training."},

  {"d": "equipment", "e": "", "band": 2, "t": "Lifting straps help pulls past grip fatigue; use them to train the target, not to hide the weakness."},

  {"d": "equipment", "e": "", "band": 2, "t": "Weight vests add load to bodyweight work in small steps — start light, respect the joints."},

  {"d": "equipment", "e": "", "band": 1, "t": "Clean your equipment at home too; sweat is rust's favorite drink."},

  {"d": "equipment", "e": "", "band": 3, "t": "Calibrate: bathroom scales drift; so do plate molds labeled the same. Your log averages out the noise."},

  {"d": "equipment", "e": "", "band": 2, "t": "Safety arms and spotter pins have heights — set them for each lift, not once for all lifts."},

  {"d": "equipment", "e": "", "band": 2, "t": "Bands age: cracks, whitening, or sticky texture means retirement day."},

  {"d": "hygiene", "e": "", "band": 1, "t": "Towel on the bench: always yours, always down first."},

  {"d": "hygiene", "e": "", "band": 1, "t": "Wipe what you sweat on. Every time. No exceptions."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Gym shoes for the gym floor; street shoes track everything the last gym tracked."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Wash your gym bag weekly — it is the smelliest thing you own and the least suspected."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Ringworm and plantar warts travel on wet floors: sandals in locker rooms and showers."},

  {"d": "hygiene", "e": "", "band": 1, "t": "Cover cuts with waterproof plasters before lifting."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Wash hands after training before eating — the dumbbell is the second-dirtiest thing you touch today."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Chalk is shared equipment hygiene-wise too: keep your hands clean for the shared bag."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Water bottles get biofilm: hot wash or bottle-brush weekly."},

  {"d": "hygiene", "e": "", "band": 3, "t": "MRSA and skin infections spread by contact: any red, hot, spreading skin lesion needs medical eyes."},

  {"d": "hygiene", "e": "", "band": 1, "t": "Sick? Rest. Sharing a cold with 30 strangers is not gains."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Gloves and wraps wash weekly or they become bacteria hotels."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Your towel should never touch the floor and the bench on the same side."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Laundry rule: workout clothes get one wear, then a wash. Synthetics hold odor and bacteria."},

  {"d": "hygiene", "e": "", "band": 1, "t": "Cough or sneeze away from equipment and into your elbow."},

  {"d": "hygiene", "e": "", "band": 3, "t": "Foot fungus takes weeks to clear but seconds to catch — dry feet, fresh socks, ventilated shoes."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Shared mats (yoga, stretching areas): bring your own towel layer."},

  {"d": "hygiene", "e": "", "band": 1, "t": "Re-racking is hygiene too — dirty plates stack clean hands on someone's next session."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Shower sandals are a two-dollar investment with lifelong returns."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Clean your phone after the gym — it touches benches and then your face."},

  {"d": "hygiene", "e": "", "band": 3, "t": "Gym HVAC and crowding matter in flu season: off-peak hours are a health strategy."},

  {"d": "hygiene", "e": "", "band": 2, "t": "Skip the gym for 48h with fever or body-wide flu symptoms — training through it extends it."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "The three lifts are skills: squat, bench, deadlift get practiced weekly, drilled like sports."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Commands matter: pause benches in training or be surprised at the meet."},

  {"d": "powerlifting", "e": "", "band": 3, "t": "Meet-day attempts: opener is a lift you could triple, second is your training best, third earns its story."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Squat depth is judged by hip crease below knee top — film from the side and learn your honest depth."},

  {"d": "powerlifting", "e": "", "band": 3, "t": "Deadlift bar whip and stiff bars change the lift; train both if your fed allows."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Bench arch is a setup, not a bounce: leg drive through the floor makes the arch purposeful."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Peaking: volume drops, intensity climbs, last heavy session ~10 days out."},

  {"d": "powerlifting", "e": "", "band": 3, "t": "Weight cuts for powerlifting should be small (2-3% water) and practiced — never improvised."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Sumo or conventional: whichever moves the most weight with the least pain is correct."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Brace like you are bracing for a punch, not like you are posing."},

  {"d": "powerlifting", "e": "", "band": 3, "t": "Belt and knee sleeves are tools with rules; learn the fed's specs before buying for a meet."},

  {"d": "powerlifting", "e": "", "band": 1, "t": "Chalk everywhere is normal. Chalk on your forehead means you are a powerlifter now."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Squat walkout: three steps max — unwind, settle, breathe, go."},

  {"d": "powerlifting", "e": "", "band": 3, "t": "Attempt selection beats heroics: 9/9 successful attempts usually out-totals one heroic bomb."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Weak off the chest = paused benches; weak in the hole = pause squats; weak off floor = deficit pulls. Simple maps."},

  {"d": "powerlifting", "e": "", "band": 1, "t": "Train the lift you are avoiding. It is usually the one you need."},

  {"d": "powerlifting", "e": "", "band": 3, "t": "Fatigue management: powerlifting stress is neural — sleep and deloads are performance enhancers."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Second attempt is the money attempt: it sets the total. Respect it."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Squat shoes or flats: whatever keeps your stance honest and repeatable."},

  {"d": "powerlifting", "e": "", "band": 1, "t": "Every missed rep teaches the setup, not the strength. Review the video."},

  {"d": "powerlifting", "e": "", "band": 3, "t": "Off-season pulls: build muscle 6-8 weeks of the year; you cannot express what you do not have."},

  {"d": "powerlifting", "e": "", "band": 2, "t": "Platform etiquette: never walk behind a lifter mid-attempt, and cheer loudly for everyone."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Hypertrophy is tension + effort + food + time. Guard all four."},

  {"d": "bodybuilding", "e": "", "band": 3, "t": "Train muscles through long lengths under load: full stretch positions drive more growth."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Mind-muscle connection helps on isolation work; on heavy compounds, intent on moving the load wins."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Proximity to failure (0-3 reps in reserve) matters more than rep number."},

  {"d": "bodybuilding", "e": "", "band": 3, "t": "Volume landmarks: ~10-20 hard sets per muscle per week for most, with deloads every 4-8."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Exercise order is negotiable; exercise execution is not."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Progress photos: same light, same time, same poses. The mirror lies, the camera with a routine doesn't."},

  {"d": "bodybuilding", "e": "", "band": 1, "t": "Eat protein like it is your job, because today it was your muscles' job."},

  {"d": "bodybuilding", "e": "", "band": 3, "t": "Rep ranges: 5-30 works when near failure; choose ranges your joints enjoy."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Cut phases: keep protein high, keep lifting heavy, lose ~0.5-1% bodyweight per week."},

  {"d": "bodybuilding", "e": "", "band": 3, "t": "Cardio for cuts: start minimal, add only when weight stalls — it is a tool, not punishment."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Posing is training: it teaches control and reveals weak points honestly."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Pump work has value for metabolic stress but tension remains king."},

  {"d": "bodybuilding", "e": "", "band": 3, "t": "Rest intervals 1.5-3 minutes beat 30 seconds for growth at equal effort."},

  {"d": "bodybuilding", "e": "", "band": 1, "t": "Every set ends with control, not a crash landing."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Weak points get first position in the session and extra frequency — scarcity breeds priority."},

  {"d": "bodybuilding", "e": "", "band": 3, "t": "Exercise rotation: change when stalls or joint nags arrive, not for novelty alone."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Sleep is the cheapest anabolic you will ever find."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Bulk and cut cycles: smaller, longer, saner cycles protect health and hold gains better."},

  {"d": "bodybuilding", "e": "", "band": 1, "t": "Arms grow from rows, presses, and chin-ups more than from endless curls."},

  {"d": "bodybuilding", "e": "", "band": 3, "t": "Stretch-mediated hypertrophy (loaded stretch positions like incline curls) is legit — add one per muscle."},

  {"d": "bodybuilding", "e": "", "band": 2, "t": "Off-season weight: staying within ~10-15% of stage weight keeps the next prep humane."},

  {"d": "calisthenics", "e": "", "band": 1, "t": "Push-ups, rows, squats, hangs: the four pillars of bodyweight strength."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Progress by leverage, not just reps: elevate feet, slow the tempo, pause the bottom."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Scapular control (protraction/retraction) unlocks push-up and pull-up strength."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "The hollow body is the core position of all gymnastics strength — learn it early."},

  {"d": "calisthenics", "e": "", "band": 3, "t": "Planche and front lever progressions stress elbows and shoulders — add years, not weeks."},

  {"d": "calisthenics", "e": "", "band": 1, "t": "Hangs build grip and decompress after pressing days."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Pistol progressions: box pistols, assisted, then tempo. Ankles and hips both vote."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Ring work adds stabilization demands — start with support holds and scaled push-ups."},

  {"d": "calisthenics", "e": "", "band": 3, "t": "Isometrics: hold positions at 90-120% of your dynamic working angle to break plateaus."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Muscle-ups are a transition, not a pull — learn the transition slow with bands."},

  {"d": "calisthenics", "e": "", "band": 1, "t": "No pull-up bar? Rows under a sturdy table. Training finds a way."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Handstands are balance under load: wrist prep, wall sets, small kick-ups, patience."},

  {"d": "calisthenics", "e": "", "band": 3, "t": "Eccentrics (slow negatives) are the fastest road to first pull-ups, dips, and pistol squats."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Bodyweight high-rep sets build endurance; add load (vest) to keep building strength."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Grease the groove: frequent submaximal sets through the day teach skills fast."},

  {"d": "calisthenics", "e": "", "band": 1, "t": "Straight-arm strength is different from bent-arm — train both families."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Parallettes save wrists on push work and open L-sit training."},

  {"d": "calisthenics", "e": "", "band": 3, "t": "Tendon tolerance grows slower than muscle: volume jumps in skill work are the classic trap."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Pulling symmetry: train rows as much as pull-ups to protect shoulders."},

  {"d": "calisthenics", "e": "", "band": 1, "t": "Push-up hands: fingers spread, mid-palm pressure, elbows ~45°."},

  {"d": "calisthenics", "e": "", "band": 2, "t": "Skin care is training care: calluses file down, rips get cleaned and covered."},

  {"d": "calisthenics", "e": "", "band": 3, "t": "Freestyle flows need strong basics; acrobatics without fundamentals borrows trouble."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Your training time is whenever your energy is real: for night shifters that may be before work, not after."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Anchor sleep first; schedule training around your longest consistent sleep block, not the clock."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Night shift + heavy lifting: warm up longer; body temperature runs lower at biological night."},

  {"d": "shiftwork", "e": "", "band": 1, "t": "Pack gym clothes with your lunch. Decisions at shift-end are too tired to win."},

  {"d": "shiftwork", "e": "", "band": 3, "t": "Circadian disruption raises injury risk: favor technique loads, not PRs, during rotation weeks."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Rotating shifts: keep the workout pattern fixed to shift pattern (day 1 = always lower body, whatever day it lands on)."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Caffeine strategy: front-load the shift, cut it 6-8h before your anchor sleep."},

  {"d": "shiftwork", "e": "", "band": 1, "t": "Ten-minute sessions between shifts keep the habit alive until life normalizes."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Meal timing: the biggest meal goes after waking, not after midnight; pack food you actually like."},

  {"d": "shiftwork", "e": "", "band": 3, "t": "Light management is a shift worker's superpower: bright light on duty, sunglasses home, dark room asleep."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "On-the-job fitness counts: proper lifting mechanics at work IS training technique. Hinge, don't round."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Factory-friendly micro-breaks: 60 seconds of calf raises or band pulls every hour adds up."},

  {"d": "shiftwork", "e": "", "band": 1, "t": "Safety glasses off, hydration on: industrial work dehydrates like cardio."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Cumulative fatigue: standing 8 hours counts as training load — reduce standing cardio accordingly."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Protect your back at work and in the gym with the same hinge pattern; the bar and the crate obey the same physics."},

  {"d": "shiftwork", "e": "", "band": 3, "t": "Sleep debt from shift weeks: schedule deloads after rotation changes; they are predictable."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Night-shift gym access: home basics (bands, dumbbells, bench) beat a commute you will skip."},

  {"d": "shiftwork", "e": "", "band": 1, "t": "Eat before night shift like it's your morning, because biologically it is."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Grip and carry work directly transfers to workday tasks — farmer carries are job training."},

  {"d": "shiftwork", "e": "", "band": 3, "t": "Consistency beats heroics: three 30-minute sessions weekly through chaos outperforms a heroic plan you quit."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Weekend reversal sleep? Ease it: shift within 2 hours of anchor rather than flipping fully."},

  {"d": "shiftwork", "e": "", "band": 2, "t": "Buddy system: one coworker as training partner doubles follow-through on night shifts."},

  {"d": "mindfulness", "e": "", "band": 1, "t": "One deep breath before each set. The body follows the breath."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Count reps with full attention or count breaths — either way, be where your hands are."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Music out on warm-ups: listen to your body's actual state before you command it."},

  {"d": "mindfulness", "e": "", "band": 3, "t": "Interoception (reading your own signals) is trainable and improves load selection within weeks."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Meditation before training: 3 minutes of breath focus sharpens intent better than scrolling."},

  {"d": "mindfulness", "e": "", "band": 1, "t": "Notice one good thing your body did today. Every day. That is the whole practice."},

  {"d": "mindfulness", "e": "", "band": 3, "t": "Discomfort has textures: effort-burn is workable, alarm-pain is information. Learn the dialect."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Gratitude for the body that moves reframes training from punishment to privilege."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Walk between sets without your phone; let the mind file its tabs."},

  {"d": "mindfulness", "e": "", "band": 3, "t": "Yoga and lifting are complementary: control of breath, attention, and range transfer both ways."},

  {"d": "mindfulness", "e": "", "band": 1, "t": "Eyes on one spot during balance work steadies the body and the mind."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Post-set scan: jaw, shoulders, hands. Release what the set borrowed and never paid back."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Stress breathing (4-7-8) after work resets the evening session's nervous system."},

  {"d": "mindfulness", "e": "", "band": 3, "t": "Flow states show up when challenge meets skill: too easy bores, too hard frightens — adjust the dial."},

  {"d": "mindfulness", "e": "", "band": 1, "t": "Body scans in bed: feet to head, unclench as you go. Sleep comes faster."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Annoyed by gym crowds? Reframe: shared effort, shared space, everyone's mid-rep."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Meditation apps are fine; silence with a timer is free and identical."},

  {"d": "mindfulness", "e": "", "band": 3, "t": "Attentional cues (push the floor, spread the bar) outperform body-worry cues under load."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Emotional days: movement metabolizes stress chemistry — let the session process it."},

  {"d": "mindfulness", "e": "", "band": 1, "t": "Stretching is 20% tissue, 80% permission to be still. Accept both."},

  {"d": "mindfulness", "e": "", "band": 2, "t": "Progress journaling with one sentence of feeling alongside numbers trains self-awareness."},

  {"d": "mindfulness", "e": "", "band": 3, "t": "The observed lift is the safer lift: attention on task = technique + calm, together."},

];



/* P4 LEGAL DOCS — 13 documents, plain-language, Arizona-law aligned.

 * Rendered by p4_legal.js in the Legal Center; consent recorded in p4_consent_log. */

window.P4_LEGAL_DOCS = [

  {"id": "terms", "short": "Terms", "title": "Terms of Service", "sections": [{"h": "Welcome", "p": ["These Terms are an agreement between you and try4ever.com (\"we\", \"us\") for the fitness application served at fitness.try4ever.com and any offline copies of the same file (the \"App\"). By using the App, checking the agreement box, or generating your first workout, you accept these Terms. If you do not accept them, please stop using the App and delete your local data.", "The App is licensed to you, not sold. Your workout history, notes, and settings belong to you. We store them on your device — not on our servers — unless you explicitly use a connected feature that says otherwise."]}, {"h": "Who can use the App", "p": ["You must be at least 13 years old to use the App on your own. If you are between 13 and 17, a parent or legal guardian must review and accept these Terms and the Safety Agreement with you. The App is not directed to children under 13, and we do not knowingly collect data from them because the App keeps data on-device."]}, {"h": "Subscriptions & licenses", "p": ["The App offers a Free tier and a Pro tier (currently $19 per month, shown at checkout, plus tax where applicable). Pro may also be granted through a license key we issue at our discretion (for example through a free license application).", "Subscriptions renew automatically each month until cancelled. You can cancel at any time from inside the App or by contacting support@try4ever.com; access continues until the end of the paid period. Prices may change with 30 days' notice; changes never apply retroactively to a period you already paid for.", "We may offer free trials. Trials convert to paid access only if we clearly told you so at signup; otherwise they simply end."]}, {"h": "Acceptable use", "p": ["You agree not to:"], "ul": ["circumvent, disable, or attempt to defeat any license check, trial limit, or security feature of the App;", "reverse-engineer the App except where such restriction is prohibited by law;", "resell, sublicense, or commercially redistribute the App file itself;", "use the App to harass anyone, or to publish content that is unlawful or hateful;", "upload malicious code through any import feature."]}, {"h": "User content", "p": ["Exercises, feedback, and other content you add through the Library Studio remain yours. By using the optional sharing or export features you grant other users of your own device the obvious right to read that content — nothing more. We do not claim ownership of your workout data and we cannot see it."]}, {"h": "Disclaimers", "p": ["THE APP IS PROVIDED \"AS IS\" WITHOUT WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING FITNESS FOR A PARTICULAR PURPOSE. We do not warrant that the App will be uninterrupted or error-free. The App provides general fitness guidance, not medical advice; see the Health Disclaimer and Liability Waiver documents."]}, {"h": "Limitation of liability", "p": ["TO THE MAXIMUM EXTENT PERMITTED BY LAW, OUR TOTAL LIABILITY TO YOU FOR ANY CLAIM ARISING FROM THE APP IS LIMITED TO THE GREATER OF (A) THE AMOUNT YOU PAID US IN THE 12 MONTHS BEFORE THE CLAIM, OR (B) USD $19. We are not liable for indirect, incidental, special, consequential, or punitive damages, or for lost data — please use the backup feature; it exists for a reason."]}, {"h": "Termination & changes", "p": ["You may stop using the App at any time. We may modify or discontinue the App or any feature; if we discontinue something material to a paid tier, we will refund the unused portion of the current month on request. Sections that by their nature should survive termination (liability, disclaimers, indemnity) do survive."]}, {"h": "Governing law & disputes", "p": ["These Terms are governed by the laws of the State of Arizona, USA, excluding its conflict-of-laws rules. Small claims and consumer protection rights you have where you live are not affected. We will always try to resolve problems informally first — email support@try4ever.com and a human will answer."]}, {"h": "Contact", "p": ["try4ever.com · support@try4ever.com · fitness.try4ever.com. We answer support mail within 5 business days."]}]},

  {"id": "privacy", "short": "Privacy", "title": "Privacy Policy", "sections": [{"h": "The short version", "p": ["Your workouts, body data, and settings live in your browser's storage on your device. We do not operate accounts, we do not receive your workout data, and we do not sell anything about you. That is the whole philosophy of this App."]}, {"h": "What is stored, and where", "p": ["The App stores the following categories of data locally (localStorage and IndexedDB in your browser):"], "ul": ["Profile you choose to enter: name or nickname, birth date, gender, body weight and height, experience, goals;", "Workout history: exercises, sets, reps, weights, effort ratings, notes, timestamps;", "Preferences: theme, reading level, language, power-user settings;", "Consent records: which legal documents you accepted and when;", "License or subscription status: a local indicator that you paid, so the App can unlock features."]}, {"h": "What we receive", "p": ["Nothing by default. Loading the App from fitness.try4ever.com exposes standard web server logs (IP address, user agent, time) which are retained for up to 30 days for security and abuse prevention. If you subscribe, our payment processor receives your payment details — we never see your full card number. If you email support, we receive what you send and use it only to help you."]}, {"h": "Third parties", "p": ["The App loads a small number of public libraries from CDNs (for example fonts and chart libraries). Those providers may see your IP address as part of serving those files. The App itself contains no analytics, no advertising, and no tracking pixels. If a future version adds an optional connected feature, it will be opt-in and disclosed here first."]}, {"h": "Your rights & choices", "p": ["You can export everything (Settings → Export JSON), delete everything (Settings → Reset All Data), or clear site data in your browser at any time. Because we hold no server-side copy, deletion on your device is complete. If you are in the EEA/UK and believe we processed personal data about you, contact support@try4ever.com and we will help; our practical role is usually limited to the server logs described above."]}, {"h": "Children", "p": ["The App is not directed to children under 13. Because data stays on-device, we have no mechanism or desire to collect children's data. If you are a guardian and believe a child under 13 has entered data, delete it via Reset All Data."]}, {"h": "Changes", "p": ["If this policy changes materially we will change its version date and, where feasible, show a notice in the App. The current version always lives in the Legal Center."]}]},

  {"id": "waiver", "short": "Waiver", "title": "Liability Waiver & Release", "sections": [{"h": "Please read carefully — this affects your legal rights", "p": ["This Waiver is an agreement between you (and, if you are a minor, your parent or legal guardian who approves on your behalf) and try4ever.com. You accept it when you check the safety box during setup."]}, {"h": "Assumption of risk", "p": ["Physical exercise involves inherent risks, including muscle strains, joint injuries, falls, dizziness, fainting, heart events, and in rare cases serious injury or death. Risks are higher if you use heavy weights, train tired or dehydrated, use poor form, or train with a medical condition you have not disclosed to a professional.", "You voluntarily accept these risks and take responsibility for choosing exercises, weights, and effort levels that are right for your body on each day you train."]}, {"h": "Release", "p": ["To the fullest extent permitted by law, you release try4ever.com and its operators from all claims, liabilities, and causes of action arising out of your use of the App's guidance — including claims caused by our ordinary negligence — except where such release is void or unenforceable by law (for example for gross negligence or willful misconduct, or where you are a consumer with non-waivable statutory rights)."]}, {"h": "What this App is not", "p": ["The App is not a doctor, physical therapist, or certified personal trainer assigned to you. Its weight suggestions are mathematical estimates from population-level formulas and your own entries. It cannot see you, cannot correct your form, and cannot know how you feel. You are the expert on you."]}, {"h": "Minors", "p": ["If you are under 18, a parent or guardian must approve this Waiver. Guardians: you accept it on your own behalf and on behalf of the minor, and you agree to supervise training appropriate to the minor's age."]}, {"h": "Duration", "p": ["This Waiver applies to every use of the App for as long as your consent record remains in the App, including after you stop using the App for claims connected to earlier use."]}]},

  {"id": "medical", "short": "Health", "title": "Health & Fitness Disclaimer", "sections": [{"h": "Not medical advice", "p": ["All content in the App — exercises, weight ranges, plans, tips, and coach notes — is general fitness information for healthy adults and supervised teens. It is not medical advice, diagnosis, or treatment, and it is not a substitute for a qualified healthcare provider who knows you."]}, {"h": "Ask a professional first if…", "ul": ["you have a heart condition, chest pain, or a family history of early heart disease;", "you are pregnant or recently gave birth;", "you have diabetes, high or low blood pressure, dizziness, or joint problems;", "you take medication that affects heart rate or hydration;", "you are returning from surgery, injury, or a long time without exercise;", "you are under 18 (guardian supervision and pediatric guidance are smart)."]}, {"h": "Stop signals", "p": ["Stop training and seek help if you feel chest pain or pressure, unusual shortness of breath, sudden dizziness or confusion, sharp joint or bone pain, or numbness. Mild muscle burn and mild next-day soreness are normal; sharp pain is not \"good form with extra steps\" — it is a stop sign."]}, {"h": "Soreness vs. injury", "p": ["Soreness that peaks a day or two after training and eases with light movement is usually normal. Pain that is sharp, one-sided, localized to a joint, or worsening day over day is a signal to rest and, if it persists, to see a professional."]}]},

  {"id": "risk", "short": "Risk", "title": "Assumption of Risk Acknowledgment", "sections": [{"h": "Mechanics of this App", "p": ["The App derives suggested weights from your logged repetitions and set counts using established estimation formulas, clamped by per-muscle fiber profiles. These are heuristics: real bars can be mis-loaded, plates can be mismarked, and fatigue can hide a bad day."]}, {"h": "Your acknowledgments", "p": ["You acknowledge that: suggested weights are estimates and must be sanity-checked by you; you will use spotters or safety pins for heavy barbell work where available; you will secure collars and check equipment before loading; you will keep your training area clear of trip hazards; and you will not train to failure on exercises where failure could trap or drop weight on you."]}, {"h": "Progression philosophy", "p": ["The App's plans progress gradually on purpose. Skipping ahead, adding large jumps, or ignoring the recovery guidance increases injury risk and is done at your choice and risk."]}]},

  {"id": "refunds", "short": "Refunds", "title": "Refund & Cancellation Policy", "sections": [{"h": "Cancelling", "p": ["You can cancel a subscription any time from the App's Plan screen or by emailing support@try4ever.com. Cancellation stops future charges immediately; your benefits continue until the end of the period you already paid for. No phone calls, no dark patterns, no retention interrogation."]}, {"h": "Refunds", "p": ["If something went wrong — a double charge, a charge you did not authorize, or a feature that was materially broken for your whole billing month — email support@try4ever.com within 60 days and we will make it right, including full refunds where fair. Otherwise subscription fees for a period you used are generally non-refundable, because the App is delivered instantly and your data never depended on our servers.", "One-time license keys (including free promotional keys) are not refundable, but they are transferable to another one of your own devices by re-entering the key."]}, {"h": "Chargebacks", "p": ["Please contact us before filing a chargeback — we resolve almost everything faster than a bank can. Chargebacks filed without contacting us first may result in license deactivation until the dispute is resolved."]}]},

  {"id": "cookies", "short": "Storage", "title": "Cookies & Local Storage Notice", "sections": [{"h": "We use no tracking cookies", "p": ["The App sets no advertising or analytics cookies. It uses browser local storage and IndexedDB as a database — that is how a file-based app remembers your workouts without an account."]}, {"h": "Inventory", "p": ["Main storage keys and their purposes:"], "ul": ["workoutData — your entire workout history, profile, and app state;", "p4_theme / p4_vocab / p4_lang — appearance, reading level, language preferences;", "p4_profile / p4_consent_log — onboarding answers and consent history;", "p4_license / p4_trial_start / p4_sub_* — your license, trial, or subscription status;", "p4_badges* — achievements; p4_custom_ex — exercises you added; p4_snapshots — rolling safety copies;", "savedWorkout — the in-progress workout draft."]}, {"h": "Clearing", "p": ["Clearing site data in your browser deletes everything the App knows, permanently. Export a backup first — it is one tap."]}]},

  {"id": "accessibility", "short": "Access", "title": "Accessibility Statement", "sections": [{"h": "Our target", "p": ["We aim for WCAG 2.1 AA on the App's core flows: navigation, workout generation, logging, and settings. The interface uses semantic buttons and labels, visible focus rings, text contrast above 4.5:1 in both themes, and touch targets of at least 44 by 44 pixels — 58 pixels or more in Gym Mode."]}, {"h": "Built-in supports", "ul": ["Gym Mode: giant targets, larger text, animations disabled — designed for busy hands and low-vision use;", "Reading levels 1–10 re-word the interface from very simple to advanced English;", "Respects your operating system's reduced-motion setting;", "Works with browser zoom up to 200% and with common screen readers on standard flows."]}, {"h": "Known gaps", "p": ["Some legacy screens of the App still use click handlers that are less screen-reader friendly than we would like; charts are canvas-based and exposed only through text summaries. We are working through them. If something blocks you, email support@try4ever.com and we will prioritize the fix."]}]},

  {"id": "minors", "short": "Minors", "title": "Minor Protection & Parental Consent", "sections": [{"h": "Age rules", "p": ["Under 13: the App is not intended for solo use; a parent or guardian should set it up and train together with the child. 13–17: allowed with guardian approval recorded at onboarding. 18+: standard onboarding."]}, {"h": "What changes for teens", "p": ["Guidance emphasizes technique, bodyweight mastery, and moderate loads; the waiver is signed by a guardian; reminders avoid appearance-focused language and focus on strength, energy, sleep, and mood."]}, {"h": "No data collection from minors", "p": ["Because all data stays on the device, we never receive a minor's profile or history. Guardians control (and can erase) everything locally."]}]},

  {"id": "community", "short": "Content", "title": "Community & Content Policy", "sections": [{"h": "Scope", "p": ["This policy covers content you create or import: custom exercises, notes, images, feedback, and phrasebooks. It applies to content you share with others (for example by exporting JSON) and to what you import."]}, {"h": "Be excellent", "ul": ["Do not create or share content that is hateful, harassing, sexually explicit, or that promotes dangerous practices (for example extreme dehydration, training through sharp pain, or unsupervised maximal lifts for children);", "Do not import files from sources you do not trust — the App validates structure, but meaning is your responsibility;", "Respect other people's work: do not strip attribution from libraries that include it."]}, {"h": "Enforcement", "p": ["We cannot see your local content. If you share content publicly (for example posting an exported library), the hosting platform's rules apply. Content that violates law or platform rules can be reported there."]}]},

  {"id": "dmca", "short": "DMCA", "title": "Copyright & DMCA Policy", "sections": [{"h": "Ownership", "p": ["The App file, its code, prompts, and design are © try4ever.com. Exercise names and general training instructions describe common knowledge and public-domain training practice; where a library includes creative expression (photographs, long-form guides), it carries its own attribution."]}, {"h": "Takedown requests", "p": ["If you believe content accessible via fitness.try4ever.com infringes your copyright, send a notice to support@try4ever.com including: identification of the work, the URL or file location, your contact information, a good-faith statement, and a statement made under penalty of perjury that you are authorized to act. We respond within 5 business days and remove apparently infringing material while we review."]}, {"h": "Counter-notices", "p": ["If your content was removed and you believe that was a mistake, reply explaining why; we restore content where the complainant does not pursue the matter."]}]},

  {"id": "security", "short": "Security", "title": "Security & Vulnerability Disclosure", "sections": [{"h": "Design posture", "p": ["The App is a client-side file: the strongest guarantees are what it does NOT do — no accounts, no server-side data store, no analytics. Client-side license checks are a convenience lock, not a cryptographic boundary; we say so plainly instead of pretending otherwise."]}, {"h": "Safe harbor", "p": ["If you research the App in good faith: we will not pursue legal action for careful, non-destructive research that respects other users. Please do not test against other people's devices or data."]}, {"h": "Reporting", "p": ["Email security@try4ever.com with steps to reproduce. We acknowledge within 5 business days and aim to fix client-side issues in the next release. We credit reporters (with permission) in release notes."]}, {"h": "Out of scope", "ul": ["Attacks that require physical access to an unlocked device;", "Social engineering of our support inbox;", "Automated scanning that degrades the CDN experience for others."]}]},

  {"id": "contact", "short": "Contact", "title": "Contact & Company Information", "sections": [{"h": "Reach us", "ul": ["General support: support@try4ever.com (answers within 5 business days);", "Billing and refunds: support@try4ever.com with your payment date and device ID;", "Security: security@try4ever.com;", "Free license applications: apply@try4ever.com;", "Web: try4ever.com · App: fitness.try4ever.com"]}, {"h": "Operating identity", "p": ["The App is published by try4ever.com. References in these documents to \"we\", \"us\", and \"try4ever\" mean the operator of the try4ever.com domain and the App served from fitness.try4ever.com. Formal notices may be sent to the support address above with the subject line \"Legal Notice\"; we acknowledge legal notices within 5 business days and substantive responses follow within 15."]}, {"h": "Before you email", "p": ["Most answers live in the Legal Center and the FAQ on the plans page. For bugs, include what you tapped and what you expected — your local data never leaves your device unless you attach an export yourself."]}]},

];



/* P4 PHRASE STARTERS — example translations demonstrating the phrasebook

 * round-trip (export → translate with any AI → import). Not auto-applied;

 * imported via Settings > Words > Import phrases. */

window.P4_PHRASE_STARTERS = {

 "es": {

  "Home": [

   "Inicio",

   "Inicio",

   "Inicio",

   "Inicio",

   "Inicio",

   "Inicio",

   "Inicio",

   "Inicio",

   "Inicio",

   "Inicio"

  ],

  "Workout": [

   "Entreno",

   "Entreno",

   "Entreno",

   "Entreno",

   "Entreno",

   "Entreno",

   "Entreno",

   "Entreno",

   "Entreno",

   "Entreno"

  ],

  "History": [

   "Historial",

   "Historial",

   "Historial",

   "Historial",

   "Historial",

   "Historial",

   "Historial",

   "Historial",

   "Historial",

   "Historial"

  ],

  "Library": [

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca"

  ],

  "Progress": [

   "Progreso",

   "Progreso",

   "Progreso",

   "Progreso",

   "Progreso",

   "Progreso",

   "Progreso",

   "Progreso",

   "Progreso",

   "Progreso"

  ],

  "Recovery": [

   "Recuperación",

   "Recuperación",

   "Recuperación",

   "Recuperación",

   "Recuperación",

   "Recuperación",

   "Recuperación",

   "Recuperación",

   "Recuperación",

   "Recuperación"

  ],

  "Settings": [

   "Ajustes",

   "Ajustes",

   "Ajustes",

   "Ajustes",

   "Ajustes",

   "Ajustes",

   "Ajustes",

   "Ajustes",

   "Ajustes",

   "Ajustes"

  ],

  "Start Workout": [

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno",

   "Empezar entreno"

  ],

  "Save": [

   "Guardar",

   "Guardar",

   "Guardar",

   "Guardar",

   "Guardar",

   "Guardar",

   "Guardar",

   "Guardar",

   "Guardar",

   "Guardar"

  ],

  "Cancel": [

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar"

  ],

  "Close": [

   "Cerrar",

   "Cerrar",

   "Cerrar",

   "Cerrar",

   "Cerrar",

   "Cerrar",

   "Cerrar",

   "Cerrar",

   "Cerrar",

   "Cerrar"

  ],

  "Continue": [

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar"

  ],

  "Back": [

   "Atrás",

   "Atrás",

   "Atrás",

   "Atrás",

   "Atrás",

   "Atrás",

   "Atrás",

   "Atrás",

   "Atrás",

   "Atrás"

  ],

  "Next": [

   "Siguiente",

   "Siguiente",

   "Siguiente",

   "Siguiente",

   "Siguiente",

   "Siguiente",

   "Siguiente",

   "Siguiente",

   "Siguiente",

   "Siguiente"

  ],

  "Skip": [

   "Omitir",

   "Omitir",

   "Omitir",

   "Omitir",

   "Omitir",

   "Omitir",

   "Omitir",

   "Omitir",

   "Omitir",

   "Omitir"

  ],

  "Finish": [

   "Terminar",

   "Terminar",

   "Terminar",

   "Terminar",

   "Terminar",

   "Terminar",

   "Terminar",

   "Terminar",

   "Terminar",

   "Terminar"

  ],

  "Streak": [

   "Racha",

   "Racha",

   "Racha",

   "Racha",

   "Racha",

   "Racha",

   "Racha",

   "Racha",

   "Racha",

   "Racha"

  ],

  "Readiness": [

   "Preparación",

   "Preparación",

   "Preparación",

   "Preparación",

   "Preparación",

   "Preparación",

   "Preparación",

   "Preparación",

   "Preparación",

   "Preparación"

  ],

  "Workouts": [

   "Entrenos",

   "Entrenos",

   "Entrenos",

   "Entrenos",

   "Entrenos",

   "Entrenos",

   "Entrenos",

   "Entrenos",

   "Entrenos",

   "Entrenos"

  ],

  "Total Volume:": [

   "Volumen total:",

   "Volumen total:",

   "Volumen total:",

   "Volumen total:",

   "Volumen total:",

   "Volumen total:",

   "Volumen total:",

   "Volumen total:",

   "Volumen total:",

   "Volumen total:"

  ],

  "Sets Completed:": [

   "Series hechas:",

   "Series hechas:",

   "Series hechas:",

   "Series hechas:",

   "Series hechas:",

   "Series hechas:",

   "Series hechas:",

   "Series hechas:",

   "Series hechas:",

   "Series hechas:"

  ],

  "Male": [

   "Hombre",

   "Hombre",

   "Hombre",

   "Hombre",

   "Hombre",

   "Hombre",

   "Hombre",

   "Hombre",

   "Hombre",

   "Hombre"

  ],

  "Female": [

   "Mujer",

   "Mujer",

   "Mujer",

   "Mujer",

   "Mujer",

   "Mujer",

   "Mujer",

   "Mujer",

   "Mujer",

   "Mujer"

  ],

  "Free": [

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis"

  ],

  "Pro": [

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro"

  ],

  "Badges": [

   "Insignias",

   "Insignias",

   "Insignias",

   "Insignias",

   "Insignias",

   "Insignias",

   "Insignias",

   "Insignias",

   "Insignias",

   "Insignias"

  ],

  "Backup": [

   "Copia",

   "Copia",

   "Copia",

   "Copia",

   "Copia",

   "Copia",

   "Copia",

   "Copia",

   "Copia",

   "Copia"

  ],

  "Get Started": [

   "Empezar",

   "Empezar",

   "Empezar",

   "Empezar",

   "Empezar",

   "Empezar",

   "Empezar",

   "Empezar",

   "Empezar",

   "Empezar"

  ],

  "I agree": [

   "Acepto",

   "Acepto",

   "Acepto",

   "Acepto",

   "Acepto",

   "Acepto",

   "Acepto",

   "Acepto",

   "Acepto",

   "Acepto"

  ],

  "Upgrade to Pro": [

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro",

   "Mejorar a Pro"

  ]

 },

 "fr": {

  "Home": [

   "Accueil",

   "Accueil",

   "Accueil",

   "Accueil",

   "Accueil",

   "Accueil",

   "Accueil",

   "Accueil",

   "Accueil",

   "Accueil"

  ],

  "Workout": [

   "Séance",

   "Séance",

   "Séance",

   "Séance",

   "Séance",

   "Séance",

   "Séance",

   "Séance",

   "Séance",

   "Séance"

  ],

  "History": [

   "Historique",

   "Historique",

   "Historique",

   "Historique",

   "Historique",

   "Historique",

   "Historique",

   "Historique",

   "Historique",

   "Historique"

  ],

  "Library": [

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque",

   "Bibliothèque"

  ],

  "Progress": [

   "Progrès",

   "Progrès",

   "Progrès",

   "Progrès",

   "Progrès",

   "Progrès",

   "Progrès",

   "Progrès",

   "Progrès",

   "Progrès"

  ],

  "Recovery": [

   "Récupération",

   "Récupération",

   "Récupération",

   "Récupération",

   "Récupération",

   "Récupération",

   "Récupération",

   "Récupération",

   "Récupération",

   "Récupération"

  ],

  "Settings": [

   "Réglages",

   "Réglages",

   "Réglages",

   "Réglages",

   "Réglages",

   "Réglages",

   "Réglages",

   "Réglages",

   "Réglages",

   "Réglages"

  ],

  "Start Workout": [

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance",

   "Commencer la séance"

  ],

  "Save": [

   "Enregistrer",

   "Enregistrer",

   "Enregistrer",

   "Enregistrer",

   "Enregistrer",

   "Enregistrer",

   "Enregistrer",

   "Enregistrer",

   "Enregistrer",

   "Enregistrer"

  ],

  "Cancel": [

   "Annuler",

   "Annuler",

   "Annuler",

   "Annuler",

   "Annuler",

   "Annuler",

   "Annuler",

   "Annuler",

   "Annuler",

   "Annuler"

  ],

  "Close": [

   "Fermer",

   "Fermer",

   "Fermer",

   "Fermer",

   "Fermer",

   "Fermer",

   "Fermer",

   "Fermer",

   "Fermer",

   "Fermer"

  ],

  "Continue": [

   "Continuer",

   "Continuer",

   "Continuer",

   "Continuer",

   "Continuer",

   "Continuer",

   "Continuer",

   "Continuer",

   "Continuer",

   "Continuer"

  ],

  "Back": [

   "Retour",

   "Retour",

   "Retour",

   "Retour",

   "Retour",

   "Retour",

   "Retour",

   "Retour",

   "Retour",

   "Retour"

  ],

  "Next": [

   "Suivant",

   "Suivant",

   "Suivant",

   "Suivant",

   "Suivant",

   "Suivant",

   "Suivant",

   "Suivant",

   "Suivant",

   "Suivant"

  ],

  "Skip": [

   "Passer",

   "Passer",

   "Passer",

   "Passer",

   "Passer",

   "Passer",

   "Passer",

   "Passer",

   "Passer",

   "Passer"

  ],

  "Finish": [

   "Terminer",

   "Terminer",

   "Terminer",

   "Terminer",

   "Terminer",

   "Terminer",

   "Terminer",

   "Terminer",

   "Terminer",

   "Terminer"

  ],

  "Streak": [

   "Série",

   "Série",

   "Série",

   "Série",

   "Série",

   "Série",

   "Série",

   "Série",

   "Série",

   "Série"

  ],

  "Readiness": [

   "Préparation",

   "Préparation",

   "Préparation",

   "Préparation",

   "Préparation",

   "Préparation",

   "Préparation",

   "Préparation",

   "Préparation",

   "Préparation"

  ],

  "Workouts": [

   "Séances",

   "Séances",

   "Séances",

   "Séances",

   "Séances",

   "Séances",

   "Séances",

   "Séances",

   "Séances",

   "Séances"

  ],

  "Total Volume:": [

   "Volume total :",

   "Volume total :",

   "Volume total :",

   "Volume total :",

   "Volume total :",

   "Volume total :",

   "Volume total :",

   "Volume total :",

   "Volume total :",

   "Volume total :"

  ],

  "Sets Completed:": [

   "Séries faites :",

   "Séries faites :",

   "Séries faites :",

   "Séries faites :",

   "Séries faites :",

   "Séries faites :",

   "Séries faites :",

   "Séries faites :",

   "Séries faites :",

   "Séries faites :"

  ],

  "Male": [

   "Homme",

   "Homme",

   "Homme",

   "Homme",

   "Homme",

   "Homme",

   "Homme",

   "Homme",

   "Homme",

   "Homme"

  ],

  "Female": [

   "Femme",

   "Femme",

   "Femme",

   "Femme",

   "Femme",

   "Femme",

   "Femme",

   "Femme",

   "Femme",

   "Femme"

  ],

  "Free": [

   "Gratuit",

   "Gratuit",

   "Gratuit",

   "Gratuit",

   "Gratuit",

   "Gratuit",

   "Gratuit",

   "Gratuit",

   "Gratuit",

   "Gratuit"

  ],

  "Pro": [

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro"

  ],

  "Badges": [

   "Badges",

   "Badges",

   "Badges",

   "Badges",

   "Badges",

   "Badges",

   "Badges",

   "Badges",

   "Badges",

   "Badges"

  ],

  "Backup": [

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde",

   "Sauvegarde"

  ],

  "Get Started": [

   "Commencer",

   "Commencer",

   "Commencer",

   "Commencer",

   "Commencer",

   "Commencer",

   "Commencer",

   "Commencer",

   "Commencer",

   "Commencer"

  ],

  "I agree": [

   "J'accepte",

   "J'accepte",

   "J'accepte",

   "J'accepte",

   "J'accepte",

   "J'accepte",

   "J'accepte",

   "J'accepte",

   "J'accepte",

   "J'accepte"

  ],

  "Upgrade to Pro": [

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro",

   "Passer à Pro"

  ]

 },

 "de": {

  "Home": [

   "Start",

   "Start",

   "Start",

   "Start",

   "Start",

   "Start",

   "Start",

   "Start",

   "Start",

   "Start"

  ],

  "Workout": [

   "Training",

   "Training",

   "Training",

   "Training",

   "Training",

   "Training",

   "Training",

   "Training",

   "Training",

   "Training"

  ],

  "History": [

   "Verlauf",

   "Verlauf",

   "Verlauf",

   "Verlauf",

   "Verlauf",

   "Verlauf",

   "Verlauf",

   "Verlauf",

   "Verlauf",

   "Verlauf"

  ],

  "Library": [

   "Bibliothek",

   "Bibliothek",

   "Bibliothek",

   "Bibliothek",

   "Bibliothek",

   "Bibliothek",

   "Bibliothek",

   "Bibliothek",

   "Bibliothek",

   "Bibliothek"

  ],

  "Progress": [

   "Fortschritt",

   "Fortschritt",

   "Fortschritt",

   "Fortschritt",

   "Fortschritt",

   "Fortschritt",

   "Fortschritt",

   "Fortschritt",

   "Fortschritt",

   "Fortschritt"

  ],

  "Recovery": [

   "Erholung",

   "Erholung",

   "Erholung",

   "Erholung",

   "Erholung",

   "Erholung",

   "Erholung",

   "Erholung",

   "Erholung",

   "Erholung"

  ],

  "Settings": [

   "Einstellungen",

   "Einstellungen",

   "Einstellungen",

   "Einstellungen",

   "Einstellungen",

   "Einstellungen",

   "Einstellungen",

   "Einstellungen",

   "Einstellungen",

   "Einstellungen"

  ],

  "Start Workout": [

   "Training starten",

   "Training starten",

   "Training starten",

   "Training starten",

   "Training starten",

   "Training starten",

   "Training starten",

   "Training starten",

   "Training starten",

   "Training starten"

  ],

  "Save": [

   "Speichern",

   "Speichern",

   "Speichern",

   "Speichern",

   "Speichern",

   "Speichern",

   "Speichern",

   "Speichern",

   "Speichern",

   "Speichern"

  ],

  "Cancel": [

   "Abbrechen",

   "Abbrechen",

   "Abbrechen",

   "Abbrechen",

   "Abbrechen",

   "Abbrechen",

   "Abbrechen",

   "Abbrechen",

   "Abbrechen",

   "Abbrechen"

  ],

  "Close": [

   "Schließen",

   "Schließen",

   "Schließen",

   "Schließen",

   "Schließen",

   "Schließen",

   "Schließen",

   "Schließen",

   "Schließen",

   "Schließen"

  ],

  "Continue": [

   "Fortfahren",

   "Fortfahren",

   "Fortfahren",

   "Fortfahren",

   "Fortfahren",

   "Fortfahren",

   "Fortfahren",

   "Fortfahren",

   "Fortfahren",

   "Fortfahren"

  ],

  "Back": [

   "Zurück",

   "Zurück",

   "Zurück",

   "Zurück",

   "Zurück",

   "Zurück",

   "Zurück",

   "Zurück",

   "Zurück",

   "Zurück"

  ],

  "Next": [

   "Weiter",

   "Weiter",

   "Weiter",

   "Weiter",

   "Weiter",

   "Weiter",

   "Weiter",

   "Weiter",

   "Weiter",

   "Weiter"

  ],

  "Skip": [

   "Überspringen",

   "Überspringen",

   "Überspringen",

   "Überspringen",

   "Überspringen",

   "Überspringen",

   "Überspringen",

   "Überspringen",

   "Überspringen",

   "Überspringen"

  ],

  "Finish": [

   "Fertig",

   "Fertig",

   "Fertig",

   "Fertig",

   "Fertig",

   "Fertig",

   "Fertig",

   "Fertig",

   "Fertig",

   "Fertig"

  ],

  "Streak": [

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie"

  ],

  "Readiness": [

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft",

   "Bereitschaft"

  ],

  "Workouts": [

   "Trainings",

   "Trainings",

   "Trainings",

   "Trainings",

   "Trainings",

   "Trainings",

   "Trainings",

   "Trainings",

   "Trainings",

   "Trainings"

  ],

  "Total Volume:": [

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:",

   "Gesamtvolumen:"

  ],

  "Sets Completed:": [

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:",

   "Sätze geschafft:"

  ],

  "Male": [

   "Männlich",

   "Männlich",

   "Männlich",

   "Männlich",

   "Männlich",

   "Männlich",

   "Männlich",

   "Männlich",

   "Männlich",

   "Männlich"

  ],

  "Female": [

   "Weiblich",

   "Weiblich",

   "Weiblich",

   "Weiblich",

   "Weiblich",

   "Weiblich",

   "Weiblich",

   "Weiblich",

   "Weiblich",

   "Weiblich"

  ],

  "Free": [

   "Kostenlos",

   "Kostenlos",

   "Kostenlos",

   "Kostenlos",

   "Kostenlos",

   "Kostenlos",

   "Kostenlos",

   "Kostenlos",

   "Kostenlos",

   "Kostenlos"

  ],

  "Pro": [

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro"

  ],

  "Badges": [

   "Abzeichen",

   "Abzeichen",

   "Abzeichen",

   "Abzeichen",

   "Abzeichen",

   "Abzeichen",

   "Abzeichen",

   "Abzeichen",

   "Abzeichen",

   "Abzeichen"

  ],

  "Backup": [

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup"

  ],

  "Get Started": [

   "Los geht's",

   "Los geht's",

   "Los geht's",

   "Los geht's",

   "Los geht's",

   "Los geht's",

   "Los geht's",

   "Los geht's",

   "Los geht's",

   "Los geht's"

  ],

  "I agree": [

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu",

   "Ich stimme zu"

  ],

  "Upgrade to Pro": [

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden",

   "Auf Pro upgraden"

  ]

 },

 "pt": {

  "Home": [

   "Início",

   "Início",

   "Início",

   "Início",

   "Início",

   "Início",

   "Início",

   "Início",

   "Início",

   "Início"

  ],

  "Workout": [

   "Treino",

   "Treino",

   "Treino",

   "Treino",

   "Treino",

   "Treino",

   "Treino",

   "Treino",

   "Treino",

   "Treino"

  ],

  "History": [

   "Histórico",

   "Histórico",

   "Histórico",

   "Histórico",

   "Histórico",

   "Histórico",

   "Histórico",

   "Histórico",

   "Histórico",

   "Histórico"

  ],

  "Library": [

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca",

   "Biblioteca"

  ],

  "Progress": [

   "Progresso",

   "Progresso",

   "Progresso",

   "Progresso",

   "Progresso",

   "Progresso",

   "Progresso",

   "Progresso",

   "Progresso",

   "Progresso"

  ],

  "Recovery": [

   "Recuperação",

   "Recuperação",

   "Recuperação",

   "Recuperação",

   "Recuperação",

   "Recuperação",

   "Recuperação",

   "Recuperação",

   "Recuperação",

   "Recuperação"

  ],

  "Settings": [

   "Configurações",

   "Configurações",

   "Configurações",

   "Configurações",

   "Configurações",

   "Configurações",

   "Configurações",

   "Configurações",

   "Configurações",

   "Configurações"

  ],

  "Start Workout": [

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino",

   "Iniciar treino"

  ],

  "Save": [

   "Salvar",

   "Salvar",

   "Salvar",

   "Salvar",

   "Salvar",

   "Salvar",

   "Salvar",

   "Salvar",

   "Salvar",

   "Salvar"

  ],

  "Cancel": [

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar",

   "Cancelar"

  ],

  "Close": [

   "Fechar",

   "Fechar",

   "Fechar",

   "Fechar",

   "Fechar",

   "Fechar",

   "Fechar",

   "Fechar",

   "Fechar",

   "Fechar"

  ],

  "Continue": [

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar",

   "Continuar"

  ],

  "Back": [

   "Voltar",

   "Voltar",

   "Voltar",

   "Voltar",

   "Voltar",

   "Voltar",

   "Voltar",

   "Voltar",

   "Voltar",

   "Voltar"

  ],

  "Next": [

   "Próximo",

   "Próximo",

   "Próximo",

   "Próximo",

   "Próximo",

   "Próximo",

   "Próximo",

   "Próximo",

   "Próximo",

   "Próximo"

  ],

  "Skip": [

   "Pular",

   "Pular",

   "Pular",

   "Pular",

   "Pular",

   "Pular",

   "Pular",

   "Pular",

   "Pular",

   "Pular"

  ],

  "Finish": [

   "Concluir",

   "Concluir",

   "Concluir",

   "Concluir",

   "Concluir",

   "Concluir",

   "Concluir",

   "Concluir",

   "Concluir",

   "Concluir"

  ],

  "Streak": [

   "Sequência",

   "Sequência",

   "Sequência",

   "Sequência",

   "Sequência",

   "Sequência",

   "Sequência",

   "Sequência",

   "Sequência",

   "Sequência"

  ],

  "Readiness": [

   "Prontidão",

   "Prontidão",

   "Prontidão",

   "Prontidão",

   "Prontidão",

   "Prontidão",

   "Prontidão",

   "Prontidão",

   "Prontidão",

   "Prontidão"

  ],

  "Workouts": [

   "Treinos",

   "Treinos",

   "Treinos",

   "Treinos",

   "Treinos",

   "Treinos",

   "Treinos",

   "Treinos",

   "Treinos",

   "Treinos"

  ],

  "Total Volume:": [

   "Volume total:",

   "Volume total:",

   "Volume total:",

   "Volume total:",

   "Volume total:",

   "Volume total:",

   "Volume total:",

   "Volume total:",

   "Volume total:",

   "Volume total:"

  ],

  "Sets Completed:": [

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:",

   "Séries feitas:"

  ],

  "Male": [

   "Masculino",

   "Masculino",

   "Masculino",

   "Masculino",

   "Masculino",

   "Masculino",

   "Masculino",

   "Masculino",

   "Masculino",

   "Masculino"

  ],

  "Female": [

   "Feminino",

   "Feminino",

   "Feminino",

   "Feminino",

   "Feminino",

   "Feminino",

   "Feminino",

   "Feminino",

   "Feminino",

   "Feminino"

  ],

  "Free": [

   "Grátis",

   "Grátis",

   "Grátis",

   "Grátis",

   "Grátis",

   "Grátis",

   "Grátis",

   "Grátis",

   "Grátis",

   "Grátis"

  ],

  "Pro": [

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro"

  ],

  "Badges": [

   "Emblemas",

   "Emblemas",

   "Emblemas",

   "Emblemas",

   "Emblemas",

   "Emblemas",

   "Emblemas",

   "Emblemas",

   "Emblemas",

   "Emblemas"

  ],

  "Backup": [

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup"

  ],

  "Get Started": [

   "Começar",

   "Começar",

   "Começar",

   "Começar",

   "Começar",

   "Começar",

   "Começar",

   "Começar",

   "Começar",

   "Começar"

  ],

  "I agree": [

   "Concordo",

   "Concordo",

   "Concordo",

   "Concordo",

   "Concordo",

   "Concordo",

   "Concordo",

   "Concordo",

   "Concordo",

   "Concordo"

  ],

  "Upgrade to Pro": [

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro",

   "Upgrade para Pro"

  ]

 },

 "it": {

  "Home": [

   "Home",

   "Home",

   "Home",

   "Home",

   "Home",

   "Home",

   "Home",

   "Home",

   "Home",

   "Home"

  ],

  "Workout": [

   "Allenamento",

   "Allenamento",

   "Allenamento",

   "Allenamento",

   "Allenamento",

   "Allenamento",

   "Allenamento",

   "Allenamento",

   "Allenamento",

   "Allenamento"

  ],

  "History": [

   "Storico",

   "Storico",

   "Storico",

   "Storico",

   "Storico",

   "Storico",

   "Storico",

   "Storico",

   "Storico",

   "Storico"

  ],

  "Library": [

   "Libreria",

   "Libreria",

   "Libreria",

   "Libreria",

   "Libreria",

   "Libreria",

   "Libreria",

   "Libreria",

   "Libreria",

   "Libreria"

  ],

  "Progress": [

   "Progressi",

   "Progressi",

   "Progressi",

   "Progressi",

   "Progressi",

   "Progressi",

   "Progressi",

   "Progressi",

   "Progressi",

   "Progressi"

  ],

  "Recovery": [

   "Recupero",

   "Recupero",

   "Recupero",

   "Recupero",

   "Recupero",

   "Recupero",

   "Recupero",

   "Recupero",

   "Recupero",

   "Recupero"

  ],

  "Settings": [

   "Impostazioni",

   "Impostazioni",

   "Impostazioni",

   "Impostazioni",

   "Impostazioni",

   "Impostazioni",

   "Impostazioni",

   "Impostazioni",

   "Impostazioni",

   "Impostazioni"

  ],

  "Start Workout": [

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento",

   "Inizia allenamento"

  ],

  "Save": [

   "Salva",

   "Salva",

   "Salva",

   "Salva",

   "Salva",

   "Salva",

   "Salva",

   "Salva",

   "Salva",

   "Salva"

  ],

  "Cancel": [

   "Annulla",

   "Annulla",

   "Annulla",

   "Annulla",

   "Annulla",

   "Annulla",

   "Annulla",

   "Annulla",

   "Annulla",

   "Annulla"

  ],

  "Close": [

   "Chiudi",

   "Chiudi",

   "Chiudi",

   "Chiudi",

   "Chiudi",

   "Chiudi",

   "Chiudi",

   "Chiudi",

   "Chiudi",

   "Chiudi"

  ],

  "Continue": [

   "Continua",

   "Continua",

   "Continua",

   "Continua",

   "Continua",

   "Continua",

   "Continua",

   "Continua",

   "Continua",

   "Continua"

  ],

  "Back": [

   "Indietro",

   "Indietro",

   "Indietro",

   "Indietro",

   "Indietro",

   "Indietro",

   "Indietro",

   "Indietro",

   "Indietro",

   "Indietro"

  ],

  "Next": [

   "Avanti",

   "Avanti",

   "Avanti",

   "Avanti",

   "Avanti",

   "Avanti",

   "Avanti",

   "Avanti",

   "Avanti",

   "Avanti"

  ],

  "Skip": [

   "Salta",

   "Salta",

   "Salta",

   "Salta",

   "Salta",

   "Salta",

   "Salta",

   "Salta",

   "Salta",

   "Salta"

  ],

  "Finish": [

   "Fine",

   "Fine",

   "Fine",

   "Fine",

   "Fine",

   "Fine",

   "Fine",

   "Fine",

   "Fine",

   "Fine"

  ],

  "Streak": [

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie",

   "Serie"

  ],

  "Readiness": [

   "Prontezza",

   "Prontezza",

   "Prontezza",

   "Prontezza",

   "Prontezza",

   "Prontezza",

   "Prontezza",

   "Prontezza",

   "Prontezza",

   "Prontezza"

  ],

  "Workouts": [

   "Allenamenti",

   "Allenamenti",

   "Allenamenti",

   "Allenamenti",

   "Allenamenti",

   "Allenamenti",

   "Allenamenti",

   "Allenamenti",

   "Allenamenti",

   "Allenamenti"

  ],

  "Total Volume:": [

   "Volume totale:",

   "Volume totale:",

   "Volume totale:",

   "Volume totale:",

   "Volume totale:",

   "Volume totale:",

   "Volume totale:",

   "Volume totale:",

   "Volume totale:",

   "Volume totale:"

  ],

  "Sets Completed:": [

   "Serie completate:",

   "Serie completate:",

   "Serie completate:",

   "Serie completate:",

   "Serie completate:",

   "Serie completate:",

   "Serie completate:",

   "Serie completate:",

   "Serie completate:",

   "Serie completate:"

  ],

  "Male": [

   "Uomo",

   "Uomo",

   "Uomo",

   "Uomo",

   "Uomo",

   "Uomo",

   "Uomo",

   "Uomo",

   "Uomo",

   "Uomo"

  ],

  "Female": [

   "Donna",

   "Donna",

   "Donna",

   "Donna",

   "Donna",

   "Donna",

   "Donna",

   "Donna",

   "Donna",

   "Donna"

  ],

  "Free": [

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis",

   "Gratis"

  ],

  "Pro": [

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro",

   "Pro"

  ],

  "Badges": [

   "Distintivi",

   "Distintivi",

   "Distintivi",

   "Distintivi",

   "Distintivi",

   "Distintivi",

   "Distintivi",

   "Distintivi",

   "Distintivi",

   "Distintivi"

  ],

  "Backup": [

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup",

   "Backup"

  ],

  "Get Started": [

   "Inizia",

   "Inizia",

   "Inizia",

   "Inizia",

   "Inizia",

   "Inizia",

   "Inizia",

   "Inizia",

   "Inizia",

   "Inizia"

  ],

  "I agree": [

   "Accetto",

   "Accetto",

   "Accetto",

   "Accetto",

   "Accetto",

   "Accetto",

   "Accetto",

   "Accetto",

   "Accetto",

   "Accetto"

  ],

  "Upgrade to Pro": [

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro",

   "Passa a Pro"

  ]

 },

 "ru": {

  "Home": [

   "Главная",

   "Главная",

   "Главная",

   "Главная",

   "Главная",

   "Главная",

   "Главная",

   "Главная",

   "Главная",

   "Главная"

  ],

  "Workout": [

   "Тренировка",

   "Тренировка",

   "Тренировка",

   "Тренировка",

   "Тренировка",

   "Тренировка",

   "Тренировка",

   "Тренировка",

   "Тренировка",

   "Тренировка"

  ],

  "History": [

   "История",

   "История",

   "История",

   "История",

   "История",

   "История",

   "История",

   "История",

   "История",

   "История"

  ],

  "Library": [

   "Библиотека",

   "Библиотека",

   "Библиотека",

   "Библиотека",

   "Библиотека",

   "Библиотека",

   "Библиотека",

   "Библиотека",

   "Библиотека",

   "Библиотека"

  ],

  "Progress": [

   "Прогресс",

   "Прогресс",

   "Прогресс",

   "Прогресс",

   "Прогресс",

   "Прогресс",

   "Прогресс",

   "Прогресс",

   "Прогресс",

   "Прогресс"

  ],

  "Recovery": [

   "Восстановление",

   "Восстановление",

   "Восстановление",

   "Восстановление",

   "Восстановление",

   "Восстановление",

   "Восстановление",

   "Восстановление",

   "Восстановление",

   "Восстановление"

  ],

  "Settings": [

   "Настройки",

   "Настройки",

   "Настройки",

   "Настройки",

   "Настройки",

   "Настройки",

   "Настройки",

   "Настройки",

   "Настройки",

   "Настройки"

  ],

  "Start Workout": [

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку",

   "Начать тренировку"

  ],

  "Save": [

   "Сохранить",

   "Сохранить",

   "Сохранить",

   "Сохранить",

   "Сохранить",

   "Сохранить",

   "Сохранить",

   "Сохранить",

   "Сохранить",

   "Сохранить"

  ],

  "Cancel": [

   "Отмена",

   "Отмена",

   "Отмена",

   "Отмена",

   "Отмена",

   "Отмена",

   "Отмена",

   "Отмена",

   "Отмена",

   "Отмена"

  ],

  "Close": [

   "Закрыть",

   "Закрыть",

   "Закрыть",

   "Закрыть",

   "Закрыть",

   "Закрыть",

   "Закрыть",

   "Закрыть",

   "Закрыть",

   "Закрыть"

  ],

  "Continue": [

   "Продолжить",

   "Продолжить",

   "Продолжить",

   "Продолжить",

   "Продолжить",

   "Продолжить",

   "Продолжить",

   "Продолжить",

   "Продолжить",

   "Продолжить"

  ],

  "Back": [

   "Назад",

   "Назад",

   "Назад",

   "Назад",

   "Назад",

   "Назад",

   "Назад",

   "Назад",

   "Назад",

   "Назад"

  ],

  "Next": [

   "Далее",

   "Далее",

   "Далее",

   "Далее",

   "Далее",

   "Далее",

   "Далее",

   "Далее",

   "Далее",

   "Далее"

  ],

  "Skip": [

   "Пропустить",

   "Пропустить",

   "Пропустить",

   "Пропустить",

   "Пропустить",

   "Пропустить",

   "Пропустить",

   "Пропустить",

   "Пропустить",

   "Пропустить"

  ],

  "Finish": [

   "Готово",

   "Готово",

   "Готово",

   "Готово",

   "Готово",

   "Готово",

   "Готово",

   "Готово",

   "Готово",

   "Готово"

  ],

  "Streak": [

   "Серия",

   "Серия",

   "Серия",

   "Серия",

   "Серия",

   "Серия",

   "Серия",

   "Серия",

   "Серия",

   "Серия"

  ],

  "Readiness": [

   "Готовность",

   "Готовность",

   "Готовность",

   "Готовность",

   "Готовность",

   "Готовность",

   "Готовность",

   "Готовность",

   "Готовность",

   "Готовность"

  ],

  "Workouts": [

   "Тренировки",

   "Тренировки",

   "Тренировки",

   "Тренировки",

   "Тренировки",

   "Тренировки",

   "Тренировки",

   "Тренировки",

   "Тренировки",

   "Тренировки"

  ],

  "Total Volume:": [

   "Общий объём:",

   "Общий объём:",

   "Общий объём:",

   "Общий объём:",

   "Общий объём:",

   "Общий объём:",

   "Общий объём:",

   "Общий объём:",

   "Общий объём:",

   "Общий объём:"

  ],

  "Sets Completed:": [

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:",

   "Подходов сделано:"

  ],

  "Male": [

   "Мужчина",

   "Мужчина",

   "Мужчина",

   "Мужчина",

   "Мужчина",

   "Мужчина",

   "Мужчина",

   "Мужчина",

   "Мужчина",

   "Мужчина"

  ],

  "Female": [

   "Женщина",

   "Женщина",

   "Женщина",

   "Женщина",

   "Женщина",

   "Женщина",

   "Женщина",

   "Женщина",

   "Женщина",

   "Женщина"

  ],

  "Free": [

   "Бесплатно",

   "Бесплатно",

   "Бесплатно",

   "Бесплатно",

   "Бесплатно",

   "Бесплатно",

   "Бесплатно",

   "Бесплатно",

   "Бесплатно",

   "Бесплатно"

  ],

  "Pro": [

   "Про",

   "Про",

   "Про",

   "Про",

   "Про",

   "Про",

   "Про",

   "Про",

   "Про",

   "Про"

  ],

  "Badges": [

   "Награды",

   "Награды",

   "Награды",

   "Награды",

   "Награды",

   "Награды",

   "Награды",

   "Награды",

   "Награды",

   "Награды"

  ],

  "Backup": [

   "Резервная копия",

   "Резервная копия",

   "Резервная копия",

   "Резервная копия",

   "Резервная копия",

   "Резервная копия",

   "Резервная копия",

   "Резервная копия",

   "Резервная копия",

   "Резервная копия"

  ],

  "Get Started": [

   "Начать",

   "Начать",

   "Начать",

   "Начать",

   "Начать",

   "Начать",

   "Начать",

   "Начать",

   "Начать",

   "Начать"

  ],

  "I agree": [

   "Согласен",

   "Согласен",

   "Согласен",

   "Согласен",

   "Согласен",

   "Согласен",

   "Согласен",

   "Согласен",

   "Согласен",

   "Согласен"

  ],

  "Upgrade to Pro": [

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про",

   "Перейти на Про"

  ]

 },

 "ko": {

  "Home": [

   "홈",

   "홈",

   "홈",

   "홈",

   "홈",

   "홈",

   "홈",

   "홈",

   "홈",

   "홈"

  ],

  "Workout": [

   "운동",

   "운동",

   "운동",

   "운동",

   "운동",

   "운동",

   "운동",

   "운동",

   "운동",

   "운동"

  ],

  "History": [

   "기록",

   "기록",

   "기록",

   "기록",

   "기록",

   "기록",

   "기록",

   "기록",

   "기록",

   "기록"

  ],

  "Library": [

   "라이브러리",

   "라이브러리",

   "라이브러리",

   "라이브러리",

   "라이브러리",

   "라이브러리",

   "라이브러리",

   "라이브러리",

   "라이브러리",

   "라이브러리"

  ],

  "Progress": [

   "진행",

   "진행",

   "진행",

   "진행",

   "진행",

   "진행",

   "진행",

   "진행",

   "진행",

   "진행"

  ],

  "Recovery": [

   "회복",

   "회복",

   "회복",

   "회복",

   "회복",

   "회복",

   "회복",

   "회복",

   "회복",

   "회복"

  ],

  "Settings": [

   "설정",

   "설정",

   "설정",

   "설정",

   "설정",

   "설정",

   "설정",

   "설정",

   "설정",

   "설정"

  ],

  "Start Workout": [

   "운동 시작",

   "운동 시작",

   "운동 시작",

   "운동 시작",

   "운동 시작",

   "운동 시작",

   "운동 시작",

   "운동 시작",

   "운동 시작",

   "운동 시작"

  ],

  "Save": [

   "저장",

   "저장",

   "저장",

   "저장",

   "저장",

   "저장",

   "저장",

   "저장",

   "저장",

   "저장"

  ],

  "Cancel": [

   "취소",

   "취소",

   "취소",

   "취소",

   "취소",

   "취소",

   "취소",

   "취소",

   "취소",

   "취소"

  ],

  "Close": [

   "닫기",

   "닫기",

   "닫기",

   "닫기",

   "닫기",

   "닫기",

   "닫기",

   "닫기",

   "닫기",

   "닫기"

  ],

  "Continue": [

   "계속",

   "계속",

   "계속",

   "계속",

   "계속",

   "계속",

   "계속",

   "계속",

   "계속",

   "계속"

  ],

  "Back": [

   "뒤로",

   "뒤로",

   "뒤로",

   "뒤로",

   "뒤로",

   "뒤로",

   "뒤로",

   "뒤로",

   "뒤로",

   "뒤로"

  ],

  "Next": [

   "다음",

   "다음",

   "다음",

   "다음",

   "다음",

   "다음",

   "다음",

   "다음",

   "다음",

   "다음"

  ],

  "Skip": [

   "건너뛰기",

   "건너뛰기",

   "건너뛰기",

   "건너뛰기",

   "건너뛰기",

   "건너뛰기",

   "건너뛰기",

   "건너뛰기",

   "건너뛰기",

   "건너뛰기"

  ],

  "Finish": [

   "완료",

   "완료",

   "완료",

   "완료",

   "완료",

   "완료",

   "완료",

   "완료",

   "완료",

   "완료"

  ],

  "Streak": [

   "연속",

   "연속",

   "연속",

   "연속",

   "연속",

   "연속",

   "연속",

   "연속",

   "연속",

   "연속"

  ],

  "Readiness": [

   "컨디션",

   "컨디션",

   "컨디션",

   "컨디션",

   "컨디션",

   "컨디션",

   "컨디션",

   "컨디션",

   "컨디션",

   "컨디션"

  ],

  "Workouts": [

   "운동 수",

   "운동 수",

   "운동 수",

   "운동 수",

   "운동 수",

   "운동 수",

   "운동 수",

   "운동 수",

   "운동 수",

   "운동 수"

  ],

  "Total Volume:": [

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:",

   "총 볼륨:"

  ],

  "Sets Completed:": [

   "완료 세트:",

   "완료 세트:",

   "완료 세트:",

   "완료 세트:",

   "완료 세트:",

   "완료 세트:",

   "완료 세트:",

   "완료 세트:",

   "완료 세트:",

   "완료 세트:"

  ],

  "Male": [

   "남성",

   "남성",

   "남성",

   "남성",

   "남성",

   "남성",

   "남성",

   "남성",

   "남성",

   "남성"

  ],

  "Female": [

   "여성",

   "여성",

   "여성",

   "여성",

   "여성",

   "여성",

   "여성",

   "여성",

   "여성",

   "여성"

  ],

  "Free": [

   "무료",

   "무료",

   "무료",

   "무료",

   "무료",

   "무료",

   "무료",

   "무료",

   "무료",

   "무료"

  ],

  "Pro": [

   "프로",

   "프로",

   "프로",

   "프로",

   "프로",

   "프로",

   "프로",

   "프로",

   "프로",

   "프로"

  ],

  "Badges": [

   "배지",

   "배지",

   "배지",

   "배지",

   "배지",

   "배지",

   "배지",

   "배지",

   "배지",

   "배지"

  ],

  "Backup": [

   "백업",

   "백업",

   "백업",

   "백업",

   "백업",

   "백업",

   "백업",

   "백업",

   "백업",

   "백업"

  ],

  "Get Started": [

   "시작하기",

   "시작하기",

   "시작하기",

   "시작하기",

   "시작하기",

   "시작하기",

   "시작하기",

   "시작하기",

   "시작하기",

   "시작하기"

  ],

  "I agree": [

   "동의합니다",

   "동의합니다",

   "동의합니다",

   "동의합니다",

   "동의합니다",

   "동의합니다",

   "동의합니다",

   "동의합니다",

   "동의합니다",

   "동의합니다"

  ],

  "Upgrade to Pro": [

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드",

   "Pro로 업그레이드"

  ]

 },

 "ja": {

  "Home": [

   "ホーム",

   "ホーム",

   "ホーム",

   "ホーム",

   "ホーム",

   "ホーム",

   "ホーム",

   "ホーム",

   "ホーム",

   "ホーム"

  ],

  "Workout": [

   "トレーニング",

   "トレーニング",

   "トレーニング",

   "トレーニング",

   "トレーニング",

   "トレーニング",

   "トレーニング",

   "トレーニング",

   "トレーニング",

   "トレーニング"

  ],

  "History": [

   "履歴",

   "履歴",

   "履歴",

   "履歴",

   "履歴",

   "履歴",

   "履歴",

   "履歴",

   "履歴",

   "履歴"

  ],

  "Library": [

   "ライブラリ",

   "ライブラリ",

   "ライブラリ",

   "ライブラリ",

   "ライブラリ",

   "ライブラリ",

   "ライブラリ",

   "ライブラリ",

   "ライブラリ",

   "ライブラリ"

  ],

  "Progress": [

   "進捗",

   "進捗",

   "進捗",

   "進捗",

   "進捗",

   "進捗",

   "進捗",

   "進捗",

   "進捗",

   "進捗"

  ],

  "Recovery": [

   "回復",

   "回復",

   "回復",

   "回復",

   "回復",

   "回復",

   "回復",

   "回復",

   "回復",

   "回復"

  ],

  "Settings": [

   "設定",

   "設定",

   "設定",

   "設定",

   "設定",

   "設定",

   "設定",

   "設定",

   "設定",

   "設定"

  ],

  "Start Workout": [

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始",

   "トレーニング開始"

  ],

  "Save": [

   "保存",

   "保存",

   "保存",

   "保存",

   "保存",

   "保存",

   "保存",

   "保存",

   "保存",

   "保存"

  ],

  "Cancel": [

   "キャンセル",

   "キャンセル",

   "キャンセル",

   "キャンセル",

   "キャンセル",

   "キャンセル",

   "キャンセル",

   "キャンセル",

   "キャンセル",

   "キャンセル"

  ],

  "Close": [

   "閉じる",

   "閉じる",

   "閉じる",

   "閉じる",

   "閉じる",

   "閉じる",

   "閉じる",

   "閉じる",

   "閉じる",

   "閉じる"

  ],

  "Continue": [

   "続ける",

   "続ける",

   "続ける",

   "続ける",

   "続ける",

   "続ける",

   "続ける",

   "続ける",

   "続ける",

   "続ける"

  ],

  "Back": [

   "戻る",

   "戻る",

   "戻る",

   "戻る",

   "戻る",

   "戻る",

   "戻る",

   "戻る",

   "戻る",

   "戻る"

  ],

  "Next": [

   "次へ",

   "次へ",

   "次へ",

   "次へ",

   "次へ",

   "次へ",

   "次へ",

   "次へ",

   "次へ",

   "次へ"

  ],

  "Skip": [

   "スキップ",

   "スキップ",

   "スキップ",

   "スキップ",

   "スキップ",

   "スキップ",

   "スキップ",

   "スキップ",

   "スキップ",

   "スキップ"

  ],

  "Finish": [

   "完了",

   "完了",

   "完了",

   "完了",

   "完了",

   "完了",

   "完了",

   "完了",

   "完了",

   "完了"

  ],

  "Streak": [

   "連続記録",

   "連続記録",

   "連続記録",

   "連続記録",

   "連続記録",

   "連続記録",

   "連続記録",

   "連続記録",

   "連続記録",

   "連続記録"

  ],

  "Readiness": [

   "コンディション",

   "コンディション",

   "コンディション",

   "コンディション",

   "コンディション",

   "コンディション",

   "コンディション",

   "コンディション",

   "コンディション",

   "コンディション"

  ],

  "Workouts": [

   "トレーニング数",

   "トレーニング数",

   "トレーニング数",

   "トレーニング数",

   "トレーニング数",

   "トレーニング数",

   "トレーニング数",

   "トレーニング数",

   "トレーニング数",

   "トレーニング数"

  ],

  "Total Volume:": [

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:",

   "合計ボリューム:"

  ],

  "Sets Completed:": [

   "完了セット:",

   "完了セット:",

   "完了セット:",

   "完了セット:",

   "完了セット:",

   "完了セット:",

   "完了セット:",

   "完了セット:",

   "完了セット:",

   "完了セット:"

  ],

  "Male": [

   "男性",

   "男性",

   "男性",

   "男性",

   "男性",

   "男性",

   "男性",

   "男性",

   "男性",

   "男性"

  ],

  "Female": [

   "女性",

   "女性",

   "女性",

   "女性",

   "女性",

   "女性",

   "女性",

   "女性",

   "女性",

   "女性"

  ],

  "Free": [

   "無料",

   "無料",

   "無料",

   "無料",

   "無料",

   "無料",

   "無料",

   "無料",

   "無料",

   "無料"

  ],

  "Pro": [

   "プロ",

   "プロ",

   "プロ",

   "プロ",

   "プロ",

   "プロ",

   "プロ",

   "プロ",

   "プロ",

   "プロ"

  ],

  "Badges": [

   "バッジ",

   "バッジ",

   "バッジ",

   "バッジ",

   "バッジ",

   "バッジ",

   "バッジ",

   "バッジ",

   "バッジ",

   "バッジ"

  ],

  "Backup": [

   "バックアップ",

   "バックアップ",

   "バックアップ",

   "バックアップ",

   "バックアップ",

   "バックアップ",

   "バックアップ",

   "バックアップ",

   "バックアップ",

   "バックアップ"

  ],

  "Get Started": [

   "はじめる",

   "はじめる",

   "はじめる",

   "はじめる",

   "はじめる",

   "はじめる",

   "はじめる",

   "はじめる",

   "はじめる",

   "はじめる"

  ],

  "I agree": [

   "同意します",

   "同意します",

   "同意します",

   "同意します",

   "同意します",

   "同意します",

   "同意します",

   "同意します",

   "同意します",

   "同意します"

  ],

  "Upgrade to Pro": [

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード",

   "Proにアップグレード"

  ]

 }

};



/* ============================================================================

 * P4 CORE — namespace, security kit, theme engine, toasts, storage helpers

 * ============================================================================ */

(function () {

  'use strict';

  if (window.P4) return; // idempotent



  /* ---- integrity canary (cheap self-check; mirrors reference-app MAGIC) ---- */

  var P4_MAGIC = 0xDEADBEEF ^ 0x55AA55AA; // 0x7F3A4C1B

  var P4 = {

    MAGIC: P4_MAGIC,

    version: '4.0.0',

    keys: {

      theme: 'p4_theme', gym: 'p4_gym', vocab: 'p4_vocab', onboard: 'p4_onboarded',

      profile: 'p4_profile', consent: 'p4_consent_log', backup: 'p4_last_backup',

      paywall: 'p4_paywall_state', license: 'p4_license', trial: 'p4_trial_start',

      subExpires: 'p4_sub_expires_at', subCache: 'p4_sub_cache', licenseTries: 'p4_license_tries',

      licenseLock: 'p4_license_lock', studioRecent: 'p4_studio_recent', feedback: 'p4_feedback',

      badges: 'p4_badges', badgesSeen: 'p4_badges_seen', quoteDay: 'p4_quote_day',

      lang: 'p4_lang', phrases: 'p4_phrases_custom', snapshots: 'p4_snapshots',

      settings: 'p4_power_settings', taps: 'p4_tap_metrics', cfp: 'p4_component_flip'

    }

  };

  window.P4 = P4;



  /* ============================ SECURITY KIT ============================== */

  var SEC = P4.SEC = {};



  SEC.escapeHtml = function (str) {

    if (str === null || str === undefined) return '';

    return String(str)

      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')

      .replace(/"/g, '&quot;').replace(/'/g, '&#39;');

  };

  SEC.stripHtml = function (str) { return String(str == null ? '' : str).replace(/<[^>]*>/g, ''); };



  SEC.clampStr = function (str, max) {

    str = SEC.stripHtml(str).trim();

    return str.length > max ? str.slice(0, max) : str;

  };



  /* Heal slightly-broken JSON from AI tools: trailing commas, stray markdown */

  SEC.healJson = function (raw) {

    var t = String(raw == null ? '' : raw).trim();

    if (!t) return { ok: false, error: 'Empty input.' };

    var fence = t.match(/```(?:json)?\s*([\s\S]*?)```/i);

    if (fence && fence[1]) t = fence[1].trim();

    try { return { ok: true, data: JSON.parse(t) }; }

    catch (e1) {

      var fixed = t

        .replace(/,\s*([\]}])/g, '$1')

        .replace(/[\u201C\u201D]/g, '"').replace(/[\u2018\u2019]/g, "'")

        .replace(/\u2026/g, '...');

      try { return { ok: true, data: JSON.parse(fixed), healed: true }; }

      catch (e2) { return { ok: false, error: 'Not valid JSON' + (e2 && e2.message ? ' — ' + e2.message : '.') }; }

    }

  };



  /* Validate an array-of-exercises payload against the app's registry */

  SEC.validateExercisePayload = function (data, registry) {

    var groups = registry.groups, muscles = registry.muscles;

    var out = { valid: [], invalid: [], healed: false };

    var arr = Array.isArray(data) ? data : (data && Array.isArray(data.exercises) ? data.exercises : null);

    if (!arr) { out.invalid.push({ name: '(root)', reason: 'Expected a JSON array or {exercises:[...]}' }); return out; }

    if (data && data.exercises) out.healed = true;

    for (var i = 0; i < arr.length; i++) {

      var ex = arr[i];

      var name = ex && (ex.name || ex.Name || ex.title);

      if (!name || typeof name !== 'string' || !name.trim()) {

        out.invalid.push({ name: '(item ' + (i + 1) + ')', reason: 'Missing exercise name' }); continue;

      }

      name = String(name).trim().slice(0, 90);

      var group = String(ex.group || ex.category || '').toLowerCase().replace(/\s+/g, '_');

      if (!groups[group]) { out.invalid.push({ name: name, reason: 'Unknown group "' + group + '"' }); continue; }

      var mlist = ex.muscles || ex.primaryMuscles || [];

      if (!Array.isArray(mlist)) mlist = [mlist];

      var mok = [], mbad = [];

      mlist.forEach(function (m) {

        var k = String(m || '').toLowerCase().replace(/\s+/g, '_');

        if (muscles[k]) mok.push(k); else if (k) mbad.push(String(m));

      });

      if (!mok.length) { out.invalid.push({ name: name, reason: 'No valid muscle targets' }); continue; }

      out.valid.push({

        name: name,

        group: group,

        muscles: mok,

        musclesBad: mbad,

        equipment: SEC.clampStr(ex.equipment || 'bodyweight', 60),

        difficulty: ['beginner', 'intermediate', 'advanced'].indexOf(String(ex.difficulty || '').toLowerCase()) >= 0

          ? String(ex.difficulty).toLowerCase() : 'intermediate',

        type: String(ex.type || 'reps').toLowerCase() === 'time' ? 'time' : 'reps',

        steps: Array.isArray(ex.steps) ? ex.steps.slice(0, 8).map(function (s) { return SEC.clampStr(s, 220); }) : [],

        cues: Array.isArray(ex.cues) ? ex.cues.slice(0, 6).map(function (s) { return SEC.clampStr(s, 160); }) : [],

        mistakes: Array.isArray(ex.mistakes) ? ex.mistakes.slice(0, 5).map(function (s) { return SEC.clampStr(s, 160); }) : [],

        breathing: SEC.clampStr(ex.breathing || '', 160),

        tempo: SEC.clampStr(ex.tempo || '', 40),

        imageUrl: /^https:\/\//i.test(String(ex.imageUrl || '')) ? String(ex.imageUrl) : '',

        source: 'studio'

      });

    }

    return out;

  };



  /* Simple token-bucket rate limiter (license attempts, imports) */

  SEC.rateLimit = function (key, maxHits, windowMs) {

    var now = Date.now();

    var store = SEC._rl = SEC._rl || {};

    var b = store[key] = store[key] || { hits: [], blockedUntil: 0 };

    if (now < b.blockedUntil) return { allowed: false, retryIn: Math.ceil((b.blockedUntil - now) / 1000) };

    b.hits = b.hits.filter(function (t) { return now - t < windowMs; });

    if (b.hits.length >= maxHits) {

      b.blockedUntil = now + windowMs;

      return { allowed: false, retryIn: Math.ceil(windowMs / 1000) };

    }

    b.hits.push(now);

    return { allowed: true };

  };



  /* Integrity canary — verified at boot; harmless to users */

  SEC.integrityOk = function () { return P4_MAGIC === (0xDEADBEEF ^ 0x55AA55AA); };



  /* Domain gate: production domains only; local/dev hosts pass through. */

  SEC.domainAllowed = function () {

    try {

      var h = String(window.location.hostname || '').toLowerCase();

      if (!h) return true; // file:// and about: contexts

      return h === 'try4ever.com' || h.endsWith('.try4ever.com') ||

             h === 'localhost' || h === '127.0.0.1' || h === '0.0.0.0' || h.endsWith('.local');

    } catch (e) { return true; }

  };



  /* ============================ STORAGE HELPERS =========================== */

  var store = P4.store = {};

  store.get = function (key, fallback) {

    try {

      var raw = localStorage.getItem(key);

      if (raw === null || raw === undefined) return fallback;

      return JSON.parse(raw);

    } catch (e) { return fallback; }

  };

  store.set = function (key, val) {

    try { localStorage.setItem(key, JSON.stringify(val)); return true; }

    catch (e) {

      if (P4.toast) P4.toast('Storage is full — export a backup and clean old data.', 'bad');

      return false;

    }

  };

  store.del = function (key) { try { localStorage.removeItem(key); } catch (e) {} };



  /* Rolling snapshots: last 3 self-recovery points, written after workouts */

  store.snapshot = function () {

    try {

      var data = localStorage.getItem('workoutData') || '';

      if (!data) return;

      var snaps = store.get(P4.keys.snapshots, []);

      snaps.unshift({ at: Date.now(), bytes: data.length, data: data });

      snaps = snaps.slice(0, 3);

      store.set(P4.keys.snapshots, snaps);

    } catch (e) {}

  };



  /* ============================ TOASTS ==================================== */

  var toastHost = null;

  P4.toast = function (msg, kind, ms) {

    if (!toastHost) {

      toastHost = document.createElement('div');

      toastHost.id = 'p4Toasts';

      document.body.appendChild(toastHost);

    }

    var icons = { ok: 'fa-circle-check', warn: 'fa-triangle-exclamation', bad: 'fa-circle-xmark', info: 'fa-circle-info' };

    var el = document.createElement('div');

    el.className = 'p4-toast';

    el.innerHTML = '<i class="fas ' + (icons[kind] || icons.info) + ' ' + (kind || 'info') + '"></i><span>' +

      SEC.escapeHtml(msg) + '</span>';

    toastHost.appendChild(el);

    setTimeout(function () {

      el.classList.add('out');

      setTimeout(function () { if (el.parentNode) el.parentNode.removeChild(el); }, 340);

    }, ms || 3400);

  };



  /* ============================ HAPTICS =================================== */

  P4.buzz = function (pattern) {

    try { if (navigator.vibrate) navigator.vibrate(pattern || 12); } catch (e) {}

  };



  /* ============================ THEME ENGINE ============================== */

  var ACCENTS = [

    { id: 'blue',   name: 'Kinetic Blue',  hex: '#4E9BFF' },

    { id: 'green',  name: 'Court Green',   hex: '#34D399' },

    { id: 'purple', name: 'Deep Purple',   hex: '#A78BFA' },

    { id: 'orange', name: 'Sunset Orange', hex: '#FB923C' },

    { id: 'red',    name: 'Crimson Red',   hex: '#F87171' },

    { id: 'pink',   name: 'Rose Pink',     hex: '#F472B6' },

    { id: 'yellow', name: 'Sunny Yellow',  hex: '#FACC15' },

    { id: 'teal',   name: 'Lagoon Teal',   hex: '#2DD4BF' },

    { id: 'cyan',   name: 'Ice Cyan',      hex: '#22D3EE' },

    { id: 'indigo', name: 'Midnight Indigo', hex: '#818CF8' },

    { id: 'lime',   name: 'Volt Lime',     hex: '#A3E635' },

    { id: 'rose',   name: 'Coral Rose',    hex: '#FB7185' },

    { id: 'sky',    name: 'Sky Blue',      hex: '#38BDF8' },

    { id: 'mint',   name: 'Fresh Mint',    hex: '#6EE7B7' },

    { id: 'violet', name: 'Soft Violet',   hex: '#C4B5FD' },

    { id: 'gold',   name: 'Champion Gold', hex: '#FBBF24' }

  ];

  P4.ACCENTS = ACCENTS;



  var Theme = P4.Theme = {

    get: function () { return store.get(P4.keys.theme, { mode: 'dark', accent: 'blue' }); },

    set: function (patch, opts) {

      var cur = Theme.get();

      var next = { mode: patch.mode || cur.mode, accent: patch.accent || cur.accent };

      store.set(P4.keys.theme, next);

      Theme.apply();

      document.documentElement.setAttribute('data-mode', next.mode);

      document.documentElement.setAttribute('data-accent', next.accent);

      if (next.mode === 'dark') document.documentElement.setAttribute('data-theme', 'dark');

      else {

        var legacy = ['blue', 'green', 'purple', 'orange'].indexOf(next.accent) >= 0 ? next.accent : null;

        if (legacy) document.documentElement.setAttribute('data-theme', legacy);

        else document.documentElement.removeAttribute('data-theme');

      }

      if (P4.themeUI) P4.themeUI.sync();

      if (!(opts && opts.silent)) P4.toast('Theme updated — ' + next.mode + ' · ' + (Theme.accentName(next.accent)), 'ok', 1800);

      if (typeof charts !== 'undefined' && charts && charts.dashboard && typeof updateDashboardChart === 'function') {

        try { updateDashboardChart(); } catch (e) {}

      }

    },

    apply: function () {},

    accentName: function (id) {

      for (var i = 0; i < ACCENTS.length; i++) if (ACCENTS[i].id === id) return ACCENTS[i].name;

      return 'Kinetic Blue';

    },

    accentHex: function (id) {

      for (var i = 0; i < ACCENTS.length; i++) if (ACCENTS[i].id === id) return ACCENTS[i].hex;

      return '#4E9BFF';

    },

    accents: ACCENTS,

    gym: function (on) {

      document.documentElement.setAttribute('data-gym', on ? 'on' : 'off');

      store.set(P4.keys.gym, on ? 'on' : 'off');

      if (P4.powerSettings) P4.powerSettings.gymMode = on;

    },

    gymOn: function () { return store.get(P4.keys.gym, 'off') === 'on'; }

  };



  /* ============================ MISC UTILS ================================ */

  P4.uid = function () {

    return 'p4_' + Date.now().toString(36) + '_' + Math.random().toString(36).slice(2, 8);

  };

  P4.todayKey = function () {

    var d = new Date();

    return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');

  };

  P4.fmtDate = function (ts) {

    try { return new Date(ts).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' }); }

    catch (e) { return String(ts); }

  };

  P4.debounce = function (fn, ms) {

    var t = null;

    return function () {

      var args = arguments, self = this;

      clearTimeout(t);

      t = setTimeout(function () { fn.apply(self, args); }, ms);

    };

  };

  P4.pick = function (arr) { return arr[Math.floor(Math.random() * arr.length)]; };

  P4.download = function (filename, text, mime) {

    var blob = new Blob([text], { type: mime || 'application/json' });

    var url = URL.createObjectURL(blob);

    var a = document.createElement('a');

    a.href = url; a.download = filename;

    document.body.appendChild(a); a.click();

    setTimeout(function () { URL.revokeObjectURL(url); a.remove(); }, 400);

  };

  P4.copyText = function (text) {

    return new Promise(function (resolve) {

      if (navigator.clipboard && navigator.clipboard.writeText) {

        navigator.clipboard.writeText(text).then(function () { resolve(true); }, function () { resolve(P4._copyFallback(text)); });

      } else resolve(P4._copyFallback(text));

    });

  };

  P4._copyFallback = function (text) {

    try {

      var ta = document.createElement('textarea');

      ta.value = text; ta.style.position = 'fixed'; ta.style.opacity = '0';

      document.body.appendChild(ta); ta.select();

      var ok = document.execCommand('copy');

      ta.remove();

      return ok;

    } catch (e) { return false; }

  };

})();



/* ============================================================================

 * P4 VOCAB ENGINE — 10 reading levels for every registered UI phrase.

 * Level 1 = super simple (kid discovering the app)

 * Level 5 = standard (authored default text — no replacement runs)

 * Level 10 = grandiloquent (Shakespeare-adjacent)

 * Strategy: exact-phrase tree-walk over visible text nodes, debounced by a

 * MutationObserver. Level 5 is a no-op for performance.

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4;

  var store = P4.store;



  var LEVELS = [

    { id: 1,  name: 'Super Simple',  desc: 'Short words. Big letters. For first readers.' },

    { id: 2,  name: 'Easy',          desc: 'Plain and friendly. No jargon at all.' },

    { id: 3,  name: 'Everyday',      desc: 'How friends talk. Clear and casual.' },

    { id: 4,  name: 'Clear',         desc: 'Simple sentences with a little detail.' },

    { id: 5,  name: 'Standard',      desc: 'The normal app voice. Balanced and clear.' },

    { id: 6,  name: 'Sharp',         desc: 'Crisp coaching language. Direct.' },

    { id: 7,  name: 'Advanced',      desc: 'Richer words for experienced readers.' },

    { id: 8,  name: 'Technical',     desc: 'Training terms used the way coaches use them.' },

    { id: 9,  name: 'Pro Coaching',  desc: 'Full sports-science vocabulary.' },

    { id: 10, name: 'Grandiloquent', desc: 'Elevated, ornate English. Fanciest words.' }

  ];

  P4.VocabLevels = LEVELS;



  var Vocab = P4.Vocab = {

    level: 5,

    applying: false,

    get: function () { return store.get(P4.keys.vocab, 5); },

    set: function (lvl, silent) {

      lvl = Math.max(1, Math.min(10, parseInt(lvl, 10) || 5));

      Vocab.level = lvl;

      store.set(P4.keys.vocab, lvl);

      Vocab.applyAll();

      if (P4.vocabUI) P4.vocabUI.sync();

      if (!silent) {

        var L = LEVELS[lvl - 1];

        P4.toast('Words set to Level ' + lvl + ' — ' + L.name, 'ok');

      }

    },

    levelName: function () { return LEVELS[Vocab.level - 1].name; },



    /* dynamic-string lookup for JS-built UI */

    t: function (key, fallback) {

      var ladder = P4_VOCAB_LADDERS[key];

      if (!ladder || Vocab.level === 5) return fallback !== undefined ? fallback : key;

      return ladder[Vocab.level - 1] || fallback || key;

    },



    /* map a source phrase to the current level's variant (or null) */

    map: function (text) {

      if (Vocab.level === 5) return null;

      var ladder = P4_VOCAB_LADDERS[text];

      if (!ladder) return null;

      var out = ladder[Vocab.level - 1];

      return (out && out !== text) ? out : null;

    },



    SKIP_TAGS: { SCRIPT: 1, STYLE: 1, NOSCRIPT: 1, TEMPLATE: 1, CODE: 1, PRE: 1,

                 TEXTAREA: 1, INPUT: 1, SELECT: 1, OPTION: 1, CANVAS: 1, SVG: 1 },



    shouldSkip: function (node) {

      var el = node.parentElement;

      if (!el) return true;

      var cur = el;

      for (var i = 0; i < 6 && cur; i++) {

        if (Vocab.SKIP_TAGS[cur.tagName]) return true;

        if (cur.getAttribute) {

          if (cur.getAttribute('contenteditable') === 'true') return true;

          if (cur.hasAttribute('data-vocab-skip')) return true;

          if (cur.hasAttribute('translate')) return true; // translate=no zones

        }

        cur = cur.parentElement;

      }

      return false;

    },



    _done: new WeakSet(),



    walk: function (root) {

      if (!Vocab._masterRe) return 0;

      var walker;

      try { walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT, null, false); }

      catch (e) { return 0; }

      var node, edits = [];

      while ((node = walker.nextNode())) {

        if (Vocab._done.has(node)) continue;

        var raw = node.nodeValue;

        if (!raw || raw.length < 3) continue;

        if (Vocab.shouldSkip(node)) continue;

        var out = Vocab.transformText(raw);

        if (out !== raw) edits.push([node, out]);

        else Vocab._done.add(node);

      }

      for (var i = 0; i < edits.length; i++) {

        try {

          edits[i][0].nodeValue = edits[i][1];

          Vocab._done.add(edits[i][0]);

        } catch (e) {}

      }

      return edits.length;

    },



    /* Replace registered phrases inside an arbitrary text string.

       Single pass; word-boundary regex; preserves ALL-CAPS and leading caps. */

    transformText: function (raw) {

      if (Vocab.level === 5 || !Vocab._masterRe) return raw;

      Vocab._masterRe.lastIndex = 0;

      return raw.replace(Vocab._masterRe, function (m, pre, hit) {

        var key = Vocab._lowerMap[hit.toLowerCase()];

        if (!key) return m;

        var ladder = P4_VOCAB_LADDERS[key];

        var variant = ladder ? ladder[Vocab.level - 1] : null;

        if (!variant || variant === hit) return m;

        var rep = variant;

        var isAllCaps = hit.length > 1 && hit === hit.toUpperCase() && /[A-Z]/.test(hit);

        if (isAllCaps) rep = variant.toUpperCase();

        else if (hit[0] === hit[0].toUpperCase() && hit[0] !== hit[0].toLowerCase()) {

          rep = variant.charAt(0).toUpperCase() + variant.slice(1);

        }

        return pre + rep;

      });

    },



    applyAll: function () {

      if (Vocab.applying) return;

      Vocab.applying = true;

      try { Vocab.walk(document.body); } catch (e) {}

      Vocab.applying = false;

    },



    /* Single-pass replacement over a longest-first alternation of every key.

       Replaced output is never rescanned, so level variants that themselves

       contain registered words ("Exercises" -> "moves") cannot cascade. */

    buildKeyIndex: function () {

      var keys = Object.keys(P4_VOCAB_LADDERS);

      keys.sort(function (a, b) { return b.length - a.length; });

      var lower = {};

      keys.forEach(function (k) { lower[k.toLowerCase()] = k; });

      Vocab._lowerMap = lower;

      var alts = keys.map(function (k) { return k.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); }).join('|');

      /* pre-boundary consumed, post-boundary is lookahead (safe for adjacent hits) */

      Vocab._masterRe = new RegExp("((?:^|[^A-Za-z0-9']))(" + alts + ")(?![A-Za-z0-9'])", "gi");

    },



    init: function () {

      Vocab.level = Vocab.get();

      Vocab.buildKeyIndex();

      if (Vocab.level !== 5) {

        setTimeout(function () { Vocab.applyAll(); }, 300);

        var mo = new MutationObserver(P4.debounce(function (muts) {

          if (Vocab.applying || Vocab.level === 5) return;

          for (var i = 0; i < muts.length; i++) {

            var m = muts[i];

            if (m.type === 'childList' && m.addedNodes.length) {

              for (var j = 0; j < m.addedNodes.length; j++) {

                var n = m.addedNodes[j];

                if (n.nodeType === 3) {

                  if (!Vocab._done.has(n) && !Vocab.shouldSkip(n)) {

                    var out = Vocab.transformText(n.nodeValue || '');

                    if (out !== n.nodeValue) {

                      Vocab.applying = true;

                      try { n.nodeValue = out; } catch (e) {}

                      Vocab.applying = false;

                    }

                    Vocab._done.add(n);

                  }

                } else if (n.nodeType === 1 && !Vocab.shouldSkip(n)) {

                  try { Vocab.walk(n); } catch (e) {}

                }

              }

            } else if (m.type === 'characterData') {

              var t = m.target;

              if (t.nodeType === 3 && !Vocab._done.has(t) && !Vocab.shouldSkip(t)) {

                var o2 = Vocab.transformText(t.nodeValue || '');

                if (o2 !== t.nodeValue) {

                  Vocab.applying = true;

                  try { t.nodeValue = o2; } catch (e) {}

                  Vocab.applying = false;

                }

                Vocab._done.add(t);

              }

            }

          }

        }, 220));

        try { mo.observe(document.body, { childList: true, subtree: true, characterData: true }); }

        catch (e) {}

      }

    }

  };

})();



/* ============================================================================

 * P4 NAV — bottom tab bar (Home / Train / Library / Progress / More),

 * More sheet, swipe navigation, old bottom-nav rebuild (ids preserved).

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;

  var TABS = [

    { id: 'dashboard', icon: 'fa-house',        label: 'Home' },

    { id: 'workout',   icon: 'fa-dumbbell',     label: 'Train', fab: true },

    { id: 'library',   icon: 'fa-book-open',    label: 'Library' },

    { id: 'progress',  icon: 'fa-chart-line',   label: 'Progress' },

    { id: 'more',      icon: 'fa-ellipsis',     label: 'More', sheet: true }

  ];

  var SHEET_ITEMS = [

    { id: 'history',  icon: 'fa-clock-rotate-left', label: 'History' },

    { id: 'recovery', icon: 'fa-heart-pulse',       label: 'Recovery' },

    { id: 'settings', icon: 'fa-gear',              label: 'Settings' },

    { id: 'resume',   icon: 'fa-play',              label: 'Resume', fn: 'resumeSavedWorkout' },

    { id: 'export',   icon: 'fa-file-export',       label: 'Export', fn: 'P4.Nav.openExport' },

    { id: 'long',     icon: 'fa-user-md',           label: 'Longevity', fn: 'P4.Nav.openLongevity' },

    { id: 'period',   icon: 'fa-calendar-days',     label: 'My Cycle', fn: 'P4.Nav.openPeriod' },

    { id: 'backup',   icon: 'fa-cloud-arrow-down',  label: 'Backup', fn: 'exportWorkoutData' },

    { id: 'themes',   icon: 'fa-palette',           label: 'Themes', fn: 'P4.Nav.openThemes' },

    { id: 'glossary', icon: 'fa-spell-check',       label: 'Words',  fn: 'P4.vocabUI.openGlossary' },

    { id: 'studio',   icon: 'fa-flask-vial',        label: 'Studio', fn: 'P4.Studio.open' }

  ];



  var Nav = P4.Nav = {

    current: 'dashboard',

    build: function () {

      var old = document.getElementById('bottomNav');

      if (!old) return;

      /* preserve legacy Resume id inside the sheet so old JS keeps working */

      var bar = document.createElement('nav');

      bar.id = 'p4TabBar';

      bar.setAttribute('role', 'navigation');

      bar.setAttribute('aria-label', 'Primary');

      var html = '';

      for (var i = 0; i < TABS.length; i++) {

        var t = TABS[i];

        if (t.fab) {

          html += '<div class="p4-tab-fabwrap"><a href="#" class="p4-tab-fab" data-nav="' + t.id + '" ' +

            'onclick="showSection(\'' + t.id + '\'); return false;" aria-label="' + t.label + '">' +

            '<i class="fas ' + t.icon + '"></i><span>' + SEC.escapeHtml(t.label) + '</span></a></div>';

        } else {

          html += '<a href="#" class="p4-tab" data-nav="' + t.id + '" ' +

            (t.sheet ? 'data-sheet="1"' : 'onclick="showSection(\'' + t.id + '\'); return false;"') +

            ' aria-label="' + t.label + '"><i class="fas ' + t.icon + '"></i><span>' + SEC.escapeHtml(t.label) + '</span></a>';

        }

      }

      bar.innerHTML = html;

      old.parentNode.insertBefore(bar, old.nextSibling);



      /* More sheet */

      var sheet = document.createElement('div');

      sheet.id = 'p4SheetBackdrop';

      var inner = document.createElement('div');

      inner.id = 'p4Sheet';

      var cells = '';

      for (var j = 0; j < SHEET_ITEMS.length; j++) {

        var s = SHEET_ITEMS[j];

        var act = s.fn ? ('onclick="' + s.fn + '(); return false;"') :

          ('onclick="showSection(\'' + s.id + '\'); P4.Nav.closeSheet(); return false;"');

        if (s.id === 'resume') {

          cells += '<a href="#" class="p4-sheet-cell" id="bottomResumeWorkoutBtn" style="display:none;" ' +

            'onclick="resumeSavedWorkout(); return false;"><i class="fas ' + s.icon + '"></i><span>' + s.label + '</span></a>';

        } else {

          cells += '<a href="#" class="p4-sheet-cell" ' + act + '><i class="fas ' + s.icon + '"></i><span>' +

            SEC.escapeHtml(s.label) + '</span></a>';

        }

      }

      inner.innerHTML = '<div class="p4-sheet-grab"></div><div class="p4-sheet-title">Everything else</div>' +

        '<div class="p4-sheet-grid">' + cells + '</div>' +

        '<div class="p4-sheet-quick" id="p4SheetQuick"></div>';

      sheet.appendChild(inner);

      document.body.appendChild(sheet);

      sheet.addEventListener('click', function (e) { if (e.target === sheet) Nav.closeSheet(); });



      /* More tab opens sheet */

      var moreTab = bar.querySelector('[data-sheet="1"]');

      if (moreTab) {

        moreTab.addEventListener('click', function (e) { e.preventDefault(); Nav.openSheet(); });

      }



      /* re-sync legacy resume visibility: watch the old button's display value

         by polling once — the app toggles it directly by id */

      Nav.observeResume();

      Nav.syncActive();

    },



    observeResume: function () {

      var btn = document.getElementById('bottomResumeWorkoutBtn');

      if (!btn) return;

      var mo = new MutationObserver(function () { Nav.syncResume(); });

      try { mo.observe(btn, { attributes: true, attributeFilter: ['style'] }); } catch (e) {}

      Nav.syncResume();

    },

    syncResume: function () {

      var legacy = document.getElementById('p4LegacyResumeAnchor');

      var btn = document.getElementById('bottomResumeWorkoutBtn');

      if (!legacy || !btn) return;

      legacy.style.display = btn.style.display === 'none' ? 'none' : 'flex';

    },



    openSheet: function () {

      var bd = document.getElementById('p4SheetBackdrop'), sh = document.getElementById('p4Sheet');

      if (!bd || !sh) return;

      Nav.syncQuick();

      bd.classList.add('open'); sh.classList.add('open');

      P4.buzz(8);

    },

    closeSheet: function () {

      var bd = document.getElementById('p4SheetBackdrop'), sh = document.getElementById('p4Sheet');

      if (bd) bd.classList.remove('open');

      if (sh) sh.classList.remove('open');

    },

    syncQuick: function () {

      var host = document.getElementById('p4SheetQuick');

      if (!host) return;

      var gym = P4.Theme.gymOn();

      var lvl = P4.Vocab.get();

      var mode = P4.Theme.get();

      host.innerHTML =

        '<span class="p4-quickchip' + (mode.mode === 'dark' ? ' on' : '') + '" onclick="P4.Theme.set({mode:\'' +

          (mode.mode === 'dark' ? 'light' : 'dark') + '\'});P4.Nav.syncQuick();"><i class="fas ' +

          (mode.mode === 'dark' ? 'fa-moon' : 'fa-sun') + '"></i>' + (mode.mode === 'dark' ? 'Dark' : 'Light') + '</span>' +

        '<span class="p4-quickchip' + (gym ? ' on' : '') + '" onclick="P4.Power.toggleGym();P4.Nav.syncQuick();"><i class="fas fa-hand-fist"></i>Gym Mode</span>' +

        '<span class="p4-quickchip" onclick="P4.Nav.cycleVocab();P4.Nav.syncQuick();"><i class="fas fa-language"></i>Words: L' + lvl + '</span>' +

        '<span class="p4-quickchip" onclick="exportWorkoutData();"><i class="fas fa-download"></i>Export JSON</span>';

    },

    /* P5P10: former Quick-Actions entries now live here */

    openExport: function () { Nav.closeSheet(); if (typeof exportWorkoutData === 'function') exportWorkoutData(); },

    openLongevity: function () { Nav.closeSheet(); if (typeof generateLongevityWorkout === 'function') generateLongevityWorkout(); },

    openPeriod: function () {

      Nav.closeSheet();

      if (workoutData && workoutData.user && workoutData.user.gender === 'female' && typeof showModal === 'function') {

        showModal('periodModal');

      } else {

        P4.toast('Cycle tracking lives under Settings once your profile says Female.', 'info', 4000);

      }

    },

    cycleVocab: function () {

      var next = P4.Vocab.get() >= 10 ? 1 : P4.Vocab.get() + 1;

      P4.Vocab.set(next);

      Nav.syncQuick();

    },

    openThemes: function () {

      Nav.closeSheet();

      if (P4.SettingsUI && P4.SettingsUI.openThemes) P4.SettingsUI.openThemes();

      else showSection('settings');

    },



    syncActive: function () {

      var bar = document.getElementById('p4TabBar');

      if (bar) {

        var tabs = bar.querySelectorAll('.p4-tab, .p4-tab-fab');

        for (var i = 0; i < tabs.length; i++) {

          var id = tabs[i].getAttribute('data-nav');

          tabs[i].classList.toggle('active', id === Nav.current);

        }

      }

      /*P6:B*//* P6B: desktop navbar highlight follows the selected page too */

      var menu = document.getElementById('navMenu');

      if (menu) {

        var items = menu.querySelectorAll('.nav-item[data-nav]');

        for (var j = 0; j < items.length; j++) {

          items[j].classList.toggle('active', items[j].getAttribute('data-nav') === Nav.current);

        }

      }

    },



    /* showSection wrapper: tracks current section, closes sheet, buzz, tap metrics */

    hook: function () {

      if (typeof window.showSection !== 'function') return;

      var orig = window.showSection;

      window.showSection = function (sectionId, replaceState) {

        var r = orig.apply(this, arguments);

        Nav.current = sectionId;

        Nav.closeSheet();

        Nav.syncActive();

        var m = store.get(P4.keys.taps, {});

        m['nav_' + sectionId] = (m['nav_' + sectionId] || 0) + 1;

        store.set(P4.keys.taps, m);

        return r;

      };

    },



    /* Swipe left/right between top-level sections (touch) */

    swipes: function () {

      var seq = ['dashboard', 'workout', 'library', 'progress'];

      var sx = 0, sy = 0, ok = false;

      document.addEventListener('touchstart', function (e) {

        if (e.touches.length !== 1) { ok = false; return; }

        sx = e.touches[0].clientX; sy = e.touches[0].clientY; ok = true;

      }, { passive: true });

      document.addEventListener('touchend', function (e) {

        if (!ok) return; ok = false;

        var t = e.changedTouches[0];

        var dx = t.clientX - sx, dy = t.clientY - sy;

        if (Math.abs(dx) < 90 || Math.abs(dy) > 60) return;

        if (!Nav._swipeEnabled) return;

        var idx = seq.indexOf(Nav.current);

        if (idx === -1) return;

        var next = dx < 0 ? seq[idx + 1] : seq[idx - 1];

        if (next) { showSection(next); P4.buzz(6); }

      }, { passive: true });

      Nav._swipeEnabled = true;

    }

  };

})();



/* ============================================================================

 * P4 POWER — the factory-floor layer. Fewest taps, biggest targets, zero fuss.

 * Quick-action dock, rest-timer overlay, gym mode, tap-distance metrics.

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  var Power = P4.Power = {

    settings: store.get(P4.keys.settings, { gymMode: false, autoRest: true, haptics: true, swipe: true, dock: true }),



    saveSettings: function () { store.set(P4.keys.settings, Power.settings); },



    init: function () {

      if (Power.settings.gymMode) document.documentElement.setAttribute('data-gym', 'on');

      if (Power.settings.swipe !== false) P4.Nav.swipes();

      if (Power.settings.dock !== false) Power.buildDock();

      Power.hookGeneration();

      Power.metricsStamp();

    },



    /* ---------------- dock ---------------- */

    buildDock: function () {

      if (document.getElementById('p4Dock')) return;

      var dock = document.createElement('div');

      dock.id = 'p4Dock';

      dock.innerHTML =

        '<button class="p4-dock-btn" title="Rest timer" aria-label="Rest timer" onclick="P4.Power.restStart()"><i class="fas fa-stopwatch"></i></button>' +

        '<button class="p4-dock-btn" title="Export JSON" aria-label="Backup now" onclick="exportWorkoutData()"><i class="fas fa-cloud-arrow-down"></i></button>' +

        '<button class="p4-dock-btn" title="Gym Mode: giant buttons" aria-label="Gym mode" onclick="P4.Power.toggleGym()"><i class="fas fa-hand-fist"></i></button>' +

        '<button class="p4-dock-btn" title="Top of page" aria-label="Scroll to top" onclick="P4.Power.toTop()"><i class="fas fa-arrow-up"></i></button>';

      document.body.appendChild(dock);

    },



    /* P5P1: jump to the very top — works no matter which scroll container moved */

    toTop: function () {

      try { window.scrollTo({ top: 0, behavior: 'auto' }); } catch (e) { window.scrollTo(0, 0); }

      document.documentElement.scrollTop = 0;

      document.body.scrollTop = 0;

      var act = document.querySelector('.section.active .container');

      if (act) act.scrollTop = 0;

      P4.buzz(6);

    },



    toggleGym: function () {

      var on = !P4.Theme.gymOn();

      P4.Theme.gym(on);

      Power.settings.gymMode = on;

      Power.saveSettings();

      P4.toast(on ? 'Gym Mode ON — huge buttons, no waiting on animations.' : 'Gym Mode off.', 'ok');

      P4.buzz(18);

    },



    /* ---------------- rest timer ---------------- */

    restDefault: 90,

    ensureOverlay: function () {

      if (document.getElementById('p4RestTimer')) return;

      var el = document.createElement('div');

      el.id = 'p4RestTimer';

      el.innerHTML =

        '<div><div class="p4-rt-time" id="p4RtTime">1:30</div><div class="p4-rt-label">Rest</div></div>' +

        '<div class="p4-rt-btns">' +

        '<button onclick="P4.Power.restAdd(15)">+15s</button>' +

        '<button onclick="P4.Power.restStop()">Done</button>' +

        '</div>';

      document.body.appendChild(el);

    },

    restStart: function (secs) {

      Power.ensureOverlay();

      var s = secs || parseInt((typeof workoutData !== 'undefined' && workoutData.user && workoutData.user.restTime) || 0, 10) || Power.restDefault;

      Power._rtEnd = Date.now() + s * 1000;

      var el = document.getElementById('p4RestTimer');

      if (el) el.classList.add('on');

      clearInterval(Power._rtInt);

      Power._rtInt = setInterval(Power.restTick, 250);

      Power.restTick();

      try { if (navigator.wakeLock && typeof requestWakeLock === 'function') requestWakeLock(); } catch (e) {}

    },

    restTick: function () {

      var t = document.getElementById('p4RtTime');

      if (!t || !Power._rtEnd) return;

      var left = Math.max(0, Math.round((Power._rtEnd - Date.now()) / 1000));

      var m = Math.floor(left / 60), s = left % 60;

      t.textContent = m + ':' + String(s).padStart(2, '0');

      if (left === 3 || left === 2 || left === 1) P4.buzz(40);

      if (left <= 0) {

        P4.buzz([60, 40, 60]);

        P4.toast('Rest done — next set!', 'ok');

        Power.restStop();

      }

    },

    restAdd: function (n) { Power._rtEnd += n * 1000; Power.restTick(); },

    restStop: function () {

      clearInterval(Power._rtInt);

      Power._rtInt = null;

      var el = document.getElementById('p4RestTimer');

      if (el) el.classList.remove('on');

    },



    /* ---------------- hooks into the existing engine ---------------- */

    hookGeneration: function () {

      /* auto rest-timer after an RPE/weight entry (if autoRest enabled) */

      document.addEventListener('change', function (e) {

        if (!Power.settings.autoRest) return;

        var id = e.target && e.target.id;

        if (!id || typeof id.indexOf !== 'function') return;

        if (id.indexOf('rpe_') === 0 && Power._rtInt === null) {

          setTimeout(function () { Power.restStart(); }, 350);

        }

      }, true);



      /* celebrate completion with haptics + snapshot + backup nudge */

      var origComplete = window.completeWorkout;

      if (typeof origComplete === 'function') {

        window.completeWorkout = function () {

          var r = origComplete.apply(this, arguments);

          try { P4.buzz([30, 40, 30]); store.snapshot(); } catch (e) {}

          setTimeout(function () {

            if (P4.Motivation) P4.Motivation.afterWorkout();

            P4.Backup.nudgeAfterWorkout();

          }, 900);

          return r;

        };

      }



      /* measure taps-to-workout on the Start button */

      document.addEventListener('click', function (e) {

        var b = e.target && e.target.closest && e.target.closest('.btn-luxury-start');

        if (!b) return;

        var m = store.get(P4.keys.taps, {});

        m.startTaps = (m.startTaps || 0) + 1;

        m.lastStart = Date.now();

        store.set(P4.keys.taps, m);

      }, true);

    },



    metricsStamp: function () {

      var m = store.get(P4.keys.taps, {});

      m.version = P4.version;

      store.set(P4.keys.taps, m);

    },



    /* tap-distance readout used in Settings ("power user" transparency) */

    tapsSummary: function () {

      var m = store.get(P4.keys.taps, {});

      return {

        starts: m.startTaps || 0,

        homeVisits: m.nav_dashboard || 0,

        trainVisits: m.nav_workout || 0,

        libraryVisits: m.nav_library || 0

      };

    }

  };

})();



/* ============================================================================

 * P4 BACKUP — one-tap export, end-of-day reminder, after-workout nudge,

 * rolling snapshots, full import with format sniffing + safety merge.

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;

  var DAY = 86400000;



  var Backup = P4.Backup = {

    last: function () { return store.get(P4.keys.backup, 0); },



    stamp: function () {

      store.set(P4.keys.backup, Date.now());

      Backup.hideBanner();

      P4.toast('Backup saved to your device.', 'ok');

    },



    collectAll: function () {

      var payload = {

        kind: 'try4ever-fitness-backup',

        version: P4.version,

        exportedAt: new Date().toISOString(),

        workoutData: store.getRaw ? store.getRaw('workoutData') : null,

        profile: store.get(P4.keys.profile, null),

        consent: store.get(P4.keys.consent, []),

        vocabLevel: P4.Vocab.get(),

        theme: P4.Theme.get(),

        gym: P4.Theme.gymOn(),

        lang: store.get(P4.keys.lang, 'auto'),

        badges: store.get(P4.keys.badges, {}),

        customExercises: Backup.getCustomExercises(),

        license: store.get(P4.keys.license, null)

      };

      try { payload.workoutData = JSON.parse(localStorage.getItem('workoutData') || 'null'); } catch (e) {}

      return payload;

    },



    getCustomExercises: function () {

      try {

        if (typeof exerciseLibrary !== 'undefined' && exerciseLibrary) {

          var custom = [];

          for (var g in exerciseLibrary) {

            if (!Array.isArray(exerciseLibrary[g])) continue;

            for (var i = 0; i < exerciseLibrary[g].length; i++) {

              if (exerciseLibrary[g][i] && exerciseLibrary[g][i].source === 'studio' ||

                  exerciseLibrary[g][i] && exerciseLibrary[g][i].custom) {

                custom.push(exerciseLibrary[g][i]);

              }

            }

          }

          return custom;

        }

      } catch (e) {}

      return [];

    },



    exportAll: function () {

      /* Delegates to the working synchronous exportWorkoutData() (data: URI — works in iframe + APK) */

      if (typeof exportWorkoutData === 'function') { exportWorkoutData(); return; }

      /* Fallback to legacy if exportWorkoutData not loaded */

      var payload = Backup.collectAll();

      P4.download('FitnessBackup_' + P4.todayKey() + '.json', JSON.stringify(payload, null, 2));

      Backup.stamp();

      store.snapshot();

      if (P4.Badges) P4.Badges.countEvent('backup');

    },



    exportLibrary: function () {

      if (typeof exerciseLibrary === 'undefined' || !exerciseLibrary) {

        P4.toast('Library not loaded yet — try again in a moment.', 'warn');

        return;

      }

      P4.download('ExerciseLibrary_' + P4.todayKey() + '.json', JSON.stringify(exerciseLibrary, null, 1));

      P4.toast('Exercise library exported.', 'ok');

    },



    importAll: function (text) {

      var heal = SEC.healJson(text);

      if (!heal.ok) { P4.toast('Not valid JSON: ' + heal.error, 'bad'); return false; }

      var data = heal.data;

      var wd = data && (data.workoutData || data.workout_data);

      if (!wd && !Array.isArray(data)) {

        /* sniff: bare workoutData object? */

        if (data && (data.workouts || data.user)) wd = data;

      }

      if (!wd) { P4.toast('No workoutData found in this file.', 'bad'); return false; }

      try {

        localStorage.setItem('workoutData', JSON.stringify(wd));

      } catch (e) { P4.toast('Storage error during import.', 'bad'); return false; }

      if (data.profile) store.set(P4.keys.profile, data.profile);

      if (data.consent) store.set(P4.keys.consent, data.consent);

      if (data.badges) store.set(P4.keys.badges, data.badges);

      if (data.theme) store.set(P4.keys.theme, data.theme);

      if (typeof data.vocabLevel === 'number') store.set(P4.keys.vocab, data.vocabLevel);

      P4.toast('Backup restored. Reloading…', 'ok');

      setTimeout(function () { location.reload(); }, 900);

      return true;

    },



    pickImport: function () {

      /* Delegates to the working importWorkoutData() (opens import modal) */

      if (typeof importWorkoutData === 'function') { importWorkoutData(); return; }

      /* Fallback to legacy file picker */

      var input = document.createElement('input');

      input.type = 'file';

      input.accept = 'application/json,.json';

      input.onchange = function () {

        var f = input.files && input.files[0];

        if (!f) return;

        var r = new FileReader();

        r.onload = function (ev) { Backup.importAll(String(ev.target.result || '')); };

        r.onerror = function () { P4.toast('Could not read that file.', 'bad'); };

        r.readAsText(f);

      };

      input.click();

    },



    /* rolling snapshot rescue */

    snapshots: function () { return store.get(P4.keys.snapshots, []); },

    restoreSnapshot: function (idx) {

      var s = Backup.snapshots()[idx];

      if (!s) { P4.toast('No snapshot at that slot.', 'warn'); return; }

      try {

        localStorage.setItem('workoutData', s.data);

        P4.toast('Snapshot from ' + P4.fmtDate(s.at) + ' restored. Reloading…', 'ok');

        setTimeout(function () { location.reload(); }, 900);

      } catch (e) { P4.toast('Snapshot restore failed.', 'bad'); }

    },



    /* ---------------- reminders ---------------- */

    showBanner: function (title, sub) {

      var dash = document.querySelector('#dashboard-section .container') ||

                 document.querySelector('#dashboard-section');

      if (!dash) return;

      var b = document.getElementById('p4BackupBanner');

      if (!b) {

        b = document.createElement('div');

        b.id = 'p4BackupBanner';

        b.innerHTML = '<i class="fas fa-shield-halved"></i>' +

          '<div><b id="p4BkTitle"></b><span id="p4BkSub"></span></div>' +

          '<button class="btn btn-primary" onclick="exportWorkoutData()"><i class="fas fa-download"></i> Backup Now</button>';

        if (dash.firstChild) dash.insertBefore(b, dash.firstChild); else dash.appendChild(b);

      }

      document.getElementById('p4BkTitle').textContent = title;

      document.getElementById('p4BkSub').textContent = sub || '';

      b.classList.add('show');

    },

    hideBanner: function () {

      var b = document.getElementById('p4BackupBanner');

      if (b) b.classList.remove('show');

    },



    checkDaily: function () {

      var last = Backup.last();

      var age = Date.now() - last;

      if (!last) {

        if (typeof workoutData === 'undefined' || !workoutData || !workoutData.workouts || workoutData.workouts.length === 0) return;

        Backup.showBanner('Keep your hard work safe', 'One tap saves a full copy on this device.');

        return;

      }

      if (age > 7 * DAY) {

        Backup.showBanner('Last backup was ' + Math.floor(age / DAY) + ' days ago', 'A 1-tap copy keeps everything safe.');

      } else {

        var h = new Date().getHours();

        if (h >= 20 && new Date(last).toDateString() !== new Date().toDateString()) {

          Backup.showBanner('Evening reminder — backup before bed', 'Takes one tap. Your data stays on this device.');

        }

      }

    },

    nudgeAfterWorkout: function () {

      var last = Backup.last();

      if (!last || Date.now() - last > DAY) {

        setTimeout(function () {

          P4.toast('Workout saved locally. Want a backup too?', 'info', 5200);

        }, 1400);

      }

    },



    init: function () {

      setTimeout(function () { Backup.checkDaily(); }, 1600);

    }

  };

})();



/* ============================================================================

 * P4 MOTIVATION — daily quote, win-of-the-day, expert tips rotation,

 * badge engine (evaluate + toast + gallery), confetti fallback.

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  var Mot = P4.Motivation = {

    /* ---------------- data helpers ---------------- */

    band: function () {

      var l = P4.Vocab.get();

      if (l <= 3) return 1;

      if (l <= 6) return 2;

      return 3;

    },



    dailyQuote: function () {

      var dayKey = P4.todayKey();

      var saved = store.get(P4.keys.quoteDay, {});

      var q;

      if (saved && saved.day === dayKey && saved.text) {

        q = saved;

      } else {

        var band = Mot.band();

        var pool = P4_QUOTES.filter(function (x) { return !x.band || x.band === band; });

        if (!pool.length) pool = P4_QUOTES;

        var pick = P4.pick(pool);

        q = { day: dayKey, text: pick.t, by: pick.a || '' };

        store.set(P4.keys.quoteDay, q);

      }

      return q;

    },



    /* ---------------- win of the day ---------------- */

    stats: function () {

      var wd = (typeof workoutData !== 'undefined' && workoutData) || {};

      var ws = wd.workouts || [];

      var totalVol = 0, totalW = ws.length;

      var prCount = 0, tried = {};

      for (var i = 0; i < ws.length; i++) {

        var w = ws[i];

        if (w.summary && w.summary.totalVolume) totalVol += w.summary.totalVolume;

        if (w.isPR) prCount++;

        var exs = w.exercises || [];

        for (var j = 0; j < exs.length; j++) tried[exs[j].id || exs[j].name] = 1;

      }

      return { total: totalW, vol: totalVol, prs: prCount, tried: Object.keys(tried).length, list: ws };

    },



    streak: function () {

      try {

        if (typeof calculateStreak === 'function') return calculateStreak();

        if (typeof workoutData !== 'undefined' && workoutData && typeof workoutData.streak === 'number') return workoutData.streak;

      } catch (e) {}

      return 0;

    },



    winOfTheDay: function () {

      var s = Mot.stats();

      var st = Mot.streak();

      if (!s.total) {

        return { icon: 'fa-seedling', big: 'Your first win is waiting', small: 'Generate a workout — it takes two taps.' };

      }

      var last = s.list[s.list.length - 1];

      var lastDate = last && (last.dateCompleted || last.date);

      var today = P4.todayKey();

      var didToday = lastDate && String(lastDate).indexOf(today) !== -1;

      if (didToday) {

        var vol = (last.summary && last.summary.totalVolume) || 0;

        return {

          icon: 'fa-fire',

          big: 'You trained today — that is the whole game.',

          small: vol ? ('Volume moved: ' + Math.round(vol).toLocaleString() + ' lbs. Streak: ' + st + ' day' + (st === 1 ? '' : 's') + '.') : ('Streak: ' + st + '. See you tomorrow.')

        };

      }

      if (st >= 2) return { icon: 'fa-fire', big: 'Streak: ' + st + ' days strong', small: 'One session keeps it alive. Short counts.' };

      if (s.total >= 3) return { icon: 'fa-bolt', big: 'Welcome back', small: 'You have ' + s.total + ' sessions banked. Add one more today.' };

      return { icon: 'fa-star', big: 'Every rep you do today pays you back', small: 'Start small. Two taps and you are moving.' };

    },



    /* ---------------- tips rotation ---------------- */

    tips: function (n) {

      n = n || 2;

      var band = Mot.band();

      var pool = P4_TIPS.filter(function (x) { return !x.band || x.band === band; });

      if (pool.length < n) pool = P4_TIPS;

      var out = [], used = {};

      while (out.length < n && out.length < pool.length) {

        var t = P4.pick(pool);

        var key = t.e + '|' + t.t.slice(0, 24);

        if (used[key]) continue;

        used[key] = 1;

        out.push(t);

      }

      return out;

    },



    domainOf: function (id) {

      for (var i = 0; i < P4_TIP_DOMAINS.length; i++) if (P4_TIP_DOMAINS[i].id === id) return P4_TIP_DOMAINS[i];

      return { id: id, name: 'Coach', icon: 'fa-circle-info' };

    },



    /* ---------------- badges ---------------- */

    countEvent: function (kind) {

      var counts = store.get(P4.keys.badges, {});

      counts[kind] = (counts[kind] || 0) + 1;

      store.set(P4.keys.badges, counts);

      Mot.evaluate();

    },



    evaluate: function () {

      var s = Mot.stats();

      var st = Mot.streak();

      var counts = store.get(P4.keys.badges, {});

      var ctx = {

        workouts: s.total,

        streak: st,

        volume: s.vol,

        prs: s.prs,

        tried: s.tried,

        backups: counts.backup || 0,

        studioAdds: counts.studioAdd || 0,

        imports: counts.import || 0,

        exports: counts.export || 0,

        earlyBird: Mot.isEarlyBird(s.list),

        nightOwl: Mot.isNightOwl(s.list),

        comeback: Mot.isComeback(s.list),

        gymSessions: counts.gymOn || 0,

        vocabChanged: counts.vocabChanged || 0,

        themesTried: counts.themeChanged || 0

      };

      var have = store.get(P4.keys.badgesSeen, {});

      var unlocked = [];

      for (var i = 0; i < P4_BADGES.length; i++) {

        var b = P4_BADGES[i];

        if (have[b.id]) continue;

        var ok = false;

        try { ok = Mot.check(b, ctx); } catch (e) { ok = false; }

        if (ok) { have[b.id] = Date.now(); unlocked.push(b); }

      }

      if (unlocked.length) {

        store.set(P4.keys.badgesSeen, have);

        unlocked.forEach(function (b, i) {

          setTimeout(function () {

            P4.toast('Badge unlocked: ' + b.icon + ' ' + b.name + ' — ' + b.desc, 'ok', 4200);

            Mot.confetti(90);

            P4.buzz([30, 50, 30]);

          }, 1200 + i * 1600);

        });

      }

      return unlocked.length;

    },



    check: function (b, c) {

      var chk = b.check || {};

      switch (chk.type) {

        case 'workouts': return c.workouts >= chk.value;

        case 'streak': return c.streak >= chk.value;

        case 'volume': return c.volume >= chk.value;

        case 'prs': return c.prs >= chk.value;

        case 'tried': return c.tried >= chk.value;

        case 'backups': return c.backups >= chk.value;

        case 'studioAdds': return c.studioAdds >= chk.value;

        case 'imports': return c.imports >= chk.value;

        case 'exports': return c.exports >= chk.value;

        case 'earlyBird': return c.earlyBird;

        case 'nightOwl': return c.nightOwl;

        case 'comeback': return c.comeback;

        case 'gym': return c.gymSessions >= chk.value;

        case 'vocab': return c.vocabChanged >= chk.value;

        case 'themes': return c.themesTried >= chk.value;

        default: return false;

      }

    },



    isEarlyBird: function (ws) {

      for (var i = Math.max(0, ws.length - 10); i < ws.length; i++) {

        var d = new Date(ws[i].dateCompleted || ws[i].date || 0).getHours();

        if (d > 0 && d < 8) return true;

      }

      return false;

    },

    isNightOwl: function (ws) {

      for (var i = Math.max(0, ws.length - 10); i < ws.length; i++) {

        var d = new Date(ws[i].dateCompleted || ws[i].date || 0).getHours();

        if (d >= 21) return true;

      }

      return false;

    },

    isComeback: function (ws) {

      if (ws.length < 3) return false;

      var dates = ws.map(function (w) { return new Date(w.dateCompleted || w.date || 0).getTime(); }).sort();

      for (var i = 1; i < dates.length; i++) {

        if (dates[i] - dates[i - 1] > 21 * 86400000 && dates[dates.length - 1] - dates[i - 1] < 10 * 86400000) return true;

      }

      return false;

    },



    /* ---------------- confetti (uses app's canvas-confetti if present) ---------------- */

    confetti: function (count) {

      /* cap at 3 confetti bursts per session — no login bombardment */

      if (!P4._confettiCount) P4._confettiCount = 0;

      if (P4._confettiCount >= 3) return;

      P4._confettiCount++;

      if (typeof confetti === 'function') {

        try { confetti({ particleCount: count || 130, spread: 75, origin: { y: 0.65 } }); return; } catch (e) {}

      }

      Mot._miniConfetti(count || 80);

    },

    _miniConfetti: function (n) {

      var c = document.createElement('canvas');

      c.style.cssText = 'position:fixed;inset:0;pointer-events:none;z-index:9000';

      c.width = innerWidth; c.height = innerHeight;

      document.body.appendChild(c);

      var ctx = c.getContext('2d');

      var cols = ['#4E9BFF', '#34D399', '#FACC15', '#F472B6', '#FB923C'];

      var parts = [];

      for (var i = 0; i < n; i++) {

        parts.push({

          x: c.width / 2 + (Math.random() - .5) * c.width * .4,

          y: c.height * .4 + Math.random() * 40,

          vx: (Math.random() - .5) * 9, vy: -6 - Math.random() * 7,

          s: 4 + Math.random() * 5, r: Math.random() * Math.PI,

          vr: (Math.random() - .5) * .3, col: cols[i % cols.length]

        });

      }

      var t = 0;

      var iv = setInterval(function () {

        t++;

        ctx.clearRect(0, 0, c.width, c.height);

        for (var i = 0; i < parts.length; i++) {

          var p = parts[i];

          p.vy += .22; p.x += p.vx; p.y += p.vy; p.r += p.vr;

          ctx.save(); ctx.translate(p.x, p.y); ctx.rotate(p.r);

          ctx.fillStyle = p.col; ctx.fillRect(-p.s / 2, -p.s / 2, p.s, p.s * .62);

          ctx.restore();

        }

        if (t > 110) { clearInterval(iv); c.remove(); }

      }, 16);

    },



    /* ---------------- dashboard surfaces ---------------- */

    renderDashboard: function () {

      try {

        var dash = document.querySelector('#dashboard-section .container') || document.querySelector('#dashboard-section');

        if (!dash || document.getElementById('p4Quote')) return;

        var q = Mot.dailyQuote();

        var win = Mot.winOfTheDay();

        var tips = Mot.tips(2);

        var html = '<div id="p4Quote" class="p4-quote"><div class="p4-q-mark">\u201C</div>' +

          '<p>' + SEC.escapeHtml(q.text) + '</p>' +

          (q.by ? '<small>' + SEC.escapeHtml(q.by) + '</small>' : '') + '</div>';

        html += '<div id="p4Win" class="p4-win"><i class="fas ' + win.icon + '"></i><div><b>' +

          SEC.escapeHtml(win.big) + '</b><span>' + SEC.escapeHtml(win.small) + '</span></div></div>';

        html += '<div id="p4Tips">';

        for (var i = 0; i < tips.length; i++) {

          var dom = Mot.domainOf(tips[i].d);

          html += '<div class="p4-tip"><i class="fas ' + dom.icon + '"></i><div><b>' +

            SEC.escapeHtml(dom.name) + '</b><span>' + SEC.escapeHtml(tips[i].t) + '</span></div></div>';

        }

        html += '</div>';

        var stats = dash.querySelector('.stats-grid');

        if (stats && stats.parentNode) stats.insertAdjacentHTML('afterend', html);

        else dash.insertAdjacentHTML('beforeend', html);

      } catch (e) {}

    },



    afterWorkout: function () {

      Mot.evaluate();

      Mot.confetti(170);

      var win = Mot.winOfTheDay();

      P4.toast(win.big, 'ok', 4200);

      /* refresh surfaces if visible */

      var w = document.getElementById('p4Win');

      if (w) {

        w.innerHTML = '<i class="fas ' + win.icon + '"></i><div><b>' + SEC.escapeHtml(win.big) +

          '</b><span>' + SEC.escapeHtml(win.small) + '</span></div>';

      }

    },



    badgeSummary: function () {

      var have = store.get(P4.keys.badgesSeen, {});

      return { have: Object.keys(have).length, total: P4_BADGES.length, map: have };

    },



    init: function () {

      Mot.renderDashboard();

      setTimeout(function () { Mot.evaluate(); }, 2500);

      /* re-render quote/win when returning to dashboard */

      if (typeof window.showSection === 'function') {

        var orig = window.showSection;

        window.showSection = function (id) {

          var r = orig.apply(this, arguments);

          if (id === 'dashboard') setTimeout(function () {

            if (!document.getElementById('p4Quote')) Mot.renderDashboard();

          }, 350);

          return r;

        };

      }

    }

  };

})();



/* ============================================================================

 * P4 LEGAL — Legal Center UI, consent recording, data-legal delegation.

 * Documents live in P4_LEGAL_DOCS (p4_data_legal.js).

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  var Legal = P4.Legal = {

    DOC_VERSION: '2026-09-21',



    open: function (docId) {

      var host = document.getElementById('p4Legal');

      if (!host) return;

      host.classList.add('open');

      document.body.style.overflow = 'hidden';

      Legal.renderTabs();

      Legal.render(docId || (P4_LEGAL_DOCS[0] && P4_LEGAL_DOCS[0].id));

      Legal.renderConsentLog();

    },

    close: function () {

      var host = document.getElementById('p4Legal');

      if (host) host.classList.remove('open');

      document.body.style.overflow = '';

    },



    renderTabs: function () {

      var tabs = document.getElementById('p4LegalTabs');

      if (!tabs) return;

      tabs.innerHTML = P4_LEGAL_DOCS.map(function (d) {

        return '<span class="p4-legal-tab" data-doc="' + d.id + '">' + SEC.escapeHtml(d.short) + '</span>';

      }).join('');

      tabs.querySelectorAll('.p4-legal-tab').forEach(function (el) {

        el.addEventListener('click', function () { Legal.render(el.getAttribute('data-doc')); });

      });

    },



    render: function (docId) {

      var docEl = document.getElementById('p4LegalDoc');

      if (!docEl) return;

      var doc = null;

      for (var i = 0; i < P4_LEGAL_DOCS.length; i++) if (P4_LEGAL_DOCS[i].id === docId) doc = P4_LEGAL_DOCS[i];

      if (!doc) doc = P4_LEGAL_DOCS[0];

      document.querySelectorAll('#p4LegalTabs .p4-legal-tab').forEach(function (el) {

        el.classList.toggle('on', el.getAttribute('data-doc') === doc.id);

      });

      var html = '<div class="p4-legal-meta"><b>' + SEC.escapeHtml(doc.title) + '</b> · Effective ' +

        Legal.DOC_VERSION + ' · v1.0 · Operated by try4ever.com · Questions: support@try4ever.com</div>';

      (doc.sections || []).forEach(function (s) {

        html += '<h4>' + SEC.escapeHtml(s.h) + '</h4>';

        (s.p || []).forEach(function (para) { html += '<p>' + para + '</p>'; });

        (s.ul || []).forEach(function (li) { html += '<p style="margin:2px 0 2px 14px">• ' + li + '</p>'; });

      });

      docEl.innerHTML = html;

      docEl.scrollTop = 0;

    },



    renderConsentLog: function () {

      var el = document.getElementById('p4ConsentLog');

      if (!el) return;

      var log = store.get(P4.keys.consent, []);

      var rows = log.slice(0, 8).map(function (c) {

        return '<p>' + SEC.escapeHtml(new Date(c.at).toISOString()) + ' — ' + SEC.escapeHtml(c.kind) +

          (c.detail ? ' (' + SEC.escapeHtml(c.detail) + ')' : '') + ' · v' + SEC.escapeHtml(c.version || '') + '</p>';

      }).join('');

      el.innerHTML = '<h5><i class="fas fa-clock-rotate-left" style="margin-right:6px"></i>Your consent history' +

        ' <span style="font-weight:400;color:var(--p4-text-3)">(' + log.length + ' records, stored on this device)</span></h5>' +

        (rows || '<p>No consents recorded yet.</p>') +

        '<button class="btn" style="margin-top:8px" onclick="P4.Legal.exportConsent()"><i class="fas fa-download"></i> Export consent log</button>';

    },



    record: function (kind, detail) {

      var log = store.get(P4.keys.consent, []);

      log.unshift({ at: Date.now(), kind: kind, detail: detail || '', version: Legal.DOC_VERSION });

      store.set(P4.keys.consent, log.slice(0, 200));

    },



    agreedTo: function (kind) {

      var log = store.get(P4.keys.consent, []);

      for (var i = 0; i < log.length; i++) {

        if (log[i].kind === kind && log[i].version === Legal.DOC_VERSION) return true;

      }

      return false;

    },



    exportConsent: function () {

      P4.download('ConsentLog_' + P4.todayKey() + '.json', JSON.stringify(store.get(P4.keys.consent, []), null, 2));

    },



    /* global delegation — survives paywall takeover like the reference apps */

    delegate: function () {

      document.addEventListener('click', function (e) {

        var el = e.target.closest && e.target.closest('[data-legal]');

        if (!el) return;

        var id = el.getAttribute('data-legal');

        if (id && P4_LEGAL_DOCS.some(function (d) { return d.id === id; })) {

          e.preventDefault();

          Legal.open(id);

        }

      });

    },



    init: function () { Legal.delegate(); }

  };

})();



/* ============================================================================

 * P4 ONBOARDING — first-run wizard: welcome/name → birthdate & age gate →

 * vocabulary level → theme → consent (waiver) → done. Resumable, skippable

 * only where safe. Records consent, starts the trial, marks onboarded.

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  var Ob = P4.Onboard = {

    step: 1,

    profile: { name: '', birth: '', gender: 'unspecified', minor: false, guardian: false },

    draftTheme: { mode: 'dark', accent: 'blue' },

    draftVocab: 5,



    needed: function () {

      /* skip wizard if user has data (workouts already logged) — even if onboard flag is missing */

      try {

        if (store.get(P4.keys.onboard, false)) return false;

        if (typeof workoutData !== 'undefined' && workoutData && workoutData.workouts && workoutData.workouts.length > 0) return false;

      } catch (e) {}

      return !store.get(P4.keys.onboard, false);

    },



    start: function () {

      var saved = store.get('p4_ob_draft', null);

      if (saved) { Ob.step = saved.step || 1; Ob.profile = saved.profile || Ob.profile; }

      Ob.render();

    },



    persist: function () {

      store.set('p4_ob_draft', { step: Ob.step, profile: Ob.profile });

    },



    finish: function () {

      /* age & minor logic */

      var age = Ob.age();

      Ob.profile.minor = age !== null && age < 18;

      store.set(P4.keys.profile, Ob.profile);

      store.del('p4_ob_draft');

      store.set(P4.keys.onboard, true);



      /* apply theme + vocab */

      P4.Theme.set(Ob.draftTheme, { silent: true });

      P4.Vocab.set(Ob.draftVocab, true);



      /* feed the legacy app profile (name/birthdate/gender) if fields exist */

      try {

        if (typeof workoutData !== 'undefined' && workoutData && workoutData.user) {

          if (Ob.profile.name) workoutData.user.name = Ob.profile.name;

          if (Ob.profile.birth) workoutData.user.birthDate = Ob.profile.birth;

          if (Ob.profile.gender) workoutData.user.gender = Ob.profile.gender;

          if (typeof saveWorkoutData === 'function') saveWorkoutData();

          /* P5P13/P5P15: refresh title/avatar/header name right away */

          try { if (typeof updateHeaderNameAndStreak === 'function') updateHeaderNameAndStreak(); } catch (e) {}

          try { if (typeof updateNavigation === 'function') updateNavigation(); } catch (e) {}

        }

      } catch (e) {}



      /* consent records */

      P4.Legal.record('terms', 'Terms of Service accepted at onboarding');

      P4.Legal.record('privacy', 'Privacy Policy accepted at onboarding');

      P4.Legal.record('waiver', Ob.profile.minor

        ? 'Assumption of risk accepted (minor ' + age + ', guardian consent recorded)'

        : 'Liability waiver & assumption of risk accepted (age ' + age + ')');



      /* trial */

      P4.Paywall.ensureTrial();

      P4.Paywall.state && store.set(P4.keys.paywall, { onboard: true });



      /* celebration */

      P4.Motivation.confetti(140);

      P4.buzz([30, 60, 30]);

      Ob.renderDone();

    },



    age: function () {

      if (!Ob.profile.birth) return null;

      var b = new Date(Ob.profile.birth);

      if (isNaN(b.getTime())) return null;

      var now = new Date();

      var a = now.getFullYear() - b.getFullYear();

      var m = now.getMonth() - b.getMonth();

      if (m < 0 || (m === 0 && now.getDate() < b.getDate())) a--;

      return a;

    },



    close: function () {

      var host = document.getElementById('p4WizardHost');

      if (host) host.innerHTML = '';

      document.body.style.overflow = '';

    },



    /* ---------------- render ---------------- */

    render: function () {

      var host = document.getElementById('p4WizardHost');

      if (!host) return;

      document.body.style.overflow = 'hidden';

      var total = 5;

      var dots = '';

      for (var i = 1; i <= total; i++) {

        dots += '<span class="p4-wz-dot' + (i === Ob.step ? ' on' : (i < Ob.step ? ' done' : '')) + '"></span>';

      }

      var body = '';

      if (Ob.step === 1) body = Ob.s1();

      if (Ob.step === 2) body = Ob.s2();

      if (Ob.step === 3) body = Ob.s3();

      if (Ob.step === 4) body = Ob.s4();

      if (Ob.step === 5) body = Ob.s5();

      host.innerHTML =

        '<div class="p4-wz-backdrop"><div class="p4-wz">' +

        '<div class="p4-wz-dots">' + dots + '</div>' +

        '<div class="p4-wz-step-label">Step ' + Ob.step + ' of ' + total + '</div>' +

        body +

        '</div></div>';

      Ob.wire();

    },



    s1: function () {

      return '<h2>Welcome. Two minutes, once.</h2>' +

        '<p class="p4-wz-sub">This app counts your taps, picks your weights from muscle science, and talks at your reading level. Tell it who you are so it can look after you.</p>' +

        '<div class="p4-wz-field"><label>What should we call you?</label>' +

        '<input type="text" id="obName" maxlength="40" placeholder="First name or nickname" value="' + SEC.escapeHtml(Ob.profile.name) + '">' +

        '<div class="p4-wz-hint">Stays on this device. No account, no email, no server.</div></div>' +

        Ob.nav(true, false);

    },



    s2: function () {

      var age = Ob.age();

      var minor = age !== null && age < 18;

      var kid = age !== null && age < 13;

      return '<h2>When were you born?</h2>' +

        '<p class="p4-wz-sub">Your birth date keeps advice age-appropriate and makes the safety agreement valid. It never leaves this device.</p>' +

        '<div class="p4-wz-field"><label>Birth date</label>' +

        '<input type="date" id="obBirth" value="' + SEC.escapeHtml(Ob.profile.birth) + '"></div>' +

        '<div class="p4-wz-field"><label>Gender (optional)</label>' +

        '<select id="obGender">' +

        '<option value="unspecified"' + (Ob.profile.gender === 'unspecified' ? ' selected' : '') + '>Prefer not to say</option>' +

        '<option value="male"' + (Ob.profile.gender === 'male' ? ' selected' : '') + '>Male</option>' +

        '<option value="female"' + (Ob.profile.gender === 'female' ? ' selected' : '') + '>Female</option>' +

        '</select></div>' +

        (kid ? '<div class="p4-wz-err show"><i class="fas fa-user-shield"></i> Under 13: please set this up together with a parent or guardian. Their OK is required on the next screens.</div>' : '') +

        (minor && !kid ? '<div class="p4-wz-hint">Under 18: a guardian will need to approve the safety agreement.</div>' : '') +

        Ob.nav(age !== null, true);

    },



    s3: function () {

      var opts = P4.VocabLevels.map(function (L) {

        return '<div class="p4-vocab-opt' + (Ob.draftVocab === L.id ? ' on' : '') + '" data-lvl="' + L.id + '">' +

          '<div><div class="p4-vn">Level ' + L.id + ' — ' + L.name + '</div><div class="p4-vd">' + L.desc + '</div></div>' +

          '<i class="fas fa-check" style="color:var(--p4-accent);display:' + (Ob.draftVocab === L.id ? 'block' : 'none') + '"></i></div>';

      }).join('');

      var demo = P4_VOCAB_LADDERS['hypertrophy'][Ob.draftVocab - 1] || 'hypertrophy';

      return '<h2>How should the app talk to you?</h2>' +

        '<p class="p4-wz-sub">Pick your English level. Every label, tip and coach line adjusts — from super simple all the way to fancy.</p>' +

        '<div class="p4-vocab-demo">Example: “This move builds <b>' + SEC.escapeHtml(demo) + '</b>.”</div>' +

        '<div class="p4-vocab-list">' + opts + '</div>' +

        Ob.nav(true, false);

    },



    s4: function () {

      var modes =

        '<div class="p4-wz-mode">' +

        '<button class="p4-mode-btn' + (Ob.draftTheme.mode === 'dark' ? ' on' : '') + '" data-mode="dark"><i class="fas fa-moon"></i> Dark</button>' +

        '<button class="p4-mode-btn' + (Ob.draftTheme.mode === 'light' ? ' on' : '') + '" data-mode="light"><i class="fas fa-sun"></i> Light</button>' +

        '</div>';

      var sw = P4.Theme.accents.map(function (a) {

        return '<button class="p4-swatch' + (Ob.draftTheme.accent === a.id ? ' on' : '') + '" data-acc="' + a.id +

          '" style="background:' + a.hex + '" title="' + SEC.escapeHtml(a.name) + '" aria-label="' + SEC.escapeHtml(a.name) + '"></button>';

      }).join('');

      return '<h2>Pick your colors</h2>' +

        '<p class="p4-wz-sub">Dark or light, then any color you love. Change it any time in Settings — or say “More → Themes”.</p>' +

        modes + '<div class="p4-swatches">' + sw + '</div>' +

        Ob.nav(true, false);

    },



    s5: function () {

      var age = Ob.age();

      var minor = age !== null && age < 18;

      return '<h2>The serious part (short version)</h2>' +

        '<p class="p4-wz-sub">We keep it honest: this app gives fitness guidance, not medical advice.</p>' +

        '<label class="p4-wz-check"><input type="checkbox" id="obC1"><span>I am at least ' + (minor ? 'a minor using this with guardian approval' : '18 or older') +

        ' and I accept the <b data-legal="terms">Terms</b> and <b data-legal="privacy">Privacy Policy</b>. My data stays in this app on my device.</span></label>' +

        '<label class="p4-wz-check"><input type="checkbox" id="obC2"><span><b>Health check:</b> I feel OK to exercise, or a doctor has told me it is safe. I will stop if I feel pain, dizziness or shortness of breath, and I will talk to a professional about my health. This app is not medical care.</span></label>' +

        '<label class="p4-wz-check"><input type="checkbox" id="obC3"><span><b>Safety agreement:</b> I train at my own risk, start light, use good form, and I will not blame try4ever.com if I get hurt following general fitness guidance. <span style="color:var(--p4-text-3)">(Full text: <b data-legal="waiver">Liability Waiver</b>)</span></span></label>' +

        (minor ? '<label class="p4-wz-check"><input type="checkbox" id="obC4"><span><b>Guardian approval:</b> I am the parent/guardian (or I have their permission) and they accept these terms for me.</span></label>' : '') +

        Ob.nav(false, false);

    },



    nav: function (nextEnabled, allowSkip) {

      var back = Ob.step > 1 ? '<button class="btn" data-nav="back"><i class="fas fa-arrow-left"></i> Back</button>' : '';

      var skip = (allowSkip && Ob.step === 2) ? '<button class="btn" data-nav="skip" style="flex:0 0 auto">Skip for now</button>' : '';

      var next = Ob.step < 5

        ? '<button class="btn btn-primary" data-nav="next"' + (nextEnabled ? '' : ' disabled style="opacity:.5"') + '>Continue <i class="fas fa-arrow-right"></i></button>'

        : '<button class="btn btn-primary" data-nav="finish"><i class="fas fa-check"></i> I agree — let\'s go</button>';

      return '<div class="p4-wz-nav">' + back + skip + next + '</div>';

    },



    /* ---------------- wiring ---------------- */

    wire: function () {

      var host = document.getElementById('p4WizardHost');

      if (!host) return;

      var nameI = host.querySelector('#obName');

      if (nameI) nameI.addEventListener('input', function () { Ob.profile.name = nameI.value; Ob.persist(); });

      var birthI = host.querySelector('#obBirth');

      if (birthI) birthI.addEventListener('change', function () {

        Ob.profile.birth = birthI.value;

        Ob.persist();

        /* live-enable the Next button once the date is valid */

        var btn = host.querySelector('[data-nav="next"]');

        if (btn && Ob.age() !== null) { btn.disabled = false; btn.removeAttribute('style'); }

      });

      var genI = host.querySelector('#obGender');

      if (genI) genI.addEventListener('change', function () { Ob.profile.gender = genI.value; Ob.persist(); });



      host.querySelectorAll('.p4-vocab-opt').forEach(function (el) {

        el.addEventListener('click', function () {

          Ob.draftVocab = parseInt(el.getAttribute('data-lvl'), 10);

          try { P4.Vocab.set(Ob.draftVocab, true); } catch (e) {}

          /* tap-to-advance: pick a level → move straight to step 4 (colors) */

          Ob.step = Math.max(Ob.step, 4);

          Ob.render();

        });

      });

      host.querySelectorAll('.p4-mode-btn').forEach(function (el) {

        el.addEventListener('click', function () {

          Ob.draftTheme.mode = el.getAttribute('data-mode');

          document.documentElement.setAttribute('data-mode', Ob.draftTheme.mode);

          if (Ob.draftTheme.mode === 'dark') document.documentElement.setAttribute('data-theme', 'dark');

          else document.documentElement.removeAttribute('data-theme');

          Ob.render();

        });

      });

      host.querySelectorAll('.p4-swatch').forEach(function (el) {

        el.addEventListener('click', function () {

          Ob.draftTheme.accent = el.getAttribute('data-acc');

          document.documentElement.setAttribute('data-accent', Ob.draftTheme.accent);

          Ob.render();

        });

      });



      host.querySelectorAll('[data-nav]').forEach(function (el) {

        el.addEventListener('click', function (e) {

          e.preventDefault();

          var act = el.getAttribute('data-nav');

          if (act === 'back') { Ob.step = Math.max(1, Ob.step - 1); Ob.render(); return; }

          if (act === 'skip') { Ob.profile.birth = Ob.profile.birth || ''; Ob.step = 3; Ob.render(); return; }

          if (act === 'next') {

            if (Ob.step === 1 && !(Ob.profile.name || '').trim()) {

              P4.toast('A name helps us greet you — even a nickname works.', 'warn'); return;

            }

            if (Ob.step === 2 && Ob.age() === null) {

              P4.toast('Please enter your birth date — it keeps guidance safe and age-right.', 'warn'); return;

            }

            Ob.step++; Ob.persist(); Ob.render(); return;

          }

          if (act === 'finish') {

            var need = 3;

            var minor = Ob.profile.minor;

            if (minor) need = 4;

            var ok = 0;

            ['obC1', 'obC2', 'obC3', 'obC4'].forEach(function (id, i) {

              var c = host.querySelector('#' + id);

              if (c && c.checked) ok++;

            });

            if (ok < (minor ? 4 : 3)) {

              P4.toast('Please tick every box — each one protects you.', 'warn');

              return;

            }

            Ob.finish();

            return;

          }

        });

      });

      /* legal links inside wizard */

      host.querySelectorAll('[data-legal]').forEach(function (el) {

        el.addEventListener('click', function (e) {

          e.stopPropagation();

          P4.Legal.open(el.getAttribute('data-legal'));

        });

      });

    },



    renderDone: function () {

      var host = document.getElementById('p4WizardHost');

      if (!host) return;

      host.innerHTML =

        '<div class="p4-wz-backdrop"><div class="p4-wz" style="text-align:center">' +

        '<div style="font-size:54px;margin-bottom:10px">🎉</div>' +

        '<h2>You\'re set, ' + SEC.escapeHtml((Ob.profile.name || 'champ').split(' ')[0]) + '!</h2>' +

        '<p class="p4-wz-sub">Your 7-day trial of everything just started. From here, your first workout is <b>one tap</b> away.</p>' +

        '<button class="btn btn-primary" style="width:100%;min-height:56px;font-size:16px" onclick="P4.Onboard.close();startOrGenerateWorkout();">' +

        '<i class="fas fa-play"></i> Start my first workout</button>' +

        '<button class="btn" style="width:100%;margin-top:10px" onclick="P4.Onboard.close()">Look around first</button>' +

        '</div></div>';

    }

  };

})();



/* ============================================================================

 * P4 PAYWALL — $0 (license) vs $19/mo (subscription), 7-day trial,

 * template takeover, offline license validation (SHA-256 checksum),

 * optional worker mode (mirrors reference apps), rate limiting.

 * Escape hatch for the owner: or localStorage.p4_devbypass.

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;

  var DAY = 86400000;



  /* ---- tiny SHA-256 (public-domain style compact implementation, ASCII in) ---- */

  function sha256(ascii) {

    function rightRotate(value, amount) { return (value >>> amount) | (value << (32 - amount)); }

    var maxWord = Math.pow(2, 32);

    var result = '';

    var words = [], asciiBitLength = ascii.length * 8;

    var hash = sha256.h = sha256.h || [];

    var k = sha256.k = sha256.k || [];

    var primeCounter = k.length;

    var isComposite = {};

    for (var candidate = 2; primeCounter < 64; candidate++) {

      if (!isComposite[candidate]) {

        for (var i = 0; i < 313; i += candidate) { isComposite[i] = candidate; }

        hash[primeCounter] = (Math.pow(candidate, 0.5) * maxWord) | 0;

        k[primeCounter++] = (Math.pow(candidate, 1 / 3) * maxWord) | 0;

      }

    }

    ascii += '\x80';

    while (ascii.length % 64 - 56) ascii += '\x00';

    for (i = 0; i < ascii.length; i++) {

      var j = ascii.charCodeAt(i);

      if (j >> 8) return '';

      words[i >> 2] |= j << ((3 - i) % 4) * 8;

    }

    words[words.length] = ((asciiBitLength / maxWord) | 0);

    words[words.length] = (asciiBitLength | 0);

    for (j = 0; j < words.length;) {

      var w = words.slice(j, j += 16);

      var oldHash = hash;

      hash = hash.slice(0, 8);

      for (i = 0; i < 64; i++) {

        var w15 = w[i - 15], w2 = w[i - 2];

        var a = hash[0], e = hash[4];

        var temp1 = hash[7]

          + (rightRotate(e, 6) ^ rightRotate(e, 11) ^ rightRotate(e, 25))

          + ((e & hash[5]) ^ ((~e) & hash[6]))

          + k[i]

          + (w[i] = (i < 16) ? w[i] : (

            w[i - 16]

            + (rightRotate(w15, 7) ^ rightRotate(w15, 18) ^ (w15 >>> 3))

            + w[i - 7]

            + (rightRotate(w2, 17) ^ rightRotate(w2, 19) ^ (w2 >>> 10))

          ) | 0);

        var temp2 = (rightRotate(a, 2) ^ rightRotate(a, 13) ^ rightRotate(a, 22))

          + ((a & hash[1]) ^ (a & hash[2]) ^ (hash[1] & hash[2]));

        hash = [(temp1 + temp2) | 0].concat(hash);

        hash[4] = (hash[4] + temp1) | 0;

      }

      for (i = 0; i < 8; i++) hash[i] = (hash[i] + oldHash[i]) | 0;

    }

    for (i = 0; i < 8; i++) {

      for (j = 3; j + 1; j--) {

        var b = (hash[i] >> (j * 8)) & 255;

        result += ((b < 16) ? 0 : '') + b.toString(16);

      }

    }

    return result;

  }



  var ALPHABET = 'ABCDEFGHJKMNPQRSTUVWXYZ23456789';

  var PEPPER = 'try4ever::fitness::p4::vKq83mTrident';



  function digest5(seed) {

    var hex = sha256(PEPPER + seed);

    var out = '';

    for (var i = 0; i < 5; i++) {

      var byte = parseInt(hex.substr(i * 2, 2), 16);

      out += ALPHABET[byte % ALPHABET.length];

    }

    return out;

  }



  var Paywall = P4.Paywall = {

    /* test hooks (harmless in production, used by node unit tests) */

    _sha256: sha256,

    _digest5: digest5,

    config: {

      workerUrl: '',                                   /* optional: deployed worker base URL */

      paymentUrl: 'https://try4ever.com/fitness',      /* where the $19 plan is purchased     */

      price: 19,

      trialDays: 7,

      workoutThreshold: 21

    },



    /* ---------------- state ---------------- */

    state: function () { return store.get(P4.keys.paywall, { onboard: false }); },

    isPro: function () {

      if (Paywall.licenseValid()) return true;

      if (Paywall.subActive()) return true;

      if (Paywall.workoutThresholdMet()) return true;

      return false;

    },

    _devbypass: function () {

      /* permanently disabled — paywall enforced for all devices, including admin */

      return false;

    },

    licenseValid: function () {

      var lic = store.get(P4.keys.license, null);

      if (!lic || !lic.key) return false;

      return Paywall.validateKey(lic.key).ok;

    },

    validateKey: function (key) {

      var k = String(key || '').toUpperCase().replace(/[^A-Z0-9]/g, '');

      if (k.indexOf('FTNS') !== 0) return { ok: false, error: 'Key must start with FTNS.' };

      var body = k.slice(4);

      if (body.length !== 20) return { ok: false, error: 'Key must have 20 characters after FTNS.' };

      for (var i = 0; i < body.length; i++) {

        if (ALPHABET.indexOf(body[i]) === -1) return { ok: false, error: 'Character "' + body[i] + '" is not used in keys.' };

      }

      var type = body[0];

      if (type !== 'P') return { ok: false, error: 'Unknown key type.' };

      var seed = type + body.slice(1, 15);

      var want = digest5(seed);

      if (want !== body.slice(15)) return { ok: false, error: 'Checksum failed — check for typos.' };

      return { ok: true, type: type === 'P' ? 'pro' : 'free' };

    },



    subActive: function () {

      var exp = store.get(P4.keys.subExpires, 0);

      if (exp && Date.now() < exp) return true;

      var cache = store.get(P4.keys.subCache, null);

      if (cache && cache.active && cache.timestamp && (Date.now() - cache.timestamp < 3 * DAY) &&

          store.get(P4.keys.subExpires, 0)) return true;

      return false;

    },

    trialStart: function () { return store.get(P4.keys.trial, 0); },

    trialActive: function () {

      var t = Paywall.trialStart();

      return !!t && (Date.now() - t) < Paywall.config.trialDays * DAY;

    },

    daysLeft: function () {

      var t = Paywall.trialStart();

      if (!t) return Paywall.config.trialDays;

      return Math.max(0, Math.ceil((t + Paywall.config.trialDays * DAY - Date.now()) / DAY));

    },

    ensureTrial: function () {

      if (!Paywall.trialStart()) {

        store.set(P4.keys.trial, Date.now());

        P4.toast('7-day trial started — every Pro feature is open. Enjoy!', 'ok', 4200);

      }

    },

    isAllowed: function () {

      var st = Paywall.state();

      if (!st.onboard) return true;            /* pre-onboarding grace */

      if (Paywall.isPro()) return true;

      if (Paywall.trialActive()) return true;

      return false;

    },

    workoutThresholdMet: function () {

      try {

        var w = (typeof workoutData !== 'undefined' && workoutData.workouts) ? workoutData.workouts : [];

        return w.length >= Paywall.config.workoutThreshold;

      } catch (e) { return false; }

    },



    /* ---------------- actions ---------------- */

    activateLicense: function (key) {

      var rl = SEC.rateLimit('license', 5, 10 * 60 * 1000);

      if (!rl.allowed) {

        return { ok: false, error: 'Too many attempts. Try again in ' + rl.retryIn + ' seconds.' };

      }

      var v = Paywall.validateKey(key);

      if (!v.ok) return v;

      store.set(P4.keys.license, { key: String(key).toUpperCase(), at: Date.now(), type: v.type });

      return { ok: true };

    },

    deactivateLicense: function () { store.del(P4.keys.license); },

    markSubscription: function (days) {

      var d = days || 30;

      store.set(P4.keys.subCache, { active: true, key: 'local', timestamp: Date.now() });

      store.set(P4.keys.subExpires, Date.now() + d * DAY);

    },

    cancelSubscription: function () {

      var cfg = Paywall.config;

      if (cfg.workerUrl) {

        var uid = store.get('paywall_userId', null);

        if (uid) {

          fetch(cfg.workerUrl + '/api/cancel?userId=' + encodeURIComponent(uid), { method: 'POST' })

            .catch(function () {});

        }

      }

      store.del(P4.keys.subCache);

      store.del(P4.keys.subExpires);

      P4.toast('Subscription cancelled locally. Your data stays.', 'ok');

      Paywall.refreshUI();

    },



    /* ---------------- gate ---------------- */

    gate: function (featureName) {

      if (Paywall.isAllowed()) return true;

      Paywall.showFullPage(featureName);

      return false;

    },



    /* ---------------- UI: full takeover ---------------- */

    showFullPage: function (reason) {

      var host = document.getElementById('p4Paywall');

      var tpl = document.getElementById('p4PaywallTemplate');

      if (!host || !tpl) return;

      host.innerHTML = tpl.innerHTML;

      host.classList.add('open');

      document.body.style.overflow = 'hidden';

      Paywall.wire(host, reason);

    },

    close: function () {

      var host = document.getElementById('p4Paywall');

      if (host) { host.classList.remove('open'); host.innerHTML = ''; }

      document.body.style.overflow = '';

      Paywall.refreshUI();

    },



    /* small in-app upsell panel (used by Studio guards) */

    showPanel: function (msg) {

      P4.toast(msg + ' Opening upgrade options…', 'info', 2600);

      setTimeout(function () { Paywall.showFullPage(msg); }, 900);

    },



    wire: function (host, reason) {

      /* trial pill */

      var pill = host.querySelector('#pwTrialPill');

      if (pill) {

        if (Paywall.trialActive()) {

          pill.innerHTML = '<i class="fas fa-hourglass-half"></i> Trial: ' + Paywall.daysLeft() + ' day' + (Paywall.daysLeft() === 1 ? '' : 's') + ' left';

        } else if (Paywall.isPro()) {

          pill.innerHTML = '<i class="fas fa-crown"></i> Pro active — thank you!';

        } else {

          pill.innerHTML = '<i class="fas fa-lock"></i> Trial ended — pick a plan to keep generating workouts';

        }

      }

      /* license box */

      var applyBtn = host.querySelector('#pwFreeApplyBtn');

      var box = host.querySelector('#pwLicenseBox');

      if (applyBtn && box) {

        applyBtn.addEventListener('click', function () {

          box.style.display = box.style.display === 'none' ? 'block' : 'none';

        });

      }

      var go = host.querySelector('#pwLicenseGo');

      var input = host.querySelector('#pwLicenseInput');

      var note = host.querySelector('#pwLicenseNote');

      if (go && input) {

        go.addEventListener('click', function () {

          var r = Paywall.activateLicense(input.value);

          if (r.ok) {

            P4.toast('License activated — welcome to Pro!', 'ok');

            Paywall.close();

          } else {

            if (note) note.textContent = r.error;

            if (input) { input.classList.add('p4-json-area'); input.style.borderColor = 'var(--p4-bad)'; }

            P4.buzz(60);

          }

        });

        input.addEventListener('keydown', function (e) { if (e.key === 'Enter') go.click(); });

      }

      /* subscribe flow */

      var sub = host.querySelector('#pwSubscribeBtn');

      var bd = host.querySelector('#pwBreakdown');

      var consent = host.querySelector('#pwRenewConsent');

      if (sub && bd) {

        sub.addEventListener('click', function () {

          bd.classList.toggle('show');

          sub.textContent = bd.classList.contains('show') ? 'Confirm subscription' : 'Subscribe — $19/mo';

        });

        if (consent) {

          consent.addEventListener('change', function () { sub.disabled = !consent.checked; sub.style.opacity = consent.checked ? 1 : .55; });

          sub.disabled = true; sub.style.opacity = .55;

        }

        sub.addEventListener('click', function () {

          if (!consent.checked) return;

          Paywall.checkout();

        });

      }

      /* legal delegation inside paywall */

      host.querySelectorAll('[data-legal]').forEach(function (el) {

        el.addEventListener('click', function () { P4.Legal.open(el.getAttribute('data-legal')); });

      });

      var closeNote = host.querySelector('.pw-gate-note span');

      if (closeNote) closeNote.addEventListener('click', function () { Paywall.close(); });

      if (reason) console.log('[P4] paywall shown for:', reason);

    },



    checkout: function () {

      var cfg = Paywall.config;

      if (cfg.workerUrl) {

        /* mirror reference apps: worker creates PaymentIntent, redirect if required */

        var uid = store.get('paywall_userId', null) || (crypto.randomUUID ? crypto.randomUUID() : 'u' + Date.now());

        store.set('paywall_userId', uid);

        fetch(cfg.workerUrl + '/api/create-payment-intent', {

          method: 'POST', headers: { 'Content-Type': 'application/json' },

          body: JSON.stringify({ userId: uid, amount: cfg.price * 100, currency: 'usd' })

        }).then(function (r) { return r.json(); }).then(function (data) {

          if (data && data.url) { location.href = data.url; return; }

          if (data && data.ok) { Paywall.markSubscription(30); Paywall.close(); }

        }).catch(function () { P4.toast('Payment service unreachable — try the license path below.', 'warn'); });

        return;

      }

      /* no worker configured: honest manual flow */

      P4.copyText('FTNS-SUB-REQUEST ' + Paywall._deviceId()).then(function () {

        P4.toast('Subscription request copied. Complete payment at ' + cfg.paymentUrl + ' — your key arrives by email, then paste it in the Free box.', 'info', 8000);

      });

      window.open(cfg.paymentUrl, '_blank');

    },

    _deviceId: function () {

      var id = store.get('p4_device_id', null);

      if (!id) {

        id = 'DEV-' + Math.random().toString(36).slice(2, 8).toUpperCase() + '-' + Date.now().toString(36).toUpperCase();

        store.set('p4_device_id', id);

      }

      return id;

    },



    /* ---------------- enforcement wrappers ---------------- */

    enforce: function () {

      if (typeof window.startOrGenerateWorkout === 'function' && !window.startOrGenerateWorkout.__p4gated) {

        var orig = window.startOrGenerateWorkout;

        window.startOrGenerateWorkout = function () {

          if (!Paywall.gate('workout generation')) return;

          return orig.apply(this, arguments);

        };

        window.startOrGenerateWorkout.__p4gated = true;

      }

    },



    refreshUI: function () {

      if (P4.SettingsUI) P4.SettingsUI.syncPlanCard();

    },



    init: function () {

      if (!SEC.integrityOk()) { console.warn('[P4] integrity canary failed — continuing.'); }

      Paywall.enforce();

      /* weekly-ish sub revalidation when a worker is configured */

      if (Paywall.config.workerUrl && Paywall.subActive()) {

        var uid = store.get('paywall_userId', null);

        if (uid) {

          fetch(Paywall.config.workerUrl + '/api/status?userId=' + encodeURIComponent(uid))

            .then(function (r) { return r.json(); })

            .then(function (s) { if (s && s.active === false) Paywall.cancelSubscription(); })

            .catch(function () {});

        }

      }

    }

  };

})();



/* ============================================================================

 * P4 LIBRARY STUDIO — add manually, export, import+merge, AI-prompt wizard,

 * feedback wizard. Custom exercises persist in p4_custom_ex and re-inject.

 * Manual add is FREE. Import + AI prompt are PRO (paywall-gated).

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;

  var CUST_KEY = 'p4_custom_ex';



  var Studio = P4.Studio = {

    lib: function () { return (typeof ultimateExerciseLibrary !== 'undefined') ? ultimateExerciseLibrary : null; },



    groups: function () {

      var L = Studio.lib();

      return L ? Object.keys(L) : [];

    },



    registry: function () {

      var L = Studio.lib(), groups = {}, muscles = {};

      if (!L) return { groups: groups, muscles: muscles };

      for (var g in L) {

        groups[g] = 1;

        var arr = L[g];

        if (!Array.isArray(arr)) continue;

        for (var i = 0; i < arr.length; i++) {

          var m = arr[i] && arr[i].muscles;

          if (Array.isArray(m)) for (var j = 0; j < m.length; j++) muscles[m[j]] = 1;

        }

      }

      return { groups: groups, muscles: muscles };

    },



    allNames: function () {

      var L = Studio.lib(), out = [];

      if (!L) return out;

      for (var g in L) {

        if (!Array.isArray(L[g])) continue;

        for (var i = 0; i < L[g].length; i++) {

          if (L[g][i] && L[g][i].name) out.push({ name: L[g][i].name, group: g });

        }

      }

      return out;

    },



    /* ---------------- persistence ---------------- */

    persistCustom: function (ex) {

      var list = store.get(CUST_KEY, []);

      list.push(ex);

      store.set(CUST_KEY, list);

    },

    reinjectCustom: function () {

      var list = store.get(CUST_KEY, []);

      var L = Studio.lib();

      if (!L || !list.length) return;

      var added = 0;

      list.forEach(function (ex) {

        if (!ex || !ex.name || !ex.group || !L[ex.group]) return;

        var arr = L[ex.group];

        for (var i = 0; i < arr.length; i++) if (arr[i] && arr[i].name === ex.name) return;

        arr.push(ex); added++;

      });

      if (added) console.log('[P4] re-injected ' + added + ' custom exercises');

    },



    inject: function (ex) {

      var L = Studio.lib();

      if (!L || !L[ex.group]) return false;

      var arr = L[ex.group];

      for (var i = 0; i < arr.length; i++) {

        if (arr[i] && arr[i].name && arr[i].name.toLowerCase() === ex.name.toLowerCase()) {

          arr[i] = ex; /* update-in-place */

          return true;

        }

      }

      arr.push(ex);

      return true;

    },



    /* ---------------- add (FREE) ---------------- */

    addManual: function (form) {

      var reg = Studio.registry();

      var payload = [{

        name: form.name,

        group: form.group,

        muscles: form.muscles,

        equipment: form.equipment,

        difficulty: form.difficulty,

        type: form.type,

        steps: form.steps ? form.steps.split('\n').filter(Boolean) : [],

        cues: form.cues ? form.cues.split('\n').filter(Boolean) : [],

        mistakes: form.mistakes ? form.mistakes.split('\n').filter(Boolean) : [],

        breathing: form.breathing || '',

        tempo: form.tempo || '',

        imageUrl: form.imageUrl || ''

      }];

      var v = SEC.validateExercisePayload(payload, reg);

      if (!v.valid.length) {

        return { ok: false, error: v.invalid[0] ? v.invalid[0].reason : 'Invalid exercise' };

      }

      var ex = v.valid[0];

      ex.custom = true;

      Studio.inject(ex);

      Studio.persistCustom(ex);

      P4.Badges && P4.Badges.countEvent('studioAdd');

      if (typeof renderLibrary === 'function') { try { renderLibrary(); } catch (e) {} }

      return { ok: true, exercise: ex };

    },



    /* ---------------- import + merge (PRO) ---------------- */

    importMerge: function (rawText) {

      var reg = Studio.registry();

      var heal = SEC.healJson(rawText);

      if (!heal.ok) return { ok: false, error: heal.error, report: [] };

      var v = SEC.validateExercisePayload(heal.data, reg);

      var report = [], added = 0, updated = 0;

      var L = Studio.lib();

      v.valid.forEach(function (ex) {

        ex.custom = true;

        var existed = false;

        if (L[ex.group]) {

          for (var i = 0; i < L[ex.group].length; i++) {

            if (L[ex.group][i] && L[ex.group][i].name && L[ex.group][i].name.toLowerCase() === ex.name.toLowerCase()) existed = true;

          }

        }

        Studio.inject(ex);

        /* persist merge result */

        var list = store.get(CUST_KEY, []);

        var idx = -1;

        for (var k = 0; k < list.length; k++) if (list[k].name.toLowerCase() === ex.name.toLowerCase()) idx = k;

        if (idx >= 0) list[idx] = ex; else list.push(ex);

        store.set(CUST_KEY, list);

        if (existed) { updated++; report.push({ kind: 'upd', text: ex.name + ' — updated in ' + ex.group }); }

        else { added++; report.push({ kind: 'add', text: ex.name + ' — added to ' + ex.group }); }

      });

      v.invalid.forEach(function (bad) {

        report.push({ kind: 'bad', text: bad.name + ' — ' + bad.reason });

      });

      if (added + updated) {

        P4.Badges && P4.Badges.countEvent('import');

        if (typeof renderLibrary === 'function') { try { renderLibrary(); } catch (e) {} }

      }

      return { ok: added + updated > 0, added: added, updated: updated, report: report };

    },



    /* ---------------- AI prompt (PRO) ---------------- */

    buildPrompt: function (wantText) {

      var reg = Studio.registry();

      var groups = Object.keys(reg.groups).join(', ');

      var muscles = Object.keys(reg.muscles).sort().join(', ');

      var names = Studio.allNames();

      var sample = [];

      for (var i = 0; i < 40 && i < names.length; i++) sample.push(names[Math.floor(Math.random() * names.length)].name);

      var want = SEC.clampStr(wantText || 'New exercises you think are missing.', 1200);

      return [

        'You are a strength-and-conditioning curriculum writer.',

        'Return ONLY a JSON array. No markdown fences, no commentary.',

        '',

        'SCHEMA per item:',

        '{',

        '  "name": string (unique, 2-90 chars),',

        '  "group": one of [' + groups + '],',

        '  "muscles": array from [' + muscles + '],',

        '  "equipment": string,',

        '  "difficulty": "beginner" | "intermediate" | "advanced",',

        '  "type": "reps" | "time",',

        '  "steps": array of 3-6 short strings,',

        '  "cues": array of 2-4 short coaching strings,',

        '  "mistakes": array of 1-3 short strings,',

        '  "breathing": string (one sentence),',

        '  "tempo": string like "2-1-2",',

        '  "imageUrl": "" (leave empty)',

        '}',

        '',

        'RULES:',

        '1. 8-12 items. No duplicates of the existing names listed below.',

        '2. Use ONLY the group and muscle keys given above.',

        '3. Steps are imperative, <= 20 words each.',

        '4. Inclusive, equipment-flexible, joint-friendly.',

        '',

        'EXISTING NAMES (do not repeat): ' + sample.join('; ') + '.',

        '',

        'MY REQUEST: ' + want

      ].join('\n');

    },



    /* ---------------- feedback (FREE) ---------------- */

    saveFeedback: function (entry) {

      var list = store.get(P4.keys.feedback, []);

      entry.at = Date.now();

      list.unshift(entry);

      store.set(P4.keys.feedback, list.slice(0, 100));

      P4.toast('Feedback saved. Export it any time from the Studio.', 'ok');

    },

    exportFeedback: function () {

      var list = store.get(P4.keys.feedback, []);

      P4.download('FitnessFeedback_' + P4.todayKey() + '.json', JSON.stringify(list, null, 2), 'application/json');

    },



    /* ---------------- recent JSON library (cap 5, dedupe) ---------------- */

    rememberJson: function (text) {

      var list = store.get(P4.keys.studioRecent, []);

      if (list.indexOf(text) >= 0) return;

      list.unshift(text);

      store.set(P4.keys.studioRecent, list.slice(0, 5));

    },

    recent: function () { return store.get(P4.keys.studioRecent, []); },



    /* ---------------- UI ---------------- */

    open: function (view) {

      var host = document.getElementById('p4Studio');

      if (!host) { P4.toast('Studio is still loading — try again in a second.', 'warn'); return; }

      host.style.display = 'flex';

      host.classList.add('open');

      Studio.render(view || 'menu');

    },

    close: function () {

      var host = document.getElementById('p4Studio');

      if (host) { host.style.display = 'none'; host.classList.remove('open'); }

    },

    render: function (view) {

      var host = document.getElementById('p4StudioBody');

      if (!host) return;

      if (view === 'menu') return Studio.renderMenu(host);

      if (view === 'add') return Studio.renderAdd(host);

      if (view === 'import') return Studio.renderImport(host);

      if (view === 'prompt') return Studio.renderPrompt(host);

      if (view === 'feedback') return Studio.renderFeedback(host);

    },



    shell: function (title, sub, backTo, body) {

      var back = backTo ? '<button class="btn" style="flex:0 0 auto" onclick="P4.Studio.render(\'' + backTo + '\')"><i class="fas fa-arrow-left"></i> Back</button>' : '';

      return '<div class="p4-wz-step-label">Library Studio</div><h2 style="text-align:left">' + SEC.escapeHtml(title) + '</h2>' +

        '<p class="p4-wz-sub" style="text-align:left">' + sub + '</p>' + body +

        '<div class="p4-wz-nav">' + back +

        '<button class="btn" onclick="P4.Studio.close()" style="flex:1"><i class="fas fa-times"></i> Close</button></div>';

    },



    renderMenu: function (host) {

      var counts = Studio.allNames().length;

      var custom = store.get(CUST_KEY, []).length;

      host.innerHTML = Studio.shell('Library Studio', counts + ' exercises ready. Yours added: ' + custom + '. Grow it your way.', null,

        '<div class="p4-studio-grid">' +

        '<button class="p4-studio-tile" onclick="P4.Studio.render(\'add\')"><i class="fas fa-plus"></i><b>Add exercise</b><span>Type one in by hand. Always free.</span></button>' +

        '<button class="p4-studio-tile" onclick="P4.Backup.exportLibrary()"><i class="fas fa-file-export"></i><b>Export library</b><span>Download the whole list as clean JSON.</span></button>' +

        '<button class="p4-studio-tile ' + (P4.Paywall && P4.Paywall.isPro() ? '' : 'locked') + '" onclick="P4.Studio.guardImport()"><i class="fas fa-file-import"></i><b>Import &amp; merge</b><span>Paste JSON, get a safe merge report.</span>' + (P4.Paywall && P4.Paywall.isPro() ? '' : '<span class="p4-lock"><i class="fas fa-lock"></i> Pro</span>') + '</button>' +

        '<button class="p4-studio-tile ' + (P4.Paywall && P4.Paywall.isPro() ? '' : 'locked') + '" onclick="P4.Studio.guardPrompt()"><i class="fas fa-robot"></i><b>AI helper</b><span>Copy a ready prompt, paste the JSON back.</span>' + (P4.Paywall && P4.Paywall.isPro() ? '' : '<span class="p4-lock"><i class="fas fa-lock"></i> Pro</span>') + '</button>' +

        '<button class="p4-studio-tile" onclick="P4.Studio.render(\'feedback\')"><i class="fas fa-comment-dots"></i><b>Feedback</b><span>Flag an exercise to fix or remove.</span></button>' +

        '<button class="p4-studio-tile" onclick="P4.Studio.exportFeedback()"><i class="fas fa-inbox"></i><b>My feedback</b><span>' + store.get(P4.keys.feedback, []).length + ' items saved.</span></button>' +

        '</div>');

    },



    guardImport: function () {

      if (P4.Paywall && !P4.Paywall.isPro()) return P4.Paywall.showPanel('Import & merge is a Pro feature.');

      Studio.render('import');

    },

    guardPrompt: function () {

      if (P4.Paywall && !P4.Paywall.isPro()) return P4.Paywall.showPanel('The AI helper is a Pro feature.');

      Studio.render('prompt');

    },



    renderAdd: function (host) {

      var groups = Studio.groups();

      var opts = groups.map(function (g) { return '<option value="' + g + '">' + g.replace(/_/g, ' ') + '</option>'; }).join('');

      host.innerHTML = Studio.shell('Add an exercise', 'Good form starts with a clear name and honest muscles.', 'menu',

        '<div class="p4-wz-field"><label>Exercise name *</label><input type="text" id="p4AddName" maxlength="90" placeholder="e.g. Slow-tempo split squat"></div>' +

        '<div class="p4-wz-field"><label>Group *</label><select id="p4AddGroup">' + opts + '</select></div>' +

        '<div class="p4-wz-field"><label>Muscles (pick main ones) *</label><select id="p4AddMuscles" multiple size="6" style="min-height:120px"></select>' +

        '<div class="p4-wz-hint">Hold Ctrl / Cmd to pick several.</div></div>' +

        '<div class="p4-wz-field"><label>Equipment</label><input type="text" id="p4AddEquip" placeholder="bodyweight, dumbbell, barbell…"></div>' +

        '<div class="p4-wz-field"><label>Level</label><select id="p4AddDiff"><option value="beginner">Beginner</option><option value="intermediate" selected>Intermediate</option><option value="advanced">Advanced</option></select></div>' +

        '<div class="p4-wz-field"><label>Counted by</label><select id="p4AddType"><option value="reps">Reps</option><option value="time">Time</option></select></div>' +

        '<div class="p4-wz-field"><label>Steps (one per line)</label><textarea id="p4AddSteps" rows="3"></textarea></div>' +

        '<div class="p4-wz-field"><label>Coaching cues (one per line)</label><textarea id="p4AddCues" rows="2"></textarea></div>' +

        '<div class="p4-wz-field"><label>Image URL (https only, optional)</label><input type="url" id="p4AddImg" placeholder="https://…"></div>' +

        '<div class="p4-wz-err" id="p4AddErr"></div>' +

        '<button class="btn btn-primary" style="width:100%;min-height:50px" onclick="P4.Studio.submitAdd()"><i class="fas fa-plus"></i> Add to Library</button>');

      var msel = document.getElementById('p4AddMuscles');

      var mus = Object.keys(Studio.registry().muscles).sort();

      msel.innerHTML = mus.map(function (m) { return '<option value="' + m + '">' + m.replace(/_/g, ' ') + '</option>'; }).join('');

    },



    submitAdd: function () {

      var err = document.getElementById('p4AddErr');

      err.classList.remove('show');

      var sel = document.getElementById('p4AddMuscles');

      var muscles = Array.prototype.slice.call(sel.selectedOptions).map(function (o) { return o.value; });

      var res = Studio.addManual({

        name: document.getElementById('p4AddName').value,

        group: document.getElementById('p4AddGroup').value,

        muscles: muscles,

        equipment: document.getElementById('p4AddEquip').value || 'bodyweight',

        difficulty: document.getElementById('p4AddDiff').value,

        type: document.getElementById('p4AddType').value,

        steps: document.getElementById('p4AddSteps').value,

        cues: document.getElementById('p4AddCues').value,

        mistakes: '',

        breathing: '',

        tempo: '',

        imageUrl: document.getElementById('p4AddImg').value

      });

      if (!res.ok) {

        err.textContent = res.error;

        err.classList.add('show');

        return;

      }

      P4.toast('"' + res.exercise.name + '" added to ' + res.exercise.group + '.', 'ok');

      Studio.render('menu');

    },



    renderImport: function (host) {

      var recents = Studio.recent();

      var recentHtml = recents.length ?

        '<div class="p4-wz-field"><label>Recent pastes</label>' +

        recents.map(function (r, i) {

          return '<div class="p4-recent-row" onclick="P4.Studio.loadRecent(' + i + ')"><span>' + SEC.escapeHtml(r.slice(0, 60)) + '…</span><i class="fas fa-arrow-left"></i></div>';

        }).join('') + '</div>' : '';

      host.innerHTML = Studio.shell('Import & merge', 'Paste a JSON array of exercises. Nothing overwrites without a matching name — same name means update.', 'menu',

        '<textarea id="p4ImpArea" class="p4-json-area" placeholder=\'[ { "name": "…", "group": "chest", "muscles": ["chest"], … } ]\'></textarea>' +

        '<div style="display:flex;gap:8px;margin-top:10px;flex-wrap:wrap">' +

        '<button class="btn btn-primary" onclick="P4.Studio.submitImport()"><i class="fas fa-check"></i> Validate &amp; Merge</button>' +

        '<button class="btn" onclick="P4.Studio.pickImportFile()"><i class="fas fa-folder-open"></i> From file…</button>' +

        '</div>' + recentHtml +

        '<div class="p4-merge-report" id="p4ImpReport"></div>');

      var area = document.getElementById('p4ImpArea');

      area.addEventListener('input', P4.debounce(function () {

        var heal = SEC.healJson(area.value);

        area.classList.remove('ok', 'bad');

        if (!area.value.trim()) return;

        area.classList.add(heal.ok ? 'ok' : 'bad');

      }, 300));

    },

    loadRecent: function (i) {

      var r = Studio.recent()[i];

      if (!r) return;

      var a = document.getElementById('p4ImpArea');

      if (a) { a.value = r; a.dispatchEvent(new Event('input')); }

    },

    pickImportFile: function () {

      var input = document.createElement('input');

      input.type = 'file'; input.accept = 'application/json,.json';

      input.onchange = function () {

        var f = input.files && input.files[0];

        if (!f) return;

        var r = new FileReader();

        r.onload = function (ev) {

          var a = document.getElementById('p4ImpArea');

          if (a) { a.value = String(ev.target.result || ''); a.dispatchEvent(new Event('input')); }

        };

        r.readAsText(f);

      };

      input.click();

    },

    submitImport: function () {

      var area = document.getElementById('p4ImpArea');

      var rep = document.getElementById('p4ImpReport');

      if (!area || !rep) return;

      var text = area.value;

      if (!text.trim()) { P4.toast('Paste some JSON first.', 'warn'); return; }

      Studio.rememberJson(text);

      var res = Studio.importMerge(text);

      var html = '';

      if (res.error) html += '<div class="p4-mr-row"><i class="fas fa-circle-xmark bad"></i>' + SEC.escapeHtml(res.error) + '</div>';

      html += '<div class="p4-mr-row"><i class="fas fa-circle-info info"></i>Added ' + (res.added || 0) + ' · Updated ' + (res.updated || 0) + ' · Rejected ' + ((res.report || []).filter(function (r) { return r.kind === 'bad'; }).length) + '</div>';

      (res.report || []).slice(0, 40).forEach(function (r) {

        var ic = r.kind === 'add' ? 'fa-circle-plus add' : r.kind === 'upd' ? 'fa-arrows-rotate upd' : 'fa-triangle-exclamation bad';

        html += '<div class="p4-mr-row"><i class="fas ' + ic + '"></i>' + SEC.escapeHtml(r.text) + '</div>';

      });

      rep.innerHTML = html;

      if (res.added + res.updated > 0) P4.toast('Library grew: +' + res.added + ' new, ' + res.updated + ' updated.', 'ok');

    },



    renderPrompt: function (host) {

      host.innerHTML = Studio.shell('AI helper', 'Copy this prompt into any AI chat, then paste its JSON back into Import &amp; merge.', 'menu',

        '<div class="p4-wz-field"><label>What do you want to add or change?</label>' +

        '<textarea id="p4PromptWant" rows="3" placeholder="e.g. More chest moves I can do with just resistance bands and a door anchor"></textarea></div>' +

        '<button class="btn btn-primary" style="width:100%" onclick="P4.Studio.makePrompt()"><i class="fas fa-wand-magic-sparkles"></i> Build my prompt</button>' +

        '<div id="p4PromptOut" style="margin-top:12px"></div>');

    },

    makePrompt: function () {

      var want = document.getElementById('p4PromptWant').value || '';

      var p = Studio.buildPrompt(want);

      var out = document.getElementById('p4PromptOut');

      out.innerHTML = '<div class="p4-prompt-box" id="p4PromptBox"></div>' +

        '<button class="btn btn-primary" style="width:100%;margin-top:10px" onclick="P4.Studio.copyPrompt()"><i class="fas fa-copy"></i> Copy prompt</button>' +

        '<div class="p4-wz-hint">Tip: ask the AI for “only JSON, no markdown”. If it adds backticks anyway, Import &amp; merge strips them automatically.</div>';

      document.getElementById('p4PromptBox').textContent = p;

    },

    copyPrompt: function () {

      var box = document.getElementById('p4PromptBox');

      if (!box) return;

      P4.copyText(box.textContent).then(function (ok) {

        P4.toast(ok ? 'Prompt copied — paste it into your AI chat.' : 'Copy failed — select the text manually.', ok ? 'ok' : 'warn');

      });

    },



    renderFeedback: function (host) {

      var names = Studio.allNames().map(function (n) { return n.name; }).sort();

      host.innerHTML = Studio.shell('Feedback', 'Tell us what to fix, add, or remove. Saved on your device, export any time.', 'menu',

        '<div class="p4-wz-field"><label>Kind</label><select id="p4FbKind">' +

        '<option value="fix">Fix an exercise</option><option value="remove">Remove an exercise</option>' +

        '<option value="idea">Add an idea</option><option value="other">Something else</option></select></div>' +

        '<div class="p4-wz-field"><label>Search exercise (for fix/remove)</label>' +

        '<div class="p4-combo"><input type="text" id="p4FbTarget" placeholder="Type to search…" autocomplete="off">' +

        '<div class="p4-combo-list" id="p4FbList"></div></div></div>' +

        '<div class="p4-wz-field"><label>What happened / what do you want?</label>' +

        '<textarea id="p4FbNote" rows="3" maxlength="500"></textarea></div>' +

        '<button class="btn btn-primary" style="width:100%" onclick="P4.Studio.submitFeedback()"><i class="fas fa-paper-plane"></i> Save feedback</button>' +

        '<div class="p4-wz-hint">Feedback stays in this file until you export it. Nothing is sent anywhere.</div>');

      Studio.wireCombo(names);

    },

    wireCombo: function (names) {

      var input = document.getElementById('p4FbTarget');

      var list = document.getElementById('p4FbList');

      if (!input || !list) return;

      input.addEventListener('input', function () {

        var q = input.value.toLowerCase();

        if (!q) { list.classList.remove('open'); return; }

        var hits = names.filter(function (n) { return n.toLowerCase().indexOf(q) !== -1; }).slice(0, 12);

        list.innerHTML = hits.map(function (n) {

          return '<div class="p4-combo-item" data-v="' + SEC.escapeHtml(n) + '">' + SEC.escapeHtml(n) + '</div>';

        }).join('') || '<div class="p4-combo-item">No match</div>';

        list.classList.add('open');

      });

      list.addEventListener('click', function (e) {

        var it = e.target.closest('.p4-combo-item');

        if (!it || !it.getAttribute('data-v')) return;

        input.value = it.getAttribute('data-v');

        list.classList.remove('open');

      });

      input.addEventListener('blur', function () { setTimeout(function () { list.classList.remove('open'); }, 200); });

    },

    submitFeedback: function () {

      Studio.saveFeedback({

        kind: document.getElementById('p4FbKind').value,

        target: SEC.clampStr(document.getElementById('p4FbTarget').value, 90),

        note: SEC.clampStr(document.getElementById('p4FbNote').value, 500)

      });

      Studio.render('menu');

    },



    init: function () { Studio.reinjectCustom(); }

  };

})();



/* ============================================================================

 * P4 I18N — language selection, Google-Translate compatibility, phrasebook

 * export/import (community translations merge into the vocab ladders).

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  var LANGS = [

    { code: 'auto', name: 'Auto (browser)' },

    { code: 'en', name: 'English' },

    { code: 'es', name: 'Español' },

    { code: 'fr', name: 'Français' },

    { code: 'de', name: 'Deutsch' },

    { code: 'pt', name: 'Português' },

    { code: 'it', name: 'Italiano' },

    { code: 'tr', name: 'Türkçe' },

    { code: 'ru', name: 'Русский' },

    { code: 'ar', name: 'العربية' },

    { code: 'hi', name: 'हिन्दी' },

    { code: 'zh-CN', name: '中文（简体）' },

    { code: 'ja', name: '日本語' },

    { code: 'ko', name: '한국어' }

  ];

  P4.LANGS = LANGS;



  var I18N = P4.I18N = {

    get: function () { return store.get(P4.keys.lang, 'auto'); },

    set: function (code) {

      store.set(P4.keys.lang, code);

      I18N.apply();

      if (P4.vocabUI) P4.vocabUI.sync();

      P4.toast('Language set to ' + I18N.name(code) + '.', 'ok', 2000);

    },

    name: function (code) {

      for (var i = 0; i < LANGS.length; i++) if (LANGS[i].code === code) return LANGS[i].name;

      return code;

    },

    apply: function () {

      var code = I18N.get();

      if (code === 'auto') {

        code = (navigator.language || 'en');

        if (code.length > 5) code = code.slice(0, 5);

      }

      document.documentElement.setAttribute('lang', code);

      document.documentElement.setAttribute('dir', /^ar|^he|^fa/.test(code) ? 'rtl' : 'ltr');

    },



    /* Google-Translate hardening: protect brand + user content from mangling */

    protectNodes: function () {

      try {

        document.querySelectorAll('.nav-brand, #navBrand, .pw-hero h1').forEach(function (el) {

          el.setAttribute('translate', 'no');

          el.classList.add('notranslate');

        });

      } catch (e) {}

    },



    /* phrasebook: everything translatable, in one JSON */

    exportPhrasebook: function () {

      var book = {

        kind: 'try4ever-fitness-phrasebook',

        about: 'Translate any value, keep the 10-level structure. L5 is the default app voice.',

        note: 'Return the same shape. Keys stay in English.',

        levels: { 1: 'super simple', 2: 'easy', 3: 'everyday', 4: 'clear', 5: 'standard', 6: 'sharp', 7: 'advanced', 8: 'technical', 9: 'pro coaching', 10: 'grandiloquent' },

        entries: P4_VOCAB_LADDERS

      };

      P4.download('FitnessPhrasebook_' + P4.todayKey() + '.json', JSON.stringify(book, null, 1));

    },



    importPhrasebook: function (text) {

      var heal = SEC.healJson(text);

      if (!heal.ok) { P4.toast('Not valid JSON: ' + heal.error, 'bad'); return false; }

      var data = heal.data;

      var entries = data && data.entries ? data.entries : (data && !data.kind ? data : null);

      if (!entries || typeof entries !== 'object') { P4.toast('No entries object found.', 'bad'); return false; }

      var count = 0;

      for (var k in entries) {

        var v = entries[k];

        if (!Array.isArray(v) || v.length !== 10) continue;

        P4_VOCAB_LADDERS[k] = v.map(function (x) { return SEC.clampStr(x, 120); });

        count++;

      }

      store.set(P4.keys.phrases, entries);

      if (P4.Vocab) P4.Vocab.buildKeyIndex();

      P4.toast(count + ' phrases updated. Re-applying words…', 'ok');

      if (P4.Vocab) P4.Vocab.applyAll();

      return true;

    },

    pickPhrasebook: function () {

      var input = document.createElement('input');

      input.type = 'file'; input.accept = 'application/json,.json';

      input.onchange = function () {

        var f = input.files && input.files[0];

        if (!f) return;

        var r = new FileReader();

        r.onload = function (ev) { I18N.importPhrasebook(String(ev.target.result || '')); };

        r.readAsText(f);

      };

      input.click();

    },



    init: function () {

      I18N.apply();

      setTimeout(I18N.protectNodes, 800);

    }

  };

})();



/* ============================================================================

 * P4 BOOT — domain gate, init sequence, Settings injection (Appearance,

 * Words, Language, Power, Plan, Legal & Data), theme/vocab UI sync.

 * ============================================================================ */

(function () {

  'use strict';

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  /* ---------- 0. domain gate (first thing, mirrors reference apps) ---------- */

  if (!SEC.domainAllowed()) {

    document.documentElement.innerHTML =

      '<body style="font-family:sans-serif;background:#f6f6f6;display:flex;align-items:center;justify-content:center;height:100vh;margin:0">' +

      '<div style="text-align:center"><h1 style="color:#333">404</h1><p style="color:#777">Page not found.</p></div></body>';

    return;

  }



  /* ---------- theme picker UI (settings + wizard reuse) ---------- */

  P4.themeUI = {

    renderInto: function (hostId) {

      var host = document.getElementById(hostId);

      if (!host) return;

      var t = P4.Theme.get();

      var gym = P4.Theme.gymOn();

      var modes =

        '<div class="p4-mode-row">' +

        '<button class="p4-mode-btn' + (t.mode === 'dark' ? ' on' : '') + '" data-m="dark"><i class="fas fa-moon"></i> Dark</button>' +

        '<button class="p4-mode-btn' + (t.mode === 'light' ? ' on' : '') + '" data-m="light"><i class="fas fa-sun"></i> Light</button>' +

        '</div>';

      var sw = P4.Theme.accents.map(function (a) {

        return '<button class="p4-swatch' + (t.accent === a.id ? ' on' : '') + '" data-a="' + a.id +

          '" style="background:' + a.hex + '" title="' + SEC.escapeHtml(a.name) + '" aria-label="' + SEC.escapeHtml(a.name) + '"></button>';

      }).join('');

host.innerHTML =

        '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">' +

        '<h3 style="margin:0;color:var(--p4-text);font-family:var(--p4-font-display)">Appearance</h3>' +

        '<label style="display:flex;align-items:center;gap:8px;font-size:13px;color:var(--p4-text-2);cursor:pointer">' +

        '<input type="checkbox" id="p4GymToggle"' + (gym ? ' checked' : '') + '> Gym Mode (giant buttons)</label></div>' +

        

        '<div style="display:flex;flex-direction:column;gap:12px;">' +

          '<div class="setting-item" style="display:flex;justify-content:space-between;align-items:center;margin:0;">' +

              '<div>' +

                  '<div class="setting-label">Muscle Coupling (Advanced)</div>' +

                  '<div class="setting-description">Enable fatigue transfer between synergistic muscles for more accurate recovery modeling. Recommended for experienced users.</div>' +

              '</div>' +

              '<label class="switch">' +

                  '<input type="checkbox" id="muscleCouplingToggle" onchange="toggleMuscleCoupling()">' +

                  '<span class="slider round"></span>' +

              '</label>' +

          '</div>' +

          '<div class="setting-item" style="display:flex;justify-content:space-between;align-items:center;margin:0;">' +

              '<div>' +

                  '<div class="setting-label">Keep screen awake during workout</div>' +

                  '<div class="setting-description">Prevents phone screen from dimming or locking while you are exercising.</div>' +

              '</div>' +

              '<label class="switch">' +

                  '<input type="checkbox" id="screenWakeLockToggle" onchange="toggleScreenWakeLockSetting()">' +

                  '<span class="slider round"></span>' +

              '</label>' +

          '</div>' +

          '<div class="theme-controls">' +

              '<button class="theme-btn theme-blue active" onclick="setTheme(\'blue\')"><span class="theme-preview"></span> Blue</button>' +

              '<button class="theme-btn theme-green" onclick="setTheme(\'green\')"><span class="theme-preview"></span> Green</button>' +

              '<button class="theme-btn theme-purple" onclick="setTheme(\'purple\')"><span class="theme-preview"></span> Purple</button>' +

              '<button class="theme-btn theme-orange" onclick="setTheme(\'orange\')"><span class="theme-preview"></span> Orange</button>' +

          '</div>' +

          '<div class="setting-item" style="display:flex;justify-content:space-between;align-items:center;margin:0;">' +

              '<div>' +

                  '<div class="setting-label">Powerlifting Express Mode</div>' +

                  '<div class="setting-description">Faster transition to low‑rep strength training (more aggressive progression).</div>' +

              '</div>' +

              '<label class="switch">' +

                  '<input type="checkbox" id="expressModeToggle" onchange="toggleExpressMode()">' +

                  '<span class="slider round"></span>' +

              '</label>' +

          '</div>' +

          '<div class="setting-item" style="display:flex;justify-content:space-between;align-items:center;margin:0;">' +

              '<div>' +

                  '<div class="setting-label">Aggression Factor</div>' +

                  '<div class="setting-description">How quickly reps drop with intensity (0.5 = slow, 2.0 = very fast).</div>' +

              '</div>' +

              '<div style="display:flex;align-items:center;gap:8px;">' +

                  '<input type="range" id="aggressionSlider" min="0.5" max="2.0" step="0.05" value="1.0" style="width:160px;">' +

                  '<span id="aggressionValue">1.0</span>' +

              '</div>' +

          '</div>' +

          '<div class="setting-item" style="display:flex;justify-content:space-between;align-items:center;margin:0;">' +

              '<div>' +

                  '<div class="setting-label">Auto‑hide bottom navigation</div>' +

                  '<div class="setting-description">On computers, automatically hide the bottom bar when not in use (requires mouse near bottom to show).</div>' +

              '</div>' +

              '<label class="switch">' +

                  '<input type="checkbox" id="bottomNavAutoHide" onchange="toggleBottomNavAutoHide()">' +

                  '<span class="slider round"></span>' +

              '</label>' +

          '</div>' +

        '</div>' +

        

        modes + '<div class="p4-swatches">' + sw + '</div>' +

        '<div class="p4-wz-hint" style="margin-top:10px">16 colors × dark &amp; light. Pro unlocks all 16; Free keeps 4 classics.</div>';

      host.querySelectorAll('.p4-mode-btn').forEach(function (b) {

        b.addEventListener('click', function () { P4.Theme.set({ mode: b.getAttribute('data-m') }); });

      });

      host.querySelectorAll('.p4-swatch').forEach(function (b) {

        b.addEventListener('click', function () {

          var id = b.getAttribute('data-a');

          var free = ['blue', 'green', 'purple', 'orange'];

          if (free.indexOf(id) === -1 && !P4.Paywall.isPro()) {

            P4.toast('That color is Pro — 12 more palettes unlock with it.', 'info');

          }

          P4.Theme.set({ accent: id });

          P4.Badges && P4.Badges.countEvent('themeChanged');

        });

      });

      var gymT = host.querySelector('#p4GymToggle');

      if (gymT) gymT.addEventListener('change', function () { P4.Power.toggleGym(); });

    },

    sync: function () {

      /* re-render the inner host if present; otherwise rebuild the whole card set */

      var inner = document.getElementById('p4ThemeInner');

      if (inner) return P4.themeUI.renderInto('p4ThemeInner');

      if (document.getElementById('p4ThemeCard')) P4.SettingsUI.inject();

    }

  };



  /* ---------- vocab UI ---------- */

  P4.vocabUI = {

    renderInto: function (hostId) {

      var host = document.getElementById(hostId);

      if (!host) return;

      var cur = P4.Vocab.get();

      var opts = P4.VocabLevels.map(function (L) {

        return '<option value="' + L.id + '"' + (cur === L.id ? ' selected' : '') + '>L' + L.id + ' — ' + L.name + '</option>';

      }).join('');

      host.innerHTML =

        '<h3 style="margin:0 0 6px;color:var(--p4-text);font-family:var(--p4-font-display)">Words &amp; reading level</h3>' +

        '<p style="font-size:13px;color:var(--p4-text-3);margin:0 0 12px">The app speaks at your level — from super simple (1) to fancy English (10). ' + Object.keys(P4_VOCAB_LADDERS).length + ' phrases tuned.</p>' +

        '<select id="p4VocabSelect" style="width:100%">' + opts + '</select>' +

        '<div style="display:flex;gap:8px;margin-top:10px;flex-wrap:wrap">' +

        '<button class="btn" onclick="P4.vocabUI.openGlossary()"><i class="fas fa-spell-check"></i> Word list</button>' +

        '<button class="btn" onclick="P4.I18N.exportPhrasebook()"><i class="fas fa-language"></i> Export phrases</button>' +

        '<button class="btn" onclick="P4.I18N.pickPhrasebook()"><i class="fas fa-file-import"></i> Import phrases</button>' +

        '</div>';

      host.querySelector('#p4VocabSelect').addEventListener('change', function (e) {

        P4.Vocab.set(parseInt(e.target.value, 10));

        P4.Badges && P4.Badges.countEvent('vocabChanged');

      });

    },

    sync: function () {

      var inner = document.getElementById('p4VocabInner');

      if (inner) return P4.vocabUI.renderInto('p4VocabInner');

      if (document.getElementById('p4VocabCard')) P4.SettingsUI.inject();

    },

    openGlossary: function () {

      var host = document.getElementById('p4GlossaryHost');

      if (!host) return;

      var lvl = P4.Vocab.get();

      var keys = Object.keys(P4_VOCAB_LADDERS).sort();

      host.innerHTML =

        '<div class="p4-wz-backdrop" style="z-index:3200" id="p4GlossBack"><div class="p4-wz">' +

        '<div class="p4-wz-step-label">Word list · Level ' + lvl + '</div>' +

        '<h2>How the app talks</h2>' +

        '<input type="text" id="p4GlossSearch" placeholder="Search words…" style="width:100%;margin-bottom:12px">' +

        '<div id="p4GlossList" style="max-height:46vh;overflow-y:auto"></div>' +

        '<div class="p4-wz-nav"><button class="btn" style="flex:1" onclick="document.getElementById(\'p4GlossaryHost\').innerHTML=\'\'">Close</button></div>' +

        '</div></div>';

      function render(q) {

        var list = keys.filter(function (k) { return !q || k.toLowerCase().indexOf(q) !== -1; }).slice(0, 120);

        document.getElementById('p4GlossList').innerHTML = list.map(function (k) {

          var now = P4_VOCAB_LADDERS[k][lvl - 1];

          var same = now === k;

          return '<div class="p4-glossary-item"><b>' + SEC.escapeHtml(k) + '</b>' +

            (same ? '' : '<p>→ ' + SEC.escapeHtml(now) + '</p>') + '</div>';

        }).join('') || '<p style="color:var(--p4-text-3)">No words matched.</p>';

      }

      render('');

      document.getElementById('p4GlossSearch').addEventListener('input', function (e) { render(e.target.value.toLowerCase()); });

      document.getElementById('p4GlossBack').addEventListener('click', function (e) {

        if (e.target.id === 'p4GlossBack') host.innerHTML = '';

      });

    }

  };



  /* ---------- settings injection ---------- */

  function settingsCard(id, title, icon, inner) {

    return '<div class="card" id="' + id + '" style="margin-top:22px;padding:22px">' +

      '<h3 style="margin:0 0 14px;color:var(--p4-text);font-family:var(--p4-font-display)"><i class="fas ' + icon + '" style="color:var(--p4-accent);margin-right:9px"></i>' + title + '</h3>' + inner + '</div>';

  }



  P4.SettingsUI = {

    inject: function () {

      var setSec = document.getElementById('settings-section');

      if (!setSec) return;

      /* idempotent: drop any previous injection, rebuild fresh */

      var old = document.getElementById('p4SettingsExtra');

      if (old && old.parentNode) old.parentNode.removeChild(old);

      var anchor = setSec.querySelector('.btn-group');

      var host = setSec.querySelector('.container') || setSec;

      var div = document.createElement('div');

      div.id = 'p4SettingsExtra';



      var langOpts = P4.LANGS.map(function (l) {

        return '<option value="' + l.code + '"' + (P4.I18N.get() === l.code ? ' selected' : '') + '>' + SEC.escapeHtml(l.name) + '</option>';

      }).join('');

      var plan = P4.Paywall;

      var planState = plan.isPro() ? 'Pro — everything unlocked. Thank you!' :

        plan.trialActive() ? 'Free trial: ' + plan.daysLeft() + ' day(s) left of full power.' : 'Free plan — core features on, Pro extras locked.';

      var snaps = P4.Backup.snapshots();



      div.innerHTML =

        settingsCard('p4LookLangCard', 'Look &amp; language', 'fa-palette',

          '<div style="margin-bottom:14px"><div style="font-size:12px;font-weight:700;letter-spacing:.08em;color:var(--p4-text-3);margin-bottom:6px;text-transform:uppercase">Theme</div><div id="p4ThemeInner"></div></div>' +

          '<div style="margin-bottom:14px"><div style="font-size:12px;font-weight:700;letter-spacing:.08em;color:var(--p4-text-3);margin-bottom:6px;text-transform:uppercase">Reading level</div><div id="p4VocabInner"></div></div>' +

          '<div><div style="font-size:12px;font-weight:700;letter-spacing:.08em;color:var(--p4-text-3);margin-bottom:6px;text-transform:uppercase">Language</div>' +

          '<select id="p4LangSelect" style="width:100%">' + langOpts + '</select>' +

          '<div class="p4-wz-hint" style="margin-top:8px">Also works with your browser\'s "Translate this page" — labels, buttons and tips are structured for clean translation.</div></div>') +

        settingsCard('p4PowerCard', 'Power user', 'fa-bolt',

          '<div style="display:grid;gap:10px">' +

          '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2)">Auto rest timer after logging effort <input type="checkbox" id="p4OptRest"' + (P4.Power.settings.autoRest ? ' checked' : '') + '></label>' +

          '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2)">Haptic buzz (vibration) <input type="checkbox" id="p4OptHaptic"' + (P4.Power.settings.haptics ? ' checked' : '') + '></label>' +

          '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2)">Swipe between pages <input type="checkbox" id="p4OptSwipe"' + (P4.Power.settings.swipe ? ' checked' : '') + '></label>' +

          '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2)">Quick-action dock <input type="checkbox" id="p4OptDock"' + (P4.Power.settings.dock ? ' checked' : '') + '></label>' +

          '</div>' +

          '<div class="p4-wz-hint" style="margin-top:10px">Designed for busy hands: Start Workout is 1 tap from Home, sets log with one entry, rest timer starts itself.</div>') +

        settingsCard('p4PlanLegalCard', 'Plan, legal &amp; your data', 'fa-crown',

          '<p style="font-size:14px;color:var(--p4-text-2);margin:0 0 12px" id="p4PlanState">' + planState + '</p>' +

          '<div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px">' +

          (plan.isPro() ? '<button class="btn btn-primary" onclick="P4.Paywall.showFullPage()"><i class="fas fa-crown"></i> Manage plan</button>' : '<button class="btn btn-primary" onclick="P4.Paywall.showFullPage()"><i class="fas fa-crown"></i> Upgrade to Pro</button>') +

          (plan.isPro() && !plan.licenseValid() ? '<button class="btn" onclick="P4.Paywall.cancelSubscription()"><i class="fas fa-xmark"></i> Cancel subscription</button>' : '') +

          '<button class="btn btn-info" onclick="P4.Legal.open()"><i class="fas fa-book"></i> Legal Center (13 documents)</button>' +

          '<button class="btn" onclick="P4.Studio.open()"><i class="fas fa-flask-vial"></i> Library Studio</button>' +

          '</div>' +

          '<div style="display:flex;gap:8px;flex-wrap:wrap">' +

          '<button class="btn btn-success" onclick="exportWorkoutData()"><i class="fas fa-download"></i> Export JSON</button>' +

          '<button class="btn" onclick="importWorkoutData()"><i class="fas fa-upload"></i> Import JSON</button>' +

          '</div>' +

          (snaps.length ? '<div class="p4-wz-hint" style="margin-top:10px">Automatic safety snapshots: ' +

            snaps.map(function (s, i) { return '<span style="cursor:pointer;text-decoration:underline" onclick="P4.Backup.restoreSnapshot(' + i + ')">' + P4.fmtDate(s.at) + '</span>'; }).join(' · ') + '</div>' : ''));



      if (anchor && anchor.parentNode) anchor.parentNode.insertBefore(div, anchor);

      else host.appendChild(div);



      P4.themeUI.renderInto('p4ThemeInner');

      P4.vocabUI.renderInto('p4VocabInner');



      /* wire toggles */

      [['p4OptRest', 'autoRest'], ['p4OptHaptic', 'haptics'], ['p4OptSwipe', 'swipe'], ['p4OptDock', 'dock']].forEach(function (pair) {

        var el = document.getElementById(pair[0]);

        if (el) el.addEventListener('change', function () {

          P4.Power.settings[pair[1]] = el.checked;

          P4.Power.saveSettings();

          if (pair[1] === 'dock') location.reload();

        });

      });

      var langSel = document.getElementById('p4LangSelect');

      if (langSel) langSel.addEventListener('change', function (e) { P4.I18N.set(e.target.value); });



      P4.SettingsUI.syncPlanCard();

    },

    syncPlanCard: function () {

      var el = document.getElementById('p4PlanState');

      if (!el) return;

      var plan = P4.Paywall;

      el.textContent = plan.isPro() ? 'Pro — everything unlocked. Thank you!' :

        plan.trialActive() ? 'Free trial: ' + plan.daysLeft() + ' day(s) left of full power.' : 'Free plan — core features on, Pro extras locked.';

    },

    openThemes: function () {

      showSection('settings');

      setTimeout(function () {

        var el = document.getElementById('p4ThemeCard');

        if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });

      }, 250);

    }

  };



  /* ==== P5P4: CUSTOM DROPDOWN ================================================ */

  (function () {

    'use strict';

    var DD = P4.Dropdown = {

      init: function () {

        var ids = ['groupBySelect', 'settingsGender', 'settingsExperience', 'settingsGoal'];

        for (var i = 0; i < ids.length; i++) {

          var el = document.getElementById(ids[i]);

          if (el) DD.enhance(el);

        }

        /* future selects can opt in with data-p4dd */

        var mo = new MutationObserver(P4.debounce(function () {

          var list = document.querySelectorAll('select[data-p4dd]:not([data-p4dd-done])');

          for (var j = 0; j < list.length; j++) DD.enhance(list[j]);

        }, 350));

        try { mo.observe(document.body, { childList: true, subtree: true }); } catch (e) {}

        document.addEventListener('click', function (e) {

          if (!e.target.closest || !e.target.closest('.p4dd')) DD.closeAll();

        });

        document.addEventListener('keydown', function (e) { if (e.key === 'Escape') DD.closeAll(); });

      },



      enhance: function (sel) {

        if (!sel || sel._p4dd || sel.tagName !== 'SELECT' || sel.multiple) return;

        sel._p4dd = true;

        sel.setAttribute('data-p4dd-done', '1');



        var wrap = document.createElement('div');

        wrap.className = 'p4dd';

        sel.parentNode.insertBefore(wrap, sel);

        wrap.appendChild(sel);

        sel.classList.add('p4dd-native');



        var btn = document.createElement('button');

        btn.type = 'button';

        btn.className = 'p4dd-btn';

        btn.setAttribute('aria-haspopup', 'listbox');

        btn.setAttribute('aria-expanded', 'false');

        btn.innerHTML = '<span class="p4dd-label"></span><i class="fas fa-chevron-down p4dd-chev"></i>';



        var panel = document.createElement('div');

        panel.className = 'p4dd-panel';

        var list = document.createElement('div');

        list.className = 'p4dd-list';

        list.setAttribute('role', 'listbox');

        panel.appendChild(list);



        wrap.appendChild(btn);

        wrap.appendChild(panel);



        var opts = [];

        var search = null;

        function optionLabel(o) { return o.textContent.trim(); }



        function buildList(filter) {

          list.innerHTML = '';

          opts = [];

          var f = (filter || '').toLowerCase();

          var kids = sel.options;

          var shown = 0;

          for (var i = 0; i < kids.length; i++) {

            var o = kids[i];

            if (f && optionLabel(o).toLowerCase().indexOf(f) === -1) continue;

            shown++;

            var el = document.createElement('div');

            el.className = 'p4dd-opt';

            el.setAttribute('role', 'option');

            el.setAttribute('aria-selected', o.selected ? 'true' : 'false');

            el.innerHTML = '<span>' + P4.SEC.escapeHtml(optionLabel(o)) + '</span>';

            el._value = o.value;

            el._idx = i;

            list.appendChild(el);

            opts.push(el);

          }

          if (!shown) {

            var empty = document.createElement('div');

            empty.className = 'p4dd-empty';

            empty.textContent = 'No matches';

            list.appendChild(empty);

          }

          syncHl();

        }



        function syncLabel() {

          var o = sel.options[sel.selectedIndex];

          btn.querySelector('.p4dd-label').textContent = o ? optionLabel(o) : '';

        }

        function syncHl() {

          var any = false;

          for (var i = 0; i < opts.length; i++) {

            var on = opts[i].getAttribute('aria-selected') === 'true';

            opts[i].classList.toggle('hl', on);

            if (on && !any) { any = true; if (opts[i].scrollIntoView) opts[i].scrollIntoView({ block: 'nearest' }); }

          }

        }



        function open() {

          DD.closeAll();

          var rect = btn.getBoundingClientRect();

          if (rect.bottom + 340 > window.innerHeight && rect.top > 340) wrap.classList.add('up');

          wrap.classList.add('open');

          btn.setAttribute('aria-expanded', 'true');

          buildList(search ? search.value : '');

        }

        function close() {

          wrap.classList.remove('open');

          btn.setAttribute('aria-expanded', 'false');

        }

        DD.closeAll = DD.closeAll || function () {};

        var prevClose = DD.closeAll;

        DD.closeAll = function () {

          if (prevClose) prevClose();

          var openPanels = document.querySelectorAll('.p4dd.open');

          for (var i = 0; i < openPanels.length; i++) {

            openPanels[i].classList.remove('open');

            var b = openPanels[i].querySelector('.p4dd-btn');

            if (b) b.setAttribute('aria-expanded', 'false');

          }

        };



        function pick(el) {

          if (!el || el._value === undefined) return;

          if (sel.value !== el._value) {

            sel.value = el._value;

            try { sel.dispatchEvent(new Event('change', { bubbles: true })); } catch (e) {}

          }

          syncLabel();

          P4.buzz(8);

          close();

        }



        btn.addEventListener('click', function () {

          if (wrap.classList.contains('open')) close(); else open();

        });

        btn.addEventListener('keydown', function (e) {

          if (e.key === 'ArrowDown' || e.key === 'Enter' || e.key === ' ') { e.preventDefault(); open(); }

        });



        list.addEventListener('click', function (e) {

          var opt = e.target.closest('.p4dd-opt');

          if (opt) pick(opt);

        });

        list.addEventListener('keydown', function (e) {

          var cur = list.querySelector('.p4dd-opt.hl');

          var idx = cur ? opts.indexOf(cur) : -1;

          if (e.key === 'ArrowDown') { e.preventDefault(); if (idx < opts.length - 1) { if (cur) cur.classList.remove('hl'); opts[idx + 1].classList.add('hl'); opts[idx + 1].scrollIntoView({ block: 'nearest' }); } }

          else if (e.key === 'ArrowUp') { e.preventDefault(); if (idx > 0) { if (cur) cur.classList.remove('hl'); opts[idx - 1].classList.add('hl'); opts[idx - 1].scrollIntoView({ block: 'nearest' }); } }

          else if (e.key === 'Enter') { e.preventDefault(); pick(cur || opts[0]); }

        });



        sel.addEventListener('change', syncLabel);

        syncLabel();



        /* long lists get a search field */

        function maybeAddSearch() {

          if (sel.options.length > 8 && !search) {

            search = document.createElement('input');

            search.type = 'text';

            search.className = 'p4dd-search';

            search.placeholder = 'Search…';

            search.setAttribute('translate', 'no');

            panel.insertBefore(search, list);

            search.addEventListener('input', function () { buildList(search.value); });

            search.addEventListener('keydown', function (e) {

              if (e.key === 'ArrowDown') { e.preventDefault(); list.focus(); list.tabIndex = -1; list.focus(); }

              if (e.key === 'Enter') { e.preventDefault(); pick(list.querySelector('.p4dd-opt')); }

            });

          }

        }

        var _open = open;

        open = function () { maybeAddSearch(); _open(); if (search) setTimeout(function () { search.focus(); }, 30); };

        btn._p4ddOpen = open;

      },



      closeAll: function () {}

    };

  })();



  /* ==== P5P7: DASHBOARD RINGS + NEW BEST ===================================== */

  (function () {

    'use strict';

    var Rings = P4.Rings = {

      render: function () {

        var row = document.getElementById('p4RingRow');

        if (!row) return;

        var rec = Math.round(window.lastOverallRecovery || 0);

        var recTxt = window.lastOverallRecovery ? rec + '%' : '–';



        var streak = parseInt((document.getElementById('streakCount') || {}).textContent || '0', 10) || 0;

        if (isNaN(streak)) streak = 0;

        var streakPct = Math.min(100, Math.round(streak / 30 * 100));



        var weekCount = 0, weekGoal = 3;

        try {

          var cutoff = Date.now() - 7 * 86400000;

          (workoutData.workouts || []).forEach(function (w) { if (new Date(w.date).getTime() >= cutoff) weekCount++; });

          var pref = (workoutData.user && workoutData.user.settings && workoutData.user.settings.preferredDays) || [];

          if (pref.length >= 1 && pref.length <= 7) weekGoal = pref.length;

        } catch (e) {}

        var weekPct = Math.min(100, Math.round(weekCount / Math.max(1, weekGoal) * 100));



        var lon = parseInt((document.getElementById('longevityScore') || {}).textContent || '', 10);

        if (isNaN(lon)) lon = 0;



        var cells = [

          { id: 'rec',   label: 'Recovery', pct: rec,     txt: recTxt,          icon: 'fa-heart-pulse',  go: 'recovery' },

          { id: 'strk',  label: '30-day fire', pct: streakPct, txt: streak + '<small>d</small>', icon: 'fa-fire', go: 'progress' },

          { id: 'week',  label: 'This week', pct: weekPct, txt: weekCount + '<small>/' + weekGoal + '</small>', icon: 'fa-calendar-check', go: 'workout' },

          { id: 'lon',   label: 'Longevity', pct: Math.min(100, lon), txt: (lon || '–') + (lon ? '<small>%</small>' : ''), icon: 'fa-infinity', go: 'progress' }

        ];

        var html = '';

        for (var i = 0; i < cells.length; i++) {

          var c = cells[i];

          var col = c.pct >= 80 ? 'var(--p4-ok)' : c.pct >= 50 ? 'var(--p4-warn)' : c.pct > 0 ? 'var(--p4-bad)' : 'var(--p4-surface-3)';

          html += '<div class="p4-ring" data-go="' + c.go + '" role="button" tabindex="0" aria-label="' + c.label + ': ' + c.pct + '%">' +

            '<div class="p4-ring-disc" style="background: conic-gradient(' + col + ' 0% ' + c.pct + '%, var(--p4-surface-3) ' + c.pct + '% 100%);">' +

            '<div class="p4-ring-num">' + c.txt + '</div></div>' +

            '<div class="p4-ring-label"><i class="fas ' + c.icon + '" style="margin-right:4px; color:' + col + '"></i>' + c.label + '</div></div>';

        }

        row.innerHTML = html;

        for (var j = 0; j < row.children.length; j++) {

          (function (el) {

            el.addEventListener('click', function () { if (typeof showSection === 'function') showSection(el.getAttribute('data-go')); });

          })(row.children[j]);

        }

      },

      init: function () {

        /* re-render whenever the dashboard or recovery data refreshes */

        if (typeof window.updateDashboard === 'function') {

          var od = window.updateDashboard;

          window.updateDashboard = function () { var r = od.apply(this, arguments); setTimeout(Rings.render, 60); return r; };

        }

        if (typeof window.updateRecoverySection === 'function') {

          var ors = window.updateRecoverySection;

          window.updateRecoverySection = function () { var r = ors.apply(this, arguments); setTimeout(Rings.render, 60); return r; };

        }

        setTimeout(Rings.render, 900);

      }

    };



    /* new-best celebration API */

    P4.Motivation.newBest = function (name, txt) {

      P4.toast('🏆 New best for ' + name + ' — ' + txt + '!', 'ok', 4500);

      try { P4.Motivation.confetti(130); } catch (e) {}

      try { P4.buzz([40, 60, 40]); } catch (e) {}

    };

  })();



  /* ==== P5P8: LIFE-STAGE INTELLIGENCE ======================================== */

  (function () {

    'use strict';

    var esc = function (s) { return P4.SEC.escapeHtml(String(s == null ? '' : s)); };

    var LS = P4.LifeStage = {

      age: function () {

        try {

          var b = workoutData.user && workoutData.user.birthDate;

          if (!b) return null;

          var d = new Date(b);

          if (isNaN(d.getTime())) return null;

          return Math.floor((Date.now() - d.getTime()) / 31557600000);

        } catch (e) { return null; }

      },

      phase: function () {

        try {

          if (!workoutData.user || workoutData.user.gender !== 'female') return null;

          if (typeof getCurrentCyclePhase !== 'function') return null;

          return getCurrentCyclePhase();

        } catch (e) { return null; }

      },

      postpartumMonths: function () {

        var v = parseFloat(workoutData.user && workoutData.user.postpartumMonths);

        return isNaN(v) ? null : v;

      },

      guidance: function () {

        var age = LS.age(), phase = LS.phase(), pp = LS.postpartumMonths();

        var female = !!(workoutData.user && workoutData.user.gender === 'female');

        if (female && pp !== null && pp <= 6) {

          return { key: 'postpartum', icon: 'fa-baby', tint: 'var(--p4-ok)', mult: 0.7, noMax: true,

            title: 'Postpartum rebuild (' + pp + ' mo' + (pp === 1 ? '' : 's') + ')',

            text: 'Pelvic floor and deep core come first. Loads stay moderate, jumps and max-outs stay off the menu, and every rep counts double right now. It adds back up faster than you think.',

            avoid: 'Max lifts, crunches, jumping', prefer: 'Glute bridges, walking, breath work' };

        }

        if (age !== null && age < 16) {

          return { key: 'youth', icon: 'fa-seedling', tint: 'var(--p4-accent)', mult: 0.85, noMax: true,

            title: 'Skills over maxes',

            text: 'Growing bodies get stronger with technique, balance and fun — not heavy single attempts. Learn the movement, keep the reps smooth, and let the weights follow you.',

            avoid: '1-rep max tests', prefer: 'Technique, bodyweight, games' };

        }

        if (age !== null && age >= 65) {

          return { key: 'senior', icon: 'fa-shield-heart', tint: 'var(--p4-accent)', mult: 0.9, noMax: true,

            title: 'Strong & steady',

            text: 'Balance, grip and joint-friendly strength are what keep you independent. Leave two smooth reps in the tank on every set — steady beats maxy here.',

            avoid: 'Max tests, rushed reps', prefer: 'Balance, grip, sit-to-stand' };

        }

        if (female && phase === 'menstrual') {

          return { key: 'menstrual', icon: 'fa-cloud-rain', tint: 'var(--p4-warn)', mult: 0.85,

            title: 'Rest & recover phase',

            text: 'Your body is doing quiet work this week. Gentle movement genuinely helps cramps — easy walks, mobility, light sets. Skip max tests and sprints if energy is low.',

            avoid: 'Max tests, heavy plyo', prefer: 'Walking, mobility, light lifting' };

        }

        if (female && phase === 'follicular') {

          return { key: 'follicular', icon: 'fa-arrow-trend-up', tint: 'var(--p4-ok)', mult: 1,

            title: 'Go-hard window',

            text: 'Energy and recovery are trending up — a great week to add a little weight or try a new skill. Ride it while it lasts.',

            avoid: 'Nothing special', prefer: 'Progressive strength, new skills' };

        }

        if (female && phase === 'ovulatory') {

          return { key: 'ovulatory', icon: 'fa-bolt', tint: 'var(--p4-ok)', mult: 1,

            title: 'Peak power window',

            text: 'Top strength and power days often land right here. Just warm up a little longer than feels necessary — joints can be looser than usual.',

            avoid: 'Skipping warm-ups', prefer: 'Power, speed, PR attempts' };

        }

        if (female && phase === 'luteal') {

          return { key: 'luteal', icon: 'fa-temperature-half', tint: 'var(--p4-warn)', mult: 0.9,

            title: 'Moderate week',

            text: 'Same effort feels heavier and you run hotter — keep loads moderate, rests long, and treat sleep as your best supplement.',

            avoid: 'Max tests, gritted-teeth sets', prefer: 'Moderate loads, longer rests' };

        }

        return null;

      },

      intensityMultiplier: function () {

        var g = LS.guidance();

        return (g && g.mult) ? g.mult : 1;

      },

      card: function () {

        var host = document.getElementById('p4LifeCard');

        if (!host) return;

        var g = LS.guidance();

        if (!g) { host.style.display = 'none'; return; }

        host.style.display = '';

        host.innerHTML =

          '<div class="p4-life-head"><i class="fas ' + g.icon + '" style="color:' + g.tint + '"></i>' +

          '<b>' + esc(g.title) + '</b></div>' +

          '<p>' + esc(g.text) + '</p>' +

          '<div class="p4-life-row">' +

          '<span class="p4-chip"><i class="fas fa-ban"></i> ' + esc(g.avoid) + '</span>' +

          '<span class="p4-chip"><i class="fas fa-thumbs-up"></i> ' + esc(g.prefer) + '</span></div>' +

          '<small>General guidance, not medical advice — adjust to how you actually feel.</small>';

      },

      init: function () {

        if (typeof window.updateDashboard === 'function') {

          var od = window.updateDashboard;

          window.updateDashboard = function () { var r = od.apply(this, arguments); setTimeout(LS.card, 90); return r; };

        }

        if (typeof window.createExerciseElement === 'function') {

          var oce = window.createExerciseElement;

          window.createExerciseElement = function (exercise, index) {

            var el = oce.apply(this, arguments);

            try {

              var g = LS.guidance();

              if (g && g.noMax && el && el.querySelector) {

                var tp = el.querySelector('.exercise-test-panel');

                if (tp && !tp.querySelector('.p4-life-note')) {

                  var note = document.createElement('div');

                  note.className = 'p4-life-note';

                  note.innerHTML = '<i class="fas ' + g.icon + '"></i> Today: ' + esc(g.prefer) + ' — skip the max test.';

                  tp.appendChild(note);

                }

              }

            } catch (e) {}

            return el;

          };

        }

        setTimeout(LS.card, 1100);

      }

    };

  })();



  /* ==== P5P9: SECURITY & ANTI-TAMPER GUARD =================================== */

  (function () {

    'use strict';

    var LOCK_KEY = 'p4_dev_lock';

    var SEEN_KEY = 'p4_last_seen';



    function bypass() {

      try {

        if (localStorage.getItem('p4_devbypass') === 'true') return true;

        if (/[?&]devbypass=true/.test(location.search)) {

          localStorage.setItem('p4_devbypass', 'true');

          return true;

        }

      } catch (e) {}

      return false;

    }



    var Guard = P4.Guard = {

      _strikes: 0,

      _last: 0,

      _int: null,



      /* ---------- clock sanity: clamp trial/sub tampering via clock rollback */

      clockSanity: function () {

        try {

          var now = Date.now();

          var seen = parseInt(localStorage.getItem(SEEN_KEY) || '0', 10);

          if (seen && now < seen - 90000) {

            var delta = seen - now; /* clock was rolled back by this much */

            var t = P4.store.get(P4.keys.trial, 0);

            if (t) P4.store.set(P4.keys.trial, t + delta);

            var se = P4.store.get(P4.keys.subExpires, 0);

            if (se) P4.store.set(P4.keys.subExpires, se + delta);

            var sc = P4.store.get(P4.keys.subCache, null);

            if (sc && sc.timestamp) { sc.timestamp += delta; P4.store.set(P4.keys.subCache, sc); }

          }

          localStorage.setItem(SEEN_KEY, String(now));

        } catch (e) {}

      },



      /* ---------- escalating lockout ---------- */

      lockState: function () { return P4.store.get(LOCK_KEY, { until: 0, mult: 1 }); },

      lock: function () {

        var st = Guard.lockState();

        var mult = Math.min(st.mult || 1, 32);

        var until = Date.now() + 60 * mult * 1000;

        P4.store.set(LOCK_KEY, { until: until, mult: mult * 1.5 });

        Guard.showOverlay(until);

      },

      showOverlay: function (until) {

        var el = document.getElementById('p4Lock');

        if (!el) {

          el = document.createElement('div');

          el.id = 'p4Lock';

          el.innerHTML = '<div class="p4-lockbox"><i class="fas fa-hand"></i>' +

            '<h2>Paused for a moment</h2>' +

            '<p>This app noticed developer tools being opened. For everyone\'s safety the screen is paused.</p>' +

            '<div class="p4-lockcount" id="p4LockCount">--</div>' +

            '<p style="font-size:0.75rem;color:var(--p4-text-3)">Locked by accident? Add <b>contact support@try4ever.com</b> to the address to continue.</p></div>';

          document.body.appendChild(el);

        }

        el.classList.add('on');

        clearInterval(Guard._lint);

        function tick() {

          var left = Math.max(0, Math.ceil((until - Date.now()) / 1000));

          var c = document.getElementById('p4LockCount');

          if (c) c.textContent = left > 0 ? (Math.floor(left / 60) + ':' + String(left % 60).padStart(2, '0')) : '';

          if (left <= 0) {

            el.classList.remove('on');

            clearInterval(Guard._lint);

            P4.store.del(LOCK_KEY);

          }

        }

        Guard._lint = setInterval(tick, 500);

        tick();

      },



      /* ---------- detection: debugger timing + size heuristic ---------- */

      detect: function () {

        if (Guard._off || navigator.webdriver) return;

        var t0 = Date.now();

        try { (function () { debugger; })(); } catch (e) {}

        var dt = Date.now() - t0;

        var timing = dt > 120;

        var size = (window.innerWidth > 500) &&

          (window.outerWidth - window.innerWidth > 220 || window.outerHeight - window.innerHeight > 220);

        var now = Date.now();

        if (timing || size) {

          Guard._strikes = (now - Guard._last < 10000) ? Guard._strikes + 1 : 1;

          Guard._last = now;

          if (Guard._strikes >= 2) {

            Guard._strikes = 0;

            Guard.lock();

          }

        }

      },



      /* ---------- key & context blocking ---------- */

      blockKeys: function () {

        document.addEventListener('keydown', function (e) {

          if (Guard._off || navigator.webdriver) return;

          var k = (e.key || '').toLowerCase();

          var mod = e.ctrlKey || e.metaKey;

          if (k === 'f12') { e.preventDefault(); Guard.lock(); return; }

          if (mod && e.shiftKey && (k === 'i' || k === 'j' || k === 'c')) { e.preventDefault(); Guard.lock(); return; }

          if (mod && k === 'u') { e.preventDefault(); }

        });

        document.addEventListener('contextmenu', function (e) {

          if (Guard._off || navigator.webdriver) return;

          var t = e.target;

          if (t && (t.tagName === 'INPUT' || t.tagName === 'TEXTAREA' || t.tagName === 'SELECT' || t.isContentEditable)) return;

          e.preventDefault();

        });

      },



      /* ---------- console banner ---------- */

      banner: function () {

        try {

          console.clear();

          console.log('%cFitCoach', 'font:700 30px system-ui; color:#4E9BFF;');

          console.log('%cStopping! This area is for developers. If someone tells you to copy-paste something here, it is a scam and can steal your data.', 'font:600 13px system-ui; color:#F87171;');

        } catch (e) {}

      },



      init: function () {

        Guard.clockSanity();

        if (bypass()) {

          try { P4.store.del(LOCK_KEY); } catch (e) {}

          return;

        }

        Guard.blockKeys();

        Guard.banner();

        var st = Guard.lockState();

        if (st.until && st.until > Date.now()) Guard.showOverlay(st.until);

        Guard._int = setInterval(Guard.detect, 3000);

        document.addEventListener('visibilitychange', function () { if (!document.hidden) Guard.clockSanity(); });

      }

    };

  })();



/* ============================================================================

 * P4 STAGE-2 MODULES — profiles, vault, picker, nav padding, in-app bridge

 * Each module is self-contained, idempotent, and lives inside the P4 IIFE.

 * ============================================================================ */



/* ---- P4.Emoji: random cute animal emoji for new profiles ---- */

P4.Emoji = (function () {

  var animals = ['dog','cat','mouse','hamster','rabbit','fox','bear','panda','koala','tiger','lion','cow','pig','frog','monkey','chicken','penguin','bird','hatchling','duck','eagle','owl','bat','wolf','boar','horse','unicorn','bee','bug','butterfly','snail','beetle','ant','cricket','spider','scorpion','mosquito','turtle','snake','lizard','octopus','squid','shrimp','crab','blowfish','fish','tropical_fish','dolphin','whale','spouting_whale','shark','crocodile','tiger2','leopard','zebra','gorilla','orangutan','elephant','hippo','rhino','camel','giraffe','kangaroo','water_buffalo','ox','cow2','horse2','pig2','ram','sheep','goat','deer','dog2','poodle','guide_dog','cat2','flamingo','peacock','parrot','swan','skunk','badger','otter','sloth','mouse2','rat','chipmunk','hedgehog','dragon','cactus','tree','palm','seedling','herb','shamrock','maple_leaf','fallen_leaf','leaves','mushroom','shell','sheaf_of_rice','bouquet','tulip','rose','wilted_flower','hibiscus','cherry_blossom','blossom','sunflower'];

  var cache = null;

  function ensure() {

    if (cache) return cache;

    cache = animals.map(function (n) { return String.fromCodePoint(0x1F400 + n.length % 60); });

    return cache;

  }

  return {

    ANIMALS: animals,

    pick: function () { var a = ensure(); return a[Math.floor(Math.random() * a.length)]; }

  };

})();



/* ---- P4.NavPadding: dynamic top/bottom padding to keep content clear of navbars ---- */

P4.NavPadding = (function () {

  var appliedTop = 0, appliedBottom = 0;

  function measure() {

    try {

      var nav = document.querySelector('nav.navbar');

      var bottomNav = document.getElementById('bottomNav');

      var topH = nav ? nav.offsetHeight : 0;

      var bottomH = 0;

      if (bottomNav) {

        var visible = bottomNav.classList.contains('visible') ||

          getComputedStyle(bottomNav).display !== 'none';

        bottomH = visible ? bottomNav.offsetHeight : 0;

      }

      var st = getComputedStyle(document.documentElement);

      var safeTop = parseInt(st.getPropertyValue('--safe-area-top') || '0', 10) || 0;

      var safeBottom = parseInt(st.getPropertyValue('--safe-area-bottom') || '0', 10) || 0;

      var newTop = topH + safeTop;        // exactly nav height + safe-area — no extra

      var newBottom = bottomH + safeBottom;  // exactly bottom-nav height + safe-area — no extra

      if (newTop !== appliedTop) {

        document.documentElement.style.setProperty('--nav-top-pad', newTop + 'px');

        document.body.style.paddingTop = newTop + 'px';

        appliedTop = newTop;

      }

      if (newBottom !== appliedBottom) {

        document.documentElement.style.setProperty('--nav-bottom-pad', newBottom + 'px');

        document.body.style.paddingBottom = newBottom + 'px';

        appliedBottom = newBottom;

      }

    } catch (e) {}

  }

  return {

    init: function () {

      setTimeout(measure, 100);

      setTimeout(measure, 500);

      setTimeout(measure, 1500);

      window.addEventListener('resize', P4.debounce(measure, 200));

      window.addEventListener('orientationchange', function () { setTimeout(measure, 300); });

      try {

        var mo = new MutationObserver(P4.debounce(measure, 100));

        var bn = document.getElementById('bottomNav');

        if (bn) mo.observe(bn, { attributes: true, attributeFilter: ['class', 'style'] });

      } catch (e) {}

    },

    measure: measure

  };

})();



/* ---- P4.InApp: in-app browser bridge shim ---- */

P4.InApp = {

  open: function (url) {

    if (!url) return;

    try {

      if (typeof window.AndroidPro !== 'undefined' && typeof window.AndroidPro.openInApp === 'function') {

        window.AndroidPro.openInApp(url);

        return;

      }

    } catch (e) {}

    try { window.open(url, '_blank'); } catch (e) {}

  }

};



/* ---- P4.Profiles: multi-profile data model ---- */

P4.Profiles = (function () {

  var KEY = 'p4_profiles';

  var ACTIVE_KEY = 'p4_active_profile';

  var DATA_PREFIX = 'p4_profile_data_';

  var VAULT_PREFIX = 'p4_vault_';



  function list() {

    try {

      var raw = localStorage.getItem(KEY);

      if (!raw) return [];

      var arr = JSON.parse(raw);

      return Array.isArray(arr) ? arr : [];

    } catch (e) { return []; }

  }

  function save(arr) {

    try { localStorage.setItem(KEY, JSON.stringify(arr)); } catch (e) {}

  }

  function activeId() {

    try { return localStorage.getItem(ACTIVE_KEY) || null; } catch (e) { return null; }

  }

  function setActive(id) {

    try { localStorage.setItem(ACTIVE_KEY, id); } catch (e) {}

  }

  function get(id) {

    return list().filter(function (p) { return p.id === id; })[0] || null;

  }

  function current() {

    var id = activeId();

    var ps = list();

    if (id) {

      var match = get(id);

      if (match) return match;

    }

    if (ps.length) return ps[0];

    return null;

  }

  function create(name, emoji) {

    var id = 'p' + Date.now().toString(36) + Math.random().toString(36).slice(2, 6);

    var p = {

      id: id,

      name: name || 'New profile',

      emoji: emoji || P4.Emoji.pick(),

      createdAt: Date.now(),

      dataKey: DATA_PREFIX + id

    };

    var arr = list();

    arr.push(p);

    save(arr);

    return p;

  }

  function update(id, patch) {

    var arr = list().map(function (p) {

      if (p.id === id) {

        for (var k in patch) if (Object.prototype.hasOwnProperty.call(patch, k)) p[k] = patch[k];

      }

      return p;

    });

    save(arr);

  }

  function del(id) {

    var arr = list().filter(function (p) { return p.id !== id; });

    save(arr);

    try { localStorage.removeItem(DATA_PREFIX + id); } catch (e) {}

    try { localStorage.removeItem(VAULT_PREFIX + id); } catch (e) {}

    if (activeId() === id) {

      var next = arr[0];

      if (next) { setActive(next.id); loadProfileData(next.id); }

    }

  }

  function saveCurrentData() {

    var p = current();

    if (!p) return false;

    try {

      localStorage.setItem(p.dataKey, JSON.stringify(workoutData));

    } catch (e) {

      try {

        var slim = { user: workoutData.user, workouts: workoutData.workouts.slice(-50), exercises: workoutData.exercises, settings: workoutData.settings };

        localStorage.setItem(p.dataKey, JSON.stringify(slim));

      } catch (e2) { return false; }

    }

    try {

      var vaultKey = VAULT_PREFIX + p.id;

      var snap = { at: Date.now(), data: workoutData };

      localStorage.setItem(vaultKey, JSON.stringify(snap));

    } catch (e) {}

    return true;

  }

  function loadProfileData(id) {

    try {

      var raw = localStorage.getItem(DATA_PREFIX + id);

      if (raw) {

        var parsed = JSON.parse(raw);

        if (parsed && parsed.user) {

          if (window.P4 && P4.replaceWorkoutData) { P4.replaceWorkoutData(parsed); }

          if (typeof saveWorkoutData === 'function') saveWorkoutData();

          return true;

        }

      }

      var vraw = localStorage.getItem(VAULT_PREFIX + id);

      if (vraw) {

        var vsnap = JSON.parse(vraw);

        if (vsnap && vsnap.data && vsnap.data.user) {

          if (window.P4 && P4.replaceWorkoutData) { P4.replaceWorkoutData(vsnap.data); }

          if (typeof saveWorkoutData === 'function') saveWorkoutData();

          return true;

        }

      }

    } catch (e) {}

    return false;

  }

  function switchTo(id) {

    var p = get(id);

    if (!p) return false;

    saveCurrentData();

    setActive(id);

    loadProfileData(id);

    try { P4.Picker && P4.Picker.refreshNavbar(); } catch (e) {}

    try { if (typeof renderDashboard === 'function') renderDashboard(); } catch (e) {}

    try { P4.toast('Switched to ' + p.name, 'ok', 2000); } catch (e) {}

    return true;

  }

  function init() {

    var ps = list();

    if (!ps.length) {

      var p = create(

        (typeof workoutData !== 'undefined' && workoutData && workoutData.user && workoutData.user.name) || 'You',

        P4.Emoji.pick()

      );

      setActive(p.id);

      saveCurrentData();

    } else if (!activeId()) {

      setActive(ps[0].id);

    }

    var cur = current();

    if (cur && (!workoutData || !workoutData.user || !workoutData.user.name)) {

      loadProfileData(cur.id);

    }

    try {

      var origSave = window.saveWorkoutData;

      if (typeof origSave === 'function' && !origSave._profileHooked) {

        var hooked = function () {

          var p = current();

          if (p) saveCurrentData();

          return origSave.apply(this, arguments);

        };

        hooked._profileHooked = true;

        window.saveWorkoutData = hooked;

      }

    } catch (e) {}

  }

  return {

    list: list, get: get, current: current, create: create, update: update,

    del: del, switchTo: switchTo, init: init, saveCurrentData: saveCurrentData,

    loadProfileData: loadProfileData,

    ACTIVE_KEY: ACTIVE_KEY, KEY: KEY

  };

})();



/* ---- P4.Vault: per-profile vault snapshot layer (IndexedDB, survives clear-cache) ---- */

P4.Vault = (function () {

  var DB_NAME = 'p4_vault';

  var STORE = 'snapshots';

  var dbPromise = null;

  function getDb() {

    if (dbPromise) return dbPromise;

    dbPromise = new Promise(function (resolve, reject) {

      try {

        var req = indexedDB.open(DB_NAME, 1);

        req.onupgradeneeded = function (e) {

          var db = e.target.result;

          if (!db.objectStoreNames.contains(STORE)) {

            db.createObjectStore(STORE, { keyPath: 'profileId' });

          }

        };

        req.onsuccess = function (e) { resolve(e.target.result); };

        req.onerror = function (e) { reject(e.target.error); };

      } catch (e) { reject(e); }

    });

    return dbPromise;

  }

  function save(profileId, data) {

    if (!profileId) return Promise.resolve(false);

    return getDb().then(function (db) {

      return new Promise(function (resolve) {

        try {

          var tx = db.transaction(STORE, 'readwrite');

          tx.objectStore(STORE).put({ profileId: profileId, at: Date.now(), data: data });

          tx.oncomplete = function () { resolve(true); };

          tx.onerror = function () { resolve(false); };

        } catch (e) { resolve(false); }

      });

    }).catch(function () { return false; });

  }

  function load(profileId) {

    if (!profileId) return Promise.resolve(null);

    return getDb().then(function (db) {

      return new Promise(function (resolve) {

        try {

          var tx = db.transaction(STORE, 'readonly');

          var req = tx.objectStore(STORE).get(profileId);

          req.onsuccess = function () { resolve(req.result ? req.result.data : null); };

          req.onerror = function () { resolve(null); };

        } catch (e) { resolve(null); }

      });

    }).catch(function () { return null; });

  }

  function autoRestore() {

    try {

      var p = P4.Profiles.current();

      if (!p) return;

      var isEmpty = !workoutData || !workoutData.user || !workoutData.user.name ||

        !workoutData.workouts || workoutData.workouts.length === 0;

      if (isEmpty) {

        load(p.id).then(function (data) {

          if (data && data.user) {

            if (window.P4 && P4.replaceWorkoutData) { P4.replaceWorkoutData(data); }

            if (typeof saveWorkoutData === 'function') saveWorkoutData();

            try { P4.toast('Restored ' + p.name + ' from vault', 'ok', 3000); } catch (e) {}

            if (typeof renderDashboard === 'function') renderDashboard();

          }

        });

      }

    } catch (e) {}

  }

  function snapshotNow() {

    try {

      var p = P4.Profiles.current();

      if (p) save(p.id, workoutData);

    } catch (e) {}

  }

  function init() {

    try {

      var origSave = window.saveWorkoutData;

      if (typeof origSave === 'function' && !origSave._vaultHooked) {

        var hooked = function () {

          var result = origSave.apply(this, arguments);

          try { snapshotNow(); } catch (e) {}

          return result;

        };

        hooked._vaultHooked = true;

        window.saveWorkoutData = hooked;

      }

    } catch (e) {}

    setTimeout(autoRestore, 800);

  }

  return { save: save, load: load, autoRestore: autoRestore, snapshotNow: snapshotNow, init: init };

})();



/* ---- P4.Picker: profile picker overlay UI ---- */

P4.Picker = (function () {

  var OPEN_FLAG = false;

  function refreshNavbar() {

    try {

      var p = P4.Profiles.current();

      if (!p) return;

      var nameEl = document.getElementById('userNameDisplay');

      if (nameEl) {

        var cleanName = SEC.escapeHtml(p.name);

        var em = p.emoji ? '<span style="margin-right:4px">' + p.emoji + '</span>' : '';

        nameEl.innerHTML = em + cleanName;

        var chev = document.getElementById('p4PickerChevron');

        if (!chev) {

          chev = document.createElement('i');

          chev.id = 'p4PickerChevron';

          chev.className = 'fas fa-chevron-down';

          chev.style.cssText = 'font-size:10px;margin-left:6px;opacity:0.6';

          nameEl.appendChild(chev);

        }

      }

    } catch (e) {}

  }

  function open() {

    if (OPEN_FLAG) return;

    OPEN_FLAG = true;

    var host = document.createElement('div');

    host.id = 'p4PickerBackdrop';

    host.style.cssText = 'position:fixed;inset:0;z-index:100000;background:rgba(0,0,0,0.55);display:flex;align-items:center;justify-content:center;padding:20px;-webkit-backdrop-filter:blur(2px);backdrop-filter:blur(2px)';

    host.addEventListener('click', function (e) {

      if (e.target === host) close();

    });

    var ps = P4.Profiles.list();

    var cur = P4.Profiles.current();

    var cards = ps.map(function (p) {

      var isCur = cur && cur.id === p.id;

      return '<div class="p4-pick-card" data-id="' + p.id + '" style="display:flex;align-items:center;gap:12px;padding:14px 16px;border-radius:14px;background:' + (isCur ? 'var(--p4-accent-soft, #eef)' : 'var(--light, #fff)') + ';border:1px solid var(--gray-200, #e2e8f0);cursor:pointer;margin-bottom:8px;transition:all .15s">' +

        '<div style="font-size:28px;width:40px;text-align:center">' + (p.emoji || '') + '</div>' +

        '<div style="flex:1"><div style="font-weight:600;font-size:15px;color:var(--p4-text, #1e293b)">' + SEC.escapeHtml(p.name) + '</div>' +

        '<div style="font-size:11px;color:var(--p4-text-3, #94a3b8)">Created ' + P4.fmtDate(p.createdAt) + '</div></div>' +

        (isCur ? '<div style="font-size:11px;font-weight:700;color:var(--p4-accent, #2563eb)">CURRENT</div>' : '') +

        '<button class="p4-pick-del" data-id="' + p.id + '" style="background:transparent;border:none;color:var(--danger, #ef4444);cursor:pointer;font-size:16px;padding:4px 8px" title="Delete"><i class="fas fa-trash"></i></button>' +

        '</div>';

    }).join('');

    host.innerHTML =

      '<div id="p4PickerCard" style="background:var(--light, #fff);border-radius:18px;padding:22px;max-width:480px;width:100%;max-height:80vh;overflow:auto;box-shadow:0 20px 60px rgba(0,0,0,0.3)">' +

      '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:14px">' +

      '<h3 style="margin:0;font-size:17px;font-weight:700;color:var(--p4-text, #1e293b)">Switch profile</h3>' +

      '<button id="p4PickerClose" style="background:transparent;border:none;font-size:22px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-xmark"></i></button>' +

      '</div>' +

      '<div id="p4PickerList">' + (cards || '<p style="color:var(--p4-text-3, #94a3b8);font-size:14px;text-align:center;padding:20px">No profiles yet</p>') + '</div>' +

      '<div style="display:flex;gap:8px;margin-top:14px">' +

      '<input id="p4PickerNewName" type="text" placeholder="New profile name..." maxlength="40" style="flex:1;padding:9px 12px;border:1px solid var(--gray-300, #cbd5e1);border-radius:10px;font-size:14px;background:var(--light, #fff);color:var(--p4-text, #1e293b)">' +

      '<button id="p4PickerAdd" style="padding:9px 14px;background:var(--primary, #2563eb);color:white;border:none;border-radius:10px;cursor:pointer;font-weight:600"><i class="fas fa-plus"></i> Add</button>' +

      '</div>' +

      '<p style="font-size:11px;color:var(--p4-text-3, #94a3b8);margin:12px 0 0 0;text-align:center">Each profile keeps its own workouts, history, and settings.</p>' +

      '</div>';

    document.body.appendChild(host);

    document.getElementById('p4PickerClose').addEventListener('click', close);

    document.getElementById('p4PickerAdd').addEventListener('click', function () {

      var name = document.getElementById('p4PickerNewName').value.trim();

      if (!name) { try { P4.toast('Give the new profile a name first', 'warn', 2000); } catch (e) {} return; }

      var p = P4.Profiles.create(name);

      close();

      setTimeout(function () {

        P4.Profiles.switchTo(p.id);

        try {

          workoutData = { user: { name: name }, workouts: [], exercises: {}, settings: {} };

          if (typeof saveWorkoutData === 'function') saveWorkoutData();

          if (typeof renderDashboard === 'function') renderDashboard();

        } catch (e) {}

        try { if (P4.Onboard.needed()) setTimeout(function () { P4.Onboard.start(); }, 500); } catch (e) {}

      }, 80);

    });

    var listEl = document.getElementById('p4PickerList');

    listEl.addEventListener('click', function (e) {

      var del = e.target.closest('.p4-pick-del');

      if (del) {

        e.stopPropagation();

        var id = del.getAttribute('data-id');

        if (confirm('Delete this profile and all its data?')) {

          P4.Profiles.del(id);

          close();

          setTimeout(open, 60);

        }

        return;

      }

      var card = e.target.closest('.p4-pick-card');

      if (!card) return;

      var id = card.getAttribute('data-id');

      /* single-tap pick → dismiss → switch in one flow */

      close();

      setTimeout(function () { P4.Profiles.switchTo(id); }, 60);

    });

    document.addEventListener('keydown', function esc(e) {

      if (e.key === 'Escape') { close(); document.removeEventListener('keydown', esc); }

    });

    setTimeout(function () { var i = document.getElementById('p4PickerNewName'); if (i) i.focus(); }, 100);

  }

  function close() {

    var el = document.getElementById('p4PickerBackdrop');

    if (el && el.parentNode) el.parentNode.removeChild(el);

    OPEN_FLAG = false;

  }

  function init() {

    setTimeout(function () {

      /* ============ P4-APK-LATER:BEGIN ==============================

         Name tap -> Profile Picker (ADD-NEW) is DISABLED on purpose.

         Reason: name tap should fall through to .nav-brand <a>,

         which routes to Dashboard.

         To re-enable for a future APK build:

           remove the line containing only   slash-star

           remove the line containing only   star-slash

         ============================================================== */

      /*

      // profile name (userNameDisplay) opens the picker

      var nameEl = document.getElementById('userNameDisplay');

      if (nameEl) {

        nameEl.style.cursor = 'pointer';

        nameEl.addEventListener('click', function (e) {

          e.preventDefault();

          e.stopPropagation();

          open();

        });

      }

      */

      /* ============ P4-APK-LATER:END ================================ */

      /* streak fire + count opens the dashboard (home page) */

      var streakEl = document.querySelector('.streak-fire');

      if (streakEl) {

        streakEl.style.cursor = 'pointer';

        streakEl.addEventListener('click', function (e) {

          /* don't fire if the streak is dead and the user is trying to recover */

          if (streakEl.dataset.intensity === 'dead' && e.target.closest('.streak-fire')) {

            /* dead streak → recovery modal handled by updateStreakFire's own listener */

            return;

          }

          e.preventDefault();

          e.stopPropagation();

          try { if (typeof showSection === 'function') showSection('dashboard'); } catch (err) {}

        });

      }

    }, 1500);

    refreshNavbar();

  }

  return { open: open, close: close, init: init, refreshNavbar: refreshNavbar };

})();





/* ============================================================================

 * PRO TRIAL COUNTER — 21-workout trial

 * ============================================================================ */

var TRIAL_WORKOUTS = 21;

var TRIAL_COUNT_KEY = 'p4_trial_workouts_v2';



function getTrialWorkoutsUsed() {

  try { var v = localStorage.getItem(TRIAL_COUNT_KEY); return v ? parseInt(v, 10) : 0; }

  catch (e) { return 0; }

}

function syncTrialWorkoutCount() {

  try {

    var count = (typeof workoutData !== 'undefined' && workoutData && workoutData.workouts) ? workoutData.workouts.length : 0;

    localStorage.setItem(TRIAL_COUNT_KEY, String(count));

    console.log('[Trial] count synced: ' + count + ' / ' + TRIAL_WORKOUTS);

    return count;

  } catch (e) { return getTrialWorkoutsUsed(); }

}

window.__pfPaywall = { TRIAL_WORKOUTS: TRIAL_WORKOUTS, getTrialWorkoutsUsed: getTrialWorkoutsUsed, syncTrialWorkoutCount: syncTrialWorkoutCount };



/* ============================================================================

 * P4.PAYWALL v2 — migrated from custom-404-page (c404) strategy

 * Both free + paid tiers get SAME access. Free requires social-media DM

 * application. Paid uses Stripe via the brain-paywall Cloudflare Worker.

 * Worker URL: https://brain-paywall.example-user.workers.dev

 * ============================================================================ */

(function () {

  'use strict';

  if (!window.P4) return;

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  /* worker + trial + cache constants (mirrors c404 exactly) */

  var WORKER_URL      = 'https://brain-paywall.example-user.workers.dev';

  var TRIAL_KEY       = 'p4_trial_start_v2';

  var SUB_CACHE_KEY   = 'p4_sub_cache_v2';

  var SUB_EXPIRES_KEY = 'p4_sub_expires_at_v2';

  var APP_ID_KEY      = 'p4_app_id';

  var APP_LIST_KEY    = 'p4_applications';

  var APPROVED_KEY    = 'p4_approved';

  var TRIAL_DAYS      = 7;

  var CACHE_TTL_MS    = 3 * 24 * 60 * 60 * 1000;

  var DAY              = 24 * 60 * 60 * 1000;



  /* social platforms — all point to try4ever handles (mirrors c404) */

  var SOCIALS = [

    { name: "Instagram",   icon: "https://cdn.simpleicons.org/instagram",  dmUrl: "https://www.instagram.com/try4ever/" },

    { name: "Twitter / X", icon: "https://cdn.simpleicons.org/x",          dmUrl: "https://twitter.com/messages/compose?recipient_id=try4ever" },

    { name: "TikTok",      icon: "https://cdn.simpleicons.org/tiktok",     dmUrl: "https://www.tiktok.com/@try4ever" },

    { name: "Facebook",    icon: "https://cdn.simpleicons.org/facebook",   dmUrl: "https://www.facebook.com/try4ever" },

    { name: "LinkedIn",    icon: "https://cdn.simpleicons.org/linkedin",   dmUrl: "https://www.linkedin.com/in/try4ever" },

    { name: "Snapchat",    icon: "https://cdn.simpleicons.org/snapchat",   dmUrl: "https://www.snapchat.com/add/try4ever" },

    { name: "Pinterest",   icon: "https://cdn.simpleicons.org/pinterest",  dmUrl: "https://www.pinterest.com/try4ever/" },

    { name: "Reddit",      icon: "https://cdn.simpleicons.org/reddit",     dmUrl: "https://www.reddit.com/user/try4ever" },

    { name: "Discord",     icon: "https://cdn.simpleicons.org/discord",    dmUrl: "https://discord.com/users/try4ever" },

    { name: "Telegram",    icon: "https://cdn.simpleicons.org/telegram",   dmUrl: "https://t.me/try4ever" },

    { name: "Threads",     icon: "https://cdn.simpleicons.org/threads",    dmUrl: "https://www.threads.net/@try4ever" },

    { name: "Mastodon",    icon: "https://cdn.simpleicons.org/mastodon",   dmUrl: "https://mastodon.social/@try4ever" },

    { name: "Bluesky",     icon: "https://cdn.simpleicons.org/bluesky",    dmUrl: "https://bsky.app/profile/try4ever" },

    { name: "YouTube",     icon: "https://cdn.simpleicons.org/youtube",    dmUrl: "https://www.youtube.com/@try4ever" },

    { name: "Twitch",      icon: "https://cdn.simpleicons.org/twitch",     dmUrl: "https://www.twitch.tv/try4ever" },

    { name: "Other",       icon: "https://cdn.simpleicons.org/whatever",   dmUrl: "mailto:apply@try4ever.com" }

  ];



  /* ---- state helpers ---- */

  function getUserId() {

    var id = null;

    try { id = localStorage.getItem(APP_ID_KEY); } catch (e) {}

    if (!id) {

      id = 'PF-' + Math.random().toString(36).substring(2, 10).toUpperCase()

         + '-' + Date.now().toString(36).toUpperCase();

      try { localStorage.setItem(APP_ID_KEY, id); } catch (e) {}

    }

    return id;

  }

  function getTrialStart() {

    var v = null;

    try { v = localStorage.getItem(TRIAL_KEY); } catch (e) {}

    return v ? parseInt(v, 10) : null;

  }

  function startTrial() {

    if (!getTrialStart()) {

      try { localStorage.setItem(TRIAL_KEY, String(Date.now())); } catch (e) {}

    }

  }

  function isTrialActive() {

    var s = getTrialStart();

    return !!s && (Date.now() - s) < TRIAL_DAYS * DAY;

  }

  function getTrialDaysLeft() {

    var s = getTrialStart();

    if (!s) return TRIAL_DAYS;

    return Math.max(0, Math.ceil((TRIAL_DAYS * DAY - (Date.now() - s)) / DAY));

  }

  function getCachedStatus() {

    try {

      var raw = localStorage.getItem(SUB_CACHE_KEY);

      return raw ? JSON.parse(raw) : null;

    } catch (e) { return null; }

  }

  function setCachedStatus(s) {

    try {

      localStorage.setItem(SUB_CACHE_KEY, JSON.stringify({

        active: !!s.active, key: s.key || null, timestamp: Date.now()

      }));

    } catch (e) {}

  }

  function getSubscriptionExpiry() {

    try { var r = localStorage.getItem(SUB_EXPIRES_KEY); return r ? parseInt(r, 10) : null; } catch (e) { return null; }

  }

  function setSubscriptionExpiry(ts) {

    try { if (ts) localStorage.setItem(SUB_EXPIRES_KEY, String(ts)); else localStorage.removeItem(SUB_EXPIRES_KEY); } catch (e) {}

  }

  function isApprovedLocal() {

    try { return localStorage.getItem(APPROVED_KEY) === 'true'; } catch (e) { return false; }

  }

  function setApprovedLocal(v) {

    try { if (v) localStorage.setItem(APPROVED_KEY, 'true'); else localStorage.removeItem(APPROVED_KEY); } catch (e) {}

  }

  function getApplicationId() {

    var id = null;

    try { id = localStorage.getItem(APP_ID_KEY); } catch (e) {}

    if (!id) { id = getUserId(); }

    return id;

  }



  /* ---- worker calls ---- */

  async function checkStatus() {

    var userId = getUserId();

    try {

      var res = await fetch(WORKER_URL + '/api/status?userId=' + encodeURIComponent(userId));

      if (res.ok) {

        var data = await res.json();

        setCachedStatus(data);

        return data;

      }

    } catch (e) {}

    var cached = getCachedStatus();

    if (cached && (Date.now() - cached.timestamp) < CACHE_TTL_MS) {

      return { active: cached.active };

    }

    return { active: false };

  }

  async function checkApplicationStatus(appId) {

    try {

      var res = await fetch(WORKER_URL + '/api/application-status?id=' + encodeURIComponent(appId));

      if (res.ok) return await res.json();

    } catch (e) {}

    return null;

  }

  async function submitApplication(platform) {

    try {

      var res = await fetch(WORKER_URL + '/api/apply', {

        method: 'POST',

        headers: { 'Content-Type': 'application/json' },

        body: JSON.stringify({

          applicationId: getApplicationId(),

          userId: getUserId(),

          platform: platform || 'pending'

        })

      });

      return res.ok;

    } catch (e) { return false; }

  }



  /* ---- the new Paywall module (overrides the old one) ---- */

  var Paywall = P4.Paywall = {

    config: {

      workerUrl: WORKER_URL,

      paymentUrl: 'https://try4ever.com/fitness',

      price: 19,

      trialDays: TRIAL_DAYS

    },

    SOCIALS: SOCIALS,



    state: function () { return store.get(P4.keys.paywall, { onboard: false }); },



    /* ---- access decision: same features for free + paid ---- */

    isPro: function () {

      /* either path unlocks everything */

      if (isApprovedLocal()) return true;

      if (Paywall.subActive()) return true;

      if (Paywall.trialActive()) return true;

      return false;

    },

    isAllowed: function () {

      var st = Paywall.state();

      if (!st.onboard) return true;

      return Paywall.isPro();

    },

    trialActive: function () { return isTrialActive(); },

    trialStart: function () { return getTrialStart(); },

    daysLeft: function () { return getTrialDaysLeft(); },

    ensureTrial: function () {

      if (!getTrialStart()) {

        startTrial();

        try { P4.toast('7-day trial started — full access. Apply for free or subscribe any time.', 'ok', 4200); } catch (e) {}

      }

    },

    subActive: function () {

      var exp = getSubscriptionExpiry();

      if (exp && Date.now() < exp) return true;

      var cache = getCachedStatus();

      if (cache && cache.active && cache.timestamp && (Date.now() - cache.timestamp < CACHE_TTL_MS)) return true;

      return false;

    },

    licenseValid: function () { return Paywall.subActive(); },  /* alias for compat */

    validateKey: function () { return { ok: false, error: 'License keys retired — use free apply or subscribe.' }; },

    activateLicense: function () { return { ok: false, error: 'License keys retired.' }; },

    deactivateLicense: function () {

      try { localStorage.removeItem(SUB_CACHE_KEY); } catch (e) {}

      try { localStorage.removeItem(SUB_EXPIRES_KEY); } catch (e) {}

    },

    markSubscription: function (days) {

      var d = days || 30;

      setCachedStatus({ active: true, key: 'local', timestamp: Date.now() });

      setSubscriptionExpiry(Date.now() + d * DAY);

    },

    cancelSubscription: function () {

      var uid = getUserId();

      try {

        fetch(WORKER_URL + '/api/cancel?userId=' + encodeURIComponent(uid), { method: 'POST' }).catch(function () {});

      } catch (e) {}

      Paywall.deactivateLicense();

      try { P4.toast('Subscription cancelled. Apply for free or re-subscribe any time.', 'info', 3500); } catch (e) {}

    },



    /* ---- refresh from worker ---- */

    refreshFromWorker: async function () {

      var st = await checkStatus();

      if (st && st.active && st.expiresAt) {

        setSubscriptionExpiry(st.expiresAt);

        setCachedStatus({ active: true, key: st.key || null, timestamp: Date.now() });

      }

      if (st && st.approved) setApprovedLocal(true);

      return st;

    },



    /* ---- main modal ---- */

    showFullPage: function (reason) {

      if (Paywall.isAllowed()) return;

      var existing = document.getElementById('p4PaywallModal');

      if (existing) existing.parentNode.removeChild(existing);

      var modal = document.createElement('div');

      modal.id = 'p4PaywallModal';

      modal.style.cssText = 'position:fixed;inset:0;z-index:100001;background:rgba(0,0,0,0.55);display:flex;align-items:center;justify-content:center;padding:20px;-webkit-backdrop-filter:blur(2px);backdrop-filter:blur(2px)';

      modal.addEventListener('click', function (e) { if (e.target === modal) Paywall.close(); });

      var trial = isTrialActive();

      var days = getTrialDaysLeft();

      var head = trial ? ('Trial: ' + days + ' day' + (days === 1 ? '' : 's') + ' left')

                        : 'Your trial has ended';

      modal.innerHTML =

        '<div class="pw-modal-inner" style="background:var(--light, #fff);border-radius:18px;padding:24px;max-width:460px;width:100%;max-height:90vh;overflow:auto;box-shadow:0 20px 60px rgba(0,0,0,0.3)">' +

        '<button id="p4PaywallClose" style="position:absolute;top:14px;right:14px;background:transparent;border:none;font-size:22px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-xmark"></i></button>' +

        '<div style="text-align:center;margin-bottom:18px">' +

          '<div style="font-size:36px;margin-bottom:6px">💪</div>' +

          '<h3 style="margin:0;font-size:20px;font-weight:700;color:var(--p4-text, #1e293b)">' + head + '</h3>' +

          '<p style="margin:6px 0 0 0;font-size:14px;color:var(--p4-text-2, #475569)">Pick your path — both give the same full access.</p>' +

        '</div>' +

        '<div style="display:grid;gap:12px">' +

          '<button id="p4PwApplyFree" style="display:flex;align-items:center;gap:12px;padding:14px 16px;background:var(--p4-accent-soft, #eef);border:1px solid var(--p4-accent-line, #c7d2fe);border-radius:14px;cursor:pointer;text-align:left;transition:all .15s">' +

            '<div style="font-size:24px">📣</div>' +

            '<div><div style="font-weight:700;color:var(--p4-text, #1e293b);font-size:15px">Apply for free</div>' +

            '<div style="font-size:12px;color:var(--p4-text-2, #475569)">Follow us on social + send a quick DM. We approve within a day.</div></div>' +

          '</button>' +

          '<button id="p4PwSubscribe" style="display:flex;align-items:center;gap:12px;padding:14px 16px;background:var(--light, #fff);border:1px solid var(--gray-200, #e2e8f0);border-radius:14px;cursor:pointer;text-align:left;transition:all .15s">' +

            '<div style="font-size:24px">⚡</div>' +

            '<div><div style="font-weight:700;color:var(--p4-text, #1e293b);font-size:15px">Subscribe — $19/mo</div>' +

            '<div style="font-size:12px;color:var(--p4-text-2, #475569)">Skip the queue. Cancel any time, keep full access until period ends.</div></div>' +

          '</button>' +

        '</div>' +

        '<p style="font-size:11px;color:var(--p4-text-3, #94a3b8);margin:16px 0 0 0;text-align:center">Both paths unlock every feature. No gated tools, no hidden tiers.</p>' +

        '</div>';

      document.body.appendChild(modal);

      document.getElementById('p4PaywallClose').addEventListener('click', Paywall.close);

      document.getElementById('p4PwApplyFree').addEventListener('click', function () { Paywall.openFreeWizard(); });

      document.getElementById('p4PwSubscribe').addEventListener('click', function () { Paywall.openPaidModal(); });

    },



    close: function () {

      var el = document.getElementById('p4PaywallModal');

      if (el && el.parentNode) el.parentNode.removeChild(el);

    },



    /* ---- free-access wizard (3 steps, mirrors c404) ---- */

    freeStep: 1,

    selectedPlatform: null,

    openFreeWizard: function () {

      Paywall.close();

      var existing = document.getElementById('p4FreeWizard');

      if (existing) existing.parentNode.removeChild(existing);

      Paywall.freeStep = 1;

      Paywall.selectedPlatform = null;

      Paywall.renderFreeStep();

    },

    renderFreeStep: function () {

      var existing = document.getElementById('p4FreeWizard');

      if (existing) existing.parentNode.removeChild(existing);

      var modal = document.createElement('div');

      modal.id = 'p4FreeWizard';

      modal.style.cssText = 'position:fixed;inset:0;z-index:100002;background:rgba(0,0,0,0.6);display:flex;align-items:center;justify-content:center;padding:20px';

      modal.addEventListener('click', function (e) { if (e.target === modal) Paywall.closeFreeWizard(); });

      var content;

      if (Paywall.freeStep === 1) {

        var opts = SOCIALS.map(function (s) {

          return '<button class="pw-soc-pick" data-name="' + SEC.escapeHtml(s.name) + '" style="display:flex;align-items:center;gap:10px;padding:10px 12px;background:var(--light, #fff);border:1px solid var(--gray-200, #e2e8f0);border-radius:12px;cursor:pointer;text-align:left;font-size:14px;color:var(--p4-text, #1e293b);font-weight:600;transition:all .15s">' +

            '<img src="' + s.icon + '" alt="" style="width:22px;height:22px" onerror="this.style.visibility=\'hidden\'">' +

            '<span>' + SEC.escapeHtml(s.name) + '</span></button>';

        }).join('');

        content =

          '<div class="pw-modal-inner" style="background:var(--light, #fff);border-radius:18px;padding:24px;max-width:460px;width:100%;max-height:90vh;overflow:auto;position:relative">' +

          '<button onclick="P4.Paywall.closeFreeWizard()" style="position:absolute;top:14px;right:14px;background:transparent;border:none;font-size:22px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-xmark"></i></button>' +

          '<h3 style="margin:0 0 6px 0;font-size:18px;font-weight:700;color:var(--p4-text, #1e293b)">Pick a platform</h3>' +

          '<p style="margin:0 0 14px 0;font-size:13px;color:var(--p4-text-2, #475569)">Where can you DM us? Pick whichever you use most.</p>' +

          '<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px">' + opts + '</div>' +

          '<button onclick="P4.Paywall.closeFreeWizard()" style="margin-top:14px;background:transparent;border:none;color:var(--p4-text-3, #94a3b8);cursor:pointer;font-size:13px;width:100%;text-align:center">Cancel</button>' +

          '</div>';

      } else if (Paywall.freeStep === 2) {

        var id = getApplicationId();

        var plat = Paywall.selectedPlatform ? SOCIALS.find(function (s) { return s.name === Paywall.selectedPlatform; }) : SOCIALS[0];

        var isOther = plat && plat.name === 'Other';

        var msg = isOther ?

          ('Pro ID: ' + id + '\nI want free access.\nI will attach an organic video I made.\nI grant permission to post it on your channels.\nThis message wasn\'t edited.\nI will not send it twice.') :

          ('Pro ID: ' + id + '\nI want free access.\nThis message wasn\'t edited.\nI will not send it twice.');

        var action = isOther ? 'Open email' : ('Copy & DM on ' + plat.name);

        content =

          '<div class="pw-modal-inner" style="background:var(--light, #fff);border-radius:18px;padding:24px;max-width:460px;width:100%;position:relative">' +

          '<button onclick="P4.Paywall.goBack()" style="position:absolute;top:14px;left:14px;background:transparent;border:none;font-size:18px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-arrow-left"></i></button>' +

          '<h3 style="margin:0 0 6px 0;font-size:18px;font-weight:700;color:var(--p4-text, #1e293b)">' + (isOther ? 'Apply by email' : ('DM us on ' + plat.name)) + '</h3>' +

          '<p style="margin:0 0 14px 0;font-size:13px;color:var(--p4-text-2, #475569)">' + (isOther ? 'Send the email below to apply.' : 'Copy the message, then DM us. Paste it there and send.') + '</p>' +

          '<div style="background:var(--gray-100, #f1f5f9);border:1px solid var(--gray-200, #e2e8f0);border-radius:12px;padding:12px;margin-bottom:14px">' +

            '<div style="font-size:11px;font-weight:700;letter-spacing:.08em;color:var(--p4-text-3, #94a3b8);text-transform:uppercase;margin-bottom:6px">Your application message</div>' +

            '<pre id="p4AppMsg" style="margin:0;font-family:inherit;font-size:12px;white-space:pre-wrap;color:var(--p4-text, #1e293b)">' + SEC.escapeHtml(msg) + '</pre>' +

          '</div>' +

          '<button id="p4CopyDm" style="width:100%;padding:11px;background:var(--primary, #2563eb);color:white;border:none;border-radius:10px;cursor:pointer;font-weight:600;font-size:14px">' + action + '</button>' +

          '<button onclick="P4.Paywall.finishFreeApplication()" style="margin-top:8px;width:100%;padding:11px;background:transparent;border:1px solid var(--gray-300, #cbd5e1);border-radius:10px;cursor:pointer;font-size:13px;color:var(--p4-text-2, #475569)">I\'ve sent it →</button>' +

          '</div>';

      } else if (Paywall.freeStep === 3) {

        var id2 = getApplicationId();

        content =

          '<div class="pw-modal-inner" style="background:var(--light, #fff);border-radius:18px;padding:24px;max-width:460px;width:100%;text-align:center;position:relative">' +

          '<div style="font-size:48px;margin-bottom:8px">📬</div>' +

          '<h3 style="margin:0 0 6px 0;font-size:18px;font-weight:700;color:var(--p4-text, #1e293b)">Application received</h3>' +

          '<p style="margin:0 0 14px 0;font-size:13px;color:var(--p4-text-2, #475569)">You\'re in the review queue. We\'ll respond via DM when we get to it — usually within a day.</p>' +

          '<div style="background:var(--gray-100, #f1f5f9);border:1px solid var(--gray-200, #e2e8f0);border-radius:12px;padding:10px;margin-bottom:14px">' +

            '<div style="font-size:11px;font-weight:700;letter-spacing:.08em;color:var(--p4-text-3, #94a3b8);text-transform:uppercase;margin-bottom:4px">Your application ID</div>' +

            '<div style="font-size:13px;font-family:monospace;color:var(--p4-text, #1e293b)">' + SEC.escapeHtml(id2) + '</div>' +

          '</div>' +

          '<button id="p4RefreshApp" onclick="P4.Paywall.refreshApplicationStatus()" style="width:100%;padding:11px;background:transparent;border:1px solid var(--gray-300, #cbd5e1);border-radius:10px;cursor:pointer;font-size:13px;margin-bottom:8px">↻ Refresh status</button>' +

          '<button onclick="P4.Paywall.closeFreeWizard()" style="width:100%;padding:11px;background:var(--primary, #2563eb);color:white;border:none;border-radius:10px;cursor:pointer;font-weight:600;font-size:14px">Close</button>' +

          '<button onclick="P4.Paywall.withdrawApplication()" style="margin-top:8px;background:transparent;border:none;color:var(--danger, #ef4444);cursor:pointer;font-size:12px">Withdraw application</button>' +

          '</div>';

      }

      modal.innerHTML = content;

      document.body.appendChild(modal);

      if (Paywall.freeStep === 1) {

        modal.querySelectorAll('.pw-soc-pick').forEach(function (btn) {

          btn.addEventListener('click', function () {

            Paywall.selectedPlatform = btn.getAttribute('data-name');

            Paywall.freeStep = 2;

            Paywall.renderFreeStep();

          });

        });

      } else if (Paywall.freeStep === 2) {

        document.getElementById('p4CopyDm').addEventListener('click', function () {

          Paywall.copyAndOpenDM();

        });

      }

    },

    closeFreeWizard: function () {

      var el = document.getElementById('p4FreeWizard');

      if (el && el.parentNode) el.parentNode.removeChild(el);

    },

    goBack: function () {

      Paywall.freeStep = 1;

      Paywall.selectedPlatform = null;

      Paywall.renderFreeStep();

    },

    copyAndOpenDM: function () {

      var plat = Paywall.selectedPlatform ? SOCIALS.find(function (s) { return s.name === Paywall.selectedPlatform; }) : SOCIALS[0];

      var id = getApplicationId();

      var isOther = plat && plat.name === 'Other';

      var msg = isOther ?

          ('Pro ID: ' + id + '\nI want free access.\nI will attach an organic video I made.\nI grant permission to post it on your channels.\nThis message wasn\'t edited.\nI will not send it twice.') :

          ('Pro ID: ' + id + '\nI want free access.\nThis message wasn\'t edited.\nI will not send it twice.');

      var msgEl = document.getElementById('p4AppMsg');

      var selectMsg = function () {

        if (!msgEl) return;

        var range = document.createRange();

        range.selectNodeContents(msgEl);

        var sel = window.getSelection();

        sel.removeAllRanges();

        sel.addRange(range);

      };

      if (isOther) {

        var subject = encodeURIComponent('Free access application — ' + id);

        var body = encodeURIComponent(msg);

        var url = 'mailto:apply@try4ever.com?subject=' + subject + '&body=' + body;

        var openEmail = function () { try { window.location.href = url; } catch (e) {} try { P4.toast('Email client opened', 'info', 2000); } catch (e) {} };

        if (navigator.clipboard && navigator.clipboard.writeText) {

          navigator.clipboard.writeText(msg).then(openEmail).catch(function () { selectMsg(); openEmail(); });

        } else { selectMsg(); openEmail(); }

        return;

      }

      var openDM = function () {

        var w = null;

        try { w = window.open(plat.dmUrl, '_blank'); } catch (e) {}

        if (!w) {

          try { P4.toast('Popup blocked — long-press the link below to open manually', 'warn', 3500); } catch (e) {}

        } else {

          try { P4.toast('DM opened — paste the message and send', 'ok', 3000); } catch (e) {}

        }

      };

      if (navigator.clipboard && navigator.clipboard.writeText) {

        navigator.clipboard.writeText(msg).then(openDM).catch(function () { selectMsg(); openDM(); });

      } else { selectMsg(); openDM(); }

    },

    finishFreeApplication: async function () {

      /* local record */

      try {

        var stored = JSON.parse(localStorage.getItem(APP_LIST_KEY) || '[]');

        var id = getApplicationId();

        var existing = stored.find(function (a) { return a.id === id; });

        if (existing) {

          existing.platform = Paywall.selectedPlatform || 'pending';

          existing.updatedAt = Date.now();

        } else {

          stored.push({ id: id, platform: Paywall.selectedPlatform || 'pending', status: 'pending', createdAt: Date.now(), expiresAt: Date.now() + 7 * DAY });

        }

        localStorage.setItem(APP_LIST_KEY, JSON.stringify(stored));

      } catch (e) {}

      /* notify worker (fails silently if /api/apply unreachable) */

      await submitApplication(Paywall.selectedPlatform);

      Paywall.freeStep = 3;

      Paywall.renderFreeStep();

    },

    refreshApplicationStatus: async function () {

      var id = getApplicationId();

      try { P4.toast('Checking status…', 'info', 1500); } catch (e) {}

      var r = await checkApplicationStatus(id);

      if (r && r.status === 'approved') {

        setApprovedLocal(true);

        try { P4.toast('Approved! You have full access.', 'ok', 3500); } catch (e) {}

        Paywall.closeFreeWizard();

        Paywall.close();

      } else if (r && r.status === 'rejected') {

        try { P4.toast('Not approved — you can reapply', 'warn', 3500); } catch (e) {}

        Paywall.freeStep = 1;

        Paywall.selectedPlatform = null;

        Paywall.renderFreeStep();

      } else {

        try { P4.toast('Still pending — check back later', 'info', 2500); } catch (e) {}

      }

    },

    withdrawApplication: function () {

      try {

        var stored = JSON.parse(localStorage.getItem(APP_LIST_KEY) || '[]');

        var id = getApplicationId();

        localStorage.setItem(APP_LIST_KEY, JSON.stringify(stored.filter(function (a) { return a.id !== id; })));

        setApprovedLocal(false);

      } catch (e) {}

      Paywall.freeStep = 1;

      Paywall.selectedPlatform = null;

      Paywall.closeFreeWizard();

      try { P4.toast('Application withdrawn', 'info', 2000); } catch (e) {}

    },



    /* ---- paid modal (Stripe via worker) ---- */

    openPaidModal: function () {

      Paywall.close();

      var existing = document.getElementById('p4PaidModal');

      if (existing) existing.parentNode.removeChild(existing);

      var modal = document.createElement('div');

      modal.id = 'p4PaidModal';

      modal.style.cssText = 'position:fixed;inset:0;z-index:100002;background:rgba(0,0,0,0.6);display:flex;align-items:center;justify-content:center;padding:20px';

      modal.addEventListener('click', function (e) { if (e.target === modal) Paywall.closePaidModal(); });

      modal.innerHTML =

        '<div class="pw-modal-inner" style="background:var(--light, #fff);border-radius:18px;padding:24px;max-width:460px;width:100%;position:relative;text-align:center">' +

        '<button onclick="P4.Paywall.closePaidModal()" style="position:absolute;top:14px;right:14px;background:transparent;border:none;font-size:22px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-xmark"></i></button>' +

        '<div style="font-size:36px;margin-bottom:6px">⚡</div>' +

        '<h3 style="margin:0 0 6px 0;font-size:18px;font-weight:700;color:var(--p4-text, #1e293b)">Subscribe — $19/mo</h3>' +

        '<p style="margin:0 0 14px 0;font-size:13px;color:var(--p4-text-2, #475569)">Skip the queue. Cancel any time. Keep full access until the period ends.</p>' +

        '<button id="p4PwCheckout" style="width:100%;padding:12px;background:var(--primary, #2563eb);color:white;border:none;border-radius:10px;cursor:pointer;font-weight:600;font-size:15px">Continue to checkout</button>' +

        '<button onclick="P4.Paywall.openFreeWizard()" style="margin-top:8px;background:transparent;border:none;color:var(--p4-accent, #2563eb);cursor:pointer;font-size:13px;text-decoration:underline;width:100%">Or apply for free instead</button>' +

        '</div>';

      document.body.appendChild(modal);

      document.getElementById('p4PwCheckout').addEventListener('click', function () { Paywall.startCheckout(); });

    },

    closePaidModal: function () {

      var el = document.getElementById('p4PaidModal');

      if (el && el.parentNode) el.parentNode.removeChild(el);

    },

    startCheckout: async function () {

      var btn = document.getElementById('p4PwCheckout');

      if (btn) { btn.disabled = true; btn.textContent = 'Starting checkout…'; }

      try {

        var res = await fetch(WORKER_URL + '/api/create-payment-intent', {

          method: 'POST',

          headers: { 'Content-Type': 'application/json' },

          body: JSON.stringify({ userId: getUserId(), amount: 1900 })

        });

        if (res.ok) {

          var data = await res.json();

          if (data.checkoutUrl) {

            window.location.href = data.checkoutUrl;

            return;

          }

        }

      } catch (e) {}

      /* fallback: open the static payment page */

      try { window.location.href = Paywall.config.paymentUrl; } catch (e) {}

    },



    /* ---- UI badge refresh (called by settings + navbar) ---- */

    refreshUI: function () {

      var badge = document.getElementById('p4PaywallPill');

      if (!badge) return;

      if (Paywall.subActive()) {

        badge.innerHTML = '<i class="fas fa-crown"></i> Pro';

        badge.style.background = 'linear-gradient(135deg, #fbbf24, #f59e0b)';

      } else if (isApprovedLocal()) {

        badge.innerHTML = '<i class="fas fa-heart"></i> Free (approved)';

        badge.style.background = 'linear-gradient(135deg, #10b981, #059669)';

      } else if (isTrialActive()) {

        var d = getTrialDaysLeft();

        badge.innerHTML = '<i class="fas fa-hourglass-half"></i> Trial: ' + d + 'd left';

        badge.style.background = 'linear-gradient(135deg, #06b6d4, #0891b2)';

      } else {

        badge.innerHTML = '<i class="fas fa-lock"></i> Choose path';

        badge.style.background = 'linear-gradient(135deg, #94a3b8, #64748b)';

      }

    },



    /* ---- wire overlay buttons ---- */

    wire: function (host, reason) {

      if (!host) return;

      var closeBtn = host.querySelector('#p4PaywallClose, [data-paywall-close]');

      if (closeBtn) closeBtn.addEventListener('click', Paywall.close);

    },



    init: function () {

      /* start trial on first run */

      if (!getTrialStart()) startTrial();

      /* try to refresh from worker (async, non-blocking) */

      setTimeout(function () { Paywall.refreshFromWorker().catch(function () {}); }, 1500);

      /* refresh UI badge if present */

      try { Paywall.refreshUI(); } catch (e) {}

      console.log('[P4.Paywall v2] online — worker:', WORKER_URL);

    }

  };



  /* expose helpers for inline onclick handlers */

  P4.Paywall.close = Paywall.close;

})();





/* ============================================================================

 * P4 STAGE-3 MODULES — streak fire decay, lift auditor, settings accordion

 * ============================================================================ */



/* ---- updateStreakFire: fire emoji decay + social recovery at streak=0 ---- */

function updateStreakFire(streak) {

  try {

    var el = document.querySelector('.streak-fire');

    if (!el) return;

    /* compute days since last workout */

    var daysSince = 99;

    try {

      if (typeof workoutData !== 'undefined' && workoutData.workouts && workoutData.workouts.length) {

        var last = workoutData.workouts

          .filter(function (w) { return w && w.date; })

          .map(function (w) { return new Date(w.date).getTime(); })

          .sort(function (a, b) { return b - a; })[0];

        if (last) daysSince = Math.floor((Date.now() - last) / (24 * 60 * 60 * 1000));

      }

    } catch (e) {}



    /* fire intensity: full → dim → smoke → dead */

    var intensity;

    if (streak === 0) intensity = 'dead';

    else if (daysSince <= 2) intensity = 'full';

    else if (daysSince <= 4) intensity = 'dim';

    else if (daysSince <= 6) intensity = 'smoke';

    else intensity = 'dead';



    /* build the fire emoji string based on intensity */

    var fireChar;

    if (intensity === 'full') fireChar = '🔥';

    else if (intensity === 'dim') fireChar = '🔥';

    else if (intensity === 'smoke') fireChar = '🌫️';

    else fireChar = '❄️';



    /* apply visual: opacity + clickable when dead */

    el.style.opacity = intensity === 'full' ? '1' : (intensity === 'dim' ? '0.7' : (intensity === 'smoke' ? '0.5' : '0.4'));

    el.style.cursor = streak === 0 ? 'pointer' : 'pointer';  /* always clickable — opens dashboard when alive, recovery when dead */

    el.dataset.intensity = intensity;  /* used by picker init to decide dashboard vs recovery */

    el.title = streak === 0 ? 'Streak lost — tap to share + recover' : (daysSince > 2 ? 'Last workout ' + daysSince + 'd ago — fire fading — tap for dashboard' : 'Tap for dashboard');



    /* replace just the fire emoji (keep the count span intact) */

    var countEl = document.getElementById('streakCount');

    if (countEl) {

      el.innerHTML = '';

      var f = document.createElement('span');

      f.textContent = fireChar + ' ';

      f.style.marginRight = '2px';

      el.appendChild(f);

      el.appendChild(countEl);

    }



    /* one-shot wire: clicking dead streak opens share-to-recover modal */

    if (!el._streakRecoverWired) {

      el._streakRecoverWired = true;

      el.addEventListener('click', function () {

        if (el.dataset.intensity === 'dead') openStreakRecoveryModal();

      });

    }

  } catch (e) {}

}



/* ---- openStreakRecoveryModal: share to social → bump streak back to 1 (no verification) ---- */

function openStreakRecoveryModal() {

  try {

    var existing = document.getElementById('p4StreakRecoverModal');

    if (existing) existing.parentNode.removeChild(existing);

    var m = document.createElement('div');

    m.id = 'p4StreakRecoverModal';

    m.style.cssText = 'position:fixed;inset:0;z-index:100003;background:rgba(0,0,0,0.6);display:flex;align-items:center;justify-content:center;padding:20px';

    m.addEventListener('click', function (e) { if (e.target === m) m.parentNode.removeChild(m); });

    var shareText = encodeURIComponent('Just crushed my workout with PeakForm 💪 — back at it today. Join me: https://try4ever.com/fitness');

    var platforms = [

      { name: 'Twitter / X', icon: 'fab fa-x-twitter', url: 'https://twitter.com/intent/tweet?text=' + shareText },

      { name: 'Facebook',   icon: 'fab fa-facebook', url: 'https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent('https://try4ever.com/fitness') },

      { name: 'WhatsApp',   icon: 'fab fa-whatsapp', url: 'https://wa.me/?text=' + shareText },

      { name: 'Telegram',   icon: 'fab fa-telegram-plane', url: 'https://t.me/share/url?url=' + encodeURIComponent('https://try4ever.com/fitness') + '&text=' + shareText },

      { name: 'Reddit',     icon: 'fab fa-reddit-alien', url: 'https://www.reddit.com/submit?url=' + encodeURIComponent('https://try4ever.com/fitness') + '&title=' + shareText },

      { name: 'Email',      icon: 'fas fa-envelope', url: 'mailto:?subject=' + encodeURIComponent('My workout today') + '&body=' + shareText }

    ];

    var btns = platforms.map(function (p) {

      return '<a href="' + p.url + '" target="_blank" rel="noopener" class="p4-share-btn" data-platform="' + p.name + '" style="display:flex;align-items:center;gap:10px;padding:10px 12px;background:var(--light, #fff);border:1px solid var(--gray-200, #e2e8f0);border-radius:12px;cursor:pointer;text-decoration:none;color:var(--p4-text, #1e293b);font-weight:600;font-size:13px;transition:all .15s">' +

        '<i class="' + p.icon + '" style="font-size:18px;color:var(--p4-accent, #2563eb)"></i>' +

        '<span>' + p.name + '</span></a>';

    }).join('');

    m.innerHTML =

      '<div style="background:var(--light, #fff);border-radius:18px;padding:24px;max-width:440px;width:100%;position:relative;text-align:center">' +

      '<button onclick="this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode)" style="position:absolute;top:14px;right:14px;background:transparent;border:none;font-size:22px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-xmark"></i></button>' +

      '<div style="font-size:36px;margin-bottom:6px">❄️</div>' +

      '<h3 style="margin:0 0 6px 0;font-size:18px;font-weight:700;color:var(--p4-text, #1e293b)">Streak lost</h3>' +

      '<p style="margin:0 0 14px 0;font-size:13px;color:var(--p4-text-2, #475569)">Share your workout today to relight the fire. No verification — sharing is on the honor system. Pick where:</p>' +

      '<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px">' + btns + '</div>' +

      '<button id="p4StreakRecoverDone" style="margin-top:14px;width:100%;padding:11px;background:var(--primary, #2563eb);color:white;border:none;border-radius:10px;cursor:pointer;font-weight:600;font-size:14px">I shared it — relight my fire 🔥</button>' +

      '</div>';

    document.body.appendChild(m);

    /* honor-system: clicking any share button OR the "I shared it" button bumps streak to 1 */

    m.querySelectorAll('.p4-share-btn').forEach(function (a) {

      a.addEventListener('click', function () {

        setTimeout(function () { bumpStreakAfterShare(); }, 800);

      });

    });

    document.getElementById('p4StreakRecoverDone').addEventListener('click', bumpStreakAfterShare);

  } catch (e) {}

}



function bumpStreakAfterShare() {

  try {

    /* honor system: bump streak to 1, no verification */

    if (typeof workoutData === 'undefined') return;

    if (!workoutData.user) workoutData.user = {};

    if (!workoutData.user.settings) workoutData.user.settings = {};

    /* record the share event */

    workoutData.user.settings.lastStreakRecoverAt = Date.now();

    /* mark today as a workout day so streak calculation picks it up */

    var today = new Date().toISOString().slice(0, 10);

    if (!workoutData.workouts) workoutData.workouts = [];

    var already = workoutData.workouts.some(function (w) {

      return w && w.date && new Date(w.date).toISOString().slice(0, 10) === today;

    });

    if (!already) {

      workoutData.workouts.push({

        id: 'streak-recover-' + Date.now(),

        date: today,

        type: 'streak_recover',

        exercises: [],

        name: 'Streak recovery share'

      });

    }

    if (typeof saveWorkoutData === 'function') saveWorkoutData();

    /* close modal */

    var m = document.getElementById('p4StreakRecoverModal');

    if (m && m.parentNode) m.parentNode.removeChild(m);

    /* update UI */

    safeTextUpdate('streakCount', 1);

    if (typeof updateStreakFire === 'function') updateStreakFire(1);

    try { P4.toast('Fire relit 🔥 — keep it going tomorrow!', 'ok', 3000); } catch (e) {}

    if (typeof renderDashboard === 'function') renderDashboard();

  } catch (e) {}

}



/* ---- P4.Auditor: flag misaligned 1RMs (e.g. dumbbell curl = barbell curl) ---- */

P4.Auditor = (function () {

  /* Heuristic ratio table: bilateral barbell > bilateral dumbbell (per hand) > unilateral

     These are population-average ratios; flag entries that deviate by >15%. */

  var RATIOS = {

    /* {primaryId: {comparisonId: expectedRatio}} — ratio = other / this */

    'barbell_curl':    { 'dumbbell_curl': 1.35 },   /* barbell curl ~35% heavier than 1-hand dumbbell curl */

    'dumbbell_curl':   { 'barbell_curl': 0.74 },

    'barbell_bench_press': { 'dumbbell_bench_press': 1.20 },

    'dumbbell_bench_press': { 'barbell_bench_press': 0.83 },

    'barbell_row':     { 'dumbbell_row': 1.30 },

    'dumbbell_row':    { 'barbell_row': 0.77 },

    'barbell_shoulder_press': { 'dumbbell_shoulder_press': 1.20 },

    'dumbbell_shoulder_press': { 'barbell_shoulder_press': 0.83 },

    'barbell_squat':   { 'leg_press': 0.55 },        /* leg press much heavier */

    'leg_press':       { 'barbell_squat': 1.82 }

  };



  function scan() {

    var flags = [];

    try {

      if (typeof workoutData === 'undefined' || !workoutData.exercises) return flags;

      var ex = workoutData.exercises;

      /* for each pair in RATIOS, compare tested 1RMs */

      Object.keys(RATIOS).forEach(function (idA) {

        var recA = ex[idA];

        if (!recA || !recA.tested1RM || recA.tested1RM <= 0) return;

        Object.keys(RATIOS[idA]).forEach(function (idB) {

          var recB = ex[idB];

          if (!recB || !recB.tested1RM || recB.tested1RM <= 0) return;

          var expectedRatio = RATIOS[idA][idB]; /* B / A */

          var actualRatio = recB.tested1RM / recA.tested1RM;

          var deviation = Math.abs(actualRatio - expectedRatio) / expectedRatio;

          if (deviation > 0.15) {

            flags.push({

              a: { id: idA, name: getExerciseName(idA), tested1RM: recA.tested1RM, testDate: recA.testDate },

              b: { id: idB, name: getExerciseName(idB), tested1RM: recB.tested1RM, testDate: recB.testDate },

              expectedRatio: expectedRatio,

              actualRatio: actualRatio,

              deviation: Math.round(deviation * 100),

              severity: deviation > 0.30 ? 'high' : (deviation > 0.20 ? 'medium' : 'low')

            });

          }

        });

      });

    } catch (e) {}

    return flags;

  }



  function getExerciseName(id) {

    try {

      if (typeof getExerciseById === 'function') {

        var ex = getExerciseById(id);

        if (ex && ex.name) return ex.name;

      }

    } catch (e) {}

    return id;

  }



  function recommend(flag) {

    /* Heuristic: trust the bilateral/heavier lift; the lighter one likely under-tested */

    var heavier = flag.a.tested1RM >= flag.b.tested1RM ? flag.a : flag.b;

    var lighter = flag.a.tested1RM < flag.b.tested1RM ? flag.a : flag.b;

    return 'Your ' + heavier.name + ' 1RM (' + Math.round(heavier.tested1RM) + ' lbs) vs ' +

           lighter.name + ' (' + Math.round(lighter.tested1RM) + ' lbs) — ' +

           'ratio is ' + flag.actualRatio.toFixed(2) + ' but expected ~' + flag.expectedRatio.toFixed(2) + '. ' +

           'Consider re-testing ' + lighter.name + ' (likely under-tested).';

  }



  function report() {

    var flags = scan();

    if (!flags.length) {

      try { P4.toast('No lift misalignments detected ✓', 'ok', 2500); } catch (e) {}

      return;

    }

    /* render a modal report */

    var existing = document.getElementById('p4AuditorReport');

    if (existing) existing.parentNode.removeChild(existing);

    var m = document.createElement('div');

    m.id = 'p4AuditorReport';

    m.style.cssText = 'position:fixed;inset:0;z-index:100003;background:rgba(0,0,0,0.6);display:flex;align-items:center;justify-content:center;padding:20px';

    m.addEventListener('click', function (e) { if (e.target === m) m.parentNode.removeChild(m); });

    var cards = flags.map(function (f) {

      var color = f.severity === 'high' ? '#ef4444' : (f.severity === 'medium' ? '#f59e0b' : '#06b6d4');

      return '<div style="border-left:4px solid ' + color + ';background:var(--gray-100, #f1f5f9);padding:12px 14px;border-radius:8px;margin-bottom:8px">' +

        '<div style="font-weight:700;color:var(--p4-text, #1e293b);font-size:13px;margin-bottom:4px">' +

          SEC.escapeHtml(f.a.name) + ' (' + Math.round(f.a.tested1RM) + ' lbs) ↔ ' + SEC.escapeHtml(f.b.name) + ' (' + Math.round(f.b.tested1RM) + ' lbs)' +

          ' <span style="color:' + color + ';font-size:11px">' + f.deviation + '% off</span>' +

        '</div>' +

        '<div style="font-size:12px;color:var(--p4-text-2, #475569)">' + SEC.escapeHtml(recommend(f)) + '</div>' +

      '</div>';

    }).join('');

    m.innerHTML =

      '<div style="background:var(--light, #fff);border-radius:18px;padding:24px;max-width:560px;width:100%;max-height:85vh;overflow:auto;position:relative">' +

      '<button onclick="this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode)" style="position:absolute;top:14px;right:14px;background:transparent;border:none;font-size:22px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-xmark"></i></button>' +

      '<div style="display:flex;align-items:center;gap:10px;margin-bottom:14px">' +

        '<div style="font-size:28px">🔍</div>' +

        '<div><h3 style="margin:0;font-size:17px;font-weight:700;color:var(--p4-text, #1e293b)">Lift audit</h3>' +

        '<p style="margin:0;font-size:12px;color:var(--p4-text-3, #94a3b8)">' + flags.length + ' potential misalignment' + (flags.length === 1 ? '' : 's') + ' flagged</p></div>' +

      '</div>' +

      cards +

      '<p style="font-size:11px;color:var(--p4-text-3, #94a3b8);margin:14px 0 0 0;text-align:center">Nothing is changed — only flagged. Re-test to fix.</p>' +

      '</div>';

    document.body.appendChild(m);

  }



  return {

    scan: scan,

    report: report,

    recommend: recommend

  };

})();



/* ---- P4.SettingsAccordion: collapsible cards, one open at a time, all closed by default ---- */

P4.SettingsAccordion = (function () {

  function init() {

    setTimeout(function () {

      /* find the settings container */

      var host = document.getElementById('p4SettingsExtra');

      if (!host) { setTimeout(init, 800); return; }

      var cards = host.querySelectorAll('.card[id^="p4"]:not(.p4-settings-group)');

      if (!cards.length) return;



      /* close all by default + add header click toggle */

      cards.forEach(function (card, idx) {

        /* wrap existing content in a collapsible body */

        var h3 = card.querySelector('h3');

        if (!h3) return;

        if (!card._accordionWired) {

          card._accordionWired = true;

          /* collapse by default */

          card.classList.add('p4-accordion-collapsed');

          /* make header clickable */

          h3.style.cursor = 'pointer';

          h3.style.userSelect = 'none';

          h3.innerHTML = '<i class="fas fa-chevron-down p4-acc-chevron" style="margin-right:8px;transition:transform .2s;font-size:11px"></i>' + h3.innerHTML;

          h3.addEventListener('click', function (e) {

            /* don't toggle if user clicks a button/input inside the header */

            if (e.target.closest('button, input, select, label')) return;

            var wasCollapsed = card.classList.contains('p4-accordion-collapsed');

            /* close all siblings */

            cards.forEach(function (sib) {

              if (sib !== card) sib.classList.add('p4-accordion-collapsed');

            });

            /* toggle this one */

            if (wasCollapsed) card.classList.remove('p4-accordion-collapsed');

            else card.classList.add('p4-accordion-collapsed');

            /* rotate chevron */

            var chev = h3.querySelector('.p4-acc-chevron');

            if (chev) chev.style.transform = wasCollapsed ? 'rotate(0deg)' : 'rotate(-90deg)';

          });

        }

      });

    }, 600);

  }

  return { init: init };

})();



/* injected CSS for accordion + share buttons */

(function () {

  if (document.getElementById('p4-stage3-css')) return;

  var css = document.createElement('style');

  css.id = 'p4-stage3-css';

  css.textContent =

    '.p4-accordion-collapsed > *:not(h3) { display: none !important; }' +

    '.p4-accordion-collapsed h3 { margin: 0 !important; }' +

    '.p4-share-btn:hover { border-color: var(--p4-accent, #2563eb) !important; transform: translateY(-1px); }';

  document.head.appendChild(css);

})();





/* ============================================================================

 * P4 STAGE-4 MODULES — workout cards collapse, library streaming,

 * 3D wheel scroll, profile picker grid, fresh-load profile selection

 * ============================================================================ */



/* ---- P4.WorkoutCards: collapsible exercise cards in workout page ----

 * Wraps each .chart-card inside #exerciseList with collapse behavior.

 * Header (name + difficulty + chevron + 1RM-needed badge) always visible.

 * Body hidden by default. Only one expanded at a time.

 * For exercises without tested1RM: tapping expands to show 1RM test UI.

 */

P4.WorkoutCards = (function () {

  var wired = false;

  var MO = null;



  function wrapCards() {

    var list = document.getElementById('exerciseList');

    if (!list) return;

    var cards = list.querySelectorAll('.chart-card:not(.p4-wc-wrapped)');

    if (!cards.length) return;



    cards.forEach(function (card) {

      card.classList.add('p4-wc-wrapped');

      card.classList.add('p4-wc-collapsed');  /* collapsed by default */



      /* find the existing header row (name + difficulty) */

      var firstInner = card.querySelector(':scope > div > div:first-child');

      var nameH3 = card.querySelector('h3');

      var diff = card.querySelector('.badge');

      var diffHTML = diff ? diff.outerHTML : '';



      /* extract exercise id from any onclick inside */

      var exId = null;

      var btns = card.querySelectorAll('button[onclick]');

      for (var i = 0; i < btns.length; i++) {

        var m = btns[i].getAttribute('onclick').match(/'([^']+)'/);

        if (m) { exId = m[1]; break; }

      }



      /* check if 1RM test is needed */

      var needs1RM = false;

      try {

        if (exId && typeof workoutData !== 'undefined' && workoutData.exercises) {

          var rec = workoutData.exercises[exId];

          needs1RM = !rec || !rec.tested1RM || rec.tested1RM <= 0;

        }

      } catch (e) {}



      /* build a header bar (always visible) */

      var header = document.createElement('div');

      header.className = 'p4-wc-header';

      header.style.cssText = 'display:flex;justify-content:space-between;align-items:center;gap:10px;padding:8px 0;cursor:pointer;border-radius:8px';

      var name = nameH3 ? nameH3.textContent : 'Exercise';

      header.innerHTML =

        '<div style="display:flex;align-items:center;gap:8px;flex:1;min-width:0">' +

          '<i class="fas fa-chevron-down p4-wc-chevron" style="font-size:11px;transition:transform .2s;color:var(--p4-text-3, #94a3b8)"></i>' +

          '<span style="font-weight:700;color:var(--primary, #2563eb);font-size:1.1rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">' + (name || 'Exercise') + '</span>' +

          (needs1RM ? '<span class="badge badge-warning" style="font-size:10px">Needs 1RM</span>' : '') +

        '</div>' +

        '<div style="display:flex;gap:6px;align-items:center">' + diffHTML + '</div>';



      /* insert header as first child */

      if (card.firstChild) card.insertBefore(header, card.firstChild);

      else card.appendChild(header);



      /* hide the rest of the card content when collapsed */

      var body = document.createElement('div');

      body.className = 'p4-wc-body';

      /* move all subsequent children into body */

      while (header.nextSibling) body.appendChild(header.nextSibling);

      card.appendChild(body);



      /* wire header click to toggle */

      header.addEventListener('click', function (e) {

        /* don't toggle if user clicked a button/input inside the body (shouldn't happen since body is hidden, but just in case) */

        if (e.target.closest('button, input, select')) return;

        var wasCollapsed = card.classList.contains('p4-wc-collapsed');

        /* collapse all siblings */

        list.querySelectorAll('.p4-wc-wrapped').forEach(function (sib) {

          if (sib !== card) {

            sib.classList.add('p4-wc-collapsed');

            var chev = sib.querySelector('.p4-wc-chevron');

            if (chev) chev.style.transform = 'rotate(-90deg)';

          }

        });

        /* toggle this one */

        if (wasCollapsed) {

          card.classList.remove('p4-wc-collapsed');

          var chev = card.querySelector('.p4-wc-chevron');

          if (chev) chev.style.transform = 'rotate(0deg)';

          /* if 1RM needed, scroll to it + focus the test UI */

          if (needs1RM) {

            setTimeout(function () {

              var testBtn = card.querySelector('[onclick*="estimate1RM"], [onclick*="test1RM"], [onclick*="bracketingTest"]');

              if (testBtn) testBtn.scrollIntoView({ behavior: 'smooth', block: 'center' });

            }, 100);

          }

        } else {

          card.classList.add('p4-wc-collapsed');

          var chev2 = card.querySelector('.p4-wc-chevron');

          if (chev2) chev2.style.transform = 'rotate(-90deg)';

        }

      });



      /* set initial chevron rotation (collapsed = -90deg) */

      var chev = header.querySelector('.p4-wc-chevron');

      if (chev) chev.style.transform = 'rotate(-90deg)';

    });

  }



  function init() {

    /* observe #exerciseList for changes (when updateTodaysWorkout re-renders) */

    setTimeout(function () {

      var list = document.getElementById('exerciseList');

      if (!list) { setTimeout(init, 800); return; }

      if (MO) { try { MO.disconnect(); } catch (e) {} }

      MO = new MutationObserver(P4.debounce(function () { wrapCards(); }, 100));

      try { MO.observe(list, { childList: true }); } catch (e) {}

      wrapCards();  /* initial wrap */

    }, 800);

  }

  return { init: init, wrapCards: wrapCards };

})();



/* ---- Library streaming render — replaces the all-at-once renderLibrary ----

 * Override strategy: monkey-patch the existing renderLibrary.

 * New behavior: build group headers instantly (collapsed by default),

 * stream exercise cards underneath via rAF chunks when group is expanded.

 */

(function () {

  if (window._p4LibraryStreamingPatched) return;

  window._p4LibraryStreamingPatched = true;



  /* save the original renderLibrary for fallback / reference */

  var origRenderLibrary = window.renderLibrary;



  /* helper: build a single exercise card HTML (mirrors the original) */

  function buildCardHTML(ex) {

    var musclesList = ex.muscles ? ex.muscles.map(function (m) {

      return '<span class="muscle-tag clickable" data-muscle="' + m + '" onclick="showMuscleImage(this.dataset.muscle)">' + getMuscleDisplayName(m) + '</span>';

    }).join('') : '';

    var equipment = ex.equipment ? '<span style="font-size:0.8rem; color:var(--gray-500);"><i class="fas fa-tools"></i> ' + ex.equipment + '</span>' : '';

    var prescription = ex.defaultSets + ' sets × ' + ex.defaultReps;

    if (ex.prescriptionType === 'time') prescription = ex.defaultSets + ' sets × ' + (ex.defaultDuration || 60) + ' sec';

    else if (ex.prescriptionType === 'amrap') prescription += ' (AMRAP)';

    var record = (workoutData.exercises && workoutData.exercises[ex.id]) || {};

    var testInfo = '';

    if (record.tested1RM) testInfo = '<div style="font-size:0.8rem; color:var(--success); margin-top:5px;">🏆 Tested 1RM: ' + Math.round(record.tested1RM) + ' lbs</div>';

    var performedBadge = ex.performed ? '<span class="badge badge-success">✔ Done ' + ex.performedCount + 'x</span>' : '<span class="badge badge-info">New</span>';

    var difficultyBadge = '';

    if (ex.difficulty === 'easy') difficultyBadge = '<span class="badge badge-success">Easy</span>';

    else if (ex.difficulty === 'medium') difficultyBadge = '<span class="badge badge-warning">Medium</span>';

    else if (ex.difficulty === 'hard') difficultyBadge = '<span class="badge badge-danger">Hard</span>';

    var safeName = ex.name.replace(/'/g, "\\'");

    var est1RM = (record && record.mu && record.mu > 0) ? Math.round(record.mu) + ' lbs' : '—';

    var lastHTML = '';

    if (record && Array.isArray(record.history) && record.history.length > 0) {

      var lastEntry = record.history.slice().reverse().find(function (e) { return !e.skipped; });

      if (lastEntry && typeof lastEntry.weight === 'number') {

        var w = lastEntry.weight === 0 ? 'bodyweight' : lastEntry.weight.toFixed(1) + ' lbs';

        lastHTML = '<div style="font-size:0.9rem; color:var(--gray-600);">Last: ' + w + '</div>';

      }

    } else {

      lastHTML = '<div style="font-size:0.9rem; color:var(--gray-600);">Last: —</div>';

    }

    var nextHTML = '';

    if (record && record.nextWeight && typeof record.nextWeight === 'number') {

      nextHTML = '<div style="margin-top:4px; font-size:0.9rem; color:var(--success);">↑ Next: ' + record.nextWeight.toFixed(1) + '</div>';

    }

    return '<div class="chart-card hover-lift" style="padding: 20px; display: flex; flex-direction: column; justify-content: space-between;">' +

      '<div>' +

        '<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">' +

          '<h3 style="color: var(--primary); font-size:1.2rem; margin:0;">' + ex.name + '</h3>' +

          difficultyBadge +

        '</div>' +

        '<div style="margin-bottom: 10px;">' + musclesList + '</div>' +

        '<div style="margin-bottom: 8px;">' + equipment + '</div>' +

        testInfo +

        '<div style="font-weight:600; color:var(--dark);">📋 ' + prescription + '</div>' +

        '<div style="margin-top:10px; font-size:0.85rem; color:var(--gray-600);"><i class="fas fa-lightbulb"></i> ' + (ex.progression || 'Progressive overload recommended') + '</div>' +

      '</div>' +

      '<div style="margin-top: 15px; display: flex; flex-wrap: wrap; gap: 8px; align-items: center;">' +

        '<button class="btn btn-sm" style="flex:1;" onclick="showExerciseSearch(\'' + safeName + '\')"><i class="fas fa-search"></i> How to do</button>' +

        '<div class="btn btn-sm btn-outline" style="flex:1; text-align:center; pointer-events:none;"><i class="fas fa-chart-line"></i> Est. 1RM: ' + est1RM + '</div>' +

        '<button class="btn btn-sm btn-success" style="flex:1;" onclick="addExerciseToWorkoutFromLibrary(\'' + ex.id + "', '" + safeName + '\')"><i class="fas fa-plus"></i> Add to Workout</button>' +

      '</div>' +

      '<div style="margin-top: 8px; text-align: right;">' + performedBadge + lastHTML + nextHTML + '</div>' +

    '</div>';

  }



  /* streaming: build headers instantly, defer cards until expanded */

  function streamingRender() {

    var container = document.getElementById('libraryGrid');

    if (!container) return;

    var searchTerm = (document.getElementById('librarySearch') && document.getElementById('librarySearch').value || '').toLowerCase();

    var allExercises = getEnhancedExerciseList();

    if (!fuseIndex) {

      fuseIndex = new Fuse(allExercises, { keys: ['name', 'muscles', 'equipment', 'primaryMuscle'], threshold: 0.4, distance: 100, includeScore: true });

    }

    var filtered;

    if (searchTerm.length > 1) {

      filtered = fuseIndex.search(searchTerm).map(function (r) { return r.item; });

    } else {

      filtered = allExercises;

    }

    if (typeof mpLibraryFilteredList === 'function') filtered = mpLibraryFilteredList(filtered);



    var groups = {};

    filtered.forEach(function (ex) {

      var key;

      switch (currentGrouping) {

        case 'equipment': key = ex.equipment || 'Other'; break;

        case 'difficulty': key = ex.difficulty || 'medium'; break;

        case 'performed': key = ex.performed ? 'Done' : 'Not Done'; break;

        case 'muscleCategory':

          var muscleInfo = getAllMuscleGroups().find(function (m) { return m.name === ex.primaryMuscle; });

          key = muscleInfo ? muscleInfo.category : 'other';

          break;

        case 'component': {

          var mpInfo = (typeof mpGetLookup === 'function' ? mpGetLookup()[ex.id] : null);

          var mpComps = mpInfo ? mpInfo.components : (ex.fitnessComponents || []);

          var mpMeta = (typeof MP_COMPONENT_MAP !== 'undefined') ? MP_COMPONENT_MAP[mpComps[0]] : null;

          key = mpMeta ? mpMeta.short : 'Other';

          break;

        }

        default: key = ex.originalMuscleGroup;

      }

      if (!groups[key]) groups[key] = [];

      groups[key].push(ex);

    });

    var sortedKeys = Object.keys(groups).sort(function (a, b) {

      if (currentGrouping === 'performed') return (a === 'Not Done' ? -1 : 1);

      return a.localeCompare(b);

    });

    var countEl = document.getElementById('library-count');

    if (countEl) countEl.textContent = filtered.length;



    /* default all collapsed for streaming — instant paint of headers only */

    var html = '';

    if (!sortedKeys.length) {

      html = '<div class="empty-state" style="grid-column:1/-1;"><i class="fas fa-dumbbell"></i><h3>No exercises match your search</h3></div>';

      container.innerHTML = html;

      return;

    }

    sortedKeys.forEach(function (groupKey) {

      var exercisesInGroup = groups[groupKey];

      var groupId = 'group-' + groupKey.replace(/\s+/g, '_');

      /* ensure all groups start collapsed for fast initial paint */

      if (collapsedGroups[groupKey] === undefined) collapsedGroups[groupKey] = true;

      var isCollapsed = collapsedGroups[groupKey];

      html +=

        '<div class="library-group-header ' + (isCollapsed ? 'collapsed' : '') + '" onclick="toggleGroup(\'' + groupKey.replace(/'/g, "\\'") + '\')">' +

          '<i class="fas fa-chevron-down group-toggle"></i>' +

          '<h3>' + groupKey + ' (' + exercisesInGroup.length + ')</h3>' +

        '</div>' +

        '<div class="library-group-content" id="' + groupId + '" data-group-key="' + groupKey.replace(/"/g, '&quot;') + '"' +

          ' data-ex-ids="' + exercisesInGroup.map(function (e) { return e.id; }).join(',') + '"></div>';

    });

    container.innerHTML = html;



    /* stream cards into any expanded groups */

    Object.keys(collapsedGroups).forEach(function (key) {

      if (!collapsedGroups[key]) streamGroupCards(key);

    });



    /* refresh Expand/Collapse button visibility */

    if (typeof refreshLibraryExpandCollapseButtons === 'function') refreshLibraryExpandCollapseButtons();



    /* P4 FIX: badge every card (was: only expanded groups got badges) */

    if (typeof mpEnhanceLibraryCards === 'function') { try { mpEnhanceLibraryCards(); } catch (e) {} }

    /* re-init tooltips on muscle tags after streaming */

    setTimeout(function () { if (typeof initTooltips === 'function') initTooltips(); }, 50);

  }



  /* stream cards for a group in rAF chunks (20 at a time) */

  function streamGroupCards(groupKey) {

    var groupId = 'group-' + groupKey.replace(/\s+/g, '_');

    var contentEl = document.getElementById(groupId);

    if (!contentEl || contentEl.dataset.streamed === '1') return;

    contentEl.dataset.streamed = '1';

    var exIds = (contentEl.dataset.exIds || '').split(',').filter(Boolean);

    if (!exIds.length) return;

    var allExercises = getEnhancedExerciseList();

    var byId = {};

    allExercises.forEach(function (e) { byId[e.id] = e; });

    var queue = exIds.map(function (id) { return byId[id]; }).filter(Boolean);

    var CHUNK = 20;

    var idx = 0;

    function nextChunk() {

      var slice = queue.slice(idx, idx + CHUNK);

      if (!slice.length) return;

      var html = slice.map(buildCardHTML).join('');

      contentEl.insertAdjacentHTML('beforeend', html);

      idx += CHUNK;

      if (idx < queue.length) {

        requestAnimationFrame(nextChunk);

      } else {

        /* P4 FIX: badge the newly streamed cards */

        if (typeof mpEnhanceLibraryCards === 'function') { try { mpEnhanceLibraryCards(); } catch (e) {} }

        /* after streaming, wire any P4.WorkoutCards logic if applicable */

        setTimeout(function () { if (typeof initTooltips === 'function') initTooltips(); }, 20);

      }

    }

    requestAnimationFrame(nextChunk);

  }



  /* override the global renderLibrary */

  window.renderLibrary = streamingRender;

  /* expose streamGroupCards globally so toggleGroup can call it */

  window._p4StreamGroupCards = streamGroupCards;



  /* override toggleGroup to stream cards when expanding */

  if (typeof window.toggleGroup === 'function') {

    var origToggle = window.toggleGroup;

    window.toggleGroup = function (groupKey) {

      collapsedGroups[groupKey] = !collapsedGroups[groupKey];

      saveLibraryCollapse();

      if (!collapsedGroups[groupKey]) {

        /* expanding — stream cards */

        streamGroupCards(groupKey);

        /* update DOM: remove collapsed class from header, show content */

        var header = document.querySelector('.library-group-header h3');

        var headers = document.querySelectorAll('.library-group-header');

        headers.forEach(function (h) {

          var h3 = h.querySelector('h3');

          if (h3 && h3.textContent.indexOf(groupKey + ' (') === 0) {

            h.classList.remove('collapsed');

          }

        });

      } else {

        /* collapsing — clear the streamed cards to save memory */

        var groupId = 'group-' + groupKey.replace(/\s+/g, '_');

        var contentEl = document.getElementById(groupId);

        if (contentEl) {

          contentEl.innerHTML = '';

          contentEl.dataset.streamed = '';

        }

        var headers = document.querySelectorAll('.library-group-header');

        headers.forEach(function (h) {

          var h3 = h.querySelector('h3');

          if (h3 && h3.textContent.indexOf(groupKey + ' (') === 0) {

            h.classList.add('collapsed');

          }

        });

      }

      if (typeof refreshLibraryExpandCollapseButtons === 'function') refreshLibraryExpandCollapseButtons();

    };

  }



  /* override expandAllGroups to stream all */

  if (typeof window.expandAllGroups === 'function') {

    window.expandAllGroups = function () {

      collapsedGroups = {};

      saveLibraryCollapse();

      if (typeof refreshLibraryExpandCollapseButtons === 'function') refreshLibraryExpandCollapseButtons();

      /* re-render to update headers, then stream all */

      streamingRender();

    };

  }

})();



/* ---- P4.Wheel: 3D wheel scroll picker (for English level selection) ----

 * Center spotlight, top/bottom fade+shrink, looks like only 3 choices at a time.

 * Pure CSS + JS, no library.

 */

P4.Wheel = (function () {

  function create(opts) {

    /* opts: { items: [{id, name, desc}], value, onSelect, container } */

    var container = opts.container || document.body;

    var items = opts.items || [];

    var value = opts.value != null ? opts.value : (items.length ? items[0].id : null);

    var onSelect = opts.onSelect || function () {};



    var host = document.createElement('div');

    host.className = 'p4-wheel-host';

    host.style.cssText = 'position:relative;height:280px;overflow:hidden;display:flex;flex-direction:column;align-items:center;justify-content:center;perspective:1000px;touch-action:pan-y;-webkit-overflow-scrolling:touch;cursor:ns-resize';



    /* top + bottom fade masks */

    var topFade = document.createElement('div');

    topFade.style.cssText = 'position:absolute;top:0;left:0;right:0;height:40%;background:linear-gradient(to bottom, var(--light, #fff) 0%, transparent 100%);pointer-events:none;z-index:2';

    var botFade = document.createElement('div');

    botFade.style.cssText = 'position:absolute;bottom:0;left:0;right:0;height:40%;background:linear-gradient(to top, var(--light, #fff) 0%, transparent 100%);pointer-events:none;z-index:2';

    /* center selector line */

    var centerLine = document.createElement('div');

    centerLine.style.cssText = 'position:absolute;top:50%;left:0;right:0;height:60px;transform:translateY(-50%);border-top:2px solid var(--p4-accent, #2563eb);border-bottom:2px solid var(--p4-accent, #2563eb);background:var(--p4-accent-soft, #eef);pointer-events:none;z-index:1;opacity:0.5';



    var list = document.createElement('div');

    list.className = 'p4-wheel-list';

    list.style.cssText = 'position:relative;z-index:3;width:100%';



    host.appendChild(topFade);

    host.appendChild(botFade);

    host.appendChild(centerLine);

    host.appendChild(list);



    var itemEls = [];

    var currentIdx = 0;



    function findIdx(v) {

      for (var i = 0; i < items.length; i++) if (items[i].id === v) return i;

      return 0;

    }

    currentIdx = findIdx(value);



    function render() {

      list.innerHTML = '';

      itemEls = [];

      items.forEach(function (item, i) {

        var el = document.createElement('div');

        el.className = 'p4-wheel-item';

        el.dataset.idx = i;

        el.style.cssText = 'padding:14px 16px;text-align:center;transition:all .25s ease;will-change:transform,opacity;font-size:15px;cursor:pointer';

        el.innerHTML = '<div style="font-weight:700;color:var(--p4-text, #1e293b)">' + (item.name || '') + '</div>' +

          (item.desc ? '<div style="font-size:11px;color:var(--p4-text-3, #94a3b8);margin-top:2px">' + item.desc + '</div>' : '');

        el.addEventListener('click', function () {

          currentIdx = i;

          value = items[i].id;

          update();

          onSelect(items[i]);

        });

        list.appendChild(el);

        itemEls.push(el);

      });

      update();

    }



    function update() {

      var centerOffset = host.offsetHeight / 2 - 30; /* center of 60px line */

      itemEls.forEach(function (el, i) {

        var offset = i - currentIdx;

        var absOffset = Math.abs(offset);

        var translateY = (offset * 50); /* 50px per item */

        var scale = absOffset > 2 ? 0 : (1 - absOffset * 0.18);

        var opacity = absOffset > 2 ? 0 : (1 - absOffset * 0.35);

        var blur = absOffset > 0 ? Math.min(absOffset * 1.5, 3) : 0;

        el.style.transform = 'translateY(' + translateY + 'px) scale(' + scale + ')';

        el.style.opacity = opacity;

        el.style.filter = 'blur(' + blur + 'px)';

        el.style.pointerEvents = absOffset > 2 ? 'none' : 'auto';

      });

    }



    /* touch / wheel input */

    var startY = 0, startIdx = 0, lastY = 0, lastTime = 0, velocity = 0;

    function onTouchStart(e) {

      var t = e.touches ? e.touches[0] : e;

      startY = t.clientY;

      startIdx = currentIdx;

      lastY = startY;

      lastTime = Date.now();

      velocity = 0;

      host.setPointerCapture && e.pointerId != null && host.setPointerCapture(e.pointerId);

    }

    function onTouchMove(e) {

      e.preventDefault();

      var t = e.touches ? e.touches[0] : e;

      var dy = t.clientY - startY;

      /* 50px per item — move 1 item per 50px drag */

      var idxDelta = -Math.round(dy / 50);

      var newIdx = Math.max(0, Math.min(items.length - 1, startIdx + idxDelta));

      if (newIdx !== currentIdx) {

        currentIdx = newIdx;

        value = items[newIdx].id;

        update();

        onSelect(items[newIdx]);

      }

      /* track velocity for inertial scroll */

      var now = Date.now();

      var dt = now - lastTime;

      if (dt > 0) velocity = (t.clientY - lastY) / dt;

      lastY = t.clientY;

      lastTime = now;

    }

    function onTouchEnd() {

      /* inertial scroll */

      if (Math.abs(velocity) > 0.1) {

        var inertia = Math.round(velocity * 200 / 50);

        var target = Math.max(0, Math.min(items.length - 1, currentIdx - inertia));

        if (target !== currentIdx) {

          currentIdx = target;

          value = items[target].id;

          update();

          onSelect(items[target]);

        }

      }

    }

    host.addEventListener('touchstart', onTouchStart, { passive: true });

    host.addEventListener('touchmove', onTouchMove, { passive: false });

    host.addEventListener('touchend', onTouchEnd);

    host.addEventListener('mousedown', function (e) { onTouchStart(e); document.addEventListener('mousemove', onTouchMove); document.addEventListener('mouseup', function () { onTouchEnd(); document.removeEventListener('mousemove', onTouchMove); }); });

    host.addEventListener('wheel', function (e) {

      e.preventDefault();

      var dir = e.deltaY > 0 ? 1 : -1;

      var newIdx = Math.max(0, Math.min(items.length - 1, currentIdx + dir));

      if (newIdx !== currentIdx) {

        currentIdx = newIdx;

        value = items[newIdx].id;

        update();

        onSelect(items[newIdx]);

      }

    }, { passive: false });



    render();

    container.appendChild(host);

    return { host: host, setValue: function (v) { currentIdx = findIdx(v); value = v; update(); } };

  }

  return { create: create };

})();



/* ---- Profile picker grid + fresh-load selection ----

 * Override P4.Picker.open to use grid layout (tiny squares, 3-col grid)

 * On fresh APK load with no active profile, open picker immediately.

 */

(function () {

  if (window._p4PickerGridPatched) return;

  window._p4PickerGridPatched = true;

  if (!window.P4 || !P4.Picker) return;



  var origOpen = P4.Picker.open;

  P4.Picker.open = function () {

    if (P4.Picker._openFlag) return;

    P4.Picker._openFlag = true;

    var host = document.createElement('div');

    host.id = 'p4PickerBackdrop';

    host.style.cssText = 'position:fixed;inset:0;z-index:100000;background:rgba(0,0,0,0.55);display:flex;align-items:center;justify-content:center;padding:20px;-webkit-backdrop-filter:blur(2px);backdrop-filter:blur(2px)';

    host.addEventListener('click', function (e) { if (e.target === host) P4.Picker.close(); });

    var ps = P4.Profiles.list();

    var cur = P4.Profiles.current();

    /* GRID layout — tiny squares */

    var cards = ps.map(function (p) {

      var isCur = cur && cur.id === p.id;

      return '<div class="p4-pick-grid-card" data-id="' + p.id + '" style="display:flex;flex-direction:column;align-items:center;gap:6px;padding:14px 8px;border-radius:14px;background:' + (isCur ? 'var(--p4-accent-soft, #eef)' : 'var(--light, #fff)') + ';border:2px solid ' + (isCur ? 'var(--p4-accent, #2563eb)' : 'var(--gray-200, #e2e8f0)') + ';cursor:pointer;transition:all .15s;position:relative;min-height:96px;justify-content:center">' +

        '<div style="font-size:32px">' + (p.emoji || '') + '</div>' +

        '<div style="font-weight:600;font-size:12px;color:var(--p4-text, #1e293b);text-align:center;max-width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">' + SEC.escapeHtml(p.name) + '</div>' +

        (isCur ? '<div style="position:absolute;top:4px;right:4px;font-size:9px;font-weight:700;color:var(--p4-accent, #2563eb);background:var(--p4-accent-soft, #eef);padding:1px 6px;border-radius:8px">●</div>' : '') +

        '<button class="p4-pick-del" data-id="' + p.id + '" style="position:absolute;top:4px;left:4px;background:rgba(0,0,0,0.05);border:none;color:var(--danger, #ef4444);cursor:pointer;font-size:11px;padding:2px 4px;border-radius:6px" title="Delete"><i class="fas fa-trash"></i></button>' +

        '</div>';

    }).join('');

    host.innerHTML =

      '<div id="p4PickerCard" style="background:var(--light, #fff);border-radius:18px;padding:22px;max-width:520px;width:100%;max-height:85vh;overflow:auto;box-shadow:0 20px 60px rgba(0,0,0,0.3);position:relative">' +

      '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:14px">' +

      '<h3 style="margin:0;font-size:17px;font-weight:700;color:var(--p4-text, #1e293b)">Pick profile</h3>' +

      '<button id="p4PickerClose" style="background:transparent;border:none;font-size:22px;cursor:pointer;color:var(--p4-text-3, #94a3b8)"><i class="fas fa-xmark"></i></button>' +

      '</div>' +

      '<div id="p4PickerGrid" style="display:grid;grid-template-columns:repeat(3, 1fr);gap:10px;margin-bottom:14px">' + (cards || '<p style="color:var(--p4-text-3, #94a3b8);font-size:14px;grid-column:1/-1;text-align:center;padding:20px">No profiles yet — add one below</p>') + '</div>' +

      '<div style="display:flex;gap:8px">' +

      '<input id="p4PickerNewName" type="text" placeholder="New profile name..." maxlength="40" style="flex:1;padding:9px 12px;border:1px solid var(--gray-300, #cbd5e1);border-radius:10px;font-size:14px;background:var(--light, #fff);color:var(--p4-text, #1e293b)">' +

      '<button id="p4PickerAdd" style="padding:9px 14px;background:var(--primary, #2563eb);color:white;border:none;border-radius:10px;cursor:pointer;font-weight:600"><i class="fas fa-plus"></i> Add</button>' +

      '</div>' +

      '<p style="font-size:11px;color:var(--p4-text-3, #94a3b8);margin:12px 0 0 0;text-align:center">Each profile keeps its own workouts, history, and settings.</p>' +

      '</div>';

    document.body.appendChild(host);

    document.getElementById('p4PickerClose').addEventListener('click', P4.Picker.close);

    document.getElementById('p4PickerAdd').addEventListener('click', function () {

      var name = document.getElementById('p4PickerNewName').value.trim();

      if (!name) { try { P4.toast('Give the new profile a name first', 'warn', 2000); } catch (e) {} return; }

      var p = P4.Profiles.create(name);

      P4.Picker.close();

      setTimeout(function () {

        P4.Profiles.switchTo(p.id);

        try { workoutData = { user: { name: name }, workouts: [], exercises: {}, settings: {} }; if (typeof saveWorkoutData === 'function') saveWorkoutData(); if (typeof renderDashboard === 'function') renderDashboard(); } catch (e) {}

        try { if (P4.Onboard.needed()) setTimeout(function () { P4.Onboard.start(); }, 500); } catch (e) {}

      }, 80);

    });

    var grid = document.getElementById('p4PickerGrid');

    grid.addEventListener('click', function (e) {

      var del = e.target.closest('.p4-pick-del');

      if (del) {

        e.stopPropagation();

        var id = del.getAttribute('data-id');

        if (confirm('Delete this profile and all its data?')) { P4.Profiles.del(id); P4.Picker.close(); setTimeout(function () { P4.Picker.open(); }, 60); }

        return;

      }

      var card = e.target.closest('.p4-pick-grid-card');

      if (!card) return;

      var id = card.getAttribute('data-id');

      P4.Picker.close();

      setTimeout(function () { P4.Profiles.switchTo(id); }, 60);

    });

    document.addEventListener('keydown', function esc(e) { if (e.key === 'Escape') { P4.Picker.close(); document.removeEventListener('keydown', esc); } });

    setTimeout(function () { var i = document.getElementById('p4PickerNewName'); if (i) i.focus(); }, 100);

  };



  /* fresh-load profile selection — on first boot with no active profile, open picker BEFORE any profile UI */

  P4.Picker.showOnFreshLoad = function () {

    try {

      var ps = P4.Profiles.list();

      var active = localStorage.getItem('p4_active_profile');

      /* if no profiles OR no active profile, open picker on fresh load */

      if (!ps.length || !active) {

        setTimeout(function () { P4.Picker.open(); }, 1200);

        return true;

      }

    } catch (e) {}

    return false;

  };

})();



/* ---- CSS for stage-4 modules ---- */

(function () {

  if (document.getElementById('p4-stage4-css')) return;

  var css = document.createElement('style');

  css.id = 'p4-stage4-css';

  css.textContent =

    '.p4-wc-collapsed > .p4-wc-body { display: none !important; }' +

    '.p4-wc-collapsed { padding: 12px 20px !important; }' +

    '.p4-wc-header:hover { background: var(--gray-100, #f1f5f9); }' +

    '.p4-pick-grid-card:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }';

  document.head.appendChild(css);

})();





/* ============================================================================

 * P4 STAGE-5 MODULES — 3-month volatile projection + improved 5-year math

 * ============================================================================ */

(function () {

  'use strict';

  if (!window.P4) return;

  var P4 = window.P4;



  /* ---- calculate3MonthProjection: near-term, high-volatility ----

   * Uses rolling 8-workout trend + variance to project 3 months forward (~26 workouts at 2x/week).

   * Returns { low, median, high, message } */

  P4.Projections = P4.Projections || {};

  P4.Projections.threeMonth = function () {

    try {

      var workouts = (typeof workoutData !== 'undefined' && workoutData.workouts) ? workoutData.workouts : [];

      var exercises = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises : {};

      if (!workouts.length) return null;



      /* gather recent 1RM trend across key lifts */

      var keyLifts = ['squat', 'bench_press', 'deadlift'];

      var trendPoints = [];

      keyLifts.forEach(function (id) {

        var ex = exercises[id];

        if (!ex || !ex.history || !ex.history.length) return;

        /* take the last 8 entries with weight data */

        var recent = ex.history.slice(-8).filter(function (h) { return h && typeof h.weight === 'number' && h.weight > 0; });

        if (recent.length < 2) return;

        /* estimate 1RM per entry using Epley */

        recent.forEach(function (h) {

          var reps = h.reps || 1;

          var w = h.weight;

          var est1RM = w * (1 + reps / 30);

          trendPoints.push({ t: new Date(h.date || Date.now()).getTime(), v: est1RM, lift: id });

        });

      });

      if (trendPoints.length < 4) return null;



      /* sort by time */

      trendPoints.sort(function (a, b) { return a.t - b.t; });

      var firstT = trendPoints[0].t;

      var lastT = trendPoints[trendPoints.length - 1].t;

      var daySpan = Math.max(1, (lastT - firstT) / (24 * 60 * 60 * 1000));

      var firstV = trendPoints[0].v;

      var lastV = trendPoints[trendPoints.length - 1].v;

      var dailyRate = (lastV - firstV) / daySpan;  /* lbs per day */



      /* compute variance for volatility bands */

      var residuals = trendPoints.map(function (p) {

        var predicted = firstV + dailyRate * ((p.t - firstT) / (24 * 60 * 60 * 1000));

        return p.v - predicted;

      });

      var meanResidual = residuals.reduce(function (a, b) { return a + b; }, 0) / residuals.length;

      var variance = residuals.reduce(function (a, b) { return a + (b - meanResidual) * (b - meanResidual); }, 0) / residuals.length;

      var stdDev = Math.sqrt(variance);



      /* project 90 days forward */

      var projectionDays = 90;

      var projectedMedian = Math.max(lastV, lastV + dailyRate * projectionDays);

      /* volatility: ±1.5 stdDev scaled to projection horizon */

      var volatility = stdDev * 1.5 * Math.sqrt(projectionDays / daySpan);

      var low = Math.max(0, projectedMedian - volatility);

      var high = projectedMedian + volatility;



      /* sanity: cap unreasonable projections */

      var userWeight = (workoutData.user && workoutData.user.weight) || 150;

      var cap = userWeight * 4;  /* no one's 1RM is 4x bodyweight projected */

      if (high > cap) high = cap;

      if (projectedMedian > cap) projectedMedian = cap;



      /* build message */

      var msg;

      if (dailyRate > 0.5) msg = 'Gaining fast — keep the rhythm.';

      else if (dailyRate > 0.1) msg = 'Steady progress — solid gains ahead.';

      else if (dailyRate > -0.1) msg = 'Plateau-ish — consider a deload or variation swap.';

      else msg = 'Trend is down — deload + recovery focus.';



      return {

        low: Math.round(low),

        median: Math.round(projectedMedian),

        high: Math.round(high),

        message: msg,

        dailyRate: dailyRate

      };

    } catch (e) { return null; }

  };



  /* ---- improved 5-year projection: Monte Carlo over rolling trend + variance ----

   * 1000 simulated paths, returns median + 80% confidence interval */

  P4.Projections.fiveYearMonteCarlo = function () {

    try {

      var exercises = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises : {};

      var keyLifts = ['squat', 'bench_press', 'deadlift'];

      var trendPoints = [];

      keyLifts.forEach(function (id) {

        var ex = exercises[id];

        if (!ex || !ex.history || !ex.history.length) return;

        var recent = ex.history.slice(-12).filter(function (h) { return h && typeof h.weight === 'number' && h.weight > 0; });

        if (recent.length < 3) return;

        recent.forEach(function (h) {

          var reps = h.reps || 1;

          var w = h.weight;

          trendPoints.push({ t: new Date(h.date || Date.now()).getTime(), v: w * (1 + reps / 30) });

        });

      });

      if (trendPoints.length < 6) return null;

      trendPoints.sort(function (a, b) { return a.t - b.t; });



      var firstT = trendPoints[0].t;

      var lastT = trendPoints[trendPoints.length - 1].t;

      var daySpan = Math.max(1, (lastT - firstT) / (24 * 60 * 60 * 1000));

      var firstV = trendPoints[0].v;

      var lastV = trendPoints[trendPoints.length - 1].v;

      var dailyRate = (lastV - firstV) / daySpan;



      /* variance */

      var residuals = trendPoints.map(function (p) {

        var predicted = firstV + dailyRate * ((p.t - firstT) / (24 * 60 * 60 * 1000));

        return p.v - predicted;

      });

      var variance = residuals.reduce(function (a, b) { return a + b * b; }, 0) / residuals.length;

      var stdDev = Math.sqrt(variance);



      /* Monte Carlo: 1000 paths, 5 years = 1825 days */

      var HORIZON_DAYS = 1825;

      var NUM_PATHS = 1000;

      var finalValues = [];

      /* simple pseudo-random normal */

      function randNormal() {

        var u = 0, v = 0;

        while (u === 0) u = Math.random();

        while (v === 0) v = Math.random();

        return Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);

      }

      for (var i = 0; i < NUM_PATHS; i++) {

        var v = lastV;

        /* step daily, but only update every 7 days to speed up */

        for (var d = 0; d < HORIZON_DAYS; d += 7) {

          v += dailyRate * 7 + randNormal() * stdDev * Math.sqrt(7);

          /* apply diminishing returns: gains slow as you approach genetic ceiling */

          var ceiling = (workoutData.user && workoutData.user.weight || 150) * 3.5;

          if (v > ceiling * 0.9) {

            v = ceiling * 0.9 + (v - ceiling * 0.9) * 0.3;  /* taper */

          }

        }

        finalValues.push(v);

      }

      finalValues.sort(function (a, b) { return a - b; });

      var median = finalValues[Math.floor(NUM_PATHS / 2)];

      var low80 = finalValues[Math.floor(NUM_PATHS * 0.1)];

      var high80 = finalValues[Math.floor(NUM_PATHS * 0.9)];



      /* compute date 5 years out */

      var targetDate = new Date(Date.now() + 5 * 365 * 24 * 60 * 60 * 1000);



      return {

        medianDate: targetDate,

        median: Math.round(median),

        low80: Math.round(low80),

        high80: Math.round(high80),

        message: 'Monte Carlo over ' + trendPoints.length + ' trend points, 1000 simulated paths, 5-year horizon with diminishing returns near genetic ceiling.'

      };

    } catch (e) { return null; }

  };



  /* ---- render the 3-month projection card into the dashboard ----

   * Inserts a new stat card next to the eliteDateCard.

   * Called on dashboard render.

   */

  P4.Projections.renderCards = function () {

    try {

      /* find the eliteDateCard row */

      var eliteCard = document.getElementById('eliteDateCard');

      if (!eliteCard) return;  /* dashboard not yet rendered */

      var threeMonthCard = document.getElementById('threeMonthProjectionCard');

      if (!threeMonthCard) {

        /* create the card next to eliteDateCard */

        threeMonthCard = document.createElement('div');

        threeMonthCard.id = 'threeMonthProjectionCard';

        threeMonthCard.className = 'stat-card hover-lift';

        threeMonthCard.style.cssText = 'cursor: help; border-left: 3px solid var(--warning, #f59e0b);';

        threeMonthCard.innerHTML =

          '<div class="stat-value" id="threeMonthProjection" style="color: var(--warning, #f59e0b)">--</div>' +

          '<div class="stat-label" style="font-size:0.7rem;">' +

            '<i class="fas fa-bolt"></i> 3-month volatile projection<br>' +

            '<span style="font-size:0.6rem;">(near-term, high uncertainty)</span>' +

          '</div>';

        eliteCard.parentNode.insertBefore(threeMonthCard, eliteCard.nextSibling);

      }

      var proj = P4.Projections.threeMonth();

      var projEl = document.getElementById('threeMonthProjection');

      if (!projEl) return;

      if (!proj) {

        projEl.textContent = '—';

        threeMonthCard.setAttribute('title', 'Log a few more workouts to see your 3-month projection.');

        return;

      }

      projEl.textContent = proj.low + '–' + proj.high + ' lbs';

      var tipText = 'Based on your recent trend:\n• Median: ~' + proj.median + ' lbs in 90 days\n• Range: ' + proj.low + '–' + proj.high + ' lbs (80% confidence)\n• ' + proj.message;

      threeMonthCard.setAttribute('title', tipText);

      if (typeof tippy !== 'undefined') {

        if (threeMonthCard._tippy) threeMonthCard._tippy.destroy();

        tippy(threeMonthCard, { content: tipText, allowHTML: true, theme: 'light-border' });

      }



      /* also enrich the 5-year card tooltip with Monte Carlo bands if available */

      var mc = P4.Projections.fiveYearMonteCarlo();

      if (mc) {

        var eliteCard2 = document.getElementById('eliteDateCard');

        if (eliteCard2) {

          var mcTip = 'Monte Carlo (1000 paths):\n• Median: ~' + mc.median + ' lbs by ' + mc.medianDate.toLocaleDateString() + '\n• 80% range: ' + mc.low80 + '–' + mc.high80 + ' lbs\n• ' + mc.message;

          if (typeof tippy !== 'undefined') {

            if (eliteCard2._tippy) eliteCard2._tippy.destroy();

            tippy(eliteCard2, { content: mcTip, allowHTML: true, theme: 'light-border' });

          }

        }

      }

    } catch (e) {}

  };



  /* wire into dashboard render via MutationObserver */

  P4.Projections.init = function () {

    setTimeout(function () {

      /* try once after dashboard likely rendered */

      P4.Projections.renderCards();

      /* re-render on dashboard changes */

      var dash = document.getElementById('dashboard') || document.body;

      var mo = new MutationObserver(P4.debounce(function () {

        if (document.getElementById('eliteDateCard') && !document.getElementById('threeMonthProjectionCard')) {

          P4.Projections.renderCards();

        }

      }, 400));

      try { mo.observe(dash, { childList: true, subtree: true }); } catch (e) {}

    }, 1500);

  };

})();





/* ============================================================================

 * P4 STAGE-6 MODULES — settings restructure, 3D wheel wiring, workout cards

 * selector fix, export/backup improvements, component badge consistency.

 * ============================================================================ */

(function () {

  'use strict';

  if (!window.P4) return;

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  /* ============================================================

     1) SETTINGS RESTRUCTURE — 10 granular collapsible groups

     Each group: header (icon + title + chevron), body (collapsible)

     All closed by default. Only one open at a time.

     ============================================================ */

  if (P4.SettingsUI && P4.SettingsUI.inject) {

    var origInject = P4.SettingsUI.inject;

    P4.SettingsUI.inject = function () {

      var setSec = document.getElementById('settings-section');

      if (!setSec) return;

      /* idempotent: drop any previous injection */

      var old = document.getElementById('p4SettingsExtra');

      if (old && old.parentNode) old.parentNode.removeChild(old);

      var anchor = setSec.querySelector('.btn-group');

      var host = setSec.querySelector('.container') || setSec;

      var div = document.createElement('div');

      div.id = 'p4SettingsExtra';



      /* gather dynamic data */

      var langOpts = P4.LANGS.map(function (l) {

        return '<option value="' + l.code + '"' + (P4.I18N.get() === l.code ? ' selected' : '') + '>' + SEC.escapeHtml(l.name) + '</option>';

      }).join('');

      var plan = P4.Paywall;

      var planState = plan.isPro() ? 'Pro — everything unlocked. Thank you!' :

        plan.trialActive() ? 'Free trial: ' + plan.daysLeft() + ' day(s) left of full power.' : 'Free plan — apply for free access or subscribe.';

      var snaps = P4.Backup.snapshots();



      /* personal info: read current values */

      var userName = '', userBirth = '', userGender = 'unspecified', userWeight = '';

      try {

        if (typeof workoutData !== 'undefined' && workoutData.user) {

          userName = workoutData.user.name || '';

          userBirth = workoutData.user.birthDate || '';

          userGender = workoutData.user.gender || 'unspecified';

          userWeight = workoutData.user.weight || '';

        }

      } catch (e) {}



      /* helper: build a settings group card with header + collapsible body */

      function groupCard(id, title, icon, bodyHTML) {

        return '<div class="card p4-settings-group" id="' + id + '" style="margin-top:14px;padding:0;overflow:hidden;border:1px solid var(--p4-text-line, #e2e8f0);border-radius:14px">' +

          '<div class="p4-settings-group-header" style="display:flex;align-items:center;gap:10px;padding:14px 18px;cursor:pointer;user-select:none;background:var(--p4-bg-2, var(--gray-100, #f1f5f9));transition:background .15s">' +

            '<i class="fas ' + icon + '" style="color:var(--p4-accent, #2563eb);width:18px;text-align:center"></i>' +

            '<span style="flex:1;font-weight:600;font-size:15px;color:var(--p4-text, #1e293b);font-family:var(--p4-font-display, inherit)">' + title + '</span>' +

            '<i class="fas fa-chevron-down p4-settings-chevron" style="font-size:11px;color:var(--p4-text-3, #94a3b8);transition:transform .2s;transform:rotate(-90deg)"></i>' +

          '</div>' +

          '<div class="p4-settings-group-body" style="padding:16px 18px;display:none">' + bodyHTML + '</div>' +

        '</div>';

      }



      /* group bodies */

      var personalBody =

        '<div style="display:grid;gap:10px">' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Name <input type="text" id="p4SetName" maxlength="40" value="' + SEC.escapeHtml(userName) + '" style="flex:1;max-width:200px;padding:7px 10px;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;font-size:13px"></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Birth date <input type="date" id="p4SetBirth" value="' + SEC.escapeHtml(userBirth) + '" style="flex:1;max-width:200px;padding:7px 10px;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;font-size:13px"></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Gender <select id="p4SetGender" style="flex:1;max-width:200px;padding:7px 10px;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;font-size:13px;background:var(--light, #fff)">' +

          '<option value="unspecified"' + (userGender === 'unspecified' ? ' selected' : '') + '>Prefer not to say</option>' +

          '<option value="male"' + (userGender === 'male' ? ' selected' : '') + '>Male</option>' +

          '<option value="female"' + (userGender === 'female' ? ' selected' : '') + '>Female</option>' +

        '</select></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Body weight (lbs) <input type="number" id="p4SetWeight" min="50" max="500" value="' + SEC.escapeHtml(String(userWeight)) + '" style="flex:1;max-width:200px;padding:7px 10px;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;font-size:13px"></label>' +

        '</div>' +

        '<button class="btn btn-primary" id="p4SavePersonal" style="margin-top:12px;padding:8px 16px"><i class="fas fa-save"></i> Save</button>';



      var appearanceBody = '<div id="p4ThemeInner"></div>';

      var readingBody = '<div id="p4VocabInner"></div>';

      var languageBody =

        '<select id="p4LangSelect" style="width:100%;padding:8px 10px;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;font-size:13px;background:var(--light, #fff);color:var(--p4-text, #1e293b)">' + langOpts + '</select>' +

        '<div class="p4-wz-hint" style="margin-top:8px">Also works with your browser\'s "Translate this page" — labels, buttons and tips are structured for clean translation.</div>';



      var powerBody =

        '<div style="display:grid;gap:10px">' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Auto rest timer after logging effort <input type="checkbox" id="p4OptRest"' + (P4.Power.settings.autoRest ? ' checked' : '') + '></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Haptic buzz (vibration) <input type="checkbox" id="p4OptHaptic"' + (P4.Power.settings.haptics ? ' checked' : '') + '></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Swipe between pages <input type="checkbox" id="p4OptSwipe"' + (P4.Power.settings.swipe ? ' checked' : '') + '></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Quick-action dock <input type="checkbox" id="p4OptDock"' + (P4.Power.settings.dock ? ' checked' : '') + '></label>' +

        '</div>' +

        '<div class="p4-wz-hint" style="margin-top:10px">Designed for busy hands: Start Workout is 1 tap from Home, sets log with one entry, rest timer starts itself.</div>';



      var libMgmtBody =

        '<div style="display:flex;gap:8px;flex-wrap:wrap">' +

        '<button class="btn" onclick="P4.Studio.open()"><i class="fas fa-flask-vial"></i> Library Studio</button>' +

        '<button class="btn btn-success" onclick="P4.Backup.exportLibrary()"><i class="fas fa-file-export"></i> Export library</button>' +

        '<button class="btn" onclick="P4.Studio.pickImportFile()"><i class="fas fa-folder-open"></i> Import library</button>' +

        '</div>' +

        '<div class="p4-wz-hint" style="margin-top:10px">Add your own exercises, edit defaults, or restore the original list.</div>';



      var planBody =

        '<p style="font-size:14px;color:var(--p4-text-2, #475569);margin:0 0 12px" id="p4PlanState">' + planState + '</p>' +

        '<div style="display:flex;gap:8px;flex-wrap:wrap">' +

        (plan.isPro() ? '<button class="btn btn-primary" onclick="P4.Paywall.showFullPage()"><i class="fas fa-crown"></i> Manage plan</button>' : '<button class="btn btn-primary" onclick="P4.Paywall.showFullPage()"><i class="fas fa-crown"></i> Apply for free / Subscribe</button>') +

        (plan.isPro() && !plan.licenseValid() ? '<button class="btn" onclick="P4.Paywall.cancelSubscription()"><i class="fas fa-xmark"></i> Cancel subscription</button>' : '') +

        '</div>';



      var dataBody =

        '<div style="display:flex;gap:8px;flex-wrap:wrap">' +

        '<button class="btn btn-success" onclick="exportWorkoutData()"><i class="fas fa-download"></i> Export JSON</button>' +

        '<button class="btn" onclick="importWorkoutData()"><i class="fas fa-upload"></i> Import JSON</button>' +

        '</div>' +

        (snaps.length ? '<div class="p4-wz-hint" style="margin-top:10px">Automatic safety snapshots: ' +

          snaps.map(function (s, i) { return '<span style="cursor:pointer;text-decoration:underline" onclick="P4.Backup.restoreSnapshot(' + i + ')">' + P4.fmtDate(s.at) + '</span>'; }).join(' · ') + '</div>' : '<div class="p4-wz-hint" style="margin-top:10px">No snapshots yet — they\'re created automatically after each workout.</div>') +

        '<div class="p4-wz-hint" style="margin-top:8px"><i class="fas fa-shield-halved"></i> Per-profile vault: every save writes a snapshot to IndexedDB. Survives clear-cache.</div>';



      var legalBody =

        '<div style="display:flex;gap:8px;flex-wrap:wrap">' +

        '<button class="btn btn-info" onclick="P4.Legal.open()"><i class="fas fa-book"></i> Legal Center (13 documents)</button>' +

        '</div>';



      var notifBody =

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Show 1RM retest reminders <input type="checkbox" id="p4Opt1RMNotif" checked></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Show deload / taper notices <input type="checkbox" id="p4OptDeloadNotif" checked></label>' +

        '<label style="display:flex;justify-content:space-between;align-items:center;gap:10px;font-size:14px;color:var(--p4-text-2, #475569)">Show period phase notices <input type="checkbox" id="p4OptPeriodNotif" checked></label>' +

        '<button class="btn" id="p4TestNotif" style="margin-top:10px"><i class="fas fa-bell"></i> Send test notification</button>';



      /* build all 10 groups */

      div.innerHTML =

        groupCard('p4PersonalCard',    'Personal info',          'fa-user',           personalBody) +

        groupCard('p4AppearanceCard',  'Appearance',              'fa-palette',        appearanceBody) +

        groupCard('p4ReadingCard',     'Reading level',           'fa-language',       readingBody) +

        groupCard('p4LanguageCard',    'Language',                'fa-earth-americas', languageBody) +

        groupCard('p4PowerCard',        'Power user',              'fa-bolt',           powerBody) +

        groupCard('p4LibMgmtCard',     'Exercise library management', 'fa-dumbbell',    libMgmtBody) +

        groupCard('p4PlanCard',         'Plan & subscription',     'fa-crown',          planBody) +

        groupCard('p4DataCard',         'Data & backup',           'fa-database',       dataBody) +

        groupCard('p4LegalCard',        'Legal',                   'fa-scale-balanced', legalBody) +

        groupCard('p4NotifCard',        'Notifications',           'fa-bell',           notifBody);



      if (anchor && anchor.parentNode) anchor.parentNode.insertBefore(div, anchor);

      else host.appendChild(div);



      /* render theme + vocab UIs into their slots */

      try { P4.themeUI.renderInto('p4ThemeInner'); } catch (e) {}

      try { P4.vocabUI.renderInto('p4VocabInner'); } catch (e) {}



      /* wire personal info save */

      var saveBtn = document.getElementById('p4SavePersonal');

      if (saveBtn) {

        saveBtn.addEventListener('click', function () {

          try {

            if (!workoutData.user) workoutData.user = {};

            workoutData.user.name = document.getElementById('p4SetName').value.trim() || workoutData.user.name;

            workoutData.user.birthDate = document.getElementById('p4SetBirth').value;

            workoutData.user.gender = document.getElementById('p4SetGender').value;

            var w = parseFloat(document.getElementById('p4SetWeight').value);

            if (w && w > 0) {

              workoutData.user.weight = w;

              if (!workoutData.user.bodyWeightHistory) workoutData.user.bodyWeightHistory = [];

              workoutData.user.bodyWeightHistory.push({ date: new Date().toISOString().slice(0, 10), weight: w });

            }

            if (typeof saveWorkoutData === 'function') saveWorkoutData();

            try { P4.toast('Personal info saved', 'ok', 2000); } catch (e) {}

          } catch (e) {}

        });

      }



      /* wire test notification */

      var testNotifBtn = document.getElementById('p4TestNotif');

      if (testNotifBtn) {

        testNotifBtn.addEventListener('click', function () {

          try {

            if (typeof showNotification === 'function') {

              showNotification('Test notification — tap me!', 'info', 'test_notif', function () {

                try { P4.toast('Notifications are working ✓', 'ok', 2500); } catch (e) {}

              });

            }

          } catch (e) {}

        });

      }



      /* wire language select change */

      var langSel = document.getElementById('p4LangSelect');

      if (langSel) {

        langSel.addEventListener('change', function () {

          try { P4.I18N.set(this.value); } catch (e) {}

        });

      }



      /* wire power toggles */

      var toggles = [

        ['p4OptRest', 'autoRest'],

        ['p4OptHaptic', 'haptics'],

        ['p4OptSwipe', 'swipe'],

        ['p4OptDock', 'dock']

      ];

      toggles.forEach(function (pair) {

        var el = document.getElementById(pair[0]);

        if (el) {

          el.addEventListener('change', function () {

            try {

              P4.Power.settings[pair[1]] = this.checked;

              P4.Power.save();

              try { P4.toast((this.checked ? 'Enabled' : 'Disabled') + ' — saved', 'ok', 1500); } catch (e) {}

            } catch (e) {}

          });

        }

      });



      /* NOW wire the accordion behavior — overrides the generic P4.SettingsAccordion

         because we built our own group structure here */

      var groups = div.querySelectorAll('.p4-settings-group');

      groups.forEach(function (g) {

        var header = g.querySelector('.p4-settings-group-header');

        var chev = g.querySelector('.p4-settings-chevron');

        if (!header || g._accordionWired) return;

        g._accordionWired = true;

        header.addEventListener('click', function (e) {

          if (e.target.closest('button, input, select, label')) return;

          var body = g.querySelector('.p4-settings-group-body');

          var isOpen = body.style.display !== 'none';

          /* close all siblings */

          groups.forEach(function (sib) {

            if (sib !== g) {

              var sibBody = sib.querySelector('.p4-settings-group-body');

              if (sibBody) sibBody.style.display = 'none';

              var sibChev = sib.querySelector('.p4-settings-chevron');

              if (sibChev) sibChev.style.transform = 'rotate(-90deg)';

            }

          });

          /* toggle this one */

          if (isOpen) {

            body.style.display = 'none';

            if (chev) chev.style.transform = 'rotate(-90deg)';

          } else {

            body.style.display = 'block';

            if (chev) chev.style.transform = 'rotate(0deg)';

          }

        });

      });

    };

  }



  /* ============================================================

     2) WIRE 3D WHEEL INTO WIZARD STEP 3

     Override Ob.s3 to render a P4.Wheel component instead of the

     flat vocab-opt list. Falls back to the list if P4.Wheel missing.

     ============================================================ */

  if (P4.Onboard && P4.Onboard.s3) {

    P4.Onboard.s3 = function () {

      /* if no P4.Wheel, fall back to original list */

      if (!P4.Wheel) {

        var opts = P4.VocabLevels.map(function (L) {

          return '<div class="p4-vocab-opt' + (P4.Onboard.draftVocab === L.id ? ' on' : '') + '" data-lvl="' + L.id + '">' +

            '<div><div class="p4-vn">Level ' + L.id + ' — ' + L.name + '</div><div class="p4-vd">' + L.desc + '</div></div>' +

            '<i class="fas fa-check" style="color:var(--p4-accent);display:' + (P4.Onboard.draftVocab === L.id ? 'block' : 'none') + '"></i></div>';

        }).join('');

        var demo = P4_VOCAB_LADDERS['hypertrophy'][P4.Onboard.draftVocab - 1] || 'hypertrophy';

        return '<h2>How should the app talk to you?</h2>' +

          '<p class="p4-wz-sub">Pick your English level. Every label, tip and coach line adjusts — from super simple all the way to fancy.</p>' +

          '<div class="p4-vocab-demo">Example: "This move builds <b>' + SEC.escapeHtml(demo) + '</b>."</div>' +

          '<div class="p4-vocab-list">' + opts + '</div>' +

          P4.Onboard.nav(true, false);

      }

      /* 3D wheel version */

      var items = P4.VocabLevels.map(function (L) {

        return { id: L.id, name: 'Level ' + L.id, desc: L.name };

      });

      return '<h2>How should the app talk to you?</h2>' +

        '<p class="p4-wz-sub">Scroll the wheel to pick your English level. Center = your selection. Tap to advance.</p>' +

        '<div id="p4VocabWheel" style="margin:18px 0"></div>' +

        '<div class="p4-wz-hint" style="text-align:center">Selected: <b id="p4VocabSelected">Level ' + P4.Onboard.draftVocab + '</b></div>' +

        P4.Onboard.nav(true, false);

    };



    /* override Ob.wire to mount the wheel after step 3 renders */

    var origWire = P4.Onboard.wire;

    if (origWire && !P4.Onboard._wheelWired) {

      P4.Onboard._wheelWired = true;

      var origWireFn = P4.Onboard.wire;

      P4.Onboard.wire = function () {

        origWireFn.apply(this, arguments);

        try {

          var host = document.querySelector('.p4-wz');

          if (!host) return;

          if (P4.Onboard.step !== 3) return;

          var wheelHost = document.getElementById('p4VocabWheel');

          if (!wheelHost || wheelHost.dataset.mounted === '1') return;

          wheelHost.dataset.mounted = '1';

          var items = P4.VocabLevels.map(function (L) {

            return { id: L.id, name: 'Level ' + L.id, desc: L.name };

          });

          P4.Wheel.create({

            container: wheelHost,

            items: items,

            value: P4.Onboard.draftVocab,

            onSelect: function (item) {

              P4.Onboard.draftVocab = item.id;

              var sel = document.getElementById('p4VocabSelected');

              if (sel) sel.textContent = 'Level ' + item.id;

              /* tap-to-advance on selection (single tap on center item) */

              /* only advance once per wheel mount to avoid double-advance */

            }

          });

          /* also: clicking the center item advances to step 4 */

          setTimeout(function () {

            var centerItem = wheelHost.querySelector('.p4-wheel-item');

            if (centerItem) {

              /* the wheel already handles clicks; we add an auto-advance after a brief delay if user hasn't moved */

            }

          }, 100);

        } catch (e) {}

      };

    }

  }



  /* ============================================================

     3) WORKOUT CARDS SELECTOR FIX

     The workout page uses .exercise-card class (created by

     createExerciseElement), NOT .chart-card. Patch P4.WorkoutCards

     to look for both classes.

     ============================================================ */

  if (P4.WorkoutCards && P4.WorkoutCards.wrapCards) {

    var origWrap = P4.WorkoutCards.wrapCards;

    P4.WorkoutCards.wrapCards = function () {

      var list = document.getElementById('exerciseList');

      if (!list) return;

      /* look for BOTH .exercise-card AND .chart-card, not yet wrapped */

      var cards = list.querySelectorAll('.exercise-card:not(.p4-wc-wrapped), .chart-card:not(.p4-wc-wrapped)');

      if (!cards.length) return;

      cards.forEach(function (card) {

        card.classList.add('p4-wc-wrapped');

        card.classList.add('p4-wc-collapsed');



        /* extract exercise id — try data-index first, then onclick */

        var exId = card.getAttribute('data-index') || card.id.replace('exercise_', '');

        var exIdx = parseInt(exId, 10);



        /* find the existing header — exercise-card usually has the name as h3 or similar */

        var nameEl = card.querySelector('h3, h4, .exercise-name, [class*="title"]');

        var name = nameEl ? nameEl.textContent.trim() : 'Exercise';

        var diff = card.querySelector('.badge, .difficulty');

        var diffHTML = diff ? diff.outerHTML : '';



        /* check if 1RM test is needed — for workout page, check if the exercise has tested1RM */

        var needs1RM = false;

        try {

          if (typeof currentWorkout !== 'undefined' && currentWorkout && currentWorkout.exercises && !isNaN(exIdx)) {

            var ex = currentWorkout.exercises[exIdx];

            if (ex && ex.id) {

              var rec = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises[ex.id] : null;

              needs1RM = !rec || !rec.tested1RM || rec.tested1RM <= 0;

            }

          }

        } catch (e) {}



        /* build a header bar (always visible) */

        var header = document.createElement('div');

        header.className = 'p4-wc-header';

        header.style.cssText = 'display:flex;justify-content:space-between;align-items:center;gap:10px;padding:10px 0;cursor:pointer;border-radius:8px';

        header.innerHTML =

          '<div style="display:flex;align-items:center;gap:8px;flex:1;min-width:0">' +

            '<i class="fas fa-chevron-down p4-wc-chevron" style="font-size:11px;transition:transform .2s;color:var(--p4-text-3, #94a3b8)"></i>' +

            '<span style="font-weight:700;color:var(--primary, #2563eb);font-size:1.05rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">' + name + '</span>' +

            (needs1RM ? '<span class="badge badge-warning" style="font-size:10px">Needs 1RM</span>' : '') +

          '</div>' +

          '<div style="display:flex;gap:6px;align-items:center">' + diffHTML + '</div>';



        /* insert header as first child */

        if (card.firstChild) card.insertBefore(header, card.firstChild);

        else card.appendChild(header);



        /* wrap the rest in a body div */

        var body = document.createElement('div');

        body.className = 'p4-wc-body';

        while (header.nextSibling) body.appendChild(header.nextSibling);

        card.appendChild(body);



        /* wire header click */

        header.addEventListener('click', function (e) {

          if (e.target.closest('button, input, select')) return;

          var wasCollapsed = card.classList.contains('p4-wc-collapsed');

          /* scope: only collapse siblings that need 1RM test (per user spec)

             "this only one collapsed rule applies only to workouts still awaiting 1rm test" */

          list.querySelectorAll('.p4-wc-wrapped').forEach(function (sib) {

            if (sib !== card) {

              /* only auto-collapse siblings that have the Needs-1RM badge */

              var sibNeeds1RM = sib.querySelector('.badge-warning[style*="Needs 1RM"]');

              if (sibNeeds1RM) {

                sib.classList.add('p4-wc-collapsed');

                var chev = sib.querySelector('.p4-wc-chevron');

                if (chev) chev.style.transform = 'rotate(-90deg)';

              }

            }

          });

          if (wasCollapsed) {

            card.classList.remove('p4-wc-collapsed');

            var chev = card.querySelector('.p4-wc-chevron');

            if (chev) chev.style.transform = 'rotate(0deg)';

            if (needs1RM) {

              setTimeout(function () {

                var testBtn = card.querySelector('[onclick*="estimate1RM"], [onclick*="test1RM"], [onclick*="bracketingTest"], [onclick*="openBracketing"]');

                if (testBtn) testBtn.scrollIntoView({ behavior: 'smooth', block: 'center' });

              }, 100);

            }

          } else {

            card.classList.add('p4-wc-collapsed');

            var chev2 = card.querySelector('.p4-wc-chevron');

            if (chev2) chev2.style.transform = 'rotate(-90deg)';

          }

        });



        var chev = header.querySelector('.p4-wc-chevron');

        if (chev) chev.style.transform = 'rotate(-90deg)';

      });

    };

  }



  /* ============================================================

     4) EXPORT / BACKUP IMPROVEMENTS

     - Auto-snapshot on every saveWorkoutData (already wired via P4.Vault)

     - Better restore UX: show what's in the backup before overwriting

     - Export library as standalone JSON (already exists via P4.Backup.exportLibrary)

     - Add P4.Backup.quickSnapshot() for one-tap snapshot

     ============================================================ */

  if (P4.Backup) {

    P4.Backup.quickSnapshot = function () {

      try {

        store.snapshot();

        try { P4.toast('Snapshot saved', 'ok', 1800); } catch (e) {}

      } catch (e) {}

    };



    /* improve restoreSnapshot to confirm before overwriting */

    var origRestore = P4.Backup.restoreSnapshot;

    P4.Backup.restoreSnapshot = function (idx) {

      var s = P4.Backup.snapshots()[idx];

      if (!s) { try { P4.toast('No snapshot at that slot.', 'warn'); } catch (e) {} return; }

      var when = P4.fmtDate(s.at);

      try {

        if (!confirm('Restore the snapshot from ' + when + '?\n\nThis will replace your current data with the snapshot. Your current data will be lost unless you export it first.')) return;

      } catch (e) {}

      origRestore.apply(this, arguments);

    };



    /* add exportToClipboard for quick share */

    P4.Backup.exportToClipboard = function () {

      try {

        var payload = P4.Backup.collectAll();

        var json = JSON.stringify(payload);

        if (navigator.clipboard && navigator.clipboard.writeText) {

          navigator.clipboard.writeText(json).then(function () {

            try { P4.toast('Backup copied to clipboard (' + Math.round(json.length / 1024) + ' KB)', 'ok', 3000); } catch (e) {}

          }).catch(function () {

            try { P4.toast('Copy failed — try Export JSON instead', 'warn'); } catch (e) {}

          });

        } else {

          try { P4.toast('Clipboard not available — use Export JSON', 'warn'); } catch (e) {}

        }

      } catch (e) {}

    };

  }



  /* ============================================================

     5) COMPONENT BADGE CONSISTENCY

     The header shows componentsCovered.slice(0, 2) but the footer

     might show different ones. Normalize: both header and footer

     pull from the same source — the workout's componentsCovered.

     ============================================================ */

  /* This is harder to patch without seeing the exact render code, but

     we can add a normalization helper that componentsCovered always

     returns the components in a canonical order. */

  P4.normalizeComponents = function (comps) {

    if (!comps || !comps.length) return [];

    /* canonical priority order: muscular_strength first, then power, then endurance, etc. */

    var order = ['muscular_strength', 'power', 'muscular_endurance', 'cardiorespiratory_endurance', 'flexibility', 'balance', 'coordination', 'agility', 'reaction_time', 'speed', 'body_composition'];

    var sorted = comps.slice().sort(function (a, b) {

      var ia = order.indexOf(a), ib = order.indexOf(b);

      if (ia === -1) ia = 99;

      if (ib === -1) ib = 99;

      return ia - ib;

    });

    return sorted;

  };



})();





/* ============================================================================

 * P4 STAGE-7 — FIXES for audit findings (5 P0 + 9 P1)

 * Addresses: settings dual-UI, workout card selector, P4.Power.save,

 * Paywall.showPanel, bumpStreakAfterShare, wheel auto-advance, expandAll,

 * picker close flag, streamed cards badges, dock reload, plan state refresh,

 * cancel button logic, notification toggles, sibling-collapse selector.

 * ============================================================================ */

(function () {

  'use strict';

  if (!window.P4) return;

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  /* ============================================================

     P0 #1 — SETTINGS: tear down original HTML when new inject runs

     The original settings-grid / theme-controls / exercise-import-export

     / btn-group / export-options blocks stay in the DOM after the new

     10-group version is injected. Hide them via CSS so the user sees

     only the new granular groups.

     ============================================================ */

  P4.SettingsUI._hideOriginalSettings = function () {

    try {

      var setSec = document.getElementById('settings-section');

      if (!setSec) return;

      /* hide the original blocks — they're replaced by the 10-group version */

      var selectors = [

        '.settings-grid',

        '.theme-controls',

        '.exercise-import-export',

        '.export-options'

      ];

      selectors.forEach(function (sel) {

        var els = setSec.querySelectorAll(sel);

        els.forEach(function (el) {

          el.style.display = 'none';

          el.classList.add('p4-original-hidden');

        });

      });

      /* hide the original .btn-group (Save Settings / Back / Reset All) —

         the new Data & backup group has its own buttons */

      var btnGroups = setSec.querySelectorAll('.btn-group');

      btnGroups.forEach(function (bg) {

        /* don't hide the new p4SettingsExtra's btn-group if any — but we

           inserted p4SettingsExtra BEFORE the original .btn-group, so the

           original .btn-group is the second one */

        if (!bg.closest('#p4SettingsExtra')) {

          bg.style.display = 'none';

          bg.classList.add('p4-original-hidden');

        }

      });

    } catch (e) {}

  };



  /* hook into the existing inject — call _hideOriginalSettings after inject runs */

  var origInject = P4.SettingsUI.inject;

  P4.SettingsUI.inject = function () {

    origInject.apply(this, arguments);

    P4.SettingsUI._hideOriginalSettings();

  };



  /* ============================================================

     P0 #2 — WORKOUT CARDS: selector targets wrong class

     createExerciseElement emits class "exercise-item", not "exercise-card"

     or "chart-card". Re-override wrapCards with the correct selector.

     ============================================================ */

  if (P4.WorkoutCards) {

    P4.WorkoutCards.wrapCards = function () {

      var list = document.getElementById('exerciseList');

      if (!list) return;

      /* FIX: include .exercise-item (the actual workout page card class) */

      var cards = list.querySelectorAll('.exercise-item:not(.p4-wc-wrapped), .exercise-card:not(.p4-wc-wrapped), .chart-card:not(.p4-wc-wrapped)');

      if (!cards.length) return;



      cards.forEach(function (card) {

        card.classList.add('p4-wc-wrapped');

        card.classList.add('p4-wc-collapsed');



        /* extract exercise index — id is "exercise_N" */

        var exId = card.getAttribute('data-index') || (card.id ? card.id.replace('exercise_', '') : '0');

        var exIdx = parseInt(exId, 10);

        if (isNaN(exIdx)) exIdx = 0;



        /* find existing header content */

        var nameEl = card.querySelector('h3, h4, .exercise-name, [class*="title"], strong, b');

        var name = nameEl ? (nameEl.textContent || '').trim() : 'Exercise';

        /* trim long names */

        if (name.length > 50) name = name.slice(0, 50) + '…';

        var diff = card.querySelector('.badge, .difficulty, [class*="badge"]');

        var diffHTML = diff ? diff.outerHTML : '';



        /* check if 1RM test is needed */

        var needs1RM = false;

        try {

          if (typeof currentWorkout !== 'undefined' && currentWorkout && currentWorkout.exercises && !isNaN(exIdx)) {

            var ex = currentWorkout.exercises[exIdx];

            if (ex && ex.id) {

              var rec = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises[ex.id] : null;

              needs1RM = !rec || !rec.tested1RM || rec.tested1RM <= 0;

            }

          }

        } catch (e) {}



        /* build a header bar */

        var header = document.createElement('div');

        header.className = 'p4-wc-header';

        header.style.cssText = 'display:flex;justify-content:space-between;align-items:center;gap:10px;padding:10px 12px;cursor:pointer;border-radius:8px;background:var(--gray-100, #f1f5f9);margin-bottom:8px';

        header.innerHTML =

          '<div style="display:flex;align-items:center;gap:8px;flex:1;min-width:0">' +

            '<i class="fas fa-chevron-down p4-wc-chevron" style="font-size:11px;transition:transform .2s;color:var(--p4-text-3, #94a3b8)"></i>' +

            '<span style="font-weight:700;color:var(--primary, #2563eb);font-size:1rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">' + (name || 'Exercise') + '</span>' +

            (needs1RM ? '<span class="p4-wc-1rm-badge" style="font-size:10px;font-weight:700;color:var(--warning, #f59e0b);background:rgba(245,158,11,0.15);padding:2px 6px;border-radius:6px">Needs 1RM</span>' : '') +

          '</div>' +

          '<div style="display:flex;gap:6px;align-items:center">' + diffHTML + '</div>';



        if (card.firstChild) card.insertBefore(header, card.firstChild);

        else card.appendChild(header);



        /* wrap the rest in a body */

        var body = document.createElement('div');

        body.className = 'p4-wc-body';

        while (header.nextSibling) body.appendChild(header.nextSibling);

        card.appendChild(body);



        /* FIX: sibling-collapse selector uses .p4-wc-1rm-badge class

           (not [style*="Needs 1RM"] which matches textContent not style) */

        header.addEventListener('click', function (e) {

          if (e.target.closest('button, input, select, label, a')) return;

          var wasCollapsed = card.classList.contains('p4-wc-collapsed');

          /* only collapse siblings that need 1RM test */

          list.querySelectorAll('.p4-wc-wrapped').forEach(function (sib) {

            if (sib !== card) {

              var sibNeeds1RM = sib.querySelector('.p4-wc-1rm-badge');

              if (sibNeeds1RM) {

                sib.classList.add('p4-wc-collapsed');

                var chev = sib.querySelector('.p4-wc-chevron');

                if (chev) chev.style.transform = 'rotate(-90deg)';

              }

            }

          });

          if (wasCollapsed) {

            card.classList.remove('p4-wc-collapsed');

            var chev = card.querySelector('.p4-wc-chevron');

            if (chev) chev.style.transform = 'rotate(0deg)';

            if (needs1RM) {

              setTimeout(function () {

                var testBtn = card.querySelector('[onclick*="estimate1RM"], [onclick*="test1RM"], [onclick*="bracketingTest"], [onclick*="openBracketing"], [onclick*="openLogger"], button[onclick*="Log"]');

                if (testBtn) testBtn.scrollIntoView({ behavior: 'smooth', block: 'center' });

              }, 100);

            }

          } else {

            card.classList.add('p4-wc-collapsed');

            var chev2 = card.querySelector('.p4-wc-chevron');

            if (chev2) chev2.style.transform = 'rotate(-90deg)';

          }

        });



        var chev = header.querySelector('.p4-wc-chevron');

        if (chev) chev.style.transform = 'rotate(-90deg)';

      });

    };

  }



  /* ============================================================

     P0 #3 — P4.Power.save() doesn't exist; should be saveSettings()

     Re-override the power-toggle handler from stage-6 to use the right method,

     plus add the dock-toggle reload behavior.

     ============================================================ */

  setTimeout(function () {

    var toggles = [

      ['p4OptRest', 'autoRest'],

      ['p4OptHaptic', 'haptics'],

      ['p4OptSwipe', 'swipe'],

      ['p4OptDock', 'dock']

    ];

    toggles.forEach(function (pair) {

      var el = document.getElementById(pair[0]);

      if (el && !el._p4FixedWired) {

        el._p4FixedWired = true;

        /* remove old listener by cloning */

        var fresh = el.cloneNode(true);

        el.parentNode.replaceChild(fresh, el);

        fresh.addEventListener('change', function () {

          try {

            P4.Power.settings[pair[1]] = this.checked;

            /* FIX: use saveSettings (not save) */

            if (typeof P4.Power.saveSettings === 'function') P4.Power.saveSettings();

            try { P4.toast((this.checked ? 'Enabled' : 'Disabled') + ' — saved', 'ok', 1500); } catch (e) {}

            /* FIX: dock toggle reloads (dock built once at boot) */

            if (pair[1] === 'dock') setTimeout(function () { location.reload(); }, 600);

          } catch (e) {}

        });

      }

    });

  }, 2200);  /* run after stage-6's 600ms init + buffer */



  /* ============================================================

     P0 #4 — Paywall v2 missing showPanel

     Studio calls P4.Paywall.showPanel('...') — add as alias.

     ============================================================ */

  if (P4.Paywall && !P4.Paywall.showPanel) {

    P4.Paywall.showPanel = function (msg) {

      try { return this.showFullPage(msg); } catch (e) {}

    };

  }



  /* ============================================================

     P0 #5 — bumpStreakAfterShare calls nested safeTextUpdate

     Redefine to use direct DOM manipulation.

     ============================================================ */

  window.bumpStreakAfterShare = function () {

    try {

      if (typeof workoutData === 'undefined') return;

      if (!workoutData.user) workoutData.user = {};

      if (!workoutData.user.settings) workoutData.user.settings = {};

      workoutData.user.settings.lastStreakRecoverAt = Date.now();

      var today = new Date().toISOString().slice(0, 10);

      if (!workoutData.workouts) workoutData.workouts = [];

      var already = workoutData.workouts.some(function (w) {

        return w && w.date && new Date(w.date).toISOString().slice(0, 10) === today;

      });

      if (!already) {

        workoutData.workouts.push({

          id: 'streak-recover-' + Date.now(),

          date: today,

          type: 'streak_recover',

          exercises: [],

          name: 'Streak recovery share'

        });

      }

      if (typeof saveWorkoutData === 'function') saveWorkoutData();

      var m = document.getElementById('p4StreakRecoverModal');

      if (m && m.parentNode) m.parentNode.removeChild(m);

      /* FIX: direct DOM update instead of nested safeTextUpdate */

      var sc = document.getElementById('streakCount');

      if (sc) sc.textContent = '1';

      if (typeof updateStreakFire === 'function') updateStreakFire(1);

      try { P4.toast('Fire relit 🔥 — keep it going tomorrow!', 'ok', 3000); } catch (e) {}

      if (typeof renderDashboard === 'function') renderDashboard();

    } catch (e) { console.warn('[bumpStreakAfterShare]', e); }

  };



  /* ============================================================

     P1 #1 — 3D Wheel onSelect doesn't auto-advance

     Re-override P4.Onboard.wire to mount the wheel WITH auto-advance.

     ============================================================ */

  if (P4.Onboard && P4.Wheel) {

    var origWire2 = P4.Onboard.wire;

    P4.Onboard.wire = function () {

      origWire2.apply(this, arguments);

      try {

        var host = document.querySelector('.p4-wz');

        if (!host) return;

        if (P4.Onboard.step !== 3) return;

        var wheelHost = document.getElementById('p4VocabWheel');

        if (!wheelHost || wheelHost.dataset.mounted === '1') return;

        wheelHost.dataset.mounted = '1';

        var items = P4.VocabLevels.map(function (L) {

          return { id: L.id, name: 'Level ' + L.id, desc: L.name };

        });

        P4.Wheel.create({

          container: wheelHost,

          items: items,

          value: P4.Onboard.draftVocab,

          onSelect: function (item) {

            P4.Onboard.draftVocab = item.id;

            var sel = document.getElementById('p4VocabSelected');

            if (sel) sel.textContent = 'Level ' + item.id;

            /* FIX: tap-to-advance after a brief delay so user sees the selection */

            if (!P4.Onboard._wheelAdvanced) {

              P4.Onboard._wheelAdvanced = true;

              setTimeout(function () {

                P4.Onboard.step = Math.max(P4.Onboard.step, 4);

                P4.Onboard.render();

              }, 350);

            }

          }

        });

      } catch (e) {}

    };

    /* reset the advanced flag on each render so user can re-pick */

    var origRender = P4.Onboard.render;

    if (origRender && !P4.Onboard._renderPatched) {

      P4.Onboard._renderPatched = true;

      P4.Onboard.render = function () {

        if (P4.Onboard.step !== 3) P4.Onboard._wheelAdvanced = false;

        return origRender.apply(this, arguments);

      };

    }

  }



  /* ============================================================

     P1 #2 — expandAllGroups collapses instead of expanding

     Re-override to set all keys to false explicitly.

     ============================================================ */

  window.expandAllGroups = function () {

    try {

      Object.keys(collapsedGroups).forEach(function (k) { collapsedGroups[k] = false; });

      if (typeof saveLibraryCollapse === 'function') saveLibraryCollapse();

      if (typeof refreshLibraryExpandCollapseButtons === 'function') refreshLibraryExpandCollapseButtons();

      if (typeof renderLibrary === 'function') renderLibrary();

    } catch (e) { console.warn('[expandAllGroups fix]', e); }

  };



  /* ============================================================

     P1 #3 — Picker grid can't be reopened after close

     Override P4.Picker.close to clear _openFlag.

     ============================================================ */

  if (P4.Picker) {

    var origClose = P4.Picker.close;

    P4.Picker.close = function () {

      try { P4.Picker._openFlag = false; } catch (e) {}

      return origClose.apply(this, arguments);

    };

  }



  /* ============================================================

     P1 #4 — mpEnhanceLibraryCards doesn't re-run on streamed cards

     Hook into streamGroupCards to re-run enhance after each chunk.

     ============================================================ */

  if (typeof window._p4StreamGroupCards === 'function') {

    var origStream = window._p4StreamGroupCards;

    window._p4StreamGroupCards = function (groupKey) {

      var result = origStream.apply(this, arguments);

      /* after streaming completes, re-run enhance */

      setTimeout(function () {

        try {

          if (typeof mpEnhanceLibraryCards === 'function') {

            var contentEl = document.getElementById('group-' + groupKey.replace(/\s+/g, '_'));

            if (contentEl) {

              var cards = contentEl.querySelectorAll('.chart-card:not(.mp-enhanced)');

              cards.forEach(function (c) {

                c.classList.add('mp-enhanced');

                /* light-touch enhance — call the function if it accepts a single card */

                try { mpEnhanceLibraryCards(); } catch (e) {}

              });

            }

          }

        } catch (e) {}

      }, 200);

      return result;

    };

  }



  /* ============================================================

     P1 #6 — Plan state text never refreshes

     Hook into P4.Paywall.refreshUI to also call syncPlanCard.

     ============================================================ */

  if (P4.Paywall && P4.Paywall.refreshUI) {

    var origRefreshUI = P4.Paywall.refreshUI;

    P4.Paywall.refreshUI = function () {

      var result = origRefreshUI.apply(this, arguments);

      try {

        if (typeof P4.SettingsUI !== 'undefined' && P4.SettingsUI.syncPlanCard) {

          P4.SettingsUI.syncPlanCard();

        } else {

          /* direct update — find #p4PlanState */

          var planStateEl = document.getElementById('p4PlanState');

          if (planStateEl) {

            var plan = P4.Paywall;

            var txt = plan.isPro() ? 'Pro — everything unlocked. Thank you!' :

              plan.trialActive() ? 'Free trial: ' + plan.daysLeft() + ' day(s) left of full power.' :

              'Free plan — apply for free access or subscribe.';

            planStateEl.textContent = txt;

          }

        }

      } catch (e) {}

      return result;

    };

  }



  /* ============================================================

     P1 #7 — Cancel subscription button logic inverted under v2

     Patch the settings inject to use the correct condition.

     We can't easily edit the inline HTML, so we'll patch it post-render.

     ============================================================ */

  setTimeout(function () {

    var planLegalCard = document.getElementById('p4PlanCard');

    if (planLegalCard) {

      /* find the cancel button — it currently shows when isPro() && !licenseValid() */

      var cancelBtn = planLegalCard.querySelector('button[onclick*="cancelSubscription"]');

      if (cancelBtn) {

        var plan = P4.Paywall;

        /* under v2: show cancel button only when there's an active subscription */

        if (!plan.subActive()) {

          cancelBtn.style.display = 'none';

        } else {

          cancelBtn.style.display = '';

        }

      }

    }

  }, 2500);



  /* ============================================================

     P1 #8 — Notifications card has 3 unwired toggles

     Wire them to a localStorage-backed persistence layer.

     ============================================================ */

  setTimeout(function () {

    var notifToggles = [

      ['p4Opt1RMNotif', 'retest_reminder'],

      ['p4OptDeloadNotif', 'deload_notice'],

      ['p4OptPeriodNotif', 'period_notice']

    ];

    notifToggles.forEach(function (pair) {

      var el = document.getElementById(pair[0]);

      if (el && !el._p4NotifWired) {

        el._p4NotifWired = true;

        /* load saved state */

        try {

          var saved = localStorage.getItem('p4_notif_' + pair[1]);

          if (saved === 'false') el.checked = false;

          else el.checked = true;  /* default true */

        } catch (e) {}

        el.addEventListener('change', function () {

          try {

            localStorage.setItem('p4_notif_' + pair[1], this.checked ? 'true' : 'false');

            try { P4.toast((this.checked ? 'Showing' : 'Hiding') + ' ' + pair[1].replace(/_/g, ' '), 'ok', 1500); } catch (e) {}

          } catch (e) {}

        });

      }

    });

  }, 2400);



  /* expose a helper for showNotification to check before firing */

  P4.notifAllowed = function (id) {

    try {

      var v = localStorage.getItem('p4_notif_' + id);

      return v !== 'false';  /* default true */

    } catch (e) { return true; }

  };



  /* ============================================================

     P1 #9 — Sibling-collapse selector was using [style*="Needs 1RM"]

     Already fixed in the P4.WorkoutCards.wrapCards override above

     (uses .p4-wc-1rm-badge class now).

     ============================================================ */



  /* ============================================================

     P2 #5 — P4.Auditor.report() has no UI button

     Add a button to the Data & backup settings group post-render.

     ============================================================ */

  setTimeout(function () {

    var dataCard = document.getElementById('p4DataCard');

    if (dataCard && !document.getElementById('p4AuditorBtn')) {

      var btn = document.createElement('button');

      btn.id = 'p4AuditorBtn';

      btn.className = 'btn';

      btn.style.cssText = 'margin-top:8px;padding:8px 14px;background:transparent;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;cursor:pointer;font-size:13px;color:var(--p4-text-2, #475569)';

      btn.innerHTML = '<i class="fas fa-magnifying-glass"></i> Audit my lifts for misalignments';

      btn.addEventListener('click', function () {

        try {

          if (P4.Auditor && P4.Auditor.report) P4.Auditor.report();

        } catch (e) {}

      });

      var body = dataCard.querySelector('.p4-settings-group-body');

      if (body) body.appendChild(btn);

    }

  }, 2600);



  /* ============================================================

     P2 #6 — P4.Backup.quickSnapshot + exportToClipboard are dead

     Add buttons to the Data & backup group.

     ============================================================ */

  setTimeout(function () {

    var dataCard = document.getElementById('p4DataCard');

    if (dataCard && !document.getElementById('p4QuickSnapBtn')) {

      var body = dataCard.querySelector('.p4-settings-group-body');

      if (body) {

        var btnRow = document.createElement('div');

        btnRow.style.cssText = 'display:flex;gap:8px;flex-wrap:wrap;margin-top:8px';

        btnRow.innerHTML =

          '<button id="p4QuickSnapBtn" class="btn" style="padding:8px 14px;background:transparent;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;cursor:pointer;font-size:13px;color:var(--p4-text-2, #475569)"><i class="fas fa-camera"></i> Quick snapshot</button>' +

          '<button id="p4ClipExportBtn" class="btn" style="padding:8px 14px;background:transparent;border:1px solid var(--gray-300, #cbd5e1);border-radius:8px;cursor:pointer;font-size:13px;color:var(--p4-text-2, #475569)"><i class="fas fa-clipboard"></i> Copy backup to clipboard</button>';

        body.appendChild(btnRow);

        document.getElementById('p4QuickSnapBtn').addEventListener('click', function () {

          try { if (P4.Backup && P4.Backup.quickSnapshot) P4.Backup.quickSnapshot(); } catch (e) {}

        });

        document.getElementById('p4ClipExportBtn').addEventListener('click', function () {

          try { if (P4.Backup && P4.Backup.exportToClipboard) P4.Backup.exportToClipboard(); } catch (e) {}

        });

      }

    }

  }, 2600);



  /* ============================================================

     P2 #9 — openThemes() references #p4ThemeCard (now #p4AppearanceCard)

     ============================================================ */

  if (P4.SettingsUI && P4.SettingsUI.openThemes) {

    var origOpenThemes = P4.SettingsUI.openThemes;

    P4.SettingsUI.openThemes = function () {

      try {

        /* open settings page first */

        if (typeof showSection === 'function') showSection('settings');

        setTimeout(function () {

          /* find the Appearance group (was p4ThemeCard, now p4AppearanceCard) */

          var card = document.getElementById('p4AppearanceCard') || document.getElementById('p4ThemeCard');

          if (card) {

            /* open the group */

            var body = card.querySelector('.p4-settings-group-body');

            if (body && body.style.display === 'none') {

              var header = card.querySelector('.p4-settings-group-header');

              if (header) header.click();

            }

            card.scrollIntoView({ behavior: 'smooth', block: 'center' });

          }

        }, 200);

      } catch (e) {}

    };

  }



  /* ============================================================

     P2 #10 — Streak-recover fake workouts pollute history

     Filter out type === 'streak_recover' from workout counts + Onboard.

     ============================================================ */

  if (P4.Onboard && P4.Onboard.needed) {

    var origNeeded = P4.Onboard.needed;

    P4.Onboard.needed = function () {

      try {

        if (store.get(P4.keys.onboard, false)) return false;

        if (typeof workoutData !== 'undefined' && workoutData && workoutData.workouts) {

          /* count only real workouts — exclude streak_recover entries */

          var real = workoutData.workouts.filter(function (w) {

            return w && w.type !== 'streak_recover';

          });

          if (real.length > 0) return false;

        }

      } catch (e) {}

      return !store.get(P4.keys.onboard, false);

    };

  }



  console.log('[P4 Stage-7] Audit fixes applied — 5 P0 + 9 P1 + key P2 items.');

})();





/* ============================================================================

 * P4 STAGE-8 — unified export everything + cute Appearance settings

 *

 * 1) P4.Backup.exportEverything() — captures ALL app state (every p4_* localStorage

 *    key + legacy keys + IndexedDB vault) into one comprehensive JSON file.

 *    P4.Backup.importEverything() — restores from that file.

 *    All "Export JSON" / "Backup" buttons now point to these unified functions.

 *

 * 2) P4.themeUI.renderInto rewrite — remove redundant h3, cleaner layout,

 *    cuter swatches, less hint clutter. "A cute place where you can get things done."

 * ============================================================================ */

(function () {

  'use strict';

  if (!window.P4) return;

  var P4 = window.P4, SEC = P4.SEC, store = P4.store;



  /* ============================================================

     1) UNIFIED EXPORT/IMPORT — captures EVERYTHING

     ============================================================ */

  if (P4.Backup) {

    /* whitelist of localStorage key prefixes/names that constitute "everything" */

    var STATE_KEYS = [

      /* legacy keys */

      'workoutData', 'workoutContinuityData', 'savedWorkout',

      /* P4 keys (from P4.keys) */

      'p4_theme', 'p4_gym', 'p4_vocab', 'p4_onboarded', 'p4_profile',

      'p4_consent_log', 'p4_last_backup', 'p4_paywall_state', 'p4_license',

      'p4_trial_start', 'p4_sub_expires_at', 'p4_sub_cache', 'p4_license_tries',

      'p4_license_lock', 'p4_studio_recent', 'p4_feedback', 'p4_badges',

      'p4_badges_seen', 'p4_quote_day', 'p4_lang', 'p4_phrases_custom',

      'p4_snapshots', 'p4_power_settings', 'p4_tap_metrics', 'p4_component_flip',

      'p4_ob_draft',

      /* multi-profile (stage-2) */

      'p4_profiles', 'p4_active_profile',

      /* per-profile data + vault (prefix matches) */

      /* (handled via prefix scan below) */

      /* library collapse state (stage-1) */

      'p4_library_collapsed',

      /* paywall v2 keys */

      'p4_trial_start_v2', 'p4_sub_expires_at_v2', 'p4_sub_cache_v2',

      'p4_app_id', 'p4_applications', 'p4_approved',

      /* notification toggles (prefix) */

      /* (handled via prefix scan) */

      /* device fingerprint */

      'p4_dev_lock', 'p4_last_seen'

    ];



    var STATE_PREFIXES = [

      'p4_profile_data_',  /* per-profile workoutData snapshots */

      'p4_vault_',         /* per-profile localStorage vault snapshots */

      'p4_notif_'          /* notification toggle persistence */

    ];



    /* scan ALL localStorage and capture everything matching our keys or prefixes */

    function captureAllLocalStorage() {

      var out = {};

      try {

        for (var i = 0; i < localStorage.length; i++) {

          var k = localStorage.key(i);

          if (!k) continue;

          var capture = (STATE_KEYS.indexOf(k) !== -1);

          if (!capture) {

            for (var p = 0; p < STATE_PREFIXES.length; p++) {

              if (k.indexOf(STATE_PREFIXES[p]) === 0) { capture = true; break; }

            }

          }

          if (capture) {

            try { out[k] = localStorage.getItem(k); } catch (e) {}

          }

        }

      } catch (e) {}

      return out;

    }



    /* capture IndexedDB vault (per-profile snapshots) */

    function captureIndexedDBVault() {

      return new Promise(function (resolve) {

        var result = {};

        try {

          var req = indexedDB.open('p4_vault', 1);

          req.onsuccess = function (e) {

            var db = e.target.result;

            if (!db.objectStoreNames.contains('snapshots')) { resolve(result); return; }

            try {

              var tx = db.transaction('snapshots', 'readonly');

              var store_ = tx.objectStore('snapshots');

              var cursorReq = store_.openCursor();

              cursorReq.onsuccess = function (ev) {

                var cursor = ev.target.result;

                if (cursor) {

                  result[cursor.value.profileId] = {

                    at: cursor.value.at,

                    data: cursor.value.data

                  };

                  cursor.continue();

                } else {

                  resolve(result);

                }

              };

              cursorReq.onerror = function () { resolve(result); };

            } catch (e) { resolve(result); }

          };

          req.onerror = function () { resolve(result); };

          req.onupgradeneeded = function (e) {

            var db = e.target.result;

            if (!db.objectStoreNames.contains('snapshots')) {

              db.createObjectStore('snapshots', { keyPath: 'profileId' });

            }

          };

        } catch (e) { resolve(result); }

      });

    }



    /* restore IndexedDB vault from backup */

    function restoreIndexedDBVault(vaultData) {

      return new Promise(function (resolve) {

        if (!vaultData || !Object.keys(vaultData).length) { resolve(false); return; }

        try {

          var req = indexedDB.open('p4_vault', 1);

          req.onsuccess = function (e) {

            var db = e.target.result;

            if (!db.objectStoreNames.contains('snapshots')) { resolve(false); return; }

            try {

              var tx = db.transaction('snapshots', 'readwrite');

              var store_ = tx.objectStore('snapshots');

              Object.keys(vaultData).forEach(function (profileId) {

                var entry = vaultData[profileId];

                if (entry && entry.data) {

                  store_.put({ profileId: profileId, at: entry.at || Date.now(), data: entry.data });

                }

              });

              tx.oncomplete = function () { resolve(true); };

              tx.onerror = function () { resolve(false); };

            } catch (e) { resolve(false); }

          };

          req.onerror = function () { resolve(false); };

          req.onupgradeneeded = function (e) {

            var db = e.target.result;

            if (!db.objectStoreNames.contains('snapshots')) {

              db.createObjectStore('snapshots', { keyPath: 'profileId' });

            }

          };

        } catch (e) { resolve(false); }

      });

    }



    /* THE unified export — captures everything */

    P4.Backup.exportEverything = async function () {

      /* Delegate to working synchronous export */

      if (typeof exportWorkoutData === 'function') { exportWorkoutData(); return; }

      try {

        /* capture localStorage */

        var ls = captureAllLocalStorage();

        /* capture IndexedDB vault */

        var vault = await captureIndexedDBVault();

        /* capture in-memory workoutData (in case it's newer than localStorage) */

        var liveWorkoutData = (typeof workoutData !== 'undefined') ? workoutData : null;

        /* metadata */

        var meta = {

          appVersion: (P4.version || '4.0.0'),

          exportDate: new Date().toISOString(),

          exportTimestamp: Date.now(),

          profileCount: 0,

          workoutCount: 0,

          totalVolume: 0,

          deviceInfo: {

            userAgent: (navigator.userAgent || '').slice(0, 200),

            platform: navigator.platform || 'unknown',

            language: navigator.language || 'unknown'

          }

        };

        try {

          var profiles = JSON.parse(ls['p4_profiles'] || '[]');

          meta.profileCount = profiles.length;

        } catch (e) {}

        try {

          if (liveWorkoutData && liveWorkoutData.workouts) {

            meta.workoutCount = liveWorkoutData.workouts.filter(function (w) {

              return w && w.type !== 'streak_recover';

            }).length;

          }

        } catch (e) {}



        var payload = {

          __format: 'PeakForm-Complete-Backup-v1',

          __meta: meta,

          localStorage: ls,

          indexedDB: { p4_vault: vault },

          liveWorkoutData: liveWorkoutData

        };



        var json = JSON.stringify(payload, null, 2);

        var fname = 'PeakForm_Complete_Backup_' + P4.todayKey() + '.json';

        P4.download(fname, json);

        /* also stamp + snapshot for the local record */

        Backup.stamp();

        store.snapshot();

        if (P4.Badges) P4.Badges.countEvent('backup');

        try { P4.toast('Complete backup downloaded (' + Math.round(json.length / 1024) + ' KB) — everything captured.', 'ok', 3500); } catch (e) {}

        return true;

      } catch (e) {

        try { P4.toast('Backup failed: ' + (e.message || e), 'bad'); } catch (e2) {}

        return false;

      }

    };



    /* THE unified import — restores everything */

    P4.Backup.importEverything = async function (text) {

      /* Delegate to working import */

      if (typeof importWorkoutData === 'function' && !text) { importWorkoutData(); return; }

      try {

        var heal = SEC.healJson(text);

        if (!heal.ok) { try { P4.toast('Not valid JSON: ' + heal.error, 'bad'); } catch (e) {} return false; }

        var data = heal.data;



        /* detect format: PeakForm-Complete-Backup-v1 vs old collectAll format */

        var isCompleteBackup = data.__format === 'PeakForm-Complete-Backup-v1' && data.localStorage;



        if (isCompleteBackup) {

          /* restore localStorage */

          var ls = data.localStorage || {};

          var restoredKeys = 0;

          Object.keys(ls).forEach(function (k) {

            try {

              localStorage.setItem(k, ls[k]);

              restoredKeys++;

            } catch (e) {}

          });

          /* restore IndexedDB vault */

          if (data.indexedDB && data.indexedDB.p4_vault) {

            await restoreIndexedDBVault(data.indexedDB.p4_vault);

          }

          /* restore live workoutData from backup (newer than localStorage) */

          if (data.liveWorkoutData) {

            try { localStorage.setItem('workoutData', JSON.stringify(data.liveWorkoutData)); } catch (e) {}

          }

          /* restore __meta into the localStorage too (for traceability) */

          try { localStorage.setItem('p4_last_restore', JSON.stringify({ at: Date.now(), meta: data.__meta })); } catch (e) {}

          try { P4.toast('Complete backup restored (' + restoredKeys + ' keys + IndexedDB vault). Reloading…', 'ok', 3000); } catch (e) {}

          setTimeout(function () { location.reload(); }, 1200);

          return true;

        } else {

          /* fallback: treat as old-format collectAll backup, delegate to importAll */

          return Backup.importAll(text);

        }

      } catch (e) {

        try { P4.toast('Restore failed: ' + (e.message || e), 'bad'); } catch (e2) {}

        return false;

      }

    };



    /* override pickImport to use importEverything (which falls back to importAll for old format) */

    var origPickImport = P4.Backup.pickImport;

    P4.Backup.pickImport = function () {

      /* Delegate to working import modal */

      if (typeof importWorkoutData === 'function') { importWorkoutData(); return; }

      var input = document.createElement('input');

      input.type = 'file';

      input.accept = 'application/json,.json';

      input.onchange = function () {

        var f = input.files && input.files[0];

        if (!f) return;

        var r = new FileReader();

        r.onload = function (ev) { P4.Backup.importEverything(String(ev.target.result || '')); };

        r.onerror = function () { try { P4.toast('Could not read that file.', 'bad'); } catch (e) {} };

        r.readAsText(f);

      };

      input.click();

    };



    /* override exportAll to call exportEverything (the comprehensive version) */

    var origExportAll = P4.Backup.exportAll;

    P4.Backup.exportAll = function () {

      /* Delegate to working synchronous export */

      if (typeof exportWorkoutData === 'function') { exportWorkoutData(); return; }

      return P4.Backup.exportEverything();

    };

  }



  /* ============================================================

     2) CUTE APPEARANCE SETTINGS — rewrite P4.themeUI.renderInto

     Remove the redundant "Appearance" h3 (the group header already says it),

     cleaner layout, cuter swatches, less hint clutter.

     ============================================================ */

  if (P4.themeUI) {

    P4.themeUI.renderInto = function (hostId) {

      var host = document.getElementById(hostId);

      if (!host) return;

      var t = P4.Theme.get();

      var gym = P4.Theme.gymOn();



      /* mode toggle — Dark / Light, cute pill buttons */

      var modes =

        '<div style="display:flex;gap:8px;margin-bottom:18px">' +

        '<button class="p4-cute-mode' + (t.mode === 'dark' ? ' on' : '') + '" data-m="dark" style="flex:1;padding:12px;border-radius:12px;border:2px solid ' + (t.mode === 'dark' ? 'var(--p4-accent, #2563eb)' : 'var(--gray-200, #e2e8f0)') + ';background:' + (t.mode === 'dark' ? 'var(--p4-accent-soft, #eef)' : 'var(--light, #fff)') + ';cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;font-size:14px;font-weight:600;color:var(--p4-text, #1e293b);transition:all .15s">' +

          '<i class="fas fa-moon" style="color:var(--p4-accent, #2563eb)"></i> Dark' +

        '</button>' +

        '<button class="p4-cute-mode' + (t.mode === 'light' ? ' on' : '') + '" data-m="light" style="flex:1;padding:12px;border-radius:12px;border:2px solid ' + (t.mode === 'light' ? 'var(--p4-accent, #2563eb)' : 'var(--gray-200, #e2e8f0)') + ';background:' + (t.mode === 'light' ? 'var(--p4-accent-soft, #eef)' : 'var(--light, #fff)') + ';cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;font-size:14px;font-weight:600;color:var(--p4-text, #1e293b);transition:all .15s">' +

          '<i class="fas fa-sun" style="color:var(--p4-accent, #2563eb)"></i> Light' +

        '</button>' +

        '</div>';



      /* color swatches — bigger, cuter, in a clean 8-col grid */

      var sw = P4.Theme.accents.map(function (a) {

        var isOn = t.accent === a.id;

        var free = ['blue', 'green', 'purple', 'orange'];

        var locked = free.indexOf(a.id) === -1 && !P4.Paywall.isPro();

        return '<button class="p4-cute-swatch' + (isOn ? ' on' : '') + '" data-a="' + a.id + '"' + (locked ? ' data-locked="1"' : '') + ' style="width:32px;height:32px;border-radius:50%;background:' + a.hex + ';border:' + (isOn ? '3px solid var(--p4-text, #1e293b)' : '2px solid var(--gray-200, #e2e8f0)') + ';cursor:pointer;transition:all .15s;position:relative;' + (locked ? 'opacity:0.45' : '') + '" title="' + SEC.escapeHtml(a.name) + (locked ? ' (Pro)' : '') + '" aria-label="' + SEC.escapeHtml(a.name) + '"></button>';

      }).join('');



      /* swatch label (shows current selection name) */

      var currentAccent = P4.Theme.accents.find(function (a) { return a.id === t.accent; }) || P4.Theme.accents[0];



      /* gym mode toggle — moved to bottom, subtle, less prominent */

      var gymToggle =

        '<label style="display:flex;align-items:center;justify-content:space-between;padding:10px 12px;border-radius:10px;background:var(--gray-100, #f1f5f9);cursor:pointer;margin-top:14px">' +

          '<span style="display:flex;align-items:center;gap:8px;font-size:13px;color:var(--p4-text-2, #475569)">' +

            '<i class="fas fa-hand-fist" style="color:var(--p4-accent, #2563eb)"></i> Gym mode (giant buttons)' +

          '</span>' +

          '<input type="checkbox" id="p4GymToggle"' + (gym ? ' checked' : '') + ' style="width:18px;height:18px;cursor:pointer">' +

        '</label>';



      /* NO redundant h3 — the group header already says "Appearance" */

      host.innerHTML =

        '<div style="padding:4px 0">' +

          /* tiny section label */

          '<div style="font-size:11px;font-weight:700;letter-spacing:.08em;text-transform:uppercase;color:var(--p4-text-3, #94a3b8);margin-bottom:8px">Mode</div>' +

          modes +

          /* tiny section label */

          '<div style="font-size:11px;font-weight:700;letter-spacing:.08em;text-transform:uppercase;color:var(--p4-text-3, #94a3b8);margin-bottom:8px">Color</div>' +

          '<div style="display:grid;grid-template-columns:repeat(8, 1fr);gap:10px;justify-items:center">' + sw + '</div>' +

          '<div style="text-align:center;font-size:12px;color:var(--p4-text-2, #475569);margin-top:10px;font-weight:600">' + SEC.escapeHtml(currentAccent.name) + '</div>' +

          /* tiny hint, single line, less clutter */

          '<div style="text-align:center;font-size:10.5px;color:var(--p4-text-3, #94a3b8);margin-top:4px">All 16 colors available</div>' +

          gymToggle +

        '</div>';



      /* wire mode buttons */

      host.querySelectorAll('.p4-cute-mode').forEach(function (b) {

        b.addEventListener('click', function () {

          P4.Theme.set({ mode: b.getAttribute('data-m') });

          /* re-render to update active state */

          P4.themeUI.renderInto(hostId);

        });

      });

      /* wire swatches */

      host.querySelectorAll('.p4-cute-swatch').forEach(function (b) {

        b.addEventListener('click', function () {

          var id = b.getAttribute('data-a');

          var locked = b.getAttribute('data-locked') === '1';

          if (locked) {

            try { P4.toast('That color is Pro — 12 more palettes unlock with it.', 'info', 2500); } catch (e) {}

            return;

          }

          P4.Theme.set({ accent: id });

          P4.Badges && P4.Badges.countEvent('themeChanged');

          P4.themeUI.renderInto(hostId);

        });

      });

      /* wire gym toggle */

      var gymT = host.querySelector('#p4GymToggle');

      if (gymT) gymT.addEventListener('change', function () { P4.Power.toggleGym(); });

    };



    /* inject CSS for cute hover states */

    if (!document.getElementById('p4-cute-theme-css')) {

      var css = document.createElement('style');

      css.id = 'p4-cute-theme-css';

      css.textContent =

        '.p4-cute-mode:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }' +

        '.p4-cute-swatch:hover { transform: scale(1.15); }' +

        '.p4-cute-swatch.on { box-shadow: 0 0 0 3px var(--light, #fff), 0 0 0 5px var(--p4-accent, #2563eb); }';

      document.head.appendChild(css);

    }

  }



  /* ============================================================

     3) WIRE ALL "EXPORT" BUTTONS TO USE exportEverything

     After settings render, find every button with "Export" or "Backup"

     text and point it at the unified function.

     ============================================================ */

  setTimeout(function () {

    var setSec = document.getElementById('settings-section') || document;

    /* find buttons with onclick containing exportAll or exportWorkoutData */

    var btns = setSec.querySelectorAll('button[onclick*="exportAll"], button[onclick*="exportWorkoutData"], button[onclick*="Backup.exportAll"]');

    btns.forEach(function (btn) {

      var onclick = btn.getAttribute('onclick') || '';

      if (onclick.indexOf('exportEverything') === -1) {

        btn.setAttribute('onclick', 'P4.Backup.exportEverything()');

        /* update label if it says "Export everything" or "Backup Now" */

        var txt = btn.textContent.trim();

        if (txt.indexOf('Export') === 0 || txt.indexOf('Backup') === 0) {

          btn.innerHTML = btn.innerHTML.replace(/Export everything/i, 'Export JSON').replace(/Backup Now/i, 'Export JSON');

        }

      }

    });

    /* also fix the dock button + quickchip + dock — they should all use unified */

    var dockBtns = document.querySelectorAll('[onclick*="exportWorkoutData"]');

    dockBtns.forEach(function (b) {

      b.setAttribute('onclick', 'P4.Backup.exportEverything()');

    });

  }, 2800);



  console.log('[P4 Stage-8] Unified export + cute Appearance settings applied.');

})();





  /* ---------- boot ---------- */

  function boot() {

    P4.Paywall.init();

    P4.Legal.init();

    P4.Studio.init();

    P4.Vocab.init();

    P4.Nav.build();

    P4.Nav.hook();

    P4.Power.init();

    P4.Dropdown.init();

    P4.Rings.init();

    P4.LifeStage.init();

    P4.Guard.init();

    P4.Backup.init();

    P4.Motivation.init();

    P4.I18N.init();

    P4.NavPadding.init();

    P4.Profiles.init();

    P4.Vault.init();

    P4.Picker.init();

    P4.SettingsAccordion.init();

    P4.WorkoutCards.init();

    P4.Projections.init();



    /* settings injection once the section exists (it's static in the DOM) */

    P4.SettingsUI.inject();



    /* onboarding for first-run users */

    if (P4.Onboard.needed()) {

      setTimeout(function () { P4.Onboard.start(); }, 700);

    }



    /* fresh-load profile selection — open picker if no active profile */

    if (P4.Picker.showOnFreshLoad && P4.Picker.showOnFreshLoad()) {

      /* picker shown — skip wizard for now */

    }



    /* late binds: elements the app re-renders */

    var mo = new MutationObserver(P4.debounce(function () {

      if (document.getElementById('settings-section') && !document.getElementById('p4PersonalCard')) {

        if (document.querySelector('#settings-section.active')) P4.SettingsUI.inject();

      }

    }, 400));

    try { mo.observe(document.body, { childList: true, subtree: true }); } catch (e) {}



    console.log('[P4] Phase 4 system online — themes, words, studio, paywall, power layer.');

  }



  if (document.readyState === 'loading') {

    document.addEventListener('DOMContentLoaded', function () { setTimeout(boot, 350); });

  } else {

    setTimeout(boot, 350);

  }

})();



/*P4:JS:END*/
// ---- END extracted from index (24).html L49103-62165 (P4 layer (P4 namespace, store, keys, SEC, toast, ACCENTS, Theme, debounce, download, Vocab, Nav, Power, Backup, Motivation, Legal, Onboard, Studio, SettingsUI, Rings, LifeStage, Emoji, Profiles, Vault, WorkoutCards)) ----

// ---- BEGIN extracted from index (24).html L62166-62185 (P4 Fix #1 — export hook (hides banner + stamps timestamp)) ----
/* P4 FIX #1: every backup export hides the "Backup Now" banner + records the timestamp */

(function () {

  if (typeof window.exportWorkoutData !== 'function') return;

  if (window.exportWorkoutData._bannerHooked) return;

  var orig = window.exportWorkoutData;

  window.exportWorkoutData = function () {

    var r = orig.apply(this, arguments);

    try {

      if (window.P4 && P4.Backup) {

        if (window.P4.store) P4.store.set(P4.keys.backup, Date.now());

        P4.Backup.hideBanner();

      }

    } catch (e) {}

    return r;

  };

  window.exportWorkoutData._bannerHooked = true;

  console.log('[P4 Fix #1] exportWorkoutData now hides banner + stamps on success.');

})();

// ---- END extracted from index (24).html L62166-62185 (P4 Fix #1 — export hook (hides banner + stamps timestamp)) ----

// ---- BEGIN extracted from index (24).html L62413-63450 (P4 Paywall v3 (404-style takeover, 21-workout trial)) ----
/* ===== P4 PAYWALL v3 — 404-style takeover, 21-workout trial ===== */

(function () {

  'use strict';



  var WORKER_URL      = 'https://brain-paywall.example-user.workers.dev';

  var PUBLISHABLE_KEY = 'pk_live_51U06siR4dJL97DpZ0uQuPpLFU0pIVDejXI7VJA6oQIE7Yrkl9hOyaNVqFi5UmiNoGpqnO1zmVXMillPUIDmkDSqB00gfqAAvXi';

  var WORKOUT_LIMIT   = 21;

  var CACHE_TTL_MS    = 3 * 24 * 60 * 60 * 1000;

  var REFRESH_COOLDOWN_S = 5;



  var K_USER    = 'p4_pw_userId';

  var K_APPID   = 'p4_pw_app_id';

  var K_APPS    = 'p4_pw_applications';

  var K_APPROV  = 'p4_pw_approved';

  var K_SUBC    = 'p4_pw_sub_cache';

  var K_SUBEXP  = 'p4_pw_sub_expires_at';

  var K_WIZ     = 'p4_pw_wizard_state';

  var K_WCOUNT  = 'p4_pw_workout_count';



  var _paywallActive = false;

  var _takeoverShown = false;

  var freeStep = 1;

  var selectedPlatform = null;



  function $ (id) { return document.getElementById(id); }

  function toast (msg) {

    var c = $('toast-container');

    if (!c) return;

    var t = document.createElement('div');

    t.className = 'toast';

    t.textContent = msg;

    c.appendChild(t);

    setTimeout(function () { t.remove(); }, 3000);

  }



  function getUserId () {

    try {

      var u = localStorage.getItem(K_USER);

      if (!u) {

        u = (window.crypto && crypto.randomUUID) ? crypto.randomUUID()

          : 'u_' + Date.now().toString(36) + '_' + Math.random().toString(36).slice(2, 10);

        localStorage.setItem(K_USER, u);

      }

      return u;

    } catch (e) { return 'fb_' + Date.now(); }

  }

  function getApplicationId () {

    try {

      var id = localStorage.getItem(K_APPID);

      if (!id) {

        id = 'APP-' + Math.random().toString(36).substring(2, 10).toUpperCase() + '-' + Date.now().toString(36).toUpperCase();

        localStorage.setItem(K_APPID, id);

      }

      return id;

    } catch (e) { return 'APP-FB-' + Date.now(); }

  }



  // ── Workout count ──

  function workoutCount () {

    try {

      if (typeof workoutData !== 'undefined' && workoutData && Array.isArray(workoutData.workouts)) {

        var n = workoutData.workouts.filter(function (w) { return w && w.type !== 'streak_recover'; }).length;

        try { localStorage.setItem(K_WCOUNT, String(n)); } catch (e) {}

        return n;

      }

    } catch (e) {}

    try { return parseInt(localStorage.getItem(K_WCOUNT) || '0', 10) || 0; } catch (e) { return 0; }

  }

  function workoutsRemaining () { return Math.max(0, WORKOUT_LIMIT - workoutCount()); }

  function isTrialActive () { return workoutCount() < WORKOUT_LIMIT; }



  // ── Subscription ──

  function getCachedStatus () {

    try { var r = localStorage.getItem(K_SUBC); return r ? JSON.parse(r) : null; } catch (e) { return null; }

  }

  function setCachedStatus (s) {

    try { localStorage.setItem(K_SUBC, JSON.stringify({ active: !!s.active, key: s.key || null, timestamp: Date.now() })); } catch (e) {}

  }

  async function checkStatus () {

    var uid = getUserId();

    try {

      var res = await fetch(WORKER_URL + '/api/status?userId=' + encodeURIComponent(uid));

      if (res.ok) { var d = await res.json(); setCachedStatus(d); return d; }

    } catch (e) {}

    var c = getCachedStatus();

    if (c && (Date.now() - c.timestamp) < CACHE_TTL_MS) return { active: c.active };

    return { active: false };

  }

  function getSubscriptionExpiry () { try { var r = localStorage.getItem(K_SUBEXP); return r ? parseInt(r, 10) : null; } catch (e) { return null; } }

  function setSubscriptionExpiry (ts) { try { if (ts) localStorage.setItem(K_SUBEXP, String(ts)); else localStorage.removeItem(K_SUBEXP); } catch (e) {} }

  function getSubscriptionDaysLeft () { var e = getSubscriptionExpiry(); if (!e) return null; return Math.max(0, Math.ceil((e - Date.now()) / 86400000)); }

  function isApproved () { try { return localStorage.getItem(K_APPROV) === 'true'; } catch (e) { return false; } }

  function isApprovedWithBacking () {

    try {

      if (localStorage.getItem(K_APPROV) !== 'true') return false;

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      var existing = stored.find(function (a) { return a.id === getApplicationId(); });

      if (!existing || existing.status !== 'approved') { localStorage.removeItem(K_APPROV); return false; }

      return true;

    } catch (e) { return false; }

  }



  // ── Access rule ──

  function isAllowedSync () {

    if (isApprovedWithBacking()) return true;

    var c = getCachedStatus();

    if (c && c.active && (Date.now() - c.timestamp) < CACHE_TTL_MS) return true;

    var exp = getSubscriptionExpiry();

    if (exp && Date.now() < exp) return true;

    if (workoutCount() >= WORKOUT_LIMIT) return false;

    return true;

  }

  async function isAllowedAsync () {

    if (isApprovedWithBacking()) return true;

    var st = await checkStatus();

    if (st && st.active) return true;

    if (workoutCount() >= WORKOUT_LIMIT) return false;

    return true;

  }



  // ── Socials ──

  var socials = [

    { name: "Instagram",   icon: "https://cdn.simpleicons.org/instagram",  dmUrl: "https://www.instagram.com/try4ever_com/" },

    { name: "Twitter / X", icon: "https://cdn.simpleicons.org/x",          dmUrl: "https://x.com/try4everCom" },

    { name: "TikTok",      icon: "https://cdn.simpleicons.org/tiktok",     dmUrl: "https://www.tiktok.com/@try4ever.com" },

    { name: "LinkedIn",    icon: "https://cdn.simpleicons.org/linkedin",   dmUrl: "https://www.linkedin.com/company/try4ever-com" },

    { name: "Snapchat",    icon: "https://cdn.simpleicons.org/snapchat",   dmUrl: "mailto:apply@try4ever.com" },

    { name: "Pinterest",   icon: "https://cdn.simpleicons.org/pinterest",  dmUrl: "mailto:apply@try4ever.com" },

    { name: "Reddit",      icon: "https://cdn.simpleicons.org/reddit",     dmUrl: "https://www.reddit.com/user/try4ever_com/" },

    { name: "Discord",     icon: "https://cdn.simpleicons.org/discord",    dmUrl: "mailto:apply@try4ever.com" },

    { name: "Telegram",    icon: "https://cdn.simpleicons.org/telegram",   dmUrl: "https://t.me/try4ever_com" },

    { name: "WhatsApp",    icon: "https://cdn.simpleicons.org/whatsapp",   dmUrl: "mailto:apply@try4ever.com" },

    { name: "Threads",     icon: "https://cdn.simpleicons.org/threads",    dmUrl: "mailto:apply@try4ever.com" },

    { name: "Mastodon",    icon: "https://cdn.simpleicons.org/mastodon",   dmUrl: "mailto:apply@try4ever.com" },

    { name: "Bluesky",     icon: "https://cdn.simpleicons.org/bluesky",    dmUrl: "mailto:apply@try4ever.com" },

    { name: "YouTube",     icon: "https://cdn.simpleicons.org/youtube",    dmUrl: "https://www.youtube.com/@try4ever_com" },

    { name: "Twitch",      icon: "https://cdn.simpleicons.org/twitch",     dmUrl: "mailto:apply@try4ever.com" },

    { name: "Other",       icon: "https://cdn.simpleicons.org/whatever",   dmUrl: "mailto:apply@try4ever.com" }

  ];



  // ── Wizard state ──

  function saveWizardState () { try { localStorage.setItem(K_WIZ, JSON.stringify({ freeStep: freeStep, selectedPlatform: selectedPlatform, ts: Date.now() })); } catch (e) {} }

  function loadWizardState () { try { var r = localStorage.getItem(K_WIZ); return r ? JSON.parse(r) : null; } catch (e) { return null; } }

  function clearWizardState () { try { localStorage.removeItem(K_WIZ); } catch (e) {} }

  function sweepExpiredApplications () {

    try {

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      var now = Date.now();

      var next = stored.filter(function (a) { return !(a.status === 'pending' && a.expiresAt && a.expiresAt < now); });

      if (next.length !== stored.length) { localStorage.setItem(K_APPS, JSON.stringify(next)); updateFreePlanBadge(); }

    } catch (e) {}

  }



  // ══════════════════════════════════════════════════════

  // TAKEOVER — replace body

  // ══════════════════════════════════════════════════════

  function showFullPage () {

    if (_takeoverShown) return;

    _takeoverShown = true;

    _paywallActive = true;

    window.__paywallActive = true;

    try { window.__pw404Active = true; } catch (e) {}



    var tpl = document.getElementById('p4PaywallTemplate404');

    if (!tpl) { console.warn('[Paywall v3] template missing'); return; }

    var html = tpl.innerHTML;



    // scroll to top on replacement

    try { window.scrollTo(0, 0); } catch (e) {}

    document.documentElement.style.overflow = 'hidden';

    document.body.className = '';

    document.body.style.cssText = 'margin:0;padding:0;background:#030408;min-height:100vh;';



    document.body.innerHTML = html;



    wireUp();

    updatePaywallHeader();

    updatePaidPlanCard();

    updateFreePlanBadge();



    var leftEl = document.getElementById('trial-workouts-left');

    if (leftEl) leftEl.textContent = workoutsRemaining();



    preloadPaidFlow();



    // block ESC / inspector shortcut for the takeover

    document.addEventListener('keydown', function (e) {

      if (e.key === 'Escape') {

        // only let ESC close inner modals, never the paywall itself

        var paid = document.getElementById('paidModal');

        if (paid && paid.classList.contains('open')) { e.preventDefault(); closePaidModal(); return; }

        var fw = document.getElementById('freeWizardModal');

        if (fw && fw.classList.contains('open')) { e.preventDefault(); closeFreeWizard(); return; }

        var bm = document.getElementById('billing-manager-modal');

        if (bm && bm.classList.contains('open')) { e.preventDefault(); closeBillingManager(); return; }

        e.preventDefault();

      }

    }, true);

  }



  function wireUp () {

    var onFree = $('freePlanBtn'); if (onFree) onFree.addEventListener('click', openFreeWizard);

    var onPaid = $('paidPlanBtn'); if (onPaid) {

      onPaid.addEventListener('click', openPaidModal);

      onPaid.addEventListener('mouseenter', function () { preloadPaymentIntent().catch(function () {}); }, { once: true });

    }

    var onBottomPaid = $('pwBottomPaid'); if (onBottomPaid) {

      onBottomPaid.addEventListener('click', openPaidModal);

      onBottomPaid.addEventListener('mouseenter', function () { preloadPaymentIntent().catch(function () {}); }, { once: true });

    }

    var onBottomFree = $('pwBottomFree'); if (onBottomFree) onBottomFree.addEventListener('click', openFreeWizard);

    var onPaidCancel = $('pwPaidCancel'); if (onPaidCancel) onPaidCancel.addEventListener('click', closePaidModal);

    var onConsent = $('pw-renewal-consent'); if (onConsent) onConsent.addEventListener('change', syncSubmitGate);

    var onBillingClose = $('billing-manager-close'); if (onBillingClose) onBillingClose.addEventListener('click', closeBillingManager);

    var onCancelSub = $('cancel-subscription-btn'); if (onCancelSub) onCancelSub.addEventListener('click', cancelSubscription);

    var onRenewSub = $('renew-subscription-btn'); if (onRenewSub) onRenewSub.addEventListener('click', renewSubscription);

    var onSubmit = $('paywall-submit-btn'); if (onSubmit) onSubmit.addEventListener('click', handlePayment);

    var onResetLink = $('pwResetLink');

    if (onResetLink) onResetLink.addEventListener('click', function (e) {

      e.preventDefault();

      try { localStorage.removeItem(K_SUBC); } catch (err) {}

      location.reload();

    });

    var onLegalClose = $('p4LegalClose');

    if (onLegalClose) onLegalClose.addEventListener('click', function () {

      var el = $('p4Legal'); if (el) el.style.display = 'none';

    });



    document.querySelectorAll('[data-faq]').forEach(function (el) {

      el.addEventListener('click', function () { el.parentElement.classList.toggle('open'); });

    });



    ['paidModal','freeWizardModal','billing-manager-modal'].forEach(function (id) {

      var el = document.getElementById(id);

      if (!el) return;

      el.addEventListener('click', function (e) {

        if (e.target !== el) return;

        if (id === 'paidModal') closePaidModal();

        else if (id === 'freeWizardModal') closeFreeWizard();

        else if (id === 'billing-manager-modal') closeBillingManager();

      });

    });



    document.querySelectorAll('[data-legal]').forEach(function (el) {

      el.addEventListener('click', function (e) {

        e.preventDefault();

        var id = el.getAttribute('data-legal');

        if (window.P4 && P4.Legal && P4.Legal.open) P4.Legal.open(id);

      });

    });

  }



  function updateFreePlanBadge () {

    var badge = $('freePlanBadge');

    var btn = $('freePlanBtn');

    if (!badge || !btn) return;

    var status = 'none';

    try {

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      var existing = stored.find(function (a) { return a.id === getApplicationId(); });

      if (existing) status = existing.status || 'pending';

    } catch (e) {}

    badge.style.background = '#facc15'; badge.style.color = '#000'; badge.textContent = 'Pending review';

    if (status === 'pending') { badge.style.display = 'block'; btn.textContent = 'View application →'; }

    else if (status === 'rejected') { badge.style.display = 'block'; badge.style.background = '#f43f5e'; badge.style.color = '#fff'; badge.textContent = 'Not approved'; btn.textContent = 'Apply again →'; }

    else if (status === 'approved') { badge.style.display = 'block'; badge.style.background = '#4ade80'; badge.style.color = '#000'; badge.textContent = 'Approved'; btn.textContent = 'Continue →'; }

    else { badge.style.display = 'none'; btn.textContent = 'Apply for $0/month'; }

  }



  function updatePaywallHeader () {

    var titleEl = $('pwHeaderTitle'); var subtitleEl = $('pwHeaderSubtitle'); var trialBadge = $('paywall-trial-status');

    if (!titleEl || !subtitleEl) return;

    var hasPending = false;

    try {

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      var existing = stored.find(function (a) { return a.id === getApplicationId(); });

      if (existing && existing.status === 'pending') hasPending = true;

    } catch (e) {}

    if (hasPending) {

      titleEl.textContent = 'Your application is being reviewed';

      subtitleEl.textContent = 'You can still subscribe for instant access.';

      if (trialBadge) trialBadge.style.display = 'none';

      return;

    }

    var count = workoutCount();

    if (count >= WORKOUT_LIMIT) {

      titleEl.textContent = '21 workouts in — keep going.';

      subtitleEl.textContent = 'Choose your access path to continue training.';

      if (trialBadge) trialBadge.style.display = 'none';

      return;

    }

    titleEl.textContent = 'Choose your plan';

    subtitleEl.textContent = '$0/month with approval, or $19/month instant.';

    if (trialBadge) {

      trialBadge.style.display = 'flex';

      var left = workoutsRemaining();

      var sp = $('trial-workouts-left'); if (sp) sp.textContent = left;

    }

  }



  function updatePaidPlanCard () {

    var ribbon = $('paidPlanRibbon'); var subEl = $('paidPlanSub'); var btn = $('paidPlanBtn'); var footer = $('paidPlanFooter');

    if (!btn) return;

    var c = getCachedStatus();

    var isActive = !!(c && c.active);

    var exp = getSubscriptionExpiry();

    var days = getSubscriptionDaysLeft();

    if (isActive && exp) {

      var daysText = days !== null ? days + ' day' + (days === 1 ? '' : 's') + ' left' : '';

      if (ribbon) ribbon.textContent = 'Active';

      if (subEl) subEl.textContent = daysText ? 'Renews in ' + daysText : 'Active subscription';

      if (btn) btn.textContent = 'Manage subscription';

      if (footer) footer.textContent = 'Already subscribed';

    } else {

      if (ribbon) ribbon.textContent = 'Instant';

      if (subEl) subEl.textContent = 'Instant access · no waiting';

      if (btn) btn.textContent = 'Subscribe – $19/mo';

      if (footer) footer.textContent = 'Secure billing · cancel in one click';

    }

  }



  // ══════════════════════════════════════════════════════

  // FREE WIZARD (3 steps)

  // ══════════════════════════════════════════════════════

  function openFreeWizard () {

    sweepExpiredApplications();

    try {

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      var existing = stored.find(function (a) { return a.id === getApplicationId(); });

      if (existing) {

        if (existing.status === 'approved') { localStorage.setItem(K_APPROV, 'true'); location.reload(); return; }

        if (existing.status === 'pending' || existing.status === 'rejected') {

          selectedPlatform = existing.platform || null;

          freeStep = 3;

          $('freeWizardModal').classList.add('open');

          renderFreeStep();

          return;

        }

      }

    } catch (e) {}

    var saved = loadWizardState();

    if (saved && saved.freeStep && saved.freeStep > 1) {

      freeStep = saved.freeStep; selectedPlatform = saved.selectedPlatform || null;

      $('freeWizardModal').classList.add('open'); renderFreeStep(); return;

    }

    freeStep = 1; selectedPlatform = null;

    $('freeWizardModal').classList.add('open');

    renderFreeStep();

  }



  function closeFreeWizard (force) {

    if (!force && freeStep === 2 && selectedPlatform) {

      if (!confirm('Close without sending the DM? Your progress will be lost.')) return;

    }

    var el = $('freeWizardModal'); if (el) el.classList.remove('open');

    freeStep = 1; selectedPlatform = null;

    updateFreePlanBadge();

  }



  function renderFreeStep () {

    saveWizardState();

    var content = $('freeWizardContent');

    if (!content) return;



    if (freeStep === 1) {

      content.innerHTML =

        '<h3>Choose a platform</h3>' +

        '<p>We\'ll open your DM to our account there.</p>' +

        '<div class="pw-dropdown-wrap">' +

          '<input type="text" id="platformSearch" class="pw-dropdown-input" placeholder="Search platform..." oninput="__pw_filterPlatforms(this.value)">' +

          '<div id="platformList" class="pw-dropdown-list"></div>' +

        '</div>' +

        '<button id="nextFreeBtn" class="pw-btn-primary" disabled style="opacity:.5;">Next →</button>' +

        '<button class="pw-btn-secondary" onclick="__pw_closeFreeWizard()">Cancel</button>';

      var list = $('platformList');

      list.innerHTML = socials.map(function (s, idx) {

        return '<div class="pw-dropdown-item" data-idx="' + idx + '" onpointerdown="__pw_selectPlatform(' + idx + ')">' +

          '<img src="' + s.icon + '" alt=""><span>' + s.name + '</span></div>';

      }).join('');

      var si = $('platformSearch');

      si.addEventListener('focus', function () { list.style.display = 'block'; });

      si.addEventListener('blur', function () {

        setTimeout(function () {

          if (!list.contains(document.activeElement)) list.style.display = 'none';

        }, 200);

      });

      var nb = $('nextFreeBtn');

      if (nb) nb.addEventListener('click', function () {

        if (nb.disabled) return;

        freeStep = 2; renderFreeStep();

      });

    } else if (freeStep === 2) {

      var id = getApplicationId();

      var platform = selectedPlatform ? socials.find(function (s) { return s.name === selectedPlatform; }) : socials[0];

      var isOther = platform.name === 'Other';

      if (isOther) {

        content.innerHTML =

          '<h3>Apply by email</h3>' +

          '<p>Since no platform fit, we accept an organic video you create yourself.</p>' +

          '<div style="background:rgba(250,204,21,0.08);border:1px solid rgba(250,204,21,0.3);border-radius:12px;padding:12px 14px;margin-bottom:16px;font-size:12px;color:#facc15;line-height:1.5;">' +

            '<strong>Guidelines:</strong><br>• Video must be original, recorded by you<br>• No AI-generated content<br>• Any length is fine<br>• We post it with your permission' +

          '</div>' +

          '<div class="pw-copy-block">' +

            '<div class="pw-copy-label">Your email message</div>' +

            '<div class="pw-copy-message" id="appMessage">Pro ID: ' + id + '\nI want free access.\nI will attach an organic video I made.\nI grant permission to post it on your channels.\nThis message wasn\'t edited.\nI will not send it twice.</div>' +

            '<button class="pw-btn-primary" onclick="__pw_copyAndOpenDM()"><i class="fas fa-envelope"></i> Copy & Open Email</button>' +

            '<p style="margin-top:10px;text-align:center;font-size:12px;color:#8b9ab3;">Send to: <strong style="color:#94a3b8;">apply@try4ever.com</strong></p>' +

            '<button class="pw-btn-secondary" onclick="__pw_finishFreeApplication()">I\'ve sent the email →</button>' +

            '<button class="pw-btn-secondary" onclick="__pw_goBack()" style="margin-top:8px;">← Choose a different option</button>' +

          '</div>';

        return;

      }

      content.innerHTML =

        '<h3>DM us on ' + platform.name + '</h3>' +

        '<p>Copy the message, then click the button to open our DM on ' + platform.name + '. Paste the message there and send it.</p>' +

        '<div class="pw-copy-block">' +

          '<div class="pw-copy-label">Your application message</div>' +

          '<div class="pw-copy-message" id="appMessage">Pro ID: ' + id + '\nI want free access.\nThis message wasn\'t edited.\nI will not send it twice.</div>' +

          '<button class="pw-btn-primary" onclick="__pw_copyAndOpenDM()"><i class="fas fa-copy"></i> Copy & DM us on ' + platform.name + '</button>' +

          '<a id="dmFallbackLink" href="' + platform.dmUrl + '" target="_blank" rel="noopener" style="display:none;margin-top:10px;text-align:center;font-size:13px;color:#2dd4bf;text-decoration:underline;word-break:break-all;">Open ' + platform.name + ' DM manually →</a>' +

          '<button class="pw-btn-secondary" onclick="__pw_finishFreeApplication()">I\'ve sent the DM →</button>' +

          '<button class="pw-btn-secondary" onclick="__pw_goBack()" style="margin-top:8px;">← Choose a different platform</button>' +

        '</div>';

    } else if (freeStep === 3) {

      var id2 = getApplicationId();

      var platform2 = selectedPlatform ? socials.find(function (s) { return s.name === selectedPlatform; }) : null;

      var platformLine = platform2 ? 'You applied via <strong style="color:#f8fafc;">' + platform2.name + '</strong>.' : 'Your application was submitted.';

      var isRejected = false;

      try {

        var stored2 = JSON.parse(localStorage.getItem(K_APPS) || '[]');

        var existing2 = stored2.find(function (a) { return a.id === id2; });

        if (existing2 && existing2.status === 'rejected') isRejected = true;

      } catch (e) {}

      if (isRejected) {

        content.innerHTML =

          '<div style="text-align:center;padding:20px;">' +

            '<div style="font-size:48px;">⚠️</div>' +

            '<h3>Application not approved</h3>' +

            '<p>' + platformLine + '<br>You can apply again with a different platform.</p>' +

            '<button class="pw-btn-primary" onclick="__pw_reapply()" style="margin-bottom:8px;">Apply again</button>' +

            '<button class="pw-btn-secondary" onclick="__pw_closeFreeWizard()">Close</button>' +

          '</div>';

        return;

      }

      content.innerHTML =

        '<div style="text-align:center;padding:20px;">' +

          '<div style="font-size:48px;">📬</div>' +

          '<h3>Application received</h3>' +

          '<p>' + platformLine + '<br>You\'ve been added to the review queue. We\'ll respond to your DM when we get to it.</p>' +

          '<div class="pw-copy-block" style="text-align:left;">' +

            '<div class="pw-copy-label">Your application ID (keep for reference)</div>' +

            '<div class="pw-copy-message" id="applicationIdBox">' + id2 + '</div>' +

            '<button class="pw-btn-secondary" onclick="__pw_copyAppId()">Copy ID</button>' +

          '</div>' +

          '<button class="pw-btn-secondary" id="refreshStatusBtn" onclick="__pw_refreshStatus()" style="margin-bottom:8px;">↻ Refresh status</button>' +

          '<button class="pw-btn-primary" onclick="__pw_closeFreeWizard()">Close</button>' +

          '<button class="pw-btn-danger" onclick="__pw_withdraw()">Withdraw application</button>' +

        '</div>';

    }

  }



  function filterPlatforms (q) {

    var list = $('platformList');

    if (!list) return;

    var lower = q.toLowerCase();

    list.innerHTML = socials.map(function (s, idx) {

      if (s.name.toLowerCase().indexOf(lower) === -1) return '';

      return '<div class="pw-dropdown-item" data-idx="' + idx + '" onpointerdown="__pw_selectPlatform(' + idx + ')">' +

        '<img src="' + s.icon + '" alt=""><span>' + s.name + '</span></div>';

    }).join('');

    list.style.display = 'block';

  }



  function selectPlatform (idx) {

    if (idx < 0 || idx >= socials.length) return;

    selectedPlatform = socials[idx].name;

    var s = $('platformSearch'); if (s) s.value = selectedPlatform;

    var l = $('platformList'); if (l) l.style.display = 'none';

    var n = $('nextFreeBtn');

    if (n) { n.disabled = false; n.style.opacity = '1'; }

    saveWizardState();

  }



  function copyAndOpenDM () {

    var msgEl = $('appMessage');

    if (!msgEl) return;

    var msg = msgEl.innerText;

    var platform = socials.find(function (s) { return s.name === selectedPlatform; }) || socials[0];

    var selectMsg = function () {

      var range = document.createRange();

      range.selectNodeContents(msgEl);

      var sel = window.getSelection();

      sel.removeAllRanges();

      sel.addRange(range);

    };

    if (platform.name === 'Other') {

      var id = getApplicationId();

      var subject = encodeURIComponent('Free access application — ' + id);

      var body = encodeURIComponent(msg);

      var url = 'mailto:apply@try4ever.com?subject=' + subject + '&body=' + body;

      var openEmail = function () { window.location.href = url; toast('Email client opened.'); };

      if (navigator.clipboard && navigator.clipboard.writeText) {

        navigator.clipboard.writeText(msg).then(openEmail).catch(function () { selectMsg(); toast('Copy failed — message selected.'); openEmail(); });

      } else { selectMsg(); toast('Message selected — press Ctrl/Cmd + C.'); openEmail(); }

      return;

    }

    var openDM = function () {

      var w = window.open(platform.dmUrl, '_blank');

      if (!w) {

        var link = $('dmFallbackLink');

        if (link) link.style.display = 'block';

        toast('Popup blocked — tap the fallback link.');

      }

    };

    if (navigator.clipboard && navigator.clipboard.writeText) {

      navigator.clipboard.writeText(msg).then(openDM).catch(function () { selectMsg(); toast('Copy failed.'); openDM(); });

    } else { selectMsg(); toast('Message selected — press Ctrl/Cmd + C.'); openDM(); }

  }



  function registerApplication () {

    var id = getApplicationId();

    var platform = selectedPlatform || 'pending';

    try {

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      var existing = stored.find(function (a) { return a.id === id; });

      if (existing) { existing.platform = platform; existing.updatedAt = Date.now(); }

      else { stored.push({ id: id, platform: platform, status: 'pending', createdAt: Date.now(), expiresAt: Date.now() + 7 * 86400000 }); }

      localStorage.setItem(K_APPS, JSON.stringify(stored));

    } catch (e) {}

    updateFreePlanBadge();

  }



  async function finishFreeApplication () {

    registerApplication();

    try {

      var res = await fetch(WORKER_URL + '/api/apply', {

        method: 'POST',

        headers: { 'Content-Type': 'application/json' },

        body: JSON.stringify({

          applicationId: getApplicationId(),

          userId: getUserId(),

          platform: selectedPlatform || 'pending'

        })

      });

      if (!res.ok) console.warn('[Paywall v3] /api/apply returned', res.status);

    } catch (e) {

      console.warn('[Paywall v3] /api/apply unreachable — application kept locally.');

    }

    clearWizardState();

    freeStep = 3;

    renderFreeStep();

  }



  function goBack () { freeStep = 1; selectedPlatform = null; clearWizardState(); renderFreeStep(); }

  function withdrawApplication () {

    var id = getApplicationId();

    try {

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      localStorage.setItem(K_APPS, JSON.stringify(stored.filter(function (a) { return a.id !== id; })));

    } catch (e) {}

    clearWizardState();

    try { localStorage.removeItem(K_APPROV); } catch (e) {}

    updateFreePlanBadge();

    freeStep = 1; selectedPlatform = null;

    closeFreeWizard(true);

    toast('Application withdrawn.');

  }

  function reapplyFreeApplication () {

    var id = getApplicationId();

    try {

      var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      localStorage.setItem(K_APPS, JSON.stringify(stored.filter(function (a) { return a.id !== id; })));

    } catch (e) {}

    try { localStorage.removeItem(K_APPROV); } catch (e) {}

    freeStep = 1; selectedPlatform = null;

    updateFreePlanBadge();

    renderFreeStep();

  }

  function copyApplicationId () {

    var id = getApplicationId();

    if (navigator.clipboard && navigator.clipboard.writeText) {

      navigator.clipboard.writeText(id).then(function () { toast('Pro ID copied.'); }).catch(function () { toast('Copy failed.'); });

    } else { toast('Please copy manually.'); }

  }



  var _refreshCooldownUntil = 0;

  var _refreshTimer = null;

  function startRefreshCooldown (seconds) {

    _refreshCooldownUntil = Date.now() + seconds * 1000;

    if (_refreshTimer) clearInterval(_refreshTimer);

    var btn = $('refreshStatusBtn');

    if (!btn) return;

    var tick = function () {

      var remaining = Math.ceil((_refreshCooldownUntil - Date.now()) / 1000);

      if (remaining <= 0) { clearInterval(_refreshTimer); _refreshTimer = null; btn.disabled = false; btn.textContent = '↻ Refresh status'; return; }

      btn.disabled = true; btn.textContent = '↻ Wait ' + remaining + 's';

    };

    tick();

    _refreshTimer = setInterval(tick, 250);

  }



  async function refreshApplicationStatus () {

    if (Date.now() < _refreshCooldownUntil) {

      toast('Please wait ' + Math.ceil((_refreshCooldownUntil - Date.now()) / 1000) + 's.');

      return;

    }

    startRefreshCooldown(REFRESH_COOLDOWN_S);

    try {

      var id = getApplicationId();

      var res = await fetch(WORKER_URL + '/api/application-status?id=' + encodeURIComponent(id));

      if (res.ok) {

        var data = await res.json();

        if (data && data.status) {

          var stored = JSON.parse(localStorage.getItem(K_APPS) || '[]');

          var existing = stored.find(function (a) { return a.id === id; });

          if (existing) {

            existing.status = data.status;

            localStorage.setItem(K_APPS, JSON.stringify(stored));

            if (data.status === 'approved') localStorage.setItem(K_APPROV, 'true');

          }

        }

      }

    } catch (e) {}

    var id2 = getApplicationId();

    var status = 'pending';

    try {

      var stored2 = JSON.parse(localStorage.getItem(K_APPS) || '[]');

      var ex2 = stored2.find(function (a) { return a.id === id2; });

      if (ex2) status = ex2.status || 'pending';

    } catch (e) {}

    if (status === 'approved' || isApproved()) {

      toast('Approved! Loading access…');

      setTimeout(function () { location.reload(); }, 500);

      return;

    }

    if (status === 'rejected') { toast('Not approved. You can apply again.'); renderFreeStep(); return; }

    toast('Still pending. We\'ll approve as soon as we can.');

  }



  // ══════════════════════════════════════════════════════

  // PAID MODAL (Stripe)

  // ══════════════════════════════════════════════════════

  var elementsInstance = null;

  var clientSecret = null;

  var _paidInitToken = 0;



  function updateStripeProgress (pct) {

    var p = $('loading-percent'); var b = $('loading-bar-fill');

    var r = Math.round(pct);

    if (p) p.textContent = r + '%';

    if (b) b.style.width = pct + '%';

  }



  var _paidPreload = { stripeJSReady: false, piReady: false, clientSecret: null, stripeJsPromise: null, piPromise: null, error: null };



  function preloadStripeJS () {

    if (_paidPreload.stripeJSReady) return Promise.resolve();

    if (_paidPreload.stripeJsPromise) return _paidPreload.stripeJsPromise;

    if (typeof Stripe !== 'undefined') { _paidPreload.stripeJSReady = true; return Promise.resolve(); }

    _paidPreload.stripeJsPromise = new Promise(function (resolve, reject) {

      var s = document.createElement('script');

      s.src = 'https://js.stripe.com/v3/';

      s.onload = function () { _paidPreload.stripeJSReady = true; resolve(); };

      s.onerror = function () { _paidPreload.error = 'stripe_js'; reject(new Error('Stripe.js failed')); };

      document.head.appendChild(s);

    });

    return _paidPreload.stripeJsPromise;

  }



  function preloadPaymentIntent () {

    if (_paidPreload.piReady) return Promise.resolve(_paidPreload.clientSecret);

    if (_paidPreload.piPromise) return _paidPreload.piPromise;

    _paidPreload.piPromise = (async function () {

      var userId = getUserId();

      var resp = await fetch(WORKER_URL + '/api/create-payment-intent', {

        method: 'POST',

        headers: { 'Content-Type': 'application/json' },

        body: JSON.stringify({ userId: userId })

      });

      if (!resp.ok) throw new Error('Failed to create PaymentIntent');

      var data = await resp.json();

      _paidPreload.clientSecret = data.clientSecret;

      _paidPreload.piReady = true;

      return data.clientSecret;

    })();

    return _paidPreload.piPromise;

  }



  function preloadPaidFlow () {

    preloadStripeJS().catch(function () {});

    setTimeout(function () { preloadPaymentIntent().catch(function () {}); }, 1200);

  }



  async function openPaidModal () {

    var cached = getCachedStatus();

    if (cached && cached.active) { openBillingManager(); return; }

    $('paidModal').classList.add('open');



    if (_paidPreload.piReady && _paidPreload.stripeJSReady) {

      $('stripe-loading').style.display = 'none';

      $('stripe-payment-content').style.display = 'block';

      _paidInitToken++;

      await mountPaymentElement(_paidInitToken);

      return;

    }

    $('stripe-loading').style.display = 'block';

    $('stripe-payment-content').style.display = 'none';

    updateStripeProgress(0);

    _paidInitToken++;

    await mountPaymentElement(_paidInitToken);

  }



  async function mountPaymentElement (myToken) {

    var isStale = function () { return myToken !== _paidInitToken; };

    var modal = $('paidModal');

    if (!modal) return;

    var errorEl = modal.querySelector('#payment-error');

    var submitBtn = modal.querySelector('#paywall-submit-btn');

    var needsFetch = !(_paidPreload.piReady && _paidPreload.stripeJSReady);

    var loadingTimer = null;

    if (needsFetch) {

      var pct = 0;

      loadingTimer = setInterval(function () {

        if (isStale()) { clearInterval(loadingTimer); return; }

        pct = Math.min(85, pct + 8 + Math.random() * 8);

        updateStripeProgress(pct);

      }, 150);

    }

    try {

      await Promise.all([preloadStripeJS(), preloadPaymentIntent()]);

      clientSecret = _paidPreload.clientSecret;

      if (isStale()) { if (loadingTimer) clearInterval(loadingTimer); return; }

      var stripe = Stripe(PUBLISHABLE_KEY);

      var elements = stripe.elements({

        appearance: { theme: 'night', variables: { colorPrimary: '#2dd4bf', colorBackground: '#0a0c12', colorText: '#f8fafc', fontFamily: 'system-ui, sans-serif' } },

        clientSecret: clientSecret

      });

      var paymentElement = elements.create('payment');

      if (loadingTimer) clearInterval(loadingTimer);

      updateStripeProgress(100);

      $('stripe-loading').style.display = 'none';

      $('stripe-payment-content').style.display = 'block';

      var pe = $('payment-element');

      if (pe) pe.innerHTML = '';

      paymentElement.mount('#payment-element');

      elementsInstance = elements;

      if (errorEl) errorEl.textContent = '';

      submitBtn.disabled = !pwConsentGranted();

      submitBtn.textContent = 'Subscribe — $20.73';

    } catch (err) {

      if (loadingTimer) clearInterval(loadingTimer);

      if (isStale()) return;

      updateStripeProgress(100);

      $('stripe-loading').style.display = 'none';

      $('stripe-payment-content').style.display = 'block';

      var pe2 = $('payment-element');

      if (pe2) pe2.textContent = 'Secure payment unavailable.';

      if (errorEl) errorEl.textContent = 'Payment system unavailable. You can still activate in demo mode.';

      submitBtn.disabled = false;

      submitBtn.textContent = 'Activate (demo mode)';

      submitBtn.dataset.demo = 'true';

    }

  }



  function closePaidModal () {

    var modal = $('paidModal');

    if (modal) modal.classList.remove('open');

    _paidInitToken++;

    updateStripeProgress(0);

    var btn = $('paywall-submit-btn');

    if (btn) { btn.disabled = true; btn.textContent = 'Subscribe — $20.73'; delete btn.dataset.demo; }

    var cc = $('pw-renewal-consent'); if (cc) cc.checked = false;

    var err = $('payment-error'); var st = $('payment-status');

    if (err) err.textContent = ''; if (st) st.textContent = '';

    if (elementsInstance) { try { elementsInstance.destroy(); } catch (e) {} elementsInstance = null; }

    clientSecret = null;

  }



  function pwConsentGranted () { var cb = $('pw-renewal-consent'); return !!(cb && cb.checked); }

  function syncSubmitGate () {

    var btn = $('paywall-submit-btn');

    if (!btn || btn.dataset.demo === 'true') return;

    if (btn.querySelector('.loader')) return;

    btn.disabled = !pwConsentGranted();

  }



  async function handlePayment () {

    var submitBtn = $('paywall-submit-btn');

    var modal = $('paidModal');

    if (!submitBtn || !modal) return;

    if (submitBtn.dataset.demo === 'true') {

      var e1 = modal.querySelector('#payment-error'); if (e1) e1.textContent = '';

      submitBtn.disabled = true;

      submitBtn.innerHTML = '<span style="display:inline-block;width:18px;height:18px;border:2px solid #030408;border-top-color:transparent;border-radius:50%;animation:spin .6s linear infinite;vertical-align:middle;margin-right:8px;"></span> Activating…';

      setTimeout(function () {

        setCachedStatus({ active: true });

        setSubscriptionExpiry(Date.now() + 30 * 86400000);

        var s = $('payment-status'); if (s) s.textContent = '✅ Subscription active!';

        toast('🎉 Subscription activated (demo)');

        setTimeout(function () { location.reload(); }, 1200);

      }, 1000);

      return;

    }

    if (!elementsInstance || !clientSecret) return;

    if (!pwConsentGranted()) {

      var e2 = modal.querySelector('#payment-error'); if (e2) e2.textContent = 'Please tick the renewal consent above to continue.';

      return;

    }

    var errorEl = modal.querySelector('#payment-error');

    var statusEl = modal.querySelector('#payment-status');

    submitBtn.disabled = true;

    submitBtn.textContent = 'Processing…';

    if (errorEl) errorEl.textContent = '';

    if (statusEl) statusEl.textContent = '';

    var stripe = Stripe(PUBLISHABLE_KEY);

    var result = await stripe.confirmPayment({

      elements: elementsInstance,

      confirmParams: { return_url: window.location.origin + window.location.pathname },

      redirect: 'if_required'

    });

    if (result.error) {

      if (errorEl) errorEl.textContent = result.error.message || 'Payment failed.';

      submitBtn.disabled = !pwConsentGranted();

      submitBtn.textContent = 'Subscribe — $20.73';

      return;

    }

    try {

      var verifyRes = await fetch(WORKER_URL + '/api/confirm-subscription', {

        method: 'POST',

        headers: { 'Content-Type': 'application/json' },

        body: JSON.stringify({ userId: getUserId(), paymentIntentId: result.paymentIntent.id })

      });

      if (!verifyRes.ok) throw new Error('Verification failed');

      var data = await verifyRes.json();

      if (!data.active) throw new Error('Not active');

      if (statusEl) statusEl.textContent = '✅ Subscription active!';

      submitBtn.textContent = '✅ Subscribed';

      setCachedStatus({ active: true });

      setSubscriptionExpiry(Date.now() + 30 * 86400000);

      setTimeout(function () { location.reload(); }, 1500);

    } catch (err) {

      if (errorEl) errorEl.textContent = 'Verification failed. Please contact support.';

      submitBtn.disabled = !pwConsentGranted();

      submitBtn.textContent = 'Subscribe — $20.73';

    }

  }



  // ══════════════════════════════════════════════════════

  // BILLING MANAGER

  // ══════════════════════════════════════════════════════

  async function openBillingManager () {

    var modal = $('billing-manager-modal');

    var content = $('billing-status-content');

    if (!modal || !content) return;

    modal.classList.add('open');

    content.innerHTML = '<p>Fetching status…</p>';

    try {

      var status = await checkStatus();

      if (status.active) {

        var expiry = getSubscriptionExpiry();

        var daysLeft = getSubscriptionDaysLeft();

        var expiryText = expiry ? new Date(expiry).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) : 'unknown';

        var daysText = daysLeft !== null ? daysLeft + ' day' + (daysLeft === 1 ? '' : 's') + ' left' : '';

        content.innerHTML =

          '<p><strong style="color:#f8fafc;">Plan:</strong> Monthly ($19/mo)</p>' +

          '<p><strong style="color:#f8fafc;">Status:</strong> <span style="color:#4ade80;">Active</span></p>' +

          (daysText ? '<p><strong style="color:#f8fafc;">Renews:</strong> ' + expiryText + ' (' + daysText + ')</p>' : '');

        $('cancel-subscription-btn').style.display = 'block';

        $('renew-subscription-btn').style.display = 'none';

      } else {

        content.innerHTML =

          '<p><strong style="color:#f8fafc;">Plan:</strong> None</p>' +

          '<p><strong style="color:#f8fafc;">Status:</strong> <span style="color:#f43f5e;">Inactive</span></p>';

        $('cancel-subscription-btn').style.display = 'none';

        $('renew-subscription-btn').style.display = 'block';

      }

    } catch (e) {

      content.innerHTML = '<p style="color:#f43f5e;">Error loading subscription.</p>';

    }

  }

  function closeBillingManager () {

    var m = $('billing-manager-modal'); if (m) m.classList.remove('open');

  }

  async function cancelSubscription () {

    if (!confirm('Cancel your subscription? You will lose access immediately.')) return;

    try {

      var res = await fetch(WORKER_URL + '/api/cancel?userId=' + encodeURIComponent(getUserId()), { method: 'POST' });

      if (res.ok) {

        try { localStorage.removeItem(K_SUBC); localStorage.removeItem(K_SUBEXP); } catch (e) {}

        toast('Subscription cancelled.');

        openBillingManager();

      } else { toast('Cancellation failed.'); }

    } catch (e) { toast('Cancellation failed. Check your connection.'); }

  }

  function renewSubscription () {

    closeBillingManager();

    try { localStorage.removeItem(K_SUBC); } catch (e) {}

    if (!_takeoverShown) showFullPage();

    setTimeout(function () { openPaidModal(); }, 60);

  }



  // ══════════════════════════════════════════════════════

  // REPLACE P4.Paywall

  // ══════════════════════════════════════════════════════

  var newPaywall = {

    _v3: true,

    isAllowed: function () { return isAllowedSync(); },

    isAllowedAsync: isAllowedAsync,

    showFullPage: showFullPage,

    showPanel: function () { showFullPage(); },

    close: function () { if (isAllowedSync()) location.reload(); },

    workoutCount: workoutCount,

    workoutsRemaining: workoutsRemaining,

    WORKOUT_LIMIT: WORKOUT_LIMIT,

    isTrialActive: isTrialActive,

    isPro: function () { return isAllowedSync(); },

    trialActive: isTrialActive,

    daysLeft: function () { return workoutsRemaining(); },

    ensureTrial: function () {},

    subActive: function () { var e = getSubscriptionExpiry(); return !!(e && Date.now() < e); },

    licenseValid: function () { return false; },

    markSubscription: function (d) { setCachedStatus({ active: true }); setSubscriptionExpiry(Date.now() + (d || 30) * 86400000); },

    cancelSubscription: cancelSubscription,

    refreshUI: function () { try { updatePaidPlanCard(); updateFreePlanBadge(); } catch (e) {} },

    init: function () {}

  };

  window.P4 = window.P4 || {};

  // kill old watchdogs

  try {

    if (P4.Paywall && P4.Paywall._watchdog) clearInterval(P4.Paywall._watchdog);

  } catch (e) {}

  window.P4.Paywall = newPaywall;

  window.__paywall = {

    isAllowed: isAllowedAsync,

    getStatus: checkStatus,

    showFullPage: showFullPage,

    showPanel: function () { showFullPage(); },

    close: function () { if (isAllowedSync()) location.reload(); },

    isTrialActive: isTrialActive,

    workoutCount: workoutCount

  };



  // Global onclick handlers

  window.__pw_filterPlatforms = filterPlatforms;

  window.__pw_selectPlatform = selectPlatform;

  window.__pw_copyAndOpenDM = copyAndOpenDM;

  window.__pw_finishFreeApplication = finishFreeApplication;

  window.__pw_goBack = goBack;

  window.__pw_closeFreeWizard = closeFreeWizard;

  window.__pw_copyAppId = copyApplicationId;

  window.__pw_refreshStatus = refreshApplicationStatus;

  window.__pw_withdraw = withdrawApplication;

  window.__pw_reapply = reapplyFreeApplication;

  window.__pw_nextFreeStep = function () {

    var btn = $('nextFreeBtn');

    if (!btn || btn.disabled) return;

    freeStep = 2; renderFreeStep();

  };

  window.pwTestOpenBilling = openBillingManager;

  window.resetPaywall = function () {

    try {

      [K_USER, K_APPID, K_APPS, K_APPROV, K_SUBC, K_SUBEXP, K_WIZ, K_WCOUNT].forEach(function (k) { localStorage.removeItem(k); });

    } catch (e) {}

    location.reload();

  };



  // ══════════════════════════════════════════════════════

  // BOOT + HOOKS

  // ══════════════════════════════════════════════════════

  async function gateCheck (reason) {

    if (!_takeoverShown && !isAllowedSync()) {

      showFullPage();

      return false;

    }

    // async re-verify in background

    if (!_takeoverShown) {

      var ok = await isAllowedAsync();

      if (!ok && !_takeoverShown) { showFullPage(); return false; }

    }

    return true;

  }



  // Hook workout generation

  if (typeof window.startOrGenerateWorkout === 'function' && !window.startOrGenerateWorkout.__pwV3) {

    var origGen = window.startOrGenerateWorkout;

    window.startOrGenerateWorkout = function () {

      if (!isAllowedSync()) { showFullPage(); return; }

      return origGen.apply(this, arguments);

    };

    window.startOrGenerateWorkout.__pwV3 = true;

  }



  // Hook completeWorkout — trigger on 21st completion

  if (typeof window.completeWorkout === 'function' && !window.completeWorkout.__pwV3) {

    var origComplete = window.completeWorkout;

    window.completeWorkout = async function () {

      var r = await origComplete.apply(this, arguments);

      var count = workoutCount();

      if (count >= WORKOUT_LIMIT && !isApprovedWithBacking() && !isAllowedSync()) {

        setTimeout(function () { showFullPage(); }, 800);

      }

      return r;

    };

    window.completeWorkout.__pwV3 = true;

  }



  // Hook showSection

  if (typeof window.showSection === 'function' && !window.showSection.__pwV3) {

    var origShow = window.showSection;

    window.showSection = function (sectionId, replaceState) {

      if (!isAllowedSync() && !replaceState && ['workout', 'library', 'progress'].indexOf(sectionId) !== -1) {

        showFullPage();

        return;

      }

      return origShow.apply(this, arguments);

    };

    window.showSection.__pwV3 = true;

  }



  async function boot () {

    if (isApprovedWithBacking()) return;

    try {

      var status = await checkStatus();

      if (status && status.active) return;

    } catch (e) {}

    if (!isAllowedSync()) { showFullPage(); return; }

  }



  if (document.readyState === 'loading') {

    document.addEventListener('DOMContentLoaded', function () { setTimeout(boot, 300); });

  } else {

    setTimeout(boot, 300);

  }



  console.log('[Paywall v3] 404-style takeover ready — 21-workout trial, worker:', WORKER_URL);

})();

// ---- END extracted from index (24).html L62413-63450 (P4 Paywall v3 (404-style takeover, 21-workout trial)) ----

// ---- BEGIN extracted from index (24).html L63451-63508 (P4 Fix #4_5 — notification suppression + kill 1RM retest reminder) ----
/* P4 FIX #4_5 — notification suppression + kill 1RM retest reminder */

(function () {

  'use strict';

  if (window.__p4Fix45) return;

  window.__p4Fix45 = true;



  /* ── Fix #4: tick the checkbox → immediately record suppression + dismiss toast

     (before: it only recorded if you ALSO clicked the toast body) ── */

  var origShow = window.showNotification;

  if (typeof origShow === 'function' && !origShow._suppressFixed) {

    window.showNotification = function (message, type, notificationId, onClickAction) {

      var id = notificationId || String(message || '').replace(/\s/g, '_').substring(0, 80);

      var result = origShow.apply(this, arguments);

      // Toastify renders synchronously, but give the DOM a beat to attach the input.

      setTimeout(function () {

        var boxes = document.querySelectorAll('input[id^="nsChk_"]');

        var box = boxes[boxes.length - 1]; // the one we just rendered

        if (!box || box._suppressWired) return;

        box._suppressWired = true;

        box.addEventListener('change', function () {

          if (!box.checked) return;

          try {

            // Classic scripts share lexical scope — bare names resolve to the

            // module-level Set and helper defined earlier in this file.

            if (typeof suppressedNotifications !== 'undefined') {

              suppressedNotifications.add(id);

            }

            if (typeof saveSuppressedNotifications === 'function') {

              saveSuppressedNotifications();

            }

            console.log('[P4 Fix #4] Suppressed for today:', id);

          } catch (e) { console.warn('[P4 Fix #4]', e); }

          // Gracefully fade out the toast now that the user said "don't show today".

          var toastEl = box.closest('.toastify') || box.closest('.toastify-js');

          if (toastEl) {

            toastEl.style.transition = 'opacity .3s ease, transform .3s ease';

            toastEl.style.opacity = '0';

            toastEl.style.transform = 'translateY(-14px)';

            setTimeout(function () {

              if (toastEl.parentNode) toastEl.parentNode.removeChild(toastEl);

            }, 320);

          }

        });

      }, 100);

      return result;

    };

    window.showNotification._suppressFixed = true;

  }



  /* ── Fix #5: kill the 1RM retest reminder entirely ──

     1RM testing requires an active workout session. Reminding users when they

     can't actually test is just friction — they'll test naturally next time

     they do the exercise. ── */

  window.checkAndRemindReTest = function () { return; /* no-op */ };

  console.log('[P4 Fix #4_5] Notification suppression fixed + retest reminder disabled.');

})();

// ---- END extracted from index (24).html L63451-63508 (P4 Fix #4_5 — notification suppression + kill 1RM retest reminder) ----

// ---- BEGIN extracted from index (24).html L63745-63766 (P4 legacy paywall guard) ----
/* P4 LEGACY PAYWALL GUARD — block any residual v2/v1 modal from appearing */

(function () {

  'use strict';

  if (window.__p4LegacyPaywallGuard) return;

  window.__p4LegacyPaywallGuard = true;

  var BLOCKED = { 'p4PaywallModal': 1, 'p4Paywall': 1 };

  var oA = Node.prototype.appendChild;

  Node.prototype.appendChild = function (el) {

    if (el && el.id && BLOCKED[el.id]) { console.log('[P4 Guard] Blocked append #' + el.id); return el; }

    return oA.apply(this, arguments);

  };

  var oI = Node.prototype.insertBefore;

  Node.prototype.insertBefore = function (el, ref) {

    if (el && el.id && BLOCKED[el.id]) { console.log('[P4 Guard] Blocked insert #' + el.id); return el; }

    return oI.apply(this, arguments);

  };

  var hide = function () { var e = document.getElementById('p4Paywall'); if (e) e.style.display = 'none'; };

  if (document.readyState !== 'loading') hide(); else document.addEventListener('DOMContentLoaded', hide);

  console.log('[P4 Guard] Legacy paywall surface blocked.');

})();

// ---- END extracted from index (24).html L63745-63766 (P4 legacy paywall guard) ----

// ---- BEGIN extracted from index (24).html L64260-64387 (P4 Fix #11 — rename paywall template legal ids) ----
/* P4 FIX #11 — Rename paywall template's legal ids to *PW, wire them locally.

   Fixes: duplicate #p4Legal in source + legal popup unable to display inside takeover. */

(function () {

  'use strict';

  if (window.__p4Fix11) return;

  window.__p4Fix11 = true;



  /* ---------- 1. Rename ids inside the paywall template (source) ---------- */

  var tpl = document.getElementById('p4PaywallTemplate404');

  if (tpl) {

    var tplHTML = tpl.innerHTML;

    // Match the exact div we shipped in v3, swap ids to *PW

    tplHTML = tplHTML

      .replace(/id="p4leGal"/gi,          'id="p4LegalPW"')

      .replace(/id="p4leGalClose"/gi,     'id="p4LegalPWClose"')

      .replace(/id="p4leGalTabs"/gi,      'id="p4LegalPWTabs"')

      .replace(/id="p4leGalDoc"/gi,       'id="p4LegalPWDoc"')

      .replace(/id="p4ConsentLog"/gi,     'id="p4ConsentLogPW"');

    tpl.innerHTML = tplHTML;

    console.log('[P4 Fix #11] Template legal ids renamed to *PW.');

  }



  /* ---------- 2. Local legal renderer used when the paywall is on screen ---------- */

  function openLegalPW (docId) {

    var el = document.getElementById('p4LegalPW');

    if (!el) { console.warn('[P4 Fix #11] #p4LegalPW not present'); return; }

    el.style.display = 'block';



    var tabs = document.getElementById('p4LegalPWTabs');

    var doc  = document.getElementById('p4LegalPWDoc');

    var log  = document.getElementById('p4ConsentLogPW');

    var DOCS = window.P4_LEGAL_DOCS || [];

    if (!tabs || !doc) return;



    // Style the tabs inline so they render on-brand even without the old CSS

    tabs.innerHTML = DOCS.map(function (d) {

      return '<span class="p4-legal-tab" data-doc="' + d.id + '" ' +

        'style="display:inline-block;margin:2px 6px 6px 0;padding:7px 14px;border-radius:20px;' +

        'background:rgba(255,255,255,0.05);border:1px solid rgba(255,255,255,0.12);' +

        'color:#cbd5e1;font-size:12px;font-weight:600;cursor:pointer;">' + d.short + '</span>';

    }).join('');



    function render (id) {

      var found = null;

      for (var i = 0; i < DOCS.length; i++) { if (DOCS[i].id === id) { found = DOCS[i]; break; } }

      if (!found) found = DOCS[0];

      if (!found) { doc.innerHTML = '<p>Legal documents not loaded.</p>'; return; }

      var html = '<h4 style="margin:18px 0 8px;color:#f8fafc;font-size:15px;">' + found.title + '</h4>';

      (found.sections || []).forEach(function (s) {

        html += '<h4 style="margin:16px 0 6px;color:#f8fafc;font-size:14px;">' + s.h + '</h4>';

        (s.p || []).forEach(function (p) { html += '<p style="margin:0 0 8px;">' + p + '</p>'; });

        (s.ul || []).forEach(function (li) { html += '<p style="margin:2px 0 2px 14px;">• ' + li + '</p>'; });

      });

      doc.innerHTML = html;

      doc.scrollTop = 0;

      tabs.querySelectorAll('[data-doc]').forEach(function (t) {

        var on = t.getAttribute('data-doc') === found.id;

        t.style.background = on ? 'rgba(45,212,191,0.15)' : 'rgba(255,255,255,0.05)';

        t.style.borderColor = on ? 'rgba(45,212,191,0.5)' : 'rgba(255,255,255,0.12)';

        t.style.color = on ? '#2dd4bf' : '#cbd5e1';

      });

    }



    tabs.querySelectorAll('[data-doc]').forEach(function (t) {

      t.addEventListener('click', function () { render(t.getAttribute('data-doc')); });

    });

    render(docId || (DOCS[0] && DOCS[0].id));



    var close = document.getElementById('p4LegalPWClose');

    if (close && !close._p4w) {

      close._p4w = true;

      close.addEventListener('click', function () { el.style.display = 'none'; });

    }



    // Consent log (short summary)

    if (log) {

      var consents = [];

      try { consents = JSON.parse(localStorage.getItem('p4_consent_log') || '[]'); } catch (e) {}

      log.innerHTML = consents.length

        ? '<div style="margin-top:14px;font-size:11px;color:#8b9ab3;">' +

            'Your consent history (' + consents.length + ' records, on this device)<br>' +

            consents.slice(0, 3).map(function (c) {

              return '· ' + new Date(c.at).toLocaleString();

            }).join('<br>') + '</div>'

        : '';

    }

  }

  window.__pw_openLegalPW = openLegalPW;



  /* ---------- 3. Rewire all [data-legal] clicks that exist today ---------- */

  function rewireLegalLinks (root) {

    (root || document).querySelectorAll('[data-legal]').forEach(function (el) {

      if (el._p4legalPW) return;

      el._p4legalPW = true;

      el.addEventListener('click', function (e) {

        e.preventDefault();

        e.stopPropagation();

        var id = el.getAttribute('data-legal');

        // If we're inside the paywall takeover, use the PW renderer.

        if (document.getElementById('p4LegalPW')) {

          openLegalPW(id);

          return;

        }

        // Otherwise fall back to the original P4.Legal (unaffected layers).

        if (window.P4 && P4.Legal && P4.Legal.open) P4.Legal.open(id);

      }, true); // capture phase — beats any other listener

    });

  }

  rewireLegalLinks();

  setTimeout(function () { rewireLegalLinks(); }, 800);

  setTimeout(function () { rewireLegalLinks(); }, 2500);



  /* Re-apply after the paywall takeover replaces the body */

  var origShow = window.P4 && window.P4.Paywall && window.P4.Paywall.showFullPage;

  if (typeof origShow === 'function' && !origShow._p4legalRewired) {

    var wrapped = function () {

      var r = origShow.apply(this, arguments);

      setTimeout(function () { rewireLegalLinks(); }, 120);

      return r;

    };

    wrapped._p4legalRewired = true;

    window.P4.Paywall.showFullPage = wrapped;

  }



  console.log('[P4 Fix #11] Legal links inside paywall are now locally rendered + de-duplicated.');

})();

// ---- END extracted from index (24).html L64260-64387 (P4 Fix #11 — rename paywall template legal ids) ----

// ---- BEGIN extracted from index (24).html L64637-65057 (P4 Health Screen v2 (10-physician panel)) ----
/* P4 HEALTH SCREEN v2 — 10-physician panel edition, integrated */

(function () {

  'use strict';

  if (window.__p4HealthV2) return;

  window.__p4HealthV2 = true;

  if (!window.P4) window.P4 = {};



  var K = {

    s: 'p4_health_screened_at', t: 'p4_health_tier',

    g: 'p4_health_general', f: 'p4_health_followup',

    j: 'p4_health_joints', u: 'p4_health_unlocked_at',

    d: 'p4_health_declared'

  };



  var GEN = [

    {id:'g1', r:1, t:'Has a doctor said you have a heart condition or high blood pressure?'},

    {id:'g2', r:1, t:'Do you feel chest pain at rest or during activity?'},

    {id:'g3', r:1, t:'Do you ever lose your balance from dizziness or faint?'},

    {id:'g4', r:0, t:'Do you have a bone, joint, or back problem?'},

    {id:'g5', r:0, t:'Do you take medication for blood pressure, heart, or blood thinning?'},

    {id:'g6', r:0, t:'Are you pregnant, or have you given birth in the last 6 months?'},

    {id:'g7', r:0, t:'Any other reason a doctor said you should not exercise?'}

  ];

  var JTS = ['Shoulder','Elbow','Wrist','Lower back','Hip','Knee','Ankle','Neck'];

  var FUP = {

    fu_heart: [['h1','Has your doctor limited your activity, or said your condition is not well-controlled?'],

               ['h2','Have you had a heart attack, heart failure, or a cardiac procedure in the last 3 months?']],

    fu_meds: [['m1','Have you started or changed this medication in the last month?']],

    fu_preg: [['p1','Has your obstetric provider cleared you for moderate exercise?']]

  };



  var S = { phase:'general', gen:{}, fup:{}, jt:[], tier:null, why:[], ctx:null, onDone:null, reason:null };



  function get(k, fb){ try { var v = localStorage.getItem(k); return v === null ? fb : JSON.parse(v); } catch(e){ return fb; } }

  function set(k, v){ try { localStorage.setItem(k, JSON.stringify(v)); } catch(e){} }

  function del(k){ try { localStorage.removeItem(k); } catch(e){} }

  function esc(s){ return String(s == null ? '' : s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;'); }



  function lastWorkoutDays(){

    try {

      if (typeof workoutData === 'undefined' || !workoutData || !workoutData.workouts) return null;

      var ws = workoutData.workouts;

      if (!ws.length) return null;

      var last = 0;

      ws.forEach(function(w){ var t = new Date(w.dateCompleted || w.date).getTime(); if (t > last) last = t; });

      if (!last) return null;

      return (Date.now() - last) / 86400000;

    } catch(e){ return null; }

  }



  function shouldScreen(){

    var screened = get(K.s, 0);

    if (screened) {

      var days = lastWorkoutDays();

      if (days !== null && days > 90) return { show:true, reason:'layoff', days:Math.floor(days) };

      var declared = get(K.d, []);

      var saved = get(K.g, {});

      for (var i = 0; i < declared.length; i++) if (!saved[declared[i]]) return { show:true, reason:'new-condition' };

      return { show:false, reason:'complete' };

    }

    /* Never screened: fire once for anyone already using the app. */

    var onboarded = get('p4_onboarded', false);

    var hasHistory = false;

    try {

      if (typeof workoutData !== 'undefined' && workoutData && workoutData.workouts && workoutData.workouts.length > 0) hasHistory = true;

    } catch(e) {}

    if (onboarded || hasHistory) return { show:true, reason: hasHistory ? 'existing-history-no-screen' : 'existing-no-screen' };

    return { show:false, reason:'pre-onboarding' };

  }



  function computeTier(){

    var g = S.gen;

    if (g.g2) return T('C','Chest pain at rest or during activity');

    if (g.g3) return T('C','Dizziness or loss of consciousness');

    if (g.g7) return T('C','Other physician restriction stated');

    if (g.g1) {

      if (S.fup.h1) return T('C','Physician has limited your activity');

      if (S.fup.h2) return T('C','Recent cardiac event or procedure');

      if (S.fup.h1 === false && S.fup.h2 === false) return T('B','Stable cardiac history — intensity ceiling applied');

      return { next:'fu_heart' };

    }

    if (g.g4) {

      if (S.jt.length === 0) return { next:'joints' };

      return T('B','Joint modifications: ' + S.jt.join(', '));

    }

    if (g.g5) {

      if (S.fup.m1) return T('C','Medication started or changed in the last month');

      if (S.fup.m1 === false) return T('B','Medication-managed — intensity ceiling applied');

      return { next:'fu_meds' };

    }

    if (g.g6) {

      if (S.fup.p1) return T('B','Prenatal-safe programming active');

      if (S.fup.p1 === false) return T('C','Obstetric clearance required before exercise');

      return { next:'fu_preg' };

    }

    return T('A', null);

  }

  function T(tier, reason){ S.tier = tier; if (reason) S.why.push(reason); return { done:true }; }



  function renderBody(){

    if (S.tier) {

      var titles = { A:'Clear to train', B:'Clear with modification', C:'Sign-off required' };

      var kinds = { A:'ok', B:'warn', C:'bad' };

      var desc = S.tier === 'A' ? 'No restrictions found. Full workout plan will be generated.' :

                 S.tier === 'B' ? (S.why[0] || 'Modified plan active.') :

                 (S.why[0] || "A doctor's sign-off is required before workout generation.");

      var tierC = S.tier === 'C';

      var h = '<div class="p4-hs-verdict ' + kinds[S.tier] + '"><b>' + titles[S.tier] + '</b>' + esc(desc) + '</div>';



      if (!tierC) {

        h += '<div class="p4-hs-recommend"><b>One last thing</b>This screen is not a medical exam and does not diagnose anything. If you feel unwell on the day, or if anything here leaves you with doubt, delay the workout and talk to your doctor — that is always a reasonable choice, even when nothing was flagged.</div>';

      }



      if (tierC) {

        h += '<div class="p4-hs-tryagain"><b>Not locked out.</b> You can still browse the app. Two ways forward:<br>&bull; <b>Adjust answers</b> &mdash; if something wasn\'t quite right, try again now.<br>&bull; <b>Confirm clearance</b> &mdash; once a doctor signs you off, tap the unlock in Settings.</div>';

      }



      if (!tierC) {

        var checked = S.consent ? ' checked' : '';

        var dis = S.consent ? '' : ' disabled';

        h += '<label class="p4-hs-consent"><input type="checkbox"' + checked + ' onchange="P4.Health.toggleConsent(this.checked)"><span>I accept the <a onclick="if(window.P4&&P4.Legal)P4.Legal.open(\'waiver\');return false;">Liability Waiver &amp; Assumption of Risk</a>, and I understand this screen is not a medical exam or a doctor\'s clearance.</span></label>';

        h += '<div class="p4-hs-actions">';

        h += '<button class="p4-hs-btn outline" onclick="P4.Health.back()"><i class="fas fa-arrow-left"></i> Back</button>';

        h += '<button class="p4-hs-btn primary" id="p4HsContinue"' + dis + ' onclick="P4.Health.finish()">I accept &mdash; Continue</button>';

        h += '</div>';

      } else {

        h += '<div class="p4-hs-legal"><b>This screen is not a diagnosis.</b> It routes you to appropriate activity. Training remains at your own risk.</div>';

        h += '<div class="p4-hs-actions">';

        h += '<button class="p4-hs-btn outline" onclick="P4.Health.back()"><i class="fas fa-arrow-left"></i> Back</button>';

        h += '<button class="p4-hs-btn outline" onclick="P4.Health.tryAgain()">Adjust answers &amp; try again</button>';

        h += '<button class="p4-hs-btn outline" onclick="P4.Health.finish()">Explore app until cleared</button>';

        h += '</div>';

      }



      return h;

    }



    if (S.phase === 'joints') {

      var html = '<h1 class="p4-hs-h1">Which joints?</h1>';

      html += '<p class="p4-hs-lead">You flagged a joint issue. Pick the ones affected — we\'ll auto-skip movements that load them heavily.</p>';

      html += '<div class="p4-hs-jts">';

      JTS.forEach(function(j){

        html += '<button class="p4-hs-jt' + (S.jt.indexOf(j) >= 0 ? ' on' : '') + '" onclick="P4.Health.toggleJoint(\'' + j + '\')">' + j + '</button>';

      });

      html += '</div>';

      html += '<div class="p4-hs-actions">';

      html += '<button class="p4-hs-btn outline" onclick="P4.Health.back()"><i class="fas fa-arrow-left"></i> Back</button>';

      html += '<button class="p4-hs-btn primary" onclick="P4.Health.submit()"' + (S.jt.length === 0 ? ' disabled' : '') + '>Continue</button>';

      html += '</div>';

      return html;

    }



    if (S.phase === 'fu_heart' || S.phase === 'fu_meds' || S.phase === 'fu_preg') {

      var html = '<h1 class="p4-hs-h1">Follow-up</h1>';

      html += '<p class="p4-hs-lead">One level of detail, then we stop asking.</p>';

      FUP[S.phase].forEach(function(pair){

        var id = pair[0], text = pair[1];

        html += '<label class="p4-hs-q"><input type="checkbox"' + (S.fup[id] ? ' checked' : '') + ' onchange="P4.Health.toggleFollowUp(\'' + id + '\')"><span><b>Follow-up</b>' + esc(text) + '</span></label>';

      });

      html += '<div class="p4-hs-actions">';

      html += '<button class="p4-hs-btn outline" onclick="P4.Health.back()"><i class="fas fa-arrow-left"></i> Back</button>';

      html += '<button class="p4-hs-btn primary" onclick="P4.Health.submit()">Continue</button>';

      html += '</div>';

      return html;

    }



    var html = '';

    if (S.ctx === 'standalone' && S.reason !== 'fresh') {

      html += '<div class="p4-hs-note">ℹ️ Quick one-time health check. Won\'t be asked again unless something changes.</div>';

    }

    html += '<h1 class="p4-hs-h1">Health check</h1>';

    html += '<p class="p4-hs-lead">Tap the ones that apply to you, then continue. 7 questions, once.</p>';

    GEN.forEach(function(q){

      var flagged = S.gen[q.id] ? (q.r ? 'flag-red' : 'flag') : '';

      html += '<label class="p4-hs-q ' + flagged + '"><input type="checkbox"' + (S.gen[q.id] ? ' checked' : '') + ' onchange="P4.Health.toggleGeneral(\'' + q.id + '\')"><span><b>' + (q.r ? 'Important' : 'Worth noting') + '</b>' + esc(q.t) + '</span></label>';

    });

    html += '<div class="p4-hs-actions"><button class="p4-hs-btn primary" onclick="P4.Health.submit()">Continue</button></div>';

    return html;

  }



  function refresh(){

    if (S.ctx === 'standalone') {

      var host = document.getElementById('p4HealthOverlay');

      if (host) {

        var card = host.querySelector('.p4-hs-card');

        if (card) card.innerHTML = renderBody();

      }

    } else if (S.ctx === 'wizard') {

      if (window.P4 && P4.Onboard && typeof P4.Onboard.render === 'function') P4.Onboard.render();

    }

  }



  var HS = P4.Health = {

    _S: S,

    shouldScreen: shouldScreen,

    getTier: function(){ return get(K.t, 'A'); },

    isGenerationLocked: function(){

      if (get(K.t, 'A') !== 'C') return false;

      return !get(K.u, 0);

    },

    open: function(reason, onDone){

      S.phase = 'general'; S.gen = {}; S.fup = {}; S.jt = []; S.tier = null; S.why = []; S.phaseHistory = [];

      S.ctx = 'standalone'; S.reason = reason || 'existing'; S.onDone = onDone || null;

      var host = document.getElementById('p4HealthOverlay');

      if (!host) {

        host = document.createElement('div');

        host.id = 'p4HealthOverlay';

        document.body.appendChild(host);

      }

      host.innerHTML = '<div class="p4-hs-card">' + renderBody() + '</div>';

      host.classList.add('on');

    },

    renderWizardBody: function(){ S.ctx = 'wizard'; return renderBody(); },

    close: function(){

      var host = document.getElementById('p4HealthOverlay');

      if (host) host.classList.remove('on');

      S.ctx = null;

    },

    toggleGeneral: function(id){ S.gen[id] = !S.gen[id]; refresh(); },

    toggleFollowUp: function(id){ S.fup[id] = !S.fup[id]; refresh(); },

    toggleJoint: function(j){

      var i = S.jt.indexOf(j);

      if (i >= 0) S.jt.splice(i, 1); else S.jt.push(j);

      refresh();

    },

    toggleConsent: function(checked){

      S.consent = !!checked;

      var btn = document.querySelector('#p4HsContinue');

      if (btn) { btn.disabled = !S.consent; btn.style.opacity = S.consent ? '' : '.5'; }

    },

    submit: function(){

      if (S.phase === 'joints' && S.jt.length === 0) return;

      S.phaseHistory = S.phaseHistory || [];

      S.phaseHistory.push(S.phase);

      var r = computeTier();

      if (r.next) { S.phase = r.next; refresh(); return; }

      set(K.t, S.tier);

      set(K.g, S.gen);

      set(K.f, S.fup);

      set(K.j, S.jt);

      del(K.d);

      refresh();

    },

    back: function(){

      S.tier = null;

      S.why = [];

      S.phaseHistory = S.phaseHistory || [];

      if (S.phaseHistory.length > 0) {

        S.phase = S.phaseHistory.pop();

      } else {

        S.phase = 'general';

      }

      refresh();

    },

    finish: function(){

      if (!S.tier) return;

      if (S.tier !== 'C' && !S.consent) {

        if (P4.toast) P4.toast('Please accept the Liability Waiver to continue.', 'warn');

        return;

      }

      set(K.s, Date.now());

      try {

        if (window.P4 && P4.Legal && P4.Legal.record && S.tier !== 'C') {

          P4.Legal.record('waiver', 'Health screen ' + S.tier + ' \u2014 waiver re-accepted');

        }

      } catch(e) {}

      if (S.ctx === 'standalone') {

        HS.close();

        if (typeof S.onDone === 'function') S.onDone(S.tier);

        setTimeout(applyDashboardLock, 100);

        return;

      }

      if (S.ctx === 'wizard') {

        if (P4.Onboard) {

          P4.Onboard._healthVerdictAccepted = true;

          P4.Onboard._healthDone = true;

        }

        if (P4.Onboard && typeof P4.Onboard.render === 'function') P4.Onboard.render();

        return;

      }

    },

    tryAgain: function(){

      S.phase = 'general'; S.gen = {}; S.fup = {}; S.jt = []; S.tier = null; S.why = [];

      refresh();

    },

    confirmClearance: function(){

      if (!confirm('Confirm your doctor has cleared you for exercise?')) return;

      set(K.u, Date.now());

      if (P4.toast) P4.toast('Clearance recorded. Workout generation unlocked.', 'ok');

      setTimeout(function(){ location.reload(); }, 700);

    },

    reset: function(){

      del(K.s); del(K.t); del(K.g); del(K.f); del(K.j); del(K.u);

      HS.open('existing', function(){ location.reload(); });

    }

  };



  /* ---- Wizard integration: intercept step 3 ---- */

  function hookWizard(){

    if (!window.P4 || !P4.Onboard) return;

    if (P4.Onboard._healthHooked) return;

    P4.Onboard._healthHooked = true;



    var origS3 = P4.Onboard.s3;

    if (typeof origS3 === 'function') {

      P4.Onboard.s3 = function(){

        if (!get(K.s, 0) && !P4.Onboard._healthDone && !P4.Onboard._healthVerdictAccepted) {

          S.ctx = 'wizard';

          if (!S._begun) {

            S.phase = 'general'; S.gen = {}; S.fup = {}; S.jt = []; S.tier = null; S.why = [];

            S._begun = true;

          }

          return HS.renderWizardBody();

        }

        return origS3.apply(this, arguments);

      };

    }



    /* Consent reduction: drop obC2 from s5 */

    var origS5 = P4.Onboard.s5;

    if (typeof origS5 === 'function') {

      P4.Onboard.s5 = function(){

        var html = origS5.apply(this, arguments);

        html = html.replace(/<label class="p4-wz-check"><input type="checkbox" id="obC2">[\s\S]*?<\/label>/, '');

        return html;

      };

    }



    /* Fix the finish check to not require obC2 */

    var origWire = P4.Onboard.wire;

    if (typeof origWire === 'function') {

      P4.Onboard.wire = function(){

        var r = origWire.apply(this, arguments);

        var host = document.getElementById('p4WizardHost');

        if (!host) return r;

        var fin = host.querySelector('[data-nav="finish"]');

        if (fin && !fin._p4healthFixed) {

          fin._p4healthFixed = true;

          var clone = fin.cloneNode(true);

          fin.parentNode.replaceChild(clone, fin);

          clone.addEventListener('click', function(e){

            e.preventDefault();

            var minor = P4.Onboard.profile.minor;

            var need = minor ? 3 : 2;

            var ok = 0;

            ['obC1','obC3','obC4'].forEach(function(id){

              var c = host.querySelector('#' + id);

              if (c && c.checked) ok++;

            });

            if (ok < need) {

              if (P4.toast) P4.toast('Please tick every box — each one protects you.', 'warn');

              return;

            }

            P4.Onboard.finish();

          });

        }

        return r;

      };

    }



    var origFinish = P4.Onboard.finish;

    if (typeof origFinish === 'function' && !P4.Onboard._healthFinishHooked) {

      P4.Onboard._healthFinishHooked = true;

      P4.Onboard.finish = function(){

        var r = origFinish.apply(this, arguments);

        setTimeout(applyDashboardLock, 200);

        return r;

      };

    }

  }



  /* ---- Dashboard lock for Tier C ---- */

  function applyDashboardLock(){

    if (!HS.isGenerationLocked()) return;

    var btn = document.querySelector('.btn-luxury-start');

    if (!btn || btn._p4healthLocked) return;

    btn._p4healthLocked = true;

    btn.style.cssText += 'opacity:.55;filter:grayscale(.6);cursor:not-allowed;';

    btn.innerHTML = '<i class="fas fa-lock"></i> <span>Locked — see note below</span>';

    btn.onclick = function(e){ e.preventDefault(); if (P4.toast) P4.toast('Confirm doctor clearance in Settings to unlock.', 'info'); };

    var parent = btn.parentNode;

    if (parent && !parent.querySelector('.p4-health-lock-note')) {

      var note = document.createElement('div');

      note.className = 'p4-health-lock-note';

      note.innerHTML = '<b>Workout generation is paused.</b> Your health screen flagged an item that needs a doctor\'s sign-off. You can still browse Library, History, and Settings. <a onclick="P4.Health.confirmClearance();">I\'ve been cleared</a>';

      parent.appendChild(note);

    }

  }



  /* ---- Boot ---- */

  function boot(){

    hookWizard();

    var s = shouldScreen();

    if (s.show) {

      setTimeout(function(){ HS.open(s.reason); }, 900);

    } else {

      setTimeout(applyDashboardLock, 500);

    }

  }



  if (document.readyState === 'loading') {

    document.addEventListener('DOMContentLoaded', function(){ setTimeout(boot, 900); });

  } else {

    setTimeout(boot, 900);

  }



  /* Re-check lock on dashboard render */

  if (typeof window.updateDashboard === 'function' && !window.updateDashboard._p4health) {

    var origUpdate = window.updateDashboard;

    window.updateDashboard = function(){

      var r = origUpdate.apply(this, arguments);

      setTimeout(applyDashboardLock, 100);

      return r;

    };

    window.updateDashboard._p4health = true;

  }



  console.log('[P4 Health v2] 10-physician panel edition loaded. Migration boot armed.');

})();

// ---- END extracted from index (24).html L64637-65057 (P4 Health Screen v2 (10-physician panel)) ----

// ---- BEGIN extracted from index (24).html L65416-65536 (p4-boot-sync-js) ----
(function(){

  'use strict';

  if (window.__p4BootSync) return;

  window.__p4BootSync = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 Boot Sync]', m); } catch(e){} }



  function profileNameFromUser(){

    try {

      var u = (typeof workoutData !== 'undefined' && workoutData && workoutData.user) ? workoutData.user : null;

      return (u && typeof u.name === 'string') ? u.name.trim() : '';

    } catch(e){ return ''; }

  }



  function syncProfileName(){

    try {

      if (!P4.Profiles || !P4.Profiles.current || !P4.Profiles.update) return;

      var p = P4.Profiles.current();

      if (!p) return;

      var n = profileNameFromUser();

      if (n && n !== p.name) {

        P4.Profiles.update(p.id, { name: n });

        log('Profile name synced: "' + p.name + '" -> "' + n + '"');

      }

    } catch(e){ log('syncProfileName failed: ' + e.message); }

  }



  function refreshNavbarName(){

    try { if (P4.Picker && P4.Picker.refreshNavbar) P4.Picker.refreshNavbar(); } catch(e){}

    try { if (typeof updateHeaderNameAndStreak === 'function') updateHeaderNameAndStreak(); } catch(e){}

  }



  function reRenderSettings(){

    try {

      var sec = document.getElementById('settings-section');

      if (!sec || !sec.classList.contains('active')) return;

      if (P4.SettingsUI && P4.SettingsUI.inject) {

        P4.SettingsUI.inject();

        log('Settings re-rendered with loaded data');

      }

    } catch(e){ log('reRenderSettings failed: ' + e.message); }

  }



  function afterDataLoaded(reason){

    log('data loaded (' + reason + ')');

    syncProfileName();

    refreshNavbarName();

    reRenderSettings();

  }



  /* Hook 1 — window.appReady (fires after loadAllData) */

  (function(){

    var tries = 0;

    var iv = setInterval(function(){

      tries++;

      if (typeof window.appReady === 'function' && !window.appReady._p4BootSync) {

        var orig = window.appReady;

        window.appReady = function(){

          window.__p4appReadyFired = true;

          var r = orig.apply(this, arguments);

          setTimeout(function(){ afterDataLoaded('appReady'); }, 50);

          return r;

        };

        window.appReady._p4BootSync = true;

        clearInterval(iv);

        log('appReady hooked');

      }

      if (tries > 40) clearInterval(iv);

    }, 100);

    /* Fallback: if appReady fired before we hooked, still try once data is populated */

    setTimeout(function(){

      if (window.__p4appReadyFired) return;

      var loaded = false;

      try {

        if (typeof workoutData !== 'undefined' && workoutData && workoutData.user && workoutData.user.name) loaded = true;

        if (typeof workoutData !== 'undefined' && workoutData && workoutData.workouts && workoutData.workouts.length > 0) loaded = true;

      } catch(e){}

      if (loaded) afterDataLoaded('fallback-poll');

    }, 2500);

  })();



  /* Hook 2 — showSection('settings') */

  (function(){

    if (typeof window.showSection !== 'function' || window.showSection._p4BootSync) return;

    var orig = window.showSection;

    window.showSection = function(id){

      var r = orig.apply(this, arguments);

      if (id === 'settings') {

        setTimeout(function(){

          try { syncProfileName(); } catch(e){}

          try { reRenderSettings(); } catch(e){}

        }, 30);

      }

      return r;

    };

    window.showSection._p4BootSync = true;

    log('showSection hooked for settings');

  })();



  /* Hook 3 — P4.Profiles.switchTo */

  (function(){

    if (!P4.Profiles || !P4.Profiles.switchTo || P4.Profiles.switchTo._p4BootSync) return;

    var orig = P4.Profiles.switchTo;

    P4.Profiles.switchTo = function(){

      var r = orig.apply(this, arguments);

      setTimeout(function(){

        syncProfileName();

        refreshNavbarName();

        reRenderSettings();

      }, 100);

      return r;

    };

    P4.Profiles.switchTo._p4BootSync = true;

    log('Profiles.switchTo hooked');

  })();



  log('armed');

})();

// ---- END extracted from index (24).html L65416-65536 (p4-boot-sync-js) ----

// ---- BEGIN extracted from index (24).html L65537-65697 (p4-race-fix-js) ----
(function(){

  'use strict';

  if (window.__p4RaceFix) return;

  window.__p4RaceFix = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 Race Fix]', m); } catch(e){} }

  function call(fn){ try { if (typeof fn === 'function') fn(); } catch(e){ log('render threw: ' + e.message); } }



  // Single broadcast — every racy render, in the right order.

  function p4Rebroadcast(origin){

    log('rebroadcast (' + origin + ')');



    // Legacy app

    call(window.updateDashboard);

    call(window.updateLongevityScoreDisplay);

    call(window.updateRecoverySection);

    call(window.updateHeaderNameAndStreak);

    call(window.updatePeriodButtonVisibility);

    call(window.updateResumeButtonVisibility);

    call(window.syncTrialWorkoutCount);



    // Navbar name — picker owns it now

    try { if (P4.Picker && P4.Picker.refreshNavbar) P4.Picker.refreshNavbar(); } catch(e){}



    // Sync profile name from workoutData (matches boot-sync patch)

    try {

      if (P4.Profiles && P4.Profiles.current && P4.Profiles.update) {

        var p = P4.Profiles.current();

        var n = (workoutData && workoutData.user && workoutData.user.name || '').trim();

        if (p && n && p.name !== n) P4.Profiles.update(p.id, { name: n });

      }

    } catch(e){}



    // P4 Settings — re-render if visible

    try {

      var sec = document.getElementById('settings-section');

      if (sec && sec.classList.contains('active') && P4.SettingsUI && P4.SettingsUI.inject) {

        P4.SettingsUI.inject();

      }

    } catch(e){}



    // MP dashboard cards — re-render if dashboard is visible

    try {

      var dash = document.getElementById('dashboard-section');

      if (dash && dash.classList.contains('active')) {

        call(window.mpRenderComponentHero);

        call(window.mpRenderWeekStrip);

        call(window.mpRenderGoalCycleCard);

        call(window.mpRenderFiveYearCard);

      }

    } catch(e){}



    // P4 dashboard add-ons

    call(function(){ if (P4.Rings && P4.Rings.render) P4.Rings.render(); });

    call(function(){ if (P4.LifeStage && P4.LifeStage.card) P4.LifeStage.card(); });

    call(function(){ if (P4.Motivation && P4.Motivation.renderDashboard) P4.Motivation.renderDashboard(); });

    call(function(){ if (P4.Motivation && P4.Motivation.evaluate) P4.Motivation.evaluate(); });



    // Paywall counter refresh

    call(function(){ if (P4.Paywall && P4.Paywall.refreshUI) P4.Paywall.refreshUI(); });

  }

  window.p4Rebroadcast = p4Rebroadcast;



  // ---- Hook 1: wrap loadAllData (async fn at top level => window.loadAllData) ----

  (function(){

    var tries = 0;

    var iv = setInterval(function(){

      tries++;

      if (typeof window.loadAllData === 'function' && !window.loadAllData._p4RaceFix) {

        var orig = window.loadAllData;

        window.loadAllData = async function(){

          var r = await orig.apply(this, arguments);

          setTimeout(function(){ p4Rebroadcast('loadAllData'); }, 40);

          return r;

        };

        window.loadAllData._p4RaceFix = true;

        clearInterval(iv);

        log('loadAllData hooked');

      }

      if (tries > 60) clearInterval(iv);

    }, 50);

  })();



  // ---- Hook 2: appReady (fires after loadAllData in the app's own handler) ----

  (function(){

    var tries = 0;

    var iv = setInterval(function(){

      tries++;

      if (typeof window.appReady === 'function' && !window.appReady._p4RaceFix) {

        var orig = window.appReady;

        window.appReady = function(){

          var r = orig.apply(this, arguments);

          setTimeout(function(){ p4Rebroadcast('appReady'); }, 60);

          return r;

        };

        window.appReady._p4RaceFix = true;

        clearInterval(iv);

        log('appReady hooked');

      }

      if (tries > 60) clearInterval(iv);

    }, 50);

  })();



  // ---- Hook 3: importEverything — write to Dexie, not just localStorage ----

  (function(){

    function patchImport(){

      if (!P4.Backup || !P4.Backup.importEverything) { setTimeout(patchImport, 300); return; }

      if (P4.Backup.importEverything._p4RaceFix) return;

      var orig = P4.Backup.importEverything;

      P4.Backup.importEverything = async function(text){

        // Read the payload here so we can also push it to Dexie.

        var heal = (window.P4 && P4.SEC && P4.SEC.healJson) ? P4.SEC.healJson(text) : null;

        var payload = (heal && heal.ok) ? heal.data : null;



        var r = await orig.apply(this, arguments);



        // Only after successful import — mirror into Dexie.

        if (r && payload && payload.liveWorkoutData && typeof db !== 'undefined' && db) {

          try {

            var wd = payload.liveWorkoutData;

            if (wd.workouts && wd.workouts.length) {

              await db.workouts.clear();

              await db.workouts.bulkPut(wd.workouts.filter(function(w){ return w && w.id; }));

              log('Dexie workouts restored: ' + wd.workouts.length);

            }

            if (wd.exercises) {

              await db.exercises.clear();

              var recs = Object.keys(wd.exercises).map(function(id){

                var o = wd.exercises[id]; o.id = id; return o;

              });

              await db.exercises.bulkPut(recs);

              log('Dexie exercises restored: ' + recs.length);

            }

            if (wd.user) {

              await db.user.put({ key: 'main', value: wd.user });

              log('Dexie user restored');

            }

          } catch(e){ log('Dexie restore threw: ' + e.message); }

        }

        return r;

      };

      P4.Backup.importEverything._p4RaceFix = true;

      log('importEverything hooked to write Dexie');

    }

    patchImport();

  })();



  // ---- Safety net: if loadAllData somehow never fires, rebroadcast at 6s ----

  setTimeout(function(){

    try {

      if (typeof workoutData !== 'undefined' && workoutData && workoutData.workouts && workoutData.workouts.length > 0) {

        p4Rebroadcast('safety-net');

      }

    } catch(e){}

  }, 6000);



  log('armed');

})();

// ---- END extracted from index (24).html L65537-65697 (p4-race-fix-js) ----

// ---- BEGIN extracted from index (24).html L65698-65963 (p4-user-reconcile-js) ----
(function(){

  'use strict';

  if (window.__p4UserReconcile) return;

  window.__p4UserReconcile = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 User]', m); } catch(e){} }

  function now(){ return Date.now(); }

  function nz(s){ return (typeof s === 'string' && s.trim().length > 0) ? s.trim() : ''; }



  // ---- Read all sources. Each returns {name, birthDate, gender, weight, at} ----

  function readDexieUser(){

    return new Promise(function(resolve){

      try {

        if (typeof db === 'undefined' || !db) return resolve(null);

        db.user.get('main').then(function(rec){

          if (!rec || !rec.value) return resolve(null);

          var v = rec.value;

          resolve({ name: nz(v.name), birthDate: v.birthDate || '', gender: v.gender || '', weight: v.weight || '', at: v._p4SavedAt || 0, src: 'dexie' });

        }).catch(function(){ resolve(null); });

      } catch(e){ resolve(null); }

    });

  }

  function readLocalStorageWD(){

    try {

      var raw = localStorage.getItem('workoutData');

      if (!raw) return null;

      var o = JSON.parse(raw);

      if (!o || !o.user) return null;

      var u = o.user;

      return { name: nz(u.name), birthDate: u.birthDate || '', gender: u.gender || '', weight: u.weight || '', at: u._p4SavedAt || 0, src: 'localStorage.workoutData' };

    } catch(e){ return null; }

  }

  function readProfileData(){

    try {

      if (!P4.Profiles || !P4.Profiles.current) return null;

      var p = P4.Profiles.current();

      if (!p) return null;

      var raw = localStorage.getItem('p4_profile_data_' + p.id);

      if (!raw) return null;

      var o = JSON.parse(raw);

      if (!o || !o.user) return null;

      var u = o.user;

      return { name: nz(u.name), birthDate: u.birthDate || '', gender: u.gender || '', weight: u.weight || '', at: u._p4SavedAt || 0, src: 'profileData' };

    } catch(e){ return null; }

  }

  function readProfileMeta(){

    try {

      if (!P4.Profiles || !P4.Profiles.current) return null;

      var p = P4.Profiles.current();

      if (!p) return null;

      return { name: nz(p.name), birthDate: '', gender: '', weight: '', at: p.updatedAt || p.createdAt || 0, src: 'profileMeta' };

    } catch(e){ return null; }

  }

  function readOnboardingProfile(){

    try {

      var raw = localStorage.getItem('p4_profile');

      if (!raw) return null;

      var o = JSON.parse(raw);

      if (!o) return null;

      return { name: nz(o.name), birthDate: o.birth || '', gender: o.gender || '', weight: '', at: o._p4SavedAt || 0, src: 'onboardProfile' };

    } catch(e){ return null; }

  }

  function readMemory(){

    try {

      if (typeof workoutData === 'undefined' || !workoutData || !workoutData.user) return null;

      var u = workoutData.user;

      return { name: nz(u.name), birthDate: u.birthDate || '', gender: u.gender || '', weight: u.weight || '', at: u._p4SavedAt || 0, src: 'memory' };

    } catch(e){ return null; }

  }



  // ---- Merge: non-empty wins; tie-break by timestamp; final tie-break by source priority ----

  var PRIORITY = ['dexie', 'profileData', 'profileMeta', 'onboardProfile', 'localStorage.workoutData', 'memory'];

  function merge(candidates){

    var best = {};

    ['name','birthDate','gender','weight'].forEach(function(f){

      var chosen = null;

      candidates.forEach(function(c){

        if (!c) return;

        var v = c[f];

        if (!v) return;

        if (!chosen) { chosen = { v: v, at: c.at || 0, src: c.src }; return; }

        if ((c.at || 0) > chosen.at) { chosen = { v: v, at: c.at || 0, src: c.src }; }

        else if ((c.at || 0) === chosen.at) {

          var newRank = PRIORITY.indexOf(c.src);

          var curRank = PRIORITY.indexOf(chosen.src);

          if (newRank >= 0 && (curRank < 0 || newRank < curRank)) { chosen = { v: v, at: c.at || 0, src: c.src }; }

        }

      });

      if (chosen) best[f] = chosen.v;

    });

    return best;

  }



  // ---- Write the winner to every store ----

  function writeAll(merged, stamp){

    var writes = [];

    try {

      if (typeof workoutData !== 'undefined' && workoutData) {

        if (!workoutData.user) workoutData.user = {};

        ['name','birthDate','gender','weight'].forEach(function(f){

          if (merged[f] !== undefined) workoutData.user[f] = merged[f];

        });

        workoutData.user._p4SavedAt = stamp;

      }

    } catch(e){}



    try {

      var raw = localStorage.getItem('workoutData');

      var o = raw ? JSON.parse(raw) : { user: {}, workouts: [], exercises: {} };

      if (!o.user) o.user = {};

      ['name','birthDate','gender','weight'].forEach(function(f){

        if (merged[f] !== undefined) o.user[f] = merged[f];

      });

      o.user._p4SavedAt = stamp;

      localStorage.setItem('workoutData', JSON.stringify(o));

    } catch(e){}



    try {

      if (P4.Profiles && P4.Profiles.current) {

        var p = P4.Profiles.current();

        if (p && merged.name && p.name !== merged.name) {

          P4.Profiles.update(p.id, { name: merged.name });

        }

        if (p && merged.name) {

          var k = 'p4_profile_data_' + p.id;

          var raw2 = localStorage.getItem(k);

          var o2 = raw2 ? JSON.parse(raw2) : { user: {}, workouts: [], exercises: {} };

          if (!o2.user) o2.user = {};

          ['name','birthDate','gender','weight'].forEach(function(f){

            if (merged[f] !== undefined) o2.user[f] = merged[f];

          });

          o2.user._p4SavedAt = stamp;

          localStorage.setItem(k, JSON.stringify(o2));

        }

      }

    } catch(e){}



    if (typeof db !== 'undefined' && db && Object.keys(merged).length) {

      try {

        writes.push(db.user.get('main').then(function(rec){

          var v = (rec && rec.value) || {};

          ['name','birthDate','gender','weight'].forEach(function(f){

            if (merged[f] !== undefined) v[f] = merged[f];

          });

          v._p4SavedAt = stamp;

          return db.user.put({ key: 'main', value: v });

        }).catch(function(e){ log('dexie write failed: ' + e.message); }));

      } catch(e){}

    }

    return Promise.all(writes);

  }



  // ---- Refresh every UI surface that shows a name ----

  function refreshUI(merged){

    try { if (typeof window.updateHeaderNameAndStreak === 'function') window.updateHeaderNameAndStreak(); } catch(e){}

    try { if (P4.Picker && P4.Picker.refreshNavbar) P4.Picker.refreshNavbar(); } catch(e){}



    // Live-update the P4 settings inputs if the card exists

    try {

      var el;

      el = document.getElementById('p4SetName');   if (el && merged.name !== undefined && el.value !== merged.name) el.value = merged.name;

      el = document.getElementById('p4SetBirth');  if (el && merged.birthDate !== undefined && el.value !== merged.birthDate) el.value = merged.birthDate;

      el = document.getElementById('p4SetGender'); if (el && merged.gender !== undefined && el.value !== merged.gender) el.value = merged.gender;

      el = document.getElementById('p4SetWeight'); if (el && merged.weight !== undefined && el.value !== merged.weight) el.value = merged.weight;

      // Legacy (original) settings form fields too

      el = document.getElementById('settingsName');      if (el && merged.name !== undefined && el.value !== merged.name) el.value = merged.name;

      el = document.getElementById('settingsBirthDate'); if (el && merged.birthDate !== undefined && el.value !== merged.birthDate) el.value = merged.birthDate;

      el = document.getElementById('settingsGender');    if (el && merged.gender !== undefined && el.value !== merged.gender) el.value = merged.gender;

      el = document.getElementById('settingsWeight');    if (el && merged.weight !== undefined && el.value !== merged.weight) el.value = merged.weight;

    } catch(e){}

  }



  // ---- Main reconciler ----

  function reconcile(origin){

    var local = readLocalStorageWD();

    var pd = readProfileData();

    var pm = readProfileMeta();

    var op = readOnboardingProfile();

    var mem = readMemory();



    return readDexieUser().then(function(dex){

      var candidates = [dex, local, pd, pm, op, mem].filter(Boolean);

      var merged = merge(candidates);

      if (!Object.keys(merged).length) { log('reconcile (' + origin + '): nothing to merge'); return {}; }

      log('reconcile (' + origin + '): ' + Object.keys(merged).map(function(k){ return k + '=' + merged[k]; }).join(', '));

      return writeAll(merged, now()).then(function(){

        refreshUI(merged);

        return merged;

      });

    }).catch(function(e){ log('reconcile failed: ' + e.message); return {}; });

  }



  P4.User = P4.User || {};

  P4.User.reconcile = reconcile;



  // ---- When saving: stamp and reconcile immediately so writes propagate ----

  (function(){

    function patchSave(){

      if (typeof window.saveWorkoutData !== 'function' || window.saveWorkoutData._p4Reconcile) return false;

      var orig = window.saveWorkoutData;

      var wrapped = function(){

        try {

          if (workoutData && workoutData.user) workoutData.user._p4SavedAt = now();

        } catch(e){}

        var r = orig.apply(this, arguments);

        // Fire-and-forget: write the same stamp to all stores

        setTimeout(function(){ reconcile('post-save'); }, 30);

        return r;

      };

      wrapped._p4Reconcile = true;

      window.saveWorkoutData = wrapped;

      return true;

    }

    if (!patchSave()) { var tries=0; var iv=setInterval(function(){ tries++; if (patchSave() || tries>40) clearInterval(iv); }, 200); }

  })();



  // ---- On settings open, always reconcile fresh ----

  (function(){

    var tries=0;

    var iv=setInterval(function(){

      tries++;

      if (typeof window.showSection === 'function' && !window.showSection._p4Reconcile) {

        var orig = window.showSection;

        window.showSection = function(id){

          var r = orig.apply(this, arguments);

          if (id === 'settings') {

            setTimeout(function(){ reconcile('settings-open'); }, 40);

            setTimeout(function(){ reconcile('settings-open-late'); }, 400);

          }

          return r;

        };

        window.showSection._p4Reconcile = true;

        clearInterval(iv);

        log('showSection hooked');

      }

      if (tries > 60) clearInterval(iv);

    }, 100);

  })();



  // ---- On returning to tab, reconcile ----

  document.addEventListener('visibilitychange', function(){

    if (!document.hidden) setTimeout(function(){ reconcile('visibility'); }, 80);

  });



  // ---- On boot, run twice: once early, once after loadAllData settles ----

  setTimeout(function(){ reconcile('boot-early'); }, 600);

  setTimeout(function(){ reconcile('boot-late'); }, 2500);

  setTimeout(function(){ reconcile('boot-settled'); }, 5500);



  // ---- Also watch for the settings section becoming active without showSection ----

  try {

    var mo = new MutationObserver(function(){

      var sec = document.getElementById('settings-section');

      if (sec && sec.classList.contains('active')) {

        var inp = document.getElementById('p4SetName');

        if (inp && !inp.value) reconcile('settings-mutation');

      }

    });

    mo.observe(document.body, { childList: true, subtree: true, attributes: true, attributeFilter: ['class'] });

  } catch(e){}



  log('reconcile armed — five sources, one truth');

})();

// ---- END extracted from index (24).html L65698-65963 (p4-user-reconcile-js) ----

// ---- BEGIN extracted from index (24).html L65964-65983 (p4-unlock-all-js) ----
(function(){

  'use strict';

  if (window.__p4UnlockAll) return;

  window.__p4UnlockAll = true;

  if (!window.P4 || !P4.Paywall) return;



  P4.Paywall.isPro = function(){ return true; };

  P4.Paywall.isAllowed = function(){ return true; };

  P4.Paywall.subActive = function(){ return true; };

  P4.Paywall.trialActive = function(){ return true; };

  P4.Paywall.daysLeft = function(){ return 999; };

  P4.Paywall.ensureTrial = function(){};

  P4.Paywall.showFullPage = function(){ try { console.log('[P4] Paywall suppressed — everything is free'); } catch(e){} };

  P4.Paywall.showPanel = function(){ try { console.log('[P4] Paywall panel suppressed'); } catch(e){} };

  P4.Paywall.gate = function(){ return true; };



  try { console.log('[P4] All features unlocked — no gate, no color lock, nothing'); } catch(e){}

})();

// ---- END extracted from index (24).html L65964-65983 (p4-unlock-all-js) ----

// ---- BEGIN extracted from index (24).html L65984-66121 (p4-wizard-guard-js) ----
(function(){

  'use strict';

  if (window.__p4WizardGuard) return;

  window.__p4WizardGuard = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 Wizard Guard]', m); } catch(e){} }



  function hasAnyUserData(){

    try {

      if (typeof workoutData !== 'undefined' && workoutData) {

        if (workoutData.workouts && workoutData.workouts.length > 0) return 'memory.workouts';

        if (workoutData.exercises && Object.keys(workoutData.exercises).length > 0) return 'memory.exercises';

        if (workoutData.user && (workoutData.user.name || workoutData.user.birthDate)) return 'memory.user';

      }

      try {

        var raw = localStorage.getItem('workoutData');

        if (raw) {

          var o = JSON.parse(raw);

          if (o && ((o.workouts && o.workouts.length > 0) || (o.user && o.user.name))) return 'localStorage.workoutData';

        }

      } catch(e){}

      for (var i = 0; i < localStorage.length; i++) {

        var k = localStorage.key(i);

        if (k && k.indexOf('p4_profile_data_') === 0) {

          try {

            var pd = JSON.parse(localStorage.getItem(k));

            if (pd && (pd.user || (pd.workouts && pd.workouts.length > 0))) return 'profileData';

          } catch(e){}

        }

      }

      try {

        var profs = JSON.parse(localStorage.getItem('p4_profiles') || '[]');

        for (var j = 0; j < profs.length; j++) {

          var nm = (profs[j].name || '').trim().toLowerCase();

          if (nm && nm !== 'you' && nm !== 'user' && nm !== 'new profile') return 'profileMeta.name';

        }

      } catch(e){}

      try {

        var op = JSON.parse(localStorage.getItem('p4_profile') || 'null');

        if (op && op.name) return 'onboardProfile';

      } catch(e){}

      try { if (localStorage.getItem('p4_onboarded') === 'true') return 'p4_onboarded'; } catch(e){}

      try { if (localStorage.getItem('p4_health_screened_at')) return 'p4_health_screened_at'; } catch(e){}

      try { if (localStorage.getItem('p4_last_backup')) return 'p4_last_backup'; } catch(e){}

    } catch(e){}

    return null;

  }



  function killWizard(reason){

    try {

      var host = document.getElementById('p4WizardHost');

      if (host && host.innerHTML.trim()) {

        host.innerHTML = '';

        document.body.style.overflow = '';

        log('killed wizard (' + reason + ')');

      }

      if (P4.Onboard) {

        P4.Onboard._healthDone = true;

        P4.Onboard._healthVerdictAccepted = true;

        try { localStorage.setItem('p4_onboarded', 'true'); } catch(e){}

      }

    } catch(e){}

  }



  function shouldSkipWizard(){

    var signal = hasAnyUserData();

    if (signal) { log('data detected (' + signal + ') — wizard suppressed'); return true; }

    return false;

  }



  function patchNeeded(){

    if (!P4.Onboard || P4.Onboard._guardPatched) return;

    P4.Onboard._guardPatched = true;

    var orig = P4.Onboard.needed;

    P4.Onboard.needed = function(){

      if (shouldSkipWizard()) return false;

      try { return orig.apply(this, arguments); } catch(e){ return false; }

    };

    log('P4.Onboard.needed patched');

  }



  function patchStart(){

    if (!P4.Onboard || P4.Onboard._guardStartPatched) return;

    P4.Onboard._guardStartPatched = true;

    var orig = P4.Onboard.start;

    P4.Onboard.start = function(){

      if (shouldSkipWizard()) { killWizard('start-blocked'); return; }

      return orig.apply(this, arguments);

    };

    log('P4.Onboard.start patched');

  }



  function watchDOM(){

    try {

      var mo = new MutationObserver(function(){

        var host = document.getElementById('p4WizardHost');

        if (host && host.innerHTML.trim() && shouldSkipWizard()) {

          killWizard('dom-watch');

        }

      });

      mo.observe(document.body, { childList: true, subtree: true });

    } catch(e){}

  }



  function hookAppReady(){

    var tries = 0;

    var iv = setInterval(function(){

      tries++;

      if (typeof window.appReady === 'function' && !window.appReady._p4WizardGuard) {

        var orig = window.appReady;

        window.appReady = function(){

          var r = orig.apply(this, arguments);

          setTimeout(function(){ if (shouldSkipWizard()) killWizard('appReady'); }, 50);

          return r;

        };

        window.appReady._p4WizardGuard = true;

        clearInterval(iv);

      }

      if (tries > 40) clearInterval(iv);

    }, 100);

  }



  document.addEventListener('visibilitychange', function(){

    if (!document.hidden && shouldSkipWizard()) killWizard('visibility');

  });



  setTimeout(function(){

    patchNeeded(); patchStart(); watchDOM(); hookAppReady();

    if (shouldSkipWizard()) killWizard('boot');

  }, 300);

  setTimeout(function(){ if (shouldSkipWizard()) killWizard('boot-late'); }, 2500);

  setTimeout(function(){ if (shouldSkipWizard()) killWizard('boot-settled'); }, 5500);



  log('armed');

})();

// ---- END extracted from index (24).html L65984-66121 (p4-wizard-guard-js) ----

// ---- BEGIN extracted from index (24).html L66122-66282 (p4-id-protect-js) ----
/* ============================================================================

 * P4 ID PROTECTION — DO NOT REMOVE, DO NOT ROTATE THESE IDS.

 *

 * Why this exists:

 *   The app's loadData() calls localStorage.clear(), which wipes every

 *   paywall key. When that happens the paywall's boot() sees no app ID,

 *   generates a brand-new one, and treats the device as if it just installed.

 *   Approvals and subscriptions bound to the old ID are orphaned forever.

 *

 * What this script does:

 *   1. Ensures p4_pw_app_id and p4_pw_userId exist and persist.

 *   2. Pre-seeds an "approved" application entry so the paywall's own

 *      isAllowedSync() returns true on the very first check.

 *   3. Wraps every future localStorage.clear() so P4 keys survive the wipe.

 *

 * WARNING FOR FUTURE MAINTAINERS:

 *   Do NOT rotate, regenerate, or hash-scope these IDs. They identify the

 *   user's device to the payment worker. Rotating them:

 *     - resets the 21-workout trial (exploit)

 *     - orphans paid subscriptions

 *     - orphans approvals

 *     - makes the worker see a "new user" on every reload

 *   If a genuine reset is needed, remove them only inside the explicit

 *   "Reset All Data" flow, after a full backup has been exported.

 * ============================================================================ */

(function(){

  'use strict';

  if (window.__p4IdProtect) return;

  window.__p4IdProtect = true;



  var PRESERVE_PREFIX = 'p4_';

  var PRESERVE_EXTRA = ['deviceId', 'workout_app_version', 'workoutTheme', 'workoutDarkMode', 'suppressedNotifications'];



  function log(m){ try { console.log('[P4 ID Protect]', m); } catch(e){} }



  function ensureKeys(){

    try {

      var appId = localStorage.getItem('p4_pw_app_id');

      if (!appId) {

        appId = 'APP-' + Math.random().toString(36).substring(2, 10).toUpperCase() + '-' + Date.now().toString(36).toUpperCase();

        localStorage.setItem('p4_pw_app_id', appId);

        log('seeded p4_pw_app_id: ' + appId);

      }

      var userId = localStorage.getItem('p4_pw_userId');

      if (!userId) {

        userId = (window.crypto && crypto.randomUUID) ? crypto.randomUUID()

          : 'u_' + Date.now().toString(36) + '_' + Math.random().toString(36).slice(2, 10);

        localStorage.setItem('p4_pw_userId', userId);

        log('seeded p4_pw_userId: ' + userId);

      }

      // v2 legacy ID (may still be read by older paths)

      if (!localStorage.getItem('p4_app_id')) {

        localStorage.setItem('p4_app_id', appId);

        log('seeded p4_app_id (legacy alias)');

      }

      return { appId: appId, userId: userId };

    } catch(e){ log('ensureKeys failed: ' + e.message); return null; }

  }



  function seedApproval(ids){

    if (!ids) return;

    try {

      localStorage.setItem('p4_pw_approved', 'true');

      var apps = [];

      try { apps = JSON.parse(localStorage.getItem('p4_pw_applications') || '[]'); } catch(e){ apps = []; }

      if (!Array.isArray(apps)) apps = [];

      var has = apps.some(function(a){ return a && a.id === ids.appId && a.status === 'approved'; });

      if (!has) {

        apps.push({ id: ids.appId, platform: 'preseeded', status: 'approved', createdAt: Date.now() });

        localStorage.setItem('p4_pw_applications', JSON.stringify(apps));

        log('seeded approved application entry');

      }

      if (!localStorage.getItem('p4_pw_sub_cache')) {

        localStorage.setItem('p4_pw_sub_cache', JSON.stringify({ active: true, timestamp: Date.now() }));

        log('seeded p4_pw_sub_cache');

      }

      if (!localStorage.getItem('p4_pw_sub_expires_at')) {

        localStorage.setItem('p4_pw_sub_expires_at', String(Date.now() + 365 * 86400000));

        log('seeded p4_pw_sub_expires_at');

      }

    } catch(e){ log('seedApproval failed: ' + e.message); }

  }



  function isPreserved(k){

    if (!k) return false;

    if (k.indexOf(PRESERVE_PREFIX) === 0) return true;

    for (var i = 0; i < PRESERVE_EXTRA.length; i++) {

      if (k === PRESERVE_EXTRA[i]) return true;

    }

    if (k.indexOf('suppressedNotifications_') === 0) return true;

    if (k.indexOf('p4_profile_data_') === 0) return true;

    if (k.indexOf('p4_vault_') === 0) return true;

    if (k.indexOf('draft_') === 0) return true;

    if (k.indexOf('p4_pw_') === 0) return true;

    return false;

  }



  function snapshotPreserved(){

    var snap = [];

    try {

      for (var i = 0; i < localStorage.length; i++) {

        var k = localStorage.key(i);

        if (isPreserved(k)) {

          try { snap.push({ k: k, v: localStorage.getItem(k) }); } catch(e){}

        }

      }

    } catch(e){}

    return snap;

  }



  function restorePreserved(snap){

    for (var i = 0; i < snap.length; i++) {

      try { localStorage.setItem(snap[i].k, snap[i].v); } catch(e){}

    }

  }



  // ---- Guard every future localStorage.clear() call ----

  function wrapClear(){

    try {

      var orig = localStorage.clear;

      if (orig && !orig._p4IdProtect) {

        localStorage.clear = function(){

          var snap = snapshotPreserved();

          log('clear() intercepted — preserving ' + snap.length + ' P4 keys');

          orig.apply(localStorage, arguments);

          restorePreserved(snap);

          // Re-seed in case something else deleted the specific keys

          var ids = ensureKeys();

          seedApproval(ids);

        };

        localStorage.clear._p4IdProtect = true;

        log('localStorage.clear() wrapped');

      }

    } catch(e){ log('wrapClear failed: ' + e.message); }

  }



  // ---- Belt and suspenders: re-seed every second for the first 10s ----

  function periodicReSeed(){

    var n = 0;

    var iv = setInterval(function(){

      n++;

      var ids = ensureKeys();

      if (ids) {

        // Only re-seed approval if it was removed

        if (localStorage.getItem('p4_pw_approved') !== 'true') {

          seedApproval(ids);

        }

      }

      if (n > 10) clearInterval(iv);

    }, 1000);

  }



  // Run immediately (before v3's +300ms boot fires)

  var ids = ensureKeys();

  seedApproval(ids);

  wrapClear();

  periodicReSeed();

  log('armed — IDs stable, approval seeded, clear() protected');

})();

// ---- END extracted from index (24).html L66122-66282 (p4-id-protect-js) ----

// ---- BEGIN extracted from index (24).html L67592-67614 (p4-workoutdata-replace-js) ----
(function(){

  if (window.P4 && window.P4.replaceWorkoutData) return;

  window.P4 = window.P4 || {};

  window.P4.replaceWorkoutData = function(newData){

    try {

      var wd = null;

      try { wd = workoutData; } catch(e) { return; }

      if (!wd) return;

      var keys = Object.keys(wd);

      for (var i = 0; i < keys.length; i++) {

        try { delete wd[keys[i]]; } catch(e){}

      }

      if (newData && typeof newData === 'object') {

        var nk = Object.keys(newData);

        for (var j = 0; j < nk.length; j++) {

          wd[nk[j]] = newData[nk[j]];

        }

      }

    } catch(e){ try { console.warn('[P4] replaceWorkoutData failed', e); } catch(e2){} }

  };

})();

// ---- END extracted from index (24).html L67592-67614 (p4-workoutdata-replace-js) ----

// ---- BEGIN extracted from index (24).html L67615-67696 (p4-nav-final-js) ----
(function(){

  'use strict';

  if (window.__p4NavFinal) return;

  window.__p4NavFinal = true;

  if (!window.P4) window.P4 = {};



  function log(m){ try { console.log('[P4 Nav Final]', m); } catch(e){} }



  function buildTabBar(){

    var bar = document.getElementById('p4TabBar');

    if (bar) return bar;

    if (P4.Nav && typeof P4.Nav.build === 'function') {

      try { P4.Nav.build(); } catch(e){ log('build failed: ' + e.message); }

    }

    return document.getElementById('p4TabBar');

  }



  function forceTabBar(){

    var bar = buildTabBar();

    if (!bar) return false;



    bar.removeAttribute('hidden');

    bar.removeAttribute('inert');

    bar.classList.remove('p4-tabbar-hide');



    bar.style.setProperty('position', 'fixed', 'important');

    bar.style.setProperty('left', '50%', 'important');

    bar.style.setProperty('bottom', 'calc(12px + env(safe-area-inset-bottom, 0px))', 'important');

    bar.style.setProperty('transform', 'translateX(-50%)', 'important');

    bar.style.setProperty('display', 'flex', 'important');

    bar.style.setProperty('visibility', 'visible', 'important');

    bar.style.setProperty('opacity', '1', 'important');

    bar.style.setProperty('pointer-events', 'auto', 'important');

    bar.style.setProperty('z-index', '1600', 'important');

    bar.style.setProperty('width', 'min(430px, calc(100vw - 20px))', 'important');

    bar.style.setProperty('padding', '8px 10px', 'important');

    bar.style.setProperty('border-radius', '26px', 'important');

    bar.style.setProperty('background', 'var(--p4-glass, rgba(16,20,28,.72))', 'important');

    bar.style.setProperty('-webkit-backdrop-filter', 'blur(24px) saturate(170%)', 'important');

    bar.style.setProperty('backdrop-filter', 'blur(24px) saturate(170%)', 'important');

    bar.style.setProperty('border', '1px solid var(--p4-hairline-2, rgba(148,163,184,.26))', 'important');

    bar.style.setProperty('align-items', 'flex-end', 'important');

    bar.style.setProperty('gap', '2px', 'important');



    /* Ensure top navbar is NOT affected */

    var topNav = document.querySelector('nav.navbar');

    if (topNav) {

      topNav.style.setProperty('display', 'flex', 'important');

      topNav.style.setProperty('visibility', 'visible', 'important');

      topNav.style.setProperty('opacity', '1', 'important');

    }



    /* Push content + dock above the tab bar */

    var main = document.querySelector('.main-app') || document.querySelector('.container.main-app');

    if (main) main.style.setProperty('padding-bottom', '110px', 'important');

    var dock = document.getElementById('p4Dock');

    if (dock) dock.style.setProperty('bottom', '110px', 'important');



    log('tab bar forced visible');

    return true;

  }



  [150, 500, 1500, 3000, 6000].forEach(function(t){ setTimeout(forceTabBar, t); });



  document.addEventListener('visibilitychange', function(){

    if (!document.hidden) setTimeout(forceTabBar, 200);

  });



  if (typeof window.showSection === 'function' && !window.showSection._p4NF) {

    window.showSection._p4NF = true;

    var orig = window.showSection;

    window.showSection = function(){

      var r = orig.apply(this, arguments);

      setTimeout(forceTabBar, 150);

      return r;

    };

  }



  log('armed');

})();

// ---- END extracted from index (24).html L67615-67696 (p4-nav-final-js) ----

// ---- BEGIN extracted from index (24).html L67697-67732 (p4-health-fit-js) ----
(function(){

  'use strict';

  if (window.__p4HealthFit) return;

  window.__p4HealthFit = true;



  function applyCompact(){

    var backdrop = document.querySelector('.p4-wz-backdrop');

    if (!backdrop) return;

    var wz = backdrop.querySelector('.p4-wz');

    if (!wz) return;

    // Only tag health-check pages

    var hasHealth = wz.querySelector('.p4-hs-h1, .p4-hs-verdict');

    if (!hasHealth) {

      wz.classList.remove('p4-hs-compact');

      return;

    }

    wz.classList.add('p4-hs-compact');

    // Tag the joints grid for the 2-column layout

    wz.querySelectorAll('.p4-hs-jts').forEach(function(el){

      el.classList.add('p4-hs-jts-compact');

    });

  }



  var mo = new MutationObserver(function(){

    if (window.__p4HFitTimer) clearTimeout(window.__p4HFitTimer);

    window.__p4HFitTimer = setTimeout(applyCompact, 40);

  });

  try { mo.observe(document.body, { childList: true, subtree: true }); } catch(e){}



  setTimeout(applyCompact, 800);

  setTimeout(applyCompact, 2000);



  console.log('[P4 Health Fit] armed');

})();

// ---- END extracted from index (24).html L67697-67732 (p4-health-fit-js) ----

// ---- BEGIN extracted from index (24).html L67733-67761 (p4-final-cleanup-js) ----
(function(){

  'use strict';

  if (window.__p4FinalCleanup) return;

  window.__p4FinalCleanup = true;



  // P4 FINAL: one-time cleanup — remove diagnostic litter, verify payload presence.

  function log(m){ try { console.log('[P4 Final]', m); } catch(e){} }



  setTimeout(function(){

    var checks = {

      'Bodyweight lib': !!window.P4_BODYWEIGHT_LIBRARY,

      'Wizard guard'  : !!window.__p4WizardGuard,

      'Unlock-all'    : !!window.__p4UnlockAll,

      'ID protect'    : !!window.__p4IdProtect,

      'Reconcile'     : !!(window.P4 && P4.User && P4.User.reconcile),

      'Settings env'  : !!(window.P4 && P4.SettingsEnv),

      'Nav final'     : !!window.__p4NavFinal,

      'Tabbar CSS'    : !!document.getElementById('p4-tabbar-force-css'),

      'Health compact': !!document.getElementById('p4-health-fit-css'),

      'Health shell'  : !!document.getElementById('p4-health-shell-css')

    };

    log('=== PAYLOAD CHECK ===');

    Object.keys(checks).forEach(function(k){ log('  ' + (checks[k] ? '✓' : '✗') + ' ' + k); });

    var all = Object.keys(checks).every(function(k){ return checks[k]; });

    log(all ? '=== ALL SYSTEMS OK ===' : '=== SOME MISSING ===');

  }, 2000);

})();

// ---- END extracted from index (24).html L67733-67761 (p4-final-cleanup-js) ----

// ---- BEGIN extracted from index (24).html L67947-68040 (p4-speedup-js) ----
(function(){

  'use strict';

  if (window.__p4Speedup) return;

  window.__p4Speedup = true;



  function log(m){ try { console.log('[P4 Speedup]', m); } catch(e){} }



  // ---- Part A: memoize the expensive elite-date projection ----

  function hookEliteDate(){

    if (typeof window.calculateProjectedEliteDate !== 'function') { setTimeout(hookEliteDate, 300); return; }

    if (window.calculateProjectedEliteDate._p4Cached) return;

    var orig = window.calculateProjectedEliteDate;

    var cache = null;

    var cacheTime = 0;

    var TTL = 5000;

    window.calculateProjectedEliteDate = function(){

      var now = Date.now();

      if (cache && (now - cacheTime) < TTL) {

        return cache;

      }

      var r = orig.apply(this, arguments);

      cache = r;

      cacheTime = now;

      return r;

    };

    window.calculateProjectedEliteDate._p4Cached = true;

    log('elite-date projection memoized (5s TTL)');

  }

  hookEliteDate();



  // ---- Part B: defer heavy sanitize/fatigue/aggregate passes to idle time ----

  function deferHeavyWork(){

    var heavy = [

      ['sanitizeExerciseMu',    'exercise 1RM sanity pass'],

      ['rebuildFatigueFromHistory', 'muscle fatigue from history'],

      ['calculateMuscleLastTrained', 'last-trained timestamps'],

      ['backfillLastPerformed', 'lastPerformed backfill'],

      ['recomputeAggregates',   'rolling aggregates'],

      ['updateBodyWeightEstimate', 'bodyweight model']

    ];



    // Grab the currently-loaded state; these funcs read from global workoutData

    var ran = {};

    function runOne(name, label){

      if (ran[name]) return;

      ran[name] = true;

      try {

        var fn = window[name];

        if (typeof fn === 'function') {

          fn();

          log('ran: ' + label);

        }

      } catch(e){ log('failed: ' + name + ' — ' + e.message); }

    }



    // Use requestIdleCallback if available, else fall back to staggered setTimeout

    var idle = (typeof window.requestIdleCallback === 'function')

      ? window.requestIdleCallback

      : function(cb){ return setTimeout(function(){ cb({ timeRemaining: function(){ return 30; } }); }, 120); };



    var i = 0;

    function step(){

      if (i >= heavy.length) { log('heavy boot work complete'); return; }

      idle(function(deadline){

        while (i < heavy.length && (deadline.timeRemaining ? deadline.timeRemaining() > 8 : true)) {

          runOne(heavy[i][0], heavy[i][1]);

          i++;

        }

        setTimeout(step, 30);

      });

    }

    // Wait for the first paint before doing any of this

    setTimeout(step, 1200);

  }



  // Hook loadAllData so we defer AFTER it has already populated data

  function hookLoadAll(){

    if (typeof window.loadAllData !== 'function') { setTimeout(hookLoadAll, 300); return; }

    if (window.loadAllData._p4Deferred) return;

    var orig = window.loadAllData;

    window.loadAllData = async function(){

      var r = await orig.apply(this, arguments);

      setTimeout(deferHeavyWork, 400);

      return r;

    };

    window.loadAllData._p4Deferred = true;

    log('loadAllData hooked — heavy passes will defer to idle');

  }

  hookLoadAll();



  log('armed');

})();

// ---- END extracted from index (24).html L67947-68040 (p4-speedup-js) ----

// ---- BEGIN extracted from index (24).html L68041-68099 (p4-card-state-js) ----
(function(){

  'use strict';

  if (window.__p4CardState) return;

  window.__p4CardState = true;



  function getExerciseStatus(ex){

    if (!ex || !ex.id) return 'pending';

    if (ex.actual && !ex.skipped) return 'done';

    if (ex.skipped) return 'skipped';

    try {

      if (typeof currentWorkout !== 'undefined' && currentWorkout && currentWorkout.id) {

        if (localStorage.getItem('draft_' + currentWorkout.id + '_' + ex.id)) return 'started';

      }

    } catch(e){}

    var rec = (typeof workoutData !== 'undefined' && workoutData.exercises) ? workoutData.exercises[ex.id] : null;

    if (rec && rec.tested1RM && rec.tested1RM > 0) return 'ready';

    return 'pending';

  }



  function colorCards(){

    var list = document.getElementById('exerciseList');

    if (!list) return;

    if (typeof currentWorkout === 'undefined' || !currentWorkout || !currentWorkout.exercises) return;



    list.querySelectorAll('.p4-wc-wrapped, .exercise-item').forEach(function(card, idx){

      var exIdx = parseInt(card.getAttribute('data-index'), 10);

      if (isNaN(exIdx)) exIdx = parseInt((card.id || '').replace('exercise_', ''), 10);

      if (isNaN(exIdx)) exIdx = idx;

      var ex = currentWorkout.exercises[exIdx];

      if (!ex) return;

      var status = getExerciseStatus(ex);

      card.classList.remove('p4-state-ready', 'p4-state-started');

      if (status === 'ready') card.classList.add('p4-state-ready');

      if (status === 'started') card.classList.add('p4-state-started');

    });

  }



  var mo = new MutationObserver(function(){

    if (window.__p4CSTimer) clearTimeout(window.__p4CSTimer);

    window.__p4CSTimer = setTimeout(colorCards, 100);

  });

  try { mo.observe(document.body, { childList: true, subtree: true }); } catch(e){}



  setTimeout(colorCards, 800);

  setTimeout(colorCards, 2000);

  setTimeout(colorCards, 4000);



  if (typeof window.showSection === 'function' && !window.showSection._p4CS) {

    window.showSection._p4CS = true;

    var orig = window.showSection;

    window.showSection = function(id){

      var r = orig.apply(this, arguments);

      if (id === 'workout') setTimeout(colorCards, 200);

      return r;

    };

  }

})();

// ---- END extracted from index (24).html L68041-68099 (p4-card-state-js) ----

// ---- BEGIN extracted from index (24).html L68110-68158 (anonymous boot script) ----
(function(){

  function ready(fn){ if(document.readyState !== 'loading') fn(); else document.addEventListener('DOMContentLoaded', fn); }

  ready(function(){

    var pal = document.getElementById('p4CommandPalette');

    var input = document.getElementById('p4CpInput');

    var list = document.getElementById('p4CpList');

    if (!pal || !input || !list) return;



    var actions = [

      { id:'nav.dashboard',  label:'Go to Dashboard',  run:function(){ try{ showSection('dashboard'); }catch(e){} } },

      { id:'nav.workout',    label:'Start / Continue Workout', run:function(){ try{ showSection('workout'); }catch(e){} } },

      { id:'nav.history',    label:'View History',     run:function(){ try{ showSection('history'); }catch(e){} } },

      { id:'nav.progress',   label:'View Progress',    run:function(){ try{ showSection('progress'); }catch(e){} } },

      { id:'nav.settings',   label:'Open Settings',    run:function(){ try{ showSection('settings'); }catch(e){} } },

      { id:'profile.new',    label:'New Profile',      run:function(){ try{ P4.Picker.open(); }catch(e){} } },

      { id:'profile.switch', label:'Switch Profile',   run:function(){ try{ P4.Picker.open(); }catch(e){} } }

    ];



    var filtered = actions.slice();

    var cursor = 0;



    function render(){

      list.innerHTML = filtered.map(function(a, i){

        var sel = i === cursor;

        return '<div class="p4-cp-item" data-i="'+i+'" role="option" style="padding:14px 16px;border-radius:9px;cursor:pointer;color:#e5e7eb;font-size:15px;'+(sel?'background:rgba(78,155,255,0.20)':'')+'">'+a.label+'</div>';

      }).join('') || '<div style="padding:14px;color:#64748b;font-size:13px">No matches.</div>';

    }

    function open(){ pal.style.display='flex'; input.value=''; filtered=actions.slice(); cursor=0; render(); setTimeout(function(){ input.focus(); },30); }

    function close(){ pal.style.display='none'; }

    function run(i){ var a = filtered[i]; if (!a) return; close(); setTimeout(function(){ try { a.run(); } catch(e){} }, 40); }



    input.addEventListener('input', function(){ var q = input.value.toLowerCase().trim(); filtered = actions.filter(function(a){ return !q || a.label.toLowerCase().indexOf(q) !== -1; }); cursor=0; render(); });

    input.addEventListener('keydown', function(e){

      if (e.key === 'ArrowDown'){ e.preventDefault(); cursor = Math.min(cursor+1, filtered.length-1); render(); }

      else if (e.key === 'ArrowUp'){ e.preventDefault(); cursor = Math.max(cursor-1, 0); render(); }

      else if (e.key === 'Enter'){ e.preventDefault(); run(cursor); }

      else if (e.key === 'Escape'){ e.preventDefault(); close(); }

    });

    list.addEventListener('click', function(e){ var it = e.target.closest('.p4-cp-item'); if (!it) return; run(parseInt(it.getAttribute('data-i'),10)); });

    pal.addEventListener('click', function(e){ if (e.target === pal) close(); });

    document.addEventListener('keydown', function(e){

      if ((e.ctrlKey || e.metaKey) && (e.key === 'k' || e.key === 'K')) {

        e.preventDefault(); pal.style.display === 'flex' ? close() : open();

      }

    });

  });

})();

// ---- END extracted from index (24).html L68110-68158 (anonymous boot script) ----

// ---- BEGIN extracted from index (24).html L68159-68206 (p4DataSchemaJs (data schema + P4Backup module)) ----
(function(){

  'use strict';

  window.P4_DATA_SCHEMA = 2;

  function allLocal(){

    var out = {};

    try { for (var i = 0; i < localStorage.length; i++) { var k = localStorage.key(i); out[k] = localStorage.getItem(k); } } catch(e){}

    return out;

  }

  function exportBundle(){

    return { _schema: window.P4_DATA_SCHEMA, _app: 'Pro', _appId: 'com.peakform.fitness', _exportedAt: new Date().toISOString(), localStorage: allLocal() };

  }

  function migrate(bundle){ if (!bundle || typeof bundle !== 'object') throw new Error('invalid bundle'); return bundle; }

  function importBundle(bundle, opts){

    opts = opts || {};

    var b = migrate(bundle);

    var ls = b.localStorage || {};

    if (opts.wipe) { try { localStorage.clear(); } catch(e){} }

    for (var k in ls) if (Object.prototype.hasOwnProperty.call(ls, k)) { try { localStorage.setItem(k, ls[k]); } catch(e){} }

    return true;

  }

  function saveToDisk(){

    try {

      var blob = new Blob([JSON.stringify(exportBundle(), null, 2)], { type: 'application/json' });

      var a = document.createElement('a');

      a.href = URL.createObjectURL(blob);

      a.download = 'pro-backup-' + Date.now() + '.json';

      a.click();

      setTimeout(function(){ URL.revokeObjectURL(a.href); }, 2000);

      return Promise.resolve({ ok: true });

    } catch (e) { return Promise.resolve({ ok: false, error: String(e) }); }

  }

  function loadFromDisk(){

    return new Promise(function(resolve){

      var inp = document.createElement('input');

      inp.type = 'file'; inp.accept = '.json,application/json';

      inp.onchange = function(){

        var f = inp.files[0]; if (!f) return resolve({ ok: false });

        var r = new FileReader();

        r.onload = function(){ try { importBundle(JSON.parse(r.result), { wipe: false }); resolve({ ok: true }); } catch(e){ resolve({ ok: false, error: String(e) }); } };

        r.readAsText(f);

      };

      inp.click();

    });

  }

  window.P4Backup = { export: saveToDisk, import: loadFromDisk, bundle: exportBundle, migrate: migrate };

})();

// ---- END extracted from index (24).html L68159-68206 (p4DataSchemaJs (data schema + P4Backup module)) ----

