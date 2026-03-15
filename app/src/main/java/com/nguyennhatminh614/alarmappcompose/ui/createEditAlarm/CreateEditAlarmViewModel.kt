package com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.domain.model.VibrationPattern
import com.nguyennhatminh614.alarmappcompose.domain.model.WakeUpMission
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class CreateEditAlarmUiState(
    val initialAlarmId: String? = null,
    val time: String = "07:30",
    val label: String = "Wake up for work",
    val repeatDays: ImmutableList<DayOfWeek> = persistentListOf(),
    val soundUri: String? = null,
    val soundName: String? = null,
    val isFadeInSound: Boolean = false,
    val vibrationPattern: VibrationPattern = VibrationPattern.HEARTBEAT,
    val wakeUpMission: WakeUpMission = WakeUpMission.MATH,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface CreateEditAlarmEvent {
    data class TimeChanged(val time: String) : CreateEditAlarmEvent
    data class LabelChanged(val label: String) : CreateEditAlarmEvent
    data class RepeatDayToggled(val day: DayOfWeek) : CreateEditAlarmEvent
    data class SoundChanged(val uri: String?, val name: String?) : CreateEditAlarmEvent
    data class FadeInSoundToggled(val fade: Boolean) : CreateEditAlarmEvent
    data class VibrationPatternChanged(val pattern: VibrationPattern) : CreateEditAlarmEvent
    data class WakeUpMissionChanged(val mission: WakeUpMission) : CreateEditAlarmEvent
    data object SaveAlarm : CreateEditAlarmEvent
    data object DeleteAlarm : CreateEditAlarmEvent
}

@HiltViewModel
class CreateEditAlarmViewModel @Inject constructor(
    private val alarmUseCases: AlarmUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val alarmId: String? = savedStateHandle["alarmId"]

    private val _uiState = MutableStateFlow(
        CreateEditAlarmUiState(
            isLoading = alarmId != null,
            isEditing = alarmId != null
        )
    )
    val uiState: StateFlow<CreateEditAlarmUiState> = _uiState.asStateFlow()

    init {
        if (alarmId != null) {
            loadAlarm(alarmId)
        }
    }

    private fun loadAlarm(alarmId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val alarm = alarmUseCases.getAlarmById(alarmId)
            _uiState.update {
                if (alarm != null) {
                    it.copy(
                        initialAlarmId = alarm.id,
                        time = alarm.time,
                        label = alarm.label,
                        repeatDays = alarm.repeatDays,
                        soundUri = alarm.soundUri,
                        soundName = alarm.soundName,
                        isFadeInSound = alarm.isFadeInSound,
                        vibrationPattern = alarm.vibrationPattern,
                        wakeUpMission = alarm.wakeUpMission,
                        isEditing = true,
                        isLoading = false
                    )
                } else {
                    it.copy(isLoading = false, isEditing = false)
                }
            }
        }
    }

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
                    state.copy(repeatDays = currentDays.toImmutableList())
                }
            }
            is CreateEditAlarmEvent.SoundChanged -> {
                _uiState.update { it.copy(soundUri = event.uri, soundName = event.name) }
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
            soundName = state.soundName,
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
                    soundName = state.soundName,
                    isFadeInSound = state.isFadeInSound,
                    vibrationPattern = state.vibrationPattern,
                    wakeUpMission = state.wakeUpMission
                )
                alarmUseCases.deleteAlarm(alarm)
            }
        }
    }
}
