package com.nguyennhatminh614.alarmappcompose.ui.camera

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nguyennhatminh614.alarmappcompose.ui.camera.components.CameraNoPermissionScreen
import com.nguyennhatminh614.alarmappcompose.ui.camera.components.CameraScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreenRoute() {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    when {
        !cameraPermissionState.status.isGranted -> {
            CameraNoPermissionScreen(
                onRequestPermissionClick = {
                    cameraPermissionState.launchPermissionRequest()
                }
            )
        }

        else -> {
            CameraScreen()
        }
    }
}
