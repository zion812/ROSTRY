package com.rio.rostry.ui.general.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.UserEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralProfileRoute(
    viewModel: GeneralProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.success) {
        uiState.success?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    if (!uiState.isAuthenticated) {
        NotAuthenticatedState()
        return
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your profile", maxLines = 1, overflow = TextOverflow.Ellipsis) }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.profile == null -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No profile data available", style = MaterialTheme.typography.titleMedium)
                }
            }
            else -> {
                val profile = uiState.profile
                ProfileContent(
                    modifier = Modifier.padding(paddingValues),
                    profile = profile!!,
                    preferences = uiState.preferences,
                    orderHistory = uiState.orderHistory,
                    supportOptions = uiState.supportOptions,
                    onUpdateName = viewModel::updateProfileName,
                    onTogglePreference = viewModel::updatePreference
                )
            }
        }
    }
}

@Composable
private fun NotAuthenticatedState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.ShoppingBag, contentDescription = null, modifier = Modifier.height(48.dp))
        Spacer(Modifier.height(12.dp))
        Text("Sign in to view your profile", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ProfileContent(
    modifier: Modifier,
    profile: UserEntity,
    preferences: List<GeneralProfileViewModel.PreferenceToggle>,
    orderHistory: List<OrderEntity>,
    supportOptions: List<GeneralProfileViewModel.SupportOption>,
    onUpdateName: (String) -> Unit,
    onTogglePreference: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileHeader(profile = profile, onUpdateName = onUpdateName)
        }
        item {
            PreferencesSection(preferences = preferences, onToggle = onTogglePreference)
        }
        if (orderHistory.isNotEmpty()) {
            item {
                OrderHistorySection(orderHistory = orderHistory)
            }
        }
        item {
            SupportSection(options = supportOptions)
        }
    }
}

@Composable
private fun ProfileHeader(profile: UserEntity, onUpdateName: (String) -> Unit) {
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(profile.fullName ?: "General user", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    profile.phoneNumber?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                    profile.email?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
                IconButton(onClick = { onUpdateName((profile.fullName ?: "") + " ✨") }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit name")
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.address?.takeIf { it.isNotBlank() }?.let {
                    PreferenceChip(icon = Icons.Filled.LocationOn, label = it)
                }
                PreferenceChip(icon = Icons.Filled.Email, label = "Email updates ${if (profile.email.isNullOrBlank()) "off" else "on"}")
                PreferenceChip(icon = Icons.Filled.Phone, label = "Phone verified")
            }
        }
    }
}

@Composable
private fun PreferenceChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.height(18.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun PreferencesSection(
    preferences: List<GeneralProfileViewModel.PreferenceToggle>,
    onToggle: (String, Boolean) -> Unit
) {
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Preferences", style = MaterialTheme.typography.titleMedium)
            preferences.forEach { preference ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(preference.title, style = MaterialTheme.typography.bodyLarge)
                        Text("Manage ${preference.title.lowercase()}", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = preference.enabled,
                        onCheckedChange = { onToggle(preference.key, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderHistorySection(orderHistory: List<OrderEntity>) {
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Recent orders", style = MaterialTheme.typography.titleMedium)
            orderHistory.forEach { order ->
                Column(Modifier.fillMaxWidth()) {
                    Text(order.orderId.takeLast(6), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("Status: ${order.status}", style = MaterialTheme.typography.bodySmall)
                    Text("Amount: ₹${"%.2f".format(order.totalAmount)}", style = MaterialTheme.typography.bodySmall)
                }
                DividerSpacer()
            }
        }
    }
}

@Composable
private fun SupportSection(options: List<GeneralProfileViewModel.SupportOption>) {
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Support & feedback", style = MaterialTheme.typography.titleMedium)
            options.forEach { option ->
                Column(Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text(option.title, style = MaterialTheme.typography.bodyLarge)
                            Text(option.description, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = option.action) { Text("Open") }
                    }
                }
                DividerSpacer()
            }
        }
    }
}

@Composable
private fun DividerSpacer() {
    Spacer(Modifier.height(8.dp))
    androidx.compose.material3.Divider()
    Spacer(Modifier.height(8.dp))
}
