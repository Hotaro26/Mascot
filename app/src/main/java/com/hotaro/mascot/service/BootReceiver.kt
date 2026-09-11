package com.hotaro.mascot.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hotaro.mascot.StrictClockApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = (context.applicationContext as StrictClockApplication).repository
            val scheduler = AlarmScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                val alarms = repository.allAlarms.first()
                alarms.forEach { alarm ->
                    if (alarm.isActive) {
                        scheduler.schedule(alarm)
                    }
                }
            }
        }
    }
}
