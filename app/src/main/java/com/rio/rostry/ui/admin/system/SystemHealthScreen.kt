package com.rio.rostry.ui.admin.system

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemHealthScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Health") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Service Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HealthCard(Modifier.weight(1f), "API Server", "Operational", true, Icons.Default.Cloud)
                    HealthCard(Modifier.weight(1f), "Database", "Operational", true, Icons.Default.Storage)
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HealthCard(Modifier.weight(1f), "Auth Service", "Operational", true, Icons.Default.Security)
                    HealthCard(Modifier.weight(1f), "Push Notifs", "Degraded", false, Icons.Default.Notifications)
                }
            }

            item { Spacer(Modifier.height(8.dp)); Text("Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            item { MetricRow("API Latency", "45ms", Color(0xFF4CAF50)) }
            item { MetricRow("Database Queries", "2.3k/min", Color(0xFF2196F3)) }
            item { MetricRow("Memory Usage", "67%", Color(0xFFFF9800)) }
            item { MetricRow("CPU Usage", "34%", Color(0xFF4CAF50)) }
            item { MetricRow("Active Connections", "156", Color(0xFF2196F3)) }
            item { MetricRow("Error Rate", "0.02%", Color(0xFF4CAF50)) }
        }
    }
}

@Composable
private fun HealthCard(modifier: Modifier, name: String, status: String, isHealthy: Boolean, icon: ImageVector) {
    val color = if (isHealthy) Color(0xFF4CAF50) else Color(0xFFFF9800)
    Card(modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(status, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, color: Color) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label)
            Text(value, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
