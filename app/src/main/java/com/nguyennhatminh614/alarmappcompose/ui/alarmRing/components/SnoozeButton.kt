package com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmRingOnBackground
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmRingSnoozeButtonColor
import kotlinx.coroutines.delay

@Composable
fun SnoozeButton(
    snoozeDurationMinutes: Int,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var remainingSeconds by rememberSaveable {
        mutableIntStateOf(snoozeDurationMinutes * 60)
    }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val countdownText = "%d:%02d".format(minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AlarmRingSnoozeButtonColor)
            .clickable(onClick = onSnooze),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.alarm_ring_snooze_format, countdownText),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AlarmRingOnBackground,
        )
    }
}
