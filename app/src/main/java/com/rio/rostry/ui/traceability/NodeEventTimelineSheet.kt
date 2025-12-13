package com.rio.rostry.ui.traceability

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
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.ui.traceability.TraceabilityViewModel.NodeEventData
import com.rio.rostry.ui.traceability.TraceabilityViewModel.NodeMetadata
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.PaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeEventTimelineSheet(
    nodeMetadata: NodeMetadata,
    events: NodeEventData,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(nodeMetadata.name, style = MaterialTheme.typography.titleLarge)
                        Text("ID: ${nodeMetadata.productId}", style = MaterialTheme.typography.bodyMedium)
                        nodeMetadata.breed?.let {
                            Text("Breed: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        nodeMetadata.ageWeeks?.let {
                            Text("Age: ${it} weeks", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    // Health score badge
                    val healthColor = when {
                        events.healthScore > 70 -> Color(0xFF4CAF50) // Green
                        events.healthScore > 50 -> Color(0xFFFFC107) // Yellow
                        else -> Color(0xFFF44336) // Red
                    }
                    Badge(
                        containerColor = healthColor,
                        contentColor = Color.White
                    ) {
                        Text("${events.healthScore}")
                    }
                }
                Text("Based on recent vaccination, health logs, and growth records", style = MaterialTheme.typography.bodySmall)
            }

            // Vaccination History
            item {
                Text("Vaccination History", style = MaterialTheme.typography.titleMedium)
            }
            if (events.vaccinations.isEmpty()) {
                item {
                    Text("No vaccination records yet. Add vaccination schedule in Farm Monitoring.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(items = events.vaccinations.sortedByDescending { it.scheduledAt }, key = { it.hashCode() }) { vacc ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (vacc.administeredAt != null) Icons.Filled.Check else Icons.Filled.Schedule,
                                contentDescription = if (vacc.administeredAt != null) "Administered" else "Scheduled"
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(vacc.vaccineType, style = MaterialTheme.typography.bodyLarge)
                                Text("Scheduled: ${formatDate(vacc.scheduledAt)}", style = MaterialTheme.typography.bodySmall)
                                vacc.administeredAt?.let {
                                    Text("Administered: ${formatDate(it)}", style = MaterialTheme.typography.bodySmall)
                                }
                                vacc.supplier?.let {
                                    Text("Supplier: $it", style = MaterialTheme.typography.bodySmall)
                                }
                                vacc.batchCode?.let {
                                    Text("Batch: $it", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            // Growth Records
            item {
                Text("Growth Records", style = MaterialTheme.typography.titleMedium)
            }
            if (events.growthRecords.isEmpty()) {
                item {
                    Text("No growth records yet.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                if (events.growthRecords.size >= 2) {
                    item {
                        com.rio.rostry.ui.components.GrowthChart(
                            records = events.growthRecords,
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )
                    }
                }

                items(items = events.growthRecords.sortedBy { it.week }, key = { it.week }) { growth ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Week ${growth.week}", style = MaterialTheme.typography.bodyLarge)
                            growth.weightGrams?.let {
                                Text("Weight: ${it}g", style = MaterialTheme.typography.bodyMedium)
                            }
                            growth.heightCm?.let {
                                Text("Height: ${it}cm", style = MaterialTheme.typography.bodyMedium)
                            }
                            growth.healthStatus?.let {
                                Text("Health: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            growth.milestone?.let {
                                Text("Milestone: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Daily Logs (last 7 days)
            item {
                Text("Recent Daily Logs", style = MaterialTheme.typography.titleMedium)
            }
            val recentLogs = events.dailyLogs.filter { it.logDate > System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 }.sortedByDescending { it.logDate }
            if (recentLogs.isEmpty()) {
                item {
                    Text("No recent daily logs.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(items = recentLogs, key = { it.hashCode() }) { log ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Log date")
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(formatDate(log.logDate), style = MaterialTheme.typography.bodyLarge)
                                log.weightGrams?.let {
                                    Text("Weight: ${it}g", style = MaterialTheme.typography.bodyMedium)
                                }
                                log.feedKg?.let {
                                    Text("Feed: ${it}kg", style = MaterialTheme.typography.bodyMedium)
                                }
                                log.notes?.let {
                                    Text("Notes: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                                log.symptomsJson?.let {
                                    Text("Symptoms: $it", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            // Transfer History
            item {
                Text("Transfer History", style = MaterialTheme.typography.titleMedium)
            }
            if (events.transfers.isEmpty()) {
                item {
                    Text("No transfer history.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(items = events.transfers.sortedByDescending { it.initiatedAt }, key = { it.hashCode() }) { transfer ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ArrowForward, contentDescription = "Transfer")
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(transfer.type, style = MaterialTheme.typography.bodyLarge)
                                Text("Status: ${transfer.status}", style = MaterialTheme.typography.bodyMedium)
                                Text("From: ${transfer.fromUserId}", style = MaterialTheme.typography.bodySmall)
                                Text("To: ${transfer.toUserId}", style = MaterialTheme.typography.bodySmall)
                                Text("Date: ${formatDate(transfer.initiatedAt)}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}