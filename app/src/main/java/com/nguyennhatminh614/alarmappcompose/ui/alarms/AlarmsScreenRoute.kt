package com.nguyennhatminh614.alarmappcompose.ui.alarms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AlarmsScreenRoute(
    onNavigateToCreateAlarm: () -> Unit,
    viewModel: AlarmsViewModel = hiltViewModel(),
) {
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()

    AlarmsScreen(
        alarms = alarms,
        onToggleAlarm = viewModel::onToggleAlarm,
        onAddAlarmClick = onNavigateToCreateAlarm
    )
}
