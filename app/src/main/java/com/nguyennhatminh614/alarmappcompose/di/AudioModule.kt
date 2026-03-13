package com.nguyennhatminh614.alarmappcompose.di

import android.content.Context
import com.nguyennhatminh614.alarmappcompose.service.AudioPreviewManager
import com.nguyennhatminh614.alarmappcompose.service.AudioPreviewManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    @Singleton
    fun provideAudioPreviewManager(
        @ApplicationContext context: Context
    ): AudioPreviewManager {
        return AudioPreviewManagerImpl(context)
    }
}
