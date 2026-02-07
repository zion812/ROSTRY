package com.rio.rostry.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.admin.theme.AdminColors
import com.rio.rostry.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    onNavigateToBiosecurity: () -> Unit,
    onNavigateToMortality: () -> Unit,
    onNavigateToDisputes: () -> Unit,
    onNavigateTo: (String) -> Unit,
    pendingVerificationsCount: Int,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminColors.adminBackground()),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome Header
        item {
            WelcomeHeader(
                isLoading = uiState.isLoading,
                onRefresh = { viewModel.refreshDashboard() }
            )
        }
        
        // Quick Stats Row
        item {
            QuickStatsRow(
                userMetrics = uiState.userMetrics,
                productMetrics = uiState.productMetrics,
                orderMetrics = uiState.orderMetrics,
                systemHealth = uiState.systemHealth,
                pendingActions = uiState.pendingActions
            )
        }
        
        // Pending Actions Alert
        if (uiState.pendingActions.total > 0) {
            item {
                PendingActionsCard(
                    pendingActions = uiState.pendingActions,
                    onNavigateToVerification = onNavigateToVerification,
                    onNavigateToDisputes = onNavigateToDisputes
                )
            }
        }
        
        // Quick Actions Grid
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AdminColors.adminOnBackground()
            )
        }
        
        item {
            QuickActionsGrid(
                pendingVerificationsCount = pendingVerificationsCount,
                onNavigateToVerification = onNavigateToVerification,
                onNavigateToUserManagement = onNavigateToUserManagement,
                onNavigateToBiosecurity = onNavigateToBiosecurity,
                onNavigateToMortality = onNavigateToMortality,
                onNavigateToDisputes = onNavigateToDisputes,
                onNavigateTo = onNavigateTo
            )
        }
        
        // Error Display
        if (uiState.error != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Admin Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AdminColors.adminOnBackground()
            )
            Text(
                text = "System overview and quick actions",
                style = MaterialTheme.typography.bodyMedium,
                color = AdminColors.adminOnBackground().copy(alpha = 0.7f)
            )
        }
        
        IconButton(
            onClick = onRefresh,
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = AdminColors.adminOnBackground()
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    userMetrics: UserMetrics,
    productMetrics: ProductMetrics,
    orderMetrics: OrderMetrics,
    systemHealth: SystemHealth,
    pendingActions: PendingActions
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            StatCard(
                title = "Total Users",
                value = userMetrics.totalUsers.toString(),
                subtitle = "${userMetrics.farmers} farmers, ${userMetrics.enthusiasts} enthusiasts",
                icon = Icons.Default.People,
                gradientColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
            )
        }
        item {
            StatCard(
                title = "Revenue",
                value = "$${orderMetrics.totalRevenue.toInt()}",
                subtitle = "${orderMetrics.completedThisMonth} orders this month",
                icon = Icons.Default.AttachMoney,
                gradientColors = listOf(Color(0xFF2E7D32), Color(0xFF4CAF50))
            )
        }
        item {
            StatCard(
                title = "Active Listings",
                value = productMetrics.activeListings.toString(),
                subtitle = "${productMetrics.flaggedProducts} flagged",
                icon = Icons.Default.Inventory,
                gradientColors = listOf(Color(0xFF11998E), Color(0xFF38EF7D))
            )
        }
        item {
            StatCard(
                title = "Pending Actions",
                value = pendingActions.total.toString(),
                subtitle = "${pendingActions.verifications} verifications",
                icon = Icons.Default.PendingActions,
                gradientColors = listOf(Color(0xFFEB3349), Color(0xFFF45C43))
            )
        }
        item {
            val healthColor = if (systemHealth.databaseStatus == HealthStatus.HEALTHY) Color(0xFF4CAF50) else Color(0xFFF44336)
            StatCard(
                title = "System Health",
                value = if (systemHealth.databaseStatus == HealthStatus.HEALTHY) "Healthy" else "Issues",
                subtitle = "DB: ${systemHealth.databaseStatus}",
                icon = Icons.Default.Dns,
                gradientColors = listOf(healthColor.copy(alpha = 0.8f), healthColor)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradientColors),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingActionsCard(
    pendingActions: PendingActions,
    onNavigateToVerification: () -> Unit,
    onNavigateToDisputes: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9800)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pending Actions Required",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = "${pendingActions.total} items need your attention",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100).copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pendingActions.verifications > 0) {
                    AssistChip(
                        onClick = onNavigateToVerification,
                        label = { Text("${pendingActions.verifications} Verifications") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
                if (pendingActions.upgradeRequests > 0) {
                    AssistChip(
                        onClick = { /* Navigate to Requests */ },
                        label = { Text("${pendingActions.upgradeRequests} Role Requests") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Upgrade,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
                if (pendingActions.reports > 0) {
                    AssistChip(
                        onClick = { /* Navigate to Moderation */ },
                        label = { Text("${pendingActions.reports} Reports") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Flag,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
                if (pendingActions.disputes > 0) {
                    AssistChip(
                        onClick = onNavigateToDisputes,
                        label = { Text("${pendingActions.disputes} Disputes") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Gavel,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    pendingVerificationsCount: Int,
    onNavigateToVerification: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    onNavigateToBiosecurity: () -> Unit,
    onNavigateToMortality: () -> Unit,
    onNavigateToDisputes: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val menuItems = listOf(
        AdminMenuItem(
            title = "Verifications",
            subtitle = "$pendingVerificationsCount pending",
            icon = Icons.Default.VerifiedUser,
            color = Color(0xFF4CAF50),
            onClick = onNavigateToVerification,
            badgeCount = pendingVerificationsCount
        ),
        AdminMenuItem(
            title = "User Management",
            subtitle = "Manage roles & users",
            icon = Icons.Default.People,
            color = Color(0xFF2196F3),
            onClick = onNavigateToUserManagement
        ),
        AdminMenuItem(
            title = "Role Requests",
            subtitle = "Approve upgrades",
            icon = Icons.Default.Upgrade,
            color = Color(0xFFE91E63),
            onClick = { onNavigateTo(Routes.Admin.UPGRADE_REQUESTS) }
        ),
        AdminMenuItem(
            title = "Biosecurity",
            subtitle = "Manage Red Zones",
            icon = Icons.Default.Warning,
            color = Color(0xFFF44336),
            onClick = onNavigateToBiosecurity
        ),
        AdminMenuItem(
            title = "Mortality Watch",
            subtitle = "Outbreak Detection",
            icon = Icons.Default.LocalHospital,
            color = Color(0xFF9C27B0),
            onClick = onNavigateToMortality
        ),
        AdminMenuItem(
            title = "Disputes",
            subtitle = "Marketplace Integrity",
            icon = Icons.Default.Gavel,
            color = Color(0xFF795548),
            onClick = onNavigateToDisputes
        ),
        AdminMenuItem(
            title = "Products",
            subtitle = "Review & Moderate",
            icon = Icons.Default.Inventory,
            color = Color(0xFFFF5722),
            onClick = { onNavigateTo(Routes.Admin.PRODUCT_MODERATION) }
        ),
        AdminMenuItem(
            title = "Orders",
            subtitle = "Support & Override",
            icon = Icons.Default.ShoppingCart,
            color = Color(0xFF009688),
            onClick = { onNavigateTo(Routes.Admin.ORDER_INTERVENTION) }
        ),
        AdminMenuItem(
            title = "Audit Logs",
            subtitle = "System Activity",
            icon = Icons.Default.History,
            color = Color(0xFF607D8B),
            onClick = { onNavigateTo(Routes.Admin.AUDIT_LOGS) }
        ),
        AdminMenuItem(
            title = "Invoices",
            subtitle = "View & Manage",
            icon = Icons.Default.Receipt,
            color = Color(0xFF673AB7),
            onClick = { onNavigateTo(Routes.Admin.INVOICES) }
        ),
        // New menu items for complete accessibility
        AdminMenuItem(
            title = "Analytics",
            subtitle = "Overview & Insights",
            icon = Icons.Default.Analytics,
            color = Color(0xFF00BCD4),
            onClick = { onNavigateTo(Routes.Admin.ANALYTICS) }
        ),
        AdminMenuItem(
            title = "Reports",
            subtitle = "Generate Reports",
            icon = Icons.Default.Assessment,
            color = Color(0xFF3F51B5),
            onClick = { onNavigateTo(Routes.Admin.REPORTS) }
        ),
        AdminMenuItem(
            title = "System Config",
            subtitle = "Settings & Health",
            icon = Icons.Default.Settings,
            color = Color(0xFF455A64),
            onClick = { onNavigateTo(Routes.Admin.SYSTEM_CONFIG) }
        ),
        AdminMenuItem(
            title = "Feature Toggles",
            subtitle = "Enable/Disable",
            icon = Icons.Default.ToggleOn,
            color = Color(0xFFFF9800),
            onClick = { onNavigateTo(Routes.Admin.FEATURE_TOGGLES) }
        ),
        AdminMenuItem(
            title = "Moderation",
            subtitle = "Content Queue",
            icon = Icons.Default.Shield,
            color = Color(0xFF8BC34A),
            onClick = { onNavigateTo(Routes.Admin.MODERATION) }
        ),
        AdminMenuItem(
            title = "Broadcast",
            subtitle = "Notifications",
            icon = Icons.Default.Campaign,
            color = Color(0xFFE91E63),
            onClick = { onNavigateTo(Routes.Admin.BROADCAST) }
        ),
        AdminMenuItem(
            title = "Bulk Operations",
            subtitle = "Import/Export",
            icon = Icons.Default.DynamicFeed,
            color = Color(0xFF795548),
            onClick = { onNavigateTo(Routes.Admin.BULK_OPERATIONS) }
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(600.dp), // Fixed height for nested scroll
        userScrollEnabled = false
    ) {
        items(menuItems) { item ->
            AdminMenuCard(item)
        }
    }
}

data class AdminMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit,
    val badgeCount: Int = 0
)

@Composable
fun AdminMenuCard(item: AdminMenuItem) {
    Card(
        onClick = item.onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AdminColors.adminSurface()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = item.color
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminColors.adminOnSurface()
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AdminColors.adminOnSurface().copy(alpha = 0.6f)
                )
            }

            if (item.badgeCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    containerColor = Color(0xFFE91E63)
                ) {
                    Text(text = item.badgeCount.toString())
                }
            }
        }
    }
}
