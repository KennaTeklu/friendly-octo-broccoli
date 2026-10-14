package com.peakform.fitness.coach.authored

import kotlinx.serialization.Serializable

@Serializable
data class AuthoredRow(
    val rowId: String,
    val sourceFile: String,
    val sourceSection: String,
    val exerciseId: String,
    val intent: String,
    val phase: String,
    val trainingAge: String,
    val sex: String,
    val bodyGoal: String,
    val sets: Int,
    val reps: String,
    val load: Double? = null,
    val pct1RM: Double? = null,
    val bandColour: String? = null,
    val tier: Int? = null,
    val duration: Double? = null,
    val distance: Double? = null,
    val pace: String? = null,
    val restSeconds: Int,
    val components: Map<String, Int>,
)

data class CoachProvenance(
    val sourceFile: String,
    val sourceSection: String,
    val coachVersion: Int,
    val authorNote: String,
)

data class ExerciseEnvelope(
    val maxSets: Int,
    val minSets: Int,
    val maxLoad: Double,
    val maxDuration: Double,
    val maxDistance: Double,
    val maxPct1RM: Double,
)

data class BodyProfile(
    val trainingAge: String,
    val sex: String,
    val bodyGoal: String,
)

data class CoachRequirements(
    val needs1RM: Boolean = false,
    val needsBand: Boolean = false,
    val needsBodyweight: Boolean = false,
    val needsDuration: Boolean = false,
    val needsDistance: Boolean = false,
    val needsPace: Boolean = false,
    val needsWeight: Boolean = false,
)

data class Prescription(
    val sets: Int = 0,
    val reps: String = "",
    val load: Double? = null,
    val pct1RM: Double? = null,
    val bandColour: String? = null,
    val tier: Int? = null,
    val duration: Double? = null,
    val distance: Double? = null,
    val pace: String? = null,
    val restSeconds: Int = 0,
    val components: Map<String, Int> = emptyMap(),
    val note: String = "",
    val sourceRowId: String? = null,
    val deferrals: List<String> = emptyList(),
)

object AuthoredRows {
    val backSquatRows = listOf(
        AuthoredRow("bs_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "back_squat", "STRONGER", "BASE", "novice", "*", "*", 3, "5", pct1RM = 0.65, restSeconds = 300, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "POWER" to 1)),
        AuthoredRow("bs_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "back_squat", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "5", pct1RM = 0.80, restSeconds = 240, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("bs_adv_stronger_peak", "prilepin-1974/chart.md", "90+ row", "back_squat", "STRONGER", "PEAK", "advanced", "*", "*", 5, "3", pct1RM = 0.90, restSeconds = 300, components = mapOf("STRENGTH" to 5, "POWER" to 2)),
        AuthoredRow("bs_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "back_squat", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.75, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("bs_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "back_squat", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val pullUpRows = listOf(
        AuthoredRow("pu_t5_stronger_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5: Pull-up", "pull_up", "STRONGER", "BUILD", "*", "*", "*", 3, "3-8", tier = 5, restSeconds = 120, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("pu_t5_hypertrophy_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5: Pull-up", "pull_up", "BIGGER", "BUILD", "*", "*", "*", 3, "6-10", tier = 5, restSeconds = 90, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("pu_t3_stronger_base", "sommer-2006/gymnastic-body-tier-table.md", "Tier 3: Jump-assisted", "pull_up", "STRONGER", "BASE", "novice", "*", "*", 3, "3-5", tier = 3, restSeconds = 90, components = mapOf("STRENGTH" to 4, "SKILL" to 2)),
        AuthoredRow("pu_t5_maintain_maintain", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5: Pull-up", "pull_up", "MAINTAIN", "MAINTAIN", "*", "*", "*", 2, "5-8", tier = 5, restSeconds = 90, components = mapOf("STRENGTH" to 3, "LONGEVITY" to 1)),
    )
    val bandedLateralRaiseRows = listOf(
        AuthoredRow("blr_yellow_hypertrophy_base", "rogue-band-tension/chart.md", "Yellow row", "banded_lateral_raise", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "yellow", restSeconds = 45, components = mapOf("HYPERTROPHY" to 5, "ENDURANCE" to 1)),
        AuthoredRow("blr_red_hypertrophy_build", "rogue-band-tension/chart.md", "Red row", "banded_lateral_raise", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 5, "ENDURANCE" to 1)),
        AuthoredRow("blr_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "banded_lateral_raise", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "8-12", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("blr_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "banded_lateral_raise", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "12-15", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )
    val plankRows = listOf(
        AuthoredRow("pl_novice_endure_base_m", "mcgill-2010/plank-standards.md", "Front plank, novice, male", "plank", "ENDURE", "BASE", "novice", "male", "*", 3, "hold", duration = 60.0, restSeconds = 45, components = mapOf("ENDURANCE" to 5, "CORE_STABILITY" to 4)),
        AuthoredRow("pl_novice_endure_base_f", "mcgill-2010/plank-standards.md", "Front plank, novice, female", "plank", "ENDURE", "BASE", "novice", "female", "*", 3, "hold", duration = 50.0, restSeconds = 45, components = mapOf("ENDURANCE" to 5, "CORE_STABILITY" to 4)),
        AuthoredRow("pl_int_endure_build_m", "mcgill-2010/plank-standards.md", "Front plank, intermediate, male", "plank", "ENDURE", "BUILD", "intermediate", "male", "*", 3, "hold", duration = 120.0, restSeconds = 45, components = mapOf("ENDURANCE" to 5, "CORE_STABILITY" to 3)),
        AuthoredRow("pl_int_maintain_maintain", "mcgill-2010/plank-standards.md", "Maintenance row", "plank", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 2, "hold", duration = 30.0, restSeconds = 30, components = mapOf("ENDURANCE" to 3, "LONGEVITY" to 2)),
    )
    val runningRows = listOf(
        AuthoredRow("run_e_endure_base", "daniels-2013/vdot-zones.md", "Zone E", "running", "ENDURE", "BASE", "*", "*", "*", 1, "continuous", distance = 3.0, pace = "59-74% VDOT", duration = 1800.0, restSeconds = 0, components = mapOf("ENDURANCE" to 5, "LONGEVITY" to 3)),
        AuthoredRow("run_t_endure_build", "daniels-2013/vdot-zones.md", "Zone T", "running", "ENDURE", "BUILD", "*", "*", "*", 1, "continuous", distance = 5.0, pace = "88-92% VDOT", duration = 2400.0, restSeconds = 0, components = mapOf("ENDURANCE" to 5, "LONGEVITY" to 2)),
        AuthoredRow("run_e_maintain_maintain", "daniels-2013/vdot-zones.md", "Zone E", "running", "MAINTAIN", "MAINTAIN", "*", "*", "*", 1, "continuous", distance = 2.0, pace = "59-74% VDOT", duration = 1200.0, restSeconds = 0, components = mapOf("ENDURANCE" to 3, "LONGEVITY" to 2)),
    )

    // ── v35: 10 new coaches ──

    val deadliftRows = listOf(
        AuthoredRow("dl_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "deadlift", "STRONGER", "BASE", "novice", "*", "*", 1, "5", pct1RM = 0.65, restSeconds = 300, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "POWER" to 1)),
        AuthoredRow("dl_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "deadlift", "STRONGER", "BUILD", "intermediate", "*", "*", 3, "5", pct1RM = 0.80, restSeconds = 300, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("dl_adv_stronger_peak", "prilepin-1974/chart.md", "90+ row", "deadlift", "STRONGER", "PEAK", "advanced", "*", "*", 3, "3", pct1RM = 0.90, restSeconds = 300, components = mapOf("STRENGTH" to 5, "POWER" to 2)),
        AuthoredRow("dl_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "deadlift", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "8-12", pct1RM = 0.70, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("dl_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "deadlift", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "8", pct1RM = 0.70, restSeconds = 120, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val benchPressRows = listOf(
        AuthoredRow("bp_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "bench_press", "STRONGER", "BASE", "novice", "*", "*", 3, "5", pct1RM = 0.65, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "POWER" to 1)),
        AuthoredRow("bp_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "bench_press", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "5", pct1RM = 0.80, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("bp_adv_stronger_peak", "prilepin-1974/chart.md", "90+ row", "bench_press", "STRONGER", "PEAK", "advanced", "*", "*", 5, "3", pct1RM = 0.90, restSeconds = 240, components = mapOf("STRENGTH" to 5, "POWER" to 2)),
        AuthoredRow("bp_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "bench_press", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.75, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("bp_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "bench_press", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val overheadPressRows = listOf(
        AuthoredRow("ohp_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "overhead_press", "STRONGER", "BASE", "novice", "*", "*", 3, "5", pct1RM = 0.65, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "POWER" to 1)),
        AuthoredRow("ohp_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "overhead_press", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "5", pct1RM = 0.80, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("ohp_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "overhead_press", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.75, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("ohp_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "overhead_press", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val barbellRowRows = listOf(
        AuthoredRow("br_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "barbell_row", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "4", pct1RM = 0.80, restSeconds = 120, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("br_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "barbell_row", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.70, restSeconds = 90, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("br_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "barbell_row", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("HYPERTROPHY" to 3, "STRENGTH" to 2, "LONGEVITY" to 1)),
    )
    val frontSquatRows = listOf(
        AuthoredRow("fs_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "front_squat", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "4", pct1RM = 0.80, restSeconds = 240, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("fs_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "front_squat", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.70, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("fs_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "front_squat", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val romanianDeadliftRows = listOf(
        AuthoredRow("rdl_int_stronger_build", "rippetoe-2011/assistance.md", "RDL row", "romanian_deadlift", "STRONGER", "BUILD", "intermediate", "*", "*", 3, "8-12", pct1RM = 0.65, restSeconds = 120, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("rdl_int_hypertrophy_build", "rippetoe-2011/assistance.md", "RDL row", "romanian_deadlift", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "8-12", pct1RM = 0.60, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("rdl_int_maintain_maintain", "rippetoe-2011/assistance.md", "RDL row", "romanian_deadlift", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.60, restSeconds = 90, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 2)),
    )
    val chinUpRows = listOf(
        AuthoredRow("cu_t4_stronger_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 4: Chin-up", "chin_up", "STRONGER", "BUILD", "*", "*", "*", 3, "3-8", tier = 4, restSeconds = 120, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("cu_t4_hypertrophy_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 4: Chin-up", "chin_up", "BIGGER", "BUILD", "*", "*", "*", 3, "6-10", tier = 4, restSeconds = 90, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("cu_t4_maintain_maintain", "sommer-2006/gymnastic-body-tier-table.md", "Tier 4: Chin-up", "chin_up", "MAINTAIN", "MAINTAIN", "*", "*", "*", 2, "5-8", tier = 4, restSeconds = 90, components = mapOf("STRENGTH" to 3, "LONGEVITY" to 1)),
    )
    val dipRows = listOf(
        AuthoredRow("dp_stronger_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5 (adapted)", "dip", "STRONGER", "BUILD", "*", "*", "*", 3, "3-8", tier = 5, restSeconds = 120, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("dp_hypertrophy_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5 (adapted)", "dip", "BIGGER", "BUILD", "*", "*", "*", 3, "6-10", tier = 5, restSeconds = 90, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("dp_maintain_maintain", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5 (adapted)", "dip", "MAINTAIN", "MAINTAIN", "*", "*", "*", 2, "5-8", tier = 5, restSeconds = 90, components = mapOf("STRENGTH" to 3, "LONGEVITY" to 1)),
    )
    val pushUpRows = listOf(
        AuthoredRow("pu2_t2_stronger_base", "sommer-2006/gymnastic-body-tier-table.md", "Tier 2 (adapted)", "push_up", "STRONGER", "BASE", "novice", "*", "*", 3, "5-10", tier = 2, restSeconds = 90, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 2, "ENDURANCE" to 1)),
        AuthoredRow("pu2_t5_stronger_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5 (adapted)", "push_up", "STRONGER", "BUILD", "intermediate", "*", "*", 3, "6-12", tier = 5, restSeconds = 90, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 2)),
        AuthoredRow("pu2_t5_maintain_maintain", "sommer-2006/gymnastic-body-tier-table.md", "Tier 5 (adapted)", "push_up", "MAINTAIN", "MAINTAIN", "*", "*", "*", 3, "10-15", tier = 5, restSeconds = 60, components = mapOf("ENDURANCE" to 3, "LONGEVITY" to 2)),
    )
    val facePullRows = listOf(
        AuthoredRow("fp_red_hypertrophy_base", "rogue-band-tension/chart.md", "Red row", "face_pull", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 4, "ENDURANCE" to 1)),
        AuthoredRow("fp_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "face_pull", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "12-15", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 3)),
        AuthoredRow("fp_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "face_pull", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "15-20", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )

    // ── v35b: 16 new coaches (6 Prilepin + 6 Rogue + 3 Sommer + 1 McGill side plank) ──

    // PART 1 — Compound barbell from Prilepin (6 coaches)

    val sumoDeadliftRows = listOf(
        AuthoredRow("sd_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "sumo_deadlift", "STRONGER", "BASE", "novice", "*", "*", 1, "5", pct1RM = 0.65, restSeconds = 300, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "POWER" to 1)),
        AuthoredRow("sd_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "sumo_deadlift", "STRONGER", "BUILD", "intermediate", "*", "*", 3, "4", pct1RM = 0.80, restSeconds = 300, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("sd_adv_stronger_peak", "prilepin-1974/chart.md", "90+ row", "sumo_deadlift", "STRONGER", "PEAK", "advanced", "*", "*", 3, "2", pct1RM = 0.90, restSeconds = 300, components = mapOf("STRENGTH" to 5, "POWER" to 2)),
        AuthoredRow("sd_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "sumo_deadlift", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "6", pct1RM = 0.75, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("sd_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "sumo_deadlift", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "6", pct1RM = 0.70, restSeconds = 120, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val inclineBenchPressRows = listOf(
        AuthoredRow("ibp_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "incline_bench_press", "STRONGER", "BASE", "novice", "*", "*", 3, "5", pct1RM = 0.65, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "POWER" to 1)),
        AuthoredRow("ibp_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "incline_bench_press", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "4", pct1RM = 0.80, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("ibp_adv_stronger_peak", "prilepin-1974/chart.md", "90+ row", "incline_bench_press", "STRONGER", "PEAK", "advanced", "*", "*", 5, "2", pct1RM = 0.90, restSeconds = 240, components = mapOf("STRENGTH" to 5, "POWER" to 2)),
        AuthoredRow("ibp_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "incline_bench_press", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.75, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("ibp_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "incline_bench_press", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val closeGripBenchPressRows = listOf(
        AuthoredRow("cgbp_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "close_grip_bench_press", "STRONGER", "BASE", "novice", "*", "*", 3, "5", pct1RM = 0.65, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "POWER" to 1)),
        AuthoredRow("cgbp_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "close_grip_bench_press", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "4", pct1RM = 0.80, restSeconds = 180, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2)),
        AuthoredRow("cgbp_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "close_grip_bench_press", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.75, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("cgbp_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "close_grip_bench_press", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 2, "LONGEVITY" to 1)),
    )
    val pendlayRowRows = listOf(
        AuthoredRow("pdr_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "pendlay_row", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "4", pct1RM = 0.80, restSeconds = 120, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("pdr_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "pendlay_row", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.70, restSeconds = 90, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("pdr_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "pendlay_row", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("HYPERTROPHY" to 3, "STRENGTH" to 2, "LONGEVITY" to 1)),
    )
    val pushPressRows = listOf(
        AuthoredRow("pp_novice_stronger_base", "rippetoe-2011/novice-lp.md", "Novice table", "push_press", "STRONGER", "BASE", "novice", "*", "*", 3, "5", pct1RM = 0.65, restSeconds = 180, components = mapOf("STRENGTH" to 5, "POWER" to 2, "HYPERTROPHY" to 1)),
        AuthoredRow("pp_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "push_press", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "4", pct1RM = 0.80, restSeconds = 180, components = mapOf("STRENGTH" to 5, "POWER" to 2)),
        AuthoredRow("pp_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "push_press", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "8-12", pct1RM = 0.75, restSeconds = 120, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("pp_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "push_press", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "10", pct1RM = 0.70, restSeconds = 90, components = mapOf("STRENGTH" to 3, "POWER" to 2, "LONGEVITY" to 1)),
    )
    val powerCleanRows = listOf(
        AuthoredRow("pc_int_stronger_build", "prilepin-1974/chart.md", "80-89% row", "power_clean", "STRONGER", "BUILD", "intermediate", "*", "*", 5, "3", pct1RM = 0.80, restSeconds = 180, components = mapOf("POWER" to 5, "STRENGTH" to 3)),
        AuthoredRow("pc_int_hypertrophy_build", "prilepin-1974/chart.md", "70-79% row", "power_clean", "BIGGER", "BUILD", "intermediate", "*", "*", 4, "5", pct1RM = 0.70, restSeconds = 180, components = mapOf("POWER" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("pc_int_maintain_maintain", "prilepin-1974/chart.md", "70-79% row", "power_clean", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "5", pct1RM = 0.70, restSeconds = 180, components = mapOf("POWER" to 3, "LONGEVITY" to 2)),
    )

    // PART 2 — Banded from Rogue (6 coaches; red hypertrophy, green stronger, blue maintain; ladder mirror of BandedLateralRaiseAuthoredCoach)

    val bandedFacePullRows = listOf(
        AuthoredRow("bfp_red_hypertrophy_base", "rogue-band-tension/chart.md", "Red row", "banded_face_pull", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 4, "ENDURANCE" to 1)),
        AuthoredRow("bfp_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "banded_face_pull", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "12-15", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 3, "HYPERTROPHY" to 3)),
        AuthoredRow("bfp_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "banded_face_pull", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "15-20", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )
    val bandedPullApartRows = listOf(
        AuthoredRow("bpa_yellow_hypertrophy_base", "rogue-band-tension/chart.md", "Yellow row", "banded_pull_apart", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "yellow", restSeconds = 45, components = mapOf("HYPERTROPHY" to 4, "ENDURANCE" to 1)),
        AuthoredRow("bpa_red_hypertrophy_build", "rogue-band-tension/chart.md", "Red row", "banded_pull_apart", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 5, "ENDURANCE" to 1)),
        AuthoredRow("bpa_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "banded_pull_apart", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "8-12", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("bpa_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "banded_pull_apart", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "12-15", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )
    val bandedSquatRows = listOf(
        AuthoredRow("bsq_yellow_hypertrophy_base", "rogue-band-tension/chart.md", "Yellow row", "banded_squat", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "yellow", restSeconds = 45, components = mapOf("HYPERTROPHY" to 4, "ENDURANCE" to 1)),
        AuthoredRow("bsq_red_hypertrophy_build", "rogue-band-tension/chart.md", "Red row", "banded_squat", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 5, "ENDURANCE" to 1)),
        AuthoredRow("bsq_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "banded_squat", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "8-12", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("bsq_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "banded_squat", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "12-15", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )
    val bandedRowRows = listOf(
        AuthoredRow("br_yellow_hypertrophy_base", "rogue-band-tension/chart.md", "Yellow row", "banded_row", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "yellow", restSeconds = 45, components = mapOf("HYPERTROPHY" to 4, "ENDURANCE" to 1)),
        AuthoredRow("br_red_hypertrophy_build", "rogue-band-tension/chart.md", "Red row", "banded_row", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 5, "ENDURANCE" to 1)),
        AuthoredRow("br_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "banded_row", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "8-12", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("br_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "banded_row", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "12-15", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )
    val bandedGluteBridgeRows = listOf(
        AuthoredRow("bgb_yellow_hypertrophy_base", "rogue-band-tension/chart.md", "Yellow row", "banded_glute_bridge", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "yellow", restSeconds = 45, components = mapOf("HYPERTROPHY" to 4, "ENDURANCE" to 1)),
        AuthoredRow("bgb_red_hypertrophy_build", "rogue-band-tension/chart.md", "Red row", "banded_glute_bridge", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 5, "ENDURANCE" to 1)),
        AuthoredRow("bgb_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "banded_glute_bridge", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "8-12", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("bgb_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "banded_glute_bridge", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "12-15", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )
    val bandedChestPressRows = listOf(
        AuthoredRow("bcp_yellow_hypertrophy_base", "rogue-band-tension/chart.md", "Yellow row", "banded_chest_press", "BIGGER", "BASE", "novice", "*", "*", 3, "15-20", bandColour = "yellow", restSeconds = 45, components = mapOf("HYPERTROPHY" to 4, "ENDURANCE" to 1)),
        AuthoredRow("bcp_red_hypertrophy_build", "rogue-band-tension/chart.md", "Red row", "banded_chest_press", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "15-20", bandColour = "red", restSeconds = 45, components = mapOf("HYPERTROPHY" to 5, "ENDURANCE" to 1)),
        AuthoredRow("bcp_green_stronger_build", "rogue-band-tension/chart.md", "Green row", "banded_chest_press", "STRONGER", "BUILD", "intermediate", "*", "*", 4, "8-12", bandColour = "green", restSeconds = 60, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 3)),
        AuthoredRow("bcp_blue_maintain_maintain", "rogue-band-tension/chart.md", "Blue row", "banded_chest_press", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 3, "12-15", bandColour = "blue", restSeconds = 45, components = mapOf("HYPERTROPHY" to 3, "LONGEVITY" to 1)),
    )

    // PART 3 — Bodyweight from Sommer (3 coaches)

    val invertedRowRows = listOf(
        AuthoredRow("ir_t1_stronger_base", "sommer-2006/gymnastic-body-tier-table.md", "Tier 1: Band-assisted", "inverted_row", "STRONGER", "BASE", "novice", "*", "*", 3, "5-8", tier = 1, restSeconds = 90, components = mapOf("STRENGTH" to 4, "HYPERTROPHY" to 2, "ENDURANCE" to 1)),
        AuthoredRow("ir_t2_hypertrophy_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 2: Negative only", "inverted_row", "BIGGER", "BUILD", "intermediate", "*", "*", 3, "6-10", tier = 2, restSeconds = 90, components = mapOf("HYPERTROPHY" to 5, "STRENGTH" to 2)),
        AuthoredRow("ir_t3_maintain_maintain", "sommer-2006/gymnastic-body-tier-table.md", "Tier 3: Jump-assisted", "inverted_row", "MAINTAIN", "MAINTAIN", "*", "*", "*", 3, "8-12", tier = 3, restSeconds = 90, components = mapOf("HYPERTROPHY" to 3, "ENDURANCE" to 2, "LONGEVITY" to 1)),
    )
    val archerPullUpRows = listOf(
        AuthoredRow("apu_t8_stronger_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 8: Archer pull-up", "archer_pull_up", "STRONGER", "BUILD", "*", "*", "*", 3, "2-4", tier = 8, restSeconds = 150, components = mapOf("STRENGTH" to 5, "POWER" to 2)),
        AuthoredRow("apu_t8_maintain_maintain", "sommer-2006/gymnastic-body-tier-table.md", "Tier 8: Archer pull-up", "archer_pull_up", "MAINTAIN", "MAINTAIN", "*", "*", "*", 2, "2-3", tier = 8, restSeconds = 150, components = mapOf("STRENGTH" to 3, "LONGEVITY" to 1)),
    )
    val lSitPullUpRows = listOf(
        AuthoredRow("lspu_t7_stronger_build", "sommer-2006/gymnastic-body-tier-table.md", "Tier 7: L-sit pull-up", "l_sit_pull_up", "STRONGER", "BUILD", "*", "*", "*", 3, "3-5", tier = 7, restSeconds = 120, components = mapOf("STRENGTH" to 5, "HYPERTROPHY" to 2, "CORE_STABILITY" to 2)),
        AuthoredRow("lspu_t7_maintain_maintain", "sommer-2006/gymnastic-body-tier-table.md", "Tier 7: L-sit pull-up", "l_sit_pull_up", "MAINTAIN", "MAINTAIN", "*", "*", "*", 2, "3-4", tier = 7, restSeconds = 120, components = mapOf("STRENGTH" to 3, "CORE_STABILITY" to 2, "LONGEVITY" to 1)),
    )

    // PART 4 — Time-based from McGill SIDE PLANK table (1 coach)
    // Side plank standards differ from front plank: intermediate male 90s, not 120s.

    val sidePlankRows = listOf(
        AuthoredRow("sp_novice_endure_base_m", "mcgill-2010/plank-standards.md", "Side plank, novice, male", "side_plank", "ENDURE", "BASE", "novice", "male", "*", 3, "hold", duration = 45.0, restSeconds = 45, components = mapOf("ENDURANCE" to 5, "CORE_STABILITY" to 4)),
        AuthoredRow("sp_novice_endure_base_f", "mcgill-2010/plank-standards.md", "Side plank, novice, female", "side_plank", "ENDURE", "BASE", "novice", "female", "*", 3, "hold", duration = 35.0, restSeconds = 45, components = mapOf("ENDURANCE" to 5, "CORE_STABILITY" to 4)),
        AuthoredRow("sp_int_endure_build_m", "mcgill-2010/plank-standards.md", "Side plank, intermediate, male", "side_plank", "ENDURE", "BUILD", "intermediate", "male", "*", 3, "hold", duration = 90.0, restSeconds = 45, components = mapOf("ENDURANCE" to 5, "CORE_STABILITY" to 3)),
        AuthoredRow("sp_int_maintain_maintain", "mcgill-2010/plank-standards.md", "Maintenance row", "side_plank", "MAINTAIN", "MAINTAIN", "intermediate", "*", "*", 2, "hold", duration = 30.0, restSeconds = 30, components = mapOf("ENDURANCE" to 3, "LONGEVITY" to 2)),
    )

    fun rowsForCoach(coachId: String): List<AuthoredRow> = when (coachId) {
        "back_squat" -> backSquatRows
        "pull_up" -> pullUpRows
        "banded_lateral_raise" -> bandedLateralRaiseRows
        "plank" -> plankRows
        "running" -> runningRows
        "deadlift" -> deadliftRows
        "bench_press" -> benchPressRows
        "overhead_press" -> overheadPressRows
        "barbell_row" -> barbellRowRows
        "front_squat" -> frontSquatRows
        "romanian_deadlift" -> romanianDeadliftRows
        "chin_up" -> chinUpRows
        "dip" -> dipRows
        "push_up" -> pushUpRows
        "face_pull" -> facePullRows
        // v35b: 16 new coaches
        "sumo_deadlift" -> sumoDeadliftRows
        "incline_bench_press" -> inclineBenchPressRows
        "close_grip_bench_press" -> closeGripBenchPressRows
        "pendlay_row" -> pendlayRowRows
        "push_press" -> pushPressRows
        "power_clean" -> powerCleanRows
        "banded_face_pull" -> bandedFacePullRows
        "banded_pull_apart" -> bandedPullApartRows
        "banded_squat" -> bandedSquatRows
        "banded_row" -> bandedRowRows
        "banded_glute_bridge" -> bandedGluteBridgeRows
        "banded_chest_press" -> bandedChestPressRows
        "inverted_row" -> invertedRowRows
        "archer_pull_up" -> archerPullUpRows
        "l_sit_pull_up" -> lSitPullUpRows
        "side_plank" -> sidePlankRows
        else -> emptyList()
    }

    fun allRows(): List<AuthoredRow> = backSquatRows + pullUpRows + bandedLateralRaiseRows + plankRows + runningRows +
        deadliftRows + benchPressRows + overheadPressRows + barbellRowRows + frontSquatRows + romanianDeadliftRows +
        chinUpRows + dipRows + pushUpRows + facePullRows +
        // v35b: 16 new coaches
        sumoDeadliftRows + inclineBenchPressRows + closeGripBenchPressRows + pendlayRowRows + pushPressRows + powerCleanRows +
        bandedFacePullRows + bandedPullApartRows + bandedSquatRows + bandedRowRows + bandedGluteBridgeRows + bandedChestPressRows +
        invertedRowRows + archerPullUpRows + lSitPullUpRows + sidePlankRows
}
