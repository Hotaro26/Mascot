package com.hotaro.strictclock.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object StopwatchManager {
    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _laps = MutableStateFlow<List<Long>>(emptyList())
    val laps: StateFlow<List<Long>> = _laps

    fun updateElapsed(millis: Long) {
        _elapsedMillis.value = millis
    }

    fun setRunning(running: Boolean) {
        _isRunning.value = running
    }

    fun setLaps(newLaps: List<Long>) {
        _laps.value = newLaps
    }

    fun addLap(lapTime: Long) {
        _laps.value = listOf(lapTime) + _laps.value
    }

    fun reset() {
        _elapsedMillis.value = 0L
        _laps.value = emptyList()
        _isRunning.value = false
    }
}
