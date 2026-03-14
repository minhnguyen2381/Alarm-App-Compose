package com.nguyennhatminh614.alarmappcompose.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    fun getAlarmPermissionDenyCount(): Flow<Int>
    suspend fun incrementAlarmPermissionDenyCount()
}
