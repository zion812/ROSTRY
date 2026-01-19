package com.rio.rostry.ui.farmer.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rio.rostry.data.database.entity.ProductEntity

@Composable
fun QuickFeedLogDialog(
    suggestedAmount: Double?,
    // We might want to pass the list of products (batches/birds) this feed applies to
    products: List<ProductEntity>, 
    onDismiss: () -> Unit,
    onConfirm: (Double, String?) -> Unit
) {
    var amountText by remember { mutableStateOf(suggestedAmount?.toString() ?: "") }
    var notes by remember { mutableStateOf("") }
    
    // Simple validation
    val amount = amountText.toDoubleOrNull()
    val isValid = amount != null && amount > 0

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "Log Feed Usage",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                if (suggestedAmount != null && suggestedAmount > 0) {
                    TextButton(
                        onClick = { amountText = suggestedAmount.toString() },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.End)
                    ) {
                        Text("Use Suggested: %.2f kg".format(suggestedAmount))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { 
                            if (isValid) {
                                onConfirm(amount!!, notes.takeIf { it.isNotBlank() })
                            }
                        },
                        enabled = isValid
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log")
                    }
                }
            }
        }
    }
}
