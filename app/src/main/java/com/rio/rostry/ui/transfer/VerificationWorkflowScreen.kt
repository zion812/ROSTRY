package com.rio.rostry.ui.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import com.rio.rostry.ui.trust.TrustScoreViewModel

@Composable
fun VerificationWorkflowScreen(
    transferId: String,
    onBack: () -> Unit,
    vm: TransferVerificationViewModel = hiltViewModel()
) {
    LaunchedEffect(transferId) { vm.load(transferId) }
    val state = vm.state.collectAsStateWithLifecycle().value
    var platformNotes by remember { mutableStateOf("") }
    var mfaCode by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Verification Workflow", style = MaterialTheme.typography.titleMedium)
        state.transfer?.let { t ->
            Text("Transfer: ${t.transferId}")
            Text("Status: ${t.status}")
            // Simple trust ribbon for counterparty (assume counterparty is receiver for now)
            val counterparty = t.toUserId ?: ""
            if (counterparty.isNotBlank()) {
                val trustVm: TrustScoreViewModel = hiltViewModel()
                LaunchedEffect(counterparty) { trustVm.load(counterparty) }
                val trustState = trustVm.state.collectAsStateWithLifecycle().value
                if (!trustState.loading) {
                    Text(
                        text = "Trust ${"%.1f".format(trustState.score)}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        if (state.error != null) {
            Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 6.dp))
        }
        if (state.success != null) {
            Text(text = state.success ?: "", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 6.dp))
        }

        // Step 1 - Seller Preparation
        Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Step 1: Seller Preparation", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(value = state.tempBeforeUrl, onValueChange = { vm.updateTemp("before", it) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Before Photo Uri") })
                OutlinedTextField(value = state.tempAfterUrl, onValueChange = { vm.updateTemp("after", it) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("After Photo Uri") })
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(onClick = { vm.submitSellerInit() }, enabled = !state.submitting) { Text("Submit Photos") }
                }
            }
        }

        // Step 2 - Buyer Verification (GPS + Signature inputs shown for demo)
        Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Step 2: Buyer Verification", style = MaterialTheme.typography.titleSmall)
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedTextField(value = state.tempLat, onValueChange = { vm.updateTemp("lat", it) }, modifier = Modifier.weight(1f), label = { Text("Meeting GPS Lat") })
                    OutlinedTextField(value = state.tempLng, onValueChange = { vm.updateTemp("lng", it) }, modifier = Modifier.weight(1f).padding(start = 8.dp), label = { Text("Meeting GPS Lng") })
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(onClick = { vm.submitGpsConfirm() }, enabled = !state.submitting) { Text("Submit GPS") }
                }
                OutlinedTextField(value = state.tempSignatureRef, onValueChange = { vm.updateTemp("sig", it) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Digital Signature Ref") })
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(onClick = { vm.submitSignature() }, enabled = !state.submitting) { Text("Submit Signature") }
                }
            }
        }

        // Step 3 - Platform Verification (>₹10k)
        Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text("Step 3: Platform Verification (High Value)", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(value = platformNotes, onValueChange = { platformNotes = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Review Notes") })
                val requiresMfa = (state.transfer?.amount ?: 0.0) > 10_000.0
                if (requiresMfa) {
                    OutlinedTextField(value = mfaCode, onValueChange = { mfaCode = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("MFA Code") })
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(onClick = { if (!requiresMfa || mfaCode.isNotBlank()) vm.submitPlatformReview(platformNotes, approved = true) }, enabled = !state.submitting) { Text("Approve") }
                    Button(onClick = { if (!requiresMfa || mfaCode.isNotBlank()) vm.submitPlatformReview(platformNotes, approved = false) }, modifier = Modifier.padding(start = 8.dp), enabled = !state.submitting) { Text("Reject") }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Divider()
        Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            TextButton(onClick = onBack) { Text("Back") }
        }
    }
}
