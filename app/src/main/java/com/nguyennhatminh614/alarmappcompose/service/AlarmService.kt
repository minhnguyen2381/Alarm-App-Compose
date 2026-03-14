package com.nguyennhatminh614.alarmappcompose.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.domain.model.Alarm
import com.nguyennhatminh614.alarmappcompose.domain.model.VibrationPattern
import com.nguyennhatminh614.alarmappcompose.domain.scheduler.AlarmScheduler
import com.nguyennhatminh614.alarmappcompose.domain.usecase.alarms.AlarmUseCases
import com.nguyennhatminh614.alarmappcompose.receiver.AlarmReceiver
import com.nguyennhatminh614.alarmappcompose.ui.alarmRing.AlarmRingActivity
import android.content.pm.PackageManager
import android.provider.Settings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    companion object {
        private const val TAG = "AlarmService"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val CHANNEL_ID = "alarm_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_DISMISS = "com.nguyennhatminh614.alarmappcompose.DISMISS_ALARM"
        const val ACTION_SNOOZE = "com.nguyennhatminh614.alarmappcompose.SNOOZE_ALARM"
        const val SNOOZE_DURATION_MINUTES = 5
    }

    @Inject
    lateinit var alarmUseCases: AlarmUseCases

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentAlarmId: String? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        createNotificationChannel()
        logDiagnosticInfo()
    }

    private fun logDiagnosticInfo() {
        Log.d(TAG, "=== DIAGNOSTIC INFO START ===")
        Log.d(TAG, "Build.VERSION.SDK_INT=${Build.VERSION.SDK_INT}")
        Log.d(TAG, "Build.MANUFACTURER=${Build.MANUFACTURER}")
        Log.d(TAG, "Build.MODEL=${Build.MODEL}")

        // Check notification channel status
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = nm.getNotificationChannel(CHANNEL_ID)
            Log.d(TAG, "NotificationChannel=$CHANNEL_ID exists=${channel != null}")
            channel?.let {
                Log.d(TAG, "  importance=${it.importance} (HIGH=4)")
                Log.d(TAG, "  canShowBadge=${it.canShowBadge()}")
                Log.d(TAG, "  lockscreenVisibility=${it.lockscreenVisibility}")
            }
            Log.d(TAG, "areNotificationsEnabled=${nm.areNotificationsEnabled()}")
        }

        // Check USE_FULL_SCREEN_INTENT permission (Android 14+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val canFullScreen = nm.canUseFullScreenIntent()
            Log.w(TAG, "canUseFullScreenIntent()=$canFullScreen (Android 14+ required)")
            if (!canFullScreen) {
                Log.e(TAG, "!!! FULL_SCREEN_INTENT permission DENIED - AlarmRingActivity WILL NOT launch !!!")
                Log.e(TAG, "User must grant USE_FULL_SCREEN_INTENT in Settings > Apps > Special access")
            }
        }

        // Check SCHEDULE_EXACT_ALARM permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            Log.d(TAG, "canScheduleExactAlarms()=${am.canScheduleExactAlarms()}")
        }

        // Check if battery optimization is on
        val pm = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        Log.d(TAG, "isIgnoringBatteryOptimizations=${pm.isIgnoringBatteryOptimizations(packageName)}")

        Log.d(TAG, "=== DIAGNOSTIC INFO END ===")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() action=${intent?.action}, alarmId=${intent?.getStringExtra(EXTRA_ALARM_ID)}")

        when (intent?.action) {
            ACTION_DISMISS -> {
                Log.d(TAG, "onStartCommand() ACTION_DISMISS")
                stopAlarm()
                return START_NOT_STICKY
            }
            ACTION_SNOOZE -> {
                Log.d(TAG, "onStartCommand() ACTION_SNOOZE")
                snoozeAlarm()
                return START_NOT_STICKY
            }
        }

        val alarmId = intent?.getStringExtra(EXTRA_ALARM_ID)
        if (alarmId == null) {
            Log.e(TAG, "onStartCommand() FAILED - alarmId is null, stopping")
            stopSelf()
            return START_NOT_STICKY
        }

        currentAlarmId = alarmId

        // Must call startForeground within 5 seconds
        try {
            startForeground(NOTIFICATION_ID, buildNotification("Alarm"))
            Log.d(TAG, "onStartCommand() startForeground SUCCESS")
        } catch (e: Exception) {
            Log.e(TAG, "onStartCommand() startForeground FAILED: ${e.message}", e)
        }

        serviceScope.launch {
            val alarm = alarmUseCases.getAlarmById(alarmId)
            Log.d(TAG, "onStartCommand() loaded alarm=$alarm")
            if (alarm != null) {
                try {
                    val notification = buildFullScreenNotification(alarm)
                    val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    nm.notify(NOTIFICATION_ID, notification)
                    Log.d(TAG, "onStartCommand() full-screen notification posted")
                } catch (e: Exception) {
                    Log.e(TAG, "onStartCommand() full-screen notification FAILED: ${e.message}", e)
                }

                startSound(alarm)
                startVibration(alarm)

                // Re-schedule repeating alarms, toggle off one-shot alarms
                if (alarm.repeatDays.isNotEmpty()) {
                    Log.d(TAG, "onStartCommand() re-scheduling repeating alarm")
                    alarmScheduler.schedule(alarm)
                } else {
                    Log.d(TAG, "onStartCommand() disabling one-shot alarm")
                    alarmUseCases.toggleAlarm(alarm, false)
                }
            } else {
                Log.e(TAG, "onStartCommand() alarm not found in DB for id=$alarmId")
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopAlarm()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun stopAlarm() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        vibrator?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun snoozeAlarm() {
        val alarmId = currentAlarmId
        if (alarmId != null) {
            serviceScope.launch {
                val alarm = alarmUseCases.getAlarmById(alarmId)
                if (alarm != null) {
                    // Schedule alarm to fire again in SNOOZE_DURATION_MINUTES
                    val snoozeTimeMillis = System.currentTimeMillis() +
                            SNOOZE_DURATION_MINUTES * 60 * 1000L
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                    val intent = Intent(this@AlarmService, AlarmReceiver::class.java).apply {
                        putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        this@AlarmService,
                        alarmId.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                android.app.AlarmManager.RTC_WAKEUP,
                                snoozeTimeMillis,
                                pendingIntent
                            )
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                            android.app.AlarmManager.RTC_WAKEUP,
                            snoozeTimeMillis,
                            pendingIntent
                        )
                    }
                }
            }
        }
        stopAlarm()
    }

    private fun startSound(alarm: Alarm) {
        val soundUri = if (alarm.soundUri != null) {
            try {
                android.net.Uri.parse(alarm.soundUri)
            } catch (_: Exception) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        Log.d(TAG, "startSound() soundUri=$soundUri, alarmSoundUri=${alarm.soundUri}")

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmService, soundUri)
                isLooping = true
                prepare()
                start()
            }
            Log.d(TAG, "startSound() MediaPlayer started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "startSound() FAILED: ${e.message}", e)
        }
    }

    private fun startVibration(alarm: Alarm) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = when (alarm.vibrationPattern) {
            VibrationPattern.DEFAULT -> longArrayOf(0, 500, 500)
            VibrationPattern.HEARTBEAT -> longArrayOf(0, 200, 200, 200, 1000)
            VibrationPattern.TICKTOCK -> longArrayOf(0, 100, 100, 100, 100, 100, 600)
            VibrationPattern.WALTZ -> longArrayOf(0, 300, 200, 300, 200, 300, 600)
            VibrationPattern.ZIGZAG -> longArrayOf(0, 100, 50, 200, 50, 300, 50, 400, 500)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
                setSound(null, null)
                enableVibration(false)
            }
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .build()
    }

    private fun buildFullScreenNotification(alarm: Alarm): Notification {
        Log.d(TAG, "buildFullScreenNotification() alarmId=${alarm.id}, label='${alarm.label}'")

        // Full-screen intent to launch AlarmRingActivity
        val fullScreenIntent = Intent(this, AlarmRingActivity::class.java).apply {
            putExtra(AlarmRingActivity.EXTRA_ALARM_ID, alarm.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        Log.d(TAG, "buildFullScreenNotification() fullScreenIntent=$fullScreenIntent")
        Log.d(TAG, "buildFullScreenNotification() intent.extras=${fullScreenIntent.extras}")
        Log.d(TAG, "buildFullScreenNotification() intent.flags=0x${Integer.toHexString(fullScreenIntent.flags)}")

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarm.id.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d(TAG, "buildFullScreenNotification() PendingIntent created: $fullScreenPendingIntent")

        // Check if PendingIntent was actually created
        val testPendingIntent = PendingIntent.getActivity(
            this,
            alarm.id.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d(TAG, "buildFullScreenNotification() PendingIntent verify (FLAG_NO_CREATE): ${testPendingIntent != null}")

        // Dismiss action
        val dismissIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_DISMISS
        }
        val dismissPendingIntent = PendingIntent.getService(
            this, 1, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze action
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_SNOOZE
        }
        val snoozePendingIntent = PendingIntent.getService(
            this, 2, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(alarm.label)
            .setContentText(alarm.time)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .addAction(0, "Snooze", snoozePendingIntent)
            .build()

        Log.d(TAG, "buildFullScreenNotification() notification.flags=0x${Integer.toHexString(notification.flags)}")
        Log.d(TAG, "buildFullScreenNotification() notification.fullScreenIntent=${notification.fullScreenIntent}")
        Log.d(TAG, "buildFullScreenNotification() BUILT SUCCESSFULLY")

        return notification
    }
}
