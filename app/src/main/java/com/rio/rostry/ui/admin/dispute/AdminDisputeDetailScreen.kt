package com.rio.rostry.ui.admin.dispute

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rio.rostry.data.database.entity.DisputeStatus
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDisputeDetailScreen(
    viewModel: AdminDisputeDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showResolveDialog by remember { mutableStateOf(false) }
    var showBanDialog by remember { mutableStateOf(false) }
    var selectedDecision by remember { mutableStateOf<DisputeStatus?>(null) }
    var resolutionNotes by remember { mutableStateOf("") }
    var banReason by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dispute Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            state.dispute == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Dispute not found")
                }
            }
            else -> {
                val dispute = state.dispute!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status Badge
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dispute #${dispute.disputeId.take(8)}", style = MaterialTheme.typography.titleLarge)
                            DisputeStatusBadge(status = dispute.status)
                        }
                    }

                    // Parties Section
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Parties Involved", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Reporter", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                        Text(state.reporterName ?: "Loading...", style = MaterialTheme.typography.bodyMedium)
                                        Text(dispute.reporterId.take(12) + "...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Reported User", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                                        Text(state.reportedUserName ?: "Loading...", style = MaterialTheme.typography.bodyMedium)
                                        Text(dispute.reportedUserId.take(12) + "...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // Reason & Description
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Complaint Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text("Reason", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(dispute.reason, style = MaterialTheme.typography.bodyLarge)
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text("Description", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(dispute.description.ifBlank { "No description provided" }, style = MaterialTheme.typography.bodyMedium)
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    "Submitted: ${formatDate(dispute.createdAt)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Evidence
                    if (dispute.evidenceUrls.isNotEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Evidence (${dispute.evidenceUrls.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(dispute.evidenceUrls) { url ->
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(url)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Evidence",
                                                modifier = Modifier
                                                    .size(150.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Resolution (if resolved)
                    if (state.isResolved && dispute.resolution != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Resolution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(dispute.resolution ?: "", style = MaterialTheme.typography.bodyMedium)
                                    dispute.resolvedAt?.let {
                                        Text("Resolved: ${formatDate(it)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons (if not resolved)
                    if (!state.isResolved) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { 
                                            selectedDecision = DisputeStatus.RESOLVED_REFUNDED
                                            showResolveDialog = true 
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        enabled = !state.isProcessing
                                    ) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Refund")
                                    }
                                    
                                    Button(
                                        onClick = { 
                                            selectedDecision = DisputeStatus.RESOLVED_DISMISSED
                                            showResolveDialog = true 
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                        enabled = !state.isProcessing
                                    ) {
                                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Dismiss")
                                    }
                                }
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { 
                                            selectedDecision = DisputeStatus.RESOLVED_WARNING_ISSUED
                                            showResolveDialog = true 
                                        },
                                        modifier = Modifier.weight(1f),
                                        enabled = !state.isProcessing
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Issue Warning")
                                    }
                                    
                                    OutlinedButton(
                                        onClick = { showBanDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                        enabled = !state.isProcessing
                                    ) {
                                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ban User")
                                    }
                                }
                                
                                TextButton(
                                    onClick = { viewModel.escalate() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !state.isProcessing
                                ) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Escalate to Management")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Resolve Dialog
    if (showResolveDialog && selectedDecision != null) {
        AlertDialog(
            onDismissRequest = { showResolveDialog = false },
            title = { Text("Resolve Dispute") },
            text = {
                Column {
                    Text("Decision: ${selectedDecision?.name?.replace("_", " ")}")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resolutionNotes,
                        onValueChange = { resolutionNotes = it },
                        label = { Text("Resolution Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDecision?.let { viewModel.resolveDispute(it, resolutionNotes) }
                        showResolveDialog = false
                        resolutionNotes = ""
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showResolveDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Ban Dialog
    if (showBanDialog) {
        AlertDialog(
            onDismissRequest = { showBanDialog = false },
            title = { Text("Ban User") },
            text = {
                Column {
                    Text("This will suspend the reported user from the platform.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = banReason,
                        onValueChange = { banReason = it },
                        label = { Text("Ban Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.banUser(banReason)
                        showBanDialog = false
                        banReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Ban User") }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
}
