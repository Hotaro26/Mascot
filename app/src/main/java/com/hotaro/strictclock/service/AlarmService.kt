package com.hotaro.strictclock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.hotaro.strictclock.MainActivity

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "mastco Alarms",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: -1
        val challengeType = intent?.getStringExtra("CHALLENGE_TYPE") ?: "None"
        val soundUriStr = intent?.getStringExtra("SOUND_URI") ?: ""
        val vibrationEnabled = intent?.getBooleanExtra("VIBRATION_ENABLED", true) ?: true
        val qrCodeData = intent?.getStringExtra("QR_CODE_DATA") ?: ""
        val qrCodeName = intent?.getStringExtra("QR_CODE_NAME") ?: ""
        val cameraObject = intent?.getStringExtra("CAMERA_OBJECT") ?: ""

        // Full screen intent to launch WakeUp UI
        val fullScreenIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("IS_WAKE_UP", true)
            putExtra("CHALLENGE_TYPE", challengeType)
            putExtra("ALARM_ID", alarmId)
            putExtra("SOUND_URI", soundUriStr)
            putExtra("VIBRATION_ENABLED", vibrationEnabled)
            putExtra("QR_CODE_DATA", qrCodeData)
            putExtra("QR_CODE_NAME", qrCodeName)
            putExtra("CAMERA_OBJECT", cameraObject)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarmId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle("mastco Alarm")
            .setContentText("Wake up! Challenge: $challengeType")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)

        playRingtone(soundUriStr)
        if (vibrationEnabled) {
            startVibration()
        }

        return START_STICKY
    }

    private fun playRingtone(soundUriStr: String) {
        try {
            val uri = if (soundUriStr.isNotEmpty() && soundUriStr != "Silent") {
                android.net.Uri.parse(soundUriStr)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
            if (soundUriStr == "Silent") return
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmService, uri)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val attrs = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                    setAudioAttributes(attrs)
                } else {
                    @Suppress("DEPRECATION")
                    setAudioStreamType(android.media.AudioManager.STREAM_ALARM)
                }
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        val pattern = longArrayOf(0, 1000, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        vibrator?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
