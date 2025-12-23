package com.rio.rostry.ui.verification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun UploadRow(
    label: String,
    isUploaded: Boolean,
    uploadUrl: String?,
    onUpload: () -> Unit,
    onDelete: () -> Unit,
    isImage: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (isUploaded) {
                Text("✓ Uploaded", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            } else {
                Text("Required", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
        
        if (isUploaded && uploadUrl != null) {
            UploadedItem(url = uploadUrl, onDelete = onDelete, isImage = isImage)
        } else {
            OutlinedButton(onClick = onUpload, shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Upload")
            }
        }
    }
}

@Composable
fun UploadedItem(url: String, onDelete: () -> Unit, isImage: Boolean, readOnly: Boolean = false) {
    var imageLoadError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    // Request only the size needed for the thumbnail (60dp * ~3x density = ~180px)
    // This prevents Coil from loading a multi-megabyte image into memory
    val model = remember(url) {
        ImageRequest.Builder(context)
            .data(url)
            .size(180) // Request only 180x180 pixels max
            .crossfade(true)
            .memoryCacheKey("thumb_$url") // Separate cache key for thumbnails
            .build()
    }

    Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
        if (isImage) {
             if (!imageLoadError) {
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onError = { imageLoadError = true },
                    onSuccess = { imageLoadError = false }
                )
             } else {
                 Icon(Icons.Default.Image, "Image error", modifier = Modifier.align(Alignment.Center), tint = MaterialTheme.colorScheme.error)
             }
        } else {
            Icon(Icons.Default.Description, null, modifier = Modifier.align(Alignment.Center))
        }
        
        if (!readOnly) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd).size(20.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
    }
}
