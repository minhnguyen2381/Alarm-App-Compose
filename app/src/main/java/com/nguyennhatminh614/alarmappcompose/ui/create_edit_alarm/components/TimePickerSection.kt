package com.nguyennhatminh614.alarmappcompose.ui.create_edit_alarm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme

@Composable
fun TimePickerSection(
    time: String,
    onTimeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Basic mock of the TimePicker Section from the UI
    // In a real app, you might use a WheelPicker or material TimePicker 
    // Here we match the visual representation from Stitch
    val parts = time.split(":")
    val hour = parts.getOrNull(0) ?: "07"
    val minute = parts.getOrNull(1) ?: "30"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours
        TimeColumn(
            currentValue = hour,
            previousValue = String.format("%02d", (hour.toIntOrNull() ?: 7) - 1),
            nextValue = String.format("%02d", (hour.toIntOrNull() ?: 7) + 1)
        )
        
        Text(
            text = ":",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 56.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
        )
        
        // Minutes
        TimeColumn(
            currentValue = minute,
            previousValue = String.format("%02d", (minute.toIntOrNull() ?: 30) - 5),
            nextValue = String.format("%02d", (minute.toIntOrNull() ?: 30) + 5)
        )

        Spacer(modifier = Modifier.width(16.dp))
        
        // AM / PM Toggle (Mocked)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "AM",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Toggle AM */ }
            )
            Text(
                text = "PM",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { /* Toggle PM */ }
            )
        }
    }
}

@Composable
private fun TimeColumn(
    currentValue: String,
    previousValue: String,
    nextValue: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = previousValue,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentValue,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Text(
            text = nextValue,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimePickerSectionPreview() {
    AlarmAppComposeTheme {
        TimePickerSection(
            time = "07:30",
            onTimeChanged = {}
        )
    }
}
