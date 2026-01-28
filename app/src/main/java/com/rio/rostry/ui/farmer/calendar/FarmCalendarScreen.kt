package com.rio.rostry.ui.farmer.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.data.database.entity.FarmEventType
import com.rio.rostry.data.repository.CalendarEvent
import com.rio.rostry.data.repository.EventSource
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmCalendarScreen(
    viewModel: FarmCalendarViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val filteredEvents by viewModel.filteredEvents.collectAsStateWithLifecycle()
    val selectedDateMillis by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedEventType by viewModel.selectedEventType.collectAsStateWithLifecycle()
    
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }
    
    val selectedDate = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Add Event")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Month View
            CalendarMonthView(
                currentMonth = currentMonth,
                onMonthChange = { currentMonth = it },
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    viewModel.selectDate(millis)
                },
                events = events
            )

            HorizontalDivider()
            
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedEventType == null,
                    onClick = { viewModel.filterByType(null) },
                    label = { Text("All") }
                )
                FarmEventType.values().take(3).forEach { type ->
                     FilterChip(
                        selected = selectedEventType == type,
                        onClick = { viewModel.filterByType(if (selectedEventType == type) null else type) },
                        label = { Text(type.name.take(4)) } // Shortened for space
                    )
                }
            }

            // Day View (Events List)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (filteredEvents.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No events for this day",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredEvents) { event ->
                        EventItem(
                            event = event,
                            onClick = { selectedEvent = event }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            initialDate = selectedDateMillis,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, date, type, recurrence, reminder ->
                viewModel.createEvent(title, desc, date, type, recurrence, reminder)
                showAddDialog = false
            }
        )
    }

    selectedEvent?.let { event ->
        EventDetailSheet(
            event = event,
            onDismiss = { selectedEvent = null },
            onComplete = { 
                viewModel.markEventComplete(it)
                selectedEvent = null 
            },
            onDelete = { id ->
                viewModel.deleteEvent(id)
                selectedEvent = null
            }
        )
    }
}

@Composable
fun CalendarMonthView(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    events: List<CalendarEvent>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Month Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ArrowBack, "Previous Month")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ArrowForward, "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days Header
        Row(modifier = Modifier.fillMaxWidth()) {
            java.time.DayOfWeek.values().forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        val firstDayOfMonth = currentMonth.atDay(1)
        val daysInMonth = currentMonth.lengthOfMonth()
        val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value - 1 // Mon=1 -> 0, Sun=7 -> 6
        
        // Compute dots map for performance
        // Map<DayOfMonth, List<FarmEventType>>
        val eventsInMonth = remember(events, currentMonth) {
            events.filter { 
                val date = Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                YearMonth.from(date) == currentMonth
            }.groupBy { 
                 Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate().dayOfMonth
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp) // Fixed height for consistency
        ) {
            // Empty slots
            items(dayOfWeekOffset) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            
            items(daysInMonth) { dayIndex ->
                val day = dayIndex + 1
                val date = currentMonth.atDay(day)
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                val dayEvents = eventsInMonth[day] ?: emptyList()

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary 
                            else if (isToday) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = day.toString(),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (dayEvents.isNotEmpty()) {
                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                dayEvents.take(3).forEach { event ->
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                                else getEventColor(event.type)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getEventColor(event.type).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event, // Placeholder icon
                    contentDescription = null,
                    tint = getEventColor(event.type)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = Instant.ofEpochMilli(event.date)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (event.source == EventSource.RECOMMENDATION) {
                 Text(
                    text = "Recommended",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            // Status Indicator
            if (event.status == com.rio.rostry.data.database.entity.EventStatus.COMPLETED) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun getEventColor(type: FarmEventType): Color {
    return when (type) {
        FarmEventType.VACCINATION -> Color(0xFFE91E63) // Pink
        FarmEventType.DEWORMING -> Color(0xFF9C27B0) // Purple
        FarmEventType.BIOSECURITY -> Color(0xFF2196F3) // Blue
        FarmEventType.FEEDING -> Color(0xFF4CAF50) // Green
        FarmEventType.WEIGHING -> Color(0xFFFF9800) // Orange
        FarmEventType.CLEANING -> Color(0xFF00BCD4) // Cyan
        FarmEventType.OTHER -> Color(0xFF9E9E9E) // Grey
    }
}
