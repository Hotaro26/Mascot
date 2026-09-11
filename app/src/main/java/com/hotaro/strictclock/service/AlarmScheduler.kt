package com.hotaro.strictclock.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.hotaro.strictclock.data.AlarmEntity
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: AlarmEntity) {
        if (!alarm.isActive) {
            cancel(alarm)
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("CHALLENGE_TYPE", alarm.challengeType)
            putExtra("SOUND_URI", alarm.soundUri)
            putExtra("VIBRATION_ENABLED", alarm.vibrationEnabled)
            putExtra("QR_CODE_DATA", alarm.qrCodeData)
            putExtra("QR_CODE_NAME", alarm.qrCodeName)
            putExtra("CAMERA_OBJECT", alarm.cameraObject)
            putExtra("MATH_OPERATIONS", alarm.mathOperations)
            putExtra("MATH_DIFFICULTY", alarm.mathDifficulty)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate next trigger time using utils
        val triggerTime = com.hotaro.strictclock.utils.AlarmUtils.getNextTriggerTime(alarm)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            triggerTime,
            pendingIntent
        )

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun cancel(alarm: AlarmEntity) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
