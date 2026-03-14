package com.nguyennhatminh614.alarmappcompose.ui.soundPicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SoundPickerScreenRoute(
    onNavigateBackWithResult: (uri: String?, title: String?) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SoundPickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SoundPickerScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = {
            viewModel.onEvent(SoundPickerEvent.StopPreview)
            onNavigateBack()
        },
        onApplyClick = {
            viewModel.onEvent(SoundPickerEvent.StopPreview)
            val selectedSound = viewModel.getSelectedSound()
            if (selectedSound != null) {
                onNavigateBackWithResult(selectedSound.uri, selectedSound.title)
            } else {
                onNavigateBack()
            }
        }
    )
}
