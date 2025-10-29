package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.ui.monitoring.vm.MortalityViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MortalityTrackingScreen(
    productId: String? = null,
    viewModel: MortalityViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsStateWithLifecycle()
    val causeCategory = remember { mutableStateOf("") }
    val circumstances = remember { mutableStateOf("") }
    val ageWeeks = remember { mutableStateOf("") }
    val financialImpact = remember { mutableStateOf("") }
    var selectedDisposalMethod by remember { mutableStateOf("Burial") }
    var showFilters by remember { mutableStateOf(false) }
    var filterCause by remember { mutableStateOf("All") }
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Mortality Tracking", style = MaterialTheme.typography.titleLarge)
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Log mortality events and review trends here.", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = causeCategory.value,
                            onValueChange = { causeCategory.value = it },
                            label = { Text("Cause Category (e.g., ILLNESS, PREDATOR, ACCIDENT)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = circumstances.value,
                            onValueChange = { circumstances.value = it },
                            label = { Text("Detailed Circumstances") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                        OutlinedTextField(
                            value = ageWeeks.value,
                            onValueChange = { ageWeeks.value = it },
                            label = { Text("Age (weeks)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        
                        Text("Disposal Method", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedDisposalMethod == "Burial",
                                onClick = { selectedDisposalMethod = "Burial" },
                                label = { Text("Burial") }
                            )
                            FilterChip(
                                selected = selectedDisposalMethod == "Cremation",
                                onClick = { selectedDisposalMethod = "Cremation" },
                                label = { Text("Cremation") }
                            )
                            FilterChip(
                                selected = selectedDisposalMethod == "Composting",
                                onClick = { selectedDisposalMethod = "Composting" },
                                label = { Text("Composting") }
                            )
                        }
                        
                        OutlinedTextField(
                            value = financialImpact.value,
                            onValueChange = { financialImpact.value = it },
                            label = { Text("Financial Impact (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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
                                    if (causeCategory.value.isNotBlank()) {
                                        viewModel.record(
                                            productId = productId,
                                            causeCategory = causeCategory.value,
                                            circumstances = circumstances.value.ifBlank { null },
                                            ageWeeks = ageWeeks.value.toIntOrNull(),
                                            disposalMethod = selectedDisposalMethod,
                                            financialImpactInr = financialImpact.value.toDoubleOrNull()
                                        )
                                        // Clear fields
                                        causeCategory.value = ""
                                        circumstances.value = ""
                                        ageWeeks.value = ""
                                        financialImpact.value = ""
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Record Mortality")
                            }
                        }
                    }
                }
            }

            if (showFilters) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FilterChip(
                            selected = filterCause == "All",
                            onClick = { filterCause = "All" },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = filterCause == "ILLNESS",
                            onClick = { filterCause = "ILLNESS" },
                            label = { Text("Illness") }
                        )
                        FilterChip(
                            selected = filterCause == "PREDATOR",
                            onClick = { filterCause = "PREDATOR" },
                            label = { Text("Predator") }
                        )
                        FilterChip(
                            selected = filterCause == "ACCIDENT",
                            onClick = { filterCause = "ACCIDENT" },
                            label = { Text("Accident") }
                        )
                    }
                }
            }

            // Summary statistics
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Summary Statistics", style = MaterialTheme.typography.titleMedium)
                        val totalDeaths = uiState.records.size
                        val totalFinancialImpact = uiState.records.mapNotNull { it.financialImpactInr }.sum()
                        val deathsByCause = uiState.records.groupBy { it.causeCategory }.mapValues { it.value.size }
                        
                        Text("Total Deaths: $totalDeaths")
                        Text("Total Financial Impact: ₹${"%.2f".format(totalFinancialImpact)}")
                        deathsByCause.forEach { (cause, count) ->
                            Text("$cause: $count")
                        }
                    }
                }
            }

            item {
                Text(
                    "Mortality Records (${uiState.records.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val displayedRecords = when (filterCause) {
                "All" -> uiState.records
                else -> uiState.records.filter { it.causeCategory == filterCause }
            }

            if (displayedRecords.isEmpty()) {
                item {
                    com.rio.rostry.ui.components.EmptyState(
                        title = "No records",
                        subtitle = "Log a mortality event to see it here",
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            }

            items(items = displayedRecords, key = { rec -> rec.hashCode() }) { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Date: ${dateFormat.format(record.occurredAt)}", style = MaterialTheme.typography.titleMedium)
                        Text("Cause: ${record.causeCategory}")
                        record.circumstances?.let { Text("Circumstances: $it") }
                        record.ageWeeks?.let { Text("Age: $it weeks") }
                        record.disposalMethod?.let { Text("Disposal: $it") }
                        record.financialImpactInr?.let { Text("Financial Impact: ₹${"%.2f".format(it)}") }
                    }
                }
            }
        }
    }
}
