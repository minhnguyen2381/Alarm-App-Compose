package com.nguyennhatminh614.alarmappcompose.ui.alarms

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm

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
        viewModel.updateCanScheduleExactAlarms(checkCanScheduleExactAlarms(context))
        viewModel.updateCanDrawOverlays(checkCanDrawOverlays(context))
        onPauseOrDispose {}
    }

    AlarmsScreen(
        alarms = alarms,
        nextAlarmText = nextAlarmText,
        showPermissionBanner = !canScheduleExactAlarms,
        shouldOpenSettings = exceedMaxDenyCount,
        onGrantPermissionClick = {
            if (exceedMaxDenyCount) {
                openExactAlarmSettings(context)
            } else {
                requestExactAlarmPermission(context)
                viewModel.onPermissionDenied()
            }
        },
        showOverlayPermissionBanner = !canDrawOverlays,
        onGrantOverlayPermissionClick = { openOverlaySettings(context) },
        onToggleAlarm = viewModel::onToggleAlarm,
        onDeleteAlarm = viewModel::onDeleteAlarm,
        onAlarmClick = onNavigateToEditAlarm,
        onAddAlarmClick = onNavigateToCreateAlarm
    )
}

private fun checkCanScheduleExactAlarms(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return alarmManager.canScheduleExactAlarms()
}

private fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

private fun checkCanDrawOverlays(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

private fun openOverlaySettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
