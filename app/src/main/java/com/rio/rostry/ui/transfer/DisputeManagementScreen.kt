package com.rio.rostry.ui.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun DisputeManagementScreen(
    transferId: String,
    onBack: () -> Unit,
    vm: DisputeManagementViewModel = hiltViewModel()
) {
    LaunchedEffect(transferId) { vm.load(transferId) }
    val state = vm.state.collectAsStateWithLifecycle().value

    var reason by remember { mutableStateOf("") }
    var resolution by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Dispute Management", style = MaterialTheme.typography.titleMedium)
        if (state.error != null) {
            Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        if (state.success != null) {
            Text(text = state.success ?: "", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }

        // List disputes
        LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
            items(state.disputes) { d ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Dispute: ${d.disputeId}")
                        Text("Status: ${d.status}")
                        d.reason?.let { Text("Reason: $it") }
                        d.resolutionNotes?.let { Text("Resolution: $it") }
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            OutlinedTextField(value = resolution, onValueChange = { resolution = it }, modifier = Modifier.weight(1f), label = { Text("Resolution") })
                            Button(onClick = { vm.setResolution(d.disputeId, transferId, resolution, status = "RESOLVED") }, modifier = Modifier.padding(start = 8.dp)) { Text("Resolve") }
                        }
                    }
                }
            }
        }

        // Open new dispute
        Text("Open Dispute", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(value = reason, onValueChange = { reason = it }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), label = { Text("Reason") })
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(onClick = { vm.openDispute(transferId, actorUserId = "me", reason = reason) }, enabled = !state.creating) { Text(if (state.creating) "Creating..." else "Open Dispute") }
            TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) { Text("Back") }
        }
    }
}
