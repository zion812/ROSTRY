package com.rio.rostry.ui.admin.system

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemConfigScreen(
    onNavigateBack: () -> Unit
) {
    val configItems = remember {
        listOf(
            ConfigItem("Max Upload Size", "10 MB", "Maximum file size for uploads"),
            ConfigItem("Session Timeout", "30 minutes", "Auto-logout after inactivity"),
            ConfigItem("Password Policy", "Strong (8+ chars, special)", "Password requirements"),
            ConfigItem("Email Notifications", "Enabled", "System email notifications"),
            ConfigItem("SMS Alerts", "Enabled", "Critical SMS alerts"),
            ConfigItem("Maintenance Mode", "Disabled", "Put system in maintenance"),
            ConfigItem("API Rate Limit", "100 req/min", "Rate limiting per user"),
            ConfigItem("Cache TTL", "5 minutes", "Default cache expiration")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Configuration") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                        Spacer(Modifier.width(12.dp))
                        Text("Configuration changes require admin approval", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            items(configItems) { item ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Medium)
                            Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                            Text(item.value, Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

private data class ConfigItem(val name: String, val value: String, val description: String)
