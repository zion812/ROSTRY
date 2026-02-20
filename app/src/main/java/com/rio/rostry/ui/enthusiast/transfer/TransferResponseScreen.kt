package com.rio.rostry.ui.enthusiast.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferResponseScreen(
    transferId: String,
    onBackClick: () -> Unit,
    onTransferComplete: () -> Unit,
    viewModel: TransferResponseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(transferId) {
        viewModel.loadTransfer(transferId)
    }

    LaunchedEffect(uiState.isActionComplete) {
        if (uiState.isActionComplete) {
            onTransferComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Asset Transfer") },
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
            val transfer = uiState.transferEntity
            val product = uiState.productEntity

            if (transfer != null && product != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("You've been offered an asset!", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Review the details below and enter the unique 6-digit security code provided by the sender to claim ownership.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        AssetSummaryCard(product) // Reuse from EnthusiastTransferScreen
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text("Time Sensitive", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                                    Text(
                                        "This transfer offer will expire if not accepted.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Security Code", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = uiState.inputCode,
                            onValueChange = { viewModel.updateInputCode(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("000000") },
                            singleLine = true,
                            isError = uiState.codeError != null,
                            supportingText = {
                                if (uiState.codeError != null) {
                                    Text(uiState.codeError!!, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.denyTransfer() },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isProcessing
                            ) {
                                Text("Deny")
                            }
                            Button(
                                onClick = { viewModel.acceptTransfer() },
                                modifier = Modifier.weight(1f),
                                enabled = uiState.inputCode.length == 6 && !uiState.isProcessing
                            ) {
                                if (uiState.isProcessing) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Accept")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
