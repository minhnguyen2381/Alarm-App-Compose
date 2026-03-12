package com.nguyennhatminh614.alarmappcompose.ui.create_edit_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.domain.model.VibrationPattern
import com.nguyennhatminh614.alarmappcompose.domain.model.WakeUpMission
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreateEditAlarmUiState(
    val initialAlarmId: String? = null,
    val time: String = "07:30", // Default from mock
    val label: String = "Wake up for work",
    val repeatDays: List<DayOfWeek> = emptyList(), // T, W, T, F is checked from mock 
    val soundUri: String? = "Morning Breeze (Gentle)", // Mocked title for now
    val isFadeInSound: Boolean = false,
    val vibrationPattern: VibrationPattern = VibrationPattern.HEARTBEAT,
    val wakeUpMission: WakeUpMission = WakeUpMission.MATH,
    val isEditing: Boolean = false
)

sealed interface CreateEditAlarmEvent {
    data class TimeChanged(val time: String) : CreateEditAlarmEvent
    data class LabelChanged(val label: String) : CreateEditAlarmEvent
    data class RepeatDayToggled(val day: DayOfWeek) : CreateEditAlarmEvent
    data class SoundUriChanged(val uri: String?) : CreateEditAlarmEvent
    data class FadeInSoundToggled(val fade: Boolean) : CreateEditAlarmEvent
    data class VibrationPatternChanged(val pattern: VibrationPattern) : CreateEditAlarmEvent
    data class WakeUpMissionChanged(val mission: WakeUpMission) : CreateEditAlarmEvent
    object SaveAlarm : CreateEditAlarmEvent
    object DeleteAlarm : CreateEditAlarmEvent
}

@HiltViewModel
class CreateEditAlarmViewModel @Inject constructor(
    private val alarmUseCases: AlarmUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEditAlarmUiState())
    val uiState: StateFlow<CreateEditAlarmUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreateEditAlarmEvent) {
        when (event) {
            is CreateEditAlarmEvent.TimeChanged -> {
                _uiState.update { it.copy(time = event.time) }
            }
            is CreateEditAlarmEvent.LabelChanged -> {
                _uiState.update { it.copy(label = event.label) }
            }
            is CreateEditAlarmEvent.RepeatDayToggled -> {
                _uiState.update { state ->
                    val currentDays = state.repeatDays.toMutableList()
                    if (currentDays.contains(event.day)) {
                        currentDays.remove(event.day)
                    } else {
                        currentDays.add(event.day)
                    }
                    state.copy(repeatDays = currentDays)
                }
            }
            is CreateEditAlarmEvent.SoundUriChanged -> {
                _uiState.update { it.copy(soundUri = event.uri) }
            }
            is CreateEditAlarmEvent.FadeInSoundToggled -> {
                _uiState.update { it.copy(isFadeInSound = event.fade) }
            }
            is CreateEditAlarmEvent.VibrationPatternChanged -> {
                _uiState.update { it.copy(vibrationPattern = event.pattern) }
            }
            is CreateEditAlarmEvent.WakeUpMissionChanged -> {
                _uiState.update { it.copy(wakeUpMission = event.mission) }
            }
            CreateEditAlarmEvent.SaveAlarm -> {
                saveAlarm()
            }
            CreateEditAlarmEvent.DeleteAlarm -> {
                deleteAlarm()
            }
        }
    }

    private fun saveAlarm() {
        val state = _uiState.value
        val alarmId = state.initialAlarmId ?: UUID.randomUUID().toString()
        val alarm = Alarm(
            id = alarmId,
            time = state.time,
            label = state.label,
            isEnabled = true,
            repeatDays = state.repeatDays,
            soundUri = state.soundUri,
            isFadeInSound = state.isFadeInSound,
            vibrationPattern = state.vibrationPattern,
            wakeUpMission = state.wakeUpMission
        )
        
        viewModelScope.launch(Dispatchers.IO) {
            alarmUseCases.addAlarm(alarm)
        }
    }

    private fun deleteAlarm() {
        val state = _uiState.value
        if (state.initialAlarmId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val alarm = Alarm(
                    id = state.initialAlarmId,
                    time = state.time,
                    label = state.label,
                    isEnabled = true,
                    repeatDays = state.repeatDays,
                    soundUri = state.soundUri,
                    isFadeInSound = state.isFadeInSound,
                    vibrationPattern = state.vibrationPattern,
                    wakeUpMission = state.wakeUpMission
                )
                alarmUseCases.deleteAlarm(alarm)
            }
        }
    }
}
