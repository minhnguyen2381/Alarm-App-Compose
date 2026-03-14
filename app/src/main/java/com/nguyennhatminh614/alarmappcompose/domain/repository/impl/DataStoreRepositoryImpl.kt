package com.nguyennhatminh614.alarmappcompose.domain.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.nguyennhatminh614.alarmappcompose.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    private companion object {
        val ALARM_PERMISSION_DENY_COUNT = intPreferencesKey("alarm_permission_deny_count")
    }

    override fun getAlarmPermissionDenyCount(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[ALARM_PERMISSION_DENY_COUNT] ?: 0
        }
    }

    override suspend fun incrementAlarmPermissionDenyCount() {
        dataStore.edit { preferences ->
            val current = preferences[ALARM_PERMISSION_DENY_COUNT] ?: 0
            preferences[ALARM_PERMISSION_DENY_COUNT] = current + 1
        }
    }
}
