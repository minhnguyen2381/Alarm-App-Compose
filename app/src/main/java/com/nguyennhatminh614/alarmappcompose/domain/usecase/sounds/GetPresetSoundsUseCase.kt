package com.nguyennhatminh614.alarmappcompose.domain.usecase.sounds

import com.nguyennhatminh614.alarmappcompose.domain.model.Sound
import com.nguyennhatminh614.alarmappcompose.domain.repository.SoundRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPresetSoundsUseCase @Inject constructor(
    private val repository: SoundRepository
) {
    operator fun invoke(): Flow<List<Sound>> = repository.getPresetSounds()
}
