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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.ui.monitoring.vm.HatchingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HatchingProcessScreen(
    viewModel: HatchingViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsStateWithLifecycle()
    val batchName = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.clearMessages()
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
                Text("Hatching Process", style = MaterialTheme.typography.titleLarge)
            }
            
            item {
                ElevatedCard {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Create and monitor incubation batches.", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = batchName.value,
                            onValueChange = { batchName.value = it },
                            label = { Text("Batch name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (batchName.value.isNotBlank()) {
                                    viewModel.startBatch(batchName.value)
                                    batchName.value = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Batch")
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }

            item {
                Text(
                    "Active Batches (${uiState.batches.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(uiState.batches) { batch ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(batch.name, style = MaterialTheme.typography.titleMedium)
                        Text("Started: ${dateFormat.format(batch.startedAt)}")
                        batch.expectedHatchAt?.let {
                            Text("Expected Hatch: ${dateFormat.format(it)}")
                        }
                        batch.temperatureC?.let {
                            Text("Temperature: $it°C")
                        }
                        batch.humidityPct?.let {
                            Text("Humidity: $it%")
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            OutlinedButton(onClick = { viewModel.selectBatch(batch.batchId) }) {
                                Text("View Logs")
                            }
                            OutlinedButton(onClick = { /* Update status */ }) {
                                Text("Update Status")
                            }
                        }
                    }
                }
            }
        }
    }
}
