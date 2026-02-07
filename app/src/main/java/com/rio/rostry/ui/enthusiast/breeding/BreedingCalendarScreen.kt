package com.rio.rostry.ui.enthusiast.breeding

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingCalendarScreen(
    viewModel: BreedingCalendarViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Calendar View
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { context ->
                        CalendarView(context).apply {
                            setOnDateChangeListener { _, year, month, dayOfMonth ->
                                val calendar = Calendar.getInstance()
                                calendar.set(year, month, dayOfMonth)
                                viewModel.selectDate(calendar.timeInMillis)
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Events for ${dateFormatter.format(Date(state.selectedDate))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(Modifier.height(8.dp))

            // Events List
            val eventsForDay = state.events.filter { event ->
                val eventCal = Calendar.getInstance().apply { timeInMillis = event.date }
                val selectedCal = Calendar.getInstance().apply { timeInMillis = state.selectedDate }
                eventCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                eventCal.get(Calendar.DAY_OF_YEAR) == selectedCal.get(Calendar.DAY_OF_YEAR)
            }

            if (eventsForDay.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No events scheduled", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(eventsForDay) { event ->
                        EventCard(event, timeFormatter)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: BreedingCalendarViewModel.BreedingEvent, timeFormatter: SimpleDateFormat) {
    val (color, icon) = when (event.type) {
        BreedingCalendarViewModel.EventType.HATCH_DUE -> Color(0xFF4CAF50) to Icons.Default.Egg
        BreedingCalendarViewModel.EventType.MATING -> Color(0xFFE91E63) to Icons.Default.Favorite
        BreedingCalendarViewModel.EventType.VACCINATION -> Color(0xFF2196F3) to Icons.Default.MedicalServices
    }

    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column {
                Text(event.title, fontWeight = FontWeight.Bold)
                Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Spacer(Modifier.weight(1f))
            
            Text(timeFormatter.format(Date(event.date)), style = MaterialTheme.typography.labelSmall)
        }
    }
}
