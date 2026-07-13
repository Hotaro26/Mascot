package com.hotaro.strictclock.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.hotaro.strictclock.MainActivity
import kotlinx.coroutines.*

class StopwatchService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var notificationManager: NotificationManager? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESET = "ACTION_RESET"
        const val ACTION_LAP = "ACTION_LAP"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "stopwatch_channel",
                "Stopwatch",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startStopwatch()
            ACTION_PAUSE -> pauseStopwatch()
            ACTION_RESET -> resetStopwatch()
            ACTION_LAP -> lapStopwatch()
        }
        return START_NOT_STICKY
    }

    private fun startStopwatch() {
        if (StopwatchManager.isRunning.value) return
        StopwatchManager.setRunning(true)

        val startTime = System.currentTimeMillis() - StopwatchManager.elapsedMillis.value
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        
        startForeground(1003, buildNotification(StopwatchManager.elapsedMillis.value, pendingIntent))
        
        scope.launch {
            while (StopwatchManager.isRunning.value) {
                val now = System.currentTimeMillis()
                val elapsed = now - startTime
                StopwatchManager.updateElapsed(elapsed)
                delay(10) // UI needs 10ms updates for centiseconds
            }
        }
    }

    private fun pauseStopwatch() {
        StopwatchManager.setRunning(false)
        scope.coroutineContext.cancelChildren()
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        notificationManager?.notify(1003, buildNotification(StopwatchManager.elapsedMillis.value, pendingIntent))
    }

    private fun resetStopwatch() {
        StopwatchManager.setRunning(false)
        StopwatchManager.reset()
        scope.coroutineContext.cancelChildren()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun lapStopwatch() {
        if (StopwatchManager.isRunning.value) {
            StopwatchManager.addLap(StopwatchManager.elapsedMillis.value)
        }
    }

    private fun buildNotification(elapsedMs: Long, pendingIntent: PendingIntent): android.app.Notification {
        val builder = NotificationCompat.Builder(this, "stopwatch_channel")
            .setContentTitle("Stopwatch")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        val stopIntent = Intent(this, StopwatchService::class.java).setAction(ACTION_RESET)
        val stopPending = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        if (StopwatchManager.isRunning.value) {
            builder.setUsesChronometer(true)
            builder.setWhen(System.currentTimeMillis() - elapsedMs)
            builder.setShowWhen(true)
            
            val pauseIntent = Intent(this, StopwatchService::class.java).setAction(ACTION_PAUSE)
            val pausePending = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            
            val lapIntent = Intent(this, StopwatchService::class.java).setAction(ACTION_LAP)
            val lapPending = PendingIntent.getService(this, 2, lapIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            
            builder.addAction(android.R.drawable.ic_media_pause, "Pause", pausePending)
            builder.addAction(android.R.drawable.ic_menu_add, "Lap", lapPending)
        } else {
            builder.setUsesChronometer(false)
            val seconds = (elapsedMs / 1000) % 60
            val minutes = (elapsedMs / (1000 * 60)) % 60
            val hours = (elapsedMs / (1000 * 60 * 60))
            val timeStr = if (hours > 0) String.format("%02d:%02d:%02d", hours, minutes, seconds) else String.format("%02d:%02d", minutes, seconds)
            builder.setContentText(timeStr)
            
            val resumeIntent = Intent(this, StopwatchService::class.java).setAction(ACTION_START)
            val resumePending = PendingIntent.getService(this, 3, resumeIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            
            builder.addAction(android.R.drawable.ic_media_play, "Resume", resumePending)
        }
        
        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Reset", stopPending)

        return builder.build()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
