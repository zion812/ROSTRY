package com.rio.rostry.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor() : ViewModel() {
    
    data class UiState(
        val isLoading: Boolean = true,
        val eventId: String = "",
        val eventName: String = "",
        val description: String = "",
        val date: Date = Date(),
        val location: String = "",
        val organizer: String = "",
        val attendeeCount: Int = 0,
        val maxAttendees: Int = 0,
        val category: String = "",
        val isRegistered: Boolean = false,
        val isFree: Boolean = true,
        val price: Double = 0.0,
        val agenda: List<String> = emptyList(),
        val error: String? = null
    )
    
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    
    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, eventId = eventId) }
            delay(500)
            
            _state.update { it.copy(
                isLoading = false,
                eventName = "Karnataka Poultry Expo 2026",
                description = "Annual gathering of poultry farmers, breeders, and enthusiasts. Features exhibitions, workshops, and networking opportunities.",
                date = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), // 1 week from now
                location = "Palace Grounds, Bangalore",
                organizer = "Karnataka Poultry Federation",
                attendeeCount = 342,
                maxAttendees = 500,
                category = "Exhibition",
                isRegistered = false,
                isFree = false,
                price = 500.0,
                agenda = listOf(
                    "09:00 AM - Registration & Breakfast",
                    "10:00 AM - Inauguration Ceremony",
                    "11:00 AM - Breed Exhibition Begins",
                    "01:00 PM - Lunch Break",
                    "02:00 PM - Expert Panel: Disease Prevention",
                    "04:00 PM - Networking Session",
                    "05:30 PM - Award Ceremony"
                )
            ) }
        }
    }
    
    fun toggleRegistration() {
        _state.update { it.copy(isRegistered = !it.isRegistered) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()) }
    
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Event Header
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small) {
                                Text(state.category, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(state.eventName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(state.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                // Event Details
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            DetailRow(Icons.Default.CalendarMonth, "Date", dateFormatter.format(state.date))
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            DetailRow(Icons.Default.LocationOn, "Location", state.location)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            DetailRow(Icons.Default.Person, "Organizer", state.organizer)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            DetailRow(Icons.Default.People, "Attendees", "${state.attendeeCount}/${state.maxAttendees}")
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            DetailRow(Icons.Default.AttachMoney, "Price", if (state.isFree) "Free" else "₹${state.price.toInt()}")
                        }
                    }
                }
                
                // Agenda
                item {
                    Text("Agenda", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(state.agenda) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Text(item, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                // Register Button
                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.toggleRegistration() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (state.isRegistered) ButtonDefaults.outlinedButtonColors() else ButtonDefaults.buttonColors()
                    ) {
                        Icon(if (state.isRegistered) Icons.Default.Check else Icons.Default.EventAvailable, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (state.isRegistered) "Registered" else "Register Now")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}
