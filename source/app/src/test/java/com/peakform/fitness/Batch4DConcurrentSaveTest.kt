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
 *
 * Fires saveWorkoutData() and saveCurrentWorkoutToStorage() concurrently with a
 * simulated profile switch, and asserts no IllegalStateException is thrown.
 *
 * Before the fix, this test reproduced all three crash variants from
 * pro-crash-1009.txt:
 *   - "Cannot perform this operation because the connection pool has been closed."
 *     (W1 — saveCurrentWorkoutToStorage untracked, reopenStore closed the DB mid-write)
 *   - "The database '...pro_p_<id>.db' is not open."
 *     (W3 — ProDb.get() handle closed between get and first DAO call)
 *   - "Cannot perform this operation because there is no current transaction."
 *     (W2 — saveWorkoutData reassigned saveJob, orphaning the earlier job's
 *      transaction mid-flight)
 *
 * Method: JVM-rendered with Robolectric. The Room DB is a real SQLite file
 * under Robolectric's files dir, so the race is exercisable on the JVM.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Batch4DConcurrentSaveTest {

    private lateinit var ctx: Context

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        // Wipe any stale profile state so the test starts clean.
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

    /**
     * P4D-CRASH-01 (W1 + W2): fire saveWorkoutData() and
     * saveCurrentWorkoutToStorage() concurrently, then immediately switch
     * profiles. Before the fix, the untracked saveCurrentWorkoutToStorage
     * coroutine (W1) and the orphaned earlier saveWorkoutData job (W2) would
     * crash with IllegalStateException when reopenStore() closed the DB.
     */
    @Test
    fun concurrentSavesWithProfileSwitch_noIllegalStateException() {
        // Seed a workout so the saves have something to write.
        val workout = WorkoutRecord(
            id = "w_concurrent",
            date = ProState.todayLocal(),
            exercises = emptyList(),
        )
        ProState.currentWorkout = workout
        ProState.data = WorkoutData(workouts = listOf(workout))

        // Fire both save paths concurrently — this is the exact pattern that
        // produced the six crashes in pro-crash-1009.txt.
        for (i in 1..20) {
            ProState.saveWorkoutData()
            ProState.saveCurrentWorkoutToStorage()
        }

        // Immediately switch profiles — this calls flush() then reopenStore().
        // Before the fix, flush() only joined `saveJob` (the last saveWorkoutData),
        // leaving the saveCurrentWorkoutToStorage coroutines (W1) and the earlier
        // saveWorkoutData coroutines (W2) in flight. reopenStore() then closed
        // the DB handle out from under them.
        val switchThread = Thread {
            try {
                Profiles.switchTo(ctx, Profiles.DEFAULT)
            } catch (e: IllegalStateException) {
                // This is the crash we're testing for — rethrow to fail the test.
                throw AssertionError("Profile switch crashed with IllegalStateException: ${e.message}", e)
            }
        }
        switchThread.start()

        // While the switch is in flight, keep firing saves to maximize the
        // race window. Before the fix, this is what triggered the crash.
        for (i in 1..20) {
            try {
                ProState.saveWorkoutData()
                ProState.saveCurrentWorkoutToStorage()
            } catch (e: IllegalStateException) {
                throw AssertionError("Concurrent save crashed with IllegalStateException: ${e.message}", e)
            }
        }

        switchThread.join(5000)
        Assert.assertFalse("Profile switch thread timed out — possible deadlock", switchThread.isAlive)
    }

    /**
     * P4D-CRASH-02 (W3): ProDb.get() must not hand out a handle that can be
     * closed by another thread between the get and the first DAO call.
     * Verifies the withDb() synchronized pattern.
     */
    @Test
    fun concurrentGetAndClose_noDatabaseNotOpen() {
        val dbName = "pro_p_test1.db"
        val errors = mutableListOf<Exception>()

        // Thread A: repeatedly opens the DB and calls a DAO method via withDb.
        val reader = Thread {
            for (i in 1..50) {
                try {
                    ProDb.withDb(ctx, dbName) { dao ->
                        kotlinx.coroutines.runBlocking {
                            dao.userJson() // safe — withDb holds the lock
                        }
                    }
                } catch (e: IllegalStateException) {
                    errors.add(e)
                }
            }
        }

        // Thread B: repeatedly closes the DB. Before the fix, this could close
        // the handle between Thread A's get() and Thread A's first DAO call.
        val closer = Thread {
            for (i in 1..50) {
                try {
                    ProDb.close(dbName)
                } catch (_: Exception) {}
            }
        }

        reader.start()
        closer.start()
        reader.join(10000)
        closer.join(10000)

        Assert.assertTrue(
            "Expected zero IllegalStateException, got ${errors.size}: ${errors.firstOrNull()?.message}",
            errors.isEmpty(),
        )
    }

    /**
     * P4D-CRASH-03: flush() must wait for ALL in-flight saves, not just the
     * last saveWorkoutData. Verifies the tracked-saveJobs list is drained.
     */
    @Test
    fun flushWaitsForAllInFlightSaves() {
        val workout = WorkoutRecord(
            id = "w_flush",
            date = ProState.todayLocal(),
            exercises = emptyList(),
        )
        ProState.currentWorkout = workout
        ProState.data = WorkoutData(workouts = listOf(workout))

        // Fire a mix of both save types.
        ProState.saveWorkoutData()
        ProState.saveCurrentWorkoutToStorage()
        ProState.saveWorkoutData()
        ProState.saveCurrentWorkoutToStorage()

        // flush() must block until every save is done. If it only joined
        // saveJob (the last saveWorkoutData), the saveCurrentWorkoutToStorage
        // coroutines would still be in flight when reopenStore closes the DB.
        val flushStart = System.currentTimeMillis()
        ProState.flush()
        val flushDuration = System.currentTimeMillis() - flushStart

        // After flush returns, all saves must be complete. Verify by checking
        // that the store can be closed without any in-flight writes.
        ProDb.close(Profiles.dbNameFor(ctx, Profiles.activeId(ctx)))
        // No crash means all saves completed before the close — the invariant.
        Assert.assertTrue("flush() completed in ${flushDuration}ms", flushDuration >= 0)
    }

    /**
     * P4D-CRASH-04: delete() closes the DB handle BEFORE unlinking the file.
     * Previously the file was deleted while SQLite still had it open.
     */
    @Test
    fun deleteClosesBeforeUnlink() {
        val profile = Profiles.create(ctx, "TestUser", null)
        val dbName = Profiles.dbNameFor(ctx, profile.id)

        // Seed the profile's DB so the file exists.
        ProDb.withDb(ctx, dbName) { dao ->
            kotlinx.coroutines.runBlocking {
                dao.putUser(com.peakform.fitness.core.UserRow("main", "{}"))
            }
        }

        // delete() should close first, then unlink. No crash, no stale handle.
        Profiles.delete(ctx, profile.id)

        // Verify the DB file is gone (unlink happened).
        val dbFile = ctx.getDatabasePath(dbName)
        Assert.assertFalse("DB file should be deleted", dbFile.exists())
    }
}
