package com.nguyennhatminh614.alarmappcompose.ui.alarms.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nguyennhatminh614.alarmappcompose.R

@Composable
fun ExactAlarmPermissionBanner(
    shouldOpenSettings: Boolean,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.alarm_permission_warning),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
        Button(
            onClick = onGrantClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
            Text(
                text = if (shouldOpenSettings) {
                    stringResource(R.string.alarm_permission_open_settings)
                } else {
                    stringResource(R.string.alarm_permission_grant)
                },
            )
        }
    }
}

// ======================== PREVIEWS ========================

@Preview(name = "Grant - Light", showBackground = true)
@Preview(name = "Grant - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExactAlarmPermissionBannerGrantPreview() {
    MaterialTheme {
        ExactAlarmPermissionBanner(
            shouldOpenSettings = false,
            onGrantClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Settings - Light", showBackground = true)
@Preview(name = "Settings - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExactAlarmPermissionBannerSettingsPreview() {
    MaterialTheme {
        ExactAlarmPermissionBanner(
            shouldOpenSettings = true,
            onGrantClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
