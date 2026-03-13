package com.nguyennhatminh614.alarmappcompose.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Sound(
    val uri: String,
    val title: String,
    val type: SoundType
)

enum class SoundType {
    PRESET,
    SYSTEM_RINGTONE,
    DEVICE_FILE
}
