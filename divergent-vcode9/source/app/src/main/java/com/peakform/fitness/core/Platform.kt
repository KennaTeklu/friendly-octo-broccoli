package com.peakform.fitness.core

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.peakform.fitness.MainActivity
import com.peakform.fitness.ProLog
import com.peakform.fitness.R
import com.peakform.fitness.engine.Stats
import com.peakform.fitness.ui.screens.RestTimer

/** BootReceiver — reschedule the notification check after reboot. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Notify.ensureChannels(context)
            Notify.scheduleDaily(context)
            ProLog.i("BOOT", "notification schedule restored")
        }
    }
}

/** ProTileService — quick-settings tile that starts/pauses the rest timer. */
class ProTileService : android.service.quicksettings.TileService() {
    override fun onStartListening() {
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (RestTimer.running) {
            RestTimer.stop()
        } else {
            RestTimer.start(90)
        }
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        if (RestTimer.running) {
            tile.label = "Rest: ${RestTimer.secondsLeft}s"
            tile.state = android.service.quicksettings.Tile.STATE_ACTIVE
        } else {
            tile.label = "Pro rest timer"
            tile.state = android.service.quicksettings.Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }
}

/** ProWidgetProvider — home-screen widget: streak, readiness, next action. */
class ProWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        for (id in ids) manager.updateAppWidget(id, buildViews(context))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ProWidgetProvider::class.java))
            for (id in ids) manager.updateAppWidget(id, buildViews(context))
        }
    }

    private fun buildViews(context: Context): RemoteViews {
        ProState.init(context)
        val streak = try { Stats.calculateStreak() } catch (_: Exception) { 0 }
        val readiness = try { Stats.overallRecoveryPct() } catch (_: Exception) { 0 }
        val draft = ProState.currentWorkout
        val line = when {
            draft != null -> "Draft ready — ${draft.exercises.count { !it.isLogged }} to log"
            ProState.data.workouts.isEmpty() -> "Welcome — generate your first workout"
            else -> "Next: generate today's session"
        }
        val views = RemoteViews(context.packageName, R.layout.pro_widget)
        views.setTextViewText(R.id.widget_streak, "🔥 $streak")
        views.setTextViewText(R.id.widget_readiness, "$readiness% ready")
        views.setTextViewText(R.id.widget_line, line)
        val open = android.app.PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_root, open)
        return views
    }

    companion object {
        const val ACTION_REFRESH = "com.peakform.fitness.WIDGET_REFRESH"

        fun refresh(context: Context) {
            context.sendBroadcast(Intent(context, ProWidgetProvider::class.java).apply { action = ACTION_REFRESH })
        }
    }
}
