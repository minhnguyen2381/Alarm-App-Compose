package com.nguyennhatminh614.alarmappcompose.ui.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.repository.DataStoreRepository
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val useCases: AlarmUseCases,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    companion object {
        private const val MAX_IN_APP_DENY_COUNT = 2
    }

    val alarms: StateFlow<ImmutableList<Alarm>> = useCases.getAlarms()
        .map { it.toImmutableList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )

    val nextAlarmText: StateFlow<String> = useCases.getNextAlarmTime()
        .map { it ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _canScheduleExactAlarms = MutableStateFlow(true)
    val canScheduleExactAlarms: StateFlow<Boolean> = _canScheduleExactAlarms.asStateFlow()

    private val _canDrawOverlays = MutableStateFlow(true)
    val canDrawOverlays: StateFlow<Boolean> = _canDrawOverlays.asStateFlow()

    val exceedMaxDenyCount: StateFlow<Boolean> = dataStoreRepository.getAlarmPermissionDenyCount()
        .map { it >= MAX_IN_APP_DENY_COUNT }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun updateCanScheduleExactAlarms(canSchedule: Boolean) {
        _canScheduleExactAlarms.value = canSchedule
    }

    fun updateCanDrawOverlays(canDraw: Boolean) {
        _canDrawOverlays.value = canDraw
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            dataStoreRepository.incrementAlarmPermissionDenyCount()
        }
    }

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
}
