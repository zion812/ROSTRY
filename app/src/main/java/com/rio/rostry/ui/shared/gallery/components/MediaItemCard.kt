package com.rio.rostry.ui.shared.gallery.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rio.rostry.domain.model.media.MediaItem
import com.rio.rostry.domain.model.media.MediaType

@Composable
fun MediaItemCard(
    mediaItem: MediaItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditCaption: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick) // Add Foundation combinedClickable for long press in real impl
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        val imageModel = if (mediaItem.isCached && mediaItem.localPath != null) {
            java.io.File(mediaItem.localPath)
        } else {
            mediaItem.thumbnailUrl ?: mediaItem.url
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageModel)
                .crossfade(true)
                .build(),
            contentDescription = "Media item",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Asset Identifier Badge (Top Left)
        if (!mediaItem.assetIdentifier.isNullOrBlank() || !mediaItem.assetName.isNullOrBlank()) {
            val displayText = mediaItem.assetIdentifier ?: mediaItem.assetName ?: ""
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = displayText,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Video Indicator
        if (mediaItem.mediaType == MediaType.VIDEO) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Video",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                val duration = mediaItem.duration
                if (duration != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${duration / 60}:${String.format("%02d", duration % 60)}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Caption and Edit Button
        if (mediaItem.caption != null || onEditCaption != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mediaItem.caption ?: "Add caption",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    maxLines = 1
                )
                if (onEditCaption != null) {
                    IconButton(
                        onClick = onEditCaption,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Caption",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Selection Overlay
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
            )
        }
    }
}
