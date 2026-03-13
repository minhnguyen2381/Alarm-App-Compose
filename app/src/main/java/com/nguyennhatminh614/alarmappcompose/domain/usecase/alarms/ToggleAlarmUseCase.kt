package com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms

import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import com.nguyennhatminh614.alarmappcompose.domain.scheduler.AlarmScheduler
import javax.inject.Inject

class ToggleAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm, isEnabled: Boolean) {
        val updatedAlarm = alarm.copy(isEnabled = isEnabled)
        repository.updateAlarm(updatedAlarm)
        if (isEnabled) {
            alarmScheduler.schedule(updatedAlarm)
        } else {
            alarmScheduler.cancel(updatedAlarm)
        }
    }
}
