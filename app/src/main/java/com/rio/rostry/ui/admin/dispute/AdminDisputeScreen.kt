package com.rio.rostry.ui.admin.dispute

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.DisputeStatus
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDisputeScreen(
    viewModel: AdminDisputeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val tabTitles = listOf("Open (${state.openDisputes.size})", "Resolved (${state.resolvedDisputes.size})")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dispute Resolution") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadDisputes() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = if (state.currentFilter == AdminDisputeViewModel.DisputeFilter.OPEN) 0 else 1
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = (state.currentFilter == AdminDisputeViewModel.DisputeFilter.OPEN && index == 0) ||
                                   (state.currentFilter == AdminDisputeViewModel.DisputeFilter.RESOLVED && index == 1),
                        onClick = {
                            viewModel.onFilterChanged(
                                if (index == 0) AdminDisputeViewModel.DisputeFilter.OPEN 
                                else AdminDisputeViewModel.DisputeFilter.RESOLVED
                            )
                        },
                        text = { Text(title) }
                    )
                }
            }

            // Content
            val currentList = if (state.currentFilter == AdminDisputeViewModel.DisputeFilter.OPEN) 
                state.openDisputes else state.resolvedDisputes

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && currentList.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (currentList.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (state.currentFilter == AdminDisputeViewModel.DisputeFilter.OPEN) 
                                Icons.Default.Check else Icons.Default.Gavel,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (state.currentFilter == AdminDisputeViewModel.DisputeFilter.OPEN) 
                                "No open disputes" else "No resolved disputes",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentList, key = { it.disputeId }) { dispute ->
                            DisputeCard(
                                dispute = dispute,
                                isHistory = state.currentFilter == AdminDisputeViewModel.DisputeFilter.RESOLVED,
                                isProcessing = state.processingId == dispute.disputeId,
                                onResolve = { status, notes -> 
                                    viewModel.resolveDispute(dispute.disputeId, status, notes) 
                                },
                                onBanUser = { userId, reason ->
                                    viewModel.banUser(userId, reason)
                                },
                                onEscalate = {
                                    viewModel.escalateDispute(dispute.disputeId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisputeCard(
    dispute: DisputeEntity,
    isHistory: Boolean,
    isProcessing: Boolean,
    onResolve: (DisputeStatus, String) -> Unit,
    onBanUser: (String, String) -> Unit,
    onEscalate: () -> Unit
) {
    var showResolveDialog by remember { mutableStateOf(false) }
    var showBanDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var banReason by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf("") }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHistory) MaterialTheme.colorScheme.surfaceVariant 
                            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Gavel, 
                    contentDescription = null,
                    tint = if (isHistory) Color.Gray else MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(dispute.reason, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Status badge
            DisputeStatusBadge(status = dispute.status)
            
            Spacer(Modifier.height(8.dp))
            
            Text("Reporter: ${dispute.reporterId.take(10)}...", style = MaterialTheme.typography.bodySmall)
            Text("Against: ${dispute.reportedUserId.take(10)}...", style = MaterialTheme.typography.bodySmall)
            if (!dispute.transferId.isNullOrBlank()) {
                Text("Order/Transfer: ${dispute.transferId}", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(Modifier.height(8.dp))
            Text(dispute.description, style = MaterialTheme.typography.bodyMedium)
            
            if (isHistory && !dispute.resolution.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        "Resolution: ${dispute.resolution}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (dispute.resolvedAt != null && dispute.resolvedAt > 0) {
                    Text(
                        "Resolved: ${dateFormatter.format(Date(dispute.resolvedAt))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            
            if (!isHistory) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showResolveDialog = true },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        Text("Resolve")
                    }
                    OutlinedButton(
                        onClick = onEscalate,
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        Text("Escalate")
                    }
                    IconButton(
                        onClick = { 
                            selectedUserId = dispute.reportedUserId
                            showBanDialog = true 
                        },
                        enabled = !isProcessing
                    ) {
                        Icon(
                            Icons.Default.PersonOff, 
                            contentDescription = "Ban User",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
    
    // Resolve Dialog
    if (showResolveDialog) {
        AlertDialog(
            onDismissRequest = { showResolveDialog = false },
            title = { Text("Resolve Dispute") },
            text = {
                Column {
                    Text("Choose resolution:", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { 
                                onResolve(DisputeStatus.RESOLVED_REFUNDED, notes)
                                showResolveDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) { 
                            Text("Refund") 
                        }
                        Button(
                            onClick = { 
                                onResolve(DisputeStatus.RESOLVED_DISMISSED, notes) 
                                showResolveDialog = false
                            }
                        ) { 
                            Text("Dismiss") 
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notes, 
                        onValueChange = { notes = it },
                        label = { Text("Admin Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showResolveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Ban User Dialog
    if (showBanDialog) {
        AlertDialog(
            onDismissRequest = { showBanDialog = false },
            title = { Text("Ban User") },
            text = {
                Column {
                    Text("Ban user: $selectedUserId")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = banReason,
                        onValueChange = { banReason = it },
                        label = { Text("Reason for ban") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBanUser(selectedUserId, banReason)
                        showBanDialog = false
                        banReason = ""
                    },
                    enabled = banReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ban")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DisputeStatusBadge(status: DisputeStatus) {
    val (color, text) = when (status) {
        DisputeStatus.OPEN -> MaterialTheme.colorScheme.error to "Open"
        DisputeStatus.UNDER_REVIEW -> Color(0xFF2196F3) to "Under Review"
        DisputeStatus.RESOLVED_REFUNDED -> Color(0xFFFF9800) to "Refunded"
        DisputeStatus.RESOLVED_DISMISSED -> Color.Gray to "Dismissed"
        DisputeStatus.RESOLVED_WARNING_ISSUED -> Color(0xFFFF5722) to "Warning Issued"
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
