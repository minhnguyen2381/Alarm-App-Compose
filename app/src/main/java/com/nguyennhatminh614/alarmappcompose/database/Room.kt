package com.nguyennhatminh614.alarmappcompose.database

import androidx.room.*
import com.nguyennhatminh614.alarmappcompose.database.dao.AlarmsDao
import com.nguyennhatminh614.alarmappcompose.database.entity.AlarmEntity
import com.nguyennhatminh614.alarmappcompose.database.entity.DetailsEntity

@Database(
    entities = [DetailsEntity::class, AlarmEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val alarmsDao: AlarmsDao
}