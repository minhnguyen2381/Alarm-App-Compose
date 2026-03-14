package com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import com.nguyennhatminh614.alarmappcompose.util.to12HourDisplay
import com.nguyennhatminh614.alarmappcompose.util.to24Hour
import kotlinx.collections.immutable.toImmutableList

@Composable
fun TimePickerSection(
    time: String,
    onTimeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = time.split(":")
    val hour24 = parts.getOrNull(0)?.toIntOrNull() ?: 7
    val minuteInt = parts.getOrNull(1)?.toIntOrNull() ?: 30

    val (displayHour12, isAm) = to12HourDisplay(hour24)

    val hourValues = remember { (1..12).map { "%02d".format(it) }.toImmutableList() }
    val minuteValues = remember { (0..59).map { "%02d".format(it) }.toImmutableList() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours
        WheelPickerColumn(
            values = hourValues,
            selectedIndex = displayHour12 - 1, // 1-12 → index 0-11
            onSelectedIndexChanged = { newIndex ->
                val newHour12 = newIndex + 1
                val newHour24 = to24Hour(newHour12, isAm)
                onTimeChanged("%02d:%02d".format(newHour24, minuteInt))
            },
        )

        Text(
            text = ":", style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold, fontSize = 56.sp
            ), color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
        )

        // Minutes
        WheelPickerColumn(
            values = minuteValues,
            selectedIndex = minuteInt,
            onSelectedIndexChanged = { newMinuteIndex ->
                onTimeChanged("%02d:%02d".format(hour24, newMinuteIndex))
            },
        )

        Spacer(modifier = Modifier.width(16.dp))

        // AM / PM Toggle
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "AM",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isAm) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.clickable {
                    if (!isAm) {
                        val newHour24 = to24Hour(displayHour12, isAm = true)
                        onTimeChanged("%02d:%02d".format(newHour24, minuteInt))
                    }
                })
            Text(
                text = "PM",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (!isAm) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.clickable {
                    if (isAm) {
                        val newHour24 = to24Hour(displayHour12, isAm = false)
                        onTimeChanged("%02d:%02d".format(newHour24, minuteInt))
                    }
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimePickerSectionPreview() {
    AlarmAppComposeTheme {
        TimePickerSection(
            time = "07:30", onTimeChanged = {})
    }
}
