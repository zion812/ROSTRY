package com.rio.rostry.ui.enthusiast.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastTransferScreen(
    productId: String,
    onBackClick: () -> Unit,
    onTransferComplete: () -> Unit,
    viewModel: EnthusiastTransferViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showUserDiscovery by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    // Handle transfer success
    LaunchedEffect(uiState.transferSuccess) {
        if (uiState.transferSuccess) {
            onTransferComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Asset") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            val product = uiState.product
            if (product != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AssetSummaryCard(product)
                    }

                    item {
                        Text("Recipient", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        
                        if (uiState.selectedRecipient != null) {
                            RecipientCard(
                                user = uiState.selectedRecipient!!,
                                onChangeClick = { showUserDiscovery = true }
                            )
                        } else {
                            OutlinedButton(
                                onClick = { showUserDiscovery = true },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Select Recipient")
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            PaddingValues(16.dp)
                            Column(Modifier.padding(16.dp)) {
                                Text("Security & Integrity", style = MaterialTheme.typography.titleSmall)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "A secure 6-digit code will be generated to complete this transfer. A complete snapshot of the asset's current health and lineage records will be locked into the transfer log.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { viewModel.initiateTransfer() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = uiState.selectedRecipient != null && !uiState.isTransferring
                        ) {
                            if (uiState.isTransferring) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Initiate Secure Transfer")
                            }
                        }
                    }
                }
            }
        }

        if (showUserDiscovery) {
            UserDiscoverySheet(
                users = uiState.discoveredUsers,
                onSearch = { viewModel.searchUsers(it) },
                onUserSelected = {
                    viewModel.selectRecipient(it)
                    showUserDiscovery = false
                },
                onDismissRequest = { showUserDiscovery = false }
            )
        }
    }
}

@Composable
fun AssetSummaryCard(product: ProductEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleLarge)
            Text(product.breed ?: "Unknown Breed", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Leg Band / ID:", style = MaterialTheme.typography.bodySmall)
                Text(product.birdCode ?: "Not Set", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Hatch Date:", style = MaterialTheme.typography.bodySmall)
                Text(
                    product.birthDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(it)) } ?: "Unknown",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun RecipientCard(user: UserEntity, onChangeClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(user.fullName ?: "Unknown User", style = MaterialTheme.typography.bodyLarge)
                Text(
                    user.address ?: "Unknown Location",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onChangeClick) {
                Text("Change")
            }
        }
    }
}
