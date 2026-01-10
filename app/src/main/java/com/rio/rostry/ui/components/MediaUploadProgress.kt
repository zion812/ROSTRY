package com.rio.rostry.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * MediaUploadProgress: Shows upload progress for photos in MediaUploadWorker.
 * 
 * Features:
 * - Thumbnail with progress bar overlay
 * - Cancel pending uploads
 * - Retry failed uploads
 * - Batch upload progress summary
 */
sealed class UploadStatus {
    object Pending : UploadStatus()
    data class Uploading(val progress: Float) : UploadStatus()
    object Completed : UploadStatus()
    data class Failed(val error: String) : UploadStatus()
    object Cancelled : UploadStatus()
}

data class MediaUploadItem(
    val id: String,
    val localUri: String,
    val fileName: String,
    val status: UploadStatus = UploadStatus.Pending,
    val sizeBytes: Long = 0L
)

@Composable
fun MediaUploadProgress(
    items: List<MediaUploadItem>,
    onCancel: (String) -> Unit,
    onRetry: (String) -> Unit,
    onCancelAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = items.count { it.status is UploadStatus.Completed }
    val failedCount = items.count { it.status is UploadStatus.Failed }
    val pendingCount = items.count { it.status is UploadStatus.Pending || it.status is UploadStatus.Uploading }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Summary header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Uploading ${items.size} files",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "$completedCount completed, $pendingCount pending" + 
                            if (failedCount > 0) ", $failedCount failed" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                if (pendingCount > 0) {
                    TextButton(onClick = onCancelAll) {
                        Text("Cancel All")
                    }
                }
            }
            
            // Overall progress bar
            val overallProgress = if (items.isNotEmpty()) {
                items.sumOf { item ->
                    when (val status = item.status) {
                        is UploadStatus.Completed -> 100
                        is UploadStatus.Uploading -> (status.progress * 100).toInt()
                        else -> 0
                    }
                } / (items.size * 100f)
            } else 0f
            
            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(Modifier.height(8.dp))
        
        // Individual items
        items.forEach { item ->
            MediaUploadItemCard(
                item = item,
                onCancel = { onCancel(item.id) },
                onRetry = { onRetry(item.id) }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MediaUploadItemCard(
    item: MediaUploadItem,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with progress overlay
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = item.localUri,
                    contentDescription = item.fileName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Progress overlay
                when (val status = item.status) {
                    is UploadStatus.Uploading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { status.progress },
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    is UploadStatus.Completed -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x8004B04B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    is UploadStatus.Failed -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x80F44336)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Failed",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    is UploadStatus.Pending -> {
                        // Pulsing pending indicator
                        val infiniteTransition = rememberInfiniteTransition(label = "pending")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 0.7f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alpha"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = alpha)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HourglassEmpty,
                                contentDescription = "Pending",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    else -> {}
                }
            }
            
            Spacer(Modifier.width(12.dp))
            
            // File info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    when (val status = item.status) {
                        is UploadStatus.Pending -> "Waiting..."
                        is UploadStatus.Uploading -> "${(status.progress * 100).toInt()}%"
                        is UploadStatus.Completed -> "Completed"
                        is UploadStatus.Failed -> status.error.take(30)
                        is UploadStatus.Cancelled -> "Cancelled"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when (item.status) {
                        is UploadStatus.Completed -> Color(0xFF4CAF50)
                        is UploadStatus.Failed -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outline
                    }
                )
                
                // Size
                if (item.sizeBytes > 0) {
                    Text(
                        formatFileSize(item.sizeBytes),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            // Action button
            when (item.status) {
                is UploadStatus.Pending, is UploadStatus.Uploading -> {
                    IconButton(onClick = onCancel) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is UploadStatus.Failed -> {
                    IconButton(onClick = onRetry) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

/**
 * Compact inline progress for use in forms.
 */
@Composable
fun MediaUploadInlineProgress(
    uploadingCount: Int,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    if (uploadingCount > 0 || completedCount < totalCount) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uploadingCount > 0) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                "Uploading $completedCount of $totalCount",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(Modifier.weight(1f))
            
            LinearProgressIndicator(
                progress = { completedCount.toFloat() / totalCount.coerceAtLeast(1) },
                modifier = Modifier
                    .width(60.dp)
                    .height(4.dp)
            )
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
