package com.nguyennhatminh614.alarmappcompose.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

interface AudioPreviewManager {
    val currentlyPlayingUri: StateFlow<String?>
    fun play(uri: String)
    fun stop()
    fun release()
}

class AudioPreviewManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioPreviewManager {

    private var mediaPlayer: MediaPlayer? = null

    private val _currentlyPlayingUri = MutableStateFlow<String?>(null)
    override val currentlyPlayingUri: StateFlow<String?> = _currentlyPlayingUri.asStateFlow()

    override fun play(uri: String) {
        // If same URI is already playing, stop it (toggle behavior)
        if (_currentlyPlayingUri.value == uri) {
            stop()
            return
        }

        stop()

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(context, Uri.parse(uri))
                setOnCompletionListener {
                    _currentlyPlayingUri.value = null
                }
                setOnErrorListener { _, what, extra ->
                    Timber.e("MediaPlayer error: what=$what, extra=$extra")
                    _currentlyPlayingUri.value = null
                    true
                }
                prepare()
                start()
            }
            _currentlyPlayingUri.value = uri
        } catch (e: Exception) {
            Timber.e(e, "Failed to play sound preview: $uri")
            _currentlyPlayingUri.value = null
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop MediaPlayer")
        }
        mediaPlayer = null
        _currentlyPlayingUri.value = null
    }

    override fun release() = stop()
}
