package com.nguyennhatminh614.alarmappcompose.ui.alarms.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek

@Composable
fun AlarmItem(
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (alarm.isEnabled) 1f else 0.5f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alarm.time,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                    )
                )

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alarm.label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val allDays = DayOfWeek.values()
                    val isEveryday = alarm.repeatDays.size == 7
                    if (isEveryday) {
                        Text(
                            text = "Daily",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                            )
                        )
                    } else {
                        allDays.forEach { day ->
                            val isSelected = alarm.repeatDays.contains(day)
                            Text(
                                text = day.shortName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f * alpha)
                                )
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* TO DO: Handle Expand/Collapse */ }
                )
            }
        }
    }
}

// ======================== PREVIEWS ========================

@Preview(name = "Light Mode - Enabled", showBackground = true)
@Preview(name = "Dark Mode - Enabled", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlarmItemPreviewEnabled() {
    MaterialTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            AlarmItem(
                alarm = Alarm(
                    id = "1",
                    time = "07:00",
                    label = "Work",
                    isEnabled = true,
                    repeatDays = listOf(
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
                    )
                ),
                onToggle = {}
            )
        }
    }
}

@Preview(name = "Dark Mode - Disabled", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlarmItemPreviewDisabled() {
    MaterialTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            AlarmItem(
                alarm = Alarm(
                    id = "2",
                    time = "06:45",
                    label = "Wake Up",
                    isEnabled = false,
                    repeatDays = DayOfWeek.values().toList()
                ),
                onToggle = {}
            )
        }
    }
}
