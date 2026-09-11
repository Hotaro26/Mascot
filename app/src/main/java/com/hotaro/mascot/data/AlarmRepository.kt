package com.hotaro.mascot.data

import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {
    val allAlarms: Flow<List<AlarmEntity>> = alarmDao.getAllAlarms()

    suspend fun getAlarmById(id: Int): AlarmEntity? {
        return alarmDao.getAlarmById(id)
    }

    suspend fun insert(alarm: AlarmEntity): Long {
        return alarmDao.insertAlarm(alarm)
    }

    suspend fun update(alarm: AlarmEntity) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun delete(alarm: AlarmEntity) {
        alarmDao.deleteAlarm(alarm)
    }
}
