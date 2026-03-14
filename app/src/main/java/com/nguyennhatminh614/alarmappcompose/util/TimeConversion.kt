package com.nguyennhatminh614.alarmappcompose.util

/**
 * Convert 24-hour value to 12-hour display.
 * @return Pair(displayHour 1-12, isAm)
 */
fun to12HourDisplay(hour24: Int): Pair<Int, Boolean> {
    val isAm = hour24 < 12
    val display = when (hour24) {
        0 -> 12
        in 1..12 -> hour24
        else -> hour24 - 12
    }
    return display to isAm
}

/**
 * Convert 12-hour display back to 24-hour value.
 * @param displayHour12 1-12
 * @param isAm true for AM, false for PM
 * @return 0-23
 */
fun to24Hour(displayHour12: Int, isAm: Boolean): Int {
    return when {
        displayHour12 == 12 && isAm -> 0
        displayHour12 == 12 && !isAm -> 12
        !isAm -> displayHour12 + 12
        else -> displayHour12
    }
}
