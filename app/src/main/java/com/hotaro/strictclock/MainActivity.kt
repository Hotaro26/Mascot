package com.hotaro.strictclock

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.hotaro.strictclock.ui.theme.StrictClockTheme
import androidx.compose.ui.Modifier
import com.hotaro.strictclock.ui.StrictClockApp

import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = android.graphics.Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        val isWakeUp = intent.getBooleanExtra("IS_WAKE_UP", false)
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE") ?: "None"
        val qrCodeData = intent.getStringExtra("QR_CODE_DATA") ?: ""
        val qrCodeName = intent.getStringExtra("QR_CODE_NAME") ?: ""
        val cameraObject = intent.getStringExtra("CAMERA_OBJECT") ?: ""

        if (isWakeUp) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            } else {
                @Suppress("DEPRECATION")
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                )
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setContent {
            StrictClockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StrictClockApp(isWakeUp = isWakeUp, challengeType = challengeType, qrCodeData = qrCodeData, qrCodeName = qrCodeName, cameraObject = cameraObject)
                }
            }
        }
    }
}
