package com.rio.rostry.ui.enthusiast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.launch

@Composable
fun EnthusiastTransfersScreen(
    onOpenTransfer: (String) -> Unit,
    onVerifyTransfer: (String) -> Unit,
    onCreateTransfer: () -> Unit,
    onOpenTraceability: (String) -> Unit,
) {
    val vm: EnthusiastTransferViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ownership Transfer Management")
        if (state.loading) {
            Text("Loading...")
        }
        state.error?.let { err ->
            ElevatedCard { Column(Modifier.padding(12.dp)) { Text("Error: $err") } }
        }
        // Filters
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Filters")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = state.statusFilter == "ALL", onClick = { vm.setStatusFilter("ALL") }, label = { Text("All") })
                    FilterChip(selected = state.statusFilter == "PENDING", onClick = { vm.setStatusFilter("PENDING") }, label = { Text("Pending") })
                    FilterChip(selected = state.statusFilter == "VERIFIED", onClick = { vm.setStatusFilter("VERIFIED") }, label = { Text("Verified") })
                    FilterChip(selected = state.statusFilter == "COMPLETED", onClick = { vm.setStatusFilter("COMPLETED") }, label = { Text("Completed") })
                    FilterChip(selected = state.statusFilter == "DISPUTED", onClick = { vm.setStatusFilter("DISPUTED") }, label = { Text("Disputed") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = state.typeFilter == "ALL", onClick = { vm.setTypeFilter("ALL") }, label = { Text("All") })
                    FilterChip(selected = state.typeFilter == "INCOMING", onClick = { vm.setTypeFilter("INCOMING") }, label = { Text("Incoming") })
                    FilterChip(selected = state.typeFilter == "OUTGOING", onClick = { vm.setTypeFilter("OUTGOING") }, label = { Text("Outgoing") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    var start by rememberSaveable { mutableStateOf("") }
                    var end by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Start (epoch ms)") })
                    OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("End (epoch ms)") })
                    OutlinedButton(onClick = {
                        val s = start.toLongOrNull()
                        val e = end.toLongOrNull()
                        vm.setDateRange(s, e)
                    }) { Text("Apply") }
                    TextButton(onClick = { vm.setDateRange(null, null) }) { Text("Clear") }
                }
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Pending Transfers (Verification Required)")
                if (state.pending.isEmpty() && !state.loading) {
                    Text("No pending transfers")
                } else {
                    // Bulk actions
                    if (state.selection.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { vm.bulkCancelSelected() }) { Text("Bulk Cancel") }
                            OutlinedButton(onClick = { vm.requestPlatformReviewSelected() }) { Text("Bulk Request Review") }
                            TextButton(onClick = { vm.clearSelection() }) { Text("Clear Selection") }
                        }
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(state.pending) { t ->
                            Column(Modifier.fillMaxWidth()) {
                                Row(Modifier.fillMaxWidth()) {
                                    // Selection toggle
                                    AssistChip(onClick = { vm.toggleSelection(t.transferId) }, label = { Text(if (state.selection.contains(t.transferId)) "Selected" else "Select") })
                                    Text(t.transferId, modifier = Modifier.weight(1f).padding(start = 8.dp))
                                    Text("${t.type} • ${t.status}")
                                }
                                // Trust badge (computed async)
                                var trust by remember(t.transferId) { mutableStateOf<Int?>(null) }
                                LaunchedEffect(t.transferId) {
                                    trust = vm.computeTrustScore(t.transferId)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Trust: ${trust?.toString() ?: "..."}")
                                    // Verification progress (simple heuristic)
                                    val progress = when (t.status.uppercase()) {
                                        "PENDING" -> "0/3"
                                        "VERIFIED" -> "2/3"
                                        "COMPLETED" -> "3/3"
                                        else -> "-"
                                    }
                                    Text("Progress: $progress")
                                }
                                // Actions
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { onOpenTransfer(t.transferId) }) { Text("Details") }
                                    TextButton(onClick = { onVerifyTransfer(t.transferId) }) { Text("Verify") }
                                    if (!t.productId.isNullOrBlank()) {
                                        TextButton(onClick = { onOpenTraceability(t.productId!!) }) { Text("Chain") }
                                    }
                                    // Export PDF
                                    val context = androidx.compose.ui.platform.LocalContext.current
                                    TextButton(onClick = {
                                        scope.launch {
                                            val json = vm.generateDocumentation(t.transferId)
                                            if (!json.isNullOrBlank()) {
                                                com.rio.rostry.utils.export.PdfExporter.writeSimpleTable(
                                                    context,
                                                    fileName = "transfer_${t.transferId}.pdf",
                                                    title = "Transfer ${t.transferId}",
                                                    headers = listOf("Payload (json)"),
                                                    rows = listOf(listOf(json.take(900)))
                                                )
                                            }
                                        }
                                    }) { Text("Export") }
                                }
                            }
                        }
                    }
                }
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("History & Documentation")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onCreateTransfer) { Text("New Transfer") }
                    TextButton(onClick = { vm.refresh() }) { Text("Refresh") }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(state.history) { t ->
                        Column(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth()) {
                                Text(t.transferId, modifier = Modifier.weight(1f))
                                Text("${t.type} • ${t.status}")
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { onOpenTransfer(t.transferId) }) { Text("Details") }
                                if (!t.productId.isNullOrBlank()) {
                                    TextButton(onClick = { onOpenTraceability(t.productId!!) }) { Text("Chain") }
                                }
                                // Export PDF
                                val context = androidx.compose.ui.platform.LocalContext.current
                                TextButton(onClick = {
                                    scope.launch {
                                        val json = vm.generateDocumentation(t.transferId)
                                        if (!json.isNullOrBlank()) {
                                            com.rio.rostry.utils.export.PdfExporter.writeSimpleTable(
                                                context,
                                                fileName = "transfer_${t.transferId}.pdf",
                                                title = "Transfer ${t.transferId}",
                                                headers = listOf("Payload (json)"),
                                                rows = listOf(listOf(json.take(900)))
                                            )
                                        }
                                    }
                                }) { Text("Export") }
                            }
                        }
                    }
                }
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Verification Steps")
                Text("• Photo: before/after • GPS confirm • Digital signature • Platform verification")
            }
        }
    }
}
