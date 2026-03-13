package com.nguyennhatminh614.alarmappcompose.domain.scheduler

import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}
