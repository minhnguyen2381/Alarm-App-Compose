package com.nguyennhatminh614.alarmappcompose.ui.alarms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.ui.alarms.components.ExactAlarmPermissionBanner
import com.nguyennhatminh614.alarmappcompose.ui.alarms.components.NextAlarmBanner
import com.nguyennhatminh614.alarmappcompose.ui.alarms.components.OverlayPermissionBanner
import com.nguyennhatminh614.alarmappcompose.ui.alarms.components.SwipeableAlarmItem
import com.nguyennhatminh614.alarmappcompose.util.DevicePreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreen(
    alarms: ImmutableList<Alarm>,
    showPermissionBanner: Boolean,
    shouldOpenSettings: Boolean,
    onGrantPermissionClick: () -> Unit,
    showOverlayPermissionBanner: Boolean,
    onGrantOverlayPermissionClick: () -> Unit,
    onToggleAlarm: (Alarm, Boolean) -> Unit,
    onDeleteAlarm: (Alarm) -> Unit,
    onAlarmClick: (Alarm) -> Unit,
    onAddAlarmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.alarms),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Open Menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                if (showPermissionBanner) {
                    item(key = "permission_banner") {
                        ExactAlarmPermissionBanner(
                            shouldOpenSettings = shouldOpenSettings,
                            onGrantClick = onGrantPermissionClick,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }

                if (showOverlayPermissionBanner) {
                    item(key = "overlay_permission_banner") {
                        OverlayPermissionBanner(
                            onGrantClick = onGrantOverlayPermissionClick,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }

                items(
                    items = alarms,
                    key = { it.id }
                ) { alarm ->
                    SwipeableAlarmItem(
                        alarm = alarm,
                        onToggle = { isEnabled -> onToggleAlarm(alarm, isEnabled) },
                        onClick = { onAlarmClick(alarm) },
                        onDeleteConfirmed = { onDeleteAlarm(alarm) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    NextAlarmBanner(
                        nextAlarmText = "Next alarm in 6h 45m", // TODO: Calculate actual time difference dynamically
                        onAddClick = onAddAlarmClick
                    )
                }
            }
        }
    }
}

// ======================== PREVIEWS ========================

class AlarmsPreviewParameterProvider : PreviewParameterProvider<ImmutableList<Alarm>> {
    override val values = sequenceOf(
        persistentListOf(
            Alarm(
                id = "1",
                time = "07:00",
                label = "WORK",
                isEnabled = true,
                repeatDays = persistentListOf(
                    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
                )
            ),
            Alarm(
                id = "2",
                time = "08:30",
                label = "GYM",
                isEnabled = true,
                repeatDays = persistentListOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
            ),
            Alarm(
                id = "3",
                time = "06:45",
                label = "WAKE UP",
                isEnabled = false,
                repeatDays = DayOfWeek.entries.toImmutableList()
            )
        ),
        persistentListOf()
    )
}

@DevicePreview
@Composable
fun AlarmsScreenPreview(
    @PreviewParameter(AlarmsPreviewParameterProvider::class) alarms: ImmutableList<Alarm>
) {
    MaterialTheme {
        AlarmsScreen(
            alarms = alarms,
            showPermissionBanner = true,
            shouldOpenSettings = false,
            onGrantPermissionClick = {},
            showOverlayPermissionBanner = false,
            onGrantOverlayPermissionClick = {},
            onToggleAlarm = { _, _ -> },
            onDeleteAlarm = {},
            onAlarmClick = {},
            onAddAlarmClick = {}
        )
    }
}

@DevicePreview
@Composable
fun AlarmsScreenPermissionSettingsPreview() {
    MaterialTheme {
        AlarmsScreen(
            alarms = persistentListOf(),
            showPermissionBanner = true,
            shouldOpenSettings = true,
            onGrantPermissionClick = {},
            showOverlayPermissionBanner = true,
            onGrantOverlayPermissionClick = {},
            onToggleAlarm = { _, _ -> },
            onDeleteAlarm = {},
            onAlarmClick = {},
            onAddAlarmClick = {}
        )
    }
}
