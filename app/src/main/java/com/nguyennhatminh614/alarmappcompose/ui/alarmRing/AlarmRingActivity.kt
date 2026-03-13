package com.nguyennhatminh614.alarmappcompose.ui.alarmRing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import com.nguyennhatminh614.alarmappcompose.service.AlarmService
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRingActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }

    @Inject
    lateinit var alarmUseCases: AlarmUseCases

    private var alarm by mutableStateOf<Alarm?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setupScreenWake()

        val alarmId = intent?.getStringExtra(EXTRA_ALARM_ID)
        if (alarmId == null) {
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            alarm = alarmUseCases.getAlarmById(alarmId)
        }

        setContent {
            AlarmAppComposeTheme {
                AlarmRingScreen(
                    alarm = alarm,
                    snoozeDurationMinutes = AlarmService.SNOOZE_DURATION_MINUTES,
                    onDismiss = {
                        sendServiceAction(AlarmService.ACTION_DISMISS)
                        finish()
                    },
                    onSnooze = {
                        sendServiceAction(AlarmService.ACTION_SNOOZE)
                        finish()
                    },
                )
            }
        }
    }

    private fun setupScreenWake() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    private fun sendServiceAction(action: String) {
        val intent = Intent(this, AlarmService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }
}
