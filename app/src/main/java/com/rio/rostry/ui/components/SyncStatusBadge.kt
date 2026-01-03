package com.rio.rostry.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sync Status Badge - Shows offline/online status and pending changes count.
 * 
 * Displays:
 * - Green dot: Online, all synced
 * - Orange dot: Online, changes pending
 * - Red dot: Offline
 * - Spinning: Currently syncing
 */

sealed class SyncStatus {
    data object Online : SyncStatus()
    data class Pending(val count: Int) : SyncStatus()
    data object Syncing : SyncStatus()
    data object Offline : SyncStatus()
}

@Composable
fun SyncStatusBadge(
    status: SyncStatus,
    lastSyncTime: String? = null,
    onSyncClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (color, icon, label) = when (status) {
        is SyncStatus.Online -> Triple(Color(0xFF4CAF50), Icons.Default.CloudDone, "Synced")
        is SyncStatus.Pending -> Triple(Color(0xFFFF9800), Icons.Default.CloudUpload, "${status.count} pending")
        is SyncStatus.Syncing -> Triple(Color(0xFF2196F3), Icons.Default.Sync, "Syncing...")
        is SyncStatus.Offline -> Triple(Color(0xFFE53935), Icons.Default.CloudOff, "Offline")
    }
    
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(300),
        label = "sync_color"
    )
    
    // Spinning animation for syncing
    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(animatedColor.copy(alpha = 0.15f))
            .then(
                if (onSyncClick != null) Modifier.clickable { onSyncClick() }
                else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Status dot with animation
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(animatedColor)
        )
        
        // Icon (spinning if syncing)
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .size(16.dp)
                .then(
                    if (status is SyncStatus.Syncing) 
                        Modifier.graphicsLayer { rotationZ = rotation }
                    else Modifier
                ),
            tint = animatedColor
        )
        
        // Label
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = animatedColor
        )
    }
}

/**
 * Compact version for toolbar use
 */
@Composable
fun SyncStatusDot(
    status: SyncStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        is SyncStatus.Online -> Color(0xFF4CAF50)
        is SyncStatus.Pending -> Color(0xFFFF9800)
        is SyncStatus.Syncing -> Color(0xFF2196F3)
        is SyncStatus.Offline -> Color(0xFFE53935)
    }
    
    // Pulse animation for pending/syncing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    
    val shouldPulse = status is SyncStatus.Pending || status is SyncStatus.Syncing
    
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = if (shouldPulse) alpha else 1f))
    )
}

/**
 * Full sync panel for settings/profile screen
 */
@Composable
fun SyncStatusPanel(
    status: SyncStatus,
    pendingChanges: List<String> = emptyList(),
    lastSyncTime: String? = null,
    onSyncNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sync Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                SyncStatusBadge(status = status)
            }
            
            if (lastSyncTime != null) {
                Text(
                    text = "Last sync: $lastSyncTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (pendingChanges.isNotEmpty()) {
                Text(
                    text = "Pending changes:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                pendingChanges.take(3).forEach { change ->
                    Text(
                        text = "• $change",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (pendingChanges.size > 3) {
                    Text(
                        text = "... and ${pendingChanges.size - 3} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Button(
                onClick = onSyncNow,
                enabled = status !is SyncStatus.Syncing && status !is SyncStatus.Offline,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync Now")
            }
        }
    }
}
