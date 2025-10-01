package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.ui.monitoring.vm.QuarantineViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun QuarantineManagementScreen(
    productId: String = "",
    viewModel: QuarantineViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsStateWithLifecycle()
    val reason = remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf("All") }
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    LaunchedEffect(productId) {
        if (productId.isNotBlank()) {
            viewModel.observe(productId)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Quarantine Management", style = MaterialTheme.typography.titleLarge)
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Start and manage quarantine protocols here.", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = reason.value,
                            onValueChange = { reason.value = it },
                            label = { Text("Reason / Protocol") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(onClick = { showFilters = !showFilters }) {
                                Text("Filters")
                            }
                            Button(
                                onClick = {
                                    if (reason.value.isNotBlank() && productId.isNotBlank()) {
                                        viewModel.start(productId, reason.value, null)
                                        reason.value = ""
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start Quarantine")
                            }
                        }
                    }
                }
            }

            if (showFilters) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = filterStatus == "All",
                            onClick = { filterStatus = "All" },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = filterStatus == "ACTIVE",
                            onClick = { filterStatus = "ACTIVE" },
                            label = { Text("Active") }
                        )
                        FilterChip(
                            selected = filterStatus == "RECOVERED",
                            onClick = { filterStatus = "RECOVERED" },
                            label = { Text("Recovered") }
                        )
                    }
                }
            }

            item {
                Text(
                    "Active Quarantines (${uiState.active.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val displayedActive = when (filterStatus) {
                "ACTIVE" -> uiState.active
                "All" -> uiState.active
                else -> emptyList()
            }

            items(displayedActive) { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Product: ${record.productId}", style = MaterialTheme.typography.titleMedium)
                        Text("Reason: ${record.reason}")
                        record.protocol?.let { Text("Protocol: $it") }
                        Text("Started: ${dateFormat.format(record.startedAt)}")
                        Text("Status: ${record.status}")
                        Button(
                            onClick = { viewModel.complete(record, "RECOVERED") },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Complete")
                        }
                    }
                }
            }

            item {
                Text(
                    "Quarantine History (${uiState.history.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            val displayedHistory = when (filterStatus) {
                "RECOVERED" -> uiState.history.filter { it.status == "RECOVERED" }
                "All" -> uiState.history
                else -> emptyList()
            }

            items(displayedHistory) { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Product: ${record.productId}", style = MaterialTheme.typography.titleMedium)
                        Text("Reason: ${record.reason}")
                        Text("Started: ${dateFormat.format(record.startedAt)}")
                        record.endedAt?.let { Text("Ended: ${dateFormat.format(it)}") }
                        Text("Status: ${record.status}")
                    }
                }
            }
        }
    }
}
