package com.nguyennhatminh614.alarmappcompose.repository

import com.nguyennhatminh614.alarmappcompose.database.AlarmsDao
import com.nguyennhatminh614.alarmappcompose.database.toDomainModel
import com.nguyennhatminh614.alarmappcompose.database.toEntity
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmsDao: AlarmsDao
) : AlarmRepository {

    override fun getAlarms(): Flow<List<Alarm>> {
        return alarmsDao.getAlarms().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        alarmsDao.insertAlarm(alarm.toEntity())
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmsDao.updateAlarm(alarm.toEntity())
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmsDao.deleteAlarm(alarm.toEntity())
    }
}
