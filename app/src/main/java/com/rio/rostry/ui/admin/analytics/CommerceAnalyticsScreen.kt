package com.rio.rostry.ui.admin.analytics

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
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommerceAnalyticsScreen(
    viewModel: CommerceAnalyticsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Commerce Analytics") },
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
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Revenue Stats
                item { Text("Revenue Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RevenueCard(Modifier.weight(1f), "Total Revenue", currencyFormatter.format(state.totalRevenue), Color(0xFF4CAF50))
                        RevenueCard(Modifier.weight(1f), "This Month", currencyFormatter.format(state.revenueThisMonth), Color(0xFF2196F3))
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RevenueCard(Modifier.weight(1f), "This Week", currencyFormatter.format(state.revenueThisWeek), Color(0xFF9C27B0))
                        RevenueCard(Modifier.weight(1f), "Avg Order", currencyFormatter.format(state.avgOrderValue), Color(0xFFFF9800))
                    }
                }

                // Order Stats
                item { Spacer(Modifier.height(8.dp)); Text("Order Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OrderStatCard(Modifier.weight(1f), "Total", state.totalOrders, Color(0xFF607D8B))
                        OrderStatCard(Modifier.weight(1f), "Completed", state.completedOrders, Color(0xFF4CAF50))
                        OrderStatCard(Modifier.weight(1f), "Pending", state.pendingOrders, Color(0xFFFF9800))
                        OrderStatCard(Modifier.weight(1f), "Disputed", state.disputedOrders, Color(0xFFD32F2F))
                    }
                }

                // Top Products
                item { Spacer(Modifier.height(8.dp)); Text("Top Products", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(state.topProducts) { product ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.Medium)
                                Text("${product.sales} sold", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(currencyFormatter.format(product.revenue), fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                    }
                }

                // Top Sellers
                item { Spacer(Modifier.height(8.dp)); Text("Top Sellers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(state.topSellers) { seller ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(seller.name, fontWeight = FontWeight.Medium)
                                Text("${seller.orders} orders", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(currencyFormatter.format(seller.revenue), fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RevenueCard(modifier: Modifier, title: String, value: String, color: Color) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = color)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun OrderStatCard(modifier: Modifier, title: String, count: Int, color: Color) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}
