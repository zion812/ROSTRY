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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAnalyticsScreen(
    viewModel: UserAnalyticsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Analytics") },
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
                // User Growth
                item {
                    Text("User Growth", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(Modifier.weight(1f), "Total Users", state.totalUsers.toString(), Color(0xFF2196F3))
                        StatCard(Modifier.weight(1f), "This Week", "+${state.newThisWeek}", Color(0xFF4CAF50))
                        StatCard(Modifier.weight(1f), "This Month", "+${state.newThisMonth}", Color(0xFF9C27B0))
                    }
                }

                // Role Distribution
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Role Distribution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            RoleBar("Enthusiasts", state.enthusiastCount, state.totalUsers, Color(0xFF673AB7))
                            Spacer(modifier = Modifier.height(12.dp))
                            RoleBar("Farmers", state.farmerCount, state.totalUsers, Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.height(12.dp))
                            RoleBar("Admins", state.adminCount, state.totalUsers, Color(0xFFD32F2F))
                        }
                    }
                }

                // Activity Metrics
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Activity Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(Modifier.weight(1f), "Active Today", state.activeToday.toString(), Color(0xFF4CAF50))
                        StatCard(Modifier.weight(1f), "Active (7d)", state.activeWeek.toString(), Color(0xFF2196F3))
                    }
                }

                // Top Regions
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Top Regions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(state.topRegions) { region ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(region.name, fontWeight = FontWeight.Medium)
                            }
                            Text("${region.userCount} users", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, title: String, value: String, color: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
private fun RoleBar(label: String, count: Int, total: Int, color: Color) {
    val percentage = if (total > 0) (count.toFloat() / total * 100) else 0f
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("$count (${String.format("%.1f", percentage)}%)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color
        )
    }
}
