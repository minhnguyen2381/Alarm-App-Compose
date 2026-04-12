package com.nguyennhatminh614.alarmappcompose.ui.alarms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.util.checkCanDrawOverlays
import com.nguyennhatminh614.alarmappcompose.util.checkCanScheduleExactAlarms
import com.nguyennhatminh614.alarmappcompose.util.openExactAlarmSettings
import com.nguyennhatminh614.alarmappcompose.util.openOverlaySettings
import com.nguyennhatminh614.alarmappcompose.util.requestExactAlarmPermission

@Composable
fun AlarmsScreenRoute(
    onNavigateToCreateAlarm: () -> Unit,
    onNavigateToEditAlarm: (Alarm) -> Unit,
    viewModel: AlarmsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val nextAlarmText by viewModel.nextAlarmText.collectAsStateWithLifecycle()
    val canScheduleExactAlarms by viewModel.canScheduleExactAlarms.collectAsStateWithLifecycle()
    val exceedMaxDenyCount by viewModel.exceedMaxDenyCount.collectAsStateWithLifecycle()
    val canDrawOverlays by viewModel.canDrawOverlays.collectAsStateWithLifecycle()

    // Re-check permission every time the screen resumes (e.g. after returning from Settings)
    LifecycleResumeEffect(Unit) {
        viewModel.updateCanScheduleExactAlarms(context.checkCanScheduleExactAlarms())
        viewModel.updateCanDrawOverlays(context.checkCanDrawOverlays())
        onPauseOrDispose {}
    }

    AlarmsScreen(
        alarms = alarms,
        nextAlarmText = nextAlarmText,
        showPermissionBanner = !canScheduleExactAlarms,
        shouldOpenSettings = exceedMaxDenyCount,
        onGrantPermissionClick = {
            if (exceedMaxDenyCount) {
                context.openExactAlarmSettings()
            } else {
                context.requestExactAlarmPermission()
                viewModel.onPermissionDenied()
            }
        },
        showOverlayPermissionBanner = !canDrawOverlays,
        onGrantOverlayPermissionClick = { context.openOverlaySettings() },
        onToggleAlarm = viewModel::onToggleAlarm,
        onDeleteAlarm = viewModel::onDeleteAlarm,
        onAlarmClick = onNavigateToEditAlarm,
        onAddAlarmClick = onNavigateToCreateAlarm
    )
}