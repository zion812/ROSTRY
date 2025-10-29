package com.rio.rostry.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.ChatMessageEntity
import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.data.database.entity.TransferEntity

enum class SyncState {
    PENDING,
    SYNCED,
    CONFLICT
}

data class ConflictDetails(val entityType: String, val entityId: String, val conflictFields: List<String>, val mergedAt: Long, val message: String)

// Shared sync state helper used by UI and ViewModels
fun getSyncState(log: DailyLogEntity): SyncState {
    return when {
        log.dirty && log.syncedAt == null -> SyncState.PENDING
        !log.dirty && log.syncedAt != null -> SyncState.SYNCED
        log.updatedAt > (log.syncedAt ?: 0) && log.dirty -> SyncState.CONFLICT
        else -> SyncState.SYNCED
    }
}

fun getSyncState(transfer: TransferEntity): SyncState {
    return when {
        transfer.dirty && transfer.syncedAt == null -> SyncState.PENDING
        !transfer.dirty && transfer.syncedAt != null -> SyncState.SYNCED
        transfer.updatedAt > (transfer.syncedAt ?: 0) && transfer.dirty -> SyncState.CONFLICT
        else -> SyncState.SYNCED
    }
}

fun getSyncState(message: ChatMessageEntity): SyncState {
    return when {
        message.dirty && message.syncedAt == null -> SyncState.PENDING
        !message.dirty && message.syncedAt != null -> SyncState.SYNCED
        message.updatedAt > (message.syncedAt ?: 0) && message.dirty -> SyncState.CONFLICT
        else -> SyncState.SYNCED
    }
}

// Overload for generic entities (e.g., TaskEntity)
fun getSyncState(dirty: Boolean, syncedAt: Long?, updatedAt: Long): SyncState {
    return when {
        dirty && syncedAt == null -> SyncState.PENDING
        !dirty && syncedAt != null -> SyncState.SYNCED
        updatedAt > (syncedAt ?: 0L) && dirty -> SyncState.CONFLICT
        else -> SyncState.SYNCED
    }
}

@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun LoadingOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Info, contentDescription = "Info")
            Spacer(Modifier.height(4.dp))
        }
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SyncStatusBadge(syncState: SyncState, modifier: Modifier = Modifier) {
    val (color, icon, text) = when (syncState) {
        SyncState.PENDING -> Triple(
            MaterialTheme.colorScheme.tertiary,
            Icons.Filled.Schedule,
            "Syncing..."
        )
        SyncState.SYNCED -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Filled.Check,
            "Synced"
        )
        SyncState.CONFLICT -> Triple(
            MaterialTheme.colorScheme.error,
            Icons.Filled.Warning,
            "Conflict"
        )
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier.height(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = text, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun ConflictNotification(conflict: ConflictDetails, onDismiss: () -> Unit, onViewDetails: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = conflict.message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
            if (conflict.conflictFields.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Affected fields: ${conflict.conflictFields.joinToString(", ")}",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDismiss) {
                    Text("Dismiss")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onViewDetails) {
                    Text("View Details")
                }
            }
        }
    }
}
