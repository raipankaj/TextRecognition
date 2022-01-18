package com.lib.recognizer

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun TextRecognizer(
    onSuccess: (Text) -> Unit,
    onException: (Exception) -> Unit) {

    JetCamera(onSuccess, onException)
}

@Composable
fun JetCamera(
    onSuccess: (Text) -> Unit,
    onException: (Exception) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current

    val recognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }

    val cameraExecutor: ExecutorService = remember {
        Executors.newSingleThreadExecutor()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context -> PreviewView(context) },
        update = { previewView ->
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            cameraExecutor, TextRecognition(recognizer,
                                onResult = {
                                    onSuccess(it)
                                },
                                onException = {
                                    onException(it)
                                })
                        )
                    }

                try {
                    cameraProvider.unbindAll()
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("TextRecognizer", "Exception ${exc.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(localContext))
        }
    )
}

private class TextRecognition(
    val recognizer: TextRecognizer,
    val onResult: (Text) -> Unit,
    val onException: (Exception) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            recognizer.process(image)
                .addOnSuccessListener {
                    onResult(it)
                    imageProxy.close()
                }.addOnFailureListener {
                    onException(it)
                    imageProxy.close()
                }
        }
    }
}