package com.nguyennhatminh614.alarmappcompose.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

import androidx.navigation.compose.composable
import com.nguyennhatminh614.alarmappcompose.ui.alarms.AlarmsScreenRoute
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.CreateEditAlarmScreen

@Composable
fun ComposeApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.LIST_ALARM
    ) {
        composable(Route.LIST_ALARM) {
            AlarmsScreenRoute(
                onNavigateToCreateAlarm = {
                    navController.navigate(Route.CREATE_EDIT_ALARM)
                }
            )
        }

        composable(Route.CREATE_EDIT_ALARM) {
            CreateEditAlarmScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

object Route {
    const val LIST_ALARM = "alarm"
    const val CREATE_EDIT_ALARM = "create_edit_alarm"
}

object Argument {
    const val USERNAME = "username"
}