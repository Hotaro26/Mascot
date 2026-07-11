package com.hotaro.strictclock.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE") ?: "None"
        val soundUri = intent.getStringExtra("SOUND_URI") ?: ""
        val vibrationEnabled = intent.getBooleanExtra("VIBRATION_ENABLED", true)
        val qrCodeData = intent.getStringExtra("QR_CODE_DATA") ?: ""
        val qrCodeName = intent.getStringExtra("QR_CODE_NAME") ?: ""
        val cameraObject = intent.getStringExtra("CAMERA_OBJECT") ?: ""

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_ID", alarmId)
            putExtra("CHALLENGE_TYPE", challengeType)
            putExtra("SOUND_URI", soundUri)
            putExtra("VIBRATION_ENABLED", vibrationEnabled)
            putExtra("QR_CODE_DATA", qrCodeData)
            putExtra("QR_CODE_NAME", qrCodeName)
            putExtra("CAMERA_OBJECT", cameraObject)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
