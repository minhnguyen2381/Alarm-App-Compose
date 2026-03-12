package com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms

import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import javax.inject.Inject

class ToggleAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(alarm: Alarm, isEnabled: Boolean) {
        repository.updateAlarm(alarm.copy(isEnabled = isEnabled))
    }
}
