package com.peakform.fitness.core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.peakform.fitness.MainActivity
import com.peakform.fitness.ProLog
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Notify — real system notifications (client decision: full permissions).
 * Respects the three legacy toggles: p4_notif_retest_reminder / p4_notif_deload_notice /
 * p4_notif_period_notice. Channels per legacy semantics; WorkManager keeps the daily
 * check alive across restarts.
 */
object Notify {
    const val CH_RETEST = "retest_reminders"
    const val CH_DELOAD = "deload_notices"
    const val CH_PERIOD = "period_notices"

    fun notifEnabled(context: Context, key: String): Boolean =
        ProPrefs.get(context, "p4_notif_$key") != "false"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mk = { id: String, name: String ->
            NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
        }
        nm.createNotificationChannel(mk(CH_RETEST, "1RM retest reminders"))
        nm.createNotificationChannel(mk(CH_DELOAD, "Deload notices"))
        nm.createNotificationChannel(mk(CH_PERIOD, "My Cycle notices"))
    }

    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun post(context: Context, channelId: String, id: Int, title: String, body: String) {
        if (!hasPermission(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pi = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val n = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(id, n)
        } catch (e: Exception) {
            ProLog.e("NOTIFY", "post failed: ${e.message}")
        }
    }

    fun scheduleDaily(context: Context) {
        val req = PeriodicWorkRequestBuilder<CheckWorker>(6, TimeUnit.HOURS)
            .setConstraints(Constraints.NONE)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "pro_notify_check", ExistingPeriodicWorkPolicy.KEEP, req)
    }

    /** The daily evaluation — runs in WorkManager and on app launch. */
    fun evaluateAndPost(context: Context): List<String> {
        ensureChannels(context)
        val fired = mutableListOf<String>()
        val d = ProState.data
        if (!d.workouts.isEmpty() || !d.exercises.isEmpty()) {
            // 1RM retest: calibrated lift with test/last-update older than 8 weeks (legacy Nudges threshold)
            if (notifEnabled(context, "retest_reminder")) {
                val stale = d.exercises.entries.count { (_, rec) ->
                    val ref = rec.testDate ?: rec.lastUpdate
                    if (ref.isNullOrBlank()) false
                    else {
                        val t = ProState.utcDayMillis(ref)
                        t > 0 && (System.currentTimeMillis() - t) > 56L * 24 * 60 * 60 * 1000
                    }
                }
                if (stale > 0) {
                    post(context, CH_RETEST, 4001, "Time to re-test your 1RM",
                        "$stale lift${if (stale == 1) "" else "s"} haven't been tested in over 8 weeks. A fresh 1RM keeps every weight suggestion sharp.")
                    fired += "retest"
                }
            }
            // Deload notice: systemic fatigue high (readiness below 40 → legacy deload band)
            if (notifEnabled(context, "deload_notice")) {
                val readiness = try {
                    com.peakform.fitness.engine.Stats.overallRecoveryPct().toDouble()
                } catch (_: Exception) { 100.0 }
                if (readiness < 40.0 && d.workouts.isNotEmpty()) {
                    post(context, CH_DELOAD, 4002, "Your body is asking for a deload",
                        "Readiness is at ${readiness.roundToInt()}%. Consider a lighter session today — the engine will scale the plan.")
                    fired += "deload"
                }
            }
        }
        return fired
    }

    class CheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            return try {
                ProState.init(applicationContext)
                if (!ProState.initialized) ProState.loadAll()
                evaluateAndPost(applicationContext)
                Result.success()
            } catch (e: Exception) {
                ProLog.e("NOTIFY", "worker failed: ${e.message}")
                Result.retry()
            }
        }
    }
}

/**
 * QR — minimal QR encoder for the share screen. zxing-core (pure Java, offline).
 */
object QR {
    /** Render text as a square ARGB bitmap. */
    fun bitmap(text: String, size: Int, dark: Int, light: Int): android.graphics.Bitmap? {
        return try {
            val hints = mapOf(
                com.google.zxing.EncodeHintType.ERROR_CORRECTION to com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M,
                com.google.zxing.EncodeHintType.MARGIN to 1,
            )
            val matrix = com.google.zxing.qrcode.QRCodeWriter().encode(text, com.google.zxing.BarcodeFormat.QR_CODE, size, size, hints)
            val bmp = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    bmp.setPixel(x, y, if (matrix.get(x, y)) dark else light)
                }
            }
            bmp
        } catch (e: Exception) {
            ProLog.e("QR", "encode failed: ${e.message}")
            null
        }
    }
}
