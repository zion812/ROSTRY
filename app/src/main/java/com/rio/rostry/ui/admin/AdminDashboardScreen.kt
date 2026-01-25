package com.rio.rostry.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    onNavigateToBiosecurity: () -> Unit,
    onNavigateToMortality: () -> Unit,
    onNavigateToDisputes: () -> Unit,
    pendingVerificationsCount: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val menuItems = listOf(
                AdminMenuItem(
                    title = "Verifications",
                    subtitle = "$pendingVerificationsCount pending",
                    icon = Icons.Default.VerifiedUser,
                    color = Color(0xFF4CAF50), // Green
                    onClick = onNavigateToVerification,
                    badgeCount = pendingVerificationsCount
                ),
                AdminMenuItem(
                    title = "User Management",
                    subtitle = "Manage roles & users",
                    icon = Icons.Default.People,
                    color = Color(0xFF2196F3), // Blue
                    onClick = onNavigateToUserManagement
                ),
                AdminMenuItem(
                    title = "Biosecurity",
                    subtitle = "Manage Red Zones",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFF44336), // Red
                    onClick = onNavigateToBiosecurity
                ),
                AdminMenuItem(
                    title = "Mortality Watch",
                    subtitle = "Outbreak Detection",
                    icon = Icons.Default.LocalHospital,
                    color = Color(0xFF9C27B0), // Purple
                    onClick = onNavigateToMortality
                ),
                AdminMenuItem(
                    title = "Disputes",
                    subtitle = "Marketplace Integrity",
                    icon = Icons.Default.Gavel,
                    color = Color(0xFF795548), // Brown
                    onClick = onNavigateToDisputes
                ),
                 AdminMenuItem(
                    title = "Analytics",
                    subtitle = "System reports",
                    icon = Icons.Default.Analytics,
                    color = Color(0xFFFFC107), // Amber
                    onClick = { /* TODO: Implement Analytics */ }
                ),
                 AdminMenuItem(
                    title = "System Settings",
                    subtitle = "Configuration",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF9E9E9E), // Grey
                    onClick = { /* TODO: Implement Settings */ }
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    AdminMenuCard(item)
                }
            }
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
            .height(160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = item.color
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (item.badgeCount > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(text = item.badgeCount.toString())
                }
            }
        }
    }
}
