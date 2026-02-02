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
fun FeatureTogglesScreen(
    onNavigateBack: () -> Unit
) {
    val features = remember {
        mutableStateListOf(
            FeatureToggle("New Checkout Flow", "Redesigned checkout experience", true, "v2.4"),
            FeatureToggle("AI Price Suggestions", "ML-powered pricing recommendations", false, "Beta"),
            FeatureToggle("Video Evidence", "Allow video uploads for disputes", true, "v2.3"),
            FeatureToggle("Dark Mode", "System-wide dark theme", true, "v2.0"),
            FeatureToggle("Biometric Auth", "Fingerprint/Face ID login", true, "v2.1"),
            FeatureToggle("Chat Support", "In-app customer support chat", false, "Planned"),
            FeatureToggle("Batch Orders", "Bulk order processing", true, "v2.2"),
            FeatureToggle("Export to Excel", "Data export functionality", true, "v2.0"),
            FeatureToggle("SMS OTP", "SMS-based verification", true, "v1.8"),
            FeatureToggle("Social Login", "Google/Facebook sign-in", false, "Beta")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feature Toggles") },
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
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Toggle features on/off for all users", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            items(features) { feature ->
                FeatureToggleCard(
                    feature = feature,
                    onToggle = { enabled ->
                        val idx = features.indexOf(feature)
                        if (idx >= 0) features[idx] = feature.copy(isEnabled = enabled)
                    }
                )
            }
        }
    }
}

private data class FeatureToggle(val name: String, val description: String, val isEnabled: Boolean, val version: String)

@Composable
private fun FeatureToggleCard(feature: FeatureToggle, onToggle: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(feature.name, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = if (feature.version.contains("Beta") || feature.version.contains("Planned"))
                            Color(0xFFFF9800).copy(alpha = 0.2f) else Color(0xFF4CAF50).copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            feature.version,
                            Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (feature.version.contains("Beta") || feature.version.contains("Planned"))
                                Color(0xFFFF9800) else Color(0xFF4CAF50)
                        )
                    }
                }
                Text(feature.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = feature.isEnabled, onCheckedChange = onToggle)
        }
    }
}
