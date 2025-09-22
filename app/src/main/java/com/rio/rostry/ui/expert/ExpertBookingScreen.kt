package com.rio.rostry.ui.expert

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ExpertBookingScreen(onBack: () -> Unit, vm: ExpertViewModel = hiltViewModel()) {
    var category by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    val bookings = vm.bookings.collectAsState().value
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Expert Bookings", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
            items(bookings) { b ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(b.topic ?: "Consultation")
                        Text("Status: ${b.status}")
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Button(onClick = { vm.updateStatus(b.bookingId, "CONFIRMED") }) { Text("Confirm") }
                            Button(onClick = { vm.updateStatus(b.bookingId, "COMPLETED") }, modifier = Modifier.padding(start = 8.dp)) { Text("Complete") }
                        }
                    }
                }
            }
        }
        Text("New Request", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(value = category, onValueChange = { category = it }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), label = { Text("Category") })
        OutlinedTextField(value = details, onValueChange = { details = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Details") })
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(onClick = { vm.createRequest(expertId = "expert-1", userId = "me", topic = category.ifBlank { null }, details = details.ifBlank { null }) }) { Text("Request") }
            Button(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) { Text("Back") }
        }
    }
}
