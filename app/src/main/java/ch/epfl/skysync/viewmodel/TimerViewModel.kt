package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.components.formatTime
import ch.epfl.skysync.util.WhileUiSubscribed
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TimerViewModel: ViewModel(){
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var job: Job? = null
    private var lastTimestamp = 0L
    private val _counter = MutableStateFlow(0L)
    val counter = _counter.map {
        formatTime(it)
    }.stateIn(viewModelScope, started=WhileUiSubscribed, initialValue = formatTime(0) )

    companion object {
        @Composable
        fun createViewModel(): TimerViewModel {
            return viewModel<TimerViewModel>(
                factory =
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TimerViewModel() as T
                    }
                })
        }
    }

    fun start() {
        if (_isRunning.value) return
        _counter.value = 0L
        _isRunning.value = true
        var newTimeStamp = 0L
        job = viewModelScope.launch {
            lastTimestamp = System.currentTimeMillis()
            while (_isRunning.value){
                delay(100)
                newTimeStamp = System.currentTimeMillis()
                _counter.value += newTimeStamp - lastTimestamp
                lastTimestamp = newTimeStamp
            }
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val secondsRounded = milliseconds / 1000
        val hours = secondsRounded / 3600
        val minutes = (secondsRounded % 3600) / 60
        val remainingSeconds = secondsRounded % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    fun stop() {
        _isRunning.value = false
        job?.cancel()
        job = null
        lastTimestamp = 0L
    }



    override fun onCleared() {
        super.onCleared()
        stop()
    }

}