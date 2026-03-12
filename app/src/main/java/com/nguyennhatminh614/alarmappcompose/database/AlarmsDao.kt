package com.nguyennhatminh614.alarmappcompose.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmsDao {

    @Query("SELECT * FROM alarms")
    fun getAlarms(): Flow<List<AlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)
}
