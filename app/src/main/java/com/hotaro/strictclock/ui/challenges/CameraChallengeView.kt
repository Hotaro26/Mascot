package com.hotaro.strictclock.ui.challenges

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

@Composable
fun CameraChallengeView(targetObject: String, onObjectDetected: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    // Pair of (Label Name, Confidence Percentage)
    val currentTopMatch = remember { mutableStateOf<Pair<String, Int>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val options = ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.4f) // Lowered to show more active matching
                        .build()
                    val labeler = ImageLabeling.getClient(options)

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executor) { imageProxy ->
                                processImageForLabeling(labeler, imageProxy, targetObject, onObjectDetected) { topLabel ->
                                    currentTopMatch.value = topLabel
                                }
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        // Handle exception
                    }
                }, executor)
                previewView
            }
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.wrapContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    val match = currentTopMatch.value
                    if (match != null) {
                        val isTargetMatch = match.first.contains(targetObject, ignoreCase = true) || targetObject.contains(match.first, ignoreCase = true)
                        val color = if (isTargetMatch) Color(0xFF4CAF50) else Color.White
                        Text(
                            text = "${match.first}: ${match.second}% Match",
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    } else {
                        Text("Scanning...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Find: $targetObject", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.background(Color.Black.copy(alpha=0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 8.dp))
        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImageForLabeling(
    labeler: com.google.mlkit.vision.label.ImageLabeler,
    imageProxy: ImageProxy,
    targetObject: String,
    onObjectDetected: () -> Unit,
    onTopLabelDetected: (Pair<String, Int>?) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val topLabel = labels.maxByOrNull { it.confidence }
                if (topLabel != null) {
                    val percentage = (topLabel.confidence * 100).toInt()
                    onTopLabelDetected(Pair(topLabel.text, percentage))
                } else {
                    onTopLabelDetected(null)
                }
                
                val found = labels.any { label ->
                    val isMatch = label.text.contains(targetObject, ignoreCase = true) || 
                                  targetObject.contains(label.text, ignoreCase = true)
                    isMatch && label.confidence >= 0.7f // Ensure target is actually confident before dismissing
                }
                if (found) {
                    onObjectDetected()
                }
            }
            .addOnFailureListener {
                // Task failed
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
