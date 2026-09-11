package com.hotaro.mascot

import android.app.Application
import com.hotaro.mascot.data.AlarmRepository
import com.hotaro.mascot.data.AppDatabase
import com.hotaro.mascot.service.AlarmScheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import com.hotaro.mascot.data.UserPreferencesRepository
import com.hotaro.mascot.ui.theme.ThemeManager

class StrictClockApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AlarmRepository(database.alarmDao()) }
    val scheduler by lazy { AlarmScheduler(this) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        ThemeManager.initialize(userPreferencesRepository, applicationScope)
    }
}
