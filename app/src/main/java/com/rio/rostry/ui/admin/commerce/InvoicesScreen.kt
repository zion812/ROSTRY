package com.rio.rostry.ui.admin.commerce

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: InvoicesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoices & Payments") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Revenue Summary
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Revenue",
                        amount = currencyFormatter.format(state.totalRevenue),
                        color = Color(0xFF4CAF50),
                        icon = Icons.Default.TrendingUp
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Pending",
                        amount = currencyFormatter.format(state.pendingAmount),
                        color = Color(0xFFFF9800),
                        icon = Icons.Default.Schedule
                    )
                }
            }

            // Search
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search invoices...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = state.filterStatus == null,
                            onClick = { viewModel.onFilterChanged(null) },
                            label = { Text("All") }
                        )
                    }
                    items(InvoicesViewModel.InvoiceStatus.entries.toList()) { status ->
                        FilterChip(
                            selected = state.filterStatus == status,
                            onClick = { viewModel.onFilterChanged(status) },
                            label = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = getStatusColor(status).copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }

            // Invoice List
            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.filteredInvoices.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No invoices found")
                    }
                }
            } else {
                items(state.filteredInvoices.sortedByDescending { it.createdAt }) { invoice ->
                    InvoiceCard(invoice = invoice, currencyFormatter = currencyFormatter)
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = color)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun InvoiceCard(
    invoice: InvoicesViewModel.Invoice,
    currencyFormatter: NumberFormat
) {
    val statusColor = getStatusColor(invoice.status)
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicator
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (invoice.status) {
                            InvoicesViewModel.InvoiceStatus.PAID -> Icons.Default.CheckCircle
                            InvoicesViewModel.InvoiceStatus.PENDING -> Icons.Default.Schedule
                            InvoicesViewModel.InvoiceStatus.OVERDUE -> Icons.Default.Warning
                            InvoicesViewModel.InvoiceStatus.REFUNDED -> Icons.Default.Refresh
                            InvoicesViewModel.InvoiceStatus.PARTIAL -> Icons.Default.MoreHoriz
                        },
                        contentDescription = null,
                        tint = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        invoice.invoiceId,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            invoice.status.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor
                        )
                    }
                }
                
                Text(
                    "Order: ${invoice.orderId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    "${invoice.buyerName} → ${invoice.sellerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row {
                    Text(
                        invoice.paymentMethod,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        dateFormatter.format(invoice.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                currencyFormatter.format(invoice.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (invoice.status == InvoicesViewModel.InvoiceStatus.PAID) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun getStatusColor(status: InvoicesViewModel.InvoiceStatus) = when (status) {
    InvoicesViewModel.InvoiceStatus.PAID -> Color(0xFF4CAF50)
    InvoicesViewModel.InvoiceStatus.PENDING -> Color(0xFFFF9800)
    InvoicesViewModel.InvoiceStatus.OVERDUE -> Color(0xFFD32F2F)
    InvoicesViewModel.InvoiceStatus.REFUNDED -> Color(0xFF9C27B0)
    InvoicesViewModel.InvoiceStatus.PARTIAL -> Color(0xFF2196F3)
}
