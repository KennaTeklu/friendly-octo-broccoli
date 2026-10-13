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

    fun rowsForCoach(coachId: String): List<AuthoredRow> = when (coachId) {
        "back_squat" -> backSquatRows
        "pull_up" -> pullUpRows
        "banded_lateral_raise" -> bandedLateralRaiseRows
        "plank" -> plankRows
        "running" -> runningRows
        else -> emptyList()
    }

    fun allRows(): List<AuthoredRow> = backSquatRows + pullUpRows + bandedLateralRaiseRows + plankRows + runningRows
}
