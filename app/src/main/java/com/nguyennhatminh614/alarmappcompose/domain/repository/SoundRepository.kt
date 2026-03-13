package com.nguyennhatminh614.alarmappcompose.domain.repository

import com.nguyennhatminh614.alarmappcompose.domain.model.Sound
import kotlinx.coroutines.flow.Flow

interface SoundRepository {
    fun getPresetSounds(): Flow<List<Sound>>
    fun getSystemRingtones(): Flow<List<Sound>>
    fun getDeviceAudioFiles(): Flow<List<Sound>>
}
