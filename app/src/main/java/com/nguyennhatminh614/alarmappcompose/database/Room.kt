package com.nguyennhatminh614.alarmappcompose.database

import androidx.room.*
import com.nguyennhatminh614.alarmappcompose.database.dao.AlarmsDao
import com.nguyennhatminh614.alarmappcompose.database.dao.UsersDao
import com.nguyennhatminh614.alarmappcompose.database.entity.AlarmEntity
import com.nguyennhatminh614.alarmappcompose.database.entity.DetailsEntity
import com.nguyennhatminh614.alarmappcompose.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [UserEntity::class, DetailsEntity::class, AlarmEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract val usersDao: UsersDao
    abstract val alarmsDao: AlarmsDao
}