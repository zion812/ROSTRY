package com.rio.rostry.ui.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadScreen(threadId: String, onBack: () -> Unit, vm: ThreadViewModel = hiltViewModel()) {
    LaunchedEffect(threadId) { vm.bind(threadId) }
    val msgs by vm.messages.collectAsState()

    val metadata by vm.threadMetadata.collectAsState()
    val currentUserId by vm.currentUserId.collectAsState()
    var input by remember { mutableStateOf("") }
    var showTitleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(metadata?.title ?: "Thread $threadId")
                        metadata?.context?.type?.let { contextType ->
                            Text(
                                when (contextType) {
                                    "PRODUCT_INQUIRY" -> "Product Inquiry"
                                    "EXPERT_CONSULT" -> "Expert Consultation"
                                    "BREEDING_DISCUSSION" -> "Breeding Discussion"
                                    else -> "Conversation"
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showTitleDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Title")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            // Header section with context info
            metadata?.let { meta ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        meta.context?.type?.let { contextType ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Badge {
                                    Text(
                                        when (contextType) {
                                            "PRODUCT_INQUIRY" -> "Product Inquiry"
                                            "EXPERT_CONSULT" -> "Expert Consultation"
                                            "BREEDING_DISCUSSION" -> "Breeding Discussion"
                                            else -> "General Chat"
                                        }
                                    )
                                }
                                Text(
                                    "Participants: ${meta.participantIds.size}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        meta.context?.topic?.let { topic ->
                            Text(
                                "Topic: $topic",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Messages list
            if (msgs.isEmpty()) {
                com.rio.rostry.ui.components.EmptyState(
                    title = "No messages yet",
                    subtitle = "Say hi to start the conversation",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = msgs, key = { it.hashCode() }) { m ->
                        val isMe = m.fromUserId == currentUserId
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            if (m.type == "OFFER") {
                                OfferCard(
                                    message = m,
                                    isMe = isMe,
                                    onAccept = { vm.acceptOffer(m) },
                                    onReject = { vm.rejectOffer(m) },
                                    onCounter = { 
                                        // For now, just send a message indicating counter offer intent
                                        vm.sendQueuedDm(threadId, currentUserId ?: "me", m.fromUserId, "I would like to propose a different price.")
                                    }
                                )
                            } else {
                                Card(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .padding(4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(m.text, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(m.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input row
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") }
                )
                Button(
                    onClick = {
                        if (input.isNotBlank()) {
                            vm.sendQueuedDm(threadId = threadId, fromUserId = currentUserId ?: "me", toUserId = "them", text = input)
                            input = ""
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                    Spacer(Modifier.width(6.dp))
                    Text("Send")
                }
            }
        }
    }

    // Title edit dialog
    if (showTitleDialog) {
        var newTitle by remember { mutableStateOf(metadata?.title ?: "") }
        AlertDialog(
            onDismissRequest = { showTitleDialog = false },
            title = { Text("Edit Thread Title") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Title") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.updateThreadTitle(threadId, newTitle)
                    showTitleDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTitleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun OfferCard(
    message: com.rio.rostry.data.repository.social.MessagingRepository.MessageDTO,
    isMe: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCounter: () -> Unit
) {
    val offerDetails = remember(message.metadata) {
        try {
            org.json.JSONObject(message.metadata ?: "{}")
        } catch (e: Exception) {
            org.json.JSONObject()
        }
    }
    val price = offerDetails.optDouble("price", 0.0)
    val quantity = offerDetails.optDouble("quantity", 0.0)
    val unit = offerDetails.optString("unit", "")

    Card(
        modifier = Modifier
            .widthIn(max = 300.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = if (isMe) "You sent an offer" else "Received an Offer",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹$price",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "for $quantity $unit",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            if (!isMe) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Accept")
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCounter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Counter Offer")
                }
            } else {
                Text(
                    text = "Waiting for seller response...",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
