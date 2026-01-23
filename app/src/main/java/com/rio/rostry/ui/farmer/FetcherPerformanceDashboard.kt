package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.cache.AggregatedCacheStats
import com.rio.rostry.data.cache.CacheHealth
import com.rio.rostry.data.cache.CacheHealthMonitor
import com.rio.rostry.data.health.FetcherHealth
import com.rio.rostry.data.health.FetcherHealthCheck
import com.rio.rostry.data.health.HealthStatus
import com.rio.rostry.data.health.SystemHealth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Admin/Debug dashboard for monitoring fetcher performance.
 * Shows real-time status, latency, and cache metrics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetcherPerformanceDashboard(
    viewModel: FetcherPerformanceViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fetcher Performance") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System Health Banner
            item {
                SystemHealthCard(uiState.systemHealth)
            }

            // Cache Stats
            item {
                CacheStatsCard(uiState.cacheStats, uiState.cacheHealth)
            }

            // Fetcher List
            item {
                Text(
                    "Fetcher Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.fetcherHealth.toList()) { (fetcherId, health) ->
                FetcherHealthCard(fetcherId, health)
            }
        }
    }
}

@Composable
fun SystemHealthCard(health: SystemHealth) {
    val (color, icon) = when (health) {
        SystemHealth.HEALTHY -> Color(0xFF4CAF50) to Icons.Default.CheckCircle
        SystemHealth.WARNING -> Color(0xFFFF9800) to Icons.Default.Warning
        SystemHealth.DEGRADED -> Color(0xFFFF5722) to Icons.Default.Warning
        SystemHealth.CRITICAL -> Color(0xFFF44336) to Icons.Default.Error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("System Health", style = MaterialTheme.typography.titleMedium)
                Text(health.name, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CacheStatsCard(stats: AggregatedCacheStats?, health: CacheHealth) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cache Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (stats != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatItem("Hit Rate", "${(stats.hitRate * 100).toInt()}%")
                    StatItem("Hits", stats.totalHits.toString())
                    StatItem("Misses", stats.totalMisses.toString())
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { stats.hitRate },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        stats.hitRate > 0.8f -> Color(0xFF4CAF50)
                        stats.hitRate > 0.5f -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            } else {
                Text("No cache data available", color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun FetcherHealthCard(fetcherId: String, health: FetcherHealth) {
    val statusColor = when (health.status) {
        HealthStatus.HEALTHY -> Color(0xFF4CAF50)
        HealthStatus.DEGRADED -> Color(0xFFFF9800)
        HealthStatus.UNHEALTHY -> Color(0xFFF44336)
        HealthStatus.UNKNOWN -> Color.Gray
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 8.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = statusColor)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(fetcherId, fontWeight = FontWeight.Medium)
                Text(
                    "Success: ${(health.successRate * 100).toInt()}% | Requests: ${health.successCount + health.failureCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                health.status.name,
                color = statusColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@HiltViewModel
class FetcherPerformanceViewModel @Inject constructor(
    private val healthCheck: FetcherHealthCheck,
    private val cacheHealthMonitor: CacheHealthMonitor
) : ViewModel() {

    data class UiState(
        val systemHealth: SystemHealth = SystemHealth.HEALTHY,
        val cacheHealth: CacheHealth = CacheHealth.HEALTHY,
        val cacheStats: AggregatedCacheStats? = null,
        val fetcherHealth: Map<String, FetcherHealth> = emptyMap()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
        observeHealth()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState(
                systemHealth = healthCheck.systemHealth.value,
                cacheHealth = cacheHealthMonitor.overallHealth.value,
                cacheStats = cacheHealthMonitor.getAggregatedStats(),
                fetcherHealth = healthCheck.getAllHealth()
            )
        }
    }

    private fun observeHealth() {
        viewModelScope.launch {
            healthCheck.systemHealth.collect { health ->
                _uiState.value = _uiState.value.copy(systemHealth = health)
            }
        }
        viewModelScope.launch {
            cacheHealthMonitor.overallHealth.collect { health ->
                _uiState.value = _uiState.value.copy(cacheHealth = health)
            }
        }
    }
}
