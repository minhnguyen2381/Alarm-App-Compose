package com.nguyennhatminh614.alarmappcompose.ui.alarms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val useCases: AlarmUseCases
) : ViewModel() {

    // uiState represents the list of Alarms
    val alarms: StateFlow<ImmutableList<Alarm>> = useCases.getAlarms().map { it.toImmutableList() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = persistentListOf()
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

}
