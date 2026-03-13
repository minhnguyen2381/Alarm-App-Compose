package com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.domain.model.VibrationPattern
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme

@Composable
fun AlarmSettingsSection(
    label: String,
    soundUriName: String?,
    isFadeInSound: Boolean,
    onFadeInSoundToggled: (Boolean) -> Unit,
    vibrationPattern: VibrationPattern,
    onSoundClick: () -> Unit,
    onLabelClick: () -> Unit,
    onVibrationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        SettingsItem(
            iconResId = R.drawable.ic_label,
            title = stringResource(R.string.alarm_label),
            subtitle = label,
            onClick = onLabelClick
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        Column(
            modifier = Modifier.clickable(onClick = onSoundClick)
        ) {
            SettingsItemContent(
                iconResId = R.drawable.ic_music,
                title = stringResource(R.string.sound),
                subtitle = soundUriName ?: stringResource(R.string.default_sound),
                showMoreButton = true
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.fade_in_sound),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Switch(
                    checked = isFadeInSound,
                    onCheckedChange = onFadeInSoundToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        SettingsItem(
            iconResId = R.drawable.ic_vibration,
            title = stringResource(R.string.vibration_pattern),
            subtitle = vibrationPattern.displayName,
            onClick = onVibrationClick
        )
    }
}

@Composable
private fun SettingsItem(
    @DrawableRes
    iconResId: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable(onClick = onClick)) {
        SettingsItemContent(
            iconResId = iconResId,
            title = title,
            subtitle = subtitle,
            showMoreButton = true
        )
    }
}

@Composable
private fun SettingsItemContent(
    modifier: Modifier = Modifier,
    @DrawableRes
    iconResId: Int,
    title: String,
    subtitle: String,
    showMoreButton: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (showMoreButton) {
            Icon(
                painter = painterResource(R.drawable.ic_more),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmSettingsSectionPreview() {
    AlarmAppComposeTheme {
        AlarmSettingsSection(
            label = "Wake up for work",
            onLabelClick = {},
            soundUriName = "Morning Breeze (Gentle)",
            onSoundClick = {},
            isFadeInSound = true,
            onFadeInSoundToggled = {},
            vibrationPattern = VibrationPattern.HEARTBEAT,
            onVibrationClick = {}
        )
    }
}
