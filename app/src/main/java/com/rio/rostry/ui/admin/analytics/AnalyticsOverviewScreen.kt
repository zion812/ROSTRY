package com.rio.rostry.ui.admin.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsOverviewScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToUsers: () -> Unit = {},
    onNavigateToCommerce: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Dashboard") },
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
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Key Metrics Row
                item {
                    Text("Key Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Users",
                            value = state.totalUsers.toString(),
                            growth = state.userGrowthPercent,
                            icon = Icons.Default.People,
                            color = Color(0xFF2196F3)
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Active Today",
                            value = state.activeUsersToday.toString(),
                            icon = Icons.Default.TrendingUp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Orders",
                            value = state.totalOrders.toString(),
                            growth = state.orderGrowthPercent,
                            icon = Icons.Default.ShoppingCart,
                            color = Color(0xFFFF9800)
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Revenue",
                            value = "₹${(state.totalRevenue / 100000).toInt()}L",
                            growth = state.revenueGrowthPercent,
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                // Status Cards
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusCard(
                            modifier = Modifier.weight(1f),
                            title = "Pending Verifications",
                            count = state.pendingVerifications,
                            color = Color(0xFFFF9800)
                        )
                        StatusCard(
                            modifier = Modifier.weight(1f),
                            title = "Active Disputes",
                            count = state.activeDisputes,
                            color = Color(0xFFD32F2F)
                        )
                        StatusCard(
                            modifier = Modifier.weight(1f),
                            title = "New Users (7d)",
                            count = state.newUsersThisWeek,
                            color = Color(0xFF2196F3)
                        )
                    }
                }

                // Quick Links
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Analytics Reports", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickLinkCard(
                            modifier = Modifier.weight(1f),
                            title = "User Analytics",
                            subtitle = "Growth, retention, activity",
                            icon = Icons.Default.People,
                            onClick = onNavigateToUsers
                        )
                        QuickLinkCard(
                            modifier = Modifier.weight(1f),
                            title = "Commerce Analytics",
                            subtitle = "Sales, orders, products",
                            icon = Icons.Default.Store,
                            onClick = onNavigateToCommerce
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    growth: Double? = null,
    icon: ImageVector,
    color: Color
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (growth != null && growth != 0.0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (growth > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (growth > 0) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${if (growth > 0) "+" else ""}${String.format("%.1f", growth)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (growth > 0) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                    )
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
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun QuickLinkCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
