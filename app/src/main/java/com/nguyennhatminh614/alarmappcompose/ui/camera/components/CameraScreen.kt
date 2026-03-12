package com.nguyennhatminh614.alarmappcompose.ui.camera.components

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.nguyennhatminh614.alarmappcompose.R
import com.nguyennhatminh614.alarmappcompose.util.DevicePreview
import timber.log.Timber
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val initialLabel = stringResource(R.string.camera_recognizing)
    var detectedLabel by remember { mutableStateOf(initialLabel) }

    val resultFormat = stringResource(R.string.camera_recognition_result)
    val failedMessage = stringResource(R.string.camera_recognition_failed)

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    val imageLabeler = remember {
        ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    }

    // Executor for MLKit analysis to run in background thread
    val backgroundExecutor = remember { Executors.newSingleThreadExecutor() }

    val onCameraFacingChange = remember {
        {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
        }
    }


    DisposableEffect(Unit) {
        onDispose {

            backgroundExecutor.shutdown()

            imageLabeler.close()

        }
    }

    CameraContentScreen(
        modifier = modifier,
        detectedLabel = detectedLabel,
        onChangeCameraFacing = onCameraFacingChange,
        cameraView = {
            LaunchedEffect(lensFacing, lifecycleOwner) {
                val cameraProviderFuture =
                    ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(backgroundExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(
                                mediaImage, imageProxy.imageInfo.rotationDegrees
                            )
                            imageLabeler.process(image).addOnSuccessListener { labels ->
                                val topLabel = labels.firstOrNull()
                                detectedLabel = if (topLabel != null) {
                                    String.format(
                                        resultFormat,
                                        topLabel.text,
                                        (topLabel.confidence * 100).toInt()
                                    )
                                } else {
                                    failedMessage
                                }
                            }.addOnCompleteListener {
                                imageProxy.close()
                            }
                        } else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                    } catch (e: Exception) {
                        Timber.tag("CameraScreen").e(e, "Use case binding failed")
                    }
                }, ContextCompat.getMainExecutor(context))
            }

            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

@Composable
fun CameraContentScreen(
    modifier: Modifier = Modifier,
    detectedLabel: String,
    onChangeCameraFacing: () -> Unit,
    cameraView: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        cameraView()

        Text(
            text = detectedLabel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .fillMaxWidth(0.5f)
                .background(
                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        // Change camera facing button
        Image(
            painter = painterResource(id = R.drawable.ic_refresh), // ID hình ảnh của bạn
            colorFilter = ColorFilter.tint(
                color = Color.White
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    bottom = 64.dp,
                    start = 16.dp,
                )
                .size(40.dp) // Kích thước của ảnh
                .clip(RoundedCornerShape(8.dp)) // Bo góc (tùy chọn)
                .clickable(onClick = onChangeCameraFacing)
        )
    }
}

@DevicePreview
@Composable
private fun CameraScreenPreview() {
    MaterialTheme {
        Surface {
            CameraContentScreen(
                detectedLabel = "Food Name: 99%",
                onChangeCameraFacing = {},
                cameraView = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }
            )
        }
    }
}
