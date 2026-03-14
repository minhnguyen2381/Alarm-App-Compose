package com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms

import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import com.nguyennhatminh614.alarmappcompose.domain.util.NextAlarmCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNextAlarmTimeUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    operator fun invoke(): Flow<String?> {
        return repository.getAlarms().map { alarms ->
            val nearestMillis = NextAlarmCalculator.findNearestAlarmTimeMillis(alarms)
            nearestMillis?.let { NextAlarmCalculator.formatTimeDifference(it) }
        }
    }
}
