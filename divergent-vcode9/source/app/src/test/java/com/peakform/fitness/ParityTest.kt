package com.peakform.fitness

import com.peakform.fitness.core.Heal
import com.peakform.fitness.core.HealthScreen
import com.peakform.fitness.core.StreakFire
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * v1.3.0 parity unit tests: JSON healing, health-screen tier verdicts (verbatim
 * legacy computeTier rules), streak-fire intensity tiers.
 */
class ParityTest {

    // ---------- Heal ----------

    @Test
    fun `heal passes valid json through unchanged`() {
        val raw = """{"a":1,"b":[1,2,3],"c":"x"}"""
        assertEquals(raw, Heal.heal(raw))
        assertNotNull(Heal.parseObject(raw))
    }

    @Test
    fun `heal strips markdown code fences`() {
        val raw = "```json\n{\"workoutData\":true}\n```"
        assertNotNull(Heal.parseObject(raw))
        assertTrue(Heal.describeFixes(raw).any { it.contains("fences") })
    }

    @Test
    fun `heal converts smart quotes`() {
        val raw = "{\u201Cname\u201D:\u201CAthlete\u201D}"
        val obj = Heal.parseObject(raw)
        assertNotNull(obj)
        assertEquals("Athlete", obj!!["name"]!!.jsonPrimitive.content)
    }

    @Test
    fun `heal removes trailing commas`() {
        val raw = """{"a":[1,2,3,],"b":{"c":1,},"d":[[1,2],[3,4],]}"""
        assertNotNull(Heal.parseObject(raw))
    }

    @Test
    fun `heal converts ellipsis and dashes`() {
        val raw = "{\"note\":\"wait…\",\"range\":\"1–5\",\"dash\":\"a—b\"}"
        val obj = Heal.parseObject(raw)
        assertNotNull(obj)
        assertEquals("wait...", obj!!["note"]!!.jsonPrimitive.content)
        assertEquals("1-5", obj["range"]!!.jsonPrimitive.content)
    }

    @Test
    fun `heal replaces NaN with null`() {
        val raw = """{"mu":NaN,"sigma2":Infinity}"""
        val obj = Heal.parseObject(raw)
        assertNotNull(obj)
    }

    @Test
    fun `heal strips BOM`() {
        val raw = "\uFEFF{\"a\":1}"
        assertNotNull(Heal.parseObject(raw))
    }

    // ---------- HealthScreen verdicts (legacy computeTier) ----------

    private fun tier(general: Map<String, Boolean>, followup: Map<String, Boolean> = emptyMap()) =
        HealthScreen.computeTier(general, followup).tier

    @Test
    fun `all no answers is tier A`() {
        val g = HealthScreen.GENERAL.associate { it.id to false }
        assertEquals("A", tier(g))
    }

    @Test
    fun `chest pain dizziness and physician restriction are tier C`() {
        assertEquals("C", tier(mapOf("g2" to true)))
        assertEquals("C", tier(mapOf("g3" to true)))
        assertEquals("C", tier(mapOf("g7" to true)))
    }

    @Test
    fun `heart condition with limited activity is C, stable is B`() {
        assertEquals("C", tier(mapOf("g1" to true), mapOf("h1" to true)))
        assertEquals("C", tier(mapOf("g1" to true), mapOf("h2" to true)))
        assertEquals("B", tier(mapOf("g1" to true), mapOf("h1" to false, "h2" to false)))
    }

    @Test
    fun `joint flag is B`() {
        assertEquals("B", tier(mapOf("g4" to true)))
    }

    @Test
    fun `medication changed recently is C, stable is B`() {
        assertEquals("C", tier(mapOf("g5" to true), mapOf("m1" to true)))
        assertEquals("B", tier(mapOf("g5" to true), mapOf("m1" to false)))
    }

    @Test
    fun `pregnancy without obstetric clearance is C, with clearance is B`() {
        assertEquals("C", tier(mapOf("g6" to true), mapOf("p1" to false)))
        assertEquals("B", tier(mapOf("g6" to true), mapOf("p1" to true)))
    }

    @Test
    fun `joint modification text lists joints`() {
        val mods = HealthScreen.jointModifications(listOf("Knee", "Shoulder"))
        assertEquals("Joint modifications: Knee, Shoulder", mods)
    }

    // ---------- StreakFire intensity tiers (legacy updateStreakFire) ----------

    @Test
    fun `fire is full within 2 days`() {
        val f = StreakFire.of(5, 2)
        assertEquals("full", f.intensity)
        assertEquals("🔥", f.glyph)
        assertEquals(1f, f.opacity)
    }

    @Test
    fun `fire dims at 3-4 days and smokes at 5-6 days`() {
        assertEquals("dim", StreakFire.of(5, 3).intensity)
        assertEquals("dim", StreakFire.of(5, 4).intensity)
        assertEquals("smoke", StreakFire.of(5, 5).intensity)
        assertEquals("smoke", StreakFire.of(5, 6).intensity)
    }

    @Test
    fun `fire is dead at streak zero or 7+ days`() {
        assertEquals("dead", StreakFire.of(0, 0).intensity)
        assertEquals("dead", StreakFire.of(12, 7).intensity)
        assertEquals("❄️", StreakFire.of(0, 3).glyph)
    }

    @Test
    fun `daysSinceLast handles blank history`() {
        assertEquals(99, StreakFire.daysSinceLast(com.peakform.fitness.core.WorkoutData(), 1000L))
    }

    // ---------- Phase multipliers ----------

    @Test
    fun `cycle multipliers match legacy`() {
        assertEquals(0.6, com.peakform.fitness.core.Period.MULTIPLIER_MENSTRUAL, 0.0001)
        assertEquals(1.0, com.peakform.fitness.core.Period.MULTIPLIER_FOLLICULAR, 0.0001)
        assertEquals(1.05, com.peakform.fitness.core.Period.MULTIPLIER_OVULATORY, 0.0001)
        assertEquals(0.8, com.peakform.fitness.core.Period.MULTIPLIER_LUTEAL, 0.0001)
    }

    // ---------- v1.4.0: RigArt pattern classifier (exercise media) ----------

    private fun pattern(name: String, equipment: String = "barbell", vararg muscles: String) =
        com.peakform.fitness.ui.components.patternFor(name, equipment, muscles.toList())

    @Test
    fun `pattern classifier maps movement keywords`() {
        assertEquals("bench", pattern("Barbell Bench Press"))
        assertEquals("squat", pattern("Barbell Back Squat (high bar)"))
        assertEquals("hinge", pattern("Romanian Deadlift"))
        assertEquals("pullup", pattern("Pull-Up", "bodyweight"))
        assertEquals("pulldown", pattern("Lat Pulldown", "cable"))
        assertEquals("pushup", pattern("Push-Up", "bodyweight"))
        assertEquals("row", pattern("Barbell Row"))
        assertEquals("curl", pattern("Dumbbell Biceps Curl", "dumbbell"))
        assertEquals("pushdown", pattern("Triceps Pushdown", "cable"))
        assertEquals("raise", pattern("Dumbbell Lateral Raise", "dumbbell"))
        assertEquals("plank", pattern("Plank", "bodyweight"))
        assertEquals("thrust", pattern("Barbell Hip Thrust"))
        assertEquals("calf", pattern("Standing Calf Raise", "machine"))
        assertEquals("carry", pattern("Farmer Carry", "dumbbell"))
        assertEquals("crunch", pattern("Bicycle Crunch", "bodyweight"))
        assertEquals("lunge", pattern("Walking Lunge", "bodyweight"))
        assertEquals("jump", pattern("Box Jump", "bodyweight"))
        assertEquals("legraise", pattern("Hanging Leg Raise", "bodyweight"))
        assertEquals("run", pattern("Treadmill Run", "machine"))
        assertEquals("stretch", pattern("Doorway Chest Stretch", "bodyweight"))
    }

    @Test
    fun `pattern classifier falls back through muscles`() {
        assertEquals("squat", pattern("Wall Sit", "bodyweight", "quadriceps"))
        assertEquals("bench", pattern("Chest Squeeze", "bodyweight", "chest"))
        assertEquals("stand", pattern("Mystery Move", "bodyweight", "unknown_muscle"))
    }

    // ---------- v1.4.0: PlateMath ----------

    @Test
    fun `plate math breaks down per side loads`() {
        val pm = com.peakform.fitness.core.PlateMath
        assertEquals(listOf(45.0, 45.0), pm.forWeight(225.0).first)
        assertEquals(0.0, pm.forWeight(225.0).second, 0.0001)
        // (217.5-45)/2 = 86.25 -> 45 + 35 + 5 = 85, residual 1.25
        assertEquals(listOf(45.0, 35.0, 5.0), pm.forWeight(217.5).first)
        assertEquals(1.25, pm.forWeight(217.5).second, 0.0001)
        assertTrue(pm.forWeight(45.0).first.isEmpty())
        assertEquals(2.5, pm.forWeight(50.0).first.last(), 0.0001)
        assertEquals("empty bar", pm.describe(40.0))
    }

    // ---------- v1.4.0: 1RM Lab input validation (verbatim legacy modal) ----------

    @Test
    fun `one rm input validation is verbatim legacy`() {
        assertTrue(com.peakform.fitness.core.OneRmInput.validate("", "5") is com.peakform.fitness.core.OneRmInput.Result.Missing)
        assertTrue(com.peakform.fitness.core.OneRmInput.validate("abc", "5") is com.peakform.fitness.core.OneRmInput.Result.Missing)
        assertTrue(com.peakform.fitness.core.OneRmInput.validate("135", "16") is com.peakform.fitness.core.OneRmInput.Result.Missing)
        assertEquals(
            "Reps must be between 1 and 15 for accurate 1RM estimation.",
            (com.peakform.fitness.core.OneRmInput.validate("135", "0") as com.peakform.fitness.core.OneRmInput.Result.Missing).message,
        )
        assertEquals(
            "Please enter both weight and reps.",
            (com.peakform.fitness.core.OneRmInput.validate("", "") as com.peakform.fitness.core.OneRmInput.Result.Missing).message,
        )
        val ok = com.peakform.fitness.core.OneRmInput.validate("135", "8") as com.peakform.fitness.core.OneRmInput.Result.Ok
        assertEquals(135.0, ok.weight, 0.0001)
        assertEquals(8, ok.reps)
        assertTrue(com.peakform.fitness.core.OneRmInput.implausible(700.0, 172.0))
        assertFalse(com.peakform.fitness.core.OneRmInput.implausible(300.0, 172.0))
        assertFalse(com.peakform.fitness.core.OneRmInput.implausible(300.0, null))
    }

    // ---------- v1.4.0: additive ExerciseRecord fields stay export-compatible ----------

    @Test
    fun `exercise record additive 1rm lab fields round trip`() {
        val rec = com.peakform.fitness.core.ExerciseRecord(
            mu = 200.0, tested1RM = 225.0, failWeight = 235.0, testConfidence = 0.8,
            workingWeightNote = "lane 2, seat one notch up",
        )
        val encoded = com.peakform.fitness.core.ProJson.encode(com.peakform.fitness.core.ExerciseRecord.serializer(), rec)
        assertTrue(encoded.contains("\"failWeight\":235.0"))
        assertTrue(encoded.contains("\"testConfidence\":0.8"))
        assertTrue(encoded.contains("\"workingWeightNote\""))

        // legacy payload without the new keys still decodes (ignoreUnknownKeys)
        val back = com.peakform.fitness.core.ProJson.decode(
            com.peakform.fitness.core.ExerciseRecord.serializer(),
            """{"mu":180.5,"tested1RM":null}""",
        )
        assertEquals(null, back.failWeight)
        assertEquals(180.5, back.mu!!, 0.0001)

        // null new fields are omitted from exports (explicitNulls=false ~= JS undefined)
        val nullEncoded = com.peakform.fitness.core.ProJson.encode(
            com.peakform.fitness.core.ExerciseRecord.serializer(),
            com.peakform.fitness.core.ExerciseRecord(mu = 1.0),
        )
        assertFalse(nullEncoded.contains("workingWeightNote"))
    }
}
