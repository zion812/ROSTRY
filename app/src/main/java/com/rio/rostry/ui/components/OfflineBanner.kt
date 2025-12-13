package com.rio.rostry.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OfflineBanner(
    onViewPending: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SyncStatusViewModel = hiltViewModel()
    val syncState by viewModel.syncState.collectAsState()

    var dismissed by rememberSaveable { mutableStateOf(false) }

    val shouldShow = remember(syncState.status, dismissed) {
        (syncState.status == SyncStatusViewModel.SyncStatus.OFFLINE ||
                syncState.status == SyncStatusViewModel.SyncStatus.SYNCING ||
                syncState.status == SyncStatusViewModel.SyncStatus.ERROR) && !dismissed
    }

    // Reset dismissed when not offline or syncing (i.e., when back online or synced)
    LaunchedEffect(syncState.status) {
        if (syncState.status == SyncStatusViewModel.SyncStatus.SYNCED) {
            dismissed = false
        }
    }

    AnimatedVisibility(visible = shouldShow) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (syncState.status == SyncStatusViewModel.SyncStatus.ERROR) 
                    MaterialTheme.colorScheme.errorContainer 
                else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (syncState.status == SyncStatusViewModel.SyncStatus.ERROR) 
                    MaterialTheme.colorScheme.onErrorContainer 
                else MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (syncState.status) {
                        SyncStatusViewModel.SyncStatus.ERROR -> androidx.compose.material.icons.Icons.Default.Close // Or Error icon
                        else -> Icons.Outlined.CloudOff
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when (syncState.status) {
                        SyncStatusViewModel.SyncStatus.SYNCING -> "Syncing ${syncState.pendingCount} updates..."
                        SyncStatusViewModel.SyncStatus.OFFLINE -> if (syncState.pendingCount > 0) {
                            "You're offline. ${syncState.pendingCount} updates pending sync."
                        } else {
                            "You're offline. Changes will sync when connected."
                        }
                        SyncStatusViewModel.SyncStatus.ERROR -> syncState.errorMessage ?: "Sync failed. Retry?"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (syncState.status == SyncStatusViewModel.SyncStatus.SYNCING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    TextButton(onClick = onViewPending) {
                        Text("View pending")
                    }
                }
                IconButton(onClick = { dismissed = true }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                        contentDescription = "Dismiss"
                    )
                }
            }
        }
    }
}