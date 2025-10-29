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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
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
        }
    }
}

