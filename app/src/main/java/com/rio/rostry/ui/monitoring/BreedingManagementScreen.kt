package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.monitoring.vm.BreedingManagementViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingManagementScreen(
    viewModel: BreedingManagementViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onListProduct: (productId: String, pairId: String) -> Unit = { _, _ -> }
) {
    val breedingPairs by viewModel.breedingPairs.collectAsState()
    val error by viewModel.error.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPairId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Pairs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Pair")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (error != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(error ?: "", color = MaterialTheme.colorScheme.onErrorContainer)
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }
            items(breedingPairs) { pair ->
                BreedingPairCard(
                    pair = pair,
                    onViewDetails = { selectedPairId = pair.pairId },
                    onRetire = { viewModel.retirePair(pair.pairId) },
                    onListPair = { onListProduct(pair.maleProductId, pair.pairId) } // List male bird with breeding info
                )
            }
        }
    }

    if (showAddDialog) {
        AddPairDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { maleId, femaleId, notes ->
                viewModel.addPair(
                    maleProductId = maleId,
                    femaleProductId = femaleId,
                    pairedAt = System.currentTimeMillis(),
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun BreedingPairCard(
    pair: com.rio.rostry.data.database.entity.BreedingPairEntity,
    onViewDetails: () -> Unit,
    onRetire: () -> Unit,
    onListPair: () -> Unit = {}
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onViewDetails
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pair ${pair.pairId.take(8)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Badge(
                    containerColor = if (pair.status == "ACTIVE")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(pair.status)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Male: ${pair.maleProductId.take(8)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Female: ${pair.femaleProductId.take(8)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Eggs: ${pair.eggsCollected}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Success: ${(pair.hatchSuccessRate * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                "Paired: ${dateFormat.format(Date(pair.pairedAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (pair.status == "ACTIVE") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    OutlinedButton(onClick = onListPair) {
                        Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("List Breeding Pair")
                    }
                    OutlinedButton(onClick = onRetire) {
                        Text("Retire")
                    }
                }
                
                Text(
                    "List this proven breeding pair on marketplace with performance stats",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddPairDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String?) -> Unit
) {
    var maleId by remember { mutableStateOf("") }
    var femaleId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Breeding Pair") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = maleId,
                    onValueChange = { maleId = it },
                    label = { Text("Male Product ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = femaleId,
                    onValueChange = { femaleId = it },
                    label = { Text("Female Product ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (maleId.isNotBlank() && femaleId.isNotBlank()) {
                        onAdd(maleId, femaleId, notes.takeIf { it.isNotBlank() })
                    }
                },
                enabled = maleId.isNotBlank() && femaleId.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
