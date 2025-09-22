package com.rio.rostry.ui.social

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun VideoPlayer(modifier: Modifier = Modifier, url: String) {
    val context = LocalContext.current
    val player = ExoPlayer.Builder(context).build().apply {
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        setMediaItem(mediaItem)
        prepare()
        playWhenReady = false
    }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                useController = true
                this.player = player
            }
        },
        update = { it.player = player }
    )
}
