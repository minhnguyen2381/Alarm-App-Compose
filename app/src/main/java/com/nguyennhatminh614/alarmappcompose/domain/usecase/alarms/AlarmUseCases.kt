package com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms

data class AlarmUseCases(
    val getAlarms: GetAlarmsUseCase,
    val addAlarm: AddAlarmUseCase,
    val toggleAlarm: ToggleAlarmUseCase,
    val deleteAlarm: DeleteAlarmUseCase
)
