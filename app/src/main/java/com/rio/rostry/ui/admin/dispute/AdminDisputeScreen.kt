package com.rio.rostry.ui.admin.dispute

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDisputeScreen(
    viewModel: AdminDisputeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dispute Resolution") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.disputes.isEmpty() && !state.isLoading) {
                 item { Text("No open disputes.") }
            }
            
            items(state.disputes) { dispute ->
                DisputeCard(
                    dispute = dispute,
                    onResolve = { status, notes -> 
                        viewModel.resolveDispute(dispute.disputeId, status, notes) 
                    }
                )
            }
        }
    }
}

@Composable
fun DisputeCard(
    dispute: DisputeEntity,
    onResolve: (DisputeStatus, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Icon(Icons.Default.Gavel, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(dispute.reason, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Reporter: ${dispute.reporterId}")
            Text("Against: ${dispute.reportedUserId}")
            Text("Transfer: ${dispute.transferId}")
            Spacer(Modifier.height(8.dp))
            Text(dispute.description)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { showDialog = true }) {
                Text("Investigate / Resolve")
            }
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Resolve Dispute") },
            text = {
                Column {
                    Text("Action:")
                    Row {
                        Button(onClick = { 
                            onResolve(DisputeStatus.RESOLVED_REFUNDED, notes)
                            showDialog = false
                        }) { Text("Refund") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { 
                            onResolve(DisputeStatus.RESOLVED_DISMISSED, notes) 
                            showDialog = false
                        }) { Text("Dismiss") }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes, 
                        onValueChange = { notes = it },
                        label = { Text("Admin Notes") }
                    )
                }
            },
            confirmButton = {}
        )
    }
}
