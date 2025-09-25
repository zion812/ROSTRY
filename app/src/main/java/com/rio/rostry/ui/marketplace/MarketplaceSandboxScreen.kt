package com.rio.rostry.ui.marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ProductEntity

@Composable
fun MarketplaceSandboxScreen() {
    val vm: MarketplaceSandboxViewModel = hiltViewModel()
    val ui by vm.ui.collectAsState()

    val (price, setPrice) = remember { mutableStateOf("1200.0") }
    val (orderId, setOrderId) = remember { mutableStateOf("demo-order") }
    val (userId, setUserId) = remember { mutableStateOf("demo-user") }
    val (failNext, setFailNext) = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Marketplace Sandbox")

        // Product builder (basic)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = price, onValueChange = setPrice, modifier = Modifier.weight(1f), label = { Text("Price") })
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val p = sampleProduct(price.toDoubleOrNull() ?: 1200.0)
                vm.validateSample(p)
            }) { Text("Validate sample") }

            Button(onClick = {
                val p = sampleProduct(price.toDoubleOrNull() ?: 1200.0)
                vm.createSample(p)
            }) { Text("Create sample") }
        }

        Spacer(Modifier.height(8.dp))
        Text("Payments (Demo)")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = orderId, onValueChange = setOrderId, modifier = Modifier.weight(1f), label = { Text("Order ID") })
            OutlinedTextField(value = userId, onValueChange = setUserId, modifier = Modifier.weight(1f), label = { Text("User ID") })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.payFixed(orderId, userId, price.toDoubleOrNull() ?: 1200.0, failNext) }) { Text("Pay FIXED") }
            Button(onClick = { vm.payAuction(orderId, userId, price.toDoubleOrNull() ?: 1200.0, failNext) }) { Text("Pay AUCTION") }
            Button(onClick = { vm.payCod(orderId, userId, price.toDoubleOrNull() ?: 1200.0) }) { Text("COD Reserve") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.payAdvance(orderId, userId, price.toDoubleOrNull() ?: 1200.0, failNext) }) { Text("Pay ADVANCE") }
            TextButton(onClick = { setFailNext(!failNext) }) { Text(if (failNext) "Fail Next: ON" else "Fail Next: OFF") }
        }

        ui.lastValidationResult?.let { r ->
            Text("Validation: ${if (r.valid) "OK" else r.reasons.joinToString("; ")}")
        }
        ui.lastCreateResult?.let { r ->
            Text("Create: ${r::class.simpleName} ${(r.message ?: r.data?.toString()).orEmpty()}")
        }
        ui.lastPaymentResult?.let { r ->
            Text("Payment: ${r::class.simpleName} ${(r.message ?: r.data?.message).orEmpty()}")
        }
    }
}

private fun sampleProduct(price: Double): ProductEntity = ProductEntity(
    productId = "sandbox-${System.currentTimeMillis()}",
    sellerId = "demo-seller",
    name = "Sandbox Bird",
    description = "Generated from sandbox",
    category = "MEAT",
    price = price,
    quantity = 1.0,
    unit = "piece",
    location = "Demo Farm",
    latitude = 12.9,
    longitude = 77.6,
    imageUrls = listOf("a", "b"),
    status = "available",
    condition = null,
    harvestDate = null,
    expiryDate = null,
    birthDate = System.currentTimeMillis() - (10L * 24 * 60 * 60 * 1000),
    vaccinationRecordsJson = null,
    weightGrams = 900.0, // add growth data so validation passes young group
    heightCm = null,
    gender = null,
    color = null,
    breed = "Demo",
    familyTreeId = null,
    parentIdsJson = null,
    breedingStatus = null,
    transferHistoryJson = null
)
