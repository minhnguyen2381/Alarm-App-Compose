package com.nguyennhatminh614.alarmappcompose.ui.alarmRing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.DayOfWeek
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import com.nguyennhatminh614.alarmappcompose.service.AlarmService
import com.nguyennhatminh614.alarmappcompose.ui.theme.AlarmAppComposeTheme
import com.nguyennhatminh614.alarmappcompose.util.DevicePreview
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.persistentListOf
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
                    onDismiss = {
                        sendServiceAction(AlarmService.ACTION_DISMISS)
                        finish()
                    },
                    onSnooze = {
                        sendServiceAction(AlarmService.ACTION_SNOOZE)
                        finish()
                    }
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

@Composable
private fun AlarmRingScreen(
    alarm: Alarm?,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Alarm time
        Text(
            text = alarm?.time ?: "--:--",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Alarm label
        Text(
            text = alarm?.label ?: "",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            OutlinedButton(
                onClick = onSnooze,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "Snooze",
                    fontSize = 18.sp,
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Dismiss",
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@DevicePreview
@Composable
private fun AlarmRingScreenPreview() {
    AlarmAppComposeTheme {
        AlarmRingScreen(
            alarm = Alarm(
                id = "1",
                time = "07:00",
                label = "Wake Up",
                isEnabled = true,
                repeatDays = persistentListOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY
                )
            ),
            onDismiss = {},
            onSnooze = {}
        )
    }
}
