package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FarmerCommunityScreen(
    onOpenThread: (String) -> Unit,
    onOpenGroupDirectory: () -> Unit,
    onOpenExpertBooking: () -> Unit,
    onOpenRegionalNews: () -> Unit,
    vm: FarmerCommunityViewModel = hiltViewModel()
) {
    val activeThreads by vm.activeThreads.collectAsState()
    val suggestedGroups by vm.suggestedGroups.collectAsState()
    val upcomingEvents by vm.upcomingEvents.collectAsState()
    val availableExperts by vm.availableExperts.collectAsState()
    val unreadCount by vm.unreadCount.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    Surface(Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Community", style = MaterialTheme.typography.titleLarge)
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = onOpenGroupDirectory, modifier = Modifier.weight(1f)) { Text("Groups") }
                        Button(onClick = onOpenExpertBooking, modifier = Modifier.weight(1f)) { Text("Expert Consult") }
                        OutlinedButton(onClick = onOpenRegionalNews, modifier = Modifier.weight(1f)) { Text("Regional News") }
                    }
                }

                item { Divider() }

                // Messages Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Messages", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (unreadCount > 0) {
                            Badge { Text("$unreadCount") }
                        }
                    }
                }

                if (activeThreads.isEmpty()) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Text(
                                "No active conversations. Start one below!",
                                Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(activeThreads) { thread ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(thread.title, style = MaterialTheme.typography.titleMedium)
                                    thread.context?.type?.let { contextType ->
                                        Badge {
                                            Text(
                                                when (contextType) {
                                                    "PRODUCT_INQUIRY" -> "Product"
                                                    "EXPERT_CONSULT" -> "Expert"
                                                    "BREEDING_DISCUSSION" -> "Breeding"
                                                    else -> "Chat"
                                                }
                                            )
                                        }
                                    }
                                }
                                Text(thread.snippet, modifier = Modifier.padding(top = 4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Button(onClick = { onOpenThread(thread.threadId) }) { Text("Open") }
                                    OutlinedButton(onClick = { vm.dismissThread(thread.threadId) }) { Text("Mute") }
                                }
                            }
                        }
                    }
                }

                // Suggested Groups Section
                if (suggestedGroups.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Suggested Groups", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    items(suggestedGroups.take(5)) { group ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(group.name, style = MaterialTheme.typography.titleSmall)
                                    group.description?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Button(onClick = { vm.joinGroup(group.groupId) }) {
                                    Text("Join")
                                }
                            }
                        }
                    }
                }

                // Upcoming Events Section
                if (upcomingEvents.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Upcoming Events", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    items(upcomingEvents.take(3)) { event ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(event.title, style = MaterialTheme.typography.titleSmall)
                                    event.location?.let {
                                        Text("Location: $it", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Button(onClick = { vm.rsvpToEvent(event.eventId, "GOING") }) {
                                    Text("RSVP")
                                }
                            }
                        }
                    }
                }

                // Available Experts Section
                if (availableExperts.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Available Experts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    items(availableExperts.take(3)) { expert ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(expert.name, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "Specialties: ${expert.specialties.joinToString()}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text("Rating: ${expert.rating}/5.0", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(
                                    onClick = { vm.bookExpert(expert.userId, expert.specialties.firstOrNull() ?: "", System.currentTimeMillis()) },
                                    enabled = expert.availability
                                ) {
                                    Text("Book")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
