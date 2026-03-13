package com.nguyennhatminh614.alarmappcompose.ui.alarmRing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingAccent
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingOnBackground
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingOnSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimeDisplay(
    time: String,
    modifier: Modifier = Modifier,
) {
    val (displayTime, period) = remember(time) { formatTo12Hour(time) }
    val dateText = remember { formatCurrentDate() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = displayTime,
            fontSize = 88.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AlarmRingOnBackground,
            letterSpacing = 2.sp,
        )

        Text(
            text = period,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = AlarmRingAccent,
            letterSpacing = 4.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = dateText,
            fontSize = 16.sp,
            color = AlarmRingOnSurface,
        )
    }
}

private fun formatTo12Hour(time: String): Pair<String, String> {
    val parts = time.split(":")
    if (parts.size != 2) return Pair(time, "")

    val hour = parts[0].toIntOrNull() ?: return Pair(time, "")
    val minute = parts[1]

    val period = if (hour < 12) "A M" else "P M"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return Pair("%02d:%s".format(displayHour, minute), period)
}

private fun formatCurrentDate(): String {
    val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    return sdf.format(Date())
}
