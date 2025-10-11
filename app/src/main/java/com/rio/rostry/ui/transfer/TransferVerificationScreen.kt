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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.rio.rostry.utils.images.ImageUtils
import com.rio.rostry.ui.session.SessionViewModel
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.marketplace.media.MediaManager

@Composable
fun TransferVerificationScreen(
    transferId: String
) {
    val vm: TransferVerificationViewModel = hiltViewModel()
    val sessionVm: SessionViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(transferId) {
        vm.load(transferId)
    }

    // MediaManager-driven pickers/capture for consistent policy
    val pickBefore = MediaManager.rememberImagePicker { uri -> vm.startUpload("before", uri.toString()) }
    val pickAfter = MediaManager.rememberImagePicker { uri -> vm.startUpload("after", uri.toString()) }

    // Permission launcher for fine location
    val requestLocation = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            fetchCurrentLocation(context)?.let { loc ->
                vm.updateTemp("lat", loc.latitude.toString())
                vm.updateTemp("lng", loc.longitude.toString())
            }
        }
    }

    // MediaManager capture helpers
    val captureBefore = MediaManager.rememberImageCapture(context, fileNamePrefix = "before_${'$'}transferId") { uri ->
        vm.startUpload("before", uri.toString())
    }
    val captureAfter = MediaManager.rememberImageCapture(context, fileNamePrefix = "after_${'$'}transferId") { uri ->
        vm.startUpload("after", uri.toString())
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

        // Admin review gate for high-value transfers
        val threshold = 10000.0
        val requiresAdmin = (state.transfer?.amount ?: 0.0) > threshold
        if (requiresAdmin) {
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pending Admin Review", color = Color(0xFFF57C00))
                    Text("This transfer exceeds the review threshold. Buyer actions are disabled until approved.")
                }
            }
        }

        // Trust score badge & breakdown
        if (state.trustScore != null) {
            val score = state.trustScore ?: 0
            val color = when (score) {
                in 0..30 -> Color(0xFFC62828) // red
                in 31..60 -> Color(0xFFF57C00) // orange
                in 61..80 -> Color(0xFFFBC02D) // yellow
                else -> Color(0xFF2E7D32) // green
            }
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Trust Score")
                        Text(score.toString(), color = color)
                    }
                    if (state.trustBreakdown.isNotEmpty()) {
                        state.trustBreakdown.forEach { (k, v) ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(k)
                                val sign = if (v >= 0) "+" else ""
                                Text("$sign$v")
                            }
                        }
                    }
                }
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Step 1: Seller Photo Verification")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { pickBefore() }, enabled = !state.submitting && !requiresAdmin) { Text("Pick Before Photo") }
                    OutlinedButton(onClick = { pickAfter() }, enabled = !state.submitting && !requiresAdmin) { Text("Pick After Photo") }
                    OutlinedButton(onClick = { captureBefore() }, enabled = !state.submitting && !requiresAdmin) { Text("Capture Before") }
                    OutlinedButton(onClick = { captureAfter() }, enabled = !state.submitting && !requiresAdmin) { Text("Capture After") }
                }
                // Previews are omitted in favor of unified pipeline; see Uploads card below for progress
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        // Optional media limits check via shared MediaManager (photos only)
                        val stats = MediaManager.MediaStats(
                            photos = listOf(state.tempBeforeUrl, state.tempAfterUrl).count { it.isNotBlank() },
                            videos = 0, audios = 0, documents = 0
                        )
                        val check = MediaManager.checkLimits(stats, maxPhotos = 12)
                        if (!check.withinLimits) {
                            // Show first reason as simple feedback
                            // Defer to VM error channel
                            // In a full UX, we'd surface a Snackbar via VM
                        }
                        vm.submitSellerInit()
                    }, enabled = !state.submitting) { Text("Submit Photos") }
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
                // Proximity badge
                val sellerLat = state.transfer?.gpsLat
                val sellerLng = state.transfer?.gpsLng
                val buyerLat = state.tempLat.toDoubleOrNull()
                val buyerLng = state.tempLng.toDoubleOrNull()
                val within = remember(sellerLat, sellerLng, buyerLat, buyerLng) {
                    if (sellerLat != null && sellerLng != null && buyerLat != null && buyerLng != null)
                        com.rio.rostry.utils.VerificationUtils.withinRadius(sellerLat, sellerLng, buyerLat, buyerLng, 100.0)
                    else null
                }
                if (within != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val dist = com.rio.rostry.utils.VerificationUtils.distanceMeters(
                            sellerLat!!, sellerLng!!, buyerLat!!, buyerLng!!
                        ).toInt()
                        val badge = if (within) "Within 100m ($dist m)" else "Outside 100m ($dist m)"
                        Text(badge, color = if (within) Color(0xFF2E7D32) else Color(0xFFC62828))
                    }
                    if (!within) {
                        OutlinedTextField(
                            value = state.tempGpsExplanation,
                            onValueChange = { vm.updateTemp("gps_explanation", it) },
                            label = { Text("Explain why GPS is outside radius (required)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { requestLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION) }, enabled = !state.submitting && !requiresAdmin) { Text("Use Current Location") }
                    val canSubmitGps = when (within) {
                        null -> !state.submitting && !requiresAdmin // no baseline to compare; allow
                        true -> !state.submitting && !requiresAdmin
                        false -> !state.submitting && !requiresAdmin && state.tempGpsExplanation.isNotBlank() // require explanation when outside
                    }
                    Button(onClick = { vm.submitGpsConfirm() }, enabled = canSubmitGps) { Text("Submit GPS") }
                    if (state.submitting) { CircularProgressIndicator(modifier = Modifier.padding(start = 8.dp)) }
                }
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Step 3: Identity Document")
                // Identity type dropdown (Aadhaar, PAN, DL)
                var idTypeExpanded by remember { mutableStateOf(false) }
                val identityTypes = listOf("Aadhaar", "PAN", "DL")
                OutlinedTextField(
                    value = state.tempIdentityDocType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Identity Type") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        OutlinedButton(onClick = { idTypeExpanded = !idTypeExpanded }) { Text(if (idTypeExpanded) "Hide" else "Choose") }
                    }
                )
                if (idTypeExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        identityTypes.forEach { opt ->
                            OutlinedButton(onClick = { vm.updateTemp("identityDocType", opt); idTypeExpanded = false }) { Text(opt) }
                        }
                    }
                }
                // Pick and upload identity image
                val pickIdentity = MediaManager.rememberImagePicker { uri -> vm.startUpload("identity", uri.toString()) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { pickIdentity() }, enabled = !state.submitting) { Text("Pick ID Image") }
                    OutlinedButton(onClick = { vm.updateTemp("identityDocRef", ""); }, enabled = !state.submitting) { Text("Clear") }
                }
                if (state.tempIdentityDocRef.isNotBlank()) {
                    Text("Uploaded: ${state.tempIdentityDocRef}")
                }
                // Identity document number input
                OutlinedTextField(
                    value = state.tempIdentityDocNumber ?: "",
                    onValueChange = { vm.updateTemp("identityDocNumber", it) },
                    label = { Text("Identity Document Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Step 4: Digital Signature")
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

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Timeline & Audit Trail")
                // Timeline
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    state.verifications.sortedBy { it.createdAt ?: 0L }.forEach { v ->
                        val whenText = v.createdAt?.let { java.text.SimpleDateFormat("MMM d, HH:mm").format(java.util.Date(it)) } ?: ""
                        Text("• ${v.step} — ${v.status} $whenText")
                    }
                }
                // Audit trail (expandable)
                var expanded by remember { mutableStateOf(false) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { expanded = !expanded }) { Text(if (expanded) "Hide Audit" else "Show Audit") }
                }
                if (expanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.auditLogs.forEach { log ->
                            val t = java.text.SimpleDateFormat("MMM d, HH:mm").format(java.util.Date(log.createdAt ?: 0L))
                            Text("• $t — ${log.type}/${log.action} by ${log.actorUserId ?: "system"}")
                        }
                    }
                }
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Disputes")
                // Show existing disputes
                if (state.disputes.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.disputes.forEach { d ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${d.reason} — ${d.status}")
                                val role = sessionVm.uiState.collectAsState().value.role
                                val canModerate = role?.name in setOf("ADMIN", "MODERATOR")
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (d.status == "OPEN" && canModerate) {
                                        OutlinedButton(onClick = { vm.resolveDispute(d.disputeId, "Resolved by admin", true) }) { Text("Resolve") }
                                        OutlinedButton(onClick = { vm.resolveDispute(d.disputeId, "Rejected", false) }) { Text("Reject") }
                                    } else if (d.status == "OPEN" && !canModerate) {
                                        Text("Platform review required", color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
                // Open new dispute
                var reason by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        if (reason.isNotBlank()) {
                            vm.raiseDispute(reason)
                            reason = ""
                        }
                    }, enabled = !state.submitting) { Text("Open Dispute") }
                    OutlinedButton(onClick = { vm.refresh() }) { Text("Refresh") }
                }
            }
        }

        // Upload progress (if any)
        if (state.uploadProgress.isNotEmpty()) {
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Uploads")
                    state.uploadProgress.forEach { (path, pct) ->
                        Text("${'$'}path — ${'$'}pct%")
                    }
                }
            }
        }
    }
    }
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

// Removed thumbnail/EXIF helpers; MediaManager centralizes media entry.
