package com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms

import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import com.nguyennhatminh614.alarmappcompose.domain.scheduler.AlarmScheduler
import javax.inject.Inject

class AddAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm) {
        repository.insertAlarm(alarm)
        if (alarm.isEnabled) {
            alarmScheduler.schedule(alarm)
        }
    }
}
