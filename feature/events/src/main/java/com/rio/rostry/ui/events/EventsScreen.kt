package com.rio.rostry.ui.events

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

data class EventItem(
    val eventId: String,
    val title: String,
    val location: String? = null
)

@Composable
fun EventsScreen(
    events: List<EventItem>,
    onRsvpGoing: (String) -> Unit,
    onRsvpInterested: (String) -> Unit,
    onCreateEvent: (title: String, description: String?, location: String?) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Events", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
            items(events, key = { it.eventId }) { event ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(event.title, style = MaterialTheme.typography.titleSmall)
                        event.location?.let { Text(it) }
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Button(onClick = { onRsvpGoing(event.eventId) }) { Text("RSVP Going") }
                            Button(
                                onClick = { onRsvpInterested(event.eventId) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) { Text("Interested") }
                        }
                    }
                }
            }
        }
        Text("Create Event", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            label = { Text("Title") }
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            label = { Text("Location") }
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            label = { Text("Description") }
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(
                onClick = {
                    onCreateEvent(
                        title.ifBlank { "Untitled" },
                        description.ifBlank { null },
                        location.ifBlank { null }
                    )
                }
            ) { Text("Create") }
            Button(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) { Text("Back") }
        }
    }
}
