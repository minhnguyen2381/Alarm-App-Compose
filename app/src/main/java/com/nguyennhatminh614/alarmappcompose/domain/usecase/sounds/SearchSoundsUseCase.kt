package com.nguyennhatminh614.alarmappcompose.domain.usecase.sounds

import com.nguyennhatminh614.alarmappcompose.domain.model.Sound
import javax.inject.Inject

class SearchSoundsUseCase @Inject constructor() {
    operator fun invoke(sounds: List<Sound>, query: String): List<Sound> {
        if (query.isBlank()) return sounds
        return sounds.filter { it.title.contains(query, ignoreCase = true) }
    }
}
