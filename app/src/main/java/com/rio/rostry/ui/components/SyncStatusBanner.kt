package com.rio.rostry.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * SyncStatusBanner: Persistent banner showing sync status with pending count and actions.
 * 
 * States:
 * - Synced: Green checkmark, hidden by default
 * - Syncing: Progress indicator
 * - Pending: Yellow warning with count
 * - Error: Red error with retry button
 */
sealed class BannerSyncState {
    object Idle : BannerSyncState()
    object Syncing : BannerSyncState()
    object Synced : BannerSyncState()
    data class Pending(val count: Int) : BannerSyncState()
    data class Error(val message: String) : BannerSyncState()
}

@Composable
fun SyncStatusBanner(
    syncState: BannerSyncState,
    pendingCount: Int = 0,
    lastSyncTime: Long? = null,
    onSyncNow: () -> Unit,
    onViewConflicts: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val showBanner = syncState !is BannerSyncState.Idle && 
                     (syncState !is BannerSyncState.Synced || pendingCount > 0)
    
    AnimatedVisibility(
        visible = showBanner,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (syncState) {
                    is BannerSyncState.Synced -> Color(0xFFE8F5E9)
                    is BannerSyncState.Pending -> Color(0xFFFFF8E1)
                    is BannerSyncState.Error -> Color(0xFFFFEBEE)
                    is BannerSyncState.Syncing -> Color(0xFFE3F2FD)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Status icon
                    when (syncState) {
                        is BannerSyncState.Synced -> Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Synced",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        is BannerSyncState.Syncing -> CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        is BannerSyncState.Pending -> Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = "Pending",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(20.dp)
                        )
                        is BannerSyncState.Error -> Icon(
                            Icons.Default.CloudOff,
                            contentDescription = "Error",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                        else -> {}
                    }
                    
                    Spacer(Modifier.width(12.dp))
                    
                    // Status text
                    Column {
                        Text(
                            text = when (syncState) {
                                is BannerSyncState.Synced -> "All synced"
                                is BannerSyncState.Syncing -> "Syncing..."
                                is BannerSyncState.Pending -> "${pendingCount} items pending"
                                is BannerSyncState.Error -> "Sync failed"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        // Secondary text
                        when {
                            syncState is BannerSyncState.Error -> Text(
                                text = syncState.message.take(50),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            lastSyncTime != null -> Text(
                                text = "Last sync: ${formatSyncTime(lastSyncTime)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                
                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (syncState is BannerSyncState.Error || syncState is BannerSyncState.Pending) {
                        TextButton(
                            onClick = onSyncNow,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Sync Now")
                        }
                    }
                    
                    if (pendingCount > 0 && syncState !is BannerSyncState.Syncing) {
                        IconButton(
                            onClick = onViewConflicts,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "View details",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact version for use in app bars or smaller spaces.
 */
@Composable
fun SyncStatusChip(
    syncState: BannerSyncState,
    pendingCount: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when (syncState) {
        is BannerSyncState.Synced -> Color(0xFF4CAF50)
        is BannerSyncState.Syncing -> Color(0xFF2196F3)
        is BannerSyncState.Pending -> Color(0xFFFFC107)
        is BannerSyncState.Error -> Color(0xFFF44336)
        else -> Color.Gray
    }
    
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = containerColor.copy(alpha = 0.2f),
        contentColor = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (syncState) {
                is BannerSyncState.Syncing -> CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 1.5.dp,
                    color = containerColor
                )
                is BannerSyncState.Synced -> Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                is BannerSyncState.Pending -> Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                is BannerSyncState.Error -> Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                else -> {}
            }
            
            if (pendingCount > 0 && syncState is BannerSyncState.Pending) {
                Text(
                    text = pendingCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatSyncTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}

