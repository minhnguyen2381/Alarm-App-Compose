package com.nguyennhatminh614.alarmappcompose.ui.alarmRing

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
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
        private const val TAG = "AlarmRingActivity"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }

    @Inject
    lateinit var alarmUseCases: AlarmUseCases

    private var alarm by mutableStateOf<Alarm?>(null)
    private var isAlarmActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "=== AlarmRingActivity onCreate() START ===")
        Log.d(TAG, "onCreate() intent=$intent")
        Log.d(TAG, "onCreate() intent.action=${intent?.action}")
        Log.d(TAG, "onCreate() intent.flags=0x${Integer.toHexString(intent?.flags ?: 0)}")
        Log.d(TAG, "onCreate() intent.extras=${intent?.extras}")
        Log.d(TAG, "onCreate() intent.categories=${intent?.categories}")
        Log.d(TAG, "onCreate() savedInstanceState=${savedInstanceState != null}")

        // Log device state
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Log.d(TAG, "onCreate() isInteractive=${pm.isInteractive}")
        Log.d(TAG, "onCreate() isKeyguardLocked=${km.isKeyguardLocked}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Log.d(TAG, "onCreate() isDeviceLocked=${km.isDeviceLocked}")
        }

        enableEdgeToEdge()
        setupScreenWake()

        // Block back press to prevent dismissing the alarm screen
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Back press intercepted and blocked")
            }
        })

        val alarmId = intent?.getStringExtra(EXTRA_ALARM_ID)
        Log.d(TAG, "onCreate() alarmId=$alarmId")
        if (alarmId == null) {
            Log.e(TAG, "onCreate() FAILED - no alarm ID, finishing")
            Log.e(TAG, "onCreate() ALL extras: ${intent?.extras?.keySet()?.joinToString { "$it=${intent.extras?.get(it)}" }}")
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            alarm = alarmUseCases.getAlarmById(alarmId)
            Log.d(TAG, "onCreate() loaded alarm from DB: $alarm")
        }

        setContent {
            AlarmAppComposeTheme {
                AlarmRingScreen(
                    alarm = alarm,
                    snoozeDurationMinutes = AlarmService.SNOOZE_DURATION_MINUTES,
                    onDismiss = {
                        Log.d(TAG, "onDismiss() called")
                        isAlarmActive = false
                        sendServiceAction(AlarmService.ACTION_DISMISS)
                        finish()
                    },
                    onSnooze = {
                        Log.d(TAG, "onSnooze() called")
                        isAlarmActive = false
                        sendServiceAction(AlarmService.ACTION_SNOOZE)
                        finish()
                    },
                )
            }
        }
        Log.d(TAG, "=== AlarmRingActivity onCreate() END ===")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent() intent=$intent, alarmId=${intent.getStringExtra(EXTRA_ALARM_ID)}")
        val newAlarmId = intent.getStringExtra(EXTRA_ALARM_ID)
        if (newAlarmId != null && newAlarmId != this.intent?.getStringExtra(EXTRA_ALARM_ID)) {
            setIntent(intent)
            CoroutineScope(Dispatchers.IO).launch {
                alarm = alarmUseCases.getAlarmById(newAlarmId)
                Log.d(TAG, "onNewIntent() loaded new alarm from DB: $alarm")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
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
