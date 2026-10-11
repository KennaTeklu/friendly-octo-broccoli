package com.peakform.fitness

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.peakform.fitness.core.ProfileState
import com.peakform.fitness.core.ProDb
import com.peakform.fitness.core.Profiles
import com.peakform.fitness.core.ProState
import com.peakform.fitness.core.WorkoutRecord
import com.peakform.fitness.core.WorkoutData
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * BATCH-4D evidence test — P4D-CRASH-01/02/03/04.
 * Re-applied in 4E (4D fix is a non-regression requirement).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch4DConcurrentSaveTest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        ProDb.close("pro.db")
        ProDb.close("pro_p_test1.db")
        ProDb.close("pro_p_test2.db")
        ctx.deleteDatabase("pro.db")
        ctx.deleteDatabase("pro_p_test1.db")
        ctx.deleteDatabase("pro_p_test2.db")
        com.peakform.fitness.core.ProPrefs.clearAll(ctx)
        ProState.init(ctx)
    }

    @After
    fun tearDown() {
        ProState.flush()
        ProDb.close("pro.db")
        ProDb.close("pro_p_test1.db")
        ProDb.close("pro_p_test2.db")
    }

    @Test
    fun concurrentSavesWithProfileSwitch_noIllegalStateException() {
        val workout = WorkoutRecord(id = "w_concurrent", date = ProState.todayLocal(), exercises = emptyList())
        ProState.currentWorkout = workout
        ProState.data = WorkoutData(workouts = listOf(workout))
        for (i in 1..20) {
            ProState.saveWorkoutData()
            ProState.saveCurrentWorkoutToStorage()
        }
        val switchThread = Thread {
            try {
                Profiles.switchTo(ctx, Profiles.DEFAULT)
            } catch (e: IllegalStateException) {
                throw AssertionError("Profile switch crashed: ${e.message}", e)
            }
        }
        switchThread.start()
        for (i in 1..20) {
            try {
                ProState.saveWorkoutData()
                ProState.saveCurrentWorkoutToStorage()
            } catch (e: IllegalStateException) {
                throw AssertionError("Concurrent save crashed: ${e.message}", e)
            }
        }
        switchThread.join(5000)
        Assert.assertFalse("Profile switch thread timed out", switchThread.isAlive)
    }

    @Test
    fun concurrentGetAndClose_noDatabaseNotOpen() {
        val dbName = "pro_p_test1.db"
        val errors = mutableListOf<Exception>()
        val reader = Thread {
            for (i in 1..50) {
                try {
                    ProDb.withDb(ctx, dbName) { dao ->
                        kotlinx.coroutines.runBlocking { dao.userJson() }
                    }
                } catch (e: IllegalStateException) { errors.add(e) }
            }
        }
        val closer = Thread {
            for (i in 1..50) { try { ProDb.close(dbName) } catch (_: Exception) {} }
        }
        reader.start(); closer.start()
        reader.join(10000); closer.join(10000)
        Assert.assertTrue("Expected zero IllegalStateException, got ${errors.size}", errors.isEmpty())
    }

    @Test
    fun flushWaitsForAllInFlightSaves() {
        val workout = WorkoutRecord(id = "w_flush", date = ProState.todayLocal(), exercises = emptyList())
        ProState.currentWorkout = workout
        ProState.data = WorkoutData(workouts = listOf(workout))
        ProState.saveWorkoutData()
        ProState.saveCurrentWorkoutToStorage()
        ProState.saveWorkoutData()
        ProState.saveCurrentWorkoutToStorage()
        ProState.flush()
        ProDb.close(Profiles.dbNameFor(ctx, Profiles.activeId(ctx)))
    }

    @Test
    fun deleteClosesBeforeUnlink() {
        val profile = Profiles.create(ctx, "TestUser", null)
        val dbName = Profiles.dbNameFor(ctx, profile.id)
        ProDb.withDb(ctx, dbName) { dao ->
            kotlinx.coroutines.runBlocking {
                dao.putUser(com.peakform.fitness.core.UserRow("main", "{}"))
            }
        }
        Profiles.delete(ctx, profile.id)
        val dbFile = ctx.getDatabasePath(dbName)
        Assert.assertFalse("DB file should be deleted", dbFile.exists())
    }
}
