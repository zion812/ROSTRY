package com.rio.rostry.ui.shared.gallery.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rio.rostry.domain.model.media.MediaItem

@Composable
fun VideoPlayerScreen(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    // Basic placeholder for Video Player
    // In a real app, this would use ExoPlayer or a similar video playback library
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Video Playback for ${mediaItem.mediaId}\nURL: ${mediaItem.url}",
            color = Color.White
        )
    }
}
