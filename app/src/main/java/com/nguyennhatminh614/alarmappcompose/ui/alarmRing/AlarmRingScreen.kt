package com.nguyennhatminh614.alarmappcompose.ui.alarmRing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components.AlarmActionButtons
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components.AlarmTimeDisplay
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import com.nguyennhatminh614.alarmappcompose.util.DevicePreview
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AlarmRingScreen(
    alarm: Alarm?,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AlarmTimeDisplay(
            time = alarm?.time ?: "--:--",
            label = alarm?.label ?: "",
        )

        Spacer(modifier = Modifier.height(64.dp))

        AlarmActionButtons(
            onSnooze = onSnooze,
            onDismiss = onDismiss,
        )
    }
}

// ======================== PREVIEWS ========================

@DevicePreview
@Composable
private fun AlarmRingScreenPreview() {
    AlarmAppComposeTheme {
        AlarmRingScreen(
            alarm = Alarm(
                id = "1",
                time = "07:00",
                label = "Wake Up",
                isEnabled = true,
                repeatDays = persistentListOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                ),
            ),
            onDismiss = {},
            onSnooze = {},
        )
    }
}

@DevicePreview
@Composable
private fun AlarmRingScreenNoAlarmPreview() {
    AlarmAppComposeTheme {
        AlarmRingScreen(
            alarm = null,
            onDismiss = {},
            onSnooze = {},
        )
    }
}
