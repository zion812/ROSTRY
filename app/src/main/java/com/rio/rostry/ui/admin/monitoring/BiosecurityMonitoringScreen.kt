package com.rio.rostry.ui.admin.monitoring

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
import com.rio.rostry.data.database.entity.DiseaseZoneEntity
import com.rio.rostry.data.database.entity.ZoneSeverity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiosecurityMonitoringScreen(
    viewModel: BiosecurityMonitoringViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biosecurity Monitoring") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Overview Cards
            item {
                Text(
                    "Zone Status Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "Active Zones",
                        count = state.activeZones,
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.LocationOn
                    )
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "Lockdown",
                        count = state.lockdownZones,
                        color = Color(0xFFD32F2F),
                        icon = Icons.Default.Lock
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "Restricted",
                        count = state.restrictedZones,
                        color = Color(0xFFFF9800),
                        icon = Icons.Default.Warning
                    )
                    StatusCard(
                        modifier = Modifier.weight(1f),
                        title = "Warnings",
                        count = state.warningZones,
                        color = Color(0xFFFFC107),
                        icon = Icons.Default.Info
                    )
                }
            }

            // Active Zones List
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Active Disease Zones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.zones.filter { it.isActive }.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("All clear! No active disease zones.")
                        }
                    }
                }
            } else {
                items(state.zones.filter { it.isActive }.sortedByDescending { it.severity }) { zone ->
                    ZoneAlertCard(zone = zone)
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(title, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}

@Composable
private fun ZoneAlertCard(zone: DiseaseZoneEntity) {
    val (color, icon) = when (zone.severity) {
        ZoneSeverity.LOCKDOWN -> Color(0xFFD32F2F) to Icons.Default.Lock
        ZoneSeverity.RESTRICTED -> Color(0xFFFF9800) to Icons.Default.Warning
        ZoneSeverity.WARNING -> Color(0xFFFFC107) to Icons.Default.Info
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        zone.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = color,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            zone.severity.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
                
                Text(
                    zone.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "📍 ${zone.radiusMeters.toInt()}m radius",
                        style = MaterialTheme.typography.labelSmall
                    )
                    zone.expiresAt?.let {
                        Text(
                            "⏰ Expires: ${SimpleDateFormat("MMM dd", Locale.getDefault()).format(it)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
