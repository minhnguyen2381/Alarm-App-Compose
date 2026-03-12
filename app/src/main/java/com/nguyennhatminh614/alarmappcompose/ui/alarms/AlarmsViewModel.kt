package com.nguyennhatminh614.alarmappcompose.ui.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val useCases: AlarmUseCases
) : ViewModel() {

    // uiState represents the list of Alarms
    val alarms: StateFlow<List<Alarm>> = useCases.getAlarms().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onToggleAlarm(alarm: Alarm, isEnabled: Boolean) {
        viewModelScope.launch {
            useCases.toggleAlarm(alarm, isEnabled)
        }
    }

    fun onDeleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            useCases.deleteAlarm(alarm)
        }
    }

    // Mock functionality for creating an alarm temporarily
    fun onAddMockAlarm() {
        viewModelScope.launch {
            val randomId = java.util.UUID.randomUUID().toString()
            val newAlarm = Alarm(
                id = randomId,
                time = "07:00",
                label = "New Alarm",
                isEnabled = true,
                repeatDays = emptyList()
            )
            useCases.addAlarm(newAlarm)
        }
    }
}
