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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.ui.trust.TrustScoreViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect

@Composable
fun OwnershipTrailScreen(
    transferId: String,
    onBack: () -> Unit,
    vm: OwnershipTrailViewModel = hiltViewModel()
) {
    LaunchedEffect(transferId) { vm.load(transferId) }
    val state = vm.state.collectAsStateWithLifecycle().value

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Ownership Trail", style = MaterialTheme.typography.titleMedium)
        state.transfer?.let { t ->
            Text("Transfer ID: ${t.transferId}")
            Text("From: ${t.fromUserId ?: "-"} → To: ${t.toUserId ?: "-"}")
            Text("Status: ${t.status}")
            val counterparty = t.toUserId ?: ""
            if (counterparty.isNotBlank()) {
                val trustVm: TrustScoreViewModel = hiltViewModel()
                LaunchedEffect(counterparty) { trustVm.load(counterparty) }
                val trustState by trustVm.state.collectAsStateWithLifecycle()
                if (!trustState.loading) {
                    Text(
                        text = "Trust ${"%.1f".format(trustState.score)}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
            items(state.verifications) { v ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Step: ${v.step}")
                        Text("Status: ${v.status}")
                        v.notes?.let { Text("Notes: $it") }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}
