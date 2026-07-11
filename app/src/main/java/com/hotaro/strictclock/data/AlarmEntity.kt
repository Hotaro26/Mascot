package com.hotaro.strictclock.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeHour: Int,
    val timeMinute: Int,
    val daysOfWeek: String,
    val isActive: Boolean,
    val challengeType: String,
    val soundName: String = "Default",
    val soundUri: String = "",
    val vibrationEnabled: Boolean = true,
    val qrCodeData: String = "",
    val qrCodeName: String = "",
    val cameraObject: String = "",
    val mathOperations: String = "Addition,Subtraction",
    val mathDifficulty: String = "Easy"
)
