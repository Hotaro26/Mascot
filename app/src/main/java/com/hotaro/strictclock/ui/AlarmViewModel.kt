package com.hotaro.strictclock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hotaro.strictclock.data.AlarmEntity
import com.hotaro.strictclock.data.AlarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.first

import com.hotaro.strictclock.service.AlarmScheduler

class AlarmViewModel(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {
    val allAlarms: StateFlow<List<AlarmEntity>> = repository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    fun insert(alarm: AlarmEntity) = viewModelScope.launch {
        val id = repository.insert(alarm)
        scheduler.schedule(alarm.copy(id = id.toInt()))
    }

    fun update(alarm: AlarmEntity) = viewModelScope.launch {
        repository.update(alarm)
        scheduler.schedule(alarm)
    }

    fun delete(alarm: AlarmEntity) = viewModelScope.launch {
        repository.delete(alarm)
        scheduler.cancel(alarm)
    }
}

class AlarmViewModelFactory(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository, scheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
