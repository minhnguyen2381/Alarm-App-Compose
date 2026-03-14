package com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms

data class AlarmUseCases(
    val getAlarms: GetAlarmsUseCase,
    val getAlarmById: GetAlarmByIdUseCase,
    val addAlarm: AddAlarmUseCase,
    val toggleAlarm: ToggleAlarmUseCase,
    val deleteAlarm: DeleteAlarmUseCase,
    val getNextAlarmTime: GetNextAlarmTimeUseCase
)
