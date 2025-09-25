package com.rio.rostry.ui.transfer

import android.net.Uri
import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.FileProvider
import androidx.compose.foundation.Image
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import androidx.compose.runtime.LaunchedEffect
import androidx.exifinterface.media.ExifInterface

@Composable
fun TransferVerificationScreen(
    transferId: String
) {
    val vm: TransferVerificationViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(transferId) {
        vm.load(transferId)
    }

    // Launchers for photo picking
    val pickBefore = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { vm.updateTemp("before", it.toString()) }
    }
    val pickAfter = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { vm.updateTemp("after", it.toString()) }
    }

    // Permission launcher for fine location
    val requestLocation = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            fetchCurrentLocation(context)?.let { loc ->
                vm.updateTemp("lat", loc.latitude.toString())
                vm.updateTemp("lng", loc.longitude.toString())
            }
        }
    }

    // Camera capture state and launchers
    val beforeCaptureUri = remember { mutableStateOf<Uri?>(null) }
    val afterCaptureUri = remember { mutableStateOf<Uri?>(null) }

    val takeBefore = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) beforeCaptureUri.value?.let { vm.updateTemp("before", it.toString()) }
    }
    val takeAfter = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) afterCaptureUri.value?.let { vm.updateTemp("after", it.toString()) }
    }

    val requestCameraBefore = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            beforeCaptureUri.value?.let { takeBefore.launch(it) }
        }
    }
    val requestCameraAfter = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            afterCaptureUri.value?.let { takeAfter.launch(it) }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.success) {
        val msg = state.success
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message = msg)
            vm.consumeSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { data -> Snackbar(snackbarData = data) } }
    ) { inner ->
    Column(Modifier.padding(inner).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Transfer Verification: $transferId")
        state.error?.let { Text("Error: $it") }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Step 1: Seller Photo Verification")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { pickBefore.launch("image/*") }, enabled = !state.submitting) { Text("Pick Before Photo") }
                    OutlinedButton(onClick = { pickAfter.launch("image/*") }, enabled = !state.submitting) { Text("Pick After Photo") }
                    OutlinedButton(onClick = {
                        beforeCaptureUri.value = createImageUri(context, "before_${transferId}")
                        requestCameraBefore.launch(Manifest.permission.CAMERA)
                    }, enabled = !state.submitting) { Text("Capture Before") }
                    OutlinedButton(onClick = {
                        afterCaptureUri.value = createImageUri(context, "after_${transferId}")
                        requestCameraAfter.launch(Manifest.permission.CAMERA)
                    }, enabled = !state.submitting) { Text("Capture After") }
                }
                if (state.tempBeforeUrl.isNotBlank()) {
                    val bmp = remember(state.tempBeforeUrl) { loadThumbnail(context, Uri.parse(state.tempBeforeUrl)) }
                    bmp?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "Before", modifier = Modifier.fillMaxWidth()) }
                    val beforeSummary = remember(state.tempBeforeUrl) { exifSummary(context, Uri.parse(state.tempBeforeUrl)) }
                    beforeSummary?.let { Text(it) }
                }
                if (state.tempAfterUrl.isNotBlank()) {
                    val bmp = remember(state.tempAfterUrl) { loadThumbnail(context, Uri.parse(state.tempAfterUrl)) }
                    bmp?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "After", modifier = Modifier.fillMaxWidth()) }
                    val afterSummary = remember(state.tempAfterUrl) { exifSummary(context, Uri.parse(state.tempAfterUrl)) }
                    afterSummary?.let { Text(it) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.submitSellerInit() }, enabled = !state.submitting) { Text("Submit Photos") }
                    if (state.submitting) { CircularProgressIndicator(modifier = Modifier.padding(start = 8.dp)) }
                }
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Step 2: GPS Location Confirmation")
                OutlinedTextField(
                    value = state.tempLat,
                    onValueChange = { vm.updateTemp("lat", it) },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.tempLng,
                    onValueChange = { vm.updateTemp("lng", it) },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { requestLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION) }, enabled = !state.submitting) { Text("Use Current Location") }
                    Button(onClick = { vm.submitGpsConfirm() }, enabled = !state.submitting) { Text("Submit GPS") }
                    if (state.submitting) { CircularProgressIndicator(modifier = Modifier.padding(start = 8.dp)) }
                }
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Step 3: Digital Signature")
                SignaturePad(
                    onSaved = { ref -> vm.updateTemp("sig", ref) }
                )
                OutlinedTextField(
                    value = state.tempSignatureRef,
                    onValueChange = { vm.updateTemp("sig", it) },
                    label = { Text("Signature Ref / Hash") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.submitSignature() }, enabled = !state.submitting) { Text("Submit Signature") }
                    if (state.submitting) { CircularProgressIndicator(modifier = Modifier.padding(start = 8.dp)) }
                }
            }
        }
    }
    }
}

// Helper to create a content Uri for camera capture using the app's FileProvider
private fun createImageUri(context: Context, name: String): Uri {
    val imagesDir = java.io.File(context.cacheDir, "images").apply { mkdirs() }
    val file = java.io.File(imagesDir, "$name-${System.currentTimeMillis()}.jpg")
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

@Composable
private fun SignaturePad(onSaved: (String) -> Unit) {
    val paths = remember { mutableStateListOf<Path>() }
    val currentPath = remember { Path() }
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray)
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = { offset ->
                        currentPath.moveTo(offset.x, offset.y)
                    }, onDrag = { _, dragAmount ->
                        currentPath.relativeLineTo(dragAmount.x, dragAmount.y)
                    }, onDragEnd = {
                        paths.add(Path().apply { addPath(currentPath) })
                        currentPath.reset()
                    })
                }
                .padding(1.dp)
        ) {
            // Draw saved paths
            for (p in paths) {
                drawPath(p, Color.Black)
            }
            // Draw current path
            drawPath(currentPath, Color.Black)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { paths.clear(); onSaved("") }) { Text("Clear") }
            Button(onClick = {
                val uri = saveSignatureAsImage(context, paths)
                onSaved(uri?.toString() ?: "")
            }, enabled = paths.isNotEmpty()) { Text("Save Signature") }
        }
    }
}

// ===== Top-level helpers =====

private fun fetchCurrentLocation(context: Context): Location? {
    return try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        var best: Location? = null
        for (p in providers) {
            val loc = lm.getLastKnownLocation(p)
            if (loc != null && (best == null || loc.accuracy < best.accuracy)) best = loc
        }
        best
    } catch (_: SecurityException) {
        null
    }
}

private fun exifSummary(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val exif = ExifInterface(input)
            val dt = exif.getAttribute(ExifInterface.TAG_DATETIME)
            val ll = exif.latLong
            val lat = ll?.getOrNull(0)
            val lng = ll?.getOrNull(1)
            buildString {
                append("EXIF: ")
                if (dt != null) append("Time=$dt ")
                if (lat != null && lng != null) append("GPS=$lat,$lng")
            }.ifBlank { null }
        }
    } catch (_: Exception) { null }
}

private fun saveSignatureAsImage(context: Context, paths: List<Path>, width: Int = 1000, height: Int = 400): Uri? {
    return try {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        for (p in paths) {
            canvas.drawPath(p.asAndroidPath(), paint)
        }
        val imagesDir = java.io.File(context.filesDir, "images").apply { mkdirs() }
        val file = java.io.File(imagesDir, "signature_${System.currentTimeMillis()}.png")
        java.io.FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } catch (_: Exception) {
        null
    }
}

private fun loadThumbnail(context: Context, uri: Uri): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, options)
            val (w, h) = options.outWidth to options.outHeight
            val sample = computeInSampleSize(w, h, 512, 512)
            context.contentResolver.openInputStream(uri)?.use { input2 ->
                val opts = BitmapFactory.Options().apply { inSampleSize = sample }
                BitmapFactory.decodeStream(input2, null, opts)
            }
        }
    } catch (_: Exception) { null }
}

private fun computeInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
