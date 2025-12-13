package com.rio.rostry.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rio.rostry.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SyncStatusIndicator(
    syncState: SyncStatusViewModel.SyncState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Shake animation for error state
    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 500
                0f at 0
                10f at 100
                -10f at 200
                10f at 300
                -10f at 400
                0f at 500
            },
            repeatMode = RepeatMode.Restart
        )
    )

    // Pulse animation for syncing state (applied to the entire indicator)
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val isPulsing = syncState.status == SyncStatusViewModel.SyncStatus.SYNCING

    AnimatedContent(
        targetState = syncState.status,
        transitionSpec = { fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300)) },
        modifier = modifier
            .alpha(if (isPulsing) pulseAlpha else 1f)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics {
                contentDescription = when (syncState.status) {
                    SyncStatusViewModel.SyncStatus.SYNCED -> "All changes synced"
                    SyncStatusViewModel.SyncStatus.SYNCING -> "Syncing ${syncState.pendingCount} items"
                    SyncStatusViewModel.SyncStatus.OFFLINE -> "You're offline. ${syncState.pendingCount} updates pending sync"
                    SyncStatusViewModel.SyncStatus.ERROR -> "Sync failed. Tap to retry"
                }
            }
    ) { status ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            when (status) {
                SyncStatusViewModel.SyncStatus.SYNCED -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "All changes synced",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green
                    )
                }
                SyncStatusViewModel.SyncStatus.SYNCING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Syncing ${syncState.pendingCount} items...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                SyncStatusViewModel.SyncStatus.OFFLINE -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFF9800), // Orange
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "You're offline. ${syncState.pendingCount} pending",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                }
                SyncStatusViewModel.SyncStatus.ERROR -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = shakeOffset.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sync failed. Tap to retry",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
        }
    }
}