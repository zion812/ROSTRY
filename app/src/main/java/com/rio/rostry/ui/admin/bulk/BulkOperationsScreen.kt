package com.rio.rostry.ui.admin.bulk

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkOperationsScreen(
    viewModel: BulkOperationsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val operations = remember {
        listOf(
            BulkOperation("Import Users", "Import users from CSV/Excel", Icons.Default.Upload, Color(0xFF2196F3)),
            BulkOperation("Export Users", "Export all users to CSV", Icons.Default.Download, Color(0xFF4CAF50)),
            BulkOperation("Bulk Verify", "Approve multiple KYC requests", Icons.Default.VerifiedUser, Color(0xFF9C27B0)),
            BulkOperation("Bulk Email", "Send email to multiple users", Icons.Default.Email, Color(0xFFFF9800)),
            BulkOperation("Data Cleanup", "Remove inactive/orphan records", Icons.Default.CleaningServices, Color(0xFFD32F2F)),
            BulkOperation("Export Orders", "Export orders to spreadsheet", Icons.Default.ShoppingCart, Color(0xFF00BCD4)),
            BulkOperation("Backup Data", "Create full database backup", Icons.Default.Backup, Color(0xFF607D8B))
        )
    }

    val runningOps by viewModel.runningOperations.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bulk Operations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFFF9800))
                        Spacer(Modifier.width(12.dp))
                        Text("Bulk operations may take time and affect many records. Use with caution.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            items(operations) { op ->
                OperationCard(
                    op = op,
                    isRunning = runningOps.contains(op.name),
                    onRun = { viewModel.runOperation(op.name) }
                )
            }
        }
    }
}

private data class BulkOperation(val name: String, val description: String, val icon: ImageVector, val color: Color)

@Composable
private fun OperationCard(op: BulkOperation, isRunning: Boolean, onRun: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(op.icon, null, tint = op.color, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(op.name, fontWeight = FontWeight.Bold)
                    Text(op.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Button(
                onClick = onRun,
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = op.color)
            ) {
                if (isRunning) {
                    CircularProgressIndicator(Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Run")
                }
            }
        }
    }
}
