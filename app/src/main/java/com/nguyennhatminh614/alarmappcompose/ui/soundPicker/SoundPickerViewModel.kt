package com.nguyennhatminh614.alarmappcompose.ui.soundPicker

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyennhatminh614.alarmappcompose.domain.model.Sound
import com.nguyennhatminh614.alarmappcompose.domain.model.SoundType
import com.nguyennhatminh614.alarmappcompose.domain.usecase.sounds.SoundUseCases
import com.nguyennhatminh614.alarmappcompose.service.AudioPreviewManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SoundPickerUiState(
    val searchQuery: String = "",
    val presetSounds: ImmutableList<Sound> = persistentListOf(),
    val systemRingtones: ImmutableList<Sound> = persistentListOf(),
    val deviceAudioFiles: ImmutableList<Sound> = persistentListOf(),
    val selectedSoundUri: String? = null,
    val currentlyPlayingUri: String? = null,
    val isLoading: Boolean = true,
    val hasAudioPermission: Boolean = false
)

sealed interface SoundPickerEvent {
    data class SearchQueryChanged(val query: String) : SoundPickerEvent
    data class SoundSelected(val sound: Sound) : SoundPickerEvent
    data object StopPreview : SoundPickerEvent
    data object AudioPermissionGranted : SoundPickerEvent
    data class DeviceFilePicked(val uri: String, val title: String) : SoundPickerEvent
}

@HiltViewModel
class SoundPickerViewModel @Inject constructor(
    private val soundUseCases: SoundUseCases,
    private val audioPreviewManager: AudioPreviewManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialSoundUri: String? = savedStateHandle["currentSoundUri"]

    private val _uiState = MutableStateFlow(
        SoundPickerUiState(selectedSoundUri = initialSoundUri)
    )
    val uiState: StateFlow<SoundPickerUiState> = _uiState.asStateFlow()

    // Store raw lists for search filtering
    private var rawPresetSounds: List<Sound> = emptyList()
    private var rawSystemRingtones: List<Sound> = emptyList()
    private var rawDeviceAudioFiles: List<Sound> = emptyList()

    init {
        loadPresetSounds()
        loadSystemRingtones()
        observePlaybackState()
    }

    fun onEvent(event: SoundPickerEvent) {
        when (event) {
            is SoundPickerEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                applySearchFilter()
            }
            is SoundPickerEvent.SoundSelected -> {
                _uiState.update { it.copy(selectedSoundUri = event.sound.uri) }
                // Auto preview when selecting
                audioPreviewManager.play(event.sound.uri)
            }
            is SoundPickerEvent.StopPreview -> {
                audioPreviewManager.stop()
            }
            is SoundPickerEvent.AudioPermissionGranted -> {
                _uiState.update { it.copy(hasAudioPermission = true) }
                loadDeviceAudioFiles()
            }
            is SoundPickerEvent.DeviceFilePicked -> {
                val sound = Sound(
                    uri = event.uri,
                    title = event.title,
                    type = SoundType.DEVICE_FILE
                )
                _uiState.update { it.copy(selectedSoundUri = event.uri) }
                audioPreviewManager.play(event.uri)
            }
        }
    }

    fun getSelectedSound(): Sound? {
        val uri = _uiState.value.selectedSoundUri ?: return null
        val allSounds = rawPresetSounds + rawSystemRingtones + rawDeviceAudioFiles
        return allSounds.find { it.uri == uri }
    }

    private fun loadPresetSounds() {
        viewModelScope.launch {
            soundUseCases.getPresetSounds().collect { sounds ->
                rawPresetSounds = sounds
                _uiState.update {
                    it.copy(
                        presetSounds = applySearch(sounds).toImmutableList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadSystemRingtones() {
        viewModelScope.launch {
            soundUseCases.getSystemRingtones().collect { sounds ->
                rawSystemRingtones = sounds
                _uiState.update {
                    it.copy(
                        systemRingtones = applySearch(sounds).toImmutableList()
                    )
                }
            }
        }
    }

    private fun loadDeviceAudioFiles() {
        viewModelScope.launch {
            soundUseCases.getDeviceAudioFiles().collect { sounds ->
                rawDeviceAudioFiles = sounds
                _uiState.update {
                    it.copy(
                        deviceAudioFiles = applySearch(sounds).toImmutableList()
                    )
                }
            }
        }
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            audioPreviewManager.currentlyPlayingUri.collect { uri ->
                _uiState.update { it.copy(currentlyPlayingUri = uri) }
            }
        }
    }

    private fun applySearchFilter() {
        _uiState.update {
            it.copy(
                presetSounds = applySearch(rawPresetSounds).toImmutableList(),
                systemRingtones = applySearch(rawSystemRingtones).toImmutableList(),
                deviceAudioFiles = applySearch(rawDeviceAudioFiles).toImmutableList()
            )
        }
    }

    private fun applySearch(sounds: List<Sound>): List<Sound> {
        return soundUseCases.searchSounds(sounds, _uiState.value.searchQuery)
    }

    override fun onCleared() {
        super.onCleared()
        audioPreviewManager.release()
    }
}
