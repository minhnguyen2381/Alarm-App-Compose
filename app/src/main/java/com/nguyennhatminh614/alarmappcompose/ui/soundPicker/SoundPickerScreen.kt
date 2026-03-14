package com.nguyennhatminh614.alarmappcompose.ui.soundPicker

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.ui.soundPicker.components.BrowseFoldersItem
import com.nguyennhatminh614.alarmappcompose.ui.soundPicker.components.SectionHeader
import com.nguyennhatminh614.alarmappcompose.ui.soundPicker.components.SoundItem
import com.nguyennhatminh614.alarmappcompose.ui.soundPicker.components.SoundSearchBar
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import com.nguyennhatminh614.alarmappcompose.util.DevicePreview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SoundPickerScreen(
    uiState: SoundPickerUiState,
    onEvent: (SoundPickerEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Permission state
    val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val permissionState = rememberPermissionState(permission = audioPermission) { granted ->
        if (granted) {
            onEvent(SoundPickerEvent.AudioPermissionGranted)
        }
    }

    // Check permission on launch
    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            onEvent(SoundPickerEvent.AudioPermissionGranted)
        }
    }

    // SAF file picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            // Take persistable permission so URI survives reboots
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers don't support persistable permissions
            }

            // Get display name from URI
            val title = getFileNameFromUri(context, it) ?: "Unknown"
            onEvent(SoundPickerEvent.DeviceFilePicked(uri = it.toString(), title = title))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.sound_picker_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: overflow menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search bar
                item {
                    SoundSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { onEvent(SoundPickerEvent.SearchQueryChanged(it)) }
                    )
                }

                // FILES ON DEVICE section
                item {
                    SectionHeader(title = stringResource(R.string.files_on_device))
                }

                item {
                    BrowseFoldersItem(
                        onClick = {
                            filePickerLauncher.launch(arrayOf("audio/*"))
                        }
                    )
                }

                // SYSTEM RINGTONES section
                if (uiState.systemRingtones.isNotEmpty() || uiState.presetSounds.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = stringResource(R.string.system_ringtones),
                            trailingText = stringResource(R.string.default_text)
                        )
                    }

                    // Show preset sounds first (they are system ringtones with custom names)
                    itemsIndexed(
                        items = uiState.presetSounds,
                        key = { index, item -> "preset_${index}_${item.uri}" }
                    ) { _, sound ->
                        SoundItem(
                            title = sound.title,
                            isSelected = sound.uri == uiState.selectedSoundUri,
                            isPlaying = sound.uri == uiState.currentlyPlayingUri,
                            onPlayClick = { onEvent(SoundPickerEvent.PreviewSound(sound)) },
                            onSelectClick = { onEvent(SoundPickerEvent.SoundSelected(sound)) }
                        )
                    }

                    // Show remaining system ringtones (filter out URIs already in presets)
                    val presetUris = uiState.presetSounds.map { it.uri }.toSet()
                    val filteredRingtones = uiState.systemRingtones.filter { it.uri !in presetUris }
                    items(
                        items = filteredRingtones,
                        key = { "ringtone_${it.uri}" }
                    ) { sound ->
                        SoundItem(
                            title = sound.title,
                            isSelected = sound.uri == uiState.selectedSoundUri,
                            isPlaying = sound.uri == uiState.currentlyPlayingUri,
                            onPlayClick = { onEvent(SoundPickerEvent.PreviewSound(sound)) },
                            onSelectClick = { onEvent(SoundPickerEvent.SoundSelected(sound)) }
                        )
                    }
                }

                // DEVICE AUDIO section
                if (uiState.hasAudioPermission && uiState.deviceAudioFiles.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(title = stringResource(R.string.device_audio))
                    }

                    items(
                        items = uiState.deviceAudioFiles,
                        key = { "device_${it.uri}" }
                    ) { sound ->
                        SoundItem(
                            title = sound.title,
                            isSelected = sound.uri == uiState.selectedSoundUri,
                            isPlaying = sound.uri == uiState.currentlyPlayingUri,
                            onPlayClick = { onEvent(SoundPickerEvent.PreviewSound(sound)) },
                            onSelectClick = { onEvent(SoundPickerEvent.SoundSelected(sound)) }
                        )
                    }
                } else if (!uiState.hasAudioPermission) {
                    // Show permission request
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(title = stringResource(R.string.device_audio))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.audio_permission_rationale),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { permissionState.launchPermissionRequest() }
                            ) {
                                Text(text = stringResource(R.string.grant_permission))
                            }
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

private fun getFileNameFromUri(context: android.content.Context, uri: android.net.Uri): String? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        if (nameIndex >= 0) cursor.getString(nameIndex) else null
    }
}

@DevicePreview
@Composable
private fun SoundPickerScreenPreview() {
    AlarmAppComposeTheme {
        SoundPickerScreen(
            uiState = SoundPickerUiState(isLoading = false),
            onEvent = {},
            onBackClick = {}
        )
    }
}
