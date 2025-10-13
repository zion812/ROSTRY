package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.rio.rostry.ui.monitoring.vm.GrowthViewModel

@Composable
fun GrowthTrackingScreen(
    productId: String = "",
    onListProduct: (String) -> Unit = {}
) {
    val vm: GrowthViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    val pid = remember { mutableStateOf(productId) }
    
    // Observe product ID changes and start Flow collection only once per productId
    androidx.compose.runtime.LaunchedEffect(pid.value) {
        if (pid.value.isNotBlank()) {
            vm.observe(pid.value)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Growth Tracking")
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pid.value, onValueChange = { pid.value = it }, label = { Text("Product ID") })
                Text("Weekly records: ${state.records.size}")
                val weight = remember { mutableStateOf(0.0) }
                val height = remember { mutableStateOf(0.0) }
                OutlinedTextField(value = weight.value.toString(), onValueChange = { runCatching { weight.value = it.toDouble() } }, label = { Text("Weight (g)") })
                OutlinedTextField(value = height.value.toString(), onValueChange = { runCatching { height.value = it.toDouble() } }, label = { Text("Height (cm)") })
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { if (pid.value.isNotBlank()) vm.recordToday(pid.value, weight.value, height.value) },
                        modifier = Modifier.weight(1f)
                    ) { 
                        Text("Record today") 
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            vm.trackListOnMarketplaceClick(pid.value)
                            onListProduct(pid.value) 
                        },
                        enabled = pid.value.isNotBlank() && state.records.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("List on Marketplace")
                    }
                }
                
                if (pid.value.isNotBlank() && state.records.isNotEmpty()) {
                    Text(
                        "Create a marketplace listing using your farm data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (pid.value.isBlank()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No product selected",
                subtitle = "Enter a Product ID to start tracking growth",
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            )
        } else if (state.records.isEmpty()) {
            com.rio.rostry.ui.components.EmptyState(
                title = "No growth records",
                subtitle = "Record today's weight and height to begin",
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            )
        }
    }
}
