package com.peakform.fitness

import com.peakform.fitness.coach.dossiers.DossierRegistry
import com.peakform.fitness.engine.Library
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-37a (updated for v37c dossiers).
 * v37c changed dossiers.json — all entries now have sources (sommer/mcgill
 * or peakform-2026). Updated to match.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch37aTest {

    private val ctx: android.content.Context
        get() = androidx.test.core.app.ApplicationProvider.getApplicationContext()

    @Test
    fun dossiersCount_equalsExercisesJsonCount() {
        Library.ensure(ctx)
        DossierRegistry.ensure(ctx)
        // Count exercises in exercises.json
        val raw = ctx.assets.open("data/exercises.json").bufferedReader().use { it.readText() }
        val j = Json { ignoreUnknownKeys = true; isLenient = true }
        val exObj = j.parseToJsonElement(raw).jsonObject
        var count = 0
        for ((_, groupVal) in exObj) {
            val arr = groupVal as? JsonArray ?: continue
            count += arr.size
        }
        Assert.assertEquals(count, DossierRegistry.all().size)
    }

    @Test
    fun everyDossierHasNonEmptySource() {
        Library.ensure(ctx)
        DossierRegistry.ensure(ctx)
        for (dossier in DossierRegistry.all()) {
            Assert.assertTrue(dossier.provenance.source.isNotBlank())
        }
    }

    @Test
    fun registryForExercise_returnsNullForUnknownId() {
        Library.ensure(ctx)
        DossierRegistry.ensure(ctx)
        Assert.assertNull(DossierRegistry.forExercise("zzz_not_a_real_exercise_xyz"))
    }
}
