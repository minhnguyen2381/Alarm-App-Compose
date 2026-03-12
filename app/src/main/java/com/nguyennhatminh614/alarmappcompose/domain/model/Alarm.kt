package com.nguyennhatminh614.alarmappcompose.domain.model

data class Alarm(
    val id: String,
    val time: String, // Format "HH:mm"
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: List<DayOfWeek>,
    val soundUri: String? = null,
    val isFadeInSound: Boolean = false,
    val vibrationPattern: VibrationPattern = VibrationPattern.DEFAULT,
    val wakeUpMission: WakeUpMission = WakeUpMission.NONE
)

enum class VibrationPattern(val displayName: String) {
    DEFAULT("Default"),
    HEARTBEAT("Heartbeat"),
    TICKTOCK("Ticktock"),
    WALTZ("Waltz"),
    ZIGZAG("Zigzag")
}

enum class WakeUpMission(val displayName: String) {
    NONE("None"),
    MATH("Math"),
    SHAKE("Shake"),
    QR_CODE("QR Code")
}

enum class DayOfWeek(val shortName: String) {
    MONDAY("M"),
    TUESDAY("T"),
    WEDNESDAY("W"),
    THURSDAY("T"),
    FRIDAY("F"),
    SATURDAY("S"),
    SUNDAY("S")
}
