package com.rio.rostry.ui.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TransferCreateScreen(
    state: TransferCreateViewModel.UiState,
    onUpdate: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Create New Transfer")
        state.error?.let { Text("Error: $it") }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.productId,
                    onValueChange = { onUpdate("productId", it) },
                    label = { Text("Product ID (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.toUserId,
                    onValueChange = { onUpdate("toUserId", it) },
                    label = { Text("Recipient User ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { onUpdate("amount", it) },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.currency,
                    onValueChange = { onUpdate("currency", it) },
                    label = { Text("Currency") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.type,
                    onValueChange = { onUpdate("type", it) },
                    label = { Text("Type (PAYMENT/PAYOUT/...)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { onUpdate("notes", it) },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onBack) { Text("Cancel") }
                    Button(onClick = onSubmit, enabled = !state.loading) { Text(if (state.loading) "Creating..." else "Create") }
                }
            }
        }
    }
}
