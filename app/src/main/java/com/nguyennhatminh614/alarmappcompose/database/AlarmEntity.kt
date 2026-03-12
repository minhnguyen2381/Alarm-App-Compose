package com.nguyennhatminh614.alarmappcompose.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.domain.model.VibrationPattern
import com.nguyennhatminh614.alarmappcompose.domain.model.WakeUpMission

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey val id: String,
    val time: String,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: String, // Lưu trữ dưới dạng chuỗi (ví dụ: "MONDAY,TUESDAY")
    val soundUri: String? = null,
    val isFadeInSound: Boolean = false,
    val vibrationPattern: String = "DEFAULT",
    val wakeUpMission: String = "NONE"
)

// Extension functions Mapper
fun AlarmEntity.toDomainModel(): Alarm {
    val days = if (repeatDays.isEmpty()) {
        emptyList()
    } else {
        repeatDays.split(",").map { DayOfWeek.valueOf(it) }
    }
    return Alarm(
        id = id,
        time = time,
        label = label,
        isEnabled = isEnabled,
        repeatDays = days,
        soundUri = soundUri,
        isFadeInSound = isFadeInSound,
        vibrationPattern = try {
            VibrationPattern.valueOf(vibrationPattern)
        } catch (e: Exception) {
            VibrationPattern.DEFAULT
        },
        wakeUpMission = try {
            WakeUpMission.valueOf(wakeUpMission)
        } catch (e: Exception) {
            WakeUpMission.NONE
        }
    )
}

fun Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        time = time,
        label = label,
        isEnabled = isEnabled,
        repeatDays = repeatDays.joinToString(",") { it.name },
        soundUri = soundUri,
        isFadeInSound = isFadeInSound,
        vibrationPattern = vibrationPattern.name,
        wakeUpMission = wakeUpMission.name
    )
}
