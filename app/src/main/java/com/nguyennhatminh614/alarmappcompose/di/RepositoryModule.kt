package com.nguyennhatminh614.alarmappcompose.di

import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import com.nguyennhatminh614.alarmappcompose.domain.repository.SoundRepository
import com.nguyennhatminh614.alarmappcompose.repository.AlarmRepositoryImpl
import com.nguyennhatminh614.alarmappcompose.repository.SoundRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAlarmRepository(
        alarmRepositoryImpl: AlarmRepositoryImpl
    ): AlarmRepository

    @Binds
    @Singleton
    abstract fun bindSoundRepository(
        soundRepositoryImpl: SoundRepositoryImpl
    ): SoundRepository
}
