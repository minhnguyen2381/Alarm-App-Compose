package com.nguyennhatminh614.alarmappcompose.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

import androidx.navigation.compose.composable
import com.nguyennhatminh614.alarmappcompose.ui.alarms.AlarmsScreenRoute

@Composable
fun ComposeApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.ALARM
    ) {
        composable(Route.ALARM) {
            AlarmsScreenRoute()
        }
    }
}

object Route {
    const val CAMERA = "camera"
    const val USER = "user"
    const val DETAIL = "detail"
    const val ALARM = "alarm"
}

object Argument {
    const val USERNAME = "username"
}