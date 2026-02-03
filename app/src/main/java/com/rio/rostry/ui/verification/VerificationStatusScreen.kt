package com.rio.rostry.ui.verification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationStatusScreen(
    onNavigateBack: () -> Unit,
    onStartVerification: (UserType) -> Unit,
    onResubmit: (UserType) -> Unit
) {
    val viewModel: VerificationViewModel = hiltViewModel()
    val uiState by viewModel.ui.collectAsState()
    val user = uiState.user

    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val context = LocalContext.current
    var showDocumentDialog by remember { mutableStateOf(false) }
    val docUrls = uiState.uploadedDocuments
    val imgUrls = uiState.uploadedImages
    val allDocuments = docUrls + imgUrls
    val docTypes = uiState.uploadedDocTypes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verification Status") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Timeline Component
            item {
                Text("Verification Timeline", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Uploaded Stage
                    TimelineItem(
                        title = "Documents Uploaded",
                        subtitle = if (docUrls.isNotEmpty() || imgUrls.isNotEmpty()) "Uploaded" else "Not uploaded",
                        icon = Icons.Default.Upload,
                        color = if (docUrls.isNotEmpty() || imgUrls.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        isCompleted = docUrls.isNotEmpty() || imgUrls.isNotEmpty()
                    )
                    // Pending Stage
                    TimelineItem(
                        title = "Under Review",
                        subtitle = if (user?.verificationStatus == VerificationStatus.PENDING) "In progress" else "Not started",
                        icon = Icons.Default.Schedule,
                        color = if (user?.verificationStatus == VerificationStatus.PENDING) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        isCompleted = user?.verificationStatus == VerificationStatus.PENDING || user?.verificationStatus == VerificationStatus.VERIFIED || user?.verificationStatus == VerificationStatus.REJECTED
                    )
                    // Final Stage
                    when (user?.verificationStatus) {
                        VerificationStatus.VERIFIED -> TimelineItem(
                            title = "Verified",
                            subtitle = user.kycVerifiedAt?.let { dateFormat.format(Date(it)) } ?: "",
                            icon = Icons.Default.CheckCircle,
                            color = MaterialTheme.colorScheme.primary,
                            isCompleted = true
                        )
                        VerificationStatus.PENDING_UPGRADE -> TimelineItem(
                            title = "Upgrade Pending",
                            subtitle = "Request under review",
                            icon = Icons.Default.Schedule,
                            color = MaterialTheme.colorScheme.tertiary,
                            isCompleted = false
                        )
                        VerificationStatus.REJECTED -> TimelineItem(
                            title = "Rejected",
                            subtitle = user.kycVerifiedAt?.let { dateFormat.format(Date(it)) } ?: "",
                            icon = Icons.Default.Close,
                            color = MaterialTheme.colorScheme.error,
                            isCompleted = true
                        )
                        else -> {}
                    }
                }
            }

            // Status Section
            item {
                when (user?.verificationStatus) {
                    VerificationStatus.UNVERIFIED -> {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Not Started", style = MaterialTheme.typography.headlineSmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Complete verification to unlock full features.")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { user.role.let { onStartVerification(it) } }) {
                                    Text("Start Verification")
                                }
                            }
                        }
                    }
                    VerificationStatus.PENDING -> {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Under Review", style = MaterialTheme.typography.headlineSmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Estimated review time: 24-48 hours")
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { showDocumentDialog = true }) {
                                    Text("View Documents")
                                }
                            }
                        }
                    }
                    VerificationStatus.PENDING_UPGRADE -> {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Upgrade Pending", style = MaterialTheme.typography.headlineSmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Your role upgrade request is being reviewed.")
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                    VerificationStatus.VERIFIED -> {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verified", style = MaterialTheme.typography.headlineSmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Verified on: ${user.kycVerifiedAt?.let { dateFormat.format(Date(it)) } ?: ""}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Benefits unlocked: ${user.role.displayName} features are now available.")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { showDocumentDialog = true }) {
                                    Text("View Documents")
                                }
                            }
                        }
                    }
                    VerificationStatus.REJECTED -> {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Needs Attention", style = MaterialTheme.typography.headlineSmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Rejection reason: ${user.kycRejectionReason ?: "Unknown"}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Please review and resubmit with corrected documents.")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { user.role.let { onResubmit(it) } }) {
                                    Text("Resubmit Verification")
                                }
                            }
                        }
                    }
                    null -> {}
                }
            }

            // Document Summary
            if (user?.verificationStatus != VerificationStatus.UNVERIFIED) {
                item {
                    Text("Document Summary", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Documents: ${docUrls.size}")
                            Text("Images: ${imgUrls.size}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Types:", fontWeight = FontWeight.Bold)
                            for (url in docUrls) {
                                val type = docTypes[url] ?: "Unknown"
                                Text("• $type", color = if (user?.verificationStatus == VerificationStatus.REJECTED) MaterialTheme.colorScheme.error else Color.Unspecified)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDocumentDialog) {
        DocumentPreviewDialog(
            onDismiss = { showDocumentDialog = false },
            documents = allDocuments,
            types = docTypes
        )
    }
}

@Composable
fun DocumentPreviewDialog(
    onDismiss: () -> Unit,
    documents: List<String>,
    types: Map<String, String>
) {
    val context = LocalContext.current
    var selectedDoc by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Verification Documents") },
        text = {
            LazyColumn {
                items(documents) { url ->
                    val type = types[url] ?: if (url.contains("images")) "Image" else "Document"
                    val isImage = url.contains("images") || type in listOf("PHOTO", "FARM_PHOTO")
                    Card(modifier = Modifier.fillMaxWidth().clickable { selectedDoc = url }.padding(8.dp)) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(if (isImage) Icons.Default.Image else Icons.Default.Description, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(type)
                                // File size metadata would require fetching file info; placeholder for now
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { downloadDocument(context, url) }) {
                                Icon(Icons.Default.Download, contentDescription = "Download")
                            }
                            IconButton(onClick = { shareDocument(context, url) }) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

    if (selectedDoc != null) {
        DocumentViewerDialog(
            url = selectedDoc!!,
            isImage = selectedDoc!!.contains("images") || ((types[selectedDoc!!] ?: "") in listOf("PHOTO", "FARM_PHOTO")),
            onDismiss = { selectedDoc = null }
        )
    }
}
@Composable
fun DocumentViewerDialog(
    url: String,
    isImage: Boolean,
    onDismiss: () -> Unit
) {
    // TODO: Track document view analytics (implement in VerificationViewModel)
    androidx.compose.runtime.LaunchedEffect(url) {
        android.util.Log.d("Analytics", "Document viewed: $url")
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Document Preview") },
        text = {
            if (isImage) {
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                    scale *= zoomChange
                    offset += offsetChange
                }
                AsyncImage(
                    model = androidx.compose.ui.platform.LocalContext.current.let {
                        coil.request.ImageRequest.Builder(it)
                            .data(url)
                            .crossfade(true)
                            .build()
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state)
                )
            } else {
                // Use WebView with Google Docs viewer for PDF preview (alternative to AndroidPdfViewer for URL-based loading)
                AndroidView(
                    factory = { WebView(it).apply { loadUrl("https://docs.google.com/viewer?url=$url") } },
                    modifier = Modifier.fillMaxWidth().height(400.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun downloadDocument(context: Context, url: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Verification Document")
        .setDescription("Downloading verification document")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
}

fun shareDocument(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
    context.startActivity(Intent.createChooser(intent, "Share Document"))
}

@Composable
fun TimelineItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isCompleted: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
