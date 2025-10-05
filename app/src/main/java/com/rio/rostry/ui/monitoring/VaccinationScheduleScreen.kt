package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VaccinationScheduleScreen(
    onListProduct: (String) -> Unit = {}
) {
    val search = remember { mutableStateOf("") }
    val sampleProductId = remember { mutableStateOf("") } // In real app, would come from ViewModel
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Vaccination Schedule")
        OutlinedTextField(value = search.value, onValueChange = { search.value = it }, label = { Text("Search fowl or batch") })
        ElevatedCard { 
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Upcoming vaccinations will appear here.")
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = { /* filter */ }, modifier = Modifier.weight(1f)) { 
                        Text("Filters") 
                    }
                    Button(onClick = { /* record */ }, modifier = Modifier.weight(1f)) { 
                        Text("Record Vaccination") 
                    }
                }
                
                // List with Vaccination Proof button
                OutlinedTextField(
                    value = sampleProductId.value, 
                    onValueChange = { sampleProductId.value = it }, 
                    label = { Text("Product ID for listing") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedButton(
                    onClick = { if (sampleProductId.value.isNotBlank()) onListProduct(sampleProductId.value) },
                    enabled = sampleProductId.value.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("List with Vaccination Proof")
                }
                
                if (sampleProductId.value.isNotBlank()) {
                    Text(
                        "Include complete vaccination records in marketplace listing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
