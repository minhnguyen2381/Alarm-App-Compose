package com.nguyennhatminh614.alarmappcompose.ui.alarmRing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.domain.model.WakeUpMission
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components.AlarmRingTopBar
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components.SnoozeButton
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components.SwipeToDismiss
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components.TimeDisplay
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components.WakeUpMissionCard
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import com.nguyennhatminh614.alarmappcompose.util.DevicePreview
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AlarmRingScreen(
    alarm: Alarm?,
    snoozeDurationMinutes: Int,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AlarmRingBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top bar
        AlarmRingTopBar(
            label = alarm?.label?.ifBlank {
                stringResource(R.string.alarm_ring_default_label)
            } ?: stringResource(R.string.alarm_ring_default_label),
            onMenuClick = { /* TODO */ },
        )

        // Push time toward upper-center
        Spacer(modifier = Modifier.weight(0.3f))

        // Time display
        TimeDisplay(
            time = alarm?.time ?: "--:--",
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Wake-up mission card (conditional)
        if (alarm != null && alarm.wakeUpMission != WakeUpMission.NONE) {
            var completedCount by remember { mutableIntStateOf(0) }
            val totalCount = getMissionTotalCount(alarm.wakeUpMission)
            WakeUpMissionCard(
                mission = alarm.wakeUpMission,
                completedCount = completedCount,
                totalCount = totalCount,
            )
        }

        // Push action area to bottom
        Spacer(modifier = Modifier.weight(1f))

        // Snooze button
        SnoozeButton(
            snoozeDurationMinutes = snoozeDurationMinutes,
            onSnooze = onSnooze,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Swipe to dismiss
        SwipeToDismiss(
            onDismissed = onDismiss,
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun getMissionTotalCount(mission: WakeUpMission): Int = when (mission) {
    WakeUpMission.MATH -> 3
    WakeUpMission.SHAKE -> 30
    WakeUpMission.QR_CODE -> 1
    WakeUpMission.NONE -> 0
}

// ======================== PREVIEWS ========================

@DevicePreview
@Composable
private fun AlarmRingScreenWithMissionPreview() {
    AlarmAppComposeTheme {
        AlarmRingScreen(
            alarm = Alarm(
                id = "1",
                time = "07:30",
                label = "Wake Up",
                isEnabled = true,
                repeatDays = persistentListOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                ),
                wakeUpMission = WakeUpMission.MATH,
            ),
            snoozeDurationMinutes = 9,
            onDismiss = {},
            onSnooze = {},
        )
    }
}

@DevicePreview
@Composable
private fun AlarmRingScreenNoMissionPreview() {
    AlarmAppComposeTheme {
        AlarmRingScreen(
            alarm = Alarm(
                id = "2",
                time = "14:00",
                label = "Gym",
                isEnabled = true,
                repeatDays = persistentListOf(),
            ),
            snoozeDurationMinutes = 5,
            onDismiss = {},
            onSnooze = {},
        )
    }
}
