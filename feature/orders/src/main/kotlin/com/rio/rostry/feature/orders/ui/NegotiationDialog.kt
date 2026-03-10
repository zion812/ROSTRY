package com.rio.rostry.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun NegotiationDialog(
    currentPrice: Double,
    currentQuantity: Double,
    unit: String,
    onDismiss: () -> Unit,
    onSubmitOffer: (Double, Double) -> Unit
) {
    var offerPrice by remember { mutableStateOf(currentPrice.toString()) }
    var offerQuantity by remember { mutableStateOf(currentQuantity.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Make an Offer", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Propose a price and quantity to the seller.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = offerPrice,
                    onValueChange = { offerPrice = it },
                    label = { Text("Offer Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = offerQuantity,
                    onValueChange = { offerQuantity = it },
                    label = { Text("Quantity ($unit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError) {
                    Text(
                        text = "Please enter valid values.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = offerPrice.toDoubleOrNull()
                    val quantity = offerQuantity.toDoubleOrNull()
                    if (price != null && quantity != null && price > 0 && quantity > 0) {
                        onSubmitOffer(price, quantity)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Send Offer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
