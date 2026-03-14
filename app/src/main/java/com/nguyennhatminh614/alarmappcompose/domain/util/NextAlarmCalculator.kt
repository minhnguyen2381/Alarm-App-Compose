package com.nguyennhatminh614.alarmappcompose.domain.util

import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import java.util.Calendar

object NextAlarmCalculator {

    fun calculateNextTriggerTime(alarm: Alarm): Long? {
        if (!alarm.isEnabled) return null

        val parts = alarm.time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alarm.repeatDays.isEmpty()) {
            if (!target.after(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            val calendarDays = alarm.repeatDays.map { it.toCalendarDay() }.toSet()
            for (daysAhead in 0..6) {
                val candidate = Calendar.getInstance().apply {
                    timeInMillis = target.timeInMillis
                    add(Calendar.DAY_OF_YEAR, daysAhead)
                }
                if (candidate.get(Calendar.DAY_OF_WEEK) in calendarDays) {
                    if (daysAhead > 0 || candidate.after(now)) {
                        target.add(Calendar.DAY_OF_YEAR, daysAhead)
                        return target.timeInMillis
                    }
                }
            }
            for (daysAhead in 1..7) {
                val candidate = Calendar.getInstance().apply {
                    timeInMillis = target.timeInMillis
                    add(Calendar.DAY_OF_YEAR, daysAhead)
                }
                if (candidate.get(Calendar.DAY_OF_WEEK) in calendarDays) {
                    target.add(Calendar.DAY_OF_YEAR, daysAhead)
                    return target.timeInMillis
                }
            }
        }
        return target.timeInMillis
    }

    fun findNearestAlarmTimeMillis(alarms: List<Alarm>): Long? {
        return alarms
            .filter { it.isEnabled }
            .mapNotNull { calculateNextTriggerTime(it) }
            .minOrNull()
    }

    fun formatTimeDifference(targetMillis: Long): String {
        val now = System.currentTimeMillis()
        val diffMillis = targetMillis - now
        if (diffMillis <= 0) return "0m"

        val totalMinutes = diffMillis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }

    private fun DayOfWeek.toCalendarDay(): Int {
        return when (this) {
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
        }
    }
}
