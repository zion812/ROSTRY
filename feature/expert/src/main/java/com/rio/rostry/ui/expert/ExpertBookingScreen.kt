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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.ExpertBookingEntity

@Composable
fun ExpertBookingScreen(
    bookings: List<ExpertBookingEntity>,
    onConfirm: (String) -> Unit,
    onComplete: (String) -> Unit,
    onRequest: (topic: String?, details: String?) -> Unit,
    onBack: () -> Unit
) {
    var category by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Expert Bookings", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
            items(bookings, key = { it.bookingId }) { booking ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(booking.topic ?: "Consultation")
                        Text("Status: ${booking.status}")
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Button(onClick = { onConfirm(booking.bookingId) }) { Text("Confirm") }
                            Button(
                                onClick = { onComplete(booking.bookingId) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) { Text("Complete") }
                        }
                    }
                }
            }
        }
        Text("New Request", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            label = { Text("Category") }
        )
        OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            label = { Text("Details") }
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(onClick = { onRequest(category.ifBlank { null }, details.ifBlank { null }) }) { Text("Request") }
            Button(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) { Text("Back") }
        }
    }
}
