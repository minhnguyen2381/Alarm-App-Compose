package com.nguyennhatminh614.alarmappcompose.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nguyennhatminh614.alarmappcompose.ui.camera.CameraScreenRoute
import com.nguyennhatminh614.alarmappcompose.ui.details.DetailsScreenRoute
import com.nguyennhatminh614.alarmappcompose.ui.users.UsersScreenRoute

@Composable
fun ComposeApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.CAMERA
    ) {
        composable(Route.CAMERA) {
            CameraScreenRoute()
        }
        composable(Route.USER) { backStackEntry ->
            UsersScreenRoute(
                onUserClick = { username ->
                    // In order to discard duplicated navigation events, we check the Lifecycle
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate("${Route.DETAIL}/$username")
                    }
                }
            )
        }
        composable(
            route = "${Route.DETAIL}/{${Argument.USERNAME}}",
            arguments = listOf(
                navArgument(Argument.USERNAME) {
                    type = NavType.StringType
                }
            ),
        ) {
            DetailsScreenRoute()
        }
    }
}

object Route {
    const val CAMERA = "camera"
    const val USER = "user"
    const val DETAIL = "detail"
}

object Argument {
    const val USERNAME = "username"
}