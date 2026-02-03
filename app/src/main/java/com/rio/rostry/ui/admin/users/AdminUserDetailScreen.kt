package com.rio.rostry.ui.admin.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.AdminUserProfile
import com.rio.rostry.domain.model.AdminOrderSummary
import com.rio.rostry.domain.model.UserType
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetailScreen(
    userId: String,
    viewModel: AdminUserDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToOrder: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                state.profile?.let { profile ->
                    UserProfileContent(
                        profile = profile,
                        onOrderClick = onNavigateToOrder
                    )
                }
            }
        }
    }
}

@Composable
fun UserProfileContent(
    profile: AdminUserProfile,
    onOrderClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            profile.user.fullName ?: "No Name",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            profile.user.email ?: "No Email",
                            style = MaterialTheme.typography.bodyMedium
                        )
                         Text(
                            "Role: ${profile.user.userType}",
                            style = MaterialTheme.typography.labelLarge,
                             color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                 Spacer(modifier = Modifier.height(16.dp))
                 Row(
                     horizontalArrangement = Arrangement.spacedBy(8.dp)
                 ) {
                     val statusColor = when (profile.user.verificationStatus.name) {
                         "VERIFIED" -> Color(0xFF4CAF50)
                         "PENDING" -> Color(0xFFFF9800)
                         else -> Color.Gray
                     }
                      AssistChip(
                        onClick = {},
                        label = { Text(profile.user.verificationStatus.name) },
                        colors = AssistChipDefaults.assistChipColors(leadingIconContentColor = statusColor)
                    )
                    if (profile.user.isSuspended) {
                         AssistChip(
                            onClick = {},
                            label = { Text("SUSPENDED") },
                            colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.error)
                        )
                    }
                 }
            }
        }

        // 2. Key Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = "Total Orders",
                value = profile.totalOrdersCount.toString(),
                icon = Icons.Default.ShoppingCart
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                title = "Total Spend",
                value = "$${profile.totalSpend}",
                icon = Icons.Default.MonetizationOn // Use currency symbol logic if preferred
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = "Active Listings",
                value = profile.activeListingsCount.toString(),
                icon = Icons.Default.List
            )
             StatsCard(
                modifier = Modifier.weight(1f),
                title = "Risk Score",
                value = "${profile.riskScore}/100",
                icon = Icons.Default.Warning,
                color = if(profile.riskScore > 50) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        // 3. Recent Orders
        Text(
            "Recent Orders",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (profile.recentOrders.isEmpty()) {
            Text("No recent orders found.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        } else {
            profile.recentOrders.forEach { order ->
                OrderItem(order = order, onClick = { onOrderClick(order.orderId) })
            }
        }
        
    }
}

@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun OrderItem(
    order: AdminOrderSummary,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold)
                Text(dateFormat.format(order.date), style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${order.amount}", fontWeight = FontWeight.Bold)
                Text(
                    order.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (order.status == "DELIVERED") Color(0xFF4CAF50) else Color.Gray
                )
            }
        }
    }
}

// Helper to handle currency icon name variation if needed

