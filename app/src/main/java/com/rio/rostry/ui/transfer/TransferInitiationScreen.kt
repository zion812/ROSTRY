package com.rio.rostry.ui.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransferInitiationScreen(onBack: () -> Unit, vm: TransferInitiationViewModel = hiltViewModel()) {
    var productId by remember { mutableStateOf("") }
    var buyerId by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("0.0") }
    var currency by remember { mutableStateOf("INR") }
    var terms by remember { mutableStateOf("") }
    var gpsLat by remember { mutableStateOf("") }
    var gpsLng by remember { mutableStateOf("") }
    val ui = vm.ui.collectAsStateWithLifecycle().value

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Initiate Transfer", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = productId, onValueChange = { productId = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Product ID (optional)") })
        OutlinedTextField(value = buyerId, onValueChange = { buyerId = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Buyer User ID") })
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            OutlinedTextField(value = amountText, onValueChange = { amountText = it }, modifier = Modifier.weight(1f), label = { Text("Amount") })
            OutlinedTextField(value = currency, onValueChange = { currency = it }, modifier = Modifier.weight(1f).padding(start = 8.dp), label = { Text("Currency") })
        }
        OutlinedTextField(value = terms, onValueChange = { terms = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Terms & Conditions") })
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            OutlinedTextField(value = gpsLat, onValueChange = { gpsLat = it }, modifier = Modifier.weight(1f), label = { Text("GPS Lat") })
            OutlinedTextField(value = gpsLng, onValueChange = { gpsLng = it }, modifier = Modifier.weight(1f).padding(start = 8.dp), label = { Text("GPS Lng") })
        }
        if (ui.error != null) {
            Text(text = ui.error ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Button(onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                val lat = gpsLat.toDoubleOrNull()
                val lng = gpsLng.toDoubleOrNull()
                vm.initiate(
                    fromUserId = "me",
                    toUserId = buyerId,
                    productId = productId.ifBlank { null },
                    amount = amt,
                    currency = currency.ifBlank { "INR" },
                    terms = terms.ifBlank { null },
                    gpsLat = lat,
                    gpsLng = lng
                )
            }, modifier = Modifier.padding(start = 8.dp), enabled = !ui.creating) { Text(if (ui.creating) "Creating..." else "Create Transfer") }
        }
        ui.createdTransferId?.let { id ->
            Text("Created: $id", modifier = Modifier.padding(top = 8.dp))
        }
    }
}
