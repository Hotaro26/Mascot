package com.hotaro.mascot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {

    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _laps = MutableStateFlow<List<Long>>(emptyList())
    val laps: StateFlow<List<Long>> = _laps.asStateFlow()

    private var timerJob: Job? = null
    private var startTime = 0L

    fun toggle() {
        if (_isRunning.value) {
            pause()
        } else {
            start()
        }
    }

    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true
        startTime = System.currentTimeMillis() - _elapsedMillis.value
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                _elapsedMillis.value = System.currentTimeMillis() - startTime
                delay(10) // Update every 10ms for smooth centiseconds
            }
        }
    }

    fun pause() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun reset() {
        pause()
        _elapsedMillis.value = 0L
        _laps.value = emptyList()
    }

    fun lap() {
        if (_isRunning.value) {
            _laps.value = listOf(_elapsedMillis.value) + _laps.value
        }
    }
}
