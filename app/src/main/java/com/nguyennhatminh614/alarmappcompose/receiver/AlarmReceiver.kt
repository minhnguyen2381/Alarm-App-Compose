package com.nguyennhatminh614.alarmappcompose.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import com.nguyennhatminh614.alarmappcompose.service.AlarmService

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmReceiver"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra(EXTRA_ALARM_ID)
        Log.d(TAG, "=== AlarmReceiver.onReceive() START ===")
        Log.d(TAG, "onReceive() alarmId=$alarmId, action=${intent.action}")
        Log.d(TAG, "onReceive() intent.extras=${intent.extras}")
        Log.d(TAG, "onReceive() SDK_INT=${Build.VERSION.SDK_INT}")

        // Check device state
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        Log.d(TAG, "onReceive() isInteractive=${pm.isInteractive}")
        Log.d(TAG, "onReceive() isIgnoringBatteryOptimizations=${pm.isIgnoringBatteryOptimizations(context.packageName)}")

        // Check background restrictions (Android 9+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val restricted = am.isBackgroundRestricted
            Log.d(TAG, "onReceive() isBackgroundRestricted=$restricted")
            if (restricted) {
                Log.e(TAG, "!!! APP IS BACKGROUND RESTRICTED - Service may not start !!!")
            }
        }

        if (alarmId == null) {
            Log.e(TAG, "onReceive() FAILED - alarmId is null, returning")
            return
        }

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmService.Companion.EXTRA_ALARM_ID, alarmId)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "onReceive() starting foreground service for alarm $alarmId")
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d(TAG, "onReceive() service started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "onReceive() FAILED to start service: ${e.message}", e)
            Log.e(TAG, "onReceive() Exception class: ${e.javaClass.name}")
        }
        Log.d(TAG, "=== AlarmReceiver.onReceive() END ===")
    }
}