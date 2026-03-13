package com.nguyennhatminh614.alarmappcompose.domain.usecase.sounds

data class SoundUseCases(
    val getPresetSounds: GetPresetSoundsUseCase,
    val getSystemRingtones: GetSystemRingtonesUseCase,
    val getDeviceAudioFiles: GetDeviceAudioFilesUseCase,
    val searchSounds: SearchSoundsUseCase
)
