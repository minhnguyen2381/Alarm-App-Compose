package com.nguyennhatminh614.alarmappcompose.domain.repository

import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAlarms(): Flow<List<Alarm>>
    suspend fun getAlarmById(id: String): Alarm?
    suspend fun insertAlarm(alarm: Alarm)
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)
}
