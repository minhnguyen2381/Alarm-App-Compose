package com.nguyennhatminh614.alarmappcompose.ui.alarms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm

@Composable
fun AlarmsScreenRoute(
    onNavigateToCreateAlarm: () -> Unit,
    onNavigateToEditAlarm: (Alarm) -> Unit,
    viewModel: AlarmsViewModel = hiltViewModel(),
) {
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()

    AlarmsScreen(
        alarms = alarms,
        onToggleAlarm = viewModel::onToggleAlarm,
        onDeleteAlarm = viewModel::onDeleteAlarm,
        onAlarmClick = onNavigateToEditAlarm,
        onAddAlarmClick = onNavigateToCreateAlarm
    )
}
