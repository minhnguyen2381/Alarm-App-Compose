package com.nguyennhatminh614.alarmappcompose.di

import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import com.nguyennhatminh614.alarmappcompose.domain.scheduler.AlarmScheduler
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AddAlarmUseCase
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.DeleteAlarmUseCase
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.GetAlarmByIdUseCase
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.GetAlarmsUseCase
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.ToggleAlarmUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UseCasesModule {

    @Provides
    @Singleton
    fun provideAlarmUseCases(
        repository: AlarmRepository,
        alarmScheduler: AlarmScheduler
    ): AlarmUseCases {
        return AlarmUseCases(
            getAlarms = GetAlarmsUseCase(repository),
            getAlarmById = GetAlarmByIdUseCase(repository),
            addAlarm = AddAlarmUseCase(repository, alarmScheduler),
            toggleAlarm = ToggleAlarmUseCase(repository, alarmScheduler),
            deleteAlarm = DeleteAlarmUseCase(repository, alarmScheduler)
        )
    }
}
