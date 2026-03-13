package com.nguyennhatminh614.alarmappcompose.di

import android.content.Context
import androidx.room.Room
import com.nguyennhatminh614.alarmappcompose.database.AppDatabase
import com.nguyennhatminh614.alarmappcompose.database.dao.AlarmsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "AlarmAppCompose"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAlarmsDao(appDatabase: AppDatabase): AlarmsDao {
        return appDatabase.alarmsDao
    }
}