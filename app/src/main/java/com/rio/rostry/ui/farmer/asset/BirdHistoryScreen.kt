package com.rio.rostry.ui.farmer.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Bird History Screen - Unified chronological timeline of all events for a bird/batch.
 * Shows vaccinations, growth measurements, mortality, activities, and daily logs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdHistoryScreen(
    assetId: String,
    onNavigateBack: () -> Unit,
    onEventClick: (String, String) -> Unit, // (eventId, eventType) -> navigate to detail
    viewModel: BirdHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("History", fontWeight = FontWeight.Bold)
                        state.asset?.let { asset ->
                            Text(
                                asset.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.filterOptions) { filter ->
                    FilterChip(
                        selected = (filter == "ALL" && state.selectedFilter == null) || 
                                   state.selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter.lowercase().replaceFirstChar { it.uppercase() }) },
                        leadingIcon = if ((filter == "ALL" && state.selectedFilter == null) || 
                                          state.selectedFilter == filter) {
                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(state.error ?: "Error")
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onNavigateBack) { Text("Go Back") }
                        }
                    }
                }
                state.filteredEvents.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.History,
                                null,
                                Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("No events found", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Activity will appear here as you record it",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    // Timeline
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(state.filteredEvents) { event ->
                            TimelineEventItem(
                                event = event,
                                dateFormatter = dateFormatter,
                                isLast = event == state.filteredEvents.last(),
                                onClick = { onEventClick(event.id, event.type) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineEventItem(
    event: TimelineEvent,
    dateFormatter: SimpleDateFormat,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(getEventColor(event.type))
            )
            // Line (if not last)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        // Event Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 8.dp),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getEventColor(event.type).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        getEventIcon(event.type),
                        null,
                        tint = getEventColor(event.type),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Title and type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            getEventTitle(event),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            dateFormatter.format(Date(event.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Description
                    Text(
                        getEventDescription(event),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getEventTitle(event: TimelineEvent): String = when (event) {
    is TimelineEvent.Vaccination -> event.vaccineType
    is TimelineEvent.Growth -> "Week ${event.week} Measurement"
    is TimelineEvent.Mortality -> "Mortality Record"
    is TimelineEvent.Activity -> event.activityType.lowercase().replaceFirstChar { it.uppercase() }
    is TimelineEvent.DailyLog -> "Daily Log"
}

private fun getEventDescription(event: TimelineEvent): String = when (event) {
    is TimelineEvent.Vaccination -> if (event.isAdministered) "Administered" else "Scheduled"
    is TimelineEvent.Growth -> event.weightGrams?.let { "${it.toInt()}g" } ?: "Weight recorded"
    is TimelineEvent.Mortality -> "${event.quantity} birds - ${event.causeCategory}"
    is TimelineEvent.Activity -> event.amountInr?.let { "₹%.2f".format(it) } ?: (event.description ?: "Activity recorded")
    is TimelineEvent.DailyLog -> buildString {
        event.feedKg?.let { append("Feed: ${it}kg ") }
        event.weightGrams?.let { append("Weight: ${it.toInt()}g") }
        if (isEmpty()) append("Log recorded")
    }
}

@Composable
private fun getEventIcon(type: String): ImageVector = when (type) {
    "VACCINATION" -> Icons.Default.Vaccines
    "GROWTH" -> Icons.Default.TrendingUp
    "MORTALITY" -> Icons.Default.Warning
    "ACTIVITY" -> Icons.Default.Event
    "DAILY_LOG" -> Icons.Default.EventNote
    else -> Icons.Default.Circle
}

@Composable
private fun getEventColor(type: String): Color = when (type) {
    "VACCINATION" -> Color(0xFF7C3AED) // Purple
    "GROWTH" -> Color(0xFF2563EB) // Blue
    "MORTALITY" -> Color(0xFFDC2626) // Red
    "ACTIVITY" -> Color(0xFF16A34A) // Green
    "DAILY_LOG" -> Color(0xFFEA580C) // Orange
    else -> Color(0xFF6B7280) // Gray
}
