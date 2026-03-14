package com.nguyennhatminh614.alarmappcompose.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.domain.scheduler.AlarmScheduler
import com.nguyennhatminh614.alarmappcompose.receiver.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val context: Context
) : AlarmScheduler {

    companion object {
        private const val TAG = "AlarmScheduler"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: Alarm) {
        val triggerTimeMillis = calculateNextTriggerTime(alarm)
        val pendingIntent = createPendingIntent(alarm)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val triggerDateStr = sdf.format(Date(triggerTimeMillis))
        Log.d(TAG, "schedule() alarm id=${alarm.id}, label='${alarm.label}', time=${alarm.time}")
        Log.d(TAG, "schedule() triggerTime=$triggerDateStr ($triggerTimeMillis)")
        Log.d(TAG, "schedule() SDK_INT=${Build.VERSION.SDK_INT}, repeatDays=${alarm.repeatDays}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canSchedule = alarmManager.canScheduleExactAlarms()
            Log.w(TAG, "schedule() canScheduleExactAlarms()=$canSchedule")
            if (canSchedule) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
                Log.d(TAG, "schedule() SUCCESS - setExactAndAllowWhileIdle scheduled")
            } else {
                Log.e(TAG, "schedule() FAILED - canScheduleExactAlarms=false! Alarm NOT scheduled!")
                Log.e(TAG, "schedule() User must grant SCHEDULE_EXACT_ALARM or app needs USE_EXACT_ALARM")
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
            Log.d(TAG, "schedule() SUCCESS - pre-S device, setExactAndAllowWhileIdle scheduled")
        }
    }

    override fun cancel(alarm: Alarm) {
        Log.d(TAG, "cancel() alarm id=${alarm.id}")
        val pendingIntent = createPendingIntent(alarm)
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(alarm: Alarm): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
        }
        val requestCode = alarm.id.hashCode()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun calculateNextTriggerTime(alarm: Alarm): Long {
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
            // One-shot alarm: if time already passed today, schedule for tomorrow
            if (!target.after(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            // Repeating alarm: find the next matching day
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
            // Fallback: schedule for next week's first matching day
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
