package com.nguyennhatminh614.alarmappcompose.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey val id: String,
    val time: String,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: String // Lưu trữ dưới dạng chuỗi (ví dụ: "MONDAY,TUESDAY")
)

// Extension functions Mapper
fun AlarmEntity.toDomainModel(): com.nguyennhatminh614.alarmappcompose.domain.model.Alarm {
    val days = if (repeatDays.isEmpty()) {
        emptyList()
    } else {
        repeatDays.split(",").map { DayOfWeek.valueOf(it) }
    }
    return com.nguyennhatminh614.alarmappcompose.domain.model.Alarm(
        id = id,
        time = time,
        label = label,
        isEnabled = isEnabled,
        repeatDays = days
    )
}

fun com.nguyennhatminh614.alarmappcompose.domain.model.Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        time = time,
        label = label,
        isEnabled = isEnabled,
        repeatDays = repeatDays.joinToString(",") { it.name }
    )
}
