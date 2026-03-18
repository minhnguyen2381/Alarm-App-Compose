package com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
fun CreateEditAlarmRoute(
    onNavigateBack: () -> Unit,
    onNavigateToSoundPicker: (currentSoundUri: String?) -> Unit = {},
    selectedSoundUri: String? = null,
    selectedSoundName: String? = null,
) {
    val viewModel: CreateEditAlarmViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Apply returned sound selection from SoundPicker
    LaunchedEffect(selectedSoundUri, selectedSoundName) {
        if (selectedSoundUri != null && selectedSoundName != null) {
            viewModel.onEvent(
                CreateEditAlarmEvent.SoundChanged(
                    uri = selectedSoundUri,
                    name = selectedSoundName
                )
            )
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        CreateEditAlarmScreen(
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
            },
            onSoundClick = { onNavigateToSoundPicker(uiState.soundUri) }
        )
    }
}

@Composable
fun CreateEditAlarmScreen(
    uiState: CreateEditAlarmUiState,
    onEvent: (CreateEditAlarmEvent) -> Unit,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSoundClick: () -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CreateEditAlarmTopBar(
                onCloseClick = onCloseClick,
                onSaveClick = onSaveClick,
                isEditing = uiState.isEditing,
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
                onLabelClick = { /* TODO: Show dialog to edit label */ },
                soundUriName = uiState.soundName ?: uiState.soundUri,
                onSoundClick = onSoundClick,
                isFadeInSound = uiState.isFadeInSound,
                onFadeInSoundToggled = { onEvent(CreateEditAlarmEvent.FadeInSoundToggled(it)) },
                vibrationPattern = uiState.vibrationPattern,
                onVibrationClick = { /* TODO: Show dialog to pick vibration pattern */ }
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
private fun CreateEditAlarmScreenPreview() {
    AlarmAppComposeTheme {
        CreateEditAlarmScreen(
            uiState = CreateEditAlarmUiState(),
            onEvent = {},
            onCloseClick = {},
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}
