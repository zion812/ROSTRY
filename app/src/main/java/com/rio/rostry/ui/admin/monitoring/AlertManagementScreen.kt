package com.rio.rostry.ui.admin.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertManagementScreen(
    viewModel: AlertManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("System Alerts")
                        if (state.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text(state.unreadCount.toString()) }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Mark All Read")
                        }
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                item {
                    FilterChip(
                        selected = state.filterType == null,
                        onClick = { viewModel.setFilter(null) },
                        label = { Text("All") }
                    )
                }
                items(AlertManagementViewModel.AlertType.entries.toList()) { type ->
                    FilterChip(
                        selected = state.filterType == type,
                        onClick = { viewModel.setFilter(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        leadingIcon = {
                            Icon(
                                getIconForType(type),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            // Alert List
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.alerts.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.NotificationsOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No alerts")
                        }
                    }
                }
                else -> {
                    val filteredAlerts = if (state.filterType != null) {
                        state.alerts.filter { it.type == state.filterType }
                    } else {
                        state.alerts
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredAlerts.sortedByDescending { it.createdAt }) { alert ->
                            AlertCard(
                                alert = alert,
                                onMarkRead = { viewModel.markAsRead(alert.id) },
                                onDismiss = { viewModel.dismissAlert(alert.id) },
                                onAction = { /* Navigate to relevant screen */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(
    alert: AlertManagementViewModel.SystemAlert,
    onMarkRead: () -> Unit,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    val (color, icon) = when (alert.severity) {
        AlertManagementViewModel.AlertSeverity.CRITICAL -> Color(0xFFD32F2F) to Icons.Default.Error
        AlertManagementViewModel.AlertSeverity.ERROR -> Color(0xFFFF5722) to Icons.Default.Warning
        AlertManagementViewModel.AlertSeverity.WARNING -> Color(0xFFFF9800) to Icons.Default.Info
        AlertManagementViewModel.AlertSeverity.INFO -> Color(0xFF2196F3) to Icons.Default.Info
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (!alert.isRead) {
            CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        alert.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (!alert.isRead) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (alert.isRead) TextDecoration.None else TextDecoration.None
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = getColorForType(alert.type).copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            alert.type.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = getColorForType(alert.type)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(alert.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!alert.isRead) {
                            TextButton(onClick = onMarkRead) {
                                Text("Mark Read", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        if (alert.isActionable) {
                            Button(
                                onClick = onAction,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(alert.actionLabel ?: "View", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getIconForType(type: AlertManagementViewModel.AlertType) = when (type) {
    AlertManagementViewModel.AlertType.SECURITY -> Icons.Default.Security
    AlertManagementViewModel.AlertType.SYSTEM -> Icons.Default.Settings
    AlertManagementViewModel.AlertType.VERIFICATION -> Icons.Default.VerifiedUser
    AlertManagementViewModel.AlertType.COMMERCE -> Icons.Default.ShoppingCart
    AlertManagementViewModel.AlertType.BIOSECURITY -> Icons.Default.Shield
    AlertManagementViewModel.AlertType.MORTALITY -> Icons.Default.TrendingDown
}

@Composable
private fun getColorForType(type: AlertManagementViewModel.AlertType) = when (type) {
    AlertManagementViewModel.AlertType.SECURITY -> Color(0xFFD32F2F)
    AlertManagementViewModel.AlertType.SYSTEM -> Color(0xFF607D8B)
    AlertManagementViewModel.AlertType.VERIFICATION -> Color(0xFF2196F3)
    AlertManagementViewModel.AlertType.COMMERCE -> Color(0xFF4CAF50)
    AlertManagementViewModel.AlertType.BIOSECURITY -> Color(0xFFFF9800)
    AlertManagementViewModel.AlertType.MORTALITY -> Color(0xFF9C27B0)
}
