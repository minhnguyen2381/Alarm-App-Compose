package com.nguyennhatminh614.alarmappcompose.domain.repository.impl

import android.content.ContentUris
import android.content.Context
import android.media.RingtoneManager
import android.provider.MediaStore
import com.nguyennhatminh614.alarmappcompose.domain.model.Sound
import com.nguyennhatminh614.alarmappcompose.domain.model.SoundType
import com.nguyennhatminh614.alarmappcompose.domain.repository.SoundRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SoundRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SoundRepository {

    private val presetNames = listOf(
        "Morning Breeze",
        "Birds Chirping",
        "Rising Sun",
        "Classic Bell",
        "Gentle Rain"
    )

    override fun getPresetSounds(): Flow<List<Sound>> = flow {
        val ringtoneManager = RingtoneManager(context).apply {
            setType(RingtoneManager.TYPE_ALARM)
        }
        val cursor = ringtoneManager.cursor
        val sounds = mutableListOf<Sound>()
        var index = 0

        while (cursor.moveToNext() && index < presetNames.size) {
            val uri = ringtoneManager.getRingtoneUri(cursor.position).toString()
            sounds.add(
                Sound(
                    uri = uri,
                    title = presetNames[index],
                    type = SoundType.PRESET
                )
            )
            index++
        }
        
        emit(sounds)
    }.flowOn(Dispatchers.IO)

    override fun getSystemRingtones(): Flow<List<Sound>> = flow {
        val ringtoneManager = RingtoneManager(context).apply {
            setType(RingtoneManager.TYPE_ALARM or RingtoneManager.TYPE_RINGTONE)
        }
        val cursor = ringtoneManager.cursor
        val sounds = mutableListOf<Sound>()
        val seenUris = mutableSetOf<String>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = ringtoneManager.getRingtoneUri(cursor.position).toString()
            if (seenUris.add(uri)) {
                sounds.add(
                    Sound(
                        uri = uri,
                        title = title,
                        type = SoundType.SYSTEM_RINGTONE
                    )
                )
            }
        }

        emit(sounds)
    }.flowOn(Dispatchers.IO)

    override fun getDeviceAudioFiles(): Flow<List<Sound>> = flow {
        val sounds = mutableListOf<Sound>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                ).toString()
                sounds.add(
                    Sound(
                        uri = uri,
                        title = title,
                        type = SoundType.DEVICE_FILE
                    )
                )
            }
        }

        emit(sounds)
    }.flowOn(Dispatchers.IO)
}