package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.rio.rostry.ui.components.EmptyState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerTransfersScreen(
    onOpenTransfer: (String) -> Unit,
    onVerifyTransfer: (String) -> Unit,
    onCreateTransfer: () -> Unit,
    onOpenTraceability: (String) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val vm: FarmerTransfersViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Transfers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Transfer Management for Farmers")

            // Statistics Card
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Transfer Statistics")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Total Value: ${state.statistics.totalValue}")
                        Text("Success Rate: ${state.statistics.successRate}%")
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            state.error?.let { err: String ->
                ElevatedCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(err, modifier = Modifier.weight(1f))
                        TextButton(onClick = { vm.retryLoad() }) { Text("Retry") }
                    }
                }
            }

            // Filters
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Filters")
                    var start by rememberSaveable { mutableStateOf("") }
                    var end by rememberSaveable { mutableStateOf("") }
                    var rangeError by rememberSaveable { mutableStateOf<String?>(null) }
                    var productFilter by rememberSaveable { mutableStateOf("") }
                    var recipientFilter by rememberSaveable { mutableStateOf("") }

                    // Local filter state mapped to VM's TransferFilters
                    var status by rememberSaveable { mutableStateOf<String?>(null) }
                    var type by rememberSaveable { mutableStateOf<String?>(null) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = status == null, onClick = { status = null; vm.updateFilters(state.filters.copy(status = null)) }, label = { Text("All") })
                        FilterChip(selected = status == "PENDING", onClick = { status = "PENDING"; vm.updateFilters(state.filters.copy(status = "PENDING")) }, label = { Text("Pending") })
                        FilterChip(selected = status == "VERIFIED", onClick = { status = "VERIFIED"; vm.updateFilters(state.filters.copy(status = "VERIFIED")) }, label = { Text("Verified") })
                        FilterChip(selected = status == "COMPLETED", onClick = { status = "COMPLETED"; vm.updateFilters(state.filters.copy(status = "COMPLETED")) }, label = { Text("Completed") })
                        FilterChip(selected = status == "DISPUTED", onClick = { status = "DISPUTED"; vm.updateFilters(state.filters.copy(status = "DISPUTED")) }, label = { Text("Disputed") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = type == null, onClick = { type = null; vm.updateFilters(state.filters.copy(type = null)) }, label = { Text("All") })
                        FilterChip(selected = type == "SENT", onClick = { type = "SENT"; vm.updateFilters(state.filters.copy(type = "SENT")) }, label = { Text("Sent") })
                        FilterChip(selected = type == "RECEIVED", onClick = { type = "RECEIVED"; vm.updateFilters(state.filters.copy(type = "RECEIVED")) }, label = { Text("Received") })
                    }
                    // Optional product/recipient local filters (not applied to VM for now)
                    OutlinedTextField(
                        value = productFilter,
                        onValueChange = { productFilter = it },
                        label = { Text("Product") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = recipientFilter,
                        onValueChange = { recipientFilter = it },
                        label = { Text("Recipient/Sender") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = start,
                            onValueChange = { start = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Start (epoch ms)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = end,
                            onValueChange = { end = it.filter { ch -> ch.isDigit() } },
                            label = { Text("End (epoch ms)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = {
                            val now = System.currentTimeMillis()
                            val startTs = now - 24L * 60 * 60 * 1000
                            start = startTs.toString(); end = now.toString(); rangeError = null
                            vm.updateFilters(state.filters.copy(dateRange = DateRange(startTs, now)))
                        }, label = { Text("Today") })
                        AssistChip(onClick = {
                            val now = System.currentTimeMillis()
                            val startTs = now - 7L * 24 * 60 * 60 * 1000
                            start = startTs.toString(); end = now.toString(); rangeError = null
                            vm.updateFilters(state.filters.copy(dateRange = DateRange(startTs, now)))
                        }, label = { Text("7d") })
                        AssistChip(onClick = {
                            val now = System.currentTimeMillis()
                            val startTs = now - 30L * 24 * 60 * 60 * 1000
                            start = startTs.toString(); end = now.toString(); rangeError = null
                            vm.updateFilters(state.filters.copy(dateRange = DateRange(startTs, now)))
                        }, label = { Text("30d") })
                        TextButton(onClick = { start = ""; end = ""; rangeError = null; vm.updateFilters(state.filters.copy(dateRange = null)) }) { Text("Clear") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            val s = start.toLongOrNull()
                            val e = end.toLongOrNull()
                            rangeError = if (s != null && e != null && e < s) "End must be after Start" else null
                            if (rangeError == null) vm.updateFilters(state.filters.copy(dateRange = DateRange(s, e)))
                        }) { Text("Apply") }
                        TextButton(onClick = { vm.updateFilters(state.filters.copy(dateRange = null)) }) { Text("Clear") }
                    }
                    rangeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }

            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pending Transfers (Action Required)")
                    val pending = remember(state.filteredTransfers) { state.filteredTransfers.filter { it.status == "PENDING" } }
                    if (pending.isEmpty() && !state.isLoading) {
                        EmptyState(
                            title = "No pending transfers",
                            subtitle = "All transfers are up to date",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    } else {
                        // Bulk actions
                        if (state.selectedTransfers.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { vm.bulkCancel() }) { Text("Bulk Cancel") }
                                OutlinedButton(onClick = { vm.bulkRequestReview() }) { Text("Bulk Request Review") }
                                TextButton(onClick = { vm.clearSelection() }) { Text("Clear Selection") }
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(pending, key = { it.transferId }) { t ->
                                Column(Modifier.fillMaxWidth()) {
                                    Row(Modifier.fillMaxWidth()) {
                                        // Selection toggle
                                        AssistChip(onClick = { vm.toggleTransferSelection(t.transferId) }, label = { Text(if (state.selectedTransfers.contains(t.transferId)) "Selected" else "Select") })
                                        Text(t.transferId, modifier = Modifier.weight(1f).padding(start = 8.dp))
                                        Text("${t.type} • ${t.status}")
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Amount: ${t.amount} ${t.currency}")
                                    }
                                    // Actions
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TextButton(onClick = { onOpenTransfer(t.transferId) }) { Text("Details") }
                                        TextButton(onClick = { onVerifyTransfer(t.transferId) }) { Text("Verify") }
                                        if (!t.productId.isNullOrBlank()) {
                                            TextButton(onClick = { onOpenTraceability(t.productId!!) }) { Text("Chain") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Transfer History")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onCreateTransfer) { Text("New Transfer") }
                        TextButton(onClick = { vm.retryLoad() }) { Text("Refresh") }
                    }
                    val history = remember(state.filteredTransfers) { state.filteredTransfers }
                    if (history.isEmpty()) {
                        if (state.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            EmptyState(
                                title = "No transfer history",
                                subtitle = "Your transfers will appear here",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(400.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(history, key = { it.transferId }) { t ->
                                Column(Modifier.fillMaxWidth()) {
                                    Row(Modifier.fillMaxWidth()) {
                                        Text(t.transferId, modifier = Modifier.weight(1f))
                                        Text("${t.type} • ${t.status}")
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Amount: ${t.amount} ${t.currency}")
                                        TextButton(onClick = { onOpenTransfer(t.transferId) }) { Text("Details") }
                                        if (!t.productId.isNullOrBlank()) {
                                            TextButton(onClick = { onOpenTraceability(t.productId!!) }) { Text("Chain") }
                                        }
                                    }
                                }
                        }
                    }
                }
            }

            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Transfer Process")
                    Text("• Initiate transfer • Recipient verification • Platform confirmation • Completion")
                }
            }
        }
    }
}
