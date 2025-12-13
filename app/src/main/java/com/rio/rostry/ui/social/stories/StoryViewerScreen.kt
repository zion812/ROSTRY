package com.rio.rostry.ui.social.stories

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.StoryEntity
import kotlinx.coroutines.delay

import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.social.SocialFeedViewModel

@Composable
fun StoryViewerScreen(
    initialIndex: Int = 0,
    onFinished: () -> Unit,
    vm: SocialFeedViewModel = hiltViewModel()
) {
    val stories by vm.activeStories.collectAsState(initial = emptyList())
    
    if (stories.isEmpty()) {
        // Show loading or just finish if really empty and not loading
        // For now, if empty, we might want to wait or return
        // But if we navigated here, we expect stories.
        // If it's async, we might show loader.
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
             CircularProgressIndicator()
             IconButton(onClick = onFinished, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                  Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
             }
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    var isPaused by remember { mutableStateOf(false) }

    val currentStory = stories.getOrNull(currentIndex) ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        if (offset.x < size.width / 2) {
                            // Previous
                            if (currentIndex > 0) currentIndex--
                        } else {
                            // Next
                            if (currentIndex < stories.size - 1) currentIndex++ else onFinished()
                        }
                    },
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    }
                )
            }
    ) {
        // Media
        AsyncImage(
            model = currentStory.mediaUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            // Progress Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stories.forEachIndexed { index, _ ->
                    StoryProgressBar(
                        modifier = Modifier.weight(1f),
                        state = when {
                            index < currentIndex -> ProgressState.COMPLETED
                            index == currentIndex -> if (isPaused) ProgressState.PAUSED else ProgressState.ACTIVE
                            else -> ProgressState.IDLE
                        },
                        onFinished = {
                            if (index == currentIndex && !isPaused) {
                                if (currentIndex < stories.size - 1) currentIndex++ else onFinished()
                            }
                        }
                    )
                }
            }

            // User Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                // Avatar placeholder
                Box(Modifier.size(32.dp).clip(RoundedCornerShape(16.dp)).background(Color.Gray))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = currentStory.authorId.take(10), // Replace with name lookup
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onFinished) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}

enum class ProgressState { IDLE, ACTIVE, PAUSED, COMPLETED }

@Composable
fun StoryProgressBar(
    modifier: Modifier,
    state: ProgressState,
    durationMs: Int = 5000,
    onFinished: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(state) {
        when (state) {
            ProgressState.IDLE -> progress = 0f
            ProgressState.COMPLETED -> progress = 1f
            ProgressState.ACTIVE -> {
                val startTime = System.currentTimeMillis()
                while (progress < 1f) {
                    val elapsed = System.currentTimeMillis() - startTime
                    progress = (elapsed.toFloat() / durationMs).coerceAtMost(1f)
                    delay(16) // ~60fps
                }
                onFinished()
            }
            ProgressState.PAUSED -> {
                // Hold progress
            }
        }
    }

    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier.height(2.dp),
        color = Color.White,
        trackColor = Color.White.copy(alpha = 0.3f),
    )
}
