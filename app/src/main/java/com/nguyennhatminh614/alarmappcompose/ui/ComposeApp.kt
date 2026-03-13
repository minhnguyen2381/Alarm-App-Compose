package com.nguyennhatminh614.alarmappcompose.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nguyennhatminh614.alarmappcompose.ui.alarms.AlarmsScreenRoute
import com.nguyennhatminh614.alarmappcompose.ui.createEditAlarm.CreateEditAlarmScreen
import com.nguyennhatminh614.alarmappcompose.ui.soundPicker.SoundPickerScreenRoute

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

        composable(Route.CREATE_EDIT_ALARM) { backStackEntry ->
            // Observe result from SoundPicker
            val selectedSoundUri by backStackEntry.savedStateHandle
                .getStateFlow<String?>("selected_sound_uri", null)
                .collectAsStateWithLifecycle()
            val selectedSoundName by backStackEntry.savedStateHandle
                .getStateFlow<String?>("selected_sound_name", null)
                .collectAsStateWithLifecycle()

            CreateEditAlarmScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSoundPicker = { currentSoundUri ->
                    val encoded = Uri.encode(currentSoundUri ?: "")
                    navController.navigate("${Route.SOUND_PICKER_BASE}?currentSoundUri=$encoded")
                },
                selectedSoundUri = selectedSoundUri,
                selectedSoundName = selectedSoundName
            )
        }

        composable(
            route = "${Route.SOUND_PICKER_BASE}?currentSoundUri={currentSoundUri}",
            arguments = listOf(
                navArgument("currentSoundUri") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            SoundPickerScreenRoute(
                onNavigateBackWithResult = { uri, title ->
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set("selected_sound_uri", uri)
                        set("selected_sound_name", title)
                    }
                    navController.popBackStack()
                },
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
    const val SOUND_PICKER_BASE = "sound_picker"
}

object Argument {
    const val USERNAME = "username"
}
