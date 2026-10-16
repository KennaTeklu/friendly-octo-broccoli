package com.peakform.fitness

import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.state.RecoveryCurves
import com.peakform.fitness.core.state.StateModel
import com.peakform.fitness.core.ProJson
import com.peakform.fitness.core.ProPrefs
import com.peakform.fitness.core.Profiles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

/**
 * BATCH-39e — Decay + recovery curves.
 *
 * Source: source/docs/system/07-the-field.md § "Decay" +
 *         source/docs/system/09-recovery-curves.md.
 *
 * Tests:
 *   1. default curves map every taxonomy muscle id
 *   2. decay reduces a slot's current by exp(-t/T)
 *   3. a slot with zero elapsed time is unchanged
 *   4. a slot decayed below 0.01 is zeroed
 *   5. peak is never decayed
 *   6. decayField is idempotent when called twice with the same `now`
 *   7. the default curve for quads is 48h, for biceps 24h
 *   8. decay runs on loadAll
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch39eTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        ProState.init(ctx)
        ProState.sessions = emptyList()
        ProState.field = StateModel.FieldState()
        ProState.recovery = StateModel.RecoveryState()
    }

    // 1. default curves map every taxonomy muscle id
    //
    // The spec lists 6 buckets of muscle ids. Every id in those buckets must
    // map to its bucket's authored time. Any other id (e.g. the long-tail
    // dossier ids like "infraspinatus") must fall through to the 48h default
    // — the spec explicitly says "If a muscle id is not in any bucket,
    // default to 48h and log a warning."
    @Test
    fun defaultCurves_mapEverySpecBucketMuscleId() {
        // compound → 48h
        RecoveryCurves.COMPOUND_MUSCLES.forEach { m ->
            assertEquals("compound muscle $m should be 48h",
                RecoveryCurves.COMPOUND_H, RecoveryCurves.defaultHours(m), 0.001)
        }
        // accessory → 24h
        RecoveryCurves.ACCESSORY_MUSCLES.forEach { m ->
            assertEquals("accessory muscle $m should be 24h",
                RecoveryCurves.ACCESSORY_H, RecoveryCurves.defaultHours(m), 0.001)
        }
        // core → 24h (isometric)
        RecoveryCurves.CORE_MUSCLES.forEach { m ->
            assertEquals("core muscle $m should be 24h (isometric)",
                RecoveryCurves.ISOMETRIC_H, RecoveryCurves.defaultHours(m), 0.001)
        }
        // mobility → 6h
        RecoveryCurves.MOBILITY_MUSCLES.forEach { m ->
            assertEquals("mobility muscle $m should be 6h",
                RecoveryCurves.MOBILITY_H, RecoveryCurves.defaultHours(m), 0.001)
        }
        // extremities → 24h
        RecoveryCurves.EXTREMITIES_MUSCLES.forEach { m ->
            assertEquals("extremities muscle $m should be 24h",
                RecoveryCurves.EXTREMITIES_H, RecoveryCurves.defaultHours(m), 0.001)
        }
        // athletic → 12h
        RecoveryCurves.ATHLETIC_MUSCLES.forEach { m ->
            assertEquals("athletic muscle $m should be 12h",
                RecoveryCurves.ATHLETIC_H, RecoveryCurves.defaultHours(m), 0.001)
        }
        // Unknown muscle id → 48h default (per spec).
        assertEquals("unknown muscle id should default to 48h",
            RecoveryCurves.DEFAULT_H,
            RecoveryCurves.defaultHours("zzz_not_a_real_muscle_zzz"),
            0.001)
        // Components → 24h fixed.
        listOf("Strength", "Endurance", "Cardio", "Flexibility", "Body Comp",
            "Power", "Speed", "Agility", "Balance", "Coordination", "Reaction"
        ).forEach { c ->
            assertEquals("component $c should be 24h",
                RecoveryCurves.COMPONENT_H,
                RecoveryCurves.defaultHoursForComponent(c),
                0.001)
        }
        // Every bucket id is in ALL_BUCKETED.
        RecoveryCurves.ALL_BUCKETED.forEach { m ->
            assertTrue("bucket muscle $m must be in ALL_BUCKETED",
                RecoveryCurves.ALL_BUCKETED.contains(m))
        }
        // ALL_BUCKETED must be the union of the 6 spec buckets.
        val expectedUnion = RecoveryCurves.COMPOUND_MUSCLES +
            RecoveryCurves.ACCESSORY_MUSCLES +
            RecoveryCurves.CORE_MUSCLES +
            RecoveryCurves.MOBILITY_MUSCLES +
            RecoveryCurves.EXTREMITIES_MUSCLES +
            RecoveryCurves.ATHLETIC_MUSCLES
        assertEquals("ALL_BUCKETED must be the union of the 6 spec buckets",
            expectedUnion, RecoveryCurves.ALL_BUCKETED)
    }

    // 2. decay reduces a slot's current by exp(-t/T)
    //
    // A slot with current=10, lastUpdate=0, T=48h, decayed at now=48h
    // (so elapsedHours=48) should reduce current to 10 * exp(-1) ≈ 3.679.
    @Test
    fun decayField_reducesCurrentByExpMinusTOverT() {
        val T = 48.0 // quads default
        val startCurrent = 10.0
        val now = 48L * 3_600_000L  // 48 hours in millis
        ProState.field = StateModel.FieldState(
            muscles = mapOf("quads" to StateModel.FieldSlot(
                current = startCurrent,
                peak = startCurrent,
                lastUpdate = 0L,
            ))
        )
        // recovery stays empty → curveFor falls back to defaultHours("quads") = 48h.
        ProState.decayField(now)
        val slot = ProState.fieldSlot("quads")!!
        val expected = startCurrent * kotlin.math.exp(-48.0 / T)
        assertEquals("current after 48h decay should be 10 * exp(-1) ≈ $expected",
            expected, slot.current, 0.001)
        // peak must NOT be decayed — covered in test 5, but assert here too.
        assertEquals("peak must NOT be decayed",
            startCurrent, slot.peak, 0.001)
        // lastUpdate must be advanced to `now`.
        assertEquals("lastUpdate must be advanced to now",
            now, slot.lastUpdate)
    }

    // 3. a slot with zero elapsed time is unchanged
    //
    // Decay with the same `now` as the slot's lastUpdate should leave
    // current untouched (factor = exp(0) = 1).
    @Test
    fun decayField_zeroElapsedTimeIsUnchanged() {
        val lastUpdate = 1_700_000_000_000L
        val startCurrent = 7.5
        ProState.field = StateModel.FieldState(
            muscles = mapOf("quads" to StateModel.FieldSlot(
                current = startCurrent,
                peak = 10.0,
                lastUpdate = lastUpdate,
            ))
        )
        ProState.decayField(now = lastUpdate)  // elapsedHours == 0
        val slot = ProState.fieldSlot("quads")!!
        assertEquals("current must be unchanged when elapsedHours == 0",
            startCurrent, slot.current, 0.0001)
        assertEquals("peak must be unchanged",
            10.0, slot.peak, 0.0001)
        assertEquals("lastUpdate must be unchanged",
            lastUpdate, slot.lastUpdate)
    }

    // 4. a slot decayed below 0.01 is zeroed
    //
    // The floor at 0.01 prevents floating-point dust from accumulating.
    // A slot that decayed to current=0.005 (below the 0.01 floor) is
    // zeroed; peak is preserved.
    @Test
    fun decayField_belowFloorIsZeroed() {
        // Set up a slot whose current is small enough that one decay step
        // takes it below 0.01. quads T=48h. Start current=0.01, decay for
        // 48h → 0.01 * exp(-1) ≈ 0.00368 → below 0.01 → zeroed.
        val startCurrent = 0.01
        val peak = 1.0
        val now = 48L * 3_600_000L
        ProState.field = StateModel.FieldState(
            muscles = mapOf("quads" to StateModel.FieldSlot(
                current = startCurrent,
                peak = peak,
                lastUpdate = 0L,
            ))
        )
        ProState.decayField(now)
        val slot = ProState.fieldSlot("quads")!!
        assertEquals("current below 0.01 after decay must be zeroed",
            0.0, slot.current, 0.0)
        assertEquals("peak must be preserved even when current is zeroed",
            peak, slot.peak, 0.001)
        assertEquals("lastUpdate must be advanced",
            now, slot.lastUpdate)
    }

    // 5. peak is never decayed
    //
    // Peak is the historical max — only grows (via accumulateField), never
    // shrinks. Decay must not touch it.
    @Test
    fun decayField_peakIsNeverDecayed() {
        // A slot with current much smaller than peak — decay over 96h should
        // shrink current toward 0 but leave peak at its original value.
        val startCurrent = 5.0
        val peak = 100.0
        val now = 96L * 3_600_000L  // 96h, ~2x recovery time → factor ≈ exp(-2) ≈ 0.135
        ProState.field = StateModel.FieldState(
            muscles = mapOf("quads" to StateModel.FieldSlot(
                current = startCurrent,
                peak = peak,
                lastUpdate = 0L,
            ))
        )
        ProState.decayField(now)
        val slot = ProState.fieldSlot("quads")!!
        // current = 5 * exp(-2) ≈ 0.677 (above floor, not zeroed)
        val expectedCurrent = startCurrent * kotlin.math.exp(-96.0 / 48.0)
        assertEquals("current should be 5 * exp(-2) ≈ $expectedCurrent",
            expectedCurrent, slot.current, 0.001)
        // PEAK IS UNCHANGED.
        assertEquals("peak MUST be preserved at $peak (never decayed)",
            peak, slot.peak, 0.001)
    }

    // 6. decayField is idempotent when called twice with the same `now`
    //
    // First call: lastUpdate was t0, now=t1, factor=exp(-(t1-t0)/T),
    //             new current = old * factor, new lastUpdate = t1.
    // Second call (same now=t1): lastUpdate=t1, now=t1, factor=exp(0)=1,
    //             current unchanged, lastUpdate unchanged.
    @Test
    fun decayField_isIdempotentWithSameNow() {
        val startCurrent = 10.0
        val now = 48L * 3_600_000L  // 48h elapsed
        ProState.field = StateModel.FieldState(
            muscles = mapOf("quads" to StateModel.FieldSlot(
                current = startCurrent,
                peak = 20.0,
                lastUpdate = 0L,
            ))
        )
        ProState.decayField(now)
        val after1 = ProState.fieldSlot("quads")!!
        val expectedAfter1 = startCurrent * kotlin.math.exp(-48.0 / 48.0)
        assertEquals("first decay should reduce current by exp(-1)",
            expectedAfter1, after1.current, 0.001)

        // Second call with the SAME now → no change.
        ProState.decayField(now)
        val after2 = ProState.fieldSlot("quads")!!
        assertEquals("second decay with same now must be a no-op (current)",
            after1.current, after2.current, 0.0001)
        assertEquals("second decay with same now must be a no-op (peak)",
            after1.peak, after2.peak, 0.0001)
        assertEquals("second decay with same now must be a no-op (lastUpdate)",
            after1.lastUpdate, after2.lastUpdate)
    }

    // 7. the default curve for quads is 48h, for biceps 24h
    @Test
    fun defaultCurve_quadsIs48h_bicepsIs24h() {
        val quadsCurve = RecoveryCurves.defaultCurve("quads")
        assertEquals("quads is a compound-family muscle → 48h",
            48.0, quadsCurve.recoveryHours, 0.001)
        assertEquals("quads default curve source = 'default'",
            "default", quadsCurve.source)
        assertEquals("quads default curve samples = 0",
            0, quadsCurve.samples)
        assertEquals("quads curve muscleId is 'quads'",
            "quads", quadsCurve.muscleId)

        val bicepsCurve = RecoveryCurves.defaultCurve("biceps")
        assertEquals("biceps is an accessory muscle → 24h",
            24.0, bicepsCurve.recoveryHours, 0.001)
        assertEquals("biceps default curve source = 'default'",
            "default", bicepsCurve.source)
        assertEquals("biceps default curve samples = 0",
            0, bicepsCurve.samples)

        // curveFor returns defaults when no learned curve is registered.
        val fromProState = ProState.curveFor("quads")
        assertEquals("ProState.curveFor(quads) should return default 48h curve",
            48.0, fromProState.recoveryHours, 0.001)
        assertEquals("ProState.curveFor(quads).source = 'default'",
            "default", fromProState.source)

        // curveFor with a learned curve registered in recovery state returns
        // the learned curve (preview of v39e-2 — the schema is ready even
        // though learning is not implemented in this batch).
        ProState.recovery = StateModel.RecoveryState(curves = mapOf(
            "quads" to StateModel.RecoveryCurve(
                muscleId = "quads",
                recoveryHours = 42.0,
                source = "learned",
                samples = 7,
            )
        ))
        val learned = ProState.curveFor("quads")
        assertEquals("ProState.curveFor(quads) with learned curve should return 42h",
            42.0, learned.recoveryHours, 0.001)
        assertEquals("ProState.curveFor(quads) with learned curve source = 'learned'",
            "learned", learned.source)
        assertEquals("ProState.curveFor(quads) with learned curve samples = 7",
            7, learned.samples)
    }

    // 8. decay runs on loadAll
    //
    // loadAll() calls loadField() + loadRecovery() + decayField() in that
    // order. Verifying the call sequence directly needs a spy; instead we
    // verify the observable contract: after loadAll(), a slot whose
    // persisted lastUpdate is in the past has its current decayed to match
    // the elapsed time since lastUpdate, and lastUpdate is advanced to
    // "now" (the wall clock at loadAll() time).
    @Test
    fun decayField_runsOnLoadAll() {
        // Pre-populate the field ProPrefs key with a known slot at a known
        // past lastUpdate. Then call loadAll() and verify the slot was
        // decayed against the current wall clock.
        val pastCurrent = 10.0
        val pastLastUpdate = System.currentTimeMillis() - 48L * 3_600_000L  // 48h ago
        val persistedField = StateModel.FieldState(
            muscles = mapOf("quads" to StateModel.FieldSlot(
                current = pastCurrent,
                peak = pastCurrent,
                lastUpdate = pastLastUpdate,
            ))
        )
        val fieldJson = ProJson.json.encodeToString(
            StateModel.FieldState.serializer(), persistedField)
        ProPrefs.put(ctx,
            "p9_field_" + Profiles.activeId(ctx),
            fieldJson)
        // recovery stays empty → decay uses defaults.

        // Run loadAll() — should load the field, then decay it.
        kotlinx.coroutines.runBlocking {
            ProState.loadAll()
        }

        val slot = ProState.fieldSlot("quads")!!
        // Elapsed hours since pastLastUpdate ≈ 48h. T for quads = 48h.
        // Expected current ≈ 10 * exp(-1) ≈ 3.679. Use a generous tolerance
        // because the actual elapsed depends on how long the test takes to
        // reach decayField() (the wall clock has moved on by milliseconds).
        val elapsedHours = (slot.lastUpdate - pastLastUpdate) / 3_600_000.0
        val T = 48.0
        val expectedCurrent = pastCurrent * kotlin.math.exp(-elapsedHours / T)
        // Allow ±5% tolerance to absorb the timing drift.
        assertTrue(
            "after loadAll, quads.current should be near 10*exp(-elapsedHours/48) ≈ $expectedCurrent (got ${slot.current}); elapsedHours=$elapsedHours",
            abs(slot.current - expectedCurrent) / expectedCurrent < 0.05
        )
        // peak must NOT be decayed.
        assertEquals("after loadAll, peak must be preserved",
            pastCurrent, slot.peak, 0.001)
        // lastUpdate must be advanced past the persisted value (decayField
        // stamps lastUpdate = now).
        assertTrue("after loadAll, lastUpdate must be advanced past the persisted value",
            slot.lastUpdate > pastLastUpdate)
    }
}
