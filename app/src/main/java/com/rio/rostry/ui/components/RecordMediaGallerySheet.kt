package com.rio.rostry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/**
 * Media data class with metadata for records
 */
data class MediaItem(
    val url: String,
    val caption: String? = null,
    val timestamp: Long? = null,
    val recordType: String? = null,
    val recordId: String? = null
)

/**
 * Full-screen media gallery sheet for viewing record media.
 * Features: swipe between photos, zoom/pan, captions, delete, share.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordMediaGallerySheet(
    mediaItems: List<MediaItem>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit,
    onDelete: ((Int) -> Unit)? = null,
    onShare: ((MediaItem) -> Unit)? = null,
    showActions: Boolean = true
) {
    if (mediaItems.isEmpty()) {
        onDismiss()
        return
    }

    val pagerState = rememberPagerState(initialPage = initialIndex) { mediaItems.size }
    val scope = rememberCoroutineScope()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val currentItem = mediaItems.getOrNull(pagerState.currentPage)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Black.copy(alpha = 0.95f),
        contentColor = Color.White,
        dragHandle = null,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Close", tint = Color.White)
                }
                
                Text(
                    "${pagerState.currentPage + 1} / ${mediaItems.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                
                if (showActions) {
                    Row {
                        if (onShare != null && currentItem != null) {
                            IconButton(onClick = { onShare(currentItem) }) {
                                Icon(Icons.Default.Share, "Share", tint = Color.White)
                            }
                        }
                        if (onDelete != null) {
                            IconButton(onClick = { showDeleteConfirm = true }) {
                                Icon(Icons.Default.Delete, "Delete", tint = Color.White)
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.width(48.dp))
                }
            }

            // Image Pager with zoom support
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                ZoomableImage(
                    url = mediaItems[page].url,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom Info Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp)
            ) {
                currentItem?.caption?.let { caption ->
                    Text(
                        caption,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                }
                
                currentItem?.recordType?.let { type ->
                    Text(
                        type.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Thumbnail indicators
                if (mediaItems.size > 1) {
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(mediaItems) { index, item ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == pagerState.currentPage) 52.dp else 48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (index == pagerState.currentPage)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        else
                                            Color.Transparent
                                    )
                                    .clickable {
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }
                            ) {
                                AsyncImage(
                                    model = item.url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Photo") },
            text = { Text("Are you sure you want to delete this photo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(pagerState.currentPage)
                        showDeleteConfirm = false
                        if (mediaItems.size <= 1) {
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Zoomable image with pinch-to-zoom and pan gestures
 */
@Composable
private fun ZoomableImage(
    url: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 4f)
                    if (scale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )
    }
}

/**
 * Simple thumbnail gallery for showing media in cards
 */
@Composable
fun MediaThumbnailRow(
    urls: List<String>,
    maxVisible: Int = 4,
    onViewGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (urls.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val visibleUrls = urls.take(maxVisible)
        val extraCount = (urls.size - maxVisible).coerceAtLeast(0)

        visibleUrls.forEachIndexed { index, url ->
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onViewGallery() }
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Show "+N" overlay on last visible item if there are more
                if (index == visibleUrls.lastIndex && extraCount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+$extraCount",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
