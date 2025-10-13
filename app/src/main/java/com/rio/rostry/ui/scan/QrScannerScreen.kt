package com.rio.rostry.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import com.rio.rostry.BuildConfig

@Composable
fun QrScannerScreen(
    onResult: (String) -> Unit,
    onValidate: ((String) -> Boolean)? = null,
    hint: String = "Product ID or rostry://product/{id}"
) {
    val vm: QrScannerViewModel = hiltViewModel()
    val context = LocalContext.current
    var showManual by rememberSaveable { mutableStateOf(true) }
    var value by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var lastEmittedAt by rememberSaveable { mutableStateOf(0L) }

    // Feature flag guard
    if (BuildConfig.FEATURE_QR_CAMERA) {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            showManual = false
            ElevatedCard(Modifier.padding(16.dp)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Scan QR")
                    Box(Modifier.fillMaxWidth().height(280.dp)) {
                        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
                        AndroidView(factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                                val analysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                val scanner = BarcodeScanning.getClient(
                                    com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                        .build()
                                )
                                analysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy: ImageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                        scanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                val now = System.currentTimeMillis()
                                                if (now - lastEmittedAt < 1200) { imageProxy.close(); return@addOnSuccessListener }
                                                val text = barcodes.firstOrNull()?.rawValue
                                                if (!text.isNullOrBlank()) {
                                                    val productId = parseProductId(text)
                                                    if (productId.isNotBlank()) {
                                                        CoroutineScope(Dispatchers.Main).launch {
                                                            val exists = vm.productExists(productId)
                                                            if (exists && (onValidate == null || onValidate(productId))) {
                                                                lastEmittedAt = now
                                                                onResult(productId)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            .addOnCompleteListener { imageProxy.close() }
                                    } else imageProxy.close()
                                }
                                // Bind analysis only (PreviewView still shows camera feed via SurfaceProvider)
                                val preview = androidx.camera.core.Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        (ctx as androidx.lifecycle.LifecycleOwner), selector, preview, analysis
                                    )
                                } catch (_: Exception) {}
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        })
                    }
                    TextButton(onClick = { showManual = true }) { Text("Use manual input") }
                }
            }
        } else {
            // No permission -> show rationale and fallback
            ElevatedCard(Modifier.padding(16.dp)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Camera permission needed to scan QR codes.")
                    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                        if (granted) {
                            showManual = false
                        }
                    }
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) { Text("Grant permission") }
                    TextButton(onClick = { showManual = true }) { Text("Use manual input") }
                }
            }
        }
    }

    if (!BuildConfig.FEATURE_QR_CAMERA || showManual) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Enter Product ID")
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text(hint) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error != null) {
                        Text(error!!, color = androidx.compose.ui.graphics.Color.Red)
                    }
                    Button(onClick = {
                        val input = value.trim()
                        if (input.isBlank()) return@Button
                        val productId = parseProductId(input)
                        if (productId.isBlank()) {
                            error = "Invalid QR content"
                            return@Button
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            val exists = vm.productExists(productId)
                            if (!exists || (onValidate != null && !onValidate(productId))) {
                                error = "Product not found. Scan a valid ROSTRY QR code."
                                return@launch
                            }
                            error = null
                            onResult(productId)
                        }
                    }) { Text("Confirm") }
                    TextButton(onClick = { value = "" }) { Text("Clear") }
                }
            }
        }
    }
}

internal fun parseProductId(text: String): String {
    val trimmed = text.trim()
    val prefix = "rostry://product/"
    return if (trimmed.startsWith(prefix, ignoreCase = true)) {
        trimmed.removePrefix(prefix).takeWhile { !it.isWhitespace() && it != '?' && it != '#' }
    } else trimmed
}
