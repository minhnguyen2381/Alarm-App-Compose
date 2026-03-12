package com.nguyennhatminh614.alarmappcompose.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun ComposeApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.CAMERA
    ) {
       /* composable(Route.CAMERA) {
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
        }*/
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