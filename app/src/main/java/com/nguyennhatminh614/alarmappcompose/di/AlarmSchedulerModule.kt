package com.nguyennhatminh614.alarmappcompose.di

import android.content.Context
import com.nguyennhatminh614.alarmappcompose.domain.scheduler.AlarmScheduler
import com.nguyennhatminh614.alarmappcompose.service.AlarmSchedulerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmSchedulerModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmSchedulerImpl(context)
    }
}
