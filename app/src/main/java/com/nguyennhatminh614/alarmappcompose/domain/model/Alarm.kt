package com.nguyennhatminh614.alarmappcompose.domain.model

data class Alarm(
    val id: String,
    val time: String, // Format "HH:mm"
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: List<DayOfWeek>
)

enum class DayOfWeek(val shortName: String) {
    MONDAY("M"),
    TUESDAY("T"),
    WEDNESDAY("W"),
    THURSDAY("T"),
    FRIDAY("F"),
    SATURDAY("S"),
    SUNDAY("S")
}
