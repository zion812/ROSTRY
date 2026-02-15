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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.TextButton
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAddressSelection: () -> Unit,
    onNavigateToAdminVerification: () -> Unit = {},
    onNavigateToBackupRestore: () -> Unit = {},
    lastSelectedAddressJson: String? = null,
    isAdmin: Boolean = false,
    pendingCount: Int = 0,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showThemeMenu by remember { mutableStateOf(false) }
    var showLangMenu by remember { mutableStateOf(false) }

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
                         if (pendingCount > 0) {
                            BadgedBox(
                                badge = { Badge { Text(pendingCount.toString()) } }
                            ) {
                                Icon(Icons.Filled.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            Icon(Icons.Filled.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                )
                androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            Text("Preferences", style = MaterialTheme.typography.titleMedium)
            ListItem(
                headlineContent = { Text("Notifications") },
                trailingContent = {
                    Switch(checked = state.notificationsEnabled, onCheckedChange = { viewModel.setNotifications(it) })
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )
            ListItem(
                headlineContent = { Text("Low data mode") },
                supportingContent = { Text("Reduce media previews and background data usage") },
                trailingContent = {
                    Switch(checked = state.lowDataMode, onCheckedChange = { viewModel.setLowDataMode(it) })
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )

            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = { Text(state.theme.replaceFirstChar { it.uppercase() }) },
                leadingContent = { Icon(Icons.Filled.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingContent = {
                    TextButton(onClick = { showThemeMenu = true }) { Text("Change") }
                    DropdownMenu(expanded = showThemeMenu, onDismissRequest = { showThemeMenu = false }) {
                        listOf("system", "light", "dark").forEach { theme ->
                            DropdownMenuItem(
                                text = { Text(theme.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    viewModel.setTheme(theme)
                                    showThemeMenu = false
                                }
                            )
                        }
                    }
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )
            ListItem(
                headlineContent = { Text("Language") },
                supportingContent = {
                    val label = when (state.language) {
                        "hi" -> "हिन्दी"
                        "te" -> "తెలుగు"
                        else -> "English"
                    }
                    Text(label)
                },
                leadingContent = { Icon(Icons.Filled.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingContent = {
                    TextButton(onClick = { showLangMenu = true }) { Text("Change") }
                    DropdownMenu(expanded = showLangMenu, onDismissRequest = { showLangMenu = false }) {
                        listOf("en" to "English", "hi" to "हिन्दी", "te" to "తెలుగు").forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    viewModel.setLanguage(code)
                                    showLangMenu = false
                                }
                            )
                        }
                    }
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
            )

            Text("Data & Storage", style = MaterialTheme.typography.titleMedium)
            ListItem(
                headlineContent = { Text("Backup & Restore") },
                supportingContent = { Text("Export or import your farm data") },
                leadingContent = {
                    Icon(Icons.Filled.Backup, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingContent = {
                    TextButton(onClick = onNavigateToBackupRestore) { Text("Open") }
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
