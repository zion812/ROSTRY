package com.rio.rostry.ui.messaging

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.components.SyncState
import com.rio.rostry.ui.components.SyncStatusBadge
import com.rio.rostry.ui.components.ConflictNotification

@Composable
fun GroupChatScreen(groupId: String, onBack: () -> Unit, vm: GroupChatViewModel = hiltViewModel()) {
    LaunchedEffect(groupId) { vm.bind(groupId) }
    val msgs by vm.messages.collectAsState()
    val isOnline by vm.isOnline.collectAsState()
    val pendingCount by vm.pendingMessagesCount.collectAsState()
    val pendingOutboxIds by vm.pendingOutboxIds.collectAsState()
    val conflict by vm.conflictDetails.collectAsState()
    var input by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        // Conflict notification for chat messages
        conflict?.let { c ->
            ConflictNotification(
                conflict = c,
                onDismiss = { vm.dismissConflict() },
                onViewDetails = { vm.viewConflictDetails(c.entityId) }
            )
        }
        if (!isOnline) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Text("Offline - Messages will send when online")
            }
        }
        if (pendingCount > 0) {
            Card {
                Text("${pendingCount} messages pending sync", style = MaterialTheme.typography.bodySmall)
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(msgs) { m ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${m.fromUserId.take(6)}: ${m.text}", modifier = Modifier.weight(1f))
                    // Approximate sync state based on pending outbox entries for this message id
                    val syncState = if (pendingOutboxIds.contains(m.messageId)) SyncState.PENDING else SyncState.SYNCED
                    if (syncState == SyncState.PENDING) {
                        SyncStatusBadge(syncState = syncState)
                    }
                    if (syncState == SyncState.CONFLICT) {
                        IconButton(onClick = { vm.retryFailedMessage(m.messageId) }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry")
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (input.isNotBlank()) {
                    vm.sendQueuedGroup(groupId = groupId, fromUserId = "me", text = input)
                    input = ""
                }
            }, modifier = Modifier.padding(start = 8.dp)) { Text("Send") }
        }
    }
}