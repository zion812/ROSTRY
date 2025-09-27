package com.rio.rostry.ui.events

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EventDetailsScreen(eventId: String, onBack: () -> Unit, vm: EventsViewModel = hiltViewModel()) {
    val event = vm.eventFlow(eventId).collectAsStateWithLifecycle(initialValue = null).value
    val rsvps = vm.rsvpsFlow(eventId).collectAsStateWithLifecycle(initialValue = emptyList()).value
    val me = vm.currentUserId()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Event Details", style = MaterialTheme.typography.titleLarge)
        if (event == null) {
            Text("Loading...")
        } else {
            Card(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(event.title, style = MaterialTheme.typography.titleMedium)
                    event.location?.let { Text(it) }
                    event.description?.let { Text(it, modifier = Modifier.padding(top = 6.dp)) }
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.rsvp(event.eventId, me, "GOING") }) { Text("RSVP Going") }
                        Button(onClick = { vm.rsvp(event.eventId, me, "INTERESTED") }) { Text("Interested") }
                    }
                }
            }
            Text("Attendees", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 12.dp))
            LazyColumn(Modifier.fillMaxWidth()) {
                items(rsvps) { r ->
                    Text("${r.userId.take(10)} - ${r.status}")
                }
            }
        }
    }
}
