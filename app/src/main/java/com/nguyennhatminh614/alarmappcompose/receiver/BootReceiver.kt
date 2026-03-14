package com.nguyennhatminh614.alarmappcompose.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nguyennhatminh614.alarmappcompose.domain.repository.AlarmRepository
import com.nguyennhatminh614.alarmappcompose.domain.scheduler.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive() action=${intent.action}")
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alarms = alarmRepository.getAlarms().first()
                val enabledAlarms = alarms.filter { it.isEnabled }
                Log.d(TAG, "onReceive() total alarms=${alarms.size}, enabled=${enabledAlarms.size}")
                enabledAlarms.forEach { alarm ->
                    Log.d(TAG, "onReceive() re-scheduling alarm id=${alarm.id}, time=${alarm.time}")
                    alarmScheduler.schedule(alarm)
                }
            } catch (e: Exception) {
                Log.e(TAG, "onReceive() FAILED: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}