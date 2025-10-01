package com.rio.rostry.ui.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
            LazyColumn(Modifier.weight(1f)) {
                items(msgs) { m ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text(
                                m.fromUserId.take(8),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(m.text, style = MaterialTheme.typography.bodyMedium)
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
                            vm.sendQueuedDm(threadId = threadId, fromUserId = "me", toUserId = "them", text = input)
                            input = ""
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
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
