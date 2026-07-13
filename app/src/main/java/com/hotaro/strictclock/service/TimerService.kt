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

class TimerService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var notificationManager: NotificationManager? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_ADD_MINUTE = "ACTION_ADD_MINUTE"
        const val ACTION_RESET = "ACTION_RESET"
        const val EXTRA_DURATION_MS = "EXTRA_DURATION_MS"
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_channel_v2",
                "mascot Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMs = intent.getLongExtra(EXTRA_DURATION_MS, 0L)
                if (durationMs > 0) {
                    startTimer(durationMs)
                }
            }
            ACTION_STOP -> stopTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_ADD_MINUTE -> addMinute()
            ACTION_RESET -> resetTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationMs: Long) {
        TimerManager.setMaxTime(durationMs)
        TimerManager.updateTime(durationMs)
        TimerManager.setRunning(true)
        startCounting()
    }

    private fun pauseTimer() {
        TimerManager.setRunning(false)
        scope.coroutineContext.cancelChildren()
        
        // Update notification immediately on pause
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        notificationManager?.notify(1002, buildNotification(TimerManager.timeRemaining.value, TimerManager.maxTime.value, pendingIntent))
    }

    private fun resumeTimer() {
        if (TimerManager.timeRemaining.value > 0) {
            TimerManager.setRunning(true)
            startCounting()
        }
    }

    private fun addMinute() {
        val newTime = TimerManager.timeRemaining.value + 60000L
        TimerManager.updateTime(newTime)
        TimerManager.setMaxTime(TimerManager.maxTime.value + 60000L)
        // Ensure UI updates Notification
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        notificationManager?.notify(1002, buildNotification(newTime, TimerManager.maxTime.value, pendingIntent))
    }

    private fun resetTimer() {
        pauseTimer()
        TimerManager.updateTime(TimerManager.maxTime.value)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        notificationManager?.notify(1002, buildNotification(TimerManager.maxTime.value, TimerManager.maxTime.value, pendingIntent))
    }

    private fun startCounting() {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )

        startForeground(1002, buildNotification(TimerManager.timeRemaining.value, TimerManager.maxTime.value, pendingIntent))

        scope.launch {
            while (TimerManager.timeRemaining.value > 0 && TimerManager.isRunning.value) {
                delay(1000)
                val remaining = TimerManager.timeRemaining.value - 1000
                TimerManager.updateTime(remaining)
                
                notificationManager?.notify(1002, buildNotification(remaining, TimerManager.maxTime.value, pendingIntent))
            }
            if (TimerManager.timeRemaining.value <= 0) {
                TimerManager.setRunning(false)
                TimerManager.updateTime(0L)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun stopTimer() {
        TimerManager.setRunning(false)
        TimerManager.updateTime(0L)
        scope.coroutineContext.cancelChildren()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(remainingMs: Long, maxMs: Long, pendingIntent: PendingIntent): android.app.Notification {
        val seconds = (remainingMs / 1000) % 60
        val minutes = (remainingMs / (1000 * 60)) % 60
        val hours = (remainingMs / (1000 * 60 * 60))

        val timeStr = if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }

        val progress = if (maxMs > 0) (remainingMs.toFloat() / maxMs.toFloat() * 100).toInt() else 0

        val builder = NotificationCompat.Builder(this, "timer_channel_v2")
            .setContentTitle("Timer: $timeStr")
            .setContentText("Time remaining")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setProgress(100, progress, false)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        val stopIntent = Intent(this, TimerService::class.java).setAction(ACTION_STOP)
        val stopPending = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        
        if (TimerManager.isRunning.value) {
            val pauseIntent = Intent(this, TimerService::class.java).setAction(ACTION_PAUSE)
            val pausePending = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            
            val addIntent = Intent(this, TimerService::class.java).setAction(ACTION_ADD_MINUTE)
            val addPending = PendingIntent.getService(this, 2, addIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            
            builder.addAction(android.R.drawable.ic_media_pause, "Pause", pausePending)
            builder.addAction(android.R.drawable.ic_menu_add, "+1 Min", addPending)
        } else {
            val resumeIntent = Intent(this, TimerService::class.java).setAction(ACTION_RESUME)
            val resumePending = PendingIntent.getService(this, 3, resumeIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            
            val resetIntent = Intent(this, TimerService::class.java).setAction(ACTION_RESET)
            val resetPending = PendingIntent.getService(this, 4, resetIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            
            builder.addAction(android.R.drawable.ic_media_play, "Resume", resumePending)
            builder.addAction(android.R.drawable.ic_menu_revert, "Reset", resetPending)
        }
        
        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", stopPending)

        return builder.build()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
