package com.rio.rostry.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAddressSelection: () -> Unit,
    onNavigateToAdminVerification: () -> Unit = {},
    lastSelectedAddressJson: String? = null,
    isAdmin: Boolean = false
) {
    val notifications = remember { mutableStateOf(true) }
    val lowDataMode = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isAdmin) {
                Text("Administrator", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                ListItem(
                    headlineContent = { Text("Verification Dashboard") },
                    supportingContent = { Text("Review and manage user KYC submissions") },
                    trailingContent = {
                        TextButton(onClick = onNavigateToAdminVerification) { Text("Manage") }
                    },
                    leadingContent = {
                        Icon(androidx.compose.material.icons.Icons.Default.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                )
                androidx.compose.material3.Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            Text("Preferences", style = MaterialTheme.typography.titleMedium)
            ListItem(
                headlineContent = { Text("Notifications") },
                trailingContent = {
                    Switch(checked = notifications.value, onCheckedChange = { notifications.value = it })
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )
            ListItem(
                headlineContent = { Text("Low data mode") },
                supportingContent = { Text("Reduce media previews and background data usage") },
                trailingContent = {
                    Switch(checked = lowDataMode.value, onCheckedChange = { lowDataMode.value = it })
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )

            Text("Location & Address", style = MaterialTheme.typography.titleMedium)
            ListItem(
                headlineContent = { Text("Pick address (Google Maps Web)") },
                supportingContent = {
                    if (!lastSelectedAddressJson.isNullOrBlank()) {
                        Text(
                            text = "Last: ${lastSelectedAddressJson.take(96)}" + if (lastSelectedAddressJson.length > 96) "…" else "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text("Open web selector to autocomplete your address")
                    }
                },
                trailingContent = {
                    TextButton(onClick = onOpenAddressSelection) { Text("Open") }
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    }
}


