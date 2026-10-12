package com.peakform.fitness.coach.engine

import com.peakform.fitness.core.state.Intent
import com.peakform.fitness.core.state.PrescriptionWithProvenance
import com.peakform.fitness.core.state.ArcState

/**
 * BATCH-31 Item 3: The twelve engines.
 * Reference: architecture/05-stimulus-field.md, "Engine selection".
 *
 * Each engine takes (intent, exerciseFamily, muscle, userLevel, arc, envelope, dossierView).
 * Returns a PrescriptionFragment — structured, deterministic, no side effects.
 */
data class PrescriptionFragment(
    val sets: Int,
    val reps: String,
    val weight: Double?,
    val pct1RM: Double?,
    val restSeconds: Int,
    val tempo: String,
    val note: String,
    val engineId: String,
    val ruleId: String,
)

data class EngineInput(
    val intent: Intent,
    val exerciseFamily: String,  // "compound_barbell", "compound_dumbbell", "bodyweight", "band", "time", "isolation", "cardio"
    val muscleType: String,
    val userLevel: String,       // "beginner", "intermediate", "advanced"
    val arc: ArcState,
    val user1RM: Double?,
    val userBodyweight: Double?,
    val phaseMultiplier: Double,
    val envelope: Envelope,
    val lastWeight: Double?,
    val lastReps: Int?,
)

data class Envelope(
    val maxSets: Int = 7,
    val minSets: Int = 2,
    val maxWeight: Double = 1000.0,
    val maxDuration: Double = 300.0,
    val maxRPE: Double = 10.0,
)

interface Engine {
    val id: String
    fun prescribe(input: EngineInput): PrescriptionFragment
}

object EngineRegistry {
    private val table: Map<Triple<String, String, String>, Engine> = mapOf(
        // (intent, family, muscleType) → engine
        Triple("STRONGER", "compound_barbell", "compound") to StrengthCompoundEngine,
        Triple("BIGGER", "compound_barbell", "compound") to HypertrophyCompoundEngine,
        Triple("BIGGER", "isolation", "isolation") to HypertrophyIsolationEngine,
        Triple("STRONGER", "band", "band") to StrengthBandEngine,
        Triple("BIGGER", "band", "band") to HypertrophyBandEngine,
        Triple("ENDURE", "time", "isometric") to EnduranceIsometricEngine,
        Triple("ENDURE", "cardio", "dynamic") to EnduranceDynamicEngine,
        Triple("STRONGER", "bodyweight", "bodyweight") to StrengthBodyweightEngine,
        Triple("MOBILE", "mobility", "mobility") to MobilityEngine,
        Triple("POWER", "compound_barbell", "compound") to PowerEngine,
        Triple("MAINTAIN", "*", "*") to MaintenanceEngine,
        Triple("LONGEVITY", "*", "*") to MinimalDoseEngine,
    )

    fun select(intent: Intent, family: String, muscleType: String): Engine {
        val key = Triple(intent.name, family, muscleType)
        table[key]?.let { return it }
        // Fallback: match on intent only
        table.entries.firstOrNull { it.key.first == intent.name && it.key.second == "*" }?.let { return it.value }
        return MaintenanceEngine
    }
}

// ---- The 12 engines ----

object StrengthCompoundEngine : Engine {
    override val id = "strength_compound"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        val orm = input.user1RM ?: return PrescriptionFragment(5, "5", null, null, 240, "31X0", "Test 1RM first.", id, "sc_1")
        val pct = if (input.userLevel == "beginner") 0.65 else 0.80
        val w = kotlin.math.max(45.0, orm * pct * input.phaseMultiplier).coerceAtMost(input.envelope.maxWeight)
        return PrescriptionFragment(5, "5", w, pct, 240, "31X0", "Heavy compound. Brace.", id, "sc_1")
    }
}

object HypertrophyCompoundEngine : Engine {
    override val id = "hypertrophy_compound"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        val orm = input.user1RM ?: return PrescriptionFragment(4, "8-12", null, null, 120, "30X1", "Test 1RM first.", id, "hc_1")
        val w = kotlin.math.max(45.0, orm * 0.70 * input.phaseMultiplier).coerceAtMost(input.envelope.maxWeight)
        return PrescriptionFragment(4, "8-12", w, 0.70, 120, "30X1", "Hypertrophy compound. Feel the stretch.", id, "hc_1")
    }
}

object HypertrophyIsolationEngine : Engine {
    override val id = "hypertrophy_isolation"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        val w = input.lastWeight?.let { it + 2.5 } ?: 15.0
        return PrescriptionFragment(3, "12-15", w.coerceAtMost(input.envelope.maxWeight), null, 60, "20X2", "Isolation. Squeeze at peak.", id, "hi_1")
    }
}

object StrengthBandEngine : Engine {
    override val id = "strength_band"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        return PrescriptionFragment(4, "8-12", null, null, 60, "20X0", "Banded strength. Control the tension.", id, "sb_1")
    }
}

object HypertrophyBandEngine : Engine {
    override val id = "hypertrophy_band"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        return PrescriptionFragment(3, "15-20", null, null, 45, "20X2", "Banded hypertrophy. Time under tension.", id, "hb_1")
    }
}

object EnduranceIsometricEngine : Engine {
    override val id = "endurance_isometric"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        val dur = input.lastWeight?.let { kotlin.math.min(input.envelope.maxDuration, it + 10) } ?: 30.0
        return PrescriptionFragment(3, "hold", null, null, 45, "static", "Hold. Breathe.", id, "ei_1")
    }
}

object EnduranceDynamicEngine : Engine {
    override val id = "endurance_dynamic"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        return PrescriptionFragment(1, "continuous", null, null, 0, "steady", "Zone 2. Nasal breathing.", id, "ed_1")
    }
}

object StrengthBodyweightEngine : Engine {
    override val id = "strength_bodyweight"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        val bw = input.userBodyweight ?: 150.0
        return PrescriptionFragment(3, "6-10", bw, null, 120, "20X0", "Bodyweight strength. Full range.", id, "sbw_1")
    }
}

object MobilityEngine : Engine {
    override val id = "mobility"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        return PrescriptionFragment(2, "hold", null, null, 15, "slow", "Breathe into the stretch.", id, "mob_1")
    }
}

object PowerEngine : Engine {
    override val id = "power"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        val orm = input.user1RM ?: return PrescriptionFragment(5, "3", null, null, 180, "X0X0", "Test 1RM first.", id, "pw_1")
        val w = kotlin.math.max(45.0, orm * 0.50 * input.phaseMultiplier)
        return PrescriptionFragment(5, "3", w, 0.50, 180, "X0X0", "Explosive. Full intent.", id, "pw_1")
    }
}

object MaintenanceEngine : Engine {
    override val id = "maintenance"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        val orm = input.user1RM
        val w = orm?.let { it * 0.60 * input.phaseMultiplier }
        return PrescriptionFragment(3, "10-12", w, 0.60, 90, "20X0", "Maintenance. Keep moving.", id, "mt_1")
    }
}

object MinimalDoseEngine : Engine {
    override val id = "minimal_dose"
    override fun prescribe(input: EngineInput): PrescriptionFragment {
        return PrescriptionFragment(2, "10-15", null, null, 30, "20X0", "Minimal dose. Just enough.", id, "md_1")
    }
}
