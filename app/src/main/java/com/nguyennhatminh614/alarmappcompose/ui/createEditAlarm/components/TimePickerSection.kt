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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // Parse initial values once, then manage independently
    val parts = time.split(":")
    val initHour24 = parts.getOrNull(0)?.toIntOrNull() ?: 7
    val initMinute = parts.getOrNull(1)?.toIntOrNull() ?: 30
    val (initHour12, initIsAm) = to12HourDisplay(initHour24)

    var hourIndex by remember { mutableIntStateOf(initHour12 - 1) }
    var minuteIndex by remember { mutableIntStateOf(initMinute) }
    var isAm by remember { mutableStateOf(initIsAm) }

    // Helper to emit combined time
    fun emitTime(h12Index: Int = hourIndex, min: Int = minuteIndex, am: Boolean = isAm) {
        val hour24 = to24Hour(h12Index + 1, am)
        onTimeChanged("%02d:%02d".format(hour24, min))
    }

    val hourValues = remember { (1..12).map { "%02d".format(it) }.toImmutableList() }
    val minuteValues = remember { (0..59).map { "%02d".format(it) }.toImmutableList() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours - only updates hourIndex, does not touch minuteIndex or isAm
        WheelPickerColumn(
            values = hourValues,
            selectedIndex = hourIndex,
            onSelectedIndexChanged = { newIndex ->
                hourIndex = newIndex
                emitTime(h12Index = newIndex)
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

        // Minutes - only updates minuteIndex, does not touch hourIndex or isAm
        WheelPickerColumn(
            values = minuteValues,
            selectedIndex = minuteIndex,
            onSelectedIndexChanged = { newMinuteIndex ->
                minuteIndex = newMinuteIndex
                emitTime(min = newMinuteIndex)
            },
        )

        Spacer(modifier = Modifier.width(16.dp))

        // AM / PM Toggle - only updates isAm, does not touch hourIndex or minuteIndex
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
                        isAm = true
                        emitTime(am = true)
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
                        isAm = false
                        emitTime(am = false)
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
