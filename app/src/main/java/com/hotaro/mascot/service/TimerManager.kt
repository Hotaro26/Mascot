package com.hotaro.mascot.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TimerManager {
    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining

    private val _maxTime = MutableStateFlow(1L)
    val maxTime: StateFlow<Long> = _maxTime

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun updateTime(remaining: Long) {
        _timeRemaining.value = remaining
    }

    fun setMaxTime(max: Long) {
        _maxTime.value = max
    }

    fun setRunning(running: Boolean) {
        _isRunning.value = running
    }
}
