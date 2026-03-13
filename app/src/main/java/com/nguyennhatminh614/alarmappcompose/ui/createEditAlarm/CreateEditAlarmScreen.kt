package com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components.AlarmSettingsSection
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components.CreateEditAlarmTopBar
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components.DangerZoneSection
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components.RepeatSection
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components.TimePickerSection
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.components.WakeUpMissionsSection
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import com.nguyennhatminh614.alarmappcompose.util.DevicePreview

@Composable
fun CreateEditAlarmScreen(
    onNavigateBack: () -> Unit,
) {
    val viewModel: CreateEditAlarmViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateEditAlarmContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onCloseClick = onNavigateBack,
        onSaveClick = {
            viewModel.onEvent(CreateEditAlarmEvent.SaveAlarm)
            onNavigateBack()
        },
        onDeleteClick = {
            viewModel.onEvent(CreateEditAlarmEvent.DeleteAlarm)
            onNavigateBack()
        }
    )
}

@Composable
fun CreateEditAlarmContent(
    uiState: CreateEditAlarmUiState,
    onEvent: (CreateEditAlarmEvent) -> Unit,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CreateEditAlarmTopBar(
                onCloseClick = onCloseClick,
                onSaveClick = onSaveClick,
                modifier = Modifier.background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TimePickerSection(
                time = uiState.time,
                onTimeChanged = { onEvent(CreateEditAlarmEvent.TimeChanged(it)) }
            )

            RepeatSection(
                selectedDays = uiState.repeatDays,
                onDayToggled = { onEvent(CreateEditAlarmEvent.RepeatDayToggled(it)) }
            )

            AlarmSettingsSection(
                label = uiState.label,
                onLabelClick = { /* Show dialog to edit label */ },
                soundUriName = uiState.soundUri,
                onSoundClick = { /* Navigate to Sound picker */ },
                isFadeInSound = uiState.isFadeInSound,
                onFadeInSoundToggled = { onEvent(CreateEditAlarmEvent.FadeInSoundToggled(it)) },
                vibrationPattern = uiState.vibrationPattern,
                onVibrationClick = { /* Show dialog to pick vibration pattern */ }
            )

            WakeUpMissionsSection(
                currentMission = uiState.wakeUpMission,
                onMissionChanged = { onEvent(CreateEditAlarmEvent.WakeUpMissionChanged(it)) }
            )

            if (uiState.isEditing) {
                DangerZoneSection(onDeleteClick = onDeleteClick)
            }
        }
    }
}

@DevicePreview
@Composable
private fun CreateEditAlarmContentPreview() {
    AlarmAppComposeTheme {
        CreateEditAlarmContent(
            uiState = CreateEditAlarmUiState(),
            onEvent = {},
            onCloseClick = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}