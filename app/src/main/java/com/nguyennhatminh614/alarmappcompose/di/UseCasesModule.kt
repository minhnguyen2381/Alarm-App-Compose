package com.nguyennhatminh614.alarmappcompose.di

import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AddAlarmUseCase
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.DeleteAlarmUseCase
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
    fun provideAlarmUseCases(repository: AlarmRepository): AlarmUseCases {
        return AlarmUseCases(
            getAlarms = GetAlarmsUseCase(repository),
            addAlarm = AddAlarmUseCase(repository),
            toggleAlarm = ToggleAlarmUseCase(repository),
            deleteAlarm = DeleteAlarmUseCase(repository)
        )
    }
}
