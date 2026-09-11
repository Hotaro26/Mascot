package com.hotaro.mascot.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AntiPowerOffService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (AlarmService.isRinging) {
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val className = event.className?.toString() ?: ""
                val packageName = event.packageName?.toString() ?: ""
                Log.d("AntiPowerOffService", "Window state changed: class=$className package=$packageName")
                
                // If it's a System UI dialog (likely the power menu), dismiss it
                if (className.contains("Dialog") || packageName == "com.android.systemui") {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    Log.d("AntiPowerOffService", "Blocked System UI dialog during alarm.")
                }
            }
        }
    }

    override fun onInterrupt() {
        // Not used
    }
}
